package com.macro.mall.portal.service;

import com.macro.mall.model.CmsTraceabilityList;
import com.macro.mall.portal.domain.TraceabilityDetail;

import java.util.List;

/**
 * 用户端溯源服务接口
 */
public interface TraceabilityService {

    /**
     * 获取某个分类的前三个溯源记录
     *
     * @param category 分类
     * @return 溯源记录列表
     */
    List<CmsTraceabilityList> getTop3ByCategory(String category);

    /**
     * 分页获取某个分类的溯源记录
     *
     * @param category 分类
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @return 分页结果
     */
    List<CmsTraceabilityList> listByCategory(String category, int pageNum, int pageSize);

    /**
     * 获取溯源记录详情及关联商品信息
     *
     * @param traceabilityId 溯源记录ID
     * @return 包含商品列表的溯源记录详情
     */
    TraceabilityDetail getDetailWithProducts(Integer traceabilityId, int pageNum, int pageSize);
}