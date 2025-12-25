<template>
  <div class="home-page">
    <!-- 顶部标题区域 -->
    <div class="welcome-header">
      <img
        src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/mall-selfcheck/assets/images/login/welcome_text.png"
        class="header-left-img"
        alt="欢迎文本"
      />
      <img
        src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/mall-selfcheck/assets/images/login/guanghengzou.png"
        class="header-right-img"
        alt="广横走标志"
      />
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 标题区域 -->
      <div class="title-section">
        <div class="scan-prompt">把商品条码对准下方扫描口</div>
        <h1 class="main-title">开始自助结账</h1>
      </div>

      <!-- 操作按钮区域 -->
      <div class="button-section">
        <button
          class="checkout-button member-button"
          @click="startMemberCheckout"
        >
          <div class="button-content">
            <img
              src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/mall-selfcheck/assets/images/login/member.png"
              class="button-icon"
              alt="会员图标"
            />
            <span class="button-text">会员结算</span>
          </div>
        </button>

        <button
          class="checkout-button guest-button"
          @click="startGuestCheckout"
        >
          <div class="button-content">
            <img
              src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/mall-selfcheck/assets/images/login/guest.png"
              class="button-icon"
              alt="非会员图标"
            />
            <span class="button-text">非会员结算</span>
          </div>
        </button>
      </div>
      <!-- 注册提示 -->
      <div class="register-prompt">
        还没有会员？<router-link to="/register" class="register-link">去注册</router-link>
      </div>

      <!-- 时间显示 -->
      <div class="time-display">
        <div class="current-time">{{ currentTime }}</div>
        <div class="current-date">{{ currentDate }}</div>
      </div>
    </div>

    <!-- 会员登录弹窗组件 -->
    <MemberLoginModal
      v-model="showMemberLogin"
      @login-success="handleLoginSuccess"
    />

    <!-- 门店选择弹窗 - 首次安装时显示 -->
    <StoreSelectionModal
      v-model="showStoreSelection"
      @confirm="handleStoreSelection"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMemberStore } from '@/store/modules/member'
import MemberLoginModal from '@/components/business/MemberLoginModal/index.vue'
import StoreSelectionModal from '@/components/business/StoreSelectionModal/index.vue'
import StoreManager from '@/utils/storeManager'

const router = useRouter()
const memberStore = useMemberStore()

// 响应式数据
const currentTime = ref('')
const currentDate = ref('')
const showMemberLogin = ref(false)
const showStoreSelection = ref(false)
let timeTimer: NodeJS.Timeout | null = null

// 使用StoreManager管理门店信息

// 更新时间
const updateTime = () => {
  const now = new Date()
  const hours = String(now.getHours()).padStart(2, '0')
  const minutes = String(now.getMinutes()).padStart(2, '0')
  const seconds = String(now.getSeconds()).padStart(2, '0')

  currentTime.value = `${hours}:${minutes}:${seconds}`

  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const weekDay = ['日', '一', '二', '三', '四', '五', '六'][now.getDay()]

  currentDate.value = `${year}年${month}月${day}日 星期${weekDay}`
}

// 获取已选择的门店信息
const getSelectedStore = () => {
  return StoreManager.getStoreInfo()
}

// 开始会员结算
const startMemberCheckout = () => {
  const selectedStore = getSelectedStore()
  if (!selectedStore) {
    console.warn('⚠️ 未选择门店，显示门店选择弹窗')
    showStoreSelection.value = true
    return
  }

  console.log('🏪 使用门店信息进行会员登录:', selectedStore)
  showMemberLogin.value = true
}

// 开始游客结算
const startGuestCheckout = async () => {
  const selectedStore = getSelectedStore()
  if (!selectedStore) {
    console.warn('⚠️ 未选择门店，显示门店选择弹窗')
    showStoreSelection.value = true
    return
  }

  console.log('🏪 使用门店信息进行游客登录:', selectedStore)
  console.log('🏪 门店参数验证:', {
    storeId: selectedStore.storeId,
    schoolId: selectedStore.schoolId,
    storeName: selectedStore.storeName,
    schoolName: selectedStore.schoolName
  })

  try {
    // 使用门店信息进行游客登录
    await memberStore.guestLoginWithStore(selectedStore.storeId, selectedStore.schoolId)
    console.log('✅ 游客登录成功，跳转到扫码页面')
    router.push('/scan')
  } catch (error: any) {
    console.error('❌ 游客登录失败:', error)
    // 如果游客登录失败，仍然跳转到扫码页面，让扫码页面处理登录逻辑
    router.push('/scan')
  }
}

// 处理登录成功
const handleLoginSuccess = (memberInfo: any) => {
  console.log('会员登录成功:', memberInfo)

  // 跳转到扫码页面
  setTimeout(() => {
    router.push('/scan')
  }, 1500)
}

