package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class SmsFreightTemplate implements Serializable {
    private Long id;

    @Schema(title = "模板名称")
    private String name;

    @Schema(title = "计费类型：1-按件数,2-按重量,3-按体积,4-固定运费")
    private Byte chargeType;

    @Schema(title = "配送类型：1-快递配送,2-同城配送,3-到店自提")
    private Byte deliveryType;

    @Schema(title = "包邮类型：0-不包邮,1-满金额包邮,2-满件数包邮,3-满重量包邮；4-无条件包邮")
    private Byte freeType;

    @Schema(title = "包邮金额条件")
    private BigDecimal freeAmount;

    @Schema(title = "包邮件数条件")
    private Integer freeCount;

    @Schema(title = "包邮重量条件(kg)")
    private BigDecimal freeWeight;

    @Schema(title = "状态：0-禁用，1-启用")
    private Byte status;

    @Schema(title = "是否默认模板：0-否，1-是")
    private Boolean isDefault;

    @Schema(title = "排序")
    private Integer sort;

    @Schema(title = "备注说明")
    private String remark;

    private Date createTime;

    private Date updateTime;

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

    public Byte getChargeType() {
        return chargeType;
    }

    public void setChargeType(Byte chargeType) {
        this.chargeType = chargeType;
    }

    public Byte getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(Byte deliveryType) {
        this.deliveryType = deliveryType;
    }

    public Byte getFreeType() {
        return freeType;
    }

    public void setFreeType(Byte freeType) {
        this.freeType = freeType;
    }

    public BigDecimal getFreeAmount() {
        return freeAmount;
    }

    public void setFreeAmount(BigDecimal freeAmount) {
        this.freeAmount = freeAmount;
    }

    public Integer getFreeCount() {
        return freeCount;
    }

    public void setFreeCount(Integer freeCount) {
        this.freeCount = freeCount;
    }

    public BigDecimal getFreeWeight() {
        return freeWeight;
    }

    public void setFreeWeight(BigDecimal freeWeight) {
        this.freeWeight = freeWeight;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
        sb.append(", name=").append(name);
        sb.append(", chargeType=").append(chargeType);
        sb.append(", deliveryType=").append(deliveryType);
        sb.append(", freeType=").append(freeType);
        sb.append(", freeAmount=").append(freeAmount);
        sb.append(", freeCount=").append(freeCount);
        sb.append(", freeWeight=").append(freeWeight);
        sb.append(", status=").append(status);
        sb.append(", isDefault=").append(isDefault);
        sb.append(", sort=").append(sort);
        sb.append(", remark=").append(remark);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}