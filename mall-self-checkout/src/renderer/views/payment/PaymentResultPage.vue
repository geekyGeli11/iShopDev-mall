<template>
  <div class="payment-result-page">
    <!-- 顶部标题区域，复用 HomePage -->
    <div class="welcome-header">
      <img
        src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/mall-selfcheck/assets/images/login/welcome_text.png"
        class="header-left-img" alt="欢迎文本" />
      <img
        src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/mall-selfcheck/assets/images/login/guanghengzou.png"
        class="header-right-img" alt="广横走标志" />
    </div>
    <div class="result-content">
      <div class="result-icon-box">
        <div class="result-icon-bg">
          <img class="result-icon"
            src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/mall-selfcheck/assets/images/payment/paysuccess.png"
            alt="支付成功" />
        </div>
      </div>
      <div class="result-text-container">
        <h2 class="result-title">支付成功</h2>
        <div class="result-subtitle">支付成功，订单已完成</div>
      </div>
      <div class="order-summary">
        <div class="summary-item">
          <span class="label">订单号</span>
          <span class="value">{{ orderSn }}</span>
        </div>
        <div class="summary-item">
          <span class="label">支付金额</span>
          <span class="value">￥{{ amount }}</span>
        </div>
      </div>
      <div class="result-actions">
        <!-- <button class="receipt-btn" @click="goToReceipt">查看小票</button> -->
        <button class="home-btn" @click="goHome">返回首页</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'
import { onMounted, ref } from 'vue'

const router = useRouter()
const route = useRoute()

const orderSn = ref('')
const amount = ref('')

onMounted(() => {
  orderSn.value = route.query.orderSn as string || ''
  amount.value = route.query.amount as string || ''
})

const goToReceipt = () => {
  if (orderSn.value) {
    router.replace({ path: '/receipt', query: { orderSn: orderSn.value } })
  }
}
const goHome = () => {
  router.replace('/')
}
</script>

<style scoped lang="scss">
.payment-result-page {
  width: 100vw;
  min-height: 100vh;
  background: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.welcome-header {
  width: 100vw;
  height: 358px;
  background-color: #a9ff00;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 48px;
  box-sizing: border-box;
  position: relative;
  z-index: 1;
}

.header-left-img {
  height: 191px;
}

.header-right-img {
  height: 175px;
}

.result-content {
  width: 100%;
  max-width: 1000px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 150px;
}

.result-icon-box {
  margin-bottom: 32px;
}

.result-icon-bg {
  width: 200px;
  height: 200px;
  border-radius: 24px;
  display: flex;
  align-items: center;
  justify-content: center;

  .result-icon {
    width: 100%;
    height: 100%;
  }
}

.result-title {
  font-size: 48px;
  font-weight: 700;
  color: #222;
  margin: 32px 0 8px 0;
  text-align: center;
}

.result-subtitle {
  font-size: 28px;
  color: #666;
  margin-bottom: 32px;
  text-align: center;
}

.order-summary {
  width: 80%;
  background: #f8f9fa;
  border-radius: 16px;
  padding: 40px 40px;
  margin-bottom: 48px;
  font-size: 32px;
  color: #222;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.label {
  color: #888;
  font-size: 32px;
}

.value {
  color: #222;
  font-size: 32px;
  font-weight: 600;
}

.result-actions {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 24px;
  margin-top: 100px;
  align-items: center;
}

.receipt-btn {
  width: 80%;
  height: 100px;
  background: #20201E;
  color: #A9FF00;
  font-size: 36px;
  border-radius: 16px;
  border: none;
  margin-bottom: 16px;
  cursor: pointer;
}

.home-btn {
  width: 80%;
  height: 100px;
  background: #20201E;
  color: #A9FF00;
  font-size: 36px;
  border-radius: 16px;
  border: 2px solid #eee;
  cursor: pointer;
}
</style>