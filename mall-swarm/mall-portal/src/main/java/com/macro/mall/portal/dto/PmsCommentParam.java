package com.macro.mall.portal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 商品评价参数
 * Created by guanghengzhou on 2024/01/13.
 */
@Schema(description = "商品评价参数")
public class PmsCommentParam {
    
    @Schema(description = "订单ID")
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    
    @Schema(description = "商品ID")
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    
    @Schema(description = "评价星数，1-5星")
    @NotNull(message = "评价星数不能为空")
    @Min(value = 1, message = "评价星数最少1星")
    @Max(value = 5, message = "评价星数最多5星")
    private Integer star;
    
    @Schema(description = "评价内容")
    @NotBlank(message = "评价内容不能为空")
    private String content;
    
    @Schema(description = "是否匿名，0-否，1-是")
    private Integer isAnonymous = 0;
    
    @Schema(description = "上传图片地址，以逗号隔开")
    private String pics;
    
    @Schema(description = "购买时的商品属性")
    private String productAttribute;
    
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public Integer getStar() {
        return star;
    }
    
    public void setStar(Integer star) {
        this.star = star;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Integer getIsAnonymous() {
        return isAnonymous;
    }
    
    public void setIsAnonymous(Integer isAnonymous) {
        this.isAnonymous = isAnonymous;
    }
    
    public String getPics() {
        return pics;
    }
    
    public void setPics(String pics) {
        this.pics = pics;
    }
    
    public String getProductAttribute() {
        return productAttribute;
    }
    
    public void setProductAttribute(String productAttribute) {
        this.productAttribute = productAttribute;
    }
} 