<template>
  <view class="material-replace-tool" v-if="visible">
    <!-- 分类选择 -->
    <view class="category-tabs">
      <scroll-view class="category-scroll" scroll-x="true" show-scrollbar="false">
        <view class="category-container">
          <view 
            v-for="(category, index) in categories" 
            :key="category.id"
            :class="['category-item', selectedCategoryId === category.id ? 'active' : '']"
            @tap="selectCategory(category)"
          >
            <image class="category-icon" :src="category.icon" mode="aspectFit" />
            <text class="category-name">{{ category.name }}</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 素材类型切换 -->
    <view class="material-types">
      <view 
        v-for="(type, index) in materialTypes" 
        :key="type.key"
        :class="['type-item', selectedType === type.key ? 'active' : '']"
        @tap="selectType(type.key)"
      >
        <text class="type-text">{{ type.name }}</text>
      </view>
    </view>

    <!-- 素材列表 -->
    <view class="material-list">
      <scroll-view class="material-scroll" scroll-y="true" @scrolltolower="loadMoreMaterials">
        <view class="material-grid">
          <view 
            v-for="(material, index) in materialList" 
            :key="material.id"
            class="material-item"
            @tap="selectMaterial(material)"
            @longpress="startDrag(material, $event)"
          >
            <view class="material-image-container">
              <image 
                class="material-image" 
                :src="material.thumbnail || material.url" 
                mode="aspectFit"
                @error="handleImageError(material)"
              />
              
              <!-- 选中状态 -->
              <view v-if="selectedMaterialId === material.id" class="selected-overlay">
                <image class="check-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/common/check.png" mode="aspectFit" />
              </view>
              
              <!-- 加载状态 -->
              <view v-if="material.loading" class="loading-overlay">
                <view class="loading-spinner"></view>
              </view>
            </view>
            
            <view class="material-info">
              <text class="material-name">{{ material.name }}</text>
              <view class="material-meta">
                <text class="material-size">{{ formatFileSize(material.size) }}</text>
                <text v-if="material.isPremium" class="premium-tag">VIP</text>
              </view>
            </view>
          </view>
        </view>
        
        <!-- 加载更多提示 -->
        <view v-if="loading" class="loading-more">
          <view class="loading-spinner"></view>
          <text class="loading-text">加载中...</text>
        </view>
        
        <!-- 没有更多数据提示 -->
        <view v-if="!hasMore && materialList.length > 0" class="no-more">
          <text class="no-more-text">没有更多素材了</text>
        </view>
        
        <!-- 空状态 -->
        <view v-if="!loading && materialList.length === 0" class="empty-state">
          <image class="empty-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/common/empty.png" mode="aspectFit" />
          <text class="empty-text">暂无素材</text>
        </view>
      </scroll-view>
    </view>

    <!-- 上传按钮 -->
    <view class="upload-section">
      <view class="upload-btn" @tap="uploadMaterial">
        <image class="upload-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/upload.png" mode="aspectFit" />
        <text class="upload-text">上传素材</text>
      </view>
    </view>

    <!-- 拖拽预览 -->
    <view v-if="isDragging" class="drag-preview" :style="dragPreviewStyle">
      <image class="drag-image" :src="dragMaterial.thumbnail || dragMaterial.url" mode="aspectFit" />
    </view>
  </view>
</template>

<script>
import { getDIYMaterials, getDIYMaterialCategories, uploadDIYImage } from '@/api/diy.js';

