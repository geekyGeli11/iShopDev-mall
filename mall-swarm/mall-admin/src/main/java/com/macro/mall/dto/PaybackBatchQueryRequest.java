package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 补货批次查询请求
 */
@Data
@Schema(description = "补货批次查询请求")
public class PaybackBatchQueryRequest {

    @Schema(description = "商品名称或货号关键词")
    private String keyword;

    @Schema(description = "批次状态：0-待启动，1-活跃，2-已回本，3-提前结束")
    private Integer batchStatus;

    @Schema(description = "补货开始日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @Schema(description = "补货结束日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "页码", defaultValue = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", defaultValue = "10")
    private Integer pageSize = 10;
}
