<template>
  <view class="sku-selector">
    <!-- 商品基本信息 -->
    <view class="product-info" v-if="product">
      <image class="product-image" :src="currentSkuImage || product.productPic" mode="aspectFill"></image>
      <view class="product-details">
        <text class="product-name">{{ product.productName }}</text>
        <view class="price-info">
          <text class="current-price">¥{{ currentPrice }}</text>
          <text class="points-price">+{{ product.pointsPrice }}积分</text>
        </view>
        <view class="stock-info" v-if="currentSku">
          <text class="stock-text">库存 {{ currentSku.stock }} 件</text>
        </view>
      </view>
    </view>

    <!-- 规格选择 -->
    <view class="spec-section" v-if="specificationList && specificationList.length > 0">
      <view class="spec-title">请选择商品规格</view>
      <view v-for="(spec, specIndex) in specificationList" :key="specIndex" class="spec-group">
        <view class="spec-name">{{ spec.name }}</view>
        <view class="spec-options">
          <view v-for="(value, valueIndex) in spec.values" :key="valueIndex" class="spec-option" :class="{
            'selected': selectedSpecs[spec.name] === value,
            'disabled': !isSpecValueAvailable(spec.name, value)
          }" @click="selectSpecValue(spec.name, value)">
            <text class="spec-option-text">{{ value }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 数量选择 -->
    <view class="quantity-section">
      <view class="quantity-row">
        <view class="quantity-label">数量</view>
        <view class="quantity-control">
          <view class="quantity-btn" :class="{ disabled: quantity <= 1 }" @click="decreaseQuantity">-</view>
          <input type="number" class="quantity-input" :value="quantity" @input="onQuantityInput"
            @blur="validateQuantity" />
          <view class="quantity-btn" :class="{ disabled: quantity >= maxQuantity }" @click="increaseQuantity">+</view>
        </view>
      </view>
      <view class="quantity-limit" v-if="maxQuantity > 0">
        <text>限购{{ maxQuantity }}件</text>
      </view>
    </view>

    <!-- 错误提示 -->
    <view class="error-message" v-if="errorMessage">
      <text class="error-text">{{ errorMessage }}</text>
    </view>
  </view>
</template>

<script>
export default {
  name: 'SkuSelector',
  props: {
    // 商品信息
    product: {
      type: Object,
      required: true
    },
    // SKU列表
    skuList: {
      type: Array,
      default: () => []
    },
    // 规格列表
    specificationList: {
      type: Array,
      default: () => []
    },
    // 最大购买数量
    maxQuantity: {
      type: Number,
      default: 999
    },
    // 初始数量
    initialQuantity: {
      type: Number,
      default: 1
    },
    // 是否使用固定价格（积分兑换场景）
    useFixedPrice: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      // 选中的规格
      selectedSpecs: {},
      // 当前数量
      quantity: 1,
      // 当前选中的SKU
      currentSku: null,
      // 错误信息
      errorMessage: ''
    }
  },
  computed: {
    // 当前价格
    currentPrice() {
      // 如果使用固定价格（积分兑换场景），始终显示商品的兑换价格
      if (this.useFixedPrice) {
        return this.product ? this.product.cashPrice.toFixed(2) : '0.00'
      }

      // 普通购买场景，选择SKU后显示SKU价格
      if (this.currentSku && this.currentSku.price) {
        return this.currentSku.price.toFixed(2)
      }
      return this.product ? this.product.cashPrice.toFixed(2) : '0.00'
    },

    // 当前SKU图片
    currentSkuImage() {
      return this.currentSku && this.currentSku.pic ? this.currentSku.pic : null
    },

    // 是否已选择完整规格
    isSpecComplete() {
      if (!this.specificationList || this.specificationList.length === 0) {
        return true // 没有规格要求，认为完整
      }

      for (let spec of this.specificationList) {
        if (!this.selectedSpecs[spec.name]) {
          return false
        }
      }
      return true
    }
  },
  watch: {
    selectedSpecs: {
      handler() {
        this.updateCurrentSku()
      },
      deep: true
    },
    quantity() {
      this.emitChange()
    },
    initialQuantity: {
      immediate: true,
      handler(newVal) {
        this.quantity = newVal || 1
      }
    }
  },
  methods: {
    // 选择规格值
    selectSpecValue(specName, value) {
      if (!this.isSpecValueAvailable(specName, value)) {
        return
      }

      this.$set(this.selectedSpecs, specName, value)
    },

    // 检查规格值是否可用
    isSpecValueAvailable(specName, value) {
      if (!this.skuList || this.skuList.length === 0) {
        return true
      }

      // 构建临时选择状态
      const tempSpecs = { ...this.selectedSpecs, [specName]: value }

      // 查找匹配的SKU
      const matchingSku = this.findMatchingSku(tempSpecs)

      // 如果找到匹配的SKU且有库存，则可用
      return matchingSku && matchingSku.stock > 0
    },

    // 根据选中规格查找匹配的SKU
    findMatchingSku(specs) {
      if (!this.skuList || this.skuList.length === 0) {
        return null
      }

      for (let sku of this.skuList) {
        if (this.isSkuMatchSpecs(sku, specs)) {
          return sku
        }
      }
      return null
    },

    // 检查SKU是否匹配规格
    isSkuMatchSpecs(sku, specs) {
      if (!sku.spData) {
        return Object.keys(specs).length === 0
      }

      try {
        const skuSpecs = JSON.parse(sku.spData)
        
        if (!Array.isArray(skuSpecs)) {
          return false
        }

        // 检查每个选中的规格是否匹配
        for (let specName in specs) {
          const specValue = specs[specName]
          const found = skuSpecs.some(item => {
            return item.key === specName && item.value === specValue
          })
          if (!found) {
            return false
          }
        }

        // 检查SKU的规格数量是否与选中规格数量一致
        return skuSpecs.length === Object.keys(specs).length
      } catch (e) {
        console.warn('解析SKU规格数据失败:', sku.spData, e)
        return false
      }
    },

    // 更新当前SKU
    updateCurrentSku() {
      if (this.isSpecComplete) {
        this.currentSku = this.findMatchingSku(this.selectedSpecs)
        
        if (!this.currentSku) {
          this.setError('所选规格组合不存在')
        } else if (this.currentSku.stock <= 0) {
          this.setError('所选规格暂无库存')
        } else {
          this.clearError()
        }
      } else {
        this.currentSku = null
      }
      
      // 每次更新SKU后都发送变更事件
      this.emitChange()
    },

    // 增加数量
    increaseQuantity() {
      if (this.quantity < this.maxQuantity) {
        this.quantity++
      }
    },

    // 减少数量
    decreaseQuantity() {
      if (this.quantity > 1) {
        this.quantity--
      }
    },

    // 处理数量输入
    onQuantityInput(e) {
      const value = parseInt(e.detail.value) || 1
      this.quantity = value
    },

    // 验证数量
    validateQuantity() {
      if (this.quantity < 1) {
        this.quantity = 1
      } else if (this.quantity > this.maxQuantity) {
        this.quantity = this.maxQuantity
      }

      // 检查库存限制
      if (this.currentSku && this.quantity > this.currentSku.stock) {
        this.quantity = this.currentSku.stock
        this.setError(`库存不足，最多可选${this.currentSku.stock}件`)
      }
    },

    // 设置错误信息
    setError(message) {
      this.errorMessage = message
    },

    // 清除错误信息
    clearError() {
      this.errorMessage = ''
    },

    // 发送变更事件
    emitChange() {
      // 对于没有SKU列表的商品，只要规格选择完整就认为有效
      let isValid = this.isSpecComplete
      
      // 如果有SKU列表，需要验证选中的SKU和库存
      if (this.skuList && this.skuList.length > 0) {
        isValid = isValid && this.currentSku && this.currentSku.stock >= this.quantity
      }
      
      const result = {
        isValid: isValid,
        selectedSku: this.currentSku,
        selectedSpecs: this.selectedSpecs,
        quantity: this.quantity,
        totalPrice: this.currentSku ? (this.currentSku.price * this.quantity).toFixed(2) : '0.00',
        errorMessage: this.errorMessage
      }

      this.$emit('change', result)
    },

    // 获取选择结果
    getSelection() {
      // 对于没有SKU列表的商品，只要规格选择完整就认为有效
      let isValid = this.isSpecComplete
      
      // 如果有SKU列表，需要验证选中的SKU和库存
      if (this.skuList && this.skuList.length > 0) {
        isValid = isValid && this.currentSku && this.currentSku.stock >= this.quantity
      }
      
      return {
        isValid: isValid,
        selectedSku: this.currentSku,
        selectedSpecs: this.selectedSpecs,
        quantity: this.quantity,
        totalPrice: this.currentSku ? (this.currentSku.price * this.quantity).toFixed(2) : '0.00',
        errorMessage: this.errorMessage
      }
    },

    // 自动选择单一规格选项
    autoSelectSingleSpecs() {
      if (this.specificationList && this.specificationList.length > 0) {
        for (let spec of this.specificationList) {
          if (spec.values && spec.values.length === 1) {
            // 如果某个规格只有一个选项，自动选择
            this.$set(this.selectedSpecs, spec.name, spec.values[0])
          }
        }
      }
    }
  },
  mounted() {
    // 如果只有一个规格选项，自动选择
    this.autoSelectSingleSpecs()
    
    this.emitChange()
  }
}
</script>

