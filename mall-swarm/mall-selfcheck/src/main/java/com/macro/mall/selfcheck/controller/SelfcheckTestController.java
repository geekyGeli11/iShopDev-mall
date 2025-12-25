package com.macro.mall.selfcheck.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.selfcheck.util.StpMemberUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 自助结算系统测试Controller
 * 
 * @author macro
 * @since 1.0.0
 */
@RestController
@RequestMapping("/test")
@Tag(name = "SelfcheckTestController", description = "自助结算系统测试接口")
public class SelfcheckTestController {

    @Operation(summary = "健康检查")
    @GetMapping("/health")
    public CommonResult<String> health() {
        return CommonResult.success("mall-selfcheck service is running");
    }

    @Operation(summary = "获取服务信息")
    @GetMapping("/info")
    public CommonResult<String> info() {
        return CommonResult.success("mall-selfcheck v1.0.0 - Self Checkout System");
    }

    @Operation(summary = "认证测试接口（需要登录）")
    @GetMapping("/authTest")
    public CommonResult<Map<String, Object>> authTest() {
        // 这个接口需要会员登录后才能访问
        Long memberId = StpMemberUtil.getLoginIdAsLong();
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "认证测试成功");
        result.put("memberId", memberId);
        result.put("isLogin", StpMemberUtil.isLogin());
        result.put("tokenTimeout", StpMemberUtil.getTokenTimeout());
        
        return CommonResult.success(result);
    }
} 