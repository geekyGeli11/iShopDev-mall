<template>
  <div class="member-card" :class="memberCardClass">
    <!-- 背景装饰 -->
    <div class="card-bg">
      <div class="bg-pattern"></div>
      <div class="shine-effect"></div>
    </div>

    <!-- 卡片内容 -->
    <div class="card-content">
      <!-- 会员头像和基本信息 -->
      <div class="member-header">
        <div class="avatar-section">
          <van-image
            :src="memberInfo.avatar || '/images/default-avatar.png'"
            width="60"
            height="60"
            round
            fit="cover"
            class="member-avatar"
          />
          <div class="level-badge" v-if="memberLevel">
            <van-tag :type="levelBadgeType as any" size="medium">
              {{ memberLevel }}
            </van-tag>
          </div>
        </div>
        
        <div class="member-basic">
          <h3 class="member-name">{{ memberInfo.name || memberInfo.phone }}</h3>
          <p class="member-phone">{{ formatPhone(memberInfo.phone) }}</p>
          <p class="member-id">会员号：{{ (memberInfo as any).memberNo || memberInfo.id }}</p>
        </div>
      </div>

      <!-- 会员特权显示 -->
      <div class="member-privileges" v-if="showPrivileges">
        <div class="privilege-item" v-for="privilege in privileges" :key="privilege.key">
          <van-icon :name="privilege.icon" class="privilege-icon" />
          <span class="privilege-text">{{ privilege.text }}</span>
        </div>
      </div>

      <!-- 积分和余额 -->
      <div class="member-stats">
        <div class="stat-item">
          <div class="stat-value">{{ formatNumber((memberInfo as any).integration || 0) }}</div>
          <div class="stat-label">积分</div>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <div class="stat-value">¥{{ formatPrice(memberInfo.balance || 0) }}</div>
          <div class="stat-label">余额</div>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <div class="stat-value">{{ formatNumber((memberInfo as any).couponCount || 0) }}</div>
          <div class="stat-label">优惠券</div>
        </div>
      </div>

      <!-- 会员等级进度 -->
      <div class="level-progress" v-if="showLevelProgress && nextLevelInfo">
        <div class="progress-header">
          <span class="current-level">{{ memberLevel }}</span>
          <span class="next-level">{{ nextLevelInfo.name }}</span>
        </div>
        <van-progress 
          :percentage="levelProgress" 
          :color="progressColor"
          track-color="#ffffff30"
          stroke-width="6"
          class="progress-bar"
        />
        <div class="progress-tip">
          还需消费 ¥{{ formatPrice(nextLevelInfo.remainAmount) }} 升级到{{ nextLevelInfo.name }}
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="card-actions" v-if="showActions">
        <van-button 
          type="primary" 
          size="small"
          plain
          @click="handleRecharge"
          class="action-btn"
        >
          <van-icon name="gold-coin-o" />
          充值
        </van-button>
        <van-button 
          type="primary" 
          size="small"
          plain
          @click="handleCoupons"
          class="action-btn"
        >
          <van-icon name="coupon-o" />
          优惠券
        </van-button>
        <van-button 
          type="primary" 
          size="small"
          plain
          @click="handleOrders"
          class="action-btn"
        >
          <van-icon name="orders-o" />
          订单
        </van-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, defineEmits, defineProps } from 'vue'
import type { MemberInfo, MemberLevel } from '@shared/types'

// Props定义
interface Props {
  memberInfo: MemberInfo
  showPrivileges?: boolean
  showLevelProgress?: boolean
  showActions?: boolean
  compact?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  showPrivileges: true,
  showLevelProgress: true,
  showActions: true,
  compact: false
})

// Emits定义
interface Emits {
  recharge: []
  coupons: []
  orders: []
  cardClick: []
}

const emit = defineEmits<Emits>()

