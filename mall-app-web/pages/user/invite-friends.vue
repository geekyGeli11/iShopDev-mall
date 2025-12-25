<template>
  <view class="invite-friends-container">
    <!-- 背景图片 -->
    <image class="background-image" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/images/invite_page_background.png" mode="scaleToFill" />

    <!-- 自定义导航栏 -->
    <nav-bar
      :placeholder="true"
      bg-color="transparent"
      :back="true"
    >
    </nav-bar>z

    <!-- 页面内容 -->
    <view class="page-content">
      
      <!-- 主标题 -->
      <view class="main-title">
        <text class="title-text">邀请好友注册</text>
        <text class="title-text">一起拿奖励</text>
      </view>
      
      <!-- 奖励说明卡片 -->
      <view class="reward-card">
        <view class="reward-content">
          <view class="reward-icon">
            <image class="icon-image" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/images/invite_coins.png" mode="aspectFit"></image>
          </view>
          <view class="reward-text">
            <text class="reward-desc">好友注册成功后，您可获得</text>
            <text class="reward-amount">200 积分</text>
          </view>
        </view>
      </view>
      
      <!-- 邀请统计 -->
      <view class="invite-stats">
        <text class="stats-label">您已邀请</text>
        <text class="stats-count">{{ inviteCount || 0 }} 人</text>
      </view>
      
      <!-- 邀请记录列表 -->
      <view class="invite-records" v-if="inviteList.length > 0">
        <view 
          class="record-item" 
          v-for="(item, index) in inviteList" 
          :key="index"
        >
          <view class="record-left">
            <image 
              class="user-avatar" 
              :src="item.avatar || 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/miss-face.png'" 
              mode="aspectFill"
            ></image>
            <text class="user-name">{{ item.nickname || '用户' }}</text>
          </view>
          <text class="register-time">{{ formatTime(item.registerTime) }}</text>
        </view>
      </view>
      
      <!-- 空状态 -->
      <view class="empty-state" v-if="inviteList.length === 0 && !loading">
        <text class="empty-text">暂无邀请记录</text>
      </view>
    </view>
    
    <!-- 底部邀请按钮 -->
    <view class="bottom-action">
      <button
        class="invite-btn"
        open-type="share"
        :disabled="!hasLogin || !inviteParam"
      >
        <text class="btn-text">马上邀请</text>
      </button>
    </view>
  </view>
</template>

<script>
import NavBar from '@/components/nav-bar.vue';
import { getMyInviteStatistics, generateInviteParam } from '@/api/invite';
import { mapState } from 'vuex';

export default {
  components: {
    'nav-bar': NavBar
  },
  
  computed: {
    ...mapState(['hasLogin', 'userInfo'])
  },
  
  data() {
    return {
      inviteCount: 0,
      inviteList: [],
      loading: false,
      inviteParam: '', // 邀请参数
      shareData: null // 分享数据
    };
  },
  
  onLoad() {
    this.loadInviteData();
    this.generateInviteParam();
  },

  onShow() {
    // 页面显示时刷新数据
    this.loadInviteData();
  },

  // 分享给朋友
  onShareAppMessage() {
    if (!this.hasLogin || !this.inviteParam) {
      return {
        title: '广横走文创 - 甄选本地好物',
        path: '/pages/new_index/index',
        imageUrl: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/images/share_card.png'
      };
    }

    return {
      title: `${this.userInfo.nickname || '用户'}邀请您体验广横走文创`,
      path: `/pages/new_index/index?inviteParam=${this.inviteParam}`,
      imageUrl: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/images/share_card.png'
    };
  },

  // 分享到朋友圈
  onShareTimeline() {
    if (!this.hasLogin || !this.inviteParam) {
      return {
        title: '广横走文创 - 甄选本地好物',
        query: ''
      };
    }

    return {
      title: `${this.userInfo.nickname || '用户'}邀请您体验广横走文创`,
      query: `inviteParam=${this.inviteParam}`
    };
  },
  
  methods: {
    // 加载邀请数据
    async loadInviteData() {
      if (!this.hasLogin) {
        return;
      }
      
      this.loading = true;
      
      try {
        const response = await getMyInviteStatistics();
        
        if (response.code === 200) {
          const data = response.data;
          this.inviteCount = data.totalInvites || 0;
          this.inviteList = data.invitedUsers || [];
        }
      } catch (error) {
        console.error('加载邀请数据失败:', error);
      } finally {
        this.loading = false;
      }
    },
    
    // 处理关闭按钮点击
    handleClose() {
      uni.navigateBack();
    },





    // 生成邀请参数
    async generateInviteParam() {
      if (!this.hasLogin) {
        return;
      }

      try {
        const response = await generateInviteParam();

        if (response.code === 200) {
          this.inviteParam = response.data.inviteParam;
          console.log('邀请参数生成成功:', this.inviteParam);
        }
      } catch (error) {
        console.error('生成邀请参数失败:', error);
      }
    },




    
    // 格式化时间
    formatTime(timeStr) {
      if (!timeStr) return '';
      
      const date = new Date(timeStr);
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      
      return `${year}/${month}/${day}`;
    }
  }
};
</script>

