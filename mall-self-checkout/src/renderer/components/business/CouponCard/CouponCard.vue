<template>
  <div class="coupon-card" :class="couponCardClass" @click="handleCardClick">
    <!-- 左侧金额区域 -->
    <div class="coupon-amount">
      <div class="amount-content">
        <span class="currency" v-if="coupon.type !== 3">¥</span>
        <span class="amount-value">{{ displayAmount }}</span>
        <span class="amount-unit">{{ amountUnit }}</span>
      </div>
      <div class="coupon-type">{{ couponTypeText }}</div>
    </div>

    <!-- 分割线装饰 -->
    <div class="coupon-divider">
      <div class="circle-top"></div>
      <div class="dashed-line"></div>
      <div class="circle-bottom"></div>
    </div>

    <!-- 右侧详情区域 -->
    <div class="coupon-details">
      <!-- 优惠券标题 -->
      <div class="coupon-header">
        <h3 class="coupon-title">{{ coupon.name }}</h3>
        <van-tag 
          :type="statusTag.type as any" 
          size="medium"
          class="status-tag"
        >
          {{ statusTag.text }}
        </van-tag>
      </div>

      <!-- 使用条件 -->
      <div class="coupon-condition">
        <van-icon name="info-o" class="condition-icon" />
        <span class="condition-text">{{ conditionText }}</span>
      </div>

      <!-- 有效期 -->
      <div class="coupon-validity">
        <van-icon name="clock-o" class="validity-icon" />
        <span class="validity-text">{{ validityText }}</span>
      </div>

      <!-- 适用商品 -->
      <div class="coupon-scope" v-if="scopeText">
        <van-icon name="label-o" class="scope-icon" />
        <span class="scope-text">{{ scopeText }}</span>
      </div>

      <!-- 操作区域 -->
      <div class="coupon-actions" v-if="showActions">
        <van-button 
          v-if="canUse"
          type="primary" 
          size="small"
          @click.stop="handleUse"
          :loading="using"
          class="action-btn"
        >
          {{ useButtonText }}
        </van-button>
        
        <van-button 
          v-else-if="canReceive"
          type="primary" 
          size="small"
          @click.stop="handleReceive"
          :loading="receiving"
          class="action-btn"
        >
          {{ receiveButtonText }}
        </van-button>
        
        <van-button 
          v-else
          size="small"
          disabled
          class="action-btn"
        >
          {{ disabledButtonText }}
        </van-button>
      </div>
    </div>

    <!-- 背景装饰 -->
    <div class="coupon-bg" v-if="!isUsed && !isExpired">
      <div class="bg-pattern"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, defineEmits, defineProps } from 'vue'
import type { MemberCouponVO } from '@shared/types'

// 定义临时的Coupon类型
interface Coupon extends MemberCouponVO {
  status?: number
  discount?: number
  minAmount?: number
}

// Props定义
interface Props {
  coupon: Coupon
  showActions?: boolean
  compact?: boolean
  selectable?: boolean
  selected?: boolean
  using?: boolean
  receiving?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  showActions: true,
  compact: false,
  selectable: false,
  selected: false,
  using: false,
  receiving: false
})

// Emits定义
interface Emits {
  cardClick: [coupon: Coupon]
  use: [coupon: Coupon]
  receive: [coupon: Coupon]
}

const emit = defineEmits<Emits>()

// 计算属性
const couponCardClass = computed(() => {
  return {
    'coupon-card--compact': props.compact,
    'coupon-card--selectable': props.selectable,
    'coupon-card--selected': props.selected,
    'coupon-card--used': isUsed.value,
    'coupon-card--expired': isExpired.value,
    'coupon-card--disabled': !canUse.value && !canReceive.value,
    [`coupon-card--${props.coupon.type}`]: true
  }
})

const isUsed = computed(() => {
  return props.coupon.status === 2 // 已使用
})

const isExpired = computed(() => {
  return props.coupon.status === 3 || // 已过期
         (props.coupon.endTime && new Date(props.coupon.endTime) < new Date())
})

