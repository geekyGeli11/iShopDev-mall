<!--
  @deprecated 此页面已废弃，不再推荐使用

  当前项目的支付流程主要通过 PaymentDialog.vue 组件实现，该组件在 ScanPage.vue 中作为弹窗使用。

  PaymentPage.vue 作为独立页面保留，但不再是主要的支付实现。
  如需修改支付流程，请优先修改 PaymentDialog.vue 组件。

  主要支付流程位置：
  - 组件: src/renderer/components/business/PaymentDialog.vue
  - 使用位置: src/renderer/views/scan/ScanPage.vue

  此文件保留的原因：
  1. 可能有其他入口仍在使用此页面
  2. 作为备用支付页面
  3. 保持路由完整性

  最后更新: 2025-11-01
-->
<template>
  <div class="payment-page">
    <!-- 头部导航 -->
    <van-nav-bar 
      title="确认支付" 
      left-arrow 
      @click-left="handleBack"
      fixed
      placeholder
    >
      <template #right>
        <van-button size="small" type="primary" @click="goToHome">
          <van-icon name="home-o" />
          首页
        </van-button>
      </template>
    </van-nav-bar>

    <!-- 加载状态 -->
    <div v-if="!orderData.finalAmount" class="loading-container">
      <van-loading size="24" text="加载支付信息..." />
    </div>

    <!-- 支付界面 -->
    <div v-else class="payment-content">
      <!-- 支付金额 -->
      <div class="amount-section">
        <div class="amount-label">应付金额</div>
        <div class="amount-value">¥{{ (orderData.finalAmount || 0).toFixed(2) }}</div>
        <div class="amount-desc">共{{ orderData.items?.length || 0 }}件商品</div>
      </div>

      <!-- 支付图标区域 -->
      <div class="payment-icon-section">
        <div class="payment-icon">
          <van-icon name="scan" size="80" color="#1989fa" />
        </div>
        <div class="payment-tip">请使用下方支付方式的付款码</div>
      </div>

      <!-- 支付方式 -->
      <div class="payment-methods">
        <div class="method-title">支持的支付方式</div>
        <div class="method-icons">
          <div 
            v-for="method in paymentMethods" 
            :key="method.id"
            class="method-item"
            :class="{ active: selectedPaymentMethod === method.id }"
            @click="selectedPaymentMethod = method.id"
          >
            <van-icon :name="method.icon" size="32" :color="method.color" />
            <span class="method-name">{{ method.name }}</span>
          </div>
        </div>
      </div>

      <!-- 确认支付按钮 -->
      <div class="payment-footer">
        <van-button 
          type="primary"
          size="large"
          :loading="creating || paying"
          round
          block
          @click="startScanPayment"
        >
          {{ creating ? '创建订单中...' : paying ? '等待扫码支付...' : '确认支付' }}
        </van-button>
      </div>
    </div>

    <!-- 扫码支付状态弹窗 -->
    <van-popup 
      v-model:show="showScanStatus" 
      position="center"
      :style="{ width: '90%', 'max-width': '400px' } as any"
      :closeable="false"
      round
    >
      <div class="scan-status-popup">
        <div class="status-header">
          <h3 class="status-title">等待扫码支付</h3>
          <p class="status-amount">¥{{ (orderData.finalAmount || 0).toFixed(2) }}</p>
        </div>
        
        <div class="status-content">
          <div class="scan-animation">
            <van-loading size="48" color="#1989fa" />
          </div>
          <p class="scan-tip">请使用{{ currentPaymentMethod?.name }}扫描设备上的付款码</p>
          <p class="scan-desc">扫码设备将自动识别您的付款码并完成支付</p>
        </div>
        
        <div class="status-actions">
          <van-button 
            v-if="testMode"
            type="primary" 
            size="large"
            @click="showTestPanel = true"
            style="margin-bottom: 12px;"
          >
            测试扫码
          </van-button>
          
          <van-button 
            type="default" 
            size="large"
            @click="cancelScanPayment"
          >
            取消支付
          </van-button>
        </div>
      </div>
    </van-popup>

    <!-- 支付结果弹窗 -->
    <van-popup 
      v-model:show="showPaymentResult" 
      position="center"
      :style="{ width: '90%', 'max-width': '400px' } as any"
      :closeable="false"
      round
    >
      <div class="payment-result-popup">
        <div class="result-content">
          <van-icon 
            :name="paymentSuccess ? 'checked' : 'close'"
            :color="paymentSuccess ? '#07c160' : '#ee0a24'"
            size="64"
            class="result-icon"
          />
          
          <h3 class="result-title">
            {{ paymentSuccess ? '支付成功' : '支付失败' }}
          </h3>
          
          <p class="result-message">
            {{ paymentMessage }}
          </p>
          
          <div v-if="paymentSuccess && orderInfo" class="order-summary">
            <div class="summary-item">
              <span class="label">订单号：</span>
              <span class="value">{{ orderInfo.orderSn }}</span>
            </div>
            <div class="summary-item">
              <span class="label">支付金额：</span>
              <span class="value">¥{{ (orderInfo.amount || 0).toFixed(2) }}</span>
            </div>
          </div>
        </div>
        
        <div class="result-actions">
          <van-button 
            v-if="paymentSuccess"
            type="primary" 
            block 
            round
            @click="goToReceipt"
          >
            查看小票
          </van-button>
          
          <van-button 
            v-else
            type="primary" 
            block 
            round
            @click="retryPayment"
          >
            重新支付
          </van-button>
          
          <van-button 
            type="default" 
            block 
            round
            @click="goToHome"
            class="back-btn"
          >
            返回首页
          </van-button>
        </div>
      </div>
    </van-popup>

    <!-- 测试扫码弹窗 -->
    <van-popup 
      v-model:show="showTestPanel" 
      position="center"
      :style="{ width: '90%', 'max-width': '400px' } as any"
      round
    >
      <div class="test-scan-popup">
        <div class="test-header">
          <h3 class="test-title">测试扫码支付</h3>
          <p class="test-desc">输入付款码测试支付流程</p>
        </div>
        
        <div class="test-content">
          <div class="test-input-section">
            <van-field
              v-model="testPaymentCode"
              label="付款码"
              placeholder="请输入18位付款码"
              maxlength="20"
              clearable
            />
          </div>
          
          <div class="test-preset-codes">
            <p class="preset-title">快捷选择:</p>
            <div class="preset-buttons">
              <van-button 
                size="small" 
                type="default"
                @click="useTestCode('wechat')"
              >
                微信测试码
              </van-button>
              <van-button 
                size="small" 
                type="default"
                @click="useTestCode('alipay')"
              >
                支付宝测试码
              </van-button>
            </div>
          </div>
        </div>
        
        <div class="test-actions">
          <van-button 
            type="primary" 
            block 
            round
            @click="simulateScanPayment"
            :disabled="!testPaymentCode"
          >
            确认支付
          </van-button>
          
          <van-button 
            type="default" 
            block 
            round
            @click="showTestPanel = false"
            style="margin-top: 12px;"
          >
            取消
          </van-button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showLoadingToast, closeToast, showNotify, showConfirmDialog } from 'vant'
