import request from '@/utils/requestUtil'

/**
 * 获取风格模型卡片列表
 */
export function fetchStyleModelCards() {
	return request({
		method: 'GET',
		url: '/styleModel/cards'
	})
}

/**
 * 根据风格模型ID获取关联商品
 */
export function fetchStyleModelProducts(styleModelId, params) {
	return request({
		method: 'GET',
		url: `/styleModel/${styleModelId}/products`,
		params: params
	})
}

/**
 * 获取风格模型关联的商品列表（用于弹窗展示）
 */
export function getStyleModelProductList(styleModelId, params = {}) {
	return request({
		method: 'GET',
		url: `/styleModel/${styleModelId}/productList`,
		params: {
			page: params.page || 1,
			pageSize: params.pageSize || 20,
			category: params.category || '',
			...params
		}
	})
}

/**
 * 获取商品的DIY模板和定制信息
 */
export function getProductDIYTemplate(productId) {
	return request({
		method: 'GET',
		url: `/diy/product/${productId}/template`
	})
}

/**
 * 获取商品的可定制面信息
 */
export function getProductCustomizableAreas(productId) {
	return request({
		method: 'GET',
		url: `/diy/product/${productId}/customizableAreas`
	})
}

/**
 * 获取风格模型详情
 */
export function getStyleModelDetail(styleModelId) {
	return request({
		method: 'GET',
		url: `/styleModel/${styleModelId}`
	})
}
