<template>
  <view class="page-container">
    <!-- 进度状态卡片 -->
    <view class="progress-card">
      <view class="progress-header">
        <text class="progress-title">售后进度</text>
        <text class="progress-status" :class="statusClass">{{ statusText }}</text>
      </view>
      
      <!-- 进度时间线 -->
      <view class="timeline">
        <view 
          v-for="(step, index) in timelineSteps" 
          :key="index"
          class="timeline-item"
          :class="{ 'active': step.active, 'completed': step.completed }"
        >
          <view class="timeline-dot"></view>
          <view class="timeline-content">
            <text class="timeline-title">{{ step.title }}</text>
            <text class="timeline-desc">{{ step.description }}</text>
            <text v-if="step.time" class="timeline-time">{{ step.time }}</text>
          </view>
        </view>
      </view>
    </view>



    <!-- 物流信息卡片（仅退货退款时显示） -->
    <view v-if="returnApply.returnType === 2" class="logistics-card">
      <text class="card-title">物流信息</text>

      <view class="logistics-content">
        <!-- 收货人信息 -->
        <view class="logistics-item">
          <text class="logistics-label">收货人</text>
          <text class="logistics-value">{{ returnAddressInfo.name || 'XXXXX' }} {{ returnAddressInfo.phone || '12994877366' }}</text>
        </view>

        <!-- 收货地址 -->
        <view class="logistics-item">
          <text class="logistics-label">收货地址</text>
          <view class="address-container">
            <text class="logistics-value address-text">{{ returnAddressInfo.fullAddress || '北京市 XXXXXXXXXXXXXXX\nXXXXXXXXXXXXX' }}</text>
          </view>
        </view>

        <!-- 复制信息按钮 -->
        <button class="copy-info-btn" @tap="copyAddressInfo">复制信息</button>
      </view>

      <!-- 我已寄出部分 -->
      <view class="shipped-section">
        <view class="shipped-left">
          <text class="shipped-title">我已寄出</text>
          <text class="shipped-subtitle">点击填写单号</text>
        </view>
        <button v-if="canFillDelivery" class="fill-number-btn" @tap="showDeliveryModal">填写单号</button>
        <text v-else class="shipped-status">{{ returnApply.deliverySn ? '已填写' : '待填写' }}</text>
      </view>
    </view>

    <!-- 退款信息卡片 -->
    <view class="refund-card">
      <!-- 商品信息和标题 -->
      <view class="refund-header">
        <image
          class="product-image"
          :src="returnApply.productPic || '/static/images/default-product.png'"
          mode="aspectFill"
        ></image>
        <text class="product-name">{{ returnApply.productName || '商品名称商品名称商品名称商品名称' }}</text>
        <text class="card-title">退款信息</text>
      </view>

      <!-- 退款详情 -->
      <view class="refund-details">
        <view class="refund-item">
          <text class="refund-label">退款原因</text>
          <text class="refund-value">{{ returnApply.reason || 'XXXXX' }}</text>
        </view>
        <view class="refund-item">
          <text class="refund-label">退款金额</text>
          <text class="refund-value">¥{{ returnApply.returnAmount || '35' }}</text>
        </view>
        <view class="refund-item">
          <text class="refund-label">申请时间</text>
          <text class="refund-value">{{ formatTime(returnApply.createTime) || '2023/09/09 00:09' }}</text>
        </view>
      </view>
    </view>

    <!-- 处理结果卡片（已处理时显示） -->
    <view v-if="returnApply.status >= 2" class="result-card">
      <text class="card-title">处理结果</text>
      <view class="info-item">
        <text class="info-label">处理状态</text>
        <text class="info-value">{{ getHandleStatusText() }}</text>
      </view>
      <view v-if="returnApply.handleNote" class="info-item">
        <text class="info-label">处理备注</text>
        <text class="info-value">{{ returnApply.handleNote }}</text>
      </view>
      <view v-if="returnApply.handleTime" class="info-item">
        <text class="info-label">处理时间</text>
        <text class="info-value">{{ formatTime(returnApply.handleTime) }}</text>
      </view>
    </view>

    <!-- 操作按钮 -->
    <view class="action-buttons">
      <button class="action-button cancel" @tap="showCloseModal">
        <text class="action-text">关闭售后</text>
      </button>
    </view>

    <!-- 填写快递信息弹窗 -->
    <view v-if="showDeliveryForm" class="delivery-modal" @tap="hideDeliveryModal">
      <view class="delivery-content" @tap.stop>
        <view class="modal-header">
          <text class="modal-title">填写快递信息</text>
          <text class="modal-close" @tap="hideDeliveryModal">×</text>
        </view>

        <view class="form-group">
          <text class="form-label">快递公司</text>
          <input
            class="form-input"
            v-model="deliveryForm.deliveryCompany"
            placeholder="请输入快递公司名称"
          />
        </view>

        <view class="form-group">
          <text class="form-label">快递单号</text>
          <input
            class="form-input"
            v-model="deliveryForm.deliverySn"
            placeholder="请输入快递单号"
          />
        </view>

        <view class="modal-buttons">
          <button class="modal-btn cancel-btn" @tap="hideDeliveryModal">取消</button>
          <button class="modal-btn confirm-btn" @tap="submitDeliveryInfo" :disabled="!canSubmitDelivery">确认</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { fetchOrderReturnApplyDetail, cancelReturnApply } from '@/api/order.js';

