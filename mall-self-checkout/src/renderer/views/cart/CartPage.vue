<template>
  <div class="cart-page">
    <!-- 头部导航 -->
    <van-nav-bar 
      title="购物车" 
      left-arrow 
      @click-left="goBack"
      fixed
      placeholder
    >
      <template #right>
        <van-button
          v-if="cartItems.length > 0"
          type="default"
          size="mini"
          round
          @click="clearCart"
        >
          清空
        </van-button>
      </template>
    </van-nav-bar>

    <!-- 空购物车状态 -->
    <div v-if="cartItems.length === 0" class="empty-cart">
      <van-empty 
        image="https://img.yzcdn.cn/vant/custom-empty-image.png"
        description="购物车是空的"
      >
        <van-button 
          type="primary" 
          round
          @click="goToScan"
        >
          去扫码购物
        </van-button>
      </van-empty>
    </div>

    <!-- 购物车内容 -->
    <div v-else class="cart-content">
      <!-- 会员信息 -->
      <div v-if="memberStore.isMemberLogin" class="member-section">
        <van-cell-group inset>
          <van-cell center>
            <template #icon>
              <van-avatar 
                size="40" 
                :src="memberStore.memberInfo?.icon"
                icon="user-o"
                class="member-avatar"
              />
            </template>
            
            <template #title>
              <div class="member-info">
                <h4 class="member-name">{{ memberStore.memberInfo?.nickname || '会员用户' }}</h4>
                <p class="member-level">{{ memberStore.memberLevelName || 'VIP会员' }}</p>
              </div>
            </template>
            
            <template #right-icon>
              <div class="member-benefits">
                <van-tag type="danger" size="medium">会员专享</van-tag>
                <span class="savings">已省 ¥{{ memberSavings.toFixed(2) }}</span>
              </div>
            </template>
          </van-cell>
        </van-cell-group>
      </div>

      <!-- 商品列表 -->
      <div class="products-section">
        <van-cell-group inset>
          <van-cell title="商品列表" :value="`${cartItems.length}件商品`" />
        </van-cell-group>

        <van-swipe-cell 
          v-for="item in cartItems" 
          :key="item.id"
          class="cart-item-swipe"
        >
          <van-cell-group inset class="cart-item">
            <van-cell center>
              <!-- 选择框 -->
              <template #icon>
                <van-checkbox 
                  v-model="item.selected"
                  :disabled="!item.stock || updatingItem === item.id"
                  @change="updateItemSelection(item)"
                />
              </template>
              
              <!-- 商品信息 -->
              <template #title>
                <div class="product-info">
                  <van-image
                    :src="item.productPic || '/images/placeholder.png'"
                    width="60"
                    height="60"
                    fit="cover"
                    round
                    class="product-image"
                  />
                  
                  <div class="product-details">
                    <h4 class="product-name">{{ item.productName }}</h4>
                    <p class="product-subtitle">{{ item.productSubTitle }}</p>
                    
                    <!-- 规格信息 -->
                    <div v-if="item.productAttr" class="product-specs">
                      <van-tag 
                        v-for="(spec, index) in parseSpecs(item.productAttr)" 
                        :key="index"
                        type="default" 
                        size="medium"
                        class="spec-tag"
                      >
                        {{ spec }}
                      </van-tag>
                    </div>
                    
                    <!-- 价格信息 -->
                    <div class="price-section">
                      <span class="current-price">¥{{ item.price }}</span>
                      
                      <span 
                        v-if="item.originalPrice && item.originalPrice > item.price"
                        class="original-price"
                      >
                        ¥{{ item.originalPrice }}
                      </span>
                      
                      <van-tag 
                        v-if="memberStore.isLoggedIn && item.memberPrice"
                        type="danger"
                        size="medium"
                      >
                        会员价
                      </van-tag>
                    </div>
                    
                    <!-- 库存警告 -->
                    <div v-if="item.stock <= 0" class="stock-warning">
                      <van-icon name="warning-o" />
                      商品已售罄
                    </div>
                    <div v-else-if="item.stock < item.quantity" class="stock-warning">
                      <van-icon name="warning-o" />
                      库存不足，仅剩 {{ item.stock }} 件
                    </div>
                  </div>
                </div>
              </template>
              
              <!-- 数量控制 -->
              <template #right-icon>
                <div class="quantity-controls">
                  <van-stepper 
                    v-model="item.quantity"
                    :min="1"
                    :max="item.stock || 99"
                    :disabled="!item.stock || updatingItem === item.id"
                    :loading="updatingItem === item.id"
                    button-size="28"
                    input-width="40"
                    @change="updateItemQuantity(item)"
                  />
                </div>
              </template>
            </van-cell>
          </van-cell-group>
          
          <!-- 滑动删除 -->
          <template #right>
            <van-button 
              square 
              type="danger" 
              text="删除"
              class="delete-button"
              @click="removeItem(item)"
            />
          </template>
        </van-swipe-cell>
      </div>

      <!-- 优惠券选择 -->
      <div v-if="memberStore.isLoggedIn && totalAmount > 0" class="coupon-section">
        <van-cell-group inset>
          <van-cell 
            title="优惠券"
            :value="selectedCoupon ? `已选择 ${selectedCoupon.name}` : availableCoupons.length > 0 ? '请选择优惠券' : '暂无可用优惠券'"
            :is-link="availableCoupons.length > 0"
            @click="availableCoupons.length > 0 && (showCouponPicker = true)"
          >
            <template #icon>
              <van-icon name="coupon-o" size="20" color="#ff976a" />
            </template>
            <template #right-icon v-if="selectedCoupon">
              <van-tag type="danger" size="medium">
                -¥{{ couponDiscount.toFixed(2) }}
              </van-tag>
            </template>
          </van-cell>
        </van-cell-group>
      </div>

      <!-- 配送方式 -->
      <div class="delivery-section">
        <van-cell-group inset>
          <van-cell 
            title="配送方式"
            value="自提（推荐）"
            is-link
            @click="showDeliveryPicker = true"
          >
            <template #icon>
              <van-icon name="logistics" size="20" color="#1989fa" />
            </template>
          </van-cell>
        </van-cell-group>
      </div>

      <!-- 费用明细 -->
      <div class="cost-section">
        <van-cell-group inset>
          <van-cell title="费用明细" />
          <van-cell title="商品金额" :value="`¥${totalAmount.toFixed(2)}`" />
          <van-cell 
            v-if="couponDiscount > 0"
            title="优惠券抵扣" 
            :value="`-¥${couponDiscount.toFixed(2)}`"
            value-class="discount-value"
          />
          <van-cell 
            v-if="memberDiscount > 0"
            title="会员折扣" 
            :value="`-¥${memberDiscount.toFixed(2)}`"
            value-class="discount-value"
          />
          <van-cell 
            title="配送费" 
            :value="deliveryFee > 0 ? `¥${deliveryFee.toFixed(2)}` : '免费'"
            :value-class="deliveryFee > 0 ? '' : 'free-value'"
          />
        </van-cell-group>
      </div>
    </div>

    <!-- 底部结算栏 -->
    <div v-if="cartItems.length > 0" class="checkout-bar">
      <div class="checkout-info">
        <van-checkbox 
          v-model="allSelected" 
          :indeterminate="indeterminate"
          @change="toggleSelectAll"
        >
          全选
        </van-checkbox>
        
        <div class="cost-info">
          <div class="selected-count">已选择 {{ selectedItems.length }} 件商品</div>
          <div class="total-cost">
            <span class="label">合计:</span>
            <span class="amount">¥{{ finalAmount.toFixed(2) }}</span>
          </div>
        </div>
      </div>
      
      <van-button 
        type="primary"
        size="large"
        :disabled="selectedItems.length === 0 || hasInvalidItems || checkingOut"
        :loading="checkingOut"
        round
        @click="proceedToCheckout"
      >
        {{ selectedItems.length === 0 ? '请选择商品' : '立即结算' }}
      </van-button>
    </div>

    <!-- 优惠券选择弹窗 -->
    <van-popup 
      v-model:show="showCouponPicker" 
      position="bottom" 
      :style="{ height: '70vh' }"
      closeable
      round
    >
      <div class="coupon-picker">
        <van-nav-bar title="选择优惠券" />
        
        <div class="coupon-list">
          <van-cell 
            title="不使用优惠券"
            :value="!selectedCoupon ? '✓' : ''"
            clickable
            @click="selectCoupon(null)"
            class="no-coupon-option"
          />
          
          <div v-if="availableCoupons.length === 0" class="empty-coupons">
            <van-empty description="暂无可用优惠券" />
          </div>
          
          <div 
            v-for="coupon in availableCoupons"
            :key="coupon.id"
            class="coupon-item"
            :class="{ selected: selectedCoupon?.id === coupon.id }"
            @click="selectCoupon(coupon)"
          >
            <div class="coupon-content">
              <div class="coupon-amount">
                <span class="currency" v-if="coupon.type !== 3">¥</span>
                <span class="amount">{{ getDisplayAmount(coupon) }}</span>
                <span class="unit" v-if="coupon.type === 2">折</span>
              </div>
              
              <div class="coupon-info">
                <h4 class="coupon-name">{{ coupon.name }}</h4>
                <p class="coupon-condition">
                  {{ getConditionText(coupon) }}
                </p>
                <p class="coupon-validity">
                  {{ getValidityText(coupon) }}
                </p>
              </div>
              
              <div class="coupon-select">
                <van-radio 
                  :model-value="selectedCoupon?.id === coupon.id"
                  @click.stop="selectCoupon(coupon)"
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </van-popup>

    <!-- 配送方式选择弹窗 -->
    <van-popup 
      v-model:show="showDeliveryPicker" 
      position="bottom" 
      :style="{ height: '50vh' }"
      closeable
      round
    >
      <div class="delivery-picker">
        <van-nav-bar title="选择配送方式" />
        
        <van-cell-group class="delivery-options">
          <van-cell 
            v-for="option in deliveryOptions"
            :key="option.id"
            :title="option.name"
            :label="option.description"
            :value="selectedDelivery?.id === option.id ? '✓' : ''"
            @click="selectDelivery(option)"
          >
            <template #right-icon>
              <span class="delivery-fee">
                {{ option.fee > 0 ? `¥${option.fee}` : '免费' }}
              </span>
            </template>
          </van-cell>
        </van-cell-group>
      </div>
    </van-popup>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showLoadingToast, closeToast, showConfirmDialog, showNotify } from 'vant'
