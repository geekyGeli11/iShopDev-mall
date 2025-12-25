/**
 * 深色模式管理工具
 * 支持自动检测、手动切换和监听系统主题变化
 */

// 主题类型枚举
export enum ThemeMode {
  LIGHT = 'light',
  DARK = 'dark',
  AUTO = 'auto'
}

// 深色模式状态接口
export interface DarkModeState {
  currentTheme: ThemeMode
  systemPrefersDark: boolean
  isDarkMode: boolean
  supportedFeatures: {
    systemDetection: boolean
    manualToggle: boolean
    persistence: boolean
  }
}

class DarkModeManager {
  private static instance: DarkModeManager
  private _currentTheme: ThemeMode = ThemeMode.LIGHT
  private _isDarkMode: boolean = false
  private _systemPrefersDark: boolean = false
  private _listeners: Array<(isDark: boolean) => void> = []
  private _mediaQuery: MediaQueryList | null = null
  
  constructor() {
    this.initialize()
  }
  
  static getInstance(): DarkModeManager {
    if (!DarkModeManager.instance) {
      DarkModeManager.instance = new DarkModeManager()
    }
    return DarkModeManager.instance
  }
  
  /**
   * 初始化深色模式管理器
   */
  private initialize(): void {
    // 检测系统是否支持深色模式
    if (this.supportsSystemDetection()) {
      this._mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
      this._systemPrefersDark = this._mediaQuery.matches
      
      // 监听系统主题变化
      this._mediaQuery.addEventListener('change', this.handleSystemThemeChange.bind(this))
    }
    
    // 从本地存储读取用户偏好
    this.loadUserPreference()
    
    // 应用当前主题
    this.applyTheme()
  }
  
  /**
   * 检测是否支持系统深色模式检测
   */
  private supportsSystemDetection(): boolean {
    return typeof window !== 'undefined' && 
           window.matchMedia && 
           window.matchMedia('(prefers-color-scheme: dark)').media !== 'not all'
  }
  
  /**
   * 处理系统主题变化
   */
  private handleSystemThemeChange(event: MediaQueryListEvent): void {
    this._systemPrefersDark = event.matches
    
    // 如果当前是自动模式，更新深色模式状态
    if (this._currentTheme === ThemeMode.AUTO) {
      this.updateDarkModeState()
    }
  }
  
  /**
   * 从本地存储加载用户偏好
   */
  private loadUserPreference(): void {
    try {
      const stored = localStorage.getItem('theme-preference')
      if (stored && Object.values(ThemeMode).includes(stored as ThemeMode)) {
        this._currentTheme = stored as ThemeMode
      } else {
        // 如果没有存储的偏好或偏好无效，使用默认值并保存
        this._currentTheme = ThemeMode.LIGHT
        this.saveUserPreference()
      }
    } catch (error) {
      console.warn('无法读取主题偏好设置:', error)
    }
  }
  
  /**
   * 保存用户偏好到本地存储
   */
  private saveUserPreference(): void {
    try {
      localStorage.setItem('theme-preference', this._currentTheme)
    } catch (error) {
      console.warn('无法保存主题偏好设置:', error)
    }
  }
  
  /**
   * 更新深色模式状态
   */
  private updateDarkModeState(): void {
    const previousState = this._isDarkMode
    
    switch (this._currentTheme) {
      case ThemeMode.LIGHT:
        this._isDarkMode = false
        break
      case ThemeMode.DARK:
        this._isDarkMode = true
        break
      case ThemeMode.AUTO:
        this._isDarkMode = this._systemPrefersDark
        break
    }
    
    // 如果状态发生变化，通知监听器
    if (previousState !== this._isDarkMode) {
      this.notifyListeners()
    }
  }
  
  /**
   * 应用当前主题
   */
  private applyTheme(): void {
    this.updateDarkModeState()
    
    // 更新DOM
    if (typeof document !== 'undefined') {
      const root = document.documentElement
      
      if (this._isDarkMode) {
        root.classList.add('dark-mode')
        root.classList.remove('light-mode')
        root.setAttribute('data-theme', 'dark')
      } else {
        root.classList.add('light-mode')
        root.classList.remove('dark-mode')
        root.setAttribute('data-theme', 'light')
      }
    }
  }
  
  /**
   * 通知所有监听器
   */
  private notifyListeners(): void {
    this._listeners.forEach(listener => {
      try {
        listener(this._isDarkMode)
      } catch (error) {
        console.error('深色模式监听器错误:', error)
      }
    })
  }
  
  /**
   * 设置主题模式
   */
  setTheme(theme: ThemeMode): void {
    if (this._currentTheme !== theme) {
      this._currentTheme = theme
      this.saveUserPreference()
      this.applyTheme()
    }
  }
  
  /**
   * 切换深色模式
   */
  toggle(): void {
    switch (this._currentTheme) {
      case ThemeMode.LIGHT:
        this.setTheme(ThemeMode.DARK)
        break
      case ThemeMode.DARK:
        this.setTheme(ThemeMode.LIGHT)
        break
      case ThemeMode.AUTO:
        // 在自动模式下，切换到手动模式
        this.setTheme(this._systemPrefersDark ? ThemeMode.LIGHT : ThemeMode.DARK)
        break
    }
  }
  
  /**
   * 添加深色模式监听器
   */
  addListener(listener: (isDark: boolean) => void): () => void {
    this._listeners.push(listener)
    
    // 立即调用一次
    listener(this._isDarkMode)
    
    // 返回取消监听的函数
    return () => {
      const index = this._listeners.indexOf(listener)
      if (index > -1) {
        this._listeners.splice(index, 1)
      }
    }
  }
  
  /**
   * 获取当前深色模式状态
   */
  getState(): DarkModeState {
    return {
      currentTheme: this._currentTheme,
      systemPrefersDark: this._systemPrefersDark,
      isDarkMode: this._isDarkMode,
      supportedFeatures: {
        systemDetection: this.supportsSystemDetection(),
        manualToggle: true,
        persistence: typeof localStorage !== 'undefined'
      }
    }
  }
  
  /**
   * 获取当前主题
   */
  get currentTheme(): ThemeMode {
    return this._currentTheme
  }
  
  /**
   * 是否为深色模式
   */
  get isDarkMode(): boolean {
    return this._isDarkMode
  }
  
  /**
   * 系统是否偏好深色模式
   */
  get systemPrefersDark(): boolean {
    return this._systemPrefersDark
  }
  
  /**
   * 重置主题设置为默认值
   */
  resetToDefault(): void {
    this._currentTheme = ThemeMode.LIGHT
    this.saveUserPreference()
    this.applyTheme()
  }
  
  /**
   * 销毁管理器
   */
  destroy(): void {
    if (this._mediaQuery) {
      this._mediaQuery.removeEventListener('change', this.handleSystemThemeChange.bind(this))
    }
    this._listeners = []
  }
}

// 导出单例实例
export const darkModeManager = DarkModeManager.getInstance()

// 便捷函数
export const setTheme = (theme: ThemeMode) => darkModeManager.setTheme(theme)
export const toggleDarkMode = () => darkModeManager.toggle()
export const isDarkMode = () => darkModeManager.isDarkMode
export const addDarkModeListener = (listener: (isDark: boolean) => void) => 
  darkModeManager.addListener(listener)
export const getDarkModeState = () => darkModeManager.getState()
export const resetThemeToDefault = () => darkModeManager.resetToDefault()

// DarkModeState 已在上面作为 interface 导出

export default darkModeManager 