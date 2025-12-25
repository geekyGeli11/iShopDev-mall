<template>
	<view class="container">
		<!-- 导航栏 -->
		<nav-bar :back="true" title="爆品榜单" :placeholder="true" bgColor="#FFFFFF" color="#0A0D05"></nav-bar>

		<!-- 页面加载状态 -->
		<view v-if="pageLoading" class="loading-container">
			<view class="loading-text">加载中...</view>
		</view>

		<!-- 主要内容 -->
		<scroll-view
			v-else
			class="scroll-view"
			scroll-y="true"
			@scrolltolower="handleScrollToBottom"
			:refresher-enabled="true"
			:refresher-triggered="refreshing"
			@refresherrefresh="onRefresh"
		>
			<!-- 主要产品列表 -->
			<view class="main-product-list">
				<view
					v-for="(item, index) in mainProductList"
					:key="item.id"
					class="main-product-card"
					:class="{
						'border-gold': index === 0,
						'border-silver': index === 1,
						'border-bronze': index === 2
					}"
					@tap="navigateToProduct(item)"
				>
					<!-- 前三名排名标签图片 -->
					<view v-if="index <= 2" class="rank-image-badge">
						<image
							:src="`https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/product/top${index + 1}.png`"
							class="rank-image"
							mode="aspectFit"
						/>
					</view>

					<!-- 第4、5名文字标签 -->
					<view v-if="index >= 3" class="rank-text-badge">
						<text class="rank-text">TOP{{ index + 1 }}</text>
					</view>

					<!-- 水平布局容器 -->
					<view class="product-horizontal-container">
						<!-- 商品图片 -->
						<view class="product-image-container">
							<image
								:src="item.pic || defaultImage"
								class="product-image"
								mode="aspectFill"
								:lazy-load="true"
							/>
						</view>

						<!-- 商品信息 -->
						<view class="product-info">
							<text class="product-name">{{ item.name || '商品名称商品名称商品名称商品名称商品名称商品名称商品名称商品名称' }}</text>
							<view class="price-action-row">
								<text class="product-price">¥{{ item.price || '360' }}</text>
								<view class="action-button" @tap.stop="handleOrder(item)">
									<text class="action-text">去下单</text>
								</view>
							</view>
						</view>
					</view>
				</view>
			</view>

			<!-- 底部小卡片网格 -->
			<view class="grid-product-list">
				<view
					v-for="(item, index) in gridProductList"
					:key="item.id"
					class="grid-product-card"
					@tap="navigateToProduct(item)"
				>
					<!-- 商品图片 -->
					<view class="grid-image-container">
						<image
							:src="item.pic || defaultImage"
							class="grid-image"
							mode="aspectFill"
							:lazy-load="true"
						/>
					</view>

					<!-- 商品信息 -->
					<view class="grid-info">
						<text class="grid-name">{{ item.name || '商品名称商品名称商品名称商品名称' }}</text>
						<view class="grid-price-row">
							<text class="grid-price">¥{{ item.price || '360' }}</text>
							<text class="grid-sales">{{ item.sale || '200' }}+购买</text>
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
	components: {
		'nav-bar': navBar,
		'uni-load-more': uniLoadMore
	},

	data() {
		return {
			// 页面状态
			pageLoading: true,
			loading: false,
			refreshing: false,

			// 分页参数
			currentPage: 1,
			pageSize: 20,
			hasMoreData: true,

			// 数据列表
			productList: [],
			mainProductList: [], // 前5个主要展示的商品
			gridProductList: [], // 底部网格展示的商品

			// 加载状态
			loadingStatus: 'more',
			loadMoreText: {
				contentdown: '上拉显示更多',
				contentrefresh: '正在加载...',
				contentnomore: '没有更多数据了'
			},

			// 默认图片
			defaultImage: 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/default.png'
		};
	},

	onLoad() {
		this.loadProductData(true);
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

				if (response.code === 200 && response.data) {
					const newProducts = response.data;

					if (reset) {
						this.productList = newProducts;
					} else {
						this.productList = [...this.productList, ...newProducts];
					}

					// 分离主要展示商品和网格商品
					this.updateProductLists();

					// 更新分页状态
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

		// 更新产品列表分布
		updateProductLists() {
			this.mainProductList = this.productList.slice(0, 5);
			this.gridProductList = this.productList.slice(5);
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
		},

		// 处理下单
		handleOrder(product) {
			this.navigateToProduct(product);
		}
	}
};
</script>

