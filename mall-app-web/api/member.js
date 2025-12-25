import request from '@/utils/requestUtil'

export function memberLogin(data) {
	return request({
		method: 'POST',
		url: '/sso/login',
		header: {
			'content-type': 'application/x-www-form-urlencoded;charset=utf-8'
		},
		data: data
	})
}

export function wechatLogin(data) {
	return request({
		method: 'POST',
		url: '/sso/wechatLogin',
		header: {
			'content-type': 'application/x-www-form-urlencoded;charset=utf-8'
		},
		data: data
	})
}

export function memberInfo() {
	return request({
		method: 'GET',
		url: '/sso/info'
	})
}

// 获取礼物数量
export function getMemberGiftCount() {
	return request({
		url: '/member/gift/count',
		method: 'get'
	})
}

export function loginByPhone(data) {
	return request({
		method: 'POST',
		url: '/sso/loginByPhone',
		header: {
			'content-type': 'application/x-www-form-urlencoded;charset=utf-8'
		},
		data: data
	})
}

export function getPhoneNumber(data) {
	return request({
		method: 'POST',
		url: '/sso/getPhoneNumber',
		header: {
			'content-type': 'application/x-www-form-urlencoded;charset=utf-8'
		},
		data: data
	})
}

export function registerMember(data) {
	return request({
		method: 'POST',
		url: '/sso/register',
		data: data
	})
}

export function uploadMemberAvatar(file) {
	return request({
		method: 'POST',
		url: '/member/uploadAvatar',
		header: {
			'content-type': 'multipart/form-data'
		},
		data: file
	})
}

// 更新用户信息
export function updateMemberInfo(data) {
	return request({
		method: 'POST',
		url: '/member/update',
		data: data
	})
}

// 上传背景图片
export function uploadBackgroundImage(file) {
	return request({
		method: 'POST',
		url: '/member/uploadBackground',
		header: {
			'content-type': 'multipart/form-data'
		},
		data: file
	})
}

// 获取会员二维码
export function getMemberQRCode() {
	return request({
		method: 'GET',
		url: '/sso/memberQRCode'
	})
}

