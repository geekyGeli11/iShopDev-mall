package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.domain.DiyOrderDetailVO;
import com.macro.mall.portal.domain.DiyOrderParam;
import com.macro.mall.portal.domain.DiyOrderResult;
import com.macro.mall.portal.service.PortalDiyOrderService;
import com.macro.mall.portal.util.StpMemberUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 小程序端DIY订单Controller
 * Created by macro on 2024/12/20.
 */
@RestController
@Tag(name = "PortalDiyOrderController", description = "小程序端DIY订单")
@RequestMapping("/diy/order")
public class PortalDiyOrderController {
    
    @Autowired
    private PortalDiyOrderService diyOrderService;

    @Operation(summary = "创建DIY订单")
    @PostMapping("/create")
    public CommonResult<DiyOrderResult> createDiyOrder(@Validated @RequestBody DiyOrderParam orderParam) {
        DiyOrderResult result = diyOrderService.createOrder(orderParam);
        if (result.getStatus() == 0) {
            return CommonResult.success(result);
        } else {
            return CommonResult.failed(result.getErrorMessage());
        }
    }

    @Operation(summary = "获取DIY订单详情")
    @GetMapping("/{orderId}")
    public CommonResult<DiyOrderResult> getDiyOrderDetail(@PathVariable Long orderId) {
        Long memberId = getCurrentMemberId();
        if (memberId == null) {
            return CommonResult.unauthorized("请先登录");
        }
        
        DiyOrderResult result = diyOrderService.getDiyOrderDetail(orderId, memberId);
        if (result.getStatus() == 0) {
            return CommonResult.success(result);
        } else {
            return CommonResult.failed(result.getErrorMessage());
        }
    }

    @Operation(summary = "取消DIY订单")
    @PostMapping("/{orderId}/cancel")
    public CommonResult cancelDiyOrder(@PathVariable Long orderId) {
        Long memberId = getCurrentMemberId();
        if (memberId == null) {
            return CommonResult.unauthorized("请先登录");
        }
        
        int count = diyOrderService.cancelDiyOrder(orderId, memberId);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed("取消订单失败");
        }
    }
    
    @Operation(summary = "计算DIY订单价格")
    @PostMapping("/calculate")
    public CommonResult<BigDecimal> calculateDiyOrderPrice(@Validated @RequestBody DiyOrderParam orderParam) {
        BigDecimal price = diyOrderService.calculateDiyOrderPrice(orderParam);
        return CommonResult.success(price);
    }

    @Operation(summary = "获取用户DIY订单列表")
    @GetMapping("/list")
    public CommonResult<CommonPage<DiyOrderDetailVO>> getDiyOrderList(
            @Parameter(description = "订单状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        Long memberId = getCurrentMemberId();
        if (memberId == null) {
            return CommonResult.unauthorized("请先登录");
        }

        CommonPage<DiyOrderDetailVO> result = diyOrderService.getDiyOrderList(memberId, status, pageNum, pageSize);
        return CommonResult.success(result);
    }

    @Operation(summary = "更新订单生产状态")
    @PostMapping("/{orderId}/production-status")
    public CommonResult updateProductionStatus(
            @PathVariable Long orderId,
            @Parameter(description = "生产状态：0->待生产；1->生产中；2->已完成") @RequestParam Byte productionStatus) {
        int count = diyOrderService.updateProductionStatus(orderId, productionStatus);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed("更新生产状态失败");
        }
    }

    @Operation(summary = "确认收货")
    @PostMapping("/{orderId}/confirm")
    public CommonResult confirmReceive(@PathVariable Long orderId) {
        Long memberId = getCurrentMemberId();
        if (memberId == null) {
            return CommonResult.unauthorized("请先登录");
        }

        int count = diyOrderService.confirmReceive(orderId, memberId);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed("确认收货失败");
        }
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentMemberId() {
        try {
            return StpMemberUtil.getLoginIdAsLong();
        } catch (Exception e) {
            // 如果获取失败，返回null
            return null;
        }
    }
}
