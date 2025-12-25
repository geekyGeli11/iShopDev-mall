<template>
  <div class="scan-page">
    <!-- 顶部栏：返回 + 会员信息 + 取消交易倒计时 -->
    <div class="header-bar">
      <div class="header-left">
        <van-icon name="arrow-left" size="60" @click="goBack" />
        
        <div 
          class="member-info" 
          :class="{ 'benefit-prompt': !memberStore.isLoggedIn || memberStore.isGuestLogin }"
          @click="handleMemberClick"
        >
          <span class="member-text">
            {{ memberDisplayText }}
          </span>
        </div>
      </div>
      
      <div class="cancel-timer" @click="cancelTransaction">
        <span class="timer-text">取消交易{{ countdownSeconds }}s</span>
      </div>
    </div>

    <!-- 商品列表区域 -->
    <div class="product-list-section">
      <!-- 有商品时显示商品列表 -->
      <div v-if="scannedProducts.length > 0" class="product-list">
        <div
          v-for="(item, index) in scannedProducts"
          :key="`${item.productId}-${item.skuId}-${index}`"
          class="product-item"
        >
          <div class="product-left">
            <van-image
              :src="item.productPic || '/images/placeholder.png'"
              width="60"
              height="60"
              fit="cover"
              radius="8"
              class="product-image"
            />
          </div>
          <div class="product-content">
            <div class="product-info">
              <h4 class="product-name">{{ item.productName }}</h4>
              <div class="product-details">
                <p class="product-price">¥{{ formatPrice(item.currentPrice) }}</p>
                <p class="product-spec" v-if="item.unit || item.skuSpecInfo">
                  {{ item.unit || '' }}{{ item.unit && item.skuSpecInfo ? ' | ' : '' }}{{ item.skuSpecInfo || '' }}
                </p>
              </div>
            </div>
            <div class="product-quantity">
              <div class="quantity-control">
                <div
                  class="quantity-btn minus"
                  @click="decreaseQuantity(index)"
                  :class="{ disabled: item.quantity <= 0 }"
                >
                  −
                </div>
                <div class="quantity-num">{{ item.quantity }}</div>
                <div
                  class="quantity-btn plus"
                  @click="increaseQuantity(index)"
                >
                  +
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 有商品时也显示手动输入入口 -->
        <div class="empty-action">
          <span>扫不出？</span>
          <span class="manual-input-link" @click="showManualInput = true">请手动输入条码</span>
        </div>
      </div>

      <!-- 无商品时显示提示 -->
      <div v-else class="empty-cart custom-empty-cart">
        <div class="empty-img-box">
          <img class="empty-img" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/mall-selfcheck/assets/images/scan/empty.png" alt="空" />
        </div>
        <div class="empty-desc">请使用扫码器扫描商品条码</div>
        <div class="scan-status" v-if="scannedBarcode">
          <span class="scan-hint">正在扫描: {{ scannedBarcode }}</span>
        </div>
        <div class="empty-action">
          <span>扫不出？</span>
          <span class="manual-input-link" @click="showManualInput = true">请手动输入条码</span>
        </div>

      </div>
    </div>

    <!-- 优惠券区域 -->
    <div
      v-if="scannedProducts.length > 0 && memberStore.isLoggedIn && !memberStore.isGuestLogin"
      class="custom-coupon-entry"
      @click="loadAndShowCoupons"
    >
      <span class="coupon-entry-text">优惠券选择入口</span>
      <img class="coupon-entry-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/mall-selfcheck/assets/images/payment/arrow_right.png" alt="扫码商品" />
    </div>

    <!-- 底部支付区域 -->
    <div class="payment-footer custom-payment-footer">
      <div class="footer-amount-box">
        <span class="footer-amount">￥{{ formatPrice(finalAmount) }}</span>
        <span class="footer-quantity">共{{ totalQuantity }}件</span>
      </div>
      <button 
        class="footer-pay-btn"
        :disabled="scannedProducts.length === 0"
        @click="goToPayment"
      >
        确认付款
      </button>
    </div>

    <!-- 会员登录弹窗组件 -->
    <MemberLoginModal 
      v-model="showMemberLogin"
      @login-success="handleLoginSuccess"
    />

    <!-- 优惠券列表弹窗 -->
    <van-popup
      v-model:show="showCouponList"
      position="bottom"
      :style="{ height: '25vh', maxHeight: '25vh' }"
      closeable
      round
      class="coupon-popup-container"
    >
      <div class="coupon-popup">
        <div class="popup-header">
          <h3>选择优惠券</h3>
          <p v-if="availableCoupons.length > 0" class="coupon-count">
            共 {{ availableCoupons.length }} 张可用优惠券
            <span v-if="availableCoupons.length > 3" class="scroll-hint">，可上下滑动查看</span>
          </p>
        </div>

        <div class="coupon-list" ref="couponListRef">
          <div 
            v-for="coupon in availableCoupons" 
            :key="coupon.historyId"
            class="coupon-item"
            :class="{ selected: selectedCoupon?.historyId === coupon.historyId }"
            @click="selectCoupon(coupon)"
          >
            <div class="coupon-info">
              <h4 class="coupon-name">{{ coupon.name }}</h4>
              <p class="coupon-desc">{{ getCouponConditionText(coupon) }}</p>
              <p class="coupon-amount">{{ getCouponDisplayAmount(coupon) }}</p>
            </div>
            <van-radio 
              :checked="selectedCoupon?.historyId === coupon.historyId"
              @click.stop="selectCoupon(coupon)"
            />
          </div>
          
          <div v-if="availableCoupons.length === 0" class="no-coupons">
            <van-empty description="暂无可用优惠券" />
          </div>
        </div>
        
        <div class="coupon-footer">
          <van-button 
            block 
            type="primary" 
            size="large"
            @click="applyCoupon"
          >
            确认使用
          </van-button>
        </div>
      </div>
    </van-popup>

    <!-- 手动输入弹窗 -->
    <div v-if="showManualInput">
      <div class="manual-input-mask" @click="showManualInput = false"></div>
      <div class="manual-input-bottom-popup manual-input-bottom-popup--custom">
        <div class="manual-input-popup">
          <div class="popup-header">
            <h3>手动输入商品码</h3>
          </div>
          <van-form @submit="handleManualScan" class="manual-form">
            <van-cell-group inset>
              <van-field
                v-model="manualCode"
                name="productCode"
                label="商品条码"
                placeholder="请输入商品条码或二维码"
                :rules="[{ required: true, message: '请输入商品码' }]"
                maxlength="20"
                left-icon="scan"
                readonly
                @click="showBarcodeKeyboard"
                autocomplete="off"
              />
            </van-cell-group>
            <div class="form-footer">
              <van-button 
                block 
                type="primary" 
                size="large"
                :loading="scanning"
                native-type="submit"
                round
              >
                确认扫描
              </van-button>
            </div>
          </van-form>
        </div>
      </div>
    </div>

    <!-- 数字键盘组件 -->
    <NumericKeyboard
      v-model:visible="keyboardVisible"
      v-model:value="currentKeyboardValue"
      :title="keyboardTitle"
      :max-length="keyboardMaxLength"
      :keyboard-type="keyboardType"
      @confirm="handleKeyboardConfirm"
      @cancel="handleKeyboardCancel"
      @close="handleKeyboardClose"
    />

    <!-- 扫码成功提示 -->
    <van-overlay 
      v-model:show="showScanSuccess" 
      class="scan-success-overlay"
      z-index="9999"
    >
      <div class="success-content">
        <van-icon name="success" size="48" color="#07c160" />
        <p class="success-text">扫码成功！</p>
      </div>
    </van-overlay>

    <!-- 支付弹窗 -->
    <PaymentDialog
      :visible="showPaymentDialog"
      :orderData="currentOrderData"
      :paymentMethods="paymentMethods"
      iconUrl="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/mall-selfcheck/assets/images/payment/arrow_right.png"
      @close="handlePaymentDialogClose"
      @pay="handlePaymentDialogPay"
      @success="handlePaymentDialogSuccess"
      @fail="handlePaymentDialogFail"
    />


  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showLoadingToast, closeToast, showNotify, showConfirmDialog } from 'vant'
