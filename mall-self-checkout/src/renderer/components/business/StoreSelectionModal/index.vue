<template>
  <div>
    <!-- 门店选择弹窗 -->
    <div v-if="props.modelValue" :class="{ 'active': props.modelValue }" class="modal-overlay" @click="handleClose">
      <div class="modal-content" :class="{ 'active': props.modelValue }" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">选择门店</h3>
          <button class="close-btn" @click="handleClose">×</button>
        </div>
        
        <div class="modal-body">
          <!-- 学校选择 -->
          <div class="school-section">
            <h4 class="section-title">选择学校</h4>
            <div class="school-list">
              <div 
                v-for="school in schoolList" 
                :key="school.id"
                :class="['school-item', { 'active': selectedSchoolId === school.id }]"
                @click="selectSchool(school)"
              >
                <div class="school-info">
                  <div class="school-name">{{ school.schoolName }}</div>
                  <div class="school-address">{{ school.address || '暂无地址信息' }}</div>
                </div>
                <div class="school-status">
                  <van-icon v-if="selectedSchoolId === school.id" name="success" color="#07c160" />
                </div>
              </div>
            </div>
          </div>

          <!-- 门店选择 -->
          <div v-if="selectedSchoolId" class="store-section">
            <h4 class="section-title">选择门店</h4>
            <div v-if="loadingStores" class="loading-container">
              <van-loading type="spinner" color="#1989fa">加载门店中...</van-loading>
            </div>
            <div v-else-if="storeList.length === 0" class="empty-container">
              <van-empty description="该学校暂无可用门店" />
            </div>
            <div v-else class="store-list">
              <div 
                v-for="store in storeList" 
                :key="store.id"
                :class="['store-item', { 'active': selectedStoreId === store.id }]"
                @click="selectStore(store)"
              >
                <div class="store-info">
                  <div class="store-name">{{ store.addressName }}</div>
                  <div class="store-address">{{ store.detailAddress || '暂无地址信息' }}</div>
                  <div class="store-hours" v-if="store.businessHours">
                    <van-icon name="clock-o" size="12" />
                    <span>{{ store.businessHours }}</span>
                  </div>
                </div>
                <div class="store-check">
                  <van-icon v-if="selectedStoreId === store.id" name="success" color="#07c160" />
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <van-button 
            type="default" 
            size="large" 
            @click="handleClose"
            class="cancel-btn"
          >
            取消
          </van-button>
          <van-button 
            type="primary" 
            size="large" 
            @click="handleConfirm"
            :disabled="!selectedStoreId"
            :loading="confirming"
            class="confirm-btn"
          >
            确认选择
          </van-button>
        </div>
      </div>
    </div>

    <!-- 消息提示 -->
    <div v-if="message" :class="['message-toast', messageType]">
      {{ message }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { showToast } from 'vant'
import { StoreApi } from '@/api/modules/store'

// Props
interface Props {
  modelValue: boolean
}

const props = defineProps<Props>()

// Emits
interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'confirm', data: { schoolId: number, storeId: number, schoolName: string, storeName: string }): void
}

const emit = defineEmits<Emits>()

// 响应式数据
const schoolList = ref<any[]>([])
const storeList = ref<any[]>([])
const selectedSchoolId = ref<number | null>(null)
const selectedStoreId = ref<number | null>(null)
const loadingStores = ref(false)
const confirming = ref(false)
const message = ref('')
const messageType = ref<'success' | 'error'>('success')

// 方法
const handleClose = () => {
  console.log('🏪 门店选择弹窗关闭')
  emit('update:modelValue', false)
  resetSelection()
}

const resetSelection = () => {
  selectedSchoolId.value = null
  selectedStoreId.value = null
  storeList.value = []
}

const selectSchool = async (school: any) => {
  console.log('🏫 选择学校:', school.schoolName)
  selectedSchoolId.value = school.id
  selectedStoreId.value = null
  
  // 加载该学校的门店列表
  await loadStoresBySchool(school.id)
}

const selectStore = (store: any) => {
  console.log('🏪 选择门店:', store.addressName)
  selectedStoreId.value = store.id
}

const loadSchoolList = async () => {
  try {
    console.log('📡 加载学校列表...')
    const response = await StoreApi.getSchoolList()
    if (response.code === 200) {
      schoolList.value = response.data || []
      console.log('✅ 学校列表加载成功:', schoolList.value.length, '个学校')
    } else {
      throw new Error(response.message || '获取学校列表失败')
    }
  } catch (error: any) {
    console.error('❌ 加载学校列表失败:', error)
    showMessage('加载学校列表失败', 'error')
  }
}

const loadStoresBySchool = async (schoolId: number) => {
  try {
    loadingStores.value = true
    console.log('📡 加载门店列表，学校ID:', schoolId)
    
    const response = await StoreApi.getStoresBySchool(schoolId)
    if (response.code === 200) {
      storeList.value = response.data || []
      console.log('✅ 门店列表加载成功:', storeList.value.length, '个门店')
    } else {
      throw new Error(response.message || '获取门店列表失败')
    }
  } catch (error: any) {
    console.error('❌ 加载门店列表失败:', error)
    showMessage('加载门店列表失败', 'error')
    storeList.value = []
  } finally {
    loadingStores.value = false
  }
}

