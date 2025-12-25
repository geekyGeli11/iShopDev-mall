<template>
  <view class="container">
    <!-- 页面顶部 -->
    <view class="header">
      <view class="title">我的足迹</view>
      <view class="action" @tap="clearHistory" v-if="historyList.length > 0">清空</view>
    </view>
    
    <!-- 按日期分组显示的浏览记录 -->
    <view class="history-list" v-if="historyList.length > 0">
      <view v-for="(group, date) in groupedHistory" :key="date" class="history-group">
        <view class="date-header">{{ formatDate(date) }}</view>
        <view class="product-list">
          <view 
            v-for="(item, index) in group" 
            :key="index"
            class="product-item" 
            @tap="navToProduct(item)"
          >
            <view class="product-wrapper">
              <image class="product-img" :src="item.pic" mode="aspectFill"></image>
              <view class="product-info">
                <text class="product-name">{{ item.name }}</text>
                <text class="product-price">¥{{ item.price }}</text>
              </view>
            </view>
            <view class="delete-btn" @tap.stop="deleteItem(item.id)">×</view>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 空状态 -->
    <view class="empty-box" v-else>
      <image class="empty-image" src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/empty.png"></image>
      <text class="empty-text">您还没有浏览过商品哦~</text>
      <view class="action-btn" @tap="navToHome">去逛逛</view>
    </view>
    
    <!-- 加载更多 -->
    <uni-load-more :status="loadingType" v-if="historyList.length > 0"></uni-load-more>
    
    <!-- 清空确认弹窗 -->
    <uni-popup ref="clearPopup" type="dialog">
      <uni-popup-dialog
        title="提示"
        content="确定要清空所有浏览记录吗？"
        :showCancel="true"
        @confirm="confirmClear"
      ></uni-popup-dialog>
    </uni-popup>
  </view>
</template>

