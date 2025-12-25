package com.macro.mall.portal.service;

import com.macro.mall.portal.domain.ExchangeResult;
import com.macro.mall.portal.domain.ExchangePayOrderResult;
import com.macro.mall.portal.domain.PointsCouponConfigDetail;
import com.macro.mall.portal.domain.PointsProductConfigDetail;
import com.macro.mall.portal.dto.CouponExchangeParam;
import com.macro.mall.portal.dto.ExchangePayOrderParam;
import com.macro.mall.portal.dto.ExchangePaySuccessParam;
import com.macro.mall.portal.dto.PointsExchangeQueryParam;
import com.macro.mall.portal.dto.ProductExchangeParam;
import com.macro.mall.model.UmsPointsExchangeLog;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 积分换购Service
 * Created by macro on 2024/01/20.
 */
public interface UmsPointsExchangeService {
    
    /**
     * 获取换购商品列表
     */
    List<PointsProductConfigDetail> getProductList(PointsExchangeQueryParam queryParam, Integer pageNum, Integer pageSize);
    
    /**
     * 获取换购商品详情
     */
    PointsProductConfigDetail getProductDetail(Long configId);
    
    /**
     * 兑换商品
     */
    @Transactional
    ExchangeResult exchangeProduct(ProductExchangeParam param);
    
    /**
     * 检查用户商品兑换资格
     */
    boolean checkProductExchangeEligible(Long memberId, Long configId);
    
    /**
     * 获取换购优惠券列表
     */
    List<PointsCouponConfigDetail> getCouponList(PointsExchangeQueryParam queryParam, Integer pageNum, Integer pageSize);
    
    /**
     * 获取换购优惠券详情
     */
    PointsCouponConfigDetail getCouponDetail(Long configId);
    
    /**
     * 兑换优惠券
     */
    @Transactional
    ExchangeResult exchangeCoupon(CouponExchangeParam param);
    
    /**
     * 检查用户优惠券兑换资格
     */
    boolean checkCouponExchangeEligible(Long memberId, Long configId);
    
    /**
     * 获取用户兑换记录
     */
    List<UmsPointsExchangeLog> getUserExchangeList(Long memberId, Byte exchangeType, Integer pageNum, Integer pageSize);
    
    /**
     * 获取用户积分余额
     */
    Integer getUserPointsBalance(Long memberId);
    
    /**
     * 获取兑换记录详情（只能查看自己的记录）
     */
    UmsPointsExchangeLog getExchangeDetail(Long exchangeId, Long memberId);
    
    /**
     * 创建积分兑换支付订单
     */
    @Transactional
    ExchangePayOrderResult createExchangePayOrder(ExchangePayOrderParam param);
    
    /**
     * 处理积分兑换支付成功回调
     */
    @Transactional
    boolean handleExchangePaySuccess(ExchangePaySuccessParam param);

    /**
     * 创建商品兑换记录（仅记录，不扣积分库存）
     */
    boolean createProductExchangeRecord(ProductExchangeParam param);
} 