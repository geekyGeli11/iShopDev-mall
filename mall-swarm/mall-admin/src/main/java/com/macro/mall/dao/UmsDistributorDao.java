package com.macro.mall.dao;

import com.macro.mall.dto.UmsDistributorListVO;
import com.macro.mall.dto.UmsDistributorQueryParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 分销商数据访问接口
 */
public interface UmsDistributorDao {
    
    /**
     * 分页查询分销商列表
     * @param queryParam 查询参数
     * @return 分销商列表
     */
    List<UmsDistributorListVO> selectByPage(@Param("param") UmsDistributorQueryParam queryParam);
    
    /**
     * 获取分销商详情（包含统计数据）
     * @param userId 用户ID
     * @return 分销商详情
     */
    UmsDistributorListVO selectDetailByUserId(@Param("userId") Long userId);
    
    /**
     * 获取分销商统计数据
     * @return 统计数据Map
     */
    Map<String, Object> selectStatistics();
    
    /**
     * 更新用户分销商状态
     * @param userId 用户ID
     * @param status 状态
     * @param reason 变更原因
     * @return 影响行数
     */
    int updateDistributorStatus(@Param("userId") Long userId, 
                               @Param("status") Byte status, 
                               @Param("reason") String reason);
    
    /**
     * 更新用户分销商等级
     * @param userId 用户ID
     * @param level 等级
     * @param reason 变更原因
     * @return 影响行数
     */
    int updateDistributorLevel(@Param("userId") Long userId, 
                              @Param("level") Byte level, 
                              @Param("reason") String reason);
    
    /**
     * 批量更新分销商状态
     * @param userIds 用户ID列表
     * @param status 状态
     * @param reason 变更原因
     * @return 影响行数
     */
    int batchUpdateStatus(@Param("userIds") List<Long> userIds, 
                         @Param("status") Byte status, 
                         @Param("reason") String reason);
} 