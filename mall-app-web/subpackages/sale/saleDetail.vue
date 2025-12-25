<template>
	<view class="sale-detail-container">
		<!-- 加载中 -->
		<view v-if="loading" class="loading-container">
			<view class="loading-spinner"></view>
			<text class="loading-text">加载中...</text>
		</view>
		
		<!-- 需要登录 -->
		<view v-else-if="needLogin" class="auth-container">
			<view class="auth-icon-box">
				<text class="yticon icon-shouhuodizhi"></text>
			</view>
			<text class="auth-title">请先登录</text>
			<text class="auth-desc">登录后可查看销售单详情</text>
			<button class="auth-btn" @click="handleLogin">立即登录</button>
		</view>
		
		<!-- 无权限 -->
		<view v-else-if="!hasAuth" class="auth-container">
			<view class="auth-icon-box forbidden">
				<text class="yticon icon-bangzhu"></text>
			</view>
			<text class="auth-title">无权限查看</text>
			<text class="auth-desc">{{ authMessage }}</text>
			<button class="auth-btn" @click="goBack">返回首页</button>
		</view>
		
		<!-- 销售单详情 -->
		<view v-else class="detail-content">
			<!-- 状态区域 -->
			<view class="status-section">
				<text class="status-text">{{ saleDetail.statusName }}</text>
				<text class="status-desc">销售单号：{{ saleDetail.saleNo }}</text>
				<view class="g-divider"></view>
			</view>
			
			<!-- 商品信息区域 -->
			<view class="goods-section">
				<view class="g-header">
					<text class="name">商品信息</text>
				</view>
				<view class="g-divider"></view>
				
				<view v-for="(item, index) in saleDetail.items" :key="index" class="g-item">
					<view class="product-container">
						<view class="product-image-container">
							<image class="product-image"
								:src="item.productPic || 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/order/product_image.png'"
								mode="aspectFill">
							</image>
						</view>
						<view class="product-info">
							<view class="product-top">
								<text class="product-name">{{ item.productName }}</text>
								<text v-if="item.specs" class="product-spec">{{ item.specs }}</text>
							</view>
							<view class="price-quantity">
								<text class="product-price">¥<text class="price-number">{{ parseFloat(item.salePrice).toFixed(2) }}</text></text>
								<text class="product-quantity">x{{ item.quantity }}</text>
							</view>
						</view>
					</view>
				</view>
			</view>
			
			<!-- 订单信息区域 -->
			<view class="info-section">
				<view class="info-item">
					<text class="info-label">销售类型</text>
					<text class="info-value">{{ saleDetail.saleTypeName }}</text>
				</view>
				<view class="info-item">
					<text class="info-label">销售门店</text>
					<text class="info-value">{{ saleDetail.storeName }}</text>
				</view>
				<view class="info-item">
					<text class="info-label">销售日期</text>
					<text class="info-value">{{ formatDate(saleDetail.saleDate) }}</text>
				</view>
				<view class="info-item" v-if="saleDetail.remark">
					<text class="info-label">备注</text>
					<text class="info-value">{{ saleDetail.remark }}</text>
				</view>
			</view>
			
			<!-- 客户信息区域 -->
			<view class="info-section" v-if="saleDetail.customerName">
				<text class="section-title">客户信息</text>
				<view class="g-divider"></view>
				<view class="customer-info">
					<view class="customer-row">
						<text class="customer-name">{{ saleDetail.customerName }}</text>
					</view>
				</view>
			</view>
			
			<!-- 价格信息区域 -->
			<view class="price-section">
				<view class="price-item">
					<text class="price-label">商品数量</text>
					<text class="price-value">{{ saleDetail.totalQuantity }} 件</text>
				</view>
				<view class="price-item total">
					<text class="price-label">合计金额</text>
					<text class="price-value total-price">¥{{ parseFloat(saleDetail.totalAmount).toFixed(2) }}</text>
				</view>
			</view>
		</view>
		
		<!-- 登录弹窗 -->
		<login-popup ref="loginPopup" @success="onLoginSuccess"></login-popup>
	</view>
