package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

/**
 * 商品DIY配置参数
 * Created by macro on 2024/12/20.
 */
public class PmsProductDiyConfigParam {
    
    @Schema(title = "是否支持DIY：0-不支持，1-支持", required = true)
    @NotNull(message = "DIY支持状态不能为空")
    private Byte diyEnabled;
    
    @Schema(title = "关联的DIY模板ID")
    private Long diyTemplateId;

    public Byte getDiyEnabled() {
        return diyEnabled;
    }

    public void setDiyEnabled(Byte diyEnabled) {
        this.diyEnabled = diyEnabled;
    }

    public Long getDiyTemplateId() {
        return diyTemplateId;
    }

    public void setDiyTemplateId(Long diyTemplateId) {
        this.diyTemplateId = diyTemplateId;
    }
}
