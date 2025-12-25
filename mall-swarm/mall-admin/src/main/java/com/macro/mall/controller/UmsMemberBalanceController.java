package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsMemberBalanceHistory;
import com.macro.mall.service.UmsMemberBalanceHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户余额记录管理Controller
 */
@RestController
@RequestMapping("/member")
@Tag(name = "UmsMemberBalanceController", description = "用户余额记录管理")
public class UmsMemberBalanceController {

    @Autowired
    private UmsMemberBalanceHistoryService balanceHistoryService;

    @Operation(summary = "获取用户充值记录")
    @GetMapping("/rechargeHistory")
    public CommonResult<CommonPage<UmsMemberBalanceHistory>> getRechargeHistory(@RequestParam Long memberId,
                                                                                @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                                                @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                                                @RequestParam(required = false) Integer status,
                                                                                @RequestParam(required = false) String startDate,
                                                                                @RequestParam(required = false) String endDate) {
        List<UmsMemberBalanceHistory> historyList = balanceHistoryService.getRechargeHistory(memberId, status, startDate, endDate, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(historyList));
    }

    @Operation(summary = "获取用户消费记录")
    @GetMapping("/consumptionHistory")
    public CommonResult<CommonPage<UmsMemberBalanceHistory>> getConsumptionHistory(@RequestParam Long memberId,
                                                                                   @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                                                   @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                                                   @RequestParam(required = false) String businessType,
                                                                                   @RequestParam(required = false) Integer changeType,
                                                                                   @RequestParam(required = false) String startDate,
                                                                                   @RequestParam(required = false) String endDate) {
        List<UmsMemberBalanceHistory> historyList = balanceHistoryService.getConsumptionHistory(memberId, businessType, changeType, startDate, endDate, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(historyList));
    }
} 