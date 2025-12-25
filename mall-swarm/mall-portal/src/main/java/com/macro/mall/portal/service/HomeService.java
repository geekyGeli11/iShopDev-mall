package com.macro.mall.portal.service;

import com.macro.mall.model.CmsSubject;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.model.PmsProductCategory;
import com.macro.mall.portal.domain.HomeContentResult;
import com.macro.mall.portal.domain.ProductListDTO;
import com.macro.mall.portal.domain.SubjectDetailResult;
import com.macro.mall.model.SmsHomeNewProduct;

import java.util.List;

/**
 * 首页内容管理Service
 * Created by macro on 2019/1/28.
 */
public interface HomeService {

    /**
     * 获取首页内容
     */
    HomeContentResult content();

    /**
     * 获取首页内容（支持学校筛选）
     */
    HomeContentResult content(Long schoolId);

    /**
     * 首页商品推荐
     */
    List<PmsProduct> recommendProductList(Integer pageSize, Integer pageNum);

    /**
     * 获取商品分类
     * @param parentId 0:获取一级分类；其他：获取指定二级分类
     */
    List<PmsProductCategory> getProductCateList(Long parentId);

    /**
     * 根据专题分类分页获取专题
     * @param cateId 专题分类id
     */
    List<CmsSubject> getSubjectList(Long cateId, Integer pageSize, Integer pageNum);

    /**
     * 分页获取人气推荐商品
     */
    List<PmsProduct> hotProductList(Integer pageNum, Integer pageSize);

    /**
     * 分页获取人气推荐商品（支持学校筛选）
     */
    List<PmsProduct> hotProductList(Integer pageNum, Integer pageSize, Long schoolId);

    /**
     * 获取带价格的人气推荐
     */
    List<java.util.Map<String, Object>> hotProductListWithPrice(Integer pageNum, Integer pageSize);

    /**
     * 分页获取新品推荐商品
     */
    List<com.macro.mall.portal.domain.NewProductDTO> newProductList(Integer pageNum, Integer pageSize);

    /**
     * 分页获取新品推荐商品（支持学校筛选）
     */
    List<com.macro.mall.portal.domain.NewProductDTO> newProductList(Integer pageNum, Integer pageSize, Long schoolId);

    /**
     * 按销量分页获取商品列表
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 商品列表
     */
    List<ProductListDTO> productListBySales(Integer pageNum, Integer pageSize);

    /**
     * 按销量分页获取商品列表（支持学校筛选）
     * @param pageNum 页码
     * @param pageSize 页大小
     * @param schoolId 学校ID
     * @return 商品列表
     */
    List<ProductListDTO> productListBySales(Integer pageNum, Integer pageSize, Long schoolId);

    SubjectDetailResult getSubjectDetail(Long subjectId, Long recommendSubjectId);

}
