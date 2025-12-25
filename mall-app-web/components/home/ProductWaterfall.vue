<template>
  <view class="waterfall-container">
    <!-- 标题区域 -->
    <view class="waterfall-header">
      <view class="title-wrapper">
        <text class="title-text">更多好物</text>
        <image class="title-indicator" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/more-goods-indicator.png" mode="aspectFit" />
      </view>
    </view>

    <!-- 瀑布流内容 -->
    <scroll-view
      class="waterfall-scroll"
      scroll-y="true"
      @scrolltolower="handleScrollToLower"
      lower-threshold="100"
    >
      <view class="waterfall">
        <view
          class="item"
          v-for="(p, i) in list"
          :key="i"
          @tap="$emit('select', p)"
        >
          <image
            :src="p.cover || p.pic"
            class="thumb"
            mode="aspectFill"
            @load="$emit('image-load')"
            @error="$emit('image-error')"
          />
          <view class="info">
            <text class="name">{{ p.productName }}</text>
            <view class="price-sales-row">
              <text class="price">¥{{ p.price }}</text>
              <text class="sales">{{ getSalesText(p) }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 加载更多状态 -->
      <view class="load-more" v-if="list.length > 0">
        <view v-if="loading" class="loading">
          <text class="loading-text">加载中...</text>
        </view>
        <view v-else-if="!hasMore" class="no-more">
          <text class="no-more-text">没有更多商品了</text>
        </view>
        <view v-else class="load-more-btn" @tap="handleLoadMore">
          <text class="load-more-text">点击加载更多</text>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script>
export default {
  name: 'ProductWaterfall',
  props: {
    list: {
      type: Array,
      default: () => []
    },
    loading: {
      type: Boolean,
      default: false
    },
    hasMore: {
      type: Boolean,
      default: true
    }
  },
  methods: {
    getSalesText(product) {
      // 容错处理：检查销量数据
      const salesCount = product.salesCount || product.soldCount || 0;

      if (salesCount >= 0) {
        return `已售${salesCount}件`;
      } else {
        return '新品上市';
      }
    },

    // 处理滚动到底部
    handleScrollToLower() {
      if (!this.loading && this.hasMore) {
        this.$emit('load-more');
      }
    },

    // 处理点击加载更多
    handleLoadMore() {
      if (!this.loading && this.hasMore) {
        this.$emit('load-more');
      }
    }
  }
};
</script>

<style scoped lang="scss">
.waterfall-container {
  margin: 20rpx 0;

  .waterfall-scroll {
    max-height: 1200rpx; // 设置最大高度以支持滚动
  }

  .waterfall-header {
    padding: 0 24rpx 20rpx;

    .title-wrapper {
      position: relative;
      display: flex;
      align-items: center;

      .title-text {
        position: relative;
        z-index: 2;
        font-size: 32rpx;
        color: #000000;
        font-weight: 500;
        margin-right: 12rpx;
      }

      .title-indicator {
        position: relative;
        z-index: 1;
        width: 32rpx;
        height: 32rpx;
        margin-top: 20rpx;
        margin-left: -30rpx; /* 让指示器稍微重叠在文字右边 */
      }
    }
  }
}

.waterfall {
  column-count: 2;
  column-gap: 16rpx;
  padding: 0 24rpx;

  .item {
    break-inside: avoid;
    margin-bottom: 24rpx;
    background: #fff;
    border-radius: 8rpx;
    overflow: hidden;
    box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.04);

    .thumb {
      width: 100%;
      height: 320rpx;
    }
    .info {
      padding: 16rpx;
      .name {
        font-size: 26rpx;
        color: #000;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }

      .price-sales-row {
        display: flex;
        align-items: baseline;
        justify-content: space-between;
        margin-top: 8rpx;
        gap: 8rpx;

        .price {
          font-size: 36rpx;
          font-weight: 600;
          color: #647D00;
          flex: 0 0 auto;
        }

        .sales {
          font-size: 24rpx;
          font-weight: 400;
          color: #999999;
          flex: 1 1 auto;
          text-align: right;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }
      }
    }
  }

  .load-more {
    padding: 40rpx 0;
    text-align: center;

    .loading {
      .loading-text {
        font-size: 28rpx;
        color: #999999;
      }
    }

    .no-more {
      .no-more-text {
        font-size: 28rpx;
        color: #cccccc;
      }
    }

    .load-more-btn {
      padding: 20rpx 40rpx;
      background: #f5f5f5;
      border-radius: 40rpx;
      display: inline-block;

      .load-more-text {
        font-size: 28rpx;
        color: #666666;
      }
    }
  }
}
</style>