package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.domain.MemberBalanceInfo;
import com.macro.mall.portal.domain.RechargeConfigResult;
import com.macro.mall.portal.domain.RechargeOrderParam;
import com.macro.mall.portal.domain.RechargeOrderResult;
import com.macro.mall.portal.service.UmsMemberBalanceService;
import com.macro.mall.portal.util.StpMemberUtil;
import com.macro.mall.model.UmsMemberBalanceHistory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * 用户余额管理Controller
 */
@RestController
@RequestMapping("/member/balance")
@Tag(name = "UmsMemberBalanceController", description = "用户余额管理")
public class UmsMemberBalanceController {

    @Autowired
    private UmsMemberBalanceService memberBalanceService;

    @Operation(summary = "获取用户余额信息")
    @GetMapping("/info")
    public CommonResult<MemberBalanceInfo> getBalanceInfo() {
        Long memberId = StpMemberUtil.getLoginIdAsLong();
        if (memberId == null) {
            return CommonResult.unauthorized("请先登录");
        }
        
        try {
            MemberBalanceInfo balanceInfo = memberBalanceService.getBalanceInfo(memberId);
            return CommonResult.success(balanceInfo);
        } catch (Exception e) {
            return CommonResult.failed("获取余额信息失败：" + e.getMessage());
        }
    }

    @Operation(summary = "创建充值订单")
    @PostMapping("/recharge/create")
    public CommonResult<RechargeOrderResult> createRechargeOrder(@Valid @RequestBody RechargeOrderParam param) {
        Long memberId = StpMemberUtil.getLoginIdAsLong();
        if (memberId == null) {
            return CommonResult.unauthorized("请先登录");
        }
        
        try {
            RechargeOrderResult result = memberBalanceService.createRechargeOrder(memberId, param);
            return CommonResult.success(result);
        } catch (Exception e) {
            return CommonResult.failed("创建充值订单失败：" + e.getMessage());
        }
    }

    @Operation(summary = "充值支付成功回调")
    @PostMapping("/recharge/pay-success")
    public CommonResult<Boolean> rechargePaySuccess(@RequestParam String orderSn,
                                                   @RequestParam Integer payType,
                                                   @RequestParam(required = false) String paySn) {
        try {
            boolean success = memberBalanceService.handleRechargePaySuccess(orderSn, payType, paySn);
            return CommonResult.success(success);
        } catch (Exception e) {
            return CommonResult.failed("处理充值支付回调失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取充值配置")
    @GetMapping("/recharge/config")
    public CommonResult<RechargeConfigResult> getRechargeConfig() {
        try {
            RechargeConfigResult config = memberBalanceService.getRechargeConfig();
            return CommonResult.success(config);
        } catch (Exception e) {
            return CommonResult.failed("获取充值配置失败：" + e.getMessage());
        }
    }

    @Operation(summary = "余额消费（用于订单支付等）")
    @PostMapping("/consume")
    public CommonResult<Boolean> consumeBalance(@RequestParam BigDecimal amount,
                                               @RequestParam String businessType,
                                               @RequestParam String businessId,
                                               @RequestParam(required = false) String remark) {
        Long memberId = StpMemberUtil.getLoginIdAsLong();
        if (memberId == null) {
            return CommonResult.unauthorized("请先登录");
        }
        
        try {
            boolean success = memberBalanceService.consumeBalance(memberId, amount, businessType, businessId, remark);
            return CommonResult.success(success);
        } catch (Exception e) {
            return CommonResult.failed("余额消费失败：" + e.getMessage());
        }
    }

    @Operation(summary = "余额退款（用于订单退款等）")
    @PostMapping("/refund")
    public CommonResult<Boolean> refundBalance(@RequestParam BigDecimal amount,
                                              @RequestParam String businessType,
                                              @RequestParam String businessId,
                                              @RequestParam(required = false) String remark) {
        Long memberId = StpMemberUtil.getLoginIdAsLong();
        if (memberId == null) {
            return CommonResult.unauthorized("请先登录");
        }
        
        try {
            boolean success = memberBalanceService.refundBalance(memberId, amount, businessType, businessId, remark);
            return CommonResult.success(success);
        } catch (Exception e) {
            return CommonResult.failed("余额退款失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取余额变动历史记录")
    @GetMapping("/history")
    public CommonResult<List<UmsMemberBalanceHistory>> getBalanceHistory(
            @RequestParam(required = false) Integer changeType,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        Long memberId = StpMemberUtil.getLoginIdAsLong();
        if (memberId == null) {
            return CommonResult.unauthorized("请先登录");
        }
        
        try {
            List<UmsMemberBalanceHistory> historyList = memberBalanceService.getBalanceHistory(memberId, changeType, pageNum, pageSize);
            return CommonResult.success(historyList);
        } catch (Exception e) {
            return CommonResult.failed("获取历史记录失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取充值历史")
    @GetMapping("/recharge/history")
    public CommonResult<List<UmsMemberBalanceHistory>> getRechargeHistory(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        Long memberId = StpMemberUtil.getLoginIdAsLong();
        if (memberId == null) {
            return CommonResult.unauthorized("请先登录");
        }
        
        try {
            // changeType = 1 表示充值记录
            List<UmsMemberBalanceHistory> historyList = memberBalanceService.getBalanceHistory(memberId, 1, pageNum, pageSize);
            return CommonResult.success(historyList);
        } catch (Exception e) {
            return CommonResult.failed("获取充值历史失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取消费记录")
    @GetMapping("/consume/history")
    public CommonResult<List<UmsMemberBalanceHistory>> getConsumeHistory(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        Long memberId = StpMemberUtil.getLoginIdAsLong();
        if (memberId == null) {
            return CommonResult.unauthorized("请先登录");
        }
        
        try {
            // changeType = 2 表示消费记录
            List<UmsMemberBalanceHistory> historyList = memberBalanceService.getBalanceHistory(memberId, 2, pageNum, pageSize);
            return CommonResult.success(historyList);
        } catch (Exception e) {
            return CommonResult.failed("获取消费记录失败：" + e.getMessage());
        }
    }
} 