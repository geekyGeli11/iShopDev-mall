<template>
  <div class="settings-page">
    <!-- 顶部导航 -->
    <van-nav-bar 
      title="系统设置" 
      left-arrow 
      @click-left="goBack"
      class="settings-nav"
    />

    <!-- 设置内容 -->
    <div class="settings-content">
      <!-- 基本设置 -->
      <div class="settings-section">
        <h4 class="section-title">基本设置</h4>
        <van-cell-group inset>
          <!-- 语言设置 -->
          <van-cell 
            title="语言设置" 
            :value="languageText"
            is-link 
            @click="showLanguagePicker = true"
          >
            <template #icon>
              <van-icon name="chat-o" color="#1989fa" />
            </template>
          </van-cell>
          
          <!-- 主题设置 -->
          <van-cell 
            title="主题模式" 
            :value="themeText"
            is-link 
            @click="showThemePicker = true"
          >
            <template #icon>
              <van-icon name="palette-o" color="#ff6b6b" />
            </template>
          </van-cell>
          
          <!-- 字体大小 -->
          <van-cell 
            title="字体大小" 
            :value="fontSizeText"
            is-link 
            @click="showFontSizePicker = true"
          >
            <template #icon>
              <van-icon name="font-o" color="#4ecdc4" />
            </template>
          </van-cell>
        </van-cell-group>
      </div>

      <!-- 扫码设置 -->
      <div class="settings-section">
        <h4 class="section-title">扫码设置</h4>
        <van-cell-group inset>
          <!-- 扫码提示音 -->
          <van-cell title="扫码提示音">
            <template #icon>
              <van-icon name="volume-o" color="#ffa726" />
            </template>
            <template #right-icon>
              <van-switch 
                v-model="settings.scanSound" 
                @change="onScanSoundChange"
                size="20"
              />
            </template>
          </van-cell>
          
          <!-- 扫码震动 -->
          <van-cell title="扫码震动">
            <template #icon>
              <van-icon name="phone-o" color="#9c27b0" />
            </template>
            <template #right-icon>
              <van-switch 
                v-model="settings.scanVibration" 
                @change="onScanVibrationChange"
                size="20"
              />
            </template>
          </van-cell>
          
          <!-- 自动对焦 -->
          <van-cell title="自动对焦">
            <template #icon>
              <van-icon name="scan" color="#2196f3" />
            </template>
            <template #right-icon>
              <van-switch 
                v-model="settings.autoFocus" 
                @change="onAutoFocusChange"
                size="20"
              />
            </template>
          </van-cell>
        </van-cell-group>
      </div>

      <!-- 支付设置 -->
      <div class="settings-section">
        <h4 class="section-title">支付设置</h4>
        <van-cell-group inset>
          <!-- 支付超时时间 -->
          <van-cell 
            title="支付超时时间" 
            :value="`${settings.paymentTimeout}秒`"
            is-link 
            @click="showPaymentTimeoutPicker = true"
          >
            <template #icon>
              <van-icon name="clock-o" color="#ff5722" />
            </template>
          </van-cell>
          
          <!-- 自动打印小票 -->
          <van-cell title="自动打印小票">
            <template #icon>
              <van-icon name="print" color="#607d8b" />
            </template>
            <template #right-icon>
              <van-switch 
                v-model="settings.autoPrint" 
                @change="onAutoPrintChange"
                size="20"
              />
            </template>
          </van-cell>
        </van-cell-group>
      </div>

      <!-- 系统信息 -->
      <div class="settings-section">
        <h4 class="section-title">系统信息</h4>
        <van-cell-group inset>
          <van-cell 
            title="应用版本" 
            :value="appVersion"
            @click="checkUpdate"
            is-link
          >
            <template #icon>
              <van-icon name="info-o" color="#795548" />
            </template>
          </van-cell>
          
          <van-cell 
            title="缓存大小" 
            :value="cacheSize"
            @click="clearCache"
            is-link
          >
            <template #icon>
              <van-icon name="delete-o" color="#9e9e9e" />
            </template>
          </van-cell>
        </van-cell-group>
      </div>

      <!-- 其他设置 -->
      <div class="settings-section">
        <h4 class="section-title">其他</h4>
        <van-cell-group inset>
          <van-cell 
            title="意见反馈" 
            @click="goToFeedback"
            is-link
          >
            <template #icon>
              <van-icon name="chat-o" color="#03a9f4" />
            </template>
          </van-cell>
          
          <van-cell
            title="关于我们"
            @click="showAbout = true"
            is-link
          >
            <template #icon>
              <van-icon name="info-o" color="#ff9800" />
            </template>
          </van-cell>

          <!-- 开发者选项 -->
          <van-cell
            title="音频播放测试"
            @click="goToAudioTest"
            is-link
          >
            <template #icon>
              <van-icon name="volume-o" color="#9c27b0" />
            </template>
          </van-cell>
        </van-cell-group>
      </div>

      <!-- 重置设置 -->
      <div class="settings-section">
        <van-button 
          type="danger" 
          size="large" 
          block 
          round
          @click="resetSettings"
          class="reset-btn"
        >
          恢复默认设置
        </van-button>
      </div>

      <!-- 网络调试区域 -->
      <div class="debug-section" v-if="showDebugInfo">
        <h3>🔧 网络调试</h3>
        <div class="debug-info">
          <p><strong>当前API地址:</strong> {{ currentApiUrl }}</p>
          <p><strong>用户代理:</strong> {{ userAgent }}</p>
          <p><strong>协议:</strong> {{ protocol }}</p>
          <p><strong>是否移动端:</strong> {{ isMobile ? '是' : '否' }}</p>
        </div>
        
        <div class="api-test">
          <van-button @click="testApiConnection" :loading="testing" type="primary" size="small">
            测试API连接
          </van-button>
          <p v-if="testResult" :class="testResult.success ? 'success' : 'error'">
            {{ testResult.message }}
          </p>
        </div>
        
        <div class="api-options">
          <h4>切换API地址:</h4>
          <van-radio-group v-model="selectedApiUrl" @change="changeApiUrl">
            <van-radio name="http://localhost:8201/mall-selfcheck">localhost (桌面)</van-radio>
            <van-radio name="http://10.0.2.2:8201/mall-selfcheck">10.0.2.2 (模拟器)</van-radio>
            <van-radio name="http://192.168.1.4:8201/mall-selfcheck">192.168.1.4 (局域网)</van-radio>
          </van-radio-group>
        </div>
      </div>
    </div>

    <!-- 语言选择器 -->
    <van-popup v-model:show="showLanguagePicker" position="bottom">
      <van-picker
        :columns="languageOptions"
        @confirm="onLanguageConfirm"
        @cancel="showLanguagePicker = false"
      />
    </van-popup>

    <!-- 主题选择器 -->
    <van-popup v-model:show="showThemePicker" position="bottom">
      <van-picker
        :columns="themeOptions"
        @confirm="onThemeConfirm"
        @cancel="showThemePicker = false"
      />
    </van-popup>

    <!-- 字体大小选择器 -->
    <van-popup v-model:show="showFontSizePicker" position="bottom">
      <van-picker
        :columns="fontSizeOptions"
        @confirm="onFontSizeConfirm"
        @cancel="showFontSizePicker = false"
      />
    </van-popup>

    <!-- 支付超时选择器 -->
    <van-popup v-model:show="showPaymentTimeoutPicker" position="bottom">
      <van-picker
        :columns="paymentTimeoutOptions"
        @confirm="onPaymentTimeoutConfirm"
        @cancel="showPaymentTimeoutPicker = false"
      />
    </van-popup>

    <!-- 关于我们弹窗 -->
    <van-popup v-model:show="showAbout" position="bottom" :style="{ height: '60%' }">
      <div class="about-popup">
        <div class="popup-header">
          <h3>关于我们</h3>
          <van-icon name="cross" @click="showAbout = false" />
        </div>
        <div class="about-content">
          <div class="about-logo">
            <van-icon name="shop-o" size="60" color="#1989fa" />
          </div>
          <h2>Mall 自助收银系统</h2>
          <p class="version">版本 {{ appVersion }}</p>
          <p class="description">智能购物，轻松结算。为您提供便捷的自助购物体验。</p>
          <div class="contact-info">
            <p>技术支持：support@mall.com</p>
            <p>客服热线：400-123-4567</p>
          </div>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showSuccessToast, showConfirmDialog } from 'vant'

