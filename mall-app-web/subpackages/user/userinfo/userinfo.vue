<template>
  <view>
    <view class="user-section">
      <image class="bg" :src="userInfo.bgImage || 'https://haojiang-1329192580.cos.ap-guangzhou.myqcloud.com/static/user-bg.jpg'"></image>
      <text class="bg-upload-btn yticon icon-paizhao" @tap="uploadBackground"></text>
      <view class="portrait-box">
        <image class="portrait" :src="userInfo.portrait || 'https://haojiang-1329192580.cos.ap-guangzhou.myqcloud.com/static/missing-face.png'"></image>
        <text class="pt-upload-btn yticon icon-paizhao" @tap="uploadAvatar"></text>
      </view>
    </view>
    
    <view class="info-section">
      <view class="info-item">
        <text class="item-title">昵称</text>
        <input class="item-input" type="text" v-model="userInfo.nickname" placeholder="请输入昵称" />
      </view>
      
      <view class="info-item">
        <text class="item-title">性别</text>
        <view class="gender-options">
          <view 
            class="gender-option" 
            :class="{'selected': userInfo.gender === 1}"
            @tap="selectGender(1)"
          >
            <text>男</text>
          </view>
          <view 
            class="gender-option" 
            :class="{'selected': userInfo.gender === 2}"
            @tap="selectGender(2)"
          >
            <text>女</text>
          </view>
        </view>
      </view>
      
      <view class="info-item">
        <text class="item-title">手机号</text>
        <input class="item-input" type="number" maxlength="11" v-model="userInfo.phone" placeholder="请输入手机号" />
      </view>
      
      <view class="info-item">
        <text class="item-title">个性签名</text>
        <textarea 
          class="item-textarea" 
          v-model="userInfo.signature" 
          placeholder="请输入个性签名"
          maxlength="100"
        ></textarea>
      </view>
    </view>
    
    <view class="save-btn" @tap="saveUserInfo">
      <text>保存</text>
    </view>
  </view>
</template>

<script>
import { memberInfo, uploadMemberAvatar, updateMemberInfo, uploadBackgroundImage } from '@/api/member';

