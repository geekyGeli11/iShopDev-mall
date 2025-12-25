<template>
  <view class="modal-container" v-if="visible">
    <!-- 遮罩层 -->
    <view class="modal-mask" :class="{ 'show': visible }" @tap="handleMaskClick"></view>

    <!-- 弹窗内容 -->
    <view class="modal-content" :class="{ 'show': visible }" @tap.stop="stopPropagation">
      <!-- 弹窗头部 -->
      <view class="modal-header">
        <!-- 拖拽指示器 -->
        <view class="drag-indicator"></view>
        
        <!-- 标题区域 -->
        <view class="header-content">
          <view class="close-btn" @tap.stop="handleClose">
            <text class="close-icon">×</text>
          </view>
          <text class="modal-title">{{ modalState === 'product-list' ? '选择载体' : '可定制范围' }}</text>
          <view class="settings-btn" v-if="modalState === 'customize-range'" @tap.stop="goBackToProductList">
            <text class="back-icon">←</text>
          </view>
          <view class="settings-btn" v-else>
            <text class="settings-icon">⚙</text>
          </view>
        </view>
        
        <!-- 提示信息 -->
        <view class="tip-banner" v-if="modalState === 'product-list'">
          <text class="tip-text">使用文创模板进行DIY创作，支持下单定制实物~</text>
          <view class="tip-icon">
            <text>💡</text>
          </view>
        </view>

        <!-- 分类标签 - 仅在商品列表状态显示 -->
        <view class="category-tabs-container" v-if="modalState === 'product-list'">
          <scroll-view class="category-tabs-scroll" scroll-x="true" show-scrollbar="false">
            <view class="category-tabs">
              <view
                v-for="(category, index) in categories"
                :key="index"
                class="category-tab"
                :class="{ 'active': selectedCategory === index }"
                @tap="handleCategoryChange(index)"
              >
                <text class="category-text">{{ category.name }}</text>
              </view>
            </view>
          </scroll-view>
        </view>
      </view>
      
      <!-- 商品列表 -->
      <scroll-view
        v-if="modalState === 'product-list'"
        class="product-list"
        scroll-y="true"
        @scrolltolower="loadMoreProducts"
      >
        <view class="product-grid">
          <view
            v-for="(product, index) in productList"
            :key="product.id"
            class="product-item"
            @tap="handleProductClick(product)"
          >
            <!-- 商品图片 -->
            <view class="product-image-container">
              <image
                class="product-image"
                :src="product.image || product.pic || 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/banner1.jpg'"
                mode="aspectFill"
              />
              <!-- 可定制标签 -->
              <view class="customizable-tag">
                <text class="tag-text">可定制</text>
              </view>
            </view>

            <!-- 商品信息 -->
            <view class="product-info">
              <text class="product-name">{{ product.name || product.productName || '商品名称商品名称商品名称商品名称' }}</text>
              <view class="product-price-row">
                <text class="product-price">¥{{ product.price || '360' }}</text>
                <text v-if="product.saleCount" class="sale-count">{{ product.saleCount }}+购买</text>
              </view>
            </view>
          </view>
        </view>

        <!-- 加载更多提示 -->
        <view v-if="loading" class="loading-more">
          <text>加载中...</text>
        </view>

        <!-- 没有更多数据提示 -->
        <view v-if="!hasMore && productList.length > 0" class="no-more">
          <text>没有更多商品了</text>
        </view>

        <!-- 空状态 -->
        <view v-if="productList.length === 0 && !loading" class="empty-state">
          <text>暂无商品</text>
        </view>
      </scroll-view>

      <!-- 定制范围选择 -->
      <view v-if="modalState === 'customize-range'" class="customize-content" :style="{ height: modalHeight }">
        <!-- 商品信息展示 -->
        <view class="selected-product-info" v-if="selectedProduct">
          <image
            class="product-preview"
            :src="selectedProduct.image || selectedProduct.pic || 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/banner1.jpg'"
            mode="aspectFill"
          />
          <view class="product-details">
            <text class="product-title">{{ selectedProduct.name || selectedProduct.productName || '商品名称' }}</text>
            <text class="product-price-text">¥{{ selectedProduct.price || '360' }}</text>
          </view>
        </view>

        <!-- 定制面选择 -->
        <view
          class="customize-areas"
          :class="areasLayoutClass"
        >
          <view
            v-for="(area, index) in customizableAreas"
            :key="area.id"
            class="area-item"
            :class="{ 'selected': selectedAreaIndex === index }"
            @tap="selectArea(index)"
          >
            <!-- 区域名称 -->
            <text class="area-name">{{ area.name }}</text>

            <!-- 区域预览图 -->
            <view class="area-preview">
              <image
                class="area-image"
                :src="area.previewImage || area.previewImageWithMarks"
                mode="aspectFit"
                @load="onImageLoad($event, area)"
                @error="onImageError($event, area)"
              />
              <!-- 可定制区域标识 -->
              <block v-if="area.customizable && area.customizableRegions && area.customizableRegions.length > 0">
                <block v-if="isImageLoaded(area)">
                  <!-- 优先使用蒙版图片 -->
                  <image
                    v-for="(region, regionIndex) in area.customizableRegions"
                    v-if="region.maskImageUrl"
                    :key="regionIndex"
                    class="mask-image"
                    :src="region.maskImageUrl"
                    mode="aspectFit"
                  />
                  <!-- 降级方案：使用SVG路径绘制 -->
                  <view
                    v-for="(region, regionIndex) in area.customizableRegions"
                    v-if="!region.maskImageUrl"
                    :key="regionIndex"
                    class="customizable-region"
                    :class="{ 'circle-region': isCircleRegion(region) }"
                    :style="{
                      position: 'absolute',
                      left: getRegionLeft(region, area) + 'rpx',
                      top: getRegionTop(region, area) + 'rpx',
                      width: getRegionWidth(region, area) + 'rpx',
                      height: getRegionHeight(region, area) + 'rpx',
                      background: 'rgba(169, 255, 0, 0.3)',
                      border: '2rpx dashed #647D00',
                      borderRadius: getRegionBorderRadius(region),
                      boxSizing: 'border-box',
                      zIndex: 10
                    }"
                  ></view>
                </block>
              </block>
              <!-- 默认可定制区域标识（兜底） -->
              <view class="customizable-indicator" v-else-if="area.customizable"></view>
            </view>
          </view>

          <!-- 奇数个时的占位元素 -->
          <view
            v-if="needPlaceholder()"
            class="area-placeholder"
          ></view>
        </view>

        <!-- 开始定制按钮 -->
        <view class="customize-btn" @tap="startCustomize">
          <text class="btn-text">开始定制</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getStyleModelProductList, getProductCustomizableAreas } from '@/api/styleModel.js';

