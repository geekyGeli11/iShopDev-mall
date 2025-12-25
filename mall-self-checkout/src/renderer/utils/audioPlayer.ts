/**
 * 音频播放工具类
 * 用于播放预录制的语音 MP3 文件
 */

import { debugLogger, LogLevel } from './debugLogger'

// 语音文件配置
interface VoiceFile {
  id: string
  filename: string
  text: string
  path: string
}

// 语音文件映射 - 使用 iPhone 录制的 m4a 文件
const VOICE_FILES: Record<string, VoiceFile> = {
  payment_success: {
    id: 'payment_success',
    filename: 'payment_success.m4a',
    text: '支付成功，欢迎再次光临',
    path: '/assets/audio/voice/payment_success.m4a'
  },
  payment_success_with_amount: {
    id: 'payment_success_with_amount',
    filename: 'payment_success.m4a',
    text: '支付成功，欢迎再次光临',
    path: '/assets/audio/voice/payment_success.m4a'
  },
  payment_failure: {
    id: 'payment_failure',
    filename: 'payment_failure.m4a',
    text: '支付失败，请重试',
    path: '/assets/audio/voice/payment_failure.m4a'
  },
  scan_to_pay: {
    id: 'scan_to_pay',
    filename: 'scan_to_pay.m4a',
    text: '请扫码支付',
    path: '/assets/audio/voice/scan_to_pay.m4a'
  },
  welcome: {
    id: 'welcome',
    filename: 'welcome.m4a',
    text: '欢迎使用广横走商城自助收银系统',
    path: '/assets/audio/voice/welcome.m4a'
  },
  scan_product: {
    id: 'scan_product',
    filename: 'scan_product.mp3',
    text: '请扫描商品条码',
    path: '/assets/audio/voice/scan_product.mp3'
  },
  add_product_success: {
    id: 'add_product_success',
    filename: 'add_product_success.mp3',
    text: '商品添加成功',
    path: '/assets/audio/voice/add_product_success.mp3'
  }
}

/**
 * 音频播放管理器
 */
export class AudioPlayerManager {
  private static instance: AudioPlayerManager
  private currentAudio: HTMLAudioElement | null = null
  private isPlaying = false
  private volume = 1.0
  private isInitialized = false

  private constructor() {}

  /**
   * 获取单例实例
   */
  public static getInstance(): AudioPlayerManager {
    if (!AudioPlayerManager.instance) {
      AudioPlayerManager.instance = new AudioPlayerManager()
    }
    return AudioPlayerManager.instance
  }

  /**
   * 初始化音频播放器
   */
  public async initialize(): Promise<void> {
    try {
      debugLogger.logInfo('音频播放器初始化', '开始初始化音频播放器')

      // 检查浏览器音频支持
      const audio = new Audio()
      const canPlayMP3 = audio.canPlayType('audio/mpeg') !== ''
      const canPlayM4A = audio.canPlayType('audio/mp4') !== '' || audio.canPlayType('audio/m4a') !== ''

      debugLogger.logInfo('音频格式支持检查', `MP3: ${canPlayMP3 ? '支持' : '不支持'}, M4A: ${canPlayM4A ? '支持' : '不支持'}`)

      if (!canPlayMP3 && !canPlayM4A) {
        debugLogger.logWarning('音频格式支持', '浏览器不支持 MP3 或 M4A 格式')
      }

      this.isInitialized = true
      debugLogger.logSuccess('音频播放器初始化', '音频播放器初始化成功')

    } catch (error) {
      debugLogger.logError('音频播放器初始化失败', `初始化失败: ${(error as Error).message}`)
      throw error
    }
  }

  /**
   * 播放指定的语音文件
   */
  public async playVoice(voiceId: string): Promise<void> {
    if (!this.isInitialized) {
      await this.initialize()
    }

    const voiceFile = VOICE_FILES[voiceId]
    if (!voiceFile) {
      debugLogger.logError('语音文件不存在', `未找到语音文件: ${voiceId}`)
      throw new Error(`语音文件不存在: ${voiceId}`)
    }

    try {
      // 停止当前播放的音频
      await this.stop()

      debugLogger.logInfo('语音播放开始', `开始播放: ${voiceFile.text}`, {
        voiceId,
        filename: voiceFile.filename,
        path: voiceFile.path
      })

      // 尝试使用 Web Speech API 作为主要方案
      if ('speechSynthesis' in window) {
        try {
          await this.speakWithWebSpeechAPI(voiceFile.text)
          return
        } catch (webError) {
          debugLogger.logWarning('Web Speech API 失败', `Web Speech API 播放失败，尝试音频文件: ${(webError as Error).message}`)
          // 继续尝试音频文件
        }
      }

      // 如果 Web Speech API 不支持或失败，尝试音频文件
      // 创建新的音频对象
      this.currentAudio = new Audio(voiceFile.path)
      this.currentAudio.volume = this.volume

      // 设置事件监听器
      this.currentAudio.onloadstart = () => {
        debugLogger.logInfo('语音加载', `开始加载音频文件: ${voiceFile.filename}`)
      }

      this.currentAudio.oncanplay = () => {
        debugLogger.logInfo('语音就绪', `音频文件加载完成: ${voiceFile.filename}`)
      }

      this.currentAudio.onplay = () => {
        this.isPlaying = true
        debugLogger.logInfo('语音播放中', `正在播放: ${voiceFile.text}`)
      }

      this.currentAudio.onended = () => {
        this.isPlaying = false
        debugLogger.logSuccess('语音播放完成', `播放完成: ${voiceFile.text}`)
      }

      this.currentAudio.onerror = (error) => {
        this.isPlaying = false
        debugLogger.logError('语音播放错误', `播放失败: ${voiceFile.filename}`, {
          error: error,
          path: voiceFile.path
        })
        // 音频文件播放失败，尝试 Web Speech API
        this.speakWithWebSpeechAPI(voiceFile.text).catch(webError => {
          debugLogger.logError('Web Speech API 也失败', `Web Speech API 播放失败: ${(webError as Error).message}`)
        })
      }

      // 开始播放
      await this.currentAudio.play()

    } catch (error) {
      this.isPlaying = false
      debugLogger.logError('语音播放失败', `播放失败: ${(error as Error).message}`, {
        voiceId,
        filename: voiceFile.filename,
        error: (error as Error).message
      })

      // 最后尝试 Web Speech API
      try {
        await this.speakWithWebSpeechAPI(voiceFile.text)
      } catch (webError) {
        debugLogger.logError('所有语音播放方案都失败', `最终失败: ${(webError as Error).message}`)
        throw new Error(`语音播放失败: ${(error as Error).message}`)
      }
    }
  }

