package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.CmsActivityMapper;
import com.macro.mall.model.CmsActivity;
import com.macro.mall.model.CmsActivityExample;
import com.macro.mall.service.CmsActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 活动管理Service实现类
 */
@Service
public class CmsActivityServiceImpl implements CmsActivityService {

    @Autowired
    private CmsActivityMapper activityMapper;

    @Override
    public CmsActivity create(CmsActivity activity) {
        activity.setCreateTime(new Date());
        activity.setUpdateTime(new Date());
        activityMapper.insert(activity);
        return activity;
    }

    @Override
    public CmsActivity update(Long id, CmsActivity activity) {
        CmsActivity existing = activityMapper.selectByPrimaryKey(id);
        if (existing == null) {
            return null;
        }
        activity.setId(id);
        activity.setUpdateTime(new Date());
        activityMapper.updateByPrimaryKeyWithBLOBs(activity);
        return activity;
    }

    @Override
    public boolean delete(Long id) {
        int count = activityMapper.deleteByPrimaryKey(id);
        return count > 0;
    }

    @Override
    public CmsActivity getById(Long id) {
        return activityMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<CmsActivity> listByFilters(String name, String location, Date startTime, Date endTime, Boolean status, int pageNum, int pageSize) {
        // 设置分页信息
        PageHelper.startPage(pageNum, pageSize);

        // 构建查询条件
        CmsActivityExample example = new CmsActivityExample();
        CmsActivityExample.Criteria criteria = example.createCriteria();

        if (StringUtils.hasText(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        if (StringUtils.hasText(location)) {
            criteria.andLocationLike("%" + location + "%");
        }
        if (startTime != null) {
            criteria.andStartTimeGreaterThanOrEqualTo(startTime);
        }
        if (endTime != null) {
            criteria.andEndTimeLessThanOrEqualTo(endTime);
        }
        if (status != null) {
            criteria.andStatusEqualTo(status);
        }

        // 排序条件
        example.setOrderByClause("create_time DESC");

        return activityMapper.selectByExampleWithBLOBs(example);
    }
} 