export default {
  data() {
    return {
      returnApplyId: null,
      returnApply: {},
      timelineSteps: [],
      showDeliveryForm: false,
      deliveryForm: {
        deliveryCompany: '',
        deliverySn: ''
      }
    };
  },
  
  computed: {
    // 是否显示快递信息
    showDeliveryInfo() {
      return this.returnApply.returnType === 2 && this.returnApply.deliverySn;
    },

    // 是否可以填写快递信息
    canFillDelivery() {
      return this.returnApply.status === 1 &&
             this.returnApply.returnType === 2 &&
             !this.returnApply.deliverySn;
    },

    // 解析退货地址信息
    returnAddressInfo() {
      if (!this.returnApply.handleNote) {
        return {
          name: '',
          phone: '',
          fullAddress: ''
        };
      }

      // 从handleNote中解析退货地址信息
      const addressMatch = this.returnApply.handleNote.match(/退货地址信息\|收货人:([^|]*)\|电话:([^|]*)\|地址:([^|]*)/);
      if (addressMatch) {
        return {
          name: addressMatch[1] || '',
          phone: addressMatch[2] || '',
          fullAddress: addressMatch[3] || ''
        };
      }

      return {
        name: '',
        phone: '',
        fullAddress: ''
      };
    },

    // 是否可以提交快递信息
    canSubmitDelivery() {
      return this.deliveryForm.deliveryCompany.trim() && this.deliveryForm.deliverySn.trim();
    },

    // 获取状态文本
    statusText() {
      const status = this.returnApply.status;
      const returnType = this.returnApply.returnType;

      switch (status) {
        case 0:
          return '待审核';
        case 1:
          if (returnType === 1) {
            return '退款处理中';
          } else {
            return this.returnApply.deliverySn ? '退款处理中' : '待寄回商品';
          }
        case 2:
          return '已完成';
        case 3:
          return '已拒绝';
        default:
          return '未知状态';
      }
    },

    // 获取状态样式类
    statusClass() {
      const status = this.returnApply.status;
      switch (status) {
        case 0:
          return 'status-pending';
        case 1:
          return 'status-processing';
        case 2:
          return 'status-completed';
        case 3:
          return 'status-rejected';
        default:
          return 'status-unknown';
      }
    }
  },
  
  onLoad(options) {
    if (options.returnApplyId) {
      this.returnApplyId = options.returnApplyId;
      this.getReturnApplyDetail();
    } else {
      uni.showToast({
        title: '参数错误',
        icon: 'none'
      });
      setTimeout(() => {
        uni.navigateBack();
      }, 1500);
    }
  },
  
  methods: {
    // 获取售后申请详情
    getReturnApplyDetail() {
      uni.showLoading({
        title: '加载中...'
      });
      
      fetchOrderReturnApplyDetail(this.returnApplyId).then(res => {
        uni.hideLoading();
        
        if (res.code === 200 && res.data) {
          this.returnApply = res.data;
          this.generateTimeline();
        } else {
          uni.showToast({
            title: res.message || '获取数据失败',
            icon: 'none'
          });
        }
      }).catch(err => {
        console.error('获取售后申请详情失败', err);
        uni.hideLoading();
        uni.showToast({
          title: '获取数据失败',
          icon: 'none'
        });
      });
    },
    
    // 生成时间线
    generateTimeline() {
      const steps = [];
      const status = this.returnApply.status;
      const returnType = this.returnApply.returnType;
      
      // 第一步：提交申请
      steps.push({
        title: '提交申请',
        description: '您已提交售后申请',
        time: this.formatTime(this.returnApply.createTime),
        completed: true,
        active: false
      });
      
      // 第二步：商家审核
      if (status >= 1) {
        steps.push({
          title: '商家审核',
          description: status === 1 ? '商家已同意您的申请' : '商家审核中',
          time: this.formatTime(this.returnApply.handleTime),
          completed: status >= 1,
          active: status === 0
        });
      } else {
        steps.push({
          title: '商家审核',
          description: '等待商家审核',
          time: '',
          completed: false,
          active: true
        });
      }
      
      // 第三步：退货流程（仅退货退款）
      if (returnType === 2) {
        if (status >= 1) {
          steps.push({
            title: '寄回商品',
            description: this.returnApply.deliverySn ? '您已寄回商品' : '请寄回商品并填写快递信息',
            time: this.returnApply.deliverySn ? this.formatTime(this.returnApply.handleTime) : '',
            completed: !!this.returnApply.deliverySn,
            active: status === 1 && !this.returnApply.deliverySn
          });
        }
      }
      
      // 最后一步：完成退款
      if (status >= 2) {
        steps.push({
          title: '退款完成',
          description: status === 2 ? '退款已完成' : '申请已拒绝',
          time: this.formatTime(this.returnApply.handleTime),
          completed: status === 2,
          active: false
        });
      } else if (status === 1 && (returnType === 1 || this.returnApply.deliverySn)) {
        steps.push({
          title: '退款处理',
          description: '商家处理退款中',
          time: '',
          completed: false,
          active: true
        });
      }
      
      this.timelineSteps = steps;
    },



    // 获取申请类型文本
    getReturnTypeText() {
      return this.returnApply.returnType === 1 ? '仅退款' : '退货退款';
    },

    // 获取处理状态文本
    getHandleStatusText() {
      return this.returnApply.status === 2 ? '已同意' : '已拒绝';
    },

    // 格式化商品属性
    formatProductAttr(attr) {
      if (!attr) return '';
      try {
        const attrObj = typeof attr === 'string' ? JSON.parse(attr) : attr;
        if (Array.isArray(attrObj)) {
          return attrObj.map(item => `${item.key}: ${item.value}`).join('; ');
        }
      } catch (e) {
        return attr;
      }
      return '';
    },

    // 取消申请
    cancelApply() {
      uni.showModal({
        title: '确认取消',
        content: '确定要取消这个售后申请吗？',
        success: (res) => {
          if (res.confirm) {
            this.performCancelApply();
          }
        }
      });
    },

    // 执行取消申请
    performCancelApply() {
      uni.showLoading({
        title: '取消中...'
      });

      cancelReturnApply(this.returnApplyId).then(res => {
        uni.hideLoading();

        if (res.code === 200) {
          uni.showToast({
            title: '取消成功',
            icon: 'success'
          });
          setTimeout(() => {
            uni.navigateBack();
          }, 1500);
        } else {
          uni.showToast({
            title: res.message || '取消失败',
            icon: 'none'
          });
        }
      }).catch(err => {
        console.error('取消申请失败', err);
        uni.hideLoading();
        uni.showToast({
          title: '取消失败',
          icon: 'none'
        });
      });
    },

    // 填写快递信息
    fillDeliveryInfo() {
      uni.navigateTo({
        url: `/pages/order/returnProgress?returnApplyId=${this.returnApplyId}&step=1`
      });
    },

    // 联系客服
    contactService() {
      uni.showToast({
        title: '客服功能开发中',
        icon: 'none'
      });
    },

    // 格式化时间
    formatTime(time) {
      if (!time) return '';

      try {
        const date = new Date(time);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');

        return `${year}-${month}-${day} ${hours}:${minutes}`;
      } catch (e) {
        console.error('时间格式化失败', e);
        return time;
      }
    },

    // 复制地址信息
    copyAddressInfo() {
      const addressInfo = `收货人：${this.returnAddressInfo.name}\n电话：${this.returnAddressInfo.phone}\n地址：${this.returnAddressInfo.fullAddress}`;

      uni.setClipboardData({
        data: addressInfo,
        success: () => {
          uni.showToast({
            title: '地址信息已复制',
            icon: 'success'
          });
        },
        fail: () => {
          uni.showToast({
            title: '复制失败',
            icon: 'none'
          });
        }
      });
    },

    // 显示关闭售后确认弹窗
    showCloseModal() {
      uni.showModal({
        title: '确认关闭售后',
        content: '关闭后将无法继续售后流程，确定要关闭吗？',
        success: (res) => {
          if (res.confirm) {
            this.closeReturnApply();
          }
        }
      });
    },

    // 关闭售后申请
    closeReturnApply() {
      uni.showLoading({
        title: '处理中...'
      });

      // 调用取消申请API
      import('@/api/order.js').then(orderApi => {
        return orderApi.cancelReturnApply(this.returnApplyId);
      }).then(res => {
        uni.hideLoading();

        if (res.code === 200) {
          uni.showToast({
            title: '已关闭售后申请',
            icon: 'success'
          });

          // 延迟返回上一页
          setTimeout(() => {
            uni.navigateBack();
          }, 1500);
        } else {
          uni.showToast({
            title: res.message || '关闭失败',
            icon: 'none'
          });
        }
      }).catch(err => {
        console.error('关闭售后申请失败', err);
        uni.hideLoading();
        uni.showToast({
          title: '关闭失败',
          icon: 'none'
        });
      });
    },

    // 显示填写快递信息弹窗
    showDeliveryModal() {
      this.showDeliveryForm = true;
      // 清空表单
      this.deliveryForm = {
        deliveryCompany: '',
        deliverySn: ''
      };
    },

    // 隐藏填写快递信息弹窗
    hideDeliveryModal() {
      this.showDeliveryForm = false;
    },

    // 提交快递信息
    submitDeliveryInfo() {
      if (!this.canSubmitDelivery) {
        uni.showToast({
          title: '请填写完整信息',
          icon: 'none'
        });
        return;
      }

      uni.showLoading({
        title: '提交中...'
      });

      // 调用更新快递信息API
      import('@/api/order.js').then(orderApi => {
        const data = {
          returnApplyId: this.returnApplyId,
          deliveryCompany: this.deliveryForm.deliveryCompany,
          deliverySn: this.deliveryForm.deliverySn
        };
        return orderApi.updateReturnApplyDelivery(data);
      }).then(res => {
        uni.hideLoading();

        if (res.code === 200) {
          uni.showToast({
            title: '提交成功',
            icon: 'success'
          });

          // 隐藏弹窗
          this.hideDeliveryModal();

          // 刷新页面数据
          this.getReturnApplyDetail();
        } else {
          uni.showToast({
            title: res.message || '提交失败',
            icon: 'none'
          });
        }
      }).catch(err => {
        console.error('提交快递信息失败', err);
        uni.hideLoading();
        uni.showToast({
          title: '提交失败',
          icon: 'none'
        });
      });
    }
  }
};
</script>

