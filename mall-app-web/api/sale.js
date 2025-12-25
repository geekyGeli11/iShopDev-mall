import request from '@/utils/requestUtil.js'

/**
 * 获取销售单详情（需要验证权限）
 * @param {Number} saleId 销售单ID
 */
export function getSaleDetail(saleId) {
	return request({
		url: `/sale/detail/${saleId}`,
		method: 'GET'
	})
}

/**
 * 检查是否有权限查看销售单
 * @param {Number} saleId 销售单ID
 */
export function checkSaleAuth(saleId) {
	return request({
		url: `/sale/checkAuth/${saleId}`,
		method: 'GET'
	})
}
