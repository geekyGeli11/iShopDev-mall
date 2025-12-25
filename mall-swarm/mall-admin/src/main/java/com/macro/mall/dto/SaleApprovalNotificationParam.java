package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 销售单审核通知参数
 */
@Data
@Schema(description = "销售单审核通知参数")
public class SaleApprovalNotificationParam {
    
    @Schema(description = "销售单号")
    private String saleNo;
    
    @Schema(description = "审核结果（通过/驳回）")
    private String auditResult;
    
    @Schema(description = "审核时间")
    private Date auditTime;
    
    @Schema(description = "申请人管理员ID")
    private Long applicantAdminId;
}
