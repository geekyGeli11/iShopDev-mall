package com.macro.mall.portal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 邀请参数生成请求
 */
@Data
public class InviteGenerateParam {
    
    @Schema(title = "用户ID", description = "生成邀请参数的用户ID")
    private Long userId;
    
    @Schema(title = "场景类型", description = "1-小程序卡片，2-小程序码")
    private Integer sceneType = 1;
    
    @Schema(title = "是否生成小程序码", description = "默认为true")
    private Boolean generateQrCode = true;
    
    @Schema(title = "小程序码尺寸", description = "默认430px")
    private Integer qrCodeSize = 430;
    
    // 为了与代码中的调用保持一致，添加自定义的 getter 方法
    public Boolean isGenerateQrCode() {
        return generateQrCode;
    }
} 