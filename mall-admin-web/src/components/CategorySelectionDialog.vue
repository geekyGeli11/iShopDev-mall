<template>
  <el-dialog
    title="选择商品分类"
    :visible.sync="dialogVisible"
    width="700px"
    @close="handleClose"
    class="category-selection-dialog">
    <div class="category-selection-container">
      <!-- 搜索框 -->
      <div class="filter-container">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索分类名称"
          prefix-icon="el-icon-search"
          clearable
          @input="handleSearch"
          class="search-input">
        </el-input>
      </div>

      <!-- 分类列表 -->
      <div class="category-list" v-loading="loading">
        <el-radio-group v-model="selectedCategoryId" @change="handleCategoryChange" class="category-radio-group">
          <div
            v-for="category in filteredCategories"
            :key="category.id"
            :class="['category-item', { 'is-selected': selectedCategoryId === category.id }]">
            <el-radio :label="category.id">
              <div class="category-content">
                <div class="category-icon-wrapper">
                  <img
                    v-if="category.icon"
                    :src="category.icon"
                    class="category-icon"
                    @error="handleImageError" />
                  <div v-else class="category-icon-placeholder">
                    <i class="el-icon-folder-opened"></i>
                  </div>
                </div>
                <div class="category-details">
                  <div class="category-name">{{ category.name }}</div>
                  <div class="category-meta">
                    <span class="category-id">ID: {{ category.id }}</span>
                    <span v-if="category.productCount" class="category-count">{{ category.productCount }} 个商品</span>
                  </div>
                </div>
              </div>
            </el-radio>
          </div>
        </el-radio-group>

        <!-- 空状态 -->
        <div v-if="filteredCategories.length === 0 && !loading" class="empty-state">
          <i class="el-icon-folder-opened empty-icon"></i>
          <p class="empty-text">{{ searchKeyword ? '未找到匹配的分类' : '暂无分类数据' }}</p>
        </div>
      </div>
    </div>

    <span slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取 消</el-button>
      <el-button type="primary" @click="handleConfirm" :disabled="!selectedCategoryId">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { fetchList } from '@/api/productCate'

export default {
  name: 'CategorySelectionDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    selectedCategory: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      categories: [],
      searchKeyword: '',
      selectedCategoryId: null
    }
  },
  computed: {
    filteredCategories() {
      if (!this.searchKeyword) {
        return this.categories
      }
      return this.categories.filter(item => 
        item.name.toLowerCase().includes(this.searchKeyword.toLowerCase())
      )
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.loadCategories()
        if (this.selectedCategory) {
          this.selectedCategoryId = this.selectedCategory.id
        }
      }
    }
  },
  methods: {
    async loadCategories() {
      this.loading = true
      try {
        // 获取一级分类列表 (parentId = 0)
        const response = await fetchList(0, { pageSize: 100, pageNum: 1 })
        if (response && response.data && response.data.list) {
          this.categories = response.data.list
        }
      } catch (error) {
        console.error('获取分类列表失败:', error)
        this.$message.error('获取分类列表失败')
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      // 搜索逻辑已在computed中实现
    },
    handleCategoryChange(categoryId) {
      console.log('选中分类ID:', categoryId)
    },
    handleConfirm() {
      const selectedCategory = this.categories.find(item => item.id === this.selectedCategoryId)
      if (selectedCategory) {
        this.$emit('selection-confirmed', selectedCategory)
        this.handleClose()
      }
    },
    handleClose() {
      this.searchKeyword = ''
      this.selectedCategoryId = null
      this.$emit('update:visible', false)
    },
    handleImageError(e) {
      // 图片加载失败时的处理
      e.target.style.display = 'none'
    }
  }
}
</script>

<style scoped>
/* 对话框整体样式 */
.category-selection-dialog >>> .el-dialog__body {
  padding: 20px 20px 10px;
}

.category-selection-container {
  min-height: 400px;
  max-height: 600px;
  display: flex;
  flex-direction: column;
}

/* 搜索框样式 */
.filter-container {
  margin-bottom: 15px;
}

.search-input {
  width: 100%;
}

.search-input >>> .el-input__inner {
  border-radius: 4px;
  transition: all 0.3s;
}

.search-input >>> .el-input__inner:focus {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
}

/* 分类列表容器 */
.category-list {
  flex: 1;
  overflow-y: auto;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 8px;
  background-color: #fafafa;
}

/* 滚动条样式 */
.category-list::-webkit-scrollbar {
  width: 6px;
}

.category-list::-webkit-scrollbar-thumb {
  background-color: #dcdfe6;
  border-radius: 3px;
}

.category-list::-webkit-scrollbar-thumb:hover {
  background-color: #c0c4cc;
}

.category-list::-webkit-scrollbar-track {
  background-color: #f5f7fa;
}

/* Radio Group */
.category-radio-group {
  width: 100%;
}

/* 分类项样式 */
.category-item {
  margin-bottom: 8px;
  padding: 12px;
  background: #ffffff;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.category-item:last-child {
  margin-bottom: 0;
}

.category-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
  transform: translateY(-1px);
}

.category-item.is-selected {
  border-color: #409eff;
  background-color: #f0f7ff;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.2);
}

/* Radio 样式调整 */
.category-item >>> .el-radio {
  width: 100%;
  margin-right: 0;
}

.category-item >>> .el-radio__label {
  width: 100%;
  padding-left: 8px;
}

.category-item >>> .el-radio__input {
  line-height: 1;
}

/* 分类内容布局 */
.category-content {
  display: flex;
  align-items: center;
  width: 100%;
}

/* 图标容器 */
.category-icon-wrapper {
  flex-shrink: 0;
  margin-right: 12px;
}

.category-icon {
  width: 50px;
  height: 50px;
  border-radius: 6px;
  object-fit: cover;
  border: 1px solid #e4e7ed;
  transition: all 0.3s;
}

.category-item:hover .category-icon {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.category-icon-placeholder {
  width: 50px;
  height: 50px;
  border-radius: 6px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  font-size: 24px;
}

/* 分类详情 */
.category-details {
  flex: 1;
  min-width: 0;
}

.category-name {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.category-item.is-selected .category-name {
  color: #409eff;
}

.category-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: #909399;
}

.category-id {
  color: #909399;
}

.category-count {
  color: #67c23a;
}

/* 空状态样式 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #909399;
}

.empty-icon {
  font-size: 64px;
  color: #dcdfe6;
  margin-bottom: 16px;
}

.empty-text {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

/* 底部按钮样式 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>

