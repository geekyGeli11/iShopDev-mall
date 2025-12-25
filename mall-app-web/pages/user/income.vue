<template>
  <view class="income-container">
    <!-- 自定义导航栏 -->
    <nav-bar
      title="收益明细"
      :back="true"
      :placeholder="true" 
      bg-color="transparent" 
      :titleCenter="true"
      back-icon-style="dark"
    >
    </nav-bar>
    
    <!-- 收益统计区域 -->
    <view class="income-stats-section">
      <image 
        class="income-bg" 
        src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/my/income_background.png" 
        mode="aspectFill"
      ></image>
      
      <!-- 装饰圆圈 -->
      <view class="decoration-circles">
        <view class="circle circle-1"></view>
        <view class="circle circle-2"></view>
        <view class="circle circle-3"></view>
        <view class="circle circle-4"></view>
      </view>
      
      <!-- 收益内容 -->
      <view class="income-content">
        <view class="income-header">
          <text class="income-label">当前总收益</text>
          <text class="income-rule" @tap="showRules">规则</text>
        </view>
        
        <view class="total-income">
          <text class="currency-symbol">￥</text>
          <text class="income-amount">{{ formattedCurrentTotalIncome }}</text>
        </view>
        
        <view class="income-details">
          <view class="detail-item">
            <text class="detail-label">可提现</text>
            <view class="detail-amount">
              <text class="detail-currency">￥</text>
              <text class="detail-value">{{ formattedWithdrawableAmount }}</text>
            </view>
          </view>
          
          <view class="detail-divider"></view>
          
          <view class="detail-item">
            <text class="detail-label">未入账</text>
            <view class="detail-amount">
              <text class="detail-currency">￥</text>
              <text class="detail-value">{{ formattedPendingAmount }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 邀请统计区域 -->
    <view class="invite-stats-section">
      <view class="section-header">
        <text class="section-title">邀请统计</text>
        <text class="view-more" @tap="viewInviteRecords">查看记录</text>
      </view>
      
      <view class="invite-stats-grid">
        <view class="stat-item">
          <text class="stat-value">{{ inviteStats.totalInvites || 0 }}</text>
          <text class="stat-label">邀请总数</text>
        </view>
        <view class="stat-item">
          <text class="stat-value">{{ inviteStats.registeredCount || 0 }}</text>
          <text class="stat-label">已注册</text>
        </view>
        <view class="stat-item">
          <text class="stat-value">{{ inviteStats.totalRewardPoints || 0 }}</text>
          <text class="stat-label">获得积分</text>
        </view>
        <view class="stat-item">
          <text class="stat-value">{{ inviteStats.conversionRate || '0.00' }}%</text>
          <text class="stat-label">转化率</text>
        </view>
      </view>
    </view>
    
    <!-- 记录与明细区域 -->
    <view class="records-section">
      <view class="records-header">
        <text class="records-title">记录与明细</text>
      </view>
      
      <!-- 年份选择器和年度收入统计 -->
      <view class="year-and-income-row">
        <!-- 年份选择器 -->
        <view class="year-selector">
          <picker 
            mode="selector" 
            :range="yearDisplayList" 
            :value="selectedYearIndex" 
            @change="onYearChange"
            class="year-picker"
          >
            <view class="year-selector-content">
              <text class="year-text">{{ selectedYear }}年</text>
              <image class="dropdown-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/arrow_down.png"></image>
            </view>
          </picker>
        </view>
        
        <!-- 年度收入统计 -->
        <view class="month-income-summary">
          <text class="summary-text">收入 ￥{{ yearTotalIncome }}</text>
        </view>
      </view>
      
      <!-- 月度收入列表 -->
      <view class="income-list">
        <view 
          class="income-item" 
          v-for="(item, index) in monthlyIncomeList" 
          :key="index"
        >
          <view class="income-item-left">
            <text class="month-label">{{ item.monthName }}总收入</text>
          </view>
          <view class="income-item-right">
            <text class="income-value">￥ {{ item.amount ? item.amount.toFixed(2) : '0.00' }}</text>
            <text v-if="item.isCurrent" class="current-tag">当前累计收入</text>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 常见问题 -->
    <view class="faq-section" @tap="showFAQ">
      <text class="faq-text">常见问题</text>
    </view>

    <!-- 底部提现功能区域 -->
    <view class="withdraw-bottom-section">

      <!-- 提现按钮区域 -->
      <view class="withdraw-buttons-wrapper">
        <view class="withdraw-buttons-container">
          <view class="withdraw-buttons">
            <!-- 去提现按钮 -->
            <view class="withdraw-btn withdraw-btn-dark" @tap="openWithdrawDialog">
              <text class="withdraw-btn-text">去提现</text>
            </view>

            <!-- 存入文创余额按钮 -->
            <view class="withdraw-btn withdraw-btn-gradient" @tap="openBalanceDialog">
              <text class="withdraw-btn-text withdraw-btn-text-dark">存入文创余额</text>
            </view>
          </view>
        </view>

        <!-- 税款说明 -->
        <view class="tax-notice">
          <text class="tax-notice-text">提现的偶然所得款项将采取分类分次计税办法扣除20%税款；存入文创账户不扣除税款</text>
        </view>
      </view>
    </view>

    <!-- 提现弹窗 -->
    <withdraw-dialog
      :visible="withdrawDialogVisible"
      :available-amount="inviteStats.withdrawableAmount"
      :withdraw-type="withdrawType"
      :account-info="userAccountInfo"
      @close="closeWithdrawDialog"
      @submit="handleWithdrawSubmit"
    />
  </view>
</template>

<script>
import NavBar from '@/components/nav-bar.vue';
import WithdrawDialog from '@/components/distribution/WithdrawDialog.vue';
import { getMyInviteStatistics, getRewardSummary, applyWithdraw } from '@/api/invite.js';
import { memberInfo } from '@/api/member.js';

export default {
  components: {
    'nav-bar': NavBar,
    'withdraw-dialog': WithdrawDialog
  },
  data() {
    return {
      selectedYear: 2025,
      selectedYearIndex: 2, // 默认选中2025年（索引为2）
      yearList: [2023, 2024, 2025],
      // 邀请统计数据 - 匹配后端返回字段，包含新的收益字段
      inviteStats: {
        totalInvites: 0,
        todayInvites: 0,
        registeredCount: 0,
        orderedCount: 0,
        totalRewardPoints: 0,
        totalRewardAmount: 0,
        pendingRewards: 0,
        conversionRate: 0,
        ranking: 0,
        // 新增的收益字段
        currentTotalIncome: 0,
        withdrawableAmount: 0,
        pendingAmount: 0
      },
      // 奖励汇总数据 - 匹配后端返回字段
      rewardSummary: {
        yearTotalAmount: 0,
        yearTotalPoints: 0,
        yearRewardCount: 0,
        monthlyRewards: []
      },
      // 提现弹窗相关
      withdrawDialogVisible: false,
      withdrawType: 'withdraw', // 'withdraw' 或 'balance'
      userAccountInfo: {
        nickname: '用户昵称',
        memberNo: 'XXXXXXXX',
        avatar: '',
        phone: ''
      },

      // 用户登录状态
      hasLogin: false
    };
  },

  computed: {
    // 从store获取登录状态
    hasLoginFromStore() {
      return this.$store.state.hasLogin;
    },

    // 从store获取用户信息
    userInfoFromStore() {
      return this.$store.state.userInfo || {};
    },
    // 年份显示列表
    yearDisplayList() {
      return this.yearList.map(year => `${year}年`);
    },
    
    // 年度总收益 - 格式化显示
    yearTotalIncome() {
      return this.rewardSummary.yearTotalAmount ? this.rewardSummary.yearTotalAmount.toFixed(2) : '0.00';
    },
    
    // 月度收益列表 - 提供给模板使用
    monthlyIncomeList() {
      return this.rewardSummary.monthlyRewards || [];
    },
    
    // 格式化总奖励金额
    formattedTotalRewardAmount() {
      return this.inviteStats.totalRewardAmount ? this.inviteStats.totalRewardAmount.toFixed(2) : '0.00';
    },
    
    // 格式化当前总收益
    formattedCurrentTotalIncome() {
      const amount = this.inviteStats.currentTotalIncome || 0;
      return amount.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    },
    
    // 格式化可提现金额
    formattedWithdrawableAmount() {
      const amount = this.inviteStats.withdrawableAmount || 0;
      return amount.toFixed(2);
    },
    
    // 格式化未入账金额
    formattedPendingAmount() {
      const amount = this.inviteStats.pendingAmount || 0;
      return amount.toFixed(2);
    }
  },
  
  onLoad() {
    // 初始化数据
    this.checkLoginStatus();
    this.loadUserAccountInfo();
    this.loadInviteStats();
    this.loadRewardSummary();
  },
  
  methods: {
    // 检查登录状态
    checkLoginStatus() {
      // 检查store中的登录状态
      this.hasLogin = this.hasLoginFromStore;

      // 如果store中没有登录状态，检查本地存储
      if (!this.hasLogin) {
        const tokenInfo = uni.getStorageSync('tokenInfo');
        const userInfoStorage = uni.getStorageSync('userInfo');

        if (tokenInfo && userInfoStorage) {
          try {
            const loginData = JSON.parse(tokenInfo);
            const hasValidToken = !!(loginData && loginData.token && loginData.tokenHead && loginData.openId);

            if (hasValidToken) {
              // 恢复登录状态到store
              this.$store.commit('login', userInfoStorage);
              this.hasLogin = true;
            }
          } catch (e) {
            console.error('解析tokenInfo失败:', e);
          }
        }
      }
    },

    // 获取用户账户信息
    async loadUserAccountInfo() {
      if (!this.hasLogin) {
        return;
      }

      try {
        // 优先从store获取用户信息
        const storeUserInfo = this.userInfoFromStore;
        if (storeUserInfo && Object.keys(storeUserInfo).length > 0) {
          this.updateUserAccountInfo(storeUserInfo);
        }

        // 从缓存获取用户信息
        const cachedUserInfo = uni.getStorageSync('userInfo');
        if (cachedUserInfo) {
          let userInfo = cachedUserInfo;
          if (typeof cachedUserInfo === 'string') {
            userInfo = JSON.parse(cachedUserInfo);
          }
          this.updateUserAccountInfo(userInfo);
        }

        // 从API获取最新用户信息
        const response = await memberInfo();
        if (response.code === 200 && response.data) {
          this.updateUserAccountInfo(response.data);
          // 更新store中的用户信息
          this.$store.commit('login', response.data);
        }
      } catch (error) {
        console.error('获取用户信息失败:', error);
      }
    },

    // 更新用户账户信息
    updateUserAccountInfo(userInfo) {
      this.userAccountInfo = {
        nickname: userInfo.nickname || userInfo.username || '会员用户',
        memberNo: userInfo.memberCode || userInfo.id || 'XXXXXXXX',
        avatar: userInfo.icon || userInfo.avatar || '',
        phone: userInfo.phone || '12774876975'
      };
    },

    // 加载收益数据
    loadIncomeData() {
      console.log('加载收益数据');
      // 加载当前年份的奖励汇总
      this.loadRewardSummary();
    },
    
    // 显示规则
    showRules() {
      uni.navigateTo({
        url: '/pages/user/income-rule'
      });
    },
    
    // 年份选择改变
    onYearChange(e) {
      const selectedIndex = e.detail.value;
      this.selectedYearIndex = selectedIndex;
      this.selectedYear = this.yearList[selectedIndex];
      
      // 重新加载该年份的数据
      this.loadYearIncomeData(this.selectedYear);
    },
    
    // 加载指定年份的收益数据
    loadYearIncomeData(year) {
      console.log('加载年份收益数据:', year);
      // 加载指定年份的奖励汇总
      this.loadRewardSummary(year);
    },
    
    // 显示常见问题
    showFAQ() {
      uni.showToast({
        title: '常见问题',
        icon: 'none',
        duration: 2000
      });
    },
    
    // 加载邀请统计数据
    async loadInviteStats() {
      try {
        const response = await getMyInviteStatistics();
        
        if (response.code === 200) {
          // 合并返回的数据到inviteStats，确保新字段被正确赋值
          this.inviteStats = {
            ...this.inviteStats,
            ...response.data
          };
          console.log('邀请统计数据:', this.inviteStats);
        } else {
          console.error('获取邀请统计失败:', response.message);
        }
      } catch (error) {
        console.error('获取邀请统计异常:', error);
        // 降级处理：保持默认值
      }
    },
    
    // 加载奖励汇总数据
    async loadRewardSummary(year) {
      try {
        // 如果没有指定年份，使用当前选中的年份
        const targetYear = year || this.selectedYear;
        
        const response = await getRewardSummary({ year: targetYear });
        
        if (response.code === 200) {
          this.rewardSummary = response.data;
          console.log('奖励汇总数据:', this.rewardSummary);
        } else {
          console.error('获取奖励汇总失败:', response.message);
        }
      } catch (error) {
        console.error('获取奖励汇总异常:', error);
        // 降级处理：保持默认值
      }
    },
    
    // 查看邀请记录
    viewInviteRecords() {
      uni.navigateTo({
        url: '/pages/user/invite-records'
      });
    },

    // 打开提现弹窗
    openWithdrawDialog() {
      this.withdrawType = 'withdraw';
      this.withdrawDialogVisible = true;
    },

    // 打开存入文创余额弹窗
    openBalanceDialog() {
      this.withdrawType = 'balance';
      this.withdrawDialogVisible = true;
    },

    // 关闭提现弹窗
    closeWithdrawDialog() {
      this.withdrawDialogVisible = false;
    },

    // 处理提现提交
    async handleWithdrawSubmit(data) {
      try {
        console.log('提现数据:', data);

        // 构造API请求参数
        const requestData = {
          applyAmount: data.amount,
          withdrawType: this.withdrawType === 'balance' ? 3 : 1, // 3-存入文创余额，1-微信零钱
          withdrawAccount: this.withdrawType === 'balance' ? '' : (this.userInfoFromStore.openId || this.userInfoFromStore.openid || ''),
          accountName: this.withdrawType === 'balance' ? '' : (this.userInfoFromStore.nickname || this.userInfoFromStore.username || ''),
          remark: this.withdrawType === 'balance' ? '存入文创余额' : '提现到微信零钱'
        };

        console.log('API请求参数:', requestData);

        // 使用API封装调用提现接口
        const response = await applyWithdraw(requestData);

        console.log('提现API响应:', response);

        if (response.code === 200) {
          uni.showToast({
            title: this.withdrawType === 'balance' ? '转存成功' : '提现申请已提交',
            icon: 'success'
          });

          this.closeWithdrawDialog();

          // 刷新数据
          this.loadInviteStats();
          this.loadRewardSummary();
        } else {
          throw new Error(response.message || '提现申请失败');
        }

      } catch (error) {
        console.error('提现失败:', error);
        uni.showToast({
          title: error.message || '操作失败，请重试',
          icon: 'none'
        });
      }
    }
  }
};
</script>

<style lang="scss">
@charset "UTF-8";

.income-container {
  background-color: #ffffff;
  min-height: 100vh;
  padding-bottom: 300rpx; /* 为底部固定区域留出空间 */
}

.page-title {
  font-size: 32rpx;
  font-weight: 400;
  color: #0A0D05;
}

/* 收益统计区域 */
.income-stats-section {
  position: relative;
  margin: 20rpx 32rpx 40rpx;
  height: 420rpx;
  border-radius: 16rpx;
  overflow: hidden;
  box-shadow: 0 6rpx 8rpx 2rpx rgba(43, 50, 0, 0.08);
}

.income-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.decoration-circles {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 2;
}

.circle {
  position: absolute;
  border-radius: 50%;
}

.circle-1 {
  width: 80rpx;
  height: 80rpx;
  background: rgba(169, 255, 0, 0.5);
  top: 60rpx;
  right: 100rpx;
}

.circle-2 {
  width: 60rpx;
  height: 60rpx;
  background: rgba(169, 255, 0, 0.6);
  top: 40rpx;
  right: 40rpx;
}

.circle-3 {
  width: 40rpx;
  height: 40rpx;
  background: #A9FF00;
  border: 4rpx solid #C7FE58;
  top: 120rpx;
  right: 200rpx;
}

.circle-4 {
  width: 32rpx;
  height: 32rpx;
  background: #A9FF00;
  border: 4rpx solid #C7FE58;
  top: 180rpx;
  right: 60rpx;
}

.income-content {
  position: relative;
  z-index: 3;
  padding: 40rpx 30rpx;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.income-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.income-label {
  font-size: 28rpx;
  color: #666666;
}

.income-rule {
  font-size: 28rpx;
  color: #9FA19D;
  text-decoration: underline;
}

.total-income {
  display: flex;
  align-items: flex-end;
  margin-bottom: 60rpx;
}

.currency-symbol {
  font-family: 'Alibaba PuHuiTi 3.0';
  font-weight: 700;
  font-size: 36rpx;
  color: #27243B;
  margin-right: 8rpx;
}

.income-amount {
  font-family: 'Alibaba PuHuiTi 3.0';
  font-weight: 700;
  font-size: 56rpx;
  color: #27243B;
  line-height: 1.2;
}

.income-details {
  display: flex;
  margin-top: auto;
}

.detail-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 32rpx;
  padding: 8rpx 32rpx;
}

.detail-divider {
  width: 2rpx;
  background: rgba(0, 0, 0, 0.07);
  margin: 8rpx 0;
}

.detail-label {
  font-size: 28rpx;
  color: #666666;
  text-align: center;
}

.detail-amount {
  display: flex;
  align-items: flex-end;
}

.detail-currency {
  font-family: 'Alibaba PuHuiTi 3.0';
  font-weight: 700;
  font-size: 28rpx;
  color: #27243B;
  margin-right: 8rpx;
}

.detail-value {
  font-family: 'Alibaba PuHuiTi 3.0';
  font-weight: 500;
  font-size: 40rpx;
  color: #27243B;
  line-height: 1.2;
}

/* 邀请统计区域 */
.invite-stats-section {
  margin: 0 32rpx 40rpx;
  background: #fff;
  border-radius: 16rpx;
  padding: 32rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
}

.view-more {
  font-size: 28rpx;
  color: #647D00;
}

.invite-stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24rpx;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  padding: 24rpx 16rpx;
  background: #F8F9FA;
  border-radius: 12rpx;
}

