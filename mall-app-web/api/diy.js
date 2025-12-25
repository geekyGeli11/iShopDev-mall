import { request } from '@/utils/requestUtil.js';
import { uploadImage } from '@/api/common.js';

/**
 * DIY定制相关API接口
 */

// 获取商品DIY配置
export function getDIYProductConfig(productId) {
  return request({
    url: `/diy/product/${productId}/config`,
    method: 'GET'
  });
}

// 获取商品DIY模板信息
export function getDIYProductTemplate(productId) {
  return request({
    url: `/diy/product/${productId}/template`,
    method: 'GET'
  });
}

// 获取DIY素材列表
export function getDIYMaterials(params = {}) {
  return request({
    url: '/diy/materials',
    method: 'GET',
    data: params
  });
}

// 获取DIY素材分类
export function getDIYMaterialCategories() {
  return request({
    url: '/diy/materials/categories',
    method: 'GET'
  });
}

// AI风格化处理
export function aiStylization(data) {
  return request({
    url: '/diy/ai/stylization',
    method: 'POST',
    data: data,
    hideLoading: true // 禁用默认loading，使用自定义进度条
  });
}

// 获取AI风格列表
export function getAIStyles() {
  return request({
    url: '/diy/ai/styles',
    method: 'GET'
  });
}

// 上传图片素材（使用通用上传接口）
export function uploadDIYImage(filePath) {
  return uploadImage(filePath);
}

// 保存设计数据
export function saveDIYDesign(data) {
  return request({
    url: '/diy/design/save',
    method: 'POST',
    data: data
  });
}

// 获取设计数据
export function getDIYDesign(designId) {
  return request({
    url: `/diy/design/${designId}`,
    method: 'GET'
  });
}

// 生成预览图
export function generateDIYPreview(data) {
  return request({
    url: '/diy/preview/generate',
    method: 'POST',
    data: data
  });
}

// 计算DIY订单价格
export function calculateDIYPrice(data) {
  return request({
    url: '/diy/order/calculate',
    method: 'POST',
    data: data
  });
}

// 创建DIY订单
export function createDIYOrder(data) {
  return request({
    url: '/diy/order/create',
    method: 'POST',
    data: data
  });
}

// 获取DIY订单详情
export function getDIYOrderDetail(orderId) {
  return request({
    url: `/diy/order/${orderId}`,
    method: 'GET'
  });
}

// 取消DIY订单
export function cancelDIYOrder(orderId) {
  return request({
    url: `/diy/order/${orderId}/cancel`,
    method: 'POST'
  });
}

// 获取用户DIY订单列表
export function getDIYOrderList(params = {}) {
  return request({
    url: '/diy/order/list',
    method: 'GET',
    data: params
  });
}

// 确认收货
export function confirmDIYOrderReceive(orderId) {
  return request({
    url: `/diy/order/${orderId}/confirm`,
    method: 'POST'
  });
}

// 获取用户DIY设计历史
export function getUserDIYHistory(params = {}) {
  return request({
    url: '/diy/user/history',
    method: 'GET',
    data: params
  });
}

// 删除DIY设计
export function deleteDIYDesign(designId) {
  return request({
    url: `/diy/design/${designId}`,
    method: 'DELETE'
  });
}

// 复制DIY设计
export function copyDIYDesign(designId) {
  return request({
    url: `/diy/design/${designId}/copy`,
    method: 'POST'
  });
}

// 获取字体列表
export function getDIYFonts() {
  return request({
    url: '/diy/fonts',
    method: 'GET'
  });
}

// 获取颜色预设
export function getDIYColorPresets() {
  return request({
    url: '/diy/colors/presets',
    method: 'GET'
  });
}

// 检查设计是否可以下单
export function checkDIYDesignValid(designId) {
  return request({
    url: `/diy/design/${designId}/validate`,
    method: 'GET'
  });
}

// 获取DIY详细价格计算
export function calculateDIYDetailPrice(data) {
  return request({
    url: '/diy/price/calculate',
    method: 'POST',
    data: data
  });
}