<style lang="scss">
page {
  background: #F8F8F8;
}

.page-container {
  min-height: 100vh;
  background: #F8F8F8;
  padding: 16rpx;
  padding-bottom: 120rpx;
}

// 进度状态卡片
.progress-card {
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 32rpx;
  margin-bottom: 16rpx;

  .progress-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 32rpx;

    .progress-title {
      font-family: PingFang SC;
      font-size: 32rpx;
      font-weight: 500;
      color: #0A0D05;
    }

    .progress-status {
      font-family: PingFang SC;
      font-size: 28rpx;
      font-weight: 400;
      padding: 8rpx 16rpx;
      border-radius: 16rpx;

      &.status-pending {
        background: #FFF3CD;
        color: #856404;
      }

      &.status-processing {
        background: #D1ECF1;
        color: #0C5460;
      }

      &.status-completed {
        background: #D4EDDA;
        color: #155724;
      }

      &.status-rejected {
        background: #F8D7DA;
        color: #721C24;
      }
    }
  }
}

// 时间线
.timeline {
  .timeline-item {
    display: flex;
    align-items: flex-start;
    position: relative;
    padding-bottom: 32rpx;

    &:not(:last-child)::after {
      content: '';
      position: absolute;
      left: 20rpx;
      top: 40rpx;
      width: 2rpx;
      height: calc(100% - 20rpx);
      background: #E5E5E5;
    }

    &.active::after {
      background: #A9FF00;
    }

    &.completed::after {
      background: #A9FF00;
    }

    .timeline-dot {
      width: 40rpx;
      height: 40rpx;
      border-radius: 50%;
      background: #E5E5E5;
      margin-right: 24rpx;
      flex-shrink: 0;
      position: relative;

      &::after {
        content: '';
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        width: 16rpx;
        height: 16rpx;
        border-radius: 50%;
        background: #FFFFFF;
      }
    }

    &.active .timeline-dot {
      background: #A9FF00;
    }

    &.completed .timeline-dot {
      background: #A9FF00;
    }

    .timeline-content {
      flex: 1;

      .timeline-title {
        font-family: PingFang SC;
        font-size: 28rpx;
        font-weight: 500;
        color: #0A0D05;
        display: block;
        margin-bottom: 8rpx;
      }

      .timeline-desc {
        font-family: PingFang SC;
        font-size: 24rpx;
        font-weight: 400;
        color: #9FA19D;
        display: block;
        margin-bottom: 8rpx;
      }

      .timeline-time {
        font-family: PingFang SC;
        font-size: 24rpx;
        font-weight: 400;
        color: #9FA19D;
        display: block;
      }
    }
  }
}

