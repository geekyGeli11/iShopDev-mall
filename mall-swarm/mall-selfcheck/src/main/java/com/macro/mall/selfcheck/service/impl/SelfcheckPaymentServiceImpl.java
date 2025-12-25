package com.macro.mall.selfcheck.service.impl;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.api.ResultCode;
import com.macro.mall.selfcheck.config.SelfcheckConfig;
import com.macro.mall.selfcheck.dto.PaymentCodeParam;
import com.macro.mall.selfcheck.dto.PaymentQRParam;
import com.macro.mall.selfcheck.dto.PaymentResultVO;
import com.macro.mall.selfcheck.service.SelfcheckPaymentService;
import com.macro.mall.selfcheck.service.SelfcheckOrderService;
import com.macro.mall.selfcheck.service.WechatPaymentService;
import com.macro.mall.selfcheck.service.AlipayPaymentService;
import com.macro.mall.selfcheck.constants.PayTypeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.HashMap;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * 自助收银支付服务实现
 * 
 * @author macro
 * @since 1.0.0
 */
@Service
public class SelfcheckPaymentServiceImpl implements SelfcheckPaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SelfcheckPaymentServiceImpl.class);

    @Autowired
    private SelfcheckConfig selfcheckConfig;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired(required = false)
    private WechatPaymentService wechatPaymentService;

    @Autowired(required = false)
    private AlipayPaymentService alipayPaymentService;

    @Autowired
    private SelfcheckOrderService orderService;

    // 微信付款码正则表达式：1开头，18位数字
    private static final Pattern WECHAT_PAYMENT_CODE_PATTERN = Pattern.compile("^1[0-5]\\d{16}$");
    
    // 支付宝付款码正则表达式：25-30开头，18位数字
    private static final Pattern ALIPAY_PAYMENT_CODE_PATTERN = Pattern.compile("^(25|26|27|28|29|30)\\d{16}$");

    @Override
    public CommonResult<PaymentResultVO> generatePaymentQR(PaymentQRParam param) {
        try {
            LOGGER.info("开始生成支付二维码，订单ID：{}，金额：{}，支付方式：{}", 
                    param.getOrderId(), param.getAmount(), param.getPayType());

            // 获取订单信息，获取订单号
            com.macro.mall.model.OmsOrder order = orderService.getOrderById(param.getOrderId());
            if (order == null) {
                return CommonResult.failed("订单不存在");
            }
            String orderSn = order.getOrderSn();

            // 生成支付ID（用于内部缓存管理）
            String paymentId = generatePaymentId();

            PaymentResultVO result = new PaymentResultVO();
            result.setPaymentId(paymentId);
            result.setOrderId(param.getOrderId());
            result.setOrderSn(orderSn); // 设置订单号，用于支付宝/微信商户订单号
            result.setPayType(param.getPayType());
            result.setAmount(param.getAmount());
            result.setPayStatus("PENDING");
            result.setCreateTime(new Date());
            result.setTerminalCode(param.getTerminalCode());

            // 检查是否启用模拟支付
            if (selfcheckConfig.getPayment().getMockEnabled()) {
                result = generateMockPaymentQR(param, result);
            } else {
                if ("WECHAT".equals(param.getPayType())) {
                    if (wechatPaymentService != null) {
                        result = wechatPaymentService.generatePaymentQR(param, result);
                    } else {
                        return CommonResult.failed("微信支付服务未启用");
                    }
                } else if ("ALIPAY".equals(param.getPayType())) {
                    if (alipayPaymentService != null) {
                        result = alipayPaymentService.generatePaymentQR(param, result);
                    } else {
                        return CommonResult.failed("支付宝支付服务未启用");
                    }
                } else {
                    return CommonResult.failed("不支持的支付方式");
                }
            }

            // 缓存支付信息
            cachePaymentInfo(paymentId, result);

            LOGGER.info("生成支付二维码成功，支付ID：{}", paymentId);
            return CommonResult.success(result);

        } catch (Exception e) {
            LOGGER.error("生成支付二维码失败", e);
            return CommonResult.failed("生成支付二维码失败：" + e.getMessage());
        }
    }

    @Override
    public CommonResult<PaymentResultVO> scanPaymentCode(PaymentCodeParam param) {
        try {
            LOGGER.info("开始扫码支付，订单ID：{}，金额：{}，付款码：{}",
                    param.getOrderId(), param.getAmount(), maskPaymentCode(param.getPaymentCode()));

            // 检查订单是否已经在支付中或已支付
            com.macro.mall.model.OmsOrder order = orderService.getOrderById(param.getOrderId());
            if (order == null) {
                LOGGER.warn("订单不存在：orderId={}", param.getOrderId());
                return CommonResult.failed("订单不存在");
            }

            if (order.getStatus() != 0) {
                LOGGER.warn("订单状态不是待付款，无法支付：orderId={}, status={}", param.getOrderId(), order.getStatus());
                if (order.getStatus() == 1 || order.getStatus() == 3) {
                    return CommonResult.failed("订单已支付，请勿重复支付");
                } else {
                    return CommonResult.failed("订单状态异常，无法支付");
                }
            }

            // 验证付款码格式
            CommonResult<String> validateResult = validatePaymentCode(param.getPaymentCode());
            if (validateResult.getCode() != 200) {
                return CommonResult.failed(validateResult.getMessage());
            }

            // 验证订单金额
            CommonResult<String> amountValidateResult = validateOrderAmount(param.getOrderId(), param.getAmount());
            if (amountValidateResult.getCode() != 200) {
                return CommonResult.failed(amountValidateResult.getMessage());
            }

            // 生成支付ID（用于内部缓存管理）
            String paymentId = generatePaymentId();

            PaymentResultVO result = new PaymentResultVO();
            result.setPaymentId(paymentId);
            result.setOrderId(param.getOrderId());
            result.setOrderSn(order.getOrderSn()); // 设置订单号，用于支付宝/微信商户订单号
            result.setPayType(param.getPayType());
            result.setAmount(param.getAmount());
            result.setPayStatus("PENDING");
            result.setCreateTime(new Date());
            result.setTerminalCode(param.getTerminalCode());

            // 检查是否启用模拟支付
            if (selfcheckConfig.getPayment().getMockEnabled()) {
                result = processMockScanPayment(param, result);
            } else {
                if ("WECHAT".equals(param.getPayType())) {
                    if (wechatPaymentService != null) {
                        result = wechatPaymentService.processScanPayment(param, result);
                    } else {
                        return CommonResult.failed("微信支付服务未启用");
                    }
                } else if ("ALIPAY".equals(param.getPayType())) {
                    if (alipayPaymentService != null) {
                        result = alipayPaymentService.processScanPayment(param, result);
                    } else {
                        return CommonResult.failed("支付宝支付服务未启用");
                    }
                } else {
                    return CommonResult.failed("不支持的支付方式");
                }
                
                // 如果真实支付立即返回成功状态，更新订单状态
                LOGGER.info("真实支付处理完成，支付状态：{}，订单ID：{}", result.getPayStatus(), result.getOrderId());
                if ("SUCCESS".equals(result.getPayStatus())) {
                    LOGGER.info("支付成功，开始更新订单状态：orderId={}, payType={}, transactionId={}",
                            result.getOrderId(), result.getPayType(), result.getTransactionId());
                    handlePaymentSuccess(result, true);
                } else {
                    LOGGER.warn("支付未成功，状态：{}，订单ID：{}，不更新订单状态",
                            result.getPayStatus(), result.getOrderId());
                }
            }

            // 缓存支付信息
            cachePaymentInfo(paymentId, result);

            LOGGER.info("扫码支付处理完成，支付ID：{}，状态：{}", paymentId, result.getPayStatus());
            return CommonResult.success(result);

        } catch (Exception e) {
            LOGGER.error("扫码支付失败", e);
            return CommonResult.failed("扫码支付失败：" + e.getMessage());
        }
    }

    @Override
    public CommonResult<PaymentResultVO> getPaymentStatus(String paymentId) {
        try {
            // 从缓存获取支付信息
            PaymentResultVO result = getCachedPaymentInfo(paymentId);
            if (result == null) {
                return CommonResult.failed("支付信息不存在");
            }

            // 如果已经完成（成功或失败），直接返回
            if ("SUCCESS".equals(result.getPayStatus()) || 
                "FAILED".equals(result.getPayStatus()) ||
                "CANCELLED".equals(result.getPayStatus())) {
                return CommonResult.success(result);
            }

            // 处理模拟支付状态查询
            if (selfcheckConfig.getPayment().getMockEnabled()) {
                result = handleMockPaymentStatusQuery(result);
            } else {
            // 查询真实支付状态
                String previousStatus = result.getPayStatus();
            if ("WECHAT".equals(result.getPayType())) {
                    if (wechatPaymentService != null) {
                        result = wechatPaymentService.queryPaymentStatus(result);
                        LOGGER.info("查询微信支付状态完成，支付ID：{}，状态：{}", paymentId, result.getPayStatus());
                    } else {
                        LOGGER.warn("微信支付服务未启用，无法查询支付状态");
                    }
            } else if ("ALIPAY".equals(result.getPayType())) {
                    if (alipayPaymentService != null) {
                        result = alipayPaymentService.queryPaymentStatus(result);
                        LOGGER.info("查询支付宝支付状态完成，支付ID：{}，状态：{}", paymentId, result.getPayStatus());
                    } else {
                        LOGGER.warn("支付宝支付服务未启用，无法查询支付状态");
                    }
                }
                
                // 如果支付状态从其他状态变更为SUCCESS，更新订单状态
                if (!"SUCCESS".equals(previousStatus) && "SUCCESS".equals(result.getPayStatus())) {
                    handlePaymentSuccess(result, true);
                }
            }

            // 更新缓存
            cachePaymentInfo(paymentId, result);

            return CommonResult.success(result);

        } catch (Exception e) {
            LOGGER.error("查询支付状态失败", e);
            return CommonResult.failed("查询支付状态失败：" + e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> cancelPayment(String paymentId) {
        try {
            PaymentResultVO result = getCachedPaymentInfo(paymentId);
            if (result == null) {
                return CommonResult.failed("支付信息不存在");
            }

            if ("SUCCESS".equals(result.getPayStatus())) {
                return CommonResult.failed("支付已完成，无法取消");
            }

            // 取消支付
            result.setPayStatus("CANCELLED");
            cachePaymentInfo(paymentId, result);

            LOGGER.info("取消支付成功，支付ID：{}", paymentId);
            return CommonResult.success(null);

        } catch (Exception e) {
            LOGGER.error("取消支付失败", e);
            return CommonResult.failed("取消支付失败：" + e.getMessage());
        }
    }

    @Override
    public String handlePaymentNotify(String payType, String notifyData) {
        try {
            LOGGER.info("收到支付回调通知，支付类型：{}", payType);

            // 验证回调安全性
            if (!validateNotifySecurity(payType, notifyData)) {
                logNotifyProcess(payType, false, "安全验证失败", null, null);
                return "FAIL";
            }

            if ("WECHAT".equals(payType)) {
                if (wechatPaymentService != null) {
                    return wechatPaymentService.handlePaymentNotify(notifyData);
                } else {
                return handleWechatNotify(notifyData);
                }
            } else if ("ALIPAY".equals(payType)) {
                if (alipayPaymentService != null) {
                    return alipayPaymentService.handlePaymentNotify(notifyData);
                } else {
                return handleAlipayNotify(notifyData);
                }
            } else {
                LOGGER.warn("不支持的支付类型：{}", payType);
                logNotifyProcess(payType, false, "不支持的支付类型", null, null);
                return "FAIL";
            }

        } catch (Exception e) {
            LOGGER.error("处理支付回调失败", e);
            logNotifyProcess(payType, false, "处理异常: " + e.getMessage(), null, null);
            return "FAIL";
        }
    }

    @Override
    public CommonResult<PaymentResultVO> refundPayment(String paymentId, BigDecimal refundAmount, String reason) {
        try {
            PaymentResultVO result = getCachedPaymentInfo(paymentId);
            if (result == null) {
                return CommonResult.failed("支付信息不存在");
            }

            if (!"SUCCESS".equals(result.getPayStatus())) {
                return CommonResult.failed("只能对成功的支付进行退款");
            }

            if (refundAmount.compareTo(result.getAmount()) > 0) {
                return CommonResult.failed("退款金额不能超过支付金额");
            }

            // TODO: 实现真实的退款逻辑
            LOGGER.info("退款处理成功，支付ID：{}，退款金额：{}", paymentId, refundAmount);
            return CommonResult.success(result);

        } catch (Exception e) {
            LOGGER.error("退款处理失败", e);
            return CommonResult.failed("退款处理失败：" + e.getMessage());
        }
    }

    @Override
    public CommonResult<String> validatePaymentCode(String paymentCode) {
        if (paymentCode == null || paymentCode.trim().isEmpty()) {
            return CommonResult.failed("付款码不能为空");
        }

        paymentCode = paymentCode.trim();

        if (WECHAT_PAYMENT_CODE_PATTERN.matcher(paymentCode).matches()) {
            return CommonResult.success("WECHAT");
        } else if (ALIPAY_PAYMENT_CODE_PATTERN.matcher(paymentCode).matches()) {
            return CommonResult.success("ALIPAY");
        } else {
            return CommonResult.failed("付款码格式不正确");
        }
    }

    /**
     * 验证订单金额
     *
     * @param orderId 订单ID
     * @param paymentAmount 支付金额
     * @return 验证结果
     */
    private CommonResult<String> validateOrderAmount(Long orderId, BigDecimal paymentAmount) {
        try {
            // 查询订单
            com.macro.mall.model.OmsOrder order = orderService.getOrderById(orderId);
            if (order == null) {
                LOGGER.warn("订单不存在：orderId={}", orderId);
                return CommonResult.failed("订单不存在");
            }

            // 检查订单状态
            if (order.getStatus() != 0) {
                LOGGER.warn("订单状态不是待付款：orderId={}, status={}", orderId, order.getStatus());
                return CommonResult.failed("订单状态不正确，无法支付");
            }

            // 验证支付金额与订单应付金额是否一致（允许0.01元的误差）
            BigDecimal orderPayAmount = order.getPayAmount();
            if (orderPayAmount == null) {
                LOGGER.warn("订单应付金额为空：orderId={}", orderId);
                return CommonResult.failed("订单金额异常");
            }

            BigDecimal diff = paymentAmount.subtract(orderPayAmount).abs();
            if (diff.compareTo(new BigDecimal("0.01")) > 0) {
                LOGGER.warn("支付金额与订单金额不一致：orderId={}, orderPayAmount={}, paymentAmount={}, diff={}",
                        orderId, orderPayAmount, paymentAmount, diff);
                return CommonResult.failed(String.format("支付金额不正确，订单应付%.2f元，实际支付%.2f元",
                        orderPayAmount.doubleValue(), paymentAmount.doubleValue()));
            }

            LOGGER.info("订单金额验证通过：orderId={}, orderPayAmount={}, paymentAmount={}",
                    orderId, orderPayAmount, paymentAmount);
            return CommonResult.success("验证通过");

        } catch (Exception e) {
            LOGGER.error("验证订单金额失败：orderId={}, paymentAmount={}", orderId, paymentAmount, e);
            return CommonResult.failed("验证订单金额失败：" + e.getMessage());
        }
    }

    /**
     * 生成模拟支付二维码
     */
    private PaymentResultVO generateMockPaymentQR(PaymentQRParam param, PaymentResultVO result) {
        // 模拟二维码内容
        String qrContent = String.format("mock://%s/pay?id=%s&amount=%s", 
                param.getPayType().toLowerCase(), result.getPaymentId(), param.getAmount());
        
        result.setQrCodeText(qrContent);
        result.setQrCodeUrl("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==");
        
        LOGGER.info("生成模拟支付二维码：{}", qrContent);
        return result;
    }

    /**
     * 处理模拟扫码支付
     */
    private PaymentResultVO processMockScanPayment(PaymentCodeParam param, PaymentResultVO result) {
        // 根据付款码最后一位数字决定模拟行为
        String paymentCode = param.getPaymentCode();
        char lastDigit = paymentCode.charAt(paymentCode.length() - 1);
        
        // 填充基本信息（无论什么状态都需要填充）
        // 填充订单编号（模拟格式）
        if (result.getOrderSn() == null) {
            result.setOrderSn("SC" + System.currentTimeMillis());
        }
        
        // 设置备注信息
        if (param.getRemark() != null) {
            result.setRemark(param.getRemark());
        }
        
        if (lastDigit == '0' || lastDigit == '1' || lastDigit == '2') {
            // 末尾是0、1、2的付款码：模拟需要等待的支付（PENDING状态）
            result.setPayStatus("PENDING");
            if (result.getRemark() == null) {
                result.setRemark("模拟扫码支付（等待处理）");
            }
            LOGGER.info("模拟扫码支付（等待状态），付款码：{}，订单号：{}，需要等待5秒后成功", 
                    maskPaymentCode(paymentCode), result.getOrderSn());
        } else {
            // 其他数字：模拟立即成功的支付
        result.setPayStatus("SUCCESS");
        result.setTransactionId("MOCK_" + System.currentTimeMillis());
        result.setPaidAmount(param.getAmount());
        result.setPayTime(new Date());
        
            if (result.getRemark() == null) {
                result.setRemark("模拟扫码支付立即成功");
            }
            
            // 支付成功时更新订单状态
            handlePaymentSuccess(result, false);
            
            LOGGER.info("模拟扫码支付立即成功，付款码：{}，订单号：{}，交易流水：{}", 
                    maskPaymentCode(paymentCode), result.getOrderSn(), result.getTransactionId());
        }
        
        return result;
    }

    /**
     * 处理模拟支付状态查询
     */
    private PaymentResultVO handleMockPaymentStatusQuery(PaymentResultVO result) {
        LOGGER.info("处理模拟支付状态查询，支付ID：{}，当前状态：{}", result.getPaymentId(), result.getPayStatus());
        
        // 如果状态是PENDING，模拟支付处理逻辑
        if ("PENDING".equals(result.getPayStatus())) {
            // 获取支付创建时间
            Date createTime = result.getCreateTime();
            long currentTime = System.currentTimeMillis();
            long paymentTime = createTime != null ? createTime.getTime() : currentTime;
            
            // 计算支付经过的时间（秒）
            long elapsedSeconds = (currentTime - paymentTime) / 1000;
            
            // 模拟支付处理：
            // - 前10秒保持PENDING状态
            // - 10秒后模拟支付成功
            if (elapsedSeconds >= 5) {
                // 模拟支付成功
                result.setPayStatus("SUCCESS");
                result.setTransactionId("MOCK_" + System.currentTimeMillis());
                result.setPaidAmount(result.getAmount());
                result.setPayTime(new Date());
                
                // 填充订单编号（如果还没有）
                if (result.getOrderSn() == null) {
                    result.setOrderSn("SC" + System.currentTimeMillis());
                }
                
                // 设置备注
                if (result.getRemark() == null) {
                    result.setRemark("模拟支付自动成功");
                }
                
                // 支付成功时更新订单状态
                handlePaymentSuccess(result, false);
                
                LOGGER.info("模拟支付状态变更为成功，支付ID：{}，交易流水：{}", 
                        result.getPaymentId(), result.getTransactionId());
            } else {
                LOGGER.info("模拟支付处理中，支付ID：{}，已等待{}秒，还需等待{}秒", 
                        result.getPaymentId(), elapsedSeconds, (10 - elapsedSeconds));
            }
        }
        
        return result;
    }

    /**
     * 处理微信支付回调
     */
    private String handleWechatNotify(String notifyData) {
        try {
            // 获取微信支付配置
            SelfcheckConfig.Payment.Wechat wechatConfig = selfcheckConfig.getPayment().getWechat();
            
            LOGGER.info("处理微信支付回调 - AppId: {}, NotifyUrl: {}", 
                    wechatConfig.getAppId(), wechatConfig.getNotifyUrl());
            
            // 如果是模拟模式，直接返回成功
            if (selfcheckConfig.getPayment().getMockEnabled()) {
                LOGGER.info("模拟模式下微信支付回调处理完成");
                return "SUCCESS";
            }
            
            // 真实微信支付回调处理
            if (wechatPaymentService != null) {
                // 由专门的微信支付服务处理回调
                String result = wechatPaymentService.handlePaymentNotify(notifyData);
                
                // 如果回调验证成功，解析回调数据并更新订单状态
                if ("SUCCESS".equals(result)) {
                    processWechatNotifySuccess(notifyData);
                }
                
        return result;
            } else {
                // 兜底处理：解析微信回调数据
                return processWechatNotifyManually(notifyData, wechatConfig);
            }
            
        } catch (Exception e) {
            LOGGER.error("处理微信支付回调失败", e);
            return "FAIL";
        }
    }

    /**
     * 手动处理微信支付回调（当微信支付服务未启用时）
     */
    private String processWechatNotifyManually(String notifyData, SelfcheckConfig.Payment.Wechat wechatConfig) {
        try {
            LOGGER.info("手动处理微信支付回调，回调数据长度: {}", notifyData.length());
            
            // TODO: 这里应该集成真实的微信支付SDK来解析和验证回调
            // 示例实现（需要根据实际微信支付SDK调整）:
            
            /*
            // 1. 解析XML格式的回调数据
            WxPayOrderNotifyResult notifyResult = WxPayOrderNotifyResult.fromXML(notifyData);
            
            // 2. 验证签名
            if (!wxPayService.checkNotifySign(notifyResult)) {
                LOGGER.warn("微信支付回调签名验证失败");
                return "FAIL";
            }
            
            // 3. 验证回调数据
            if (!"SUCCESS".equals(notifyResult.getReturnCode()) || 
                !"SUCCESS".equals(notifyResult.getResultCode())) {
                LOGGER.warn("微信支付回调结果异常: returnCode={}, resultCode={}", 
                        notifyResult.getReturnCode(), notifyResult.getResultCode());
                return "FAIL";
    }

            // 4. 处理支付成功逻辑
            processWechatPaymentSuccess(notifyResult);
            */
            
            // 临时模拟处理（实际应该使用上面的真实处理逻辑）
            LOGGER.warn("微信支付回调处理使用模拟逻辑，请集成真实的微信支付SDK");
            
            // 解析关键信息（模拟）
            if (notifyData.contains("<return_code><![CDATA[SUCCESS]]></return_code>") &&
                notifyData.contains("<result_code><![CDATA[SUCCESS]]></result_code>")) {
                
                // 模拟解析订单号和交易号
                String outTradeNo = extractXmlValue(notifyData, "out_trade_no");
                String transactionId = extractXmlValue(notifyData, "transaction_id");
                String totalFee = extractXmlValue(notifyData, "total_fee");
                
                if (outTradeNo != null && transactionId != null) {
                    // 处理支付成功
                    processWechatPaymentCallbackSuccess(outTradeNo, transactionId, totalFee);
                    return "SUCCESS";
                }
            }
            
            LOGGER.warn("微信支付回调数据解析失败");
            return "FAIL";
            
        } catch (Exception e) {
            LOGGER.error("手动处理微信支付回调异常", e);
            return "FAIL";
        }
    }

    /**
     * 处理微信支付回调成功（当微信支付服务已处理回调验证后）
     */
    private void processWechatNotifySuccess(String notifyData) {
        try {
            // 从回调数据中解析关键信息
            String outTradeNo = extractXmlValue(notifyData, "out_trade_no");
            String transactionId = extractXmlValue(notifyData, "transaction_id");
            String totalFee = extractXmlValue(notifyData, "total_fee");
            
            if (outTradeNo != null && transactionId != null) {
                processWechatPaymentCallbackSuccess(outTradeNo, transactionId, totalFee);
            } else {
                LOGGER.warn("微信支付回调数据缺少关键信息");
            }
            
        } catch (Exception e) {
            LOGGER.error("处理微信支付回调成功逻辑异常", e);
        }
    }

    /**
     * 处理微信支付回调成功的核心逻辑
     */
    private void processWechatPaymentCallbackSuccess(String outTradeNo, String transactionId, String totalFee) {
        try {
            LOGGER.info("处理微信支付回调成功 - 商户订单号: {}, 微信交易号: {}, 金额: {}分", 
                    outTradeNo, transactionId, totalFee);
            
            // 根据商户订单号（支付ID）获取缓存的支付信息
            PaymentResultVO paymentResult = getCachedPaymentInfo(outTradeNo);
            
            if (paymentResult != null) {
                // 更新支付结果
                paymentResult.setPayStatus("SUCCESS");
                paymentResult.setTransactionId(transactionId);
                paymentResult.setPayTime(new Date());
                
                if (totalFee != null) {
                    try {
                        // 微信返回的金额是分，需要转换为元
                        BigDecimal paidAmount = new BigDecimal(totalFee).divide(new BigDecimal(100));
                        paymentResult.setPaidAmount(paidAmount);
                    } catch (NumberFormatException e) {
                        LOGGER.warn("解析微信支付金额失败: {}", totalFee);
                    }
                }
                
                // 更新缓存
                cachePaymentInfo(outTradeNo, paymentResult);
                
                // 调用订单状态更新
                handlePaymentSuccess(paymentResult, true);
                
                LOGGER.info("微信支付回调处理完成，订单状态已更新 - 支付ID: {}, 交易号: {}", 
                        outTradeNo, transactionId);
            } else {
                LOGGER.warn("微信支付回调: 未找到对应的支付信息 - 支付ID: {}", outTradeNo);
            }
            
        } catch (Exception e) {
            LOGGER.error("处理微信支付回调成功逻辑异常", e);
        }
    }

    /**
     * 处理支付宝支付回调
     */
    private String handleAlipayNotify(String notifyData) {
        try {
            // 获取支付宝支付配置
            SelfcheckConfig.Payment.Alipay alipayConfig = selfcheckConfig.getPayment().getAlipay();
            
            LOGGER.info("处理支付宝支付回调 - AppId: {}, NotifyUrl: {}", 
                    alipayConfig.getAppId(), alipayConfig.getNotifyUrl());
            
            // 如果是模拟模式，直接返回成功
            if (selfcheckConfig.getPayment().getMockEnabled()) {
                LOGGER.info("模拟模式下支付宝支付回调处理完成");
        return "SUCCESS";
            }
            
            // 真实支付宝支付回调处理
            if (alipayPaymentService != null) {
                // 由专门的支付宝支付服务处理回调
                String result = alipayPaymentService.handlePaymentNotify(notifyData);
                
                // 如果回调验证成功，解析回调数据并更新订单状态
                if ("SUCCESS".equals(result)) {
                    processAlipayNotifySuccess(notifyData);
                }
                
                return result;
            } else {
                // 兜底处理：解析支付宝回调数据
                return processAlipayNotifyManually(notifyData, alipayConfig);
            }
            
        } catch (Exception e) {
            LOGGER.error("处理支付宝支付回调失败", e);
            return "FAIL";
        }
    }

    /**
     * 手动处理支付宝支付回调（当支付宝支付服务未启用时）
     */
    private String processAlipayNotifyManually(String notifyData, SelfcheckConfig.Payment.Alipay alipayConfig) {
        try {
            LOGGER.info("手动处理支付宝支付回调，回调数据长度: {}", notifyData.length());
            
            // TODO: 这里应该集成真实的支付宝SDK来解析和验证回调
            // 示例实现（需要根据实际支付宝SDK调整）:
            
            /*
            // 1. 解析支付宝回调参数
            Map<String, String> params = parseAlipayNotifyParams(notifyData);
            
            // 2. 验证签名
            boolean signVerified = AlipaySignature.rsaCheckV1(
                params, 
                alipayConfig.getAlipayPublicKey(), 
                alipayConfig.getCharset(), 
                alipayConfig.getSignType()
            );
            
            if (!signVerified) {
                LOGGER.warn("支付宝支付回调签名验证失败");
                return "FAIL";
            }
            
            // 3. 验证回调数据
            String tradeStatus = params.get("trade_status");
            if (!"TRADE_SUCCESS".equals(tradeStatus) && !"TRADE_FINISHED".equals(tradeStatus)) {
                LOGGER.warn("支付宝支付回调状态异常: {}", tradeStatus);
                return "FAIL";
            }
            
            // 4. 处理支付成功逻辑
            processAlipayPaymentSuccess(params);
            */
            
            // 临时模拟处理（实际应该使用上面的真实处理逻辑）
            LOGGER.warn("支付宝支付回调处理使用模拟逻辑，请集成真实的支付宝SDK");
            
            // 解析关键信息（模拟）
            Map<String, String> params = parseUrlEncodedData(notifyData);
            
            String tradeStatus = params.get("trade_status");
            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                
                String outTradeNo = params.get("out_trade_no");
                String tradeNo = params.get("trade_no");
                String totalAmount = params.get("total_amount");
                
                if (outTradeNo != null && tradeNo != null) {
                    // 处理支付成功
                    processAlipayPaymentCallbackSuccess(outTradeNo, tradeNo, totalAmount);
                    return "SUCCESS";
                }
            }
            
            LOGGER.warn("支付宝支付回调数据解析失败或状态异常: {}", tradeStatus);
            return "FAIL";
            
        } catch (Exception e) {
            LOGGER.error("手动处理支付宝支付回调异常", e);
            return "FAIL";
        }
    }

    /**
     * 处理支付宝支付回调成功（当支付宝支付服务已处理回调验证后）
     */
    private void processAlipayNotifySuccess(String notifyData) {
        try {
            // 从回调数据中解析关键信息
            Map<String, String> params = parseUrlEncodedData(notifyData);
            
            String outTradeNo = params.get("out_trade_no");
            String tradeNo = params.get("trade_no");
            String totalAmount = params.get("total_amount");
            
            if (outTradeNo != null && tradeNo != null) {
                processAlipayPaymentCallbackSuccess(outTradeNo, tradeNo, totalAmount);
            } else {
                LOGGER.warn("支付宝支付回调数据缺少关键信息");
            }
            
        } catch (Exception e) {
            LOGGER.error("处理支付宝支付回调成功逻辑异常", e);
        }
    }

    /**
     * 处理支付宝支付回调成功的核心逻辑
     */
    private void processAlipayPaymentCallbackSuccess(String outTradeNo, String tradeNo, String totalAmount) {
        try {
            LOGGER.info("处理支付宝支付回调成功 - 商户订单号: {}, 支付宝交易号: {}, 金额: {}元", 
                    outTradeNo, tradeNo, totalAmount);
            
            // 根据商户订单号（支付ID）获取缓存的支付信息
            PaymentResultVO paymentResult = getCachedPaymentInfo(outTradeNo);
            
            if (paymentResult != null) {
                // 更新支付结果
                paymentResult.setPayStatus("SUCCESS");
                paymentResult.setTransactionId(tradeNo);
                paymentResult.setPayTime(new Date());
                
                if (totalAmount != null) {
                    try {
                        // 支付宝返回的金额是元
                        BigDecimal paidAmount = new BigDecimal(totalAmount);
                        paymentResult.setPaidAmount(paidAmount);
                    } catch (NumberFormatException e) {
                        LOGGER.warn("解析支付宝支付金额失败: {}", totalAmount);
                    }
                }
                
                // 更新缓存
                cachePaymentInfo(outTradeNo, paymentResult);
                
                // 调用订单状态更新
                handlePaymentSuccess(paymentResult, true);
                
                LOGGER.info("支付宝支付回调处理完成，订单状态已更新 - 支付ID: {}, 交易号: {}", 
                        outTradeNo, tradeNo);
            } else {
                LOGGER.warn("支付宝支付回调: 未找到对应的支付信息 - 支付ID: {}", outTradeNo);
            }
            
        } catch (Exception e) {
            LOGGER.error("处理支付宝支付回调成功逻辑异常", e);
        }
    }

    /**
     * 生成支付ID
     */
    private String generatePaymentId() {
        return "PAY" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * 缓存支付信息
     */
    private void cachePaymentInfo(String paymentId, PaymentResultVO result) {
        String key = "selfcheck:payment:" + paymentId;
        redisTemplate.opsForValue().set(key, result, selfcheckConfig.getPayment().getTimeout(), TimeUnit.MINUTES);
    }

    /**
     * 获取缓存的支付信息
     */
    private PaymentResultVO getCachedPaymentInfo(String paymentId) {
        String key = "selfcheck:payment:" + paymentId;
        return (PaymentResultVO) redisTemplate.opsForValue().get(key);
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

    /**
     * 处理支付成功回调
     * 更新订单状态和支付信息
     * 
     * @param paymentResult 支付结果
     * @param isRealPayment 是否为真实支付（false表示模拟支付）
     */
    private void handlePaymentSuccess(PaymentResultVO paymentResult, boolean isRealPayment) {
        try {
            Long orderId = Long.valueOf(paymentResult.getOrderId());
            Integer payType = getPayTypeCode(paymentResult.getPayType());
            String transactionId = paymentResult.getTransactionId();
            
            boolean updateResult = orderService.paymentSuccess(orderId, payType, transactionId);
            
            String paymentTypeDesc = isRealPayment ? "真实" : "模拟";
            if (updateResult) {
                LOGGER.info("{}支付成功，订单状态已更新：orderId={}, payType={}, transactionId={}", 
                        paymentTypeDesc, orderId, paymentResult.getPayType(), transactionId);
            } else {
                LOGGER.warn("{}支付成功，但订单状态更新失败：orderId={}, payType={}", 
                        paymentTypeDesc, orderId, paymentResult.getPayType());
            }
        } catch (Exception e) {
            LOGGER.error("{}支付成功，但更新订单状态时发生异常：orderId={}, payType={}, error={}", 
                    isRealPayment ? "真实" : "模拟", 
                    paymentResult.getOrderId(), 
                    paymentResult.getPayType(), 
                    e.getMessage());
        }
    }

    /**
     * 获取支付方式代码
     * 使用全局统一的支付方式映射常量
     * 
     * @param payType 支付方式字符串
     * @return 支付方式代码
     */
    private Integer getPayTypeCode(String payType) {
        return PayTypeConstants.getPayTypeCode(payType);
    }

    /**
     * 从XML数据中提取指定字段的值
     * 
     * @param xmlData XML数据
     * @param fieldName 字段名
     * @return 字段值
     */
    private String extractXmlValue(String xmlData, String fieldName) {
        try {
            String startTag = "<" + fieldName + "><![CDATA[";
            String endTag = "]]></" + fieldName + ">";
            
            int startIndex = xmlData.indexOf(startTag);
            if (startIndex == -1) {
                // 尝试简单标签格式
                startTag = "<" + fieldName + ">";
                endTag = "</" + fieldName + ">";
                startIndex = xmlData.indexOf(startTag);
            }
            
            if (startIndex != -1) {
                startIndex += startTag.length();
                int endIndex = xmlData.indexOf(endTag, startIndex);
                if (endIndex != -1) {
                    return xmlData.substring(startIndex, endIndex).trim();
                }
            }
            
            return null;
            
        } catch (Exception e) {
            LOGGER.warn("解析XML字段失败: fieldName={}, error={}", fieldName, e.getMessage());
            return null;
        }
    }

    /**
     * 解析URL编码的表单数据
     * 
     * @param data URL编码的数据
     * @return 解析后的参数Map
     */
    private Map<String, String> parseUrlEncodedData(String data) {
        Map<String, String> result = new HashMap<>();
        
        try {
            if (data == null || data.trim().isEmpty()) {
                return result;
            }
            
            String[] pairs = data.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                    String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                    result.put(key, value);
                }
            }
            
        } catch (Exception e) {
            LOGGER.warn("解析URL编码数据失败: {}", e.getMessage());
        }
        
        return result;
    }

    /**
     * 验证回调请求的安全性
     * 
     * @param payType 支付类型
     * @param notifyData 回调数据
     * @return 是否安全
     */
    private boolean validateNotifySecurity(String payType, String notifyData) {
        try {
            // 基本安全检查
            if (notifyData == null || notifyData.trim().isEmpty()) {
                LOGGER.warn("回调数据为空");
                return false;
            }
            
            // 检查数据长度（防止过大的恶意数据）
            if (notifyData.length() > 10240) { // 最大10KB
                LOGGER.warn("回调数据过大: {} bytes", notifyData.length());
                return false;
            }
            
            // 检查支付类型
            if (!"WECHAT".equals(payType) && !"ALIPAY".equals(payType)) {
                LOGGER.warn("不支持的支付类型: {}", payType);
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            LOGGER.error("验证回调安全性失败", e);
            return false;
        }
    }

    /**
     * 记录回调处理日志
     * 
     * @param payType 支付类型
     * @param success 是否成功
     * @param message 消息
     * @param outTradeNo 商户订单号
     * @param transactionId 交易号
     */
    private void logNotifyProcess(String payType, boolean success, String message, 
                                 String outTradeNo, String transactionId) {
        try {
            if (success) {
                LOGGER.info("{}支付回调处理成功 - 商户订单号: {}, 交易号: {}, 消息: {}", 
                        payType, outTradeNo, transactionId, message);
            } else {
                LOGGER.warn("{}支付回调处理失败 - 商户订单号: {}, 消息: {}", 
                        payType, outTradeNo, message);
            }
            
            // TODO: 可以在这里记录到数据库或发送监控告警
            
        } catch (Exception e) {
            LOGGER.error("记录回调处理日志失败", e);
        }
    }
} 