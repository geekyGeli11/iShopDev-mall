<template>
  <view>
    <view class="container">
      <view class="left-bottom-sign"></view>
      <view class="back-btn yticon icon-zuojiantou-up" @tap="navBack"></view>
      <view class="right-top-sign"></view>
      
      <view class="wrapper">
        <view class="left-top-sign">LOGIN</view>
        <view class="welcome">欢迎回来！</view>
        
        <view class="lb_con">
          <text class="l_c_txt" @tap="showPrivacy">《隐私政策》</text>和<text class="l_c_txt" @tap="showTerms">《用户协议》</text>帮助您了解我们收集、使用、存储和共享个人信息的情况，同时可了解我们所采集的个人信息类型与用途的对应关系。您需充分阅读并理解如上协议内容，若您同意，请点击下方按钮开始接受我们的服务。
        </view>
        
        <view class="agreement">
          <checkbox-group @change="checkboxChange">
            <checkbox class="checkbox" :value="checkVal" :checked="isAgreed"></checkbox>
            <text class="agreement-text">已阅读并同意<text class="l_c_txt" @tap="showTerms">《用户协议》</text>及<text class="l_c_txt" @tap="showPrivacy">《隐私政策》</text></text>
          </checkbox-group>
        </view>
        
        <button 
          class="confirm-btn" 
          :disabled="!isAgreed || logining" 
          @tap="toLogin"
        >同意协议并登录</button>
        
        <!-- 登录弹窗 -->
        <view class="login-popup" v-if="visible">
          <view class="mask" @tap="closePopup"></view>
          <view class="popup-content">
            <view class="top-btn" @tap="closePopup">
              <image class="close-btn" src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/close.png"></image>
            </view>
            <view class="content">
              <button 
                class="avatar-container" 
                open-type="chooseAvatar" 
                @chooseavatar="onChooseAvatar"
              >
                <image 
                  class="avatar" 
                  :src="avatarUrl || defaultAvatarUrl" 
                  alt="头像" 
                  mode="aspectFill"
                ></image>
              </button>
              
              <view class="nickname-wrapper center">
                <view class="nickname-item">昵称</view>
                <input 
                  class="nickname-input" 
                  type="nickname" 
                  placeholder="请输入昵称" 
                  maxlength="20" 
                  v-model="nickname"
                />
              </view>
              
              <button 
                class="submit-btn center" 
                :class="{'disabled': logining}"
                :disabled="logining" 
                @tap="submitInfo"
              >提交</button>
            </view>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 隐私政策弹窗 -->
    <privacy-popup 
      :visible="showPrivacyPopup"
      @close="closePrivacy"
    ></privacy-popup>
    
    <!-- 用户协议弹窗 -->
    <terms-popup 
      :visible="showTermsPopup"
      @close="closeTerms"
    ></terms-popup>
  </view>
</template>

<script>
export default {
  data() {
    return {
      isAgreed: false,
      checkVal: 'agreed',
      visible: false,
      showPrivacyPopup: false,
      showTermsPopup: false,
      avatarUrl: '',
      defaultAvatarUrl: 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/default-avatar.png',
      nickname: '',
      logining: false
    };
  },
  onLoad() {
    // 检查是否已经登录 - 使用统一的tokenInfo
    const tokenInfo = uni.getStorageSync('tokenInfo');
    let hasValidToken = false;

    if (tokenInfo) {
      try {
        const loginData = JSON.parse(tokenInfo);
        hasValidToken = !!(loginData && loginData.token && loginData.tokenHead && loginData.openId);
      } catch (e) {
        console.error('解析tokenInfo失败:', e);
        hasValidToken = false;
      }
    }

    if (hasValidToken) {
      this.navigateBack();
    }
  },
  methods: {
    // 返回上一页
    navBack() {
      uni.navigateBack();
    },
    
    // 导航回退处理
    navigateBack() {
      // 如果有上一页，返回上一页
      const pages = getCurrentPages();
      if (pages.length > 1) {
        uni.navigateBack();
      } else {
        // 否则跳转到首页
        uni.switchTab({
          url: '/pages/new_index/index'
        });
      }
    },
    
    // 协议复选框变化
    checkboxChange(e) {
      this.isAgreed = e.detail.value.length > 0;
    },
    
    // 点击登录按钮
    toLogin() {
      if (!this.isAgreed) {
        return uni.showToast({
          title: '请先同意用户协议和隐私政策',
          icon: 'none'
        });
      }
      
      // 显示登录弹窗
      this.visible = true;
    },
    
    // 显示隐私政策
    showPrivacy() {
      this.showPrivacyPopup = true;
    },
    
    // 关闭隐私政策
    closePrivacy() {
      this.showPrivacyPopup = false;
    },
    
    // 显示用户协议
    showTerms() {
      this.showTermsPopup = true;
    },
    
    // 关闭用户协议
    closeTerms() {
      this.showTermsPopup = false;
    },
    
    // 关闭登录弹窗
    closePopup() {
      this.visible = false;
    },
    
    // 选择头像
    onChooseAvatar(e) {
      this.avatarUrl = e.detail.avatarUrl;
    },
    
    // 提交用户信息
    submitInfo() {
      if (!this.nickname.trim()) {
        return uni.showToast({
          title: '请输入昵称',
          icon: 'none'
        });
      }
      
      if (!this.avatarUrl) {
        return uni.showToast({
          title: '请选择头像',
          icon: 'none'
        });
      }
      
      this.logining = true;
      
      // 实际项目中应该调用API进行登录
      setTimeout(() => {
        // 模拟登录成功
        const userInfo = {
          nickname: this.nickname,
          avatar: this.avatarUrl,
          userId: 'user_' + Date.now()
        };
        
        // 保存登录信息
        uni.setStorageSync('token', 'mock_token_' + Date.now());
        uni.setStorageSync('userInfo', userInfo);
        
        this.logining = false;
        this.visible = false;
        
        uni.showToast({
          title: '登录成功',
          icon: 'success'
        });
        
        // 延迟返回，让用户看到登录成功提示
        setTimeout(() => {
          this.navigateBack();
        }, 1500);
      }, 1000);
    }
  }
};
</script>

