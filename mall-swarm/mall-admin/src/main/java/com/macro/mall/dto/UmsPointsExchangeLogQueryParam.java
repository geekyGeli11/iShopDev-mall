package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 积分兑换记录查询参数
 * Created by macro on 2024/01/20.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UmsPointsExchangeLogQueryParam {
    
    @Schema(title = "用户ID")
    private Long memberId;
    
    @Schema(title = "用户名")
    private String memberUsername;
    
    @Schema(title = "兑换类型：1-商品，2-优惠券")
    private Byte exchangeType;
    
    @Schema(title = "目标名称")
    private String targetName;
    
    @Schema(title = "兑换状态：1-成功，2-失败，3-已退回")
    private Byte exchangeStatus;
    
    @Schema(title = "开始日期")
    private Date startDate;
    
    @Schema(title = "结束日期")
    private Date endDate;
    
    @Schema(title = "最小积分使用量")
    private Integer minPointsUsed;
    
    @Schema(title = "最大积分使用量")
    private Integer maxPointsUsed;
} 