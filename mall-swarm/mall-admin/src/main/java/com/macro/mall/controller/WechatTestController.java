package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.service.WechatMiniProgramService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信小程序API测试Controller
 * Created by macro on 2024/12/30.
 */
@Controller
@Tag(name = "WechatTestController", description = "微信小程序API测试")
@RequestMapping("/wechat/test")
public class WechatTestController {
    
    @Autowired
    private WechatMiniProgramService wechatMiniProgramService;
    
    @Operation(summary = "测试获取access_token")
    @RequestMapping(value = "/token", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> testGetAccessToken() {
        Map<String, Object> result = new HashMap<>();
        
        boolean configAvailable = wechatMiniProgramService.isConfigAvailable();
        result.put("configAvailable", configAvailable);
        
        if (configAvailable) {
            String accessToken = wechatMiniProgramService.getAccessToken();
            result.put("accessToken", accessToken != null ? "获取成功" : "获取失败");
            result.put("hasToken", accessToken != null);
        } else {
            result.put("message", "微信小程序配置不完整");
        }
        
        return CommonResult.success(result);
    }
    
    @Operation(summary = "测试生成URL Link")
    @RequestMapping(value = "/urllink", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<String> testGenerateUrlLink(@RequestParam String path, @RequestParam String query) {
        String urlLink = wechatMiniProgramService.generateUrlLink(path, query);
        if (urlLink != null) {
            return CommonResult.success(urlLink, "URL Link生成成功");
        } else {
            return CommonResult.failed("URL Link生成失败");
        }
    }
    
    @Operation(summary = "测试生成小程序码")
    @RequestMapping(value = "/qrcode", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<String> testGenerateMiniProgramCode(@RequestParam String scene, @RequestParam String page) {
        String qrCodeUrl = wechatMiniProgramService.generateMiniProgramCode(scene, page, 430);
        if (qrCodeUrl != null) {
            return CommonResult.success(qrCodeUrl, "小程序码生成成功");
        } else {
            return CommonResult.failed("小程序码生成失败");
        }
    }
}
