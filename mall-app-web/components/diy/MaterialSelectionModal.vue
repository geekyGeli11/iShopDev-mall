<template>
  <view v-if="visible" class="material-modal-overlay" @tap="handleOverlayTap">
    <view :class="['material-modal', visible ? 'modal-enter' : 'modal-leave']" @tap.stop>
      <!-- 分类标签 -->
      <view class="category-tabs">
        <view
          v-for="(category, idx) in categories"
          :key="category.id"
          class="category-tab"
          :data-index="idx"
          @tap="onCategoryTap"
        >
          <text :class="['category-name', currentCategoryIndex === idx ? 'active' : '']">{{ category.name }}</text>
          <view class="category-indicator" v-show="currentCategoryIndex === idx"></view>
        </view>
      </view>

      <!-- 素材网格 -->
      <view class="materials-grid">
        <!-- 无素材提示 -->
        <view v-if="currentCategoryMaterials.length === 0" class="no-materials-tip">
          <text>暂无素材</text>
        </view>

        <view
          v-for="material in currentCategoryMaterials"
          :key="material.id"
          :class="['material-item', selectedMaterials.includes(material.id) ? 'selected' : '']"
          @tap="toggleMaterial(material)"
        >
          <!-- 系统字体使用文字图标 -->
          <view v-if="material.isSystemFont" class="system-font-icon">
            <text class="system-font-text">Aa</text>
          </view>
          <image v-else class="material-image" :src="material.fileUrl || material.image" mode="aspectFit" />
          <text class="material-name">{{ material.name }}</text>
        </view>
      </view>

      <!-- 选择计数 -->
      <view class="selection-count">
        <text class="count-text">(已选择{{ selectedMaterials.length }}/1)</text>
      </view>

      <!-- 已选择区域 -->
      <view class="selected-section">
        <text class="section-title">已选择</text>
        <view class="selected-materials">
          <view
            v-for="materialId in selectedMaterials"
            :key="materialId"
            class="selected-material"
          >
            <!-- 系统字体使用文字图标 -->
            <view v-if="getMaterialById(materialId).isSystemFont" class="selected-system-font-icon">
              <text class="selected-system-font-text">Aa</text>
            </view>
            <image
              v-else-if="getMaterialById(materialId).fileUrl"
              class="selected-image"
              :src="getMaterialById(materialId).fileUrl"
              mode="aspectFit"
            />
            <text class="selected-name">{{ getMaterialById(materialId).name }}</text>
          </view>
        </view>
        <text class="selected-count">(已选择{{ selectedMaterials.length }})</text>
      </view>

      <!-- 底部按钮 -->
      <view class="modal-footer">
        <view class="footer-btn cancel-btn" @tap="handleCancel">
          <text class="btn-text">取消</text>
        </view>
        <view class="footer-btn confirm-btn" @tap="handleConfirm">
          <text class="btn-text">完成</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getDIYMaterials, getDIYMaterialCategories } from '@/api/diy.js';