// 信息卡片
.info-card, .product-card, .delivery-card, .result-card {
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 32rpx;
  margin-bottom: 16rpx;

  .card-title {
    font-family: PingFang SC;
    font-size: 32rpx;
    font-weight: 500;
    color: #0A0D05;
    margin-bottom: 24rpx;
    display: block;
  }

  .info-item {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 16rpx;

    &:last-child {
      margin-bottom: 0;
    }

    .info-label {
      font-family: PingFang SC;
      font-size: 28rpx;
      font-weight: 400;
      color: #9FA19D;
      width: 120rpx;
      flex-shrink: 0;
    }

    .info-value {
      font-family: PingFang SC;
      font-size: 28rpx;
      font-weight: 400;
      color: #0A0D05;
      flex: 1;
      text-align: right;
      line-height: 1.4;
    }
  }
}

// 商品信息
.product-item {
  display: flex;
  align-items: flex-start;

  .product-image {
    width: 120rpx;
    height: 120rpx;
    border-radius: 16rpx;
    margin-right: 24rpx;
    background: #F5F5F5;
    flex-shrink: 0;
  }

  .product-details {
    flex: 1;

    .product-name {
      font-family: PingFang SC;
      font-size: 28rpx;
      font-weight: 400;
      color: #0A0D05;
      line-height: 1.4;
      margin-bottom: 8rpx;
      display: block;
    }

    .product-attr {
      font-family: PingFang SC;
      font-size: 24rpx;
      font-weight: 400;
      color: #9FA19D;
      line-height: 1.4;
      margin-bottom: 16rpx;
      display: block;
    }

    .product-meta {
      .meta-item {
        font-family: PingFang SC;
        font-size: 24rpx;
        font-weight: 400;
        color: #9FA19D;
        line-height: 1.4;
        margin-right: 24rpx;
        display: inline-block;
      }
    }
  }
}

