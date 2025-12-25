<template>
  <view class="content">
    <!-- 顶部导航栏 -->
    <view class="navbar">
      <view 
        v-for="(item, index) in navList" 
        :key="index" 
        class="nav-item" 
        :class="{ 'current': tabCurrentIndex === index }"
        @tap="tabClick(index)"
      >
        {{ item.text }}
      </view>
    </view>
    
    <!-- 积分卡片 -->
    <view class="point-card">
      <view class="point-rule" @tap="showRules">
        <text>使用规则</text>
      </view>
      <view class="point-content">
        <view class="point-info">
          <view class="point-label">我的积分</view>
          <view class="point-value">{{ pointInfo.total || 0 }}</view>
          <view class="use-btn" @tap="goUse">去使用</view>
        </view>
      </view>
    </view>
    
    <!-- 积分列表 -->
    <view class="point-list">
      <view 
        class="point-item" 
        v-for="(item, index) in pointList" 
        :key="index"
      >
        <view class="point-desc">{{ item.desc }}</view>
        <view
          class="point-change"
          :class="{
            'point-add': item.change.toString().startsWith('+'),
            'point-reduce': item.change.toString().startsWith('-')
          }"
        >
          {{ item.change }}
        </view>
        <view class="point-time">{{ item.time }}</view>
        <view class="divider"></view>
      </view>
      
      <!-- 空数据提示 -->
      <view class="empty" v-if="pointList.length === 0">
        <text>暂无积分记录</text>
      </view>
    </view>
    
    <!-- 规则弹窗 -->
    <view 
      class="point-rules-popup" 
      v-if="showRulesPopup" 
      @tap="closeRulesPopup"
    >
      <view 
        class="rules-content" 
        @tap.stop
      >
        <view class="rules-header">
          <text class="rules-title">积分使用规则</text>
          <view class="close-btn" @tap="closeRulesPopup">×</view>
        </view>
        <view class="rules-body">
          <view class="rule-item">
            <view class="rule-title">
              <view class="rule-badge">1</view>
              <text>购物获得积分</text>
            </view>
            <view class="rule-desc">购买商品可获得订单金额1%的积分奖励</view>
          </view>
          <view class="rule-item">
            <view class="rule-title">
              <view class="rule-badge">2</view>
              <text>积分抵扣比例</text>
            </view>
            <view class="rule-desc">100积分可抵扣1元人民币</view>
          </view>
          <view class="rule-item">
            <view class="rule-title">
              <view class="rule-badge">3</view>
              <text>最高使用限额</text>
            </view>
            <view class="rule-desc">每笔订单最高可使用2000积分</view>
          </view>
          <view class="rule-item">
            <view class="rule-title">
              <view class="rule-badge">4</view>
              <text>积分有效期</text>
            </view>
            <view class="rule-desc">积分有效期为1年，请及时使用</view>
          </view>
          <view class="rule-item">
            <view class="rule-title">
              <view class="rule-badge">5</view>
              <text>使用范围</text>
            </view>
            <view class="rule-desc">积分仅限本平台使用</view>
          </view>
        </view>
        <view class="rules-footer">
          <view class="confirm-btn" @tap="closeRulesPopup">我知道了</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getMyPoints, getPointRecords, getCurrentIntegration, getIntegrationHistory } from '@/api/point';

