package com.macro.mall.portal.service;

import com.macro.mall.model.PmsDiyMaterial;
import com.macro.mall.model.PmsDiyMaterialCategory;
import com.macro.mall.model.PmsDiyTemplate;
import com.macro.mall.model.UmsDiyDesign;
import com.macro.mall.portal.domain.DiyDesignParam;
import com.macro.mall.portal.domain.DiyPreviewResult;

import java.util.List;

/**
 * 小程序端DIY功能Service
 * Created by macro on 2024/12/20.
 */
public interface PortalDiyService {
    
    /**
     * 根据商品ID获取DIY模板信息
     */
    PmsDiyTemplate getDiyTemplateByProductId(Long productId);
    
    /**
     * 获取DIY素材分类列表
     */
    List<PmsDiyMaterialCategory> getDiyMaterialCategories();

    /**
     * 获取DIY素材列表
     */
    List<PmsDiyMaterial> getDiyMaterials(Long categoryId, Integer type);
    
    /**
     * 保存用户DIY设计
     */
    Long saveDiyDesign(DiyDesignParam designParam, Long memberId);
    
    /**
     * 更新用户DIY设计
     */
    int updateDiyDesign(Long designId, DiyDesignParam designParam);
    
    /**
     * 获取用户DIY设计详情
     */
    UmsDiyDesign getDiyDesign(Long designId, Long memberId);
    
    /**
     * 获取用户的DIY设计列表
     */
    List<UmsDiyDesign> getUserDiyDesigns(Long memberId, Integer pageSize, Integer pageNum);
    
    /**
     * 删除用户DIY设计
     */
    int deleteDiyDesign(Long designId, Long memberId);
    
    /**
     * 生成DIY预览图
     */
    DiyPreviewResult generatePreview(DiyDesignParam designParam);
    
    /**
     * 获取AI风格列表
     */
    List<Object> getAIStyles();

    /**
     * AI风格化处理
     */
    String aiStylization(Long memberId, String imageUrl, String style, String prompt, String functionType);

    /**
     * 获取AI风格化记录
     */
    List<com.macro.mall.model.UmsAiStylizationRecord> getAiStylizationRecords(Long memberId, Integer pageSize, Integer pageNum);
    
    /**
     * 检查商品是否支持DIY
     */
    boolean checkProductDiyEnabled(Long productId);
    
    /**
     * 获取商品DIY配置信息
     */
    com.macro.mall.portal.domain.ProductDiyConfig getProductDiyConfig(Long productId);

    /**
     * 获取商品可定制面信息
     */
    List<Object> getProductCustomizableAreas(Long productId);
}
