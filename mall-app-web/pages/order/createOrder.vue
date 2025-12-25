<template>
  <view class="container" :class="{ 'pickup-mode': orderType === 1 }">
    <!-- 送给朋友的提示 -->
    <view v-if="isGift === 1" class="gift-order-section">
      <view class="gift-order-tip">
        <text class="gift-tip-text">下单后，分享给朋友-TA填写口令与收货地址-收下礼物</text>
      </view>
    </view>

    <!-- 配送方式区域 - 始终显示（非送礼订单） -->
    <view v-if="isGift !== 1" class="delivery-section" :class="{ 'floating': orderType === 1 }">
      <view class="delivery-options">
        <view
          v-if="!isPickupOnly"
          class="delivery-option"
          :class="{ 'active': orderType === 0 }"
          @tap="e0"
        >
          <text class="delivery-text">邮寄</text>
        </view>
        <view
          class="delivery-option"
          :class="{ 'active': orderType === 1 }"
          @tap="e1"
        >
          <text class="delivery-text">到店消费</text>
        </view>
      </view>
      <!-- 免运费标签 -->
      <view v-if="orderType === 0" class="free-shipping-tag">
        <text class="free-shipping-text">免运费</text>
      </view>
    </view>

    <!-- 自提模式：地图和门店区域 -->
    <view v-if="isGift !== 1 && orderType === 1" class="pickup-section">
      <!-- 地图组件 -->
      <view class="map-container">
        <StoreMap
          :stores="storeList"
          :selectedStoreId="selectedStore ? selectedStore.id : null"
          @store-selected="onStoreSelected"
          @visit-store="onVisitStore"
        />
      </view>

      <!-- 门店卡片列表 -->
      <view class="store-cards-container">
        <view v-if="storeList.length > 0" class="store-cards">
          <StoreCard
            v-for="store in storeList"
            :key="store.id"
            :store="store"
            :distance="store.distance"
            @click="onStoreCardClick"
            @visit="onVisitStore"
          />
        </view>

        <!-- 空状态 -->
        <view v-else class="empty-state">
          <image
            class="empty-icon"
            src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/empty.png"
            mode="aspectFit"
          />
          <text class="empty-text">暂无门店信息</text>
        </view>
      </view>
    </view>



    <!-- 订单信息区域 - 仅在选择邮寄时显示 -->
    <view v-if="orderType === 0 && isGift !== 1" class="order-info-section">
      <!-- 地址信息区域 - 非送礼订单才显示 -->
      <view v-if="isGift !== 1" class="address-section">
        <!-- 配送地址 -->
        <navigator
          v-if="currentAddress && currentAddress.name"
          class="address-content"
          url="/pages/address/address?source=1"
        >
          <view class="address-icon">
            <image
              class="address-icon-img"
              src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/product/address.png"
            ></image>
          </view>
          <view class="address-left">
            <view class="top">
              <text class="name">{{ currentAddress.name }}</text>
              <text class="mobile">{{ currentAddress.phoneNumber }}</text>
            </view>
            <text class="address">
              {{ currentAddress.province + " " + currentAddress.city + " " + currentAddress.region + " " + currentAddress.detailAddress }}
            </text>
          </view>
          <image
            class="arrow-right-img"
            src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/order/arrow.png"
          ></image>
        </navigator>

        <!-- 空地址 -->
        <navigator
          v-else
          class="address-content empty-address"
          url="/pages/address/address?source=1"
        >
          <view class="address-left">
            <text class="empty-text">请添加地址</text>
          </view>
          <image
            class="arrow-right-img"
            src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/order/arrow.png"
          ></image>
        </navigator>
      </view>

      <!-- 商品信息区域 -->
      <view class="goods-section">
        <view
          v-for="item in cartList"
          :key="item.id"
          class="g-item"
        >
          <view class="goods-img-container">
            <image
              class="goods-img"
              :src="item.productPic"
              mode="aspectFill"
            ></image>
          </view>
          <view class="goods-info">
            <view class="goods-name-row">
              <text class="goods-name">{{ item.productName }}</text>
              <text class="goods-price">￥{{ item.price }}</text>
            </view>
            <view class="goods-attrs-row">
              <text class="goods-attrs">{{ item.spDataStr }}</text>
              <text class="goods-quantity">x{{ item.quantity }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 优惠券区域 -->
      <view class="coupon-section">
        <view class="coupon-header" @tap="toggleMask('show')">
          <view class="coupon-left">
            <view class="coupon-icon">
              <image class="coupon-icon-img" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/order/coupon.png"></image>
            </view>
            <view class="coupon-title">优惠券</view>
          </view>
          <view class="coupon-right">
            <view v-if="currCoupon">
              <text class="selected-coupon-text">已选择: -￥{{ calculateCouponDiscount(currCoupon).toFixed(2) }}</text>
            </view>
            <view v-else-if="couponList && couponList.length > 0">
              <text class="coupon-count-text">{{ couponList.length }}张优惠券可用</text>
            </view>
            <view v-else>
              <text class="no-coupon-text">暂无可用优惠券</text>
            </view>
            <image
              class="coupon-arrow"
              src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/order/arrow.png"
            ></image>
          </view>
        </view>
      </view>

      <!-- 积分区域 -->
      <!-- <view class="integration-section">
        <view class="integration-header">
          <view class="integration-left">
            <view class="integration-icon">
              <image
                class="integral-img"
                src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/order/integral_icon.svg"
              ></image>
            </view>
            <view class="integration-title">积分</view>
          </view>
          <view class="integration-right">
            <input
              class="integration-input"
              type="number"
              placeholder="使用积分数量"
              placeholder-class="placeholder"
              v-model="useIntegration"
              @input="handleIntegrationInput"
            />
          </view>
        </view>
      </view> -->

      <!-- 订单摘要区域 -->
      <view class="order-summary">
        <view class="section-title">价格明细</view>
        <view class="summary-item">
          <text class="summary-label">小计</text>
          <text class="summary-value">￥{{ calcAmount.totalAmount }}</text>
        </view>
        <view class="summary-item">
          <text class="summary-label">活动优惠</text>
          <text class="summary-value discount">-￥{{ calcAmount.promotionAmount }}</text>
        </view>
        <view class="summary-item">
          <text class="summary-label">运费</text>
          <text class="summary-value">
            {{ getFreightDisplayText() }}
          </text>
        </view>
        <view class="summary-item">
          <text class="summary-label">优惠券</text>
          <text v-if="currCoupon != null" class="summary-value discount">-￥{{ calculateCouponDiscount(currCoupon).toFixed(2) }}</text>
          <text v-else class="summary-value discount">-￥0</text>
        </view>
        <!-- <view class="summary-item">
          <text class="summary-label">积分抵扣</text>
          <text class="summary-value discount">-￥{{ integrationAmount }}</text>
        </view> -->
        <view class="total-divider"></view>
        <view class="summary-item total">
          <text class="summary-label">总计</text>
          <text class="summary-value total-price">￥{{ calcAmount.payAmount }}</text>
        </view>

        <view class="remark-section">
          <text class="remark-title">备注</text>
          <view class="remark-input-wrapper">
            <input
              class="remark-input"
              type="text"
              placeholder="请填写备注信息"
              placeholder-class="placeholder"
              v-model="desc"
            />
          </view>
        </view>
      </view>

      <!-- 付款方式选择区域 -->
      <view class="payment-method-section">
        <view class="payment-options">
          <view
            class="payment-option"
            :class="{ 'active': selectedPaymentMethod === 'wechat' }"
            @tap="selectPaymentMethod('wechat')"
          >
            <view class="payment-icon">
              <image
                class="payment-icon-img"
                src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/product/wechat.png"
              ></image>
            </view>
            <text class="payment-text">微信付款</text>
            <view class="payment-radio" :class="{ 'active': selectedPaymentMethod === 'wechat' }"></view>
          </view>
          <view
            class="payment-option"
            :class="{
              'active': selectedPaymentMethod === 'balance',
              'disabled': memberBalance < calcAmount.payAmount
            }"
            @tap="selectPaymentMethod('balance')"
          >
            <view class="payment-icon">
              <image
                class="payment-icon-img"
                src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/order/balance.png"
              ></image>
            </view>
            <view class="payment-content">
              <text class="payment-text">余额支付</text>
              <text class="balance-amount">余额：￥{{ memberBalance.toFixed(2) }}</text>
            </view>
            <view class="payment-radio" :class="{ 'active': selectedPaymentMethod === 'balance' }"></view>
          </view>
        </view>
      </view>

      <!-- 底部支付区域 - 仅在邮寄模式下显示 -->
      <view v-if="orderType === 0" class="footer">
        <view class="order-info">
          <text class="order-count">共{{ totalQuantity }}件</text>
          <text class="order-total-label">合计</text>
        </view>
        <view class="price-payment">
          <text class="order-total-price">￥{{ calcAmount.payAmount }}</text>
          <view
            class="pay-btn"
            :class="{ 'disabled': loading || isPaymentLoading }"
            @tap="submit"
          >{{ loading || isPaymentLoading ? '处理中...' : '立即支付' }}</view>
        </view>
      </view>
    </view>
    
    <!-- 优惠券弹窗组件 -->
    <CouponSelectPopup
      :visible="showCouponPopup"
      :couponList="couponList"
      :selectedCoupon="currCoupon"
      @close="closeCouponPopup"
      @select="selectCoupon"
      @confirm="confirmCouponSelection"
    />
    

  </view>
</template>

<script>
import { generateConfirmOrder, generateDirectConfirmOrder, generateOrder, getWechatPayParams } from '@/api/order.js';
import { formatDate } from '@/utils/date';
import { addAddress } from '@/api/address.js';
import { fetchStoreList, fetchStoreListBySchool } from '@/api/store.js';
import CouponSelectPopup from '@/components/CouponSelectPopup.vue';
import StoreMap from '@/components/store/StoreMap.vue';
import StoreCard from '@/components/store/StoreCard.vue';

