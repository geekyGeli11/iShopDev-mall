<template>
  <view>
    <!-- 顶部背景图区域 -->
    <view class="top-background">
      <!-- 导航栏 -->
      <nav-bar
        :placeholder="true"
        bgColor="transparent"
      />

      <view class="content-header">
        <text class="tit">{{ payText }}</text>

        <!-- 订单商品信息卡片 -->
        <view class="order-card" v-if="orderDetail && orderDetail.orderItemList && orderDetail.orderItemList.length > 0">
        <!-- 礼品订单的赠言展示 -->
        <view v-if="isGiftOrder && giftMessage" class="gift-message-header">
          <text class="gift-message-label">赠言：</text>
          <text class="gift-message-text">{{ giftMessage }}</text>
        </view>
        
        <view class="card-content">
          <!-- 左：商品图 -->
          <view class="product-image-container">
            <image 
              class="product-image" 
              :src="orderDetail.orderItemList[0].productPic || 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/product/default-product.png'"
              mode="aspectFill"
            ></image>
            <!-- 多商品数量标识 -->
            <view class="quantity-badge" v-if="orderDetail.orderItemList.length > 1">
              +{{ orderDetail.orderItemList.length - 1 }}
            </view>
          </view>
          
          <!-- 中：商品信息和地址 -->
          <view class="product-info">
            <text class="product-name">
              {{ orderDetail.orderItemList[0].productName }}
              <text v-if="orderDetail.orderItemList.length > 1" class="extra-count">
                等{{ orderDetail.orderItemList.length }}件商品
              </text>
            </text>
            
            <!-- 礼品订单显示"订单"，普通订单显示地址信息 -->
            <text class="address-text" v-if="isGiftOrder">
              送礼订单
            </text>
            <text class="address-text" v-else-if="orderDetail.deliveryType !== 1">
              {{ orderDetail.receiverName }} {{ orderDetail.receiverPhone }}
            </text>
            <text class="address-text" v-else-if="orderDetail.deliveryType === 1">
              {{ orderDetail.storeName || '门店自取' }}
            </text>
          </view>
          
          <!-- 右：查看订单按钮 -->
          <view class="view-order-btn" @tap="viewOrderDetail">
            查看订单
          </view>
        </view>
      </view>
      </view>
    </view>

    <view class="content" :class="{ 'gift-order': isGiftOrder }">
      <!-- 取货码信息 -->
      <view class="pickup-info" v-if="isPickupOrder">
        <view class="pickup-title">取货信息</view>
        <view class="pickup-code-section">
          <view class="pickup-code-label">取货码</view>
          <view class="pickup-code">{{ pickupCode }}</view>
        </view>
        <view class="pickup-qr-section" v-if="qrCodeBase64">
          <view class="pickup-qr-label">取货二维码</view>
          <image class="pickup-qr-image" :src="qrCodeBase64" mode="aspectFit" />
          <view class="pickup-qr-tip">请向商家出示此二维码完成取货</view>
        </view>
      </view>
      
      <!-- 礼品订单的后续步骤 -->
      <view v-if="isGiftOrder" class="gift-steps-section">
        <view class="gift-steps-content">
          <view class="gift-steps-title">后续步骤</view>
          <view class="step-item">
            <view class="step-number completed">1</view>
            <view class="step-content">
              <text class="step-title">支付成功</text>
            </view>
          </view>
          <view class="step-item">
            <view class="step-number active">2</view>
            <view class="step-content">
              <text class="step-title">分享给朋友</text>
            </view>
          </view>
          <view class="step-item">
            <view class="step-number">3</view>
            <view class="step-content">
              <text class="step-title">朋友填写收货地址</text>
            </view>
          </view>
          <view class="step-item">
            <view class="step-number">4</view>
            <view class="step-content">
              <text class="step-title">等待发货</text>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 推荐商品区域（普通订单） -->
      <view v-else-if="recommendedProductList.length > 0" class="recommended-section">
        <view class="recommended-content">
          <ProductWaterfall :list="recommendedProductList" @select="navToDetailPage"></ProductWaterfall>
        </view>
      </view>

      <!-- 礼品订单分享按钮 -->
      <view v-if="isGiftOrder" class="gift-share-bottom">
        <button class="share-btn" open-type="share" @click="prepareShare">
          去分享给朋友
        </button>
      </view>
    </view>
  </view>
