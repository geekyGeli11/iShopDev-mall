<template>
  <div class="update-settings">
    <van-nav-bar
      title="更新设置"
      left-text="返回"
      left-arrow
      @click-left="$emit('back')"
    />

    <div class="settings-content">
      <!-- 当前版本信息 -->
      <van-cell-group title="版本信息">
        <van-cell title="当前版本" :value="currentVersion" />
        <van-cell 
          title="检查更新" 
          is-link 
          @click="handleCheckUpdate"
          :loading="isChecking"
        >
          <template #value>
            <span v-if="!isChecking">立即检查</span>
          </template>
        </van-cell>
      </van-cell-group>

      <!-- 自动更新设置 -->
      <van-cell-group title="自动更新">
        <van-cell title="自动检查更新">
          <template #right-icon>
            <van-switch 
              v-model="localConfig.autoCheck" 
              @change="handleConfigChange"
            />
          </template>
        </van-cell>
        
        <van-cell 
          title="检查间隔" 
          is-link 
          :value="intervalText"
          @click="showIntervalPicker = true"
        />
        
        <van-cell title="仅WiFi下载">
          <template #right-icon>
            <van-switch 
              v-model="localConfig.downloadOnWifi" 
              @change="handleConfigChange"
            />
          </template>
        </van-cell>
        
        <van-cell title="空闲时安装">
          <template #right-icon>
            <van-switch 
              v-model="localConfig.installOnIdle" 
              @change="handleConfigChange"
            />
          </template>
        </van-cell>
      </van-cell-group>

      <!-- 更新时间设置 -->
      <van-cell-group title="更新时间">
        <van-cell 
          title="允许更新时间" 
          is-link 
          :value="timeRangeText"
          @click="showTimePicker = true"
        />
      </van-cell-group>

      <!-- 更新历史 -->
      <van-cell-group title="更新历史">
        <van-cell 
          title="查看更新日志" 
          is-link 
          @click="showUpdateHistory"
        />
        <van-cell 
          title="清除缓存" 
          is-link 
          @click="clearUpdateCache"
        />
      </van-cell-group>

      <!-- 高级设置 -->
      <van-cell-group title="高级设置">
        <van-cell 
          title="重置设置" 
          is-link 
          @click="resetSettings"
        />
      </van-cell-group>
    </div>

    <!-- 检查间隔选择器 -->
    <van-popup v-model:show="showIntervalPicker" position="bottom">
      <van-picker
        title="选择检查间隔"
        :columns="intervalOptions"
        @confirm="onIntervalConfirm"
        @cancel="showIntervalPicker = false"
      />
    </van-popup>

    <!-- 时间范围选择器 -->
    <van-popup v-model:show="showTimePicker" position="bottom">
      <div class="time-picker-container">
        <div class="time-picker-header">
          <van-button plain @click="showTimePicker = false">取消</van-button>
          <span class="title">设置更新时间</span>
          <van-button type="primary" @click="onTimeConfirm">确定</van-button>
        </div>
        
        <div class="time-picker-content">
          <div class="time-item">
            <span class="label">开始时间：</span>
            <van-time-picker
              v-model="tempStartTime"
              title="开始时间"
            />
          </div>
          
          <div class="time-item">
            <span class="label">结束时间：</span>
            <van-time-picker
              v-model="tempEndTime"
              title="结束时间"
            />
          </div>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted } from 'vue'
import { 
  NavBar, CellGroup, Cell, Switch, Popup, Picker, TimePicker, Button,
  showToast, showDialog, showConfirmDialog
} from 'vant'
import { useAppUpdate } from '@/renderer/composables/useAppUpdate'
import type { UpdateConfig } from '@/shared/services/AppUpdateService'

interface Emits {
  (e: 'back'): void
}

const emit = defineEmits<Emits>()

const { 
  isChecking,
  checkForUpdates,
  getUpdateConfig,
  saveUpdateConfig,
  getCurrentVersionInfo
} = useAppUpdate()

// 本地配置状态
const localConfig = reactive<UpdateConfig>({
  autoCheck: true,
  checkInterval: 4 * 60 * 60 * 1000,
  downloadOnWifi: true,
  installOnIdle: true,
  allowedUpdateTime: {
    start: '02:00',
    end: '06:00'
  }
})