const router = useRouter()

// 响应式数据
const showLanguagePicker = ref(false)
const showThemePicker = ref(false)
const showFontSizePicker = ref(false)
const showPaymentTimeoutPicker = ref(false)
const showAbout = ref(false)

// 设置数据
const settings = ref({
  language: 'zh-CN',
  theme: 'auto',
  fontSize: 'medium',
  scanSound: true,
  scanVibration: true,
  autoFocus: true,
  paymentTimeout: 300,
  autoPrint: false
})

// 应用信息
const appVersion = ref('1.0.0')
const cacheSize = ref('12.5MB')

// 选择器选项
const languageOptions = [
  { text: '简体中文', value: 'zh-CN' },
  { text: 'English', value: 'en-US' }
]

const themeOptions = [
  { text: '跟随系统', value: 'auto' },
  { text: '浅色模式', value: 'light' },
  { text: '深色模式', value: 'dark' }
]

const fontSizeOptions = [
  { text: '小号', value: 'small' },
  { text: '标准', value: 'medium' },
  { text: '大号', value: 'large' }
]

const paymentTimeoutOptions = [
  { text: '120秒', value: 120 },
  { text: '180秒', value: 180 },
  { text: '300秒', value: 300 },
  { text: '600秒', value: 600 }
]

// 计算属性
const languageText = computed(() => {
  const option = languageOptions.find(item => item.value === settings.value.language)
  return option?.text || '简体中文'
})

const themeText = computed(() => {
  const option = themeOptions.find(item => item.value === settings.value.theme)
  return option?.text || '跟随系统'
})

