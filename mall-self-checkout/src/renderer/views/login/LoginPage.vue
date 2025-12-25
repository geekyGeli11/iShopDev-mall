<template>
  <div class="login-page">
    <!-- 顶部装饰背景 -->
    <div class="login-header">
      <div class="header-bg">
        <div class="header-shapes">
          <div class="shape shape-1"></div>
          <div class="shape shape-2"></div>
          <div class="shape shape-3"></div>
        </div>
      </div>
      
      <!-- Logo区域 -->
      <div class="logo-section">
        <div class="logo-icon">
          <van-icon name="shop-o" size="60" color="#fff" />
        </div>
        <h1 class="app-title">Mall 自助收银</h1>
        <p class="app-subtitle">智能购物，轻松结算</p>
      </div>
    </div>

    <!-- 登录内容区 -->
    <div class="login-content">
      <!-- 模式选择 -->
      <div class="mode-selector">
        <van-tabs v-model:active="activeMode" @click-tab="handleModeChange" swipeable>
          <van-tab title="会员登录" name="member">
            <div class="tab-content">
              <!-- 会员登录表单 -->
              <van-form @submit="handleMemberLogin" class="member-form">
                <div class="form-section">
                  <h3 class="section-title">手机号登录</h3>
                  
                  <van-field
                    v-model="memberForm.phone"
                    type="tel"
                    name="phone"
                    label="手机号"
                    placeholder="请输入手机号"
                    :rules="phoneRules"
                    left-icon="phone-o"
                    maxlength="11"
                    @input="onPhoneInput"
                  />
                  
                  <van-field
                    v-model="memberForm.code"
                    type="digit"
                    name="code"
                    label="验证码"
                    placeholder="请输入验证码"
                    :rules="codeRules"
                    left-icon="shield-o"
                    maxlength="6"
                  >
                    <template #button>
                      <van-button
                        size="small"
                        type="primary"
                        :disabled="!canSendCode || codeCountDown > 0"
                        :loading="isSendingCode"
                        @click="sendCode"
                        class="code-btn"
                      >
                        {{ codeCountDown > 0 ? `${codeCountDown}s` : '获取验证码' }}
                      </van-button>
                    </template>
                  </van-field>
                  
                  <!-- 门店选择 -->
                  <div class="store-selection">
                    <van-cell
                      :title="selectedStore ? `${selectedStore.schoolName} - ${selectedStore.storeName}` : '选择门店'"
                      :value="selectedStore ? '' : '请选择'"
                      is-link
                      @click="showStoreSelection = true"
                      class="store-cell"
                    >
                      <template #icon>
                        <van-icon name="shop-o" color="#1989fa" />
                      </template>
                    </van-cell>
                  </div>

                  <div class="login-actions">
                    <van-button
                      type="primary"
                      size="large"
                      block
                      round
                      :loading="isLogging"
                      :disabled="!selectedStore"
                      native-type="submit"
                      class="login-btn"
                    >
                      会员登录
                    </van-button>
                  </div>
                </div>
              </van-form>
              
              <!-- 会员权益说明 -->
              <div class="member-benefits">
                <h4>会员专享权益</h4>
                <div class="benefit-list">
                  <div class="benefit-item">
                    <van-icon name="gold-coin-o" color="#ffb700" />
                    <span>购物积分奖励</span>
                  </div>
                  <div class="benefit-item">
                    <van-icon name="coupon-o" color="#ff6b6b" />
                    <span>专属优惠券</span>
                  </div>
                  <div class="benefit-item">
                    <van-icon name="gem-o" color="#4ecdc4" />
                    <span>会员折扣价</span>
                  </div>
                </div>
              </div>
            </div>
          </van-tab>
          
          <van-tab title="游客购物" name="guest">
            <div class="tab-content">
              <!-- 游客模式 -->
              <div class="guest-section">
                <div class="guest-icon">
                  <van-icon name="user-o" size="80" color="#969799" />
                </div>
                <h3 class="guest-title">游客模式</h3>
                <p class="guest-desc">无需登录，直接开始购物</p>
                
                <div class="guest-features">
                  <div class="feature-item">
                    <van-icon name="scan" color="#1989fa" />
                    <span>扫码购物</span>
                  </div>
                  <div class="feature-item">
                    <van-icon name="cart-o" color="#1989fa" />
                    <span>购物车管理</span>
                  </div>
                  <div class="feature-item">
                    <van-icon name="credit-pay" color="#1989fa" />
                    <span>便捷支付</span>
                  </div>
                </div>
                
                <!-- 门店选择 -->
                <div class="store-selection">
                  <van-cell
                    :title="selectedStore ? `${selectedStore.schoolName} - ${selectedStore.storeName}` : '选择门店'"
                    :value="selectedStore ? '' : '请选择'"
                    is-link
                    @click="showStoreSelection = true"
                    class="store-cell"
                  >
                    <template #icon>
                      <van-icon name="shop-o" color="#1989fa" />
                    </template>
                  </van-cell>
                </div>

                <van-button
                  type="primary"
                  size="large"
                  block
                  round
                  @click="startGuestShopping"
                  :disabled="!selectedStore"
                  class="guest-btn"
                >
                  开始购物
                </van-button>
                
                <p class="guest-note">
                  <van-icon name="info-o" size="12" />
                  游客模式无法享受会员权益
                </p>
              </div>
            </div>
          </van-tab>
        </van-tabs>
      </div>
    </div>

    <!-- 底部信息 -->
    <div class="login-footer">
      <p class="footer-text">
        使用即表示同意
        <span class="link-text" @click="showPrivacy">《隐私政策》</span>
        和
        <span class="link-text" @click="showTerms">《使用条款》</span>
      </p>
    </div>

    <!-- 隐私政策弹窗 -->
    <van-popup v-model:show="showPrivacyPopup" position="bottom" :style="{ height: '70%' }">
      <div class="popup-content">
        <div class="popup-header">
          <h3>隐私政策</h3>
          <van-icon name="cross" @click="showPrivacyPopup = false" />
        </div>
        <div class="popup-body">
          <p>我们重视您的隐私保护...</p>
          <!-- 隐私政策内容 -->
        </div>
      </div>
    </van-popup>

    <!-- 使用条款弹窗 -->
    <van-popup v-model:show="showTermsPopup" position="bottom" :style="{ height: '70%' }">
      <div class="popup-content">
        <div class="popup-header">
          <h3>使用条款</h3>
          <van-icon name="cross" @click="showTermsPopup = false" />
        </div>
        <div class="popup-body">
          <p>欢迎使用Mall自助收银系统...</p>
          <!-- 使用条款内容 -->
        </div>
      </div>
    </van-popup>

    <!-- 门店选择弹窗 -->
    <StoreSelectionModal
      v-model="showStoreSelection"
      @confirm="handleStoreSelection"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showSuccessToast, showFailToast, showDialog } from 'vant'
