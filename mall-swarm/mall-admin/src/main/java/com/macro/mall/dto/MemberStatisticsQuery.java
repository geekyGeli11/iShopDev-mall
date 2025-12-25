package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;

/**
 * 会员数据统计查询参数
 * Created by macro on 2025/11/28.
 */
@Data
@Schema(description = "会员数据统计查询参数")
public class MemberStatisticsQuery {
    
    @Schema(description = "开始日期", required = true, example = "2025-01-01")
    @NotNull(message = "开始日期不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @Schema(description = "结束日期", required = true, example = "2025-01-31")
    @NotNull(message = "结束日期不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    @Schema(description = "学校ID（可选）", example = "1")
    private Long schoolId;
    
    @Schema(description = "门店ID（可选）", example = "1")
    private Long storeId;
    
    @Schema(description = "排行榜数量限制", example = "10")
    @Min(value = 1, message = "排行榜数量限制必须大于0")
    private Integer topLimit = 10;
}