<style lang="scss" scoped>
@charset "UTF-8";
/* 页面左右间距 */
/* 文字尺寸 */
/*文字颜色*/
/* 边框颜色 */
/* 图片加载中颜色 */
/* 行为相关颜色 */
page {
  background: #fff;
}
.container {
  padding-top: 115px;
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  background: #fff;
}
.wrapper {
  position: relative;
  background: #fff;
  padding-bottom: 40rpx;
}
.back-btn {
  position: absolute;
  left: 40rpx;
  z-index: 999;
  padding-top: 25px;
  top: 40rpx;
  font-size: 40rpx;
  color: #333333;
}
.left-top-sign {
  font-size: 120rpx;
  color: #f8f8f8;
  position: relative;
  left: -16rpx;
}
.right-top-sign {
  position: absolute;
  top: 80rpx;
  right: -30rpx;
  
  &:before, &:after {
    display: block;
    content: "";
    width: 400rpx;
    height: 80rpx;
    background: #b4f3e2;
  }
  
  &:before {
    transform: rotate(50deg);
    border-radius: 0 50px 0 0;
  }
  
  &:after {
    position: absolute;
    right: -198rpx;
    top: 0;
    transform: rotate(-50deg);
    border-radius: 50px 0 0 0;
    /* background: pink; */
  }
}
.left-bottom-sign {
  position: absolute;
  left: -270rpx;
  bottom: -320rpx;
  border: 100rpx solid #d0d1fd;
  border-radius: 50%;
  padding: 180rpx;
}
.welcome {
  position: relative;
  left: 50rpx;
  top: -90rpx;
  font-size: 46rpx;
  color: #555;
  text-shadow: 1px 0px 1px rgba(0, 0, 0, 0.3);
}
.input-content {
  padding: 0 60rpx;
}
.input-item {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: center;
  padding: 0 30rpx;
  background: #f8f6fc;
  height: 120rpx;
  border-radius: 4px;
  margin-bottom: 50rpx;
  
  &:last-child {
    margin-bottom: 0;
  }
  
  .tit {
    height: 50rpx;
    line-height: 56rpx;
    font-size: 26rpx;
    color: #606266;
  }
  
  input {
    height: 60rpx;
    font-size: 30rpx;
    color: #333333;
    width: 100%;
  }
}
.confirm-btn {
  width: 630rpx;
  height: 86rpx;
  line-height: 86rpx;
  border-radius: 50px;
  border-radius: 8rpx;
  color: #000000;
  border: 2px solid #000000;
  font-size: 30rpx;
  background: linear-gradient(270deg, #C8EBF8 0%, #E2F1F5 100%);
}
.lb_con {
  font-size: 28rpx;
  color: #999;
  padding: 50rpx;
  
  .l_c_txt {
    color: #0088FF;
    
    &:active {
      opacity: 0.8;
    }
  }
}
.login-popup {
  position: fixed;
  top: 10rpx;
  width: 100%;
  height: 100%;
  z-index: 1000;
  
  .mask {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.6);
  }
  
  .popup-content {
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 800rpx;
    background-color: #fff;
    border-radius: 16px 16px 0 0;
    box-shadow: 0 -4px 16px rgba(0, 0, 0, 0.1);
    animation: slideUp 0.3s ease-in-out;
    
    .top-btn {
      border-top-left-radius: 20px; /* 左上角圆角 */
      border-top-right-radius: 20px; /* 右上角圆角 */
      width: 100%;
      height: 112rpx;
      position: relative;
      /* 背景颜色使用线性渐变并加上透明度 */
      background: linear-gradient(90deg, rgba(45, 255, 245, 0.24) 0%, rgba(255, 248, 120, 0.24) 100%);
      
      .close-btn {
        position: absolute;
        top: 30rpx;
        right: 30rpx;
        width: 48rpx;
        height: 48rpx;
      }
    }
    
    .content {
      padding: 20px;
      
      .avatar-container {
        display: flex;
        padding: 0 120rpx;
        height: 120rpx;
        justify-content: center;
        margin-bottom: 30rpx;
        background: #FFFFFF;
      }
      
      .avatar {
        width: 120rpx;
        height: 120rpx;
        object-fit: cover;
        border-radius: 50%;
      }
      
      .nickname-wrapper {
        display: flex;
        flex: 1;
        align-items: center;
        justify-content: space-between;
        gap: 10px;
        height: 60rpx;
        width: 630rpx;
        margin: 80rpx auto;
        padding-bottom: 30rpx;
        border-bottom: 1rpx solid #BBBBBB;
        
        .nickname-item {
          width: 20%;
          height: 80rpx;
          line-height: 80rpx;
          color: #000000;
          font-size: 28rpx;
        }
        
        .nickname-input {
          width: 80%;
          height: 80rpx;
          line-height: 80rpx;
          padding: 0 10rpx;
          font-size: 28rpx;
          box-sizing: border-box;
          
          &::placeholder {
            color: #999999;
          }
        }
      }
      
      .submit-btn {
        width: 630rpx;
        height: 88rpx;
        padding: 12px;
        margin-bottom: 32rpx;
        border: none;
        border-radius: 8rpx;
        background: linear-gradient(270deg, #C8EBF8 0%, #E2F1F5 100%);
        color: #000000;
        font-size: 30rpx;
        cursor: pointer;
        border: 2rpx solid #000000;
      }
    }
  }
}

@keyframes slideUp {
  from {
    transform: translateY(100%);
  }
  to {
    transform: translateY(0%);
  }
}

.popup {
  position: fixed;
  bottom: 50rpx;
  top: 180rpx;
  width: 100%;
  height: 100%;
  z-index: l00;
  margin: 0 auto;
  
  .agreement-popup {
    width: 700rpx;
    height: 1200rpx;
    background-color: #fff;
    border-radius: 20rpx;
    display: flex;
    flex-direction: column; /* 垂直排列内容 */
    overflow: hidden; /* 隐藏超出的部分 */
    z-index: l00;
  }
  
  .agreement-content {
    padding: 40rpx;
    flex: 1; /* 让内容部分占据大部分空间 */
    overflow-y: auto; /* 让内容部分可滚动 */
    
    .agreement-title {
      font-size: 36rpx;
      font-weight: bold;
      text-align: center;
      margin-bottom: 20rpx;
    }
    
    .agreement-date {
      font-size: 24rpx;
      color: #999;
      text-align: center;
      margin-bottom: 40rpx;
    }
    
    .agreement-section {
      margin-bottom: 30rpx;
      
      .section-title {
        font-size: 32rpx;
        font-weight: bold;
        margin-bottom: 20rpx;
        color: #333;
      }
      
      .section-content {
        font-size: 28rpx;
        color: #666;
        line-height: 1.6;
        margin-bottom: 20rpx;
      }
      
      .section-subcontent {
        font-size: 28rpx;
        color: #666;
        line-height: 1.6;
        padding-left: 20rpx;
        
        view {
          margin-bottom: 16rpx;
        }
      }
    }
    
    .agreement-footer {
      text-align: right;
      margin-top: 40rpx;
      
      .company-name {
        font-weight: bold;
        font-size: 28rpx;
        margin-bottom: 10rpx;
      }
      
      .sign-date {
        font-size: 24rpx;
        color: #999;
      }
    }
  }
  
  .popup-btn {
    width: 240rpx;
    height: 80rpx;
    line-height: 80rpx;
    text-align: center;
    background-color: #0052d9;
    color: #fff;
    font-size: 28rpx;
    border-radius: 40rpx;
    margin: 0 auto;
    box-shadow: 0 6rpx 16rpx rgba(0, 82, 217, 0.2);
    transition: all 0.2s ease;
    
    &:active {
      opacity: 0.9;
      transform: translateY(2rpx);
      box-shadow: 0 2rpx 8rpx rgba(0, 82, 217, 0.2);
    }
  }
}

.agreement {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 20px;
  padding: 10px 0;
}

.checkbox {
  margin-right: 8px;
  width: 20px;
  height: 20px;
}

.agreement-text {
  font-size: 14px;
  color: #333;
  text-align: center;
}

.l_c_txt {
  color: #0088FF;
  cursor: pointer;
}
</style> 