</template>

<script>
import { getSaleDetail, checkSaleAuth } from '@/api/sale'
import LoginPopup from '@/components/login-popup.vue'

export default {
	name: 'SaleDetail',
	components: {
		LoginPopup
	},
	data() {
		return {
			saleId: null,
			loading: true,
			needLogin: false,
			hasAuth: false,
			authMessage: '',
			saleDetail: null
		}
	},
	onLoad(options) {
		console.log('销售单详情页面参数:', options)
		
		try {
			// 优先从 saleId 参数获取
			let id = options.saleId || options.id
			
			// 处理小程序码扫码进入的情况
			// 当通过小程序码进入时，销售单ID会在scene参数中
			if (!id && options.scene) {
				console.log('通过小程序码进入，scene参数:', options.scene)
				try {
					// 解析scene参数，支持多种格式
					const scene = decodeURIComponent(options.scene)
					console.log('解码后的scene参数:', scene)
					
					// 销售单分享的scene参数直接就是销售单ID
					if (scene && /^\d+$/.test(scene)) {
						// 纯数字，直接作为销售单ID
						id = scene
						console.log('从scene参数解析出销售单ID:', id)
					} else {
						// 尝试解析 key=value 格式
						const params = this.parseScene(scene)
						if (params.saleId || params.id) {
							id = params.saleId || params.id
							console.log('从scene参数解析出销售单ID:', id)
						} else {
							console.warn('scene参数格式不正确:', scene)
						}
					}
				} catch (error) {
					console.error('解析scene参数失败:', error)
				}
			}
			
			// 处理短链接跳转的情况（q参数）
			if (!id && options.q) {
				try {
					const url = decodeURIComponent(options.q)
					console.log('短链接URL:', url)
					// 从URL中提取saleId参数
					const urlParams = new URL(url).searchParams
					id = urlParams.get('saleId') || urlParams.get('id')
					console.log('从短链接解析出销售单ID:', id)
				} catch (error) {
					console.error('解析短链接参数失败:', error)
				}
			}
			
			this.saleId = id
			console.log('最终saleId:', this.saleId)
			
			if (this.saleId) {
				this.checkAuthAndLoad()
			} else {
				this.loading = false
				this.hasAuth = false
				this.authMessage = '销售单ID无效'
			}
		} catch (error) {
			console.error('页面加载错误:', error)
			this.loading = false
			this.hasAuth = false
			this.authMessage = '页面加载出错'
		}
	},
	methods: {
		// 解析scene参数
		parseScene(scene) {
			const params = {}
			const pairs = scene.split('&')
			pairs.forEach(pair => {
				const [key, value] = pair.split('=')
				if (key && value) {
					params[key] = value
				}
			})
			return params
		},
		
		// 检查权限并加载数据
		async checkAuthAndLoad() {
			this.loading = true
			try {
				const authRes = await checkSaleAuth(this.saleId)
				console.log('权限检查结果:', authRes)
				
				if (authRes.code === 200) {
					const authData = authRes.data
					if (authData.needLogin) {
						this.needLogin = true
						this.hasAuth = false
						this.authMessage = authData.message
					} else if (!authData.hasAuth) {
						this.needLogin = false
						this.hasAuth = false
						this.authMessage = authData.message
					} else {
						await this.loadSaleDetail()
					}
				} else {
					this.hasAuth = false
					this.authMessage = authRes.message || '权限验证失败'
				}
			} catch (error) {
				console.error('检查权限失败', error)
				this.hasAuth = false
				this.authMessage = '网络错误，请稍后重试'
			} finally {
				this.loading = false
			}
		},
		
		// 加载销售单详情
		async loadSaleDetail() {
			try {
				const res = await getSaleDetail(this.saleId)
				console.log('销售单详情:', res)
				
				if (res.code === 200) {
					this.saleDetail = res.data
					this.hasAuth = true
				} else if (res.code === 403) {
					this.hasAuth = false
					this.authMessage = res.message || '无权限查看'
				} else {
					this.hasAuth = false
					this.authMessage = res.message || '获取详情失败'
				}
			} catch (error) {
				console.error('加载销售单详情失败', error)
				this.hasAuth = false
				this.authMessage = '网络错误，请稍后重试'
			}
		},
		
		// 处理登录
		handleLogin() {
			this.$refs.loginPopup.open()
		},
		
		// 登录成功回调
		onLoginSuccess() {
			this.needLogin = false
			this.checkAuthAndLoad()
		},
		
		// 返回首页
		goBack() {
			uni.switchTab({
				url: '/pages/new_index/index'
			})
		},
		
		// 格式化日期
		formatDate(date) {
			if (!date) return '-'
			const d = new Date(date)
			if (isNaN(d.getTime())) return date
			return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
		}
	}
}
</script>


