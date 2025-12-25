package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 提现申请结果
 */
@Data
public class WithdrawApplyResult {
    
    @Schema(title = "提现申请ID")
    private Long withdrawId;
    
    @Schema(title = "提现金额")
    private BigDecimal amount;
    
    @Schema(title = "手续费")
    private BigDecimal feeAmount;
    
    @Schema(title = "实际到账金额")
    private BigDecimal actualAmount;
    
    @Schema(title = "申请状态", description = "0-待审核 1-审核中 2-处理中 3-已完成 4-已拒绝")
    private Integer status;
    
    @Schema(title = "申请时间")
    private Date applyTime;
    
    @Schema(title = "预计到账时间")
    private Date expectedArrivalTime;
    
    @Schema(title = "备注")
    private String remark;
} 