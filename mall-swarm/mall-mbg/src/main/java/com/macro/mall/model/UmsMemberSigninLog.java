package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

public class UmsMemberSigninLog implements Serializable {
    private Long id;

    @Schema(title = "用户ID")
    private Long memberId;

    @Schema(title = "用户名")
    private String memberUsername;

    @Schema(title = "签到日期")
    private Date signinDate;

    @Schema(title = "签到时间")
    private Date signinTime;

    @Schema(title = "获得积分")
    private Integer pointsEarned;

    @Schema(title = "连续签到天数")
    private Integer continuousDays;

    @Schema(title = "当前周期签到天数")
    private Integer cycleSigninDays;

    @Schema(title = "是否已领取连签奖励")
    private Byte isRewardClaimed;

    @Schema(title = "连签奖励优惠券ID")
    private Long rewardCouponId;

    @Schema(title = "签到月份(YYYY-MM)")
    private String signinMonth;

    @Schema(title = "客户端IP")
    private String clientIp;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberUsername() {
        return memberUsername;
    }

    public void setMemberUsername(String memberUsername) {
        this.memberUsername = memberUsername;
    }

    public Date getSigninDate() {
        return signinDate;
    }

    public void setSigninDate(Date signinDate) {
        this.signinDate = signinDate;
    }

    public Date getSigninTime() {
        return signinTime;
    }

    public void setSigninTime(Date signinTime) {
        this.signinTime = signinTime;
    }

    public Integer getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(Integer pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public Integer getContinuousDays() {
        return continuousDays;
    }

    public void setContinuousDays(Integer continuousDays) {
        this.continuousDays = continuousDays;
    }

    public Integer getCycleSigninDays() {
        return cycleSigninDays;
    }

    public void setCycleSigninDays(Integer cycleSigninDays) {
        this.cycleSigninDays = cycleSigninDays;
    }

    public Byte getIsRewardClaimed() {
        return isRewardClaimed;
    }

    public void setIsRewardClaimed(Byte isRewardClaimed) {
        this.isRewardClaimed = isRewardClaimed;
    }

    public Long getRewardCouponId() {
        return rewardCouponId;
    }

    public void setRewardCouponId(Long rewardCouponId) {
        this.rewardCouponId = rewardCouponId;
    }

    public String getSigninMonth() {
        return signinMonth;
    }

    public void setSigninMonth(String signinMonth) {
        this.signinMonth = signinMonth;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", memberId=").append(memberId);
        sb.append(", memberUsername=").append(memberUsername);
        sb.append(", signinDate=").append(signinDate);
        sb.append(", signinTime=").append(signinTime);
        sb.append(", pointsEarned=").append(pointsEarned);
        sb.append(", continuousDays=").append(continuousDays);
        sb.append(", cycleSigninDays=").append(cycleSigninDays);
        sb.append(", isRewardClaimed=").append(isRewardClaimed);
        sb.append(", rewardCouponId=").append(rewardCouponId);
        sb.append(", signinMonth=").append(signinMonth);
        sb.append(", clientIp=").append(clientIp);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}