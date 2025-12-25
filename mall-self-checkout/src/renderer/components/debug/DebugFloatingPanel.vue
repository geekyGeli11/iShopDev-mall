<template>
  <div
    v-if="isVisible"
    ref="panelRef"
    class="debug-floating-panel"
    :class="{ 'expanded': isExpanded, 'dragging': isDragging }"
    :style="panelStyle"
  >
    <!-- 头部控制栏 -->
    <div class="debug-header">
      <div class="debug-title" @click="toggleExpanded" style="cursor: pointer; flex: 1;">
        <span class="debug-icon">🐛</span>
        <span class="debug-text">调试面板</span>
        <span class="debug-count">({{ logs.length }})</span>
      </div>
      <div class="debug-drag-handle"
           @mousedown="startDrag"
           @touchstart="startDrag"
           title="拖拽移动">
        ⋮⋮
      </div>
      <div class="debug-controls">
        <button class="debug-btn" @click.stop="clearLogs" title="清空日志">
          🗑️
        </button>
        <button class="debug-btn" @click.stop="exportLogs" title="导出日志">
          📤
        </button>
        <button class="debug-btn" @click.stop="toggleExpanded" title="展开/收起">
          {{ isExpanded ? '📉' : '📈' }}
        </button>
        <button class="debug-btn" @click.stop="closePanel" title="关闭">
          ❌
        </button>
      </div>
    </div>

    <!-- 展开的内容区域 -->
    <div v-if="isExpanded" class="debug-content">
      <!-- 过滤器 -->
      <div class="debug-filters">
        <select v-model="selectedType" class="debug-select">
          <option value="">所有类型</option>
          <option value="scan">扫码</option>
          <option value="api">API</option>
          <option value="tts">语音播报</option>
          <option value="error">错误</option>
          <option value="info">信息</option>
          <option value="warning">警告</option>
          <option value="success">成功</option>
        </select>
        <select v-model="selectedLevel" class="debug-select">
          <option value="">所有级别</option>
          <option value="0">调试</option>
          <option value="1">信息</option>
          <option value="2">警告</option>
          <option value="3">错误</option>
        </select>
        <button class="debug-btn" @click="autoScroll = !autoScroll" :class="{ active: autoScroll }">
          {{ autoScroll ? '📌' : '📍' }}
        </button>
        <button class="debug-btn" @click="showTTSStatus = !showTTSStatus" :class="{ active: showTTSStatus }">
          🔊
        </button>
      </div>

      <!-- TTS 状态面板 -->
      <div v-if="showTTSStatus" class="debug-tts-status">
        <div class="tts-status-header">
          <span class="tts-status-title">🔊 TTS 语音播报状态</span>
          <button class="debug-btn-small" @click="refreshTTSStatus">🔄</button>
        </div>
        <div class="tts-status-content">
          <div v-if="ttsStatus" class="tts-status-grid">
            <div class="tts-status-item">
              <span class="tts-label">初始化状态:</span>
              <span :class="['tts-value', ttsStatus.isInitialized ? 'success' : 'error']">
                {{ ttsStatus.isInitialized ? '✅ 已初始化' : '❌ 未初始化' }}
              </span>
            </div>
            <div class="tts-status-item">
              <span class="tts-label">平台:</span>
              <span class="tts-value">{{ ttsStatus.platform || 'unknown' }}</span>
            </div>
            <div class="tts-status-item">
              <span class="tts-label">原生支持:</span>
              <span :class="['tts-value', ttsStatus.isNative ? 'success' : 'warning']">
                {{ ttsStatus.isNative ? '✅ 原生' : '⚠️ Web API' }}
              </span>
            </div>
            <div class="tts-status-item">
              <span class="tts-label">播放状态:</span>
              <span :class="['tts-value', ttsStatus.isPlaying ? 'warning' : 'success']">
                {{ ttsStatus.isPlaying ? '🔊 播放中' : '⏹️ 停止' }}
              </span>
            </div>
            <div class="tts-status-item">
              <span class="tts-label">支持语言:</span>
              <span class="tts-value">{{ ttsStatus.supportedLanguages?.length || 0 }} 种</span>
            </div>
            <div v-if="ttsStatus.nativeError" class="tts-status-item error">
              <span class="tts-label">错误信息:</span>
              <span class="tts-value">{{ ttsStatus.nativeError }}</span>
            </div>
          </div>
          <div v-else class="tts-status-loading">
            正在获取 TTS 状态...
          </div>
        </div>
      </div>

      <!-- 日志列表 -->
      <div ref="logListRef" class="debug-logs" @scroll="handleScroll">
        <div
          v-for="log in filteredLogs"
          :key="log.id"
          class="debug-log-item"
          :class="[`log-${log.type}`, `level-${log.level}`]"
        >
          <div class="log-header">
            <span class="log-emoji">{{ getLogEmoji(log.type, log.level) }}</span>
            <span class="log-time">{{ formatTime(log.timestamp) }}</span>
            <span class="log-title">{{ log.title }}</span>
          </div>
          <div class="log-message">{{ log.message }}</div>
          <div v-if="log.data" class="log-data">
            <details>
              <summary>详细数据</summary>
              <pre>{{ formatData(log.data) }}</pre>
            </details>
          </div>
          <div v-if="log.stack" class="log-stack">
            <details>
              <summary>错误堆栈</summary>
              <pre>{{ log.stack }}</pre>
            </details>
          </div>
        </div>
        
        <div v-if="filteredLogs.length === 0" class="debug-empty">
          暂无日志数据
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { debugLogger, type LogEntry, LogType, LogLevel } from '../../utils/debugLogger'
import { config } from '../../../config/env'

