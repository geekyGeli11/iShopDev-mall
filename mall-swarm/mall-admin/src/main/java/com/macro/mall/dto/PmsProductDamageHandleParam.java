package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 产品报损处理参数
 */
@Data
@Schema(description = "产品报损处理参数")
public class PmsProductDamageHandleParam {
    
    @Schema(description = "处理方式：1-退回厂家，2-厂家换货，3-内部消化，4-报废处理，5-折价销售，6-其他")
    private Integer handleMethod;
    
    @Schema(description = "处理方案描述")
    private String handleDescription;
    
    @Schema(description = "处理凭证图片（多张用逗号分隔）")
    private String handlePics;
    
    @Schema(description = "厂家反馈")
    private String supplierFeedback;
    
    @Schema(description = "重新发货单号")
    private String reshipmentSn;
    
    @Schema(description = "重新发货时间")
    private Date reshipmentTime;
    
    @Schema(description = "备注")
    private String remark;
}
