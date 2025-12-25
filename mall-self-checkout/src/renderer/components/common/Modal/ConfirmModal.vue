<template>
  <van-popup
    v-model:show="visible"
    :position="position"
    :round="round"
    :closeable="closeable"
    :close-icon="closeIcon"
    :overlay="overlay"
    :overlay-class="overlayClass"
    :overlay-style="overlayStyle"
    :duration="duration"
    :z-index="zIndex"
    :lock-scroll="lockScroll"
    :lazy-render="lazyRender"
    :safe-area-inset-bottom="safeAreaInsetBottom"
    :close-on-click-overlay="closeOnClickOverlay"
    :close-on-popstate="closeOnPopstate"
    class="confirm-modal"
    @open="handleOpen"
    @close="handleClose"
    @opened="handleOpened"
    @closed="handleClosed"
    @click-overlay="handleOverlayClick"
  >
    <div class="modal-content" :class="modalContentClass">
      <!-- 头部区域 -->
      <div class="modal-header" v-if="showHeader">
        <slot name="header">
          <!-- 图标 -->
          <div class="modal-icon" v-if="icon">
            <van-icon 
              :name="icon" 
              :color="iconColor"
              :size="iconSize"
              class="modal-icon-img"
            />
          </div>
          
          <!-- 标题 -->
          <h3 class="modal-title" v-if="title">
            {{ title }}
          </h3>
        </slot>
      </div>

      <!-- 内容区域 -->
      <div class="modal-body">
        <slot>
          <div class="modal-message" v-if="message" v-html="message"></div>
        </slot>
      </div>

      <!-- 底部操作区域 -->
      <div class="modal-footer" v-if="showFooter">
        <slot name="footer">
          <div class="modal-actions" :class="actionsClass">
            <van-button 
              v-if="showCancel"
              :type="cancelButtonType"
              :size="buttonSize"
              :loading="cancelLoading"
              :disabled="cancelDisabled"
              @click="handleCancel"
              class="modal-btn cancel-btn"
            >
              {{ cancelText }}
            </van-button>
            
            <van-button 
              v-if="showConfirm"
              :type="confirmButtonType"
              :size="buttonSize"
              :loading="confirmLoading"
              :disabled="confirmDisabled"
              @click="handleConfirm"
              class="modal-btn confirm-btn"
            >
              {{ confirmText }}
            </van-button>
          </div>
        </slot>
      </div>
    </div>
  </van-popup>
</template>

<script setup lang="ts">
import { computed, defineEmits, defineProps, ref, watch } from 'vue'

// Props定义
interface Props {
  modelValue?: boolean
  title?: string
  message?: string
  icon?: string
  iconColor?: string
  iconSize?: string | number
  type?: 'info' | 'success' | 'warning' | 'error' | 'confirm'
  theme?: 'default' | 'round-button'
  position?: 'center' | 'top' | 'bottom' | 'left' | 'right'
  round?: boolean
  closeable?: boolean
  closeIcon?: string
  overlay?: boolean
  overlayClass?: string
  overlayStyle?: Record<string, any>
  duration?: number | string
  zIndex?: number | string
  lockScroll?: boolean
  lazyRender?: boolean
  safeAreaInsetBottom?: boolean
  closeOnClickOverlay?: boolean
  closeOnPopstate?: boolean
  showHeader?: boolean
  showFooter?: boolean
  showCancel?: boolean
  showConfirm?: boolean
  cancelText?: string
  confirmText?: string
  cancelButtonType?: 'default' | 'primary' | 'success' | 'warning' | 'danger'
  confirmButtonType?: 'default' | 'primary' | 'success' | 'warning' | 'danger'
  buttonSize?: 'small' | 'normal' | 'large'
  cancelLoading?: boolean
  confirmLoading?: boolean
  cancelDisabled?: boolean
  confirmDisabled?: boolean
  width?: string
  maxWidth?: string
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: false,
  type: 'confirm',
  theme: 'default',
  position: 'center',
  round: true,
  closeable: false,
  overlay: true,
  duration: 300,
  zIndex: 2000,
  lockScroll: true,
  lazyRender: true,
  safeAreaInsetBottom: true,
  closeOnClickOverlay: false,
  closeOnPopstate: true,
  showHeader: true,
  showFooter: true,
  showCancel: true,
  showConfirm: true,
  cancelText: '取消',
  confirmText: '确定',
  cancelButtonType: 'default',
  confirmButtonType: 'primary',
  buttonSize: 'normal',
  cancelLoading: false,
  confirmLoading: false,
  cancelDisabled: false,
  confirmDisabled: false,
  iconSize: '48px',
  width: '320px',
  maxWidth: '90vw'
})

// Emits定义
interface Emits {
  'update:modelValue': [visible: boolean]
  open: []
  close: []
  opened: []
  closed: []
  confirm: []
  cancel: []
  'click-overlay': []
}

