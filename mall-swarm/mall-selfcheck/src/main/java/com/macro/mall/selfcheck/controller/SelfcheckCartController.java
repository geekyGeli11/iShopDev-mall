package com.macro.mall.selfcheck.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.selfcheck.dto.CartItemParam;
import com.macro.mall.selfcheck.dto.CartVO;
import com.macro.mall.selfcheck.dto.MemberCouponVO;
import com.macro.mall.selfcheck.service.SelfcheckCartService;
import com.macro.mall.selfcheck.util.StpMemberUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自助收银购物车管理Controller
 * 
 * @author macro
 * @since 1.0.0
 */
@Slf4j
@Tag(name = "SelfcheckCartController", description = "自助收银购物车管理")
@RestController
@RequestMapping("/cart")
public class SelfcheckCartController {

    @Autowired
    private SelfcheckCartService cartService;

    @Operation(summary = "添加商品到购物车", description = "支持会员和游客模式，会员购物车持久化，游客购物车临时存储")
    @PostMapping("/addItem")
    public CommonResult<Boolean> addItem(@Valid @RequestBody CartItemParam param) {
        try {
            log.info("添加商品到购物车: {}", param);
            
            boolean success = cartService.addItem(param);
            if (success) {
                return CommonResult.success(true, "商品已添加到购物车");
            } else {
                return CommonResult.failed("添加商品到购物车失败");
            }
        } catch (Exception e) {
            log.error("添加商品到购物车异常", e);
            return CommonResult.failed("添加商品到购物车失败: " + e.getMessage());
        }
    }

    @Operation(summary = "更新购物车商品数量", description = "更新指定商品的购买数量")
    @PutMapping("/updateItem")
    public CommonResult<Boolean> updateItem(@Valid @RequestBody CartItemParam param) {
        try {
            log.info("更新购物车商品: {}", param);
            
            boolean success = cartService.updateItem(param);
            if (success) {
                return CommonResult.success(true, "商品数量已更新");
            } else {
                return CommonResult.failed("更新商品数量失败");
            }
        } catch (Exception e) {
            log.error("更新购物车商品异常", e);
            return CommonResult.failed("更新商品数量失败: " + e.getMessage());
        }
    }

    @Operation(summary = "删除购物车商品", description = "从购物车中删除指定商品")
    @DeleteMapping("/removeItem")
    public CommonResult<Boolean> removeItem(@Valid @RequestBody CartItemParam param) {
        try {
            log.info("删除购物车商品: {}", param);
            
            boolean success = cartService.removeItem(param);
            if (success) {
                return CommonResult.success(true, "商品已从购物车删除");
            } else {
                return CommonResult.failed("删除商品失败");
            }
        } catch (Exception e) {
            log.error("删除购物车商品异常", e);
            return CommonResult.failed("删除商品失败: " + e.getMessage());
        }
    }

