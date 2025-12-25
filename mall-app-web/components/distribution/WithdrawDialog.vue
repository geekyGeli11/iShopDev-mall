<template>
  <view v-if="visible" class="withdraw-dialog">
    <!-- 遮罩层 -->
    <view class="dialog-mask" @tap="handleMaskTap"></view>

    <!-- 弹窗内容 -->
    <view class="dialog-content">
      <!-- 弹窗头部 -->
      <view class="dialog-header">
        <text class="dialog-title">{{ dialogTitle }}</text>
        <view class="close-btn" @tap="handleClose">
          <text class="close-icon">✕</text>
        </view>
      </view>

      <!-- 表单内容 -->
      <view class="dialog-body">
        <!-- 到账账号信息 -->
        <view class="account-section">
          <text class="section-title">{{ withdrawType === 'balance' ? '到账会员账号' : '到账账号' }}</text>

          <!-- 文创余额账户 -->
          <view class="account-item" v-if="withdrawType === 'balance'">
            <image class="account-avatar" :src="accountInfo.avatar || defaultAvatar"></image>
            <view class="account-info">
              <text class="account-name">会员号{{ accountInfo.memberNo || 'XXXXXXXX' }}</text>
              <text class="account-phone">{{ accountInfo.phone || '12774876975' }}</text>
            </view>
          </view>

          <!-- 微信零钱账户 -->
          <view class="account-item" v-else>
            <image class="wechat-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/wechat-icon.png"></image>
            <view class="account-info">
              <text class="account-name">微信零钱</text>
              <text class="account-desc">2小时内到账</text>
            </view>
          </view>
        </view>

        <!-- 提现金额 -->
        <view class="amount-section">
          <text class="section-title">提现金额</text>
          <view class="amount-input-wrapper">
            <text class="currency-symbol">¥</text>
            <input
              class="amount-input"
              :class="{ error: errors.amount }"
              v-model="formData.amount"
              type="digit"
              placeholder="请输入金额"
              @input="onAmountInput"
              @blur="validateAmount"
            />
          </view>
          <text v-if="errors.amount" class="error-text">{{ errors.amount }}</text>

          <!-- 费率和实际到账（仅微信零钱显示） -->
          <view class="fee-info" v-if="withdrawType === 'withdraw' && formData.amount">
            <view class="fee-row">
              <text class="fee-label">费率</text>
              <text class="fee-value">20%</text>
            </view>
            <view class="fee-row">
              <text class="fee-label">实际到账金额</text>
              <text class="fee-value">¥ {{ actualAmount }}</text>
            </view>
          </view>
        </view>

        <!-- 说明文字 -->
        <view class="notice-section">
          <text class="notice-title">{{ noticeTitle }}</text>
          <view class="notice-content">
            <text v-for="(item, index) in noticeItems" :key="index" class="notice-item">{{ index + 1 }}. {{ item }}</text>
          </view>
        </view>
      </view>

      <!-- 提交按钮 -->
      <view class="dialog-footer">
        <button class="cancel-btn" @tap="handleClose">取消</button>
        <button
          class="submit-btn"
          :class="{ disabled: !canSubmit || submitting }"
          @tap="handleSubmit"
          :disabled="!canSubmit || submitting"
        >
          {{ submitting ? '提交中...' : (withdrawType === 'balance' ? '确认转存' : '确认提现') }}
        </button>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'WithdrawDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    availableAmount: {
      type: Number,
      default: 0
    },
    // 提现类型：'withdraw' 提现到微信零钱，'balance' 存入文创余额
    withdrawType: {
      type: String,
      default: 'withdraw'
    },
    // 用户账户信息（当withdrawType为'balance'时显示）
    accountInfo: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      submitting: false,
      formData: {
        amount: ''
      },
      errors: {},
      defaultAvatar: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/default-avatar.png',
      withdrawConfig: {
        minAmount: 10,
        wechatFeeRate: 0.02,
        balanceFeeRate: 0
      }
    };
  },
  computed: {
    dialogTitle() {
      return this.withdrawType === 'balance' ? '转存到文创余额' : '提现到微信零钱';
    },

    // 计算实际到账金额（根据配置的手续费率）
    actualAmount() {
      if (!this.formData.amount) {
        return '0';
      }
      const amount = parseFloat(this.formData.amount) || 0;

      if (this.withdrawType === 'balance') {
        // 存入余额无手续费
        return amount.toFixed(2);
      } else {
        // 微信提现按配置的费率计算
        const feeRate = this.withdrawConfig.wechatFeeRate || 0.02;
        const actual = amount * (1 - feeRate);
        return actual.toFixed(2);
      }
    },

    // 说明标题
    noticeTitle() {
      return this.withdrawType === 'balance' ? '转存说明' : '提现说明';
    },

    // 说明内容
    noticeItems() {
      const minAmount = this.withdrawConfig.minAmount || 10;
      const wechatFeeRate = (this.withdrawConfig.wechatFeeRate || 0.02) * 100;

      if (this.withdrawType === 'balance') {
        return [
          `每月支持转存1次，单次转存金额不少于${minAmount}元，转存支付无手续费，转存后可用于平台内消费`,
          '用户在平台内消费时，当月收入会计入当月单笔可提现额度且无法提现',
          '转存到账户余额后，可支持线下消费'
        ];
      } else {
        return [
          `提现到微信零钱，手续费${wechatFeeRate}%，提现后1-3个工作日到账`,
          `单笔提现金额不少于${minAmount}元，每月最多提现3次`,
          '提现金额将从您的可提现余额中扣除'
        ];
      }
    },

    // 是否可以提交
    canSubmit() {
      return this.formData.amount && !this.errors.amount && parseFloat(this.formData.amount) > 0;
    }
  },
  methods: {
    // 加载提现配置
    async loadWithdrawConfig() {
      try {
        const { getWithdrawConfig } = await import('@/api/invite');
        const response = await getWithdrawConfig();
        if (response.code === 200 && response.data) {
          this.withdrawConfig = {
            minAmount: response.data.minAmount || 10,
            wechatFeeRate: response.data.wechatFeeRate || 0.02,
            balanceFeeRate: response.data.balanceFeeRate || 0
          };
        }
      } catch (error) {
        console.error('加载提现配置失败:', error);
        // 使用默认配置
      }
    },

    // 金额输入处理
    onAmountInput() {
      this.validateAmount();
    },

    // 验证金额
    validateAmount() {
      const amount = parseFloat(this.formData.amount);

      if (!this.formData.amount) {
        this.errors.amount = '请输入提现金额';
        return false;
      }

      if (isNaN(amount) || amount <= 0) {
        this.errors.amount = '请输入有效的金额';
        return false;
      }

      const minAmount = this.withdrawConfig.minAmount || 10;
      if (amount < minAmount) {
        this.errors.amount = `最小${this.withdrawType === 'balance' ? '转存' : '提现'}金额为${minAmount}元`;
        return false;
      }

      if (amount > this.availableAmount) {
        this.errors.amount = '金额不能超过可提现余额';
        return false;
      }

      this.errors.amount = '';
      return true;
    },

    // 遮罩点击
    handleMaskTap() {
      // 不允许点击遮罩关闭
    },

    // 关闭弹窗
    handleClose() {
      this.$emit('close');
    },

    // 提交申请
    async handleSubmit() {
      if (!this.validateAmount()) {
        uni.showToast({
          title: '请检查输入信息',
          icon: 'none'
        });
        return;
      }

      // 确认提现
      const actionText = this.withdrawType === 'balance' ? '转存' : '提现';
      const confirmResult = await new Promise((resolve) => {
        uni.showModal({
          title: `确认${actionText}`,
          content: `确认申请${actionText} ¥${this.formData.amount}？`,
          success: (res) => resolve(res.confirm),
          fail: () => resolve(false)
        });
      });

      if (!confirmResult) return;

      try {
        this.submitting = true;

        // 构造提交数据
        const submitData = {
          amount: parseFloat(this.formData.amount),
          withdrawMethod: 'wechat' // 固定为微信
        };

        // 发送提交事件
        this.$emit('submit', submitData);

      } catch (error) {
        console.error('提交申请失败:', error);
        uni.showToast({
          title: '提交失败，请重试',
          icon: 'none'
        });
      } finally {
        this.submitting = false;
      }
    }
  },
  created() {
    // 加载提现配置
    this.loadWithdrawConfig();
  }
}
</script>

