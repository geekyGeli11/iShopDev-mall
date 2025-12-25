package com.macro.mall.selfcheck.service;

import com.macro.mall.selfcheck.dto.PaymentCodeParam;
import com.macro.mall.selfcheck.dto.PaymentQRParam;
import com.macro.mall.selfcheck.dto.PaymentResultVO;

/**
 * 支付宝支付服务接口
 * 
 * @author macro
 * @since 1.0.0
 */
public interface AlipayPaymentService {

    /**
     * 生成支付宝支付二维码
     * 
     * @param param 支付参数
     * @param result 支付结果对象
     * @return 更新后的支付结果
     * @throws Exception 支付异常
     */
    PaymentResultVO generatePaymentQR(PaymentQRParam param, PaymentResultVO result) throws Exception;

    /**
     * 处理支付宝扫码支付
     * 
     * @param param 扫码支付参数
     * @param result 支付结果对象
     * @return 更新后的支付结果
     * @throws Exception 支付异常
     */
    PaymentResultVO processScanPayment(PaymentCodeParam param, PaymentResultVO result) throws Exception;

    /**
     * 查询支付宝支付状态
     * 
     * @param result 支付结果对象
     * @return 更新后的支付结果
     * @throws Exception 支付异常
     */
    PaymentResultVO queryPaymentStatus(PaymentResultVO result) throws Exception;

    /**
     * 处理支付宝支付回调
     * 
     * @param notifyData 回调数据
     * @return 处理结果
     */
    String handlePaymentNotify(String notifyData);
} 