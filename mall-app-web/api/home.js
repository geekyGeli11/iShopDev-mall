import request from '@/utils/requestUtil'

export function fetchContent(params) {
	return request({
		method: 'GET',
		url: '/home/content',
		params: params
	})
}

export function fetchRecommendProductList(params) {
	return request({
		method: 'GET',
		url: '/home/recommendProductList',
		params:params
	})
}

export function fetchProductCateList(parentId) {
	return request({
		method: 'GET',
		url: '/home/productCateList/'+parentId,
	})
}

export function fetchNewProductList(params) {
	return request({
		method: 'GET',
		url: '/home/newProductList',
		params:params
	})
}

export function fetchHotProductList(params) {
	return request({
		method: 'GET',
		url: '/home/hotProductList',
		params:params
	})
}

export function fetchSubjectDetail(params) {
	return request({
		method: 'GET',
		url: '/home/subjectDetail',
		params:params
	})
}

export function fetchProductListBySales(params) {
	return request({
		method: 'GET',
		url: '/home/productListBySales',
		params:params
	})
}

