package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * 运费模板参数
 * Created by AI Assistant
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SmsFreightTemplateParam {
    
    @NotEmpty(message = "模板名称不能为空")
    @Schema(title = "模板名称", required = true)
    private String name;
    
    @NotNull(message = "计费类型不能为空")
    @Schema(title = "计费类型：1-按件数,2-按重量,3-按体积,4-固定运费", required = true)
    private Byte chargeType;
    
    @NotNull(message = "配送类型不能为空")
    @Schema(title = "配送类型：1-快递配送,2-同城配送,3-到店自提", required = true)
    private Byte deliveryType;
    
    @Schema(title = "包邮类型：0-不包邮,1-满金额包邮,2-满件数包邮,3-满重量包邮")
    private Byte freeType = 0;
    
    @Schema(title = "包邮金额条件")
    private BigDecimal freeAmount = BigDecimal.ZERO;
    
    @Schema(title = "包邮件数条件")
    private Integer freeCount = 0;
    
    @Schema(title = "包邮重量条件(kg)")
    private BigDecimal freeWeight = BigDecimal.ZERO;
    
    @Schema(title = "状态：0-禁用，1-启用")
    private Byte status = 1;
    
    @Schema(title = "是否默认模板：false-否，true-是")
    private Boolean isDefault = false;
    
    @Schema(title = "排序")
    private Integer sort = 0;
    
    @Schema(title = "备注说明")
    private String remark;
    
    @Valid
    @Schema(title = "区域运费规则列表")
    private List<SmsFreightTemplateRegionParam> regionRules;
} 