// 检查是否首次启动（增强APK支持）
const checkFirstLaunch = () => {
  const isFirstLaunch = StoreManager.isFirstLaunch()
  const hasSelectedStore = StoreManager.hasStoreInfo()

  // APK环境下的门店信息有效性检查
  const storeInfoValid = hasSelectedStore ? StoreManager.isStoreInfoValid() : false

  console.log('🏠 首页启动检查:', {
    isFirstLaunch,
    hasSelectedStore,
    storeInfoValid,
    storeInfo: StoreManager.getStoreDisplayText(),
    storeStatus: StoreManager.getStoreInfoStatus()
  })

  // 需要显示门店选择弹窗的情况：
  // 1. 首次启动
  // 2. 没有选择过门店
  // 3. 门店信息无效或已过期
  const needStoreSelection = isFirstLaunch || !hasSelectedStore || !storeInfoValid

  if (needStoreSelection) {
    if (isFirstLaunch) {
      console.log('🎉 首次启动，显示门店选择弹窗')
    } else if (!hasSelectedStore) {
      console.log('📍 未找到门店信息，显示门店选择弹窗')
    } else if (!storeInfoValid) {
      console.log('⚠️ 门店信息无效或已过期，重新显示门店选择弹窗')
      // 清除无效的门店信息
      StoreManager.clearStoreInfo()
    }

    showStoreSelection.value = true
  } else {
    console.log('✅ 门店信息有效，直接进入应用')
  }

  // 标记已经启动过
  if (isFirstLaunch) {
    StoreManager.markAsLaunched()
  }
}

// 处理门店选择确认
const handleStoreSelection = (storeData: {
  schoolId: number
  storeId: number
  schoolName: string
  storeName: string
}) => {
  console.log('✅ 门店选择完成:', storeData)

  try {
    // 使用StoreManager保存门店信息（带备份和验证）
    StoreManager.saveStoreInfo(storeData)

    console.log('💾 门店信息已安全保存（含备份）')
    console.log('🏪 当前门店:', StoreManager.getStoreDisplayText())
  } catch (error) {
    console.error('❌ 保存门店信息失败:', error)
    // 可以显示错误提示给用户
  }
}

// 生命周期
onMounted(() => {
  // 检查并清理之前的登录状态
  if (memberStore.isLoggedIn) {
    console.log('检测到用户已登录，自动退出登录状态')
    memberStore.performLogout()
  }

  // 检查首次启动
  checkFirstLaunch()

  updateTime()
  timeTimer = setInterval(updateTime, 1000)
})

onUnmounted(() => {
  if (timeTimer) {
    clearInterval(timeTimer)
  }
})
</script>

<style scoped lang="scss">
.home-page {
  width: 100%;
  height: 100%;
  min-width: unset;
  min-height: unset;
  max-width: 100vw;
  max-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  background: #f5f5f5;
  position: relative;
}

.welcome-header {
  width: 100%;
  /* 使用视口高度的百分比而非固定像素 */
  height: min(358px, 25vh);
  min-height: 200px;
  background-color: #a9ff00;
  padding: 10px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: min(40px, 3vh);
  flex-shrink: 0; /* 防止被压缩 */
}
.header-left-img,
.header-right-img {
  height: 191px;
  width: auto;
  object-fit: contain;
}
.main-title {
  font-size: 120px;
  font-weight: 600;
  margin: 30px 0;
  white-space: nowrap;
}
.scan-prompt {
  font-size: 40px;
  font-weight: 400;
  margin: 20px 0;
  white-space: nowrap;
}

.welcome-title {
  font-family: 'PingFang SC', sans-serif;
  font-size: 56px;
  color: #000000;
  margin-bottom: 12px;
}

.system-title {
  font-family: 'HelloFont WenYiHei', sans-serif;
  font-size: 80px;
  color: #000000;
}

.main-content {
  width: 100%;
  max-width: none;
  flex: 1; /* 使用flex布局自动填充剩余空间 */
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: min(32px, 2vh) 0 0 0;
  min-width: unset; /* 移除固定最小宽度 */
  overflow: hidden; /* 防止内容溢出 */
}
.checkout-buttons {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-top: 60px;
}

.title-section {
  text-align: center;
  flex-shrink: 0; /* 防止被压缩 */

  .scan-prompt {
    font-size: clamp(24px, 4vw, 40px); /* 响应式字体大小 */
    color: #0a0d05;
    letter-spacing: 0.3em;
    font-family: 'PingFang SC', sans-serif;
    font-weight: 400;
    line-height: 1.4em;
    margin-bottom: min(20px, 2vh);
  }

  .main-title {
    font-size: clamp(60px, 10vw, 120px); /* 响应式字体大小 */
    font-weight: 600;
    color: #0a0d05;
    margin: min(20px, 2vh) 0;
    line-height: 1.2em;
    text-align: center;
  }
}

