package com.macro.mall.service;

import com.macro.mall.model.CmsActivitySignup;

import java.util.List;

/**
 * 活动报名管理Service
 */
public interface CmsActivitySignupService {
    /**
     * 添加活动报名
     */
    CmsActivitySignup create(CmsActivitySignup activitySignup);

    /**
     * 修改活动报名
     */
    CmsActivitySignup update(Long id, CmsActivitySignup activitySignup);

    /**
     * 删除活动报名
     */
    boolean delete(Long id);

    /**
     * 根据ID获取活动报名详情
     */
    CmsActivitySignup getById(Long id);

    /**
     * 根据条件分页获取活动报名列表
     */
    List<CmsActivitySignup> list(String name, String phone, Long activityId, Integer pageNum, Integer pageSize);
} 