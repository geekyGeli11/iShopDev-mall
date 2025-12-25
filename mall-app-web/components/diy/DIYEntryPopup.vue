<template>
  <view :class="['diy-entry-popup', visible ? 'show' : 'hide']" @touchmove.stop.prevent @tap="handleMaskClick">
    <view class="popup-content" @tap.stop>
      <!-- 关闭按钮 -->
      <view class="close-btn" @tap="close">
        <image class="close-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/common/close.png" mode="aspectFit" />
      </view>
      
      <!-- 标题 -->
      <view class="popup-title">
        <text class="title-text">选择定制面</text>
        <text class="subtitle-text">请选择您要定制的面</text>
      </view>
      
      <!-- 定制面列表 -->
      <view class="customize-faces">
        <view 
          v-for="(face, index) in customizeFaces" 
          :key="index"
          class="face-item"
          @tap="selectFace(face, index)"
        >
          <view class="face-image-container">
            <image 
              class="face-image" 
              :src="face.previewImage || defaultFaceImage" 
              mode="aspectFit"
            />
            <view v-if="selectedFaces.includes(index)" class="selected-overlay">
              <image class="check-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/common/check.png" mode="aspectFit" />
            </view>
          </view>
          <text class="face-name">{{ face.name }}</text>
        </view>
      </view>
      
      <!-- 操作按钮 -->
      <view class="action-buttons">
        <view class="customize-btn" @tap="startCustomize">
          <text class="btn-text">去定制</text>
        </view>
      </view>
    </view>
    
    <!-- 温馨提示弹窗 -->
    <view :class="['tips-popup', showTips ? 'show' : 'hide']" @touchmove.stop.prevent @tap="hideTips">
      <view class="tips-content" @tap.stop>
        <view class="tips-header">
          <image class="tips-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/tips.png" mode="aspectFit" />
          <text class="tips-title">DIY定制温馨提示</text>
        </view>
        
        <view class="tips-list">
          <view class="tips-item">
            <text class="tips-number">1</text>
            <text class="tips-text">定制过程中请保持网络连接稳定</text>
          </view>
          <view class="tips-item">
            <text class="tips-number">2</text>
            <text class="tips-text">AI风格化处理需要1-3分钟时间</text>
          </view>
          <view class="tips-item">
            <text class="tips-number">3</text>
            <text class="tips-text">定制完成后可随时保存和修改</text>
          </view>
          <view class="tips-item">
            <text class="tips-number">4</text>
            <text class="tips-text">定制商品价格会根据复杂度调整</text>
          </view>
        </view>
        
        <view class="tips-buttons">
          <view class="tips-cancel-btn" @tap="hideTips">
            <text class="tips-btn-text">取消</text>
          </view>
          <view class="tips-confirm-btn" @tap="confirmCustomize">
            <text class="tips-btn-text">开始定制</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'DIYEntryPopup',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    productId: {
      type: [String, Number],
      default: ''
    },
    customizeFaces: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      selectedFaces: [], // 选中的定制面索引数组
      showTips: false,
      defaultFaceImage: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/default-face.png'
    };
  },
  watch: {
    visible(newVal) {
      if (newVal) {
        // 默认选中第一个面
        if (this.customizeFaces.length > 0) {
          this.selectedFaces = [0];
        }
      } else {
        this.showTips = false;
        this.selectedFaces = [];
      }
    }
  },
  methods: {
    // 选择定制面
    selectFace(face, index) {
      const selectedIndex = this.selectedFaces.indexOf(index);
      if (selectedIndex > -1) {
        // 如果已选中，则取消选中
        this.selectedFaces.splice(selectedIndex, 1);
      } else {
        // 如果未选中，则添加到选中列表
        this.selectedFaces.push(index);
      }
    },
    
    // 开始定制
    startCustomize() {
      if (this.selectedFaces.length === 0) {
        uni.showToast({
          title: '请至少选择一个定制面',
          icon: 'none'
        });
        return;
      }
      
      // 显示温馨提示
      this.showTips = true;
    },
    
    // 隐藏温馨提示
    hideTips() {
      this.showTips = false;
    },
    
    // 确认开始定制
    confirmCustomize() {
      this.hideTips();
      
      // 获取选中的定制面数据
      const selectedFaceData = this.selectedFaces.map(index => this.customizeFaces[index]);
      
      // 触发开始定制事件
      this.$emit('start-customize', {
        productId: this.productId,
        selectedFaces: selectedFaceData,
        selectedIndexes: this.selectedFaces
      });
      
      this.close();
    },
    
    // 关闭弹窗
    close() {
      this.$emit('close');
    },
    
    // 点击遮罩关闭
    handleMaskClick() {
      this.close();
    }
  }
};
</script>

