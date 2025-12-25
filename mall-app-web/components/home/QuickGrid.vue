<template>
  <view class="quick-grid">
    <!-- 背景图片 -->
    <image class="grid-bg" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/hot-product-background.png" mode="scaleToFill" />

    <!-- 标题区域 -->
    <view class="header">
      <view class="title-container">
        <view class="title-group">
          <text class="title-text">热榜推荐</text>
          <view class="title-bg"></view>
        </view>
      </view>
      <view class="more-container" @tap="$emit('more')">
        <text class="more-text">更多商品</text>
        <image src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/arrow_right_gray.svg" class="arrow-icon" />
      </view>
    </view>

    <!-- 横向滚动商品列表 -->
    <scroll-view class="scroll-container" scroll-x="true" show-scrollbar="false">
      <view class="item-list">
        <view
          class="grid-item"
          v-for="(item, index) in items"
          :key="index"
          @tap="$emit('select', item)"
        >
          <view class="item-content">
            <image
              :src="getImageUrl(item, index)"
              class="item-image"
              mode="aspectFill"
              @load="$emit('image-load')"
              @error="$emit('image-error')"
            />
            <view class="item-tag">
              <text class="tag-text">{{ item.tag || getTagText(index) }}</text>
            </view>
          </view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script>
export default {
  name: 'QuickGrid',
  props: {
    // 数组元素：{ name: string, icon: string, cover: string, pic: string, tag: string }
    items: {
      type: Array,
      default: () => []
    }
  },
  methods: {
    // 获取图片URL，如果商品没有图片则使用默认图片
    getImageUrl(item, index) {
      // 优先使用商品的封面图或图片
      if (item.cover) return item.cover;
      if (item.pic) return item.pic;
      if (item.icon) return item.icon;
      
      // 如果没有图片，使用预设的商品图片
      const defaultImages = [
        'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/hot_product1.jpg',
        'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/hot_product2.jpg', 
        'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/hot_product3.jpg',
        'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/hot_product4.jpg',
        'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/grid_product1.jpg',
        'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/grid_product2.jpg',
        'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/grid_product3.jpg',
        'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/grid_product4.jpg'
      ];
      
      return defaultImages[index % defaultImages.length];
    },
    
    // 获取标签文字
    getTagText(index) {
      const tags = ['热门风格', '热门商品', '新品推荐', '限时特惠'];
      return tags[index % tags.length];
    }
  }
};
</script>

<style lang="scss" scoped>
.quick-grid {
  position: relative;
  border-radius: 16rpx;
  margin: 24rpx 20rpx;
  box-shadow: 0 0 8rpx 0 rgba(8, 27, 12, 0.05);
  overflow: hidden;

  .grid-bg {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: 1;
  }

  .header {
    position: relative;
    z-index: 2;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 24rpx 32rpx 20rpx;

    .title-container {
      position: relative;
      
      .title-group {
        position: relative;
        width: 128rpx;
        height: 40rpx;
        
        .title-text {
          position: absolute;
          width: 128rpx;
          height: 40rpx;
          left: 0;
          top: 0;
          font-family: 'HelloFont WenYiHei', -apple-system, BlinkMacSystemFont, sans-serif;
          font-style: normal;
          font-weight: 400;
          font-size: 32rpx;
          line-height: 40rpx;
          display: flex;
          align-items: center;
          color: #333333;
          z-index: 1;
        }
        
        .title-bg {
          position: absolute;
          width: 128rpx;
          height: 18rpx;
          left: 0;
          top: 22rpx;
          background: linear-gradient(180deg, #A7FD03 0%, #B2F597 100%);
        }
      }
    }

    .more-container {
      display: flex;
      align-items: center;
      gap: 8rpx;

      .more-text {
        font-family: 'PingFang SC', -apple-system, BlinkMacSystemFont, sans-serif;
        font-size: 24rpx;
        font-weight: 400;
        color: #9FA19D;
        line-height: 1.4;
      }

      .arrow-icon {
        width: 28rpx;
        height: 28rpx;
      }
    }
  }

  .scroll-container {
    position: relative;
    z-index: 2;
    width: 100%;
    white-space: nowrap;
    padding-bottom: 24rpx;

    .item-list {
      display: flex;
      padding: 0 32rpx;
      gap: 24rpx;
    }

    .grid-item {
      flex-shrink: 0;
      width: 168rpx;

      .item-content {
        position: relative;
        border-radius: 8rpx;
        overflow: hidden;
        border: 2rpx solid #A9FF00;

        .item-image {
          width: 100%;
          height: 208rpx;
          object-fit: cover;
        }

        .item-tag {
          position: absolute;
          bottom: 8rpx;
          left: 50%;
          transform: translateX(-50%);
          background: #282921;
          border-radius: 48rpx;
          padding: 4rpx 12rpx;

          .tag-text {
            font-family: 'PingFang SC', -apple-system, BlinkMacSystemFont, sans-serif;
            font-size: 24rpx;
            font-weight: 400;
            color: #A9FF00;
            line-height: 1.8;
          }
        }
      }
    }
  }
}
</style> 