// 组件属性
interface Props {
  initialPosition?: { x: number; y: number }
  initialExpanded?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  initialPosition: () => ({ x: 20, y: 100 }),
  initialExpanded: false
})

// 响应式数据
const panelRef = ref<HTMLElement>()
const logListRef = ref<HTMLElement>()
const isVisible = ref(false)
const isExpanded = ref(props.initialExpanded)
const isDragging = ref(false)
const logs = ref<LogEntry[]>([])
const selectedType = ref('')
const selectedLevel = ref('')
const autoScroll = ref(true)

// TTS 状态相关
const showTTSStatus = ref(false)
const ttsStatus = ref<any>(null)

// 拖拽相关
const position = ref(props.initialPosition)
const dragStart = ref({ x: 0, y: 0 })
const dragOffset = ref({ x: 0, y: 0 })

// 计算属性
const panelStyle = computed(() => ({
  left: `${position.value.x}px`,
  top: `${position.value.y}px`,
  zIndex: 9999
}))

const filteredLogs = computed(() => {
  let filtered = logs.value

  if (selectedType.value) {
    filtered = filtered.filter(log => log.type === selectedType.value)
  }

  if (selectedLevel.value !== '') {
    const level = parseInt(selectedLevel.value)
    filtered = filtered.filter(log => log.level >= level)
  }

  return filtered
})

// 方法
const toggleExpanded = () => {
  isExpanded.value = !isExpanded.value
  if (isExpanded.value && autoScroll.value) {
    nextTick(() => scrollToBottom())
  }
}

const closePanel = () => {
  isVisible.value = false
}

const clearLogs = () => {
  debugLogger.clearLogs()
}