export default {
  data() {
    return {
      navList: [
        { text: '全部' },
        { text: '获得' },
        { text: '使用' }
      ],
      tabCurrentIndex: 0,
      pointInfo: {
        total: 0,
        available: 0
      },
      pointList: [],
      page: 1,
      limit: 20,
      loading: false,
      finished: false,
      showRulesPopup: false
    };
  },
  
  onLoad() {
    this.getPointInfo();
    this.getPointList();
  },
  
  onPullDownRefresh() {
    this.page = 1;
    this.finished = false;
    this.getPointInfo();
    this.getPointList(() => {
      uni.stopPullDownRefresh();
    });
  },
  
  onReachBottom() {
    if (!this.finished && !this.loading) {
      this.page++;
      this.getPointList();
    }
  },
  
  methods: {
    // 获取积分信息
    getPointInfo() {
      getCurrentIntegration().then(res => {
        if (res.code === 200) {
          this.pointInfo = {
            total: res.data || 0,
            available: res.data || 0
          };
        } else {
          uni.showToast({
            title: res.message || '获取积分信息失败',
            icon: 'none'
          });
        }
      }).catch(err => {
        console.error('获取积分信息失败', err);
        uni.showToast({
          title: '获取积分信息失败',
          icon: 'none'
        });
      });
    },
    
    // 获取积分记录
    getPointList(callback) {
      if (this.loading || this.finished) {
        if (callback) callback();
        return;
      }
      
      this.loading = true;
      
      const params = {
        pageNum: this.page,
        pageSize: this.limit
      };
      
      // 根据tab筛选类型
      if (this.tabCurrentIndex === 1) {
        params.type = 1; // 获得积分
      } else if (this.tabCurrentIndex === 2) {
        params.type = 2; // 使用积分
      } else {
        params.type = 0; // 全部
      }
      
      getIntegrationHistory(params).then(res => {
        this.loading = false;
        
        if (res.code === 200) {
          const list = res.data?.list || [];
          
          // 数据格式转换，适配新接口
          const formattedList = list.map(item => {
            let change = '';
            if (item.changeType === 0) {
              change = '+' + item.changeCount; // 获得积分显示 +
            } else if (item.changeType === 1) {
              change = '-' + item.changeCount; // 使用积分显示 -
            } else {
              change = item.changeCount;
            }
            return {
              id: item.id,
              desc: item.operateNote || (item.changeType === 0 ? '积分获取' : '积分消耗'),
              change,
              time: this.formatTime(item.createTime)
            };
          });
          
          if (this.page === 1) {
            this.pointList = formattedList;
          } else {
            this.pointList = [...this.pointList, ...formattedList];
          }
          
          // 判断是否全部加载完成
          const total = res.data?.total || 0;
          this.finished = this.pointList.length >= total;
        } else {
          uni.showToast({
            title: res.message || '获取积分记录失败',
            icon: 'none'
          });
        }
        
        if (callback) callback();
      }).catch(err => {
        console.error('获取积分记录失败', err);
        this.loading = false;
        
        uni.showToast({
          title: '获取积分记录失败',
          icon: 'none'
        });
        
        if (callback) callback();
      });
    },
    
    // 格式化时间
    formatTime(time) {
      if (!time) return '';
      // 直接用 new Date 解析 ISO 8601 字符串
      const date = new Date(time);
      if (isNaN(date.getTime())) return '';
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hour = String(date.getHours()).padStart(2, '0');
      const minute = String(date.getMinutes()).padStart(2, '0');
      return `${year}-${month}-${day} ${hour}:${minute}`;
    },
    
    // 切换标签
    tabClick(index) {
      if (this.tabCurrentIndex === index) return;
      
      this.tabCurrentIndex = index;
      this.page = 1;
      this.finished = false;
      this.pointList = [];
      this.getPointList();
    },
    
    // 显示规则弹窗
    showRules() {
      this.showRulesPopup = true;
    },
    
    // 关闭规则弹窗
    closeRulesPopup() {
      this.showRulesPopup = false;
    },
    
    // 去使用积分
    goUse() {
      uni.navigateTo({
        url: '/pages/signin/signin'
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

page {
  background-color: #fff;
  padding-bottom: 30rpx;
}

.content {
  padding: 0;
}

/* 导航栏 */
.navbar {
  display: flex;
  height: 40px;
  padding: 0 15px;
  background: #fff;
  position: relative;
  z-index: 10;
  margin-bottom: 20rpx;
  border-bottom: 0.5px solid rgba(0, 0, 0, 0.1);
  
  .nav-item {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100%;
    font-size: 14px;
    position: relative;
    opacity: 0.4;
    font-weight: 350;
    
    &.current {
      color: #000;
      opacity: 1;
      font-weight: 500;
      
      &:after {
        content: "";
        position: absolute;
        left: 50%;
        bottom: 0;
        transform: translateX(-50%);
        width: 44px;
        height: 0;
        border-bottom: 2px solid #000;
      }
    }
  }
}

/* 积分卡片 */
.point-card {
  position: relative;
  width: calc(100% - 40rpx);
  height: 222rpx;
  background: linear-gradient(to bottom, #DAF2FA, #F7FCFE);
  border-radius: 10px;
  box-shadow: inset 0px 4px 4px 0px #f7fdff;
  padding: 30rpx;
  margin: 50rpx 20rpx 30rpx 20rpx;
  
  .point-content {
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 0 30rpx;
  }
  
  .point-info {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
  }
  
  .point-value {
    font-size: 48rpx;
    font-weight: 500;
    line-height: 1.2;
    margin: 10rpx 0;
  }
  
  .point-label {
    font-size: 24rpx;
    font-weight: 400;
    line-height: 1.4;
  }
  
  .point-rule {
    position: absolute;
    top: 20rpx;
    right: 20rpx;
    font-size: 12px;
    opacity: 0.8;
    display: flex;
    align-items: center;
    background-color: rgba(255, 255, 255, 0.6);
    padding: 4rpx 12rpx;
    border-radius: 24rpx;
    
    .rule-icon {
      width: 24rpx;
      height: 24rpx;
      margin-right: 6rpx;
    }
  }
  
  .use-btn {
    margin-top: 15rpx;
    background-color: #D3FB51;
    color: #000;
    font-size: 14px;
    font-weight: 500;
    height: 60rpx;
    line-height: 60rpx;
    padding: 0 60rpx;
    border-radius: 30px;
    text-align: center;
  }
}

/* 积分明细列表 */
.point-list {
  padding: 0 15px;
  margin-top: 150rpx;
}

.point-item {
  position: relative;
  padding: 20rpx 0;
  height: 120rpx;
  display: flex;
  flex-direction: column;
  justify-content: center;
  
  .point-desc {
    font-size: 14px;
    font-weight: 500;
    color: #000;
    text-transform: uppercase;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: calc(100% - 120rpx); /* 为右边的增减按钮留出空间 */
  }
  
  .point-change {
    font-size: 14px;
    font-weight: 500;
    position: absolute;
    right: 0;
    top: 30rpx;
    
    &.point-add {
      color: #0a0;
    }
    
    &.point-reduce {
      color: #000;
    }
  }
  
  .point-time {
    font-size: 12px;
    color: rgba(0, 0, 0, 0.6);
    margin-top: 5rpx;
    text-transform: uppercase;
  }
  
  .divider {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 0.5px;
    background-color: #ccc;
  }
}

/* 空状态 */
.empty {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200rpx;
  color: #999;
  font-size: 28rpx;
}

/* 积分规则弹窗 */
.point-rules-popup {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  z-index: 999;
  display: flex;
  justify-content: center;
  align-items: center;
  
  .rules-content {
    width: 80%;
    background-color: #fff;
    border-radius: 16rpx;
    overflow: hidden;
    max-height: 80vh;
    display: flex;
    flex-direction: column;
  }
  
  .rules-header {
    position: relative;
    padding: 30rpx;
    text-align: center;
    border-bottom: 1rpx solid #f0f0f0;
    
    .rules-title {
      font-size: 32rpx;
      font-weight: 500;
      color: #000;
    }
    
    .close-btn {
      position: absolute;
      right: 30rpx;
      top: 30rpx;
      font-size: 40rpx;
      color: #999;
      line-height: 1;
    }
  }
  
  .rules-body {
    padding: 30rpx;
    max-height: 60vh;
    overflow-y: auto;
    
    .rule-item {
      margin-bottom: 30rpx;
      
      &:last-child {
        margin-bottom: 0;
      }
    }
    
    .rule-title {
      display: flex;
      align-items: center;
      margin-bottom: 10rpx;
      font-size: 28rpx;
      font-weight: 500;
      color: #000;
    }
    
    .rule-badge {
      width: 36rpx;
      height: 36rpx;
      background-color: #D3FB51;
      border-radius: 50%;
      display: flex;
      justify-content: center;
      align-items: center;
      font-size: 24rpx;
      font-weight: 600;
      margin-right: 16rpx;
    }
    
    .rule-desc {
      font-size: 24rpx;
      color: #666;
      padding-left: 52rpx;
      line-height: 1.5;
    }
  }
  
  .rules-footer {
    padding: 20rpx 30rpx 40rpx;
    
    .confirm-btn {
      background-color: #D3FB51;
      color: #000;
      font-size: 28rpx;
      font-weight: 500;
      height: 80rpx;
      line-height: 80rpx;
      border-radius: 40rpx;
      text-align: center;
    }
  }
}
</style> 