// 物流信息卡片
.logistics-card {
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 32rpx;
  margin: 24rpx 0;

  .card-title {
    font-family: PingFang SC;
    font-size: 28rpx;
    font-weight: 600;
    color: #000000;
    margin-bottom: 24rpx;
    display: block;
  }

  .logistics-content {
    margin-bottom: 32rpx;

    .logistics-item {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 32rpx;

      &:last-child {
        margin-bottom: 24rpx;
      }

      .logistics-label {
        font-family: PingFang SC;
        font-size: 28rpx;
        font-weight: 400;
        color: #666666;
        width: 120rpx;
        flex-shrink: 0;
      }

      .logistics-value {
        font-family: PingFang SC;
        font-size: 28rpx;
        font-weight: 400;
        color: #000000;
        flex: 1;
        text-align: right;
        line-height: 1.4;
      }

      .address-container {
        flex: 1;
        display: flex;
        justify-content: flex-end;

        .address-text {
          text-align: right;
          white-space: pre-line;
        }
      }
    }

    .copy-info-btn {
      background: #F6F6F6;
      border: 2rpx solid #0A0D05;
      border-radius: 16rpx;
      padding: 16rpx;
      width: 100%;
      height: 80rpx;
      font-family: PingFang SC;
      font-size: 28rpx;
      font-weight: 400;
      color: #0A0D05;
      display: flex;
      align-items: center;
      justify-content: center;
      line-height: 1;
    }
  }

  .shipped-section {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .shipped-left {
      display: flex;
      flex-direction: column;
      gap: 8rpx;
      flex: 1;

      .shipped-title {
        font-family: PingFang SC;
        font-size: 28rpx;
        font-weight: 600;
        color: #000000;
        line-height: 1.4;
      }

      .shipped-subtitle {
        font-family: PingFang SC;
        font-size: 28rpx;
        font-weight: 400;
        color: #9FA19D;
        line-height: 1.4;
      }
    }

    .fill-number-btn {
      background: #20201E;
      border-radius: 16rpx;
      padding: 16rpx 24rpx;
      height: 64rpx;
      font-family: PingFang SC;
      font-size: 28rpx;
      font-weight: 400;
      color: #A9FF00;
      display: flex;
      align-items: center;
      justify-content: center;
      line-height: 1;
      flex-shrink: 0;
      margin-left: auto;
    }

    .shipped-status {
      font-family: PingFang SC;
      font-size: 28rpx;
      font-weight: 400;
      color: #9FA19D;
      margin-left: auto;
      flex-shrink: 0;
    }
  }
}