import { useCartStore } from '@/store/modules/cart'
import { useMemberStore } from '@/store/modules/member'
import { CouponAPI } from '@/api/modules/coupon'

const router = useRouter()
const cartStore = useCartStore()
const memberStore = useMemberStore()

// 返回方法
const goBack = () => {
  router.back()
}

// 响应式数据
const cartItems = ref<any[]>([])
const availableCoupons = ref<any[]>([])
const selectedCoupon = ref<any>(null)
const selectedDelivery = ref<any>(null)
const updatingItem = ref<string | null>(null)
const checkingOut = ref(false)
const showCouponPicker = ref(false)
const showDeliveryPicker = ref(false)

// 配送选项
const deliveryOptions = ref([
  {
    id: 'pickup',
    name: '自提',
    description: '到店自取，节省配送费',
    fee: 0
  },
  {
    id: 'express',
    name: '快递配送',
    description: '1-2个工作日送达',
    fee: 8
  },
  {
    id: 'same_day',
    name: '当日达',
    description: '当天送达（限本市）',
    fee: 15
  }
])

// 计算属性
const selectedItems = computed(() => {
  return cartItems.value.filter(item => item.selected && item.stock > 0)
})

const allSelected = computed({
  get: () => {
    const validItems = cartItems.value.filter(item => item.stock > 0)
    return validItems.length > 0 && validItems.every(item => item.selected)
  },
  set: (value: boolean) => {
    toggleSelectAll(value)
  }
})

