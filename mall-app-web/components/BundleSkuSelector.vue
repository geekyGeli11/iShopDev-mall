<template>
	<view class="bundle-sku-selector" v-if="visible">
		<view class="mask" @tap="handleClose"></view>
		<view class="selector-content">
			<view class="selector-header">
				<text class="header-title">选择规格</text>
				<view class="close-btn" @tap="handleClose">
					<text class="close-icon">×</text>
				</view>
			</view>
			
			<scroll-view scroll-y class="selector-body">
				<!-- 遍历组合内每个商品 -->
				<view class="product-sku-item" v-for="(product, pIndex) in bundle.products" :key="pIndex">
					<view class="product-header">
						<image class="product-pic" :src="product.productPic" mode="aspectFill"></image>
						<view class="product-info">
							<text class="product-name">{{ product.productName }}</text>
							<text class="product-quantity">x{{ product.quantity }}</text>
						</view>
					</view>
					
					<!-- SKU选择 -->
					<view class="sku-options" v-if="product.skuList && product.skuList.length > 0">
						<view class="sku-label">请选择规格：</view>
						<view class="sku-list">
							<view 
								class="sku-item" 
								v-for="(sku, sIndex) in product.skuList" 
								:key="sIndex"
								:class="{
									'selected': isSkuSelected(product.productId, sku.id),
									'disabled': sku.stock <= 0
								}"
								@tap="selectSku(product.productId, sku)">
								<text class="sku-text">{{ formatSkuSpec(sku.spData) }}</text>
								<text class="sku-stock" v-if="sku.stock <= 0">缺货</text>
							</view>
						</view>
						<view class="selected-sku-info" v-if="getSelectedSku(product.productId)">
							<text class="sku-price">¥{{ getSelectedSku(product.productId).price }}</text>
						</view>
					</view>
					<view class="no-sku-tip" v-else>
						<text>该商品无需选择规格</text>
					</view>
				</view>
			</scroll-view>
			
			<!-- 底部汇总 -->
			<view class="selector-footer">
				<view class="summary">
					<text class="summary-label">组合价：</text>
					<text class="summary-price">¥{{ bundle.bundlePrice }}</text>
					<text class="summary-saved">省¥{{ bundle.savedAmount }}</text>
				</view>
				<view class="confirm-btn" :class="{ disabled: !canConfirm }" @tap="handleConfirm">
					<text>{{ confirmBtnText }}</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	name: 'BundleSkuSelector',
	props: {
		visible: {
			type: Boolean,
			default: false
		},
		bundle: {
			type: Object,
			default: () => ({})
		}
	},
	data() {
		return {
			// 存储每个商品选中的SKU { productId: skuId }
			selectedSkus: {}
		}
	},
	computed: {
		// 是否可以确认
		canConfirm() {
			if (!this.bundle.products || this.bundle.products.length === 0) {
				return false
			}
			// 检查每个商品是否都选择了SKU
			for (let product of this.bundle.products) {
				if (product.skuList && product.skuList.length > 0) {
					if (!this.selectedSkus[product.productId]) {
						return false
					}
					// 检查选中的SKU是否有库存
					const selectedSku = this.getSelectedSku(product.productId)
					if (!selectedSku || selectedSku.stock <= 0) {
						return false
					}
				}
			}
			return true
		},
		// 确认按钮文字
		confirmBtnText() {
			if (!this.bundle.products || this.bundle.products.length === 0) {
				return '加载中...'
			}
			if (!this.canConfirm) {
				return '请选择规格'
			}
			return '确定'
		}
	},
	methods: {
		handleClose() {
			this.$emit('close')
		},
		// 选择SKU
		selectSku(productId, sku) {
			if (sku.stock <= 0) {
				uni.showToast({
					title: '该规格暂无库存',
					icon: 'none'
				})
				return
			}
			this.$set(this.selectedSkus, productId, sku.id)
		},
		// 判断SKU是否被选中
		isSkuSelected(productId, skuId) {
			return this.selectedSkus[productId] === skuId
		},
		// 获取选中的SKU对象
		getSelectedSku(productId) {
			const skuId = this.selectedSkus[productId]
			if (!skuId) return null
			const product = this.bundle.products.find(p => p.productId === productId)
			if (!product || !product.skuList) return null
			return product.skuList.find(s => s.id === skuId)
		},
		// 格式化SKU规格显示
		formatSkuSpec(spData) {
			if (!spData) return '默认'
			try {
				const specs = JSON.parse(spData)
				if (Array.isArray(specs)) {
					return specs.map(s => s.value).join(' / ')
				}
				return '默认'
			} catch (e) {
				return spData
			}
		},
		// 确认选择
		handleConfirm() {
			if (!this.canConfirm) {
				return
			}
			// 构建SKU选择结果（每个商品都必须有SKU）
			const skuSelections = []
			for (let product of this.bundle.products) {
				const skuId = this.selectedSkus[product.productId]
				if (skuId) {
					skuSelections.push({
						productId: product.productId,
						skuId: skuId
					})
				}
			}
			console.log('SKU选择结果:', skuSelections)
			this.$emit('confirm', skuSelections)
		}
	},
	watch: {
		visible(val) {
			if (val) {
				// 重置选择
				this.selectedSkus = {}
				// 自动选择只有一个SKU的商品
				if (this.bundle.products) {
					for (let product of this.bundle.products) {
						if (product.skuList && product.skuList.length === 1 && product.skuList[0].stock > 0) {
							this.$set(this.selectedSkus, product.productId, product.skuList[0].id)
						}
					}
				}
			}
		}
	}
}
</script>
<style lang="scss" scoped>
.bundle-sku-selector {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	z-index: 999;
}

