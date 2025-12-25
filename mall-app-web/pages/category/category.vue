<template>
  <view class="container" style="height:100vh;">
    <!-- ✅ 骨架屏：数据加载时显示 -->
    <skeleton-screen :visible="!dataLoaded" type="category" />

    <!-- 主要内容：数据加载完成后显示 -->
    <view v-show="dataLoaded" class="main-container">
    <!-- 顶部区域（包含导航栏、门店信息、搜索框的统一背景） -->
    <view class="top-section">
      <!-- 自定义导航栏 -->
      <nav-bar 
        :placeholder="false" 
        :bg-color="'rgba(221, 255, 153, 1)'" 
        :has-slot="true" 
        :titleCenter="true"
      >
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

      <!-- 门店信息和会员码区域 -->
      <view class="store-member-section" :style="{ marginTop: navBarHeight + 'px' }">
        <!-- 学校信息 -->
                  <view class="store-info-area" @tap="handleStoreSelect">
            <view class="store-avatar">
              <!-- ✅ 优化：使用缩略图 + 懒加载 -->
              <image
                class="store-logo"
                :src="getStoreThumbnail(getSchoolDisplayImage(selectedSchool))"
                mode="aspectFill"
                :lazy-load="true"
                @error="handleStoreLogoError"
              />
            </view>
            <view class="store-details">
              <image class="dropdown-arrow" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/category/arrow_down.png" mode="aspectFit" />
              <text class="store-name">{{ selectedSchool ? selectedSchool.schoolName : '选择学校' }}</text>
            </view>
          </view>
          
          <!-- 线下门店 -->
          <view class="offline-store-area" @tap="handleOfflineStoreClick">
            <image class="store-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/category/store.png" mode="aspectFit" />
            <text class="store-text">线下门店</text>
          </view>
      </view>

      <!-- 搜索区域 -->
      <view class="search-area" @tap="handleSearch">
        <image class="search-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/search_icon.svg" mode="aspectFit" />
        <text class="search-placeholder">搜索定制产品、大学</text>
        <view class="search-btn">
          <text class="search-btn-text">搜索</text>
        </view>
      </view>
    </view>
    
    <!-- 主体内容 -->
    <view class="main-content-wrapper">
      <!-- 左侧分类列表 -->
      <view class="left-sidebar-fixed">
        <view class="left-sidebar">
          <scroll-view
            class="category-scroll-view"
            scroll-y="true"
            show-scrollbar="false"
            enhanced="true"
            :scroll-with-animation="true"
          >
            <view class="category-list">
              <!-- 特惠商品选项（组合商品）- 仅当有组合商品时显示 -->
              <view
                v-if="hasBundleProducts"
                class="category-item special-category"
                :class="{ 'active': selectedItemId === -1 }"
                @tap="handleBundleClick"
              >
                <view class="category-icon-wrap">
                  <image
                    class="category-icon"
                    src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/category/all_product.png"
                  ></image>
                </view>
                <text class="category-name" :class="{ 'active-text': selectedItemId === -1 }">特惠商品</text>
              </view>

              <!-- 全部商品选项 -->
              <view
                class="category-item"
                :class="{ 'active': selectedItemId === 0 }"
                @tap="handleAllProductsClick"
              >
                <view class="category-icon-wrap">
                  <image
                    class="category-icon"
                    src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/category/all_product.png"
                  ></image>
                </view>
                <text class="category-name" :class="{ 'active-text': selectedItemId === 0 }">全部商品</text>
              </view>

              <!-- 分类列表 - 显示所有分类，移除数量限制 -->
              <view
                v-for="(item, index) in categoryList"
                :key="item.id"
                class="category-item"
                :class="{ 'active': selectedItemId === item.id }"
                @tap="handleCategoryClick(item)"
              >
                <view class="category-icon-wrap">
                  <image
                    class="category-icon"
                    :src="item.icon || 'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20190519/default.png'"
                  ></image>
                </view>
                <text class="category-name" :class="{ 'active-text': selectedItemId === item.id }">{{ item.name }}</text>
              </view>
            </view>
          </scroll-view>
        </view>
      </view>
      
      <!-- 右侧内容区域 -->
      <view class="right-content-container">
        <!-- 组合商品列表（特惠商品） -->
        <template v-if="selectedItemId === -1">
          <!-- 组合商品标题 -->
          <view class="bundle-header">
            <text class="bundle-header-title">特惠组合</text>
            <text class="bundle-header-desc">多件组合购买更优惠</text>
          </view>
          
          <!-- 组合商品列表 -->
          <scroll-view
            class="right-content-scroll"
            scroll-y="true"
            lower-threshold="100"
            @scrolltolower="handleBundleScrollToLower"
          >
            <view class="product-grid">
              <view 
                v-for="(item, index) in bundleList" 
                :key="item.id"
                class="product-card"
                @tap="navToBundleDetail(item)"
              >
                <!-- 组合商品图片 -->
                <view class="product-image-container">
                  <image
                    class="product-image"
                    :src="getProductThumbnail(item.pic)"
                    mode="aspectFit"
                    :lazy-load="true"
                  ></image>
                  <!-- 组合标签 -->
                  <view class="bundle-tag">
                    <text>组合</text>
                  </view>
                </view>
                
                <!-- 组合商品信息 -->
                <view class="product-info">
                  <text class="product-title">{{ item.name }}</text>
                  <view class="product-bottom">
                    <view class="price-purchase-row">
                      <text class="product-price">¥<text class="price-number">{{ item.bundlePrice }}</text></text>
                      <text class="purchase-count">{{ item.saleCount || 0 }}+购买</text>
                    </view>
                  </view>
                </view>
              </view>
            </view>
            
            <!-- 加载更多 -->
            <uni-load-more :status="bundleLoadingType" @click="loadBundleList"></uni-load-more>
          </scroll-view>
        </template>
        
        <!-- 普通商品列表 -->
        <template v-else>
          <!-- 二级分类 -->
          <scroll-view 
            class="sub-category" 
            scroll-x="true" 
            show-scrollbar="false"
            v-if="subCategories.length > 0"
          >
            <view class="sub-category-items">
              <view 
                v-for="item in subCategories" 
                :key="item.id"
                class="sub-category-item"
                :class="{ 'active': selectedSubCategoryId === item.id }"
                @tap="handleSubCategoryClick(item.id)"
              >
                <text class="sub-category-name">{{ item.name }}</text>
                <view class="sub-indicator" v-if="selectedSubCategoryId === item.id"></view>
              </view>
            </view>
          </scroll-view>
          
          <!-- 排序选项卡 -->
          <view class="sort-tabs">
          <view class="tab-items">
            <view 
              class="sort-tab-item" 
              :class="{ 'active': filterIndex === 0 }"
              @tap="tabClick(0)"
            >
              <text>综合</text>
              <view class="indicator" v-if="filterIndex === 0"></view>
            </view>
            <view 
              class="sort-tab-item" 
              :class="{ 'active': filterIndex === 1 }"
              @tap="tabClick(1)"
            >
              <text>新品</text>
              <view class="indicator" v-if="filterIndex === 1"></view>
            </view>
            <view 
              class="sort-tab-item" 
              :class="{ 'active': filterIndex === 2 }"
              @tap="tabClick(2)"
            >
              <text>销量</text>
              <view class="price-arrows">
                <image 
                  class="arrow-icon" 
                  :class="{ 'active': priceOrder === 1 && filterIndex === 2 }"
                  src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/category/arrow-up.png"
                ></image>
                <image 
                  class="arrow-icon" 
                  :class="{ 'active': priceOrder === 2 && filterIndex === 2 }"
                  src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/category/arrow-down.png"
                ></image>
              </view>
              <view class="indicator" v-if="filterIndex === 2"></view>
            </view>
            <view 
              class="sort-tab-item" 
              :class="{ 'active': filterIndex === 3 }"
              @tap="tabClick(3)"
            >
              <text>价格</text>
              <view class="price-arrows">
                <image 
                  class="arrow-icon" 
                  :class="{ 'active': priceOrder === 1 && filterIndex === 3 }"
                  src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/category/arrow-up.png"
                ></image>
                <image 
                  class="arrow-icon" 
                  :class="{ 'active': priceOrder === 2 && filterIndex === 3 }"
                  src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/category/arrow-down.png"
                ></image>
              </view>
              <view class="indicator" v-if="filterIndex === 3"></view>
            </view>
          </view>
          <!-- 切换视图模式 -->
          <view class="view-toggle" @tap="toggleViewMode">
            <image 
              class="view-mode-icon" 
              :src="isGridView ? 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/category/list-view.svg' : 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/category/grid-view.svg'"
            ></image>
          </view>
        </view>
        
        <!-- 商品列表 -->
        <scroll-view
          class="right-content-scroll"
          scroll-y="true"
          lower-threshold="100"
          @scrolltolower="handleScrollToLower"
          @scroll="handleRightScroll"
        >
          <view class="product-grid" :class="{ 'list-view': !isGridView }">
            <view 
              v-for="(item, index) in productList" 
              :key="index"
              class="product-card"
              @tap="navToDetailPage(item)"
            >
              <!-- 列表视图品牌名（已移除） -->
              
              <!-- 商品图片 -->
              <view class="product-image-container">
                <!-- 图片加载状态 -->
                <view class="image-placeholder" v-if="loadingImages[index] || errorImages[index]">
                  <view class="placeholder-error" v-if="errorImages[index]">
                    <text>加载失败</text>
                  </view>
                  <view class="placeholder-loading" v-else>
                    <view class="loading-spinner"></view>
                    <text>加载中...</text>
                  </view>
                </view>
                
                <!-- ✅ 优化：使用缩略图 + 懒加载 -->
                <image
                  class="product-image"
                  :style="'opacity:'+(!loadingImages[index] && !errorImages[index] ? 1 : 0)+';' + (item.stock <= 0 ? 'filter: grayscale(100%);' : '')"
                  :src="getProductThumbnail(item.pic)"
                  mode="aspectFit"
                  :lazy-load="true"
                  @load="handleImageLoad(index)"
                  @error="handleImageError(index)"
                ></image>
                
                <!-- 售罄蒙版 -->
                <view class="sold-out-mask" v-if="item.stock <= 0">
                  <text class="sold-out-text">已售罄</text>
                </view>
                
                <!-- 商品标签（已移除） -->
                
                <!-- 网格视图品牌名（已移除） -->
              </view>
              
              <!-- 商品信息 -->
              <view class="product-info">
                <text class="product-title">{{ item.name }}</text>
                <view class="product-bottom">
                  <view class="price-purchase-row">
                    <text class="product-price">¥<text class="price-number">{{ item.price }}</text></text>
                    <text class="purchase-count">{{ (item.sale || 0) }}+购买</text>
                  </view>
                </view>
              </view>
            </view>
          </view>
          
          <!-- 加载更多 -->
          <uni-load-more :status="loadingType" @click="loadProducts"></uni-load-more>
        </scroll-view>
        </template>
      </view>
    </view>
    

    
    <!-- 优惠券弹窗 -->
    <coupon-popup
      :show="showCouponPopup"
      :couponData="couponData"
      @updateShow="showCouponPopup = $event"
      @getCoupon="handleGetCoupon"
      @close="closeCouponPopup"
    ></coupon-popup>

    <!-- 会员码弹窗 -->
    <member-qr-popup
      :show="showMemberQRPopup"
      :memberData="memberQRData"
      @updateShow="showMemberQRPopup = $event"
      @close="closeMemberQRPopup"
    ></member-qr-popup>

    <!-- 门店选择弹窗（修改为学校选择） -->
    <store-selector
      :visible="showStoreSelector"
      :current-store="selectedStore"
      :school-groups="schoolGroups"
      :selected-school-id="selectedSchool ? selectedSchool.id : null"
      @select="handleStoreSelected"
      @close="handleStoreSelectorClose"
    />

    <!-- 购物车悬浮窗 -->
    <shopping-cart
      :cart-count="cartCount"
      icon-src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/shoping-cart.png"
      @click-cart="handleCartClick"
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
import uniLoadMore from '@/components/uni-load-more/uni-load-more.vue';
import couponPopup from '@/components/CouponPopup';
import MemberQRPopup from '@/components/MemberQRPopup';
import NavBar from '@/components/nav-bar.vue';
import StoreSelector from '@/components/store/StoreSelector.vue';
import ShoppingCart from '@/components/shoppingCart.vue';
import SkeletonScreen from '@/components/SkeletonScreen.vue';
// import FullScreenLoading from '@/components/FullScreenLoading.vue';
import { fetchProductCateList } from '@/api/home.js';
import { searchProductList } from '@/api/product.js';
import { fetchCouponList, addMemberCoupon } from '@/api/coupon.js';
import { getMemberQRCode } from '@/api/member.js';
import { fetchStoreGroupsBySchool } from '@/api/store.js';
import { fetchBundleList } from '@/api/bundle.js';
import cacheManager, { CACHE_KEYS, CACHE_EXPIRE_TIME } from '@/utils/cacheManager.js';
import { lazyLoadUtils } from '@/utils/lazyLoad.js';

