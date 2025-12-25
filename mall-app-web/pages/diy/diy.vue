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

      <!-- 搜索栏区域 -->
      <view class="search-section" :style="{ marginTop: navBarHeight + 'px' }">
        <!-- 学校选择 -->
        <view class="store-selector" @tap="handleStoreSelect">
          <view class="store-avatar">
            <image
              class="store-logo"
              :src="getSchoolDisplayImage(selectedSchool)"
              mode="aspectFill"
              :lazy-load="true"
              @error="handleStoreLogoError"
            />
          </view>
          <view class="store-info">
            <text class="store-name">
              {{ selectedSchool ? selectedSchool.schoolName : '选择学校' }}
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
    </view>
    
    <!-- Tab导航栏 -->
    <view class="tab-navigation">
      <view 
        v-for="(tab, index) in tabs" 
        :key="index"
        class="tab-item"
        :class="{ 'active': selectedTab === index }"
        @tap="handleTabClick(index)"
      >
        <view class="tab-text-container">
          <text class="tab-text">{{ tab }}</text>
          <image 
            v-if="selectedTab === index"
            class="tab-indicator" 
            src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/tab_indicator.png" 
            mode="aspectFit"
          />
        </view>
      </view>
    </view>
    
    <!-- 主体内容 -->
    <view class="main-content" :style="{ backgroundColor: selectedTab === 0 ? '#F0F8FF' : '#F5F5F5' }">
      <!-- 第一个Tab：风格模型 -->
      <view v-if="selectedTab === 0" class="style-model-content">
        <!-- 调试信息 -->
        <view v-if="styleModelData.length === 0" style="padding: 20rpx; text-align: center; color: #999;">
          <text>正在加载风格模型数据...</text>
        </view>

        <!-- 前两个风格模型：大卡片样式 -->
        <view
          v-for="(item, index) in styleModelData.slice(0, 2)"
          :key="item.id"
          class="style-model-card"
        >
          <!-- 风格封面图片 -->
          <view class="style-cover-container">
            <image
              class="style-cover-image"
              :src="item.image || 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/banner1.jpg'"
              mode="aspectFill"
            />
          </view>

          <!-- 底部信息区域 -->
          <view class="style-info-container">
            <!-- 左侧文字区域 -->
            <view class="style-text-area">
              <text class="style-title">{{ item.name || '风格名称' }}</text>
              <text class="style-subtitle">{{ item.description || '风格介绍' }}</text>
            </view>
            <!-- 右侧按钮 -->
            <view class="generate-button" @tap="handleGenerateClick(item)">
              <text class="generate-button-text">去生成</text>
            </view>
          </view>
        </view>

        <!-- 第三个及后续风格模型：横向滚动小卡片 -->
        <view v-if="styleModelData.length > 2" class="style-model-scroll-section">
          <scroll-view class="style-scroll-view" scroll-x="true" show-scrollbar="false">
            <view class="style-scroll-container">
              <view
                v-for="(item, index) in styleModelData.slice(2)"
                :key="item.id"
                class="style-small-card"
                @tap="handleGenerateClick(item)"
              >
                <!-- 小卡片封面图片 -->
                <view class="small-card-image-container">
                  <image
                    class="small-card-image"
                    :src="item.image || 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/banner1.jpg'"
                    mode="aspectFill"
                  />
                </view>

                <!-- 小卡片信息 -->
                <view class="small-card-info">
                  <text class="small-card-title">{{ item.name || '风格名称' }}</text>
                </view>
              </view>
            </view>
          </scroll-view>
        </view>
      </view>
      
      <!-- 第二个Tab：产品分类 -->
      <view v-if="selectedTab === 1" class="category-content">
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

                  <!-- 分类列表 - 显示所有分类 -->
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
            <!-- 商品列表 -->
            <scroll-view
              class="right-content-scroll"
              scroll-y="true"
              lower-threshold="100"
              @scrolltolower="handleScrollToLower"
            >
              <!-- 商品列表 -->
              <view class="product-grid" v-if="productList.length > 0">
                <view
                  v-for="(item, index) in productList"
                  :key="index"
                  class="product-card"
                  @tap="navToDetailPage(item)"
                >
                  <!-- 商品图片 -->
                  <view class="product-image-container">
                    <image
                      class="product-image"
                      :src="item.pic"
                      mode="aspectFit"
                    ></image>
                  </view>

                  <!-- 商品信息 -->
                  <view class="product-info">
                    <text class="product-title">{{ item.name }}</text>
                    <view class="product-bottom">
                      <view class="price-purchase-row">
                        <text class="product-price">¥<text class="price-number">{{ item.price }}</text></text>
                        <text class="purchase-count">{{ (item.sale || 200) }}+购买</text>
                      </view>
                    </view>
                  </view>
                </view>
              </view>

              <!-- 空状态 -->
              <view class="empty-state" v-else>
                <image
                  class="empty-image"
                  src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/common/empty.png"
                  mode="aspectFit"
                ></image>
                <text class="empty-text">暂无商品</text>
                <text class="empty-tip">该分类下暂时没有可定制的商品</text>
              </view>
            </scroll-view>
          </view>
        </view>
      </view>
    </view>

    <!-- 风格模型商品选择弹窗 -->
    <style-model-product-modal
      :visible="showProductModal"
      :style-model-id="selectedStyleModel.id"
      :style-model-name="selectedStyleModel.name"
      @close="handleProductModalClose"
    />

    <!-- 学校选择弹窗 -->
    <store-selector
      :visible="showStoreSelector"
      :current-store="selectedStore"
      :school-groups="schoolGroups"
      :selected-school-id="selectedSchool ? selectedSchool.id : null"
      @select="handleStoreSelected"
      @close="handleStoreSelectorClose"
    />
    </view>
  </view>
