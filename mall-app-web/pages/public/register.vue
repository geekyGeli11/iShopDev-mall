<template>
  <view class="container">
    <nav-bar 
      :placeholder="true" 
      :back="true" 
      bgColor="transparent" 
      color="#333333"
      @ready="navBarReady"
    ></nav-bar>
    
    <image 
      class="bg-image" 
      src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/register/bg-image.png"
      mode="aspectFill"
    ></image>
    
    <!-- <view class="logo-section">
      <image 
        class="logo-image" 
        src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/guanghengzou_logo.png"
        mode="aspectFit"
      ></image>
    </view> -->
    
    <view class="content">
      <view class="avatar-section">
        <view class="avatar-wrapper">
          <button
            class="avatar-button"
            open-type="chooseAvatar"
            @chooseavatar="onChooseAvatar"
          >
            <view class="avatar">
              <image 
                :src="userInfo.avatar || 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/register/default-avatar.png'" 
                mode="aspectFill"
              ></image>
            </view>
            <view class="avatar-camera">
              <image 
                src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/register/camera-add.png"
                mode="aspectFit"
              ></image>
            </view>
          </button>
        </view>
      </view>
      
      <view class="form-container">
        <!-- 昵称 -->
        <view class="form-item">
          <view class="form-label">昵称<text class="required">*</text></view>
          <view class="form-row">
            <input 
              class="form-input" 
              type="nickname" 
              placeholder="请输入你的昵称" 
              placeholder-class="input-placeholder"
              v-model="userInfo.nickname"
            />
            <view class="form-btn" @tap="getWxUserInfo">微信一键授权</view>
          </view>
          <view class="divider"></view>
        </view>
        
        <!-- 性别 -->
        <view class="form-item">
          <view class="form-label">性别</view>
          <view class="form-row">
            <picker 
              class="form-picker" 
              :value="genderIndex" 
              :range="genderArray"
              @change="onGenderChange"
            >
              <view class="picker-text">{{ genderArray[genderIndex] }}</view>
            </picker>
            <view class="picker-arrow">
              <image 
                class="arrow-icon" 
                src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/register/arrow.png"
                mode="aspectFit"
              ></image>
            </view>
          </view>
          <view class="divider"></view>
        </view>
        
        <!-- 手机号 -->
        <view class="form-item">
          <view class="form-label">手机号<text class="required">*</text></view>
          <view class="form-row">
            <input 
              class="form-input" 
              type="number" 
              maxlength="11" 
              placeholder="请输入手机号码" 
              placeholder-class="input-placeholder"
              v-model="userInfo.phone"
            />
            <button 
              class="form-btn wx-phone-btn" 
              open-type="getPhoneNumber" 
              @getphonenumber="getPhoneNumber"
            >更改微信授权手机号</button>
          </view>
          <view class="divider"></view>
        </view>
        
        <!-- 生日 -->
        <view class="form-item">
          <view class="form-label">生日</view>
          <view class="date-row">
            <view class="date-picker">
              <picker 
                class="form-picker" 
                mode="selector" 
                :value="yearIndex" 
                :range="yearArray"
                @change="onYearChange"
              >
                <view class="picker-text date-text">{{ userInfo.birth_year ? userInfo.birth_year + '年' : '年' }}</view>
              </picker>
              <view class="picker-arrow date-arrow">
                <image 
                  class="arrow-icon" 
                  src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/register/arrow.png"
                  mode="aspectFit"
                ></image>
              </view>
            </view>
            <view class="date-picker">
              <picker 
                class="form-picker" 
                mode="selector" 
                :value="monthIndex" 
                :range="monthArray"
                @change="onMonthChange"
              >
                <view class="picker-text date-text">{{ userInfo.birth_month ? userInfo.birth_month + '月' : '月' }}</view>
              </picker>
              <view class="picker-arrow date-arrow">
                <image 
                  class="arrow-icon" 
                  src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/register/arrow.png"
                  mode="aspectFit"
                ></image>
              </view>
            </view>
          </view>
          <view class="divider"></view>
        </view>
        
        <!-- 协议 -->
        <view class="agreement">
          <view class="checkbox" @tap="toggleAgreement">
            <view class="checkbox-inner" :class="{ 'checked': isAgreed }"></view>
          </view>
          <view class="agreement-text">
            我已阅读、理解并同意<text class="link" @tap="showTerms">《用户协议》</text>及<text class="link" @tap="showPrivacy">《隐私政策》</text>的内容，收集和使用我的个人信息。
          </view>
        </view>
        
        <!-- 提交按钮 -->
        <view class="submit-btn" @tap="submitForm">立即加入</view>
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
import { getPhoneNumber as getWxPhoneNumber, uploadMemberAvatar, loginByPhone } from '@/api/member';
import NavBar from '@/components/nav-bar.vue';
import PrivacyPopup from '@/components/PrivacyPopup.vue';
import TermsPopup from '@/components/TermsPopup.vue';
import { mapMutations } from 'vuex';

