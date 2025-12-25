package com.macro.mall.dto;

import com.macro.mall.model.PmsDiyArea;
import com.macro.mall.model.PmsDiyTemplate;
import com.macro.mall.model.PmsDiyTemplateSurface;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * DIY模板详情VO，包含模板、面和区域的完整信息
 * Created by macro on 2024/12/20.
 */
public class PmsDiyTemplateDetailVO extends PmsDiyTemplate {
    
    @Schema(title = "模板面列表")
    private List<SurfaceDetailVO> surfaces;

    public List<SurfaceDetailVO> getSurfaces() {
        return surfaces;
    }

    public void setSurfaces(List<SurfaceDetailVO> surfaces) {
        this.surfaces = surfaces;
    }

    /**
     * 面详情VO，包含面和区域信息
     */
    public static class SurfaceDetailVO extends PmsDiyTemplateSurface {
        
        @Schema(title = "DIY区域列表")
        private List<PmsDiyArea> areas;

        public List<PmsDiyArea> getAreas() {
            return areas;
        }

        public void setAreas(List<PmsDiyArea> areas) {
            this.areas = areas;
        }
    }
}
