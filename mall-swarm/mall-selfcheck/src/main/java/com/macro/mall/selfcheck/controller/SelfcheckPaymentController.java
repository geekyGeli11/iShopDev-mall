package com.macro.mall.selfcheck.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.selfcheck.dto.PaymentCodeParam;
import com.macro.mall.selfcheck.dto.PaymentQRParam;
import com.macro.mall.selfcheck.dto.PaymentResultVO;
import com.macro.mall.selfcheck.service.SelfcheckPaymentService;
import com.macro.mall.selfcheck.util.StpMemberUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * 自助收银支付控制器
 * 
 * @author macro
 * @since 1.0.0
 */
@RestController
@RequestMapping("/selfcheck/payment")
@Tag(name = "SelfcheckPaymentController", description = "自助收银支付管理")
@Validated
public class SelfcheckPaymentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SelfcheckPaymentController.class);

    @Autowired
    private SelfcheckPaymentService paymentService;

    @Operation(summary = "生成收款二维码")
    @PostMapping("/generateQR")
    public CommonResult<PaymentResultVO> generatePaymentQR(@Valid @RequestBody PaymentQRParam param) {
        try {
            // 设置终端和用户信息
            setTerminalInfo(param);
            
            LOGGER.info("生成收款二维码请求：订单ID={}, 金额={}, 支付方式={}", 
                    param.getOrderId(), param.getAmount(), param.getPayType());
            
            return paymentService.generatePaymentQR(param);
            
        } catch (Exception e) {
            LOGGER.error("生成收款二维码失败", e);
            return CommonResult.failed("生成收款二维码失败：" + e.getMessage());
        }
    }

    @Operation(summary = "扫描付款码支付")
    @PostMapping("/scanCode")
    public CommonResult<PaymentResultVO> scanPaymentCode(@Valid @RequestBody PaymentCodeParam param) {
        try {
            // 设置终端和用户信息
            setTerminalInfo(param);
            
            LOGGER.info("扫描付款码支付请求：订单ID={}, 金额={}, 支付方式={}", 
                    param.getOrderId(), param.getAmount(), param.getPayType());
            
            return paymentService.scanPaymentCode(param);
            
        } catch (Exception e) {
            LOGGER.error("扫描付款码支付失败", e);
            return CommonResult.failed("扫描付款码支付失败：" + e.getMessage());
        }
    }

    @Operation(summary = "查询支付状态")
    @GetMapping("/status/{paymentId}")
    public CommonResult<PaymentResultVO> getPaymentStatus(
            @Parameter(description = "支付ID", required = true)
            @PathVariable @NotBlank(message = "支付ID不能为空") String paymentId) {
        try {
            LOGGER.info("查询支付状态：支付ID={}", paymentId);
            return paymentService.getPaymentStatus(paymentId);
            
        } catch (Exception e) {
            LOGGER.error("查询支付状态失败", e);
            return CommonResult.failed("查询支付状态失败：" + e.getMessage());
        }
    }

    @Operation(summary = "取消支付")
    @PostMapping("/cancel/{paymentId}")
    public CommonResult<Void> cancelPayment(
            @Parameter(description = "支付ID", required = true)
            @PathVariable @NotBlank(message = "支付ID不能为空") String paymentId) {
        try {
            LOGGER.info("取消支付：支付ID={}", paymentId);
            return paymentService.cancelPayment(paymentId);
            
        } catch (Exception e) {
            LOGGER.error("取消支付失败", e);
            return CommonResult.failed("取消支付失败：" + e.getMessage());
        }
    }

    @Operation(summary = "验证付款码格式")
    @PostMapping("/validateCode")
    public CommonResult<String> validatePaymentCode(
            @Parameter(description = "付款码", required = true)
            @RequestParam @NotBlank(message = "付款码不能为空") String paymentCode) {
        try {
            LOGGER.info("验证付款码格式：付款码长度={}", paymentCode.length());
            return paymentService.validatePaymentCode(paymentCode);
            
        } catch (Exception e) {
            LOGGER.error("验证付款码格式失败", e);
            return CommonResult.failed("验证付款码格式失败：" + e.getMessage());
        }
    }

    @Operation(summary = "退款处理")
    @PostMapping("/refund/{paymentId}")
    public CommonResult<PaymentResultVO> refundPayment(
            @Parameter(description = "支付ID", required = true)
            @PathVariable @NotBlank(message = "支付ID不能为空") String paymentId,
            @Parameter(description = "退款金额", required = true)
            @RequestParam BigDecimal refundAmount,
            @Parameter(description = "退款原因")
            @RequestParam(required = false, defaultValue = "用户申请退款") String reason) {
        try {
            LOGGER.info("退款处理：支付ID={}, 退款金额={}, 退款原因={}", paymentId, refundAmount, reason);
            return paymentService.refundPayment(paymentId, refundAmount, reason);
            
        } catch (Exception e) {
            LOGGER.error("退款处理失败", e);
            return CommonResult.failed("退款处理失败：" + e.getMessage());
        }
    }

    @Operation(summary = "微信支付回调")
    @PostMapping("/notify/wechat")
    public String wechatPaymentNotify(HttpServletRequest request, @RequestBody String notifyData) {
        try {
            LOGGER.info("收到微信支付回调通知");
            return paymentService.handlePaymentNotify("WECHAT", notifyData);
            
        } catch (Exception e) {
            LOGGER.error("处理微信支付回调失败", e);
            return "FAIL";
        }
    }

    @Operation(summary = "支付宝支付回调")
    @PostMapping("/notify/alipay")
    public String alipayPaymentNotify(HttpServletRequest request, @RequestBody String notifyData) {
        try {
            LOGGER.info("收到支付宝支付回调通知");
            return paymentService.handlePaymentNotify("ALIPAY", notifyData);
            
        } catch (Exception e) {
            LOGGER.error("处理支付宝支付回调失败", e);
            return "FAIL";
        }
    }

    @Operation(summary = "支付方式检测")
    @PostMapping("/detectPayType")
    public CommonResult<String> detectPaymentType(
            @Parameter(description = "付款码", required = true)
            @RequestParam @NotBlank(message = "付款码不能为空") String paymentCode) {
        try {
            CommonResult<String> result = paymentService.validatePaymentCode(paymentCode);
            if (result.getCode() == 200) {
                String payType = result.getData();
                String payTypeName = "WECHAT".equals(payType) ? "微信支付" : "支付宝";
                return CommonResult.success(payTypeName);
            } else {
                return CommonResult.failed("无法识别的付款码格式");
            }
            
        } catch (Exception e) {
            LOGGER.error("检测支付方式失败", e);
            return CommonResult.failed("检测支付方式失败：" + e.getMessage());
        }
    }

    /**
     * 设置终端信息（二维码支付）
     */
    private void setTerminalInfo(PaymentQRParam param) {
        // 设置终端编号
        if (param.getTerminalCode() == null) {
            param.setTerminalCode("SC001"); // 默认终端编号
        }
        
        // 设置游客ID
        if (!StpMemberUtil.isLogin()) {
            // 游客模式，生成临时游客ID
            if (param.getGuestId() == null) {
                param.setGuestId("guest_" + System.currentTimeMillis());
            }
        }
    }

    /**
     * 设置终端信息（扫码支付）
     */
    private void setTerminalInfo(PaymentCodeParam param) {
        // 设置终端编号
        if (param.getTerminalCode() == null) {
            param.setTerminalCode("SC001"); // 默认终端编号
        }
        
        // 设置游客ID
        if (!StpMemberUtil.isLogin()) {
            // 游客模式，生成临时游客ID
            if (param.getGuestId() == null) {
                param.setGuestId("guest_" + System.currentTimeMillis());
            }
        }
    }
} 