import { useMemberStore } from '@/store/modules/member'
import { useCartStore } from '@/store/modules/cart'
import { OrderAPI } from '@/api/modules/order'
import { PaymentAPI } from '@/api/modules/payment'
import { PaymentMode } from '@shared/types/payment'
import { PaymentMethod, PaymentResultVO } from '@shared/types/index'
import { speakPaymentSuccess, speakPaymentFailure, speakScanToPay, TTSManager } from '@/utils/tts'

const router = useRouter()
const route = useRoute()
const memberStore = useMemberStore()
const cartStore = useCartStore()

// 响应式数据
const orderData = ref<any>({})
const selectedPaymentMethod = ref('wechat') // 默认微信支付
const orderInfo = ref<any>(null)
const paymentMessage = ref('')
const scannedPaymentCode = ref('')

// 状态管理
const creating = ref(false)
const paying = ref(false)
const showScanStatus = ref(false)
const showPaymentResult = ref(false)
const paymentSuccess = ref(false)

// 扫码监听相关
let scanListenerAdded = ref(false)
let paymentPollingTimer: any = null
let isProcessingPayment = ref(false) // 防止重复支付

// 测试模式相关
const testMode = ref(import.meta.env.DEV || import.meta.env.VITE_ENABLE_PAYMENT_TEST === 'true') // 开发环境或配置启用测试模式
const testPaymentCode = ref('')
const showTestPanel = ref(false)

