<template>
  <div class="template-preview">
    <!-- 预览头部 -->
    <div class="preview-header">
      <div class="template-info">
        <h2>{{ template.name }}</h2>
        <p class="template-desc">{{ template.description || '暂无描述' }}</p>
        <div class="template-meta">
          <el-tag :type="template.status === 1 ? 'success' : 'danger'" size="small">
            {{ template.status === 1 ? '启用' : '禁用' }}
          </el-tag>
          <span class="category-name">{{ getCategoryName(template.productCategoryId) }}</span>
          <span class="create-time">创建于 {{ $formatDate(template.createTime) }}</span>
        </div>
      </div>
      <div class="preview-actions">
        <el-button type="primary" icon="el-icon-edit" @click="editTemplate">编辑模板</el-button>
        <el-button type="success" icon="el-icon-view" @click="togglePreviewMode">
          {{ isFullPreview ? '退出预览' : '全屏预览' }}
        </el-button>
        <el-button icon="el-icon-refresh" @click="refreshPreview">刷新</el-button>
      </div>
    </div>

    <!-- 模板面预览网格 -->
    <div class="surfaces-grid" :class="{ 'full-preview': isFullPreview }">
      <div 
        v-for="surface in template.surfaces" 
        :key="surface.id"
        class="surface-card"
        @click="selectSurface(surface)"
        :class="{ 'selected': selectedSurface && selectedSurface.id === surface.id }"
      >
        <div class="surface-header">
          <h3>{{ surface.name }}</h3>
          <div class="surface-actions">
            <el-button type="text" size="mini" @click.stop="editSurface(surface)">
              <i class="el-icon-edit"></i>
            </el-button>
            <el-button type="text" size="mini" @click.stop="viewAreas(surface)">
              <i class="el-icon-view"></i>
            </el-button>
          </div>
        </div>
        
        <div class="surface-preview">
          <div class="image-container" :data-surface-id="surface.id">
            <img
              v-if="surface.exampleImage"
              :src="surface.exampleImage"
              :alt="surface.name"
              class="surface-image"
              @load="onImageLoad"
              @error="onImageError"
            />
            <div v-else class="no-image">
              <i class="el-icon-picture"></i>
              <p>暂无示例图</p>
            </div>
            
            <!-- DIY区域覆盖层 - 使用蒙版图片 -->
            <div class="areas-overlay" v-if="surface.areas && surface.areas.length > 0">
              <!-- 优先使用蒙版图片 -->
              <template v-for="area in surface.areas">
                <img
                  v-if="area.maskImageUrl"
                  :key="'mask-' + area.id"
                  :src="area.maskImageUrl"
                  class="area-mask-image"
                  :title="area.name || `区域${area.id}`"
                  @click.stop="selectArea(area)"
                  @error="handleMaskImageError(area)"
                />
              </template>

              <!-- 降级方案：使用SVG path (仅当没有蒙版图片时) -->
              <svg
                v-if="hasAreasWithoutMask(surface.areas)"
                class="areas-svg"
                :viewBox="getSvgViewBox(surface)"
                preserveAspectRatio="xMidYMid meet"
              >
                <path
                  v-for="area in surface.areas"
                  v-if="!area.maskImageUrl && area.pathData"
                  :key="area.id"
                  :d="area.pathData"
                  class="area-path"
                  :title="area.name || `区域${area.id}`"
                  @click.stop="selectArea(area)"
                />
              </svg>
            </div>
          </div>
          
          <div class="surface-info">
            <div class="areas-count">
              <i class="el-icon-location"></i>
              {{ surface.areas ? surface.areas.length : 0 }} 个DIY区域
            </div>
            <div class="surface-size" v-if="surface.exampleImage && imageDimensions[surface.id]">
              {{ imageDimensions[surface.id].width }} × {{ imageDimensions[surface.id].height }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 选中面的详细信息 -->
    <div v-if="selectedSurface" class="surface-detail">
      <el-card shadow="never">
        <div slot="header" class="clearfix">
          <span>{{ selectedSurface.name }} - 详细信息</span>
          <el-button style="float: right; padding: 3px 0" type="text" @click="selectedSurface = null">
            关闭
          </el-button>
        </div>
        
        <div class="detail-content">
          <div class="detail-image">
            <img 
              v-if="selectedSurface.exampleImage" 
              :src="selectedSurface.exampleImage" 
              :alt="selectedSurface.name"
              class="detail-surface-image"
            />
          </div>
          
          <div class="detail-info">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="面名称">{{ selectedSurface.name }}</el-descriptions-item>
              <el-descriptions-item label="排序">{{ selectedSurface.sort }}</el-descriptions-item>
              <el-descriptions-item label="DIY区域数量">
                {{ selectedSurface.areas ? selectedSurface.areas.length : 0 }}
              </el-descriptions-item>
              <el-descriptions-item label="创建时间">
                {{ $formatDate(selectedSurface.createTime) }}
              </el-descriptions-item>
            </el-descriptions>
            
            <div v-if="selectedSurface.areas && selectedSurface.areas.length > 0" class="areas-list">
              <h4>DIY区域列表</h4>
              <el-table :data="selectedSurface.areas" size="mini" border>
                <el-table-column prop="name" label="区域名称" width="120"/>
                <el-table-column prop="bounds" label="边界框" width="150"/>
                <el-table-column label="路径数据" min-width="200">
                  <template slot-scope="{row}">
                    <el-tooltip :content="row.pathData" placement="top">
                      <span class="path-data-preview">{{ row.pathData }}</span>
                    </el-tooltip>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="100">
                  <template slot-scope="{row}">
                    <el-button type="text" size="mini" @click="highlightArea(row)">
                      高亮
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 区域详情对话框 -->
    <el-dialog title="DIY区域详情" :visible.sync="areaDialogVisible" width="800px">
      <div v-if="selectedAreaSurface">
        <h4>{{ selectedAreaSurface.name }} - DIY区域列表</h4>
        <el-table :data="selectedAreaSurface.areas" border style="width: 100%">
          <el-table-column prop="id" label="区域ID" width="80"/>
          <el-table-column prop="name" label="区域名称" width="150"/>
          <el-table-column label="路径数据" min-width="200">
            <template slot-scope="{row}">
              <el-tooltip :content="row.pathData" placement="top">
                <span class="path-data-preview">{{ row.pathData }}</span>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="bounds" label="边界框" width="150"/>
          <el-table-column label="创建时间" width="160">
            <template slot-scope="{row}">
              {{ $formatDate(row.createTime) }}
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="areaDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'TemplatePreview',
  props: {
    template: {
      type: Object,
      required: true
    },
    categoryOptions: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      isFullPreview: false,
      selectedSurface: null,
      selectedArea: null,
      areaDialogVisible: false,
      selectedAreaSurface: null,
      imageDimensions: {} // 存储每个面的图片尺寸
    }
  },
  methods: {
    getCategoryName(categoryId) {
      if (!categoryId) return '全部分类'

      // 先在一级分类中查找
      for (let i = 0; i < this.categoryOptions.length; i++) {
        if (this.categoryOptions[i].value === categoryId) {
          return this.categoryOptions[i].label
        }
        // 再在二级分类中查找
        if (this.categoryOptions[i].children) {
          for (let j = 0; j < this.categoryOptions[i].children.length; j++) {
            if (this.categoryOptions[i].children[j].value === categoryId) {
              return this.categoryOptions[i].children[j].label
            }
          }
        }
      }
      return '未知分类'
    },
    
    editTemplate() {
      this.$router.push({ path: '/diy/updateTemplate', query: { id: this.template.id } })
    },
    
    togglePreviewMode() {
      this.isFullPreview = !this.isFullPreview
    },
    
    refreshPreview() {
      this.$emit('refresh')
    },
    
    selectSurface(surface) {
      this.selectedSurface = this.selectedSurface && this.selectedSurface.id === surface.id ? null : surface
    },
    
    editSurface(surface) {
      this.$emit('edit-surface', surface)
    },
    
    viewAreas(surface) {
      this.selectedAreaSurface = surface
      this.areaDialogVisible = true
    },
    
    selectArea(area) {
      this.selectedArea = area
      console.log('选中区域:', area)
    },
    
    highlightArea(area) {
      // 高亮显示指定区域
      console.log('高亮区域:', area)
      this.$message.info(`高亮显示区域: ${area.name || area.id}`)
    },
    
    getSvgViewBox(surface) {
      const dimensions = this.imageDimensions[surface.id]
      if (dimensions) {
        console.log(`SVG ViewBox for surface ${surface.id}:`, `0 0 ${dimensions.width} ${dimensions.height}`)
        return `0 0 ${dimensions.width} ${dimensions.height}`
      }
      console.warn(`使用默认ViewBox for surface ${surface.id}`)
      return '0 0 400 300' // 默认尺寸
    },

    onImageLoad(event) {
      const img = event.target
      const surfaceId = this.getCurrentSurfaceId(img)
      if (surfaceId) {
        this.$set(this.imageDimensions, surfaceId, {
          width: img.naturalWidth,
          height: img.naturalHeight
        })
        console.log(`图片加载完成 - Surface ${surfaceId}:`, {
          width: img.naturalWidth,
          height: img.naturalHeight
        })
        // 强制重新渲染SVG
        this.$forceUpdate()
      }
    },

    getCurrentSurfaceId(imgElement) {
      // 通过DOM结构查找对应的surface ID
      let parent = imgElement.parentElement
      while (parent && !parent.dataset.surfaceId) {
        parent = parent.parentElement
      }
      return parent ? parseInt(parent.dataset.surfaceId) : null
    },

    onImageError(event) {
      console.error('图片加载失败:', event)
    },

    /**
     * 检查是否有区域没有蒙版图片
     */
    hasAreasWithoutMask(areas) {
      if (!areas || areas.length === 0) return false
      return areas.some(area => !area.maskImageUrl && area.pathData)
    },

    /**
     * 蒙版图片加载失败处理
     */
    handleMaskImageError(area) {
      console.error('蒙版图片加载失败:', area.maskImageUrl)
      // 降级到使用pathData
      this.$set(area, 'maskImageUrl', null)
      this.$message.warning(`区域"${area.name}"的蒙版图片加载失败，已切换到路径模式`)
    }
  }
}
</script>

