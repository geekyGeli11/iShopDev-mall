<template>
  <view class="team-container">
    <image class="bg-image" src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/team/team.png" mode="widthFix"></image>
    
    <view class="qrcode-container">
      <image class="qrcode" :src="qrcode" mode="aspectFit"></image>
    </view>
    
    <view class="share-buttons">
      <button class="share-btn poster-btn" @tap="generatePoster">分享海报</button>
      <button class="share-btn friend-btn" open-type="share">分享给好友</button>
    </view>
    
    <view class="stats-container">
      <view class="stat-item">
        <view class="stat-num">{{ teamStats.points || 0 }}</view>
        <view class="stat-label">好友点击</view>
      </view>
      <view class="stat-item">
        <view class="stat-num">{{ teamStats.friends || 0 }}</view>
        <view class="stat-label">好友下单</view>
      </view>
      <view class="stat-item">
        <view class="stat-num">{{ teamStats.visits || 0 }}</view>
        <view class="stat-label">获得推广</view>
      </view>
    </view>
    
    <canvas class="poster-canvas" type="2d" id="posterCanvas"></canvas>
  </view>
</template>

<script>
import { getTeamStatistics, getTeamQrCode } from '@/api/team';
import { generateQrCode } from '@/api/common';

export default {
  data() {
    return {
      qrcode: '', // 二维码图片链接
      teamStats: {
        points: 0,
        friends: 0,
        visits: 0
      },
      isLoading: false,
      posterGenerated: false
    };
  },
  
  onLoad() {
    this.loadTeamStats();
    this.loadTeamQrCode();
  },
  
  onShareAppMessage() {
    return {
      title: '邀请您加入我的团队',
      path: '/pages/new_index/index?inviteCode=' + this.getInviteCode()
    };
  },
  
  methods: {
    // 加载团队统计数据
    loadTeamStats() {
      this.isLoading = true;
      getTeamStatistics().then(res => {
        if (res.code === 200 && res.data) {
          this.teamStats = {
            points: res.data.clickCount || 0,
            friends: res.data.orderCount || 0,
            visits: res.data.visitCount || 0
          };
        } else {
          // 如果API失败使用默认值
          this.teamStats = {
            points: 0,
            friends: 0,
            visits: 0
          };
        }
      }).catch(() => {
        // 如果API失败使用默认值
        this.teamStats = {
          points: 0,
          friends: 0,
          visits: 0
        };
      }).finally(() => {
        this.isLoading = false;
      });
    },
    
    // 加载团队二维码
    loadTeamQrCode() {
      this.isLoading = true;
      
      // 尝试从API获取团队二维码
      getTeamQrCode().then(res => {
        if (res instanceof ArrayBuffer) {
          // 转换ArrayBuffer为Base64
          let binary = '';
          const bytes = new Uint8Array(res);
          for (let i = 0; i < bytes.byteLength; i++) {
            binary += String.fromCharCode(bytes[i]);
          }
          this.qrcode = 'data:image/png;base64,' + uni.arrayBufferToBase64(res);
        } else {
          // 如果不是ArrayBuffer，生成本地二维码
          this.generateLocalQrCode();
        }
      }).catch(() => {
        // 如果API失败，生成本地二维码
        this.generateLocalQrCode();
      }).finally(() => {
        this.isLoading = false;
      });
    },
    
    // 生成本地二维码
    generateLocalQrCode() {
      const inviteCode = this.getInviteCode();
      const inviteUrl = `https://haojiang.com/invite?code=${inviteCode}`;
      
      const params = {
        scene: inviteCode,
        page: 'pages/new_index/index'
      };
      
      generateQrCode(params).then(res => {
        if (res instanceof ArrayBuffer) {
          this.qrcode = 'data:image/png;base64,' + uni.arrayBufferToBase64(res);
        } else {
          // 如果失败，使用默认二维码图片
          this.qrcode = 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/product/source.png';
        }
      }).catch(() => {
        // 如果失败，使用默认二维码图片
        this.qrcode = 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/product/source.png';
      });
    },
    
    // 获取邀请码
    getInviteCode() {
      const userInfo = uni.getStorageSync('userInfo');
      return userInfo?.userId || '123456';
    },
    
    // 生成海报
    generatePoster() {
      const that = this;
      const inviteCode = this.getInviteCode();
      
      uni.showLoading({
        title: '生成海报中...'
      });
      
      // 获取 team-container 的尺寸信息
      uni.createSelectorQuery()
        .select('.team-container')
        .boundingClientRect(containerInfo => {
          if (!containerInfo) {
            uni.hideLoading();
            uni.showToast({
              title: '获取页面元素失败',
              icon: 'none'
            });
            return;
          }
          
          // 创建绘制上下文
          uni.createSelectorQuery()
            .select('#posterCanvas')
            .fields({ node: true, size: true })
            .exec((res) => {
              if (!res[0]?.node) {
                uni.hideLoading();
                uni.showToast({
                  title: '生成海报失败',
                  icon: 'none'
                });
                return;
              }
              
              const canvas = res[0].node;
              const ctx = canvas.getContext('2d');
              
              // 设置画布尺寸与 team-container 一致
              canvas.width = containerInfo.width * 2; // 乘以2提高清晰度
              canvas.height = containerInfo.height * 2;
              
              // 清空画布
              ctx.fillStyle = '#f5f7f9';
              ctx.fillRect(0, 0, canvas.width, canvas.height);
              
              // 绘制背景图片
              this.drawBackgroundImage(ctx, canvas, () => {
                // 绘制二维码
                this.drawQrCode(ctx, canvas, () => {
                  // 绘制分享按钮
                  this.drawShareButtons(ctx, canvas);
                  
                  // 绘制数据统计
                  this.drawStatistics(ctx, canvas);                                
                  
                  // 导出海报图片
                  uni.canvasToTempFilePath({
                    canvas: canvas,
                    success: function(res) {
                      that.posterGenerated = true;
                      
                      // 保存到相册
                      uni.saveImageToPhotosAlbum({
                        filePath: res.tempFilePath,
                        success: function() {
                          uni.hideLoading();
                          uni.showToast({
                            title: '已保存到相册',
                            icon: 'success'
                          });
                        },
                        fail: function() {
                          uni.hideLoading();
                          uni.showToast({
                            title: '保存图片失败',
                            icon: 'none'
                          });
                        }
                      });
                    },
                    fail: function() {
                      uni.hideLoading();
                      uni.showToast({
                        title: '生成海报失败',
                        icon: 'none'
                      });
                    }
                  });
                });
              });
            });
        })
        .exec();
    },
    
    // 绘制背景图片
    drawBackgroundImage(ctx, canvas, callback) {
      const bgImage = canvas.createImage();
      bgImage.onload = () => {
        // 计算图片比例，确保完整显示
        const bgWidth = canvas.width;
        const bgHeight = bgWidth * (bgImage.height / bgImage.width);
        
        // 绘制背景图片
        ctx.drawImage(bgImage, 0, 0, bgWidth, bgHeight);
        callback && callback();
      };
      
      bgImage.onerror = () => {
        console.error('背景图片加载失败');
        // 使用纯色背景
        ctx.fillStyle = '#f5f7f9';
        ctx.fillRect(0, 0, canvas.width, canvas.height);
        callback && callback();
      };
      
      bgImage.src = 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/team/team.png';
    },
    
    // 绘制二维码
    drawQrCode(ctx, canvas, callback) {
      if (!this.qrcode) {
        console.error('二维码图片不存在');
        callback && callback();
        return;
      }
      
      const qrImage = canvas.createImage();
      qrImage.onload = () => {
        // 计算二维码在画布中的位置，与页面位置一致
        const qrSize = canvas.width * 0.3; // 与页面上的尺寸比例一致
        const qrX = (canvas.width - qrSize) / 2;
        const qrY = canvas.height * 0.5; // 在画布中间位置
        
        // 绘制圆形二维码
        ctx.save();
        ctx.beginPath();
        ctx.arc(qrX + qrSize/2, qrY + qrSize/2, qrSize/2, 0, 2 * Math.PI);
        ctx.clip();
        ctx.drawImage(qrImage, qrX, qrY, qrSize, qrSize);
        ctx.restore();
        
        callback && callback();
      };
      
      qrImage.onerror = () => {
        console.error('二维码图片加载失败');
        callback && callback();
      };
      
      qrImage.src = this.qrcode;
    },
    
    // 绘制分享按钮
    drawShareButtons(ctx, canvas) {
      const btnY = canvas.height * 0.65;
      const btnHeight = canvas.width * 0.1;
      const btnWidth = canvas.width * 0.35;
      const spacing = canvas.width * 0.05;
      const radius = btnHeight/2;
      
      // 绘制第一个按钮
      ctx.strokeStyle = '#000';
      ctx.lineWidth = 2;
      ctx.beginPath();
      // 使用路径绘制圆角矩形，替代roundRect
      const btn1X = canvas.width/2 - btnWidth - spacing/2;
      this.drawRoundedRect(ctx, btn1X, btnY, btnWidth, btnHeight, radius);
      ctx.stroke();
      
      // 绘制第一个按钮文字
      ctx.fillStyle = '#000';
      ctx.font = `bold ${canvas.width * 0.04}px sans-serif`;
      ctx.textAlign = 'center';
      ctx.textBaseline = 'middle';
      ctx.fillText('分享海报', btn1X + btnWidth/2, btnY + btnHeight/2);
      
      // 绘制第二个按钮
      const gradient = ctx.createLinearGradient(
        canvas.width/2 + spacing/2, 
        btnY, 
        canvas.width/2 + spacing/2 + btnWidth, 
        btnY
      );
      gradient.addColorStop(0, '#EBF9FD');
      gradient.addColorStop(1, '#DEF4FB');
      
      ctx.fillStyle = gradient;
      ctx.beginPath();
      // 使用路径绘制圆角矩形，替代roundRect
      const btn2X = canvas.width/2 + spacing/2;
      this.drawRoundedRect(ctx, btn2X, btnY, btnWidth, btnHeight, radius);
      ctx.fill();
      
      // 绘制第二个按钮文字
      ctx.fillStyle = '#000';
      ctx.fillText('分享给好友', btn2X + btnWidth/2, btnY + btnHeight/2);
    },
    
    // 辅助方法：绘制圆角矩形路径
    drawRoundedRect(ctx, x, y, width, height, radius) {
      ctx.moveTo(x + radius, y);
      ctx.lineTo(x + width - radius, y);
      ctx.arcTo(x + width, y, x + width, y + radius, radius);
      ctx.lineTo(x + width, y + height - radius);
      ctx.arcTo(x + width, y + height, x + width - radius, y + height, radius);
      ctx.lineTo(x + radius, y + height);
      ctx.arcTo(x, y + height, x, y + height - radius, radius);
      ctx.lineTo(x, y + radius);
      ctx.arcTo(x, y, x + radius, y, radius);
    },
    
    // 绘制数据统计
    drawStatistics(ctx, canvas) {
      const statsY = canvas.height * 0.8;
      const sectionWidth = canvas.width / 3;
      
      // 设置文本样式
      ctx.textAlign = 'center';
      
      // 绘制三个统计数据
      const stats = [
        { value: this.teamStats.points || 0, label: '好友点击' },
        { value: this.teamStats.friends || 0, label: '好友下单' },
        { value: this.teamStats.visits || 0, label: '获得推广' }
      ];
      
      stats.forEach((stat, index) => {
        const x = sectionWidth * (index + 0.5);
        
        // 绘制数值
        ctx.font = `bold ${canvas.width * 0.06}px sans-serif`;
        ctx.fillStyle = '#333';
        ctx.fillText(stat.value, x, statsY);
        
        // 绘制标签
        ctx.font = `${canvas.width * 0.04}px sans-serif`;
        ctx.fillStyle = '#666';
        ctx.fillText(stat.label, x, statsY + canvas.width * 0.07);
      });
    },

  }
};
</script>

