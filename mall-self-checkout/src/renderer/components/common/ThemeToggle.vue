<template>
  <div class="theme-toggle">
    <!-- 使用Vant按钮组件 -->
    <van-button 
      :icon="currentThemeIcon"
      :title="currentThemeText"
      size="large"
      type="default"
      round
      @click="toggleTheme"
      class="toggle-btn"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { darkModeManager, ThemeMode, type DarkModeState } from '@/utils/darkMode'
import { hapticFeedback } from '@/utils/platform'

// 响应式数据
const darkModeState = ref<DarkModeState>(darkModeManager.getState())

// 主题选项配置
const themeOptions = [
  {
    value: ThemeMode.LIGHT,
    name: '浅色模式',
    description: '始终使用浅色主题',
    icon: 'sun-o'
  },
  {
    value: ThemeMode.DARK,
    name: '深色模式',
    description: '始终使用深色主题',
    icon: 'moon-o'
  },
  {
    value: ThemeMode.AUTO,
    name: '跟随系统',
    description: '根据系统设置自动切换',
    icon: 'setting-o'
  }
]

// 计算属性
const currentTheme = computed(() => darkModeState.value.currentTheme)

const currentThemeText = computed(() => {
  const option = themeOptions.find(opt => opt.value === currentTheme.value)
  return option ? option.name : '主题设置'
})

const currentThemeIcon = computed(() => {
  const option = themeOptions.find(opt => opt.value === currentTheme.value)
  return option ? option.icon : 'setting-o'
})

// 深色模式监听器
let removeListener: (() => void) | null = null

// 简化的主题切换方法
const toggleTheme = () => {
  console.log('点击主题切换按钮')
  hapticFeedback()
  
  // 循环切换: light -> dark -> auto -> light
  switch (currentTheme.value) {
    case ThemeMode.LIGHT:
      console.log('切换到深色模式')
      darkModeManager.setTheme(ThemeMode.DARK)
      break
    case ThemeMode.DARK:
      console.log('切换到自动模式')
      darkModeManager.setTheme(ThemeMode.AUTO)
      break
    case ThemeMode.AUTO:
      console.log('切换到浅色模式')
      darkModeManager.setTheme(ThemeMode.LIGHT)
      break
    default:
      console.log('默认切换到浅色模式')
      darkModeManager.setTheme(ThemeMode.LIGHT)
      break
  }
  
  // 更新状态
  darkModeState.value = darkModeManager.getState()
  console.log('主题切换完成，当前状态:', darkModeState.value)
}

// 监听系统主题变化
const handleDarkModeChange = () => {
  console.log('检测到系统主题变化')
  darkModeState.value = darkModeManager.getState()
}

// 生命周期
onMounted(() => {
  console.log('ThemeToggle组件已挂载，当前主题状态:', darkModeState.value)
  
  // 添加深色模式监听器
  removeListener = darkModeManager.addListener(handleDarkModeChange)
})

onUnmounted(() => {
  // 移除监听器
  if (removeListener) {
    removeListener()
  }
})
</script>

<style scoped lang="scss">
.theme-toggle {
  display: inline-block;
}

.toggle-btn {
  // 使用Vant的设计规范，只需要简单的自定义样式
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.95);
  }
  
  // 确保在不同平台上的一致性
  :deep(.van-button__content) {
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  :deep(.van-icon) {
    font-size: 18px;
  }
}

// 移动端优化
@media (max-width: 767px) {
  .toggle-btn {
    // Vant已经处理了移动端优化，只需要确保触摸区域足够大
    min-width: 44px;
    min-height: 44px;
  }
}

// 无障碍支持
@media (prefers-reduced-motion: reduce) {
  .toggle-btn {
    transition: none;
    
    &:active {
      transform: none;
    }
  }
}
</style> 