// 退款信息卡片
.refund-card {
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 32rpx;
  margin: 24rpx 0;

  .refund-header {
    display: flex;
    align-items: center;
    margin-bottom: 32rpx;
    gap: 16rpx;

    .product-image {
      width: 80rpx;
      height: 80rpx;
      border-radius: 16rpx;
      flex-shrink: 0;
      background: #F5F5F5;
    }

    .product-name {
      font-family: PingFang SC;
      font-size: 28rpx;
      font-weight: 400;
      color: #0A0D05;
      line-height: 1.4;
      flex: 1;
    }

    .card-title {
      font-family: PingFang SC;
      font-size: 28rpx;
      font-weight: 600;
      color: #000000;
      flex-shrink: 0;
    }
  }

  .refund-details {
    display: flex;
    flex-direction: column;
    gap: 16rpx;

    .refund-item {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .refund-label {
        font-family: PingFang SC;
        font-size: 24rpx;
        font-weight: 400;
        color: #9FA19D;
        line-height: 1.4;
      }

      .refund-value {
        font-family: PingFang SC;
        font-size: 24rpx;
        font-weight: 400;
        color: #0A0D05;
        line-height: 1.4;
      }
    }
  }
}

// 操作按钮
.action-buttons {
  position: fixed;
  left: 0;
  bottom: 0;
  width: 100%;
  background: #FFFFFF;
  padding: 20rpx 32rpx;
  box-shadow: 0rpx -2rpx 2rpx 0rpx rgba(0, 0, 0, 0.05);
  display: flex;
  justify-content: center;

  .action-button {
    width: 100%;
    height: 80rpx;
    border-radius: 16rpx;
    display: flex;
    align-items: center;
    justify-content: center;

    &.cancel {
      background: #20201E;

      .action-text {
        color: #A9FF00;
      }
    }

    .action-text {
      font-family: PingFang SC;
      font-size: 28rpx;
      font-weight: 400;
    }
  }
}

// 填写快递信息弹窗
.delivery-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;

  .delivery-content {
    background: #FFFFFF;
    border-radius: 24rpx;
    padding: 48rpx 32rpx;
    margin: 0 32rpx;
    width: calc(100% - 64rpx);
    max-width: 600rpx;

    .modal-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 48rpx;

      .modal-title {
        font-family: PingFang SC;
        font-size: 32rpx;
        font-weight: 600;
        color: #000000;
      }

      .modal-close {
        font-size: 48rpx;
        color: #999999;
        line-height: 1;
        padding: 8rpx;
      }
    }

    .form-group {
      margin-bottom: 32rpx;

      .form-label {
        font-family: PingFang SC;
        font-size: 28rpx;
        font-weight: 400;
        color: #333333;
        display: block;
        margin-bottom: 16rpx;
      }

      .form-input {
        width: 100%;
        height: 80rpx;
        border: 2rpx solid #E5E5E5;
        border-radius: 12rpx;
        padding: 0 24rpx;
        font-family: PingFang SC;
        font-size: 28rpx;
        font-weight: 400;
        color: #333333;
        background: #FFFFFF;

        &:focus {
          border-color: #A9FF00;
        }
      }
    }

    .modal-buttons {
      display: flex;
      gap: 24rpx;
      margin-top: 48rpx;

      .modal-btn {
        flex: 1;
        height: 80rpx;
        border-radius: 16rpx;
        font-family: PingFang SC;
        font-size: 28rpx;
        font-weight: 400;
        display: flex;
        align-items: center;
        justify-content: center;
        line-height: 1;

        &.cancel-btn {
          background: #F5F5F5;
          color: #666666;
        }

        &.confirm-btn {
          background: #20201E;
          color: #A9FF00;

          &:disabled {
            background: #E5E5E5;
            color: #999999;
          }
        }
      }
    }
  }
}
</style>
