package com.macro.mall.service;

import com.macro.mall.dto.CmsLocalGoodsDetail;
import com.macro.mall.dto.CmsLocalGoodsParam;
import com.macro.mall.model.CmsLocalGoods;
import java.util.Date;
import java.util.List;

/**
 * 本地好物管理Service
 */
public interface CmsLocalGoodsService {
    /**
     * 创建本地好物
     */
    CmsLocalGoods create(CmsLocalGoods localGoods);
    
    /**
     * 创建本地好物(含商品关联)
     */
    CmsLocalGoods create(CmsLocalGoodsParam localGoodsParam);

    /**
     * 更新本地好物
     */
    CmsLocalGoods update(Long id, CmsLocalGoods localGoods);
    
    /**
     * 更新本地好物(含商品关联)
     */
    CmsLocalGoods update(Long id, CmsLocalGoodsParam localGoodsParam);

    /**
     * 删除本地好物
     */
    boolean delete(Long id);

    /**
     * 根据ID获取本地好物详情
     */
    CmsLocalGoods getById(Long id);
    
    /**
     * 根据ID获取本地好物详情(包含关联商品信息)
     */
    CmsLocalGoodsDetail getDetailById(Long id);

    /**
     * 根据条件分页获取本地好物列表
     */
    List<CmsLocalGoods> listByFilters(String name, Integer type, Boolean status, Date startTime, Date endTime, int pageNum, int pageSize);
    
    /**
     * 根据条件分页获取本地好物列表(包含关联商品信息)
     */
    List<CmsLocalGoodsDetail> listDetailByFilters(String name, Integer type, Boolean status, Date startTime, Date endTime, int pageNum, int pageSize);
} 