</template>

<script>
import { payOrderSuccess, fetchOrderDetail } from '@/api/order.js';
import { fetchProductListBySales } from '@/api/home.js';
import navBar from '@/components/nav-bar.vue';
import ProductWaterfall from '@/components/home/ProductWaterfall.vue';

export default {
  components: {
    'nav-bar': navBar,
    ProductWaterfall
  },
  data() {
    return {
      payText: '支付成功',
      orderSn: '',
      orderId: '',
      orderAmount: '',
      payTime: '',
      payType: 2, // 支付方式，默认微信支付
      isProcessed: false, // 防止重复处理
      isPickupOrder: false, // 是否为取货订单
      pickupCode: '', // 取货码
      qrCodeBase64: '', // 取货二维码base64图片
      orderDetail: null, // 订单详情
      recommendedProductList: [], // 推荐商品列表
      isGiftOrder: false, // 是否为礼品订单
      giftMessage: '', // 礼品留言
      isDIYOrder: false // 是否为DIY订单
    };
  },
  onLoad(options) {
    // 获取支付金额和订单号
    if (options.orderSn && options.orderId) {
      this.orderSn = options.orderSn;
      this.orderId = options.orderId;
      
      if (options.amount) {
        this.orderAmount = options.amount;
      }

      // 获取支付方式
      if (options.payType) {
        this.payType = parseInt(options.payType);
      }

      // 检查是否为礼品订单
      if (options.isGift === '1' || options.isGift === 1) {
        this.isGiftOrder = true;
        this.payText = '支付成功';
      }

      // 检查是否为DIY订单
      if (options.isDIY === '1' || options.isDIY === 1) {
        this.isDIYOrder = true;
      }
      
      // 获取礼品留言
      if (options.giftMessage) {
        try {
          // 处理可能的双重编码
          let decodedMessage = decodeURIComponent(options.giftMessage);
          
          // 检查是否还需要再次解码（如果包含%字符，可能是双重编码）
          if (decodedMessage.includes('%')) {
            try {
              decodedMessage = decodeURIComponent(decodedMessage);
            } catch (e) {
              console.warn('第二次解码失败，使用第一次解码结果:', e);
            }
          }
          
          this.giftMessage = decodedMessage;
          console.log('最终解码后的礼物留言:', this.giftMessage);
        } catch (e) {
          console.error('解析礼物留言失败:', e);
          this.giftMessage = options.giftMessage; // 解码失败时使用原值
        }
      }
      
      // 通知后端更新订单状态并获取订单详情
      this.notifyPaymentSuccess();
    } else {
      uni.showToast({
        title: '订单参数缺失',
        icon: 'none'
      });
      
      // 3秒后跳转到订单列表
      setTimeout(() => {
        uni.redirectTo({
          url: '/pages/order/order?state=0'
        });
      }, 3000);
    }
    
    // 只有非礼品订单才加载推荐商品
    if (!this.isGiftOrder) {
      this.loadRecommendedProducts();
    }
  },
  methods: {
    // 通知后端支付成功
    notifyPaymentSuccess() {
      // 防止重复处理
      if (this.isProcessed) return;
      this.isProcessed = true;
      
      uni.showLoading({
        title: '处理中...'
      });
      
      // 统一先获取订单详情，再调用支付成功接口
      this.getOrderDetail();
    },
    
    // 获取订单详情
    getOrderDetail() {
      fetchOrderDetail(this.orderId).then(res => {
        if (res.code === 200 && res.data) {
          this.orderDetail = res.data;
          console.log('订单详情:', this.orderDetail);
          
          // 获取到订单详情后，调用支付成功接口
          this.callPayOrderSuccess();
        } else {
          console.error('获取订单详情失败', res);
          uni.hideLoading();
          // 即使获取详情失败，也尝试调用支付成功接口
          this.callPayOrderSuccess();
        }
      }).catch(err => {
        console.error('获取订单详情失败', err);
        // 即使获取详情失败，也尝试调用支付成功接口
        this.callPayOrderSuccess();
      });
    },
    
    // 调用支付成功接口
    callPayOrderSuccess() {
      // 构建支付成功参数
      const paySuccessData = {
        orderId: this.orderId,
        payType: this.payType // 使用实际的支付方式
      };
      
      // 如果是礼品订单，添加额外参数
      if (this.isGiftOrder) {
        if (this.giftMessage) {
          paySuccessData.giftMessage = this.giftMessage;
        }
        // 从订单详情中获取商品图片作为礼品图片
        if (this.orderDetail && this.orderDetail.orderItemList && this.orderDetail.orderItemList.length > 0) {
          paySuccessData.giftPic = this.orderDetail.orderItemList[0].productPic;
        }
      }
      
      // 调用支付成功接口
      payOrderSuccess(paySuccessData).then(res => {
        uni.hideLoading();
        
        if (res.code === 200) {
          // 设置支付时间
          this.payTime = this.formatDate(new Date());
          
          // 如果接口返回了更详细的订单信息，可以更新页面数据
          if (res.data) {
            if (res.data.payAmount && !this.orderAmount) {
              this.orderAmount = res.data.payAmount.toFixed(2);
            }
            
            // 如果有支付时间，使用服务器返回的
            if (res.data.paymentTime) {
              this.payTime = this.formatDate(new Date(res.data.paymentTime));
            }
            
            // 处理取货相关信息
            if (res.data.isPickupOrder) {
              this.isPickupOrder = true;
              this.pickupCode = res.data.pickupCode || '';
              this.qrCodeBase64 = res.data.qrCodeBase64 || '';
            }
          }
        } else {
          console.error('支付状态更新失败', res);
        }
      }).catch(err => {
        console.error('支付状态更新请求失败', err);
        uni.hideLoading();
      });
    },
    
    // 加载推荐商品
    loadRecommendedProducts() {
      const params = {
        pageNum: 1,
        pageSize: 10
      };

      // 添加学校ID参数
      const schoolInfo = uni.getStorageSync('selectedSchool');
      if (schoolInfo) {
        try {
          const school = typeof schoolInfo === 'string' ? JSON.parse(schoolInfo) : schoolInfo;
          if (school && school.id) {
            params.schoolId = school.id;
          }
        } catch (error) {
          console.error('解析学校信息失败:', error);
        }
      }

      fetchProductListBySales(params).then(res => {
        if (res.code === 200 && res.data) {
          // 转换数据格式以适配 ProductWaterfall 组件
          this.recommendedProductList = res.data.map(item => ({
            ...item,
            productName: item.name || item.productName,
            pic: item.pic || item.cover,
            cover: item.pic || item.cover
          }));
          console.log('加载推荐商品成功:', this.recommendedProductList.length, '个');
        } else {
          console.error('获取推荐商品失败', res);
        }
      }).catch(err => {
        console.error('获取推荐商品失败', err);
        // 设置一些默认商品，避免空白
        this.recommendedProductList = [];
      });
    },
    
    // 跳转到商品详情页
    navToDetailPage(item) {
      uni.navigateTo({
        url: `/pages/product/product?id=${item.id}`
      });
    },
    
    // 查看订单详情
    viewOrderDetail() {
      uni.navigateTo({
        url: `/pages/order/orderDetail?id=${this.orderId}`
      });
    },
    
    // 格式化日期
    formatDate(date) {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hours = String(date.getHours()).padStart(2, '0');
      const minutes = String(date.getMinutes()).padStart(2, '0');
      const seconds = String(date.getSeconds()).padStart(2, '0');
      
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    },
    
    // 准备分享（使用open-type="share"时会自动调用onShareAppMessage）
    prepareShare() {
      console.log('准备分享礼品订单');
      // 使用open-type="share"时，无需手动处理分享逻辑
      // 微信小程序会自动调用onShareAppMessage方法
    }
  },
  
  // 小程序分享配置
  onShareAppMessage() {
    if (this.isGiftOrder && this.orderDetail) {
      try {
        // 获取用户信息用于分享
        const userInfo = uni.getStorageSync('userInfo') || {};
        let userName = '';
        let userIcon = '';
        let userId = '';
        
        if (userInfo.nickname) {
          userName = userInfo.nickname;
        } else if (userInfo.phone) {
          userName = `用户${userInfo.phone}`;
        } else {
          userName = '朋友';
        }
        
        if (userInfo.icon) {
          userIcon = userInfo.icon;
        }
        
        if (userInfo.id) {
          userId = userInfo.id;
        }
        
        // 构建分享路径，指向gift-confirm页面
        const sharePath = `/pages/gift-bag/gift-confirm?from=${userId}&orderId=${this.orderId}&senderName=${encodeURIComponent(userName)}&senderIcon=${encodeURIComponent(userIcon)}`;
        
        // 获取商品图片用于分享
        const productImage = this.orderDetail.orderItemList && this.orderDetail.orderItemList.length > 0 
          ? this.orderDetail.orderItemList[0].productPic 
          : '';
        
        return {
          title: '这是我为你精心准备的礼物，快拆开看看吧~',
          path: sharePath,
          imageUrl: productImage
        };
      } catch (error) {
        console.error('礼品分享配置出错:', error);
        return {
          title: '这是我为你精心准备的礼物，快拆开看看吧~',
          path: `/pages/gift-bag/gift-confirm?orderId=${this.orderId}`
        };
      }
    } else {
      // 普通订单的分享
      return {
        title: '支付成功',
        path: '/pages/new_index/index'
      };
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
  background-color: #F3F7F3;
}

/* 顶部背景图区域 */
.top-background {
  background-image: url('https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/order/paysuccess_background.png');
  background-size: cover;
  background-position: center top;
  background-repeat: no-repeat;
  height: 520rpx; /* 260px = 520rpx */
  position: relative;
}

.content-header {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 0 30rpx;
  margin: 0 30rpx;
  position: relative;
  background-color: rgba(255, 255, 255, 0.8);
  border-radius: 16rpx;
}

.content {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 0 30rpx;
  background-color: #F3F7F3;
  position: relative;

  &.gift-order {
    padding-bottom: calc(140rpx + constant(safe-area-inset-bottom));
    padding-bottom: calc(140rpx + env(safe-area-inset-bottom));
  }
}

.tit {
  font-size: 56rpx;
  font-weight: 600;
  color: #000000;
  margin-bottom: 40rpx;
}

.order-card {
  width: 100%;
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 40rpx;
}

.card-content {
  display: flex;
  align-items: center;
}

.product-image-container {
  position: relative;
  margin-right: 20rpx;
  flex-shrink: 0;
}

.product-image {
  width: 100rpx;
  height: 100rpx;
  border-radius: 12rpx;
  background-color: #f5f5f5;
}

.quantity-badge {
  position: absolute;
  top: -6rpx;
  right: -6rpx;
  background: #ff4444;
  color: #fff;
  font-size: 20rpx;
  padding: 2rpx 8rpx;
  border-radius: 20rpx;
  min-width: 30rpx;
  text-align: center;
  line-height: 1.2;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-right: 20rpx;
}

.product-name {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
  margin-bottom: 8rpx;
  line-height: 1.4;
  display: block;

  /* 允许多行显示，最多显示2行 */
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  word-break: break-all;
  max-height: 80rpx; /* 约2行的高度 */
}

.extra-count {
  color: #999;
  font-weight: 400;
  font-size: 24rpx;
}

.address-text {
  font-size: 24rpx;
  color: #999;
  line-height: 1.4;
  
  /* 限制一行显示 */
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.view-order-btn {
  flex-shrink: 0;
  padding: 12rpx 24rpx;
  background: #E5FDC0;
  border-radius: 20rpx;
  font-size: 28rpx;
  color: #000000;
}

.pickup-info {
  margin-top: 60rpx;
  padding: 40rpx;
  background: #f8f9fa;
  border-radius: 16rpx;
  width: 600rpx;
}

.pickup-title {
  font-size: 32rpx;
  color: #303133;
  font-weight: bold;
  text-align: center;
  margin-bottom: 30rpx;
}

.pickup-code-section {
  text-align: center;
  margin-bottom: 40rpx;
}

.pickup-code-label {
  font-size: 28rpx;
  color: #666;
  margin-bottom: 15rpx;
}

.pickup-code {
  font-size: 48rpx;
  color: #0088FF;
  font-weight: bold;
  letter-spacing: 8rpx;
  padding: 20rpx 30rpx;
  background: #fff;
  border-radius: 12rpx;
  border: 2rpx solid #0088FF;
  display: inline-block;
}

.pickup-qr-section {
  text-align: center;
}

.pickup-qr-label {
  font-size: 28rpx;
  color: #666;
  margin-bottom: 20rpx;
}

.pickup-qr-image {
  width: 300rpx;
  height: 300rpx;
  background: #fff;
  border-radius: 12rpx;
  border: 2rpx solid #e5e5e5;
  margin-bottom: 20rpx;
}

.pickup-qr-tip {
  font-size: 24rpx;
  color: #999;
  line-height: 1.4;
}

.recommended-section {
  position: relative;
  width: 100vw;
  margin-top: 40rpx;
  background: #fff;
  border-radius: 40rpx 40rpx 0 0;
}

.recommended-content {
  padding: 30rpx;
}

.recommended-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
  text-align: center;
  margin-bottom: 30rpx;
}

/* 礼品订单样式 */
.gift-message-header {
  display: flex;
  flex-direction: row;
  align-items: center;
  padding: 16rpx 32rpx;
  gap: 16rpx;
  margin-bottom: 20rpx;
  border-bottom: 1rpx solid #EEEEEE;
}

.gift-message-label {
  font-size: 28rpx;
  color: #0A0D05;
  font-weight: 500;
  line-height: 40rpx;
  flex: none;
}

.gift-message-text {
  font-size: 28rpx;
  color: #0A0D05;
  font-weight: 500;
  line-height: 40rpx;
  flex: 1;
}

.gift-steps-section {
  position: relative;
  width: calc(100vw - 60rpx);
  margin: 40rpx 30rpx 0;
  background: #fff;
  border-radius: 40rpx 40rpx 0 0;
  padding: 0 30rpx;
}

.gift-steps-content {
  padding: 30rpx 0;
}

.gift-steps-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
  text-align: left;
  margin-bottom: 40rpx;
}

.step-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 50rpx;
  position: relative;
  min-height: 60rpx;
  
  &:not(:last-child)::after {
    content: '';
    position: absolute;
    left: 23rpx;
    top: 46rpx;
    width: 2rpx;
    height: 60rpx;
    background-color: #e5e5e5;
  }
}

.step-number {
  width: 46rpx;
  height: 46rpx;
  border-radius: 50%;
  background-color: #fff;
  color: #333;
  border: 2rpx solid #e5e5e5;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  font-weight: 500;
  margin-right: 20rpx;
  flex-shrink: 0;
  
  &.completed {
    background-color: #52c41a;
    color: #fff;
    border-color: #52c41a;
  }
  
  &.active {
    background-color: #A9FF00;
    color: #20201E;
    border-color: #A9FF00;
  }
}

.step-content {
  flex: 1;
  display: flex;
  align-items: center;
  height: 46rpx;
}

.step-title {
  font-size: 32rpx;
  color: #333;
  font-weight: 500;
  line-height: 1;
}

/* 礼品分享底部区域 */
.gift-share-bottom {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  background: #fff;
  padding: 20rpx 30rpx;
  padding-bottom: calc(20rpx + constant(safe-area-inset-bottom));
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  border-top: 1rpx solid #f5f5f5;
  z-index: 1000;
}

.share-btn {
  width: 100%;
  height: 84rpx;
  background: #20201E;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  color: #A9FF00;
  border: none;
  outline: none;
  
  &::after {
    border: none;
  }
}
</style> 