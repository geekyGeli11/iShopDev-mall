<template>
	<view class="container">
		<nav-bar :back="true" :placeholder="false" bgColor="transparent"></nav-bar>
		<view class="carousel">
			<swiper indicator-dots="true" circular="true" duration="400" @change="onSwiperChange">
				<swiper-item v-for="(item, index) in imgList" :key="index" class="swiper-item">
					<view class="image-wrapper" @tap="previewImage(index)">
						<image class="loaded" :src="item.src" mode="aspectFill" :style="!hasAnyStock ? 'filter: grayscale(100%);' : ''"></image>
					</view>
				</swiper-item>
			</swiper>
			<!-- 售罄蒙版 -->
			<view class="sold-out-overlay" v-if="!hasAnyStock">
				<text class="sold-out-text">已售罄</text>
			</view>
		</view>

		<view v-if="hasLogin && promoInfo.hasPromo" class="promo-bar" @tap="handlePromoClick">
			<text class="promo-text">{{ promoInfo.text }}</text>
			<view class="get-promo-btn">立即领取</view>
		</view>

		<view class="introduce-section">
			<text class="title">{{ product.name }}</text>
			<view class="price-box">
				<view class="price-container">
					<!-- 左侧购买价 -->
					<view class="price-left">
						<view class="buy-price-tag">
							<text class="buy-price-label">{{ isFlash ? '限时优惠价' : (product.promotionType === 1 ? '特惠价' : '购买价') }}</text>
							<view class="price-row">
								<view class="current-price-container">
									<text class="buy-price-symbol">¥</text>
									<text class="buy-price">{{ isFlash ? flashPrice : product.price }}</text>
								</view>
								<!-- 显示原价（秒杀或有促销时显示） -->
								<text v-if="(isFlash || product.promotionType === 1) && product.originalPrice && product.originalPrice > product.price" class="original-price">¥{{ product.originalPrice }}</text>
							</view>
						</view>
						<text class="buy-price-desc">{{ isFlash ? '限时优惠到手价' : (product.promotionType === 1 ? '特惠促销到手价' : '直接购买预制到手价') }}</text>
					</view>

					<!-- 右侧定制价格 -->
					<view v-if="product.isDIY" class="price-right">
						<view class="diy-price-range">
							<text class="diy-range-symbol">¥</text>
							<text class="diy-range-price">{{ getDIYPrice() }}</text>
							<text class="diy-range-separator"> ~ </text>
							<text class="diy-range-symbol">¥</text>
							<text class="diy-range-price">{{ getDIYPriceHigh() }}</text>
						</view>
						<text class="diy-price-desc">产品定制价格预估</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 发货信息 -->
		<view class="shipping-section">
			<view class="shipping-row">
				<image class="shipping-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/product/express.png" mode="widthFix"></image>
				<view class="shipping-info">
					<text class="shipping-desc">{{ freightDescription }}</text>
				</view>
			</view>
		</view>

		<!-- 商品属性 -->
		<view v-if="displayAttrList.length > 0" class="attribute-section">
			<view class="attr-header">
				<image class="attr-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/product/attribute.png" mode="widthFix"></image>
				<view class="attr-content">
					<view class="attr-row">
						<text v-for="(item, index) in displayAttrList.slice(0, 3)" :key="index" class="attr-name">{{
							item.key }}</text>
					</view>
					<view class="attr-row">
						<text v-for="(item, index) in displayAttrList.slice(0, 3)" :key="index" class="attr-value">{{
							item.value }}</text>
					</view>
				</view>
				<text class="icon-arrow">></text>
			</view>
		</view>

		<!-- <view class="service-section">
			<view class="service-row">
				<image class="service-icon"
					src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/product/express.png"
					mode="widthFix"></image>
				<view class="service-items">
					<text class="service-item">无忧退货</text>
					<text class="divider">|</text>
					<text class="service-item">快速退款</text>
					<text class="divider">|</text>
					<text class="service-item">免费包邮</text>
				</view>
			</view>
			<view class="insurance-row">
				<image class="service-icon"
					src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/product/save.png"
					mode="widthFix"></image>
				<text class="insurance-text">本产品由中国人民财产保险股份有限公司承保</text>
			</view>
		</view> -->

		<view class="detail-desc">
			<view class="d-header">
				<text>宝贝详情</text>
			</view>
			<rich-text :nodes="desc"></rich-text>
		</view>

		<!-- <view class="code-container">
			<view class="code-wrapper">
				<image class="code-bg"
					src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/product/code-bg.png"
					mode="aspectFit"></image>
				<image class="brand-img" :src="brandloge" mode="aspectFit"></image>
				<image class="code-img" :src="codeloge" mode="aspectFit"></image>
				<image class="source-img" :src="sourceloge" mode="aspectFit"></image>
			</view>
		</view> -->


		<view class="page-bottom">
			<view class="bottom-item" @tap="contactService">
				<image class="bottom-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/product/kefu.png"></image>
				<text class="bottom-text">客服</text>
			</view>
			<view class="bottom-item" @tap="toFavorite">
				<image class="bottom-icon"
					:src="favorite ? 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/images/collection_selected.png' : 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/product/favorite.png'"></image>
				<text class="bottom-text">收藏</text>
			</view>

			<!-- 按钮组 -->
			<view class="button-group">
				<view class="group-button cart-button" @tap="toggleSpec">
					<image class="button-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/product/cart.png"></image>
					<view v-if="cartCount > 0" class="cart-badge">{{ cartCount }}</view>
				</view>
				<view v-if="product.isDIY" class="group-button diy-button" @tap="handleDIY">
					<text class="button-text">DIY定制</text>
				</view>
				<view class="group-button buy-button" @tap="toggleSpec">
					<text class="button-text">立即购买</text>
				</view>
			</view>
		</view>

		<!-- 推广浮动按钮：只有从分销中心跳转过来才显示 -->
		<view v-if="isPromotion" class="float-promotion-btn" @tap="handlePromotion">
			<image class="promotion-icon"
				src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/share-icon.png"></image>
		</view>

		<view :class="['popup', 'spec', specClass]" @touchmove.stop="stopPrevent" @tap="toggleSpec">
			<view class="mask"></view>
			<view class="layer attr-content" @tap.stop="stopPrevent">
				<!-- 弹窗头部 -->
				<view class="popup-header">
					<image class="close-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/product/close.png" @tap="toggleSpec"></image>
					<view class="popup-title-container">
						<image class="comment-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/product/comment.png"></image>
						<text class="popup-title">大家都说很不错</text>
					</view>
				</view>

				<!-- 购买类型切换 -->
				<view class="buy-type-tabs">
					<view :class="['tab-item', buyType === 'myself' ? 'active' : '']" @tap="switchBuyType('myself')">
						<text class="tab-text">买给自己</text>
					</view>
					<view :class="['tab-item', buyType === 'friend' ? 'active' : '']" @tap="switchBuyType('friend')">
						<text class="tab-text">微信送礼</text>
					</view>
				</view>

				<!-- 送给朋友的提示 -->
				<!-- <view v-if="buyType === 'friend'" class="gift-tip">
					<text class="gift-tip-text">下单后，分享给朋友-TA填写口令与收货地址-收下礼物</text>
				</view> -->

				<!-- 地址信息 (仅在买给自己时显示) -->
				<view v-if="buyType === 'myself'" class="address-section" @tap="toAddressManage">
					<view class="address-content">
						<view class="address-left">
							<view class="address-header">
								<image class="address-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/product/address.png"></image>
								<text class="address-title">{{ currentAddress ? currentAddress.name : '用户信息' }}</text>
								<text class="address-detail">{{ getAddressDisplay() }}</text>
							</view>
							<view class="shipping-row">
								<text class="shipping-info">{{ freightDescription }}｜预计{{ getExpectedDeliveryDate()
									}}送达</text>
							</view>
						</view>
						<image class="arrow-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/product/arrow_right.png"></image>
					</view>
				</view>

				<!-- 商品信息 -->
				<view class="product-section">
					<!-- 上方：商品图片 + 价格 + 数量选择器 -->
					<view class="product-info">
						<view class="spec-image-container" @tap="previewSpecImage">
							<image :src="selectedSkuPic || product.pic" mode="aspectFit" class="spec-image"></image>
						</view>
						<view class="product-right">
							<view class="price-info">
								<text class="price-prefix">实付 ¥</text>
								<text class="price-amount">{{ isFlash ? flashPrice : product.price }}</text>
								<!-- <text class="price-discount">｜优惠 ¥70</text> -->
							</view>
							<view class="quantity-control">
								<button class="quantity-btn" @tap="decreaseQuantity">-</button>
								<input type="number" v-model="quantity" class="quantity-input"
									@input="validateQuantity" />
								<button class="quantity-btn" @tap="increaseQuantity">+</button>
							</view>
						</view>
					</view>

					<!-- 下方：商品详情（规格选择） -->
					<view class="product-details">
						<view class="product-specs">
							<view v-for="(item, index) in specList" :key="index" class="spec-row">
								<text class="spec-label">{{ item.name }}</text>
								<view class="spec-options">
									<view v-for="(childItem, childIndex) in specChildList" :key="childIndex"
										v-if="childItem.pid === item.id" :class="['spec-option-wrapper']">
										<text
											:class="['spec-option', childItem.selected ? 'selected' : '', !childItem.available ? 'disabled' : '']"
											@tap="childItem.available ? selectSpec(childIndex, childItem.pid) : null">{{
												childItem.name }}</text>
										<text v-if="!childItem.available" class="out-of-stock-tip">缺货</text>

									</view>
								</view>
							</view>
						</view>
					</view>
				</view>

				<!-- 分隔条 -->
				<view class="section-divider"></view>

				<!-- 礼物赠言 (仅在送给朋友时显示) -->
				<view v-if="buyType === 'friend'" class="gift-message-section">
					<view class="gift-message-label">
						<image class="gift-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/product/gift.png"></image>
						<text class="gift-message-text">礼物赠言</text>
					</view>
					<textarea class="gift-message-input" v-model="giftMessage" placeholder="心想事成，事事如意"
						maxlength="100"></textarea>
				</view>

				<!-- 分隔条 (礼物赠言显示时) -->
				<view v-if="buyType === 'friend'" class="section-divider"></view>

				<!-- 支付方式 -->
				<!-- <view class="payment-section">
					<view class="payment-method">
						<view class="payment-item">
							<image class="payment-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/product/wechat.png"></image>
							<text class="payment-text">微信</text>
						</view>
						<view class="payment-radio">
							<view class="radio-outer">
								<view class="radio-inner"></view>
							</view>
						</view>
					</view>
					<view class="more-payment">
						<text class="more-payment-text">更多支付方式</text>
						<image class="arrow-down-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/product/arrow_down.png"></image>
					</view>
				</view> -->

				<!-- 操作按钮 -->
				<view class="action-btn-group">
					<button v-if="currentAction === 'gift'" class="action-btn buy-now-btn" type="primary"
						@tap="confirmGift">确定</button>
					<template v-else>
						<button class="action-btn add-cart-btn" type="primary" @tap="addToCart">加入购物袋</button>
						<button class="action-btn buy-now-btn" type="primary" @tap="buy">立即支付</button>
					</template>
				</view>
			</view>
		</view>

		<view :class="['popup', 'spec', attrClass]" @touchmove.stop="stopPrevent" @tap="toggleAttr">
			<view class="mask"></view>
			<view class="layer attr-content no-padding" @tap.stop="stopPrevent">
				<view class="c-list">
					<view v-for="item in attrList" :key="item.key" class="c-row b-b">
						<text class="tit">{{ item.key }}</text>
						<view class="con">
							<text class="con t-r">{{ item.value }}</text>
						</view>
					</view>
				</view>
			</view>

			<!-- DIY入口弹窗 -->
			<diy-entry-popup
				:visible="showDIYPopup"
				:product-id="product.id"
				:customize-faces="diyCustomizeFaces"
				@close="closeDIYPopup"
				@start-customize="handleStartCustomize"
			/>
		</view>

		<view :class="['mask', couponState === 0 ? 'none' : couponState === 1 ? 'show' : '']" @tap="toggleCoupon">
			<view class="mask-content" @tap.stop="stopPrevent">
				<view v-for="(item, index) in couponList" :key="index" class="coupon-item" @tap="addCoupon(item)">
					<view class="con">
						<view class="left">
							<text class="title">{{ item.name }}</text>
							<text class="time">有效期至{{ formatDateTime(item.endTime) }}</text>
						</view>
						<view class="right">
							<text class="price">{{ item.amount }}</text>
							<text>满{{ item.minPoint }}可用</text>
						</view>
						<view class="circle l"></view>
						<view class="circle r"></view>
					</view>
					<text class="tips">{{ formatCouponUseType(item.useType) }}</text>
				</view>
			</view>
		</view>

		<share ref="share" :shareList="shareList" :product="shareProduct"></share>
		<coupon-popup :show="showCouponPopup" :couponData="couponData" @updateShow="showCouponPopup = $event"
			@close="closeCouponPopup" @getCoupon="handleGetCoupon"></coupon-popup>
	</view>
</template>

