<template>
  <view class="container">
    <!-- 礼物列表 -->
    <view 
      class="product-card" 
      v-for="(item, index) in giftItems" 
      :key="index"
    >
      <view class="product-content">
        <!-- 选择按钮 -->
        <view 
          class="custom-checkbox" 
          :class="{ 'checked': item.checked }"
          @tap="toggleCheckItem(index)"
        >
          <view class="checkbox-bg"></view>
          <image 
            v-if="item.checked"
            class="checkbox-icon" 
            src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/cart/checked.png"
            mode="aspectFit"
          ></image>
        </view>
        
        <!-- 商品图片 -->
        <image class="product-image" :src="item.image" mode="aspectFill" @tap="viewProductDetail(item)"></image>
        
        <!-- 商品信息 -->
        <view class="product-info" @tap="viewProductDetail(item)">
          <view>
            <view class="product-title">{{ item.name }}</view>
            <view class="product-desc" v-if="item.desc">{{ item.desc }}</view>
            <view class="product-specs" v-if="item.selectedSpecs">
              <text class="specs-label">已选：</text>
              <text class="specs-value">{{ item.selectedSpecs }}</text>
            </view>
          </view>
          
          <view class="product-price-box">
            <text class="product-price">¥<text class="price-number">{{ item.price }}</text></text>
            <view class="product-quantity">
              <view class="quantity-btn" @tap="decreaseQuantity(index)">-</view>
              <view class="quantity-value">{{ item.quantity }}</view>
              <view class="quantity-btn" @tap="increaseQuantity(index)">+</view>
            </view>
          </view>
        </view>
        
        <!-- 删除按钮 -->
        <view class="del-btn" @tap="removeItem(index)">×</view>
      </view>
    </view>
    
    <!-- 空状态 -->
    <view class="placeholder" v-if="giftItems.length === 0"></view>
    
    <!-- 推荐商品组件 -->
    <recommended-products
      :position-bottom="true"
      add-button-text="已添加到礼袋"
      @addProduct="addToGiftBag"
    ></recommended-products>
    
    <!-- 底部结算栏 -->
    <view class="bottom-bar">
      <!-- 全选区域 -->
      <view class="checkbox-container" @tap="toggleCheckAll">
        <view class="custom-checkbox" :class="{ 'checked': allChecked }">
          <view class="checkbox-bg"></view>
          <image 
            v-if="allChecked"
            class="checkbox-icon" 
            src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/cart/checked.png"
            mode="aspectFit"
          ></image>
        </view>
        <text class="checkbox-text">全选</text>
        <text class="checkbox-count" v-if="giftItems.length > 0">({{ checkedCount }}/{{ giftItems.length }})</text>
      </view>
      
      <!-- 总价 -->
      <view class="total-info">
        <view class="total-price">¥ {{ totalPrice }}</view>
      </view>
      
      <!-- 购买按钮 -->
      <view class="buy-btn" @tap="handleBuy">立即购买</view>
    </view>
  </view>
</template>

<script>
import { getMyGifts } from '@/api/gift';
import { exchangePoints } from '@/api/point';
import recommendedProducts from '@/components/recommended-products/recommended-products';
import { mapState } from 'vuex';

