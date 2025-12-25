package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 补货批次VO
 */
@Data
@Schema(description = "补货批次VO")
public class PaybackBatchVO {

    @Schema(description = "批次ID")
    private Long id;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "商品名称")
    private String productName;

    @Schema(description = "商品货号")
    private String productSn;

    @Schema(description = "商品图片")
    private String productPic;

    @Schema(description = "批次序号")
    private Integer batchNo;

    @Schema(description = "补货数量")
    private Integer replenishmentQuantity;

    @Schema(description = "补货金额（进货成本）")
    private BigDecimal replenishmentAmount;

    @Schema(description = "回本目标金额")
    private BigDecimal targetAmount;

    @Schema(description = "补货日期")
    private Date replenishmentDate;

    @Schema(description = "当前已售数量")
    private Integer currentSoldQuantity;

    @Schema(description = "当前已售金额")
    private BigDecimal currentSoldAmount;

    @Schema(description = "回本进度百分比")
    private BigDecimal paybackProgress;

    @Schema(description = "利润金额")
    private BigDecimal profitAmount;

    @Schema(description = "利润率百分比")
    private BigDecimal profitRate;

    @Schema(description = "批次状态：0-待启动，1-活跃，2-已回本，3-提前结束")
    private Integer batchStatus;

    @Schema(description = "批次状态文本")
    private String batchStatusText;

    @Schema(description = "开始统计日期")
    private Date startDate;

    @Schema(description = "回本完成日期")
    private Date completedDate;

    @Schema(description = "创建时间")
    private Date createdAt;

    @Schema(description = "更新时间")
    private Date updatedAt;

    /**
     * 获取批次状态文本
     */
    public String getBatchStatusText() {
        if (batchStatus == null) return "未知";
        switch (batchStatus) {
            case 0: return "待启动";
            case 1: return "活跃";
            case 2: return "已回本";
            case 3: return "提前结束";
            default: return "未知";
        }
    }
}
