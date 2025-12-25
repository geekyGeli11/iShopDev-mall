<template>
  <view class="customer-service-page">
    <!-- 导航栏 -->
    <nav-bar :back="true" :placeholder="false" bgColor="transparent" title="客服"></nav-bar>

    <!-- 渐变背景 -->
    <view class="gradient-bg"></view>

    <!-- 页面内容 -->
    <view class="page-content">
      <!-- 企业微信二维码（上方） -->
      <view v-if="serviceInfo.enterpriseWechatQrcodeUrl" class="qrcode-section enterprise-qrcode">
        <view class="qrcode-bg-container">
          <image 
            class="qrcode-bg" 
            src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/product/qr_bg_0.png"
            mode="aspectFit"
          ></image>
          <image 
            :src="serviceInfo.enterpriseWechatQrcodeUrl" 
            class="qrcode-image enterprise-qrcode-image" 
            mode="aspectFit"
            :show-menu-by-longpress="true"
          ></image>
        </view>
      </view>

      <!-- 微信二维码（下方） -->
      <view v-if="serviceInfo.wechatQrcodeUrl" class="qrcode-section wechat-qrcode">
        <view class="qrcode-bg-container">
          <image 
            class="qrcode-bg" 
            src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/product/qr_bg_1.png"
            mode="aspectFit"
          ></image>
          <image 
            :src="serviceInfo.wechatQrcodeUrl" 
            class="qrcode-image wechat-qrcode-image" 
            mode="aspectFit"
            :show-menu-by-longpress="true"
          ></image>
          
          <!-- 微信号复制区域（覆盖在微信二维码背景上） -->
          <view v-if="serviceInfo.wechatAccount" class="wechat-account-section">
            <button class="copy-btn" @tap="copyWechatAccount">
              <text class="copy-btn-text">复制微信号</text>
            </button>
          </view>
        </view>
      </view>

      <!-- 加载状态 -->
      <view v-if="loading" class="loading-container">
        <text class="loading-text">加载中...</text>
      </view>

      <!-- 错误状态 -->
      <view v-if="error && !loading" class="error-container">
        <text class="error-text">{{ error }}</text>
        <button class="retry-btn" @tap="loadCustomerServiceInfo">重试</button>
      </view>
    </view>
  </view>
</template>

<script>
import { getCustomerServiceInfo } from '@/api/customerService'
import navBar from '@/components/nav-bar'

export default {
  name: 'CustomerServicePage',
  components: {
    'nav-bar': navBar
  },
  data() {
    return {
      serviceInfo: {
        wechatQrcodeUrl: '',
        wechatAccount: '',
        enterpriseWechatQrcodeUrl: '',
        servicePhone: '',
        serviceHours: '',
        serviceDescription: '',
        isEnabled: 1
      },
      loading: false,
      error: ''
    }
  },
  onLoad() {
    this.loadCustomerServiceInfo()
  },
  methods: {
    // 加载客服信息
    loadCustomerServiceInfo() {
      if (this.loading) return

      this.loading = true
      this.error = ''

      getCustomerServiceInfo()
        .then(response => {
          if (response.code === 200 && response.data) {
            this.serviceInfo = {
              wechatQrcodeUrl: response.data.wechatQrcodeUrl || '',
              wechatAccount: response.data.wechatAccount || '',
              enterpriseWechatQrcodeUrl: response.data.enterpriseWechatQrcodeUrl || '',
              servicePhone: response.data.servicePhone || '',
              serviceHours: response.data.serviceHours || '',
              serviceDescription: response.data.serviceDescription || '',
              isEnabled: response.data.isEnabled || 1
            }
          } else {
            this.error = '获取客服信息失败'
          }
        })
        .catch(error => {
          console.error('加载客服信息出错:', error)
          this.error = '加载客服信息失败，请重试'
        })
        .finally(() => {
          this.loading = false
        })
    },
    // 复制微信号
    copyWechatAccount() {
      if (!this.serviceInfo.wechatAccount) return

      uni.setClipboardData({
        data: this.serviceInfo.wechatAccount,
        success: () => {
          uni.showToast({
            title: '微信号已复制',
            icon: 'success',
            duration: 1500
          })
        },
        fail: () => {
          uni.showToast({
            title: '复制失败，请手动复制',
            icon: 'none'
          })
        }
      })
    }
  }
}
</script>

<style scoped>
.customer-service-page {
  width: 100%;
  height: 100vh;
  background-color: #fff;
  display: flex;
  flex-direction: column;
  position: relative;
}

/* 渐变背景 */
.gradient-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 198px;
  background: linear-gradient(180deg, rgba(221, 255, 153, 0.8) 0%, rgba(221, 255, 153, 0) 100%);
  pointer-events: none;
  z-index: 1;
}

/* 页面内容 */
.page-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 40px 0px;
  box-sizing: border-box;
  overflow-y: auto;
  position: relative;
  z-index: 2;
}

/* 二维码区域 */
.qrcode-section {
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin-bottom: 30px;
}

/* 企业微信二维码往下移动 */
.enterprise-qrcode {
  margin-top: 40px;
}

.qrcode-bg-container {
  position: relative;
  width: 100%;
  max-width: 320px;
  height: 280px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.qrcode-bg {
  position: absolute;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.qrcode-image {
  width: 140px;
  height: 140px;
  position: absolute;
  z-index: 2;
  top: 50%;
  transform: translateY(calc(-50% - 20px));
}

/* 企业微信二维码定位 */
.enterprise-qrcode-image {
  left: 50%;
  transform: translate(calc(-50% - 10px), calc(-50% - 20px));
}

/* 微信二维码定位 */
.wechat-qrcode-image {
  left: 53%;
  transform: translate(calc(-50% - 10px), calc(-50% - 20px));
}

.wechat-account-section {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 3;
  display: flex;
  justify-content: center;
  align-items: center;
}

.copy-btn {
  padding: 8px 16px;
  background-color: #000000;
  color: #a9ff00;
  border: none;
  border-radius: 16px;
  font-size: 14px;
  font-weight: 400;
  font-style: normal;
  display: flex;
  justify-content: center;
  align-items: center;
  white-space: nowrap;
}

.copy-btn-text {
  text-align: center;
  line-height: 1;
  color: #a9ff00;
}

/* 加载状态 */
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px;
}

.loading-text {
  font-size: 14px;
  color: #999;
}

/* 错误状态 */
.error-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 40px;
}

.error-text {
  font-size: 14px;
  color: #999;
  margin-bottom: 20px;
  text-align: center;
}

.retry-btn {
  padding: 10px 20px;
  background-color: #a8ff00;
  color: #000;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: bold;
}
</style>