export default {
  components: {
    CouponSelectPopup,
    StoreMap,
    StoreCard
  },
  data() {
    return {
      orderType: 0, // 0: 快递邮寄

      // 地址信息
      currentAddress: null,

      // 商品信息
      cartList: [],
      cartIds: [], // 购物车ID列表

      // 优惠券
      couponList: [],
      currCoupon: null,
      maskState: 0, // 0: 隐藏, 1: 显示 (保留用于兼容)
      showCouponPopup: false, // 新的优惠券弹窗状态

      // 积分
      useIntegration: '',
      integrationAmount: 0,
      integrationConsumeSetting: {},
      memberIntegration: 0,

      // 用户余额
      memberBalance: 0,

      // 订单信息
      calcAmount: {
        totalAmount: 0,
        promotionAmount: 0,
        freightAmount: 0,
        payAmount: 0
      },

      // 备注
      desc: '',

      // 是否是礼物订单
      isGift: 0,
      // 送礼留言
      giftMessage: '',

      // 加载状态
      loading: false,

      // 直接购买的商品
      directBuyItem: null,

      // 保存原始促销信息数据，用于提交订单时使用
      cartPromotionItemList: [],

      // 支付相关状态
      isPaymentLoading: false,
      currentOrderInfo: null,

      // 付款方式选择
      selectedPaymentMethod: 'wechat', // 默认选择微信付款

      // 门店相关数据
      storeList: [],
      selectedStore: null,
      schoolFilter: null,
      currentLocation: null, // 用户当前位置
      
      // 组合商品相关数据
      isBundleOrder: false,
      bundleOrderData: null,
      bundleInfo: null
    };
  },
  
  computed: {
    // 当直接购买且商品仅支持线下自提时返回 true
    isPickupOnly() {
      if (!this.directBuyItem) return false;
      const pt = this.directBuyItem.purchaseType;
      return pt === 1 || pt === true;
    },

    // 计算商品总件数
    totalQuantity() {
      if (!this.cartList || this.cartList.length === 0) return 0;
      return this.cartList.reduce((total, item) => total + (item.quantity || 0), 0);
    },


  },
  
  onLoad(options) {
    try {
      // 获取当前位置（用于计算门店距离）
      this.getCurrentLocation();

      // 设置配送方式
      if (options.deliveryType) {
        this.deliveryType = parseInt(options.deliveryType);
        this.orderType = this.deliveryType; // 使用相同的值

        // 如果是自提模式，加载门店列表
        if (this.orderType === 1) {
          this.loadStoreList();
        }
      }

      // 判断是否为礼物订单
      if (options.isGift) {
        this.isGift = parseInt(options.isGift);
      }

      // 获取学校筛选参数
      if (options.schoolId) {
        this.schoolFilter = {
          id: parseInt(options.schoolId),
          name: decodeURIComponent(options.schoolName || '')
        };
      } else {
        // 没有学校参数时，尝试从本地存储获取已选择的学校
        try {
          const schoolInfo = uni.getStorageSync('selectedSchool');
          if (schoolInfo) {
            const school = JSON.parse(schoolInfo);
            this.schoolFilter = {
              id: school.id,
              name: school.schoolName || ''
            };
          }
        } catch (error) {
          console.log('获取本地学校信息失败:', error);
        }
      }

      // 获取礼物留言
      if (options.giftMessage) {
        try {
          this.giftMessage = decodeURIComponent(options.giftMessage);
          console.log('解码后的礼物留言:', this.giftMessage);
        } catch (e) {
          console.error('解析礼物留言失败:', e);
          this.giftMessage = options.giftMessage; // 解码失败时使用原值
        }
      }
      
      // 获取从商品详情页传递的地址信息
      let productPageAddress = null;
      if (options.addressInfo) {
        try {
          productPageAddress = JSON.parse(decodeURIComponent(options.addressInfo));
          console.log('从商品详情页获取到地址信息:', productPageAddress);
        } catch (e) {
          console.error('解析地址信息失败:', e);
        }
      }
      
      // 组合商品订单
      if (options.type === 'bundle' && options.data) {
        try {
          this.bundleOrderData = JSON.parse(decodeURIComponent(options.data));
          this.isBundleOrder = true;
          this.loadBundleConfirmOrder();
        } catch (e) {
          console.error('解析组合商品数据失败:', e);
          uni.showToast({
            title: '订单数据解析失败',
            icon: 'none'
          });
        }
      }
      // 从购物车来的订单
      else if (options.cartIds) {
        try {
          this.cartIds = JSON.parse(options.cartIds);
          // 确保cartIds是数组格式
          if (!Array.isArray(this.cartIds)) {
            this.cartIds = [this.cartIds];
          }
          this.loadCartConfirmOrder();
        } catch (e) {
          console.error('解析cartIds失败:', e);
          // 尝试作为单个ID处理
          this.cartIds = [parseInt(options.cartIds)];
          this.loadCartConfirmOrder();
        }
      } 
      // 商品详情直接购买
      else if (options.productInfo) {
        this.directBuyItem = JSON.parse(decodeURIComponent(options.productInfo));
        // 如果有商品详情页传递的地址信息，优先使用
        if (productPageAddress) {
          this.currentAddress = productPageAddress;
        }
        // 如果商品仅支持线下自提，默认选择自提并隐藏快递选项
        const pt = this.directBuyItem.purchaseType;
        if (pt === 1 || pt === true) {
          this.orderType = 1;
          this.deliveryType = 1;
          // 自提模式下加载门店列表
          this.loadStoreList();
        }
        this.loadDirectBuyConfirmOrder();
      }
      // 其他情况，可能从购物车页面传递选中商品
      else {
        // 获取所有已选中商品
        const selectedCartItems = uni.getStorageSync('selectedCartItems');
        if (selectedCartItems && selectedCartItems.length > 0) {
          this.cartIds = selectedCartItems.map(item => item.id);
          this.loadCartConfirmOrder();
        } else {
          uni.showToast({
            title: '没有可下单的商品',
            icon: 'none'
          });
          setTimeout(() => {
            uni.navigateBack();
          }, 1500);
        }
      }
      
      // 如果不是从商品详情页跳转或没有传递地址信息，则获取用户默认地址
      if (!productPageAddress) {
        this.getDefaultAddress();
      }

      // 如果当前是自提模式，加载门店列表
      if (this.orderType === 1) {
        this.loadStoreList();
      }
    } catch (error) {
      console.error('订单页面加载错误', error);
      uni.showToast({
        title: '订单数据加载失败',
        icon: 'none'
      });
    }
  },
  
  onShow() {
  },
  
  methods: {
    // 选择订单类型
    e0() {
      this.orderType = 0;
      // 切换到快递配送时重新计算运费并重新选择优惠券
      this.calculateAmount();
      this.autoSelectBestCoupon();
      this.calculateAmount(); // 重新计算包含新优惠券的金额
    },

    // 选择自提方式
    e1() {
      this.orderType = 1;
      // 切换到自提时加载门店列表
      this.loadStoreList();
      // 切换到自提时重新计算运费并重新选择优惠券
      this.calculateAmount();
      this.autoSelectBestCoupon();
      this.calculateAmount(); // 重新计算包含新优惠券的金额
    },

    // 获取当前位置
    getCurrentLocation() {
      console.log('开始获取位置...');
      uni.getLocation({
        type: 'gcj02',
        success: (res) => {
          console.log('位置获取成功:', res);
          this.currentLocation = {
            longitude: res.longitude,
            latitude: res.latitude
          };
          console.log('当前位置设置为:', this.currentLocation);
          // 位置获取成功后，重新计算所有门店的距离
          this.recalculateDistances();
        },
        fail: (err) => {
          console.warn('获取位置失败:', err);
        }
      });
    },

    // 重新计算所有门店的距离
    recalculateDistances() {
      console.log('开始重新计算距离, 门店数量:', this.storeList?.length);
      console.log('当前位置:', this.currentLocation);
      if (this.storeList && this.storeList.length > 0) {
        this.storeList.forEach((store, index) => {
          const oldDistance = store.distance;
          store.distance = this.calculateDistance(store);
          console.log(`门店${index + 1} (${store.addressName}):`, {
            经度: store.longitude,
            纬度: store.latitude,
            原距离: oldDistance,
            新距离: store.distance
          });
        });
        console.log('距离重新计算完成');
      } else {
        console.log('没有门店数据需要计算距离');
      }
    },

    // 计算距离
    calculateDistance(store) {
      console.log('计算距离 - 输入参数:', {
        currentLocation: this.currentLocation,
        storeLongitude: store.longitude,
        storeLatitude: store.latitude,
        storeName: store.addressName
      });

      if (!this.currentLocation || !store.longitude || !store.latitude) {
        console.log('距离计算失败 - 缺少必要参数');
        return null;
      }

      const lat1 = this.currentLocation.latitude;
      const lng1 = this.currentLocation.longitude;
      const lat2 = parseFloat(store.latitude);
      const lng2 = parseFloat(store.longitude);

      console.log('距离计算 - 坐标信息:', {
        用户位置: { lat: lat1, lng: lng1 },
        门店位置: { lat: lat2, lng: lng2 }
      });

      // 验证坐标有效性
      if (isNaN(lat2) || isNaN(lng2)) {
        console.log('距离计算失败 - 门店坐标无效');
        return null;
      }

      const R = 6371; // 地球半径（公里）
      const dLat = (lat2 - lat1) * Math.PI / 180;
      const dLng = (lng2 - lng1) * Math.PI / 180;
      const a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                Math.sin(dLng/2) * Math.sin(dLng/2);
      const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
      const distance = R * c;

      console.log('距离计算结果:', distance, 'km');
      return distance;
    },

    // 加载门店列表
    async loadStoreList() {
      try {
        let res;
        // 如果有学校筛选条件，使用学校筛选接口
        if (this.schoolFilter && this.schoolFilter.id) {
          res = await fetchStoreListBySchool(this.schoolFilter.id);
        } else {
          res = await fetchStoreList();
        }

        if (res && res.code === 200 && res.data) {
          this.storeList = res.data.map(store => {
            const processedStore = {
              id: store.id,
              addressName: store.addressName || store.name || store.storeName || '',
              address: store.address || store.detailAddress || '',
              logo: store.logo || 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/guanghengzou_logo.png',
              businessHours: store.businessHours || '10:00-18:40',
              phone: store.phone || '',
              province: store.province || '',
              city: store.city || '',
              region: store.region || '',
              longitude: store.longitude,
              latitude: store.latitude,
              schoolId: store.schoolId || null,
              schoolName: store.schoolName || '',
              storeGallery: store.storeGallery || '',
              distance: null // 将在后面计算
            };

            // 计算距离
            processedStore.distance = this.calculateDistance(processedStore);

            return processedStore;
          });

          // 门店数据加载完成后，如果已有位置信息，重新计算距离
          if (this.currentLocation) {
            console.log('门店数据加载完成，重新计算距离');
            this.recalculateDistances();
          }
        } else {
          console.error('获取门店列表失败:', res);
        }
      } catch (error) {
        console.error('加载门店列表失败:', error);
      }
    },

    // 地图门店选择事件
    onStoreSelected(store) {
      this.selectedStore = store;
    },

    // 门店卡片点击事件
    onStoreCardClick(store) {
      this.selectedStore = store;
    },

    // 访问门店事件 - 与storeList页面保持一致的导航逻辑
    onVisitStore(store) {
      console.log('访问门店:', store);

      if (!store.latitude || !store.longitude) {
        uni.showToast({
          title: '门店位置信息不完整',
          icon: 'none'
        });
        return;
      }

      const latitude = parseFloat(store.latitude);
      const longitude = parseFloat(store.longitude);
      const name = store.addressName || '目标门店';
      const address = this.getStoreFullAddress(store);

      // 检查是否为有效坐标
      if (isNaN(latitude) || isNaN(longitude)) {
        uni.showToast({
          title: '门店坐标信息无效',
          icon: 'none'
        });
        return;
      }

      // 检查位置权限
      uni.getSetting({
        success: (res) => {
          if (res.authSetting['scope.userLocation'] === false) {
            // 用户拒绝了位置权限，引导用户开启
            uni.showModal({
              title: '位置权限',
              content: '需要位置权限才能使用导航功能，请在设置中开启',
              confirmText: '去设置',
              success: (modalRes) => {
                if (modalRes.confirm) {
                  uni.openSetting();
                }
              }
            });
            return;
          }

          // 使用uni.openLocation打开地图导航
          this.doOpenLocation(latitude, longitude, name, address);
        },
        fail: () => {
          // 获取设置失败，直接尝试打开导航
          this.doOpenLocation(latitude, longitude, name, address);
        }
      });
    },

    // 执行打开地图导航
    doOpenLocation(latitude, longitude, name, address) {
      // 优先使用腾讯地图路线规划插件
      if (this.canUseRoutePlanPlugin()) {
        this.openTencentMapNavigation(latitude, longitude, name, address);
      } else {
        // 降级到系统导航
        console.log('当前环境不支持腾讯地图插件，使用系统导航');
        this.openSystemNavigation(latitude, longitude, name, address);
      }
    },

    // 打开系统默认导航
    openSystemNavigation(latitude, longitude, name, address) {
      uni.openLocation({
        latitude: latitude,
        longitude: longitude,
        name: name,
        address: address,
        scale: 16,
        success: () => {
          console.log('成功打开系统导航');
        },
        fail: (err) => {
          console.error('系统导航失败:', err);
          // 提供手动选择方案
          this.showNavigationOptions(latitude, longitude, name, address);
        }
      });
    },

    // 检查是否可以使用路线规划插件
    canUseRoutePlanPlugin() {
      // #ifdef MP-WEIXIN
      return true; // 在微信小程序中尝试使用腾讯地图插件
      // #endif

      // #ifndef MP-WEIXIN
      return false;
      // #endif
    },

    // 使用腾讯地图路线规划插件导航
    openTencentMapNavigation(latitude, longitude, name, address) {
      // #ifdef MP-WEIXIN
      try {
        const key = 'ZEOBZ-FKOCX-3PM4E-7WRYL-FEMZV-ULF5W'; // 腾讯地图key
        const referer = '广横走x鲤享文创'; // 和腾讯地图开放平台的应用名称一致

        // 先获取用户当前位置作为起点
        uni.getLocation({
          type: 'gcj02',
          success: (locationRes) => {
            this.doOpenTencentMapWithLocation(locationRes, latitude, longitude, name, address, key, referer);
          },
          fail: (locationErr) => {
            console.warn('获取当前位置失败，使用默认起点:', locationErr);
            // 如果获取位置失败，使用默认位置或仅传终点
            this.doOpenTencentMapWithLocation(null, latitude, longitude, name, address, key, referer);
          }
        });
      } catch (e) {
        console.error('腾讯地图插件调用异常:', e);
        uni.showToast({
          title: '腾讯地图插件调用异常',
          icon: 'none',
          duration: 2000
        });
      }
      // #endif
    },

    // 执行腾讯地图导航（带位置信息）
    doOpenTencentMapWithLocation(currentLocation, latitude, longitude, name, address, key, referer) {
      try {
        // 构建终点信息对象
        const endPointData = {
          name: name,
          latitude: parseFloat(latitude),
          longitude: parseFloat(longitude)
        };

        // 构建起点信息对象（如果有当前位置）
        let startPointData = null;
        if (currentLocation) {
          startPointData = {
            name: '我的位置',
            latitude: currentLocation.latitude,
            longitude: currentLocation.longitude
          };
        }

        // 转换为JSON字符串，并处理URL中的特殊字符
        const endPointJson = JSON.stringify(endPointData);
        // 只对可能破坏URL的字符进行替换，而不是完整URL编码
        const safeEndPointJson = endPointJson.replace(/&/g, '%26').replace(/#/g, '%23');

        // 使用官方文档推荐的参数配置，现在跳转权限已配置，可以正常使用
        let pluginUrl = `plugin://routePlan/index?key=${key}&referer=${encodeURIComponent(referer)}&endPoint=${safeEndPointJson}&navigation=1`;

        // 如果有起点信息，添加到URL中
        if (startPointData) {
          const startPointJson = JSON.stringify(startPointData);
          const safeStartPointJson = startPointJson.replace(/&/g, '%26').replace(/#/g, '%23');
          pluginUrl += `&startPoint=${safeStartPointJson}`;
        }

        console.log('腾讯地图导航参数:', {
          起点数据: startPointData,
          终点数据: endPointData,
          完整URL: pluginUrl
        });

        uni.navigateTo({
          url: pluginUrl,
          success: (res) => {
            console.log('成功打开腾讯地图路线规划:', res);
          },
          fail: (error) => {
            console.error('腾讯地图路线规划失败:', error);

            // 如果是跳转权限问题（实物交易管控），降级到系统导航
            if (error.errMsg && error.errMsg.includes('banded')) {
              console.log('检测到实物交易小程序跳转管控，降级到系统导航');
              uni.showToast({
                title: '正在为您打开系统导航',
                icon: 'none',
                duration: 1500
              });
              setTimeout(() => {
                this.openSystemNavigation(latitude, longitude, name, address);
              }, 500);
            } else {
              uni.showToast({
                title: '腾讯地图插件调用失败',
                icon: 'none',
                duration: 2000
              });
            }
          }
        });
      } catch (e) {
        console.error('构建腾讯地图URL异常:', e);
        uni.showToast({
          title: '导航参数构建失败',
          icon: 'none',
          duration: 2000
        });
      }
    },

    // 显示导航选项（备用方案）
    showNavigationOptions(latitude, longitude, name, address) {
      const actionSheetItems = [
        '腾讯地图导航',
        '打开地图页面',
        '复制门店信息',
        '复制地址',
        '复制坐标',
        '重试导航'
      ];

      uni.showActionSheet({
        itemList: actionSheetItems,
        success: (res) => {
          switch (res.tapIndex) {
            case 0:
              // 腾讯地图导航
              this.openTencentMapNavigation(latitude, longitude, name, address);
              break;
            case 1:
              // 打开地图页面
              this.openMapPage(latitude, longitude, name, address);
              break;
            case 2:
              // 复制门店信息
              const storeInfo = `${name}\n地址：${address}\n坐标：${latitude},${longitude}`;
              uni.setClipboardData({
                data: storeInfo,
                success: () => {
                  uni.showToast({
                    title: '门店信息已复制',
                    icon: 'success'
                  });
                }
              });
              break;
            case 3:
              // 复制地址
              uni.setClipboardData({
                data: address,
                success: () => {
                  uni.showToast({
                    title: '地址已复制',
                    icon: 'success'
                  });
                }
              });
              break;
            case 4:
              // 复制坐标
              const coordinates = `${latitude},${longitude}`;
              uni.setClipboardData({
                data: coordinates,
                success: () => {
                  uni.showToast({
                    title: '坐标已复制',
                    icon: 'success'
                  });
                }
              });
              break;
            case 5:
              // 重试导航
              this.doOpenLocation(latitude, longitude, name, address);
              break;
          }
        },
        fail: () => {
          uni.showToast({
            title: '操作取消',
            icon: 'none'
          });
        }
      });
    },

    // 打开地图页面（使用内置地图组件）
    openMapPage(latitude, longitude, name, address) {
      // 可以跳转到一个专门的地图页面，或者使用chooseLocation
      uni.chooseLocation({
        latitude: parseFloat(latitude),
        longitude: parseFloat(longitude),
        success: (res) => {
          console.log('用户选择的位置:', res);
          // 可以根据用户选择的位置进行后续操作
        },
        fail: (err) => {
          console.error('打开地图选择失败:', err);
          uni.showToast({
            title: '无法打开地图选择',
            icon: 'none'
          });
        }
      });
    },

    // 获取门店完整地址
    getStoreFullAddress(store) {
      const { province = '', city = '', region = '', address = '', detailAddress = '' } = store;
      return `${province}${city}${region}${address || detailAddress}`;
    },

    // 从购物车加载确认订单信息
    loadCartConfirmOrder() {
      this.loading = true;
      uni.showLoading({
        title: '加载中...'
      });
      
      // 确保cartIds是数组，并且序列化为字符串传递
      const cartIdsParam = Array.isArray(this.cartIds) ? JSON.stringify(this.cartIds) : JSON.stringify([this.cartIds]);
      
      generateConfirmOrder(cartIdsParam).then(response => {
        uni.hideLoading();
        this.loading = false;
        
        if (response.code === 200 && response.data) {
          // 设置地址列表
          this.memberReceiveAddressList = response.data.memberReceiveAddressList || [];
          // 如果当前没有地址，则设置默认地址
          if (!this.currentAddress) {
            this.currentAddress = this.getDefaultAddress();
          }
          
          // 设置商品列表
          this.cartList = response.data.cartPromotionItemList || [];
          
          // 处理商品属性显示
          this.processCartItems();
          
          // 设置优惠券列表
          this.couponList = [];
          if (response.data.couponHistoryDetailList) {
            for (let item of response.data.couponHistoryDetailList) {
              this.couponList.push(item.coupon);
            }
          }
          
          // 设置计算金额
          this.calcAmount = response.data.calcAmount || {
            totalAmount: 0,
            promotionAmount: 0,
            freightAmount: 0,
            payAmount: 0
          };
          
          // 设置积分规则
          this.integrationConsumeSetting = response.data.integrationConsumeSetting || {};
          this.memberIntegration = response.data.memberIntegration || 0;

          // 设置用户余额
          this.memberBalance = response.data.memberBalance || 0;
          
          // 自动选择最优惠券
          this.autoSelectBestCoupon();
          
          // 根据当前配送方式重新计算运费
          this.calculateAmount();
        } else {
          uni.showToast({
            title: response.message || '获取订单数据失败',
            icon: 'none'
          });
          setTimeout(() => {
            uni.navigateBack();
          }, 1500);
        }
      }).catch(err => {
        console.error('获取订单数据失败', err);
        uni.hideLoading();
        this.loading = false;
        
        // 提取具体的错误信息
        let errorMessage = '获取订单数据失败';
        if (err && err.data && err.data.message) {
          errorMessage = err.data.message;
        } else if (err && err.message) {
          errorMessage = err.message;
        }
        
        uni.showToast({
          title: errorMessage,
          icon: 'none'
        });
        setTimeout(() => {
          uni.navigateBack();
        }, 1500);
      });
    },
    
    // 加载直接购买的商品确认订单
    loadDirectBuyConfirmOrder() {
      this.loading = true;
      uni.showLoading({
        title: '加载中...'
      });
      
      try {
        // 构建完整的DirectBuyParam参数
        let params = {
          productId: this.directBuyItem.productId,
          productSkuId: this.directBuyItem.productSkuId,
          quantity: this.directBuyItem.quantity,
          price: this.directBuyItem.price,
          productAttr: this.directBuyItem.productAttr,
          productBrand: this.directBuyItem.productBrand,
          productCategoryId: this.directBuyItem.productCategoryId,
          productName: this.directBuyItem.productName,
          productPic: this.directBuyItem.productPic,
          productSkuCode: this.directBuyItem.productSkuCode,
          productSn: this.directBuyItem.productSn,
          productSubTitle: this.directBuyItem.productSubTitle,
          purchaseType: (this.directBuyItem.purchaseType === true || this.directBuyItem.purchaseType === 1) ? 1 : 0,
          deliveryType: this.deliveryType || 0 // 0->快递配送；1->门店自取
        };
        
        console.log('直接购买参数:', JSON.stringify(params));
        
        generateDirectConfirmOrder(params).then(response => {
          uni.hideLoading();
          this.loading = false;
          
          if (response.code === 200 && response.data) {
            // 设置地址列表
            this.memberReceiveAddressList = response.data.memberReceiveAddressList || [];
            // 如果当前没有地址（未从商品详情页传递），则设置默认地址
            if (!this.currentAddress) {
              this.currentAddress = this.getDefaultAddress();
            }
            
            // 设置商品列表
            this.cartList = response.data.cartPromotionItemList || [];
            
            // 保存原始促销信息数据，用于提交订单时使用
            this.cartPromotionItemList = this.deepClone(response.data.cartPromotionItemList) || [];
            
            // 处理商品属性显示
            this.processCartItems();
            
            // 设置优惠券列表
            this.couponList = [];
            if (response.data.couponHistoryDetailList) {
              for (let item of response.data.couponHistoryDetailList) {
                this.couponList.push(item.coupon);
              }
            }
            
            // 设置计算金额
            this.calcAmount = response.data.calcAmount || {
              totalAmount: 0,
              promotionAmount: 0,
              freightAmount: 0,
              payAmount: 0
            };
            
            // 设置积分规则
            this.integrationConsumeSetting = response.data.integrationConsumeSetting || {};
            this.memberIntegration = response.data.memberIntegration || 0;

            // 设置用户余额
            this.memberBalance = response.data.memberBalance || 0;
            
            // 自动选择最优惠券
            this.autoSelectBestCoupon();
            
            // 根据当前配送方式重新计算运费
            this.calculateAmount();
          } else {
            uni.showToast({
              title: response.message || '获取订单数据失败',
              icon: 'none'
            });
            setTimeout(() => {
              uni.navigateBack();
            }, 1500);
          }
        }).catch(err => {
          console.error('获取订单数据失败', err);
          uni.hideLoading();
          this.loading = false;
          
          uni.showToast({
            title: '获取订单数据失败',
            icon: 'none'
          });
          setTimeout(() => {
            uni.navigateBack();
          }, 1500);
        });
      } catch (error) {
        console.error('构建直接购买参数失败:', error);
        uni.hideLoading();
        this.loading = false;
        uni.showToast({
          title: '构建参数失败',
          icon: 'none'
        });
        setTimeout(() => {
          uni.navigateBack();
        }, 1500);
      }
    },
    
    // 加载组合商品确认订单
    loadBundleConfirmOrder() {
      console.log('loadBundleConfirmOrder 开始加载，数据:', this.bundleOrderData);
      this.loading = true;
      uni.showLoading({
        title: '加载中...'
      });
      
      import('@/api/bundle.js').then(({ generateBundleConfirmOrder }) => {
        console.log('API模块加载成功，开始请求');
        generateBundleConfirmOrder(this.bundleOrderData).then(response => {
          console.log('API响应:', response);
          uni.hideLoading();
          this.loading = false;
          
          if (response.code === 200 && response.data) {
            // 保存组合信息
            this.bundleInfo = response.data.bundleInfo;
            
            // 设置地址列表
            this.memberReceiveAddressList = response.data.memberReceiveAddressList || [];
            if (!this.currentAddress) {
              this.currentAddress = this.getDefaultAddress();
            }
            
            // 将组合商品订单项转换为购物车列表格式
            this.cartList = (response.data.orderItems || []).map(item => ({
              productId: item.productId,
              productName: item.productName,
              productPic: item.productPic,
              productSkuId: item.skuId,
              productSkuCode: item.skuCode,
              productAttr: item.spData,
              price: item.price,
              quantity: item.quantity,
              bundleDiscount: item.bundleDiscount
            }));
            
            // 设置优惠券列表
            this.couponList = [];
            if (response.data.couponHistoryDetailList) {
              for (let item of response.data.couponHistoryDetailList) {
                this.couponList.push(item.coupon);
              }
            }
            
            // 设置计算金额（组合商品使用bundleDiscountAmount作为promotionAmount）
            const calcAmountData = response.data.calcAmount || {};
            this.calcAmount = {
              totalAmount: calcAmountData.totalAmount || 0,
              promotionAmount: calcAmountData.bundleDiscountAmount || calcAmountData.promotionAmount || 0,
              freightAmount: calcAmountData.freightAmount || 0,
              couponAmount: calcAmountData.couponAmount || 0,
              integrationAmount: calcAmountData.integrationAmount || 0,
              payAmount: calcAmountData.payAmount || 0
            };
            
            // 设置积分规则
            this.integrationConsumeSetting = response.data.integrationConsumeSetting || {};
            this.memberIntegration = response.data.memberIntegration || 0;
            this.memberBalance = response.data.memberBalance || 0;
            
            // 处理商品属性显示
            this.processCartItems();
            console.log('组合商品订单数据加载完成, calcAmount:', this.calcAmount);
          } else {
            console.error('API返回错误:', response);
            uni.showToast({
              title: response.message || '获取订单数据失败',
              icon: 'none'
            });
            setTimeout(() => {
              uni.navigateBack();
            }, 1500);
          }
        }).catch(err => {
          console.error('获取组合商品订单数据失败', err);
          uni.hideLoading();
          this.loading = false;
          uni.showToast({
            title: '获取订单数据失败',
            icon: 'none'
          });
        });
      }).catch(err => {
        console.error('加载bundle API模块失败', err);
        uni.hideLoading();
        this.loading = false;
        uni.showToast({
          title: 'API模块加载失败',
          icon: 'none'
        });
      });
    },
    
    // 深拷贝对象
    deepClone(obj) {
      if (obj === null || typeof obj !== 'object') {
        return obj;
      }
      
      if (Array.isArray(obj)) {
        return obj.map(item => this.deepClone(item));
      }
      
      const result = {};
      for (const key in obj) {
        if (Object.prototype.hasOwnProperty.call(obj, key)) {
          result[key] = this.deepClone(obj[key]);
        }
      }
      
      return result;
    },
    
    // 处理购物车商品属性显示
    processCartItems() {
      for (let item of this.cartList) {
        // 处理商品规格显示
        try {
          if (item.productAttr) {
            let attrObj = typeof item.productAttr === 'string' ? JSON.parse(item.productAttr) : item.productAttr;
            let attrStr = '';
            
            if (Array.isArray(attrObj)) {
              for (let attr of attrObj) {
                attrStr += attr.key + ': ' + attr.value + '; ';
              }
            }
            
            item.spDataStr = attrStr;
          }
        } catch (e) {
          console.error('处理商品属性出错', e);
          item.spDataStr = '';
        }
      }
    },
    
    // 获取用户默认地址
    getDefaultAddress() {
      try {
        // 如果已有地址列表，从中获取默认地址
        if (this.memberReceiveAddressList && this.memberReceiveAddressList.length > 0) {
          // 查找默认地址
          const defaultAddress = this.memberReceiveAddressList.find(item => item.defaultStatus === 1);
          if (defaultAddress) {
            this.currentAddress = defaultAddress;
            return defaultAddress;
          } else {
            // 如果没有默认地址，使用第一个地址
            this.currentAddress = this.memberReceiveAddressList[0];
            return this.memberReceiveAddressList[0];
          }
        }
        
        // 从本地存储获取默认地址
        const addressList = uni.getStorageSync('addressList');
        if (addressList && addressList.length > 0) {
          // 查找默认地址
          const defaultAddress = addressList.find(item => item.isDefault === 1);
          if (defaultAddress) {
            this.currentAddress = defaultAddress;
            return defaultAddress;
          } else {
            // 如果没有默认地址，使用第一个地址
            this.currentAddress = addressList[0];
            return addressList[0];
          }
        }
        
        this.currentAddress = null;
        return null;
      } catch (error) {
        console.error('获取默认地址出错:', error);
        this.currentAddress = null;
        return null;
      }
    },
    
    // 获取微信地址
    async getWechatAddress() {
      // #ifdef MP-WEIXIN
      uni.chooseAddress({
        success: async (res) => {
          console.log('微信地址获取成功:', res);

          try {
            // 将微信地址转换为应用格式
            const addressData = {
              name: res.userName,
              phoneNumber: res.telNumber,
              province: res.provinceName,
              city: res.cityName,
              region: res.countyName,
              detailAddress: res.detailInfo,
              isDefault: 0 // 默认不设为默认地址
            };

            // 显示保存中提示
            uni.showLoading({
              title: '保存地址中...'
            });

            // 调用后端API保存地址
            const saveResult = await addAddress(addressData);

            uni.hideLoading();

            if (saveResult && saveResult.code === 200) {
              // 保存成功，设置为当前选中地址
              this.currentAddress = {
                ...addressData,
                id: saveResult.data // 后端返回的地址ID
              };

              uni.showToast({
                title: '地址保存成功',
                icon: 'success'
              });

              console.log('地址保存成功:', this.currentAddress);
            } else {
              // 保存失败，但仍然可以临时使用
              this.currentAddress = addressData;

              uni.showToast({
                title: '地址保存失败，但可临时使用',
                icon: 'none',
                duration: 2000
              });

              console.error('地址保存失败:', saveResult);
            }

            // 保存到本地存储（作为备份）
            const addressList = uni.getStorageSync('addressList') || [];
            const index = addressList.findIndex(item =>
              item.phoneNumber === res.telNumber && item.detailAddress === res.detailInfo
            );

            if (index !== -1) {
              // 更新已有地址
              addressList[index] = this.currentAddress;
            } else {
              // 添加新地址
              addressList.push(this.currentAddress);
            }

            uni.setStorageSync('addressList', addressList);

            // 重新计算金额
            this.calculateAmount();

          } catch (error) {
            uni.hideLoading();
            console.error('保存地址时发生错误:', error);

            // 即使保存失败，也设置为临时地址
            this.currentAddress = {
              name: res.userName,
              phoneNumber: res.telNumber,
              province: res.provinceName,
              city: res.cityName,
              region: res.countyName,
              detailAddress: res.detailInfo,
              isDefault: 0
            };

            uni.showToast({
              title: '地址保存失败，但可临时使用',
              icon: 'none',
              duration: 2000
            });

            // 重新计算金额
            this.calculateAmount();
          }
        },
        fail: (err) => {
          console.error('获取微信地址失败', err);
          if (err.errMsg !== 'chooseAddress:fail cancel') {
            uni.showToast({
              title: '获取地址失败，请手动添加',
              icon: 'none'
            });
          }
        }
      });
      // #endif
      
      // #ifndef MP-WEIXIN
      uni.showToast({
        title: '非微信环境无法使用此功能',
        icon: 'none'
      });
      // #endif
    },
    








    
    // 切换优惠券弹窗
    toggleMask(type) {
      // 显示弹窗时，检查优惠券是否可用
      if (type === 'show' && (!this.couponList || this.couponList.length === 0)) {
        uni.showToast({
          title: '暂无可用优惠券',
          icon: 'none',
          duration: 1500
        });
        return;
      }

      // 使用新的弹窗组件
      if (type === 'show') {
        this.showCouponPopup = true;
      } else {
        this.showCouponPopup = false;
      }
    },

    // 关闭优惠券弹窗
    closeCouponPopup() {
      this.showCouponPopup = false;
    },

    // 确认优惠券选择
    confirmCouponSelection(coupon) {
      this.currCoupon = coupon;
      this.calcPayAmount();
      this.showCouponPopup = false;
    },
    

    
    // 阻止事件冒泡
    stopPrevent() {},
    
    // 自动选择最优惠券
    autoSelectBestCoupon() {
      if (!this.couponList || this.couponList.length === 0) {
        this.currCoupon = null;
        return;
      }

      // 筛选出满足条件的优惠券（订单金额 >= 优惠券最低使用要求）
      const availableCoupons = this.couponList.filter(coupon => {
        return coupon.minPoint <= this.calcAmount.totalAmount;
      });

      if (availableCoupons.length === 0) {
        // 没有满足条件的优惠券
        if (this.currCoupon) {
          console.log('当前优惠券不再满足条件，清除选择');
          this.currCoupon = null;
        }
        return;
      }

      // 从满足条件的优惠券中选择优惠金额最大的
      const bestCoupon = availableCoupons.reduce((prev, current) => {
        const prevDiscount = this.calculateCouponDiscount(prev);
        const currentDiscount = this.calculateCouponDiscount(current);
        return (currentDiscount > prevDiscount) ? current : prev;
      });

      // 检查当前优惠券是否还满足条件
      if (this.currCoupon && !availableCoupons.find(c => c.id === this.currCoupon.id)) {
        // 当前优惠券不再满足条件，清除并选择新的
        console.log('当前优惠券不再满足条件，重新选择');
        this.currCoupon = bestCoupon;
        console.log('自动选择优惠券:', bestCoupon);
      } else if (!this.currCoupon) {
        // 没有选中优惠券，选择最优的
        this.currCoupon = bestCoupon;
        console.log('自动选择优惠券:', bestCoupon);
      } else {
        // 比较当前优惠券和最优优惠券的优惠金额
        const currentDiscount = this.calculateCouponDiscount(this.currCoupon);
        const bestDiscount = this.calculateCouponDiscount(bestCoupon);
        if (bestDiscount > currentDiscount) {
          this.currCoupon = bestCoupon;
          console.log('找到更优惠券，自动切换:', bestCoupon);
        } else {
          console.log('当前优惠券已是最优，无需更换');
        }
      }
    },

    // 计算优惠券折扣金额
    calculateCouponDiscount(coupon) {
      if (!coupon) return 0;

      const orderAmount = this.calcAmount.totalAmount - this.calcAmount.promotionAmount;

      // 检查使用门槛
      if (coupon.minPoint && orderAmount < coupon.minPoint) {
        return 0;
      }

      if (coupon.couponType === 1) {
        // 打折券：订单金额 * (1 - 折扣率) = 优惠金额
        if (coupon.discountRate) {
          const discount = orderAmount * (1 - coupon.discountRate);
          return parseFloat(discount.toFixed(2)); // 修复浮点数精度问题
        }
      } else {
        // 满减券：直接返回面额
        if (coupon.amount) {
          return coupon.amount;
        }
      }

      return 0;
    },

    // 选择优惠券
    selectCoupon(coupon) {
      // 检查优惠券是否满足使用条件
      if (coupon.minPoint > this.calcAmount.totalAmount) {
        uni.showToast({
          title: `订单金额需满${coupon.minPoint}元才可使用此优惠券`,
          icon: 'none',
          duration: 2000
        });
        return;
      }
      
      this.currCoupon = coupon;
      this.calcPayAmount();
      this.toggleMask();
    },
    
    // 处理积分输入
    handleIntegrationInput() {
      // 输入格式化为数字
      if (this.useIntegration === '') {
        this.integrationAmount = 0;
        this.calcPayAmount();
        return;
      }

      let integration = parseInt(this.useIntegration);
      if (isNaN(integration)) {
        integration = 0;
      }

      // 限制最大值
      if (integration > this.memberIntegration) {
        integration = this.memberIntegration;
        this.useIntegration = String(integration);
        uni.showToast({
          title: `您的积分只有${this.memberIntegration}`,
          duration: 1000
        });
      }

      // 计算抵扣金额
      this.integrationAmount = this.calcIntegrationAmount(integration);

      // 重新计算订单金额
      this.calcPayAmount();
    },
    
    // 计算积分抵扣金额
    calcIntegrationAmount(integration) {
      if (!this.integrationConsumeSetting || this.integrationConsumeSetting.couponStatus === 0) {
        return 0;
      }
      
      const amount = integration / this.integrationConsumeSetting.deductionPerAmount;
      
      // 检查是否超过最大抵扣比例
      const maxAmount = this.calcAmount.totalAmount * (this.integrationConsumeSetting.maxPercentPerOrder / 100);
      return Math.min(amount, maxAmount);
    },
    
    // 计算订单金额
    calculateAmount() {
      this.calcPayAmount();
    },
    
    // 计算支付金额
    calcPayAmount() {
      // 初始金额 = 总金额 - 促销折扣
      let payAmount = this.calcAmount.totalAmount - this.calcAmount.promotionAmount;

      // 计算运费：现场自取不收运费，快递配送才收运费
      let freightAmount = 0;
      if (this.orderType === 0) {
        // 快递配送时使用原有运费
        freightAmount = this.calcAmount.freightAmount || 0;
      } else {
        // 现场自取时运费为0
        freightAmount = 0;
      }

      // 加上运费
      payAmount += freightAmount;

      // 减去优惠券金额（使用新的计算方法）
      if (this.currCoupon) {
        const couponDiscount = this.calculateCouponDiscount(this.currCoupon);
        payAmount -= couponDiscount;
      }

      // 减去积分抵扣金额
      if (this.useIntegration) {
        const integrationAmount = this.calcIntegrationAmount(parseInt(this.useIntegration));
        payAmount -= integrationAmount;
        this.integrationAmount = integrationAmount;
      }

      // 保证金额不小于0
      if (payAmount < 0) {
        payAmount = 0;
      }

      // 更新应付金额
      this.calcAmount.payAmount = parseFloat(payAmount.toFixed(2));
    },
    
    // 格式化时间
    formatTime(time) {
      if (!time) return '';
      
      const date = new Date(time);
      return formatDate(date, 'yyyy-MM-dd');
    },

    // 获取运费显示文本
    getFreightDisplayText() {
      if (this.orderType === 1) {
        // 现场自取
        return '门店自取';
      } else {
        // 快递配送
        return this.calcAmount.freightAmount > 0 ? '￥' + this.calcAmount.freightAmount : '免费配送';
      }
    },
    
    // 检查订单信息是否完整
    validateOrder() {
      // 如果是送礼订单，跳过地址和门店验证
      if (this.isGift !== 1) {
        if (this.orderType === 0) { // 快递邮寄
          if (!this.currentAddress) {
            uni.showToast({
              title: '请选择收货地址',
              icon: 'none'
            });
            return false;
          }
        } else { // 现场自取
          if (!this.selectedStore) {
            uni.showToast({
              title: '请选择自取门店',
              icon: 'none'
            });
            return false;
          }
        }
      }
      
      if (this.cartList.length === 0) {
        uni.showToast({
          title: '订单中没有商品',
          icon: 'none'
        });
        return false;
      }
      
      return true;
    },
    
    // 提交订单
    submit() {
      if (this.loading || this.isPaymentLoading) return;
      
      // 验证订单
      if (!this.validateOrder()) {
        return;
      }
      
      this.loading = true;
      uni.showLoading({
        title: '提交订单中...'
      });
      
      try {
        // 组合商品订单走单独的下单逻辑
        if (this.isBundleOrder) {
          this.submitBundleOrder();
          return;
        }

        // 构建订单参数
        const orderParam = {
          payType: this.selectedPaymentMethod === 'balance' ? 3 : 2, // 支付方式: 0->未支付, 1->支付宝, 2->微信， 3->余额支付
          useIntegration: this.useIntegration ? parseInt(this.useIntegration) : 0,
          note: this.desc || "",
          isGift: this.isGift, // 是否送礼订单: 0->否, 1->是
          schoolId: this.schoolFilter ? parseInt(this.schoolFilter.id) : null // 添加学校ID
        };
        
        // 如果不是送礼订单，才传递地址信息
        if (this.isGift !== 1) {
          orderParam.memberReceiveAddressId = this.currentAddress ? this.currentAddress.id : 0;
        }
        
        // 检查优惠券是否满足使用条件，只有满足条件才添加
        if (this.currCoupon && this.currCoupon.minPoint <= this.calcAmount.totalAmount) {
          orderParam.couponId = this.currCoupon.id;
        }
        
        // 根据情况添加额外参数
        if (this.cartIds && this.cartIds.length > 0) {
          // 从购物车来的订单 - 确保类型正确
          orderParam.cartIds = this.cartIds;
        } else if (this.directBuyItem) {
          // 直接购买商品 - 封装到cartPromotionItemList参数中
          if (this.cartPromotionItemList && this.cartPromotionItemList.length > 0) {
            // 使用从确认订单接口返回的促销信息
            orderParam.cartPromotionItemList = this.cartPromotionItemList;
          } else {
            // 如果没有从接口获取到，则手动构建一个cartPromotionItem
            const cartPromotionItem = {
              productId: this.directBuyItem.productId,
              productSkuId: this.directBuyItem.productSkuId,
              quantity: this.directBuyItem.quantity,
              price: this.directBuyItem.price,
              productAttr: this.directBuyItem.productAttr,
              productBrand: this.directBuyItem.productBrand,
              productCategoryId: this.directBuyItem.productCategoryId,
              productName: this.directBuyItem.productName,
              productPic: this.directBuyItem.productPic,
              productSkuCode: this.directBuyItem.productSkuCode,
              productSn: this.directBuyItem.productSn,
              productSubTitle: this.directBuyItem.productSubTitle,
              purchaseType: (this.directBuyItem.purchaseType === true || this.directBuyItem.purchaseType === 1) ? 1 : 0,
              deliveryType: this.deliveryType || 0
            };

            orderParam.cartPromotionItemList = [cartPromotionItem];
          }
        }
        
        // 如果是礼物订单，添加礼物消息
        if (this.isGift === 1) {
          orderParam.giftMessage = this.giftMessage || "";
          console.log('传递给后端的礼物留言:', orderParam.giftMessage);
        }
        
        console.log('提交订单参数:', JSON.stringify(orderParam));
        
        // 调用生成订单接口
        generateOrder(orderParam).then(response => {
          uni.hideLoading();
          this.loading = false;
          
          if (response.code === 200 && response.data) {
            // 保存订单信息
            this.currentOrderInfo = {
              id: response.data.order.id,
              orderSn: response.data.order.orderSn,
              payAmount: response.data.order.payAmount
            };

            // 判断支付方式
            if (this.selectedPaymentMethod === 'balance') {
              // 余额支付：检查订单状态，如果已支付则直接跳转成功页面
              if (response.data.order.status === 1) {
                // 余额支付成功，直接跳转到支付成功页面
                this.handleBalancePaymentSuccess();
              } else {
                // 余额支付失败
                uni.showToast({
                  title: '余额支付失败，请重试',
                  icon: 'none'
                });
              }
            } else {
              // 微信支付：开始支付流程
              this.requestNotificationConsent();
            }
          } else {
            uni.showToast({
              title: response.message || '提交订单失败',
              icon: 'none'
            });
          }
        }).catch(err => {
          console.error('提交订单失败', err);
          uni.hideLoading();
          this.loading = false;
          
          // 尝试从错误响应中提取具体的错误信息
          let errorMessage = '提交订单失败，请重试';
          if (err && err.data && err.data.message) {
            errorMessage = err.data.message;
          } else if (err && err.message) {
            errorMessage = err.message;
          } else if (typeof err === 'string') {
            errorMessage = err;
          }
          
          uni.showToast({
            title: errorMessage,
            icon: 'none',
            duration: 3000
          });
        });
      } catch (error) {
        console.error('构建订单参数错误:', error);
        uni.hideLoading();
        this.loading = false;
        
        uni.showToast({
          title: '订单参数错误，请重试',
          icon: 'none',
          duration: 2000
        });
      }
    },
    
    // 请求订阅支付结果通知
    requestNotificationConsent() {
      // 微信小程序的订阅消息接口
      // 下单成功通知和物流发货通知
      const templateIds = [
        'H72bLIkhB2WllYtR20xAIV4HEGH179U2wLhE-i64Y7g', // 下单成功通知
        '5LeDpP78sOjg9W_WEASAfjMryI_Jps8XIHQjgu8mwho'  // 物流发货通知
      ];

      // 防止重复点击
      if (this.isPaymentLoading) return;

      // 订阅消息（仅微信小程序环境）
      // #ifdef MP-WEIXIN
      uni.requestSubscribeMessage({
        tmplIds: templateIds,
        success: (res) => {
          console.log('订阅消息结果', res);
          // 无论用户是否同意，都继续支付流程
          this.handlePay();
        },
        fail: (err) => {
          console.error('订阅消息失败', err);
          // 继续支付流程
          this.handlePay();
        }
      });
      // #endif

      // 非微信小程序环境直接处理支付
      // #ifndef MP-WEIXIN
      this.handlePay();
      // #endif
    },
    
    // 处理支付
    handlePay() {
      if (this.isPaymentLoading) return;
      this.isPaymentLoading = true;
      
      uni.showLoading({
        title: '支付处理中...'
      });
      
      // 确保订单信息完整
      if (!this.currentOrderInfo || !this.currentOrderInfo.orderSn) {
        uni.hideLoading();
        this.isPaymentLoading = false;
        
        uni.showToast({
          title: '订单信息不完整',
          icon: 'none'
        });
        return;
      }
      
      // 微信支付
      this.wxPay();
    },
    
    // 微信支付
    wxPay() {
      // 获取用户信息
      const userInfo = uni.getStorageSync('userInfo') || {};
      
      // 检查用户openId
      if (!userInfo.openid && !userInfo.wxOpenId) {
        uni.hideLoading();
        this.isPaymentLoading = false;
        
        uni.showToast({
          title: '用户信息不完整，请重新登录',
          icon: 'none'
        });
        
        setTimeout(() => {
          uni.navigateTo({
            url: '/pages/public/register'
          });
        }, 1500);
        return;
      }
      
      // 获取客户端IP地址（小程序获取不到真实IP，由服务端处理）
      let clientIP = '127.0.0.1';
      
      const payData = {
        orderSn: this.currentOrderInfo.orderSn,
        amount: parseInt(parseFloat(this.currentOrderInfo.payAmount) * 100), // 转换为分
        spbillCreateIp: clientIP, // 客户端IP
        openId: userInfo.openid || userInfo.wxOpenId // 从用户信息中获取openId
      };
      
      console.log('支付请求参数：', payData);
      
      getWechatPayParams(payData).then(res => {
        uni.hideLoading();
        
        if (res.code === 200 && res.data) {
          const payParams = res.data;
          
          // 调用微信支付API (仅在微信小程序环境)
          // #ifdef MP-WEIXIN
          uni.requestPayment({
            provider: 'wxpay',
            timeStamp: payParams.timeStamp,
            nonceStr: payParams.nonceStr,
            package: payParams.packageValue,
            signType: payParams.signType,
            paySign: payParams.paySign,
            success: (res) => {
              console.log('支付成功', res);
              this.paySuccess();
            },
            fail: (err) => {
              console.error('支付失败', err);
              
              let errMsg = '支付失败';
              if (err.errMsg === 'requestPayment:fail cancel') {
                errMsg = '支付已取消';
                // 支付取消，跳转到订单列表
                setTimeout(() => {
                  uni.redirectTo({
                    url: '/pages/order/order?state=0'
                  });
                }, 1000);
              } else {
                uni.showToast({
                  title: errMsg,
                  icon: 'none'
                });
              }
            },
            complete: () => {
              this.isPaymentLoading = false;
            }
          });
          // #endif
          
          // 非微信小程序环境
          // #ifndef MP-WEIXIN
          uni.showToast({
            title: '当前环境不支持微信支付',
            icon: 'none'
          });
          this.isPaymentLoading = false;
          
          // 跳转到订单列表
          setTimeout(() => {
            uni.redirectTo({
              url: '/pages/order/order?state=0'
            });
          }, 1500);
          // #endif
        } else {
          this.isPaymentLoading = false;
          uni.showToast({
            title: res.message || '获取支付参数失败',
            icon: 'none'
          });
          
          // 支付失败，询问是否查看订单
          setTimeout(() => {
            uni.showModal({
              title: '支付失败',
              content: '是否查看订单详情？',
              confirmText: '查看订单',
              cancelText: '返回',
              success: (modalRes) => {
                if (modalRes.confirm) {
                  uni.redirectTo({
                    url: '/pages/order/order?state=0'
                  });
                } else {
                  uni.navigateBack();
                }
              }
            });
          }, 1000);
        }
      }).catch(err => {
        uni.hideLoading();
        this.isPaymentLoading = false;
        
        console.error('获取支付参数失败', err);
        uni.showToast({
          title: '获取支付参数失败',
          icon: 'none'
        });
        
        // 支付失败，询问是否查看订单
        setTimeout(() => {
          uni.showModal({
            title: '支付失败',
            content: '是否查看订单详情？',
            confirmText: '查看订单',
            cancelText: '返回',
            success: (modalRes) => {
              if (modalRes.confirm) {
                uni.redirectTo({
                  url: '/pages/order/order?state=0'
                });
              } else {
                uni.navigateBack();
              }
            }
          });
        }, 1000);
      });
    },

    // 选择付款方式
    selectPaymentMethod(method) {
      // 如果选择余额支付，检查余额是否充足
      if (method === 'balance') {
        const payAmount = parseFloat(this.calcAmount.payAmount || 0);
        const balance = parseFloat(this.memberBalance || 0);

        if (balance < payAmount) {
          uni.showToast({
            title: '余额不足，请选择其他支付方式',
            icon: 'none',
            duration: 2000
          });
          return; // 不允许选择余额支付
        }
      }

      this.selectedPaymentMethod = method;
    },
    
    // 提交组合商品订单
    submitBundleOrder() {
      import('@/api/bundle.js').then(({ createBundleOrder }) => {
        // 构建组合商品订单参数
        const bundleOrderParam = {
          bundleId: this.bundleOrderData.bundleId,
          quantity: this.bundleOrderData.quantity,
          skuSelections: this.bundleOrderData.skuSelections,
          addressId: this.currentAddress ? this.currentAddress.id : null,
          payType: this.selectedPaymentMethod === 'balance' ? 3 : 2,
          deliveryType: this.orderType,
          note: this.desc || "",
          schoolId: this.schoolFilter ? parseInt(this.schoolFilter.id) : null
        };
        
        // 如果是自提订单，添加门店ID
        if (this.orderType === 1 && this.selectedStore) {
          bundleOrderParam.storeId = this.selectedStore.id;
        }
        
        // 检查优惠券
        if (this.currCoupon && this.currCoupon.minPoint <= this.calcAmount.totalAmount) {
          bundleOrderParam.couponId = this.currCoupon.id;
        }
        
        console.log('提交组合商品订单参数:', JSON.stringify(bundleOrderParam));
        
        createBundleOrder(bundleOrderParam).then(response => {
          uni.hideLoading();
          this.loading = false;
          
          if (response.code === 200 && response.data) {
            // 保存订单信息
            this.currentOrderInfo = {
              id: response.data.order.id,
              orderSn: response.data.order.orderSn,
              payAmount: response.data.order.payAmount
            };
            
            // 判断支付方式
            if (this.selectedPaymentMethod === 'balance') {
              if (response.data.order.status === 1) {
                this.handleBalancePaymentSuccess();
              } else {
                uni.showToast({
                  title: '余额支付失败，请重试',
                  icon: 'none'
                });
              }
            } else {
              // 微信支付
              this.requestNotificationConsent();
            }
          } else {
            uni.showToast({
              title: response.message || '提交订单失败',
              icon: 'none'
            });
          }
        }).catch(err => {
          console.error('提交组合商品订单失败', err);
          uni.hideLoading();
          this.loading = false;
          
          let errorMessage = '提交订单失败，请重试';
          if (err && err.data && err.data.message) {
            errorMessage = err.data.message;
          } else if (err && err.message) {
            errorMessage = err.message;
          }
          
          uni.showToast({
            title: errorMessage,
            icon: 'none',
            duration: 3000
          });
        });
      });
    },

    // 余额支付成功处理
    handleBalancePaymentSuccess() {
      uni.showToast({
        title: '余额支付成功',
        icon: 'success'
      });

      // 跳转到支付成功页
      setTimeout(() => {
        let url = `/pages/money/paySuccess?orderId=${this.currentOrderInfo.id}&orderSn=${this.currentOrderInfo.orderSn}&amount=${this.currentOrderInfo.payAmount}&payType=3`;

        // 如果是礼品订单，传递相关参数
        if (this.isGift === 1) {
          url += `&isGift=1`;
          if (this.giftMessage) {
            url += `&giftMessage=${encodeURIComponent(this.giftMessage)}`;
          }
        }

        uni.redirectTo({
          url: url
        });
      }, 1500);
    },

    // 支付成功处理
    paySuccess() {
      uni.showToast({
        title: '支付成功',
        icon: 'success'
      });
      
      // 跳转到支付成功页
      setTimeout(() => {
        let url = `/pages/money/paySuccess?orderId=${this.currentOrderInfo.id}&orderSn=${this.currentOrderInfo.orderSn}&amount=${this.currentOrderInfo.payAmount}`;
        
        // 如果是礼品订单，传递相关参数
        if (this.isGift === 1) {
          url += `&isGift=1`;
          if (this.giftMessage) {
            url += `&giftMessage=${encodeURIComponent(this.giftMessage)}`;
          }
        }
        
        uni.redirectTo({
          url: url
        });
      }, 1500);
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
  background: #F8F8F8;
  height: 100vh;
  overflow-x: hidden;
}

/* 邮寄模式下的底部间距 */
.container:not(.pickup-mode) {
  padding-bottom: calc(120rpx + constant(safe-area-inset-bottom));
  /* 兼容 iOS < 11.2 */
  padding-bottom: calc(120rpx + env(safe-area-inset-bottom));
  /* 兼容 iOS >= 11.2 */
}

/* 自提模式下不需要底部间距 */
.container.pickup-mode {
  padding-bottom: constant(safe-area-inset-bottom);
  /* 兼容 iOS < 11.2 */
  padding-bottom: env(safe-area-inset-bottom);
  /* 兼容 iOS >= 11.2 */
}

.container {
  min-height: 100vh;
  position: relative;
}



.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 40rpx;
}

.empty-icon {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: 30rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}

/* 共用部分 */
.section-title {
  font-family: "Source Han Sans CN";
  font-weight: 500;
  font-size: 32rpx;
}

.section-divider {
  height: 4rpx;
  background-color: #F9F9F9;
  width: 100%;
}

/* 礼品订单提示 */
.gift-order-section {
  background: #fff;
  margin-bottom: 10rpx;
  padding: 20rpx 0;
}

.gift-order-tip {
  display: flex;
  align-items: center;
  padding: 20rpx 30rpx;
  background-color: #A9FF001A;
  margin: 0 30rpx;
  border-radius: 8rpx;
}

.gift-tip-text {
  font-size: 24rpx;
  color: #647D00;
}

/* 配送方式 */
.delivery-section {
  margin: 30rpx 30rpx;
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  position: relative;
  z-index: 10;
}

/* 自提模式下配送方式区域浮动样式 */
.delivery-section.floating {
  position: fixed;
  top: calc(var(--status-bar-height) + 20rpx);
  left: 30rpx;
  right: 30rpx;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10rpx);
  box-shadow: 0px 2px 8px rgba(0, 0, 0, 0.1);
  z-index: 100;
  margin: 0;
}

.delivery-options {
  display: flex;
  gap: 20rpx;
  border: 2rpx solid #647D00;
  border-radius: 31rpx;
  padding: 4rpx;
  background: #fff;
}

.delivery-option {
  flex: 1;
  height: 42rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 21rpx;
  transition: all 0.3s;
  background: transparent;

  &.active {
    background: linear-gradient(90deg, #A9FF00 0%, #91F104 100%);

    .delivery-text {
      color: #282921;
    }
  }

  &:not(.active) .delivery-text {
    color: #282921;
  }
}

.free-shipping-tag {
  position: absolute;
  top: 10rpx;
  right: 20rpx;
  width: 95.72rpx; /* 47.86px * 2 */
  height: 34rpx; /* 17px * 2 */
  background: #FF4A4A;
  border-radius: 20rpx 20rpx 20rpx 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.free-shipping-text {
  font-family: "PingFang SC";
  font-weight: 400;
  font-size: 20rpx;
  color: #fff;
  line-height: 1;
}

.delivery-icon {
  display: flex;
  margin-right: 10rpx;
}

.icon-img {
  width: 40rpx;
  height: 40rpx;
}

.delivery-text {
  font-family: "PingFang SC";
  font-weight: 500;
  font-size: 28rpx;
}

/* 配送信息 */
.address-section {
  background: #fff;
  margin: 0 30rpx 30rpx;
  border-radius: 16rpx;
}

.address-content {
  display: flex;
  padding: 30rpx;
  align-items: center;
}

.address-icon {
  margin-right: 20rpx;
}

.address-icon-img {
  width: 40rpx;
  height: 40rpx;
}

.store-content {
  display: flex;
  padding: 30rpx;
  align-items: center;
  background: #fff;
}

.address-left {
  flex: 1;
}

.top {
  display: flex;
  margin-bottom: 10rpx;
}

.name {
  font-weight: 400;
  font-size: 28rpx;
  margin-right: 20rpx;
}

.mobile {
  font-size: 28rpx;
}

.address {
  font-size: 24rpx;
  color: #999;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 100%;
}

.arrow-right {
  color: #999;
  font-size: 24rpx;
}

.arrow-right-img {
  width: 24rpx;
  height: 24rpx;
}

.empty-text {
  font-size: 28rpx;
  display: block;
  margin-bottom: 10rpx;
}

/* 商品信息 */
.goods-section {
  background: #fff;
  margin: 0 30rpx 30rpx;
  border-radius: 16rpx;
}

.g-item {
  display: flex;
  padding: 20rpx 30rpx;
  align-items: center;
}

.goods-img-container {
  width: 140rpx;
  height: 140rpx;
  border-radius: 8rpx;
  overflow: hidden;
  background: linear-gradient(180deg, #89C4D0 0%, #E2F1F5 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.goods-img {
  width: 100%;
  height: 100%;
}

.goods-info {
  flex: 1;
  margin-left: 20rpx;
  display: flex;
  flex-direction: column;
}

.goods-name-row {
  margin-bottom: 10rpx;
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.goods-name {
  font-family: "Source Han Sans CN";
  font-weight: 500;
  font-size: 28rpx;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 20rpx;
}

.goods-attrs-row {
  margin-bottom: 10rpx;
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.goods-attrs {
  font-family: "Source Han Sans CN";
  font-weight: 400;
  font-size: 28rpx;
  color: #999;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 20rpx;
}

.goods-price {
  font-family: "Source Han Sans CN";
  font-weight: 350;
  font-size: 26rpx;
  white-space: nowrap;
  flex-shrink: 0;
}

.goods-quantity {
  font-weight: 400;
  font-size: 24rpx;
  white-space: nowrap;
  flex-shrink: 0;
}

/* 优惠券 */
.coupon-section {
  background: #fff;
  margin: 0 30rpx 30rpx;
  border-radius: 16rpx;
}

.coupon-header {
  display: flex;
  padding: 30rpx 30rpx;
  justify-content: space-between;
  align-items: center;
}

.coupon-left {
  display: flex;
  align-items: center;
}

.coupon-icon {
  margin-right: 20rpx;
  height: 40rpx;
  display: flex;
  align-items: center;
}

.coupon-title {
  font-family: "Source Han Sans SC";
  font-weight: 500;
  font-size: 32rpx;
  line-height: 32rpx;
  height: 40rpx;
  display: flex;
  align-items: center;
}

.coupon-icon-img {
  width: 40rpx;
  height: 40rpx;
}

.coupon-right {
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.selected-coupon-text {
  color: #647D00;
  font-size: 24rpx;
  text-align: right;
  margin-right: 10rpx;
  font-weight: 500;
  line-height: 24rpx;
  height: 40rpx;
  display: flex;
  align-items: center;
}

.coupon-count-text {
  color: #647D00;
  font-size: 24rpx;
  text-align: right;
  margin-right: 10rpx;
  line-height: 24rpx;
  height: 40rpx;
  display: flex;
  align-items: center;
}

.no-coupon-text {
  color: #999;
  font-size: 24rpx;
  text-align: right;
  margin-right: 10rpx;
  line-height: 24rpx;
  height: 40rpx;
  display: flex;
  align-items: center;
}

.coupon-arrow {
  width: 24rpx;
  height: 24rpx;
}

/* 积分 */
.integration-section {
  background: #fff;
  margin-bottom: 10rpx;
}

.integration-header {
  display: flex;
  padding: 20rpx 30rpx;
  justify-content: space-between;
  align-items: center;
}

.integration-left {
  display: flex;
  align-items: center;
}

.integration-icon {
  width: 40rpx;
  height: 40rpx;
  background: linear-gradient(135deg, #FF3D4D 0%, #FD935A 100%);
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20rpx;
}

.integral-img {
  width: 24rpx;
  height: 24rpx;
}

.integration-title {
  font-family: "Source Han Sans SC";
  font-weight: 500;
  font-size: 32rpx;
  margin-left: 20rpx;
}

.integration-right {
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.integration-available {
  color: #647D00;
  font-family: "Source Han Sans SC";
  font-weight: 500;
  font-size: 28rpx;
  text-align: right;
  margin-right: 10rpx;
}

.integration-input {
  width: 200rpx;
  text-align: right;
  font-size: 28rpx;
}

.placeholder {
  color: #999;
  font-size: 24rpx;
}

/* 订单金额明细 */
.order-summary {
  background: #fff;
  padding: 30rpx;
  margin: 0 30rpx 30rpx;
  border-radius: 16rpx;

  .section-title {
    padding: 0 0 20rpx 0;
  }
}

.summary-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20rpx;
  height: 60rpx;
  line-height: 60rpx;
}

.summary-label {
  font-family: "Source Han Sans SC";
  font-weight: 500;
  color: #282921;
  font-size: 24rpx;
}

.summary-value {
  font-family: "Source Han Sans SC";
  font-weight: 400;
  font-size: 24rpx;
}

.discount {
  color: #647D00;
}

.total {
  margin-top: 10rpx;
  
  .summary-label {
    font-family: "Source Han Sans SC";
    font-weight: 500;
    font-size: 32rpx;
  }
}

.total-price {
  font-family: "Source Han Sans SC";
  font-weight: 500;
  font-size: 28rpx;
}

.total-divider {
  height: 1px;
  background-color: #eee;
  margin: 20rpx 0;
}

/* 备注 */
.remark-section {
  margin-top: 30rpx;
}

.remark-title {
  font-family: "Source Han Sans SC";
  font-weight: 500;
  font-size: 32rpx;
  margin-bottom: 30rpx;
}

.remark-input-wrapper {
  background-color: #F9F9F9;
  border-radius: 8rpx;
  padding: 20rpx;
  height: 80rpx;
  margin-top: 20rpx;
}

.remark-input {
  width: 100%;
  font-size: 24rpx;
  height: 100%;
}

/* 付款方式选择 */
.payment-method-section {
  background: #fff;
  margin: 0 30rpx 30rpx;
  border-radius: 16rpx;
}

.payment-options {
  padding: 30rpx;
}

.payment-option {
  display: flex;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f5f5f5;

  &:last-child {
    border-bottom: none;
  }

  &.disabled {
    opacity: 0.5;

    .payment-text,
    .balance-amount {
      color: #ccc;
    }
  }
}

.payment-icon {
  margin-right: 20rpx;
}

.payment-icon-img {
  width: 40rpx;
  height: 40rpx;
}

.payment-text {
  flex: 1;
  font-family: "PingFang SC";
  font-weight: 400;
  font-size: 28rpx;
  color: #333;
}

.payment-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.balance-amount {
  font-family: "PingFang SC";
  font-weight: 400;
  font-size: 24rpx;
  color: #999;
  margin-top: 4rpx;
}

.payment-radio {
  width: 40rpx;
  height: 40rpx;
  border: 2rpx solid #ddd;
  border-radius: 50%;
  position: relative;

  &.active {
    border-color: #A9FF00;

    &::after {
      content: '';
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      width: 20rpx;
      height: 20rpx;
      background: #A9FF00;
      border-radius: 50%;
    }
  }
}

/* 底部支付栏 */
.footer {
  position: fixed;
  left: 0;
  bottom: 0;
  width: 100%;
  height: 150rpx;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  border-top: 1rpx solid rgba(0, 0, 0, 0.2);
  z-index: 998;
  padding: 20rpx 30rpx 0 30rpx;
  padding-bottom: constant(safe-area-inset-bottom);
  /* 兼容 iOS < 11.2 */
  padding-bottom: env(safe-area-inset-bottom);
  /* 兼容 iOS >= 11.2 */
}

.order-info {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.order-count {
  font-family: "PingFang SC";
  font-weight: 400;
  font-size: 24rpx;
  color: #999;
}

.order-total-label {
  font-family: "PingFang SC";
  font-weight: 400;
  font-size: 28rpx;
  color: #333;
}

.price-payment {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.order-total-price {
  font-family: "PingFang SC";
  font-weight: 500;
  font-size: 32rpx;
  color: #333;
}

.pay-btn {
  width: 252rpx;
  height: 84rpx;
  background: #20201E;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: "PingFang SC";
  font-weight: 400;
  font-size: 32rpx;
  color: #A9FF00;
  
  &.disabled {
    background: #ccc;
    color: #999;
  }
}

/* 自提模式样式 */
.pickup-section {
  display: flex;
  flex-direction: column;
  min-height: calc(100vh - 140rpx);
  margin-top: 140rpx; /* 为浮动的配送方式区域留出空间 */
  background: #F8F8F8;
}

.map-container {
  height: 500rpx;
  width: 100%;
  position: relative;
  flex-shrink: 0;
  background: #fff;
}

.store-cards-container {
  flex: 1;
  background: #F8F8F8;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  min-height: 400rpx;
}

.store-cards {
  flex: 1;
  overflow-y: auto;
  padding: 20rpx 0;

  /* 确保滚动区域有足够的高度 */
  max-height: calc(100vh - 640rpx);
}

/* 自提模式下的空状态 */
.pickup-section .empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 40rpx;
  height: 100%;
  min-height: 400rpx;
}

.pickup-section .empty-icon {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: 30rpx;
}

.pickup-section .empty-text {
  font-size: 28rpx;
  color: #999;
}

/* 通用空状态样式 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 40rpx;
}

.empty-icon {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: 30rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}

/* 门店弹窗面板 */
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

  &.none {
    display: none;
  }

  &.show {
    background: rgba(0, 0, 0, 0.4);

  }
}






</style> 