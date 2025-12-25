package com.macro.mall.controller;

import com.macro.mall.service.WechatCallbackService;
import com.macro.mall.service.WechatServiceAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 微信服务号回调接口
 * 用于接收微信服务器的消息推送
 */
@RestController
@RequestMapping("/wechat/callback")
@Tag(name = "WechatCallbackController", description = "微信服务号回调接口")
public class WechatCallbackController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WechatCallbackController.class);
    
    @Autowired
    private WechatServiceAccountService wechatServiceAccountService;
    
    @Autowired
    private WechatCallbackService wechatCallbackService;
    
    /**
     * 微信服务器验证接口
     * 用于验证服务器配置时的签名校验
     */
    @Operation(summary = "微信服务器验证")
    @GetMapping(produces = "text/plain;charset=UTF-8")
    public String verify(@RequestParam("signature") String signature,
                         @RequestParam("timestamp") String timestamp,
                         @RequestParam("nonce") String nonce,
                         @RequestParam("echostr") String echostr) {
        
        LOGGER.info("收到微信服务器验证请求: signature={}, timestamp={}, nonce={}", signature, timestamp, nonce);
        
        // 验证签名
        if (wechatServiceAccountService.verifySignature(signature, timestamp, nonce)) {
            LOGGER.info("微信服务器验证成功");
            return echostr;
        }
        
        LOGGER.warn("微信服务器验证失败");
        return "fail";
    }
    
    /**
     * 接收微信消息推送
     * 处理用户关注、扫码等事件
     */
    @Operation(summary = "接收微信消息推送")
    @PostMapping(produces = "application/xml;charset=UTF-8", consumes = {"application/xml", "text/xml"})
    public String handleMessage(@RequestParam("signature") String signature,
                                @RequestParam("timestamp") String timestamp,
                                @RequestParam("nonce") String nonce,
                                @RequestBody String xmlMessage) {
        
        LOGGER.info("收到微信消息推送");
        
        // 验证签名
        if (!wechatServiceAccountService.verifySignature(signature, timestamp, nonce)) {
            LOGGER.warn("微信消息签名验证失败");
            return "fail";
        }
        
        // 处理消息
        return wechatCallbackService.handleEvent(xmlMessage);
    }
}
