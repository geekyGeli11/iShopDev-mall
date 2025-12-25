package com.macro.mall.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 补货批次更新请求
 */
@Data
@Schema(description = "补货批次更新请求")
public class PaybackBatchUpdateRequest {

    @Min(value = 1, message = "补货数量必须大于0")
    @Schema(description = "补货数量")
    private Integer replenishmentQuantity;

    @DecimalMin(value = "0.01", message = "补货金额必须大于0")
    @Schema(description = "补货金额（进货成本）")
    private BigDecimal replenishmentAmount;

    @DecimalMin(value = "0.01", message = "回本目标金额必须大于0")
    @Schema(description = "回本目标金额")
    private BigDecimal targetAmount;

    @Schema(description = "补货日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date replenishmentDate;
}
