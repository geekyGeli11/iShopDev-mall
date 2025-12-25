<template>
	<view class="product-list-container">
		<!-- 导航栏 -->
		<nav-bar 
			:back="true" 
			:placeholder="false"
			bg-color="transparent" 
		/>
		
		<!-- 页面加载状态 -->
		<view v-if="pageLoading" class="page-loading">
			<view class="loading-spinner"></view>
			<text class="loading-text">正在加载商品...</text>
		</view>
		
		<!-- 商品列表内容 -->
		<scroll-view 
			v-else
			class="scroll-view" 
			scroll-y="true" 
			@scrolltolower="handleScrollToBottom"
			:refresher-enabled="true"
			:refresher-triggered="refreshing"
			@refresherrefresh="onRefresh"
		>
			<view class="product-list">
				<view 
					v-for="(item, index) in productList" 
					:key="item.id" 
					class="product-card"
					@tap="navigateToProduct(item)"
				>
					<!-- 商品图片 -->
					<view class="product-image-container" @tap="navigateToProduct(item)">
						<image 
							:src="item.pic || 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/default.png'" 
							class="product-image"
							mode="aspectFill"
							:lazy-load="true"
							:style="item.stock <= 0 ? 'filter: grayscale(100%);' : ''"
						/>
						<!-- 售罄蒙版 -->
						<view class="sold-out-mask" v-if="item.stock <= 0">
							<text class="sold-out-text">已售罄</text>
						</view>
					</view>
					
					<!-- 商品信息和操作栏 -->
					<view class="product-content">
						<!-- 商品标题和查看详情 -->
						<view class="product-header">
							<text class="product-name">{{ item.name }}</text>
							<view class="view-detail" @tap="navigateToProduct(item)">
								<text class="detail-text">详情</text>
								<text class="detail-arrow">></text>
							</view>
						</view>
						
						<!-- 价格和销量信息 -->
						<view class="product-info">
							<view class="price-sale-row">
								<view class="price-info">
									<text class="price-symbol">¥</text>
									<text class="price-value">{{ item.price || '0.00' }}</text>
								</view>
								<view class="sale-info">
									<text class="sale-text">已售 {{ item.sale || 0 }}</text>
								</view>
							</view>
						</view>
						
						<!-- 底部操作栏 -->
						<view class="product-bottom">
							<view class="left-items">
								<view class="bottom-item">
									<image class="bottom-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/product/kefu.png"/>
									<text class="bottom-text">客服</text>
								</view>
								
								<view class="bottom-item">
									<image class="bottom-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/product/favorite.png"/>
									<text class="bottom-text">{{ item.collectCount || 0 }}</text>
								</view>
							</view>
							
							<!-- 按钮组 -->
							<view class="button-group">
								<view class="group-button cart-button">
									<image class="button-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/product/cart.png"/>
								</view>
								<view class="group-button buy-button">
									<text class="button-text">立即购买</text>
								</view>
							</view>
						</view>
					</view>
				</view>
			</view>
			
			<!-- 加载更多提示 -->
			<view class="load-more" v-if="productList.length > 0">
				<uni-load-more 
					v-if="loading || !hasMoreData" 
					:status="loadingStatus"
					:content-text="loadMoreText"
				/>
			</view>
		</scroll-view>
		
		<!-- 空状态 -->
		<view v-if="!pageLoading && productList.length === 0 && !loading && !refreshing" class="empty-state">
			<view class="empty-icon">🛍️</view>
			<text class="empty-text">暂无商品</text>
		</view>
	</view>
</template>

<script>
import navBar from '@/components/nav-bar';
import uniLoadMore from '@/components/uni-load-more/uni-load-more.vue';
import { fetchProductListBySales } from '@/api/home.js';