<style lang="scss" scoped>
.diy-entry-popup {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  
  &.hide {
    opacity: 0;
    visibility: hidden;
  }
  
  &.show {
    opacity: 1;
    visibility: visible;
  }
}

.popup-content {
  background: #FFFFFF;
  border-radius: 24rpx;
  width: 640rpx;
  max-height: 80vh;
  position: relative;
  padding: 40rpx 30rpx 30rpx;
  box-sizing: border-box;
}

.close-btn {
  position: absolute;
  top: 20rpx;
  right: 20rpx;
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-icon {
  width: 32rpx;
  height: 32rpx;
}

.popup-title {
  text-align: center;
  margin-bottom: 40rpx;
}

.title-text {
  display: block;
  font-size: 36rpx;
  font-weight: 600;
  color: #000000;
  margin-bottom: 10rpx;
}

.subtitle-text {
  display: block;
  font-size: 28rpx;
  color: #666666;
}

.customize-faces {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20rpx;
  margin-bottom: 40rpx;
  max-height: 400rpx;
  overflow-y: auto;
}

.face-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20rpx;
  border-radius: 16rpx;
  border: 2rpx solid #EEEEEE;
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.95);
  }
}

.face-image-container {
  position: relative;
  width: 120rpx;
  height: 120rpx;
  margin-bottom: 16rpx;
}

.face-image {
  width: 100%;
  height: 100%;
  border-radius: 12rpx;
}

.selected-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(167, 203, 0, 0.8);
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.check-icon {
  width: 40rpx;
  height: 40rpx;
}

.face-name {
  font-size: 26rpx;
  color: #333333;
  text-align: center;
}

.action-buttons {
  display: flex;
  justify-content: center;
}

.customize-btn {
  background: linear-gradient(135deg, #A7CB00, #D7FF35);
  border-radius: 24rpx;
  padding: 24rpx 60rpx;
  box-shadow: 0 4rpx 12rpx rgba(167, 203, 0, 0.3);
}

.btn-text {
  font-size: 32rpx;
  font-weight: 600;
  color: #000000;
}

/* 温馨提示弹窗样式 */
.tips-popup {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  
  &.hide {
    opacity: 0;
    visibility: hidden;
  }
  
  &.show {
    opacity: 1;
    visibility: visible;
  }
}

.tips-content {
  background: #FFFFFF;
  border-radius: 24rpx;
  width: 560rpx;
  padding: 40rpx 30rpx 30rpx;
  box-sizing: border-box;
}

.tips-header {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 30rpx;
  gap: 16rpx;
}

.tips-icon {
  width: 48rpx;
  height: 48rpx;
}

.tips-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #000000;
}

.tips-list {
  margin-bottom: 40rpx;
}

.tips-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 20rpx;
  gap: 16rpx;
}

.tips-number {
  width: 36rpx;
  height: 36rpx;
  background: #A7CB00;
  border-radius: 50%;
  color: #FFFFFF;
  font-size: 24rpx;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.tips-text {
  font-size: 28rpx;
  color: #333333;
  line-height: 1.5;
  flex: 1;
}

.tips-buttons {
  display: flex;
  gap: 20rpx;
}

.tips-cancel-btn,
.tips-confirm-btn {
  flex: 1;
  padding: 24rpx;
  border-radius: 16rpx;
  text-align: center;
}

.tips-cancel-btn {
  background: #F5F5F5;
  border: 1rpx solid #DDDDDD;
}

.tips-confirm-btn {
  background: linear-gradient(135deg, #A7CB00, #D7FF35);
}

.tips-btn-text {
  font-size: 30rpx;
  font-weight: 500;
  color: #000000;
}
</style>
