package com.macro.mall.selfcheck.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.domain.AlipayTradePayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.macro.mall.selfcheck.config.SelfcheckConfig;
import com.macro.mall.selfcheck.dto.PaymentCodeParam;
import com.macro.mall.selfcheck.dto.PaymentQRParam;
import com.macro.mall.selfcheck.dto.PaymentResultVO;
import com.macro.mall.selfcheck.service.AlipayPaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 支付宝支付服务实现类
 * 
 * @author macro
 * @since 1.0.0
 */
@Service
@ConditionalOnProperty(name = "selfcheck.payment.mockEnabled", havingValue = "false")
public class AlipayPaymentServiceImpl implements AlipayPaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlipayPaymentServiceImpl.class);

    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private SelfcheckConfig selfcheckConfig;

    @Override
    public PaymentResultVO generatePaymentQR(PaymentQRParam param, PaymentResultVO result) throws Exception {
        try {
            LOGGER.info("开始调用支付宝预下单API - 订单ID: {}, 金额: {}", param.getOrderId(), param.getAmount());

            // 构建支付宝预下单请求
            AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
            
            AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
            model.setOutTradeNo(result.getOrderSn()); // 使用订单号作为商户订单号
            model.setSubject("自助收银-订单支付");
            model.setBody("订单号: " + result.getOrderSn());
            model.setTotalAmount(param.getAmount().toString());
            model.setTimeoutExpress("5m"); // 5分钟超时
            
            request.setBizModel(model);
            request.setNotifyUrl(selfcheckConfig.getPayment().getAlipay().getNotifyUrl());
            request.setReturnUrl(selfcheckConfig.getPayment().getAlipay().getReturnUrl());

            // 调用支付宝预下单API
            AlipayTradePrecreateResponse response = alipayClient.execute(request);
            
            if (response.isSuccess()) {
                // 设置二维码信息
                result.setQrCodeText(response.getQrCode());
                result.setQrCodeUrl(response.getQrCode());
                result.setTransactionId(response.getOutTradeNo());

                LOGGER.info("支付宝预下单成功 - PaymentId: {}, QrCode: {}", 
                        result.getPaymentId(), response.getQrCode());
            } else {
                String errorMsg = "支付宝预下单失败: " + response.getSubMsg();
                LOGGER.error(errorMsg);
                throw new Exception(errorMsg);
            }

            return result;

        } catch (AlipayApiException e) {
            LOGGER.error("支付宝预下单异常", e);
            throw new Exception("支付宝预下单异常: " + e.getErrMsg(), e);
        }
    }

    @Override
    public PaymentResultVO processScanPayment(PaymentCodeParam param, PaymentResultVO result) throws Exception {
        try {
            LOGGER.info("开始调用支付宝付款码支付API - 订单ID: {}, 付款码: {}", 
                    param.getOrderId(), maskPaymentCode(param.getPaymentCode()));

            // 构建支付宝付款码支付请求
            AlipayTradePayRequest request = new AlipayTradePayRequest();
            
            AlipayTradePayModel model = new AlipayTradePayModel();
            model.setOutTradeNo(result.getOrderSn()); // 使用订单号作为商户订单号
            model.setSubject("自助收银-扫码支付");
            model.setBody("订单号: " + result.getOrderSn());
            model.setTotalAmount(param.getAmount().toString());
            model.setAuthCode(param.getPaymentCode());
            model.setScene("bar_code"); // 条码支付场景
            
            request.setBizModel(model);

            // 调用支付宝付款码支付API
            AlipayTradePayResponse response = alipayClient.execute(request);
            
            // 获取响应码
            String code = response.getCode();
            String subCode = response.getSubCode();
            String subMsg = response.getSubMsg();
            
            LOGGER.info("支付宝付款码支付响应 - code: {}, msg: {}, subCode: {}, subMsg: {}", 
                    code, response.getMsg(), subCode, subMsg);

            if ("10000".equals(code)) {
                // 支付成功
                result.setPayStatus("SUCCESS");
                result.setTransactionId(response.getTradeNo());
                result.setPaidAmount(param.getAmount());
                result.setPayTime(new Date());
                LOGGER.info("支付宝付款码支付成功 - PaymentId: {}, TradeNo: {}", 
                        result.getPaymentId(), response.getTradeNo());
            } else if ("10003".equals(code)) {
                // 等待用户输入密码，需要轮询查询支付结果
                result.setPayStatus("PENDING");
                result.setTransactionId(response.getTradeNo());
                LOGGER.info("支付宝付款码支付等待用户输入密码 - PaymentId: {}, TradeNo: {}", 
                        result.getPaymentId(), response.getTradeNo());
            } else {
                // 支付失败，设置失败状态和原因，返回给前端显示
                result.setPayStatus("FAILED");
                // 优先使用 subMsg（用户友好的错误信息），如果没有则使用 subCode
                String failureReason = buildFailureReason(code, subCode, subMsg);
                result.setFailureReason(failureReason);
                LOGGER.warn("支付宝付款码支付失败 - PaymentId: {}, code: {}, subCode: {}, subMsg: {}", 
                        result.getPaymentId(), code, subCode, subMsg);
            }

            return result;

        } catch (AlipayApiException e) {
            LOGGER.error("支付宝付款码支付异常", e);
            result.setPayStatus("FAILED");
            result.setFailureReason("支付宝支付异常: " + e.getErrMsg());
            return result;
        }
    }
    
    /**
     * 构建用户友好的失败原因
     * 支付宝响应码说明：
     * - 10000: 成功
     * - 10003: 等待用户输入密码
     * - 20000: 服务不可用
     * - 20001: 授权权限不足
     * - 40001: 缺少必选参数
     * - 40002: 非法参数
     * - 40004: 业务处理失败
     * - 40006: 权限不足
     */
    private String buildFailureReason(String code, String subCode, String subMsg) {
        // 如果有 subMsg，优先使用（更友好的错误描述）
        if (subMsg != null && !subMsg.isEmpty()) {
            return subMsg;
        }
        
        // 根据 code 返回通用错误信息
        switch (code) {
            case "20000":
                return "支付宝服务暂时不可用，请稍后重试";
            case "20001":
                return "授权权限不足，请联系管理员";
            case "40001":
                return "支付参数缺失，请重试";
            case "40002":
                return "支付参数错误，请刷新付款码后重试";
            case "40004":
                return "支付处理失败，请刷新付款码后重试";
            case "40006":
                return "权限不足，请联系管理员";
            default:
                return "支付失败[" + code + "]，请重试或联系工作人员";
        }
    }

    @Override
    public PaymentResultVO queryPaymentStatus(PaymentResultVO result) throws Exception {
        try {
            LOGGER.info("开始查询支付宝支付状态 - PaymentId: {}", result.getPaymentId());

            // 构建支付宝订单查询请求
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            
            AlipayTradeQueryModel model = new AlipayTradeQueryModel();
            model.setOutTradeNo(result.getOrderSn()); // 使用订单号查询
            
            request.setBizModel(model);

            // 调用支付宝订单查询API
            AlipayTradeQueryResponse response = alipayClient.execute(request);

            if (response.isSuccess()) {
                String tradeStatus = response.getTradeStatus();
                
                switch (tradeStatus) {
                    case "TRADE_SUCCESS":
                    case "TRADE_FINISHED":
                        result.setPayStatus("SUCCESS");
                        result.setTransactionId(response.getTradeNo());
                        result.setPaidAmount(new java.math.BigDecimal(response.getTotalAmount()));
                        if (response.getSendPayDate() != null) {
                            result.setPayTime(response.getSendPayDate());
                        }
                        break;
                    case "WAIT_BUYER_PAY":
                        result.setPayStatus("PENDING");
                        break;
                    case "TRADE_CLOSED":
                        result.setPayStatus("CANCELLED");
                        break;
                    default:
                        result.setPayStatus("UNKNOWN");
                        break;
                }

                LOGGER.info("支付宝支付状态查询成功 - PaymentId: {}, TradeStatus: {}, Status: {}", 
                        result.getPaymentId(), tradeStatus, result.getPayStatus());
            } else {
                LOGGER.warn("支付宝支付状态查询失败 - PaymentId: {}, 错误: {}", 
                        result.getPaymentId(), response.getSubMsg());
            }

            return result;

        } catch (AlipayApiException e) {
            LOGGER.error("支付宝支付状态查询异常", e);
            throw new Exception("支付宝支付状态查询失败: " + e.getErrMsg(), e);
        }
    }

    @Override
    public String handlePaymentNotify(String notifyData) {
        try {
            LOGGER.info("收到支付宝支付回调通知");

            // 解析回调参数
            Map<String, String> params = parseNotifyParams(notifyData);
            
            // 验证回调签名
            SelfcheckConfig.Payment.Alipay alipayConfig = selfcheckConfig.getPayment().getAlipay();
            boolean signVerified = AlipaySignature.rsaCheckV1(
                params, 
                alipayConfig.getAlipayPublicKey(), 
                alipayConfig.getCharset(), 
                alipayConfig.getSignType()
            );

            if (signVerified) {
                String outTradeNo = params.get("out_trade_no");
                String tradeNo = params.get("trade_no");
                String tradeStatus = params.get("trade_status");
                String totalAmount = params.get("total_amount");

                LOGGER.info("支付宝支付回调验证成功 - OutTradeNo: {}, TradeNo: {}, TradeStatus: {}, TotalAmount: {}", 
                        outTradeNo, tradeNo, tradeStatus, totalAmount);

                // 根据交易状态处理业务逻辑
                if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                    // TODO: 在这里更新订单状态到数据库
                    // 可以发送MQ消息通知其他服务
                }
                
                return "success";
            } else {
                LOGGER.warn("支付宝支付回调签名验证失败");
                return "failure";
            }

        } catch (Exception e) {
            LOGGER.error("支付宝支付回调处理失败", e);
            return "failure";
        }
    }

    /**
     * 解析回调参数
     */
    private Map<String, String> parseNotifyParams(String notifyData) {
        // TODO: 实现参数解析逻辑
        // 这里应该根据实际的回调数据格式进行解析
        // 可能是表单数据或JSON数据
        return new java.util.HashMap<>();
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