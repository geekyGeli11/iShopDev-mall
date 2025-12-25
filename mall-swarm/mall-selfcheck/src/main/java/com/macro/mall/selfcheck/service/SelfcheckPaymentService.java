package com.macro.mall.selfcheck.service;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.selfcheck.dto.PaymentCodeParam;
import com.macro.mall.selfcheck.dto.PaymentQRParam;
import com.macro.mall.selfcheck.dto.PaymentResultVO;

/**
 * 自助收银支付服务
 * 
 * @author macro
 * @since 1.0.0
 */
public interface SelfcheckPaymentService {

    /**
     * 生成收款二维码
     * 支持微信和支付宝收款二维码生成
     * 
     * @param param 二维码生成参数
     * @return 包含二维码信息的支付结果
     */
    CommonResult<PaymentResultVO> generatePaymentQR(PaymentQRParam param);

    /**
     * 扫描用户付款码进行支付
     * 用于自助收银场景，扫描用户微信/支付宝付款码完成支付
     * 
     * @param param 付款码支付参数
     * @return 支付结果
     */
    CommonResult<PaymentResultVO> scanPaymentCode(PaymentCodeParam param);

    /**
     * 查询支付状态
     * 根据支付ID查询支付状态，用于支付状态轮询
     * 
     * @param paymentId 支付ID
     * @return 支付结果
     */
    CommonResult<PaymentResultVO> getPaymentStatus(String paymentId);

    /**
     * 取消支付
     * 取消未完成的支付订单
     * 
     * @param paymentId 支付ID
     * @return 取消结果
     */
    CommonResult<Void> cancelPayment(String paymentId);

    /**
     * 处理支付回调通知
     * 处理微信/支付宝的支付结果回调通知
     * 
     * @param payType 支付类型（WECHAT/ALIPAY）
     * @param notifyData 回调数据
     * @return 处理结果
     */
    String handlePaymentNotify(String payType, String notifyData);

    /**
     * 退款处理
     * 处理支付退款请求
     * 
     * @param paymentId 支付ID
     * @param refundAmount 退款金额
     * @param reason 退款原因
     * @return 退款结果
     */
    CommonResult<PaymentResultVO> refundPayment(String paymentId, java.math.BigDecimal refundAmount, String reason);

    /**
     * 验证付款码格式
     * 验证用户输入的付款码是否符合微信/支付宝格式要求
     * 
     * @param paymentCode 付款码
     * @return 验证结果，包含支付类型信息
     */
    CommonResult<String> validatePaymentCode(String paymentCode);
} 