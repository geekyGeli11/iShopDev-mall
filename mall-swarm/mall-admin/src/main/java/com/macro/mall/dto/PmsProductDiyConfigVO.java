package com.macro.mall.dto;

import com.macro.mall.model.PmsDiyTemplate;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 商品DIY配置VO
 * Created by macro on 2024/12/20.
 */
public class PmsProductDiyConfigVO {
    
    @Schema(title = "商品ID")
    private Long id;
    
    @Schema(title = "商品名称")
    private String name;
    
    @Schema(title = "是否支持DIY：0-不支持，1-支持")
    private Byte diyEnabled;
    
    @Schema(title = "关联的DIY模板ID")
    private Long diyTemplateId;
    
    @Schema(title = "关联的DIY模板信息")
    private PmsDiyTemplate diyTemplate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public PmsDiyTemplate getDiyTemplate() {
        return diyTemplate;
    }

    public void setDiyTemplate(PmsDiyTemplate diyTemplate) {
        this.diyTemplate = diyTemplate;
    }
}
