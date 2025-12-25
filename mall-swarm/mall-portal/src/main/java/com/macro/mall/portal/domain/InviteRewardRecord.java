package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 邀请奖励记录
 */
@Data
public class InviteRewardRecord {
    
    @Schema(title = "奖励ID")
    private Long rewardId;
    
    @Schema(title = "奖励类型", description = "1-积分 2-余额 3-优惠券")
    private Integer rewardType;
    
    @Schema(title = "奖励数量")
    private BigDecimal rewardValue;
    
    @Schema(title = "奖励描述")
    private String rewardDesc;
    
    @Schema(title = "奖励状态", description = "0-未发放 1-已发放 2-发放失败")
    private Integer status;
    
    @Schema(title = "触发条件", description = "1-注册奖励 2-首单奖励 3-消费奖励")
    private Integer triggerCondition;
    
    @Schema(title = "被邀请者昵称")
    private String inviteeNickname;
    
    @Schema(title = "创建时间")
    private Date createTime;
    
    @Schema(title = "发放时间")
    private Date grantTime;
} 