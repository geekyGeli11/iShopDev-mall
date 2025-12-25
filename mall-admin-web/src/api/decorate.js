import request from '@/utils/request';

// 组件库列表
export function fetchComponentList() {
  return request({
    url: '/decorate/component/list',
    method: 'get'
  });
}

// 页面列表
export function fetchPageList(params) {
  return request({
    url: '/decorate/page/list',
    method: 'get',
    params
  });
}

// 创建页面
export function createPage(data) {
  return request({
    url: '/decorate/page',
    method: 'post',
    data
  });
}

// 页面详情
export function fetchPageDetail(id) {
  return request({
    url: `/decorate/page/${id}/detail`,
    method: 'get'
  });
}

// 更新页面（保存草稿）
export function updatePage(id, data) {
  return request({
    url: `/decorate/page/${id}`,
    method: 'put',
    data
  });
}

// 发布页面
export function publishPage(id) {
  return request({
    url: `/decorate/page/${id}/publish`,
    method: 'post'
  });
}

// 获取最新草稿 DSL（预览）
export function fetchPagePreview(id){
  return request({
    url:`/decorate/page/${id}/preview`,
    method:'get'
  });
}

// 组件 CRUD
export function createComponent(data){
  return request({url:'/decorate/component',method:'post',data});
}
export function updateComponent(id,data){
  return request({url:`/decorate/component/${id}`,method:'put',data});
}
export function deleteComponent(id){
  return request({url:`/decorate/component/${id}`,method:'delete'});
} 