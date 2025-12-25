<template>
  <view class="ai-stylization-tool" v-if="visible">
    <!-- AI风格选择 -->
    <view class="style-selection">
      <view class="section-title">
        <image class="title-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/ai-style.png" mode="aspectFit" />
        <text class="title-text">选择AI风格</text>
      </view>
      
      <scroll-view class="style-scroll" scroll-x="true" show-scrollbar="false">
        <view class="style-container">
          <view 
            v-for="(style, index) in aiStyles" 
            :key="style.id"
            :class="['style-item', selectedStyleId === style.id ? 'active' : '']"
            @tap="selectStyle(style)"
          >
            <view class="style-image-container">
              <image class="style-image" :src="style.previewImage" mode="aspectFill" />
              <view v-if="selectedStyleId === style.id" class="selected-overlay">
                <image class="check-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/common/check.png" mode="aspectFit" />
              </view>
            </view>
            <text class="style-name">{{ style.name }}</text>
            <text class="style-desc">{{ style.description }}</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 提示词输入 -->
    <view class="prompt-input">
      <view class="section-title">
        <image class="title-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/prompt.png" mode="aspectFit" />
        <text class="title-text">AI提示词</text>
        <text class="optional-text">(可选)</text>
      </view>
      
      <view class="input-container">
        <textarea 
          class="prompt-textarea"
          v-model="promptText"
          placeholder="描述您想要的风格效果，例如：温暖的色调、复古风格、简约现代..."
          :maxlength="200"
          auto-height
        />
        <view class="char-count">{{ promptText.length }}/200</view>
      </view>
      
      <!-- 预设提示词 -->
      <view class="preset-prompts">
        <text class="preset-title">常用提示词：</text>
        <view class="prompt-tags">
          <view 
            v-for="(prompt, index) in presetPrompts" 
            :key="index"
            class="prompt-tag"
            @tap="addPrompt(prompt)"
          >
            <text class="tag-text">{{ prompt }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 风格化参数 -->
    <view class="stylization-params">
      <view class="section-title">
        <image class="title-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/params.png" mode="aspectFit" />
        <text class="title-text">风格化强度</text>
      </view>
      
      <view class="param-item">
        <text class="param-label">强度</text>
        <slider 
          class="param-slider"
          :value="stylizationStrength"
          min="0"
          max="100"
          step="10"
          activeColor="#A7CB00"
          backgroundColor="#EEEEEE"
          block-color="#A7CB00"
          @change="onStrengthChange"
        />
        <text class="param-value">{{ stylizationStrength }}%</text>
      </view>
      
      <view class="param-item">
        <text class="param-label">保真度</text>
        <slider 
          class="param-slider"
          :value="fidelity"
          min="0"
          max="100"
          step="10"
          activeColor="#A7CB00"
          backgroundColor="#EEEEEE"
          block-color="#A7CB00"
          @change="onFidelityChange"
        />
        <text class="param-value">{{ fidelity }}%</text>
      </view>
    </view>

    <!-- 生成按钮 -->
    <view class="generate-section">
      <view class="generate-btn" :class="{ 'disabled': !canGenerate }" @tap="generateStylization">
        <image v-if="isGenerating" class="loading-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/common/loading.png" mode="aspectFit" />
        <text class="generate-text">{{ isGenerating ? '生成中...' : '开始生成' }}</text>
      </view>
      
      <view class="generate-tips">
        <image class="tip-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/tip.png" mode="aspectFit" />
        <text class="tip-text">AI生成需要1-3分钟，请耐心等待</text>
      </view>
    </view>

    <!-- 生成历史 -->
    <view v-if="generationHistory.length > 0" class="generation-history">
      <view class="section-title">
        <image class="title-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/history.png" mode="aspectFit" />
        <text class="title-text">生成历史</text>
      </view>
      
      <scroll-view class="history-scroll" scroll-x="true" show-scrollbar="false">
        <view class="history-container">
          <view 
            v-for="(item, index) in generationHistory" 
            :key="item.id"
            class="history-item"
            @tap="selectHistoryItem(item)"
          >
            <view class="history-image-container">
              <image class="history-image" :src="item.resultImage" mode="aspectFill" />
              <view v-if="item.isSelected" class="selected-overlay">
                <image class="check-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/common/check.png" mode="aspectFit" />
              </view>
            </view>
            <text class="history-style">{{ item.styleName }}</text>
            <text class="history-time">{{ formatTime(item.createTime) }}</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 进度弹窗 -->
    <view v-if="showProgressModal" class="progress-modal" @touchmove.stop.prevent>
      <view class="progress-content">
        <view class="progress-header">
          <image class="progress-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/ai-generating.png" mode="aspectFit" />
          <text class="progress-title">AI正在生成中...</text>
        </view>
        
        <view class="progress-bar-container">
          <view class="progress-bar">
            <view class="progress-fill" :style="{ width: progress + '%' }"></view>
          </view>
          <text class="progress-text">{{ progress }}%</text>
        </view>
        
        <view class="progress-steps">
          <view 
            v-for="(step, index) in progressSteps" 
            :key="index"
            :class="['progress-step', currentProgressStep >= index ? 'active' : '']"
          >
            <view class="step-dot"></view>
            <text class="step-text">{{ step }}</text>
          </view>
        </view>
        
        <view class="progress-actions">
          <view class="cancel-btn" @tap="cancelGeneration">
            <text class="cancel-text">取消生成</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getAIStyles, aiStylization } from '@/api/diy.js';

