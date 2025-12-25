package com.macro.mall.service;

import com.macro.mall.dto.UmsDistributorApplyListVO;
import com.macro.mall.dto.UmsDistributorApplyQueryParam;
import com.macro.mall.dto.UmsDistributorApplyReviewParam;
import com.macro.mall.model.UmsDistributorApply;

import java.util.List;

/**
 * 分销商申请服务接口
 */
public interface UmsDistributorApplyService {
    
    /**
     * 分页查询分销商申请列表
     * @param queryParam 查询参数
     * @param pageSize 每页大小
     * @param pageNum 页码
     * @return 申请列表
     */
    List<UmsDistributorApplyListVO> list(UmsDistributorApplyQueryParam queryParam, Integer pageSize, Integer pageNum);
    
    /**
     * 获取申请详情
     * @param id 申请ID
     * @return 申请详情
     */
    UmsDistributorApply getDetail(Long id);
    
    /**
     * 审核申请
     * @param reviewParam 审核参数
     * @param reviewerId 审核人ID
     * @return 是否成功
     */
    int reviewApply(UmsDistributorApplyReviewParam reviewParam, Long reviewerId);
    
    /**
     * 批量审核申请
     * @param ids 申请ID列表
     * @param status 审核状态
     * @param reviewComment 审核意见
     * @param reviewerId 审核人ID
     * @return 影响行数
     */
    int batchReview(List<Long> ids, Byte status, String reviewComment, Long reviewerId);
    
    /**
     * 删除申请
     * @param id 申请ID
     * @return 是否成功
     */
    int deleteApply(Long id);
    
    /**
     * 获取申请统计数据
     * @return 统计数据
     */
    Object getStatistics();
} 