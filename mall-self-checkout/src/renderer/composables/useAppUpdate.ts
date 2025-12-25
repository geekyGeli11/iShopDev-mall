/**
 * 应用更新管理 Composable
 */

import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { showToast, showDialog, showNotify } from 'vant'
import { AppUpdateService } from '@/shared/services/AppUpdateService'
import type { VersionInfo, VersionCheckResult, UpdateProgress, UpdateConfig } from '@/shared/services/AppUpdateService'
import { logger } from '@/config/env'

export function useAppUpdate() {
  const updateService = AppUpdateService.getInstance()
  
  // 响应式状态
  const isChecking = ref(false)
  const showUpdateDialog = ref(false)
  const currentVersionInfo = ref<VersionInfo>()
  const latestVersionInfo = ref<VersionInfo>()
  const isForceUpdate = ref(false)
  const errorMessage = ref('')
  
  const updateProgress = reactive<UpdateProgress>({
    status: 'checking',
    progress: 0,
    message: ''
  })

  /**
   * 初始化更新服务
   */
  const initializeUpdate = async () => {
    try {
      await updateService.initialize()
      
      // 设置进度回调
      updateService.setProgressCallback((progress) => {
        Object.assign(updateProgress, progress)
        
        // 根据状态显示不同的提示
        if (progress.status === 'completed' && !showUpdateDialog.value) {
          if (progress.message.includes('已是最新版本')) {
            // 静默处理，不显示提示
          } else {
            showNotify({
              type: 'success',
              message: progress.message,
              duration: 2000
            })
          }
        } else if (progress.status === 'failed' && !showUpdateDialog.value) {
          showNotify({
            type: 'danger',
            message: progress.error || '操作失败',
            duration: 3000
          })
        }
      })
      
      logger.info('应用更新服务初始化完成')
    } catch (error) {
      logger.error('初始化更新服务失败', error)
    }
  }

  /**
   * 手动检查更新
   */
  const checkForUpdates = async (showLoading = true) => {
    try {
      if (showLoading) {
        isChecking.value = true
        showToast({
          type: 'loading',
          message: '检查更新中...',
          forbidClick: true,
          duration: 0
        })
      }

      const result = await updateService.manualCheck()
      
      if (showLoading) {
        isChecking.value = false
      }

      if (result) {
        handleUpdateCheckResult(result)
      } else {
        if (showLoading) {
          showToast({
            type: 'fail',
            message: '检查更新失败'
          })
        }
      }
    } catch (error) {
      isChecking.value = false
      logger.error('检查更新失败', error)
      
      if (showLoading) {
        showToast({
          type: 'fail',
          message: '检查更新失败'
        })
      }
    }
  }

  /**
   * 处理更新检查结果
   */
  const handleUpdateCheckResult = (result: VersionCheckResult) => {
    currentVersionInfo.value = result.currentVersion
    latestVersionInfo.value = result.latestVersion
    isForceUpdate.value = result.forceUpdate || false
    errorMessage.value = ''

    if (result.hasUpdate && result.latestVersion) {
      // 显示更新对话框
      showUpdateDialog.value = true
      
      logger.info('发现新版本', {
        current: result.currentVersion?.versionName,
        latest: result.latestVersion.versionName,
        force: result.forceUpdate
      })
    } else {
      // 没有更新
      showToast({
        type: 'success',
        message: '已是最新版本'
      })
    }
  }

  /**
   * 确认更新
   */
  const confirmUpdate = async () => {
    if (!latestVersionInfo.value) {
      return
    }

    try {
      errorMessage.value = ''
      
      // 开始下载和安装
      const success = await updateService.downloadAndInstall(latestVersionInfo.value)
      
      if (success) {
        // 更新成功，应用将重启
        showUpdateDialog.value = false
        
        showDialog({
          title: '更新完成',
          message: '应用将在3秒后重启以完成更新',
          showCancelButton: false,
          confirmButtonText: '确定'
        }).then(() => {
          // 重启应用
          setTimeout(() => {
            window.location.reload()
          }, 1000)
        })
      } else {
        // 更新失败
        errorMessage.value = updateProgress.error || '更新失败，请重试'
      }
    } catch (error) {
      logger.error('更新失败', error)
      errorMessage.value = error instanceof Error ? error.message : '更新失败'
    }
  }

  /**
   * 取消更新
   */
  const cancelUpdate = () => {
    if (!isForceUpdate.value) {
      showUpdateDialog.value = false
      errorMessage.value = ''
    }
  }

  /**
   * 重试更新
   */
  const retryUpdate = () => {
    errorMessage.value = ''
    confirmUpdate()
  }

  /**
   * 获取更新配置
   */
  const getUpdateConfig = (): UpdateConfig => {
    return updateService.getConfig()
  }

  /**
   * 保存更新配置
   */
  const saveUpdateConfig = (config: Partial<UpdateConfig>) => {
    updateService.saveConfig(config)
    showToast({
      type: 'success',
      message: '配置已保存'
    })
  }

  /**
   * 显示更新设置
   */
  const showUpdateSettings = () => {
    // 这里可以打开更新设置页面
    // 暂时使用简单的对话框
    const config = getUpdateConfig()
    
    showDialog({
      title: '更新设置',
      message: `
        自动检查: ${config.autoCheck ? '开启' : '关闭'}
        检查间隔: ${config.checkInterval / (60 * 60 * 1000)}小时
        更新时间: ${config.allowedUpdateTime.start} - ${config.allowedUpdateTime.end}
      `,
      showCancelButton: true,
      confirmButtonText: '修改设置',
      cancelButtonText: '取消'
    }).then(() => {
      // 打开设置页面的逻辑
      logger.info('打开更新设置页面')
    })
  }

  /**
   * 获取当前版本信息
   */
  const getCurrentVersionInfo = () => {
    return currentVersionInfo.value
  }

  /**
   * 获取最新版本信息
   */
  const getLatestVersionInfo = () => {
    return latestVersionInfo.value
  }

  /**
   * 检查是否有可用更新
   */
  const hasAvailableUpdate = () => {
    return showUpdateDialog.value && latestVersionInfo.value
  }

  /**
   * 强制检查更新（忽略时间限制）
   */
  const forceCheckUpdate = async () => {
    await checkForUpdates(true)
  }

  // 生命周期
  onMounted(() => {
    initializeUpdate()
  })

  onUnmounted(() => {
    updateService.destroy()
  })

  return {
    // 状态
    isChecking,
    showUpdateDialog,
    currentVersionInfo,
    latestVersionInfo,
    isForceUpdate,
    errorMessage,
    updateProgress,
    
    // 方法
    checkForUpdates,
    confirmUpdate,
    cancelUpdate,
    retryUpdate,
    getUpdateConfig,
    saveUpdateConfig,
    showUpdateSettings,
    getCurrentVersionInfo,
    getLatestVersionInfo,
    hasAvailableUpdate,
    forceCheckUpdate
  }
}