import { useCartStore } from '@/store/modules/cart'
import { useMemberStore } from '@/store/modules/member'
import { ProductAPI } from '@/api/modules/product'
import { CouponAPI } from '@/api/modules/coupon'
import MemberLoginModal from '@/components/business/MemberLoginModal/index.vue'
import NumericKeyboard from '@/components/common/NumericKeyboard/index.vue'
import PaymentDialog from '@/components/business/PaymentDialog.vue'

import { scanManager } from '@/utils/scanManager'
import { scanSimulator } from '@/utils/scanSimulator'
import { debugLogger, type ScanLogData } from '../../utils/debugLogger'

const router = useRouter()
const cartStore = useCartStore()
const memberStore = useMemberStore()

// 返回方法
const goBack = () => {
  router.back()
}

// 判断是否为二维码格式
const isQRCode = (code: string): boolean => {
  // 二维码通常包含以下特征：
  // 1. 包含 http:// 或 https://
  // 2. 包含 www.
  // 3. 长度较长（通常超过30个字符）
  // 4. 包含特殊字符如 :、/、?、=、&

  const qrCodePatterns = [
    /^https?:\/\//i,           // 以 http:// 或 https:// 开头
    /^www\./i,                 // 以 www. 开头
    /^ftp:\/\//i,              // 以 ftp:// 开头
    /[?&=]/,                   // 包含 URL 参数字符
  ]

  // 检查是否匹配二维码特征
  const matchesPattern = qrCodePatterns.some(pattern => pattern.test(code))

  // 或者长度过长（商品条码通常不超过20位）
  const tooLong = code.length > 30

  return matchesPattern || tooLong
}

// 响应式数据
const manualCode = ref('')
const countdownSeconds = ref(600)

// 本地扫描商品列表
const scannedProducts = ref<any[]>([])

// 状态管理
const scanning = ref(false)

// 扫码监听相关
const scannedBarcode = ref('')
let scanListenerAdded = false



// 弹窗状态
const showManualInput = ref(false)
const showScanSuccess = ref(false)
const showCouponList = ref(false)
const showMemberLogin = ref(false)
const showPaymentDialog = ref(false)

// DOM引用
const couponListRef = ref<HTMLElement>()



// 优惠券相关
const availableCoupons = ref<any[]>([])
const selectedCoupon = ref<any>(null)

// 支付相关
const paymentMethods = computed(() => {
  const methods = [
    { id: 'wechat', name: '微信支付' },
    { id: 'alipay', name: '支付宝' }
  ]

  // 如果用户已登录，添加余额支付选项
  if (memberStore.isLoggedIn) {
    methods.push({ id: 'balance', name: '会员储值支付' })
  }

  return methods
})
const currentOrderData = ref<any>(null)

// 计时器
let countdownTimer: NodeJS.Timeout | null = null

// 数字键盘相关状态
const keyboardVisible = ref(false)
const currentKeyboardValue = ref('')
const keyboardTitle = ref('')
const keyboardMaxLength = ref(20)
const keyboardType = ref<'numeric' | 'phone' | 'memberCode'>('numeric')
const currentInputField = ref<'barcode' | ''>('')

// 计算属性
const totalQuantity = computed(() => {
  return scannedProducts.value.reduce((total, item) => total + item.quantity, 0)
})

const totalAmount = computed(() => {
  return scannedProducts.value.reduce((total, item) => {
    return total + (item.currentPrice * item.quantity)
  }, 0)
})

// 计算优惠券折扣金额
const couponDiscount = computed(() => {
  if (!selectedCoupon.value) return 0
  return calculateCouponDiscount(selectedCoupon.value)
})

// 计算最终应付金额
const finalAmount = computed(() => {
  return Math.max(0, totalAmount.value - couponDiscount.value)
})

// 格式化价格
const formatPrice = (price: number) => {
  return (price || 0).toFixed(2)
}

// 获取会员显示文本 - 使用计算属性避免重复执行
const memberDisplayText = computed(() => {
  if (!memberStore.isLoggedIn) {
    return '登录可享受会员权益'
  }
  
  // 如果是游客登录，显示会员权益提示
  if (memberStore.isGuestLogin) {
    return '登录可享受会员权益'
  }
  
  const memberInfo = memberStore.memberInfo
  if (!memberInfo) {
    return '会员信息加载中...'
  }
  
  // 优先显示nickname，如果没有则显示手机号码
  const displayName = memberInfo.nickname || memberInfo.phone || memberInfo.username || '会员'
  
  return `欢迎 ${displayName}`
})

// 处理会员区域点击
const handleMemberClick = () => {
  // 如果未登录或者是游客登录，显示会员登录弹窗
  if (!memberStore.isLoggedIn || memberStore.isGuestLogin) {
    showMemberLogin.value = true
  }
}

