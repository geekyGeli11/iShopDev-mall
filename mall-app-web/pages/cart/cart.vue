<template>
  <view class="container">
    <!-- 自定义导航栏 -->
    <nav-bar :back="true" :placeholder="true" :bg-color="'#FFFFFF'" :title="'购物车(' + cartList.length + ')'" @ready="onNavBarReady">
    </nav-bar>

    <!-- 空购物车状态 -->
    <view class="empty" v-if="!hasLogin || empty === true">
      <image src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/cart/empty-cart.png" mode="aspectFit"></image>
      <view class="empty-tips">
        <text class="tip-line">购物车还是空荡荡的哦，</text>
        <text class="tip-line">快动动手指，开启快乐购物之旅！</text>
      </view>
      <!-- <view class="empty-recommended">
        <recommended-products @addProduct="addToEmptyCart"></recommended-products>
      </view> -->
    </view>
    
    <!-- 购物车有商品 -->
    <view v-else class="cart-content">
      <!-- 管理按钮区域 -->
      <view class="manage-section">
        <view class="manage-btn-wrapper">
          <text class="nav-manage" @tap="toggleManageMode">
            {{manageMode ? '完成' : '管理'}}
          </text>
        </view>
      </view>

      <!-- 购物车列表 -->
      <view class="cart-list">
        <view 
          v-for="(item, index) in cartList" 
          :key="item.id"
          class="cart-item-wrapper"
        >
          <view 
            class="cart-item"
            :class="{ 'manage-mode': manageMode }"
          >
          <!-- 左侧复选框 -->
          <view class="checkbox-wrapper">
            <image 
              :src="item.checked ? 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/cart/checked.png' : 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/cart/uncheck.png'"
              class="checkbox-image"
              @tap="check('item', index)"
            ></image>
          </view>

          <!-- 商品图片 -->
          <view class="product-image-wrapper">
            <image 
              :src="item.productPic" 
              mode="aspectFill" 
              class="product-image"
              lazy-load 
              @load="onImageLoad('cartList', index)"
              @error="onImageError('cartList', index)"
            ></image>
          </view>

          <!-- 商品信息 -->
          <view class="product-info">
            <text class="product-name">{{item.productName}}</text>
            <text class="product-attr">{{item.spDataStr}}</text>
            
            <!-- 价格和数量区域 -->
            <view class="price-quantity-wrapper">
              <text class="product-price">¥ {{item.price}}</text>
              
              <!-- 数量选择器 -->
              <view class="quantity-selector" v-if="!manageMode">
                <view class="quantity-btn" @tap="decreaseQuantity(index)">
                  <image src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/cart/number_less.png" class="quantity-icon"></image>
                </view>
                <view class="quantity-display">
                  <text class="quantity-text">{{item.quantity}}</text>
                </view>
                <view class="quantity-btn" @tap="increaseQuantity(index)">
                  <image src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/cart/number_more.png" class="quantity-icon"></image>
                </view>
              </view>

              <!-- 数量显示（管理模式） -->
              <view class="quantity-display-manage" v-else>
                <text class="quantity-text-manage">×{{item.quantity}}</text>
              </view>
            </view>
          </view>

          </view>
          
          <!-- 删除按钮（管理模式） -->
          <view class="delete-btn" v-if="manageMode" @tap="handleDeleteCartItem(index)">
            <image src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/cart/cart_delete.png" class="delete-icon"></image>
            <text class="delete-text">删除</text>
          </view>
        </view>
      </view>
      
      <!-- 推荐商品 -->
      <!-- <view class="cart-recommended">
        <recommended-products @addProduct="addToCart"></recommended-products>
      </view> -->
      
      <!-- 底部操作栏 -->
      <view class="action-section">
        <!-- 全选区域 -->
        <view class="select-all-wrapper">
          <image 
            :src="allChecked ? 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/cart/checked.png' : 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/cart/uncheck.png'"
            class="select-all-checkbox"
            @tap="check('all')"
          ></image>
          <text class="select-all-text">全选</text>
        </view>

        <!-- 合计和结算 -->
        <view class="total-checkout-wrapper">
          <text class="total-text">合计：¥{{total}}</text>
          <view class="checkout-btn" @tap="createOrder">
            <text class="checkout-text">结算({{getTotalSelectedCount()}})</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import NavBar from '@/components/nav-bar.vue';