export default {
	name: 'ProductList',
	components: {
		navBar,
		uniLoadMore
	},
			data() {
		return {
			productList: [],
			currentPage: 1,
			pageSize: 5,
			loading: false,
			loadingStatus: 'more', // more, loading, noMore
			noMore: false,
			refreshing: false,
			scrollTop: 0,
			loadMoreText: {
				contentdown: '上拉显示更多',
				contentrefresh: '正在加载...',
				contentnomore: '没有更多商品了'
			},
			hasMoreData: true,
			pageLoading: true,
			selectedProduct: null // 存储从瀑布流选中的商品
		};
	},

	onLoad(options) {
		// 接收从瀑布流传递的商品信息
		if (options.selectedProduct) {
			try {
				this.selectedProduct = JSON.parse(decodeURIComponent(options.selectedProduct));
			} catch (error) {
				console.error('解析商品参数失败:', error);
				this.selectedProduct = null;
			}
		}
		this.loadProductData(true);
	},
	onPullDownRefresh() {
		this.onRefresh();
	},
	methods: {
		// 处理滚动到底部
		handleScrollToBottom() {
			if (this.hasMoreData && !this.loading) {
				this.loadingStatus = 'loading';
				this.loadProductData();
			}
		},

		// 加载商品数据
		async loadProductData(reset = false) {
			if (this.loading) return;
			
			this.loading = true;
			if (reset) {
				this.currentPage = 1;
				this.productList = [];
			}
			
			try {
				const params = {
					pageNum: this.currentPage,
					pageSize: this.pageSize
				};

				// 添加学校ID参数
				const schoolInfo = uni.getStorageSync('selectedSchool');
				if (schoolInfo) {
					try {
						const school = typeof schoolInfo === 'string' ? JSON.parse(schoolInfo) : schoolInfo;
						if (school && school.id) {
							params.schoolId = school.id;
						}
					} catch (error) {
						console.error('解析学校信息失败:', error);
					}
				}

				const response = await fetchProductListBySales(params);
				
				if (response && response.data && Array.isArray(response.data)) {
					const newProducts = response.data;
					
					// 如果是第一次加载且有选中的商品，将其作为第一个商品
					if (reset && this.selectedProduct) {
						// 过滤掉与选中商品ID相同的商品，避免重复
						const filteredProducts = newProducts.filter(product => product.id != this.selectedProduct.id);
						this.productList = [this.selectedProduct, ...filteredProducts];
					} else {
						this.productList = reset ? newProducts : [...this.productList, ...newProducts];
					}
					
					// 如果没有更多数据
					if (newProducts.length < this.pageSize) {
						this.hasMoreData = false;
						this.loadingStatus = 'noMore';
					} else {
						this.hasMoreData = true;
						this.loadingStatus = 'more';
					}
					
					this.currentPage++;
					
					// 如果是第一次加载，结束页面加载状态
					if (reset && this.pageLoading) {
						this.pageLoading = false;
					}
				}
			} catch (error) {
				console.error('加载商品数据失败:', error);
				uni.showToast({
					title: '加载失败，请重试',
					icon: 'none'
				});
				
				// 加载失败也要结束页面加载状态
				if (reset && this.pageLoading) {
					this.pageLoading = false;
				}
			} finally {
				this.loading = false;
				this.refreshing = false;
			}
		},

		
		// 下拉刷新
		onRefresh() {
			this.refreshing = true;
			this.loadProductData(true);
		},
		
		// 跳转到商品详情
		navigateToProduct(product) {
			uni.navigateTo({
				url: `/pages/product/product?id=${product.id}`
			});
		}
	}
};
</script>

<style lang="scss" scoped>
.product-list-container {
	background-color: #000000;
	min-height: 100vh;
}

.scroll-view {
	height: 100vh;
	padding-top: 20rpx;
}

.product-list {
	padding: 0;
}

.product-card {
	background-color: #ffffff;
	margin-bottom: 20rpx;
	overflow: hidden;
	border-radius: 16rpx;
}

