/**
 * 语音播报工具类
 * 基于 @capacitor-community/text-to-speech 实现本地离线语音合成
 */

import { TextToSpeech } from '@capacitor-community/text-to-speech'
import { Capacitor } from '@capacitor/core'
import { debugLogger, LogLevel } from './debugLogger'
import {
  audioPlayer,
  playPaymentSuccess as audioPlayPaymentSuccess,
  playPaymentFailure as audioPlayPaymentFailure,
  playScanToPay as audioPlayScanToPay,
  playWelcome as audioPlayWelcome
} from './audioPlayer'

export interface TTSOptions {
  /** 要播报的文本 */
  text: string
  /** 语言代码，默认中文 */
  lang?: string
  /** 语速，范围 0.1-10，默认 1.0 */
  rate?: number
  /** 音调，范围 0-2，默认 1.0 */
  pitch?: number
  /** 音量，范围 0-1，默认 1.0 */
  volume?: number
  /** 是否等待播放完成 */
  waitForCompletion?: boolean
}

export class TTSManager {
  private static instance: TTSManager
  private isInitialized = false
  private supportedLanguages: string[] = []
  private isPlaying = false
  private diagnosticInfo: any = {}

  private constructor() {}

  /**
   * 获取单例实例
   */
  public static getInstance(): TTSManager {
    if (!TTSManager.instance) {
      TTSManager.instance = new TTSManager()
    }
    return TTSManager.instance
  }

  /**
   * 环境诊断和问题检测
   */
  private async runDiagnostics(): Promise<void> {
    try {
      debugLogger.logTTS('TTS环境诊断开始', '开始检测TTS环境和可能的问题', LogLevel.INFO)

      // 基础环境信息
      const platform = Capacitor.getPlatform()
      const isNative = Capacitor.isNativePlatform()
      const userAgent = navigator.userAgent

      this.diagnosticInfo = {
        platform,
        isNative,
        userAgent,
        timestamp: new Date().toISOString()
      }

      debugLogger.logTTS('TTS环境信息', '基础环境检测完成', LogLevel.INFO, {
        platform,
        isNative,
        userAgent: userAgent.substring(0, 100) + '...'
      })

      // 检查 Web Speech API 支持
      const webSpeechSupported = 'speechSynthesis' in window
      this.diagnosticInfo.webSpeechSupported = webSpeechSupported

      if (!webSpeechSupported) {
        debugLogger.logTTS('TTS兼容性问题', '浏览器不支持 Web Speech API', LogLevel.WARNING)
      }

      // 原生平台特定检查
      if (isNative) {
        try {
          // 检查 Capacitor TTS 插件是否可用
          const hasTextToSpeech = typeof TextToSpeech !== 'undefined'
          this.diagnosticInfo.hasTextToSpeech = hasTextToSpeech

          if (!hasTextToSpeech) {
            debugLogger.logTTS('TTS插件问题', 'Capacitor TextToSpeech 插件未正确加载', LogLevel.ERROR)
            return
          }

          // 尝试获取支持的语言（这会触发权限检查）
          debugLogger.logTTS('TTS语言检测', '开始获取支持的语言列表', LogLevel.INFO)

          const languageResult = await TextToSpeech.getSupportedLanguages()

          // 安全处理可能为 null 的语言列表
          if (languageResult && languageResult.languages) {
            this.diagnosticInfo.nativeLanguages = Array.isArray(languageResult.languages)
              ? languageResult.languages
              : []
          } else {
            this.diagnosticInfo.nativeLanguages = []
            debugLogger.logTTS('TTS语言检测警告', '获取到的语言列表为空或无效', LogLevel.WARNING, {
              languageResult
            })
          }

          debugLogger.logTTS('TTS原生支持检测', '原生TTS引擎检测完成', LogLevel.INFO, {
            languageCount: this.diagnosticInfo.nativeLanguages.length,
            languages: this.diagnosticInfo.nativeLanguages.slice(0, 5), // 只显示前5个语言
            hasChineseSupport: this.diagnosticInfo.nativeLanguages.some((lang: string) =>
              lang && (lang.startsWith('zh') || lang.includes('chinese') || lang.includes('cmn'))
            )
          })

        } catch (error) {
          this.diagnosticInfo.nativeError = (error as Error).message
          debugLogger.logTTS('TTS原生检测失败', `原生TTS检测失败: ${(error as Error).message}`, LogLevel.ERROR, {
            error: (error as Error).message,
            errorStack: (error as Error).stack,
            possibleCauses: [
              '缺少TTS权限 (RECORD_AUDIO, MODIFY_AUDIO_SETTINGS)',
              'Android系统TTS引擎未安装或未启用',
              'Capacitor插件配置错误',
              '设备不支持TTS功能',
              'TTS服务返回null对象（常见于未初始化的TTS引擎）'
            ],
            solutions: [
              '检查设置 > 辅助功能 > 文字转语音输出',
              '安装Google文字转语音引擎',
              '在系统设置中启用TTS服务',
              '重启设备后重试'
            ]
          })
        }
      }

      // Android 特定问题检测
      if (platform === 'android') {
        debugLogger.logTTS('Android TTS 问题诊断', '检测常见的Android TTS问题', LogLevel.INFO, {
          commonIssues: [
            '权限问题: 需要 RECORD_AUDIO 和 MODIFY_AUDIO_SETTINGS 权限',
            'TTS引擎: 设备可能未安装或启用中文TTS引擎',
            '系统设置: 用户可能在系统设置中禁用了TTS',
            '音频焦点: 其他应用可能占用了音频焦点',
            '版本兼容: Android版本与Capacitor TTS插件兼容性问题'
          ],
          troubleshooting: [
            '检查设置 > 辅助功能 > 文字转语音输出',
            '安装Google文字转语音引擎',
            '检查应用权限设置',
            '重启应用或设备'
          ]
        })

        // 执行Android特定的TTS状态检测
        await this.checkAndroidTTSStatus()
      }

      debugLogger.logTTS('TTS环境诊断完成', '环境诊断和问题检测完成', LogLevel.INFO, this.diagnosticInfo)

    } catch (error) {
      debugLogger.logTTS('TTS诊断失败', `环境诊断过程中发生错误: ${(error as Error).message}`, LogLevel.ERROR, { error })
    }
  }