<style lang="scss" scoped>
.sku-selector {
  background: #fff;
}

.product-info {
  display: flex;
  margin-bottom: 40rpx;

  .product-image {
    width: 160rpx;
    height: 160rpx;
    border-radius: 16rpx;
    margin-right: 24rpx;
  }

  .product-details {
    flex: 1;

    .product-name {
      font-size: 32rpx;
      font-weight: 500;
      color: #333;
      line-height: 1.4;
      margin-bottom: 16rpx;
    }

    .price-info {
      display: flex;
      align-items: baseline;
      margin-bottom: 12rpx;

      .current-price {
        font-size: 36rpx;
        font-weight: 600;
        color: #ff6b35;
        margin-right: 16rpx;
      }

      .points-price {
        font-size: 28rpx;
        color: #647D00;
      }
    }

    .stock-info {
      .stock-text {
        font-size: 24rpx;
        color: #999;
      }
    }
  }
}

.spec-section {
  margin-bottom: 40rpx;

  .spec-title {
    font-size: 32rpx;
    font-weight: 500;
    color: #333;
    margin-bottom: 24rpx;
  }

  .spec-group {
    margin-bottom: 32rpx;

    .spec-name {
      font-size: 28rpx;
      color: #666;
      margin-bottom: 16rpx;
    }

    .spec-options {
      display: flex;
      flex-wrap: wrap;
      gap: 16rpx;
    }

    .spec-option {
      padding: 16rpx 24rpx;
      border: 2rpx solid #e0e0e0;
      border-radius: 8rpx;
      background: #fff;

      &.selected {
        border-color: #647D00;
        background: #f0f8e8;

        .spec-option-text {
          color: #647D00;
          font-weight: 500;
        }
      }

      &.disabled {
        border-color: #f0f0f0;
        background: #f8f8f8;

        .spec-option-text {
          color: #ccc;
        }
      }

      .spec-option-text {
        font-size: 28rpx;
        color: #333;
      }
    }
  }
}

.quantity-section {
  margin-bottom: 32rpx;

  .quantity-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 12rpx;
  }

  .quantity-label {
    font-size: 28rpx;
    color: #666;
    flex-shrink: 0;
  }

  .quantity-control {
    display: flex;
    align-items: center;

    .quantity-btn {
      width: 64rpx;
      height: 64rpx;
      border: 2rpx solid #e0e0e0;
      border-radius: 8rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 32rpx;
      color: #333;
      background: #fff;

      &.disabled {
        color: #ccc;
        border-color: #f0f0f0;
        background: #f8f8f8;
      }
    }

    .quantity-input {
      width: 120rpx;
      height: 64rpx;
      border: 2rpx solid #e0e0e0;
      border-left: none;
      border-right: none;
      text-align: center;
      font-size: 28rpx;
      color: #333;
    }
  }

  .quantity-limit {
    .text {
      font-size: 24rpx;
      color: #999;
    }
  }
}

.error-message {
  padding: 16rpx;
  background: #fff2f0;
  border-radius: 8rpx;
  border-left: 6rpx solid #ff4d4f;

  .error-text {
    font-size: 24rpx;
    color: #ff4d4f;
  }
}
</style>