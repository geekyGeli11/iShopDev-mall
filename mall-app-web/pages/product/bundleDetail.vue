<template>
	<view class="container">
		<nav-bar :back="true" :placeholder="false" bgColor="transparent"></nav-bar>
		
		<!-- 轮播图 -->
		<view class="carousel">
			<swiper indicator-dots="true" circular="true" duration="400">
				<swiper-item v-for="(item, index) in albumPics" :key="index" class="swiper-item">
					<view class="image-wrapper" @tap="previewImage(index)">
						<image class="loaded" :src="item" mode="aspectFill"></image>
					</view>
				</swiper-item>
			</swiper>
			<!-- 组合标签 -->
			<view class="bundle-tag">
				<text>组合优惠</text>
			</view>
		</view>

		<!-- 组合信息 -->
		<view class="introduce-section">
			<text class="title">{{ bundle.name }}</text>
			<view class="price-box">
				<view class="price-container">
					<view class="price-left">
						<view class="buy-price-tag">
							<text class="buy-price-label">组合价</text>
							<view class="price-row">
								<view class="current-price-container">
									<text class="buy-price-symbol">¥</text>
									<text class="buy-price">{{ bundle.bundlePrice }}</text>
								</view>
								<text class="original-price">¥{{ bundle.originalPrice }}</text>
							</view>
						</view>
						<text class="buy-price-desc">购买组合立省 ¥{{ bundle.savedAmount }}</text>
					</view>
				</view>
			</view>
			<view class="bundle-desc" v-if="bundle.description">
				<text>{{ bundle.description }}</text>
			</view>
		</view>

		<!-- 组合内商品列表 -->
		<view class="products-section">
			<view class="section-header">
				<text class="section-title">组合包含 {{ bundle.products ? bundle.products.length : 0 }} 件商品</text>
			</view>
			<view class="product-list">
				<view class="product-item" v-for="(item, index) in bundle.products" :key="index">
					<image class="product-pic" :src="item.productPic" mode="aspectFill"></image>
					<view class="product-info">
						<text class="product-name">{{ item.productName }}</text>
						<text class="product-subtitle" v-if="item.subTitle">{{ item.subTitle }}</text>
						<view class="product-price-row">
							<text class="product-price">¥{{ item.price }}</text>
							<text class="product-quantity">x{{ item.quantity }}</text>
						</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 组合详情 -->
		<view class="detail-desc" v-if="bundle.detailDesc">
			<view class="d-header">
				<text>组合详情</text>
			</view>
			<rich-text :nodes="bundle.detailDesc"></rich-text>
		</view>

		<!-- 底部操作栏 -->
		<view class="page-bottom">
			<view class="bottom-item" @tap="contactService">
				<image class="bottom-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/product/kefu.png"></image>
				<text class="bottom-text">客服</text>
			</view>
			<view class="button-group">
				<view class="group-button buy-button" @tap="showSkuSelector">
					<text class="button-text">立即购买</text>
				</view>
			</view>
		</view>

		<!-- SKU选择器弹窗 -->
		<bundle-sku-selector
			v-if="showSelector"
			:visible="showSelector"
			:bundle="bundle"
			@close="showSelector = false"
			@confirm="handleSkuConfirm">
		</bundle-sku-selector>
	</view>
</template>

<script>
import { fetchBundleDetail } from '@/api/bundle'
import BundleSkuSelector from '@/components/BundleSkuSelector.vue'
import navBar from '@/components/nav-bar'

export default {
	components: {
		BundleSkuSelector,
		'nav-bar': navBar
	},
	data() {
		return {
			bundleId: null,
			storeId: null,
			bundle: {},
			showSelector: false
		}
	},
	computed: {
		albumPics() {
			if (this.bundle.albumPics && this.bundle.albumPics.length > 0) {
				return this.bundle.albumPics
			}
			return this.bundle.pic ? [this.bundle.pic] : []
		}
	},
	onLoad(options) {
		this.bundleId = options.id
		this.storeId = options.storeId || null
		this.loadBundleDetail()
	},
	methods: {
		loadBundleDetail() {
			fetchBundleDetail(this.bundleId, this.storeId).then(res => {
				if (res.code === 200) {
					this.bundle = res.data
				} else {
					uni.showToast({
						title: res.message || '加载失败',
						icon: 'none'
					})
				}
			}).catch(err => {
				uni.showToast({
					title: '网络错误',
					icon: 'none'
				})
			})
		},
		previewImage(index) {
			uni.previewImage({
				current: index,
				urls: this.albumPics
			})
		},
		contactService() {
			// 联系客服
			console.log('contactService 方法被调用');
			try {
				uni.navigateTo({
					url: '/pages/customerService/index',
					fail: (error) => {
						console.error('跳转失败:', error);
						uni.showToast({
							title: '跳转失败',
							icon: 'none'
						});
					}
				});
			} catch (error) {
				console.error('跳转客服页面出错:', error);
				uni.showToast({
					title: '跳转失败',
					icon: 'none'
				});
			}
		},
		showSkuSelector() {
			if (!this.bundle.products || this.bundle.products.length === 0) {
				uni.showToast({
					title: '组合商品信息加载中',
					icon: 'none'
				})
				return
			}
			this.showSelector = true
		},
		handleSkuConfirm(skuSelections) {
			this.showSelector = false
			// 跳转到订单确认页
			const orderData = {
				bundleId: this.bundleId,
				quantity: 1,
				skuSelections: skuSelections,
				storeId: this.storeId
			}
			console.log('跳转订单确认页，数据:', JSON.stringify(orderData))
			const url = '/pages/order/createOrder?type=bundle&data=' + encodeURIComponent(JSON.stringify(orderData))
			console.log('跳转URL:', url)
			uni.navigateTo({
				url: url,
				success: () => {
					console.log('跳转订单确认页成功')
				},
				fail: (error) => {
					console.error('跳转订单确认页失败:', error)
					uni.showToast({
						title: '跳转失败: ' + (error.errMsg || '未知错误'),
						icon: 'none'
					})
				}
			})
		}
	}
}
</script>
<style lang="scss" scoped>
.container {
	padding-bottom: calc(140rpx + env(safe-area-inset-bottom));
}

