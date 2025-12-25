package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

/**
 * 积分换购优惠券配置参数
 * Created by macro on 2024/01/20.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UmsPointsCouponConfigParam {
    
    @Schema(title = "配置ID（更新时必填）")
    private Long id;
    
    @Schema(title = "优惠券ID", required = true)
    @NotNull(message = "优惠券ID不能为空")
    private Long couponId;
    
    @Schema(title = "优惠券名称", required = true)
    private String couponName;
    
    @Schema(title = "兑换所需积分", required = true)
    @NotNull(message = "兑换所需积分不能为空")
    @Min(value = 1, message = "兑换所需积分必须大于0")
    private Integer pointsPrice;
    
    @Schema(title = "总发放数量", required = true)
    @NotNull(message = "总发放数量不能为空")
    @Min(value = 0, message = "总发放数量不能为负数")
    private Integer totalCount;
    
    @Schema(title = "每人限领数量", required = true)
    @NotNull(message = "每人限领数量不能为空")
    @Min(value = 1, message = "每人限领数量必须大于0")
    private Integer perPersonLimit;
    
    @Schema(title = "状态：0-禁用，1-启用")
    private Byte status = 1;
    
    @Schema(title = "开始时间")
    private Date startTime;
    
    @Schema(title = "结束时间")
    private Date endTime;
    
    @Schema(title = "排序")
    private Integer sortOrder = 0;
    
    @Schema(title = "兑换说明")
    private String description;
} 