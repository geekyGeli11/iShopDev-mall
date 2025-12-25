package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 邀请统计结果
 */
@Data
public class InviteStatisticsResult {
    
    @Schema(title = "邀请总人数")
    private Integer totalInvites;
    
    @Schema(title = "今日邀请人数")
    private Integer todayInvites;
    
    @Schema(title = "已注册人数")
    private Integer registeredCount;
    
    @Schema(title = "已下单人数")
    private Integer orderedCount;
    
    @Schema(title = "累计奖励积分")
    private Integer totalRewardPoints;
    
    @Schema(title = "累计奖励金额")
    private BigDecimal totalRewardAmount;
    
    @Schema(title = "待领取奖励数")
    private Integer pendingRewards;
    
    @Schema(title = "邀请转化率")
    private BigDecimal conversionRate;
    
    @Schema(title = "排行榜排名")
    private Integer ranking;
    
    @Schema(title = "当前总收益")
    private BigDecimal currentTotalIncome;
    
    @Schema(title = "可提现金额")
    private BigDecimal withdrawableAmount;
    
    @Schema(title = "未入账金额")
    private BigDecimal pendingAmount;
    
    @Schema(title = "被邀请人列表")
    private List<InvitedUserInfo> invitedUsers;
    
    /**
     * 被邀请人信息
     */
    @Data
    public static class InvitedUserInfo {
        @Schema(title = "用户ID")
        private Long userId;
        
        @Schema(title = "用户昵称")
        private String nickname;
        
        @Schema(title = "用户头像")
        private String avatar;
        
        @Schema(title = "状态：0-已邀请，1-已注册，2-已下单")
        private Integer status;
        
        @Schema(title = "邀请时间")
        private Date createTime;
    }
} 