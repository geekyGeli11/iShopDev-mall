<template>
  <view class="invite-popup" v-if="visible" @click.stop="">
    <view class="invite-overlay" @click="handleClose"></view>
    <view class="invite-container">
      <!-- 关闭按钮 -->
      <view class="close-btn" @click="handleClose">
        <text class="close-icon">×</text>
      </view>
      
      <!-- 标题 -->
      <view class="invite-header">
        <text class="invite-title">邀请好友有礼</text>
        <text class="invite-subtitle">邀请好友注册，双方都有奖励</text>
      </view>
      
      <!-- 奖励展示 -->
      <view class="reward-section">
        <view class="reward-card">
          <view class="reward-item">
            <text class="reward-label">邀请奖励</text>
            <text class="reward-value">50积分</text>
          </view>
          <view class="reward-divider"></view>
          <view class="reward-item">
            <text class="reward-label">新人奖励</text>
            <text class="reward-value">100积分</text>
          </view>
        </view>
      </view>
      
      <!-- 邀请海报预览 -->
      <view class="poster-preview" v-if="posterUrl">
        <image class="poster-image" :src="posterUrl" mode="aspectFit"></image>
      </view>
      
      <!-- 分享方式 -->
      <view class="share-section">
        <button class="share-item share-button-item" open-type="share" @click="shareToFriend">
          <view class="share-icon-wrapper">
            <text class="share-icon">👥</text>
          </view>
          <text class="share-text">分享给好友</text>
        </button>
        
        <view class="share-item" @click="generatePoster">
          <view class="share-icon-wrapper">
            <text class="share-icon">📱</text>
          </view>
          <text class="share-text">生成海报</text>
        </view>
        
        <view class="share-item" @click="savePoster" v-if="posterUrl">
          <view class="share-icon-wrapper">
            <text class="share-icon">💾</text>
          </view>
          <text class="share-text">保存海报</text>
        </view>
      </view>
      
      <!-- 邀请统计 -->
      <view class="invite-stats">
        <text class="stats-text">已邀请 {{ inviteCount }} 人</text>
      </view>
    </view>
    
    <!-- 隐藏的Canvas -->
    <canvas canvas-id="posterCanvas" id="posterCanvas" style="position: fixed; top: -9999rpx; left: -9999rpx; width: 375px; height: 667px;"></canvas>
  </view>
</template>

<script>
import { mapState } from 'vuex';
import { generateInviteParam, getMyInviteStatistics, generateQrCode } from '@/api/invite.js';