<style lang="scss" scoped>
.withdraw-dialog {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}

.dialog-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
}

.dialog-content {
  position: relative;
  width: 100%;
  height: 80vh;
  max-height: 80vh;
  background-color: #ffffff;
  border-radius: 32rpx 32rpx 0 0;
  display: flex;
  flex-direction: column;
  padding: 0 32rpx 0;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.dialog-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}

.dialog-title {
  font-size: 36rpx;
  font-weight: 600;
  color: #333333;
}

.close-btn {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-icon {
  font-size: 32rpx;
  color: #999999;
}

.dialog-body {
  flex: 1;
  padding: 32rpx 0;
  overflow-y: auto;
  max-height: calc(80vh - 200rpx); /* 减去header和footer的高度 */
}

.account-section {
  margin-bottom: 48rpx;
}

.section-title {
  font-size: 28rpx;
  color: #666666;
  margin-bottom: 24rpx;
  display: block;
}

.account-item {
  display: flex;
  align-items: center;
  padding: 24rpx;
  background-color: #f8f9fa;
  border-radius: 16rpx;
}

.account-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  margin-right: 24rpx;
}

.wechat-icon {
  width: 80rpx;
  height: 80rpx;
  margin-right: 24rpx;
}

.account-info {
  flex: 1;
}