  /**
   * 初始化 TTS 引擎
   */
  public async initialize(): Promise<void> {
    if (this.isInitialized) {
      debugLogger.logTTS('TTS初始化检查', 'TTS已经初始化，跳过重复初始化', LogLevel.DEBUG)
      return
    }

    try {
      debugLogger.logTTS('TTS初始化开始', '开始初始化语音播报引擎', LogLevel.INFO)

      // 运行环境诊断
      await this.runDiagnostics()

      // 检查平台支持
      if (!Capacitor.isNativePlatform()) {
        const message = '当前平台不支持原生 TTS，将使用 Web Speech API'
        console.warn('TTS: ' + message)
        debugLogger.logTTS('TTS平台检查', message, LogLevel.WARNING, {
          platform: Capacitor.getPlatform(),
          isNative: false
        })
        this.isInitialized = true
        debugLogger.logTTS('TTS初始化完成', 'Web Speech API 模式初始化成功', LogLevel.INFO)
        return
      }

      // 获取支持的语言
      debugLogger.logTTS('TTS语言获取', '开始获取TTS支持的语言列表', LogLevel.INFO)

      try {
        const result = await TextToSpeech.getSupportedLanguages()

        // 安全处理可能为 null 的语言列表
        if (result && result.languages) {
          this.supportedLanguages = Array.isArray(result.languages) ? result.languages : []
        } else {
          this.supportedLanguages = []
          debugLogger.logTTS('TTS语言获取警告', 'TTS引擎返回空的语言列表', LogLevel.WARNING)
        }

        console.log('TTS: 初始化成功，支持的语言:', this.supportedLanguages)
        debugLogger.logTTS('TTS语言列表', `获取到${this.supportedLanguages.length}种支持的语言`, LogLevel.INFO, {
          count: this.supportedLanguages.length,
          languages: this.supportedLanguages.slice(0, 10) // 只记录前10个
        })

      } catch (languageError) {
        this.supportedLanguages = []
        debugLogger.logTTS('TTS语言获取失败', `获取语言列表失败: ${(languageError as Error).message}`, LogLevel.ERROR, {
          error: (languageError as Error).message
        })
      }

      // 检查中文支持
      const chineseSupported = this.supportedLanguages.length > 0 && this.supportedLanguages.some(lang =>
        lang && (lang.startsWith('zh') || lang.includes('chinese') || lang.includes('cmn'))
      )

      if (!chineseSupported) {
        const message = '系统不支持中文语音，建议用户安装中文语音包'
        console.warn('TTS: ' + message)
        debugLogger.logTTS('TTS语言支持检查', message, LogLevel.WARNING, {
          supportedLanguages: this.supportedLanguages
        })
      }

      this.isInitialized = true
      debugLogger.logTTS('TTS初始化完成', '原生TTS引擎初始化成功', LogLevel.INFO, {
        platform: Capacitor.getPlatform(),
        supportedLanguages: this.supportedLanguages.length,
        chineseSupported
      })
    } catch (error) {
      const errorMessage = 'TTS初始化失败: ' + (error as Error).message
      console.error('TTS: 初始化失败:', error)
      debugLogger.logTTS('TTS初始化失败', errorMessage, LogLevel.ERROR, { error })
      throw new Error('语音播报初始化失败')
    }
  }

