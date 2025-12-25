package com.macro.mall.service;

import com.macro.mall.model.CmsActivity;
import java.util.Date;
import java.util.List;

/**
 * 活动管理Service
 */
public interface CmsActivityService {
    /**
     * 创建活动
     */
    CmsActivity create(CmsActivity activity);

    /**
     * 更新活动
     */
    CmsActivity update(Long id, CmsActivity activity);

    /**
     * 删除活动
     */
    boolean delete(Long id);

    /**
     * 根据ID获取活动详情
     */
    CmsActivity getById(Long id);

    /**
     * 根据条件分页获取活动列表
     */
    List<CmsActivity> listByFilters(String name, String location, Date startTime, Date endTime, Boolean status, int pageNum, int pageSize);
} 