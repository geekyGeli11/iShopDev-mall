<template>
  <view v-if="visible" class="poster-dialog">
    <!-- 遮罩层 -->
    <view class="dialog-mask" @tap="handleMaskTap"></view>
    
    <!-- 弹窗内容 -->
    <view class="dialog-content">
      <!-- 弹窗头部 -->
      <view class="dialog-header">
        <text class="dialog-title">推广海报</text>
        <view class="close-btn" @tap="handleClose">
          <text class="close-icon">✕</text>
        </view>
      </view>
      
      <!-- 海报展示区 -->
      <view class="poster-wrapper">
        <view class="poster-container" id="posterContainer">
          <!-- 海报背景 -->
          <view class="poster-background">
            <!-- 顶部装饰 -->
            <view class="poster-header">
              <text class="app-name">广恒州商城</text>
              <text class="slogan">优质商品，超值体验</text>
            </view>
            
            <!-- 用户信息区 -->
            <view class="user-info">
              <image class="user-avatar" :src="userInfo.avatar || 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/images/default-avatar.png'"></image>
              <text class="user-name">{{ userInfo.nickname || '分销商' }}</text>
              <text class="invite-text">邀请您一起购物赚钱</text>
            </view>
            
            <!-- 商品推荐区 -->
            <view class="product-showcase" v-if="recommendProducts.length > 0">
              <text class="showcase-title">精选推荐</text>
              <view class="product-grid">
                <view 
                  v-for="(product, index) in recommendProducts.slice(0, 4)" 
                  :key="index"
                  class="product-item"
                >
                  <image class="product-image" :src="product.image"></image>
                  <text class="product-price">¥{{ product.price }}</text>
                </view>
              </view>
            </view>
            
            <!-- 优惠信息 -->
            <view class="promotion-info">
              <view class="promo-item">
                <text class="promo-icon">🎁</text>
                <text class="promo-text">新用户专享优惠券</text>
              </view>
              <view class="promo-item">
                <text class="promo-icon">💰</text>
                <text class="promo-text">购物返现金</text>
              </view>
              <view class="promo-item">
                <text class="promo-icon">🔥</text>
                <text class="promo-text">限时特价商品</text>
              </view>
            </view>
            
            <!-- 二维码区域 -->
            <view class="qrcode-area">
              <view class="qrcode-wrapper">
                <canvas 
                  class="qrcode-canvas" 
                  canvas-id="qrcodeCanvas"
                  :style="{width: qrcodeSize + 'px', height: qrcodeSize + 'px'}"
                ></canvas>
              </view>
              <text class="qrcode-text">长按识别二维码</text>
              <text class="qrcode-subtext">立即享受优惠</text>
            </view>
            
            <!-- 底部信息 -->
            <view class="poster-footer">
              <text class="footer-text">邀请码：{{ userInfo.inviteCode || 'ABC123' }}</text>
              <text class="footer-text">广恒州商城 · 让购物更有价值</text>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 操作按钮 -->
      <view class="action-buttons">
        <button class="action-btn save-btn" @tap="savePoster">
          <text class="btn-icon">📱</text>
          <text class="btn-text">保存到相册</text>
        </button>
        <button class="action-btn share-btn" @tap="sharePoster">
          <text class="btn-icon">📤</text>
          <text class="btn-text">分享海报</text>
        </button>
      </view>
      
      <!-- 分享提示 -->
      <view class="share-tips">
        <text class="tips-title">分享推广小贴士：</text>
        <text class="tips-item">• 分享到朋友圈、微信群效果更佳</text>
        <text class="tips-item">• 添加个人推荐语增加吸引力</text>
        <text class="tips-item">• 好友通过您的推广购买即可获得佣金</text>
      </view>
    </view>
  </view>
</template>

<script>
import { getUserPromoCode, getRecommendProducts } from '@/api/distribution.js'

