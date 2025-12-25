import request from '@/utils/requestUtil'

// 获取我的礼物列表
export function getMyGifts(params) {
  return request({
    url: '/member/gift',
    method: 'get',
    params
  })
}

// 获取我赠送的礼物
export function getSentGifts(params) {
  return request({
    url: '/member/gift/sent',
    method: 'get',
    params
  })
}

// 获取我收到的礼物
export function getReceivedGifts(params) {
  return request({
    url: '/member/gift/received',
    method: 'get',
    params
  })
}

// 分享礼物给好友
export function shareGift(data) {
  return request({
    url: '/member/gift/share',
    method: 'post',
    data
  })
}

// 更新礼物记录的收礼人ID
export function updateReceiverId(id, receiverId) {
  return request({
    url: '/member/gift/updateReceiver',
    method: 'post',
    params: {
      id,
      receiverId
    }
  })
}

// 获取祝福语列表
export function getWishList(params) {
  return request({
    url: '/member/gift/wishes',
    method: 'get',
    params
  })
}

// 获取礼物详情
export function getGiftDetail(giftRecordId) {
  return request({
    url: `/member/gift/orderDetail/${giftRecordId}`,
    method: 'get'
  })
}

// 接收礼物
export function receiveGift(giftRecordId) {
  return request({
    url: '/member/gift/receive',
    method: 'post',
    header: {
      'content-type': 'application/x-www-form-urlencoded'
    },
    data: {
      giftRecordId
    }
  })
}

// 更新送礼订单收货地址
export function updateGiftAddress(giftRecordId, memberReceiveAddressId) {
  return request({
    url: '/member/gift/updateAddress',
    method: 'post',
    header: {
      'content-type': 'application/x-www-form-urlencoded'
    },
    data: {
      giftRecordId,
      memberReceiveAddressId
    }
  })
} 