</template>

<script>
import NavBar from '@/components/nav-bar.vue';
import StyleModelProductModal from '@/components/diy/StyleModelProductModal.vue';
import StoreSelector from '@/components/store/StoreSelector.vue';
import SkeletonScreen from '@/components/SkeletonScreen.vue';
import { fetchProductCateList, fetchNewProductList } from '@/api/home.js';
import { searchProductList } from '@/api/product.js';
import { fetchStyleModelCards, getStyleModelProductList, getProductDIYTemplate } from '@/api/styleModel.js';
import { fetchStoreGroupsBySchool } from '@/api/store.js';
import cacheManager, { CACHE_KEYS, CACHE_EXPIRE_TIME } from '@/utils/cacheManager.js';
import { lazyLoadUtils } from '@/utils/lazyLoad.js';

export default {
  components: {
    'nav-bar': NavBar,
    'style-model-product-modal': StyleModelProductModal,
    'store-selector': StoreSelector,
    'skeleton-screen': SkeletonScreen
  },
  data() {
    return {
      // 导航和门店信息
      navBarHeight: 0,
      statusBarHeight: 0,
      selectedSchool: null, // 选中的学校信息
      selectedStore: null, // 保留兼容性
      showStoreSelector: false, // 是否显示学校选择弹窗
      schoolGroups: [], // 学校分组数据
      
      // Tab相关
      // tabs: ['产品分类', '推荐', '风格模型'], // 原始包含推荐tab的配置
      tabs: ['风格模型', '产品分类'], // 风格模型放第一个，产品分类放第二个
      selectedTab: 0,
      
      // 第一个Tab：产品分类相关数据
      categoryList: [],
      selectedItemId: 0,
      subCategories: [],
      selectedSubCategoryId: 0,
      productList: [],
      page: 1,
      pageSize: 10,
      
      // 第二个Tab：推荐相关数据 - 已注释移除
      // hotSaleProducts: [],
      // blindBoxThemes: [],
      
      // 第二个Tab：风格模型相关数据（原第三个Tab，移除推荐tab后索引变更）
      themeProducts: [],
      styleModelData: [], // 从API获取的风格模型数据

      // 弹窗相关数据
      showProductModal: false,
      selectedStyleModel: {
        id: '',
        name: ''
      },

      styleModels: [
        {
          title: '风格名称',
          image: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/banner1.jpg'
        },
        {
          title: '风格名称',
          image: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/banner2.jpg'
        },
        {
          title: '风格名称',
          image: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/banner3.jpg'
        },
        {
          title: '风格名称',
          image: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/banner1.jpg'
        }
      ],

      // 性能优化相关
      dataLoaded: false, // 数据是否已加载（用于控制骨架屏显示）
      lastSchoolChangeCheck: 0, // 上次检查学校变化的时间戳
      schoolChangeDebounceTime: 500, // 学校变化检查防抖时间（毫秒）
      categoryListLoaded: false, // 分类列表是否已加载
    };
  },
  async onLoad() {
    // 获取系统信息
    const systemInfo = uni.getSystemInfoSync();
    this.statusBarHeight = systemInfo.statusBarHeight;
    this.navBarHeight = this.statusBarHeight + 40;

    // 加载学校列表数据
    await this.loadStoreGroups();

    // 加载学校信息
    this.loadSelectedSchool();

    // 初始化数据
    this.initData();
  },
  onShow() {
    // 重新加载学校信息（从学校选择页面返回时可能有更新）
    this.loadSelectedSchool();

    // ✅ 如果数据已加载，直接显示（避免白屏）
    if (this.categoryListLoaded) {
      this.dataLoaded = true;
    }
  },
  // 分享给朋友
  onShareAppMessage(res) {
    return {
      title: '广横走文创 - DIY定制专区',
      path: '/pages/diy/diy',
      imageUrl: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/share/diy_share.jpg'
    }
  },
  // 分享到朋友圈
  onShareTimeline() {
    return {
      title: '广横走文创 - DIY定制专区',
      query: ''
    }
  },
  methods: {
    // 初始化数据
    async initData() {
      await this.loadCategoryList();
      // await this.loadRecommendData(); // 推荐数据加载已注释移除
      await this.loadStyleModelData();

      // ✅ 标记数据已加载，隐藏骨架屏
      this.dataLoaded = true;
    },
    
    // 加载已选择的学校信息
    loadSelectedSchool() {
      try {
        const schoolInfo = uni.getStorageSync('selectedSchool');
        if (schoolInfo) {
          this.selectedSchool = typeof schoolInfo === 'string' ? JSON.parse(schoolInfo) : schoolInfo;
          // 同时更新 selectedStore 以保持兼容性
          this.selectedStore = {
            schoolId: this.selectedSchool.id,
            schoolName: this.selectedSchool.schoolName
          };
        }
      } catch (error) {
        console.error('加载学校信息失败:', error);
      }
    },

    // 获取学校显示图片
    getSchoolDisplayImage(school) {
      if (!school) {
        return 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/guanghengzou_logo.png';
      }
      // 优先使用缩略图，其次使用原图，最后使用默认图
      return school.coverThumbnail || school.schoolLogo || 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/guanghengzou_logo.png';
    },

    // 处理学校选择
    handleStoreSelect() {
      this.showStoreSelector = true;
    },

    // 处理学校选择确认
    handleStoreSelected(school) {
      // 保存选择的学校信息
      this.selectedSchool = school;
      this.saveSelectedSchool(school);

      // 更新 selectedStore 以保持兼容性
      this.selectedStore = {
        schoolId: school.id,
        schoolName: school.schoolName
      };

      // 学校变化后重新加载商品列表
      this.loadProductList();

      // 提示用户
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
        uni.setStorageSync('selectedSchool', JSON.stringify(school));
        console.log('学校信息已保存:', school);
      } catch (error) {
        console.error('保存学校信息失败:', error);
      }
    },

    // 加载学校列表数据
    async loadStoreGroups() {
      try {
        console.log('开始加载学校列表数据...');
        const res = await fetchStoreGroupsBySchool();

        if (res && res.data && res.data.schoolGroups) {
          this.schoolGroups = res.data.schoolGroups;
        } else if (res && res.schoolGroups) {
          this.schoolGroups = res.schoolGroups;
        } else if (res && res.data) {
          this.schoolGroups = res.data;
        } else {
          console.warn('学校列表数据格式不正确');
          this.schoolGroups = [];
        }

        console.log('学校列表加载成功，共', this.schoolGroups.length, '个学校');
      } catch (error) {
        console.error('加载学校列表失败:', error);
        this.schoolGroups = [];
        uni.showToast({
          title: '加载学校列表失败',
          icon: 'none'
        });
      }
    },

    // 处理搜索
    handleSearch() {
      uni.navigateTo({
        url: '/pages/search/search'
      });
    },
    
    // 处理门店logo加载错误
    handleStoreLogoError() {
      console.log('门店logo加载失败，已使用默认logo');
    },
    
    // 处理Tab点击
    handleTabClick(index) {
      this.selectedTab = index;
    },
    
    // 加载分类数据
    async loadCategoryList() {
      try {
        // ✅ 使用缓存机制（15分钟缓存）
        const result = await cacheManager.getOrSet(
          CACHE_KEYS.CATEGORY_LIST,
          async () => {
            console.log('🌐 从服务器获取DIY分类列表');
            return await fetchProductCateList(0);
          },
          CACHE_EXPIRE_TIME.LONG,
          false
        );

        if (result && result.data) {
          this.categoryList = result.data;
          this.categoryListLoaded = true; // ✅ 标记分类列表已加载

          if (this.categoryList.length > 0) {
            // 默认选中全部商品
            this.selectedItemId = 0;

            // 清空二级分类
            this.subCategories = [];
            this.selectedSubCategoryId = 0;

            // 重置商品列表状态
            this.resetProductState();

            // 加载所有可DIY商品
            this.loadProductList();
          }
        }
      } catch (error) {
        console.error('获取分类列表失败', error);
      }
    },
    
    // 加载二级分类
    async loadSubCategories(parentId) {
      try {
        const result = await fetchProductCateList(parentId);
        
        if (result && result.data && result.data.length > 0) {
          this.subCategories = result.data;
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
      } catch (error) {
        console.error('获取二级分类失败', error);
        this.subCategories = [];
        this.selectedSubCategoryId = 0;
        
        // 即使获取二级分类失败，仍然加载一级分类的商品
        this.resetProductState();
        this.loadProductList();
      }
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

      // 加载所有可DIY商品
      this.loadProductList();
    },

    // 处理分类点击
    handleCategoryClick(item) {
      if (this.selectedItemId === item.id) return;
      this.selectedItemId = item.id;

      // 加载二级分类
      this.loadSubCategories(item.id);
    },
    
    // 重置商品列表状态
    resetProductState() {
      this.page = 1;
      this.productList = [];
    },
    
    // 加载商品数据
    async loadProductList() {
      try {
        const params = {
          pageNum: this.page,
          pageSize: this.pageSize,
          sort: 0,  // 默认排序
          isDIY: true  // 只查询可DIY定制的商品
        };

        // 添加学校ID参数
        if (this.selectedStore && this.selectedStore.schoolId) {
          params.schoolId = this.selectedStore.schoolId;
        }

        // 设置分类ID - 优先使用二级分类ID，如果没有则使用一级分类ID
        if (this.selectedSubCategoryId !== 0) {
          params.productCategoryId = this.selectedSubCategoryId;
        } else if (this.selectedItemId !== 0) {
          params.productCategoryId = this.selectedItemId;
        }

        const result = await searchProductList(params);
        if (result && result.data && result.data.list) {
          this.productList = [...this.productList, ...result.data.list];
          this.page++;
        }
      } catch (error) {
        console.error('获取商品列表失败', error);
      }
    },
    
    // 处理滚动到底部
    handleScrollToLower() {
      this.loadProductList();
    },
    
    // 加载推荐数据 - 已注释移除
    /*
    async loadRecommendData() {
      try {
        const result = await fetchNewProductList({ pageNum: 1, pageSize: 10 });
        if (result && result.data) {
          // 根据API返回的数据结构，data直接是数组
          const products = Array.isArray(result.data) ? result.data : (result.data.list || []);

          // 转换字段名以匹配组件期望的数据结构
          const transformedProducts = products.map(item => ({
            id: item.productId || item.id,
            name: item.productName || item.name,
            price: item.price,
            pic: item.pic,
            sale: item.sale || 200
          }));

          this.hotSaleProducts = transformedProducts.slice(0, 3);

          // 为盲盒主题创建6个项目（前3个大卡片，后3个小卡片）
          const blindBoxData = [];
          for (let i = 0; i < 6; i++) {
            const sourceIndex = i % transformedProducts.length;
            blindBoxData.push({
              ...transformedProducts[sourceIndex],
              id: `blindbox_${i}`,
              name: `盲盒主题商品 ${i + 1}`
            });
          }
          this.blindBoxThemes = blindBoxData;
        }
      } catch (error) {
        console.error('获取推荐数据失败', error);
      }
    },
    */
    
    // 加载风格模型数据
    async loadStyleModelData() {
      try {
        console.log('开始获取风格模型数据...');
        const result = await fetchStyleModelCards();
        console.log('API返回结果:', result);
        if (result && result.data) {
          // 转换数据格式以匹配前端组件期望的结构
          this.styleModelData = result.data.map(item => ({
            id: item.id,
            name: item.name,
            description: item.description,
            image: item.coverImage || item.bannerImage,
            productCount: item.productCount || 0
          }));
          console.log('转换后的数据:', this.styleModelData);
        } else {
          console.log('API返回数据为空或格式不正确');
          // 如果API返回数据为空，使用模拟数据
          this.setFallbackData();
        }
      } catch (error) {
        console.error('获取风格模型数据失败', error);
        // 如果API调用失败，使用模拟数据作为备用
        this.setFallbackData();
      }
    },

    // 设置备用数据
    setFallbackData() {
      this.styleModelData = [
          {
            id: 1,
            name: '简约现代',
            description: '简约而不简单，现代时尚的设计风格',
            image: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/banner1.jpg',
            productCount: 0
          },
          {
            id: 2,
            name: '复古经典',
            description: '经典复古风格，传承经典设计元素',
            image: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/banner2.jpg',
            productCount: 0
          },
          {
            id: 3,
            name: '清新自然',
            description: '清新自然的设计风格，贴近自然',
            image: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/banner3.jpg',
            productCount: 0
          },
          {
            id: 4,
            name: '潮流时尚',
            description: '紧跟时尚潮流，个性张扬的设计风格',
            image: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/banner1.jpg',
            productCount: 0
          }
        ];
    },
    
    // 导航到商品详情
    navToDetailPage(item) {
      // 使用productId或id字段
      const productId = item.productId || item.id;
      uni.navigateTo({
        url: `/pages/product/product?id=${productId}`
      });
    },

    // 处理风格模型卡片点击（显示商品选择弹窗）
    handleGenerateClick(styleModel) {
      console.log('点击风格模型:', styleModel);
      this.selectedStyleModel = {
        id: styleModel.id || styleModel.styleModelId,
        name: styleModel.name || '风格模型'
      };
      this.showProductModal = true;
    },

    // 关闭商品选择弹窗
    handleProductModalClose() {
      console.log('父组件接收到关闭事件');
      this.showProductModal = false;
      this.selectedStyleModel = {
        id: '',
        name: ''
      };
    },
    
    // 处理主题点击 - 已注释移除
    /*
    handleThemeClick(theme) {
      console.log('主题点击:', theme);
    },

    // 处理盲盒查看更多
    handleBlindBoxViewMore() {
      uni.showToast({
        title: '盲盒查看更多功能',
        icon: 'none'
      });
    },
    */
    
    // 处理风格点击
    handleStyleClick(style) {
      console.log('风格点击:', style);
    }
  }
};
</script>

