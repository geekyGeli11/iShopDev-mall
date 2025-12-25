<template>
  <view class="content">
    <!-- 导航栏 -->
    <view class="navbar">
      <view 
        class="nav-item" 
        v-for="(item, index) in navList" 
        :key="index" 
        :class="{'active': tabCurrentIndex === index}"
        @tap="tabClick(index)"
      >
        <view class="tab-text-container">
          <text class="tab-text">{{item.text}}</text>
          <image 
            v-if="tabCurrentIndex === index"
            class="tab-indicator" 
            src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/tab_indicator.png" 
            mode="aspectFit"
          />
        </view>
      </view>
    </view>
    
    <!-- 礼物列表 -->
    <view class="gift-list">
      <view 
        class="gift-item" 
        v-for="(item, index) in giftList" 
        :key="index"
        @click="goToGiftDetail(item)"
      >
        <view class="gift-card">
          <view class="gift-content">
            <view class="gift-image-container">
              <image class="gift-image" :src="item.giftImage || item.productPic" mode="aspectFill"></image>
            </view>
            <view class="gift-info">
              <view class="gift-name">{{item.productName || '礼物'}}</view>
              <view class="gift-desc" v-if="giftType === 0">{{item.giftMessage || '小小"濠"礼，满载心意，愿它开启你生活的小确幸，所行皆坦途，所遇皆美好 。'}}</view>
              
              <!-- 赠送人信息 -->
              <view class="sender-info" v-if="giftType === 1">
                <view class="sender-label">赠送人</view>
                <view class="sender-detail">
                  <view class="sender-avatar">
                    <image :src="item.senderIcon || 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/images/default-avatar.png'" mode="aspectFill"></image>
                  </view>
                  <text class="sender-name">{{item.senderName || '匿名用户'}}</text>
                </view>
              </view>
              
              <!-- 赠送时间 -->
              <view class="gift-time-info" v-if="giftType === 1">
                <view class="time-label">赠送时间</view>
                <view class="time-value">{{formatDate(item.giftTime)}}</view>
              </view>
            </view>
          </view>
          
          <!-- 价格和按钮区域 - 移到卡片底部 -->
          <view class="bottom-row" @click.stop>
            <view class="gift-price">
              <text class="currency-symbol">¥</text>
              <text class="price-amount">{{item.giftAmount || 0}}</text>
            </view>
            <view class="btn-group">
              <button v-if="giftType === 0" class="share-btn custom-share-btn" open-type="share" :data-id="item.id" :data-image="item.giftImage || item.productPic">分享给好友</button>
              <view class="return-btn" v-if="giftType === 1" @tap="returnGift(index)">回礼</view>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 空状态 -->
      <view class="empty" v-if="giftList.length === 0">
        <text>暂无礼物记录</text>
      </view>
    </view>
  </view>
</template>

<script>
import { getSentGifts, getReceivedGifts } from '@/api/gift';
import { mapState } from 'vuex';

