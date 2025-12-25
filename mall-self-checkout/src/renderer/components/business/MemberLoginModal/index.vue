<template>
  <div>
    <!-- 会员登录选择弹窗 -->
    <div v-if="props.modelValue" :class="{ 'active': props.modelValue }" class="modal-overlay" @click="handleClose">
      <div class="modal-content" :class="{ 'active': props.modelValue }" @click.stop>
        <div class="modal-header">
          <h3 style="font-size: 64px;font-weight: 600;">会员登录</h3>
          <button class="close-btn" @click="handleClose">×</button>
        </div>

        <div class="login-options">
          <button class="login-option" @click="showPhoneLogin = true; emit('update:modelValue', false)">
            <div class="option-icon">
              <img
                src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/mall-selfcheck/assets/images/login/phone_login.png"
                alt="手机号登录"
                style="width: 130px; height: 130px"
              />
            </div>
            <div class="option-text">
              <div class="option-title">手机号登录</div>
              <div class="option-desc">使用手机号和验证码登录</div>
            </div>
          </button>

          <button class="login-option" @click="startMemberCodeScan">
            <div class="option-icon">
              <img
                src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/mall-selfcheck/assets/images/login/memberCode_login.png"
                alt="会员码登录"
                style="width: 130px; height: 130px"
              />
            </div>
            <div class="option-text">
              <div class="option-title">会员码登录</div>
              <div class="option-desc">扫描会员二维码快速登录</div>
            </div>
          </button>
        </div>
      </div>
    </div>

    <!-- 手机号登录弹窗 -->
    <div v-if="showPhoneLogin" :class="{ 'active': showPhoneLogin }" class="modal-overlay" @click="closePhoneLogin">
      <div class="modal-content" :class="{ 'active': showPhoneLogin }" @click.stop>
        <div class="modal-header">
          <h3 style="font-size: 64px;font-weight: 600;">手机号登录</h3>
          <button class="close-btn" @click="closePhoneLogin">×</button>
        </div>

        <form @submit.prevent="handlePhoneLogin" class="login-form">
          <div class="form-group">
            <label style="font-size: 40px;font-weight: 600;">手机号</label>
            <div class="input-with-keyboard" @click="showPhoneKeyboard">
              <input
                v-model="loginForm.phone"
                style="font-size: 40px; height: 136px;"
                type="tel"
                placeholder="请输入手机号"
                maxlength="11"
                readonly
                required
              />
            </div>
          </div>

          <div class="form-group">
            <label style="font-size: 40px;font-weight: 600;">验证码</label>
            <div class="code-input-group">
              <div class="input-with-keyboard" @click="showVerifyCodeKeyboard" style="flex: 2;">
                <input
                  v-model="loginForm.verifyCode"
                  style="font-size: 40px; height: 136px;"
                  type="text"
                  placeholder="请输入验证码"
                  maxlength="6"
                  readonly
                  required
                />
              </div>
              <button
                style="font-size: 40px; height: 136px; flex: 1; color: #0A0D05;"
                type="button"
                class="send-code-btn"
                :disabled="!canSendSms || smsCountdown > 0"
                @click="sendSmsCode"
              >
                {{ smsCountdown > 0 ? `${smsCountdown}s` : '获取验证码' }}
              </button>
            </div>
          </div>

          <button type="submit" class="submit-btn" :disabled="loggingIn">
            {{ loggingIn ? '登录中...' : '立即登录' }}
          </button>
        </form>
      </div>
    </div>

    <!-- 会员码扫描弹窗 -->
    <div
      v-if="showMemberCodeLogin" :class="{ 'active': showMemberCodeLogin }"
      class="modal-overlay"
      @click="closeMemberCodeLogin"
    >
      <div class="modal-content" :class="{ 'active': showMemberCodeLogin }" @click.stop>
        <div class="modal-header">
          <h3 style="font-size: 64px;font-weight: 600;">会员码登录</h3>
          <button class="close-btn" @click="closeMemberCodeLogin">×</button>
        </div>

        <div class="scan-area">
          <div
            class="scan-placeholder"
            :class="{ scanning: scanningMemberCode }"
          >
            <div class="scan-icon">{{ scanningMemberCode ? '🔍' : '📷' }}</div>
            <p style="font-size: 40px; font-weight: 400;" v-if="scanningMemberCode">正在等待扫描会员码...</p>
            <p style="font-size: 40px; font-weight: 400;" v-else>请扫描您的会员二维码</p>
            <p style="font-size: 40px; font-weight: 400;" class="scan-tip">将会员码对准扫描区域</p>         
          </div>

          <div class="scan-actions">
            <button
              v-if="!scanningMemberCode"
              class="start-scan-btn"
              @click="startMemberCodeScanListener"
            >
              开始扫描
            </button>

            <button
              v-if="scanningMemberCode"
              class="stop-scan-btn"
              @click="stopMemberCodeScanListener"
            >
              停止扫描
            </button>

            <button class="manual-input-btn" @click="showManualInput = true">
              手动输入会员码
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 手动输入会员码弹窗 -->
    <div v-if="showManualInput" :class="{ 'active': showManualInput }" class="modal-overlay" @click="closeManualInput">
      <div class="modal-content" :class="{ 'active': showManualInput }" @click.stop>
        <div class="modal-header">
          <h3 style="font-size: 64px;font-weight: 600;">输入会员码</h3>
          <button class="close-btn" @click="closeManualInput">×</button>
        </div>

        <form @submit.prevent="handleMemberCodeLogin" class="login-form">
          <div class="form-group">
            <label style="font-size: 40px;font-weight: 600;">会员码</label>
            <div class="input-with-keyboard" @click="showMemberCodeKeyboard">
              <input
                v-model="memberCodeForm.memberCode"
                style="font-size: 40px; height: 136px;"
                type="text"
                placeholder="请输入会员码"
                maxlength="20"
                readonly
                required
              />
            </div>
          </div>

          <button type="submit" class="submit-btn" :disabled="loggingIn">
            {{ loggingIn ? '登录中...' : '确认登录' }}
          </button>
        </form>
      </div>
    </div>

    <!-- 消息提示 -->
    <div v-if="message" :class="['message-toast', messageType]">
      {{ message }}
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
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onUnmounted } from 'vue'
import { useMemberStore } from '@/store/modules/member'
import NumericKeyboard from '@/components/common/NumericKeyboard/index.vue'
import { scanManager } from '@/utils/scanManager'
import { debugLogger, type ScanLogData } from '../../../utils/debugLogger'
import StoreManager from '@/utils/storeManager'

