package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class PmsProductDamageReport implements Serializable {
    @Schema(title = "涓婚敭ID")
    private Long id;

    @Schema(title = "鎶ユ崯鍗曞彿")
    private String reportSn;

    @Schema(title = "鎶ユ崯闂ㄥ簵ID")
    private Long storeId;

    @Schema(title = "鎶ユ崯闂ㄥ簵鍚嶇О")
    private String storeName;

    @Schema(title = "鎶ユ崯鎬绘暟閲")
    private Integer totalQuantity;

    @Schema(title = "鎶ユ崯鎬婚噾棰濓紙鎸夋垚鏈?环璁＄畻锛")
    private BigDecimal totalDamageAmount;

    @Schema(title = "閿?敭鎬婚噾棰濓紙鎸夐攢鍞?环璁＄畻锛")
    private BigDecimal totalSalesAmount;

    @Schema(title = "鎶ユ崯绫诲瀷锛?-鍒拌揣鐟曠柕锛?-澶栧?鎹熷潖锛?-淇濆瓨涓嶅綋锛?-浜轰负鍘熷洜锛?-鍏朵粬")
    private Byte damageType;

    @Schema(title = "鎶ユ崯鍘熷洜ID")
    private Long damageReasonId;

    @Schema(title = "鎶ユ崯鍘熷洜锛堝啑浣欏瓨鍌?級")
    private String damageReason;

    @Schema(title = "鐟曠柕鍥剧墖锛堝?寮犵敤閫楀彿鍒嗛殧锛")
    private String damagePics;

    @Schema(title = "澶勭悊鏂瑰紡锛?-閫?洖鍘傚?锛?-鍘傚?鎹㈣揣锛?-鍐呴儴娑堝寲锛?-鎶ュ簾澶勭悊锛?-鎶樹环閿?敭锛?-鍏朵粬")
    private Byte handleMethod;

    @Schema(title = "澶勭悊鍑?瘉鍥剧墖锛堝?寮犵敤閫楀彿鍒嗛殧锛")
    private String handlePics;

    @Schema(title = "渚涘簲鍟?鍘傚?鍚嶇О")
    private String supplierName;

    @Schema(title = "鍘傚?鑱旂郴鏂瑰紡")
    private String supplierContact;

    @Schema(title = "閲嶆柊鍙戣揣鍗曞彿")
    private String reshipmentSn;

    @Schema(title = "閲嶆柊鍙戣揣鏃堕棿")
    private Date reshipmentTime;

    @Schema(title = "楠屾敹鐘舵?锛?-寰呴獙鏀讹紝1-楠屾敹閫氳繃锛?-楠屾敹涓嶉?杩")
    private Byte acceptanceStatus;

    @Schema(title = "楠屾敹鏃堕棿")
    private Date acceptanceTime;

    @Schema(title = "楠屾敹澶囨敞")
    private String acceptanceRemark;

    @Schema(title = "澶栧?鐢ㄩ?")
    private String borrowPurpose;

    @Schema(title = "鍊熺敤浜")
    private String borrowPerson;

    @Schema(title = "鍊熷嚭鏃堕棿")
    private Date borrowTime;

    @Schema(title = "褰掕繕鏃堕棿")
    private Date returnTime;

    @Schema(title = "澶勭悊鐘舵?锛?-寰呭?鐞嗭紝1-澶勭悊涓?紝2-寰呴獙鏀讹紝3-宸插畬鎴愶紝4-宸插叧闂")
    private Byte status;

    @Schema(title = "鎻愪氦浜篒D")
    private Long submitAdminId;

    @Schema(title = "鎻愪氦浜哄?鍚")
    private String submitAdminName;

    @Schema(title = "澶勭悊浜篒D")
    private Long handleAdminId;

    @Schema(title = "澶勭悊浜哄?鍚")
    private String handleAdminName;

    @Schema(title = "澶勭悊鏃堕棿")
    private Date handleTime;

    @Schema(title = "瀹屾垚鏃堕棿")
    private Date completeTime;

    @Schema(title = "澶囨敞")
    private String remark;

    @Schema(title = "鍒涘缓鏃堕棿")
    private Date createTime;

    @Schema(title = "鏇存柊鏃堕棿")
    private Date updateTime;

    @Schema(title = "鍒犻櫎鐘舵?锛?-鏈?垹闄わ紝1-宸插垹闄")
    private Byte deleteStatus;

    @Schema(title = "鎶ユ崯璇︾粏鎻忚堪")
    private String damageDescription;

    @Schema(title = "澶勭悊鏂规?鎻忚堪")
    private String handleDescription;

    @Schema(title = "鍘傚?鍙嶉?")
    private String supplierFeedback;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReportSn() {
        return reportSn;
    }

    public void setReportSn(String reportSn) {
        this.reportSn = reportSn;
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

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public BigDecimal getTotalDamageAmount() {
        return totalDamageAmount;
    }

    public void setTotalDamageAmount(BigDecimal totalDamageAmount) {
        this.totalDamageAmount = totalDamageAmount;
    }

    public BigDecimal getTotalSalesAmount() {
        return totalSalesAmount;
    }

    public void setTotalSalesAmount(BigDecimal totalSalesAmount) {
        this.totalSalesAmount = totalSalesAmount;
    }

    public Byte getDamageType() {
        return damageType;
    }

    public void setDamageType(Byte damageType) {
        this.damageType = damageType;
    }

    public Long getDamageReasonId() {
        return damageReasonId;
    }

    public void setDamageReasonId(Long damageReasonId) {
        this.damageReasonId = damageReasonId;
    }

    public String getDamageReason() {
        return damageReason;
    }

    public void setDamageReason(String damageReason) {
        this.damageReason = damageReason;
    }

    public String getDamagePics() {
        return damagePics;
    }

    public void setDamagePics(String damagePics) {
        this.damagePics = damagePics;
    }

    public Byte getHandleMethod() {
        return handleMethod;
    }

    public void setHandleMethod(Byte handleMethod) {
        this.handleMethod = handleMethod;
    }

    public String getHandlePics() {
        return handlePics;
    }

    public void setHandlePics(String handlePics) {
        this.handlePics = handlePics;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierContact() {
        return supplierContact;
    }

    public void setSupplierContact(String supplierContact) {
        this.supplierContact = supplierContact;
    }

    public String getReshipmentSn() {
        return reshipmentSn;
    }

    public void setReshipmentSn(String reshipmentSn) {
        this.reshipmentSn = reshipmentSn;
    }

    public Date getReshipmentTime() {
        return reshipmentTime;
    }

    public void setReshipmentTime(Date reshipmentTime) {
        this.reshipmentTime = reshipmentTime;
    }

    public Byte getAcceptanceStatus() {
        return acceptanceStatus;
    }

    public void setAcceptanceStatus(Byte acceptanceStatus) {
        this.acceptanceStatus = acceptanceStatus;
    }

    public Date getAcceptanceTime() {
        return acceptanceTime;
    }

    public void setAcceptanceTime(Date acceptanceTime) {
        this.acceptanceTime = acceptanceTime;
    }

    public String getAcceptanceRemark() {
        return acceptanceRemark;
    }

    public void setAcceptanceRemark(String acceptanceRemark) {
        this.acceptanceRemark = acceptanceRemark;
    }

    public String getBorrowPurpose() {
        return borrowPurpose;
    }

    public void setBorrowPurpose(String borrowPurpose) {
        this.borrowPurpose = borrowPurpose;
    }

    public String getBorrowPerson() {
        return borrowPerson;
    }

    public void setBorrowPerson(String borrowPerson) {
        this.borrowPerson = borrowPerson;
    }

    public Date getBorrowTime() {
        return borrowTime;
    }

    public void setBorrowTime(Date borrowTime) {
        this.borrowTime = borrowTime;
    }

    public Date getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(Date returnTime) {
        this.returnTime = returnTime;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Long getSubmitAdminId() {
        return submitAdminId;
    }

    public void setSubmitAdminId(Long submitAdminId) {
        this.submitAdminId = submitAdminId;
    }

    public String getSubmitAdminName() {
        return submitAdminName;
    }

    public void setSubmitAdminName(String submitAdminName) {
        this.submitAdminName = submitAdminName;
    }

    public Long getHandleAdminId() {
        return handleAdminId;
    }

    public void setHandleAdminId(Long handleAdminId) {
        this.handleAdminId = handleAdminId;
    }

    public String getHandleAdminName() {
        return handleAdminName;
    }

    public void setHandleAdminName(String handleAdminName) {
        this.handleAdminName = handleAdminName;
    }

    public Date getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(Date handleTime) {
        this.handleTime = handleTime;
    }

    public Date getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Date completeTime) {
        this.completeTime = completeTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public Byte getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(Byte deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

    public String getDamageDescription() {
        return damageDescription;
    }

    public void setDamageDescription(String damageDescription) {
        this.damageDescription = damageDescription;
    }

    public String getHandleDescription() {
        return handleDescription;
    }

    public void setHandleDescription(String handleDescription) {
        this.handleDescription = handleDescription;
    }

    public String getSupplierFeedback() {
        return supplierFeedback;
    }

    public void setSupplierFeedback(String supplierFeedback) {
        this.supplierFeedback = supplierFeedback;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", reportSn=").append(reportSn);
        sb.append(", storeId=").append(storeId);
        sb.append(", storeName=").append(storeName);
        sb.append(", totalQuantity=").append(totalQuantity);
        sb.append(", totalDamageAmount=").append(totalDamageAmount);
        sb.append(", totalSalesAmount=").append(totalSalesAmount);
        sb.append(", damageType=").append(damageType);
        sb.append(", damageReasonId=").append(damageReasonId);
        sb.append(", damageReason=").append(damageReason);
        sb.append(", damagePics=").append(damagePics);
        sb.append(", handleMethod=").append(handleMethod);
        sb.append(", handlePics=").append(handlePics);
        sb.append(", supplierName=").append(supplierName);
        sb.append(", supplierContact=").append(supplierContact);
        sb.append(", reshipmentSn=").append(reshipmentSn);
        sb.append(", reshipmentTime=").append(reshipmentTime);
        sb.append(", acceptanceStatus=").append(acceptanceStatus);
        sb.append(", acceptanceTime=").append(acceptanceTime);
        sb.append(", acceptanceRemark=").append(acceptanceRemark);
        sb.append(", borrowPurpose=").append(borrowPurpose);
        sb.append(", borrowPerson=").append(borrowPerson);
        sb.append(", borrowTime=").append(borrowTime);
        sb.append(", returnTime=").append(returnTime);
        sb.append(", status=").append(status);
        sb.append(", submitAdminId=").append(submitAdminId);
        sb.append(", submitAdminName=").append(submitAdminName);
        sb.append(", handleAdminId=").append(handleAdminId);
        sb.append(", handleAdminName=").append(handleAdminName);
        sb.append(", handleTime=").append(handleTime);
        sb.append(", completeTime=").append(completeTime);
        sb.append(", remark=").append(remark);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", deleteStatus=").append(deleteStatus);
        sb.append(", damageDescription=").append(damageDescription);
        sb.append(", handleDescription=").append(handleDescription);
        sb.append(", supplierFeedback=").append(supplierFeedback);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}