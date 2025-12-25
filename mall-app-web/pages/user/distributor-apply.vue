<template>
  <view class="apply-page">
    <!-- 导航栏 - 只在非第四步显示 -->
    <nav-bar
      v-if="currentStep !== 4"
      title="申请团长"
      :back="true"
      :placeholder="true"
      bg-color="#FFFFFF"
      color="#000"
    ></nav-bar>

    <!-- 步骤指示器 - 只在非第四步显示 -->
    <view v-if="currentStep !== 4" class="step-indicator">
      <view 
        v-for="(step, index) in steps" 
        :key="index"
        class="step-item"
        :class="{ 'active': currentStep >= index + 1, 'current': currentStep === index + 1 }"
      >
        <view class="step-number">{{ index + 1 }}</view>
        <view class="step-title">{{ step.title }}</view>
      </view>
    </view>

    <!-- 步骤内容 -->
    <view class="step-content">
      <!-- 步骤1: 会员验证 -->
      <view v-if="currentStep === 1" class="step-1">
        <view class="verification-item">
          <view class="item-label">会员账号</view>
          <view class="item-value">
            <text>{{ userInfo.phone || 'XXXXXXXXXXXXXXX' }}</text>
            <text class="status" :class="{ 'success': userInfo.phone }">
              {{ userInfo.phone ? '已注册' : '未注册' }}
            </text>
          </view>
        </view>

        <view class="verification-item">
          <view class="item-label">绑定手机号</view>
          <view class="item-value">
            <text>{{ userInfo.phone ? formatPhone(userInfo.phone) : '未绑定' }}</text>
            <text class="status" :class="{ 'success': userInfo.phone }">
              {{ userInfo.phone ? '已绑定' : '去绑定' }}
            </text>
          </view>
        </view>

        <view class="verification-item">
          <view class="item-label">会员积分</view>
          <view class="item-value">
            <text>{{ userInfo.integration || 0 }}</text>
            <text class="status" :class="{ 'success': userInfo.integration >= 200 }">
              {{ userInfo.integration >= 200 ? '已满200积分' : '赚积分' }}
            </text>
          </view>
        </view>


      </view>

      <!-- 步骤2: 个人介绍 -->
      <view v-if="currentStep === 2" class="step-2">
        <view class="intro-section">
          <textarea
            v-model="formData.selfIntroduction"
            placeholder="请输入一段自我介绍"
            class="intro-textarea"
            maxlength="300"
          ></textarea>
          <view class="char-count">{{ formData.selfIntroduction.length }}/300</view>
        </view>

        <view class="influence-section">
          <view class="section-title">私域影响力（填写可提高审核通过率）</view>
          <view class="section-desc">请提供相关资料（如小红书、朋友圈、视频号、社群等）证明有一定粉丝基础或社群运营能力</view>

          <view class="upload-grid">
            <view class="upload-area" @tap="chooseImage">
              <image
                v-if="formData.influenceScreenshots.length > 0"
                :src="formData.influenceScreenshots[0]"
                class="uploaded-image"
                mode="aspectFill"
              ></image>
              <view v-else class="upload-placeholder">
                <image src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/camera.png" class="camera-icon"></image>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 步骤3: 身份认证 -->
      <view v-if="currentStep === 3" class="step-3">
        <view class="phone-section">
          <view class="phone-label">本人手机号</view>
          <view class="phone-display">{{ userInfo.phone || '18739578536' }}</view>
        </view>

        <view class="id-section">
          <view class="section-title">身份证照片</view>
          <view class="section-desc">不可上传翻拍手机/手机截屏/复印件</view>

          <view class="id-upload-group">
            <view class="id-upload-item" @tap="uploadIdFront">
              <image
                v-if="formData.idCardFront"
                :src="formData.idCardFront"
                class="id-image"
                mode="aspectFill"
              ></image>
              <view v-else class="id-placeholder">
                <view class="id-example-container">
                  <image src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/id-image-1.png" class="id-example" mode="aspectFit"></image>
                </view>
                <view class="upload-action">
                  <image src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/camera.png" class="camera-icon"></image>
                  <text class="upload-text">点击上传身份证人像面照片</text>
                </view>
              </view>
            </view>

            <view class="id-upload-item" @tap="uploadIdBack">
              <image
                v-if="formData.idCardBack"
                :src="formData.idCardBack"
                class="id-image"
                mode="aspectFill"
              ></image>
              <view v-else class="id-placeholder">
                <view class="id-example-container">
                  <image src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/id-image-2.png" class="id-example" mode="aspectFit"></image>
                </view>
                <view class="upload-action">
                  <image src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/camera.png" class="camera-icon"></image>
                  <text class="upload-text">点击上传身份证反面图片</text>
                </view>
              </view>
            </view>
          </view>
        </view>

        <view class="other-section">
          <view class="section-title">其他身份证明</view>
          <view class="section-desc">学生证/教师证/ 在读证明/学籍证明/校友证明/实体门店等</view>

          <view class="upload-grid">
            <view class="upload-area" @tap="uploadOtherCert">
              <image
                v-if="formData.otherCertificates.length > 0"
                :src="formData.otherCertificates[0]"
                class="uploaded-image"
                mode="aspectFill"
              ></image>
              <view v-else class="upload-placeholder">
                <image src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/camera.png" class="camera-icon"></image>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 步骤4: 提交成功 -->
      <view v-if="currentStep === 4" class="step-4">
        <!-- 顶部背景图 -->
        <view class="success-header">
          <view class="success-bg">
            <image src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/commit-bg.png" class="bg-image" mode="aspectFill"></image>
          </view>

          <!-- 成功内容 --> 
          <view class="success-content">
            <view class="success-title">已提交申请</view>
            <view class="success-desc">如审核通过，将收到通知</view>
          </view>
        </view>

        <!-- 白色内容区域 -->
        <view class="white-content">
          <view class="info-section">
            <view class="info-header">
              <image src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/rocket.png" class="info-icon"></image>
              <text class="info-title">如何加快审核进度</text>
            </view>
            <view class="info-desc">
              如果你是一位博主，活跃在小红书、抖音视频号等平台;如果你是文化爱好者、实体店长、社群活跃者...可以提供账号、社群、门店照片等信息，加快审核速度
            </view>
          </view>

          <view class="upload-section">
            <view class="section-title">上传截图</view>
            <view class="upload-grid">
              <!-- 已上传的工作截图预览 -->
              <view v-for="(image, index) in formData.workScreenshots" :key="index" class="upload-area uploaded">
                <image :src="image" class="uploaded-image" mode="aspectFill"></image>
                <view class="delete-btn" @tap="deleteWorkImage(index)">
                  <image src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/delete.png" class="delete-icon"></image>
                </view>
              </view>

              <!-- 上传按钮 -->
              <view v-if="formData.workScreenshots.length < 9" class="upload-area" @tap="uploadWorkImage">
                <view class="upload-placeholder">
                  <image src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/camera.png" class="camera-icon"></image>
                </view>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 底部提示和按钮 -->
    <view class="bottom-section">
      <!-- 步骤1的底部提示 -->
      <view v-if="currentStep === 1" class="bottom-tip" :class="{ 'not-satisfied': !canProceed }">
        <image src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/info.png" class="tip-icon"></image>
        <text class="tip-text">{{ canProceed ? '已达到申请要求' : '未达到申请要求' }}</text>
      </view>

      <!-- 底部按钮 -->
      <view class="bottom-actions">
        <view v-if="currentStep > 1 && currentStep < 4" class="prev-btn" @tap="prevStep">上一步</view>
        <view class="next-btn" @tap="nextStep" :class="{ 'disabled': !canProceed }">
          <text>{{ getButtonText() }}</text>
          <image v-if="canProceed && currentStep < 4" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/right.png" class="right-icon"></image>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { uploadImage } from '@/api/common.js'