export default {
  components: {
    uniLoadMore,
    'nav-bar': NavBar,
    couponPopup,
    'member-qr-popup': MemberQRPopup,
    StoreSelector,
    'shopping-cart': ShoppingCart,
    'skeleton-screen': SkeletonScreen,
    // 'full-screen-loading': FullScreenLoading
  },
  data() {
    return {
      // 导航和门店信息
      navBarHeight: 0,
      statusBarHeight: 0,
      selectedSchool: null, // 选中的学校信息
      selectedStore: null, // 选中的门店信息（保留兼容性）
      showStoreSelector: false, // 是否显示门店选择弹窗
      schoolGroups: [], // 门店分组数据

      // 登录状态
      hasLogin: false,

      // 购物车
      cartCount: 0, // 购物车商品数量

      // 分类数据
      categoryList: [],
      selectedItemId: -1, // 默认选中特惠商品
      subCategories: [],
      selectedSubCategoryId: 0,
      
      // 商品列表
      productList: [],
      page: 1,
      pageSize: 10,
      loadingType: 'more',
      hasMore: true,
      
      // 组合商品列表（特惠商品）
      bundleList: [],
      bundlePage: 1,
      bundlePageSize: 10,
      bundleLoadingType: 'more',
      bundleHasMore: true,
      hasBundleProducts: false, // 是否有组合商品（用于控制特惠商品选项显示）
      
      // 筛选和排序
      filterIndex: 0,   // 0: 综合, 1: 新品, 2: 销量, 3: 价格
      priceOrder: 0,    // 0: 默认, 1: 升序, 2: 降序
      
      // 视图模式
      isGridView: true,

      // 数据加载状态
      dataLoaded: false, // 数据是否已加载完成

      // 图片加载状态
      loadingImages: [],
      errorImages: [],
      
      // 优惠券弹窗（保留以支持现有功能）
      showCouponPopup: false,
      couponData: {
        amount: '0',
        title: '优惠券',
        desc: '暂无可用优惠券',
        footerText: '优惠详情'
      },
      
      // 会员码弹窗
      showMemberQRPopup: false,
      memberQRData: {
        memberCode: '',
        memberName: '',
        memberAvatar: '',
        qrCodeBase64: ''
      },

      // 全屏加载状态
      // showFullScreenLoading: false, // 是否显示全屏加载
      // loadingText: '加载中...', // 加载提示文字
      // imageLoadCount: 0, // 已加载的图片数量
      // totalImageCount: 0, // 总图片数量
      // loadingTimeout: null, // 加载超时定时器

      // 性能优化相关
      lastSchoolChangeCheck: 0, // 上次检查学校变化的时间戳
      schoolChangeDebounceTime: 500, // 学校变化检查防抖时间（毫秒）
      storeGroupsLoaded: false, // 门店分组是否已加载
    };
  },
  computed: {
    // 当前分类
    currentCategory() {
      if (this.categoryList.length === 0) {
        return {};
      }
      return this.categoryList[this.selectedItemId];
    }
  },
  onLoad(options) {
    // 获取系统信息（同步，快速执行）
    const systemInfo = uni.getSystemInfoSync();
    this.statusBarHeight = systemInfo.statusBarHeight;
    // 在小程序中，导航栏高度通常是44px
    this.navBarHeight = this.statusBarHeight + 40;

    // 检查登录状态（同步，快速执行）
    this.checkLoginStatus();

    // 获取已选择的门店信息（同步，快速执行）
    this.loadSelectedStore();

    // 初始化Vuex中的学校状态（在loadSelectedStore之后）
    this.$store.commit('setSelectedSchool', this.selectedSchool);

    // 检查是否有传入的分类ID参数
    if (options && options.categoryId) {
      const categoryId = parseInt(options.categoryId);
      console.log('从URL参数接收到分类ID:', categoryId);
      // 保存到Vuex，在分类列表加载完成后自动选中
      this.$store.commit('setSelectedCategoryId', categoryId);
    }

    // 立即渲染页面结构，然后分批加载数据
    this.$nextTick(() => {
      this.loadCategoryDataInBatches();
    });
  },
  onShow() {
    // 更新登录状态
    this.checkLoginStatus();

    // 重新加载门店信息（从门店选择页面返回时可能有更新）
    this.loadSelectedStore();

    // ✅ 优化：移除每次 onShow 都调用 loadStoreGroups()
    // 只在门店分组未加载时才加载（通常只在首次进入时）
    if (!this.storeGroupsLoaded) {
      this.loadStoreGroups();
    }

    // ✅ 优化：使用防抖机制检测学校变化
    this.checkSchoolChangeDebounced();

    // 检查 Vuex 中是否有选中的分类ID（仅在分类列表已加载时处理）
    // 注意：第一次进入时，分类列表可能还在加载中，会在 loadCategoryList 完成后处理
    const selectedCategoryId = this.$store.state.selectedCategoryId;
    if (selectedCategoryId !== null && selectedCategoryId !== undefined && this.categoryList.length > 0) {
      // 特殊处理：-1 表示特惠商品（组合商品）
      if (selectedCategoryId === -1) {
        if (this.selectedItemId !== -1) {
          console.log('🎯 onShow中检测到待切换到特惠商品');
          this.selectedItemId = -1;
          this.loadBundleList();
        }
        // 使用完后清空 store 中的 selectedCategoryId
        this.$store.commit('setSelectedCategoryId', null);
      } else {
        // 查找分类是否存在
        const findIndex = this.categoryList.findIndex(item => item.id === selectedCategoryId);

        // 只有在分类存在且与当前选中分类不同时才切换
        if (findIndex !== -1 && this.selectedItemId !== selectedCategoryId) {
          console.log('🎯 onShow中检测到待切换的分类ID:', selectedCategoryId);
          // 更新选中的分类
          this.selectedItemId = selectedCategoryId;

          // 加载二级分类
          this.loadSubCategories(selectedCategoryId);

          // loadSubCategories会重置商品列表状态并加载商品
        }

        // 使用完后清空 store 中的 selectedCategoryId
        this.$store.commit('setSelectedCategoryId', null);
      }
    }
  },

  onPullDownRefresh() {
    this.refreshProductList();
  },

  // 分享给朋友
  onShareAppMessage(res) {
    return {
      title: '广横走文创 - 精选商品分类',
      path: '/pages/category/category',
      imageUrl: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/share/category_share.jpg'
    }
  },
  // 分享到朋友圈
  onShareTimeline() {
    return {
      title: '广横走文创 - 精选商品分类',
      query: ''
    }
  },
  methods: {
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

    // 加载已选择的学校信息
    loadSelectedStore() {
      try {
        // 优先加载学校信息
        const schoolInfo = uni.getStorageSync('selectedSchool');
        if (schoolInfo) {
          this.selectedSchool = JSON.parse(schoolInfo);
          // 同步更新Vuex状态
          this.$store.commit('setSelectedSchool', this.selectedSchool);
          return;
        }

        // 兼容旧的门店信息，从中提取学校信息
        const storeInfo = uni.getStorageSync('selectedStore');
        if (storeInfo) {
          const store = JSON.parse(storeInfo);
          this.selectedStore = store;
          if (store.schoolId && store.schoolName) {
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
        // 清空Vuex状态
        this.$store.commit('setSelectedSchool', null);
      }
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

    // 处理门店选择
    handleStoreSelect() {
      this.showStoreSelector = true;
    },

    // 处理线下门店点击
    handleOfflineStoreClick() {
      let url = '/pages/store/storeList';

      // 如果已选择学校，传递学校参数
      if (this.selectedSchool && this.selectedSchool.id) {
        const schoolName = this.selectedSchool.schoolName || '';
        url += `?schoolId=${this.selectedSchool.id}&schoolName=${encodeURIComponent(schoolName)}`;
      }

      // 跳转到门店列表页面
      uni.navigateTo({
        url: url
      });
    },
    
    // 处理搜索
    handleSearch() {
      uni.navigateTo({
        url: '/pages/search/search'
      });
    },
    
    // 处理门店logo加载错误
    handleStoreLogoError() {
      // 当门店logo加载失败时，这里可以做一些处理
      console.log('门店logo加载失败，已使用默认logo');
    },

    // 处理学校选择确认（StoreSelector组件现在返回学校信息）
    handleStoreSelected(school) {
      // 保存选择的学校信息
      this.selectedSchool = school;
      this.saveSelectedSchool(school);

      // 更新Vuex中的学校状态
      this.$store.commit('setSelectedSchool', school);

      // 学校变化后重新获取数据
      this.refreshProductList();

      // 使用简洁的Toast提示
      uni.showToast({
        title: '学校选择成功',
        icon: 'success',
        duration: 1500
      });
    },

    // 处理门店选择弹窗关闭
    handleStoreSelectorClose() {
      this.showStoreSelector = false;
    },

    // 保存选中的学校
    saveSelectedSchool(schoolInfo) {
      try {
        this.selectedSchool = schoolInfo;
        uni.setStorageSync('selectedSchool', JSON.stringify(schoolInfo));
        console.log('学校信息已保存:', schoolInfo);

        // 更新Vuex中的学校状态
        this.$store.commit('setSelectedSchool', schoolInfo);
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
          console.log('🔄 检测到学校变化，重新获取商品数据', {
            currentSchoolId,
            lastSchoolId
          });

          // 更新最后记录的学校ID
          this.$store.commit('updateLastSchoolId', currentSchoolId);

          // ✅ 优化：清除相关缓存
          cacheManager.invalidateByPrefix(CACHE_KEYS.PRODUCT_LIST);
          cacheManager.invalidateByPrefix(CACHE_KEYS.SUB_CATEGORY);

          // 重新获取商品数据（静默更新，不显示加载蒙版）
          this.refreshProductList(false);
        }
      } catch (error) {
        console.error('检测学校变化失败:', error);
      }
    },

    // 保存选中的门店（保留兼容性）
    saveSelectedStore(storeInfo) {
      try {
        this.selectedStore = storeInfo;
        uni.setStorageSync('selectedStore', JSON.stringify(storeInfo));
        console.log('门店信息已保存:', storeInfo);
      } catch (error) {
        console.error('保存门店信息失败:', error);
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
          false // 不持久化到本地存储
        );

        this.schoolGroups = schoolGroups;
        this.storeGroupsLoaded = true; // 标记已加载
        console.log('✅ 门店分组数据加载成功，共', schoolGroups.length, '个学校');

      } catch (error) {
        console.error('💥 加载门店分组失败:', error);

        // 详细错误信息分析
        if (error.message && error.message.includes('empty fetchedData')) {
          console.error('🔍 检测到 empty fetchedData 错误，可能原因:');
          console.error('1. 门店分组接口返回数据为空');
          console.error('2. 网络请求失败');
          console.error('3. 服务器响应异常');
        }

        this.schoolGroups = [];
        this.storeGroupsLoaded = false; // 标记加载失败，下次重试

        uni.showToast({
          title: error.message || '网络错误',
          icon: 'none'
        });
      }
    },

    
    // 优惠券弹窗相关方法（保留以支持现有功能）
    handleGetCoupon() {
      if (!this.hasLogin) {
        uni.navigateTo({
          url: '/pages/public/register'
        });
        return;
      }
      
      uni.showToast({
        title: '功能开发中',
        icon: 'none'
      });
      
      this.showCouponPopup = false;
    },
    
    closeCouponPopup() {
      this.showCouponPopup = false;
    },

    // 会员码弹窗相关方法
    async showMemberQRCode() {
      if (!this.hasLogin) {
        uni.navigateTo({
          url: '/pages/public/register'
        });
        return;
      }

      try {
        const result = await getMemberQRCode();
        if (result && result.data) {
          this.memberQRData = result.data;
          this.showMemberQRPopup = true;
        } else {
          uni.showToast({
            title: '获取会员码失败',
            icon: 'none'
          });
        }
      } catch (error) {
        console.error('获取会员码失败', error);
        uni.showToast({
          title: '获取会员码失败',
          icon: 'none'
        });
      }
    },

    closeMemberQRPopup() {
      this.showMemberQRPopup = false;
    },

    // 分批加载分类页数据，避免阻塞渲染
    async loadCategoryDataInBatches() {
      try {
        // 第一批：加载门店分组数据（非关键数据，失败不影响分类加载）
        this.loadStoreGroups().catch(err => {
          console.warn('门店分组加载失败，不影响分类显示:', err);
        });

        // 第二批：加载分类数据（关键数据，带重试机制）
        setTimeout(async () => {
          let retryCount = 0;
          const maxRetries = 2;
          
          while (retryCount <= maxRetries) {
            try {
              await this.loadCategoryList();
              // 加载成功，跳出循环
              break;
            } catch (error) {
              retryCount++;
              console.error(`分类列表加载失败 (${retryCount}/${maxRetries + 1}):`, error);
              
              if (retryCount <= maxRetries) {
                // 清除缓存后重试
                cacheManager.delete(CACHE_KEYS.CATEGORY_LIST);
                // 延迟后重试
                await new Promise(resolve => setTimeout(resolve, 500 * retryCount));
              } else {
                // 达到最大重试次数，使用降级处理
                console.error('❌ 分类列表加载失败，已达最大重试次数');
                this.categoryList = [];
                this.selectedItemId = 0;
                // 尝试直接加载全部商品
                this.loadProducts();
              }
            }
          }
          // 标记数据加载完成
          this.dataLoaded = true;
        }, 50);

      } catch (error) {
        console.error('分批加载分类数据失败:', error);
        // 降级处理：直接加载基础数据
        this.loadBasicCategoryData();
      }
    },

    // 降级加载基础分类数据
    async loadBasicCategoryData() {
      try {
        await this.loadCategoryList();
        this.dataLoaded = true;
      } catch (error) {
        console.error('基础分类数据加载失败:', error);
        // 即使分类加载失败，也要显示页面结构
        this.categoryList = [];
        this.selectedItemId = 0;
        this.dataLoaded = true; // 标记为已加载，避免一直显示加载状态
      }
    },

    // 重试加载分类列表（清除缓存后重新加载）
    async retryCategoryLoad() {
      console.log('🔄 重试加载分类列表...');
      try {
        // 清除分类相关缓存
        cacheManager.delete(CACHE_KEYS.CATEGORY_LIST);
        
        // 延迟500ms后重试，避免网络抖动
        await new Promise(resolve => setTimeout(resolve, 500));
        
        // 重新加载分类列表
        await this.loadCategoryList();
        console.log('✅ 分类列表重试加载成功');
      } catch (error) {
        console.error('❌ 分类列表重试加载失败:', error);
        // 重试失败，使用降级处理
        this.categoryList = [];
        this.selectedItemId = 0;
        // 尝试加载全部商品
        this.loadProducts();
      }
    },

    // ✅ 优化：加载分类数据（带缓存）
    async loadCategoryList() {
      try {
        // 使用缓存机制，15分钟过期
        const categoryList = await cacheManager.getOrSet(
          CACHE_KEYS.CATEGORY_LIST,
          async () => {
            console.log('🌐 从服务器获取分类列表');
            const result = await fetchProductCateList(0);

            if (result && result.data && Array.isArray(result.data) && result.data.length > 0) {
              return result.data;
            } else if (result && result.data && Array.isArray(result.data)) {
              // 返回空数组，不缓存
              console.warn('⚠️ 分类列表为空');
              throw new Error('分类列表为空');
            } else {
              throw new Error('获取分类列表失败');
            }
          },
          CACHE_EXPIRE_TIME.LONG, // 15分钟缓存
          false
        );

        // 确保返回的是有效数组
        if (!categoryList || !Array.isArray(categoryList) || categoryList.length === 0) {
          console.warn('⚠️ 缓存的分类列表无效，清除缓存');
          cacheManager.delete(CACHE_KEYS.CATEGORY_LIST);
          throw new Error('分类列表数据无效');
        }

        this.categoryList = categoryList;

        if (this.categoryList.length > 0) {
          // 检查 Vuex 中是否有待切换的分类ID（从轮播图跳转过来）
          const selectedCategoryId = this.$store.state.selectedCategoryId;

          if (selectedCategoryId !== null && selectedCategoryId !== undefined) {
            // 特殊处理：-1 表示特惠商品（组合商品）
            if (selectedCategoryId === -1) {
              console.log('🎯 检测到待切换到特惠商品');
              // 检查是否有组合商品
              if (this.hasBundleProducts) {
                this.selectedItemId = -1;
                // 加载组合商品列表
                this.loadBundleList();
              } else {
                // 没有组合商品，先检查一下
                this.checkBundleProducts().then(() => {
                  if (this.hasBundleProducts) {
                    this.selectedItemId = -1;
                    this.loadBundleList();
                  } else {
                    // 确实没有组合商品，使用默认逻辑
                    this.loadDefaultCategory();
                  }
                });
              }
              // 使用完后清空 store 中的 selectedCategoryId
              this.$store.commit('setSelectedCategoryId', null);
            } else {
              // 查找分类是否存在
              const findIndex = this.categoryList.findIndex(item => item.id === selectedCategoryId);

              if (findIndex !== -1) {
                console.log('🎯 检测到待切换的分类ID:', selectedCategoryId);
                // 更新选中的分类
                this.selectedItemId = selectedCategoryId;

                // 清空二级分类（loadSubCategories会重新加载）
                this.subCategories = [];
                this.selectedSubCategoryId = 0;

                // 加载该分类的二级分类和商品
                this.loadSubCategories(selectedCategoryId);

                // 使用完后清空 store 中的 selectedCategoryId
                this.$store.commit('setSelectedCategoryId', null);
              } else {
                console.warn('⚠️ 未找到对应的分类ID:', selectedCategoryId);
                // 分类不存在，使用默认逻辑
                this.loadDefaultCategory();
              }
            }
          } else {
            // 没有待切换的分类，使用默认逻辑
            this.loadDefaultCategory();
          }
        }

        console.log('✅ 分类列表加载成功，共', categoryList.length, '个分类');

      } catch (error) {
        console.error('获取分类列表失败', error);
        uni.showToast({
          title: '获取分类列表失败',
          icon: 'none'
        });
      }
    },

    // 加载默认分类（先检查是否有组合商品）
    async loadDefaultCategory() {
      // 清空二级分类
      this.subCategories = [];
      this.selectedSubCategoryId = 0;

      // 重置组合商品列表状态
      this.resetBundleState();

      // 先预检查是否有组合商品
      try {
        const params = {
          pageNum: 1,
          pageSize: 1
        };
        if (this.selectedSchool && this.selectedSchool.id) {
          params.schoolId = this.selectedSchool.id;
        }
        const result = await fetchBundleList(params);
        
        if (result && result.data && result.data.total > 0) {
          // 有组合商品，选中特惠商品
          this.hasBundleProducts = true;
          this.selectedItemId = -1;
          // 重新加载完整的组合商品列表
          this.resetBundleState();
          this.loadBundleList();
        } else {
          // 没有组合商品，选中全部商品
          this.hasBundleProducts = false;
          this.selectedItemId = 0;
          this.loadProducts();
        }
      } catch (error) {
        console.error('预检查组合商品失败', error);
        // 出错时默认选中全部商品
        this.hasBundleProducts = false;
        this.selectedItemId = 0;
        this.loadProducts();
      }
    },
    
    // ✅ 优化：加载二级分类（带缓存）
    async loadSubCategories(parentId) {
      try {
        const cacheKey = `${CACHE_KEYS.SUB_CATEGORY}${parentId}`;

        // 使用缓存机制，15分钟过期
        const subCategories = await cacheManager.getOrSet(
          cacheKey,
          async () => {
            console.log('🌐 从服务器获取二级分类，parentId:', parentId);
            const result = await fetchProductCateList(parentId);

            if (result && result.data) {
              return result.data;
            }
            return [];
          },
          CACHE_EXPIRE_TIME.LONG, // 15分钟缓存
          false
        );

        if (subCategories && subCategories.length > 0) {
          this.subCategories = subCategories;
          // 默认选中第一个二级分类
          this.selectedSubCategoryId = this.subCategories[0].id;
        } else {
          this.subCategories = [];
          // 没有二级分类，清空选中的二级分类ID
          this.selectedSubCategoryId = 0;
        }

        // 重置分页和商品列表
        this.resetProductState();

        // 加载商品列表
        this.loadProductList();

        console.log('✅ 二级分类加载成功，共', subCategories.length, '个');

      } catch (error) {
        console.error('获取二级分类失败', error);
        this.subCategories = [];
        this.selectedSubCategoryId = 0;

        // 即使获取二级分类失败，仍然加载一级分类的商品
        this.resetProductState();
        this.loadProductList();
      }
    },
    
    // 重置商品列表状态
    resetProductState() {
      // 重置分页和商品列表
      this.page = 1;
      this.productList = [];
      this.loadingImages = [];
      this.errorImages = [];
      this.loadingType = 'more';
      this.hasMore = true;
    },
    
    // 处理特惠商品（组合商品）点击
    handleBundleClick() {
      if (this.selectedItemId === -1) return;

      // 更新选中的分类为特惠商品
      this.selectedItemId = -1;

      // 清空二级分类
      this.subCategories = [];
      this.selectedSubCategoryId = 0;

      // 重置组合商品列表状态
      this.resetBundleState();

      // 加载组合商品列表
      this.loadBundleList();
    },

    // 重置组合商品列表状态
    resetBundleState() {
      this.bundlePage = 1;
      this.bundleList = [];
      this.bundleLoadingType = 'more';
      this.bundleHasMore = true;
    },

    // 检查是否有组合商品（用于从轮播图跳转时的预检查）
    async checkBundleProducts() {
      try {
        const params = {
          pageNum: 1,
          pageSize: 1
        };
        if (this.selectedSchool && this.selectedSchool.id) {
          params.schoolId = this.selectedSchool.id;
        }
        const result = await fetchBundleList(params);
        if (result && result.data && result.data.list && result.data.list.length > 0) {
          this.hasBundleProducts = true;
        } else {
          this.hasBundleProducts = false;
        }
      } catch (error) {
        console.error('检查组合商品失败', error);
        this.hasBundleProducts = false;
      }
    },

    // 加载组合商品列表
    async loadBundleList() {
      if (this.bundleLoadingType === 'loading') return;

      this.bundleLoadingType = 'loading';

      try {
        const params = {
          pageNum: this.bundlePage,
          pageSize: this.bundlePageSize
        };

        // 添加学校ID参数
        if (this.selectedSchool && this.selectedSchool.id) {
          params.schoolId = this.selectedSchool.id;
        }

        const result = await fetchBundleList(params);

        if (result && result.data) {
          const { list, total } = result.data;

          if (list && list.length > 0) {
            this.bundleList = [...this.bundleList, ...list];
            this.hasBundleProducts = true; // 有组合商品

            // 判断是否还有更多数据
            if (this.bundleList.length >= total) {
              this.bundleHasMore = false;
              this.bundleLoadingType = 'nomore';
            } else {
              this.bundleHasMore = true;
              this.bundleLoadingType = 'more';
            }

            this.bundlePage++;
          } else {
            this.bundleHasMore = false;
            this.bundleLoadingType = 'nomore';
            
            // 第一页就没有数据，说明没有组合商品
            if (this.bundlePage === 1) {
              this.hasBundleProducts = false;
              // 如果当前选中的是特惠商品，自动切换到全部商品
              if (this.selectedItemId === -1) {
                this.handleAllProductsClick();
              }
            }
          }
        } else {
          throw new Error('获取组合商品列表失败');
        }
      } catch (error) {
        console.error('获取组合商品列表失败', error);
        // 加载失败时，如果是第一页，也认为没有组合商品
        if (this.bundlePage === 1) {
          this.hasBundleProducts = false;
          if (this.selectedItemId === -1) {
            this.handleAllProductsClick();
          }
        }
        this.bundleLoadingType = 'more';
      }
    },

    // 处理组合商品列表滚动到底部
    handleBundleScrollToLower() {
      if (this.bundleHasMore && this.bundleLoadingType === 'more') {
        this.loadBundleList();
      }
    },

    // 跳转到组合商品详情页
    navToBundleDetail(item) {
      uni.navigateTo({
        url: `/pages/product/bundleDetail?id=${item.id}`
      });
    },

    // 处理全部商品点击
    handleAllProductsClick() {
      if (this.selectedItemId === 0) return;

      // 更新选中的分类为全部商品
      this.selectedItemId = 0;

      // 清空二级分类
      this.subCategories = [];
      this.selectedSubCategoryId = 0;

      // 重置商品列表状态
      this.resetProductState();

      // 加载所有商品
      this.loadProductList();
    },

    // 处理分类点击
    handleCategoryClick(item) {
      if (this.selectedItemId === item.id) return;

      // 更新选中的分类
      this.selectedItemId = item.id;

      // 加载二级分类
      this.loadSubCategories(item.id);

      // 在loadSubCategories中会重置商品列表并加载数据，这里不需要重复操作
    },
    
    // 处理二级分类点击
    handleSubCategoryClick(subCategoryId) {
      if (this.selectedSubCategoryId === subCategoryId) {
        // 如果点击已选中的分类，则取消选中
        this.selectedSubCategoryId = 0;
      } else {
        // 选中点击的分类
        this.selectedSubCategoryId = subCategoryId;
      }
      
      // 重置分页和商品列表
      this.page = 1;
      this.productList = [];
      this.loadingImages = [];
      this.errorImages = [];
      this.loadingType = 'more';
      this.hasMore = true;
      
      // 重新加载商品列表
      this.loadProductList();
    },
    
    // 处理排序选项点击
    tabClick(index) {
      if (this.filterIndex === index) {
        // 如果点击已选中的选项
        if (index === 2 || index === 3) {
          // 销量排序或价格排序，切换升降序
          this.priceOrder = this.priceOrder === 1 ? 2 : 1;
        } else {
          // 其他选项，不做处理
          return;
        }
      } else {
        // 选中新的选项
        this.filterIndex = index;
        
        // 如果是销量排序或价格排序，默认升序
        if (index === 2 || index === 3) {
          this.priceOrder = 1;
        }
      }
      
      // 重置分页和商品列表
      this.page = 1;
      this.productList = [];
      this.loadingImages = [];
      this.errorImages = [];
      this.loadingType = 'more';
      this.hasMore = true;
      
      // 重新加载商品列表
      this.loadProductList();
    },
    
    // 切换视图模式
    toggleViewMode() {
      this.isGridView = !this.isGridView;
    },
    
    // ✅ 优化：加载商品数据（带缓存，仅第一页缓存）
    async loadProductList(showLoading = true) {
      if (this.loadingType === 'loading') return;

      this.loadingType = 'loading';

      try {
        // 构建请求参数
        const params = {
          pageNum: this.page,
          pageSize: this.pageSize,
          sort: 0 // 默认排序
        };

        // 添加学校ID参数
        if (this.selectedSchool && this.selectedSchool.id) {
          params.schoolId = this.selectedSchool.id;
        }

        // 设置分类ID
        if (this.selectedSubCategoryId !== 0) {
          params.productCategoryId = this.selectedSubCategoryId;
        } else if (this.selectedItemId !== 0) {
          params.productCategoryId = this.selectedItemId;
        }

        // 设置排序条件
        if (this.filterIndex === 1) { // 新品排序
          params.sort = 1;
        } else if (this.filterIndex === 2) { // 销量排序
          params.sort = this.priceOrder === 1 ? 2 : 2;
        } else if (this.filterIndex === 3) { // 价格排序
          params.sort = this.priceOrder === 1 ? 3 : 4;
        }

        let result;

        // ✅ 优化：只对第一页使用缓存，避免分页数据混乱
        if (this.page === 1) {
          const cacheKey = `${CACHE_KEYS.PRODUCT_LIST}${JSON.stringify(params)}`;

          result = await cacheManager.getOrSet(
            cacheKey,
            async () => {
              console.log('🌐 从服务器获取商品列表，参数:', params);
              return await searchProductList(params);
            },
            CACHE_EXPIRE_TIME.MEDIUM, // 5分钟缓存
            false
          );
        } else {
          // 非第一页，直接请求
          result = await searchProductList(params);
        }
        
        if (result && result.data) {
          const { list, total } = result.data;
          
          if (list && list.length > 0) {
            // 添加图片加载状态数组
            const newLoadingImages = Array(list.length).fill(true);
            const newErrorImages = Array(list.length).fill(false);
            
            // 更新商品列表
            this.productList = [...this.productList, ...list];
            
            // 更新图片加载状态数组
            this.loadingImages = [...this.loadingImages, ...newLoadingImages];
            this.errorImages = [...this.errorImages, ...newErrorImages];
            
            // 判断是否还有更多数据
            if (this.productList.length >= total) {
              this.hasMore = false;
              this.loadingType = 'nomore';
            } else {
              this.hasMore = true;
              this.loadingType = 'more';
            }
            
            // 页码加1
            this.page++;
          } else {
            this.hasMore = false;
            this.loadingType = 'nomore';
            
            // 移除暂无商品数据的提示，避免与loading动画冲突
          }
        } else {
          throw new Error('获取商品列表失败');
        }
      } catch (error) {
        console.error('获取商品列表失败', error);
        uni.showToast({
          title: '获取商品列表失败',
          icon: 'none'
        });
        this.loadingType = 'more';

        // 计算总图片数量并开始监听图片加载（仅第一页）
        // if (this.page === 2) { // page在成功后会+1，所以这里判断是否为2表示是第一页
        //   this.calculateTotalImages();
        // }
      } finally {
        // 出错时也要隐藏加载
        // if (this.page === 1) { // 如果是第一页且出错，隐藏加载
        //   this.hideFullScreenLoading();
        // }
      }
      // 注意：不在这里隐藏加载，改为在图片加载完成后隐藏
    },

    // 计算总图片数量
    // calculateTotalImages() {
    //   let count = 0;

    //   // 商品列表图片
    //   if (this.productList && this.productList.length > 0) {
    //     count += this.productList.length;
    //   }

    //   this.totalImageCount = count;
    //   this.imageLoadCount = 0;

    //   console.log('分类页总图片数量:', this.totalImageCount, '商品列表长度:', this.productList?.length);
    //   console.log('当前页码:', this.page, '是否显示加载:', this.showFullScreenLoading);

    //   // 如果没有图片，直接隐藏加载
    //   if (this.totalImageCount === 0) {
    //     console.log('没有图片需要加载，直接隐藏加载动画');
    //     this.hideFullScreenLoading();
    //   } else {
    //     // 设置一个额外的兜底定时器，5秒后强制隐藏
    //     setTimeout(() => {
    //       if (this.showFullScreenLoading && this.imageLoadCount < this.totalImageCount) {
    //         console.log('图片加载检测兜底触发，强制隐藏加载动画', {
    //           imageLoadCount: this.imageLoadCount,
    //           totalImageCount: this.totalImageCount
    //         });
    //         this.hideFullScreenLoading();
    //       }
    //     }, 5000);
    //   }
    // },

    // 图片加载完成回调
    // onImageLoad() {
    //   this.imageLoadCount++;
    //   console.log(`分类页图片加载进度: ${this.imageLoadCount}/${this.totalImageCount}`);

    //   // 所有图片加载完成
    //   if (this.imageLoadCount >= this.totalImageCount) {
    //     console.log('分类页所有图片加载完成，隐藏加载动画');
    //     this.hideFullScreenLoading();
    //   }
    // },

    // 图片加载失败回调
    // onImageError() {
    //   this.imageLoadCount++;
    //   console.log(`分类页图片加载失败，进度: ${this.imageLoadCount}/${this.totalImageCount}`);

    //   // 即使图片加载失败，也要检查是否所有图片都处理完了
    //   if (this.imageLoadCount >= this.totalImageCount) {
    //     console.log('分类页所有图片处理完成（包含失败），隐藏加载动画');
    //     this.hideFullScreenLoading();
    //   }
    // },

    // 隐藏全屏加载（统一方法）
    // hideFullScreenLoading() {
    //   // 清除超时定时器
    //   if (this.loadingTimeout) {
    //     clearTimeout(this.loadingTimeout);
    //     this.loadingTimeout = null;
    //   }
    //   // 隐藏加载动画
    //   this.showFullScreenLoading = false;
    // },
    
    // 加载更多
    handleScrollToLower() {
      if (this.hasMore && this.loadingType === 'more') {
        this.loadProductList();
      }
    },

    // 处理右侧滚动
    handleRightScroll(e) {
      // 可在这里实现滚动相关逻辑
    },
    
    // 图片加载完成
    handleImageLoad(index) {
      this.$set(this.loadingImages, index, false);
      // 如果是第一页，调用全屏加载的图片计数
      // 注意：这里判断page <= 2，因为在loadProductList成功后page会+1
      // if (this.page <= 2) {
      //   this.onImageLoad();
      // }
    },

    // 图片加载失败
    handleImageError(index) {
      this.$set(this.loadingImages, index, false);
      this.$set(this.errorImages, index, true);
      // 如果是第一页，调用全屏加载的图片计数
      // 注意：这里判断page <= 2，因为在loadProductList成功后page会+1
      // if (this.page <= 2) {
      //   this.onImageError();
      // }
    },
    
    // 导航到商品详情
    navToDetailPage(item) {
      uni.navigateTo({
        url: `/pages/product/product?id=${item.id}`
      });
    },
    
    // 添加到购物车
    async addToCart(productId) {

      // 跳转到商品详情页，让用户选择完整规格后添加到购物车
      // 因为类别页面的商品缺少完整的规格信息
      uni.navigateTo({
        url: `/pages/product/product?id=${productId}`
      });
    },
    

    
    // 加载商品
    loadProducts() {
      if (this.hasMore) {
        this.loadProductList();
      }
    },
    
    // 刷新商品列表
    refreshProductList(showLoading = true) {
      // 重置分页参数
      this.page = 1;
      this.productList = [];
      this.loadingImages = [];
      this.errorImages = [];
      this.loadingType = 'more';
      this.hasMore = true;

      // 重新加载商品数据
      this.loadProductList(showLoading);

      // 停止下拉刷新动画
      uni.stopPullDownRefresh();
    },

    // 处理购物车点击事件
    handleCartClick() {
      console.log('购物车被点击');
      uni.navigateTo({
        url: '/pages/cart/cart'
      });
    },

    // ✅ 优化：获取商品缩略图
    getProductThumbnail(url) {
      // 使用300x300的缩略图，减少图片大小
      return lazyLoadUtils.getThumbnail(url, 300, 300);
    },

    // ✅ 优化：获取门店logo缩略图
    getStoreThumbnail(url) {
      // 使用100x100的缩略图
      return lazyLoadUtils.getThumbnail(url, 100, 100);
    }
  }
};
</script>

<style lang="scss" scoped>
@charset "UTF-8";
/* 页面左右间距 */
/* 文字尺寸 */
/*文字颜色*/
/* 边框颜色 */
/* 图片加载中颜色 */
/* 行为相关颜色 */

.container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #F8F8F8;
  position: relative;
}

