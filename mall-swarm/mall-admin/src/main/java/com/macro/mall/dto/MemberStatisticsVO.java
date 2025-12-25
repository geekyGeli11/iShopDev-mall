package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 会员数据统计响应DTO
 * Created by macro on 2025/11/28.
 */
@Data
@Schema(description = "会员数据统计响应")
public class MemberStatisticsVO {
    
    @Schema(description = "新增会员数")
    private Integer newMemberCount;
    
    @Schema(description = "总活跃会员数")
    private Integer totalActiveMembers;
    
    @Schema(description = "会员增长率（百分比）")
    private BigDecimal growthRate;
    
    @Schema(description = "会员消费排行榜")
    private List<TopSpenderVO> topSpenders;
    
    @Schema(description = "会员增长趋势数据")
    private List<MemberTrendDataVO> memberTrendData;
    
    @Schema(description = "小程序访问数据")
    private WechatVisitDataVO wechatVisitData;
}
