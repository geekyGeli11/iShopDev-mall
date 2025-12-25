import request from '@/utils/request';

// 获取溯源列表（带筛选和分页）
export function fetchTraceabilityList(params) {
  return request({
    url: '/traceability/list',
    method: 'get',
    params: params,
  });
}

// 创建溯源记录
export function createTraceability(data) {
  return request({
    url: '/traceability/create',
    method: 'post',
    data: data,
  });
}

// 更新溯源记录
export function updateTraceability(id, data) {
    return request({
      url: `/traceability/update/${id}`,
      method: 'post',
      headers: {
        'Content-Type': 'application/json',  // 确保请求体是 JSON 格式
      },
      data: data,  // 传递的请求数据，会被转为 JSON 格式
    });
  }  

// 删除溯源记录
export function deleteTraceability(id) {
  return request({
    url: `/traceability/delete/${id}`,
    method: 'post',
  });
}

// 根据 ID 获取溯源记录
export function getTraceability(id) {
  return request({
    url: `/traceability/${id}`,
    method: 'get',
  });
}

// 按创建时间范围查询溯源记录
export function fetchByCreateTimeRange(params) {
  return request({
    url: '/traceability/search',
    method: 'get',
    params: params,
  });
}

// 获取所有溯源记录
export function fetchAllTraceability() {
  return request({
    url: '/traceability/all',
    method: 'get',
  });
}
