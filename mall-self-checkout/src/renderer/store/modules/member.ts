/**
 * 会员相关状态管理
 */
import { defineStore } from 'pinia'
import { MemberAPI } from '../../api'
import { envConfig } from '../../../config/env'
import type { 
  MemberLoginParam, 
  MemberCodeLoginParam, 
  GuestLoginParam,
  MemberLoginResult,
  UmsMember,
  UmsGuest,
  ApiResponse
} from '@shared/types'
import { UserType } from '@shared/types'

/**
 * 会员会话信息
 */
interface MemberSession {
  memberId: number
  memberInfo: UmsMember
  loginTime: number
  lastActiveTime: number
  orderInProgress: boolean
  sessionTimeout: number
}

/**
 * 游客会话信息
 */
interface GuestSession {
  guestId: string
  guestInfo: UmsGuest
  loginTime: number
  lastActiveTime: number
  orderInProgress: boolean
}

/**
 * 会员状态
 */
interface MemberState {
  // 登录状态
  isLoggedIn: boolean
  userType: UserType | null
  
  // 会员信息
  memberSession: MemberSession | null
  guestSession: GuestSession | null
  
  // 自动退出计时器
  autoLogoutTimer: NodeJS.Timeout | null
  warningTimer: NodeJS.Timeout | null
  
  // 登录加载状态
  loginLoading: boolean
  
  // 短信验证码
  smsCodeSending: boolean
  smsCodeCountdown: number
}

// 用于导航的路由实例 - 将在main.ts中设置
let routerInstance: any = null

export function setRouterInstance(router: any) {
  routerInstance = router
}

