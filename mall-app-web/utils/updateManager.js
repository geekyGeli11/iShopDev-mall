/**
 * 小程序版本更新管理工具
 * 支持版本检测、下载更新、重启应用等功能
 */
class UpdateManager {
  constructor() {
    this.updateManager = null;
    this.isSupported = false;
    this.init();
  }

  /**
   * 初始化更新管理器
   */
  init() {
    // #ifdef MP-WEIXIN
    try {
      if (uni.getUpdateManager) {
        this.updateManager = uni.getUpdateManager();
        this.isSupported = true;
        console.log('版本更新管理器初始化成功');
      } else {
        console.warn('当前微信版本过低，无法使用 UpdateManager');
      }
    } catch (error) {
      console.error('UpdateManager 初始化失败:', error);
    }
    // #endif
  }

  /**
   * 检查版本更新
   * @param {Object} options 配置选项
   * @param {Function} options.onCheckUpdate 检查更新回调
   * @param {Function} options.onUpdateReady 更新准备完成回调
   * @param {Function} options.onUpdateFailed 更新失败回调
   */
  checkForUpdate(options = {}) {
    // #ifdef MP-WEIXIN
    if (!this.isSupported) {
      console.warn('不支持版本更新检测');
      return;
    }

    try {
      // 检查是否有新版本
      this.updateManager.onCheckForUpdate((res) => {
        console.log('检查更新结果:', res);
        
        if (res.hasUpdate) {
          console.log('发现新版本，准备下载');
          uni.showToast({
            title: '发现新版本，正在更新...',
            icon: 'loading',
            duration: 2000
          });
        } else {
          console.log('当前已是最新版本');
        }

        // 执行自定义检查更新回调
        if (typeof options.onCheckUpdate === 'function') {
          options.onCheckUpdate(res);
        }
      });

      // 新版本下载成功
      this.updateManager.onUpdateReady(() => {
        console.log('新版本下载完成');
        this.showUpdateModal();

        // 执行自定义更新准备完成回调
        if (typeof options.onUpdateReady === 'function') {
          options.onUpdateReady();
        }
      });

      // 新版本下载失败
      this.updateManager.onUpdateFailed(() => {
        console.error('新版本下载失败');
        this.showUpdateFailedModal();

        // 执行自定义更新失败回调
        if (typeof options.onUpdateFailed === 'function') {
          options.onUpdateFailed();
        }
      });

    } catch (error) {
      console.error('版本更新检测失败:', error);
    }
    // #endif
  }

  /**
   * 显示更新弹窗
   */
  showUpdateModal() {
    uni.showModal({
      title: '更新提示',
      content: '新版本已准备好，是否立即重启应用以获得更好体验？',
      showCancel: true,
      cancelText: '稍后',
      confirmText: '立即重启',
      confirmColor: '#0088FF',
      success: (res) => {
        if (res.confirm) {
          console.log('用户确认重启应用');
          this.applyUpdate();
        } else {
          console.log('用户选择稍后重启');
          this.scheduleUpdateReminder();
        }
      }
    });
  }

  /**
   * 显示更新失败弹窗
   */
  showUpdateFailedModal() {
    uni.showModal({
      title: '更新失败',
      content: '新版本下载失败，请检查网络连接或稍后重试',
      showCancel: false,
      confirmText: '确定',
      confirmColor: '#0088FF'
    });
  }

  /**
   * 应用更新
   */
  applyUpdate() {
    // #ifdef MP-WEIXIN
    try {
      if (this.updateManager && this.isSupported) {
        this.updateManager.applyUpdate();
        // 清除更新提醒状态
        this.clearUpdateReminder();
      }
    } catch (error) {
      console.error('应用更新失败:', error);
      uni.showToast({
        title: '重启失败，请手动重启',
        icon: 'none'
      });
    }
    // #endif
  }

  /**
   * 安排更新提醒
   */
  scheduleUpdateReminder() {
    // #ifdef MP-WEIXIN
    try {
      uni.setStorageSync('hasNewVersion', true);
      uni.setStorageSync('updateReminderTime', Date.now());
      console.log('已安排更新提醒');
    } catch (error) {
      console.error('安排更新提醒失败:', error);
    }
    // #endif
  }

  /**
   * 检查是否需要提醒用户更新
   * @param {number} reminderInterval 提醒间隔时间（毫秒），默认30分钟
   */
  checkUpdateReminder(reminderInterval = 30 * 60 * 1000) {
    // #ifdef MP-WEIXIN
    try {
      const hasNewVersion = uni.getStorageSync('hasNewVersion');
      const updateReminderTime = uni.getStorageSync('updateReminderTime');
      const currentTime = Date.now();

      // 如果有新版本且距离上次提醒超过指定时间，再次提醒
      if (hasNewVersion && updateReminderTime && (currentTime - updateReminderTime > reminderInterval)) {
        this.showReminderModal();
      }
    } catch (error) {
      console.error('检查更新提醒失败:', error);
    }
    // #endif
  }

  /**
   * 显示更新提醒弹窗
   */
  showReminderModal() {
    uni.showModal({
      title: '更新提醒',
      content: '检测到新版本已下载完成，建议立即重启应用以获得更好体验',
      showCancel: true,
      cancelText: '稍后',
      confirmText: '立即重启',
      confirmColor: '#0088FF',
      success: (res) => {
        if (res.confirm) {
          this.applyUpdate();
        } else {
          // 更新提醒时间
          this.scheduleUpdateReminder();
        }
      }
    });
  }

  /**
   * 清除更新提醒状态
   */
  clearUpdateReminder() {
    // #ifdef MP-WEIXIN
    try {
      uni.removeStorageSync('hasNewVersion');
      uni.removeStorageSync('updateReminderTime');
      console.log('已清除更新提醒状态');
    } catch (error) {
      console.error('清除更新提醒状态失败:', error);
    }
    // #endif
  }

  /**
   * 手动触发版本检测
   */
  manualCheckUpdate() {
    this.checkForUpdate({
      onCheckUpdate: (res) => {
        if (!res.hasUpdate) {
          uni.showToast({
            title: '当前已是最新版本',
            icon: 'success'
          });
        }
      }
    });
  }
}

// 创建单例实例
const updateManager = new UpdateManager();

export default updateManager; 