const fontSizeText = computed(() => {
  const option = fontSizeOptions.find(item => item.value === settings.value.fontSize)
  return option?.text || '标准'
})

// 方法
const goBack = () => {
  router.back()
}

const goToFeedback = () => {
  router.push('/feedback')
}

const goToAudioTest = () => {
  router.push('/test/audio')
}

// 设置变更处理
const onScanSoundChange = (value: boolean) => {
  settings.value.scanSound = value
  saveSettings()
}

const onScanVibrationChange = (value: boolean) => {
  settings.value.scanVibration = value
  saveSettings()
}

const onAutoFocusChange = (value: boolean) => {
  settings.value.autoFocus = value
  saveSettings()
}

const onAutoPrintChange = (value: boolean) => {
  settings.value.autoPrint = value
  saveSettings()
}

// 选择器确认处理
const onLanguageConfirm = ({ selectedOptions }: any) => {
  settings.value.language = selectedOptions[0].value
  showLanguagePicker.value = false
  saveSettings()
  showSuccessToast('语言设置已更新')
}

const onThemeConfirm = ({ selectedOptions }: any) => {
  settings.value.theme = selectedOptions[0].value
  showThemePicker.value = false
  saveSettings()
  showSuccessToast('主题设置已更新')
}

const onFontSizeConfirm = ({ selectedOptions }: any) => {
  settings.value.fontSize = selectedOptions[0].value
  showFontSizePicker.value = false
  saveSettings()
  showSuccessToast('字体大小已更新')
}

const onPaymentTimeoutConfirm = ({ selectedOptions }: any) => {
  settings.value.paymentTimeout = selectedOptions[0].value
  showPaymentTimeoutPicker.value = false
  saveSettings()
}

// 系统操作
const checkUpdate = async () => {
  showToast('正在检查更新...')
  
  // 模拟检查更新
  setTimeout(() => {
    showSuccessToast('当前已是最新版本')
  }, 2000)
}

const clearCache = async () => {
  try {
    await showConfirmDialog({
      title: '清理缓存',
      message: '确定要清理应用缓存吗？这将删除所有本地数据。'
    })
    
    // 清理缓存逻辑
    localStorage.clear()
    sessionStorage.clear()
    
    showSuccessToast('缓存清理完成')
    cacheSize.value = '0MB'
    
  } catch (error) {
    // 用户取消
  }
}

const resetSettings = async () => {
  try {
    await showConfirmDialog({
      title: '恢复默认设置',
      message: '确定要恢复所有设置为默认值吗？'
    })
    
    // 重置为默认设置
    settings.value = {
      language: 'zh-CN',
      theme: 'auto',
      fontSize: 'medium',
      scanSound: true,
      scanVibration: true,
      autoFocus: true,
      paymentTimeout: 300,
      autoPrint: false
    }
    
    saveSettings()
    showSuccessToast('设置已恢复默认')
    
  } catch (error) {
    // 用户取消
  }
}

// 设置保存
const saveSettings = () => {
  localStorage.setItem('app_settings', JSON.stringify(settings.value))
}

// 生命周期
onMounted(() => {
  // 加载设置
  const savedSettings = localStorage.getItem('app_settings')
  if (savedSettings) {
    settings.value = { ...settings.value, ...JSON.parse(savedSettings) }
  }
})
</script>

<style scoped>
.settings-page {
  min-height: 100vh;
  background: #f8f9fa;
}

.settings-nav {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

:deep(.van-nav-bar__title) {
  color: #fff;
}

:deep(.van-nav-bar .van-icon) {
  color: #fff;
}

.settings-content {
  padding: 16px 16px 32px;
}

.settings-section {
  margin-bottom: 24px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #323233;
  margin: 0 0 12px 16px;
}

:deep(.van-cell-group--inset) {
  margin: 0;
}

:deep(.van-cell__left-icon) {
  margin-right: 12px;
}

.reset-btn {
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  margin-top: 20px;
}

/* 弹窗样式 */
.about-popup {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #ebedf0;
  background: #fff;
}

.popup-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

/* 关于我们 */
.about-content {
  flex: 1;
  padding: 32px 20px;
  text-align: center;
  overflow-y: auto;
}

.about-logo {
  margin-bottom: 20px;
}

.about-content h2 {
  font-size: 20px;
  font-weight: 600;
  color: #323233;
  margin: 0 0 8px 0;
}

.version {
  font-size: 14px;
  color: #969799;
  margin: 0 0 20px 0;
}

.description {
  font-size: 14px;
  color: #646566;
  line-height: 1.6;
  margin: 0 0 32px 0;
}

.contact-info {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
}

.contact-info p {
  font-size: 12px;
  color: #646566;
  margin: 0 0 8px 0;
}

.contact-info p:last-child {
  margin-bottom: 0;
}

/* 响应式设计 */
@media (max-width: 375px) {
  .settings-content {
    padding: 12px;
  }
  
  .section-title {
    margin-left: 12px;
  }
}
</style> 