const indeterminate = computed(() => {
  const validItems = cartItems.value.filter(item => item.stock > 0)
  const selectedValidItems = validItems.filter(item => item.selected)
  return selectedValidItems.length > 0 && selectedValidItems.length < validItems.length
})

const totalAmount = computed(() => {
  return selectedItems.value.reduce((total, item) => {
    return total + (item.price * item.quantity)
  }, 0)
})

const memberDiscount = computed(() => {
  if (!memberStore.isLoggedIn) return 0
  return selectedItems.value.reduce((discount, item) => {
    if (item.originalPrice && item.price < item.originalPrice) {
      return discount + ((item.originalPrice - item.price) * item.quantity)
    }
    return discount
  }, 0)
})

const memberSavings = computed(() => {
  return memberDiscount.value + couponDiscount.value
})

const couponDiscount = computed(() => {
  if (!selectedCoupon.value) return 0
  
  // 根据优惠券类型计算折扣
  const coupon = selectedCoupon.value
  let discount = 0
  
  if (coupon.type === 1) {
    // 满减券
    if (totalAmount.value >= (coupon.minAmount || 0)) {
      discount = Math.min(coupon.amount || 0, totalAmount.value)
    }
  } else if (coupon.type === 2) {
    // 折扣券
    if (totalAmount.value >= (coupon.minAmount || 0)) {
      const discountRate = (coupon.discount || 100) / 100
      discount = totalAmount.value * (1 - discountRate)
      discount = Math.min(discount, coupon.maxAmount || discount)
    }
  } else if (coupon.type === 3) {
    // 免费券/赠品券
    if (totalAmount.value >= (coupon.minAmount || 0)) {
      discount = Math.min(coupon.amount || 0, totalAmount.value)
    }
  }
  
  return discount
})

