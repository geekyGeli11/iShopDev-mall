package com.macro.mall.dto;

import com.macro.mall.model.PmsStockOperationApproval;
import com.macro.mall.model.PmsStockOperationApprovalItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 调货申请详情VO
 */
@Data
public class PmsStockTransferDetailVO {
    
    @Schema(title = "审核记录")
    private PmsStockOperationApproval approval;
    
    @Schema(title = "审核明细列表")
    private List<PmsStockOperationApprovalItem> items;
    
    @Schema(title = "调货源门店名称")
    private String fromStoreName;
    
    @Schema(title = "调货目标门店名称")
    private String toStoreName;
    
    @Schema(title = "当前用户是否可以确认发货")
    private Boolean canShip;
    
    @Schema(title = "当前用户是否可以确认收货")
    private Boolean canConfirm;
    
    @Schema(title = "当前用户是否可以驳回")
    private Boolean canReject;
}