const emit = defineEmits<Emits>()

// 响应式数据
const visible = ref(props.modelValue)

// 监听modelValue变化
watch(() => props.modelValue, (newVal) => {
  visible.value = newVal
})

watch(visible, (newVal) => {
  emit('update:modelValue', newVal)
})

// 计算属性
const modalContentClass = computed(() => {
  return {
    [`modal-content--${props.type}`]: true,
    [`modal-content--${props.theme}`]: true
  }
})

const actionsClass = computed(() => {
  return {
    'modal-actions--single': !props.showCancel || !props.showConfirm,
    'modal-actions--vertical': props.theme === 'round-button'
  }
})

const icon = computed(() => {
  if (props.icon) return props.icon
  
  switch (props.type) {
    case 'success':
      return 'success'
    case 'warning':
      return 'warning-o'
    case 'error':
      return 'close'
    case 'info':
      return 'info-o'
    default:
      return 'question-o'
  }
})

const iconColor = computed(() => {
  if (props.iconColor) return props.iconColor
  
  switch (props.type) {
    case 'success':
      return '#07c160'
    case 'warning':
      return '#ff976a'
    case 'error':
      return '#ee0a24'
    case 'info':
      return '#1989fa'
    default:
      return '#646566'
  }
})

// 事件处理
const handleOpen = () => {
  emit('open')
}

const handleClose = () => {
  emit('close')
}

const handleOpened = () => {
  emit('opened')
}

const handleClosed = () => {
  emit('closed')
}

const handleOverlayClick = () => {
  emit('click-overlay')
}

const handleConfirm = () => {
  if (!props.confirmDisabled && !props.confirmLoading) {
    emit('confirm')
    if (!props.confirmLoading) {
      visible.value = false
    }
  }
}

const handleCancel = () => {
  if (!props.cancelDisabled && !props.cancelLoading) {
    emit('cancel')
    visible.value = false
  }
}
</script>

<style scoped lang="scss">
.confirm-modal {
  :deep(.van-popup) {
    width: v-bind(width);
    max-width: v-bind(maxWidth);
  }
}

.modal-content {
  padding: 0;
  overflow: hidden;
  
  &--round-button {
    .modal-actions {
      flex-direction: column;
      gap: 12px;
      
      .modal-btn {
        width: 100%;
        border-radius: 20px;
      }
    }
  }
}

.modal-header {
  padding: 24px 24px 16px;
  text-align: center;
  
  .modal-icon {
    margin-bottom: 16px;
    
    .modal-icon-img {
      display: block;
    }
  }
  
  .modal-title {
    font-size: 16px;
    font-weight: 600;
    color: #323233;
    margin: 0;
    line-height: 1.4;
  }
}

.modal-body {
  padding: 0 24px 24px;
  
  .modal-message {
    font-size: 14px;
    color: #646566;
    line-height: 1.6;
    text-align: center;
    word-wrap: break-word;
  }
}

.modal-footer {
  padding: 0 24px 24px;
  
  .modal-actions {
    display: flex;
    gap: 12px;
    
    &--single {
      .modal-btn {
        flex: 1;
      }
    }
    
    &--vertical {
      flex-direction: column;
      
      .modal-btn {
        width: 100%;
      }
    }
    
    .modal-btn {
      flex: 1;
      min-height: 40px;
      
      &.cancel-btn {
        border-color: #e5e5e5;
        color: #646566;
        
        &:hover {
          border-color: #c8c9cc;
        }
      }
      
      &.confirm-btn {
        // 使用默认的primary样式
      }
    }
  }
}

// 不同类型的样式调整
.modal-content--success {
  .modal-title {
    color: #07c160;
  }
}

.modal-content--warning {
  .modal-title {
    color: #ff976a;
  }
}

.modal-content--error {
  .modal-title {
    color: #ee0a24;
  }
}

.modal-content--info {
  .modal-title {
    color: #1989fa;
  }
}

// 移动端优化
@media (max-width: 767px) {
  .confirm-modal {
    :deep(.van-popup) {
      width: 300px;
      max-width: 85vw;
    }
  }
  
  .modal-header {
    padding: 20px 20px 12px;
    
    .modal-icon {
      margin-bottom: 12px;
    }
    
    .modal-title {
      font-size: 15px;
    }
  }
  
  .modal-body {
    padding: 0 20px 20px;
    
    .modal-message {
      font-size: 13px;
    }
  }
  
  .modal-footer {
    padding: 0 20px 20px;
    
    .modal-actions {
      gap: 10px;
      
      .modal-btn {
        min-height: 36px;
      }
    }
  }
}

// 无障碍支持
@media (prefers-reduced-motion: reduce) {
  .confirm-modal {
    :deep(.van-popup) {
      transition: none;
    }
  }
}
</style> 