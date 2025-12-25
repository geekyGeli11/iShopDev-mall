<template>
  <div class="numeric-keyboard-container" v-show="visible">
    <div class="keyboard-overlay" @click="$emit('close')"></div>
    <div class="keyboard-wrapper" :data-keyboard-type="keyboardType">
      <div class="keyboard-header">
        <span class="keyboard-title">{{ title }}</span>
        <button class="keyboard-close" @click="$emit('close')">×</button>
      </div>
      <div class="keyboard-display">
        <div class="input-display">{{ displayValue }}</div>
      </div>
      <div :class="keyboardClass" ref="keyboardRef" class="simple-keyboard-numeric"></div>
      <div class="keyboard-actions">
        <button class="keyboard-action cancel" @click="$emit('cancel')">
          取消
        </button>
        <button class="keyboard-action confirm" @click="handleConfirm" :disabled="!displayValue">
          确认
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import Keyboard from 'simple-keyboard'
import 'simple-keyboard/build/css/index.css'

interface Props {
  visible: boolean
  title?: string
  placeholder?: string
  maxLength?: number
  keyboardType?: 'numeric' | 'phone' | 'memberCode'
  value?: string
}

interface Emits {
  'update:value': [value: string]
  'confirm': [value: string]
  'cancel': []
  'close': []
}

const props = withDefaults(defineProps<Props>(), {
  title: '数字输入',
  placeholder: '请输入',
  maxLength: 11,
  keyboardType: 'numeric',
  value: ''
})

const emit = defineEmits<Emits>()

const keyboardRef = ref<HTMLDivElement>()
const keyboardClass = 'simple-keyboard-numeric'
const displayValue = ref('')
let keyboard: Keyboard | null = null

// 根据键盘类型定义不同的布局
const getKeyboardLayout = () => {
  const layouts = {
    numeric: {
      default: [
        '1 2 3',
        '4 5 6', 
        '7 8 9',
        '{clear} 0 {bksp}'
      ]
    },
    phone: {
      default: [
        '1 2 3',
        '4 5 6',
        '7 8 9',
        '{clear} 0 {bksp}'
      ]
    },
    memberCode: {
      default: [
        '1 2 3',
        '4 5 6',
        '7 8 9',
        '{clear} 0 {bksp}',
        'M {space}'
      ]
    }
  }
  
  return layouts[props.keyboardType] || layouts.numeric
}

// 初始化键盘
const initKeyboard = () => {
  if (!keyboardRef.value) return

  try {
    // 直接使用 DOM 元素而不是选择器
    keyboard = new Keyboard(keyboardRef.value, {
      onKeyPress: handleKeyPress,
      layout: getKeyboardLayout(),
      display: {
        '{bksp}': '⌫',
        '{clear}': '清空',
        '{space}': '空格'
      },
      theme: 'hg-theme-default hg-theme-numeric',
      mergeDisplay: true,
      preventMouseDownDefault: true
    })
    
    // 设置初始值
    if (props.value) {
      keyboard.setInput(props.value)
      displayValue.value = props.value
    }
  } catch (error) {
    console.error('键盘初始化失败:', error)
  }
}

// 处理按键点击
const handleKeyPress = (button: string) => {
  if (button === '{clear}') {
    displayValue.value = ''
    keyboard?.setInput('')
    emit('update:value', '')
  } else if (button === '{bksp}') {
    const currentInput = displayValue.value
    const newInput = currentInput.slice(0, -1)
    displayValue.value = newInput
    keyboard?.setInput(newInput)
    emit('update:value', newInput)
  } else if (button === '{space}') {
    // 空格处理
    const currentInput = displayValue.value
    if (currentInput.length < props.maxLength) {
      const newInput = currentInput + ' '
      displayValue.value = newInput
      keyboard?.setInput(newInput)
      emit('update:value', newInput)
    }
  } else {
    // 普通字符输入
    const currentInput = displayValue.value
    if (currentInput.length < props.maxLength) {
      const newInput = currentInput + button
      displayValue.value = newInput
      keyboard?.setInput(newInput)
      emit('update:value', newInput)
    }
  }
  
  // 播放按键音效
  playKeySound()
}

