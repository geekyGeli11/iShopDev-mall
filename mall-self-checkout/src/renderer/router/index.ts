/**
 * 路由配置
 */

import { createRouter, createWebHashHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/HomePage.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/LoginPage.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/scan',
    name: 'Scan',
    component: () => import('@/views/scan/ScanPage.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('@/views/cart/CartPage.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/payment',
    name: 'Payment',
    component: () => import('@/views/payment/PaymentPage.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/member',
    name: 'Member',
    component: () => import('@/views/member/MemberPage.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/receipt',
    name: 'Receipt',
    component: () => import('@/views/receipt/ReceiptPage.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import('@/views/settings/SettingsPage.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/RegisterPage.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/payment-result',
    name: 'PaymentResult',
    component: () => import('@/views/payment/PaymentResultPage.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/test/tts',
    name: 'TTSTest',
    component: () => import('@/views/test/TTSTestPage.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/test/audio',
    name: 'AudioTest',
    component: () => import('@/views/test/AudioTestPage.vue'),
    meta: { requiresAuth: false }
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 路由守卫：当进入首页时自动清理登录状态
router.beforeEach((to, from, next) => {
  // 当目标路由是首页时，处理自动退出逻辑
  if (to.path === '/home' || to.path === '/') {
    // 延迟导入store避免循环依赖
    import('@/store/modules/member').then(({ useMemberStore }) => {
      const memberStore = useMemberStore()
      
      // 如果是从支付、扫码等页面返回首页，且有登录状态，则自动退出
      if (memberStore.isLoggedIn && 
          (from.path === '/payment' || from.path === '/scan' || from.path === '/receipt')) {
        console.log(`从 ${from.path} 返回首页，自动退出登录`)
        memberStore.performLogout()
      }
    })
  }
  
  next()
})

export default router