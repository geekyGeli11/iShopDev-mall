import request from '@/utils/request';

// 获取溯源与产品关系列表（带筛选和分页）
export function fetchTraceabilityProductRelationList(params) {
  return request({
    url: '/traceabilityProductRelation/list',
    method: 'get',
    params: params,
  });
}

// 创建溯源与产品关系记录（支持批量创建）
export function createTraceabilityProductRelation(data) {
  return request({
    url: '/traceabilityProductRelation/create',
    method: 'post',
    data: data,
  });
}

// 更新溯源与产品关系记录
export function updateTraceabilityProductRelation(id, data) {
  return request({
    url: `/traceabilityProductRelation/update/${id}`,
    method: 'post',
    headers: {
      'Content-Type': 'application/json', // 确保请求体是 JSON 格式
    },
    data: data, // 请求数据会被自动转为 JSON 格式
  });
}

// 删除溯源与产品关系记录
export function deleteTraceabilityProductRelation(id) {
  return request({
    url: `/traceabilityProductRelation/delete/${id}`,
    method: 'post',
  });
}

// 根据 ID 获取溯源与产品关系记录
export function getTraceabilityProductRelation(id) {
  return request({
    url: `/traceabilityProductRelation/${id}`,
    method: 'get',
  });
}

// 按溯源 ID 获取溯源与产品关系记录
export function fetchTraceabilityProductRelationsByTraceabilityId(traceabilityId) {
  return request({
    url: '/traceabilityProductRelation/list',
    method: 'get',
    params: { traceabilityId },
  });
}

// 获取溯源与产品关系记录的总数
export function fetchTraceabilityProductRelationCount(traceabilityId) {
  return request({
    url: '/traceabilityProductRelation/count',
    method: 'get',
    params: { traceabilityId },
  });
}
