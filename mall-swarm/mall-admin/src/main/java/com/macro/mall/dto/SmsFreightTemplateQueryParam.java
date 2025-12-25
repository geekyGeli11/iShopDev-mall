package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 运费模板查询参数
 * Created by AI Assistant
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SmsFreightTemplateQueryParam {
    
    @Schema(title = "模板名称关键字")
    private String keyword;
    
    @Schema(title = "状态：0-禁用，1-启用")
    private Byte status;
    
    @Schema(title = "计费类型：1-按件数,2-按重量,3-按体积,4-固定运费")
    private Byte chargeType;
    
    @Schema(title = "配送类型：1-快递配送,2-同城配送,3-到店自提")
    private Byte deliveryType;
} 