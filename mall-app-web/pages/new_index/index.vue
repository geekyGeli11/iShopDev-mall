<template>
  <view class="home-page">
    <!-- ✅ 骨架屏：数据加载时显示 -->
    <skeleton-screen :visible="!dataLoaded" type="home" />

    <!-- 主要内容：数据加载完成后显示 -->
    <view v-show="dataLoaded" class="main-container">
    <!-- 顶部区域容器（包含导航栏、搜索框、轮播图的统一背景图） -->
    <view class="top-section">
      <!-- 顶部背景图片 -->
      <image class="top-bg-image" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/top_backgroundImage.png" mode="scaleToFill" />
      <!-- 自定义导航栏 -->
      <nav-bar :placeholder="true" :bg-color="navBarBgColor" :has-slot="true" :titleCenter="true" :style="{ boxShadow: navBarBoxShadow }">
        <view class="nav-bar-content">
          <!-- 左侧Logo -->
          <view class="nav-left">
            <image class="nav-logo" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/guanghengzou_logo.png" mode="aspectFit" />
          </view>
          <!-- 中间标题 -->
          <view class="nav-center">
            <image class="nav-title" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/guanghengzou_title.png" mode="aspectFit" />
        </view>
      </view>
    </nav-bar>

      <!-- 搜索栏区域 -->
      <view class="search-section">
        <!-- 学校选择 -->
        <view class="store-selector" @tap="handleStoreSelect">
          <view class="store-avatar">
            <!-- ✅ 优化：使用缩略图 + 懒加载 -->
            <image
              class="store-logo"
              :src="getSchoolThumbnail(getSchoolDisplayImage(selectedSchool))"
              mode="aspectFill"
              :lazy-load="true"
              @error="handleStoreLogoError"
            />
          </view>
          <view class="store-info">
            <text class="store-name" :class="{ 'auto-selecting': isAutoSelectingSchool }">
              {{ isAutoSelectingSchool ? '正在为您选择最近学校...' : (selectedSchool ? selectedSchool.schoolName : '选择学校') }}
            </text>
          </view>
          <image class="arrow-down" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/arrow_down.png" mode="aspectFit" />
        </view>

        <!-- 搜索区域 -->
        <view class="search-area" @tap="handleSearch">
          <image class="search-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/search_icon.svg" mode="aspectFit" />
          <text class="search-placeholder">搜索定制产品</text>
          <view class="search-btn">
            <text class="search-btn-text">搜索</text>
        </view>
      </view>
    </view>

      <!-- Banner轮播图 -->
      <view class="banner-container">
        <swiper class="banner" :indicator-dots="true" :autoplay="true" :interval="3000" :duration="500" indicator-color="rgba(255,255,255,0.4)" indicator-active-color="#fff" circular>
          <swiper-item v-for="(item,index) in advertiseList" :key="index" @tap="handleBannerClick(item)">
            <!-- ✅ 优化：使用缩略图 + 懒加载 -->
            <image
              :src="getBannerThumbnail(item.pic || 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/banner1.jpg')"
              class="banner-img"
              mode="aspectFill"
              :lazy-load="true"
            />
            </swiper-item>
          </swiper>
      </view>
    </view>

    <!-- 秒杀/邀请/积分区域 -->
    <view class="flash-invite-section">
      <!-- 左侧秒杀区域 -->
      <view class="flash-section" @tap="handleFlashSection">
        <!-- 秒杀区域背景图 -->
        <image class="flash-bg" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/hot-product.png" mode="scaleToFill" />
        <view class="flash-content">
          <!-- 秒杀商品网格 -->
          <view class="flash-products">
            <view
              v-for="(item, index) in hotProductList.slice(0, 4)"
              :key="index"
              class="flash-product-item"
              @tap.stop="handleFlashSaleItemClick(item)"
            >
              <image :src="item.cover || item.pic" class="flash-product-image" mode="aspectFill" />
              <view class="flash-price-tag">
                <text class="flash-price-text">￥{{ item.price }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 右侧区域 -->
      <view class="right-section">
        <!-- 邀请收益卡片 -->
        <view class="invite-section" @tap="handleInviteSection">
          <image class="invite-card" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/invite-income.png" mode="aspectFit" />
        </view>

        <!-- 积分商城卡片 -->
        <view class="point-section" @tap="handlePointSection">
          <image class="point-card" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/point-mall.png" mode="aspectFit" />
        </view>
      </view>
    </view>

    <!-- 特色卡片流 -->
    <card-flow
      :cards="flashSaleProducts"
      @select="handleProductDetail"
      @order="handleProductDetail"
    />

    <!-- 热榜推荐 -->
    <quick-grid
      :items="newProductList"
      @select="handleProductDetail"
      @more="handleMoreProducts"
    />

    <!-- 商品瀑布流 -->
    <product-waterfall
      :list="waterfallProductList"
      :loading="waterfallLoading"
      :has-more="waterfallHasMore"
      @select="handleToProduct"
      @load-more="loadMoreWaterfallProducts"
    />

    <!-- 购物车悬浮窗 -->
    <shopping-cart
      :cart-count="cartCount"
      icon-src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/shoping-cart.png"
      @click-cart="handleCartClick"
    />

    <!-- 登录弹窗 -->
    <login-popup
      :visible="shouldShowLoginPopup"
      @login="handleLoginSuccess"
      @close="handleLoginClose"
      @openPrivacy="openPrivacy"
      @openTerms="openTerms"
    />
    
    <!-- 隐私政策弹窗 -->
    <privacy-popup
      :visible="showPrivacyPopup"
      @close="closePrivacy"
    />

    <!-- 服务条款弹窗 -->
    <terms-popup
      :visible="showTermsPopup"
      @close="closeTerms"
    />




    <!-- 首次进入学校选择弹窗 -->
    <school-select-popup
      :visible="showSchoolSelectPopup"
      :imageUrl="schoolSelectPopupImageUrl"
      @confirm="handleSchoolSelectPopupConfirm"
    />

    <!-- 门店选择弹窗（修改为学校选择） -->
    <store-selector
      :visible="showStoreSelector"
      :current-store="selectedStore"
      :school-groups="schoolGroups"
      :selected-school-id="selectedSchool ? selectedSchool.id : null"
      @select="handleStoreSelected"
      @close="handleStoreSelectorClose"
    />

    <!-- 全屏加载组件 -->
    <!-- <full-screen-loading
      :visible="showFullScreenLoading"
      :text="loadingText"
    /> -->
    </view>
  </view>
</template>

<script>
import NavBar from '@/components/nav-bar.vue';
import LoginPopup from '@/components/login-popup.vue';
import PrivacyPopup from '@/components/PrivacyPopup.vue';
import TermsPopup from '@/components/TermsPopup.vue';
import SchoolSelectPopup from '@/components/home/SchoolSelectPopup.vue';
import { fetchContent, fetchProductCateList } from '@/api/home';
import { fetchNearbyStores, fetchDefaultStore, fetchStoreGroupsBySchool } from '@/api/store';
import { searchProductList } from '@/api/product';

import QuickGrid from '@/components/home/QuickGrid.vue';
import CardFlow from '@/components/home/CardFlow.vue';
import ProductWaterfall from '@/components/home/ProductWaterfall.vue';
import ShoppingCart from '@/components/shoppingCart.vue';
import StoreSelector from '@/components/store/StoreSelector.vue';
import SkeletonScreen from '@/components/SkeletonScreen.vue';
import cacheManager, { CACHE_KEYS, CACHE_EXPIRE_TIME } from '@/utils/cacheManager.js';
import { lazyLoadUtils } from '@/utils/lazyLoad.js';


export default {
  components: {
    'nav-bar': NavBar,
    'login-popup': LoginPopup,
    'privacy-popup': PrivacyPopup,
    'terms-popup': TermsPopup,
    'school-select-popup': SchoolSelectPopup,
    'quick-grid': QuickGrid,
    'card-flow': CardFlow,
    'product-waterfall': ProductWaterfall,
    'shopping-cart': ShoppingCart,
    'store-selector': StoreSelector,
    'skeleton-screen': SkeletonScreen
  },
  data() {
    return {
      statusBarHeight: 0,
      navBarHeight: 0,
      opacityvalue: 0,
      navBarBgColor: 'transparent', // 导航栏背景色
      navBarBoxShadow: 'none', // 导航栏阴影
      hasNewNotification: false,
      selectedSchool: null, // 选中的学校信息
    selectedStore: null, // 兼容性：保留selectedStore字段
    userLocation: null, // 用户地理位置
    isLocationPermissionGranted: false, // 是否获得定位权限
    isAutoSelectingSchool: false, // 是否正在自动选择学校
    showStoreSelector: false, // 兼容性：保留showStoreSelector字段
    showSchoolSelector: false, // 是否显示学校选择弹窗
    schoolGroups: [], // 门店分组数据（保留兼容性）
    schools: [], // 学校列表数据
      cartCount: 0, // 购物车商品数量
      advertiseList: [],
      brandList: [],
      categoryNames: ['时令水果', '海鲜产品', '特色美食', '伴手礼', '珠宝玉器', '伴手礼', '工艺品', '精美包装'],
      latestNotice: null,
      hotProductList: [],
      showFlashSale: false,
      hours: '00',
      minutes: '00',
      seconds: '00',
      flashSaleProducts: [],
      tabs: ['今日推荐', '本地活动', '精彩濠江'],
      selectedTab: 0,
      newProductList: [],
      waterfallProductList: [], // 瀑布流商品列表
      waterfallPage: 1, // 瀑布流分页页码
      waterfallPageSize: 10, // 瀑布流每页数量
      waterfallLoading: false, // 瀑布流加载状态
      waterfallHasMore: true, // 是否还有更多数据
      filteredProducts: [],
      localEventsList: [],
      localGoodsList: [],
      haojianFeatures: [
        {
          title: '两标两码',
          description: '企业标准&平台标准品质保障',
          icon: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/icon1.png'
        },
        {
          title: '政府指导',
          description: '汕头市濠江区&深证市南山区政府指导',
          icon: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/icon2.png'
        },
        {
          title: '官方认证',
          description: '濠江区供销社&市场监督局品质认证',
          icon: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/icon3.png'
        }
      ],
      wonderfulMacauList: [],
      showPrivacyPopup: false,
      showTermsPopup: false,
      showSchoolSelectPopup: false, // 首次进入学校选择弹窗的显示状态
      schoolSelectPopupImageUrl: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/preload.png', // 弹窗图片地址
      timer: null,
      pageActive: true,  // 添加页面活跃状态标记
      dataLoaded: false, // 数据是否已加载完成
      userInitiatedPlay: {}, // 跟踪用户主动播放状态
      videoContexts: {}, // 存储视频上下文
      currentPlayingVideo: null, // 当前播放的视频信息
      activeVideoId: null, // 当前正在播放的视频ID
      showVideoPopup: false, // 是否显示视频弹窗
      popupVideoUrl: '', // 弹窗中播放的视频URL
      isPopupVideoFullscreen: false, // 弹窗视频是否全屏
      isClosingPopup: false, // 是否正在关闭弹窗
      userExitedFullscreen: false, // 用户是否主动退出全屏

      // 性能优化相关
      lastSchoolChangeCheck: 0, // 上次检查学校变化的时间戳
      schoolChangeDebounceTime: 500, // 学校变化检查防抖时间（毫秒）
      storeGroupsLoaded: false, // 门店分组是否已加载
      dataLoaded: false, // 数据是否已加载（用于控制骨架屏显示）
    };
  },
  computed: {
    // 从 Vuex 获取登录弹窗状态
    shouldShowLoginPopup() {
      return this.$store.state.shouldShowLoginPopup;
    },
    // 筛选精彩濠江列表，分成两部分
    wonderfulMacauFirstGroup() {
      return this.wonderfulMacauList.slice(0, 2);
    },
    wonderfulMacauSecondGroup() {
      return this.wonderfulMacauList.slice(3, 5);
    }
  },
  async onLoad() {
    // 获取系统信息（同步，快速执行）
    const systemInfo = uni.getSystemInfoSync();
    this.statusBarHeight = systemInfo.statusBarHeight;
    // 在小程序中，导航栏高度通常是44px
    this.navBarHeight = this.statusBarHeight + 44;

    // 获取已选择的学校信息（同步，快速执行）
    this.loadSelectedStore();

    // 初始化Vuex中的学校状态（在loadSelectedStore之后）
    this.$store.commit('setSelectedSchool', this.selectedSchool);

    // ✅ 检查是否首次进入小程序，显示学校选择弹窗
    this.checkAndShowSchoolSelectPopup();

    // 设置页面为活跃状态，允许页面立即渲染
    this.pageActive = true;

    // 立即渲染页面结构，然后异步加载数据
    this.$nextTick(() => {
      // 分批异步加载数据，避免阻塞渲染
      this.loadDataInBatches();
    });
  },
  onShow() {
    // 检查是否需要显示登录弹窗
    this.checkAndShowLoginPopup();

    // 重新加载门店信息（从门店选择页面返回时可能有更新）
    this.loadSelectedStore();

    // ✅ 优化：使用防抖机制检测学校变化
    this.checkSchoolChangeDebounced();

    this.pageActive = true;

    // 重置所有视频的播放状态
    this.userInitiatedPlay = {};

    // 重新初始化视频上下文
    this.initVideoContexts();

    // 确保视频不会自动播放和全屏
    // 使用两次延时，确保在UI渲染和微信小程序内部处理之后执行
    setTimeout(() => {
      this.stopAllVideos();

      // 再次延迟执行，确保在微信小程序完成所有内部状态更新后执行
      setTimeout(() => {
        this.stopAllVideos();
      }, 300);
    }, 50);
  },
  onHide() {
    // 页面隐藏时停止所有视频播放
    this.stopAllVideos();
    this.pageActive = false;
  },
  // 微信小程序专用 - 当前页面是tab页时，点击tab时触发
  onTabItemTap(item) {
    // 点击当前tab项也停止视频播放，避免重复播放问题
    this.stopAllVideos();
  },
  // 监听返回事件 (在支持的平台上)
  onBackPress() {
    // 返回前停止所有视频播放
    this.stopAllVideos();
  },
  onUnload() {
    if (this.timer) {
      clearInterval(this.timer);
    }
    // 停止所有视频播放
    this.stopAllVideos();
  },
  // 分享给朋友
  onShareAppMessage(res) {
    return {
      title: '广横走文创 - 甄选本地好物',
      path: '/pages/new_index/index'
    }
  },
  // 分享到朋友圈
  onShareTimeline() {
    return {
      title: '广横走文创 - 甄选本地好物',
      query: ''
    }
  },
  onPullDownRefresh() {
    this.fetchInitialData().then(() => {
      uni.stopPullDownRefresh();
    });
  },
  methods: {
    // ✅ 检查并显示首次进入的学校选择弹窗
    checkAndShowSchoolSelectPopup() {
      try {
        // 检查是否已经显示过弹窗（使用缓存标记）
        const hasSeenSchoolSelectPopup = uni.getStorageSync('hasSeenSchoolSelectPopup');
        
        if (!hasSeenSchoolSelectPopup) {
          // 首次进入，显示弹窗
          this.showSchoolSelectPopup = true;
          console.log('🎯 首次进入小程序，显示学校选择弹窗');
        } else {
          console.log('✅ 已显示过学校选择弹窗，跳过显示');
        }
      } catch (error) {
        console.error('检查学校选择弹窗失败:', error);
      }
    },

    // ✅ 处理学校选择弹窗的"我知道了"按钮
    handleSchoolSelectPopupConfirm() {
      // 隐藏弹窗
      this.showSchoolSelectPopup = false;
      
      // 标记已经显示过弹窗（存储到缓存中，下次启动时不再显示）
      try {
        uni.setStorageSync('hasSeenSchoolSelectPopup', true);
        console.log('✅ 已标记学校选择弹窗为已显示，缓存已保存');
      } catch (error) {
        console.error('保存缓存失败:', error);
      }

      // 弹窗关闭后的业务逻辑（如果需要）
      // 例如：跳转到学校选择，触发某个事件等
    },

    // 获取学校显示图片（优先使用封面缩略图，回退到校徽）
    getSchoolDisplayImage(school) {
      if (!school) {
        return 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/guanghengzou_logo.png';
      }

      // 优先使用封面缩略图
      if (school.coverThumbnail) {
        return school.coverThumbnail;
      }

      // 回退到校徽
      if (school.schoolLogo) {
        return school.schoolLogo;
      }

      // 默认图片
      return 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/guanghengzou_logo.png';
    },

    // 分批加载数据，避免阻塞渲染
    async loadDataInBatches() {
      try {
        // 第一批：加载关键数据（学校信息）
        await this.loadCriticalData();

        // 第二批：加载主要内容数据
        setTimeout(async () => {
          await this.loadMainContent();
        }, 50);

        // 第三批：加载次要功能
        setTimeout(() => {
          this.loadSecondaryFeatures();
        }, 100);

      } catch (error) {
        console.error('分批加载数据失败:', error);
        // 降级处理：直接加载基础数据
        this.loadBasicData();
      }
    },

    // 加载关键数据（必须优先加载的）
    async loadCriticalData() {
      try {
        // 加载学校列表
        await this.loadStoreGroups();

        // 如果没有选择学校，默认选择第一个学校
        if (!this.selectedSchool && this.schoolGroups.length > 0) {
          console.log('没有选择学校，默认选择第一个学校');
          const firstSchoolGroup = this.schoolGroups[0];
          this.selectedSchool = {
            id: firstSchoolGroup.school.id,
            schoolName: firstSchoolGroup.school.schoolName,
            schoolLogo: firstSchoolGroup.school.schoolLogo || null,
            coverThumbnail: firstSchoolGroup.school.coverThumbnail || null
          };
          this.saveSelectedSchool(this.selectedSchool);
          this.$store.commit('setSelectedSchool', this.selectedSchool);
        }
      } catch (error) {
        console.error('加载关键数据失败:', error);
      }
    },

    // 加载主要内容数据
    async loadMainContent() {
      try {
        // 并行加载主要数据
        await Promise.all([
          this.fetchInitialData(false), // 不显示加载动画，避免闪烁
          this.fetchCategoryList()
        ]);

        // 标记数据加载完成
        this.dataLoaded = true;
      } catch (error) {
        console.error('加载主要内容失败:', error);
        // 降级：串行加载
        try {
          await this.fetchInitialData(false);
          this.fetchCategoryList();
          this.dataLoaded = true;
        } catch (fallbackError) {
          console.error('降级加载也失败:', fallbackError);
          // 即使失败也标记为已加载，避免一直显示加载状态
          this.dataLoaded = true;
        }
      }
    },

    // 加载次要功能
    loadSecondaryFeatures() {
      try {
        // 初始化秒杀倒计时
        this.initCountdown();

        // 初始化视频上下文
        this.initVideoContexts();

        // 如果用户还没有授权位置，尝试自动选择最近的学校（非阻塞）
        if (this.selectedSchool && this.schoolGroups.length > 0) {
          this.tryAutoSelectNearestSchool();
        }
      } catch (error) {
        console.error('加载次要功能失败:', error);
      }
    },

    // 降级加载基础数据
    async loadBasicData() {
      try {
        await this.fetchInitialData(false);
      } catch (error) {
        console.error('基础数据加载失败:', error);
      }
    },

    // 初始化学校和数据加载
    async initializeSchoolAndData() {
      try {
        // 先加载学校列表
        await this.loadStoreGroups();

        // 如果没有选择学校，默认选择第一个学校
        if (!this.selectedSchool && this.schoolGroups.length > 0) {
          console.log('没有选择学校，默认选择第一个学校');
          const firstSchoolGroup = this.schoolGroups[0];
          this.selectedSchool = {
            id: firstSchoolGroup.school.id,
            schoolName: firstSchoolGroup.school.schoolName,
            schoolLogo: firstSchoolGroup.school.schoolLogo || null,
            coverThumbnail: firstSchoolGroup.school.coverThumbnail || null
          };

          // 保存默认选择的学校
          this.saveSelectedSchool(this.selectedSchool);
          this.$store.commit('setSelectedSchool', this.selectedSchool);
        }

        // 获取首页数据
        await this.fetchInitialData();

        // 如果用户还没有授权位置，尝试自动选择最近的学校（但不阻塞数据加载）
        if (this.selectedSchool && this.schoolGroups.length > 0) {
          this.tryAutoSelectNearestSchool();
        }

      } catch (error) {
        console.error('初始化学校和数据失败:', error);
        // 即使出错也要尝试获取数据
        this.fetchInitialData();
      }
    },

    // 尝试自动选择最近的学校（非阻塞）
    async tryAutoSelectNearestSchool() {
      try {
        // 检查是否已经根据位置选择过学校
        const hasLocationBasedSelection = uni.getStorageSync('hasLocationBasedSelection');
        if (hasLocationBasedSelection) {
          console.log('已经根据位置选择过学校，跳过自动选择');
          return;
        }

        console.log('尝试根据位置自动选择最近的学校...');

        // 获取用户位置
        const userLocation = await this.getUserLocation();

        if (!userLocation) {
          console.log('无法获取用户位置，保持当前学校选择');
          return;
        }

        console.log('用户位置:', userLocation);
        this.userLocation = userLocation;

        // 计算所有门店的距离，找到最近的学校
        let nearestSchool = null;
        let minDistance = Infinity;

        for (const schoolGroup of this.schoolGroups) {
          if (schoolGroup.stores && schoolGroup.stores.length > 0) {
            // 计算该学校下所有门店的最近距离
            let schoolMinDistance = Infinity;

            for (const store of schoolGroup.stores) {
              if (store.latitude && store.longitude) {
                const distance = this.calculateDistance(
                  userLocation.latitude,
                  userLocation.longitude,
                  parseFloat(store.latitude),
                  parseFloat(store.longitude)
                );

                if (distance < schoolMinDistance) {
                  schoolMinDistance = distance;
                }
              }
            }

            console.log(`学校 ${schoolGroup.school.schoolName} 最近距离: ${schoolMinDistance.toFixed(2)}km`);

            if (schoolMinDistance < minDistance) {
              minDistance = schoolMinDistance;
              nearestSchool = schoolGroup.school;
            }
          }
        }

        if (nearestSchool && nearestSchool.id !== this.selectedSchool.id) {
          console.log(`找到最近的学校: ${nearestSchool.schoolName} (${minDistance.toFixed(2)}km)`);

          // 更新选择的学校
          this.selectedSchool = {
            id: nearestSchool.id,
            schoolName: nearestSchool.schoolName,
            schoolLogo: nearestSchool.schoolLogo || null,
            coverThumbnail: nearestSchool.coverThumbnail || null
          };

          // 保存学校选择
          this.saveSelectedSchool(this.selectedSchool);
          this.$store.commit('setSelectedSchool', this.selectedSchool);

          // 标记已经根据位置选择过学校
          uni.setStorageSync('hasLocationBasedSelection', true);

          // 重新获取首页数据
          await this.fetchInitialData();

          uni.showToast({
            title: `已切换到最近的学校: ${nearestSchool.schoolName}`,
            icon: 'none',
            duration: 2000
          });
        } else {
          console.log('当前选择的学校已经是最近的学校');
          // 标记已经根据位置选择过学校
          uni.setStorageSync('hasLocationBasedSelection', true);
        }

      } catch (error) {
        console.error('自动选择最近学校失败:', error);
        // 不影响正常使用，只是无法自动选择
      }
    },

    // 初始化视频上下文
    initVideoContexts() {
      setTimeout(() => {
        const videoIds = ['myVideo1', 'myVideo2'];
        videoIds.forEach(id => {
          this.videoContexts[id] = uni.createVideoContext(id, this);
        });
      }, 100);
    },
    
    // 获取视频上下文
    getVideoContext(videoId) {
      // 如果已存在视频上下文，则直接返回
      if (this.videoContexts[videoId]) {
        return this.videoContexts[videoId];
      }
      
      // 否则创建新的视频上下文
      const videoContext = uni.createVideoContext(videoId, this);
      this.videoContexts[videoId] = videoContext;
      return videoContext;
    },
    
    // 停止所有视频播放
    stopAllVideos() {
      // 清除活跃视频ID
      this.activeVideoId = null;
      
      const videoIds = ['myVideo1', 'myVideo2'];
      videoIds.forEach(id => {
        // 重置播放状态标记
        this.userInitiatedPlay[id] = false;
        
        const videoContext = this.getVideoContext(id);
        if (videoContext) {
          // 先暂停视频播放
          videoContext.pause();
          
          // 尝试退出全屏
          try {
            // 微信小程序的视频控制API
            if (typeof videoContext.exitFullScreen === 'function') {
              videoContext.exitFullScreen();
            } else if (typeof videoContext.exitFullscreen === 'function') {
              // 兼容不同写法
              videoContext.exitFullscreen();
            }
          } catch (e) {
            console.error('退出全屏失败:', e);
          }
        }
      });
    },
    
    // 检查并显示登录弹窗（现在使用 Vuex 状态，无需额外处理）
    checkAndShowLoginPopup() {
      // 登录弹窗状态现在由 Vuex 管理，通过计算属性 shouldShowLoginPopup 自动响应
      // 这个方法保留是为了兼容性，实际上已经不需要手动处理
      console.log('登录弹窗状态检查 - 当前状态:', this.shouldShowLoginPopup);
    },
    
    // 页面滚动透明度变化
    onPageScroll(e) {
      const scrollTop = e.scrollTop;

      // 设置渐变的触发距离，可以根据需要调整
      const maxScrollDistance = 200; // 滚动200px后完全变为白色

      // 计算透明度，从0到1
      const opacity = Math.min(scrollTop / maxScrollDistance, 1);

      // 根据滚动距离动态设置导航栏背景色和阴影
      if (opacity === 0) {
        // 完全透明，无阴影
        this.navBarBgColor = 'transparent';
        this.navBarBoxShadow = 'none';
      } else if (opacity === 1) {
        // 完全白色，显示阴影
        this.navBarBgColor = '#ffffff';
        this.navBarBoxShadow = '0 2px 8px rgba(0, 0, 0, 0.1)';
      } else {
        // 渐变过程中，使用rgba实现半透明白色，阴影也渐变
        this.navBarBgColor = `rgba(255, 255, 255, ${opacity})`;
        this.navBarBoxShadow = `0 2px 8px rgba(0, 0, 0, ${0.1 * opacity})`;
      }

      // 保持原有的透明度值
      this.opacityvalue = 1;
    },
    
    // ✅ 优化：初始数据获取（带缓存）
    async fetchInitialData(showLoading = true) {
      try {
        // 根据参数决定是否显示全屏加载
        if (showLoading) {
          uni.showLoading({
            title: '加载中...',
            mask: true
          });
        }

        // 构建请求参数，包含学校ID
        const params = {};
        if (this.selectedSchool && this.selectedSchool.id) {
          params.schoolId = this.selectedSchool.id;
        }

        console.log('🚀 开始获取首页数据，参数:', params);

        // ✅ 优化：使用缓存机制，5分钟过期
        const cacheKey = `${CACHE_KEYS.HOME_CONTENT}${params.schoolId || 'default'}`;
        const res = await cacheManager.getOrSet(
          cacheKey,
          async () => {
            console.log('🌐 从服务器获取首页内容');
            return await fetchContent(params);
          },
          CACHE_EXPIRE_TIME.MEDIUM, // 5分钟缓存
          false
        );

        console.log('📦 首页数据响应:', res);

        if (res && res.code === 200) {
          const content = res.data;
          console.log('✅ 首页数据内容:', content);

          // 检查数据是否为空
          if (!content) {
            console.warn('⚠️ 首页数据内容为空');
            throw new Error('首页数据内容为空');
          }
          
          // 将接口返回的数据映射到轮播图数据
          if (content.advertiseList && content.advertiseList.length > 0) {
            this.advertiseList = content.advertiseList.map(item => ({
              id: item.id,
              pic: item.pic,
              link: item.url || `/pages/product/list?bannerType=${item.id}`
            }));
          }                   
          
          // 获取热门产品列表
          if (content.hotProductList && content.hotProductList.length > 0) {
            this.hotProductList = content.hotProductList.map(item => ({
              productId: item.productId || item.id,
              cover: item.cover,
              productName: item.productName || '',
              price: item.price || 0
            }));
          }
          
          // 获取新品列表
          if (content.newProductList && content.newProductList.length > 0) {
            this.newProductList = content.newProductList.map(item => ({
              productId: item.productId || item.id,
              pic: item.pic,
              cover: item.cover || item.pic,
              productName: item.productName || item.name || '',
              price: item.price || 0,
              newStatus: item.newStatus || 0,
              productCategoryName: item.productCategoryName || '',
              productRecommandStatus: item.productRecommandStatus || 0
            }));
          }
          
          // 获取秒杀产品
          if (content.homeFlashPromotion && content.homeFlashPromotion.productList && content.homeFlashPromotion.productList.length > 0) {
            this.flashSaleProducts = content.homeFlashPromotion.productList.map(item => {
              const mappedItem = {
                productId: item.id,
                id: item.id,
                pic: item.pic,
                cover: item.pic,
                productName: item.productName || item.name || '',
                flashPromotionPrice: item.flashPromotionPrice, // 优惠价
                price: item.price, // 原价
                originalPrice: item.originalPrice // 更早的原价（不使用）
              };
              console.log('首页秒杀商品数据映射:', {
                原始数据: item,
                映射后数据: mappedItem
              });
              return mappedItem;
            });
            this.showFlashSale = true;
          } else {
            // 如果没有秒杀数据，设置为空数组并隐藏秒杀区域
            this.flashSaleProducts = [];
            this.showFlashSale = false;
          }
          
          // 获取本地活动
          if (content.activityList && content.activityList.length > 0) {
            this.localEventsList = content.activityList.map(item => ({
              id: item.id,
              name: item.name,
              introduction: item.introduction || '',
              pic: item.pic
            }));
          }
          
          // 获取本地好物
          if (content.localGoodsList && content.localGoodsList.length > 0) {
            this.localGoodsList = content.localGoodsList.map(item => ({
              id: item.id,
              name: item.name,
              description: item.description || '',
              pic: item.pic,
              status: item.status
            }));
          }
          
          // 获取精彩濠江
          if (content.wonderfulMacauList && content.wonderfulMacauList.length > 0) {
            this.wonderfulMacauList = content.wonderfulMacauList.map(item => ({
              id: item.id,
              title: item.title,
              cover: item.cover,
              video: item.video || '',
              content: item.content || ''
            }));
          }
        }

        // 重置瀑布流分页状态并加载数据
        this.resetWaterfallPagination();
        this.loadWaterfallProducts();

        // ✅ 标记数据已加载，隐藏骨架屏
        this.dataLoaded = true;

        // 隐藏加载提示
        if (showLoading) {
          uni.hideLoading();
        }

      } catch (error) {
        console.error('💥 获取首页数据失败:', error);

        // 详细错误信息分析
        if (error.message && error.message.includes('empty fetchedData')) {
          console.error('🔍 检测到 empty fetchedData 错误，可能原因:');
          console.error('1. 接口返回数据为空');
          console.error('2. 网络请求失败');
          console.error('3. 参数传递错误:', params);
          console.error('4. 服务器响应异常');

          // 尝试重新获取数据（不带参数）
          console.log('🔄 尝试不带参数重新获取数据...');
          try {
            const fallbackRes = await fetchContent({});
            console.log('🔄 降级请求结果:', fallbackRes);
            if (fallbackRes && fallbackRes.code === 200 && fallbackRes.data) {
              console.log('✅ 降级请求成功，使用降级数据');
              // 处理降级数据...
              return;
            }
          } catch (fallbackError) {
            console.error('🔄 降级请求也失败:', fallbackError);
          }
        }

        uni.showToast({
          title: error.message || '获取数据失败',
          icon: 'none'
        });
        // 出错时也要隐藏加载
        if (showLoading) {
          uni.hideLoading();
        }
      }
    },



    // 加载瀑布流商品数据
    async loadWaterfallProducts() {
      if (this.waterfallLoading) return;

      try {
        this.waterfallLoading = true;

        // 构建请求参数
        const params = {
          pageNum: this.waterfallPage,
          pageSize: this.waterfallPageSize,
          sort: 0  // 默认排序
        };

        // 添加学校ID参数
        if (this.selectedSchool && this.selectedSchool.id) {
          params.schoolId = this.selectedSchool.id;
        }

        const result = await searchProductList(params);
        if (result && result.data && result.data.list) {
          // 转换数据格式以适配 ProductWaterfall 组件
          const products = result.data.list.map(item => ({
            productId: item.id,
            pic: item.pic || item.cover,
            cover: item.pic || item.cover,
            productName: item.name || item.productName || '',
            price: item.price || 0,
            newStatus: item.newStatus || 0,
            productCategoryName: item.productCategoryName || '',
            productRecommandStatus: item.productRecommandStatus || 0
          }));

          if (this.waterfallPage === 1) {
            this.waterfallProductList = products;
          } else {
            this.waterfallProductList = [...this.waterfallProductList, ...products];
          }

          // 判断是否还有更多数据
          if (products.length < this.waterfallPageSize ||
              (result.data.pages && this.waterfallPage >= result.data.pages)) {
            this.waterfallHasMore = false;
          } else {
            this.waterfallHasMore = true;
          }
        } else {
          this.waterfallHasMore = false;
        }
      } catch (error) {
        console.error('加载瀑布流商品失败:', error);
        this.waterfallHasMore = false;
      } finally {
        this.waterfallLoading = false;
      }
    },

    // 加载更多瀑布流商品
    async loadMoreWaterfallProducts() {
      if (!this.waterfallHasMore || this.waterfallLoading) return;

      this.waterfallPage++;
      await this.loadWaterfallProducts();
    },

    // 重置瀑布流分页状态
    resetWaterfallPagination() {
      this.waterfallPage = 1;
      this.waterfallProductList = [];
      this.waterfallLoading = false;
      this.waterfallHasMore = true;
    },
    
    // 初始化秒杀倒计时
    initCountdown() {
      // 设置结束时间为当天23:59:59
      const now = new Date();
      const endTime = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 23, 59, 59);
      
      // 更新倒计时
      const updateCountdown = () => {
        const now = new Date();
        const diff = Math.max(0, endTime - now) / 1000;
        
        const hours = Math.floor(diff / 3600);
        const minutes = Math.floor((diff % 3600) / 60);
        const seconds = Math.floor(diff % 60);
        
        this.hours = hours.toString().padStart(2, '0');
        this.minutes = minutes.toString().padStart(2, '0');
        this.seconds = seconds.toString().padStart(2, '0');
        
        if (diff <= 0) {
          clearInterval(this.timer);
        }
      };
      
      // 先执行一次，然后每秒更新
      updateCountdown();
      this.timer = setInterval(updateCountdown, 1000);
    },
    
    // 导航到搜索页面
    handleSearch() {
      uni.navigateTo({
        url: '/pages/search/search'
      });
    },
    

    

    
    // 获取用户地理位置
    getUserLocation() {
      return new Promise((resolve, reject) => {
        // 先检查定位权限
        uni.getSetting({
          success: (settingRes) => {
            if (settingRes.authSetting['scope.userLocation'] === false) {
              // 用户之前拒绝了定位权限，引导用户开启
              uni.showModal({
                title: '定位权限',
                content: '为了为您推荐最近的门店，需要获取您的地理位置。请在设置中开启定位权限。',
                showCancel: true,
                cancelText: '跳过',
                confirmText: '去设置',
                success: (modalRes) => {
                  if (modalRes.confirm) {
                    uni.openSetting({
                      success: (openRes) => {
                        if (openRes.authSetting['scope.userLocation']) {
                          // 用户开启了权限，重新获取位置
                          this.getLocation(resolve, reject);
                        } else {
                          resolve(null);
                        }
                      },
                      fail: () => resolve(null)
                    });
                  } else {
                    resolve(null);
                  }
                }
              });
            } else {
              // 权限未拒绝，尝试获取位置
              this.getLocation(resolve, reject);
            }
          },
          fail: () => {
            // 获取设置失败，直接尝试获取位置
            this.getLocation(resolve, reject);
          }
        });
      });
    },
    
    // 实际获取地理位置的方法
    getLocation(resolve, reject) {
      uni.getLocation({
        type: 'gcj02', // 使用国测局坐标系
        success: (res) => {
          console.log('获取位置成功:', res);
          this.isLocationPermissionGranted = true;
          resolve({
            longitude: res.longitude,
            latitude: res.latitude
          });
        },
        fail: (err) => {
          console.log('获取位置失败:', err);
          
          if (err.errMsg.includes('auth deny')) {
            // 用户拒绝授权
            uni.showModal({
              title: '定位权限',
              content: '获取位置权限被拒绝，将为您选择默认门店。',
              showCancel: false,
              confirmText: '确定'
            });
          }
          
          resolve(null);
        }
      });
    },
    

    

    
    // 处理门店logo加载错误
    handleStoreLogoError() {
      // 当门店logo加载失败时，这里可以做一些处理
      console.log('门店logo加载失败，已使用默认logo');
    },
    
    // 处理学校选择
    handleStoreSelect() {
      // 如果正在自动选择学校，则不允许手动选择
      if (this.isAutoSelectingSchool) {
        uni.showToast({
          title: '正在为您自动选择学校，请稍候',
          icon: 'none',
          duration: 1500
        });
        return;
      }
      this.showStoreSelector = true;
    },







    // 导航到通知页面
    handleNotification() {
      uni.navigateTo({
        url: '/pages/notice/notice'
      });
    },
    
    // 立即购买
    toBuy() {
      uni.switchTab({
        url: '/pages/category/category'
      });
    },
    
    // 处理分类点击
    handleCategoryClick(item, index) {
      // 使用 Vuex 存储选中的分类ID
      this.$store.commit('setSelectedCategoryId', item.id || index + 1);
      
      // 使用switchTab跳转到category页面
      uni.switchTab({
        url: '/pages/category/category'
      });
    },
    
    // 导航到产品列表页（商品瀑布流专用）
    handleToProduct(product) {
      // 封装商品信息作为参数传递
      const productInfo = {
        id: product.productId || product.id,
        name: product.productName || product.name || '',
        pic: product.pic || product.cover || '',
        price: product.price || 0,
        sale: 0, // 默认销量为0，因为瀑布流商品没有销量信息
        collectCount: 0 // 默认收藏数为0
      };
      
      // 跳转到 product-list 页面，并传递选中的商品信息
      uni.navigateTo({
        url: `/pages/product/product-list?selectedProduct=${encodeURIComponent(JSON.stringify(productInfo))}`
      });
    },

    // 统一的商品详情页跳转方法
    // 用于处理特色卡片流和热榜推荐的点击事件，跳转到商品详情页
    handleProductDetail(product) {
      const productId = product.productId || product.id;
      if (productId) {
        console.log('跳转到商品详情页:', productId);

        // 构建跳转URL，如果有优惠价则传递相关参数
        let url = `/pages/product/product?id=${productId}`;

        // 如果是秒杀商品（有flashPromotionPrice），传递秒杀价格信息
        if (product.flashPromotionPrice && product.flashPromotionPrice > 0) {
          url += `&type=flash&isFlash=true&flashPrice=${product.flashPromotionPrice}`;
          console.log('跳转秒杀商品详情页:', {
            productId: productId,
            flashPrice: product.flashPromotionPrice,
            originalPrice: product.price,
            url: url
          });
        }

        uni.navigateTo({
          url: url
        });
      } else {
        console.error('商品ID缺失:', product);
        uni.showToast({
          title: '商品信息错误',
          icon: 'none'
        });
      }
    },
    
    // 处理秒杀商品点击
    handleFlashSaleItemClick(item) {
      const productId = item.productId || item.id;
      if (productId) {
        console.log('跳转到商品详情页:', productId);
        uni.navigateTo({
          url: `/pages/product/product?id=${productId}`
        });
      } else {
        console.error('商品ID缺失:', item);
        uni.showToast({
          title: '商品信息错误',
          icon: 'none'
        });
      }
    },

    // 处理标签点击
    handleTabClick(index) {
      this.selectedTab = index;
    },
    
    // 处理活动点击
    handleEventClick(item) {
      uni.navigateTo({
        url: `/pages/activities/detail/detail?id=${item.id}`
      });
    },
    
    // 处理本地好物点击
    handleLocalGoodsDetail(item) {
      uni.navigateTo({
        url: `/pages/activities/local-goods-detail/local-goods-detail?id=${item.id}`
      });
    },
    
    // 处理精彩濠江点击
    handleWonderfulMacauClick(item) {
      // 如果有视频且content为空，直接全屏播放视频
      if (item.video && !item.content) {
        this.openVideoPopup(item.video);
        return;
      }
      
      // 否则跳转到详情页
      uni.navigateTo({
        url: `/pages/activities/haojiang-detail/haojiang-detail?id=${item.id}`
      });
    },
    
    // 打开视频弹窗
    openVideoPopup(videoUrl) {
      // 先暂停所有可能正在播放的视频
      this.stopAllVideos();
      
      // 设置弹窗视频URL并显示弹窗
      this.popupVideoUrl = videoUrl;
      this.showVideoPopup = true;
      
      // 确保页面状态正确
      this.pageActive = true;
      this.userInitiatedPlay = {};
      this.activeVideoId = 'popupVideo';
      
      // 标记为未进入全屏，且用户未主动退出
      this.isPopupVideoFullscreen = false;
      this.userExitedFullscreen = false;
    },
    
    // 关闭视频弹窗
    closeVideoPopup() {
      // 标记正在关闭弹窗
      this.isClosingPopup = true;
      
      // 强制关闭弹窗的工具函数
      const forceClosePopup = () => {
        try {
          const videoContext = uni.createVideoContext('popupVideo', this);
          if (videoContext) {
            videoContext.pause();
            videoContext.stop();
          }
        } catch (e) {
          console.error('停止视频播放失败:', e);
        }
        
        // 无论如何都关闭弹窗
        this.showVideoPopup = false;
        this.popupVideoUrl = '';
        this.activeVideoId = null;
        this.isClosingPopup = false;
      };
      
      // 获取弹窗视频上下文并处理关闭
      try {
        const videoContext = uni.createVideoContext('popupVideo', this);
        if (videoContext) {
          // 如果当前是全屏状态，先尝试退出全屏
          if (this.isPopupVideoFullscreen) {
            try {
              if (typeof videoContext.exitFullScreen === 'function') {
                videoContext.exitFullScreen();
              } else if (typeof videoContext.exitFullscreen === 'function') {
                videoContext.exitFullscreen();
              }
              
              // 给退出全屏一些时间，然后继续后续操作
              // 不立即隐藏弹窗，而是在fullscreenchange事件中处理
              setTimeout(() => {
                // 如果已经退出全屏但弹窗仍然显示，说明没有触发fullscreenchange事件
                if (!this.isPopupVideoFullscreen && this.showVideoPopup) {
                  videoContext.pause();
                  videoContext.stop();
                  this.showVideoPopup = false;
                  this.popupVideoUrl = '';
                  this.activeVideoId = null;
                  this.isClosingPopup = false;
                } else if (this.isClosingPopup) {
                  // 超时处理：如果还在关闭状态但没有完成关闭，强制关闭
                  videoContext.pause();
                  videoContext.stop();
                  this.showVideoPopup = false;
                  this.popupVideoUrl = '';
                  this.activeVideoId = null;
                  this.isClosingPopup = false;
                }
              }, 500);
            } catch (e) {
              console.error('退出全屏失败:', e);
              // 如果退出全屏失败，直接停止播放并关闭弹窗
              forceClosePopup();
            }
          } else {
            // 如果不是全屏状态，直接关闭弹窗
            forceClosePopup();
          }
        } else {
          // 如果无法获取视频上下文，也直接关闭弹窗
          forceClosePopup();
        }
      } catch (e) {
        console.error('关闭视频弹窗出错:', e);
        forceClosePopup();
      }
    },
    
    // 处理弹窗视频全屏状态变化
    onPopupVideoFullscreenChange(e) {
      console.log('弹窗视频全屏状态:', e.detail.fullScreen);
      
      // 检测用户退出全屏的意图
      // 如果之前是全屏，现在不是，并且不是由于关闭弹窗导致的，认为是用户主动退出
      if (this.isPopupVideoFullscreen && !e.detail.fullScreen && !this.isClosingPopup) {
        console.log('用户主动退出全屏');
        this.userExitedFullscreen = true;
      }
      
      // 更新全屏状态
      this.isPopupVideoFullscreen = e.detail.fullScreen;
      
      // 如果退出全屏，但不是由于关闭弹窗引起的，并且用户未主动退出过全屏，则尝试再次进入全屏
      if (!e.detail.fullScreen && !this.isClosingPopup && !this.userExitedFullscreen) {
        // 短暂延迟后尝试再次进入全屏
        setTimeout(() => {
          // 确保弹窗仍然显示且不在关闭过程中，且用户没有主动退出全屏
          if (this.showVideoPopup && !this.isClosingPopup && !this.userExitedFullscreen) {
            const videoContext = uni.createVideoContext('popupVideo', this);
            if (videoContext) {
              videoContext.requestFullScreen({
                direction: 0,
                success: () => {
                  console.log('弹窗视频重新进入全屏成功');
                  this.isPopupVideoFullscreen = true;
                },
                fail: (err) => {
                  console.error('弹窗视频重新进入全屏失败:', err);
                  // 如果尝试三次仍失败，不再尝试
                  if (!this.userExitedFullscreen) {
                    this.userExitedFullscreen = true;
                  }
                }
              });
            }
          }
        }, 300);
      }
      
      // 如果页面不活跃且视频处于全屏状态，尝试退出全屏
      if (!this.pageActive && e.detail.fullScreen) {
        const videoContext = uni.createVideoContext('popupVideo', this);
        if (videoContext) {
          try {
            if (typeof videoContext.exitFullScreen === 'function') {
              videoContext.exitFullScreen();
            } else if (typeof videoContext.exitFullscreen === 'function') {
              videoContext.exitFullscreen();
            }
          } catch (error) {
            console.error('退出全屏失败:', error);
          }
        }
      }
      
      // 如果退出全屏且用户同时点击了关闭按钮的情况
      if (!e.detail.fullScreen && this.isClosingPopup) {
        // 隐藏弹窗
        this.showVideoPopup = false;
        this.popupVideoUrl = '';
        this.activeVideoId = null;
        this.isClosingPopup = false;
      }
    },
    
    // 播放视频全屏
    playVideoFullscreen(videoUrl, title) {
      // 使用视频弹窗播放
      this.openVideoPopup(videoUrl);
    },
    
    // 处理视频全屏变化
    onVideoFullscreenChange(e) {
      console.log('视频全屏状态:', e.detail.fullScreen);
      const videoId = e.currentTarget.id;
      
      // 记录进入全屏和退出全屏事件
      if (!e.detail.fullScreen) {
        // 退出全屏时，重置播放状态，防止返回页面后自动全屏
        this.userInitiatedPlay[videoId] = false;
      }
      
      // 如果页面不活跃且视频进入全屏，尝试退出全屏
      if (!this.pageActive && e.detail.fullScreen) {
        const videoContext = this.getVideoContext(videoId);
        if (videoContext) {
          // 尝试暂停和退出全屏
          videoContext.pause();
          try {
            if (typeof videoContext.exitFullScreen === 'function') {
              videoContext.exitFullScreen();
            } else if (typeof videoContext.exitFullscreen === 'function') {
              videoContext.exitFullscreen();
            }
          } catch (error) {
            console.error('退出全屏失败:', error);
          }
        }
      }
    },
    
    // 处理视频暂停
    onVideoPause(e) {
      console.log('视频暂停', e);
      const videoId = e.currentTarget.id;
      
      // 如果当前暂停的是活跃视频，清除活跃视频ID
      if (this.activeVideoId === videoId) {
        this.activeVideoId = null;
      }
      
      // 如果页面不活跃状态下，确保视频不会再次自动全屏
      if (!this.pageActive) {
        this.userInitiatedPlay[videoId] = false;
        
        // 确保退出全屏
        const videoContext = this.getVideoContext(videoId);
        if (videoContext) {
          try {
            if (typeof videoContext.exitFullScreen === 'function') {
              videoContext.exitFullScreen();
            } else if (typeof videoContext.exitFullscreen === 'function') {
              videoContext.exitFullscreen();
            }
          } catch (error) {
            console.error('退出全屏失败:', error);
          }
        }
      }
    },
    
    // 查看全部
    handleViewAll() {
      switch (this.selectedTab) {
        case 0:
          uni.switchTab({
            url: '/pages/category/category'
          });
          break;
        case 1:
          // 存储 tab 到缓存
          uni.setStorageSync('activities_selected_tab', 1);
          uni.switchTab({
            url: '/pages/activities/activities'
          });
          break;
        case 2:
          // 存储 tab 到缓存
          uni.setStorageSync('activities_selected_tab', 2);
          uni.switchTab({
            url: '/pages/activities/activities'
          });
          break;
      }
    },
    
    // 导航到购物车
    handleCart() {
      uni.navigateTo({
        url: '/pages/cart/cart'
      });
    },
    
    // 处理客服联系
    handleContact(e) {
      console.log('联系客服:', e);
    },
    
    // 处理登录成功
    handleLoginSuccess(userInfo) {
      // 清除 Vuex 中的登录弹窗状态
      this.$store.commit('clearLoginPopup');
      console.log('登录成功:', userInfo);

      // 登录成功后刷新页面数据
      this.fetchInitialData();
    },

    // 处理登录关闭
    handleLoginClose() {
      // 清除 Vuex 中的登录弹窗状态
      this.$store.commit('clearLoginPopup');
    },
    
    // 关闭隐私政策弹窗
    closePrivacy() {
      this.showPrivacyPopup = false;
    },
    
    // 关闭服务条款弹窗
    closeTerms() {
      this.showTermsPopup = false;
    },
    
    // 打开隐私政策弹窗
    openPrivacy() {
      this.showPrivacyPopup = true;
    },
    
    // 打开服务条款弹窗
    openTerms() {
      this.showTermsPopup = true;
    },
    
    // 获取类别数据
    async fetchCategoryList() {
      try {
        const cateRes = await fetchProductCateList(0);
        if (cateRes && cateRes.data) {
          this.brandList = cateRes.data.map(item => ({
            id: item.id,
            name: item.name,
            icon: item.icon,
            link: `/pages/product/list?categoryId=${item.id}`
          }));
        } else {      
        }
      } catch (err) {
        console.error('获取分类数据失败:', err);
      }
    },
    handleBannerClick(item) {
      console.log('轮播图点击', item);

      // 如果url存在且是数字，表示是产品ID
      if (item.link && !isNaN(Number(item.link))) {
        // 跳转到产品详情页
        uni.navigateTo({
          url: `/pages/product/product?id=${item.link}`
        });
      }
      // 如果链接指向分类页面
      else if (item.link && item.link.includes('/pages/category/category')) {
        // 提取分类ID
        const match = item.link.match(/categoryId=(\d+)/);
        if (match && match[1]) {
          const categoryId = parseInt(match[1]);
          // 使用 switchTab 切换到分类页，并通过 Vuex 传递分类ID
          this.$store.commit('setSelectedCategoryId', categoryId);
          wx.switchTab({
            url: '/pages/category/category'
          });
        } else {
          // 没有分类ID，直接切换到分类页
          wx.switchTab({
            url: '/pages/category/category'
          });
        }
      }
      else if (item.link) {
        // 如果有其他类型的URL
        uni.navigateTo({
          url: item.link
        });
      }
      else {
        // 如果没有URL，则按类型跳转到产品列表
        uni.switchTab({
          url: '/pages/category/category'
        });
      }
    },
    // 视频播放事件处理
    onVideoPlay(e) {
      console.log('视频开始播放', e);
      const videoId = e.currentTarget.id;
      
      // 如果页面不是活跃状态，停止播放
      if (!this.pageActive) {
        const videoContext = this.getVideoContext(videoId);
        if (videoContext) {
          videoContext.pause();
          return;
        }
      }
      
      // 如果有其他视频正在播放，先暂停它
      if (this.activeVideoId && this.activeVideoId !== videoId) {
        const otherVideoContext = this.getVideoContext(this.activeVideoId);
        if (otherVideoContext) {
          console.log('暂停其他正在播放的视频:', this.activeVideoId);
          otherVideoContext.pause();
        }
      }
      
      // 更新当前活跃视频ID
      this.activeVideoId = videoId;
      
      // 仅当是用户第一次点击播放时才自动全屏
      // 当页面切换回来后，这个值会被重置，所以不会再次触发全屏
      if (!this.userInitiatedPlay[videoId]) {
        this.userInitiatedPlay[videoId] = true;
        
        // 延迟300ms执行，确保视频已开始加载
        setTimeout(() => {
          // 再次检查页面是否活跃
          if (!this.pageActive) return;
          
          const videoContext = this.getVideoContext(videoId);
          if (videoContext) {
            videoContext.requestFullScreen({
              success: () => {
                console.log('全屏请求成功');
              },
              fail: (err) => {
                console.error('全屏请求失败:', err);
              }
            });
          }
        }, 300);
      }
    },
    // 备选播放方法
    fallbackVideoPlay(videoUrl) {
      // 使用uni-app API播放视频
      uni.navigateTo({
        url: '/pages/video/video',
        success: (res) => {
          // 通过事件通信传递要播放的视频URL
          res.$getOpenerEventChannel().emit('playVideo', { url: videoUrl });
        },
        fail: () => {
          uni.showToast({
            title: '无法播放此视频',
            icon: 'none'
          });
        }
      });
    },
    // 处理弹窗视频播放开始事件
    onPopupVideoPlay(e) {
      console.log('弹窗视频开始播放');
      
      // 如果视频尚未进入全屏状态，且用户未主动退出过全屏，请求全屏
      if (!this.isPopupVideoFullscreen && !this.userExitedFullscreen) {
        // 延迟执行全屏操作，确保视频已经开始加载
        setTimeout(() => {
          // 再次检查用户是否退出过全屏
          if (!this.userExitedFullscreen && !this.isClosingPopup) {
            const videoContext = uni.createVideoContext('popupVideo', this);
            if (videoContext) {
              videoContext.requestFullScreen({
                direction: 0,
                success: () => {
                  console.log('弹窗视频全屏成功');
                  this.isPopupVideoFullscreen = true;
                },
                fail: (err) => {
                  console.error('弹窗视频全屏失败:', err);
                  // 标记为用户拒绝全屏，不再尝试
                  this.userExitedFullscreen = true;
                }
              });
            }
          }
        }, 300);
      }
    },
    handleGiftBag() {
      console.log('处理送礼按钮点击');
      // 导航到礼品列表页面
      uni.navigateTo({
        url: '/pages/gift-bag/giftsList'
      });
    },
    
    // 处理秒杀区域点击
    handleFlashSection() {
      // 跳转到爆品榜单页面
      uni.navigateTo({
        url: '/pages/product/sales-ranking'
      });
    },
    
    // 处理邀请收益区域点击  
    handleInviteSection() {
      // 跳转到邀请收益页面
      uni.navigateTo({
        url: '/pages/user/income'
      });
    },
    
    // 处理积分商城区域点击
    handlePointSection() {
      // 跳转到积分商城页面
      uni.navigateTo({
        url: '/pages/signin/signin'
      });
    },
    
    // 处理热榜推荐商品点击
    handleProductClick(product) {
      console.log('商品点击:', product);
      if (product.productId) {
        // 跳转到商品详情页
        uni.navigateTo({
          url: `/pages/product/product?id=${product.productId}`
        });
      }
    },
    
    // 处理更多商品点击
    handleMoreProducts() {
      console.log('更多商品点击');
      // 跳转到商品列表页
      uni.switchTab({
        url: '/pages/category/category'
      });
    },


    // 处理购物车点击事件
    handleCartClick() {
      console.log('购物车被点击');
      uni.navigateTo({
        url: '/pages/cart/cart'
      });
    },

    // 加载已选择的门店信息
    loadSelectedStore() {
      try {
        // 优先加载学校信息
        const schoolInfo = uni.getStorageSync('selectedSchool');
        if (schoolInfo) {
          let school = schoolInfo;
          if (typeof schoolInfo === 'string') {
            school = JSON.parse(schoolInfo);
          }
          this.selectedSchool = school;
          // 同步更新Vuex状态
          this.$store.commit('setSelectedSchool', school);
          return;
        }

        // 兼容旧的门店信息，从中提取学校信息
        const storeInfo = uni.getStorageSync('selectedStore');
        if (storeInfo) {
          let store = storeInfo;
          if (typeof storeInfo === 'string') {
            store = JSON.parse(storeInfo);
          }
          if (store && store.schoolId && store.schoolName) {
            this.selectedSchool = {
              id: store.schoolId,
              schoolName: store.schoolName,
              schoolLogo: store.schoolLogo || null,
              coverThumbnail: store.coverThumbnail || null
            };
            // 保存学校信息到新的存储键
            this.saveSelectedSchool(this.selectedSchool);
            // 同步更新Vuex状态
            this.$store.commit('setSelectedSchool', this.selectedSchool);
          }
        }
      } catch (error) {
        console.error('加载学校信息失败:', error);
        this.selectedSchool = null;
        // 清空Vuex状态
        this.$store.commit('setSelectedSchool', null);
      }
    },

    // 加载学校列表数据（使用现有的门店分组接口）
    async loadSchoolList() {
      try {
        await this.loadStoreGroups();
      } catch (error) {
        console.error('加载学校列表失败:', error);
      }
    },

    // ✅ 优化：加载门店分组数据（带缓存）
    async loadStoreGroups() {
      try {
        // 使用缓存机制，15分钟过期
        const schoolGroups = await cacheManager.getOrSet(
          CACHE_KEYS.STORE_GROUPS,
          async () => {
            console.log('🌐 从服务器获取门店分组数据');
            const res = await fetchStoreGroupsBySchool();

            if (res && res.data && res.data.schoolGroups) {
              return res.data.schoolGroups;
            } else if (res && res.schoolGroups) {
              return res.schoolGroups;
            } else if (res && res.data) {
              return res.data;
            } else {
              throw new Error('门店分组数据格式不正确');
            }
          },
          CACHE_EXPIRE_TIME.LONG, // 15分钟缓存
          false
        );

        this.schoolGroups = schoolGroups;
        this.storeGroupsLoaded = true; // 标记已加载
        console.log('✅ 门店分组数据加载成功，共', schoolGroups.length, '个学校');

      } catch (error) {
        console.error('💥 加载门店分组失败:', error);
        this.schoolGroups = [];
        this.storeGroupsLoaded = false; // 标记加载失败，下次重试

        uni.showToast({
          title: error.message || '网络错误',
          icon: 'none'
        });
      }
    },

    // 处理学校选择确认（StoreSelector组件现在返回学校信息）
    handleStoreSelected(school) {
      // 保存选择的学校信息
      this.selectedSchool = school;
      this.saveSelectedSchool(school);

      // 更新Vuex中的学校状态
      this.$store.commit('setSelectedSchool', school);

      // 清除位置选择标记，允许下次重新根据位置选择
      uni.removeStorageSync('hasLocationBasedSelection');

      // 重置瀑布流分页状态
      this.resetWaterfallPagination();

      // 学校变化后重新获取数据
      this.fetchInitialData();

      // 使用简洁的Toast提示
      uni.showToast({
        title: '学校选择成功',
        icon: 'success',
        duration: 1500
      });
    },

    // 处理学校选择弹窗关闭
    handleStoreSelectorClose() {
      this.showStoreSelector = false;
    },

    // 保存选择的学校信息
    saveSelectedSchool(school) {
      try {
        this.selectedSchool = school;
        uni.setStorageSync('selectedSchool', JSON.stringify(school));
        console.log('学校信息已保存:', school);

        // 更新Vuex中的学校状态
        this.$store.commit('setSelectedSchool', school);
      } catch (error) {
        console.error('保存学校信息失败:', error);
      }
    },

    // ✅ 优化：带防抖的学校变化检测
    checkSchoolChangeDebounced() {
      const now = Date.now();

      // 防抖：如果距离上次检查时间太短，跳过
      if (now - this.lastSchoolChangeCheck < this.schoolChangeDebounceTime) {
        console.log('⏱️ 学校变化检查防抖，跳过');
        return;
      }

      this.lastSchoolChangeCheck = now;
      this.checkSchoolChange();
    },

    // 检测学校是否发生变化
    checkSchoolChange() {
      try {
        const currentSchoolId = this.selectedSchool ? this.selectedSchool.id : null;
        const lastSchoolId = this.$store.state.lastSchoolId;

        // 如果学校发生变化，重新获取数据
        if (currentSchoolId !== lastSchoolId) {
          console.log('🔄 检测到学校变化，重新获取数据', {
            currentSchoolId,
            lastSchoolId
          });

          // 更新最后记录的学校ID
          this.$store.commit('updateLastSchoolId', currentSchoolId);

          // ✅ 优化：清除相关缓存
          cacheManager.invalidateByPrefix(CACHE_KEYS.HOME_CONTENT);
          cacheManager.invalidateByPrefix(CACHE_KEYS.HOME_ADVERTISE);
          cacheManager.invalidateByPrefix(CACHE_KEYS.HOME_HOT_PRODUCTS);

          // 重新获取首页数据（静默更新，不显示加载蒙版）
          this.fetchInitialData(false);
        }
      } catch (error) {
        console.error('检测学校变化失败:', error);
      }
    },

    // 自动选择距离最近的学校
    async autoSelectNearestSchool() {
      try {
        this.isAutoSelectingSchool = true;
        console.log('开始自动选择最近的学校...');

        // 先加载学校列表数据
        await this.loadStoreGroups();

        // 获取用户位置
        const userLocation = await this.getUserLocation();

        if (!userLocation) {
          console.log('无法获取用户位置，跳过自动选择学校');
          this.isAutoSelectingSchool = false;
          return;
        }

        console.log('用户位置:', userLocation);
        this.userLocation = userLocation;

        // 计算所有门店的距离，找到最近的门店
        let nearestStore = null;
        let minDistance = Infinity;
        let nearestSchool = null;

        for (const schoolGroup of this.schoolGroups) {
          if (schoolGroup.stores && schoolGroup.stores.length > 0) {
            for (const store of schoolGroup.stores) {
              if (store.longitude && store.latitude) {
                const distance = this.calculateDistance(
                  userLocation.latitude,
                  userLocation.longitude,
                  parseFloat(store.latitude),
                  parseFloat(store.longitude)
                );

                console.log(`门店 ${store.addressName} 距离: ${distance.toFixed(2)}km`);

                if (distance < minDistance) {
                  minDistance = distance;
                  nearestStore = store;
                  nearestSchool = {
                    id: schoolGroup.school.id,
                    schoolName: schoolGroup.school.schoolName,
                    schoolLogo: schoolGroup.school.schoolLogo || null
                  };
                }
              }
            }
          }
        }

        if (nearestSchool && nearestStore) {
          console.log(`自动选择最近的学校: ${nearestSchool.schoolName}, 最近门店: ${nearestStore.addressName}, 距离: ${minDistance.toFixed(2)}km`);

          // 保存自动选择的学校
          this.saveSelectedSchool(nearestSchool);

          // 自动选择学校后重新获取数据
          this.fetchInitialData();

          // 显示提示信息
          uni.showToast({
            title: `已为您选择最近的${nearestSchool.schoolName}`,
            icon: 'success',
            duration: 2000
          });
        } else {
          console.log('未找到合适的学校，可能是门店数据中缺少坐标信息');
        }

      } catch (error) {
        console.error('自动选择学校失败:', error);
      } finally {
        this.isAutoSelectingSchool = false;
      }
    },

    // 计算两点间距离（单位：公里）
    calculateDistance(lat1, lng1, lat2, lng2) {
      const R = 6371; // 地球半径（公里）

      const dLat = this.toRadians(lat2 - lat1);
      const dLng = this.toRadians(lng2 - lng1);

      const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(this.toRadians(lat1)) * Math.cos(this.toRadians(lat2)) *
                Math.sin(dLng / 2) * Math.sin(dLng / 2);

      const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

      return R * c;
    },

    // 角度转弧度
    toRadians(degrees) {
      return degrees * (Math.PI / 180);
    },

    // ✅ 优化：获取商品缩略图
    getProductThumbnail(url) {
      // 使用300x300的缩略图，减少图片大小
      return lazyLoadUtils.getThumbnail(url, 300, 300);
    },

    // ✅ 优化：获取轮播图缩略图
    getBannerThumbnail(url) {
      // 使用750x400的缩略图（适配轮播图尺寸）
      return lazyLoadUtils.getThumbnail(url, 750, 400);
    },

    // ✅ 优化：获取学校logo缩略图
    getSchoolThumbnail(url) {
      // 使用100x100的缩略图
      return lazyLoadUtils.getThumbnail(url, 100, 100);
    }
  }
};
</script>

