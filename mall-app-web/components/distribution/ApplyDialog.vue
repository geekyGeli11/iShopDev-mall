<template>
  <view v-if="visible" class="apply-dialog">
    <!-- 遮罩层 -->
    <view class="dialog-mask" @tap="handleMaskTap"></view>
    
    <!-- 弹窗内容 -->
    <view class="dialog-content">
      <!-- 头部 -->
      <view class="dialog-header">
        <view class="header-title">申请成为分销商</view>
        <view class="header-close" @tap="handleClose">
          <text class="close-icon">✕</text>
        </view>
      </view>
      
      <!-- 表单区域 -->
      <scroll-view class="dialog-body" scroll-y>
        <form @submit="handleSubmit">
          <!-- 基本信息 -->
          <view class="form-section">
            <view class="section-title">基本信息</view>
            
            <view class="form-item">
              <view class="item-label required">真实姓名</view>
              <input 
                class="item-input" 
                v-model="formData.realName"
                placeholder="请输入真实姓名"
                maxlength="20"
              />
            </view>
            
            <view class="form-item">
              <view class="item-label required">身份证号</view>
              <input 
                class="item-input" 
                v-model="formData.idCard"
                placeholder="请输入身份证号码"
                maxlength="18"
              />
            </view>
            
            <view class="form-item">
              <view class="item-label required">手机号码</view>
              <input 
                class="item-input" 
                v-model="formData.phoneNumber"
                placeholder="请输入手机号码"
                type="number"
                maxlength="11"
              />
            </view>
            
            <view class="form-item">
              <view class="item-label required">邮箱地址</view>
              <input 
                class="item-input" 
                v-model="formData.email"
                placeholder="请输入邮箱地址"
                type="email"
              />
            </view>
          </view>
          
          <!-- 申请理由 -->
          <view class="form-section">
            <view class="section-title">申请理由</view>
            
            <view class="form-item">
              <view class="item-label required">申请原因</view>
              <textarea 
                class="item-textarea" 
                v-model="formData.applyReason"
                placeholder="请详细说明您申请成为分销商的原因和优势（不少于50字）"
                maxlength="500"
                show-confirm-bar
              ></textarea>
              <view class="char-count">{{ formData.applyReason.length }}/500</view>
            </view>
          </view>
          
          <!-- 协议确认 -->
          <view class="form-section">
            <view class="agreement-section">
              <checkbox-group @change="handleAgreementChange">
                <label class="agreement-item">
                  <checkbox :checked="formData.agreeTerms" />
                  <text class="agreement-text">
                    我已阅读并同意
                    <text class="agreement-link" @tap="viewAgreement">《分销商协议》</text>
                  </text>
                </label>
              </checkbox-group>
            </view>
          </view>
          
        </form>
      </scroll-view>
      
      <!-- 提交按钮 -->
      <view class="dialog-footer">
        <button class="cancel-btn" @tap="handleClose">取消</button>
        <button 
          class="submit-btn"
          :class="{ disabled: !canSubmit }"
          :disabled="!canSubmit"
          @tap="handleSubmit"
        >
          {{ submitText }}
        </button>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'ApplyDialog',
  
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  
  data() {
    return {
      formData: {
        realName: '',
        idCard: '',
        phoneNumber: '',
        email: '',
        applyReason: '',
        agreeTerms: false
      },
      submitting: false
    }
  },
  
  computed: {
    canSubmit() {
      return this.formData.realName &&
             this.formData.idCard &&
             this.formData.phoneNumber &&
             this.formData.email &&
             this.formData.applyReason.length >= 50 &&
             this.formData.agreeTerms &&
             !this.submitting;
    },
    
    submitText() {
      return this.submitting ? '提交中...' : '提交申请';
    }
  },
  
  watch: {
    visible(newVal) {
      if (!newVal) {
        // 弹窗关闭时重置表单
        this.resetForm();
      }
    }
  },
  
  methods: {
    handleClose() {
      this.$emit('close');
    },
    
    handleMaskTap() {
      this.handleClose();
    },
    
    handleAgreementChange(e) {
      this.formData.agreeTerms = e.detail.value.length > 0;
    },
    
    viewAgreement() {
      uni.navigateTo({
        url: '/pages/user/distribution-agreement'
      });
    },
    
    validateForm() {
      // 验证真实姓名
      if (!this.formData.realName.trim()) {
        throw new Error('请输入真实姓名');
      }
      
      if (this.formData.realName.length < 2) {
        throw new Error('真实姓名至少2个字符');
      }
      
      // 验证身份证号
      const idCardReg = /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/;
      if (!idCardReg.test(this.formData.idCard)) {
        throw new Error('请输入正确的身份证号码');
      }
      
      // 验证手机号
      const phoneReg = /^1[3-9]\d{9}$/;
      if (!phoneReg.test(this.formData.phoneNumber)) {
        throw new Error('请输入正确的手机号码');
      }
      
      // 验证邮箱
      const emailReg = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
      if (!emailReg.test(this.formData.email)) {
        throw new Error('请输入正确的邮箱地址');
      }
      
      // 验证申请理由
      if (this.formData.applyReason.length < 50) {
        throw new Error('申请理由不能少于50字');
      }
      
      // 验证协议
      if (!this.formData.agreeTerms) {
        throw new Error('请先同意分销商协议');
      }
    },
    
    async handleSubmit() {
      if (!this.canSubmit) return;
      
      try {
        this.validateForm();
        
        this.submitting = true;
        
        // 提交表单数据
        this.$emit('submit', this.formData);
        
      } catch (error) {
        uni.showToast({
          title: error.message,
          icon: 'none',
          duration: 2000
        });
      } finally {
        this.submitting = false;
      }
    },
    
    // 重置表单
    resetForm() {
      this.formData = {
        realName: '',
        idCard: '',
        phoneNumber: '',
        email: '',
        applyReason: '',
        agreeTerms: false
      };
      this.submitting = false;
    }
  }
}
</script>

