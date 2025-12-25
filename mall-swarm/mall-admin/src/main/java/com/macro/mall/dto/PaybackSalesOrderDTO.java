package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 回本分析销售订单详情 DTO
 */
@Data
@Schema(description = "回本分析销售订单详情")
public class PaybackSalesOrderDTO {

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "订单编号")
    private String orderSn;

    @Schema(description = "订单来源：1-小程序，2-自助设备")
    private Integer sourceType;

    @Schema(description = "订单来源名称")
    private String sourceTypeName;

    @Schema(description = "商品数量")
    private Integer productQuantity;

    @Schema(description = "商品金额")
    private BigDecimal productAmount;

    @Schema(description = "实付金额")
    private BigDecimal payAmount;

    @Schema(description = "支付时间")
    private Date paymentTime;

    @Schema(description = "订单状态")
    private Integer orderStatus;

    @Schema(description = "订单状态名称")
    private String orderStatusName;

    @Schema(description = "收货人")
    private String receiverName;

    @Schema(description = "收货电话")
    private String receiverPhone;

    /**
     * 获取订单来源名称
     */
    public String getSourceTypeName() {
        if (sourceType == null) return "未知";
        switch (sourceType) {
            case 1: return "小程序";
            case 2: return "自助设备";
            default: return "未知";
        }
    }

    /**
     * 获取订单状态名称
     */
    public String getOrderStatusName() {
        if (orderStatus == null) return "未知";
        switch (orderStatus) {
            case 0: return "待付款";
            case 1: return "待发货";
            case 2: return "已发货";
            case 3: return "已完成";
            case 4: return "已关闭";
            case 5: return "无效订单";
            default: return "未知";
        }
    }
}
