package com.macro.mall.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.CmsTraceabilityListMapper;
import com.macro.mall.model.CmsTraceabilityList;
import com.macro.mall.model.CmsTraceabilityListExample;
import com.macro.mall.service.CmsTraceabilityListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CmsTraceabilityListServiceImpl implements CmsTraceabilityListService {

    @Autowired
    private CmsTraceabilityListMapper traceabilityListMapper;

    @Override
    public CmsTraceabilityList create(CmsTraceabilityList traceabilityList) {
        traceabilityList.setCreateTime(new Date());
        traceabilityList.setUpdateTime(new Date());
        traceabilityListMapper.insert(traceabilityList);
        return traceabilityList;
    }

    @Override
    public CmsTraceabilityList update(Integer id, CmsTraceabilityList traceabilityList) {
        CmsTraceabilityList existing = traceabilityListMapper.selectByPrimaryKey(id);
        if (existing == null) {
            return null;
        }
        traceabilityList.setId(id);
        traceabilityList.setUpdateTime(new Date());
        traceabilityListMapper.updateByPrimaryKeyWithBLOBs(traceabilityList);
        return traceabilityList;
    }

    @Override
    public boolean delete(Integer id) {
        int count = traceabilityListMapper.deleteByPrimaryKey(id);
        return count > 0;
    }

    @Override
    public CmsTraceabilityList getById(Integer id) {
        return traceabilityListMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<CmsTraceabilityList> listByFilters(String title, String category, Date startTime, Date endTime, int pageNum, int pageSize) {
        // 设置分页信息
        PageHelper.startPage(pageNum, pageSize);

        // 构建查询条件
        CmsTraceabilityListExample example = new CmsTraceabilityListExample();
        CmsTraceabilityListExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(title)) {
            criteria.andTitleLike("%" + title + "%");
        }
        if (!StringUtils.isEmpty(category)) {
            criteria.andCategoryEqualTo(category);
        }
        if (startTime != null && endTime != null) {
            criteria.andCreateTimeBetween(startTime, endTime);
        }

        // 排序条件（可选）
        example.setOrderByClause("create_time DESC");

        return traceabilityListMapper.selectByExample(example);
    }

}
