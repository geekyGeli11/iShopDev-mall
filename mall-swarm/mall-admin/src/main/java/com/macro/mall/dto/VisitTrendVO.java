package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 访问趋势数据VO
 * Created by macro on 2025/11/28.
 */
@Data
@Schema(description = "访问趋势数据")
public class VisitTrendVO {
    
    @Schema(description = "日期")
    private String date;
    
    @Schema(description = "访问用户数")
    private Integer visitUv;
    
    @Schema(description = "访问次数")
    private Integer visitPv;
    
    @Schema(description = "新增用户数")
    private Integer newUser;
}
