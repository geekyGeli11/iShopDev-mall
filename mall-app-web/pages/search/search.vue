<template>
  <view class="container">
    <!-- 自定义导航栏 -->
    <nav-bar 
      :back="true" 
      :hasSlot="true" 
      :bgColor="'transparent'"
      @ready="onNavBarReady"
    >
      <view class="nav-search-container">
        <view class="search-wrapper">
          <view class="search-btn">
            <image class="search-icon" src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/search.png"></image>
          </view>
          <input 
            class="search-input" 
            :focus="true" 
            type="text" 
            v-model="searchQuery" 
            placeholder="搜索商品" 
            confirm-type="search" 
            @confirm="performSearch"
          />
          <view class="clear-btn" v-if="searchQuery" @tap="clearSearch">
            <text class="clear-text">✕</text>
          </view>
        </view>
      </view>
    </nav-bar>
    
    <!-- 主体内容 -->
    <view class="main-body">
      <!-- 未搜索状态显示热门搜索和历史 -->
      <block v-if="!searchStatus">
        <view class="hot-area">      
          <!-- 搜索历史 -->
          <view class="search-history" v-if="searchHistory && searchHistory.length > 0">
            <view class="history-header">
              <view class="title">历史搜索</view>
              <view class="clear-history" @tap="clearSearchHistory">
                <text>清空</text>
              </view>
            </view>
            <view class="keyword-list">
              <view 
                class="keyword" 
                v-for="(item, index) in searchHistory" 
                :key="index"
                @tap="toggleSearch(item)"
              >
                <view class="keyword-text">{{ item }}</view>
              </view>
            </view>
          </view>
        </view>
      </block>
      
      <!-- 搜索结果状态 -->
      <block v-else>
        <view class="content">
          <!-- 无搜索结果 -->
          <view class="no-results" v-if="productList.length === 0 && !isLoading">
            <image class="no-results-image" src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/search/no-result.png"></image>
            <text class="no-results-text">抱歉，暂无相关商品</text>
          </view>
          
          <!-- 有搜索结果 -->
          <block v-else>
            <!-- 结果头部和筛选 -->
            <view class="result-header-container">
              <view class="divider"></view>
              <view class="filter-tabs">
                <view class="tab-items">
                  <!-- 综合 -->
                  <view 
                    class="filter-tab"
                    :class="{ active: currentFilterTab === 0 }"
                    @tap="switchFilterTab(0)"
                  >
                    <text>综合</text>
                    <view class="indicator" v-if="currentFilterTab === 0"></view>
                  </view>
                  
                  <!-- 新品 -->
                  <view 
                    class="filter-tab"
                    :class="{ active: currentFilterTab === 1 }"
                    @tap="switchFilterTab(1)"
                  >
                    <text>新品</text>
                    <view class="indicator" v-if="currentFilterTab === 1"></view>
                  </view>
                  
                  <!-- 销量 -->
                  <view 
                    class="filter-tab"
                    :class="{ active: currentFilterTab === 2 }"
                    @tap="switchFilterTab(2)"
                  >
                    <text>销量</text>
                    <view class="indicator" v-if="currentFilterTab === 2"></view>
                  </view>
                  
                  <!-- 价格 -->
                  <view 
                    class="filter-tab"
                    :class="{ active: currentFilterTab === 3 }"
                    @tap="switchFilterTab(3)"
                  >
                    <text>价格</text>
                    <view class="price-arrows">
                      <image 
                        class="arrow-icon" 
                        :class="{ active: priceOrder === 1 && currentFilterTab === 3 }"
                        src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/category/arrow-up.png"
                      ></image>
                      <image 
                        class="arrow-icon" 
                        :class="{ active: priceOrder === 2 && currentFilterTab === 3 }"
                        src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/category/arrow-down.png"
                      ></image>
                    </view>
                    <view class="indicator" v-if="currentFilterTab === 3"></view>
                  </view>
                </view>
              </view>
            </view>
          </block>
          
          <!-- 产品列表 -->
          <view class="product-grid" v-if="productList.length > 0">
            <view 
              class="product-item"
              v-for="(item, index) in productList"
              :key="index"
              @tap="navToDetailPage(item)"
            >
              <!-- 商品图片 -->
              <view class="item-image-container">
                <image 
                  class="item-image" 
                  :src="item.pic" 
                  mode="aspectFill"
                  :style="item.stock <= 0 ? 'filter: grayscale(100%);' : ''"
                  @load="handleImageLoad(index)"
                  @error="handleImageError(index)"
                ></image>
                <!-- 售罄蒙版 -->
                <view class="sold-out-mask" v-if="item.stock <= 0">
                  <text class="sold-out-text">已售罄</text>
                </view>
              </view>
              
              <!-- 商品信息 -->
              <view class="item-info">
                <text class="item-title">{{ item.name }}</text>
                <text class="item-price">¥{{ item.price }}</text>
              </view>
            </view>
          </view>
          
          <!-- 加载更多 -->
          <view class="load-more" v-if="productList.length > 0">
            <uni-load-more :status="loadingType" @loadmore="loadMoreProduct"></uni-load-more>
          </view>
        </view>
      </block>
      
      <!-- 推荐商品 -->
      <view class="recommended-container relative-container" v-if="shouldShowRecommended">
        <view class="recommended-title">推荐商品</view>
        <ProductWaterfall :list="recommendedProductList" @select="navToDetailPage"></ProductWaterfall>
      </view>
    </view>
  </view>
