package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.service.InviteService;
import com.macro.mall.portal.service.WxMiniProgramService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信功能管理
 * Created on 2024/01/20.
 */
@RestController
@RequestMapping("/api/wechat")
@Tag(name = "微信功能管理", description = "微信小程序相关功能")
public class WechatController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WechatController.class);

    @Autowired
    private InviteService inviteService;
    
    @Autowired
    private WxMiniProgramService wxMiniProgramService;
    
    @Value("${wechat.app-id}")
    private String appId;

    @Value("${wechat.app-secret}")
    private String appSecret;

    @Operation(summary = "生成带参数小程序码")
    @PostMapping("/generate-qrcode")
    public CommonResult<Map<String, Object>> generateQrCode(@RequestParam String inviteParam) {
        return inviteService.generateQrCode(inviteParam);
    }
    
    @Operation(summary = "测试微信配置")
    @GetMapping("/test-config")
    public CommonResult<Map<String, Object>> testWechatConfig() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 检查配置
            result.put("appId", appId);
            result.put("appSecretConfigured", appSecret != null && !appSecret.isEmpty());
            
            if (appId == null || appId.isEmpty()) {
                return CommonResult.failed("微信AppId未配置");
            }
            if (appSecret == null || appSecret.isEmpty()) {
                return CommonResult.failed("微信AppSecret未配置");
            }
            
            // 测试获取access_token
            String url = String.format(
                    "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s",
                    appId, appSecret
            );
            
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);
            
            LOGGER.info("微信access_token测试响应: {}", response);
            result.put("access_token_response", response);
            
            if (response != null && response.contains("access_token")) {
                result.put("access_token_status", "SUCCESS");
                result.put("message", "微信配置正常，可以获取access_token");
            } else {
                result.put("access_token_status", "FAILED");
                result.put("message", "无法获取access_token，请检查AppId和AppSecret是否正确");
            }
            
            return CommonResult.success(result);
        } catch (Exception e) {
            LOGGER.error("测试微信配置失败", e);
            result.put("error", e.getMessage());
            return CommonResult.failed("测试微信配置失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "测试小程序码生成")
    @PostMapping("/test-qrcode")
    public CommonResult<Map<String, Object>> testQrCodeGeneration(
            @RequestParam(defaultValue = "pages/new_index/index") String page,
            @RequestParam(defaultValue = "test_123") String scene) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            LOGGER.info("测试生成小程序码 - 页面: {}, 场景: {}", page, scene);
            
            byte[] qrCodeBytes = wxMiniProgramService.generateMiniProgramCodeBytes(page, scene);
            
            result.put("success", true);
            result.put("qr_code_size", qrCodeBytes.length);
            result.put("message", "小程序码生成成功");
            
            return CommonResult.success(result);
        } catch (Exception e) {
            LOGGER.error("测试小程序码生成失败", e);
            result.put("success", false);
            result.put("error", e.getMessage());
            return CommonResult.failed("测试小程序码生成失败: " + e.getMessage());
        }
    }
}