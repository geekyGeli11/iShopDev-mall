package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 访问页面数据VO
 * Created by macro on 2025/11/28.
 */
@Data
@Schema(description = "访问页面数据")
public class PageVisitVO {
    
    @Schema(description = "页面路径")
    private String pagePath;
    
    @Schema(description = "访问用户数")
    private Integer visitUv;
    
    @Schema(description = "访问次数")
    private Integer visitPv;
    
    @Schema(description = "平均停留时长（秒）")
    private Integer avgStayTime;
    
    @Schema(description = "跳出率（百分比）")
    private Double bounceRate;
}
