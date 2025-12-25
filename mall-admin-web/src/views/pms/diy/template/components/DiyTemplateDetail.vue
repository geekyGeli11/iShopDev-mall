<template>
  <div class="diy-template-detail">
    <!-- 基础信息表单 -->
    <el-card class="form-container" shadow="never">
      <div slot="header" class="clearfix">
        <span>基础信息</span>
      </div>
      <el-form :model="template" :rules="rules" ref="templateForm" label-width="150px">
        <el-form-item label="模板名称：" prop="name">
          <el-input v-model="template.name" placeholder="请输入模板名称"></el-input>
        </el-form-item>
        <el-form-item label="适用分类：" prop="productCategoryId">
          <el-select v-model="template.productCategoryId" placeholder="请选择商品分类" style="width: 100%">
            <el-option-group
              v-for="group in categoryOptions"
              :key="group.value"
              :label="group.label">
              <!-- 一级分类选项 -->
              <el-option
                :key="'parent-' + group.value"
                :label="group.label"
                :value="group.value">
                <span style="font-weight: bold">{{ group.label }}</span>
              </el-option>
              <!-- 二级分类选项 -->
              <el-option
                v-for="item in group.children"
                :key="item.value"
                :label="'　└ ' + item.label"
                :value="item.value">
              </el-option>
            </el-option-group>
          </el-select>
        </el-form-item>
        <el-form-item label="描述：">
          <el-input
            v-model="template.description"
            type="textarea"
            :autosize="{ minRows: 3, maxRows: 6 }"
            placeholder="请输入模板描述">
          </el-input>
        </el-form-item>
        <el-form-item label="是否启用：">
          <el-radio-group v-model="template.status">
            <el-radio :label="1">是</el-radio>
            <el-radio :label="0">否</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 模板面管理 -->
    <el-card class="form-container" shadow="never" style="margin-top: 20px;">
      <div slot="header" class="clearfix">
        <span>模板面管理</span>
        <el-button 
          style="float: right; padding: 3px 0" 
          type="text" 
          @click="addSurface"
          :disabled="!isEdit && !template.id">
          添加模板面
        </el-button>
      </div>
      <surface-manager 
        ref="surfaceManager"
        :template-id="template.id"
        :surfaces="surfaces"
        @surface-change="handleSurfaceChange">
      </surface-manager>
    </el-card>

    <!-- 操作按钮 -->
    <el-card class="form-container" shadow="never" style="margin-top: 20px;">
      <el-form>
        <el-form-item>
          <el-button type="primary" @click="onSubmit" :loading="submitLoading">
            {{ isEdit ? '更新' : '创建' }}
          </el-button>
          <el-button v-if="!isEdit" @click="resetForm">重置</el-button>
          <el-button @click="goBack">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { createTemplate, updateTemplate, getTemplate } from '@/api/diyTemplate'
import { fetchListWithChildren } from '@/api/productCate'
import SurfaceManager from './SurfaceManager'

const defaultTemplate = {
  name: '',
  productCategoryId: null,
  description: '',
  status: 1
}

export default {
  name: 'DiyTemplateDetail',
  components: {
    SurfaceManager
  },
  props: {
    isEdit: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      template: Object.assign({}, defaultTemplate),
      surfaces: [],
      categoryOptions: [],
      submitLoading: false,
      rules: {
        name: [
          { required: true, message: '请输入模板名称', trigger: 'blur' },
          { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
        ],
        productCategoryId: [
          { required: true, message: '请选择适用分类', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    this.getCategoryList()
    if (this.isEdit) {
      this.getTemplateInfo()
    }
  },
  methods: {
    getCategoryList() {
      fetchListWithChildren().then(response => {
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
      }).catch(error => {
        console.error('获取分类列表失败:', error);
        this.$message.error('获取分类列表失败');
      });
    },
    getTemplateInfo() {
      const id = this.$route.params && this.$route.params.id || this.$route.query && this.$route.query.id
      if (id) {
        getTemplate(id).then(response => {
          this.template = response.data
        })
      }
    },
    handleSurfaceChange(surfaces) {
      this.surfaces = surfaces
    },
    addSurface() {
      if (this.$refs.surfaceManager) {
        this.$refs.surfaceManager.showAddDialog()
      } else {
        this.$message.error('模板面管理组件未初始化')
      }
    },
    onSubmit() {
      this.$refs.templateForm.validate(valid => {
        if (valid) {
          this.submitLoading = true
          if (this.isEdit) {
            this.updateTemplate()
          } else {
            this.createTemplate()
          }
        }
      })
    },
    createTemplate() {
      createTemplate(this.template).then(response => {
        this.$message.success('创建成功')
        this.template.id = response.data.id
        this.submitLoading = false
        // 创建成功后可以继续编辑模板面
      }).catch(() => {
        this.submitLoading = false
      })
    },
    updateTemplate() {
      updateTemplate(this.template.id, this.template).then(response => {
        this.$message.success('更新成功')
        this.submitLoading = false
      }).catch(() => {
        this.submitLoading = false
      })
    },
    resetForm() {
      this.$refs.templateForm.resetFields()
      this.template = Object.assign({}, defaultTemplate)
      this.surfaces = []
      if (this.$refs.surfaceManager) {
        this.$refs.surfaceManager.reset()
      }
    },
    goBack() {
      this.$router.go(-1)
    }
  }
}
</script>

<style scoped>
.diy-template-detail {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

.form-container {
  margin-bottom: 20px;
  position: relative !important; /* 强制覆盖全局样式，解决遮挡问题 */
}

.form-container .el-card__header {
  background: #fff;
  border-bottom: 1px solid #ebeef5;
}

.form-container .el-card__body {
  background: #fff;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}

.clearfix:after {
  clear: both;
}

/* 确保按钮样式正确 */
.el-button--text {
  color: #409eff;
  background: transparent;
  border: none;
  padding: 0;
}

.el-button--text:hover {
  color: #66b1ff;
}

.el-button--text:disabled {
  color: #c0c4cc;
  cursor: not-allowed;
}
</style>