// 支付方式配置
const paymentMethods = ref([
  {
    id: 'wechat',
    name: '微信支付',
    description: '推荐使用微信扫码支付',
    icon: 'wechat',
    color: '#07c160'
  },
  {
    id: 'alipay',
    name: '支付宝',
    description: '支持花呗分期付款',
    icon: 'alipay',
    color: '#1677ff'
  }
])

// 计算属性
const currentPaymentMethod = computed(() => {
  return paymentMethods.value.find(method => method.id === selectedPaymentMethod.value)
})

// 初始化订单数据
const initOrderData = () => {
  try {
    const orderDataStr = route.query.orderData as string
    if (!orderDataStr) {
      throw new Error('订单数据缺失')
    }
    
    const parsedData = JSON.parse(orderDataStr)
    
    // 确保所有必要字段都存在并有默认值
    orderData.value = {
      items: parsedData.items || [],
      totalAmount: parsedData.totalAmount || 0,
      memberDiscount: parsedData.memberDiscount || 0,
      couponDiscount: parsedData.couponDiscount || 0,
      deliveryFee: parsedData.deliveryFee || 0,
      finalAmount: parsedData.finalAmount || 0,
      deliveryType: parsedData.deliveryType || 0,
      ...parsedData
    }
    
    // 验证订单数据
    if (!orderData.value.items || orderData.value.items.length === 0) {
      throw new Error('订单商品数据缺失')
    }
    
    if (!orderData.value.finalAmount || orderData.value.finalAmount <= 0) {
      throw new Error('订单金额异常')
    }
    
    console.log('订单数据初始化完成:', orderData.value)
    
  } catch (error: any) {
    console.error('订单数据初始化失败:', error)
    showToast(error.message || '订单数据异常')
    
    // 返回扫描页面
    setTimeout(() => {
      router.replace('/scan')
    }, 1500)
  }
}

// 开始扫码支付
const startScanPayment = async () => {
  if (!orderData.value.items || orderData.value.items.length === 0) {
    showToast('订单数据异常')
    return
  }
  
  creating.value = true
  
  try {
    // 1. 创建订单
    const orderResult = await createOrder()
    
    if (orderResult.code !== 200) {
      throw new Error(orderResult.message || '创建订单失败')
    }
    
    orderInfo.value = orderResult.data
    creating.value = false
    
    // 2. 显示扫码等待界面
    showScanStatus.value = true
    paying.value = true

    // 3. 启动扫码监听
    startScanCodeListener()

    // 4. 语音提醒：请扫码支付
    try {
      await speakScanToPay()
    } catch (error) {
      console.warn('请扫码支付语音播报失败:', error)
    }
    
  } catch (error: any) {
    console.error('创建订单失败:', error)
    showToast(error.message || '创建订单失败，请重试')
    creating.value = false
  }
}

// 创建订单
const createOrder = async () => {
  try {
    const orderParam: any = {
      orderType: 'CART',
      orderItems: orderData.value.items.map((item: any) => ({
        productId: item.productId,
        skuId: item.skuId,
        quantity: item.quantity,
        unitPrice: item.price,
        totalPrice: item.price * item.quantity,
        promotionType: 0,
        promotionAmount: 0,
        remark: '扫码添加'
      })),
      payType: selectedPaymentMethod.value.toUpperCase(),
      deliveryType: orderData.value.deliveryType || 0,
      note: '自助收银下单',
      expectedAmount: orderData.value.finalAmount || 0,
      usePoints: 0,
      forceOrder: false
    }
    
    // 如果未登录，需要提供游客ID
    if (!memberStore.isLoggedIn) {
      orderParam.guestId = 'guest_' + Date.now()
    }
    
    console.log('创建订单参数:', orderParam)
    
    return await OrderAPI.createOrder(orderParam)
    
  } catch (error: any) {
    console.error('创建订单失败:', error)
    throw error
  }
}

// 启动扫码监听
const startScanCodeListener = () => {
  if (scanListenerAdded.value) return
  
  // 监听键盘输入（扫码设备通常模拟键盘输入）
  document.addEventListener('keydown', handleScanInput)
  scanListenerAdded.value = true
  
  console.log('扫码监听已启动，等待用户扫码...')
}

