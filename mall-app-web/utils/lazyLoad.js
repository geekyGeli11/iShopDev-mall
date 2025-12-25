/**
 * 图片懒加载工具
 * 用于优化页面性能，延迟加载图片
 */

class LazyLoadManager {
  constructor() {
    this.observers = new Map();
    this.loadedImages = new Set();
  }

  /**
   * 创建 IntersectionObserver（仅H5支持）
   * 小程序环境下直接加载图片
   */
  createObserver(callback) {
    // #ifdef H5
    if ('IntersectionObserver' in window) {
      return new IntersectionObserver(
        (entries) => {
          entries.forEach((entry) => {
            if (entry.isIntersecting) {
              callback(entry.target);
            }
          });
        },
        {
          rootMargin: '50px', // 提前50px开始加载
          threshold: 0.01
        }
      );
    }
    // #endif
    return null;
  }

  /**
   * 观察图片元素
   * @param {Element} element - 图片元素
   * @param {string} src - 图片源地址
   */
  observe(element, src) {
    // 如果已经加载过，直接返回
    if (this.loadedImages.has(src)) {
      this.loadImage(element, src);
      return;
    }

    // #ifdef H5
    const observer = this.createObserver((target) => {
      this.loadImage(target, src);
      observer.unobserve(target);
    });

    if (observer) {
      observer.observe(element);
      this.observers.set(element, observer);
    } else {
      // 不支持 IntersectionObserver，直接加载
      this.loadImage(element, src);
    }
    // #endif

    // #ifndef H5
    // 小程序环境，直接加载（小程序有自己的图片懒加载机制）
    this.loadImage(element, src);
    // #endif
  }

  /**
   * 加载图片
   * @param {Element} element - 图片元素
   * @param {string} src - 图片源地址
   */
  loadImage(element, src) {
    if (!element || !src) return;

    // 设置图片源
    element.setAttribute('src', src);
    
    // 标记为已加载
    this.loadedImages.add(src);

    // 添加加载完成类名
    element.addEventListener('load', () => {
      element.classList.add('lazy-loaded');
    });

    // 添加加载失败处理
    element.addEventListener('error', () => {
      element.classList.add('lazy-error');
      // 可以设置默认图片
      // element.setAttribute('src', '/static/images/default.png');
    });
  }

  /**
   * 取消观察
   * @param {Element} element - 图片元素
   */
  unobserve(element) {
    const observer = this.observers.get(element);
    if (observer) {
      observer.unobserve(element);
      this.observers.delete(element);
    }
  }

  /**
   * 清理所有观察器
   */
  cleanup() {
    this.observers.forEach((observer, element) => {
      observer.unobserve(element);
    });
    this.observers.clear();
  }

  /**
   * 预加载图片
   * @param {string|string[]} urls - 图片URL或URL数组
   * @returns {Promise}
   */
  preload(urls) {
    const urlArray = Array.isArray(urls) ? urls : [urls];
    
    return Promise.all(
      urlArray.map((url) => {
        return new Promise((resolve, reject) => {
          // 如果已经加载过，直接resolve
          if (this.loadedImages.has(url)) {
            resolve(url);
            return;
          }

          // #ifdef H5
          const img = new Image();
          img.onload = () => {
            this.loadedImages.add(url);
            resolve(url);
          };
          img.onerror = reject;
          img.src = url;
          // #endif

          // #ifndef H5
          // 小程序环境使用 uni.getImageInfo 预加载
          uni.getImageInfo({
            src: url,
            success: () => {
              this.loadedImages.add(url);
              resolve(url);
            },
            fail: reject
          });
          // #endif
        });
      })
    );
  }

  /**
   * 获取缩略图URL（如果支持）
   * @param {string} url - 原始图片URL
   * @param {number} width - 缩略图宽度
   * @param {number} height - 缩略图高度
   * @returns {string} 缩略图URL
   */
  getThumbnail(url, width = 300, height = 300) {
    if (!url) return '';

    // 如果是阿里云OSS，添加缩略图参数
    if (url.includes('aliyuncs.com') || url.includes('oss-cn-')) {
      const separator = url.includes('?') ? '&' : '?';
      return `${url}${separator}x-oss-process=image/resize,m_fill,w_${width},h_${height}`;
    }

    // 如果是腾讯云COS，添加缩略图参数
    if (url.includes('myqcloud.com') || url.includes('cos.')) {
      const separator = url.includes('?') ? '&' : '?';
      return `${url}${separator}imageMogr2/thumbnail/${width}x${height}`;
    }

    // 其他情况返回原图
    return url;
  }

  /**
   * 批量获取缩略图URL
   * @param {string[]} urls - 图片URL数组
   * @param {number} width - 缩略图宽度
   * @param {number} height - 缩略图高度
   * @returns {string[]} 缩略图URL数组
   */
  getThumbnails(urls, width = 300, height = 300) {
    return urls.map(url => this.getThumbnail(url, width, height));
  }
}

// 创建单例
const lazyLoadManager = new LazyLoadManager();

// 导出单例
export default lazyLoadManager;

// 导出 Vue 指令（用于H5）
export const lazyLoadDirective = {
  bind(el, binding) {
    // 保存原始src到data-src
    el.setAttribute('data-src', binding.value);
    // 设置占位图
    el.setAttribute('src', 'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7');
    // 添加懒加载类
    el.classList.add('lazy-image');
    // 开始观察
    lazyLoadManager.observe(el, binding.value);
  },
  unbind(el) {
    lazyLoadManager.unobserve(el);
  }
};

// 导出工具函数
export const lazyLoadUtils = {
  /**
   * 预加载关键图片
   * @param {string[]} urls - 图片URL数组
   */
  preloadCriticalImages(urls) {
    return lazyLoadManager.preload(urls);
  },

  /**
   * 获取缩略图
   * @param {string} url - 原始图片URL
   * @param {number} width - 宽度
   * @param {number} height - 高度
   */
  getThumbnail(url, width, height) {
    return lazyLoadManager.getThumbnail(url, width, height);
  },

  /**
   * 批量获取缩略图
   * @param {string[]} urls - 图片URL数组
   * @param {number} width - 宽度
   * @param {number} height - 高度
   */
  getThumbnails(urls, width, height) {
    return lazyLoadManager.getThumbnails(urls, width, height);
  }
};