interface Props {
  modelValue: boolean
  redirectAfterLogin?: boolean
}

interface Emits {
  'update:modelValue': [value: boolean]
  loginSuccess: [memberInfo: any]
}

const props = withDefaults(defineProps<Props>(), {
  redirectAfterLogin: false,
})

const emit = defineEmits<Emits>()

const memberStore = useMemberStore()

// 响应式数据
const showPhoneLogin = ref(false)
const showMemberCodeLogin = ref(false)
const showManualInput = ref(false)
const loggingIn = ref(false)
const smsCountdown = ref(0)
const message = ref('')
const messageType = ref('info')

// 扫码相关状态
const scanningMemberCode = ref(false)
const scannedMemberCode = ref('')
let memberCodeScanListenerAdded = ref(false)

// 表单数据
const loginForm = ref({
  phone: '',
  verifyCode: '',
})

const memberCodeForm = ref({
  memberCode: '',
})

// 数字键盘相关状态
const keyboardVisible = ref(false)
const currentKeyboardValue = ref('')
const keyboardTitle = ref('')
const keyboardMaxLength = ref(11)
const keyboardType = ref<'numeric' | 'phone' | 'memberCode'>('numeric')
const currentInputField = ref<'phone' | 'verifyCode' | 'memberCode' | ''>('')

// 计算属性
const showMemberLogin = computed({
  get: () => props.modelValue,
  set: (value: boolean) => emit('update:modelValue', value),
})

const canSendSms = computed(() => {
  return /^1[3-9]\d{9}$/.test(loginForm.value.phone)
})

// 消息提示
const showMessage = (msg: string, type: string = 'info') => {
  message.value = msg
  messageType.value = type
  setTimeout(() => {
    message.value = ''
  }, 3000)
}

// 关闭弹窗
const handleClose = () => {
  console.log('🔄 会员登录弹窗关闭')

  // 停止扫码监听
  stopMemberCodeScanListener()

  // 通知父组件关闭弹窗
  emit('update:modelValue', false)

  // 重置表单
  resetForms()

  // 调试日志 - 弹窗关闭
  debugLogger.logScan(
    '会员登录弹窗关闭',
    '会员登录弹窗已关闭，应该恢复商品扫码',
    {
      scanType: 'member',
      status: 'success',
      result: { action: 'modal_closed' }
    } as ScanLogData
  )
}

