package com.macro.mall.portal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 邀请参数验证请求
 */
@Data
public class InviteVerifyParam {
    
    @Schema(title = "邀请参数", description = "格式：{用户ID}_{时间戳}")
    private String inviteParam;
    
    @Schema(title = "设备信息", description = "JSON格式的设备信息")
    private String deviceInfo;
    
    @Schema(title = "场景类型", description = "1-小程序卡片分享，2-小程序码扫码")
    private Integer sceneType;
    
    @Schema(title = "IP地址")
    private String ipAddress;
} 