.account-name {
  font-size: 32rpx;
  color: #333333;
  font-weight: 500;
  display: block;
  margin-bottom: 8rpx;
}

.account-phone {
  font-size: 28rpx;
  color: #999999;
  display: block;
}

.account-desc {
  font-size: 28rpx;
  color: #ff9500;
  display: block;
}

.amount-section {
  margin-bottom: 48rpx;
}

.amount-input-wrapper {
  display: flex;
  align-items: center;
  border: 2rpx solid #e5e5e5;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-top: 16rpx;
}

.currency-symbol {
  font-size: 36rpx;
  color: #333333;
  margin-right: 16rpx;
}

.amount-input {
  flex: 1;
  font-size: 36rpx;
  color: #333333;
  border: none;
  outline: none;
}

.amount-input.error {
  border-color: #ff4757;
}

.error-text {
  font-size: 24rpx;
  color: #ff4757;
  margin-top: 16rpx;
  display: block;
}

.fee-info {
  margin-top: 24rpx;
  padding: 24rpx;
  background-color: #f8f9fa;
  border-radius: 16rpx;
}

.fee-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.fee-row:last-child {
  margin-bottom: 0;
}

.fee-label {
  font-size: 28rpx;
  color: #666666;
}

.fee-value {
  font-size: 28rpx;
  color: #333333;
  font-weight: 500;
}

.notice-section {
  margin-bottom: 32rpx;
}

.notice-title {
  font-size: 28rpx;
  color: #666666;
  margin-bottom: 16rpx;
  display: block;
}

.notice-content {
  padding: 24rpx;
  background-color: #f8f9fa;
  border-radius: 16rpx;
}

.notice-item {
  font-size: 24rpx;
  color: #999999;
  line-height: 1.6;
  margin-bottom: 12rpx;
  display: block;
}

.notice-item:last-child {
  margin-bottom: 0;
}

.dialog-footer {
  display: flex;
  gap: 24rpx;
  padding: 32rpx 32rpx 32rpx;
  margin: 0 -32rpx;
  border-top: 1rpx solid #f5f5f5;
  background-color: #ffffff;
  flex-shrink: 0;
  background-color: #ffffff;
  flex-shrink: 0; /* 防止被压缩 */
}

.cancel-btn {
  flex: 1;
  height: 88rpx;
  background-color: #f5f5f5;
  color: #666666;
  border: none;
  border-radius: 16rpx;
  font-size: 32rpx;
}

.submit-btn {
  flex: 2;
  height: 88rpx;
  background-color: #0A0D05;
  color: #A9FF00;
  border: none;
  border-radius: 16rpx;
  font-size: 32rpx;
  font-weight: 500;
}

.submit-btn.disabled {
  background-color: #cccccc;
  color: #999999;
}
</style>