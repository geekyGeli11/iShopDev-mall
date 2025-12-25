<template>
  <div class="member-page">
    <!-- 顶部导航 -->
    <van-nav-bar 
      title="会员中心" 
      left-arrow 
      @click-left="goBack"
      class="member-nav"
    >
      <template #right>
        <van-icon name="setting-o" @click="goToSettings" />
      </template>
    </van-nav-bar>

    <!-- 页面内容 -->
    <div class="member-content">
      <!-- 会员信息卡片 -->
      <div class="member-info-card">
        <div class="member-header">
          <div class="avatar-section">
            <van-image
              :src="memberInfo?.icon || defaultAvatar"
              round
              width="60"
              height="60"
              fit="cover"
              class="member-avatar"
            />
            <div class="vip-badge" v-if="memberInfo && memberInfo.memberLevel > 0">
              <van-tag type="warning" size="medium">VIP{{ memberInfo.memberLevel }}</van-tag>
            </div>
          </div>
          
          <div class="member-details">
            <h3 class="member-name">{{ memberInfo?.nickname || memberInfo?.username || '会员用户' }}</h3>
            <p class="member-phone">{{ maskedPhone }}</p>
            <div class="member-level">
              <van-tag 
                :type="levelTagType as any" 
                size="medium"
                round
              >
                {{ levelText }}
              </van-tag>
            </div>
          </div>
          
          <div class="member-actions">
            <van-button size="mini" type="primary" @click="editProfile">
              编辑
            </van-button>
          </div>
        </div>

        <!-- 会员权益进度 -->
        <div class="member-progress" v-if="nextLevelInfo">
          <div class="progress-info">
            <span class="progress-text">距离{{ nextLevelInfo.name }}还需</span>
            <span class="progress-value">{{ nextLevelInfo.needed }}积分</span>
          </div>
          <van-progress 
            :percentage="levelProgress" 
            stroke-width="6"
            color="linear-gradient(to right, #ff6b6b, #ffa726)"
            track-color="#f5f5f5"
          />
        </div>
      </div>

      <!-- 积分余额卡片 -->
      <div class="balance-section">
        <div class="balance-grid">
          <div class="balance-item" @click="goToPoints">
            <div class="balance-icon">
              <van-icon name="gold-coin-o" size="24" color="#ffb700" />
            </div>
            <div class="balance-content">
              <p class="balance-value">{{ memberInfo?.integration || 0 }}</p>
              <p class="balance-label">积分余额</p>
            </div>
            <van-icon name="arrow" class="balance-arrow" />
          </div>
          
          <div class="balance-item" @click="goToCoupons">
            <div class="balance-icon">
              <van-icon name="coupon-o" size="24" color="#ff6b6b" />
            </div>
            <div class="balance-content">
              <p class="balance-value">{{ availableCoupons }}</p>
              <p class="balance-label">可用优惠券</p>
            </div>
            <van-icon name="arrow" class="balance-arrow" />
          </div>
        </div>
      </div>

      <!-- 快捷功能 -->
      <div class="quick-actions">
        <h4 class="section-title">快捷功能</h4>
        <div class="action-grid">
          <div class="action-item" @click="goToOrders">
            <div class="action-icon">
              <van-icon name="orders-o" size="20" color="#1989fa" />
            </div>
            <span class="action-label">我的订单</span>
          </div>
          
          <div class="action-item" @click="goToFavorites">
            <div class="action-icon">
              <van-icon name="heart-o" size="20" color="#ff6b6b" />
            </div>
            <span class="action-label">我的收藏</span>
          </div>
          
          <div class="action-item" @click="goToAddresses">
            <div class="action-icon">
              <van-icon name="location-o" size="20" color="#4ecdc4" />
            </div>
            <span class="action-label">收货地址</span>
          </div>
          
          <div class="action-item" @click="goToFeedback">
            <div class="action-icon">
              <van-icon name="chat-o" size="20" color="#ffa726" />
            </div>
            <span class="action-label">意见反馈</span>
          </div>
        </div>
      </div>

      <!-- 最近订单 -->
      <div class="recent-orders" v-if="recentOrders.length > 0">
        <div class="section-header">
          <h4 class="section-title">最近订单</h4>
          <van-button size="mini" type="primary" plain @click="goToOrders">
            查看全部
          </van-button>
        </div>
        
        <div class="order-list">
          <div 
            v-for="order in recentOrders" 
            :key="order.orderId"
            class="order-item"
            @click="goToOrderDetail(order.orderId)"
          >
            <div class="order-header">
              <span class="order-number">订单号: {{ order.orderSn }}</span>
              <van-tag 
                :type="getOrderStatusType(order.status) as any" 
                size="medium"
              >
                {{ getOrderStatusText(order.status) }}
              </van-tag>
            </div>
            
            <div class="order-content">
              <div class="order-products">
                <van-image
                  v-for="(product, index) in order.orderItems.slice(0, 3)"
                  :key="index"
                  :src="product.productPic"
                  width="40"
                  height="40"
                  fit="cover"
                  round
                  class="product-thumb"
                />
                <span v-if="order.orderItems.length > 3" class="more-products">
                  +{{ order.orderItems.length - 3 }}
                </span>
              </div>
              
              <div class="order-info">
                <p class="order-total">共{{ order.orderItems.reduce((sum, item) => sum + item.productQuantity, 0) }}件商品</p>
                <p class="order-amount">¥{{ order.payAmount.toFixed(2) }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 会员等级说明 -->
      <div class="level-info">
        <h4 class="section-title">会员等级说明</h4>
        <div class="level-list">
          <div 
            v-for="level in levelDescriptions" 
            :key="level.id"
            class="level-item"
            :class="{ active: level.id === (memberInfo?.memberLevel || 0) }"
          >
            <div class="level-badge">
              <van-tag 
                :type="level.tagType as any" 
                size="medium"
                round
              >
                {{ level.name }}
              </van-tag>
            </div>
            <div class="level-content">
              <p class="level-requirement">{{ level.requirement }}</p>
              <div class="level-benefits">
                <span v-for="benefit in level.benefits" :key="benefit" class="benefit-tag">
                  {{ benefit }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑资料弹窗 -->
    <van-popup v-model:show="showEditProfile" position="bottom" :style="{ height: '60%' }">
      <div class="edit-profile-popup">
        <div class="popup-header">
          <van-button size="mini" @click="showEditProfile = false">取消</van-button>
          <h3>编辑资料</h3>
          <van-button size="mini" type="primary" @click="saveProfile">保存</van-button>
        </div>
        
        <div class="popup-content">
          <van-form>
            <van-field
              v-model="editForm.name"
              label="昵称"
              placeholder="请输入昵称"
              maxlength="20"
            />
            
            <van-field
              v-model="editForm.phone"
              label="手机号"
              type="tel"
              readonly
            />
            
            <van-field
              v-model="editForm.email"
              label="邮箱"
              type="email"
              placeholder="请输入邮箱"
            />
          </van-form>
          
          <div class="avatar-upload">
            <h4>头像</h4>
            <van-uploader 
              v-model="editForm.avatar"
              :max-count="1"
              :preview-size="80"
              upload-text="上传头像"
            />
          </div>
        </div>
      </div>
    </van-popup>

    <!-- 底部退出按钮 -->
    <div class="member-footer">
      <van-button 
        type="danger" 
        size="large" 
        block 
        round
        @click="logout"
        class="logout-btn"
      >
        退出登录
      </van-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showSuccessToast, showConfirmDialog } from 'vant'
import { useMemberStore } from '@/store/modules/member'
import type { MemberLoginResult, OrderVO, OrderStatus } from '@shared/types'

const router = useRouter()
const memberStore = useMemberStore()

// 响应式数据
const showEditProfile = ref(false)
const recentOrders = ref<OrderVO[]>([])
const availableCoupons = ref(0)

// 默认头像
const defaultAvatar = 'https://img.yzcdn.cn/vant/cat.jpeg'

// 编辑表单类型
interface EditForm {
  name: string
  phone: string
  email: string
  avatar: any[]
}

// 编辑表单
const editForm = ref<EditForm>({
  name: '',
  phone: '',
  email: '',
  avatar: []
})

// 会员信息
const memberInfo = computed((): MemberLoginResult | null => {
  return memberStore.memberInfo
})

// 脱敏手机号
const maskedPhone = computed(() => {
  return memberStore.maskedPhone || '未绑定'
})

// 等级相关
const levelDescriptions = [
  {
    id: 0,
    name: '普通会员',
    tagType: 'default',
    requirement: '注册即可获得',
    benefits: ['基础积分', '生日优惠']
  },
  {
    id: 1,
    name: 'VIP1',
    tagType: 'primary',
    requirement: '累计消费满500元',
    benefits: ['1.2倍积分', '专属优惠券', '优先客服']
  },
  {
    id: 2,
    name: 'VIP2',
    tagType: 'success',
    requirement: '累计消费满2000元',
    benefits: ['1.5倍积分', '包邮特权', '专属客服']
  },
  {
    id: 3,
    name: 'VIP3',
    tagType: 'warning',
    requirement: '累计消费满5000元',
    benefits: ['2倍积分', '专属折扣', '尊享客服', '生日礼品']
  }
]

const levelText = computed(() => {
  return memberStore.memberLevelName || '普通会员'
})

const levelTagType = computed(() => {
  const level = levelDescriptions.find(l => l.id === (memberInfo.value?.memberLevel || 0))
  return level?.tagType || 'default'
})

const nextLevelInfo = computed(() => {
  const currentLevel = memberInfo.value?.memberLevel || 0
  const nextLevel = levelDescriptions.find(l => l.id === currentLevel + 1)
  if (!nextLevel) return null
  
  const currentPoints = memberInfo.value?.integration || 0
  const requiredPoints = (currentLevel + 1) * 500 // 假设每级需要500积分
  const needed = Math.max(0, requiredPoints - currentPoints)
  
  return {
    name: nextLevel.name,
    needed
  }
})

const levelProgress = computed(() => {
  if (!nextLevelInfo.value) return 100
  
  const currentPoints = memberInfo.value?.integration || 0
  const currentLevel = memberInfo.value?.memberLevel || 0
  const levelPoints = currentLevel * 500
  const nextLevelPoints = (currentLevel + 1) * 500
  
  const progress = ((currentPoints - levelPoints) / (nextLevelPoints - levelPoints)) * 100
  return Math.min(100, Math.max(0, progress))
})

// 方法
const goBack = () => {
  router.back()
}

const goToSettings = () => {
  router.push('/settings')
}

const editProfile = () => {
  editForm.value = {
    name: memberInfo.value?.nickname || memberInfo.value?.username || '',
    phone: memberInfo.value?.telephone || '',
    email: '', // 暂时没有email字段
    avatar: memberInfo.value?.icon ? [{ url: memberInfo.value.icon }] : []
  }
  showEditProfile.value = true
}

const saveProfile = async () => {
  try {
    // 这里应该调用API更新会员信息
    // 暂时只显示成功消息
    showSuccessToast('资料更新成功')
    showEditProfile.value = false
    
  } catch (error) {
    console.error('更新资料失败:', error)
    showToast('更新失败，请重试')
  }
}

const goToPoints = () => {
  router.push('/member/points')
}

const goToCoupons = () => {
  router.push('/member/coupons')
}

const goToOrders = () => {
  router.push('/member/orders')
}

const goToFavorites = () => {
  router.push('/member/favorites')
}

const goToAddresses = () => {
  router.push('/member/addresses')
}

const goToFeedback = () => {
  router.push('/member/feedback')
}

const goToOrderDetail = (orderId: number) => {
  router.push(`/member/orders/${orderId}`)
}

const getOrderStatusType = (status: number): string => {
  // 根据OrderStatus枚举映射
  const statusMap: { [key: number]: string } = {
    [0]: 'warning',  // PENDING_PAYMENT
    [1]: 'primary',  // PENDING_DELIVERY
    [2]: 'primary',  // DELIVERED
    [3]: 'success',  // COMPLETED
    [4]: 'danger',   // CLOSED
    [5]: 'danger'    // INVALID
  }
  return statusMap[status] || 'default'
}

const getOrderStatusText = (status: number): string => {
  const statusMap: { [key: number]: string } = {
    [0]: '待支付',  // PENDING_PAYMENT
    [1]: '待发货',  // PENDING_DELIVERY
    [2]: '已发货',  // DELIVERED
    [3]: '已完成',  // COMPLETED
    [4]: '已关闭',  // CLOSED
    [5]: '无效订单' // INVALID
  }
  return statusMap[status] || '未知状态'
}

const logout = async () => {
  try {
    await showConfirmDialog({
      title: '退出登录',
      message: '确定要退出当前账户吗？'
    })
    
    await memberStore.logout()
    showSuccessToast('已退出登录')
    router.replace('/login')
    
  } catch (error) {
    // 用户取消退出
  }
}

// 初始化数据
const loadMemberData = async () => {
  try {
    // 模拟加载最近订单数据
    // 实际应该调用API获取数据
    recentOrders.value = []
    
    // 模拟加载可用优惠券数量
    availableCoupons.value = 0
    
  } catch (error) {
    console.error('加载会员数据失败:', error)
  }
}

// 生命周期
onMounted(() => {
  // 检查登录状态
  if (!memberStore.isLoggedIn) {
    router.replace('/login')
    return
  }
  
  loadMemberData()
})
</script>

<style scoped>
.member-page {
  min-height: 100vh;
  background: #f8f9fa;
}

.member-nav {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

:deep(.van-nav-bar__title) {
  color: #fff;
}

:deep(.van-nav-bar .van-icon) {
  color: #fff;
}

.member-content {
  padding: 16px;
  padding-bottom: 80px;
}

/* 会员信息卡片 */
.member-info-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 16px;
  color: #fff;
}

.member-header {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 20px;
}

.avatar-section {
  position: relative;
}

.member-avatar {
  border: 3px solid rgba(255, 255, 255, 0.3);
}

.vip-badge {
  position: absolute;
  top: -5px;
  right: -5px;
}

.member-details {
  flex: 1;
}

.member-name {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 4px 0;
}

.member-phone {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  margin: 0 0 8px 0;
}

.member-level {
  margin-top: 8px;
}

.member-actions {
  align-self: flex-start;
}

.member-progress {
  margin-top: 16px;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.progress-text {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
}

.progress-value {
  font-size: 14px;
  font-weight: 600;
}

/* 积分余额 */
.balance-section {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
}

.balance-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1px;
  background: #f5f5f5;
  border-radius: 8px;
  overflow: hidden;
}

.balance-item {
  background: #fff;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.balance-item:active {
  background: #f8f9fa;
}

.balance-content {
  flex: 1;
}

.balance-value {
  font-size: 18px;
  font-weight: 600;
  color: #323233;
  margin: 0 0 4px 0;
}

.balance-label {
  font-size: 12px;
  color: #969799;
  margin: 0;
}

.balance-arrow {
  color: #c8c9cc;
}

/* 快捷功能 */
.quick-actions {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #323233;
  margin: 0 0 16px 0;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.action-item:active {
  background: #f8f9fa;
}

.action-icon {
  width: 40px;
  height: 40px;
  background: #f8f9fa;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-label {
  font-size: 12px;
  color: #646566;
  text-align: center;
}

/* 最近订单 */
.recent-orders {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-item {
  border: 1px solid #ebedf0;
  border-radius: 8px;
  padding: 12px;
  cursor: pointer;
  transition: border-color 0.2s;
}

.order-item:active {
  border-color: #1989fa;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.order-number {
  font-size: 12px;
  color: #969799;
}

.order-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-products {
  display: flex;
  align-items: center;
  gap: 4px;
}

.product-thumb {
  border: 1px solid #ebedf0;
}

.more-products {
  font-size: 12px;
  color: #969799;
  margin-left: 4px;
}

.order-info {
  text-align: right;
}

.order-total {
  font-size: 12px;
  color: #969799;
  margin: 0 0 4px 0;
}

.order-amount {
  font-size: 14px;
  font-weight: 600;
  color: #323233;
  margin: 0;
}

/* 会员等级说明 */
.level-info {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
}

.level-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.level-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #ebedf0;
  transition: all 0.2s;
}

.level-item.active {
  border-color: #1989fa;
  background: #f0f8ff;
}

.level-content {
  flex: 1;
}

.level-requirement {
  font-size: 14px;
  color: #323233;
  margin: 0 0 8px 0;
}

.level-benefits {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.benefit-tag {
  font-size: 11px;
  color: #646566;
  background: #f8f9fa;
  padding: 2px 6px;
  border-radius: 4px;
}

/* 编辑资料弹窗 */
.edit-profile-popup {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #ebedf0;
}

.popup-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.popup-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.avatar-upload {
  margin-top: 20px;
}

.avatar-upload h4 {
  font-size: 14px;
  color: #323233;
  margin: 0 0 12px 0;
}

/* 底部退出按钮 */
.member-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px;
  background: #fff;
  border-top: 1px solid #ebedf0;
}

.logout-btn {
  height: 48px;
  font-size: 16px;
  font-weight: 600;
}

/* 响应式设计 */
@media (max-width: 375px) {
  .member-content {
    padding: 12px;
  }
  
  .action-grid {
    grid-template-columns: repeat(4, 1fr);
    gap: 12px;
  }
  
  .action-item {
    padding: 8px;
  }
  
  .action-icon {
    width: 36px;
    height: 36px;
  }
}
</style> 