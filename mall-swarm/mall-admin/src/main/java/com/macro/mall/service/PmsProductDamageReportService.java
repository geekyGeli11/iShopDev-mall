package com.macro.mall.service;

import com.macro.mall.dto.*;
import com.macro.mall.model.PmsProductDamageReport;

import java.util.List;

/**
 * 产品报损管理Service
 */
public interface PmsProductDamageReportService {
    
    /**
     * 创建报损记录
     */
    int create(PmsProductDamageReportParam param, Long adminId, String adminName);
    
    /**
     * 分页查询报损记录
     */
    List<PmsProductDamageReport> list(PmsProductDamageReportQueryParam queryParam, Integer pageSize, Integer pageNum);
    
    /**
     * 获取报损详情
     */
    PmsProductDamageReportResult getDetail(Long id);
    
    /**
     * 处理报损（开始处理）
     */
    int startHandle(Long id, Long adminId, String adminName);
    
    /**
     * 更新处理信息
     */
    int updateHandle(Long id, PmsProductDamageHandleParam param, Long adminId, String adminName);
    
    /**
     * 验收（针对厂家换货场景）
     */
    int acceptance(Long id, Integer acceptanceStatus, String acceptanceRemark, Long adminId, String adminName);
    
    /**
     * 完成处理
     */
    int complete(Long id, Long adminId, String adminName);
    
    /**
     * 关闭报损
     */
    int close(Long id, String remark, Long adminId, String adminName);
    
    /**
     * 删除报损记录
     */
    int delete(List<Long> ids);
    
    /**
     * 获取待处理报损数量
     */
    Integer getPendingCount();
    
    /**
     * 按门店统计报损
     */
    List<PmsProductDamageStatistics> statisticsByStore(String startDate, String endDate);
    
    /**
     * 按报损类型统计
     */
    List<PmsProductDamageStatistics> statisticsByType(String startDate, String endDate);
    
    /**
     * 按时间统计（按天/月）
     */
    List<PmsProductDamageStatistics> statisticsByTime(String startDate, String endDate, String timeUnit);
}
