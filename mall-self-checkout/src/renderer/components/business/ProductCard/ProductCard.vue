<template>
  <div class="product-card" @click="handleCardClick">
    <!-- 商品图片 -->
    <div class="product-image">
      <van-image
        :src="product.pic || '/images/placeholder-product.png'"
        :width="imageWidth"
        :height="imageHeight"
        fit="cover"
        lazy-load
        :show-error="true"
        :show-loading="true"
        :error-icon="'photo-fail'"
        :loading-icon="'photo'"
        class="product-img"
      />
      
      <!-- 促销标签 -->
      <div class="promotion-tags" v-if="product.promotionType && product.promotionType > 0">
        <van-tag 
          type="danger" 
          size="medium"
          v-if="product.promotionType === 1"
        >
          促销
        </van-tag>
        <van-tag 
          type="primary" 
          size="medium"
          v-else-if="product.promotionType === 2"
        >
          会员价
        </van-tag>
        <van-tag 
          type="warning" 
          size="medium"
          v-else-if="product.promotionType === 5"
        >
          限时购
        </van-tag>
      </div>
    </div>

    <!-- 商品信息 -->
    <div class="product-info">
      <h3 class="product-name" :class="{ 'single-line': !showDescription }">
        {{ product.name }}
      </h3>
      
      <p class="product-desc" v-if="showDescription && product.description">
        {{ product.description }}
      </p>

      <!-- 价格信息 -->
      <div class="price-section">
        <div class="current-price">
          <span class="currency">¥</span>
          <span class="price-value">{{ currentPrice }}</span>
        </div>
        
        <div class="original-price" v-if="showOriginalPrice">
          <span class="original-text">¥{{ product.originalPrice }}</span>
        </div>
        
        <!-- 会员专享价 -->
        <div class="member-price" v-if="memberPrice && showMemberPrice">
          <van-tag type="primary" size="medium">会员价</van-tag>
          <span class="member-price-text">¥{{ memberPrice }}</span>
        </div>
      </div>

      <!-- 库存状态 -->
      <div class="stock-status" v-if="showStock">
        <van-tag 
          :type="stockTagType" 
          size="medium"
          class="stock-tag"
        >
          {{ stockStatusText }}
        </van-tag>
        <span class="stock-count" v-if="product.stock > 0">
          库存: {{ product.stock }}
        </span>
      </div>

      <!-- 操作按钮 -->
      <div class="product-actions" v-if="showActions">
        <van-button 
          type="primary" 
          size="small"
          :disabled="!product.saleable || product.stock <= 0"
          :loading="addingToCart"
          @click.stop="handleAddToCart"
          class="add-btn"
        >
          {{ addButtonText }}
        </van-button>
        
        <!-- 数量控制器 -->
        <div class="quantity-control" v-if="showQuantityControl && quantity > 0">
          <van-stepper 
            v-model="quantity" 
            :min="0"
            :max="product.stock || 99"
            :disabled="!product.saleable"
            @change="handleQuantityChange"
            button-size="24px"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, defineEmits, defineProps } from 'vue'
import type { ProductScanVO } from '@shared/types'

// 定义临时的Product类型
interface Product extends ProductScanVO {
  id?: number
  pic?: string
  name?: string
  description?: string
  price?: number
  memberPrice?: number
  promotionType?: number
  promotionPrice?: number
  promotionStartTime?: Date
  promotionEndTime?: Date
}

// Props定义
interface Props {
  product: Product
  imageWidth?: string | number
  imageHeight?: string | number
  showDescription?: boolean
  showStock?: boolean
  showActions?: boolean
  showQuantityControl?: boolean
  showMemberPrice?: boolean
  layout?: 'vertical' | 'horizontal'
  addingToCart?: boolean
  quantity?: number
}

const props = withDefaults(defineProps<Props>(), {
  imageWidth: 120,
  imageHeight: 120,
  showDescription: false,
  showStock: true,
  showActions: true,
  showQuantityControl: false,
  showMemberPrice: true,
  layout: 'vertical',
  addingToCart: false,
  quantity: 0
})

// Emits定义
interface Emits {
  cardClick: [product: Product]
  addToCart: [product: Product, quantity: number]
  quantityChange: [product: Product, quantity: number]
}