const isReceived = computed(() => {
  return props.coupon.status === 1 // 已领取
})

const canUse = computed(() => {
  return isReceived.value && !isUsed.value && !isExpired.value
})

const canReceive = computed(() => {
  return props.coupon.status === 0 && !isExpired.value // 未领取且未过期
})

const displayAmount = computed(() => {
  switch (props.coupon.type) {
    case 1: // 满减券
      return props.coupon.amount
    case 2: // 折扣券
      return ((props.coupon as any).discount || 0.9 * 10).toFixed(1)
    case 3: // 免费券
      return '免费'
    default:
      return props.coupon.amount || 0
  }
})

const amountUnit = computed(() => {
  switch (props.coupon.type) {
    case 2: // 折扣券
      return '折'
    default:
      return ''
  }
})

const couponTypeText = computed(() => {
  switch (props.coupon.type) {
    case 1:
      return '满减券'
    case 2:
      return '折扣券'
    case 3:
      return '免费券'
    default:
      return '优惠券'
  }
})

const statusTag = computed(() => {
  if (isUsed.value) {
    return { type: 'default', text: '已使用' }
  }
  if (isExpired.value) {
    return { type: 'danger', text: '已过期' }
  }
  if (isReceived.value) {
    return { type: 'success', text: '可使用' }
  }
  return { type: 'primary', text: '可领取' }
})

const conditionText = computed(() => {
  if (props.coupon.minAmount && props.coupon.minAmount > 0) {
    return `满¥${props.coupon.minAmount}可用`
  }
  return '无门槛使用'
})

const validityText = computed(() => {
  if (props.coupon.startTime && props.coupon.endTime) {
    const start = new Date(props.coupon.startTime).toLocaleDateString()
    const end = new Date(props.coupon.endTime).toLocaleDateString()
    return `${start} - ${end}`
  }
  return '长期有效'
})

const scopeText = computed(() => {
  const coupon = props.coupon as any
  if (coupon.productIds && coupon.productIds.length > 0) {
    return '指定商品可用'
  }
  if (coupon.categoryIds && coupon.categoryIds.length > 0) {
    return '指定分类可用'
  }
  return '全场通用'
})

const useButtonText = computed(() => {
  if (props.using) return '使用中...'
  return props.selectable ? '选择' : '立即使用'
})

const receiveButtonText = computed(() => {
  if (props.receiving) return '领取中...'
  return '立即领取'
})

const disabledButtonText = computed(() => {
  if (isUsed.value) return '已使用'
  if (isExpired.value) return '已过期'
  return '不可用'
})

// 事件处理
const handleCardClick = () => {
  if (props.selectable && canUse.value) {
    emit('use', props.coupon)
  } else {
    emit('cardClick', props.coupon)
  }
}

const handleUse = () => {
  if (canUse.value) {
    emit('use', props.coupon)
  }
}

const handleReceive = () => {
  if (canReceive.value) {
    emit('receive', props.coupon)
  }
}
</script>