.stat-value {
  font-size: 36rpx;
  font-weight: 600;
  color: #647D00;
}

.stat-label {
  font-size: 24rpx;
  color: #666;
}

/* 记录与明细区域 */
.records-section {
  margin: 0;
}

.records-header {
  background: #F8F8F8;
  padding: 8rpx 32rpx;
  margin-bottom: 20rpx;
  width: 100%;
}

.records-title {
  font-size: 28rpx;
  color: #9FA19D;
}

.year-and-income-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 0 32rpx 20rpx;
}

.year-selector {
  flex: 1;
}

.year-picker {
  width: 100%;
}

.year-selector-content {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.year-text {
  font-size: 32rpx;
  color: #666666;
}

.dropdown-icon {
  width: 24rpx;
  height: 24rpx;
}

.month-income-summary {
  flex-shrink: 0;
}

.summary-text {
  font-size: 28rpx;
  color: #9FA19D;
}

/* 收入列表 */
.income-list {
  background: #fff;
  border-radius: 16rpx;
  overflow: hidden;
  margin: 0 32rpx;
}

.income-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx 0;
  border-bottom: 2rpx solid #EEEEEE;
  
  &:last-child {
    border-bottom: none;
  }
}

.income-item-left {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.month-label {
  font-size: 32rpx;
  color: #000;
}

.current-tag {
  font-size: 24rpx;
  color: #647D00;
}

.income-item-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4rpx;
  text-align: right;
}

