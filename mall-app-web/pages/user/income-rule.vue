<template>
  <view class="rule-container">
    <!-- 背景图 -->
    <image 
      class="background-image" 
      src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/my/income_rule_background.png" 
      mode="aspectFill"
    ></image>
    
    <!-- 自定义导航栏 -->
    <nav-bar
      :placeholder="true" 
      :back="true"
      bg-color="transparent" 
      :titleCenter="true"
      back-icon-style="dark"
    >
    </nav-bar>
    
    <!-- 规则内容卡片 -->
    <view class="rule-content-card">
      <view class="rule-header">
        <text class="rule-title">规则概览</text>
        <image class="title-indicator" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/tab_indicator.png"></image>
      </view>
      
      <view class="rule-list">
        <view class="rule-item" v-for="(rule, index) in ruleList" :key="index">
          <text class="rule-number">{{ index + 1 }}.</text>
          <view class="rule-content">
            <text class="rule-text">{{ rule.title }}</text>
            <text class="rule-desc">{{ rule.description }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import NavBar from '@/components/nav-bar.vue';

export default {
  components: {
    'nav-bar': NavBar
  },
  data() {
    return {
      ruleList: [
        {
          title: '邀请好友注册并下单，即可获得收益',
          description: '好友通过您的邀请链接注册并成功下单，您将获得相应的推广收益。'
        },
        {
          title: '收益金额根据好友订单金额计算',
          description: '推广收益按照好友订单金额的一定比例计算，订单金额越高，收益越多。'
        },
        {
          title: '收益将在订单完成后7个工作日内到账',
          description: '好友订单确认收货后，推广收益将在7个工作日内自动到账至您的收益账户。'
        },
        {
          title: '可提现金额需满足最低提现门槛',
          description: '当您的可提现金额达到最低提现门槛时，即可申请提现至您的微信钱包或银行卡。'
        },
        {
          title: '未入账收益为待确认收货的订单收益',
          description: '好友已下单但尚未确认收货的订单产生的收益，将显示在未入账栏目中。'
        }
      ]
    };
  },
  
  onLoad() {
    // 页面加载
  },
  
  methods: {
    // 页面方法
  }
};
</script>

<style lang="scss">
@charset "UTF-8";

.rule-container {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
}

.background-image {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.rule-content-card {
  position: relative;
  z-index: 3;
  margin: 180rpx 32rpx;
  background: #ffffff;
  border-radius: 20rpx;
  padding: 40rpx 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
}

.rule-header {
  position: relative;
  margin-bottom: 40rpx;
}

.rule-title {
  position: relative;
  font-size: 36rpx;
  font-weight: 600;
  color: #333;
  line-height: 1.2;
  z-index: 1;
}

.title-indicator {
  position: absolute;
  bottom: 0rpx;
  left: 0;
  width:200rpx;
  height: 40rpx;
  z-index: 0;
}

.rule-list {
  display: flex;
  flex-direction: column;
  gap: 32rpx;
}

.rule-item {
  display: flex;
  gap: 16rpx;
  align-items: flex-start;
}

.rule-number {
  font-size: 32rpx;
  font-weight: 600;
  color: #27243B;
  line-height: 1.4;
  flex-shrink: 0;
}

.rule-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.rule-text {
  font-size: 32rpx;
  font-weight: 500;
  color: #333;
  line-height: 1.4;
}

.rule-desc {
  font-size: 28rpx;
  color: #666;
  line-height: 1.5;
}
</style> 