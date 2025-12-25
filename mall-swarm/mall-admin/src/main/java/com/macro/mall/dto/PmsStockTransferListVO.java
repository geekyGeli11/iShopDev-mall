package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 调货申请列表VO
 */
@Data
public class PmsStockTransferListVO {
    
    @Schema(title = "主键ID")
    private Long id;
    
    @Schema(title = "操作单号")
    private String operationNo;
    
    @Schema(title = "调货源门店ID")
    private Long fromStoreId;
    
    @Schema(title = "调货源门店名称")
    private String fromStoreName;
    
    @Schema(title = "调货目标门店ID")
    private Long toStoreId;
    
    @Schema(title = "调货目标门店名称")
    private String toStoreName;
    
    @Schema(title = "操作包含的SKU数量")
    private Integer totalItems;
    
    @Schema(title = "审核状态：0=已申请，1=已发货，2=已驳回，3=已确认")
    private Byte approvalStatus;
    
    @Schema(title = "审核状态文本")
    private String approvalStatusText;
    
    @Schema(title = "申请人ID")
    private Long operatorId;
    
    @Schema(title = "申请人名称")
    private String operatorName;
    
    @Schema(title = "发货人名称")
    private String shipperName;
    
    @Schema(title = "发货时间")
    private Date shipTime;
    
    @Schema(title = "确认人名称")
    private String confirmerName;
    
    @Schema(title = "确认时间")
    private Date confirmTime;
    
    @Schema(title = "操作理由")
    private String operationReason;
    
    @Schema(title = "创建时间")
    private Date createdAt;
    
    @Schema(title = "当前用户是否可以确认发货")
    private Boolean canShip;
    
    @Schema(title = "当前用户是否可以确认收货")
    private Boolean canConfirm;
    
    @Schema(title = "当前用户是否可以驳回")
    private Boolean canReject;
}
