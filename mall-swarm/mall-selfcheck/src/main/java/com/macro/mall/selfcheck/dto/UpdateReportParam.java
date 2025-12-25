package com.macro.mall.selfcheck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 更新状态上报参数
 * 
 * @author macro
 * @since 1.0.0
 */
@Data
@Schema(description = "更新状态上报参数")
public class UpdateReportParam {
    
    @Schema(description = "设备唯一标识", required = true)
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;
    
    @Schema(description = "更新前版本号")
    private Integer fromVersionCode;
    
    @Schema(description = "更新后版本号", required = true)
    @NotNull(message = "目标版本号不能为空")
    private Integer toVersionCode;
    
    @Schema(description = "更新类型(0:可选更新 1:强制更新)", required = true)
    @NotNull(message = "更新类型不能为空")
    private Integer updateType;
    
    @Schema(description = "更新状态(0:开始 1:下载中 2:下载完成 3:安装中 4:成功 5:失败)", required = true)
    @NotNull(message = "更新状态不能为空")
    private Integer updateStatus;
    
    @Schema(description = "下载进度(0-100)")
    private Integer downloadProgress;
    
    @Schema(description = "错误信息")
    private String errorMessage;
}
