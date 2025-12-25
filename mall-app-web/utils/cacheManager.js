/**
 * 数据缓存管理工具
 * 支持过期时间、内存缓存和本地存储缓存
 */

class CacheManager {
  constructor() {
    // 内存缓存
    this.memoryCache = new Map();
    // 默认缓存时间（5分钟）
    this.defaultExpireTime = 5 * 60 * 1000;
  }

  /**
   * 设置缓存
   * @param {string} key - 缓存键
   * @param {any} data - 缓存数据
   * @param {number} expireTime - 过期时间（毫秒），默认5分钟
   * @param {boolean} persistent - 是否持久化到本地存储，默认false
   */
  set(key, data, expireTime = this.defaultExpireTime, persistent = false) {
    const cacheData = {
      data,
      timestamp: Date.now(),
      expireTime
    };

    // 存入内存缓存
    this.memoryCache.set(key, cacheData);

    // 如果需要持久化，存入本地存储
    if (persistent) {
      try {
        uni.setStorageSync(key, JSON.stringify(cacheData));
      } catch (error) {
        console.error('缓存持久化失败:', error);
      }
    }
  }

  /**
   * 获取缓存
   * @param {string} key - 缓存键
   * @param {boolean} checkPersistent - 是否检查本地存储，默认true
   * @returns {any|null} 缓存数据，如果不存在或已过期返回null
   */
  get(key, checkPersistent = true) {
    // 先从内存缓存获取
    let cacheData = this.memoryCache.get(key);

    // 如果内存缓存不存在，尝试从本地存储获取
    if (!cacheData && checkPersistent) {
      try {
        const persistentData = uni.getStorageSync(key);
        if (persistentData) {
          cacheData = JSON.parse(persistentData);
          // 恢复到内存缓存
          this.memoryCache.set(key, cacheData);
        }
      } catch (error) {
        console.error('读取持久化缓存失败:', error);
      }
    }

    // 检查缓存是否存在
    if (!cacheData) {
      return null;
    }

    // 检查缓存是否过期
    const now = Date.now();
    if (now - cacheData.timestamp > cacheData.expireTime) {
      // 缓存已过期，删除
      this.delete(key);
      return null;
    }

    return cacheData.data;
  }

  /**
   * 删除缓存
   * @param {string} key - 缓存键
   */
  delete(key) {
    // 从内存缓存删除
    this.memoryCache.delete(key);

    // 从本地存储删除
    try {
      uni.removeStorageSync(key);
    } catch (error) {
      console.error('删除持久化缓存失败:', error);
    }
  }

  /**
   * 清空所有缓存
   */
  clear() {
    // 清空内存缓存
    this.memoryCache.clear();

    // 注意：不清空本地存储，因为可能包含其他重要数据
  }

  /**
   * 检查缓存是否存在且未过期
   * @param {string} key - 缓存键
   * @returns {boolean}
   */
  has(key) {
    return this.get(key) !== null;
  }

  /**
   * 获取或设置缓存（如果缓存不存在，执行回调函数获取数据并缓存）
   * @param {string} key - 缓存键
   * @param {Function} fetchFn - 获取数据的异步函数
   * @param {number} expireTime - 过期时间（毫秒）
   * @param {boolean} persistent - 是否持久化
   * @returns {Promise<any>}
   */
  async getOrSet(key, fetchFn, expireTime = this.defaultExpireTime, persistent = false) {
    // 先尝试从缓存获取
    const cachedData = this.get(key, persistent);
    if (cachedData !== null) {
      console.log(`✅ 从缓存获取数据: ${key}`);
      return cachedData;
    }

    // 缓存不存在，执行回调函数获取数据
    console.log(`🔄 缓存未命中，获取新数据: ${key}`);
    try {
      const data = await fetchFn();
      // 缓存数据
      this.set(key, data, expireTime, persistent);
      return data;
    } catch (error) {
      console.error(`获取数据失败: ${key}`, error);
      throw error;
    }
  }

  /**
   * 使缓存失效（删除指定前缀的所有缓存）
   * @param {string} prefix - 缓存键前缀
   */
  invalidateByPrefix(prefix) {
    // 清理内存缓存
    const keysToDelete = [];
    for (const key of this.memoryCache.keys()) {
      if (key.startsWith(prefix)) {
        keysToDelete.push(key);
      }
    }
    keysToDelete.forEach(key => this.delete(key));

    console.log(`🗑️ 清理缓存前缀: ${prefix}, 共清理 ${keysToDelete.length} 条`);
  }
}

// 创建单例
const cacheManager = new CacheManager();

// 导出单例
export default cacheManager;

// 导出缓存键常量
export const CACHE_KEYS = {
  // 门店相关
  STORE_GROUPS: 'cache_store_groups',
  SELECTED_SCHOOL: 'cache_selected_school',
  
  // 首页相关
  HOME_CONTENT: 'cache_home_content_',  // 后面拼接 schoolId
  HOME_ADVERTISE: 'cache_home_advertise_',
  HOME_HOT_PRODUCTS: 'cache_home_hot_products_',
  
  // 分类相关
  CATEGORY_LIST: 'cache_category_list',
  PRODUCT_LIST: 'cache_product_list_',  // 后面拼接查询参数
  SUB_CATEGORY: 'cache_sub_category_',  // 后面拼接 parentId
  
  // 用户相关
  USER_LOCATION: 'cache_user_location',
};

// 导出缓存过期时间常量（毫秒）
export const CACHE_EXPIRE_TIME = {
  SHORT: 2 * 60 * 1000,      // 2分钟
  MEDIUM: 5 * 60 * 1000,     // 5分钟
  LONG: 15 * 60 * 1000,      // 15分钟
  VERY_LONG: 60 * 60 * 1000, // 1小时
  DAY: 24 * 60 * 60 * 1000,  // 1天
};

