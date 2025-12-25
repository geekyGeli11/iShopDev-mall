<template>
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
      <div>
        <i class="el-icon-search"></i>
        <span>筛选搜索</span>
        <el-button style="float:right" type="primary" @click="handleSearchList()" size="small">查询搜索</el-button>
        <el-button style="float:right;margin-right: 15px" @click="handleResetSearch()" size="small">重置</el-button>
      </div>
      <div style="margin-top: 15px">
        <el-form :inline="true" :model="listQuery" size="small" label-width="100px">
          <el-form-item label="报损单号：">
            <el-input v-model="listQuery.reportSn" class="input-width" placeholder="报损单号"></el-input>
          </el-form-item>
          <el-form-item label="报损门店：">
            <el-select v-model="listQuery.storeId" placeholder="全部门店" clearable class="input-width">
              <el-option v-for="item in storeList" :key="item.id" :label="item.addressName" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="报损类型：">
            <el-select v-model="listQuery.damageType" placeholder="全部类型" clearable class="input-width">
              <el-option v-for="item in damageTypeOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="处理状态：">
            <el-select v-model="listQuery.status" placeholder="全部状态" clearable class="input-width">
              <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="报损时间：">
            <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期"
              end-placeholder="结束日期" value-format="yyyy-MM-dd" class="input-width" @change="handleDateChange">
            </el-date-picker>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="operate-container" shadow="never">
      <i class="el-icon-tickets"></i>
      <span>数据列表</span>
      <el-button size="mini" type="primary" style="float:right" @click="handleAdd()">新增报损</el-button>
    </el-card>
    <div class="table-container">
      <el-table ref="damageTable" :data="list" style="width: 100%;" @selection-change="handleSelectionChange"
        v-loading="listLoading" border>
        <el-table-column type="selection" width="50" align="center"></el-table-column>
        <el-table-column label="报损单号" min-width="200" align="center" prop="reportSn"></el-table-column>
        <el-table-column label="报损门店" min-width="100" align="center" prop="storeName"></el-table-column>
        <el-table-column label="报损数量" width="70" align="center" prop="totalQuantity"></el-table-column>
        <el-table-column label="报损金额" width="90" align="center">
          <template slot-scope="scope">￥{{ scope.row.totalDamageAmount || 0 }}</template>
        </el-table-column>
        <el-table-column label="报损类型" width="90" align="center">
          <template slot-scope="scope">
            <el-tag :type="getDamageTypeTag(scope.row.damageType)" size="small">{{ formatDamageType(scope.row.damageType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="报损原因" min-width="150" align="center" prop="damageReason" show-overflow-tooltip></el-table-column>
        <el-table-column label="处理状态" width="80" align="center">
          <template slot-scope="scope">
            <el-tag :type="getStatusTag(scope.row.status)" size="small">{{ formatStatus(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="提交人" width="80" align="center" prop="submitAdminName"></el-table-column>
        <el-table-column label="处理人" width="80" align="center" prop="handleAdminName"></el-table-column>
        <el-table-column label="报损时间" width="150" align="center">
          <template slot-scope="scope">{{ scope.row.createTime | formatTime }}</template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="handleViewDetail(scope.row)">详情</el-button>
            <el-button size="mini" type="text" v-if="scope.row.status === 0" @click="handleStartProcess(scope.row)">处理</el-button>
            <el-button size="mini" type="text" v-if="scope.row.status === 1" @click="handleComplete(scope.row)">完成</el-button>
            <el-button size="mini" type="text" v-if="scope.row.status < 3" @click="handleClose(scope.row)">关闭</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="pagination-container">
      <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange"
        layout="total, sizes, prev, pager, next, jumper" :current-page.sync="listQuery.pageNum"
        :page-size="listQuery.pageSize" :page-sizes="[10, 20, 50]" :total="total">
      </el-pagination>
    </div>

    <!-- 新增报损对话框 -->
    <el-dialog title="新增报损" :visible.sync="addDialogVisible" width="900px">
      <el-form :model="damageForm" :rules="damageRules" ref="damageForm" label-width="100px">
        <el-form-item label="报损门店" prop="storeId">
          <el-select v-model="damageForm.storeId" placeholder="请选择门店" style="width:100%" @change="handleStoreChange">
            <el-option v-for="item in storeList" :key="item.id" :label="item.addressName" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="报损商品" prop="items">
          <div class="items-container">
            <el-button type="primary" plain @click="showProductDialog" :disabled="!damageForm.storeId" style="margin-bottom: 10px">
              {{ damageForm.storeId ? '选择商品' : '请先选择门店' }}
            </el-button>
            <el-table v-if="damageForm.items.length > 0" :data="damageForm.items" border size="small">
              <el-table-column label="商品" min-width="200">
                <template slot-scope="scope">
                  <div style="display: flex; align-items: center;">
                    <img v-if="scope.row.productPic" :src="scope.row.productPic" style="width: 40px; height: 40px; margin-right: 8px; border-radius: 4px;" />
                    <div>
                      <div>{{ scope.row.productName }}</div>
                      <div style="font-size: 12px; color: #999;">{{ scope.row.skuCode }} {{ scope.row.skuSpec }}</div>
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="数量" width="120" align="center">
                <template slot-scope="scope">
                  <el-input-number v-model="scope.row.damageQuantity" :min="1" :max="9999" size="mini" @change="calcItemAmount(scope.row)"></el-input-number>
                </template>
              </el-table-column>
              <el-table-column label="成本价" width="100" align="center">
                <template slot-scope="scope">￥{{ scope.row.costPrice || 0 }}</template>
              </el-table-column>
              <el-table-column label="销售价" width="100" align="center">
                <template slot-scope="scope">￥{{ scope.row.salePrice || 0 }}</template>
              </el-table-column>
              <el-table-column label="操作" width="60" align="center">
                <template slot-scope="scope">
                  <el-button type="text" size="mini" style="color: #f56c6c" @click="removeItem(scope.$index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <div v-if="damageForm.items.length > 0" class="items-summary">
              合计：{{ getTotalQuantity() }} 件商品，报损金额 ￥{{ getTotalDamageAmount() }}
            </div>
          </div>
        </el-form-item>
        <el-form-item label="报损类型" prop="damageType">
          <el-select v-model="damageForm.damageType" placeholder="请选择报损类型" style="width:100%" @change="handleDamageTypeChange">
            <el-option v-for="item in damageTypeOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="报损原因">
          <el-select v-model="damageForm.damageReasonId" placeholder="请选择报损原因" style="width:100%">
            <el-option v-for="item in reasonList" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="详细描述">
          <el-input type="textarea" v-model="damageForm.damageDescription" :rows="3" placeholder="请描述报损详情"></el-input>
        </el-form-item>
        <el-form-item label="瑕疵图片">
          <multi-upload v-model="damageForm.damagePicsList" :maxCount="5"></multi-upload>
        </el-form-item>
        <!-- 到货瑕疵特有字段 -->
        <template v-if="damageForm.damageType === 1">
          <el-form-item label="供应商名称">
            <el-input v-model="damageForm.supplierName" placeholder="请输入供应商名称"></el-input>
          </el-form-item>
          <el-form-item label="联系方式">
            <el-input v-model="damageForm.supplierContact" placeholder="请输入供应商联系方式"></el-input>
          </el-form-item>
        </template>
        <!-- 外借损坏特有字段 -->
        <template v-if="damageForm.damageType === 2">
          <el-form-item label="外借用途">
            <el-input v-model="damageForm.borrowPurpose" placeholder="如：拍摄、活动等"></el-input>
          </el-form-item>
          <el-form-item label="借用人">
            <el-input v-model="damageForm.borrowPerson" placeholder="请输入借用人"></el-input>
          </el-form-item>
          <el-form-item label="借出时间">
            <el-date-picker v-model="damageForm.borrowTime" type="datetime" placeholder="选择借出时间" style="width:100%"></el-date-picker>
          </el-form-item>
          <el-form-item label="归还时间">
            <el-date-picker v-model="damageForm.returnTime" type="datetime" placeholder="选择归还时间" style="width:100%"></el-date-picker>
          </el-form-item>
        </template>
        <el-form-item label="备注">
          <el-input v-model="damageForm.remark" placeholder="备注信息"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitAdd">确定</el-button>
      </span>
    </el-dialog>

    <!-- 商品SKU选择对话框 -->
    <product-sku-selector
      :visible.sync="productDialogVisible"
      :store-id="damageForm.storeId"
      @confirm="handleProductSkuSelected"
    ></product-sku-selector>
  </div>
</template>

<script>
import { formatDate } from '@/utils/date'
import { fetchDamageReportList, createDamageReport, startHandleDamage, completeDamage, closeDamage, fetchDamageReasonsByType } from '@/api/damage'
import { fetchList as fetchStoreList } from '@/api/storeAddress'
import MultiUpload from '@/components/Upload/multiUpload'
import ProductSkuSelector from '@/components/ProductSkuSelector'

const defaultListQuery = {
  pageNum: 1,
  pageSize: 10,
  reportSn: null,
  storeId: null,
  damageType: null,
  status: null,
  startTime: null,
  endTime: null
}

const defaultDamageForm = {
  storeId: null,
  items: [],
  damageType: null,
  damageReasonId: null,
  damageDescription: '',
  damagePicsList: [],
  supplierName: '',
  supplierContact: '',
  borrowPurpose: '',
  borrowPerson: '',
  borrowTime: null,
  returnTime: null,
  remark: ''
}

export default {
  name: 'DamageReport',
  components: { MultiUpload, ProductSkuSelector },
  data() {
    const validateItems = (rule, value, callback) => {
      if (!this.damageForm.items || this.damageForm.items.length === 0) {
        callback(new Error('请选择报损商品'))
      } else {
        callback()
      }
    }
    return {
      listQuery: Object.assign({}, defaultListQuery),
      list: [],
      total: 0,
      listLoading: false,
      multipleSelection: [],
      dateRange: null,
      storeList: [],
      reasonList: [],
      addDialogVisible: false,
      productDialogVisible: false,
      damageForm: Object.assign({}, defaultDamageForm, { items: [] }),
      damageRules: {
        storeId: [{ required: true, message: '请选择报损门店', trigger: 'change' }],
        items: [{ required: true, validator: validateItems, trigger: 'change' }],
        damageType: [{ required: true, message: '请选择报损类型', trigger: 'change' }]
      },
      damageTypeOptions: [
        { value: 1, label: '到货瑕疵' },
        { value: 2, label: '外借损坏' },
        { value: 3, label: '保存不当' },
        { value: 4, label: '人为原因' },
        { value: 5, label: '其他' }
      ],
      statusOptions: [
        { value: 0, label: '待处理' },
        { value: 1, label: '处理中' },
        { value: 2, label: '待验收' },
        { value: 3, label: '已完成' },
        { value: 4, label: '已关闭' }
      ]
    }
  },
  created() {
    this.getList()
    this.getStoreList()
  },
  filters: {
    formatTime(time) {
      if (!time) return 'N/A'
      return formatDate(new Date(time), 'yyyy-MM-dd hh:mm:ss')
    }
  },
  methods: {
    getList() {
      this.listLoading = true
      fetchDamageReportList(this.listQuery).then(response => {
        this.listLoading = false
        this.list = response.data.list
        this.total = response.data.total
      })
    },
    getStoreList() {
      fetchStoreList({ pageNum: 1, pageSize: 100 }).then(response => {
        this.storeList = response.data.list
      })
    },
    handleSearchList() {
      this.listQuery.pageNum = 1
      this.getList()
    },
    handleResetSearch() {
      this.listQuery = Object.assign({}, defaultListQuery)
      this.dateRange = null
      this.getList()
    },
    handleDateChange(dateRange) {
      if (dateRange && dateRange.length === 2) {
        this.listQuery.startTime = dateRange[0]
        this.listQuery.endTime = dateRange[1]
      } else {
        this.listQuery.startTime = null
        this.listQuery.endTime = null
      }
    },
    handleSelectionChange(val) {
      this.multipleSelection = val
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
    formatDamageType(type) {
      const item = this.damageTypeOptions.find(i => i.value === type)
      return item ? item.label : '未知'
    },
    formatStatus(status) {
      const item = this.statusOptions.find(i => i.value === status)
      return item ? item.label : '未知'
    },
    getDamageTypeTag(type) {
      const tags = { 1: 'danger', 2: 'warning', 3: 'info', 4: 'danger', 5: '' }
      return tags[type] || ''
    },
    getStatusTag(status) {
      const tags = { 0: 'warning', 1: 'primary', 2: 'info', 3: 'success', 4: 'info' }
      return tags[status] || ''
    },
    handleAdd() {
      this.damageForm = Object.assign({}, defaultDamageForm, { items: [], damagePicsList: [] })
      this.reasonList = []
      this.addDialogVisible = true
    },
    handleStoreChange() {
      // 切换门店时清空已选商品
      this.damageForm.items = []
    },
    showProductDialog() {
      if (!this.damageForm.storeId) {
        this.$message.warning('请先选择报损门店')
        return
      }
      this.productDialogVisible = true
    },
    handleProductSkuSelected(items) {
      // ProductSkuSelector返回的是数组，支持多选
      if (items && items.length > 0) {
        for (const item of items) {
          // 检查是否已存在
          const exists = this.damageForm.items.find(i => i.skuId === item.skuId)
          if (!exists) {
            this.damageForm.items.push({
              productId: item.productId,
              productName: item.productName,
              productPic: item.productPic || '',
              skuId: item.skuId,
              skuCode: item.skuCode,
              skuSpec: item.specs || '',
              damageQuantity: item.quantity || 1,
              costPrice: item.systemPrice || 0,
              salePrice: item.salePrice || 0
            })
          }
        }
      }
    },
    removeItem(index) {
      this.damageForm.items.splice(index, 1)
    },
    calcItemAmount(item) {
      // 数量变化时可以做一些计算
    },
    getTotalQuantity() {
      return this.damageForm.items.reduce((sum, item) => sum + (item.damageQuantity || 0), 0)
    },
    getTotalDamageAmount() {
      return this.damageForm.items.reduce((sum, item) => {
        const qty = item.damageQuantity || 0
        const price = item.costPrice || 0
        return sum + qty * price
      }, 0).toFixed(2)
    },
    handleDamageTypeChange(type) {
      this.damageForm.damageReasonId = null
      if (type) {
        fetchDamageReasonsByType(type).then(response => {
          this.reasonList = response.data
        })
      } else {
        this.reasonList = []
      }
    },
    handleSubmitAdd() {
      this.$refs.damageForm.validate(valid => {
        if (valid) {
          if (this.damageForm.items.length === 0) {
            this.$message.warning('请选择报损商品')
            return
          }
          const data = {
            storeId: this.damageForm.storeId,
            damageType: this.damageForm.damageType,
            damageReasonId: this.damageForm.damageReasonId,
            damageDescription: this.damageForm.damageDescription,
            damagePics: this.damageForm.damagePicsList.join(','),
            supplierName: this.damageForm.supplierName,
            supplierContact: this.damageForm.supplierContact,
            borrowPurpose: this.damageForm.borrowPurpose,
            borrowPerson: this.damageForm.borrowPerson,
            borrowTime: this.damageForm.borrowTime,
            returnTime: this.damageForm.returnTime,
            remark: this.damageForm.remark,
            items: this.damageForm.items
          }
          createDamageReport(data).then(response => {
            this.$message.success('创建成功')
            this.addDialogVisible = false
            this.getList()
          })
        }
      })
    },
    handleViewDetail(row) {
      this.$router.push({ path: '/pms/damageDetail', query: { id: row.id } })
    },
    handleStartProcess(row) {
      this.$confirm('确定开始处理该报损记录?', '提示', { type: 'warning' }).then(() => {
        startHandleDamage(row.id).then(() => {
          this.$message.success('操作成功')
          this.getList()
        })
      })
    },
    handleComplete(row) {
      this.$confirm('确定完成该报损处理?', '提示', { type: 'warning' }).then(() => {
        completeDamage(row.id).then(() => {
          this.$message.success('操作成功')
          this.getList()
        })
      })
    },
    handleClose(row) {
      this.$prompt('请输入关闭原因', '关闭报损', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(({ value }) => {
        closeDamage(row.id, value).then(() => {
          this.$message.success('操作成功')
          this.getList()
        })
      })
    }
  }
}
</script>
<style scoped>
.input-width {
  width: 200px;
}
.items-container {
  width: 100%;
}
.items-summary {
  margin-top: 10px;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
  font-size: 14px;
  color: #606266;
}
</style>
