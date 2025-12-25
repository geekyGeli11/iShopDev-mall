<template>
  <view class="signin-container">
    <!-- 背景图片 -->
    <image class="background-image"
      src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/signin/background.png" mode="scaleToFill" />

    <!-- 顶部导航栏 -->
    <nav-bar :placeholder="true" :back="true" :bg-color="'transparent'" :has-slot="false" :titleCenter="true">
    </nav-bar>

    <!-- 积分显示区域 -->
    <view class="points-section">
      <view class="points-display">
        <text class="points-number">{{ hasLogin ? userPoints : '---' }}</text>
        <text class="points-label">积分</text>
      </view>

      <!-- 右上角兑换记录按钮 -->
      <view class="exchange-record-btn" @tap="handleExchangeRecord">
        <text class="exchange-text">{{ hasLogin ? '兑换记录' : '登录后查看' }}</text>
      </view>

      <!-- 了解积分规则 -->
      <view class="rules-link" @tap="handlePointsRules">
        <text class="rules-text">了解积分规则</text>
        <view class="arrow-icon">></view>
      </view>
    </view>

    <!-- 签到内容区域 -->
    <view class="content-section">
      <!-- 签到区域容器（带阴影） -->
      <view class="signin-card-container">
        <!-- 每日签到标题 -->
        <view class="signin-title">
          <text class="title-text">每日签到赢积分，换取奖励</text>
        </view>

        <!-- 签到统计 -->
        <view class="signin-stats">
          <view class="stat-item continuous">
            <image class="stat-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/signin/star.png"
              mode="aspectFit" />
            <text class="stat-text">已连续签到 {{ hasLogin ? continuousDays : '--' }} 天</text>
          </view>
          <view class="stat-item total">
            <image class="stat-icon"
              src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/signin/calendar.png" mode="aspectFit" />
            <text class="stat-text">累计签到 {{ hasLogin ? totalDays : '--' }} 天</text>
          </view>
        </view>

        <!-- 本周签到日历 -->
        <view class="week-calendar">
          <view v-for="(day, index) in weekDays" :key="index" class="calendar-day"
            :class="{ 'signed': day.isSigned, 'today': day.isToday }" @tap="handleDayTap(day)">
            <view class="day-card">
              <!-- 积分奖励 -->
              <view class="points-reward">
                <text class="reward-text">+{{ day.points }}</text>
                <text class="reward-label">积分</text>
              </view>

              <!-- 签到图标 -->
              <view class="signin-icon">
                <view v-if="day.isSigned" class="icon-signed">✓</view>
                <view v-else-if="day.isToday" class="icon-today">签</view>
                <image v-else class="icon-future" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/signin/coins.png" mode="aspectFit" />
              </view>
            </view>

            <!-- 日期标签 -->
            <text class="day-label">{{ day.label }}</text>
          </view>
        </view>
      </view>

      <!-- 任务奖品部分 -->
      <view class="task-rewards">
        <view class="section-header">
          <view class="title-wrapper">
            <text class="title-text">任务奖品</text>
            <image class="title-indicator"
              src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/more-goods-indicator.png"
              mode="aspectFit" />
          </view>
          <text class="section-subtitle">每连续签到{{ signinConfig ? signinConfig.continuousDaysForReward : 30 }}天可领取</text>
        </view>

        <!-- 奖品卡片 -->
        <view class="reward-coupon-item">
          <view class="reward-coupon-content">
            <!-- 优惠券背景 -->
            <image class="coupon-bg"
              src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/order/coupon_background.png"
              mode="scaleToFill" />

            <!-- 优惠券信息 -->
            <view class="coupon-info">
              <view class="coupon-left">
                <view class="coupon-title">{{ getRewardCouponTitle() }}</view>
                <view class="coupon-rule">{{ getRewardCouponRule() }}</view>
                <view class="coupon-validity">{{ getRewardCouponValidity() }}</view>
              </view>
              <view class="coupon-right">
                <view class="status-area">
                  <view class="receive-text" @tap="handleClaimReward">{{ hasLogin ? '领取' : '登录后查看' }}</view>
                </view>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 积分兑换区域 -->
    <view class="points-exchange-section">
      <view>
        <!-- 积分兑换背景 -->
        <image class="exchange-background-image" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/signin/point_exchange_bg.png" mode="scaleToFill" />

        <!-- 积分兑换标题 -->
        <view class="exchange-title-section">
          <text class="exchange-title-text">积分换购</text>
        </view>

        <!-- 商品换购列表 -->
        <view class="product-list">
          <view v-if="productList.length === 0" class="empty-state">
            <u-empty text="暂无换购商品" mode="list"></u-empty>
          </view>
          <view v-else>
            <!-- 大卡片商品 -->
            <view v-for="(item, index) in productList.slice(0, 3)" :key="item.id" class="product-large-item"
              @click="handleProductExchange(item)">
              <view class="product-large-image">
                <image
                  :src="(item.productPic || '').replace(/`/g, '') || 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/default/product.png'"
                  mode="aspectFill">
                </image>
                <view v-if="!item.canExchange" class="sold-out-mask">
                  <text class="sold-out-text">{{ item.cannotExchangeReason || '不可兑换' }}</text>
                </view>
              </view>

              <view class="product-large-info">
                <view class="product-large-name">{{ item.productName }}</view>
                <view class="product-large-price-row">
                  <view class="product-large-price">
                    <text class="large-original-price">¥{{ item.originalPrice }}</text>
                    <text class="large-cash-price">¥{{ item.cashPrice }}{{ item.pointsPrice > 0 ? ' + ' +
                      item.pointsPrice + '积分' : '' }}</text>
                  </view>
                  <view class="product-large-btn" :class="{ 'points-exchange': item.pointsPrice > 0 }">
                    <text class="large-btn-text">{{ hasLogin ? (item.pointsPrice > 0 ? '积分兑换' : '立即定制') : '登录后查看' }}</text>
                  </view>
                </view>
              </view>
            </view>

            <!-- 小卡片商品网格 -->
            <view class="product-small-grid">
              <view v-for="(item, index) in productList.slice(3, 6)" :key="item.id" class="product-small-item"
                @click="handleProductExchange(item)">
                <!-- TOP标签 -->
                <view class="top-badge">
                  <text class="top-text">TOP{{ index + 4 }}</text>
                </view>

                <view class="product-small-image">
                  <image
                    :src="(item.productPic || '').replace(/`/g, '') || 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/default/product.png'"
                    mode="aspectFill">
                  </image>
                  <view v-if="!item.canExchange" class="sold-out-mask">
                    <text class="sold-out-text">{{ item.cannotExchangeReason || '不可兑换' }}</text>
                  </view>
                </view>

                <view class="product-small-info">
                  <view class="product-small-name">{{ item.productName }}</view>
                  <view class="product-small-price">
                    <text class="small-cash-price">¥{{ item.cashPrice }}{{ item.pointsPrice > 0 ? ' + ' +
                      item.pointsPrice + '积分' : '' }}</text>
                  </view>
                </view>
              </view>
            </view>


          </view>
        </view>
      </view>
      <!-- 积分领券区域 -->
      <view class="coupon-section">
        <view class="section-header">
          <view class="title-wrapper">
            <text class="title-text">积分领券</text>
            <image class="title-indicator"
              src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/more-goods-indicator.png"
              mode="aspectFit" />
          </view>
        </view>

        <view class="coupon-list">
          <view v-if="couponList.length === 0" class="empty-state">
            <u-empty text="暂无兑换优惠券" mode="list"></u-empty>
          </view>
          <view v-else>
            <view v-for="item in couponList" :key="item.id" class="coupon-item" @click="handleCouponExchange(item)">
              <view class="coupon-content">
                <!-- 优惠券背景 -->
                <image class="coupon-bg"
                  src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/order/coupon_background.png"
                  mode="scaleToFill" />

                <!-- 优惠券内容 -->
                <view class="coupon-info">
                  <!-- 左侧内容区域 -->
                  <view class="coupon-left">
                    <view class="coupon-title">{{ getCouponDisplayText(item) }}</view>
                    <view class="coupon-rule">规则：{{ getCouponCondition(item) }}</view>
                    <view class="coupon-validity">积分兑换：{{ item.pointsPrice }}积分</view>
                  </view>

                  <!-- 右侧状态区域 -->
                  <view class="coupon-right">
                    <view class="status-area">
                      <view class="receive-text" :class="{ 'receive-disabled': item.userExchangedCount > 0 }">
                        {{ hasLogin ? (item.userExchangedCount > 0 ? '去使用' : '立即兑换') : '登录后查看' }}
                      </view>
                    </view>
                  </view>
                </view>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 加载更多 -->
      <view v-if="hasMore" class="load-more" @click="loadMore">
        <u-loading-icon v-if="loading" mode="circle"></u-loading-icon>
        <text v-else>点击加载更多</text>
      </view>
    </view>



    <!-- 签到规则弹窗 -->
    <view v-if="showRulesPopup" class="rules-popup-mask" @tap="closeRulesPopup">
      <view class="rules-popup-content" @tap.stop>
        <!-- 弹窗标题和关闭按钮 -->
        <view class="popup-header">
          <text class="popup-title">签到规则</text>
          <view class="close-btn" @tap="closeRulesPopup">
            <view class="close-icon">×</view>
          </view>
        </view>

        <!-- 弹窗内容 -->
        <view class="popup-body">
          <text class="rules-content">{{ getSigninRulesText() }}</text>
        </view>

        <!-- 确认按钮 -->
        <view class="popup-footer">
          <view class="confirm-btn" @tap="closeRulesPopup">
            <text class="confirm-text">我知道了</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 商品兑换确认弹窗 -->
    <view v-if="showProductModal" class="custom-popup-mask" @click="showProductModal = false">
      <view class="custom-popup bottom-mode product-exchange-modal" @click.stop>
        <view class="modal-header">
          <text class="modal-title">确认兑换</text>
          <view class="close-btn" @click="showProductModal = false">×</view>
        </view>
        <view v-if="selectedProduct" class="modal-content product-modal-content">
          <!-- SKU选择器 -->
          <SkuSelector :product="selectedProduct" :skuList="selectedProduct.skuList || []"
            :specificationList="selectedProduct.specificationList || []" :maxQuantity="selectedProduct.perPersonLimit"
            :initialQuantity="exchangeQuantity" :useFixedPrice="true" @change="onSkuSelectionChange"
            ref="skuSelector" />
          <view class="product-row">
            <text class="row-label">收货地址：</text>
            <view class="address-info" @tap="selectAddress">
              <view v-if="currentAddress && currentAddress.name" class="address-content">
                <view class="address-main">
                  <text class="address-name">{{ currentAddress.name }}</text>
                  <text class="address-phone">{{ currentAddress.phoneNumber }}</text>
                </view>
                <text class="address-detail">{{ getFullAddress(currentAddress) }}</text>
              </view>
              <text v-else class="placeholder">请选择收货地址</text>
              <view class="arrow-right">></view>
            </view>
          </view>
          <view class="product-row total-row">
            <view class="total-item">
              <text>需要现金：</text>
              <text class="total-cash">¥{{ getProductTotalCash() }}</text>
            </view>
            <view class="total-item">
              <text>需要积分：</text>
              <text class="total-points">{{ getProductTotalPoints() }}积分</text>
            </view>
          </view>
        </view>
        <view class="modal-footer product-modal-footer">
          <button class="btn-plain" @click="showProductModal = false">取消</button>
          <button class="btn-primary" @click="confirmProductExchange" :disabled="productExchangeLoading">
            <text v-if="productExchangeLoading" class="btn-loading"></text>确认兑换
          </button>
        </view>
      </view>
    </view>

    <!-- 优惠券兑换确认弹窗 -->
    <view v-if="showCouponModal" class="custom-popup-mask" @click="showCouponModal = false">
      <view class="custom-popup center-mode coupon-exchange-modal" @click.stop>
        <view class="modal-header">
          <text class="modal-title">确认兑换</text>
          <u-icon name="close" @click="showCouponModal = false"></u-icon>
        </view>
        <view v-if="selectedCoupon" class="modal-content coupon-modal-content">
          <view class="coupon-preview-block">
            <text class="coupon-name">{{ selectedCoupon.couponName }}</text>
            <text class="coupon-desc">{{ getCouponDescription(selectedCoupon) }}</text>
          </view>
          <view class="exchange-info-block">
            <text class="exchange-label">需要积分</text>
            <text class="points-required">{{ selectedCoupon.pointsPrice }} 积分</text>
          </view>
        </view>
        <view class="modal-footer coupon-modal-footer">
          <button class="btn-plain" @click="showCouponModal = false">取消</button>
          <button class="btn-primary" @click="confirmCouponExchange" :disabled="couponExchangeLoading">
            <text v-if="couponExchangeLoading" class="btn-loading"></text>确认兑换
          </button>
        </view>
      </view>
    </view>

  </view>
