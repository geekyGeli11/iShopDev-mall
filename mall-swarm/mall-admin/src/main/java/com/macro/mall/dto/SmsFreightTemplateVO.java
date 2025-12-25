package com.macro.mall.dto;

import com.macro.mall.model.SmsFreightTemplate;
import com.macro.mall.model.SmsFreightTemplateRegion;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 运费模板视图对象
 * Created by AI Assistant
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SmsFreightTemplateVO extends SmsFreightTemplate {
    
    @Schema(title = "区域运费规则列表")
    private List<SmsFreightTemplateRegion> regionRules;
    
    @Schema(title = "应用商品数量")
    private Long productCount;
} 