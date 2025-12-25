import { MemberAPI } from './modules/member'
import { ProductAPI } from './modules/product'
import { CartAPI } from './modules/cart'
import { PaymentAPI } from './modules/payment'
import { OrderAPI } from './modules/order'
import { CouponAPI } from './modules/coupon'
import type { 
  MemberLoginParam, 
  GuestLoginParam,
  ProductScanParam,
  CartItemParam,
  PaymentQRParam,
  OrderCreateParam
} from '@shared/types'

/**
 * API集成测试类
 */
export class APITester {
  
  /**
   * 测试会员登录API
   */
  static async testMemberLogin() {
    try {
      console.log('=== 测试会员登录 ===')
      
      // 1. 发送验证码
      const phone = '13800138000'
      await MemberAPI.sendVerifyCode(phone)
      console.log('✓ 发送验证码成功')
      
      // 2. 手机号登录
      const loginParam: MemberLoginParam = {
        telephone: phone,
        verifyCode: '123456'
      }
      const loginResult = await MemberAPI.login(loginParam)
      console.log('✓ 会员登录成功:', loginResult)
      
      return loginResult
    } catch (error) {
      console.error('✗ 会员登录测试失败:', error)
      throw error
    }
  }
  
  /**
   * 测试游客登录API
   */
  static async testGuestLogin() {
    try {
      console.log('=== 测试游客登录 ===')
      
      const guestParam: GuestLoginParam = {
        deviceId: 'TEST_DEVICE_001',
        deviceType: 'SELF_CHECKOUT',
        terminalCode: 'SC001'
      }
      
      const guestResult = await MemberAPI.guestLogin(
        guestParam.deviceId,
        guestParam.deviceType,
        1, // 测试门店ID
        1  // 测试学校ID
      )
      console.log('✓ 游客登录成功:', guestResult)
      
      return guestResult
    } catch (error) {
      console.error('✗ 游客登录测试失败:', error)
      throw error
    }
  }
  
  /**
   * 测试商品扫描API
   */
  static async testProductScan() {
    try {
      console.log('=== 测试商品扫描 ===')
      
      const scanParam: ProductScanParam = {
        barcode: '6901234567890',
        scanType: 'BARCODE',
        needStockCheck: true,
        needPromotionInfo: true
      }
      
      const product = await ProductAPI.scanProduct(scanParam)
      console.log('✓ 商品扫描成功:', product)
      
      return product
    } catch (error) {
      console.error('✗ 商品扫描测试失败:', error)
      throw error
    }
  }
  
  /**
   * 测试购物车API
   */
  static async testCartOperations() {
    try {
      console.log('=== 测试购物车操作 ===')
      
      // 1. 添加商品到购物车
      const addParam: CartItemParam = {
        productId: 1,
        skuId: 101,
        quantity: 2,
        operation: 'ADD',
        remark: 'API测试添加'
      }
      
      await CartAPI.addItem(addParam)
      console.log('✓ 添加商品到购物车成功')
      
      // 2. 获取购物车信息
      const cart = await CartAPI.getCart()
      console.log('✓ 获取购物车成功:', cart)
      
      // 3. 更新商品数量
      const updateParam: CartItemParam = {
        productId: 1,
        skuId: 101,
        quantity: 3,
        operation: 'UPDATE'
      }
      
      await CartAPI.updateItem(updateParam)
      console.log('✓ 更新商品数量成功')
      
      return cart
    } catch (error) {
      console.error('✗ 购物车操作测试失败:', error)
      throw error
    }
  }
  
  /**
   * 测试支付API
   */
  static async testPaymentOperations() {
    try {
      console.log('=== 测试支付操作 ===')
      
      // 1. 生成支付二维码
      const qrParam: PaymentQRParam = {
        orderId: 1001,
        amount: 100.50,
        payType: 'WECHAT',
        title: 'API测试支付',
        description: '测试订单支付',
        terminalCode: 'SC001',
        deviceInfo: 'Test Device',
        expireMinutes: 5
      }
      
      const qrResult = await PaymentAPI.generatePaymentQR(qrParam)
      console.log('✓ 生成支付二维码成功:', qrResult)
      
      // 2. 查询支付状态
      const statusResult = await PaymentAPI.getPaymentStatus(qrResult.data.paymentId)
      console.log('✓ 查询支付状态成功:', statusResult)
      
      return qrResult
    } catch (error) {
      console.error('✗ 支付操作测试失败:', error)
      throw error
    }
  }
  
