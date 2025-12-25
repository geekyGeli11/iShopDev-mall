package com.macro.mall.portal.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.portal.domain.ExchangeResult;
import com.macro.mall.portal.domain.ExchangePayOrderResult;
import com.macro.mall.portal.domain.PointsCouponConfigDetail;
import com.macro.mall.portal.domain.PointsProductConfigDetail;
import com.macro.mall.portal.domain.ProductSpecification;
import com.macro.mall.portal.dto.CouponExchangeParam;
import com.macro.mall.portal.dto.ExchangePayOrderParam;
import com.macro.mall.portal.dto.ExchangePaySuccessParam;
import com.macro.mall.portal.dto.PointsExchangeQueryParam;
import com.macro.mall.portal.dto.ProductExchangeParam;
import com.macro.mall.portal.service.UmsPointsExchangeService;
import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.portal.service.OmsPortalOrderService;
import com.macro.mall.portal.service.UmsIntegrationService;
import com.macro.mall.portal.domain.OrderParam;
import com.macro.mall.portal.domain.CartPromotionItem;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

/**
 * 积分换购Service实现类
 * Created by macro on 2024/01/20.
 */
@Service
public class UmsPointsExchangeServiceImpl implements UmsPointsExchangeService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UmsPointsExchangeServiceImpl.class);
    
    @Autowired
    private UmsPointsProductConfigMapper pointsProductConfigMapper;
    @Autowired
    private UmsPointsCouponConfigMapper pointsCouponConfigMapper;
    @Autowired
    private UmsPointsExchangeLogMapper pointsExchangeLogMapper;
    @Autowired
    private UmsMemberMapper memberMapper;
    @Autowired
    private UmsIntegrationChangeHistoryMapper integrationChangeHistoryMapper;
    @Autowired
    private SmsCouponHistoryMapper couponHistoryMapper;
    @Autowired
    private SmsCouponMapper couponMapper;
    @Autowired
    private UmsMemberService memberService;
    @Autowired
    private OmsPortalOrderService orderService;
    @Autowired
    private OmsPortalOrderService portalOrderService;
    @Autowired
    private OmsOrderMapper orderMapper;
    @Autowired
    private OmsOrderItemMapper orderItemMapper;
    @Autowired
    private PmsSkuStockMapper skuStockMapper;
    @Autowired
    private UmsIntegrationService integrationService;
    
    @Override
    public List<PointsProductConfigDetail> getProductList(PointsExchangeQueryParam queryParam, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        UmsPointsProductConfigExample example = createProductExample(queryParam);
        List<UmsPointsProductConfig> configs = pointsProductConfigMapper.selectByExample(example);
        
        List<PointsProductConfigDetail> details = new ArrayList<>();
        UmsMember currentMember = memberService.getCurrentMember();
        Long memberId = currentMember != null ? currentMember.getId() : null;
        
        for (UmsPointsProductConfig config : configs) {
            PointsProductConfigDetail detail = new PointsProductConfigDetail();
            BeanUtils.copyProperties(config, detail);
            
            // 查询并设置SKU信息
            if (config.getProductId() != null) {
                List<PmsSkuStock> skuList = getProductSkuList(config.getProductId());
                detail.setSkuList(skuList);
                
                List<ProductSpecification> specificationList = parseProductSpecifications(skuList);
                detail.setSpecificationList(specificationList);
            }
            
            if (memberId != null) {
                // 设置用户已兑换数量
                int userExchangedCount = getUserExchangedCount(memberId, config.getId(), (byte) 1);
                detail.setUserExchangedCount(userExchangedCount);
                
                // 检查是否可兑换
                detail.setCanExchange(checkProductExchangeEligible(memberId, config.getId()));
                if (!detail.getCanExchange()) {
                    detail.setCannotExchangeReason(getCannotExchangeReason(memberId, config));
                }
            }
            
            details.add(detail);
        }
        
        return details;
    }
    
    @Override
    public PointsProductConfigDetail getProductDetail(Long configId) {
        UmsPointsProductConfig config = pointsProductConfigMapper.selectByPrimaryKey(configId);
        if (config == null) {
            return null;
        }
        
        PointsProductConfigDetail detail = new PointsProductConfigDetail();
        BeanUtils.copyProperties(config, detail);
        
        // 查询并设置SKU信息
        if (config.getProductId() != null) {
            List<PmsSkuStock> skuList = getProductSkuList(config.getProductId());
            detail.setSkuList(skuList);
            
            List<ProductSpecification> specificationList = parseProductSpecifications(skuList);
            detail.setSpecificationList(specificationList);
        }
        
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember != null) {
            int userExchangedCount = getUserExchangedCount(currentMember.getId(), configId, (byte) 1);
            detail.setUserExchangedCount(userExchangedCount);
            detail.setCanExchange(checkProductExchangeEligible(currentMember.getId(), configId));
            if (!detail.getCanExchange()) {
                detail.setCannotExchangeReason(getCannotExchangeReason(currentMember.getId(), config));
            }
        }
        
        return detail;
    }
    
    @Override
    public ExchangeResult exchangeProduct(ProductExchangeParam param) {
        LOGGER.info("开始商品兑换，参数：{}", param);

        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            LOGGER.warn("用户未登录，兑换失败");
            return ExchangeResult.builder()
                .exchangeStatus((byte) 2)
                .message("请先登录")
                .build();
        }

        LOGGER.info("用户兑换商品，用户ID：{}，用户名：{}", currentMember.getId(), currentMember.getNickname());
        
        // 1. 校验换购配置
        UmsPointsProductConfig config = pointsProductConfigMapper.selectByPrimaryKey(param.getConfigId());
        if (config == null || config.getStatus() != 1) {
            return ExchangeResult.builder()
                .exchangeStatus((byte) 2)
                .message("商品换购配置不存在或已下架")
                .build();
        }
        
        // 2. 校验SKU信息
        PmsSkuStock selectedSku = null;
        if (param.getProductSkuId() != null) {
            selectedSku = validateAndGetSku(param.getProductSkuId(), config.getProductId(), param.getQuantity());
            if (selectedSku == null) {
                return ExchangeResult.builder()
                    .exchangeStatus((byte) 2)
                    .message("所选商品规格不存在或库存不足")
                    .build();
            }
        }
        
        // 3. 校验库存（优先使用SKU库存，否则使用商品总库存）
        int availableStock = selectedSku != null ? selectedSku.getStock() : config.getRemainingStock();
        if (availableStock < param.getQuantity()) {
            return ExchangeResult.builder()
                .exchangeStatus((byte) 2)
                .message("库存不足")
                .build();
        }
        
        // 3. 校验积分
        int totalPointsNeeded = config.getPointsPrice() * param.getQuantity();
        // 安全获取用户积分，处理null值
        Integer memberIntegration = currentMember.getIntegration();
        int currentIntegration = memberIntegration != null ? memberIntegration : 0;
        if (currentIntegration < totalPointsNeeded) {
            return ExchangeResult.builder()
                .exchangeStatus((byte) 2)
                .message("积分不足")
                .build();
        }
        
        // 4. 校验个人限购
        int userExchangedCount = getUserExchangedCount(currentMember.getId(), param.getConfigId(), (byte) 1);
        if (userExchangedCount + param.getQuantity() > config.getPerPersonLimit()) {
            return ExchangeResult.builder()
                .exchangeStatus((byte) 2)
                .message("超过个人限购数量")
                .build();
        }
        
        try {
            // 5. 构建订单参数，创建纯积分兑换订单
            OrderParam orderParam = buildPurePointsExchangeOrderParam(config, param, currentMember);
            LOGGER.info("纯积分兑换 - 准备调用订单生成服务，orderParam.useIntegration: {}", orderParam.getUseIntegration());

            // 6. 调用订单生成服务创建订单
            Map<String, Object> orderResult = portalOrderService.generateOrder(orderParam);
            LOGGER.info("纯积分兑换 - 订单生成服务调用完成，返回结果: {}", orderResult);
            OmsOrder order = (OmsOrder) orderResult.get("order");

            if (order == null || order.getOrderSn() == null) {
                LOGGER.error("纯积分兑换 - 订单创建失败，order: {}", order);
                throw new RuntimeException("订单创建失败");
            }

            LOGGER.info("纯积分兑换 - 订单创建成功，订单号: {}, useIntegration: {}",
                       order.getOrderSn(), order.getUseIntegration());

            // 7. 扣除积分
            updateMemberPoints(currentMember.getId(), -totalPointsNeeded, "商品兑换");

            // 8. 纯积分兑换订单直接设置为已支付状态（因为不需要现金支付）
            portalOrderService.paySuccessByOrderSn(order.getOrderSn(), 4); // payType=4表示积分支付

            // 9. 记录兑换日志
            UmsPointsExchangeLog log = createExchangeLogWithOrder(currentMember, config, param, order.getId());
            int insertResult = pointsExchangeLogMapper.insertSelective(log);
            LOGGER.info("纯积分兑换记录创建结果：{}，记录ID：{}，用户ID：{}，商品：{}，订单ID：{}",
                       insertResult, log.getId(), currentMember.getId(), config.getProductName(), order.getId());

            return ExchangeResult.builder()
                .exchangeType((byte) 1)
                .exchangeStatus((byte) 1)
                .pointsUsed(totalPointsNeeded)
                .cashAmount(BigDecimal.ZERO) // 纯积分兑换，现金金额为0
                .quantity(param.getQuantity())
                .targetName(config.getProductName())
                .orderId(order.getId()) // 返回订单ID
                .orderSn(order.getOrderSn()) // 返回订单号
                .message("兑换成功")
                .build();

        } catch (Exception e) {
            LOGGER.error("纯积分商品兑换失败", e);
            return ExchangeResult.builder()
                .exchangeStatus((byte) 2)
                .message("兑换失败：" + e.getMessage())
                .build();
        }
    }
    
    @Override
    public boolean checkProductExchangeEligible(Long memberId, Long configId) {
        UmsPointsProductConfig config = pointsProductConfigMapper.selectByPrimaryKey(configId);
        if (config == null || config.getStatus() != 1) {
            return false;
        }
        
        // 检查库存
        if (config.getRemainingStock() <= 0) {
            return false;
        }
        
        // 检查用户积分
        UmsMember member = memberMapper.selectByPrimaryKey(memberId);
        if (member == null) {
            return false;
        }

        // 安全获取用户积分，处理null值
        Integer memberIntegration = member.getIntegration();
        int currentIntegration = memberIntegration != null ? memberIntegration : 0;
        if (currentIntegration < config.getPointsPrice()) {
            return false;
        }
        
        // 检查个人限购
        int userExchangedCount = getUserExchangedCount(memberId, configId, (byte) 1);
        if (userExchangedCount >= config.getPerPersonLimit()) {
            return false;
        }
        
        // 检查活动时间
        Date now = new Date();
        if (config.getStartTime() != null && now.before(config.getStartTime())) {
            return false;
        }
        if (config.getEndTime() != null && now.after(config.getEndTime())) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public List<PointsCouponConfigDetail> getCouponList(PointsExchangeQueryParam queryParam, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        UmsPointsCouponConfigExample example = createCouponExample(queryParam);
        List<UmsPointsCouponConfig> configs = pointsCouponConfigMapper.selectByExample(example);

        List<PointsCouponConfigDetail> details = new ArrayList<>();
        UmsMember currentMember = memberService.getCurrentMember();
        Long memberId = currentMember != null ? currentMember.getId() : null;

        for (UmsPointsCouponConfig config : configs) {
            PointsCouponConfigDetail detail = new PointsCouponConfigDetail();
            BeanUtils.copyProperties(config, detail);

            // 查询关联的优惠券详细信息
            if (config.getCouponId() != null) {
                SmsCoupon coupon = couponMapper.selectByPrimaryKey(config.getCouponId());
                if (coupon != null) {
                    // 设置优惠券详细信息
                    detail.setCouponType(coupon.getType());
                    detail.setAmount(coupon.getAmount());
                    detail.setMinPoint(coupon.getMinPoint());
                    detail.setCouponStartTime(coupon.getStartTime());
                    detail.setCouponEndTime(coupon.getEndTime());
                    detail.setUseType(coupon.getUseType());

                    // 设置优惠券类型信息（满减券或打折券）
                    if (coupon.getCouponType() != null) {
                        detail.setCouponTypeDetail(coupon.getCouponType());
                    }

                    // 设置打折率（如果是打折券）
                    if (coupon.getDiscountRate() != null) {
                        detail.setDiscountRate(coupon.getDiscountRate());
                    }
                }
            }

            if (memberId != null) {
                int userExchangedCount = getUserExchangedCount(memberId, config.getId(), (byte) 2);
                detail.setUserExchangedCount(userExchangedCount);
                detail.setCanExchange(checkCouponExchangeEligible(memberId, config.getId()));
            }

            details.add(detail);
        }

        return details;
    }
    
    @Override
    public PointsCouponConfigDetail getCouponDetail(Long configId) {
        UmsPointsCouponConfig config = pointsCouponConfigMapper.selectByPrimaryKey(configId);
        if (config == null) {
            return null;
        }

        PointsCouponConfigDetail detail = new PointsCouponConfigDetail();
        BeanUtils.copyProperties(config, detail);

        // 查询关联的优惠券详细信息
        if (config.getCouponId() != null) {
            SmsCoupon coupon = couponMapper.selectByPrimaryKey(config.getCouponId());
            if (coupon != null) {
                // 设置优惠券详细信息
                detail.setCouponType(coupon.getType());
                detail.setAmount(coupon.getAmount());
                detail.setMinPoint(coupon.getMinPoint());
                detail.setCouponStartTime(coupon.getStartTime());
                detail.setCouponEndTime(coupon.getEndTime());
                detail.setUseType(coupon.getUseType());

                // 设置优惠券类型信息（满减券或打折券）
                if (coupon.getCouponType() != null) {
                    detail.setCouponTypeDetail(coupon.getCouponType());
                }

                // 设置打折率（如果是打折券）
                if (coupon.getDiscountRate() != null) {
                    detail.setDiscountRate(coupon.getDiscountRate());
                }
            }
        }

        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember != null) {
            int userExchangedCount = getUserExchangedCount(currentMember.getId(), configId, (byte) 2);
            detail.setUserExchangedCount(userExchangedCount);
            detail.setCanExchange(checkCouponExchangeEligible(currentMember.getId(), configId));
        }

        return detail;
    }
    
    @Override
    public ExchangeResult exchangeCoupon(CouponExchangeParam param) {
        LOGGER.info("开始优惠券兑换，参数：{}", param);

        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            LOGGER.warn("用户未登录，兑换失败");
            return ExchangeResult.builder()
                .exchangeStatus((byte) 2)
                .message("请先登录")
                .build();
        }

        LOGGER.info("用户兑换优惠券，用户ID：{}，用户名：{}", currentMember.getId(), currentMember.getNickname());
        
        // 1. 校验换购配置
        UmsPointsCouponConfig config = pointsCouponConfigMapper.selectByPrimaryKey(param.getConfigId());
        if (config == null || config.getStatus() != 1) {
            return ExchangeResult.builder()
                .exchangeStatus((byte) 2)
                .message("优惠券兑换配置不存在或已禁用")
                .build();
        }
        
        // 2. 校验剩余数量
        if (config.getRemainingCount() <= 0) {
            return ExchangeResult.builder()
                .exchangeStatus((byte) 2)
                .message("优惠券已兑完")
                .build();
        }
        
        // 3. 校验积分
        if (currentMember.getIntegration() < config.getPointsPrice()) {
            return ExchangeResult.builder()
                .exchangeStatus((byte) 2)
                .message("积分不足")
                .build();
        }
        
        // 4. 校验个人限领
        int userExchangedCount = getUserExchangedCount(currentMember.getId(), param.getConfigId(), (byte) 2);
        if (userExchangedCount >= config.getPerPersonLimit()) {
            return ExchangeResult.builder()
                .exchangeStatus((byte) 2)
                .message("超过个人限领数量")
                .build();
        }
        
        try {
            // 5. 扣除积分
            updateMemberPoints(currentMember.getId(), -config.getPointsPrice(), "优惠券兑换");
            
            // 6. 减少可领数量
            reduceCouponCount(param.getConfigId(), 1);
            
            // 7. 发放优惠券
            Long couponHistoryId = issueCouponToMember(currentMember.getId(), config.getCouponId());
            
            // 8. 记录兑换日志
            UmsPointsExchangeLog log = createCouponExchangeLog(currentMember, config, param, couponHistoryId);
            int insertResult = pointsExchangeLogMapper.insertSelective(log);
            LOGGER.info("优惠券兑换记录创建结果：{}，记录ID：{}，用户ID：{}，优惠券：{}",
                       insertResult, log.getId(), currentMember.getId(), config.getCouponName());
            
            return ExchangeResult.builder()
                .exchangeType((byte) 2)
                .exchangeStatus((byte) 1)
                .pointsUsed(config.getPointsPrice())
                .couponHistoryId(couponHistoryId)
                .quantity(1)
                .targetName(config.getCouponName())
                .message("兑换成功")
                .build();
                
        } catch (Exception e) {
            LOGGER.error("优惠券兑换失败", e);
            return ExchangeResult.builder()
                .exchangeStatus((byte) 2)
                .message("兑换失败：" + e.getMessage())
                .build();
        }
    }
    
    @Override
    public boolean checkCouponExchangeEligible(Long memberId, Long configId) {
        UmsPointsCouponConfig config = pointsCouponConfigMapper.selectByPrimaryKey(configId);
        if (config == null || config.getStatus() != 1) {
            return false;
        }
        
        // 检查剩余数量
        if (config.getRemainingCount() <= 0) {
            return false;
        }
        
        // 检查用户积分
        UmsMember member = memberMapper.selectByPrimaryKey(memberId);
        if (member == null) {
            return false;
        }

        // 安全获取用户积分，处理null值
        Integer memberIntegration = member.getIntegration();
        int currentIntegration = memberIntegration != null ? memberIntegration : 0;
        if (currentIntegration < config.getPointsPrice()) {
            return false;
        }
        
        // 检查个人限领
        int userExchangedCount = getUserExchangedCount(memberId, configId, (byte) 2);
        if (userExchangedCount >= config.getPerPersonLimit()) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public List<UmsPointsExchangeLog> getUserExchangeList(Long memberId, Byte exchangeType, Integer pageNum, Integer pageSize) {
        LOGGER.info("查询用户兑换记录，用户ID：{}，兑换类型：{}，页码：{}，页大小：{}", memberId, exchangeType, pageNum, pageSize);

        PageHelper.startPage(pageNum, pageSize);
        UmsPointsExchangeLogExample example = new UmsPointsExchangeLogExample();
        UmsPointsExchangeLogExample.Criteria criteria = example.createCriteria();
        criteria.andMemberIdEqualTo(memberId);
        if (exchangeType != null) {
            criteria.andExchangeTypeEqualTo(exchangeType);
        }
        example.setOrderByClause("exchange_time desc");

        List<UmsPointsExchangeLog> result = pointsExchangeLogMapper.selectByExample(example);
        LOGGER.info("查询到兑换记录数量：{}", result.size());

        return result;
    }
    
    @Override
    public Integer getUserPointsBalance(Long memberId) {
        UmsMember member = memberMapper.selectByPrimaryKey(memberId);
        if (member != null) {
            Integer integration = member.getIntegration();
            return integration != null ? integration : 0;
        }
        return 0;
    }
    
    @Override
    public UmsPointsExchangeLog getExchangeDetail(Long exchangeId, Long memberId) {
        UmsPointsExchangeLog exchangeLog = pointsExchangeLogMapper.selectByPrimaryKey(exchangeId);
        // 权限检查：只能查看自己的兑换记录
        if (exchangeLog != null && !exchangeLog.getMemberId().equals(memberId)) {
            return null;
        }
        return exchangeLog;
    }
    
    @Override
    public ExchangePayOrderResult createExchangePayOrder(ExchangePayOrderParam param) {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            throw new RuntimeException("请先登录");
        }
        
        // 1. 校验换购配置
        UmsPointsProductConfig config = pointsProductConfigMapper.selectByPrimaryKey(param.getConfigId());
        if (config == null || config.getStatus() != 1) {
            throw new RuntimeException("商品换购配置不存在或已下架");
        }
        
        // 2. 校验SKU信息
        PmsSkuStock selectedSku = null;
        if (param.getProductSkuId() != null) {
            selectedSku = validateAndGetSku(param.getProductSkuId(), config.getProductId(), param.getQuantity());
            if (selectedSku == null) {
                throw new RuntimeException("所选商品规格不存在或库存不足");
            }
        }
        
        // 3. 校验库存（优先使用SKU库存，否则使用商品总库存）
        int availableStock = selectedSku != null ? selectedSku.getStock() : config.getRemainingStock();
        if (availableStock < param.getQuantity()) {
            throw new RuntimeException("库存不足");
        }
        
        // 3. 校验积分
        int totalPointsNeeded = config.getPointsPrice() * param.getQuantity();
        // 安全获取用户积分，处理null值
        Integer memberIntegration = currentMember.getIntegration();
        int currentIntegration = memberIntegration != null ? memberIntegration : 0;
        if (currentIntegration < totalPointsNeeded) {
            throw new RuntimeException("积分不足");
        }
        
        // 4. 校验个人限购
        int userExchangedCount = getUserExchangedCount(currentMember.getId(), param.getConfigId(), (byte) 1);
        if (userExchangedCount + param.getQuantity() > config.getPerPersonLimit()) {
            throw new RuntimeException("超过个人限购数量");
        }
        
        // 5. 校验现金金额（始终使用配置价格）
        BigDecimal unitPrice = config.getCashPrice();
        BigDecimal expectedCashAmount = unitPrice.multiply(new BigDecimal(param.getQuantity()));
        if (param.getCashAmount().compareTo(expectedCashAmount) != 0) {
            throw new RuntimeException("现金金额不正确");
        }
        
        // 6. 校验积分数量
        if (!param.getPointsAmount().equals(totalPointsNeeded)) {
            throw new RuntimeException("积分数量不正确");
        }
        
        try {
            // 7. 构建订单参数，调用现有订单服务创建订单
            OrderParam orderParam = buildExchangeOrderParam(config, param, currentMember);
            
            // 8. 调用现有的订单生成服务
            Map<String, Object> orderResult = portalOrderService.generateOrder(orderParam);
            OmsOrder order = (OmsOrder) orderResult.get("order");
            
            if (order == null || order.getOrderSn() == null) {
                throw new RuntimeException("订单创建失败");
            }
            
            String orderSn = order.getOrderSn();
            
            // 9. 构建返回结果
            ExchangePayOrderResult result = new ExchangePayOrderResult();
            result.setOrderSn(orderSn);
            result.setPayAmount(param.getCashAmount());
            result.setPointsAmount(param.getPointsAmount());
            result.setProductName(config.getProductName());
            result.setQuantity(param.getQuantity());
            
            LOGGER.info("创建积分+现金兑换订单成功，订单号：{}，用户：{}，商品：{}，数量：{}，现金：{}，积分：{}", 
                orderSn, currentMember.getId(), config.getProductName(), param.getQuantity(), 
                param.getCashAmount(), param.getPointsAmount());
            
            return result;
            
        } catch (Exception e) {
            LOGGER.error("创建积分+现金兑换订单失败", e);
            throw new RuntimeException("创建兑换订单失败：" + e.getMessage());
        }
    }
    
    @Override
    public boolean handleExchangePaySuccess(ExchangePaySuccessParam param) {
        try {
            LOGGER.info("处理积分兑换支付成功回调，订单号：{}，支付方式：{}，支付流水号：{}",
                param.getOrderSn(), param.getPayType(), param.getPaySn());

            // 1. 根据订单号查找订单
            OmsOrderExample orderExample = new OmsOrderExample();
            orderExample.createCriteria().andOrderSnEqualTo(param.getOrderSn());
            List<OmsOrder> orders = orderMapper.selectByExample(orderExample);

            if (orders.isEmpty()) {
                throw new RuntimeException("订单不存在：" + param.getOrderSn());
            }

            OmsOrder order = orders.get(0);
            if (order.getStatus() != 0) {
                LOGGER.warn("订单状态不是待支付，订单号：{}，当前状态：{}", param.getOrderSn(), order.getStatus());
                return true; // 已处理过，返回成功
            }

            // 2. 验证支付方式
            if (param.getPayType() != 1 && param.getPayType() != 2) {
                throw new RuntimeException("无效的支付方式");
            }

            // 3. 调用现有的订单支付成功逻辑，这会处理订单状态更新、库存扣减、积分扣减等
            portalOrderService.paySuccessByOrderSn(param.getOrderSn(), param.getPayType());

            LOGGER.info("积分兑换支付成功处理完成，订单号：{}，支付方式：{}",
                param.getOrderSn(), param.getPayType() == 1 ? "支付宝" : "微信支付");

            return true;

        } catch (Exception e) {
            LOGGER.error("处理积分兑换支付成功回调失败", e);
            return false;
        }
    }

    @Override
    public boolean createProductExchangeRecord(ProductExchangeParam param) {
        try {
            LOGGER.info("创建商品兑换记录，参数：{}", param);

            UmsMember currentMember = memberService.getCurrentMember();
            if (currentMember == null) {
                LOGGER.warn("用户未登录，无法创建兑换记录");
                return false;
            }

            // 获取换购配置
            UmsPointsProductConfig config = pointsProductConfigMapper.selectByPrimaryKey(param.getConfigId());
            if (config == null) {
                LOGGER.warn("商品换购配置不存在：{}", param.getConfigId());
                return false;
            }

            // 创建兑换记录（不扣积分，不减库存）
            UmsPointsExchangeLog log = createExchangeLog(currentMember, config, param);
            int insertResult = pointsExchangeLogMapper.insertSelective(log);

            LOGGER.info("商品兑换记录创建成功：{}，记录ID：{}，用户ID：{}，商品：{}",
                       insertResult > 0, log.getId(), currentMember.getId(), config.getProductName());

            return insertResult > 0;

        } catch (Exception e) {
            LOGGER.error("创建商品兑换记录失败", e);
            return false;
        }
    }

    // 私有辅助方法
    private UmsPointsProductConfigExample createProductExample(PointsExchangeQueryParam queryParam) {
        UmsPointsProductConfigExample example = new UmsPointsProductConfigExample();
        UmsPointsProductConfigExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo((byte) 1); // 只查询上架状态
        
        if (!StringUtils.isEmpty(queryParam.getKeyword())) {
            criteria.andProductNameLike("%" + queryParam.getKeyword() + "%");
        }
        
        Date now = new Date();
        criteria.andStartTimeLessThanOrEqualTo(now).andEndTimeGreaterThanOrEqualTo(now);
        
        if (queryParam.getOnlyAvailable()) {
            criteria.andRemainingStockGreaterThan(0);
        }
        
        example.setOrderByClause("sort_order asc, created_at desc");
        return example;
    }
    
    private UmsPointsCouponConfigExample createCouponExample(PointsExchangeQueryParam queryParam) {
        UmsPointsCouponConfigExample example = new UmsPointsCouponConfigExample();
        UmsPointsCouponConfigExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo((byte) 1); // 只查询启用状态
        
        if (!StringUtils.isEmpty(queryParam.getKeyword())) {
            criteria.andCouponNameLike("%" + queryParam.getKeyword() + "%");
        }
        
        Date now = new Date();
        criteria.andStartTimeLessThanOrEqualTo(now).andEndTimeGreaterThanOrEqualTo(now);
        
        if (queryParam.getOnlyAvailable()) {
            criteria.andRemainingCountGreaterThan(0);
        }
        
        example.setOrderByClause("sort_order asc, created_at desc");
        return example;
    }
    
    private int getUserExchangedCount(Long memberId, Long configId, Byte exchangeType) {
        UmsPointsExchangeLogExample example = new UmsPointsExchangeLogExample();
        UmsPointsExchangeLogExample.Criteria criteria = example.createCriteria();
        criteria.andMemberIdEqualTo(memberId);
        criteria.andTargetIdEqualTo(configId);
        criteria.andExchangeTypeEqualTo(exchangeType);
        criteria.andExchangeStatusEqualTo((byte) 1); // 只统计成功的兑换
        
        List<UmsPointsExchangeLog> logs = pointsExchangeLogMapper.selectByExample(example);
        int totalCount = 0;
        for (UmsPointsExchangeLog log : logs) {
            totalCount += log.getQuantity() != null ? log.getQuantity() : 1;
        }
        return totalCount;
    }
    
    private String getCannotExchangeReason(Long memberId, UmsPointsProductConfig config) {
        UmsMember member = memberMapper.selectByPrimaryKey(memberId);
        
        if (config.getRemainingStock() <= 0) {
            return "库存不足";
        }

        // 安全获取用户积分，处理null值
        Integer memberIntegration = member.getIntegration();
        int currentIntegration = memberIntegration != null ? memberIntegration : 0;
        if (currentIntegration < config.getPointsPrice()) {
            return "积分不足";
        }
        
        int userExchangedCount = getUserExchangedCount(memberId, config.getId(), (byte) 1);
        if (userExchangedCount >= config.getPerPersonLimit()) {
            return "已达兑换上限";
        }
        
        Date now = new Date();
        if (config.getStartTime() != null && now.before(config.getStartTime())) {
            return "活动未开始";
        }
        if (config.getEndTime() != null && now.after(config.getEndTime())) {
            return "活动已结束";
        }
        
        return "不可兑换";
    }
    
    private void updateMemberPoints(Long memberId, Integer pointsChange, String operateNote) {
        // 使用统一积分服务更新积分（积分兑换通常是扣减，不需要检查等级升级）
        boolean result = integrationService.updateIntegrationAndCheckLevel(
            memberId,
            pointsChange,
            operateNote,
            4 // 4->其他（积分兑换）
        );

        if (!result) {
            throw new RuntimeException("积分更新失败: " + operateNote);
        }
    }
    
    private void reduceProductStock(Long configId, Integer quantity) {
        UmsPointsProductConfig config = pointsProductConfigMapper.selectByPrimaryKey(configId);
        config.setRemainingStock(config.getRemainingStock() - quantity);
        config.setUpdatedAt(new Date());
        pointsProductConfigMapper.updateByPrimaryKeySelective(config);
    }
    
    private void reduceCouponCount(Long configId, Integer quantity) {
        UmsPointsCouponConfig config = pointsCouponConfigMapper.selectByPrimaryKey(configId);
        config.setRemainingCount(config.getRemainingCount() - quantity);
        config.setUpdatedAt(new Date());
        pointsCouponConfigMapper.updateByPrimaryKeySelective(config);
    }
    
    private Long issueCouponToMember(Long memberId, Long couponId) {
        // 创建优惠券发放记录
        SmsCouponHistory history = new SmsCouponHistory();
        history.setCouponId(couponId);
        history.setMemberId(memberId);
        history.setCouponCode(generateCouponCode());
        history.setGetType(1); // 主动获取
        history.setCreateTime(new Date());
        history.setUseStatus(0); // 未使用
        couponHistoryMapper.insertSelective(history);
        return history.getId();
    }
    
    private String generateCouponCode() {
        return "CP" + System.currentTimeMillis();
    }
    
    private UmsPointsExchangeLog createExchangeLog(UmsMember member, UmsPointsProductConfig config, ProductExchangeParam param) {
        UmsPointsExchangeLog log = new UmsPointsExchangeLog();
        log.setMemberId(member.getId());
        // 确保设置用户名，如果为空则使用昵称或默认值
        String username = member.getUsername();
        if (username == null || username.trim().isEmpty()) {
            username = member.getNickname();
            if (username == null || username.trim().isEmpty()) {
                username = "用户" + member.getId();
            }
        }
        log.setMemberUsername(username);
        log.setExchangeType((byte) 1); // 商品
        log.setTargetId(config.getId());
        log.setTargetName(config.getProductName());
        log.setPointsUsed(config.getPointsPrice() * param.getQuantity());
        
        // 计算现金金额，如果有SKU则使用SKU价格
        BigDecimal cashAmount = config.getCashPrice();
        String remarkWithSku = param.getRemark();
        
        if (param.getProductSkuId() != null) {
            PmsSkuStock sku = skuStockMapper.selectByPrimaryKey(param.getProductSkuId());
            if (sku != null) {
                if (sku.getPrice() != null) {
                    cashAmount = sku.getPrice();
                }
                // 在备注中记录SKU信息
                String skuInfo = String.format("SKU ID: %d, SKU编码: %s", sku.getId(), sku.getSkuCode());
                remarkWithSku = StringUtils.hasText(param.getRemark()) ? 
                    param.getRemark() + " | " + skuInfo : skuInfo;
            }
        }
        
        log.setCashAmount(cashAmount.multiply(new BigDecimal(param.getQuantity())));
        log.setQuantity(param.getQuantity());
        log.setExchangeStatus((byte) 1); // 成功
        log.setExchangeTime(new Date());
        log.setRemark(remarkWithSku);
        return log;
    }
    
    private UmsPointsExchangeLog createCouponExchangeLog(UmsMember member, UmsPointsCouponConfig config, CouponExchangeParam param, Long couponHistoryId) {
        UmsPointsExchangeLog log = new UmsPointsExchangeLog();
        log.setMemberId(member.getId());
        // 确保设置用户名，如果为空则使用昵称或默认值
        String username = member.getUsername();
        if (username == null || username.trim().isEmpty()) {
            username = member.getNickname();
            if (username == null || username.trim().isEmpty()) {
                username = "用户" + member.getId();
            }
        }
        log.setMemberUsername(username);
        log.setExchangeType((byte) 2); // 优惠券
        log.setTargetId(config.getId());
        log.setTargetName(config.getCouponName());
        log.setPointsUsed(config.getPointsPrice());
        log.setCashAmount(BigDecimal.ZERO);
        log.setQuantity(1);
        log.setCouponHistoryId(couponHistoryId);
        log.setExchangeStatus((byte) 1); // 成功
        log.setExchangeTime(new Date());
        log.setRemark(param.getRemark());
        return log;
    }
    
    /**
     * 构建积分兑换订单参数
     */
    private OrderParam buildExchangeOrderParam(UmsPointsProductConfig config, ExchangePayOrderParam param, UmsMember member) {
        OrderParam orderParam = new OrderParam();
        
        // 设置收货地址
        orderParam.setMemberReceiveAddressId(param.getAddressId());
        
        // 设置积分使用
        orderParam.setUseIntegration(param.getPointsAmount());
        
        // 设置支付方式（待支付）
        orderParam.setPayType(0);
        
        // 构建商品项
        List<CartPromotionItem> cartPromotionItemList = new ArrayList<>();
        CartPromotionItem item = new CartPromotionItem();
        
        // 设置商品基本信息
        item.setProductId(config.getProductId());
        item.setProductSkuId(param.getProductSkuId() != null ? param.getProductSkuId() : config.getProductId());
        item.setQuantity(param.getQuantity());
        
        // 积分兑换订单：商品价格设置为用户实际支付的现金金额
        // 这样订单记录的金额就是用户实际支付的现金，便于后续售后退款处理
        BigDecimal itemPrice = config.getCashPrice();
        if (param.getProductSkuId() != null) {
            PmsSkuStock sku = skuStockMapper.selectByPrimaryKey(param.getProductSkuId());
            if (sku != null && sku.getPrice() != null) {
                // 对于积分兑换，即使有SKU价格，也使用配置的现金价格
                // 因为用户实际支付的是现金价格，不是SKU的原价
                itemPrice = config.getCashPrice();
            }
        }
        item.setPrice(itemPrice);
        item.setProductName(config.getProductName());
        item.setProductPic(config.getProductPic());
        item.setProductBrand("");
        item.setProductSn("");
        item.setProductCategoryId(0L);
        item.setProductAttr("");
        item.setRealStock(config.getRemainingStock());
        
        // 设置促销信息
        item.setPromotionMessage("积分+现金兑换");
        item.setReduceAmount(BigDecimal.ZERO);
        item.setIntegration(0);
        item.setGrowth(0);
        item.setDeliveryType(0); // 快递配送
        
        cartPromotionItemList.add(item);
        orderParam.setCartPromotionItemList(cartPromotionItemList);
        
        // 设置配送方式
        orderParam.setDeliveryType(0); // 快递配送
        
        // 设置是否为送礼订单
        orderParam.setIsGift(0); // 正常订单

        // 设置订单类型为积分兑换订单
        orderParam.setOrderType(2); // 2->积分兑换订单

        return orderParam;
    }
    
    /**
     * 生成支付订单号
     */
     private String generatePayOrderSn() {
        // EPO = Exchange Pay Order
        return "EPO" + System.currentTimeMillis() + String.format("%04d", (int)(Math.random() * 10000));
    }
    
    /**
     * 获取商品的SKU列表
     */
    private List<PmsSkuStock> getProductSkuList(Long productId) {
        if (productId == null) {
            return new ArrayList<>();
        }
        
        PmsSkuStockExample example = new PmsSkuStockExample();
        PmsSkuStockExample.Criteria criteria = example.createCriteria();
        criteria.andProductIdEqualTo(productId);
        
        // 只查询有库存的SKU，提高用户体验
        criteria.andStockGreaterThan(0);
        
        example.setOrderByClause("id asc");
        
        List<PmsSkuStock> skuList = skuStockMapper.selectByExample(example);
        if (skuList == null) {
            return new ArrayList<>();
        }
        
        // 为每个SKU添加可用性标记
        for (PmsSkuStock sku : skuList) {
            // 可以在这里添加更多的可用性判断逻辑
            // 比如检查SKU是否启用、是否在促销期间等
        }
        
        return skuList;
    }
    
    /**
     * 解析商品规格信息
     * 从SKU的spData JSON字段中提取规格信息并组织成规格列表
     */
    private List<ProductSpecification> parseProductSpecifications(List<PmsSkuStock> skuList) {
        if (skuList == null || skuList.isEmpty()) {
            LOGGER.debug("SKU列表为空，返回空的规格列表");
            return new ArrayList<>();
        }
        
        Map<String, Set<String>> specMap = new HashMap<>();
        int parsedCount = 0;
        int errorCount = 0;
        
        for (PmsSkuStock sku : skuList) {
            if (StringUtils.hasText(sku.getSpData())) {
                try {
                    // 解析spData JSON，格式如：[{"key":"颜色","value":"红色"},{"key":"尺寸","value":"L"}]
                    com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(sku.getSpData());
                    
                    if (jsonNode.isArray()) {
                        for (com.fasterxml.jackson.databind.JsonNode specNode : jsonNode) {
                            if (specNode.has("key") && specNode.has("value")) {
                                String key = specNode.get("key").asText();
                                String value = specNode.get("value").asText();
                                
                                if (StringUtils.hasText(key) && StringUtils.hasText(value)) {
                                    specMap.computeIfAbsent(key, k -> new HashSet<>()).add(value);
                                }
                            }
                        }
                        parsedCount++;
                    } else {
                        LOGGER.warn("SKU规格数据格式不正确，不是数组格式，SKU ID：{}，spData：{}", sku.getId(), sku.getSpData());
                        errorCount++;
                    }
                } catch (Exception e) {
                    LOGGER.warn("解析SKU规格数据失败，SKU ID：{}，spData：{}", sku.getId(), sku.getSpData(), e);
                    errorCount++;
                }
            }
        }
        
        LOGGER.info("SKU规格解析完成，总SKU数：{}，成功解析：{}，解析失败：{}，规格类型数：{}", 
            skuList.size(), parsedCount, errorCount, specMap.size());
        
        List<ProductSpecification> specificationList = new ArrayList<>();
        for (Map.Entry<String, Set<String>> entry : specMap.entrySet()) {
            ProductSpecification spec = new ProductSpecification();
            spec.setName(entry.getKey());
            spec.setValues(new ArrayList<>(entry.getValue()));
            // 对规格值进行排序，提供更好的用户体验
            spec.getValues().sort(String::compareTo);
            specificationList.add(spec);
        }
        
        // 对规格列表进行排序，确保显示顺序一致
        specificationList.sort((a, b) -> a.getName().compareTo(b.getName()));
        
        return specificationList;
    }
    
    /**
     * 验证并获取SKU信息
     */
    private PmsSkuStock validateAndGetSku(Long skuId, Long productId, Integer quantity) {
        if (skuId == null) {
            return null;
        }
        
        PmsSkuStock sku = skuStockMapper.selectByPrimaryKey(skuId);
        if (sku == null) {
            LOGGER.warn("SKU不存在，SKU ID：{}", skuId);
            return null;
        }
        
        // 验证SKU是否属于指定商品
        if (!sku.getProductId().equals(productId)) {
            LOGGER.warn("SKU不属于指定商品，SKU ID：{}，商品ID：{}，SKU商品ID：{}", 
                skuId, productId, sku.getProductId());
            return null;
        }
        
        // 验证SKU库存
        if (sku.getStock() == null || sku.getStock() < quantity) {
            LOGGER.warn("SKU库存不足，SKU ID：{}，当前库存：{}，需要数量：{}", 
                skuId, sku.getStock(), quantity);
            return null;
        }
        
        return sku;
    }
    
    /**
     * 扣减SKU库存
     * 使用乐观锁机制确保库存扣减的准确性
     */
    private void reduceSkuStock(Long skuId, Integer quantity) {
        if (skuId == null || quantity == null || quantity <= 0) {
            throw new RuntimeException("SKU ID或扣减数量无效");
        }
        
        PmsSkuStock sku = skuStockMapper.selectByPrimaryKey(skuId);
        if (sku == null) {
            throw new RuntimeException("SKU不存在，SKU ID：" + skuId);
        }
        
        // 检查库存是否充足
        if (sku.getStock() == null || sku.getStock() < quantity) {
            throw new RuntimeException(String.format("SKU库存不足，SKU ID：%d，当前库存：%d，需要扣减：%d", 
                skuId, sku.getStock(), quantity));
        }
        
        // 扣减库存
        int originalStock = sku.getStock();
        sku.setStock(originalStock - quantity);
        
        int updateCount = skuStockMapper.updateByPrimaryKeySelective(sku);
        if (updateCount != 1) {
            throw new RuntimeException("SKU库存扣减失败，可能存在并发问题，SKU ID：" + skuId);
        }
        
        LOGGER.info("扣减SKU库存成功，SKU ID：{}，原库存：{}，扣减数量：{}，剩余库存：{}", 
            skuId, originalStock, quantity, sku.getStock());
    }
    
    /**
     * 验证SKU库存是否充足
     */
    private boolean checkSkuStockAvailable(Long skuId, Integer requiredQuantity) {
        if (skuId == null || requiredQuantity == null || requiredQuantity <= 0) {
            return false;
        }
        
        PmsSkuStock sku = skuStockMapper.selectByPrimaryKey(skuId);
        if (sku == null) {
            LOGGER.warn("SKU不存在，SKU ID：{}", skuId);
            return false;
        }
        
        if (sku.getStock() == null || sku.getStock() < requiredQuantity) {
            LOGGER.warn("SKU库存不足，SKU ID：{}，当前库存：{}，需要数量：{}", 
                skuId, sku.getStock(), requiredQuantity);
            return false;
        }
        
        return true;
    }

    /**
     * 构建纯积分兑换订单参数
     */
    private OrderParam buildPurePointsExchangeOrderParam(UmsPointsProductConfig config, ProductExchangeParam param, UmsMember member) {
        OrderParam orderParam = new OrderParam();

        // 设置收货地址
        orderParam.setMemberReceiveAddressId(param.getAddressId());

        // 设置积分使用
        int totalPointsNeeded = config.getPointsPrice() * param.getQuantity();
        orderParam.setUseIntegration(totalPointsNeeded);
        LOGGER.info("纯积分兑换订单参数构建 - 设置useIntegration: {}, 配置积分价格: {}, 数量: {}",
                   totalPointsNeeded, config.getPointsPrice(), param.getQuantity());

        // 设置支付方式（纯积分兑换不需要支付）
        orderParam.setPayType(0);

        // 构建商品项
        List<CartPromotionItem> cartPromotionItemList = new ArrayList<>();
        CartPromotionItem item = new CartPromotionItem();

        // 设置商品基本信息
        item.setProductId(config.getProductId());
        item.setProductSkuId(param.getProductSkuId() != null ? param.getProductSkuId() : config.getProductId());
        item.setQuantity(param.getQuantity());

        // 纯积分兑换：商品价格设置为0（因为用户不需要支付现金）
        item.setPrice(BigDecimal.ZERO);
        item.setProductName(config.getProductName());
        item.setProductPic(config.getProductPic());
        item.setProductBrand("");
        item.setProductSn("");
        item.setProductCategoryId(0L);
        item.setProductAttr("");
        item.setRealStock(config.getRemainingStock());

        // 设置促销信息
        item.setPromotionMessage("纯积分兑换");
        item.setReduceAmount(BigDecimal.ZERO);
        item.setIntegration(0);
        item.setGrowth(0);
        item.setDeliveryType(0); // 快递配送

        cartPromotionItemList.add(item);
        orderParam.setCartPromotionItemList(cartPromotionItemList);

        // 设置配送方式
        orderParam.setDeliveryType(0); // 快递配送

        // 设置是否为送礼订单
        orderParam.setIsGift(0); // 正常订单

        // 设置订单类型为积分兑换订单
        orderParam.setOrderType(2); // 2->积分兑换订单

        return orderParam;
    }

    /**
     * 创建带订单ID的兑换记录
     */
    private UmsPointsExchangeLog createExchangeLogWithOrder(UmsMember member, UmsPointsProductConfig config,
                                                           ProductExchangeParam param, Long orderId) {
        UmsPointsExchangeLog exchangeLog = new UmsPointsExchangeLog();
        exchangeLog.setMemberId(member.getId());
        // 确保设置用户名，如果为空则使用昵称或默认值
        String username = member.getUsername();
        if (username == null || username.trim().isEmpty()) {
            username = member.getNickname();
            if (username == null || username.trim().isEmpty()) {
                username = "用户" + member.getId();
            }
        }
        exchangeLog.setMemberUsername(username);
        exchangeLog.setExchangeType((byte) 1); // 1-商品
        exchangeLog.setTargetId(config.getProductId());
        exchangeLog.setTargetName(config.getProductName());
        exchangeLog.setPointsUsed(config.getPointsPrice() * param.getQuantity());
        exchangeLog.setCashAmount(BigDecimal.ZERO); // 纯积分兑换，现金金额为0
        exchangeLog.setQuantity(param.getQuantity());
        exchangeLog.setOrderId(orderId); // 关联订单ID
        exchangeLog.setExchangeStatus((byte) 1); // 1-成功
        exchangeLog.setExchangeTime(new Date());

        return exchangeLog;
    }
}