.income-value {
  font-size: 32rpx;
  color: #000;
}

/* 常见问题 */
.faq-section {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 16rpx 0;
  margin: 40rpx 0;
}

.faq-text {
  font-size: 24rpx;
  color: #647D00;
}

/* 底部提现功能区域 */
.withdraw-bottom-section {
  position: fixed;
  left: 0;
  bottom: 0;
  width: 100%;
  background: #FFFFFF;
  box-shadow: 0px -1px 1px rgba(0, 0, 0, 0.05);
}

.home-indicator-wrapper {
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  align-items: center;
  padding: 42rpx 193rpx 16rpx;
}

.home-indicator {
  width: 268rpx;
  height: 10rpx;
  background: #212134;
  border-radius: 200rpx;
}

.withdraw-buttons-wrapper {
  padding: 0 32rpx 84rpx;
}

.withdraw-buttons-container {
  margin-bottom: 20rpx;
}

.withdraw-buttons {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 16rpx;
}

.withdraw-btn {
  flex: 1;
  height: 84rpx;
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  padding: 20rpx 28rpx;
  border-radius: 16rpx;
}

.withdraw-btn-dark {
  background: #20201E;
}

.withdraw-btn-gradient {
  background: linear-gradient(180deg, #A7FD03 0%, #B2F597 100%);
}

.withdraw-btn-text {
  font-family: 'PingFang SC', sans-serif;
  font-style: normal;
  font-weight: 400;
  font-size: 32rpx;
  line-height: 44rpx;
  color: #A9FF00;
}

.withdraw-btn-text-dark {
  color: #0A0D05;
}

.tax-notice {
  padding: 0 32rpx;
}

.tax-notice-text {
  font-family: 'PingFang SC', sans-serif;
  font-style: normal;
  font-weight: 400;
  font-size: 24rpx;
  line-height: 36rpx;
  color: #9FA19D;
  text-align: left;
}
</style> 