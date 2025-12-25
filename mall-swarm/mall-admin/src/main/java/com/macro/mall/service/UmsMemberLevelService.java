package com.macro.mall.service;

import com.macro.mall.model.UmsMemberLevel;
import com.macro.mall.dto.UmsMemberLevelVO;

import java.util.List;

/**
 * 会员等级管理Service
 * Created by macro on 2018/4/26.
 */
public interface UmsMemberLevelService {
    /**
     * 获取所有会员等级
     * @param defaultStatus 是否为默认会员
     */
    List<UmsMemberLevelVO> list(Integer defaultStatus);

    /**
     * 创建会员等级
     */
    int create(UmsMemberLevel memberLevel);

    /**
     * 更新会员等级
     */
    int update(Long id, UmsMemberLevel memberLevel);

    /**
     * 删除会员等级
     */
    int delete(Long id);

    /**
     * 获取会员等级详情
     */
    UmsMemberLevel getItem(Long id);
}
