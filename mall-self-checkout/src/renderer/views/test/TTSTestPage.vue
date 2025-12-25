<template>
  <div class="tts-test-page">
    <van-nav-bar 
      title="语音播报测试" 
      left-arrow 
      @click-left="$router.go(-1)"
      fixed
      placeholder
    />

    <div class="test-content">
      <van-cell-group title="TTS 状态">
        <van-cell title="初始化状态" :value="initStatus" />
        <van-cell title="支持的语言数量" :value="supportedLanguages.length" />
        <van-cell title="是否正在播放" :value="isPlaying ? '是' : '否'" />
      </van-cell-group>

      <van-cell-group title="测试文本">
        <van-field
          v-model="testText"
          label="测试文本"
          placeholder="请输入要播报的文本"
          type="textarea"
          rows="3"
          maxlength="200"
          show-word-limit
        />
      </van-cell-group>

      <van-cell-group title="语音参数">
        <van-field
          v-model="language"
          label="语言"
          placeholder="zh-CN"
        />
        <van-cell title="语速">
          <template #value>
            <van-slider 
              v-model="rate" 
              :min="0.1" 
              :max="3" 
              :step="0.1"
              style="width: 120px;"
            />
            <span style="margin-left: 8px;">{{ rate }}</span>
          </template>
        </van-cell>
        <van-cell title="音调">
          <template #value>
            <van-slider 
              v-model="pitch" 
              :min="0.5" 
              :max="2" 
              :step="0.1"
              style="width: 120px;"
            />
            <span style="margin-left: 8px;">{{ pitch }}</span>
          </template>
        </van-cell>
        <van-cell title="音量">
          <template #value>
            <van-slider 
              v-model="volume" 
              :min="0" 
              :max="1" 
              :step="0.1"
              style="width: 120px;"
            />
            <span style="margin-left: 8px;">{{ volume }}</span>
          </template>
        </van-cell>
      </van-cell-group>

      <div class="action-buttons">
        <van-button 
          type="primary" 
          block 
          @click="testSpeak"
          :loading="speaking"
          :disabled="!testText.trim()"
        >
          {{ speaking ? '播放中...' : '开始播放' }}
        </van-button>
        
        <van-button 
          type="default" 
          block 
          @click="stopSpeak"
          :disabled="!speaking"
          style="margin-top: 12px;"
        >
          停止播放
        </van-button>
      </div>

      <van-cell-group title="快捷测试">
        <van-button 
          size="small" 
          type="success" 
          @click="testPaymentSuccess"
          style="margin: 8px;"
        >
          测试支付成功
        </van-button>
        
        <van-button
          size="small"
          type="danger"
          @click="testPaymentFailure"
          style="margin: 8px;"
        >
          测试支付失败
        </van-button>

        <van-button
          size="small"
          type="primary"
          @click="testScanToPay"
          style="margin: 8px;"
        >
          测试请扫码支付
        </van-button>
      </van-cell-group>

      <van-cell-group title="支持的语言" v-if="supportedLanguages.length > 0">
        <van-cell 
          v-for="lang in supportedLanguages.slice(0, 10)" 
          :key="lang"
          :title="lang"
          @click="language = lang"
        />
        <van-cell 
          v-if="supportedLanguages.length > 10"
          :title="`还有 ${supportedLanguages.length - 10} 种语言...`"
          title-class="van-cell__title--secondary"
        />
      </van-cell-group>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { showToast } from 'vant'
import { TTSManager, speakPaymentSuccess, speakPaymentFailure, speakScanToPay } from '@/utils/tts'

// 响应式数据
const initStatus = ref('未初始化')
const supportedLanguages = ref<string[]>([])
const isPlaying = ref(false)
const speaking = ref(false)

// 测试参数
const testText = ref('你好，这是语音播报测试')
const language = ref('zh-CN')
const rate = ref(1.0)
const pitch = ref(1.0)
const volume = ref(1.0)

// TTS 实例
const tts = TTSManager.getInstance()

// 初始化 TTS
const initTTS = async () => {
  try {
    initStatus.value = '初始化中...'
    await tts.initialize()
    initStatus.value = '初始化成功'
    
    // 获取支持的语言
    supportedLanguages.value = tts.getSupportedLanguages()
    
    showToast('TTS 初始化成功')
  } catch (error) {
    console.error('TTS 初始化失败:', error)
    initStatus.value = '初始化失败'
    showToast('TTS 初始化失败')
  }
}

// 测试播放
const testSpeak = async () => {
  if (!testText.value.trim()) {
    showToast('请输入测试文本')
    return
  }

  try {
    speaking.value = true
    isPlaying.value = true
    
    await tts.speak({
      text: testText.value,
      lang: language.value,
      rate: rate.value,
      pitch: pitch.value,
      volume: volume.value
    })
    
    showToast('播放完成')
  } catch (error) {
    console.error('播放失败:', error)
    showToast('播放失败')
  } finally {
    speaking.value = false
    isPlaying.value = false
  }
}

// 停止播放
const stopSpeak = async () => {
  try {
    await tts.stop()
    speaking.value = false
    isPlaying.value = false
    showToast('已停止播放')
  } catch (error) {
    console.error('停止播放失败:', error)
    showToast('停止播放失败')
  }
}

// 测试支付成功
const testPaymentSuccess = async () => {
  try {
    await speakPaymentSuccess(99.99)
    showToast('支付成功语音播报完成')
  } catch (error) {
    console.error('支付成功语音播报失败:', error)
    showToast('支付成功语音播报失败')
  }
}

// 测试支付失败
const testPaymentFailure = async () => {
  try {
    await speakPaymentFailure()
    showToast('支付失败语音播报完成')
  } catch (error) {
    console.error('支付失败语音播报失败:', error)
    showToast('支付失败语音播报失败')
  }
}

// 测试请扫码支付
const testScanToPay = async () => {
  try {
    await speakScanToPay()
    showToast('请扫码支付语音播报完成')
  } catch (error) {
    console.error('请扫码支付语音播报失败:', error)
    showToast('请扫码支付语音播报失败')
  }
}

// 生命周期
onMounted(() => {
  initTTS()
})
</script>

<style scoped lang="scss">
.tts-test-page {
  min-height: 100vh;
  background: #f7f8fa;
}

.test-content {
  padding: 16px;
  
  .van-cell-group {
    margin-bottom: 16px;
  }
}

.action-buttons {
  margin: 24px 0;
}

.van-cell__title--secondary {
  color: #969799;
  font-size: 14px;
}
</style>
