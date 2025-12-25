package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 产品报损提交参数
 */
@Data
@Schema(description = "产品报损提交参数")
public class PmsProductDamageReportParam {
    
    @Schema(description = "报损门店ID")
    private Long storeId;
    
    @Schema(description = "报损类型：1-到货瑕疵，2-外借损坏，3-保存不当，4-人为原因，5-其他")
    private Integer damageType;
    
    @Schema(description = "报损原因ID")
    private Long damageReasonId;
    
    @Schema(description = "报损详细描述")
    private String damageDescription;
    
    @Schema(description = "瑕疵图片（多张用逗号分隔）")
    private String damagePics;
    
    @Schema(description = "供应商/厂家名称")
    private String supplierName;
    
    @Schema(description = "厂家联系方式")
    private String supplierContact;
    
    @Schema(description = "外借用途")
    private String borrowPurpose;
    
    @Schema(description = "借用人")
    private String borrowPerson;
    
    @Schema(description = "借出时间")
    private Date borrowTime;
    
    @Schema(description = "归还时间")
    private Date returnTime;
    
    @Schema(description = "备注")
    private String remark;
    
    @Schema(description = "报损商品明细列表")
    private List<PmsProductDamageItemParam> items;
}
