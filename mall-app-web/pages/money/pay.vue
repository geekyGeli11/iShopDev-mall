<template>
  <view class="app">
    <!-- 支付金额区域 -->
    <view class="price-box">
      <text>支付金额</text>
      <text class="price">{{ orderInfo.payAmount }}</text>
    </view>
    
    <!-- 支付方式列表 -->
    <view class="pay-type-list">
      <view class="type-item b-b" @tap="changePayType(2)">
        <text class="icon yticon icon-weixinzhifu"></text>
        <view class="con">
          <text class="tit">微信支付</text>
        </view>
        <label class="radio">
          <radio value="" color="#fa436a" :checked="payType == 2" />
        </label>
      </view>
    </view>
    
    <!-- 确认支付按钮 -->
    <text class="mix-btn" @tap="requestNotificationConsent">确认支付</text>
  </view>
</template>

<script>
import { fetchOrderDetail, getWechatPayParams } from '@/api/order';

export default {
  data() {
    return {
      payType: 2, // 0-未支付, 1-支付宝, 2-微信, 3-余额支付
      orderInfo: {
        id: '',
        orderSn: '',
        payAmount: '0.00'
      },
      isLoading: false // 支付处理中状态
    };
  },
  
  onLoad(options) {
    if (options.id) {
      // 从订单详情页进入
      this.loadOrderInfo(options.id);
    } else if (options.orderSn) {
      // 直接支付指定订单号
      this.orderInfo.orderSn = options.orderSn;
      this.loadOrderInfo(options.orderSn);
    } else {
      // 错误处理
      uni.showToast({
        title: '订单参数错误',
        icon: 'none'
      });
      setTimeout(() => {
        uni.navigateBack();
      }, 1500);
    }
  },
  
  methods: {
    // 加载订单信息
    loadOrderInfo(orderId) {
      uni.showLoading({
        title: '加载中...'
      });
      
      fetchOrderDetail(orderId).then(res => {
        uni.hideLoading();
        
        if (res.code === 200) {
          this.orderInfo = {
            id: res.data.id,
            orderSn: res.data.orderSn,
            payAmount: (res.data.payAmount || 0).toFixed(2)
          };
        } else {
          uni.showToast({
            title: res.message || '获取订单信息失败',
            icon: 'none'
          });
          setTimeout(() => {
            uni.navigateBack();
          }, 1500);
        }
      }).catch(err => {
        uni.hideLoading();
        console.error('获取订单信息失败', err);
        
        uni.showToast({
          title: '获取订单信息失败',
          icon: 'none'
        });
        
        setTimeout(() => {
          uni.navigateBack();
        }, 1500);
      });
    },
    
    // 改变支付方式
    changePayType(type) {
      this.payType = type;
    },
    
    // 请求订阅支付结果通知
    requestNotificationConsent() {
      // 微信小程序的订阅消息接口
      // 下单成功通知和物流发货通知
      const templateIds = [
        'H72bLIkhB2WllYtR20xAIV4HEGH179U2wLhE-i64Y7g', // 下单成功通知
        '5LeDpP78sOjg9W_WEASAfjMryI_Jps8XIHQjgu8mwho'  // 物流发货通知
      ];

      // 防止重复点击
      if (this.isLoading) return;

      // 订阅消息（仅微信小程序环境）
      // #ifdef MP-WEIXIN
      uni.requestSubscribeMessage({
        tmplIds: templateIds,
        success: (res) => {
          console.log('订阅消息结果', res);
          // 无论用户是否同意，都继续支付流程
          this.handlePay();
        },
        fail: (err) => {
          console.error('订阅消息失败', err);
          // 继续支付流程
          this.handlePay();
        }
      });
      // #endif

      // 非微信小程序环境直接处理支付
      // #ifndef MP-WEIXIN
      this.handlePay();
      // #endif
    },
    
    // 处理支付
    handlePay() {
      if (this.isLoading) return;
      this.isLoading = true;
      
      uni.showLoading({
        title: '支付处理中...'
      });
      
      // 确保订单信息完整
      if (!this.orderInfo.orderSn) {
        uni.hideLoading();
        this.isLoading = false;
        
        uni.showToast({
          title: '订单信息不完整',
          icon: 'none'
        });
        return;
      }
      
      if (this.payType === 2) {
        // 微信支付
        this.wxPay();
      } else {
        // 余额支付或其他支付方式
        uni.hideLoading();
        this.isLoading = false;
        
        uni.showToast({
          title: '暂不支持此支付方式',
          icon: 'none'
        });
      }
    },
    
    // 微信支付
    wxPay() {
      // 获取用户信息
      const userInfo = uni.getStorageSync('userInfo') || {};
      
      // 检查用户openId
      if (!userInfo.openid && !userInfo.wxOpenId) {
        uni.hideLoading();
        this.isLoading = false;
        
        uni.showToast({
          title: '用户信息不完整，请重新登录',
          icon: 'none'
        });
        
        setTimeout(() => {
          uni.navigateTo({
            url: '/pages/public/register'
          });
        }, 1500);
        return;
      }
      
      // 获取客户端IP地址（小程序获取不到真实IP，由服务端处理）
      let clientIP = '127.0.0.1';
      
      // #ifdef MP-WEIXIN
      // 微信小程序环境下尝试获取用户网络信息
      uni.getNetworkType({
        success: function (res) {
          console.log('当前网络类型：' + res.networkType);
        }
      });
      // #endif
      
      const payData = {
        orderSn: this.orderInfo.orderSn,
        amount: parseInt(parseFloat(this.orderInfo.payAmount) * 100), // 转换为分
        spbillCreateIp: clientIP, // 客户端IP
        openId: userInfo.openid || userInfo.wxOpenId // 从用户信息中获取openId
      };
      
      console.log('支付请求参数：', payData);
      
      getWechatPayParams(payData).then(res => {
        uni.hideLoading();
        
        if (res.code === 200 && res.data) {
          const payParams = res.data;
          
          // 调用微信支付API (仅在微信小程序环境)
          // #ifdef MP-WEIXIN
          uni.requestPayment({
            provider: 'wxpay',
            timeStamp: payParams.timeStamp,
            nonceStr: payParams.nonceStr,
            package: payParams.packageValue,
            signType: payParams.signType,
            paySign: payParams.paySign,
            success: (res) => {
              console.log('支付成功', res);
              this.paySuccess();
            },
            fail: (err) => {
              console.error('支付失败', err);
              
              let errMsg = '支付失败';
              if (err.errMsg === 'requestPayment:fail cancel') {
                errMsg = '支付已取消';
              }
              
              uni.showToast({
                title: errMsg,
                icon: 'none'
              });
            },
            complete: () => {
              this.isLoading = false;
            }
          });
          // #endif
          
          // 非微信小程序环境
          // #ifndef MP-WEIXIN
          uni.showToast({
            title: '当前环境不支持微信支付',
            icon: 'none'
          });
          this.isLoading = false;
          // #endif
        } else {
          this.isLoading = false;
          uni.showToast({
            title: res.message || '获取支付参数失败',
            icon: 'none'
          });
        }
      }).catch(err => {
        uni.hideLoading();
        this.isLoading = false;
        
        console.error('获取支付参数失败', err);
        uni.showToast({
          title: '获取支付参数失败',
          icon: 'none'
        });
      });
    },
    
    // 支付成功处理
    paySuccess() {
      uni.showToast({
        title: '支付成功',
        icon: 'success'
      });
      
      // 跳转到支付成功页
      setTimeout(() => {
        uni.redirectTo({
          url: `/pages/money/paySuccess?orderId=${this.orderInfo.id}&orderSn=${this.orderInfo.orderSn}&amount=${this.orderInfo.payAmount}`
        });
      }, 1500);
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
.app {
  width: 100%;
}

.price-box {
  background-color: #fff;
  height: 265rpx;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  font-size: 28rpx;
  color: #909399;
  
  .price {
    font-size: 50rpx;
    color: #303133;
    margin-top: 12rpx;
    
    &:before {
      content: "￥";
      font-size: 40rpx;
    }
  }
}

.pay-type-list {
  margin-top: 20rpx;
  background-color: #fff;
  padding-left: 60rpx;
  
  .type-item {
    height: 120rpx;
    padding: 20rpx 0;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-right: 60rpx;
    font-size: 30rpx;
    position: relative;
  }
  
  .icon {
    width: 100rpx;
    font-size: 52rpx;
  }
  
  .icon-erjiye-yucunkuan {
    color: #fe8e2e;
  }
  
  .icon-weixinzhifu {
    color: #36cb59;
  }
  
  .icon-alipay {
    color: #01aaef;
  }
  
  .tit {
    font-size: 32rpx;
    color: #333333;
    margin-bottom: 4rpx;
  }
  
  .con {
    flex: 1;
    display: flex;
    flex-direction: column;
    font-size: 24rpx;
    color: #909399;
  }
}

.mix-btn {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  padding: 20rpx 28rpx;
  gap: 16rpx;
  width: 252rpx;
  height: 84rpx;
  margin: 80rpx auto 30rpx;
  background: #20201E;
  border-radius: 16rpx;
  font-family: 'PingFang SC';
  font-style: normal;
  font-weight: 400;
  font-size: 32rpx;
  line-height: 44rpx;
  color: #A9FF00;
  flex: none;
  order: 1;
  flex-grow: 0;
}
</style> 