import { useMemberStore } from '@/store/modules/member'
import { validatePhone } from '../../utils/validation'
import StoreSelectionModal from '@/components/business/StoreSelectionModal/index.vue'
import type { LoginMode, MemberLoginForm } from '@shared/types'

const router = useRouter()
const memberStore = useMemberStore()

// 响应式数据
const activeMode = ref<LoginMode>('member')
const isLogging = ref(false)
const isSendingCode = ref(false)
const codeCountDown = ref(0)
const showPrivacyPopup = ref(false)
const showTermsPopup = ref(false)

// 表单数据
const memberForm = ref<MemberLoginForm>({
  phone: '',
  code: ''
})

// 验证规则
const phoneRules = [
  { required: true, message: '请输入手机号' },
  { validator: validatePhone, message: '请输入正确的手机号' }
]

const codeRules = [
  { required: true, message: '请输入验证码' },
  { pattern: /^\d{6}$/, message: '验证码格式不正确' }
]

// 计算属性
const canSendCode = computed(() => {
  return validatePhone(memberForm.value.phone) && codeCountDown.value === 0
})

// 定时器
let countDownTimer: number | null = null

// 方法
const handleModeChange = (event: any) => {
  activeMode.value = event.name
}

const onPhoneInput = (value: string) => {
  // 自动格式化手机号
  memberForm.value.phone = value.replace(/\D/g, '')
}

const sendCode = async () => {
  if (!canSendCode.value) return
  
  try {
    isSendingCode.value = true
    
    // 调用发送验证码API
    await memberStore.sendVerifyCode(memberForm.value.phone)
    
    showSuccessToast('验证码已发送')
    startCountDown()
    
  } catch (error) {
    console.error('发送验证码失败:', error)
    showFailToast(error instanceof Error ? error.message : '发送失败，请重试')
  } finally {
    isSendingCode.value = false
  }
}

const startCountDown = () => {
  codeCountDown.value = 60
  countDownTimer = window.setInterval(() => {
    codeCountDown.value--
    if (codeCountDown.value <= 0) {
      clearInterval(countDownTimer!)
      countDownTimer = null
    }
  }, 1000)
}

const handleMemberLogin = async () => {
  try {
    isLogging.value = true
    
    // 验证表单
    if (!validatePhone(memberForm.value.phone)) {
      showToast('请输入正确的手机号')
      return
    }
    
    if (!/^\d{6}$/.test(memberForm.value.code)) {
      showToast('请输入6位验证码')
      return
    }
    
    // 执行登录
    await memberStore.login({
      telephone: memberForm.value.phone,
      verifyCode: memberForm.value.code
    })
    
    showSuccessToast('登录成功')
    
    // 跳转到首页
    await router.replace('/')
    
  } catch (error) {
    console.error('会员登录失败:', error)
    showFailToast(error instanceof Error ? error.message : '登录失败，请重试')
  } finally {
    isLogging.value = false
  }
}

