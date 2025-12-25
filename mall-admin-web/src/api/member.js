import request from '@/utils/request'

// =============== 用户基础管理API ===============

/**
 * 分页查询小程序用户列表
 */
export function fetchList(params) {
  return request({
    url: '/member/list',
    method: 'get',
    params: params
  })
}

/**
 * 获取用户详细信息
 */
export function getDetail(id) {
  return request({
    url: `/member/detail/${id}`,
    method: 'get'
  })
}

/**
 * 更新用户信息
 */
export function updateMember(id, data) {
  return request({
    url: `/member/update/${id}`,
    method: 'post',
    data: data
  })
}

/**
 * 更新用户状态
 */
export function updateStatus(id, status) {
  return request({
    url: `/member/updateStatus/${id}`,
    method: 'post',
    params: { status }
  })
}

/**
 * 删除用户
 */
export function deleteMember(id) {
  return request({
    url: `/member/delete/${id}`,
    method: 'post'
  })
}

/**
 * 获取用户统计信息
 */
export function getStatistics() {
  return request({
    url: '/member/statistics',
    method: 'get'
  })
}

/**
 * 导出用户数据
 */
export function exportMemberData(params) {
  return request({
    url: '/member/export',
    method: 'get',
    params: params
  })
}

// =============== 积分管理API ===============

/**
 * 发放积分给用户
 */
export function giveIntegration(memberId, integration, reason) {
  return request({
    url: '/member/integration/give',
    method: 'post',
    params: { memberId, integration, reason }
  })
}

/**
 * 扣减用户积分
 */
export function deductIntegration(memberId, integration, reason) {
  return request({
    url: '/member/integration/deduct',
    method: 'post',
    params: { memberId, integration, reason }
  })
}

/**
 * 获取积分变更历史
 */
export function getIntegrationHistory(id, params) {
  return request({
    url: `/member/integration/history/${id}`,
    method: 'get',
    params: params
  })
}

// =============== 签到记录API ===============

/**
 * 获取用户签到记录列表
 */
export function getSignInHistory(params) {
  return request({
    url: '/member/signin/history',
    method: 'get',
    params: params
  })
}

/**
 * 获取用户签到统计信息
 */
export function getSignInSummary(memberId) {
  return request({
    url: `/member/signin/summary/${memberId}`,
    method: 'get'
  })
}

/**
 * 管理员为用户补签
 */
export function supplementSignIn(memberId, signinDate, reason) {
  return request({
    url: '/member/signin/supplement',
    method: 'post',
    params: { memberId, signinDate, reason }
  })
}

/**
 * 获取积分汇总信息
 */
export function getIntegrationSummary(id) {
  return request({
    url: `/member/integration/summary/${id}`,
    method: 'get'
  })
}

/**
 * 批量调整用户积分
 */
export function batchAdjustIntegration(memberIds, integration, operationType, reason) {
  return request({
    url: '/member/integration/batch',
    method: 'post',
    params: { memberIds, integration, operationType, reason }
  })
}

// =============== 优惠券管理API ===============

/**
 * 获取优惠券使用历史
 */
export function getCouponHistory(id, params) {
  return request({
    url: `/member/coupon/history/${id}`,
    method: 'get',
    params: params
  })
}

/**
 * 发放优惠券给用户
 */
export function sendCoupon(memberId, couponId, reason) {
  return request({
    url: '/member/coupon/send',
    method: 'post',
    params: { memberId, couponId, reason }
  })
}

/**
 * 获取可发放的优惠券列表
 */
export function getAvailableCoupons(params) {
  return request({
    url: '/member/coupon/available',
    method: 'get',
    params: params
  })
}

/**
 * 批量发放优惠券
 */
export function batchSendCoupon(memberIds, couponId, reason) {
  return request({
    url: '/member/coupon/batch-send',
    method: 'post',
    params: { memberIds, couponId, reason }
  })
}

// =============== 订单查询API ===============

/**
 * 获取用户订单列表
 */
export function getMemberOrders(id, params) {
  return request({
    url: `/member/orders/${id}`,
    method: 'get',
    params: params
  })
}

/**
 * 获取用户订单统计
 */
export function getMemberOrderStatistics(id) {
  return request({
    url: `/member/orders/statistics/${id}`,
    method: 'get'
  })
}