export default {
  components: {
    recommendedProducts
  },
  
  data() {
    return {
      giftItems: [],
      returnTo: '', // 回赠对象
      giftId: '', // 回赠的礼物ID
      loading: false,
      isFromProductPage: false // 标记是否从商品页面进入
    };
  },
  
  computed: {
    ...mapState(['hasLogin']),
    // 是否全选
    allChecked() {
      if (this.giftItems.length === 0) return false;
      return this.giftItems.every(item => item.checked);
    },
    
    // 选中的商品数量
    checkedCount() {
      return this.giftItems.filter(item => item.checked).length;
    },
    
    // 总价
    totalPrice() {
      return this.giftItems
        .filter(item => item.checked)
        .reduce((total, item) => {
          return total + (parseFloat(item.price) * item.quantity);
        }, 0)
        .toFixed(2);
    }
  },
  
  onLoad(options) {
    // 检查是否是回赠礼物
    if (options.returnTo) {
      this.returnTo = options.returnTo;
      this.giftId = options.giftId;
      uni.setNavigationBarTitle({
        title: '回赠礼物'
      });
    }
    
    // 检查是否从商品页面传递了商品信息
    if (options.productInfo) {
      try {
        // 解析传递过来的商品信息
        const productInfo = JSON.parse(decodeURIComponent(options.productInfo));
        this.isFromProductPage = true;
        
        console.log('从商品页面接收到的商品信息:', productInfo);
        
        // 设置页面标题
        uni.setNavigationBarTitle({
          title: '送礼'
        });
        
        // 添加到礼物列表
        this.addProductToGiftItems(productInfo);
        
        // 显示提示
        uni.showToast({
          title: '已添加商品到礼袋',
          icon: 'success',
          duration: 1500
        });
      } catch (error) {
        console.error('解析商品信息失败', error);
        uni.showToast({
          title: '商品信息加载失败',
          icon: 'none'
        });
      }
    } else {
      // 加载礼袋商品
      this.loadGiftItems();
    }
  },
  
  methods: {
    // 加载礼袋商品
    loadGiftItems() {
      if (this.loading) return;
      
      this.loading = true;
      uni.showLoading({
        title: '加载中...'
      });
      
      // 调用API获取礼物列表
      getMyGifts({
        pageNum: 1,
        pageSize: 20
      }).then(res => {
        const list = res.data?.list || [];
        
        // 处理数据，确保与模板匹配
        this.giftItems = list.map(item => ({
          id: item.id,
          name: item.name || '礼物',
          desc: item.description || '这是一份精美礼物',
          image: item.image || 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/default.png',
          price: (item.price || 0).toString(),
          quantity: 1,
          checked: true
        }));
        
        this.loading = false;
        uni.hideLoading();
      }).catch(err => {
        console.error('获取礼物列表失败', err);
        uni.showToast({
          title: '获取礼物列表失败',
          icon: 'none'
        });
        this.loading = false;
        uni.hideLoading();
      });
    },
    
    // 添加商品到礼物列表
    addProductToGiftItems(product) {
      try {
        // 解析规格信息
        let selectedSpecs = '';
        if (product.productAttr) {
          try {
            // 尝试解析JSON规格数据
            const specData = typeof product.productAttr === 'string' 
              ? JSON.parse(product.productAttr) 
              : product.productAttr;
            
            if (Array.isArray(specData)) {
              selectedSpecs = specData.map(spec => spec.value).join(', ');
            }
          } catch (error) {
            console.error('解析商品规格信息失败', error);
          }
        }
        
        // 确保商品包含必要的字段
        const giftItem = {
          id: product.productId || product.id,
          name: product.productName || product.name || '礼物',
          desc: product.productSubTitle || product.subTitle || product.description || '这是一份精美礼物',
          image: product.productPic || product.pic || 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/default.png',
          price: (product.price || 0).toString(),
          quantity: product.quantity || 1,
          checked: true,
          productId: product.productId || product.id, // 保存原始商品ID
          skuId: product.productSkuId || product.skuId || null, // 如果有SKU ID也保存
          productAttr: product.productAttr || null, // 保存商品属性信息
          productSkuCode: product.productSkuCode || null, // 保存商品SKU编码
          productSn: product.productSn || null, // 保存商品编号
          productBrand: product.productBrand || null, // 保存商品品牌
          selectedSpecs: selectedSpecs // 保存已选规格
        };
        
        // 添加到列表
        this.giftItems.push(giftItem);
      } catch (error) {
        console.error('添加商品到礼物列表失败', error);
        uni.showToast({
          title: '添加商品失败',
          icon: 'none'
        });
      }
    },
    
    // 切换选中状态
    toggleCheckItem(index) {
      this.giftItems[index].checked = !this.giftItems[index].checked;
    },
    
    // 全选/取消全选
    toggleCheckAll() {
      const newStatus = !this.allChecked;
      this.giftItems.forEach(item => {
        item.checked = newStatus;
      });
    },
    
    // 增加数量
    increaseQuantity(index) {
      if (this.giftItems[index].quantity < 99) {
        this.giftItems[index].quantity++;
      }
    },
    
    // 减少数量
    decreaseQuantity(index) {
      if (this.giftItems[index].quantity > 1) {
        this.giftItems[index].quantity--;
      }
    },
    
    // 删除商品
    removeItem(index) {
      uni.showModal({
        title: '提示',
        content: '确定要删除该商品吗？',
        success: res => {
          if (res.confirm) {
            this.giftItems.splice(index, 1);
          }
        }
      });
    },
    
    // 添加到礼袋
    addToGiftBag(product) {
      // 检查是否登录
      if (!this.hasLogin) {
        uni.showModal({
          title: '提示',
          content: '请先登录后再操作',
          confirmText: '去登录',
          success: (res) => {
            if (res.confirm) {
              uni.navigateTo({
                url: '/pages/public/register'
              });
            }
          }
        });
        return;
      }
      
      // 查找是否已存在相同商品
      const existIndex = this.giftItems.findIndex(item => {
        // 检查产品ID和SKU ID（如果有的话）
        const sameProduct = item.productId === product.id || item.id === product.id;
        const sameSku = !product.skuId || (item.skuId === product.skuId);
        return sameProduct && sameSku;
      });
      
      if (existIndex !== -1) {
        // 已存在，增加数量
        this.giftItems[existIndex].quantity++;
        uni.showToast({
          title: '已增加数量',
          icon: 'success'
        });
      } else {
        // 不存在，先跳转到商品详情页选择规格
        uni.navigateTo({
          url: `/pages/product/product?id=${product.id || product.productId}`
        });
      }
    },
    
    // 购买
    handleBuy() {
      // 检查是否有选中的商品
      if (this.checkedCount === 0) {
        uni.showToast({
          title: '请选择礼物',
          icon: 'none'
        });
        return;
      }
      
      // 获取选中的商品
      const selectedItems = this.giftItems.filter(item => item.checked);
      
      // 如果是回赠礼物，使用积分兑换API
      if (this.returnTo && this.giftId) {
        uni.showLoading({
          title: '处理中...'
        });
        
        const itemIds = selectedItems.map(item => item.id);
        
        // 调用兑换积分API
        exchangePoints({
          itemIds: itemIds,
          returnTo: this.returnTo,
          giftId: this.giftId
        }).then(res => {
          uni.hideLoading();
          
          uni.showToast({
            title: '兑换成功',
            icon: 'success'
          });
          
          // 跳转到成功页面或订单页面
          setTimeout(() => {
            uni.redirectTo({
              url: '/pages/order/orderSuccess'
            });
          }, 1500);
        }).catch(err => {
          uni.hideLoading();
          console.error('兑换失败', err);
          
          uni.showToast({
            title: err.message || '兑换失败',
            icon: 'none'
          });
        });
      } else {
        // 普通礼物购买流程，跳转到创建订单页
        // 准备订单数据
        const cartItem = selectedItems[0]; // 目前只支持一次选择一个礼物
        
        // 准备商品信息，优先使用原始数据
        let productInfo = {};
        
        // 从产品页面传递过来的商品，尝试保留原始属性
        if (this.isFromProductPage && cartItem.productAttr) {
          productInfo = {
            productId: cartItem.productId,
            productName: cartItem.name,
            productPic: cartItem.image,
            productSkuId: cartItem.skuId,
            productSkuCode: cartItem.productSkuCode,
            productSn: cartItem.productSn,
            productBrand: cartItem.productBrand,
            productAttr: cartItem.productAttr,
            price: cartItem.price,
            quantity: cartItem.quantity,
            selectedSpecs: cartItem.selectedSpecs
          };
        } else {
          // 默认情况
          productInfo = {
            productId: cartItem.productId || cartItem.id,
            productName: cartItem.name,
            productPic: cartItem.image,
            price: cartItem.price,
            quantity: cartItem.quantity,
            productSkuId: cartItem.skuId || 0,
            selectedSpecs: cartItem.selectedSpecs
          };
        }
        
        // 将商品信息转为字符串并进行编码
        const productInfoStr = encodeURIComponent(JSON.stringify(productInfo));
        
        // 跳转到创建订单页面
        uni.navigateTo({
          url: `/pages/order/createOrder?productInfo=${productInfoStr}&deliveryType=0&isGift=1`
        });
      }
    },
    
    // 跳转到商品详情页
    viewProductDetail(item) {
      // 使用原始商品ID跳转到商品详情页
      const productId = item.productId || item.id;
      if (productId) {
        uni.navigateTo({
          url: `/pages/product/product?id=${productId}`
        });
      } else {
        uni.showToast({
          title: '商品信息不完整',
          icon: 'none'
        });
      }
    }
  }
};
</script>

