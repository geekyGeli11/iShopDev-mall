<template>
  <view v-if="visible" class="step-apply-dialog">
    <view class="dialog-mask" @tap="handleClose"></view>
    <view class="dialog-content">
      <!-- 头部 -->
      <view class="dialog-header">
        <text class="dialog-title">申请成为文创团长</text>
        <view class="close-btn" @tap="handleClose">
          <text class="close-icon">×</text>
        </view>
      </view>
      
      <!-- 步骤指示器 -->
      <view class="step-indicator">
        <view 
          v-for="(step, index) in steps" 
          :key="index"
          class="step-item"
          :class="{ active: currentStep === index + 1, completed: currentStep > index + 1 }"
        >
          <view class="step-number">{{ index + 1 }}</view>
          <text class="step-title">{{ step.title }}</text>
        </view>
      </view>
      
      <!-- 步骤内容 -->
      <view class="step-content">
        <!-- 步骤1：会员验证 -->
        <view v-if="currentStep === 1" class="step-form">
          <view class="form-title">会员验证</view>
          <view class="verification-list">
            <view class="verification-item" :class="{ passed: eligibility.isMember }">
              <text class="verification-text">已注册成为会员</text>
              <text class="verification-status">{{ eligibility.isMember ? '✓' : '×' }}</text>
            </view>
            <view class="verification-item" :class="{ passed: eligibility.hasPhone }">
              <text class="verification-text">已绑定手机号</text>
              <text class="verification-status">{{ eligibility.hasPhone ? '✓' : '×' }}</text>
            </view>
            <view class="verification-item" :class="{ passed: eligibility.hasEnoughPoints }">
              <text class="verification-text">小程序积分满200</text>
              <text class="verification-status">{{ eligibility.hasEnoughPoints ? '✓' : '×' }}</text>
            </view>
          </view>
          <view v-if="!allEligibilityPassed" class="tips">
            <text class="tips-text">请先完善以上条件后再申请</text>
            <button class="complete-btn" @tap="handleCompleteConditions">去完善</button>
          </view>
        </view>
        
        <!-- 步骤2：个人介绍完善 -->
        <view v-if="currentStep === 2" class="step-form">
          <view class="form-title">个人介绍完善</view>
          <view class="form-group">
            <text class="form-label">自我介绍</text>
            <textarea 
              class="form-textarea"
              v-model="formData.selfIntroduction"
              placeholder="请介绍您的个人背景、推广经验等"
              maxlength="500"
            ></textarea>
            <text class="char-count">{{ formData.selfIntroduction.length }}/500</text>
          </view>
          <view class="form-group">
            <text class="form-label">私域影响力截图</text>
            <view class="upload-area">
              <view 
                v-for="(image, index) in formData.influenceScreenshots" 
                :key="index"
                class="image-item"
              >
                <image :src="image" mode="aspectFill"></image>
                <view class="delete-btn" @tap="removeImage(index)">×</view>
              </view>
              <view v-if="formData.influenceScreenshots.length < 3" class="upload-btn" @tap="uploadInfluenceImage">
                <text class="upload-text">+</text>
              </view>
            </view>
            <text class="upload-tips">最多上传3张图片，展示您的私域影响力</text>
          </view>
        </view>
        
        <!-- 步骤3：身份认证 -->
        <view v-if="currentStep === 3" class="step-form">
          <view class="form-title">身份认证</view>
          <view class="form-group">
            <text class="form-label">本人手机号</text>
            <text class="phone-display">{{ userInfo.phone || '未绑定' }}</text>
          </view>
          <view class="form-group">
            <text class="form-label">身份证正面</text>
            <view class="id-card-upload">
              <view v-if="formData.idCardFront" class="id-card-preview">
                <image :src="formData.idCardFront" mode="aspectFit"></image>
                <view class="delete-btn" @tap="formData.idCardFront = ''">×</view>
              </view>
              <view v-else class="upload-placeholder" @tap="uploadIdCardFront">
                <text class="upload-text">点击上传身份证正面</text>
              </view>
            </view>
          </view>
          <view class="form-group">
            <text class="form-label">身份证反面</text>
            <view class="id-card-upload">
              <view v-if="formData.idCardBack" class="id-card-preview">
                <image :src="formData.idCardBack" mode="aspectFit"></image>
                <view class="delete-btn" @tap="formData.idCardBack = ''">×</view>
              </view>
              <view v-else class="upload-placeholder" @tap="uploadIdCardBack">
                <text class="upload-text">点击上传身份证反面</text>
              </view>
            </view>
          </view>
          <view class="form-group">
            <text class="form-label">其他身份证明（可选）</text>
            <view class="upload-area">
              <view 
                v-for="(image, index) in formData.otherCertificates" 
                :key="index"
                class="image-item"
              >
                <image :src="image" mode="aspectFill"></image>
                <view class="delete-btn" @tap="removeOtherCertificate(index)">×</view>
              </view>
              <view v-if="formData.otherCertificates.length < 3" class="upload-btn" @tap="uploadOtherCertificate">
                <text class="upload-text">+</text>
              </view>
            </view>
            <text class="upload-tips">如营业执照、资质证书等，最多3张</text>
          </view>
        </view>
      </view>
      
      <!-- 底部按钮 -->
      <view class="dialog-footer">
        <button v-if="currentStep > 1" class="btn btn-secondary" @tap="prevStep">上一步</button>
        <button 
          v-if="currentStep < 3" 
          class="btn btn-primary" 
          :disabled="!canNextStep"
          @tap="nextStep"
        >下一步</button>
        <button 
          v-if="currentStep === 3" 
          class="btn btn-primary" 
          :disabled="!canSubmit"
          @tap="handleSubmit"
        >提交申请</button>
      </view>
    </view>
  </view>