export default {
  name: 'MaterialSelectionModal',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    // 素材类型过滤：1-图片素材，2-文字素材，不传则显示全部
    materialType: {
      type: Number,
      default: null
    }
  },
  data() {
    return {
      currentCategoryIndex: 0,
      selectedMaterials: [],
      categories: [],
      materials: {}
    };
  },
  computed: {
    currentCategoryMaterials() {
      const categoryId = this.categories[this.currentCategoryIndex]?.id;
      const materials = this.materials[categoryId] || [];
      
      // 如果当前分类是文字素材分类，添加系统文字选项
      const currentCategory = this.categories[this.currentCategoryIndex];
      if (currentCategory && currentCategory.type == 2) {
        // 检查是否已经有系统文字选项
        const hasSystemFont = materials.some(m => m.id === 'system-font');
        if (!hasSystemFont) {
          return [
            {
              id: 'system-font',
              name: '系统文字',
              fileUrl: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/system-font-icon.png',
              fileType: '2',
              fontFamily: 'sans-serif',
              fontFileUrl: '',
              isSystemFont: true
            },
            ...materials
          ];
        }
      }
      
      return materials;
    }
  },
  watch: {
    visible: {
      handler(newVal) {
        if (newVal) {
          // 每次打开弹窗时重置状态并加载数据
          this.resetAndLoad();
        }
      },
      immediate: true
    }
  },
  methods: {
    // 重置状态并加载数据
    async resetAndLoad() {
      this.currentCategoryIndex = 0;
      this.selectedMaterials = [];
      this.categories = [];
      this.materials = {};
      await this.loadMaterials();
    },

    // 加载素材数据
    async loadMaterials() {
      try {
        // 加载分类数据
        const categoriesResponse = await getDIYMaterialCategories();
        if (categoriesResponse && categoriesResponse.code === 200) {
          let categories = categoriesResponse.data;
          // 根据materialType过滤分类
          if (this.materialType) {
            categories = categories.filter(cat => cat.type == this.materialType);
          }
          this.categories = categories;
        } else {
          this.categories = [{ id: 1, name: '默认分类' }];
        }

        // 加载素材数据
        const materialsResponse = await getDIYMaterials();
        if (materialsResponse && materialsResponse.code === 200) {
          // 按分类组织素材数据
          const materialsByCategory = {};
          materialsResponse.data.forEach(material => {
            const categoryId = material.categoryId || 1;
            // 根据materialType过滤素材
            if (this.materialType && material.fileType != this.materialType) {
              return;
            }
            if (!materialsByCategory[categoryId]) {
              materialsByCategory[categoryId] = [];
            }
            materialsByCategory[categoryId].push(material);
          });
          this.materials = materialsByCategory;

          // 确保当前分类索引有效
          if (this.categories.length > 0) {
            this.currentCategoryIndex = 0;
          }
        }
      } catch (error) {
        // 出错时使用模拟数据
        this.loadMockData();
      }
    },

    // 加载模拟数据
    loadMockData() {
      this.categories = [
        { id: 1, name: '建筑' },
        { id: 2, name: '装饰' },
        { id: 3, name: '文字' }
      ];
      this.materials = {
        1: [
          {
            id: 1,
            name: '测试素材1',
            fileUrl: 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/mall/images/86cb7c5dde2d43c7a5d270be3f9996ce.png',
            categoryId: 1
          },
          {
            id: 2,
            name: '测试素材2',
            fileUrl: 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/mall/images/86cb7c5dde2d43c7a5d270be3f9996ce.png',
            categoryId: 1
          }
        ]
      };

    },

    // 分类点击事件处理
    onCategoryTap(e) {
      const index = parseInt(e.currentTarget.dataset.index);
      console.log('分类点击事件, index:', index);
      this.switchCategory(index);
    },

    // 切换分类
    switchCategory(index) {
      console.log('切换分类:', index, '当前索引:', this.currentCategoryIndex);
      if (this.currentCategoryIndex !== index) {
        this.currentCategoryIndex = index;
        // 切换分类时清空已选择的素材
        this.selectedMaterials = [];
      }
      console.log('切换后索引:', this.currentCategoryIndex);
    },

    // 切换素材选择状态
    toggleMaterial(material) {
      const index = this.selectedMaterials.indexOf(material.id);
      if (index > -1) {
        this.selectedMaterials.splice(index, 1);
      } else {
        // 限制只能选择一个
        this.selectedMaterials = [material.id];
      }
    },

    // 根据ID获取素材
    getMaterialById(id) {
      // 处理系统文字选项
      if (id === 'system-font') {
        return {
          id: 'system-font',
          name: '系统文字',
          fileUrl: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/system-font-icon.png',
          fileType: '2',
          fontFamily: 'sans-serif',
          fontFileUrl: '',
          isSystemFont: true
        };
      }
      
      for (const categoryId in this.materials) {
        const material = this.materials[categoryId].find(m => m.id === id);
        if (material) return material;
      }
      return { name: '', image: '', fileUrl: '' };
    },

    // 处理遮罩层点击
    handleOverlayTap() {
      this.handleCancel();
    },

    // 取消
    handleCancel() {
      this.selectedMaterials = [];
      this.$emit('close');
    },

    // 确认选择
    handleConfirm() {
      if (this.selectedMaterials.length > 0) {
        const selectedMaterials = this.selectedMaterials.map(id => {
          const material = this.getMaterialById(id);
          return material;
        });
        this.$emit('material-selected', selectedMaterials);
      }
      this.$emit('close');
    }
  }
};
</script>

<style lang="scss" scoped>
.material-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
}