export default {
  name: 'StyleModelProductModal',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    styleModelId: {
      type: [String, Number],
      default: ''
    },
    styleModelName: {
      type: String,
      default: '风格模型'
    }
  },
  data() {
    return {
      // 弹窗状态：'product-list' | 'customize-range'
      modalState: 'product-list',

      // 商品列表相关
      productList: [],
      loading: false,
      hasMore: true,
      page: 1,
      pageSize: 20,
      selectedCategory: 0,
      categories: [
        { name: '全部商品', value: '' },
        { name: '衣帽服饰', value: 'clothing' },
        { name: '文具办公', value: 'stationery' },
        { name: '生活用品', value: 'lifestyle' },
        { name: '数码产品', value: 'digital' }
      ],

      // 定制范围相关
      selectedProduct: null,
      selectedAreaIndex: 0,

      // 图片尺寸缓存
      imageSizeCache: {},

      // 图片加载完成计数器,用于触发视图更新
      imageLoadedCount: 0,

      // 可定制面列表 - 从API加载,不使用默认值
      customizableAreas: [],
      selectedAreaIndex: 0
    }
  },
  watch: {
    visible(newVal, oldVal) {
      console.log('弹窗可见性变化:', newVal, '(旧值:', oldVal, ')');
      if (newVal) {
        // 弹窗打开
        this.modalState = 'product-list';
        this.loadProducts();
      } else {
        // 弹窗关闭 - 立即清空数据,避免渲染错误
        console.log('⚠️ 弹窗即将关闭,立即清空可能导致渲染错误的数据');
        this.customizableAreas = [];
        this.imageSizeCache = {};
        this.imageLoadedCount = 0;
        // 使用 nextTick 确保数据清空后再重置其他状态
        this.$nextTick(() => {
          this.resetData();
        });
      }
    },
    modalState(newVal) {
      console.log('弹窗状态变化:', newVal);
    }
  },
  computed: {
    // 获取定制面布局的CSS类
    areasLayoutClass() {
      const count = this.customizableAreas.length;
      if (count === 1) {
        return 'single-item';
      } else if (count === 2) {
        return 'two-items';
      } else if (count === 3) {
        return 'three-items';
      } else if (count === 4) {
        return 'four-items';
      }
      return 'four-items'; // 默认按4个的布局
    },

    // 计算弹窗高度
    modalHeight() {
      if (this.modalState === 'product-list') {
        return '70vh'; // 商品列表固定高度
      }

      if (this.modalState === 'customize-range') {
        // 使用固定高度，避免动态计算导致的问题
        return '75vh';
      }

      return '60vh'; // 默认高度
    }
  },
  methods: {
    // 阻止事件冒泡
    stopPropagation() {
      // 空方法,仅用于阻止事件冒泡
    },

    // 处理遮罩点击
    handleMaskClick() {
      console.log('🎭 遮罩层点击,关闭弹窗');
      this.handleClose();
    },

    // 关闭弹窗
    handleClose() {
      console.log('❌ 关闭弹窗, 当前状态:', this.modalState);
      // 重置状态
      this.modalState = 'product-list';
      this.selectedProduct = null;
      this.selectedAreaIndex = 0;
      this.customizableAreas = [];
      this.imageSizeCache = {}; // 清空图片尺寸缓存
      this.imageLoadedCount = 0; // 重置计数器
      console.log('✅ 状态已重置, 触发关闭事件');
      // 触发关闭事件
      this.$emit('close');
    },
    
    // 分类切换
    handleCategoryChange(index) {
      this.selectedCategory = index;
      this.resetData();
      this.loadProducts();
    },
    
    // 商品点击 - 切换到定制范围状态
    async handleProductClick(product) {
      this.selectedProduct = product;
      this.modalState = 'customize-range';

      // 加载该商品的可定制面信息
      await this.loadCustomizableAreas(product.id);
    },
    
    // 加载商品列表
    async loadProducts() {
      if (this.loading || !this.hasMore || !this.styleModelId) return;

      this.loading = true;
      try {
        const response = await getStyleModelProductList(this.styleModelId, {
          page: this.page,
          pageSize: this.pageSize,
          category: this.categories[this.selectedCategory].value
        });

        if (response && response.code === 200) {
          const products = response.data.list || response.data || [];

          if (this.page === 1) {
            this.productList = products;
          } else {
            this.productList.push(...products);
          }

          this.page++;
          this.hasMore = products.length === this.pageSize;

          console.log('成功加载商品列表:', products.length, '个商品');
        } else {
          throw new Error(response?.message || '获取商品列表失败');
        }
      } catch (error) {
        console.error('API调用失败，使用模拟数据:', error);

        // 如果API调用失败，使用模拟数据兜底
        const mockProducts = this.generateMockProducts();

        if (this.page === 1) {
          this.productList = mockProducts;
        } else {
          this.productList.push(...mockProducts);
        }

        this.page++;
        this.hasMore = mockProducts.length === this.pageSize;

        // 只在开发环境显示模拟数据提示
        if (process.env.NODE_ENV === 'development') {
          uni.showToast({
            title: '使用模拟数据',
            icon: 'none',
            duration: 1500
          });
        }
      } finally {
        this.loading = false;
      }
    },
    
    // 加载更多商品
    loadMoreProducts() {
      this.loadProducts();
    },
    
    // 重置数据
    resetData() {
      console.log('🔄 重置弹窗数据');
      this.modalState = 'product-list';
      this.productList = [];
      this.page = 1;
      this.hasMore = true;
      this.loading = false;
      this.selectedProduct = null;
      this.selectedAreaIndex = 0;
      // 清空可定制面数据,避免关闭时渲染错误
      this.customizableAreas = [];
      this.imageSizeCache = {};
      this.imageLoadedCount = 0;
      console.log('✅ 弹窗数据已重置');
    },

    // 返回商品列表
    goBackToProductList() {
      console.log('🔙 返回商品列表, 当前状态:', this.modalState);
      this.modalState = 'product-list';
      this.selectedProduct = null;
      this.selectedAreaIndex = 0;
      this.customizableAreas = [];
      this.imageSizeCache = {}; // 清空图片尺寸缓存
      this.imageLoadedCount = 0; // 重置计数器
      console.log('✅ 已切换到商品列表状态');
      // 不需要强制更新,Vue会自动响应式更新
    },

    // 加载商品的可定制面信息
    async loadCustomizableAreas(productId) {
      try {
        console.log('🔄 开始加载商品可定制面信息, productId:', productId);
        const response = await getProductCustomizableAreas(productId);

        console.log('📦 API返回数据:', response);

        if (response && response.code === 200 && response.data) {
          this.customizableAreas = response.data;
          console.log('✅ 成功加载可定制面信息:', this.customizableAreas.length, '个定制面');

          // 详细输出每个面的信息
          this.customizableAreas.forEach((area, index) => {
            console.log(`📋 定制面 ${index + 1}:`, {
              id: area.id,
              name: area.name,
              customizable: area.customizable,
              regionsCount: area.customizableRegions?.length || 0,
              previewImage: area.previewImage
            });

            // 输出区域详情
            if (area.customizableRegions && area.customizableRegions.length > 0) {
              area.customizableRegions.forEach((region, rIndex) => {
                console.log(`  🎯 区域 ${rIndex + 1}:`, {
                  id: region.id,
                  name: region.name,
                  bounds: region.bounds,
                  pathData: region.pathData
                });
              });
            }
          });
        } else {
          throw new Error(response?.message || '获取可定制面信息失败');
        }
      } catch (error) {
        console.error('❌ API调用失败:', error);

        // 显示错误提示
        uni.showToast({
          title: '加载定制面失败',
          icon: 'none',
          duration: 2000
        });

        // 清空数据
        this.customizableAreas = [];
      }
    },

    // 选择定制面
    selectArea(index) {
      this.selectedAreaIndex = index;
    },

    // 开始定制
    startCustomize() {
      const selectedArea = this.customizableAreas[this.selectedAreaIndex];
      if (!selectedArea || !this.selectedProduct) {
        uni.showToast({
          title: '请选择定制面',
          icon: 'none'
        });
        return;
      }

      // 关闭弹窗并跳转到DIY编辑器
      this.$emit('close');

      // 跳转到DIY编辑器页面
      uni.navigateTo({
        url: `/subpackages/diy/customize?productId=${this.selectedProduct.id}&areaId=${selectedArea.id}&styleModelId=${this.styleModelId}`
      });
    },

    // 判断是否需要占位元素
    needPlaceholder() {
      const count = this.customizableAreas.length;
      return count > 2 && count % 2 === 1;
    },

    // 解析矩形SVG路径
    parseRectFromPath(pathData) {
      if (!pathData) return null;

      try {
        // 矩形路径格式: M x1 y1 L x2 y1 L x2 y2 L x1 y2 Z
        const rectRegex = /M\s+([\d.]+)\s+([\d.]+)\s+L\s+([\d.]+)\s+([\d.]+)\s+L\s+([\d.]+)\s+([\d.]+)\s+L\s+([\d.]+)\s+([\d.]+)/;
        const match = pathData.match(rectRegex);

        if (match) {
          const [, x1, y1, x2, y2_1, x2_2, y2, x1_2, y2_2] = match.map(Number);

          // 计算矩形的位置和尺寸
          const x = Math.min(x1, x2);
          const y = Math.min(y1, y2);
          const width = Math.abs(x2 - x1);
          const height = Math.abs(y2 - y1);

          console.log('✅ 解析矩形路径成功:', {
            pathData,
            解析结果: { x, y, width, height }
          });

          return { x, y, width, height };
        }
      } catch (error) {
        console.error('解析矩形路径失败:', error);
      }

      return null;
    },

    // 解析SVG路径，判断是否为圆形
    parseCircleFromPath(pathData) {
      if (!pathData) return null;

      try {
        // 匹配圆形SVG路径：M x y A rx ry 0 1 1 x2 y2 A rx ry 0 1 1 x y Z
        const circleRegex = /M\s+([\d.]+)\s+([\d.]+)\s+A\s+([\d.]+)\s+([\d.]+)\s+0\s+1\s+1\s+([\d.]+)\s+([\d.]+)\s+A\s+([\d.]+)\s+([\d.]+)\s+0\s+1\s+1\s+([\d.]+)\s+([\d.]+)/;
        const match = pathData.match(circleRegex);

        if (match) {
          const [, startX, startY, rx1, ry1, midX, midY, rx2, ry2, endX, endY] = match.map(Number);

          // 计算圆心：圆形路径的起点和中点在圆周上，圆心在它们的中点
          const centerX = (parseFloat(startX) + parseFloat(midX)) / 2;
          const centerY = (parseFloat(startY) + parseFloat(midY)) / 2;
          const radius = parseFloat(rx1); // 假设是正圆

          console.log('✅ 解析圆形路径成功:', {
            pathData,
            圆心: `(${centerX}, ${centerY})`,
            半径: radius
          });

          return { centerX, centerY, radius };
        }
      } catch (error) {
        console.error('解析SVG路径失败:', error);
      }

      return null;
    },

    // 判断区域是否为圆形
    isCircleRegion(region) {
      if (!region || !region.pathData) {
        console.log('⚠️ 区域数据不完整:', region);
        return false;
      }
      const isCircle = this.parseCircleFromPath(region.pathData) !== null;
      console.log('🔍 判断区域形状:', {
        regionId: region.id,
        regionName: region.name,
        isCircle: isCircle,
        pathData: region.pathData
      });
      return isCircle;
    },

    // 图片加载完成事件
    onImageLoad(event, area) {
      console.log('📸 图片加载完成:', {
        areaId: area?.id,
        areaName: area?.name,
        hasRegions: area?.customizableRegions?.length > 0
      });

      // 检查area是否存在
      if (!area) {
        console.warn('⚠️ 图片加载完成但area为空');
        return;
      }

      // 获取图片的真实尺寸
      if (event && event.detail) {
        const { width, height } = event.detail;
        const imageUrl = area.previewImage || area.previewImageWithMarks;

        // 使用 $set 缓存图片尺寸,确保响应式更新
        this.$set(this.imageSizeCache, imageUrl, { width, height });

        // 增加计数器,触发视图更新
        this.imageLoadedCount++;

        console.log('✅ 缓存图片尺寸:', {
          areaId: area.id,
          areaName: area.name,
          url: imageUrl,
          width,
          height,
          loadedCount: this.imageLoadedCount
        });

        // 如果有定制区域,输出区域信息
        if (area.customizableRegions && area.customizableRegions.length > 0) {
          console.log('🎯 该面包含定制区域:', area.customizableRegions.length, '个');
          area.customizableRegions.forEach((region, index) => {
            console.log(`  区域 ${index + 1}:`, {
              id: region.id,
              name: region.name,
              pathData: region.pathData,
              bounds: region.bounds
            });
          });
        }

        // 不要使用 $forceUpdate(),让 Vue 自动响应式更新
        // this.$forceUpdate(); // 移除这行,避免触发 cos of null 错误
      }
    },

    // 图片加载错误事件
    onImageError(event, area) {
      console.error('图片加载失败:', event, area);

      // 检查area是否存在
      if (!area) {
        console.warn('图片加载失败但area为空');
        return;
      }
    },

    // 计算区域的基础参数
    getRegionBaseParams(region) {
      if (!region) return { scale: 1, offsetX: 0, offsetY: 0 };

      // 预览图容器尺寸（270rpx x 270rpx）
      const containerWidth = 270;
      const containerHeight = 270;

      // 获取当前区域对应的图片尺寸
      const currentArea = this.customizableAreas.find(area =>
        area.customizableRegions && area.customizableRegions.includes(region)
      );

      let originalImageWidth = 279;  // 默认值
      let originalImageHeight = 181; // 默认值

      if (currentArea) {
        const imageUrl = currentArea.previewImage || currentArea.previewImageWithMarks;
        const cachedSize = this.imageSizeCache[imageUrl];

        if (cachedSize) {
          originalImageWidth = cachedSize.width;
          originalImageHeight = cachedSize.height;
          console.log('使用缓存的图片尺寸:', cachedSize);
        } else {
          console.log('使用默认图片尺寸:', { originalImageWidth, originalImageHeight });
        }
      }

      // 计算图片在容器中的实际显示尺寸（aspectFit模式）
      const imageAspectRatio = originalImageWidth / originalImageHeight;
      const containerAspectRatio = containerWidth / containerHeight;

      let displayWidth, displayHeight, offsetX, offsetY;

      if (imageAspectRatio > containerAspectRatio) {
        // 图片较宽，按宽度缩放
        displayWidth = containerWidth;
        displayHeight = containerWidth / imageAspectRatio;
        offsetX = 0;
        offsetY = (containerHeight - displayHeight) / 2;
      } else {
        // 图片较高，按高度缩放
        displayHeight = containerHeight;
        displayWidth = containerHeight * imageAspectRatio;
        offsetX = (containerWidth - displayWidth) / 2;
        offsetY = 0;
      }

      // 计算缩放比例
      const scale = displayWidth / originalImageWidth;

      console.log('区域基础参数计算:', {
        原始图片尺寸: { originalImageWidth, originalImageHeight },
        容器尺寸: { containerWidth, containerHeight },
        显示尺寸: { displayWidth, displayHeight },
        偏移: { offsetX, offsetY },
        缩放比例: scale
      });

      return { scale, offsetX, offsetY };
    },

    // 获取图片原始尺寸
    getImageSize(area) {
      if (!area) {
        console.warn('⚠️ getImageSize: area为空,使用默认尺寸');
        return { width: 279, height: 181 }; // 默认尺寸
      }

      const imageUrl = area.previewImage || area.previewImageWithMarks;

      // 检查 imageUrl 是否有效
      if (!imageUrl) {
        console.warn('⚠️ getImageSize: imageUrl为空,使用默认尺寸');
        return { width: 279, height: 181 };
      }

      const cached = this.imageSizeCache[imageUrl];

      if (cached) {
        console.log('✅ 使用缓存的图片尺寸:', {
          areaId: area.id,
          areaName: area.name,
          cached
        });
        return cached;
      }

      // 返回默认尺寸
      console.warn('⚠️ 图片尺寸未缓存,使用默认尺寸:', {
        areaId: area.id,
        areaName: area.name,
        imageUrl
      });
      return { width: 279, height: 181 };
    },

    // 计算区域左边距
    getRegionLeft(region, area) {
      if (!region) return 0;

      const circleData = this.parseCircleFromPath(region.pathData);
      const rectData = this.parseRectFromPath(region.pathData);

      // 图片在容器中的偏移（从CSS得知）
      const imageOffsetX = 25; // .area-image 的 left: 25rpx

      // 获取图片原始尺寸
      const imageSize = this.getImageSize(area);
      const originalImageWidth = imageSize.width;
      const originalImageHeight = imageSize.height;

      // 图片容器尺寸
      const containerSize = 270;

      // 计算图片的实际显示尺寸（aspectFit模式）
      const imageAspectRatio = originalImageWidth / originalImageHeight;
      const containerAspectRatio = 1; // 270/270 = 1

      let displayWidth, horizontalOffset, scale;
      if (imageAspectRatio > containerAspectRatio) {
        // 图片较宽，按宽度缩放
        displayWidth = containerSize;
        horizontalOffset = 0;
        scale = containerSize / originalImageWidth;
      } else {
        // 图片较高，按高度缩放，左右有空白
        scale = containerSize / originalImageHeight;
        displayWidth = originalImageWidth * scale;
        horizontalOffset = (containerSize - displayWidth) / 2;
      }

      if (circleData) {
        // 圆形区域
        const scaledCenterX = circleData.centerX * scale;
        const scaledRadius = circleData.radius * scale;
        const result = Math.round(imageOffsetX + horizontalOffset + scaledCenterX - scaledRadius);

        console.log('🔍 圆形区域左边距计算:', {
          原始圆心X: circleData.centerX,
          原始半径: circleData.radius,
          缩放后圆心X: scaledCenterX.toFixed(1),
          缩放后半径: scaledRadius.toFixed(1),
          最终left: result
        });

        return result;
      } else if (rectData) {
        // 矩形区域 - 使用从pathData解析的坐标
        const x = rectData.x;
        const result = Math.round(imageOffsetX + horizontalOffset + (x * scale));

        console.log('🔍 矩形区域左边距计算:', {
          原始x: x,
          缩放比例: scale.toFixed(3),
          水平偏移: horizontalOffset.toFixed(1),
          最终left: result
        });

        return result;
      } else {
        // 兜底：使用region的x属性
        const x = region.x || 0;
        const result = Math.round(imageOffsetX + horizontalOffset + (x * scale));
        return result;
      }
    },

    // 计算区域顶边距
    getRegionTop(region, area) {
      if (!region) return 0;

      const circleData = this.parseCircleFromPath(region.pathData);
      const rectData = this.parseRectFromPath(region.pathData);

      // 图片在容器中的偏移（从CSS得知）
      const imageOffsetY = 25; // .area-image 的 top: 25rpx

      // 获取图片原始尺寸
      const imageSize = this.getImageSize(area);
      const originalImageWidth = imageSize.width;
      const originalImageHeight = imageSize.height;

      // 图片容器尺寸
      const containerSize = 270;

      // 计算图片的实际显示尺寸（aspectFit模式）
      const imageAspectRatio = originalImageWidth / originalImageHeight;
      const containerAspectRatio = 1; // 270/270 = 1

      let displayHeight, verticalOffset, scale;
      if (imageAspectRatio > containerAspectRatio) {
        // 图片较宽，按宽度缩放，上下有空白
        scale = containerSize / originalImageWidth;
        displayHeight = originalImageHeight * scale;
        verticalOffset = (containerSize - displayHeight) / 2;
      } else {
        // 图片较高，按高度缩放
        scale = containerSize / originalImageHeight;
        displayHeight = containerSize;
        verticalOffset = 0;
      }

      if (circleData) {
        // 圆形区域
        const scaledCenterY = circleData.centerY * scale;
        const scaledRadius = circleData.radius * scale;
        const result = Math.round(imageOffsetY + verticalOffset + scaledCenterY - scaledRadius);

        console.log('🔍 圆形区域顶边距计算:', {
          原始圆心Y: circleData.centerY,
          原始半径: circleData.radius,
          缩放后圆心Y: scaledCenterY.toFixed(1),
          缩放后半径: scaledRadius.toFixed(1),
          最终top: result
        });

        return result;
      } else if (rectData) {
        // 矩形区域 - 使用从pathData解析的坐标
        const y = rectData.y;
        const result = Math.round(imageOffsetY + verticalOffset + (y * scale));

        console.log('🔍 矩形区域顶边距计算:', {
          原始y: y,
          缩放比例: scale.toFixed(3),
          垂直偏移: verticalOffset.toFixed(1),
          最终top: result
        });

        return result;
      } else {
        // 兜底：使用region的y属性
        const y = region.y || 0;
        const result = Math.round(imageOffsetY + verticalOffset + (y * scale));
        return result;
      }
    },

    // 计算区域宽度
    getRegionWidth(region, area) {
      if (!region) return 50;

      const circleData = this.parseCircleFromPath(region.pathData);
      const rectData = this.parseRectFromPath(region.pathData);

      // 获取图片原始尺寸
      const imageSize = this.getImageSize(area);
      const originalImageWidth = imageSize.width;
      const originalImageHeight = imageSize.height;

      // 图片容器尺寸
      const containerSize = 270;

      // 计算图片的实际显示尺寸（aspectFit模式）
      const imageAspectRatio = originalImageWidth / originalImageHeight;
      const containerAspectRatio = 1;

      let scale;
      if (imageAspectRatio > containerAspectRatio) {
        // 图片较宽，按宽度缩放
        scale = containerSize / originalImageWidth;
      } else {
        // 图片较高，按高度缩放
        scale = containerSize / originalImageHeight;
      }

      if (circleData) {
        // 圆形区域
        const scaledRadius = circleData.radius * scale;
        const diameter = scaledRadius * 2;
        return Math.round(diameter);
      } else if (rectData) {
        // 矩形区域 - 使用从pathData解析的宽度
        const width = rectData.width;
        return Math.round(width * scale);
      } else {
        // 兜底：使用region的width属性
        const width = region.width || 50;
        return Math.round(width * scale);
      }
    },

    // 计算区域高度
    getRegionHeight(region, area) {
      if (!region) return 50;

      const circleData = this.parseCircleFromPath(region.pathData);
      const rectData = this.parseRectFromPath(region.pathData);

      // 获取图片原始尺寸
      const imageSize = this.getImageSize(area);
      const originalImageWidth = imageSize.width;
      const originalImageHeight = imageSize.height;

      // 图片容器尺寸
      const containerSize = 270;

      // 计算图片的实际显示尺寸（aspectFit模式）
      const imageAspectRatio = originalImageWidth / originalImageHeight;
      const containerAspectRatio = 1;

      let scale;
      if (imageAspectRatio > containerAspectRatio) {
        // 图片较宽，按宽度缩放
        scale = containerSize / originalImageWidth;
      } else {
        // 图片较高，按高度缩放
        scale = containerSize / originalImageHeight;
      }

      if (circleData) {
        // 圆形区域
        const scaledRadius = circleData.radius * scale;
        const diameter = scaledRadius * 2;
        return Math.round(diameter);
      } else if (rectData) {
        // 矩形区域 - 使用从pathData解析的高度
        const height = rectData.height;
        return Math.round(height * scale);
      } else {
        // 兜底：使用region的height属性
        const height = region.height || 50;
        return Math.round(height * scale);
      }
    },

    // 计算区域边框圆角
    getRegionBorderRadius(region) {
      if (!region) return '8rpx';

      const circleData = this.parseCircleFromPath(region.pathData);

      if (circleData) {
        // 圆形区域
        return '50%';
      } else {
        // 矩形区域
        return '8rpx';
      }
    },

    // 计算蒙版图片左边距
    getMaskLeft(region, area) {
      if (!region) return 0;

      // 图片在容器中的偏移（从CSS得知）
      const imageOffsetX = 25; // .area-image 的 left: 25rpx

      // 获取图片原始尺寸
      const imageSize = this.getImageSize(area);
      const originalImageWidth = imageSize.width;
      const originalImageHeight = imageSize.height;

      // 图片容器尺寸
      const containerSize = 270;

      // 计算图片的实际显示尺寸（aspectFit模式）
      const imageAspectRatio = originalImageWidth / originalImageHeight;
      const containerAspectRatio = 1; // 270/270 = 1

      let displayWidth, horizontalOffset, scale;
      if (imageAspectRatio > containerAspectRatio) {
        // 图片较宽，按宽度缩放
        displayWidth = containerSize;
        horizontalOffset = 0;
        scale = containerSize / originalImageWidth;
      } else {
        // 图片较高，按高度缩放，左右有空白
        scale = containerSize / originalImageHeight;
        displayWidth = originalImageWidth * scale;
        horizontalOffset = (containerSize - displayWidth) / 2;
      }

      // 使用region的x坐标
      const regionX = region.x || 0;
      const scaledX = regionX * scale;

      return Math.round(imageOffsetX + horizontalOffset + scaledX);
    },

    // 计算蒙版图片顶边距
    getMaskTop(region, area) {
      if (!region) return 0;

      // 图片在容器中的偏移（从CSS得知）
      const imageOffsetY = 25; // .area-image 的 top: 25rpx

      // 获取图片原始尺寸
      const imageSize = this.getImageSize(area);
      const originalImageWidth = imageSize.width;
      const originalImageHeight = imageSize.height;

      // 图片容器尺寸
      const containerSize = 270;

      // 计算图片的实际显示尺寸（aspectFit模式）
      const imageAspectRatio = originalImageWidth / originalImageHeight;
      const containerAspectRatio = 1; // 270/270 = 1

      let displayHeight, verticalOffset, scale;
      if (imageAspectRatio > containerAspectRatio) {
        // 图片较宽，按宽度缩放，上下有空白
        scale = containerSize / originalImageWidth;
        displayHeight = originalImageHeight * scale;
        verticalOffset = (containerSize - displayHeight) / 2;
      } else {
        // 图片较高，按高度缩放
        scale = containerSize / originalImageHeight;
        displayHeight = containerSize;
        verticalOffset = 0;
      }

      // 使用region的y坐标
      const regionY = region.y || 0;
      const scaledY = regionY * scale;

      return Math.round(imageOffsetY + verticalOffset + scaledY);
    },

    // 计算蒙版图片宽度
    getMaskWidth(region, area) {
      if (!region) return 50;

      // 获取图片原始尺寸
      const imageSize = this.getImageSize(area);
      const originalImageWidth = imageSize.width;
      const originalImageHeight = imageSize.height;

      // 图片容器尺寸
      const containerSize = 270;

      // 计算图片的实际显示尺寸（aspectFit模式）
      const imageAspectRatio = originalImageWidth / originalImageHeight;
      const containerAspectRatio = 1;

      let scale;
      if (imageAspectRatio > containerAspectRatio) {
        // 图片较宽，按宽度缩放
        scale = containerSize / originalImageWidth;
      } else {
        // 图片较高，按高度缩放
        scale = containerSize / originalImageHeight;
      }

      // 使用region的width属性
      const regionWidth = region.width || 50;
      return Math.round(regionWidth * scale);
    },

    // 计算蒙版图片高度
    getMaskHeight(region, area) {
      if (!region) return 50;

      // 获取图片原始尺寸
      const imageSize = this.getImageSize(area);
      const originalImageWidth = imageSize.width;
      const originalImageHeight = imageSize.height;

      // 图片容器尺寸
      const containerSize = 270;

      // 计算图片的实际显示尺寸（aspectFit模式）
      const imageAspectRatio = originalImageWidth / originalImageHeight;
      const containerAspectRatio = 1;

      let scale;
      if (imageAspectRatio > containerAspectRatio) {
        // 图片较宽，按宽度缩放
        scale = containerSize / originalImageWidth;
      } else {
        // 图片较高，按高度缩放
        scale = containerSize / originalImageHeight;
      }

      // 使用region的height属性
      const regionHeight = region.height || 50;
      return Math.round(regionHeight * scale);
    },

    // 检查图片是否已加载
    isImageLoaded(area) {
      if (!area) {
        console.log('⚠️ isImageLoaded: area为空');
        return false;
      }

      // 访问 imageLoadedCount 以建立响应式依赖
      const count = this.imageLoadedCount;

      const imageUrl = area.previewImage || area.previewImageWithMarks;
      const loaded = !!this.imageSizeCache[imageUrl];

      if (!loaded) {
        console.log('⏳ 图片未加载:', {
          areaId: area.id,
          areaName: area.name,
          imageUrl: imageUrl,
          loadedCount: count,
          cacheKeys: Object.keys(this.imageSizeCache),
          cacheSize: Object.keys(this.imageSizeCache).length
        });
      } else {
        console.log('✅ 图片已加载,可以渲染区域:', {
          areaId: area.id,
          areaName: area.name,
          imageUrl: imageUrl,
          loadedCount: count
        });
      }

      return loaded;
    },


    
    // 生成模拟数据
    generateMockProducts() {
      const products = [];
      for (let i = 0; i < this.pageSize; i++) {
        products.push({
          id: Date.now() + i,
          name: `商品名称商品名称商品名称商品名称 ${i + 1}`,
          price: Math.floor(Math.random() * 500) + 100,
          saleCount: Math.floor(Math.random() * 1000) + 100,
          image: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/banner1.jpg'
        });
      }
      return products;
    }
  }
}
</script>