    @Operation(summary = "清空购物车", description = "清空当前用户的购物车")
    @DeleteMapping("/clear")
    public CommonResult<Boolean> clearCart(
            @Parameter(description = "游客ID（游客模式必传）") @RequestParam(required = false) String guestId) {
        try {
            log.info("清空购物车, guestId: {}", guestId);
            
            boolean success = cartService.clearCart(guestId);
            if (success) {
                return CommonResult.success(true, "购物车已清空");
            } else {
                return CommonResult.failed("清空购物车失败");
            }
        } catch (Exception e) {
            log.error("清空购物车异常", e);
            return CommonResult.failed("清空购物车失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取购物车信息", description = "获取当前用户的购物车详细信息")
    @GetMapping("/list")
    public CommonResult<CartVO> getCart(
            @Parameter(description = "游客ID（游客模式必传）") @RequestParam(required = false) String guestId) {
        try {
            log.info("获取购物车信息, guestId: {}", guestId);
            
            CartVO cart = cartService.getCart(guestId);
            return CommonResult.success(cart);
        } catch (Exception e) {
            log.error("获取购物车信息异常", e);
            return CommonResult.failed("获取购物车信息失败: " + e.getMessage());
        }
    }

    @Operation(summary = "计算购物车价格", description = "计算购物车总价，支持应用优惠券和积分")
    @PostMapping("/calculate")
    public CommonResult<CartVO> calculateCart(
            @Parameter(description = "游客ID（游客模式必传）") @RequestParam(required = false) String guestId,
            @Parameter(description = "应用的优惠券ID列表") @RequestParam(required = false) List<Long> couponIds,
            @Parameter(description = "使用的积分数量") @RequestParam(required = false) Integer usePoints) {
        try {
            log.info("计算购物车价格, guestId: {}, couponIds: {}, usePoints: {}", guestId, couponIds, usePoints);
            
            CartVO cart = cartService.calculateCart(guestId, couponIds, usePoints);
            return CommonResult.success(cart);
        } catch (Exception e) {
            log.error("计算购物车价格异常", e);
            return CommonResult.failed("计算购物车价格失败: " + e.getMessage());
        }
    }

    @Operation(summary = "应用优惠券", description = "将优惠券应用到购物车（仅会员可用）")
    @PostMapping("/applyCoupon")
    public CommonResult<Boolean> applyCoupon(
            @Parameter(description = "游客ID（游客模式必传）") @RequestParam(required = false) String guestId,
            @Parameter(description = "优惠券ID", required = true) @RequestParam Long couponId) {
        try {
            log.info("应用优惠券, guestId: {}, couponId: {}", guestId, couponId);
            
            if (!StpMemberUtil.isLogin()) {
                return CommonResult.failed("请先登录会员账号");
            }
            
            boolean success = cartService.applyCoupon(guestId, couponId);
            if (success) {
                return CommonResult.success(true, "优惠券已应用");
            } else {
                return CommonResult.failed("应用优惠券失败");
            }
        } catch (Exception e) {
            log.error("应用优惠券异常", e);
            return CommonResult.failed("应用优惠券失败: " + e.getMessage());
        }
    }

    @Operation(summary = "移除优惠券", description = "从购物车中移除优惠券")
    @DeleteMapping("/removeCoupon")
    public CommonResult<Boolean> removeCoupon(
            @Parameter(description = "游客ID（游客模式必传）") @RequestParam(required = false) String guestId,
            @Parameter(description = "优惠券ID", required = true) @RequestParam Long couponId) {
        try {
            log.info("移除优惠券, guestId: {}, couponId: {}", guestId, couponId);
            
            boolean success = cartService.removeCoupon(guestId, couponId);
            if (success) {
                return CommonResult.success(true, "优惠券已移除");
            } else {
                return CommonResult.failed("移除优惠券失败");
            }
        } catch (Exception e) {
            log.error("移除优惠券异常", e);
            return CommonResult.failed("移除优惠券失败: " + e.getMessage());
        }
    }

    @Operation(summary = "批量更新购物车", description = "批量添加、更新或删除购物车商品")
    @PostMapping("/batchUpdate")
    public CommonResult<Boolean> batchUpdate(
            @Parameter(description = "购物车商品操作列表", required = true) @Valid @RequestBody List<CartItemParam> items,
            @Parameter(description = "游客ID（游客模式必传）") @RequestParam(required = false) String guestId) {
        try {
            log.info("批量更新购物车, items: {}, guestId: {}", items.size(), guestId);
            
            boolean success = cartService.batchUpdate(items, guestId);
            if (success) {
                return CommonResult.success(true, "购物车批量更新成功");
            } else {
                return CommonResult.failed("购物车批量更新失败");
            }
        } catch (Exception e) {
            log.error("批量更新购物车异常", e);
            return CommonResult.failed("购物车批量更新失败: " + e.getMessage());
        }
    }

    @Operation(summary = "同步购物车", description = "同步购物车数据，验证商品有效性、价格、库存等")
    @PostMapping("/sync")
    public CommonResult<CartVO> syncCart(
            @Parameter(description = "游客ID（游客模式必传）") @RequestParam(required = false) String guestId) {
        try {
            log.info("同步购物车, guestId: {}", guestId);
            
            CartVO cart = cartService.syncCart(guestId);
            return CommonResult.success(cart, "购物车已同步");
        } catch (Exception e) {
            log.error("同步购物车异常", e);
            return CommonResult.failed("同步购物车失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取购物车商品数量", description = "获取购物车中商品的总数量")
    @GetMapping("/count")
    public CommonResult<Integer> getCartItemCount(
            @Parameter(description = "游客ID（游客模式必传）") @RequestParam(required = false) String guestId) {
        try {
            log.info("获取购物车商品数量, guestId: {}", guestId);
            
            int count = cartService.getCartItemCount(guestId);
            return CommonResult.success(count);
        } catch (Exception e) {
            log.error("获取购物车商品数量异常", e);
            return CommonResult.failed("获取购物车商品数量失败: " + e.getMessage());
        }
    }

    @Operation(summary = "检查商品是否在购物车中", description = "检查指定商品是否已在购物车中")
    @GetMapping("/contains")
    public CommonResult<Boolean> isProductInCart(
            @Parameter(description = "商品ID", required = true) @RequestParam Long productId,
            @Parameter(description = "SKU ID") @RequestParam(required = false) Long skuId,
            @Parameter(description = "游客ID（游客模式必传）") @RequestParam(required = false) String guestId) {
        try {
            log.info("检查商品是否在购物车中, productId: {}, skuId: {}, guestId: {}", productId, skuId, guestId);
            
            boolean exists = cartService.isProductInCart(productId, skuId, guestId);
            return CommonResult.success(exists);
        } catch (Exception e) {
            log.error("检查商品是否在购物车中异常", e);
            return CommonResult.failed("检查失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取可用优惠券", description = "获取当前购物车可使用的优惠券列表（仅会员可用）")
    @GetMapping("/availableCoupons")
    public CommonResult<List<MemberCouponVO>> getAvailableCoupons(
            @Parameter(description = "游客ID（游客模式必传）") @RequestParam(required = false) String guestId) {
        try {
            log.info("获取可用优惠券, guestId: {}", guestId);
            
            if (!StpMemberUtil.isLogin()) {
                return CommonResult.failed("请先登录会员账号");
            }
            
            List<MemberCouponVO> coupons = cartService.getAvailableCoupons(guestId);
            return CommonResult.success(coupons);
        } catch (Exception e) {
            log.error("获取可用优惠券异常", e);
            return CommonResult.failed("获取可用优惠券失败: " + e.getMessage());
        }
    }

    @Operation(summary = "删除无效商品", description = "删除购物车中的无效商品（已下架、删除、库存不足等）")
    @DeleteMapping("/removeInvalid")
    public CommonResult<Map<String, Object>> removeInvalidItems(
            @Parameter(description = "游客ID（游客模式必传）") @RequestParam(required = false) String guestId) {
        try {
            log.info("删除无效商品, guestId: {}", guestId);
            
            int removedCount = cartService.removeInvalidItems(guestId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("removedCount", removedCount);
            result.put("message", "已删除 " + removedCount + " 个无效商品");
            
            return CommonResult.success(result);
        } catch (Exception e) {
            log.error("删除无效商品异常", e);
            return CommonResult.failed("删除无效商品失败: " + e.getMessage());
        }
    }

    @Operation(summary = "扫码添加商品", description = "扫码快速添加商品到购物车")
    @PostMapping("/scanAdd")
    public CommonResult<CartVO> scanAddItem(
            @Parameter(description = "商品ID", required = true) @RequestParam Long productId,
            @Parameter(description = "SKU ID") @RequestParam(required = false) Long skuId,
            @Parameter(description = "数量", required = false) @RequestParam(defaultValue = "1") Integer quantity,
            @Parameter(description = "游客ID（游客模式必传）") @RequestParam(required = false) String guestId) {
        try {
            log.info("扫码添加商品, productId: {}, skuId: {}, quantity: {}, guestId: {}", 
                productId, skuId, quantity, guestId);
            
            // 构建购物车参数
            CartItemParam param = new CartItemParam();
            param.setProductId(productId);
            param.setSkuId(skuId);
            param.setQuantity(quantity);
            param.setOperation("ADD");
            param.setGuestId(guestId);
            param.setRemark("扫码添加");
            
            // 添加商品到购物车
            boolean success = cartService.addItem(param);
            if (!success) {
                return CommonResult.failed("添加商品到购物车失败");
            }
            
            // 返回更新后的购物车信息
            CartVO cart = cartService.getCart(guestId);
            return CommonResult.success(cart, "商品已添加到购物车");
            
        } catch (Exception e) {
            log.error("扫码添加商品异常", e);
            return CommonResult.failed("扫码添加商品失败: " + e.getMessage());
        }
    }
} 