package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 调货确认收货参数
 */
@Data
public class PmsStockTransferConfirmParam {
    
    @Schema(title = "审核记录ID")
    private Long approvalId;
    
    @Schema(title = "确认备注")
    private String confirmRemark;
    
    @Schema(title = "确认明细列表")
    private List<ConfirmItem> items;
    
    @Data
    public static class ConfirmItem {
        @Schema(title = "审核明细ID")
        private Long itemId;
        
        @Schema(title = "实际收货数量")
        private Integer actualQuantity;
        
        @Schema(title = "差异原因说明（当实际数量与申请数量不同时填写）")
        private String diffReason;
    }
}
