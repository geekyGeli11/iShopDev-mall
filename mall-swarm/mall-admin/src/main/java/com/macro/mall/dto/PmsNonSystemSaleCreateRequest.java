package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 非系统销售数据创建请求
 * Created by macro on 2025-11-27.
 */
@Schema(title = "非系统销售单创建请求")
public class PmsNonSystemSaleCreateRequest implements Serializable {
    
    @Schema(title = "销售类型ID", required = true)
    private Long saleTypeId;

    @Schema(title = "门店ID", required = true)
    private Long storeId;

    @Schema(title = "门店名称")
    private String storeName;

    @Schema(title = "销售日期", required = true)
    private Date saleDate;

    @Schema(title = "备注")
    private String remark;

    @Schema(title = "客户姓名")
    private String customerName;

    @Schema(title = "客户手机号码")
    private String customerPhone;

    @Schema(title = "销售商品明细列表", required = true)
    private List<PmsNonSystemSaleItemRequest> items;

    @Schema(title = "库存扣减方案")
    private List<PmsStockDeductionPlan> stockDeductionPlans;

    private static final long serialVersionUID = 1L;

    public Long getSaleTypeId() {
        return saleTypeId;
    }

    public void setSaleTypeId(Long saleTypeId) {
        this.saleTypeId = saleTypeId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
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

    public List<PmsNonSystemSaleItemRequest> getItems() {
        return items;
    }

    public void setItems(List<PmsNonSystemSaleItemRequest> items) {
        this.items = items;
    }

    public List<PmsStockDeductionPlan> getStockDeductionPlans() {
        return stockDeductionPlans;
    }

    public void setStockDeductionPlans(List<PmsStockDeductionPlan> stockDeductionPlans) {
        this.stockDeductionPlans = stockDeductionPlans;
    }

    /**
     * 销售商品明细请求
     */
    @Schema(title = "销售商品明细")
    public static class PmsNonSystemSaleItemRequest implements Serializable {
        
        @Schema(title = "商品ID", required = true)
        private Long productId;

        @Schema(title = "商品名称")
        private String productName;

        @Schema(title = "SKU ID", required = true)
        private Long skuId;

        @Schema(title = "SKU编码")
        private String skuCode;

        @Schema(title = "销售数量", required = true)
        private Integer quantity;

        @Schema(title = "系统价格")
        private BigDecimal systemPrice;

        @Schema(title = "销售价格", required = true)
        private BigDecimal salePrice;

        private static final long serialVersionUID = 1L;

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

        @Override
        public String toString() {
            return "PmsNonSystemSaleItemRequest{" +
                    "productId=" + productId +
                    ", productName='" + productName + '\'' +
                    ", skuId=" + skuId +
                    ", skuCode='" + skuCode + '\'' +
                    ", quantity=" + quantity +
                    ", systemPrice=" + systemPrice +
                    ", salePrice=" + salePrice +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PmsNonSystemSaleCreateRequest{" +
                "saleTypeId=" + saleTypeId +
                ", storeId=" + storeId +
                ", storeName='" + storeName + '\'' +
                ", saleDate=" + saleDate +
                ", remark='" + remark + '\'' +
                ", customerName='" + customerName + '\'' +
                ", customerPhone='" + customerPhone + '\'' +
                ", items=" + items +
                '}';
    }
}