// 处理扫码输入
const handleScanInput = (event: KeyboardEvent) => {
  // 如果不在支付状态，忽略输入
  if (!paying.value || !showScanStatus.value) return
  
  // Enter键表示扫码完成
  if (event.key === 'Enter') {
    if (scannedPaymentCode.value.length > 0) {
      // 处理扫到的付款码
      processScanPayment(scannedPaymentCode.value)
      scannedPaymentCode.value = '' // 清空缓存
    }
    return
  }
  
  // 忽略特殊键
  if (event.key.length > 1) return
  
  // 累积扫码数据
  scannedPaymentCode.value += event.key
  
  // 防止输入过长
  if (scannedPaymentCode.value.length > 20) {
    scannedPaymentCode.value = ''
  }
}

// 处理扫码支付
const processScanPayment = async (paymentCode: string) => {
  if (!orderInfo.value?.orderId) {
    showToast('订单信息异常')
    return
  }

  // 防止重复支付
  if (isProcessingPayment.value) {
    console.warn('支付正在处理中，忽略重复请求')
    return
  }

  console.log('扫码完成，付款码:', paymentCode)

  try {
    isProcessingPayment.value = true

    // 调用扫码支付接口
    const paymentResult = await PaymentAPI.scanPayment({
      orderId: orderInfo.value.orderId.toString(),
      paymentCode: paymentCode,
      paymentMethod: selectedPaymentMethod.value.toUpperCase() as PaymentMethod,
      amount: Number((orderData.value.finalAmount || 0).toFixed(2)) // 确保金额精度
    })

    if (paymentResult.code === 200) {
      const paymentData = paymentResult.data

      // 根据支付状态处理
      if (paymentData.payStatus === 'SUCCESS') {
        // 支付成功
        handlePaymentSuccess(paymentData)
      } else if (paymentData.payStatus === 'PENDING') {
        // 支付处理中，开始轮询
        startPaymentStatusPolling(paymentData.paymentId)
      } else if (paymentData.payStatus === 'FAILED') {
        // 支付失败
        handlePaymentFailure(paymentData.failureReason || '支付失败')
      } else {
        // 未知状态
        handlePaymentFailure('支付状态异常，请联系工作人员')
      }
    } else {
      // 接口调用失败
      handlePaymentFailure(paymentResult.message || '支付接口调用失败')
    }

  } catch (error: any) {
    console.error('扫码支付失败:', error)
    handlePaymentFailure(error.message || '支付处理失败')
  } finally {
    // 延迟重置标志，防止扫码枪重复发送
    setTimeout(() => {
      isProcessingPayment.value = false
    }, 1000)
  }
}

// 支付超时处理
const handlePaymentTimeout = async () => {
  stopScanCodeListener()
  stopPaymentStatusPolling()
  showScanStatus.value = false
  paying.value = false

  // 支付超时时取消订单
  if (orderInfo.value?.orderId) {
    try {
      const guestId = memberStore.isLoggedIn ? undefined : 'guest'
      const reason = '支付超时自动取消'

      const result = await OrderAPI.cancelOrder(orderInfo.value.orderId, guestId, reason)

      if (result.code === 200) {
        console.log('超时订单已自动取消')
      } else {
        console.warn('取消超时订单失败:', result.message)
      }
    } catch (error) {
      console.error('取消超时订单失败:', error)
    }
  }

  paymentSuccess.value = false
  paymentMessage.value = '支付超时，订单已自动取消'
  showPaymentResult.value = true
}

// 支付成功处理
const handlePaymentSuccess = async (paymentData: any) => {
  stopScanCodeListener()
  stopPaymentStatusPolling()
  showScanStatus.value = false
  paying.value = false

  paymentSuccess.value = true
  paymentMessage.value = '支付成功，订单已完成'

  // 更新订单信息
  orderInfo.value = {
    ...orderInfo.value,
    ...paymentData,
    payTime: new Date().toISOString()
  }

  showPaymentResult.value = true

  // 清空购物车中已支付的商品
  clearPaidItems()

  // 语音播报支付成功
  try {
    const amount = orderData.value.finalAmount
    await speakPaymentSuccess(amount)
  } catch (error) {
    console.warn('语音播报失败:', error)
  }

  showNotify({
    type: 'success',
    message: '支付成功',
    duration: 2000
  })
}

