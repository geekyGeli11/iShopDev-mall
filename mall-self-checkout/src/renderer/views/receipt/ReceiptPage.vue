<template>
  <div class="receipt-page">
    <!-- 顶部栏：简洁设计 -->
    <div class="header-bar">
      <div class="header-left">
        <van-icon name="arrow-left" size="20" @click="goBack" />
        <span class="page-title">电子小票</span>
      </div>
      
      <div class="header-right">
        <van-button size="small" type="primary" @click="goHome">
          <van-icon name="home-o" />
          首页
        </van-button>
      </div>
    </div>

    <!-- 主内容区域：简化的卡片布局 -->
    <div class="receipt-container">
      <!-- 支付成功提示 -->
      <div class="success-banner">
        <van-icon name="checked" color="#07c160" size="32px" />
        <div class="success-text">
          <h3>支付成功</h3>
          <p>感谢您的惠顾！</p>
        </div>
      </div>

      <!-- 订单信息卡片 -->
      <div class="info-card">
        <div class="card-header">
          <van-icon name="bill-o" size="16" />
          <span>订单信息</span>
        </div>
        
        <div class="card-content">
          <div class="info-row">
            <span class="label">订单号：</span>
            <span class="value">{{ orderData?.orderSn || orderNumber }}</span>
          </div>
          <div class="info-row">
            <span class="label">交易时间：</span>
            <span class="value">{{ orderData?.createTime ? formatTime(orderData.createTime) : transactionTime }}</span>
          </div>
          <div class="info-row">
            <span class="label">支付方式：</span>
            <span class="value">{{ getPaymentMethodText() }}</span>
          </div>
          <div class="info-row">
            <span class="label">支付状态：</span>
            <van-tag type="success" size="medium">支付成功</van-tag>
          </div>
        </div>
      </div>

      <!-- 费用汇总卡片 -->
      <div class="info-card">
        <div class="card-header">
          <van-icon name="balance-o" size="16" />
          <span>费用汇总</span>
        </div>
        
        <div class="card-content">
          <div class="summary-row">
            <span class="label">商品总计：</span>
            <span class="value">¥{{ formatPrice(getTotalAmount()) }}</span>
          </div>
          
          <div v-if="getDiscountAmount() > 0" class="summary-row discount">
            <span class="label">优惠折扣：</span>
            <span class="value text-red">-¥{{ formatPrice(getDiscountAmount()) }}</span>
          </div>
          
          <div class="summary-row total">
            <span class="label">实付金额：</span>
            <span class="value amount-highlight">¥{{ formatPrice(getPayAmount()) }}</span>
          </div>
        </div>
      </div>



      <!-- 小票尾部 -->
      <div class="receipt-footer">
        <p class="thank-text">感谢惠顾</p>
        <p class="footer-note">此为电子小票，与纸质小票具有同等效力</p>
      </div>
    </div>

    <!-- 底部操作按钮 -->
    <div class="action-footer">
      <van-button 
        type="default" 
        size="large"
        @click="printReceipt"
        class="action-btn"
      >
        <van-icon name="print" />
        打印小票
      </van-button>
      
      <van-button 
        type="primary" 
        size="large"
        @click="newTransaction"
        class="action-btn"
      >
        <van-icon name="shop-o" />
        继续购物
      </van-button>
    </div>

    <!-- 加载状态 -->
    <van-loading v-if="loading" type="spinner" color="#1989fa" vertical>
      加载订单信息...
    </van-loading>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { OrderAPI } from '@/api/modules/order'
import { showToast } from 'vant'
import { useMemberStore } from '@/store/modules/member'
import type { OrderVO, OrderItemVO } from '@shared/types'

const router = useRouter()
const route = useRoute()
const memberStore = useMemberStore()

// 响应式数据
const loading = ref(false)
const orderData = ref<OrderVO | null>(null)
const orderNumber = ref('')
const transactionTime = ref('')

