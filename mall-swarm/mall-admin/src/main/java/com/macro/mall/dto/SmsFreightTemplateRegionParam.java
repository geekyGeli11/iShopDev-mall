package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 运费模板区域规则参数
 * Created by AI Assistant
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SmsFreightTemplateRegionParam {
    
    @Schema(title = "区域规则ID，更新时使用")
    private Long id;
    
    @NotNull(message = "区域类型不能为空")
    @Schema(title = "区域类型：1-省份,2-城市,3-距离范围", required = true)
    private Byte regionType;
    
    @Schema(title = "区域ID列表（JSON格式）")
    private String regionIds;
    
    @Schema(title = "区域名称列表")
    private String regionNames;
    
    @Schema(title = "起始距离(km)")
    private BigDecimal distanceStart = BigDecimal.ZERO;
    
    @Schema(title = "结束距离(km)")
    private BigDecimal distanceEnd = BigDecimal.ZERO;
    
    @NotNull(message = "首件/首重运费不能为空")
    @Schema(title = "首件/首重运费", required = true)
    private BigDecimal firstAmount;
    
    @Schema(title = "首件数量/首重重量")
    private BigDecimal firstCount = BigDecimal.ONE;
    
    @NotNull(message = "续件/续重运费不能为空")
    @Schema(title = "续件/续重运费", required = true)
    private BigDecimal additionalAmount;
    
    @Schema(title = "续件数量/续重重量")
    private BigDecimal additionalCount = BigDecimal.ONE;
    
    @Schema(title = "排序")
    private Integer sort = 0;
} 