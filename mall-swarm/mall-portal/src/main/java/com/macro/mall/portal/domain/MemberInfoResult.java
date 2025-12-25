package com.macro.mall.portal.domain;

import com.macro.mall.model.UmsMember;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 会员信息响应结果（包含扩展信息）
 * Created by macro on 2025/09/28.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "会员信息响应结果")
public class MemberInfoResult extends UmsMember {
    
    @Schema(description = "距离下一等级所需剩余积分")
    private Integer remainingPointsToNextLevel;
    
    @Schema(description = "当前等级名称")
    private String currentLevelName;
    
    @Schema(description = "下一等级名称")
    private String nextLevelName;
    
    @Schema(description = "下一等级所需总积分")
    private Integer nextLevelRequiredPoints;
    
    @Schema(description = "是否已达到最高等级")
    private Boolean isMaxLevel;
    
    /**
     * 从UmsMember创建MemberInfoResult
     */
    public static MemberInfoResult fromUmsMember(UmsMember member) {
        if (member == null) {
            return null;
        }

        MemberInfoResult result = new MemberInfoResult();

        // 复制基础字段（只复制实际存在的字段）
        result.setId(member.getId());
        result.setMemberLevelId(member.getMemberLevelId());
        result.setUsername(member.getUsername());
        result.setPassword(member.getPassword());
        result.setNickname(member.getNickname());
        result.setPhone(member.getPhone());
        result.setStatus(member.getStatus());
        result.setCreateTime(member.getCreateTime());
        result.setIcon(member.getIcon());
        result.setGender(member.getGender());
        result.setBirthday(member.getBirthday());
        result.setCity(member.getCity());
        result.setJob(member.getJob());
        result.setPersonalizedSignature(member.getPersonalizedSignature());
        result.setSourceType(member.getSourceType());
        result.setIntegration(member.getIntegration());
        result.setGrowth(member.getGrowth());
        result.setLuckeyCount(member.getLuckeyCount());
        result.setHistoryIntegration(member.getHistoryIntegration());
        result.setOpenid(member.getOpenid());
        result.setBalance(member.getBalance());
        result.setMemberCode(member.getMemberCode());
        result.setSchoolId(member.getSchoolId());
        result.setIsDistributor(member.getIsDistributor());
        result.setDistributorLevel(member.getDistributorLevel());
        result.setApplyTime(member.getApplyTime());
        result.setApprovedTime(member.getApprovedTime());
        result.setTotalCommission(member.getTotalCommission());

        return result;
    }
}