// 处理扫码结果（只获取商品信息并显示，不添加到购物车）
const handleScanResult = async (code: string) => {
  if (scanning.value) return

  scanning.value = true

  // 调试日志 - 扫码开始
  const scanStartTime = Date.now()
  debugLogger.logScan(
    '商品扫码开始',
    `开始处理商品条码: ${code}`,
    {
      scanType: 'product',
      barcode: code,
      status: 'start'
    } as ScanLogData
  )

  try {
    // 过滤二维码格式（通常包含http、https、www等）
    if (isQRCode(code)) {
      console.warn('⚠️ 检测到二维码格式，已过滤:', code)
      showToast({
        type: 'fail',
        message: '请扫描商品条码，不是二维码',
        duration: 2000
      })

      debugLogger.logScan(
        '商品扫码过滤',
        `检测到二维码格式，已过滤: ${code}`,
        {
          scanType: 'product',
          barcode: code,
          status: 'error',
          error: 'QR code detected'
        } as ScanLogData
      )

      scanning.value = false
      return
    }

    // 显示扫码成功动画
    showScanSuccess.value = true
    setTimeout(() => {
      showScanSuccess.value = false
    }, 1000)

    // 查询商品信息
    const loadingToast = showLoadingToast({
      message: '查询商品信息...',
      forbidClick: true,
      duration: 0
    })

    // 调用ProductAPI.scanProduct获取商品信息
    const response = await ProductAPI.scanProduct({
      barcode: code
    })

    closeToast()
    
    if (response && response.data) {
      // 调试日志 - 商品查询成功
      const scanDuration = Date.now() - scanStartTime
      debugLogger.logScan(
        '商品扫码成功',
        `成功获取商品信息: ${response.data.productName}`,
        {
          scanType: 'product',
          barcode: code,
          status: 'success',
          result: response.data,
          duration: scanDuration
        } as ScanLogData
      )

      // 检查是否已存在相同的SKU
      const existingIndex = scannedProducts.value.findIndex(item =>
        item.productId === response.data.productId && item.skuId === response.data.skuId
      )

      if (existingIndex !== -1) {
        // 如果商品已存在，数量+1
        scannedProducts.value[existingIndex].quantity += 1

        debugLogger.logScan(
          '商品数量增加',
          `${response.data.productName} 数量增加到 ${scannedProducts.value[existingIndex].quantity}`,
          {
            scanType: 'product',
            barcode: code,
            status: 'success',
            result: { action: 'quantity_increase', newQuantity: scannedProducts.value[existingIndex].quantity }
          } as ScanLogData
        )

        showNotify({
          type: 'success',
          message: `${response.data.productName} 数量+1`,
          duration: 1500
        })
      } else {
        // 如果是新商品，添加到扫描列表
        const productData = {
          productId: response.data.productId,
          skuId: response.data.skuId,
          productName: response.data.productName,
          productPic: response.data.productPic,
          brandName: response.data.brandName,
          categoryName: response.data.categoryName,
          skuSpecInfo: response.data.skuSpecInfo,
          unit: response.data.unit,
          currentPrice: response.data.currentPrice,
          originalPrice: response.data.originalPrice,
          promotionPrice: response.data.promotionPrice,
          stock: response.data.stock,
          stockStatus: response.data.stockStatus,
          quantity: 1 // 默认数量为1
        }

        scannedProducts.value.push(productData)

        debugLogger.logScan(
          '新商品添加',
          `添加新商品到购物车: ${response.data.productName}`,
          {
            scanType: 'product',
            barcode: code,
            status: 'success',
            result: { action: 'new_product_added', product: productData }
          } as ScanLogData
        )

        showNotify({
          type: 'success',
          message: `已添加${response.data.productName}`,
          duration: 1500
        })
      }

    } else {
      throw new Error('商品不存在或已下架')
    }
    
  } catch (error: any) {
    closeToast()
    console.error('商品查询失败:', error)

    // 调试日志 - 扫码失败
    const scanDuration = Date.now() - scanStartTime
    debugLogger.logScan(
      '商品扫码失败',
      `商品条码扫描失败: ${error.message || '未知错误'}`,
      {
        scanType: 'product',
        barcode: code,
        status: 'error',
        error: error.message || '商品查询失败',
        duration: scanDuration
      } as ScanLogData
    )

    showToast({
      type: 'fail',
      message: error.message || '商品查询失败'
    })
  } finally {
    scanning.value = false
  }
}

// 手动扫码
const handleManualScan = async () => {
  if (!manualCode.value.trim()) return
  
  await handleScanResult(manualCode.value.trim())
  manualCode.value = ''
  showManualInput.value = false
}

// 增加商品数量
const increaseQuantity = (index: number) => {
  scannedProducts.value[index].quantity++
  console.log('增加数量:', scannedProducts.value[index].productName, scannedProducts.value[index].quantity)
}

// 减少商品数量
const decreaseQuantity = (index: number) => {
  // 如果数量已经是0或1，就不能再减少
  if (scannedProducts.value[index].quantity <= 0) {
    return
  }
  
  if (scannedProducts.value[index].quantity > 1) {
    scannedProducts.value[index].quantity--
    console.log('减少数量:', scannedProducts.value[index].productName, scannedProducts.value[index].quantity)
  } else {
    // 如果数量为1，再减少就移除商品
    const productName = scannedProducts.value[index].productName
    scannedProducts.value.splice(index, 1)
    console.log('移除商品:', productName)
  }
}

// 更新商品数量（保留原方法以防其他地方使用）
const updateQuantity = (index: number, newQuantity: number) => {
  if (newQuantity === 0) {
    // 删除商品
    scannedProducts.value.splice(index, 1)
  } else {
    // 更新数量
    scannedProducts.value[index].quantity = newQuantity
  }
}