const deliveryFee = computed(() => {
  return selectedDelivery.value?.fee || 0
})

const finalAmount = computed(() => {
  return Math.max(0, totalAmount.value - couponDiscount.value - memberDiscount.value + deliveryFee.value)
})

const hasInvalidItems = computed(() => {
  return selectedItems.value.some(item => !item.stock || item.stock < item.quantity)
})

// 解析商品规格
const parseSpecs = (productAttr: string) => {
  if (!productAttr) return []
  try {
    const specs = JSON.parse(productAttr)
    return Object.values(specs)
  } catch {
    return productAttr.split(',').filter(Boolean)
  }
}

// 加载购物车数据
const loadCartData = async () => {
  try {
    await cartStore.loadCart()
    cartItems.value = cartStore.cartItems.map((item: any) => ({
      ...item,
      selected: true
    }))
  } catch (error) {
    console.error('加载购物车失败:', error)
    showToast('加载购物车失败')
  }
}

// 加载优惠券
const loadCoupons = async () => {
  if (!memberStore.isLoggedIn) {
    availableCoupons.value = []
    return
  }
  
  try {
    // 获取订单可用优惠券
    const response = await CouponAPI.getAvailableCouponsForOrder(totalAmount.value)
    
    if (response.code === 200 && response.data) {
      availableCoupons.value = response.data.list || []
      console.log('加载可用优惠券成功:', availableCoupons.value.length, '张')
    } else {
      console.warn('获取可用优惠券失败:', response.message)
      availableCoupons.value = []
    }
  } catch (error) {
    console.error('加载优惠券失败:', error)
    availableCoupons.value = []
    
    // 不显示错误提示，避免影响用户体验
    // showToast('加载优惠券失败')
  }
}

// 更新商品选中状态
const updateItemSelection = (item: any) => {
  // 如果商品缺货，不允许选中
  if (!item.stock && item.selected) {
    item.selected = false
    showToast('商品缺货，无法选择')
  }
}

// 全选/取消全选
const toggleSelectAll = (value?: boolean) => {
  const selectAll = value !== undefined ? value : !allSelected.value
  cartItems.value.forEach(item => {
    if (item.stock > 0) {
      item.selected = selectAll
    }
  })
}