const closePhoneLogin = () => {
  showPhoneLogin.value = false
  showMemberLogin.value = true
}

const closeMemberCodeLogin = () => {
  // 停止扫码监听
  stopMemberCodeScanListener()
  showMemberCodeLogin.value = false
  showMemberLogin.value = true
}

const closeManualInput = () => {
  showManualInput.value = false
  showMemberCodeLogin.value = true
}

// 重置表单
const resetForms = () => {
  loginForm.value = { phone: '', verifyCode: '' }
  memberCodeForm.value = { memberCode: '' }
  showPhoneLogin.value = false
  showMemberCodeLogin.value = false
  showManualInput.value = false
  smsCountdown.value = 0
  // 重置键盘状态
  keyboardVisible.value = false
  currentInputField.value = ''
  currentKeyboardValue.value = ''
  // 重置扫码状态
  scanningMemberCode.value = false
  scannedMemberCode.value = ''
}

// 🚀 新增：开始会员码扫描
const startMemberCodeScan = () => {
  showMemberCodeLogin.value = true
  showMemberLogin.value = false

  // 自动开始扫描监听
  setTimeout(() => {
    startMemberCodeScanListener()
  }, 300) // 等待弹窗动画完成
}

// 🚀 新增：启动会员码扫描监听
const startMemberCodeScanListener = () => {
  if (memberCodeScanListenerAdded.value) return

  scanningMemberCode.value = true
  scannedMemberCode.value = ''

  // 调试日志 - 会员码扫描开始
  debugLogger.logScan(
    '会员码扫描监听启动',
    '开始监听会员码扫描输入',
    {
      scanType: 'member',
      status: 'start'
    } as ScanLogData
  )

  // 注册并激活会员码扫码监听器
  scanManager.register('member', handleMemberCodeScanInput)
  scanManager.activate('member')
  memberCodeScanListenerAdded.value = true

  console.log('🔍 会员码扫描监听已启动，等待扫码...')
  showMessage('请使用扫码设备扫描会员码', 'info')
}

// 🚀 新增：处理会员码扫描输入
const handleMemberCodeScanInput = (event: KeyboardEvent) => {
  // 如果不在扫描状态，忽略输入
  if (!scanningMemberCode.value || !showMemberCodeLogin.value) return

  // 防止与其他输入框冲突
  if (event.target && (event.target as HTMLElement).tagName === 'INPUT') {
    return
  }

  // 调试日志 - 会员码扫码键盘事件
  debugLogger.logScan(
    '会员码扫码键盘事件',
    `接收到按键: ${event.key}, 当前累积: ${scannedMemberCode.value}`,
    {
      scanType: 'member',
      status: 'progress',
      result: { key: event.key, accumulated: scannedMemberCode.value }
    } as ScanLogData
  )

  console.log('🎯 会员码扫码事件:', event.key, '当前累积:', scannedMemberCode.value)

  // Enter键表示扫码完成
  if (event.key === 'Enter') {
    if (scannedMemberCode.value.length > 0) {
      console.log('✅ 会员码扫码完成:', scannedMemberCode.value)

      // 调试日志 - 会员码扫码完成
      debugLogger.logScan(
        '会员码扫码输入完成',
        `扫码器输入完成，会员码: ${scannedMemberCode.value}`,
        {
          scanType: 'member',
          memberCode: scannedMemberCode.value,
          status: 'success',
          result: { finalMemberCode: scannedMemberCode.value, inputLength: scannedMemberCode.value.length }
        } as ScanLogData
      )

      // 处理扫到的会员码
      processMemberCodeScan(scannedMemberCode.value)
      scannedMemberCode.value = '' // 清空缓存
    }
    return
  }

  // 忽略特殊键和控制键
  if (event.key.length > 1) {
    console.log('⏭️ 忽略特殊键:', event.key)

    // 调试日志 - 忽略特殊键
    debugLogger.logScan(
      '会员码扫码忽略按键',
      `忽略特殊键: ${event.key}`,
      {
        scanType: 'member',
        status: 'progress',
        result: { ignoredKey: event.key, reason: 'special_key' }
      } as ScanLogData
    )

    return
  }

  // 累积扫码数据
  scannedMemberCode.value += event.key
  console.log('📝 累积会员码数据:', scannedMemberCode.value)

  // 防止输入过长，会员码最长15位
  if (scannedMemberCode.value.length > 20) {
    console.log('⚠️ 会员码过长，重置')

    // 调试日志 - 会员码过长重置
    debugLogger.logScan(
      '会员码输入过长重置',
      `会员码输入超过20位，自动重置`,
      {
        scanType: 'member',
        status: 'error',
        error: '输入过长',
        result: { resetLength: scannedMemberCode.value.length }
      } as ScanLogData
    )

    scannedMemberCode.value = ''
  }

  // 阻止默认行为
  event.preventDefault()
}

