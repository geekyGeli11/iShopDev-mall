package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 游客查询参数
 */
@Data
@Schema(description = "游客查询参数")
public class UmsGuestQueryParam {

    @Schema(title = "关键字（游客ID或设备ID）")
    private String keyword;

    @Schema(title = "设备类型")
    private String deviceType;

    @Schema(title = "状态：1->正常；0->禁用")
    private Integer status;

    @Schema(title = "绑定学校ID")
    private Long schoolId;

    @Schema(title = "开始时间")
    private String startTime;

    @Schema(title = "结束时间")
    private String endTime;
}
