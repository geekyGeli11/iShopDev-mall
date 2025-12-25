package com.macro.mall.portal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class OrderLogisticsInfo {
    @Schema(title = "物流公司")
    private String deliveryCompany;
    @Schema(title = "物流单号")
    private String deliverySn;
    @Schema(title = "物流信息")
    private List<LogisticsDetail> logisticsInfo;
    @Schema(title = "错误信息", description = "如果查询发生错误，返回的错误信息")
    private String message;  // 用于返回异常情况时的提示信息
}