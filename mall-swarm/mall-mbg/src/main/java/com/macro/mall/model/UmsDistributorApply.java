package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

public class UmsDistributorApply implements Serializable {
    private Long id;

    @Schema(title = "申请用户ID")
    private Long userId;

    @Schema(title = "真实姓名")
    private String realName;

    @Schema(title = "身份证号")
    private String idCard;

    @Schema(title = "联系电话")
    private String phone;

    @Schema(title = "微信号")
    private String wechat;

    @Schema(title = "申请状态：0-待审核，1-已通过，2-已拒绝")
    private Byte status;

    @Schema(title = "申请时间")
    private Date applyTime;

    @Schema(title = "审核时间")
    private Date reviewTime;

    @Schema(title = "审核人ID")
    private Long reviewerId;

    @Schema(title = "身份证正面图片URL")
    private String idCardFrontImg;

    @Schema(title = "身份证反面图片URL")
    private String idCardBackImg;

    private Date createdAt;

    private Date updatedAt;

    @Schema(title = "申请理由")
    private String applyReason;

    @Schema(title = "推广经验描述")
    private String experience;

    @Schema(title = "审核意见")
    private String reviewComment;

    @Schema(title = "自我介绍")
    private String selfIntroduction;

    @Schema(title = "私域影响力截图（JSON格式存储多张图片URL）")
    private String influenceScreenshots;

    @Schema(title = "其他身份证明（JSON格式存储多张图片URL）")
    private String otherCertificates;

    @Schema(title = "工作截图（JSON格式存储多张图片URL）")
    private String workScreenshots;

    private static final long serialVersionUID = 1L;

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

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getApplyReason() {
        return applyReason;
    }

    public void setApplyReason(String applyReason) {
        this.applyReason = applyReason;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", realName=").append(realName);
        sb.append(", idCard=").append(idCard);
        sb.append(", phone=").append(phone);
        sb.append(", wechat=").append(wechat);
        sb.append(", status=").append(status);
        sb.append(", applyTime=").append(applyTime);
        sb.append(", reviewTime=").append(reviewTime);
        sb.append(", reviewerId=").append(reviewerId);
        sb.append(", idCardFrontImg=").append(idCardFrontImg);
        sb.append(", idCardBackImg=").append(idCardBackImg);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", applyReason=").append(applyReason);
        sb.append(", experience=").append(experience);
        sb.append(", reviewComment=").append(reviewComment);
        sb.append(", selfIntroduction=").append(selfIntroduction);
        sb.append(", influenceScreenshots=").append(influenceScreenshots);
        sb.append(", otherCertificates=").append(otherCertificates);
        sb.append(", workScreenshots=").append(workScreenshots);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}