// 会员等级配置
const levelConfig = {
  0: { name: '普通会员', color: '#969799', bgColor: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  1: { name: 'VIP1', color: '#1989fa', bgColor: 'linear-gradient(135deg, #ff6b6b 0%, #ffa500 100%)' },
  2: { name: 'VIP2', color: '#07c160', bgColor: 'linear-gradient(135deg, #ffd700 0%, #ffb347 100%)' },
  3: { name: 'VIP3', color: '#ee0a24', bgColor: 'linear-gradient(135deg, #000000 0%, #434343 100%)' }
}

// 计算属性
const memberLevel = computed(() => {
  const memberLevel = (props.memberInfo as any).memberLevel || 0
  const level = (props.memberInfo as any).memberLevelName || (levelConfig as any)[memberLevel]?.name
  return level
})

const levelBadgeType = computed(() => {
  const level = (props.memberInfo as any).memberLevel || 0
  if (level === 0) return 'default'
  if (level === 1) return 'primary'
  if (level === 2) return 'success'
  return 'danger'
})

const memberCardClass = computed(() => {
  return {
    'member-card--compact': props.compact,
    [`member-card--level-${(props.memberInfo as any).memberLevel || 0}`]: true
  }
})

const cardBackground = computed(() => {
  const level = (props.memberInfo as any).memberLevel || 0
  return levelConfig[level as keyof typeof levelConfig]?.bgColor || levelConfig[0].bgColor
})

const progressColor = computed(() => {
  const level = (props.memberInfo as any).memberLevel || 0
  return levelConfig[level as keyof typeof levelConfig]?.color || '#1989fa'
})

const privileges = computed(() => {
  const level = (props.memberInfo as any).memberLevel || 0
  const basePrivileges = [
    { key: 'points', icon: 'star-o', text: '积分奖励' },
    { key: 'coupons', icon: 'coupon-o', text: '专属优惠券' }
  ]
  
  if (level >= 1) {
    basePrivileges.push({ key: 'discount', icon: 'label-o', text: '会员折扣' })
  }
  
  if (level >= 2) {
    basePrivileges.push({ key: 'express', icon: 'logistics', text: '免费配送' })
  }
  
  if (level >= 3) {
    basePrivileges.push({ key: 'service', icon: 'service-o', text: '专属客服' })
  }
  
  return basePrivileges
})

const nextLevelInfo = computed(() => {
  const currentLevel = (props.memberInfo as any).memberLevel || 0
  if (currentLevel >= 3) return null
  
  const nextLevel = currentLevel + 1
  const nextLevelConfig = levelConfig[nextLevel as keyof typeof levelConfig]
  const currentSpent = (props.memberInfo as any).totalSpent || 0
  const levelThresholds = [0, 1000, 5000, 20000] // 升级阈值
  const nextThreshold = levelThresholds[nextLevel]
  
  return {
    name: nextLevelConfig.name,
    remainAmount: Math.max(0, nextThreshold - currentSpent)
  }
})

const levelProgress = computed(() => {
  if (!nextLevelInfo.value) return 100
  
  const currentSpent = (props.memberInfo as any).totalSpent || 0
  const currentLevel = (props.memberInfo as any).memberLevel || 0
  const levelThresholds = [0, 1000, 5000, 20000]
  const currentThreshold = levelThresholds[currentLevel]
  const nextThreshold = levelThresholds[currentLevel + 1]
  
  const progress = ((currentSpent - currentThreshold) / (nextThreshold - currentThreshold)) * 100
  return Math.min(100, Math.max(0, progress))
})

// 工具函数
const formatPhone = (phone: string) => {
  if (!phone) return ''
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

const formatPrice = (price: number) => {
  return price.toFixed(2)
}

const formatNumber = (num: number) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  }
  return num.toString()
}

// 事件处理
const handleRecharge = () => {
  emit('recharge')
}

const handleCoupons = () => {
  emit('coupons')
}

const handleOrders = () => {
  emit('orders')
}
</script>

