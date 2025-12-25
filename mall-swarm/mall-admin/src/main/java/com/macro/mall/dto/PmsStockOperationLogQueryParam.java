package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 库存操作记录查询参数
 * Created by macro on 2024-01-20.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PmsStockOperationLogQueryParam {
    @Schema(title = "页码")
    private Integer pageNum;
    
    @Schema(title = "每页大小")
    private Integer pageSize;
    
    @Schema(title = "商品ID")
    private Long productId;
    
    @Schema(title = "关键词（商品名称/货号）")
    private String keyword;
    
    @Schema(title = "操作类型")
    private String operationType;
    
    @Schema(title = "操作人ID")
    private Long operatorId;
    
    @Schema(title = "操作人姓名")
    private String operatorName;
    
    @Schema(title = "开始时间")
    private String startTime;
    
    @Schema(title = "结束时间")
    private String endTime;
    
    @Schema(title = "门店ID（用于筛选特定门店的操作记录）")
    private Long storeId;
} 