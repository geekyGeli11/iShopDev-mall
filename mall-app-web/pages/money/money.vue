<template>
  <view class="page-container">
    <!-- 导航栏 -->
    <NavBar 
			:back="true" 
			:placeholder="false"
			bg-color="transparent" 
		/>
    
    <!-- 顶部背景区域 -->
    <view class="gradient-header">
      <!-- 背景图片 -->
      <image class="bg-image" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/money/bg.png" mode="aspectFill"></image>
      <!-- 余额卡片 -->
      <view class="balance-card">
        <view class="balance-title">账户余额</view>
        <view class="balance-amount">
          <text class="currency">¥</text>
          <text class="amount">{{ formatAmount(userInfo.balance || 0) }}</text>
        </view>
        <!-- 操作链接 -->
        <view class="balance-actions">
          <text class="action-link" @tap="showHistory">充值历史</text>
          <text class="action-link" @tap="showRecords">消费记录</text>
        </view>
      </view>
    </view>
    
    <!-- 主要内容区域 -->
    <view class="content-section">
      <!-- 选项卡 -->
      <view class="tab-section">
        <view 
          class="tab-item" 
          :class="{'active': activeTab === 'amount'}"
          @tap="switchTab('amount')"
        >
          <text class="tab-text">余额充值</text>
        </view>
        <view 
          class="tab-item" 
          :class="{'active': activeTab === 'points'}"
          @tap="switchTab('points')"
        >
          <text class="tab-text">购物卡充值</text>
        </view>
      </view>
      
      <!-- 金额充值内容 -->
      <view v-if="activeTab === 'amount'" class="recharge-content">
        <!-- 快速金额选择 -->
        <view class="quick-amounts">
          <view 
            v-for="(option, index) in rechargeConfig.quickAmounts" 
            :key="index"
            class="amount-item" 
            :class="{'selected': rechargeAmount == option.amount}"
            @tap="selectAmount(option.amount, option)"
          >
            <view class="amount-display">
              <text class="currency">¥</text>
              <text class="amount-text">{{ option.amount }}</text>
            </view>
          </view>
          <!-- 自定义金额 -->
          <view 
            class="amount-item custom-amount" 
            :class="{'selected': customAmountSelected}"
            @tap="selectCustomAmount"
          >
            <text class="amount-text">自定义金额</text>
          </view>
        </view>
        
        <!-- 自定义金额输入 -->
        <view v-if="customAmountSelected" class="custom-input-section">
          <view class="custom-input-wrapper">
            <text class="currency-symbol">¥</text>
            <input 
              class="custom-input" 
              type="digit" 
              v-model="customAmount" 
              placeholder="请输入金额"
              placeholder-style="color: #9FA19D; font-size: 48rpx; font-weight: 400;"
            />
          </view>
        </view>
      </view>
      
      <!-- 积分卡充值内容 -->
      <view v-else class="points-content">
        <view class="empty-state">
          <text class="empty-text">购物卡充值功能开发中...</text>
        </view>
      </view>
      
      <!-- 限时赠送 -->
      <view v-if="selectedBonusInfo.hasBonus" class="gift-section">
        <view class="gift-title">限时赠送</view>
        <view class="gift-items">
          <view v-if="selectedBonusInfo.bonusIntegration > 0" class="gift-item">
            <view class="gift-icon">
              <image src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/money/point.png" mode="aspectFit"></image>
            </view>
            <text class="gift-text">{{ selectedBonusInfo.bonusIntegration }} 积分</text>
          </view>
          
          <view v-if="selectedBonusInfo.bonusBalance > 0" class="gift-item">
            <view class="gift-icon">
              <image src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/money/meney.png" mode="aspectFit"></image>
            </view>
            <text class="gift-text">￥{{ selectedBonusInfo.bonusBalance }} 余额</text>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 底部操作区域 -->
    <view class="bottom-section">
      <view class="bottom-content">
        <view class="total-info">
          <text class="total-label">合计:¥{{ finalAmount }}</text>
        </view>
        <button class="recharge-btn" @tap="confirmRecharge">充值</button>
      </view>
    </view>

    <!-- 历史记录弹窗 -->
    <view v-if="showHistoryModal" class="history-modal" @tap="closeHistoryModal">
      <view class="history-modal-content" @tap.stop="">
        <!-- 弹窗头部 -->
        <view class="history-header">
          <view class="history-title">{{ historyTitle }}</view>
          <view class="history-close" @tap="closeHistoryModal">
            <text class="history-close-icon">×</text>
          </view>
        </view>
        
        <!-- 历史记录列表 -->
        <scroll-view 
          class="history-scroll" 
          scroll-y
          @scrolltolower="onHistoryScrollToLower"
        >
          <view v-if="historyLoading && historyList.length === 0" class="history-loading">
            <text class="loading-text">加载中...</text>
          </view>
          
          <view v-else-if="historyList.length === 0" class="history-empty">
            <text class="empty-text">暂无记录</text>
          </view>
          
          <view v-else class="history-list">
            <view 
              v-for="(item, index) in historyList" 
              :key="index" 
              class="history-item"
            >
              <view class="history-item-content">
                <view class="history-item-info">
                  <view class="history-item-title">{{ formatHistoryRecord(item).businessTypeText }}</view>
                  <view class="history-item-time">{{ formatHistoryRecord(item).createTimeText }}</view>
                  <view v-if="item.remark" class="history-item-remark">{{ item.remark }}</view>
                </view>
                <view class="history-item-amount">
                  <text 
                    class="amount-value" 
                    :class="{'positive': formatHistoryRecord(item).isPositive, 'negative': !formatHistoryRecord(item).isPositive}"
                  >
                    {{ formatHistoryRecord(item).isPositive ? '+' : '-' }}¥{{ formatHistoryRecord(item).amountText }}
                  </text>
                </view>
              </view>
            </view>
          </view>
          
          <!-- 加载更多提示 -->
          <view v-if="historyLoading && historyList.length > 0" class="history-load-more">
            <text class="load-more-text">加载中...</text>
          </view>
        </scroll-view>
      </view>
    </view>
  </view>