export default {
  name: "invite-popup",
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    ...mapState(['hasLogin', 'userInfo'])
  },
  data() {
    return {
      posterUrl: '', // 海报图片URL
      qrCodeUrl: '', // 小程序码URL（单独保存）
      inviteCount: 0, // 邀请人数
      inviteParam: '', // 邀请参数
      loading: false
    };
  },
  watch: {
    visible(newVal) {
      if (newVal && this.hasLogin) {
        this.initInviteData();
      }
    }
  },
  
  // 分享配置
  onShareAppMessage(res) {
    console.log('邀请弹窗分享事件触发:', res);
    
    if (!this.hasLogin || !this.inviteParam) {
      return {
        title: '广横走文创',
        path: '/pages/new_index/index',
        imageUrl: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/logo.png'
      };
    }
    
    return {
      title: `${this.userInfo.nickname || '用户'}邀请您体验广横走文创`,
      path: `/pages/new_index/index?inviteParam=${this.inviteParam}`,
      imageUrl: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/logo.png'
    };
  },
  
  methods: {
    // 初始化邀请数据
    async initInviteData() {
      try {
        // 获取或生成邀请参数
        await this.generateInviteParam();
        // 获取邀请统计
        await this.getInviteStats();
      } catch (error) {
        console.error('初始化邀请数据失败', error);
      }
    },
    
    // 生成邀请参数
    async generateInviteParam() {
      try {
        const response = await generateInviteParam({
          sceneType: 1,
          generateQrCode: true,
          qrCodeSize: 430
        });
        
        if (response.code === 200) {
          const data = response.data;
          this.inviteParam = data.inviteParam;
          this.qrCodeUrl = data.qrCodeUrl; // 直接使用后端生成的小程序码
          
          // 立即生成完整海报用于显示
          await this.generateCompletePoster();
          
          console.log('生成邀请参数成功:', this.inviteParam);
        } else {
          throw new Error(response.message || '生成邀请参数失败');
        }
      } catch (error) {
        console.error('生成邀请参数失败', error);
        uni.showToast({
          title: '生成邀请失败',
          icon: 'none'
        });
        
        // 降级处理：使用本地生成的新格式临时参数
        const timestamp = Date.now();
        const userId = this.userInfo.id || '999999'; // 使用默认用户ID
        const randomString = this.generateRandomString(6);
        this.inviteParam = `INVITE_v2_${userId}_${timestamp}_${randomString}`;
      }
    },

    // 生成随机字符串
    generateRandomString(length) {
      const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
      let result = '';
      for (let i = 0; i < length; i++) {
        result += chars.charAt(Math.floor(Math.random() * chars.length));
      }
      return result;
    },

    // 获取邀请统计
    async getInviteStats() {
      try {
        const response = await getMyInviteStatistics();
        
        if (response.code === 200) {
          const data = response.data;
          this.inviteCount = data.totalInvites || 0;
          
          console.log('获取邀请统计成功:', data);
        } else {
          throw new Error(response.message || '获取邀请统计失败');
        }
      } catch (error) {
        console.error('获取邀请统计失败', error);
        // 降级处理：使用默认值
        this.inviteCount = 0;
      }
    },
    
    // 分享给好友（小程序卡片）
    shareToFriend() {
      if (!this.hasLogin) {
        uni.showToast({
          title: '请先登录',
          icon: 'none'
        });
        return;
      }
      
      // 触发页面的分享功能
      this.$emit('share', {
        type: 'friend',
        inviteParam: this.inviteParam
      });
      
      console.log('分享数据已设置，等待分享事件触发');
    },
    
    // 生成完整海报（带小程序码）
    async generateCompletePoster() {
      if (!this.qrCodeUrl) {
        console.warn('没有小程序码，跳过海报生成');
        return;
      }
      
      try {
        // 生成完整海报
        const posterUrl = await this.createPosterCanvas(this.qrCodeUrl);
        this.posterUrl = posterUrl;
      } catch (error) {
        console.error('生成完整海报失败', error);
        // 降级处理：直接使用小程序码
        this.posterUrl = this.qrCodeUrl;
      }
    },
    
    // 生成邀请海报（保持现有功能，但不覆盖posterUrl）
    async generatePoster() {
      if (!this.hasLogin) {
        uni.showToast({
          title: '请先登录',
          icon: 'none'
        });
        return;
      }
      
      if (this.loading) return;
      
      this.loading = true;
      uni.showLoading({
        title: '重新生成海报...'
      });
      
      try {
        // 如果还没有邀请参数，先生成
        if (!this.inviteParam) {
          await this.generateInviteParam();
          return; // generateInviteParam 已经会调用 generateCompletePoster
        }
        
        // 重新生成完整海报
        await this.generateCompletePoster();
        
        uni.hideLoading();
        uni.showToast({
          title: '海报重新生成成功',
          icon: 'success'
        });
      } catch (error) {
        console.error('重新生成海报失败', error);
        uni.hideLoading();
        uni.showToast({
          title: '生成海报失败',
          icon: 'none'
        });
      } finally {
        this.loading = false;
      }
    },
    
    // 创建海报Canvas
    async createPosterCanvas(qrCodeUrl) {
      return new Promise((resolve, reject) => {
        const ctx = uni.createCanvasContext('posterCanvas', this);
        const canvasWidth = 375;
        const canvasHeight = 667;
        
        // 设置背景
        ctx.setFillStyle('#FFFFFF');
        ctx.fillRect(0, 0, canvasWidth, canvasHeight);
        
        // 添加渐变背景
        ctx.setFillStyle('rgba(221, 255, 153, 0.8)');
        ctx.fillRect(0, 0, canvasWidth, 200);
        
        // 添加标题
        ctx.setFillStyle('#333333');
        ctx.setFontSize(24);
        ctx.setTextAlign('center');
        ctx.fillText('邀请您体验广横走文创', canvasWidth / 2, 50);
        
        // 添加用户昵称
        ctx.setFillStyle('#666666');
        ctx.setFontSize(18);
        ctx.fillText(`${this.userInfo.nickname || '用户'}邀请您`, canvasWidth / 2, 120);
        
        // 添加奖励信息
        ctx.setFillStyle('#333333');
        ctx.setFontSize(20);
        ctx.fillText('注册即得100积分', canvasWidth / 2, 160);
        
        // 小程序码区域
        const qrSize = 120;
        const qrX = canvasWidth / 2 - qrSize / 2;
        const qrY = canvasHeight - qrSize - 100;
        
        // 如果有小程序码URL，下载并绘制真实的小程序码
        if (qrCodeUrl) {
          // 先绘制占位背景
          ctx.setFillStyle('#F0F0F0');
          ctx.fillRect(qrX, qrY, qrSize, qrSize);
          
          // 下载并绘制小程序码
          uni.downloadFile({
            url: qrCodeUrl,
            success: (downloadRes) => {
              if (downloadRes.statusCode === 200) {
                ctx.drawImage(downloadRes.tempFilePath, qrX, qrY, qrSize, qrSize);
              }
              this.finishCanvas(ctx, canvasWidth, canvasHeight, qrY, qrSize, resolve, reject);
            },
            fail: () => {
              // 下载失败，使用占位区域
              console.warn('小程序码下载失败，使用占位区域');
              this.finishCanvas(ctx, canvasWidth, canvasHeight, qrY, qrSize, resolve, reject);
            }
          });
        } else {
          // 降级处理：绘制占位区域
          ctx.setFillStyle('#F0F0F0');
          ctx.fillRect(qrX, qrY, qrSize, qrSize);
          this.finishCanvas(ctx, canvasWidth, canvasHeight, qrY, qrSize, resolve, reject);
        }
      });
    },
    
    // 完成Canvas绘制的公共方法
    finishCanvas(ctx, canvasWidth, canvasHeight, qrY, qrSize, resolve, reject) {
      // 添加小程序码说明
      ctx.setFillStyle('#999999');
      ctx.setFontSize(14);
      ctx.setTextAlign('center');
      ctx.fillText('长按识别小程序码', canvasWidth / 2, qrY + qrSize + 20);
      
      // 添加底部文案
      ctx.setFillStyle('#999999');
      ctx.setFontSize(12);
      ctx.fillText('广横走文创 · 品质生活', canvasWidth / 2, canvasHeight - 20);
      
      ctx.draw(true, () => {
        // 延迟一下再生成图片，确保图片绘制完成
        setTimeout(() => {
          uni.canvasToTempFilePath({
            canvasId: 'posterCanvas',
            success: (res) => {
              resolve(res.tempFilePath);
            },
            fail: reject
          }, this);
        }, 800);
      });
    },
    
    // 保存海报
    savePoster() {
      if (!this.posterUrl) {
        uni.showToast({
          title: '请先生成海报',
          icon: 'none'
        });
        return;
      }
      
      uni.saveImageToPhotosAlbum({
        filePath: this.posterUrl,
        success: () => {
          uni.showToast({
            title: '保存成功',
            icon: 'success'
          });
        },
        fail: (err) => {
          if (err.errMsg.includes('auth deny') || err.errMsg.includes('authorize')) {
            uni.showModal({
              title: '提示',
              content: '需要授权访问相册才能保存图片',
              confirmText: '去设置',
              success: (res) => {
                if (res.confirm) {
                  uni.openSetting();
                }
              }
            });
          } else {
            uni.showToast({
              title: '保存失败',
              icon: 'none'
            });
          }
        }
      });
    },
    
    // 关闭弹窗
    handleClose() {
      // 不清理 posterUrl，保持海报状态
      this.$emit('close');
    }
  }
};
</script>

