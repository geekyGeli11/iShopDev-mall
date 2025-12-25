import request from '@/utils/requestUtil'

// 获取门店列表
export function fetchStoreList(params = {}) {
  return request({
    method: 'GET',
    url: '/storeAddress/list',
    data: params
  })
}

// 根据学校ID获取门店列表
export function fetchStoreListBySchool(schoolId) {
  return request({
    method: 'GET',
    url: '/storeAddress/listBySchool',
    data: {
      schoolId
    }
  })
}

// 搜索门店
export function searchStores(keyword, schoolId = null) {
  const params = { keyword };

  // 只有当schoolId不为null且不为undefined时才添加到参数中
  if (schoolId !== null && schoolId !== undefined) {
    params.schoolId = schoolId;
  }

  return request({
    method: 'GET',
    url: '/storeAddress/search',
    data: params
  })
}

// 根据距离查询附近门店
export function fetchNearbyStores(longitude, latitude, radius = 10) {
  return request({
    method: 'GET',
    url: '/storeAddress/nearby',
    data: {
      longitude,
      latitude,
      radius
    }
  })
}

// 获取默认门店地址
export function fetchDefaultStore() {
  return request({
    method: 'GET',
    url: '/storeAddress/default'
  })
}

// 获取按学校分组的门店列表
export function fetchStoreGroupsBySchool() {
  return request({
    method: 'GET',
    url: '/storeAddress/listGroupBySchool'
  })
}