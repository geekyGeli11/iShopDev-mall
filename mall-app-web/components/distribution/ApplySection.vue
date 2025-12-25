<template>
  <view class="apply-section">
    <view class="apply-card">
      <view class="apply-header">
        <image class="apply-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/apply-icon.png"></image>
        <view class="apply-title">
          <text class="title-text">成为分销商</text>
          <text class="title-subtitle">邀请好友，赚取佣金</text>
        </view>
      </view>
      
      <view class="apply-benefits">
        <view class="benefit-item">
          <image class="benefit-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/benefit-commission.png"></image>
          <text class="benefit-text">推荐订单获得佣金</text>
        </view>
        <view class="benefit-item">
          <image class="benefit-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/benefit-team.png"></image>
          <text class="benefit-text">发展团队获得收益</text>
        </view>
        <view class="benefit-item">
          <image class="benefit-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/benefit-tools.png"></image>
          <text class="benefit-text">专业推广工具</text>
        </view>
      </view>
      
      <view class="apply-rules">
        <text class="rules-title">申请条件：</text>
        <text class="rules-item">• 注册时间满7天</text>
        <text class="rules-item">• 成功完成至少1笔订单</text>
        <text class="rules-item">• 无违规记录</text>
      </view>
      
      <view class="apply-action">
        <button class="apply-btn" @tap="handleApply" :disabled="applying">
          <text class="apply-btn-text">{{ applying ? '申请中...' : '立即申请' }}</text>
        </button>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'ApplySection',
  data() {
    return {
      applying: false
    }
  },
  methods: {
    handleApply() {
      if (this.applying) return;
      
      this.$emit('apply');
    }
  }
}
</script>

<style lang="scss" scoped>
.apply-section {
  padding: 30rpx;
}

.apply-card {
  background: linear-gradient(135deg, #A9FF00 0%, #647D00 100%);
  border-radius: 20rpx;
  padding: 40rpx;
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    right: 0;
    width: 200rpx;
    height: 200rpx;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
    transform: translate(50%, -50%);
  }
}

.apply-header {
  display: flex;
  align-items: center;
  margin-bottom: 40rpx;
}

.apply-icon {
  width: 80rpx;
  height: 80rpx;
  margin-right: 20rpx;
}

.apply-title {
  flex: 1;
}

.title-text {
  display: block;
  font-size: 36rpx;
  font-weight: bold;
  color: #fff;
  margin-bottom: 8rpx;
}

.title-subtitle {
  display: block;
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.8);
}

.apply-benefits {
  margin-bottom: 40rpx;
}

.benefit-item {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
  
  &:last-child {
    margin-bottom: 0;
  }
}

.benefit-icon {
  width: 36rpx;
  height: 36rpx;
  margin-right: 20rpx;
}

.benefit-text {
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.9);
}

.apply-rules {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12rpx;
  padding: 30rpx;
  margin-bottom: 40rpx;
}

.rules-title {
  display: block;
  font-size: 28rpx;
  font-weight: bold;
  color: #fff;
  margin-bottom: 16rpx;
}

.rules-item {
  display: block;
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 8rpx;
  
  &:last-child {
    margin-bottom: 0;
  }
}

.apply-action {
  text-align: center;
}

.apply-btn {
  background: #fff;
  border-radius: 50rpx;
  padding: 0 80rpx;
  border: none;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  
  &:disabled {
    opacity: 0.6;
  }
  
  &::after {
    border: none;
  }
}

.apply-btn-text {
  font-size: 32rpx;
  font-weight: bold;
  color: #647D00;
  line-height: 1;
}
</style> 