package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsPointsExchangeLog;
import com.macro.mall.portal.domain.ExchangeResult;
import com.macro.mall.portal.domain.ExchangePayOrderResult;
import com.macro.mall.portal.domain.PointsCouponConfigDetail;
import com.macro.mall.portal.domain.PointsProductConfigDetail;
import com.macro.mall.portal.dto.CouponExchangeParam;
import com.macro.mall.portal.dto.ExchangePayOrderParam;
import com.macro.mall.portal.dto.ExchangePaySuccessParam;
import com.macro.mall.portal.dto.PointsExchangeQueryParam;
import com.macro.mall.portal.dto.ProductExchangeParam;
import com.macro.mall.portal.service.UmsPointsExchangeService;
import com.macro.mall.portal.service.UmsMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 积分换购Controller
 * Created by macro on 2024/01/20.
 */
@RestController
@Tag(name = "UmsPointsExchangeController", description = "积分换购管理")
@RequestMapping("/app/points")
public class UmsPointsExchangeController {
    
    @Autowired
    private UmsPointsExchangeService pointsExchangeService;
    @Autowired
    private UmsMemberService memberService;
    
    @Operation(summary = "获取换购商品列表")
    @GetMapping("/product/list")
    public CommonResult<CommonPage<PointsProductConfigDetail>> getProductList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "productCategoryId", required = false) Long productCategoryId,
            @RequestParam(value = "minPointsPrice", required = false) Integer minPointsPrice,
            @RequestParam(value = "maxPointsPrice", required = false) Integer maxPointsPrice,
            @RequestParam(value = "sortType", defaultValue = "0") Integer sortType,
            @RequestParam(value = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        
        PointsExchangeQueryParam queryParam = new PointsExchangeQueryParam();
        queryParam.setConfigType(1); // 商品换购
        queryParam.setKeyword(keyword);
        queryParam.setProductCategoryId(productCategoryId);
        queryParam.setMinPointsPrice(minPointsPrice);
        queryParam.setMaxPointsPrice(maxPointsPrice);
        queryParam.setSortType(sortType);
        queryParam.setOnlyAvailable(onlyAvailable);
        
        List<PointsProductConfigDetail> productList = pointsExchangeService.getProductList(queryParam, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(productList));
    }
    
    @Operation(summary = "获取换购商品详情")
    @GetMapping("/product/detail/{configId}")
    public CommonResult<PointsProductConfigDetail> getProductDetail(@PathVariable Long configId) {
        PointsProductConfigDetail detail = pointsExchangeService.getProductDetail(configId);
        if (detail == null) {
            return CommonResult.failed("商品换购配置不存在");
        }
        return CommonResult.success(detail);
    }
    
    @Operation(summary = "兑换商品")
    @PostMapping("/product/exchange")
    public CommonResult<ExchangeResult> exchangeProduct(@Valid @RequestBody ProductExchangeParam param) {
        try {
            ExchangeResult result = pointsExchangeService.exchangeProduct(param);
            if (result.getExchangeStatus() == 1) {
                return CommonResult.success(result, "兑换成功");
            } else {
                return CommonResult.failed(result.getMessage());
            }
        } catch (Exception e) {
            return CommonResult.failed("兑换失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "检查商品兑换资格")
    @GetMapping("/product/checkEligible/{configId}")
    public CommonResult<Boolean> checkProductExchangeEligible(@PathVariable Long configId) {
        try {
            Long memberId = memberService.getCurrentMember().getId();
            boolean eligible = pointsExchangeService.checkProductExchangeEligible(memberId, configId);
            return CommonResult.success(eligible);
        } catch (Exception e) {
            return CommonResult.failed("检查失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "获取换购优惠券列表")
    @GetMapping("/coupon/list")
    public CommonResult<CommonPage<PointsCouponConfigDetail>> getCouponList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "minPointsPrice", required = false) Integer minPointsPrice,
            @RequestParam(value = "maxPointsPrice", required = false) Integer maxPointsPrice,
            @RequestParam(value = "sortType", defaultValue = "0") Integer sortType,
            @RequestParam(value = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        
        PointsExchangeQueryParam queryParam = new PointsExchangeQueryParam();
        queryParam.setConfigType(2); // 优惠券兑换
        queryParam.setKeyword(keyword);
        queryParam.setMinPointsPrice(minPointsPrice);
        queryParam.setMaxPointsPrice(maxPointsPrice);
        queryParam.setSortType(sortType);
        queryParam.setOnlyAvailable(onlyAvailable);
        
        List<PointsCouponConfigDetail> couponList = pointsExchangeService.getCouponList(queryParam, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(couponList));
    }
    
    @Operation(summary = "获取换购优惠券详情")
    @GetMapping("/coupon/detail/{configId}")
    public CommonResult<PointsCouponConfigDetail> getCouponDetail(@PathVariable Long configId) {
        PointsCouponConfigDetail detail = pointsExchangeService.getCouponDetail(configId);
        if (detail == null) {
            return CommonResult.failed("优惠券兑换配置不存在");
        }
        return CommonResult.success(detail);
    }
    
    @Operation(summary = "兑换优惠券")
    @PostMapping("/coupon/exchange")
    public CommonResult<ExchangeResult> exchangeCoupon(@Valid @RequestBody CouponExchangeParam param) {
        try {
            ExchangeResult result = pointsExchangeService.exchangeCoupon(param);
            if (result.getExchangeStatus() == 1) {
                return CommonResult.success(result, "兑换成功");
            } else {
                return CommonResult.failed(result.getMessage());
            }
        } catch (Exception e) {
            return CommonResult.failed("兑换失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "检查优惠券兑换资格")
    @GetMapping("/coupon/checkEligible/{configId}")
    public CommonResult<Boolean> checkCouponExchangeEligible(@PathVariable Long configId) {
        try {
            Long memberId = memberService.getCurrentMember().getId();
            boolean eligible = pointsExchangeService.checkCouponExchangeEligible(memberId, configId);
            return CommonResult.success(eligible);
        } catch (Exception e) {
            return CommonResult.failed("检查失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "获取用户兑换记录")
    @GetMapping("/exchange/list")
    public CommonResult<CommonPage<UmsPointsExchangeLog>> getUserExchangeList(
            @RequestParam(value = "exchangeType", required = false) Byte exchangeType,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        try {
            UmsMember currentMember = memberService.getCurrentMember();
            if (currentMember == null) {
                return CommonResult.failed("请先登录");
            }

            List<UmsPointsExchangeLog> logList = pointsExchangeService.getUserExchangeList(currentMember.getId(), exchangeType, pageNum, pageSize);
            return CommonResult.success(CommonPage.restPage(logList));
        } catch (Exception e) {
            return CommonResult.failed("获取记录失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "获取兑换记录详情")
    @GetMapping("/exchange/detail/{id}")
    public CommonResult<UmsPointsExchangeLog> getExchangeDetail(@PathVariable Long id) {
        try {
            UmsMember currentMember = memberService.getCurrentMember();
            if (currentMember == null) {
                return CommonResult.failed("请先登录");
            }

            UmsPointsExchangeLog exchangeLog = pointsExchangeService.getExchangeDetail(id, currentMember.getId());
            if (exchangeLog == null) {
                return CommonResult.failed("兑换记录不存在或无权限查看");
            }
            return CommonResult.success(exchangeLog);
        } catch (Exception e) {
            return CommonResult.failed("获取兑换记录失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "获取用户积分余额")
    @GetMapping("/balance")
    public CommonResult<Integer> getUserPointsBalance() {
        try {
            Long memberId = memberService.getCurrentMember().getId();
            Integer balance = pointsExchangeService.getUserPointsBalance(memberId);
            return CommonResult.success(balance);
        } catch (Exception e) {
            return CommonResult.failed("获取积分余额失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "创建积分兑换支付订单")
    @PostMapping("/exchange/createPayOrder")
    public CommonResult<ExchangePayOrderResult> createExchangePayOrder(@Valid @RequestBody ExchangePayOrderParam param) {
        try {
            ExchangePayOrderResult result = pointsExchangeService.createExchangePayOrder(param);
            return CommonResult.success(result);
        } catch (Exception e) {
            return CommonResult.failed("创建支付订单失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "积分兑换支付成功回调")
    @PostMapping("/exchange/paySuccess")
    public CommonResult<String> exchangePaySuccess(@Valid @RequestBody ExchangePaySuccessParam param) {
        try {
            boolean success = pointsExchangeService.handleExchangePaySuccess(param);
            if (success) {
                return CommonResult.success("支付成功");
            } else {
                return CommonResult.failed("支付处理失败");
            }
        } catch (Exception e) {
            return CommonResult.failed("支付回调处理失败：" + e.getMessage());
        }
    }

    @Operation(summary = "创建商品兑换记录（仅记录，不扣积分库存）")
    @PostMapping("/product/createExchangeRecord")
    public CommonResult<String> createProductExchangeRecord(@Valid @RequestBody ProductExchangeParam param) {
        try {
            boolean success = pointsExchangeService.createProductExchangeRecord(param);
            if (success) {
                return CommonResult.success("兑换记录创建成功");
            } else {
                return CommonResult.failed("兑换记录创建失败");
            }
        } catch (Exception e) {
            return CommonResult.failed("创建兑换记录失败：" + e.getMessage());
        }
    }
}