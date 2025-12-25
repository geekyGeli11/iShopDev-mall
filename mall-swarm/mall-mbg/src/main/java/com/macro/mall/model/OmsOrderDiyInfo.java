package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

public class OmsOrderDiyInfo implements Serializable {
    @Schema(title = "主键ID")
    private Long id;

    @Schema(title = "订单ID")
    private Long orderId;

    @Schema(title = "订单项ID")
    private Long orderItemId;

    @Schema(title = "订单编号")
    private String orderSn;

    @Schema(title = "设计ID")
    private Long designId;

    @Schema(title = "模板ID")
    private Long templateId;

    @Schema(title = "预览图片URL")
    private String previewImage;

    @Schema(title = "生产状态：0-待生产，1-生产中，2-已完成")
    private Byte productionStatus;

    @Schema(title = "创建时间")
    private Date createTime;

    @Schema(title = "更新时间")
    private Date updateTime;

    @Schema(title = "最终效果图URLs，逗号分隔")
    private String finalImages;

    @Schema(title = "设计数据快照")
    private String designSnapshot;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public Long getDesignId() {
        return designId;
    }

    public void setDesignId(Long designId) {
        this.designId = designId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getPreviewImage() {
        return previewImage;
    }

    public void setPreviewImage(String previewImage) {
        this.previewImage = previewImage;
    }

    public Byte getProductionStatus() {
        return productionStatus;
    }

    public void setProductionStatus(Byte productionStatus) {
        this.productionStatus = productionStatus;
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

    public String getFinalImages() {
        return finalImages;
    }

    public void setFinalImages(String finalImages) {
        this.finalImages = finalImages;
    }

    public String getDesignSnapshot() {
        return designSnapshot;
    }

    public void setDesignSnapshot(String designSnapshot) {
        this.designSnapshot = designSnapshot;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", orderId=").append(orderId);
        sb.append(", orderItemId=").append(orderItemId);
        sb.append(", orderSn=").append(orderSn);
        sb.append(", designId=").append(designId);
        sb.append(", templateId=").append(templateId);
        sb.append(", previewImage=").append(previewImage);
        sb.append(", productionStatus=").append(productionStatus);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", finalImages=").append(finalImages);
        sb.append(", designSnapshot=").append(designSnapshot);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}