export const useMemberStore = defineStore('member', {
  state: (): MemberState => ({
    isLoggedIn: false,
    userType: null,
    memberSession: null,
    guestSession: null,
    autoLogoutTimer: null,
    warningTimer: null,
    loginLoading: false,
    smsCodeSending: false,
    smsCodeCountdown: 0
  }),

  getters: {
    /**
     * 当前用户ID
     */
    currentUserId: (state): string | null => {
      if (state.userType === UserType.MEMBER && state.memberSession) {
        return state.memberSession.memberId.toString()
      }
      if (state.userType === UserType.GUEST && state.guestSession) {
        return state.guestSession.guestId
      }
      return null
    },

    /**
     * 会员信息
     */
    memberInfo: (state): UmsMember | null => {
      return state.memberSession?.memberInfo || null
    },

    /**
     * 游客信息
     */
    guestInfo: (state): UmsGuest | null => {
      return state.guestSession?.guestInfo || null
    },

    /**
     * 是否为会员登录
     */
    isMemberLogin: (state): boolean => {
      return state.isLoggedIn && state.userType === UserType.MEMBER
    },

    /**
     * 是否为游客登录
     */
    isGuestLogin: (state): boolean => {
      return state.isLoggedIn && state.userType === UserType.GUEST
    },

    /**
     * 脱敏手机号
     */
    maskedPhone: (state): string => {
      const phone = state.memberSession?.memberInfo?.phone
      if (!phone) return ''
      return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
    },

    /**
     * 会员等级名称
     */
    memberLevelName: (state): string => {
      const level = state.memberSession?.memberInfo?.memberLevelId
      if (!level) return '普通会员'
      
      const levelNames = {
        1: '铜牌会员',
        2: '银牌会员', 
        3: '金牌会员',
        4: '白金会员',
        5: '钻石会员'
      }
      
      return levelNames[level as keyof typeof levelNames] || '普通会员'
    },

    /**
     * 会话剩余时间
     */
    sessionRemainingTime: (state): number => {
      if (!state.isLoggedIn) return 0
      
      const session = state.memberSession || state.guestSession
      if (!session) return 0
      
      const elapsed = Date.now() - session.lastActiveTime
      const timeout = state.memberSession?.sessionTimeout || envConfig.member.sessionTimeout
      
      return Math.max(0, timeout * 1000 - elapsed)
    }
  },

  actions: {
    /**
     * 会员手机号登录
     */
    async login(loginParam: MemberLoginParam): Promise<void> {
      this.loginLoading = true
      
      try {
        const response = await MemberAPI.login(loginParam)
        
        if (response.code === 200 && response.data) {
          const { memberInfo, token, tokenHead, expiresIn, memberId } = response.data
          
          // 存储会员会话
          this.memberSession = {
            memberId: memberId,
            memberInfo: memberInfo,
            loginTime: Date.now(),
            lastActiveTime: Date.now(),
            orderInProgress: false,
            sessionTimeout: expiresIn || envConfig.member.sessionTimeout
          }
          
          this.isLoggedIn = true
          this.userType = UserType.MEMBER
          
          // 清除游客token，确保使用会员token
          localStorage.removeItem('token') // 清除游客token（完整格式）
          localStorage.removeItem('guest_id') // 清除游客ID

          // 存储会员token
          localStorage.setItem('access_token', token)
          localStorage.setItem('token_type', tokenHead || 'Bearer ')
          localStorage.setItem('member_id', memberId.toString())

          // 启动自动退出计时器
          this.startAutoLogoutTimer()

          console.log('会员登录成功:', loginParam.telephone)
        } else {
          throw new Error(response.message || '登录失败')
        }
      } catch (error) {
        console.error('会员登录失败:', error)
        throw error
      } finally {
        this.loginLoading = false
      }
    },

    /**
     * 会员号码登录
     */
    async loginByCode(loginParam: MemberCodeLoginParam): Promise<void> {
      this.loginLoading = true
      
      try {
        const response = await MemberAPI.loginByCode(loginParam)
        
        if (response.code === 200 && response.data) {
          const { memberInfo, token, tokenHead, expiresIn, memberId } = response.data
          
          // 存储会员会话
          this.memberSession = {
            memberId: memberId,
            memberInfo: memberInfo,
            loginTime: Date.now(),
            lastActiveTime: Date.now(),
            orderInProgress: false,
            sessionTimeout: expiresIn || envConfig.member.sessionTimeout
          }
          
          this.isLoggedIn = true
          this.userType = UserType.MEMBER
          
          // 清除游客token，确保使用会员token
          localStorage.removeItem('token') // 清除游客token（完整格式）
          localStorage.removeItem('guest_id') // 清除游客ID

          // 存储会员token
          localStorage.setItem('access_token', token)
          localStorage.setItem('token_type', tokenHead || 'Bearer ')
          localStorage.setItem('member_id', memberId.toString())

          // 启动自动退出计时器
          this.startAutoLogoutTimer()

          console.log('会员号码登录成功:', loginParam.memberCode)
        } else {
          throw new Error(response.message || '登录失败')
        }
      } catch (error) {
        console.error('会员号码登录失败:', error)
        throw error
      } finally {
        this.loginLoading = false
      }
    },

    /**
     * 游客登录
     */
    async guestLogin(loginParam?: GuestLoginParam): Promise<void> {
      this.loginLoading = true
      
      try {
        // 获取门店信息
        const storeData = localStorage.getItem('mall_selfcheck_store_selected')
        const selectedStore = storeData ? JSON.parse(storeData) : null

        const response = await MemberAPI.guestLogin(
          loginParam?.deviceId,
          loginParam?.deviceType,
          selectedStore?.storeId,
          selectedStore?.schoolId
        )
        
        if (response.code === 200 && response.data) {
          const { guestId, deviceId, deviceType, token, tokenHead, expiresIn, createTime, isGuest } = response.data
          
          // 构造游客信息对象
          const guestInfo: UmsGuest = {
            id: 0, // 临时ID
            guestId,
            deviceId,
            deviceType: deviceType || 'unknown',
            deviceInfo: undefined,
            loginIp: '',
            sessionTimeout: expiresIn,
            createTime: new Date(createTime),
            updateTime: new Date(),
            lastActiveTime: new Date(),
            hasActiveOrder: false
          }
          
          // 存储游客会话
          this.guestSession = {
            guestId,
            guestInfo,
            loginTime: Date.now(),
            lastActiveTime: Date.now(),
            orderInProgress: false
          }
          
          this.isLoggedIn = true
          this.userType = UserType.GUEST
          
          // 存储token - 与mall-portal保持一致，直接存储完整的token
          const fullToken = `${tokenHead || 'Bearer '}${token}`
          localStorage.setItem('token', fullToken)  // 使用'token'键名，与mall-portal一致
          localStorage.setItem('access_token', token)  // 保留原有格式作为备用
          localStorage.setItem('guest_id', guestId)
          
          console.log('游客登录成功:', guestId)
        } else {
          throw new Error(response.message || '游客登录失败')
        }
      } catch (error) {
        console.error('游客登录失败:', error)
        throw error
      } finally {
        this.loginLoading = false
      }
    },

    /**
     * 发送验证码
     */
    async sendVerifyCode(telephone: string): Promise<void> {
      if (this.smsCodeCountdown > 0) {
        throw new Error('请等待验证码倒计时结束')
      }
      
      this.smsCodeSending = true
      
      try {
        const response = await MemberAPI.sendVerifyCode(telephone)
        
        if (response.code === 200) {
          // 开始倒计时
          this.startSmsCountdown()
          console.log('验证码发送成功')
        } else {
          throw new Error(response.message || '验证码发送失败')
        }
      } catch (error) {
        console.error('验证码发送失败:', error)
        throw error
      } finally {
        this.smsCodeSending = false
      }
    },

    /**
     * 游客登录（带门店信息）
     */
    async guestLoginWithStore(storeId: number, schoolId: number): Promise<void> {
      this.loginLoading = true

      try {
        // 构建带门店信息的登录参数
        const loginParam: GuestLoginParam = {
          deviceId: this.generateDeviceId(),
          deviceType: 'selfcheck',
          storeId,
          schoolId
        }

        console.log('🏪 游客登录（带门店信息）:', loginParam)

        const response = await MemberAPI.guestLogin(
          loginParam.deviceId,
          loginParam.deviceType,
          loginParam.storeId,
          loginParam.schoolId
        )

        if (response.code === 200 && response.data) {
          const { guestId, deviceId, token, tokenHead, expiresIn, createTime } = response.data

          // 设置游客会话信息
          this.guestSession = {
            guestId,
            guestInfo: {
              guestId,
              deviceId,
              deviceType: 'selfcheck',
              createTime: new Date(createTime).getTime(),
              lastActiveTime: Date.now(),
              schoolId,
              storeId
            } as UmsGuest,
            loginTime: Date.now(),
            lastActiveTime: Date.now(),
            orderInProgress: false
          }

          // 设置登录状态
          this.isLoggedIn = true
          this.userType = UserType.GUEST
          this.loginLoading = false

          // 保存token和游客信息
          const fullToken = `${tokenHead || 'Bearer '}${token}`
          localStorage.setItem('token', fullToken)
          localStorage.setItem('access_token', token)
          localStorage.setItem('guest_id', guestId)

          // 保存门店信息
          localStorage.setItem('current_store_id', String(storeId))
          localStorage.setItem('current_school_id', String(schoolId))

          console.log('✅ 游客登录（带门店信息）成功:', { guestId, storeId, schoolId })
        } else {
          throw new Error(response.message || '游客登录失败')
        }
      } catch (error) {
        console.error('❌ 游客登录（带门店信息）失败:', error)
        throw error
      } finally {
        this.loginLoading = false
      }
    },

    /**
     * 设置游客模式（快捷方法）
     */
    async setGuestMode(): Promise<void> {
      await this.guestLogin()
    },

    /**
     * 刷新活跃时间
     */
    refreshActivity(): void {
      if (!this.isLoggedIn) return
      
      const now = Date.now()
      
      if (this.memberSession) {
        this.memberSession.lastActiveTime = now
        this.resetAutoLogoutTimer()
      }
      
      if (this.guestSession) {
        this.guestSession.lastActiveTime = now
      }
      
      // 发送到后端
      MemberAPI.refreshActivity(this.currentUserId || undefined).catch((error: any) => {
        console.warn('刷新活跃状态失败:', error)
      })
    },

    /**
     * 支付完成后自动退出
     */
    async onPaymentCompleted(orderId: number): Promise<void> {
      if (!this.isLoggedIn) return

      // 显示订单完成提示
      this.showOrderCompletedMessage()
      
      // 设置订单进行中状态
      if (this.memberSession) {
        this.memberSession.orderInProgress = false
      }
      if (this.guestSession) {
        this.guestSession.orderInProgress = false
      }
      
      // 延迟后自动退出（支持会员和游客）
      const delayTime = (envConfig.member?.autoLogoutDelay || 3) * 1000
      setTimeout(() => {
        this.autoLogoutAfterPayment(orderId)
      }, delayTime)
    },

    /**
     * 支付完成后自动退出
     */
    async autoLogoutAfterPayment(orderId: number): Promise<void> {
      const userType = this.userType === UserType.MEMBER ? '会员' : '游客'
      console.log(`订单 ${orderId} 支付完成，执行${userType}自动退出登录`)
      
      try {
        // 发送退出请求（静默处理）
        if (this.currentUserId) {
          await MemberAPI.logout(this.currentUserId)
        }
      } catch (error) {
        console.warn('退出登录请求失败，继续执行本地清理:', error)
      }
      
      // 执行本地清理
      this.performLogout()
      
      // 显示提示信息
      this.showLogoutMessage('支付完成，已自动退出登录')
      
      // 导航到首页
      this.navigateToHome()
    },

    /**
     * 退出登录
     */
    async logout(): Promise<void> {
      try {
        // 发送退出请求
        await MemberAPI.logout(this.currentUserId || undefined)
      } catch (error) {
        console.warn('退出登录请求失败:', error)
      }
      
      this.performLogout()
      this.showLogoutMessage()
      this.navigateToHome()
    },

    /**
     * 强制退出
     */
    forceLogout(): void {
      this.performLogout()
      this.showLogoutMessage('会话已过期，请重新登录')
      this.navigateToHome()
    },

    /**
     * 执行退出清理
     */
    performLogout(): void {
      this.clearAutoLogoutTimer()
      this.clearRelatedData()
      
      // 重置状态
      this.isLoggedIn = false
      this.userType = null
      this.memberSession = null
      this.guestSession = null
      this.loginLoading = false
      this.smsCodeSending = false
      this.smsCodeCountdown = 0
      
      // 清除本地存储
      localStorage.removeItem('token')        // 新的token格式
      localStorage.removeItem('access_token') // 旧的token格式
      localStorage.removeItem('token_type')   // token类型
      localStorage.removeItem('member_id')
      localStorage.removeItem('guest_id')
    },

    /**
     * 启动自动退出计时器
     */
    startAutoLogoutTimer(): void {
      this.clearAutoLogoutTimer()
      
      const warningTime = 30 * 1000 // 30秒警告时间
      const sessionTime = envConfig.member.sessionTimeout * 1000
      
      // 警告计时器
      this.warningTimer = setTimeout(() => {
        this.showTimeoutWarning()
      }, sessionTime - warningTime)
      
      // 自动退出计时器
      this.autoLogoutTimer = setTimeout(() => {
        this.forceLogout()
      }, sessionTime)
    },

    /**
     * 重置自动退出计时器
     */
    resetAutoLogoutTimer(): void {
      if (this.isLoggedIn && this.userType === UserType.MEMBER) {
        this.startAutoLogoutTimer()
      }
    },

    /**
     * 清除自动退出计时器
     */
    clearAutoLogoutTimer(): void {
      if (this.autoLogoutTimer) {
        clearTimeout(this.autoLogoutTimer)
        this.autoLogoutTimer = null
      }
      
      if (this.warningTimer) {
        clearTimeout(this.warningTimer)
        this.warningTimer = null
      }
    },

    /**
     * 开始短信倒计时
     */
    startSmsCountdown(): void {
      this.smsCodeCountdown = 60
      
      const timer = setInterval(() => {
        this.smsCodeCountdown--
        if (this.smsCodeCountdown <= 0) {
          clearInterval(timer)
        }
      }, 1000)
    },

    /**
     * 检查登录状态
     */
    async checkLoginStatus(): Promise<void> {
      const token = localStorage.getItem('access_token')
      if (!token) {
        this.performLogout()
        return
      }
      
      try {
        // 这里可以调用API验证token有效性
        this.restoreSession()
      } catch (error) {
        console.error('检查登录状态失败:', error)
        this.performLogout()
      }
    },

    /**
     * 恢复会话
     */
    restoreSession(): void {
      const memberId = localStorage.getItem('member_id')
      const guestId = localStorage.getItem('guest_id')
      
      if (memberId) {
        this.userType = UserType.MEMBER
        this.isLoggedIn = true
        // 这里可以恢复更多会员信息
      } else if (guestId) {
        this.userType = UserType.GUEST
        this.isLoggedIn = true
        // 这里可以恢复更多游客信息
      }
    },

    /**
     * 清理相关数据
     */
    clearRelatedData(): void {
      // 清理购物车、支付等相关数据
      // 这里可以调用其他store的清理方法
      
      // 清理localStorage中的所有相关数据
      localStorage.removeItem('cart_items')
      localStorage.removeItem('guest_session')
      localStorage.removeItem('payment_session')
      localStorage.removeItem('order_in_progress')
      localStorage.removeItem('scan_session')
      
      // 清理sessionStorage
      sessionStorage.clear()
      
      console.log('已清理所有相关数据')
    },

    /**
     * 显示订单完成消息
     */
    showOrderCompletedMessage(): void {
      // 显示订单完成提示
      console.log('订单已完成，即将自动退出')
    },

    /**
     * 显示超时警告
     */
    showTimeoutWarning(): void {
      // 显示会话即将过期警告
      console.log('会话即将过期，请及时操作')
    },

    /**
     * 显示退出消息
     */
    showLogoutMessage(message: string = '已退出登录'): void {
      console.log(message)
    },

    /**
     * 导航到首页
     */
    navigateToHome(): void {
      if (routerInstance) {
        routerInstance.replace('/home')
        console.log('已导航到首页')
      } else {
        console.warn('路由实例未设置，无法导航到首页')
      }
    },

    /**
     * 生成设备ID
     */
    generateDeviceId(): string {
      // 尝试从本地存储获取已有的设备ID
      let deviceId = localStorage.getItem('device_id')

      if (!deviceId) {
        // 生成新的设备ID
        const timestamp = Date.now()
        const random = Math.random().toString(36).substring(2, 15)
        deviceId = `selfcheck_${timestamp}_${random}`

        // 保存到本地存储
        localStorage.setItem('device_id', deviceId)
        console.log('🆔 生成新的设备ID:', deviceId)
      } else {
        console.log('🆔 使用已有的设备ID:', deviceId)
      }

      return deviceId
    }
  }
})