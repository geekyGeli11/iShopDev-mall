<template>
  <div class="app-container">
    <el-card class="box-card" shadow="never">
      <div slot="header" class="clearfix">
        <span>模板详情</span>
        <div style="float: right;">
          <el-button type="primary" size="mini" @click="handlePreview" style="margin-right: 10px;">
            预览模板
          </el-button>
          <el-button type="text" @click="$router.back()">返回</el-button>
        </div>
      </div>
      
      <div v-if="template">
        <el-descriptions title="基本信息" :column="2" border>
          <el-descriptions-item label="模板名称">{{ template.name }}</el-descriptions-item>
          <el-descriptions-item label="适用分类">{{ getCategoryName(template.productCategoryId) }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="template.status === 1 ? 'success' : 'danger'">
              {{ template.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ $formatDate(template.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="描述" :span="2">{{ template.description || '无' }}</el-descriptions-item>
        </el-descriptions>

        <div style="margin-top: 20px;">
          <h3>模板面列表</h3>
          <el-table :data="template.surfaces" border style="width: 100%">
            <el-table-column prop="name" label="面名称" width="200"/>
            <el-table-column prop="sort" label="排序" width="100"/>
            <el-table-column label="示例图" width="120">
              <template slot-scope="{row}">
                <img v-if="row.exampleImage" :src="row.exampleImage" style="height: 60px; width: 60px">
              </template>
            </el-table-column>
            <el-table-column label="DIY区域" min-width="300">
              <template slot-scope="{row}">
                <div v-if="row.areas && row.areas.length > 0">
                  <el-tag
                    v-for="area in row.areas"
                    :key="area.id"
                    style="margin-right: 5px; margin-bottom: 5px;"
                    size="small"
                  >
                    {{ area.name || `区域${area.id}` }}
                  </el-tag>
                </div>
                <span v-else style="color: #999;">暂无DIY区域</span>
              </template>
            </el-table-column>
            <el-table-column label="创建时间" width="160">
              <template slot-scope="{row}">
                {{ $formatDate(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template slot-scope="{row}">
                <el-button type="primary" size="mini" @click="showAreaDetail(row)">
                  查看区域
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-card>

    <!-- 区域详情对话框 -->
    <el-dialog title="DIY区域详情" :visible.sync="areaDialogVisible" width="800px">
      <div v-if="selectedSurface">
        <h4>{{ selectedSurface.name }} - DIY区域列表</h4>
        <el-table :data="selectedSurface.areas" border style="width: 100%">
          <el-table-column prop="id" label="区域ID" width="80"/>
          <el-table-column prop="name" label="区域名称" width="150"/>
          <el-table-column label="路径数据" min-width="200">
            <template slot-scope="{row}">
              <el-tooltip :content="row.pathData" placement="top">
                <span style="display: inline-block; max-width: 200px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                  {{ row.pathData }}
                </span>
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
import { getTemplateDetail } from '@/api/diyTemplate'
import { fetchList as fetchProductCategoryList } from '@/api/productCate'

export default {
  name: 'DiyTemplateDetail',
  data() {
    return {
      template: null,
      categoryOptions: [],
      areaDialogVisible: false,
      selectedSurface: null
    }
  },
  created() {
    if (this.$route.query.id != null) {
      this.getTemplateDetail(this.$route.query.id)
    }
    this.getCategoryList()
  },
  methods: {
    getTemplateDetail(id) {
      getTemplateDetail(id).then(response => {
        this.template = response.data
      })
    },
    getCategoryList() {
      fetchProductCategoryList(0, { pageNum: 1, pageSize: 100 }).then(response => {
        this.categoryOptions = response.data.list.map(item => ({
          label: item.name,
          value: item.id
        }))
      })
    },
    getCategoryName(categoryId) {
      if (!categoryId) return '全部分类'
      const category = this.categoryOptions.find(item => item.value === categoryId)
      return category ? category.label : '未知分类'
    },
    showAreaDetail(surface) {
      this.selectedSurface = surface
      this.areaDialogVisible = true
    },
    handlePreview() {
      if (this.template && this.template.id) {
        this.$router.push({ path: `/diy/templatePreview/${this.template.id}` })
      }
    }
  }
}
</script>

<style scoped>
.box-card {
  margin: 20px;
}
</style>