<style scoped lang="scss">
.coupon-card {
  position: relative;
  display: flex;
  background: #ffffff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  cursor: pointer;
  
  &:hover {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
    transform: translateY(-2px);
  }
  
  &:active {
    transform: translateY(0) scale(0.98);
  }
  
  // 状态样式
  &--selectable {
    cursor: pointer;
    
    &:hover {
      border: 2px solid var(--van-primary-color);
    }
  }
  
  &--selected {
    border: 2px solid var(--van-primary-color);
    box-shadow: 0 0 0 2px rgba(25, 137, 250, 0.2);
  }
  
  &--used,
  &--expired,
  &--disabled {
    opacity: 0.6;
    cursor: not-allowed;
    
    &:hover {
      transform: none;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    }
  }
  
  // 紧凑模式
  &--compact {
    .coupon-amount {
      min-width: 80px;
      
      .amount-content {
        .amount-value {
          font-size: 18px;
        }
      }
    }
    
    .coupon-details {
      padding: 12px;
      
      .coupon-title {
        font-size: 13px;
      }
    }
  }
  
  // 券类型主题色
  &--1 { // 满减券
    .coupon-amount {
      background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
    }
  }
  
  &--2 { // 折扣券
    .coupon-amount {
      background: linear-gradient(135deg, #ffa500 0%, #ff8c00 100%);
    }
  }
  
  &--3 { // 免费券
    .coupon-amount {
      background: linear-gradient(135deg, #00c851 0%, #00a83f 100%);
    }
  }
}

.coupon-amount {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-width: 100px;
  padding: 16px 12px;
  background: linear-gradient(135deg, #1989fa 0%, #0e7ce8 100%);
  color: white;
  
  .amount-content {
    display: flex;
    align-items: baseline;
    line-height: 1;
    
    .currency {
      font-size: 14px;
      font-weight: 500;
    }
    
    .amount-value {
      font-size: 24px;
      font-weight: 700;
      margin: 0 2px;
    }
    
    .amount-unit {
      font-size: 14px;
      font-weight: 500;
    }
  }
  
  .coupon-type {
    font-size: 11px;
    margin-top: 4px;
    opacity: 0.9;
  }
}

.coupon-divider {
  position: relative;
  width: 1px;
  background: transparent;
  
  .circle-top,
  .circle-bottom {
    position: absolute;
    width: 16px;
    height: 16px;
    background: #f7f8fa;
    border-radius: 50%;
    left: -8px;
  }
  
  .circle-top {
    top: -8px;
  }
  
  .circle-bottom {
    bottom: -8px;
  }
  
  .dashed-line {
    position: absolute;
    top: 8px;
    bottom: 8px;
    left: 0;
    width: 1px;
    background: repeating-linear-gradient(
      to bottom,
      #e5e5e5 0px,
      #e5e5e5 4px,
      transparent 4px,
      transparent 8px
    );
  }
}

.coupon-details {
  flex: 1;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  
  .coupon-header {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 8px;
    
    .coupon-title {
      flex: 1;
      font-size: 14px;
      font-weight: 600;
      color: #323233;
      margin: 0;
      line-height: 1.4;
      
      // 最多显示两行
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
    
    .status-tag {
      flex-shrink: 0;
    }
  }
  
  .coupon-condition,
  .coupon-validity,
  .coupon-scope {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 12px;
    color: #646566;
    line-height: 1.2;
    
    .condition-icon,
    .validity-icon,
    .scope-icon {
      font-size: 12px;
      color: #969799;
      flex-shrink: 0;
    }
    
    .condition-text,
    .validity-text,
    .scope-text {
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
  
  .coupon-actions {
    margin-top: auto;
    display: flex;
    justify-content: flex-end;
    
    .action-btn {
      min-width: 80px;
    }
  }
}

.coupon-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  overflow: hidden;
  
  .bg-pattern {
    position: absolute;
    top: -20px;
    right: -20px;
    width: 60px;
    height: 60px;
    background: radial-gradient(circle, rgba(25, 137, 250, 0.1) 2px, transparent 2px);
    background-size: 12px 12px;
    border-radius: 50%;
    animation: float 6s ease-in-out infinite;
  }
}

// 动画
@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-10px) rotate(180deg);
  }
}

// 移动端优化
@media (max-width: 767px) {
  .coupon-card {
    border-radius: 8px;
    
    .coupon-amount {
      min-width: 85px;
      padding: 12px 8px;
      
      .amount-content .amount-value {
        font-size: 20px;
      }
    }
    
    .coupon-details {
      padding: 12px;
      gap: 6px;
      
      .coupon-title {
        font-size: 13px;
      }
      
      .coupon-condition,
      .coupon-validity,
      .coupon-scope {
        font-size: 11px;
      }
    }
  }
}

// 无障碍支持
@media (prefers-reduced-motion: reduce) {
  .coupon-card {
    transition: none;
    
    &:hover {
      transform: none;
    }
    
    &:active {
      transform: none;
    }
  }
  
  .bg-pattern {
    animation: none;
  }
}
</style> 