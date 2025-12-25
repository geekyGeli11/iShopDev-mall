package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品导出DTO
 * Created by macro on 2024/12/30.
 */
@Schema(title = "商品导出DTO")
public class PmsProductExportDTO implements Serializable {
    
    @Schema(title = "商品ID")
    private Long productId;
    
    @Schema(title = "商品名称")
    private String productName;
    
    @Schema(title = "商品货号")
    private String productSn;
    
    @Schema(title = "品牌名称")
    private String brandName;
    
    @Schema(title = "商品分类名称")
    private String productCategoryName;
    
    @Schema(title = "商品价格")
    private BigDecimal price;
    
    @Schema(title = "促销价格")
    private BigDecimal promotionPrice;
    
    @Schema(title = "商品重量(克)")
    private BigDecimal weight;
    
    @Schema(title = "商品单位")
    private String unit;
    
    @Schema(title = "商品库存")
    private Integer stock;
    
    @Schema(title = "库存预警值")
    private Integer lowStock;
    
    @Schema(title = "销量")
    private Integer sale;
    
    @Schema(title = "上架状态：0->下架；1->上架")
    private Integer publishStatus;
    
    @Schema(title = "上架状态名称")
    private String publishStatusName;
    
    @Schema(title = "新品状态:0->不是新品；1->新品")
    private Integer newStatus;
    
    @Schema(title = "新品状态名称")
    private String newStatusName;
    
    @Schema(title = "推荐状态；0->不推荐；1->推荐")
    private Integer recommandStatus;
    
    @Schema(title = "推荐状态名称")
    private String recommandStatusName;
    
    @Schema(title = "DIY状态：0->不支持；1->支持")
    private Byte diyEnabled;
    
    @Schema(title = "DIY状态名称")
    private String diyEnabledName;
    
    @Schema(title = "运费模板ID")
    private Long freightTemplateId;
    
    @Schema(title = "运费模板名称")
    private String freightTemplateName;
    
    @Schema(title = "关联学校名称（多个用逗号分隔）")
    private String schoolNames;
    
    @Schema(title = "SKU ID")
    private Long skuId;
    
    @Schema(title = "SKU编码")
    private String skuCode;
    
    @Schema(title = "SKU价格")
    private BigDecimal skuPrice;
    
    @Schema(title = "SKU库存")
    private Integer skuStock;
    
    @Schema(title = "SKU库存预警值")
    private Integer skuLowStock;
    
    @Schema(title = "SKU规格信息")
    private String skuSpData;
    
    @Schema(title = "商品副标题")
    private String subTitle;
    
    @Schema(title = "商品关键词")
    private String keywords;
    
    @Schema(title = "商品备注")
    private String note;
    
    @Schema(title = "商品服务：1->无忧退货；2->快速退款；3->免费包邮")
    private String serviceIds;
    
    @Schema(title = "商品服务名称")
    private String serviceNames;
    
    @Schema(title = "创建时间")
    private Date createTime;
    
    @Schema(title = "更新时间")
    private Date updateTime;
    
    private static final long serialVersionUID = 1L;

    // Getter and Setter methods
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

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getProductCategoryName() {
        return productCategoryName;
    }

    public void setProductCategoryName(String productCategoryName) {
        this.productCategoryName = productCategoryName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPromotionPrice() {
        return promotionPrice;
    }

    public void setPromotionPrice(BigDecimal promotionPrice) {
        this.promotionPrice = promotionPrice;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getLowStock() {
        return lowStock;
    }

    public void setLowStock(Integer lowStock) {
        this.lowStock = lowStock;
    }

    public Integer getSale() {
        return sale;
    }

    public void setSale(Integer sale) {
        this.sale = sale;
    }

    public Integer getPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(Integer publishStatus) {
        this.publishStatus = publishStatus;
    }

    public String getPublishStatusName() {
        return publishStatusName;
    }

    public void setPublishStatusName(String publishStatusName) {
        this.publishStatusName = publishStatusName;
    }

    public Integer getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(Integer newStatus) {
        this.newStatus = newStatus;
    }

    public String getNewStatusName() {
        return newStatusName;
    }

    public void setNewStatusName(String newStatusName) {
        this.newStatusName = newStatusName;
    }

    public Integer getRecommandStatus() {
        return recommandStatus;
    }

    public void setRecommandStatus(Integer recommandStatus) {
        this.recommandStatus = recommandStatus;
    }

    public String getRecommandStatusName() {
        return recommandStatusName;
    }

    public void setRecommandStatusName(String recommandStatusName) {
        this.recommandStatusName = recommandStatusName;
    }

    public Byte getDiyEnabled() {
        return diyEnabled;
    }

    public void setDiyEnabled(Byte diyEnabled) {
        this.diyEnabled = diyEnabled;
    }

    public String getDiyEnabledName() {
        return diyEnabledName;
    }

    public void setDiyEnabledName(String diyEnabledName) {
        this.diyEnabledName = diyEnabledName;
    }

    public Long getFreightTemplateId() {
        return freightTemplateId;
    }

    public void setFreightTemplateId(Long freightTemplateId) {
        this.freightTemplateId = freightTemplateId;
    }

    public String getFreightTemplateName() {
        return freightTemplateName;
    }

    public void setFreightTemplateName(String freightTemplateName) {
        this.freightTemplateName = freightTemplateName;
    }

    public String getSchoolNames() {
        return schoolNames;
    }

    public void setSchoolNames(String schoolNames) {
        this.schoolNames = schoolNames;
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

    public BigDecimal getSkuPrice() {
        return skuPrice;
    }

    public void setSkuPrice(BigDecimal skuPrice) {
        this.skuPrice = skuPrice;
    }

    public Integer getSkuStock() {
        return skuStock;
    }

    public void setSkuStock(Integer skuStock) {
        this.skuStock = skuStock;
    }

    public Integer getSkuLowStock() {
        return skuLowStock;
    }

    public void setSkuLowStock(Integer skuLowStock) {
        this.skuLowStock = skuLowStock;
    }

    public String getSkuSpData() {
        return skuSpData;
    }

    public void setSkuSpData(String skuSpData) {
        this.skuSpData = skuSpData;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(String serviceIds) {
        this.serviceIds = serviceIds;
    }

    public String getServiceNames() {
        return serviceNames;
    }

    public void setServiceNames(String serviceNames) {
        this.serviceNames = serviceNames;
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
}
