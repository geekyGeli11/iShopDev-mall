<template>
  <view class="card-flow">
    <view class="card-list">
      <view class="card" v-for="(c, i) in cards" :key="i" @tap="$emit('select', c)">
        <!-- 卡片背景图片 -->
        <image class="card-bg" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/sales-card-background.png" mode="scaleToFill" />

        <!-- 商品封面 -->
        <view class="card-image-wrapper">
          <image
            :src="c.cover || c.pic"
            class="card-image"
            mode="aspectFill"
            @load="$emit('image-load')"
            @error="$emit('image-error')"
          />
          <!-- Tag标签 -->
          <view class="card-tag">
            <image class="tag-bg" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/sales-tag-background.png" mode="scaleToFill" />
            <text class="tag-text">{{ getDiscountText(c) }}</text>
          </view>
        </view>

        <!-- 商品信息区域 -->
        <view class="card-info">
          <!-- 商品标题 -->
          <text class="product-title">{{ c.productName || c.name || '商品名称商品名称商品名称' }}</text>

          <!-- 价格和按钮行 -->
          <view class="price-action-row">
            <view class="price-container">
              <text class="product-price" v-if="getFlashPrice(c)">¥{{ getFlashPrice(c) }}</text>
              <text class="original-price" v-if="c.flashPromotionPrice && c.flashPromotionPrice < c.price">¥{{ c.price }}</text>
            </view>
            <view class="order-btn" @tap.stop="handleOrder(c)">
              <text class="btn-text">去下单</text>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default { 
  name: 'CardFlow', 
  props: { 
    cards: { 
      type: Array, 
      default: () => [] 
    } 
  },
  methods: {
    handleOrder(item) {
      this.$emit('order', item);
    },

    // 计算折扣文字
    getDiscountText(item) {
      const flashPrice = item.flashPromotionPrice; // 优惠价
      const originalPrice = item.price; // 原价

      console.log('CardFlow折扣计算:', {
        flashPrice,
        originalPrice,
        item
      });

      if (flashPrice && originalPrice && originalPrice > 0 && flashPrice < originalPrice) {
        const discount = (flashPrice / originalPrice * 100).toFixed(2);
        return `限时${discount}折`;
      }

      return '限时优惠';
    },

    // 获取显示价格（优惠价优先）
    getFlashPrice(item) {
      return item.flashPromotionPrice || item.price;
    }
  }
};
</script>

<style scoped lang="scss">
.card-flow {
  padding: 0 20rpx;
  margin: 20rpx 0;

  .card-list {
    display: flex;
    flex-direction: column;
    gap: 20rpx;
  }

  .card {
    position: relative;
    width: 100%;
    border-radius: 16rpx;
    overflow: hidden;
    box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);

    .card-bg {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      z-index: 1;
    }

    .card-image-wrapper {
      position: relative;
      z-index: 2;
      width: 100%;
      height: 368rpx;

      .card-image {
        width: calc(100% - 60rpx);
        height: calc(100% - 80rpx);
        object-fit: cover;
        margin: 80rpx 30rpx 0 30rpx;
        border-radius: 8rpx;
      }

      .card-tag {
        position: absolute;
        top: 41rpx; // 向上移动5rpx (46rpx - 5rpx = 41rpx)
        left: 18rpx;
        width: 142rpx;
        height: 57rpx;
        display: flex;
        align-items: center;
        justify-content: center;

        .tag-bg {
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          z-index: 1;
        }

        .tag-text {
          position: absolute;
          top: -6rpx; // 向上移动5rpx
          left: 0;
          width: 100%;
          height: 100%;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 22rpx;
          font-weight: 500;
          color: #000000;
          z-index: 2;
          line-height: 1;
          text-align: center;
          box-sizing: border-box;
        }
      }
    }
    
    .card-info {
      position: relative;
      z-index: 2;
      padding: 24rpx;
    }
    
    .product-title {
      font-size: 32rpx;
      font-weight: 500;
      color: #333333;
      line-height: 1.4;
      margin-bottom: 16rpx;
      display: block;
      /* 最多显示2行 */
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
    }
    
    .price-action-row {
      display: flex;
      justify-content: space-between;
      align-items: center;
      width: 100%;
    }

    .price-container {
      display: flex;
      flex-direction: row;
      align-items: center;
      flex: 1;
      gap: 16rpx;
    }

    .product-price {
      font-size: 36rpx;
      font-weight: 700;
      color: #647D00;
      line-height: 1.2;
    }

    .original-price {
      font-size: 24rpx;
      font-weight: 400;
      color: #999999;
      text-decoration: line-through;
      line-height: 1.2;
    }
    
    .order-btn {
      background-color: #20201E;
      border-radius: 32rpx;
      padding: 12rpx 32rpx;
      flex: 0 0 auto;
      margin-left: auto;
    }
    
    .btn-text {
      font-size: 28rpx;
      font-weight: 400;
      color: #A9FF00;
      white-space: nowrap;
    }
  }
}
</style> 