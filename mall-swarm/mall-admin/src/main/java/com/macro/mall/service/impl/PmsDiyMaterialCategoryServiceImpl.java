package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.PmsDiyMaterialCategoryMapper;
import com.macro.mall.model.PmsDiyMaterialCategory;
import com.macro.mall.model.PmsDiyMaterialCategoryExample;
import com.macro.mall.service.PmsDiyMaterialCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * DIY素材分类管理Service实现类
 * Created by macro on 2024/12/20.
 */
@Service
public class PmsDiyMaterialCategoryServiceImpl implements PmsDiyMaterialCategoryService {
    
    @Autowired
    private PmsDiyMaterialCategoryMapper categoryMapper;

    @Override
    public int create(PmsDiyMaterialCategory category) {
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());
        if (category.getStatus() == null) {
            category.setStatus((byte) 1); // 默认启用
        }
        if (category.getSort() == null) {
            category.setSort(0);
        }
        if (category.getType() == null) {
            category.setType((byte) 1); // 默认为图片素材
        }
        return categoryMapper.insertSelective(category);
    }

    @Override
    public int update(Long id, PmsDiyMaterialCategory category) {
        category.setId(id);
        category.setUpdateTime(new Date());
        return categoryMapper.updateByPrimaryKeySelective(category);
    }

    @Override
    public int delete(Long id) {
        return categoryMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int delete(List<Long> ids) {
        PmsDiyMaterialCategoryExample example = new PmsDiyMaterialCategoryExample();
        example.createCriteria().andIdIn(ids);
        return categoryMapper.deleteByExample(example);
    }

    @Override
    public PmsDiyMaterialCategory getById(Long id) {
        return categoryMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<PmsDiyMaterialCategory> list(String keyword, Integer type, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PmsDiyMaterialCategoryExample example = new PmsDiyMaterialCategoryExample();
        example.setOrderByClause("sort desc, create_time desc");
        PmsDiyMaterialCategoryExample.Criteria criteria = example.createCriteria();
        
        if (!StringUtils.isEmpty(keyword)) {
            criteria.andNameLike("%" + keyword + "%");
        }
        if (type != null) {
            criteria.andTypeEqualTo(type.byteValue());
        }
        
        return categoryMapper.selectByExample(example);
    }

    @Override
    public List<PmsDiyMaterialCategory> listEnabled() {
        PmsDiyMaterialCategoryExample example = new PmsDiyMaterialCategoryExample();
        example.setOrderByClause("sort desc, create_time desc");
        example.createCriteria().andStatusEqualTo((byte) 1);
        return categoryMapper.selectByExample(example);
    }

    @Override
    public int updateStatus(List<Long> ids, Byte status) {
        PmsDiyMaterialCategory category = new PmsDiyMaterialCategory();
        category.setStatus(status);
        category.setUpdateTime(new Date());
        PmsDiyMaterialCategoryExample example = new PmsDiyMaterialCategoryExample();
        example.createCriteria().andIdIn(ids);
        return categoryMapper.updateByExampleSelective(category, example);
    }
}