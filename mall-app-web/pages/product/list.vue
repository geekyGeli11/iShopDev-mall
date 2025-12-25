<template>
  <view class="content">
    <!-- 导航栏 -->
    <view class="navbar" :style="{'position': headerPosition, 'top': headerTop}">
      <view 
        class="nav-item" 
        :class="{ 'current': filterIndex === 0 }"
        @tap="tabClick(0)"
      >综合排序</view>
      <view 
        class="nav-item" 
        :class="{ 'current': filterIndex === 1 }"
        @tap="tabClick(1)"
      >销量优先</view>
      <view 
        class="nav-item" 
        :class="{ 'current': filterIndex === 2 }"
        @tap="tabClick(2)"
      >
        <text>价格</text>
        <view class="p-box">
          <text class="yticon icon-shang" :class="{ 'active': priceOrder === 1 && filterIndex === 2 }"></text>
          <text class="yticon icon-shang xia" :class="{ 'active': priceOrder === 2 && filterIndex === 2 }"></text>
        </view>
      </view>
      <text class="cate-item yticon icon-fenlei1" @tap="toggleCateMask('show')"></text>
    </view>
    
    <!-- 商品列表 -->
    <view class="goods-list">
      <view 
        v-for="(item, index) in productList" 
        :key="index" 
        class="goods-item" 
        @tap="navToDetailPage(item)"
      >
        <view class="image-wrapper">
          <image :src="item.pic" mode="aspectFill"></image>
        </view>
        <view class="info-wrapper">
          <text class="title clamp">{{ item.name }}</text>
          <text class="title2">{{ item.subTitle }}</text>
          <view class="price-box">
            <text class="price">{{ item.price }}</text>
            <text>已售 {{ item.sale }}</text>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 加载状态 -->
    <uni-load-more :status="loadingType"></uni-load-more>
    
    <!-- 分类侧边栏 -->
    <view 
      class="cate-mask" 
      :class="[cateMaskState === 0 ? 'none' : cateMaskState === 1 ? 'show' : '']" 
      @tap="toggleCateMask"
    >
      <view 
        class="cate-content" 
        @tap.stop="stopPrevent" 
        @touchmove.stop="stopPrevent"
      >
        <scroll-view class="cate-list" scroll-y="true">
          <view v-for="item in cateList" :key="item.id">
            <view class="cate-item b-b two">{{ item.name }}</view>
            <view 
              v-for="tItem in item.children" 
              :key="tItem.id" 
              class="cate-item b-b" 
              :class="{ 'active': tItem.id == searchParam.productCategoryId }"
              @tap="changeCate(item.id, tItem.id)"
            >{{ tItem.name }}</view>
          </view>
        </scroll-view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      headerPosition: 'fixed',
      headerTop: '0px',
      filterIndex: 0,
      priceOrder: 0,
      cateMaskState: 0,
      loadingType: 'more',
      productList: [],
      page: 1,
      pageSize: 10,
      totalPage: 1,
      searchParam: {
        keyword: '',
        brandId: '',
        productCategoryId: '',
        sort: 0
      },
      cateList: []
    };
  },
  
  onLoad(options) {
    // 获取参数
    if (options.keyword) {
      this.searchParam.keyword = options.keyword;
    }
    if (options.brandId) {
      this.searchParam.brandId = options.brandId;
    }
    if (options.categoryId) {
      this.searchParam.productCategoryId = options.categoryId;
    }
    
    // 获取分类列表
    this.getCateList();
    
    // 加载商品列表
    this.loadProductList();
  },
  
  onPullDownRefresh() {
    this.page = 1;
    this.productList = [];
    this.loadProductList();
    uni.stopPullDownRefresh();
  },
  
  onReachBottom() {
    if (this.loadingType === 'nomore') return;
    this.page++;
    this.loadProductList();
  },
  
  methods: {
    // 加载商品列表
    loadProductList() {
      this.loadingType = 'loading';
      
      // 模拟请求
      setTimeout(() => {
        let mockData = [];
        for (let i = 1; i <= 10; i++) {
          let index = this.productList.length + i;
          mockData.push({
            id: index,
            name: '濠江特色产品' + index,
            subTitle: '精选优质特产，品质保证',
            price: (Math.random() * 500).toFixed(2),
            sale: Math.floor(Math.random() * 1000),
            pic: 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/product/product' + (index % 4 + 1) + '.jpg'
          });
        }
        
        this.productList = this.page === 1 ? mockData : this.productList.concat(mockData);
        
        // 根据筛选条件排序
        this.sortProductList();
        
        // 更新加载状态
        if (this.page >= this.totalPage) {
          this.loadingType = 'nomore';
        } else {
          this.loadingType = 'more';
        }
      }, 500);
    },
    
    // 排序商品列表
    sortProductList() {
      let list = JSON.parse(JSON.stringify(this.productList));
      
      // 根据筛选条件排序
      switch (this.filterIndex) {
        case 0: // 综合排序
          // 默认顺序
          break;
        case 1: // 销量排序
          list.sort((a, b) => b.sale - a.sale);
          break;
        case 2: // 价格排序
          if (this.priceOrder === 1) { // 价格升序
            list.sort((a, b) => parseFloat(a.price) - parseFloat(b.price));
          } else if (this.priceOrder === 2) { // 价格降序
            list.sort((a, b) => parseFloat(b.price) - parseFloat(a.price));
          }
          break;
      }
      
      this.productList = list;
    },
    
    // 获取分类列表
    getCateList() {
      // 模拟获取分类数据
      this.cateList = [
        {
          id: 1,
          name: '特色食品',
          children: [
            { id: 11, name: '零食' },
            { id: 12, name: '糕点' },
            { id: 13, name: '地方特产' }
          ]
        },
        {
          id: 2,
          name: '生活用品',
          children: [
            { id: 21, name: '日用' },
            { id: 22, name: '家居' }
          ]
        },
        {
          id: 3,
          name: '健康保健',
          children: [
            { id: 31, name: '养生食材' },
            { id: 32, name: '滋补品' }
          ]
        }
      ];
    },
    
    // 点击顶部筛选
    tabClick(index) {
      this.filterIndex = index;
      
      // 价格排序点击
      if (index === 2) {
        this.priceOrder = this.priceOrder === 1 ? 2 : 1;
      } else {
        this.priceOrder = 0;
      }
      
      // 更新排序参数
      this.searchParam.sort = index;
      
      // 重新排序
      this.sortProductList();
      
      // 回到顶部
      uni.pageScrollTo({
        scrollTop: 0,
        duration: 300
      });
    },
    
    // 切换分类侧边栏状态
    toggleCateMask(type) {
      if (type === 'show') {
        this.cateMaskState = 1;
      } else {
        this.cateMaskState = this.cateMaskState === 0 ? 1 : 0;
      }
    },
    
    // 阻止事件冒泡
    stopPrevent() {},
    
    // 选择分类
    changeCate(parentId, id) {
      this.searchParam.productCategoryId = id;
      this.toggleCateMask();
      
      // 重新加载商品列表
      this.page = 1;
      this.productList = [];
      this.loadProductList();
    },
    
    // 跳转到商品详情页
    navToDetailPage(item) {
      uni.navigateTo({
        url: `/pages/product/product?id=${item.id}`
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
page,
.content {
  background: #f8f8f8;
}

.content {
  padding-top: 96rpx;
}

.navbar {
  position: fixed;
  left: 0;
  top: 0px;
  display: flex;
  width: 100%;
  height: 80rpx;
  background: #fff;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.06);
  z-index: 10;
  
  .nav-item {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100%;
    font-size: 30rpx;
    color: #333333;
    position: relative;
    
    &.current {
      color: #0088FF;
      
      &:after {
        content: "";
        position: absolute;
        left: 50%;
        bottom: 0;
        transform: translateX(-50%);
        width: 120rpx;
        height: 0;
        border-bottom: 4rpx solid #0088FF;
      }
    }
  }
  
  .p-box {
    display: flex;
    flex-direction: column;
    
    .yticon {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 30rpx;
      height: 14rpx;
      line-height: 1;
      margin-left: 4rpx;
      font-size: 26rpx;
      color: #888;
      
      &.active {
        color: #0088FF;
      }
    }
    
    .xia {
      transform: scaleY(-1);
    }
  }
  
  .cate-item {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100%;
    width: 80rpx;
    position: relative;
    font-size: 44rpx;
    
    &:after {
      content: "";
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-50%);
      border-left: 1px solid #ddd;
      width: 0;
      height: 36rpx;
    }
  }
}

/* 分类 */
.cate-mask {
  position: fixed;
  left: 0;
  top: 0px;
  bottom: 0;
  width: 100%;
  background: rgba(0, 0, 0, 0);
  z-index: 95;
  transition: 0.3s;
  
  .cate-content {
    width: 630rpx;
    height: 100%;
    background: #fff;
    float: right;
    transform: translateX(100%);
    transition: 0.3s;
  }
  
  &.none {
    display: none;
  }
  
  &.show {
    background: rgba(0, 0, 0, 0.4);
    
    .cate-content {
      transform: translateX(0);
    }
  }
}

.cate-list {
  display: flex;
  flex-direction: column;
  height: 100%;
  
  .cate-item {
    display: flex;
    align-items: center;
    height: 90rpx;
    padding-left: 30rpx;
    font-size: 28rpx;
    color: #555;
    position: relative;
  }
  
  .two {
    height: 64rpx;
    color: #303133;
    font-size: 30rpx;
    background: #f8f8f8;
  }
  
  .active {
    color: #0088FF;
  }
}

/* 商品列表 */
.goods-list {
  display: flex;
  flex-direction: column;
  padding: 0 30rpx;
  background: #fff;
  
  .goods-item {
    display: flex;
    flex-direction: row;
    padding: 20rpx 0;
    border-bottom: 1px solid #eee;
  }
  
  .image-wrapper {
    width: 30%;
    height: 250rpx;
    margin-right: 10rpx;
    border-radius: 3px;
    overflow: hidden;
    
    image {
      width: 100%;
      height: 100%;
      object-fit: cover;
      opacity: 1;
    }
  }
  
  .info-wrapper {
    flex: 1;
    max-width: 65%;
    display: flex;
    flex-direction: column;
    margin-top: 15rpx;
    justify-content: space-between;
    
    .title {
      font-size: 32rpx;
      color: #333333;
      line-height: 40rpx;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      /* 限制显示两行文字 */
      -webkit-box-orient: vertical;
      white-space: normal;
      /* 确保自动换行 */
    }
    
    .title2 {
      font-size: 24rpx;
      color: #909399;
      line-height: 30rpx;
      overflow: hidden;
      text-overflow: ellipsis;
      display: block;
    }
    
    .price-box {
      display: flex;
      align-items: center;
      justify-content: space-between;
      font-size: 24rpx;
      color: #909399;
      
      .price {
        font-size: 32rpx;
        color: #FF4225;
        
        &:before {
          content: "￥";
          font-size: 26rpx;
        }
      }
    }
  }
}
</style> 