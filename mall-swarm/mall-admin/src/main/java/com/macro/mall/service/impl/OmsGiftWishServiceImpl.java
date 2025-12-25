package com.macro.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.OmsGiftWishMapper;
import com.macro.mall.model.OmsGiftWish;
import com.macro.mall.model.OmsGiftWishExample;
import com.macro.mall.service.OmsGiftWishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 礼物心愿管理Service实现类
 */
@Service
public class OmsGiftWishServiceImpl implements OmsGiftWishService {
    @Autowired
    private OmsGiftWishMapper giftWishMapper;

    @Override
    public OmsGiftWish create(OmsGiftWish wishItem) {
        wishItem.setCreateTime(new Date());
        wishItem.setUpdateTime(new Date());
        giftWishMapper.insert(wishItem);
        return wishItem;
    }

    @Override
    public OmsGiftWish update(Long id, OmsGiftWish wishItem) {
        wishItem.setId(id);
        wishItem.setUpdateTime(new Date());
        giftWishMapper.updateByPrimaryKeySelective(wishItem);
        return giftWishMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean delete(Long id) {
        int count = giftWishMapper.deleteByPrimaryKey(id);
        return count > 0;
    }

    @Override
    public OmsGiftWish getById(Long id) {
        return giftWishMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<OmsGiftWish> listByFilters(Boolean type, Boolean category, String content, Boolean status, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        OmsGiftWishExample example = new OmsGiftWishExample();
        OmsGiftWishExample.Criteria criteria = example.createCriteria();
        
        if (type != null) {
            criteria.andTypeEqualTo(type);
        }
        if (category != null) {
            criteria.andCategoryEqualTo(category);
        }
        if (StrUtil.isNotEmpty(content)) {
            criteria.andContentLike("%" + content + "%");
        }
        if (status != null) {
            criteria.andStatusEqualTo(status);
        }
        
        example.setOrderByClause("sort desc");
        return giftWishMapper.selectByExample(example);
    }
} 