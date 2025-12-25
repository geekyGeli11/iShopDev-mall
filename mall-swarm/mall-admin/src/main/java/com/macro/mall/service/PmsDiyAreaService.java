package com.macro.mall.service;

import com.macro.mall.model.PmsDiyArea;

import java.util.List;

/**
 * DIY区域管理Service
 * Created by macro on 2024/12/20.
 */
public interface PmsDiyAreaService {
    
    /**
     * 创建DIY区域
     */
    int create(PmsDiyArea area);
    
    /**
     * 更新DIY区域
     */
    int update(Long id, PmsDiyArea area);
    
    /**
     * 删除DIY区域
     */
    int delete(Long id);
    
    /**
     * 批量删除DIY区域
     */
    int delete(List<Long> ids);
    
    /**
     * 获取DIY区域详情
     */
    PmsDiyArea getItem(Long id);
    
    /**
     * 根据面ID获取DIY区域列表
     */
    List<PmsDiyArea> listBySurfaceId(Long surfaceId);
    
    /**
     * 删除面的所有区域
     */
    int deleteBySurfaceId(Long surfaceId);
    
    /**
     * 复制区域到新面
     */
    int copyAreasToSurface(Long sourceSurfaceId, Long targetSurfaceId);
}
