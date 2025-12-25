package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * 商品列表页DTO
 * Created for product-list page
 */
@Schema(description = "商品列表信息")
public class ProductListDTO {
    
    @Schema(description = "商品ID")
    private Long id;
    
    @Schema(description = "商品标题")
    private String name;
    
    @Schema(description = "商品封面")
    private String pic;
    
    @Schema(description = "商品销量")
    private Integer sale;
    
    @Schema(description = "商品价格")
    private BigDecimal price;
    
    @Schema(description = "收藏人数")
    private Integer collectCount;
    
    @Schema(description = "库存")
    private Integer stock;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public Integer getSale() {
        return sale;
    }

    public void setSale(Integer sale) {
        this.sale = sale;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(Integer collectCount) {
        this.collectCount = collectCount;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
} 