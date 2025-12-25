package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 渠道销售数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "渠道销售数据")
public class ChannelSalesData {

    @Schema(description = "渠道标识：miniprogram-小程序，selfcheck-自助售卖机，non_system-非系统销售")
    private String channel;

    @Schema(description = "渠道名称")
    private String channelName;

    @Schema(description = "销售数量")
    private Integer quantity;

    @Schema(description = "销售金额")
    private BigDecimal amount;

    /**
     * 根据渠道标识获取渠道名称
     */
    public static String getChannelName(String channel) {
        if (channel == null) return "未知";
        switch (channel) {
            case "miniprogram": return "小程序";
            case "selfcheck": return "自助售卖机";
            case "non_system": return "非系统销售";
            default: return "未知";
        }
    }
}
