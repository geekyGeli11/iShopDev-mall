<template>
  <div id="app" class="fullscreen-container" :class="{ 'aspect-9-16': !isMobileDevice }">
    <router-view />

    <!-- 全局调试浮窗 -->
    <DebugFloatingPanel
      ref="debugPanelRef"
      :initial-position="{ x: 20, y: 100 }"
      :initial-expanded="false"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useAppStore } from '@/store/modules/app'
import DebugFloatingPanel from './components/debug/DebugFloatingPanel.vue'
import { debugLogger } from './utils/debugLogger'
import { androidAdapter, type ViewportInfo } from './utils/androidWebViewAdapter'

const appStore = useAppStore()
const debugPanelRef = ref()
const isMobileDevice = ref(false)

let resizeHandler: () => void

// 检测是否为移动设备
const detectMobileDevice = () => {
  const userAgent = navigator.userAgent.toLowerCase()
  const isAndroid = userAgent.includes('android')
  const isIOS = userAgent.includes('iphone') || userAgent.includes('ipad')
  const isCapacitor = !!(window as any).Capacitor

  isMobileDevice.value = isAndroid || isIOS || isCapacitor

  console.log('设备检测结果:', {
    userAgent,
    isAndroid,
    isIOS,
    isCapacitor,
    isMobileDevice: isMobileDevice.value
  })
}

// 动态计算实际可用高度
const calculateActualHeight = () => {
  const windowHeight = window.innerHeight
  const screenHeight = screen.height
  const documentHeight = document.documentElement.clientHeight

  // 在Android WebView中，使用window.innerHeight更准确
  const actualHeight = isMobileDevice.value ? windowHeight : documentHeight

  console.log('高度计算:', {
    windowHeight,
    screenHeight,
    documentHeight,
    actualHeight,
    isMobile: isMobileDevice.value
  })

  return actualHeight
}

onMounted(() => {
  // 检测设备类型
  detectMobileDevice()

  // 初始化Android WebView适配器
  const viewportInfo = androidAdapter.getViewportInfo()
  if (viewportInfo) {
    console.log('📱 Android WebView适配器已初始化:', viewportInfo)
    isMobileDevice.value = viewportInfo.isAndroid || viewportInfo.isWebView
  }

  // 初始化应用
  appStore.initialize()

  // 初始化调试功能
  if (debugLogger.isDebugEnabled()) {
    console.log('🐛 调试功能已启用')

    // 添加一些测试日志
    debugLogger.logInfo('应用启动', 'Mall自助收银系统已启动')
    debugLogger.logInfo('调试面板', '调试面板已加载到全局')
    debugLogger.logInfo('设备信息', `设备类型: ${isMobileDevice.value ? '移动设备' : '桌面设备'}`)

    // 测试不同类型的日志
    setTimeout(() => {
      debugLogger.logScan('测试扫码日志', '这是一条测试扫码日志', {
        scanType: 'product',
        barcode: 'TEST123456',
        status: 'success'
      })

      debugLogger.logApi('测试API日志', '这是一条测试API日志', {
        method: 'GET',
        url: '/test/api',
        status: 200,
        duration: 100
      })

      debugLogger.logSuccess('测试成功日志', '调试功能初始化完成')
    }, 1000)
  }

  // 确保全屏显示
  document.body.classList.add('no-scroll')

  // 使用Android适配器的回调处理尺寸变化
  const handleViewportChange = (info: ViewportInfo) => {
    const app = document.getElementById('app')
    if (app) {
      if (info.isWebView || info.isAndroid) {
        // Android WebView使用实际可用高度
        app.style.width = '100vw'
        app.style.height = `${info.actualHeight}px`
        app.style.maxHeight = `${info.actualHeight}px`

        // 更新CSS变量
        document.documentElement.style.setProperty('--app-height', `${info.actualHeight}px`)
      } else {
        // 桌面设备使用viewport单位
        app.style.width = '100vw'
        app.style.height = '100vh'
      }

      console.log('📱 应用容器尺寸已更新:', {
        width: app.style.width,
        height: app.style.height,
        actualHeight: info.actualHeight,
        isWebView: info.isWebView,
        isAndroid: info.isAndroid
      })
    }
  }

  // 注册适配器回调
  androidAdapter.onResize(handleViewportChange)

  // 立即执行一次适配
  setTimeout(() => {
    const currentInfo = androidAdapter.getViewportInfo()
    if (currentInfo) {
      handleViewportChange(currentInfo)
    }
  }, 100)
})

onUnmounted(() => {
  document.body.classList.remove('no-scroll')
  if (resizeHandler) {
    window.removeEventListener('resize', resizeHandler)
    window.removeEventListener('orientationchange', resizeHandler)
  }
})
</script>

<style scoped>
#app.fullscreen-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #fff;
  overflow: hidden;
  position: relative;
}

/* 桌面端保持9:16比例 */
#app.fullscreen-container.aspect-9-16 {
  aspect-ratio: 9 / 16;
  max-width: 100vw;
  max-height: 100vh;
  width: 56.25vh; /* 9/16 = 0.5625, 用高度控制宽度 */
  height: 100vh;
  background: #fff;
  box-shadow: 0 0 24px rgba(0,0,0,0.04);
  border-radius: 0;
}

/* 移动端全屏显示 */
#app.fullscreen-container:not(.aspect-9-16) {
  width: 100vw;
  height: 100vh;
  max-height: 100vh;
  max-width: 100vw;
  background: #fff;
}

/* 小屏幕设备强制全屏 */
@media (max-width: 540px), (max-height: 960px) {
  #app.fullscreen-container.aspect-9-16 {
    width: 100vw !important;
    height: 100vh !important;
    max-height: 100vh !important;
    max-width: 100vw !important;
    background: #fff;
    aspect-ratio: unset;
    box-shadow: none;
  }
}

/* Android WebView特殊优化 */
@supports (-webkit-touch-callout: none) {
  #app.fullscreen-container {
    /* 使用window.innerHeight的JavaScript值 */
    height: var(--app-height, 100vh);
  }
}
</style>