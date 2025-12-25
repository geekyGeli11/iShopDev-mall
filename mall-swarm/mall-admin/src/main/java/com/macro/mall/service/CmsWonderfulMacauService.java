package com.macro.mall.service;

import com.macro.mall.model.CmsWonderfulMacau;
import java.util.List;

/**
 * 精彩濠江管理Service
 */
public interface CmsWonderfulMacauService {
    /**
     * 创建精彩濠江内容
     */
    CmsWonderfulMacau create(CmsWonderfulMacau wonderfulMacau);

    /**
     * 更新精彩濠江内容
     */
    CmsWonderfulMacau update(Long id, CmsWonderfulMacau wonderfulMacau);

    /**
     * 删除精彩濠江内容
     */
    boolean delete(Long id);

    /**
     * 根据ID获取精彩濠江详情
     */
    CmsWonderfulMacau getById(Long id);

    /**
     * 根据条件分页获取精彩濠江列表
     */
    List<CmsWonderfulMacau> listByFilters(String title, Boolean isTop, Boolean status, int pageNum, int pageSize);
} 