/**
 * Android WebView 适配工具
 * 解决Android WebView与浏览器在viewport和CSS单位方面的差异
 */

export interface ViewportInfo {
  windowWidth: number
  windowHeight: number
  screenWidth: number
  screenHeight: number
  devicePixelRatio: number
  isAndroid: boolean
  isWebView: boolean
  actualHeight: number
  safeAreaTop: number
  safeAreaBottom: number
  // DPI相关信息
  physicalWidth: number
  physicalHeight: number
  cssPixelRatio: number
  dpiCategory: 'ldpi' | 'mdpi' | 'hdpi' | 'xhdpi' | 'xxhdpi' | 'xxxhdpi'
  densityDpi: number
}

export class AndroidWebViewAdapter {
  private static instance: AndroidWebViewAdapter
  private viewportInfo: ViewportInfo | null = null
  private resizeCallbacks: Array<(info: ViewportInfo) => void> = []

  private constructor() {
    this.init()
  }

  public static getInstance(): AndroidWebViewAdapter {
    if (!AndroidWebViewAdapter.instance) {
      AndroidWebViewAdapter.instance = new AndroidWebViewAdapter()
    }
    return AndroidWebViewAdapter.instance
  }

  /**
   * 初始化适配器
   */
  private init(): void {
    this.updateViewportInfo()
    this.setupEventListeners()
    this.applyGlobalStyles()
  }

  /**
   * 检测是否为Android WebView环境
   */
  private isAndroidWebView(): boolean {
    const userAgent = navigator.userAgent.toLowerCase()
    const isAndroid = userAgent.includes('android')
    const isWebView = userAgent.includes('wv') || 
                     userAgent.includes('webview') ||
                     !!(window as any).Capacitor ||
                     !userAgent.includes('chrome') && isAndroid

    return isAndroid && isWebView
  }



  /**
   * 获取DPI分类和密度信息
   */
  private getDpiInfo(dpr: number) {
    const densityDpi = Math.round(160 * dpr)
    let dpiCategory: 'ldpi' | 'mdpi' | 'hdpi' | 'xhdpi' | 'xxhdpi' | 'xxxhdpi'

    if (dpr <= 0.75) dpiCategory = 'ldpi'
    else if (dpr <= 1.0) dpiCategory = 'mdpi'
    else if (dpr <= 1.5) dpiCategory = 'hdpi'
    else if (dpr <= 2.0) dpiCategory = 'xhdpi'
    else if (dpr <= 3.0) dpiCategory = 'xxhdpi'
    else dpiCategory = 'xxxhdpi'

    return { densityDpi, dpiCategory }
  }

  /**
   * 获取安全区域信息（考虑DPI）
   */
  private getSafeAreaInsets(): { top: number; bottom: number } {
    // 尝试从CSS环境变量获取
    const computedStyle = getComputedStyle(document.documentElement)
    const safeAreaTop = parseInt(computedStyle.getPropertyValue('env(safe-area-inset-top)')) || 0
    const safeAreaBottom = parseInt(computedStyle.getPropertyValue('env(safe-area-inset-bottom)')) || 0

    // 如果CSS环境变量不可用，根据DPI计算经验值
    if (safeAreaTop === 0 && safeAreaBottom === 0 && this.isAndroidWebView()) {
      const dpr = window.devicePixelRatio || 1
      const { dpiCategory } = this.getDpiInfo(dpr)

      // 根据DPI分类调整状态栏和导航栏高度（CSS像素）
      const dpiHeights = {
        ldpi: { status: 18, nav: 36 },
        mdpi: { status: 24, nav: 48 },
        hdpi: { status: 30, nav: 60 },
        xhdpi: { status: 36, nav: 72 },
        xxhdpi: { status: 42, nav: 84 },
        xxxhdpi: { status: 48, nav: 96 }
      }

      const heights = dpiHeights[dpiCategory]
      return {
        top: heights.status,
        bottom: heights.nav
      }
    }

    return { top: safeAreaTop, bottom: safeAreaBottom }
  }

