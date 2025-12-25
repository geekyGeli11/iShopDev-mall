package com.macro.mall.portal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.portal.dao.PortalOrderItemDao;
import com.macro.mall.portal.domain.*;
import com.macro.mall.portal.service.PortalBundleService;
import com.macro.mall.portal.service.UmsMemberBalanceService;
import com.macro.mall.portal.service.UmsMemberCouponService;
import com.macro.mall.portal.service.UmsMemberReceiveAddressService;
import com.macro.mall.portal.service.UmsMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 组合商品Portal服务实现类
 */
@Service
public class PortalBundleServiceImpl implements PortalBundleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PortalBundleServiceImpl.class);

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Autowired
    private PmsProductBundleMapper bundleMapper;
    @Autowired
    private PmsProductBundleItemMapper bundleItemMapper;
    @Autowired
    private PmsProductMapper productMapper;
    @Autowired
    private PmsSkuStockMapper skuStockMapper;
    @Autowired
    private PmsStoreSkuStockMapper storeSkuStockMapper;
    @Autowired
    private UmsMemberService memberService;
    @Autowired
    private UmsMemberReceiveAddressService addressService;
    @Autowired
    private OmsOrderMapper orderMapper;
    @Autowired
    private PortalOrderItemDao orderItemDao;
    @Autowired
    private OmsStoreAddressMapper storeAddressMapper;
    @Autowired
    private UmsMemberBalanceService memberBalanceService;
    @Autowired
    private OmsOrderSettingMapper orderSettingMapper;
    @Autowired
    private UmsMemberCouponService memberCouponService;

    @Override
    public List<PortalBundleListItem> list(Long schoolId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PmsProductBundleExample example = new PmsProductBundleExample();
        
        if (schoolId != null) {
            // 查询指定学校的组合商品 或 schoolId为null的通用组合商品
            PmsProductBundleExample.Criteria criteria1 = example.createCriteria();
            criteria1.andPublishStatusEqualTo((byte) 1);
            criteria1.andSchoolIdEqualTo(schoolId);
            
            PmsProductBundleExample.Criteria criteria2 = example.createCriteria();
            criteria2.andPublishStatusEqualTo((byte) 1);
            criteria2.andSchoolIdIsNull();
            
            example.or(criteria2);
        } else {
            // 未指定学校时，查询所有已上架的组合商品
            PmsProductBundleExample.Criteria criteria = example.createCriteria();
            criteria.andPublishStatusEqualTo((byte) 1);
        }
        
        example.setOrderByClause("sort desc, create_time desc");
        
        List<PmsProductBundle> bundles = bundleMapper.selectByExample(example);
        
        // 获取每个组合的商品数量
        List<PortalBundleListItem> result = new ArrayList<>();
        for (PmsProductBundle bundle : bundles) {
            PortalBundleListItem item = new PortalBundleListItem();
            BeanUtils.copyProperties(bundle, item);
            
            // 计算节省金额
            if (bundle.getOriginalPrice() != null && bundle.getBundlePrice() != null) {
                item.setSavedAmount(bundle.getOriginalPrice().subtract(bundle.getBundlePrice()));
            }
            
            // 获取商品数量
            PmsProductBundleItemExample itemExample = new PmsProductBundleItemExample();
            itemExample.createCriteria().andBundleIdEqualTo(bundle.getId());
            long productCount = bundleItemMapper.countByExample(itemExample);
            item.setProductCount((int) productCount);
            
            result.add(item);
        }
        
        return result;
    }

    @Override
    public PortalBundleDetail getDetail(Long id, Long storeId) {
        PmsProductBundle bundle = bundleMapper.selectByPrimaryKey(id);
        if (bundle == null || bundle.getPublishStatus() != 1) {
            Asserts.fail("组合商品不存在或已下架");
        }
        
        PortalBundleDetail detail = new PortalBundleDetail();
        BeanUtils.copyProperties(bundle, detail);
        detail.setPriceType(bundle.getPriceType().intValue());
        
        // 处理宣传图片
        if (StrUtil.isNotEmpty(bundle.getAlbumPics())) {
            detail.setAlbumPics(Arrays.asList(bundle.getAlbumPics().split(",")));
        } else {
            detail.setAlbumPics(new ArrayList<>());
        }
        
        // 计算节省金额
        if (bundle.getOriginalPrice() != null && bundle.getBundlePrice() != null) {
            detail.setSavedAmount(bundle.getOriginalPrice().subtract(bundle.getBundlePrice()));
        }
        
        // 获取组合内商品列表
        PmsProductBundleItemExample itemExample = new PmsProductBundleItemExample();
        itemExample.createCriteria().andBundleIdEqualTo(id);
        itemExample.setOrderByClause("sort asc");
        List<PmsProductBundleItem> items = bundleItemMapper.selectByExample(itemExample);
        
        List<PortalBundleDetail.BundleProductItem> products = new ArrayList<>();
        for (PmsProductBundleItem item : items) {
            PortalBundleDetail.BundleProductItem productItem = new PortalBundleDetail.BundleProductItem();
            productItem.setProductId(item.getProductId());
            productItem.setProductName(item.getProductName());
            productItem.setProductPic(item.getProductPic());
            productItem.setQuantity(item.getQuantity());
            
            // 获取商品详情
            PmsProduct product = productMapper.selectByPrimaryKey(item.getProductId());
            if (product != null) {
                productItem.setPrice(product.getPrice());
                productItem.setSubTitle(product.getSubTitle());
            }
            
            // 获取SKU列表
            List<PmsSkuStock> skuList = getSkuList(item.getProductId(), storeId);
            productItem.setSkuList(skuList);
            
            products.add(productItem);
        }
        detail.setProducts(products);
        
        return detail;
    }

    @Override
    public BundleConfirmOrderResult generateConfirmOrder(BundleOrderParam param) {
        // 获取组合商品
        PmsProductBundle bundle = bundleMapper.selectByPrimaryKey(param.getBundleId());
        if (bundle == null || bundle.getPublishStatus() != 1) {
            Asserts.fail("组合商品不存在或已下架");
        }
        
        // 获取组合内商品
        PmsProductBundleItemExample itemExample = new PmsProductBundleItemExample();
        itemExample.createCriteria().andBundleIdEqualTo(param.getBundleId());
        List<PmsProductBundleItem> bundleItems = bundleItemMapper.selectByExample(itemExample);
        
        // 验证SKU选择
        validateSkuSelections(bundleItems, param.getSkuSelections(), param.getQuantity(), param.getStoreId());
        
        BundleConfirmOrderResult result = new BundleConfirmOrderResult();
        
        // 设置组合信息
        BundleConfirmOrderResult.BundleInfo bundleInfo = new BundleConfirmOrderResult.BundleInfo();
        bundleInfo.setBundleId(bundle.getId());
        bundleInfo.setBundleName(bundle.getName());
        bundleInfo.setBundlePic(bundle.getPic());
        bundleInfo.setBundlePrice(bundle.getBundlePrice());
        bundleInfo.setOriginalPrice(bundle.getOriginalPrice());
        bundleInfo.setSavedAmount(bundle.getOriginalPrice().subtract(bundle.getBundlePrice()));
        bundleInfo.setQuantity(param.getQuantity());
        result.setBundleInfo(bundleInfo);
        
        // 构建订单项
        List<BundleConfirmOrderResult.BundleOrderItem> orderItems = buildOrderItems(
                bundleItems, param.getSkuSelections(), param.getQuantity(), bundle);
        result.setOrderItems(orderItems);
        
        // 获取收货地址
        result.setMemberReceiveAddressList(addressService.list());
        
        // 获取会员信息
        UmsMember member = memberService.getCurrentMember();
        result.setMemberIntegration(member.getIntegration());
        result.setMemberBalance(member.getBalance());
        
        // 获取可用优惠券列表
        // 将组合商品转换为CartPromotionItem格式以便查询可用优惠券
        List<CartPromotionItem> cartPromotionItemList = convertToCartPromotionItems(bundleItems, param.getSkuSelections(), param.getQuantity());
        List<SmsCouponHistoryDetail> couponHistoryDetailList = memberCouponService.listCart(cartPromotionItemList, 1, true);
        result.setCouponHistoryDetailList(couponHistoryDetailList);
        
        // 计算金额
        BundleConfirmOrderResult.CalcAmount calcAmount = calculateAmount(bundle, param.getQuantity());
        result.setCalcAmount(calcAmount);
        
        return result;
    }
    
    /**
     * 将组合商品项转换为CartPromotionItem格式
     */
    private List<CartPromotionItem> convertToCartPromotionItems(List<PmsProductBundleItem> bundleItems, 
                                                                  List<BundleSkuSelection> skuSelections,
                                                                  Integer quantity) {
        // SKU选择映射
        Map<Long, Long> skuSelectionMap = new HashMap<>();
        for (BundleSkuSelection selection : skuSelections) {
            skuSelectionMap.put(selection.getProductId(), selection.getSkuId());
        }
        
        List<CartPromotionItem> result = new ArrayList<>();
        for (PmsProductBundleItem item : bundleItems) {
            Long skuId = skuSelectionMap.get(item.getProductId());
            PmsSkuStock sku = skuStockMapper.selectByPrimaryKey(skuId);
            PmsProduct product = productMapper.selectByPrimaryKey(item.getProductId());
            
            CartPromotionItem cartItem = new CartPromotionItem();
            cartItem.setProductId(item.getProductId());
            cartItem.setProductName(item.getProductName());
            cartItem.setProductPic(item.getProductPic());
            cartItem.setProductSkuId(skuId);
            cartItem.setProductSkuCode(sku != null ? sku.getSkuCode() : null);
            cartItem.setProductCategoryId(product != null ? product.getProductCategoryId() : null);
            cartItem.setProductBrand(product != null ? product.getBrandName() : null);
            cartItem.setPrice(product != null ? product.getPrice() : BigDecimal.ZERO);
            cartItem.setQuantity(item.getQuantity() * quantity);
            cartItem.setProductAttr(sku != null ? sku.getSpData() : null);
            // 设置促销相关字段为默认值
            cartItem.setReduceAmount(BigDecimal.ZERO);
            cartItem.setGrowth(0);
            cartItem.setIntegration(0);
            
            result.add(cartItem);
        }
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> createOrder(BundleOrderParam param) {
        // 获取当前用户
        UmsMember currentMember = memberService.getCurrentMember();
        
        // 获取组合商品
        PmsProductBundle bundle = bundleMapper.selectByPrimaryKey(param.getBundleId());
        if (bundle == null || bundle.getPublishStatus() != 1) {
            Asserts.fail("组合商品不存在或已下架");
        }
        
        // 获取组合内商品
        PmsProductBundleItemExample itemExample = new PmsProductBundleItemExample();
        itemExample.createCriteria().andBundleIdEqualTo(param.getBundleId());
        List<PmsProductBundleItem> bundleItems = bundleItemMapper.selectByExample(itemExample);
        
        // 验证SKU选择和库存
        validateSkuSelections(bundleItems, param.getSkuSelections(), param.getQuantity(), param.getStoreId());
        
        // 配送方式
        Integer deliveryType = param.getDeliveryType() != null ? param.getDeliveryType() : 0;
        
        // 校验收货地址（门店自取时无需校验）
        if (deliveryType == 0 && param.getAddressId() == null) {
            Asserts.fail("请选择收货地址！");
        }
        
        // 计算优惠分摊
        BigDecimal totalSaved = bundle.getOriginalPrice().subtract(bundle.getBundlePrice());
        Map<Long, BigDecimal> discountMap = calculateDiscountDistribution(bundleItems, totalSaved);
        
        // SKU选择映射（处理null值）
        Map<Long, Long> skuSelectionMap = new HashMap<>();
        for (BundleSkuSelection selection : param.getSkuSelections()) {
            skuSelectionMap.put(selection.getProductId(), selection.getSkuId());
        }
        
        // 构建订单项列表
        List<OmsOrderItem> orderItemList = new ArrayList<>();
        for (PmsProductBundleItem item : bundleItems) {
            Long skuId = skuSelectionMap.get(item.getProductId());
            PmsSkuStock sku = skuStockMapper.selectByPrimaryKey(skuId);
            PmsProduct product = productMapper.selectByPrimaryKey(item.getProductId());
            
            OmsOrderItem orderItem = new OmsOrderItem();
            orderItem.setProductId(item.getProductId());
            orderItem.setProductPic(item.getProductPic());
            orderItem.setProductName(item.getProductName());
            orderItem.setProductBrand(product != null ? product.getBrandName() : "");
            orderItem.setProductSn(product != null ? product.getProductSn() : "");
            orderItem.setProductPrice(sku != null ? sku.getPrice() : (product != null ? product.getPrice() : BigDecimal.ZERO));
            orderItem.setProductQuantity(item.getQuantity() * param.getQuantity());
            orderItem.setProductSkuId(skuId);
            orderItem.setProductSkuCode(sku != null ? sku.getSkuCode() : "");
            orderItem.setProductCategoryId(product != null ? product.getProductCategoryId() : null);
            orderItem.setProductAttr(sku != null ? sku.getSpData() : "");
            
            // 设置组合商品相关字段
            orderItem.setBundleId(bundle.getId());
            BigDecimal itemDiscount = discountMap.getOrDefault(item.getProductId(), BigDecimal.ZERO)
                    .multiply(new BigDecimal(param.getQuantity()));
            orderItem.setBundleDiscount(itemDiscount);
            
            // 设置优惠金额
            orderItem.setPromotionAmount(itemDiscount);
            orderItem.setCouponAmount(BigDecimal.ZERO);
            orderItem.setIntegrationAmount(BigDecimal.ZERO);
            
            // 计算实付金额
            BigDecimal realAmount = orderItem.getProductPrice()
                    .multiply(new BigDecimal(orderItem.getProductQuantity()))
                    .subtract(itemDiscount);
            orderItem.setRealAmount(realAmount);
            
            orderItemList.add(orderItem);
        }
        
        // 锁定库存
        lockBundleStock(param.getSkuSelections(), bundleItems, param.getQuantity(), param.getStoreId());
        
        // 创建订单
        OmsOrder order = new OmsOrder();
        order.setMemberId(currentMember.getId());
        order.setMemberUsername(currentMember.getUsername());
        order.setCreateTime(new Date());
        
        // 计算订单金额
        BigDecimal totalAmount = bundle.getOriginalPrice().multiply(new BigDecimal(param.getQuantity()));
        BigDecimal promotionAmount = totalSaved.multiply(new BigDecimal(param.getQuantity()));
        BigDecimal payAmount = bundle.getBundlePrice().multiply(new BigDecimal(param.getQuantity()));
        
        order.setTotalAmount(totalAmount);
        order.setPromotionAmount(promotionAmount);
        order.setPromotionInfo("组合商品优惠：" + bundle.getName());
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setCouponAmount(BigDecimal.ZERO);
        order.setIntegrationAmount(BigDecimal.ZERO);
        order.setFreightAmount(BigDecimal.ZERO); // 组合商品暂不计算运费
        order.setPayAmount(payAmount);
        
        // 支付方式
        order.setPayType(param.getPayType() != null ? param.getPayType() : 0);
        order.setSourceType(1); // 小程序订单
        order.setStatus(0); // 待付款
        order.setOrderType(0); // 正常订单
        order.setDeliveryType(deliveryType);
        
        // 设置学校ID
        if (param.getSchoolId() != null) {
            order.setSourceSchoolId(param.getSchoolId());
        } else if (bundle.getSchoolId() != null) {
            order.setSourceSchoolId(bundle.getSchoolId());
        } else if (currentMember.getSchoolId() != null) {
            order.setSourceSchoolId(currentMember.getSchoolId());
        }
        
        // 设置门店信息（自提订单）
        if (deliveryType == 1 && param.getStoreId() != null) {
            OmsStoreAddress store = storeAddressMapper.selectByPrimaryKey(param.getStoreId());
            if (store != null) {
                order.setStoreId(store.getId());
                order.setStoreName(store.getAddressName());
                order.setStoreAddress(store.getDetailAddress());
                order.setStorePhone(store.getPhone());
            }
        }
        
        // 设置收货人信息
        if (deliveryType == 0) {
            UmsMemberReceiveAddress address = addressService.getItem(param.getAddressId());
            if (address != null) {
                order.setReceiverName(address.getName());
                order.setReceiverPhone(address.getPhoneNumber());
                order.setReceiverPostCode(address.getPostCode());
                order.setReceiverProvince(address.getProvince());
                order.setReceiverCity(address.getCity());
                order.setReceiverRegion(address.getRegion());
                order.setReceiverDetailAddress(address.getDetailAddress());
            }
        } else {
            order.setReceiverName(currentMember.getNickname());
            order.setReceiverPhone(currentMember.getPhone());
            order.setReceiverPostCode("");
            order.setReceiverProvince("");
            order.setReceiverCity("");
            order.setReceiverRegion("");
            order.setReceiverDetailAddress("门店自取");
        }
        
        order.setConfirmStatus(0);
        order.setDeleteStatus(0);
        order.setNote(param.getNote());
        order.setOrderSn(generateOrderSn(order));
        
        // 设置自动收货天数
        List<OmsOrderSetting> orderSettings = orderSettingMapper.selectByExample(new OmsOrderSettingExample());
        if (!CollectionUtils.isEmpty(orderSettings)) {
            order.setAutoConfirmDay(orderSettings.get(0).getConfirmOvertime());
        }
        
        // 插入订单
        orderMapper.insert(order);
        
        // 插入订单项
        for (OmsOrderItem orderItem : orderItemList) {
            orderItem.setOrderId(order.getId());
            orderItem.setOrderSn(order.getOrderSn());
        }
        orderItemDao.insertList(orderItemList);
        
        // 更新组合商品销量
        bundle.setSaleCount(bundle.getSaleCount() + param.getQuantity());
        bundleMapper.updateByPrimaryKeySelective(bundle);
        
        // 处理余额支付
        if (param.getPayType() != null && param.getPayType() == 3) {
            if (!memberBalanceService.checkBalanceEnough(currentMember.getId(), payAmount)) {
                Asserts.fail("余额不足，无法完成支付");
            }
            
            boolean success = memberBalanceService.recordBalanceChange(
                    currentMember.getId(),
                    2, // 消费类型
                    payAmount,
                    "ORDER_PAY",
                    order.getOrderSn(),
                    "组合商品订单支付：" + order.getOrderSn()
            );
            
            if (!success) {
                Asserts.fail("余额支付失败");
            }
            
            order.setStatus(1); // 待发货
            order.setPaymentTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        }
        
        LOGGER.info("组合商品订单创建成功 - 订单号: {}, 组合ID: {}, 数量: {}, 支付金额: {}",
                order.getOrderSn(), bundle.getId(), param.getQuantity(), payAmount);
        
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("orderItemList", orderItemList);
        return result;
    }
    
    /**
     * 锁定组合商品库存（参考普通订单的lockStock逻辑）
     * 注意：这里只锁定库存，不扣减库存。库存扣减是在支付成功后进行的。
     */
    private void lockBundleStock(List<BundleSkuSelection> skuSelections, 
                                  List<PmsProductBundleItem> bundleItems,
                                  Integer quantity, Long storeId) {
        Map<Long, Integer> itemQuantityMap = bundleItems.stream()
                .collect(Collectors.toMap(PmsProductBundleItem::getProductId, PmsProductBundleItem::getQuantity));
        
        for (BundleSkuSelection selection : skuSelections) {
            Integer itemQuantity = itemQuantityMap.get(selection.getProductId());
            int lockQuantity = itemQuantity * quantity;
            
            if (storeId != null) {
                // 门店自取订单：锁定门店库存
                PmsStoreSkuStockExample storeExample = new PmsStoreSkuStockExample();
                storeExample.createCriteria()
                        .andStoreIdEqualTo(storeId)
                        .andSkuIdEqualTo(selection.getSkuId());
                List<PmsStoreSkuStock> storeStocks = storeSkuStockMapper.selectByExample(storeExample);
                if (!CollectionUtils.isEmpty(storeStocks)) {
                    PmsStoreSkuStock storeStock = storeStocks.get(0);
                    storeStock.setLockedStock(storeStock.getLockedStock() + lockQuantity);
                    storeSkuStockMapper.updateByPrimaryKeySelective(storeStock);
                }
            } else {
                // 快递配送订单：锁定总库存
                PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(selection.getSkuId());
                if (skuStock != null) {
                    skuStock.setLockStock(skuStock.getLockStock() + lockQuantity);
                    skuStockMapper.updateByPrimaryKeySelective(skuStock);
                }
            }
        }
    }
    
    /**
     * 生成订单编号
     */
    private String generateOrderSn(OmsOrder order) {
        StringBuilder sb = new StringBuilder();
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        
        // 根据环境添加前缀
        if (!"prod".equals(activeProfile)) {
            sb.append("TEST_");
        }
        
        sb.append("GHZ");
        sb.append(date);
        sb.append(String.format("%02d", order.getSourceType()));
        sb.append(String.format("%02d", order.getPayType()));
        sb.append(String.format("%06d", (int) (Math.random() * 1000000)));
        
        return sb.toString();
    }

    /**
     * 获取SKU列表
     */
    private List<PmsSkuStock> getSkuList(Long productId, Long storeId) {
        PmsSkuStockExample example = new PmsSkuStockExample();
        example.createCriteria().andProductIdEqualTo(productId);
        List<PmsSkuStock> skuList = skuStockMapper.selectByExample(example);
        
        // 如果有门店ID，获取门店库存
        if (storeId != null && !CollectionUtils.isEmpty(skuList)) {
            List<Long> skuIds = skuList.stream().map(PmsSkuStock::getId).collect(Collectors.toList());
            PmsStoreSkuStockExample storeExample = new PmsStoreSkuStockExample();
            storeExample.createCriteria()
                    .andStoreIdEqualTo(storeId)
                    .andSkuIdIn(skuIds);
            List<PmsStoreSkuStock> storeStocks = storeSkuStockMapper.selectByExample(storeExample);
            Map<Long, Integer> storeStockMap = storeStocks.stream()
                    .collect(Collectors.toMap(PmsStoreSkuStock::getSkuId, PmsStoreSkuStock::getStock));
            
            // 更新SKU库存为门店库存
            for (PmsSkuStock sku : skuList) {
                Integer storeStock = storeStockMap.get(sku.getId());
                if (storeStock != null) {
                    sku.setStock(storeStock);
                }
            }
        }
        
        return skuList;
    }

    /**
     * 验证SKU选择
     */
    private void validateSkuSelections(List<PmsProductBundleItem> bundleItems, 
                                       List<BundleSkuSelection> skuSelections,
                                       Integer quantity, Long storeId) {
        if (CollectionUtils.isEmpty(skuSelections)) {
            Asserts.fail("请为所有商品选择规格");
        }
        
        // 检查是否所有商品都选择了SKU
        Set<Long> selectedProductIds = skuSelections.stream()
                .map(BundleSkuSelection::getProductId)
                .collect(Collectors.toSet());
        
        for (PmsProductBundleItem item : bundleItems) {
            if (!selectedProductIds.contains(item.getProductId())) {
                Asserts.fail("请为商品\"" + item.getProductName() + "\"选择规格");
            }
        }
        
        // 验证库存（参考普通订单的库存验证逻辑，考虑锁定库存）
        Map<Long, Integer> itemQuantityMap = bundleItems.stream()
                .collect(Collectors.toMap(PmsProductBundleItem::getProductId, PmsProductBundleItem::getQuantity));
        
        for (BundleSkuSelection selection : skuSelections) {
            Integer itemQuantity = itemQuantityMap.get(selection.getProductId());
            int requiredStock = itemQuantity * quantity;
            
            PmsSkuStock sku = skuStockMapper.selectByPrimaryKey(selection.getSkuId());
            if (sku == null) {
                Asserts.fail("SKU不存在");
            }
            
            // 获取实际可用库存（总库存 - 锁定库存 或 门店库存 - 门店锁定库存）
            int realStock;
            if (storeId != null) {
                // 门店自取：检查门店库存
                PmsStoreSkuStockExample storeExample = new PmsStoreSkuStockExample();
                storeExample.createCriteria()
                        .andStoreIdEqualTo(storeId)
                        .andSkuIdEqualTo(sku.getId());
                List<PmsStoreSkuStock> storeStocks = storeSkuStockMapper.selectByExample(storeExample);
                if (!CollectionUtils.isEmpty(storeStocks)) {
                    PmsStoreSkuStock storeStock = storeStocks.get(0);
                    realStock = storeStock.getStock() - storeStock.getLockedStock();
                } else {
                    realStock = 0;
                }
            } else {
                // 快递配送：检查总库存
                realStock = sku.getStock() - sku.getLockStock();
            }
            
            if (realStock < requiredStock) {
                PmsProduct product = productMapper.selectByPrimaryKey(selection.getProductId());
                String productName = product != null ? product.getName() : "未知商品";
                Asserts.fail("商品\"" + productName + "\"库存不足，当前可用库存：" + realStock);
            }
        }
    }

    /**
     * 构建订单项
     */
    private List<BundleConfirmOrderResult.BundleOrderItem> buildOrderItems(
            List<PmsProductBundleItem> bundleItems,
            List<BundleSkuSelection> skuSelections,
            Integer quantity,
            PmsProductBundle bundle) {
        
        // 计算优惠分摊
        BigDecimal totalSaved = bundle.getOriginalPrice().subtract(bundle.getBundlePrice());
        Map<Long, BigDecimal> discountMap = calculateDiscountDistribution(bundleItems, totalSaved);
        
        // SKU选择映射（处理null值）
        Map<Long, Long> skuSelectionMap = new HashMap<>();
        for (BundleSkuSelection selection : skuSelections) {
            skuSelectionMap.put(selection.getProductId(), selection.getSkuId());
        }
        
        List<BundleConfirmOrderResult.BundleOrderItem> orderItems = new ArrayList<>();
        for (PmsProductBundleItem item : bundleItems) {
            Long skuId = skuSelectionMap.get(item.getProductId());
            PmsSkuStock sku = skuStockMapper.selectByPrimaryKey(skuId);
            PmsProduct product = productMapper.selectByPrimaryKey(item.getProductId());
            
            BundleConfirmOrderResult.BundleOrderItem orderItem = new BundleConfirmOrderResult.BundleOrderItem();
            orderItem.setProductId(item.getProductId());
            orderItem.setProductName(item.getProductName());
            orderItem.setProductPic(item.getProductPic());
            orderItem.setSkuId(skuId);
            orderItem.setSkuCode(sku != null ? sku.getSkuCode() : null);
            orderItem.setSpData(sku != null ? sku.getSpData() : null);
            orderItem.setPrice(product != null ? product.getPrice() : BigDecimal.ZERO);
            orderItem.setQuantity(item.getQuantity() * quantity);
            orderItem.setBundleDiscount(discountMap.getOrDefault(item.getProductId(), BigDecimal.ZERO)
                    .multiply(new BigDecimal(quantity)));
            
            orderItems.add(orderItem);
        }
        
        return orderItems;
    }

    /**
     * 计算优惠分摊
     */
    private Map<Long, BigDecimal> calculateDiscountDistribution(List<PmsProductBundleItem> items, BigDecimal totalSaved) {
        Map<Long, BigDecimal> result = new HashMap<>();
        
        // 计算原价总和
        BigDecimal totalOriginal = BigDecimal.ZERO;
        Map<Long, BigDecimal> itemPriceMap = new HashMap<>();
        for (PmsProductBundleItem item : items) {
            PmsProduct product = productMapper.selectByPrimaryKey(item.getProductId());
            if (product != null && product.getPrice() != null) {
                BigDecimal itemTotal = product.getPrice().multiply(new BigDecimal(item.getQuantity()));
                itemPriceMap.put(item.getProductId(), itemTotal);
                totalOriginal = totalOriginal.add(itemTotal);
            }
        }
        
        // 按比例分摊优惠
        BigDecimal distributedTotal = BigDecimal.ZERO;
        int index = 0;
        for (PmsProductBundleItem item : items) {
            index++;
            BigDecimal itemPrice = itemPriceMap.getOrDefault(item.getProductId(), BigDecimal.ZERO);
            BigDecimal discount;
            
            if (index == items.size()) {
                // 最后一个商品，分摊剩余优惠（避免精度问题）
                discount = totalSaved.subtract(distributedTotal);
            } else {
                // 按比例分摊
                discount = totalSaved.multiply(itemPrice)
                        .divide(totalOriginal, 2, RoundingMode.HALF_UP);
                distributedTotal = distributedTotal.add(discount);
            }
            
            result.put(item.getProductId(), discount);
        }
        
        return result;
    }

    /**
     * 计算金额
     */
    private BundleConfirmOrderResult.CalcAmount calculateAmount(PmsProductBundle bundle, Integer quantity) {
        BundleConfirmOrderResult.CalcAmount calcAmount = new BundleConfirmOrderResult.CalcAmount();
        
        BigDecimal totalAmount = bundle.getOriginalPrice().multiply(new BigDecimal(quantity));
        BigDecimal bundleDiscountAmount = bundle.getOriginalPrice().subtract(bundle.getBundlePrice())
                .multiply(new BigDecimal(quantity));
        BigDecimal payAmount = bundle.getBundlePrice().multiply(new BigDecimal(quantity));
        
        calcAmount.setTotalAmount(totalAmount);
        calcAmount.setBundleDiscountAmount(bundleDiscountAmount);
        calcAmount.setFreightAmount(BigDecimal.ZERO); // 运费需要根据地址计算
        calcAmount.setCouponAmount(BigDecimal.ZERO);
        calcAmount.setIntegrationAmount(BigDecimal.ZERO);
        calcAmount.setPayAmount(payAmount);
        
        return calcAmount;
    }
}
