package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 优惠券分享结果
 * Created by macro on 2024/12/30.
 */
@Schema(description = "优惠券分享结果")
public class SmsCouponShareResult {
    
    @Schema(description = "优惠券ID")
    private Long couponId;
    
    @Schema(description = "优惠券名称")
    private String couponName;
    
    @Schema(description = "分享链接")
    private String shareLink;
    
    @Schema(description = "小程序码图片URL")
    private String miniProgramCodeUrl;

    @Schema(description = "小程序码base64图片数据")
    private String miniProgramCodeBase64;
    
    @Schema(description = "分享标题")
    private String shareTitle;
    
    @Schema(description = "分享描述")
    private String shareDescription;
    
    @Schema(description = "分享图片URL")
    private String shareImageUrl;

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public String getShareLink() {
        return shareLink;
    }

    public void setShareLink(String shareLink) {
        this.shareLink = shareLink;
    }

    public String getMiniProgramCodeUrl() {
        return miniProgramCodeUrl;
    }

    public void setMiniProgramCodeUrl(String miniProgramCodeUrl) {
        this.miniProgramCodeUrl = miniProgramCodeUrl;
    }

    public String getShareTitle() {
        return shareTitle;
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public String getShareDescription() {
        return shareDescription;
    }

    public void setShareDescription(String shareDescription) {
        this.shareDescription = shareDescription;
    }

    public String getShareImageUrl() {
        return shareImageUrl;
    }

    public void setShareImageUrl(String shareImageUrl) {
        this.shareImageUrl = shareImageUrl;
    }

    public String getMiniProgramCodeBase64() {
        return miniProgramCodeBase64;
    }

    public void setMiniProgramCodeBase64(String miniProgramCodeBase64) {
        this.miniProgramCodeBase64 = miniProgramCodeBase64;
    }
}