<style lang="scss">
@charset "UTF-8";

page {
	background: #f7f7f7;
	padding-bottom: 40rpx;
}

.sale-detail-container {
	min-height: 100vh;
	background-color: #f7f7f7;
}

.loading-container {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	height: 60vh;
	
	.loading-spinner {
		width: 60rpx;
		height: 60rpx;
		border: 4rpx solid #e0e0e0;
		border-top-color: #333;
		border-radius: 50%;
		animation: spin 1s linear infinite;
	}
	
	.loading-text {
		margin-top: 20rpx;
		font-size: 28rpx;
		color: #999;
	}
}

@keyframes spin {
	to {
		transform: rotate(360deg);
	}
}

.auth-container {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	height: 60vh;
	padding: 40rpx;
	
	.auth-icon-box {
		width: 160rpx;
		height: 160rpx;
		background: #333;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-bottom: 30rpx;
		
		&.forbidden {
			background: #999;
		}
		
		.yticon {
			font-size: 80rpx;
			color: #fff;
		}
	}
	
	.auth-title {
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 16rpx;
	}
	
	.auth-desc {
		font-size: 28rpx;
		color: #999;
		margin-bottom: 40rpx;
		text-align: center;
	}
	
	.auth-btn {
		width: 300rpx;
		height: 80rpx;
		line-height: 80rpx;
		background: #333;
		color: #fff;
		font-size: 30rpx;
		border-radius: 40rpx;
		border: none;
	}
}

.detail-content {
	padding-bottom: 40rpx;
}

/* 状态区域 - 参考orderDetail */
.status-section {
	width: 750rpx;
	min-height: 152rpx;
	position: relative;
	background: #FFFFFF;
	padding: 30rpx;
	box-sizing: border-box;
	
	.status-text {
		display: block;
		font-family: "Source Han Sans SC";
		font-style: normal;
		font-weight: 500;
		font-size: 32rpx;
		line-height: 48rpx;
		color: #000000;
		margin-bottom: 16rpx;
	}
	
	.status-desc {
		font-family: "Source Han Sans SC";
		font-style: normal;
		font-weight: 400;
		font-size: 24rpx;
		line-height: 36rpx;
		color: #666666;
	}
	
	.g-divider {
		position: absolute;
		width: 750rpx;
		height: 0rpx;
		left: 0;
		bottom: 0;
		border-bottom: 8rpx solid #F9F9F9;
	}
}