// 工具函数
const getCachedPaymentData = () => {
  const savedData = localStorage.getItem('payment_data')
  return savedData ? JSON.parse(savedData) : null
}

const getTotalAmount = () => {
  if (orderData.value && orderData.value.totalAmount !== undefined) {
    return orderData.value.totalAmount
  }
  // 降级处理：从缓存数据计算
  const savedData = getCachedPaymentData()
  if (savedData && savedData.items) {
    return savedData.items.reduce((total: number, item: any) => {
      const price = item.productPrice || item.price || 0
      const quantity = item.productQuantity || item.quantity || 0
      return total + (price * quantity)
    }, 0)
  }
  return 0
}

const getDiscountAmount = () => {
  if (orderData.value) {
    const totalAmount = orderData.value.totalAmount || 0
    const payAmount = orderData.value.payAmount || 0
    return totalAmount - payAmount
  }
  const savedData = getCachedPaymentData()
  return savedData?.coupon?.value || 0
}

const getPayAmount = () => {
  if (orderData.value && orderData.value.payAmount !== undefined) {
    return orderData.value.payAmount
  }
  // 降级处理：计算最终金额
  return getTotalAmount() - getDiscountAmount()
}

const getPaymentMethodText = () => {
  if (orderData.value) {
    // 使用全局映射标准：0-未支付, 1-支付宝, 2-微信
    // 后端payType是字符串类型，需要进行字符串比较或转换
    const payType = String(orderData.value.payType)
    switch (payType) {
      case '0': return '未支付'
      case '1': return '支付宝'
      case '2': return '微信支付'
      default: return '扫码支付'
    }
  }
  const savedData = getCachedPaymentData()
  return savedData?.memberMode ? '会员储值卡' : '扫码支付'
}

const formatPrice = (price: number | string) => {
  const num = typeof price === 'string' ? parseFloat(price) : price
  return isNaN(num) ? '0.00' : num.toFixed(2)
}

const formatTime = (time: string | Date) => {
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

// 主要业务逻辑
const loadOrderDetail = async () => {
  try {
    loading.value = true
    
    // 尝试从路由参数获取订单ID
    const orderId = route.query.orderId as string
    
    if (orderId) {
      console.log('从后端获取订单详情, orderId:', orderId)
      const response = await OrderAPI.getOrderDetail(parseInt(orderId))
      
      if (response.code === 200 && response.data) {
        orderData.value = response.data
        orderNumber.value = response.data.orderSn
        transactionTime.value = formatTime(response.data.createTime)
        
        console.log('成功加载订单详情:', response.data)
      } else {
        throw new Error(response.message || '获取订单详情失败')
      }
    } else {
      // 从localStorage获取缓存数据
      console.log('使用缓存数据初始化小票')
      loadCachedData()
    }
    
  } catch (error) {
    console.error('初始化小票数据失败:', error)
    showToast('加载订单信息失败，使用缓存数据')
    // 降级到缓存数据
    loadCachedData()
  } finally {
    loading.value = false
  }
}

const loadCachedData = () => {
  // 生成订单号
  orderNumber.value = 'SC' + Date.now().toString().slice(-8)
  
  // 设置交易时间
  transactionTime.value = new Date().toLocaleString('zh-CN')
}



// 导航方法
const goBack = () => {
  router.go(-1)
}

const goHome = () => {
  // 清理所有购物数据
  localStorage.removeItem('payment_data')
  localStorage.removeItem('checkout_mode')
  localStorage.removeItem('member_phone')
  localStorage.removeItem('member_code')
  
  // 如果用户已登录，退出登录
  if (memberStore.isLoggedIn) {
    memberStore.performLogout()
  }
  
  router.push('/')
}

const newTransaction = () => {
  // 清理购物数据但保留会员登录状态
  localStorage.removeItem('payment_data')
  router.push('/scan')
}

const printReceipt = () => {
  // 模拟打印功能
  if (window.print) {
    window.print()
  } else {
    showToast('打印功能暂不可用')
  }
}

// 生命周期
onMounted(() => {
  loadOrderDetail()
})
</script>

<style scoped lang="scss">
.receipt-page {
  width: 100%;
  height: 100%;
  max-width: 100vw;
  max-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  background: #f5f5f5;
  position: relative;
}

/* ===== 顶部导航栏：简洁设计 ===== */
.header-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: #4a90e2;
  color: white;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 12px;
    
    .page-title {
      font-size: 16px;
      font-weight: 600;
    }
  }
  
  .header-right .van-button {
    background: rgba(255,255,255,0.2);
    border: 1px solid rgba(255,255,255,0.3);
    color: white;
    
    &:hover {
      background: rgba(255,255,255,0.3);
    }
  }
}

