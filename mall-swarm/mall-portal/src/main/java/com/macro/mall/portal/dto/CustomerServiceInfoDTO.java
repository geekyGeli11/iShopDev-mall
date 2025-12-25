package com.macro.mall.portal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 客服信息DTO
 * Created by macro on 2025/11/28.
 */
@Schema(description = "客服信息")
public class CustomerServiceInfoDTO {
    
    @Schema(description = "微信客服二维码URL")
    private String wechatQrcodeUrl;
    
    @Schema(description = "微信号")
    private String wechatAccount;
    
    @Schema(description = "企业微信二维码URL")
    private String enterpriseWechatQrcodeUrl;
    
    @Schema(description = "客服电话")
    private String servicePhone;
    
    @Schema(description = "服务时间")
    private String serviceHours;
    
    @Schema(description = "服务描述")
    private String serviceDescription;
    
    @Schema(description = "是否启用")
    private Integer isEnabled;
    
    public CustomerServiceInfoDTO() {
    }
    
    public CustomerServiceInfoDTO(String wechatQrcodeUrl, String wechatAccount, 
                                   String enterpriseWechatQrcodeUrl, String servicePhone,
                                   String serviceHours, String serviceDescription, Integer isEnabled) {
        this.wechatQrcodeUrl = wechatQrcodeUrl;
        this.wechatAccount = wechatAccount;
        this.enterpriseWechatQrcodeUrl = enterpriseWechatQrcodeUrl;
        this.servicePhone = servicePhone;
        this.serviceHours = serviceHours;
        this.serviceDescription = serviceDescription;
        this.isEnabled = isEnabled;
    }
    
    public String getWechatQrcodeUrl() {
        return wechatQrcodeUrl;
    }
    
    public void setWechatQrcodeUrl(String wechatQrcodeUrl) {
        this.wechatQrcodeUrl = wechatQrcodeUrl;
    }
    
    public String getWechatAccount() {
        return wechatAccount;
    }
    
    public void setWechatAccount(String wechatAccount) {
        this.wechatAccount = wechatAccount;
    }
    
    public String getEnterpriseWechatQrcodeUrl() {
        return enterpriseWechatQrcodeUrl;
    }
    
    public void setEnterpriseWechatQrcodeUrl(String enterpriseWechatQrcodeUrl) {
        this.enterpriseWechatQrcodeUrl = enterpriseWechatQrcodeUrl;
    }
    
    public String getServicePhone() {
        return servicePhone;
    }
    
    public void setServicePhone(String servicePhone) {
        this.servicePhone = servicePhone;
    }
    
    public String getServiceHours() {
        return serviceHours;
    }
    
    public void setServiceHours(String serviceHours) {
        this.serviceHours = serviceHours;
    }
    
    public String getServiceDescription() {
        return serviceDescription;
    }
    
    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }
    
    public Integer getIsEnabled() {
        return isEnabled;
    }
    
    public void setIsEnabled(Integer isEnabled) {
        this.isEnabled = isEnabled;
    }
}