</template>

<script>
import { checkApplyEligibility } from '@/api/distribution'

export default {
  name: 'StepApplyDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    userInfo: {
      type: Object,
      default: () => ({})
    }
  },
  
  data() {
    return {
      currentStep: 1,
      steps: [
        { title: '会员验证' },
        { title: '个人介绍' },
        { title: '身份认证' }
      ],
      eligibility: {
        isMember: false,
        hasPhone: false,
        hasEnoughPoints: false
      },
      formData: {
        selfIntroduction: '',
        influenceScreenshots: [],
        idCardFront: '',
        idCardBack: '',
        otherCertificates: []
      }
    }
  },
  
  computed: {
    allEligibilityPassed() {
      return this.eligibility.isMember && this.eligibility.hasPhone && this.eligibility.hasEnoughPoints
    },
    
    canNextStep() {
      if (this.currentStep === 1) {
        return this.allEligibilityPassed
      }
      if (this.currentStep === 2) {
        return this.formData.selfIntroduction.trim().length > 0
      }
      return true
    },
    
    canSubmit() {
      return this.formData.idCardFront && this.formData.idCardBack
    }
  },
  
  watch: {
    visible(newVal) {
      if (newVal) {
        this.checkEligibility()
        this.resetForm()
      }
    }
  },
  
  methods: {
    async checkEligibility() {
      try {
        const res = await checkApplyEligibility()
        if (res.code === 200) {
          this.eligibility = res.data
        }
      } catch (error) {
        console.error('检查申请资格失败:', error)
      }
    },
    
    resetForm() {
      this.currentStep = 1
      this.formData = {
        selfIntroduction: '',
        influenceScreenshots: [],
        idCardFront: '',
        idCardBack: '',
        otherCertificates: []
      }
    },
    
    handleClose() {
      this.$emit('close')
    },
    
    handleCompleteConditions() {
      // 跳转到相应页面完善条件
      uni.showToast({
        title: '请先完善申请条件',
        icon: 'none'
      })
    },
    
    nextStep() {
      if (this.canNextStep && this.currentStep < 3) {
        this.currentStep++
      }
    },
    
    prevStep() {
      if (this.currentStep > 1) {
        this.currentStep--
      }
    },
    
    // 图片上传相关方法
    uploadInfluenceImage() {
      uni.chooseImage({
        count: 3 - this.formData.influenceScreenshots.length,
        success: (res) => {
          // 这里应该上传到服务器，暂时使用本地路径
          this.formData.influenceScreenshots.push(...res.tempFilePaths)
        }
      })
    },
    
    removeImage(index) {
      this.formData.influenceScreenshots.splice(index, 1)
    },
    
    uploadIdCardFront() {
      uni.chooseImage({
        count: 1,
        success: (res) => {
          this.formData.idCardFront = res.tempFilePaths[0]
        }
      })
    },
    
    uploadIdCardBack() {
      uni.chooseImage({
        count: 1,
        success: (res) => {
          this.formData.idCardBack = res.tempFilePaths[0]
        }
      })
    },
    
    uploadOtherCertificate() {
      uni.chooseImage({
        count: 3 - this.formData.otherCertificates.length,
        success: (res) => {
          this.formData.otherCertificates.push(...res.tempFilePaths)
        }
      })
    },
    
    removeOtherCertificate(index) {
      this.formData.otherCertificates.splice(index, 1)
    },
    
    handleSubmit() {
      if (!this.canSubmit) {
        uni.showToast({
          title: '请完善必填信息',
          icon: 'none'
        })
        return
      }
      
      const submitData = {
        selfIntroduction: this.formData.selfIntroduction,
        influenceScreenshots: JSON.stringify(this.formData.influenceScreenshots),
        idCardFront: this.formData.idCardFront,
        idCardBack: this.formData.idCardBack,
        otherCertificates: JSON.stringify(this.formData.otherCertificates),
        applyStep: 4 // 已完成所有步骤
      }
      
      this.$emit('submit', submitData)
    }
  }
}
</script>

<style lang="scss" scoped>
.step-apply-dialog {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.dialog-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
}

.dialog-content {
  position: relative;
  width: 90%;
  max-width: 600rpx;
  max-height: 80vh;
  background: #FFFFFF;
  border-radius: 20rpx;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1rpx solid #E5E5E5;
}

