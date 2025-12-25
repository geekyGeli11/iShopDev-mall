<template>
  <div v-if="visible" class="payment-dialog-mask" @click.self="handleClose">
    <div class="payment-dialog-container">
      <div class="dialog-title">请选择支付方式</div>
      <!-- 金额、件数、支付方式、按钮等区域 -->
      <div class="dialog-header">
        <div class="amount-label">应付金额</div>
        <div class="amount-value">¥{{ displayPayAmount.toFixed(2) }}</div>
        <!-- 组合优惠信息 -->
        <div v-if="bundleDiscountAmount > 0" class="bundle-discount-info">
          <span class="original-price">原价 ¥{{ originalTotalAmount.toFixed(2) }}</span>
          <span class="discount-tag">组合优惠 -¥{{ bundleDiscountAmount.toFixed(2) }}</span>
        </div>
        <div class="amount-desc">共{{ totalItemCount }}件商品</div>
      </div>
      <div class="dialog-methods">
        <!-- 第一行：微信和支付宝 -->
        <div class="method-list method-row">
          <div
            v-for="method in availablePaymentMethods.filter(m => m.id !== 'balance')"
            :key="method.id"
            class="method-item active"
            :class="{ 'method-half': true }"
            :aria-wechat="method.id === 'wechat' ? true : null"
            :aria-alipay="method.id === 'alipay' ? true : null"
            @click="selectMethod(method.id)"
          >
            <img :src="paymentMethodIcons[method.id] || iconUrl" class="method-icon" />
            <div class="method-content">
              <span class="method-name">{{ method.name }}</span>
            </div>
          </div>
        </div>

        <!-- 第二行：余额支付（如果有） -->
        <div v-if="availablePaymentMethods.some(m => m.id === 'balance')" class="method-list method-row">
          <div
            class="method-item active method-full method-balance"
            :aria-balance="true"
            @click="selectMethod('balance')"
          >
            <img :src="paymentMethodIcons['balance']" class="method-icon" />
            <div class="method-content">
              <span class="method-name">会员储值支付</span>
              <span class="method-desc">使用储值支付享受更多优惠</span>
            </div>
          </div>
        </div>
      </div>
      <!-- <div v-if="testMode && !showScanStatus && !showPaymentResult" class="test-pay-btn-box">
        <button class="test-pay-btn" @click="openTestPanel">测试扫码</button>
      </div> -->
      <!-- 测试扫码弹窗 -->
      <div v-if="showTestPanel" class="test-scan-popup-mask">
        <div class="test-scan-popup">
          <div class="test-header">
            <h3 class="test-title">测试扫码支付</h3>
            <p class="test-desc">输入付款码测试支付流程</p>
          </div>
          <div class="test-content">
            <div class="test-input-section">
              <input v-model="testPaymentCode" class="test-input" placeholder="请输入18位付款码" maxlength="20" />
            </div>
            <div class="test-preset-codes">
              <p class="preset-title">快捷选择:</p>
              <div class="preset-buttons">
                <button class="preset-btn" @click="useTestCode('wechat')">微信测试码</button>
                <button class="preset-btn" @click="useTestCode('alipay')">支付宝测试码</button>
              </div>
            </div>
          </div>
          <div class="test-actions">
            <button class="test-confirm-btn" :disabled="!testPaymentCode" @click="simulateScanPayment">确认支付</button>
            <button class="test-cancel-btn" @click="showTestPanel = false">取消</button>
          </div>
        </div>
      </div>
      <!-- 覆盖式扫码支付状态弹窗 -->
      <div v-if="showScanStatus" class="dialog-overlay">
        <div class="scan-status-popup dialog-popup-center">
          <div class="status-header">
            <h3 class="status-title">等待扫码支付</h3>
            <p class="status-amount">¥{{ displayPayAmount.toFixed(2) }}</p>
            <!-- 组合优惠信息 -->
            <div v-if="bundleDiscountAmount > 0" class="status-discount-info">
              <span class="original-price">原价 ¥{{ originalTotalAmount.toFixed(2) }}</span>
              <span class="discount-tag">组合优惠 -¥{{ bundleDiscountAmount.toFixed(2) }}</span>
            </div>
          </div>
          <div class="status-content">
            <div class="scan-steps">
              <div class="scan-step">
                <img class="scan-step-img" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/mall-selfcheck/assets/images/payment/show_qrcode.png" alt="出示付款码" />
                <div class="scan-step-label">1 出示付款码</div>
              </div>
              <div class="scan-arrow">
                <img class="scan-arrow-img" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/mall-selfcheck/assets/images/payment/arrow_right.png" alt=">" />
              </div>
              <div class="scan-step">
                <img class="scan-step-img" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/mall-selfcheck/assets/images/payment/scan_example.png" alt="扫码口支付" />
                <div class="scan-step-label">2 对准下方扫码口支付</div>
              </div>
            </div>
          </div>
          <div class="status-actions">
            <button class="cancel-btn" @click="handleClose">取消支付</button>
          </div>
        </div>
      </div>
      <!-- 余额支付确认弹窗 -->
      <div v-if="showBalanceConfirm" class="dialog-overlay">
        <div class="balance-confirm-popup dialog-popup-center">
          <div class="confirm-content">
            <h3 class="confirm-title">确认使用余额支付</h3>
            <div class="balance-info">
              <div class="balance-item">
                <span class="label">当前余额</span>
                <span class="value balance-amount">¥{{ (memberBalance || 0).toFixed(2) }}</span>
              </div>
              <div class="balance-item highlight">
                <span class="label">支付金额</span>
                <span class="value pay-amount">¥{{ displayPayAmount.toFixed(2) }}</span>
              </div>
              <div class="balance-item">
                <span class="label">剩余余额</span>
                <span class="value remaining-amount">¥{{ ((memberBalance || 0) - displayPayAmount).toFixed(2) }}</span>
              </div>
            </div>
            <p v-if="(memberBalance || 0) < displayPayAmount" class="insufficient-warning">
              余额不足，请充值后再试
            </p>
          </div>
          <div class="confirm-actions">
            <button class="cancel-btn" @click="cancelBalancePayment">取消</button>
            <button
              class="confirm-btn"
              :disabled="(memberBalance || 0) < displayPayAmount"
              @click="confirmBalancePayment"
            >
              确认支付
            </button>
          </div>
        </div>
      </div>

      <!-- 覆盖式支付结果弹窗 -->
      <div v-if="showPaymentResult" class="dialog-overlay">
        <div class="payment-result-popup dialog-popup-center">
          <div class="result-content">
            <div class="result-icon" :class="{ success: paymentSuccess, fail: !paymentSuccess }"></div>
            <h3 class="result-title">{{ paymentSuccess ? '支付成功' : '支付失败' }}</h3>
            <p class="result-message">{{ paymentMessage }}</p>
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
            <button v-if="!paymentSuccess" class="pay-btn" @click="retryPayment">重新支付</button>
            <button class="cancel-btn" @click="handleClose">返回 ({{ autoReturnCountdown }}s)</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { showToast, closeToast, showNotify, showDialog } from 'vant'