<script>
import share from '@/components/share';
import couponPopup from '@/components/CouponPopup';
import recommendedProducts from '@/components/recommended-products/recommended-products.vue';
import navBar from '@/components/nav-bar';
import DIYEntryPopup from '@/components/diy/DIYEntryPopup.vue';
import {
	fetchProductDetail
} from '@/api/product.js';
import {
	addCartItem,
	fetchCartList
} from '@/api/cart.js';
import {
	fetchProductCouponList,
	addMemberCoupon
} from '@/api/coupon.js';
import {
	createReadHistory
} from '@/api/memberReadHistory.js';
import {
	createProductCollection,
	deleteProductCollection,
	productCollectionDetail
} from '@/api/memberProductCollection.js';
import {
	fetchAddressList
} from '@/api/address.js';
import {
	getDIYProductConfig,
	getDIYProductTemplate
} from '@/api/diy.js';
import {
	mapState
} from 'vuex';
import {
	formatDate
} from '@/utils/date';

const defaultServiceList = [{
	id: 1,
	name: "无忧退货"
}, {
	id: 2,
	name: "快速退款"
}, {
	id: 3,
	name: "免费包邮"
}];
const defaultShareList = [{
	type: 1,
	icon: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/temp/share_wechat.png',
	text: '微信好友'
},
{
	type: 2,
	icon: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/temp/share_moment.png',
	text: '朋友圈'
},
{
	type: 3,
	icon: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/temp/share_qq.png',
	text: 'QQ好友'
},
{
	type: 4,
	icon: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/temp/share_qqzone.png',
	text: 'QQ空间'
}
];