/* 主容器 */
.main-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  width: 100%;
}

/* 顶部区域（包含导航栏、门店信息、搜索框的统一背景） */
.top-section {
  background: linear-gradient(to bottom, rgba(221, 255, 153, 1), rgba(221, 255, 153, 0.8));
  position: relative;
  z-index: 10;
  flex-shrink: 0;
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

/* 门店信息和会员码区域 */
.store-member-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 30rpx;
  box-sizing: border-box;
}

/* 门店信息 */
.store-info-area {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 16rpx;
  margin-right: 20rpx;
}

.store-avatar {
  width: 96rpx;
  height: 96rpx;
  border-radius: 16rpx;
  background-color: rgba(255, 255, 255, 1);
  overflow: hidden;
  flex-shrink: 0;
}

.store-logo {
  width: 100%;
  height: 100%;
}

.store-details {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8rpx;
  /* 下拉箭头和门店名之间的间距 */
}

.dropdown-arrow {
  width: 28rpx;
  height: 28rpx;
  flex-shrink: 0;
  /* 防止图标被压缩 */
}

.store-name {
  font-size: 28rpx;
  font-weight: 500;
  color: #0A0D05;
  line-height: 1.4;
  font-family: 'PingFang SC', -apple-system, BlinkMacSystemFont, sans-serif;
}



