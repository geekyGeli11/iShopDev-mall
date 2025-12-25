package com.macro.mall.selfcheck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 版本检查请求参数
 * 
 * @author macro
 * @since 1.0.0
 */
@Data
@Schema(description = "版本检查请求参数")
public class VersionCheckParam {
    
    @Schema(description = "设备唯一标识", required = true)
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;
    
    @Schema(description = "当前版本号", required = true)
    @NotNull(message = "当前版本号不能为空")
    private Integer currentVersionCode;
    
    @Schema(description = "当前版本名称")
    private String currentVersionName;
    
    @Schema(description = "设备名称")
    private String deviceName;
    
    @Schema(description = "设备型号")
    private String deviceModel;
    
    @Schema(description = "Android版本")
    private String androidVersion;
    
    @Schema(description = "门店ID")
    private Long storeId;
    
    @Schema(description = "门店名称")
    private String storeName;
    
    @Schema(description = "目标平台", example = "android")
    private String platform = "android";
}
