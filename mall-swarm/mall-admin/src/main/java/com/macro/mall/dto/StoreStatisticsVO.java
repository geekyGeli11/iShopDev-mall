package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 门店营业数据统计VO
 * Created by macro on 2025/11/28.
 */
@Data
@Schema(description = "门店营业数据统计")
public class StoreStatisticsVO {
    
    @Schema(description = "门店ID")
    private Long storeId;
    
    @Schema(description = "门店名称")
    private String storeName;
    
    @Schema(description = "营业金额")
    private BigDecimal totalAmount;
    
    @Schema(description = "订单数")
    private Integer orderCount;
    
    @Schema(description = "平均订单金额")
    private BigDecimal avgOrderAmount;
}
