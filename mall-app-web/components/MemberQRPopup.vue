<template>
  <view class="member-qr-popup" v-if="show">
    <view class="popup-mask" @click="closePopup"></view>
    <view class="popup-content">
      <!-- 关闭按钮 -->
      <view class="close-btn" @click="closePopup">
        <image class="close-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/product/close.png" mode="aspectFit" />
      </view>

      <!-- 卡片内容 -->
      <view class="card-wrapper" ref="cardWrapper">
        <!-- 会员信息 -->
        <view class="member-info">
          <text class="member-greeting">Hi, {{ userInfo.nickname }}</text>
          <text class="member-phone">TEL: {{ userInfo.phone }}</text>
        </view>

        <!-- 二维码区域 -->
        <view class="qr-code-container">
          <image
            v-if="memberData.qrCodeBase64"
            class="qr-code-image"
            :src="memberData.qrCodeBase64"
            mode="aspectFit"
          />
          <view v-else class="qr-code-placeholder">
            <text class="placeholder-text">二维码加载中...</text>
          </view>
        </view>
      </view>

      <!-- 底部按钮 -->
      <view class="button-group">
        <view class="btn-item" @click="saveToAlbum">
          <image class="btn-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/my/saveToAlbum.png" mode="aspectFit" />
          <text class="btn-text">保存到相册</text>
        </view>
        <view class="btn-item" @click="saveToAlbum">
          <image class="btn-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/my/share.png" mode="aspectFit" />
          <text class="btn-text">分享</text>
        </view>
      </view>
    </view>

    <!-- 隐藏的 canvas 用于生成图片，使用 type="2d" 支持同层渲染 -->
    <canvas type="2d" id="qrCodeCanvas" class="qr-canvas"></canvas>
  </view>
</template>