// UI状态
const showIntervalPicker = ref(false)
const showTimePicker = ref(false)
const tempStartTime = ref(['02', '00'])
const tempEndTime = ref(['06', '00'])

// 当前版本
const currentVersion = computed(() => {
  const versionInfo = getCurrentVersionInfo()
  return versionInfo ? `${versionInfo.versionName} (${versionInfo.versionCode})` : '未知'
})

// 检查间隔文本
const intervalText = computed(() => {
  const hours = localConfig.checkInterval / (60 * 60 * 1000)
  return `${hours}小时`
})

// 时间范围文本
const timeRangeText = computed(() => {
  return `${localConfig.allowedUpdateTime.start} - ${localConfig.allowedUpdateTime.end}`
})

// 间隔选项
const intervalOptions = [
  { text: '1小时', value: 1 * 60 * 60 * 1000 },
  { text: '2小时', value: 2 * 60 * 60 * 1000 },
  { text: '4小时', value: 4 * 60 * 60 * 1000 },
  { text: '6小时', value: 6 * 60 * 60 * 1000 },
  { text: '12小时', value: 12 * 60 * 60 * 1000 },
  { text: '24小时', value: 24 * 60 * 60 * 1000 }
]

// 处理配置变更
const handleConfigChange = () => {
  saveUpdateConfig(localConfig)
}

// 处理检查更新
const handleCheckUpdate = async () => {
  await checkForUpdates(true)
}

// 间隔选择确认
const onIntervalConfirm = ({ selectedOptions }: any) => {
  localConfig.checkInterval = selectedOptions[0].value
  showIntervalPicker.value = false
  handleConfigChange()
}

// 时间选择确认
const onTimeConfirm = () => {
  localConfig.allowedUpdateTime.start = `${tempStartTime.value[0]}:${tempStartTime.value[1]}`
  localConfig.allowedUpdateTime.end = `${tempEndTime.value[0]}:${tempEndTime.value[1]}`
  showTimePicker.value = false
  handleConfigChange()
}

// 显示更新历史
const showUpdateHistory = () => {
  showDialog({
    title: '更新历史',
    message: '暂未实现更新历史功能',
    showCancelButton: false
  })
}

// 清除更新缓存
const clearUpdateCache = async () => {
  try {
    const confirmed = await showConfirmDialog({
      title: '清除缓存',
      message: '确定要清除所有更新缓存吗？'
    })
    
    if (confirmed) {
      // 这里实现清除缓存的逻辑
      showToast({
        type: 'success',
        message: '缓存已清除'
      })
    }
  } catch {
    // 用户取消
  }
}

// 重置设置
const resetSettings = async () => {
  try {
    const confirmed = await showConfirmDialog({
      title: '重置设置',
      message: '确定要重置所有更新设置吗？'
    })
    
    if (confirmed) {
      // 重置为默认配置
      Object.assign(localConfig, {
        autoCheck: true,
        checkInterval: 4 * 60 * 60 * 1000,
        downloadOnWifi: true,
        installOnIdle: true,
        allowedUpdateTime: {
          start: '02:00',
          end: '06:00'
        }
      })
      
      handleConfigChange()
      
      showToast({
        type: 'success',
        message: '设置已重置'
      })
    }
  } catch {
    // 用户取消
  }
}

// 初始化
onMounted(() => {
  // 加载当前配置
  const currentConfig = getUpdateConfig()
  Object.assign(localConfig, currentConfig)
  
  // 初始化时间选择器
  const [startHour, startMinute] = localConfig.allowedUpdateTime.start.split(':')
  const [endHour, endMinute] = localConfig.allowedUpdateTime.end.split(':')
  
  tempStartTime.value = [startHour, startMinute]
  tempEndTime.value = [endHour, endMinute]
})
</script>

<style scoped>
.update-settings {
  height: 100vh;
  background: #f7f8fa;
}

.settings-content {
  padding-bottom: 20px;
}

.time-picker-container {
  background: white;
}

.time-picker-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  border-bottom: 1px solid #ebedf0;
}

.time-picker-header .title {
  font-size: 16px;
  font-weight: 500;
  color: #323233;
}

.time-picker-content {
  padding: 20px;
}

.time-item {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.time-item .label {
  width: 80px;
  font-size: 14px;
  color: #646566;
}

.time-item:last-child {
  margin-bottom: 0;
}
</style>
