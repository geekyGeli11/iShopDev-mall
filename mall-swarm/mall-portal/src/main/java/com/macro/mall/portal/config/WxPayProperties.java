package com.macro.mall.portal.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * wxpay pay properties.
 *
 * @author Binary Wang
 */
@Getter
@Setter
@Data
@ConfigurationProperties(prefix = "wx.pay")
public class WxPayProperties {
    /**
     * 设置微信公众号或者小程序等的appid
     */
    private String appId;

    /**
     * 微信支付商户号
     */
    private String mchId;

    /**
     * 微信支付商户密钥
     */
    private String mchKey;

    /**
     * 服务商模式下的子商户公众账号ID，普通模式请不要配置，请在配置文件中将对应项删除
     */
    private String subAppId;

    /**
     * 服务商模式下的子商户号，普通模式请不要配置，最好是请在配置文件中将对应项删除
     */
    private String subMchId;

    /**
     * apiclient_cert.p12文件的绝对路径，或者如果放在项目中，请以classpath:开头指定
     */
    private String keyPath;

    /**
     * 商户私钥文件路径（API v3需要）
     */
    private String privateKeyPath;

    /**
     * 商户证书文件路径（API v3需要）
     */
    private String privateCertPath;

    /**
     * 开放状态：0-不开放支付功能，1-开放支付功能
     */
    private String openType;

    /**
     * 微信支付回调通知url
     */
    private String notifyUrl;

    /**
     * 微信支付API v3密钥
     */
    private String apiV3Key;

    /**
     * 商户证书序列号
     */
    private String merchantSerialNumber;

}