</template>

<script>
import { getWalletInfo, createRechargeOrder, formatAmount, getRechargeConfig as fetchRechargeConfig, rechargePaySuccess, getRechargeHistory, getConsumeHistory } from '@/api/wallet';
import NavBar from '@/components/nav-bar.vue';

export default {
  components: {
    NavBar
  },
  data() {
    return {
      userInfo: {
        memberId: null,
        balance: 0.00,
        totalRechargeAmount: 0.00,
        totalConsumeAmount: 0.00,
        todayRechargeAmount: 0.00,
        todayConsumeAmount: 0.00,
        canRecharge: true,
        canConsume: true
      },
      activeTab: 'amount', // 当前选中的选项卡
      rechargeAmount: '', // 当前选中的充值金额
      customAmount: '', // 自定义金额
      customAmountSelected: false, // 是否选中自定义金额
      selectedOption: null, // 当前选中的快速金额选项
      rechargeConfig: {
        minAmount: 1,
        maxAmount: 50000,
        quickAmounts: [],
        enableBonus: false,
        bonusBalanceRate: 0,
        bonusIntegrationRate: 0,
        bonusMinAmount: 0
      },
      // 弹窗相关
      showHistoryModal: false, // 是否显示历史记录弹窗
      historyType: '', // 历史记录类型：'recharge' 或 'consume'
      historyTitle: '', // 弹窗标题
      historyList: [], // 历史记录列表
      historyLoading: false, // 历史记录加载状态
      historyPage: 1, // 当前页码
      historyTotal: 0 // 总记录数
    };
  },
  
  onLoad() {
    // 页面加载时获取数据
    this.getUserInfo();
    this.getRechargeConfig();
  },
  
  onShow() {
    // 页面显示时不重复调用接口，避免重复请求
    // 如果需要刷新数据，用户可以手动下拉刷新
  },
  
  onPullDownRefresh() {
    // 下拉刷新
    this.refreshData();
  },
  
  computed: {
    // 计算最终金额
    finalAmount() {
      if (this.customAmountSelected && this.customAmount) {
        return this.customAmount;
      }
      return this.rechargeAmount || '0';
    },
    
    // 计算当前选中金额的赠送信息
    selectedBonusInfo() {
      if (this.customAmountSelected) {
        // 自定义金额没有赠送
        return {
          hasBonus: false,
          bonusBalance: 0,
          bonusIntegration: 0
        };
      }
      
      if (this.selectedOption) {
        return {
          hasBonus: this.selectedOption.bonusBalance > 0 || this.selectedOption.bonusIntegration > 0,
          bonusBalance: this.selectedOption.bonusBalance || 0,
          bonusIntegration: this.selectedOption.bonusIntegration || 0
        };
      }
      
      return {
        hasBonus: false,
        bonusBalance: 0,
        bonusIntegration: 0
      };
    }
  },
  
  methods: {
    // 切换选项卡
    switchTab(tab) {
      this.activeTab = tab;
      // 切换选项卡时重置选择
      this.rechargeAmount = '';
      this.customAmount = '';
      this.customAmountSelected = false;
      this.selectedOption = null;
    },
    
    // 显示充值历史
    showHistory() {
      this.historyType = 'recharge';
      this.historyTitle = '充值历史';
      this.showHistoryModal = true;
      this.loadHistoryData();
    },
    
    // 显示消费记录
    showRecords() {
      this.historyType = 'consume';
      this.historyTitle = '消费记录';
      this.showHistoryModal = true;
      this.loadHistoryData();
    },
    
    // 加载历史记录数据
    loadHistoryData(loadMore = false) {
      if (!loadMore) {
        this.historyPage = 1;
        this.historyList = [];
      }
      
      this.historyLoading = true;
      
      const apiFunc = this.historyType === 'recharge' ? getRechargeHistory : getConsumeHistory;
      const params = {
        pageNum: this.historyPage,
        pageSize: 20
      };
      
      apiFunc(params).then(res => {
        if (res.code === 200) {
          const newList = res.data || [];
          
          if (loadMore) {
            this.historyList = [...this.historyList, ...newList];
          } else {
            this.historyList = newList;
          }
          
          // 如果返回的记录数小于页大小，说明没有更多数据了
          if (newList.length < params.pageSize) {
            this.historyTotal = this.historyList.length;
          } else {
            this.historyPage++;
          }
        } else {
          uni.showToast({
            title: res.message || '获取记录失败',
            icon: 'none'
          });
        }
      }).catch(err => {
        console.error('获取历史记录失败', err);
        uni.showToast({
          title: '获取记录失败',
          icon: 'none'
        });
      }).finally(() => {
        this.historyLoading = false;
      });
    },

    // 关闭历史记录弹窗
    closeHistoryModal() {
      this.showHistoryModal = false;
      this.historyList = [];
      this.historyPage = 1;
    },

    // 滚动到底部加载更多
    onHistoryScrollToLower() {
      if (!this.historyLoading && this.historyList.length > 0) {
        this.loadHistoryData(true);
      }
    },

    // 格式化历史记录显示
    formatHistoryRecord(record) {
      const changeTypeMap = {
        1: '充值',
        2: '消费',
        3: '退款',
        4: '转入',
        5: '转出'
      };
      
      const businessTypeMap = {
        'recharge': '余额充值',
        'recharge_bonus': '充值奖励',
        'order': '订单支付',
        'refund': '订单退款',
        'transfer_in': '转账收入',
        'transfer_out': '转账支出'
      };
      
      return {
        ...record,
        changeTypeText: changeTypeMap[record.changeType] || '未知',
        businessTypeText: businessTypeMap[record.businessType] || record.businessType || '其他',
        amountText: this.formatAmount(Math.abs(record.amount)),
        isPositive: record.amount > 0,
        createTimeText: this.formatDateTime(record.createTime)
      };
    },

    // 格式化日期时间
    formatDateTime(dateTime) {
      if (!dateTime) return '';
      
      const date = new Date(dateTime);
      const now = new Date();
      const diffTime = now.getTime() - date.getTime();
      const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));
      
      if (diffDays === 0) {
        return '今天 ' + date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
      } else if (diffDays === 1) {
        return '昨天 ' + date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
      } else if (diffDays < 7) {
        return diffDays + '天前';
      } else {
        return date.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' }) + ' ' + 
               date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
      }
    },
    
    // 格式化金额显示
    formatAmount(amount) {
      return formatAmount(amount);
    },
    
    // 刷新页面数据
    refreshData() {
      Promise.all([
        this.getUserInfo(),
        this.getRechargeConfig()
      ]).finally(() => {
        // 停止下拉刷新动画
        uni.stopPullDownRefresh();
      });
    },
    
    // 获取用户钱包信息
    getUserInfo() {
      return getWalletInfo().then(res => {
        if (res.code === 200) {
          this.userInfo = res.data || {
            memberId: null,
            balance: 0.00,
            totalRechargeAmount: 0.00,
            totalConsumeAmount: 0.00,
            todayRechargeAmount: 0.00,
            todayConsumeAmount: 0.00,
            canRecharge: true,
            canConsume: true
          };
        } else {
          uni.showToast({
            title: res.message || '获取余额信息失败',
            icon: 'none'
          });
        }
      }).catch(err => {
        console.error('获取余额信息失败', err);
        uni.showToast({
          title: '获取余额信息失败',
          icon: 'none'
        });
      });
    },
    
    // 获取充值配置
    getRechargeConfig() {
      return fetchRechargeConfig().then(res => {
        if (res.code === 200) {
          this.rechargeConfig = res.data || {
            minAmount: 1,
            maxAmount: 50000,
            quickAmounts: [],
            enableBonus: false,
            bonusBalanceRate: 0,
            bonusIntegrationRate: 0,
            bonusMinAmount: 0
          };
          
          // 如果有快速金额选项，默认选中第一个
          if (this.rechargeConfig.quickAmounts && this.rechargeConfig.quickAmounts.length > 0) {
            const firstOption = this.rechargeConfig.quickAmounts[0];
            this.rechargeAmount = firstOption.amount;
            this.selectedOption = firstOption;
          }
        } else {
          console.error('获取充值配置失败', res.message);
        }
      }).catch(err => {
        console.error('获取充值配置失败', err);
      });
    },
    
    // 选择快速充值金额
    selectAmount(amount, option) {
      this.rechargeAmount = amount;
      this.selectedOption = option;
      this.customAmountSelected = false;
      this.customAmount = '';
    },
    
    // 选择自定义金额
    selectCustomAmount() {
      this.customAmountSelected = true;
      this.rechargeAmount = '';
      this.selectedOption = null;
    },
    
    // 确认充值
    confirmRecharge() {
      const amount = this.customAmountSelected ? this.customAmount : this.rechargeAmount;
      
      if (!amount) {
        return uni.showToast({
          title: '请选择或输入充值金额',
          icon: 'none'
        });
      }
      
      const numAmount = parseFloat(amount);
      
      if (isNaN(numAmount) || numAmount <= 0) {
        return uni.showToast({
          title: '请输入有效的充值金额',
          icon: 'none'
        });
      }
      
      // 验证金额范围
      if (numAmount < this.rechargeConfig.minAmount) {
        return uni.showToast({
          title: `充值金额不能小于${this.rechargeConfig.minAmount}元`,
          icon: 'none'
        });
      }
      
      if (numAmount > this.rechargeConfig.maxAmount) {
        return uni.showToast({
          title: `充值金额不能大于${this.rechargeConfig.maxAmount}元`,
          icon: 'none'
        });
      }
      
      if (!this.userInfo.canRecharge) {
        return uni.showToast({
          title: '当前账户不支持充值',
          icon: 'none'
        });
      }
      
      uni.showLoading({
        title: '创建订单中...'
      });
      
      // 1. 创建充值订单
      createRechargeOrder({
        amount: amount,
        payType: 2, // 微信支付
        note: `账户充值${amount}元`
      }).then(res => {
        if (res.code === 200) {
          const orderData = res.data;
          const payParams = orderData.payParams;
          
          if (!payParams) {
            uni.hideLoading();
            return uni.showToast({
              title: '获取支付参数失败',
              icon: 'none'
            });
          }
          
          uni.hideLoading();
          
          // 2. 发起微信支付
          this.wxPay(orderData, payParams);
        } else {
          uni.hideLoading();
          
          // 检查是否是openid相关错误
          if (res.message && res.message.includes('openid')) {
            uni.showModal({
              title: '授权提示',
              content: '请重新授权登录后再试',
              confirmText: '重新登录',
              cancelText: '取消',
              success: (modalRes) => {
                if (modalRes.confirm) {
                  // 跳转到登录页面或重新授权
                  uni.reLaunch({
                    url: '/pages/login/login'
                  });
                }
              }
            });
          } else {
            uni.showToast({
              title: res.message || '创建订单失败',
              icon: 'none'
            });
          }
        }
      }).catch(err => {
        uni.hideLoading();
        console.error('创建订单失败', err);
        
        // 检查错误消息是否包含openid
        const errorMessage = err.message || err.toString() || '';
        if (errorMessage.includes('openid')) {
          uni.showModal({
            title: '授权提示',
            content: '请重新授权登录后再试',
            confirmText: '重新登录',
            cancelText: '取消',
            success: (modalRes) => {
              if (modalRes.confirm) {
                uni.reLaunch({
                  url: '/pages/login/login'
                });
              }
            }
          });
        } else {
          uni.showToast({
            title: '创建订单失败，请稍后再试',
            icon: 'none'
          });
        }
      });
    },
    
    // 发起微信支付
    wxPay(orderData, payParams) {
      uni.showLoading({
        title: '正在调起支付...'
      });
      
      uni.requestPayment({
        provider: 'wxpay',
        timeStamp: payParams.timeStamp || payParams.timestamp,
        nonceStr: payParams.nonceStr,
        package: payParams.packageValue || payParams.package,
        signType: payParams.signType || 'MD5',
        paySign: payParams.paySign,
        success: (res) => {
          console.log('微信支付成功', res);
          uni.hideLoading();
          
          // 3. 调用支付成功接口
          this.handlePaySuccess(orderData);
        },
        fail: (err) => {
          console.error('微信支付失败', err);
          uni.hideLoading();
          
          if (err.errMsg && err.errMsg.includes('cancel')) {
            uni.showToast({
              title: '支付已取消',
              icon: 'none'
            });
          } else {
            uni.showToast({
              title: '支付失败，请重试',
              icon: 'none'
            });
          }
        }
      });
    },
    
    // 处理支付成功
    handlePaySuccess(orderData) {
      uni.showLoading({
        title: '处理支付结果...'
      });
      
      rechargePaySuccess({
        orderSn: orderData.orderSn,
        payType: 2, // 微信支付
        paySn: 'WX_' + Date.now() // 微信支付流水号（实际应该从微信回调中获取）
      }).then(res => {
        uni.hideLoading();
        
        if (res.code === 200) {
          // 支付成功，刷新余额
          this.getUserInfo();
          
          uni.showToast({
            title: '充值成功',
            icon: 'success'
          });
          
          // 清空输入，重新选择第一个快速金额
          if (this.rechargeConfig.quickAmounts && this.rechargeConfig.quickAmounts.length > 0) {
            const firstOption = this.rechargeConfig.quickAmounts[0];
            this.rechargeAmount = firstOption.amount;
            this.selectedOption = firstOption;
          }
          this.customAmount = '';
          this.customAmountSelected = false;
        } else {
          uni.showToast({
            title: res.message || '充值处理失败',
            icon: 'none'
          });
        }
      }).catch(err => {
        uni.hideLoading();
        console.error('处理支付结果失败', err);
        
        uni.showToast({
          title: '充值处理失败，请联系客服',
          icon: 'none'
        });
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.page-container {
  min-height: 100vh;
  background-color: #FFFFFF;
}

// 顶部背景
.gradient-header {
  height: 624rpx;
  padding: 40rpx 30rpx 60rpx;
  position: relative;
  overflow: hidden;
}

// 背景图片
.bg-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

// 余额卡片
.balance-card {
  background: #fff;
  height: 360rpx;
  border-radius: 24rpx;
  padding: 40rpx 30rpx;
  margin: 0 20rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.08);
  position: absolute;
  left: 0;
  right: 0;
  bottom: 30rpx;
  z-index: 2;
  
  .balance-title {
    font-size: 28rpx;
    color: #666;
    margin-bottom: 16rpx;
    text-align: center;
  }
  
  .balance-amount {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 28rpx;
    
    .currency {
      font-size: 32rpx;
      color: #333;
      margin-right: 8rpx;
      margin-top: 8rpx;
    }
    
    .amount {
      font-size: 52rpx;
      font-weight: 700;
      color: #333;
    }
  }
  
  .balance-actions {
    display: flex;
    justify-content: center;
    gap: 40rpx;
    width: 472rpx;
    margin: 0 auto;
    
    .action-link {
      display: flex;
      width: 220rpx;
      flex-direction: row;
      justify-content: center;
      align-items: center;
      padding: 16rpx 24rpx;
      border: 1rpx solid #EEEEEE;
      border-radius: 16rpx;
      font-size: 28rpx;
      color: #666666;
      text-decoration: none;
      font-weight: 400;
      line-height: 40rpx;
    }
  }
}

// 主要内容区域
.content-section {
  margin-top: 80rpx;
  padding: 0 30rpx;
}

// 选项卡
.tab-section {
  display: flex;
  margin-bottom: 32rpx;
  width: fit-content;
  
  .tab-item {
    padding: 16rpx 0;
    margin-right: 60rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    background: transparent;
    position: relative;
    
    &.active {
      .tab-text {
        color: #333;
        font-weight: 600;
      }
      
      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        height: 6rpx;
        background: #A9FF00;
        border-radius: 3rpx;
      }
    }
    
    .tab-text {
      font-size: 32rpx;
      color: #999;
      font-weight: 400;
    }
  }
}

// 充值内容
.recharge-content {
  .quick-amounts {
    display: grid;
    grid-template-columns: 1fr 1fr 1fr;
    gap: 20rpx;
    margin-bottom: 32rpx;
    
    .amount-item {
      background: #fff;
      border-radius: 16rpx;
      width: 220rpx;
      height: 220rpx;
      padding: 0;
      text-align: center;
      position: relative;
      border: 2rpx solid #e5e5e5;
      box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      
      &.selected {
        border-color: #A9FF00;
        background: rgba(169, 255, 0, 0.1);
        
        &::after {
          content: '✓';
          position: absolute;
          top: -10rpx;
          right: -10rpx;
          width: 36rpx;
          height: 36rpx;
          background: #A9FF00;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          color: #333;
          font-size: 20rpx;
          font-weight: bold;
          box-shadow: 0 2rpx 8rpx rgba(169, 255, 0, 0.3);
        }
        
        .currency,
        .amount-text {
          color: #27243B;
          font-weight: 700;
        }
      }
      
      .amount-display {
        display: flex;
        align-items: center;
        justify-content: center;
        flex-direction: row;
        gap: 8rpx;
      }
      
      .currency {
        font-size: 36rpx;
        color: #333;
        font-weight: 700;
        line-height: 1;
      }
      
      .amount-text {
        font-size: 48rpx;
        color: #333;
        font-weight: 700;
        line-height: 1;
      }
      

    }
    
    .custom-amount {
      .amount-text {
        font-size: 28rpx;
        color: #333;
        font-weight: 600;
        line-height: 1.2;
      }
      
      &.selected .amount-text {
        color: #27243B;
        font-weight: 700;
      }
    }
  }
}

// 自定义金额输入
.custom-input-section {
  margin-bottom: 32rpx;
  
  .custom-input-wrapper {
    display: flex;
    flex-direction: row;
    align-items: center;
    padding: 32rpx 0;
    gap: 16rpx;
    width: 686rpx;
    height: 132rpx;
    border-bottom: 2rpx solid #EEEEEE;
    
    .currency-symbol {
      width: 48rpx;
      height: 68rpx;
      font-size: 48rpx;
      font-weight: 600;
      line-height: 68rpx;
      color: #0A0D05;
      display: flex;
      align-items: center;
    }
    
    .custom-input {
      flex: 1;
      height: 68rpx;
      font-size: 48rpx;
      font-weight: 400;
      line-height: 68rpx;
      color: #0A0D05;
      background: transparent;
      border: none;
      padding: 0;
      
      &::after {
        border: none;
      }
    }
  }
}

// 积分卡充值内容
.points-content {
  .empty-state {
    text-align: center;
    padding: 80rpx 0;
    
    .empty-text {
      font-size: 28rpx;
      color: #999;
    }
  }
}

// 限时赠送
.gift-section {
  margin-top: 48rpx;
  margin-bottom: 140rpx;
  
  .gift-title {
    font-size: 28rpx;
    color: #333;
    margin-bottom: 28rpx;
    font-weight: 600;
  }
  
  .gift-items {
    display: flex;
    flex-direction: column;
    gap: 20rpx;
    
    .gift-item {
      border: 1px solid #EEEEEE;
      display: flex;
      align-items: center;
      background: #fff;
      padding: 24rpx 32rpx;
      border-radius: 16rpx;
      box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
      
      .gift-icon {
        width: 80rpx;
        height: 80rpx;
        margin-right: 20rpx;
        background: #f0fdf4;
        border-radius: 12rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        
        image {
          width: 80rpx;
          height: 80rpx;
        }
      }
      
      .gift-text {
        font-size: 28rpx;
        color: #000;
        font-weight: 500;
      }
    }
  }
}

// 底部操作区
.bottom-section {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 32rpx;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.08);
  z-index: 100;
  
  .bottom-content {
    display: flex;
    align-items: center;
    gap: 24rpx;
  }
  
  .total-info {
    display: flex;
    align-items: center;
    
    .total-label {
      font-size: 32rpx;
      color: #000000;
      font-weight: 400;
    }
  }
  
  .recharge-btn {
    background: #20201E;
    color: #A9FF00;
    border-radius: 16rpx;
    height: 84rpx;
    width: 252rpx;
    line-height: 84rpx;
    padding: 20rpx 28rpx;
    font-size: 32rpx;
    font-weight: 400;
    border: none;
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    
    &::after {
      border: none;
    }
  }
}

