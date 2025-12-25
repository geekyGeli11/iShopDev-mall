<template>
  <van-dialog
    v-model:show="showDialog"
    :title="dialogTitle"
    :show-cancel-button="!isForceUpdate && !isUpdating"
    :cancel-button-text="'稍后更新'"
    :confirm-button-text="confirmButtonText"
    :close-on-click-overlay="false"
    :close-on-popstate="false"
    @confirm="handleConfirm"
    @cancel="handleCancel"
  >
    <div class="update-dialog-content">
      <!-- 版本信息 -->
      <div v-if="versionInfo" class="version-info">
        <div class="version-item">
          <span class="label">新版本：</span>
          <span class="value">{{ versionInfo.versionName }}</span>
          <van-tag v-if="isForceUpdate" type="danger" size="mini">强制更新</van-tag>
        </div>
        <div class="version-item">
          <span class="label">文件大小：</span>
          <span class="value">{{ versionInfo.apkFileSizeFormatted }}</span>
        </div>
        <div class="version-item">
          <span class="label">发布时间：</span>
          <span class="value">{{ formatReleaseTime(versionInfo.releaseTime) }}</span>
        </div>
      </div>

      <!-- 更新内容 -->
      <div v-if="versionInfo?.updateContent" class="update-content">
        <div class="content-title">更新内容：</div>
        <div class="content-text">{{ versionInfo.updateContent }}</div>
      </div>

      <!-- 进度显示 -->
      <div v-if="isUpdating" class="progress-section">
        <div class="progress-text">{{ updateProgress.message }}</div>
        <van-progress 
          :percentage="updateProgress.progress" 
          :show-pivot="true"
          :color="progressColor"
        />
        <div v-if="updateProgress.error" class="error-text">
          {{ updateProgress.error }}
        </div>
      </div>

      <!-- 错误信息 -->
      <div v-if="errorMessage" class="error-section">
        <van-icon name="warning-o" color="#ee0a24" />
        <span class="error-text">{{ errorMessage }}</span>
      </div>
    </div>
  </van-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { Dialog, Progress, Tag, Icon } from 'vant'
import type { VersionInfo, UpdateProgress } from '@/shared/services/AppUpdateService'

interface Props {
  show: boolean
  versionInfo?: VersionInfo
  isForceUpdate?: boolean
  updateProgress?: UpdateProgress
  errorMessage?: string
}

interface Emits {
  (e: 'update:show', value: boolean): void
  (e: 'confirm'): void
  (e: 'cancel'): void
}

const props = withDefaults(defineProps<Props>(), {
  show: false,
  isForceUpdate: false
})

const emit = defineEmits<Emits>()

const showDialog = computed({
  get: () => props.show,
  set: (value) => emit('update:show', value)
})

const isUpdating = computed(() => {
  return props.updateProgress && 
         ['downloading', 'installing'].includes(props.updateProgress.status)
})

const dialogTitle = computed(() => {
  if (props.errorMessage) return '更新失败'
  if (isUpdating.value) return '正在更新'
  return '发现新版本'
})

const confirmButtonText = computed(() => {
  if (props.errorMessage) return '重试'
  if (isUpdating.value) return '更新中...'
  return props.isForceUpdate ? '立即更新' : '立即更新'
})

const progressColor = computed(() => {
  if (!props.updateProgress) return '#1989fa'
  
  switch (props.updateProgress.status) {
    case 'downloading':
      return '#1989fa'
    case 'installing':
      return '#07c160'
    case 'failed':
      return '#ee0a24'
    case 'completed':
      return '#07c160'
    default:
      return '#1989fa'
  }
})

const handleConfirm = () => {
  emit('confirm')
}

const handleCancel = () => {
  if (!props.isForceUpdate && !isUpdating.value) {
    emit('cancel')
  }
}

const formatReleaseTime = (timeStr: string) => {
  try {
    const date = new Date(timeStr)
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch {
    return timeStr
  }
}

// 监听强制更新，禁用返回键
watch(() => props.isForceUpdate, (isForce) => {
  if (isForce && props.show) {
    // 禁用Android返回键
    document.addEventListener('backbutton', preventBack, false)
  } else {
    document.removeEventListener('backbutton', preventBack, false)
  }
})

const preventBack = (e: Event) => {
  e.preventDefault()
  e.stopPropagation()
  return false
}
</script>

<style scoped>
.update-dialog-content {
  padding: 16px 0;
}

.version-info {
  margin-bottom: 16px;
}

.version-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
}

.version-item .label {
  color: #646566;
  width: 80px;
  flex-shrink: 0;
}

.version-item .value {
  color: #323233;
  flex: 1;
}

.version-item .van-tag {
  margin-left: 8px;
}

.update-content {
  margin-bottom: 16px;
}

.content-title {
  font-size: 14px;
  color: #323233;
  font-weight: 500;
  margin-bottom: 8px;
}

.content-text {
  font-size: 13px;
  color: #646566;
  line-height: 1.5;
  background: #f7f8fa;
  padding: 12px;
  border-radius: 4px;
  white-space: pre-wrap;
}

.progress-section {
  margin-bottom: 16px;
}

.progress-text {
  font-size: 14px;
  color: #323233;
  margin-bottom: 12px;
  text-align: center;
}

.error-section {
  display: flex;
  align-items: center;
  padding: 12px;
  background: #fef0f0;
  border-radius: 4px;
  margin-bottom: 16px;
}

.error-text {
  font-size: 13px;
  color: #ee0a24;
  margin-left: 8px;
  line-height: 1.4;
}
</style>
