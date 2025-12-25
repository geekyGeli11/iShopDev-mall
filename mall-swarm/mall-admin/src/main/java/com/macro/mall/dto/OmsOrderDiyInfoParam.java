package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

/**
 * 订单DIY信息创建/更新参数
 * Created by macro on 2024/12/20.
 */
public class OmsOrderDiyInfoParam {
    
    @Schema(title = "订单ID", required = true)
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    
    @Schema(title = "订单项ID", required = true)
    @NotNull(message = "订单项ID不能为空")
    private Long orderItemId;
    
    @Schema(title = "订单编号")
    private String orderSn;
    
    @Schema(title = "商品ID")
    private Long productId;
    
    @Schema(title = "商品名称")
    private String productName;
    
    @Schema(title = "DIY模板ID")
    private Long templateId;
    
    @Schema(title = "设计数据(JSON格式)")
    private String designData;
    
    @Schema(title = "预览图URL")
    private String previewImage;
    
    @Schema(title = "生产状态：0-待生产，1-生产中，2-已完成")
    private Byte productionStatus;

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

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getDesignData() {
        return designData;
    }

    public void setDesignData(String designData) {
        this.designData = designData;
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
}
