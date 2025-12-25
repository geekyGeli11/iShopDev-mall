package com.macro.mall.service;

import com.macro.mall.model.OmsGiftWish;

import java.util.List;

/**
 * 礼物心愿管理Service
 */
public interface OmsGiftWishService {
    /**
     * 添加礼物心愿
     */
    OmsGiftWish create(OmsGiftWish wishItem);

    /**
     * 更新礼物心愿
     */
    OmsGiftWish update(Long id, OmsGiftWish wishItem);

    /**
     * 删除礼物心愿
     */
    boolean delete(Long id);

    /**
     * 根据ID获取礼物心愿详情
     */
    OmsGiftWish getById(Long id);

    /**
     * 根据条件分页获取礼物心愿列表
     */
    List<OmsGiftWish> listByFilters(
            Boolean type,
            Boolean category,
            String content,
            Boolean status,
            Integer pageNum,
            Integer pageSize);
} 