<style lang="scss">
@charset "UTF-8";
/* 页面左右间距 */
/* 文字尺寸 */
/*文字颜色*/
/* 边框颜色 */
/* 图片加载中颜色 */
/* 行为相关颜色 */

page {
  background-color: #fff;
  padding-bottom: 100rpx;
}

.container {
  padding-bottom: 120rpx;
}

/* 搜索框 */
.search-box {
  background-color: #fff;
  padding: 20rpx 30rpx;
  position: sticky;
  top: 0;
  z-index: 10;
  display: flex;
  justify-content: center;
  align-items: center;
}

.search-input-box {
  display: flex;
  align-items: center;
  background-color: #f7f7f7;
  height: 72rpx;
  border-radius: 36rpx;
  padding: 0 30rpx;
  width: 100%;
  max-width: 690rpx;
  /* 与product-card宽度一致 */
}

.search-icon {
  width: 40rpx;
  height: 40rpx;
  margin-right: 20rpx;
}

.search-input {
  flex: 1;
  height: 100%;
  font-size: 28rpx;
  color: #333;
}

/* 商品信息卡片 */
.product-card {
  background-color: #fff;
  margin: 20rpx 0;
  padding: 30rpx;
  
  .product-content {
    display: flex;
    position: relative;
  }
}

