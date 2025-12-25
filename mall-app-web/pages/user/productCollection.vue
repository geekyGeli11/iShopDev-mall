<template>
  <view class="container">
    <!-- 标题栏 -->
    <view class="header">
      <view class="title">我的收藏</view>
      <view class="edit-btn" @tap="toggleEditMode">{{ isEditMode ? '完成' : '编辑' }}</view>
    </view>
    
    <!-- 商品列表 -->
    <view class="product-list" v-if="collectionList.length > 0">
      <view 
        class="product-item" 
        v-for="(item, index) in collectionList" 
        :key="index"
        @tap="navToDetail(item)"
      >
        <!-- 选择框（编辑模式下显示） -->
        <view 
          class="checkbox" 
          v-if="isEditMode"
          @tap.stop="toggleSelect(index)"
        >
          <view class="checkbox-inner" :class="{ 'checked': item.selected }"></view>
        </view>
        
        <!-- 商品信息 -->
        <image class="product-image" :src="item.pic" mode="aspectFill"></image>
        <view class="product-info">
          <view class="product-name">{{ item.name }}</view>
          <view class="product-desc">{{ item.subtitle }}</view>
          <view class="product-price-box">
            <view class="product-price">¥{{ item.price }}</view>
            <view class="product-origin-price" v-if="item.originalPrice">¥{{ item.originalPrice }}</view>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 空状态 -->
    <view class="empty-box" v-else>
      <image class="empty-image" src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/empty-cart.png"></image>
      <view class="empty-text">您还没有收藏商品</view>
      <view class="to-shopping" @tap="navToHome">去逛逛</view>
    </view>
    
    <!-- 底部操作栏（编辑模式下显示） -->
    <view class="footer-action" v-if="isEditMode && collectionList.length > 0">
      <view class="action-left">
        <view class="checkbox" @tap="toggleSelectAll">
          <view class="checkbox-inner" :class="{ 'checked': isAllSelected }"></view>
        </view>
        <text>全选</text>
      </view>
      <view class="action-right" @tap="removeCollection">
        删除所选({{ selectedCount }})
      </view>
    </view>
  </view>
</template>

<script>
import { fetchProductCollectionList, deleteProductCollection, clearProductCollection } from '@/api/memberProductCollection';