// 播放按键音效
const playKeySound = () => {
  // 创建简单的按键音效
  const audioContext = new (window.AudioContext || (window as any).webkitAudioContext)()
  const oscillator = audioContext.createOscillator()
  const gainNode = audioContext.createGain()
  
  oscillator.connect(gainNode)
  gainNode.connect(audioContext.destination)
  
  oscillator.frequency.value = 800
  oscillator.type = 'sine'
  
  gainNode.gain.setValueAtTime(0.1, audioContext.currentTime)
  gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.1)
  
  oscillator.start(audioContext.currentTime)
  oscillator.stop(audioContext.currentTime + 0.1)
}

// 确认输入
const handleConfirm = () => {
  emit('confirm', displayValue.value)
}

// 监听外部值变化
watch(() => props.value, (newValue) => {
  if (newValue !== displayValue.value) {
    displayValue.value = newValue || ''
    if (keyboard) {
      keyboard.setInput(displayValue.value)
    }
  }
})

// 监听显示状态
watch(() => props.visible, async (visible) => {
  if (visible) {
    await nextTick()
    if (!keyboard) {
      initKeyboard()
    }
    displayValue.value = props.value || ''
    if (keyboard) {
      keyboard.setInput(displayValue.value)
    }
  }
})

onMounted(() => {
  if (props.visible) {
    initKeyboard()
  }
})

onUnmounted(() => {
  if (keyboard) {
    keyboard.destroy()
  }
})
</script>

<style scoped lang="scss">
.numeric-keyboard-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 3000;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}

.keyboard-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
}

.keyboard-wrapper {
  position: relative;
  width: 100%;
  max-width: 100%;
  background: #f8f9fa;
  border-radius: 16px 16px 0 0;
  box-shadow: 0 -8px 32px rgba(0, 0, 0, 0.2);
  animation: slideUp 0.3s ease-out;
  max-height: 85vh;
  overflow-y: auto;
  
  @media (max-width: 480px) {
    max-width: 100%;
    border-radius: 0;
  }
}

.keyboard-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid #e9ecef;
  
  .keyboard-title {
    font-size: 40px;
    font-weight: 600;
    color: #333;
  }
  
  .keyboard-close {
    background: none;
    border: none;
    font-size: 40px;
    color: #999;
    cursor: pointer;
    padding: 0;
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    transition: all 0.2s;
    
    &:hover {
      background: #e9ecef;
      color: #666;
    }
  }
}

.keyboard-display {
  padding: 20px;
  
  .input-display {
    background: white;
    border: 2px solid #e9ecef;
    border-radius: 8px;
    padding: 16px;
    font-size: 40px;
    font-family: 'Consolas', 'Monaco', monospace;
    min-height: 40px;
    color: #333;
    text-align: center;
    letter-spacing: 2px;
    
    &:empty::before {
      content: '请输入数字';
      color: #999;
    }
  }
}

.keyboard-actions {
  display: flex;
  gap: 12px;
  padding: 16px 20px 20px;
  
  .keyboard-action {
    flex: 1;
    height: 150px;
    border: none;
    border-radius: 8px;
    font-size: 48px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;
    
    &.cancel {
      background: #f8f8f8;
      color: #666;
      border: 1px solid #e9ecef;
      
      &:hover {
        background: #e9ecef;
      }
    }
    
    &.confirm {
      background: #0A0D05;
      color: #A9FF00;
      
      &:hover:not(:disabled) {
        background: #0A0D05;
      }
      
      &:disabled {
        background: #0A0D05;
        cursor: not-allowed;
      }
    }
  }
}

@keyframes slideUp {
  from {
    transform: translateY(100%);
  }
  to {
    transform: translateY(0);
  }
}

// 自定义 simple-keyboard 样式
:deep(.simple-keyboard-numeric) {
  background: transparent;
  border-radius: 0;
  padding: 0 20px;
  min-height: 200px;
  
  .hg-button {
    background: white;
    border: 1px solid #e9ecef;
    color: #000;
    font-size: 48px;
    font-weight: 600;
    height: 175px;
    margin: 4px;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    transition: all 0.2s;
    cursor: pointer;
    user-select: none;
    display: flex;
    align-items: center;
    justify-content: center;
    
    &:hover {
      background: #f8f9fa;
      transform: translateY(-1px);
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
    }
    
    &:active {
      transform: translateY(0);
      box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
      background: #e9ecef;
    }
    
    // 特殊按键样式
    &.hg-functionBtn {
      background: #6c757d;
      color: white;
      font-size: 48px;
      
      &:hover {
        background: #5a6268;
      }
    }
  }
  
  .hg-row {
    display: flex;
    justify-content: center;
    margin-bottom: 8px;
    
    &:last-child {
      margin-bottom: 0;
    }
  }
}
</style>