  /**
   * 测试订单API
   */
  static async testOrderOperations() {
    try {
      console.log('=== 测试订单操作 ===')
      
      // 创建订单
      const orderParam: OrderCreateParam = {
        orderType: 'QUICK',
        orderItems: [
          {
            productId: 1,
            skuId: 101,
            quantity: 1,
            unitPrice: 50.00,
            totalPrice: 50.00,
            remark: 'API测试商品'
          }
        ],
        payType: 'WECHAT',
        deliveryType: 0,
        note: 'API测试订单',
        terminalCode: 'SC001',
        deviceInfo: 'Test Device',
        expectedAmount: 50.00
      }
      
      const order = await OrderAPI.createOrder(orderParam)
      console.log('✓ 创建订单成功:', order)
      
      // 查询订单详情
      const orderDetail = await OrderAPI.getOrderDetail(order.data.orderId)
      console.log('✓ 查询订单详情成功:', orderDetail)
      
      return order
    } catch (error) {
      console.error('✗ 订单操作测试失败:', error)
      throw error
    }
  }
  
  /**
   * 测试优惠券API
   */
  static async testCouponOperations() {
    try {
      console.log('=== 测试优惠券操作 ===')
      
      // 获取可用优惠券
      const coupons = await CouponAPI.getAvailableCoupons({
        totalAmount: 100.00,
        productIds: [1, 2, 3]
      })
      console.log('✓ 获取可用优惠券成功:', coupons)
      
      // 获取会员优惠券列表
      const memberCoupons = await CouponAPI.getMemberCoupons({
        status: 'UNUSED',
        page: 1,
        size: 10
      })
      console.log('✓ 获取会员优惠券成功:', memberCoupons)
      
      return coupons
    } catch (error) {
      console.error('✗ 优惠券操作测试失败:', error)
      throw error
    }
  }
  
  /**
   * 运行完整的API测试流程
   */
  static async runFullTest() {
    try {
      console.log('🚀 开始完整API测试流程...')
      
      // 1. 游客登录
      const guestResult = await this.testGuestLogin()
      
      // 2. 商品扫描
      const product = await this.testProductScan()
      
      // 3. 购物车操作
      const cart = await this.testCartOperations()
      
      // 4. 支付操作
      const payment = await this.testPaymentOperations()
      
      // 5. 订单操作
      const order = await this.testOrderOperations()
      
      // 6. 优惠券操作
      const coupons = await this.testCouponOperations()
      
      console.log('🎉 所有API测试通过!')
      
      return {
        guest: guestResult,
        product,
        cart,
        payment,
        order,
        coupons
      }
    } catch (error) {
      console.error('❌ API测试失败:', error)
      throw error
    }
  }
  
  /**
   * 测试会员完整流程
   */
  static async runMemberTest() {
    try {
      console.log('👤 开始会员完整流程测试...')
      
      // 1. 会员登录
      const memberResult = await this.testMemberLogin()
      
      // 2. 商品扫描
      const product = await this.testProductScan()
      
      // 3. 购物车操作
      const cart = await this.testCartOperations()
      
      // 4. 优惠券操作
      const coupons = await this.testCouponOperations()
      
      // 5. 支付操作
      const payment = await this.testPaymentOperations()
      
      // 6. 订单操作
      const order = await this.testOrderOperations()
      
      console.log('🎉 会员流程测试通过!')
      
      return {
        member: memberResult,
        product,
        cart,
        coupons,
        payment,
        order
      }
    } catch (error) {
      console.error('❌ 会员流程测试失败:', error)
      throw error
    }
  }
}

// 开发环境下自动暴露测试工具到全局
if (import.meta.env.DEV) {
  (window as any).APITester = APITester
  console.log('💡 开发模式：可在控制台使用 APITester 进行API测试')
  console.log('  - APITester.runFullTest() // 运行完整测试')
  console.log('  - APITester.runMemberTest() // 运行会员测试')
  console.log('  - APITester.testMemberLogin() // 测试会员登录')
  console.log('  - APITester.testGuestLogin() // 测试游客登录')
}

export default APITester 