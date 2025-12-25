package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.CmsPrincipalPersonMapper;
import com.macro.mall.model.CmsPrincipalPerson;
import com.macro.mall.model.CmsPrincipalPersonExample;
import com.macro.mall.service.CmsPrincipalPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 百大主理人管理Service实现类
 */
@Service
public class CmsPrincipalPersonServiceImpl implements CmsPrincipalPersonService {

    @Autowired
    private CmsPrincipalPersonMapper principalPersonMapper;

    @Override
    public CmsPrincipalPerson create(CmsPrincipalPerson principalPerson) {
        principalPerson.setCreateTime(new Date());
        principalPerson.setUpdateTime(new Date());
        principalPersonMapper.insert(principalPerson);
        return principalPerson;
    }

    @Override
    public CmsPrincipalPerson update(Long id, CmsPrincipalPerson principalPerson) {
        CmsPrincipalPerson existing = principalPersonMapper.selectByPrimaryKey(id);
        if (existing == null) {
            return null;
        }
        principalPerson.setId(id);
        principalPerson.setUpdateTime(new Date());
        principalPersonMapper.updateByPrimaryKeyWithBLOBs(principalPerson);
        return principalPerson;
    }

    @Override
    public boolean delete(Long id) {
        int count = principalPersonMapper.deleteByPrimaryKey(id);
        return count > 0;
    }

    @Override
    public CmsPrincipalPerson getById(Long id) {
        return principalPersonMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<CmsPrincipalPerson> list(String name, String position, Boolean status, Boolean isRecommend, int pageNum, int pageSize) {
        // 设置分页信息
        PageHelper.startPage(pageNum, pageSize);

        // 构建查询条件
        CmsPrincipalPersonExample example = new CmsPrincipalPersonExample();
        CmsPrincipalPersonExample.Criteria criteria = example.createCriteria();

        if (StringUtils.hasText(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        if (StringUtils.hasText(position)) {
            criteria.andPositionLike("%" + position + "%");
        }
        if (status != null) {
            criteria.andStatusEqualTo(status);
        }
        if (isRecommend != null) {
            criteria.andIsRecommendEqualTo(isRecommend);
        }

        // 按照排序字段和创建时间倒序排列
        example.setOrderByClause("sort ASC, create_time DESC");

        return principalPersonMapper.selectByExampleWithBLOBs(example);
    }
} 