.button-section {
  display: flex;
  flex-direction: row;
  gap: min(40px, 3vw);
  width: 100%;
  max-width: none;
  justify-content: center;
  flex-wrap: nowrap;
  margin-top: min(60px, 4vh);
  flex: 1; /* 允许按钮区域扩展 */
  align-items: center; /* 垂直居中按钮 */
}

.checkout-button {
  border: none;
  border-radius: 16px;
  padding: min(40px, 3vh) 0;
  transition: all 0.3s ease;
  cursor: pointer;
  /* 响应式尺寸 */
  width: min(454px, 40vw);
  height: min(660px, 45vh);
  max-height: 60vh; /* 防止按钮过高 */
  min-height: 300px; /* 确保最小可用高度 */

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    border-color: #999;
  }

  &:active {
    transform: translateY(0);
  }

  .button-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 30px;

    .button-icon {
      width: min(200px, 15vw);
      height: min(200px, 15vw);
      margin-bottom: min(30px, 2vh);
      object-fit: contain; /* 保持图标比例 */
    }

    .button-text {
      font-size: clamp(32px, 5vw, 56px); /* 响应式字体 */
      font-weight: 600;
      color: #333;
    }
  }

  &.member-button {
    background: #000;

    .button-text {
      color: white;
    }

    &:hover {
      background: #555;
      border-color: #555;
    }
  }

  &.guest-button {
    background: #A9FF00;

    .button-text {
      color: black;
    }

    &:hover {
      background: #777;
      border-color: #777;
    }
  }
}

.time-display {
  position: absolute;
  bottom: 32px;
  right: 32px;
  text-align: right;
  color: #666;

  .current-time {
    font-size: 24px;
    font-weight: 600;
    margin-bottom: 4px;
  }

  .current-date {
    font-size: 14px;
  }
}

.register-prompt {
  margin-top: 50px;
  font-size: 40px;
  color: #999999;
  font-family: 'PingFang SC', sans-serif;
  text-align: center;
}

.register-link {
  color: #999999;
  text-decoration: underline;
}

/* 移动端响应式设计 */
@media (max-width: 768px), (max-height: 960px) {
  .home-page {
    /* 确保移动端全屏显示 */
    height: 100vh;
  }

  .welcome-header {
    height: min(250px, 20vh);
    min-height: 150px;
    padding: 8px 16px;
    margin-bottom: min(20px, 2vh);
  }

  .header-left-img,
  .header-right-img {
    height: min(150px, 12vh);
  }

  .main-content {
    padding: min(16px, 2vh) 16px;
    justify-content: space-evenly; /* 均匀分布内容 */
  }

  .title-section {
    margin-bottom: min(20px, 2vh);

    .scan-prompt {
      font-size: clamp(18px, 3vw, 28px);
      letter-spacing: 0.2em;
    }

    .main-title {
      font-size: clamp(40px, 8vw, 80px);
      margin: min(15px, 1.5vh) 0;
    }
  }

  .button-section {
    gap: min(20px, 2vw);
    margin-top: min(30px, 3vh);
    flex-direction: row; /* 保持横向布局 */
  }

  .checkout-button {
    width: min(350px, 42vw);
    height: min(500px, 40vh);
    min-height: 250px;
    max-height: 45vh;
    padding: min(20px, 2vh) 0;

    .button-content {
      gap: min(20px, 2vh);

      .button-icon {
        width: min(120px, 12vw);
        height: min(120px, 12vw);
        margin-bottom: min(15px, 1.5vh);
      }

      .button-text {
        font-size: clamp(24px, 4vw, 40px);
      }
    }
  }

  .register-prompt {
    margin-top: min(30px, 3vh);
    font-size: clamp(24px, 3vw, 32px);
  }

  .time-display {
    position: absolute;
    bottom: min(16px, 2vh);
    right: min(16px, 2vw);

    .current-time {
      font-size: clamp(16px, 2vw, 20px);
    }

    .current-date {
      font-size: clamp(12px, 1.5vw, 14px);
    }
  }
}

/* 超小屏幕优化 */
@media (max-width: 480px) or (max-height: 800px) {
  .button-section {
    flex-direction: column; /* 小屏幕垂直排列按钮 */
    gap: min(15px, 2vh);
  }

  .checkout-button {
    width: min(300px, 80vw);
    height: min(200px, 25vh);
    min-height: 180px;

    .button-content {
      flex-direction: row; /* 小按钮时图标和文字横向排列 */
      gap: min(15px, 2vw);

      .button-icon {
        width: min(80px, 15vw);
        height: min(80px, 15vw);
        margin-bottom: 0;
      }

      .button-text {
        font-size: clamp(20px, 4vw, 28px);
      }
    }
  }
}

// 弹窗样式
:deep(.member-login-modal__wrapper) {
  width: 80vw !important;
  max-width: 480px !important;
  left: 50% !important;
  transform: translateX(-50%) !important;
}
</style>
