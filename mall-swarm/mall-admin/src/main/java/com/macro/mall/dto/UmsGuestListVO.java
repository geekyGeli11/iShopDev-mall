package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 游客列表VO
 */
@Data
@Schema(description = "游客列表VO")
public class UmsGuestListVO {

    @Schema(title = "游客ID")
    private String guestId;

    @Schema(title = "设备ID")
    private String deviceId;

    @Schema(title = "设备类型")
    private String deviceType;

    @Schema(title = "登录IP")
    private String loginIp;

    @Schema(title = "状态：1->正常；0->禁用")
    private Integer status;

    @Schema(title = "创建时间")
    private Date createTime;

    @Schema(title = "最后活跃时间")
    private Date lastActiveTime;

    @Schema(title = "绑定学校ID")
    private Long schoolId;

    @Schema(title = "绑定学校名称")
    private String schoolName;

    @Schema(title = "订单数量")
    private Integer orderCount;

    @Schema(title = "订单总金额")
    private java.math.BigDecimal orderAmount;

    @Schema(title = "会话时长（分钟）")
    private Long sessionDuration;
}