.material-modal {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  width: 100%;
  max-height: 80vh;
  background: #282828;
  border-radius: 32rpx 32rpx 0 0;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  transform: translateY(100%);
  transition: transform 0.3s ease-out;
}

.material-modal.modal-enter {
  transform: translateY(0);
}

.material-modal.modal-leave {
  transform: translateY(100%);
}

.modal-header {
  padding: 32rpx 32rpx 20rpx;
  border-bottom: 1rpx solid rgba(255, 255, 255, 0.1);
}

.modal-title {
  font-size: 32rpx;
  color: #FFFFFF;
  font-weight: 500;
  text-align: center;
  display: block;
}

.category-tabs {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  padding: 24rpx 32rpx;
  gap: 80rpx;
  border-bottom: 1rpx solid rgba(255, 255, 255, 0.1);
  width: 100%;
  box-sizing: border-box;
}

.category-tab {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16rpx 16rpx 0;
  background: transparent;
  border: none;
  border-radius: 0;
}

.category-indicator {
  width: 48rpx;
  height: 6rpx;
  border-radius: 3rpx;
  margin-top: 12rpx;
  background-color: #A9FF00;
}

.category-name {
  font-size: 32rpx;
  color: rgba(255, 255, 255, 0.5);
  font-weight: 400;
}

.category-name.active {
  color: #FFFFFF;
  font-weight: 500;
}

.materials-grid {
  flex: 1;
  padding: 20rpx 32rpx;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20rpx;
  overflow-y: auto;
}

.no-materials-tip {
  grid-column: 1 / -1;
  padding: 60rpx 20rpx;
  text-align: center;
  color: rgba(255, 255, 255, 0.7);
  font-size: 28rpx;
}

.material-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16rpx;
  border-radius: 8rpx;
  background: rgba(255, 255, 255, 0.05);
  border: 2rpx solid transparent;
  transition: all 0.3s ease;

  &.selected {
    background: #A9FF00;
    border-color: #A9FF00;
  }
}

.material-image {
  width: 80rpx;
  height: 80rpx;
  border-radius: 6rpx;
  margin-bottom: 8rpx;
}

.material-name {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.8);
  text-align: center;

  .material-item.selected & {
    color: #0A0D05;
  }
}

.selection-count {
  padding: 0 32rpx 16rpx;
  text-align: right;
}

.count-text {
  font-size: 24rpx;
  color: #A9FF00;
}

.selected-section {
  padding: 20rpx 32rpx;
  border-top: 1rpx solid rgba(255, 255, 255, 0.1);
}

.section-title {
  font-size: 28rpx;
  color: #FFFFFF;
  margin-bottom: 16rpx;
}

.selected-materials {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-bottom: 12rpx;
}

.selected-material {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12rpx;
  background: #A9FF00;
  border-radius: 8rpx;
}

.selected-image {
  width: 60rpx;
  height: 60rpx;
  border-radius: 6rpx;
  margin-bottom: 6rpx;
}

.selected-name {
  font-size: 20rpx;
  color: #0A0D05;
  text-align: center;
}

.selected-count {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.6);
  text-align: right;
}

.modal-footer {
  display: flex;
  gap: 20rpx;
  padding: 20rpx 32rpx 32rpx;
}

.footer-btn {
  flex: 1;
  padding: 16rpx;
  border-radius: 8rpx;
  text-align: center;
}

.cancel-btn {
  background: rgba(255, 255, 255, 0.1);
  border: 1rpx solid rgba(255, 255, 255, 0.2);
}

.confirm-btn {
  background: #A9FF00;
}

.btn-text {
  font-size: 28rpx;
  font-weight: 500;

  .cancel-btn & {
    color: rgba(255, 255, 255, 0.8);
  }

  .confirm-btn & {
    color: #0A0D05;
  }
}

// 系统字体图标样式
.system-font-icon {
  width: 80rpx;
  height: 80rpx;
  border-radius: 6rpx;
  margin-bottom: 8rpx;
  background: rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
}

.system-font-text {
  font-size: 36rpx;
  color: #A9FF00;
  font-weight: 600;
}

.selected-system-font-icon {
  width: 60rpx;
  height: 60rpx;
  border-radius: 6rpx;
  margin-bottom: 6rpx;
  background: rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
}

.selected-system-font-text {
  font-size: 28rpx;
  color: #0A0D05;
  font-weight: 600;
}
</style>
