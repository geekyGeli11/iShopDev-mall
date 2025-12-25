package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 产品报损查询参数
 */
@Data
@Schema(description = "产品报损查询参数")
public class PmsProductDamageReportQueryParam {
    
    @Schema(description = "报损单号")
    private String reportSn;
    
    @Schema(description = "报损门店ID")
    private Long storeId;
    
    @Schema(description = "商品名称（模糊查询）")
    private String productName;
    
    @Schema(description = "SKU编码")
    private String skuCode;
    
    @Schema(description = "报损类型：1-到货瑕疵，2-外借损坏，3-保存不当，4-人为原因，5-其他")
    private Integer damageType;
    
    @Schema(description = "处理状态：0-待处理，1-处理中，2-待验收，3-已完成，4-已关闭")
    private Integer status;
    
    @Schema(description = "提交人ID")
    private Long submitAdminId;
    
    @Schema(description = "处理人ID")
    private Long handleAdminId;
    
    @Schema(description = "开始时间")
    private Date startTime;
    
    @Schema(description = "结束时间")
    private Date endTime;
}