// 🚀 新增：处理扫描到的会员码
const processMemberCodeScan = async (scannedData: string) => {
  console.log('🔍 扫描到原始数据:', scannedData)
  console.log('🔍 数据类型:', typeof scannedData)
  console.log('🔍 数据长度:', scannedData.length)

  let memberCode = ''

  // 直接使用扫描到的数据作为会员码（不再解析JSON）
  memberCode = scannedData.trim()
  console.log('📝 会员码:', memberCode)

  // 基本验证：不能为空
  if (!memberCode) {
    console.log('❌ 会员码为空')
    showMessage('扫描到的会员码为空，请重新扫描', 'error')
    return
  }

  console.log('🚀 最终会员码:', memberCode)
  showMessage('会员码识别成功，正在登录...', 'success')

  // 停止扫描监听
  stopMemberCodeScanListener()

  // 设置到表单中
  memberCodeForm.value.memberCode = memberCode
  console.log('📝 设置到表单:', memberCodeForm.value.memberCode)

  // 自动执行登录（不再传递memberId）
  await handleMemberCodeLogin()
}

// 🚀 新增：停止会员码扫描监听
const stopMemberCodeScanListener = () => {
  if (memberCodeScanListenerAdded.value) {
    scanManager.deactivate('member')
    scanManager.unregister('member')
    memberCodeScanListenerAdded.value = false
    scanningMemberCode.value = false
    scannedMemberCode.value = ''
    console.log('🛑 会员码扫描监听已停止')
  }
}

// 显示手机号键盘
const showPhoneKeyboard = () => {
  currentInputField.value = 'phone'
  currentKeyboardValue.value = loginForm.value.phone
  keyboardTitle.value = '请输入手机号'
  keyboardMaxLength.value = 11
  keyboardType.value = 'phone'
  keyboardVisible.value = true
}

// 显示验证码键盘
const showVerifyCodeKeyboard = () => {
  if (!canSendSms.value) {
    showMessage('请先输入正确的手机号', 'error')
    return
  }

  currentInputField.value = 'verifyCode'
  currentKeyboardValue.value = loginForm.value.verifyCode
  keyboardTitle.value = '请输入验证码'
  keyboardMaxLength.value = 6
  keyboardType.value = 'numeric'
  keyboardVisible.value = true
}

// 显示会员码键盘
const showMemberCodeKeyboard = () => {
  currentInputField.value = 'memberCode'
  currentKeyboardValue.value = memberCodeForm.value.memberCode
  keyboardTitle.value = '请输入会员码'
  keyboardMaxLength.value = 20
  keyboardType.value = 'memberCode'
  keyboardVisible.value = true
}