<style lang="scss" scoped>
.home-page {
  position: relative;
  width: 100%;
  background-color: #FFFFFF;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen, Ubuntu, Cantarell, "Open Sans", "Helvetica Neue", sans-serif;
}

/* 顶部区域容器（包含导航栏、搜索框、轮播图的统一背景图） */
.top-section {
  padding: 0;
  position: relative;
  width: 100%;

  .top-bg-image {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: 0;
  }
}

/* 自定义导航栏 */
.nav-bar-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 30rpx;
  position: relative;
  width: 100%;
  z-index: 3;
}

/* 左侧Logo */
.nav-left {
  width: 120rpx;
  display: flex;
  justify-content: flex-start;
  align-items: center;
}

.nav-logo {
  width: 114rpx;
  height: 76rpx;
}

/* 中间标题 */
.nav-center {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.nav-title {
  width: 180rpx;
  height: 36rpx;
}

/* 搜索栏区域 */
.search-section {
  position: relative;
  z-index: 3;
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 20rpx 30rpx;
  height: 80rpx;
  width: 100%;
  box-sizing: border-box;
}

/* 门店选择 */
.store-selector {
  display: flex;
  align-items: center;
  gap: 10rpx;
  background: rgba(169, 255, 0, 0.2);
  border-radius: 20rpx;
  padding: 8rpx 16rpx;
  border: 2rpx solid rgba(169, 255, 0, 0.3);
}

.store-avatar {
  width: 56rpx;
  height: 56rpx;
  border-radius: 12rpx;
  background-color: rgba(255, 255, 255, 1);
  overflow: hidden;
}

.store-logo {
  width: 100%;
  height: 100%;
}

.store-info {
  flex: 1;
  margin-left: 8rpx;
  display: flex;
  flex-direction: column;
  gap: 2rpx;
}

.store-name {
  font-size: 26rpx;
  font-weight: 500;
  color: #0A0D05;
  line-height: 1.4;
  font-family: 'PingFang SC', -apple-system, BlinkMacSystemFont, sans-serif;
}

.store-name.auto-selecting {
  color: #666666;
  font-size: 24rpx;
}

.store-distance {
  font-size: 22rpx;
  color: #999999;
  line-height: 1.2;
  font-family: 'PingFang SC', -apple-system, BlinkMacSystemFont, sans-serif;
}

.arrow-down {
  width: 24rpx;
  height: 24rpx;
}

/* 搜索区域 */
.search-area {
  flex: 1;
  height: 72rpx;
  border: 2rpx solid transparent;
  border-radius: 20rpx;
  background: linear-gradient(#FFFFFF, #FFFFFF) padding-box, 
              linear-gradient(to right, #A3CB00, #BBE50F) border-box;
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 20rpx 12rpx;
  box-sizing: border-box;
}

.search-icon {
  width: 28rpx;
  height: 28rpx;
  flex-shrink: 0;
}

.search-placeholder {
  color: #9FA19D;
  font-size: 28rpx;
  font-weight: 400;
  flex: 1;
  font-family: 'PingFang SC', -apple-system, BlinkMacSystemFont, sans-serif;
}

.search-btn {
  background-color: #0A0D05;
  border-radius: 16rpx;
  padding: 10rpx 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.search-btn-text {
  color: #A9FF00;
  font-size: 28rpx;
  font-weight: 600;
  font-family: 'PingFang SC', -apple-system, BlinkMacSystemFont, sans-serif;
}

/* Banner轮播图 */
.banner-container {
  width: calc(100% - 60rpx);
  height: 276rpx; /* 调整高度以匹配 343:138 的宽高比 (690:276 ≈ 2.49:1) */
  position: relative;
  overflow: hidden;
  border-radius: 16rpx;
  margin: 36rpx 30rpx 20rpx 30rpx;
  z-index: 2;
}

.banner {
  width: 100%;
  height: 100%;
}

.banner-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 16rpx;
}

/* 宫格入口样式 */
.quick-grid {
  margin: -40rpx 20rpx 20rpx 20rpx;
  padding: 30rpx 0;
  background-color: #FFFFFF;
  border-radius: 40rpx 40rpx 0 0;
  box-shadow: none;
  position: relative;
  z-index: 5; /* 将 z-index 从 10 改为较小的值 */
}

.quick-grid .grid-list {
  display: flex;
  justify-content: space-between;
  width: 100%;
  box-sizing: border-box;
  padding: 0 20rpx;
}

.quick-grid .grid-list .grid-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 25%; /* 一行四个，平均分配宽度 */
  box-sizing: border-box;
}

.quick-grid .grid-list .grid-item .grid-image-wrap {
  width: 120rpx;
  height: 120rpx;
  border-radius: 60rpx;
  background: linear-gradient(to bottom, #89C4D0, #DCFF6A);
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  margin-bottom: 16rpx;
}

.quick-grid .grid-list .grid-item .grid-image-wrap .grid-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.quick-grid .grid-list .grid-item .grid-name {
  font-size: 14px;
  color: #000000;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 100%;
  text-align: center;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen, Ubuntu, Cantarell, "Open Sans", "Helvetica Neue", sans-serif;
}

/* 秒杀/邀请/积分区域样式 */
.flash-invite-section {
  margin: 0 20rpx 20rpx;
  display: flex;
  gap: 25rpx;
  height: 430rpx;
}

/* 左侧秒杀区域 */
.flash-section {
  width: 354rpx;
  position: relative;
  border-radius: 16rpx;
  overflow: hidden;
}

.flash-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.flash-content {
  position: relative;
  z-index: 2;
  height: 100%;
  padding: 16rpx;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.flash-header {
  display: flex;
  align-items: flex-start;
  gap: 16rpx;
  margin-bottom: 16rpx;
}

.flash-titles {
  flex: 0 0 auto;
}

.flash-title {
  font-size: 36rpx;
  font-weight: 500;
  color: #333333;
  line-height: 1.2;
  display: block;
  margin-bottom: 4rpx;
}

.flash-subtitle {
  font-size: 24rpx;
  color: #9FA19D;
  line-height: 1.2;
  display: block;
}

.flash-status {
  background: linear-gradient(135deg, #F02A41, #FE6C43);
  border-radius: 0 12rpx 0 12rpx;
  padding: 4rpx 12rpx;
  flex-shrink: 0;
}

.status-text {
  color: #FFFFFF;
  font-size: 24rpx;
  font-weight: 500;
}

.flash-products {
  flex: 1;
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: 1fr 1fr;
  margin-top: 120rpx;
}

.flash-product-item {
  position: relative;
  border-radius: 8rpx;
  border: 1px solid #A9FF00;
  overflow: visible;
  width: 110rpx;
  height: 110rpx;
  justify-self: center;
  align-self: center;
}

.flash-product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8rpx;
}

.flash-price-tag {
  position: absolute;
  bottom: -10rpx;
  left: 50%;
  transform: translateX(-50%);
  background: #A9FF00;
  border-radius: 24rpx;
  padding: 0 16rpx;
  min-width: 80rpx;
  height: 32rpx;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 8rpx;
  white-space: nowrap;
}

.flash-price-text {
  color: #262626;
  font-size: 24rpx;
  font-weight: 400;
  line-height: 44rpx;
  display: flex;
  align-items: center;
  flex: none;
  order: 0;
  flex-grow: 0;
}

/* 右侧区域 */
.right-section {
  margin-top: 20rpx;
  width: 320rpx;
  display: flex;
  flex-direction: column;
  gap: 30rpx;
  position: relative;
  z-index: 2;
}

/* 邀请收益区域 */
.invite-section {
  height: 190rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.invite-card {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.invite-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8rpx;
}

.invite-title {
  font-size: 30rpx;
  font-weight: 500;
  color: #333333;
}

.invite-detail {
  display: flex;
  align-items: center;
  gap: 4rpx;
}

.invite-detail-text {
  font-size: 20rpx;
  color: #9FA19D;
}

.invite-arrow {
  font-size: 20rpx;
  color: #9FA19D;
}

.invite-icons {
  flex: 1;
  display: flex;
  justify-content: space-around;
  align-items: center;
}

.invite-icon-item {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.invite-icon {
  width: 84rpx;
  height: 84rpx;
}

.invite-number {
  position: absolute;
  bottom: -5rpx;
  left: 50%;
  transform: translateX(-50%);
  background-color: rgba(0, 0, 0, 0.5);
  border-radius: 24rpx;
  padding: 0 15rpx;
  height: 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.invite-number-text {
  color: #FFFFFF;
  font-size: 24rpx;
  font-weight: 500;
}

/* 积分商城区域 */
.point-section {
  height: 190rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.point-card {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.point-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8rpx;
}

.point-title {
  font-size: 30rpx;
  font-weight: 500;
  color: #333333;
}

.point-arrow {
  font-size: 20rpx;
  color: #9FA19D;
}

.point-icons {
  flex: 1;
  display: flex;
  justify-content: space-around;
  align-items: center;
}

.point-icon-item {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.point-icon {
  width: 84rpx;
  height: 84rpx;
}

.point-number {
  position: absolute;
  bottom: -15rpx;
  left: 50%;
  transform: translateX(-50%);
  background-color: #F02B41;
  border-radius: 24rpx;
  padding: 0 15rpx;
  height: 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.point-number-text {
  color: #FFFFFF;
  font-size: 24rpx;
  font-weight: 500;
}

/* 标签页样式 */
.tab-section {
  padding: 20rpx 0;
  background-color: #FFFFFF;
  margin: 0 20rpx;
  border-radius: 20rpx;
}

.tab-section .tab-scroll {
  width: 100%;
  white-space: nowrap;
}

.tab-section .tab-list {
  display: flex;
  padding: 0 30rpx;
  height: 80rpx;
  width: 100%;
}

.tab-section .tab-list .tab-item {
  flex: 1;
  /* 平均分配空间 */
  font-size: 32rpx;
  position: relative;
  color: #333;
  display: flex;
  align-items: center;
  justify-content: center;
  /* 水平居中内容 */
  text-align: center;
}

.tab-section .tab-list .tab-item .tab-inner {
  position: relative;
  padding-bottom: 20rpx;
}

.tab-section .tab-list .tab-item .tab-inner .tab-indicator {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 8rpx;
  background: linear-gradient(to right, #89C4D0, #DCFF6A);
  border-radius: 4rpx;
}

.tab-section .tab-list .tab-item.active {
  font-weight: 500;
  color: #000;
}

/* 标签内容区域 */
.tab-content-section {
  padding: 20rpx;
}

.tab-content-section .placeholder-content {
  padding: 60rpx 0;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #FFFFFF;
  border-radius: 20rpx;
}

.tab-content-section .placeholder-content .placeholder-text {
  font-size: 28rpx;
  color: #999999;
}

.tab-content-section .recommend-section .product-big-card {
  position: relative;
  border-radius: 10px;
  margin: 0 20rpx 20px;
  overflow: hidden;
  height: 140px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.tab-content-section .recommend-section .product-card-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to right, #DAF2FA, #F7FCFE);
  border-radius: 10px;
  z-index: 0;
}

.tab-content-section .recommend-section .product-card-content {
  position: relative;
  z-index: 1;
  display: flex;
  height: 100%;
}

.tab-content-section .recommend-section .product-image-container {
  position: relative;
  width: 120px;
  height: 120px;
  margin: 10px;
}

.tab-content-section .recommend-section .product-image-container .product-big-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 6px;
}

.tab-content-section .recommend-section .product-image-container .product-tags {
  position: absolute;
  top: 0px;
  left: -10px;
  /* 向左偏移，使标签在图片外 */
  display: flex;
  flex-direction: column;
  /* 改为垂直布局 */
  z-index: 2;
}

.tab-content-section .recommend-section .product-image-container .product-tags .product-tag {
  position: relative;
  padding: 2px 6px;
  color: #000000;
  font-size: 10px;
  font-weight: 500;
  margin-right: 5px;
  margin-bottom: 2px;
  /* 增加底部间距 */
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen, Ubuntu, Cantarell, "Open Sans", "Helvetica Neue", sans-serif;
  height: 32px;
  /* 固定高度 */
  width: 32px;
  /* 固定宽度 */
  display: flex;
  align-items: center;
  justify-content: center;
}

.tab-content-section .recommend-section .product-image-container .product-tags .product-tag::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url("https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/tag_bg.png");
  background-size: 100% 100%;
  z-index: -1;
}

.tab-content-section .recommend-section .product-image-container .product-tags .product-tag.new::before, .tab-content-section .recommend-section .product-image-container .product-tags .product-tag.recommend::before {
  background-image: url("https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/tag_bg.png");
}

.tab-content-section .recommend-section .product-big-card .product-info-container {
  padding: 10px;
  position: relative;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.tab-content-section .recommend-section .product-big-card .product-info-container .product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.tab-content-section .recommend-section .product-big-card .product-info-container .product-info .product-title {
  font-size: 18px;
  color: #000000;
  font-weight: 500;
  line-height: 1.3;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen, Ubuntu, Cantarell, "Open Sans", "Helvetica Neue", sans-serif;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  text-overflow: ellipsis;
}

.tab-content-section .recommend-section .product-big-card .product-info-container .product-info .product-desc {
  font-size: 12px;
  color: #333333;
  margin-top: 4px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen, Ubuntu, Cantarell, "Open Sans", "Helvetica Neue", sans-serif;
  font-weight: 400;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 1;
  overflow: hidden;
  text-overflow: ellipsis;
}

.tab-content-section .recommend-section .product-big-card .product-info-container .product-info .product-price-area {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
  position: relative;
}

.tab-content-section .recommend-section .product-big-card .product-info-container .product-info .product-price-area .price-tag-bg {
  height: 50px;
  width: 215px;
  object-fit: contain;
}

.tab-content-section .recommend-section .product-big-card .product-info-container .product-info .product-price-area .product-price {
  position: absolute;
  bottom: 3px;
  left: 90px;
  /* 增加左边距 */
  font-size: 12px;
  font-weight: 400;
  color: #000000;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen, Ubuntu, Cantarell, "Open Sans", "Helvetica Neue", sans-serif;
  /* 价格数字加粗且字体更大 */
}

.tab-content-section .recommend-section .product-big-card .product-info-container .product-info .product-price-area .product-price .price-number {
  font-size: 22px;
  font-weight: 500;
}

.product-price-area {
  position: relative;
}
.product-category-name {
  position: absolute;
  left: 12px;
  bottom: 8px;
  z-index: 2;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}
.category-text {
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  line-height: 1.2;
  white-space: pre-line;
  text-shadow: 0 2px 8px rgba(0,0,0,0.18);
}

/* 本地活动区域样式 */
.local-events-section {
  padding: 0;
  min-height: 500rpx;
  
  .top-events-container {
    margin-bottom: 30rpx;
    padding: 20rpx;
    width: 100%;
    box-sizing: border-box;
    
    .events-scroll-view {
      width: 100%;
      white-space: nowrap;
      padding: 0;
      
      .events-card-list {
        display: flex;
        padding: 10rpx 0;
        width: 2400rpx;
        /* 足够宽以容纳多个卡片 */
        
        .event-card {
          width: 240px;
          height: 180px;
          flex-shrink: 0;
          position: relative;
          border-radius: 20rpx;
          overflow: hidden;
          background-color: #f0f9fc;
          margin-right: 20rpx;
          display: inline-block;
          box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
          
          .event-bg-image {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }
          
          .event-overlay {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            padding: 20rpx;
            
            .event-content {
              height: 100%;
              display: flex;
              flex-direction: column;
              justify-content: flex-end;
              /* 将内容移至底部 */
              padding-bottom: 30rpx;
              /* 底部留出一些空间 */
              
              .event-title {
                font-size: 28rpx;
                font-weight: bold;
                color: #333;
                line-height: 1.4;
                margin-bottom: 8rpx;
              }
              
              .event-desc {
                font-size: 22rpx;
                font-weight: normal;
                color: #999999;
                margin-bottom: 12rpx;
              }
              
              .event-action-btn {
                font-size: 22rpx;
                color: #333;
                margin-top: 16rpx;
                display: inline-block;
                background-color: #FFFFFF;
                padding: 8rpx 20rpx;
                border-radius: 30rpx;
                align-self: flex-start;
                box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.1);
                font-weight: 500;
              }
            }
          }
        }
      }
    }
  }
  
  .events-list {
    padding: 0 20rpx 20rpx;
    width: 100%;
    box-sizing: border-box;
    
    .king-cup-card {
      position: relative;
      width: 100%;
      border-radius: 20rpx;
      overflow: hidden;
      margin-bottom: 30rpx;
      background: linear-gradient(to bottom right, #DAF2FA, #F7FCFE);
      box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
      padding: 20rpx;
      
      .king-cup-image {
        width: 100%;
        height: 316rpx;
        object-fit: cover;
        background-color: #eaeaea;
        border-radius: 16rpx;
        box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
      }
      
      .king-cup-footer {
        padding: 16rpx 10rpx;
        background-color: transparent;
        
        .king-cup-header {
          display: flex;
          align-items: center;
          margin-bottom: 12rpx;
          
          .king-cup-title {
            font-size: 32rpx;
            font-weight: 600;
            color: #000000;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            max-width: 85%;
          }
          
          .hot-badge {
            display: flex;
            align-items: center;
            background-color: transparent;
            border-radius: 30rpx;
            padding: 0;
            margin-left: 16rpx;
            
            .hot-icon {
              width: 158rpx;
              height: 48rpx;
              flex-shrink: 0;
              object-fit: contain;
            }
          }
        }
        
        .king-cup-desc {
          font-size: 28rpx;
          font-weight: 400;
          color: #666666;
          line-height: 1.5;
        }
      }
    }
  }
}

/* 精彩濠江区域样式 */
.haojiang-section {
  padding: 0 20rpx;
  
  .haojiang-title {
    display: flex;
    justify-content: center;
    align-items: center;
    margin: 30rpx 0;
    position: relative;
    
    .title-text, .subtitle-text {
      font-size: 32rpx;
      font-weight: normal;
      color: #000000;
      margin: 0 10rpx;
      position: relative;
      z-index: 1;
      font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen, Ubuntu, Cantarell, "Open Sans", "Helvetica Neue", sans-serif;
    }
  }
  
  .haojiang-features {
    display: flex;
    justify-content: space-between;
    margin: 40rpx 0;
    
    .feature-item {
      flex: 1;
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 0 10rpx;
      
      .feature-icon-container {
        width: 116rpx;
        height: 116rpx;
        border-radius: 50%;
        background: linear-gradient(#DAF2FA, #F7FCFE);
        margin-bottom: 15rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        position: relative;
        overflow: hidden;
        
        .feature-icon {
          width: 64rpx;
          height: 64rpx;
          z-index: 2;
        }
      }
      
      .feature-info {
        text-align: center;
        
        .feature-title {
          font-size: 24rpx;
          font-weight: 500;
          color: #000000;
          margin-bottom: 8rpx;
          display: block;
        }
        
        .feature-desc {
          font-size: 20rpx;
          color: #999999;
          line-height: 1.4;
          display: block;
        }
      }
    }
  }
  
  .haojiang-scenic-spots {
    margin-bottom: 30rpx;
    
    .scenic-spots-card {
      background: linear-gradient(to right, #DAF2FA, #F7FCFE);
      border-radius: 10px;
      overflow: hidden;
      padding: 20rpx;
      box-sizing: border-box;
      position: relative;
      height: 420rpx;
      margin-bottom: 30rpx;
      
      .spots-content {
        height: 316rpx;
        
        .spot-item {
          width: 100%;
          height: 100%;
          display: flex;
          flex-direction: column;
          align-items: center;
          
          .spot-image {
            width: 650rpx;
            height: 316rpx;
            border-radius: 8px;
            object-fit: cover;
          }
        }
      }
      
      .card-footer {
        display: flex;
        flex-direction: row;
        justify-content: space-between;
        align-items: center;
        margin-top: 20rpx;
        
        .spot-title {
          font-size: 28rpx;
          font-weight: 500;
          color: #000000;
          text-align: left;
          max-width: 80%;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
          text-shadow: none;
        }
        
        .spot-btn {
          position: static;
          width: 68px;
          height: 22px;
          font-size: 10px;
          color: #000000;
          background-color: #FFFFFF;
          padding: 4px 14px;
          border-radius: 10px;
          font-weight: 500;
          display: flex;
          justify-content: center;
          align-items: center;
          letter-spacing: 0px;
          font-family: "Source Han Sans CN", sans-serif;
          box-sizing: border-box;
          flex-shrink: 0;
        }
      }
    }
  }
  
  .haojiang-video {
    position: relative;
    width: 100%;
    height: 356rpx;
    border-radius: 10px;
    overflow: hidden;
    margin-bottom: 30rpx;
    
    .video-player {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }
  
  .placeholder-content {
    padding: 60rpx 0;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: #FFFFFF;
    border-radius: 20rpx;
    
    .placeholder-text {
      font-size: 28rpx;
      color: #999999;
    }
  }
}

/* 查看全部链接 */
.view-all-link {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 30rpx 0;
  
  text {
    font-size: 28rpx;
    color: #666666;
    position: relative;
    
    &::after {
      content: '';
      position: absolute;
      bottom: -6rpx;
      left: 0;
      right: 0;
      height: 2rpx;
      background-color: #666666;
    }
  }
}

/* 悬浮工具栏 */
.float-tools {
  position: fixed;
  right: 30rpx;
  bottom: 120rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 100;
  
  .float-item {
    width: 98rpx;
    height: 98rpx;
    margin-bottom: 20rpx;
    display: flex;
    justify-content: center;
    align-items: center;
    
    .float-icon {
      width: 98rpx;
      height: 98rpx;
    }
    
    .cart-icon {
      background-color: #DCFF6A;
    }
    
    text {
      font-size: 20rpx;
      color: #FFFFFF;
      background-color: #89C4D0;
      padding: 2rpx 8rpx;
      border-radius: 10rpx;
    }
    
    .contact-button {
      width: 100%;
      height: 100%;
      padding: 0;
      background-color: transparent;
      border-radius: 0;
      display: flex;
      justify-content: center;
      align-items: center;
      
      &::after {
        border: none;
      }
    }
  }
}

/* 动画样式 */
.alimama-dongfangdakai {
  font-family: "DongFangDaKai", sans-serif;
}

/* 媒体查询 */
@media screen and (min-width: 768px) {
  .banner-container {
    height: 800rpx;
  }
}

.tab-content-section .recommend-section .more-card {
  background-color: #FFFFFF;
  border-radius: 20rpx;
  margin-bottom: 20rpx;
  overflow: visible;
  /* 改为visible允许标签超出容器 */
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  width: 100%;
  height: 380rpx;
  position: relative;
}
.tab-content-section .recommend-section .more-card .more-header {
  position: relative;
  width: 100%;
  height: 280rpx;
  overflow: visible;
  /* 允许内容超出 */
}
.tab-content-section .recommend-section .more-card .more-header .new-tag-container {
  position: absolute;
  top: -5px;
  left: 50%;
  -webkit-transform: translateX(-50%);
          transform: translateX(-50%);
  width: 120px;
  height: 22px;
  z-index: 10;
  /* 提高z-index确保可见 */
  display: flex;
  justify-content: center;
  align-items: center;
}
.tab-content-section .recommend-section .more-card .more-header .new-tag {
  position: absolute;
  width: 100%;
  height: 100%;
  z-index: 2;
}
.tab-content-section .recommend-section .more-card .more-header .new-tag-text {
  position: relative;
  z-index: 3;
  color: #000000;
  font-size: 24rpx;
  font-weight: 600;
  text-align: center;
}
.tab-content-section .recommend-section .more-card .more-header .more-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.tab-content-section .recommend-section .more-card .more-footer {
  position: relative;
  height: 100rpx;
  background-image: url("https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/more-footer.png");
  background-size: cover;
  background-position: center;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10rpx 30rpx;
  padding-top: 20rpx;
  border-bottom-left-radius: 20rpx;
  border-bottom-right-radius: 20rpx;
  margin-top: -40rpx;
  /* 添加负边距，使footer往上移动与图片重叠 */
}
.tab-content-section .recommend-section .more-card .more-footer .product-name {
  color: #FFFFFF;
  font-size: 32rpx;
  font-weight: 500;
  margin-top: 5rpx;
  max-width: 80%;
  /* 限制宽度为footer宽度的60% */
  white-space: nowrap;
  /* 确保只显示一行 */
  overflow: hidden;
  /* 隐藏溢出部分 */
  text-overflow: ellipsis;
  /* 使用省略号 */
}
.tab-content-section .recommend-section .more-card .more-footer .more-btn {
  background-color: rgba(255, 255, 255, 0.8);
  color: #000000;
  font-size: 24rpx;
  padding: 8rpx 30rpx;
  border-radius: 40rpx;
  margin-top: 5rpx;
  white-space: nowrap;
  /* 确保按钮文字不换行 */
  flex-shrink: 0;
  /* 防止按钮被压缩 */
  min-width: 120rpx;
  /* 设置最小宽度 */
  text-align: center;
  /* 文字居中 */
}

.tab-content-section .recommend-section .product-grid {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}
.tab-content-section .recommend-section .product-grid .product-item {
  width: 48%;
  background-color: #EDF9FD;
  border-radius: 10px;
  margin-bottom: 20rpx;
  overflow: visible;
  /* 允许标签超出容器 */
  position: relative;
}
.tab-content-section .recommend-section .product-grid .product-item .item-tags {
  position: absolute;
  top: 0;
  left: -10px;
  /* 向左偏移，与大卡片保持一致 */
  display: flex;
  flex-direction: column;
  /* 保持垂直布局 */
  z-index: 10;
  /* 增加z-index确保显示在图片上层 */
}
.tab-content-section .recommend-section .product-grid .product-item .item-tags .item-tag {
  position: relative;
  padding: 2px 6px;
  color: #000000;
  font-size: 10px;
  font-weight: 500;
  margin-bottom: 2px;
  /* 标签之间的间距 */
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen, Ubuntu, Cantarell, "Open Sans", "Helvetica Neue", sans-serif;
  height: 32px;
  /* 固定高度与大卡片一致 */
  width: 32px;
  /* 固定宽度与大卡片一致 */
  display: flex;
  align-items: center;
  justify-content: center;
}
.tab-content-section .recommend-section .product-grid .product-item .item-tags .item-tag::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url("https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/tag_bg.png");
  background-size: 100% 100%;
  z-index: -1;
}
.tab-content-section .recommend-section .product-grid .product-item .item-tags .item-tag.new::before, .tab-content-section .recommend-section .product-grid .product-item .item-tags .item-tag.recommend::before {
  background-image: url("https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/tag_bg.png");
}
.tab-content-section .recommend-section .product-grid .product-item .item-image {
  width: 100%;
  height: auto;
  aspect-ratio: 1/1;
  /* 确保图片比例为1:1 */
  object-fit: cover;
  display: block;
  border-top-left-radius: 10px;
  /* 添加与卡片一致的上方圆角 */
  border-top-right-radius: 10px;
  /* 添加与卡片一致的上方圆角 */
}
.tab-content-section .recommend-section .product-grid .product-item .item-info {
  padding: 16rpx;
  position: relative;
}
.tab-content-section .recommend-section .product-grid .product-item .item-info .item-title {
  font-size: 28rpx;
  color: #000000;
  font-weight: 500;
  margin-bottom: 20px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}
.tab-content-section .recommend-section .product-grid .product-item .item-info .item-price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
}
.tab-content-section .recommend-section .product-grid .product-item .item-info .item-price-row .item-price {
  font-size: 26rpx;
  color: #000000;
}
.tab-content-section .recommend-section .product-grid .product-item .item-info .item-price-row .item-price .price-number {
  font-size: 42rpx;
  font-weight: 500;
}
.tab-content-section .recommend-section .product-grid .product-item .item-info .item-price-row .item-add {
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
  line-height: 1;
  /* 确保文本垂直居中 */
  text-align: center;
  /* 水平居中文本 */
}
.tab-content-section .recommend-section .product-grid .product-item .item-info .item-price-row .item-add text {
  display: inline-block;
  -webkit-transform: translateY(-1rpx);
          transform: translateY(-1rpx);
  /* 微调垂直位置 */
}

.scenic-spots-grid {
  margin-bottom: 30rpx;
}
.scenic-spots-grid .scenic-spots-card {
  background: linear-gradient(to right, #DAF2FA, #F7FCFE);
  border-radius: 10px;
  overflow: hidden;
  padding: 20rpx;
  box-sizing: border-box;
  position: relative;
}
.scenic-spots-grid .scenic-spots-card .spots-grid-content {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}
.scenic-spots-grid .scenic-spots-card .spots-grid-content .spot-grid-item {
  width: 31%;
  margin-bottom: 15rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.scenic-spots-grid .scenic-spots-card .spots-grid-content .spot-grid-item .grid-spot-image {
  width: 100%;
  height: 180rpx;
  border-radius: 8px;
  object-fit: cover;
  margin-bottom: 10rpx;
}
.scenic-spots-grid .scenic-spots-card .card-footer {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  margin-top: 20rpx;
}
.scenic-spots-grid .scenic-spots-card .card-footer .grid-spot-title {
  font-size: 28rpx;
  font-weight: 500;
  color: #000000;
  text-align: left;
}
.scenic-spots-grid .scenic-spots-card .card-footer .spot-btn {
  position: static;
  width: 68px;
  height: 22px;
  font-size: 10px;
  color: #000000;
  background-color: #FFFFFF;
  padding: 4px 14px;
  border-radius: 10px;
  font-weight: 500;
  display: flex;
  justify-content: center;
  align-items: center;
  letter-spacing: 0px;
  font-family: "Source Han Sans CN", sans-serif;
  box-sizing: border-box;
}

.global-popups-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1100;
  background-color: rgba(0, 0, 0, 0.4);
  -webkit-backdrop-filter: blur(4px);
          backdrop-filter: blur(4px);
}

/* 视频弹窗样式 */
.video-popup {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.85);
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
}

.video-popup-content {
  width: 100%;
  height: 56.25vw; /* 16:9 视频比例 */
  max-height: 80vh;
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
}

.popup-video-player {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.popup-close-btn {
  position: absolute;
  top: -60px;
  right: 20px;
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  color: #FFFFFF;
  font-size: 30px;
  z-index: 1001;
  /* 确保触摸区域足够大 */
  padding: 10px;
  box-sizing: content-box;
  margin: -10px;
}
</style>
