package com.macro.mall.service;

import com.macro.mall.model.OmsSchool;
import java.util.List;

/**
 * 学校管理Service
 */
public interface OmsSchoolService {
    
    /**
     * 创建学校
     */
    OmsSchool create(OmsSchool school);

    /**
     * 更新学校信息
     */
    OmsSchool update(Long id, OmsSchool school);

    /**
     * 删除学校
     */
    boolean delete(Long id);

    /**
     * 根据ID获取学校详情
     */
    OmsSchool getById(Long id);

    /**
     * 根据条件分页获取学校列表
     */
    List<OmsSchool> list(String schoolName, Boolean status, int pageNum, int pageSize);

    /**
     * 获取所有启用状态的学校（用于下拉选择）
     */
    List<OmsSchool> listEnabled();

    /**
     * 更新学校状态
     */
    boolean updateStatus(Long id, Integer status);
}
