<template>
  <view class="gifts-list-container">
    <!-- 导航栏 -->
    <nav-bar :placeholder="true" bg-color="rgba(255, 255, 255, 1)" color="#000000" :has-slot="true" :back="true">
      <view class="nav-title-container">
        <text class="nav-title">送好友好礼</text>
      </view>
    </nav-bar>

    <!-- Banner区域 -->
    <view class="banner-container" v-if="!isSearching">
      <image class="banner-image"
        src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/gift-bag/gift_banner.png"
        mode="aspectFill"></image>
    </view>

    <!-- 搜索栏 -->
    <view class="search-header">
      <view class="search-container">
        <view class="search-icon-btn">
          <image class="search-icon" src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/search.png">
          </image>
        </view>
        <input class="search-input" type="text" v-model="keyword" placeholder="搜索礼品" confirm-type="search"
          @confirm="performSearch" @focus="onSearchFocus" />
        <view class="clear-btn" v-if="keyword" @tap="clearSearch">
          <text class="clear-text">✕</text>
        </view>
      </view>
    </view>

    <!-- 品牌列表 -->
    <view class="brand-list-container" v-if="!isSearching">
      <scroll-view class="brand-list" scroll-x="true" show-scrollbar="false">
        <view class="brand-item" :class="{ active: selectedBrandId === 0 && !isSearching }" @tap="selectBrand(0)">
          <image v-if="selectedBrandId === 0 && !isSearching" class="tab-indicator" src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/cart/tab-active-v.png" mode="aspectFit"></image>
          <text class="brand-name">全部</text>
        </view>
        <view v-for="(brand, index) in brandList" :key="brand.id" class="brand-item"
          :class="{ active: selectedBrandId === brand.id && !isSearching }" @tap="selectBrand(brand.id)">
          <image v-if="selectedBrandId === brand.id && !isSearching" class="tab-indicator" src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/cart/tab-active-v.png" mode="aspectFit"></image>
          <text class="brand-name">{{ brand.name }}</text>
        </view>
      </scroll-view>
    </view>

    <!-- 礼品列表 - 普通状态 -->
    <view class="gifts-list" :style="{ paddingBottom: '120rpx' }" v-if="!isSearching">
      <!-- 无结果提示 -->
      <view class="no-results" v-if="productList.length === 0 && !isLoading">
        <image class="no-results-image"
          src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/search/no-result.png"></image>
        <text class="no-results-text">抱歉，暂无相关礼品</text>
      </view>

      <!-- 产品列表 -->
      <view class="product-list" v-if="productList.length > 0">
        <view class="product-big-card" v-for="(item, index) in productList" :key="index" @tap="navToDetailPage(item)">
          <view class="product-card-background"></view>
          <view class="product-card-content">
            <view class="product-image-container">
              <image class="product-big-image" :src="item.pic" mode="aspectFill"></image>
              <view class="product-tags" v-if="item.newStatus === 1">
                <view class="product-tag new">新品</view>
              </view>
            </view>
            <view class="product-info-container">
              <view class="product-info">
                <view> <text class="product-title">{{ item.name }}</text> <text class="product-desc"
                    v-if="item.subTitle">{{ item.subTitle }}</text> </view>
                <view class="product-price-area" @tap.stop="toggleSpec">
                  <image class="price-tag-bg"
                    src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/product_card/price_tag/price_area_bg1.png"
                    mode="aspectFit"></image>
                  <view class="product-category-name">
                    <text v-if="item.productCategoryName" class="category-text">
                      {{ item.productCategoryName ? (item.productCategoryName.slice(0,2) + '\n' + item.productCategoryName.slice(2,4)) : '' }}
                    </text>
                  </view>
                  <text class="product-price">¥<text class="price-number">{{ item.price }}</text>起/份</text>
                  <view class="product-add" @tap.stop="addToGiftCart(item)"></view>
                </view>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 加载更多 -->
      <uni-load-more :status="loadingType" v-if="productList.length > 0"></uni-load-more>
    </view>

    <!-- 搜索结果视图 -->
    <view class="search-result-container" v-if="isSearching" :style="{ paddingBottom: '120rpx' }">
      <!-- 搜索结果数量 -->
      <view class="result-header-container" v-if="productList.length > 0">
        <view class="result-count">共 {{ productList.length }} 个搜索结果</view>
        <view class="divider"></view>
      </view>

      <!-- 无搜索结果提示 -->
      <view class="no-results" v-if="productList.length === 0 && !isLoading">
        <image class="no-results-image"
          src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/search/no-result.png"></image>
        <text class="no-results-text">抱歉，未找到相关礼品</text>
      </view>

      <!-- 搜索结果列表 -->
      <view class="product-list" v-if="productList.length > 0">
        <view class="product-big-card" v-for="(item, index) in productList" :key="index" @tap="navToDetailPage(item)">
          <view class="product-card-background"></view>
          <view class="product-card-content">
            <view class="product-image-container">
              <image class="product-big-image" :src="item.pic" mode="aspectFill"></image>
              <view class="product-tags" v-if="item.newStatus === 1">
                <view class="product-tag new">新品</view>
              </view>
            </view>
            <view class="product-info-container">
              <view class="product-info">
                <view> <text class="product-title">{{ item.name }}</text> <text class="product-desc"
                    v-if="item.subTitle">{{ item.subTitle }}</text> </view>
                <view class="product-price-area" @tap.stop="toggleSpec">
                  <image class="price-tag-bg"
                    src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/product_card/price_tag/price_area_bg1.png"
                    mode="aspectFit"></image>
                  <view class="product-category-name">
                    <text v-if="item.productCategoryName" class="category-text">
                      {{ item.productCategoryName ? (item.productCategoryName.slice(0,2) + '\n' + item.productCategoryName.slice(2,4)) : '' }}
                    </text>
                  </view>
                  <text class="product-price">¥<text class="price-number">{{ item.price }}</text>起/份</text>
                  <view class="product-add" @tap.stop="addToGiftCart(item)"></view>
                </view>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 加载更多 -->
      <uni-load-more :status="loadingType" v-if="productList.length > 0"></uni-load-more>
    </view>

    <!-- 底部操作栏 -->
    <view class="bottom-action-bar">
      <view class="cart-icon-container" @tap="toggleCartPopup">
        <image class="cart-icon" src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/product/cart-icon.png" mode="aspectFit"></image>
        <view class="cart-badge" v-if="giftCartItems.length > 0">{{ giftCartItems.length }}</view>
      </view>
      <view class="total-info">
        <text>合计: ¥ {{ totalPrice.toFixed(2) }}</text>
        <view class="expand-btn" @tap="toggleCartPopup">
          <text>明细</text>
          <image src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/category/arrow-down.png" mode="aspectFit" style="width: 24rpx; height: 24rpx; margin-left: 6rpx;"></image>
        </view>
      </view>
      <view class="next-btn" @tap="goToNextStep">
        <text>下一步</text>
      </view>
    </view>

    <!-- 购物车弹窗 -->
    <view class="cart-popup" v-if="showCartPopup" @tap.stop="closeCartPopup">
      <view class="cart-popup-mask"></view>
      <view class="cart-popup-content" @tap.stop>
        <view class="cart-popup-header">
          <text class="cart-popup-title">已选 {{ totalItems }} 件礼物</text>
          <view class="cart-popup-close" @tap="closeCartPopup">
            <text>×</text>
          </view>
        </view>
        <scroll-view class="cart-items-list" scroll-y="true">
          <view class="cart-item" v-for="(item, index) in giftCartItems" :key="index">
            <image class="cart-item-img" :src="item.pic" mode="aspectFill"></image>
            <view class="cart-item-info">
              <text class="cart-item-name">{{ item.name }}</text>
              <view class="cart-item-specs" v-if="item.selectedSpecs">
                <text class="specs-text">{{ item.selectedSpecs }}</text>
              </view>
              <view class="cart-item-bottom">
                <text class="cart-item-price">¥{{ item.price }}</text>
                <view class="quantity-control">
                  <view class="quantity-btn minus" @tap.stop="decreaseQuantity(index)">-</view>
                  <view class="quantity-number">{{ item.quantity }}</view>
                  <view class="quantity-btn plus" @tap.stop="increaseQuantity(index)">+</view>
                </view>
              </view>
            </view>
            <view class="cart-item-delete" @tap.stop="removeItem(index)">
              <text>×</text>
            </view>
          </view>
        </scroll-view>
        <view class="cart-popup-footer">
          <view class="cart-total">
            <text>合计：</text>
            <text class="cart-total-price">¥{{ totalPrice.toFixed(2) }}</text>
          </view>
          <view class="cart-action-btn" @tap="goToNextStep">下一步</view>
        </view>
      </view>
    </view>

    <!-- 规格选择弹窗 -->
    <view :class="['popup', 'spec', specClass]" @touchmove.stop="stopPrevent" @tap="toggleSpec">
      <view class="mask"></view>
      <view class="layer attr-content" @tap.stop="stopPrevent">
        <view class="a-t">
          <image :src="selectedSkuPic || currentProduct.pic"></image>
          <view class="right">
            <text class="name">{{ currentProduct.name }}</text>
            <view class="selected">
              已选：
              <text v-for="(sItem, sIndex) in specSelected" :key="sIndex" class="selected-text">{{ sItem.name }}</text>
            </view>
            <text class="price">{{ currentPrice || currentProduct.price }}</text>
          </view>
        </view>

        <view v-for="(item, index) in specList" :key="index" class="attr-list">
          <text>{{ item.name }}</text>
          <view class="item-list">
            <text v-for="(childItem, childIndex) in specChildList" :key="childIndex" v-if="childItem.pid === item.id"
              :class="['tit', childItem.selected ? 'selected' : '']"
              @tap="selectSpec(childIndex, childItem.pid)">{{ childItem.name }}</text>
          </view>
        </view>

        <view class="product-quantity">
          <view class="label">数量:</view>
          <view class="quantity-control">
            <button @tap="decreaseSpecQuantity">-</button>
            <input type="number" v-model="currentQuantity" @input="validateQuantity" />
            <button @tap="increaseSpecQuantity">+</button>
          </view>
        </view>

        <view class="action-btn-group">
          <button class="action-btn confirm-btn" type="primary" @tap="confirmAddToCart">确定</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import NavBar from '@/components/nav-bar.vue';
