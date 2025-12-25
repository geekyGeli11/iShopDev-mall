package com.macro.mall.portal.service;

import com.macro.mall.model.CmsActivity;
import com.macro.mall.model.CmsPrincipalPerson;
import com.macro.mall.model.CmsWonderfulMacau;
import com.macro.mall.portal.domain.LocalGoodsDetail;
import com.macro.mall.model.CmsActivitySignup;

import java.util.List;

/**
 * 内容源管理Service
 */
public interface CmsSourceService {
    
    /**
     * 根据类型获取内容源列表
     * @param type 内容类型：0-本地活动，1-本地好物，2-精彩濠江，3-百大主理人
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 内容列表
     */
    List<?> getSourceList(Integer type, Integer pageNum, Integer pageSize);
    
    /**
     * 获取本地活动详情
     */
    CmsActivity getActivityDetail(Long id);
    
    /**
     * 获取本地好物详情
     */
    LocalGoodsDetail getLocalGoodsDetail(Long id);
    
    /**
     * 获取精彩濠江详情
     */
    CmsWonderfulMacau getWonderfulMacauDetail(Long id);
    
    /**
     * 获取百大主理人详情
     */
    CmsPrincipalPerson getPrincipalPersonDetail(Long id);

    /**
     * 创建活动报名记录
     */
    CmsActivitySignup createActivitySignup(CmsActivitySignup activitySignup);
} 