// 处理登录成功
const handleLoginSuccess = (memberInfo: any) => {
  showNotify({
    type: 'success',
    message: '登录成功，享受会员专属权益',
    duration: 1500
  })

  console.log('会员登录成功:', memberInfo)

  // 清除游客token，确保使用会员token
  localStorage.removeItem('token') // 清除游客token
  localStorage.removeItem('guest_id') // 清除游客ID
  console.log('✅ 已清除游客token，确保使用会员token')

  // 会员登录成功后，重新激活商品扫码监听器
  console.log('🔄 会员登录成功，重新激活商品扫码监听器')

  // 调试日志 - 会员登录成功后恢复商品扫码
  debugLogger.logScan(
    '会员登录成功',
    '会员登录成功，重新激活商品扫码监听器',
    {
      scanType: 'product',
      status: 'start',
      result: { memberInfo, action: 'reactivate_product_scan' }
    } as ScanLogData
  )

  // 确保商品扫码监听器被正确激活
  try {
    // 先停用所有监听器，确保状态清洁
    scanManager.deactivateAll()

    // 重新注册并激活商品扫码监听器
    scanManager.register('product', handleBarcodeInput)
    scanManager.activate('product')

    console.log('✅ 商品扫码监听器已重新激活')
    console.log('📊 当前激活的扫码类型:', scanManager.getCurrentActiveType())

    // 调试日志 - 商品扫码监听器重新激活成功
    debugLogger.logScan(
      '商品扫码监听器重新激活',
      '会员登录后商品扫码监听器已成功重新激活',
      {
        scanType: 'product',
        status: 'success',
        result: {
          activeType: scanManager.getCurrentActiveType(),
          isProductActive: scanManager.isActive('product')
        }
      } as ScanLogData
    )

  } catch (error: any) {
    console.error('❌ 重新激活商品扫码监听器失败:', error)

    // 调试日志 - 重新激活失败
    debugLogger.logScan(
      '商品扫码监听器重新激活失败',
      `重新激活商品扫码监听器时发生错误: ${error?.message || '未知错误'}`,
      {
        scanType: 'product',
        status: 'error',
        error: error?.message || '未知错误'
      } as ScanLogData
    )
  }
}

// 加载并显示优惠券
const loadAndShowCoupons = async () => {
  if (!memberStore.isLoggedIn || memberStore.isGuestLogin || totalAmount.value <= 0) {
    showToast('请先登录会员账户并添加商品')
    return
  }

  showLoadingToast({
    message: '加载优惠券...',
    forbidClick: true
  })

  try {
    // 获取订单可用优惠券
    const response = await CouponAPI.getAvailableCouponsForOrder(totalAmount.value)
    
    if (response.code === 200 && response.data) {
      availableCoupons.value = response.data.list || []
      closeToast()
      
      if (availableCoupons.value.length === 0) {
        showToast('暂无可用优惠券')
      } else {
        showCouponList.value = true
      }
    } else {
      closeToast()
      showToast('获取优惠券失败')
      console.warn('获取可用优惠券失败:', response.message)
    }
  } catch (error) {
    closeToast()
    console.error('加载优惠券失败:', error)
    showToast('网络异常，请重试')
  }
}

// 选择优惠券
const selectCoupon = (coupon: any) => {
  selectedCoupon.value = coupon
}

// 应用优惠券
const applyCoupon = () => {
  if (selectedCoupon.value) {
    // 计算优惠券折扣
    const discount = calculateCouponDiscount(selectedCoupon.value)
    
    if (discount > 0) {
      showNotify({
        type: 'success',
        message: `已选择优惠券：${selectedCoupon.value.name}，优惠¥${formatPrice(discount)}`,
        duration: 2000
      })
    } else {
      showNotify({
        type: 'warning',
        message: '当前订单金额不满足优惠券使用条件',
        duration: 2000
      })
      selectedCoupon.value = null
    }
  }
  showCouponList.value = false
}

// 计算优惠券折扣（支持满减券和打折券）
const calculateCouponDiscount = (coupon: any) => {
  if (!coupon) return 0

  // 检查是否满足使用门槛
  const minPoint = coupon.minPoint || 0
  if (totalAmount.value < minPoint) {
    return 0
  }

  if (coupon.couponType === 1 && coupon.discountRate) {
    // 打折券：按折扣率计算
    const discountAmount = totalAmount.value * (1 - coupon.discountRate)
    return Number(Math.min(discountAmount, totalAmount.value).toFixed(2))
  } else if (coupon.amount) {
    // 满减券：优惠金额不能超过订单总额
    return Number(Math.min(coupon.amount, totalAmount.value).toFixed(2))
  }

  return 0
}

// 获取优惠券显示金额（支持满减券和打折券）
const getCouponDisplayAmount = (coupon: any) => {
  if (coupon.couponType === 1 && coupon.discountRate) {
    // 打折券：显示折扣率
    const discount = (coupon.discountRate * 10).toFixed(1)
    return `${discount}折`
  } else {
    // 满减券：显示减免金额
    return `¥${formatPrice(coupon.amount || 0)}`
  }
}

// 获取优惠券使用条件文本
const getCouponConditionText = (coupon: any) => {
  if (coupon.minPoint && coupon.minPoint > 0) {
    return `满¥${formatPrice(coupon.minPoint)}可用`
  }
  return '无门槛使用'
}

// 手动取消交易（带确认弹窗）
const cancelTransaction = async () => {
  const confirmed = await showConfirmDialog({
    title: '确认取消交易',
    message: '取消后将清空商品列表，是否继续？'
  })
  
  if (confirmed) {
    scannedProducts.value = []
    router.push('/')
  }
}

// 自动取消交易（倒计时结束，直接返回首页）
const autoCancel = () => {
  scannedProducts.value = []
  router.push('/')
}

// 前往支付页面（直接提交商品列表下单）
const goToPayment = () => {
  if (scannedProducts.value.length === 0) {
    showToast('请先添加商品')
    return
  }
  // 构造订单数据（同原逻辑）
  const orderItems = scannedProducts.value.map(item => ({
    id: `${item.productId}-${item.skuId}`,
    productId: item.productId,
    skuId: item.skuId,
    productName: item.productName,
    productPic: item.productPic,
    productSubTitle: item.brandName || item.unit,
    price: item.currentPrice,
    quantity: item.quantity,
    subtotal: item.currentPrice * item.quantity
  }))
  const totalOrderAmount = Number(orderItems.reduce((sum, item) => sum + item.subtotal, 0).toFixed(2))
  const memberDiscount = 0
  const appliedCouponDiscount = selectedCoupon.value ? Number(calculateCouponDiscount(selectedCoupon.value).toFixed(2)) : 0
  const deliveryFee = 0
  const finalOrderAmount = Number((totalOrderAmount - memberDiscount - appliedCouponDiscount + deliveryFee).toFixed(2))
  const orderData = {
    items: orderItems,
    totalAmount: totalOrderAmount,
    memberDiscount: memberDiscount,
    couponDiscount: appliedCouponDiscount,
    deliveryFee: deliveryFee,
    finalAmount: finalOrderAmount,
    deliveryType: 0,
    couponId: selectedCoupon.value?.historyId || null,
    couponInfo: selectedCoupon.value ? {
      id: selectedCoupon.value.historyId,
      name: selectedCoupon.value.name,
      type: selectedCoupon.value.type,
      discount: appliedCouponDiscount
    } : null
  }
  // 弹出支付弹窗
  currentOrderData.value = orderData
  showPaymentDialog.value = true
}
const handlePaymentDialogClose = () => {
  showPaymentDialog.value = false
}
const handlePaymentDialogPay = (method: string) => {
  // 由 PaymentDialog 内部完成支付流程，这里无需实现具体逻辑
  // 可根据需要在父组件监听 success/fail 事件后刷新订单、跳转等
}