// 开始支付状态轮询
const startPaymentStatusPolling = (paymentId: string) => {
  console.log('开始轮询支付状态:', paymentId)
  
  let pollCount = 0
  const maxPollCount = 30 // 最多轮询30次（30秒）
  
  paymentPollingTimer = setInterval(async () => {
    pollCount++
    
    try {
      const statusResult = await PaymentAPI.getPaymentStatus(paymentId)
      
      if (statusResult.code === 200) {
        const paymentData = statusResult.data
        
        if (paymentData.payStatus === 'SUCCESS') {
          // 支付成功
          clearInterval(paymentPollingTimer)
          handlePaymentSuccess(paymentData)
          return
        } else if (paymentData.payStatus === 'FAILED') {
          // 支付失败
          clearInterval(paymentPollingTimer)
          await handlePaymentFailure(paymentData.failureReason || '支付失败')
          return
        }
      }
      
      // 达到最大轮询次数
      if (pollCount >= maxPollCount) {
        clearInterval(paymentPollingTimer)
        await handlePaymentTimeout()
      }
      
    } catch (error) {
      console.error('轮询支付状态失败:', error)
      pollCount++ // 错误也计入轮询次数
      
      // 如果是网络错误等致命错误，也应该取消订单
      if (pollCount >= maxPollCount) {
        clearInterval(paymentPollingTimer)
        await handlePaymentTimeout()
      }
    }
  }, 1000) // 每秒轮询一次
}

// 停止支付状态轮询
const stopPaymentStatusPolling = () => {
  if (paymentPollingTimer) {
    clearInterval(paymentPollingTimer)
    paymentPollingTimer = null
  }
}

// 测试模式 - 模拟扫码
const simulateScanPayment = () => {
  if (!testPaymentCode.value) {
    showToast('请输入测试付款码')
    return
  }
  
  console.log('模拟扫码支付，付款码:', testPaymentCode.value)
  processScanPayment(testPaymentCode.value)
  showTestPanel.value = false
}

// 预设测试付款码
const useTestCode = (type: 'wechat' | 'alipay') => {
  if (type === 'wechat') {
    testPaymentCode.value = '134567890123456789' // 微信测试付款码
  } else {
    testPaymentCode.value = '280000000000000000' // 支付宝测试付款码
  }
}

// 支付失败处理
const handlePaymentFailure = async (reason: string) => {
  stopScanCodeListener()
  stopPaymentStatusPolling()
  showScanStatus.value = false
  paying.value = false

  // 支付失败时取消订单
  if (orderInfo.value?.orderId) {
    try {
      const guestId = memberStore.isLoggedIn ? undefined : 'guest'
      const cancelReason = `支付失败自动取消: ${reason}`

      const result = await OrderAPI.cancelOrder(orderInfo.value.orderId, guestId, cancelReason)

      if (result.code === 200) {
        console.log('失败订单已自动取消')
      } else {
        console.warn('取消失败订单失败:', result.message)
      }
    } catch (error) {
      console.error('取消失败订单失败:', error)
    }
  }

  // 语音播报支付失败
  try {
    await speakPaymentFailure()
  } catch (error) {
    console.warn('语音播报失败:', error)
  }

  paymentSuccess.value = false
  paymentMessage.value = reason || '支付失败，订单已取消'
  showPaymentResult.value = true
}

// 取消扫码支付
const cancelScanPayment = async () => {
  try {
    await showConfirmDialog({
      title: '确认取消',
      message: '确定要取消支付吗？订单将会被取消。'
    })
    
    stopScanCodeListener()
    stopPaymentStatusPolling()
    showScanStatus.value = false
    paying.value = false
    
    // 取消订单
    if (orderInfo.value?.orderId) {
      try {
        // 获取当前用户状态
        const guestId = memberStore.isLoggedIn ? undefined : 'guest'
        const reason = '用户主动取消支付'
        
        const result = await OrderAPI.cancelOrder(orderInfo.value.orderId, guestId, reason)
        
        if (result.code === 200) {
          showNotify({
            type: 'success',
            message: '订单已取消',
            duration: 2000
          })
          
          console.log('订单取消成功')
        } else {
          showNotify({
            type: 'warning',
            message: result.message || '取消订单失败',
            duration: 3000
          })
          
          console.warn('取消订单失败:', result.message)
        }
      } catch (error) {
        console.error('取消订单失败:', error)
        showNotify({
          type: 'danger',
          message: '取消订单失败，请稍后再试',
          duration: 3000
        })
      }
    }
    
    // 返回扫码页面
    router.replace('/scan')
    
  } catch (error) {
    // 用户取消操作，不做处理
    console.log('用户取消了取消支付操作')
  }
}