/* 线下门店区域 */
.offline-store-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  padding: 16rpx;
}

.store-icon {
  width: 72rpx;
  height: 72rpx;
  border-radius: 8rpx;
}

.store-text {
  font-size: 24rpx;
  color: #000000;
  font-weight: 400;
}

/* 搜索区域 */
.search-area {
  margin: 0 30rpx 20rpx 30rpx;
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
  background-color: #D7FF35;
  border-radius: 16rpx;
  padding: 10rpx 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.search-btn-text {
  color: #0A0D05;
  font-size: 28rpx;
  font-weight: 600;
  font-family: 'PingFang SC', -apple-system, BlinkMacSystemFont, sans-serif;
}



/* 主要内容区 */
.main-content-wrapper {
  display: flex;
  background-color: #F5F5F5;
  /* 修改背景色为灰色 */
  z-index: 2;
  /* 确保主内容在背景图片之上 */
  margin-top: 20rpx;
  padding-top: 20rpx;
  flex: 1;
  overflow: hidden;
  /* 防止内容溢出 */
}

/* 左侧分类导航，固定在左侧 */
.left-sidebar-fixed {
  width: 86px;
  height: 100%;
  /* 占满内容区域高度 */
  background-color: #FFFFFF;
  flex-shrink: 0;
  /* 防止侧边栏被压缩 */
  position: relative;
  /* 为子元素提供定位参考 */
  z-index: 10;
  /* 确保在较高层级 */
}

.left-sidebar {
  width: 100%;
  height: 100%;
  background-color: transparent;
  display: flex;
  /* 新增flex布局 */
  flex-direction: column;
  /* 垂直方向flex */
}

/* 滚动容器样式 */
.category-scroll-view {
  width: 100%;
  height: 100%;
  /* 隐藏滚动条 */
  &::-webkit-scrollbar {
    display: none;
    width: 0 !important;
    height: 0 !important;
    -webkit-appearance: none;
    background: transparent;
  }
}

.category-list {
  display: flex;
  flex-direction: column;
  min-height: 100%;
  /* 最小高度占满容器，允许内容超出时滚动 */
  padding: 0;
  /* 移除内边距 */
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  /* 居中对齐 */
  height: 160rpx;
  /* 固定高度，适合6个项目显示（约16.67%的容器高度） */
  width: 100%;
  background-color: #FFFFFF !important;
  /* 未选中状态背景色，使用!important确保优先级 */
  opacity: 0.4;
  /* 未选中状态半透明 */
  transition: all 0.3s ease;
  flex-shrink: 0;
  /* 防止项目被压缩 */

  &.active {
    background-color: #F8F8F8 !important;
    /* 选中状态背景色，使用!important确保优先级 */
    opacity: 1;
    /* 选中状态完全不透明 */
  }
  
  .category-icon-wrap {
    width: 54rpx;
    height: 54rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: transparent;
    /* 移除背景色 */
    transition: all 0.3s ease;

    .category-icon {
      width: 54rpx;
      /* 固定图标宽度 */
      height: 54rpx;
      /* 固定图标高度 */
      object-fit: cover;
      /* 确保图片填充整个容器 */
    }
  }
  
  .category-name {
    font-size: 22rpx;
    /* 调小字体以适应更紧凑的布局 */
    color: rgba(0, 0, 0, 0.6);
    margin-top: 8rpx;
    /* 减少上边距 */
    transition: all 0.3s ease;
    text-align: center;
    /* 文字居中 */
    line-height: 1.2;
    /* 调整行高 */

    &.active-text {
      color: #000;
      font-weight: 500;
    }
  }
}

/* 右侧内容区域 */
.right-content-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
  /* scroll-view需要父容器有明确高度 */
  position: relative;
  /* 添加定位上下文 */
  overflow: hidden;
  /* 防止溢出 */
}