import recommendedProducts from '@/components/recommended-products/recommended-products';
import { fetchCartList, deletCartItem, updateQuantity, clearCartList, addCartItem } from '@/api/cart.js';

export default {
  components: {
    'nav-bar': NavBar,
    recommendedProducts
  },
  data() {
    return {
      hasLogin: false,
      empty: true,
      cartList: [],
      allChecked: false,
      total: '0.00',
      totalCount: 0,
      manageMode: false,
      navBarHeight: 0
    };
  },
  onLoad() {
    // 检查登录状态
    this.checkLoginStatus();
    // 加载购物车数据
    this.loadCartData();
  },
  onShow() {
    // 检查登录状态
    this.checkLoginStatus();
    // 刷新购物车数据
    this.loadCartData();
  },
  onPullDownRefresh() {
    this.loadCartData(() => {
      uni.stopPullDownRefresh();
    });
  },
  methods: {
    // 导航栏准备完成
    onNavBarReady(navBarHeight) {
      this.navBarHeight = navBarHeight;
    },
    // 检查登录状态
    checkLoginStatus() {
      // 使用统一的tokenInfo检查登录状态
      const tokenInfo = uni.getStorageSync('tokenInfo');
      const userInfo = uni.getStorageSync('userInfo');

      let hasValidToken = false;
      if (tokenInfo) {
        try {
          const loginData = JSON.parse(tokenInfo);
          hasValidToken = !!(loginData && loginData.token && loginData.tokenHead && loginData.openId);
        } catch (e) {
          console.error('解析tokenInfo失败:', e);
          hasValidToken = false;
        }
      }

      // 更严格的登录状态检查
      this.hasLogin = !!(hasValidToken && userInfo && userInfo.id);

      console.log('登录状态检查:', this.hasLogin, 'tokenInfo:', !!hasValidToken, 'userInfo:', !!userInfo);

      // 如果有tokenInfo但没有登录状态，可能需要重新登录
      if (hasValidToken && !this.hasLogin) {
        console.warn('TokenInfo存在但用户信息不完整，可能需要重新登录');
      }
    },
    
    // 切换管理模式
    toggleManageMode() {
      this.manageMode = !this.manageMode;
    },
    
    // 加载购物车数据
    async loadCartData(callback) {
      uni.showLoading({
        title: '加载中...'
      });
      
      try {
        const result = await fetchCartList();
        console.log('购物车接口返回:', result);
        
        if (result && result.data) {
          // 处理返回的购物车数据
          this.cartList = result.data.map(item => ({
            ...item,
            checked: true,
            loaded: '',
            // 解析商品规格信息
            spDataStr: this.parseProductAttr(item.productAttr)
          }));
          
          // 预加载图片处理方案
          this.checkImagesValidity();
          
          this.empty = this.cartList.length === 0;
          this.calcTotal();
          
          // 如果接口能正常返回数据且有商品，说明用户状态正常，即使没有严格的登录验证也应该显示
          if (this.cartList.length > 0 && !this.hasLogin) {
            console.log('购物车有数据但登录状态为false，可能是游客模式或登录状态检查问题，允许显示购物车');
            this.hasLogin = true; // 临时设置为true以显示购物车内容
          }
          
          console.log('购物车数据处理完成:', {
            hasLogin: this.hasLogin,
            empty: this.empty,
            cartListLength: this.cartList.length
          });
        } else {
          throw new Error('获取购物车数据失败');
        }
      } catch (error) {
        console.error('获取购物车数据失败', error);
        uni.showToast({
          title: '获取购物车失败',
          icon: 'none'
        });
        this.empty = true;
      } finally {
        uni.hideLoading();
        if (callback) callback();
      }
    },
    
    // 计算总价
    calcTotal() {
      let total = 0;
      let count = 0;
      let allChecked = true;
      
      this.cartList.forEach(item => {
        if (item.checked) {
          total += item.price * item.quantity;
          count += item.quantity;
        } else {
          allChecked = false;
        }
      });
      
      this.total = total.toFixed(2);
      this.totalCount = count;
      this.allChecked = allChecked && this.cartList.length > 0;
    },

    // 获取选中商品总数量
    getTotalSelectedCount() {
      return this.cartList.filter(item => item.checked).length;
    },
    
    // 商品图片加载完成
    onImageLoad(listName, index) {
      this[listName][index].loaded = 'loaded';
      // 强制更新视图
      this.$forceUpdate();
    },
    
    // 商品图片加载失败
    onImageError(listName, index) {
      this[listName][index].productPic = 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/errorImage.jpg';
      // 强制更新视图
      this.$forceUpdate();
    },

    // 增加数量
    async increaseQuantity(index) {
      const item = this.cartList[index];
      const newQuantity = item.quantity + 1;
      await this.updateItemQuantity(index, newQuantity);
    },

    // 减少数量
    async decreaseQuantity(index) {
      const item = this.cartList[index];
      if (item.quantity > 1) {
        const newQuantity = item.quantity - 1;
        await this.updateItemQuantity(index, newQuantity);
      }
    },

    // 更新商品数量
    async updateItemQuantity(index, newQuantity) {
      const item = this.cartList[index];
      const oldQuantity = item.quantity;
      item.quantity = newQuantity;
      this.calcTotal();
      
      try {
        // 调用更新购物车数量接口
        const result = await updateQuantity({
          id: item.id,
          quantity: newQuantity
        });
        
        if (!result || result.code !== 200) {
          // 如果更新失败，回滚数量
          item.quantity = oldQuantity;
          this.calcTotal();
          throw new Error('更新购物车数量失败');
        }
      } catch (error) {
        console.error('更新购物车数量失败', error);
        uni.showToast({
          title: '更新数量失败',
          icon: 'none'
        });
      }
    },
    
    // 选择/取消选择
    check(type, index) {
      if (type === 'item') {
        this.cartList[index].checked = !this.cartList[index].checked;
      } else if (type === 'all') {
        const checked = !this.allChecked;
        this.cartList.forEach(item => {
          item.checked = checked;
        });
      }
      this.calcTotal();
    },
    
    // 删除购物车商品
    handleDeleteCartItem(index) {
      const item = this.cartList[index];
      
      uni.showModal({
        title: '提示',
        content: '确定要删除该商品吗？',
        success: async (res) => {
          if (res.confirm) {
            uni.showLoading({
              title: '删除中...'
            });
            
            try {
              const result = await deletCartItem({
                ids: [item.id]
              });
              
              if (result && result.code === 200) {
                this.cartList.splice(index, 1);
                this.empty = this.cartList.length === 0;
                this.calcTotal();
                
                uni.showToast({
                  title: '删除成功'
                });
              } else {
                throw new Error('删除购物车商品失败');
              }
            } catch (error) {
              console.error('删除购物车商品失败', error);
              uni.showToast({
                title: '删除失败',
                icon: 'none'
              });
            } finally {
              uni.hideLoading();
            }
          }
        }
      });
    },
    
    // 清空购物车
    async clearCart() {
      if (this.cartList.length === 0) return;
      
      uni.showModal({
        title: '提示',
        content: '确定要清空购物车吗？',
        success: async (res) => {
          if (res.confirm) {
            uni.showLoading({
              title: '清空中...'
            });
            
            try {
              const result = await clearCartList();
              
              if (result && result.code === 200) {
                this.cartList = [];
                this.empty = true;
                this.calcTotal();
                
                uni.showToast({
                  title: '已清空购物车'
                });
              } else {
                throw new Error('清空购物车失败');
              }
            } catch (error) {
              console.error('清空购物车失败', error);
              uni.showToast({
                title: '清空失败',
                icon: 'none'
              });
            } finally {
              uni.hideLoading();
            }
          }
        }
      });
    },
    
    // 添加空购物车时推荐的商品
    addToEmptyCart(product) {
      // 转到登录页面
      if (!this.hasLogin) {
        uni.navigateTo({
          url: '/pages/public/register'
        });
        return;
      }
      
      // 添加到购物车
      this.addToCart(product);
    },
    
    // 添加商品到购物车
    async addToCart(product) {
      uni.showLoading({
        title: '添加中...'
      });
      
      try {
        // 调用添加到购物车接口
        const cartData = {
          productId: product.id,
          productSkuId: product.skuId || 0,
          quantity: 1
        };
        
        const result = await addCartItem(cartData);
        
        if (result && result.code === 200) {
          // 重新获取购物车数据
          this.loadCartData();
          
          uni.showToast({
            title: '已添加到购物车'
          });
        } else {
          throw new Error('添加购物车失败');
        }
      } catch (error) {
        console.error('添加购物车失败', error);
        uni.showToast({
          title: '添加失败',
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
      }
    },
    
    // 创建订单
    createOrder() {
      try {
        if (this.totalCount === 0) {
          return uni.showToast({
            title: '请选择要购买的商品',
            icon: 'none'
          });
        }
        
        // 筛选选中的商品
        const selectedItems = this.cartList.filter(item => item.checked);
        const cartIds = selectedItems.map(item => item.id);
        
        // 跳转到创建订单页面
        uni.navigateTo({
          url: `/pages/order/createOrder?cartIds=${JSON.stringify(cartIds)}&deliveryType=0&isGift=0`
        });
      } catch (error) {
        console.error('创建订单出错:', error);
        uni.showToast({
          title: '创建订单失败，请重试',
          icon: 'none'
        });
      }
    },
    
    // 检查图片有效性
    checkImagesValidity() {
      // 使用uni小程序API检查图片
      this.cartList.forEach((item, index) => {
        if (item.productPic) {
          uni.getImageInfo({
            src: item.productPic,
            success: () => {
              this.onImageLoad('cartList', index);
            },
            fail: () => {
              this.onImageError('cartList', index);
            }
          });
        }
      });
    },
    
    // 解析商品规格属性
    parseProductAttr(productAttr) {
      if (!productAttr) return '';
      
      try {
        const attrs = JSON.parse(productAttr);
        if (Array.isArray(attrs) && attrs.length > 0) {
          return attrs.map(attr => `${attr.key}：${attr.value}`).join('，');
        }
        return '';
      } catch (error) {
        console.error('解析商品属性失败:', error);
        return '';
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.container {
  background-color: #F8F8F8;
  min-height: 100vh;
  padding-bottom: 160rpx; // 为底部操作栏留出空间
}

/* 管理按钮区域 */
.manage-section {
  background-color: #FFFFFF;
  padding: 20rpx 30rpx;
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20rpx;
  
  .manage-btn-wrapper {
    .nav-manage {
      font-size: 28rpx;
      color: #0A0D05;
      padding: 10rpx 20rpx;
      background-color: #F5F5F5;
      border-radius: 20rpx;
    }
  }
}



/* 空白页 */
.empty {
  position: fixed;
  left: 0;
  top: 0;
  width: 100%;
  height: 100vh;
  padding-bottom: 100rpx;
  display: flex;
  justify-content: center;
  flex-direction: column;
  align-items: center;
  background: #fff;
  
  image {
    width: 344rpx;
    height: 266rpx;
  }
  
  .empty-tips {
    display: flex;
    flex-direction: column;
    align-items: center;
    font-size: 32rpx;
    color: #000;
    text-align: center;
    margin-bottom: 30rpx;
    font-weight: 500;
    
    .tip-line {
      line-height: 1.5;
      margin-bottom: 6rpx;
    }
  }
  
  .empty-recommended {
    position: absolute;
    left: 0;
    bottom: 0;
    max-height: 570rpx;
    width: 100%;
    padding: 0 30rpx 20rpx;
    box-sizing: border-box;
  }
}

/* 购物车内容区域 */
.cart-content {
  padding-top: 0;
}

/* 购物车列表 */
.cart-list {
  padding-bottom: 300rpx; // 为推荐商品区域留出空间
}

.cart-item-wrapper {
  position: relative;
  overflow: hidden;
  margin-bottom: 20rpx;
}

.cart-item {
  display: flex;
  align-items: center;
  background-color: #FFFFFF;
  padding: 30rpx;
  position: relative;
  min-height: 220rpx;
  transition: transform 0.3s ease;
  transform: translateX(0);
  
  &.manage-mode {
    transform: translateX(-144rpx); // 向左移动，露出删除按钮
  }
  
  .checkbox-wrapper {
    margin-right: 24rpx;
    display: flex;
    align-items: center;
    height: 100%;
    
    .checkbox-image {
      width: 40rpx;
      height: 40rpx;
    }
  }
  
  .product-image-wrapper {
    margin-right: 24rpx;
    
    .product-image {
      width: 160rpx;
      height: 160rpx;
      border-radius: 16rpx;
      background-color: #f8f8f8;
    }
  }
  
  .product-info {
    flex: 1;
    
    .product-name {
      font-size: 28rpx;
      color: #0A0D05;
      line-height: 1.4;
      margin-bottom: 12rpx;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
    
    .product-attr {
      font-size: 24rpx;
      color: #9FA19D;
      line-height: 1.4;
      margin-bottom: 20rpx;
    }
    
    .price-quantity-wrapper {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .product-price {
        font-size: 28rpx;
        color: #0A0D05;
        font-weight: 400;
      }
      
      // 数量选择器
      .quantity-selector {
        display: flex;
        align-items: center;
        
        .quantity-btn {
          width: 48rpx;
          height: 48rpx;
          background-color: #F5F5F5;
          border-radius: 8rpx;
          display: flex;
          align-items: center;
          justify-content: center;
          
          .quantity-icon {
            width: 24rpx;
            height: 24rpx;
          }
        }
        
        .quantity-display {
          width: 48rpx;
          height: 48rpx;
          background-color: #F5F5F5;
          border-radius: 8rpx;
          display: flex;
          align-items: center;
          justify-content: center;
          margin: 0 8rpx;
          
          .quantity-text {
            font-size: 24rpx;
            color: #0A0D05;
          }
        }
      }
      
      // 管理模式下的数量显示
      .quantity-display-manage {
        background-color: #F5F5F5;
        padding: 8rpx 16rpx;
        border-radius: 8rpx;
        
        .quantity-text-manage {
          font-size: 24rpx;
          color: #666666;
        }
      }
    }
  }
  
}

// 删除按钮（在wrapper层级）
.cart-item-wrapper .delete-btn {
  position: absolute;
  right: 0;
  top: 0;
  bottom: 0;
  width: 144rpx;
  background-color: #C72222;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 1;
  
  .delete-icon {
    width: 32rpx;
    height: 32rpx;
    margin-bottom: 8rpx;
  }
  
  .delete-text {
    font-size: 28rpx;
    color: #FFFFFF;
  }
}

/* 推荐商品区域 */
.cart-recommended {
  position: fixed;
  left: 0;
  bottom: 160rpx; // 底部操作栏高度
  width: 100%;
  max-height: 300rpx;
  background-color: #FFFFFF;
  padding: 20rpx 30rpx;
  box-sizing: border-box;
  z-index: 90;
}

/* 底部操作栏 */
.action-section {
  position: fixed;
  left: 0;
  bottom: 0;
  width: 100%;
  height: 160rpx;
  background-color: #FFFFFF;
  border-top: 1px solid #EEEEEE;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 30rpx;
  box-sizing: border-box;
  z-index: 95;
  
  .select-all-wrapper {
    display: flex;
    align-items: center;
    
    .select-all-checkbox {
      width: 40rpx;
      height: 40rpx;
      margin-right: 16rpx;
    }
    
    .select-all-text {
      font-size: 24rpx;
      color: #9FA19D;
    }
  }
  
  .total-checkout-wrapper {
    display: flex;
    align-items: center;
    
    .total-text {
      font-size: 32rpx;
      color: #000000;
      margin-right: 20rpx;
    }
    
    .checkout-btn {
      background-color: #20201E;
      border-radius: 16rpx;
      padding: 20rpx 28rpx;
      
      .checkout-text {
        font-size: 32rpx;
        color: #A9FF00;
      }
    }
  }
}
</style> 