// 更新商品数量
const updateItemQuantity = async (item: any) => {
  if (updatingItem.value || !item.stock) return
  
  // 检查库存
  if (item.quantity > item.stock) {
    item.quantity = item.stock
    showToast(`库存不足，已调整为${item.stock}件`)
    return
  }
  
  updatingItem.value = item.id
  
  try {
    await cartStore.updateItemQuantity(item.productId, item.skuId, item.quantity)
    
    showNotify({
      type: 'success',
      message: '数量已更新',
      duration: 1000
    })
  } catch (error: any) {
    console.error('更新数量失败:', error)
    showToast(error.message || '更新数量失败')
    
    // 恢复原数量
    await loadCartData()
  } finally {
    updatingItem.value = null
  }
}

// 删除商品
const removeItem = async (item: any) => {
  try {
    await showConfirmDialog({
      title: '确认删除',
      message: `确定要删除 "${item.productName}" 吗？`
    })
    
    await cartStore.removeItem(item.productId, item.skuId)
    
    // 从本地列表移除
    const index = cartItems.value.findIndex(i => i.id === item.id)
    if (index >= 0) {
      cartItems.value.splice(index, 1)
    }
    
    showNotify({
      type: 'success',
      message: '商品已删除',
      duration: 1000
    })
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除商品失败:', error)
      showToast(error.message || '删除失败')
    }
  }
}

// 清空购物车
const clearCart = async () => {
  try {
    await showConfirmDialog({
      title: '确认清空',
      message: '确定要清空购物车吗？此操作不可恢复。'
    })
    
    // 使用本地清理方法，不调用后端API
    cartStore.clearLocalCart()
    
    cartItems.value = []
    showNotify({
      type: 'success',
      message: '购物车已清空',
      duration: 1000
    })
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('清空购物车失败:', error)
      showToast(error.message || '清空失败')
    }
  }
}

// 选择优惠券
const selectCoupon = (coupon: any) => {
  selectedCoupon.value = coupon
  showCouponPicker.value = false
  
  if (coupon) {
    showNotify({
      type: 'success',
      message: `已选择优惠券：${coupon.name}`,
      duration: 1500
    })
  }
}

// 获取优惠券显示金额
const getDisplayAmount = (coupon: any) => {
  switch (coupon.type) {
    case 1: // 满减券
      return coupon.amount
    case 2: // 折扣券
      return (coupon.discount / 10).toFixed(1)
    case 3: // 免费券
      return '免费'
    default:
      return coupon.amount || 0
  }
}

// 获取优惠券使用条件文本
const getConditionText = (coupon: any) => {
  if (coupon.minAmount && coupon.minAmount > 0) {
    return `满¥${coupon.minAmount}可用`
  }
  return '无门槛使用'
}

// 获取优惠券有效期文本
const getValidityText = (coupon: any) => {
  if (coupon.startTime && coupon.endTime) {
    const start = new Date(coupon.startTime).toLocaleDateString()
    const end = new Date(coupon.endTime).toLocaleDateString()
    return `${start} - ${end}`
  }
  return '长期有效'
}

// 选择配送方式
const selectDelivery = (delivery: any) => {
  selectedDelivery.value = delivery
  showDeliveryPicker.value = false
  
  showNotify({
    type: 'success',
    message: `已选择：${delivery.name}`,
    duration: 1500
  })
}

// 前往结算
const proceedToCheckout = async () => {
  if (selectedItems.value.length === 0) {
    showToast('请选择要结算的商品')
    return
  }
  
  if (hasInvalidItems.value) {
    showToast('存在缺货商品，请处理后再结算')
    return
  }
  
  checkingOut.value = true
  
  try {
    // 准备订单数据
    const orderData = {
      items: selectedItems.value,
      couponId: selectedCoupon.value?.id,
      deliveryType: selectedDelivery.value?.id || 'pickup',
      totalAmount: totalAmount.value,
      finalAmount: finalAmount.value,
      couponDiscount: couponDiscount.value,
      memberDiscount: memberDiscount.value,
      deliveryFee: deliveryFee.value
    }
    
    // 跳转到支付页面
    router.push({
      path: '/payment',
      query: {
        orderData: JSON.stringify(orderData)
      }
    })
    
  } catch (error: any) {
    console.error('准备结算失败:', error)
    showToast(error.message || '准备结算失败')
  } finally {
    checkingOut.value = false
  }
}

