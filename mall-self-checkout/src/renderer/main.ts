/**
 * Vue 应用入口
 */

import { createApp } from 'vue'
import { createPinia } from 'pinia'

// Capacitor
import { App as CapacitorApp } from '@capacitor/app'

// Vant
import Vant from 'vant'
import 'vant/lib/index.css'

// 引入全局样式 - 必须在其他样式之前引入
import './assets/styles/global.css'

import App from './App.vue'
import router from './router'
import { setRouterInstance } from './store/modules/member'

// 开发模式下导入API测试工具
if (import.meta.env.DEV) {
  import('./api/test-api')
}

const app = createApp(App)

// 状态管理
app.use(createPinia())

// 路由
app.use(router)

// 设置路由实例到memberStore
setRouterInstance(router)

// UI组件库
app.use(Vant)

// Capacitor返回按钮处理
CapacitorApp.addListener('backButton', () => {
  // 如果可以返回上一页，则返回
  if (router.currentRoute.value.name !== 'HomePage') {
    router.back()
  } else {
    // 如果在首页，则退出应用
    CapacitorApp.exitApp()
  }
})

app.mount('#app')