/* 商品区域 - 参考orderDetail */
.goods-section {
	background: #FFFFFF;
	padding: 20rpx 30rpx;
	margin-top: 10rpx;
	box-sizing: border-box;
	
	.g-header {
		display: flex;
		align-items: center;
		padding: 10rpx 0 15rpx;
		
		.name {
			font-size: 32rpx;
			font-weight: 500;
			font-family: "Source Han Sans SC";
			color: #000000;
		}
	}
	
	.g-divider {
		width: 100%;
		height: 1rpx;
		background-color: #DDDDDD;
		margin: 0;
	}
	
	.g-item {
		padding: 20rpx 0;
		border-bottom: 1rpx solid #f5f5f5;
		
		&:last-child {
			border-bottom: none;
		}
	}
	
	.product-container {
		display: flex;
		width: 100%;
	}
	
	.product-image-container {
		position: relative;
		width: 180rpx;
		height: 180rpx;
		flex-shrink: 0;
		border-radius: 10rpx;
		overflow: hidden;
		background-color: #F6F3E8;
	}
	
	.product-image {
		width: 100%;
		height: 100%;
		display: block;
	}
	
	.product-info {
		flex: 1;
		padding-left: 20rpx;
		display: flex;
		flex-direction: column;
		justify-content: space-between;
		min-height: 180rpx;
	}
	
	.product-top {
		display: flex;
		flex-direction: column;
		gap: 8rpx;
	}
	
	.product-name {
		font-size: 28rpx;
		font-weight: 500;
		font-family: "Source Han Sans SC";
		color: #000000;
		line-height: 1.5;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 2;
		line-clamp: 2;
		overflow: hidden;
	}
	
	.product-spec {
		font-size: 26rpx;
		font-weight: 400;
		font-family: "Source Han Sans SC";
		color: #999999;
		line-height: 1.4;
	}
	
	.price-quantity {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-top: 10rpx;
	}
	
	.product-price {
		font-size: 22rpx;
		font-weight: 400;
		font-family: "Source Han Sans CN";
		color: #000000;
		line-height: 1.5;
		
		.price-number {
			font-size: 32rpx;
			font-weight: 500;
		}
	}
	
	.product-quantity {
		font-size: 24rpx;
		font-weight: 400;
		font-family: "Source Han Sans SC";
		color: #666666;
		line-height: 1.6;
	}
}

/* 信息区域 - 参考orderDetail */
.info-section {
	background: #FFFFFF;
	padding: 20rpx 30rpx;
	margin-top: 10rpx;
	
	.section-title {
		font-size: 32rpx;
		font-weight: 500;
		color: #000000;
		margin-bottom: 10rpx;
		padding: 10rpx 0;
	}
	
	.g-divider {
		width: 100%;
		height: 1rpx;
		background-color: #DDDDDD;
		margin-bottom: 15rpx;
	}
	
	.info-item {
		display: flex;
		justify-content: space-between;
		padding: 16rpx 0;
	}
	
	.info-label {
		font-size: 28rpx;
		color: #666666;
	}
	
	.info-value {
		font-size: 28rpx;
		color: #000000;
		font-weight: 400;
		text-align: right;
		flex: 1;
		margin-left: 20rpx;
	}
}

/* 客户信息 */
.customer-info {
	padding: 15rpx 0;
	
	.customer-row {
		display: flex;
		align-items: center;
	}
	
	.customer-name {
		font-size: 28rpx;
		font-weight: 500;
		color: #000000;
	}
}

/* 价格区域 - 参考orderDetail */
.price-section {
	background: #FFFFFF;
	padding: 30rpx;
	margin-top: 10rpx;
	
	.price-item {
		display: flex;
		justify-content: space-between;
		margin-bottom: 20rpx;
		
		&:last-child {
			margin-bottom: 0;
		}
		
		.price-label {
			font-size: 28rpx;
			color: #333333;
		}
		
		.price-value {
			font-size: 28rpx;
			color: #333333;
		}
	}
	
	.total {
		margin-top: 20rpx;
		border-top: 1rpx solid #EEEEEE;
		padding-top: 20rpx;
		
		.price-label {
			font-size: 32rpx;
			font-weight: 500;
		}
		
		.total-price {
			font-size: 32rpx;
			font-weight: 500;
			color: #FF3D4D;
		}
	}
}
</style>
