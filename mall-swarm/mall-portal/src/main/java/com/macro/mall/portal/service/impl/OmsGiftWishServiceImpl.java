package com.macro.mall.portal.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.OmsGiftWishMapper;
import com.macro.mall.model.OmsGiftWish;
import com.macro.mall.model.OmsGiftWishExample;
import com.macro.mall.portal.service.OmsGiftWishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 前台礼物心愿Service实现类
 */
@Service
public class OmsGiftWishServiceImpl implements OmsGiftWishService {
    @Autowired
    private OmsGiftWishMapper giftWishMapper;

    @Override
    public List<OmsGiftWish> listWishes(Boolean type, Boolean category, Boolean status, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        OmsGiftWishExample example = new OmsGiftWishExample();
        OmsGiftWishExample.Criteria criteria = example.createCriteria();
        
        // 默认只查询状态为启用的记录
        criteria.andStatusEqualTo(true);
        
        if (type != null) {
            criteria.andTypeEqualTo(type);
        }
        if (category != null) {
            criteria.andCategoryEqualTo(category);
        }
        if (status != null) {
            criteria.andStatusEqualTo(status);
        }
        
        example.setOrderByClause("sort desc");
        return giftWishMapper.selectByExample(example);
    }
} 