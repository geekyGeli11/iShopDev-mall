<template>
  <div class="surface-manager">
    <!-- 模板面列表 -->
    <div v-if="surfaceList.length > 0" class="surface-list">
      <div 
        v-for="(surface, index) in surfaceList" 
        :key="surface.id || index"
        class="surface-item"
        :class="{ 'active': selectedSurfaceIndex === index }"
        @click="selectSurface(index)">
        <div class="surface-preview">
          <img v-if="surface.exampleImage" :src="surface.exampleImage" alt="示例图">
          <div v-else class="no-image">无图片</div>
        </div>
        <div class="surface-info">
          <div class="surface-name">{{ surface.name }}</div>
          <div class="surface-actions">
            <el-button type="text" size="mini" @click.stop="editSurface(index)">编辑</el-button>
            <el-button type="text" size="mini" @click.stop="deleteSurface(index)">删除</el-button>
            <el-button 
              type="text" 
              size="mini" 
              @click.stop="manageDiyAreas(index)"
              :disabled="!surface.id">
              DIY区域
            </el-button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 空状态 -->
    <div v-else class="empty-state">
      <i class="el-icon-picture-outline"></i>
      <p>暂无模板面，请先添加模板面</p>
    </div>

    <!-- DIY区域管理 -->
    <div v-if="selectedSurface && showAreaManager" class="area-manager">
      <area-drawing-tool
        ref="areaDrawingTool"
        :surface="selectedSurface"
        @areas-change="handleAreasChange">
      </area-drawing-tool>
    </div>

    <!-- 添加/编辑模板面对话框 -->
    <el-dialog 
      :title="surfaceDialogTitle" 
      :visible.sync="surfaceDialogVisible" 
      width="600px"
      @close="resetSurfaceForm">
      <el-form :model="surfaceForm" :rules="surfaceRules" ref="surfaceForm" label-width="100px">
        <el-form-item label="面名称：" prop="name">
          <el-input v-model="surfaceForm.name" placeholder="如：正面、背面、侧面等"></el-input>
        </el-form-item>
        <el-form-item label="样机图片：" prop="exampleImage">
          <single-upload
            v-model="surfaceForm.exampleImage"
            :custom-validate="validateExampleImage"
            upload-tip="请上传至少 2M 的高清样机图片，支持 JPG/PNG/GIF 格式">
          </single-upload>
        </el-form-item>
        <el-form-item label="排序：">
          <el-input-number v-model="surfaceForm.sort" :min="0" :max="999"></el-input-number>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="surfaceDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveSurface" :loading="surfaceSaveLoading">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { fetchSurfaceList, createSurface, updateSurface, deleteSurface } from '@/api/diyTemplate'
import SingleUpload from '@/components/Upload/singleUpload'
import AreaDrawingTool from './AreaDrawingTool'

const defaultSurfaceForm = {
  name: '',
  exampleImage: '',
  sort: 0
}

