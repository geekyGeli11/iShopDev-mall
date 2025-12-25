package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 非系统销售单DTO
 * Created by macro on 2025-11-27.
 */
@Schema(title = "非系统销售单DTO")
public class PmsNonSystemSaleDTO implements Serializable {
    
    @Schema(title = "销售单ID")
    private Long id;

    @Schema(title = "销售单号")
    private String saleNo;

    @Schema(title = "销售类型ID")
    private Long saleTypeId;

    @Schema(title = "销售类型名称")
    private String saleTypeName;

    @Schema(title = "销售日期")
    private Date saleDate;

    @Schema(title = "销售总金额")
    private BigDecimal totalAmount;

    @Schema(title = "销售总数量")
    private Integer totalQuantity;

    @Schema(title = "备注")
    private String remark;

    @Schema(title = "客户姓名")
    private String customerName;

    @Schema(title = "客户手机号码")
    private String customerPhone;

    @Schema(title = "操作人ID")
    private Long operatorId;

    @Schema(title = "操作人名称")
    private String operatorName;

    @Schema(title = "状态：1-已提交，2-已审核，3-已驳回")
    private Byte status;

    @Schema(title = "状态名称")
    private String statusName;

    @Schema(title = "销售商品明细列表")
    private List<PmsNonSystemSaleItemDTO> items;

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

    public String getSaleNo() {
        return saleNo;
    }

    public void setSaleNo(String saleNo) {
        this.saleNo = saleNo;
    }

    public Long getSaleTypeId() {
        return saleTypeId;
    }

    public void setSaleTypeId(Long saleTypeId) {
        this.saleTypeId = saleTypeId;
    }

    public String getSaleTypeName() {
        return saleTypeName;
    }

    public void setSaleTypeName(String saleTypeName) {
        this.saleTypeName = saleTypeName;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
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

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public List<PmsNonSystemSaleItemDTO> getItems() {
        return items;
    }

    public void setItems(List<PmsNonSystemSaleItemDTO> items) {
        this.items = items;
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

    /**
     * 销售商品明细DTO
     */
    @Schema(title = "销售商品明细DTO")
    public static class PmsNonSystemSaleItemDTO implements Serializable {
        
        @Schema(title = "销售明细ID")
        private Long id;

        @Schema(title = "商品ID")
        private Long productId;

        @Schema(title = "商品名称")
        private String productName;

        @Schema(title = "SKU ID")
        private Long skuId;

        @Schema(title = "SKU编码")
        private String skuCode;

        @Schema(title = "商品图片")
        private String productPic;

        @Schema(title = "规格信息")
        private String specs;

        @Schema(title = "销售数量")
        private Integer quantity;

        @Schema(title = "系统价格")
        private BigDecimal systemPrice;

        @Schema(title = "销售价格")
        private BigDecimal salePrice;

        @Schema(title = "行金额（销售价格*数量）")
        private BigDecimal lineAmount;

        @Schema(title = "从销售门店库存扣减的数量")
        private Integer storeStockDeducted;

        @Schema(title = "从其他门店调动的数量")
        private Integer totalStockDeducted;

        @Schema(title = "库存扣减详情（JSON格式）")
        private String stockDeductionDetail;

        @Schema(title = "创建时间")
        private Date createTime;

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

        public Long getSkuId() {
            return skuId;
        }

        public void setSkuId(Long skuId) {
            this.skuId = skuId;
        }

        public String getSkuCode() {
            return skuCode;
        }

        public void setSkuCode(String skuCode) {
            this.skuCode = skuCode;
        }

        public String getProductPic() {
            return productPic;
        }

        public void setProductPic(String productPic) {
            this.productPic = productPic;
        }

        public String getSpecs() {
            return specs;
        }

        public void setSpecs(String specs) {
            this.specs = specs;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getSystemPrice() {
            return systemPrice;
        }

        public void setSystemPrice(BigDecimal systemPrice) {
            this.systemPrice = systemPrice;
        }

        public BigDecimal getSalePrice() {
            return salePrice;
        }

        public void setSalePrice(BigDecimal salePrice) {
            this.salePrice = salePrice;
        }

        public BigDecimal getLineAmount() {
            return lineAmount;
        }

        public void setLineAmount(BigDecimal lineAmount) {
            this.lineAmount = lineAmount;
        }

        public Integer getStoreStockDeducted() {
            return storeStockDeducted;
        }

        public void setStoreStockDeducted(Integer storeStockDeducted) {
            this.storeStockDeducted = storeStockDeducted;
        }

        public Integer getTotalStockDeducted() {
            return totalStockDeducted;
        }

        public void setTotalStockDeducted(Integer totalStockDeducted) {
            this.totalStockDeducted = totalStockDeducted;
        }

        public String getStockDeductionDetail() {
            return stockDeductionDetail;
        }

        public void setStockDeductionDetail(String stockDeductionDetail) {
            this.stockDeductionDetail = stockDeductionDetail;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

        @Override
        public String toString() {
            return "PmsNonSystemSaleItemDTO{" +
                    "id=" + id +
                    ", productId=" + productId +
                    ", productName='" + productName + '\'' +
                    ", skuId=" + skuId +
                    ", skuCode='" + skuCode + '\'' +
                    ", quantity=" + quantity +
                    ", systemPrice=" + systemPrice +
                    ", salePrice=" + salePrice +
                    ", lineAmount=" + lineAmount +
                    ", createTime=" + createTime +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PmsNonSystemSaleDTO{" +
                "id=" + id +
                ", saleNo='" + saleNo + '\'' +
                ", saleTypeId=" + saleTypeId +
                ", saleTypeName='" + saleTypeName + '\'' +
                ", saleDate=" + saleDate +
                ", totalAmount=" + totalAmount +
                ", totalQuantity=" + totalQuantity +
                ", remark='" + remark + '\'' +
                ", customerName='" + customerName + '\'' +
                ", customerPhone='" + customerPhone + '\'' +
                ", operatorId=" + operatorId +
                ", operatorName='" + operatorName + '\'' +
                ", status=" + status +
                ", statusName='" + statusName + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
