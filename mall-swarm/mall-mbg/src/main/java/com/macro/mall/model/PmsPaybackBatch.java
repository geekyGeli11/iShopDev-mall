package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class PmsPaybackBatch implements Serializable {
    @Schema(title = "批次ID")
    private Long id;

    @Schema(title = "商品ID")
    private Long productId;

    @Schema(title = "商品名称")
    private String productName;

    @Schema(title = "商品货号")
    private String productSn;

    @Schema(title = "商品图片")
    private String productPic;

    @Schema(title = "批次序号（同一商品内递增）")
    private Integer batchNo;

    @Schema(title = "补货数量")
    private Integer replenishmentQuantity;

    @Schema(title = "补货金额（进货成本）")
    private BigDecimal replenishmentAmount;

    @Schema(title = "回本目标金额")
    private BigDecimal targetAmount;

    @Schema(title = "补货日期")
    private Date replenishmentDate;

    @Schema(title = "当前已售数量")
    private Integer currentSoldQuantity;

    @Schema(title = "当前已售金额")
    private BigDecimal currentSoldAmount;

    @Schema(title = "回本进度百分比")
    private BigDecimal paybackProgress;

    @Schema(title = "利润金额")
    private BigDecimal profitAmount;

    @Schema(title = "利润率百分比")
    private BigDecimal profitRate;

    @Schema(title = "批次状态：0-待启动，1-活跃，2-已回本，3-提前结束")
    private Byte batchStatus;

    @Schema(title = "开始统计日期")
    private Date startDate;

    @Schema(title = "回本完成日期")
    private Date completedDate;

    private Date createdAt;

    private Date updatedAt;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductSn() {
        return productSn;
    }

    public void setProductSn(String productSn) {
        this.productSn = productSn;
    }

    public String getProductPic() {
        return productPic;
    }

    public void setProductPic(String productPic) {
        this.productPic = productPic;
    }

    public Integer getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(Integer batchNo) {
        this.batchNo = batchNo;
    }

    public Integer getReplenishmentQuantity() {
        return replenishmentQuantity;
    }

    public void setReplenishmentQuantity(Integer replenishmentQuantity) {
        this.replenishmentQuantity = replenishmentQuantity;
    }

    public BigDecimal getReplenishmentAmount() {
        return replenishmentAmount;
    }

    public void setReplenishmentAmount(BigDecimal replenishmentAmount) {
        this.replenishmentAmount = replenishmentAmount;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public Date getReplenishmentDate() {
        return replenishmentDate;
    }

    public void setReplenishmentDate(Date replenishmentDate) {
        this.replenishmentDate = replenishmentDate;
    }

    public Integer getCurrentSoldQuantity() {
        return currentSoldQuantity;
    }

    public void setCurrentSoldQuantity(Integer currentSoldQuantity) {
        this.currentSoldQuantity = currentSoldQuantity;
    }

    public BigDecimal getCurrentSoldAmount() {
        return currentSoldAmount;
    }

    public void setCurrentSoldAmount(BigDecimal currentSoldAmount) {
        this.currentSoldAmount = currentSoldAmount;
    }

    public BigDecimal getPaybackProgress() {
        return paybackProgress;
    }

    public void setPaybackProgress(BigDecimal paybackProgress) {
        this.paybackProgress = paybackProgress;
    }

    public BigDecimal getProfitAmount() {
        return profitAmount;
    }

    public void setProfitAmount(BigDecimal profitAmount) {
        this.profitAmount = profitAmount;
    }

    public BigDecimal getProfitRate() {
        return profitRate;
    }

    public void setProfitRate(BigDecimal profitRate) {
        this.profitRate = profitRate;
    }

    public Byte getBatchStatus() {
        return batchStatus;
    }

    public void setBatchStatus(Byte batchStatus) {
        this.batchStatus = batchStatus;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(Date completedDate) {
        this.completedDate = completedDate;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", productId=").append(productId);
        sb.append(", productName=").append(productName);
        sb.append(", productSn=").append(productSn);
        sb.append(", productPic=").append(productPic);
        sb.append(", batchNo=").append(batchNo);
        sb.append(", replenishmentQuantity=").append(replenishmentQuantity);
        sb.append(", replenishmentAmount=").append(replenishmentAmount);
        sb.append(", targetAmount=").append(targetAmount);
        sb.append(", replenishmentDate=").append(replenishmentDate);
        sb.append(", currentSoldQuantity=").append(currentSoldQuantity);
        sb.append(", currentSoldAmount=").append(currentSoldAmount);
        sb.append(", paybackProgress=").append(paybackProgress);
        sb.append(", profitAmount=").append(profitAmount);
        sb.append(", profitRate=").append(profitRate);
        sb.append(", batchStatus=").append(batchStatus);
        sb.append(", startDate=").append(startDate);
        sb.append(", completedDate=").append(completedDate);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}