<style scoped>
.template-preview {
  padding: 20px;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.template-info h2 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 24px;
}

.template-desc {
  margin: 0 0 12px 0;
  color: #606266;
  font-size: 14px;
}

.template-meta {
  display: flex;
  align-items: center;
  gap: 15px;
}

.category-name, .create-time {
  font-size: 12px;
  color: #909399;
}

.surfaces-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.surfaces-grid.full-preview {
  grid-template-columns: repeat(auto-fill, minmax(500px, 1fr));
}

.surface-card {
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
  background: white;
}

.surface-card:hover {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.surface-card.selected {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.25);
}

.surface-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
}

.surface-header h3 {
  margin: 0;
  font-size: 16px;
  color: #303133;
}

.surface-actions {
  display: flex;
  gap: 5px;
}

.surface-preview {
  padding: 16px;
}

.image-container {
  position: relative;
  width: 100%;
  height: 200px;
  border-radius: 4px;
  overflow: hidden;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

.surface-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.no-image {
  text-align: center;
  color: #c0c4cc;
}

.no-image i {
  font-size: 48px;
  margin-bottom: 8px;
}

.areas-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.areas-svg {
  width: 100%;
  height: 100%;
}

/* 蒙版图片样式 */
.area-mask-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: contain;
  cursor: pointer;
  pointer-events: all;
  opacity: 0.5;
  transition: opacity 0.3s ease;
  mix-blend-mode: multiply; /* 正片叠底混合模式 */
}

.area-mask-image:hover {
  opacity: 0.7;
}

.area-path {
  fill: rgba(64, 158, 255, 0.3);
  stroke: #409eff;
  stroke-width: 2;
  cursor: pointer;
  pointer-events: all;
  transition: all 0.3s ease;
}

.area-path:hover {
  fill: rgba(64, 158, 255, 0.5);
  stroke-width: 3;
}

.surface-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  font-size: 12px;
  color: #909399;
}

.areas-count {
  display: flex;
  align-items: center;
  gap: 4px;
}

.surface-detail {
  margin-top: 20px;
}

.detail-content {
  display: flex;
  gap: 20px;
}

.detail-image {
  flex: 0 0 300px;
}

.detail-surface-image {
  width: 100%;
  height: auto;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

.detail-info {
  flex: 1;
}

.areas-list {
  margin-top: 20px;
}

.areas-list h4 {
  margin: 0 0 12px 0;
  color: #303133;
}

.path-data-preview {
  display: inline-block;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
