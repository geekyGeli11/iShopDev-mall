package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 邀请参数生成结果
 */
@Data
public class InviteGenerateResult {
    
    @Schema(title = "邀请参数", description = "格式：{用户ID}_{时间戳}")
    private String inviteParam;
    
    @Schema(title = "分享链接", description = "小程序卡片分享链接")
    private String shareUrl;
    
    @Schema(title = "小程序码URL", description = "生成的小程序码图片地址")
    private String qrCodeUrl;
    
    @Schema(title = "邀请者用户名")
    private String inviterUsername;
    
    @Schema(title = "邀请者头像")
    private String inviterAvatar;
    
    @Schema(title = "有效期", description = "参数过期时间")
    private Date expireTime;
    
    @Schema(title = "奖励信息")
    private String rewardDesc;
} 