export default {
  name: 'AiStylizationTool',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    currentFace: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      // AI风格
      aiStyles: [],
      selectedStyleId: null,
      
      // 提示词
      promptText: '',
      presetPrompts: [
        '温暖色调', '复古风格', '简约现代', '梦幻效果',
        '水彩风格', '油画质感', '卡通风格', '科技感',
        '自然清新', '浪漫唯美', '工业风格', '中国风'
      ],
      
      // 参数
      stylizationStrength: 70,
      fidelity: 80,
      
      // 生成状态
      isGenerating: false,
      showProgressModal: false,
      progress: 0,
      currentProgressStep: 0,
      progressSteps: ['分析图像', '应用风格', '优化细节', '生成完成'],
      generationTaskId: null,
      
      // 生成历史
      generationHistory: []
    };
  },
  computed: {
    canGenerate() {
      return this.selectedStyleId && !this.isGenerating;
    }
  },
  watch: {
    visible(newVal) {
      if (newVal) {
        this.loadAIStyles();
      }
    }
  },
  methods: {
    // 加载AI风格
    async loadAIStyles() {
      try {
        const result = await getAIStyles();
        if (result && result.code === 200) {
          this.aiStyles = result.data || [];
          if (this.aiStyles.length > 0 && !this.selectedStyleId) {
            this.selectedStyleId = this.aiStyles[0].id;
          }
        }
      } catch (error) {
        console.error('加载AI风格失败:', error);
      }
    },
    
    // 选择风格
    selectStyle(style) {
      this.selectedStyleId = style.id;
    },
    
    // 添加提示词
    addPrompt(prompt) {
      if (this.promptText) {
        this.promptText += '，' + prompt;
      } else {
        this.promptText = prompt;
      }
      
      // 限制长度
      if (this.promptText.length > 200) {
        this.promptText = this.promptText.substring(0, 200);
      }
    },
    
    // 强度变化
    onStrengthChange(e) {
      this.stylizationStrength = e.detail.value;
    },
    
    // 保真度变化
    onFidelityChange(e) {
      this.fidelity = e.detail.value;
    },
    
    // 开始生成
    async generateStylization() {
      if (!this.canGenerate) return;
      
      try {
        this.isGenerating = true;
        this.showProgressModal = true;
        this.progress = 0;
        this.currentProgressStep = 0;
        
        // 构建请求数据
        const requestData = {
          faceId: this.currentFace.id,
          styleId: this.selectedStyleId,
          prompt: this.promptText,
          strength: this.stylizationStrength,
          fidelity: this.fidelity
        };
        
        // 开始进度模拟
        this.startProgressSimulation();
        
        // 调用AI风格化接口
        const result = await aiStylization(requestData);
        
        if (result && result.code === 200) {
          // 生成成功
          this.progress = 100;
          this.currentProgressStep = this.progressSteps.length - 1;
          
          // 添加到历史记录
          const historyItem = {
            id: Date.now(),
            styleId: this.selectedStyleId,
            styleName: this.aiStyles.find(s => s.id === this.selectedStyleId)?.name || '',
            prompt: this.promptText,
            resultImage: result.data.resultImage,
            createTime: new Date(),
            isSelected: true
          };
          
          // 取消其他项的选中状态
          this.generationHistory.forEach(item => item.isSelected = false);
          this.generationHistory.unshift(historyItem);
          
          // 限制历史记录数量
          if (this.generationHistory.length > 10) {
            this.generationHistory = this.generationHistory.slice(0, 10);
          }
          
          setTimeout(() => {
            this.showProgressModal = false;
            this.$emit('stylization-complete', {
              success: true,
              result: result.data,
              historyItem: historyItem
            });
          }, 1000);
          
        } else {
          throw new Error(result.message || '生成失败');
        }
        
      } catch (error) {
        console.error('AI风格化失败:', error);
        this.showProgressModal = false;
        
        uni.showToast({
          title: error.message || 'AI生成失败',
          icon: 'none'
        });
        
        this.$emit('stylization-complete', {
          success: false,
          error: error.message
        });
        
      } finally {
        this.isGenerating = false;
      }
    },
    
    // 开始进度模拟
    startProgressSimulation() {
      const progressInterval = setInterval(() => {
        if (this.progress < 90) {
          this.progress += Math.random() * 10;
          
          // 更新步骤
          if (this.progress > 25 && this.currentProgressStep < 1) {
            this.currentProgressStep = 1;
          } else if (this.progress > 50 && this.currentProgressStep < 2) {
            this.currentProgressStep = 2;
          } else if (this.progress > 75 && this.currentProgressStep < 3) {
            this.currentProgressStep = 3;
          }
        } else {
          clearInterval(progressInterval);
        }
      }, 500);
      
      // 保存定时器ID以便取消
      this.progressInterval = progressInterval;
    },
    
    // 取消生成
    cancelGeneration() {
      if (this.progressInterval) {
        clearInterval(this.progressInterval);
      }
      
      this.isGenerating = false;
      this.showProgressModal = false;
      this.progress = 0;
      this.currentProgressStep = 0;
      
      // 如果有任务ID，调用取消接口
      if (this.generationTaskId) {
        // 这里可以调用取消生成的接口
      }
    },
    
    // 选择历史项
    selectHistoryItem(item) {
      // 取消其他项的选中状态
      this.generationHistory.forEach(historyItem => historyItem.isSelected = false);
      item.isSelected = true;
      
      this.$emit('stylization-complete', {
        success: true,
        result: { resultImage: item.resultImage },
        historyItem: item
      });
    },
    
    // 格式化时间
    formatTime(time) {
      const now = new Date();
      const diff = now - time;
      const minutes = Math.floor(diff / 60000);
      
      if (minutes < 1) {
        return '刚刚';
      } else if (minutes < 60) {
        return `${minutes}分钟前`;
      } else {
        const hours = Math.floor(minutes / 60);
        return `${hours}小时前`;
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.ai-stylization-tool {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 30rpx;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 20rpx;
}

.title-icon {
  width: 32rpx;
  height: 32rpx;
}

.title-text {
  font-size: 28rpx;
  font-weight: 600;
  color: #333333;
}

.optional-text {
  font-size: 24rpx;
  color: #999999;
}

.style-selection {
  padding: 0 20rpx;
}

.style-scroll {
  white-space: nowrap;
}

.style-container {
  display: flex;
  gap: 20rpx;
}

.style-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 160rpx;
  padding: 16rpx;
  border-radius: 12rpx;
  border: 2rpx solid #EEEEEE;
  transition: all 0.3s ease;
  
  &.active {
    border-color: #A7CB00;
    background-color: rgba(167, 203, 0, 0.1);
  }
}

.style-image-container {
  position: relative;
  width: 120rpx;
  height: 120rpx;
  margin-bottom: 12rpx;
}

.style-image {
  width: 100%;
  height: 100%;
  border-radius: 8rpx;
}

.selected-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(167, 203, 0, 0.8);
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.check-icon {
  width: 40rpx;
  height: 40rpx;
}

.style-name {
  font-size: 24rpx;
  font-weight: 500;
  color: #333333;
  margin-bottom: 4rpx;
  text-align: center;
}

.style-desc {
  font-size: 20rpx;
  color: #999999;
  text-align: center;
  line-height: 1.4;
}

.prompt-input {
  padding: 0 20rpx;
}

.input-container {
  position: relative;
  background: #F8F8F8;
  border-radius: 12rpx;
  padding: 20rpx;
  margin-bottom: 20rpx;
}

.prompt-textarea {
  width: 100%;
  min-height: 120rpx;
  font-size: 26rpx;
  color: #333333;
  line-height: 1.5;
  background: transparent;
  border: none;
  outline: none;
}

.char-count {
  position: absolute;
  bottom: 10rpx;
  right: 15rpx;
  font-size: 20rpx;
  color: #999999;
}

.preset-prompts {
  margin-top: 20rpx;
}

.preset-title {
  font-size: 24rpx;
  color: #666666;
  margin-bottom: 16rpx;
}

.prompt-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.prompt-tag {
  padding: 8rpx 16rpx;
  background: #F0F0F0;
  border-radius: 16rpx;
  border: 1rpx solid #DDDDDD;
}

.tag-text {
  font-size: 22rpx;
  color: #666666;
}

.stylization-params {
  padding: 0 20rpx;
}

.param-item {
  display: flex;
  align-items: center;
  margin-bottom: 30rpx;
  gap: 20rpx;
}

.param-label {
  font-size: 26rpx;
  color: #333333;
  width: 100rpx;
  flex-shrink: 0;
}

.param-slider {
  flex: 1;
}

.param-value {
  font-size: 24rpx;
  color: #A7CB00;
  font-weight: 600;
  width: 80rpx;
  text-align: right;
}

.generate-section {
  padding: 0 20rpx;
}

.generate-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16rpx;
  padding: 32rpx;
  background: linear-gradient(135deg, #A7CB00, #D7FF35);
  border-radius: 16rpx;
  margin-bottom: 16rpx;
  
  &.disabled {
    opacity: 0.5;
    pointer-events: none;
  }
}

.loading-icon {
  width: 32rpx;
  height: 32rpx;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.generate-text {
  font-size: 32rpx;
  font-weight: 600;
  color: #000000;
}

.generate-tips {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
}

.tip-icon {
  width: 24rpx;
  height: 24rpx;
}

.tip-text {
  font-size: 22rpx;
  color: #999999;
}

.generation-history {
  padding: 0 20rpx;
}

.history-scroll {
  white-space: nowrap;
}

.history-container {
  display: flex;
  gap: 16rpx;
}

.history-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 120rpx;
}

.history-image-container {
  position: relative;
  width: 100rpx;
  height: 100rpx;
  margin-bottom: 8rpx;
}

.history-image {
  width: 100%;
  height: 100%;
  border-radius: 8rpx;
}

.history-style {
  font-size: 22rpx;
  color: #333333;
  margin-bottom: 4rpx;
  text-align: center;
}

.history-time {
  font-size: 20rpx;
  color: #999999;
  text-align: center;
}

.progress-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
}

.progress-content {
  background: #FFFFFF;
  border-radius: 24rpx;
  width: 600rpx;
  padding: 40rpx;
}

.progress-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 40rpx;
}