  /**
   * 播放语音
   */
  public async speak(options: TTSOptions): Promise<void> {
    const {
      text,
      lang = 'zh-CN',
      rate = 1.0,
      pitch = 1.0,
      volume = 1.0,
      waitForCompletion = false
    } = options

    try {
      // 确保已初始化
      if (!this.isInitialized) {
        await this.initialize()
      }

      // 如果正在播放，先停止
      if (this.isPlaying) {
        await this.stop()
      }

      // 验证参数
      if (!text || text.trim().length === 0) {
        debugLogger.logTTS('TTS播报参数错误', '播报文本不能为空', LogLevel.ERROR)
        throw new Error('播报文本不能为空')
      }

      // 限制参数范围
      const normalizedRate = Math.max(0.1, Math.min(10, rate))
      const normalizedPitch = Math.max(0, Math.min(2, pitch))
      const normalizedVolume = Math.max(0, Math.min(1, volume))

      debugLogger.logTTS('TTS播报开始', `开始播报: ${text}`, LogLevel.INFO, {
        text,
        lang,
        rate: normalizedRate,
        pitch: normalizedPitch,
        volume: normalizedVolume,
        platform: Capacitor.getPlatform(),
        isNative: Capacitor.isNativePlatform()
      })

      this.isPlaying = true

      if (Capacitor.isNativePlatform()) {
        // 使用原生 TTS
        try {
          debugLogger.logTTS('TTS原生播报', '开始使用原生TTS引擎播报', LogLevel.INFO, {
            textLength: text.length,
            lang,
            rate: normalizedRate,
            pitch: normalizedPitch,
            volume: normalizedVolume
          })

          await TextToSpeech.speak({
            text,
            lang,
            rate: normalizedRate,
            pitch: normalizedPitch,
            volume: normalizedVolume
          })

          debugLogger.logTTS('TTS原生播报成功', '原生TTS播报完成', LogLevel.INFO)

        } catch (nativeError) {
          debugLogger.logTTS('TTS原生播报失败', `原生TTS播报失败，尝试Web API: ${(nativeError as Error).message}`, LogLevel.WARNING, {
            error: (nativeError as Error).message,
            fallbackToWeb: true
          })

          // 原生TTS失败，尝试使用Web Speech API作为后备
          await this.speakWithWebAPI({
            text,
            lang,
            rate: normalizedRate,
            pitch: normalizedPitch,
            volume: normalizedVolume
          })
        }
      } else {
        // 使用 Web Speech API 作为后备方案
        debugLogger.logTTS('TTS Web播报', '使用Web Speech API播报', LogLevel.INFO)
        await this.speakWithWebAPI({
          text,
          lang,
          rate: normalizedRate,
          pitch: normalizedPitch,
          volume: normalizedVolume
        })
      }

      // 如果需要等待播放完成
      if (waitForCompletion) {
        // 估算播放时间（基于文本长度和语速）
        const estimatedDuration = (text.length / normalizedRate) * 200 // 毫秒
        await new Promise(resolve => setTimeout(resolve, estimatedDuration))
      }

      console.log('TTS: 播报完成:', text)
      debugLogger.logTTS('TTS播报完成', `播报完成: ${text}`, LogLevel.INFO)
    } catch (error) {
      const errorMessage = `TTS播报失败: ${(error as Error).message}`
      console.error('TTS: 播报失败:', error)
      debugLogger.logTTS('TTS播报失败', errorMessage, LogLevel.ERROR, {
        text,
        error: (error as Error).message
      })
      throw error
    } finally {
      this.isPlaying = false
    }
  }

