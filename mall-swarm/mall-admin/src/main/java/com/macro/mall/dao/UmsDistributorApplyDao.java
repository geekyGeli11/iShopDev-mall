package com.macro.mall.dao;

import com.macro.mall.dto.UmsDistributorApplyListVO;
import com.macro.mall.dto.UmsDistributorApplyQueryParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分销商申请数据访问接口
 */
public interface UmsDistributorApplyDao {
    
    /**
     * 分页查询分销商申请列表
     * @param queryParam 查询参数
     * @return 申请列表
     */
    List<UmsDistributorApplyListVO> selectByPage(@Param("param") UmsDistributorApplyQueryParam queryParam);
    
    /**
     * 获取申请详情（包含关联的用户信息和审核人信息）
     * @param id 申请ID
     * @return 申请详情
     */
    UmsDistributorApplyListVO selectDetailById(@Param("id") Long id);
    
    /**
     * 批量审核申请
     * @param ids 申请ID列表
     * @param status 审核状态
     * @param reviewComment 审核意见
     * @param reviewerId 审核人ID
     * @return 影响行数
     */
    int batchReview(@Param("ids") List<Long> ids, 
                   @Param("status") Byte status, 
                   @Param("reviewComment") String reviewComment, 
                   @Param("reviewerId") Long reviewerId);
} 