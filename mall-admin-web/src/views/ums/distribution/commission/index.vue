<template>
  <div class="app-container">
    <!-- 筛选搜索区域 -->
    <el-card class="filter-container" shadow="never">
      <div>
        <i class="el-icon-search"></i>
        <span>筛选搜索</span>
        <el-button
          style="float:right"
          type="primary"
          @click="handleSearchList()"
          size="small">
          查询搜索
        </el-button>
        <el-button
          style="float:right;margin-right: 15px"
          @click="handleResetSearch()"
          size="small">
          重置
        </el-button>
      </div>
      <div style="margin-top: 15px">
        <el-form :inline="true" :model="listQuery" size="small" label-width="120px">
          <el-form-item label="配置名称：">
            <el-input 
              v-model="listQuery.configName" 
              class="input-width" 
              placeholder="请输入配置名称" 
              clearable>
            </el-input>
          </el-form-item>
          <el-form-item label="商品分类：">
            <el-select v-model="listQuery.productCategoryId" placeholder="全部" clearable class="input-width">
              <el-option 
                v-for="item in categoryOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="配置状态：">
            <el-select v-model="listQuery.isActive" placeholder="全部" clearable class="input-width">
              <el-option label="启用" :value="1"></el-option>
              <el-option label="禁用" :value="0"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="佣金类型：">
            <el-select v-model="listQuery.commissionType" placeholder="全部" clearable class="input-width">
              <el-option label="按比例" :value="1"></el-option>
              <el-option label="固定金额" :value="2"></el-option>
              <el-option label="比例+固定" :value="3"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 操作区域 -->
    <el-card class="operate-container" shadow="never">
      <i class="el-icon-tickets"></i>
      <span>配置列表</span>
      <el-button 
        size="mini" 
        class="btn-add" 
        @click="handleAdd()" 
        style="margin-left: 20px">
        新增配置
      </el-button>
      <el-button 
        size="mini" 
        class="btn-add" 
        @click="handleBatchEnable()"
        :disabled="multipleSelection.length === 0">
        批量启用
      </el-button>
      <el-button 
        size="mini" 
        class="btn-add" 
        @click="handleBatchDisable()"
        :disabled="multipleSelection.length === 0">
        批量禁用
      </el-button>
      <el-button 
        size="mini" 
        class="btn-add" 
        @click="handleRefreshStats()">
        刷新统计
      </el-button>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="statistic-card">
            <div class="statistic-value">{{ statistics.totalCount || 0 }}</div>
            <div class="statistic-label">总配置数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="statistic-card">
            <div class="statistic-value">{{ statistics.activeCount || 0 }}</div>
            <div class="statistic-label">生效配置</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="statistic-card">
            <div class="statistic-value">{{ statistics.categoryConfigCount || 0 }}</div>
            <div class="statistic-label">分类配置</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="statistic-card">
            <div class="statistic-value">{{ statistics.globalConfigCount || 0 }}</div>
            <div class="statistic-label">全局配置</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数据列表 -->
    <div class="table-container">
      <el-table 
        ref="configTable"
        :data="list"
        style="width: 100%;"
        v-loading="listLoading" 
        border
        @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column label="配置ID" width="80" align="center">
          <template slot-scope="scope">{{ scope.row.id }}</template>
        </el-table-column>
        <el-table-column label="配置名称" width="150" align="center">
          <template slot-scope="scope">{{ scope.row.configName || '-' }}</template>
        </el-table-column>
        <el-table-column label="应用范围" width="120" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.productCategoryId ? 'warning' : 'success'" size="small">
              {{ scope.row.productCategoryId ? '指定分类' : '全局配置' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="佣金类型" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getCommissionTypeTag(scope.row.commissionType)" size="small">
              {{ getCommissionTypeText(scope.row.commissionType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="一级佣金" width="120" align="center">
          <template slot-scope="scope">
            <span v-if="scope.row.commissionType === 1 || scope.row.commissionType === 3">
              {{ (scope.row.level1Rate * 100).toFixed(2) }}%
            </span>
            <span v-if="scope.row.commissionType === 2 || scope.row.commissionType === 3">
              {{ scope.row.commissionType === 3 ? ' + ' : '' }}￥{{ scope.row.level1Amount || '0.00' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="二级佣金" width="120" align="center">
          <template slot-scope="scope">
            <span v-if="scope.row.commissionType === 1 || scope.row.commissionType === 3">
              {{ (scope.row.level2Rate * 100).toFixed(2) }}%
            </span>
            <span v-if="scope.row.commissionType === 2 || scope.row.commissionType === 3">
              {{ scope.row.commissionType === 3 ? ' + ' : '' }}￥{{ scope.row.level2Amount || '0.00' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="最小订单金额" width="120" align="center">
          <template slot-scope="scope">￥{{ scope.row.minOrderAmount || '0.00' }}</template>
        </el-table-column>
        <el-table-column label="最大佣金" width="100" align="center">
          <template slot-scope="scope">
            <span v-if="scope.row.maxCommission">￥{{ scope.row.maxCommission }}</span>
            <span v-else>无限制</span>
          </template>
        </el-table-column>
        <el-table-column label="生效时间" width="160" align="center">
          <template slot-scope="scope">
            <div v-if="scope.row.startTime">{{ scope.row.startTime | formatDate }}</div>
            <div v-else>立即生效</div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-switch
              @change="handleStatusChange(scope.row)"
              :active-value="1"
              :inactive-value="0"
              v-model="scope.row.isActive">
            </el-switch>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button 
              size="mini"
              type="text"
              @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button 
              size="mini"
              type="text"
              @click="handleViewDetail(scope.row)">
              查看详情
            </el-button>
            <el-button 
              size="mini"
              type="text"
              style="color: #f56c6c"
              @click="handleDelete(scope.row.id)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        layout="total, sizes,prev, pager, next,jumper"
        :current-page.sync="listQuery.pageNum"
        :page-size="listQuery.pageSize"
        :page-sizes="[10,20,50,100]"
        :total="total">
      </el-pagination>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      :title="isEdit ? '编辑配置' : '新增配置'"
      :visible.sync="formDialogVisible"
      width="800px">
      <el-form :model="configForm" :rules="rules" ref="configForm" label-width="120px" size="small">
        <el-row>
          <el-col :span="12">
            <el-form-item label="配置名称：" prop="configName">
              <el-input v-model="configForm.configName" placeholder="请输入配置名称"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品分类：" prop="productCategoryId">
              <el-select v-model="configForm.productCategoryId" placeholder="全部分类（全局配置）" clearable style="width: 100%">
                <el-option 
                  v-for="item in categoryOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="佣金类型：" prop="commissionType">
              <el-radio-group v-model="configForm.commissionType">
                <el-radio :label="1">按比例</el-radio>
                <el-radio :label="2">固定金额</el-radio>
                <el-radio :label="3">比例+固定</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最小订单金额：">
              <el-input-number v-model="configForm.minOrderAmount" :min="0" :precision="2" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        
        <!-- 按比例 -->
        <el-row v-if="configForm.commissionType === 1 || configForm.commissionType === 3">
          <el-col :span="12">
            <el-form-item label="一级佣金比例：" prop="level1Rate">
              <el-input-number 
                v-model="configForm.level1Rate" 
                :min="0" 
                :max="1" 
                :precision="4" 
                style="width: 100%">
              </el-input-number>
              <span style="color: #999; font-size: 12px;">（0-1之间，如0.05表示5%）</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="二级佣金比例：" prop="level2Rate">
              <el-input-number 
                v-model="configForm.level2Rate" 
                :min="0" 
                :max="1" 
                :precision="4" 
                style="width: 100%">
              </el-input-number>
              <span style="color: #999; font-size: 12px;">（0-1之间，如0.02表示2%）</span>
            </el-form-item>
          </el-col>
        </el-row>
        
        <!-- 固定金额 -->
        <el-row v-if="configForm.commissionType === 2 || configForm.commissionType === 3">
          <el-col :span="12">
            <el-form-item label="一级固定佣金：">
              <el-input-number v-model="configForm.level1Amount" :min="0" :precision="2" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="二级固定佣金：">
              <el-input-number v-model="configForm.level2Amount" :min="0" :precision="2" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row>
          <el-col :span="12">
            <el-form-item label="单笔最大佣金：">
              <el-input-number v-model="configForm.maxCommission" :min="0" :precision="2" style="width: 100%"></el-input-number>
              <span style="color: #999; font-size: 12px;">（留空表示无限制）</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否启用：">
              <el-radio-group v-model="configForm.isActive">
                <el-radio :label="1">启用</el-radio>
                <el-radio :label="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row>
          <el-col :span="12">
            <el-form-item label="生效开始时间：">
              <el-date-picker
                v-model="configForm.startTime"
                type="datetime"
                placeholder="立即生效"
                value-format="yyyy-MM-dd HH:mm:ss"
                style="width: 100%">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="生效结束时间：">
              <el-date-picker
                v-model="configForm.endTime"
                type="datetime"
                placeholder="永不过期"
                value-format="yyyy-MM-dd HH:mm:ss"
                style="width: 100%">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="formDialogVisible = false" size="small">取 消</el-button>
        <el-button type="primary" @click="handleSubmit()" size="small">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog
      title="配置详情"
      :visible.sync="detailDialogVisible"
      width="600px">
      <el-form :model="currentConfig" label-width="120px" size="small">
        <el-form-item label="配置名称：">
          <span>{{ currentConfig.configName }}</span>
        </el-form-item>
        <el-form-item label="应用范围：">
          <el-tag :type="currentConfig.productCategoryId ? 'warning' : 'success'" size="small">
            {{ currentConfig.productCategoryId ? '指定分类' : '全局配置' }}
          </el-tag>
        </el-form-item>
        <el-form-item label="佣金类型：">
          <el-tag :type="getCommissionTypeTag(currentConfig.commissionType)" size="small">
            {{ getCommissionTypeText(currentConfig.commissionType) }}
          </el-tag>
        </el-form-item>
        <el-form-item label="一级佣金：">
          <span v-if="currentConfig.commissionType === 1 || currentConfig.commissionType === 3">
            {{ (currentConfig.level1Rate * 100).toFixed(2) }}%
          </span>
          <span v-if="currentConfig.commissionType === 2 || currentConfig.commissionType === 3">
            {{ currentConfig.commissionType === 3 ? ' + ' : '' }}￥{{ currentConfig.level1Amount || '0.00' }}
          </span>
        </el-form-item>
        <el-form-item label="二级佣金：">
          <span v-if="currentConfig.commissionType === 1 || currentConfig.commissionType === 3">
            {{ (currentConfig.level2Rate * 100).toFixed(2) }}%
          </span>
          <span v-if="currentConfig.commissionType === 2 || currentConfig.commissionType === 3">
            {{ currentConfig.commissionType === 3 ? ' + ' : '' }}￥{{ currentConfig.level2Amount || '0.00' }}
          </span>
        </el-form-item>
        <el-form-item label="最小订单金额：">
          <span>￥{{ currentConfig.minOrderAmount || '0.00' }}</span>
        </el-form-item>
        <el-form-item label="单笔最大佣金：">
          <span v-if="currentConfig.maxCommission">￥{{ currentConfig.maxCommission }}</span>
          <span v-else>无限制</span>
        </el-form-item>
        <el-form-item label="生效时间：">
          <span>{{ currentConfig.startTime || '立即生效' }} ~ {{ currentConfig.endTime || '永不过期' }}</span>
        </el-form-item>
        <el-form-item label="创建时间：">
          <span>{{ currentConfig.createdAt | formatDateTime }}</span>
        </el-form-item>
        <el-form-item label="更新时间：">
          <span>{{ currentConfig.updatedAt | formatDateTime }}</span>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false" size="small">关 闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { 
  fetchList, 
  getDetail, 
  createConfig, 
  updateConfig, 
  deleteConfig, 
  updateStatus, 
  batchUpdateStatus, 
  getStatistics, 
  checkConfigName 
} from '@/api/commissionConfig'
import { formatDate } from '@/utils/date'

const defaultListQuery = {
  pageNum: 1,
  pageSize: 20,
  configName: null,
  productCategoryId: null,
  isActive: null,
  commissionType: null
}

const defaultConfigForm = {
  configName: '',
  productCategoryId: null,
  level1Rate: 0.05,
  level2Rate: 0.02,
  level1Amount: null,
  level2Amount: null,
  commissionType: 1,
  minOrderAmount: 0,
  maxCommission: null,
  isActive: 1,
  startTime: null,
  endTime: null
}

export default {
  name: 'commissionConfigList',
  data() {
    return {
      listQuery: Object.assign({}, defaultListQuery),
      list: null,
      total: null,
      listLoading: false,
      multipleSelection: [],
      statistics: {},
      categoryOptions: [
        { value: 1, label: '食品生鲜' },
        { value: 2, label: '数码家电' },
        { value: 3, label: '服装鞋帽' },
        { value: 4, label: '家居用品' },
        { value: 5, label: '美妆个护' }
      ],
      
      // 表单弹窗
      formDialogVisible: false,
      isEdit: false,
      configForm: Object.assign({}, defaultConfigForm),
      
      // 详情弹窗
      detailDialogVisible: false,
      currentConfig: {},
      
      // 表单验证规则
      rules: {
        configName: [
          { required: true, message: '请输入配置名称', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        commissionType: [
          { required: true, message: '请选择佣金类型', trigger: 'change' }
        ],
        level1Rate: [
          { required: true, message: '请输入一级佣金比例', trigger: 'blur' }
        ],
        level2Rate: [
          { required: true, message: '请输入二级佣金比例', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
    this.getStatisticsData()
  },
  filters: {
    formatDateTime(time) {
      if (time == null || time === '') {
        return '-'
      }
      let date = new Date(time)
      return formatDate(date, 'yyyy-MM-dd hh:mm:ss')
    },
    formatDate(time) {
      if (time == null || time === '') {
        return '-'
      }
      let date = new Date(time)
      return formatDate(date, 'yyyy-MM-dd')
    }
  },
  methods: {
    handleResetSearch() {
      this.listQuery = Object.assign({}, defaultListQuery)
    },
    handleSearchList() {
      this.listQuery.pageNum = 1
      this.getList()
    },
    handleSizeChange(val) {
      this.listQuery.pageNum = 1
      this.listQuery.pageSize = val
      this.getList()
    },
    handleCurrentChange(val) {
      this.listQuery.pageNum = val
      this.getList()
    },
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    handleAdd() {
      this.isEdit = false
      this.configForm = Object.assign({}, defaultConfigForm)
      this.formDialogVisible = true
    },
    handleEdit(row) {
      this.isEdit = true
      this.configForm = Object.assign({}, row)
      this.formDialogVisible = true
    },
    handleViewDetail(row) {
      this.currentConfig = row
      this.detailDialogVisible = true
    },
    handleDelete(id) {
      this.$confirm('此操作将永久删除该配置, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteConfig(id).then(response => {
          this.$message.success('删除成功')
          this.getList()
        })
      })
    },
    handleStatusChange(row) {
      updateStatus(row.id, row.isActive).then(response => {
        this.$message.success('状态修改成功')
      }).catch(() => {
        row.isActive = row.isActive === 1 ? 0 : 1
      })
    },
    handleBatchEnable() {
      this.handleBatchStatus(1)
    },
    handleBatchDisable() {
      this.handleBatchStatus(0)
    },
    handleBatchStatus(status) {
      if (this.multipleSelection.length === 0) {
        this.$message.warning('请选择要操作的配置')
        return
      }
      
      const statusText = status === 1 ? '启用' : '禁用'
      this.$confirm(`确定要批量${statusText}选中的配置吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const ids = this.multipleSelection.map(item => item.id).join(',')
        batchUpdateStatus(ids, status).then(response => {
          this.$message.success(`批量${statusText}成功`)
          this.getList()
        })
      })
    },
    handleSubmit() {
      this.$refs.configForm.validate((valid) => {
        if (valid) {
          if (this.isEdit) {
            updateConfig(this.configForm).then(response => {
              this.$message.success('更新成功')
              this.formDialogVisible = false
              this.getList()
            })
          } else {
            createConfig(this.configForm).then(response => {
              this.$message.success('创建成功')
              this.formDialogVisible = false
              this.getList()
            })
          }
        }
      })
    },
    handleRefreshStats() {
      this.getStatisticsData()
      this.$message.success('统计数据已刷新')
    },
    getList() {
      this.listLoading = true
      fetchList(this.listQuery).then(response => {
        this.list = response.data.list
        this.total = parseInt(response.data.total)
        this.listLoading = false
      })
    },
    getStatisticsData() {
      getStatistics().then(response => {
        this.statistics = response.data
      })
    },
    getCommissionTypeText(type) {
      const typeMap = {
        1: '按比例',
        2: '固定金额',
        3: '比例+固定'
      }
      return typeMap[type] || '未知'
    },
    getCommissionTypeTag(type) {
      const tagMap = {
        1: 'success',
        2: 'warning',
        3: 'danger'
      }
      return tagMap[type] || 'info'
    }
  }
}
</script>

<style scoped>
.input-width {
  width: 200px;
}

.statistic-card {
  text-align: center;
  padding: 10px;
}

.statistic-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 5px;
}

.statistic-label {
  font-size: 14px;
  color: #666;
}
</style> 