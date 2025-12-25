<template>
  <view v-if="visible" class="school-select-popup-container">
    <!-- 全屏半透明蒙层 -->
    <view class="popup-overlay"></view>
    
    <!-- 弹窗内容（图片 + 按钮在下方） -->
    <view class="popup-content">
      <!-- 弹窗图片（占据屏幕上方） -->
      <image 
        class="popup-image" 
        :src="imageUrl" 
        mode="aspectFit"
      />
      
      <!-- 知道了按钮（在屏幕下方） -->
      <view class="button-container">
        <button class="confirm-button" @tap="handleConfirm">知道了</button>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'SchoolSelectPopup',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    imageUrl: {
      type: String,
      default: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/preload.png'
    }
  },
  data() {
    return {};
  },
  methods: {
    handleConfirm() {
      this.$emit('confirm');
    }
  }
};
</script>

<style scoped lang="scss">
.school-select-popup-container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 999;
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.popup-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  z-index: 1;
}

.popup-content {
  position: relative;
  z-index: 2;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  padding-top: 50px;
}

.popup-image {
  width: 100%;
  max-width: 350px;
  height: 300px;
  display: block;
  margin: 20rpx 40rpx;
  flex-shrink: 0;
}

.button-container {
  width: 100%;
  display: flex;
  justify-content: center;
  z-index: 3;
  flex-shrink: 0;
  margin-bottom: 60px;
}

.confirm-button {
  width: 120px;
  padding: 12px 30px;
  background-color: #fff;
  border: none;
  border-radius: 6px;
  color: #333;
  font-size: 15px;
  font-weight: 600;
  line-height: 1.4;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  
  &::after {
    border: none;
  }
}

.confirm-button:active {
  background-color: #f5f5f5;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
</style>