<style lang="scss">
@charset "UTF-8";
/* 页面左右间距 */
/* 文字尺寸 */
/*文字颜色*/
/* 边框颜色 */
/* 图片加载中颜色 */
/* 行为相关颜色 */

.team-container {
  position: relative;
  width: 92%;
  height: 100vh;
  background-color: #f5f7f9;
  overflow: hidden;
  margin: 0 auto;
}

.bg-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 1;
}

.qrcode-container {
  position: absolute;
  bottom: 40%;
  left: 50%;
  transform: translateX(-50%);
  z-index: 2;
  display: flex;
  justify-content: center;
  align-items: center;
}

.qrcode {
  width: 200rpx;
  height: 200rpx;
  background-color: #fff;
  border-radius: 200rpx;
}

.share-buttons {
  position: absolute;
  bottom: 33%;
  left: 0;
  width: 100%;
  display: flex;
  justify-content: center;
  z-index: 2;
  padding: 0 30rpx;
}

.share-btn {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  text-align: center;
  border-radius: 40rpx;
  margin: 0 20rpx;
  font-size: 28rpx;
  color: #000;
}

.poster-btn {
  background-color: transparent;
  border: 1rpx solid #000;
}

.friend-btn {
  background: linear-gradient(90deg, #EBF9FD 0%, #DEF4FB 100%);
  border: 0.5px solid #FFFFFF;
  box-shadow: 0px 2px 10.5px rgba(207, 234, 239, 0.69);
}

.stats-container {
  position: absolute;
  bottom: 20%;
  left: 0;
  width: 100%;
  display: flex;
  justify-content: space-around;
  align-items: center;
  z-index: 2;
  padding: 0 20rpx;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-num {
  font-size: 40rpx;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 28rpx;
  color: #666;
  margin-top: 10rpx;
}

/* 添加海报画布样式 */
.poster-canvas {
  position: fixed;
  left: -2000px;
  top: -2000px;
  width: 750px;
  height: 1334px;
  z-index: -1;
}
</style> 