.dialog-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #000;
}

.close-btn {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #F5F5F5;
  border-radius: 50%;
}

.close-icon {
  font-size: 40rpx;
  color: #999;
}

.step-indicator {
  display: flex;
  justify-content: space-between;
  padding: 30rpx;
  background: #F8F9FA;
}

.step-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  position: relative;
}

.step-item:not(:last-child)::after {
  content: '';
  position: absolute;
  top: 20rpx;
  right: -50%;
  width: 100%;
  height: 2rpx;
  background: #E5E5E5;
  z-index: 1;
}

.step-item.active::after,
.step-item.completed::after {
  background: #A9FF00;
}

.step-number {
  width: 40rpx;
  height: 40rpx;
  border-radius: 50%;
  background: #E5E5E5;
  color: #999;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  font-weight: bold;
  margin-bottom: 10rpx;
  position: relative;
  z-index: 2;
}

.step-item.active .step-number {
  background: #A9FF00;
  color: #000;
}

.step-item.completed .step-number {
  background: #A9FF00;
  color: #000;
}

.step-title {
  font-size: 24rpx;
  color: #666;
}

.step-item.active .step-title {
  color: #000;
  font-weight: bold;
}

.step-content {
  flex: 1;
  padding: 30rpx;
  overflow-y: auto;
}

.form-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #000;
  margin-bottom: 30rpx;
}

.verification-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
  margin-bottom: 30rpx;
}

.verification-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx;
  background: #F8F9FA;
  border-radius: 10rpx;
  border: 2rpx solid #E5E5E5;
}

.verification-item.passed {
  background: #E5FDC0;
  border-color: #A9FF00;
}

.verification-text {
  font-size: 28rpx;
  color: #333;
}

.verification-status {
  font-size: 32rpx;
  font-weight: bold;
}

.verification-item.passed .verification-status {
  color: #A9FF00;
}

.verification-item:not(.passed) .verification-status {
  color: #FF3B30;
}

.tips {
  text-align: center;
  padding: 20rpx;
}

.tips-text {
  font-size: 28rpx;
  color: #666;
  margin-bottom: 20rpx;
}

.complete-btn {
  background: #A9FF00;
  color: #000;
  border: none;
  border-radius: 20rpx;
  padding: 16rpx 40rpx;
  font-size: 28rpx;
}

.form-group {
  margin-bottom: 30rpx;
}

.form-label {
  display: block;
  font-size: 28rpx;
  color: #333;
  margin-bottom: 10rpx;
  font-weight: 500;
}

.form-textarea {
  width: 100%;
  min-height: 200rpx;
  padding: 20rpx;
  border: 2rpx solid #E5E5E5;
  border-radius: 10rpx;
  font-size: 28rpx;
  color: #333;
  background: #FFFFFF;
  box-sizing: border-box;
}

.char-count {
  display: block;
  text-align: right;
  font-size: 24rpx;
  color: #999;
  margin-top: 10rpx;
}

.phone-display {
  font-size: 28rpx;
  color: #333;
  padding: 20rpx;
  background: #F8F9FA;
  border-radius: 10rpx;
}

.upload-area {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
}

.image-item {
  position: relative;
  width: 150rpx;
  height: 150rpx;
  border-radius: 10rpx;
  overflow: hidden;
}

.image-item image {
  width: 100%;
  height: 100%;
}

.delete-btn {
  position: absolute;
  top: -10rpx;
  right: -10rpx;
  width: 40rpx;
  height: 40rpx;
  background: #FF3B30;
  color: #FFFFFF;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
}

.upload-btn {
  width: 150rpx;
  height: 150rpx;
  border: 2rpx dashed #E5E5E5;
  border-radius: 10rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #F8F9FA;
}

.upload-text {
  font-size: 48rpx;
  color: #999;
}

.upload-tips {
  font-size: 24rpx;
  color: #999;
  margin-top: 10rpx;
}

.id-card-upload {
  width: 100%;
}

.id-card-preview {
  position: relative;
  width: 100%;
  height: 300rpx;
  border-radius: 10rpx;
  overflow: hidden;
}

.id-card-preview image {
  width: 100%;
  height: 100%;
}

.upload-placeholder {
  width: 100%;
  height: 300rpx;
  border: 2rpx dashed #E5E5E5;
  border-radius: 10rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #F8F9FA;
}

.upload-placeholder .upload-text {
  font-size: 28rpx;
  color: #999;
}

.dialog-footer {
  display: flex;
  gap: 20rpx;
  padding: 30rpx;
  border-top: 1rpx solid #E5E5E5;
}

.btn {
  flex: 1;
  height: 80rpx;
  border: none;
  border-radius: 40rpx;
  font-size: 28rpx;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-secondary {
  background: #F5F5F5;
  color: #666;
}

.btn-primary {
  background: #A9FF00;
  color: #000;
}

.btn-primary:disabled {
  background: #CCCCCC;
  color: #FFFFFF;
}
</style>
