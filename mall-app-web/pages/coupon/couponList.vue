<template>
  <view class="content">
    <!-- 优惠券详情模式 -->
    <view v-if="isDetailMode" class="coupon-detail-container">

      <view v-if="couponDetail" class="coupon-detail-card">
        <!-- 优惠券背景 -->
        <image
          class="detail-coupon-bg"
          src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/order/coupon_background.png"
          mode="scaleToFill"
        />

        <!-- 优惠券内容 -->
        <view class="detail-coupon-info">
          <view class="detail-coupon-left">
            <view class="detail-coupon-amount-container">
              <view class="detail-coupon-amount-number">{{ getDetailDisplayAmount(couponDetail) }}</view>
              <view class="detail-coupon-rmb">{{ getDetailDisplayUnit(couponDetail) }}</view>
            </view>
          </view>
          <view class="detail-coupon-right">
            <view class="detail-coupon-title">{{ couponDetail.name }}</view>
            <view class="detail-coupon-rule">{{ getDetailRuleText(couponDetail) }}</view>
            <view class="detail-coupon-time">使用时间：{{ formatTime(couponDetail.startTime) }} - {{ formatTime(couponDetail.endTime) }}</view>
          </view>
        </view>
      </view>

      <!-- 优惠券详细信息 -->
      <view v-if="couponDetail" class="coupon-info-section">
        <view class="info-item">
          <text class="info-label">优惠券名称：</text>
          <text class="info-value">{{ couponDetail.name }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">优惠信息：</text>
          <text class="info-value">{{ getDetailInfoText(couponDetail) }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">使用条件：</text>
          <text class="info-value">{{ getDetailRuleText(couponDetail) }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">有效期：</text>
          <text class="info-value">{{ formatTime(couponDetail.startTime) }} 至 {{ formatTime(couponDetail.endTime) }}</text>
        </view>
        <view class="info-item" v-if="couponDetail.note">
          <text class="info-label">使用说明：</text>
          <text class="info-value">{{ couponDetail.note }}</text>
        </view>
      </view>

      <!-- 领取按钮 -->
      <view class="receive-button-container">
        <button
          class="receive-button"
          :class="{ 'button-disabled': !canReceive }"
          :disabled="!canReceive"
          @tap="receiveCoupon"
          :loading="receiving"
        >
          {{ getReceiveButtonText() }}
        </button>
      </view>

      <!-- 加载状态 -->
      <view v-if="detailLoading" class="loading-container">
        <text>加载中...</text>
      </view>

      <!-- 错误状态 -->
      <view v-if="detailError" class="error-container">
        <text>{{ detailError }}</text>
        <button class="retry-button" @tap="loadCouponDetail">重试</button>
      </view>
    </view>

    <!-- 优惠券列表模式 -->
    <view v-else>
      <view class="navbar">
        <view
          v-for="(item, index) in tabs"
          :key="index"
          class="nav-item"
          :class="{ current: currentTab === index }"
          @tap="switchTab(index)"
        >
          {{ item.title }}
        </view>
      </view>
      <view class="coupon-list">
      <view
        v-for="(item, index) in couponList"
        :key="index"
        class="coupon-item"
        :class="{
          'coupon-used': item.status === 1,
          'coupon-expired': item.status === 2
        }"
      >
        <view class="coupon-content">
          <!-- 优惠券背景 -->
          <image
            class="coupon-bg"
            src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/order/coupon_background.png"
            mode="scaleToFill"
          />

          <!-- 优惠券内容 -->
          <view class="coupon-info">
            <!-- 左侧内容区域 -->
            <view class="coupon-left">
              <view class="coupon-title">{{ item.name }} - {{ getCouponDisplayText(item) }}</view>
              <view class="coupon-rule">规则：{{ getRuleText(item) }}</view>
              <view class="coupon-validity">有效期：{{ formatTime(item.startTime) }} - {{ formatTime(item.endTime) }}</view>
            </view>

            <!-- 右侧状态区域 -->
            <view class="coupon-right">
              <view class="status-area">
                <!-- 待领取状态显示领取按钮 -->
                <view
                  v-if="tabs[currentTab].status === -1"
                  class="receive-text"
                  :class="{ 'receive-disabled': !canReceiveCoupon(item) }"
                  @tap="receiveCouponFromList(item)"
                >
                  {{ getReceiveButtonText(item) }}
                </view>
                <!-- 其他状态显示状态文字 -->
                <view
                  v-else
                  class="status-text"
                  :class="{
                    'status-available': tabs[currentTab].status === 0,
                    'status-used': tabs[currentTab].status === 1,
                    'status-expired': tabs[currentTab].status === 2
                  }"
                  @tap="tabs[currentTab].status === 0 ? useCoupon(item) : null"
                >
                  {{ getStatusTextByTab() }}
                </view>
              </view>
            </view>
          </view>
        </view>
      </view>
        <view class="empty" v-if="couponList.length === 0">
          <text>暂无优惠券</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import uniLoadMore from '@/components/uni-load-more/uni-load-more.vue';
import { fetchMemberCouponList, fetchCouponDetail, addMemberCoupon, fetchCouponList } from '@/api/coupon.js';

export default {
  components: {
    uniLoadMore
  },
  data() {
    return {
      // 列表模式数据
      currentTab: 0,
      tabs: [
        { title: '可领取', status: -1 },
        { title: '未使用', status: 0 },
        { title: '已使用', status: 1 },
        { title: '已过期', status: 2 }
      ],
      couponList: [],
      loadingType: 'more',
      page: 1,
      pageSize: 10,
      totalPage: 1,
      hasLogin: false,
      isFirstLoad: true, // 标记是否首次加载

      // 详情模式数据
      isDetailMode: false,
      couponId: null,
      couponDetail: null,
      detailLoading: false,
      detailError: null,
      receiving: false,
      canReceive: false,
      hasReceived: false // 是否已经领取过该优惠券
    };
  },
  
  computed: {
    emptyText() {
      const textMap = {
        '-1': '暂无可领取优惠券',
        0: '暂无可用优惠券',
        1: '已使用的优惠券为空',
        2: '暂无过期优惠券'
      };
      return textMap[this.tabs[this.currentTab].status] || '暂无相关数据';
    }
  },
  
  onLoad(options) {
    // 首先加载学校信息
    this.loadSchoolInfo();

    let couponId = null;

    // 1. 检查页面参数（直接传递的参数，包括短链接参数）
    if (options && options.couponId) {
      couponId = options.couponId;
      console.log('从页面参数获取优惠券ID:', couponId);
    }

    // 2. 检查小程序码参数（scene参数）
    if (!couponId && options && options.scene) {
      console.log('检测到小程序码参数:', options.scene);
      try {
        // 解析scene参数，格式可能是 "couponId=123" 或经过URL编码的
        const scene = decodeURIComponent(options.scene);
        console.log('解码后的scene参数:', scene);

        // 解析键值对，支持多种格式
        if (scene.includes('couponId=')) {
          const match = scene.match(/couponId=(\d+)/);
          if (match) {
            couponId = match[1];
            console.log('从scene参数解析出优惠券ID:', couponId);
          }
        }
      } catch (error) {
        console.error('解析scene参数失败:', error);
      }
    }

    // 如果有couponId参数，进入详情模式
    if (couponId) {
      this.isDetailMode = true;
      this.couponId = couponId;
      this.loadCouponDetail();
    } else {
      // 检查登录状态
      this.checkLoginStatus();

      // 待领取选项卡无论是否登录都要显示数据，其他选项卡需要登录
      const currentTabStatus = this.tabs[this.currentTab].status;

      if (currentTabStatus === -1 || this.hasLogin) {
        this.loadCouponList();
      }
    }
  },
  
  onShow() {
    // 检查登录状态
    this.checkLoginStatus();

    // 如果不是首次加载，才在onShow中加载数据
    if (!this.isFirstLoad) {
      const currentTabStatus = this.tabs[this.currentTab].status;
      if (currentTabStatus === -1 || this.hasLogin) {
        this.loadCouponList();
      }
    }
    this.isFirstLoad = false;
  },
  
  onPullDownRefresh() {
    this.page = 1;
    this.loadCouponList();
    setTimeout(() => {
      uni.stopPullDownRefresh();
    }, 500);
  },
  
  onReachBottom() {
    if (this.loadingType === 'nomore') return;
    this.page++;
    this.loadCouponList();
  },
  
  methods: {
    // 加载学校信息
    loadSchoolInfo() {
      try {
        // 从本地存储获取学校信息
        const schoolInfo = uni.getStorageSync('selectedSchool');
        if (schoolInfo) {
          const school = JSON.parse(schoolInfo);
          // 同步更新Vuex状态
          this.$store.commit('setSelectedSchool', school);
          console.log('优惠券页面加载学校信息:', school);
        } else {
          console.log('优惠券页面未找到学校信息');
          // 清空Vuex状态
          this.$store.commit('setSelectedSchool', null);
        }
      } catch (error) {
        console.error('加载学校信息失败:', error);
        // 清空Vuex状态
        this.$store.commit('setSelectedSchool', null);
      }
    },

    // 检查登录状态
    checkLoginStatus() {
      // 使用统一的tokenInfo检查登录状态
      const tokenInfo = uni.getStorageSync('tokenInfo');
      const userInfo = uni.getStorageSync('userInfo');

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

      // 更严格的登录状态检查
      this.hasLogin = !!(hasValidToken && userInfo && userInfo.id);
    },
    
    // 切换选项卡
    switchTab(index) {
      if (this.currentTab === index) return;
      this.currentTab = index;
      this.page = 1;
      this.couponList = [];
      this.loadCouponList();
    },
    
    // 加载优惠券列表
    async loadCouponList() {
      this.loadingType = 'loading';

      try {
        // 获取当前选项卡对应的优惠券状态
        const useStatus = this.tabs[this.currentTab].status;

        // 获取当前选择的学校ID
        const selectedSchool = this.$store.state.selectedSchool;
        const schoolId = selectedSchool && selectedSchool.id ? selectedSchool.id : undefined;

        console.log('优惠券列表加载 - 当前学校信息:', selectedSchool);
        console.log('优惠券列表加载 - 学校ID:', schoolId);

        let result;
        if (useStatus === -1) {
          // 可领取优惠券，调用不同的接口
          result = await fetchCouponList(null, schoolId);
        } else {
          // 已领取的优惠券
          result = await fetchMemberCouponList(useStatus, schoolId);
        }

        if (result && result.data) {
          // 处理数据
          const newCouponList = result.data;

          // 更新优惠券列表
          if (this.page === 1) {
            this.couponList = newCouponList;
          } else {
            this.couponList = [...this.couponList, ...newCouponList];
          }

          // 更新加载状态
          this.loadingType = newCouponList.length < this.pageSize ? 'nomore' : 'more';
        } else {
          this.loadingType = 'nomore';
        }
      } catch (error) {
        console.error('获取优惠券列表失败:', error);
        uni.showToast({
          title: '获取优惠券列表失败',
          icon: 'none'
        });
        this.loadingType = 'more';
      }
    },
    
    // 使用优惠券
    useCoupon(coupon) {

      // 跳转到分类页面
      uni.switchTab({
        url: '/pages/category/category'
      });
    },
    
    // 格式化时间
    formatTime(date) {
      if (!date) return '';
      if (typeof date === 'string') {
        date = new Date(date);
      }

      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');

      return `${year}-${month}-${day}`;
    },

    // 获取状态文字
    getStatusText(status) {
      const statusMap = {
        0: '去使用',
        1: '已使用',
        2: '已过期'
      };
      return statusMap[status] || '去使用';
    },

    // 根据当前选项卡获取状态文字
    getStatusTextByTab() {
      const currentTabStatus = this.tabs[this.currentTab].status;
      const statusMap = {
        0: '去使用',    // 未使用
        1: '已使用',    // 已使用
        2: '已过期'     // 已过期
      };
      return statusMap[currentTabStatus] || '去使用';
    },

    // 获取优惠券显示文本（列表用）
    getCouponDisplayText(item) {
      if (item.couponType === 1) {
        // 打折券：显示折扣比例
        if (item.discountRate) {
          return `${(item.discountRate * 10).toFixed(1)}折`;
        }
        return '折扣券';
      } else {
        // 满减券：显示金额
        return `${item.amount || 0}元`;
      }
    },

    // 获取规则文字
    getRuleText(item) {
      if (item.minPoint && item.minPoint > 0) {
        return `满${item.minPoint}可用`;
      }

      // 根据useType判断使用范围
      if (item.useType === 0) {
        return '全场通用';
      } else if (item.useType === 1) {
        return '指定商品可用';
      } else {
        return '全场通用';
      }
    },

    // 详情模式相关方法
    async loadCouponDetail() {
      if (!this.couponId) return;

      this.detailLoading = true;
      this.detailError = null;

      try {
        const result = await fetchCouponDetail(this.couponId);
        if (result && result.data) {
          this.couponDetail = result.data;
          this.checkCanReceive();
        } else {
          this.detailError = '优惠券不存在或已下架';
        }
      } catch (error) {
        console.error('获取优惠券详情失败:', error);
        this.detailError = '获取优惠券详情失败，请稍后重试';
      } finally {
        this.detailLoading = false;
      }
    },

    checkCanReceive() {
      if (!this.couponDetail) {
        this.canReceive = false;
        return;
      }

      // 如果已经领取过，不能再领取
      if (this.hasReceived) {
        this.canReceive = false;
        return;
      }

      const now = new Date();
      const startTime = new Date(this.couponDetail.startTime);
      const endTime = new Date(this.couponDetail.endTime);
      const enableTime = new Date(this.couponDetail.enableTime);

      // 检查是否在有效期内
      if (now < enableTime || now > endTime) {
        this.canReceive = false;
        return;
      }

      // 检查是否还有库存
      if (this.couponDetail.publishCount && this.couponDetail.receiveCount) {
        if (this.couponDetail.receiveCount >= this.couponDetail.publishCount) {
          this.canReceive = false;
          return;
        }
      }

      this.canReceive = true;
    },

    async receiveCoupon() {
      if (!this.canReceive || this.receiving) return;

      // 检查登录状态 - 使用统一的tokenInfo检查方式
      const tokenInfo = uni.getStorageSync('tokenInfo');
      const userInfo = uni.getStorageSync('userInfo');

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

      // 更严格的登录状态检查
      const isLoggedIn = !!(hasValidToken && userInfo && userInfo.id);

      if (!isLoggedIn) {
        uni.showModal({
          title: '提示',
          content: '请先登录后再领取优惠券',
          confirmText: '去登录',
          success: (res) => {
            if (res.confirm) {
              uni.navigateTo({
                url: '/pages/public/register'
              });
            }
          }
        });
        return;
      }

      this.receiving = true;

      try {
        const result = await addMemberCoupon(this.couponId);

        if (result && result.code === 200) {
          const data = result.data || {};

          if (data.received === true) {
            // 领取成功
            uni.showToast({
              title: '领取成功',
              icon: 'success'
            });

            // 更新按钮状态
            this.hasReceived = true;

            // 领取成功后跳转到优惠券列表
            setTimeout(() => {
              uni.redirectTo({
                url: '/pages/coupon/couponList'
              });
            }, 1500);
          } else {
            // 领取失败，显示具体的错误信息
            if (data.alreadyReceived === true) {
              // 已经领取过的情况
              this.hasReceived = true;
              uni.showToast({
                title: '您已经领取过该优惠券',
                icon: 'none',
                duration: 3000
              });
            } else {
              // 其他错误情况
              uni.showToast({
                title: data.message || result.message || '领取失败',
                icon: 'none',
                duration: 3000
              });
            }
          }
        } else {
          uni.showToast({
            title: result.message || '领取失败',
            icon: 'none',
            duration: 3000
          });
        }
      } catch (error) {
        console.error('领取优惠券失败:', error);
        uni.showToast({
          title: '领取失败，请稍后重试',
          icon: 'none'
        });
      } finally {
        this.receiving = false;
      }
    },

    getReceiveButtonText() {
      if (this.receiving) {
        return '领取中...';
      }

      if (!this.couponDetail) {
        return '加载中...';
      }

      // 如果已经领取过，显示已领取
      if (this.hasReceived) {
        return '已领取';
      }

      const now = new Date();
      const enableTime = new Date(this.couponDetail.enableTime);
      const endTime = new Date(this.couponDetail.endTime);

      if (now < enableTime) {
        return '未开始';
      }

      if (now > endTime) {
        return '已过期';
      }

      if (this.couponDetail.publishCount && this.couponDetail.receiveCount) {
        if (this.couponDetail.receiveCount >= this.couponDetail.publishCount) {
          return '已领完';
        }
      }

      if (!this.hasLogin) {
        return '登录后领取';
      }

      return '立即领取';
    },

    // 获取详情页面显示金额
    getDetailDisplayAmount(item) {
      if (!item) return '';

      if (item.couponType === 1) {
        // 打折券：显示折扣比例数字
        if (item.discountRate) {
          return (item.discountRate * 10).toFixed(1);
        }
        return '折扣';
      } else {
        // 满减券：显示金额
        return item.amount || 0;
      }
    },

    // 获取详情页面显示单位
    getDetailDisplayUnit(item) {
      if (!item) return '';

      if (item.couponType === 1) {
        // 打折券：显示"折"
        return '折';
      } else {
        // 满减券：显示"RMB"
        return 'RMB';
      }
    },

    // 获取详情页面优惠信息文本
    getDetailInfoText(item) {
      if (!item) return '';

      if (item.couponType === 1) {
        // 打折券：显示折扣信息
        if (item.discountRate) {
          return `${(item.discountRate * 10).toFixed(1)}折优惠`;
        }
        return '折扣优惠';
      } else {
        // 满减券：显示金额
        return `￥${item.amount || 0}`;
      }
    },

    getDetailRuleText(item) {
      if (!item) return '';

      if (item.minPoint && item.minPoint > 0) {
        if (item.couponType === 1) {
          return `满${item.minPoint}享折扣`;
        } else {
          return `满${item.minPoint}立减`;
        }
      }

      if (item.useType === 0) {
        return '全场通用';
      } else if (item.useType === 1) {
        return '指定商品可用';
      } else if (item.useType === 2) {
        return '指定分类可用';
      } else {
        return '全场通用';
      }
    },

    formatTime(timeStr) {
      if (!timeStr) return '';
      const date = new Date(timeStr);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}/${month}/${day}`;
    },

    // 判断优惠券是否可以领取
    canReceiveCoupon(item) {
      // 后端已经过滤了不可领取的优惠券，这里只需要检查登录状态
      return this.hasLogin;
    },

    // 获取领取按钮文字
    getReceiveButtonText(item) {
      // 后端已经过滤了不可领取的优惠券，这里只需要根据登录状态显示文字
      if (!this.hasLogin) {
        return '登录领取';
      }
      return '立即领取';
    },

    // 从列表中领取优惠券
    async receiveCouponFromList(item) {
      // 检查登录状态
      if (!this.hasLogin) {
        uni.showModal({
          title: '提示',
          content: '请先登录后再领取优惠券',
          confirmText: '去登录',
          success: (res) => {
            if (res.confirm) {
              uni.navigateTo({
                url: '/pages/public/register'
              });
            }
          }
        });
        return;
      }

      try {
        const result = await addMemberCoupon(item.id);

        if (result && result.code === 200) {
          uni.showToast({
            title: '领取成功',
            icon: 'success'
          });

          // 重新加载列表
          this.page = 1;
          this.loadCouponList();
        } else {
          uni.showToast({
            title: result.message || '领取失败',
            icon: 'none'
          });
        }
      } catch (error) {
        console.error('领取优惠券失败:', error);
        uni.showToast({
          title: '领取失败，请稍后重试',
          icon: 'none'
        });
      }
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
  padding-bottom: 30rpx;
}

.navbar {
  display: flex;
  height: 40px;
  padding: 0 15px;
  background: #fff;
  box-shadow: 0 1px 5px rgba(0, 0, 0, 0.06);
  position: relative;
  z-index: 10;
  
  .nav-item {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100%;
    font-size: 14px;
    color: #333333;
    position: relative;
    opacity: 0.4;
    
    &.current {
      color: #000;
      opacity: 1;
      font-weight: 500;
      
      &:after {
        content: "";
        position: absolute;
        left: 50%;
        bottom: 0;
        transform: translateX(-50%);
        width: 44px;
        height: 0;
        border-bottom: 2px solid #000;
      }
    }
  }
}

/* 优惠券列表 */
.coupon-list {
  padding: 20rpx 0;
}

.coupon-item {
  margin: 24rpx 32rpx;
  position: relative;

  .coupon-content {
    position: relative;
    width: 100%;
    height: 184rpx; // 343*92px 的比例

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
      padding-left: 50rpx; // 向右移动10rpx
    }

    .status-area {
      writing-mode: vertical-lr;
      text-orientation: upright;
    }

    .status-text {
      font-size: 28rpx;
      font-weight: 600;
      color: #333; // 默认黑色

      &.status-available {
        color: #333; // 未使用：黑色
        cursor: pointer; // 添加手型光标
        transition: opacity 0.2s; // 添加过渡效果

        &:active {
          opacity: 0.6; // 点击时的透明度效果
        }
      }

      &.status-used {
        color: #999; // 已使用：灰色
      }

      &.status-expired {
        color: #999; // 已过期：灰色
      }
    }

    .receive-text {
      color: #ff6b6b !important; // 强制红色，覆盖其他样式
      font-size: 28rpx;
      font-weight: 600;
      cursor: pointer;

      &.receive-disabled {
        color: #ccc !important;
        cursor: not-allowed;
      }

      &:not(.receive-disabled):active {
        opacity: 0.7;
      }
    }
  }
}



.empty {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200rpx;
  color: #909399;
  font-size: 28rpx;
}

/* 详情模式样式 */
.coupon-detail-container {
  padding: 32rpx;
  background: #f8f8f8;
  min-height: 100vh;
}

.detail-header {
  text-align: center;
  margin-bottom: 40rpx;
}

.detail-title {
  font-size: 36rpx;
  font-weight: 600;
  color: #282921; /* 主题背景色作为标题颜色 */
}

.coupon-detail-card {
  position: relative;
  width: 686rpx; /* 343px * 2 */
  height: 184rpx; /* 92px * 2 */
  margin: 0 auto 40rpx auto;

  .detail-coupon-bg {
    position: absolute;
    top: 0;
    left: 0;
    width: 686rpx; /* 保持原始尺寸比例 343px */
    height: 184rpx; /* 保持原始尺寸比例 92px */
    z-index: 1;
  }

  .detail-coupon-info {
    position: relative;
    z-index: 2;
    display: flex;
    height: 100%;
    padding: 24rpx 40rpx;
  }

  .detail-coupon-left {
    width: 200rpx;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
  }

  .detail-coupon-amount-container {
    display: flex;
    flex-direction: row;
    align-items: baseline;
    justify-content: center;
  }

  .detail-coupon-amount-number {
    font-size: 80rpx;
    font-weight: 700;
    color: #282921; /* 主题背景色作为金额颜色 */
    line-height: 1;
  }

  .detail-coupon-rmb {
    font-size: 24rpx;
    color: #282921; /* 主题背景色作为金额颜色 */
    margin-left: 8rpx;
  }

  .detail-coupon-right {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
    padding-left: 20rpx;
  }

  .detail-coupon-title {
    font-size: 32rpx;
    font-weight: 600;
    color: #282921; /* 主题背景色作为标题颜色 */
    margin-bottom: 8rpx;
  }

  .detail-coupon-rule {
    font-size: 24rpx;
    color: #282921; /* 主题背景色作为文字颜色 */
    margin-bottom: 8rpx;
  }

  .detail-coupon-time {
    font-size: 20rpx;
    color: #666;
  }
}

.coupon-info-section {
  background: #fff;
  border-radius: 16rpx;
  padding: 32rpx;
  margin-bottom: 40rpx;

  .info-item {
    display: flex;
    margin-bottom: 24rpx;

    &:last-child {
      margin-bottom: 0;
    }
  }

  .info-label {
    font-size: 28rpx;
    color: #666;
    width: 200rpx; /* 增大宽度 */
    flex-shrink: 0;
  }

  .info-value {
    font-size: 28rpx;
    color: #282921; /* 主题背景色作为文字颜色 */
    flex: 1;
    word-break: break-all;
  }
}

.receive-button-container {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 32rpx;
  background: #fff;
  border-top: 1rpx solid #eee;
}

.receive-button {
  width: 100%;
  height: 88rpx;
  background: #282921; /* 主题背景色 */
  color: #A9FF00; /* 主题文字颜色 */
  font-size: 32rpx;
  font-weight: 600;
  border-radius: 44rpx;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;

  &.button-disabled {
    background: #ccc;
    color: #999;
  }
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200rpx;
  color: #666;
  font-size: 28rpx;
}

.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200rpx;
  color: #999;
  font-size: 28rpx;

  .retry-button {
    margin-top: 20rpx;
    padding: 16rpx 32rpx;
    background: #282921; /* 主题背景色 */
    color: #A9FF00; /* 主题文字颜色 */
    font-size: 24rpx;
    border-radius: 20rpx;
    border: none;
  }
}
</style> 