<style lang="scss" scoped>
.container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #F5F5F5;
  position: relative;
}

/* 主容器 */
.main-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  width: 100%;
}

/* 顶部区域样式 - 复用category页面样式 */
.top-section {
  background: linear-gradient(to bottom, rgba(221, 255, 153, 1), rgba(221, 255, 153, 0.8));
  position: relative;
  z-index: 10;
  flex-shrink: 0;
}

.nav-bar-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 30rpx;
  position: relative;
  width: 100%;
}

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

/* 学校选择器 */
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

/* Tab导航栏 */
.tab-navigation {
  display: flex;
  background-color: #FFFFFF;
  padding: 20rpx 30rpx;
  position: relative;
  z-index: 5;
  gap: 20rpx;
  justify-content: center;
}

.tab-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20rpx;
  position: relative;
  transition: all 0.3s ease;
}

.tab-text-container {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.tab-text {
  font-size: 32rpx;
  color: #666666;
  font-weight: 500;
  transition: all 0.3s ease;
  position: relative;
  z-index: 2;
}

.tab-item.active .tab-text {
  color: #000000;
  font-weight: 600;
}

.tab-indicator {
  position: absolute;
  bottom: 0rpx;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  height: 24rpx;
  z-index: 1;
}

/* 主体内容 */
.main-content {
  flex: 1;
  overflow: hidden;
}

/* 第一个Tab：产品分类内容 */
.category-content {
  height: 100%;
}

.main-content-wrapper {
  display: flex;
  background-color: #F5F5F5;
  z-index: 2;
  padding-top: 20rpx;
  flex: 1;
  overflow: hidden;
  height: 100%;
}

.left-sidebar-fixed {
  width: 94px;
  height: 100%;
  background-color: #FFFFFF;
  flex-shrink: 0;
  position: relative;
  z-index: 10;
}

.left-sidebar {
  width: 100%;
  height: 100%;
  background-color: transparent;
  display: flex;
  flex-direction: column;
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
  padding: 0;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 160rpx;
  width: 100%;
  background-color: #FFFFFF !important;
  opacity: 0.4;
  transition: all 0.3s ease;
  flex-shrink: 0;

  &.active {
    background-color: #F8F8F8 !important;
    opacity: 1;
  }

  .category-icon-wrap {
    width: 54rpx;
    height: 54rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: transparent;
    transition: all 0.3s ease;

    .category-icon {
      width: 54rpx;
      height: 54rpx;
      object-fit: cover;
    }
  }

  .category-name {
    font-size: 22rpx;
    color: rgba(0, 0, 0, 0.6);
    margin-top: 8rpx;
    transition: all 0.3s ease;
    text-align: center;
    line-height: 1.2;

    &.active-text {
      color: #000;
      font-weight: 500;
    }
  }
}

.right-content-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
  /* scroll-view需要父容器有明确高度 */
  position: relative;
  overflow: hidden;
}

