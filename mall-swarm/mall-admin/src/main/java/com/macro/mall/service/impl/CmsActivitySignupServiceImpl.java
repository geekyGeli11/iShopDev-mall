package com.macro.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.CmsActivitySignupMapper;
import com.macro.mall.model.CmsActivitySignup;
import com.macro.mall.model.CmsActivitySignupExample;
import com.macro.mall.service.CmsActivitySignupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 活动报名管理Service实现类
 */
@Service
public class CmsActivitySignupServiceImpl implements CmsActivitySignupService {
    @Autowired
    private CmsActivitySignupMapper activitySignupMapper;

    @Override
    public CmsActivitySignup create(CmsActivitySignup activitySignup) {
        activitySignup.setCreateTime(new Date());
        activitySignup.setSignupTime(new Date());
        activitySignupMapper.insert(activitySignup);
        return activitySignup;
    }

    @Override
    public CmsActivitySignup update(Long id, CmsActivitySignup activitySignup) {
        activitySignup.setId(id);
        activitySignup.setUpdateTime(new Date());
        int count = activitySignupMapper.updateByPrimaryKeySelective(activitySignup);
        if (count > 0) {
            return activitySignup;
        }
        return null;
    }

    @Override
    public boolean delete(Long id) {
        int count = activitySignupMapper.deleteByPrimaryKey(id);
        return count > 0;
    }

    @Override
    public CmsActivitySignup getById(Long id) {
        return activitySignupMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<CmsActivitySignup> list(String name, String phone, Long activityId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        CmsActivitySignupExample example = new CmsActivitySignupExample();
        CmsActivitySignupExample.Criteria criteria = example.createCriteria();
        if (StrUtil.isNotEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        if (StrUtil.isNotEmpty(phone)) {
            criteria.andPhoneLike("%" + phone + "%");
        }
        if (activityId != null) {
            criteria.andActivityIdEqualTo(activityId);
        }
        example.setOrderByClause("create_time desc");
        return activitySignupMapper.selectByExample(example);
    }
} 