/* 自定义复选框 */
.custom-checkbox {
  width: 40rpx;
  height: 40rpx;
  position: relative;
  margin-right: 20rpx;
  align-self: center;
  
  .checkbox-bg {
    position: absolute;
    left: 0;
    right: 0;
    top: 0;
    bottom: 0;
    border-radius: 50%;
    border: 2rpx solid #000;
    background: #fff;
  }
  
  .checkbox-icon {
    position: absolute;
    width: 24rpx;
    height: 24rpx;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    z-index: 10;
  }
  
  &.checked {
    .checkbox-bg {
      background: linear-gradient(270deg, #C8EBF8 0%, #E2F1F5 100%);
      border-color: transparent;
    }
  }
}

.product-image {
  width: 180rpx;
  height: 180rpx;
  border-radius: 8rpx;
  background-color: #f5f5f5;
}

.product-info {
  flex: 1;
  margin-left: 30rpx;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  
  .product-title {
    font-size: 28rpx;
    font-weight: 500;
    color: #333;
    margin-bottom: 10rpx;
  }
  
  .product-desc {
    font-size: 24rpx;
    color: #999;
    margin-bottom: 20rpx;
  }

  .product-specs {
    font-size: 24rpx;
    color: #666;
    margin-bottom: 10rpx;
    display: flex;
    flex-wrap: wrap;
  }

  .specs-label {
    color: #999;
    margin-right: 10rpx;
  }

  .specs-value {
    color: #333;
  }
}

.product-price-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  .product-price {
    font-size: 28rpx;
    color: #333;
  }
  
  .price-number {
    font-weight: 500;
    font-size: 32rpx;
  }
}

.product-quantity {
  display: flex;
  align-items: center;
  
  .quantity-btn {
    width: 52rpx;
    height: 52rpx;
    background-color: #f7f7f7;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 28rpx;
    color: #333;
  }
  
  .quantity-value {
    width: 60rpx;
    text-align: center;
    font-size: 28rpx;
  }
}

.del-btn {
  position: absolute;
  top: 10rpx;
  right: 10rpx;
  width: 40rpx;
  height: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  color: #999;
  background-color: #f7f7f7;
  border-radius: 50%;
}

/* 占位区域 */
.placeholder {
  height: 400rpx;
}

/* 底部购买按钮 */
.bottom-bar {
  position: fixed;
  bottom: 20rpx;
  left: 0;
  right: 0;
  height: 100rpx;
  background-color: #fff;
  display: flex;
  align-items: center;
  padding: 0 30rpx;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
  z-index: 11;
  will-change: transform; /* 优化性能 */
  transform: translateZ(0); /* 启用GPU加速 */
}

/* 底部全选区域 */
.checkbox-container {
  display: flex;
  align-items: center;
  height: 52rpx;
  margin-right: 30rpx;
  
  .checkbox-text {
    font-size: 28rpx;
    color: #000;
    margin-right: 20rpx;
  }
  
  .checkbox-count {
    font-size: 28rpx;
    color: #666;
  }
}

.total-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  
  .total-price {
    font-size: 32rpx;
    font-weight: 500;
    color: #333;
  }
}

.buy-btn {
  width: 240rpx;
  height: 72rpx;
  background: linear-gradient(270deg, #C8EBF8 0%, #E2F1F5 100%);
  color: #000;
  border-radius: 36rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  font-weight: 500;
}
</style> 