// 历史记录弹窗
.history-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  z-index: 1000;
  
  .history-modal-content {
    background: #fff;
    border-radius: 24rpx 24rpx 0 0;
    padding: 0;
    width: 100%;
    max-height: 80vh;
    display: flex;
    flex-direction: column;
    
    .history-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 32rpx 40rpx 24rpx;
      border-bottom: 1px solid #EEEEEE;
      
      .history-title {
        font-size: 32rpx;
        color: #333;
        font-weight: 600;
      }
      
      .history-close {
        width: 48rpx;
        height: 48rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 50%;
        background: #F5F5F5;
        
        .history-close-icon {
          font-size: 36rpx;
          color: #666;
          line-height: 1;
        }
      }
    }
    
    .history-scroll {
      flex: 1;
      overflow-y: auto;
      padding: 0 40rpx 40rpx;
      
      .history-loading {
        text-align: center;
        padding: 80rpx 0;
        
        .loading-text {
          font-size: 28rpx;
          color: #999;
        }
      }
      
      .history-empty {
        text-align: center;
        padding: 80rpx 0;
        
        .empty-text {
          font-size: 28rpx;
          color: #999;
        }
      }
      
      .history-list {
        .history-item {
          padding: 32rpx 0;
          border-bottom: 1px solid #F5F5F5;
          
          &:last-child {
            border-bottom: none;
          }
          
          .history-item-content {
            display: flex;
            align-items: flex-start;
            justify-content: space-between;
            
            .history-item-info {
              flex: 1;
              
              .history-item-title {
                font-size: 30rpx;
                color: #333;
                font-weight: 500;
                margin-bottom: 8rpx;
              }
              
              .history-item-time {
                font-size: 24rpx;
                color: #999;
                margin-bottom: 4rpx;
              }
              
              .history-item-remark {
                font-size: 24rpx;
                color: #666;
                line-height: 1.4;
              }
            }
            
            .history-item-amount {
              margin-left: 20rpx;
              
              .amount-value {
                font-size: 30rpx;
                font-weight: 600;
                
                &.positive {
                  color: #00B300;
                }
                
                &.negative {
                  color: #FF4500;
                }
              }
            }
          }
        }
      }
      
      .history-load-more {
        text-align: center;
        padding: 40rpx 0;
        
        .load-more-text {
          font-size: 24rpx;
          color: #999;
        }
      }
    }
  }
}
</style> 