</template>

<script>
import NavBar from '@/components/nav-bar.vue';
import SkuSelector from '@/components/SkuSelector.vue';
import {
  getSigninStatus,
  checkin,
  getSigninCalendar,
  claimContinuousReward,
  getSigninConfig
} from '@/api/signin.js';
import {
  getPointsProductList,
  getPointsCouponList,
  exchangeProduct,
  exchangeCoupon,
  createExchangePayOrder,
  exchangePaySuccess,
  createProductExchangeRecord
} from '@/api/point';
import { getWechatPayParams } from '@/api/order';
import { memberInfo } from '@/api/member';
import request from '@/utils/requestUtil';
import { mapState } from 'vuex';

export default {
  components: {
    'nav-bar': NavBar,
    SkuSelector
  },
  computed: {
    ...mapState(['hasLogin', 'userInfo']),

    totalCash() {
      if (!this.selectedProduct) return '0.00'

      // 如果有选中的SKU，使用SKU价格，否则使用商品价格
      const unitPrice = this.selectedSku && this.selectedSku.price
        ? this.selectedSku.price
        : this.selectedProduct.cashPrice

      return (unitPrice * this.exchangeQuantity).toFixed(2)
    },

    totalPoints() {
      if (!this.selectedProduct) return 0
      return this.selectedProduct.pointsPrice * this.exchangeQuantity
    }
  },
  data() {
    return {
      navBarHeight: 0,
      userPoints: 0, // 用户积分，从接口获取
      continuousDays: 0, // 连续签到天数
      totalDays: 1, // 累计签到天数
      signinLoading: false,

      // 本周日历数据
      weekDays: [
        { label: '今天', date: '', isSigned: true, isToday: true, points: 10 },
        { label: '明天', date: '', isSigned: false, isToday: false, points: 10 },
        { label: '6.30', date: '', isSigned: false, isToday: false, points: 10 },
        { label: '6.30', date: '', isSigned: false, isToday: false, points: 10 },
        { label: '6.30', date: '', isSigned: false, isToday: false, points: 10 },
        { label: '6.30', date: '', isSigned: false, isToday: false, points: 10 },
        { label: '6.30', date: '', isSigned: false, isToday: false, points: 10 }
      ],

      // 弹窗状态
      showRulesPopup: false,

      // 积分兑换相关数据
      // 商品换购
      productList: [],
      productQuery: {
        pageNum: 1,
        pageSize: 20,
        onlyAvailable: false
      },

      // 优惠券兑换
      couponList: [],
      couponQuery: {
        pageNum: 1,
        pageSize: 10,
        onlyAvailable: false
      },

      // 加载状态
      loading: false,
      hasMore: true,

      // 兑换相关
      showProductModal: false,
      showCouponModal: false,
      selectedProduct: null,
      selectedCoupon: null,
      exchangeQuantity: 1,
      selectedAddress: null,

      // 收货地址列表
      addressList: [],
      // 当前选中的地址
      currentAddress: null,

      // 筛选排序相关
      filterOptions: [
        { label: '综合', value: 'comprehensive', sortable: false },
        { label: '新品', value: 'newest', sortable: false },
        { label: '销量', value: 'sales', sortable: true },
        { label: '价格', value: 'price', sortable: true }
      ],
      currentFilter: 'comprehensive',
      currentSort: {
        field: '',
        order: '' // 'asc' or 'desc'
      },
      couponExchangeLoading: false,
      productExchangeLoading: false,

      // SKU选择相关
      selectedSku: null,
      skuSelection: null,

      // 签到配置
      signinConfig: null
    };
  },
  onLoad() {
    // 获取系统信息
    const systemInfo = uni.getSystemInfoSync();
    this.navBarHeight = systemInfo.statusBarHeight + 44;

    // 先设置默认数据，确保页面不显示undefined
    this.setDefaultSigninData();

    // 延迟检查登录状态，确保Vuex状态已初始化
    setTimeout(() => {
      // 先加载签到配置（不需要登录）
      this.loadSigninConfig();

      if (this.checkLogin()) {
        // 已登录，加载需要登录的数据
        this.loadSigninStatus();
        this.initExchangeData();
      } else {
        // 未登录，只显示基本页面结构，不调用需要登录的接口
        console.log('用户未登录，显示基本页面');
      }
    }, 100);
  },
  onShow() {
    // 每次显示页面时重新检查登录状态
    if (this.checkLogin()) {
      this.loadSigninStatus();
      // loadSigninStatus 已经会更新 userPoints，不需要重复调用
    }
  },

  onPullDownRefresh() {
    this.refresh();
  },

  onReachBottom() {
    this.loadMore();
  },
  methods: {
    // 检查登录状态（不强制登录，只返回登录状态）
    checkLogin() {
      // 检查Vuex中的登录状态
      if (!this.hasLogin) {
        // 再次检查本地存储的tokenInfo
        const tokenInfo = uni.getStorageSync('tokenInfo');
        let hasValidToken = false;

        if (tokenInfo) {
          try {
            const loginData = JSON.parse(tokenInfo);
            hasValidToken = !!(loginData && loginData.token && loginData.tokenHead && loginData.openId);
          } catch (e) {
            console.error('解析tokenInfo失败:', e);
            hasValidToken = false;
          }
        }

        return hasValidToken;
      }
      return true;
    },

    // 返回上一页
    handleBack() {
      uni.navigateBack();
    },

    // 兑换记录
    handleExchangeRecord() {
      if (!this.hasLogin) {
        uni.showModal({
          title: '提示',
          content: '请先登录后再操作',
          confirmText: '去登录',
          cancelText: '取消',
          success: (res) => {
            if (res.confirm) {
              // 保存当前页面信息到本地存储，登录成功后可以返回
              uni.setStorageSync('redirectPage', '/pages/signin/signin');
              // 使用Vuex统一管理登录弹窗状态
              this.$store.commit('setLoginPopup', { show: true, reason: 'unauthorized' });
              // 跳转到首页显示登录弹窗
              uni.switchTab({
                url: '/pages/new_index/index'
              });
            }
          }
        });
        return;
      }
      uni.navigateTo({
        url: '/pages/user/exchangeRecord'
      });
    },

    // 积分规则
    handlePointsRules() {
      this.showRulesPopup = true;
    },

    // 关闭规则弹窗
    closeRulesPopup() {
      this.showRulesPopup = false;
    },

    // 加载签到配置
    async loadSigninConfig() {
      // 未登录时不调用接口
      if (!this.hasLogin) {
        console.log('未登录，不获取签到配置');
        return;
      }

      try {
        const res = await getSigninConfig();
        if (res.code === 200 && res.data) {
          this.signinConfig = res.data;
          console.log('签到配置加载成功:', this.signinConfig);
        } else {
          console.warn('获取签到配置失败:', res);
        }
      } catch (error) {
        console.error('获取签到配置失败:', error);
      }
    },



    // 加载签到状态
    async loadSigninStatus() {
      try {
        const res = await getSigninStatus();
        if (res.code === 200) {
          const data = res.data;
          this.userPoints = data.currentPoints || 0;
          this.continuousDays = data.continuousDays || 0;
          this.totalDays = data.monthSigninDays || 1;

          // 重新生成日历（传入今日积分）
          this.generateWeekCalendar(data.todayPoints || 5);

          // 更新本周签到状态，包括今日签到状态
          this.updateWeekSigninStatus(data.signedDates || [], data.isSigninToday);
        } else if (res.code === 401) {
          // 未登录，设置默认值并提示
          this.setDefaultSigninData();
          uni.showToast({
            title: '请先登录',
            icon: 'none'
          });
        } else {
          // 其他错误，设置默认值并提示
          this.setDefaultSigninData();
          uni.showToast({
            title: '获取签到状态失败',
            icon: 'none'
          });
        }
      } catch (error) {
        console.error('获取签到状态失败:', error);
        // 设置默认值，确保页面正常显示
        this.setDefaultSigninData();

        if (error.response && error.response.status === 401) {
          uni.showToast({
            title: '请先登录',
            icon: 'none'
          });
        } else {
          uni.showToast({
            title: '网络错误，请稍后重试',
            icon: 'none'
          });
        }
      }
    },

    // 设置默认签到数据
    setDefaultSigninData() {
      this.userPoints = 0;
      this.continuousDays = 0;
      this.totalDays = 1;
      this.generateWeekCalendar(5);
    },

    // 生成本周日历
    generateWeekCalendar(todayPoints = 5) {
      const today = new Date();
      const currentWeekDay = today.getDay(); // 0是周日，1是周一
      const weekStart = new Date(today);

      // 计算本周一的日期
      const mondayOffset = currentWeekDay === 0 ? -6 : 1 - currentWeekDay;
      weekStart.setDate(today.getDate() + mondayOffset);

      this.weekDays = [];

      for (let i = 0; i < 7; i++) {
        const date = new Date(weekStart);
        date.setDate(weekStart.getDate() + i);

        const month = date.getMonth() + 1;
        const day = date.getDate();
        const isToday = date.toDateString() === today.toDateString();

        let label;
        if (isToday) {
          label = '今天';
        } else {
          const tomorrow = new Date(today.getTime() + 24 * 60 * 60 * 1000);
          if (date.toDateString() === tomorrow.toDateString()) {
            label = '明天';
          } else {
            label = `${month}.${day.toString().padStart(2, '0')}`;
          }
        }

        // 计算每日积分
        let dailyPoints = 5; // 默认基础积分
        if (isToday) {
          // 今日积分使用从接口获取的值
          dailyPoints = todayPoints;
        } else {
          // 其他天显示基础积分
          dailyPoints = 5;
        }

        this.weekDays.push({
          label: label,
          date: date.toISOString().split('T')[0],
          isSigned: false,
          isToday: isToday,
          points: dailyPoints
        });
      }
    },

    // 更新本周签到状态
    updateWeekSigninStatus(signedDates, isSigninToday) {
      this.weekDays.forEach(day => {
        day.isSigned = signedDates.includes(day.date);
        // 如果是今天且已签到，确保状态正确
        if (day.isToday) {
          day.isSigned = isSigninToday || false;
        }
      });
    },

    // 点击日期处理签到
    async handleDayTap(day) {
      // 签到前先检查登录状态
      if (!this.hasLogin) {
        uni.showModal({
          title: '提示',
          content: '请先登录后再操作',
          confirmText: '去登录',
          cancelText: '取消',
          success: (res) => {
            if (res.confirm) {
              // 保存当前页面信息到本地存储，登录成功后可以返回
              uni.setStorageSync('redirectPage', '/pages/signin/signin');
              // 使用Vuex统一管理登录弹窗状态
              this.$store.commit('setLoginPopup', { show: true, reason: 'unauthorized' });
              // 跳转到首页显示登录弹窗
              uni.switchTab({
                url: '/pages/new_index/index'
              });
            }
          }
        });
        return;
      }

      // 只能在今天且未签到的情况下签到
      if (!day.isToday || day.isSigned || this.signinLoading) {
        if (day.isSigned && day.isToday) {
          uni.showToast({
            title: '今日已签到',
            icon: 'none'
          });
        }
        return;
      }

      this.signinLoading = true;

      try {
        const res = await checkin();
        if (res.code === 200) {
          const data = res.data;

          // 检查签到结果
          if (data.points > 0) {
            // 签到成功
            day.isSigned = true;
            this.continuousDays = data.continuousDays;
            this.totalDays++;
            this.userPoints += data.points;

            uni.showToast({
              title: `签到成功，获得${data.points}积分`,
              icon: 'success'
            });
          } else {
            // 已签到的情况
            day.isSigned = true;
            uni.showToast({
              title: data.message || '今日已签到',
              icon: 'none'
            });
          }
        } else {
          uni.showToast({
            title: res.message || '签到失败',
            icon: 'none'
          });
        }
      } catch (error) {
        console.error('签到失败:', error);
        uni.showToast({
          title: '签到失败，请重试',
          icon: 'none'
        });
      } finally {
        this.signinLoading = false;
        // 签到后重新加载状态
        this.loadSigninStatus();
      }
    },

    // 领取奖励
    async handleClaimReward() {
      // 领取奖励前检查登录状态
      if (!this.hasLogin) {
        uni.showModal({
          title: '提示',
          content: '请先登录后再操作',
          confirmText: '去登录',
          cancelText: '取消',
          success: (res) => {
            if (res.confirm) {
              // 保存当前页面信息到本地存储，登录成功后可以返回
              uni.setStorageSync('redirectPage', '/pages/signin/signin');
              // 使用Vuex统一管理登录弹窗状态
              this.$store.commit('setLoginPopup', { show: true, reason: 'unauthorized' });
              // 跳转到首页显示登录弹窗
              uni.switchTab({
                url: '/pages/new_index/index'
              });
            }
          }
        });
        return;
      }

      if (this.continuousDays < 30) {
        uni.showToast({
          title: '连续签到30天才可领取',
          icon: 'none'
        });
        return;
      }

      try {
        const res = await claimContinuousReward();
        if (res.code === 200) {
          uni.showToast({
            title: '奖励领取成功',
            icon: 'success'
          });
        } else {
          uni.showToast({
            title: res.message || '领取失败',
            icon: 'none'
          });
        }
      } catch (error) {
        console.error('领取奖励失败:', error);
        uni.showToast({
          title: '领取失败，请重试',
          icon: 'none'
        });
      }
    },

    // ============ 积分兑换相关方法 ============

    // 初始化积分兑换数据
    async initExchangeData() {
      try {
        console.log('开始初始化积分兑换数据');
        // 不需要重复获取积分，loadSigninStatus 已经获取了
        await this.getProductList();
        await this.getCouponList();
        console.log('积分兑换数据初始化完成');
      } catch (error) {
        console.error('初始化积分兑换数据失败:', error);
        // 即使失败也不阻止页面显示
      }
    },

    // 刷新数据
    async refresh() {
      this.productQuery.pageNum = 1;
      this.couponQuery.pageNum = 1;
      this.hasMore = true;

      await this.getProductList();
      await this.getCouponList();

      uni.stopPullDownRefresh();
    },

    // 获取用户积分（使用签到API获取最新积分）
    async getUserPoints() {
      try {
        const result = await getSigninStatus();
        if (result.code === 200) {
          this.userPoints = result.data.currentPoints || 0;
        }
      } catch (error) {
        console.error('获取用户积分失败', error);
      }
    },

    // 获取商品列表
    async getProductList(append = false) {
      if (this.loading) return;

      // 未登录时不调用接口
      if (!this.hasLogin) {
        console.log('未登录，不获取商品列表');
        this.productList = [];
        return;
      }

      this.loading = true;
      try {
        const result = await getPointsProductList(this.productQuery);
        const list = result.data.list || [];

        if (append) {
          this.productList = [...this.productList, ...list];
        } else {
          this.productList = list;
        }

        this.hasMore = list.length >= this.productQuery.pageSize;
      } catch (error) {
        console.error('获取商品列表失败:', error);
        if (!append) {
          this.productList = []; // 确保有默认值
        }
        uni.showToast({
          title: '获取商品列表失败',
          icon: 'none'
        });
      } finally {
        this.loading = false;
      }
    },

    // 获取优惠券列表
    async getCouponList(append = false) {
      if (this.loading) return;

      // 未登录时不调用接口
      if (!this.hasLogin) {
        console.log('未登录，不获取优惠券列表');
        this.couponList = [];
        return;
      }

      this.loading = true;
      try {
        const result = await getPointsCouponList(this.couponQuery);
        const list = result.data.list || [];

        if (append) {
          this.couponList = [...this.couponList, ...list];
        } else {
          this.couponList = list;
        }

        this.hasMore = list.length >= this.couponQuery.pageSize;
      } catch (error) {
        uni.showToast({
          title: '获取优惠券列表失败',
          icon: 'none'
        });
      } finally {
        this.loading = false;
      }
    },

    // 加载更多
    loadMore() {
      if (!this.hasMore || this.loading) return;

      this.productQuery.pageNum++;
      this.getProductList(true);

      this.couponQuery.pageNum++;
      this.getCouponList(true);
    },

    // 处理商品兑换
    handleProductExchange(item) {
      // 未登录时提示登录
      if (!this.hasLogin) {
        uni.showModal({
          title: '提示',
          content: '请先登录后再操作',
          confirmText: '去登录',
          cancelText: '取消',
          success: (res) => {
            if (res.confirm) {
              // 保存当前页面信息到本地存储，登录成功后可以返回
              uni.setStorageSync('redirectPage', '/pages/signin/signin');
              // 使用Vuex统一管理登录弹窗状态
              this.$store.commit('setLoginPopup', { show: true, reason: 'unauthorized' });
              // 跳转到首页显示登录弹窗
              uni.switchTab({
                url: '/pages/new_index/index'
              });
            }
          }
        });
        return;
      }

      if (!item.canExchange) {
        uni.showToast({
          title: item.cannotExchangeReason || '不可兑换',
          icon: 'none'
        });
        return;
      }

      this.selectedProduct = item;
      this.exchangeQuantity = 1;
      // 重置SKU选择状态
      this.skuSelection = null;
      this.selectedSku = null;
      this.showProductModal = true;
    },

    // 处理优惠券兑换
    handleCouponExchange(item) {
      // 未登录时提示登录
      if (!this.hasLogin) {
        uni.showModal({
          title: '提示',
          content: '请先登录后再操作',
          confirmText: '去登录',
          cancelText: '取消',
          success: (res) => {
            if (res.confirm) {
              // 保存当前页面信息到本地存储，登录成功后可以返回
              uni.setStorageSync('redirectPage', '/pages/signin/signin');
              // 使用Vuex统一管理登录弹窗状态
              this.$store.commit('setLoginPopup', { show: true, reason: 'unauthorized' });
              // 跳转到首页显示登录弹窗
              uni.switchTab({
                url: '/pages/new_index/index'
              });
            }
          }
        });
        return;
      }

      if (!item.canExchange) {
        uni.showToast({
          title: '不可兑换',
          icon: 'none'
        });
        return;
      }

      this.selectedCoupon = item;
      this.showCouponModal = true;
    },

    // 选择收货地址
    selectAddress() {
      uni.navigateTo({
        url: '/pages/address/address?source=1'
      });
    },

    // 处理SKU选择变化
    onSkuSelectionChange(selection) {
      console.log('SKU选择变化:', selection);
      this.skuSelection = selection;
      this.selectedSku = selection.selectedSku;
      this.exchangeQuantity = selection.quantity;

      // 强制更新计算属性
      this.$forceUpdate();
    },

    // 获取商品总现金价格（不受SKU影响）
    getProductTotalCash() {
      if (!this.selectedProduct) return '0.00';
      return (this.selectedProduct.cashPrice * this.exchangeQuantity).toFixed(2);
    },

    // 获取商品总积分价格（不受SKU影响）
    getProductTotalPoints() {
      if (!this.selectedProduct) return 0;
      return this.selectedProduct.pointsPrice * this.exchangeQuantity;
    },

    // 确认商品兑换
    async confirmProductExchange() {
      if (!this.currentAddress) {
        uni.showToast({
          title: '请选择收货地址',
          icon: 'none'
        });
        return;
      }
      if (this.userPoints < this.getProductTotalPoints()) {
        uni.showToast({
          title: '积分不足',
          icon: 'none'
        });
        return;
      }

      // 验证SKU选择
      if (this.selectedProduct.skuList && this.selectedProduct.skuList.length > 0) {
        if (!this.skuSelection || !this.skuSelection.isValid) {
          uni.showToast({
            title: '请选择完整的商品规格',
            icon: 'none'
          });
          return;
        }
      }

      this.productExchangeLoading = true;
      try {
        const exchangeParams = {
          configId: this.selectedProduct.id,
          productSkuId: this.selectedSku ? this.selectedSku.id : null,
          quantity: this.exchangeQuantity,
          addressId: this.currentAddress.id,
          remark: ''
        };

        // 检查是否需要现金支付
        const totalCashAmount = parseFloat(this.getProductTotalCash());
        if (totalCashAmount > 0) {
          // 需要现金支付，先创建支付订单
          await this.handlePaymentAndExchange(exchangeParams, totalCashAmount);
        } else {
          // 纯积分兑换，直接调用兑换接口
          const result = await exchangeProduct(exchangeParams);
          uni.showToast({
            title: '兑换成功',
            icon: 'success'
          });
          this.showProductModal = false;
          this.getUserPoints();
          this.getProductList();
        }
      } catch (error) {
        uni.showToast({
          title: error.message || '兑换失败',
          icon: 'none'
        });
      } finally {
        this.productExchangeLoading = false;
      }
    },

    // 确认优惠券兑换
    async confirmCouponExchange() {
      if (this.userPoints < this.selectedCoupon.pointsPrice) {
        uni.showToast({
          title: '积分不足',
          icon: 'none'
        });
        return;
      }
      this.couponExchangeLoading = true;
      try {
        console.log('开始兑换优惠券', this.selectedCoupon);
        const params = {
          configId: this.selectedCoupon.id,
          remark: ''
        };
        const result = await exchangeCoupon(params);
        console.log('兑换结果', result);
        uni.showToast({
          title: '兑换成功',
          icon: 'success'
        });
        this.showCouponModal = false;
        this.getUserPoints();
        this.getCouponList();
      } catch (error) {
        console.error('兑换失败', error);
        uni.showToast({
          title: error.message || '兑换失败',
          icon: 'none'
        });
      } finally {
        this.couponExchangeLoading = false;
      }
    },

    // 处理支付和兑换流程
    async handlePaymentAndExchange(exchangeParams, cashAmount) {
      try {
        // 1. 创建支付订单
        const payOrderData = {
          configId: exchangeParams.configId,
          productSkuId: exchangeParams.productSkuId,
          quantity: exchangeParams.quantity,
          addressId: exchangeParams.addressId,
          cashAmount: cashAmount,
          pointsAmount: this.getProductTotalPoints(),
          remark: exchangeParams.remark
        };

        const payOrderResult = await createExchangePayOrder(payOrderData);
        const { orderSn, payAmount } = payOrderResult.data;

        // 2. 调用支付
        await this.initiatePayment(orderSn, payAmount, exchangeParams);

      } catch (error) {
        throw new Error(error.message || '创建支付订单失败');
      }
    },

    // 发起支付
    async initiatePayment(orderSn, payAmount, exchangeParams) {
      // #ifdef MP-WEIXIN
      // 小程序环境直接调用微信支付
      try {
        await this.handleWechatPayment(orderSn, payAmount, exchangeParams);
      } catch (error) {
        uni.showToast({
          title: '支付失败：' + error.message,
          icon: 'none'
        });
      }
      // #endif

      // #ifndef MP-WEIXIN
      // 非小程序环境显示支付方式选择
      const paymentMethods = ['支付宝', '微信支付'];

      uni.showActionSheet({
        itemList: paymentMethods,
        success: async (res) => {
          const payType = res.tapIndex + 1; // 1-支付宝，2-微信

          try {
            if (payType === 1) {
              // 支付宝支付
              await this.handleAlipayPayment(orderSn, payAmount, exchangeParams);
            } else if (payType === 2) {
              // 微信支付
              await this.handleWechatPayment(orderSn, payAmount, exchangeParams);
            }
          } catch (error) {
            uni.showToast({
              title: '支付失败：' + error.message,
              icon: 'none'
            });
          }
        },
        fail: () => {
          // 用户取消支付
          uni.showToast({
            title: '已取消支付',
            icon: 'none'
          });
        }
      });
      // #endif
    },

    // 微信支付处理
    async handleWechatPayment(orderSn, payAmount, exchangeParams) {
      // 获取用户openId
      let openId = null;

      // #ifdef MP-WEIXIN
      // 优先从store中获取用户信息
      const userInfo = this.$store.state.userInfo;
      openId = userInfo?.openId || userInfo?.openid;

      // 如果store中没有openId，尝试从缓存获取
      if (!openId) {
        try {
          const cachedUserInfo = uni.getStorageSync('userInfo');
          if (cachedUserInfo) {
            openId = cachedUserInfo.openId || cachedUserInfo.openid;
          }
        } catch (error) {
          console.error('从缓存获取用户信息失败', error);
        }
      }

      // 如果缓存中也没有openId，尝试从API获取
      if (!openId) {
        try {
          const memberResult = await memberInfo();
          if (memberResult.code === 200 && memberResult.data) {
            openId = memberResult.data.openId || memberResult.data.openid;
            // 更新store中的用户信息
            if (openId) {
              this.$store.commit('login', { ...userInfo, openId });
            }
          }
        } catch (error) {
          console.error('获取用户信息失败', error);
        }
      }

      // 如果仍然没有openId，提示用户重新登录
      if (!openId) {
        uni.showToast({
          title: '获取用户信息失败，请重新登录',
          icon: 'none'
        });
        // 跳转到登录页面
        uni.navigateTo({
          url: '/pages/login/login'
        });
        return;
      }
      // #endif

      const payData = {
        orderSn: orderSn,
        amount: Math.round(payAmount * 100), // 将元转换为分
        payType: 2,
        openId: openId
      };

      const result = await getWechatPayParams(payData);

      // #ifdef MP-WEIXIN
      // 微信小程序支付
      console.log('微信支付参数:', result.data);
      uni.requestPayment({
        timeStamp: result.data.timeStamp,
        nonceStr: result.data.nonceStr,
        package: result.data.packageValue || result.data.package,
        signType: result.data.signType,
        paySign: result.data.paySign,
        success: async (res) => {
          console.log('微信支付成功:', res);
          // 微信小程序支付成功后，通常不会直接返回transactionId
          // 先尝试查询支付状态获取真实的交易号
          try {
            await this.queryAndHandlePaymentSuccess(orderSn, 2, exchangeParams);
          } catch (error) {
            // 如果查询失败，使用订单号作为支付流水号
            console.warn('查询支付状态失败，使用订单号作为支付流水号:', error);
            await this.handlePaymentSuccess(orderSn, 2, orderSn, exchangeParams);
          }
        },
        fail: (err) => {
          console.error('微信支付失败详情:', err);
          uni.showToast({
            title: '支付失败: ' + (err.errMsg || '未知错误'),
            icon: 'none'
          });
        }
      });
      // #endif

      // #ifdef H5
      // H5环境微信支付
      if (result.data && result.data.mweb_url) {
        window.location.href = result.data.mweb_url;
      }
      // #endif
    },

    // 查询支付状态并处理支付成功
    async queryAndHandlePaymentSuccess(orderSn, payType, exchangeParams) {
      try {
        // 调用后端的查询支付状态接口
        const queryResult = await request({
          url: '/wxpay/queryAndUpdateOrder',
          method: 'get',
          params: { orderSn: orderSn }
        });

        if (queryResult.code === 200) {
          // 支付状态查询成功，创建兑换记录
          await this.createExchangeRecordAfterPayment(exchangeParams);

          uni.showToast({
            title: '支付成功，兑换完成',
            icon: 'success'
          });

          this.showProductModal = false;
          this.getUserPoints();
          this.getProductList();
        } else {
          throw new Error(queryResult.message || '查询支付状态失败');
        }
      } catch (error) {
        console.error('查询支付状态失败:', error);
        throw error;
      }
    },

    // 处理支付成功
    async handlePaymentSuccess(orderSn, payType, transactionId, exchangeParams) {
      try {
        // 调用支付成功回调接口，后端会处理订单状态更新和积分扣减等逻辑
        const paySuccessData = {
          orderSn: orderSn,
          payType: payType,
          paySn: transactionId
        };

        await exchangePaySuccess(paySuccessData);

        // 创建兑换记录
        await this.createExchangeRecordAfterPayment(exchangeParams);

        uni.showToast({
          title: '支付成功，兑换完成',
          icon: 'success'
        });

        this.showProductModal = false;
        this.getUserPoints();
        this.getProductList();

      } catch (error) {
        uni.showToast({
          title: '支付成功但处理失败，请联系客服',
          icon: 'none'
        });
      }
    },

    // 支付成功后创建兑换记录
    async createExchangeRecordAfterPayment(exchangeParams) {
      try {
        console.log('创建兑换记录，参数：', exchangeParams);

        // 调用专门的创建兑换记录接口，只创建记录，不扣积分和减库存
        const recordParams = {
          configId: exchangeParams.configId,
          productSkuId: exchangeParams.productSkuId,
          quantity: exchangeParams.quantity,
          addressId: exchangeParams.addressId,
          remark: exchangeParams.remark || '积分+现金兑换'
        };

        await createProductExchangeRecord(recordParams);

        console.log('兑换记录创建成功');
      } catch (error) {
        console.error('创建兑换记录失败：', error);
        // 不抛出异常，避免影响支付成功的提示
      }
    },

    // 获取优惠券金额
    getCouponAmount(coupon) {
      return coupon.amount || '0';
    },

    // 获取优惠券使用条件（根据接口返回的数据结构）
    getCouponCondition(coupon) {
      // 优先使用接口返回的 minPoint 字段
      if (coupon.minPoint && coupon.minPoint > 0) {
        return `满${coupon.minPoint}元可用`;
      }

      // 如果没有 minPoint 字段，尝试从名称中提取
      const couponName = coupon.couponName || coupon.name || '';

      if (couponName.includes('折')) {
        // 尝试从名称中提取折扣信息
        const match = couponName.match(/([零一二三四五六七八九十百千万\d.]+)折/);
        if (match) {
          const discount = match[1];
          return `${discount}折优惠`;
        }
        return '折扣优惠';
      }

      // 默认显示为无门槛
      return '无门槛使用';
    },

    // 获取优惠券描述（根据接口返回的数据结构）
    getCouponDescription(coupon) {
      const couponName = coupon.couponName || coupon.name || '优惠券';
      const condition = this.getCouponCondition(coupon);

      // 优先使用接口返回的优惠券类型信息
      if (coupon.couponTypeDetail !== undefined) {
        if (coupon.couponTypeDetail === 0) {
          // 满减券
          const amount = coupon.amount || 0;
          return `${amount}元优惠券，${condition}`;
        } else if (coupon.couponTypeDetail === 1) {
          // 打折券
          if (coupon.discountRate) {
            const discount = (coupon.discountRate * 10).toFixed(1);
            return `${discount}折优惠券，${condition}`;
          }
        }
      }

      // 如果没有接口字段，尝试从名称判断
      if (couponName.includes('折')) {
        // 打折券
        const match = couponName.match(/([零一二三四五六七八九十百千万\d.]+)折/);
        if (match) {
          const discount = match[1];
          return `${discount}折优惠券，${condition}`;
        }
        return `折扣优惠券，${condition}`;
      }

      // 默认作为现金券处理
      const amount = coupon.amount || 0;
      return `${amount}元优惠券，${condition}`;
    },

    // 获取优惠券显示文本（根据接口返回的数据结构）
    getCouponDisplayText(coupon) {
      const couponName = coupon.couponName || coupon.name || '优惠券';

      // 优先使用接口返回的优惠券类型信息
      if (coupon.couponTypeDetail !== undefined) {
        if (coupon.couponTypeDetail === 0) {
          // 满减券
          const amount = coupon.amount || 0;
          return `${couponName} - ${amount}元`;
        } else if (coupon.couponTypeDetail === 1) {
          // 打折券
          if (coupon.discountRate) {
            const discount = (coupon.discountRate * 10).toFixed(1);
            return `${couponName} - ${discount}折`;
          }
        }
      }

      // 如果没有接口字段，使用原有逻辑
      return `积分兑换 - ${couponName}`;
    },

    // 处理筛选变化
    handleFilterChange(filterValue) {
      if (this.currentFilter === filterValue) return;

      this.currentFilter = filterValue;
      this.currentSort = { field: '', order: '' };

      // 重置分页并重新加载数据
      this.productQuery.pageNum = 1;
      this.updateProductQuery();
      this.getProductList();
    },

    // 处理排序变化
    handleSortChange(field, order) {
      this.currentFilter = field;
      this.currentSort = { field, order };

      // 重置分页并重新加载数据
      this.productQuery.pageNum = 1;
      this.updateProductQuery();
      this.getProductList();
    },

    // 获取排序箭头颜色
    getSortArrowColor(field, direction) {
      if (this.currentFilter === field && this.currentSort.field === field && this.currentSort.order === direction) {
        return '#647D00';
      }
      return '#CCCCCC';
    },

    // 更新商品查询参数
    updateProductQuery() {
      // 根据筛选和排序条件更新查询参数
      switch (this.currentFilter) {
        case 'comprehensive':
          this.productQuery.sortBy = 'comprehensive';
          this.productQuery.sortOrder = 'desc';
          break;
        case 'newest':
          this.productQuery.sortBy = 'createTime';
          this.productQuery.sortOrder = 'desc';
          break;
        case 'sales':
          this.productQuery.sortBy = 'salesCount';
          this.productQuery.sortOrder = this.currentSort.order || 'desc';
          break;
        case 'price':
          this.productQuery.sortBy = 'cashPrice';
          this.productQuery.sortOrder = this.currentSort.order || 'asc';
          break;
        default:
          this.productQuery.sortBy = 'comprehensive';
          this.productQuery.sortOrder = 'desc';
      }
    },

    // 格式化日期
    formatDate(dateStr) {
      if (!dateStr) return '';
      const date = new Date(dateStr);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}.${month}.${day}`;
    },

    // 获取完整地址
    getFullAddress(address) {
      if (!address) return '';
      const parts = [];
      if (address.province) parts.push(address.province);
      if (address.city) parts.push(address.city);
      if (address.region) parts.push(address.region);
      if (address.detailAddress) parts.push(address.detailAddress);
      return parts.join(' ');
    },

    // 获取奖励优惠券标题
    getRewardCouponTitle() {
      if (this.signinConfig && this.signinConfig.rewardCouponName) {
        return `任务奖品 - ${this.signinConfig.rewardCouponName}`;
      }
      return '任务奖品 - 优惠券';
    },

    // 获取奖励优惠券规则
    getRewardCouponRule() {
      if (this.signinConfig && this.signinConfig.rewardCouponMinPoint) {
        const minPoint = this.signinConfig.rewardCouponMinPoint;
        if (minPoint > 0) {
          return `规则：满${minPoint}元可用`;
        }
      }
      return '规则：无门槛使用';
    },

    // 获取奖励优惠券有效期
    getRewardCouponValidity() {
      if (this.signinConfig && this.signinConfig.rewardCouponStartTime && this.signinConfig.rewardCouponEndTime) {
        return `使用时间：${this.signinConfig.rewardCouponStartTime} - ${this.signinConfig.rewardCouponEndTime}`;
      }
      return '使用时间：领取后生效';
    },

    // 获取签到规则文本
    getSigninRulesText() {
      if (!this.signinConfig) {
        return '初次签到得5积分，连续签到每日递增3积分，上限30积分。签到中断，按初次签到重新开始。连续签到30天获取优惠券。在单个周期内（当前周期：30天）连续签到可以获得连签奖励，同个周期内最多可领取1次，已连续签到0天';
      }

      const basePoints = this.signinConfig.basePoints || 5;
      const incrementPoints = this.signinConfig.incrementPoints || 3;
      const maxDailyPoints = this.signinConfig.maxDailyPoints || 30;
      const continuousDaysForReward = this.signinConfig.continuousDaysForReward || 30;
      const cycleDays = this.signinConfig.cycleDays || 30;

      return `初次签到得${basePoints}积分，连续签到每日递增${incrementPoints}积分，上限${maxDailyPoints}积分。签到中断，按初次签到重新开始。连续签到${continuousDaysForReward}天获取优惠券。在单个周期内（当前周期：${cycleDays}天）连续签到可以获得连签奖励，同个周期内最多可领取1次，已连续签到${this.continuousDays}天`;
    }
  }
};
</script>

<style lang="scss" scoped>
.signin-container {
  position: relative;
  width: 100%;
  min-height: 100vh;
  background-color: #F5F5F5;
  padding-bottom: 120rpx;
}

.background-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 540rpx;
  z-index: 1;
}

/* 导航栏 */
.nav-bar-content {
  position: relative;
  z-index: 10;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 30rpx;
}

.nav-left,
.nav-right {
  width: 80rpx;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-btn {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-icon {
  font-size: 32rpx;
  color: #000000;
  font-weight: 600;
}

.more-btn {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.more-dots {
  display: flex;
  gap: 4rpx;
}

.dot {
  width: 6rpx;
  height: 6rpx;
  background-color: #000;
  border-radius: 50%;
}

/* 积分显示区域 */
.points-section {
  position: relative;
  z-index: 5;
  padding: 80rpx 30rpx 80rpx;
  text-align: left;
}

.points-display {
  margin-bottom: 40rpx;
}

.points-number {
  font-size: 80rpx;
  font-weight: 700;
  color: #FFFFFF;
  line-height: 1.2;
  font-family: 'Alibaba PuHuiTi 3.0', -apple-system, BlinkMacSystemFont, sans-serif;
}

.points-label {
  font-size: 28rpx;
  color: #FFFFFF;
  margin-left: 8rpx;
  font-weight: 400;
}

.exchange-record-btn {
  position: absolute;
  top: 120rpx;
  right: 0;
  background-color: rgba(0, 0, 0, 0.7);
  border-radius: 32rpx 0 0 32rpx;
  padding: 8rpx 32rpx;
}

.exchange-text {
  font-size: 28rpx;
  color: #FFFFFF;
  font-weight: 400;
}

.rules-link {
  position: absolute;
  left: 30rpx;
  display: flex;
  align-items: center;
  gap: 6rpx;
}

.rules-text {
  font-size: 28rpx;
  color: #FFFFFF;
  font-weight: 400;
}

.arrow-icon {
  font-size: 20rpx;
  color: #FFFFFF;
  font-weight: 400;
}

/* 内容区域 */
.content-section {
  position: relative;
  z-index: 5;
  background-color: #FFFFFF;
  border-radius: 16rpx;
  margin: -20rpx 20rpx 20rpx;
  padding: 32rpx 24rpx;
}

/* 签到区域容器样式（带阴影） */
.signin-card-container {
  background: #FFFFFF;
  box-shadow: 0px 4px 4px rgba(0, 0, 0, 0.25);
  border-radius: 8px;
  padding: 24rpx;
  margin-bottom: 24rpx;
}

.signin-title {
  text-align: left;
  margin-bottom: 32rpx;
}

.title-text {
  font-size: 32rpx;
  font-weight: 500;
  color: #000000;
  line-height: 1.4;
}

/* 签到统计 */
.signin-stats {
  display: flex;
  gap: 2rpx;
  margin-bottom: 40rpx;
}

.stat-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  padding: 12rpx 32rpx;
  background-color: rgba(100, 125, 0, 0.09);
}

.stat-item.continuous {
  border-radius: 32rpx 0 0 32rpx;
  border-right: 1rpx solid rgba(100, 125, 0, 0.1);
}

.stat-item.total {
  border-radius: 0 32rpx 32rpx 0;
}

.stat-icon {
  width: 24rpx;
  height: 24rpx;
}

.stat-text {
  font-size: 24rpx;
  color: #647D00;
  font-weight: 400;
}

/* 本周签到日历 */
.week-calendar {
  display: flex;
  justify-content: space-between;
  gap: 8rpx;
  margin-bottom: 48rpx;
}

.calendar-day {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  width: 78rpx;
}

.day-card {
  width: 78rpx;
  height: 130rpx;
  background: #F8F8F8;
  border: 1px solid #E5E5E5;
  border-radius: 8rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-between;
  padding: 12rpx 8rpx;
  position: relative;
}

/* 已签到状态 */
.calendar-day.signed .day-card {
  background: #282921;
  border: 1px solid #A9FF00;
}

.calendar-day.signed .reward-text {
  color: #A9FF00;
  font-weight: 600;
}

.calendar-day.signed .reward-label {
  color: #A9FF00;
  font-weight: 500;
}

.calendar-day.signed .day-label {
  color: #999999;
  font-weight: 500;
}

.calendar-day.signed .icon-signed {
  color: #A9FF00;
}

/* 未签到状态 */
.calendar-day:not(.signed) .day-card {
  background: #E5FDC0;
  border: 1px solid #E5E5E5;
}

.calendar-day:not(.signed) .reward-text {
  color: #282921;
}

.calendar-day:not(.signed) .reward-label {
  color: #282921;
}

.calendar-day:not(.signed) .day-label {
  color: #282921;
}

/* 今日待签到状态（未签到） */
.calendar-day.today:not(.signed) .day-card {
  background: #FFFFFF;
  border: 2px solid #A9FF00;
}

.calendar-day.today:not(.signed) .reward-text {
  color: #647D00;
  font-weight: 600;
}

.calendar-day.today:not(.signed) .reward-label {
  color: #647D00;
  font-weight: 500;
}

.calendar-day.today:not(.signed) .day-label {
  color: #647D00;
  font-weight: 500;
}

.points-reward {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 8rpx;
}

.reward-text {
  font-size: 28rpx;
  color: #000000;
  font-weight: 400;
  line-height: 1.2;
}

.reward-label {
  font-size: 20rpx;
  color: #999999;
  font-weight: 400;
  line-height: 1.2;
}

.signin-icon {
  width: 32rpx;
  height: 32rpx;
  margin-top: 4rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-signed {
  color: #A9FF00;
  font-size: 28rpx;
  font-weight: bold;
}

.icon-today {
  color: #A9FF00;
  font-size: 24rpx;
  font-weight: bold;
  background-color: rgba(169, 255, 0, 0.2);
  border-radius: 50%;
  width: 32rpx;
  height: 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-future {
  width: 24rpx;
  height: 24rpx;
}

.day-label {
  font-size: 24rpx;
  color: #000000;
  font-weight: 400;
  text-align: center;
}

/* 任务奖品 */
.task-rewards {
  margin-bottom: 32rpx;
}

.task-rewards .section-header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 24rpx;
  padding: 0 0rpx 20rpx;
}

.section-title {
  font-size: 28rpx;
  font-weight: 500;
  color: #000000;
}

.section-subtitle {
  font-size: 28rpx;
  color: #999999;
  font-weight: 400;
}

/* 奖品优惠券样式 - 复用积分领券样式 */
.reward-coupon-item {
  margin: 24rpx 0;
  position: relative;
}

.reward-coupon-content {
  position: relative;
  width: 100%;
  height: 184rpx;
}







/* 签到规则弹窗 */
.rules-popup-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.rules-popup-content {
  width: 600rpx;
  background-color: #FFFFFF;
  border-radius: 16rpx;
  overflow: hidden;
  position: relative;
}

.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 40rpx 40rpx 20rpx;
  border-bottom: 1rpx solid #F5F5F5;
}

.popup-title {
  font-size: 36rpx;
  font-weight: 500;
  color: #000000;
}

.close-btn {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #F5F5F5;
  border-radius: 50%;
}

.close-icon {
  font-size: 40rpx;
  color: #999999;
  line-height: 1;
}

.popup-body {
  padding: 40rpx;
}

.rules-content {
  font-size: 28rpx;
  color: #666666;
  line-height: 1.6;
  font-weight: 400;
}

.popup-footer {
  padding: 20rpx 40rpx 40rpx;
}

.confirm-btn {
  width: 100%;
  height: 88rpx;
  background-color: #000000;
  border-radius: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.confirm-text {
  font-size: 32rpx;
  color: #A9FF00;
  font-weight: 500;
}

/* ============ 积分兑换区域样式 ============ */

.points-exchange-section {
  margin: 20rpx;
}

/* 积分换购容器样式 */
.points-exchange-section > view:first-child {
  position: relative;
  background: linear-gradient(180deg, #282921 0%, #1a1c16 100%);
  border-radius: 16rpx;
  padding: 32rpx 24rpx;
  overflow: hidden;
  margin-bottom: 40rpx;
}

.exchange-background-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  opacity: 0.3;
}

.product-list {
  position: relative;
  z-index: 5;
}

.exchange-title-section {
  position: relative;
  z-index: 5;
  margin-bottom: 32rpx;
  text-align: center;
}

.exchange-title-text {
  font-size: 40rpx;
  font-weight: 400;
  color: #A9FF00;
  line-height: 1.4;
  display: block;
}

.title-wrapper {
  position: relative;
  display: inline-block;
}

.title-text {
  position: relative;
  z-index: 2;
  font-size: 32rpx;
  color: #000000;
  font-weight: 500;
  display: inline-block;
}

.title-indicator {
  position: absolute;
  z-index: 1;
  width: 32rpx;
  height: 32rpx;
  right: -8rpx;
  bottom: -8rpx;
}

/* 大卡片商品样式 */
.product-large-item {
  position: relative;
  z-index: 5;
  background: #FFFFFF;
  border-radius: 16rpx;
  margin-bottom: 24rpx;
  overflow: hidden;
  display: flex;
  align-items: center;
  padding: 24rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.product-large-image {
  width: 160rpx;
  height: 160rpx;
  border-radius: 16rpx;
  overflow: hidden;
  margin-right: 24rpx;
  position: relative;
  flex-shrink: 0;
}

.product-large-image image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-large-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 160rpx;
}

.product-large-name {
  font-size: 28rpx;
  color: #0A0D05;
  font-weight: 400;
  line-height: 1.4;
  margin-bottom: 16rpx;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-large-price-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.product-large-price {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.large-original-price {
  font-size: 28rpx;
  color: #999999;
  text-decoration: line-through;
  line-height: 1.4;
  margin-bottom: 8rpx;
}

.large-cash-price {
  font-size: 36rpx;
  font-weight: 700;
  color: #647D00;
  line-height: 1.2;
}

.product-large-btn {
  background: #0A0D05;
  border-radius: 8rpx;
  padding: 8rpx 20rpx;
  min-width: 120rpx;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-large-btn.points-exchange {
  background: #0A0D05;
}

.large-btn-text {
  font-size: 24rpx;
  font-weight: 600;
  color: #A9FF00;
  line-height: 1.4;
}

/* 小卡片商品网格样式 */
.product-small-grid {
  position: relative;
  z-index: 5;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16rpx;
  margin-bottom: 32rpx;
}

.product-small-item {
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 16rpx;
  position: relative;
  display: flex;
  flex-direction: column;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.top-badge {
  position: absolute;
  top: 0;
  left: 0;
  background: #282921;
  border-radius: 16rpx 0 8rpx 0;
  padding: 4rpx 8rpx;
  z-index: 10;
}

.top-text {
  font-size: 24rpx;
  font-weight: 500;
  color: #A9FF00;
  line-height: 1.4;
}

.product-small-image {
  width: 100%;
  aspect-ratio: 1;
  border-radius: 16rpx;
  overflow: hidden;
  margin-bottom: 16rpx;
  position: relative;
}

.product-small-image image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-small-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.product-small-name {
  font-size: 28rpx;
  color: #0A0D05;
  font-weight: 400;
  line-height: 1.4;
  margin-bottom: 12rpx;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-small-price {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.small-cash-price {
  font-size: 28rpx;
  font-weight: 700;
  color: #000000;
  line-height: 1.2;
}



.sold-out-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.sold-out-text {
  color: white;
  font-size: 24rpx;
}

/* 积分领券区域样式 */
.coupon-section {
  position: relative;
  z-index: 5;
  margin-bottom: 40rpx;
}



.empty-state {
  padding: 120rpx 0;
  color: #999999;
  text-align: center;
}

/* 优惠券列表样式 */
.coupon-list {
  padding: 20rpx 0;
}

.coupon-item {
  margin: 24rpx 0;
  position: relative;
}

.coupon-content {
  position: relative;
  width: 100%;
  height: 184rpx;
}

.coupon-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.coupon-info {
  position: relative;
  z-index: 2;
  display: flex;
  height: 100%;
  padding: 24rpx;
}

.coupon-left {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding-right: 24rpx;
}

.coupon-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 12rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.coupon-rule {
  font-size: 24rpx;
  color: #666;
  margin-bottom: 8rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.coupon-validity {
  font-size: 22rpx;
  color: #999;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.coupon-right {
  width: 70rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  padding-left: 50rpx;
}

.status-area {
  writing-mode: vertical-lr;
  text-orientation: upright;
}

.receive-text {
  color: #000000;
  font-size: 28rpx;
  font-weight: 600;
  cursor: pointer;
}

.receive-text.receive-disabled {
  color: #ccc;
  cursor: not-allowed;
}

.receive-text:not(.receive-disabled):active {
  opacity: 0.6;
}

/* 加载更多样式 */
.load-more {
  text-align: center;
  padding: 40rpx;
  color: #999999;
  font-size: 28rpx;
}

/* 弹窗样式 */
.custom-popup-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.custom-popup {
  background-color: #FFFFFF;
  border-radius: 16rpx;
  overflow: hidden;
  position: relative;
  max-height: 80vh;
  overflow-y: auto;
}

.bottom-mode {
  width: 100%;
  position: fixed;
  bottom: 0;
  left: 0;
  border-radius: 16rpx 16rpx 0 0;
}

.center-mode {
  width: 90vw;
  max-width: 600rpx;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 40rpx 40rpx 20rpx;
  border-bottom: 1rpx solid #F5F5F5;
}

.modal-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #000000;
}

.close-btn {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #F5F5F5;
  border-radius: 50%;
  font-size: 40rpx;
  color: #999999;
  line-height: 1;
}

.modal-content {
  padding: 40rpx;
}

.product-row {
  display: flex;
  align-items: center;
  margin-bottom: 24rpx;
}

.row-label {
  font-size: 28rpx;
  color: #000000;
  margin-right: 16rpx;
  min-width: 120rpx;
}

.address-info {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx;
  background: #FFFFFF;
  border: 2rpx solid rgba(0, 0, 0, 0.03);
  border-radius: 8rpx;
  transition: all 0.3s ease;
}

.address-info:active {
  background: #F5F5F5;
  transform: scale(0.98);
}

.address-content {
  flex: 1;
  margin-right: 16rpx;
}

.address-main {
  display: flex;
  align-items: center;
  margin-bottom: 8rpx;
}

.address-name {
  font-size: 28rpx;
  font-weight: 500;
  color: #000000;
  margin-right: 16rpx;
}

.address-phone {
  font-size: 24rpx;
  color: #999999;
}

.address-detail {
  font-size: 24rpx;
  color: #999999;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.placeholder {
  flex: 1;
  color: #CCCCCC;
  font-size: 28rpx;
}

.arrow-right {
  font-size: 20rpx;
  color: #999999;
}

.total-row {
  border-top: 1rpx solid #F5F5F5;
  padding-top: 24rpx;
  margin-top: 24rpx;
  flex-direction: column;
  align-items: stretch;
}

.total-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16rpx;
  font-size: 28rpx;
}

.total-cash {
  color: #647D00;
  font-weight: 700;
}

.total-points {
  color: #647D00;
  font-weight: 700;
}

.modal-footer {
  padding: 20rpx 40rpx 40rpx;
  display: flex;
  gap: 24rpx;
}

.btn-plain {
  flex: 1;
  height: 88rpx;
  background-color: #F5F5F5;
  border: none;
  border-radius: 44rpx;
  font-size: 32rpx;
  color: #999999;
  font-weight: 500;
}

.btn-primary {
  flex: 1;
  height: 88rpx;
  background-color: #A9FF00;
  border: none;
  border-radius: 44rpx;
  font-size: 32rpx;
  color: #0A0D05;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-primary:disabled {
  background-color: #CCCCCC;
  color: #FFFFFF;
}

.btn-loading {
  width: 24rpx;
  height: 24rpx;
  border: 2rpx solid #A9FF00;
  border-top-color: transparent;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-right: 8rpx;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(360deg);
  }
}

/* 优惠券弹窗特殊样式 */
.coupon-modal-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.coupon-preview-block {
  width: 100%;
  text-align: center;
  margin-bottom: 24rpx;
}

.coupon-preview-block .coupon-name {
  display: block;
  font-size: 32rpx;
  font-weight: 700;
  color: #000000;
  margin-bottom: 8rpx;
}

.coupon-preview-block .coupon-desc {
  display: block;
  font-size: 28rpx;
  color: #999999;
}

.exchange-info-block {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.exchange-label {
  font-size: 28rpx;
  color: #999999;
  margin-bottom: 8rpx;
}

.points-required {
  font-size: 48rpx;
  font-weight: 700;
  color: #647D00;
}
</style>