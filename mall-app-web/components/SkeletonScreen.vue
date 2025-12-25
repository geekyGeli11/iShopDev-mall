<template>
  <view class="skeleton-screen" v-if="visible">
    <!-- 骨架屏类型：分类页 -->
    <view v-if="type === 'category'" class="skeleton-category">
      <!-- 顶部区域骨架 -->
      <view class="skeleton-header">
        <view class="skeleton-nav-bar"></view>
        <view class="skeleton-store-info">
          <view class="skeleton-avatar"></view>
          <view class="skeleton-text skeleton-text-long"></view>
        </view>
        <view class="skeleton-search-bar"></view>
      </view>
      
      <!-- 主体内容骨架 -->
      <view class="skeleton-main">
        <!-- 左侧分类骨架 -->
        <view class="skeleton-sidebar">
          <view v-for="i in 6" :key="i" class="skeleton-category-item">
            <view class="skeleton-icon"></view>
            <view class="skeleton-text skeleton-text-short"></view>
          </view>
        </view>
        
        <!-- 右侧商品列表骨架 -->
        <view class="skeleton-content">
          <view class="skeleton-tabs">
            <view v-for="i in 4" :key="i" class="skeleton-tab"></view>
          </view>
          <view class="skeleton-product-grid">
            <view v-for="i in 6" :key="i" class="skeleton-product-card">
              <view class="skeleton-product-image"></view>
              <view class="skeleton-product-info">
                <view class="skeleton-text skeleton-text-long"></view>
                <view class="skeleton-text skeleton-text-medium"></view>
                <view class="skeleton-text skeleton-text-short"></view>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 骨架屏类型：首页 -->
    <view v-else-if="type === 'home'" class="skeleton-home">
      <!-- 顶部导航骨架 -->
      <view class="skeleton-nav"></view>
      
      <!-- 轮播图骨架 -->
      <view class="skeleton-banner"></view>
      
      <!-- 快捷入口骨架 -->
      <view class="skeleton-quick-grid">
        <view v-for="i in 8" :key="i" class="skeleton-quick-item">
          <view class="skeleton-icon"></view>
          <view class="skeleton-text skeleton-text-short"></view>
        </view>
      </view>
      
      <!-- 商品卡片骨架 -->
      <view class="skeleton-cards">
        <view v-for="i in 3" :key="i" class="skeleton-card">
          <view class="skeleton-card-image"></view>
          <view class="skeleton-card-content">
            <view class="skeleton-text skeleton-text-long"></view>
            <view class="skeleton-text skeleton-text-medium"></view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'SkeletonScreen',
  props: {
    // 是否显示骨架屏
    visible: {
      type: Boolean,
      default: false
    },
    // 骨架屏类型：category（分类页）、home（首页）
    type: {
      type: String,
      default: 'category'
    }
  }
};
</script>

<style lang="scss" scoped>
// 骨架屏基础样式
.skeleton-screen {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #F8F8F8;
  z-index: 9999;
  overflow: hidden;
}

// 骨架屏动画
@keyframes skeleton-loading {
  0% {
    background-position: -200rpx 0;
  }
  100% {
    background-position: calc(200rpx + 100%) 0;
  }
}

// 骨架屏元素基础样式
.skeleton-text,
.skeleton-icon,
.skeleton-avatar,
.skeleton-image,
.skeleton-nav-bar,
.skeleton-search-bar,
.skeleton-banner,
.skeleton-card-image,
.skeleton-product-image,
.skeleton-tab {
  background: linear-gradient(
    90deg,
    #f0f0f0 25%,
    #e0e0e0 50%,
    #f0f0f0 75%
  );
  background-size: 200rpx 100%;
  animation: skeleton-loading 1.5s ease-in-out infinite;
  border-radius: 8rpx;
}

// 分类页骨架屏
.skeleton-category {
  display: flex;
  flex-direction: column;
  height: 100vh;
  
  .skeleton-header {
    background: linear-gradient(to bottom, rgba(221, 255, 153, 1), rgba(221, 255, 153, 0.8));
    padding: 20rpx 30rpx;
    
    .skeleton-nav-bar {
      height: 88rpx;
      margin-bottom: 20rpx;
    }
    
    .skeleton-store-info {
      display: flex;
      align-items: center;
      gap: 16rpx;
      margin-bottom: 20rpx;
      
      .skeleton-avatar {
        width: 96rpx;
        height: 96rpx;
        border-radius: 16rpx;
      }
      
      .skeleton-text-long {
        flex: 1;
        height: 40rpx;
      }
    }
    
    .skeleton-search-bar {
      height: 72rpx;
      border-radius: 20rpx;
    }
  }
  
  .skeleton-main {
    display: flex;
    flex: 1;
    margin-top: 20rpx;
    
    .skeleton-sidebar {
      width: 94px;
      background-color: #FFFFFF;
      padding: 20rpx 0;
      
      .skeleton-category-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        margin-bottom: 30rpx;
        
        .skeleton-icon {
          width: 54rpx;
          height: 54rpx;
          border-radius: 50%;
          margin-bottom: 8rpx;
        }
        
        .skeleton-text-short {
          width: 60rpx;
          height: 24rpx;
        }
      }
    }
    
    .skeleton-content {
      flex: 1;
      padding: 20rpx;
      
      .skeleton-tabs {
        display: flex;
        gap: 20rpx;
        margin-bottom: 20rpx;
        
        .skeleton-tab {
          flex: 1;
          height: 60rpx;
        }
      }
      
      .skeleton-product-grid {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 20rpx;
        
        .skeleton-product-card {
          background: #FFFFFF;
          border-radius: 16rpx;
          overflow: hidden;
          
          .skeleton-product-image {
            width: 100%;
            height: 300rpx;
          }
          
          .skeleton-product-info {
            padding: 16rpx;
            
            .skeleton-text {
              margin-bottom: 12rpx;
              
              &.skeleton-text-long {
                width: 100%;
                height: 32rpx;
              }
              
              &.skeleton-text-medium {
                width: 80%;
                height: 28rpx;
              }
              
              &.skeleton-text-short {
                width: 50%;
                height: 24rpx;
              }
            }
          }
        }
      }
    }
  }
}

// 首页骨架屏
.skeleton-home {
  .skeleton-nav {
    height: 88rpx;
    margin: 20rpx 30rpx;
  }
  
  .skeleton-banner {
    height: 400rpx;
    margin: 20rpx 30rpx;
    border-radius: 16rpx;
  }
  
  .skeleton-quick-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20rpx;
    padding: 20rpx 30rpx;
    
    .skeleton-quick-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      
      .skeleton-icon {
        width: 80rpx;
        height: 80rpx;
        border-radius: 50%;
        margin-bottom: 8rpx;
      }
      
      .skeleton-text-short {
        width: 60rpx;
        height: 24rpx;
      }
    }
  }
  
  .skeleton-cards {
    padding: 20rpx 30rpx;
    
    .skeleton-card {
      background: #FFFFFF;
      border-radius: 16rpx;
      margin-bottom: 20rpx;
      overflow: hidden;
      
      .skeleton-card-image {
        width: 100%;
        height: 300rpx;
      }
      
      .skeleton-card-content {
        padding: 20rpx;
        
        .skeleton-text {
          margin-bottom: 12rpx;
          
          &.skeleton-text-long {
            width: 100%;
            height: 32rpx;
          }
          
          &.skeleton-text-medium {
            width: 70%;
            height: 28rpx;
          }
        }
      }
    }
  }
}
</style>

