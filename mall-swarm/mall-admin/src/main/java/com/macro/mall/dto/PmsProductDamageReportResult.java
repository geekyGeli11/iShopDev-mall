package com.macro.mall.dto;

import com.macro.mall.model.PmsProductDamageItem;
import com.macro.mall.model.PmsProductDamageLog;
import com.macro.mall.model.PmsProductDamageReport;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 产品报损详情结果
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "产品报损详情结果")
public class PmsProductDamageReportResult extends PmsProductDamageReport {
    
    @Schema(description = "报损商品明细列表")
    private List<PmsProductDamageItem> items;
    
    @Schema(description = "处理日志列表")
    private List<PmsProductDamageLog> logList;
}
