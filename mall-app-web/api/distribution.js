/**
 * 分销相关API
 */
import request from '@/utils/requestUtil'

// ========== 分销商申请相关 ==========

// 获取分销商申请状态
export function getDistributorApplyStatus() {
	return request({
		method: 'GET',
		url: '/api/distribution/apply/status'
	})
}

// 提交分销商申请
export function submitDistributorApply(data) {
	return request({
		method: 'POST',
		url: '/api/distribution/apply',
		data: data
	})
}

// 获取分销商申请详情
export function getDistributorApplyDetail() {
	return request({
		method: 'GET',
		url: '/api/distribution/apply/detail'
	})
}

// 检查申请资格
export function checkApplyEligibility() {
	return request({
		method: 'GET',
		url: '/api/distribution/apply/check-eligibility'
	})
}

// 获取分销商申请要求
export function getDistributorRequirements() {
	return request({
		method: 'GET',
		url: '/api/distribution/apply/requirements'
	})
}

// 提交补充申请信息
export function submitSupplementaryInfo(data) {
	return request({
		method: 'POST',
		url: '/api/distribution/apply/supplement',
		data
	})
}

// ========== 分销推广相关 ==========

// 获取我的分销统计
export function getMyDistributionStats() {
	return request({
		method: 'GET',
		url: '/api/distribution/my-stats'
	})
}

// 获取我的客户列表
export function getMyCustomers(params) {
	return request({
		method: 'GET',
		url: '/api/distribution/my-customers',
		params: params
	})
}

// 获取我的客户统计
export function getMyCustomerStats() {
	return request({
		method: 'GET',
		url: '/api/distribution/my-customer-stats'
	})
}

// 获取我的佣金记录
export function getMyCommissionRecords(params) {
	return request({
		method: 'GET',
		url: '/api/distribution/my-commission-records',
		params: params
	})
}

// 获取我的佣金汇总
export function getMyCommissionSummary(params) {
	return request({
		method: 'GET',
		url: '/api/distribution/my-commission-summary',
		params: params
	})
}

// 生成分销码
export function generateDistributionCode() {
	return request({
		method: 'POST',
		url: '/api/distribution/generate-code'
	})
}

// 获取我的分销码
export function getMyDistributionCode() {
	return request({
		method: 'GET',
		url: '/api/distribution/my-code'
	})
}

// 绑定分销关系
export function bindDistributionRelation(data) {
	return request({
		method: 'POST',
		url: '/api/distribution/bind-relation',
		data: data
	})
}

// ========== 提现相关 ==========

// 获取可提现金额
export function getAvailableAmount() {
	return request({
		method: 'GET',
		url: '/api/withdraw/available-amount'
	})
}

// 获取提现配置
export function getWithdrawConfig() {
	return request({
		method: 'GET',
		url: '/api/withdraw/config'
	})
}

// 申请提现
export function applyWithdraw(data) {
	return request({
		method: 'POST',
		url: '/api/withdraw/apply',
		data: data
	})
}

// 获取我的提现记录
export function getMyWithdrawRecords(params) {
	return request({
		method: 'GET',
		url: '/api/withdraw/my-records',
		params: params
	})
}

// 获取提现申请详情
export function getWithdrawDetail(id) {
	return request({
		method: 'GET',
		url: `/api/withdraw/detail/${id}`
	})
}

// 取消提现申请
export function cancelWithdraw(id) {
	return request({
		method: 'POST',
		url: `/api/withdraw/cancel/${id}`
	})
}

// 获取提现统计
export function getWithdrawStatistics() {
	return request({
		method: 'GET',
		url: '/api/withdraw/statistics'
	})
}

// ========== 推广海报相关 ==========

// 生成推广海报
export function generatePoster(data) {
	return request({
		method: 'POST',
		url: '/api/poster/generate',
		data: data
	})
}

// 获取海报模板列表
export function getPosterTemplates() {
	return request({
		method: 'GET',
		url: '/api/poster/templates'
	})
}

// 获取我的海报列表
export function getMyPosters(params) {
	return request({
		method: 'GET',
		url: '/api/poster/my-posters',
		params: params
	})
}

// 删除海报
export function deletePoster(id) {
	return request({
		method: 'DELETE',
		url: `/api/poster/delete/${id}`
	})
}

// 获取海报详情
export function getPosterDetail(id) {
	return request({
		method: 'GET',
		url: `/api/poster/detail/${id}`
	})
}

// 分享海报
export function sharePoster(id) {
	return request({
		method: 'POST',
		url: `/api/poster/share/${id}`
	})
}

// 申请佣金提现
export function applyCommissionWithdraw(data) {
	return request({
		method: 'POST',
		url: '/api/commission/withdraw',
		data: data
	})
}

// 获取提现历史
export function getWithdrawHistory(params) {
	return request({
		method: 'GET',
		url: '/api/commission/withdraw/history',
		params: params
	})
}

// 生成推广海报
export function generatePromotionPoster(data) {
	return request({
		method: 'POST',
		url: '/api/distribution/generate-poster',
		data: data
	})
} 