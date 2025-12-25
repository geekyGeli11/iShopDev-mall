import request from '@/utils/requestUtil'

// 获取团队统计数据
export function getTeamStatistics() {
	return request({
		method: 'GET',
		url: '/member/team/statistics'
	})
}

// 获取团队成员列表
export function getTeamMembers(params) {
	return request({
		method: 'GET',
		url: '/member/team/list',
		params: params
	})
}

// 获取团队分享二维码
export function getTeamQrCode() {
	return request({
		method: 'GET',
		url: '/member/team/qrcode',
		responseType: 'arraybuffer'
	})
} 