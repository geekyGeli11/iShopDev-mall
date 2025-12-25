<template>
  <view class="gift-confirm-container">
    <!-- 卡片背景 -->
    <view class="gift-card-bg">
      <image v-if="giftData.imageUrl" class="bg-image" :src="giftData.imageUrl" mode="aspectFill"></image>
    </view>

    <!-- 主内容区域 -->
    <view class="content">
      <!-- 用户信息 -->
      <view class="user-info">
        <view class="user-profile">
          <image class="user-avatar" :src="displayUserInfo.icon || 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/images/default-avatar.png'" mode="aspectFill"></image>
          <text class="user-name">{{displayUserInfo.nickname || '用户'}}</text>
        </view>
        <text class="gift-message">
          <template v-if="isSender">
            <template v-if="giftDetail && giftDetail.isReceived">{{receiverName}}领取了您的礼包</template>
            <template v-else>您的礼盒已为您打包好，快去赠送吧</template>
          </template>
          <template v-else>
            <template v-if="giftDetail && giftDetail.isReceived">恭喜！您收到了{{orderItemList.length}}件礼品</template>
            <template v-else>{{senderInfo.nickname}}给您送来一份礼物，快打开看看吧</template>
          </template>
        </text>
      </view>

      <!-- 礼物卡片 -->
      <view class="gift-card-wrapper" v-if="!isRecipientReceived">
        <view class="gift-card">
          <view class="gift-card-image">
            <image class="card-image" :src="giftData.imageUrl || 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/images/gift-bag/default-card.png'" mode="aspectFill"></image>
          </view>
          <view class="gift-card-content">
            <view class="gift-wish-container">
              <text class="gift-wish">{{giftData.wishText || '身体棒棒，钱包胖胖'}}</text>
            </view>
            <text class="gift-from">From {{senderInfo.nickname || '好友'}}</text>
          </view>
        </view>
      </view>

      <!-- 商品列表 - 收礼人已领取时显示 -->
      <view class="product-list-container" v-if="isRecipientReceived">
        <view class="product-count-info">
          <text class="product-count">已选择 {{orderItemList.length}} 件礼品</text>
        </view>
        <view class="product-list">
          <view class="product-item" v-for="(item, index) in orderItemList" :key="index">
            <view class="product-image">
              <image :src="item.productPic" mode="aspectFill"></image>
            </view>
            <view class="product-info">
              <text class="product-name">{{item.productName}}</text>
              <text class="product-attrs">{{formatProductAttr(item.productAttr)}}</text>
              <view class="product-price-quantity">
                <text class="product-price">¥{{item.productPrice.toFixed(2)}}</text>
                <text class="quantity-num">×{{item.productQuantity}}</text>
              </view>
            </view>
          </view>
        </view>

        <!-- 收货地址信息 -->
        <view class="address-info" v-if="isAddressInfoComplete">
          <view class="address-title">
            <text class="title-text">收货信息</text>
          </view>
          <view class="address-content">
            <view class="address-row">
              <text class="address-label">收货人：</text>
              <text class="address-value">{{giftDetail.receiverName || (addressInfo ? addressInfo.name : '')}}</text>
            </view>
            <view class="address-row">
              <text class="address-label">联系电话：</text>
              <text class="address-value">{{giftDetail.receiverPhone || (addressInfo ? addressInfo.phoneNumber : '')}}</text>
            </view>
            <view class="address-row">
              <text class="address-label">收货地址：</text>
              <text class="address-value">{{fullAddress}}</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 底部操作区 -->
    <view class="bottom-container">
      <!-- 提示信息 -->
      <view class="notice-container" v-if="showNotice">
        <text class="notice-text">{{noticeText}}</text>
      </view>

      <!-- 底部按钮 -->
      <view class="bottom-action-bar">
        <!-- 送礼人查看未领取礼物 - 显示分享按钮 -->
        <button v-if="giftDetail && isSender && !giftDetail.isReceived && !giftDetail.isExpired" 
                class="action-btn primary-btn" open-type="share">
          去赠送给好友
        </button>

        <!-- 送礼人查看过期未领取礼物 - 可以自己领取 -->
        <button v-else-if="giftDetail && isSender && giftDetail.isExpired && !giftDetail.isReceived" 
                class="action-btn primary-btn" @click="handleReceiveGift">
          收下礼品
        </button>

        <!-- 收礼人未领取且未过期 - 可以领取 -->
        <button v-else-if="giftDetail && !isSender && !giftDetail.isReceived && !giftDetail.isExpired" 
                class="action-btn primary-btn" @click="handleReceiveGift">
          收下礼物
          </button>

        <!-- 收礼人已领取但地址未完善 -->
        <button v-else-if="giftDetail && isRecipient && giftDetail.isReceived && !isAddressInfoComplete" 
                class="action-btn primary-btn" @click="goToAddressPage">
          填写收礼地址
        </button>

        <!-- 其他情况显示状态按钮 -->
        <button v-else class="action-btn disabled-btn" disabled>
          {{getStatusText()}}
        </button>
      </view>
    </view>
  </view>
