import request from '@/utils/request'
export function fetchList(params) {
  return request({
    url:'/order/list',
    method:'get',
    params:params
  })
}

export function closeOrder(params) {
  return request({
    url:'/order/update/close',
    method:'post',
    params:params
  })
}

export function deleteOrder(params) {
  return request({
    url:'/order/delete',
    method:'post',
    params:params
  })
}

export function deliveryOrder(data) {
  return request({
    url:'/order/update/delivery',
    method:'post',
    data:data
  });
}

export function getOrderDetail(id) {
  return request({
    url:'/order/'+id,
    method:'get'
  });
}

export function getOrderLogisticsInfo(id) {
  return request({
    url:'/order/'+id + '/logistics',
    method:'get'
  });
}

export function updateReceiverInfo(data) {
  return request({
    url:'/order/update/receiverInfo',
    method:'post',
    data:data
  });
}

export function updateMoneyInfo(data) {
  return request({
    url:'/order/update/moneyInfo',
    method:'post',
    data:data
  });
}

export function updateOrderNote(params) {
  return request({
    url:'/order/update/note',
    method:'post',
    params:params
  })
}

export function pickupOrder(params) {
  return request({
    url:'/order/pickup',
    method:'post',
    params:params
  })
}

export function exportOrder(params) {
  return request({
    url:'/order/export',
    method:'get',
    params:params,
    responseType:'blob'
  })
}

export function batchDelivery(data) {
  return request({
    url: '/order/batchDelivery',
    method: 'post',
    data: data
  })
}

export function downloadDeliveryTemplate() {
  return request({
    url: '/order/deliveryTemplate',
    method: 'get',
    responseType: 'blob'
  })
}

export function getStoreStockList(skuId) {
  return request({
    url: '/order/storeStock/' + skuId,
    method: 'get'
  })
}

export function changeDeliveryStore(data) {
  return request({
    url: '/order/changeDeliveryStore',
    method: 'post',
    data: data
  })
}

export function refundOrder(data) {
  return request({
    url: '/order/refund',
    method: 'post',
    data: data
  })
}

export function canRefundOrder(orderId) {
  return request({
    url: '/order/canRefund/' + orderId,
    method: 'get'
  })
}
