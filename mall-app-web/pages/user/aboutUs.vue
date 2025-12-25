<template>
  <view class="container">
    <view class="header">
      <image class="header-image" src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/user/about.png" mode="aspectFill"></image>
      <view class="header-title">关于我们</view>
    </view>
    <view class="content">
      <view class="about-section">
        <view class="section-title">关于我们的品牌理念</view>
        <view class="section-text">我们致力于为用户提供高品质的购物体验，精选优质商品，满足您的日常所需。从创立之初，我们就坚持"用户至上、品质为先"的理念，不断创新和完善我们的服务，让每一位用户都能享受便捷、安心的购物乐趣。</view>
      </view>
      <view class="about-section">
        <view class="section-title">我们的服务承诺</view>
        <view class="section-text">我们承诺为您提供正品保障、快速配送、无忧退换等全方位服务。我们的专业团队随时为您解答疑问，处理反馈，确保您的每一次购物都得到满意的结果。您的满意是我们不断前进的动力，我们将持续优化产品与服务，为您创造更美好的购物体验。</view>
      </view>
      <view class="about-section">
        <view class="section-title">联系与合作</view>
        <view class="section-text">如果您有任何问题、建议或合作意向，欢迎随时与我们联系。我们期待听取您的声音，共同成长。商务合作请发送邮件至：haojiangzhenhao@163.com。</view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {};
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
.container {
  background-color: #fff;
  min-height: 100vh;
}

.header {
  position: relative;
  height: 400rpx;
  overflow: hidden;
  
  .header-image {
    width: 100%;
    height: 100%;
  }
  
  .header-title {
    position: absolute;
    bottom: 40rpx;
    left: 40rpx;
    font-size: 48rpx;
    font-weight: bold;
    color: #fff;
    text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.3);
  }
}

.content {
  padding: 30rpx;
}

.about-section {
  margin-bottom: 50rpx;
  
  .section-title {
    font-size: 36rpx;
    font-weight: 500;
    color: #333;
    margin-bottom: 20rpx;
  }
  
  .section-text {
    font-size: 28rpx;
    color: #666;
    line-height: 1.8;
  }
}
</style> 