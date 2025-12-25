package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class PmsProductPaybackAnalysis implements Serializable {
    private Long id;

    @Schema(title = "商品ID")
    private Long productId;

    @Schema(title = "商品名称")
    private String productName;

    @Schema(title = "商品货号")
    private String productSn;

    @Schema(title = "回本目标数量")
    private Integer targetQuantity;

    @Schema(title = "回本目标金额")
    private BigDecimal targetAmount;

    @Schema(title = "当前已售数量")
    private Integer currentSoldQuantity;

    @Schema(title = "当前销售金额")
    private BigDecimal currentSoldAmount;

    @Schema(title = "回本进度百分比")
    private BigDecimal paybackProgress;

    @Schema(title = "回本状态：0-未开始，1-回本中，2-已回本，3-销售缓慢，4-已下架")
    private Byte paybackStatus;

    @Schema(title = "开始统计日期")
    private Date startDate;

    @Schema(title = "回本完成日期")
    private Date paybackCompletedDate;

    @Schema(title = "实际回本天数")
    private Integer paybackDays;

    @Schema(title = "日均销量")
    private BigDecimal dailyAvgQuantity;

    @Schema(title = "预计完成日期")
    private Date predictedCompletionDate;

    @Schema(title = "最后一次销售日期")
    private Date lastOrderDate;

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

    public Integer getTargetQuantity() {
        return targetQuantity;
    }

    public void setTargetQuantity(Integer targetQuantity) {
        this.targetQuantity = targetQuantity;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
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

    public Byte getPaybackStatus() {
        return paybackStatus;
    }

    public void setPaybackStatus(Byte paybackStatus) {
        this.paybackStatus = paybackStatus;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getPaybackCompletedDate() {
        return paybackCompletedDate;
    }

    public void setPaybackCompletedDate(Date paybackCompletedDate) {
        this.paybackCompletedDate = paybackCompletedDate;
    }

    public Integer getPaybackDays() {
        return paybackDays;
    }

    public void setPaybackDays(Integer paybackDays) {
        this.paybackDays = paybackDays;
    }

    public BigDecimal getDailyAvgQuantity() {
        return dailyAvgQuantity;
    }

    public void setDailyAvgQuantity(BigDecimal dailyAvgQuantity) {
        this.dailyAvgQuantity = dailyAvgQuantity;
    }

    public Date getPredictedCompletionDate() {
        return predictedCompletionDate;
    }

    public void setPredictedCompletionDate(Date predictedCompletionDate) {
        this.predictedCompletionDate = predictedCompletionDate;
    }

    public Date getLastOrderDate() {
        return lastOrderDate;
    }

    public void setLastOrderDate(Date lastOrderDate) {
        this.lastOrderDate = lastOrderDate;
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
        sb.append(", targetQuantity=").append(targetQuantity);
        sb.append(", targetAmount=").append(targetAmount);
        sb.append(", currentSoldQuantity=").append(currentSoldQuantity);
        sb.append(", currentSoldAmount=").append(currentSoldAmount);
        sb.append(", paybackProgress=").append(paybackProgress);
        sb.append(", paybackStatus=").append(paybackStatus);
        sb.append(", startDate=").append(startDate);
        sb.append(", paybackCompletedDate=").append(paybackCompletedDate);
        sb.append(", paybackDays=").append(paybackDays);
        sb.append(", dailyAvgQuantity=").append(dailyAvgQuantity);
        sb.append(", predictedCompletionDate=").append(predictedCompletionDate);
        sb.append(", lastOrderDate=").append(lastOrderDate);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}