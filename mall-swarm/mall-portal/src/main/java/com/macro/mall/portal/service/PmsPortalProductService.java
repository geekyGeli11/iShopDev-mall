package com.macro.mall.portal.service;

import com.macro.mall.model.PmsProduct;
import com.macro.mall.portal.domain.PmsPortalProductDetail;
import com.macro.mall.portal.domain.PmsProductCategoryNode;

import java.util.List;

/**
 * 前台商品管理Service
 * Created by macro on 2020/4/6.
 */
public interface PmsPortalProductService {
    /**
     * 综合搜索商品
     */
    List<PmsProduct> search(String keyword, Long brandId, Long productCategoryId, Integer pageNum, Integer pageSize, Integer sort);

    /**
     * 综合搜索商品（支持学校筛选）
     */
    List<PmsProduct> search(String keyword, Long brandId, Long productCategoryId, Integer pageNum, Integer pageSize, Integer sort, Long schoolId);

    /**
     * 综合搜索商品（支持学校筛选和DIY筛选）
     */
    List<PmsProduct> search(String keyword, Long brandId, Long productCategoryId, Integer pageNum, Integer pageSize, Integer sort, Long schoolId, Boolean isDIY);

    /**
     * 获取搜索结果总数（支持学校筛选）
     */
    long searchCount(String keyword, Long brandId, Long productCategoryId, Long schoolId);

    /**
     * 获取搜索结果总数（支持学校筛选和DIY筛选）
     */
    long searchCount(String keyword, Long brandId, Long productCategoryId, Long schoolId, Boolean isDIY);

    /**
     * 以树形结构获取所有商品分类
     */
    List<PmsProductCategoryNode> categoryTreeList();

    /**
     * 获取前台商品详情
     */
    PmsPortalProductDetail detail(Long id);
    
    /**
     * 获取前台商品详情（支持门店库存）
     * @param id 商品ID
     * @param storeId 门店ID，为空时查询总库存，不为空时查询门店库存
     */
    PmsPortalProductDetail detail(Long id, Long storeId);
}
