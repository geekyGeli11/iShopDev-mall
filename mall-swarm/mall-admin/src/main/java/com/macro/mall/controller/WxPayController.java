package com.macro.mall.controller;

import com.macro.mall.service.WechatRefundCallbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 微信支付回调控制器
 * Created by macro on 2024/01/01.
 */
@RestController
@RequestMapping("/wxpay")
@Tag(name = "WxPayController", description = "微信支付回调管理")
public class WxPayController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WxPayController.class);

    @Autowired
    private WechatRefundCallbackService wechatRefundCallbackService;

    @Operation(summary = "微信退款回调", description = "接收微信支付的退款异步通知")
    @RequestMapping(value = "/refund/notify", method = RequestMethod.POST)
    @ResponseBody
    public String refundNotify(@RequestBody String xmlData) {
        try {
            LOGGER.info("收到微信退款回调通知");
            // 调用业务逻辑处理微信退款通知
            return wechatRefundCallbackService.handleRefundNotify(xmlData);
        } catch (Exception e) {
            // 记录错误日志并返回失败响应
            LOGGER.error("处理微信退款异步回调失败: {}", e.getMessage(), e);
            return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[处理失败]]></return_msg></xml>";
        }
    }
}
