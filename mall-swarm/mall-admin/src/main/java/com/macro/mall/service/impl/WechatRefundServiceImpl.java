package com.macro.mall.service.impl;

import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.macro.mall.dto.WechatRefundParam;
import com.macro.mall.dto.WechatRefundResult;
import com.macro.mall.service.WechatRefundService;
import com.macro.mall.config.WxPayConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 微信支付退款服务实现
 * Created by macro on 2024/01/01.
 */
@Service
public class WechatRefundServiceImpl implements WechatRefundService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WechatRefundServiceImpl.class);
    
    @Autowired
    private WxPayConfig wxPayConfig;

    @Autowired
    private WxPayService wxPayService;
    
    // 保留原有的@Value注入方式作为备选
    @Value("${wx.pay.appId:}")
    private String appId;
    
    @Value("${wx.pay.mchId:}")
    private String mchId;
    
    @Value("${wx.pay.mchKey:}")
    private String mchKey;
    
    @Value("${wx.pay.keyPath:}")
    private String keyPath;
    
    @Value("${wx.pay.notifyUrl:}")
    private String notifyUrl;

    @Override
    public WechatRefundResult processRefund(WechatRefundParam refundParam) {
        WechatRefundResult result = new WechatRefundResult();
        
        try {
            LOGGER.info("开始处理微信退款，商户订单号：{}，退款金额：{}", 
                refundParam.getOutTradeNo(), refundParam.getRefundFee());
            
            // 构建微信退款请求
            WxPayRefundRequest request = new WxPayRefundRequest();
            request.setOutTradeNo(refundParam.getOutTradeNo());
            request.setOutRefundNo(refundParam.getOutRefundNo());
            request.setRefundFee(refundParam.getRefundFee());
            request.setTotalFee(refundParam.getTotalFee());
            request.setRefundDesc(refundParam.getRefundDesc());
            // 设置退款回调地址
            String refundNotifyUrl = wxPayConfig.getNotifyUrl();
            request.setNotifyUrl(refundNotifyUrl);

            LOGGER.info("微信退款回调地址：{}", refundNotifyUrl);

            LOGGER.info("微信退款请求参数 - 商户订单号：{}，退款单号：{}，退款金额：{}分，订单总金额：{}分",
                request.getOutTradeNo(), request.getOutRefundNo(),
                request.getRefundFee(), request.getTotalFee());

            // 调用微信支付退款接口
            WxPayRefundResult wxResult = wxPayService.refund(request);

            LOGGER.info("微信退款响应 - 返回码：{}，结果码：{}，退款单号：{}",
                wxResult.getReturnCode(), wxResult.getResultCode(), wxResult.getRefundId());

            if ("SUCCESS".equals(wxResult.getReturnCode()) && "SUCCESS".equals(wxResult.getResultCode())) {
                result.setSuccess(true);
                result.setRefundId(wxResult.getRefundId());
                result.setOutRefundNo(wxResult.getOutRefundNo());
                result.setRefundFee(wxResult.getRefundFee());
                result.setRefundStatus("PROCESSING"); // 微信退款提交成功，状态为处理中
                result.setRefundTime(new Date());
                result.setRefundChannel("ORIGINAL");

                LOGGER.info("微信退款提交成功，微信退款单号：{}，状态：处理中",
                    wxResult.getRefundId());
            } else {
                result.setSuccess(false);
                String errorMsg = wxResult.getReturnMsg() != null ? wxResult.getReturnMsg() : wxResult.getErrCodeDes();
                result.setErrorMsg(errorMsg);

                LOGGER.error("微信退款失败，错误信息：{}，错误代码：{}",
                    errorMsg, wxResult.getErrCode());
            }
            

        } catch (WxPayException e) {
            LOGGER.error("微信退款异常，错误代码：{}，错误信息：{}", e.getErrCode(), e.getErrCodeDes(), e);
            result.setSuccess(false);
            result.setErrorMsg("微信退款失败：" + e.getErrCodeDes());
        } catch (Exception e) {
            LOGGER.error("微信退款处理异常", e);
            result.setSuccess(false);
            result.setErrorMsg("退款处理异常：" + e.getMessage());
        }
        
        return result;
    }

    @Override
    public WechatRefundResult queryRefund(String refundSn) {
        WechatRefundResult result = new WechatRefundResult();

        try {
            LOGGER.info("查询微信退款状态，退款单号：{}", refundSn);

            // 构建微信退款查询请求
            com.github.binarywang.wxpay.bean.request.WxPayRefundQueryRequest request =
                new com.github.binarywang.wxpay.bean.request.WxPayRefundQueryRequest();
            request.setOutRefundNo(refundSn);

            // 调用微信支付退款查询接口
            com.github.binarywang.wxpay.bean.result.WxPayRefundQueryResult wxResult =
                wxPayService.refundQuery(request);

            LOGGER.info("微信退款查询响应 - 返回码：{}，结果码：{}",
                wxResult.getReturnCode(), wxResult.getResultCode());

            if ("SUCCESS".equals(wxResult.getReturnCode()) && "SUCCESS".equals(wxResult.getResultCode())) {
                result.setSuccess(true);
                result.setRefundId("WX_REFUND_" + System.currentTimeMillis());
                result.setOutRefundNo(refundSn);
                result.setRefundStatus("SUCCESS"); // 查询成功说明退款已完成
                result.setRefundTime(new Date());

                LOGGER.info("微信退款查询成功，退款状态：SUCCESS");
            } else {
                result.setSuccess(false);
                String errorMsg = wxResult.getReturnMsg() != null ? wxResult.getReturnMsg() : wxResult.getErrCodeDes();
                result.setErrorMsg(errorMsg);

                LOGGER.error("微信退款查询失败，错误信息：{}", errorMsg);
            }

        } catch (WxPayException e) {
            LOGGER.error("微信退款查询异常，错误代码：{}，错误信息：{}", e.getErrCode(), e.getErrCodeDes(), e);
            result.setSuccess(false);
            result.setErrorMsg("退款查询失败：" + e.getErrCodeDes());
        } catch (Exception e) {
            LOGGER.error("微信退款状态查询异常", e);
            result.setSuccess(false);
            result.setErrorMsg("查询异常：" + e.getMessage());
        }

        return result;
    }
} 