export default {
  name: 'PosterDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    userInfo: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      qrcodeSize: 120,
      recommendProducts: [],
      generating: false
    }
  },
  watch: {
    visible(newVal) {
      if (newVal) {
        this.initPoster();
      }
    }
  },
  methods: {
    // 初始化海报
    async initPoster() {
      try {
        this.generating = true;
        
        // 加载推荐商品
        await this.loadRecommendProducts();
        
        // 生成二维码
        await this.generateQRCode();
        
      } catch (error) {
        console.error('初始化海报失败:', error);
        uni.showToast({
          title: '海报生成失败',
          icon: 'none'
        });
      } finally {
        this.generating = false;
      }
    },
    
    // 加载推荐商品
    async loadRecommendProducts() {
      try {
        const result = await getRecommendProducts({ limit: 4 });
        if (result.code === 200) {
          this.recommendProducts = result.data.list || [];
        }
      } catch (error) {
        console.error('加载推荐商品失败:', error);
        // 使用默认商品数据
        this.recommendProducts = [
          {
            image: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com /static/images/product-placeholder.png',
            price: '99.00'
          },
          {
            image: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com /static/images/product-placeholder.png', 
            price: '159.00'
          },
          {
            image: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com /static/images/product-placeholder.png',
            price: '239.00'
          },
          {
            image: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com /static/images/product-placeholder.png',
            price: '89.00'
          }
        ];
      }
    },
    
    // 生成二维码
    async generateQRCode() {
      try {
        const ctx = uni.createCanvasContext('qrcodeCanvas', this);
        
        // 生成推广链接
        const promoLink = this.generatePromoLink();
        
        // 这里应该调用二维码生成库，简化处理
        // 实际项目中建议使用 uQRCode 或其他二维码库
        
        // 绘制简单的二维码占位符
        ctx.fillStyle = '#000';
        ctx.fillRect(0, 0, this.qrcodeSize, this.qrcodeSize);
        
        // 绘制白色边框
        ctx.fillStyle = '#fff';
        ctx.fillRect(10, 10, this.qrcodeSize - 20, this.qrcodeSize - 20);
        
        // 绘制中心图案
        ctx.fillStyle = '#000';
        const centerSize = 30;
        const centerX = (this.qrcodeSize - centerSize) / 2;
        const centerY = (this.qrcodeSize - centerSize) / 2;
        ctx.fillRect(centerX, centerY, centerSize, centerSize);
        
        ctx.draw();
        
      } catch (error) {
        console.error('生成二维码失败:', error);
      }
    },
    
    // 生成推广链接
    generatePromoLink() {
      const baseUrl = 'https://mall.example.com';
      const inviteCode = this.userInfo.inviteCode || 'ABC123';
      return `${baseUrl}?invite=${inviteCode}`;
    },
    
    // 保存海报到相册
    async savePoster() {
      try {
        // 检查相册权限
        const authResult = await this.checkPhotoAuth();
        if (!authResult) {
          return;
        }
        
        uni.showLoading({
          title: '正在保存...'
        });
        
        // 生成海报图片
        const tempFilePath = await this.generatePosterImage();
        
        // 保存到相册
        await new Promise((resolve, reject) => {
          uni.saveImageToPhotosAlbum({
            filePath: tempFilePath,
            success: resolve,
            fail: reject
          });
        });
        
        uni.hideLoading();
        uni.showToast({
          title: '保存成功',
          icon: 'success'
        });
        
      } catch (error) {
        uni.hideLoading();
        console.error('保存海报失败:', error);
        uni.showToast({
          title: '保存失败',
          icon: 'none'
        });
      }
    },
    
    // 分享海报
    async sharePoster() {
      try {
        uni.showLoading({
          title: '正在生成...'
        });
        
        // 生成海报图片
        const tempFilePath = await this.generatePosterImage();
        
        uni.hideLoading();
        
        // 调用分享
        uni.share({
          provider: 'weixin',
          scene: 'WXSceneTimeline',
          type: 0,
          imageUrl: tempFilePath,
          success: () => {
            uni.showToast({
              title: '分享成功',
              icon: 'success'
            });
          },
          fail: (error) => {
            console.error('分享失败:', error);
            uni.showToast({
              title: '分享失败',
              icon: 'none'
            });
          }
        });
        
      } catch (error) {
        uni.hideLoading();
        console.error('分享海报失败:', error);
        uni.showToast({
          title: '分享失败',
          icon: 'none'
        });
      }
    },
    
    // 检查相册权限
    async checkPhotoAuth() {
      try {
        const result = await new Promise((resolve) => {
          uni.getSetting({
            success: (res) => {
              resolve(res.authSetting['scope.writePhotosAlbum']);
            },
            fail: () => resolve(false)
          });
        });
        
        if (result === false) {
          // 权限被拒绝，引导用户设置
          const confirmResult = await new Promise((resolve) => {
            uni.showModal({
              title: '权限提示',
              content: '需要相册权限来保存图片，请在设置中开启',
              confirmText: '去设置',
              success: (res) => resolve(res.confirm),
              fail: () => resolve(false)
            });
          });
          
          if (confirmResult) {
            uni.openSetting();
          }
          return false;
        }
        
        if (result === undefined) {
          // 未授权，申请权限
          return new Promise((resolve) => {
            uni.authorize({
              scope: 'scope.writePhotosAlbum',
              success: () => resolve(true),
              fail: () => resolve(false)
            });
          });
        }
        
        return true;
        
      } catch (error) {
        console.error('检查相册权限失败:', error);
        return false;
      }
    },
    
    // 生成海报图片
    async generatePosterImage() {
      return new Promise((resolve, reject) => {
        // 使用 canvas 生成海报图片
        const query = uni.createSelectorQuery().in(this);
        query.select('#posterContainer').boundingClientRect((rect) => {
          
          // 创建 canvas 绘制海报
          const ctx = uni.createCanvasContext('posterCanvas', this);
          
          // 这里需要实现具体的海报绘制逻辑
          // 包括背景、文字、图片、二维码等元素的绘制
          
          // 简化处理，返回临时文件路径
          setTimeout(() => {
            resolve('https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/temp/poster.png');
          }, 1000);
          
        }).exec();
      });
    },
    
    // 遮罩点击
    handleMaskTap() {
      // 不允许点击遮罩关闭
    },
    
    // 关闭弹窗
    handleClose() {
      this.$emit('close');
    }
  }
}
</script>

