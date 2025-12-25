package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;

public class VStoreSchoolInfo implements Serializable {
    @Schema(title = "主键ID")
    private Long storeId;

    @Schema(title = "门店名称")
    private String storeName;

    @Schema(title = "电话号码")
    private String storePhone;

    @Schema(title = "省份")
    private String storeProvince;

    @Schema(title = "城市")
    private String storeCity;

    @Schema(title = "区域")
    private String storeRegion;

    @Schema(title = "详细地址")
    private String storeAddress;

    @Schema(title = "营业时间")
    private String businessHours;

    @Schema(title = "经度")
    private BigDecimal longitude;

    @Schema(title = "纬度")
    private BigDecimal latitude;

    @Schema(title = "门店logo图片URL")
    private String storeLogo;

    @Schema(title = "学校ID")
    private Long schoolId;

    @Schema(title = "学校名称")
    private String schoolName;

    @Schema(title = "学校校徽logo URL")
    private String schoolLogo;

    @Schema(title = "状态：0-禁用，1-启用")
    private Boolean schoolStatus;

    private static final long serialVersionUID = 1L;

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

    public String getStorePhone() {
        return storePhone;
    }

    public void setStorePhone(String storePhone) {
        this.storePhone = storePhone;
    }

    public String getStoreProvince() {
        return storeProvince;
    }

    public void setStoreProvince(String storeProvince) {
        this.storeProvince = storeProvince;
    }

    public String getStoreCity() {
        return storeCity;
    }

    public void setStoreCity(String storeCity) {
        this.storeCity = storeCity;
    }

    public String getStoreRegion() {
        return storeRegion;
    }

    public void setStoreRegion(String storeRegion) {
        this.storeRegion = storeRegion;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public String getBusinessHours() {
        return businessHours;
    }

    public void setBusinessHours(String businessHours) {
        this.businessHours = businessHours;
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

    public String getStoreLogo() {
        return storeLogo;
    }

    public void setStoreLogo(String storeLogo) {
        this.storeLogo = storeLogo;
    }

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolLogo() {
        return schoolLogo;
    }

    public void setSchoolLogo(String schoolLogo) {
        this.schoolLogo = schoolLogo;
    }

    public Boolean getSchoolStatus() {
        return schoolStatus;
    }

    public void setSchoolStatus(Boolean schoolStatus) {
        this.schoolStatus = schoolStatus;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", storeId=").append(storeId);
        sb.append(", storeName=").append(storeName);
        sb.append(", storePhone=").append(storePhone);
        sb.append(", storeProvince=").append(storeProvince);
        sb.append(", storeCity=").append(storeCity);
        sb.append(", storeRegion=").append(storeRegion);
        sb.append(", storeAddress=").append(storeAddress);
        sb.append(", businessHours=").append(businessHours);
        sb.append(", longitude=").append(longitude);
        sb.append(", latitude=").append(latitude);
        sb.append(", storeLogo=").append(storeLogo);
        sb.append(", schoolId=").append(schoolId);
        sb.append(", schoolName=").append(schoolName);
        sb.append(", schoolLogo=").append(schoolLogo);
        sb.append(", schoolStatus=").append(schoolStatus);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}