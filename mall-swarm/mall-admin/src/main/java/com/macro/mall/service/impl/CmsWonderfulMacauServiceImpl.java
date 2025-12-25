package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.CmsWonderfulMacauMapper;
import com.macro.mall.model.CmsWonderfulMacau;
import com.macro.mall.model.CmsWonderfulMacauExample;
import com.macro.mall.service.CmsWonderfulMacauService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 精彩濠江管理Service实现类
 */
@Service
public class CmsWonderfulMacauServiceImpl implements CmsWonderfulMacauService {

    @Autowired
    private CmsWonderfulMacauMapper wonderfulMacauMapper;

    @Override
    public CmsWonderfulMacau create(CmsWonderfulMacau wonderfulMacau) {
        wonderfulMacau.setCreateTime(new Date());
        wonderfulMacau.setUpdateTime(new Date());
        wonderfulMacauMapper.insert(wonderfulMacau);
        return wonderfulMacau;
    }

    @Override
    public CmsWonderfulMacau update(Long id, CmsWonderfulMacau wonderfulMacau) {
        CmsWonderfulMacau existing = wonderfulMacauMapper.selectByPrimaryKey(id);
        if (existing == null) {
            return null;
        }
        wonderfulMacau.setId(id);
        wonderfulMacau.setUpdateTime(new Date());
        wonderfulMacauMapper.updateByPrimaryKeyWithBLOBs(wonderfulMacau);
        return wonderfulMacau;
    }

    @Override
    public boolean delete(Long id) {
        int count = wonderfulMacauMapper.deleteByPrimaryKey(id);
        return count > 0;
    }

    @Override
    public CmsWonderfulMacau getById(Long id) {
        return wonderfulMacauMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<CmsWonderfulMacau> listByFilters(String title, Boolean isTop, Boolean status, int pageNum, int pageSize) {
        // 设置分页信息
        PageHelper.startPage(pageNum, pageSize);

        // 构建查询条件
        CmsWonderfulMacauExample example = new CmsWonderfulMacauExample();
        CmsWonderfulMacauExample.Criteria criteria = example.createCriteria();

        if (StringUtils.hasText(title)) {
            criteria.andTitleLike("%" + title + "%");
        }
        if (isTop != null) {
            criteria.andIsTopEqualTo(isTop);
        }
        if (status != null) {
            criteria.andStatusEqualTo(status);
        }

        // 排序条件 - 先按置顶排序，再按发布时间倒序
        example.setOrderByClause("is_top DESC, sort ASC, create_time DESC");

        return wonderfulMacauMapper.selectByExampleWithBLOBs(example);
    }
} 