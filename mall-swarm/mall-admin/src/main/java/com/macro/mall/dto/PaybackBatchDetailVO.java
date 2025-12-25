package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * 补货批次详情VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "补货批次详情VO")
public class PaybackBatchDetailVO extends PaybackBatchVO {

    @Schema(description = "销售日志列表")
    private List<PaybackSalesLogVO> salesLogs;

    @Schema(description = "各渠道销售汇总")
    private Map<String, ChannelSalesData> channelSummary;

    @Schema(description = "系统订单销售明细")
    private List<PaybackSalesOrderDTO> orderDetails;

    @Schema(description = "非系统销售明细")
    private List<PaybackNonSystemSaleDTO> nonSystemSaleDetails;
}