  /**
   * 停止当前播放
   */
  public async stop(): Promise<void> {
    try {
      if (Capacitor.isNativePlatform()) {
        await TextToSpeech.stop()
      } else {
        // Web Speech API 停止
        if (window.speechSynthesis) {
          window.speechSynthesis.cancel()
        }
      }
      this.isPlaying = false
      console.log('TTS: 播报已停止')
    } catch (error) {
      console.error('TTS: 停止播报失败:', error)
    }
  }

  /**
   * 检查语言是否支持
   */
  public async isLanguageSupported(lang: string): Promise<boolean> {
    try {
      if (!this.isInitialized) {
        await this.initialize()
      }

      if (Capacitor.isNativePlatform()) {
        const result = await TextToSpeech.isLanguageSupported({ lang })
        return result.supported
      } else {
        // Web Speech API 检查
        return this.supportedLanguages.includes(lang)
      }
    } catch (error) {
      console.error('TTS: 检查语言支持失败:', error)
      return false
    }
  }

  /**
   * 获取支持的语言列表
   */
  public getSupportedLanguages(): string[] {
    return [...this.supportedLanguages]
  }

  /**
   * 检查是否正在播放
   */
  public isCurrentlyPlaying(): boolean {
    return this.isPlaying
  }

  /**
   * Web Speech API 后备实现
   */
  private async speakWithWebAPI(options: {
    text: string
    lang: string
    rate: number
    pitch: number
    volume: number
  }): Promise<void> {
    return new Promise((resolve, reject) => {
      if (!window.speechSynthesis) {
        reject(new Error('浏览器不支持 Web Speech API'))
        return
      }

      const utterance = new SpeechSynthesisUtterance(options.text)
      utterance.lang = options.lang
      utterance.rate = options.rate
      utterance.pitch = options.pitch
      utterance.volume = options.volume

      utterance.onend = () => resolve()
      utterance.onerror = (event) => reject(new Error(`Web Speech API 错误: ${event.error}`))

      window.speechSynthesis.speak(utterance)
    })
  }

  /**
   * Android TTS 状态检测
   */
  private async checkAndroidTTSStatus(): Promise<void> {
    if (Capacitor.getPlatform() !== 'android') return

    try {
      debugLogger.logTTS('Android TTS检测', '开始检测Android TTS状态', LogLevel.INFO)

      // 检测TTS引擎可用性
      const isAvailable = await TextToSpeech.isLanguageSupported({ lang: 'zh-CN' })

      debugLogger.logTTS('Android TTS可用性', `中文TTS支持状态: ${isAvailable.supported}`, LogLevel.INFO, {
        supported: isAvailable.supported,
        recommendations: isAvailable.supported ? [] : [
          '安装Google文字转语音引擎',
          '在设置 > 辅助功能 > 文字转语音输出中启用TTS',
          '下载中文语音包'
        ]
      })

    } catch (error) {
      debugLogger.logTTS('Android TTS检测失败', `Android TTS状态检测失败: ${(error as Error).message}`, LogLevel.ERROR, {
        error: (error as Error).message,
        troubleshooting: [
          '检查TTS权限是否已授予',
          '确认设备支持TTS功能',
          '重启应用或设备',
          '检查系统TTS设置'
        ]
      })
    }
  }

  /**
   * 获取诊断信息
   */
  public getDiagnosticInfo(): any {
    return {
      ...this.diagnosticInfo,
      isInitialized: this.isInitialized,
      supportedLanguages: this.supportedLanguages,
      isPlaying: this.isPlaying,
      lastUpdated: new Date().toISOString()
    }
  }
}

