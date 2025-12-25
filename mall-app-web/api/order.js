import request from '@/utils/requestUtil'

export function generateConfirmOrder(data, storeId) {
	const params = {};
	if (storeId) {
		params.storeId = storeId;
	}
	
	return request({
		method: 'POST',
		url: '/order/generateConfirmOrder',
		params: params,
		data: data
	})
}

export function generateDirectConfirmOrder(data, storeId) {
	const params = {};
	if (storeId) {
		params.storeId = storeId;
	}
	
	return request({
		method: 'POST',
		url: '/order/generateDirectConfirmOrder',
		params: params,
		header: {
			'content-type': 'application/json;charset=utf-8'
		},
		data: data
	})
}

export function generateOrder(data) {
	return request({
		method: 'POST',
		url: '/order/generateOrder',
		header: {
			'content-type': 'application/json;charset=utf-8'
		},
		data: data
	})
}

export function generateGiftOrder(data) {
	return request({
		method: 'POST',
		url: '/order/generateGiftOrder',
		header: {
			'content-type': 'application/json;charset=utf-8'
		},
		data: data
	})
}

export function fetchOrderList(params) {
	return request({
		method: 'GET',
		url: '/order/list',
		params: params
	})
}

export function payOrderSuccess(data) {
	return request({
		method: 'POST',
		url: '/order/paySuccess',
		header: {
			'content-type': 'application/x-www-form-urlencoded;charset=utf-8'
		},
		data: data
	})
}

export function fetchOrderDetail(orderId) {
	return request({
		method: 'GET',
		url: `/order/detail/${orderId}`
	})
}

export function fetchOrderLogistics(orderId) {
	return request({
		method: 'GET',
		url: `/order/${orderId}/logistics`
	})
}

export function cancelUserOrder(data) {
	return request({
		method: 'POST',
		url: '/order/cancelUserOrder',
		header: {
			'content-type': 'application/x-www-form-urlencoded;charset=utf-8'
		},
		data: data
	})
}

export function confirmReceiveOrder(data) {
	return request({
		method: 'POST',
		url: '/order/confirmReceiveOrder',
		header: {
			'content-type': 'application/x-www-form-urlencoded;charset=utf-8'
		},
		data: data
	})
}

export function deleteUserOrder(data) {
	return request({
		method: 'POST',
		url: '/order/deleteOrder',
		header: {
			'content-type': 'application/x-www-form-urlencoded;charset=utf-8'
		},
		data: data
	})
}

export function fetchAliapyStatus(params) {
	return request({
		method: 'GET',
		url: '/alipay/query',
		params: params
	})
}

export function getWechatPayParams(data) {
	return request({
		method: 'POST',
		url: '/wxpay/pay',
		header: {
			'content-type': 'application/json;charset=utf-8'
		},
		data: data
	})
}

export function getPickupQRCode(pickupCode) {
	return request({
		method: 'GET',
		url: `/order/qrcode/${pickupCode}`
	})
}

// 申请退货
export function createReturnApply(data) {
	return request({
		method: 'POST',
		url: '/returnApply/createMultiStep',
		header: {
			'content-type': 'application/json;charset=utf-8'
		},
		data: data
	})
}

// 获取退货原因列表
export function fetchReturnReasonList() {
	return request({
		method: 'GET',
		url: '/returnApply/returnReasons'
	})
}

// 获取订单的退货申请列表
export function fetchOrderReturnApplyList(orderId) {
	return request({
		method: 'GET',
		url: `/returnApply/order/${orderId}`
	})
}

// 更新退货申请快递信息
export function updateReturnApplyDelivery(data) {
	return request({
		method: 'POST',
		url: '/returnApply/updateDelivery',
		header: {
			'content-type': 'application/json;charset=utf-8'
		},
		data: data
	})
}

// 获取退货申请详情
export function fetchOrderReturnApplyDetail(returnApplyId) {
	return request({
		method: 'GET',
		url: `/returnApply/detail/${returnApplyId}`
	})
}

// 取消退货申请
export function cancelReturnApply(returnApplyId) {
	return request({
		method: 'POST',
		url: `/returnApply/cancel/${returnApplyId}`
	})
}

// 获取用户的退货申请列表
export function fetchUserReturnApplyList() {
	return request({
		method: 'GET',
		url: '/returnApply/userList'
	})
}