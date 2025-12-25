<template>
  <view class="invite-records-container">
    <!-- 自定义导航栏 -->
    <nav-bar
      title="邀请记录"
      :placeholder="true" 
      bg-color="transparent" 
      :titleCenter="true"
      back-icon-style="dark"
    >
    </nav-bar>
    
    <!-- 统计汇总 -->
    <view class="summary-section">
      <view class="summary-card">
        <view class="summary-item">
          <text class="summary-label">邀请总数</text>
          <text class="summary-value">{{ statistics.totalInvites || 0 }}</text>
        </view>
        <view class="summary-item">
          <text class="summary-label">今日邀请</text>
          <text class="summary-value">{{ statistics.todayInvites || 0 }}</text>
        </view>
        <view class="summary-item">
          <text class="summary-label">累计积分</text>
          <text class="summary-value">{{ statistics.totalRewardPoints || 0 }}</text>
        </view>
      </view>
    </view>
    
    <!-- 奖励记录列表 -->
    <view class="records-section">
      <view class="section-title">奖励记录</view>
      
      <view class="records-list">
        <view 
          class="record-item" 
          v-for="(item, index) in rewardRecords" 
          :key="index"
        >
          <view class="record-left">
            <view class="record-main">
              <text class="record-desc">{{ item.rewardDesc }}</text>
              <text class="record-time">{{ formatTime(item.createTime) }}</text>
            </view>
            <view class="record-extra" v-if="item.inviteeNickname">
              <text class="invitee-name">{{ item.inviteeNickname }}</text>
            </view>
          </view>
          
          <view class="record-right">
            <text class="reward-amount" :class="'reward-type-' + item.rewardType">
              +{{ item.rewardValue }}{{ getRewardUnit(item.rewardType) }}
            </text>
            <text class="reward-status" :class="'status-' + item.status">
              {{ getStatusText(item.status) }}
            </text>
          </view>
        </view>
        
        <!-- 空状态 -->
        <view class="empty-state" v-if="rewardRecords.length === 0 && !loading">
          <image class="empty-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/common/empty.png" mode="aspectFit"></image>
          <text class="empty-text">暂无奖励记录</text>
        </view>
        
        <!-- 加载状态 -->
        <view class="loading-more" v-if="loading">
          <text class="loading-text">加载中...</text>
        </view>
      </view>
    </view>

    <!-- 固定底部按钮 -->
    <view class="fixed-bottom-btn" @click="goToIncome">
      <text class="btn-text">收益明细</text>
    </view>
  </view>
</template>

<script>
import NavBar from '@/components/nav-bar.vue';
import request from '@/utils/requestUtil';

