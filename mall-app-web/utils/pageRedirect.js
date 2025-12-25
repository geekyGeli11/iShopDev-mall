/**
 * 页面重定向工具
 * 用于处理历史遗留的无效页面路径，自动重定向到首页
 */

/* global getCurrentPages, uni */

// 从pages.json中提取的所有有效页面路径
const VALID_PAGES = [
  // 主包页面
  'pages/new_index/index',
  'pages/product/product',
  'pages/cart/cart',
  'pages/public/login',
  'pages/public/register',
  'pages/user/user',
  'pages/order/order',
  'pages/money/money',
  'pages/order/createOrder',
  'pages/order/orderDetail',
  'pages/order/returnApply',
  'pages/address/address',
  'pages/address/addressManage',
  'pages/money/pay',
  'pages/money/paySuccess',
  'pages/category/category',
  'pages/product/list',
  'pages/coupon/couponList',
  'pages/product/product-list',
  'pages/product/sales-ranking',
  'pages/user/readHistory',
  'pages/user/productCollection',
  'pages/user/exchangeRecord',
  'pages/search/search',
  'pages/point/point',
  'pages/gift/gift',
  'pages/user/team',
  'pages/user/income',
  'pages/user/income-rule',
  'pages/user/aboutUs',
  'pages/gift-bag/gift-bag',
  'pages/gift-bag/giftsList',
  'pages/gift-bag/gift-wishes',
  'pages/gift-bag/gift-confirm',
  'pages/store/storeList',
  'pages/signin/signin',
  'pages/user/distribution-center',
  'pages/user/invite-friends',
  'pages/order/afterSaleEvaluation',
  'pages/user/distributor-apply',
  'pages/user/invite-records',
  'pages/product/bundleDetail',
  "pages/customerService/index",
  
  // DIY主页面（已移至主包）
  'pages/diy/diy',

  // 分包页面
  'subpackages/diy/customize',
  'subpackages/diy/preview',
  'subpackages/diy/order',
  'subpackages/diy/designDetail',
  'subpackages/user/userinfo/userinfo',
  'subpackages/user/set/set',
  
  // 销售单分包页面
  'subpackages/sale/saleDetail'
];

// 默认重定向页面（首页）
const DEFAULT_PAGE = '/pages/new_index/index';

// 重定向状态标记，防止无限重定向
let isRedirecting = false;
let lastRedirectTime = 0;

/**
 * 检查页面路径是否有效
 * @param {string} pagePath 页面路径
 * @returns {boolean} 是否为有效页面
 */
