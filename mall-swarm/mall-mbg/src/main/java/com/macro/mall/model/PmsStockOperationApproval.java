package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

public class PmsStockOperationApproval implements Serializable {
    @Schema(title = "主键ID")
    private Long id;

    @Schema(title = "操作单号（唯一标识一个操作）")
    private String operationNo;

    @Schema(title = "操作类型：1=入库，2=出库，3=调整，4=调货")
    private Byte operationType;

    @Schema(title = "操作子类型")
    private Byte operationSubtype;

    @Schema(title = "门店ID（为空表示总库存操作）")
    private Long storeId;

    @Schema(title = "调货源门店ID")
    private Long fromStoreId;

    @Schema(title = "调货目标门店ID")
    private Long toStoreId;

    @Schema(title = "操作包含的SKU数量")
    private Integer totalItems;

    @Schema(title = "审核状态：0=已申请(待发货)，1=已发货(待确认)，2=已驳回，3=已确认收货")
    private Byte approvalStatus;

    @Schema(title = "审核人ID")
    private Long approverId;

    @Schema(title = "审核人名称")
    private String approverName;

    @Schema(title = "审核时间")
    private Date approvalTime;

    @Schema(title = "操作人ID")
    private Long operatorId;

    @Schema(title = "操作人名称")
    private String operatorName;

    @Schema(title = "操作人IP")
    private String operatorIp;

    @Schema(title = "是否已执行库存操作：0=未执行，1=已执行")
    private Byte isExecuted;

    @Schema(title = "库存操作执行时间")
    private Date executedTime;

    @Schema(title = "发货时间")
    private Date shipTime;

    @Schema(title = "发货人ID")
    private Long shipperId;

    @Schema(title = "发货人名称")
    private String shipperName;

    @Schema(title = "确认收货时间")
    private Date confirmTime;

    @Schema(title = "确认人ID")
    private Long confirmerId;

    @Schema(title = "确认人名称")
    private String confirmerName;

    @Schema(title = "创建时间")
    private Date createdAt;

    @Schema(title = "更新时间")
    private Date updatedAt;

    @Schema(title = "操作理由")
    private String operationReason;

    @Schema(title = "操作描述")
    private String operationDescription;

    @Schema(title = "审核意见")
    private String approvalReason;

    @Schema(title = "确认备注（如数量差异说明）")
    private String confirmRemark;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOperationNo() {
        return operationNo;
    }

    public void setOperationNo(String operationNo) {
        this.operationNo = operationNo;
    }

    public Byte getOperationType() {
        return operationType;
    }

    public void setOperationType(Byte operationType) {
        this.operationType = operationType;
    }

    public Byte getOperationSubtype() {
        return operationSubtype;
    }

    public void setOperationSubtype(Byte operationSubtype) {
        this.operationSubtype = operationSubtype;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public Long getFromStoreId() {
        return fromStoreId;
    }

    public void setFromStoreId(Long fromStoreId) {
        this.fromStoreId = fromStoreId;
    }

    public Long getToStoreId() {
        return toStoreId;
    }

    public void setToStoreId(Long toStoreId) {
        this.toStoreId = toStoreId;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public Byte getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Byte approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Long getApproverId() {
        return approverId;
    }

    public void setApproverId(Long approverId) {
        this.approverId = approverId;
    }

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    public Date getApprovalTime() {
        return approvalTime;
    }

    public void setApprovalTime(Date approvalTime) {
        this.approvalTime = approvalTime;
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

    public String getOperatorIp() {
        return operatorIp;
    }

    public void setOperatorIp(String operatorIp) {
        this.operatorIp = operatorIp;
    }

    public Byte getIsExecuted() {
        return isExecuted;
    }

    public void setIsExecuted(Byte isExecuted) {
        this.isExecuted = isExecuted;
    }

    public Date getExecutedTime() {
        return executedTime;
    }

    public void setExecutedTime(Date executedTime) {
        this.executedTime = executedTime;
    }

    public Date getShipTime() {
        return shipTime;
    }

    public void setShipTime(Date shipTime) {
        this.shipTime = shipTime;
    }

    public Long getShipperId() {
        return shipperId;
    }

    public void setShipperId(Long shipperId) {
        this.shipperId = shipperId;
    }

    public String getShipperName() {
        return shipperName;
    }

    public void setShipperName(String shipperName) {
        this.shipperName = shipperName;
    }

    public Date getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(Date confirmTime) {
        this.confirmTime = confirmTime;
    }

    public Long getConfirmerId() {
        return confirmerId;
    }

    public void setConfirmerId(Long confirmerId) {
        this.confirmerId = confirmerId;
    }

    public String getConfirmerName() {
        return confirmerName;
    }

    public void setConfirmerName(String confirmerName) {
        this.confirmerName = confirmerName;
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

    public String getOperationReason() {
        return operationReason;
    }

    public void setOperationReason(String operationReason) {
        this.operationReason = operationReason;
    }

    public String getOperationDescription() {
        return operationDescription;
    }

    public void setOperationDescription(String operationDescription) {
        this.operationDescription = operationDescription;
    }

    public String getApprovalReason() {
        return approvalReason;
    }

    public void setApprovalReason(String approvalReason) {
        this.approvalReason = approvalReason;
    }

    public String getConfirmRemark() {
        return confirmRemark;
    }

    public void setConfirmRemark(String confirmRemark) {
        this.confirmRemark = confirmRemark;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", operationNo=").append(operationNo);
        sb.append(", operationType=").append(operationType);
        sb.append(", operationSubtype=").append(operationSubtype);
        sb.append(", storeId=").append(storeId);
        sb.append(", fromStoreId=").append(fromStoreId);
        sb.append(", toStoreId=").append(toStoreId);
        sb.append(", totalItems=").append(totalItems);
        sb.append(", approvalStatus=").append(approvalStatus);
        sb.append(", approverId=").append(approverId);
        sb.append(", approverName=").append(approverName);
        sb.append(", approvalTime=").append(approvalTime);
        sb.append(", operatorId=").append(operatorId);
        sb.append(", operatorName=").append(operatorName);
        sb.append(", operatorIp=").append(operatorIp);
        sb.append(", isExecuted=").append(isExecuted);
        sb.append(", executedTime=").append(executedTime);
        sb.append(", shipTime=").append(shipTime);
        sb.append(", shipperId=").append(shipperId);
        sb.append(", shipperName=").append(shipperName);
        sb.append(", confirmTime=").append(confirmTime);
        sb.append(", confirmerId=").append(confirmerId);
        sb.append(", confirmerName=").append(confirmerName);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", operationReason=").append(operationReason);
        sb.append(", operationDescription=").append(operationDescription);
        sb.append(", approvalReason=").append(approvalReason);
        sb.append(", confirmRemark=").append(confirmRemark);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}