export default {
  name: 'MaterialReplaceTool',
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      // 分类数据
      categories: [],
      selectedCategoryId: null,
      
      // 素材类型
      materialTypes: [
        { key: 'all', name: '全部' },
        { key: 'image', name: '图片' },
        { key: 'pattern', name: '图案' },
        { key: 'texture', name: '纹理' }
      ],
      selectedType: 'all',
      
      // 素材列表
      materialList: [],
      selectedMaterialId: null,
      
      // 分页
      page: 1,
      pageSize: 20,
      loading: false,
      hasMore: true,
      
      // 拖拽相关
      isDragging: false,
      dragMaterial: null,
      dragStartPosition: { x: 0, y: 0 },
      dragPreviewStyle: {}
    };
  },
  watch: {
    visible(newVal) {
      if (newVal) {
        this.initData();
      }
    },
    selectedCategoryId() {
      this.resetAndLoadMaterials();
    },
    selectedType() {
      this.resetAndLoadMaterials();
    }
  },
  methods: {
    // 初始化数据
    async initData() {
      try {
        await this.loadCategories();
        await this.loadMaterials();
      } catch (error) {
        console.error('初始化素材数据失败:', error);
      }
    },
    
    // 加载分类
    async loadCategories() {
      try {
        const result = await getDIYMaterialCategories();
        if (result && result.code === 200) {
          this.categories = [
            { id: null, name: '全部', icon: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/category-all.png' },
            ...result.data
          ];
          this.selectedCategoryId = null;
        }
      } catch (error) {
        console.error('加载分类失败:', error);
      }
    },
    
    // 加载素材
    async loadMaterials() {
      if (this.loading || !this.hasMore) return;
      
      try {
        this.loading = true;
        
        const params = {
          page: this.page,
          pageSize: this.pageSize,
          categoryId: this.selectedCategoryId,
          type: this.selectedType === 'all' ? null : this.selectedType
        };
        
        const result = await getDIYMaterials(params);
        
        if (result && result.code === 200) {
          const newMaterials = result.data.list || [];
          
          if (this.page === 1) {
            this.materialList = newMaterials;
          } else {
            this.materialList = [...this.materialList, ...newMaterials];
          }
          
          this.hasMore = newMaterials.length === this.pageSize;
          this.page++;
        }
      } catch (error) {
        console.error('加载素材失败:', error);
        uni.showToast({
          title: '加载素材失败',
          icon: 'none'
        });
      } finally {
        this.loading = false;
      }
    },
    
    // 重置并加载素材
    resetAndLoadMaterials() {
      this.page = 1;
      this.hasMore = true;
      this.materialList = [];
      this.selectedMaterialId = null;
      this.loadMaterials();
    },
    
    // 加载更多素材
    loadMoreMaterials() {
      this.loadMaterials();
    },
    
    // 选择分类
    selectCategory(category) {
      this.selectedCategoryId = category.id;
    },
    
    // 选择类型
    selectType(type) {
      this.selectedType = type;
    },
    
    // 选择素材
    selectMaterial(material) {
      this.selectedMaterialId = material.id;
      this.$emit('material-selected', material);
    },
    
    // 开始拖拽
    startDrag(material, event) {
      this.isDragging = true;
      this.dragMaterial = material;
      
      const touch = event.touches[0];
      this.dragStartPosition = {
        x: touch.clientX,
        y: touch.clientY
      };
      
      this.updateDragPreview(touch.clientX, touch.clientY);
      
      // 监听全局触摸事件
      this.addGlobalTouchListeners();
    },
    
    // 更新拖拽预览位置
    updateDragPreview(x, y) {
      this.dragPreviewStyle = {
        left: (x - 40) + 'px',
        top: (y - 40) + 'px'
      };
    },
    
    // 添加全局触摸监听
    addGlobalTouchListeners() {
      // 这里可以添加全局触摸事件监听
      // 由于小程序限制，可能需要其他方式实现
    },
    
    // 结束拖拽
    endDrag() {
      this.isDragging = false;
      this.dragMaterial = null;
      this.removeGlobalTouchListeners();
    },
    
    // 移除全局触摸监听
    removeGlobalTouchListeners() {
      // 移除全局触摸事件监听
    },
    
    // 上传素材
    uploadMaterial() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: async (res) => {
          try {
            uni.showLoading({ title: '上传中...' });
            
            const result = await uploadDIYImage(res.tempFilePaths[0]);
            
            if (result && result.code === 200) {
              uni.showToast({
                title: '上传成功',
                icon: 'success'
              });
              
              // 重新加载素材列表
              this.resetAndLoadMaterials();
            } else {
              throw new Error(result.message || '上传失败');
            }
          } catch (error) {
            console.error('上传失败:', error);
            uni.showToast({
              title: '上传失败',
              icon: 'none'
            });
          } finally {
            uni.hideLoading();
          }
        }
      });
    },
    
    // 处理图片加载错误
    handleImageError(material) {
      material.thumbnail = 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/common/image-error.png';
    },
    
    // 格式化文件大小
    formatFileSize(size) {
      if (!size) return '';
      
      if (size < 1024) {
        return size + 'B';
      } else if (size < 1024 * 1024) {
        return Math.round(size / 1024) + 'KB';
      } else {
        return Math.round(size / (1024 * 1024)) + 'MB';
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.material-replace-tool {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.category-tabs {
  margin-bottom: 20rpx;
}

.category-scroll {
  white-space: nowrap;
}

.category-container {
  display: flex;
  padding: 0 20rpx;
  gap: 20rpx;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16rpx;
  border-radius: 12rpx;
  border: 1rpx solid #EEEEEE;
  min-width: 100rpx;
  transition: all 0.3s ease;
  
  &.active {
    border-color: #A7CB00;
    background-color: rgba(167, 203, 0, 0.1);
  }
}

.category-icon {
  width: 48rpx;
  height: 48rpx;
  margin-bottom: 8rpx;
}

.category-name {
  font-size: 24rpx;
  color: #333333;
}

.material-types {
  display: flex;
  margin-bottom: 20rpx;
  padding: 0 20rpx;
  gap: 20rpx;
}

.type-item {
  padding: 16rpx 24rpx;
  border-radius: 20rpx;
  background: #F5F5F5;
  transition: all 0.3s ease;
  
  &.active {
    background: #A7CB00;
    
    .type-text {
      color: #FFFFFF;
    }
  }
}

.type-text {
  font-size: 26rpx;
  color: #666666;
}

.material-list {
  flex: 1;
  overflow: hidden;
}

.material-scroll {
  height: 100%;
}

.material-grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 20rpx;
  padding: 0 20rpx;
}

.material-item {
  background: #FFFFFF;
  border-radius: 12rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}

.material-image-container {
  position: relative;
  width: 100%;
  height: 160rpx;
}

.material-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.selected-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(167, 203, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
}

.check-icon {
  width: 40rpx;
  height: 40rpx;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
}

.material-info {
  padding: 16rpx;
}

.material-name {
  font-size: 24rpx;
  color: #333333;
  margin-bottom: 8rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.material-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.material-size {
  font-size: 20rpx;
  color: #999999;
}

.premium-tag {
  background: #FF6B35;
  color: #FFFFFF;
  font-size: 20rpx;
  padding: 2rpx 8rpx;
  border-radius: 8rpx;
}

.upload-section {
  padding: 20rpx;
  border-top: 1rpx solid #EEEEEE;
}

.upload-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16rpx;
  padding: 24rpx;
  background: #F5F5F5;
  border: 2rpx dashed #CCCCCC;
  border-radius: 12rpx;
}

.upload-icon {
  width: 32rpx;
  height: 32rpx;
}

.upload-text {
  font-size: 28rpx;
  color: #666666;
}

.loading-more,
.no-more,
.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40rpx;
  gap: 16rpx;
}

.loading-spinner {
  width: 32rpx;
  height: 32rpx;
  border: 3rpx solid #EEEEEE;
  border-top: 3rpx solid #A7CB00;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text,
.no-more-text,
.empty-text {
  font-size: 26rpx;
  color: #999999;
}

.empty-icon {
  width: 80rpx;
  height: 80rpx;
  margin-bottom: 16rpx;
}

.drag-preview {
  position: fixed;
  width: 80rpx;
  height: 80rpx;
  z-index: 9999;
  pointer-events: none;
  opacity: 0.8;
}

.drag-image {
  width: 100%;
  height: 100%;
  border-radius: 8rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.3);
}
</style>
