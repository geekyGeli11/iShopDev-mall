package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

/**
 * 分销商申请列表展示VO
 */
@Schema(description = "分销商申请列表展示对象")
public class UmsDistributorApplyListVO {
    
    @Schema(description = "申请ID")
    private Long id;
    
    @Schema(description = "申请用户ID")
    private Long userId;
    
    @Schema(description = "用户昵称")
    private String nickname;
    
    @Schema(description = "真实姓名")
    private String realName;
    
    @Schema(description = "联系电话")
    private String phone;
    
    @Schema(description = "微信号")
    private String wechat;
    
    @Schema(description = "申请状态：0-待审核，1-已通过，2-已拒绝")
    private Byte status;
    
    @Schema(description = "申请状态文本")
    private String statusText;
    
    @Schema(description = "申请时间")
    private Date applyTime;
    
    @Schema(description = "审核时间")
    private Date reviewTime;
    
    @Schema(description = "审核人ID")
    private Long reviewerId;
    
    @Schema(description = "审核人姓名")
    private String reviewerName;
    
    @Schema(description = "申请理由")
    private String applyReason;

    @Schema(description = "审核意见")
    private String reviewComment;

    @Schema(description = "自我介绍")
    private String selfIntroduction;

    @Schema(description = "私域影响力截图")
    private String influenceScreenshots;

    @Schema(description = "身份证正面图片URL")
    private String idCardFrontImg;

    @Schema(description = "身份证反面图片URL")
    private String idCardBackImg;

    @Schema(description = "其他身份证明")
    private String otherCertificates;

    @Schema(description = "工作截图")
    private String workScreenshots;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
        // 设置状态文本
        switch (status) {
            case 0:
                this.statusText = "待审核";
                break;
            case 1:
                this.statusText = "已通过";
                break;
            case 2:
                this.statusText = "已拒绝";
                break;
            default:
                this.statusText = "未知状态";
        }
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public Date getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(Date reviewTime) {
        this.reviewTime = reviewTime;
    }

    public Long getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public String getApplyReason() {
        return applyReason;
    }

    public void setApplyReason(String applyReason) {
        this.applyReason = applyReason;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
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

    public String getIdCardFrontImg() {
        return idCardFrontImg;
    }

    public void setIdCardFrontImg(String idCardFrontImg) {
        this.idCardFrontImg = idCardFrontImg;
    }

    public String getIdCardBackImg() {
        return idCardBackImg;
    }

    public void setIdCardBackImg(String idCardBackImg) {
        this.idCardBackImg = idCardBackImg;
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