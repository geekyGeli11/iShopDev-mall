package com.macro.mall.service;

import com.macro.mall.dto.*;
import java.util.List;
import java.util.Map;

/**
 * 分销商服务接口
 */
public interface UmsDistributorService {
    
    /**
     * 分页查询分销商列表
     * @param queryParam 查询参数
     * @param pageSize 每页大小
     * @param pageNum 页码
     * @return 分销商列表
     */
    List<UmsDistributorListVO> list(UmsDistributorQueryParam queryParam, Integer pageSize, Integer pageNum);
    
    /**
     * 获取分销商详情
     * @param userId 用户ID
     * @return 分销商详情
     */
    UmsDistributorListVO getDetail(Long userId);
    
    /**
     * 更新分销商状态
     * @param statusParam 状态更新参数
     * @return 更新结果
     */
    int updateStatus(UmsDistributorStatusParam statusParam);
    
    /**
     * 更新分销商等级
     * @param levelParam 等级更新参数
     * @return 更新结果
     */
    int updateLevel(UmsDistributorLevelParam levelParam);
    
    /**
     * 批量更新分销商状态
     * @param userIds 用户ID列表
     * @param status 状态
     * @param reason 变更原因
     * @return 更新结果
     */
    int batchUpdateStatus(List<Long> userIds, Byte status, String reason);
    
    /**
     * 获取分销商统计数据
     * @return 统计数据
     */
    Map<String, Object> getStatistics();
    
    /**
     * 启用分销商
     * @param userId 用户ID
     * @param reason 原因
     * @return 更新结果
     */
    int enableDistributor(Long userId, String reason);
    
    /**
     * 禁用分销商
     * @param userId 用户ID
     * @param reason 原因
     * @return 更新结果
     */
    int disableDistributor(Long userId, String reason);
    
    /**
     * 暂停分销商
     * @param userId 用户ID
     * @param reason 原因
     * @return 更新结果
     */
    int pauseDistributor(Long userId, String reason);
} 