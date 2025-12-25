package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.NewOrderNotificationParam;
import com.macro.mall.dto.RefundNotificationParam;
import com.macro.mall.dto.SaleApprovalNotificationParam;
import com.macro.mall.dto.StockOutNotificationParam;
import com.macro.mall.service.AdminNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员通知Controller
 * 提供给其他微服务调用的通知接口
 */
@RestController
@Tag(name = "AdminNotificationController", description = "管理员通知接口")
@RequestMapping("/notification")
public class AdminNotificationController {
    
    @Autowired
    private AdminNotificationService adminNotificationService;
    
    @Operation(summary = "发送新订单通知")
    @PostMapping("/newOrder")
    public CommonResult<Void> notifyNewOrder(@RequestBody NewOrderNotificationParam param) {
        adminNotificationService.notifyNewOrder(
            param.getOrderSn(),
            param.getOrderType(),
            param.getOrderAmount(),
            param.getOrderTime(),
            param.getProductName(),
            param.getStoreId()
        );
        return CommonResult.success(null);
    }
    
    @Operation(summary = "发送销售单审核结果通知")
    @PostMapping("/saleApprovalResult")
    public CommonResult<Void> notifySaleApprovalResult(@RequestBody SaleApprovalNotificationParam param) {
        adminNotificationService.notifySaleApprovalResult(
            param.getSaleNo(),
            param.getAuditResult(),
            param.getAuditTime(),
            param.getApplicantAdminId()
        );
        return CommonResult.success(null);
    }
    
    @Operation(summary = "发送销售单新申请通知")
    @PostMapping("/saleNewApplication")
    public CommonResult<Void> notifySaleNewApplication(@RequestBody SaleApprovalNotificationParam param) {
        adminNotificationService.notifySaleNewApplication(
            param.getSaleNo(),
            param.getAuditTime()
        );
        return CommonResult.success(null);
    }
    
    @Operation(summary = "发送出库单新申请通知")
    @PostMapping("/stockOutNewApplication")
    public CommonResult<Void> notifyStockOutNewApplication(@RequestBody StockOutNotificationParam param) {
        adminNotificationService.notifyStockOutNewApplication(
            param.getTransferNo(),
            param.getAuditTime(),
            param.getCustomerName(),
            param.getTargetStoreId()
        );
        return CommonResult.success(null);
    }
    
    @Operation(summary = "发送出库单发货通知")
    @PostMapping("/stockOutShipped")
    public CommonResult<Void> notifyStockOutShipped(@RequestBody StockOutNotificationParam param) {
        adminNotificationService.notifyStockOutShipped(
            param.getTransferNo(),
            param.getAuditTime(),
            param.getCustomerName(),
            param.getApplicantStoreId()
        );
        return CommonResult.success(null);
    }
    
    @Operation(summary = "发送出库单收货通知")
    @PostMapping("/stockOutReceived")
    public CommonResult<Void> notifyStockOutReceived(@RequestBody StockOutNotificationParam param) {
        adminNotificationService.notifyStockOutReceived(
            param.getTransferNo(),
            param.getAuditTime(),
            param.getCustomerName(),
            param.getTargetStoreId()
        );
        return CommonResult.success(null);
    }
    
    @Operation(summary = "发送退款申请通知")
    @PostMapping("/refundApplication")
    public CommonResult<Void> notifyRefundApplication(@RequestBody RefundNotificationParam param) {
        adminNotificationService.notifyRefundApplication(
            param.getOrderSn(),
            param.getProductName(),
            param.getRefundAmount(),
            param.getApplyTime(),
            param.getPhoneNumber()
        );
        return CommonResult.success(null);
    }
}
