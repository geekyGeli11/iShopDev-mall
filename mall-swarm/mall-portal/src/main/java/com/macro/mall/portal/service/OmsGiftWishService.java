package com.macro.mall.portal.service;

import com.macro.mall.model.OmsGiftWish;
import java.util.List;

/**
 * 前台礼物心愿Service
 */
public interface OmsGiftWishService {
    /**
     * 根据条件分页获取礼物心愿列表
     */
    List<OmsGiftWish> listWishes(Boolean type, Boolean category, Boolean status, Integer pageNum, Integer pageSize);
} 