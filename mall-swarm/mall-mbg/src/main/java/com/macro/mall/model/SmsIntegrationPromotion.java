package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class SmsIntegrationPromotion implements Serializable {
    @Schema(title = "主键ID")
    private Long id;

    @Schema(title = "活动名称")
    private String name;

    @Schema(title = "活动描述")
    private String description;

    @Schema(title = "活动开始时间")
    private Date startTime;

    @Schema(title = "活动结束时间")
    private Date endTime;

    @Schema(title = "积分倍数（如1.5表示1.5倍积分，2.0表示2倍积分）")
    private BigDecimal multiplier;

    @Schema(title = "活动状态：0->禁用；1->启用")
    private Boolean status;

    @Schema(title = "优先级，数值越大优先级越高，同时间段内优先级高的生效")
    private Integer priority;

    @Schema(title = "适用类型：0->全部商品；1->指定分类；2->指定商品")
    private Integer applicableType;

    @Schema(title = "适用对象ID列表，用逗号分隔（当applicable_type不为0时使用）")
    private String applicableIds;

    @Schema(title = "最小订单金额限制（为空表示无限制）")
    private BigDecimal minOrderAmount;

    @Schema(title = "单笔订单最大积分奖励限制（为空表示无限制）")
    private Integer maxIntegrationReward;

    @Schema(title = "活动期间总积分奖励上限（为空表示无限制）")
    private Integer totalLimit;

    @Schema(title = "已使用的积分奖励数量")
    private Integer usedIntegration;

    @Schema(title = "单个用户参与次数限制（为空表示无限制）")
    private Integer memberLimit;

    @Schema(title = "创建时间")
    private Date createTime;

    @Schema(title = "更新时间")
    private Date updateTime;

    @Schema(title = "创建人")
    private String creator;

    @Schema(title = "更新人")
    private String updater;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(BigDecimal multiplier) {
        this.multiplier = multiplier;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getApplicableType() {
        return applicableType;
    }

    public void setApplicableType(Integer applicableType) {
        this.applicableType = applicableType;
    }

    public String getApplicableIds() {
        return applicableIds;
    }

    public void setApplicableIds(String applicableIds) {
        this.applicableIds = applicableIds;
    }

    public BigDecimal getMinOrderAmount() {
        return minOrderAmount;
    }

    public void setMinOrderAmount(BigDecimal minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }

    public Integer getMaxIntegrationReward() {
        return maxIntegrationReward;
    }

    public void setMaxIntegrationReward(Integer maxIntegrationReward) {
        this.maxIntegrationReward = maxIntegrationReward;
    }

    public Integer getTotalLimit() {
        return totalLimit;
    }

    public void setTotalLimit(Integer totalLimit) {
        this.totalLimit = totalLimit;
    }

    public Integer getUsedIntegration() {
        return usedIntegration;
    }

    public void setUsedIntegration(Integer usedIntegration) {
        this.usedIntegration = usedIntegration;
    }

    public Integer getMemberLimit() {
        return memberLimit;
    }

    public void setMemberLimit(Integer memberLimit) {
        this.memberLimit = memberLimit;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", description=").append(description);
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", multiplier=").append(multiplier);
        sb.append(", status=").append(status);
        sb.append(", priority=").append(priority);
        sb.append(", applicableType=").append(applicableType);
        sb.append(", applicableIds=").append(applicableIds);
        sb.append(", minOrderAmount=").append(minOrderAmount);
        sb.append(", maxIntegrationReward=").append(maxIntegrationReward);
        sb.append(", totalLimit=").append(totalLimit);
        sb.append(", usedIntegration=").append(usedIntegration);
        sb.append(", memberLimit=").append(memberLimit);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", creator=").append(creator);
        sb.append(", updater=").append(updater);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}