.right-content-scroll {
  flex: 1;
  height: 0;
  /* 配合flex: 1使用，确保scroll-view有明确高度 */
  box-sizing: border-box;
}

.product-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20rpx;
  margin-bottom: 20rpx;
  width: 100%;
  padding: 10rpx;
  box-sizing: border-box;
}

/* 空状态样式 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 40rpx;
  min-height: 600rpx;
}

.empty-image {
  width: 300rpx;
  height: 300rpx;
  margin-bottom: 40rpx;
  opacity: 0.6;
}

.empty-text {
  font-size: 32rpx;
  color: #333333;
  font-weight: 500;
  margin-bottom: 16rpx;
}

.empty-tip {
  font-size: 26rpx;
  color: #999999;
  text-align: center;
  line-height: 1.5;
}

.product-card {
  background: #FFFFFF;
  border-radius: 16rpx;
  overflow: visible;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
  transform: translateY(0);
  transition: all 0.3s ease;
  position: relative;
  margin-left: 10rpx;
  
  &:active {
    transform: translateY(-6rpx);
  }
}

.product-image-container {
  width: 100%;
  height: 0;
  padding-bottom: 100%;
  position: relative;
  overflow: visible;
  border-radius: 16rpx 16rpx 0 0;
}

.product-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: contain;
  border-radius: 16rpx 16rpx 0 0;
}

.product-info {
  padding: 16rpx;
}

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

.price-purchase-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.product-price {
  font-size: 22rpx;
  color: #647D00;
  font-weight: 700;
}

.price-number {
  font-size: 36rpx;
  font-weight: 700;
}

.purchase-count {
  font-size: 24rpx;
  color: #999999;
  font-weight: 400;
}

/* 第二个Tab：推荐内容 - 已注释移除 */
/*
.recommend-content {
  padding: 20rpx;
  height: 100%;
  overflow-y: auto;
}
*/

