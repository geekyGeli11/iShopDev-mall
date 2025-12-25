package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 批次销售统计数据
 */
@Data
@Schema(description = "批次销售统计数据")
public class BatchSalesStatistics {

    @Schema(description = "总销售数量")
    private Integer totalQuantity = 0;

    @Schema(description = "总销售金额")
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Schema(description = "利润金额")
    private BigDecimal profitAmount = BigDecimal.ZERO;

    @Schema(description = "利润率")
    private BigDecimal profitRate = BigDecimal.ZERO;

    @Schema(description = "回本进度")
    private BigDecimal paybackProgress = BigDecimal.ZERO;

    @Schema(description = "各渠道销售数据")
    private Map<String, ChannelSalesData> channelData = new HashMap<>();

    /**
     * 添加渠道销售数据
     */
    public void addChannelData(String channel, Integer quantity, BigDecimal amount) {
        ChannelSalesData data = channelData.getOrDefault(channel, 
                new ChannelSalesData(channel, ChannelSalesData.getChannelName(channel), 0, BigDecimal.ZERO));
        data.setQuantity(data.getQuantity() + quantity);
        data.setAmount(data.getAmount().add(amount));
        channelData.put(channel, data);
        
        // 更新总计
        this.totalQuantity += quantity;
        this.totalAmount = this.totalAmount.add(amount);
    }

    /**
     * 计算利润和进度
     * @param replenishmentAmount 补货金额
     * @param targetAmount 回本目标金额
     */
    public void calculateProfitAndProgress(BigDecimal replenishmentAmount, BigDecimal targetAmount) {
        // 计算利润金额
        this.profitAmount = this.totalAmount.subtract(replenishmentAmount);
        
        // 计算利润率
        if (replenishmentAmount.compareTo(BigDecimal.ZERO) > 0) {
            this.profitRate = this.profitAmount
                    .divide(replenishmentAmount, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100"));
        }
        
        // 计算回本进度
        if (targetAmount.compareTo(BigDecimal.ZERO) > 0) {
            this.paybackProgress = this.totalAmount
                    .divide(targetAmount, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100"));
            // 最大100%
            if (this.paybackProgress.compareTo(new BigDecimal("100")) > 0) {
                this.paybackProgress = new BigDecimal("100");
            }
        }
    }
}