// 支付弹窗回调：支付成功
const handlePaymentDialogSuccess = (orderInfo: any) => {
  showToast({ type: 'success', message: '支付成功，欢迎下次光临！' })
  // 如需后续处理可在此补充（如跳转首页、清空购物车等）
}
// 支付弹窗回调：支付失败
const handlePaymentDialogFail = (reason: string) => {
  showToast({ type: 'fail', message: reason || '支付失败，请重试' })
}

// 启动倒计时
const startCountdown = () => {
  countdownTimer = setInterval(() => {
    countdownSeconds.value--
    
    if (countdownSeconds.value <= 0) {
      // 倒计时结束，自动取消交易（不弹窗确认）
      clearTimers()
      autoCancel()
    }
  }, 1000)
}

// 清理计时器
const clearTimers = () => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
}

// 显示条码键盘
const showBarcodeKeyboard = () => {
  currentInputField.value = 'barcode'
  currentKeyboardValue.value = manualCode.value
  keyboardTitle.value = '请输入商品条码'
  keyboardMaxLength.value = 20
  keyboardType.value = 'numeric'
  keyboardVisible.value = true
}

// 处理键盘确认
const handleKeyboardConfirm = (value: string) => {
  switch (currentInputField.value) {
    case 'barcode':
      manualCode.value = value
      break
  }
  keyboardVisible.value = false
  currentInputField.value = ''
}

// 处理键盘取消
const handleKeyboardCancel = () => {
  keyboardVisible.value = false
  currentInputField.value = ''
}

// 处理键盘关闭
const handleKeyboardClose = () => {
  keyboardVisible.value = false
  currentInputField.value = ''
}



// 监听手动输入弹窗状态，确保弹窗关闭时键盘也关闭
watch(() => showManualInput.value, (newValue) => {
  if (!newValue) {
    // 弹窗关闭时，同时关闭键盘
    keyboardVisible.value = false
  }
})

// 监听会员登录弹窗状态，避免扫码冲突
watch(() => showMemberLogin.value, (newValue) => {
  if (newValue) {
    // 会员登录弹窗打开时，暂停商品扫码监听
    pauseBarcodeListener()
    console.log('🔄 会员登录弹窗打开，暂停商品扫码')
  } else {
    // 会员登录弹窗关闭时，恢复商品扫码监听
    resumeBarcodeListener()
    console.log('🔄 会员登录弹窗关闭，恢复商品扫码')
  }
})

// 监听支付弹窗状态，避免扫码冲突
watch(() => showPaymentDialog.value, (newValue) => {
  if (newValue) {
    // 支付弹窗打开时，暂停商品扫码监听
    pauseBarcodeListener()
    console.log('🔄 支付弹窗打开，暂停商品扫码')

    // 调试日志 - 支付弹窗打开
    debugLogger.logScan(
      '支付弹窗打开',
      '支付弹窗打开，暂停商品扫码监听',
      {
        scanType: 'product',
        status: 'progress',
        result: { action: 'pause_for_payment' }
      } as ScanLogData
    )
  } else {
    // 支付弹窗关闭时，恢复商品扫码监听
    resumeBarcodeListener()
    console.log('🔄 支付弹窗关闭，恢复商品扫码')

    // 调试日志 - 支付弹窗关闭
    debugLogger.logScan(
      '支付弹窗关闭',
      '支付弹窗关闭，恢复商品扫码监听',
      {
        scanType: 'product',
        status: 'success',
        result: { action: 'resume_after_payment' }
      } as ScanLogData
    )
  }
})

// 启动扫码监听
const startBarcodeListener = () => {
  // 注册商品扫码监听器
  scanManager.register('product', handleBarcodeInput)
  // 激活商品扫码监听器
  scanManager.activate('product')
  scanListenerAdded = true
}

// 停止扫码监听
const stopBarcodeListener = () => {
  if (scanListenerAdded) {
    scanManager.deactivate('product')
    scanManager.unregister('product')
    scanListenerAdded = false
    scannedBarcode.value = ''
  }
}

// 暂停扫码监听（当其他弹窗打开时）
const pauseBarcodeListener = () => {
  scanManager.deactivate('product')
}

// 恢复扫码监听（当其他弹窗关闭时）
const resumeBarcodeListener = () => {
  console.log('🔄 尝试恢复商品扫码监听器')
  console.log('📊 scanListenerAdded:', scanListenerAdded)
  console.log('📊 当前激活的扫码类型:', scanManager.getCurrentActiveType())

  // 调试日志 - 恢复扫码监听器
  debugLogger.logScan(
    '恢复商品扫码监听器',
    '弹窗关闭，尝试恢复商品扫码监听器',
    {
      scanType: 'product',
      status: 'start',
      result: {
        scanListenerAdded: scanListenerAdded,
        currentActiveType: scanManager.getCurrentActiveType()
      }
    } as ScanLogData
  )

  try {
    // 确保商品扫码监听器已注册
    if (!scanListenerAdded) {
      console.log('📝 商品扫码监听器未注册，重新注册')
      scanManager.register('product', handleBarcodeInput)
      scanListenerAdded = true
    }

    // 激活商品扫码监听器
    scanManager.activate('product')

    console.log('✅ 商品扫码监听器已恢复')
    console.log('📊 恢复后激活的扫码类型:', scanManager.getCurrentActiveType())
    console.log('📊 商品扫码是否激活:', scanManager.isActive('product'))

    // 调试日志 - 恢复成功
    debugLogger.logScan(
      '商品扫码监听器恢复成功',
      '商品扫码监听器已成功恢复',
      {
        scanType: 'product',
        status: 'success',
        result: {
          activeType: scanManager.getCurrentActiveType(),
          isProductActive: scanManager.isActive('product')
        }
      } as ScanLogData
    )

  } catch (error: any) {
    console.error('❌ 恢复商品扫码监听器失败:', error)

    // 调试日志 - 恢复失败
    debugLogger.logScan(
      '商品扫码监听器恢复失败',
      `恢复商品扫码监听器时发生错误: ${error?.message || '未知错误'}`,
      {
        scanType: 'product',
        status: 'error',
        error: error?.message || '未知错误'
      } as ScanLogData
    )
  }
}

