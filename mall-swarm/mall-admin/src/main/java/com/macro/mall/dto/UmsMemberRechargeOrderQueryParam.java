package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 充值订单查询参数
 */
@Data
@Schema(description = "充值订单查询参数")
public class UmsMemberRechargeOrderQueryParam {

    @Schema(description = "订单编号")
    private String orderSn;

    @Schema(description = "用户账号")
    private String memberUsername;

    @Schema(description = "创建时间-开始")
    private String createTime;

    @Schema(description = "支付时间-开始")
    private String payTime;

    @Schema(description = "订单状态：0-待支付，1-支付成功，2-支付失败，3-已取消")
    private Integer status;

    @Schema(description = "支付方式：1-支付宝，2-微信")
    private Integer payType;

    @Schema(description = "用户ID")
    private Long memberId;

    @Schema(description = "开始时间")
    private String startTime;

    @Schema(description = "结束时间")
    private String endTime;
} 