const emit = defineEmits<Emits>()

// 响应式数据
const quantity = ref(props.quantity || 0)

// 计算属性
const currentPrice = computed(() => {
  if (props.product.promotionPrice && props.product.promotionPrice > 0) {
    return props.product.promotionPrice.toFixed(2)
  }
  return props.product.currentPrice?.toFixed(2) || props.product.price?.toFixed(2) || '0.00'
})

const memberPrice = computed(() => {
  return props.product.memberPrice
})

const showOriginalPrice = computed(() => {
  const original = props.product.originalPrice
  const current = parseFloat(currentPrice.value)
  return original && original > current
})

const stockTagType = computed(() => {
  if (props.product.stock <= 0) return 'danger'
  if (props.product.stock <= (props.product.lowStock || 10)) return 'warning'
  return 'success'
})

const stockStatusText = computed(() => {
  if (props.product.stock <= 0) return '缺货'
  if (props.product.stock <= (props.product.lowStock || 10)) return '库存不足'
  return '有库存'
})

const addButtonText = computed(() => {
  if (!props.product.saleable) return '不可售'
  if (props.product.stock <= 0) return '缺货'
  if (props.addingToCart) return '添加中...'
  return '加入购物车'
})

// 事件处理
const handleCardClick = () => {
  emit('cardClick', props.product)
}

const handleAddToCart = () => {
  if (!props.product.saleable || props.product.stock <= 0) return
  
  const qty = quantity.value > 0 ? quantity.value : 1
  emit('addToCart', props.product, qty)
}

const handleQuantityChange = (value: number) => {
  quantity.value = value
  emit('quantityChange', props.product, value)
}
</script>

<style scoped lang="scss">
.product-card {
  background: #ffffff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  
  &:hover {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
    transform: translateY(-2px);
  }
  
  &:active {
    transform: translateY(0) scale(0.98);
  }
}

.product-image {
  position: relative;
  background: #f7f8fa;
  display: flex;
  align-items: center;
  justify-content: center;
  
  .product-img {
    width: 100%;
    height: 100%;
  }
  
  .promotion-tags {
    position: absolute;
    top: 8px;
    left: 8px;
    display: flex;
    flex-direction: column;
    gap: 4px;
  }
}

.product-info {
  padding: 12px;
}

.product-name {
  font-size: 14px;
  font-weight: 600;
  color: #323233;
  margin: 0 0 4px 0;
  line-height: 1.4;
  
  &:not(.single-line) {
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
  
  &.single-line {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.product-desc {
  font-size: 12px;
  color: #646566;
  line-height: 1.4;
  margin: 0 0 8px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.price-section {
  margin-bottom: 8px;
  
  .current-price {
    display: flex;
    align-items: baseline;
    
    .currency {
      font-size: 12px;
      color: #ee0a24;
      font-weight: 500;
    }
    
    .price-value {
      font-size: 16px;
      font-weight: 600;
      color: #ee0a24;
    }
  }
  
  .original-price {
    margin-top: 2px;
    
    .original-text {
      font-size: 12px;
      color: #969799;
      text-decoration: line-through;
    }
  }
  
  .member-price {
    display: flex;
    align-items: center;
    gap: 4px;
    margin-top: 4px;
    
    .member-price-text {
      font-size: 12px;
      font-weight: 600;
      color: #1989fa;
    }
  }
}

.stock-status {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  
  .stock-count {
    font-size: 12px;
    color: #646566;
  }
}

.product-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  
  .add-btn {
    flex: 1;
    min-height: 32px;
    font-size: 12px;
  }
  
  .quantity-control {
    flex-shrink: 0;
  }
}

// 水平布局样式
.product-card.horizontal {
  display: flex;
  
  .product-image {
    flex-shrink: 0;
    width: 100px;
    height: 100px;
  }
  
  .product-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }
}

// 移动端优化
@media (max-width: 767px) {
  .product-card {
    border-radius: 8px;
  }
  
  .product-info {
    padding: 10px;
  }
  
  .product-name {
    font-size: 13px;
  }
  
  .current-price .price-value {
    font-size: 15px;
  }
}
</style> 