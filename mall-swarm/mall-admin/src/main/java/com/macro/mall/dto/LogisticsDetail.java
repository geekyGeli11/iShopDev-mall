package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogisticsDetail {
    @Schema(title = "时间")
    private String time;

    @Schema(title = "物流信息")
    private String context;

    @Schema(title = "时间")
    private String ftime;

    @Schema(title = "地区编码")
    private String areaCode;

    @Schema(title = "地区名称")
    private String areaName;

    @Schema(title = "状态")
    private String status;

    @Schema(title = "地点")
    private String location;

    @Schema(title = "地区中心坐标")
    private String areaCenter;

    @Schema(title = "地区拼音")
    private String areaPinYin;

    @Schema(title = "状态码")
    private String statusCode;
}