<template>
  <div class="app-container">
    <div class="preview-container">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <i class="el-icon-loading" style="font-size: 24px;"></i>
        <p>正在加载模板数据...</p>
      </div>

      <!-- 错误状态 -->
      <div v-else-if="error" class="error-container">
        <el-alert
          title="加载失败"
          :description="error"
          type="error"
          show-icon
          :closable="false"
        />
        <div class="error-actions">
          <el-button type="primary" @click="loadTemplate">重新加载</el-button>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </div>

      <!-- 模板预览 -->
      <template-preview
        v-else-if="template"
        :template="template"
        :category-options="categoryOptions"
        @refresh="loadTemplate"
        @edit-surface="handleEditSurface"
      />

      <!-- 空状态 -->
      <div v-else class="empty-container">
        <div class="empty-content">
          <i class="el-icon-document" style="font-size: 64px; color: #c0c4cc;"></i>
          <p>未找到模板数据</p>
          <el-button type="primary" @click="$router.back()">返回列表</el-button>
        </div>
      </div>
    </div>

    <!-- 面编辑对话框 -->
    <el-dialog 
      title="编辑模板面" 
      :visible.sync="editSurfaceDialogVisible" 
      width="800px"
      :close-on-click-modal="false"
    >
      <surface-manager
        v-if="editingSurface"
        :surface="editingSurface"
        :template-id="template.id"
        @save="handleSurfaceSave"
        @cancel="handleSurfaceCancel"
      />
    </el-dialog>
  </div>
</template>

<script>
import { getTemplateDetail } from '@/api/diyTemplate'
import { fetchListWithChildren } from '@/api/productCate'
import TemplatePreview from './components/TemplatePreview'
import SurfaceManager from './components/SurfaceManager'

export default {
  name: 'DiyTemplatePreview',
  components: {
    TemplatePreview,
    SurfaceManager
  },
  data() {
    return {
      template: null,
      categoryOptions: [],
      loading: false,
      error: null,
      editSurfaceDialogVisible: false,
      editingSurface: null
    }
  },
  created() {
    this.init()
  },
  methods: {
    async init() {
      await this.getCategoryList()
      if (this.$route.params.id || this.$route.query.id) {
        const templateId = this.$route.params.id || this.$route.query.id
        await this.loadTemplate(templateId)
      } else {
        this.error = '缺少模板ID参数'
      }
    },

    async loadTemplate(templateId) {
      if (!templateId && this.template) {
        templateId = this.template.id
      }
      
      if (!templateId) {
        this.error = '缺少模板ID参数'
        return
      }

      this.loading = true
      this.error = null

      try {
        const response = await getTemplateDetail(templateId)
        this.template = response.data
        
        // 确保每个面都有areas数组
        if (this.template.surfaces) {
          this.template.surfaces.forEach(surface => {
            if (!surface.areas) {
              surface.areas = []
            }
          })
        }
        
        console.log('模板数据加载完成:', this.template)
      } catch (error) {
        console.error('加载模板失败:', error)
        this.error = error.message || '加载模板数据失败，请稍后重试'
      } finally {
        this.loading = false
      }
    },

    async getCategoryList() {
      try {
        const response = await fetchListWithChildren()
        let list = response.data;
        this.categoryOptions = [];
        for (let i = 0; i < list.length; i++) {
          let children = [];
          if (list[i].children != null && list[i].children.length > 0) {
            for (let j = 0; j < list[i].children.length; j++) {
              children.push({
                label: list[i].children[j].name,
                value: list[i].children[j].id,
                leaf: true
              });
            }
          }
          this.categoryOptions.push({
            label: list[i].name,
            value: list[i].id,
            children: children,
            leaf: children.length === 0
          });
        }
      } catch (error) {
        console.error('获取分类列表失败:', error)
      }
    },

    handleEditSurface(surface) {
      this.editingSurface = { ...surface }
      this.editSurfaceDialogVisible = true
    },

    handleSurfaceSave(updatedSurface) {
      // 更新模板中的面数据
      const index = this.template.surfaces.findIndex(s => s.id === updatedSurface.id)
      if (index !== -1) {
        this.$set(this.template.surfaces, index, updatedSurface)
      }
      this.editSurfaceDialogVisible = false
      this.editingSurface = null
      this.$message.success('模板面更新成功')
    },

    handleSurfaceCancel() {
      this.editSurfaceDialogVisible = false
      this.editingSurface = null
    }
  },

  // 路由守卫：离开页面前确认
  beforeRouteLeave(to, from, next) {
    if (this.editSurfaceDialogVisible) {
      this.$confirm('正在编辑模板面，确定要离开吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        next()
      }).catch(() => {
        next(false)
      })
    } else {
      next()
    }
  }
}
</script>

<style scoped>
.app-container {
  padding: 0;
}

.preview-container {
  min-height: calc(100vh - 84px);
  background: #f0f2f5;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400px;
  color: #606266;
}

.loading-container p {
  margin-top: 16px;
  font-size: 14px;
}

.error-container {
  padding: 40px;
  text-align: center;
}

.error-actions {
  margin-top: 20px;
}

.empty-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 400px;
}

.empty-content {
  text-align: center;
}

.empty-content p {
  margin: 16px 0;
  color: #606266;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .preview-container {
    padding: 10px;
  }
}
</style>
