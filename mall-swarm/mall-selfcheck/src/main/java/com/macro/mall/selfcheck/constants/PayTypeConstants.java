package com.macro.mall.selfcheck.constants;

/**
 * 支付方式常量
 * 全局统一映射：0->未支付；1->支付宝；2->微信
 * 
 * @author macro
 * @since 1.0.0
 */
public class PayTypeConstants {
    
    /**
     * 未支付/未知支付方式
     */
    public static final Integer PAY_TYPE_NONE = 0;
    
    /**
     * 支付宝支付
     */
    public static final Integer PAY_TYPE_ALIPAY = 1;
    
    /**
     * 微信支付
     */
    public static final Integer PAY_TYPE_WECHAT = 2;
    
    /**
     * 余额支付
     */
    public static final Integer PAY_TYPE_BALANCE = 3;
    
    /**
     * 积分支付
     */
    public static final Integer PAY_TYPE_POINTS = 4;
    
    // 支付方式字符串常量
    public static final String PAY_TYPE_STR_ALIPAY = "ALIPAY";
    public static final String PAY_TYPE_STR_WECHAT = "WECHAT";
    public static final String PAY_TYPE_STR_BALANCE = "BALANCE";
    public static final String PAY_TYPE_STR_POINTS = "POINTS";
    
    /**
     * 根据支付方式字符串获取支付方式代码
     * 
     * @param payTypeStr 支付方式字符串
     * @return 支付方式代码
     */
    public static Integer getPayTypeCode(String payTypeStr) {
        if (PAY_TYPE_STR_WECHAT.equals(payTypeStr)) {
            return PAY_TYPE_WECHAT;
        } else if (PAY_TYPE_STR_ALIPAY.equals(payTypeStr)) {
            return PAY_TYPE_ALIPAY;
        } else if (PAY_TYPE_STR_BALANCE.equals(payTypeStr)) {
            return PAY_TYPE_BALANCE;
        } else if (PAY_TYPE_STR_POINTS.equals(payTypeStr)) {
            return PAY_TYPE_POINTS;
        } else {
            return PAY_TYPE_NONE;
        }
    }
    
    /**
     * 根据支付方式代码获取支付方式名称
     * 
     * @param payTypeCode 支付方式代码
     * @return 支付方式名称
     */
    public static String getPayTypeName(Integer payTypeCode) {
        if (PAY_TYPE_WECHAT.equals(payTypeCode)) {
            return "微信支付";
        } else if (PAY_TYPE_ALIPAY.equals(payTypeCode)) {
            return "支付宝";
        } else if (PAY_TYPE_BALANCE.equals(payTypeCode)) {
            return "余额支付";
        } else if (PAY_TYPE_POINTS.equals(payTypeCode)) {
            return "积分支付";
        } else {
            return "未支付";
        }
    }
    
    /**
     * 根据支付方式代码获取支付方式字符串
     * 
     * @param payTypeCode 支付方式代码
     * @return 支付方式字符串
     */
    public static String getPayTypeString(Integer payTypeCode) {
        if (PAY_TYPE_WECHAT.equals(payTypeCode)) {
            return PAY_TYPE_STR_WECHAT;
        } else if (PAY_TYPE_ALIPAY.equals(payTypeCode)) {
            return PAY_TYPE_STR_ALIPAY;
        } else if (PAY_TYPE_BALANCE.equals(payTypeCode)) {
            return PAY_TYPE_STR_BALANCE;
        } else if (PAY_TYPE_POINTS.equals(payTypeCode)) {
            return PAY_TYPE_STR_POINTS;
        } else {
            return "NONE";
        }
    }
    
    /**
     * 检查是否为有效的支付方式
     * 
     * @param payTypeCode 支付方式代码
     * @return 是否有效
     */
    public static boolean isValidPayType(Integer payTypeCode) {
        return payTypeCode != null && 
               (payTypeCode.equals(PAY_TYPE_ALIPAY) || 
                payTypeCode.equals(PAY_TYPE_WECHAT) || 
                payTypeCode.equals(PAY_TYPE_BALANCE) || 
                payTypeCode.equals(PAY_TYPE_POINTS));
    }
    
    /**
     * 检查是否为第三方支付（微信、支付宝）
     * 
     * @param payTypeCode 支付方式代码
     * @return 是否为第三方支付
     */
    public static boolean isThirdPartyPay(Integer payTypeCode) {
        return PAY_TYPE_WECHAT.equals(payTypeCode) || PAY_TYPE_ALIPAY.equals(payTypeCode);
    }
} 