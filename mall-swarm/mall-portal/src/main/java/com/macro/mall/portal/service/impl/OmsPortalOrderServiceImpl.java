package com.macro.mall.portal.service.impl;

import com.macro.mall.portal.service.QRCodeService;
import com.macro.mall.portal.util.PickupCodeUtil;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.exception.ApiException;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.common.service.RedisService;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.model.PmsCommentExample;
import com.macro.mall.portal.component.CancelOrderSender;
import com.macro.mall.portal.component.PayCheckSender;
import com.macro.mall.portal.dao.PortalOrderDao;
import com.macro.mall.portal.dao.PortalOrderItemDao;
import com.macro.mall.portal.dao.PortalProductDao;
import com.macro.mall.portal.dao.SmsCouponHistoryDao;
import com.macro.mall.portal.domain.*;
import com.macro.mall.portal.dto.DirectBuyParam;
import com.macro.mall.portal.dto.GiftOrderParam;
import com.macro.mall.portal.dto.SmsFreightCalculateParam;
import com.macro.mall.portal.dto.SmsFreightCalculateResult;
import com.macro.mall.portal.service.*;
import com.macro.mall.portal.util.CouponApplicabilityUtil;
import com.macro.mall.portal.service.CommissionService;
import com.macro.mall.common.util.WxMessageUtil;
import com.macro.mall.portal.service.InviteService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import java.util.ArrayList;
import org.springframework.context.annotation.Lazy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 前台订单管理Service
 * Created by macro on 2018/8/30.
 */