const startGuestShopping = async () => {
  try {
    // 设置游客模式
    await memberStore.setGuestMode()
    
    showSuccessToast('已进入游客模式')
    
    // 跳转到首页
    await router.replace('/')
    
  } catch (error) {
    console.error('进入游客模式失败:', error)
    showFailToast('进入失败，请重试')
  }
}

const showPrivacy = () => {
  showPrivacyPopup.value = true
}

const showTerms = () => {
  showTermsPopup.value = true
}

// 生命周期
onMounted(() => {
  // 检查是否已经登录
  if (memberStore.isLoggedIn) {
    router.replace('/')
  }
})

onUnmounted(() => {
  if (countDownTimer) {
    clearInterval(countDownTimer)
  }
})
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
}

/* 顶部装饰 */
.login-header {
  position: relative;
  padding: 60px 20px 40px;
  text-align: center;
  overflow: hidden;
}

.header-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
}

.header-shapes {
  position: relative;
  width: 100%;
  height: 100%;
}

.shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 80px;
  height: 80px;
  top: 20%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 120px;
  height: 120px;
  top: 50%;
  right: 15%;
  animation-delay: 2s;
}

.shape-3 {
  width: 60px;
  height: 60px;
  bottom: 20%;
  left: 20%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
}

.logo-section {
  position: relative;
  z-index: 1;
}

.logo-icon {
  margin-bottom: 16px;
}

.app-title {
  font-size: 28px;
  font-weight: bold;
  color: #fff;
  margin: 0 0 8px 0;
}

.app-subtitle {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.8);
  margin: 0;
}

/* 登录内容 */
.login-content {
  flex: 1;
  background: #fff;
  border-radius: 24px 24px 0 0;
  padding: 0;
  margin-top: 20px;
}

.mode-selector {
  padding: 20px 20px 0;
}

:deep(.van-tabs__wrap) {
  background: transparent;
}

:deep(.van-tab) {
  font-size: 16px;
  font-weight: 500;
}

.tab-content {
  padding: 24px 0;
}

/* 会员登录表单 */
.member-form {
  margin-bottom: 32px;
}

.form-section {
  margin-bottom: 24px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #323233;
  margin: 0 0 20px 0;
  text-align: center;
}

:deep(.van-field) {
  padding: 16px 0;
  margin-bottom: 12px;
}

:deep(.van-field__label) {
  font-weight: 500;
  color: #323233;
  min-width: 80px;
}

.code-btn {
  font-size: 14px;
  height: 32px;
  padding: 0 12px;
}

.login-actions {
  margin-top: 32px;
}

.login-btn {
  height: 50px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

/* 会员权益 */
.member-benefits {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
  margin-top: 24px;
}

.member-benefits h4 {
  font-size: 16px;
  font-weight: 600;
  color: #323233;
  margin: 0 0 16px 0;
  text-align: center;
}

.benefit-list {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 16px;
}

.benefit-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: 8px;
}

.benefit-item span {
  font-size: 12px;
  color: #646566;
}

/* 游客模式 */
.guest-section {
  text-align: center;
  padding: 32px 0;
}

.guest-icon {
  margin-bottom: 24px;
}

.guest-title {
  font-size: 20px;
  font-weight: 600;
  color: #323233;
  margin: 0 0 8px 0;
}

.guest-desc {
  font-size: 14px;
  color: #969799;
  margin: 0 0 32px 0;
}

.guest-features {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 20px;
  margin-bottom: 40px;
  padding: 0 20px;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.feature-item span {
  font-size: 12px;
  color: #646566;
}

.guest-btn {
  height: 50px;
  font-size: 16px;
  font-weight: 600;
  margin: 0 20px 20px;
}

.guest-note {
  font-size: 12px;
  color: #969799;
  margin: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

/* 底部信息 */
.login-footer {
  padding: 20px;
  text-align: center;
}

.footer-text {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
  margin: 0;
}

.link-text {
  color: #fff;
  text-decoration: underline;
}

/* 弹窗样式 */
.popup-content {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #eee;
}

.popup-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.popup-body {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

/* 响应式设计 */
@media (max-width: 375px) {
  .login-header {
    padding: 40px 16px 32px;
  }
  
  .app-title {
    font-size: 24px;
  }
  
  .login-content {
    margin-top: 16px;
  }
  
  .mode-selector {
    padding: 16px;
  }
  
  .tab-content {
    padding: 20px 0;
  }
}

/* 深色模式适配 */
@media (prefers-color-scheme: dark) {
  .login-content {
    background: #1a1a1a;
  }
  
  .section-title {
    color: #fff;
  }
  
  .member-benefits {
    background: #2a2a2a;
  }
  
  .guest-title {
    color: #fff;
  }
}
</style> 