<template>
  <view class="exchange-record-container">
    <!-- 筛选器 -->
    <view class="filter-container">
      <view class="filter-tabs">
        <view
          v-for="(item, index) in filterTabs"
          :key="index"
          class="filter-tab"
          :class="{ active: activeFilter === item.value }"
          @click="switchFilter(item.value)"
        >
          <view class="tab-text-container">
            <text class="tab-text">{{ item.label }}</text>
            <image
              v-if="activeFilter === item.value"
              class="tab-indicator"
              src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/tab_indicator.png"
              mode="aspectFit"
            />
          </view>
        </view>
      </view>
    </view>

    <!-- 兑换记录列表 -->
    <view class="record-list">
      <view v-if="recordList.length === 0 && !loading" class="empty-state">
        <u-empty text="暂无兑换记录" mode="list"></u-empty>
      </view>
      
      <view v-else>
        <view
          v-for="item in recordList"
          :key="item.id"
          class="record-item"
        >
          <view class="record-header">
            <view class="record-info">
              <text class="record-title">{{ item.targetName }}</text>
              <view class="record-meta">
                <u-tag 
                  :text="getExchangeTypeText(item.exchangeType)" 
                  :type="getExchangeTypeColor(item.exchangeType)"
                  size="mini"
                ></u-tag>
                <text class="record-time">{{ item.exchangeTime | formatTime }}</text>
              </view>
            </view>
            
            <view class="record-status">
              <u-tag 
                :text="getStatusText(item.exchangeStatus)" 
                :type="getStatusColor(item.exchangeStatus)"
                size="mini"
              ></u-tag>
            </view>
          </view>
          
          <view class="record-content">
            <view class="cost-info">
              <view class="cost-item">
                <text class="cost-label">使用积分：</text>
                <text class="cost-value points">{{ item.pointsUsed }}积分</text>
              </view>
              <view v-if="item.cashAmount > 0" class="cost-item">
                <text class="cost-label">支付现金：</text>
                <text class="cost-value cash">¥{{ item.cashAmount }}</text>
              </view>
              <view class="cost-item">
                <text class="cost-label">兑换数量：</text>
                <text class="cost-value">{{ item.quantity }}</text>
              </view>
            </view>
            
            <view v-if="item.remark" class="record-remark">
              <text class="remark-label">备注：</text>
              <text class="remark-text">{{ item.remark }}</text>
            </view>
          </view>
          
          <view class="record-actions">
            <view
              v-if="item.exchangeStatus === 1 && item.exchangeType === 1"
              class="action-button primary"
              @click.stop="goToOrderDetail(item)"
            >
              查看订单
            </view>
            <view
              v-if="item.exchangeStatus === 1 && item.exchangeType === 2"
              class="action-button primary"
              @click.stop="goToCouponDetail(item)"
            >
              查看优惠券
            </view>
            <view
              v-if="item.exchangeStatus === 2"
              class="action-button error"
              @click.stop="retryExchange(item)"
            >
              重新兑换
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 加载更多 -->
    <view v-if="hasMore" class="load-more" @click="loadMore">
      <u-loading-icon v-if="loading" mode="circle"></u-loading-icon>
      <text v-else>点击加载更多</text>
    </view>
    
    <!-- 没有更多数据 -->
    <view v-else-if="recordList.length > 0" class="no-more">
      <text>没有更多数据了</text>
    </view>
  </view>
</template>

<script>
import { getUserExchangeList } from '@/api/point'