export default {
  components: {
    'privacy-popup': PrivacyPopup,
    'terms-popup': TermsPopup,
    'nav-bar': NavBar
  },
  data() {
    return {
      userInfo: {
        avatar: '',
        nickname: '',
        gender: 0,
        phone: '',
        birth_year: '',
        birth_month: ''
      },
      genderArray: ['请选择性别', '男', '女'],
      genderIndex: 0,
      yearArray: [],
      yearIndex: 0,
      monthArray: Array.from({ length: 12 }, (_, i) => i + 1),
      monthIndex: 0,
      isAgreed: false,
      showPrivacyPopup: false,
      showTermsPopup: false,
      avatarFile: null // 保存头像文件
    };
  },
  onLoad() {
    this.initYearArray();
  },
  methods: {
    ...mapMutations(['login']),
    
    // 导航栏加载完成
    navBarReady() {
      console.log('NavBar ready');
    },
    
    // 初始化年份数组
    initYearArray() {
      const currentYear = new Date().getFullYear();
      const startYear = currentYear - 100;
      const years = [];
      
      for (let year = currentYear; year >= startYear; year--) {
        years.push(year);
      }
      
      this.yearArray = years;
    },
    
    // 选择头像 - 微信官方接口
    onChooseAvatar(e) {
      const { avatarUrl } = e.detail;
      this.userInfo.avatar = avatarUrl;
      
      // 转换为文件对象
      uni.downloadFile({
        url: avatarUrl,
        success: (res) => {
          if (res.statusCode === 200) {
            this.avatarFile = {
              path: res.tempFilePath,
              size: 0
            };
          }
        }
      });
    },
    
    // 保留原有的选择头像方法用于调试或其他平台
    chooseImage() {
      // 在微信小程序模式下优先使用 chooseAvatar
      // #ifdef MP-WEIXIN
      console.log('在微信小程序中请点击头像选择');
      return;
      // #endif
      
      // 在其他平台继续使用普通的图片选择
      // #ifndef MP-WEIXIN
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          const tempFilePath = res.tempFilePaths[0];
          this.userInfo.avatar = tempFilePath;
          this.avatarFile = res.tempFiles[0]; // 保存文件对象用于上传
        }
      });
      // #endif
    },
    
    // 统一的token获取函数
    getTokenFromStorage() {
      try {
        const tokenInfo = uni.getStorageSync('tokenInfo');
        if (!tokenInfo) return '';

        const loginData = JSON.parse(tokenInfo);
        if (loginData.token && loginData.tokenHead) {
          return loginData.tokenHead + loginData.token;
        }
        return '';
      } catch (e) {
        console.error('解析tokenInfo失败:', e);
        return '';
      }
    },

    // 上传头像
    async uploadAvatar() {
      if (!this.avatarFile) return Promise.resolve('');

      return new Promise((resolve, reject) => {
        uni.showLoading({ title: '头像上传中...' });

        // 使用uni.uploadFile方法，适合小程序环境
        const uploadTask = uni.uploadFile({
          url: 'https://haojiangzhenhao.hello4am.com/mall-admin' + '/aliyun/oss/upload',
          filePath: this.avatarFile.path || this.userInfo.avatar,
          name: 'file',
          header: {
            'Authorization': this.getTokenFromStorage()
          },
          success: (res) => {
            if (res.statusCode === 200) {
              try {
                const data = JSON.parse(res.data);
                if (data.code === 200) {
                  resolve(data.data || '');
                } else {
                  reject(new Error(data.message || '上传失败'));
                }
              } catch (e) {
                reject(new Error('解析响应失败'));
              }
            } else {
              reject(new Error('上传失败'));
            }
          },
          fail: (err) => {
            reject(err);
          },
          complete: () => {
            uni.hideLoading();
          }
        });
        
        uploadTask.onProgressUpdate((res) => {
          console.log('上传进度', res.progress);
        });
      });
    },
    
    // 获取微信用户信息
    getWxUserInfo() {
      // 微信已废弃 getUserProfile 接口，现在只提示用户
      uni.showToast({
        title: '请直接在上方输入昵称',
        icon: 'none',
        duration: 2000
      });
    },
    
    // 获取微信手机号
    async getPhoneNumber(e) {
      if (e.detail.errMsg === 'getPhoneNumber:ok') {
        const phoneCode = e.detail.code; // 新版接口直接拿 code
        try {
          uni.showLoading({ title: '获取手机号中...' });
          // 使用封装的接口
          const res = await getWxPhoneNumber({ phoneCode });
          if (res.code === 200) {
            this.userInfo.phone = res.data;
            uni.showToast({ title: '获取手机号成功', icon: 'success' });
          } else {
            throw new Error(res.message || '获取手机号失败');
          }
        } catch (error) {
          uni.showToast({ title: error.message, icon: 'none' });
        } finally {
          uni.hideLoading();
        }
      } else {
        uni.showToast({ title: '用户未授权手机号', icon: 'none' });
      }
    },
    
    // 性别选择器变化
    onGenderChange(e) {
      this.genderIndex = e.detail.value;
      // 0: 未选择, 1: 男, 2: 女
      this.userInfo.gender = parseInt(this.genderIndex);
    },
    
    // 年份选择器变化
    onYearChange(e) {
      this.yearIndex = e.detail.value;
      this.userInfo.birth_year = this.yearArray[this.yearIndex];
    },
    
    // 月份选择器变化
    onMonthChange(e) {
      this.monthIndex = e.detail.value;
      this.userInfo.birth_month = this.monthArray[this.monthIndex];
    },
    
    // 切换协议同意状态
    toggleAgreement() {
      this.isAgreed = !this.isAgreed;
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
    
    // 表单提交
    async submitForm() {
      // 表单验证
      if (!this.userInfo.nickname) {
        return uni.showToast({
          title: '请输入昵称',
          icon: 'none'
        });
      }
      
      if (!this.userInfo.phone) {
        return uni.showToast({
          title: '请输入手机号',
          icon: 'none'
        });
      }
      
      if (!/^1\d{10}$/.test(this.userInfo.phone)) {
        return uni.showToast({
          title: '手机号格式不正确',
          icon: 'none'
        });
      }
      
      if (!this.isAgreed) {
        return uni.showToast({
          title: '请阅读并同意用户协议和隐私政策',
          icon: 'none'
        });
      }
      
      try {
        uni.showLoading({
          title: '登录中...'
        });
        
        // 如果有选择新头像，先上传头像
        let avatarUrl = '';
        if (this.avatarFile) {
          avatarUrl = await this.uploadAvatar();
        } else {
          avatarUrl = this.userInfo.avatar;
        }
        
        // 获取微信code
        const loginResult = await new Promise((resolve, reject) => {
          uni.login({
            success: (res) => {
              if (res.code) {
                resolve(res.code);
              } else {
                reject(new Error('微信登录失败'));
              }
            },
            fail: (err) => {
              reject(new Error('微信登录失败'));
            }
          });
        });
        
        // 构建注册/登录数据
        const loginData = {
          code: loginResult, // 微信code
          phone: this.userInfo.phone, // 手机号
          nickname: this.userInfo.nickname, // 昵称
          icon: avatarUrl, // 头像
          gender: this.userInfo.gender, // 性别
          birthday: this.userInfo.birth_year && this.userInfo.birth_month ? 
                    `${this.userInfo.birth_year}-${this.userInfo.birth_month.toString().padStart(2, '0')}-01` : 
                    undefined
        };
        
        // 调用登录接口（包含注册功能）
        const res = await loginByPhone(loginData);
        
        if (res.code === 200) {
          // 登录成功，保存完整的token信息
          const tokenData = res.data;
          const tokenInfo = {
            token: tokenData.token,
            tokenHead: tokenData.tokenHead,
            expiresIn: tokenData.expiresIn,
            loginTime: Date.now(),
            openId: tokenData.openId
          };
          uni.setStorageSync('tokenInfo', JSON.stringify(tokenInfo));
          // 统一只保存tokenInfo，不再保存单独的token

          // 直接保存 member 到本地缓存
          const member = res.data.member;
          uni.setStorageSync('userInfo', member);

          // 更新 store 中的登录状态
          this.login(member);

          // 清除邀请参数（后端已处理）
          uni.removeStorageSync('pendingInvite');

          // 清除 Vuex 中的登录弹窗状态
          this.$store.commit('clearLoginPopup');

          uni.showToast({
            title: '登录成功',
            icon: 'success'
          });

          // 检查是否需要返回特定页面
          const returnPageInfo = uni.getStorageSync('returnPageInfo');
          
          // 延迟跳转
          setTimeout(() => {
            if (returnPageInfo) {
              // 清除返回页面信息
              uni.removeStorageSync('returnPageInfo');
              
              // 构建完整URL并跳转回原页面
              const url = this.buildUrlWithParams(returnPageInfo.url, returnPageInfo.options);
              uni.reLaunch({ url });
            } else {
              // 没有返回页面信息，正常返回上一页
              uni.navigateBack();
            }
          }, 1500);
        } else {
          throw new Error(res.message || '登录失败');
        }
      } catch (error) {
        console.error('登录失败', error);
        uni.showToast({
          title: error.message || '登录失败',
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
      }
    },
    
    // 构建带参数的URL
    buildUrlWithParams(route, options) {
      let url = `/${route}`;
      const params = Object.keys(options || {});
      if (params.length > 0) {
        const queryString = params.map(key => `${key}=${encodeURIComponent(options[key])}`).join('&');
        url += `?${queryString}`;
      }
      return url;
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
  background: #fff;
}
.container {
  position: relative;
  width: 100vw;
  min-height: 100vh;
  overflow: hidden;
}
.bg-image {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -1;
}
.logo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-top: 120rpx;
  margin-bottom: 20rpx;
  
  .logo-image {
    width: 280rpx;
    height: 45rpx;
  }
  
  .version-tag {
    margin-top: 10rpx;
    font-size: 22rpx;
    color: #fff;
    background-color: #3a87ff;
    padding: 2rpx 16rpx;
    border-radius: 8rpx;
  }
}
.content {
  padding: 0 40rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.avatar-section {
  display: flex;
  justify-content: center;
  padding: 20rpx 0 40rpx;
}
.avatar-wrapper {
  position: relative;
  width: 160rpx;
  height: 160rpx;
}
.avatar-button {
  position: relative;
  width: 160rpx;
  height: 160rpx;
  padding: 0;
  margin: 0;
  border: none;
  background: transparent;
  line-height: normal;
  text-align: left;
  box-sizing: border-box;
  overflow: visible;
  font-size: 0; /* 防止可能的文字样式影响 */
  display: block;
  
  &::after {
    border: none;
  }
  
  &:active, &:hover, &:focus {
    background: transparent;
    outline: none;
  }
}
.avatar {
  width: 160rpx;
  height: 160rpx;
  border-radius: 50%;
  overflow: hidden;
  background-color: #ffffff;
  border: 4rpx solid #ffffff;
  
  image {
    width: 100%;
    height: 100%;
  }
}
.avatar-camera {
  position: absolute;
  right: 0;
  bottom: 0;
  z-index: 2;
  width: 46rpx;
  height: 46rpx;
  background: #ffffff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  
  image {
    width: 38rpx;
    height: 38rpx;
  }
}
.form-container {
  width: 100%;
  background-color: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-radius: 20rpx;
  padding: 40rpx 30rpx;
  margin-bottom: 30rpx;
  display: flex;
  flex-direction: column;
}
.form-item {
  position: relative;
  margin-bottom: 30rpx;
}
.form-label {
  font-size: 32rpx;
  font-weight: 500;
  margin-bottom: 20rpx;
  
  .required {
    color: #ff0000;
  }
}
.form-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
}
.form-input {
  font-size: 24rpx;
  padding: 10rpx 0;
  flex: 1;
}
.input-placeholder {
  color: #666;
}
.form-btn {
  font-size: 24rpx;
  color: #000;
  white-space: nowrap;
  margin-left: 20rpx;
}
.wx-phone-btn {
  background: none;
  border: none;
  font-size: 24rpx;
  color: #000;
  white-space: nowrap;
  margin-left: 20rpx;
  padding: 0;
  line-height: 1.5;
  min-height: auto;
  height: auto;
  
  &::after {
    border: none;
  }
}
.divider {
  position: absolute;
  bottom: -10rpx;
  left: 0;
  right: 0;
  height: 1rpx;
  background-color: #999;
  opacity: 0.3;
}
.form-picker {
  width: 100%;
}
.picker-text {
  font-size: 24rpx;
  padding: 10rpx 0;
}
.picker-arrow {
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  
  .arrow-icon {
    width: 24rpx;
    height: 24rpx;
  }
}
.date-row {
  display: flex;
}
.date-picker {
  position: relative;
  flex: 1;
  margin-right: 40rpx;
  
  &:last-child {
    margin-right: 0;
  }
}
.date-text {
  color: #000;
}
.date-arrow {
  right: 10rpx;
}
.agreement {
  display: flex;
  align-items: flex-start;
  padding: 30rpx 0;
  width: 100%;
}
.checkbox {
  width: 30rpx;
  height: 30rpx;
  border-radius: 50%;
  border: 1rpx solid #000;
  margin-right: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 4rpx;
  flex-shrink: 0;
}
.checkbox-inner {
  width: 20rpx;
  height: 20rpx;
  border-radius: 50%;
  background-color: transparent;
  
  &.checked {
    background-color: #3a87ff;
  }
}
.agreement-text {
  flex: 1;
  font-size: 24rpx;
  line-height: 1.4;
}
.link {
  color: #3a87ff;
}
.submit-btn {
  width: 100%;
  height: 80rpx;
  line-height: 80rpx;
  text-align: center;
  background: #20201E;
  color: #A9FF00;
  border-radius: 40rpx;
  font-size: 28rpx;
  font-weight: 500;
  margin-top: 20rpx;
  margin-bottom: 10rpx;
}
</style> 