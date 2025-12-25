<template>
  <van-overlay 
    :show="visible" 
    :duration="0"
    :z-index="zIndex"
    :lock-scroll="lockScroll"
    class="loading-overlay"
  >
    <div class="loading-wrapper" @click.stop>
      <div class="loading-content" :class="loadingContentClass">
        <!-- 主加载图标 -->
        <van-loading 
          :type="type" 
          :size="size"
          :color="color"
          class="loading-spinner"
        />
        
        <!-- 加载文本 -->
        <div class="loading-text" v-if="text">
          {{ text }}
        </div>
        
        <!-- 加载描述 -->
        <div class="loading-description" v-if="description">
          {{ description }}
        </div>
        
        <!-- 进度条 -->
        <div class="loading-progress" v-if="showProgress && progress !== null">
          <van-progress 
            :percentage="progress" 
            :color="progressColor"
            :track-color="progressTrackColor"
            :stroke-width="progressStrokeWidth"
            class="progress-bar"
          />
          <div class="progress-text" v-if="progressText">
            {{ progressText }}
          </div>
        </div>
        
        <!-- 取消按钮 -->
        <div class="loading-actions" v-if="showCancel || $slots.actions">
          <slot name="actions">
            <van-button 
              v-if="showCancel"
              type="default" 
              size="small"
              @click="handleCancel"
              :disabled="cancelDisabled"
              class="cancel-btn"
            >
              {{ cancelText }}
            </van-button>
          </slot>
        </div>
      </div>
    </div>
  </van-overlay>
</template>

<script setup lang="ts">
import { computed, defineEmits, defineProps } from 'vue'

// Props定义
interface Props {
  visible?: boolean
  type?: 'circular' | 'spinner'
  size?: string | number
  color?: string
  text?: string
  description?: string
  zIndex?: number
  lockScroll?: boolean
  showProgress?: boolean
  progress?: number | null
  progressColor?: string
  progressTrackColor?: string
  progressStrokeWidth?: number
  progressText?: string
  showCancel?: boolean
  cancelText?: string
  cancelDisabled?: boolean
  theme?: 'light' | 'dark'
  opacity?: number
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  type: 'circular',
  size: '32px',
  color: '#1989fa',
  zIndex: 2000,
  lockScroll: true,
  showProgress: false,
  progress: null,
  progressColor: '#1989fa',
  progressTrackColor: '#e5e5e5',
  progressStrokeWidth: 6,
  showCancel: false,
  cancelText: '取消',
  cancelDisabled: false,
  theme: 'light',
  opacity: 0.7
})

// Emits定义
interface Emits {
  cancel: []
  'update:visible': [visible: boolean]
}

const emit = defineEmits<Emits>()

// 计算属性
const loadingContentClass = computed(() => {
  return {
    [`loading-content--${props.theme}`]: true,
    'loading-content--with-progress': props.showProgress,
    'loading-content--with-actions': props.showCancel
  }
})

// 事件处理
const handleCancel = () => {
  if (!props.cancelDisabled) {
    emit('cancel')
    emit('update:visible', false)
  }
}
</script>

<style scoped lang="scss">
.loading-overlay {
  display: flex;
  align-items: center;
  justify-content: center;
  
  // 自定义背景透明度
  :deep(.van-overlay) {
    background-color: rgba(0, 0, 0, v-bind(opacity));
  }
}

.loading-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.loading-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  min-width: 120px;
  max-width: 280px;
  pointer-events: auto;
  
  &--dark {
    background: #2c2c2c;
    color: #ffffff;
    
    .loading-text {
      color: #ffffff;
    }
    
    .loading-description {
      color: rgba(255, 255, 255, 0.7);
    }
    
    .progress-text {
      color: rgba(255, 255, 255, 0.8);
    }
  }
  
  &--with-progress {
    min-width: 200px;
  }
  
  &--with-actions {
    padding-bottom: 20px;
  }
}

.loading-spinner {
  margin-bottom: 16px;
}

.loading-text {
  font-size: 14px;
  font-weight: 500;
  color: #323233;
  text-align: center;
  line-height: 1.4;
  margin-bottom: 8px;
}

.loading-description {
  font-size: 12px;
  color: #646566;
  text-align: center;
  line-height: 1.4;
  margin-bottom: 12px;
}

.loading-progress {
  width: 100%;
  margin-bottom: 16px;
  
  .progress-bar {
    margin-bottom: 8px;
  }
  
  .progress-text {
    font-size: 12px;
    color: #646566;
    text-align: center;
  }
}

.loading-actions {
  display: flex;
  gap: 8px;
  
  .cancel-btn {
    min-width: 80px;
  }
}

// 移动端优化
@media (max-width: 767px) {
  .loading-content {
    padding: 20px;
    border-radius: 8px;
    min-width: 100px;
    max-width: 240px;
    
    &--with-progress {
      min-width: 180px;
    }
  }
  
  .loading-text {
    font-size: 13px;
  }
  
  .loading-description {
    font-size: 11px;
  }
}

// 无障碍支持
@media (prefers-reduced-motion: reduce) {
  .loading-spinner {
    :deep(.van-loading__spinner) {
      animation: none;
    }
  }
}
</style> 