</template>

<script>
import { getGiftDetail, receiveGift, updateGiftAddress } from '@/api/gift';
import { fetchOrderDetail } from '@/api/order';

export default {
  data() {
    return {
      userInfo: {
        nickname: '',
        icon: '',
        id: 0
      },
      senderInfo: {
        nickname: '',
        icon: '',
        id: 0
      },
      giftData: {
        imageUrl: '',
        wishText: ''
      },
      orderId: null,
      orderDetail: null,
      giftDetail: null,
      orderItemList: [],
      isRecipient: false, // 是否为收礼人
      isSender: false, // 是否为送礼人
      isUserLoggedIn: false,
      addressInfo: null,
      receiverName: '', // 实际收礼人姓名
      isIdentityFromUrl: false // 身份是否来自URL参数
    };
  },
  computed: {
    // 显示的用户信息
    displayUserInfo() {
      if (this.isRecipientReceived) {
        return this.userInfo;
      }
      return this.senderInfo;
    },

    // 收礼人是否已领取
    isRecipientReceived() {
      return this.isRecipient && this.giftDetail && this.giftDetail.isReceived;
    },

    // 地址信息是否完整
    isAddressInfoComplete() {
      const hasGiftDetailAddress = this.giftDetail &&
        this.giftDetail.receiverName &&
        this.giftDetail.receiverPhone &&
        this.giftDetail.receiverProvince &&
        this.giftDetail.receiverDetailAddress;

      const hasAddressInfo = this.addressInfo &&
        this.addressInfo.name &&
        this.addressInfo.phoneNumber &&
        this.addressInfo.province &&
        this.addressInfo.detailAddress;

      return hasGiftDetailAddress || hasAddressInfo;
    },

    // 完整地址
    fullAddress() {
      if (this.giftDetail && this.giftDetail.receiverProvince) {
        return (this.giftDetail.receiverProvince || '') +
               (this.giftDetail.receiverCity || '') +
               (this.giftDetail.receiverRegion || '') +
               (this.giftDetail.receiverDetailAddress || '');
      }
      if (this.addressInfo) {
        return (this.addressInfo.province || '') +
               (this.addressInfo.city || '') +
               (this.addressInfo.region || '') +
               (this.addressInfo.detailAddress || '');
      }
      return '';
    },

    // 是否显示提示
    showNotice() {
      if (!this.giftDetail) return false;
      
      if (this.isSender && !this.giftDetail.isReceived && !this.giftDetail.isExpired) {
        return true;
      }
      if (!this.isSender && (this.giftDetail.isExpired || this.giftDetail.isReceived)) {
        return true;
      }
      return false;
    },

    // 提示文本
    noticeText() {
      if (!this.giftDetail) return '';
      
      if (this.isSender && !this.giftDetail.isReceived && !this.giftDetail.isExpired) {
        return '赠送后可提醒好友接收，若超过 24 小时未领取将自动转入"我的礼物"，本人可使用';
      }
      if (!this.isSender && this.giftDetail.isExpired) {
        return '很抱歉，该礼物已过期';
      }
      if (!this.isSender && this.giftDetail.isReceived) {
        return '很抱歉，该礼物已被领取';
      }
      return '';
    }
  },

    onLoad(options) {
    // 设置导航栏标题
    uni.setNavigationBarTitle({
      title: '礼物详情'
    });
    
    // 获取订单ID
    if (options.orderId) {
      this.orderId = options.orderId;
    }
    
    // 判断用户身份
    if (options.from) {
      // 从分享或收礼列表进入，当前用户是收礼人
        this.isRecipient = true;
        this.isSender = false;
      this.isIdentityFromUrl = true;
          this.senderInfo.id = options.from;
      
        if (options.senderName) {
          this.senderInfo.nickname = decodeURIComponent(options.senderName);
        }
        if (options.senderIcon) {
          this.senderInfo.icon = decodeURIComponent(options.senderIcon);
        }
    } else {
      // 从送礼列表进入，当前用户是送礼人
      this.isSender = true;
      this.isRecipient = false;
      this.isIdentityFromUrl = true;
    }
    
    // 获取用户信息
    this.getUserInfo();
    
    // 加载数据
    if (this.orderId) {
      this.loadData();
    } else {
      this.loadGiftDataFromStorage();
    }
  },

  // 分享配置
  onShareAppMessage() {
    const userId = this.userInfo && this.userInfo.id ? this.userInfo.id : '';
    const userName = this.userInfo && this.userInfo.nickname ? this.userInfo.nickname : '';
    const userIcon = this.userInfo && this.userInfo.icon ? encodeURIComponent(this.userInfo.icon) : '';

    return {
      title: '这是我为你精心准备的礼物，快拆开看看吧~',
      path: '/pages/gift-bag/gift-confirm?from=' + userId +
        (this.orderId ? '&orderId=' + this.orderId : '') +
        '&senderName=' + userName +
        '&senderIcon=' + userIcon,
      imageUrl: this.giftData.imageUrl
    };
  },

  onShow() {
    // 从地址页面返回时更新地址
    const pages = getCurrentPages();
    const currentPage = pages[pages.length - 1];

    if (currentPage.$vm.addressInfo && currentPage.$vm.addressInfo.id && this.giftDetail) {
      this.updateGiftShippingAddress(currentPage.$vm.addressInfo);
    }
  },

    methods: {
    // 获取用户信息
    getUserInfo() {
      try {
        const userInfoStr = uni.getStorageSync('userInfo');
        if (userInfoStr) {
          let parsedUserInfo;
          if (typeof userInfoStr === 'object') {
            parsedUserInfo = userInfoStr;
          } else {
            parsedUserInfo = JSON.parse(userInfoStr);
          }
          this.userInfo = parsedUserInfo;
          this.isUserLoggedIn = true;

          // 如果当前用户是送礼人，设置送礼人信息
          if (this.isSender) {
            this.senderInfo = { ...this.userInfo };
          }
        } else {
          this.isUserLoggedIn = false;
        }
      } catch (error) {
        console.error('获取用户信息失败:', error);
        this.isUserLoggedIn = false;
      }
    },

    // 从本地存储加载礼物数据
    loadGiftDataFromStorage() {
      try {
        const wishDataStr = uni.getStorageSync('giftWishData');
        if (wishDataStr) {
          const wishData = JSON.parse(wishDataStr);
          this.giftData.imageUrl = wishData.imageUrl;
          this.giftData.wishText = wishData.text || '身体棒棒，钱包胖胖';
        }

        const orderDataStr = uni.getStorageSync('giftOrderData');
        if (orderDataStr) {
          const orderData = JSON.parse(orderDataStr);
          this.orderId = orderData.orderId;
          if (this.orderId) {
            this.loadData();
          }
        }
      } catch (error) {
        console.error('加载礼物数据失败:', error);
      }
    },

    // 加载数据 - 调用订单详情和礼物详情接口
    async loadData() {
      if (!this.orderId) return;

      uni.showLoading({ title: '加载中...' });

      try {
        // 并行调用两个接口
        const [orderRes, giftRes] = await Promise.all([
          fetchOrderDetail(this.orderId),
          getGiftDetail(this.orderId)
        ]);

        // 处理订单详情
        if (orderRes.code === 200) {
          this.orderDetail = orderRes.data;
          if (this.orderDetail.orderItemList) {
            this.orderItemList = this.orderDetail.orderItemList;
          }
          }

        // 处理礼物详情
        if (giftRes.code === 200) {
          this.giftDetail = giftRes.data;
          this.processGiftDetail();
        } else {
          uni.showToast({
            title: giftRes.message || '获取礼物信息失败',
            icon: 'none'
          });
        }

      } catch (error) {
        console.error('加载数据失败:', error);
        uni.showToast({
          title: '加载失败，请稍后再试',
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
      }
    },

    // 处理礼物详情数据
    processGiftDetail() {
      if (!this.giftDetail) return;

      // 身份判断：优先使用数据库数据进行准确判断
      if (this.isUserLoggedIn && this.userInfo.id && this.giftDetail.giftRecord) {
        const giftRecord = this.giftDetail.giftRecord;
        
        // 使用严格的类型转换进行比较
        const currentUserId = String(this.userInfo.id);
        const senderId = String(giftRecord.senderId);
        const receiverId = giftRecord.receiverId ? String(giftRecord.receiverId) : null;
        
        // 数据库判断优先：先检查是否为收礼人，再检查是否为送礼人
        if (receiverId && receiverId === currentUserId) {
          this.isRecipient = true;
          this.isSender = false;
        }
        else if (senderId && senderId === currentUserId) {
          this.isSender = true;
          this.isRecipient = false;
        }

        // 获取实际收礼人姓名
        if (this.giftDetail.receiverName) {
          this.receiverName = this.giftDetail.receiverName;
        }
          }

      // 更新礼物显示数据
          if (this.giftDetail.giftRecord) {
            const giftRecord = this.giftDetail.giftRecord;

            if (giftRecord.giftMessage) {
              this.giftData.wishText = giftRecord.giftMessage;
            }

            if (giftRecord.giftImage) {
              this.giftData.imageUrl = giftRecord.giftImage;
            } else if (giftRecord.productPic) {
              this.giftData.imageUrl = giftRecord.productPic;
            }

            // 如果是收礼人且没有赠送者信息，从礼物记录中获取
        if (this.isRecipient && giftRecord.senderId && !this.senderInfo.nickname) {
              this.senderInfo.id = giftRecord.senderId;
            }
          }

      // 保存商品列表
      if (this.giftDetail.orderItemList && this.giftDetail.orderItemList.length > 0) {
        this.orderItemList = this.giftDetail.orderItemList;
      }
    },

    // 格式化商品属性
    formatProductAttr(attrStr) {
      try {
        if (!attrStr) return '';
        const attrs = JSON.parse(attrStr);
        if (!Array.isArray(attrs)) return '';
        return attrs.map(attr => `${attr.key}: ${attr.value}`).join(', ');
      } catch (error) {
        console.error('解析商品属性失败:', error);
        return '';
          }
    },

    // 获取状态文本
    getStatusText() {
      if (!this.giftDetail) return '加载中...';
      
      if (this.giftDetail.isExpired) {
        return '礼物已过期';
        }
      if (this.giftDetail.isReceived) {
        return '礼物已被领取';
      }
      return '礼物状态异常';
    },

    // 处理领取礼物
    handleReceiveGift() {
      // 检查用户登录状态
      if (!this.isUserLoggedIn) {
        uni.showModal({
          title: '提示',
          content: '您尚未登录，请先登录再领取礼物',
          confirmText: '去登录',
          success: (res) => {
            if (res.confirm) {
              // 保存当前页面信息，用于登录成功后返回
              const currentPages = getCurrentPages();
              const currentPage = currentPages[currentPages.length - 1];
              const returnPageInfo = {
                url: currentPage.route,
                options: currentPage.options || {}
              };
              uni.setStorageSync('returnPageInfo', returnPageInfo);
              // 使用Vuex统一管理登录弹窗状态
              this.$store.commit('setLoginPopup', { show: true, reason: 'unauthorized' });
              uni.switchTab({ url: '/pages/new_index/index' });
            }
          }
        });
        return;
      }

      // 检查礼物状态
      if (!this.giftDetail || !this.giftDetail.giftRecord) {
        uni.showToast({
          title: '礼物信息不完整',
          icon: 'none'
        });
        return;
      }

      if (this.giftDetail.isReceived) {
        uni.showToast({
          title: '该礼物已被领取',
          icon: 'none'
        });
        return;
      }

      // 非送礼人且礼物已过期，不能领取
      if (this.giftDetail.isExpired && !this.isSender) {
        uni.showToast({
          title: '该礼物已过期',
          icon: 'none'
        });
        return;
      }

      // 调用领取接口
      this.receiveGift();
    },

    // 调用领取礼物接口
    receiveGift() {
      uni.showLoading({ title: '领取中...' });

      const giftRecordId = this.giftDetail.giftRecord.id;

      receiveGift(giftRecordId).then(res => {
        uni.hideLoading();

        if (res.code === 200) {
          uni.showToast({
            title: '礼物领取成功',
            icon: 'success'
          });

          // 重新加载数据
          this.loadData();
        } else {
          uni.showToast({
            title: res.message || '领取失败',
            icon: 'none'
          });
        }
      }).catch(err => {
        uni.hideLoading();
        console.error('领取礼物失败:', err);
        uni.showToast({
          title: '领取失败，请稍后再试',
          icon: 'none'
        });
      });
    },

    // 前往地址页面
    goToAddressPage() {
      uni.navigateTo({
        url: '/pages/address/addressManage?fromGift=1&giftRecordId=' + this.giftDetail.giftRecord.id
      });
    },

    // 更新礼物收货地址
    updateGiftShippingAddress(addressInfo) {
      if (!this.giftDetail || !this.giftDetail.giftRecord || !addressInfo) {
        return;
      }

      const giftRecordId = this.giftDetail.giftRecord.id;
      const memberReceiveAddressId = addressInfo.id;

      uni.showLoading({ title: '更新地址中...' });

      updateGiftAddress(giftRecordId, memberReceiveAddressId).then(res => {
        uni.hideLoading();

        if (res.code === 200) {
          this.addressInfo = addressInfo;
          uni.showToast({
            title: '地址更新成功',
            icon: 'success'
          });
          // 重新加载数据
          this.loadData();
        } else {
          uni.showToast({
            title: res.message || '地址更新失败',
            icon: 'none'
          });
        }
      }).catch(err => {
        uni.hideLoading();
        console.error('更新地址失败:', err);
        uni.showToast({
          title: '更新地址失败，请稍后再试',
          icon: 'none'
        });
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.gift-confirm-container {
  width: 100%;
  min-height: 100vh;
  position: relative;
  display: flex;
  flex-direction: column;
  background-color: #fff;
}

/* 卡片背景 */
.gift-card-bg {
  position: absolute;
  left: 0;
  width: 100%;
  height: 45vh;
  z-index: 1;
  overflow: hidden;

  .bg-image {
    width: 100%;
    height: 100%;
    opacity: 0.6;
    object-fit: cover;
    filter: blur(15px);
    transform: scale(1.2);
  }

  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(to bottom,
        rgba(255, 255, 255, 0.5) 0%,
        rgba(255, 255, 255, 0.5) 80%,
        rgba(255, 255, 255, 1) 100%);
  }
}

/* 主内容区域 */
.content {
  width: 100%;
  padding: 20rpx 30rpx;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  position: relative;
  z-index: 2;
  flex: 1;

  /* 用户信息 */
  .user-info {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    margin-bottom: 40rpx;
    width: 100%;

    .user-profile {
      display: flex;
      align-items: center;
      margin-bottom: 30rpx;

      .user-avatar {
        width: 60rpx;
        height: 60rpx;
        border-radius: 50%;
        overflow: hidden;
        border: 1rpx solid #BABABA;
        margin-right: 15rpx;
        background-color: #fff;
      }

      .user-name {
        font-size: 28rpx;
        color: #000000;
        font-weight: 400;
      }
    }

    .gift-message {
      font-size: 32rpx;
      color: #000000;
      font-weight: 500;
      line-height: 1.4;
    }
  }

  /* 礼物卡片容器 */
  .gift-card-wrapper {
    width: 470rpx;
    height: 600rpx;
    box-sizing: border-box;
    margin: 0 auto 40rpx;
    display: flex;
    justify-content: center;
    position: relative;
  }

  /* 礼物卡片 */
  .gift-card {
    width: 100%;
    max-width: 470rpx;
    background-color: #FFFFFF;
    border-radius: 16rpx;
    overflow: hidden;
    box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.08);
    position: relative;
    z-index: 2;

    .gift-card-image {
      width: 100%;
      padding: 20rpx;
      border-radius: 16rpx;
      height: 352rpx;
      overflow: hidden;

      .card-image {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }

    .gift-card-content {
      padding: 40rpx 20rpx;
      position: relative;
      display: flex;
      flex-direction: column;
      align-items: center;
      border-top: 1rpx dashed rgba(0, 0, 0, 0.1);
      background-color: #fff;
      margin: 20rpx 20rpx;
      border: 2rpx dashed #E2F1F5;
      border-radius: 12rpx;

      .gift-wish-container {
        width: 100%;
        text-align: center;
        margin-bottom: 30rpx;
      }

      .gift-wish {
        font-size: 32rpx;
        color: #000000;
        font-weight: 500;
        line-height: 1.5;
      }

      .gift-from {
        font-size: 24rpx;
        color: #666666;
        position: absolute;
        right: 30rpx;
        bottom: 20rpx;
      }
    }
  }

  /* 商品列表容器 */
  .product-list-container {
    width: 100%;
    background-color: transparent;
    border-radius: 16rpx;
    overflow: hidden;
    margin-bottom: 40rpx;

    .product-count-info {
      padding: 20rpx 30rpx;
      background: linear-gradient(297.79deg, #DAF2FA -25.9%, #F7FCFE 67.16%);
      border-bottom: 1rpx solid rgba(255, 255, 255, 0.2);

      .product-count {
        font-size: 28rpx;
        color: #333333;
        font-weight: 500;
      }
    }

    .product-list {
      background: linear-gradient(297.79deg, #DAF2FA -25.9%, #F7FCFE 67.16%);

      .product-item {
        display: flex;
        padding: 30rpx;
        border-bottom: 1rpx solid rgba(255, 255, 255, 0.2);

        &:last-child {
          border-bottom: none;
        }

        .product-image {
          width: 160rpx;
          height: 160rpx;
          border-radius: 8rpx;
          overflow: hidden;
          margin-right: 20rpx;
          border: 1rpx solid #FFFFFF;

          image {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }
        }

        .product-info {
          flex: 1;
          display: flex;
          flex-direction: column;
          justify-content: space-between;

          .product-name {
            font-size: 28rpx;
            color: #333333;
            font-weight: 500;
            margin-bottom: 10rpx;
            line-height: 1.3;
          }

          .product-attrs {
            font-size: 24rpx;
            color: #666666;
            margin-bottom: 20rpx;
          }

          .product-price-quantity {
            display: flex;
            justify-content: space-between;
            align-items: center;

            .product-price {
              font-size: 32rpx;
              color: #ff6b6b;
              font-weight: 500;
            }

              .quantity-num {
                font-size: 26rpx;
                color: #333333;
            }
          }
        }
      }
    }
  }

  /* 地址信息样式 */
  .address-info {
    margin-top: 30rpx;
    background: linear-gradient(297.79deg, #DAF2FA -25.9%, #F7FCFE 67.16%);
    border-radius: 16rpx;
    overflow: hidden;

  .address-title {
    padding: 20rpx 30rpx;
    border-bottom: 1rpx solid rgba(255, 255, 255, 0.2);

    .title-text {
      font-size: 28rpx;
      color: #333333;
      font-weight: 500;
    }
  }

  .address-content {
    padding: 20rpx 30rpx;

    .address-row {
      display: flex;
      margin-bottom: 10rpx;

      &:last-child {
        margin-bottom: 0;
      }

      .address-label {
        font-size: 26rpx;
        color: #666666;
        min-width: 140rpx;
      }

      .address-value {
        font-size: 26rpx;
        color: #333333;
        flex: 1;
        }
      }
    }
  }
}

/* 底部容器 */
.bottom-container {
  width: 100%;
  position: fixed;
  bottom: 0;
  left: 0;
  padding-bottom: 40rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 10;
  background-color: #FFFFFF;
}

/* 提示信息 */
.notice-container {
  width: 90%;
  text-align: center;
  margin-bottom: 30rpx;

  .notice-text {
    font-size: 24rpx;
    color: #666666;
    text-align: center;
    line-height: 1.6;
  }
}

/* 底部操作栏 */
.bottom-action-bar {
  width: 90%;

  .action-btn {
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    padding: 20rpx 28rpx;
    gap: 16rpx;
    width: 686rpx;
    height: 84rpx;
    border-radius: 16rpx;
    border: none;
    font-family: 'PingFang SC';
    font-style: normal;
    font-weight: 400;
    font-size: 32rpx;
    line-height: 44rpx;
    flex: none;
    flex-grow: 0;
  }

  .primary-btn {
    background: #20201E;
    color: #A9FF00;
  }

  .disabled-btn {
    background: #F0F0F0;
    color: #999999;
  }
}
</style>