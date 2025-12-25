package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 调货申请查询参数
 */
@Data
public class PmsStockTransferQueryParam {
    
    @Schema(title = "页码")
    private Integer pageNum = 1;
    
    @Schema(title = "每页数量")
    private Integer pageSize = 10;
    
    @Schema(title = "操作单号")
    private String operationNo;
    
    @Schema(title = "审核状态：0=已申请，1=已发货，2=已驳回，3=已确认")
    private Byte approvalStatus;
    
    @Schema(title = "调货源门店ID")
    private Long fromStoreId;
    
    @Schema(title = "调货目标门店ID")
    private Long toStoreId;
    
    @Schema(title = "申请人ID")
    private Long operatorId;
    
    @Schema(title = "开始时间")
    private String startTime;
    
    @Schema(title = "结束时间")
    private String endTime;
    
    @Schema(title = "商品名称关键字")
    private String productKeyword;
    
    @Schema(title = "关联门店ID（筛选fromStoreId或toStoreId等于此值的记录，用于门店账号筛选）")
    private Long relatedStoreId;
}