/* 右侧滚动区域 */
.right-content-scroll {
  flex: 1;
  height: 0;
  /* 配合flex: 1使用，确保scroll-view有明确高度 */
  box-sizing: border-box;
  /* 确保padding计入总宽度 */

  /* 调整加载更多组件的间距 */
  .uni-load-more {
    padding-bottom: 30rpx;
    margin-top: 10rpx;
  }
}

/* 确保购物车组件可见 */
.shopping-cart-area {
  z-index: 999;
  /* 确保购物车在最上层 */
  pointer-events: auto;
  /* 启用点击事件 */
}

/* 二级分类 */
.sub-category {
  width: 100%;
  white-space: nowrap;
  padding: 20rpx 0 10rpx;
  position: relative;
  flex-shrink: 0;
  /* 防止被压缩 */

  /* 隐藏滚动条 */
  &::-webkit-scrollbar {
    display: none;
    width: 0 !important;
    height: 0 !important;
    -webkit-appearance: none;
    background: transparent;
  }
  
  .sub-category-items {
    display: inline-flex;
    padding: 0 20rpx;
  }
  
  .sub-category-item {
    position: relative;
    display: inline-block;
    padding: 0 24rpx;
    text-align: center;
    
    &.active .sub-category-name {
      font-weight: 500;
      color: #000000;
    }
    
    .sub-category-name {
      font-size: 24rpx;
      color: #000000;
      font-weight: 400;
      line-height: 1.5;
      position: relative;
      z-index: 2;
      /* 确保文字在指示器上方 */
    }
    
    .sub-indicator {
      position: absolute;
      width: 80rpx;
      height: 20rpx;
      border-radius: 20rpx;
      background: linear-gradient(90deg, #89C4D0 0%, #DCFF6A 100%);
      z-index: 1;
      /* 确保在文字背后 */
      left: 50%;
      top: 50%;
      transform: translate(-50%, -50%);
      opacity: 0.4;
    }
  }
}

/* 排序选项卡 */
.sort-tabs {
  display: flex;
  margin-bottom: 0;
  background-color: #FFFFFF;
  border-radius: 0;
  padding: 0 20rpx 0rpx 0rpx;
  height: 80rpx;
  flex-shrink: 0;
  /* 防止被压缩 */
  z-index: 1;
  position: relative;
  align-items: center;
  justify-content: space-between;
  
  .tab-items {
    flex: 1;
    display: flex;
    position: relative;
    margin-right: 20rpx;
    /* 与视图切换按钮保持距离 */
  }
  
  .sort-tab-item {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    
    text {
      font-size: 28rpx;
      color: #666;
      position: relative;
      z-index: 2;
      /* 文字层级高于指示器 */
    }
    
    &.active text {
      color: #000;
      font-weight: 500;
    }
    
    .indicator {
      position: absolute;
      width: 24rpx;
      height: 24rpx;
      border-radius: 50%;
      background: linear-gradient(140deg, #DAF2FA 15%, #DCFF6A 85%);
      z-index: 1;
      /* 确保在文字背后 */
      /* 调整圆点位置，向下偏移 */
      left: 50%;
      top: 80%;
      transform: translate(-50%, -50%);
    }
    
    .price-arrows {
      display: flex;
      flex-direction: column;
      margin-left: 6rpx;
      
      .arrow-icon {
        width: 24rpx;
        height: 12rpx;
        opacity: 0.5;
        
        &.active {
          opacity: 1;
        }
      }
    }
  }
  
  /* 视图切换按钮样式 */
  .view-toggle {
    width: 40rpx;
    height: 40rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    /* 防止被压缩 */
    
    .view-mode-icon {
      width: 40rpx;
      height: 40rpx;
    }
  }
}

/* 商品列表 */
.product-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20rpx;
  margin-bottom: 20rpx;
  width: 100%;
  padding: 10rpx;
  /* 添加内边距，为左侧的标签留出空间 */
  box-sizing: border-box;
  /* 确保内边距计入宽度 */
  
  &.list-view {
    grid-template-columns: 1fr;
  }
}

.product-card {
  background: #FFFFFF;
  border-radius: 16rpx;
  overflow: visible;
  /* 修改为visible，允许子元素超出容器 */
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
  transform: translateY(0);
  transition: all 0.3s ease;
  position: relative;
  /* 确保内部绝对定位元素相对于卡片定位 */
  margin-left: 10rpx;
  /* 给左侧标签留出空间 */
  
  &:active {
    transform: translateY(-6rpx);
    box-shadow: none;
    
    /* 确保点击卡片时标签不受影响 */
    .product-labels {
      transform: translateY(6rpx);
      /* 与卡片移动方向相反，保持视觉上的固定 */
    }
  }
  
  /* 商品标签 */
  .product-labels {
    position: absolute;
    top: 0;
    /* 调整顶部位置 */
    left: -18rpx;
    /* 调整左侧位置，确保标签完全可见 */
    display: flex;
    flex-direction: column;
    /* 保持垂直布局 */
    z-index: 20;
    /* 增加z-index以确保显示在最上层 */
    pointer-events: none;
    /* 防止标签拦截点击事件 */
    
    .label {
      position: relative;
      padding: 2px 6px;
      color: #000000;
      /* 修改为黑色 */
      font-size: 10px;
      font-weight: 500;
      margin-bottom: 2px;
      /* 标签之间的间距 */
      height: 32px;
      /* 固定高度与新首页一致 */
      width: 32px;
      /* 固定宽度与新首页一致 */
      display: flex;
      align-items: center;
      justify-content: center;
      white-space: nowrap;
      /* 防止文字换行 */
      font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen, Ubuntu, Cantarell, "Open Sans", "Helvetica Neue", sans-serif;
      z-index: 2;
      /* 确保文字在背景层上方 */
      -webkit-transform: translateZ(0);
      /* 启用硬件加速 */
      transform: translateZ(0);
      text-shadow: -1px -1px 0 rgba(255, 255, 255, 0.5), 1px -1px 0 rgba(255, 255, 255, 0.5), -1px 1px 0 rgba(255, 255, 255, 0.5), 1px 1px 0 rgba(255, 255, 255, 0.5);
      
      &::before {
        content: "";
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-image: url("https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/product_card/tag_bg.png");
        background-size: 100% 100%;
        background-repeat: no-repeat;
        z-index: -1;
        /* 设置负值使其位于文字后面 */
      }
    }
    
    /* 调整各类标签文字的颜色和大小以便于显示 */
    .sale-label, .new-label, .rec-label, .hot-label {
      font-size: 9px;
      /* 调小文字 */
      color: #000000;
      /* 黑色文字 */
      font-weight: bold;
      /* 加粗使其更明显 */
      letter-spacing: -0.3px;
      /* 紧凑字间距 */
      text-shadow: 0 0 1px rgba(255, 255, 255, 0.5);
      /* 添加微弱文字阴影增加可读性 */
    }
  }
  
  .product-image-container {
    width: 100%;
    height: 0;
    padding-bottom: 100%;
    /* 1:1 比例 */
    position: relative;
    overflow: visible;
    /* 允许标签溢出 */
    border-radius: 16rpx 16rpx 0 0;
    /* 顶部圆角 */
    
    .product-image {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      object-fit: contain;
      /* 显示原比例，确保图片完整显示 */
      border-radius: 16rpx 16rpx 0 0;
      /* 顶部圆角与容器一致 */
    }
    
    .image-placeholder {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      background-color: #f9f9f9;
      border-radius: 16rpx 16rpx 0 0;
    }
    
    .placeholder-error,
    .placeholder-loading {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      width: 100%;
      height: 100%;
      
      text {
        font-size: 24rpx;
        color: #bbbbbb;
        margin-top: 10rpx;
      }
    }
    
    .loading-spinner {
      width: 40rpx;
      height: 40rpx;
      border: 3rpx solid #dddddd;
      border-top-color: #89C4D0;
      border-radius: 50%;
      animation: spin 1s linear infinite;
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
      border-radius: 16rpx 16rpx 0 0;
      z-index: 10;
      
      .sold-out-text {
        color: #ffffff;
        font-size: 32rpx;
        font-weight: bold;
        text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.3);
      }
    }
  }
  
  .product-info {
    padding: 16rpx;
    
    .product-title {
      font-size: 28rpx;
      color: #000;
      line-height: 1.5;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      min-height: 84rpx;
    }
    
    .product-bottom {
      display: flex;
      flex-direction: column;
      margin-top: 16rpx;
    }
  }
}