/* ===== 主内容区域 ===== */
.receipt-container {
  flex: 1;
  padding: 16px;
  width: 92%;
  max-width: 520px;
  margin: 0 auto;
  width: 100%;
}

/* ===== 成功提示 ===== */
.success-banner {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 20px;
  margin-bottom: 16px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  
  .success-text {
    text-align: center;
    
    h3 {
      margin: 0 0 4px 0;
      font-size: 18px;
      color: #07c160;
      font-weight: 600;
    }
    
    p {
      margin: 0;
      color: #666;
      font-size: 14px;
    }
  }
}

/* ===== 信息卡片：统一样式 ===== */
.info-card {
  background: white;
  border-radius: 12px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  overflow: hidden;
  
  .card-header {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 16px;
    background: #f8f9fa;
    border-bottom: 1px solid #eee;
    font-weight: 600;
    color: #333;
    font-size: 14px;
  }
  
  .card-content {
    padding: 16px;
  }
}

/* ===== 信息行样式 ===== */
.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f5f5f5;
  
  &:last-child {
    border-bottom: none;
  }
  
  .label {
    color: #666;
    font-size: 14px;
  }
  
  .value {
    color: #333;
    font-weight: 500;
    font-size: 14px;
  }
}

/* ===== 汇总行样式 ===== */
.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  font-size: 14px;
  
  &.discount .value {
    color: #ff6b35;
  }
  
  &.total {
    border-top: 2px solid #f0f0f0;
    margin-top: 8px;
    padding-top: 12px;
    font-size: 16px;
    font-weight: 600;
    
    .label {
      color: #333;
    }
    
    .value.amount-highlight {
      color: #ff6b35;
      font-size: 18px;
    }
  }
}

/* ===== 小票尾部 ===== */
.receipt-footer {
  text-align: center;
  padding: 20px;
  color: #666;
  
  .thank-text {
    font-size: 16px;
    font-weight: 600;
    color: #333;
    margin-bottom: 8px;
  }
  
  .footer-note {
    font-size: 12px;
    margin: 0;
  }
}

/* ===== 底部操作区域 ===== */
.action-footer {
  width: 100%;
  display: flex;
  justify-content: center;
  gap: 24px;
  margin-top: 24px;
}

.action-btn {
  width: 40vw;
  max-width: 200px;
}

/* ===== 工具类 ===== */
.text-red {
  color: #ff6b35 !important;
}

/* ===== 加载状态 ===== */
.van-loading {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1000;
}

/* ===== 响应式设计 ===== */
@media (max-width: 768px) {
  .receipt-container {
    padding: 12px;
  }
  
  .header-bar {
    padding: 10px 12px;
    
    .header-left .page-title {
      font-size: 15px;
    }
  }
  
  .success-banner {
    padding: 16px;
    
    .success-text h3 {
      font-size: 16px;
    }
  }
  
  .action-footer {
    padding: 12px;
    
    .action-btn {
      height: 44px;
      font-size: 15px;
    }
  }
}

:deep(.van-popup) {
  width: 80vw !important;
  max-width: 480px !important;
  left: 50% !important;
  transform: translateX(-50%) !important;
}
</style> 