<script>
export default {
  name: 'MemberQRPopup',
  props: {
    show: {
      type: Boolean,
      default: false
    },
    memberData: {
      type: Object,
      default: () => ({
        memberCode: '',
        memberName: '',
        memberPhone: '',
        memberAvatar: '',
        qrCodeBase64: ''
      })
    }
  },

  data() {
    return {
      userInfo: {},
      shareImagePath: '', // 分享图片路径
      shareTitle: '' // 分享标题
    };
  },

  onShareAppMessage() {
    return {
      title: this.shareTitle || '会员二维码',
      path: '/pages/index/index',
      imageUrl: this.shareImagePath
    };
  },

  onShareTimeline() {
    return {
      title: this.shareTitle || '会员二维码',
      imageUrl: this.shareImagePath
    };
  },

  watch: {
    show(newVal) {
      if (newVal) {
        this.loadUserInfo();
      }
    }
  },

  mounted() {
    this.loadUserInfo();
  },

  methods: {
    // 从缓存获取用户信息
    loadUserInfo() {
      try {
        const userInfoStr = uni.getStorageSync('userInfo');
        if (userInfoStr) {
          this.userInfo = typeof userInfoStr === 'string' ? JSON.parse(userInfoStr) : userInfoStr;
        }
      } catch (error) {
        console.error('获取用户信息失败:', error);
      }
    },
    closePopup() {
      this.$emit('updateShow', false);
      this.$emit('close');
    },

    // 生成卡片图片 - 使用 Canvas 2D API
    async generateCardImage() {
      return new Promise((resolve) => {
        const query = uni.createSelectorQuery().in(this);
        query.select('#qrCodeCanvas')
          .fields({ node: true, size: true })
          .exec((res) => {
            if (!res || !res[0] || !res[0].node) {
              console.error('获取Canvas节点失败');
              uni.showToast({
                title: '获取画布失败',
                icon: 'none'
              });
              resolve(null);
              return;
            }

            const canvas = res[0].node;
            const ctx = canvas.getContext('2d');

            // 设置画布尺寸
            canvas.width = 273;
            canvas.height = 482.62;

            // 先加载背景图片
            const bgImage = canvas.createImage();
            bgImage.onload = () => {
              // 绘制背景
              ctx.drawImage(bgImage, 0, 0, canvas.width, canvas.height);

              // 绘制会员信息 - 根据 CSS 中 top: 250rpx 计算位置，再往下移动 25px
              // 250rpx ≈ 125px + 25px = 150px
              const memberInfoY = 150;
              ctx.fillStyle = '#333333';
              ctx.font = 'bold 16px sans-serif';
              ctx.textAlign = 'center';
              ctx.fillText(`Hi, ${this.userInfo.nickname || '会员'}`, canvas.width / 2, memberInfoY);

              ctx.fillStyle = '#666666';
              ctx.font = '12px sans-serif';
              ctx.fillText(`TEL: ${this.userInfo.phone || ''}`, canvas.width / 2, memberInfoY + 20);

              // 绘制二维码 - 二维码大小改为 380rpx ≈ 190px
              // 二维码往上移动，调整 Y 坐标
              const qrSize = 190;
              const qrX = (canvas.width - qrSize) / 2;
              const qrY = memberInfoY + 40 + 120 - qrSize / 2;

              if (this.memberData.qrCodeBase64) {
                const qrImage = canvas.createImage();
                qrImage.onload = () => {
                  ctx.drawImage(qrImage, qrX, qrY, qrSize, qrSize);
                  this.saveCanvasImage(canvas, resolve);
                };
                qrImage.onerror = () => {
                  console.error('二维码图片加载失败');
                  this.saveCanvasImage(canvas, resolve);
                };
                qrImage.src = this.memberData.qrCodeBase64;
              } else {
                this.saveCanvasImage(canvas, resolve);
              }
            };

            bgImage.onerror = () => {
              console.error('背景图片加载失败');
              // 使用填充色作为备用
              ctx.fillStyle = '#E2F1F5';
              ctx.fillRect(0, 0, canvas.width, canvas.height);

              // 绘制会员信息 - 往下移动 25px
              const memberInfoY = 150;
              ctx.fillStyle = '#333333';
              ctx.font = 'bold 16px sans-serif';
              ctx.textAlign = 'center';
              ctx.fillText(`Hi, ${this.userInfo.nickname || '会员'}`, canvas.width / 2, memberInfoY);

              ctx.fillStyle = '#666666';
              ctx.font = '12px sans-serif';
              ctx.fillText(`TEL: ${this.userInfo.phone || ''}`, canvas.width / 2, memberInfoY + 20);

              // 绘制二维码 - 大小改为 190px，往上移动
              const qrSize = 190;
              const qrX = (canvas.width - qrSize) / 2;
              const qrY = memberInfoY + 40 + 120 - qrSize / 2;

              if (this.memberData.qrCodeBase64) {
                const qrImage = canvas.createImage();
                qrImage.onload = () => {
                  ctx.drawImage(qrImage, qrX, qrY, qrSize, qrSize);
                  this.saveCanvasImage(canvas, resolve);
                };
                qrImage.onerror = () => {
                  this.saveCanvasImage(canvas, resolve);
                };
                qrImage.src = this.memberData.qrCodeBase64;
              } else {
                this.saveCanvasImage(canvas, resolve);
              }
            };

            bgImage.src = 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/my/qrcode_background_1.png';
          });
      });
    },

    // 保存 canvas 为图片
    saveCanvasImage(canvas, resolve) {
      wx.canvasToTempFilePath({
        canvas: canvas,
        success: (res) => {
          resolve(res.tempFilePath);
        },
        fail: (error) => {
          console.error('Canvas 转图片失败:', error);
          uni.showToast({
            title: '生成图片失败',
            icon: 'none'
          });
          resolve(null);
        }
      });
    },

    // 保存到相册
    async saveToAlbum() {
      uni.showLoading({
        title: '生成图片中...'
      });

      try {
        const imagePath = await this.generateCardImage();
        if (!imagePath) {
          uni.hideLoading();
          return;
        }

        uni.saveImageToPhotosAlbum({
          filePath: imagePath,
          success: () => {
            uni.hideLoading();
            uni.showToast({
              title: '已保存到相册',
              icon: 'success'
            });
          },
          fail: (error) => {
            uni.hideLoading();
            console.error('保存失败:', error);
            uni.showToast({
              title: '保存失败，请重试',
              icon: 'none'
            });
          }
        });
      } catch (error) {
        uni.hideLoading();
        console.error('保存到相册失败:', error);
        uni.showToast({
          title: '操作失败',
          icon: 'none'
        });
      }
    },

    // 分享到微信
    async shareToWechat() {
      uni.showLoading({
        title: '生成图片中...'
      });

      try {
        const imagePath = await this.generateCardImage();
        if (!imagePath) {
          uni.hideLoading();
          return;
        }

        // 保存图片路径供 onShareAppMessage 使用
        this.shareImagePath = imagePath;
        this.shareTitle = `${this.userInfo.nickname || '会员'}的会员二维码`;

        // 显示微信分享菜单
        wx.showShareMenu({
          withShareTicket: true,
          menus: ['shareAppMessage', 'shareTimeline'],
          success: () => {
            uni.hideLoading();
            uni.showToast({
              title: '请选择分享方式',
              icon: 'none'
            });
          },
          fail: (error) => {
            uni.hideLoading();
            console.error('显示分享菜单失败:', error);
            uni.showToast({
              title: '分享失败，请重试',
              icon: 'none'
            });
          }
        });
      } catch (error) {
        uni.hideLoading();
        console.error('分享失败:', error);
        uni.showToast({
          title: '操作失败',
          icon: 'none'
        });
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.member-qr-popup {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;

  .popup-mask {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.6);
  }

  .popup-content {
    position: relative;
    z-index: 10000;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: flex-start;

    .close-btn {
      position: absolute;
      top: 20rpx;
      right: 20rpx;
      width: 60rpx;
      height: 60rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      background: rgba(255, 255, 255, 0.9);
      border-radius: 50%;
      z-index: 1001;

      .close-icon {
        width: 36rpx;
        height: 36rpx;
      }
    }

    .card-wrapper {
      width: 273px;
      height: 482.62px;
      background-image: url('https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/my/qrcode_background_1.png');
      background-size: cover;
      background-position: center;
      background-repeat: no-repeat;
      border-radius: 24rpx;
      overflow: hidden;
      display: flex;
      flex-direction: column;
      align-items: center;
      position: relative;
      padding: 30rpx 20rpx;

      .member-info {
        position: absolute;
        top: 250rpx;
        left: 0;
        right: 0;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        z-index: 10;

        .member-greeting {
          font-size: 32rpx;
          font-weight: bold;
          color: #333333;
          margin-bottom: 8rpx;
        }

        .member-phone {
          font-size: 24rpx;
          color: #666666;
        }
      }

      .qr-code-container {
        width: 380rpx;
        height: 380rpx;
        border-radius: 16rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-top: 380rpx;
        position: relative;
        z-index: 5;

        .qr-code-image {
          width: 380rpx;
          height: 380rpx;
        }

        .qr-code-placeholder {
          display: flex;
          align-items: center;
          justify-content: center;
          width: 100%;
          height: 100%;

          .placeholder-text {
            font-size: 28rpx;
            color: #999999;
          }
        }
      }
    }

    .button-group {
      display: flex;
      align-items: center;
      justify-content: space-between;
      width: 273px;
      margin-top: 60rpx;
      z-index: 10001;
      padding: 0 40rpx;
      box-sizing: border-box;

      .btn-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        gap: 16rpx;
        cursor: pointer;

        .btn-icon {
          width: 102rpx;
          height: 102rpx;
          border-radius: 50%;
          box-sizing: border-box;
        }

        .btn-text {
          font-size: 28rpx;
          color: #FFFFFF;
          font-weight: 500;
        }

        &:active {
          opacity: 0.7;
        }
      }
    }
  }

  .qr-canvas {
    position: fixed;
    left: -2000px;
    top: -2000px;
    width: 273px;
    height: 482.62px;
    z-index: -1;
  }
}
</style>