<style lang="scss" scoped>
.modal-container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999;
}

.modal-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  opacity: 0;
  transition: opacity 0.3s ease;
  
  &.show {
    opacity: 1;
  }
}

.modal-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: auto;
  max-height: 85vh;
  background: #FFFFFF;
  border-radius: 16rpx 16rpx 0 0;
  transform: translateY(100%);
  transition: transform 0.3s ease;
  display: flex;
  flex-direction: column;

  &.show {
    transform: translateY(0);
  }
}

.modal-header {
  padding: 20rpx 30rpx;
  border-bottom: 1rpx solid #EEEEEE;
  flex-shrink: 0;
  position: relative;
  z-index: 100;
}

.drag-indicator {
  width: 60rpx;
  height: 6rpx;
  background: #EEEEEE;
  border-radius: 3rpx;
  margin: 0 auto 20rpx;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20rpx;
}

.close-btn, .settings-btn {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #EEEEEE;
  border-radius: 50%;
  position: relative;
  z-index: 1000;
  cursor: pointer;
  transition: all 0.2s ease;

  &:active {
    background: #DDDDDD;
    transform: scale(0.95);
  }
}

.close-icon, .settings-icon {
  font-size: 32rpx;
  color: #666666;
}

.modal-title {
  font-size: 32rpx;
  font-weight: 500;
  color: #282921;
}