// 处理键盘确认
const handleKeyboardConfirm = (value: string) => {
  switch (currentInputField.value) {
    case 'phone':
      loginForm.value.phone = value
      break
    case 'verifyCode':
      loginForm.value.verifyCode = value
      break
    case 'memberCode':
      memberCodeForm.value.memberCode = value
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

// 发送验证码
const sendSmsCode = async () => {
  if (!loginForm.value.phone) {
    showMessage('请输入手机号', 'error')
    return
  }

  if (smsCountdown.value > 0) {
    return
  }

  try {
    await memberStore.sendVerifyCode(loginForm.value.phone)
    showMessage('验证码已发送', 'success')

    // 开始倒计时
    smsCountdown.value = 60
    const timer = setInterval(() => {
      smsCountdown.value--
      if (smsCountdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  } catch (error: any) {
    console.error('发送验证码失败:', error)
    showMessage(error.message || '发送失败，请重试', 'error')
  }
}

// 手机号登录
const handlePhoneLogin = async () => {
  console.log('🚀 开始手机号登录流程')
  console.log('🔍 表单数据:', loginForm.value)
  console.log('🔍 phone 类型:', typeof loginForm.value.phone)
  console.log('🔍 verifyCode 类型:', typeof loginForm.value.verifyCode)

  if (!loginForm.value.phone || !loginForm.value.verifyCode) {
    console.log('❌ 表单信息不完整')
    showMessage('请填写完整信息', 'error')
    return
  }

  loggingIn.value = true

  try {
    // 获取门店信息（使用StoreManager）
    const storeParams = StoreManager.getStoreParams()

    const loginParams = {
      telephone: String(loginForm.value.phone || ''),
      verifyCode: String(loginForm.value.verifyCode || ''),
      ...storeParams
    }

    console.log('📤 发送登录参数（含门店信息）:', loginParams)
    console.log('📤 telephone 类型:', typeof loginParams.telephone)
    console.log('📤 verifyCode 类型:', typeof loginParams.verifyCode)

    await memberStore.login(loginParams)

    showMessage('登录成功，享受会员专属权益', 'success')

    // 触发登录成功事件
    emit('loginSuccess', memberStore.memberInfo)

    // 关闭弹窗
    setTimeout(() => {
      handleClose()
    }, 500) // 减少延迟时间，确保商品扫码能够快速恢复
  } catch (error: any) {
    console.error('❌ 手机号登录失败:', error)
    showMessage(error.message || '登录失败，请重试', 'error')
  } finally {
    loggingIn.value = false
  }
}

// 会员码登录
const handleMemberCodeLogin = async () => {
  console.log('🚀 开始会员码登录流程')
  console.log('🔍 表单中的会员码:', memberCodeForm.value.memberCode)

  if (!memberCodeForm.value.memberCode) {
    console.log('❌ 表单中会员码为空')
    showMessage('请输入会员码', 'error')
    return
  }

  // 简单处理会员码格式，去除空格并转大写
  let memberCode = memberCodeForm.value.memberCode.trim().toUpperCase()
  console.log('🔄 处理后的会员码:', memberCode)

  // 基本验证：不能为空
  if (!memberCode) {
    console.log('❌ 处理后会员码为空')
    showMessage('会员码不能为空', 'error')
    return
  }

  loggingIn.value = true

  try {
    // 获取门店信息（使用StoreManager）
    const storeParams = StoreManager.getStoreParams()

    // 构建登录参数
    const loginParams = {
      memberCode: memberCode,
      loginType: 'code',
      ...storeParams
    }

    console.log('📤 发送登录请求（含门店信息），参数:', loginParams)
    await memberStore.loginByCode(loginParams)

    showMessage('登录成功，享受会员专属权益', 'success')

    // 触发登录成功事件
    emit('loginSuccess', memberStore.memberInfo)

    // 立即关闭弹窗，确保商品扫码能够快速恢复
    setTimeout(() => {
      handleClose()
    }, 500) // 减少延迟时间，从1500ms改为500ms
  } catch (error: any) {
    console.error('❌ 登录失败:', error)
    showMessage(error.message || '登录失败，请重试', 'error')
  } finally {
    loggingIn.value = false
  }
}

// 组件卸载时清理监听器
onUnmounted(() => {
  stopMemberCodeScanListener()
})
</script>

<style scoped lang="scss">
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7);
  z-index: 1000;
  padding: 20px;
  opacity: 0;
  visibility: hidden;
  transition: opacity 0.3s ease, visibility 0.3s ease;
}

.modal-overlay.active {
  opacity: 1;
  visibility: visible;
}

.modal-content {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  transform: translateY(100%);
  transition: transform 0.3s ease-out;
  border-radius: 24px 24px 0 0;
  background: white;
  max-height: 85vh;
  overflow-y: auto;
  box-shadow: 0 -8px 32px rgba(0, 0, 0, 0.2);
  z-index: 1001;
  padding-bottom: 40px;
}

.modal-content.active {
  transform: translateY(0);
}

.modal-header {
  padding: 32px 32px 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  border-bottom: 1px solid #f0f0f0;

  h3 {
    margin: 0;
    font-size: 64px;
    font-weight: 600;
    color: #333;
    text-align: center;
  }

  .close-btn {
    position: absolute;
    right: 32px;
    top: 32px;
    background: #f8f9fa;
    border: none;
    font-size: 48px;
    color: #666;
    cursor: pointer;
    padding: 0;
    width: 64px;
    height: 64px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    transition: all 0.2s;

    &:hover {
      background: #e9ecef;
      color: #333;
      transform: scale(1.05);
    }
  }
}

.login-options {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.login-option {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #e9ecef;
    border-color: #999;
  }

  .option-icon {
    font-size: 24px;
  }

  .option-text {
    flex: 1;
    text-align: left;

    .option-title {
      font-size: 40px;
      font-weight: 600;
      color: #000;
      margin-bottom: 4px;
    }

    .option-desc {
      font-size: 32px;
      color: #999;
    }
  }
}

.login-form {
  padding: 20px;
}

.form-group {
  margin-bottom: 20px;

  label {
    display: block;
    margin-bottom: 8px;
    font-weight: 600;
    color: #333;
    font-size: 14px;
  }

  input {
    width: 100%;
    padding: 12px 16px;
    border: 1px solid #e9ecef;
    border-radius: 8px;
    font-size: 16px;
    transition: border-color 0.2s;
    box-sizing: border-box;

    &:focus {
      outline: none;
      border-color: #999;
    }
  }
}

.input-with-keyboard {
  position: relative;
  cursor: pointer;

  input {
    cursor: pointer;
    padding-right: 48px;
    height: 136px;
    background:#fff;
    border: 1px solid #CCCCCC;
    &:read-only {
      background: #f8f9fa;
      color: #333;
    }
  }

  .keyboard-icon {
    position: absolute;
    right: 12px;
    top: 50%;
    transform: translateY(-50%);
    font-size: 18px;
    color: #666;
    pointer-events: none;
  }

  &:hover {
    input {
      border-color: #007bff;
    }

    .keyboard-icon {
      color: #007bff;
    }
  }
}

.code-input-group {
  display: flex;
  gap: 8px;

  input {
    flex: 1;
  }

  .send-code-btn {
  background-color: #A9FF0080;
  color: #0A0D05;
  border: none;
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s;
  white-space: nowrap;

  &:hover:not(:disabled) {
    background-color: #555;
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}
}

.submit-btn {
  width: 100%;
  margin-top: 40px;
  background: #20201E;
  height: 136px;
  color: #A9FF00;
  border: none;
  padding: 16px;
  border-radius: 16px;
  font-size: 48px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;

  &:hover:not(:disabled) {
    background: #555;
    transform: translateY(-1px);
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}

.scan-area {
  padding: 40px;
  text-align: center;

  .scan-placeholder {
    padding: 60px 40px;
    border: 3px dashed #e9ecef;
    border-radius: 20px;
    margin-bottom: 40px;
    background: #fafafa;
    transition: all 0.3s ease;

    &.scanning {
      border-color: #28a745;
      background: #f8fff9;

      .scan-icon {
        animation: pulse 2s infinite;
      }
    }

    .scan-icon {
      font-size: 80px;
      margin-bottom: 24px;
      display: block;
    }

    p {
      margin: 16px 0;
      color: #333;
      line-height: 1.4;
    }

    .scan-tip {
      font-size: 32px;
      color: #666;
      margin-top: 8px;
    }

    .scan-status {
      margin-top: 20px;

      .scan-animation {
        width: 40px;
        height: 40px;
        border: 3px solid #f3f3f3;
        border-top: 3px solid #007bff;
        border-radius: 50%;
        animation: spin 1s linear infinite;
        margin: 0 auto 12px;
      }

      .scan-instruction {
        font-size: 14px;
        color: #007bff;
        font-weight: 500;
      }
    }
  }

  .scan-actions {
    display: flex;
    flex-direction: column;
    gap: 24px;
    margin-top: 32px;

    .start-scan-btn {
      background: #20201E;
      color: #A9FF00;
      border: none;
      padding: 20px 32px;
      border-radius: 16px;
      cursor: pointer;
      font-size: 48px;
      font-weight: 600;
      height: 120px;
      transition: all 0.2s;

      &:hover {
        background: #333;
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
      }
    }

    .stop-scan-btn {
      background: #dc3545;
      color: white;
      border: none;
      padding: 20px 32px;
      border-radius: 16px;
      cursor: pointer;
      font-size: 48px;
      font-weight: 600;
      height: 120px;
      transition: all 0.2s;

      &:hover {
        background: #c82333;
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(220, 53, 69, 0.3);
      }
    }

    .manual-input-btn {
      background: #f8f9fa;
      color: #333;
      border: 2px solid #e9ecef;
      padding: 20px 32px;
      border-radius: 16px;
      cursor: pointer;
      font-size: 48px;
      font-weight: 600;
      height: 120px;
      transition: all 0.2s;

      &:hover {
        background: #e9ecef;
        border-color: #adb5bd;
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      }
    }
  }
}

.message-toast {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  background: #333;
  color: white;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 14px;
  z-index: 2000;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);

  &.success {
    background: #28a745;
  }

  &.error {
    background: #dc3545;
  }

  &.info {
    background: #666;
  }
}

// 动画定义
@keyframes pulse {
  0%,
  100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>