</template>

<script>
import uniLoadMore from '@/components/uni-load-more/uni-load-more.vue';
import ProductWaterfall from '@/components/home/ProductWaterfall.vue';
import navBar from '@/components/nav-bar.vue';
import { searchProductList } from '@/api/product';
import { addCartItem } from '@/api/cart';

export default {
  components: {
    uniLoadMore,
    ProductWaterfall,
    navBar
  },
  data() {
    return {
      searchQuery: '',
      searchStatus: false,
      searchHistory: [],
      keywordList: [
        '广横走文创',
        '达濠鱼丸',
        '东京丸'       
      ],
      productList: [],
      page: 1,
      limit: 10,
      loadingType: 'more',
      isLoading: false,
      currentFilterTab: 0, // 0-综合，1-新品，2-销量，3-价格
      priceOrder: 1, // 1-升序，2-降序
      showRecommended: true,
      navBarHeight: 0,
      recommendedProductList: []
    };
  },
  computed: {
    // 根据搜索状态和结果决定是否显示推荐商品
    shouldShowRecommended() {
      // 在未搜索状态或搜索结果为空时显示推荐商品
      return !this.searchStatus || (this.searchStatus && this.productList.length === 0);
    }
  },
  onLoad(options) {
    // 获取搜索历史记录
    this.loadSearchHistory();

    // 加载推荐商品
    this.loadRecommendedProducts();

    // 如果有关键词参数，直接搜索
    if (options.keyword) {
      this.searchQuery = options.keyword;
      this.performSearch();
    }
  },
  onReachBottom() {
    // 只有在搜索状态下才触发加载更多
    if (this.searchStatus) {
      this.loadMoreProduct();
    }
  },
  methods: {
    // 导航栏准备就绪
    onNavBarReady(height) {
      this.navBarHeight = height;
    },
    
    // 执行搜索
    performSearch() {
      if (!this.searchQuery) return;
      
      // 保存搜索历史
      this.saveSearchHistory();
      
      // 显示搜索结果
      this.searchStatus = true;
      
      // 重置分页
      this.page = 1;
      
      // 加载商品数据
      this.loadProductData();
    },
    
    // 清空搜索
    clearSearch() {
      this.searchQuery = '';
      this.searchStatus = false;
    },
    
    // 点击搜索历史或热门搜索
    toggleSearch(keyword) {
      this.searchQuery = keyword;
      this.performSearch();
    },
    
    // 加载搜索历史
    loadSearchHistory() {
      const history = uni.getStorageSync('searchHistory');
      if (history) {
        this.searchHistory = JSON.parse(history);
      }
    },
    
    // 保存搜索历史
    saveSearchHistory() {
      // 如果关键词为空，不保存
      if (!this.searchQuery) return;
      
      // 如果没有历史记录，创建一个空数组
      if (!this.searchHistory) {
        this.searchHistory = [];
      }
      
      // 先删除已存在的相同关键词
      const index = this.searchHistory.indexOf(this.searchQuery);
      if (index !== -1) {
        this.searchHistory.splice(index, 1);
      }
      
      // 将新关键词添加到列表头部
      this.searchHistory.unshift(this.searchQuery);
      
      // 最多保存10条历史记录
      if (this.searchHistory.length > 10) {
        this.searchHistory = this.searchHistory.slice(0, 10);
      }
      
      // 保存到本地存储
      uni.setStorageSync('searchHistory', JSON.stringify(this.searchHistory));
    },
    
    // 清空搜索历史
    clearSearchHistory() {
      uni.showModal({
        title: '提示',
        content: '确定清空搜索历史？',
        success: (res) => {
          if (res.confirm) {
            this.searchHistory = [];
            uni.removeStorageSync('searchHistory');
          }
        }
      });
    },
    
    // 切换过滤选项卡
    switchFilterTab(tabIndex) {
      if (this.currentFilterTab === tabIndex && tabIndex === 3) {
        // 如果点击的是价格，且当前已经是价格选项卡，则切换升序/降序
        this.priceOrder = this.priceOrder === 1 ? 2 : 1;
      } else {
        this.currentFilterTab = tabIndex;
        if (tabIndex === 3) {
          // 默认价格升序
          this.priceOrder = 1;
        }
      }
      
      // 重置分页
      this.page = 1;
      
      // 重新加载数据
      this.loadProductData();
    },
    
    // 加载商品数据
    loadProductData() {
      this.isLoading = true;
      this.loadingType = 'loading';
      
      // 构建请求参数
      let params = {
        keyword: this.searchQuery,
        pageNum: this.page,
        pageSize: this.limit
      };

      // 添加学校ID参数
      const schoolInfo = uni.getStorageSync('selectedSchool');
      if (schoolInfo) {
        try {
          const school = typeof schoolInfo === 'string' ? JSON.parse(schoolInfo) : schoolInfo;
          if (school && school.id) {
            params.schoolId = school.id;
          }
        } catch (error) {
          console.error('解析学校信息失败:', error);
        }
      }

      // 根据排序类型添加排序参数（按照后端接口要求传递数字）
      if (this.currentFilterTab === 0) {
        params.sort = 0; // 按相关度排序（综合）
      } else if (this.currentFilterTab === 1) {
        params.sort = 1; // 按新品排序
      } else if (this.currentFilterTab === 2) {
        params.sort = 2; // 按销量排序
      } else if (this.currentFilterTab === 3) {
        params.sort = this.priceOrder === 1 ? 3 : 4; // 3->价格从低到高，4->价格从高到低
      }
      
      // 调用搜索商品API
      searchProductList(params).then(response => {
        const { data } = response;
        
        if (data && data.list) {
          if (this.page === 1) {
            this.productList = data.list;
          } else {
            this.productList = [...this.productList, ...data.list];
          }
          
          // 判断是否还有更多数据
          if (data.list.length < this.limit || this.page >= data.totalPage) {
            this.loadingType = 'nomore';
          } else {
            this.loadingType = 'more';
          }
        } else {
          this.loadingType = 'nomore';
        }
        
        this.isLoading = false;
      }).catch(() => {
        this.loadingType = 'more';
        this.isLoading = false;
      });
    },
    
    // 加载更多
    loadMoreProduct() {
      if (this.loadingType === 'nomore' || this.isLoading) return;
      
      this.page += 1;
      this.loadProductData();
    },
    
    // 跳转到商品详情
    navToDetailPage(item) {
      uni.navigateTo({
        url: `/pages/product/product?id=${item.id}`
      });
    },
    
    // 添加到购物车
    addToCart(product) {
      // 直接跳转到商品详情页，让用户选择完整规格
      uni.navigateTo({
        url: `/pages/product/product?id=${product.id}`
      });
    },
    
    // 处理图片加载
    handleImageLoad(index) {
      // 图片加载成功处理
    },
    
    // 处理图片加载错误
    handleImageError(index) {
      // 图片加载错误处理
    },
    
    // 返回上一页
    goBack() {
      uni.navigateBack();
    },
    
    // 加载推荐商品
    loadRecommendedProducts() {
      // 使用搜索API获取推荐商品，不传关键词，按相关度排序
      const params = {
        keyword: '',
        pageNum: 1,
        pageSize: 20,
        sort: 0 // 按相关度排序
      };

      // 添加学校ID参数
      const schoolInfo = uni.getStorageSync('selectedSchool');
      if (schoolInfo) {
        try {
          const school = typeof schoolInfo === 'string' ? JSON.parse(schoolInfo) : schoolInfo;
          if (school && school.id) {
            params.schoolId = school.id;
          }
        } catch (error) {
          console.error('解析学校信息失败:', error);
        }
      }
      
      searchProductList(params).then(response => {
        const { data } = response;
        if (data && data.list) {
          // 转换数据格式以适配 ProductWaterfall 组件
          this.recommendedProductList = data.list.map(item => ({
            ...item,
            productName: item.name || item.productName,
            pic: item.pic || item.cover,
            cover: item.pic || item.cover
          }));
        }
      }).catch(error => {
        console.error('加载推荐商品失败:', error);
        // 如果API调用失败，可以设置一些默认的推荐商品
        this.recommendedProductList = [];
      });
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

/* 搜索页面导航栏渐变背景 */
.container {
  ::v-deep .nav-bar {
    background: linear-gradient(180deg, rgba(221, 255, 153, 0.8) 0%, rgba(221, 255, 153, 0.7) 100%) !important;
  }
}

.source-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100vw;
  height: 480rpx;
  background: url("https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/category-top-bg.png");
  z-index: -1;
  
  &.fixed {
    position: fixed;
  }
}

/* 导航栏内的搜索容器 */
.nav-search-container {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding-right: 20rpx;
  box-sizing: border-box;
  
  .search-wrapper {
    display: flex;
    align-items: center;
    width: 100%;
    border-radius: 40rpx;
    padding: 10rpx 20rpx;
    background-color: rgba(255, 255, 255, 0.9);
    position: relative;
    
    .search-btn {
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 0 10rpx;
      margin-right: 10rpx;
      
      .search-icon {
        width: 40rpx;
        height: 40rpx;
      }
    }
    
    .search-input {
      flex: 1;
      border: none;
      outline: none;
      background-color: transparent;
      font-size: 28rpx;
      color: #333;
    }
    
    .clear-btn {
      padding: 0;
      display: flex;
      align-items: center;
      justify-content: center;
      position: absolute;
      right: 20rpx;
      top: 50%;
      transform: translateY(-50%);
      z-index: 10;
      width: 40rpx;
      height: 40rpx;
      background-color: #ddd;
      border-radius: 50%;
      
      .clear-text {
        font-size: 24rpx;
        color: #fff;
        display: flex;
        align-items: center;
        justify-content: center;
        width: 100%;
        height: 100%;
        padding-bottom: 2rpx;
      }
    }
  }
}

.main-body {
  background: #fff;
  height: 100%;
  width: 100%;
  position: relative;
  padding-bottom: 30rpx;
  /* 底部间距 */
  
  .hot-area {
    padding: 20rpx 30rpx;
    background-color: #fff;
    /* 搜索历史 */
    
    .title {
      font-size: 28rpx;
      font-weight: 500;
      color: #030303;
      margin-bottom: 20rpx;
    }
    
    .keyword-list {
      display: flex;
      flex-wrap: wrap;
      margin-bottom: 20rpx;
    }
    
    .keyword {
      display: inline-block;
      padding: 10rpx 20rpx;
      margin: 0 30rpx 10rpx 0;
      background-color: #f5f5f5;
      border-radius: 20rpx;
      cursor: pointer;
      box-shadow: 2rpx 2rpx 4rpx rgba(0, 0, 0, 0.1);
      transition: background-color 0.3s;
      
      &:hover {
        background-color: #eaeaea;
      }
      
      .keyword-text {
        font-size: 24rpx;
        color: #555;
        display: flex;
        align-items: center;
      }
    }
    
    .search-history {
      margin-top: 30rpx;
      
      .history-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20rpx;
        
        .clear-history {
          cursor: pointer;
          
          text {
            font-size: 28rpx;
            color: #030303;
            font-weight: 500;
          }
        }
      }
    }
  }
  
  /* 搜索结果为空 */
  .no-results {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 20px;
    text-align: center;
    
    .no-results-image {
      width: 344rpx;
      height: 266rpx;
      margin-bottom: 10px;
    }
    
    .no-results-text {
      font-size: 32rpx;
      color: #000000;
    }
  }
  
  /* 商品列表 */
  .goods-list {
    margin-top: 20rpx;
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(48%, 1fr));
    gap: 10px;
    padding: 10px;
    
    .goods-item {
      display: flex;
      flex-direction: column;
      background: #fff;
      border-radius: 10px;
      box-shadow: 4rpx 24rpx 20rpx rgba(0, 0, 0, 0.05);
      padding: 10px;
      transition: all 0.3s ease-in-out;
      overflow: hidden;
      
      &:hover {
        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15);
      }
      
      .image-wrapper {
        width: 100%;
        margin-bottom: 10px;
        border-radius: 3px;
        overflow: hidden;
        
        image {
          width: 100%;
          height: 250rpx;
          object-fit: cover;
          opacity: 1;
        }
      }
      
      .placeholder {
        width: 100%;
        height: 250rpx;
        background-color: #f0f0f0;
        display: flex;
        justify-content: center;
        align-items: center;
        
        &::before {
          content: "缺少商品主图";
          color: #ccc;
          font-size: 14px;
        }
      }
      
      .info-wrapper {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        
        .title {
          font-size: 32rpx;
          color: #333333;
          line-height: 35px;
          overflow: hidden;
          text-overflow: ellipsis;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          word-wrap: break-word;
          word-break: break-all;
        }
        
        .title2 {
          font-size: 24rpx;
          color: #909399;
          line-height: 30px;
          margin-top: 5px;
          overflow: hidden;
          text-overflow: ellipsis;
        }
        
        .price-box {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-top: 10px;
          font-size: 24px;
          color: #909399;
          
          .price {
            font-size: 42rpx;
            font-weight: bold;
            color: #FF4225;
            
            &:before {
              content: "￥";
              font-size: 24rpx;
            }
          }
          
          .add-cart {
            width: 48rpx;
            height: 48rpx;
          }
        }
      }
    }
  }
  
  /* 推荐商品区域样式 */
  .recommended-container {
    margin-top: 30rpx;
    width: 100%;
    position: relative;
    overflow: visible;
    
    &.relative-container {
      position: relative;
      z-index: 1;
    }
    
    .recommended-title {
      font-size: 28rpx;
      font-weight: 500;
      color: #030303;
      padding: 20rpx 24rpx;
      text-align: left;
    }
  }
  
  .result-header-container {
    display: flex;
    flex-direction: column;
    padding: 0;
    background-color: #fff;
    
    .divider {
      width: 100%;
      height: 10rpx;
      background-color: #f5f5f5;
    }
    
    .filter-tabs {
      display: flex;
      align-items: center;
      padding: 20rpx 30rpx;
      width: 100%;
      
      .tab-items {
        display: flex;
        justify-content: space-between;
        width: 100%;
        align-items: center;
        
        .filter-tab {
          padding: 10rpx 10rpx;
          cursor: pointer;
          transition: all 0.3s;
          position: relative;
          flex: 1;
          text-align: center;
          
          &:hover {
            color: #000;
          }
          
          &.active {
            color: #000;
            font-weight: 500;
            
            .indicator {
              background-color: #000;
            }
          }
          
          text {
            font-size: 26rpx;
            color: #666;
          }
          
          .indicator {
            position: absolute;
            bottom: -5rpx;
            left: 50%;
            -webkit-transform: translateX(-50%);
            transform: translateX(-50%);
            width: 40rpx;
            height: 4rpx;
          }
          
          .price-arrows {
            display: flex;
            flex-direction: column;
            position: absolute;
            right: -10rpx;
            top: 50%;
            -webkit-transform: translateY(-50%);
            transform: translateY(-50%);
            
            .arrow-icon {
              width: 24rpx;
              height: 16rpx;
              opacity: 0.5;
              
              &.active {
                opacity: 1;
              }
            }
          }
        }
      }
    }
  }
  
  .product-grid {
    column-count: 2;
    column-gap: 16rpx;
    padding: 0 24rpx;
    margin-top: 0;
    
          .product-item {
        break-inside: avoid;
        margin-bottom: 24rpx;
        background: #fff;
        border-radius: 8rpx;
        overflow: hidden;
        box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.04);
        
        .item-image-container {
          position: relative;
          width: 100%;
          height: 320rpx;
          
          .item-image {
            width: 100%;
            height: 100%;
            object-fit: cover;
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
            z-index: 10;
            
            .sold-out-text {
              color: #ffffff;
              font-size: 32rpx;
              font-weight: bold;
              text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.3);
            }
          }
        }
        
        .item-info {
          padding: 16rpx;
          
          .item-title {
            font-size: 26rpx;
            color: #000;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            margin-bottom: 8rpx;
          }
          
          .item-price {
            font-size: 36rpx;
            font-weight: 600;
            color: #647D00;
          }
        }
      }
  }
  
  .load-more {
    padding: 20rpx 0;
    margin-top: 20rpx;
  }
}
</style> 