export default {
  data() {
    return {
      tabCurrentIndex: 0,
      giftType: 0, // 0: 赠送礼物, 1: 收到礼物
      navList: [
        { text: '赠送礼物' },
        { text: '收到礼物' }
      ],
      giftList: [],
      page: 1,
      limit: 10,
      loading: false,
      finished: false,
    }
  },
  
  computed: {
    ...mapState(['userInfo'])
  },
  
  onLoad(options) {
    // 处理tab参数，支持直接切换到指定tab
    if (options.tab === 'received') {
      this.tabCurrentIndex = 1;
      this.giftType = 1;
    } else if (options.tab === 'sent') {
      this.tabCurrentIndex = 0;
      this.giftType = 0;
    }
    
    // 加载礼物列表
    this.loadGiftList();
  },
  
  onPullDownRefresh() {
    this.page = 1;
    this.finished = false;
    this.loadGiftList(() => {
      uni.stopPullDownRefresh();
    });
  },
  
  onReachBottom() {
    if (!this.finished && !this.loading) {
      this.page++;
      this.loadGiftList();
    }
  },
  
  methods: {
    // 切换导航
    tabClick(index) {
      if (this.tabCurrentIndex === index) return;
      this.tabCurrentIndex = index;
      this.giftType = index;
      this.page = 1;
      this.finished = false;
      this.giftList = [];
      this.loadGiftList();
    },
    
    // 加载礼物列表
    loadGiftList(callback) {
      if (this.loading || this.finished) {
        if (callback) callback();
        return;
      }
      
      this.loading = true;
      uni.showLoading({ title: '加载中...' });
      
      // 根据类型调用不同的API
      const api = this.giftType === 0 ? getSentGifts : getReceivedGifts;
      const params = {
        pageNum: this.page,
        pageSize: this.limit
      };
      
      api(params).then(res => {
        const list = res.data?.list || [];
        
        // 直接使用服务端返回的数据，不做额外处理
        if (this.page === 1) {
          this.giftList = list;
        } else {
          this.giftList = [...this.giftList, ...list];
        }
        
        // 判断是否加载完成
        this.finished = list.length < this.limit;
        
        uni.hideLoading();
        this.loading = false;
        if (callback) callback();
      }).catch(err => {
        console.error('获取礼物列表失败', err);
        uni.showToast({
          title: '获取礼物列表失败',
          icon: 'none'
        });
        uni.hideLoading();
        this.loading = false;
        if (callback) callback();
      });
    },
    
    // 回赠礼物
    returnGift(index) {
      const item = this.giftList[index];
      if (!item) return;
      
      uni.navigateTo({
        url: `/pages/gift-bag/giftsList`
      });
    },  
    
    // 分享功能
    onShareAppMessage(e) {
      const id = e.target.dataset.id;
      const item = this.giftList.find(gift => gift.id == id);
      if (!item) return {};
      
      // 获取用户信息，参考 gift-confirm.vue 的获取方式
      const userId = this.userInfo && this.userInfo.id ? this.userInfo.id : '';
      const userName = this.userInfo && this.userInfo.nickname ? this.userInfo.nickname : '';
      const userIcon = this.userInfo && this.userInfo.icon ? encodeURIComponent(this.userInfo.icon) : '';
      
      return {
        title: '这是我为你精心准备的礼物，快拆开看看吧~',
        path: '/pages/gift-bag/gift-confirm?from=' + userId +
          (item.orderId ? '&orderId=' + item.orderId : '') +
          '&senderName=' + userName +
          '&senderIcon=' + userIcon,
        imageUrl: item.giftImage || item.productPic
      };
    },
    
    // 格式化日期
    formatDate(date) {
      if (!date) return '';
      if (typeof date === 'string') {
        date = new Date(date);
      }
      
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      
      return `${year}/${month}/${day}`;
    },
    
    // 跳转到礼物详情页
    goToGiftDetail(item) {
      if (!item || !item.id) return;
      
      // 构建跳转参数，和分享卡片进入的参数格式保持一致
      let url = `/pages/gift-bag/gift-confirm?orderId=${item.orderId}`;
      
      // 根据当前tab判断用户身份
      if (this.giftType === 0) {
        // 赠送礼物tab - 用户是发送者，不需要传from参数
        // 参数格式：orderId=xxx
      } else {
        // 收到礼物tab - 用户是接收者，需要传发送者信息
        // 参数格式：from=senderId&orderId=xxx&senderName=xxx&senderIcon=xxx
        if (item.senderId) {
          url = `/pages/gift-bag/gift-confirm?from=${item.senderId}&orderId=${item.orderId}`;
          
          // 添加发送者信息
          if (item.senderName) {
            url += `&senderName=${encodeURIComponent(item.senderName)}`;
          }
          if (item.senderIcon) {
            url += `&senderIcon=${encodeURIComponent(item.senderIcon)}`;
          }
        }
      }
      
      uni.navigateTo({
        url: url
      });
    }
  }
}
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
  background-color: #f7f7f7;
  padding-bottom: 30rpx;
}

.content {
  padding: 0;
}

