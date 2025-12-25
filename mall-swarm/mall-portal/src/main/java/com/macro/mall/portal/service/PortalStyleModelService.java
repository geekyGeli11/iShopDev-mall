package com.macro.mall.portal.service;

import com.macro.mall.model.PmsProduct;
import com.macro.mall.model.PmsStyleModel;
import com.macro.mall.portal.domain.StyleModelCardResult;

import java.util.List;

/**
 * 小程序端风格模型Service
 * Created by macro on 2024/8/25.
 */
public interface PortalStyleModelService {
    /**
     * 获取风格模型卡片列表
     */
    List<StyleModelCardResult> getStyleModelCards();

    /**
     * 根据风格模型ID获取关联商品
     */
    List<PmsProduct> getStyleModelProducts(Long styleModelId, Integer pageSize, Integer pageNum);

    /**
     * 获取风格模型关联的商品列表（支持分类筛选）
     */
    List<PmsProduct> getStyleModelProductList(Long styleModelId, Integer page, Integer pageSize, String category);

    /**
     * 获取风格模型详情
     */
    PmsStyleModel getStyleModelDetail(Long styleModelId);
}
