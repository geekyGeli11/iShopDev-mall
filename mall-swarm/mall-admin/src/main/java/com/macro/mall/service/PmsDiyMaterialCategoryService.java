package com.macro.mall.service;

import com.macro.mall.model.PmsDiyMaterialCategory;

import java.util.List;

/**
 * DIY素材分类管理Service
 * Created by macro on 2024/12/20.
 */
public interface PmsDiyMaterialCategoryService {
    
    /**
     * 创建素材分类
     */
    int create(PmsDiyMaterialCategory category);
    
    /**
     * 更新素材分类
     */
    int update(Long id, PmsDiyMaterialCategory category);
    
    /**
     * 删除素材分类
     */
    int delete(Long id);
    
    /**
     * 批量删除素材分类
     */
    int delete(List<Long> ids);
    
    /**
     * 根据ID获取素材分类
     */
    PmsDiyMaterialCategory getById(Long id);
    
    /**
     * 分页查询素材分类
     */
    List<PmsDiyMaterialCategory> list(String keyword, Integer type, Integer pageNum, Integer pageSize);
    
    /**
     * 获取所有启用的素材分类
     */
    List<PmsDiyMaterialCategory> listEnabled();
    
    /**
     * 批量修改状态
     */
    int updateStatus(List<Long> ids, Byte status);
}