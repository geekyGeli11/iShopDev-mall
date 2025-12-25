package com.macro.mall.selfcheck.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.selfcheck.dto.OrderCreateParam;
import com.macro.mall.selfcheck.dto.OrderVO;
import com.macro.mall.selfcheck.service.SelfcheckOrderService;
import com.macro.mall.selfcheck.util.StpMemberUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 自助收银订单Controller
 * 
 * @author macro
 * @date 2025/01/22
 */
@Slf4j
@Tag(name = "SelfcheckOrderController", description = "自助收银订单管理")
@RestController
@RequestMapping("/order")
public class SelfcheckOrderController {

    @Autowired
    private SelfcheckOrderService orderService;

    @Operation(summary = "快速下单", description = "扫码后直接下单单个商品")
    @PostMapping("/createQuick")
    public CommonResult<Map<String, Object>> createQuickOrder(
            @Parameter(description = "商品ID", required = true) @RequestParam Long productId,
            @Parameter(description = "SKU ID") @RequestParam(required = false) Long skuId,
            @Parameter(description = "购买数量") @RequestParam(defaultValue = "1") Integer quantity,
            @Parameter(description = "游客ID（游客模式必传）") @RequestParam(required = false) String guestId,
            @Parameter(description = "终端编码") @RequestParam(required = false) String terminalCode,
            @Parameter(description = "设备信息") @RequestParam(required = false) String deviceInfo) {
        try {
            // 验证用户身份
            if (!StpMemberUtil.isLogin() && (guestId == null || guestId.trim().isEmpty())) {
                return CommonResult.unauthorized("用户未登录且未提供游客ID");
            }

            Map<String, Object> result = orderService.createQuickOrder(
                productId, skuId, quantity, guestId, terminalCode, deviceInfo);

            log.info("快速下单成功，商品ID：{}，用户类型：{}", productId, StpMemberUtil.isLogin() ? "会员" : "游客");
            return CommonResult.success(result);

        } catch (Exception e) {
            log.error("快速下单失败，商品ID：{}，错误：{}", productId, e.getMessage(), e);
            return CommonResult.failed("快速下单失败：" + e.getMessage());
        }
    }

    @Operation(summary = "购物车下单", description = "将购物车中的商品批量下单")
    @PostMapping("/createFromCart")
    public CommonResult<Map<String, Object>> createCartOrder(
            @Parameter(description = "购物车商品ID列表") @RequestParam(required = false) List<Long> cartItemIds,
            @Parameter(description = "订单创建参数", required = true) @Valid @RequestBody OrderCreateParam orderParam) {
        try {
            // 验证用户身份
            if (!StpMemberUtil.isLogin() && (orderParam.getGuestId() == null || orderParam.getGuestId().trim().isEmpty())) {
                return CommonResult.unauthorized("用户未登录且未提供游客ID");
            }

            Map<String, Object> result = orderService.createCartOrder(cartItemIds, orderParam);

            log.info("购物车下单成功，订单类型：{}，用户类型：{}", orderParam.getOrderType(), StpMemberUtil.isLogin() ? "会员" : "游客");
            return CommonResult.success(result);

        } catch (Exception e) {
            log.error("购物车下单失败，错误：{}", e.getMessage(), e);
            return CommonResult.failed("购物车下单失败：" + e.getMessage());
        }
    }

    @Operation(summary = "创建订单", description = "通用订单创建接口")
    @PostMapping("/create")
    public CommonResult<Map<String, Object>> createOrder(
            @Parameter(description = "订单创建参数", required = true) @Valid @RequestBody OrderCreateParam orderParam) {
        try {
            // 验证用户身份
            if (!StpMemberUtil.isLogin() && (orderParam.getGuestId() == null || orderParam.getGuestId().trim().isEmpty())) {
                return CommonResult.unauthorized("用户未登录且未提供游客ID");
            }

            Map<String, Object> result = orderService.createOrder(orderParam);

            log.info("创建订单成功，订单类型：{}，用户类型：{}", orderParam.getOrderType(), StpMemberUtil.isLogin() ? "会员" : "游客");
            return CommonResult.success(result);

        } catch (Exception e) {
            log.error("创建订单失败，错误：{}", e.getMessage(), e);
            return CommonResult.failed("创建订单失败：" + e.getMessage());
        }
    }

