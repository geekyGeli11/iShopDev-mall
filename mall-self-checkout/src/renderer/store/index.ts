/**
 * Store 入口
 */

import { createPinia } from 'pinia'
import { createPersistedState } from 'pinia-plugin-persistedstate'

// 创建Pinia实例
export const pinia = createPinia()

// 配置持久化插件
pinia.use(
  createPersistedState({
    // 默认存储到localStorage
    storage: localStorage,
    // 自动序列化
    auto: true,
    // 排除敏感数据的字段
    beforeRestore: (context: any) => {
      // 在恢复状态前的处理
      console.log('恢复状态:', context.store.$id)
    },
    afterRestore: (context: any) => {
      // 在恢复状态后的处理
      console.log('状态已恢复:', context.store.$id)
    }
  })
)

// 导出所有 store
export * from './modules/app'
export * from './modules/auth'
export * from './modules/cart'
export * from './modules/member'
export * from './modules/payment'
export * from './modules/scanner'

// 导入Store模块
import { useMemberStore } from './modules/member'
import { useCartStore } from './modules/cart'
import { usePaymentStore } from './modules/payment'

// 导出Store模块
export { useMemberStore, useCartStore, usePaymentStore }

// 导出Store类型（暂时注释，因为这些接口没有导出）
// export type {
//   MemberState
// } from './modules/member'

// export type {
//   CartState  
// } from './modules/cart'

// export type {
//   PaymentState
// } from './modules/payment'

/**
 * 初始化应用状态
 * 在应用启动时调用，用于恢复用户会话等
 */
export function initializeStores(): void {
  // 恢复会员会话
  const memberStore = useMemberStore()
  memberStore.restoreSession()
  
  // 如果已登录，加载购物车
  if (memberStore.isLoggedIn) {
    const cartStore = useCartStore()
    cartStore.loadCart().catch(error => {
      console.warn('初始化购物车失败:', error)
    })
  }
  
  console.log('应用状态初始化完成')
}

/**
 * 清理应用状态
 * 在应用关闭或重置时调用
 */
export function clearAllStores(): void {
  const memberStore = useMemberStore()
  const cartStore = useCartStore()
  const paymentStore = usePaymentStore()
  
  // 清理会员状态
  memberStore.logout()
  
  // 清理购物车 - 使用本地清理方法
  cartStore.clearLocalCart()
  
  // 重置支付状态
  paymentStore.resetPayment()
  
  console.log('应用状态已清理')
}

/**
 * 监听应用生命周期事件
 */
export function setupStoreListeners(): void {
  // 监听页面可见性变化
  document.addEventListener('visibilitychange', () => {
    const memberStore = useMemberStore()
    
    if (document.visibilityState === 'visible') {
      // 页面变为可见时刷新活跃状态
      if (memberStore.isLoggedIn) {
        memberStore.refreshActivity()
      }
    }
  })
  
  // 监听用户交互事件（刷新活跃状态）
  const events = ['click', 'keydown', 'scroll', 'mousemove']
  let lastActivity = Date.now()
  
  const handleActivity = () => {
    const now = Date.now()
    // 限制频率，最多每5秒触发一次
    if (now - lastActivity > 5000) {
      const memberStore = useMemberStore()
      if (memberStore.isLoggedIn) {
        memberStore.refreshActivity()
      }
      lastActivity = now
    }
  }
  
  events.forEach(event => {
    document.addEventListener(event, handleActivity, { passive: true })
  })
  
  // 监听支付相关事件
  window.addEventListener('payment:success', (event: Event) => {
    const customEvent = event as CustomEvent
    console.log('支付成功事件:', customEvent.detail)
    // 可以在这里添加全局的支付成功处理逻辑
  })
  
  window.addEventListener('payment:failed', (event: Event) => {
    const customEvent = event as CustomEvent
    console.log('支付失败事件:', customEvent.detail)
    // 可以在这里添加全局的支付失败处理逻辑
  })
  
  window.addEventListener('payment:timeout', () => {
    console.log('支付超时事件')
    // 可以在这里添加全局的支付超时处理逻辑
  })
  
  console.log('Store监听器已设置')
}

/**
 * 开发环境调试工具
 */
export function setupDevtools(): void {
  if (import.meta.env.DEV) {
    // 在开发环境中将stores暴露到全局对象，方便调试
    ;(window as any).__STORES__ = {
      member: useMemberStore,
      cart: useCartStore,
      payment: usePaymentStore
    }
    
    console.log('开发调试工具已启用: window.__STORES__')
  }
}

// 默认导出pinia实例
export default pinia 