const exportLogs = () => {
  const data = debugLogger.exportLogs()
  const blob = new Blob([data], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `debug-logs-${new Date().toISOString().slice(0, 19).replace(/:/g, '-')}.json`
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  URL.revokeObjectURL(url)
}

const startDrag = (event: MouseEvent | TouchEvent) => {
  if (!panelRef.value) return
  
  isDragging.value = true
  
  const clientX = 'touches' in event ? event.touches[0].clientX : event.clientX
  const clientY = 'touches' in event ? event.touches[0].clientY : event.clientY
  
  dragStart.value = { x: clientX, y: clientY }
  dragOffset.value = { x: position.value.x, y: position.value.y }
  
  document.addEventListener('mousemove', handleDrag)
  document.addEventListener('mouseup', stopDrag)
  document.addEventListener('touchmove', handleDrag)
  document.addEventListener('touchend', stopDrag)
  
  event.preventDefault()
}

const handleDrag = (event: MouseEvent | TouchEvent) => {
  if (!isDragging.value) return
  
  const clientX = 'touches' in event ? event.touches[0].clientX : event.clientX
  const clientY = 'touches' in event ? event.touches[0].clientY : event.clientY
  
  const deltaX = clientX - dragStart.value.x
  const deltaY = clientY - dragStart.value.y
  
  position.value = {
    x: Math.max(0, Math.min(window.innerWidth - 300, dragOffset.value.x + deltaX)),
    y: Math.max(0, Math.min(window.innerHeight - 100, dragOffset.value.y + deltaY))
  }
}

const stopDrag = () => {
  isDragging.value = false
  document.removeEventListener('mousemove', handleDrag)
  document.removeEventListener('mouseup', stopDrag)
  document.removeEventListener('touchmove', handleDrag)
  document.removeEventListener('touchend', stopDrag)
}

const handleScroll = () => {
  if (!logListRef.value) return
  
  const { scrollTop, scrollHeight, clientHeight } = logListRef.value
  const isAtBottom = scrollTop + clientHeight >= scrollHeight - 10
  
  if (!isAtBottom) {
    autoScroll.value = false
  }
}

const scrollToBottom = () => {
  if (logListRef.value) {
    logListRef.value.scrollTop = logListRef.value.scrollHeight
  }
}

// TTS 状态相关方法
const refreshTTSStatus = async () => {
  try {
    // 动态导入 TTS 管理器以避免循环依赖
    const { TTSManager } = await import('../../utils/tts')
    const ttsInstance = TTSManager.getInstance()
    ttsStatus.value = ttsInstance.getDiagnosticInfo()
  } catch (error) {
    console.error('获取 TTS 状态失败:', error)
    ttsStatus.value = {
      error: '无法获取 TTS 状态',
      errorMessage: (error as Error).message
    }
  }
}

const formatTime = (timestamp: number): string => {
  return new Date(timestamp).toLocaleTimeString()
}

const formatData = (data: any): string => {
  try {
    return JSON.stringify(data, null, 2)
  } catch {
    return String(data)
  }
}

const getLogEmoji = (type: LogType, level: LogLevel): string => {
  if (level === LogLevel.ERROR) return '❌'
  if (level === LogLevel.WARNING) return '⚠️'

  switch (type) {
    case LogType.SCAN: return '📱'
    case LogType.API: return '🌐'
    case LogType.TTS: return '🔊'
    case LogType.SUCCESS: return '✅'
    case LogType.INFO: return 'ℹ️'
    default: return '🐛'
  }
}

const updateLogs = (newLogs: LogEntry[]) => {
  logs.value = newLogs
  if (isExpanded.value && autoScroll.value) {
    nextTick(() => scrollToBottom())
  }
}

// 监听日志变化
watch(() => filteredLogs.value.length, () => {
  if (isExpanded.value && autoScroll.value) {
    nextTick(() => scrollToBottom())
  }
})

// 生命周期
onMounted(() => {
  // 检查环境和调试状态
  console.log('🔍 调试面板初始化检查:')
  console.log('  - config.isDev:', config.isDev)
  console.log('  - debugLogger.isDebugEnabled():', debugLogger.isDebugEnabled())
  console.log('  - import.meta.env.MODE:', import.meta.env.MODE)

  // 在开发环境或明确启用调试时显示
  const shouldShow = config.isDev || debugLogger.isDebugEnabled() || import.meta.env.MODE === 'development'
  isVisible.value = shouldShow

  console.log('  - 调试面板是否显示:', isVisible.value)

  if (isVisible.value) {
    // 获取初始日志
    logs.value = debugLogger.getLogs()
    console.log('  - 初始日志数量:', logs.value.length)

    // 监听日志更新
    debugLogger.addListener(updateLogs)

    console.log('🐛 调试浮窗已启动')

    // 添加一些测试日志来验证功能
    setTimeout(() => {
      debugLogger.logInfo('调试面板测试', '调试面板已成功加载')
      debugLogger.logSuccess('功能验证', '日志系统正常工作')

      // 初始化 TTS 状态
      refreshTTSStatus()
    }, 500)
  } else {
    console.log('⚠️ 调试面板未启动 - 不在开发环境')
  }
})

onUnmounted(() => {
  if (isVisible.value) {
    debugLogger.removeListener(updateLogs)
    stopDrag()
  }
})

// 暴露方法给父组件
defineExpose({
  show: () => { isVisible.value = true },
  hide: () => { isVisible.value = false },
  toggle: () => { isVisible.value = !isVisible.value },
  expand: () => { isExpanded.value = true },
  collapse: () => { isExpanded.value = false }
})
</script>

<style scoped>
.debug-floating-panel {
  position: fixed;
  background: rgba(0, 0, 0, 0.9);
  border: 1px solid #333;
  border-radius: 8px;
  color: #fff;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  min-width: 300px;
  max-width: 90vw;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5);
  user-select: none;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.debug-floating-panel.dragging {
  transition: none;
  cursor: grabbing;
}

.debug-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px 8px 0 0;
  border-bottom: 1px solid #333;
}