.tip-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #F5FEE8;
  border-radius: 8rpx;
  padding: 10rpx 20rpx;
  margin-bottom: 20rpx;
}

.tip-text {
  font-size: 24rpx;
  color: #647D00;
  flex: 1;
}

.tip-icon {
  margin-left: 10rpx;
}

.category-tabs-container {
  width: 100%;
  margin-bottom: 20rpx;
}

.category-tabs-scroll {
  width: 100%;
  white-space: nowrap;
}

.category-tabs {
  display: inline-flex;
  gap: 16rpx;
  padding: 0 4rpx;
  min-width: 100%;
}

.category-tab {
  flex-shrink: 0;
  min-width: 120rpx;
  padding: 8rpx 22rpx;
  border-radius: 8rpx;
  border: 1rpx solid #282921;
  background: #FFFFFF;
  text-align: center;
  transition: all 0.3s ease;

  &.active {
    background: #282921;

    .category-text {
      color: #A9FF00;
    }
  }

  &:active {
    transform: scale(0.95);
  }
}

.category-text {
  font-size: 24rpx;
  color: #282921;
  white-space: nowrap;
  line-height: 1.2;
}

.product-list {
  flex: 1;
  padding: 20rpx 30rpx;
}

.product-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20rpx;
}

.product-item {
  background: #FFFFFF;
  border-radius: 16rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  
  &:active {
    transform: translateY(-4rpx);
  }
}

