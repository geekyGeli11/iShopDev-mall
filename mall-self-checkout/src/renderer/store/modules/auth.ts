/**
 * 用户认证状态管理
 */
import { defineStore } from 'pinia'
import type { MemberInfo } from '@shared/types'

export interface AuthState {
  isLoggedIn: boolean
  memberInfo: MemberInfo | null
  loginTime: number | null
  lastActiveTime: number | null
  autoLogoutTimer: number | null
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    isLoggedIn: false,
    memberInfo: null,
    loginTime: null,
    lastActiveTime: null,
    autoLogoutTimer: null
  }),

  getters: {
    // 获取会员信息
    getMemberInfo: (state) => state.memberInfo,
    
    // 是否已登录
    getIsLoggedIn: (state) => state.isLoggedIn,
    
    // 获取会员等级
    getMemberLevel: (state) => state.memberInfo?.level || '',
    
    // 获取会员积分
    getMemberPoints: (state) => state.memberInfo?.points || 0
  },

  actions: {
    // 会员登录
    async login(phone: string, code: string) {
      try {
        // TODO: 调用登录API
        const response = await fetch('/api/member/login', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ phone, code })
        })
        
        if (response.ok) {
          const memberInfo = await response.json()
          this.setMemberInfo(memberInfo)
          this.startSession()
          return true
        }
        return false
      } catch (error) {
        console.error('登录失败:', error)
        return false
      }
    },

    // 设置会员信息
    setMemberInfo(memberInfo: MemberInfo) {
      this.memberInfo = memberInfo
      this.isLoggedIn = true
    },

    // 开始会话
    startSession() {
      this.loginTime = Date.now()
      this.lastActiveTime = Date.now()
      this.startAutoLogoutTimer()
    },

    // 刷新活跃时间
    refreshActivity() {
      this.lastActiveTime = Date.now()
    },

    // 退出登录
    logout() {
      this.isLoggedIn = false
      this.memberInfo = null
      this.loginTime = null
      this.lastActiveTime = null
      this.clearAutoLogoutTimer()
    },

    // 启动自动退出定时器
    startAutoLogoutTimer() {
      this.clearAutoLogoutTimer()
      this.autoLogoutTimer = window.setTimeout(() => {
        this.logout()
      }, 60000) // 60秒后自动退出
    },

    // 清除自动退出定时器
    clearAutoLogoutTimer() {
      if (this.autoLogoutTimer) {
        clearTimeout(this.autoLogoutTimer)
        this.autoLogoutTimer = null
      }
    }
  }
}) 