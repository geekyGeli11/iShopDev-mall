/**
 * 测试API工具类
 * 用于验证各种功能
 */

import { RequestUtil } from '../request'

export class TestAPI {
  /**
   * 测试游客登录和数据库记录创建
   */
  static async testGuestLoginAndDB(): Promise<void> {
    console.log('开始测试游客登录和数据库记录创建...')
    
    try {
      // 1. 游客登录
      console.log('1. 执行游客登录...')
      const loginResponse = await RequestUtil.post('/member/guestLogin', {
        deviceType: 'test'
      })
      
      console.log('游客登录响应:', loginResponse)
      
      if (loginResponse.code === 200 && loginResponse.data) {
        const { guestId, deviceId, token } = loginResponse.data
        
        // 2. 存储token
        localStorage.setItem('token', 'Bearer ' + token)
        localStorage.setItem('access_token', token)
        
        console.log('2. Token已存储:', token.substring(0, 50) + '...')
        
        // 3. 获取游客信息验证数据库记录
        console.log('3. 获取游客信息验证数据库记录...')
        const guestInfoResponse = await RequestUtil.get(`/member/guestInfo?guestId=${guestId}`)
        
        console.log('游客信息查询响应:', guestInfoResponse)
        
        // 4. 检查当前登录状态
        console.log('4. 检查当前登录状态...')
        const currentUserResponse = await RequestUtil.get('/member/getCurrentUser')
        
        console.log('当前登录状态响应:', currentUserResponse)
        
        // 5. 测试购物车功能（需要用户ID）
        console.log('5. 测试购物车功能...')
        const cartResponse = await RequestUtil.get('/cart/list')
        
        console.log('购物车查询响应:', cartResponse)
        
        return {
          success: true,
          guestId,
          deviceId,
          message: '游客登录和数据库记录创建测试成功'
        }
        
      } else {
        throw new Error('游客登录失败: ' + JSON.stringify(loginResponse))
      }
      
    } catch (error) {
      console.error('测试失败:', error)
      throw error
    }
  }

  /**
   * 测试会员登录流程
   */
  static async testMemberLogin(phone: string = '13800138001', code: string = '123456'): Promise<void> {
    console.log('开始测试会员登录流程...')
    
    try {
      // 1. 发送验证码
      console.log('1. 发送验证码...')
      const smsResponse = await RequestUtil.post('/member/sendVerifyCode', {
        telephone: phone
      })
      
      console.log('验证码发送响应:', smsResponse)
      
      // 2. 会员登录
      console.log('2. 执行会员登录...')
      const loginResponse = await RequestUtil.post('/member/login', {
        telephone: phone,
        authCode: code
      })
      
      console.log('会员登录响应:', loginResponse)
      
      if (loginResponse.code === 200 && loginResponse.data) {
        const { token, memberId, memberInfo } = loginResponse.data
        
        // 3. 存储token
        localStorage.setItem('token', 'Bearer ' + token)
        localStorage.setItem('access_token', token)
        
        console.log('3. 会员Token已存储:', token.substring(0, 50) + '...')
        
        // 4. 获取会员信息
        console.log('4. 获取会员信息...')
        const memberInfoResponse = await RequestUtil.get('/member/info')
        
        console.log('会员信息查询响应:', memberInfoResponse)
        
        return {
          success: true,
          memberId,
          memberInfo,
          message: '会员登录测试成功'
        }
        
      } else {
        throw new Error('会员登录失败: ' + JSON.stringify(loginResponse))
      }
      
    } catch (error) {
      console.error('会员登录测试失败:', error)
      throw error
    }
  }

  /**
   * 清理测试数据
   */
  static clearTestData(): void {
    localStorage.removeItem('token')
    localStorage.removeItem('access_token')
    console.log('测试数据已清理')
  }
}

// 导出到全局，方便在控制台调试
(window as any).TestAPI = TestAPI 