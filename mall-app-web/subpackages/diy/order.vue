<template>
  <view class="container">
    <!-- 导航栏 -->
    <nav-bar
      :back="true"
      :placeholder="true"
      bg-color="#FFFFFF"
      title="确认订单"
      color="#000000"
    />

    <!-- 顶部预览图 -->
    <view class="preview-section">
      <swiper
        class="preview-swiper"
        :indicator-dots="false"
        :autoplay="false"
        :circular="false"
        :duration="300"
        @change="onSwiperChange"
        :current="currentPreviewIndex"
      >
        <swiper-item
          v-for="(face, index) in designFaces"
          :key="index"
          class="swiper-item"
        >
          <image
            class="preview-image"
            :src="face.finalImage || face.previewImage || productInfo.pic"
            mode="aspectFit"
            @tap="previewImage(face.finalImage || face.previewImage || productInfo.pic)"
          />
        </swiper-item>
      </swiper>

      <!-- 轮播指示器 -->
      <view class="preview-indicators" v-if="designFaces.length > 1">
        <view
          v-for="(face, index) in designFaces"
          :key="index"
          :class="['indicator-dot', index === currentPreviewIndex ? 'active' : '']"
          @tap="switchPreview(index)"
        ></view>
      </view>
    </view>

    <!-- 地址信息区域 -->
    <view class="address-section">
      <view class="section-header">
        <view class="section-title">配送信息</view>
        <navigator
          v-if="selectedAddress && selectedAddress.name"
          class="wechat-address-wrapper"
          url="/pages/address/address?source=1"
        >
          <text class="wechat-address">编辑地址</text>
        </navigator>
        <view
          v-else
          class="wechat-address-wrapper"
          @tap="getWechatAddress"
        >
          <text class="wechat-address">微信授权地址</text>
        </view>
      </view>

      <!-- 配送地址 -->
      <navigator
        v-if="selectedAddress && selectedAddress.name"
        class="address-content"
        url="/pages/address/address?source=1"
      >
        <view class="address-left">
          <view class="top">
            <text class="name">{{ selectedAddress.name }}</text>
            <text class="mobile">{{ selectedAddress.phone || selectedAddress.phoneNumber }}</text>
          </view>
          <text class="address">{{ getFullAddress(selectedAddress) }}</text>
        </view>
        <text class="arrow-right">></text>
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
        <text class="arrow-right">></text>
      </navigator>

      <view class="section-divider"></view>
    </view>

    <!-- 商品信息区域 -->
    <view class="goods-section">
      <view class="section-title">商品信息</view>
      <view class="g-item">
        <view class="goods-img-container">
          <image
            class="goods-img"
            :src="productInfo.pic"
            mode="aspectFill"
          ></image>
        </view>
        <view class="goods-info">
          <view class="goods-name-row">
            <text class="goods-name">{{ productInfo.name }}</text>
            <text class="goods-price">￥{{ calculatedPrice }}</text>
          </view>
          <view class="goods-attrs-row">
            <text class="goods-attrs">个性化定制商品</text>
            <text class="goods-quantity">x{{ quantity }}</text>
          </view>
        </view>
      </view>
      <view class="section-divider"></view>
    </view>

    <!-- 优惠券区域 -->
    <view class="coupon-section">
      <view class="coupon-header" @tap="selectCoupon">
        <view class="coupon-left">
          <view class="coupon-icon">
            <image class="coupon-icon-img" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/order/coupon.png"></image>
          </view>
          <view class="coupon-title">优惠券</view>
        </view>
        <view class="coupon-right">
          <view v-if="selectedCoupon">
            <text class="selected-coupon-text">已选择: -￥{{ calculateCouponDiscount(selectedCoupon, calculatedPrice * quantity).toFixed(2) }}</text>
          </view>
          <view v-else-if="availableCoupons && availableCoupons.length > 0">
            <text class="coupon-count-text">{{ availableCoupons.length }}张优惠券可用</text>
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
      <view class="section-divider"></view>
    </view>

    <!-- 订单摘要区域 -->
    <view class="order-summary">
      <view class="price-detail-title">价格明细</view>
      <view class="summary-item">
        <text class="summary-label">产品原价</text>
        <text class="summary-value">￥{{ (calculatedPrice * quantity).toFixed(2) }}</text>
      </view>
      <view class="summary-item">
        <text class="summary-label">活动优惠</text>
        <text class="summary-value discount">-￥0</text>
      </view>
      <view class="summary-item">
        <text class="summary-label">运费</text>
        <text class="summary-value">{{ getDeliveryPrice() }}</text>
      </view>
      <view class="summary-item">
        <text class="summary-label">优惠券</text>
        <text v-if="selectedCoupon" class="summary-value discount">-￥{{ selectedCoupon.amount }}</text>
        <text v-else class="summary-value discount">-￥0</text>
      </view>
      <view class="summary-item total">
        <text class="summary-label">合计</text>
        <text class="summary-value total-price">￥{{ getTotalPrice() }}</text>
      </view>

      <view class="remark-section">
        <text class="remark-title">备注</text>
        <view class="remark-input-wrapper">
          <input
            class="remark-input"
            type="text"
            placeholder="请填写备注信息"
            placeholder-class="placeholder"
            v-model="orderRemark"
          />
        </view>
      </view>

      <!-- 支付方式选择 -->
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
              'disabled': userBalance < getTotalPrice()
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
              <text class="balance-amount">余额：￥{{ userBalance.toFixed(2) }}</text>
            </view>
            <view class="payment-radio" :class="{ 'active': selectedPaymentMethod === 'balance' }"></view>
          </view>
        </view>
      </view>
      <view class="section-divider"></view>
    </view>

    <!-- 底部支付区域 -->
    <view class="footer">
      <view class="order-info">
        <text class="order-count">共{{ quantity }}件</text>
        <text class="order-total-label">合计</text>
      </view>
      <view class="price-payment">
        <text class="order-total-price">￥{{ getTotalPrice() }}</text>
        <view
          class="pay-btn"
          :class="{ 'disabled': !canSubmit || isSubmitting }"
          @tap="submitOrder"
        >{{ isSubmitting ? '处理中...' : '立即支付' }}</view>
      </view>
    </view>

    <!-- 优惠券弹窗组件 -->
    <coupon-select-popup
      :visible="showCouponPopup"
      :couponList="availableCoupons"
      :selectedCoupon="selectedCoupon"
      @close="closeCouponPopup"
      @select="selectCouponFromPopup"
      @confirm="confirmCouponSelection"
    />

  </view>