<script>
export default {
  data() {
    return {
      historyList: [],
      loadingType: 'more',
      page: 1,
      pageSize: 20,
      totalPage: 2
    };
  },
  
  computed: {
    groupedHistory() {
      const grouped = {};
      
      this.historyList.forEach(item => {
        const date = item.viewTime.split(' ')[0]; // 提取日期部分
        if (!grouped[date]) {
          grouped[date] = [];
        }
        grouped[date].push(item);
      });
      
      return grouped;
    }
  },
  
  onLoad() {
    this.loadHistoryList();
  },
  
  onPullDownRefresh() {
    this.page = 1;
    this.loadHistoryList();
    setTimeout(() => {
      uni.stopPullDownRefresh();
    }, 500);
  },
  
  onReachBottom() {
    if (this.loadingType === 'nomore') return;
    this.page++;
    this.loadHistoryList();
  },
  
  methods: {
    // 加载浏览历史
    loadHistoryList() {
      this.loadingType = 'loading';
      
      // 模拟请求
      setTimeout(() => {
        let mockData = [];
        
        // 生成最近三天的浏览记录
        for (let i = 0; i < 3; i++) {
          const date = new Date();
          date.setDate(date.getDate() - i);
          
          const dateStr = this.formatDateForApi(date);
          
          // 每天的浏览记录
          for (let j = 1; j <= 5; j++) {
            const index = i * 5 + j;
            const hours = Math.floor(Math.random() * 24);
            const minutes = Math.floor(Math.random() * 60);
            
            mockData.push({
              id: (this.page - 1) * 20 + index,
              productId: 100 + index,
              name: '浏览商品' + index,
              price: (Math.random() * 500 + 50).toFixed(2),
              pic: 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/product/product' + (index % 4 + 1) + '.jpg',
              viewTime: `${dateStr} ${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:00`
            });
          }
        }
        
        // 按照浏览时间排序
        mockData.sort((a, b) => new Date(b.viewTime) - new Date(a.viewTime));
        
        if (this.page === 1) {
          this.historyList = mockData;
        } else {
          this.historyList = this.historyList.concat(mockData);
        }
        
        // 更新加载状态
        if (this.page >= this.totalPage) {
          this.loadingType = 'nomore';
        } else {
          this.loadingType = 'more';
        }
      }, 500);
    },
    
    // 格式化日期 (用于显示)
    formatDate(dateStr) {
      const now = new Date();
      const today = now.toISOString().split('T')[0];
      const yesterday = new Date(now);
      yesterday.setDate(yesterday.getDate() - 1);
      const yesterdayStr = yesterday.toISOString().split('T')[0];
      
      if (dateStr === today) {
        return '今天';
      } else if (dateStr === yesterdayStr) {
        return '昨天';
      } else {
        const date = new Date(dateStr);
        return `${date.getMonth() + 1}月${date.getDate()}日`;
      }
    },
    
    // 格式化日期 (用于API)
    formatDateForApi(date) {
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      
      return `${year}-${month}-${day}`;
    },
    
    // 跳转到商品详情
    navToProduct(item) {
      uni.navigateTo({
        url: `/pages/product/product?id=${item.productId}`
      });
    },
    
    // 清空浏览历史
    clearHistory() {
      this.$refs.clearPopup.open();
    },
    
    // 确认清空
    confirmClear() {
      this.historyList = [];
      this.loadingType = 'more';
      
      uni.showToast({
        title: '已清空足迹',
        icon: 'success'
      });
      
      // 实际项目中应该调用API清空浏览历史
    },
    
    // 删除单个浏览记录
    deleteItem(id) {
      uni.showModal({
        title: '提示',
        content: '确认删除该浏览记录?',
        success: (res) => {
          if (res.confirm) {
            // 删除记录
            this.historyList = this.historyList.filter(item => item.id !== id);
            
            uni.showToast({
              title: '已删除',
              icon: 'success'
            });
            
            // 实际项目中应该调用API删除浏览记录
          }
        }
      });
    },
    
    // 跳转到首页
    navToHome() {
      uni.switchTab({
        url: '/pages/new_index/index'
      });
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
page,
.content {
  background: #f8f8f8;
}

.hot-section {
  display: flex;
  flex-wrap: wrap;
  padding: 0 30rpx;
  margin-top: 16rpx;
  
  .guess-item {
    display: flex;
    flex-direction: row;
    width: 100%;
    margin-top: 16rpx;
    background: #fff;
  }
  
  .image-wrapper {
    width: 30%;
    height: 250rpx;
    border-radius: 3px;
    overflow: hidden;
    
    image {
      width: 100%;
      height: 100%;
      opacity: 1;
    }
  }
  
  .title {
    font-size: 32rpx;
    color: #333333;
    line-height: 80rpx;
  }
  
  .title2 {
    font-size: 24rpx;
    color: #909399;
    line-height: 40rpx;
    height: 80rpx;
    overflow: hidden;
    text-overflow: ellipsis;
    display: block;
  }
  
  .price {
    font-size: 32rpx;
    color: #FF4225;
    line-height: 80rpx;
  }
  
  .txt {
    width: 70%;
    display: flex;
    flex-direction: column;
    padding-left: 40rpx;
  }
  
  .hor-txt {
    display: flex;
    justify-content: space-between;
  }
  
  .time {
    font-size: 24rpx;
    color: #333333;
    line-height: 80rpx;
  }
}

.container {
  padding-bottom: 30rpx;
}

// 页面顶部
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 30rpx;
  background: #fff;
  position: sticky;
  top: 0;
  z-index: 10;
  
  .title {
    font-size: 32rpx;
    font-weight: bold;
    color: #303133;
  }
  
  .action {
    font-size: 28rpx;
    color: #909399;
  }
}

// 历史记录
.history-list {
  padding: 20rpx;
  
  .history-group {
    margin-bottom: 30rpx;
    
    .date-header {
      font-size: 28rpx;
      color: #909399;
      padding: 10rpx 20rpx;
      background: #f1f1f1;
      border-radius: 30rpx;
      display: inline-block;
      margin-bottom: 20rpx;
    }
    
    .product-list {
      .product-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        background: #fff;
        padding: 20rpx;
        margin-bottom: 20rpx;
        border-radius: 12rpx;
        box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
        
        .product-wrapper {
          display: flex;
          align-items: center;
          flex: 1;
          
          .product-img {
            width: 120rpx;
            height: 120rpx;
            border-radius: 8rpx;
            margin-right: 20rpx;
          }
          
          .product-info {
            flex: 1;
            
            .product-name {
              font-size: 28rpx;
              color: #303133;
              display: -webkit-box;
              -webkit-box-orient: vertical;
              -webkit-line-clamp: 2;
              overflow: hidden;
              text-overflow: ellipsis;
              margin-bottom: 10rpx;
            }
            
            .product-price {
              font-size: 30rpx;
              color: #fa436a;
              font-weight: bold;
            }
          }
        }
        
        .delete-btn {
          width: 60rpx;
          height: 60rpx;
          line-height: 60rpx;
          text-align: center;
          font-size: 40rpx;
          color: #909399;
        }
      }
    }
  }
}

// 空状态
.empty-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
  
  .empty-image {
    width: 240rpx;
    height: 240rpx;
    margin-bottom: 30rpx;
  }
  
  .empty-text {
    font-size: 28rpx;
    color: #909399;
    margin-bottom: 40rpx;
  }
  
  .action-btn {
    width: 200rpx;
    height: 70rpx;
    line-height: 70rpx;
    background: linear-gradient(to right, #0088FF, #36BBFA);
    color: #fff;
    text-align: center;
    border-radius: 35rpx;
    font-size: 28rpx;
  }
}
</style> 