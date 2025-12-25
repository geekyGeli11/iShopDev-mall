package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 会员增长趋势数据DTO
 * Created by macro on 2025/11/28.
 */
@Data
@Schema(description = "会员增长趋势数据")
public class MemberTrendDataVO {
    
    @Schema(description = "日期")
    private String date;
    
    @Schema(description = "新增会员数")
    private Integer newMembers;
    
    @Schema(description = "活跃会员数")
    private Integer activeMembers;
}