<style lang="scss" scoped>
.apply-dialog {
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
  z-index: 1;
}

.dialog-content {
  position: relative;
  z-index: 2;
  width: 100%;
  max-height: 80vh;
  background: #FFFFFF;
  border-radius: 20rpx 20rpx 0 0;
  display: flex;
  flex-direction: column;
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from {
    transform: translateY(100%);
  }
  to {
    transform: translateY(0);
  }
}

.dialog-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx 40rpx;
  border-bottom: 2rpx solid #F5F5F5;
  
  .header-title {
    font-size: 36rpx;
    font-weight: bold;
    color: #000000;
  }
  
  .header-close {
    width: 60rpx;
    height: 60rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #F8F9FA;
    border-radius: 50%;
    
    .close-icon {
      font-size: 32rpx;
      color: #666666;
    }
  }
}

.dialog-body {
  flex: 1;
  padding: 0 40rpx;
  overflow-y: auto;
}

.form-section {
  margin-bottom: 40rpx;
  
  .section-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #000000;
    margin-bottom: 32rpx;
  }
}

.form-item {
  margin-bottom: 32rpx;
  
  .item-label {
    font-size: 28rpx;
    color: #333333;
    margin-bottom: 16rpx;
    font-weight: 500;
    
    &.required::after {
      content: '*';
      color: #FF3B30;
      margin-left: 8rpx;
    }
  }
  
  .item-input {
    width: 100%;
    height: 88rpx;
    padding: 0 24rpx;
    border: 2rpx solid #E9ECEF;
    border-radius: 12rpx;
    font-size: 28rpx;
    color: #333333;
    background: #F8F9FA;
    box-sizing: border-box;
    
    &::placeholder {
      color: #999999;
    }
    
    &:focus {
      border-color: #A9FF00;
      background: #FFFFFF;
    }
  }
  
  .item-textarea {
    width: 100%;
    min-height: 180rpx;
    padding: 20rpx 24rpx;
    border: 2rpx solid #E9ECEF;
    border-radius: 12rpx;
    font-size: 28rpx;
    color: #333333;
    background: #F8F9FA;
    box-sizing: border-box;
    
    &::placeholder {
      color: #999999;
    }
    
    &:focus {
      border-color: #A9FF00;
      background: #FFFFFF;
    }
  }
  
  .char-count {
    text-align: right;
    font-size: 24rpx;
    color: #999999;
    margin-top: 8rpx;
  }
}

.agreement-section {
  .agreement-item {
    display: flex;
    align-items: flex-start;
    
    checkbox {
      margin-right: 16rpx;
      margin-top: 6rpx;
      transform: scale(0.8);
    }
    
    .agreement-text {
      flex: 1;
      font-size: 26rpx;
      color: #666666;
      line-height: 1.6;
      
      .agreement-link {
        color: #A9FF00;
        text-decoration: underline;
      }
    }
  }
}

.dialog-footer {
  display: flex;
  gap: 24rpx;
  padding: 32rpx 40rpx;
  border-top: 2rpx solid #F5F5F5;
  
  .cancel-btn, .submit-btn {
    flex: 1;
    height: 88rpx;
    border-radius: 44rpx;
    border: none;
    font-size: 32rpx;
    font-weight: 500;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .cancel-btn {
    background: #F8F9FA;
    color: #666666;
    
    &::after {
      border: none;
    }
  }
  
  .submit-btn {
    background: #A9FF00;
    color: #000000;
    
    &.disabled {
      background: #CCCCCC;
      color: #999999;
    }
    
    &::after {
      border: none;
    }
  }
}
</style> 