<style lang="scss" scoped>
.poster-dialog {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.dialog-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
}

.dialog-content {
  position: relative;
  width: 90%;
  max-width: 700rpx;
  max-height: 90vh;
  background: #fff;
  border-radius: 20rpx;
  overflow: hidden;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx 40rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.dialog-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.close-btn {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: #f5f5f5;
}

.close-icon {
  font-size: 32rpx;
  color: #666;
}

.poster-wrapper {
  padding: 40rpx;
  max-height: 60vh;
  overflow-y: auto;
}

.poster-container {
  width: 100%;
  background: #fff;
  border-radius: 16rpx;
  overflow: hidden;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);
}

.poster-background {
  background: linear-gradient(135deg, #A9FF00 0%, #647D00 100%);
  color: #000000;
  position: relative;
  padding: 40rpx;
}

.poster-header {
  text-align: center;
  margin-bottom: 40rpx;
}

.app-name {
  display: block;
  font-size: 40rpx;
  font-weight: bold;
  margin-bottom: 16rpx;
}

.slogan {
  font-size: 26rpx;
  opacity: 0.9;
}

.user-info {
  text-align: center;
  margin-bottom: 40rpx;
}

.user-avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  border: 4rpx solid rgba(255, 255, 255, 0.3);
  margin-bottom: 20rpx;
}

.user-name {
  display: block;
  font-size: 32rpx;
  font-weight: bold;
  margin-bottom: 8rpx;
}

.invite-text {
  font-size: 24rpx;
  opacity: 0.8;
}

.product-showcase {
  margin-bottom: 40rpx;
}

.showcase-title {
  display: block;
  font-size: 28rpx;
  font-weight: bold;
  text-align: center;
  margin-bottom: 24rpx;
}

.product-grid {
  display: flex;
  gap: 16rpx;
  justify-content: center;
}

.product-item {
  width: 120rpx;
  text-align: center;
}

.product-image {
  width: 100rpx;
  height: 100rpx;
  border-radius: 12rpx;
  background: rgba(255, 255, 255, 0.2);
  margin-bottom: 8rpx;
}

.product-price {
  font-size: 22rpx;
  font-weight: bold;
}

.promotion-info {
  margin-bottom: 40rpx;
}

.promo-item {
  display: flex;
  align-items: center;
  margin-bottom: 16rpx;
  
  &:last-child {
    margin-bottom: 0;
  }
}

.promo-icon {
  font-size: 32rpx;
  margin-right: 16rpx;
}

.promo-text {
  font-size: 26rpx;
}

.qrcode-area {
  text-align: center;
  margin-bottom: 40rpx;
}

.qrcode-wrapper {
  display: inline-block;
  padding: 20rpx;
  background: #fff;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
}

.qrcode-canvas {
  display: block;
}

.qrcode-text {
  display: block;
  font-size: 26rpx;
  font-weight: bold;
  margin-bottom: 8rpx;
}

.qrcode-subtext {
  font-size: 22rpx;
  opacity: 0.8;
}

.poster-footer {
  text-align: center;
  border-top: 1rpx solid rgba(255, 255, 255, 0.3);
  padding-top: 30rpx;
}

.footer-text {
  display: block;
  font-size: 22rpx;
  opacity: 0.8;
  margin-bottom: 8rpx;
  
  &:last-child {
    margin-bottom: 0;
  }
}

.action-buttons {
  display: flex;
  gap: 20rpx;
  padding: 30rpx 40rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.action-btn {
  flex: 1;
  height: 88rpx;
  border-radius: 12rpx;
  border: none;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  
  &.save-btn {
    background: #A9FF00;
    color: #000000;
  }
  
  &.share-btn {
    background: #647D00;
    color: #FFFFFF;
  }
}

.btn-icon {
  font-size: 32rpx;
}

.btn-text {
  font-size: 24rpx;
}

.share-tips {
  padding: 30rpx 40rpx;
  background: #f8f9fa;
}

.tips-title {
  display: block;
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 16rpx;
}

.tips-item {
  display: block;
  font-size: 24rpx;
  color: #666;
  line-height: 1.6;
  margin-bottom: 8rpx;
  
  &:last-child {
    margin-bottom: 0;
  }
}
</style> 