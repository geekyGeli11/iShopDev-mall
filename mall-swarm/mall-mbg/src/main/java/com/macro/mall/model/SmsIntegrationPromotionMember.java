package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

public class SmsIntegrationPromotionMember implements Serializable {
    @Schema(title = "主键ID")
    private Long id;

    @Schema(title = "营销活动ID")
    private Long promotionId;

    @Schema(title = "用户ID")
    private Long memberId;

    @Schema(title = "订单ID")
    private Long orderId;

    @Schema(title = "订单编号")
    private String orderSn;

    @Schema(title = "原始积分奖励")
    private Integer originalIntegration;

    @Schema(title = "额外获得的积分奖励")
    private Integer bonusIntegration;

    @Schema(title = "总积分奖励")
    private Integer totalIntegration;

    @Schema(title = "创建时间")
    private Date createTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public Integer getOriginalIntegration() {
        return originalIntegration;
    }

    public void setOriginalIntegration(Integer originalIntegration) {
        this.originalIntegration = originalIntegration;
    }

    public Integer getBonusIntegration() {
        return bonusIntegration;
    }

    public void setBonusIntegration(Integer bonusIntegration) {
        this.bonusIntegration = bonusIntegration;
    }

    public Integer getTotalIntegration() {
        return totalIntegration;
    }

    public void setTotalIntegration(Integer totalIntegration) {
        this.totalIntegration = totalIntegration;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", promotionId=").append(promotionId);
        sb.append(", memberId=").append(memberId);
        sb.append(", orderId=").append(orderId);
        sb.append(", orderSn=").append(orderSn);
        sb.append(", originalIntegration=").append(originalIntegration);
        sb.append(", bonusIntegration=").append(bonusIntegration);
        sb.append(", totalIntegration=").append(totalIntegration);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}