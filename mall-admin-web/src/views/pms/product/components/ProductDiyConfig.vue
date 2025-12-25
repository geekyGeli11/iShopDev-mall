<template>
  <div class="diy-config-container">
    <div class="section-title">
      <i class="el-icon-edit"></i> DIY定制配置
    </div>
    
    <el-form :model="diyConfig" :rules="rules" ref="diyConfigForm" label-width="120px" size="small">
      <el-form-item label="支持DIY定制：">
        <el-switch
          v-model="diyConfig.diyEnabled"
          :active-value="1"
          :inactive-value="0"
          active-text="支持"
          inactive-text="不支持"
          @change="handleDiyEnabledChange">
        </el-switch>
        <span style="margin-left: 10px; color: #909399; font-size: 12px;">
          启用后用户可以对此商品进行个性化定制
        </span>
      </el-form-item>
      
      <el-form-item v-if="diyConfig.diyEnabled === 1" label="DIY模板：" prop="diyTemplateId">
        <el-select
          v-model="diyConfig.diyTemplateId"
          placeholder="请选择DIY模板"
          clearable
          style="width: 300px"
          @change="handleTemplateChange">
          <el-option
            v-for="item in diyTemplateOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value">
            <span>{{ item.label }}</span>
            <span style="float: right; color: #8492a6; font-size: 12px">
              {{ item.description || '无描述' }}
            </span>
          </el-option>
        </el-select>
        <span style="margin-left: 10px; color: #909399; font-size: 12px;">
          选择适合此商品的DIY模板
        </span>
      </el-form-item>
      
      <!-- 模板预览 -->
      <el-form-item v-if="diyConfig.diyEnabled === 1 && selectedTemplate" label="模板预览：">
        <div class="template-preview">
          <div class="template-info">
            <h4>{{ selectedTemplate.name }}</h4>
            <p>{{ selectedTemplate.description || '暂无描述' }}</p>
            <p>适用分类：{{ selectedTemplate.productCategoryName || '通用' }}</p>
          </div>
          <el-button 
            type="text" 
            @click="viewTemplateDetail"
            v-if="selectedTemplate.id">
            查看模板详情
          </el-button>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { fetchTemplateList, getTemplate } from '@/api/diyTemplate'

export default {
  name: 'ProductDiyConfig',
  props: {
    value: {
      type: Object,
      default: () => ({
        diyEnabled: 0,
        diyTemplateId: null
      })
    }
  },
  data() {
    return {
      diyConfig: {
        diyEnabled: 0,
        diyTemplateId: null
      },
      diyTemplateOptions: [],
      selectedTemplate: null,
      rules: {
        diyTemplateId: [
          {
            validator: (rule, value, callback) => {
              if (this.diyConfig.diyEnabled === 1 && !value) {
                callback(new Error('启用DIY时必须选择模板'));
              } else {
                callback();
              }
            },
            trigger: 'change'
          }
        ]
      }
    }
  },
  watch: {
    value: {
      handler(newVal) {
        if (newVal) {
          this.diyConfig = {
            diyEnabled: newVal.diyEnabled || 0,
            diyTemplateId: newVal.diyTemplateId || null
          }
          if (this.diyConfig.diyTemplateId) {
            this.loadTemplateDetail(this.diyConfig.diyTemplateId)
          }
        }
      },
      immediate: true,
      deep: true
    },
    diyConfig: {
      handler(newVal) {
        this.$emit('input', newVal)
      },
      deep: true
    }
  },
  created() {
    this.getDiyTemplateList()
  },
  methods: {
    getDiyTemplateList() {
      fetchTemplateList({ pageNum: 1, pageSize: 100, status: 1 }).then(response => {
        this.diyTemplateOptions = []
        if (response.data && response.data.list) {
          let templateList = response.data.list
          for (let i = 0; i < templateList.length; i++) {
            this.diyTemplateOptions.push({
              label: templateList[i].name,
              value: templateList[i].id,
              description: templateList[i].description
            })
          }
        }
      }).catch(error => {
        console.error('获取DIY模板列表失败:', error)
        // 不显示错误消息，避免影响页面加载
        this.diyTemplateOptions = []
      })
    },
    handleDiyEnabledChange(value) {
      if (value === 0) {
        // 禁用DIY时清空模板选择
        this.diyConfig.diyTemplateId = null
        this.selectedTemplate = null
      }
    },
    handleTemplateChange(templateId) {
      if (templateId) {
        this.loadTemplateDetail(templateId)
      } else {
        this.selectedTemplate = null
      }
    },
    loadTemplateDetail(templateId) {
      if (!templateId) return
      getTemplate(templateId).then(response => {
        if (response.data) {
          this.selectedTemplate = response.data
        }
      }).catch(error => {
        console.error('获取模板详情失败:', error)
        this.selectedTemplate = null
      })
    },
    viewTemplateDetail() {
      if (this.selectedTemplate && this.selectedTemplate.id) {
        this.$router.push({
          path: '/diy/templateDetail',
          query: { id: this.selectedTemplate.id }
        })
      }
    },
    validate() {
      return new Promise((resolve) => {
        this.$refs.diyConfigForm.validate((valid) => {
          resolve(valid)
        })
      })
    }
  }
}
</script>

<style scoped>
.diy-config-container {
  margin: 20px 0;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 30px 0 20px 0;
  padding-bottom: 10px;
  border-bottom: 2px solid #409eff;
  position: relative;
}

.template-preview {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 15px;
  background-color: #fafafa;
}

.template-info h4 {
  margin: 0 0 10px 0;
  color: #303133;
}

.template-info p {
  margin: 5px 0;
  color: #606266;
  font-size: 13px;
}
</style>