function isValidPage(pagePath) {
  if (!pagePath || typeof pagePath !== 'string') {
    return false;
  }
  
  // 去掉开头的斜杠
  const cleanPath = pagePath.replace(/^\//, '');
  
  // 去掉查询参数
  const pathWithoutQuery = cleanPath.split('?')[0];
  
  // 检查是否在有效页面列表中
  return VALID_PAGES.includes(pathWithoutQuery);
}

/**
 * 获取当前页面路径
 * @returns {string|null} 当前页面路径，如果获取失败返回null
 */
function getCurrentPagePath() {
  try {
    // 确保在uni-app环境中
    if (typeof uni !== 'undefined' && typeof getCurrentPages === 'function') {
      const pages = getCurrentPages();
      if (pages && pages.length > 0) {
        const currentPage = pages[pages.length - 1];
        return currentPage.route || null;
      }
    }
  } catch (error) {
    console.error('获取当前页面路径失败:', error);
  }
  return null;
}

/**
 * 执行页面重定向
 * @param {string} targetPage 目标页面路径
 * @param {string} reason 重定向原因
 */
function executeRedirect(targetPage = DEFAULT_PAGE, reason = '页面不存在') {
  const now = Date.now();
  
  // 防重复重定向：1秒内不重复执行
  if (isRedirecting || (now - lastRedirectTime) < 1000) {
    console.log('重定向已在进行中，跳过本次重定向');
    return;
  }
  
  isRedirecting = true;
  lastRedirectTime = now;
  
  console.log(`页面重定向: ${reason}, 跳转到: ${targetPage}`);
  
  // 使用reLaunch确保完全替换当前页面栈
  uni.reLaunch({
    url: targetPage,
    success: () => {
      console.log('页面重定向成功');
      isRedirecting = false;
    },
    fail: (error) => {
      console.error('页面重定向失败:', error);
      isRedirecting = false;
    }
  });
}

/**
 * 检查当前页面并执行重定向（如果需要）
 * @param {Object} options 启动参数，包含path信息
 */
function checkAndRedirect(options = {}) {
  try {
    // 如果正在重定向中，跳过检查
    if (isRedirecting) {
      return;
    }

    let currentPath = null;

    // 优先从options.path获取页面路径（适用于无效页面场景）
    if (options && options.path) {
      currentPath = options.path;
      console.log(`从启动参数获取页面路径: ${currentPath}`);
    } else {
      // 尝试从页面栈获取当前页面路径
      currentPath = getCurrentPagePath();
      if (currentPath) {
        console.log(`从页面栈获取页面路径: ${currentPath}`);
      }
    }

    // 如果仍然无法获取页面路径，尝试重定向到首页
    if (!currentPath) {
      console.log('无法获取当前页面路径，可能是无效页面，执行重定向');
      executeRedirect(DEFAULT_PAGE, '无法获取页面路径，可能是无效页面');
      return;
    }

    // 如果当前已经是首页，不需要重定向
    if (currentPath === 'pages/new_index/index') {
      console.log('当前已是首页，跳过重定向检查');
      return;
    }

    // 检查当前页面是否有效
    if (!isValidPage(currentPath)) {
      console.log(`检测到无效页面: ${currentPath}`);
      executeRedirect(DEFAULT_PAGE, `无效页面: ${currentPath}`);
    } else {
      console.log(`页面有效: ${currentPath}`);
    }

  } catch (error) {
    console.error('页面检查过程中发生错误:', error);
    // 发生错误时也尝试重定向到首页
    executeRedirect(DEFAULT_PAGE, '页面检查发生错误');
  }
}

/**
 * 处理小程序启动时的页面检查
 * @param {Object} options 启动参数
 */
function handleLaunchRedirect(options = {}) {
  console.log('小程序启动，检查页面有效性:', options);

  // 如果启动参数中包含path，立即检查是否为有效页面
  if (options.path && !isValidPage(options.path)) {
    console.log(`启动参数中检测到无效页面: ${options.path}，立即重定向`);
    executeRedirect(DEFAULT_PAGE, `启动时检测到无效页面: ${options.path}`);
    return;
  }

  // 延迟执行，确保页面完全加载
  setTimeout(() => {
    checkAndRedirect(options);
  }, 100);
}

/**
 * 处理小程序显示时的页面检查
 * @param {Object} options 显示参数
 */
function handleShowRedirect(options = {}) {
  console.log('小程序显示，检查页面有效性:', options);

  // 如果显示参数中包含path，立即检查是否为有效页面
  if (options.path && !isValidPage(options.path)) {
    console.log(`显示参数中检测到无效页面: ${options.path}，立即重定向`);
    executeRedirect(DEFAULT_PAGE, `显示时检测到无效页面: ${options.path}`);
    return;
  }

  // 延迟执行，确保页面状态稳定
  setTimeout(() => {
    checkAndRedirect(options);
  }, 50);
}

/**
 * 重置重定向状态（用于测试或特殊情况）
 */
function resetRedirectState() {
  isRedirecting = false;
  lastRedirectTime = 0;
  console.log('重定向状态已重置');
}

/**
 * 监听页面加载错误并处理重定向
 * 这个函数可以在页面的onLoad中调用
 */
function handlePageLoadError() {
  try {
    const currentPath = getCurrentPagePath();
    if (currentPath && !isValidPage(currentPath)) {
      console.log(`页面加载时检测到无效页面: ${currentPath}`);
      executeRedirect(DEFAULT_PAGE, `页面加载错误: ${currentPath}`);
    }
  } catch (error) {
    console.error('页面加载错误处理失败:', error);
    executeRedirect(DEFAULT_PAGE, '页面加载发生未知错误');
  }
}

/**
 * 获取所有有效页面列表（用于调试）
 * @returns {Array} 有效页面列表
 */
function getValidPages() {
  return [...VALID_PAGES];
}

export {
  isValidPage,
  getCurrentPagePath,
  executeRedirect,
  checkAndRedirect,
  handleLaunchRedirect,
  handleShowRedirect,
  resetRedirectState,
  handlePageLoadError,
  getValidPages,
  DEFAULT_PAGE
};

export default {
  isValidPage,
  getCurrentPagePath,
  executeRedirect,
  checkAndRedirect,
  handleLaunchRedirect,
  handleShowRedirect,
  resetRedirectState,
  handlePageLoadError,
  getValidPages,
  DEFAULT_PAGE
};