// 前往扫码
const goToScan = () => {
  router.push('/scan')
}

// 生命周期
onMounted(async () => {
  // 设置默认配送方式
  selectedDelivery.value = deliveryOptions.value[0]
  
  // 加载数据
  await loadCartData()
  
  // 等待购物车数据加载完成后再加载优惠券
  if (memberStore.isLoggedIn && totalAmount.value > 0) {
    await loadCoupons()
  }
})

// 监听购物车变化
watch(() => cartStore.cartItems, () => {
  loadCartData()
}, { deep: true })

// 监听会员登录状态变化
watch(() => memberStore.isLoggedIn, (newValue) => {
  if (newValue) {
    // 会员登录后重新加载优惠券
    loadCoupons()
  } else {
    // 会员退出后清空优惠券
    availableCoupons.value = []
    selectedCoupon.value = null
  }
})

// 监听订单金额变化，重新加载可用优惠券
watch(() => totalAmount.value, (newAmount, oldAmount) => {
  if (memberStore.isLoggedIn && newAmount > 0 && newAmount !== oldAmount) {
    loadCoupons()
  }
}, { immediate: false })
</script>

<style scoped lang="scss">
.cart-page {
  min-height: 100vh;
  background: var(--van-background-color);
  padding-bottom: calc(80px + var(--van-safe-area-inset-bottom));
}

.empty-cart {
  padding: 80px 32px;
  text-align: center;
}

.cart-content {
  padding-bottom: 16px;
}

.member-section {
  padding: 16px 16px 0;
  
  .member-avatar {
    margin-right: 12px;
  }
  
  .member-info {
    .member-name {
      font-size: 16px;
      font-weight: 600;
      color: var(--van-text-color);
      margin: 0 0 4px 0;
    }
    
    .member-level {
      font-size: 12px;
      color: var(--van-text-color-2);
      margin: 0;
    }
  }
  
  .member-benefits {
    text-align: right;
    
    .savings {
      display: block;
      font-size: 12px;
      color: #ee0a24;
      font-weight: 600;
      margin-top: 4px;
    }
  }
}

.products-section {
  padding: 16px 16px 0;
}

.cart-item-swipe {
  margin-bottom: 8px;
  
  .cart-item {
    .product-info {
      display: flex;
      align-items: flex-start;
      gap: 12px;
      
      .product-details {
        flex: 1;
        min-width: 0;
        
        .product-name {
          font-size: 14px;
          font-weight: 600;
          color: var(--van-text-color);
          margin: 0 0 4px 0;
          line-height: 1.4;
          overflow: hidden;
          text-overflow: ellipsis;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
        }
        
        .product-subtitle {
          font-size: 12px;
          color: var(--van-text-color-2);
          margin: 0 0 8px 0;
          line-height: 1.3;
        }
        
        .product-specs {
          margin-bottom: 8px;
          
          .spec-tag {
            margin-right: 4px;
            margin-bottom: 4px;
            font-size: 10px;
          }
        }
        
        .price-section {
          display: flex;
          align-items: center;
          gap: 6px;
          margin-bottom: 8px;
          
          .current-price {
            font-size: 16px;
            font-weight: 700;
            color: #ee0a24;
          }
          
          .original-price {
            font-size: 12px;
            color: var(--van-text-color-3);
            text-decoration: line-through;
          }
        }
        
        .stock-warning {
          display: flex;
          align-items: center;
          gap: 4px;
          font-size: 12px;
          color: #ff976a;
          
          .van-icon {
            font-size: 14px;
          }
        }
      }
    }
    
    .quantity-controls {
      margin-left: 12px;
    }
  }
  
  .delete-button {
    height: 100%;
    width: 80px;
  }
}