.product-image-container {
	width: 100%;
	height: 0;
	padding-bottom: 100%; /* 1:1 比例 */
	position: relative;
	background-color: #f8f8f8;
}

.product-image {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	display: block;
	object-fit: contain;
}

/* 售罄蒙版样式 */
.sold-out-mask {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background-color: rgba(0, 0, 0, 0.5);
	display: flex;
	align-items: center;
	justify-content: center;
	z-index: 10;
}

.sold-out-text {
	color: #ffffff;
	font-size: 48rpx;
	font-weight: bold;
	text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.3);
}

.product-content {
	padding: 30rpx;
}

.product-header {
	display: flex;
	align-items: flex-start;
	margin-bottom: 30rpx;
}

.product-name {
	font-size: 32rpx;
	font-weight: 600;
	color: #000000;
	line-height: 1.4;
	flex: 1;
	display: -webkit-box;
	-webkit-box-orient: vertical;
	-webkit-line-clamp: 2;
	overflow: hidden;
	text-overflow: ellipsis;
}

.view-detail {
	display: flex;
	align-items: center;
	flex-shrink: 0;
	margin-left: 20rpx;
	margin-top: 4rpx;
}

.detail-text {
	font-size: 28rpx;
	color: #647D00;
	margin-right: 8rpx;
}

.detail-arrow {
	font-size: 28rpx;
	color: #647D00;
	font-weight: bold;
}

.product-info {
	margin-bottom: 30rpx;
}

.price-sale-row {
	display: flex;
	align-items: center;
	justify-content: flex-start;
	gap: 24rpx;
}

.price-info {
	margin-bottom: 10rpx;
	display: flex;
	align-items: baseline;
}

.price-symbol {
	font-size: 22rpx;
	color: #647D00;
	margin-right: 2rpx;
}

.price-value {
	font-size: 32rpx;
	color: #647D00;
	font-weight: 600;
}

.sale-info {
	font-size: 24rpx;
	color: #666666;
}

.sale-text {
	margin-right: 10rpx;
}

.product-bottom {
	display: flex;
	align-items: center;
	justify-content: space-between;
}

.left-items {
	display: flex;
	align-items: center;
	gap: 40rpx;
}

.bottom-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	flex-shrink: 0;
}

.bottom-icon {
	width: 44rpx;
	height: 44rpx;
	margin-bottom: 8rpx;
}

.bottom-text {
	font-size: 24rpx;
	color: #666666;
}

.button-group {
	display: flex;
	align-items: center;
	gap: 0;
}

.group-button {
	display: flex;
	align-items: center;
	justify-content: center;
	height: 84rpx;
}

.cart-button {
	background-color: #ffffff;
	border: 2rpx solid #eeeeee;
	border-radius: 16rpx 0 0 16rpx;
	width: 122rpx;
}

.button-icon {
	width: 48rpx;
	height: 48rpx;
}

.buy-button {
	background-color: #20201E;
	border-radius: 0 16rpx 16rpx 0;
	flex: 1;
	min-width: 200rpx;
}

.button-text {
	font-size: 32rpx;
	color: #A9FF00;
	font-weight: 500;
}

.load-more {
	padding: 40rpx 0;
	text-align: center;
}

.empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 200rpx 0;
}

.empty-icon {
	font-size: 120rpx;
	margin-bottom: 40rpx;
}

.empty-text {
	font-size: 28rpx;
	color: #999999;
}

.page-loading {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 200rpx 0;
}

.loading-spinner {
	width: 80rpx;
	height: 80rpx;
	border: 4rpx solid #A9FF00;
	border-radius: 50%;
	animation: spin 1s linear infinite;
	margin-bottom: 40rpx;
}

@keyframes spin {
	0% { transform: rotate(0deg); }
	100% { transform: rotate(360deg); }
}

.loading-text {
	font-size: 28rpx;
	color: #A9FF00;
	font-weight: 500;
}


</style> 