<style scoped lang="scss">
.member-card {
  position: relative;
  background: v-bind(cardBackground);
  border-radius: 16px;
  overflow: hidden;
  color: white;
  min-height: 200px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 32px rgba(0, 0, 0, 0.2);
  }
  
  &--compact {
    min-height: 140px;
    
    .card-content {
      padding: 16px;
    }
    
    .member-header {
      margin-bottom: 12px;
    }
    
    .member-privileges,
    .level-progress {
      display: none;
    }
  }
}

.card-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
  
  .bg-pattern {
    position: absolute;
    top: -50%;
    right: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 1px, transparent 1px);
    background-size: 30px 30px;
    animation: pattern-move 20s linear infinite;
  }
  
  .shine-effect {
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: linear-gradient(45deg, transparent 30%, rgba(255, 255, 255, 0.1) 50%, transparent 70%);
    transform: rotate(45deg);
    animation: shine 3s ease-in-out infinite;
  }
}

.card-content {
  position: relative;
  z-index: 1;
  padding: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.member-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
  
  .avatar-section {
    position: relative;
    flex-shrink: 0;
    
    .member-avatar {
      border: 3px solid rgba(255, 255, 255, 0.3);
    }
    
    .level-badge {
      position: absolute;
      bottom: -4px;
      right: -4px;
    }
  }
  
  .member-basic {
    flex: 1;
    min-width: 0;
    
    .member-name {
      font-size: 18px;
      font-weight: 600;
      margin: 0 0 4px 0;
      color: white;
    }
    
    .member-phone,
    .member-id {
      font-size: 12px;
      color: rgba(255, 255, 255, 0.8);
      margin: 2px 0;
    }
  }
}

.member-privileges {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
  flex-wrap: wrap;
  
  .privilege-item {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 12px;
    color: rgba(255, 255, 255, 0.9);
    
    .privilege-icon {
      font-size: 14px;
    }
  }
}

.member-stats {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  padding: 12px 0;
  
  .stat-item {
    text-align: center;
    flex: 1;
    
    .stat-value {
      font-size: 18px;
      font-weight: 600;
      color: white;
      line-height: 1.2;
    }
    
    .stat-label {
      font-size: 12px;
      color: rgba(255, 255, 255, 0.7);
      margin-top: 2px;
    }
  }
  
  .stat-divider {
    width: 1px;
    height: 20px;
    background: rgba(255, 255, 255, 0.3);
    margin: 0 8px;
  }
}

.level-progress {
  margin-bottom: 16px;
  
  .progress-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
    font-size: 12px;
    
    .current-level {
      color: white;
      font-weight: 500;
    }
    
    .next-level {
      color: rgba(255, 255, 255, 0.7);
    }
  }
  
  .progress-bar {
    margin-bottom: 4px;
  }
  
  .progress-tip {
    font-size: 10px;
    color: rgba(255, 255, 255, 0.7);
    text-align: center;
  }
}

.card-actions {
  display: flex;
  gap: 8px;
  margin-top: auto;
  
  .action-btn {
    flex: 1;
    border-color: rgba(255, 255, 255, 0.5);
    color: white;
    
    &:hover {
      background: rgba(255, 255, 255, 0.1);
      border-color: rgba(255, 255, 255, 0.8);
    }
    
    :deep(.van-icon) {
      margin-right: 4px;
    }
  }
}

// 动画
@keyframes pattern-move {
  0% {
    transform: translate(0, 0);
  }
  100% {
    transform: translate(30px, 30px);
  }
}

@keyframes shine {
  0%, 100% {
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
}

// 移动端优化
@media (max-width: 767px) {
  .member-card {
    border-radius: 12px;
    
    .card-content {
      padding: 16px;
    }
    
    .member-header {
      gap: 12px;
      
      .member-basic .member-name {
        font-size: 16px;
      }
    }
    
    .member-stats .stat-item .stat-value {
      font-size: 16px;
    }
    
    .member-privileges {
      gap: 12px;
    }
  }
}
</style> 