/* 特定标签背景设置 */
.new-label,
.rec-label,
.hot-label,
.sale-label {
  background-image: none;
  
  &::before {
    background-image: url("https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/product_card/tag_bg.png") !important;
    background-size: 100% 100% !important;
    background-repeat: no-repeat !important;
  }
}

/* 确保不仅在点击状态而且在所有状态下标签都完全可见 */
.product-card .product-labels .label {
  opacity: 1 !important;
  visibility: visible !important;
}

/* 强化标签文字显示 */
.product-labels .label {
  -webkit-transform: translateZ(0);
  /* 启用硬件加速 */
  transform: translateZ(0);
}

/* 为标签文字添加描边效果增强可读性 */
.label {
  text-shadow: -1px -1px 0 rgba(255, 255, 255, 0.5), 1px -1px 0 rgba(255, 255, 255, 0.5), -1px 1px 0 rgba(255, 255, 255, 0.5), 1px 1px 0 rgba(255, 255, 255, 0.5);
}

/* 列表视图下的商品卡片样式 */
.list-view {
  .product-card {
    display: flex;
    flex-direction: row;
    height: 200rpx;
    align-items: center;
    position: relative;
    
    .list-brand {
      position: absolute;
      top: 0;
      right: 0;
      height: 40rpx;
      background: linear-gradient(to right, #FED8A6, #FFFAED, #FED28F);
      border-radius: 0 20rpx 0 20rpx;
      display: flex;
      align-items: center;
      padding: 0 16rpx;
      z-index: 20;
      /* 确保在其他元素之上 */
      
      .brand-name {
        font-size: 20rpx;
        color: #000;
      }
    }
    
    .product-image-container {
      width: 180rpx;
      height: 180rpx;
      padding-bottom: 0;
      flex-shrink: 0;
    }
    
    .product-image {
      position: relative;
      width: 100%;
      height: 100%;
    }
    
    .product-info {
      flex: 1;
      padding-left: 20rpx;
      padding-top: 30rpx;
      /* 增加上边距，避免标题被品牌标签遮挡 */
      padding-right: 20rpx;
      /* 增加右边距 */
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      height: 180rpx;
      
      .product-title {
        min-height: auto;
        margin-bottom: 10rpx;
        /* 增加标题和底部区域的间距 */
      }
      
      .product-bottom {
        display: flex;
        width: 100%;
        justify-content: stretch;
        /* 让内容拉伸填充整个宽度 */
      }
    }
    
    /* 列表视图下的价格和购买数量区域 */
    .price-purchase-row {
      background-color: transparent;
      border-radius: 0;
      padding: 0;
      margin-top: 10rpx;
      width: 100%;
      box-shadow: none;
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-left: 0;
      /* 与标题对齐 */
      min-width: auto;
      gap: 20rpx;
      /* 价格和购买数量之间的间距 */
    }
  }
}

/* 品牌标签基本样式 */
.product-brand {
  position: absolute;
  top: 0;
  right: 0;
  height: 40rpx;
  background: linear-gradient(to right, #FED8A6, #FFFAED, #FED28F);
  border-radius: 0 20rpx 0 20rpx;
  /* 修改圆角位置，与卡片右上角对齐 */
  display: flex;
  align-items: center;
  padding: 0 16rpx;
  z-index: 10;
  /* 确保在其他元素之上 */
}

.brand-name {
  font-size: 20rpx;
  color: #000;
  font-weight: 500;
}

.price-purchase-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  
  .product-price {
    font-size: 22rpx;
    color: #647D00;
    font-weight: 700;
    
    .price-number {
      font-size: 36rpx;
      font-weight: 700;
    }
  }
  
  .purchase-count {
    font-size: 24rpx;
    color: #999999;
    font-weight: 400;
  }
}

/* 底部导航 */
.bottom-navigation {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 100rpx;
  background-color: #fff;
  display: flex;
  justify-content: space-around;
  align-items: center;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
  z-index: 100;
  
  .nav-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    opacity: 0.5;
    
    &.active {
      opacity: 1;
    }
    
    .nav-icon {
      width: 48rpx;
      height: 48rpx;
      margin-bottom: 6rpx;
    }
    
    .nav-text {
      font-size: 20rpx;
      color: #333;
    }
  }
}

/* 动画 */
@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* 所有标签和标签背景保持可见，即使父元素有透明度变化 */
.product-labels,
.product-labels .label,
.product-labels .label::before {
  opacity: 1 !important;
}

/* 组合商品（特惠商品）样式 */
.bundle-header {
  padding: 20rpx 24rpx;
  background-color: #FFFFFF;
  
  .bundle-header-title {
    font-size: 32rpx;
    font-weight: 600;
    color: #333333;
    display: block;
  }
  
  .bundle-header-desc {
    font-size: 24rpx;
    color: #999999;
    margin-top: 8rpx;
    display: block;
  }
}

/* 组合商品标签样式 */
.bundle-tag {
  position: absolute;
  top: 0;
  left: 0;
  background: linear-gradient(135deg, #FF6B6B, #FF8E53);
  padding: 4rpx 12rpx;
  border-radius: 0 0 12rpx 0;
  z-index: 10;
  
  text {
    font-size: 20rpx;
    color: #FFFFFF;
    font-weight: 500;
  }
}

/* 特惠商品分类项特殊样式 */
.special-category {
  /* 无特殊样式 */
}
</style> 