  /**
   * 使用 Web Speech API 播放语音
   */
  private async speakWithWebSpeechAPI(text: string): Promise<void> {
    return new Promise((resolve, reject) => {
      if (!('speechSynthesis' in window)) {
        reject(new Error('浏览器不支持 Web Speech API'))
        return
      }

      const utterance = new SpeechSynthesisUtterance(text)
      utterance.lang = 'zh-CN'
      utterance.rate = 1.0
      utterance.pitch = 1.0
      utterance.volume = this.volume

      utterance.onstart = () => {
        this.isPlaying = true
        debugLogger.logInfo('Web Speech API 开始播放', `开始播放: ${text}`)
      }

      utterance.onend = () => {
        this.isPlaying = false
        debugLogger.logSuccess('Web Speech API 播放完成', `播放完成: ${text}`)
        resolve()
      }

      utterance.onerror = (error) => {
        this.isPlaying = false
        debugLogger.logError('Web Speech API 播放错误', `播放失败: ${error.error}`)
        reject(new Error(`Web Speech API 播放失败: ${error.error}`))
      }

      speechSynthesis.speak(utterance)
    })
  }

  /**
   * 停止当前播放
   */
  public async stop(): Promise<void> {
    if (this.currentAudio && this.isPlaying) {
      this.currentAudio.pause()
      this.currentAudio.currentTime = 0
      this.isPlaying = false
      debugLogger.logInfo('语音播放停止', '已停止当前语音播放')
    }
  }

  /**
   * 设置音量 (0.0 - 1.0)
   */
  public setVolume(volume: number): void {
    this.volume = Math.max(0, Math.min(1, volume))
    if (this.currentAudio) {
      this.currentAudio.volume = this.volume
    }
    debugLogger.logInfo('音量设置', `音量设置为: ${(this.volume * 100).toFixed(0)}%`)
  }

  /**
   * 获取当前播放状态
   */
  public getStatus(): {
    isInitialized: boolean
    isPlaying: boolean
    volume: number
    currentVoice: string | null
  } {
    return {
      isInitialized: this.isInitialized,
      isPlaying: this.isPlaying,
      volume: this.volume,
      currentVoice: this.currentAudio ? 'playing' : null
    }
  }

  /**
   * 获取所有可用的语音文件
   */
  public getAvailableVoices(): VoiceFile[] {
    return Object.values(VOICE_FILES)
  }
}

// 便捷函数
export const audioPlayer = AudioPlayerManager.getInstance()

/**
 * 播放支付成功语音
 */
export async function playPaymentSuccess(amount?: number): Promise<void> {
  try {
    if (amount) {
      // 如果有金额，播放带金额的版本
      await audioPlayer.playVoice('payment_success_with_amount')
    } else {
      // 否则播放通用版本
      await audioPlayer.playVoice('payment_success')
    }
  } catch (error) {
    debugLogger.logError('支付成功语音播放失败', `播放失败: ${(error as Error).message}`)
  }
}

/**
 * 播放支付失败语音
 */
export async function playPaymentFailure(): Promise<void> {
  try {
    await audioPlayer.playVoice('payment_failure')
  } catch (error) {
    debugLogger.logError('支付失败语音播放失败', `播放失败: ${(error as Error).message}`)
  }
}

/**
 * 播放扫码支付语音
 */
export async function playScanToPay(): Promise<void> {
  try {
    await audioPlayer.playVoice('scan_to_pay')
  } catch (error) {
    debugLogger.logError('扫码支付语音播放失败', `播放失败: ${(error as Error).message}`)
  }
}

/**
 * 播放欢迎语音
 */
export async function playWelcome(): Promise<void> {
  try {
    await audioPlayer.playVoice('welcome')
  } catch (error) {
    debugLogger.logError('欢迎语音播放失败', `播放失败: ${(error as Error).message}`)
  }
}
