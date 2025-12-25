package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 出库单通知参数
 */
@Data
@Schema(description = "出库单通知参数")
public class StockOutNotificationParam {
    
    @Schema(description = "出库单号/调货单号")
    private String transferNo;
    
    @Schema(description = "操作时间")
    private Date auditTime;
    
    @Schema(description = "客户名称/门店名称")
    private String customerName;
    
    @Schema(description = "目标门店ID（被申请调货的门店）")
    private Long targetStoreId;
    
    @Schema(description = "申请门店ID")
    private Long applicantStoreId;
}
