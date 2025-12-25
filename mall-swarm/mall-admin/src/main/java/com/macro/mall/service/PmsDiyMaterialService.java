package com.macro.mall.service;

import com.macro.mall.model.PmsDiyMaterial;

import java.util.List;

/**
 * DIY素材管理Service
 * Created by macro on 2024/12/20.
 */
public interface PmsDiyMaterialService {
    
    /**
     * 创建素材
     */
    int create(PmsDiyMaterial material);
    
    /**
     * 更新素材
     */
    int update(Long id, PmsDiyMaterial material);
    
    /**
     * 删除素材
     */
    int delete(Long id);
    
    /**
     * 批量删除素材
     */
    int delete(List<Long> ids);
    
    /**
     * 根据ID获取素材
     */
    PmsDiyMaterial getById(Long id);
    
    /**
     * 分页查询素材
     */
    List<PmsDiyMaterial> list(String keyword, Long categoryId, String fileType, Integer pageNum, Integer pageSize);
    
    /**
     * 根据分类ID获取素材列表
     */
    List<PmsDiyMaterial> listByCategory(Long categoryId);
    
    /**
     * 批量修改状态
     */
    int updateStatus(List<Long> ids, Byte status);
    
    /**
     * 增加使用次数
     */
    int incrementUsageCount(Long id);

    /**
     * 批量删除素材
     */
    int deleteBatch(List<Long> ids);
}