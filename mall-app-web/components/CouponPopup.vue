<template>
  <view class="coupon-popup" v-if="show">
    <view class="popup-mask" @click="closePopup"></view>
    <view class="popup-content">
      <!-- 优惠券弹窗标题 -->
      <view class="popup-header">
        <view class="title-line left-line"></view>
        <text class="popup-title">恭喜获得优惠券</text>
        <view class="title-line right-line"></view>
      </view>
      
      <!-- 顶部文案 -->
      <view class="popup-footer">
        <text class="footer-text">{{couponData.footerText}}</text>
      </view>
      
      <!-- 优惠券卡片 -->
      <view class="coupon-card">
        <image class="coupon-bg" src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/coupon/price-bg.png" mode="aspectFill"></image>
        <view class="coupon-left">
          <view class="coupon-amount">
            <view class="amount-wrapper">
              <text class="amount">{{couponData.amount}}</text>
              <text class="unit">元</text>
            </view>
          </view>
        </view>
        <view class="coupon-right">
          <text class="coupon-title">{{couponData.title}}</text>
          <text class="coupon-desc">{{couponData.desc}}</text>
        </view>
      </view>
      
      <!-- 领取按钮区域 带毛玻璃效果 -->
      <view class="get-coupon-area">
        <image class="tag-bg" src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/coupon/tag-bg.png" mode="widthFix"></image>
        <view class="get-coupon-btn" @click="handleGetCoupon">
          <text>立即领取</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'CouponPopup',
  props: {
    show: {
      type: Boolean,
      default: false
    },
    couponData: {
      type: Object,
      default: () => ({
        amount: '50',
        title: '优惠券文案优惠券文案',
        desc: '优惠券文案优惠券文案',
        footerText: '这里是自定义文案'
      })
    }
  },
  methods: {
    closePopup() {
      this.$emit('update:show', false);
      this.$emit('close');
    },
    handleGetCoupon() {
      this.$emit('getCoupon');
      this.closePopup();
    }
  }
}
</script>

<style lang="scss">
.coupon-popup {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: center;
  
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
    width: 80%;
    background: linear-gradient(to bottom, #FDF1D7, #F7FCFE);
    background-size: 100% 100%;
    border-radius: 10px;
    box-shadow: inset 0px 0px 8rpx rgba(255, 255, 255, 1);
    backdrop-filter: blur(6rpx);
    z-index: 1000;
    
    .popup-header {
      display: flex;
      justify-content: center;
      align-items: center;
      position: relative;
      margin: 80rpx 0 30rpx;
      
      .popup-title {
        font-size: 36rpx;
        font-weight: 500;
        color: #000;
        text-align: center;
        padding: 0 40rpx;
      }
      
      .title-line {
        height: 2rpx;
        width: 80rpx;
        &.left-line {
          background: linear-gradient(to right, rgba(0,0,0,0.05), rgba(0,0,0,0.5));
        }
        &.right-line {
          background: linear-gradient(to left, rgba(0,0,0,0.05), rgba(0,0,0,0.5));
        }
      }
    }
    
    .popup-footer {
      position: absolute;
      z-index: 10;
      width: 200rpx;
      height: 48rpx;
      top: 0;
      left: 50%;
      transform: translateX(-50%);
      background: linear-gradient(to right, #FEE7C6, #FED79C);
      border-radius: 0 0 20rpx 20rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      overflow: hidden;
      
      .footer-text {
        font-size: 20rpx;
        color: #000;
        text-align: center;
        display: block;
      }
    }
    
    .coupon-card {
      width: 100%;
      height: 240rpx;
      border-radius: 16rpx;
      overflow: hidden;
      position: relative;
      display: flex;
      margin-bottom: 30rpx;
      
      .coupon-bg {
        position: absolute;
        width: 100%;
        height: 100%;
        left: 0;
        top: 0;
        z-index: 1;
      }
      
      .coupon-left {
        width: 35%;
        height: 100%;
        position: relative;
        z-index: 2;
        display: flex;
        align-items: center;
        justify-content: flex-end;
        padding-right: 15rpx;
                    
        .coupon-amount {
          display: flex;
          flex-direction: column;
          align-items: center;
          color: #fff;
          
          .amount-wrapper {
            display: flex;
            align-items: flex-end;
            
            .amount {
              font-size: 90rpx;
              font-weight: 700;
              line-height: 1;
              margin-right: 5rpx;
              text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.2);
            }
            
            .unit {
              font-size: 24rpx;
              font-weight: 400;
              line-height: 1.2;
              padding-bottom: 10rpx;
            }
          }
        }
      }
      
      .coupon-right {
        width: 65%;
        padding: 20rpx;
        padding-left: 50rpx;
        display: flex;
        flex-direction: column;
        justify-content: center;
        position: relative;
        z-index: 2;
        
        .coupon-title {
          font-size: 24rpx;
          font-weight: 500;
          color: #fff;
          text-align: left;
          margin-bottom: 16rpx;
          text-shadow: 0 1rpx 2rpx rgba(0, 0, 0, 0.3);
          padding-left: 15rpx;
        }
        
        .coupon-desc {
          font-size: 20rpx;
          font-weight: 400;
          color: rgba(255, 255, 255, 0.9);
          text-align: left;
          padding-left: 15rpx;
        }
      }
    }
    
    .get-coupon-area {
      position: relative;
      width: 100%;
      height: 130rpx;
      display: flex;
      justify-content: center;
      align-items: center;
      margin-bottom: 10rpx;
      
      .tag-bg {
        position: absolute;
        width: 100%;
        height: auto;
        left: 0;
        bottom: 0;
        z-index: 2;
      }
      
      .get-coupon-btn {
        width: 80%;
        height: 70rpx;
        position: relative;
        z-index: 3;
        background: linear-gradient(to right, #FD905A, #FF454E);
        border-radius: 36rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
        font-size: 28rpx;
        font-weight: 500;
        box-shadow: 0 4rpx 6rpx rgba(239, 182, 183, 0.8),
                   inset 0 -6rpx 0 rgba(226, 0, 6, 0.8),
                   inset 4rpx 4rpx 5rpx rgba(255, 170, 127, 0.8);
        backdrop-filter: blur(6rpx);
      }
    }
  }
}
</style> 