/* 第三个Tab：风格模型内容 */
.style-model-content {
  padding: 20rpx;
  height: 100%;
  overflow-y: auto;
}

.style-model-card {
  width: 100%;
  margin-bottom: 30rpx;
  border-radius: 20rpx;
  overflow: hidden;
  background-image: url('https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/diy_model_background.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  padding: 30rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);
}

.style-cover-container {
  width: 100%;
  height: 300rpx;
  margin-bottom: 30rpx;
  border-radius: 16rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.1);
}

.style-cover-image {
  width: 100%;
  height: 100%;
}

/* 底部信息区域 */
.style-info-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 10rpx;
}

/* 左侧文字区域 */
.style-text-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  margin-right: 20rpx;
}

/* 风格名称样式 */
.style-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 8rpx;
  line-height: 1.2;
}

/* 风格介绍样式 */
.style-subtitle {
  font-size: 24rpx;
  color: #666666;
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  min-height: 60rpx;
}

/* 去生成按钮样式 */
.generate-button {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  padding: 10rpx 24rpx;
  gap: 16rpx;
  width: 132rpx;
  height: 60rpx;
  background: #282921;
  border-radius: 32rpx;
}

.generate-button-text {
  font-family: 'PingFang SC';
  font-style: normal;
  font-weight: 400;
  font-size: 28rpx;
  line-height: 40rpx;
  display: flex;
  align-items: center;
  color: #A9FF00;
  flex: none;
  order: 0;
  flex-grow: 0;
}