  /**
   * 更新viewport信息
   */
  private updateViewportInfo(): void {
    const safeArea = this.getSafeAreaInsets()
    const dpr = window.devicePixelRatio || 1
    const { densityDpi, dpiCategory } = this.getDpiInfo(dpr)

    this.viewportInfo = {
      windowWidth: window.innerWidth,
      windowHeight: window.innerHeight,
      screenWidth: screen.width,
      screenHeight: screen.height,
      devicePixelRatio: dpr,
      isAndroid: navigator.userAgent.toLowerCase().includes('android'),
      isWebView: this.isAndroidWebView(),
      actualHeight: window.innerHeight - safeArea.top - safeArea.bottom,
      safeAreaTop: safeArea.top,
      safeAreaBottom: safeArea.bottom,
      // DPI相关信息
      physicalWidth: Math.round(window.innerWidth * dpr),
      physicalHeight: Math.round(window.innerHeight * dpr),
      cssPixelRatio: 1 / dpr,
      dpiCategory,
      densityDpi
    }

    console.log('📱 Viewport信息更新 (包含DPI):', {
      ...this.viewportInfo,
      dpiAnalysis: {
        cssSize: `${this.viewportInfo.windowWidth}×${this.viewportInfo.windowHeight}`,
        physicalSize: `${this.viewportInfo.physicalWidth}×${this.viewportInfo.physicalHeight}`,
        dpiCategory: this.viewportInfo.dpiCategory,
        densityDpi: this.viewportInfo.densityDpi,
        devicePixelRatio: this.viewportInfo.devicePixelRatio
      }
    })
  }

  /**
   * 设置事件监听器
   */
  private setupEventListeners(): void {
    const updateHandler = () => {
      setTimeout(() => {
        this.updateViewportInfo()
        this.notifyResizeCallbacks()
      }, 100) // 延迟执行确保获取正确的尺寸
    }

    window.addEventListener('resize', updateHandler)
    window.addEventListener('orientationchange', updateHandler)
    
    // Android特有的事件
    if (this.isAndroidWebView()) {
      document.addEventListener('visibilitychange', updateHandler)
    }
  }

  /**
   * 应用全局样式修复
   */
  private applyGlobalStyles(): void {
    if (!this.viewportInfo) return

    const style = document.createElement('style')
    style.id = 'android-webview-adapter-styles'
    
    let css = `
      /* Android WebView 全局修复 */
      :root {
        --viewport-height: ${this.viewportInfo.actualHeight}px;
        --safe-area-top: ${this.viewportInfo.safeAreaTop}px;
        --safe-area-bottom: ${this.viewportInfo.safeAreaBottom}px;
      }
    `

    if (this.viewportInfo.isWebView) {
      css += `
        /* WebView 特殊优化 */
        html, body {
          height: ${this.viewportInfo.actualHeight}px !important;
          max-height: ${this.viewportInfo.actualHeight}px !important;
          overflow: hidden !important;
        }
        
        #app {
          height: ${this.viewportInfo.actualHeight}px !important;
          max-height: ${this.viewportInfo.actualHeight}px !important;
        }
        
        /* 禁用WebView的默认行为 */
        * {
          -webkit-touch-callout: none !important;
          -webkit-user-select: none !important;
          -webkit-tap-highlight-color: transparent !important;
        }
        
        /* 修复WebView中的滚动问题 */
        body {
          position: fixed !important;
          width: 100% !important;
          top: 0 !important;
          left: 0 !important;
        }
      `
    }

    style.textContent = css
    
    // 移除旧的样式
    const oldStyle = document.getElementById('android-webview-adapter-styles')
    if (oldStyle) {
      oldStyle.remove()
    }
    
    document.head.appendChild(style)
  }

  /**
   * 通知所有回调函数
   */
  private notifyResizeCallbacks(): void {
    if (this.viewportInfo) {
      this.resizeCallbacks.forEach(callback => {
        try {
          callback(this.viewportInfo!)
        } catch (error) {
          console.error('Resize callback error:', error)
        }
      })
    }
  }

  /**
   * 获取当前viewport信息
   */
  public getViewportInfo(): ViewportInfo | null {
    return this.viewportInfo
  }

  /**
   * 添加尺寸变化回调
   */
  public onResize(callback: (info: ViewportInfo) => void): void {
    this.resizeCallbacks.push(callback)
  }

  /**
   * 移除尺寸变化回调
   */
  public offResize(callback: (info: ViewportInfo) => void): void {
    const index = this.resizeCallbacks.indexOf(callback)
    if (index > -1) {
      this.resizeCallbacks.splice(index, 1)
    }
  }

  /**
   * 强制刷新适配
   */
  public refresh(): void {
    this.updateViewportInfo()
    this.applyGlobalStyles()
    this.notifyResizeCallbacks()
  }

  /**
   * 获取推荐的CSS高度值
   */
  public getRecommendedHeight(): string {
    if (!this.viewportInfo) return '100vh'
    
    return this.viewportInfo.isWebView 
      ? `${this.viewportInfo.actualHeight}px`
      : '100vh'
  }

  /**
   * 检查是否需要特殊适配
   */
  public needsSpecialAdaptation(): boolean {
    return this.viewportInfo?.isWebView || false
  }
}

// 导出单例实例
export const androidAdapter = AndroidWebViewAdapter.getInstance()
