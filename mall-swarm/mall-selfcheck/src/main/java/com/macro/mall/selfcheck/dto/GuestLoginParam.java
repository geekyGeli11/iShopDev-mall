package com.macro.mall.selfcheck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 游客登录参数
 * 
 * @author macro
 * @since 1.0.0
 */
@Data
@Schema(description = "游客登录参数")
public class GuestLoginParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "设备ID")
    private String deviceId;

    @Schema(description = "设备类型")
    private String deviceType;

    @Schema(description = "设备信息")
    private String deviceInfo;

    @Schema(description = "门店ID", example = "1")
    private Long storeId;

    @Schema(description = "学校ID", example = "1")
    private Long schoolId;
}