export default {
  components: {
    'nav-bar': NavBar
  },
  
  data() {
    return {
      statistics: {
        totalInvites: 0,
        todayInvites: 0,
        totalRewardPoints: 0
      },
      rewardRecords: [],
      loading: false,
      pageNum: 1,
      pageSize: 20,
      hasMore: true
    };
  },
  
  onLoad() {
    this.loadStatistics();
    this.loadRewardRecords();
  },
  
  onReachBottom() {
    if (this.hasMore && !this.loading) {
      this.loadMore();
    }
  },
  
  onPullDownRefresh() {
    this.refresh();
  },



  methods: {
    // 加载统计数据
    async loadStatistics() {
      try {
        const response = await request({
          method: 'GET',
          url: '/api/invite/my-statistics'
        });

        if (response.code === 200) {
          this.statistics = response.data;
        }
      } catch (error) {
        console.error('加载统计数据失败:', error);
      }
    },
    
    // 加载奖励记录
    async loadRewardRecords(reset = false) {
      if (this.loading) return;
      
      this.loading = true;
      
      try {
        if (reset) {
          this.pageNum = 1;
          this.rewardRecords = [];
        }
        
        const response = await request({
          method: 'GET',
          url: '/api/invite/my-rewards',
          params: {
            pageNum: this.pageNum,
            pageSize: this.pageSize
          }
        });
        
        if (response.code === 200) {
          const data = response.data;
          const records = data.list || [];
          
          if (reset) {
            this.rewardRecords = records;
          } else {
            this.rewardRecords = this.rewardRecords.concat(records);
          }
          
          // 判断是否还有更多数据
          this.hasMore = records.length >= this.pageSize;
        }
      } catch (error) {
        console.error('加载奖励记录失败:', error);
        uni.showToast({
          title: '加载失败',
          icon: 'none'
        });
      } finally {
        this.loading = false;
        if (reset) {
          uni.stopPullDownRefresh();
        }
      }
    },
    
    // 加载更多
    loadMore() {
      this.pageNum++;
      this.loadRewardRecords();
    },
    
    // 刷新
    refresh() {
      this.loadStatistics();
      this.loadRewardRecords(true);
    },
    
    // 格式化时间
    formatTime(timeStr) {
      if (!timeStr) return '';
      
      const date = new Date(timeStr);
      const now = new Date();
      const diff = now.getTime() - date.getTime();
      
      // 一天内显示相对时间
      if (diff < 24 * 60 * 60 * 1000) {
        const hours = Math.floor(diff / (60 * 60 * 1000));
        const minutes = Math.floor((diff % (60 * 60 * 1000)) / (60 * 1000));
        
        if (hours > 0) {
          return `${hours}小时前`;
        } else if (minutes > 0) {
          return `${minutes}分钟前`;
        } else {
          return '刚刚';
        }
      }
      
      // 超过一天显示具体日期
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      
      if (year === now.getFullYear()) {
        return `${month}-${day}`;
      } else {
        return `${year}-${month}-${day}`;
      }
    },

    // 跳转到收益明细页面
    goToIncome() {
      uni.navigateTo({
        url: '/pages/user/income'
      });
    },

    // 获取奖励类型单位
    getRewardUnit(type) {
      switch (type) {
        case 1: return '积分';
        case 2: return '元';
        case 3: return '张';
        default: return '';
      }
    },

    // 获取状态文本
    getStatusText(status) {
      switch (status) {
        case 0: return '待发放';
        case 1: return '已发放';
        case 2: return '发放失败';
        default: return '未知';
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.invite-records-container {
  background: #f8f8f8;
  min-height: 100vh;
}

/* 统计汇总区域 */
.summary-section {
  margin: 20rpx 32rpx 24rpx;
}

.summary-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 32rpx;
  display: flex;
  justify-content: space-around;
}

.summary-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
}

.summary-label {
  font-size: 28rpx;
  color: #666;
}

.summary-value {
  font-size: 40rpx;
  font-weight: 600;
  color: #647D00;
}

/* 记录列表区域 */
.records-section {
  margin: 0 32rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 24rpx;
}

.records-list {
  background: #fff;
  border-radius: 16rpx;
  overflow: hidden;
}

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx;
  border-bottom: 2rpx solid #f5f5f5;
  
  &:last-child {
    border-bottom: none;
  }
}

.record-left {
  flex: 1;
}

.record-main {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.record-desc {
  font-size: 32rpx;
  color: #333;
}

.record-time {
  font-size: 24rpx;
  color: #999;
}

.record-extra {
  margin-top: 8rpx;
}

.invitee-name {
  font-size: 24rpx;
  color: #647D00;
  background: rgba(100, 125, 0, 0.1);
  padding: 4rpx 12rpx;
  border-radius: 12rpx;
}

.record-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8rpx;
}

.reward-amount {
  font-size: 32rpx;
  font-weight: 600;

  &.reward-type-1 {
    color: #FF6B35; // 积分
  }

  &.reward-type-2 {
    color: #47C757; // 余额
  }

  &.reward-type-3 {
    color: #6B73FF; // 优惠券
  }
}

.reward-status {
  font-size: 24rpx;
  padding: 4rpx 12rpx;
  border-radius: 12rpx;

  &.status-0 {
    background: #FFF3E0;
    color: #FF9800; // 待发放
  }

  &.status-1 {
    background: #E8F5E8;
    color: #4CAF50; // 已发放
  }

  &.status-2 {
    background: #FFEBEE;
    color: #F44336; // 发放失败
  }
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80rpx 32rpx;
}

.empty-icon {
  width: 120rpx;
  height: 120rpx;
  margin-bottom: 24rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}

/* 加载状态 */
.loading-more {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 32rpx;
}

.loading-text {
  font-size: 28rpx;
  color: #999;
}

/* 固定底部按钮 */
.fixed-bottom-btn {
  position: fixed;
  left: 32rpx;
  bottom: 100rpx;
  width: 686rpx;
  height: 84rpx;
  background: #20201E;
  border-radius: 16rpx;
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  padding: 20rpx 28rpx;
  gap: 16rpx;
  z-index: 999;
}

.btn-text {
  font-family: 'PingFang SC', sans-serif;
  font-style: normal;
  font-weight: 400;
  font-size: 32rpx;
  line-height: 44rpx;
  color: #A9FF00;
  flex: none;
}
</style> 