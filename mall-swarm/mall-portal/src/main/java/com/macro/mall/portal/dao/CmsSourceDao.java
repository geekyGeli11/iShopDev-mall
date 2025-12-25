package com.macro.mall.portal.dao;

import com.macro.mall.model.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 内容源管理自定义DAO
 */
public interface CmsSourceDao {

    /**
     * 获取本地活动列表
     */
    List<CmsActivity> getActivityList(@Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 获取本地好物列表
     */
    List<CmsLocalGoods> getLocalGoodsList(@Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 获取精彩濠江列表
     */
    List<CmsWonderfulMacau> getWonderfulMacauList(@Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 获取百大主理人列表
     */
    List<CmsPrincipalPerson> getPrincipalPersonList(@Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 获取本地好物关联的商品ID
     */
    List<Long> getProductIdsByLocalGoodsId(@Param("localGoodsId") Long localGoodsId);

    /**
     * 根据商品ID列表查询商品详情
     */
    List<PmsProduct> getProductsByIds(@Param("ids") List<Long> ids);
} 