// 停止扫码监听
const stopScanCodeListener = () => {
  if (scanListenerAdded.value) {
    document.removeEventListener('keydown', handleScanInput)
    scanListenerAdded.value = false
    scannedPaymentCode.value = ''
    console.log('扫码监听已停止')
  }
}

// 重试支付
const retryPayment = () => {
  showPaymentResult.value = false
  scannedPaymentCode.value = ''
  
  // 重新开始扫码支付
  if (orderInfo.value?.orderId) {
    showScanStatus.value = true
    paying.value = true
    startScanCodeListener()

    // 语音提醒：请扫码支付
    try {
      speakScanToPay()
    } catch (error) {
      console.warn('请扫码支付语音播报失败:', error)
    }
  } else {
    // 重新创建订单
    startScanPayment()
  }
}

// 返回处理
const handleBack = async () => {
  if (paying.value || creating.value) {
    await showConfirmDialog({
      title: '确认离开',
      message: '支付正在进行中，确定要离开吗？'
    })
    
    stopScanCodeListener()
  }
  
  router.go(-1)
}

// 查看小票
const goToReceipt = () => {
  if (orderInfo.value?.orderId) {
    router.replace({
      path: '/receipt',
      query: {
        orderId: orderInfo.value.orderId
      }
    })
  }
}

// 返回首页
const goToHome = () => {
  // 如果用户手动返回首页，也应该退出登录
  if (memberStore.isLoggedIn) {
    memberStore.performLogout()
  }
  router.replace('/')
}

// 清理已支付商品
const clearPaidItems = async () => {
  try {
    // 使用store的本地清理方法
    cartStore.clearLocalCart()
    
    console.log('本地购物车数据已清理')
  } catch (error) {
    console.warn('清理本地购物车数据失败:', error)
  }
}

// 格式化支付时间
const formatPaymentTime = (timeStr: string) => {
  if (!timeStr) return ''
  
  const date = new Date(timeStr)
  const now = new Date()
  
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  
  // 如果是今天，只显示时间
  if (date.toDateString() === now.toDateString()) {
    return `今天 ${hours}:${minutes}`
  }
  
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

// 生命周期
onMounted(async () => {
  // 初始化订单数据
  initOrderData()

  // 设置默认支付方式
  if (paymentMethods.value.length > 0) {
    selectedPaymentMethod.value = paymentMethods.value[0].id
  }

  // 初始化语音播报
  try {
    const tts = TTSManager.getInstance()
    await tts.initialize()
    console.log('TTS 初始化成功')
  } catch (error) {
    console.warn('TTS 初始化失败:', error)
  }
})

onUnmounted(() => {
  stopScanCodeListener()
  stopPaymentStatusPolling()
})
</script>

<style scoped lang="scss">
.payment-page {
  width: 100%;
  height: 100%;
  max-width: 100vw;
  max-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  background: #fff;
  position: relative;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 50vh;
}

.payment-content {
  flex: 1;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  padding-bottom: 100px;
  background: #fff;
}

.amount-section,
.payment-icon-section,
.payment-methods {
  width: 100%;
  margin: 0 0 32px 0;
  background: #fff;
  border-radius: 0;
  box-shadow: none;
}

.amount-section {
  text-align: center;
  padding: 40px 0;
  background: #fff;
  border-radius: 0;
  margin-bottom: 32px;
  box-shadow: none;
  
  .amount-label {
    font-size: 16px;
    color: #666;
    margin-bottom: 8px;
  }
  
  .amount-value {
    font-size: 48px;
    font-weight: 700;
    color: #ff6b35;
    margin-bottom: 8px;
  }
  
  .amount-desc {
    font-size: 14px;
    color: #999;
  }
}

.payment-icon-section {
  text-align: center;
  padding: 40px 0;
  background: #fff;
  border-radius: 0;
  box-shadow: none;
  
  .payment-icon {
    margin-bottom: 16px;
  }
  
  .payment-tip {
    font-size: 16px;
    color: #666;
    margin-bottom: 8px;
  }
}

.payment-methods {
  background: #fff;
  border-radius: 0;
  padding: 24px 0;
  margin-bottom: 32px;
  box-shadow: none;
  
  .method-title {
    font-size: 18px;
    font-weight: 600;
    color: #333;
    margin-bottom: 20px;
    text-align: center;
  }
  
  .method-icons {
    display: flex;
    justify-content: center;
    gap: 32px;
    
    .method-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 8px;
      padding: 16px;
      border-radius: 12px;
      cursor: pointer;
      transition: all 0.3s ease;
      min-width: 80px;
      
      &:hover {
        background: #f5f7fa;
      }
      
      &.active {
        background: #e8f4fd;
        border: 2px solid #1989fa;
      }
      
      .method-name {
        font-size: 14px;
        color: #666;
        font-weight: 500;
      }
    }
  }
}

