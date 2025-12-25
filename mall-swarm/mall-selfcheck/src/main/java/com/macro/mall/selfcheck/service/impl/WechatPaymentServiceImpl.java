package com.macro.mall.selfcheck.service.impl;

import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.order.WxPayNativeOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayMicropayRequest;
import com.github.binarywang.wxpay.bean.request.WxPayOrderQueryRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayMicropayResult;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryResult;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.macro.mall.selfcheck.dto.PaymentCodeParam;
import com.macro.mall.selfcheck.dto.PaymentQRParam;
import com.macro.mall.selfcheck.dto.PaymentResultVO;
import com.macro.mall.selfcheck.service.WechatPaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 微信支付服务实现类
 * 
 * @author macro
 * @since 1.0.0
 */
@Service
@ConditionalOnProperty(name = "selfcheck.payment.mockEnabled", havingValue = "false")
public class WechatPaymentServiceImpl implements WechatPaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WechatPaymentServiceImpl.class);

    @Autowired
    private WxPayService wxPayService;

    @Override
    public PaymentResultVO generatePaymentQR(PaymentQRParam param, PaymentResultVO result) throws Exception {
        try {
            LOGGER.info("开始调用微信统一下单API - 订单ID: {}, 金额: {}", param.getOrderId(), param.getAmount());

            // 构建统一下单请求
            WxPayUnifiedOrderRequest request = WxPayUnifiedOrderRequest.newBuilder()
                .outTradeNo(result.getOrderSn()) // 使用订单号作为商户订单号
                .body("自助收银-订单支付")
                .detail("订单号: " + result.getOrderSn())
                .totalFee(param.getAmount().multiply(new java.math.BigDecimal(100)).intValue()) // 转换为分
                .spbillCreateIp("127.0.0.1")
                .tradeType(WxPayConstants.TradeType.NATIVE)
                .productId(param.getOrderId().toString())
                .build();

            // 调用微信统一下单API
            WxPayNativeOrderResult wxResult = wxPayService.createOrder(request);
            
            // 设置二维码信息
            result.setQrCodeText(wxResult.getCodeUrl());
            result.setQrCodeUrl(wxResult.getCodeUrl());
            result.setTransactionId(wxResult.getCodeUrl()); // 对于二维码支付，使用 CodeUrl 作为标识

            LOGGER.info("微信统一下单成功 - PaymentId: {}, CodeUrl: {}", 
                    result.getPaymentId(), wxResult.getCodeUrl());

            return result;

        } catch (WxPayException e) {
            LOGGER.error("微信统一下单失败 - 错误码: {}, 错误信息: {}", e.getErrCode(), e.getErrCodeDes(), e);
            throw new Exception("微信支付下单失败: " + e.getErrCodeDes(), e);
        }
    }

    @Override
    public PaymentResultVO processScanPayment(PaymentCodeParam param, PaymentResultVO result) throws Exception {
        try {
            LOGGER.info("开始调用微信付款码支付API - 订单ID: {}, 付款码: {}", 
                    param.getOrderId(), maskPaymentCode(param.getPaymentCode()));

            // 构建付款码支付请求
            WxPayMicropayRequest request = WxPayMicropayRequest.newBuilder()
                .outTradeNo(result.getOrderSn()) // 使用订单号作为商户订单号
                .body("自助收银-扫码支付")
                .detail("订单号: " + result.getOrderSn())
                .totalFee(param.getAmount().multiply(new java.math.BigDecimal(100)).intValue()) // 转换为分
                .spbillCreateIp("127.0.0.1")
                .authCode(param.getPaymentCode())
                .build();

            // 调用微信付款码支付API
            WxPayMicropayResult wxResult = wxPayService.micropay(request);

            // 根据返回结果更新支付状态
            if ("SUCCESS".equals(wxResult.getReturnCode()) && "SUCCESS".equals(wxResult.getResultCode())) {
                result.setPayStatus("SUCCESS");
                result.setTransactionId(wxResult.getTransactionId());
                result.setPaidAmount(param.getAmount());
                result.setPayTime(new Date());

                LOGGER.info("微信付款码支付成功 - PaymentId: {}, TransactionId: {}", 
                        result.getPaymentId(), wxResult.getTransactionId());
            } else {
                result.setPayStatus("FAILED");
                String errorMsg = wxResult.getErrCodeDes() != null ? wxResult.getErrCodeDes() : "支付失败";
                
                LOGGER.warn("微信付款码支付失败 - PaymentId: {}, 错误: {}", 
                        result.getPaymentId(), errorMsg);
                
                throw new Exception("微信支付失败: " + errorMsg);
            }

            return result;

        } catch (WxPayException e) {
            LOGGER.error("微信付款码支付异常 - 错误码: {}, 错误信息: {}", e.getErrCode(), e.getErrCodeDes(), e);
            result.setPayStatus("FAILED");
            throw new Exception("微信支付异常: " + e.getErrCodeDes(), e);
        }
    }

    @Override
    public PaymentResultVO queryPaymentStatus(PaymentResultVO result) throws Exception {
        try {
            LOGGER.info("开始查询微信支付状态 - PaymentId: {}", result.getPaymentId());

            // 构建订单查询请求
            WxPayOrderQueryRequest request = WxPayOrderQueryRequest.newBuilder()
                .outTradeNo(result.getOrderSn()) // 使用订单号查询
                .build();

            // 调用微信订单查询API
            WxPayOrderQueryResult wxResult = wxPayService.queryOrder(request);

            // 根据查询结果更新支付状态
            if ("SUCCESS".equals(wxResult.getReturnCode()) && "SUCCESS".equals(wxResult.getResultCode())) {
                String tradeState = wxResult.getTradeState();
                
                switch (tradeState) {
                    case "SUCCESS":
                        result.setPayStatus("SUCCESS");
                        result.setTransactionId(wxResult.getTransactionId());
                        result.setPaidAmount(new java.math.BigDecimal(wxResult.getTotalFee()).divide(new java.math.BigDecimal(100)));
                        result.setPayTime(new Date()); // 使用当前时间作为支付时间
                        break;
                    case "REFUND":
                        result.setPayStatus("REFUNDED");
                        break;
                    case "NOTPAY":
                        result.setPayStatus("PENDING");
                        break;
                    case "CLOSED":
                        result.setPayStatus("CANCELLED");
                        break;
                    case "REVOKED":
                        result.setPayStatus("CANCELLED");
                        break;
                    case "USERPAYING":
                        result.setPayStatus("PENDING");
                        break;
                    case "PAYERROR":
                        result.setPayStatus("FAILED");
                        break;
                    default:
                        result.setPayStatus("UNKNOWN");
                        break;
                }

                LOGGER.info("微信支付状态查询成功 - PaymentId: {}, TradeState: {}, Status: {}", 
                        result.getPaymentId(), tradeState, result.getPayStatus());
            } else {
                LOGGER.warn("微信支付状态查询失败 - PaymentId: {}, 错误: {}", 
                        result.getPaymentId(), wxResult.getErrCodeDes());
            }

            return result;

        } catch (WxPayException e) {
            LOGGER.error("微信支付状态查询异常 - 错误码: {}, 错误信息: {}", e.getErrCode(), e.getErrCodeDes(), e);
            throw new Exception("微信支付状态查询失败: " + e.getErrCodeDes(), e);
        }
    }

    @Override
    public String handlePaymentNotify(String notifyData) {
        try {
            LOGGER.info("收到微信支付回调通知");

            // 解析微信支付回调数据
            WxPayOrderNotifyResult notifyResult = wxPayService.parseOrderNotifyResult(notifyData);

            // 验证回调数据的有效性
            if (notifyResult != null && "SUCCESS".equals(notifyResult.getResultCode())) {
                String outTradeNo = notifyResult.getOutTradeNo();
                String transactionId = notifyResult.getTransactionId();
                Integer totalFee = notifyResult.getTotalFee();

                LOGGER.info("微信支付回调验证成功 - OutTradeNo: {}, TransactionId: {}, TotalFee: {}", 
                        outTradeNo, transactionId, totalFee);

                // TODO: 在这里更新订单状态到数据库
                // 可以发送MQ消息通知其他服务
                
                return WxPayNotifyResponse.success("OK");
            } else {
                LOGGER.warn("微信支付回调验证失败 - ResultCode: {}", 
                        notifyResult != null ? notifyResult.getResultCode() : "null");
                return WxPayNotifyResponse.fail("验证失败");
            }

        } catch (WxPayException e) {
            LOGGER.error("微信支付回调处理失败", e);
            return WxPayNotifyResponse.fail("处理失败");
        }
    }

    /**
     * 脱敏付款码
     */
    private String maskPaymentCode(String paymentCode) {
        if (paymentCode == null || paymentCode.length() < 8) {
            return paymentCode;
        }
        return paymentCode.substring(0, 4) + "****" + paymentCode.substring(paymentCode.length() - 4);
    }
} 