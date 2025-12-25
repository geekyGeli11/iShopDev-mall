import request from '@/utils/requestUtil'

// 获取当前商品可领取优惠券
export function fetchProductCouponList(productId) {
	return request({
		method: 'GET',
		url: `/member/coupon/listByProduct/${productId}`,
	})
}
// 领取优惠券
export function addMemberCoupon(couponId) {
	return request({
		method: 'POST',
		url: `/member/coupon/add/${couponId}`,
	})
}
// 获取会员已领取优惠券
export function fetchMemberCouponList(useStatus, schoolId) {
	const params = {useStatus: useStatus};
	if (schoolId !== undefined && schoolId !== null) {
		params.schoolId = schoolId;
	}
	return request({
		method: 'GET',
		url: '/member/coupon/list',
		params: params
	})
}
// 获取可领取优惠券
export function fetchCouponList(useStatus, schoolId) {
	const params = {useStatus: useStatus};
	if (schoolId !== undefined && schoolId !== null) {
		params.schoolId = schoolId;
	}
	return request({
		method: 'GET',
		url: '/member/coupon/listAvailable',
		params: params
	})
}
// 获取优惠券详情
export function fetchCouponDetail(couponId) {
	return request({
		method: 'GET',
		url: `/member/coupon/detail/${couponId}`,
	})
}