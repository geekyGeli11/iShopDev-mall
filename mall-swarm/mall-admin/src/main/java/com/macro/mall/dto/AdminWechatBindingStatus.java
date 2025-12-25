package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 管理员微信绑定状态DTO
 */
@Data
@Schema(description = "管理员微信绑定状态")
public class AdminWechatBindingStatus {
    
    @Schema(title = "是否已绑定")
    private Boolean bound;
    
    @Schema(title = "微信昵称")
    private String nickname;
    
    @Schema(title = "微信头像")
    private String headImgUrl;
    
    @Schema(title = "绑定时间")
    private Date bindTime;
}
