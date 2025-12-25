package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 访问留存数据VO
 * Created by macro on 2025/11/28.
 */
@Data
@Schema(description = "访问留存数据")
public class VisitRetainVO {
    
    @Schema(description = "周期（如：第1周、第2周等）")
    private String period;
    
    @Schema(description = "新增用户数")
    private Integer newUser;
    
    @Schema(description = "留存用户数")
    private Integer retainUser;
    
    @Schema(description = "留存率（百分比）")
    private Double retainRate;
}