export default {
  name: 'ExchangeRecord',
  data() {
    return {
      // 筛选选项
      filterTabs: [
        { label: '全部', value: null },
        { label: '商品换购', value: 1 },
        { label: '优惠券兑换', value: 2 }
      ],
      activeFilter: null,
      
      // 兑换记录
      recordList: [],
      query: {
        pageNum: 1,
        pageSize: 10,
        exchangeType: null
      },
      
      // 加载状态
      loading: false,
      hasMore: true
    }
  },
  
  onLoad() {
    this.getRecordList()
  },
  
  onPullDownRefresh() {
    this.refresh()
  },
  
  onReachBottom() {
    this.loadMore()
  },
  
  filters: {
    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}`
    }
  },
  
  methods: {
    // 获取兑换记录列表
    async getRecordList(append = false) {
      if (this.loading) return

      this.loading = true
      try {
        // 构建查询参数，只有当exchangeType不为null时才添加到参数中
        const params = {
          pageNum: this.query.pageNum,
          pageSize: this.query.pageSize
        };

        if (this.query.exchangeType !== null) {
          params.exchangeType = this.query.exchangeType;
        }

        const result = await getUserExchangeList(params)
        const list = result.data.list || []
        
        if (append) {
          this.recordList = [...this.recordList, ...list]
        } else {
          this.recordList = list
        }
        
        this.hasMore = list.length >= this.query.pageSize
      } catch (error) {
        uni.showToast({
          title: '获取兑换记录失败',
          icon: 'none'
        })
      } finally {
        this.loading = false
      }
    },
    
    // 刷新数据
    async refresh() {
      this.query.pageNum = 1
      this.hasMore = true
      await this.getRecordList()
      uni.stopPullDownRefresh()
    },
    
    // 加载更多
    loadMore() {
      if (!this.hasMore || this.loading) return
      this.query.pageNum++
      this.getRecordList(true)
    },
    
    // 切换筛选器
    switchFilter(value) {
      this.activeFilter = value
      this.query.exchangeType = value
      this.query.pageNum = 1
      this.hasMore = true
      this.getRecordList()
    },
    
    // 获取兑换类型文本
    getExchangeTypeText(type) {
      const typeMap = {
        1: '商品换购',
        2: '优惠券兑换'
      }
      return typeMap[type] || '未知'
    },
    
    // 获取兑换类型颜色
    getExchangeTypeColor(type) {
      const colorMap = {
        1: 'primary',
        2: 'success'
      }
      return colorMap[type] || 'info'
    },
    
    // 获取状态文本
    getStatusText(status) {
      const statusMap = {
        1: '兑换成功',
        2: '兑换失败',
        3: '已退回'
      }
      return statusMap[status] || '未知'
    },
    
    // 获取状态颜色
    getStatusColor(status) {
      const colorMap = {
        1: 'success',
        2: 'error',
        3: 'warning'
      }
      return colorMap[status] || 'info'
    },
    
    // 跳转订单详情
    goToOrderDetail(item) {
      if (item.orderId) {
        uni.navigateTo({
          url: `/pages/order/orderDetail?id=${item.orderId}`
        })
      } else {
        uni.showToast({
          title: '订单信息不存在',
          icon: 'none'
        })
      }
    },
    
    // 跳转优惠券列表页
    goToCouponDetail(item) {
      uni.navigateTo({
        url: '/pages/coupon/couponList'
      })
    },
    
    // 重新兑换
    retryExchange(item) {
      uni.showModal({
        title: '重新兑换',
        content: '是否重新兑换该商品？',
        success: (res) => {
          if (res.confirm) {
            uni.navigateTo({
              url: '/pages/signin/signin'
            })
          }
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.exchange-record-container {
  background-color: #f5f5f5;
  min-height: 100vh;
}

/* 筛选器样式 - 参考DIY页面Tab导航栏 */
.filter-container {
  background: white;
  margin-bottom: 16rpx;

  .filter-tabs {
    display: flex;
    background-color: #FFFFFF;
    padding: 20rpx 30rpx;
    position: relative;
    z-index: 5;
    gap: 20rpx;
    justify-content: center;

    .filter-tab {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 20rpx;
      position: relative;
      transition: all 0.3s ease;

      .tab-text-container {
        position: relative;
        display: flex;
        flex-direction: column;
        align-items: center;
      }

      .tab-text {
        font-size: 32rpx;
        color: #666666;
        font-weight: 500;
        transition: all 0.3s ease;
        position: relative;
        z-index: 2;
      }

      &.active .tab-text {
        color: #000000;
        font-weight: 600;
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
  }
}

.record-list {
  padding: 0 32rpx;
}

.empty-state {
  padding: 120rpx 0;
}

.record-item {
  background: white;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
  
  .record-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 20rpx;
    
    .record-info {
      flex: 1;
      
      .record-title {
        font-size: 32rpx;
        font-weight: bold;
        color: #333;
        margin-bottom: 12rpx;
        display: block;
      }
      
      .record-meta {
        display: flex;
        align-items: center;
        
        .record-time {
          font-size: 24rpx;
          color: #999;
          margin-left: 16rpx;
        }
      }
    }
    
    .record-status {
      margin-left: 16rpx;
    }
  }
  
  .record-content {
    margin-bottom: 20rpx;
    
    .cost-info {
      .cost-item {
        display: flex;
        justify-content: space-between;
        margin-bottom: 8rpx;
        font-size: 28rpx;
        
        .cost-label {
          color: #666;
        }
        
        .cost-value {
          font-weight: bold;

          &.points {
            color: #A9FF00;
          }

          &.cash {
            color: #A9FF00;
          }
        }
      }
    }
    
    .record-remark {
      margin-top: 16rpx;
      padding-top: 16rpx;
      border-top: 2rpx solid #f0f0f0;
      
      .remark-label {
        font-size: 24rpx;
        color: #666;
      }
      
      .remark-text {
        font-size: 24rpx;
        color: #333;
        margin-left: 8rpx;
      }
    }
  }
  
  .record-actions {
    display: flex;
    justify-content: flex-end;
    gap: 16rpx;

    .action-button {
      padding: 16rpx 32rpx;
      border-radius: 8rpx;
      font-size: 28rpx;
      font-weight: 500;
      text-align: center;
      cursor: pointer;
      transition: all 0.3s ease;

      &.primary {
        background: #20201E;
        color: #A9FF00;
        border: none;

        &:active {
          opacity: 0.8;
          transform: scale(0.98);
        }
      }

      &.error {
        background: #20201E;
        color: #ff3b30;
        border: none;

        &:active {
          opacity: 0.8;
          transform: scale(0.98);
        }
      }
    }
  }
}

.load-more {
  text-align: center;
  padding: 40rpx;
  color: #666;
  font-size: 28rpx;
}

.no-more {
  text-align: center;
  padding: 40rpx;
  color: #999;
  font-size: 24rpx;
}
</style> 