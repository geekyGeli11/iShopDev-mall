<template>
  <view class="design-detail-page">
    <!-- 导航栏 -->
    <nav-bar title="设计详情" />
    
    <!-- 设计信息 -->
    <view v-if="designInfo" class="design-info-section">
      <view class="design-header">
        <text class="design-title">{{ designInfo.name || '我的设计' }}</text>
        <text class="design-status" :class="statusClass">
          {{ statusText }}
        </text>
      </view>
      
      <!-- 设计预览图 -->
      <view class="design-preview-section">
        <text class="section-title">设计预览</text>
        <view class="preview-grid">
          <image 
            v-for="(image, index) in previewImages"
            :key="index"
            class="preview-image"
            :src="image"
            mode="aspectFit"
            @tap="previewImage(image)"
          />
        </view>
      </view>
      
      <!-- 设计信息 -->
      <view class="design-details">
        <view class="detail-item">
          <text class="detail-label">创建时间</text>
          <text class="detail-value">{{ formatTime(designInfo.createTime) }}</text>
        </view>
        <view class="detail-item">
          <text class="detail-label">更新时间</text>
          <text class="detail-value">{{ formatTime(designInfo.updateTime) }}</text>
        </view>
        <view v-if="designInfo.productName" class="detail-item">
          <text class="detail-label">关联商品</text>
          <text class="detail-value">{{ designInfo.productName }}</text>
        </view>
      </view>
      
      <!-- 生产状态（如果是订单相关） -->
      <view v-if="orderId && productionStatus !== null" class="production-status-section">
        <text class="section-title">生产状态</text>
        <view class="status-timeline">
          <view 
            v-for="(step, index) in productionSteps"
            :key="index"
            class="status-step"
            :class="{ 'active': step.status <= productionStatus, 'current': step.status === productionStatus }"
          >
            <view class="step-icon">
              <text class="step-number">{{ index + 1 }}</text>
            </view>
            <view class="step-content">
              <text class="step-title">{{ step.title }}</text>
              <text class="step-desc">{{ step.desc }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 加载状态 -->
    <view v-else class="loading-section">
      <text class="loading-text">加载中...</text>
    </view>
    
    <!-- 操作按钮 -->
    <view v-if="designInfo" class="action-section">
      <view v-if="!orderId" class="action-btn primary" @tap="editDesign">
        <text class="action-text">编辑设计</text>
      </view>
      <view v-if="!orderId" class="action-btn secondary" @tap="createOrder">
        <text class="action-text">立即下单</text>
      </view>
    </view>
  </view>
</template>

<script>
import NavBar from '@/components/nav-bar.vue';
import { getDIYDesign } from '@/api/diy.js';

export default {
  name: 'DIYDesignDetail',
  components: {
    NavBar
  },
  data() {
    return {
      designId: '',
      orderId: '',
      designInfo: null,
      productionStatus: null,
      productionSteps: [
        { status: 0, title: '设计确认', desc: '设计方案已确认' },
        { status: 1, title: '开始生产', desc: '工厂开始制作' },
        { status: 2, title: '生产完成', desc: '商品制作完成' },
        { status: 3, title: '已发货', desc: '商品已发出' }
      ]
    };
  },
  computed: {
    // 状态样式类
    statusClass() {
      if (!this.designInfo || !this.designInfo.status) return '';
      return this.getStatusClass(this.designInfo.status);
    },

    // 状态文本
    statusText() {
      if (!this.designInfo || !this.designInfo.status) return '';
      return this.getStatusText(this.designInfo.status);
    },

    previewImages() {
      if (!this.designInfo) return [];

      const images = [];
      if (this.designInfo.previewImage) {
        images.push(this.designInfo.previewImage);
      }
      if (this.designInfo.previewImages) {
        const previewImages = this.designInfo.previewImages.split(',');
        images.push(...previewImages);
      }
      return images;
    }
  },
  onLoad(options) {
    this.designId = options.designId;
    this.orderId = options.orderId;
    this.productionStatus = options.productionStatus ? parseInt(options.productionStatus) : null;
    
    if (this.designId) {
      this.loadDesignDetail();
    }
  },
  methods: {
    // 加载设计详情
    async loadDesignDetail() {
      try {
        uni.showLoading({ title: '加载中...' });
        
        const result = await getDIYDesign(this.designId);
        if (result && result.code === 200) {
          this.designInfo = result.data;
        } else {
          throw new Error(result.message || '获取设计详情失败');
        }
      } catch (error) {
        console.error('获取设计详情失败:', error);
        uni.showToast({
          title: error.message || '获取设计详情失败',
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
      }
    },
    
    // 预览图片
    previewImage(image) {
      uni.previewImage({
        urls: this.previewImages,
        current: image
      });
    },
    
    // 编辑设计
    editDesign() {
      uni.navigateTo({
        url: `/subpackages/diy/designer?designId=${this.designId}&productId=${this.designInfo.productId}`
      });
    },
    
    // 创建订单
    createOrder() {
      uni.navigateTo({
        url: `/subpackages/diy/order?designId=${this.designId}&productId=${this.designInfo.productId}`
      });
    },
    
    // 格式化时间
    formatTime(time) {
      if (!time) return '';
      const date = new Date(time);
      return date.toLocaleString('zh-CN');
    },
    
    // 获取状态文本
    getStatusText(status) {
      const statusMap = {
        0: '草稿',
        1: '已保存',
        2: '已完成'
      };
      return statusMap[status] || '未知';
    },
    
    // 获取状态样式
    getStatusClass(status) {
      const classMap = {
        0: 'status-draft',
        1: 'status-saved',
        2: 'status-completed'
      };
      return classMap[status] || 'status-unknown';
    }
  }
};
</script>

<style lang="scss" scoped>
.design-detail-page {
  min-height: 100vh;
  background: #f5f5f5;
  
  .design-info-section {
    background: white;
    margin-bottom: 20rpx;
    padding: 30rpx;
    
    .design-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 30rpx;
      
      .design-title {
        font-size: 36rpx;
        font-weight: 600;
        color: #333;
      }
      
      .design-status {
        padding: 8rpx 16rpx;
        border-radius: 16rpx;
        font-size: 24rpx;
        
        &.status-draft {
          background: #fff3cd;
          color: #856404;
        }
        
        &.status-saved {
          background: #d1ecf1;
          color: #0c5460;
        }
        
        &.status-completed {
          background: #d4edda;
          color: #155724;
        }
      }
    }
    
    .section-title {
      font-size: 32rpx;
      font-weight: 500;
      color: #333;
      margin-bottom: 20rpx;
      display: block;
    }
    
    .design-preview-section {
      margin-bottom: 40rpx;
      
      .preview-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(200rpx, 1fr));
        gap: 20rpx;
      }
      
      .preview-image {
        width: 100%;
        height: 200rpx;
        border-radius: 10rpx;
        background: #f5f5f5;
      }
    }
    
    .design-details {
      .detail-item {
        display: flex;
        justify-content: space-between;
        padding: 20rpx 0;
        border-bottom: 1px solid #f0f0f0;
        
        &:last-child {
          border-bottom: none;
        }
        
        .detail-label {
          font-size: 28rpx;
          color: #666;
        }
        
        .detail-value {
          font-size: 28rpx;
          color: #333;
        }
      }
    }
  }
  
  .production-status-section {
    background: white;
    margin-bottom: 20rpx;
    padding: 30rpx;
    
    .status-timeline {
      .status-step {
        display: flex;
        align-items: flex-start;
        margin-bottom: 30rpx;
        
        &:last-child {
          margin-bottom: 0;
        }
        
        .step-icon {
          width: 60rpx;
          height: 60rpx;
          border-radius: 50%;
          background: #e0e0e0;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 20rpx;
          flex-shrink: 0;
          
          .step-number {
            font-size: 24rpx;
            color: #999;
            font-weight: 500;
          }
        }
        
        .step-content {
          flex: 1;
          
          .step-title {
            font-size: 28rpx;
            color: #999;
            display: block;
            margin-bottom: 8rpx;
          }
          
          .step-desc {
            font-size: 24rpx;
            color: #ccc;
          }
        }
        
        &.active {
          .step-icon {
            background: #ff6b6b;
            
            .step-number {
              color: white;
            }
          }
          
          .step-content {
            .step-title {
              color: #333;
            }
            
            .step-desc {
              color: #666;
            }
          }
        }
        
        &.current {
          .step-icon {
            background: linear-gradient(135deg, #ff6b6b, #ff8e8e);
            box-shadow: 0 0 20rpx rgba(255, 107, 107, 0.3);
          }
        }
      }
    }
  }
  
  .loading-section {
    padding: 100rpx;
    text-align: center;
    
    .loading-text {
      font-size: 28rpx;
      color: #999;
    }
  }
  
  .action-section {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    background: white;
    padding: 20rpx 30rpx;
    box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.1);
    display: flex;
    gap: 20rpx;
    
    .action-btn {
      flex: 1;
      padding: 24rpx;
      border-radius: 25rpx;
      text-align: center;
      
      .action-text {
        font-size: 28rpx;
        font-weight: 500;
      }
      
      &.primary {
        background: linear-gradient(135deg, #ff6b6b, #ff8e8e);
        color: white;
      }
      
      &.secondary {
        background: #f5f5f5;
        color: #333;
      }
    }
  }
}
</style>
