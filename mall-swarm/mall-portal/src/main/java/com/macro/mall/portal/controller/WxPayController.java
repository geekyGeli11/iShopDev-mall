package com.macro.mall.portal.controller;

import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayOrderQueryRequest;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.config.WxPayProperties;
import com.macro.mall.portal.dto.PayOrderRequest;
import com.macro.mall.portal.service.OmsPortalOrderService;
import com.macro.mall.portal.service.WxPayBusiness;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import org.springframework.web.bind.annotation.*;

/**
 * @description 微信支付Controller
 */
@Controller
@Tag(name = "WxPayController", description = "微信支付相关接口")
@RequestMapping("/wxpay")
public class WxPayController {
    private static final Logger logger = LoggerFactory.getLogger(WxPayController.class);
    @Autowired
    private WxPayBusiness wxPayBusiness;

    @Autowired
    private WxPayProperties wxPayProperties;
    
    @Autowired
    private OmsPortalOrderService portalOrderService;

    @Operation(summary = "微信小程序支付")
    @RequestMapping(value = "/pay", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<WxPayMpOrderResult> createPayOrder(@RequestBody PayOrderRequest request) throws Exception {
        // 参数验证
        if (request.getOrderSn() == null || request.getOrderSn().trim().isEmpty()) {
            return CommonResult.failed("订单号不能为空");
        }
        if (request.getAmount() == null || request.getAmount() <= 0) {
            return CommonResult.failed("支付金额必须大于0");
        }
        if (request.getOpenId() == null || request.getOpenId().trim().isEmpty()) {
            return CommonResult.failed("用户OpenId不能为空");
        }
        
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        orderRequest.setBody("商品购买");
        orderRequest.setOutTradeNo(request.getOrderSn());
        orderRequest.setTotalFee(request.getAmount()); // 金额单位：分
        // 设置客户端IP，如果前端没有传递则使用默认值
        String clientIp = request.getSpbillCreateIp();
        if (clientIp == null || clientIp.trim().isEmpty()) {
            clientIp = "127.0.0.1"; // 默认IP
        }
        orderRequest.setSpbillCreateIp(clientIp);
        orderRequest.setOpenid(request.getOpenId());
        orderRequest.setNotifyUrl(wxPayProperties.getNotifyUrl());
        orderRequest.setTradeType("JSAPI");
        
        try {
            // 调用微信支付服务
            WxPayMpOrderResult result = wxPayBusiness.createOrder(orderRequest);

            // 发送延迟消息，用于主动查询支付状态
            portalOrderService.sendDelayMessageCheckPayStatus(request.getOrderSn());
            
            logger.info("微信支付订单创建成功，订单号：{}", request.getOrderSn());
            return CommonResult.success(result);
        } catch (WxPayException e) {
            logger.error("微信支付订单创建失败，订单号：{}，错误：{}", request.getOrderSn(), e.getMessage(), e);
            return CommonResult.failed("支付订单创建失败：" + e.getCustomErrorMsg());
        } catch (Exception e) {
            logger.error("微信支付订单创建异常，订单号：{}，错误：{}", request.getOrderSn(), e.getMessage(), e);
            return CommonResult.failed("支付订单创建异常");
        }
    }

    @Operation(summary = "微信支付异步回调", description = "接收微信支付的异步通知，处理支付结果")
    @RequestMapping(value = "/notify", method = RequestMethod.POST)
    @ResponseBody
    public String notify(@RequestBody String xmlData) {
        try {
            // 调用业务逻辑处理微信支付通知
            return wxPayBusiness.parseOrderNotifyResult(xmlData);
        } catch (Exception e) {
            // 记录错误日志并返回失败响应
            logger.error("处理微信支付异步回调失败: {}", e.getMessage(), e);
            return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[处理失败]]></return_msg></xml>";
        }
    }

    @Operation(summary = "微信支付查询")
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<WxPayOrderQueryResult> query(WxPayOrderQueryRequest wxPayOrderQueryRequest) throws WxPayException {
        return CommonResult.success(wxPayBusiness.queryOrder(wxPayOrderQueryRequest));
    }
    
    @Operation(summary = "主动查询支付状态并更新订单", description = "用于客户端主动查询订单支付状态并更新")
    @RequestMapping(value = "/queryAndUpdateOrder", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<String> queryAndUpdateOrder(@RequestParam String orderSn) {
        try {
            logger.info("主动查询订单支付状态：{}", orderSn);
            // 通过商户订单号查询
            WxPayOrderQueryResult queryResult = wxPayBusiness.queryOrder(null, orderSn);
            
            // 判断支付状态
            if ("SUCCESS".equals(queryResult.getResultCode()) && "SUCCESS".equals(queryResult.getTradeState())) {
                // 支付成功，更新订单状态
                logger.info("订单{}支付成功，准备更新订单状态", orderSn);
                portalOrderService.paySuccessByOrderSn(orderSn, 2); // 2表示微信支付
                return CommonResult.success("支付成功");
            } else if ("USERPAYING".equals(queryResult.getTradeState())) {
                // 用户支付中
                return CommonResult.success("支付处理中");
            } else {
                // 其他状态
                logger.info("订单{}支付状态：{}", orderSn, queryResult.getTradeState());
                return CommonResult.success("支付未完成：" + queryResult.getTradeState());
            }
        } catch (Exception e) {
            logger.error("查询订单支付状态失败：{}", e.getMessage(), e);
            return CommonResult.failed("查询支付状态失败");
        }
    }
}
