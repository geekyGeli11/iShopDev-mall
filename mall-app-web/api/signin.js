import request from '@/utils/requestUtil'

/**
 * 获取签到状态
 */
export function getSigninStatus() {
	return request({
		method: 'GET',
		url: '/signin/status'
	})
}

/**
 * 获取签到日历
 * @param {Object} params - 查询参数
 * @param {String} params.year - 年份
 * @param {String} params.month - 月份
 */
export function getSigninCalendar(params) {
	return request({
		method: 'GET',
		url: '/signin/calendar',
		data: params
	})
}

/**
 * 执行签到
 */
export function checkin() {
	return request({
		method: 'POST',
		url: '/signin/checkin',
		header: {
			'content-type': 'application/x-www-form-urlencoded;charset=utf-8'
		}
	})
}

/**
 * 获取签到奖励
 */
export function getSigninReward() {
	return request({
		method: 'GET',
		url: '/signin/reward'
	})
}

/**
 * 领取连续签到奖励
 */
export function claimContinuousReward() {
	return request({
		method: 'POST',
		url: '/signin/claimReward',
		header: {
			'content-type': 'application/x-www-form-urlencoded;charset=utf-8'
		}
	})
}

/**
 * 获取签到配置信息
 */
export function getSigninConfig() {
	return request({
		method: 'GET',
		url: '/signin/config'
	})
}

/**
 * 获取签到统计数据
 */
export function getSigninStatistics() {
	return request({
		method: 'GET',
		url: '/signin/statistics'
	})
} 