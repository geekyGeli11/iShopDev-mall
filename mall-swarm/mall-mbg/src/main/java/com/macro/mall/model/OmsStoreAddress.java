package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OmsStoreAddress implements Serializable {
    @Schema(title = "主键ID")
    private Long id;

    @Schema(title = "门店名称")
    private String addressName;

    @Schema(title = "联系人")
    private String name;

    @Schema(title = "电话号码")
    private String phone;

    @Schema(title = "省份")
    private String province;

    @Schema(title = "城市")
    private String city;

    @Schema(title = "区域")
    private String region;

    @Schema(title = "详细地址")
    private String detailAddress;

    @Schema(title = "营业时间")
    private String businessHours;

    @Schema(title = "是否默认地址：0->否；1->是")
    private Boolean isDefault;

    @Schema(title = "创建时间")
    private Date createTime;

    @Schema(title = "更新时间")
    private Date updateTime;

    @Schema(title = "经度")
    private BigDecimal longitude;

    @Schema(title = "纬度")
    private BigDecimal latitude;

    @Schema(title = "门店logo图片URL")
    private String logo;

    @Schema(title = "所属学校ID")
    private Long schoolId;

    @Schema(title = "是否为地库：0-普通门店，1-地库")
    private Boolean isWarehouse;

    @Schema(title = "门店类型：STORE-门店，WAREHOUSE-地库，CENTRAL_WAREHOUSE-中央仓库")
    private String storeType;

    @Schema(title = "门店相册图片URL列表")
    private String storeGallery;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public String getBusinessHours() {
        return businessHours;
    }

    public void setBusinessHours(String businessHours) {
        this.businessHours = businessHours;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
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

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    public Boolean getIsWarehouse() {
        return isWarehouse;
    }

    public void setIsWarehouse(Boolean isWarehouse) {
        this.isWarehouse = isWarehouse;
    }

    public String getStoreType() {
        return storeType;
    }

    public void setStoreType(String storeType) {
        this.storeType = storeType;
    }

    public String getStoreGallery() {
        return storeGallery;
    }

    public void setStoreGallery(String storeGallery) {
        this.storeGallery = storeGallery;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", addressName=").append(addressName);
        sb.append(", name=").append(name);
        sb.append(", phone=").append(phone);
        sb.append(", province=").append(province);
        sb.append(", city=").append(city);
        sb.append(", region=").append(region);
        sb.append(", detailAddress=").append(detailAddress);
        sb.append(", businessHours=").append(businessHours);
        sb.append(", isDefault=").append(isDefault);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", longitude=").append(longitude);
        sb.append(", latitude=").append(latitude);
        sb.append(", logo=").append(logo);
        sb.append(", schoolId=").append(schoolId);
        sb.append(", isWarehouse=").append(isWarehouse);
        sb.append(", storeType=").append(storeType);
        sb.append(", storeGallery=").append(storeGallery);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}