const handleConfirm = async () => {
  if (!selectedSchoolId.value || !selectedStoreId.value) {
    showMessage('请选择学校和门店', 'error')
    return
  }

  try {
    confirming.value = true
    
    const selectedSchool = schoolList.value.find(s => s.id === selectedSchoolId.value)
    const selectedStore = storeList.value.find(s => s.id === selectedStoreId.value)
    
    if (!selectedSchool || !selectedStore) {
      throw new Error('选择的学校或门店信息不完整')
    }

    console.log('✅ 确认选择门店:', {
      schoolId: selectedSchoolId.value,
      storeId: selectedStoreId.value,
      schoolName: selectedSchool.schoolName,
      storeName: selectedStore.addressName
    })

    // 触发确认事件
    emit('confirm', {
      schoolId: selectedSchoolId.value,
      storeId: selectedStoreId.value,
      schoolName: selectedSchool.schoolName,
      storeName: selectedStore.addressName
    })

    showMessage('门店选择成功', 'success')
    
    // 延迟关闭弹窗
    setTimeout(() => {
      handleClose()
    }, 500)
    
  } catch (error: any) {
    console.error('❌ 确认门店选择失败:', error)
    showMessage(error.message || '门店选择失败', 'error')
  } finally {
    confirming.value = false
  }
}

const showMessage = (text: string, type: 'success' | 'error' = 'success') => {
  message.value = text
  messageType.value = type
  setTimeout(() => {
    message.value = ''
  }, 3000)
}

// 监听弹窗显示状态
watch(() => props.modelValue, (newVal) => {
  if (newVal) {
    console.log('🏪 门店选择弹窗打开')
    loadSchoolList()
  }
})

// 组件挂载时加载数据
onMounted(() => {
  if (props.modelValue) {
    loadSchoolList()
  }
})
</script>

<style scoped>
/* 弹窗遮罩层 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.modal-overlay.active {
  opacity: 1;
}

/* 弹窗内容 */
.modal-content {
  background: #ffffff;
  border-radius: 24px;
  width: 90%;
  max-width: 800px;
  max-height: 85vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  transform: scale(0.9) translateY(20px);
  transition: all 0.3s ease;
  overflow: hidden;
}

.modal-content.active {
  transform: scale(1) translateY(0);
}

/* 弹窗头部 */
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32px;
  border-bottom: 1px solid #ebedf0;
  background: #f7f8fa;
}

.modal-title {
  font-size: 64px;
  font-weight: 600;
  color: #323233;
  margin: 0;
}

.close-btn {
  width: 80px;
  height: 80px;
  border: none;
  background: #f8f9fa;
  border-radius: 50%;
  font-size: 48px;
  color: #646566;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.close-btn:hover {
  background: #e8e9ea;
  transform: scale(1.1);
}

/* 弹窗主体 */
.modal-body {
  flex: 1;
  padding: 32px;
  overflow-y: auto;
}

.section-title {
  font-size: 48px;
  font-weight: 600;
  color: #323233;
  margin: 0 0 24px 0;
  padding-bottom: 16px;
  border-bottom: 2px solid #1989fa;
}

/* 学校列表 */
.school-section {
  margin-bottom: 40px;
}

.school-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.school-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px;
  border: 2px solid #ebedf0;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #ffffff;
}

.school-item:hover {
  border-color: #1989fa;
  box-shadow: 0 4px 12px rgba(25, 137, 250, 0.1);
}

.school-item.active {
  border-color: #07c160;
  background: #f0f9ff;
}

.school-info {
  flex: 1;
}

.school-name {
  font-size: 40px;
  font-weight: 600;
  color: #323233;
  margin-bottom: 8px;
}

.school-address {
  font-size: 32px;
  color: #646566;
}

/* 门店列表 */
.store-section {
  margin-top: 40px;
}

.loading-container,
.empty-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 60px 0;
}

.store-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 16px;
}

.store-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px;
  border: 2px solid #ebedf0;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #ffffff;
}

.store-item:hover {
  border-color: #1989fa;
  box-shadow: 0 4px 12px rgba(25, 137, 250, 0.1);
}

.store-item.active {
  border-color: #07c160;
  background: #f0f9ff;
}

.store-info {
  flex: 1;
}

.store-name {
  font-size: 40px;
  font-weight: 600;
  color: #323233;
  margin-bottom: 8px;
}

.store-address {
  font-size: 32px;
  color: #646566;
  margin-bottom: 8px;
}

.store-hours {
  margin-top: 8px;
  font-size: 28px;
  color: #969799;
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 弹窗底部 */
.modal-footer {
  display: flex;
  gap: 16px;
  padding: 32px;
  border-top: 1px solid #ebedf0;
  background: #f7f8fa;
}

.cancel-btn,
.confirm-btn {
  flex: 1;
  height: 120px;
  font-size: 48px;
  font-weight: 600;
  border-radius: 16px;
}

.cancel-btn {
  background: #f8f9fa;
  border: 2px solid #ebedf0;
  color: #646566;
}

.confirm-btn {
  background: #1989fa;
  border: none;
  color: #ffffff;
}

.confirm-btn:disabled {
  background: #c8c9cc;
  color: #ffffff;
}

/* 消息提示 */
.message-toast {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  padding: 24px 32px;
  border-radius: 16px;
  font-size: 40px;
  font-weight: 600;
  z-index: 10000;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.message-toast.success {
  background: #07c160;
  color: #ffffff;
}

.message-toast.error {
  background: #ee0a24;
  color: #ffffff;
}
</style>