import { useMemberStore } from '@/store/modules/member'
import { useCartStore } from '@/store/modules/cart'
import { OrderAPI } from '@/api/modules/order'
import { PaymentAPI } from '@/api/modules/payment'
import { MemberAPI } from '@/api/modules/member'
import { useRouter } from 'vue-router'
import { scanManager } from '../../utils/scanManager'
import { debugLogger, type ScanLogData } from '../../utils/debugLogger'
import StoreManager from '@/utils/storeManager'
import { speakPaymentSuccess, speakPaymentFailure, speakScanToPay, TTSManager } from '@/utils/tts'

interface PaymentMethod {
  id: string
  name: string
}

const props = defineProps({
  visible: Boolean,
  orderData: { type: Object, required: true },
  paymentMethods: { type: Array as () => PaymentMethod[], required: true },
  defaultMethod: { type: String, default: '' },
  iconUrl: { type: String, default: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/mall-selfcheck/assets/images/payment/arrow_right.png' }
})
const emit = defineEmits(['close', 'pay', 'success', 'fail'])

const memberStore = useMemberStore()
const cartStore = useCartStore()
const router = useRouter()

const selectedPaymentMethod = ref(props.defaultMethod || (props.paymentMethods[0]?.id || 'wechat'))
const orderInfo = ref<any>(null)
const paymentMessage = ref('')
const scannedPaymentCode = ref('')
const creating = ref(false)
const paying = ref(false)
const showScanStatus = ref(false)
const showPaymentResult = ref(false)
const paymentSuccess = ref(false)
const memberBalance = ref<number | null>(null)
const showBalanceConfirm = ref(false) // 余额支付确认弹窗
const actualPayAmount = ref<number | null>(null) // 后端返回的实际支付金额（包含组合优惠）
let scanListenerAdded = false
let paymentPollingTimer: any = null
let isProcessingPayment = ref(false) // 防止重复支付
let lastProcessedPaymentCode = '' // 记录最后处理的付款码，防止重复扫描同一付款码
let lastPaymentCodeTime = 0 // 记录最后处理付款码的时间
let autoReturnTimer: any = null // 支付结果自动返回首页计时器
const autoReturnCountdown = ref(10) // 自动返回倒计时秒数

// 计算实际显示的支付金额（优先使用后端返回的金额）
const displayPayAmount = computed(() => {
  if (actualPayAmount.value !== null) {
    return actualPayAmount.value
  }
  return props.orderData.finalAmount || 0
})

// 组合优惠金额
const bundleDiscountAmount = ref<number>(0)

// 原价总金额
const originalTotalAmount = computed(() => {
  return props.orderData.totalAmount || props.orderData.finalAmount || 0
})

// 商品总件数
const totalItemCount = computed(() => {
  if (!props.orderData.items) return 0
  return props.orderData.items.reduce((sum: number, item: any) => sum + (item.quantity || 1), 0)
})

// 测试模式
const testPaymentCode = ref('')
const showTestPanel = ref(false)

const paymentMethodIcons: Record<string, string> = {
  wechat: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/mall-selfcheck/assets/images/payment/wechat_pay.png',
  alipay: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/mall-selfcheck/assets/images/payment/alipay.png',
  balance: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/mall-selfcheck/assets/images/payment/balance_pay.png'
}

// 过滤支付方式：游客不能使用余额支付
const availablePaymentMethods = computed(() => {
  return props.paymentMethods.filter(method => {
    // 如果是余额支付，只有会员登录时才显示（不包括游客）
    if (method.id === 'balance') {
      return memberStore.isMemberLogin
    }
    return true
  })
})

const currentPaymentMethod = computed(() => {
  return availablePaymentMethods.value.find(method => method.id === selectedPaymentMethod.value)
})

// 加载会员余额
const loadMemberBalance = async () => {
  if (!memberStore.isMemberLogin) {
    memberBalance.value = null
    return
  }

  try {
    const result = await MemberAPI.getBalanceInfo()
    if (result.code === 200 && result.data) {
      memberBalance.value = result.data.balance || 0
    }
  } catch (error) {
    console.error('获取会员余额失败:', error)
    memberBalance.value = null
  }
}

watch(() => props.visible, async (val) => {
  if (!val) {
    stopScanCodeListener()
    stopPaymentStatusPolling()
    stopAutoReturnTimer() // 停止自动返回计时器
    showScanStatus.value = false
    showPaymentResult.value = false
    creating.value = false
    paying.value = false
    paymentSuccess.value = false
    paymentMessage.value = ''
    orderInfo.value = null
    scannedPaymentCode.value = ''
    actualPayAmount.value = null // 重置实际支付金额
    bundleDiscountAmount.value = 0 // 重置组合优惠金额
    isProcessingPayment.value = false // 重置支付处理标志
    lastProcessedPaymentCode = '' // 重置最后处理的付款码
    lastPaymentCodeTime = 0 // 重置最后处理时间
  } else {
    // 弹窗打开时加载余额
    await loadMemberBalance()
  }
})

const selectMethod = async (id: string) => {
  // 如果选择余额支付，显示确认弹窗
  if (id === 'balance') {
    // 检查是否为会员登录
    if (!memberStore.isMemberLogin) {
      showToast('请先登录会员账号')
      return
    }

    // 加载余额信息
    if (memberBalance.value === null) {
      await loadMemberBalance()
    }

    // 显示余额确认弹窗
    selectedPaymentMethod.value = id
    showBalanceConfirm.value = true
    return
  }

  // 其他支付方式直接触发支付
  selectedPaymentMethod.value = id
  await handlePay()
}

// 取消余额支付
const cancelBalancePayment = () => {
  showBalanceConfirm.value = false
  selectedPaymentMethod.value = 'wechat' // 重置为默认支付方式
}

// 确认余额支付
const confirmBalancePayment = async () => {
  showBalanceConfirm.value = false
  // 触发支付流程
  await handlePay()
}

const handlePay = async () => {
  if (!props.orderData.items || props.orderData.items.length === 0) {
    showToast('订单数据异常')
    return
  }

  // 获取门店信息
  const storeParams = StoreManager.getStoreParams()
  console.log('🏪 PaymentDialog - handlePay获取门店信息:', storeParams)
  console.log('🏪 PaymentDialog - 缓存检查:', {
    current_store_id: localStorage.getItem('current_store_id'),
    current_school_id: localStorage.getItem('current_school_id'),
    hasStoreInfo: StoreManager.hasStoreInfo(),
    isStoreInfoValid: StoreManager.isStoreInfoValid()
  })

  // 检查门店信息
  if (!storeParams.storeId || !storeParams.schoolId) {
    console.error('❌ PaymentDialog - handlePay门店信息缺失')
    showToast('请先选择门店后再下单')
    creating.value = false
    return
  }

  creating.value = true
  try {
    // 支付方式映射：wechat->WECHAT, alipay->ALIPAY, balance->BALANCE
    const payTypeMap: Record<string, string> = {
      'wechat': 'WECHAT',
      'alipay': 'ALIPAY',
      'balance': 'BALANCE'
    }

    // 1. 创建订单，补全 orderType/orderItems 字段和门店信息
    const orderCreateParam = {
      orderType: 'CART',
      orderItems: props.orderData.items.map((item: any) => ({
        productId: item.productId,
        skuId: item.skuId,
        quantity: item.quantity,
        unitPrice: item.price,
        totalPrice: item.price * item.quantity,
        promotionType: 0,
        promotionAmount: 0,
        remark: '扫码添加'
      })),
      payType: payTypeMap[selectedPaymentMethod.value] || 'WECHAT',
      deliveryType: props.orderData.deliveryType || 0,
      note: '自助收银下单',
      expectedAmount: Number((props.orderData.finalAmount || 0).toFixed(2)),
      usePoints: 0,
      forceOrder: false,
      // 添加门店信息
      storeId: storeParams.storeId,
      schoolId: storeParams.schoolId,
      // 添加优惠券信息
      couponHistoryIds: props.orderData.couponId ? [props.orderData.couponId] : undefined
    }

    console.log('📦 PaymentDialog - handlePay订单创建参数:', orderCreateParam)

    const orderResult = await OrderAPI.createOrder(orderCreateParam)
    if (orderResult.code !== 200) {
      throw new Error(orderResult.message || '创建订单失败')
    }
    orderInfo.value = orderResult.data
    creating.value = false
    
    console.log('📦 订单创建成功，订单信息:', {
      orderId: orderResult.data?.orderId,
      orderSn: orderResult.data?.orderSn,
      payAmount: orderResult.data?.payAmount
    })

    // 更新实际支付金额（后端已计算组合优惠）
    if (orderResult.data.payAmount !== undefined) {
      actualPayAmount.value = orderResult.data.payAmount
      // 保存组合优惠金额
      bundleDiscountAmount.value = (orderResult.data as any).bundleDiscount || 0
      console.log('💰 后端返回实际支付金额:', actualPayAmount.value, '组合优惠:', bundleDiscountAmount.value)
    }

    // 如果是余额支付，直接调用余额支付接口
    if (selectedPaymentMethod.value === 'balance') {
      await processBalancePayment()
    } else {
      // 其他支付方式，显示扫码界面
      showScanStatus.value = true
      paying.value = true
      startScanCodeListener()

      // 语音提醒：请扫码支付
      try {
        await speakScanToPay()
      } catch (error) {
        console.warn('请扫码支付语音播报失败:', error)
      }
    }
  } catch (error: any) {
    closeToast()
    creating.value = false

    // 显示详细的错误信息
    const errorMessage = error.message || '创建订单失败，请重试'
    console.error('❌ PaymentDialog - 创建订单失败:', errorMessage)

    showToast({
      type: 'fail',
      message: errorMessage,
      duration: 3000
    })

    // 触发失败事件
    emit('fail', errorMessage)
  }
}

const startScanCodeListener = () => {
  if (scanListenerAdded) return

  console.log('🔍 启动支付扫码监听器')

  // 调试日志 - 支付扫码监听器启动
  debugLogger.logScan(
    '支付扫码监听器启动',
    '开始监听支付码扫描输入',
    {
      scanType: 'payment',
      status: 'start'
    } as ScanLogData
  )

  // 停用其他所有扫码监听器，避免冲突
  scanManager.deactivateAll()

  // 注册并激活支付扫码监听器
  scanManager.register('payment', handleScanInput)
  scanManager.activate('payment')
  scanListenerAdded = true

  console.log('✅ 支付扫码监听器已启动')
}

const stopScanCodeListener = () => {
  if (scanListenerAdded) {
    console.log('🛑 停止支付扫码监听器')

    // 调试日志 - 支付扫码监听器停止
    debugLogger.logScan(
      '支付扫码监听器停止',
      '停止支付码扫描监听',
      {
        scanType: 'payment',
        status: 'success'
      } as ScanLogData
    )

    // 停用并注销支付扫码监听器
    scanManager.deactivate('payment')
    scanManager.unregister('payment')
    scanListenerAdded = false
    scannedPaymentCode.value = ''

    console.log('✅ 支付扫码监听器已停止')
  }
}
const handleScanInput = (event: KeyboardEvent) => {
  if (!paying.value || !showScanStatus.value) return

  // 防止支付处理中重复扫码
  if (isProcessingPayment.value) {
    console.warn('⚠️ 支付正在处理中，忽略扫码输入')
    // 如果是 Enter 键，清空累积的付款码，防止下次误触发
    if (event.key === 'Enter') {
      scannedPaymentCode.value = ''
    }
    return
  }

  // 调试日志 - 支付扫码键盘事件
  debugLogger.logScan(
    '支付扫码键盘事件',
    `接收到按键: ${event.key}, 当前累积: ${scannedPaymentCode.value}`,
    {
      scanType: 'payment',
      status: 'progress',
      result: { key: event.key, accumulated: scannedPaymentCode.value }
    } as ScanLogData
  )

  console.log('🎯 支付扫码事件:', event.key, '当前累积:', scannedPaymentCode.value)

  if (event.key === 'Enter') {
    if (scannedPaymentCode.value.length > 0) {
      const paymentCode = scannedPaymentCode.value
      // 立即清空，防止重复触发
      scannedPaymentCode.value = ''

      // 检查是否是重复扫描同一付款码（5秒内）
      const now = Date.now()
      if (paymentCode === lastProcessedPaymentCode && (now - lastPaymentCodeTime) < 5000) {
        console.warn('⚠️ 检测到重复扫描同一付款码，已忽略:', paymentCode.substring(0, 4) + '****')
        debugLogger.logScan(
          '重复付款码已忽略',
          `5秒内重复扫描同一付款码，已忽略`,
          {
            scanType: 'payment',
            status: 'error',
            error: '重复扫描',
            result: { paymentCode: paymentCode.substring(0, 4) + '****', timeSinceLastScan: now - lastPaymentCodeTime }
          } as ScanLogData
        )
        showToast('请勿重复扫描付款码')
        return
      }

      console.log('✅ 支付扫码完成，付款码:', paymentCode.substring(0, 4) + '****')

      // 调试日志 - 支付扫码完成
      debugLogger.logScan(
        '支付扫码输入完成',
        `扫码器输入完成，付款码: ${paymentCode.substring(0, 4)}****`,
        {
          scanType: 'payment',
          status: 'success',
          result: { finalPaymentCode: paymentCode.substring(0, 4) + '****', inputLength: paymentCode.length }
        } as ScanLogData
      )

      // 记录本次处理的付款码
      lastProcessedPaymentCode = paymentCode
      lastPaymentCodeTime = now

      processScanPayment(paymentCode)
    }
    return
  }

  if (event.key.length > 1) {
    console.log('⏭️ 忽略特殊键:', event.key)

    // 调试日志 - 忽略特殊键
    debugLogger.logScan(
      '支付扫码忽略按键',
      `忽略特殊键: ${event.key}`,
      {
        scanType: 'payment',
        status: 'progress',
        result: { ignoredKey: event.key, reason: 'special_key' }
      } as ScanLogData
    )

    return
  }

  scannedPaymentCode.value += event.key
  console.log('📝 累积支付码数据:', scannedPaymentCode.value)

  if (scannedPaymentCode.value.length > 20) {
    console.log('⚠️ 支付码过长，重置')

    // 调试日志 - 支付码过长重置
    debugLogger.logScan(
      '支付码输入过长重置',
      `支付码输入超过20位，自动重置`,
      {
        scanType: 'payment',
        status: 'error',
        error: '输入过长',
        result: { resetLength: scannedPaymentCode.value.length }
      } as ScanLogData
    )

    scannedPaymentCode.value = ''
  }
}

// 处理余额支付
const processBalancePayment = async () => {
  if (!orderInfo.value?.orderId) {
    showToast('订单信息异常')
    return
  }

  try {
    // 调用订单支付成功接口，payType=3表示余额支付
    const result = await OrderAPI.paymentSuccess(
      orderInfo.value.orderId,
      3, // 余额支付
      'BALANCE_' + Date.now() // 生成余额支付流水号
    )

    if (result.code === 200) {
      // 支付成功
      handlePaymentSuccess({
        paymentId: 'BALANCE_' + orderInfo.value.orderId,
        payStatus: 'SUCCESS',
        transactionId: 'BALANCE_' + Date.now(),
        amount: orderInfo.value.payAmount || orderInfo.value.totalAmount
      })
    } else {
      handlePaymentFailure(result.message || '余额支付失败')
    }
  } catch (error: any) {
    handlePaymentFailure(error.message || '余额支付处理失败')
  }
}

const processScanPayment = async (paymentCode: string) => {
  if (!orderInfo.value?.orderId) {
    showToast('订单信息异常')
    return
  }

  // 防止重复支付
  if (isProcessingPayment.value) {
    console.warn('⚠️ 支付正在处理中，忽略重复请求')
    showToast('支付正在处理中，请稍候')
    return
  }

  console.log('💳 扫码完成，付款码:', paymentCode.substring(0, 4) + '****' + paymentCode.substring(paymentCode.length - 4))

  try {
    isProcessingPayment.value = true

    const paymentResult = await PaymentAPI.scanPayment({
      orderId: orderInfo.value.orderId.toString(),
      paymentCode: paymentCode,
      paymentMethod: selectedPaymentMethod.value.toUpperCase() as any,
      amount: Number(displayPayAmount.value.toFixed(2))
    })
    if (paymentResult.code === 200) {
      const paymentData = paymentResult.data
      if (paymentData.payStatus === 'SUCCESS') {
        handlePaymentSuccess(paymentData)
      } else if (paymentData.payStatus === 'PENDING') {
        startPaymentStatusPolling(paymentData.paymentId)
      } else if (paymentData.payStatus === 'FAILED') {
        handlePaymentFailure(paymentData.failureReason || '支付失败')
      } else {
        handlePaymentFailure('支付状态异常，请联系工作人员')
      }
    } else {
      handlePaymentFailure(paymentResult.message || '支付接口调用失败')
    }
  } catch (error: any) {
    handlePaymentFailure(error.message || '支付处理失败')
  } finally {
    // 延迟重置标志，防止扫码枪重复发送
    setTimeout(() => {
      isProcessingPayment.value = false
      console.log('🔓 支付处理标志已重置')
    }, 1000)
  }
}
const startPaymentStatusPolling = (paymentId: string) => {
  let pollCount = 0
  const maxPollCount = 30
  paymentPollingTimer = setInterval(async () => {
    pollCount++
    try {
      const statusResult = await PaymentAPI.getPaymentStatus(paymentId)
      if (statusResult.code === 200) {
        const paymentData = statusResult.data
        if (paymentData.payStatus === 'SUCCESS') {
          clearInterval(paymentPollingTimer)
          handlePaymentSuccess(paymentData)
          return
        } else if (paymentData.payStatus === 'FAILED') {
          clearInterval(paymentPollingTimer)
          await handlePaymentFailure(paymentData.failureReason || '支付失败')
          return
        }
      }
      if (pollCount >= maxPollCount) {
        clearInterval(paymentPollingTimer)
        await handlePaymentTimeout()
      }
    } catch (error) {
      pollCount++
      if (pollCount >= maxPollCount) {
        clearInterval(paymentPollingTimer)
        await handlePaymentTimeout()
      }
    }
  }, 1000)
}
const stopPaymentStatusPolling = () => {
  if (paymentPollingTimer) {
    clearInterval(paymentPollingTimer)
    paymentPollingTimer = null
  }
}

// 启动自动返回首页计时器
const startAutoReturnTimer = () => {
  stopAutoReturnTimer() // 先清除已有计时器
  autoReturnCountdown.value = 10
  autoReturnTimer = setInterval(() => {
    autoReturnCountdown.value--
    if (autoReturnCountdown.value <= 0) {
      stopAutoReturnTimer()
      console.log('⏰ 10秒无操作，自动返回首页')
      handleClose()
    }
  }, 1000)
}

// 停止自动返回首页计时器
const stopAutoReturnTimer = () => {
  if (autoReturnTimer) {
    clearInterval(autoReturnTimer)
    autoReturnTimer = null
  }
  autoReturnCountdown.value = 30 // 默认30秒倒计时
}

const handlePaymentSuccess = async (paymentData: any) => {
  stopScanCodeListener()
  stopPaymentStatusPolling()
  showScanStatus.value = false
  paying.value = false
  paymentSuccess.value = true
  
  // 保留原有的 orderSn，避免被 paymentData 覆盖
  const originalOrderSn = orderInfo.value?.orderSn
  orderInfo.value = { 
    ...orderInfo.value, 
    ...paymentData, 
    orderSn: originalOrderSn || paymentData.orderSn,
    payTime: new Date().toISOString() 
  }
  
  console.log('💰 支付成功，订单信息:', {
    orderSn: orderInfo.value?.orderSn,
    orderId: orderInfo.value?.orderId,
    amount: orderInfo.value?.amount,
    payAmount: orderInfo.value?.payAmount
  })
  
  clearPaidItems()

  // 语音播报支付成功
  try {
    const amount = orderInfo.value.amount || orderInfo.value.payAmount || props.orderData.finalAmount
    await speakPaymentSuccess(amount)
  } catch (error) {
    console.warn('语音播报失败:', error)
  }

  showNotify({ type: 'success', message: '支付成功', duration: 1500 })
  emit('success', orderInfo.value)
  
  // 保存订单信息用于跳转
  const savedOrderInfo = { ...orderInfo.value }
  
  // 直接跳转到支付结果页
  emit('close')
  router.replace({
    path: '/payment-result',
    query: {
      orderSn: savedOrderInfo.orderSn || '',
      amount: savedOrderInfo.amount || savedOrderInfo.payAmount || ''
    }
  })
}
const handlePaymentFailure = async (reason: string) => {
  stopScanCodeListener()
  stopPaymentStatusPolling()
  showScanStatus.value = false
  paying.value = false
  if (orderInfo.value?.orderId) {
    try {
      const guestId = memberStore.isLoggedIn ? undefined : 'guest_' + Date.now()
      const cancelReason = `支付失败自动取消: ${reason}`
      await OrderAPI.cancelOrder(orderInfo.value.orderId, guestId, cancelReason)
    } catch {}
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
  
  // 启动自动返回首页计时器（30秒无操作自动返回）
  startAutoReturnTimer()
  
  emit('fail', reason)
}
const handlePaymentTimeout = async () => {
  stopScanCodeListener()
  stopPaymentStatusPolling()
  showScanStatus.value = false
  paying.value = false
  if (orderInfo.value?.orderId) {
    try {
      const guestId = memberStore.isLoggedIn ? undefined : 'guest_' + Date.now()
      const reason = '支付超时自动取消'
      await OrderAPI.cancelOrder(orderInfo.value.orderId, guestId, reason)
    } catch {}
  }
  paymentSuccess.value = false
  paymentMessage.value = '支付超时，订单已自动取消'
  showPaymentResult.value = true
  
  // 启动自动返回首页计时器（10秒无操作自动返回）
  startAutoReturnTimer()
  
  emit('fail', 'timeout')
}
const clearPaidItems = async () => {
  try {
    cartStore.clearLocalCart()
  } catch {}
}
const handleClose = () => {
  stopScanCodeListener()
  stopPaymentStatusPolling()
  stopAutoReturnTimer() // 停止自动返回计时器
  
  // 保存订单信息用于跳转，避免状态重置后丢失
  const savedOrderInfo = orderInfo.value ? { ...orderInfo.value } : null
  const wasPaymentSuccess = paymentSuccess.value
  
  emit('close')
  
  // 如果在支付结果弹窗
  if (showPaymentResult.value) {
    // 支付成功跳转到支付结果页，支付失败返回首页
    if (wasPaymentSuccess && savedOrderInfo?.orderSn) {
      router.replace({
        path: '/payment-result',
        query: {
          orderSn: savedOrderInfo.orderSn,
          amount: savedOrderInfo.amount || savedOrderInfo.payAmount || ''
        }
      })
    } else {
      router.replace('/')
    }
  }
}
const retryPayment = async () => {
  // 停止自动返回计时器
  stopAutoReturnTimer()
  
  showPaymentResult.value = false
  scannedPaymentCode.value = ''
  
  // 重置支付处理标志，允许重新扫码
  isProcessingPayment.value = false
  lastProcessedPaymentCode = ''
  lastPaymentCodeTime = 0

  // 支付失败后订单已被取消，需要重新创建订单
  // 清空订单信息，重新走下单流程
  orderInfo.value = null
  await handlePay()
}
const simulateScanPayment = () => {
  if (!testPaymentCode.value) {
    showToast('请输入测试付款码')
    return
  }
  processScanPayment(testPaymentCode.value)
  showTestPanel.value = false
}
const useTestCode = (type: 'wechat' | 'alipay') => {
  if (type === 'wechat') {
    testPaymentCode.value = '134567890123456789'
  } else {
    testPaymentCode.value = '280000000000000000'
  }
}

// 组件挂载时初始化 TTS
onMounted(async () => {
  try {
    await TTSManager.getInstance().initialize()
    console.log('✅ PaymentDialog TTS 初始化成功')
  } catch (error) {
    console.warn('⚠️ PaymentDialog TTS 初始化失败:', error)
  }
})

// 组件卸载时清理扫码监听器
onUnmounted(() => {
  console.log('🧹 PaymentDialog组件卸载，清理支付扫码监听器')
  stopScanCodeListener()
  stopAutoReturnTimer() // 清理自动返回计时器

  // 调试日志 - 组件卸载清理
  debugLogger.logScan(
    '支付组件卸载清理',
    'PaymentDialog组件卸载，清理支付扫码监听器',
    {
      scanType: 'payment',
      status: 'success',
      result: { action: 'component_unmounted_cleanup' }
    } as ScanLogData
  )
})
</script>

<style scoped lang="scss">
.payment-dialog-mask {
  position: fixed;
  left: 0;
  top: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0,0,0,0.45);
  z-index: 2000;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}
.payment-dialog-container {
  width: 100%;
  max-width: 100vw;
  background: #fff;
  border-radius: 32px 32px 0 0;
  box-shadow: 0 -8px 32px rgba(0,0,0,0.08);
  padding: 48px 48px 32px 48px;
  margin-bottom: 0;
  animation: popup-slide-up 0.25s cubic-bezier(0.4,0,0.2,1);
}
.dialog-title {
  text-align: center;
  font-weight: 600;
  font-size: 64px;
  line-height: 90px;
  color: #0A0D05;
  margin-bottom: 40px;
}
@keyframes popup-slide-up {
  from { transform: translateY(100%); }
  to { transform: translateY(0); }
}
.dialog-header {
  text-align: center;
  margin-bottom: 40px;
  .amount-label {
    font-size: 32px;
    color: #666;
    margin-bottom: 8px;
  }
  .amount-value {
    font-size: 56px;
    font-weight: 700;
    color: #647D00;
    margin-bottom: 8px;
  }
  .bundle-discount-info {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 16px;
    margin-bottom: 12px;
    .original-price {
      font-size: 28px;
      color: #999;
      text-decoration: line-through;
    }
    .discount-tag {
      font-size: 26px;
      color: #647D00;
      background: rgba(169, 255, 0, 0.3);
      padding: 6px 16px;
      border-radius: 20px;
      font-weight: 500;
    }
  }
  .amount-desc {
    font-size: 32px;
    color: #999;
  }
}
.dialog-methods {
  margin-bottom: 40px;
  .method-title {
    font-size: 32px;
    color: #333;
    margin-bottom: 24px;
    text-align: center;
  }
  .method-row {
    margin-bottom: 32px;
    &:last-child {
      margin-bottom: 0;
    }
  }

  .method-list {
    display: flex;
    justify-content: center;
    gap: 32px;
    .method-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      gap: 16px;
      padding: 16px;
      border-radius: 16px;
      cursor: pointer;
      min-width: 120px;
      border: 2px solid transparent;
      position: relative;
      width: 454px;
      height: 461px;
      box-sizing: border-box;
    }

    // 半宽样式（微信、支付宝）
    .method-half {
      width: 454px;
      height: 461px;
    }

    // 全宽样式（余额支付）
    .method-full {
      width: 940px;
      min-height: 280px;
      height: auto; // 自适应高度
    }

    .method-item.active[aria-wechat] {
      background: rgba(169,255,0,0.2);
      border-radius: 16px;
    }
    .method-item.active[aria-alipay] {
      background: rgba(6,180,253,0.2);
      border-radius: 16px;
    }
    .method-item.active[aria-balance] {
      background: rgba(27,253,6,0.2);
      border-radius: 16px;
    }
    .method-icon {
      width: 168px;
      height: 168px;
      object-fit: contain;
    }
    .method-content {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 8px;
    }
    .method-name {
      font-size: 56px;
      color: #000;
      font-weight: 600;
      text-align: center;
      line-height: 1.4;
    }
    .method-desc {
      font-size: 32px;
      color: #666;
      text-align: center;
      line-height: 1.4;
    }
  }
}

