<template>
	<view class="recommended-section" :class="{ 'position-bottom': positionBottom }">
		<view class="tab-header">
			<view class="tab-container">
				<view 
					class="tab-item" 
					v-for="(item, index) in tabs" 
					:key="index"
					:class="{ active: currentProductTab === index }"
					@click="switchProductTab(index)"
				>
					<image v-if="currentProductTab === index" class="tab-indicator" src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/cart/tab-active-v.png" mode="aspectFit"></image>
					<text class="tab-text">{{ item }}</text>
				</view>
			</view>
		</view>
		
		<!-- 推荐商品 -->
		<scroll-view v-if="currentProductTab === 0" class="product-scroll" scroll-x show-scrollbar="false">
			<view class="product-row">
				<view class="product-item" v-for="(product, index) in recommendedProducts" :key="index" 
					@click="handleToProduct(product.productId)">
					<view class="item-tags" v-if="index % 3 === 0">
						<view class="item-tag">秒杀</view>
					</view>
					<view class="item-tags" v-if="index % 3 === 1">
						<view class="item-tag new">新品</view>
						<view class="item-tag recommend">推荐</view>
					</view>
					<view class="item-tags" v-if="index % 3 === 2">
						<view class="item-tag hot">热卖</view>
					</view>
					<image class="item-image" :src="product.cover || 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/index/product-' + ((index % 3) + 1) + '.jpg'" mode="aspectFill"></image>
					<view class="item-info">
						<text class="item-title">{{ product.productName || '潮汕味道潮汕三宝礼盒装' }}</text>
						<view class="item-price-row">
							<text class="item-price">¥ <text class="price-number">{{ product.price || defaultPrice[index % 6] }}</text> 起/份</text>
							<view class="item-add" @click.stop="addToCart(product.productId)"><text>+</text></view>
						</view>
					</view>
				</view>
			</view>
		</scroll-view>
		
		<!-- 浏览历史 -->
		<scroll-view v-if="currentProductTab === 1" class="product-scroll" scroll-x show-scrollbar="false">
			<view class="product-row">
				<view class="product-item" v-for="(product, index) in historyProducts" :key="index" 
					@click="handleToProduct(product.productId)">
					<view class="item-tags" v-if="index % 3 === 0">
						<view class="item-tag hot">热卖</view>
					</view>
					<image class="item-image" :src="product.productPic || 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/index/product-' + ((index % 3) + 1) + '.jpg'" mode="aspectFill"></image>
					<view class="item-info">
						<text class="item-title">{{ product.productName || '潮汕味道潮汕三宝礼盒装' }}</text>
						<view class="item-price-row">
							<text class="item-price">¥ <text class="price-number">{{ product.productPrice || defaultPrice[index % 6] }}</text> 起/份</text>
							<view class="item-add" @click.stop="addToCart(product.productId)"><text>+</text></view>
						</view>
					</view>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	import { 
		fetchHotProductList 
	} from '@/api/home.js';
	import {
		fetchReadHistoryList
	} from '@/api/memberReadHistory.js';
	import { mapState } from 'vuex';
	
	export default {
		name: 'RecommendedProducts',
		props: {
			// 是否位于底部
			positionBottom: {
				type: Boolean,
				default: false
			},
			// 自定义添加按钮点击事件文本
			addButtonText: {
				type: String,
				default: '已添加到购物车'
			}
		},
		data() {
			return {
				tabs: ['推荐商品'],
				currentProductTab: 0,
				recommendedProducts: [],
				historyProducts: [],
				defaultPrice: [112, 125, 98, 199, 80, 168], // 默认价格数组，仅用于数据加载前
				loading: false,
				initialized: false
			}
		},
		computed: {
			...mapState(['hasLogin'])
		},
		mounted() {
			this.$nextTick(this.initData);
		},
		methods: {
			// 初始化数据，只执行一次
			initData() {
				if (this.initialized) return;
				this.initialized = true;
				
				// 获取推荐商品
				this.getRecommendedProducts();
				
				// 如果用户已登录，添加浏览历史选项卡并获取浏览历史
				if (this.hasLogin) {
					this.tabs.push('浏览历史');
					this.getHistoryProducts();
				}
			},
			
			// 切换推荐商品选项卡
			switchProductTab(index) {
				if (this.currentProductTab === index) return;
				this.currentProductTab = index;
			},
			
			// 获取推荐商品
			getRecommendedProducts() {
				// 防止重复加载
				if (this.loading || this.recommendedProducts.length > 0) return;
				
				this.loading = true;
				// 显示加载中
				uni.showLoading({
					title: '加载中...'
				});
				
				// 立即设置一些模拟数据，避免等待空白
				this.setTemporaryData();
				
				// 从API获取数据
				fetchHotProductList().then(res => {
					if (res.code === 200 && res.data) {
						// 直接使用返回的data数组
						this.recommendedProducts = res.data;
						console.log('获取到推荐商品:', this.recommendedProducts.length, '个');
					} else {
						console.log('推荐商品API响应格式不正确:', res);
					}
				}).catch(err => {
					console.error('获取推荐商品失败', err);
				}).finally(() => {
					this.loading = false;
					uni.hideLoading();
				});
			},
			
			// 获取浏览历史
			getHistoryProducts() {
				// 如果用户未登录，不执行请求
				if (!this.hasLogin) return;
				
				// 从API获取数据
				fetchReadHistoryList().then(res => {
					if (res.code === 200 && res.data && res.data.list) {
						// 使用返回的data.list数组
						this.historyProducts = res.data.list;
						console.log('获取到浏览历史:', this.historyProducts.length, '个');
					} else {
						console.log('浏览历史API响应格式不正确:', res);
					}
				}).catch(err => {
					console.error('获取浏览历史失败', err);
				});
			},
			
			// 设置临时数据，避免加载时空白
			setTemporaryData() {
				if (this.recommendedProducts.length === 0) {
					this.recommendedProducts = Array(6).fill().map((_, index) => {
						return {
							productId: 1000 + index,
							productName: '潮汕味道潮汕三宝礼盒装',
							price: this.defaultPrice[index % 6],
							cover: 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/index/product-' + ((index % 3) + 1) + '.jpg'
						}
					});
				}
			},
			
			// 跳转到商品详情页
			handleToProduct(productId) {
				uni.navigateTo({
					url: `/pages/product/product?id=${productId}`
				});
			},
			
			// 添加到购物车/礼袋
			addToCart(productId) {
				uni.navigateTo({
					url: `/pages/product/product?id=${productId}`
				});
			}
		}
	}
