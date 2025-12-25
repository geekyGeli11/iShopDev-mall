package com.macro.mall.portal.domain;

import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.OmsOrderDiyInfo;
import com.macro.mall.model.OmsOrderItem;
import com.macro.mall.model.UmsDiyDesign;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * DIY订单详情VO
 * Created by macro on 2024/12/20.
 */
public class DiyOrderDetailVO {
    
    @Schema(title = "订单ID")
    private Long orderId;
    
    @Schema(title = "订单编号")
    private String orderSn;
    
    @Schema(title = "订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭")
    private Integer status;
    
    @Schema(title = "订单状态名称")
    private String statusName;
    
    @Schema(title = "商品ID")
    private Long productId;
    
    @Schema(title = "商品名称")
    private String productName;
    
    @Schema(title = "商品图片")
    private String productPic;
    
    @Schema(title = "商品价格")
    private BigDecimal productPrice;
    
    @Schema(title = "购买数量")
    private Integer quantity;
    
    @Schema(title = "订单总金额")
    private BigDecimal totalAmount;
    
    @Schema(title = "实付金额")
    private BigDecimal payAmount;
    
    @Schema(title = "运费金额")
    private BigDecimal freightAmount;
    
    @Schema(title = "优惠券抵扣金额")
    private BigDecimal couponAmount;
    
    @Schema(title = "积分抵扣金额")
    private BigDecimal integrationAmount;
    
    @Schema(title = "收货人姓名")
    private String receiverName;
    
    @Schema(title = "收货人电话")
    private String receiverPhone;
    
    @Schema(title = "收货地址")
    private String receiverAddress;
    
    @Schema(title = "订单备注")
    private String note;
    
    @Schema(title = "创建时间")
    private Date createTime;
    
    @Schema(title = "支付时间")
    private Date payTime;
    
    @Schema(title = "发货时间")
    private Date deliveryTime;
    
    @Schema(title = "确认收货时间")
    private Date receiveTime;
    
    @Schema(title = "DIY设计ID")
    private Long designId;
    
    @Schema(title = "模板ID")
    private Long templateId;
    
    @Schema(title = "设计数据")
    private String designData;
    
    @Schema(title = "预览图片")
    private String previewImage;
    
    @Schema(title = "最终效果图URLs")
    private String finalImages;
    
    @Schema(title = "生产状态：0->待生产；1->生产中；2->已完成")
    private Byte productionStatus;
    
    @Schema(title = "生产状态名称")
    private String productionStatusName;
    
    @Schema(title = "定制面列表")
    private List<DiyFaceVO> customizeFaces;
    
    @Schema(title = "是否可以取消")
    private Boolean canCancel;
    
    @Schema(title = "是否可以支付")
    private Boolean canPay;
    
    @Schema(title = "是否可以确认收货")
    private Boolean canConfirm;
    
    @Schema(title = "是否可以评价")
    private Boolean canComment;
    
    @Schema(title = "是否可以申请退款")
    private Boolean canRefund;

    // Getters and Setters
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
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

    public String getProductPic() {
        return productPic;
    }

    public void setProductPic(String productPic) {
        this.productPic = productPic;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public BigDecimal getFreightAmount() {
        return freightAmount;
    }

    public void setFreightAmount(BigDecimal freightAmount) {
        this.freightAmount = freightAmount;
    }

    public BigDecimal getCouponAmount() {
        return couponAmount;
    }

    public void setCouponAmount(BigDecimal couponAmount) {
        this.couponAmount = couponAmount;
    }

    public BigDecimal getIntegrationAmount() {
        return integrationAmount;
    }

    public void setIntegrationAmount(BigDecimal integrationAmount) {
        this.integrationAmount = integrationAmount;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Date getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
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

    public String getFinalImages() {
        return finalImages;
    }

    public void setFinalImages(String finalImages) {
        this.finalImages = finalImages;
    }

    public Byte getProductionStatus() {
        return productionStatus;
    }

    public void setProductionStatus(Byte productionStatus) {
        this.productionStatus = productionStatus;
    }

    public String getProductionStatusName() {
        return productionStatusName;
    }

    public void setProductionStatusName(String productionStatusName) {
        this.productionStatusName = productionStatusName;
    }

    public List<DiyFaceVO> getCustomizeFaces() {
        return customizeFaces;
    }

    public void setCustomizeFaces(List<DiyFaceVO> customizeFaces) {
        this.customizeFaces = customizeFaces;
    }

    public Boolean getCanCancel() {
        return canCancel;
    }

    public void setCanCancel(Boolean canCancel) {
        this.canCancel = canCancel;
    }

    public Boolean getCanPay() {
        return canPay;
    }

    public void setCanPay(Boolean canPay) {
        this.canPay = canPay;
    }

    public Boolean getCanConfirm() {
        return canConfirm;
    }

    public void setCanConfirm(Boolean canConfirm) {
        this.canConfirm = canConfirm;
    }

    public Boolean getCanComment() {
        return canComment;
    }

    public void setCanComment(Boolean canComment) {
        this.canComment = canComment;
    }

    public Boolean getCanRefund() {
        return canRefund;
    }

    public void setCanRefund(Boolean canRefund) {
        this.canRefund = canRefund;
    }
    
    /**
     * DIY定制面VO
     */
    public static class DiyFaceVO {
        @Schema(title = "面ID")
        private Long faceId;
        
        @Schema(title = "面名称")
        private String faceName;
        
        @Schema(title = "预览图")
        private String previewImage;
        
        @Schema(title = "最终效果图")
        private String finalImage;

        public Long getFaceId() {
            return faceId;
        }

        public void setFaceId(Long faceId) {
            this.faceId = faceId;
        }

        public String getFaceName() {
            return faceName;
        }

        public void setFaceName(String faceName) {
            this.faceName = faceName;
        }

        public String getPreviewImage() {
            return previewImage;
        }

        public void setPreviewImage(String previewImage) {
            this.previewImage = previewImage;
        }

        public String getFinalImage() {
            return finalImage;
        }

        public void setFinalImage(String finalImage) {
            this.finalImage = finalImage;
        }
    }
}
