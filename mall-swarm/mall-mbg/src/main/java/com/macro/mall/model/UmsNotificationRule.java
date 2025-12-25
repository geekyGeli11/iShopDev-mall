package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

public class UmsNotificationRule implements Serializable {
    private Long id;

    @Schema(title = "通知类型：ORDER_SHIP-发货通知, ORDER_RETURN-退货通知, STOCK_TRANSFER-调货通知")
    private String notificationType;

    @Schema(title = "微信模板消息ID")
    private String templateId;

    @Schema(title = "规则类型：1-按角色, 2-按指定账号")
    private Byte ruleType;

    @Schema(title = "角色ID列表，逗号分隔（rule_type=1时使用）")
    private String roleIds;

    @Schema(title = "管理员ID列表，逗号分隔（rule_type=2时使用）")
    private String adminIds;

    @Schema(title = "状态：0-禁用, 1-启用")
    private Byte status;

    @Schema(title = "创建时间")
    private Date createTime;

    @Schema(title = "更新时间")
    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public Byte getRuleType() {
        return ruleType;
    }

    public void setRuleType(Byte ruleType) {
        this.ruleType = ruleType;
    }

    public String getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(String roleIds) {
        this.roleIds = roleIds;
    }

    public String getAdminIds() {
        return adminIds;
    }

    public void setAdminIds(String adminIds) {
        this.adminIds = adminIds;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", notificationType=").append(notificationType);
        sb.append(", templateId=").append(templateId);
        sb.append(", ruleType=").append(ruleType);
        sb.append(", roleIds=").append(roleIds);
        sb.append(", adminIds=").append(adminIds);
        sb.append(", status=").append(status);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}