package com.macro.mall.portal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

/**
 * 分销商申请参数 - 根据前端页面设计稿定制
 */
@Schema(description = "分销商申请参数")
public class DistributorApplyParam {

    @Schema(description = "手机号", required = true)
    @NotBlank(message = "手机号不能为空")
    private String phone;

    @Schema(description = "自我介绍", required = true)
    @NotBlank(message = "个人介绍不能为空")
    private String selfIntroduction;

    @Schema(description = "私域影响力截图（JSON格式）")
    private String influenceScreenshots;

    @Schema(description = "身份证正面图片URL", required = true)
    @NotBlank(message = "身份证正面照片不能为空")
    private String idCardFront;

    @Schema(description = "身份证反面图片URL", required = true)
    @NotBlank(message = "身份证反面照片不能为空")
    private String idCardBack;

    @Schema(description = "其他身份证明（JSON格式）")
    private String otherCertificates;

    @Schema(description = "工作截图（JSON格式）")
    private String workScreenshots;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSelfIntroduction() {
        return selfIntroduction;
    }

    public void setSelfIntroduction(String selfIntroduction) {
        this.selfIntroduction = selfIntroduction;
    }

    public String getInfluenceScreenshots() {
        return influenceScreenshots;
    }

    public void setInfluenceScreenshots(String influenceScreenshots) {
        this.influenceScreenshots = influenceScreenshots;
    }

    public String getIdCardFront() {
        return idCardFront;
    }

    public void setIdCardFront(String idCardFront) {
        this.idCardFront = idCardFront;
    }

    public String getIdCardBack() {
        return idCardBack;
    }

    public void setIdCardBack(String idCardBack) {
        this.idCardBack = idCardBack;
    }

    public String getOtherCertificates() {
        return otherCertificates;
    }

    public void setOtherCertificates(String otherCertificates) {
        this.otherCertificates = otherCertificates;
    }

    public String getWorkScreenshots() {
        return workScreenshots;
    }

    public void setWorkScreenshots(String workScreenshots) {
        this.workScreenshots = workScreenshots;
    }
}