</template>

<script>
import NavBar from '@/components/nav-bar.vue';
import CouponSelectPopup from '@/components/CouponSelectPopup.vue';
import { createDIYOrder, calculateDIYPrice } from '@/api/diy.js';
import { fetchAddressList, addAddress } from '@/api/address.js';
import { fetchMemberCouponList } from '@/api/coupon.js';
import { getWechatPayParams } from '@/api/order.js';
import { getWalletInfo } from '@/api/wallet.js';

export default {
  name: 'DIYOrder',
  components: {
    'nav-bar': NavBar,
    'coupon-select-popup': CouponSelectPopup
  },
  data() {
    return {
      // 基础数据
      orderData: null,
      productInfo: {
        id: '',
        name: '个性化定制商品',
        pic: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/default-product.png',
        price: 0
      },
      designFaces: [],
      calculatedPrice: 0,
      productId: '',
      designData: {},
      designId: '',
      templateId: '',

      // 预览相关
      currentPreviewIndex: 0,
      
      // 订单信息
      quantity: 1,
      orderRemark: '',

      // 积分相关
      useIntegration: 0, // 使用的积分数量
      integrationAmount: 0, // 积分抵扣金额

      // 订单和支付相关
      currentOrderInfo: null, // 当前订单信息
      isSubmitting: false, // 是否正在提交订单
      isPaymentLoading: false, // 是否正在支付
      
      // 地址相关
      selectedAddress: null,
      addressList: [],
      
      // 优惠券相关
      selectedCoupon: null,
      availableCoupons: [],
      showCouponPopup: false,
      
      // 配送方式
      deliveryOptions: [
        { id: 1, name: '标准快递', description: '3-5个工作日', price: 0 },
        { id: 2, name: '顺丰快递', description: '1-2个工作日', price: 15 },
        { id: 3, name: '同城配送', description: '当日达', price: 20 }
      ],
      selectedDeliveryId: 1,
      
      // 预览相关
      showPreviewModal: false,
      previewFaceData: {},

      // 学校信息
      schoolInfo: null,

      // 余额支付相关
      userBalance: 0,
      selectedPaymentMethod: 'wechat', // 'wechat' 或 'balance'
      showPaymentMethodSelector: false
    };
  },
  computed: {
    canSubmit() {
      return this.selectedAddress && !this.isSubmitting;
    }
  },
  onLoad(options) {
    try {
      console.log('页面参数:', options);

      if (options.orderData) {
        // 先打印原始数据进行调试
        console.log('原始orderData:', options.orderData);

        // 尝试多次解码，处理可能的双重编码问题
        let decodedData = options.orderData;
        try {
          // 第一次解码
          decodedData = decodeURIComponent(decodedData);
          console.log('第一次解码后:', decodedData);

          // 检查是否还需要再次解码
          if (decodedData.includes('%')) {
            decodedData = decodeURIComponent(decodedData);
            console.log('第二次解码后:', decodedData);
          }
        } catch (decodeError) {
          console.error('解码失败:', decodeError);
          throw new Error('数据解码失败');
        }

        // 解析JSON
        this.orderData = JSON.parse(decodedData);
        console.log('解析后的订单数据:', this.orderData);

        this.initOrderData();
      } else {
        console.error('缺少orderData参数，使用默认数据');

        // 提供默认的测试数据
        this.orderData = {
          productId: 'test-product-001',
          designData: {
            productName: '个性化定制商品'
          },
          calculatedPrice: 150,
          designFaces: [
            {
              name: '正面',
              previewImage: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/default-product.png',
              finalImage: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/default-product.png'
            }
          ]
        };

        uni.showToast({
          title: '使用默认数据进行测试',
          icon: 'none'
        });

        this.initOrderData();
      }
    } catch (error) {
      console.error('页面初始化失败:', error);
      console.error('错误详情:', error.message);

      uni.showToast({
        title: '页面初始化失败: ' + error.message,
        icon: 'none',
        duration: 3000
      });

      setTimeout(() => {
        uni.navigateBack();
      }, 3000);
    }

    // 获取学校信息
    this.loadSchoolInfo(options);
  },
  methods: {
    // 获取主预览图
    getMainPreviewImage() {
      if (this.designFaces && this.designFaces.length > 0) {
        return this.designFaces[0].finalImage || this.designFaces[0].previewImage || this.productInfo.pic;
      }
      return this.productInfo.pic || 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/default-product.png';
    },

    // Swiper切换事件
    onSwiperChange(e) {
      this.currentPreviewIndex = e.detail.current;
    },

    // 切换预览图
    switchPreview(index) {
      this.currentPreviewIndex = index;
    },

    // 预览大图
    previewImage(imageUrl) {
      if (!imageUrl) return;

      // 收集所有预览图片
      const imageUrls = this.designFaces.map(face =>
        face.finalImage || face.previewImage || this.productInfo.pic
      ).filter(url => url);

      uni.previewImage({
        current: imageUrl,
        urls: imageUrls
      });
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
              phone: res.telNumber,
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
              this.selectedAddress = {
                ...addressData,
                id: saveResult.data // 后端返回的地址ID
              };

              // 重新加载地址列表
              await this.loadAddressList();

              uni.showToast({
                title: '地址保存成功',
                icon: 'success'
              });

              console.log('地址保存成功:', this.selectedAddress);
            } else {
              // 保存失败，但仍然可以临时使用
              this.selectedAddress = addressData;

              uni.showToast({
                title: '地址保存失败，但可临时使用',
                icon: 'none',
                duration: 2000
              });

              console.error('地址保存失败:', saveResult);
            }

          } catch (error) {
            uni.hideLoading();
            console.error('保存地址时发生错误:', error);

            // 即使保存失败，也设置为临时地址
            this.selectedAddress = {
              name: res.userName,
              phoneNumber: res.telNumber,
              phone: res.telNumber,
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

    // 初始化订单数据
    async initOrderData() {
      try {
        console.log('初始化订单数据:', this.orderData);

        // 处理从preview页面传递过来的数据结构
        if (this.orderData.productId) {
          // 如果有productId，说明是从preview页面跳转过来的
          this.productId = this.orderData.productId;
          this.designData = this.orderData.designData || {};
          this.designFaces = this.orderData.designFaces || [];
          this.calculatedPrice = this.orderData.calculatedPrice || 0;

          // 保存设计相关ID
          this.designId = this.orderData.designId;
          this.templateId = this.orderData.templateId;

          // 构造产品信息
          this.productInfo = {
            id: this.productId,
            name: this.designData.productName || '个性化定制商品',
            pic: this.designFaces.length > 0 ? this.designFaces[0].finalImage || this.designFaces[0].previewImage : '',
            price: this.calculatedPrice
          };
        } else {
          // 兼容其他数据结构
          this.productInfo = this.orderData.productInfo || {};
          this.designFaces = this.orderData.designFaces || [];
          this.calculatedPrice = this.orderData.calculatedPrice || 0;
        }

        // 加载地址列表
        await this.loadAddressList();

        // 加载用户余额
        await this.loadUserBalance();

        // 加载优惠券
        await this.loadCoupons();

        // 自动选择最优惠券
        this.autoSelectBestCoupon();

      } catch (error) {
        console.error('初始化订单数据失败:', error);
      }
    },
    
    // 加载地址列表
    async loadAddressList() {
      try {
        const result = await fetchAddressList();
        if (result && result.code === 200) {
          this.addressList = result.data || [];
          // 选择默认地址
          const defaultAddress = this.addressList.find(addr => addr.defaultStatus === 1);
          if (defaultAddress) {
            this.selectedAddress = defaultAddress;
          } else if (this.addressList.length > 0) {
            this.selectedAddress = this.addressList[0];
          }
        }
      } catch (error) {
        console.error('加载地址列表失败:', error);
      }
    },
    
    // 加载优惠券
    async loadCoupons() {
      try {
        console.log('开始加载优惠券...');

        // 获取会员已领取的优惠券（未使用的）
        const result = await fetchMemberCouponList(0); // 0表示未使用
        if (result && result.code === 200) {
          // 处理优惠券数据结构，参考createOrder页面
          this.availableCoupons = [];
          if (result.data && Array.isArray(result.data)) {
            for (let item of result.data) {
              // 如果返回的是couponHistory对象，取其中的coupon
              if (item.coupon) {
                this.availableCoupons.push(item.coupon);
              } else {
                // 如果直接是coupon对象
                this.availableCoupons.push(item);
              }
            }
          }
          console.log('加载优惠券成功:', this.availableCoupons);
        } else {
          console.log('优惠券接口返回失败:', result);
          this.availableCoupons = [];
        }
      } catch (error) {
        console.error('加载优惠券失败:', error);
        this.availableCoupons = [];

        // 不显示错误提示，静默处理
        // uni.showToast({
        //   title: '加载优惠券失败',
        //   icon: 'none'
        // });
      }
    },

    // 加载用户余额 - 参考个人中心页面，直接调用钱包接口
    async loadUserBalance() {
      try {
        const result = await getWalletInfo();
        if (result && result.code === 200) {
          this.userBalance = result.data.balance || 0;
          console.log('用户余额:', this.userBalance);
        } else {
          console.log('获取余额失败:', result);
          this.userBalance = 0;
        }
      } catch (error) {
        console.error('加载用户余额失败:', error);
        this.userBalance = 0;
      }
    },

    // 获取完整地址
    getFullAddress(address) {
      return `${address.province}${address.city}${address.region}${address.detailAddress}`;
    },
    
    // 获取总元素数
    getTotalElements() {
      return this.designFaces.reduce((total, face) => {
        return total + (face.elements ? face.elements.length : 0);
      }, 0);
    },
    
    // 减少数量
    decreaseQuantity() {
      if (this.quantity > 1) {
        this.quantity--;
      }
    },
    
    // 增加数量
    increaseQuantity() {
      if (this.quantity < 99) {
        this.quantity++;
      }
    },
    
    // 预览面
    previewFace(face) {
      this.previewFaceData = face;
      this.showPreviewModal = true;
    },
    
    // 关闭预览
    closePreview() {
      this.showPreviewModal = false;
      this.previewFaceData = {};
    },
    
    // 选择地址
    selectAddress() {
      uni.navigateTo({
        url: '/pages/address/address?from=order'
      });
    },
    
    // 选择优惠券
    selectCoupon() {
      if (this.availableCoupons.length === 0) {
        uni.showToast({
          title: '暂无可用优惠券',
          icon: 'none'
        });
        return;
      }

      // 打开优惠券选择弹窗
      this.showCouponPopup = true;
    },

    // 关闭优惠券弹窗
    closeCouponPopup() {
      this.showCouponPopup = false;
    },

    // 选择优惠券（弹窗回调）
    selectCouponFromPopup(coupon) {
      // 检查优惠券是否满足使用条件
      const orderAmount = this.calculatedPrice * this.quantity;
      if (coupon.minPoint > orderAmount) {
        uni.showToast({
          title: `订单金额需满${coupon.minPoint}元才可使用此优惠券`,
          icon: 'none',
          duration: 2000
        });
        return;
      }

      this.selectedCoupon = coupon;
    },

    // 确认选择优惠券
    confirmCouponSelection(coupon) {
      this.selectedCoupon = coupon;
      this.showCouponPopup = false;
    },

    // 自动选择最优惠券 - 参考createOrder页面
    autoSelectBestCoupon() {
      if (!this.availableCoupons || this.availableCoupons.length === 0) {
        this.selectedCoupon = null;
        return;
      }

      // 计算当前订单金额
      const orderAmount = this.calculatedPrice * this.quantity;

      // 筛选出满足条件的优惠券（订单金额 >= 优惠券最低使用要求）
      const availableCoupons = this.availableCoupons.filter(coupon => {
        return coupon.minPoint <= orderAmount;
      });

      if (availableCoupons.length === 0) {
        // 没有满足条件的优惠券
        if (this.selectedCoupon) {
          console.log('当前优惠券不再满足条件，清除选择');
          this.selectedCoupon = null;
        }
        return;
      }

      // 从满足条件的优惠券中选择优惠金额最大的
      const bestCoupon = availableCoupons.reduce((prev, current) => {
        const prevDiscount = this.calculateCouponDiscount(prev, orderAmount);
        const currentDiscount = this.calculateCouponDiscount(current, orderAmount);
        return (currentDiscount > prevDiscount) ? current : prev;
      });

      // 检查当前优惠券是否还满足条件
      if (this.selectedCoupon && !availableCoupons.find(c => c.id === this.selectedCoupon.id)) {
        // 当前优惠券不再满足条件，清除并选择新的
        console.log('当前优惠券不再满足条件，重新选择');
        this.selectedCoupon = bestCoupon;
        console.log('自动选择优惠券:', bestCoupon);
      } else if (!this.selectedCoupon) {
        // 没有选中优惠券，选择最优的
        this.selectedCoupon = bestCoupon;
        console.log('自动选择优惠券:', bestCoupon);
      } else {
        // 比较当前优惠券和最优优惠券的优惠金额
        const currentDiscount = this.calculateCouponDiscount(this.selectedCoupon, orderAmount);
        const bestDiscount = this.calculateCouponDiscount(bestCoupon, orderAmount);
        if (bestDiscount > currentDiscount) {
          this.selectedCoupon = bestCoupon;
          console.log('找到更优惠券，自动切换:', bestCoupon);
        } else {
          console.log('当前优惠券已是最优，无需更换');
        }
      }
    },

    // 选择支付方式 - 参考createOrder页面
    selectPaymentMethod(method) {
      // 如果选择余额支付，检查余额是否充足
      if (method === 'balance') {
        const payAmount = parseFloat(this.getTotalPrice() || 0);
        const balance = parseFloat(this.userBalance || 0);

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
    
    // 选择配送方式
    selectDelivery(option) {
      this.selectedDeliveryId = option.id;
    },
    
    // 获取配送价格
    getDeliveryPrice() {
      const delivery = this.deliveryOptions.find(d => d.id === this.selectedDeliveryId);
      return delivery && delivery.price > 0 ? `¥${delivery.price}` : '免费';
    },
    
    // 获取总价格
    getTotalPrice() {
      // calculatedPrice已经在preview页面通过后端API计算过准确的DIY价格，这里直接使用
      let payAmount = this.calculatedPrice * this.quantity;

      // 加上运费
      const delivery = this.deliveryOptions.find(d => d.id === this.selectedDeliveryId);
      if (delivery && delivery.price > 0) {
        payAmount += delivery.price;
      }

      // 减去优惠券金额（使用新的计算方法）
      if (this.selectedCoupon) {
        const couponDiscount = this.calculateCouponDiscount(this.selectedCoupon, payAmount);
        payAmount -= couponDiscount;
      }

      // 减去积分抵扣金额（如果有的话）
      if (this.useIntegration) {
        const integrationAmount = this.calcIntegrationAmount(parseInt(this.useIntegration));
        payAmount -= integrationAmount;
      }

      // 保证金额不小于0
      if (payAmount < 0) {
        payAmount = 0;
      }

      return parseFloat(payAmount.toFixed(2));
    },

    // 计算优惠券折扣金额
    calculateCouponDiscount(coupon, orderAmount) {
      if (!coupon) return 0;

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

    // 计算积分抵扣金额 - 参考createOrder页面
    calcIntegrationAmount(integration) {
      if (!integration || integration <= 0) return 0;

      // 1积分 = 0.01元
      const amount = integration * 0.01;

      // 积分抵扣不能超过商品总金额的一定比例（假设最多50%）
      // calculatedPrice已经是后端计算的准确DIY价格
      const maxAmount = (this.calculatedPrice * this.quantity) * 0.5;

      return parseFloat(Math.min(amount, maxAmount).toFixed(2));
    },

    // 计算实时价格
    async calculateRealTimePrice() {
      try {
        // 检查必需参数
        if (!this.selectedAddress || !this.selectedAddress.id) {
          console.log('地址未选择，无法计算价格');
          return;
        }

        if (!this.designId && !this.designData?.designId && !this.designData?.id) {
          console.log('设计ID未设置，无法计算价格');
          return;
        }

        const orderData = {
          productId: parseInt(this.productInfo.id),
          designId: parseInt(this.designId) || parseInt(this.designData?.designId) || parseInt(this.designData?.id) || null,
          quantity: this.quantity,
          memberReceiveAddressId: parseInt(this.selectedAddress.id),
          deliveryType: this.selectedDeliveryId,
          couponId: this.selectedCoupon ? this.selectedCoupon.id : null,
          useIntegration: 0, // 暂时不使用积分
          schoolId: this.schoolInfo ? parseInt(this.schoolInfo.id) : null
        };

        const result = await calculateDIYPrice(orderData);
        if (result && result.code === 200) {
          this.calculatedPrice = result.data;
        }
      } catch (error) {
        console.error('计算价格失败:', error);
      }
    },
    
    // 提交订单
    async submitOrder() {
      if (!this.canSubmit) return;
      
      try {
        this.isSubmitting = true;
        
        // 构建订单数据
        const orderData = {
          productId: parseInt(this.productInfo.id), // 转换为Long类型
          designId: parseInt(this.designId) || parseInt(this.designData.designId) || parseInt(this.designData.id) || null, // 转换为Long类型
          quantity: this.quantity,
          memberReceiveAddressId: parseInt(this.selectedAddress.id), // 转换为Long类型
          couponId: this.selectedCoupon ? parseInt(this.selectedCoupon.id) : null, // 转换为Long类型
          useIntegration: 0, // 暂时不使用积分
          payType: this.selectedPaymentMethod === 'balance' ? 3 : 2, // 3-余额支付，2-微信支付
          sourceType: 2, // 小程序订单
          note: this.orderRemark,
          deliveryType: this.selectedDeliveryId,
          designData: JSON.stringify(this.designData),
          customizeFaces: JSON.stringify(this.designFaces),
          totalPrice: parseFloat(this.getTotalPrice()),
          freightAmount: this.getDeliveryPrice() === '免费' ? 0 : parseFloat(this.getDeliveryPrice().replace('¥', '')),
          schoolId: this.schoolInfo ? parseInt(this.schoolInfo.id) : null // 添加学校ID
        };

        console.log('提交订单数据:', orderData);

        // 验证必填字段
        if (!orderData.designId) {
          console.error('DIY设计ID不能为空');
          uni.showToast({
            title: 'DIY设计ID不能为空，请重新进入',
            icon: 'none'
          });
          return;
        }
        
        uni.showLoading({ title: '提交订单中...' });
        
        const result = await createDIYOrder(orderData);
        
        if (result && result.code === 200) {
          uni.hideLoading();

          // 保存订单信息 - 参考createOrder页面
          const orderData = result.data.order || result.data;
          this.currentOrderInfo = {
            id: orderData.id || orderData.orderId,
            orderSn: orderData.orderSn,
            payAmount: this.getTotalPrice()
          };

          console.log('DIY订单创建成功:', this.currentOrderInfo);

          // 根据支付方式处理
          if (this.selectedPaymentMethod === 'balance') {
            // 余额支付：检查订单状态，如果已支付则直接跳转成功页面
            // 订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
            if (orderData.status === 1) {
              // 余额支付成功，订单状态为待发货，直接跳转到支付成功页面
              this.balancePaySuccess();
            } else {
              // 余额支付失败，订单状态不是待发货
              uni.showToast({
                title: '余额支付失败，请重试',
                icon: 'none'
              });
              // 确保按钮状态重置
              this.isSubmitting = false;
            }
          } else {
            // 微信支付流程
            this.handlePay();
          }
        } else {
          throw new Error(result.message || '订单提交失败');
        }
        
      } catch (error) {
        console.error('提交订单失败:', error);
        uni.hideLoading();
        
        uni.showToast({
          title: error.message || '订单提交失败',
          icon: 'none'
        });
      } finally {
        this.isSubmitting = false;
      }
    },

    // 请求订阅支付结果通知 - 参考createOrder页面
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

    // 处理支付 - 参考createOrder页面
    handlePay() {
      if (this.isPaymentLoading) return;
      this.isPaymentLoading = true;

      uni.showLoading({
        title: '支付处理中...'
      });

      // 确保订单信息完整
      console.log('检查订单信息:', this.currentOrderInfo);

      if (!this.currentOrderInfo || !this.currentOrderInfo.orderSn) {
        console.error('订单信息不完整:', {
          currentOrderInfo: this.currentOrderInfo,
          hasOrderSn: this.currentOrderInfo ? !!this.currentOrderInfo.orderSn : false
        });

        uni.hideLoading();
        this.isPaymentLoading = false;

        uni.showToast({
          title: '订单信息不完整，请重试',
          icon: 'none'
        });
        return;
      }

      // 微信支付
      this.wxPay();
    },

    // 微信支付 - 参考createOrder页面
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

      console.log('DIY订单支付请求参数：', payData);

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
              console.log('DIY订单支付成功', res);
              this.paySuccess();
            },
            fail: (err) => {
              console.error('DIY订单支付失败', err);

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

          // 跳转到订单列表
          setTimeout(() => {
            uni.redirectTo({
              url: '/pages/order/order?state=0'
            });
          }, 1000);
        }
      }).catch(err => {
        console.error('获取支付参数失败', err);
        uni.hideLoading();
        this.isPaymentLoading = false;

        uni.showToast({
          title: '获取支付参数失败',
          icon: 'none'
        });

        // 跳转到订单列表
        setTimeout(() => {
          uni.redirectTo({
            url: '/pages/order/order?state=0'
          });
        }, 1000);
      });
    },

    // 支付成功处理 - 参考createOrder页面
    paySuccess() {
      uni.showToast({
        title: '支付成功',
        icon: 'success'
      });

      // 跳转到支付成功页，添加DIY标识
      setTimeout(() => {
        let url = `/pages/money/paySuccess?orderId=${this.currentOrderInfo.id}&orderSn=${this.currentOrderInfo.orderSn}&amount=${this.currentOrderInfo.payAmount}&isDIY=1`;

        uni.redirectTo({
          url: url
        });
      }, 1500);
    },

    // 余额支付成功处理
    balancePaySuccess() {
      uni.showToast({
        title: '支付成功',
        icon: 'success'
      });

      // 跳转到支付成功页，添加DIY和余额支付标识
      setTimeout(() => {
        let url = `/pages/money/paySuccess?orderId=${this.currentOrderInfo.id}&orderSn=${this.currentOrderInfo.orderSn}&amount=${this.currentOrderInfo.payAmount}&isDIY=1&payType=3`;

        uni.redirectTo({
          url: url
        });
      }, 1500);
    },

    // 获取学校信息
    loadSchoolInfo(options = {}) {
    try {
      // 优先从URL参数获取学校信息
      if (options.schoolId) {
        this.schoolInfo = {
          id: parseInt(options.schoolId),
          schoolName: decodeURIComponent(options.schoolName || ''),
          name: decodeURIComponent(options.schoolName || '')
        };
        console.log('从URL参数获取到学校信息:', this.schoolInfo);
        return;
      }

      // 从本地存储获取学校信息
      const schoolInfo = uni.getStorageSync('selectedSchool');
      if (schoolInfo) {
        this.schoolInfo = JSON.parse(schoolInfo);
        console.log('从本地存储获取到学校信息:', this.schoolInfo);
      } else {
        console.log('未找到学校信息');
      }
    } catch (error) {
      console.error('获取学校信息失败:', error);
    }
  }
  },

  // 页面显示时检查地址更新
  onShow() {
    // 如果从地址页面返回，重新加载地址
    const pages = getCurrentPages();
    const currentPage = pages[pages.length - 1];
    if (currentPage.options && currentPage.options.from === 'address') {
      this.loadAddressList();
    }

    // 每次页面显示时都重新加载用户余额，确保余额信息是最新的
    this.loadUserBalance();
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
  background: #fff;
  padding-bottom: calc(120rpx + constant(safe-area-inset-bottom));
  /* 兼容 iOS < 11.2 */
  padding-bottom: calc(120rpx + env(safe-area-inset-bottom));
  /* 兼容 iOS >= 11.2 */
}

.container {
  background: #F8F8F8;
}

/* 共用部分 */
.section-title {
  font-family: "Source Han Sans CN";
  font-weight: 500;
  font-size: 32rpx;
  color: #282921;
}

.section-divider {
  height: 4rpx;
  background-color: #F9F9F9;
  width: 100%;
}

.section-header {
  padding: 20rpx 30rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.wechat-address-wrapper {
  display: flex;
  align-items: center;
}

.wechat-address {
  font-size: 24rpx;
  color: #0088FF;
  text-decoration: underline;
}

/* 预览区域样式 */
.preview-section {
  position: relative;
  height: 375px;
  background: #FFFFFF;
  margin-bottom: 10rpx;
}

.preview-swiper {
  width: 100%;
  height: 100%;
}

.swiper-item {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.preview-indicators {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
  z-index: 10;
}

.indicator-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: all 0.3s ease;
  filter: drop-shadow(1px 2px 4px rgba(0, 0, 0, 0.25));

  &.active {
    background: #A9FF00;
    transform: scale(1.2);
  }
}

/* 配送信息 */
.address-section {
  background: #fff;
  margin-bottom: 10rpx;
}

.address-content {
  display: flex;
  padding: 0 30rpx 20rpx;
  align-items: center;
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

.empty-text {
  font-size: 28rpx;
  display: block;
  margin-bottom: 10rpx;
}

/* 商品信息 */
.goods-section {
  background: #fff;
  margin-bottom: 10rpx;

  .section-title {
    padding: 20rpx 30rpx;
  }
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
  font-size: 28rpx;
  color: #333;
  flex: 1;
  margin-right: 20rpx;
}

.goods-price {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
}

.goods-attrs-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.goods-attrs {
  font-size: 24rpx;
  color: #999;
  flex: 1;
}

.goods-quantity {
  font-size: 24rpx;
  color: #999;
}

/* 优惠券 */
.coupon-section {
  background: #fff;
  margin: 30rpx 0;
}

.coupon-header {
  padding: 30rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.coupon-left {
  display: flex;
  align-items: center;
}

.coupon-icon {
  width: 40rpx;
  height: 40rpx;
  margin-right: 20rpx;
}

.coupon-icon-img {
  width: 100%;
  height: 100%;
}

.coupon-title {
  font-size: 28rpx;
  color: #333;
}

.coupon-right {
  display: flex;
  align-items: center;
}

.selected-coupon-text,
.coupon-count-text,
.no-coupon-text {
  font-size: 24rpx;
  color: #999;
  margin-right: 10rpx;
}

.selected-coupon-text {
  color: #647D00;
}

.coupon-arrow {
  width: 20rpx;
  height: 20rpx;
}

/* 订单摘要 */
.order-summary {
  background: #fff;
  margin: 30rpx 0;
  padding: 30rpx;
}

.price-detail-title {
  font-weight: 500;
  font-size: 32rpx;
  line-height: 44rpx;
  color: #282921;
  margin-bottom: 30rpx;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;

  &:last-child {
    margin-bottom: 0;
  }

  &.total {
    margin-top: 20rpx;

    .summary-label,
    .summary-value {
      font-size: 32rpx;
      font-weight: 600;
    }
  }
}

.summary-label {
  font-size: 28rpx;
  color: #333;
}

.summary-value {
  font-size: 28rpx;
  color: #333;

  &.discount {
    color: #647D00;
  }

  &.total-price {
    color: #647D00;
  }
}

.total-divider {
  height: 1rpx;
  background: #F0F0F0;
  margin: 20rpx 0;
}

.remark-section {
  margin-top: 30rpx;
}

.remark-title {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 20rpx;
}

.remark-input-wrapper {
  background: #F8F8F8;
  border-radius: 8rpx;
  padding: 20rpx;
}

.remark-input {
  width: 100%;
  font-size: 26rpx;
  color: #333;
  background: transparent;
  border: none;
  outline: none;
}

.placeholder {
  color: #999;
}

/* 支付方式选择 - 参考createOrder页面 */
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

/* 底部支付区域 */
.footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 20rpx 30rpx;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  border-top: 1rpx solid #F0F0F0;
  z-index: 100;
  padding-bottom: 80rpx;
  gap: 20rpx;
}

.order-info {
  display: flex;
  align-items: center;
  gap: 10rpx;
}

.order-count {
  font-size: 24rpx;
  color: #999;
}

.order-total-label {
  font-size: 28rpx;
  color: #333;
}

.price-payment {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.order-total-price {
  font-size: 32rpx;
  font-weight: 600;
  color: #282921;
}


.pay-btn {
  background: #20201E;
  color: #A9FF00;
  padding: 20rpx 40rpx;
  border-radius: 8rpx;
  font-size: 28rpx;
  font-weight: 500;

  &.disabled {
    opacity: 0.5;
    pointer-events: none;
  }
}
</style>
