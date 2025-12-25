package com.macro.mall.service;

import com.macro.mall.dto.PmsDiyTemplateDetailVO;
import com.macro.mall.model.PmsDiyTemplate;

import java.util.List;

/**
 * DIY模板管理Service
 * Created by macro on 2024/12/20.
 */
public interface PmsDiyTemplateService {
    
    /**
     * 创建DIY模板
     */
    int create(PmsDiyTemplate template);
    
    /**
     * 更新DIY模板
     */
    int update(Long id, PmsDiyTemplate template);
    
    /**
     * 删除DIY模板
     */
    int delete(Long id);
    
    /**
     * 批量删除DIY模板
     */
    int delete(List<Long> ids);
    
    /**
     * 获取DIY模板详情
     */
    PmsDiyTemplate getItem(Long id);
    
    /**
     * 分页查询DIY模板
     */
    List<PmsDiyTemplate> list(String keyword, Long productCategoryId, Byte status, Integer pageSize, Integer pageNum);
    
    /**
     * 获取启用的模板列表
     */
    List<PmsDiyTemplate> listEnabled();
    
    /**
     * 批量修改模板状态
     */
    int updateStatus(List<Long> ids, Byte status);
    
    /**
     * 复制模板
     */
    int copyTemplate(Long id, String newName);
    
    /**
     * 根据商品分类获取模板列表
     */
    List<PmsDiyTemplate> listByProductCategory(Long productCategoryId);

    /**
     * 获取模板完整详情（包含面和区域）
     */
    PmsDiyTemplateDetailVO getTemplateDetail(Long id);
}
