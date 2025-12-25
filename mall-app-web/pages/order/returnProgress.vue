<template>
  <view class="page-container">
    <!-- 步骤1: 寄回商品 -->
    <view v-if="currentStep === 1" class="step-page">
      <!-- 标题卡片 -->
      <view class="title-card">
        <text class="title-text">寄回商品</text>
        <text class="subtitle-text">{{ orderInfo.createTime || '2023/10/10 14:00:00' }}</text>
        <text class="description-text">与商家协商，已同意退货退款，请正确填写物流单号</text>
      </view>

      <!-- 物流信息卡片 -->
      <view class="info-card">
        <text class="card-title">物流信息</text>
        <view class="info-item">
          <text class="info-label">收货人</text>
          <text class="info-value">{{ returnAddress.receiverName || 'XXXXX 12994877366' }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">收货地址</text>
          <text class="info-value">{{ returnAddress.receiverAddress || '北京市 XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX' }}</text>
        </view>
        <view class="copy-button" @tap="copyAddress">
          <text class="copy-text">复制信息</text>
        </view>
      </view>

      <!-- 我已寄出按钮 -->
      <view class="action-section">
        <text class="action-label">我已寄出</text>
        <view class="action-button" @tap="confirmShipped">
          <text class="action-text">填写单号</text>
        </view>
      </view>

      <!-- 退货信息卡片 -->
      <view class="product-card">
        <text class="card-title">退货信息</text>
        <view class="product-item">
          <image
            class="product-image"
            :src="productInfo.productPic || '/static/images/default-product.png'"
            mode="aspectFill"
          ></image>
          <view class="product-details">
            <text class="product-name">{{ productInfo.productName || '商品名称待获取名称商品名称' }}</text>
            <view class="product-meta">
              <text class="meta-item">退货数量：{{ productInfo.quantity || 'XXXXX' }}</text>
              <text class="meta-item">退货金额：¥ {{ productInfo.refundAmount || '35' }}</text>
              <text class="meta-item">申请时间：{{ productInfo.applyTime || '2023/09/09 00:09' }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 底部按钮 -->
      <view class="bottom-button-container">
        <view class="bottom-button" @tap="closePage">
          <text class="bottom-button-text">关闭退货</text>
        </view>
      </view>
    </view>

    <!-- 步骤2: 输入运单号 -->
    <view v-if="currentStep === 2" class="step-page">
      <!-- 标题 -->
      <view class="input-title">
        <text class="input-title-text">填写快递单号</text>
      </view>

      <!-- 运单号输入 -->
      <view class="input-container">
        <input
          class="tracking-input"
          v-model="trackingNumber"
          placeholder="XXXXX 12994877366"
          maxlength="50"
        />
      </view>

      <!-- 底部按钮 -->
      <view class="modal-buttons">
        <view class="modal-button cancel" @tap="cancelInput">
          <text class="modal-button-text">取消</text>
        </view>
        <view class="modal-button confirm" @tap="confirmInput" :class="{ 'disabled': !trackingNumber }">
          <text class="modal-button-text">确定</text>
        </view>
      </view>
    </view>

    <!-- 步骤3: 已提交运单号 -->
    <view v-if="currentStep === 3" class="step-page">
      <!-- 标题卡片 -->
      <view class="title-card">
        <text class="title-text">寄回商品</text>
        <text class="subtitle-text">{{ orderInfo.createTime || '2023/10/10 14:00:00' }}</text>
        <text class="description-text">与商家协商，已同意退货退款，请正确填写物流单号</text>
      </view>

      <!-- 物流信息卡片 -->
      <view class="info-card">
        <text class="card-title">物流信息</text>
        <view class="info-item">
          <text class="info-label">收货人</text>
          <text class="info-value">{{ returnAddress.receiverName || 'XXXXX 12994877366' }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">收货地址</text>
          <text class="info-value">{{ returnAddress.receiverAddress || '北京市 XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX' }}</text>
        </view>
        <view class="copy-button" @tap="copyAddress">
          <text class="copy-text">复制信息</text>
        </view>
      </view>

      <!-- 已寄出状态 -->
      <view class="shipped-section">
        <text class="shipped-label">我已寄出</text>
        <view class="shipped-info">
          <text class="shipped-number">{{ trackingNumber || 'XXXXX 12994877366' }}</text>
          <text class="shipped-status">修改</text>
        </view>
      </view>

      <!-- 退货信息卡片 -->
      <view class="product-card">
        <text class="card-title">退货信息</text>
        <view class="product-item">
          <image
            class="product-image"
            :src="productInfo.productPic || '/static/images/default-product.png'"
            mode="aspectFill"
          ></image>
          <view class="product-details">
            <text class="product-name">{{ productInfo.productName || '商品名称待获取名称商品名称' }}</text>
            <view class="product-meta">
              <text class="meta-item">退货数量：{{ productInfo.quantity || 'XXXXX' }}</text>
              <text class="meta-item">退货金额：¥ {{ productInfo.refundAmount || '35' }}</text>
              <text class="meta-item">申请时间：{{ productInfo.applyTime || '2023/09/09 00:09' }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 底部按钮 -->
      <view class="bottom-button-container">
        <view class="bottom-button" @tap="closePage">
          <text class="bottom-button-text">关闭退货</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { updateReturnApplyDelivery, fetchOrderReturnApplyList } from '@/api/order.js';

export default {
  data() {
    return {
      currentStep: 1, // 当前步骤：1-寄回商品，2-输入运单号，3-已提交运单号
      trackingNumber: '', // 运单号
      returnApplyId: null, // 退货申请ID
      
      // 订单信息
      orderInfo: {
        createTime: '2023/10/10 14:00:00'
      },
      
      // 退货地址信息
      returnAddress: {
        receiverName: 'XXXXX 12994877366',
        receiverAddress: '北京市 XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX'
      },
      
      // 商品信息
      productInfo: {
        productPic: '',
        productName: '商品名称待获取名称商品名称',
        quantity: 'XXXXX',
        refundAmount: '35',
        applyTime: '2023/09/09 00:09'
      }
    };
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
      return;
    }

    // 根据传入的步骤参数设置当前步骤
    if (options.step) {
      this.currentStep = parseInt(options.step);
    }
  },
  
  methods: {
    // 获取退货申请详情
    getReturnApplyDetail() {
      if (!this.returnApplyId) return;

      uni.showLoading({
        title: '加载中...'
      });

      // 这里应该调用获取单个退货申请详情的API
      // 暂时使用获取列表API然后筛选
      fetchOrderReturnApplyList().then(res => {
        uni.hideLoading();

        if (res.code === 200 && res.data && res.data.length > 0) {
          // 找到对应的退货申请
          const data = res.data.find(item => item.id == this.returnApplyId);
          if (data) {
            this.orderInfo = {
              createTime: data.createTime
            };
            this.productInfo = {
              productPic: data.productPic,
              productName: data.productName,
              quantity: data.productCount,
              refundAmount: data.productRealPrice,
              applyTime: data.createTime
            };
            this.returnAddress = {
              receiverName: data.receiverName || 'XXXXX 12994877366',
              receiverAddress: data.receiverAddress || '北京市 XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX'
            };

            // 根据状态判断当前步骤
            if (data.status === 1) { // 已审核通过，可以寄回商品
              if (data.deliverySn) {
                // 已有运单号，显示在第三步
                this.trackingNumber = data.deliverySn;
                this.currentStep = 3;
              } else {
                // 还没有运单号，显示第一步
                this.currentStep = 1;
              }
            } else {
              // 还在审核中，返回上一页
              uni.showModal({
                title: '提示',
                content: '您的售后申请还在审核中，请耐心等待',
                showCancel: false,
                success: () => {
                  uni.navigateBack();
                }
              });
            }
          } else {
            uni.showToast({
              title: '未找到退货申请',
              icon: 'none'
            });
            setTimeout(() => {
              uni.navigateBack();
            }, 1500);
          }
        } else {
          uni.showToast({
            title: '获取数据失败',
            icon: 'none'
          });
        }
      }).catch(err => {
        console.error('获取退货申请详情失败', err);
        uni.hideLoading();
        uni.showToast({
          title: '获取数据失败',
          icon: 'none'
        });
      });
    },
    
    // 复制地址信息
    copyAddress() {
      const addressInfo = `收货人：${this.returnAddress.receiverName}\n收货地址：${this.returnAddress.receiverAddress}`;
      
      uni.setClipboardData({
        data: addressInfo,
        success: () => {
          uni.showToast({
            title: '地址信息已复制',
            icon: 'success'
          });
        }
      });
    },
    
    // 确认已寄出
    confirmShipped() {
      this.currentStep = 2; // 跳转到输入运单号步骤
    },
    
    // 取消输入运单号
    cancelInput() {
      this.currentStep = 1; // 返回寄回商品步骤
      this.trackingNumber = ''; // 清空输入
    },
    
    // 确认输入运单号
    confirmInput() {
      if (!this.trackingNumber.trim()) {
        uni.showToast({
          title: '请输入运单号',
          icon: 'none'
        });
        return;
      }
      
      uni.showLoading({
        title: '提交中...'
      });
      
      const deliveryData = {
        returnApplyId: this.returnApplyId,
        deliverySn: this.trackingNumber.trim()
      };
      
      // 调用API提交运单号
      updateReturnApplyDelivery(deliveryData).then(res => {
        uni.hideLoading();
        
        if (res.code === 200) {
          uni.showToast({
            title: '运单号提交成功',
            icon: 'success'
          });
          
          this.currentStep = 3; // 跳转到已提交状态
        } else {
          uni.showToast({
            title: res.message || '提交失败',
            icon: 'none'
          });
        }
      }).catch(err => {
        console.error('提交运单号失败', err);
        uni.hideLoading();
        
        uni.showToast({
          title: '提交失败',
          icon: 'none'
        });
      });
    },
    
    // 关闭页面
    closePage() {
      uni.navigateBack();
    }
  }
};
</script>

<style lang="scss">
// 根据设计稿样式
page {
  background: #F8F8F8;
}

.page-container {
  min-height: 100vh;
  background: #F8F8F8;
  padding: 16rpx;
  padding-bottom: 120rpx;
}

// 步骤页面
.step-page {
  display: flex;
  flex-direction: column;
}

// 标题卡片
.title-card {
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 32rpx;
  margin-bottom: 16rpx;

  .title-text {
    font-family: PingFang SC;
    font-size: 36rpx;
    font-weight: 500;
    color: #0A0D05;
    margin-bottom: 8rpx;
    display: block;
  }

  .subtitle-text {
    font-family: PingFang SC;
    font-size: 28rpx;
    font-weight: 400;
    color: #9FA19D;
    margin-bottom: 16rpx;
    display: block;
  }

  .description-text {
    font-family: PingFang SC;
    font-size: 28rpx;
    font-weight: 400;
    color: #0A0D05;
    line-height: 1.5;
    display: block;
  }
}

// 信息卡片
.info-card {
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

    &:last-of-type {
      margin-bottom: 24rpx;
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

  .copy-button {
    width: 100%;
    height: 80rpx;
    background: #F5F5F5;
    border-radius: 16rpx;
    display: flex;
    align-items: center;
    justify-content: center;

    .copy-text {
      font-family: PingFang SC;
      font-size: 28rpx;
      font-weight: 400;
      color: #0A0D05;
    }
  }
}

// 操作区域
.action-section {
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 32rpx;
  margin-bottom: 16rpx;

  .action-label {
    font-family: PingFang SC;
    font-size: 32rpx;
    font-weight: 500;
    color: #0A0D05;
    margin-bottom: 24rpx;
    display: block;
  }

  .action-button {
    width: 100%;
    height: 80rpx;
    background: #A9FF00;
    border-radius: 16rpx;
    display: flex;
    align-items: center;
    justify-content: center;

    .action-text {
      font-family: PingFang SC;
      font-size: 28rpx;
      font-weight: 400;
      color: #0A0D05;
    }
  }
}

// 已寄出状态
.shipped-section {
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 32rpx;
  margin-bottom: 16rpx;

  .shipped-label {
    font-family: PingFang SC;
    font-size: 32rpx;
    font-weight: 500;
    color: #0A0D05;
    margin-bottom: 24rpx;
    display: block;
  }

  .shipped-info {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .shipped-number {
      font-family: PingFang SC;
      font-size: 28rpx;
      font-weight: 400;
      color: #0A0D05;
    }

    .shipped-status {
      font-family: PingFang SC;
      font-size: 28rpx;
      font-weight: 400;
      color: #9FA19D;
    }
  }
}

// 商品卡片
.product-card {
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
        margin-bottom: 16rpx;
      }

      .product-meta {
        .meta-item {
          font-family: PingFang SC;
          font-size: 24rpx;
          font-weight: 400;
          color: #9FA19D;
          line-height: 1.5;
          margin-bottom: 8rpx;
          display: block;

          &:last-child {
            margin-bottom: 0;
          }
        }
      }
    }
  }
}

// 输入运单号页面样式
.input-title {
  text-align: center;
  padding: 80rpx 0 120rpx;

  .input-title-text {
    font-family: PingFang SC;
    font-size: 36rpx;
    font-weight: 500;
    color: #0A0D05;
  }
}

.input-container {
  padding: 0 32rpx;
  margin-bottom: 120rpx;

  .tracking-input {
    width: 100%;
    height: 80rpx;
    background: #FFFFFF;
    border-radius: 16rpx;
    padding: 0 24rpx;
    font-family: PingFang SC;
    font-size: 28rpx;
    font-weight: 400;
    color: #0A0D05;
    text-align: center;
    box-sizing: border-box;
    border: none;

    &::placeholder {
      color: #9FA19D;
    }
  }
}

.modal-buttons {
  position: fixed;
  left: 0;
  bottom: 0;
  width: 100%;
  background: #FFFFFF;
  padding: 20rpx 32rpx;
  box-shadow: 0rpx -2rpx 2rpx 0rpx rgba(0, 0, 0, 0.05);
  display: flex;

  .modal-button {
    flex: 1;
    height: 80rpx;
    border-radius: 16rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 8rpx;

    &:first-child {
      margin-left: 0;
    }

    &:last-child {
      margin-right: 0;
    }

    &.cancel {
      background: #F5F5F5;

      .modal-button-text {
        color: #9FA19D;
      }
    }

    &.confirm {
      background: #20201E;

      .modal-button-text {
        color: #A9FF00;
      }

      &.disabled {
        background: #F5F5F5;

        .modal-button-text {
          color: #CCCCCC;
        }
      }
    }

    .modal-button-text {
      font-family: PingFang SC;
      font-size: 28rpx;
      font-weight: 400;
    }
  }
}

// 底部按钮容器
.bottom-button-container {
  position: fixed;
  left: 0;
  bottom: 0;
  width: 100%;
  background: #FFFFFF;
  padding: 20rpx 32rpx;
  box-shadow: 0rpx -2rpx 2rpx 0rpx rgba(0, 0, 0, 0.05);

  .bottom-button {
    width: 100%;
    height: 80rpx;
    background: #20201E;
    border-radius: 16rpx;
    display: flex;
    align-items: center;
    justify-content: center;

    .bottom-button-text {
      font-family: PingFang SC;
      font-size: 32rpx;
      font-weight: 400;
      color: #A9FF00;
    }
  }
}
</style>