export default {
  name: 'SurfaceManager',
  components: {
    SingleUpload,
    AreaDrawingTool
  },
  props: {
    templateId: {
      type: [Number, String],
      default: null
    },
    surfaces: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      surfaceList: [],
      selectedSurfaceIndex: -1,
      showAreaManager: false,
      surfaceDialogVisible: false,
      surfaceForm: Object.assign({}, defaultSurfaceForm),
      surfaceSaveLoading: false,
      editingSurfaceIndex: -1,
      surfaceRules: {
        name: [
          { required: true, message: '请输入面名称', trigger: 'blur' },
          { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' }
        ],
        exampleImage: [
          { required: true, message: '请上传示例图片', trigger: 'change' }
        ]
      }
    }
  },
  computed: {
    selectedSurface() {
      return this.selectedSurfaceIndex >= 0 ? this.surfaceList[this.selectedSurfaceIndex] : null
    },
    surfaceDialogTitle() {
      return this.editingSurfaceIndex >= 0 ? '编辑模板面' : '添加模板面'
    }
  },
  watch: {
    templateId: {
      handler(newVal) {
        if (newVal) {
          this.loadSurfaces()
        }
      },
      immediate: true
    },
    surfaces: {
      handler(newVal) {
        this.surfaceList = [...newVal]
      },
      immediate: true
    }
  },
  methods: {
    // 验证样机图片大小（至少 2M）
    validateExampleImage(file) {
      const minSize = 2 * 1024 * 1024 // 2MB
      if (file.size < minSize) {
        return {
          valid: false,
          message: '样机图片至少需要 2M 以上，以确保图片清晰度'
        }
      }
      return {
        valid: true,
        message: ''
      }
    },
    loadSurfaces() {
      if (!this.templateId) return

      fetchSurfaceList(this.templateId).then(response => {
        this.surfaceList = response.data || []
        this.emitChange()
      })
    },
    selectSurface(index) {
      this.selectedSurfaceIndex = index
      this.showAreaManager = false
    },
    showAddDialog() {
      this.editingSurfaceIndex = -1
      this.surfaceForm = Object.assign({}, defaultSurfaceForm)
      this.surfaceDialogVisible = true
    },
    editSurface(index) {
      this.editingSurfaceIndex = index
      const surface = this.surfaceList[index]
      this.surfaceForm = {
        name: surface.name,
        exampleImage: surface.exampleImage,
        sort: surface.sort || 0
      }
      this.surfaceDialogVisible = true
    },
    deleteSurface(index) {
      this.$confirm('确认删除该模板面吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const surface = this.surfaceList[index]
        if (surface.id) {
          deleteSurface(surface.id).then(() => {
            this.surfaceList.splice(index, 1)
            this.emitChange()
            this.$message.success('删除成功')
          })
        } else {
          this.surfaceList.splice(index, 1)
          this.emitChange()
          this.$message.success('删除成功')
        }
      })
    },
    manageDiyAreas(index) {
      this.selectedSurfaceIndex = index
      this.showAreaManager = true
    },
    saveSurface() {
      this.$refs.surfaceForm.validate(valid => {
        if (valid) {
          this.surfaceSaveLoading = true
          
          const surfaceData = {
            ...this.surfaceForm,
            templateId: this.templateId
          }
          
          if (this.editingSurfaceIndex >= 0) {
            // 编辑模式
            const surface = this.surfaceList[this.editingSurfaceIndex]
            if (surface.id) {
              updateSurface(surface.id, surfaceData).then(() => {
                Object.assign(this.surfaceList[this.editingSurfaceIndex], surfaceData)
                this.surfaceDialogVisible = false
                this.surfaceSaveLoading = false
                this.emitChange()
                this.$message.success('更新成功')
              }).catch(() => {
                this.surfaceSaveLoading = false
              })
            } else {
              Object.assign(this.surfaceList[this.editingSurfaceIndex], surfaceData)
              this.surfaceDialogVisible = false
              this.surfaceSaveLoading = false
              this.emitChange()
            }
          } else {
            // 新增模式
            if (this.templateId) {
              createSurface(surfaceData).then(response => {
                this.surfaceList.push({
                  ...surfaceData,
                  id: response.data.id
                })
                this.surfaceDialogVisible = false
                this.surfaceSaveLoading = false
                this.emitChange()
                this.$message.success('添加成功')
              }).catch(() => {
                this.surfaceSaveLoading = false
              })
            } else {
              // 模板还未创建，先添加到本地列表
              this.surfaceList.push(surfaceData)
              this.surfaceDialogVisible = false
              this.surfaceSaveLoading = false
              this.emitChange()
            }
          }
        }
      })
    },
    handleAreasChange(areas) {
      // 处理DIY区域变化
      if (this.selectedSurface) {
        this.selectedSurface.diyAreas = areas
        this.emitChange()
      }
    },
    resetSurfaceForm() {
      this.$refs.surfaceForm && this.$refs.surfaceForm.resetFields()
      this.surfaceForm = Object.assign({}, defaultSurfaceForm)
    },
    reset() {
      this.surfaceList = []
      this.selectedSurfaceIndex = -1
      this.showAreaManager = false
    },
    emitChange() {
      this.$emit('surface-change', this.surfaceList)
    }
  }
}
</script>

<style scoped>
.surface-manager {
  min-height: 200px;
}

.surface-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.surface-item {
  width: 200px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.surface-item:hover,
.surface-item.active {
  border-color: #409eff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.surface-preview {
  width: 100%;
  height: 120px;
  border: 1px dashed #dcdfe6;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8px;
  overflow: hidden;
}

.surface-preview img {
  max-width: 100%;
  max-height: 100%;
  object-fit: cover;
}

.no-image {
  color: #c0c4cc;
  font-size: 14px;
}

.surface-info {
  text-align: center;
}

.surface-name {
  font-weight: 500;
  margin-bottom: 8px;
  color: #303133;
}

.surface-actions {
  display: flex;
  justify-content: space-around;
}

.empty-state {
  text-align: center;
  padding: 40px 0;
  color: #909399;
}

.empty-state i {
  font-size: 48px;
  margin-bottom: 16px;
  display: block;
}

.area-manager {
  margin-top: 20px;
  border-top: 1px solid #ebeef5;
  padding-top: 20px;
}
</style>
