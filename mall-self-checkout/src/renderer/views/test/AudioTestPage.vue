<template>
  <div class="audio-test-page">
    <van-nav-bar title="音频播放测试" left-arrow @click-left="$router.back()" />
    
    <div class="test-container">
      <!-- 音频播放器状态 -->
      <van-cell-group title="音频播放器状态">
        <van-cell title="初始化状态" :value="audioStatus.isInitialized ? '✅ 已初始化' : '❌ 未初始化'" />
        <van-cell title="播放状态" :value="audioStatus.isPlaying ? '🔊 播放中' : '⏸️ 停止'" />
        <van-cell title="音量" :value="`${Math.round(audioStatus.volume * 100)}%`" />
      </van-cell-group>

      <!-- 音量控制 -->
      <van-cell-group title="音量控制">
        <van-cell title="音量">
          <template #right-icon>
            <van-slider 
              v-model="volumeValue" 
              :min="0" 
              :max="100" 
              @change="onVolumeChange"
              style="width: 120px;"
            />
          </template>
        </van-cell>
      </van-cell-group>

      <!-- 语音测试按钮 -->
      <van-cell-group title="语音播放测试">
        <van-cell 
          title="支付成功" 
          is-link 
          @click="testPaymentSuccess"
          :loading="loading.paymentSuccess"
        >
          <template #right-icon>
            <van-icon name="volume-o" />
          </template>
        </van-cell>
        
        <van-cell 
          title="支付成功（带金额）" 
          is-link 
          @click="testPaymentSuccessWithAmount"
          :loading="loading.paymentSuccessAmount"
        >
          <template #right-icon>
            <van-icon name="volume-o" />
          </template>
        </van-cell>
        
        <van-cell 
          title="支付失败" 
          is-link 
          @click="testPaymentFailure"
          :loading="loading.paymentFailure"
        >
          <template #right-icon>
            <van-icon name="volume-o" />
          </template>
        </van-cell>
        
        <van-cell 
          title="请扫码支付" 
          is-link 
          @click="testScanToPay"
          :loading="loading.scanToPay"
        >
          <template #right-icon>
            <van-icon name="volume-o" />
          </template>
        </van-cell>
        
        <van-cell 
          title="欢迎语音" 
          is-link 
          @click="testWelcome"
          :loading="loading.welcome"
        >
          <template #right-icon>
            <van-icon name="volume-o" />
          </template>
        </van-cell>
      </van-cell-group>

      <!-- 直接音频文件测试 -->
      <van-cell-group title="直接音频文件测试">
        <van-cell 
          v-for="voice in availableVoices" 
          :key="voice.id"
          :title="voice.text"
          is-link 
          @click="testDirectAudio(voice.id)"
          :loading="loading[voice.id]"
        >
          <template #right-icon>
            <van-icon name="music-o" />
          </template>
        </van-cell>
      </van-cell-group>

      <!-- 控制按钮 -->
      <van-cell-group title="播放控制">
        <van-cell title="停止播放" is-link @click="stopAudio">
          <template #right-icon>
            <van-icon name="pause" />
          </template>
        </van-cell>
        
        <van-cell title="刷新状态" is-link @click="refreshStatus">
          <template #right-icon>
            <van-icon name="replay" />
          </template>
        </van-cell>
      </van-cell-group>

      <!-- 测试日志 -->
      <van-cell-group title="测试日志">
        <div class="log-container">
          <div 
            v-for="(log, index) in testLogs" 
            :key="index"
            class="log-item"
            :class="log.type"
          >
            <span class="log-time">{{ log.time }}</span>
            <span class="log-message">{{ log.message }}</span>
          </div>
          <div v-if="testLogs.length === 0" class="log-empty">
            暂无测试日志
          </div>
        </div>
      </van-cell-group>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { showToast } from 'vant'
import { audioPlayer } from '../../utils/audioPlayer'
import { 
  speakPaymentSuccess, 
  speakPaymentFailure, 
  speakScanToPay 
} from '../../utils/tts'

// 响应式数据
const audioStatus = ref({
  isInitialized: false,
  isPlaying: false,
  volume: 1.0,
  currentVoice: null
})

const volumeValue = ref(100)

const loading = reactive({
  paymentSuccess: false,
  paymentSuccessAmount: false,
  paymentFailure: false,
  scanToPay: false,
  welcome: false,
  payment_success: false,
  payment_success_with_amount: false,
  payment_failure: false,
  scan_to_pay: false,
  scan_product: false,
  add_product_success: false
})

const availableVoices = ref<any[]>([])

const testLogs = ref<Array<{
  time: string
  message: string
  type: 'info' | 'success' | 'error' | 'warning'
}>>([])

// 添加日志
const addLog = (message: string, type: 'info' | 'success' | 'error' | 'warning' = 'info') => {
  const time = new Date().toLocaleTimeString()
  testLogs.value.unshift({ time, message, type })
  if (testLogs.value.length > 20) {
    testLogs.value = testLogs.value.slice(0, 20)
  }
}

