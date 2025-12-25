/**
 * 邀请相关API
 */
import request from '@/utils/requestUtil'

// 生成邀请参数
export function generateInviteParam(data) {
	return request({
		method: 'POST',
		url: '/api/invite/generate',
		data: data
	})
}

// 验证邀请参数
export function verifyInviteParam(data) {
	return request({
		method: 'POST',
		url: '/api/invite/verify',
		data: data
	})
}

// 建立邀请关系
export function establishInviteRelation(data) {
	return request({
		method: 'POST',
		url: '/api/invite/establish',
		header: {
			'content-type': 'application/x-www-form-urlencoded;charset=utf-8'
		},
		data: data
	})
}

// 获取我的邀请统计
export function getMyInviteStatistics() {
	return request({
		method: 'GET',
		url: '/api/invite/my-statistics'
	})
}

// 获取我的奖励记录
export function getMyRewardRecords(params) {
	return request({
		method: 'GET',
		url: '/api/invite/my-rewards',
		params: params
	})
}

// 获取奖励汇总
export function getRewardSummary(params) {
	return request({
		method: 'GET',
		url: '/api/invite/reward-summary',
		params: params
	})
}

// 生成小程序码
export function generateQrCode(data) {
	return request({
		method: 'POST',
		url: '/api/wechat/generate-qrcode',
		header: {
			'content-type': 'application/x-www-form-urlencoded;charset=utf-8'
		},
		data: data
	})
}

// 申请提现
export function applyWithdraw(data) {
	return request({
		method: 'POST',
		url: '/api/invite/apply-withdraw',
		data: data
	})
}

// 获取提现配置
export function getWithdrawConfig() {
	return request({
		method: 'GET',
		url: '/api/invite/withdraw-config'
	})
}