package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class UmsMember implements Serializable {
    private Long id;

    private Long memberLevelId;

    @Schema(title = "会员等级名称")
    private String memberLevelName;

    @Schema(title = "用户名")
    private String username;

    @Schema(title = "密码")
    private String password;

    @Schema(title = "昵称")
    private String nickname;

    @Schema(title = "手机号码")
    private String phone;

    @Schema(title = "会员码（M+yyMMdd+5位随机数）")
    private String memberCode;

    @Schema(title = "帐号启用状态:0->禁用；1->启用")
    private Integer status;

    @Schema(title = "注册时间")
    private Date createTime;

    @Schema(title = "头像")
    private String icon;

    @Schema(title = "性别：0->未知；1->男；2->女")
    private Integer gender;

    @Schema(title = "生日")
    private Date birthday;

    @Schema(title = "所做城市")
    private String city;

    @Schema(title = "职业")
    private String job;

    @Schema(title = "个性签名")
    private String personalizedSignature;

    @Schema(title = "用户来源:1-小程序")
    private Integer sourceType;

    @Schema(title = "积分")
    private Integer integration;

    @Schema(title = "用户余额")
    private BigDecimal balance;

    @Schema(title = "成长值")
    private Integer growth;

    @Schema(title = "剩余抽奖次数")
    private Integer luckeyCount;

    @Schema(title = "历史积分数量")
    private Integer historyIntegration;

    @Schema(title = "微信用户 openid")
    private String openid;

    @Schema(title = "绑定学校ID")
    private Long schoolId;

    @Schema(title = "是否分销商：0-否，1-是")
    private Byte isDistributor;

    @Schema(title = "分销商等级：0-普通，1-初级，2-中级，3-高级")
    private Byte distributorLevel;

    @Schema(title = "申请分销商时间")
    private Date applyTime;

    @Schema(title = "审核通过时间")
    private Date approvedTime;

    @Schema(title = "累计佣金（冗余字段，便于快速查询）")
    private BigDecimal totalCommission;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberLevelId() {
        return memberLevelId;
    }

    public void setMemberLevelId(Long memberLevelId) {
        this.memberLevelId = memberLevelId;
    }

    public String getMemberLevelName() {
        return memberLevelName;
    }

    public void setMemberLevelName(String memberLevelName) {
        this.memberLevelName = memberLevelName;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMemberCode() {
        return memberCode;
    }

    public void setMemberCode(String memberCode) {
        this.memberCode = memberCode;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getPersonalizedSignature() {
        return personalizedSignature;
    }

    public void setPersonalizedSignature(String personalizedSignature) {
        this.personalizedSignature = personalizedSignature;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public Integer getIntegration() {
        return integration;
    }

    public void setIntegration(Integer integration) {
        this.integration = integration;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Integer getGrowth() {
        return growth;
    }

    public void setGrowth(Integer growth) {
        this.growth = growth;
    }

    public Integer getLuckeyCount() {
        return luckeyCount;
    }

    public void setLuckeyCount(Integer luckeyCount) {
        this.luckeyCount = luckeyCount;
    }

    public Integer getHistoryIntegration() {
        return historyIntegration;
    }

    public void setHistoryIntegration(Integer historyIntegration) {
        this.historyIntegration = historyIntegration;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    public Byte getIsDistributor() {
        return isDistributor;
    }

    public void setIsDistributor(Byte isDistributor) {
        this.isDistributor = isDistributor;
    }

    public Byte getDistributorLevel() {
        return distributorLevel;
    }

    public void setDistributorLevel(Byte distributorLevel) {
        this.distributorLevel = distributorLevel;
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public Date getApprovedTime() {
        return approvedTime;
    }

    public void setApprovedTime(Date approvedTime) {
        this.approvedTime = approvedTime;
    }

    public BigDecimal getTotalCommission() {
        return totalCommission;
    }

    public void setTotalCommission(BigDecimal totalCommission) {
        this.totalCommission = totalCommission;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", memberLevelId=").append(memberLevelId);
        sb.append(", memberLevelName=").append(memberLevelName);
        sb.append(", username=").append(username);
        sb.append(", password=").append(password);
        sb.append(", nickname=").append(nickname);
        sb.append(", phone=").append(phone);
        sb.append(", memberCode=").append(memberCode);
        sb.append(", status=").append(status);
        sb.append(", createTime=").append(createTime);
        sb.append(", icon=").append(icon);
        sb.append(", gender=").append(gender);
        sb.append(", birthday=").append(birthday);
        sb.append(", city=").append(city);
        sb.append(", job=").append(job);
        sb.append(", personalizedSignature=").append(personalizedSignature);
        sb.append(", sourceType=").append(sourceType);
        sb.append(", integration=").append(integration);
        sb.append(", balance=").append(balance);
        sb.append(", growth=").append(growth);
        sb.append(", luckeyCount=").append(luckeyCount);
        sb.append(", historyIntegration=").append(historyIntegration);
        sb.append(", openid=").append(openid);
        sb.append(", schoolId=").append(schoolId);
        sb.append(", isDistributor=").append(isDistributor);
        sb.append(", distributorLevel=").append(distributorLevel);
        sb.append(", applyTime=").append(applyTime);
        sb.append(", approvedTime=").append(approvedTime);
        sb.append(", totalCommission=").append(totalCommission);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}