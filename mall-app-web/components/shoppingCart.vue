<template>
  <movable-area class="shopping-cart-area">
    <movable-view class="shopping-cart" :x="x" :y="y" direction="all" @change="onMove" @tap="onClick">
      <image class="shopping-cart-icon" :src="iconSrc" />
      <view class="shopping-cart-number" v-if="cartCount > 0">{{ cartCount }}</view>
    </movable-view>
  </movable-area>
</template>

<script>
export default {
  name: "shoppingCart",
  props: {
    cartCount: {
      type: Number,
      default: 0, // 默认购物车商品数量
    },
    iconSrc: {
      type: String,
      default: "https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/shoping-cart.png", // 默认购物车图标
    },
    defaultX: {
      type: Number,
      default: 594, // 初始 x 位置 (750rpx - 96rpx - 60rpx = 594rpx) 增加右侧间距
    },
    defaultY: {
      type: Number,
      default: 700, // 初始 y 位置
    },
  },
  data() {
    return {
      x: this.defaultX, // 当前 x 位置
      y: this.defaultY, // 当前 y 位置
    };
  },
  methods: {
    onMove(event) {
      const { x, y } = event.detail;
      this.x = x;
      this.y = y;
      this.$emit("position-change", { x, y }); // 触发位置变化事件
    },
	onClick() {
	      this.$emit("click-cart"); // 触发点击事件
	}
  },
};
</script>

<style lang="scss" scoped>
.shopping-cart-area {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  pointer-events: none;
  z-index: 9999;

  .shopping-cart {
    width: 96rpx;
    height: 96rpx;
    pointer-events: auto;

    .shopping-cart-icon {
      width: 96rpx;
      height: 96rpx;
    }

    .shopping-cart-number {
      position: absolute;
      top: 0;
      right: 12rpx;
      padding: 0 8rpx;
      min-width: 30rpx;
      height: 30rpx;
      background: #ff0000;
      border-radius: 14rpx;
      font-weight: bold;
      font-size: 22rpx;
      color: #ffffff;
      text-align: center;
    }
  }
}
</style>

