package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

public class CustomerServiceConfig implements Serializable {
    @Schema(title = "配置ID")
    private Long id;

    @Schema(title = "微信客服二维码URL")
    private String wechatQrcodeUrl;

    @Schema(title = "微信号")
    private String wechatAccount;

    @Schema(title = "企业微信二维码URL")
    private String enterpriseWechatQrcodeUrl;

    @Schema(title = "客服电话")
    private String servicePhone;

    @Schema(title = "服务时间")
    private String serviceHours;

    @Schema(title = "是否启用(0:否 1:是)")
    private Boolean isEnabled;

    @Schema(title = "创建时间")
    private Date createdTime;

    @Schema(title = "更新时间")
    private Date updatedTime;

    @Schema(title = "创建人")
    private String createdBy;

    @Schema(title = "更新人")
    private String updatedBy;

    @Schema(title = "服务描述")
    private String serviceDescription;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", wechatQrcodeUrl=").append(wechatQrcodeUrl);
        sb.append(", wechatAccount=").append(wechatAccount);
        sb.append(", enterpriseWechatQrcodeUrl=").append(enterpriseWechatQrcodeUrl);
        sb.append(", servicePhone=").append(servicePhone);
        sb.append(", serviceHours=").append(serviceHours);
        sb.append(", isEnabled=").append(isEnabled);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", updatedTime=").append(updatedTime);
        sb.append(", createdBy=").append(createdBy);
        sb.append(", updatedBy=").append(updatedBy);
        sb.append(", serviceDescription=").append(serviceDescription);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}