// 处理扫码输入
const handleBarcodeInput = (event: KeyboardEvent) => {
  // 如果正在扫描中，忽略输入
  if (scanning.value) return

  // 防止与其他输入框冲突
  if (event.target && (event.target as HTMLElement).tagName === 'INPUT') {
    return
  }

  // 调试日志 - 扫码键盘事件
  debugLogger.logScan(
    '商品扫码键盘事件',
    `接收到按键: ${event.key}, 当前累积: ${scannedBarcode.value}`,
    {
      scanType: 'product',
      status: 'progress',
      result: { key: event.key, accumulated: scannedBarcode.value }
    } as ScanLogData
  )

  console.log('🎯 商品扫码事件:', event.key, '当前累积:', scannedBarcode.value)

  // Enter键表示扫码完成
  if (event.key === 'Enter') {
    if (scannedBarcode.value.length > 0) {
      console.log('✅ 商品扫码完成，条码:', scannedBarcode.value)

      // 调试日志 - 扫码完成
      debugLogger.logScan(
        '商品扫码输入完成',
        `扫码器输入完成，条码: ${scannedBarcode.value}`,
        {
          scanType: 'product',
          barcode: scannedBarcode.value,
          status: 'success',
          result: { finalBarcode: scannedBarcode.value, inputLength: scannedBarcode.value.length }
        } as ScanLogData
      )

      handleScanResult(scannedBarcode.value)
      scannedBarcode.value = '' // 清空缓存
    }
    return
  }

  // 忽略特殊键和控制键
  if (event.key.length > 1) {
    console.log('⏭️ 忽略特殊键:', event.key)

    // 调试日志 - 忽略特殊键
    debugLogger.logScan(
      '商品扫码忽略按键',
      `忽略特殊键: ${event.key}`,
      {
        scanType: 'product',
        status: 'progress',
        result: { ignoredKey: event.key, reason: 'special_key' }
      } as ScanLogData
    )

    return
  }

  // 累积扫码数据
  scannedBarcode.value += event.key
  console.log('📝 累积商品条码数据:', scannedBarcode.value)

  // 防止输入过长
  if (scannedBarcode.value.length > 20) {
    console.log('⚠️ 商品条码过长，重置')
    scannedBarcode.value = ''
  }

  // 阻止默认行为
  event.preventDefault()
}

// 生命周期
onMounted(async () => {
  // 检查是否需要进行游客登录
  if (!memberStore.isLoggedIn) {
    try {
      console.log('🔄 检测到未登录状态，开始游客登录...')
      await memberStore.guestLogin()
      console.log('✅ 游客登录成功')
    } catch (error: any) {
      console.error('❌ 游客登录失败:', error)
      // 游客登录失败时显示提示，但不阻止用户继续使用
      showToast({
        type: 'fail',
        message: '初始化失败，部分功能可能受限',
        duration: 2000
      })
    }
  }

  // 启动倒计时
  startCountdown()

  // 启动扫码监听
  startBarcodeListener()

  // 开发环境下挂载扫码模拟器到全局
  if (import.meta.env.DEV) {
    ;(window as any).scanSimulator = scanSimulator
    console.log('🔧 开发模式：扫码模拟器已挂载到 window.scanSimulator')
    console.log('💡 使用方法：')
    console.log('  - scanSimulator.simulateProductBarcode("020306001") // 模拟商品条码')
    console.log('  - scanSimulator.simulateMemberCode("M12345678901") // 模拟会员码')
    console.log('  - scanSimulator.simulatePaymentCode("134567890123456789") // 模拟付款码')
  }
})

onUnmounted(() => {
  clearTimers()
  stopBarcodeListener()
})
</script>

<style scoped lang="scss">
.scan-page {
  width: 100vw;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
  margin: 0;
  padding: 0;
  overflow: hidden;
  position: relative;
}

.header-bar {
  height: 126px;
  flex-shrink: 0;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  padding: 0 24px;
  z-index: 10;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}
.member-info {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-left: 8px;
  font-size: 15px;
  color: #333;
  cursor: pointer;
}

