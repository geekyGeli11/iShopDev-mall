package com.macro.mall.portal.domain;

import com.macro.mall.model.PmsDiyTemplate;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 商品DIY配置信息
 * Created by macro on 2024/12/20.
 */
public class ProductDiyConfig {
    
    @Schema(title = "商品ID")
    private Long productId;
    
    @Schema(title = "商品名称")
    private String productName;
    
    @Schema(title = "是否支持DIY")
    private Boolean diyEnabled;
    
    @Schema(title = "DIY模板ID")
    private Long diyTemplateId;
    
    @Schema(title = "DIY模板信息")
    private PmsDiyTemplate diyTemplate;
    
    @Schema(title = "商品主图")
    private String productImage;
    
    @Schema(title = "商品价格")
    private java.math.BigDecimal price;

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

    public Boolean getDiyEnabled() {
        return diyEnabled;
    }

    public void setDiyEnabled(Boolean diyEnabled) {
        this.diyEnabled = diyEnabled;
    }

    public Long getDiyTemplateId() {
        return diyTemplateId;
    }

    public void setDiyTemplateId(Long diyTemplateId) {
        this.diyTemplateId = diyTemplateId;
    }

    public PmsDiyTemplate getDiyTemplate() {
        return diyTemplate;
    }

    public void setDiyTemplate(PmsDiyTemplate diyTemplate) {
        this.diyTemplate = diyTemplate;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public java.math.BigDecimal getPrice() {
        return price;
    }

    public void setPrice(java.math.BigDecimal price) {
        this.price = price;
    }
}
