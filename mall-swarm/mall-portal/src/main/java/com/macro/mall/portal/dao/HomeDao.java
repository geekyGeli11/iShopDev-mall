package com.macro.mall.portal.dao;

import com.macro.mall.model.*;
import com.macro.mall.portal.domain.FlashPromotionProduct;
import com.macro.mall.portal.domain.ProductListDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 首页内容管理自定义Dao
 * Created by macro on 2019/1/28.
 */
public interface HomeDao {

    /**
     * 获取推荐品牌
     */
    List<PmsBrand> getRecommendBrandList(@Param("offset") Integer offset,@Param("limit") Integer limit);

    /**
     * 获取秒杀商品
     */
    List<FlashPromotionProduct> getFlashProductList(@Param("flashPromotionId") Long flashPromotionId, @Param("sessionId") Long sessionId);

    /**
     * 获取新品推荐
     */
    List<com.macro.mall.portal.domain.NewProductDTO> getNewProductList(@Param("offset") Integer offset,@Param("limit") Integer limit);

    /**
     * 获取新品推荐（支持学校筛选）
     */
    List<com.macro.mall.portal.domain.NewProductDTO> getNewProductList(@Param("offset") Integer offset,@Param("limit") Integer limit, @Param("schoolId") Long schoolId);

    /**
     * 获取人气推荐
     */
    List<PmsProduct> getHotProductList(@Param("offset") Integer offset,@Param("limit") Integer limit);

    /**
     * 获取人气推荐（支持学校筛选）
     */
    List<PmsProduct> getHotProductListBySchool(@Param("offset") Integer offset,@Param("limit") Integer limit, @Param("schoolId") Long schoolId);

    /**
     * 获取人气推荐（包含价格）
     */
    List<java.util.Map<String, Object>> getHotProductListWithPrice(@Param("offset") Integer offset,@Param("limit") Integer limit);

    /**
     * 获取人气推荐（包含价格，支持学校筛选）
     */
    List<java.util.Map<String, Object>> getHotProductListWithPrice(@Param("offset") Integer offset,@Param("limit") Integer limit, @Param("schoolId") Long schoolId);

    /**
     * 获取推荐专题
     */
    List<CmsSubject> getRecommendSubjectList(@Param("offset") Integer offset, @Param("limit") Integer limit);
    /**
     * 获取专题推荐
     */
    List<SmsHomeRecommendSubject> selectRecommendSubjectList(@Param("offset") Integer offset, @Param("limit") Integer limit);
    /**
     * 获取溯源推荐
     */
    List<CmsTraceabilityList> getTraceabilityListTrace(@Param("offset") Integer offset, @Param("limit") Integer limit);

    List<Long> getProductIdsBySubjectId(@Param("subjectId") Long subjectId);

    List<PmsProduct> selectByIds(@Param("ids") List<Long> ids);

    /**
     * 获取本地活动推荐
     */
    List<CmsActivity> getActivityList(@Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 获取本地好物推荐
     */
    List<CmsLocalGoods> getLocalGoodsList(@Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 获取精彩濠江推荐
     */
    List<CmsWonderfulMacau> getWonderfulMacauList(@Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 获取百大主理人推荐
     */
    List<CmsPrincipalPerson> getPrincipalPersonList(@Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 按销量分页获取商品列表
     */
    List<ProductListDTO> getProductListBySales(@Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 按销量分页获取商品列表（支持学校筛选）
     */
    List<ProductListDTO> getProductListBySales(@Param("offset") Integer offset, @Param("limit") Integer limit, @Param("schoolId") Long schoolId);

}