.payment-footer {
  position: absolute;
  left: 0;
  bottom: 32px;
  width: 100%;
  display: flex;
  justify-content: center;
  z-index: 10;
}

.payment-footer .van-button {
  width: 90%;
  max-width: 520px;
  height: 64px;
  font-size: 22px;
  font-weight: bold;
  border-radius: 32px;
  margin: 0 auto;
  display: block;
}

.scan-status-popup {
  padding: 32px 24px;
  text-align: center;
  
  .status-header {
    margin-bottom: 32px;
    
    .status-title {
      font-size: 20px;
      font-weight: 600;
      color: #333;
      margin: 0 0 12px 0;
    }
    
    .status-amount {
      font-size: 28px;
      font-weight: 700;
      color: #ff6b35;
      margin: 0;
    }
  }
  
  .status-content {
    margin-bottom: 32px;
    
    .scan-animation {
      margin-bottom: 24px;
    }
    
    .scan-tip {
      font-size: 16px;
      color: #666;
      margin: 0 0 8px 0;
      line-height: 1.5;
    }
    
    .scan-desc {
      font-size: 14px;
      color: #999;
      margin: 0;
      line-height: 1.4;
    }
  }
  
  .status-actions {
    .van-button {
      height: 48px;
      font-size: 16px;
      border-radius: 24px;
    }
  }
}

.payment-result-popup {
  padding: 32px 24px 24px;
  text-align: center;
  
  .result-content {
    margin-bottom: 32px;
    
    .result-icon {
      margin-bottom: 16px;
    }
    
    .result-title {
      font-size: 20px;
      font-weight: 600;
      color: var(--van-text-color);
      margin: 0 0 8px 0;
    }
    
    .result-message {
      font-size: 14px;
      color: var(--van-text-color-2);
      margin: 0 0 24px 0;
      line-height: 1.5;
    }
    
    .order-summary {
      text-align: left;
      background: #f8f9fa;
      border-radius: 8px;
      padding: 16px;
      
      .summary-item {
        display: flex;
        justify-content: space-between;
        margin-bottom: 8px;
        
        &:last-child {
          margin-bottom: 0;
        }
        
        .label {
          color: #666;
          font-size: 14px;
        }
        
        .value {
          color: #333;
          font-size: 14px;
          font-weight: 600;
        }
      }
    }
  }
  
  .result-actions {
    display: flex;
    flex-direction: column;
    gap: 12px;
    
    .back-btn {
      margin-top: 8px;
    }
  }
}

/* 深色模式支持 */
@media (prefers-color-scheme: dark) {
  .payment-footer {
    background: var(--van-background-color-light);
    border-top-color: var(--van-border-color);
  }
  
  .qr-code {
    background: white !important;
  }
}

/* 测试扫码弹窗样式 */
.test-scan-popup {
  padding: 24px;
  
  .test-header {
    text-align: center;
    margin-bottom: 24px;
    
    .test-title {
      font-size: 18px;
      font-weight: 600;
      color: #333;
      margin: 0 0 8px 0;
    }
    
    .test-desc {
      font-size: 14px;
      color: #666;
      margin: 0;
    }
  }
  
  .test-content {
    margin-bottom: 24px;
    
    .test-input-section {
      margin-bottom: 20px;
    }
    
    .test-preset-codes {
      .preset-title {
        font-size: 14px;
        color: #666;
        margin: 0 0 12px 0;
      }
      
      .preset-buttons {
        display: flex;
        gap: 12px;
        
        .van-button {
          flex: 1;
        }
      }
    }
  }
  
  .test-actions {
    .van-button {
      height: 44px;
      font-size: 16px;
    }
  }
}

/* 响应式设计 */
@media (min-width: 768px) {
  .payment-page {
    max-width: 600px;
    margin: 0 auto;
  }
  
  .payment-footer {
    max-width: 600px;
    left: 50%;
    transform: translateX(-50%);
  }
}

:deep(.van-popup) {
  width: 80vw !important;
  max-width: 480px !important;
  left: 50% !important;
  transform: translateX(-50%) !important;
}
</style> 