/**
 * 便捷的语音播报函数
 */
export async function speakText(text: string, options?: Partial<TTSOptions>): Promise<void> {
  const tts = TTSManager.getInstance()
  await tts.speak({ text, ...options })
}

/**
 * 支付成功语音播报
 */
export async function speakPaymentSuccess(amount?: number): Promise<void> {
  try {
    const text = amount
      ? `支付成功，金额${amount}元，欢迎再次光临`
      : '支付成功，欢迎再次光临'

    debugLogger.logTTS('支付成功语音播报', `准备播报支付成功: ${text}`, LogLevel.INFO, { amount })

    // 优先使用音频播放器
    try {
      await audioPlayPaymentSuccess(amount)
      debugLogger.logTTS('支付成功语音完成', '音频播放器播放完成', LogLevel.SUCCESS)
      return
    } catch (audioError) {
      debugLogger.logTTS('音频播放失败', '音频播放器失败，尝试TTS', LogLevel.WARNING, {
        error: (audioError as Error).message
      })
    }

    // 音频播放失败时，回退到TTS
    await speakText(text, {
      lang: 'zh-CN',
      rate: 1.0,
      pitch: 1.0,
      volume: 1.0
    })

    debugLogger.logTTS('支付成功语音完成', 'TTS播放完成', LogLevel.SUCCESS)

  } catch (error) {
    console.error('支付成功语音播报失败:', error)
    debugLogger.logTTS('支付成功语音播报失败', `播报失败: ${(error as Error).message}`, LogLevel.ERROR, { error })
    // 不抛出错误，避免影响主流程
  }
}

/**
 * 支付失败语音播报
 */
export async function speakPaymentFailure(): Promise<void> {
  try {
    debugLogger.logTTS('支付失败语音播报', '准备播报支付失败: 支付失败，请重试', LogLevel.INFO)

    // 优先使用音频播放器
    try {
      await audioPlayPaymentFailure()
      debugLogger.logTTS('支付失败语音完成', '音频播放器播放完成', LogLevel.SUCCESS)
      return
    } catch (audioError) {
      debugLogger.logTTS('音频播放失败', '音频播放器失败，尝试TTS', LogLevel.WARNING, {
        error: (audioError as Error).message
      })
    }

    // 音频播放失败时，回退到TTS
    await speakText('支付失败，请重试', {
      lang: 'zh-CN',
      rate: 1.0,
      pitch: 1.0,
      volume: 1.0
    })

    debugLogger.logTTS('支付失败语音完成', 'TTS播放完成', LogLevel.SUCCESS)

  } catch (error) {
    console.error('支付失败语音播报失败:', error)
    debugLogger.logTTS('支付失败语音播报失败', `播报失败: ${(error as Error).message}`, LogLevel.ERROR, { error })
    // 不抛出错误，避免影响主流程
  }
}

/**
 * 请扫码支付语音播报
 */
export async function speakScanToPay(): Promise<void> {
  try {
    debugLogger.logTTS('请扫码支付语音播报', '准备播报请扫码支付: 请扫码支付', LogLevel.INFO)

    // 优先使用音频播放器
    try {
      await audioPlayScanToPay()
      debugLogger.logTTS('请扫码支付语音完成', '音频播放器播放完成', LogLevel.SUCCESS)
      return
    } catch (audioError) {
      debugLogger.logTTS('音频播放失败', '音频播放器失败，尝试TTS', LogLevel.WARNING, {
        error: (audioError as Error).message
      })
    }

    // 音频播放失败时，回退到TTS
    await speakText('请扫码支付', {
      lang: 'zh-CN',
      rate: 1.0,
      pitch: 1.0,
      volume: 1.0
    })

    debugLogger.logTTS('请扫码支付语音完成', 'TTS播放完成', LogLevel.SUCCESS)

  } catch (error) {
    console.error('请扫码支付语音播报失败:', error)
    debugLogger.logTTS('请扫码支付语音播报失败', `播报失败: ${(error as Error).message}`, LogLevel.ERROR, { error })
    // 不抛出错误，避免影响主流程
  }
}

// 导出默认实例
export default TTSManager.getInstance()