.carousel {
	position: relative;
	width: 100%;
	height: 750rpx;
	
	swiper {
		width: 100%;
		height: 100%;
	}
	
	.swiper-item {
		width: 100%;
		height: 100%;
	}
	
	.image-wrapper {
		width: 100%;
		height: 100%;
		
		image {
			width: 100%;
			height: 100%;
		}
	}
	
	.bundle-tag {
		position: absolute;
		top: 20rpx;
		left: 20rpx;
		background: linear-gradient(135deg, #ff6b6b, #ff8e53);
		color: #fff;
		padding: 8rpx 20rpx;
		border-radius: 20rpx;
		font-size: 24rpx;
	}
}

.introduce-section {
	background: #fff;
	padding: 30rpx;
	margin-bottom: 20rpx;
	
	.title {
		font-size: 34rpx;
		font-weight: bold;
		color: #333;
		line-height: 1.4;
	}
	
	.price-box {
		margin-top: 20rpx;
		
		.price-container {
			display: flex;
			align-items: flex-end;
		}
		
		.price-left {
			.buy-price-tag {
				display: flex;
				align-items: center;
				
				.buy-price-label {
					background: #647D00;
					color: #fff;
					padding: 4rpx 12rpx;
					border-radius: 8rpx;
					font-size: 22rpx;
					margin-right: 16rpx;
				}
				
				.price-row {
					display: flex;
					align-items: baseline;
					
					.current-price-container {
						display: flex;
						align-items: baseline;
						
						.buy-price-symbol {
							font-size: 28rpx;
							color: #647D00;
							font-weight: bold;
						}
						
						.buy-price {
							font-size: 48rpx;
							color: #647D00;
							font-weight: bold;
						}
					}
					
					.original-price {
						font-size: 26rpx;
						color: #999;
						text-decoration: line-through;
						margin-left: 16rpx;
					}
				}
			}
			
			.buy-price-desc {
				font-size: 24rpx;
				color: #999999;
				margin-top: 8rpx;
			}
		}
	}
	
	.bundle-desc {
		margin-top: 20rpx;
		font-size: 26rpx;
		color: #666;
		line-height: 1.5;
	}
}

.products-section {
	background: #fff;
	padding: 30rpx;
	margin-bottom: 20rpx;
	
	.section-header {
		margin-bottom: 20rpx;
		
		.section-title {
			font-size: 30rpx;
			font-weight: bold;
			color: #333;
		}
	}
	
	.product-list {
		.product-item {
			display: flex;
			padding: 20rpx 0;
			border-bottom: 1rpx solid #f5f5f5;
			
			&:last-child {
				border-bottom: none;
			}
			
			.product-pic {
				width: 160rpx;
				height: 160rpx;
				border-radius: 12rpx;
				flex-shrink: 0;
			}
			
			.product-info {
				flex: 1;
				margin-left: 20rpx;
				display: flex;
				flex-direction: column;
				justify-content: space-between;
				
				.product-name {
					font-size: 28rpx;
					color: #333;
					line-height: 1.4;
				}
				
				.product-subtitle {
					font-size: 24rpx;
					color: #999;
					margin-top: 8rpx;
				}
				
				.product-price-row {
					display: flex;
					justify-content: space-between;
					align-items: center;
					margin-top: 16rpx;
					
					.product-price {
						font-size: 28rpx;
						color: #647D00;
						font-weight: bold;
					}
					
					.product-quantity {
						font-size: 26rpx;
						color: #999;
					}
				}
			}
		}
	}
}

.detail-desc {
	background: #fff;
	padding: 30rpx;
	
	.d-header {
		font-size: 30rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 20rpx;
		padding-bottom: 20rpx;
		border-bottom: 1rpx solid #f5f5f5;
	}
}

.page-bottom {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	background: #fff;
	display: flex;
	align-items: center;
	padding: 16rpx 20rpx;
	padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
	box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
	border-top: 0.5px solid #EBEBEB;
	z-index: 95;
	
	.bottom-item {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		width: 100rpx;
		flex-shrink: 0;
		
		.bottom-icon {
			width: 44rpx;
			height: 44rpx;
			margin-bottom: 4rpx;
		}
		
		.bottom-text {
			font-size: 20rpx;
			color: #666666;
		}
	}
	
	.button-group {
		flex: 1;
		display: flex;
		justify-content: flex-end;
		height: 84rpx;
		margin-left: 20rpx;
		
		.group-button {
			padding: 0 60rpx;
			height: 100%;
			border-radius: 16rpx;
			display: flex;
			align-items: center;
			justify-content: center;
			
			&.buy-button {
				background: #20201E;
				border: 1px solid #EEEEEE;
				flex: 1;
				
				.button-text {
					color: #A9FF00;
					font-size: 32rpx;
					font-weight: 400;
				}
			}
		}
	}
}
</style>
