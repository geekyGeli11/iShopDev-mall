package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class PmsProductBundle implements Serializable {
    private Long id;

    @Schema(title = "组合名称")
    private String name;

    @Schema(title = "组合主图")
    private String pic;

    @Schema(title = "组合宣传图片，多个用逗号分隔")
    private String albumPics;

    @Schema(title = "组合描述")
    private String description;

    @Schema(title = "定价方式：1-固定价格 2-折扣比例")
    private Byte priceType;

    @Schema(title = "组合固定价格")
    private BigDecimal bundlePrice;

    @Schema(title = "折扣比例(0-100)")
    private BigDecimal discountRate;

    @Schema(title = "原价总和(计算值)")
    private BigDecimal originalPrice;

    @Schema(title = "上架状态：0-下架 1-上架")
    private Byte publishStatus;

    @Schema(title = "销售数量")
    private Integer saleCount;

    @Schema(title = "排序")
    private Integer sort;

    @Schema(title = "关联学校ID")
    private Long schoolId;

    private Date createTime;

    private Date updateTime;

    @Schema(title = "组合详情描述(富文本)")
    private String detailDesc;

    private static final long serialVersionUID = 1L;

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

    public String getAlbumPics() {
        return albumPics;
    }

    public void setAlbumPics(String albumPics) {
        this.albumPics = albumPics;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Byte getPriceType() {
        return priceType;
    }

    public void setPriceType(Byte priceType) {
        this.priceType = priceType;
    }

    public BigDecimal getBundlePrice() {
        return bundlePrice;
    }

    public void setBundlePrice(BigDecimal bundlePrice) {
        this.bundlePrice = bundlePrice;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Byte getPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(Byte publishStatus) {
        this.publishStatus = publishStatus;
    }

    public Integer getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(Integer saleCount) {
        this.saleCount = saleCount;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
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

    public String getDetailDesc() {
        return detailDesc;
    }

    public void setDetailDesc(String detailDesc) {
        this.detailDesc = detailDesc;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", pic=").append(pic);
        sb.append(", albumPics=").append(albumPics);
        sb.append(", description=").append(description);
        sb.append(", priceType=").append(priceType);
        sb.append(", bundlePrice=").append(bundlePrice);
        sb.append(", discountRate=").append(discountRate);
        sb.append(", originalPrice=").append(originalPrice);
        sb.append(", publishStatus=").append(publishStatus);
        sb.append(", saleCount=").append(saleCount);
        sb.append(", sort=").append(sort);
        sb.append(", schoolId=").append(schoolId);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", detailDesc=").append(detailDesc);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}