.progress-icon {
  width: 80rpx;
  height: 80rpx;
  margin-bottom: 20rpx;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.progress-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
}

.progress-bar-container {
  display: flex;
  align-items: center;
  gap: 20rpx;
  margin-bottom: 40rpx;
}

.progress-bar {
  flex: 1;
  height: 12rpx;
  background: #EEEEEE;
  border-radius: 6rpx;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #A7CB00, #D7FF35);
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 24rpx;
  color: #A7CB00;
  font-weight: 600;
  width: 80rpx;
  text-align: right;
}

.progress-steps {
  display: flex;
  justify-content: space-between;
  margin-bottom: 40rpx;
}

.progress-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  
  &.active {
    .step-dot {
      background: #A7CB00;
    }
    
    .step-text {
      color: #A7CB00;
    }
  }
}

.step-dot {
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
  background: #DDDDDD;
  margin-bottom: 12rpx;
}

.step-text {
  font-size: 22rpx;
  color: #999999;
  text-align: center;
}

.progress-actions {
  display: flex;
  justify-content: center;
}

.cancel-btn {
  padding: 20rpx 40rpx;
  background: #F5F5F5;
  border-radius: 12rpx;
  border: 1rpx solid #DDDDDD;
}

.cancel-text {
  font-size: 26rpx;
  color: #666666;
}
</style>