<style lang="scss" scoped>
.container {
	background: linear-gradient(180deg, rgba(221, 255, 153, 0.8) 0%, rgba(221, 255, 153, 0) 100%), #F8F8F8;
	min-height: 100vh;
}

.loading-container {
	display: flex;
	justify-content: center;
	align-items: center;
	height: 400rpx;
}

.loading-text {
	font-size: 28rpx;
	color: #666666;
}

.scroll-view {
	height: 100vh;
	padding: 20rpx 16rpx 0;
}

/* 主要产品列表样式 */
.main-product-list {
	margin-bottom: 40rpx;
}

.main-product-card {
	background: #FFFFFF;
	border-radius: 16rpx;
	margin-bottom: 20rpx;
	box-shadow: 0px 8rpx 8rpx 0px rgba(25, 43, 0, 0.05);
	position: relative;
	overflow: hidden;
	padding: 24rpx;

	&.border-gold {
		border: 2rpx solid #FFD853;
	}

	&.border-silver {
		border: 2rpx solid #C1C1C1;
	}

	&.border-bronze {
		border: 2rpx solid #EEEEEE;
	}
}

/* 水平布局容器 */
.product-horizontal-container {
	display: flex;
	flex-direction: row;
	align-items: flex-start;
	gap: 24rpx;
}

/* 前三名图片排名标签 */
.rank-image-badge {
	position: absolute;
	top: 0;
	left: 0;
	z-index: 2;
	width: 60rpx;
	height: 60rpx;
}

.rank-image {
	width: 100%;
	height: 100%;
}

/* 第4、5名文字排名标签 */
.rank-text-badge {
	position: absolute;
	top: 0;
	left: 0;
	background: #647D00;
	border-radius: 16rpx 0 16rpx 0;
	padding: 4rpx 8rpx;
	z-index: 2;
}

.rank-text {
	font-weight: 500;
	font-size: 24rpx;
	line-height: 1.4;
	color: #FFFFFF;
}

.product-image-container {
	width: 200rpx;
	height: 200rpx;
	position: relative;
	flex-shrink: 0;
}

.product-image {
	width: 100%;
	height: 100%;
	border-radius: 12rpx;
}

.product-info {
	flex: 1;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
	min-height: 200rpx;
}

.product-name {
	font-weight: 500;
	font-size: 28rpx;
	line-height: 1.4;
	color: #0A0D05;
	display: -webkit-box;
	-webkit-box-orient: vertical;
	-webkit-line-clamp: 2;
	overflow: hidden;
	text-overflow: ellipsis;
	margin-bottom: 20rpx;
}

.price-action-row {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.product-price {
	font-weight: 700;
	font-size: 36rpx;
	line-height: 1.19;
	color: #647D00;
}

.action-button {
	background: #20201E;
	border-radius: 32rpx;
	padding: 10rpx 24rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.action-text {
	font-weight: 400;
	font-size: 28rpx;
	line-height: 1.4;
	color: #A9FF00;
}

/* 网格产品列表样式 */
.grid-product-list {
	display: flex;
	flex-wrap: wrap;
	justify-content: space-between;
	margin-bottom: 40rpx;
}

.grid-product-card {
	background: #FFFFFF;
	border-radius: 16rpx;
	overflow: hidden;
	width: calc(50% - 8rpx);
	margin-bottom: 16rpx;
}

.grid-image-container {
	width: 100%;
	height: 200rpx;
}

.grid-image {
	width: 100%;
	height: 100%;
	border-radius: 16rpx 16rpx 0 0;
}

.grid-info {
	padding: 16rpx;
}

.grid-name {
	font-weight: 400;
	font-size: 28rpx;
	line-height: 1.4;
	color: #0A0D05;
	display: -webkit-box;
	-webkit-box-orient: vertical;
	-webkit-line-clamp: 2;
	overflow: hidden;
	text-overflow: ellipsis;
	margin-bottom: 12rpx;
}

.grid-price-row {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.grid-price {
	font-weight: 700;
	font-size: 36rpx;
	line-height: 1.17;
	color: #647D00;
}

.grid-sales {
	font-weight: 400;
	font-size: 24rpx;
	line-height: 1.4;
	color: #999999;
}

/* 加载更多和空状态 */
.load-more {
	padding: 40rpx 0;
}

.empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	height: 400rpx;
}

.empty-icon {
	font-size: 80rpx;
	margin-bottom: 20rpx;
}

.empty-text {
	font-size: 28rpx;
	color: #666666;
}
</style>