/* 页面标题 */
.header {
  display: flex;
  height: 88rpx;
  line-height: 88rpx;
  justify-content: center;
  align-items: center;
  position: relative;
  background-color: #fff;
  
  .title {
    font-size: 34rpx;
    font-weight: 500;
  }
  
  .back-icon, .right-icon {
    position: absolute;
    width: 88rpx;
    height: 88rpx;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .back-icon {
    left: 0;
  }
  
  .right-icon {
    right: 0;
  }
  
  .icon {
    font-family: "iconfont";
    font-size: 40rpx;
  }
}

/* 导航栏 */
.navbar {
  display: flex;
  padding: 20rpx 30rpx;
  position: relative;
  z-index: 10;
  gap: 20rpx;
  justify-content: flex-start;
  width: 50%;
  align-self: flex-start;
  
  .nav-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20rpx;
    position: relative;
    transition: all 0.3s ease;
    
    &.active .tab-text {
      color: #000000;
      font-weight: 600;
    }
  }
  
  .tab-text-container {
    position: relative;
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  
  .tab-text {
    font-size: 28rpx;
    color: #666666;
    font-weight: 400;
    transition: all 0.3s ease;
    position: relative;
    z-index: 2;
  }
  
  .tab-indicator {
    position: absolute;
    bottom: 0rpx;
    left: 50%;
    transform: translateX(-50%);
    width: 100%;
    height: 24rpx;
    z-index: 1;
  }
}

/* 礼物列表 */
.gift-list {
  padding: 20rpx;
  
  .gift-item {
    position: relative;
    margin-bottom: 20rpx;
  }
  
  .gift-card {
    position: relative;
    border-radius: 16rpx;
    overflow: hidden;
    background: linear-gradient(180deg, #E6FFB4 -118.9%, #FFFFFF 100%);;
    box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
  }
  
  .gift-content {
    position: relative;
    z-index: 2;
    display: flex;
    padding: 30rpx;
  }
  
  .gift-image-container {
    position: relative;
  }
  
  .gift-image {
    width: 180rpx;
    height: 180rpx;
    border-radius: 16rpx;
    background-color: #f5f5f5;
  }
  
  .gift-info {
    flex: 1;
    padding-left: 30rpx;
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
  }
  
  .gift-name {
    font-size: 28rpx;
    font-weight: 400;
    color: #0A0D05;
    margin-bottom: 8rpx;
    line-height: 1.4;
  }
  
  .gift-desc {
    font-size: 24rpx;
    color: rgba(0, 0, 0, 0.6);
    margin-bottom: 20rpx;
    line-height: 1.4;
  }
  
  // 赠送人信息样式
  .sender-info {
    display: flex;
    align-items: center;
    gap: 16rpx;
    margin-bottom: 12rpx;
    
    .sender-label {
      font-size: 24rpx;
      color: #9FA19D;
    }
    
    .sender-detail {
      display: flex;
      align-items: center;
      gap: 8rpx;
    }
    
    .sender-avatar {
      width: 40rpx;
      height: 40rpx;
      border-radius: 50%;
      overflow: hidden;
      background-color: #D9D9D9;
      
      image {
        width: 100%;
        height: 100%;
      }
    }
    
    .sender-name {
      font-size: 24rpx;
      color: #666666;
    }
  }
  
  // 赠送时间样式
  .gift-time-info {
    display: flex;
    align-items: center;
    gap: 16rpx;
    margin-bottom: 20rpx;
    
    .time-label {
      font-size: 24rpx;
      color: #9FA19D;
    }
    
    .time-value {
      font-size: 24rpx;
      color: #666666;
    }
  }
  
  .bottom-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 30rpx;
    background-color: rgba(255, 255, 255, 0.7);
  }
  
  .gift-price {
    flex: 1;
    display: flex;
    align-items: baseline;
    font-family: 'PingFang SC', sans-serif;
    color: #0A0D05;
    
    .currency-symbol {
      font-size: 28rpx;
      font-weight: 500;
      margin-right: 4rpx;
    }
    
    .price-amount {
      font-size: 44rpx;
      font-weight: 500;
      line-height: 1;
    }
  }
  
  .btn-group {
    display: flex;
    flex-shrink: 0;
  }
  
  .share-btn, .return-btn {
    font-size: 28rpx;
    font-weight: 400;
    height: 64rpx;
    line-height: 64rpx;
    text-align: center;
    border-radius: 16rpx;
    background-color: #20201E;
    color: #A9FF00;
    width: 160rpx;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .share-btn {
    padding: 0;
    margin: 0;
    border: none;
    
    &::after {
      border: none;
    }
    
    &:not([size='mini']) {
      width: 160rpx;
      line-height: 64rpx;
      margin-left: 0;
      margin-right: 0;
    }
  }
  
  /* 加强特异性 */
  button.custom-share-btn {
    border-radius: 16rpx !important;
    border: none !important;
    box-shadow: none !important;
    background-color: #20201E !important;
    color: #A9FF00 !important;
    font-weight: 400 !important;
  }
}

/* 空状态 */
.empty {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400rpx;
  color: #999;
  font-size: 28rpx;
}


</style> 