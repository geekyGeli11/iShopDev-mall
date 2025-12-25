package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

/**
 * 非系统销售单分享结果
 * Created by macro on 2025-12-12.
 */
@Schema(title = "非系统销售单分享结果")
public class PmsNonSystemSaleShareResult implements Serializable {
    
    @Schema(title = "销售单ID")
    private Long saleId;

    @Schema(title = "销售单号")
    private String saleNo;

    @Schema(title = "分享标题")
    private String shareTitle;

    @Schema(title = "分享描述")
    private String shareDescription;

    @Schema(title = "分享链接（小程序短链接）")
    private String shareLink;

    @Schema(title = "小程序码URL")
    private String miniProgramCodeUrl;

    @Schema(title = "小程序码Base64")
    private String miniProgramCodeBase64;

    @Schema(title = "分享图片URL")
    private String shareImageUrl;

    private static final long serialVersionUID = 1L;

    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
    }

    public String getSaleNo() {
        return saleNo;
    }

    public void setSaleNo(String saleNo) {
        this.saleNo = saleNo;
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

    public String getMiniProgramCodeBase64() {
        return miniProgramCodeBase64;
    }

    public void setMiniProgramCodeBase64(String miniProgramCodeBase64) {
        this.miniProgramCodeBase64 = miniProgramCodeBase64;
    }

    public String getShareImageUrl() {
        return shareImageUrl;
    }

    public void setShareImageUrl(String shareImageUrl) {
        this.shareImageUrl = shareImageUrl;
    }

    @Override
    public String toString() {
        return "PmsNonSystemSaleShareResult{" +
                "saleId=" + saleId +
                ", saleNo='" + saleNo + '\'' +
                ", shareTitle='" + shareTitle + '\'' +
                ", shareDescription='" + shareDescription + '\'' +
                ", shareLink='" + shareLink + '\'' +
                ", miniProgramCodeUrl='" + miniProgramCodeUrl + '\'' +
                ", shareImageUrl='" + shareImageUrl + '\'' +
                '}';
    }
}