</script>

<style lang="scss">
	/* 推荐商品区域 */
	.recommended-section {
		margin-bottom: 30rpx;
		background-color: #fff;
		box-sizing: border-box;
		min-height: 550rpx;
		width: 100%;
		
		&.position-bottom {
			position: fixed;
			bottom: 100rpx;
			left: 0;
			margin-bottom: 120rpx;
			z-index: 10;
			height: 470rpx;
			will-change: transform; /* 优化性能 */
			transform: translateZ(0); /* 启用GPU加速 */
		}
	}
	
	.tab-header {
		background-color: #fff;
		padding: 10rpx 10rpx 0;
	}
	
	.tab-container {
		display: flex;
		border-bottom: 1px solid #f2f2f2;
	}
	
	.tab-item {
		position: relative;
		padding: 0 20rpx 15rpx;
		margin-right: 30rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
	}
	
	.tab-text {
		font-size: 28rpx;
		color: #666;
		position: relative;
		z-index: 2;
	}
	
	.tab-item.active .tab-text {
		color: #333;
		font-weight: 500;
	}
	
	.tab-indicator {
		position: absolute;
		bottom: 5rpx;
		left: 50%;
		transform: translateX(-50%);
		width: 64rpx;
		height: 20rpx;
	}
	
	.product-scroll {
		padding: 15rpx 0;
		white-space: nowrap;
		height: 430rpx;
		overflow-x: auto;
		-webkit-overflow-scrolling: touch;
	}
	
	.product-row {
		padding: 0 20rpx;
		display: inline-flex;
		flex-wrap: nowrap;
		width: auto;
	}
	
	.product-item {
		position: relative;
		width: 250rpx;
		margin-right: 15rpx;
		background: linear-gradient(180deg, #F7FCFE 67.56%, #EDF9FD 100%);
		border-radius: 12rpx;
		overflow: visible; /* 允许标签超出容器 */
		display: inline-block;
		flex-shrink: 0;
	}
	
	.item-tags {
		position: absolute;
		top: 0;
		left: -10px; /* 向左偏移，与首页保持一致 */
		display: flex;
		flex-direction: column; /* 保持垂直布局 */
		z-index: 10; /* 增加z-index确保显示在最上层 */
	}
	
	.item-tag {
		position: relative;
		padding: 2px 6px;
		color: #000000;
		font-size: 10px;
		font-weight: 500;
		margin-bottom: 2px; /* 标签之间的间距 */
		font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
		height: 32px; /* 固定高度与首页一致 */
		width: 32px; /* 固定宽度与首页一致 */
		display: flex;
		align-items: center;
		justify-content: center;
		
		&::before {
			content: '';
			position: absolute;
			top: 0;
			left: 0;
			width: 100%;
			height: 100%;
			background-image: url('https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/product_card/tag_bg.png');
			background-size: 100% 100%;
			z-index: -1;
		}
		
		&.new, &.recommend, &.hot {
			&::before {
				background-image: url('https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/product_card/tag_bg.png');
			}
		}
	}
	
	.item-image {
		width: 250rpx;
		height: 250rpx;
		background-color: #f5f5f5;
		display: block;
		border-top-left-radius: 12rpx; /* 添加与卡片一致的上方圆角 */
		border-top-right-radius: 12rpx; /* 添加与卡片一致的上方圆角 */
		overflow: hidden; /* 确保图片不会溢出 */
	}
	
	.item-info {
		padding: 16rpx;
		overflow: hidden; /* 确保内容不会溢出 */
	}
	
	.item-title {
		font-size: 24rpx;
		color: #333;
		white-space: normal;
		word-break: break-all;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 2;
		overflow: hidden;
		text-overflow: ellipsis;
		line-height: 32rpx;
		height: 64rpx;
		font-weight: 500;
		color: #000000;
	}
	
	.item-price-row {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-top: 10rpx;
	}
	
	.item-price {
		font-size: 24rpx;
		color: #000000;
	}
	
	.price-number {
		font-weight: 500;
		font-size: 32rpx;
	}
	
	.item-add {
		width: 48rpx;
		height: 48rpx;
		background-color: #D3FB51;
		border-radius: 24rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 30rpx;
		font-weight: 350;
		color: #000000;
		line-height: 1; /* 确保文本垂直居中 */
		text-align: center; /* 水平居中文本 */
		
		text {
			display: inline-block;
			transform: translateY(-1rpx); /* 微调垂直位置 */
		}
	}
</style> 