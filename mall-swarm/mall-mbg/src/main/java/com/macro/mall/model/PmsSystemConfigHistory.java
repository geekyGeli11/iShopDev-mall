package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

public class PmsSystemConfigHistory implements Serializable {
    private Long id;

    @Schema(title = "配置ID")
    private Long configId;

    @Schema(title = "配置键")
    private String configKey;

    @Schema(title = "配置描述")
    private String configDesc;

    @Schema(title = "操作用户")
    private String changeUser;

    @Schema(title = "变更原因")
    private String changeReason;

    @Schema(title = "变更时间")
    private Date changeTime;

    @Schema(title = "操作类型(CREATE/UPDATE/DELETE)")
    private String operationType;

    @Schema(title = "旧值")
    private String oldValue;

    @Schema(title = "新值")
    private String newValue;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConfigId() {
        return configId;
    }

    public void setConfigId(Long configId) {
        this.configId = configId;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigDesc() {
        return configDesc;
    }

    public void setConfigDesc(String configDesc) {
        this.configDesc = configDesc;
    }

    public String getChangeUser() {
        return changeUser;
    }

    public void setChangeUser(String changeUser) {
        this.changeUser = changeUser;
    }

    public String getChangeReason() {
        return changeReason;
    }

    public void setChangeReason(String changeReason) {
        this.changeReason = changeReason;
    }

    public Date getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(Date changeTime) {
        this.changeTime = changeTime;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", configId=").append(configId);
        sb.append(", configKey=").append(configKey);
        sb.append(", configDesc=").append(configDesc);
        sb.append(", changeUser=").append(changeUser);
        sb.append(", changeReason=").append(changeReason);
        sb.append(", changeTime=").append(changeTime);
        sb.append(", operationType=").append(operationType);
        sb.append(", oldValue=").append(oldValue);
        sb.append(", newValue=").append(newValue);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}