export default {
  data() {
    return {
      collectionList: [],
      isEditMode: false,
      page: 1,
      limit: 10,
      loading: false,
      finished: false
    };
  },
  
  computed: {
    // 是否全选
    isAllSelected() {
      if (this.collectionList.length === 0) return false;
      return this.collectionList.every(item => item.selected);
    },
    
    // 选中的数量
    selectedCount() {
      return this.collectionList.filter(item => item.selected).length;
    }
  },
  
  onLoad() {
    this.getCollectionList();
  },
  
  onPullDownRefresh() {
    this.page = 1;
    this.finished = false;
    this.collectionList = [];
    this.getCollectionList(() => {
      uni.stopPullDownRefresh();
    });
  },
  
  onReachBottom() {
    if (!this.finished && !this.loading) {
      this.page++;
      this.getCollectionList();
    }
  },
  
  methods: {
    // 获取收藏列表
    getCollectionList(callback) {
      if (this.loading || this.finished) {
        if (callback) callback();
        return;
      }
      
      this.loading = true;
      uni.showLoading({
        title: '加载中...'
      });
      
      // 调用API
      fetchProductCollectionList({
        pageNum: this.page,
        pageSize: this.limit
      }).then(res => {
        const list = res.data?.list || [];
        
        // 添加选中状态
        list.forEach(item => {
          item.selected = false;
        });
        
        if (this.page === 1) {
          this.collectionList = list;
        } else {
          this.collectionList = [...this.collectionList, ...list];
        }
        
        // 判断是否加载完成
        this.finished = list.length < this.limit;
        
        uni.hideLoading();
        this.loading = false;
        if (callback) callback();
      }).catch(() => {
        uni.hideLoading();
        this.loading = false;
        if (callback) callback();
      });
    },
    
    // 切换编辑模式
    toggleEditMode() {
      this.isEditMode = !this.isEditMode;
      
      // 退出编辑模式时，取消所有选择
      if (!this.isEditMode) {
        this.collectionList.forEach(item => {
          item.selected = false;
        });
      }
    },
    
    // 切换选择状态
    toggleSelect(index) {
      this.collectionList[index].selected = !this.collectionList[index].selected;
    },
    
    // 全选/取消全选
    toggleSelectAll() {
      const newStatus = !this.isAllSelected;
      this.collectionList.forEach(item => {
        item.selected = newStatus;
      });
    },
    
    // 删除所选收藏
    removeCollection() {
      if (this.selectedCount === 0) {
        uni.showToast({
          title: '请先选择商品',
          icon: 'none'
        });
        return;
      }
      
      uni.showModal({
        title: '提示',
        content: '确定要删除所选收藏商品吗？',
        success: res => {
          if (res.confirm) {
            const selectedItems = this.collectionList.filter(item => item.selected);
            
            // 如果是全选，则清空全部
            if (this.isAllSelected) {
              this.clearAllCollection();
              return;
            }
            
            // 单独删除选中的商品
            const deletePromises = selectedItems.map(item => {
              return deleteProductCollection({ productId: item.productId });
            });
            
            Promise.all(deletePromises).then(() => {
              // 重新获取收藏列表
              this.page = 1;
              this.finished = false;
              this.collectionList = [];
              this.getCollectionList();
              
              // 显示提示
              uni.showToast({
                title: '删除成功',
                icon: 'success'
              });
              
              // 如果删除后列表为空，退出编辑模式
              if (this.collectionList.length === 0) {
                this.isEditMode = false;
              }
            });
          }
        }
      });
    },
    
    // 清空所有收藏
    clearAllCollection() {
      clearProductCollection().then(() => {
        this.collectionList = [];
        this.isEditMode = false;
        
        uni.showToast({
          title: '清空成功',
          icon: 'success'
        });
      });
    },
    
    // 跳转到商品详情
    navToDetail(item) {
      // 编辑模式下点击不跳转
      if (this.isEditMode) return;
      
      uni.navigateTo({
        url: `/pages/product/product?id=${item.productId}`
      });
    },
    
    // 跳转到首页
    navToHome() {
      uni.switchTab({
        url: '/pages/new_index/index'
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

page, .content {
  background: #f8f8f8;
}

.hot-section {
  display: flex;
  flex-wrap: wrap;
  padding: 0 30rpx;
  margin-top: 16rpx;
  background: #fff;
  
  .guess-item {
    display: flex;
    flex-direction: row;
    width: 100%;
    padding-bottom: 40rpx;
  }
  
  .image-wrapper {
    width: 30%;
    height: 250rpx;
    border-radius: 3px;
    overflow: hidden;
    
    image {
      width: 100%;
      height: 100%;
      opacity: 1;
    }
  }
  
  .title {
    font-size: 32rpx;
    color: #333333;
    line-height: 80rpx;
  }
  
  .title2 {
    font-size: 24rpx;
    color: #909399;
    line-height: 40rpx;
    height: 80rpx;
    overflow: hidden;
    text-overflow: ellipsis;
    display: block;
  }
  
  .price {
    font-size: 32rpx;
    color: #FF4225;
    line-height: 80rpx;
  }
  
  .txt {
    width: 70%;
    display: flex;
    flex-direction: column;
    padding-left: 40rpx;
  }
  
  .hor-txt {
    display: flex;
    justify-content: space-between;
  }
  
  .time {
    font-size: 24rpx;
    color: #333333;
    line-height: 80rpx;
  }
}

.container {
  .header {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    height: 90rpx;
    background: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 30rpx;
    box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
    z-index: 100;
    
    .title {
      font-size: 32rpx;
      font-weight: bold;
      color: #303133;
    }
    
    .edit-btn {
      position: absolute;
      right: 30rpx;
      font-size: 28rpx;
      color: #0088FF;
    }
  }
  
  .product-list {
    margin-top: 110rpx;
    padding: 0 20rpx;
    
    .product-item {
      display: flex;
      align-items: center;
      background: #fff;
      margin-bottom: 20rpx;
      padding: 20rpx;
      border-radius: 12rpx;
      position: relative;
      
      .checkbox {
        width: 40rpx;
        height: 40rpx;
        border-radius: 50%;
        border: 2rpx solid #dcdfe6;
        margin-right: 20rpx;
        display: flex;
        justify-content: center;
        align-items: center;
        
        .checkbox-inner {
          width: 24rpx;
          height: 24rpx;
          border-radius: 50%;
          transition: all 0.2s;
          
          &.checked {
            background: #0088FF;
          }
        }
      }
      
      .product-image {
        width: 180rpx;
        height: 180rpx;
        border-radius: 8rpx;
        margin-right: 20rpx;
      }
      
      .product-info {
        flex: 1;
        height: 180rpx;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        
        .product-name {
          font-size: 28rpx;
          font-weight: bold;
          color: #303133;
          display: -webkit-box;
          -webkit-box-orient: vertical;
          -webkit-line-clamp: 1;
          overflow: hidden;
        }
        
        .product-desc {
          font-size: 24rpx;
          color: #909399;
          margin: 10rpx 0;
          display: -webkit-box;
          -webkit-box-orient: vertical;
          -webkit-line-clamp: 2;
          overflow: hidden;
        }
        
        .product-price-box {
          display: flex;
          align-items: baseline;
          
          .product-price {
            font-size: 32rpx;
            color: #fa436a;
            font-weight: bold;
          }
          
          .product-origin-price {
            font-size: 24rpx;
            color: #909399;
            text-decoration: line-through;
            margin-left: 10rpx;
          }
        }
      }
    }
  }
  
  .empty-box {
    margin-top: 110rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding-top: 120rpx;
    
    .empty-image {
      width: 240rpx;
      height: 240rpx;
      margin-bottom: 30rpx;
    }
    
    .empty-text {
      font-size: 28rpx;
      color: #909399;
      margin-bottom: 30rpx;
    }
    
    .to-shopping {
      width: 240rpx;
      height: 70rpx;
      line-height: 70rpx;
      text-align: center;
      font-size: 28rpx;
      color: #fff;
      background: #0088FF;
      border-radius: 35rpx;
    }
  }
  
  .footer-action {
    position: fixed;
    left: 0;
    right: 0;
    bottom: 0;
    height: 100rpx;
    background: #fff;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 30rpx;
    box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
    
    .action-left {
      display: flex;
      align-items: center;
      font-size: 28rpx;
      color: #303133;
      
      .checkbox {
        width: 40rpx;
        height: 40rpx;
        border-radius: 50%;
        border: 2rpx solid #dcdfe6;
        margin-right: 20rpx;
        display: flex;
        justify-content: center;
        align-items: center;
        
        .checkbox-inner {
          width: 24rpx;
          height: 24rpx;
          border-radius: 50%;
          transition: all 0.2s;
          
          &.checked {
            background: #0088FF;
          }
        }
      }
    }
    
    .action-right {
      width: 260rpx;
      height: 70rpx;
      line-height: 70rpx;
      text-align: center;
      font-size: 28rpx;
      color: #fff;
      background: #fa436a;
      border-radius: 35rpx;
    }
  }
}
</style> 