    @Operation(summary = "计算订单金额", description = "计算订单总价，包含优惠券、积分等")
    @PostMapping("/calculate")
    public CommonResult<Map<String, Object>> calculateOrderAmount(
            @Parameter(description = "订单创建参数", required = true) @Valid @RequestBody OrderCreateParam orderParam) {
        try {
            // 验证用户身份
            if (!StpMemberUtil.isLogin() && (orderParam.getGuestId() == null || orderParam.getGuestId().trim().isEmpty())) {
                return CommonResult.unauthorized("用户未登录且未提供游客ID");
            }

            Map<String, Object> result = orderService.calculateOrderAmount(orderParam);

            log.info("计算订单金额成功，用户类型：{}", StpMemberUtil.isLogin() ? "会员" : "游客");
            return CommonResult.success(result);

        } catch (Exception e) {
            log.error("计算订单金额失败，错误：{}", e.getMessage(), e);
            return CommonResult.failed("计算订单金额失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取订单详情", description = "根据订单ID获取订单详细信息")
    @GetMapping("/detail/{orderId}")
    public CommonResult<OrderVO> getOrderDetail(
            @Parameter(description = "订单ID", required = true) @PathVariable Long orderId,
            @Parameter(description = "游客ID（游客模式必传）") @RequestParam(required = false) String guestId) {
        try {
            // 验证用户身份
            if (!StpMemberUtil.isLogin() && (guestId == null || guestId.trim().isEmpty())) {
                return CommonResult.unauthorized("用户未登录且未提供游客ID");
            }

            OrderVO order = orderService.getOrderDetail(orderId, guestId);

            if (order == null) {
                return CommonResult.failed("订单不存在或无权限访问");
            }

            return CommonResult.success(order);

        } catch (Exception e) {
            log.error("获取订单详情失败，订单ID：{}，错误：{}", orderId, e.getMessage(), e);
            return CommonResult.failed("获取订单详情失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取订单状态", description = "查询订单状态和支付状态")
    @GetMapping("/status/{orderId}")
    public CommonResult<Map<String, Object>> getOrderStatus(
            @Parameter(description = "订单ID", required = true) @PathVariable Long orderId,
            @Parameter(description = "游客ID（游客模式必传）") @RequestParam(required = false) String guestId) {
        try {
            // 验证用户身份
            if (!StpMemberUtil.isLogin() && (guestId == null || guestId.trim().isEmpty())) {
                return CommonResult.unauthorized("用户未登录且未提供游客ID");
            }

            Map<String, Object> result = orderService.getOrderStatus(orderId, guestId);

            return CommonResult.success(result);

        } catch (Exception e) {
            log.error("获取订单状态失败，订单ID：{}，错误：{}", orderId, e.getMessage(), e);
            return CommonResult.failed("获取订单状态失败：" + e.getMessage());
        }
    }

    @Operation(summary = "取消订单", description = "取消未支付的订单")
    @PostMapping("/cancel/{orderId}")
    public CommonResult<Boolean> cancelOrder(
            @Parameter(description = "订单ID", required = true) @PathVariable Long orderId,
            @Parameter(description = "游客ID（游客模式必传）") @RequestParam(required = false) String guestId,
            @Parameter(description = "取消原因") @RequestParam(required = false) String reason) {
        try {
            // 验证用户身份
            if (!StpMemberUtil.isLogin() && (guestId == null || guestId.trim().isEmpty())) {
                return CommonResult.unauthorized("用户未登录且未提供游客ID");
            }

            boolean success = orderService.cancelOrder(orderId, guestId, reason);

            if (success) {
                log.info("取消订单成功，订单ID：{}", orderId);
                return CommonResult.success(true);
            } else {
                return CommonResult.failed("取消订单失败");
            }

        } catch (Exception e) {
            log.error("取消订单失败，订单ID：{}，错误：{}", orderId, e.getMessage(), e);
            return CommonResult.failed("取消订单失败：" + e.getMessage());
        }
    }

    @Operation(summary = "确认收货", description = "确认收货完成订单")
    @PostMapping("/confirmReceive/{orderId}")
    public CommonResult<Boolean> confirmReceiveOrder(
            @Parameter(description = "订单ID", required = true) @PathVariable Long orderId,
            @Parameter(description = "游客ID（游客模式必传）") @RequestParam(required = false) String guestId) {
        try {
            // 验证用户身份
            if (!StpMemberUtil.isLogin() && (guestId == null || guestId.trim().isEmpty())) {
                return CommonResult.unauthorized("用户未登录且未提供游客ID");
            }

            boolean success = orderService.confirmReceiveOrder(orderId, guestId);

            if (success) {
                log.info("确认收货成功，订单ID：{}", orderId);
                return CommonResult.success(true);
            } else {
                return CommonResult.failed("确认收货失败");
            }

        } catch (Exception e) {
            log.error("确认收货失败，订单ID：{}，错误：{}", orderId, e.getMessage(), e);
            return CommonResult.failed("确认收货失败：" + e.getMessage());
        }
    }

    @Operation(summary = "订单历史", description = "获取会员订单历史记录")
    @GetMapping("/history")
    public CommonResult<CommonPage<OrderVO>> getOrderHistory(
            @Parameter(description = "订单状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            // 检查登录状态
            if (!StpMemberUtil.isLogin()) {
                return CommonResult.unauthorized("用户未登录");
            }

            CommonPage<OrderVO> orderPage = orderService.getOrderHistory(status, pageNum, pageSize);

            return CommonResult.success(orderPage);

        } catch (Exception e) {
            log.error("获取订单历史失败，错误：{}", e.getMessage(), e);
            return CommonResult.failed("获取订单历史失败：" + e.getMessage());
        }
    }

    @Operation(summary = "游客订单历史", description = "获取游客订单历史记录")
    @GetMapping("/guestHistory")
    public CommonResult<CommonPage<OrderVO>> getGuestOrderHistory(
            @Parameter(description = "游客ID", required = true) @RequestParam String guestId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            if (guestId == null || guestId.trim().isEmpty()) {
                return CommonResult.validateFailed("游客ID不能为空");
            }

            CommonPage<OrderVO> orderPage = orderService.getGuestOrderHistory(guestId, pageNum, pageSize);

            return CommonResult.success(orderPage);

        } catch (Exception e) {
            log.error("获取游客订单历史失败，游客ID：{}，错误：{}", guestId, e.getMessage(), e);
            return CommonResult.failed("获取游客订单历史失败：" + e.getMessage());
        }
    }

    @Operation(summary = "验证库存", description = "验证订单商品库存是否充足")
    @PostMapping("/validateStock")
    public CommonResult<Map<String, Object>> validateOrderStock(
            @Parameter(description = "订单创建参数", required = true) @Valid @RequestBody OrderCreateParam orderParam) {
        try {
            Map<String, Object> result = orderService.validateOrderStock(orderParam);

            return CommonResult.success(result);

        } catch (Exception e) {
            log.error("验证库存失败，错误：{}", e.getMessage(), e);
            return CommonResult.failed("验证库存失败：" + e.getMessage());
        }
    }

    @Operation(summary = "支付成功回调", description = "支付成功后的处理")
    @PostMapping("/paymentSuccess/{orderId}")
    public CommonResult<Boolean> paymentSuccess(
            @Parameter(description = "订单ID", required = true) @PathVariable Long orderId,
            @Parameter(description = "支付方式") @RequestParam Integer payType,
            @Parameter(description = "支付流水号") @RequestParam String transactionId) {
        try {
            boolean success = orderService.paymentSuccess(orderId, payType, transactionId);

            if (success) {
                log.info("支付成功处理完成，订单ID：{}，流水号：{}", orderId, transactionId);
                return CommonResult.success(true);
            } else {
                return CommonResult.failed("支付成功处理失败");
            }

        } catch (Exception e) {
            log.error("支付成功处理失败，订单ID：{}，错误：{}", orderId, e.getMessage(), e);
            return CommonResult.failed("支付成功处理失败：" + e.getMessage());
        }
    }

    @Operation(summary = "支付失败回调", description = "支付失败后的处理")
    @PostMapping("/paymentFailed/{orderId}")
    public CommonResult<Boolean> paymentFailed(
            @Parameter(description = "订单ID", required = true) @PathVariable Long orderId,
            @Parameter(description = "失败原因") @RequestParam String reason) {
        try {
            boolean success = orderService.paymentFailed(orderId, reason);

            if (success) {
                log.info("支付失败处理完成，订单ID：{}，原因：{}", orderId, reason);
                return CommonResult.success(true);
            } else {
                return CommonResult.failed("支付失败处理失败");
            }

        } catch (Exception e) {
            log.error("支付失败处理失败，订单ID：{}，错误：{}", orderId, e.getMessage(), e);
            return CommonResult.failed("支付失败处理失败：" + e.getMessage());
        }
    }

    @Operation(summary = "扫码核销订单", description = "通过扫描核销码完成订单核销")
    @PostMapping("/pickup")
    public CommonResult<Map<String, Object>> pickupOrder(
            @Parameter(description = "核销码", required = true) @RequestParam String pickupCode,
            @Parameter(description = "操作员", required = false) @RequestParam(defaultValue = "自助设备") String operator,
            @Parameter(description = "设备编码") @RequestParam(required = false) String deviceCode) {
        try {
            Map<String, Object> result = orderService.pickupOrder(pickupCode, operator, deviceCode);
            log.info("自助设备核销成功，核销码：{}，操作员：{}", pickupCode, operator);
            return CommonResult.success(result, "核销成功");
        } catch (Exception e) {
            log.error("自助设备核销失败，核销码：{}，错误：{}", pickupCode, e.getMessage(), e);
            return CommonResult.failed(e.getMessage());
        }
    }
} 