.debug-drag-handle {
  cursor: grab;
  padding: 4px 8px;
  color: #888;
  font-size: 14px;
  user-select: none;
  border-radius: 4px;
  transition: background 0.2s;
}

.debug-drag-handle:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}

.debug-drag-handle:active {
  cursor: grabbing;
}

.debug-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: bold;
}

.debug-controls {
  display: flex;
  gap: 4px;
}

.debug-btn {
  background: none;
  border: none;
  color: #fff;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  font-size: 12px;
  transition: background 0.2s;
}

.debug-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

.debug-btn.active {
  background: rgba(0, 123, 255, 0.5);
}

.debug-content {
  max-height: 400px;
  display: flex;
  flex-direction: column;
}

.debug-filters {
  display: flex;
  gap: 8px;
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.05);
  border-bottom: 1px solid #333;
}

.debug-select {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid #555;
  color: #fff;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 11px;
}

.debug-logs {
  flex: 1;
  overflow-y: auto;
  max-height: 300px;
  padding: 8px;
}

.debug-log-item {
  margin-bottom: 8px;
  padding: 8px;
  border-radius: 4px;
  border-left: 3px solid #555;
}

.debug-log-item.log-error {
  border-left-color: #ff4757;
  background: rgba(255, 71, 87, 0.1);
}

.debug-log-item.log-warning {
  border-left-color: #ffa502;
  background: rgba(255, 165, 2, 0.1);
}

.debug-log-item.log-success {
  border-left-color: #2ed573;
  background: rgba(46, 213, 115, 0.1);
}

.debug-log-item.log-scan {
  border-left-color: #3742fa;
  background: rgba(55, 66, 250, 0.1);
}

.debug-log-item.log-api {
  border-left-color: #5352ed;
  background: rgba(83, 82, 237, 0.1);
}

.log-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.log-time {
  color: #888;
  font-size: 10px;
}

.log-title {
  font-weight: bold;
  color: #fff;
}

.log-message {
  color: #ccc;
  margin-bottom: 4px;
  word-break: break-word;
}

.log-data, .log-stack {
  margin-top: 4px;
}

.log-data details, .log-stack details {
  cursor: pointer;
}

.log-data summary, .log-stack summary {
  color: #888;
  font-size: 10px;
  margin-bottom: 4px;
}

.log-data pre, .log-stack pre {
  background: rgba(0, 0, 0, 0.3);
  padding: 8px;
  border-radius: 4px;
  font-size: 10px;
  overflow-x: auto;
  white-space: pre-wrap;
  word-break: break-word;
}

.debug-empty {
  text-align: center;
  color: #888;
  padding: 20px;
}

/* 滚动条样式 */
.debug-logs::-webkit-scrollbar {
  width: 6px;
}

.debug-logs::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 3px;
}

.debug-logs::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
}

.debug-logs::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.5);
}

/* TTS 状态面板样式 */
.debug-tts-status {
  background: rgba(0, 0, 0, 0.8);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 6px;
  margin-bottom: 10px;
  overflow: hidden;
}

.tts-status-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.1);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.tts-status-title {
  color: #fff;
  font-size: 12px;
  font-weight: bold;
}

.debug-btn-small {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 3px;
  color: #fff;
  cursor: pointer;
  font-size: 10px;
  padding: 2px 6px;
  transition: all 0.2s;
}

.debug-btn-small:hover {
  background: rgba(255, 255, 255, 0.2);
}

.tts-status-content {
  padding: 10px;
}

.tts-status-grid {
  display: grid;
  gap: 8px;
}

.tts-status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.tts-status-item:last-child {
  border-bottom: none;
}

.tts-status-item.error {
  background: rgba(255, 0, 0, 0.1);
  border-radius: 4px;
  padding: 6px 8px;
  margin: 4px 0;
}

.tts-label {
  color: #ccc;
  font-size: 11px;
  min-width: 80px;
}

.tts-value {
  color: #fff;
  font-size: 11px;
  text-align: right;
  flex: 1;
}

.tts-value.success {
  color: #4CAF50;
}

.tts-value.warning {
  color: #FF9800;
}

.tts-value.error {
  color: #F44336;
}

.tts-status-loading {
  text-align: center;
  color: #888;
  font-size: 11px;
  padding: 10px;
}
</style>