export default {
	components: {
		share,
		couponPopup,
		recommendedProducts,
		'nav-bar': navBar,
		'diy-entry-popup': DIYEntryPopup,
	},
	// 添加分享到朋友圈的页面选项
	onShareTimeline() {
		try {
			return {
				title: this.product.name,
				query: `id=${this.product.id}`,
				imageUrl: this.imgList.length > 0 ? this.imgList[0].src : this.product.pic
			};
		} catch (error) {
			console.error('生成分享朋友圈参数出错:', error);
			return {
				title: '精选好物',
				imageUrl: this.product.pic
			};
		}
	},
	// 保持原有的onShareAppMessage方法
	onShareAppMessage() {
		try {
			return {
				title: this.product.name || '精选商品',
				path: `/pages/product/product?id=${this.product.id}`,
				imageUrl: this.imgList.length > 0 ? this.imgList[0].src : ''
			};
		} catch (error) {
			console.error('分享消息配置出错:', error);
			return {
				title: '精选商品',
				path: '/pages/new_index/index'
			};
		}
	},
	data() {
		return {
			specClass: 'none',
			attrClass: 'none',
			specSelected: [],
			favorite: false,
			shareList: [],
			imgList: [],
			currentImageIndex: 0, // 当前轮播图索引
			desc: '',
			specList: [],
			specChildList: [],
			product: {},
			brand: {},
			serviceList: [],
			skuStockList: [],
			attrList: [],
			promotionTipList: [],
			couponState: 0,
			couponList: [],
			quantity: 1,
			cartCount: 0,
			currentAction: 'cart', // 'cart' or 'gift'
			price: [39, 59, 89, 129, 199, 269],
			promoInfo: {
				hasPromo: false,
				text: ''
			},
			brandloge: '',
			codeloge: '',
			sourceloge: '',
			shareProduct: {},
			showCouponPopup: false,
			couponData: {
				amount: '0',
				title: '',
				desc: '',
				footerText: ''
			},
			isLoading: false, // 添加加载状态标识
			isFlash: false,
			flashPrice: 0,
			originalProductPrice: 0, // 添加这一行，用于保存商品原始价格
			selectedSkuPic: '', // 添加选中SKU的图片
			displayAttrList: [], // 展示用的商品属性列表
			freightDescription: '快递：免运费，可加顺丰', // 运费描述，默认值
			// 新增地址相关数据
			currentAddress: null, // 当前选中的地址
			addressList: [], // 用户地址列表
			// 新增购买弹窗相关数据
			buyType: 'myself', // 'myself' 或 'friend'
			giftMessage: '心想事成，事事如意', // 礼物赠言
			// 新增库存检查相关数据
			hasAnyStock: true, // 是否有任何库存
			// DIY相关数据
			showDIYPopup: false, // 是否显示DIY入口弹窗
			diyCustomizeFaces: [], // DIY可定制面列表
			diyConfig: null, // DIY配置信息
			diyTemplate: null, // DIY模板信息
			// 推广相关数据
			isPromotion: false, // 是否来自分销中心推广
			distributorId: null // 分销商ID
		};
	},
	async onLoad(options) {
		try {
			let id = options.id;

			// 处理小程序码扫码进入的情况
			// 当通过小程序码进入时，商品ID会在scene参数中
			if (!id && options.scene) {
				console.log('通过小程序码进入，scene参数:', options.scene);
				try {
					// 解析scene参数，支持多种格式
					const scene = decodeURIComponent(options.scene);
					console.log('解码后的scene参数:', scene);

					// 商品分享的scene参数直接就是商品ID
					// 与后端generateMiniProgramCode中的 scene = productId.toString() 对应
					if (scene && /^\d+$/.test(scene)) {
						// 纯数字，直接作为商品ID
						id = scene;
						console.log('从scene参数解析出商品ID:', id);
					} else {
						console.warn('scene参数格式不正确:', scene);
					}
				} catch (error) {
					console.error('解析scene参数失败:', error);
				}
			}

			if (!id) {
				uni.showToast({
					title: '商品ID不存在',
					icon: 'none'
				});
				setTimeout(() => {
					uni.navigateBack();
				}, 1500);
				return;
			}

			console.log('商品ID:', id);

			// 检查是否是秒杀商品
			this.isFlash = options.isFlash === 'true';
			this.flashPrice = options.flashPrice ? Number(options.flashPrice) : 0;

			// 检查是否来自分销中心推广
			this.isPromotion = options.isPromotion === 'true';
			this.distributorId = options.distributorId || null;

			console.log('商品详情页参数:', {
				isFlash: this.isFlash,
				flashPrice: this.flashPrice,
				options: options
			});

			this.shareList = defaultShareList;
			this.loadData(id);
			this.getCartItemCount();
			// 获取用户默认地址
			this.getDefaultAddress();

			// 初始化默认图片路径，稍后在loadData中处理
			this.brandloge = 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/logo.png';
			this.codeloge = 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/product/sales.png';
			this.sourceloge = 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/product/source.png';

			// 启用分享到朋友圈功能
			this.enableShareTimeline();
		} catch (error) {
			console.error('页面加载错误:', error);
			uni.showToast({
				title: '页面加载出错',
				icon: 'none'
			});
		}
	},

	// 新增：页面显示时重新获取地址信息
	onShow() {
		// 只有在没有当前地址时才重新获取默认地址
		// 避免覆盖用户从地址管理页面选择的地址
		if (!this.currentAddress || !this.currentAddress.name) {
			this.getDefaultAddress();
		}
	},
	computed: {
		...mapState(['hasLogin'])
	},
	filters: {
		formatDateTime(time) {
			if (time == null || time === '') {
				return 'N/A';
			}
			let date = new Date(time);
			return formatDate(date, 'yyyy-MM-dd hh:mm:ss')
		},
		formatCouponUseType(useType) {
			if (useType == 0) {
				return "全场通用";
			} else if (useType == 1) {
				return "指定分类商品可用";
			} else if (useType == 2) {
				return "指定商品可用";
			}
			return null;
		},
	},
	methods: {

		async loadData(id) {
			try {
				this.isLoading = true;


				const response = await fetchProductDetail(id);
				if (!response || !response.data) {
					throw new Error('获取商品详情失败');
				}

				// 保存API返回的原始数据
				const responseData = response.data;

				// 商品基本信息
				this.product = responseData.product || {};

				// 根据后端返回的 diyEnabled 字段设置 isDIY 属性
				// diyEnabled: 1 表示可DIY定制，0 表示不可定制
				this.product.isDIY = this.product.diyEnabled === 1;

				// 保存原始的商品价格
				this.originalProductPrice = this.product.price;

			// 处理促销价格：当有单品优惠时，使用促销价格
			if (this.product.promotionType === 1 && this.product.promotionPrice && this.product.promotionPrice > 0) {
				// 保存原价用于显示划线价
				this.product.originalPrice = this.product.price;
				// 将商品价格设置为促销价
				this.product.price = this.product.promotionPrice;
				console.log('应用单品促销价格:', {
					originalPrice: this.product.originalPrice,
					promotionPrice: this.product.promotionPrice,
					currentPrice: this.product.price
				});
			}

				// 确保detailMobileHtml优先使用
				if (!this.product.detailMobileHtml && this.product.detailHtml) {
					this.product.detailMobileHtml = this.product.detailHtml;
				}

				this.skuStockList = responseData.skuStockList || [];
				this.brand = responseData.brand || {};

				// 更新品牌logo，如果存在则使用
				if (this.brand && this.brand.logo) {
					this.brandloge = this.brand.logo;
				}

				// 初始化各种数据，每个都用try-catch包装
				this.initImgList();
				this.initServiceList();
				this.initSpecList(responseData);
				this.initAttrList(responseData);
				this.initDisplayAttrList(responseData);
				this.initPromotionTipList(responseData);
				this.initProductDesc();
				this.handleReadHistory();
				this.initProductCollection();

				// 获取优惠券列表
				await this.fetchCoupons();

				// 处理运费信息
				this.updateFreightDescription(responseData.freightTemplateInfo);

				// 检查库存状态
				this.checkAnyStock();

				// 处理秒杀价格逻辑
				if (this.isFlash) {
					// 确保有有效的秒杀价格，否则取消秒杀状态
					if (!this.flashPrice || this.flashPrice <= 0) {
						console.warn('秒杀价格无效，取消秒杀状态');
						this.isFlash = false;
					} else {
						console.log('秒杀商品价格信息:', {
							isFlash: this.isFlash,
							flashPrice: this.flashPrice,
							productPrice: this.product.price,
							productOriginalPrice: this.product.originalPrice
						});
					}
				}

				// 设置分享产品信息
				this.shareProduct = {
					id: this.product.id,
					title: this.product.name,
					pic: this.product.pic,
					price: this.isFlash ? this.flashPrice : this.product.price,
					points: this.product.price, // 可得积分
					limited: this.product.promotionType === 1, // 是否为秒杀商品
					recommended: this.product.recommandStatus === 1, // 是否为推荐商品
					description: this.product.subTitle || '优质好物，品质保证',
					qrcode: this.codeloge, // 使用营销码
					userName: this.getUserName(), // 使用方法获取用户名，避免内联复杂逻辑
					brandName: this.product.brandName || this.brand.name // 品牌名称
				};

			} catch (error) {
				console.error('加载商品数据出错:', error);
				uni.showToast({
					title: '商品数据加载失败',
					icon: 'none'
				});
				// 设置默认值，确保UI不崩溃
				this.product = this.product || { name: '商品信息', price: 0, pic: '' };
				this.imgList = this.imgList.length ? this.imgList : [{ src: 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/default.png' }];
			} finally {
				this.isLoading = false;
			}
		},
		// 获取优惠券列表
		async fetchCoupons() {
			try {
				if (!this.hasLogin || !this.product.id) {
					return;
				}

				const response = await fetchProductCouponList(this.product.id);
				this.couponList = response.data || [];

				// 如果有优惠券，则显示营销工具条
				if (this.couponList.length > 0) {
					this.promoInfo = {
						hasPromo: true,
						text: '优惠活动'
					};

					// 预设优惠券弹窗数据
					if (this.couponList.length > 0) {
						const firstCoupon = this.couponList[0];
						this.couponData = {
							amount: firstCoupon.amount || '0',
							title: this.promoInfo.text || '优惠活动',
							desc: firstCoupon.name || '限时优惠活动',
							footerText: '立即领取优惠券'
						};
					}
				}
			} catch (error) {
				console.error('获取优惠券列表失败:', error);
			}
		},
		//规格弹窗开关
		toggleSpec() {
			try {
				this.currentAction = 'cart';
				if (this.specClass === 'show') {
					this.specClass = 'hide';
					setTimeout(() => {
						this.specClass = 'none';
					}, 250);
				} else if (this.specClass === 'none') {
					// 确保打开弹窗时根据已选规格更新图片
					let skuStock = this.getSkuStock();
					if (skuStock && skuStock.pic) {
						this.selectedSkuPic = skuStock.pic;
					} else {
						// 如果没有选择完整规格，显示默认图片
						this.selectedSkuPic = this.product.pic;
					}

					// 每次打开弹窗时重新获取地址信息
					this.getDefaultAddress();

					this.specClass = 'show';
				}
			} catch (error) {
				console.error('切换规格窗口出错:', error);
				this.specClass = 'none';
			}
		},
		//属性弹窗开关
		toggleAttr() {
			if (this.attrClass === 'show') {
				this.attrClass = 'hide';
				setTimeout(() => {
					this.attrClass = 'none';
				}, 250);
			} else if (this.attrClass === 'none') {
				this.attrClass = 'show';
			}
		},
		//优惠券弹窗开关
		toggleCoupon(type) {
			try {
				// 检查登录状态
				if (!this.hasLogin) {
					uni.showToast({
						title: "请先登录",
						icon: "none"
					});
					return;
				}

				fetchProductCouponList(this.product.id).then(response => {
					this.couponList = response.data || [];
					if (this.couponList == null || this.couponList.length == 0) {
						uni.showToast({
							title: "暂无可领优惠券",
							icon: "none"
						});
						return;
					}
					let timer = type === 'show' ? 10 : 300;
					let state = type === 'show' ? 1 : 0;
					this.couponState = 2;
					setTimeout(() => {
						this.couponState = state;
					}, timer);
				}).catch(error => {
					console.error('获取优惠券列表出错:', error);

					// 提取具体的错误信息
					let errorMessage = '获取优惠券失败';
					if (error && error.data && error.data.message) {
						errorMessage = error.data.message;
					} else if (error && error.message) {
						errorMessage = error.message;
					} else if (typeof error === 'string') {
						errorMessage = error;
					}

					uni.showToast({
						title: errorMessage,
						icon: 'none',
						duration: 3000
					});
				});
			} catch (error) {
				console.error('切换优惠券窗口出错:', error);
				this.couponState = 0;
			}
		},
		//选择规格
		selectSpec(index, pid) {
			try {
				let list = this.specChildList;
				let selectedSpec = list[index];

				// 检查规格是否可用（库存检查）
				if (!selectedSpec.available) {
					uni.showToast({
						title: `该规格【${selectedSpec.name}】暂无库存，请选择其他规格`,
						icon: 'none',
						duration: 2000
					});
					return;
				}

				list.forEach(item => {
					if (item.pid === pid) {
						this.$set(item, 'selected', false);
					}
				});

				this.$set(list[index], 'selected', true);
				//存储已选择
				/**
				 * 修复选择规格存储错误
				 * 将这几行代码替换即可
				 * 选择的规格存放在specSelected中
				 */
				this.specSelected = [];
				list.forEach(item => {
					if (item.selected === true) {
						this.specSelected.push(item);
					}
				});

				// 更新规格可用性
				this.updateSpecAvailability();
				this.changeSpecInfo();
			} catch (error) {
				console.error('选择规格出错:', error);
				uni.showToast({
					title: '选择规格出错',
					icon: 'none'
				});
			}
		},
		//领取优惠券
		addCoupon(coupon) {
			try {
				this.toggleCoupon();
				if (!coupon || !coupon.id) {
					throw new Error('优惠券数据无效');
				}

				// 检查登录状态
				if (!this.hasLogin) {
					uni.showToast({
						title: "请先登录",
						icon: "none"
					});
					return;
				}

				addMemberCoupon(coupon.id).then(response => {
					uni.showToast({
						title: '领取优惠券成功！',
						duration: 2000
					});
				}).catch(error => {
					console.error('领取优惠券出错:', error);

					// 提取具体的错误信息
					let errorMessage = '领取优惠券失败';
					if (error && error.data && error.data.message) {
						errorMessage = error.data.message;
					} else if (error && error.message) {
						errorMessage = error.message;
					} else if (typeof error === 'string') {
						errorMessage = error;
					}

					uni.showToast({
						title: errorMessage,
						icon: 'none',
						duration: 3000
					});
				});
			} catch (error) {
				console.error('添加优惠券出错:', error);

				// 提取具体的错误信息
				let errorMessage = '领取优惠券失败';
				if (error && error.data && error.data.message) {
					errorMessage = error.data.message;
				} else if (error && error.message) {
					errorMessage = error.message;
				} else if (typeof error === 'string') {
					errorMessage = error;
				}

				uni.showToast({
					title: errorMessage,
					icon: 'none',
					duration: 3000
				});
			}
		},
		//分享
		share() {
			this.$refs.share.toggleMask();
		},
		//收藏
		toFavorite() {
			if (!this.checkForLogin()) {
				return;
			}
			if (this.favorite) {
				//取消收藏
				deleteProductCollection({
					productId: this.product.id
				}).then(response => {
					uni.showToast({
						title: "取消收藏成功！",
						icon: 'none'
					});
					this.favorite = !this.favorite;
				});
			} else {
				//收藏
				let productCollection = {
					productId: this.product.id,
					productName: this.product.name,
					productPic: this.product.pic,
					productPrice: this.product.price,
					productSubTitle: this.product.subTitle
				}
				createProductCollection(productCollection).then(response => {
					uni.showToast({
						title: "收藏成功！",
						icon: 'none'
					});
					this.favorite = !this.favorite;
				});
			}
		},
		// 立即购买
		buy() {
			try {
				if (!this.checkForLogin()) {
					return;
				}

				// 检查是否有库存
				if (!this.hasAnyStock) {
					uni.showToast({
						title: "该商品暂无库存",
						icon: 'none',
						duration: 3000
					});
					return;
				}

				let productSkuStock = this.getSkuStock();
				if (!productSkuStock) {
					uni.showToast({
						title: "请选择完整规格",
						icon: 'none'
					});
					return;
				}

				// 准备商品信息
				const cartItem = {
					price: this.isFlash ? this.flashPrice : this.product.price,
					productAttr: productSkuStock.spData,
					productBrand: this.product.brandName,
					productCategoryId: this.product.productCategoryId,
					productId: this.product.id,
					productName: this.product.name,
					productPic: this.product.pic,
					productSkuCode: productSkuStock.skuCode,
					productSkuId: productSkuStock.id,
					productSn: this.product.productSn,
					productSubTitle: this.product.subTitle,
					purchaseType: (this.product.purchaseType === true || this.product.purchaseType === 1) ? 1 : 0,
					quantity: this.quantity
				};

				// 将商品信息转为字符串并进行编码
				const productInfo = encodeURIComponent(JSON.stringify(cartItem));

				// 根据购买类型设置isGift参数
				const isGift = this.buyType === 'friend' ? 1 : 0;

				// 构建跳转URL，包含地址信息
				let url = `/pages/order/createOrder?productInfo=${productInfo}&deliveryType=0&isGift=${isGift}`;

				// 如果是送给朋友，并且有礼物留言，传递礼物留言
				if (this.buyType === 'friend' && this.giftMessage) {
					const giftMessage = encodeURIComponent(this.giftMessage);
					url += `&giftMessage=${giftMessage}`;
				}

				// 如果有选中的地址，传递地址信息
				if (this.currentAddress && this.currentAddress.name) {
					const addressInfo = encodeURIComponent(JSON.stringify(this.currentAddress));
					url += `&addressInfo=${addressInfo}`;
				}

				uni.navigateTo({
					url: url
				});
			} catch (error) {
				console.error('立即购买出错:', error);
				uni.showToast({
					title: '下单失败，请稍后再试',
					icon: 'none'
				});
			}
		},
		stopPrevent() { },
		//设置头图信息
		initImgList() {
			try {
				if (!this.product.albumPics) {
					this.imgList = [{ src: this.product.pic || 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/default.png' }];
					return;
				}

				let tempPics = this.product.albumPics.split(',');
				tempPics.unshift(this.product.pic);
				for (let item of tempPics) {
					if (item != null && item != '') {
						this.imgList.push({
							src: item
						});
					}
				}

				// 确保至少有一张图片
				if (this.imgList.length === 0) {
					this.imgList = [{ src: 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/default.png' }];
				}
			} catch (error) {
				console.error('初始化图片列表出错:', error);
				this.imgList = [{ src: 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/default.png' }];
			}
		},
		//设置服务信息
		initServiceList() {
			try {
				if (!this.product.serviceIds) {
					this.serviceList = defaultServiceList.map(item => item.name);
					return;
				}

				for (let item of defaultServiceList) {
					if (this.product.serviceIds.indexOf(item.id) != -1) {
						this.serviceList.push(item.name);
					}
				}

				// 如果没有服务，设置默认服务
				if (this.serviceList.length === 0) {
					this.serviceList = defaultServiceList.map(item => item.name);
				}
			} catch (error) {
				console.error('初始化服务列表出错:', error);
				this.serviceList = defaultServiceList.map(item => item.name);
			}
		},
		//设置商品规格
		initSpecList(data) {
			try {
				if (!data.productAttributeList || !Array.isArray(data.productAttributeList) ||
					!data.productAttributeValueList || !Array.isArray(data.productAttributeValueList)) {
					console.warn('缺少商品属性数据或数据格式不正确');
					return;
				}

				for (let i = 0; i < data.productAttributeList.length; i++) {
					let item = data.productAttributeList[i];
					if (item.type == 0) {
						this.specList.push({
							id: item.id,
							name: item.name
						});
						if (item.handAddStatus == 1) {
							//支持手动新增的
							let valueList = data.productAttributeValueList;
							let filterValueList = valueList.filter(value => value.productAttributeId == item.id);
							if (filterValueList.length > 0) {
								let inputList = filterValueList[0].value.split(',');
								for (let j = 0; j < inputList.length; j++) {
									this.specChildList.push({
										pid: item.id,
										pname: item.name,
										name: inputList[j],
										available: false // 初始化时先设为不可用，后续根据库存更新
									});
								}
							}
						} else if (item.handAddStatus == 0) {
							//不支持手动新增的
							if (item.inputList) {
								let inputList = item.inputList.split(',');
								for (let j = 0; j < inputList.length; j++) {
									this.specChildList.push({
										pid: item.id,
										pname: item.name,
										name: inputList[j],
										available: false // 初始化时先设为不可用，后续根据库存更新
									});
								}
							}
						}
					}
				}

				// 根据库存设置规格可用性
				if (this.skuStockList && this.skuStockList.length > 0) {
					this.specChildList.forEach(specChild => {
						let isAvailable = false;

						// 检查是否存在有库存的SKU支持这个规格选项
						for (let sku of this.skuStockList) {
							try {
								// 检查该SKU是否包含当前规格选项
								let spDataArr = JSON.parse(sku.spData);
								let skuSpecs = {};
								spDataArr.forEach(sp => {
									skuSpecs[sp.key] = sp.value;
								});

								if (skuSpecs[specChild.pname] === specChild.name) {
									// 只要有库存就标记为可用
									if (sku.stock > 0) {
										isAvailable = true;
										break;
									}
								}
							} catch (e) {
								console.error('解析SKU数据出错:', e);
								continue;
							}
						}

						// 设置规格选项的可用状态
						this.$set(specChild, 'available', isAvailable);
					});

					// 不再过滤掉不可用的规格，让所有规格都显示
					// 库存为0的规格会显示为"缺货"状态，但仍然可见
					// this.specChildList = this.specChildList.filter(item => item.available);

					// 检查是否有任何库存
					this.checkAnyStock();
				}

				// 规格 默认选中第一条
				if (this.specList.length > 0 && this.specChildList.length > 0) {
					this.specList.forEach(item => {
						for (let cItem of this.specChildList) {
							if (cItem.pid === item.id) {
								this.$set(cItem, 'selected', true);
								this.specSelected.push(cItem);
								// 找到匹配的规格并设置图片
								if (this.skuStockList && this.skuStockList.length > 0) {
									// 设置选中的SKU图片
									this.changeSpecInfo();
								}
								break;
							}
						}
					});
					// 初始化后更新规格可用性
					this.updateSpecAvailability();
				}
			} catch (error) {
				console.error('初始化规格列表出错:', error);
			}
		},
		//设置商品参数
		initAttrList(data) {
			try {
				// 检查 productAttributeList 是否存在
				if (!data.productAttributeList || !Array.isArray(data.productAttributeList)) {
					console.warn('productAttributeList 不存在或不是数组');
					return;
				}

				// 检查 productAttributeValueList 是否存在
				if (!data.productAttributeValueList || !Array.isArray(data.productAttributeValueList)) {
					console.warn('productAttributeValueList 不存在或不是数组');
					return;
				}

				for (let item of data.productAttributeList) {
					if (item.type == 1) {
						let valueList = data.productAttributeValueList;
						let filterValueList = valueList.filter(value => value.productAttributeId == item.id);

						// 检查是否找到匹配的属性值
						if (filterValueList.length > 0 && filterValueList[0].value !== undefined) {
							let value = filterValueList[0].value;
							this.attrList.push({
								key: item.name,
								value: value
							});
						}
					}
				}
			} catch (error) {
				console.error('初始化商品参数出错:', error);
			}
		},
		//设置促销活动信息
		initPromotionTipList(data) {
			let promotionType = this.product.promotionType;
			if (promotionType == 0) {
				this.promotionTipList.push("暂无优惠");
			} else if (promotionType == 1) {
				this.promotionTipList.push("单品优惠");
			} else if (promotionType == 2) {
				this.promotionTipList.push("会员优惠");
			} else if (promotionType == 3) {
				this.promotionTipList.push("多买优惠");
				for (let item of data.productLadderList) {
					this.promotionTipList.push("满" + item.count + "件打" + item.discount * 10 + "折");
				}
			} else if (promotionType == 4) {
				this.promotionTipList.push("满减优惠");
				for (let item of data.productFullReductionList) {
					this.promotionTipList.push("满" + item.fullPrice + "元减" + item.reducePrice + "元");
				}
			} else if (promotionType == 5) {
				this.promotionTipList.push("限时优惠");
			}
		},
		//初始化商品详情信息
		initProductDesc() {
			try {
				if (!this.product.detailMobileHtml) {
					this.desc = '<div>暂无详细描述</div>';
					return;
				}

				let rawhtml = this.product.detailMobileHtml;
				// 微信小程序环境中不能使用document.createElement
				// 直接处理HTML字符串，添加样式属性
				let processedHtml = rawhtml.replace(/<img/gi, '<img style="width:100%;height:auto;display:block;"');
				this.desc = processedHtml;
			} catch (error) {
				console.error('初始化商品详情出错:', error);
				this.desc = '<div>商品详情加载失败</div>';
			}
		},
		//处理创建浏览记录
		handleReadHistory() {
			try {
				// 检查登录状态
				if (!this.hasLogin) {
					return;
				}

				if (this.product && this.product.id) {
					let data = {
						productId: this.product.id,
						productName: this.product.name,
						productPic: this.product.pic,
						productPrice: this.isFlash ? this.flashPrice : this.product.price,
						productSubTitle: this.product.subTitle,
					}
					createReadHistory(data).catch(err => {
						console.error('创建浏览记录失败:', err);
					});
				}
			} catch (error) {
				console.error('处理浏览记录出错:', error);
			}
		},
		//更新规格可用性（规格联动） - 支持多层级规格
		updateSpecAvailability() {
			try {
				if (!this.skuStockList || this.skuStockList.length === 0) {
					return;
				}

				// 获取当前已选择的规格
				let selectedSpecs = {};
				this.specSelected.forEach(spec => {
					selectedSpecs[spec.pname] = spec.name;
				});

				// 遍历所有规格选项，检查每个选项是否可用
				this.specChildList.forEach(specChild => {
					// 如果这个规格选项已经被选中，保持可用状态
					if (specChild.selected) {
						this.$set(specChild, 'available', true);
						return;
					}

					// 获取与当前检查规格不同维度的已选择规格
					let relevantSelectedSpecs = {};
					for (let specName in selectedSpecs) {
						// 排除与当前检查规格同一维度的已选择项
						if (specName !== specChild.pname) {
							relevantSelectedSpecs[specName] = selectedSpecs[specName];
						}
					}

					// 检查是否存在SKU支持这个规格选项
					let isAvailable = false;

					for (let sku of this.skuStockList) {
						try {
							// 检查库存
							if (sku.stock <= 0) {
								continue;
							}

							let spDataArr = JSON.parse(sku.spData);
							let skuSpecs = {};
							spDataArr.forEach(sp => {
								skuSpecs[sp.key] = sp.value;
							});

							// 检查该SKU是否包含所有相关的已选择规格（排除同维度的）
							let containsRelevantSpecs = true;
							for (let specName in relevantSelectedSpecs) {
								if (skuSpecs[specName] !== relevantSelectedSpecs[specName]) {
									containsRelevantSpecs = false;
									break;
								}
							}

							// 检查该SKU是否包含当前待检查的规格选项
							let containsCurrentSpec = skuSpecs[specChild.pname] === specChild.name;

							// 如果都满足且有库存，则该规格选项可用
							if (containsRelevantSpecs && containsCurrentSpec) {
								isAvailable = true;
								break;
							}
						} catch (e) {
							console.error('解析SKU数据出错:', e);
							continue;
						}
					}

					// 更新规格选项的可用状态
					this.$set(specChild, 'available', isAvailable);
				});

				// 更新整体库存状态
				this.checkAnyStock();
			} catch (error) {
				console.error('更新规格可用性出错:', error);
			}
		},

		/**
		 * 多层级规格联动说明（已修复同维度规格冲突问题）：
		 * 
		 * 假设有两个规格维度：颜色、尺寸
		 * SKU数据示例：
		 * [
		 *   {spData: '[{"key":"颜色","value":"黑"},{"key":"尺寸","value":"S"}]', stock: 14},
		 *   {spData: '[{"key":"颜色","value":"黑"},{"key":"尺寸","value":"M"}]', stock: 28},
		 *   {spData: '[{"key":"颜色","value":"黑"},{"key":"尺寸","value":"L"}]', stock: 38},
		 *   {spData: '[{"key":"颜色","value":"白"},{"key":"尺寸","value":"M"}]', stock: 27}
		 * ]
		 * 
		 * 场景1：用户选择了"黑色"，检查尺寸可用性
		 * - 检查"尺寸S"：relevantSelectedSpecs={颜色:黑}, 检查是否存在包含{颜色:黑, 尺寸:S}的SKU → 可用
		 * - 检查"尺寸M"：relevantSelectedSpecs={颜色:黑}, 检查是否存在包含{颜色:黑, 尺寸:M}的SKU → 可用
		 * - 检查"尺寸L"：relevantSelectedSpecs={颜色:黑}, 检查是否存在包含{颜色:黑, 尺寸:L}的SKU → 可用
		 * 
		 * 场景2：用户选择了"黑色"和"尺寸M"，检查颜色可用性
		 * - 检查"白色"：relevantSelectedSpecs={尺寸:M}, 检查是否存在包含{尺寸:M, 颜色:白}的SKU → 可用
		 * 
		 * 关键修复：排除同维度规格冲突
		 * - 当检查"尺寸M"时，不会包含已选的"尺寸S"，避免要求SKU同时包含S和M的矛盾
		 * - 只考虑不同维度的已选规格作为约束条件
		 */
		//当商品规格改变时，修改商品信息
		changeSpecInfo() {
			try {
				let skuStock = this.getSkuStock();
				if (skuStock != null) {
					// 更新SKU图片
					if (skuStock.pic) {
						this.selectedSkuPic = skuStock.pic;
					}

					// 如果是秒杀商品，不修改product.price，只更新商品库存
					if (this.isFlash) {
						this.originalProductPrice = skuStock.price; // 更新原价
						this.product.stock = skuStock.stock;
					} else {
						// SKU的price字段已经是促销后的价格（后端已处理）
						// 直接使用SKU价格，不需要再判断promotionType
						this.product.originalPrice = skuStock.price;
						this.product.price = skuStock.price;
						this.product.stock = skuStock.stock;

						console.log('更新SKU价格信息:', {
							skuPrice: skuStock.price,
							productPrice: this.product.price,
							promotionType: this.product.promotionType
						});
					}
				}
			} catch (error) {
				console.error('更新规格信息出错:', error);
			}
		},
		//获取当前选中商品的SKU
		getSkuStock() {
			try {
				if (!this.skuStockList || this.skuStockList.length === 0 || !this.specSelected || this.specSelected.length === 0) {
					return null;
				}

				for (let i = 0; i < this.skuStockList.length; i++) {
					try {
						let spDataArr = JSON.parse(this.skuStockList[i].spData);
						let availAbleSpecSet = new Map();
						for (let j = 0; j < spDataArr.length; j++) {
							availAbleSpecSet.set(spDataArr[j].key, spDataArr[j].value);
						}
						let correctCount = 0;
						for (let item of this.specSelected) {
							let value = availAbleSpecSet.get(item.pname);
							if (value != null && value == item.name) {
								correctCount++;
							}
						}
						if (correctCount == this.specSelected.length) {
							return this.skuStockList[i];
						}
					} catch (e) {
						console.error('解析SKU数据出错:', e);
						continue;
					}
				}
				return null;
			} catch (error) {
				console.error('获取SKU出错:', error);
				return null;
			}
		},
		//将商品加入到购物车
		addToCart() {
			try {
				if (!this.checkForLogin()) {
					return;
				}

				// 检查是否有库存
				if (!this.hasAnyStock) {
					uni.showToast({
						title: "该商品暂无库存",
						icon: 'none',
						duration: 3000
					});
					return;
				}

				let productSkuStock = this.getSkuStock();
				if (!productSkuStock) {
					uni.showToast({
						title: "请选择完整规格",
						icon: 'none'
					});
					return;
				}

				let cartItem = {
					price: this.isFlash ? this.flashPrice : this.product.price,
					productAttr: productSkuStock.spData,
					productBrand: this.product.brandName,
					productCategoryId: this.product.productCategoryId,
					productId: this.product.id,
					productName: this.product.name,
					productPic: this.product.pic,
					productSkuCode: productSkuStock.skuCode,
					productSkuId: productSkuStock.id,
					productSn: this.product.productSn,
					productSubTitle: this.product.subTitle,
					purchaseType: (this.product.purchaseType === true || this.product.purchaseType === 1) ? 1 : 0,
					quantity: this.quantity
				};

				addCartItem(cartItem).then(response => {
					uni.showToast({
						title: response.message || '已加入购物车',
						icon: 'success',
						duration: 1500
					});
					this.specClass = 'hide';
					setTimeout(() => {
						this.specClass = 'none';
					}, 250);
					this.getCartItemCount();
				}).catch(error => {
					console.error('添加购物车失败:', error);

					// 提取具体的错误信息
					let errorMessage = '加入购物车失败';
					if (error && error.data && error.data.message) {
						errorMessage = error.data.message;
					} else if (error && error.message) {
						errorMessage = error.message;
					} else if (typeof error === 'string') {
						errorMessage = error;
					}

					uni.showToast({
						title: errorMessage,
						icon: 'none',
						duration: 3000
					});
				});
			} catch (error) {
				console.error('添加购物车出错:', error);

				// 提取具体的错误信息
				let errorMessage = '加入购物车失败';
				if (error && error.data && error.data.message) {
					errorMessage = error.data.message;
				} else if (error && error.message) {
					errorMessage = error.message;
				} else if (typeof error === 'string') {
					errorMessage = error;
				}

				uni.showToast({
					title: errorMessage,
					icon: 'none',
					duration: 3000
				});
			}
		},
		//检查登录状态并弹出登录框
		checkForLogin() {
			if (!this.hasLogin) {
				uni.showModal({
					title: '提示',
					content: '你还没登录，是否要登录？',
					confirmText: '去登录',
					cancelText: '取消',
					success: (res) => {
						if (res.confirm) {
							// 使用Vuex统一管理登录弹窗状态
							this.$store.commit('setLoginPopup', { show: true, reason: 'unauthorized' });
							setTimeout(() => {
								uni.switchTab({
									url: '/pages/new_index/index'
								});
							}, 100);
						} else if (res.cancel) {
							console.log('用户点击取消');
						}
					}
				});
				return false;
			} else {
				return true;
			}
		},
		//初始化收藏状态
		initProductCollection() {
			if (this.hasLogin) {
				productCollectionDetail({
					productId: this.product.id
				}).then(response => {
					this.favorite = response.data != null;
				});
			}
		},
		// 修改数量
		decreaseQuantity() {
			if (this.quantity > 1) {
				this.quantity--;
			}
		},
		increaseQuantity() {
			this.quantity++;
		},
		validateQuantity(e) {
			let val = parseInt(e.detail.value);
			if (isNaN(val) || val < 1) {
				this.quantity = 1;
			} else {
				this.quantity = val;
			}
		},
		// 获取购物车数量
		getCartItemCount() {
			try {
				if (this.hasLogin) {
					fetchCartList().then(res => {
						this.cartCount = res.data && res.data.length || 0;
					}).catch(err => {
						console.error('获取购物车数量失败:', err);
						this.cartCount = 0;
					});
				}
			} catch (error) {
				console.error('获取购物车数量出错:', error);
				this.cartCount = 0;
			}
		},
		// 送礼功能
		handleGift() {
			try {
				this.currentAction = 'gift';

				// 确保打开弹窗时根据已选规格更新图片
				let skuStock = this.getSkuStock();
				if (skuStock && skuStock.pic) {
					this.selectedSkuPic = skuStock.pic;
				} else {
					// 如果没有选择完整规格，显示默认图片
					this.selectedSkuPic = this.product.pic;
				}

				this.specClass = 'show';
			} catch (error) {
				console.error('送礼功能出错:', error);
				uni.showToast({
					title: '送礼功能暂时不可用',
					icon: 'none'
				});
			}
		},
		confirmGift() {
			try {
				if (!this.checkForLogin()) {
					return;
				}

				// 检查是否有库存
				if (!this.hasAnyStock) {
					uni.showToast({
						title: "该商品暂无库存",
						icon: 'none',
						duration: 3000
					});
					return;
				}

				let productSkuStock = this.getSkuStock();
				if (!productSkuStock) {
					uni.showToast({
						title: "请选择完整规格",
						icon: 'none'
					});
					return;
				}

				// 获取已选规格文本，用于展示
				let selectedSpecsText = '';
				if (this.specSelected && this.specSelected.length > 0) {
					selectedSpecsText = this.specSelected.map(item => item.name).join(', ');
				}

				// 构建单个商品数据
				const singleGiftItem = {
					id: this.product.id,
					name: this.product.name,
					price: this.isFlash ? this.flashPrice : this.product.price,
					pic: this.product.pic,
					quantity: this.quantity,
					productSkuId: productSkuStock.id,
					productAttr: productSkuStock.spData,
					productSkuCode: productSkuStock.skuCode,
					selectedSpecs: selectedSpecsText,
					productSn: this.product.productSn || '',
					productSubTitle: this.product.subTitle || '',
					productCategoryId: this.product.productCategoryId || 0,
					productBrand: this.product.brandName || '',
					source: 'product' // 标识来源为商品详情页
				};

				// 保存单个商品数据
				uni.setStorageSync('singleGiftData', JSON.stringify(singleGiftItem));

				// 关闭规格选择框
				this.specClass = 'hide';
				setTimeout(() => {
					this.specClass = 'none';

					// 显示添加成功提示
					uni.showToast({
						title: '已添加到礼品袋',
						icon: 'success',
						duration: 1500
					});

					// 延迟跳转到礼品祝福页面
					setTimeout(() => {
						uni.navigateTo({
							url: '/pages/gift-bag/gift-wishes'
						});
					}, 1500);
				}, 250);
			} catch (error) {
				console.error('确认送礼出错:', error);
				uni.showToast({
					title: '添加到礼品袋失败',
					icon: 'none'
				});
			}
		},
		// 切换商品推荐标签
		switchProductTab(index) {
			this.currentProductTab = index;
		},

		// 跳转到商品详情页
		navigateToProduct(product) {
			let id = product.id || product.productId;
			if (id) {
				uni.navigateTo({
					url: `/pages/product/product?id=${id}`
				});
			}
		},
		// 处理促销点击
		handlePromoClick() {
			try {
				// 检查登录状态
				if (!this.hasLogin) {
					uni.showToast({
						title: "请先登录",
						icon: "none"
					});
					return;
				}

				if (this.couponList && this.couponList.length > 0) {
					// 直接显示弹窗，数据已在fetchCoupons中预设
					this.showCouponPopup = true;
				} else {
					// 尝试重新获取优惠券
					this.fetchCoupons().then(() => {
						if (this.couponList && this.couponList.length > 0) {
							this.showCouponPopup = true;
						} else {
							uni.showToast({
								title: '暂无可用优惠券',
								icon: 'none'
							});
						}
					});
				}
			} catch (error) {
				console.error('处理促销点击出错:', error);
				uni.showToast({
					title: '优惠信息暂时不可用',
					icon: 'none'
				});
			}
		},
		// 关闭优惠券弹窗
		closeCouponPopup() {
			this.showCouponPopup = false;
		},
		// 领取优惠券
		handleGetCoupon() {
			// 检查登录状态
			if (!this.hasLogin) {
				uni.showToast({
					title: "请先登录",
					icon: "none"
				});
				return;
			}

			// 这里修改为直接领取第一张优惠券
			if (this.couponList && this.couponList.length > 0) {
				this.addCoupon(this.couponList[0]);
			} else {
				uni.showToast({
					title: '暂无可用优惠券',
					icon: 'none'
				});
			}
		},
		// 跳转到首页
		navToHome() {
			uni.switchTab({
				url: '/pages/new_index/index'
			});
		},
		// 跳转到购物车
		navToCart() {
			uni.navigateTo({
				url: '/pages/cart/cart'
			});
		},
		// 轮播图切换事件
		onSwiperChange(e) {
			this.currentImageIndex = e.detail.current;
		},
		// 预览图片
		previewImage(index) {
			try {
				if (!this.imgList || this.imgList.length === 0) {
					return;
				}

				// 构建预览图片URL数组
				const previewUrls = this.imgList.map(item => item.src);
				const currentImage = this.imgList[index] ? this.imgList[index].src : previewUrls[0];

				uni.previewImage({
					current: currentImage, // 当前显示图片的http链接
					urls: previewUrls, // 需要预览的图片http链接列表
					success: function (res) {
						console.log('预览图片成功');
					},
					fail: function (err) {
						console.error('预览图片失败:', err);
						uni.showToast({
							title: '图片预览失败',
							icon: 'none'
						});
					}
				});
			} catch (error) {
				console.error('预览图片出错:', error);
				uni.showToast({
					title: '图片预览失败',
					icon: 'none'
				});
			}
		},
		// 跳转到评价页
		navToReview() {
			uni.navigateTo({
				url: `/pages/review/review?id=${this.product.id}`
			});
		},
		// 跳转到品牌详情页
		navToBrandDetail() {
			let id = this.brand.id;
			uni.navigateTo({
				url: `/pages/brand/brandDetail?id=${id}`
			})
		},
		// 选择分享方式
		shareSelect(index) {
			if (index === 0) {
				// 分享给朋友
			} else if (index === 1) {
				// 分享到朋友圈
			} else if (index === 2) {
				// 分享给QQ好友
			} else if (index === 3) {
				// 分享到QQ空间
			}
		},
		// 添加获取用户名的方法
		getUserName() {
			try {
				const userInfoStr = uni.getStorageSync('userInfo');
				// 检查是否为字符串且不为空
				if (userInfoStr && typeof userInfoStr === 'string') {
					const userInfo = JSON.parse(userInfoStr);
					return userInfo && userInfo.nickname ? userInfo.nickname : '广横走文创';
				} else if (userInfoStr && typeof userInfoStr === 'object') {
					// 已经是对象，直接使用
					return userInfoStr.nickname || '广横走文创';
				}
				return '广横走文创'; // 默认值
			} catch (error) {
				console.error('获取用户名出错:', error);
				return '广横走文创'; // 发生错误时返回默认值
			}
		},
		// 启用分享到朋友圈功能
		enableShareTimeline() {
			// #ifdef MP-WEIXIN
			wx.showShareMenu({
				withShareTicket: true,
				menus: ['shareAppMessage', 'shareTimeline'],
				success: () => {
					console.log('已启用分享到朋友圈功能');
				},
				fail: (err) => {
					console.error('启用分享到朋友圈功能失败', err);
				}
			});
			// #endif
		},
		// 预览规格图片
		previewSpecImage() {
			try {
				// 构建预览图片数组，包含主图和所有SKU图片
				let previewUrls = [];

				// 添加当前选中的SKU图片作为首张
				const currentImage = this.selectedSkuPic || this.product.pic;
				if (currentImage) {
					previewUrls.push(currentImage);
				}

				// 添加商品主图（如果与当前图片不同）
				if (this.product.pic && this.product.pic !== currentImage) {
					previewUrls.push(this.product.pic);
				}

				// 添加所有轮播图片（如果与已有图片不同）
				if (this.imgList && this.imgList.length > 0) {
					this.imgList.forEach(img => {
						if (img.src && !previewUrls.includes(img.src)) {
							previewUrls.push(img.src);
						}
					});
				}

				// 添加其他SKU的图片（如果与已有图片不同）
				if (this.skuStockList && this.skuStockList.length > 0) {
					this.skuStockList.forEach(sku => {
						if (sku.pic && !previewUrls.includes(sku.pic)) {
							previewUrls.push(sku.pic);
						}
					});
				}

				// 如果没有图片可预览，则使用默认图片
				if (previewUrls.length === 0) {
					previewUrls = ['https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/default.png'];
				}

				// 调用微信小程序预览图片功能
				uni.previewImage({
					current: currentImage, // 当前显示图片的http链接
					urls: previewUrls, // 需要预览的图片http链接列表
					success: function (res) {
						console.log('预览图片成功');
					},
					fail: function (err) {
						console.error('预览图片失败:', err);
						uni.showToast({
							title: '图片预览失败',
							icon: 'none'
						});
					}
				});
			} catch (error) {
				console.error('预览规格图片出错:', error);
				uni.showToast({
					title: '图片预览失败',
					icon: 'none'
				});
			}
		},

		// 获取DIY价格
		getDIYPrice() {
			try {
				return (parseFloat(this.product.price || 0) * 1.5).toFixed(2);
			} catch (error) {
				console.error('计算DIY价格出错:', error);
				return '0.00';
			}
		},

		// 获取DIY高价格
		getDIYPriceHigh() {
			try {
				return (parseFloat(this.product.price || 0) * 2).toFixed(2);
			} catch (error) {
				console.error('计算DIY高价格出错:', error);
				return '0.00';
			}
		},

		// 初始化展示用属性列表
		initDisplayAttrList(data) {
			try {
				this.displayAttrList = [];

				if (!data.productAttributeList || !Array.isArray(data.productAttributeList)) {
					console.warn('productAttributeList 不存在或不是数组');
					return;
				}

				if (!data.productAttributeValueList || !Array.isArray(data.productAttributeValueList)) {
					console.warn('productAttributeValueList 不存在或不是数组');
					return;
				}
				// 处理所有属性
				for (let item of data.productAttributeList) {
					let valueList = data.productAttributeValueList;
					let filterValueList = valueList.filter(value => value.productAttributeId == item.id);
					if (filterValueList.length > 0 && filterValueList[0].value !== undefined) {
						let value = filterValueList[0].value;
						this.displayAttrList.push({
							key: item.name,
							value: value
						});
					}
				}
			} catch (error) {
				console.error('初始化展示属性列表出错:', error);
			}
		},



		// 联系客服
		contactService() {
			console.log('contactService 方法被调用');
			try {
				console.log('准备跳转到客服页面');
				uni.navigateTo({
					url: '/pages/customerService/index',
					success: () => {
						console.log('跳转成功');
					},
					fail: (error) => {
						console.error('跳转失败:', error);
						uni.showToast({
							title: '跳转失败: ' + error.errMsg,
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

		// 推广商品
		handlePromotion() {
			try {
				if (!this.checkForLogin()) {
					return;
				}

				// 基于现有的 shareProduct 添加推广参数，避免重复设置
				this.shareProduct = {
					...this.shareProduct, // 使用已有的完整商品信息
					// 覆盖推广相关的特定参数
					recommended: true, // 推广商品标记为推荐
					distributorId: this.distributorId, // 添加推广参数
					isPromotion: true // 标记为推广模式
				};

				// 显示分享组件
				this.$refs.share.toggleMask();
			} catch (error) {
				console.error('推广商品出错:', error);
				uni.showToast({
					title: '推广失败，请稍后再试',
					icon: 'none'
				});
			}
		},

		// DIY定制
		async handleDIY() {
			try {
				if (!this.checkForLogin()) {
					return;
				}

				// 检查商品是否有 diyTemplateId
				if (!this.product.diyTemplateId) {
					uni.showToast({
						title: '该商品暂未配置定制模板',
						icon: 'none'
					});
					return;
				}

				// 直接跳转到 customize 页面，传递产品ID和模板ID
				// 不传递风格模型ID，让画板初始化为空白状态
				const params = {
					productId: this.product.id,
					templateId: this.product.diyTemplateId
				};

				const queryString = Object.keys(params)
					.map(key => `${key}=${encodeURIComponent(params[key])}`)
					.join('&');

				console.log('跳转DIY定制页面，参数:', params);

				uni.navigateTo({
					url: `/subpackages/diy/customize?${queryString}`
				});
			} catch (error) {
				console.error('DIY定制出错:', error);
				uni.showToast({
					title: '跳转定制页面失败',
					icon: 'none'
				});
			}
		},

		// 加载DIY数据
		async loadDIYData() {
			try {
				// 并行获取DIY配置和模板信息
				const [configResult, templateResult] = await Promise.all([
					getDIYProductConfig(this.product.id),
					getDIYProductTemplate(this.product.id)
				]);

				if (configResult && configResult.code === 200) {
					this.diyConfig = configResult.data;
				}

				if (templateResult && templateResult.code === 200) {
					this.diyTemplate = templateResult.data;
					// 设置可定制面列表
					if (this.diyTemplate && this.diyTemplate.customizeFaces) {
						this.diyCustomizeFaces = this.diyTemplate.customizeFaces;
					}
				}
			} catch (error) {
				console.error('加载DIY数据失败:', error);
				throw error;
			}
		},

		// 关闭DIY弹窗
		closeDIYPopup() {
			this.showDIYPopup = false;
		},

		// 开始定制
		handleStartCustomize(data) {
			try {
				// 检查是否有DIY模板信息
				if (!this.diyTemplate || !this.diyTemplate.id) {
					uni.showToast({
						title: '商品DIY配置异常，请稍后再试',
						icon: 'none'
					});
					return;
				}

				// 跳转到DIY定制页面，包含templateId
				const params = {
					productId: data.productId,
					templateId: this.diyTemplate.id, // 添加templateId
					selectedFaces: JSON.stringify(data.selectedFaces),
					selectedIndexes: JSON.stringify(data.selectedIndexes)
				};

				const queryString = Object.keys(params)
					.map(key => `${key}=${encodeURIComponent(params[key])}`)
					.join('&');

				console.log('跳转DIY定制页面，参数:', params);

				uni.navigateTo({
					url: `/subpackages/diy/customize?${queryString}`
				});
			} catch (error) {
				console.error('跳转定制页面失败:', error);
				uni.showToast({
					title: '跳转失败，请稍后再试',
					icon: 'none'
				});
			}
		},



		// 更新运费描述
		updateFreightDescription(freightTemplateInfo) {
			try {
				if (freightTemplateInfo && freightTemplateInfo.freightDescription) {
					this.freightDescription = freightTemplateInfo.freightDescription;
				} else {
					// 如果没有运费模板信息，使用默认描述
					this.freightDescription = '快递：免运费，可加顺丰';
				}
			} catch (error) {
				console.error('更新运费描述出错:', error);
				this.freightDescription = '快递：免运费，可加顺丰'; // 出错时使用默认值
			}
		},

		// 新增：获取用户默认地址
		async getDefaultAddress() {
			try {
				if (!this.hasLogin) {
					return;
				}

				const result = await fetchAddressList();
				if (result && result.code === 200 && result.data && result.data.length > 0) {
					this.addressList = result.data;
					// 查找默认地址
					const defaultAddress = this.addressList.find(item => item.defaultStatus === 1);
					if (defaultAddress) {
						this.currentAddress = defaultAddress;
					} else {
						// 如果没有默认地址，使用第一个地址
						this.currentAddress = this.addressList[0];
					}
				}
			} catch (error) {
				console.error('获取默认地址失败:', error);
			}
		},

		// 新增：切换购买类型
		switchBuyType(type) {
			this.buyType = type;
			if (type === 'myself') {
				this.giftMessage = '';
			}
		},

		// 新增：跳转到地址管理页面
		toAddressManage() {
			uni.navigateTo({
				url: '/pages/address/address?source=1'
			});
		},

		// 新增：获取地址显示格式
		getAddressDisplay() {
			if (this.currentAddress) {
				return this.currentAddress.province + ' ' + this.currentAddress.city + ' ' + this.currentAddress.region + ' ' + this.currentAddress.detailAddress;
			}
			return '用户地址XXXXXXXXXX';
		},

		// 新增：获取预计送达日期
		getExpectedDeliveryDate() {
			const now = new Date();
			const deliveryDate = new Date(now.getTime() + 2 * 24 * 60 * 60 * 1000); // 当前时间加2天
			const month = deliveryDate.getMonth() + 1;
			const day = deliveryDate.getDate();
			return `${month}月${day}日`;
		},

		// 检查是否有任何库存
		checkAnyStock() {
			try {
				this.hasAnyStock = false;

				if (this.skuStockList && this.skuStockList.length > 0) {
					// 检查是否有任何SKU有库存
					for (let sku of this.skuStockList) {
						if (sku.stock > 0) {
							this.hasAnyStock = true;
							break;
						}
					}
				}

				// 如果没有任何库存，也检查规格列表是否为空
				if (!this.hasAnyStock && this.specChildList.length === 0) {
					this.hasAnyStock = false;
				}
			} catch (error) {
				console.error('检查库存状态出错:', error);
				this.hasAnyStock = true; // 出错时默认为有库存，避免误报
			}
		}
	},
	// 小程序分享
	onShareAppMessage() {
		try {
			return {
				title: this.product.name || '精选商品',
				path: `/pages/product/product?id=${this.product.id}`,
				imageUrl: this.imgList.length > 0 ? this.imgList[0].src : ''
			};
		} catch (error) {
			console.error('分享消息配置出错:', error);
			return {
				title: '精选商品',
				path: '/pages/new_index/index'
			};
		}
	}
};
</script>

<style lang="scss">
@charset "UTF-8";

/* 页面左右间距 */
/* 文字尺寸 */
/*文字颜色*/
/* 边框颜色 */
/* 图片加载中颜色 */
/* 行为相关颜色 */
page {
	background: #f8f8f8;
	padding-bottom: 160rpx;
}

.icon-you {
	font-size: 24rpx;
	color: rgba(51, 51, 51, 0.5);
}

.carousel {
	width: 100%;
	height: 0;
	padding-bottom: 100%; /* 使用padding-bottom实现1:1比例 */
	position: relative;

	swiper {
		position: absolute;
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
	}

	.image-wrapper {
		width: 100%;
		height: 100%;
	}

	.swiper-item {
		display: flex;
		justify-content: center;
		align-content: center;
		width: 100%;
		height: 100%;
		overflow: hidden;

		image {
			width: 100%;
			height: 100%;
		}
	}
	
	/* 售罄蒙版 */
	.sold-out-overlay {
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
		pointer-events: none;
		
		.sold-out-text {
			color: #ffffff;
			font-size: 64rpx;
			font-weight: bold;
			text-shadow: 0 4rpx 8rpx rgba(0, 0, 0, 0.3);
		}
	}
}

/* 标题简介 */
.introduce-section {
	background: #FFFFFF;
	background-size: cover;
	width: 100%;
	height: auto;
	padding: 30rpx 30rpx;

	.title {
		font-size: 42rpx;
		color: #000000;
		font-weight: bold;
		line-height: 1.3;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 2;
		overflow: hidden;
		text-overflow: ellipsis;
		margin-bottom: 16rpx;
	}

	.tags-box {
		display: flex;
		flex-wrap: wrap;
		margin: 20rpx 0;
	}

	.price-tag {
		background: #FFF8E5;
		border: 1px solid #FFECB6;
		border-radius: 8rpx;
		padding: 6rpx 12rpx;
		font-size: 24rpx;
		color: #F19F34;
		margin-right: 15rpx;
	}

	.points-tag {
		background: #FFEAE5;
		border: 1px solid #FFD3C5;
		border-radius: 8rpx;
		padding: 6rpx 12rpx;
		font-size: 24rpx;
		color: #F15B34;
		margin-right: 15rpx;
	}

	.seconds-tag {
		background: #FFDDE5;
		border: 1px solid #FFBDCB;
		border-radius: 8rpx;
		padding: 6rpx 12rpx;
		font-size: 24rpx;
		color: #FF3366;
		margin-right: 15rpx;
	}

	.price-box {
		margin-top: 20rpx;
	}

	.price-container {
		display: flex;
		justify-content: space-between;
		margin-top: 20rpx;
		gap: 20rpx;
	}

	.price-left {
		flex: 1;
		display: flex;
		background: rgba(228, 255, 118, 0.2);
		border-radius: 8rpx;
		padding: 20rpx;
		flex-direction: column;
	}

	.price-right {
		flex: 1;
		display: flex;
		padding: 20rpx;
		flex-direction: column;
		align-items: flex-start;
	}

	.buy-price-tag {
		display: flex;
		align-items: baseline;
		margin-bottom: 10rpx;
	}

	.buy-price-label {
		font-size: 24rpx;
		color: #647D00;
		margin-right: 8rpx;
	}

	.buy-price-symbol {
		font-size: 28rpx;
		color: #647D00;
		font-weight: bold;
		margin-right: 4rpx;
	}

	.buy-price {
		font-size: 42rpx;
		color: #647D00;
		font-weight: bold;
		line-height: 1;
	}

	.buy-price-desc {
		font-size: 22rpx;
		color: #999999;
		line-height: 1.2;
	}

	.price-row {
		display: flex;
		align-items: center;
		gap: 16rpx;
	}

	.current-price-container {
		display: flex;
		align-items: baseline;
	}

	.original-price {
		font-size: 28rpx;
		color: #999999;
		text-decoration: line-through;
	}

	.diy-price-range {
		display: flex;
		align-items: baseline;
		justify-content: flex-end;
		margin-bottom: 10rpx;
	}

	.diy-range-symbol {
		font-size: 24rpx;
		color: #666666;
		margin-right: 2rpx;
	}

	.diy-range-price {
		font-size: 42rpx;
		color: #666666;
		font-weight: 500;
	}

	.diy-range-separator {
		font-size: 24rpx;
		color: #666666;
		margin: 0 4rpx;
	}

	.diy-price-desc {
		font-size: 22rpx;
		color: #999999;
		text-align: right;
		line-height: 1.2;
	}
}

/* 发货信息 */
.shipping-section {
	background-color: #ffffff;
	margin-top: 20rpx;
	padding: 30rpx 30rpx;
	border-radius: 12rpx;
	border-bottom: 1px solid #f5f5f5;

	.shipping-row {
		display: flex;
		align-items: center;
	}

	.shipping-icon {
		width: 36rpx;
		height: 36rpx;
		margin-right: 20rpx;
		flex-shrink: 0;
	}

	.shipping-info {
		display: flex;
		flex-direction: column;
		flex: 1;
	}

	.shipping-location {
		font-size: 26rpx;
		color: #000000;
		font-weight: 500;
		margin-bottom: 8rpx;
	}

	.shipping-desc {
		font-size: 24rpx;
		color: #666666;
	}
}

/* 商品属性 */
.attribute-section {
	background-color: #ffffff;
	padding: 0rpx 30rpx;
	border-radius: 12rpx;
	border-bottom: 1px solid #f5f5f5;

	.attr-header {
		display: flex;
		align-items: center;
		padding: 30rpx 0;
	}

	.attr-icon {
		width: 36rpx;
		height: 36rpx;
		margin-right: 20rpx;
		flex-shrink: 0;
	}

	.attr-content {
		flex: 1;
		display: flex;
		flex-direction: column;
		justify-content: center;
	}

	.attr-row {
		display: flex;
		justify-content: space-between;
		margin-bottom: 8rpx;
	}

	.attr-row:last-child {
		margin-bottom: 0;
	}

	.attr-name {
		flex: 1;
		font-size: 26rpx;
		color: #000000;
		font-weight: 500;
		text-align: left;
	}

	.attr-value {
		flex: 1;
		font-size: 24rpx;
		color: #666666;
		text-align: left;
	}
}

.icon-arrow {
	font-size: 24rpx;
	color: #cccccc;
	margin-left: 10rpx;
	align-self: center;
}

/* 商品服务信息 */
.service-section {
	background-color: #ffffff;
	margin-top: 20rpx;
	padding: 30rpx 30rpx;
	border-radius: 12rpx;
	border-top: 1px solid #f5f5f5;
	border-bottom: 1px solid #f5f5f5;
}

.service-row,
.insurance-row {
	display: flex;
	align-items: center;
	padding: 10rpx 0;
}

.insurance-row {
	margin-top: 30rpx;
}

.service-icon {
	width: 36rpx;
	height: 36rpx;
	margin-right: 20rpx;
	flex-shrink: 0;
}

.service-items {
	display: flex;
	align-items: center;
	flex: 1;
}

.service-item {
	font-size: 24rpx;
	color: #000000;
	font-weight: 400;
}

.divider {
	margin: 0 10rpx;
	color: #cccccc;
	font-size: 24rpx;
}

.insurance-text {
	font-size: 24rpx;
	color: #000000;
	flex: 1;
	font-weight: 400;
}

/* 分享 */
.share-section {
	display: flex;
	align-items: center;
	color: #606266;
	background: linear-gradient(left, #fdf5f6, #fbebf6);
	padding: 12rpx 30rpx;

	.share-icon {
		display: flex;
		align-items: center;
		width: 70rpx;
		height: 30rpx;
		line-height: 1;
		border: 1px solid #FF4225;
		border-radius: 4rpx;
		position: relative;
		overflow: hidden;
		font-size: 22rpx;
		color: #FF4225;

		&:after {
			content: "";
			width: 50rpx;
			height: 50rpx;
			border-radius: 50%;
			left: -20rpx;
			top: -12rpx;
			position: absolute;
			background: #FF4225;
		}
	}

	.icon-xingxing {
		position: relative;
		z-index: 1;
		font-size: 24rpx;
		margin-left: 2rpx;
		margin-right: 10rpx;
		color: #fff;
		line-height: 1;
	}

	.tit {
		font-size: 28rpx;
		margin-left: 10rpx;
	}

	.icon-bangzhu1 {
		padding: 10rpx;
		font-size: 30rpx;
		line-height: 1;
	}

	.share-btn {
		flex: 1;
		text-align: right;
		font-size: 24rpx;
		color: #FF4225;
	}

	.icon-you {
		font-size: 24rpx;
		margin-left: 4rpx;
		color: #FF4225;
	}
}

.c-list {
	font-size: 24rpx;
	color: #000000;
	background: #fff;
	margin-top: 10rpx;

	.c-row {
		display: flex;
		align-items: center;
		padding: 40rpx 30rpx;
		position: relative;
	}

	.list-icon {
		width: 28rpx;
		height: 28rpx;
		margin: 0 15rpx;
	}

	.tit {
		width: 140rpx;
	}

	.con {
		flex: 1;
		color: #000000;
		display: flex;
		margin-right: 15rpx;
		/* 使用 flex 布局 */
		justify-content: flex-end;
		/* 子元素右对齐 */

		.selected-text {
			margin-right: 10rpx;
		}
	}

	.bz-list {
		flex: 1;
		background-color: rgba(45, 255, 245, 0.05);
		font-size: 26rpx;
		display: flex;
		align-items: center;
		color: #333333;
		height: 94rpx;

		.bz-list-icon {
			width: 24rpx;
			height: 24rpx;
			margin: 0 10rpx;
			flex-shrink: 0;
		}

		text {
			display: inline-block;
			margin-right: 30rpx;
		}
	}

	.con-list {
		flex: 1;
		display: flex;
		flex-direction: column;
		color: #333333;
		line-height: 40rpx;
	}

	.red {
		color: #FF4225;
	}
}

/* 评价 */
.eva-section {
	display: flex;
	flex-direction: column;
	padding: 20rpx 30rpx;
	background: #fff;
	margin-top: 16rpx;

	.e-header {
		display: flex;
		align-items: center;
		height: 70rpx;
		font-size: 26rpx;
		color: #909399;

		.tit {
			font-size: 30rpx;
			color: #333333;
			margin-right: 4rpx;
		}

		.tip {
			flex: 1;
			text-align: right;
		}

		.icon-you {
			margin-left: 10rpx;
		}
	}
}

.eva-box {
	display: flex;
	padding: 20rpx 0;

	.portrait {
		flex-shrink: 0;
		width: 80rpx;
		height: 80rpx;
		border-radius: 100px;
	}

	.right {
		flex: 1;
		display: flex;
		flex-direction: column;
		font-size: 28rpx;
		color: #606266;
		padding-left: 26rpx;

		.con {
			font-size: 28rpx;
			color: #333333;
			padding: 20rpx 0;
		}

		.bot {
			display: flex;
			justify-content: space-between;
			font-size: 24rpx;
			color: #909399;
		}
	}
}

/*  详情 */
.detail-desc {
	background: #fff;
	margin-top: 16rpx;
	padding: 0 30rpx 30rpx;

	.d-header {
		display: flex;
		justify-content: flex-start;
		align-items: center;
		height: 80rpx;
		font-size: 32rpx;
		color: #000000;
		font-weight: 500;
		position: relative;
		margin-bottom: 20rpx;

		text {
			padding: 0 20rpx;
			background: #fff;
			position: relative;
			z-index: 1;
		}
	}

	rich-text {
		width: 100%;
		overflow: hidden;
	}

	.desc-image {
		width: 100%;

		image {
			width: 100%;
			height: 100%;
			object-fit: cover;
			/* 确保图片充满容器，可能需要根据平台兼容性调整 */
			display: block;
			/* 避免 inline-block 图片出现间隙 */
		}
	}

	._img {
		width: 100%;
		height: auto;
	}
}

/* 规格选择弹窗 */
.popup .attr-content {
	padding: 0rpx 0rpx 50rpx 0rpx;

	.a-t {
		display: flex;

		.spec-image-container {
			position: relative;
			width: 144rpx;
			height: 144rpx;
			flex-shrink: 0;
			border-radius: 8rpx;
			background-color: #f8f8f8;
			overflow: hidden;
			transition: transform 0.2s ease;

			&:active {
				transform: scale(0.95);
			}

			.spec-image {
				width: 100%;
				height: 100%;
				display: block;
			}

			.preview-icon {
				position: absolute;
				bottom: 0;
				left: 0;
				right: 0;
				background: rgba(0, 0, 0, 0.6);
				display: flex;
				align-items: center;
				justify-content: center;
				height: 36rpx;
				opacity: 0.8;

				.preview-text {
					color: #ffffff;
					font-size: 20rpx;
					font-weight: 500;
				}
			}
		}

		.right {
			display: flex;
			flex-direction: column;
			padding-left: 30rpx;
			font-size: 26rpx;
			color: #606266;
			line-height: 42rpx;

			.name {
				font-size: 28rpx;
				color: #000000;
			}

			.price {
				font-size: 36rpx;
				color: #000000;
				font-weight: 400;

				&:before {
					content: "￥";
					font-size: 26rpx;
				}
			}

			.selected {
				margin-right: 10rpx;
				margin-bottom: 30rpx;
			}
		}
	}

	.attr-list {
		display: flex;
		flex-direction: column;
		font-size: 28rpx;
		color: #000;
		padding-top: 30rpx;
		padding-left: 10rpx;
	}

	.item-list {
		padding: 20rpx 0 0;
		display: flex;
		flex-wrap: wrap;


	}

	.spec-option-wrapper {
		position: relative;
		display: inline-flex;
		margin-right: 20rpx;
		margin-bottom: 20rpx;

		.spec-option {
			display: flex;
			align-items: center;
			justify-content: center;
			background: #F8F8F8;
			border-radius: 4rpx;
			border: none;
			min-width: 60rpx;
			height: 70rpx;
			padding: 10rpx 20rpx;
			font-size: 24rpx;
			color: #0A0D05;
			opacity: 0.8;

			&.selected {
				background: rgba(100, 125, 0, 0.05);
				border: 1px solid #A9FF00;
				border-radius: 8rpx;
				color: #647D00;
				opacity: 1;
			}

			&.disabled {
				background: #F8F8F8 !important;
				color: #CCCCCC !important;
				opacity: 0.5;
				position: relative;

				&::after {
					content: '';
					position: absolute;
					top: 50%;
					left: 0;
					right: 0;
					height: 1px;
					background: #cccccc;
					transform: rotate(-45deg);
				}
			}
		}
	}

	.out-of-stock-tip {
		position: absolute;
		top: -10rpx;
		right: -10rpx;
		background: #ff4757;
		color: #fff;
		font-size: 18rpx !important;
		padding: 2rpx 6rpx !important;
		border-radius: 8rpx;
		line-height: 1.2;
		z-index: 10;
		transform: scale(0.8);
		white-space: nowrap;
	}

	.no-stock-notice {
		margin-bottom: 30rpx;
		padding: 20rpx;
		background: linear-gradient(135deg, #fff5f5 0%, #fed7d7 100%);
		border-radius: 12rpx;
		border-left: 6rpx solid #ff6b6b;
		cursor: pointer;
		transition: all 0.3s ease;

		&:active {
			transform: scale(0.98);
			opacity: 0.8;
		}

		.notice-content {
			display: flex;
			align-items: center;
			justify-content: space-between;

			.notice-icon {
				font-size: 32rpx;
				margin-right: 16rpx;
				flex-shrink: 0;
			}

			.notice-text {
				font-size: 28rpx;
				color: #c53030;
				font-weight: 500;
				line-height: 1.4;
				flex: 1;
			}

			.notice-arrow {
				width: 24rpx;
				height: 24rpx;
				flex-shrink: 0;
				opacity: 0.7;
			}
		}
	}

	.product-quantity {
		margin-top: 30rpx;
		display: flex;
		justify-content: space-between;
		width: 100%;
		align-items: center;
		gap: 20rpx;

		.label {
			font-size: 30rpx;
			flex: 0 0 auto;
		}

		.quantity-control {
			display: flex;
			align-items: center;
			/* 防止控件被压缩 */
			flex: 0 0 auto;
		}

		button {
			width: 60rpx;
			height: 60rpx;
			text-align: center;
			color: #000;
			font-size: 50rpx;
			background-color: #ffffff;
			border: 2rpx solid #EAEAEA;
			border-radius: 8rpx;
			line-height: 60rpx;
			padding: 0rpx;
		}

		input {
			width: 60rpx;
			height: 60rpx;
			text-align: center;
			border: 2rpx solid #EAEAEA;
			font-size: 30rpx;
			line-height: 60rpx;
		}
	}

	.action-btn-group {
		display: flex;
		margin-top: 40rpx;
		height: 80rpx;
		position: relative;
		gap: 20rpx;

		.action-btn {
			display: flex;
			align-items: center;
			justify-content: center;
			width: 333rpx;
			height: 100%;
			border-radius: 40rpx;
			font-size: 32rpx;
			padding: 0;
			background: transparent;
			color: #000000;
		}

		.buy-now-btn {
			background: #20201E;
			color: #A9FF00;
			border: none;
		}

		.add-cart-btn {
			background: #FFFFFF;
			border: 2rpx solid #000;
		}
	}
}

.no-padding {
	padding: 0rpx 0rpx;
}

/*  弹出层 */
.popup {
	position: fixed;
	left: 0;
	top: 0;
	right: 0;
	bottom: 0;
	z-index: 99;

	&.show {
		display: block;

		.mask {
			animation: showPopup 0.2s linear both;
		}

		.layer {
			animation: showLayer 0.2s linear both;
		}
	}

	&.hide {
		.mask {
			animation: hidePopup 0.2s linear both;
		}

		.layer {
			animation: hideLayer 0.2s linear both;
		}
	}

	&.none {
		display: none;
	}

	.mask {
		position: fixed;
		top: 0;
		width: 100%;
		height: 100%;
		z-index: 1;
		background-color: rgba(0, 0, 0, 0.4);
	}

	.layer {
		position: fixed;
		z-index: 99;
		bottom: 0;
		width: 100%;
		min-height: 40vh;
		border-radius: 16rpx 16rpx 0 0;
		background-color: #fff;

		.btn {
			height: 88rpx;
			line-height: 88rpx;
			border-radius: 100rpx;
			background: #0088FF;
			font-size: 30rpx;
			color: #fff;
			margin: 30rpx auto 20rpx;
		}
	}
}

@keyframes showPopup {
	0% {
		opacity: 0;
	}

	100% {
		opacity: 1;
	}
}

@keyframes hidePopup {
	0% {
		opacity: 1;
	}

	100% {
		opacity: 0;
	}
}

@keyframes showLayer {
	0% {
		transform: translateY(120%);
	}

	100% {
		transform: translateY(0%);
	}
}

@keyframes hideLayer {
	0% {
		transform: translateY(0);
	}

	100% {
		transform: translateY(120%);
	}
}

/* 底部操作菜单 */
.page-bottom {
	position: fixed;
	left: 0;
	bottom: 0;
	width: 100%;
	height: 160rpx;
	background-color: #FFFFFF;
	display: flex;
	align-items: center;
	padding: 0 20rpx;
	z-index: 95;
	box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
	border-top: 0.5px solid #EBEBEB;
}

.bottom-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	width: 120rpx;
	flex-shrink: 0;
	position: relative;
	height: 100rpx;
}

.cart-item {
	position: relative;
}

.bottom-icon {
	width: 44rpx;
	height: 44rpx;
	margin-bottom: 8rpx;
}

.bottom-text {
	font-size: 20rpx;
	color: #666666;
}

.badge {
	position: absolute;
	top: 8rpx;
	right: 50%;
	transform: translateX(20rpx);
	min-width: 32rpx;
	height: 32rpx;
	line-height: 32rpx;
	background-color: #D3FB51;
	border-radius: 16rpx;
	font-size: 20rpx;
	font-weight: 500;
	color: #000000;
	text-align: center;
	padding: 0 6rpx;
}

/* 按钮组样式 */
.button-group {
	display: flex;
	flex: 2;
	height: 84rpx;
	margin-left: 20rpx;
	gap: 0;
}

.group-button {
	display: flex;
	align-items: center;
	justify-content: center;
	height: 100%;
	position: relative;
}

/* 购物车按钮 */
.cart-button {
	background: transparent;
	border: 1px solid #EEEEEE;
	border-radius: 16rpx 0 0 16rpx;
	width: 122rpx;
	flex-shrink: 0;
}

/* 当没有DIY按钮时，购物车按钮右侧也要有边框 */
.button-group:not(:has(.diy-button)) .cart-button {
	border-right: 1px solid #EEEEEE;
}

.button-icon {
	width: 48rpx;
	height: 48rpx;
}

.cart-badge {
	position: absolute;
	top: 8rpx;
	right: 8rpx;
	min-width: 32rpx;
	height: 32rpx;
	line-height: 32rpx;
	background-color: #D3FB51;
	border-radius: 16rpx;
	font-size: 20rpx;
	font-weight: 500;
	color: #000000;
	text-align: center;
	padding: 0 6rpx;
}

/* DIY定制按钮 */
.diy-button {
	background: #A9FF00;
	flex: 1;
	border-top: 1px solid #EEEEEE;
	border-bottom: 1px solid #EEEEEE;
	min-width: 140rpx;
}

/* 立即购买按钮 */
.buy-button {
	background: #20201E;
	border: 1px solid #EEEEEE;
	border-radius: 0 16rpx 16rpx 0;
	border-left: none;
	flex: 2;
	min-width: 160rpx;
}

/* 当没有DIY按钮时，立即购买按钮需要左边框 */
.button-group:not(:has(.diy-button)) .buy-button {
	border-left: 1px solid #EEEEEE;
}

.button-text {
	font-size: 32rpx;
	font-weight: 400;
}

.diy-button .button-text {
	color: #000000;
}

.buy-button .button-text {
	color: #A9FF00;
}

/* 优惠券面板 */
.mask {
	display: flex;
	align-items: flex-end;
	position: fixed;
	left: 0;
	top: 0px;
	bottom: 0;
	width: 100%;
	background: rgba(0, 0, 0, 0);
	z-index: 9995;
	transition: 0.3s;

	.mask-content {
		width: 100%;
		min-height: 30vh;
		max-height: 100vh;
		background: #f3f3f3;
		transform: translateY(100%);
		transition: 0.3s;
		overflow-y: scroll;
	}

	&.none {
		display: none;
	}

	&.show {
		background: rgba(0, 0, 0, 0.4);

		.mask-content {
			transform: translateY(0);
		}
	}
}

/* 优惠券列表 */
.coupon-item {
	display: flex;
	flex-direction: column;
	margin: 20rpx 24rpx;
	background: #fff;

	.con {
		display: flex;
		align-items: center;
		position: relative;
		height: 120rpx;
		padding: 0 30rpx;

		&:after {
			position: absolute;
			left: 0;
			bottom: 0;
			content: "";
			width: 100%;
			height: 0;
			border-bottom: 1px dashed #f3f3f3;
			transform: scaleY(50%);
		}
	}

	.left {
		display: flex;
		flex-direction: column;
		justify-content: center;
		flex: 1;
		overflow: hidden;
		height: 100rpx;
	}

	.title {
		font-size: 32rpx;
		color: #333333;
		margin-bottom: 10rpx;
	}

	.time {
		font-size: 24rpx;
		color: #909399;
	}

	.right {
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		font-size: 26rpx;
		color: #606266;
		height: 100rpx;
	}

	.price {
		font-size: 44rpx;
		color: #0088FF;

		&:before {
			content: "￥";
			font-size: 34rpx;
		}
	}

	.tips {
		font-size: 24rpx;
		color: #909399;
		line-height: 60rpx;
		padding-left: 30rpx;
	}

	.circle {
		position: absolute;
		left: -6rpx;
		bottom: -10rpx;
		z-index: 10;
		width: 20rpx;
		height: 20rpx;
		background: #f3f3f3;
		border-radius: 100px;

		&.r {
			left: auto;
			right: -6rpx;
		}
	}
}

.brand-info {
	margin-top: 16rpx;
	background-color: #fff;
	display: flex;
	flex-direction: column;

	.brand-box {
		display: flex;
		align-items: center;
		padding: 30rpx 50rpx;

		.image-wrapper {
			width: 210rpx;
			height: 70rpx;

			image {
				width: 100%;
				height: 100%;
			}
		}

		.title {
			flex: 1;
			display: flex;
			flex-direction: column;
			font-size: 36rpx;
			margin-left: 30rpx;
			color: #333333;

			text:last-child {
				font-size: 24rpx;
				color: #909399;
				margin-top: 8rpx;

				&.Skeleton {
					width: 220rpx;
				}
			}
		}
	}

	.d-header {
		display: flex;
		justify-content: center;
		align-items: center;
		height: 80rpx;
		font-size: 30rpx;
		color: #333333;
		position: relative;

		text {
			padding: 0 20rpx;
			background: #fff;
			position: relative;
			z-index: 1;
		}

		&:after {
			position: absolute;
			left: 50%;
			top: 50%;
			transform: translateX(-50%);
			width: 300rpx;
			height: 0;
			content: "";
			border-bottom: 1px solid #ccc;
		}
	}
}

/* 促销信息条 */
.promo-bar {
	display: flex;
	align-items: center;
	justify-content: space-between;
	background-color: #D3FB51;
	padding: 0 24rpx;
	height: 80rpx;
	/* 固定高度 */
	min-height: 80rpx;
	/* 添加最小高度 */
	width: 100%;
	box-sizing: border-box;
	/* 确保padding不改变整体尺寸 */
	z-index: 2;
	/* 确保在顶部图片之上 */
}

.promo-text {
	font-size: 28rpx;
	font-weight: 500;
	flex: 1;
	/* 让文本区域占据剩余空间 */
	white-space: nowrap;
	/* 防止文本换行 */
	overflow: hidden;
	/* 溢出隐藏 */
	text-overflow: ellipsis;
	/* 溢出显示省略号 */
}

/* 黑色样式的按钮 */
.get-promo-btn {
	background-color: #ffffff;
	color: #000000;
	font-size: 24rpx;
	padding: 8rpx 20rpx;
	border-radius: 30rpx;
	margin-left: 16rpx;
	/* 增加与文本的间距 */
	white-space: nowrap;
	/* 防止文本换行 */
	flex-shrink: 0;
	/* 防止按钮被压缩 */
	height: 50rpx;
	/* 固定高度 */
	line-height: 50rpx;
	/* 文字垂直居中 */
	display: flex;
	align-items: center;
	justify-content: center;
}

/* 防伪码显示区域 */
.code-container {
	margin-top: 20rpx;
	margin-bottom: 20rpx;
	padding: 0;
	background-color: #fff;
	width: 100%;
	overflow: hidden;
}

.code-wrapper {
	position: relative;
	width: 100%;
	height: 1176rpx;
	display: flex;
	justify-content: center;
	align-items: center;
	overflow: hidden;
}

.code-bg {
	width: 100%;
	height: 100%;
	object-fit: contain;
}

.brand-img {
	position: absolute;
	top: 765rpx;
	/* 调整品牌图片的垂直位置 */
	left: 160rpx;
	/* 调整品牌图片的水平位置 */
	width: 180rpx;
	height: 60rpx;
	object-fit: contain;
	z-index: 2;
}

.code-img {
	position: absolute;
	top: 880rpx;
	/* 调整二维码图片的垂直位置 */
	left: 225rpx;
	/* 调整二维码图片的水平位置 */
	width: 108rpx;
	height: 108rpx;
	object-fit: contain;
	z-index: 2;
}

.source-img {
	position: absolute;
	top: 880rpx;
	/* 调整来源图片的垂直位置 */
	left: 420rpx;
	/* 调整来源图片的水平位置 */
	width: 108rpx;
	height: 108rpx;
	object-fit: contain;
	z-index: 2;
}

/* 悬浮分享按钮 */
.float-share-btn {
	position: fixed;
	right: 30rpx;
	bottom: 200rpx;
	width: 100rpx;
	height: 100rpx;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	z-index: 90;
}

.share-icon {
	width: 60rpx;
	height: 60rpx;
}

/* 悬浮推广按钮 */
.float-promotion-btn {
	position: fixed;
	right: 30rpx;
	bottom: 200rpx;
	width: 96rpx;
	height: 96rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	z-index: 90;
}

.promotion-icon {
	width: 96rpx;
	height: 96rpx;
}

.popup-header {
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 12rpx 0;
	background-color: rgba(100, 125, 0, 0.09);
	border-radius: 16rpx 16rpx 0 0;
	position: relative;
	margin: 0;
}

.close-icon {
	position: absolute;
	right: 24rpx;
	top: 50%;
	transform: translateY(-50%);
	width: 32rpx;
	height: 32rpx;
	z-index: 1;
}

.popup-title-container {
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 8rpx;
}

.comment-icon {
	width: 32rpx;
	height: 32rpx;
}

.popup-title {
	font-size: 24rpx;
	font-weight: 400;
	color: #647D00;
	text-align: center;
}

.buy-type-tabs {
	display: flex;
	background-color: #F5F5F5;
	border-radius: 8rpx;
	padding: 4rpx;
	margin: 20rpx 30rpx;
	gap: 8rpx;
}

.tab-item {
	flex: 1;
	padding: 12rpx 32rpx;
	border-radius: 4rpx;
	text-align: center;
	background-color: transparent;
	border: none;
}

.tab-item.active {
	background-color: #A9FF00;
}

.tab-text {
	font-size: 28rpx;
	font-weight: 400;
	color: #0A0D05;
}

.tab-item.active .tab-text {
	font-weight: 600;
}

.gift-tip {
	margin: 0 30rpx 20rpx 30rpx;
	padding: 16rpx 20rpx;
	background-color: #A9FF001A;
}

.gift-tip-text {
	font-size: 24rpx;
	color: #647D00;
	line-height: 1.4;
}

.address-section {
	padding: 32rpx 30rpx;
	border-bottom: 1px solid #EEEEEE;
	margin-bottom: 20rpx;
}

.address-content {
	display: flex;
	align-items: center;
	justify-content: space-between;
}

.address-left {
	display: flex;
	flex-direction: column;
	flex: 1;
}

.address-header {
	display: flex;
	align-items: flex-start;
	margin-bottom: 8rpx;
}

.address-icon {
	width: 32rpx;
	height: 32rpx;
	margin-right: 16rpx;
	margin-top: 4rpx;
	flex-shrink: 0;
}

.address-title {
	font-size: 28rpx;
	font-weight: 500;
	color: #0A0D05;
	margin-right: 16rpx;
	flex-shrink: 0;
}

.address-detail {
	font-size: 28rpx;
	color: #0A0D05;
	line-height: 1.4;
	flex: 1;
}

.address-section {
	.shipping-row {
		margin-left: 40rpx;
	}

	.shipping-info {
		font-size: 24rpx;
		color: #666666;
		margin-top: 4rpx;
	}
}

.arrow-icon {
	width: 32rpx;
	height: 32rpx;
}

.product-section {
	padding: 0 30rpx;
	margin-bottom: 20rpx;
}

.product-info {
	display: flex;
	align-items: flex-start;
	margin-bottom: 20rpx;
}

.spec-image-container {
	width: 176rpx;
	height: 176rpx;
	margin-right: 20rpx;
	border-radius: 8rpx;
	overflow: hidden;
	flex-shrink: 0;
}

.spec-image {
	width: 100%;
	height: 100%;
}

.product-right {
	flex: 1;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
	height: 176rpx;
}

.product-details {
	margin-top: 20rpx;
}

.product-specs {
	margin-bottom: 0;
}

.spec-row {
	display: flex;
	flex-direction: column;
	margin-bottom: 20rpx;
}

.spec-label {
	font-size: 28rpx;
	font-weight: 400;
	color: #0A0D05;
	margin-bottom: 10rpx;
}

.spec-options {
	display: flex;
	flex-wrap: wrap;
	gap: 10rpx;
}

.spec-option {
	padding: 16rpx 32rpx;
	border-radius: 4rpx;
	font-size: 28rpx;
	color: #0A0D05;
	background: #F8F8F8;
	border: none;
	opacity: 0.8;
}

.spec-option.selected {
	background: rgba(100, 125, 0, 0.05);
	border: 1px solid #A9FF00;
	border-radius: 8rpx;
	color: #647D00;
	opacity: 1;
}

.spec-option.disabled {
	background: #F8F8F8;
	color: #CCCCCC;
	opacity: 0.5;
}

.out-of-stock-tip {
	position: absolute;
	top: -10rpx;
	right: -10rpx;
	background: #ff4757;
	color: white;
	font-size: 20rpx;
	padding: 2rpx 8rpx;
	border-radius: 8rpx;
	transform: scale(0.8);
	z-index: 10;
	white-space: nowrap;
}

.price-info {
	margin-bottom: 16rpx;
	display: flex;
	align-items: baseline;
}

.price-prefix {
	font-size: 28rpx;
	color: #647D00;
	font-weight: 400;
}

.price-amount {
	font-size: 48rpx;
	color: #647D00;
	font-weight: 400;
}

.price-discount {
	font-size: 28rpx;
	color: #647D00;
	font-weight: 400;
}

.quantity-control {
	display: flex;
	align-items: center;
	justify-content: flex-start;
}

.quantity-btn {
	width: 56rpx;
	height: 56rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	background-color: #F8F8F8;
	border: 1px solid #EEEEEE;
	color: #000000;
	font-size: 32rpx;
	font-weight: 400;
	padding: 0;
	margin: 0;
	line-height: 1;
}

.quantity-btn:first-child {
	border-radius: 8rpx 0 0 8rpx;
	border-right: none;
}

.quantity-btn:last-child {
	border-radius: 0 8rpx 8rpx 0;
	border-left: none;
}

.quantity-input {
	width: 108rpx;
	height: 56rpx;
	text-align: center;
	border: 1px solid #EEEEEE;
	border-left: none;
	border-right: none;
	background-color: #F8F8F8;
	font-size: 28rpx;
	color: #000000;
	padding: 0;
	margin: 0;
}

.gift-message-section {
	padding: 24rpx 30rpx;
	margin-bottom: 20rpx;
	display: flex;
	flex-direction: column;
	justify-content: center;
}

.gift-message-label {
	display: flex;
	align-items: center;
	margin-bottom: 16rpx;
}

.gift-icon {
	width: 32rpx;
	height: 32rpx;
	margin-right: 16rpx;
}

.gift-message-text {
	font-size: 28rpx;
	font-weight: 400;
	color: #0A0D05;
}

.gift-message-input {
	width: 100%;
	height: 180rpx;
	border-radius: 8rpx;
	border: 1px solid #EEEEEE;
	padding: 60rpx 16rpx;
	font-size: 28rpx;
	color: #0A0D05;
	background-color: #F8F8F8;
	box-sizing: border-box;
	line-height: 40rpx;
	resize: none;
	vertical-align: middle;
}

.payment-section {
	padding: 24rpx 30rpx;
	margin-bottom: 20rpx;
	display: flex;
	flex-direction: column;
	justify-content: center;
}

.payment-method {
	display: flex;
	align-items: center;
	justify-content: space-between;
	margin-bottom: 16rpx;
}

.payment-item {
	display: flex;
	align-items: center;
}

.payment-icon {
	width: 32rpx;
	height: 32rpx;
	margin-right: 16rpx;
}

.payment-text {
	font-size: 28rpx;
	font-weight: 400;
	color: #0A0D05;
}

.payment-radio {
	display: flex;
	align-items: center;
}

.radio-outer {
	width: 40rpx;
	height: 40rpx;
	border-radius: 50%;
	border: 5px solid #A9FF00;
	display: flex;
	align-items: center;
	justify-content: center;
}

.radio-inner {
	width: 20rpx;
	height: 20rpx;
	border-radius: 50%;
	background-color: #FFFFFF;
}

.more-payment {
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 16rpx 0;
	border-top: 1px solid #EEEEEE;
}

.more-payment-text {
	font-size: 24rpx;
	color: #9FA19D;
	margin-right: 8rpx;
}

.arrow-down-icon {
	width: 24rpx;
	height: 24rpx;
}

.section-divider {
	width: 100%;
	height: 16rpx;
	background-color: #F8F8F8;
}
</style>