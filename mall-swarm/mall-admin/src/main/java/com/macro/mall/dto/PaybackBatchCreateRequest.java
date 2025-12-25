package com.macro.mall.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 补货批次创建请求
 */
@Data
@Schema(description = "补货批次创建请求")
public class PaybackBatchCreateRequest {

    @NotNull(message = "商品ID不能为空")
    @Schema(description = "商品ID", required = true)
    private Long productId;

    @NotNull(message = "补货数量不能为空")
    @Min(value = 1, message = "补货数量必须大于0")
    @Schema(description = "补货数量", required = true)
    private Integer replenishmentQuantity;

    @NotNull(message = "补货金额不能为空")
    @DecimalMin(value = "0.01", message = "补货金额必须大于0")
    @Schema(description = "补货金额（进货成本）", required = true)
    private BigDecimal replenishmentAmount;

    @NotNull(message = "回本目标金额不能为空")
    @DecimalMin(value = "0.01", message = "回本目标金额必须大于0")
    @Schema(description = "回本目标金额", required = true)
    private BigDecimal targetAmount;

    @Schema(description = "补货日期，默认当天")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date replenishmentDate;
}