// 刷新状态
const refreshStatus = async () => {
  try {
    const status = audioPlayer.getStatus()
    audioStatus.value = status
    volumeValue.value = Math.round(status.volume * 100)
    
    const voices = audioPlayer.getAvailableVoices()
    availableVoices.value = voices
    
    addLog(`状态刷新成功 - 初始化: ${status.isInitialized}, 播放中: ${status.isPlaying}`, 'success')
  } catch (error) {
    addLog(`状态刷新失败: ${(error as Error).message}`, 'error')
  }
}

// 音量变化
const onVolumeChange = (value: number) => {
  const volume = value / 100
  audioPlayer.setVolume(volume)
  audioStatus.value.volume = volume
  addLog(`音量设置为: ${value}%`, 'info')
}

// 测试函数
const testPaymentSuccess = async () => {
  loading.paymentSuccess = true
  try {
    addLog('开始测试支付成功语音', 'info')
    await speakPaymentSuccess()
    addLog('支付成功语音测试完成', 'success')
    showToast('支付成功语音播放完成')
  } catch (error) {
    addLog(`支付成功语音测试失败: ${(error as Error).message}`, 'error')
    showToast('支付成功语音播放失败')
  } finally {
    loading.paymentSuccess = false
  }
}

const testPaymentSuccessWithAmount = async () => {
  loading.paymentSuccessAmount = true
  try {
    addLog('开始测试支付成功语音（带金额）', 'info')
    await speakPaymentSuccess(99.99)
    addLog('支付成功语音（带金额）测试完成', 'success')
    showToast('支付成功语音（带金额）播放完成')
  } catch (error) {
    addLog(`支付成功语音（带金额）测试失败: ${(error as Error).message}`, 'error')
    showToast('支付成功语音（带金额）播放失败')
  } finally {
    loading.paymentSuccessAmount = false
  }
}

const testPaymentFailure = async () => {
  loading.paymentFailure = true
  try {
    addLog('开始测试支付失败语音', 'info')
    await speakPaymentFailure()
    addLog('支付失败语音测试完成', 'success')
    showToast('支付失败语音播放完成')
  } catch (error) {
    addLog(`支付失败语音测试失败: ${(error as Error).message}`, 'error')
    showToast('支付失败语音播放失败')
  } finally {
    loading.paymentFailure = false
  }
}

const testScanToPay = async () => {
  loading.scanToPay = true
  try {
    addLog('开始测试请扫码支付语音', 'info')
    await speakScanToPay()
    addLog('请扫码支付语音测试完成', 'success')
    showToast('请扫码支付语音播放完成')
  } catch (error) {
    addLog(`请扫码支付语音测试失败: ${(error as Error).message}`, 'error')
    showToast('请扫码支付语音播放失败')
  } finally {
    loading.scanToPay = false
  }
}

const testWelcome = async () => {
  loading.welcome = true
  try {
    addLog('开始测试欢迎语音', 'info')
    await audioPlayer.playVoice('welcome')
    addLog('欢迎语音测试完成', 'success')
    showToast('欢迎语音播放完成')
  } catch (error) {
    addLog(`欢迎语音测试失败: ${(error as Error).message}`, 'error')
    showToast('欢迎语音播放失败')
  } finally {
    loading.welcome = false
  }
}

const testDirectAudio = async (voiceId: string) => {
  loading[voiceId] = true
  try {
    addLog(`开始测试直接音频播放: ${voiceId}`, 'info')
    await audioPlayer.playVoice(voiceId)
    addLog(`直接音频播放完成: ${voiceId}`, 'success')
    showToast(`音频播放完成: ${voiceId}`)
  } catch (error) {
    addLog(`直接音频播放失败: ${voiceId} - ${(error as Error).message}`, 'error')
    showToast(`音频播放失败: ${voiceId}`)
  } finally {
    loading[voiceId] = false
  }
}

const stopAudio = async () => {
  try {
    await audioPlayer.stop()
    addLog('音频播放已停止', 'info')
    showToast('音频播放已停止')
    await refreshStatus()
  } catch (error) {
    addLog(`停止音频播放失败: ${(error as Error).message}`, 'error')
  }
}

// 初始化
onMounted(async () => {
  try {
    addLog('初始化音频播放器...', 'info')
    await audioPlayer.initialize()
    addLog('音频播放器初始化成功', 'success')
    await refreshStatus()
  } catch (error) {
    addLog(`音频播放器初始化失败: ${(error as Error).message}`, 'error')
  }
})
</script>

<style scoped>
.audio-test-page {
  min-height: 100vh;
  background-color: #f7f8fa;
}

.test-container {
  padding: 16px;
}

.log-container {
  max-height: 300px;
  overflow-y: auto;
  background: #fff;
  border-radius: 8px;
  padding: 12px;
}

.log-item {
  display: flex;
  margin-bottom: 8px;
  font-size: 12px;
  line-height: 1.4;
}

.log-time {
  color: #969799;
  margin-right: 8px;
  min-width: 60px;
}

.log-message {
  flex: 1;
}

.log-item.success .log-message {
  color: #07c160;
}

.log-item.error .log-message {
  color: #ee0a24;
}

.log-item.warning .log-message {
  color: #ff976a;
}

.log-item.info .log-message {
  color: #323233;
}

.log-empty {
  text-align: center;
  color: #969799;
  padding: 20px;
}
</style>