.coupon-section,
.delivery-section,
.cost-section {
  padding: 16px 16px 0;
}

.cost-section {
  .discount-value {
    color: #ee0a24;
    font-weight: 600;
  }
  
  .free-value {
    color: #07c160;
    font-weight: 600;
  }
}

.checkout-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: var(--van-background-color-light);
  border-top: 1px solid var(--van-border-color);
  padding: 12px 16px;
  padding-bottom: calc(12px + var(--van-safe-area-inset-bottom));
  display: flex;
  align-items: center;
  gap: 16px;
  z-index: 100;
  
  .checkout-info {
    flex: 1;
    display: flex;
    align-items: center;
    gap: 16px;
    
    .cost-info {
      flex: 1;
      text-align: right;
      
      .selected-count {
        font-size: 12px;
        color: var(--van-text-color-2);
        margin-bottom: 2px;
      }
      
      .total-cost {
        .label {
          font-size: 14px;
          color: var(--van-text-color);
        }
        
        .amount {
          font-size: 18px;
          font-weight: 700;
          color: #ee0a24;
          margin-left: 4px;
        }
      }
    }
  }
  
  .van-button {
    min-width: 100px;
    height: 44px;
    font-size: 16px;
    font-weight: 600;
  }
}

.coupon-picker,
.delivery-picker {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: var(--van-background-color);
  
  .coupon-list,
  .delivery-options {
    flex: 1;
    overflow-y: auto;
    
    .coupon-icon {
      margin-right: 12px;
    }
    
    .delivery-fee {
      font-size: 14px;
      font-weight: 600;
      color: #ee0a24;
    }
  }
}

/* 深色模式支持 */
@media (prefers-color-scheme: dark) {
  .checkout-bar {
    background: var(--van-background-color-light);
    border-top-color: var(--van-border-color);
  }
}

/* 响应式设计 */
@media (min-width: 768px) {
  .cart-content {
    max-width: 600px;
    margin: 0 auto;
  }
  
  .checkout-bar {
    max-width: 600px;
    left: 50%;
    transform: translateX(-50%);
  }
}

/* 优惠券选择弹窗样式 */
.coupon-picker {
  height: 100%;
  display: flex;
  flex-direction: column;
  
  .coupon-list {
    flex: 1;
    overflow-y: auto;
    padding: 16px;
    
    .no-coupon-option {
      margin-bottom: 16px;
      border-radius: 12px;
      overflow: hidden;
    }
    
    .empty-coupons {
      padding: 40px 20px;
      text-align: center;
    }
    
    .coupon-item {
      margin-bottom: 12px;
      background: white;
      border-radius: 12px;
      overflow: hidden;
      border: 2px solid transparent;
      transition: all 0.3s ease;
      
      &.selected {
        border-color: var(--van-primary-color);
        box-shadow: 0 0 0 2px rgba(25, 137, 250, 0.2);
      }
      
      .coupon-content {
        display: flex;
        align-items: center;
        padding: 16px;
        
        .coupon-amount {
          display: flex;
          align-items: baseline;
          min-width: 80px;
          margin-right: 16px;
          
          .currency {
            font-size: 14px;
            color: #ff6b6b;
            font-weight: 600;
          }
          
          .amount {
            font-size: 24px;
            font-weight: 700;
            color: #ff6b6b;
          }
          
          .unit {
            font-size: 14px;
            color: #ff6b6b;
            font-weight: 600;
            margin-left: 2px;
          }
        }
        
        .coupon-info {
          flex: 1;
          
          .coupon-name {
            font-size: 16px;
            font-weight: 600;
            color: var(--van-text-color);
            margin: 0 0 4px 0;
          }
          
          .coupon-condition {
            font-size: 12px;
            color: var(--van-text-color-2);
            margin: 0 0 2px 0;
          }
          
          .coupon-validity {
            font-size: 12px;
            color: var(--van-text-color-3);
            margin: 0;
          }
        }
        
        .coupon-select {
          margin-left: 12px;
        }
      }
    }
  }
}
</style> 