.dialog-overlay {
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.35);
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: center;
}
.dialog-popup-center {
  background: #fff;
  border-radius: 32px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.12);
  padding: 96px 0 80px 0;
  min-width: 800px;
  max-width: 960px;
  min-height: 480px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.scan-status-popup .status-header {
  margin-bottom: 48px;
  text-align: center;
}
.scan-status-popup .status-title {
  font-size: 64px;
  font-weight: 700;
  color: #333;
  margin-bottom: 24px;
}
.scan-status-popup .status-amount {
  font-size: 56px;
  font-weight: 600;
  color: #647D00;
  margin-bottom: 12px;
}
.scan-status-popup .status-discount-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-bottom: 16px;
  .original-price {
    font-size: 28px;
    color: #999;
    text-decoration: line-through;
  }
  .discount-tag {
    font-size: 26px;
    color: #647D00;
    background: rgba(169, 255, 0, 0.3);
    padding: 6px 16px;
    border-radius: 20px;
    font-weight: 500;
  }
}
.scan-status-popup .status-content {
  margin-bottom: 48px;
  text-align: center;
}
.scan-status-popup .scan-steps {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 32px;
  margin-bottom: 32px;
}
.scan-status-popup .scan-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 414px;
  min-width: 414px;
  justify-content: flex-start;
}
.scan-status-popup .scan-step-img {
  width: 414px;
  height: 436px;
  object-fit: contain;
  margin-bottom: 12px;
}
.scan-status-popup .scan-step-label {
  font-size: 40px;
  color: #0A0D05;
  text-align: center;
  font-weight: 600;
}
.scan-status-popup .scan-arrow {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 48px;
}
.scan-status-popup .scan-arrow-img {
  width: 48px;
  height: 48px;
  object-fit: contain;
}
.scan-status-popup .scan-tip {
  font-size: 40px;
  color: #333;
  margin-bottom: 12px;
}
.scan-status-popup .scan-desc {
  font-size: 32px;
  color: #999;
}
.scan-status-popup .status-actions {
  margin-top: 32px;
  display: flex;
  justify-content: center;
}
.scan-status-popup .cancel-btn {
  font-size: 40px;
  width: 320px;
  height: 100px;
  border-radius: 16px;
  background: #EDF2DC;
  color: #647D00;
  border: none;
  margin: 0 16px;
  cursor: pointer;
}
.payment-result-popup .result-title {
  font-size: 64px;
  font-weight: 700;
  color: #333;
  margin-bottom: 24px;
  text-align: center;
}
.payment-result-popup .result-message {
  font-size: 40px;
  color: #999;
  margin-bottom: 32px;
  text-align: center;
}
.payment-result-popup .order-summary {
  font-size: 36px;
  color: #333;
  margin-bottom: 32px;
}
.payment-result-popup .result-actions {
  display: flex;
  justify-content: center;
  gap: 32px;
}
.payment-result-popup .pay-btn, .payment-result-popup .cancel-btn {
  font-size: 40px;
  width: 320px;
  height: 100px;
  border-radius: 16px;
  background: #EDF2DC;
  color: #647D00;
  border: none;
  margin: 0 16px;
  cursor: pointer;
}
.payment-result-popup .pay-btn {
  background: #20201E;
  color: #A9FF00;
}