.product-image-container {
  position: relative;
  width: 100%;
  height: 280rpx;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.customizable-tag {
  position: absolute;
  top: 0;
  left: 0;
  background: #91F104;
  padding: 8rpx 16rpx;
  border-radius: 0 0 16rpx 0;
}

.tag-text {
  font-size: 20rpx;
  color: #0A0D05;
  font-weight: 400;
}

.product-info {
  padding: 20rpx;
}

.product-name {
  font-size: 28rpx;
  color: #0A0D05;
  font-weight: 400;
  line-height: 1.4;
  margin-bottom: 16rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-price {
  font-size: 32rpx;
  color: #647D00;
  font-weight: 700;
}

.sale-count {
  font-size: 24rpx;
  color: #999999;
  font-weight: 400;
}

.loading-more, .no-more, .empty-state {
  text-align: center;
  padding: 40rpx 0;
  color: #999999;
  font-size: 28rpx;
}

/* 定制范围相关样式 */
.customize-content {
  flex: 1;
  padding: 20rpx 30rpx 30rpx;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
  display: flex;
  flex-direction: column;
  position: relative;
  z-index: 1;
}

.selected-product-info {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 20rpx;
  background: #F8F8F8;
  border-radius: 16rpx;
  margin-bottom: 30rpx;
}

.product-preview {
  width: 120rpx;
  height: 120rpx;
  border-radius: 12rpx;
  object-fit: cover;
}

.product-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.product-title {
  font-size: 28rpx;
  color: #0A0D05;
  font-weight: 500;
  line-height: 1.4;
}

.product-price-text {
  font-size: 32rpx;
  color: #647D00;
  font-weight: 700;
}

.customize-areas {
  display: grid;
  grid-template-columns: repeat(2, 320rpx);
  gap: 32rpx 24rpx;
  margin-bottom: 40rpx;
  padding: 0 32rpx;
  justify-content: center;
  min-height: fit-content;
}

.customize-areas.single-item {
  grid-template-columns: 320rpx;
  justify-content: center;
}

.customize-areas.two-items {
  grid-template-columns: repeat(2, 320rpx);
  justify-content: center;
}

.customize-areas.three-items {
  grid-template-columns: repeat(2, 320rpx);
  justify-content: center;
}

.customize-areas.three-items .area-item:nth-child(3) {
  grid-column: 1 / -1;
  justify-self: center;
}

.customize-areas.four-items {
  grid-template-columns: repeat(2, 320rpx);
  justify-content: center;
}

.area-placeholder {
  visibility: hidden;
}

.area-item {
  position: relative;
  width: 320rpx;
  height: 410rpx;
  transition: all 0.3s ease;

  &.selected {
    .area-preview {
      border-color: #A9FF00;
      box-shadow: 0 4rpx 12rpx rgba(169, 255, 0, 0.2);
    }
  }

  &:active {
    transform: scale(0.98);
    opacity: 0.8;
  }
}

.area-preview {
  position: absolute;
  width: 320rpx;
  height: 320rpx;
  left: 0rpx;
  top: 76rpx;

  background: #FFFFFF;
  border: 2rpx solid #000000;
  border-radius: 16rpx;
  box-sizing: border-box;
  overflow: hidden;
  transition: all 0.3s ease;
}

.area-image {
  position: absolute;
  width: 270rpx;
  height: 270rpx;
  left: 25rpx;
  top: 25rpx;

  border-radius: 16rpx;
  object-fit: cover;
}

.mask-image {
  position: absolute;
  width: 270rpx;
  height: 270rpx;
  left: 25rpx;
  top: 25rpx;

  border-radius: 16rpx;
  opacity: 0.5;
  z-index: 10;
  pointer-events: none;
}

.customizable-indicator {
  position: absolute;
  width: 174rpx;
  height: 208rpx;
  left: 65rpx;
  top: 109rpx;

  background: rgba(169, 255, 0, 0.3);
  border: 2rpx dashed #647D00;
  border-radius: 8rpx;
  box-sizing: border-box;
  pointer-events: none;
  z-index: 10;
}

.customizable-region {
  /* 样式通过内联样式动态设置 */

  &.circle-region {
    /* 圆形区域特殊样式 */
  }
}

.area-name {
  position: absolute;
  width: 64rpx;
  height: 44rpx;
  left: calc(50% - 32rpx);
  top: 16rpx;

  font-family: 'PingFang SC', sans-serif;
  font-style: normal;
  font-weight: 400;
  font-size: 32rpx;
  line-height: 44rpx;
  text-align: center;
  color: #0A0D05;
}

.customize-btn {
  width: 100%;
  height: 88rpx;
  background: #000000;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  margin-top: 20rpx;
  flex-shrink: 0;

  &:active {
    opacity: 0.8;
    transform: translateY(2rpx);
  }
}

.btn-text {
  font-size: 28rpx;
  color: #A9FF00;
  font-weight: 500;
}

.back-icon {
  font-size: 32rpx;
  color: #666666;
}
</style>
