package com.macro.mall.portal.service;

import com.github.binarywang.wxpay.bean.coupon.*;
import com.github.binarywang.wxpay.bean.request.*;
import com.github.binarywang.wxpay.bean.result.*;
import com.github.binarywang.wxpay.exception.WxPayException;

import java.io.File;
import java.util.Date;

/**
 * @author 维狄
 * @date 2021-01-15-015 17:02
 */
public interface WxPayBusiness {
    WxPayOrderQueryResult queryOrder(String transactionId, String outTradeNo) throws WxPayException;
    WxPayOrderQueryResult queryOrder(WxPayOrderQueryRequest wxPayOrderQueryRequest) throws WxPayException;
    WxPayOrderCloseResult closeOrder(String outTradeNo) throws WxPayException;
    WxPayOrderCloseResult closeOrder(WxPayOrderCloseRequest wxPayOrderCloseRequest) throws WxPayException;
    <T> T createOrder(WxPayUnifiedOrderRequest request) throws WxPayException;
    WxPayUnifiedOrderResult unifiedOrder(WxPayUnifiedOrderRequest request) throws WxPayException;
    WxPayRefundResult refund(WxPayRefundRequest request) throws WxPayException;
    WxPayRefundQueryResult refundQuery(String transactionId, String outTradeNo, String outRefundNo, String refundId) throws WxPayException;
    WxPayRefundQueryResult refundQuery(WxPayRefundQueryRequest wxPayRefundQueryRequest) throws WxPayException;
    String parseOrderNotifyResult(String xmlData) throws WxPayException;
    String parseRefundNotifyResult(String xmlData) throws WxPayException;
    String parseScanPayNotifyResult(String xmlData) throws WxPayException;
    WxPaySendRedpackResult sendRedpack(WxPaySendRedpackRequest request) throws WxPayException;
    WxPayRedpackQueryResult queryRedpack(String mchBillNo) throws WxPayException;
    byte[] createScanPayQrcodeMode1(String productId, File logoFile, Integer sideLength);
    String createScanPayQrcodeMode1(String productId);
    byte[] createScanPayQrcodeMode2(String codeUrl, File logoFile, Integer sideLength);
    void report(WxPayReportRequest request) throws WxPayException;
    WxPayBillResult downloadBill(String billDate, String billType, String tarType, String deviceInfo) throws WxPayException;
    WxPayBillResult downloadBill(WxPayDownloadBillRequest wxPayDownloadBillRequest) throws WxPayException;
    WxPayMicropayResult micropay(WxPayMicropayRequest request) throws WxPayException;
    WxPayOrderReverseResult reverseOrder(WxPayOrderReverseRequest request) throws WxPayException;
    String getSandboxSignKey() throws WxPayException;
    WxPayCouponSendResult sendCoupon(WxPayCouponSendRequest request) throws WxPayException;
    WxPayCouponStockQueryResult queryCouponStock(WxPayCouponStockQueryRequest request) throws WxPayException;
    WxPayCouponInfoQueryResult queryCouponInfo(WxPayCouponInfoQueryRequest request) throws WxPayException;
    String queryComment(Date beginDate, Date endDate, Integer offset, Integer limit) throws WxPayException;

    Integer paySuccess(Long orderId, Integer payType, String giftMessage, String giftPic);
}
