<template>
  <view class="container">
    <view 
      class="list-cell b-b" 
      hover-class="cell-hover" 
      hover-stay-time="50" 
      @tap="navTo('/pages/address/address')"
    >
      <text class="cell-tit">收货地址</text>
      <text class="cell-more yticon icon-you"></text>
    </view>
    
    <view class="list-cell m-t">
      <text class="cell-tit">消息推送</text>
      <switch checked color="#fa436a" @change="switchChange"></switch>
    </view>
    
    <view 
      class="list-cell b-b" 
      hover-class="cell-hover" 
      hover-stay-time="50" 
      @tap="navToOuter('https://github.com/macrozheng/mall')"
    >
      <text class="cell-tit">关于广横走文创</text>
      <text class="cell-more yticon icon-you"></text>
    </view>
    
    <view 
      class="list-cell b-b" 
      hover-class="cell-hover" 
      hover-stay-time="50" 
      @tap="checkUpdate"
    >
      <text class="cell-tit">检查更新</text>
      <text class="cell-tip">当前版本 {{ currentVersion }}</text>
      <text class="cell-more yticon icon-you"></text>
    </view>
    
    <view class="list-cell log-out-btn" @tap="toLogout">
      <text class="cell-tit">退出登录</text>
    </view>
  </view>
</template>

<script>
import updateManager from '@/utils/updateManager.js';

export default {
  data() {
    return {
      pushStatus: true,
      currentVersion: '1.0.0'
    }
  },
  onLoad() {
    this.getCurrentVersion();
  },
  methods: {
    /**
     * 获取当前版本号
     */
    getCurrentVersion() {
      // #ifdef MP-WEIXIN
      try {
        // 从 manifest.json 中获取版本号，或者从小程序基础库获取
        const accountInfo = uni.getAccountInfoSync();
        if (accountInfo && accountInfo.miniProgram && accountInfo.miniProgram.version) {
          this.currentVersion = accountInfo.miniProgram.version;
        }
      } catch (error) {
        console.error('获取版本号失败:', error);
      }
      // #endif
    },
    
    /**
     * 检查更新
     */
    checkUpdate() {
      // #ifdef MP-WEIXIN
      uni.showLoading({
        title: '检查更新中...'
      });
      
      // 延迟隐藏loading，给用户更好的反馈
      setTimeout(() => {
        uni.hideLoading();
        updateManager.manualCheckUpdate();
      }, 1000);
      // #endif
      
      // #ifndef MP-WEIXIN
      uni.showToast({
        title: '仅支持微信小程序',
        icon: 'none'
      });
      // #endif
    },
    
    /**
     * 跳转到其他页面
     * @param {Object} url
     */
    navTo(url) {
      uni.navigateTo({
        url
      })
    },
    
    /**
     * 跳转到外部链接
     * @param {Object} url
     */
    navToOuter(url) {
      uni.showModal({
        title: '提示',
        content: `是否打开外部链接？\n${url}`,
        success: (res) => {
          if (res.confirm) {
            // 在小程序中，需要使用特殊API
            // #ifdef MP-WEIXIN
            uni.setClipboardData({
              data: url,
              success: () => {
                uni.showToast({
                  title: '链接已复制',
                  icon: 'none'
                });
              }
            });
            // #endif
            
            // 在APP中可以直接打开
            // #ifdef APP-PLUS
            plus.runtime.openURL(url);
            // #endif
          }
        }
      });
    },
    
    /**
     * 切换消息推送开关
     */
    switchChange(e) {
      this.pushStatus = e.detail.value;
      // 实际应用中应该调用后端接口保存设置
    },
    
    /**
     * 退出登录
     */
    toLogout() {
      uni.showModal({
        title: '提示',
        content: '确定要退出登录吗？',
        success: (res) => {
          if (res.confirm) {
            // 清除本地存储的用户信息
            uni.removeStorageSync('token');
            uni.removeStorageSync('tokenInfo');
            uni.removeStorageSync('userInfo');
            
            // 跳转到登录页
            uni.reLaunch({
              url: '/pages/public/register'
            });
          }
        }
      });
    }
  }
}
</script>

<style lang="scss">
page {
  background: #f8f8f8;
}

.list-cell {
  display: flex;
  align-items: baseline;
  padding: 20rpx 30rpx;
  line-height: 60rpx;
  position: relative;
  background: #fff;
  justify-content: center;
  
  &.log-out-btn {
    margin-top: 40rpx;
    
    .cell-tit {
      color: #FF4225;
      text-align: center;
      margin-right: 0;
    }
  }
  
  &.cell-hover {
    background: #fafafa;
  }
  
  &.b-b:after {
    left: 30rpx;
  }
  
  &.m-t {
    margin-top: 16rpx;
  }
  
  .cell-more {
    align-self: baseline;
    font-size: 32rpx;
    color: #909399;
    margin-left: 10rpx;
  }
  
  .cell-tit {
    flex: 1;
    font-size: 30rpx;
    color: #333333;
    margin-right: 10rpx;
  }
  
  .cell-tip {
    font-size: 28rpx;
    color: #909399;
  }
  
  switch {
    transform: translateX(16rpx) scale(0.84);
  }
}
</style> 