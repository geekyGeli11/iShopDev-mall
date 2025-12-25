<template>
  <view class="container" @click="handleClick">
    <image 
      class="apng-animation"
      :class="{'opacity-transition': startTransition}"
      src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/index/loading-4.png" 
      mode="aspectFill"
      @load="onImageLoaded"
    ></image>
  </view>
</template>

<script>
export default {
  data() {
    return {
      timer: null,
      animationDuration: 4000, // 动画持续时间4秒
      extraDelay: 2000,        // 额外等待时间2秒
      isLoaded: false,         // 标记图片是否已加载
      startTransition: false   // 控制CSS过渡动画
    };
  },
  onLoad() {
    // 设置一个超时定时器，确保即使图片加载失败也能跳转
    this.timer = setTimeout(() => {
      this.navigateToIndex();
    }, 7000); // 设置一个较长的超时时间作为兜底
  },
  onUnload() {
    // 页面卸载时清除定时器
    this.clearAllTimers();
  },
  methods: {
    onImageLoaded() {
      // 图片资源加载完成
      this.isLoaded = true;
      console.log('图片加载完成，等待动画播放...');
      
      // 清除兜底定时器
      this.clearAllTimers();
      
      // WebView 在不同平台上对 APNG 的处理可能不同
      // 所以我们使用 setTimeout 来模拟动画播放时间
      this.timer = setTimeout(() => {
        console.log('动画播放完成，额外等待2秒...');
        
        // 额外等待2秒
        this.extraTimer = setTimeout(() => {
          this.navigateToIndex();
        }, this.extraDelay);
      }, this.animationDuration);
    },
    
    handleClick() {
      // 阻止多次点击
      if (this.clickLocked) return;
      this.clickLocked = true;
      
      // 用户点击时立即跳转，但仅当图片已加载完成
      if (this.isLoaded) {
        this.clearAllTimers();
        
        // 添加淡出效果，然后跳转
        this.startTransition = true;
        setTimeout(() => {
          this.navigateToIndex();
        }, 300); // 等待过渡效果完成
      }
    },
    
    clearAllTimers() {
      // 清除所有计时器
      if (this.timer) {
        clearTimeout(this.timer);
        this.timer = null;
      }
      if (this.extraTimer) {
        clearTimeout(this.extraTimer);
        this.extraTimer = null;
      }
    },
    
    navigateToIndex() {
      // 确保只跳转一次
      if (this.hasNavigated) return;
      this.hasNavigated = true;
      
      // 跳转到首页并关闭当前页面
      uni.switchTab({
        url: '/pages/new_index/index'
      });
    }
  }
};
</script>

<style lang="scss">
.container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #fff;
}

.apng-animation {
  width: 100%;
  height: 100%;
  opacity: 1;
}

.opacity-transition {
  transition: opacity 0.3s ease;
  opacity: 0.5;
}
</style> 