// 余额支付确认弹窗样式
.balance-confirm-popup {
  background: white;
  border-radius: 24px;
  padding: 64px 48px 48px;
  width: 800px;
  max-width: 90vw;

  .confirm-content {
    margin-bottom: 48px;
  }

  .confirm-title {
    font-size: 56px;
    font-weight: 700;
    color: #0A0D05;
    margin-bottom: 48px;
    text-align: center;
  }

  .balance-info {
    background: transparent;
    border-radius: 0;
    padding: 0;
    margin-bottom: 24px;
  }

  .balance-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 28px 0;
    font-size: 40px;
    border-bottom: 1px solid #F0F0F0;

    &:first-child {
      padding-top: 0;
    }

    &:last-child {
      border-bottom: none;
      padding-bottom: 0;
    }

    // 支付金额行高亮
    &.highlight {
      background: #F8F8F8;
      padding: 28px 24px;
      margin: 0 -24px;
      border-radius: 12px;
      border-bottom: none;

      .label {
        color: #0A0D05;
        font-weight: 600;
      }

      .value {
        font-size: 48px;
        font-weight: 700;
      }
    }

    .label {
      color: #666666;
      font-size: 40px;
      font-weight: 400;
    }

    .value {
      font-weight: 600;
      font-size: 44px;

      &.balance-amount {
        color: #1BAF00;
      }

      &.pay-amount {
        color: #FF6B00;
      }

      &.remaining-amount {
        color: #0A0D05;
      }
    }
  }

  .insufficient-warning {
    color: #FF3B30;
    font-size: 36px;
    text-align: center;
    margin-top: 24px;
    padding: 16px;
    background: rgba(255, 59, 48, 0.1);
    border-radius: 12px;
  }

  .confirm-actions {
    display: flex;
    justify-content: center;
    gap: 32px;

    button {
      font-size: 44px;
      font-weight: 600;
      width: 320px;
      height: 100px;
      border-radius: 16px;
      border: none;
      cursor: pointer;
      transition: all 0.2s ease;

      &.cancel-btn {
        background: #EDF2DC;
        color: #647D00;

        &:active {
          background: #DDE2CC;
        }
      }

      &.confirm-btn {
        background: #20201E;
        color: #A9FF00;

        &:active {
          background: #0A0D05;
        }

        &:disabled {
          background: #E5E5E5;
          color: #999999;
          cursor: not-allowed;
        }
      }
    }
  }
}

