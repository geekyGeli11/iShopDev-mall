import request from '@/utils/requestUtil'

/**
 * 获取组合商品列表
 */
export function fetchBundleList(params) {
	return request({
		method: 'GET',
		url: '/bundle/list',
		params: params
	})
}

/**
 * 获取组合商品详情
 */
export function fetchBundleDetail(id, storeId) {
	const params = {}
	if (storeId) {
		params.storeId = storeId
	}
	return request({
		method: 'GET',
		url: '/bundle/detail/' + id,
		params: params
	})
}

/**
 * 生成组合商品确认订单
 */
export function generateBundleConfirmOrder(data) {
	return request({
		method: 'POST',
		url: '/bundle/generateConfirmOrder',
		data: data
	})
}

/**
 * 创建组合商品订单
 */
export function createBundleOrder(data) {
	return request({
		method: 'POST',
		url: '/bundle/createOrder',
		data: data
	})
}
