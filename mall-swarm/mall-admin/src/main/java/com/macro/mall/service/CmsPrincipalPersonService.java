package com.macro.mall.service;

import com.macro.mall.model.CmsPrincipalPerson;

import java.util.List;

/**
 * 百大主理人管理Service
 */
public interface CmsPrincipalPersonService {
    /**
     * 创建主理人
     */
    CmsPrincipalPerson create(CmsPrincipalPerson principalPerson);

    /**
     * 更新主理人
     */
    CmsPrincipalPerson update(Long id, CmsPrincipalPerson principalPerson);

    /**
     * 删除主理人
     */
    boolean delete(Long id);

    /**
     * 根据ID获取主理人详情
     */
    CmsPrincipalPerson getById(Long id);

    /**
     * 分页获取主理人列表
     */
    List<CmsPrincipalPerson> list(String name, String position, Boolean status, Boolean isRecommend, int pageNum, int pageSize);
} 