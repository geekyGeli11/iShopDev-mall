package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 回本分析统计VO
 */
@Data
@Schema(description = "回本分析统计VO")
public class PaybackStatisticsVO {

    @Schema(description = "批次总数")
    private Integer totalBatchCount;

    @Schema(description = "活跃批次数")
    private Integer activeBatchCount;

    @Schema(description = "已回本批次数")
    private Integer completedBatchCount;

    @Schema(description = "待启动批次数")
    private Integer pendingBatchCount;

    @Schema(description = "提前结束批次数")
    private Integer earlyEndBatchCount;

    @Schema(description = "总销售金额")
    private BigDecimal totalSalesAmount;

    @Schema(description = "总利润金额")
    private BigDecimal totalProfitAmount;

    @Schema(description = "平均利润率")
    private BigDecimal avgProfitRate;

    @Schema(description = "总补货金额")
    private BigDecimal totalReplenishmentAmount;
}