<style lang="scss" scoped>
.invite-popup {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.invite-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
}

.invite-container {
  position: relative;
  width: 600rpx;
  max-height: 80%;
  background: #fff;
  border-radius: 20rpx;
  padding: 40rpx 30rpx;
  margin: 0 30rpx;
  overflow-y: auto;
}

.close-btn {
  position: absolute;
  top: 15rpx;
  right: 20rpx;
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
}

.close-icon {
  font-size: 48rpx;
  color: #999;
  line-height: 1;
}

.invite-header {
  text-align: center;
  margin-bottom: 30rpx;
}

.invite-title {
  font-size: 36rpx;
  font-weight: 600;
  color: #333;
  display: block;
  margin-bottom: 10rpx;
}

.invite-subtitle {
  font-size: 28rpx;
  color: #666;
  display: block;
}

.reward-section {
  margin-bottom: 30rpx;
}

.reward-card {
  background: linear-gradient(135deg, #C8FF3F 0%, #FBFFF3 100%);
  border-radius: 16rpx;
  padding: 30rpx;
  display: flex;
  align-items: center;
  justify-content: space-around;
}

.reward-item {
  text-align: center;
  flex: 1;
}

.reward-label {
  font-size: 28rpx;
  color: #666;
  display: block;
  margin-bottom: 8rpx;
}

.reward-value {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
  display: block;
}

.reward-divider {
  width: 2rpx;
  height: 60rpx;
  background: rgba(0, 0, 0, 0.1);
}

.poster-preview {
  margin-bottom: 30rpx;
  text-align: center;
}

.poster-image {
  width: 300rpx;
  height: 400rpx;
  border-radius: 12rpx;
  border: 2rpx solid #eee;
}

.share-section {
  display: flex;
  justify-content: space-around;
  margin-bottom: 30rpx;
}

.share-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16rpx;
  flex: 1;
}

.share-button {
  background: none;
  border: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16rpx;
  width: 100%;
  font-size: inherit;
  color: inherit;
  line-height: 1;
}

.share-button::after {
  border: none;
}

/* 分享按钮样式 */
.share-button-item {
  background: none;
  border: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16rpx;
  flex: 1;
  font-size: inherit;
  line-height: 1;
  min-height: 120rpx;
}

.share-button-item::after {
  border: none;
}

/* 确保button内的文字正确显示 */
.share-button .share-text,
.share-button-item .share-text {
  font-size: 24rpx !important;
  color: #666 !important;
  display: block !important;
}

.share-icon-wrapper {
  width: 80rpx;
  height: 80rpx;
  min-width: 80rpx;
  min-height: 80rpx;
  background: #f8f8f8;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.share-icon {
  font-size: 32rpx;
}

.share-text {
  font-size: 24rpx;
  color: #666;
}

.invite-stats {
  text-align: center;
  padding-top: 20rpx;
  border-top: 2rpx solid #f0f0f0;
}

.stats-text {
  font-size: 24rpx;
  color: #999;
}
</style> 