// =============== 邀请数据API ===============

/**
 * 获取用户邀请汇总信息
 */
export function getInviteSummary(id) {
  return request({
    url: `/member/invite/summary/${id}`,
    method: 'get'
  })
}

/**
 * 获取用户邀请关系列表
 */
export function getInviteRelations(id, params) {
  return request({
    url: `/member/invite/relations/${id}`,
    method: 'get',
    params: params
  })
}

/**
 * 获取用户邀请奖励记录
 */
export function getInviteRewards(id, params) {
  return request({
    url: `/member/invite/rewards/${id}`,
    method: 'get',
    params: params
  })
}

/**
 * 获取用户提现申请记录
 */
export function getWithdrawApplies(id, params) {
  return request({
    url: `/member/invite/withdraws/${id}`,
    method: 'get',
    params: params
  })
}

// =============== 用户画像API ===============

/**
 * 获取用户画像信息
 */
export function getMemberProfile(id) {
  return request({
    url: `/member/profile/${id}`,
    method: 'get'
  })
}

/**
 * 重新生成用户画像
 */
export function generateMemberProfile(id) {
  return request({
    url: `/member/profile/generate/${id}`,
    method: 'post'
  })
}

/**
 * 获取用户标签
 */
export function getMemberTags(id) {
  return request({
    url: `/member/tags/${id}`,
    method: 'get'
  })
}

/**
 * 更新用户标签
 */
export function updateMemberTags(id, tagIds) {
  return request({
    url: `/member/tags/update/${id}`,
    method: 'post',
    params: { tagIds }
  })
}

// =============== 标签管理API ===============

/**
 * 获取所有标签列表
 */
export function getAllTags() {
  return request({
    url: '/member/tags/list',
    method: 'get'
  })
}

/**
 * 创建新标签
 */
export function createTag(name, description, tagType, color) {
  return request({
    url: '/member/tags/create',
    method: 'post',
    params: { name, description, tagType, color }
  })
}

/**
 * 更新标签
 */
export function updateTag(tagId, name, description, color) {
  return request({
    url: `/member/tags/update/${tagId}`,
    method: 'put',
    params: { name, description, color }
  })
}

/**
 * 更新标签状态
 */
export function updateTagStatus(tagId, status) {
  return request({
    url: `/member/tags/status/${tagId}`,
    method: 'put',
    params: { status }
  })
}

/**
 * 删除标签
 */
export function deleteTag(tagId) {
  return request({
    url: `/member/tags/delete/${tagId}`,
    method: 'delete'
  })
}

/**
 * 给用户绑定标签
 */
export function bindMemberTag(memberId, tagId) {
  return request({
    url: `/member/tags/bind/${memberId}`,
    method: 'post',
    params: { tagId }
  })
}

/**
 * 解除用户标签绑定
 */
export function unbindMemberTag(memberId, tagId) {
  return request({
    url: `/member/tags/unbind/${memberId}`,
    method: 'post',
    params: { tagId }
  })
}

/**
 * 批量给用户打标签
 */
export function batchBindTags(memberIds, tagIds) {
  return request({
    url: '/member/tags/batch/bind',
    method: 'post',
    params: { memberIds, tagIds }
  })
}

// =============== 函数别名（为了兼容组件中的不同命名） ===============

// 用户详情别名
export const getMemberDetail = getDetail

// 积分管理别名
export const getMemberIntegrationHistory = getIntegrationHistory
export const giveMemberIntegration = giveIntegration
export const deductMemberIntegration = deductIntegration

// 优惠券管理别名
export const getMemberCouponHistory = getCouponHistory
export const sendMemberCoupon = sendCoupon

// 邀请数据别名
export const getMemberInviteSummary = getInviteSummary
export const getMemberInviteRelations = getInviteRelations
export const getMemberInviteRewards = getInviteRewards
export const getMemberInviteWithdraw = getWithdrawApplies

// 获取用户充值记录（用于用户详情页面）
export function getMemberRechargeHistory(params) {
  return request({
    url: '/member/rechargeHistory',
    method: 'get',
    params: params
  })
}

// 获取用户消费记录（用于用户详情页面）
export function getMemberConsumptionHistory(params) {
  return request({
    url: '/member/consumptionHistory',
    method: 'get',
    params: params
  })
}