export default {
  data() {
    return {
      userInfo: {
        portrait: '',
        bgImage: '',
        nickname: '',
        gender: 1,
        phone: '',
        signature: ''
      }
    };
  },
  onLoad() {
    this.getUserInfo();
  },
  methods: {
    /**
     * 获取用户信息
     */
    getUserInfo() {
      uni.showLoading({
        title: '加载中...'
      });
      
      memberInfo().then(res => {
        if (res.code === 200 && res.data) {
          this.userInfo = {
            portrait: res.data.icon || '',
            bgImage: res.data.backgroundImg || '',
            nickname: res.data.username || '',
            gender: res.data.gender || 1,
            phone: res.data.phone || '',
            signature: res.data.bio || ''
          };
        }
      }).catch(err => {
        uni.showToast({
          title: '获取用户信息失败',
          icon: 'none'
        });
      }).finally(() => {
        uni.hideLoading();
      });
    },
    
    /**
     * 上传头像
     */
    uploadAvatar() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          const tempFilePath = res.tempFilePaths[0];
          
          uni.showLoading({
            title: '上传中...'
          });
          
          // 上传到服务器
          const uploadTask = uni.uploadFile({
            url: '/member/uploadAvatar', // 请求路径与API接口中一致
            filePath: tempFilePath,
            name: 'file',
            formData: {},
            success: (uploadRes) => {
              let result;
              try {
                if (typeof uploadRes.data === 'string') {
                  result = JSON.parse(uploadRes.data);
                } else {
                  result = uploadRes.data;
                }
                
                if (result.code === 200 && result.data) {
                  this.userInfo.portrait = result.data;
                  uni.showToast({
                    title: '头像已更新',
                    icon: 'success'
                  });
                } else {
                  uni.showToast({
                    title: result.message || '上传失败',
                    icon: 'none'
                  });
                }
              } catch (e) {
                uni.showToast({
                  title: '上传失败',
                  icon: 'none'
                });
              }
            },
            fail: () => {
              uni.showToast({
                title: '上传失败',
                icon: 'none'
              });
            },
            complete: () => {
              uni.hideLoading();
            }
          });
        }
      });
    },
    
    /**
     * 上传背景图
     */
    uploadBackground() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          const tempFilePath = res.tempFilePaths[0];
          
          uni.showLoading({
            title: '上传中...'
          });
          
          // 上传到服务器
          const uploadTask = uni.uploadFile({
            url: '/member/uploadBackground', // 请求路径与API接口中一致
            filePath: tempFilePath,
            name: 'file',
            formData: {},
            success: (uploadRes) => {
              let result;
              try {
                if (typeof uploadRes.data === 'string') {
                  result = JSON.parse(uploadRes.data);
                } else {
                  result = uploadRes.data;
                }
                
                if (result.code === 200 && result.data) {
                  this.userInfo.bgImage = result.data;
                  uni.showToast({
                    title: '背景已更新',
                    icon: 'success'
                  });
                } else {
                  uni.showToast({
                    title: result.message || '上传失败',
                    icon: 'none'
                  });
                }
              } catch (e) {
                uni.showToast({
                  title: '上传失败',
                  icon: 'none'
                });
              }
            },
            fail: () => {
              uni.showToast({
                title: '上传失败',
                icon: 'none'
              });
            },
            complete: () => {
              uni.hideLoading();
            }
          });
        }
      });
    },
    
    /**
     * 选择性别
     */
    selectGender(gender) {
      this.userInfo.gender = gender;
    },
    
    /**
     * 保存用户信息
     */
    saveUserInfo() {
      // 表单验证
      if (!this.userInfo.nickname) {
        return uni.showToast({
          title: '请输入昵称',
          icon: 'none'
        });
      }
      
      if (this.userInfo.phone && !/^1\d{10}$/.test(this.userInfo.phone)) {
        return uni.showToast({
          title: '手机号格式不正确',
          icon: 'none'
        });
      }
      
      // 构建请求参数
      const params = {
        username: this.userInfo.nickname,
        gender: this.userInfo.gender,
        phone: this.userInfo.phone,
        bio: this.userInfo.signature,
        icon: this.userInfo.portrait,
        backgroundImg: this.userInfo.bgImage
      };
      
      uni.showLoading({
        title: '保存中...'
      });
      
      // 调用接口保存用户信息
      updateMemberInfo(params).then(res => {
        if (res.code === 200) {
          uni.showToast({
            title: '保存成功',
            icon: 'success'
          });
          
          // 更新本地缓存
          const userInfo = uni.getStorageSync('userInfo') || {};
          userInfo.username = this.userInfo.nickname;
          userInfo.gender = this.userInfo.gender;
          userInfo.phone = this.userInfo.phone;
          userInfo.bio = this.userInfo.signature;
          userInfo.icon = this.userInfo.portrait;
          userInfo.backgroundImg = this.userInfo.bgImage;
          uni.setStorageSync('userInfo', userInfo);
          
          setTimeout(() => {
            uni.navigateBack();
          }, 1500);
        } else {
          uni.showToast({
            title: res.message || '保存失败',
            icon: 'none'
          });
        }
      }).catch(() => {
        uni.showToast({
          title: '保存失败',
          icon: 'none'
        });
      }).finally(() => {
        uni.hideLoading();
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
  background: #f8f8f8;
}
.user-section {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 460rpx;
  padding: 40rpx 30rpx 0;
  position: relative;
  
  .bg {
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    filter: blur(1px);
    opacity: 0.7;
  }
  
  .portrait-box {
    width: 200rpx;
    height: 200rpx;
    border: 6rpx solid #fff;
    border-radius: 50%;
    position: relative;
    z-index: 2;
  }
  
  .portrait {
    position: relative;
    width: 100%;
    height: 100%;
    border-radius: 50%;
  }
  
  .yticon {
    position: absolute;
    line-height: 1;
    z-index: 5;
    font-size: 48rpx;
    color: #fff;
    padding: 4rpx 6rpx;
    border-radius: 6rpx;
    background: rgba(0, 0, 0, 0.4);
  }
  
  .pt-upload-btn {
    right: 0;
    bottom: 10rpx;
  }
  
  .bg-upload-btn {
    right: 20rpx;
    bottom: 16rpx;
  }
}

.info-section {
  padding: 20rpx 30rpx;
  background-color: #fff;
  margin-top: 20rpx;
  
  .info-item {
    padding: 30rpx 0;
    border-bottom: 1rpx solid #f8f8f8;
    
    &:last-child {
      border-bottom: none;
    }
    
    .item-title {
      font-size: 30rpx;
      color: #303133;
      margin-bottom: 20rpx;
    }
    
    .item-input {
      height: 70rpx;
      font-size: 28rpx;
      color: #303133;
      padding: 0 20rpx;
      background-color: #f8f8f8;
      border-radius: 10rpx;
    }
    
    .item-textarea {
      width: 100%;
      height: 200rpx;
      font-size: 28rpx;
      color: #303133;
      padding: 20rpx;
      background-color: #f8f8f8;
      border-radius: 10rpx;
      box-sizing: border-box;
    }
    
    .gender-options {
      display: flex;
      margin-top: 20rpx;
      
      .gender-option {
        width: 180rpx;
        height: 70rpx;
        display: flex;
        justify-content: center;
        align-items: center;
        margin-right: 30rpx;
        background-color: #f8f8f8;
        border-radius: 35rpx;
        
        &.selected {
          background-color: #FA436A;
          
          text {
            color: #fff;
          }
        }
        
        text {
          font-size: 28rpx;
          color: #303133;
        }
      }
    }
  }
}

.save-btn {
  position: fixed;
  bottom: 50rpx;
  left: 50%;
  transform: translateX(-50%);
  width: 90%;
  height: 90rpx;
  line-height: 90rpx;
  text-align: center;
  background-color: #FA436A;
  color: #fff;
  font-size: 32rpx;
  border-radius: 45rpx;
  box-shadow: 0 10rpx 20rpx rgba(250, 67, 106, 0.2);
}
</style> 