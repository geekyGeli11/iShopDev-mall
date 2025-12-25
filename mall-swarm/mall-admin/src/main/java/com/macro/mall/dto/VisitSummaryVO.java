package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 访问概况数据VO
 * Created by macro on 2025/11/28.
 */
@Data
@Schema(description = "访问概况数据")
public class VisitSummaryVO {
    
    @Schema(description = "总访问用户数")
    private Integer totalVisitUv;
    
    @Schema(description = "总访问次数")
    private Integer totalVisitPv;
    
    @Schema(description = "新增用户数")
    private Integer newUserCount;
    
    @Schema(description = "平均访问深度")
    private Double avgVisitDepth;
    
    @Schema(description = "平均访问时长（秒）")
    private Integer avgVisitDuration;
    
    @Schema(description = "跳出率（百分比）")
    private Double bounceRate;
}
