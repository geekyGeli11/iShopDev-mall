package com.macro.mall.service;

import com.macro.mall.model.OmsOrderDiyInfo;

import java.util.List;

/**
 * 订单DIY信息管理Service
 * Created by macro on 2024/12/20.
 */
public interface OmsOrderDiyInfoService {
    
    /**
     * 创建订单DIY信息
     */
    int create(OmsOrderDiyInfo diyInfo);
    
    /**
     * 更新订单DIY信息
     */
    int update(Long id, OmsOrderDiyInfo diyInfo);
    
    /**
     * 删除订单DIY信息
     */
    int delete(Long id);
    
    /**
     * 批量删除订单DIY信息
     */
    int delete(List<Long> ids);
    
    /**
     * 获取订单DIY信息详情
     */
    OmsOrderDiyInfo getItem(Long id);
    
    /**
     * 根据订单ID获取DIY信息列表
     */
    List<OmsOrderDiyInfo> listByOrderId(Long orderId);
    
    /**
     * 根据订单项ID获取DIY信息
     */
    OmsOrderDiyInfo getByOrderItemId(Long orderItemId);
    
    /**
     * 分页查询订单DIY信息
     */
    List<OmsOrderDiyInfo> list(String keyword, Byte productionStatus, String startTime, String endTime, Integer pageSize, Integer pageNum);
    
    /**
     * 更新生产状态
     */
    int updateProductionStatus(Long id, Byte productionStatus);
    
    /**
     * 批量更新生产状态
     */
    int updateProductionStatus(List<Long> ids, Byte productionStatus);
    
    /**
     * 下载DIY设计文件
     */
    String downloadDesignFile(Long id);
    
    /**
     * 生成生产文件
     */
    String generateProductionFile(Long id);
    
    /**
     * 根据生产状态统计数量
     */
    Long countByProductionStatus(Byte productionStatus);
}
