package com.macro.mall.service;

import com.macro.mall.dto.UmsMemberDetailVO;
import com.macro.mall.dto.UmsMemberListVO;
import com.macro.mall.dto.UmsMemberQueryParam;
import com.macro.mall.dto.UmsMemberUpdateParam;
import com.macro.mall.dto.SmsCouponHistoryDetailVO;
import com.macro.mall.model.UmsIntegrationChangeHistory;
import com.macro.mall.model.SmsCouponHistory;
import com.macro.mall.model.SmsCoupon;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.PmsInviteRelation;
import com.macro.mall.model.PmsInviteReward;
import com.macro.mall.model.PmsInviteWithdrawApply;
import com.macro.mall.model.UmsMemberTag;
import com.macro.mall.model.UmsMemberSigninLog;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户管理Admin Service
 */
public interface UmsMemberAdminService {
    
    /**
     * 分页查询用户列表
     */
    List<UmsMemberListVO> list(UmsMemberQueryParam queryParam, Integer pageSize, Integer pageNum);
    
    /**
     * 获取用户详情
     */
    UmsMemberDetailVO getDetail(Long id);
    
    /**
     * 更新用户信息
     */
    int updateMember(Long id, UmsMemberUpdateParam updateParam);
    
    /**
     * 更新用户状态
     */
    int updateStatus(Long id, Integer status);
    
    /**
     * 删除用户
     */
    int deleteMember(Long id);
    
    /**
     * 获取用户统计信息
     */
    int getTotalMemberCount();
    
    /**
     * 发放积分给用户
     */
    int giveIntegration(Long memberId, Integer integration, String reason);
    
    /**
     * 扣减用户积分
     */
    int deductIntegration(Long memberId, Integer integration, String reason);
    
    /**
     * 获取用户积分变更历史
     */
    List<UmsIntegrationChangeHistory> getIntegrationHistory(Long memberId, Integer pageSize, Integer pageNum);
    
    /**
     * 获取用户积分汇总信息
     */
    Map<String, Object> getIntegrationSummary(Long memberId);
    
    /**
     * 获取用户优惠券使用历史
     */
    List<SmsCouponHistory> getCouponHistory(Long memberId, Integer pageSize, Integer pageNum);
    
    /**
     * 获取用户优惠券使用历史详情（包含优惠券详细信息）
     */
    List<SmsCouponHistoryDetailVO> getCouponHistoryDetail(Long memberId, Integer pageSize, Integer pageNum);
    
    /**
     * 发放优惠券给用户
     */
    int sendCouponToMember(Long memberId, Long couponId, String reason);
    
    /**
     * 获取可发放的优惠券列表
     * @param memberId 用户ID，如果不为空则过滤掉用户已领取且有限制的优惠券
     * @param pageSize 每页条数
     * @param pageNum 页码
     * @return 可发放的优惠券列表
     */
    List<SmsCoupon> getAvailableCoupons(Long memberId, Integer pageSize, Integer pageNum);
    
    /**
     * 获取用户订单列表
     * @param memberId 用户ID
     * @param orderType 订单类型 (0:正常订单, 1:秒杀订单, 2:送礼订单, null:所有)
     * @param status 订单状态 (0:待付款, 1:待发货, 2:已发货, 3:已完成, 4:已关闭, null:所有)
     * @param pageSize 每页条数
     * @param pageNum 页码
     * @return 订单列表
     */
    List<OmsOrder> getMemberOrders(Long memberId, Integer orderType, Integer status, Integer pageSize, Integer pageNum);
    
    /**
     * 获取用户订单统计
     */
    Map<String, Object> getMemberOrderStatistics(Long memberId);
    
    /**
     * 获取用户邀请汇总信息
     */
    Map<String, Object> getInviteSummary(Long memberId);
    
    /**
     * 获取用户邀请关系列表
     */
    List<com.macro.mall.dto.PmsInviteRelationDetailVO> getInviteRelations(Long memberId, Integer pageSize, Integer pageNum);
    
    /**
     * 获取用户邀请奖励记录
     */
    List<com.macro.mall.dto.PmsInviteRewardDetailVO> getInviteRewards(Long memberId, Integer pageSize, Integer pageNum);
    
    /**
     * 获取用户提现申请记录
     */
    List<PmsInviteWithdrawApply> getWithdrawApplies(Long memberId, Integer pageSize, Integer pageNum);
    
    /**
     * 获取用户画像信息
     */
    Map<String, Object> getMemberProfile(Long memberId);
    
    /**
     * 重新生成用户画像
     */
    Map<String, Object> generateMemberProfile(Long memberId);
    
    /**
     * 获取用户标签
     */
    List<UmsMemberTag> getMemberTags(Long memberId);
    
    /**
     * 更新用户标签
     */
    int updateMemberTags(Long memberId, List<Long> tagIds);
    
    /**
     * 获取所有标签列表
     */
    List<UmsMemberTag> getAllTags();
    
    /**
     * 创建新标签
     */
    int createTag(String name, String description, Boolean tagType, String color);
    
    /**
     * 更新标签
     */
    int updateTag(Long tagId, String name, String description, String color);
    
    /**
     * 更新标签状态
     */
    int updateTagStatus(Long tagId, Boolean status);
    
    /**
     * 删除标签
     */
    int deleteTag(Long tagId);
    
    /**
     * 给用户绑定标签
     */
    int bindMemberTag(Long memberId, Long tagId);
    
    /**
     * 解除用户标签绑定
     */
    int unbindMemberTag(Long memberId, Long tagId);
    
    /**
     * 批量给用户打标签
     */
    int batchBindTags(List<Long> memberIds, List<Long> tagIds);
    
    /**
     * 批量调整用户积分
     */
    int batchAdjustIntegration(List<Long> memberIds, Integer integration, Integer operationType, String reason);
    
    /**
     * 批量发放优惠券
     */
    int batchSendCoupon(List<Long> memberIds, Long couponId, String reason);
    
    /**
     * 导出用户数据
     */
    List<UmsMemberListVO> exportMemberData(UmsMemberQueryParam queryParam);
    
    // =============== 签到记录管理 ===============
    
    /**
     * 获取用户签到记录列表
     * @param memberId 用户ID
     * @param startDate 开始日期（格式：yyyy-MM-dd）
     * @param endDate 结束日期（格式：yyyy-MM-dd）
     * @param status 签到状态（1:正常签到, 2:补签）
     * @param pageSize 每页条数
     * @param pageNum 页码
     * @return 签到记录列表
     */
    List<UmsMemberSigninLog> getSignInHistory(Long memberId, String startDate, String endDate, Integer status, Integer pageSize, Integer pageNum);
    
    /**
     * 获取用户签到统计信息
     * @param memberId 用户ID
     * @return 签到统计信息（总签到天数、连续签到天数、本月签到天数、累计获得积分等）
     */
    Map<String, Object> getSignInSummary(Long memberId);
    
    /**
     * 管理员为用户补签
     * @param memberId 用户ID
     * @param signinDate 补签日期（格式：yyyy-MM-dd）
     * @param reason 补签原因
     * @return 操作结果
     */
    int supplementSignIn(Long memberId, String signinDate, String reason);
} 