@Service
public class OmsPortalOrderServiceImpl implements OmsPortalOrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OmsPortalOrderServiceImpl.class);
    
    @Autowired
    private UmsMemberService memberService;
    @Autowired
    private OmsCartItemService cartItemService;
    @Autowired
    private UmsMemberReceiveAddressService memberReceiveAddressService;
    @Autowired
    private UmsMemberCouponService memberCouponService;
    @Autowired
    private UmsIntegrationConsumeSettingMapper integrationConsumeSettingMapper;
    @Autowired
    private PmsSkuStockMapper skuStockMapper;
    @Autowired
    private SmsCouponHistoryDao couponHistoryDao;
    @Autowired
    private OmsOrderMapper orderMapper;
    @Autowired
    private PortalOrderItemDao orderItemDao;
    @Autowired
    private SmsCouponHistoryMapper couponHistoryMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private CouponApplicabilityUtil couponApplicabilityUtil;
    @Value("${redis.key.orderId}")
    private String REDIS_KEY_ORDER_ID;
    @Value("${redis.database}")
    private String REDIS_DATABASE;
    @Value("${spring.profiles.active:dev}")
    private String activeProfile;
    @Autowired
    private PortalOrderDao portalOrderDao;
    @Autowired
    private OmsOrderSettingMapper orderSettingMapper;
    @Autowired
    private OmsOrderItemMapper orderItemMapper;
    @Autowired
    private CancelOrderSender cancelOrderSender;
    @Autowired
    private PayCheckSender payCheckSender;
    @Autowired
    private PortalProductDao portalProductDao;
    @Autowired
    private UmsIntegrationChangeHistoryMapper integrationChangeHistoryMapper;
    @Autowired
    private WxPayBusiness wxPayBusiness;
    @Autowired
    private OmsGiftRecordMapper omsGiftRecordMapper;
    @Lazy
    @Autowired
    private OmsGiftService omsGiftService;
    @Autowired
    private PickupCodeUtil pickupCodeUtil;
    @Autowired
    private QRCodeService qrCodeService;
    @Autowired
    private OmsOrderReturnApplyMapper orderReturnApplyMapper;
    @Autowired
    private PmsCommentMapper commentMapper;
    @Autowired
    private OmsStoreAddressMapper storeAddressMapper;
    @Autowired
    private CommissionService commissionService;
    @Autowired
    private PmsInviteRewardMapper inviteRewardMapper;

    @Autowired
    private InviteService inviteService;

    @Autowired
    private PmsProductPaybackService paybackService;
    @Autowired
    private FreightCalculateService freightCalculateService;
    @Autowired
    private PmsStoreSkuStockMapper storeSkuStockMapper;
    @Autowired
    private UmsMemberBalanceService memberBalanceService;
    @Autowired
    private SmsIntegrationPromotionMapper integrationPromotionMapper;
    @Autowired
    private SmsIntegrationPromotionMemberMapper integrationPromotionMemberMapper;
    @Autowired
    private PmsProductMapper productMapper;
    @Autowired
    private OmsPromotionService promotionService;
    @Autowired
    private UmsIntegrationService integrationService;
    @Autowired
    private PmsStockOperationLogMapper stockOperationLogMapper;

    @Autowired(required = false)
    private WxMessageUtil wxMessageUtil;

    @Autowired(required = false)
    private AdminNotificationFeignService adminNotificationFeignService;

    @Value("${template.order-success:}")
    private String orderSuccessTemplateId;
    /**
     * 根据购物车信息生成确认单信息
     */
    @Override
    public ConfirmOrderResult generateConfirmOrder(List<Long> cartIds) {
        return generateConfirmOrder(cartIds, null);
    }
    
    /**
     * 根据购物车信息生成确认单信息（支持门店库存校验）
     */
    @Override
    public ConfirmOrderResult generateConfirmOrder(List<Long> cartIds, Long storeId) {
        ConfirmOrderResult result = new ConfirmOrderResult();
        //获取购物车信息
        UmsMember currentMember = memberService.getCurrentMember();
        List<CartPromotionItem> cartPromotionItemList = cartItemService.listPromotion(currentMember.getId(),cartIds);
        
        // 如果指定了门店ID，进行门店库存校验
        if (storeId != null) {
            validateStoreStock(cartPromotionItemList, storeId);
        }
        
        result.setCartPromotionItemList(cartPromotionItemList);
        //获取用户收货地址列表
        List<UmsMemberReceiveAddress> memberReceiveAddressList = memberReceiveAddressService.list();
        result.setMemberReceiveAddressList(memberReceiveAddressList);
        //获取用户可用优惠券列表（支持学校过滤）
        List<SmsCouponHistoryDetail> couponHistoryDetailList = memberCouponService.listCart(cartPromotionItemList, 1, true);
        result.setCouponHistoryDetailList(couponHistoryDetailList);
        //获取用户积分
        result.setMemberIntegration(currentMember.getIntegration());
        //获取用户余额
        result.setMemberBalance(currentMember.getBalance() != null ? currentMember.getBalance() : BigDecimal.ZERO);
        //获取积分使用规则
        UmsIntegrationConsumeSetting integrationConsumeSetting = integrationConsumeSettingMapper.selectByPrimaryKey(1L);
        result.setIntegrationConsumeSetting(integrationConsumeSetting);
        //计算总金额、活动优惠、运费、应付金额
        UmsMemberReceiveAddress defaultAddress = getDefaultReceiveAddress(memberReceiveAddressList);
        ConfirmOrderResult.CalcAmount calcAmount = calcCartAmount(cartPromotionItemList, defaultAddress);
        result.setCalcAmount(calcAmount);
        return result;
    }

    @Override
    public ConfirmOrderResult generateDirectConfirmOrder(DirectBuyParam directBuyParam) {
        return generateDirectConfirmOrder(directBuyParam, null);
    }

    @Override
    public ConfirmOrderResult generateDirectConfirmOrder(DirectBuyParam directBuyParam, Long storeId) {
        ConfirmOrderResult result = new ConfirmOrderResult();
        UmsMember currentMember = memberService.getCurrentMember();

        PromotionProduct  promotionProduct = portalProductDao.getPromotionProduct(directBuyParam.getProductId());
        // 添加日志输出商品促销信息
        LOGGER.info("商品ID:{}，促销类型:{}，满减规则数量:{}", 
                promotionProduct.getId(), 
                promotionProduct.getPromotionType(),
                promotionProduct.getProductFullReductionList() == null ? 0 : promotionProduct.getProductFullReductionList().size());
        
        if (promotionProduct.getProductFullReductionList() != null && !promotionProduct.getProductFullReductionList().isEmpty()) {
            for (PmsProductFullReduction reduction : promotionProduct.getProductFullReductionList()) {
                LOGGER.info("满减规则：满{}元减{}元", reduction.getFullPrice(), reduction.getReducePrice());
            }
        }
        
        //组装直接购买商品信息
        List<CartPromotionItem> cartPromotionItemList = getCartPromotionItems(directBuyParam, promotionProduct);
        
        // 如果指定了门店ID，进行门店库存校验
        if (storeId != null) {
            validateStoreStock(cartPromotionItemList, storeId);
        }
        
        result.setCartPromotionItemList(cartPromotionItemList);

        // 获取用户收货地址
        List<UmsMemberReceiveAddress> memberReceiveAddressList = memberReceiveAddressService.list();
        result.setMemberReceiveAddressList(memberReceiveAddressList);

        // 获取可用优惠券（支持学校过滤）
        List<SmsCouponHistoryDetail> couponHistoryDetailList = memberCouponService.listCart(cartPromotionItemList, 1, true);
        result.setCouponHistoryDetailList(couponHistoryDetailList);

        // 获取用户积分
        result.setMemberIntegration(currentMember.getIntegration());

        // 获取用户余额
        result.setMemberBalance(currentMember.getBalance() != null ? currentMember.getBalance() : BigDecimal.ZERO);

        //积分使用规则
        UmsIntegrationConsumeSetting integrationConsumeSetting = integrationConsumeSettingMapper.selectByPrimaryKey(1L);
        result.setIntegrationConsumeSetting(integrationConsumeSetting);

        // 计算金额（包含运费）
        UmsMemberReceiveAddress defaultAddress = getDefaultReceiveAddress(memberReceiveAddressList);
        ConfirmOrderResult.CalcAmount calcAmount = calcCartAmount(cartPromotionItemList, defaultAddress);
        result.setCalcAmount(calcAmount);

        return result;
    }
    
    /**
     * 校验门店库存是否充足
     */
    private void validateStoreStock(List<CartPromotionItem> cartPromotionItemList, Long storeId) {
        for (CartPromotionItem item : cartPromotionItemList) {
            // 查询门店SKU库存
            PmsStoreSkuStockExample storeSkuExample = new PmsStoreSkuStockExample();
            storeSkuExample.createCriteria()
                    .andStoreIdEqualTo(storeId)
                    .andSkuIdEqualTo(item.getProductSkuId())
                    .andStatusEqualTo(1); // 只查询启用状态的库存
            List<PmsStoreSkuStock> storeSkuStockList = storeSkuStockMapper.selectByExample(storeSkuExample);
            
            if (storeSkuStockList.isEmpty()) {
                throw new ApiException("门店暂无该商品库存：" + item.getProductName());
            }
            
            PmsStoreSkuStock storeSkuStock = storeSkuStockList.get(0);
            int availableStock = storeSkuStock.getStock() - storeSkuStock.getLockedStock();
            
            if (availableStock < item.getQuantity()) {
                throw new ApiException("门店库存不足，商品：" + item.getProductName() + 
                        "，需要：" + item.getQuantity() + "，可用：" + availableStock);
            }
        }
    }

    /**
     * 使用统一的营销计算服务处理直接购买的商品
     */
    @NotNull
    private List<CartPromotionItem> getCartPromotionItems(DirectBuyParam directBuyParam, PromotionProduct promotionProduct) {
        // 创建临时的 OmsCartItem 用于营销计算
        OmsCartItem cartItem = createCartItemFromDirectBuy(directBuyParam);

        // 调用统一的营销计算服务
        List<OmsCartItem> cartItemList = Collections.singletonList(cartItem);
        List<CartPromotionItem> cartPromotionItemList = promotionService.calcCartPromotion(cartItemList);

        // 设置直接购买特有的字段
        if (!cartPromotionItemList.isEmpty()) {
            CartPromotionItem cartPromotionItem = cartPromotionItemList.get(0);
            cartPromotionItem.setDeliveryType(directBuyParam.getDeliveryType());

            LOGGER.info("直接购买营销计算完成 - 商品ID:{}, 促销类型:{}, 促销信息:{}, 优惠金额:{}",
                    directBuyParam.getProductId(),
                    promotionProduct.getPromotionType(),
                    cartPromotionItem.getPromotionMessage(),
                    cartPromotionItem.getReduceAmount());
        }

        return cartPromotionItemList;
    }

    /**
     * 将 DirectBuyParam 转换为 OmsCartItem
     */
    private OmsCartItem createCartItemFromDirectBuy(DirectBuyParam directBuyParam) {
        OmsCartItem cartItem = new OmsCartItem();
        cartItem.setProductId(directBuyParam.getProductId());
        cartItem.setProductSkuId(directBuyParam.getProductSkuId());
        cartItem.setQuantity(directBuyParam.getQuantity());
        cartItem.setPrice(directBuyParam.getPrice());
        cartItem.setProductName(directBuyParam.getProductName());
        cartItem.setProductPic(directBuyParam.getProductPic());
        cartItem.setProductSubTitle(directBuyParam.getProductSubTitle());
        cartItem.setProductAttr(directBuyParam.getProductAttr());
        cartItem.setProductBrand(directBuyParam.getProductBrand());
        cartItem.setProductCategoryId(directBuyParam.getProductCategoryId());
        cartItem.setProductSkuCode(directBuyParam.getProductSkuCode());
        cartItem.setProductSn(directBuyParam.getProductSn());

        // 设置必要的默认值
        cartItem.setDeleteStatus(0);
        cartItem.setCreateDate(new Date());

        return cartItem;
    }
    
    /**
     * 获取满足条件的打折优惠
     */
    private static PmsProductLadder getProductLadder(int count, List<PmsProductLadder> productLadderList) {
        if (productLadderList == null || productLadderList.isEmpty()) {
            return null;
        }
        // 按数量从大到小排序
        productLadderList.sort((o1, o2) -> o2.getCount() - o1.getCount());
        for (PmsProductLadder ladder : productLadderList) {
            // 如果购买数量大于等于阶梯数量，则满足条件
            if (count >= ladder.getCount()) {
                return ladder;
            }
        }
        return null;
    }
    
    /**
     * 获取满足条件的满减优惠
     */
    private static PmsProductFullReduction getProductFullReduction(BigDecimal totalAmount, List<PmsProductFullReduction> fullReductionList) {
        if (fullReductionList == null || fullReductionList.isEmpty()) {
            return null;
        }
        // 按满减金额从大到小排序
        fullReductionList.sort((o1, o2) -> o2.getFullPrice().subtract(o1.getFullPrice()).intValue());
        for (PmsProductFullReduction fullReduction : fullReductionList) {
            // 如果总金额大于等于满减条件金额，则满足条件
            if (totalAmount.compareTo(fullReduction.getFullPrice()) >= 0) {
                return fullReduction;
            }
        }
        return null;
    }
    
    /**
     * 获取打折优惠的促销信息
     */
    private static String getLadderPromotionMessage(PmsProductLadder ladder) {
        StringBuilder sb = new StringBuilder();
        sb.append("打折优惠：");
        sb.append("满");
        sb.append(ladder.getCount());
        sb.append("件，");
        sb.append("打");
        sb.append(ladder.getDiscount().multiply(new BigDecimal(10)));
        sb.append("折");
        return sb.toString();
    }
    
    /**
     * 获取满减促销消息
     */
    private static String getFullReductionPromotionMessage(PmsProductFullReduction fullReduction) {
        StringBuilder sb = new StringBuilder();
        sb.append("满减优惠：");
        sb.append("满");
        sb.append(fullReduction.getFullPrice());
        sb.append("元，");
        sb.append("减");
        sb.append(fullReduction.getReducePrice());
        sb.append("元");
        return sb.toString();
    }

    /**
     * 获取商品的原价
     */
    private static PmsSkuStock getOriginalPrice(PromotionProduct promotionProduct, Long productSkuId) {
        for (PmsSkuStock skuStock : promotionProduct.getSkuStockList()) {
            if (productSkuId.equals(skuStock.getId())) {
                return skuStock;
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> generateOrder(OrderParam orderParam) {
        List<OmsOrderItem> orderItemList = new ArrayList<>();
        //判断配送方式
        Integer deliveryType = orderParam.getDeliveryType();
        // 如果OrderParam中没有配送方式，尝试从直接购买的CartPromotionItem中获取
        if (deliveryType == null && orderParam.getCartPromotionItemList() != null && !orderParam.getCartPromotionItemList().isEmpty()) {
            CartPromotionItem firstItem = orderParam.getCartPromotionItemList().get(0);
            if (firstItem.getDeliveryType() != null) {
                deliveryType = firstItem.getDeliveryType();
            }
        }
        // 默认为快递配送
        if (deliveryType == null) {
            deliveryType = 0;
        }
        //校验收货地址（门店自取时无需校验，送礼订单下单时也暂不校验）
        if(deliveryType == 0 && (orderParam.getIsGift() == null || orderParam.getIsGift() != 1) && orderParam.getMemberReceiveAddressId()==null){
            Asserts.fail("请选择收货地址！");
        }
        //获取购物车及优惠信息
        UmsMember currentMember = memberService.getCurrentMember();
        List<CartPromotionItem> cartPromotionItemList;
        if (orderParam.getCartIds() != null && !orderParam.getCartIds().isEmpty()) {
            // 购物车流程：从购物车重新查询数据
            cartPromotionItemList = cartItemService.listPromotion(currentMember.getId(), orderParam.getCartIds());
        } else if (orderParam.getCartPromotionItemList() != null && !orderParam.getCartPromotionItemList().isEmpty()) {
            // 直接购买流程：使用预下单锁定后的数据
            cartPromotionItemList = orderParam.getCartPromotionItemList();
            // TODO: 2025/03/13 这里没有做关键校验：确保数据未被篡改（如价格、库存）
//            validateCartPromotionItems(cartPromotionItemList);
        } else {
            throw new ApiException("订单参数不合法");
        }

        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            //生成下单商品信息
            OmsOrderItem orderItem = getOmsOrderItem(cartPromotionItem);
            orderItemList.add(orderItem);
        }
        //判断购物车中商品是否都有库存
        if (!hasStock(cartPromotionItemList, orderParam.getStoreId())) {
            Asserts.fail("库存不足，无法下单");
        }
        //判断使用使用了优惠券
        if (orderParam.getCouponId() == null) {
            //不用优惠券
            for (OmsOrderItem orderItem : orderItemList) {
                orderItem.setCouponAmount(new BigDecimal(0));
            }
        } else {
            //使用优惠券
            SmsCouponHistoryDetail couponHistoryDetail = getUseCoupon(cartPromotionItemList, orderParam.getCouponId());
            if (couponHistoryDetail == null) {
                Asserts.fail("该优惠券不可用");
            }
            //对下单商品的优惠券进行处理
            handleCouponAmount(orderItemList, couponHistoryDetail);
        }
        //判断是否使用积分 - 临时注释掉积分逻辑，避免下单异常
        // TODO: 修复积分使用逻辑后重新启用
        /*
        if (orderParam.getUseIntegration() == null||orderParam.getUseIntegration().equals(0)) {
            //不使用积分
            for (OmsOrderItem orderItem : orderItemList) {
                orderItem.setIntegrationAmount(new BigDecimal(0));
            }
        } else {
            //使用积分
            BigDecimal totalAmount = calcTotalAmount(orderItemList);
            BigDecimal integrationAmount = getUseIntegrationAmount(orderParam.getUseIntegration(), totalAmount, currentMember, orderParam.getCouponId() != null);
            if (integrationAmount.compareTo(new BigDecimal(0)) == 0) {
                Asserts.fail("积分不可用");
            } else {
                //可用情况下分摊到可用商品中
                for (OmsOrderItem orderItem : orderItemList) {
                    BigDecimal perAmount = orderItem.getProductPrice().divide(totalAmount, 3, RoundingMode.HALF_EVEN).multiply(integrationAmount);
                    orderItem.setIntegrationAmount(perAmount);
                }
            }
        }
        */
        
        // 临时处理：所有订单项的积分抵扣金额设为0
        for (OmsOrderItem orderItem : orderItemList) {
            orderItem.setIntegrationAmount(new BigDecimal(0));
        }
        //计算order_item的实付金额
        handleRealAmount(orderItemList);
        //进行库存锁定
        lockStock(cartPromotionItemList, orderParam.getStoreId());
        //根据商品合计、运费、活动优惠、优惠券、积分计算应付金额
        OmsOrder order = new OmsOrder();
        order.setDiscountAmount(new BigDecimal(0));
        order.setTotalAmount(calcTotalAmount(orderItemList));
        
        // 计算运费（自取订单运费为0）
        BigDecimal freightAmount;
        if (deliveryType == 1) {
            // 门店自取，运费为0
            freightAmount = new BigDecimal(0);
        } else {
            // 快递配送，计算实际运费
            UmsMemberReceiveAddress receiveAddress = null;
            if (orderParam.getMemberReceiveAddressId() != null) {
                receiveAddress = memberReceiveAddressService.getItem(orderParam.getMemberReceiveAddressId());
            }
            freightAmount = calculateFreightAmount(cartPromotionItemList, receiveAddress);
        }
        order.setFreightAmount(freightAmount);
        
        order.setPromotionAmount(calcPromotionAmount(orderItemList));
        order.setPromotionInfo(getOrderPromotionInfo(orderItemList));
        if (orderParam.getCouponId() == null) {
            order.setCouponAmount(new BigDecimal(0));
        } else {
            order.setCouponId(orderParam.getCouponId());
            order.setCouponAmount(calcCouponAmount(orderItemList));
        }
        if (orderParam.getUseIntegration() == null) {
            order.setIntegration(0);
            order.setIntegrationAmount(new BigDecimal(0));
        } else {
            order.setIntegration(orderParam.getUseIntegration());
            order.setIntegrationAmount(calcIntegrationAmount(orderItemList));
        }

        // 特殊处理积分兑换订单的金额记录
        if (orderParam.getOrderType() != null && orderParam.getOrderType() == 2) {
            // 积分兑换订单：确保记录的是用户实际支付的现金金额
            // totalAmount 和 payAmount 都应该是用户实际支付的现金金额
            // 这样便于后续售后退款处理
            BigDecimal actualCashAmount = calcTotalAmount(orderItemList); // 这已经是现金价格了
            order.setTotalAmount(actualCashAmount);
            order.setPayAmount(actualCashAmount.add(freightAmount)); // 现金金额 + 运费

            LOGGER.info("积分兑换订单金额设置 - 订单类型: {}, 现金金额: {}, 运费: {}, 最终支付金额: {}",
                       orderParam.getOrderType(), actualCashAmount, freightAmount, order.getPayAmount());
        } else {
            // 普通订单：使用原有逻辑
            order.setPayAmount(calcPayAmount(order));
        }
        //转化为订单信息并插入数据库
        order.setMemberId(currentMember.getId());
        order.setCreateTime(new Date());

        // 设置积分使用信息（必须在插入数据库之前设置）
        if (orderParam.getUseIntegration() != null) {
            order.setUseIntegration(orderParam.getUseIntegration());
            LOGGER.info("订单生成 - 提前设置useIntegration: {}，设置后order.getUseIntegration(): {}",
                       orderParam.getUseIntegration(), order.getUseIntegration());
        }
        order.setMemberUsername(currentMember.getUsername());
        //支付方式：0->未支付；1->支付宝；2->微信
        order.setPayType(orderParam.getPayType());
        //订单来源：0->PC订单；1->小程序订单；2->自助设备订单
        order.setSourceType(1);
        //订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
        order.setStatus(0);
        //订单类型：0->正常订单；1->秒杀订单；2->积分兑换订单
        order.setOrderType(orderParam.getOrderType() != null ? orderParam.getOrderType() : 0);
        //配送方式：0->快递配送；1->门店自取
        order.setDeliveryType(deliveryType != null ? deliveryType : Integer.valueOf(0));
        //设置学校ID：优先使用参数传递的学校ID，否则从会员信息获取
        if (orderParam.getSchoolId() != null) {
            order.setSourceSchoolId(orderParam.getSchoolId());
        } else if (currentMember.getSchoolId() != null) {
            order.setSourceSchoolId(currentMember.getSchoolId());
        }
        // 如果是门店自提订单, 记录门店信息
        if(order.getDeliveryType()!=null && order.getDeliveryType()==1 && orderParam.getStoreId()!=null){
            OmsStoreAddress store = storeAddressMapper.selectByPrimaryKey(orderParam.getStoreId());
            if(store!=null){
                order.setStoreId(store.getId());
                order.setStoreName(store.getAddressName());
                order.setStoreAddress(store.getDetailAddress());
                order.setStorePhone(store.getPhone());
            }
        }
        //是否为送礼订单：0->正常订单；1->送礼订单
        order.setIsGift(orderParam.getIsGift() != null && orderParam.getIsGift() == 1);
        //设置订单备注
        order.setNote(orderParam.getNote());
        //收货人信息：姓名、电话、邮编、地址
        if(deliveryType == 0) { // 快递配送
            if (orderParam.getIsGift() != null && orderParam.getIsGift() == 1) {
                // 送礼订单暂时使用当前用户信息作为收货信息，后续由收礼人更新
                order.setReceiverName(currentMember.getNickname());
                order.setReceiverPhone(currentMember.getPhone());
                order.setReceiverPostCode("");
                order.setReceiverProvince("");
                order.setReceiverCity("");
                order.setReceiverRegion("");
                order.setReceiverDetailAddress("送礼订单，待收礼人确认地址");
            } else {
                // 普通订单使用选择的收货地址
                UmsMemberReceiveAddress address = memberReceiveAddressService.getItem(orderParam.getMemberReceiveAddressId());
                order.setReceiverName(address.getName());
                order.setReceiverPhone(address.getPhoneNumber());
                order.setReceiverPostCode(address.getPostCode());
                order.setReceiverProvince(address.getProvince());
                order.setReceiverCity(address.getCity());
                order.setReceiverRegion(address.getRegion());
                order.setReceiverDetailAddress(address.getDetailAddress());
            }
        } else { // 门店自取
            // 设置当前用户信息为收货人
            order.setReceiverName(currentMember.getNickname());
            order.setReceiverPhone(currentMember.getPhone());
            // 其他地址信息可以设置为门店信息或者为空
            order.setReceiverPostCode("");
            order.setReceiverProvince("");
            order.setReceiverCity("");
            order.setReceiverRegion("");
            order.setReceiverDetailAddress("门店自取");
        }
        //0->未确认；1->已确认
        order.setConfirmStatus(0);
        order.setDeleteStatus(0);
        //计算赠送积分
        order.setIntegration(calcGifIntegration(orderItemList));
        //计算赠送成长值
        order.setGrowth(calcGiftGrowth(orderItemList));
        //生成订单号
        order.setOrderSn(generateOrderSn(order));
        //设置自动收货天数
        List<OmsOrderSetting> orderSettings = orderSettingMapper.selectByExample(new OmsOrderSettingExample());
        if(CollUtil.isNotEmpty(orderSettings)){
            order.setAutoConfirmDay(orderSettings.get(0).getConfirmOvertime());
        }
        // TODO: 2018/9/3 bill_*,delivery_*
        //插入order表和order_item表
        LOGGER.info("订单插入数据库前 - 订单信息: orderSn={}, orderType={}, useIntegration={}, payAmount={}",
                   order.getOrderSn(), order.getOrderType(), order.getUseIntegration(), order.getPayAmount());
        orderMapper.insert(order);
        for (OmsOrderItem orderItem : orderItemList) {
            orderItem.setOrderId(order.getId());
            orderItem.setOrderSn(order.getOrderSn());
        }
        orderItemDao.insertList(orderItemList);
        //如使用优惠券更新优惠券使用状态
        if (orderParam.getCouponId() != null) {
            updateCouponStatus(orderParam.getCouponId(), currentMember.getId(), 1);
        }
        // 移除积分消耗逻辑 - 不再支持下单时使用积分抵扣
        // useIntegration字段已经在订单插入数据库之前设置，这里不再重复设置
        LOGGER.info("订单生成 - useIntegration参数检查: orderParam.getUseIntegration()={}, order.getUseIntegration()={}",
                   orderParam.getUseIntegration(), order.getUseIntegration());
        
        //删除购物车中的下单商品
        deleteCartItemList(cartPromotionItemList, currentMember);
        //发送延迟消息取消订单
        sendDelayMessageCancelOrder(order.getId());

        // 处理余额支付
        if (orderParam.getPayType() != null && orderParam.getPayType() == 3) {
            // 余额支付逻辑
            BigDecimal payAmount = order.getPayAmount();

            // 检查余额是否充足
            if (!memberBalanceService.checkBalanceEnough(currentMember.getId(), payAmount)) {
                throw new ApiException("余额不足，无法完成支付");
            }

            // 扣除用户余额并记录消费记录
            boolean success = memberBalanceService.recordBalanceChange(
                currentMember.getId(),
                2, // 消费类型
                payAmount,
                "ORDER_PAY",
                order.getOrderSn(),
                "订单支付：" + order.getOrderSn()
            );

            if (!success) {
                throw new ApiException("余额支付失败");
            }

            // 更新订单状态为已支付
            order.setStatus(1); // 待发货
            order.setPaymentTime(new Date());
            order.setPayType(3); // 余额支付
            // 确保payAmount字段被正确保存
            order.setPayAmount(payAmount);
            orderMapper.updateByPrimaryKeySelective(order);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("orderItemList", orderItemList);
        return result;
    }

    @NotNull
    private static OmsOrderItem getOmsOrderItem(CartPromotionItem cartPromotionItem) {
        OmsOrderItem orderItem = new OmsOrderItem();
        orderItem.setProductId(cartPromotionItem.getProductId());
        orderItem.setProductName(cartPromotionItem.getProductName());
        orderItem.setProductPic(cartPromotionItem.getProductPic());
        orderItem.setProductAttr(cartPromotionItem.getProductAttr());
        orderItem.setProductBrand(cartPromotionItem.getProductBrand());
        orderItem.setProductSn(cartPromotionItem.getProductSn());
        orderItem.setProductPrice(cartPromotionItem.getPrice());
        orderItem.setProductQuantity(cartPromotionItem.getQuantity());
        orderItem.setProductSkuId(cartPromotionItem.getProductSkuId());
        orderItem.setProductSkuCode(cartPromotionItem.getProductSkuCode());
        orderItem.setProductCategoryId(cartPromotionItem.getProductCategoryId());
        orderItem.setPromotionAmount(cartPromotionItem.getReduceAmount());
        orderItem.setPromotionName(cartPromotionItem.getPromotionMessage());
        orderItem.setGiftIntegration(cartPromotionItem.getIntegration());
        orderItem.setGiftGrowth(cartPromotionItem.getGrowth());
        return orderItem;
    }

    @Override
    public Map<String, Object> paySuccess(Long orderId, Integer payType, String giftMessage, String giftPic) {
        //修改订单支付状态
        OmsOrder order = new OmsOrder();
        order.setId(orderId);
        order.setStatus(1);
        order.setPaymentTime(new Date());
        order.setPayType(payType);

        //获取订单详情
        OmsOrderDetail orderDetail = portalOrderDao.getDetail(orderId);

        // 确保订单详情中的payAmount字段不为null
        if (orderDetail != null && orderDetail.getPayAmount() == null) {
            // 如果payAmount为null，重新计算并设置
            BigDecimal calculatedPayAmount = calcPayAmount(orderDetail);
            orderDetail.setPayAmount(calculatedPayAmount);
            LOGGER.warn("订单payAmount为null，已重新计算并设置，订单ID: {}, 计算金额: {}", orderId, calculatedPayAmount);
        }
        LOGGER.info("订单ID:{}, isGift值:{}, 类型:{}, 配送类型:{}, 支付方式:{}", orderId, orderDetail.getIsGift(),
                   orderDetail.getIsGift() != null ? orderDetail.getIsGift().getClass() : "null",
                   orderDetail.getDeliveryType(), payType);

        // 余额支付的特殊处理
        if (payType != null && payType == 3) {
            LOGGER.info("余额支付订单支付成功回调，订单ID:{}, 订单号:{}", orderId, orderDetail.getOrderSn());
            // 余额支付在下单时已经完成扣款，这里主要是确认支付状态
        }

        String pickupCode = null;
        String qrCodeBase64 = null;
        
        // 判断是否为自提订单（deliveryType == 1），生成核销码
        if (orderDetail.getDeliveryType() != null && orderDetail.getDeliveryType() == 1) {
            try {
                // 生成核销码
                pickupCode = pickupCodeUtil.generatePickupCode(orderId);
                if (pickupCode != null) {
                    // 保存到数据库
                    order.setPickupCode(pickupCode);     
                    order.setPickupStatus((byte) 0); // 未核销
                    
                    // 生成二维码
                    qrCodeBase64 = qrCodeService.generatePickupCodeQR(pickupCode);
                    
                    LOGGER.info("自提订单生成核销码成功，订单ID:{}, 核销码:{}", orderId, pickupCode);
                } else {
                    LOGGER.error("自提订单生成核销码失败，订单ID:{}", orderId);
                }
            } catch (Exception e) {
                LOGGER.error("生成核销码异常，订单ID:{}, 错误:{}", orderId, e.getMessage(), e);
            }
        }
        
        // 更新订单状态
        orderMapper.updateByPrimaryKeySelective(order);

        if (Boolean.TRUE.equals(orderDetail.getIsGift())) {
            LOGGER.info("开始处理送礼记录，订单ID:{}, 礼品消息:{}, 礼品图片:{}", orderId, giftMessage, giftPic);
            omsGiftService.handleGiftOrderPaySuccess(orderId, giftMessage, giftPic);
        } else {
            LOGGER.info("不是送礼订单或isGift值为:{}", orderDetail.getIsGift());
        }
        
        //恢复所有下单商品的锁定库存，扣减真实库存，同时更新销量
        LOGGER.info("开始更新库存和销量，订单ID: {}, 订单项数量: {}", orderId, orderDetail.getOrderItemList().size());
        
        // 打印订单项信息验证字段完整性
        for (OmsOrderItem item : orderDetail.getOrderItemList()) {
            LOGGER.info("订单项详情 - 商品ID: {}, SKU ID: {}, 商品名称: {}, 数量: {}, 价格: {}", 
                item.getProductId(), item.getProductSkuId(), item.getProductName(), item.getProductQuantity(), item.getProductPrice());
        }
        
        int count = 0;
        
        // 更新总库存
        int skuStockCount = portalOrderDao.updateSkuStock(orderDetail.getOrderItemList());
        LOGGER.info("更新总库存表影响行数: {}", skuStockCount);
        count += skuStockCount;
        
        // 同步扣减门店库存
        if (orderDetail.getStoreId() != null) {
            // 有门店ID：扣减指定门店库存并记录日志
            int storeSkuStockCount = deductStoreStockWithLog(orderDetail.getOrderItemList(), orderDetail.getStoreId());
            LOGGER.info("更新指定门店库存表影响行数: {}, 门店ID: {}", storeSkuStockCount, orderDetail.getStoreId());
            count += storeSkuStockCount;
        } else {
            // 没有门店ID：智能扣减门店库存（优先地库，地库不足则扣减库存最多的门店）
            int storeStockCount = smartDeductStoreStock(orderDetail.getOrderItemList());
            LOGGER.info("智能扣减门店库存影响行数: {}", storeStockCount);
            count += storeStockCount;
        }
        
        // 更新商品销量
        int productSaleCount = portalOrderDao.updateProductSale(orderDetail.getOrderItemList());
        LOGGER.info("更新商品销量表影响行数: {}", productSaleCount);
        count += productSaleCount;
        
        LOGGER.info("库存和销量更新完成，总影响行数: {}", count);

        // 支付成功后处理积分奖励
        try {
            processIntegrationReward(orderDetail);
            LOGGER.info("支付成功后积分奖励处理完成，订单ID: {}", orderId);
        } catch (Exception e) {
            LOGGER.error("支付成功后处理积分奖励失败，订单ID: {}, 错误: {}", orderId, e.getMessage(), e);
            // 积分奖励处理失败不影响订单支付成功
        }

        // 支付成功后计算分销佣金（状态为待结算）
        try {
            if (commissionService instanceof CommissionServiceImpl) {
                ((CommissionServiceImpl) commissionService).calculateCommissionOnPayment(orderId);
                LOGGER.info("支付成功后佣金计算完成，订单ID: {}", orderId);
            }
        } catch (Exception e) {
            LOGGER.error("支付成功后计算佣金失败，订单ID: {}, 错误: {}", orderId, e.getMessage(), e);
            // 佣金计算失败不影响订单支付成功
        }
        
        // 支付成功后更新商品回本分析数据
        try {
            paybackService.updateOnPaymentSuccess(orderId, orderDetail.getOrderSn());
            LOGGER.info("支付成功后回本分析更新完成，订单ID: {}", orderId);
        } catch (Exception e) {
            LOGGER.error("支付成功后更新回本分析失败，订单ID: {}, 错误: {}", orderId, e.getMessage(), e);
            // 回本分析更新失败不影响订单支付成功
        }

        // 支付成功后处理邀请奖励
        try {
            processInviteRewardOnPayment(orderDetail);
            LOGGER.info("支付成功后邀请奖励处理完成，订单ID: {}", orderId);
        } catch (Exception e) {
            LOGGER.error("支付成功后处理邀请奖励失败，订单ID: {}, 错误: {}", orderId, e.getMessage(), e);
            // 邀请奖励处理失败不影响订单支付成功
        }

        // 发送下单成功订阅消息通知
        try {
            sendOrderSuccessNotification(orderDetail, pickupCode);
            LOGGER.info("下单成功通知发送完成，订单ID: {}", orderId);
        } catch (Exception e) {
            LOGGER.error("发送下单成功通知失败，订单ID: {}, 错误: {}", orderId, e.getMessage(), e);
            // 通知发送失败不影响订单支付成功
        }
        
        // 发送新订单模板消息通知给管理员和门店店长
        try {
            sendNewOrderNotificationToAdmin(orderDetail);
            LOGGER.info("新订单管理员通知发送完成，订单ID: {}", orderId);
        } catch (Exception e) {
            LOGGER.error("发送新订单管理员通知失败，订单ID: {}, 错误: {}", orderId, e.getMessage(), e);
            // 通知发送失败不影响订单支付成功
        }
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("isPickupOrder", orderDetail.getDeliveryType() != null && orderDetail.getDeliveryType() == 1);
        
        if (pickupCode != null) {
            result.put("pickupCode", pickupCode);
            if (qrCodeBase64 != null) {
                result.put("qrCodeBase64", qrCodeBase64);
            }
        }
        
        return result;
    }

    @Override
    public Integer cancelTimeOutOrder() {
        Integer count=0;
        OmsOrderSetting orderSetting = orderSettingMapper.selectByPrimaryKey(1L);
        //查询超时、未支付的订单及订单详情
        List<OmsOrderDetail> timeOutOrders = portalOrderDao.getTimeOutOrders(orderSetting.getNormalOrderOvertime());
        if (CollectionUtils.isEmpty(timeOutOrders)) {
            return count;
        }
        //修改订单状态为交易取消
        List<Long> ids = new ArrayList<>();
        for (OmsOrderDetail timeOutOrder : timeOutOrders) {
            ids.add(timeOutOrder.getId());
        }
        portalOrderDao.updateOrderStatus(ids, 4);
        for (OmsOrderDetail timeOutOrder : timeOutOrders) {
            //解除订单商品库存锁定：无论什么类型订单都要释放总库存和门店库存锁定
            if (timeOutOrder.getStoreId() != null) {
                // 有门店信息：分别释放总库存和门店库存锁定
                portalOrderDao.releaseSkuStockLock(timeOutOrder.getOrderItemList());
                portalOrderDao.releaseStoreSkuStockLock(timeOutOrder.getOrderItemList(), timeOutOrder.getStoreId());
            } else {
                // 无门店信息：只释放总库存锁定
                portalOrderDao.releaseSkuStockLock(timeOutOrder.getOrderItemList());
            }
            //修改优惠券使用状态
            updateCouponStatus(timeOutOrder.getCouponId(), timeOutOrder.getMemberId(), 0);
            // 移除积分返还逻辑 - 因为下单时不再扣除积分，所以取消时也不需要返还
        }
        return timeOutOrders.size();
    }

    @Override
    public void cancelOrder(Long orderId) {
        //查询未付款的取消订单
        OmsOrderExample example = new OmsOrderExample();
        example.createCriteria().andIdEqualTo(orderId).andStatusEqualTo(0).andDeleteStatusEqualTo(0);
        List<OmsOrder> cancelOrderList = orderMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(cancelOrderList)) {
            return;
        }
        OmsOrder cancelOrder = cancelOrderList.get(0);
        if (cancelOrder != null) {
            //修改订单状态为取消
            cancelOrder.setStatus(4);
            orderMapper.updateByPrimaryKeySelective(cancelOrder);
            OmsOrderItemExample orderItemExample = new OmsOrderItemExample();
            orderItemExample.createCriteria().andOrderIdEqualTo(orderId);
            List<OmsOrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample);
            //解除订单商品库存锁定：无论什么类型订单都要释放总库存和门店库存锁定
            if (!CollectionUtils.isEmpty(orderItemList)) {
                if (cancelOrder.getStoreId() != null) {
                    // 有门店信息：分别释放总库存和门店库存锁定
                    portalOrderDao.releaseSkuStockLock(orderItemList);
                    portalOrderDao.releaseStoreSkuStockLock(orderItemList, cancelOrder.getStoreId());
                } else {
                    // 无门店信息：只释放总库存锁定
                    portalOrderDao.releaseSkuStockLock(orderItemList);
                }
            }
            //修改优惠券使用状态
            updateCouponStatus(cancelOrder.getCouponId(), cancelOrder.getMemberId(), 0);
            //返还使用积分
            if (cancelOrder.getUseIntegration() != null && cancelOrder.getUseIntegration() > 0) {
                // 使用统一积分服务返还积分
                boolean result = integrationService.updateIntegrationAndCheckLevel(
                    cancelOrder.getMemberId(),
                    cancelOrder.getUseIntegration(),
                    "取消订单返还积分",
                    0 // 0->购物
                );

                if (result) {
                    LOGGER.info("订单取消积分返还成功: memberId={}, orderId={}, points={}",
                        cancelOrder.getMemberId(), cancelOrder.getId(), cancelOrder.getUseIntegration());
                } else {
                    LOGGER.warn("订单取消积分返还失败: memberId={}, orderId={}, points={}",
                        cancelOrder.getMemberId(), cancelOrder.getId(), cancelOrder.getUseIntegration());
                }
            }
        }
        
        // 订单取消时处理分销佣金（将待结算佣金状态改为失败）
        try {
            // 查询该订单的待结算佣金并标记为失败
            PmsInviteRewardExample commissionExample = new PmsInviteRewardExample();
            commissionExample.createCriteria()
                    .andOrderIdEqualTo(orderId)
                    .andSettlementStatusEqualTo((byte) 0); // 待结算状态
            List<PmsInviteReward> pendingCommissions = inviteRewardMapper.selectByExample(commissionExample);
            
            if (!CollectionUtils.isEmpty(pendingCommissions)) {
                List<Long> commissionIds = pendingCommissions.stream()
                        .map(PmsInviteReward::getId)
                        .collect(Collectors.toList());
                commissionService.updateCommissionSettlement(commissionIds, 2); // 2=结算失败
                LOGGER.info("订单取消后佣金状态更新完成，订单ID: {}, 佣金数量: {}", orderId, commissionIds.size());
            }
        } catch (Exception e) {
            LOGGER.error("订单取消后处理佣金失败，订单ID: {}, 错误: {}", orderId, e.getMessage(), e);
            // 佣金处理失败不影响订单取消
        }
    }

    @Override
    public void sendDelayMessageCancelOrder(Long orderId) {
        //获取订单超时时间
        OmsOrderSetting orderSetting = orderSettingMapper.selectByPrimaryKey(1L);
        long delayTimes = orderSetting.getNormalOrderOvertime() * 60 * 1000;
        //发送延迟消息
        cancelOrderSender.sendMessage(orderId, delayTimes);
    }

    @Override
    public void confirmReceiveOrder(Long orderId) {
        UmsMember member = memberService.getCurrentMember();
        OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
        if(!member.getId().equals(order.getMemberId())){
            Asserts.fail("不能确认他人订单！");
        }
        if(order.getStatus()!=2){
            Asserts.fail("该订单还未发货！");
        }
        order.setStatus(3);
        order.setConfirmStatus(1);
        order.setReceiveTime(new Date());
        orderMapper.updateByPrimaryKey(order);
        
        // 订单完成后给用户增加积分
        if (order.getIntegration() != null && order.getIntegration() > 0) {
            // 使用统一积分服务更新积分并检查等级升级
            boolean result = integrationService.updateIntegrationAndCheckLevel(
                member.getId(),
                order.getIntegration(),
                "订单完成赠送积分",
                0 // 0->购物
            );

            if (result) {
                LOGGER.info("订单完成积分发放成功: memberId={}, orderId={}, points={}",
                    member.getId(), orderId, order.getIntegration());
            } else {
                LOGGER.warn("订单完成积分发放失败: memberId={}, orderId={}, points={}",
                    member.getId(), orderId, order.getIntegration());
            }
        }
        
        // 订单完成后发放分销佣金（更新状态为已结算）
        try {
            if (commissionService instanceof CommissionServiceImpl) {
                ((CommissionServiceImpl) commissionService).settleCommissionOnOrderComplete(orderId);
                LOGGER.info("订单完成后佣金发放完成，订单ID: {}", orderId);
            }
        } catch (Exception e) {
            LOGGER.error("订单完成后发放佣金失败，订单ID: {}, 错误: {}", orderId, e.getMessage(), e);
            // 佣金发放失败不影响订单确认收货
        }
    }

    @Override
    public CommonPage<OmsOrderDetail> list(Integer status, Integer pageNum, Integer pageSize) {
        UmsMember member = memberService.getCurrentMember();
        PageHelper.startPage(pageNum,pageSize);
        OmsOrderExample orderExample = new OmsOrderExample();
        OmsOrderExample.Criteria criteria = orderExample.createCriteria();
        criteria.andDeleteStatusEqualTo(0)
                .andMemberIdEqualTo(member.getId());

        if(status == -1){
            // 全部订单，不添加状态筛选
        } else if(status == 5) {
            // 售后状态：特殊处理，筛选有售后申请的订单（after_sale_status > 0）
            // 注意：这里不是按订单状态筛选，而是按售后状态筛选
            criteria.andAfterSaleStatusGreaterThan(0);
        } else if(status != null) {
            // 其他状态：按订单状态筛选（status字段）
            // 前端会过滤掉有售后申请的订单，后端返回所有对应状态的订单
            criteria.andStatusEqualTo(status);
        }

        orderExample.setOrderByClause("create_time desc");
        List<OmsOrder> orderList = orderMapper.selectByExample(orderExample);
        CommonPage<OmsOrder> orderPage = CommonPage.restPage(orderList);
        //设置分页信息
        CommonPage<OmsOrderDetail> resultPage = new CommonPage<>();
        resultPage.setPageNum(orderPage.getPageNum());
        resultPage.setPageSize(orderPage.getPageSize());
        resultPage.setTotal(orderPage.getTotal());
        resultPage.setTotalPage(orderPage.getTotalPage());
        if(CollUtil.isEmpty(orderList)){
            return resultPage;
        }
        //设置数据信息
        List<Long> orderIds = orderList.stream().map(OmsOrder::getId).collect(Collectors.toList());
        OmsOrderItemExample orderItemExample = new OmsOrderItemExample();
        orderItemExample.createCriteria().andOrderIdIn(orderIds);
        List<OmsOrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample);
        
        // 查询退货申请状态和详细售后信息
        Map<Long, Integer> returnApplyStatusMap = new HashMap<>();
        Map<Long, OmsOrderDetail.AfterSaleStatusInfo> afterSaleStatusMap = new HashMap<>();
        if (!orderIds.isEmpty()) {
            OmsOrderReturnApplyExample returnApplyExample = new OmsOrderReturnApplyExample();
            returnApplyExample.createCriteria().andOrderIdIn(orderIds);
            List<OmsOrderReturnApply> returnApplyList = orderReturnApplyMapper.selectByExample(returnApplyExample);
            for (OmsOrderReturnApply returnApply : returnApplyList) {
                returnApplyStatusMap.put(returnApply.getOrderId(), 1); // 有退货申请

                // 构建详细的售后状态信息
                OmsOrderDetail.AfterSaleStatusInfo statusInfo = new OmsOrderDetail.AfterSaleStatusInfo();
                statusInfo.setReturnApplyId(returnApply.getId());
                statusInfo.setReturnApplyStatus(returnApply.getStatus());
                statusInfo.setRefundProcessStatus(returnApply.getRefundProcessStatus());
                statusInfo.setReturnType(returnApply.getReturnType());

                // 根据订单状态和售后申请状态确定整体售后状态
                Integer afterSaleStatus = determineAfterSaleStatus(returnApply);
                statusInfo.setAfterSaleStatus(afterSaleStatus);
                statusInfo.setStatusDescription(getAfterSaleStatusDescription(returnApply));

                afterSaleStatusMap.put(returnApply.getOrderId(), statusInfo);
            }
        }
        
        // 查询评价状态 - 使用订单ID+商品ID+用户ID精确查询
        Map<Long, Integer> commentStatusMap = new HashMap<>();
        if (!orderIds.isEmpty()) {
            for (Long orderId : orderIds) {
                // 查询该订单下的商品是否有评价
                List<OmsOrderItem> orderItems = orderItemList.stream()
                    .filter(item -> item.getOrderId().equals(orderId))
                    .collect(Collectors.toList());
                
                boolean hasComment = false;
                for (OmsOrderItem orderItem : orderItems) {
                    // 使用订单ID+商品ID+用户ID进行精确查询
                    PmsCommentExample commentExample = new PmsCommentExample();
                    commentExample.createCriteria()
                        .andOrderIdEqualTo(orderId)
                        .andProductIdEqualTo(orderItem.getProductId())
                        .andMemberIdEqualTo(member.getId());
                    long commentCount = commentMapper.countByExample(commentExample);
                    if (commentCount > 0) {
                        hasComment = true;
                        break;
                    }
                }
                commentStatusMap.put(orderId, hasComment ? 1 : 0);
            }
        }
        
        List<OmsOrderDetail> orderDetailList = new ArrayList<>();
        for (OmsOrder omsOrder : orderList) {
            OmsOrderDetail orderDetail = new OmsOrderDetail();
            BeanUtil.copyProperties(omsOrder,orderDetail);
            List<OmsOrderItem> relatedItemList = orderItemList.stream().filter(item -> item.getOrderId().equals(orderDetail.getId())).collect(Collectors.toList());
            orderDetail.setOrderItemList(relatedItemList);
            
            // 设置退货申请状态
            orderDetail.setHasReturnApply(returnApplyStatusMap.getOrDefault(omsOrder.getId(), 0));

            // 设置详细的售后状态信息
            OmsOrderDetail.AfterSaleStatusInfo afterSaleInfo = afterSaleStatusMap.get(omsOrder.getId());
            if (afterSaleInfo == null) {
                // 没有售后申请时，创建默认状态信息
                afterSaleInfo = new OmsOrderDetail.AfterSaleStatusInfo();
                afterSaleInfo.setAfterSaleStatus(0); // 无售后
                afterSaleInfo.setStatusDescription("无售后申请");
            }
            orderDetail.setAfterSaleStatusInfo(afterSaleInfo);

            // 设置评价状态
            orderDetail.setHasComment(commentStatusMap.getOrDefault(omsOrder.getId(), 0));
            
            orderDetailList.add(orderDetail);
        }
        resultPage.setList(orderDetailList);
        return resultPage;
    }

    @Override
    public OmsOrderDetail detail(Long orderId) {
        OmsOrder omsOrder = orderMapper.selectByPrimaryKey(orderId);
        OmsOrderItemExample example = new OmsOrderItemExample();
        example.createCriteria().andOrderIdEqualTo(orderId);
        List<OmsOrderItem> orderItemList = orderItemMapper.selectByExample(example);
        
        // 查询是否有退货申请和详细售后信息
        OmsOrderReturnApplyExample returnApplyExample = new OmsOrderReturnApplyExample();
        returnApplyExample.createCriteria().andOrderIdEqualTo(orderId);
        List<OmsOrderReturnApply> returnApplyList = orderReturnApplyMapper.selectByExample(returnApplyExample);

        OmsOrderDetail orderDetail = new OmsOrderDetail();
        BeanUtil.copyProperties(omsOrder,orderDetail);
        orderDetail.setOrderItemList(orderItemList);
        orderDetail.setHasReturnApply(returnApplyList.isEmpty() ? 0 : 1);

        // 设置详细的售后状态信息
        if (!returnApplyList.isEmpty()) {
            // 如果有多个售后申请，取最新的一个
            OmsOrderReturnApply latestReturnApply = returnApplyList.stream()
                .max((a, b) -> a.getCreateTime().compareTo(b.getCreateTime()))
                .orElse(null);

            if (latestReturnApply != null) {
                OmsOrderDetail.AfterSaleStatusInfo statusInfo = new OmsOrderDetail.AfterSaleStatusInfo();
                statusInfo.setReturnApplyId(latestReturnApply.getId());
                statusInfo.setReturnApplyStatus(latestReturnApply.getStatus());
                statusInfo.setRefundProcessStatus(latestReturnApply.getRefundProcessStatus());
                statusInfo.setReturnType(latestReturnApply.getReturnType());
                statusInfo.setAfterSaleStatus(determineAfterSaleStatus(latestReturnApply));
                statusInfo.setStatusDescription(getAfterSaleStatusDescription(latestReturnApply));
                orderDetail.setAfterSaleStatusInfo(statusInfo);
            }
        } else {
            // 没有售后申请时，创建默认状态信息
            OmsOrderDetail.AfterSaleStatusInfo statusInfo = new OmsOrderDetail.AfterSaleStatusInfo();
            statusInfo.setAfterSaleStatus(0); // 无售后
            statusInfo.setStatusDescription("无售后申请");
            orderDetail.setAfterSaleStatusInfo(statusInfo);
        }
        
        // 查询是否已评价
        UmsMember member = memberService.getCurrentMember();
        // 使用订单ID+商品ID+用户ID进行精确评价状态查询
        boolean hasComment = false;
        for (OmsOrderItem orderItem : orderItemList) {
            PmsCommentExample commentExample = new PmsCommentExample();
            commentExample.createCriteria()
                .andOrderIdEqualTo(orderId)
                .andProductIdEqualTo(orderItem.getProductId())
                .andMemberIdEqualTo(member.getId());
            long commentCount = commentMapper.countByExample(commentExample);
            if (commentCount > 0) {
                hasComment = true;
                break;
            }
        }
        orderDetail.setHasComment(hasComment ? 1 : 0);
        
        // 填充运费详情信息
        fillFreightDetail(orderDetail, orderItemList);
        
        // 填充门店详情信息
        fillStoreDetail(orderDetail);
        
        return orderDetail;
    }
    
    /**
     * 填充订单运费详情信息
     */
    private void fillFreightDetail(OmsOrderDetail orderDetail, List<OmsOrderItem> orderItemList) {
        try {
            // 如果运费为0或null，设置为包邮
            if (orderDetail.getFreightAmount() == null || orderDetail.getFreightAmount().compareTo(BigDecimal.ZERO) == 0) {
                orderDetail.setIsFreeShipping(true);
                orderDetail.setFreeShippingReason("包邮订单");
                return;
            }
            
            // 构建运费计算参数（重新计算运费详情用于显示）
            List<CartPromotionItem> cartItems = convertToCartPromotionItems(orderItemList);
            UmsMemberReceiveAddress receiveAddress = buildReceiveAddressFromOrder(orderDetail);
            
            // 调用运费计算服务获取详情
            SmsFreightCalculateParam calculateParam = buildFreightCalculateParam(cartItems, receiveAddress);
            SmsFreightCalculateResult freightDetail = freightCalculateService.calculateFreight(calculateParam);
            
            if (freightDetail != null) {
                orderDetail.setFreightDetail(freightDetail);
                orderDetail.setIsFreeShipping(freightDetail.getIsFreeShipping());
                orderDetail.setFreeShippingReason(freightDetail.getFreeShippingReason());
            } else {
                orderDetail.setIsFreeShipping(false);
                orderDetail.setFreeShippingReason("");
            }
            
        } catch (Exception e) {
            LOGGER.warn("填充订单运费详情失败，订单ID: {}", orderDetail.getId(), e);
            orderDetail.setIsFreeShipping(orderDetail.getFreightAmount().compareTo(BigDecimal.ZERO) == 0);
            orderDetail.setFreeShippingReason(orderDetail.getIsFreeShipping() ? "包邮订单" : "");
        }
    }
    
    /**
     * 将订单商品转换为购物车商品项（用于运费计算）
     */
    private List<CartPromotionItem> convertToCartPromotionItems(List<OmsOrderItem> orderItemList) {
        List<CartPromotionItem> cartItems = new ArrayList<>();
        for (OmsOrderItem orderItem : orderItemList) {
            CartPromotionItem cartItem = new CartPromotionItem();
            cartItem.setProductId(orderItem.getProductId());
            cartItem.setProductSkuId(orderItem.getProductSkuId());
            cartItem.setQuantity(orderItem.getProductQuantity());
            cartItem.setPrice(orderItem.getProductPrice());
            // 设置默认重量0.5kg
            cartItem.setReduceAmount(orderItem.getPromotionAmount() != null ? orderItem.getPromotionAmount() : BigDecimal.ZERO);
            cartItems.add(cartItem);
        }
        return cartItems;
    }
    
    /**
     * 从订单信息构建收货地址对象（用于运费计算）
     */
    private UmsMemberReceiveAddress buildReceiveAddressFromOrder(OmsOrderDetail orderDetail) {
        UmsMemberReceiveAddress address = new UmsMemberReceiveAddress();
        address.setProvince(orderDetail.getReceiverProvince());
        address.setCity(orderDetail.getReceiverCity());
        address.setRegion(orderDetail.getReceiverRegion());
        address.setDetailAddress(orderDetail.getReceiverDetailAddress());
        return address;
    }
    
    /**
     * 填充订单门店详情信息
     */
    private void fillStoreDetail(OmsOrderDetail orderDetail) {
        try {
            // 只有门店自取订单才需要填充门店信息
            if (orderDetail.getDeliveryType() == null || orderDetail.getDeliveryType() != 1 || orderDetail.getStoreId() == null) {
                return;
            }
            
            // 如果订单中的门店信息不完整，从门店表中获取最新信息
            if (orderDetail.getStoreName() == null || orderDetail.getStoreAddress() == null) {
                OmsStoreAddress storeAddress = storeAddressMapper.selectByPrimaryKey(orderDetail.getStoreId());
                if (storeAddress != null) {
                    orderDetail.setStoreName(storeAddress.getAddressName());
                    orderDetail.setStoreAddress(storeAddress.getDetailAddress());
                    orderDetail.setStorePhone(storeAddress.getPhone());
                }
            }
            
        } catch (Exception e) {
            LOGGER.warn("填充订单门店详情失败，订单ID: {}", orderDetail.getId(), e);
        }
    }

    @Override
    public void deleteOrder(Long orderId) {
        UmsMember member = memberService.getCurrentMember();
        OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
        if(!member.getId().equals(order.getMemberId())){
            Asserts.fail("不能删除他人订单！");
        }
        if(order.getStatus()==3||order.getStatus()==4){
            order.setDeleteStatus(1);
            orderMapper.updateByPrimaryKey(order);
        }else{
            Asserts.fail("只能删除已完成或已关闭的订单！");
        }
    }

    @Override
    public void paySuccessByOrderSn(String orderSn, Integer payType) {
        OmsOrderExample example =  new OmsOrderExample();
        example.createCriteria()
                .andOrderSnEqualTo(orderSn)
                .andStatusEqualTo(0)
                .andDeleteStatusEqualTo(0);
        List<OmsOrder> orderList = orderMapper.selectByExample(example);
        if(CollUtil.isNotEmpty(orderList)){
            OmsOrder order = orderList.get(0);
            paySuccess(order.getId(),payType, null, null);
        }
    }

    /**
     * 生成订单编号:
     * 正式环境: GHZ+8位日期+2位平台号码+2位支付方式+6位以上自增id
     * 测试环境: TEST_GHZ+8位日期+2位平台号码+2位支付方式+6位以上自增id
     */
    private String generateOrderSn(OmsOrder order) {
        StringBuilder sb = new StringBuilder();
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String key = REDIS_DATABASE+":"+ REDIS_KEY_ORDER_ID + date;
        Long increment = redisService.incr(key, 1);

        // 根据环境添加不同的前缀
        if (!"prod".equals(activeProfile)) {
            // 非生产环境（包括dev、test等）都添加TEST_前缀
            sb.append("TEST_");
        }

        sb.append("GHZ");
        sb.append(date);
        sb.append(String.format("%02d", order.getSourceType()));
        sb.append(String.format("%02d", order.getPayType()));
        String incrementStr = increment.toString();
        if (incrementStr.length() <= 6) {
            sb.append(String.format("%06d", increment));
        } else {
            sb.append(incrementStr);
        }
        return sb.toString();
    }

    /**
     * 删除下单商品的购物车信息
     */
    private void deleteCartItemList(List<CartPromotionItem> cartPromotionItemList, UmsMember currentMember) {
        List<Long> ids = new ArrayList<>();
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            ids.add(cartPromotionItem.getId());
        }
        cartItemService.delete(currentMember.getId(), ids);
    }

    /**
     * 计算该订单赠送的成长值
     */
    private Integer calcGiftGrowth(List<OmsOrderItem> orderItemList) {
        Integer sum = 0;
        for (OmsOrderItem orderItem : orderItemList) {
            sum = sum + orderItem.getGiftGrowth() * orderItem.getProductQuantity();
        }
        return sum;
    }

    /**
     * 计算该订单赠送的积分
     */
    private Integer calcGifIntegration(List<OmsOrderItem> orderItemList) {
        int sum = 0;
        for (OmsOrderItem orderItem : orderItemList) {
            sum += orderItem.getGiftIntegration() * orderItem.getProductQuantity();
        }
        return sum;
    }

    /**
     * 将优惠券信息更改为指定状态
     *
     * @param couponId  优惠券id
     * @param memberId  会员id
     * @param useStatus 0->未使用；1->已使用
     */
    private void updateCouponStatus(Long couponId, Long memberId, Integer useStatus) {
        if (couponId == null) return;
        //查询第一张优惠券
        SmsCouponHistoryExample example = new SmsCouponHistoryExample();
        example.createCriteria().andMemberIdEqualTo(memberId)
                .andCouponIdEqualTo(couponId).andUseStatusEqualTo(useStatus == 0 ? 1 : 0);
        List<SmsCouponHistory> couponHistoryList = couponHistoryMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(couponHistoryList)) {
            SmsCouponHistory couponHistory = couponHistoryList.get(0);
            couponHistory.setUseTime(new Date());
            couponHistory.setUseStatus(useStatus);
            couponHistoryMapper.updateByPrimaryKeySelective(couponHistory);
        }
    }

    private void handleRealAmount(List<OmsOrderItem> orderItemList) {
        for (OmsOrderItem orderItem : orderItemList) {
            //原价-促销优惠-优惠券抵扣-积分抵扣
            BigDecimal realAmount = orderItem.getProductPrice()
                    .subtract(orderItem.getPromotionAmount())
                    .subtract(orderItem.getCouponAmount())
                    .subtract(orderItem.getIntegrationAmount());
            orderItem.setRealAmount(realAmount);
        }
    }

    /**
     * 获取订单促销信息
     */
    private String getOrderPromotionInfo(List<OmsOrderItem> orderItemList) {
        StringBuilder sb = new StringBuilder();
        for (OmsOrderItem orderItem : orderItemList) {
            sb.append(orderItem.getPromotionName());
            sb.append(";");
        }
        String result = sb.toString();
        if (result.endsWith(";")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    /**
     * 计算订单应付金额
     */
    private BigDecimal calcPayAmount(OmsOrder order) {
        // 确保所有金额字段不为null，为null时设置为0
        BigDecimal totalAmount = order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO;
        BigDecimal freightAmount = order.getFreightAmount() != null ? order.getFreightAmount() : BigDecimal.ZERO;
        BigDecimal promotionAmount = order.getPromotionAmount() != null ? order.getPromotionAmount() : BigDecimal.ZERO;
        BigDecimal couponAmount = order.getCouponAmount() != null ? order.getCouponAmount() : BigDecimal.ZERO;
        BigDecimal integrationAmount = order.getIntegrationAmount() != null ? order.getIntegrationAmount() : BigDecimal.ZERO;

        //总金额+运费-促销优惠-优惠券优惠-积分抵扣
        BigDecimal payAmount = totalAmount
                .add(freightAmount)
                .subtract(promotionAmount)
                .subtract(couponAmount)
                .subtract(integrationAmount);

        // 确保支付金额不为负数
        if (payAmount.compareTo(BigDecimal.ZERO) < 0) {
            payAmount = BigDecimal.ZERO;
        }

        return payAmount;
    }

    /**
     * 计算订单优惠券金额
     */
    private BigDecimal calcIntegrationAmount(List<OmsOrderItem> orderItemList) {
        BigDecimal integrationAmount = new BigDecimal(0);
        for (OmsOrderItem orderItem : orderItemList) {
            if (orderItem.getIntegrationAmount() != null) {
                integrationAmount = integrationAmount.add(orderItem.getIntegrationAmount().multiply(new BigDecimal(orderItem.getProductQuantity())));
            }
        }
        return integrationAmount;
    }

    /**
     * 计算订单优惠券金额
     */
    private BigDecimal calcCouponAmount(List<OmsOrderItem> orderItemList) {
        BigDecimal couponAmount = new BigDecimal(0);
        for (OmsOrderItem orderItem : orderItemList) {
            if (orderItem.getCouponAmount() != null) {
                couponAmount = couponAmount.add(orderItem.getCouponAmount().multiply(new BigDecimal(orderItem.getProductQuantity())));
            }
        }
        return couponAmount;
    }

    /**
     * 计算订单活动优惠
     */
    private BigDecimal calcPromotionAmount(List<OmsOrderItem> orderItemList) {
        BigDecimal promotionAmount = new BigDecimal(0);
        for (OmsOrderItem orderItem : orderItemList) {
            if (orderItem.getPromotionAmount() != null) {
                promotionAmount = promotionAmount.add(orderItem.getPromotionAmount().multiply(new BigDecimal(orderItem.getProductQuantity())));
            }
        }
        return promotionAmount;
    }

    /**
     * 获取可用积分抵扣金额
     *
     * @param useIntegration 使用的积分数量
     * @param totalAmount    订单总金额
     * @param currentMember  使用的用户
     * @param hasCoupon      是否已经使用优惠券
     */
    private BigDecimal getUseIntegrationAmount(Integer useIntegration, BigDecimal totalAmount, UmsMember currentMember, boolean hasCoupon) {
        BigDecimal zeroAmount = new BigDecimal(0);
        //判断用户是否有这么多积分
        if (useIntegration.compareTo(currentMember.getIntegration()) > 0) {
            return zeroAmount;
        }
        //根据积分使用规则判断是否可用
        //是否可与优惠券共用
        UmsIntegrationConsumeSetting integrationConsumeSetting = integrationConsumeSettingMapper.selectByPrimaryKey(1L);
        if (hasCoupon && integrationConsumeSetting.getCouponStatus().equals(0)) {
            //不可与优惠券共用
            return zeroAmount;
        }
        //是否达到最低使用积分门槛
        if (useIntegration.compareTo(integrationConsumeSetting.getUseUnit()) < 0) {
            return zeroAmount;
        }
        //是否超过订单抵用最高百分比
        BigDecimal integrationAmount = new BigDecimal(useIntegration).divide(new BigDecimal(integrationConsumeSetting.getUseUnit()), 2, RoundingMode.HALF_EVEN);
        BigDecimal maxPercent = new BigDecimal(integrationConsumeSetting.getMaxPercentPerOrder()).divide(new BigDecimal(100), 2, RoundingMode.HALF_EVEN);
        if (integrationAmount.compareTo(totalAmount.multiply(maxPercent)) > 0) {
            return zeroAmount;
        }
        return integrationAmount;
    }

    /**
     * 对优惠券优惠进行处理
     *
     * @param orderItemList       order_item列表
     * @param couponHistoryDetail 可用优惠券详情
     */
    private void handleCouponAmount(List<OmsOrderItem> orderItemList, SmsCouponHistoryDetail couponHistoryDetail) {
        SmsCoupon coupon = couponHistoryDetail.getCoupon();

        // 使用新的适用性检查逻辑，筛选出适用的订单项
        List<OmsOrderItem> applicableOrderItems = new ArrayList<>();
        for (OmsOrderItem orderItem : orderItemList) {
            boolean isApplicable = couponApplicabilityUtil.isCouponApplicableToProduct(
                coupon, orderItem.getProductId(), orderItem.getProductCategoryId());

            if (isApplicable) {
                applicableOrderItems.add(orderItem);
            } else {
                // 不适用的商品，优惠券金额设为0
                orderItem.setCouponAmount(BigDecimal.ZERO);
            }
        }

        // 对适用的订单项计算优惠券金额
        if (!applicableOrderItems.isEmpty()) {
            calcPerCouponAmount(applicableOrderItems, coupon);
        }
    }

    /**
     * 对每个下单商品进行优惠券金额分摊的计算
     *
     * @param orderItemList 可用优惠券的下单商品商品
     */
    private void calcPerCouponAmount(List<OmsOrderItem> orderItemList, SmsCoupon coupon) {
        BigDecimal totalAmount = calcTotalAmount(orderItemList);

        if (coupon.getCouponType() != null && coupon.getCouponType() == 1) {
            // 打折券计算
            if (coupon.getDiscountRate() != null) {
                for (OmsOrderItem orderItem : orderItemList) {
                    // 商品价格 * (1 - 折扣率) = 优惠金额
                    BigDecimal couponAmount = orderItem.getProductPrice()
                        .multiply(BigDecimal.ONE.subtract(coupon.getDiscountRate()))
                        .setScale(2, RoundingMode.HALF_UP);
                    orderItem.setCouponAmount(couponAmount);
                }
            }
        } else {
            // 满减券计算（原有逻辑）
            if (coupon.getAmount() != null) {
                for (OmsOrderItem orderItem : orderItemList) {
                    //(商品价格/可用商品总价)*优惠券面额
                    BigDecimal couponAmount = orderItem.getProductPrice()
                        .divide(totalAmount, 3, RoundingMode.HALF_EVEN)
                        .multiply(coupon.getAmount());
                    orderItem.setCouponAmount(couponAmount);
                }
            }
        }
    }

    /**
     * 获取与优惠券有关系的下单商品
     *
     * @param couponHistoryDetail 优惠券详情
     * @param orderItemList       下单商品
     * @param type                使用关系类型：0->相关分类；1->指定商品
     */
    private List<OmsOrderItem> getCouponOrderItemByRelation(SmsCouponHistoryDetail couponHistoryDetail, List<OmsOrderItem> orderItemList, int type) {
        List<OmsOrderItem> result = new ArrayList<>();
        if (type == 0) {
            List<Long> categoryIdList = new ArrayList<>();
            for (SmsCouponProductCategoryRelation productCategoryRelation : couponHistoryDetail.getCategoryRelationList()) {
                categoryIdList.add(productCategoryRelation.getProductCategoryId());
            }
            for (OmsOrderItem orderItem : orderItemList) {
                if (categoryIdList.contains(orderItem.getProductCategoryId())) {
                    result.add(orderItem);
                } else {
                    orderItem.setCouponAmount(new BigDecimal(0));
                }
            }
        } else if (type == 1) {
            List<Long> productIdList = new ArrayList<>();
            for (SmsCouponProductRelation productRelation : couponHistoryDetail.getProductRelationList()) {
                productIdList.add(productRelation.getProductId());
            }
            for (OmsOrderItem orderItem : orderItemList) {
                if (productIdList.contains(orderItem.getProductId())) {
                    result.add(orderItem);
                } else {
                    orderItem.setCouponAmount(new BigDecimal(0));
                }
            }
        }
        return result;
    }

    /**
     * 获取该用户可以使用的优惠券
     *
     * @param cartPromotionItemList 购物车优惠列表
     * @param couponId              使用优惠券id
     */
    private SmsCouponHistoryDetail getUseCoupon(List<CartPromotionItem> cartPromotionItemList, Long couponId) {
        List<SmsCouponHistoryDetail> couponHistoryDetailList = memberCouponService.listCart(cartPromotionItemList, 1, true);
        for (SmsCouponHistoryDetail couponHistoryDetail : couponHistoryDetailList) {
            if (couponHistoryDetail.getCoupon().getId().equals(couponId)) {
                return couponHistoryDetail;
            }
        }
        return null;
    }

    /**
     * 计算总金额
     */
    private BigDecimal calcTotalAmount(List<OmsOrderItem> orderItemList) {
        BigDecimal totalAmount = new BigDecimal("0");
        for (OmsOrderItem item : orderItemList) {
            totalAmount = totalAmount.add(item.getProductPrice().multiply(new BigDecimal(item.getProductQuantity())));
        }
        return totalAmount;
    }

    /**
     * 进行库存锁定
     */
    private void lockStock(List<CartPromotionItem> cartPromotionItemList) {
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(cartPromotionItem.getProductSkuId());
            skuStock.setLockStock(skuStock.getLockStock() + cartPromotionItem.getQuantity());
            skuStockMapper.updateByPrimaryKeySelective(skuStock);
        }
    }

    /**
     * 进行库存锁定（支持门店库存）
     */
    private void lockStock(List<CartPromotionItem> cartPromotionItemList, Long storeId) {
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            if (storeId != null) {
                // 门店自取订单：锁定门店库存
                PmsStoreSkuStockExample example = new PmsStoreSkuStockExample();
                example.createCriteria().andStoreIdEqualTo(storeId).andSkuIdEqualTo(cartPromotionItem.getProductSkuId());
                List<PmsStoreSkuStock> storeSkuStockList = storeSkuStockMapper.selectByExample(example);
                if (!storeSkuStockList.isEmpty()) {
                    PmsStoreSkuStock storeSkuStock = storeSkuStockList.get(0);
                    storeSkuStock.setLockedStock(storeSkuStock.getLockedStock() + cartPromotionItem.getQuantity());
                    storeSkuStockMapper.updateByPrimaryKeySelective(storeSkuStock);
                }
            } else {
                // 快递配送订单：锁定总库存
                PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(cartPromotionItem.getProductSkuId());
                skuStock.setLockStock(skuStock.getLockStock() + cartPromotionItem.getQuantity());
                skuStockMapper.updateByPrimaryKeySelective(skuStock);
            }
        }
    }

    /**
     * 扣减指定门店库存并记录日志
     * @param orderItemList 订单商品列表
     * @param storeId 门店ID
     * @return 影响的行数
     */
    private int deductStoreStockWithLog(List<OmsOrderItem> orderItemList, Long storeId) {
        int totalCount = 0;
        
        for (OmsOrderItem item : orderItemList) {
            Long skuId = item.getProductSkuId();
            Long productId = item.getProductId();
            Integer quantity = item.getProductQuantity();
            String orderSn = item.getOrderSn();
            Long orderId = item.getOrderId();
            
            // 查询门店库存
            PmsStoreSkuStockExample example = new PmsStoreSkuStockExample();
            example.createCriteria()
                    .andStoreIdEqualTo(storeId)
                    .andSkuIdEqualTo(skuId);
            List<PmsStoreSkuStock> storeStockList = storeSkuStockMapper.selectByExample(example);
            
            if (!storeStockList.isEmpty()) {
                PmsStoreSkuStock storeStock = storeStockList.get(0);
                Integer beforeStock = storeStock.getStock() != null ? storeStock.getStock() : 0;
                Integer afterStock = beforeStock - quantity;
                
                // 扣减库存
                storeStock.setStock(afterStock);
                storeStock.setLockedStock((storeStock.getLockedStock() != null ? storeStock.getLockedStock() : 0) - quantity);
                storeStock.setSaleCount((storeStock.getSaleCount() != null ? storeStock.getSaleCount() : 0) + quantity);
                storeSkuStockMapper.updateByPrimaryKeySelective(storeStock);
                
                // 记录库存操作日志
                recordStoreStockOperation(storeId, orderId, orderSn, productId, skuId, 
                        quantity, beforeStock, afterStock, "小程序订单销售出库（指定门店）");
                
                LOGGER.info("扣减指定门店库存成功 - 门店ID: {}, SKU ID: {}, 扣减数量: {}, 剩余库存: {}", 
                        storeId, skuId, quantity, afterStock);
                totalCount++;
            } else {
                LOGGER.warn("门店库存记录不存在 - 门店ID: {}, SKU ID: {}", storeId, skuId);
            }
        }
        
        return totalCount;
    }

    /**
     * 智能扣减门店库存（支付成功后调用）
     * 规则：优先扣减库存最多的门店库存，门店库存不足则扣减地库库存
     * @param orderItemList 订单商品列表
     * @return 影响的行数
     */
    private int smartDeductStoreStock(List<OmsOrderItem> orderItemList) {
        int totalCount = 0;
        
        for (OmsOrderItem item : orderItemList) {
            Long skuId = item.getProductSkuId();
            Long productId = item.getProductId();
            Integer quantity = item.getProductQuantity();
            String orderSn = item.getOrderSn();
            Long orderId = item.getOrderId();
            
            LOGGER.info("智能扣减门店库存 - SKU ID: {}, 商品名称: {}, 数量: {}", skuId, item.getProductName(), quantity);
            
            // 1. 优先查找库存最多的门店（排除地库）
            Long maxStockStoreId = findMaxStockStoreId(skuId, quantity);
            
            if (maxStockStoreId != null) {
                // 扣减库存最多的门店库存
                PmsStoreSkuStockExample storeExample = new PmsStoreSkuStockExample();
                storeExample.createCriteria()
                        .andStoreIdEqualTo(maxStockStoreId)
                        .andSkuIdEqualTo(skuId);
                List<PmsStoreSkuStock> storeStockList = storeSkuStockMapper.selectByExample(storeExample);
                
                if (!storeStockList.isEmpty()) {
                    PmsStoreSkuStock storeStock = storeStockList.get(0);
                    Integer beforeStock = storeStock.getStock() != null ? storeStock.getStock() : 0;
                    Integer afterStock = beforeStock - quantity;
                    storeStock.setStock(afterStock);
                    storeStock.setSaleCount((storeStock.getSaleCount() != null ? storeStock.getSaleCount() : 0) + quantity);
                    storeSkuStockMapper.updateByPrimaryKeySelective(storeStock);
                    
                    // 记录库存操作日志
                    recordStoreStockOperation(maxStockStoreId, orderId, orderSn, productId, skuId, 
                            quantity, beforeStock, afterStock, "小程序订单销售出库（门店）");
                    
                    LOGGER.info("从库存最多的门店扣减库存成功 - 门店ID: {}, SKU ID: {}, 扣减数量: {}, 剩余库存: {}", 
                            maxStockStoreId, skuId, quantity, afterStock);
                    totalCount++;
                    continue;
                }
            } else {
                LOGGER.info("门店库存不足 - SKU ID: {}, 需要数量: {}", skuId, quantity);
            }
            
            // 2. 门店库存不足，查找地库（is_warehouse = true）
            Long warehouseStoreId = findWarehouseStoreId();
            
            if (warehouseStoreId != null) {
                // 检查地库库存是否充足
                PmsStoreSkuStockExample warehouseExample = new PmsStoreSkuStockExample();
                warehouseExample.createCriteria()
                        .andStoreIdEqualTo(warehouseStoreId)
                        .andSkuIdEqualTo(skuId);
                List<PmsStoreSkuStock> warehouseStockList = storeSkuStockMapper.selectByExample(warehouseExample);
                
                if (!warehouseStockList.isEmpty()) {
                    PmsStoreSkuStock warehouseStock = warehouseStockList.get(0);
                    Integer beforeStock = warehouseStock.getStock() != null ? warehouseStock.getStock() : 0;
                    
                    if (beforeStock >= quantity) {
                        // 地库库存充足，扣减地库库存
                        Integer afterStock = beforeStock - quantity;
                        warehouseStock.setStock(afterStock);
                        warehouseStock.setSaleCount((warehouseStock.getSaleCount() != null ? warehouseStock.getSaleCount() : 0) + quantity);
                        storeSkuStockMapper.updateByPrimaryKeySelective(warehouseStock);
                        
                        // 记录库存操作日志
                        recordStoreStockOperation(warehouseStoreId, orderId, orderSn, productId, skuId, 
                                quantity, beforeStock, afterStock, "小程序订单销售出库（地库-门店库存不足）");
                        
                        LOGGER.info("从地库扣减库存成功 - 地库ID: {}, SKU ID: {}, 扣减数量: {}, 剩余库存: {}", 
                                warehouseStoreId, skuId, quantity, afterStock);
                        totalCount++;
                        continue;
                    } else {
                        LOGGER.info("地库库存也不足 - 地库ID: {}, SKU ID: {}, 可用库存: {}, 需要: {}", 
                                warehouseStoreId, skuId, beforeStock, quantity);
                    }
                }
            }
            
            LOGGER.warn("未找到可扣减库存的门店或地库 - SKU ID: {}, 商品名称: {}, 需要数量: {}", 
                    skuId, item.getProductName(), quantity);
        }
        
        return totalCount;
    }

    /**
     * 记录门店库存操作日志
     */
    private void recordStoreStockOperation(Long storeId, Long orderId, String orderSn,
                                          Long productId, Long skuId, Integer quantity,
                                          Integer beforeStock, Integer afterStock, String reason) {
        try {
            // 获取商品和SKU信息
            PmsProduct product = productMapper.selectByPrimaryKey(productId);
            PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(skuId);

            // 生成操作单号
            String operationNo = "PORTAL_OUT_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) 
                    + "_" + System.currentTimeMillis() % 1000;

            PmsStockOperationLog log = new PmsStockOperationLog();
            log.setOperationNo(operationNo);
            log.setOperationType((byte) 2); // 2-出库
            log.setOperationSubtype((byte) 1); // 1-销售出库
            log.setProductId(productId);
            log.setProductName(product != null ? product.getName() : "");
            log.setProductSn(product != null ? product.getProductSn() : "");
            log.setSkuId(skuId);
            log.setSkuCode(skuStock != null ? skuStock.getSkuCode() : "");
            log.setStoreId(storeId);
            log.setOrderId(orderId); // 关联订单ID
            log.setOrderSn(orderSn); // 关联订单编号
            log.setBeforeStock(beforeStock);
            log.setOperationQuantity(-quantity); // 负数表示出库
            log.setAfterStock(afterStock);
            log.setOperationReason(reason);
            log.setOperatorId(0L); // 系统操作
            log.setOperatorName("系统");
            log.setCreatedAt(new Date());

            stockOperationLogMapper.insert(log);
            LOGGER.info("记录门店库存操作日志成功 - 操作单号: {}, 门店ID: {}, SKU ID: {}, 订单ID: {}", operationNo, storeId, skuId, orderId);

        } catch (Exception e) {
            // 记录日志失败不影响主流程
            LOGGER.error("记录门店库存操作日志失败 - 门店ID: {}, SKU ID: {}, 错误: {}", storeId, skuId, e.getMessage());
        }
    }

    /**
     * 查找地库门店ID（排除中央仓库）
     * @return 地库门店ID，如果没有则返回null
     */
    private Long findWarehouseStoreId() {
        OmsStoreAddressExample example = new OmsStoreAddressExample();
        // 查找地库（is_warehouse=true 且 store_type='WAREHOUSE'），排除中央仓库
        example.createCriteria()
                .andIsWarehouseEqualTo(true)
                .andStoreTypeEqualTo("WAREHOUSE");
        List<OmsStoreAddress> warehouseList = storeAddressMapper.selectByExample(example);
        
        if (!warehouseList.isEmpty()) {
            return warehouseList.get(0).getId();
        }
        return null;
    }

    /**
     * 查找指定SKU库存最多的门店ID（排除地库和中央仓库）
     * @param skuId SKU ID
     * @param requiredQuantity 需要的数量
     * @return 库存最多且库存充足的门店ID，如果没有则返回null
     */
    private Long findMaxStockStoreId(Long skuId, Integer requiredQuantity) {
        // 查询所有普通门店（排除地库和中央仓库）
        OmsStoreAddressExample storeExample = new OmsStoreAddressExample();
        storeExample.createCriteria()
                .andIsWarehouseEqualTo(false)
                .andStoreTypeEqualTo("STORE");  // 只查询普通门店，排除中央仓库
        List<OmsStoreAddress> storeList = storeAddressMapper.selectByExample(storeExample);
        
        if (storeList.isEmpty()) {
            return null;
        }
        
        List<Long> storeIds = storeList.stream().map(OmsStoreAddress::getId).collect(Collectors.toList());
        
        // 查询这些门店中该SKU的库存
        PmsStoreSkuStockExample stockExample = new PmsStoreSkuStockExample();
        stockExample.createCriteria()
                .andStoreIdIn(storeIds)
                .andSkuIdEqualTo(skuId)
                .andStockGreaterThanOrEqualTo(requiredQuantity);
        stockExample.setOrderByClause("stock DESC");
        
        List<PmsStoreSkuStock> stockList = storeSkuStockMapper.selectByExample(stockExample);
        
        if (!stockList.isEmpty()) {
            return stockList.get(0).getStoreId();
        }
        
        return null;
    }

    /**
     * 判断下单商品是否都有库存
     */
    private boolean hasStock(List<CartPromotionItem> cartPromotionItemList) {
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            if (cartPromotionItem.getRealStock()==null //判断真实库存是否为空
                    ||cartPromotionItem.getRealStock() <= 0 //判断真实库存是否小于0
                    || cartPromotionItem.getRealStock() < cartPromotionItem.getQuantity()) //判断真实库存是否小于下单的数量
            {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断下单商品是否都有库存（支持门店库存）
     */
    private boolean hasStock(List<CartPromotionItem> cartPromotionItemList, Long storeId) {
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            Integer realStock = cartPromotionItem.getRealStock();
            
            if (storeId != null) {
                // 门店自取订单：检查门店库存
                PmsStoreSkuStockExample example = new PmsStoreSkuStockExample();
                example.createCriteria().andStoreIdEqualTo(storeId).andSkuIdEqualTo(cartPromotionItem.getProductSkuId());
                List<PmsStoreSkuStock> storeSkuStockList = storeSkuStockMapper.selectByExample(example);
                if (!storeSkuStockList.isEmpty()) {
                    PmsStoreSkuStock storeSkuStock = storeSkuStockList.get(0);
                    realStock = storeSkuStock.getStock() - storeSkuStock.getLockedStock();
                }
            }
            
            if (realStock == null || realStock <= 0 || realStock < cartPromotionItem.getQuantity()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 计算购物车中商品的价格（不包含运费计算）
     */
    private ConfirmOrderResult.CalcAmount calcCartAmount(List<CartPromotionItem> cartPromotionItemList) {
        return calcCartAmount(cartPromotionItemList, null);
    }
    
    /**
     * 计算购物车中商品的价格（包含运费计算）
     */
    private ConfirmOrderResult.CalcAmount calcCartAmount(List<CartPromotionItem> cartPromotionItemList, UmsMemberReceiveAddress receiveAddress) {
        ConfirmOrderResult.CalcAmount calcAmount = new ConfirmOrderResult.CalcAmount();
        
        // 计算商品总金额和促销优惠
        BigDecimal totalAmount = new BigDecimal("0");
        BigDecimal promotionAmount = new BigDecimal("0");
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            totalAmount = totalAmount.add(cartPromotionItem.getPrice().multiply(new BigDecimal(cartPromotionItem.getQuantity())));
            promotionAmount = promotionAmount.add(cartPromotionItem.getReduceAmount().multiply(new BigDecimal(cartPromotionItem.getQuantity())));
        }
        calcAmount.setTotalAmount(totalAmount);
        calcAmount.setPromotionAmount(promotionAmount);
        
        // 计算运费
        BigDecimal freightAmount = calculateFreightAmount(cartPromotionItemList, receiveAddress);
        calcAmount.setFreightAmount(freightAmount);
        
        // 计算应付金额
        calcAmount.setPayAmount(totalAmount.subtract(promotionAmount).add(freightAmount));
        
        return calcAmount;
    }
    
    /**
     * 计算运费 - 将整个购物车的商品合并计算，满足包邮条件则整单包邮
     */
    private BigDecimal calculateFreightAmount(List<CartPromotionItem> cartPromotionItemList, UmsMemberReceiveAddress receiveAddress) {
        if (CollectionUtils.isEmpty(cartPromotionItemList)) {
            return new BigDecimal("0");
        }
        
        try {
            // 构建包含所有商品的运费计算参数（整个购物车合并计算）
            SmsFreightCalculateParam calculateParam = buildFreightCalculateParam(cartPromotionItemList, receiveAddress);
            
            // 调用运费计算服务
            SmsFreightCalculateResult result = freightCalculateService.calculateFreight(calculateParam);
            
            if (result != null) {
                if (result.getIsFreeShipping() != null && result.getIsFreeShipping()) {
                    LOGGER.info("订单满足包邮条件，原因: {}，最终运费: 0", result.getFreeShippingReason());
                    return new BigDecimal("0");
                }
                
                if (result.getTotalFreight() != null) {
                    LOGGER.info("订单运费计算完成，运费: {}", result.getTotalFreight());
                    return result.getTotalFreight();
                }
            }
            
            // 如果计算结果为空，使用默认运费
            LOGGER.warn("运费计算结果为空，使用默认运费");
            return new BigDecimal("10.00");
            
        } catch (Exception e) {
            LOGGER.error("运费计算失败", e);
            // 计算失败时返回默认运费
            return new BigDecimal("10.00");
        }
    }
    
    /**
     * 构建运费计算参数 - 多个商品
     */
    private SmsFreightCalculateParam buildFreightCalculateParam(List<CartPromotionItem> cartPromotionItemList, UmsMemberReceiveAddress receiveAddress) {
        SmsFreightCalculateParam calculateParam = new SmsFreightCalculateParam();
        
        // 设置收货地址信息
        if (receiveAddress != null) {
            calculateParam.setReceiverProvince(receiveAddress.getProvince());
            calculateParam.setReceiverCity(receiveAddress.getCity());
            calculateParam.setReceiverRegion(receiveAddress.getRegion());
        } else {
            // 如果没有收货地址，使用默认值
            calculateParam.setReceiverProvince("广东省");
            calculateParam.setReceiverCity("广州市");
            calculateParam.setReceiverRegion("天河区");
        }
        
        // 设置商品列表
        List<SmsFreightCalculateParam.FreightCalculateItemParam> items = new ArrayList<>();
        for (CartPromotionItem cartItem : cartPromotionItemList) {
            SmsFreightCalculateParam.FreightCalculateItemParam item = new SmsFreightCalculateParam.FreightCalculateItemParam();
            item.setProductId(cartItem.getProductId());
            item.setSkuId(cartItem.getProductSkuId());
            item.setQuantity(cartItem.getQuantity());
            item.setPrice(cartItem.getPrice());
            
            // 从数据库获取商品的实际重量和运费模板ID
            PortalProductDao.ProductFreightInfo freightInfo = portalProductDao.getProductFreightInfo(cartItem.getProductId());
            if (freightInfo != null) {
                // 商品重量（克转换为千克）
                BigDecimal weightInKg = freightInfo.getWeight() != null ? 
                    freightInfo.getWeight().divide(new BigDecimal("1000"), 3, RoundingMode.HALF_UP) : 
                    new BigDecimal("1.0"); // 默认1kg
                item.setWeight(weightInKg);
                item.setFreightTemplateId(freightInfo.getFreightTemplateId());
            } else {
                // 如果查询不到商品信息，使用默认值
                item.setWeight(new BigDecimal("1.0")); // 默认1kg
                item.setFreightTemplateId(null);
            }
            
            // 设置商品体积（暂时使用默认值，按照重量估算：1kg ≈ 0.001m³）
            BigDecimal volume = item.getWeight() != null ? 
                item.getWeight().multiply(new BigDecimal("0.001")) : 
                new BigDecimal("0.001");
            item.setVolume(volume);
            
            items.add(item);
        }
        calculateParam.setItems(items);
        
        return calculateParam;
    }
    
    /**
     * 构建运费计算参数 - 单个商品
     */
    private SmsFreightCalculateParam buildFreightCalculateParamForSingleItem(CartPromotionItem cartItem, UmsMemberReceiveAddress receiveAddress) {
        SmsFreightCalculateParam calculateParam = new SmsFreightCalculateParam();
        
        // 设置收货地址信息
        if (receiveAddress != null) {
            calculateParam.setReceiverProvince(receiveAddress.getProvince());
            calculateParam.setReceiverCity(receiveAddress.getCity());
            calculateParam.setReceiverRegion(receiveAddress.getRegion());
        } else {
            // 如果没有收货地址，使用默认值
            calculateParam.setReceiverProvince("广东省");
            calculateParam.setReceiverCity("广州市");
            calculateParam.setReceiverRegion("天河区");
        }
        
        // 设置单个商品信息
        List<SmsFreightCalculateParam.FreightCalculateItemParam> items = new ArrayList<>();
        SmsFreightCalculateParam.FreightCalculateItemParam item = new SmsFreightCalculateParam.FreightCalculateItemParam();
        item.setProductId(cartItem.getProductId());
        item.setSkuId(cartItem.getProductSkuId());
        item.setQuantity(cartItem.getQuantity());
        item.setPrice(cartItem.getPrice());
        
        // 从数据库获取商品的实际重量和运费模板ID
        PortalProductDao.ProductFreightInfo freightInfo = portalProductDao.getProductFreightInfo(cartItem.getProductId());
        if (freightInfo != null) {
            // 商品重量（克转换为千克）
            BigDecimal weightInKg = freightInfo.getWeight() != null ? 
                freightInfo.getWeight().divide(new BigDecimal("1000"), 3, RoundingMode.HALF_UP) : 
                new BigDecimal("1.0"); // 默认1kg
            item.setWeight(weightInKg);
            item.setFreightTemplateId(freightInfo.getFreightTemplateId());
        } else {
            // 如果查询不到商品信息，使用默认值
            item.setWeight(new BigDecimal("1.0")); // 默认1kg
            item.setFreightTemplateId(null);
        }
        
        // 设置商品体积（暂时使用默认值，按照重量估算：1kg ≈ 0.001m³）
        BigDecimal volume = item.getWeight() != null ? 
            item.getWeight().multiply(new BigDecimal("0.001")) : 
            new BigDecimal("0.001");
        item.setVolume(volume);
        
        items.add(item);
        calculateParam.setItems(items);
        
        return calculateParam;
    }
    

    
    /**
     * 获取默认收货地址
     */
    private UmsMemberReceiveAddress getDefaultReceiveAddress(List<UmsMemberReceiveAddress> addressList) {
        if (CollectionUtils.isEmpty(addressList)) {
            return null;
        }
        
        // 先查找默认地址
        for (UmsMemberReceiveAddress address : addressList) {
            if (address.getDefaultStatus() != null && address.getDefaultStatus() == 1) {
                return address;
            }
        }
        
        // 如果没有默认地址，返回第一个地址
        return addressList.get(0);
    }

    @Override
    public void sendDelayMessageCheckPayStatus(String orderSn) {
        // 获取订单设置，查询时间设为5分钟
        long delayTimes = 5 * 60 * 1000;
        LOGGER.info("发送延迟消息查询订单:{}支付状态，延迟：{}ms", orderSn, delayTimes);
        // 发送延迟消息
        payCheckSender.sendDelayMessageCheckPayStatus(orderSn, delayTimes);
    }

    @Override
    public void checkPayStatus(String orderSn) {
        LOGGER.info("检查订单:{}的支付状态", orderSn);
        // 查询订单信息
        OmsOrderExample example = new OmsOrderExample();
        example.createCriteria()
                .andOrderSnEqualTo(orderSn)
                .andStatusEqualTo(0)
                .andDeleteStatusEqualTo(0);
        List<OmsOrder> orderList = orderMapper.selectByExample(example);
        
        if (CollUtil.isEmpty(orderList)) {
            LOGGER.info("订单:{}不存在或已支付", orderSn);
            return;
        }
        
        try {
            // 调用微信支付接口查询订单支付状态
            WxPayOrderQueryResult queryResult = wxPayBusiness.queryOrder(null, orderSn);
            
            // 判断是否支付成功
            if ("SUCCESS".equals(queryResult.getResultCode()) && "SUCCESS".equals(queryResult.getTradeState())) {
                // 支付成功，更新订单状态
                LOGGER.info("订单:{}已支付成功，更新订单状态", orderSn);
                paySuccessByOrderSn(orderSn, 2); // 2表示微信支付
            } else {
                LOGGER.info("订单:{}支付状态为:{}, 不做处理", orderSn, queryResult.getTradeState());
            }
        } catch (WxPayException e) {
            LOGGER.error("查询订单:{}支付状态出错: {}", orderSn, e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Map<String, Object> generateGiftOrder(GiftOrderParam giftOrderParam) {
        UmsMember currentMember = memberService.getCurrentMember();
        
        // 创建订单参数
        OrderParam orderParam = new OrderParam();
        // 设置支付方式
        orderParam.setPayType(giftOrderParam.getPayType());
        // 设置为送礼订单
        orderParam.setIsGift(1);
        // 设置送礼祝福语
        orderParam.setGiftMessage(giftOrderParam.getGiftMessage());
        // 设置配送方式为快递配送
        orderParam.setDeliveryType(0);
        // 设置购物商品列表
        orderParam.setCartPromotionItemList(getCartPromotionItemsFromDirectBuy(giftOrderParam.getDirectBuyParamList()));
        
        // 生成订单
        Map<String, Object> result = generateOrder(orderParam);
        
        return result;
    }

    /**
     * 将DirectBuyParam列表转换为CartPromotionItem列表（使用统一的营销计算服务）
     */
    private List<CartPromotionItem> getCartPromotionItemsFromDirectBuy(List<DirectBuyParam> directBuyParamList) {
        List<CartPromotionItem> cartPromotionItemList = new ArrayList<>();

        for (DirectBuyParam directBuyParam : directBuyParamList) {
            // 获取商品促销信息
            PromotionProduct promotionProduct = portalProductDao.getPromotionProduct(directBuyParam.getProductId());
            if (promotionProduct != null) {
                // 使用统一的营销计算服务
                List<CartPromotionItem> itemList = getCartPromotionItems(directBuyParam, promotionProduct);
                cartPromotionItemList.addAll(itemList);
            } else {
                // 如果没有促销信息，创建基础的CartPromotionItem
                CartPromotionItem cartPromotionItem = createBasicCartPromotionItem(directBuyParam);
                cartPromotionItemList.add(cartPromotionItem);
            }
        }

        return cartPromotionItemList;
    }

    /**
     * 创建基础的CartPromotionItem（无促销信息时使用）
     */
    private CartPromotionItem createBasicCartPromotionItem(DirectBuyParam directBuyParam) {
        CartPromotionItem cartPromotionItem = new CartPromotionItem();
        cartPromotionItem.setProductId(directBuyParam.getProductId());
        cartPromotionItem.setProductSkuId(directBuyParam.getProductSkuId());
        cartPromotionItem.setQuantity(directBuyParam.getQuantity());
        cartPromotionItem.setPrice(directBuyParam.getPrice());
        cartPromotionItem.setProductAttr(directBuyParam.getProductAttr());
        cartPromotionItem.setProductBrand(directBuyParam.getProductBrand());
        cartPromotionItem.setProductCategoryId(directBuyParam.getProductCategoryId());
        cartPromotionItem.setProductName(directBuyParam.getProductName());
        cartPromotionItem.setProductPic(directBuyParam.getProductPic());
        cartPromotionItem.setProductSkuCode(directBuyParam.getProductSkuCode());
        cartPromotionItem.setProductSn(directBuyParam.getProductSn());
        cartPromotionItem.setProductSubTitle(directBuyParam.getProductSubTitle());
        cartPromotionItem.setDeliveryType(directBuyParam.getDeliveryType());

        // 无优惠
        cartPromotionItem.setReduceAmount(new BigDecimal("0"));
        cartPromotionItem.setPromotionMessage("无优惠");

        // 从商品信息中获取积分和成长值奖励
        try {
            PmsProduct product = productMapper.selectByPrimaryKey(directBuyParam.getProductId());
            if (product != null) {
                cartPromotionItem.setIntegration(product.getGiftPoint() != null ? product.getGiftPoint() : 0);
                cartPromotionItem.setGrowth(product.getGiftGrowth() != null ? product.getGiftGrowth() : 0);
            } else {
                cartPromotionItem.setIntegration(0);
                cartPromotionItem.setGrowth(0);
            }
        } catch (Exception e) {
            LOGGER.error("获取商品积分奖励信息失败，商品ID: {}, 错误: {}", directBuyParam.getProductId(), e.getMessage(), e);
            cartPromotionItem.setIntegration(0);
            cartPromotionItem.setGrowth(0);
        }

        // 查询商品库存信息
        PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(directBuyParam.getProductSkuId());
        if (skuStock != null) {
            cartPromotionItem.setRealStock(skuStock.getStock() - skuStock.getLockStock());
        } else {
            cartPromotionItem.setRealStock(0);
        }

        return cartPromotionItem;
    }

    @Override
    @Transactional
    public Map<String, Object> pickupOrder(String pickupCode, String operator) {
        // 验证核销码格式
        if (!pickupCodeUtil.isValidFormat(pickupCode)) {
            throw new RuntimeException("核销码格式不正确");
        }

        // 检查核销码是否存在
        if (!pickupCodeUtil.isValidCode(pickupCode)) {
            throw new RuntimeException("核销码不存在或已失效");
        }

        // 检查核销码是否已使用
        if (pickupCodeUtil.isCodeUsed(pickupCode)) {
            throw new RuntimeException("核销码已使用");
        }

        // 获取订单ID
        Long orderId = pickupCodeUtil.getOrderId(pickupCode);
        if (orderId == null) {
            throw new RuntimeException("无法找到对应的订单");
        }

        // 查询订单详情
        OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 验证订单状态
        if (order.getStatus() == null || order.getStatus() != 1) {
            throw new RuntimeException("订单状态不正确，只有已付款订单才能核销");
        }

        // 验证是否为自提订单
        if (order.getDeliveryType() == null || order.getDeliveryType() != 1) {
            throw new RuntimeException("该订单不是自提订单");
        }

        // 检查是否已核销
        if (order.getPickupStatus() != null && order.getPickupStatus() == 1) {
            throw new RuntimeException("订单已核销");
        }

        // 更新订单核销状态
        OmsOrder updateOrder = new OmsOrder();
        updateOrder.setId(orderId);
        updateOrder.setPickupStatus((byte) 1); // 已核销
        updateOrder.setPickupTime(new Date());
        updateOrder.setPickupOperator(operator);
        updateOrder.setStatus(3); // 已完成

        int updateResult = orderMapper.updateByPrimaryKeySelective(updateOrder);
        if (updateResult <= 0) {
            throw new RuntimeException("更新订单状态失败");
        }

        // 标记核销码为已使用
        boolean markResult = pickupCodeUtil.markCodeAsUsed(pickupCode, operator);
        if (!markResult) {
            LOGGER.warn("标记核销码为已使用失败，但订单状态已更新，订单ID:{}, 核销码:{}", orderId, pickupCode);
        }

        // 自提订单核销完成后发放分销佣金（更新状态为已结算）
        try {
            if (commissionService instanceof CommissionServiceImpl) {
                ((CommissionServiceImpl) commissionService).settleCommissionOnOrderComplete(orderId);
                LOGGER.info("自提订单核销完成后佣金发放完成，订单ID: {}", orderId);
            }
        } catch (Exception e) {
            LOGGER.error("自提订单核销完成后发放佣金失败，订单ID: {}, 错误: {}", orderId, e.getMessage(), e);
            // 佣金发放失败不影响订单核销
        }

        LOGGER.info("订单核销成功，订单ID:{}, 核销码:{}, 操作员:{}", orderId, pickupCode, operator);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("orderSn", order.getOrderSn());
        result.put("pickupCode", pickupCode);
        result.put("operator", operator);
        result.put("pickupTime", updateOrder.getPickupTime());

        return result;
    }

    @Override
    public String getPickupQRCode(String pickupCode) {
        // 验证核销码格式
        if (!pickupCodeUtil.isValidFormat(pickupCode)) {
            throw new RuntimeException("核销码格式不正确");
        }

        // 检查核销码是否存在
        if (!pickupCodeUtil.isValidCode(pickupCode)) {
            throw new RuntimeException("核销码不存在或已失效");
        }

        try {
            // 生成QR码
            String qrCodeBase64 = qrCodeService.generatePickupCodeQR(pickupCode);
            return qrCodeBase64;
        } catch (Exception e) {
            LOGGER.error("生成核销码二维码失败，核销码:{}", pickupCode, e);
            throw new RuntimeException("生成二维码失败");
        }
    }
    
    /**
     * 为订单计算并分发分销佣金
     */
    private void calculateAndDistributeCommissionForOrder(OmsOrderDetail orderDetail) {
        try {
            LOGGER.info("开始处理分销佣金，订单ID: {}, 客户ID: {}, 订单金额: {}", 
                       orderDetail.getId(), orderDetail.getMemberId(), orderDetail.getPayAmount());
            
            // 检查是否为送礼订单或特殊订单，如果是则不计算佣金
            if (Boolean.TRUE.equals(orderDetail.getIsGift())) {
                LOGGER.info("送礼订单不计算分销佣金，订单ID: {}", orderDetail.getId());
                return;
            }
            
            // 调用佣金计算方法（这里需要根据分销关系来确定分销商ID和层级）
            // 暂时用订单处理逻辑的占位符，实际实现时需要查询分销关系
            Long customerId = orderDetail.getMemberId();
            // TODO: 查询分销关系获取分销商ID和层级，这里暂时跳过
            // commissionService.calculateAndGrantCommission(orderDetail.getId(), customerId, distributorId, level);
            
            LOGGER.info("订单佣金处理逻辑已触发（待完整实现），订单ID: {}", orderDetail.getId());

        } catch (Exception e) {
            LOGGER.error("处理分销佣金异常，订单ID: {}", orderDetail.getId(), e);
            throw e; // 重新抛出异常以便上层捕获和处理
        }
    }

    /**
     * 处理积分奖励逻辑
     * 支付成功后根据订单商品的积分奖励规则计算用户应获得的积分
     * 积分计算优先级：
     * 1. 首先检查订单项的giftIntegration字段，如果有值则使用该值乘以购买数量
     * 2. 如果giftIntegration为空或0，则按照商品真实支付金额(realAmount)乘以10作为基础积分，最少1积分
     * 3. 获得基础积分后，应用营销活动倍数
     */
    private void processIntegrationReward(OmsOrderDetail orderDetail) {
        if (orderDetail == null || orderDetail.getOrderItemList() == null || orderDetail.getOrderItemList().isEmpty()) {
            LOGGER.warn("订单详情或订单项为空，跳过积分奖励处理");
            return;
        }

        Long memberId = orderDetail.getMemberId();
        if (memberId == null) {
            LOGGER.warn("订单用户ID为空，跳过积分奖励处理");
            return;
        }

        // 获取用户当前积分
        UmsMember member = memberService.getById(memberId);
        if (member == null) {
            LOGGER.warn("用户不存在，用户ID: {}", memberId);
            return;
        }

        Integer currentIntegration = member.getIntegration() == null ? 0 : member.getIntegration();
        int totalRewardIntegration = 0;

        // 获取当前适用的营销活动
        SmsIntegrationPromotion applicablePromotion = getApplicablePromotion(orderDetail);
        BigDecimal multiplier = BigDecimal.ONE; // 默认倍数为1
        String promotionInfo = "";

        if (applicablePromotion != null) {
            multiplier = applicablePromotion.getMultiplier();
            promotionInfo = "（营销活动：" + applicablePromotion.getName() + " " + multiplier + "倍）";
            LOGGER.info("订单适用积分营销活动 - 订单ID: {}, 活动: {}, 倍数: {}",
                orderDetail.getId(), applicablePromotion.getName(), multiplier);
        }

        // 为每个商品分别计算和记录积分奖励
        for (OmsOrderItem orderItem : orderDetail.getOrderItemList()) {
            // 数据验证
            if (orderItem.getProductQuantity() == null || orderItem.getProductQuantity() <= 0) {
                LOGGER.warn("订单项数量无效，跳过积分计算 - 商品ID: {}, 数量: {}",
                    orderItem.getProductId(), orderItem.getProductQuantity());
                continue;
            }

            int baseIntegration = 0;
            String calculationMethod = "";

            // 积分计算优先级处理
            if (orderItem.getGiftIntegration() != null && orderItem.getGiftIntegration() > 0) {
                // 使用设置的积分值
                baseIntegration = orderItem.getGiftIntegration() * orderItem.getProductQuantity();
                calculationMethod = "设置积分值";
                LOGGER.info("使用设置的积分值计算 - 商品: {}, 单个积分: {}, 数量: {}, 基础积分: {}",
                    orderItem.getProductName(), orderItem.getGiftIntegration(),
                    orderItem.getProductQuantity(), baseIntegration);
            } else {
                // 使用真实支付金额计算积分：realAmount * 10，最少1积分
                if (orderItem.getRealAmount() == null || orderItem.getRealAmount().compareTo(BigDecimal.ZERO) <= 0) {
                    LOGGER.warn("订单项真实支付金额无效，跳过积分计算 - 商品ID: {}, 真实金额: {}",
                        orderItem.getProductId(), orderItem.getRealAmount());
                    continue;
                }

                BigDecimal realAmountBasedIntegration = orderItem.getRealAmount()
                    .multiply(BigDecimal.TEN);
                baseIntegration = realAmountBasedIntegration.setScale(0, RoundingMode.UP).intValue();

                // 确保至少获得1个积分（只要真实金额大于0）
                if (baseIntegration <= 0 && orderItem.getRealAmount().compareTo(BigDecimal.ZERO) > 0) {
                    baseIntegration = 1;
                }

                calculationMethod = "真实金额计算";
                LOGGER.info("使用真实支付金额计算积分 - 商品: {}, 真实金额: {}, 基础积分: {}",
                    orderItem.getProductName(), orderItem.getRealAmount(), baseIntegration);
            }

            // 如果基础积分为0，跳过后续处理
            if (baseIntegration <= 0) {
                LOGGER.info("商品基础积分为0，跳过 - 商品: {}", orderItem.getProductName());
                continue;
            }

            // 应用营销活动倍数
            int finalIntegration = new BigDecimal(baseIntegration)
                .multiply(multiplier)
                .setScale(0, RoundingMode.DOWN)
                .intValue();

            totalRewardIntegration += finalIntegration;

            // 为每个商品创建积分获得记录
            UmsIntegrationChangeHistory history = new UmsIntegrationChangeHistory();
            history.setMemberId(memberId);
            history.setCreateTime(new Date());
            history.setChangeType(0); // 0->增加；1->减少
            history.setChangeCount(finalIntegration);
            history.setOperateMan("系统");
            history.setOperateNote("购买商品获得积分" + promotionInfo);
            history.setSourceType(0); // 0->购物；1->管理员修改
            integrationChangeHistoryMapper.insert(history);

            LOGGER.info("商品积分奖励记录创建成功 - 用户ID: {}, 商品: {}, 计算方式: {}, 基础积分: {}, 最终积分: {}",
                memberId, orderItem.getProductName(), calculationMethod, baseIntegration, finalIntegration);

            // 如果有营销活动，记录参与记录
            if (applicablePromotion != null) {
                recordPromotionParticipation(applicablePromotion, orderDetail, baseIntegration, finalIntegration - baseIntegration, finalIntegration);
            }
        }

        // 如果有积分奖励，更新用户总积分
        if (totalRewardIntegration > 0) {
            memberService.updateIntegration(memberId, currentIntegration + totalRewardIntegration);
            LOGGER.info("用户积分更新成功 - 用户ID: {}, 原积分: {}, 奖励积分: {}, 新积分: {}",
                memberId, currentIntegration, totalRewardIntegration, currentIntegration + totalRewardIntegration);
        } else {
            LOGGER.info("订单无积分奖励 - 订单ID: {}", orderDetail.getId());
        }
    }

    /**
     * 获取适用的积分营销活动
     */
    private SmsIntegrationPromotion getApplicablePromotion(OmsOrderDetail orderDetail) {
        try {
            // 检查订单支付金额是否为空
            if (orderDetail.getPayAmount() == null) {
                LOGGER.warn("订单支付金额为空，无法获取适用的积分营销活动，订单ID: {}", orderDetail.getId());
                return null;
            }

            Date now = new Date();

            // 查询当前生效的营销活动
            SmsIntegrationPromotionExample example = new SmsIntegrationPromotionExample();
            example.createCriteria()
                    .andStatusEqualTo(true)
                    .andStartTimeLessThanOrEqualTo(now)
                    .andEndTimeGreaterThanOrEqualTo(now);
            example.setOrderByClause("priority desc, create_time desc");

            List<SmsIntegrationPromotion> activePromotions = integrationPromotionMapper.selectByExample(example);

            for (SmsIntegrationPromotion promotion : activePromotions) {
                // 检查订单金额限制
                if (promotion.getMinOrderAmount() != null &&
                    orderDetail.getPayAmount().compareTo(promotion.getMinOrderAmount()) < 0) {
                    continue;
                }

                // 检查适用范围
                if (isPromotionApplicableToOrder(promotion, orderDetail)) {
                    return promotion;
                }
            }

        } catch (Exception e) {
            LOGGER.error("获取适用的积分营销活动失败", e);
        }

        return null;
    }

    /**
     * 检查营销活动是否适用于当前订单
     */
    private boolean isPromotionApplicableToOrder(SmsIntegrationPromotion promotion, OmsOrderDetail orderDetail) {
        Integer applicableType = promotion.getApplicableType();

        // 全部商品适用 (0表示全部商品)
        if (applicableType == null || applicableType == 0) {
            return true;
        }

        String applicableIds = promotion.getApplicableIds();
        if (applicableIds == null || applicableIds.trim().isEmpty()) {
            return applicableType == 0;
        }

        List<String> idList = Arrays.asList(applicableIds.split(","));

        // 检查订单中的商品是否符合条件
        for (OmsOrderItem orderItem : orderDetail.getOrderItemList()) {
            if (applicableType == 1) {
                // 指定分类：检查商品分类ID是否匹配
                // 这里需要根据实际的商品分类字段来判断
                // 暂时使用商品ID作为示例，实际应该检查分类ID
                if (idList.contains(orderItem.getProductCategoryId().toString())) {
                    return true;
                }
            } else if (applicableType == 2) {
                // 指定商品：检查商品ID是否匹配
                if (idList.contains(orderItem.getProductId().toString())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 记录用户参与营销活动的记录
     */
    private void recordPromotionParticipation(SmsIntegrationPromotion promotion, OmsOrderDetail orderDetail,
                                            int originalIntegration, int bonusIntegration, int totalIntegration) {
        try {
            // 创建参与记录
            SmsIntegrationPromotionMember member = new SmsIntegrationPromotionMember();
            member.setPromotionId(promotion.getId());
            member.setMemberId(orderDetail.getMemberId());
            member.setOrderId(orderDetail.getId());
            member.setOrderSn(orderDetail.getOrderSn());
            member.setOriginalIntegration(originalIntegration);
            member.setBonusIntegration(bonusIntegration);
            member.setTotalIntegration(totalIntegration);
            member.setCreateTime(new Date());

            // 保存参与记录
            integrationPromotionMemberMapper.insertSelective(member);

            LOGGER.info("营销活动参与记录保存成功 - 活动ID: {}, 用户ID: {}, 订单ID: {}, 原始积分: {}, 额外积分: {}, 总积分: {}",
                promotion.getId(), orderDetail.getMemberId(), orderDetail.getId(),
                originalIntegration, bonusIntegration, totalIntegration);

        } catch (Exception e) {
            LOGGER.error("记录营销活动参与失败", e);
        }
    }

    /**
     * 根据售后申请信息确定整体售后状态
     */
    private Integer determineAfterSaleStatus(OmsOrderReturnApply returnApply) {
        if (returnApply == null) {
            return 0; // 无售后
        }

        Integer status = returnApply.getStatus();
        Byte refundProcessStatus = returnApply.getRefundProcessStatus();

        if (status == null) {
            return 1; // 售后申请中
        }

        switch (status) {
            case 0:
                return 1; // 售后申请中（待处理）
            case 1:
                // 退货中状态，需要根据退款处理状态细分
                if (refundProcessStatus != null && refundProcessStatus == 1) {
                    return 2; // 售后处理中（退款中）
                }
                return 2; // 售后处理中（退货中）
            case 2:
                return 3; // 售后完成
            case 3:
                return 4; // 售后拒绝
            default:
                return 1; // 默认为申请中
        }
    }

    /**
     * 获取售后状态描述
     */
    private String getAfterSaleStatusDescription(OmsOrderReturnApply returnApply) {
        if (returnApply == null) {
            return "无售后申请";
        }

        Integer status = returnApply.getStatus();
        Byte refundProcessStatus = returnApply.getRefundProcessStatus();
        Byte returnType = returnApply.getReturnType();

        if (status == null) {
            return "售后申请中";
        }

        String typeDesc = (returnType != null && returnType == 1) ? "退款" : "退货退款";

        switch (status) {
            case 0:
                return "等待商家处理" + typeDesc + "申请";
            case 1:
                if (refundProcessStatus != null) {
                    switch (refundProcessStatus) {
                        case 0:
                            return returnType != null && returnType == 1 ? "商家同意退款，处理中" : "请寄回商品";
                        case 1:
                            return "退款处理中";
                        case 2:
                            return "退款成功";
                        case 3:
                            return "退款失败，请联系客服";
                        default:
                            return returnType != null && returnType == 1 ? "退款处理中" : "请寄回商品";
                    }
                }
                return returnType != null && returnType == 1 ? "退款处理中" : "请寄回商品";
            case 2:
                return typeDesc + "已完成";
            case 3:
                return typeDesc + "申请被拒绝";
            default:
                return "售后申请中";
        }
    }

    /**
     * 发送下单成功订阅消息通知
     */
    private void sendOrderSuccessNotification(OmsOrderDetail orderDetail, String pickupCode) {
        if (wxMessageUtil == null || orderSuccessTemplateId == null || orderSuccessTemplateId.isEmpty()) {
            LOGGER.info("微信消息服务或模板ID未配置，跳过下单成功通知发送");
            return;
        }

        try {
            // 获取用户信息
            UmsMember member = memberService.getById(orderDetail.getMemberId());
            if (member == null || member.getOpenid() == null || member.getOpenid().isEmpty()) {
                LOGGER.warn("用户信息或openid不存在，无法发送下单成功通知，订单ID: {}", orderDetail.getId());
                return;
            }

            // 构建模板数据
            Map<String, String> templateData = new HashMap<>();

            // 店铺名称 (thing5)
            templateData.put("thing5", "光恒州商城");

            // 订单编号 (character_string6)
            templateData.put("character_string6", orderDetail.getOrderSn());

            // 取单号 (character_string7) - 自提订单显示取货码，快递订单显示订单号
            if (orderDetail.getDeliveryType() != null && orderDetail.getDeliveryType() == 1 && pickupCode != null) {
                templateData.put("character_string7", pickupCode);
            } else {
                templateData.put("character_string7", orderDetail.getOrderSn());
            }

            // 构建小程序跳转路径
            String miniProgramPath = "/pages/order/orderDetail?id=" + orderDetail.getId();

            // 发送订阅消息
            boolean success = wxMessageUtil.sendSubscribeMessage(
                member.getOpenid(),
                orderSuccessTemplateId,
                templateData,
                miniProgramPath
            );

            if (success) {
                LOGGER.info("下单成功通知发送成功，订单ID: {}, 用户openid: {}", orderDetail.getId(), member.getOpenid());
            } else {
                LOGGER.warn("下单成功通知发送失败，订单ID: {}, 用户openid: {}", orderDetail.getId(), member.getOpenid());
            }

        } catch (Exception e) {
            LOGGER.error("发送下单成功通知异常，订单ID: {}", orderDetail.getId(), e);
        }
    }

    /**
     * 发送新订单通知给管理员和门店店长
     * 通过Feign调用mall-admin的通知服务
     */
    private void sendNewOrderNotificationToAdmin(OmsOrderDetail orderDetail) {
        if (adminNotificationFeignService == null) {
            LOGGER.debug("管理员通知服务未配置，跳过新订单通知发送");
            return;
        }

        try {
            // 构建通知参数
            com.macro.mall.portal.dto.NewOrderNotificationParam param = new com.macro.mall.portal.dto.NewOrderNotificationParam();
            param.setOrderSn(orderDetail.getOrderSn());
            param.setOrderAmount(orderDetail.getPayAmount());
            param.setOrderTime(orderDetail.getCreateTime());
            param.setStoreId(orderDetail.getStoreId());
            
            // 确定订单类型
            String orderType = getOrderTypeDescription(orderDetail);
            param.setOrderType(orderType);
            
            // 获取商品名称（取第一个商品名称，如果有多个则加"等"）
            String productName = getOrderProductName(orderDetail);
            param.setProductName(productName);
            
            // 异步调用通知服务
            adminNotificationFeignService.notifyNewOrder(param);
            LOGGER.info("新订单通知请求已发送，订单号: {}", orderDetail.getOrderSn());
            
        } catch (Exception e) {
            LOGGER.error("发送新订单通知失败，订单ID: {}", orderDetail.getId(), e);
        }
    }
    
    /**
     * 获取订单类型描述
     */
    private String getOrderTypeDescription(OmsOrderDetail orderDetail) {
        // 根据订单特征判断类型
        if (orderDetail.getOrderSn() != null && orderDetail.getOrderSn().startsWith("DIY")) {
            return "DIY定制订单";
        }
        if (Boolean.TRUE.equals(orderDetail.getIsGift())) {
            return "送礼订单";
        }
        if (orderDetail.getDeliveryType() != null && orderDetail.getDeliveryType() == 1) {
            return "自提订单";
        }
        return "普通订单";
    }
    
    /**
     * 获取订单商品名称
     */
    private String getOrderProductName(OmsOrderDetail orderDetail) {
        List<OmsOrderItem> items = orderDetail.getOrderItemList();
        if (items == null || items.isEmpty()) {
            return "商品";
        }
        
        String firstName = items.get(0).getProductName();
        if (firstName == null) {
            firstName = "商品";
        }
        
        // 截断过长的商品名称
        if (firstName.length() > 15) {
            firstName = firstName.substring(0, 15) + "...";
        }
        
        if (items.size() > 1) {
            return firstName + "等" + items.size() + "件商品";
        }
        return firstName;
    }

    /**
     * 处理支付成功后的邀请奖励
     */
    private void processInviteRewardOnPayment(OmsOrderDetail orderDetail) {
        if (orderDetail == null || orderDetail.getMemberId() == null) {
            return;
        }

        try {
            // 检查用户是否有邀请关系
            Long inviteeUserId = orderDetail.getMemberId();
            Long inviterUserId = inviteService.getInviterUserId(inviteeUserId);

            if (inviterUserId == null) {
                LOGGER.debug("用户{}没有邀请关系，跳过邀请奖励处理", inviteeUserId);
                return;
            }

            // 检查是否为首单
            boolean isFirstOrder = isUserFirstOrder(inviteeUserId);

            if (isFirstOrder) {
                // 处理首单奖励
                processFirstOrderReward(inviterUserId, inviteeUserId, orderDetail);
            }

            // 处理复购分佣
            processRepurchaseCommission(inviterUserId, inviteeUserId, orderDetail);

        } catch (Exception e) {
            LOGGER.error("处理邀请奖励失败，订单ID: {}", orderDetail.getId(), e);
        }
    }

    /**
     * 检查是否为用户首单
     */
    private boolean isUserFirstOrder(Long userId) {
        try {
            OmsOrderExample example = new OmsOrderExample();
            example.createCriteria()
                .andMemberIdEqualTo(userId)
                .andStatusEqualTo(1) // 已支付
                .andDeleteStatusEqualTo(0);

            long orderCount = orderMapper.countByExample(example);
            return orderCount == 1; // 当前订单是第一个已支付订单
        } catch (Exception e) {
            LOGGER.error("检查用户首单状态失败，用户ID: {}", userId, e);
            return false;
        }
    }

    /**
     * 处理首单奖励
     */
    private void processFirstOrderReward(Long inviterUserId, Long inviteeUserId, OmsOrderDetail orderDetail) {
        try {
            // 获取邀请配置
            Map<String, Object> config = inviteService.getInviteConfig();
            Map<String, Object> inviterReward = (Map<String, Object>) config.get("inviterReward");

            if (inviterReward == null) {
                return;
            }

            // 发放首单返现奖励
            Double cashbackRate = (Double) inviterReward.get("first_order_cashback_rate");
            if (cashbackRate != null && cashbackRate > 0 && orderDetail.getPayAmount() != null) {
                BigDecimal cashbackAmount = orderDetail.getPayAmount().multiply(new BigDecimal(cashbackRate));

                // 创建奖励记录
                createInviteRewardRecord(inviterUserId, 3, cashbackAmount, "邀请好友首单返现",
                    2, 1, orderDetail.getId());
            }

            LOGGER.info("首单奖励处理完成，邀请者ID: {}, 被邀请者ID: {}, 订单ID: {}",
                inviterUserId, inviteeUserId, orderDetail.getId());

        } catch (Exception e) {
            LOGGER.error("处理首单奖励失败，邀请者ID: {}, 订单ID: {}", inviterUserId, orderDetail.getId(), e);
        }
    }

    /**
     * 处理复购分佣
     */
    private void processRepurchaseCommission(Long inviterUserId, Long inviteeUserId, OmsOrderDetail orderDetail) {
        try {
            // 获取邀请配置
            Map<String, Object> config = inviteService.getInviteConfig();
            Map<String, Object> inviterReward = (Map<String, Object>) config.get("inviterReward");

            if (inviterReward == null) {
                return;
            }

            // 检查分佣有效期
            Integer validDays = (Integer) inviterReward.get("repurchase_valid_days");
            if (validDays == null || validDays <= 0) {
                return;
            }

            // 检查邀请关系是否在有效期内
            Date inviteDate = inviteService.getInviteDate(inviterUserId, inviteeUserId);
            if (inviteDate == null) {
                return;
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(inviteDate);
            cal.add(Calendar.DAY_OF_MONTH, validDays);
            Date expireDate = cal.getTime();

            if (new Date().after(expireDate)) {
                LOGGER.debug("邀请关系已过期，跳过复购分佣，邀请者ID: {}, 被邀请者ID: {}", inviterUserId, inviteeUserId);
                return;
            }

            // 计算分佣金额
            Double commissionRate = (Double) inviterReward.get("repurchase_commission_rate");
            if (commissionRate != null && commissionRate > 0 && orderDetail.getPayAmount() != null) {
                BigDecimal commissionAmount = orderDetail.getPayAmount().multiply(new BigDecimal(commissionRate));

                // 创建奖励记录
                createInviteRewardRecord(inviterUserId, 3, commissionAmount, "邀请好友复购分佣",
                    3, 1, orderDetail.getId());

                LOGGER.info("复购分佣处理完成，邀请者ID: {}, 被邀请者ID: {}, 订单ID: {}, 分佣金额: {}",
                    inviterUserId, inviteeUserId, orderDetail.getId(), commissionAmount);
            }

        } catch (Exception e) {
            LOGGER.error("处理复购分佣失败，邀请者ID: {}, 订单ID: {}", inviterUserId, orderDetail.getId(), e);
        }
    }

    /**
     * 创建邀请奖励记录
     */
    private void createInviteRewardRecord(Long userId, Integer rewardType, BigDecimal rewardValue,
                                        String rewardDesc, Integer triggerType, Integer commissionType, Long orderId) {
        try {
            // 获取邀请关系ID
            Long relationId = getInviteRelationIdForUser(userId);

            PmsInviteReward reward = new PmsInviteReward();
            reward.setRelationId(relationId);
            reward.setUserId(userId);
            reward.setUserType((byte) 1); // 邀请者
            reward.setRewardType(rewardType.byteValue());
            reward.setRewardValue(rewardValue);
            reward.setRewardDesc(rewardDesc);
            reward.setTriggerType(triggerType.byteValue());
            reward.setCommissionType(commissionType.byteValue());
            reward.setStatus((byte) 1); // 已发放
            reward.setOrderId(orderId);
            reward.setSettlementStatus((byte) 0); // 未结算
            reward.setSendTime(new Date());
            reward.setSendResult("自动发放成功");
            reward.setCreatedAt(new Date());
            reward.setUpdatedAt(new Date());

            inviteRewardMapper.insert(reward);
            LOGGER.info("邀请奖励记录创建成功，用户ID: {}, 奖励类型: {}, 奖励值: {}", userId, rewardType, rewardValue);
        } catch (Exception e) {
            LOGGER.error("创建邀请奖励记录失败，用户ID: {}", userId, e);
        }
    }

    /**
     * 获取用户的邀请关系ID
     */
    private Long getInviteRelationIdForUser(Long userId) {
        try {
            return inviteService.getInviterUserId(userId);
        } catch (Exception e) {
            LOGGER.warn("获取邀请关系ID失败，用户ID: {}", userId, e);
            return null;
        }
    }
}
