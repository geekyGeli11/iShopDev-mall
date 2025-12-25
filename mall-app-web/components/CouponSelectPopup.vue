<template>
  <view class="coupon-popup" v-if="visible">
    <!-- 遮罩层 -->
    <view 
      class="popup-mask" 
      :class="{ 'show': showMask }"
      @tap="handleMaskClick"
    ></view>
    
    <!-- 弹窗内容 -->
    <view 
      class="popup-content" 
      :class="{ 'show': showContent }"
      @tap.stop
    >
      <!-- 弹窗头部 -->
      <view class="popup-header">
        <view class="header-title">选择优惠券</view>
        <view class="close-btn" @tap="closePopup">
          <text class="close-icon">×</text>
        </view>
      </view>
      
      <!-- 优惠券列表 -->
      <scroll-view class="coupon-list" scroll-y>
        <view
          v-for="(item, index) in couponList"
          :key="index"
          class="coupon-card"
        >
          <!-- 优惠券背景 -->
          <image 
            class="coupon-bg" 
            src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/order/coupon_background.png"
            mode="scaleToFill"
          />
          
          <!-- 优惠券内容 -->
          <view class="coupon-content">
            <!-- 左侧内容区域 -->
            <view class="coupon-left">
              <view class="coupon-title">{{ item.name }} - {{ getCouponDisplayText(item) }}</view>
              <view class="coupon-rule">规则：{{ getRuleText(item) }}</view>
              <view class="coupon-validity">有效期：{{ formatTime(item.startTime) }} - {{ formatTime(item.endTime) }}</view>
            </view>

            <!-- 右侧状态区域 -->
            <view class="coupon-right" @tap="selectAndClose(item)">
              <view class="status-area">
                <view class="status-text status-use">
                  立即使用
                </view>
              </view>
            </view>
          </view>
          

        </view>
        
        <!-- 空状态 -->
        <view v-if="!couponList || couponList.length === 0" class="empty-state">
          <text class="empty-text">暂无可用优惠券</text>
        </view>
      </scroll-view>
      

    </view>
  </view>
</template>

<script>
export default {
  name: 'CouponSelectPopup',
  props: {
    // 控制弹窗显示
    visible: {
      type: Boolean,
      default: false
    },
    // 优惠券列表
    couponList: {
      type: Array,
      default: () => []
    },
    // 当前选中的优惠券
    selectedCoupon: {
      type: Object,
      default: null
    }
  },
  
  data() {
    return {
      showMask: false,
      showContent: false
    };
  },
  
  watch: {
    visible: {
      handler(newVal) {
        if (newVal) {
          this.openPopup();
        } else {
          this.closePopup();
        }
      },
      immediate: true
    }
  },
  
  methods: {
    // 打开弹窗
    openPopup() {
      this.showMask = true;
      this.$nextTick(() => {
        setTimeout(() => {
          this.showContent = true;
        }, 50);
      });
    },
    
    // 关闭弹窗
    closePopup() {
      this.showContent = false;
      setTimeout(() => {
        this.showMask = false;
        this.$emit('close');
      }, 300);
    },
    
    // 点击遮罩层
    handleMaskClick() {
      this.closePopup();
    },
    
    // 选择优惠券
    selectCoupon(coupon) {
      this.$emit('select', coupon);
    },

    // 选择并关闭弹窗
    selectAndClose(coupon) {
      this.$emit('select', coupon);
      this.$emit('confirm', coupon);
      this.closePopup();
    },

    // 确认选择
    confirmSelection() {
      this.$emit('confirm', this.selectedCoupon);
      this.closePopup();
    },
    
    // 获取状态文字
    getStatusText(status) {
      const statusMap = {
        0: '去使用',
        1: '已使用',
        2: '已过期'
      };
      return statusMap[status] || '去使用';
    },

    // 获取优惠券显示文本
    getCouponDisplayText(item) {
      if (item.couponType === 1) {
        // 打折券：显示折扣比例
        // 后台返回的是0.8这样的小数，需要转换为8折显示
        if (item.discountRate) {
          return `${(item.discountRate * 10).toFixed(1)}折`;
        }
        return '折扣券';
      } else {
        // 满减券：显示金额
        return `${item.amount || 0}元`;
      }
    },

    // 获取规则文字
    getRuleText(item) {
      if (item.minPoint && item.minPoint > 0) {
        return `满${item.minPoint}可用`;
      }

      // 根据useType判断使用范围
      if (item.useType === 0) {
        return '全场通用';
      } else if (item.useType === 1) {
        return '指定商品可用';
      } else {
        return '全场通用';
      }
    },

    // 格式化时间
    formatTime(date) {
      if (!date) return '';
      if (typeof date === 'string') {
        date = new Date(date);
      }

      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');

      return `${year}-${month}-${day}`;
    }
  }
};
</script>

<style lang="scss" scoped>
.coupon-popup {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999;
}

.popup-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0);
  transition: background 0.3s ease;
  
  &.show {
    background: rgba(0, 0, 0, 0.5);
  }
}

.popup-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  border-radius: 24rpx 24rpx 0 0;
  max-height: 80vh;
  transform: translateY(100%);
  transition: transform 0.3s ease;
  display: flex;
  flex-direction: column;
  
  &.show {
    transform: translateY(0);
  }
}

.popup-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx 32rpx 24rpx;
  border-bottom: 1rpx solid #f5f5f5;
}

.header-title {
  font-size: 36rpx;
  font-weight: 600;
  color: #333;
}

.close-btn {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-icon {
  font-size: 48rpx;
  color: #999;
  line-height: 1;
}

.coupon-list {
  flex: 1;
  padding: 24rpx 32rpx;
}

.coupon-card {
  position: relative;
  width: 100%;
  height: 184rpx;
  margin-bottom: 24rpx;
}

.coupon-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.coupon-content {
  position: relative;
  z-index: 2;
  display: flex;
  height: 100%;
  padding: 24rpx;
}

.coupon-left {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding-right: 24rpx;
}

.coupon-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 12rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.coupon-rule {
  font-size: 24rpx;
  color: #666;
  margin-bottom: 8rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.coupon-validity {
  font-size: 22rpx;
  color: #999;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.coupon-right {
  width: 70rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  padding-left: 50rpx;
  cursor: pointer;
}

.status-area {
  writing-mode: vertical-lr;
  text-orientation: upright;
}

.status-text {
  font-size: 28rpx;
  font-weight: 600;

  &.status-use {
    color: #000000;
  }

  &.status-available {
    color: #000000;
  }

  &.status-used {
    color: #999;
  }

  &.status-expired {
    color: #999;
  }
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 200rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}
</style>
