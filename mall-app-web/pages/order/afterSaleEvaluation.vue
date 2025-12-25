<template>
  <view class="app">
    <!-- 页面内容 -->
    <view class="content">
      <!-- 服务评价区域 -->
      <view class="service-rating">
        <text class="section-title">服务评价</text>
        <view class="stars-container">
          <image 
            v-for="(star, index) in 5" 
            :key="index"
            class="star" 
            :src="index < rating ? 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/order/star_selected.png' : 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/order/star.png'"
            @tap="setRating(index + 1)"
            mode="aspectFit"
          />
        </view>
      </view>     
      
      <!-- 文本输入区域 -->
      <view class="textarea-section">
        <textarea 
          class="comment-textarea"
          v-model="comment"
          placeholder="描述具体情况"
          maxlength="300"
          :auto-height="true"
        />
        <text class="char-count">{{ comment.length }}/300</text>
      </view>
    </view>

     <!-- 匿名选项 -->
     <view class="anonymous-section">
        <view class="anonymous-checkbox" @tap="toggleAnonymous">
          <view class="checkbox" :class="{ 'checked': isAnonymous }"></view>
        </view>
        <text class="anonymous-text">匿名</text>
      </view>
    
    <!-- 提交按钮 -->
    <view class="submit-container">
      <button class="submit-btn" @tap="submitEvaluation">提交评价</button>
    </view>
  </view>
</template>

<script>
import { createComment } from '@/api/comment.js';

export default {
  data() {
    return {
      orderId: '',
      productId: '', // 商品ID
      rating: 0, // 默认0星，用户点击选择
      isAnonymous: false,
      comment: ''
    };
  },
  
  onLoad(options) {
    if (options.orderId) {
      this.orderId = options.orderId;
    }
    if (options.productId) {
      this.productId = options.productId;
    }
  },
  
  methods: {
    // 设置评分
    setRating(rating) {
      this.rating = rating;
    },
    
    // 切换匿名状态
    toggleAnonymous() {
      this.isAnonymous = !this.isAnonymous;
    },
    
    // 调用评价API
    createComment(params) {
      return createComment(params);
    },
    
    // 提交评价
    submitEvaluation() {
      if (!this.orderId) {
        uni.showToast({
          title: '订单信息异常',
          icon: 'none'
        });
        return;
      }
      
      if (this.rating === 0) {
        uni.showToast({
          title: '请选择评分',
          icon: 'none'
        });
        return;
      }
      
      if (this.comment.trim().length === 0) {
        uni.showToast({
          title: '请填写评价内容',
          icon: 'none'
        });
        return;
      }
      
      // 显示加载中
      uni.showLoading({
        title: '提交中...'
      });
      
      const params = {
        orderId: this.orderId,
        productId: this.productId, // 需要传递商品ID
        star: this.rating,
        content: this.comment.trim(),
        isAnonymous: this.isAnonymous ? 1 : 0
      };
      
      // 调用评价接口
      this.createComment(params).then(res => {
        uni.hideLoading();
        
        if (res.code === 200) {
          uni.showToast({
            title: '评价提交成功',
            icon: 'success'
          });
          
          // 设置刷新标志，通知订单列表页面需要刷新
          uni.setStorageSync('needRefreshOrderList', true);
          
          // 返回上一页
          setTimeout(() => {
            uni.navigateBack();
          }, 1500);
        } else {
          uni.showToast({
            title: res.message || '提交失败',
            icon: 'none'
          });
        }
      }).catch(err => {
        uni.hideLoading();
        console.error('提交评价失败:', err);
        
        uni.showToast({
          title: '提交失败，请稍后重试',
          icon: 'none'
        });
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.app {
  min-height: 100vh;
  background: #F8F8F8;
  display: flex;
  flex-direction: column;
}

.content {
  flex: 1;
  padding-top: 20rpx;
}

.service-rating {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 32rpx;
  padding: 32rpx;
  background: #FFFFFF;
  
  .section-title {
    font-family: 'PingFang SC';
    font-weight: 400;
    font-size: 28rpx;
    line-height: 40rpx;
    color: #0A0D05;
  }
  
  .stars-container {
    display: flex;
    flex-direction: row;
    align-items: center;
    gap: 32rpx;
    
    .star {
      width: 48rpx;
      height: 48rpx;
    }
  }
}

.anonymous-section {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 16rpx;
  padding: 32rpx;
  background: #FFFFFF;
  margin-top: 2rpx;
  
  .anonymous-checkbox {
    .checkbox {
      width: 40rpx;
      height: 40rpx;
      border-radius: 50%;
      background: #D9D9D9;
      border: 10rpx solid #FFFFFF;
      
      &.checked {
        border-color: #A9FF00;
      }
    }
  }
  
  .anonymous-text {
    font-family: 'PingFang SC';
    font-weight: 400;
    font-size: 28rpx;
    line-height: 40rpx;
    color: #0A0D05;
  }
}

.textarea-section {
  position: relative;
  background: #FFFFFF;
  margin-top: 2rpx;
  padding: 32rpx;
  min-height: 348rpx;
  border-bottom: 2rpx solid #EEEEEE;
  
  .comment-textarea {
    width: 100%;
    min-height: 280rpx;
    font-family: 'PingFang SC';
    font-weight: 400;
    font-size: 28rpx;
    line-height: 40rpx;
    color: #0A0D05;
    background: transparent;
    border: none;
    outline: none;
    resize: none;
    
    &::placeholder {
      color: #9FA19D;
    }
  }
  
  .char-count {
    position: absolute;
    bottom: 32rpx;
    right: 32rpx;
    font-family: 'PingFang SC';
    font-weight: 400;
    font-size: 28rpx;
    line-height: 40rpx;
    color: #9FA19D;
  }
}

.submit-container {
  padding: 32rpx;
  background: #FFFFFF;
  box-shadow: 0px -2rpx 2rpx 0px rgba(0, 0, 0, 0.05);
  
  .submit-btn {
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    gap: 16rpx;
    padding: 20rpx 28rpx;
    width: 100%;
    height: auto;
    background: #20201E;
    border-radius: 16rpx;
    border: none;
    margin: 0;
    
    font-family: 'PingFang SC';
    font-weight: 400;
    font-size: 32rpx;
    line-height: 44rpx;
    color: #A9FF00;
    
    &::after {
      border: none;
    }
  }
}
</style> 