package com.macro.mall.portal.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.CmsActivityMapper;
import com.macro.mall.mapper.CmsActivitySignupMapper;
import com.macro.mall.mapper.CmsLocalGoodsMapper;
import com.macro.mall.mapper.CmsPrincipalPersonMapper;
import com.macro.mall.mapper.CmsWonderfulMacauMapper;
import com.macro.mall.model.*;
import com.macro.mall.portal.dao.CmsSourceDao;
import com.macro.mall.portal.domain.LocalGoodsDetail;
import com.macro.mall.portal.service.CmsSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 内容源管理Service实现类
 */
@Service
public class CmsSourceServiceImpl implements CmsSourceService {
    
    @Autowired
    private CmsSourceDao cmsSourceDao;
    
    @Autowired
    private CmsActivityMapper activityMapper;
    
    @Autowired
    private CmsActivitySignupMapper activitySignupMapper;
    
    @Autowired
    private CmsLocalGoodsMapper localGoodsMapper;
    
    @Autowired
    private CmsWonderfulMacauMapper wonderfulMacauMapper;
    
    @Autowired
    private CmsPrincipalPersonMapper principalPersonMapper;

    @Override
    public List<?> getSourceList(Integer type, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        
        switch (type) {
            case 0: // 本地活动
                return cmsSourceDao.getActivityList(offset, pageSize);
            case 1: // 本地好物
                return cmsSourceDao.getLocalGoodsList(offset, pageSize);
            case 2: // 精彩濠江
                return cmsSourceDao.getWonderfulMacauList(offset, pageSize);
            case 3: // 百大主理人
                return cmsSourceDao.getPrincipalPersonList(offset, pageSize);
            default:
                return new ArrayList<>();
        }
    }

    @Override
    public CmsActivity getActivityDetail(Long id) {
        return activityMapper.selectByPrimaryKey(id);
    }

    @Override
    public LocalGoodsDetail getLocalGoodsDetail(Long id) {
        // 获取本地好物信息
        CmsLocalGoods localGoods = localGoodsMapper.selectByPrimaryKey(id);
        if (localGoods == null || !localGoods.getStatus()) {
            return null;
        }
        
        // 获取关联商品ID列表
        List<Long> productIds = cmsSourceDao.getProductIdsByLocalGoodsId(id);
        List<PmsProduct> productList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(productIds)) {
            // 获取商品详情
            productList = cmsSourceDao.getProductsByIds(productIds);
        }
        
        // 封装结果
        LocalGoodsDetail result = new LocalGoodsDetail();
        result.setLocalGoods(localGoods);
        result.setProductList(productList);
        return result;
    }

    @Override
    public CmsWonderfulMacau getWonderfulMacauDetail(Long id) {
        return wonderfulMacauMapper.selectByPrimaryKey(id);
    }

    @Override
    public CmsPrincipalPerson getPrincipalPersonDetail(Long id) {
        return principalPersonMapper.selectByPrimaryKey(id);
    }

    @Override
    public CmsActivitySignup createActivitySignup(CmsActivitySignup activitySignup) {
        // 检查活动是否存在且状态正常
        CmsActivity activity = activityMapper.selectByPrimaryKey(activitySignup.getActivityId());
        if (activity == null || !activity.getStatus()) {
            return null;
        }
        
        // 设置创建时间和报名时间
        activitySignup.setCreateTime(new Date());
        activitySignup.setSignupTime(new Date());
        
        // 插入报名记录
        activitySignupMapper.insert(activitySignup);
        return activitySignup;
    }
} 