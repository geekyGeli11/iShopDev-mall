package com.macro.mall.service;

import com.macro.mall.model.PmsProductDamageReason;

import java.util.List;

/**
 * 报损原因配置Service
 */
public interface PmsProductDamageReasonService {
    
    /**
     * 创建报损原因
     */
    int create(PmsProductDamageReason reason);
    
    /**
     * 更新报损原因
     */
    int update(Long id, PmsProductDamageReason reason);
    
    /**
     * 删除报损原因
     */
    int delete(Long id);
    
    /**
     * 获取所有启用的报损原因
     */
    List<PmsProductDamageReason> listAll();
    
    /**
     * 按类型获取报损原因
     */
    List<PmsProductDamageReason> listByType(Integer type);
    
    /**
     * 分页查询报损原因
     */
    List<PmsProductDamageReason> list(String keyword, Integer type, Integer pageSize, Integer pageNum);
    
    /**
     * 获取报损原因详情
     */
    PmsProductDamageReason getItem(Long id);
    
    /**
     * 更新状态
     */
    int updateStatus(Long id, Integer status);
}