<style lang="scss" scoped>
.invite-friends-container {
  position: relative;
  width: 100%;
  min-height: 100vh;
  background-color: #F5F5F5;
  padding-bottom: 120rpx;
}

.background-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 540rpx;
  z-index: 1;
}

/* 导航栏右侧按钮 */
.nav-right {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.nav-btn {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: rgba(30, 30, 30, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 关闭按钮样式 */
.close-icon {
  position: relative;
  width: 24rpx;
  height: 24rpx;
}

.close-line {
  position: absolute;
  width: 24rpx;
  height: 2rpx;
  background: #FFFFFF;
  top: 50%;
  left: 50%;
  transform-origin: center;

  &:first-child {
    transform: translate(-50%, -50%) rotate(45deg);
  }

  &:last-child {
    transform: translate(-50%, -50%) rotate(-45deg);
  }
}

/* 更多按钮样式 */
.more-dots {
  display: flex;
  align-items: center;
  gap: 4rpx;
}

.dot {
  width: 6rpx;
  height: 6rpx;
  border-radius: 50%;
  background: #FFFFFF;
}

/* 页面内容 */
.page-content {
  position: relative;
  z-index: 5;
  padding: 80rpx 30rpx 80rpx;
}



/* 主标题 */
.main-title {
  margin-top: 60rpx;
  margin-bottom: 40rpx;
  text-align: left;
}

.title-text {
  display: block;
  font-family: 'Alibaba PuHuiTi';
  font-weight: 700;
  font-size: 48rpx;
  line-height: 1.5;
  color: #FFFFFF;
}

/* 奖励说明卡片 */
.reward-card {
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 32rpx;
  margin-bottom: 32rpx;
  border: 2rpx solid #EEEEEE;
}

.reward-content {
  display: flex;
  align-items: center;
  gap: 32rpx;
}

.reward-icon {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  background: linear-gradient(180deg, #A7FD03 0%, #B2F597 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-image {
  width: 48rpx;
  height: 48rpx;
}

.reward-text {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.reward-desc {
  font-size: 28rpx;
  font-weight: 600;
  color: #000000;
  line-height: 1.4;
}

.reward-amount {
  font-size: 28rpx;
  font-weight: 400;
  color: #000000;
  line-height: 1.4;
}

/* 邀请统计 */
.invite-stats {
  display: flex;
  align-items: center;
  gap: 8rpx;
  margin-bottom: 32rpx;
}

.stats-label {
  font-size: 28rpx;
  font-weight: 600;
  color: #0A0D05;
}

.stats-count {
  font-size: 28rpx;
  font-weight: 600;
  color: #9FA19D;
}

/* 邀请记录列表 */
.invite-records {
  background: #FFFFFF;
  border-radius: 16rpx;
  overflow: hidden;
  margin-bottom: 120rpx;
}

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx;
  border-bottom: 2rpx solid #EEEEEE;
  
  &:last-child {
    border-bottom: none;
  }
}

.record-left {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.user-avatar {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: #D9D9D9;
}

.user-name {
  font-size: 28rpx;
  font-weight: 400;
  color: #0A0D05;
}

.register-time {
  font-size: 28rpx;
  font-weight: 400;
  color: #9FA19D;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80rpx 0;
  margin-bottom: 120rpx;
}

.empty-text {
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.7);
}

/* 底部邀请按钮 */
.bottom-action {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20rpx 32rpx 40rpx;
  background: transparent;
  z-index: 10;
}

.invite-btn {
  background: #20201E;
  border-radius: 16rpx;
  padding: 20rpx 28rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  border: none;
  outline: none;
  width: 100%;
  box-sizing: border-box;
}

.invite-btn:disabled {
  background: #666;
  opacity: 0.6;
}

.invite-btn::after {
  border: none;
}

.btn-text {
  font-size: 32rpx;
  font-weight: 400;
  color: #A9FF00;
}
</style>