.mask {
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(0, 0, 0, 0.5);
}

.selector-content {
	position: absolute;
	left: 0;
	right: 0;
	bottom: 0;
	background: #fff;
	border-radius: 24rpx 24rpx 0 0;
	max-height: 80vh;
	display: flex;
	flex-direction: column;
}

.selector-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 30rpx;
	border-bottom: 1rpx solid #f5f5f5;
}

.header-title {
	font-size: 32rpx;
	font-weight: 500;
	color: #333;
}

.close-btn {
	width: 60rpx;
	height: 60rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.close-icon {
	font-size: 48rpx;
	color: #999;
}

.selector-body {
	flex: 1;
	padding: 0 30rpx;
	max-height: 60vh;
}

.product-sku-item {
	padding: 30rpx 0;
	border-bottom: 1rpx solid #f5f5f5;
}

.product-sku-item:last-child {
	border-bottom: none;
}

.product-header {
	display: flex;
	align-items: center;
	margin-bottom: 20rpx;
}

.product-pic {
	width: 100rpx;
	height: 100rpx;
	border-radius: 12rpx;
	flex-shrink: 0;
}

.product-info {
	flex: 1;
	margin-left: 20rpx;
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.product-name {
	font-size: 28rpx;
	color: #333;
	flex: 1;
}

.product-quantity {
	font-size: 26rpx;
	color: #999;
}

.sku-options {
	margin-top: 16rpx;
}

.sku-label {
	font-size: 28rpx;
	color: #666;
	margin-bottom: 16rpx;
}

.sku-list {
	display: flex;
	flex-wrap: wrap;
	gap: 16rpx;
}

.sku-item {
	padding: 16rpx 24rpx;
	border: 2rpx solid #e0e0e0;
	border-radius: 8rpx;
	background: #fff;
	display: flex;
	align-items: center;
}

.sku-item.selected {
	border-color: #647D00 !important;
	background: #f0f8e8 !important;
}

.sku-item.selected .sku-text {
	color: #647D00 !important;
	font-weight: 500;
}

.sku-item.disabled {
	border-color: #f0f0f0;
	background: #f8f8f8;
}

.sku-item.disabled .sku-text {
	color: #ccc;
}

.sku-text {
	font-size: 28rpx;
	color: #333;
}

.sku-stock {
	font-size: 22rpx;
	color: #999;
	margin-left: 8rpx;
}

.selected-sku-info {
	margin-top: 16rpx;
	display: flex;
	align-items: center;
}

.sku-price {
	font-size: 28rpx;
	color: #647D00 !important;
	font-weight: bold;
}

.no-sku-tip {
	font-size: 26rpx;
	color: #999;
	padding: 16rpx 0;
}

.selector-footer {
	padding: 20rpx 30rpx;
	border-top: 1rpx solid #f5f5f5;
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
	flex-wrap: nowrap;
}

.summary {
	display: flex;
	align-items: baseline;
	flex-shrink: 0;
}

.summary-label {
	font-size: 24rpx;
	color: #666;
	white-space: nowrap;
}

.summary-price {
	font-size: 32rpx;
	color: #647D00 !important;
	font-weight: bold;
	white-space: nowrap;
}

.summary-saved {
	font-size: 22rpx;
	color: #999;
	margin-left: 12rpx;
	white-space: nowrap;
}

.confirm-btn {
	padding: 0 40rpx;
	height: 76rpx;
	background: #20201E !important;
	border-radius: 16rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	flex-shrink: 0;
	min-width: 180rpx;
}

.confirm-btn text {
	color: #A9FF00 !important;
	font-size: 28rpx;
	font-weight: 400;
	white-space: nowrap;
}

.confirm-btn.disabled {
	background: #ccc !important;
}

.confirm-btn.disabled text {
	color: #fff !important;
}
</style>
