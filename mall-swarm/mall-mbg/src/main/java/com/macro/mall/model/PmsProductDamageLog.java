package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

public class PmsProductDamageLog implements Serializable {
    @Schema(title = "涓婚敭ID")
    private Long id;

    @Schema(title = "鎶ユ崯璁板綍ID")
    private Long damageReportId;

    @Schema(title = "鎿嶄綔绫诲瀷锛?-鎻愪氦鎶ユ崯锛?-寮??澶勭悊锛?-鑱旂郴鍘傚?锛?-鍘傚?鍙戣揣锛?-楠屾敹锛?-瀹屾垚澶勭悊锛?-鍏抽棴锛?-澶囨敞")
    private Byte actionType;

    @Schema(title = "鎿嶄綔鍐呭?")
    private String actionContent;

    @Schema(title = "鎿嶄綔鍓嶇姸鎬")
    private Byte beforeStatus;

    @Schema(title = "鎿嶄綔鍚庣姸鎬")
    private Byte afterStatus;

    @Schema(title = "鎿嶄綔浜篒D")
    private Long operatorId;

    @Schema(title = "鎿嶄綔浜哄?鍚")
    private String operatorName;

    @Schema(title = "鎿嶄綔鏃堕棿")
    private Date createTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDamageReportId() {
        return damageReportId;
    }

    public void setDamageReportId(Long damageReportId) {
        this.damageReportId = damageReportId;
    }

    public Byte getActionType() {
        return actionType;
    }

    public void setActionType(Byte actionType) {
        this.actionType = actionType;
    }

    public String getActionContent() {
        return actionContent;
    }

    public void setActionContent(String actionContent) {
        this.actionContent = actionContent;
    }

    public Byte getBeforeStatus() {
        return beforeStatus;
    }

    public void setBeforeStatus(Byte beforeStatus) {
        this.beforeStatus = beforeStatus;
    }

    public Byte getAfterStatus() {
        return afterStatus;
    }

    public void setAfterStatus(Byte afterStatus) {
        this.afterStatus = afterStatus;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
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
        sb.append(", damageReportId=").append(damageReportId);
        sb.append(", actionType=").append(actionType);
        sb.append(", actionContent=").append(actionContent);
        sb.append(", beforeStatus=").append(beforeStatus);
        sb.append(", afterStatus=").append(afterStatus);
        sb.append(", operatorId=").append(operatorId);
        sb.append(", operatorName=").append(operatorName);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}