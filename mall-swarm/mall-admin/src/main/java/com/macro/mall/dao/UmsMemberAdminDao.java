package com.macro.mall.dao;

import com.macro.mall.dto.UmsMemberDetailVO;
import com.macro.mall.dto.UmsMemberListVO;
import com.macro.mall.dto.UmsMemberQueryParam;
import com.macro.mall.dto.SmsCouponHistoryDetailVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户管理自定义Dao
 */
public interface UmsMemberAdminDao {
    
    /**
     * 分页查询用户列表
     */
    List<UmsMemberListVO> selectMemberList(@Param("queryParam") UmsMemberQueryParam queryParam);
    
    /**
     * 获取用户详情
     */
    UmsMemberDetailVO selectMemberDetail(@Param("id") Long id);
    
    /**
     * 获取用户优惠券历史详情（关联查询）
     */
    List<SmsCouponHistoryDetailVO> selectCouponHistoryDetail(@Param("memberId") Long memberId);
    
    /**
     * 统计用户总数
     */
    int countMemberTotal(@Param("queryParam") UmsMemberQueryParam queryParam);
    
    /**
     * 获取用户邀请关系详情列表（包含被邀请人信息）
     */
    List<com.macro.mall.dto.PmsInviteRelationDetailVO> selectInviteRelationsWithDetail(@Param("memberId") Long memberId);
    
    /**
     * 获取用户邀请奖励记录详情列表（包含被邀请人信息）
     */
    List<com.macro.mall.dto.PmsInviteRewardDetailVO> selectInviteRewardsWithDetail(@Param("memberId") Long memberId);
} 