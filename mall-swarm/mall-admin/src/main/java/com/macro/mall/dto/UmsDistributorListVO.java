package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 分销商列表展示VO
 */
@Schema(description = "分销商列表展示对象")
public class UmsDistributorListVO {
    
    @Schema(description = "用户ID")
    private Long id;
    
    @Schema(description = "用户昵称")
    private String nickname;
    
    @Schema(description = "手机号")
    private String phone;
    
    @Schema(description = "会员码")
    private String memberCode;
    
    @Schema(description = "头像")
    private String icon;
    
    @Schema(description = "分销商状态：0-普通用户，1-分销商，2-暂停，3-禁用")
    private Byte isDistributor;
    
    @Schema(description = "分销商状态文本")
    private String distributorStatusText;
    
    @Schema(description = "分销商等级：0-普通，1-初级，2-中级，3-高级")
    private Byte distributorLevel;
    
    @Schema(description = "分销商等级文本")
    private String distributorLevelText;
    
    @Schema(description = "注册时间")
    private Date createTime;
    
    @Schema(description = "申请分销商时间")
    private Date applyTime;
    
    @Schema(description = "审核通过时间")
    private Date approvedTime;
    
    @Schema(description = "累计佣金")
    private BigDecimal totalCommission;
    
    @Schema(description = "一级客户数量")
    private Integer level1CustomerCount;
    
    @Schema(description = "二级客户数量")
    private Integer level2CustomerCount;
    
    @Schema(description = "总推广订单数")
    private Integer totalOrderCount;
    
    @Schema(description = "推广订单金额")
    private BigDecimal totalOrderAmount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Byte getIsDistributor() {
        return isDistributor;
    }

    public void setIsDistributor(Byte isDistributor) {
        this.isDistributor = isDistributor;
        // 设置状态文本
        if (isDistributor != null) {
            switch (isDistributor) {
                case 0:
                    this.distributorStatusText = "普通用户";
                    break;
                case 1:
                    this.distributorStatusText = "分销商";
                    break;
                case 2:
                    this.distributorStatusText = "暂停";
                    break;
                case 3:
                    this.distributorStatusText = "禁用";
                    break;
                default:
                    this.distributorStatusText = "未知状态";
            }
        }
    }

    public String getDistributorStatusText() {
        return distributorStatusText;
    }

    public void setDistributorStatusText(String distributorStatusText) {
        this.distributorStatusText = distributorStatusText;
    }

    public Byte getDistributorLevel() {
        return distributorLevel;
    }

    public void setDistributorLevel(Byte distributorLevel) {
        this.distributorLevel = distributorLevel;
        // 设置等级文本
        if (distributorLevel != null) {
            switch (distributorLevel) {
                case 0:
                    this.distributorLevelText = "普通";
                    break;
                case 1:
                    this.distributorLevelText = "初级";
                    break;
                case 2:
                    this.distributorLevelText = "中级";
                    break;
                case 3:
                    this.distributorLevelText = "高级";
                    break;
                default:
                    this.distributorLevelText = "未知等级";
            }
        }
    }

    public String getDistributorLevelText() {
        return distributorLevelText;
    }

    public void setDistributorLevelText(String distributorLevelText) {
        this.distributorLevelText = distributorLevelText;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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

    public Integer getLevel1CustomerCount() {
        return level1CustomerCount;
    }

    public void setLevel1CustomerCount(Integer level1CustomerCount) {
        this.level1CustomerCount = level1CustomerCount;
    }

    public Integer getLevel2CustomerCount() {
        return level2CustomerCount;
    }

    public void setLevel2CustomerCount(Integer level2CustomerCount) {
        this.level2CustomerCount = level2CustomerCount;
    }

    public Integer getTotalOrderCount() {
        return totalOrderCount;
    }

    public void setTotalOrderCount(Integer totalOrderCount) {
        this.totalOrderCount = totalOrderCount;
    }

    public BigDecimal getTotalOrderAmount() {
        return totalOrderAmount;
    }

    public void setTotalOrderAmount(BigDecimal totalOrderAmount) {
        this.totalOrderAmount = totalOrderAmount;
    }
} 