import {
  submitDistributorApply,
  checkApplyEligibility,
  getDistributorRequirements,
  submitSupplementaryInfo
} from '@/api/distribution.js'
import NavBar from '@/components/nav-bar.vue'

export default {
  components: {
    'nav-bar': NavBar
  },
  data() {
    return {
      currentStep: 1,
      steps: [
        { title: '会员验证' },
        { title: '个人介绍' },
        { title: '身份认证' }
      ],
      userInfo: {
        phone: '18739578536',
        integration: 200,
        nickname: '测试用户',
        email: 'test@example.com'
      },
      formData: {
        selfIntroduction: '',
        influenceScreenshots: [],
        idCardFront: '',
        idCardBack: '',
        otherCertificates: [],
        workScreenshots: []
      },
      applyId: null // 申请ID，用于补充信息
    }
  },
  computed: {
    canProceed() {
      switch (this.currentStep) {
        case 1:
          return this.userInfo.phone && this.userInfo.integration >= 200;
        case 2:
          return this.formData.selfIntroduction.trim().length > 0;
        case 3:
          return this.formData.idCardFront && this.formData.idCardBack;
        default:
          return true;
      }
    }
  },
  onLoad() {
    this.loadUserInfo();
    this.checkEligibility();
  },
  methods: {
    loadUserInfo() {
      // 加载用户信息
      const userInfo = uni.getStorageSync('userInfo');
      if (userInfo) {
        this.userInfo = userInfo;
      }
    },
    async checkEligibility() {
      try {
        const result = await checkApplyEligibility();
        console.log('申请资格检查结果:', result);

        if (result.code === 200) {
          // 更新用户信息
          this.userInfo = {
            ...this.userInfo,
            ...result.data
          };

          // 如果已经提交了申请，自动进入第四步
          if (result.data.hasPendingApply) {
            this.currentStep = 4;
            this.applyId = result.data.applyId; // 保存申请ID
            console.log('检测到已有待审核申请，自动进入第四步，申请ID:', this.applyId);
          }
        } else {
          console.log('申请资格检查失败:', result.message);
        }
      } catch (error) {
        console.log('检查申请资格失败:', error);
        // 检查是否是因为未登录导致的错误
        if (error.data && error.data.code === 401) {
          console.log('用户未登录，无法检查申请资格');
        }
        // 如果接口失败，使用默认数据
      }
    },
    formatPhone(phone) {
      if (!phone) return '';
      return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
    },
    getButtonText() {
      if (this.currentStep === 1) {
        return this.canProceed ? '信息无误，下一步' : '不满足申请要求';
      } else if (this.currentStep === 3) {
        return '提交申请';
      } else if (this.currentStep === 4) {
        return '提交信息';
      } else {
        return '信息无误，下一步';
      }
    },
    prevStep() {
      if (this.currentStep > 1) {
        this.currentStep--;
      }
    },
    nextStep() {
      if (!this.canProceed && this.currentStep !== 4) return;

      if (this.currentStep === 3) {
        // 第三步点击提交申请
        this.submitApplication();
      } else if (this.currentStep === 4) {
        // 第四步点击提交补充信息
        this.submitInfo();
      } else if (this.currentStep < 3) {
        // 第一步和第二步进入下一步
        this.currentStep++;
      }
    },
    async submitApplication() {
      try {
        uni.showLoading({ title: '提交中...' });

        const applicationData = {
          // 根据页面设计稿的实际字段
          phone: this.userInfo.phone,
          selfIntroduction: this.formData.selfIntroduction,
          influenceScreenshots: JSON.stringify(this.formData.influenceScreenshots || []),
          idCardFront: this.formData.idCardFront,
          idCardBack: this.formData.idCardBack,
          otherCertificates: JSON.stringify(this.formData.otherCertificates || []),
          workScreenshots: JSON.stringify(this.formData.workScreenshots || [])
        };

        const result = await submitDistributorApply(applicationData);

        uni.hideLoading();

        if (result.code === 200) {
          this.currentStep = 4;
          uni.showToast({
            title: '申请提交成功',
            icon: 'success',
            duration: 2000
          });
        } else {
          uni.showToast({
            title: result.message || '提交失败',
            icon: 'error'
          });
        }
      } catch (error) {
        uni.hideLoading();
        console.error('提交申请失败:', error);
        uni.showToast({
          title: error.message || '提交失败，请重试',
          icon: 'error'
        });
      }
    },
    chooseImage() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: async (res) => {
          try {
            console.log('选择图片成功:', res);

            const filePath = res.tempFilePaths[0];
            if (!filePath) {
              throw new Error('未能获取到文件路径');
            }

            uni.showLoading({ title: '上传中...' });

            const uploadRes = await uploadImage(filePath);
            console.log('上传响应:', uploadRes);

            this.formData.influenceScreenshots = [uploadRes.data];
            uni.hideLoading();
            uni.showToast({ title: '上传成功', icon: 'success' });
          } catch (error) {
            uni.hideLoading();
            console.error('上传失败详情:', error);
            uni.showToast({
              title: error.message || '上传失败',
              icon: 'error',
              duration: 3000
            });
          }
        },
        fail: (error) => {
          console.error('选择图片失败:', error);
          uni.showToast({
            title: '选择图片失败',
            icon: 'error'
          });
        }
      });
    },
    uploadIdFront() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: async (res) => {
          try {
            console.log('选择身份证正面成功:', res);

            const filePath = res.tempFilePaths[0];
            if (!filePath) {
              throw new Error('未能获取到文件路径');
            }

            uni.showLoading({ title: '上传身份证正面...' });

            const uploadRes = await uploadImage(filePath);
            console.log('身份证正面上传响应:', uploadRes);

            this.formData.idCardFront = uploadRes.data;
            uni.hideLoading();
            uni.showToast({ title: '身份证正面上传成功', icon: 'success' });
          } catch (error) {
            uni.hideLoading();
            console.error('身份证正面上传失败:', error);
            uni.showToast({
              title: error.message || '身份证正面上传失败',
              icon: 'error',
              duration: 3000
            });
          }
        },
        fail: (error) => {
          console.error('选择身份证正面失败:', error);
          uni.showToast({
            title: '选择图片失败',
            icon: 'error'
          });
        }
      });
    },
    uploadIdBack() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: async (res) => {
          try {
            console.log('选择身份证背面成功:', res);

            const filePath = res.tempFilePaths[0];
            if (!filePath) {
              throw new Error('未能获取到文件路径');
            }

            uni.showLoading({ title: '上传身份证背面...' });

            const uploadRes = await uploadImage(filePath);
            console.log('身份证背面上传响应:', uploadRes);

            this.formData.idCardBack = uploadRes.data;
            uni.hideLoading();
            uni.showToast({ title: '身份证背面上传成功', icon: 'success' });
          } catch (error) {
            uni.hideLoading();
            console.error('身份证背面上传失败:', error);
            uni.showToast({
              title: error.message || '身份证背面上传失败',
              icon: 'error',
              duration: 3000
            });
          }
        },
        fail: (error) => {
          console.error('选择身份证背面失败:', error);
          uni.showToast({
            title: '选择图片失败',
            icon: 'error'
          });
        }
      });
    },
    uploadOtherCert() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: async (res) => {
          try {
            console.log('选择其他证明材料成功:', res);

            const filePath = res.tempFilePaths[0];
            if (!filePath) {
              throw new Error('未能获取到文件路径');
            }

            uni.showLoading({ title: '上传证明材料...' });

            const uploadRes = await uploadImage(filePath);
            console.log('其他证明材料上传响应:', uploadRes);

            this.formData.otherCertificates = [uploadRes.data];
            uni.hideLoading();
            uni.showToast({ title: '证明材料上传成功', icon: 'success' });
          } catch (error) {
            uni.hideLoading();
            console.error('其他证明材料上传失败:', error);
            uni.showToast({
              title: error.message || '证明材料上传失败',
              icon: 'error',
              duration: 3000
            });
          }
        },
        fail: (error) => {
          console.error('选择其他证明材料失败:', error);
          uni.showToast({
            title: '选择图片失败',
            icon: 'error'
          });
        }
      });
    },
    uploadWorkImage() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: async (res) => {
          try {
            console.log('选择工作截图成功:', res);

            const filePath = res.tempFilePaths[0];
            if (!filePath) {
              throw new Error('未能获取到文件路径');
            }

            uni.showLoading({ title: '上传工作截图...' });

            const uploadRes = await uploadImage(filePath);
            console.log('工作截图上传成功:', uploadRes.data);

            // 可以将工作截图保存到额外的数组中
            if (!this.formData.workScreenshots) {
              this.formData.workScreenshots = [];
            }
            this.formData.workScreenshots.push(uploadRes.data);

            uni.hideLoading();
            uni.showToast({ title: '工作截图上传成功', icon: 'success' });
          } catch (error) {
            uni.hideLoading();
            console.error('工作截图上传失败:', error);
            uni.showToast({
              title: error.message || '工作截图上传失败',
              icon: 'error',
              duration: 3000
            });
          }
        },
        fail: (error) => {
          console.error('选择工作截图失败:', error);
          uni.showToast({
            title: '选择图片失败',
            icon: 'error'
          });
        }
      });
    },

    // 删除工作截图
    deleteWorkImage(index) {
      uni.showModal({
        title: '确认删除',
        content: '确定要删除这张工作截图吗？',
        success: (res) => {
          if (res.confirm) {
            this.formData.workScreenshots.splice(index, 1);
            uni.showToast({
              title: '删除成功',
              icon: 'success'
            });
          }
        }
      });
    },

    // 返回上一页
    goBack() {
      uni.navigateBack();
    },

    // 提交补充信息
    async submitInfo() {
      try {
        // 检查申请ID
        if (!this.applyId) {
          uni.showToast({
            title: '申请ID不存在，请重新进入页面',
            icon: 'error'
          });
          return;
        }

        // 检查是否有工作截图
        if (!this.formData.workScreenshots || this.formData.workScreenshots.length === 0) {
          uni.showToast({
            title: '请至少上传一张工作截图',
            icon: 'error'
          });
          return;
        }

        uni.showLoading({ title: '提交中...' });

        // 准备提交的补充信息数据
        const supplementaryData = {
          applyId: this.applyId,
          workScreenshots: JSON.stringify(this.formData.workScreenshots)
        };

        console.log('提交补充信息:', supplementaryData);

        const result = await submitSupplementaryInfo(supplementaryData);

        uni.hideLoading();

        if (result.code === 200) {
          uni.showToast({
            title: '补充信息提交成功',
            icon: 'success'
          });

          // 可以选择返回上一页或刷新当前状态
          setTimeout(() => {
            uni.navigateBack();
          }, 1500);
        } else {
          uni.showToast({
            title: result.message || '提交失败',
            icon: 'error'
          });
        }
      } catch (error) {
        uni.hideLoading();
        console.error('提交补充信息失败:', error);
        uni.showToast({
          title: error.message || '提交失败，请重试',
          icon: 'error'
        });
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.apply-page {
  min-height: 100vh;
  background: #f8f8f8;
  padding-bottom: 200rpx;
}

/* 步骤指示器 */
.step-indicator {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx;
  background: #fff;
  margin-bottom: 20rpx;
}

.step-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;

  &:not(:last-child)::after {
    content: '';
    position: absolute;
    top: 20rpx;
    left: 130rpx;
    width: 120rpx;
    height: 2rpx;
    background: #cccccc;
    z-index: 1;
  }

  &.active:not(:last-child)::after {
    background: #A9FF00;
  }
}

.step-number {
  width: 40rpx;
  height: 40rpx;
  border-radius: 32rpx;
  background: #efefef;
  color: #9fa19d;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  margin-bottom: 16rpx;
  position: relative;
  z-index: 2;

  .step-item.active & {
    background: linear-gradient(135deg, #A7FD03 0%, #B2F597 100%);
    color: #000;
  }

  .step-item.current & {
    background: linear-gradient(135deg, #A7FD03 0%, #B2F597 100%);
    color: #0A0D05;
  }
}

.step-title {
  font-size: 28rpx;
  color: #666;

  .step-item.active & {
    color: #000;
    font-weight: 600;
  }

  .step-item.current & {
    color: #000;
    font-weight: 600;
  }
}

/* 步骤内容 */
.step-content {
  background: #fff;
  margin: 0 32rpx;
  border-radius: 16rpx;
  padding: 32rpx;
  min-height: 600rpx;
  box-shadow: 0px 0px 4rpx 0px rgba(0, 0, 0, 0.08);
}

/* 步骤1: 会员验证 */
.verification-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx;
  margin-bottom: 16rpx;
  background: #fff;
  border-radius: 16rpx;
  box-shadow: 0px 0px 4rpx 0px rgba(0, 0, 0, 0.08);

  &:last-child {
    margin-bottom: 0;
  }
}

.item-label {
  font-size: 28rpx;
  color: #0A0D05;
}

.item-value {
  display: flex;
  align-items: center;
  gap: 20rpx;

  text {
    font-size: 28rpx;
    color: #666;
  }
}

.status {
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
  font-size: 28rpx;
  background: #f0f0f0;
  color: #999;

  &.success {
    background: transparent;
    color: #647D00;
  }
}



/* 步骤2: 个人介绍 */
.intro-section {
  margin-bottom: 32rpx;
  background: #fff;
  border-radius: 16rpx;
  padding: 32rpx;
  box-shadow: 0px 0px 4rpx 0px rgba(0, 0, 0, 0.08);
  position: relative;
}

.intro-textarea {
  width: 100%;
  min-height: 348rpx;
  padding: 32rpx;
  border: none;
  border-radius: 16rpx;
  font-size: 28rpx;
  line-height: 1.6;
  background: #f8f8f8;
  color: #666;
  box-sizing: border-box;
}

.char-count {
  position: absolute;
  bottom: 32rpx;
  right: 32rpx;
  font-size: 28rpx;
  color: #9fa19d;
}

.influence-section {
  background: #fff;
  border-radius: 16rpx;
  padding: 32rpx 32rpx 0;

  .section-title {
    font-size: 28rpx;
    color: #0A0D05;
    margin-bottom: 16rpx;
  }

  .section-desc {
    font-size: 24rpx;
    color: #666;
    line-height: 1.5;
    margin-bottom: 16rpx;
  }
}

.upload-grid {
  display: flex;
  gap: 16rpx;
  padding-bottom: 32rpx;
}

.upload-area {
  width: 160rpx;
  height: 160rpx;
  border: 2rpx dashed #eeeeee;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f8f8;
  position: relative;
}

.upload-area.uploaded {
  border: none;
  background: transparent;
}

.uploaded-image {
  width: 100%;
  height: 100%;
  border-radius: 14rpx;
}

.delete-btn {
  position: absolute;
  top: -8rpx;
  right: -8rpx;
  width: 32rpx;
  height: 32rpx;
  background: #ff4757;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
}

.delete-icon {
  width: 16rpx;
  height: 16rpx;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.camera-icon {
  width: 48rpx;
  height: 48rpx;
}

/* 步骤3: 身份认证 */
.phone-section {
  background: #fff;
  border-radius: 16rpx;
  padding: 32rpx;
  margin-bottom: 16rpx;
  box-shadow: 0px 0px 4rpx 0px rgba(0, 0, 0, 0.08);
}

.phone-label {
  font-size: 28rpx;
  color: #9fa19d;
  margin-bottom: 16rpx;
}

.phone-display {
  font-size: 32rpx;
  color: #0A0D05;
  font-weight: 400;
  border-bottom: 2rpx solid #eeeeee;
  padding-bottom: 16rpx;
}

.id-section {
  background: #fff;
  border-radius: 16rpx;
  padding: 32rpx;
  margin-bottom: 16rpx;

  .section-title {
    font-size: 28rpx;
    color: #0A0D05;
    margin-bottom: 16rpx;
  }

  .section-desc {
    font-size: 24rpx;
    color: #666;
    line-height: 1.5;
    margin-bottom: 32rpx;
  }
}

.id-upload-group {
  display: flex;
  flex-direction: column;
  gap: 32rpx;
}

.id-upload-item {
  height: 364rpx;
  border: 2rpx dashed #cccccc;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  position: relative;
}

.id-image {
  width: 100%;
  height: 100%;
  border-radius: 14rpx;
}

.id-placeholder {
  width: 426rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 16rpx;
  box-sizing: border-box;
}

.id-example-container {
  width: 426rpx;
  height: 248rpx;
  margin-bottom: 20rpx;
}

.id-example {
  width: 100%;
  height: 100%;
  border-radius: 8rpx;
  object-fit: contain; /* 确保图片不被压缩 */
}

.upload-action {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
}

.camera-icon {
  width: 32rpx;
  height: 32rpx;
  margin-right: 8rpx;
}

.upload-text {
  font-size: 24rpx;
  color: #666;
  text-align: center;
}

.other-section {
  background: #fff;
  border-radius: 16rpx;
  padding: 32rpx 32rpx 0;

  .section-title {
    font-size: 28rpx;
    color: #0A0D05;
    margin-bottom: 16rpx;
  }

  .section-desc {
    font-size: 24rpx;
    color: #666;
    line-height: 1.5;
    margin-bottom: 16rpx;
  }
}

/* 步骤4: 提交成功 */
.step-4 {
  margin: -32rpx -64rpx;
  position: relative;
  min-height: 80vh;
  background: #ffffff;
}

.success-header {
  position: relative;
  height: 30vh;
  background-image: url('https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/commit-bg.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  display: flex;
  align-items: center;
  justify-content: center;
}

.success-bg {
  display: none; /* 使用CSS背景图片替代 */
}

.bg-image {
  display: none; /* 使用CSS背景图片替代 */
}



/* 成功内容 */
.success-content {
  text-align: center;
  color: #000;
  padding: 100rpx 32rpx 60rpx;
}

.success-title {
  font-size: 56rpx;
  font-weight: 600;
  margin-bottom: 16rpx;
  color: #000;
}

.success-desc {
  font-size: 28rpx;
  color: #666;
}

/* 白色内容区域 */
.white-content {
  background: #ffffff;
  border-radius: 32rpx 32rpx 0 0;
  margin-top: -32rpx;
  padding: 60rpx 32rpx 120rpx;
  position: relative;
  z-index: 2;
}

.info-section {
  margin-bottom: 32rpx;
}

.info-header {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 16rpx;
}

.info-icon {
  width: 32rpx;
  height: 32rpx;
}

.info-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #0A0D05;
}

.info-desc {
  font-size: 28rpx;
  line-height: 1.5;
  color: #666;
}

.upload-section {
  margin-bottom: 48rpx;

  .section-title {
    font-size: 28rpx;
    color: #0A0D05;
    margin-bottom: 16rpx;
  }
}

/* 提交按钮 */
.submit-button {
  background: #20201E;
  border-radius: 16rpx;
  height: 96rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 32rpx;
}

.submit-text {
  font-size: 32rpx;
  color: #A9FF00;
  font-weight: 400;
}

/* 底部按钮 */
.bottom-actions {
  display: flex;
  gap: 32rpx;
}

.prev-btn {
  width: 156rpx;
  height: 80rpx;
  border: 2rpx solid #eeeeee;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  color: #0A0D05;
  background: #fff;
}

.next-btn {
  flex: 1;
  height: 80rpx;
  background: #20201E;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16rpx;
  font-size: 32rpx;
  color: #A9FF00;

  &.disabled {
    background: #DDDDDD;
    color: #fff;
  }
}

.right-icon {
  width: 24rpx;
  height: 24rpx;
}

/* 底部区域 */
.bottom-section {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 32rpx;
}

.bottom-tip {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  margin-bottom: 0;
  padding: 16rpx 0 32rpx;
  background: #A9FF00;
  border-radius: 16rpx 16rpx 0 0;
  box-shadow: 0px 0px 4rpx 0px rgba(0, 0, 0, 0.12);

  &.not-satisfied {
    background: #fff;

    .tip-text {
      color: #9FA19D;
    }

    .tip-icon {
      filter: grayscale(1);
    }
  }
}

.tip-icon {
  width: 24rpx;
  height: 24rpx;
}

.tip-text {
  font-size: 24rpx;
  color: #000;
  font-weight: 400;
}
</style>
