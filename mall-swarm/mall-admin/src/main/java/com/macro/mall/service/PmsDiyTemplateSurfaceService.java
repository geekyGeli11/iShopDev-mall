package com.macro.mall.service;

import com.macro.mall.model.PmsDiyTemplateSurface;

import java.util.List;

/**
 * DIY模板面管理Service
 * Created by macro on 2024/12/20.
 */
public interface PmsDiyTemplateSurfaceService {
    
    /**
     * 创建模板面
     */
    int create(PmsDiyTemplateSurface surface);
    
    /**
     * 更新模板面
     */
    int update(Long id, PmsDiyTemplateSurface surface);
    
    /**
     * 删除模板面
     */
    int delete(Long id);
    
    /**
     * 批量删除模板面
     */
    int delete(List<Long> ids);
    
    /**
     * 获取模板面详情
     */
    PmsDiyTemplateSurface getItem(Long id);
    
    /**
     * 根据模板ID获取模板面列表
     */
    List<PmsDiyTemplateSurface> listByTemplateId(Long templateId);
    
    /**
     * 删除模板的所有面
     */
    int deleteByTemplateId(Long templateId);
    
    /**
     * 复制模板面到新模板
     */
    int copySurfacesToTemplate(Long sourceTemplateId, Long targetTemplateId);
}
