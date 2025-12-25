import request from '@/utils/requestUtil';

// 创建商品评价
export function createComment(data) {
  return request({
    url: '/comment/create',
    method: 'POST',
    data: data
  });
}

// 根据商品ID获取评价列表
export function getCommentsByProductId(productId, pageNum = 1, pageSize = 10) {
  return request({
    url: `/comment/product/${productId}`,
    method: 'GET',
    data: {
      pageNum: pageNum,
      pageSize: pageSize
    }
  });
}

// 根据会员获取评价列表
export function getCommentsByMember(memberNickName, pageNum = 1, pageSize = 10) {
  return request({
    url: `/comment/member/${memberNickName}`,
    method: 'GET',
    data: {
      pageNum: pageNum,
      pageSize: pageSize
    }
  });
}

// 检查是否已评价（根据昵称）
export function checkCommented(orderId, productId, memberNickName) {
  return request({
    url: '/comment/check',
    method: 'GET',
    data: {
      orderId: orderId,
      productId: productId,
      memberNickName: memberNickName
    }
  });
}

// 检查当前用户是否已评价
export function checkCurrentUserCommented(orderId, productId) {
  return request({
    url: '/comment/check/current',
    method: 'GET',
    data: {
      orderId: orderId,
      productId: productId
    }
  });
} 