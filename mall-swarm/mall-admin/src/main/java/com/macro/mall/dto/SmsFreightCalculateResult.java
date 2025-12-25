package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 运费计算结果
 * Created by AI Assistant
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SmsFreightCalculateResult {
    
    @Schema(title = "总运费")
    private BigDecimal totalFreight;
    
    @Schema(title = "是否免运费")
    private Boolean isFreeShipping;
    
    @Schema(title = "免运费原因")
    private String freeShippingReason;
    
    @Schema(title = "运费计算详情")
    private List<FreightCalculateDetail> details;
    
    /**
     * 运费计算详情
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class FreightCalculateDetail {
        
        @Schema(title = "运费模板ID")
        private Long templateId;
        
        @Schema(title = "运费模板名称")
        private String templateName;
        
        @Schema(title = "模板运费")
        private BigDecimal templateFreight;
        
        @Schema(title = "商品列表")
        private List<Long> productIds;
        
        @Schema(title = "计算说明")
        private String calculation;
        
        @Schema(title = "是否包邮")
        private Boolean isFree;
        
        @Schema(title = "包邮原因")
        private String freeReason;
    }
} 