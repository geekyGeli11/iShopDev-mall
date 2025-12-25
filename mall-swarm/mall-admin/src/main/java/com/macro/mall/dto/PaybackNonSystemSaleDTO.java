package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 回本分析非系统销售详情 DTO
 */
@Data
@Schema(description = "回本分析非系统销售详情")
public class PaybackNonSystemSaleDTO {

    @Schema(description = "销售单ID")
    private Long saleId;

    @Schema(description = "销售单号")
    private String saleNo;

    @Schema(description = "销售类型名称")
    private String saleTypeName;

    @Schema(description = "商品数量")
    private Integer quantity;

    @Schema(description = "销售金额")
    private BigDecimal amount;

    @Schema(description = "销售日期")
    private Date saleDate;

    @Schema(description = "操作人")
    private String operatorName;

    @Schema(description = "备注")
    private String remark;
}
