package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

public class UmsAdmin implements Serializable {
    private Long id;

    private String username;

    private String password;

    @Schema(title = "头像")
    private String icon;

    @Schema(title = "邮箱")
    private String email;

    @Schema(title = "昵称")
    private String nickName;

    @Schema(title = "备注信息")
    private String note;

    @Schema(title = "创建时间")
    private Date createTime;

    @Schema(title = "最后登录时间")
    private Date loginTime;

    @Schema(title = "帐号启用状态：0->禁用；1->启用")
    private Integer status;

    @Schema(title = "微信服务号OpenID")
    private String wxServiceOpenid;

    @Schema(title = "微信昵称")
    private String wxServiceNickname;

    @Schema(title = "微信头像URL")
    private String wxServiceHeadimg;

    @Schema(title = "微信绑定时间")
    private Date wxServiceBindtime;

    @Schema(title = "账户类型：0->管理账号(可查看所有门店)；1->门店账号(只能查看关联门店)")
    private Boolean adminType;

    @Schema(title = "关联门店ID(仅门店账号有效)")
    private Long storeId;

    @Schema(title = "关联门店名称(冗余字段)")
    private String storeName;

    @Schema(title = "关联学校ID(仅门店账号有效)")
    private Long schoolId;

    @Schema(title = "关联学校名称(冗余字段)")
    private String schoolName;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getWxServiceOpenid() {
        return wxServiceOpenid;
    }

    public void setWxServiceOpenid(String wxServiceOpenid) {
        this.wxServiceOpenid = wxServiceOpenid;
    }

    public String getWxServiceNickname() {
        return wxServiceNickname;
    }

    public void setWxServiceNickname(String wxServiceNickname) {
        this.wxServiceNickname = wxServiceNickname;
    }

    public String getWxServiceHeadimg() {
        return wxServiceHeadimg;
    }

    public void setWxServiceHeadimg(String wxServiceHeadimg) {
        this.wxServiceHeadimg = wxServiceHeadimg;
    }

    public Date getWxServiceBindtime() {
        return wxServiceBindtime;
    }

    public void setWxServiceBindtime(Date wxServiceBindtime) {
        this.wxServiceBindtime = wxServiceBindtime;
    }

    public Boolean getAdminType() {
        return adminType;
    }

    public void setAdminType(Boolean adminType) {
        this.adminType = adminType;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", username=").append(username);
        sb.append(", password=").append(password);
        sb.append(", icon=").append(icon);
        sb.append(", email=").append(email);
        sb.append(", nickName=").append(nickName);
        sb.append(", note=").append(note);
        sb.append(", createTime=").append(createTime);
        sb.append(", loginTime=").append(loginTime);
        sb.append(", status=").append(status);
        sb.append(", wxServiceOpenid=").append(wxServiceOpenid);
        sb.append(", wxServiceNickname=").append(wxServiceNickname);
        sb.append(", wxServiceHeadimg=").append(wxServiceHeadimg);
        sb.append(", wxServiceBindtime=").append(wxServiceBindtime);
        sb.append(", adminType=").append(adminType);
        sb.append(", storeId=").append(storeId);
        sb.append(", storeName=").append(storeName);
        sb.append(", schoolId=").append(schoolId);
        sb.append(", schoolName=").append(schoolName);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}