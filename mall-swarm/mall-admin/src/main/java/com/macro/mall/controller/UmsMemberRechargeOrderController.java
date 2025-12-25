package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.UmsMemberRechargeOrderQueryParam;
import com.macro.mall.model.UmsMemberRechargeOrder;
import com.macro.mall.model.UmsMemberBalanceHistory;
import com.macro.mall.service.UmsMemberRechargeOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 充值订单管理Controller
 */
@RestController
@RequestMapping("/rechargeOrder")
@Tag(name = "UmsMemberRechargeOrderController", description = "充值订单管理")
public class UmsMemberRechargeOrderController {

    @Autowired
    private UmsMemberRechargeOrderService rechargeOrderService;

    @Operation(summary = "分页查询充值订单")
    @GetMapping("/list")
    public CommonResult<CommonPage<UmsMemberRechargeOrder>> list(UmsMemberRechargeOrderQueryParam queryParam,
                                                                @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                                @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<UmsMemberRechargeOrder> orderList = rechargeOrderService.list(queryParam, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(orderList));
    }

    @Operation(summary = "获取充值订单详情")
    @GetMapping("/{id}")
    public CommonResult<UmsMemberRechargeOrder> getItem(@PathVariable Long id) {
        UmsMemberRechargeOrder order = rechargeOrderService.getItem(id);
        return CommonResult.success(order);
    }

    @Operation(summary = "批量删除充值订单")
    @PostMapping("/delete")
    public CommonResult delete(@RequestParam("ids") List<Long> ids) {
        int count = rechargeOrderService.delete(ids);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "导出充值订单")
    @GetMapping("/export")
    public void exportRechargeOrder(UmsMemberRechargeOrderQueryParam queryParam, HttpServletResponse response) {
        rechargeOrderService.exportRechargeOrder(queryParam, response);
    }

    @Operation(summary = "获取用户充值记录")
    @GetMapping("/member/history")
    public CommonResult<CommonPage<UmsMemberRechargeOrder>> getMemberRechargeHistory(@RequestParam Long memberId,
                                                                                     @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                                                     @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                                                     @RequestParam(required = false) Integer status,
                                                                                     @RequestParam(required = false) String startDate,
                                                                                     @RequestParam(required = false) String endDate) {
        List<UmsMemberRechargeOrder> historyList = rechargeOrderService.getMemberRechargeHistory(memberId, status, startDate, endDate, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(historyList));
    }
} 