import uniLoadMore from '@/components/uni-load-more/uni-load-more.vue';
import { fetchBrandRecommendList } from '@/api/brand';
import { searchProductList } from '@/api/product';
import { fetchProductDetail } from '@/api/product';
import { mapState } from 'vuex';

export default {
  components: {
    'nav-bar': NavBar,
    uniLoadMore
  },
  data() {
    return {
      brandList: [],
      selectedBrandId: 0,
      productList: [],
      page: 1,
      limit: 10,
      keyword: '',
      loadingType: 'more',
      isLoading: false,
      giftCartItems: [], // 购物车商品
      showCartPopup: false, // 是否显示购物车弹窗
      searchHistory: [], // 搜索历史记录
      isSearching: false, // 是否处于搜索状态
      tempBrandId: null, // 临时存储品牌ID
      specClass: 'none', // 规格弹窗样式
      specList: [], // 规格列表
      specChildList: [], // 规格子项列表
      specSelected: [], // 已选规格
      currentProduct: {}, // 当前选中商品
      currentQuantity: 1, // 当前商品数量
      skuStockList: [], // SKU库存列表
      selectedSkuPic: '', // 添加选中SKU的图片
      currentPrice: 0, // 添加当前选中规格的价格
    };
  },
  computed: {
    ...mapState(['hasLogin', 'userInfo']),
    // 计算总商品数
    totalItems() {
      return this.giftCartItems.reduce((sum, item) => sum + item.quantity, 0);
    },
    // 计算总价格
    totalPrice() {
      return this.giftCartItems.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    }
  },
  onLoad() {
    // 检查登录状态
    this.checkLogin();
    // 获取推荐品牌列表
    this.fetchBrands();
    // 获取礼品列表
    this.loadProductData();
    // 检查是否有已保存的购物车
    this.loadGiftCart();
  },
  onReachBottom() {
    // 触底加载更多
    this.loadMoreProduct();
  },
  onPullDownRefresh() {
    // 重置页面数据并重新加载
    this.page = 1;
    this.productList = [];
    this.fetchBrands();
    this.loadProductData().then(() => {
      uni.stopPullDownRefresh();
    });
  },
  methods: {
    // 获取品牌列表
    async fetchBrands() {
      try {
        const res = await fetchBrandRecommendList({
          pageSize: 10,
          pageNum: 1
        });

        if (res && res.data) {
          this.brandList = res.data;
        }
      } catch (error) {
        console.error('获取品牌列表失败:', error);
        uni.showToast({
          title: '获取品牌列表失败',
          icon: 'none'
        });
      }
    },

    // 选择品牌
    selectBrand(brandId) {
      if (this.selectedBrandId === brandId) return;

      // 清除搜索状态
      if (this.isSearching) {
        this.keyword = '';
        this.isSearching = false;
      }

      this.selectedBrandId = brandId;
      this.page = 1;
      this.productList = [];
      this.loadProductData();
    },

    // 加载产品数据
    async loadProductData() {
      this.isLoading = true;
      this.loadingType = 'loading';

      try {
        // 构建请求参数
        const params = {
          pageNum: this.page,
          pageSize: this.limit,
        };

        // 如果有搜索关键词，添加到参数中
        if (this.keyword.trim()) {
          params.keyword = this.keyword.trim();
        }

        // 如果选择了品牌且不是搜索状态，添加品牌ID参数
        if (this.selectedBrandId > 0 && !this.isSearching) {
          params.brandId = this.selectedBrandId;
        }

        const res = await searchProductList(params);

        if (res && res.data) {
          const { list, pageNum, pages } = res.data;

          if (this.page === 1) {
            this.productList = list || [];
          } else {
            this.productList = [...this.productList, ...(list || [])];
          }

          // 判断是否有更多数据
          if (!list || list.length < this.limit || pageNum >= pages) {
            this.loadingType = 'noMore';
          } else {
            this.loadingType = 'more';
          }
        }
      } catch (error) {
        console.error('获取产品列表失败:', error);
        uni.showToast({
          title: '获取产品列表失败',
          icon: 'none'
        });
        this.loadingType = 'more';
      } finally {
        this.isLoading = false;
      }
    },

    // 加载更多产品
    loadMoreProduct() {
      if (this.loadingType !== 'more' || this.isLoading) return;

      this.page++;
      this.loadProductData();
    },

    // 导航到商品详情页
    navToDetailPage(item) {
      uni.navigateTo({
        url: `/pages/product/product?id=${item.id}&fromGiftList=true`
      });
    },

    // 添加商品到礼品购物车 - 修改为显示规格选择弹窗
    addToGiftCart(item) {
      // 保存当前选中的商品
      this.currentProduct = item;
      this.currentQuantity = 1;
      
      // 初始化图片和价格
      this.selectedSkuPic = item.pic;
      this.currentPrice = item.price;

      // 获取商品详情，包括规格信息
      this.fetchProductDetail(item.id);
    },

    // 获取商品详情
    async fetchProductDetail(productId) {
      uni.showLoading({
        title: '加载中...'
      });

      try {
        const res = await fetchProductDetail(productId);
        if (res && res.data) {
          // 保存SKU库存列表
          this.skuStockList = res.data.skuStockList || [];

          // 初始化规格列表
          this.initSpecList(res.data);

          // 显示规格选择弹窗
          this.toggleSpec();
        } else {
          uni.showToast({
            title: '获取商品信息失败',
            icon: 'none'
          });
        }
      } catch (error) {
        console.error('获取商品详情失败:', error);
        uni.showToast({
          title: '获取商品信息失败',
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
      }
    },

    // 初始化规格列表
    initSpecList(data) {
      this.specList = [];
      this.specChildList = [];
      this.specSelected = [];

      if (!data.productAttributeList || !data.productAttributeValueList) {
        console.warn('缺少商品属性数据');
        return;
      }

      // 遍历商品属性列表，找出规格类型的属性
      for (let i = 0; i < data.productAttributeList.length; i++) {
        let item = data.productAttributeList[i];
        if (item.type == 0) {
          this.specList.push({
            id: item.id,
            name: item.name
          });

          if (item.handAddStatus == 1) {
            // 支持手动新增的
            let valueList = data.productAttributeValueList;
            let filterValueList = valueList.filter(value => value.productAttributeId == item.id);
            if (filterValueList.length > 0) {
              let inputList = filterValueList[0].value.split(',');
              for (let j = 0; j < inputList.length; j++) {
                this.specChildList.push({
                  pid: item.id,
                  pname: item.name,
                  name: inputList[j]
                });
              }
            }
          } else if (item.handAddStatus == 0) {
            // 不支持手动新增的
            if (item.inputList) {
              let inputList = item.inputList.split(',');
              for (let j = 0; j < inputList.length; j++) {
                this.specChildList.push({
                  pid: item.id,
                  pname: item.name,
                  name: inputList[j]
                });
              }
            }
          }
        }
      }

      // 根据SKU库存筛选可用规格
      let availAbleSpecSet = new Set();
      if (this.skuStockList && this.skuStockList.length > 0) {
        for (let i = 0; i < this.skuStockList.length; i++) {
          try {
            let spDataArr = JSON.parse(this.skuStockList[i].spData);
            for (let j = 0; j < spDataArr.length; j++) {
              availAbleSpecSet.add(spDataArr[j].value);
            }
          } catch (e) {
            console.error('解析SKU数据出错:', e);
          }
        }
      }

      // 根据商品sku筛选出可用规格
      this.specChildList = this.specChildList.filter(item => {
        return availAbleSpecSet.has(item.name)
      });

      // 规格默认选中第一条
      if (this.specList.length > 0 && this.specChildList.length > 0) {
        this.specList.forEach(item => {
          for (let cItem of this.specChildList) {
            if (cItem.pid === item.id) {
              this.$set(cItem, 'selected', true);
              this.specSelected.push(cItem);
              break;
            }
          }
        });
        
        // 初始化时也需要更新规格信息
        this.changeSpecInfo();
      } else {
        // 如果没有规格，使用默认图片和价格
        this.selectedSkuPic = this.currentProduct.pic;
        this.currentPrice = this.currentProduct.price;
      }
    },

    // 规格弹窗开关
    toggleSpec() {
      if (this.specClass === 'show') {
        this.specClass = 'hide';
        setTimeout(() => {
          this.specClass = 'none';
        }, 250);
      } else if (this.specClass === 'none') {
        // 确保打开弹窗时根据已选规格更新图片和价格
        let skuStock = this.getSkuStock();
        if (skuStock && skuStock.pic) {
          this.selectedSkuPic = skuStock.pic;
        } else {
          // 如果没有选择完整规格，显示默认图片
          this.selectedSkuPic = this.currentProduct.pic;
        }
        
        // 初始化价格
        if (skuStock) {
          if (this.currentProduct.promotionType == 1) {
            this.currentPrice = skuStock.promotionPrice || skuStock.price;
          } else {
            this.currentPrice = skuStock.price;
          }
        } else {
          this.currentPrice = this.currentProduct.price;
        }
        
        this.specClass = 'show';
      }
    },

    // 选择规格
    selectSpec(index, pid) {
      let list = this.specChildList;
      list.forEach(item => {
        if (item.pid === pid) {
          this.$set(item, 'selected', false);
        }
      });

      this.$set(list[index], 'selected', true);

      // 存储已选择
      this.specSelected = [];
      list.forEach(item => {
        if (item.selected === true) {
          this.specSelected.push(item);
        }
      });
      
      // 当商品规格改变时，修改商品信息
      this.changeSpecInfo();
    },

    // 获取当前选中商品的SKU
    getSkuStock() {
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
    },

    // 确认添加到购物车
    confirmAddToCart() {
      let productSkuStock = this.getSkuStock();
      if (!productSkuStock) {
        uni.showToast({
          title: "请选择完整规格",
          icon: 'none'
        });
        return;
      }

      // 构建商品项
      let cartItem = {
        id: this.currentProduct.id,
        name: this.currentProduct.name,
        price: this.currentPrice || this.currentProduct.price,
        pic: this.selectedSkuPic || this.currentProduct.pic,
        quantity: this.currentQuantity,
        productSkuId: productSkuStock.id,
        productAttr: productSkuStock.spData,
        productSkuCode: productSkuStock.skuCode,
        selectedSpecs: this.specSelected.map(item => item.name).join(', '),
        productSn: this.currentProduct.productSn || '',
        productSubTitle: this.currentProduct.subTitle || '',
        productCategoryId: this.currentProduct.productCategoryId || 0,
        productBrand: this.currentProduct.brandName || ''
      };

      // 查找商品是否已在购物车
      const existingItemIndex = this.giftCartItems.findIndex(item =>
        item.id === cartItem.id && item.productSkuId === cartItem.productSkuId
      );

      if (existingItemIndex !== -1) {
        // 如果已存在，增加数量
        this.giftCartItems[existingItemIndex].quantity += this.currentQuantity;
      } else {
        // 否则添加新商品
        this.giftCartItems.push(cartItem);
      }

      // 保存购物车
      this.saveGiftCart();

      // 关闭规格弹窗
      this.toggleSpec();

      // 显示添加成功提示
      uni.showToast({
        title: '已添加到礼品袋',
        icon: 'success'
      });
    },

    // 保存购物车到本地存储
    saveGiftCart() {
      uni.setStorageSync('giftCartItems', JSON.stringify(this.giftCartItems));
    },

    // 从本地存储加载购物车
    loadGiftCart() {
      const cartItems = uni.getStorageSync('giftCartItems');
      if (cartItems) {
        this.giftCartItems = JSON.parse(cartItems);
      }
    },

    // 减少商品数量
    decreaseQuantity(index) {
      if (this.giftCartItems[index].quantity > 1) {
        this.giftCartItems[index].quantity--;
        this.saveGiftCart();
      } else {
        this.removeItem(index);
      }
    },

    // 增加商品数量
    increaseQuantity(index) {
      this.giftCartItems[index].quantity++;
      this.saveGiftCart();
    },

    // 移除商品
    removeItem(index) {
      this.giftCartItems.splice(index, 1);
      this.saveGiftCart();
    },

    // 下一步 - 修改为传递购物车数据
    goToNextStep() {
      if (this.giftCartItems.length === 0) {
        uni.showToast({
          title: '请先添加礼品',
          icon: 'none'
        });
        return;
      }

      // 检查用户是否已登录
      if (!this.hasLogin) {
        uni.showModal({
          title: '提示',
          content: '请先登录后再操作',
          confirmText: '去登录',
          cancelText: '取消',
          success: (res) => {
            if (res.confirm) {
              // 保存当前页面信息到本地存储，登录成功后可以返回
              uni.setStorageSync('redirectPage', '/pages/gift-bag/giftsList');
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

      // 保存购物车数据
      this.saveGiftCart();

      // 跳转到填写祝福页面
      uni.navigateTo({
        url: '/pages/gift-bag/gift-wishes'
      });
    },

    // 切换购物车弹窗显示状态
    toggleCartPopup() {
      this.showCartPopup = !this.showCartPopup;
    },

    // 关闭购物车弹窗
    closeCartPopup() {
      this.showCartPopup = false;
    },

    // 验证数量输入
    validateQuantity(e) {
      let val = parseInt(e.detail.value);
      if (isNaN(val) || val < 1) {
        this.currentQuantity = 1;
      } else {
        this.currentQuantity = val;
      }
    },

    // 规格弹窗中减少数量
    decreaseSpecQuantity() {
      if (this.currentQuantity > 1) {
        this.currentQuantity--;
      }
    },

    // 规格弹窗中增加数量
    increaseSpecQuantity() {
      this.currentQuantity++;
    },

    // 阻止事件传播
    stopPrevent() { },

    // 执行搜索
    performSearch() {
      if (!this.keyword.trim()) return;

      // 保存搜索历史
      this.saveSearchHistory();

      // 设置搜索状态
      this.isSearching = true;

      // 清空品牌选择
      this.selectedBrandId = 0;

      // 重置页面数据并执行搜索
      this.page = 1;
      this.productList = [];
      this.loadProductData();
    },

    // 清除搜索
    clearSearch() {
      this.keyword = '';
      this.isSearching = false;

      // 如果有暂存的品牌ID，恢复选中状态
      if (this.tempBrandId) {
        this.selectedBrandId = this.tempBrandId;
        this.tempBrandId = null;
      }

      this.page = 1;
      this.productList = [];
      this.loadProductData();
    },

    // 保存搜索历史
    saveSearchHistory() {
      // 如果关键词为空，不保存
      if (!this.keyword.trim()) return;

      // 获取现有历史记录
      let searchHistory = uni.getStorageSync('searchHistory');
      if (searchHistory) {
        searchHistory = JSON.parse(searchHistory);
      } else {
        searchHistory = [];
      }

      // 先删除已存在的相同关键词
      const index = searchHistory.indexOf(this.keyword);
      if (index !== -1) {
        searchHistory.splice(index, 1);
      }

      // 将新关键词添加到列表头部
      searchHistory.unshift(this.keyword);

      // 最多保存10条历史记录
      if (searchHistory.length > 10) {
        searchHistory = searchHistory.slice(0, 10);
      }

      // 保存到本地存储
      uni.setStorageSync('searchHistory', JSON.stringify(searchHistory));
    },

    // 搜索框聚焦
    onSearchFocus() {
      // 如果不是搜索状态，则设置为搜索状态
      if (!this.isSearching && this.selectedBrandId !== 0) {
        // 暂存当前选中的品牌ID
        this.tempBrandId = this.selectedBrandId;
        // 清除选中状态以便UI更新
        this.selectedBrandId = 0;
      }
    },

    // 检查登录状态
    checkLogin() {
      // 使用统一的tokenInfo检查登录状态
      const tokenInfo = uni.getStorageSync('tokenInfo');
      const userInfoStorage = uni.getStorageSync('userInfo');

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

      if (hasValidToken && !!userInfoStorage && !this.hasLogin) {
        // 如果本地有有效的 tokenInfo 和 userInfo，但 store 中 hasLogin 为 false，则提交登录到 store
        console.log('检测到本地登录信息，恢复登录状态');
        this.$store.commit('login', userInfoStorage);
      } else if ((!hasValidToken || !userInfoStorage) && this.hasLogin) {
        // 如果本地没有有效的 tokenInfo 或 userInfo，但 store 中 hasLogin 为 true，则提交登出到 store
        console.log('本地登录信息无效，清除登录状态');
        this.$store.commit('logout');
      }
    },

    // 当商品规格改变时，修改商品信息
    changeSpecInfo() {
      try {
        let skuStock = this.getSkuStock();
        if (skuStock != null) {
          // 更新SKU图片
          if (skuStock.pic) {
            this.selectedSkuPic = skuStock.pic;
          } else {
            this.selectedSkuPic = this.currentProduct.pic;
          }
          
          // 更新价格
          if (this.currentProduct.promotionType == 1) {
            // 单品优惠使用促销价
            this.currentPrice = skuStock.promotionPrice || skuStock.price;
          } else {
            this.currentPrice = skuStock.price;
          }
        } else {
          // 如果没有匹配的SKU，恢复默认值
          this.selectedSkuPic = this.currentProduct.pic;
          this.currentPrice = this.currentProduct.price;
        }
      } catch (error) {
        console.error('更新规格信息出错:', error);
        // 发生错误时恢复默认值
        this.selectedSkuPic = this.currentProduct.pic;
        this.currentPrice = this.currentProduct.price;
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.gifts-list-container {
  width: 100%;
  min-height: 100vh;
  background-color: #f5f5f5;
}

/* 导航栏样式 */
.nav-title-container {
  display: flex;
  justify-content: center;
  align-items: center;

  .nav-title {
    font-size: 18px;
    color: #000000;
    font-weight: 500;
  }
}

/* 搜索栏样式 */
.search-header {
  padding: 20rpx 30rpx;
  position: absolute;
  top: 180rpx;
  left: 0;
  width: 100%;
  z-index: 10;
  box-sizing: border-box;

  .search-container {
    display: flex;
    align-items: center;
    width: 100%;
    height: 72rpx;
    border-radius: 36rpx;
    padding: 0 20rpx;
    background-color: rgba(255, 255, 255, 0.85);
    backdrop-filter: blur(10px);

    .search-icon-btn {
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 0 10rpx;
      margin-right: 10rpx;

      .search-icon {
        width: 32rpx;
        height: 32rpx;
      }
    }

    .search-input {
      flex: 1;
      height: 100%;
      font-size: 28rpx;
      color: #333333;
      background: transparent;
    }

    .clear-btn {
      width: 48rpx;
      height: 48rpx;
      display: flex;
      align-items: center;
      justify-content: center;

      .clear-text {
        font-size: 28rpx;
        color: #999999;
        line-height: 1;
      }
    }
  }
}

/* Banner样式 */
.banner-container {
  width: 100%;
  height: 450rpx;
  background-color: #FFFFFF;
  padding: 0;
  box-sizing: border-box;
  position: relative;
  margin-bottom: 20rpx;

  .banner-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

/* 品牌列表样式 */
.brand-list-container {
  padding: 20rpx 0;
  margin-bottom: 20rpx;
  
  .brand-list {
    white-space: nowrap;
    padding: 0 20rpx;
    
    .brand-item {
      display: inline-block;
      padding: 10rpx 30rpx 30rpx;
      margin-right: 20rpx;
      border-radius: 30rpx;
      position: relative;
      
      &.active {
        .brand-name {
          font-size: 28rpx;
          color: #000000;
          font-weight: 500;
        }
      }
      
      .tab-indicator {
        position: absolute;
        width: 64rpx;
        height: 20rpx;
        bottom: 20rpx;
        left: 50%;
        transform: translateX(-50%);
      }
      
      .brand-name {
        font-size: 24rpx;
        color: #333333;
        position: relative;
        z-index: 2;
      }
    }
  }
}

/* 产品列表样式 */
.gifts-list {
  padding: 20rpx;

  .no-results {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 100rpx 0;

    .no-results-image {
      width: 200rpx;
      height: 200rpx;
      margin-bottom: 30rpx;
    }

    .no-results-text {
      font-size: 28rpx;
      color: #999999;
    }
  }

  .product-list {
    display: flex;
    flex-direction: column;

    .product-big-card {
      position: relative;
      width: 100%;
      height: 300rpx;
      margin-bottom: 30rpx;
      border-radius: 20rpx;
      overflow: hidden;
      box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);

      .product-card-background {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: linear-gradient(to right, #DAF2FA, #F7FCFE);
        z-index: 1;
      }

      .product-card-content {
        position: relative;
        z-index: 2;
        display: flex;
        width: 100%;
        height: 100%;
      }

      .product-image-container {
        position: relative;
        width: 40%;
        height: 100%;
        margin-left: 20rpx;

        .product-big-image {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }

        .product-tags {
          position: absolute;
          top: 0;
          left: -20rpx;
          display: flex;
          flex-direction: column;
          z-index: 10;

          .product-tag {
            position: relative;
            padding: 4rpx 12rpx;
            color: #000000;
            font-size: 20rpx;
            font-weight: 500;
            margin-bottom: 4rpx;
            height: 64rpx;
            width: 64rpx;
            display: flex;
            align-items: center;
            justify-content: center;

            &::before {
              content: "";
              position: absolute;
              top: 0;
              left: 0;
              width: 100%;
              height: 100%;
              background-image: url("https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/product_card/tag_bg.png");
              background-size: 100% 100%;
              z-index: -1;
            }

            &.new::before {
              background-image: url("https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/product_card/tag_bg.png");
            }
          }
        }
      }

      .product-info-container {
        flex: 1;
        padding: 20rpx;
        display: flex;
        flex-direction: column;
        justify-content: space-between;

        .product-info {
          height: 100%;
          display: flex;
          flex-direction: column;
          justify-content: space-between;

          .product-title {
            font-size: 30rpx;
            color: #000000;
            font-weight: 500;
            display: block;
            margin-bottom: 10rpx;
            display: -webkit-box;
            -webkit-line-clamp: 1;
            -webkit-box-orient: vertical;
            overflow: hidden;
            text-overflow: ellipsis;
          }

          .product-desc {
            font-size: 24rpx;
            color: #666666;
            display: block;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            text-overflow: ellipsis;
          }

          .product-price-area {
            display: flex;
            align-items: center;
            position: relative;
            margin-top: 10rpx;

            .price-tag-bg {
              position: absolute;
              width: 400rpx;
              height: 100rpx;
              left: -20rpx;
              bottom: -10rpx;
              z-index: -1;
            }

            .product-category-name {
              position: absolute;
              left: 0rpx;
              bottom: 16rpx;
              z-index: 2;
              display: flex;
              flex-direction: column;
              align-items: flex-start;

              .category-text {
                color: #fff;
                font-size: 32rpx;
                font-weight: bold;
                line-height: 1.2;
                white-space: pre-line;
                text-shadow: 0 4rpx 16rpx rgba(0,0,0,0.18);
              }
            }

            .product-price {
              position: absolute;
              bottom: 6rpx;
              left: 140rpx;
              font-size: 24rpx;
              font-weight: 400;
              color: #000000;
              z-index: 2;

              .price-number {
                font-size: 44rpx;
                font-weight: 500;
              }
            }
            
            .product-add {
              position: absolute;
              width: 60rpx;
              height: 60rpx;
              right: 0rpx;
              bottom: 0rpx;
              z-index: 3;
              background-color: transparent;
            }
          }
        }
      }
    }
  }

  .no-more-data {
    text-align: center;
    padding: 20rpx 0;
    color: #999;
    font-size: 24rpx;
  }
}

/* 底部操作栏样式 */
.bottom-action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 150rpx;
  background-color: #FFFFFF;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30rpx 30rpx;
  box-sizing: border-box;
  box-shadow: 0 -1rpx 0 0 rgba(0, 0, 0, 0.05);
  z-index: 100;

  .cart-icon-container {
    position: relative;
    width: 60rpx;
    height: 60rpx;
    
    .cart-icon {
      width: 100%;
      height: 100%;
    }
    
    .cart-badge {
      position: absolute;
      top: -10rpx;
      right: -10rpx;
      background-color: #D3FB51;
      color: #000000;
      font-size: 20rpx;
      min-width: 32rpx;
      height: 32rpx;
      border-radius: 16rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 0 6rpx;
    }
  }
  
  .total-info {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 30rpx;
    font-size: 32rpx;
    font-weight: 500;
    color: #000000;
    
    .expand-btn {
      display: flex;
      align-items: center;
      font-size: 28rpx;
      color: #666666;
    }
  }
  
  .next-btn {
    height: 72rpx;
    min-width: 180rpx;
    background: linear-gradient(to right, #DAF2FA, #F7FCFE);
    border-radius: 36rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 28rpx;
    font-weight: 500;
    color: #000000;
  }
}

/* 购物车弹窗样式 */
.cart-popup {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  z-index: 1000;

  .cart-popup-mask {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
  }

  .cart-popup-content {
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    background-color: #FFFFFF;
    border-top-left-radius: 20rpx;
    border-top-right-radius: 20rpx;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    max-height: 80vh;

    .cart-popup-header {
      padding: 30rpx;
      display: flex;
      justify-content: space-between;
      align-items: center;
      border-bottom: 1rpx solid #f5f5f5;
      flex-shrink: 0;

      .cart-popup-title {
        font-size: 32rpx;
        font-weight: 500;
        color: #000000;
      }

      .cart-popup-close {
        width: 48rpx;
        height: 48rpx;
        display: flex;
        align-items: center;
        justify-content: center;

        text {
          font-size: 48rpx;
          color: #999999;
          line-height: 1;
        }
      }
    }

    .cart-items-list {
      flex: 1;
      max-height: 60vh;
      padding: 0 30rpx;
      box-sizing: border-box;
    }

    .cart-item {
      display: flex;
      align-items: flex-start;
      padding: 30rpx 0;
      border-bottom: 1rpx solid #f5f5f5;
      position: relative;

      .cart-item-img {
        width: 120rpx;
        height: 120rpx;
        object-fit: cover;
        border-radius: 10rpx;
        margin-right: 20rpx;
      }

      .cart-item-info {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        min-height: 120rpx;

        .cart-item-name {
          font-size: 28rpx;
          color: #000000;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
          text-overflow: ellipsis;
          margin-bottom: 10rpx;
        }

        .cart-item-specs {
          margin-bottom: 10rpx;

          .specs-text {
            font-size: 24rpx;
            color: #999999;
            line-height: 1.3;
          }
        }

        .cart-item-bottom {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-top: 10rpx;

          .cart-item-price {
            font-size: 32rpx;
            font-weight: 500;
            color: #000000;
          }

          .quantity-control {
            display: flex;
            align-items: center;

            .quantity-btn {
              width: 48rpx;
              height: 48rpx;
              border-radius: 24rpx;
              display: flex;
              align-items: center;
              justify-content: center;
              font-size: 32rpx;

              &.minus {
                background-color: #f5f5f5;
                color: #333333;
              }

              &.plus {
                background-color: #D3FB51;
                color: #000000;
              }
            }

            .quantity-number {
              width: 60rpx;
              text-align: center;
              font-size: 28rpx;
              color: #000000;
            }
          }
        }
      }

      .cart-item-delete {
        position: absolute;
        top: 30rpx;
        right: 0;
        width: 40rpx;
        height: 40rpx;
        display: flex;
        align-items: center;
        justify-content: center;

        text {
          font-size: 32rpx;
          color: #999999;
          line-height: 1;
        }
      }
    }

    .cart-popup-footer {
      padding: 30rpx;
      display: flex;
      justify-content: space-between;
      align-items: center;
      border-top: 1rpx solid #f5f5f5;
      flex-shrink: 0;

      .cart-total {
        display: flex;
        align-items: baseline;

        text {
          font-size: 28rpx;
          color: #333333;
        }

        .cart-total-price {
          font-size: 36rpx;
          font-weight: 500;
          color: #000000;
        }
      }

      .cart-action-btn {
        height: 72rpx;
        padding: 0 40rpx;
        background: linear-gradient(to right, #DAF2FA, #F7FCFE);
        color: #000000;
        font-size: 28rpx;
        font-weight: 500;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 36rpx;
      }
    }
  }
}

/* 搜索结果样式 */
.search-result-container {
  padding: 20rpx;
  padding-top: 140rpx; /* 为搜索栏留出空间 */
  background-color: #f5f5f5;
  min-height: calc(100vh - 140rpx);

  .result-header-container {
    margin-bottom: 20rpx;

    .result-count {
      font-size: 28rpx;
      color: #666666;
      padding: 10rpx 0;
    }

    .divider {
      height: 2rpx;
      background-color: #eaeaea;
      margin: 10rpx 0;
    }
  }

  .no-results {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 100rpx 0;

    .no-results-image {
      width: 200rpx;
      height: 200rpx;
      margin-bottom: 30rpx;
    }

    .no-results-text {
      font-size: 28rpx;
      color: #999999;
    }
  }

  .product-list {
    display: flex;
    flex-direction: column;

    .product-big-card {
      position: relative;
      width: 100%;
      height: 300rpx;
      margin-bottom: 30rpx;
      border-radius: 20rpx;
      overflow: hidden;
      box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);

      .product-card-background {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: linear-gradient(to right, #DAF2FA, #F7FCFE);
        z-index: 1;
      }

      .product-card-content {
        position: relative;
        z-index: 2;
        display: flex;
        width: 100%;
        height: 100%;
      }

      .product-image-container {
        position: relative;
        width: 40%;
        height: 100%;
        margin-left: 20rpx;

        .product-big-image {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }

        .product-tags {
          position: absolute;
          top: 0;
          left: -20rpx;
          display: flex;
          flex-direction: column;
          z-index: 10;

          .product-tag {
            position: relative;
            padding: 4rpx 12rpx;
            color: #000000;
            font-size: 20rpx;
            font-weight: 500;
            margin-bottom: 4rpx;
            height: 64rpx;
            width: 64rpx;
            display: flex;
            align-items: center;
            justify-content: center;

            &::before {
              content: "";
              position: absolute;
              top: 0;
              left: 0;
              width: 100%;
              height: 100%;
              background-image: url("https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/product_card/tag_bg.png");
              background-size: 100% 100%;
              z-index: -1;
            }

            &.new::before {
              background-image: url("https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/product_card/tag_bg.png");
            }
          }
        }
      }

      .product-info-container {
        flex: 1;
        padding: 20rpx;
        display: flex;
        flex-direction: column;
        justify-content: space-between;

        .product-info {
          height: 100%;
          display: flex;
          flex-direction: column;
          justify-content: space-between;

          .product-title {
            font-size: 30rpx;
            color: #000000;
            font-weight: 500;
            display: block;
            margin-bottom: 10rpx;
            display: -webkit-box;
            -webkit-line-clamp: 1;
            -webkit-box-orient: vertical;
            overflow: hidden;
            text-overflow: ellipsis;
          }

          .product-desc {
            font-size: 24rpx;
            color: #666666;
            display: block;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            text-overflow: ellipsis;
          }

          .product-price-area {
            display: flex;
            align-items: center;
            position: relative;
            margin-top: 10rpx;

            .price-tag-bg {
              position: absolute;
              width: 400rpx;
              height: 100rpx;
              left: -20rpx;
              bottom: -10rpx;
              z-index: -1;
            }

            .product-category-name {
              position: absolute;
              left: 0rpx;
              bottom: 16rpx;
              z-index: 2;
              display: flex;
              flex-direction: column;
              align-items: flex-start;

              .category-text {
                color: #fff;
                font-size: 32rpx;
                font-weight: bold;
                line-height: 1.2;
                white-space: pre-line;
                text-shadow: 0 4rpx 16rpx rgba(0,0,0,0.18);
              }
            }

            .product-price {
              position: absolute;
              bottom: 6rpx;
              left: 140rpx;
              font-size: 24rpx;
              font-weight: 400;
              color: #000000;
              z-index: 2;

              .price-number {
                font-size: 44rpx;
                font-weight: 500;
              }
            }
            
            .product-add {
              position: absolute;
              width: 60rpx;
              height: 60rpx;
              right: 0rpx;
              bottom: 0rpx;
              z-index: 3;
              background-color: transparent;
            }
          }
        }
      }
    }
  }
}

/* 规格弹窗样式 */
.popup {
  position: fixed;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;

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
    z-index: 1000;
    background-color: rgba(0, 0, 0, 0.4);
  }

  .layer {
    position: fixed;
    z-index: 1001;
    bottom: 0;
    width: 100%;
    min-height: 40vh;
    border-radius: 16rpx 16rpx 0 0;
    background-color: #fff;
  }
}

.attr-content {
  padding: 50rpx 30rpx;

  .a-t {
    display: flex;

    image {
      width: 144rpx;
      height: 144rpx;
      flex-shrink: 0;
      border-radius: 8rpx;
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

    text {
      display: flex;
      align-items: center;
      justify-content: center;
      background: #fff;
      margin-right: 20rpx;
      margin-bottom: 20rpx;
      border-radius: 4rpx;
      border: #EEEEEE solid 1rpx;
      min-width: 60rpx;
      height: 70rpx;
      padding: 10rpx 20rpx;
      font-size: 24rpx;
      color: #000;
    }

    .selected {
      background: linear-gradient(270deg, #C8EBF8 0%, #E2F1F5 100%);
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
      flex: 0 0 auto;
    }

    button {
      width: 60rpx;
      height: 60rpx;
      text-align: center;
      color: #000;
      font-size: 38rpx;
      background-color: #ffffff;
      border: 2rpx solid #EAEAEA;
      border-radius: 8rpx;
      line-height: 54rpx;
      padding: 0rpx;
    }

    input {
      width: 80rpx;
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
  }

  .confirm-btn {
    width: 100%;
    height: 100%;
    border-radius: 40rpx;
    font-size: 32rpx;
    padding: 0;
    background: linear-gradient(to right, #DAF2FA, #F7FCFE);
    color: #000000;
    display: flex;
    align-items: center;
    justify-content: center;
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
</style>