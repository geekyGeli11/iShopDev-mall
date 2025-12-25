package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 回本分析销售日志VO
 */
@Data
@Schema(description = "回本分析销售日志VO")
public class PaybackSalesLogVO {

    @Schema(description = "日志ID")
    private Long id;

    @Schema(description = "批次ID")
    private Long batchId;

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "订单编号")
    private String orderSn;

    @Schema(description = "销售渠道：miniprogram-小程序，selfcheck-自助售卖机，non_system-非系统销售")
    private String salesChannel;

    @Schema(description = "销售渠道名称")
    private String salesChannelName;

    @Schema(description = "销售数量")
    private Integer soldQuantity;

    @Schema(description = "销售金额")
    private BigDecimal soldAmount;

    @Schema(description = "订单日期")
    private Date orderDate;

    @Schema(description = "创建时间")
    private Date createdAt;

    /**
     * 获取销售渠道名称
     */
    public String getSalesChannelName() {
        return ChannelSalesData.getChannelName(salesChannel);
    }
}