/* 横向滚动小卡片样式 */
.style-model-scroll-section {
  margin-top: 30rpx;
}

.style-scroll-view {
  width: 100%;
  white-space: nowrap;
  padding: 0;
}

.style-scroll-container {
  display: flex;
  padding: 0 20rpx;
  gap: 20rpx;
}

.style-small-card {
  flex-shrink: 0;
  width: 280rpx;
  background: #FFFFFF;
  border-radius: 16rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.style-small-card:active {
  transform: translateY(-4rpx);
}

.small-card-image-container {
  width: 100%;
  height: 200rpx;
  overflow: hidden;
}

.small-card-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.small-card-info {
  padding: 20rpx;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
}

.small-card-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #333333;
  line-height: 1.2;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  min-height: 68rpx;
  text-align: center;
}

/* 旧的样式已移除，使用新的风格模型布局 */

.blind-box-theme {
  background: #FFFFFF;
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
}

.theme-header {
  margin-bottom: 30rpx;
}

.theme-title {
  font-size: 36rpx;
  font-weight: 600;
  color: #000000;
}

.theme-products {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.theme-product-item {
  position: relative;
  border-radius: 16rpx;
  overflow: hidden;
  height: 200rpx;
}

.theme-product-image-container {
  width: 100%;
  height: 100%;
  position: relative;
}

.theme-product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.theme-product-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
  padding: 30rpx 20rpx 20rpx;
  color: white;
}

.theme-product-title {
  font-size: 28rpx;
  font-weight: 500;
  margin-bottom: 10rpx;
}

.theme-product-price-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.theme-product-price {
  font-size: 32rpx;
  font-weight: 600;
  color: #DCFF6A;
}

.theme-product-original {
  font-size: 24rpx;
  color: #CCCCCC;
  text-decoration: line-through;
  margin-left: 10rpx;
}

.theme-custom-btn {
  background-color: #DCFF6A;
  border-radius: 20rpx;
  padding: 8rpx 20rpx;
}

.custom-btn-text {
  font-size: 24rpx;
  color: #000000;
  font-weight: 500;
}

.style-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20rpx;
}

.style-grid-item {
  position: relative;
  height: 300rpx;
  border-radius: 16rpx;
  overflow: hidden;
}

.style-grid-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.style-grid-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.6));
  padding: 40rpx 20rpx 20rpx;
}

.style-grid-title {
  color: white;
  font-size: 28rpx;
  font-weight: 500;
}
</style> 