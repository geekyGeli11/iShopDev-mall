import { request } from '../request'
import { API_ENDPOINTS } from '@shared/constants/api'
import type { 
  MemberLoginParam, 
  MemberCodeLoginParam,
  GuestLoginParam,
  MemberLoginResult,
  UmsMember,
  UmsGuest,
  ApiResponse 
} from '@shared/types'

/**
 * 会员相关API服务
 */
export class MemberAPI {
  /**
   * 会员手机号登录
   * @param loginParam 登录参数
   * @returns 登录结果
   */
  static async login(loginParam: MemberLoginParam): Promise<ApiResponse<{
    memberInfo: UmsMember
    token: string
    tokenHead: string
    memberId: number
    sessionId: string
    expiresIn: number
  }>> {
    console.log('🔍 MemberAPI.login 调用参数:', loginParam)
    console.log('🔍 telephone 类型:', typeof loginParam.telephone)
    console.log('🔍 verifyCode 类型:', typeof loginParam.verifyCode)
    console.log('🔍 telephone 值:', loginParam.telephone)
    console.log('🔍 verifyCode 值:', loginParam.verifyCode)

    return request.post(API_ENDPOINTS.MEMBER_LOGIN, loginParam)
  }

  /**
   * 会员号码登录
   * @param loginParam 会员号码登录参数
   * @returns 登录结果
   */
  static async loginByCode(loginParam: MemberCodeLoginParam): Promise<ApiResponse<{
    memberInfo: UmsMember
    token: string
    tokenHead: string
    memberId: number
    memberCode: string
    sessionId: string
    expiresIn: number
  }>> {
    return request.post(API_ENDPOINTS.MEMBER_LOGIN_BY_CODE, loginParam)
  }

  /**
   * 游客登录
   * @param deviceId 设备ID
   * @param deviceType 设备类型
   * @param storeId 门店ID
   * @param schoolId 学校ID
   * @returns 游客信息
   */
  static async guestLogin(
    deviceId?: string,
    deviceType?: string,
    storeId?: number,
    schoolId?: number
  ): Promise<ApiResponse<{
    guestId: string
    deviceId: string
    deviceType: string
    token: string
    tokenHead: string
    expiresIn: number
    createTime: string
    isGuest: boolean
  }>> {
    // 使用表单格式发送参数，符合@RequestParam期望
    const formData = new URLSearchParams()
    if (deviceId) formData.append('deviceId', deviceId)
    if (deviceType) formData.append('deviceType', deviceType)
    if (storeId) formData.append('storeId', String(storeId))
    if (schoolId) formData.append('schoolId', String(schoolId))

    return request.post(API_ENDPOINTS.MEMBER_GUEST_LOGIN, formData, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    })
  }

  /**
   * 发送验证码
   * @param telephone 手机号
   * @returns 发送结果
   */
  static async sendVerifyCode(telephone: string): Promise<ApiResponse<{
    message: string
    waitTime: number
    canResend: boolean
  }>> {
    // 使用表单格式发送参数，符合@RequestParam期望
    const formData = new URLSearchParams()
    formData.append('telephone', telephone)
    
    return request.post(API_ENDPOINTS.MEMBER_SEND_CODE, formData, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    })
  }

  /**
   * 退出登录
   * @param guestId 游客ID（可选）
   * @returns 退出结果
   */
  static async logout(guestId?: string): Promise<ApiResponse<string>> {
    // 使用表单格式发送参数，符合@RequestParam期望
    const formData = new URLSearchParams()
    if (guestId) formData.append('guestId', guestId)
    
    return request.post(API_ENDPOINTS.MEMBER_LOGOUT, formData, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    })
  }

  /**
   * 刷新活跃状态
   * @param guestId 游客ID（可选）
   * @returns 刷新结果
   */
  static async refreshActivity(guestId?: string): Promise<ApiResponse<string>> {
    // 使用表单格式发送参数，符合@RequestParam期望
    const formData = new URLSearchParams()
    if (guestId) formData.append('guestId', guestId)
    
    return request.post(API_ENDPOINTS.MEMBER_REFRESH_ACTIVITY, formData, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    })
  }

  /**
   * 获取当前会员信息
   * @returns 会员信息
   */
  static async getMemberInfo(): Promise<ApiResponse<{
    member: MemberLoginResult
    isGuest: boolean
    sessionValid: boolean
  }>> {
    return request.get(API_ENDPOINTS.MEMBER_INFO)
  }

  /**
   * 检查登录状态
   * @returns 登录状态
   */
  static async checkLogin(): Promise<ApiResponse<{
    isLoggedIn: boolean
    userType: 'MEMBER' | 'GUEST'
    loginTime: number
    expiresIn: number
  }>> {
    return request.get(API_ENDPOINTS.MEMBER_CHECK_LOGIN)
  }

  /**
   * 获取游客信息
   * @param guestId 游客ID
   * @returns 游客信息
   */
  static async getGuestInfo(guestId: string): Promise<ApiResponse<{
    guestId: string
    deviceId: string
    deviceType: string
    createTime: number
    lastActiveTime: number
    hasActiveOrder: boolean
  }>> {
    return request.get(API_ENDPOINTS.MEMBER_GUEST_INFO, { guestId })
  }

  /**
   * 获取当前登录用户信息
   * @returns 用户信息
   */
  static async getCurrentUser(): Promise<ApiResponse<{
    userType: 'MEMBER' | 'GUEST'
    userId: string
    userInfo: any
    sessionInfo: any
  }>> {
    return request.get(API_ENDPOINTS.MEMBER_CURRENT_USER)
  }

  /**
   * 检查会话状态
   * @param guestId 游客ID（可选）
   * @returns 会话状态
   */
  static async checkSession(guestId?: string): Promise<ApiResponse<{
    sessionValid: boolean
    userType: 'MEMBER' | 'GUEST'
    expiresIn: number
    needRefresh: boolean
  }>> {
    const params = guestId ? { guestId } : {}
    return request.get(API_ENDPOINTS.MEMBER_CHECK_SESSION, params)
  }

  /**
   * 获取会员号码
   * @returns 会员号码
   */
  static async getMemberCode(): Promise<ApiResponse<{
    memberCode: string
    qrCodeUrl: string
    expiresIn: number
  }>> {
    return request.get(API_ENDPOINTS.MEMBER_GET_CODE)
  }

  /**
   * 生成会员二维码
   * @returns 二维码信息
   */
  static async generateQRCode(): Promise<ApiResponse<{
    qrCodeUrl: string
    qrCodeText: string
    memberCode: string
    expiresIn: number
  }>> {
    return request.get(API_ENDPOINTS.MEMBER_GENERATE_QR)
  }

  /**
   * 获取会员余额信息
   * @returns 余额信息
   */
  static async getBalanceInfo(): Promise<ApiResponse<{
    balance: number
    frozenBalance: number
    totalRecharge: number
    totalConsume: number
  }>> {
    return request.get(API_ENDPOINTS.MEMBER_BALANCE_INFO)
  }
}