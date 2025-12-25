import { CapacitorConfig } from '@capacitor/cli'

const config: CapacitorConfig = {
  appId: 'com.mall.selfcheckout',
  appName: '广横走商城',
  webDir: 'dist/renderer',
  server: {
    androidScheme: 'https',
    // 开发环境允许访问本地服务器
    cleartext: true,
    // 生产环境允许访问HTTPS API
    allowNavigation: [
      'https://guanghengzou.hello4am.com',
      'https://test.guanghengzou.hello4am.com'
    ]
  },
  plugins: {
    Camera: {
      permissions: ['camera']
    },
    Device: {},
    App: {},
    CapacitorHttp: {
      enabled: true
    },
    TextToSpeech: {
      // TTS 插件配置
    }
  },
  android: {
    allowMixedContent: true,
    captureInput: true,
    webContentsDebuggingEnabled: true,
    useLegacyBridge: false,
    backgroundColor: '#ffffff'
  }
}

export default config 