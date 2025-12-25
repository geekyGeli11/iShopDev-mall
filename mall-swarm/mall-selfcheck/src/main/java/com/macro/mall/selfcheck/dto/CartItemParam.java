package com.macro.mall.selfcheck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 购物车商品操作参数
 * 
 * @author macro
 * @since 1.0.0
 */
@Data
@Schema(title = "购物车商品操作参数")
public class CartItemParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(title = "商品ID", description = "商品基础信息ID", example = "1")
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @Schema(title = "SKU ID", description = "商品规格ID，如果商品有规格则必传", example = "1")
    private Long skuId;

    @Schema(title = "数量", description = "商品数量，必须大于0", example = "2")
    @NotNull(message = "商品数量不能为空")
    @Min(value = 1, message = "商品数量必须大于0")
    private Integer quantity;

    @Schema(title = "操作类型", description = "操作类型：ADD-添加，UPDATE-更新，REMOVE-删除", example = "ADD")
    @NotBlank(message = "操作类型不能为空")
    private String operation = "ADD";

    @Schema(title = "游客ID", description = "游客购物车需要传递游客ID", example = "guest_12345")
    private String guestId;

    @Schema(title = "强制更新", description = "是否强制更新，忽略库存检查", example = "false")
    private Boolean forceUpdate = false;

    @Schema(title = "备注", description = "操作备注", example = "扫码添加")
    private String remark;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getGuestId() {
        return guestId;
    }

    public void setGuestId(String guestId) {
        this.guestId = guestId;
    }

    public Boolean getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(Boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "CartItemParam{" +
                "productId=" + productId +
                ", skuId=" + skuId +
                ", quantity=" + quantity +
                ", operation='" + operation + '\'' +
                ", guestId='" + guestId + '\'' +
                ", forceUpdate=" + forceUpdate +
                ", remark='" + remark + '\'' +
                '}';
    }
} 