.test-pay-btn-box {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}
.test-pay-btn {
  width: 308px;
  height: 80px;
  background: #20201E;
  color: #A9FF00;
  font-size: 36px;
  border-radius: 16px;
  border: none;
  font-family: 'PingFang SC', sans-serif;
  font-weight: 400;
  cursor: pointer;
}
.test-scan-popup-mask {
  position: fixed;
  left: 0; top: 0; width: 100vw; height: 100vh;
  background: rgba(0,0,0,0.45);
  z-index: 3000;
  display: flex;
  align-items: center;
  justify-content: center;
}
.test-scan-popup {
  background: #fff;
  border-radius: 32px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.12);
  padding: 64px 64px 48px 64px;
  min-width: 720px;
  max-width: 98vw;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.test-header {
  text-align: center;
  margin-bottom: 48px;
}
.test-title {
  font-size: 56px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
}
.test-desc {
  font-size: 36px;
  color: #666;
  margin: 0;
}
.test-input-section {
  margin-bottom: 32px;
}
.test-input {
  width: 480px;
  height: 96px;
  font-size: 40px;
  border: 2px solid #eee;
  border-radius: 16px;
  padding: 0 32px;
  margin-bottom: 16px;
}
.test-preset-codes {
  margin-bottom: 32px;
}
.preset-title {
  font-size: 32px;
  color: #666;
  margin: 0 0 16px 0;
}
.preset-buttons {
  display: flex;
  gap: 32px;
}
.preset-btn {
  flex: 1;
  height: 64px;
  font-size: 32px;
  border-radius: 16px;
  border: 2px solid #ddd;
  background: #f8f9fa;
  color: #333;
  cursor: pointer;
}
.test-actions {
  display: flex;
  gap: 32px;
  margin-top: 32px;
}
.test-confirm-btn {
  width: 220px;
  height: 80px;
  background: #20201E;
  color: #A9FF00;
  font-size: 36px;
  border-radius: 16px;
  border: none;
  cursor: pointer;
}
.test-cancel-btn {
  width: 160px;
  height: 80px;
  background: #EDF2DC;
  color: #647D00;
  font-size: 36px;
  border-radius: 16px;
  border: none;
  cursor: pointer;
}
// 放大全局 toast 字体
:deep(.van-toast__text) {
  font-size: 48px !important;
  line-height: 1.4;
}
@media (max-width: 1200px) {
  .dialog-popup-center {
    min-width: 90vw;
    max-width: 98vw;
    padding: 48px 0 40px 0;
  }
  .scan-status-popup .status-title,
  .payment-result-popup .result-title {
    font-size: 40px;
  }
  .scan-status-popup .status-amount {
    font-size: 32px;
  }
  .scan-status-popup .scan-tip {
    font-size: 24px;
  }
  .scan-status-popup .scan-desc {
    font-size: 18px;
  }
}
</style> 