.member-text {
  width: 360px;
  height: 56px;
  font-family: 'PingFang SC', sans-serif;
  font-style: normal;
  font-weight: 400;
  font-size: 40px;
  line-height: 56px;
  color: #647D00;
  flex: none;
  order: 1;
  flex-grow: 0;
  /* 登录可享受会员权益 */
  display: flex;
  align-items: center;
  /* 保证文字不溢出 */
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
.cancel-timer {
  margin-left: auto;
  font-size: 32px;
  color: #000;
  border: 1px solid #CCCCCC;
  padding: 16px 32px;
  border-radius: 8px;
  cursor: pointer;
}

.product-list-section {
  flex: 1;
  width: 92%;
  margin: 0 auto;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  align-items: stretch;
  justify-content: flex-start;
  padding: 24px 0 300px 0; /* 增加底部内边距，为优惠券区域(104px)和底部结算栏(174px)留出空间，再加一些缓冲 */
}
.product-list {
  width: 984px;
  min-width: 984px;
  max-width: 984px;
  background: transparent;
  margin: 0 auto 32px auto;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: flex-start;
  gap: 32px;
  overflow: visible;

  /* 商品列表中的手动输入入口样式 */
  .empty-action {
    width: 100%;
    color: #000;
    font-size: 48px;
    font-weight: 600;
    text-align: center;
    margin-top: 32px;
    padding: 32px 0;
    background: #fff;
    border-radius: 16px;
    box-shadow: 0px 2px 1px rgba(0, 0, 0, 0.08);

    .manual-input-link {
      color: #647D00;
      font-size: 48px;
      font-weight: bold;
      margin-left: 8px;
      cursor: pointer;
      text-decoration: underline;

      &:hover {
        color: #5a6f00;
      }
    }
  }
}
.product-item {
  position: relative;
  width: 984px;
  height: 244px;
  display: flex;
  flex-direction: row;
  align-items: center;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0px 2px 1px rgba(0, 0, 0, 0.08);
  border-bottom: none;
  min-height: unset;
  padding: 0;
}
.product-item:last-child {
  border-bottom: none;
}
.product-left {
  width: 164px;
  height: 164px;
  border-radius: 8px;
  overflow: hidden;
  margin: 40px 0 40px 40px;
  flex-shrink: 0;
}
.product-image {
  width: 164px !important;
  height: 164px !important;
  border-radius: 8px;
  object-fit: cover;
  border: none;
  box-shadow: none;
}
.product-content {
  flex: 1;
  height: 164px;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: flex-start;
  min-height: unset;
  margin: 40px 40px 40px 40px;
  padding: 0;
  position: relative;
}
.product-info {
  width: 474px;
  height: 90px;
  margin-bottom: 24px;
}
.product-name {
  font-size: 32px;
  font-weight: 400;
  color: #0A0D05;
  line-height: 45px;
  height: 90px;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: normal;
  max-width: 474px;
}
.product-details {
  font-size: 24px;
  color: #666666;
  line-height: 34px;
  height: 34px;
  margin-top: 20px;
  width: 250px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.product-price {
  position: absolute;
  right: 0;
  bottom: 0;
  font-size: 40px;
  font-weight: 600;
  color: #647D00;
  width: 102px;
  height: 56px;
  line-height: 56px;
  text-align: right;
  margin: 0;
  z-index: 2;
}
.product-quantity {
  position: absolute;
  right: 142px;
  bottom: 0;
  width: 206px;
  height: 56px;
  display: flex;
  flex-direction: row;
  align-items: center;
  background: transparent;
  margin: 0;
  gap: 0;
  z-index: 3;
}
.quantity-control {
  display: flex;
  flex-direction: row;
  align-items: center;
  height: 56px;
  gap: 0;
}
.quantity-btn.minus {
  width: 56px;
  height: 56px;
  background: #F8F8F8;
  border-right: 1px solid #EEEEEE;
  border-radius: 4px 0 0 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  color: #999;
  cursor: pointer;
  box-sizing: border-box;
}
.quantity-num {
  width: 94px;
  height: 56px;
  background: #F8F8F8;
  border: 1px solid #EEEEEE;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  color: #000;
  font-family: 'PingFang SC', sans-serif;
  font-weight: 400;
  line-height: 45px;
  box-sizing: border-box;
}
.quantity-btn.plus {
  width: 56px;
  height: 56px;
  background: #F8F8F8;
  border-left: 1px solid #EEEEEE;
  border-radius: 0 4px 4px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  color: #000;
  cursor: pointer;
  box-sizing: border-box;
}

.action-buttons {
  width: 92%;
  margin: 0 auto;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  gap: 16px;
  padding: 0 0 12px 0;
}
.action-buttons .van-button {
  width: 440px;
  min-width: 360px;
  height: 126px;
  font-size: 40px;
  background: white;
  border: 1px solid #ddd;
  color: #666;
}

.payment-footer {
  height: 80px;
  flex-shrink: 0;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: relative;
  background: #fff;
  box-shadow: 0 -2px 8px rgba(0,0,0,0.04);
  padding: 0 32px;
  z-index: 10;
}
.payment-info {
  font-size: 20px;
  font-weight: 600;
  color: #333;
}
.payment-footer .payment-button {
  width: 220px;
  height: 56px;
  font-size: 20px;
  font-weight: bold;
  border-radius: 28px;
  margin-left: 24px;
}

.coupon-popup {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 0;

  .popup-header {
    text-align: center;
    padding: 20px;
    background: white;
    border-bottom: 1px solid #f0f0f0;
    flex-shrink: 0;
    position: sticky;
    top: 0;
    z-index: 10;

    h3 {
      margin: 0 0 8px 0;
      font-size: 18px;
      font-weight: 600;
      color: #333;
    }

    .coupon-count {
      margin: 0;
      font-size: 14px;
      color: #666;
      font-weight: 400;

      .scroll-hint {
        color: #999;
        font-size: 12px;
      }
    }
  }
}

/* 强制覆盖van-popup的高度限制 */
.coupon-popup-container {
  height: 25vh !important;
  max-height: 25vh !important;
}

/* 更强的覆盖，针对van-popup的所有可能类名 */
:deep(.van-popup--bottom.van-popup--round) {
  height: 25vh !important;
  max-height: 25vh !important;
}

.manual-input-popup {
  padding: 20px;

  .popup-header {
    text-align: center;
    margin-bottom: 20px;

    h3 {
      margin: 0;
      font-size: 18px;
      font-weight: 600;
      color: #333;
    }
  }
}

.manual-form {
  .form-footer {
    margin-top: 24px;
  }
  
  // 为只读输入框添加点击提示样式
  :deep(.van-field__control) {
    cursor: pointer;
    
    &[readonly] {
      background: #f8f9fa;
      color: #333;
      
      &::placeholder {
        color: #999;
      }
    }
  }
  
}

.coupon-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px;

  /* 优化滚动体验 */
  -webkit-overflow-scrolling: touch;
  scroll-behavior: smooth;

  /* 确保滚动区域明确，至少显示三个优惠券卡片 */
  min-height: 300px;
  
  .coupon-item {
    display: flex;
    align-items: center;
    padding: 18px;
    border: 2px solid #eee;
    border-radius: 12px;
    margin-bottom: 16px;
    cursor: pointer;
    transition: all 0.3s ease;
    background: white;
    min-height: 80px;

    &:last-child {
      margin-bottom: 0;
    }
    
    &:hover {
      border-color: #647D00;
      box-shadow: 0 2px 8px rgba(100, 125, 0, 0.15);
    }

    &.selected {
      border-color: #647D00;
      background: linear-gradient(135deg, #f5f8f0 0%, #edf2dc 100%);
      box-shadow: 0 4px 12px rgba(100, 125, 0, 0.25);

      .coupon-info {
        .coupon-name {
          color: #647D00;
        }
      }
    }
    
    .coupon-info {
      flex: 1;
      margin-right: 12px;
      
      .coupon-name {
        font-size: 15px;
        font-weight: 600;
        color: #333;
        margin: 0 0 6px 0;
        line-height: 1.3;
      }
      
      .coupon-desc {
        font-size: 12px;
        color: #666;
        margin: 0 0 8px 0;
        line-height: 1.2;
      }
      
      .coupon-amount {
        font-size: 18px;
        font-weight: 700;
        color: #ff4444;
        margin: 0;
      }
    }
    
    .van-radio {
      margin-left: auto;

      :deep(.van-radio__icon--checked) {
        background-color: #647D00 !important;
        border-color: #647D00 !important;
      }

      :deep(.van-radio__icon) {
        border-color: #ddd;

        &:hover {
          border-color: #647D00;
        }
      }
    }
  }
  
  .no-coupons {
    padding: 40px 20px;
    text-align: center;
  }
}

.coupon-footer {
  padding: 20px;
  border-top: 1px solid #eee;
  flex-shrink: 0;
  background: white;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  bottom: 0;
  z-index: 10;
  position: sticky;
  bottom: 0;
  z-index: 10;

  .van-button--primary {
    background: #20201E !important;
    border-color: #20201E !important;
    color: #A9FF00 !important;
    height: 50px !important;
    font-size: 16px !important;
    font-weight: 600 !important;
    border-radius: 25px !important;

    &:hover {
      background: #2a2a28 !important;
      border-color: #2a2a28 !important;
    }

    &:active {
      background: #1a1a18 !important;
      border-color: #1a1a18 !important;
    }
  }
}

.scan-success-overlay {
  display: flex;
  align-items: center;
  justify-content: center;
  
  .success-content {
    text-align: center;
    color: white;
    
    .success-text {
      margin-top: 16px;
      font-size: 18px;
      font-weight: 600;
    }
  }
}

.custom-empty-cart {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 1572px;
  padding: 40px 0 0 0;
  background: #fff;
  border-radius: 12px;
}
.empty-img-box {
  margin-bottom: 16px;
  display: flex;
  justify-content: center;
  align-items: center;
}
.empty-img {
  width: 440px;
  height: 480px;
  object-fit: contain;
  display: block;
}
.empty-desc {
  color: #999999;
  font-size: 40px;
  margin-bottom: 16px;
  margin-top: 0;
  text-align: center;
}
.empty-action {
  color: #000;
  font-size: 48px;
  font-weight: 600;
  text-align: center;
  margin-top: 0;
}
.manual-input-link {
  color: #000;
  font-size: 48px;
  font-weight: bold;
  margin-left: 2px;
}
.scan-status {
  margin: 16px 0;
  text-align: center;
}
.scan-hint {
  color: #647D00;
  font-size: 32px;
  font-weight: 600;
  background: rgba(164, 255, 0, 0.1);
  padding: 8px 16px;
  border-radius: 8px;
  display: inline-block;
}



.custom-payment-footer {
  position: fixed;
  left: 0;
  bottom: 0;
  width: 100%;
  height: 174px;
  background: #fff;
  box-shadow: 0px -1px 2px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: flex-start;
  padding: 0 48px;
  z-index: 20;
  /* 确保贴底显示，不受安全区域影响 */
  margin-bottom: 0;
  padding-bottom: 0;
}
.footer-amount-box {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 24px;
  width: 320px;
  height: 78px;
  margin-right: auto;
}
.footer-amount {
  font-family: 'PingFang SC', sans-serif;
  font-style: normal;
  font-weight: 600;
  font-size: 56px;
  line-height: 78px;
  color: #647D00;
  width: 219px;
  height: 78px;
  display: flex;
  align-items: center;
}
.footer-quantity {
  font-family: 'PingFang SC', sans-serif;
  font-style: normal;
  font-weight: 400;
  font-size: 32px;
  line-height: 45px;
  color: #999999;
  width: 150px;
  height: 45px;
  display: flex;
  align-items: center;
  white-space: nowrap;
}
.footer-pay-btn {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  padding: 17px 32px;
  gap: 8px;
  width: 308px;
  height: 126px;
  background: #20201E;
  border-radius: 16px;
  border: none;
  outline: none;
  font-family: 'PingFang SC', sans-serif;
  font-style: normal;
  font-weight: 400;
  font-size: 48px;
  line-height: 67px;
  color: #A9FF00;
  cursor: pointer;
  margin-left: 32px;
  transition: opacity 0.2s;
}
.footer-pay-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.custom-coupon-entry {
  position: fixed;
  left: 0;
  bottom: 174px; /* 位于底部结算栏上方 */
  width: 100%;
  height: 104px;
  background: #EDF2DC;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  padding: 24px 70px;
  gap: 8px;
  cursor: pointer;
  z-index: 15;
}
.coupon-entry-text {
  width: 280px;
  height: 56px;
  font-family: 'PingFang SC', sans-serif;
  font-style: normal;
  font-weight: 400;
  font-size: 40px;
  line-height: 56px;
  color: #647D00;
  display: flex;
  align-items: center;
}
.coupon-entry-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

/* 响应式设计 */
@media (max-width: 375px) {
  .action-buttons {
    padding: 12px;
  }
}

:deep(.van-popup) {
  width: 80vw !important;
  max-width: 480px !important;
  left: 50% !important;
  transform: translateX(-50%) !important;
  height: auto !important;
  min-height: 220px !important;
  max-height: 340px !important;
  top: 50% !important;
  bottom: auto !important;
  transform: translate(-50%, -50%) !important;
}
:deep(.manual-input-bottom-popup.van-popup) {
  width: 100% !important;
  max-width: 100% !important;
  left: 0 !important;
  transform: none !important;
  border-radius: 32px 32px 0 0 !important;
  bottom: 0 !important;
  min-height: 480px;
  box-shadow: 0 -8px 32px rgba(0,0,0,0.08);
}
.manual-input-popup {
  width: 100%;
  max-width: 100%;
  box-sizing: border-box;
  padding: 48px 48px 32px 48px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.manual-input-popup .popup-header {
  width: 100%;
  text-align: center;
  margin-bottom: 48px;
}
.manual-input-popup .popup-header h3 {
  font-size: 56px;
  font-weight: 600;
  margin: 0;
}
.manual-form {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
}
.manual-form .form-footer {
  margin-top: 48px;
}
.manual-form :deep(.van-cell) {
  align-items: center;
  min-height: 136px;
}
.manual-form :deep(.van-field__label) {
  font-size: 40px;
  font-weight: 600;
  width: 240px;
  min-width: 240px;
  max-width: 240px;
  text-align: left;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding-right: 24px;
}
.manual-form :deep(.van-field__control) {
  font-size: 40px;
  height: 136px;
  text-align: left;
  flex: 1 1 0%;
  min-width: 0;
}
.manual-form :deep(.van-button) {
  font-size: 40px;
  height: 120px;
  border-radius: 16px;
  background: #20201E !important;
  color: #A9FF00 !important;
  border: none !important;
}
.manual-input-mask {
  position: fixed;
  left: 0;
  top: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0,0,0,0.45);
  z-index: 1000;
}
.manual-input-bottom-popup--custom {
  position: fixed;
  left: 0;
  bottom: 0;
  width: 100vw;
  max-width: 100vw;
  border-radius: 32px 32px 0 0;
  background: #fff;
  z-index: 1001;
  box-shadow: 0 -8px 32px rgba(0,0,0,0.08);
  min-height: 480px;
  display: flex;
  flex-direction: column;
  align-items: center;
  animation: popup-slide-up 0.25s cubic-bezier(0.4,0,0.2,1);
}
@keyframes popup-slide-up {
  from { transform: translateY(100%); }
  to { transform: translateY(0); }
}
</style> 