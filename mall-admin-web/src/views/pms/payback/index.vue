<template>
  <div class="app-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="statistics-cards">
      <el-col :span="4">
        <el-card>
          <div class="stat-card">
            <div class="stat-value">{{ statistics.totalBatchCount || 0 }}</div>
            <div class="stat-label">批次总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card>
          <div class="stat-card">
            <div class="stat-value active">{{ statistics.activeBatchCount || 0 }}</div>
            <div class="stat-label">活跃批次</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card>
          <div class="stat-card">
            <div class="stat-value success">{{ statistics.completedBatchCount || 0 }}</div>
            <div class="stat-label">已回本</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card>
          <div class="stat-card">
            <div class="stat-value pending">{{ statistics.pendingBatchCount || 0 }}</div>
            <div class="stat-label">待启动</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card>
          <div class="stat-card">
            <div class="stat-value">￥{{ formatMoney(statistics.totalSalesAmount) }}</div>
            <div class="stat-label">总销售额</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card>
          <div class="stat-card">
            <div class="stat-value" :class="{ 'profit': statistics.totalProfitAmount > 0, 'loss': statistics.totalProfitAmount < 0 }">
              ￥{{ formatMoney(statistics.totalProfitAmount) }}
            </div>
            <div class="stat-label">总利润</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索筛选 -->
    <el-card class="filter-container" shadow="never">
      <el-form :inline="true" :model="listQuery" class="demo-form-inline">
        <el-form-item label="关键词：">
          <el-input
            v-model="listQuery.keyword"
            placeholder="商品名称/货号"
            style="width: 200px;"
            clearable>
          </el-input>
        </el-form-item>
        <el-form-item label="批次状态：">
          <el-select v-model="listQuery.batchStatus" placeholder="请选择状态" clearable style="width: 150px;">
            <el-option label="待启动" :value="0"></el-option>
            <el-option label="活跃" :value="1"></el-option>
            <el-option label="已回本" :value="2"></el-option>
            <el-option label="提前结束" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="补货日期：">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            style="width: 240px;">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearchList">查询</el-button>
          <el-button icon="el-icon-refresh" @click="handleResetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作按钮 -->
    <div class="table-toolbar">
      <el-button type="primary" icon="el-icon-plus" @click="handleAddBatch">添加回本分析</el-button>
      <el-button type="success" icon="el-icon-refresh" @click="handleRefreshAll">批量刷新</el-button>
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <el-table
        ref="batchTable"
        :data="list"
        v-loading="listLoading"
        style="width: 100%;"
        border>
        <el-table-column label="商品信息" width="280">
          <template slot-scope="scope">
            <div class="product-info">
              <img v-if="scope.row.productPic" :src="scope.row.productPic" class="product-pic" alt="商品图片">
              <div class="product-details">
                <div class="product-name">{{ scope.row.productName }}</div>
                <div class="product-sn">货号：{{ scope.row.productSn }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="批次" width="80" align="center">
          <template slot-scope="scope">
            <el-tag size="small">第{{ scope.row.batchNo }}批</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="补货信息" width="140">
          <template slot-scope="scope">
            <div>数量：{{ scope.row.replenishmentQuantity }}</div>
            <div>成本：￥{{ formatMoney(scope.row.replenishmentAmount) }}</div>
            <div>目标：￥{{ formatMoney(scope.row.targetAmount) }}</div>
          </template>
        </el-table-column>
        <el-table-column label="销售情况" width="130">
          <template slot-scope="scope">
            <div>数量：{{ scope.row.currentSoldQuantity || 0 }}</div>
            <div>金额：￥{{ formatMoney(scope.row.currentSoldAmount) }}</div>
          </template>
        </el-table-column>
        <el-table-column label="回本进度" width="140">
          <template slot-scope="scope">
            <div class="progress-container">
              <el-progress 
                :percentage="getProgressPercentage(scope.row)" 
                :color="getProgressColor(scope.row)"
                :show-text="false">
              </el-progress>
              <div class="progress-text">{{ getProgressPercentage(scope.row) }}%</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="利润分析" width="130">
          <template slot-scope="scope">
            <div :class="{ 'profit-text': scope.row.profitAmount > 0, 'loss-text': scope.row.profitAmount < 0 }">
              利润：￥{{ formatMoney(scope.row.profitAmount) }}
            </div>
            <div :class="{ 'profit-text': scope.row.profitRate > 0, 'loss-text': scope.row.profitRate < 0 }">
              利润率：{{ formatRate(scope.row.profitRate) }}%
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template slot-scope="scope">
            <el-tag :type="getStatusTagType(scope.row.batchStatus)" size="small">
              {{ scope.row.batchStatusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="补货日期" width="110">
          <template slot-scope="scope">
            <span>{{ formatDateToString(scope.row.replenishmentDate) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" align="center">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" @click="handleShowDetail(scope.row)">详情</el-button>
            <el-button type="warning" size="mini" @click="handleEditBatch(scope.row)">编辑</el-button>
            <el-dropdown trigger="click" @command="(cmd) => handleCommand(cmd, scope.row)" style="margin-left: 10px;">
              <el-button type="info" size="mini">
                更多<i class="el-icon-arrow-down el-icon--right"></i>
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="refresh">刷新数据</el-dropdown-item>
                <el-dropdown-item command="forceStart" v-if="scope.row.batchStatus === 0">强制启动</el-dropdown-item>
                <el-dropdown-item command="delete" divided>删除</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
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
        layout="total, sizes, prev, pager, next, jumper"
        :current-page.sync="listQuery.pageNum"
        :page-size="listQuery.pageSize"
        :page-sizes="[10, 15, 20]"
        :total="total">
      </el-pagination>
    </div>

    <!-- 添加/编辑批次弹窗 -->
    <el-dialog
      :title="dialogType === 'add' ? '添加回本分析' : '编辑批次'"
      :visible.sync="batchDialogVisible"
      width="500px">
      <el-form ref="batchForm" :model="batchForm" :rules="batchRules" label-width="100px">
        <el-form-item label="选择商品" prop="productId" v-if="dialogType === 'add'">
          <div class="product-select-container">
            <el-button type="primary" icon="el-icon-plus" @click="showProductSelectionDialog">选择商品</el-button>
            <div v-if="batchForm.productId" class="selected-product-info">
              <img v-if="batchForm.productPic" :src="batchForm.productPic" class="selected-product-pic">
              <div class="selected-product-detail">
                <div class="selected-product-name">{{ batchForm.productName }}</div>
                <div class="selected-product-sn">货号：{{ batchForm.productSn }}</div>
              </div>
              <el-button type="text" icon="el-icon-close" @click="clearSelectedProduct"></el-button>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="商品" v-else>
          <div class="selected-product-info" v-if="batchForm.productId">
            <img v-if="batchForm.productPic" :src="batchForm.productPic" class="selected-product-pic">
            <div class="selected-product-detail">
              <div class="selected-product-name">{{ batchForm.productName }}</div>
              <div class="selected-product-sn">货号：{{ batchForm.productSn }}</div>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="补货数量" prop="replenishmentQuantity">
          <el-input-number v-model="batchForm.replenishmentQuantity" :min="1" style="width: 100%;"></el-input-number>
        </el-form-item>
        <el-form-item label="补货金额" prop="replenishmentAmount">
          <el-input-number v-model="batchForm.replenishmentAmount" :min="0.01" :precision="2" style="width: 100%;">
            <template slot="prepend">￥</template>
          </el-input-number>
        </el-form-item>
        <el-form-item label="回本目标" prop="targetAmount">
          <el-input-number v-model="batchForm.targetAmount" :min="0.01" :precision="2" style="width: 100%;">
            <template slot="prepend">￥</template>
          </el-input-number>
        </el-form-item>
        <el-form-item label="补货日期" prop="replenishmentDate">
          <el-date-picker
            v-model="batchForm.replenishmentDate"
            type="datetime"
            placeholder="选择日期时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            style="width: 100%;">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="batchDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitBatch" :loading="submitLoading">确定</el-button>
      </span>
    </el-dialog>


    <!-- 商品选择弹窗 -->
    <product-selection-dialog
      ref="productSelectionDialog"
      :visible.sync="productSelectionVisible"
      :title="'选择商品'"
      :selection-mode="'single'"
      :selected-products="selectedProductsForDialog"
      @selection-confirmed="handleProductSelectionConfirm">
    </product-selection-dialog>

    <!-- 批次详情弹窗 -->
    <el-dialog
      title="批次详情"
      :visible.sync="detailDialogVisible"
      width="700px">
      <div v-if="detailData" class="detail-content">
        <!-- 商品基本信息 -->
        <el-card class="detail-card" shadow="never">
          <div slot="header" class="card-header">
            <span>商品信息</span>
            <el-tag v-if="detailData.batchStatus === 0" type="warning" size="small" style="margin-left: 10px;">
              待启动批次 - 前序批次回本后自动启动
            </el-tag>
          </div>
          <el-row :gutter="20">
            <el-col :span="6">
              <img v-if="detailData.productPic" :src="detailData.productPic" class="detail-product-pic" alt="商品图片">
            </el-col>
            <el-col :span="18">
              <div class="detail-info">
                <h3>{{ detailData.productName }}</h3>
                <p><strong>商品货号：</strong>{{ detailData.productSn }}</p>
                <p><strong>批次序号：</strong>第{{ detailData.batchNo }}批</p>
                <p><strong>批次状态：</strong>
                  <el-tag :type="getStatusTagType(detailData.batchStatus)" size="small">
                    {{ detailData.batchStatusText }}
                  </el-tag>
                </p>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <!-- 补货与销售数据 -->
        <el-card class="detail-card" shadow="never">
          <div slot="header" class="card-header">
            <span>补货与销售数据</span>
          </div>
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="analysis-item">
                <h4>补货信息</h4>
                <p><strong>补货数量：</strong>{{ detailData.replenishmentQuantity }} 件</p>
                <p><strong>补货金额：</strong>￥{{ formatMoney(detailData.replenishmentAmount) }}</p>
                <p><strong>回本目标：</strong>￥{{ formatMoney(detailData.targetAmount) }}</p>
                <p><strong>补货日期：</strong>{{ formatDateToString(detailData.replenishmentDate) }}</p>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="analysis-item">
                <h4>销售情况</h4>
                <p><strong>已售数量：</strong>{{ detailData.currentSoldQuantity || 0 }} 件</p>
                <p><strong>已售金额：</strong>￥{{ formatMoney(detailData.currentSoldAmount) }}</p>
                <p><strong>回本进度：</strong>{{ getProgressPercentage(detailData) }}%</p>
              </div>
            </el-col>
          </el-row>
          
          <div class="progress-detail">
            <h4>回本进度</h4>
            <el-progress 
              :percentage="getProgressPercentage(detailData)" 
              :color="getProgressColor(detailData)"
              :stroke-width="20">
            </el-progress>
          </div>
        </el-card>

        <!-- 利润分析 -->
        <el-card class="detail-card" shadow="never">
          <div slot="header" class="card-header">
            <span>利润分析</span>
          </div>
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="profit-item" :class="{ 'profit': detailData.profitAmount > 0, 'loss': detailData.profitAmount < 0 }">
                <div class="profit-label">利润金额</div>
                <div class="profit-value">￥{{ formatMoney(detailData.profitAmount) }}</div>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="profit-item" :class="{ 'profit': detailData.profitRate > 0, 'loss': detailData.profitRate < 0 }">
                <div class="profit-label">利润率</div>
                <div class="profit-value">{{ formatRate(detailData.profitRate) }}%</div>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <!-- 渠道销售明细 -->
        <el-card class="detail-card" shadow="never" v-if="detailData.channelSummary">
          <div slot="header" class="card-header">
            <span>渠道销售汇总</span>
          </div>
          <el-table :data="channelList" border size="small">
            <el-table-column prop="channelName" label="渠道" width="150"></el-table-column>
            <el-table-column prop="quantity" label="销售数量" width="120"></el-table-column>
            <el-table-column label="销售金额">
              <template slot-scope="scope">
                ￥{{ formatMoney(scope.row.amount) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- 系统订单销售明细 -->
        <el-card class="detail-card" shadow="never" v-if="detailData.orderDetails && detailData.orderDetails.length > 0">
          <div slot="header" class="card-header">
            <span>系统订单销售明细</span>
            <el-tag size="small" type="info" style="margin-left: 10px;">{{ detailData.orderDetails.length }} 笔</el-tag>
          </div>
          <el-table :data="detailData.orderDetails" border size="small" max-height="300">
            <el-table-column prop="orderSn" label="订单编号" width="180"></el-table-column>
            <el-table-column prop="sourceTypeName" label="订单来源" width="100"></el-table-column>
            <el-table-column prop="productQuantity" label="数量" width="80"></el-table-column>
            <el-table-column label="实付金额" width="100">
              <template slot-scope="scope">
                ￥{{ formatMoney(scope.row.payAmount) }}
              </template>
            </el-table-column>
            <el-table-column label="支付时间" width="160">
              <template slot-scope="scope">
                {{ formatDateTimeToString(scope.row.paymentTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="orderStatusName" label="状态" width="80"></el-table-column>
          </el-table>
        </el-card>

        <!-- 非系统销售明细 -->
        <el-card class="detail-card" shadow="never" v-if="detailData.nonSystemSaleDetails && detailData.nonSystemSaleDetails.length > 0">
          <div slot="header" class="card-header">
            <span>非系统销售明细</span>
            <el-tag size="small" type="info" style="margin-left: 10px;">{{ detailData.nonSystemSaleDetails.length }} 笔</el-tag>
          </div>
          <el-table :data="detailData.nonSystemSaleDetails" border size="small" max-height="300">
            <el-table-column prop="saleNo" label="销售单号" width="180"></el-table-column>
            <el-table-column prop="saleTypeName" label="销售类型" width="100"></el-table-column>
            <el-table-column prop="quantity" label="数量" width="80"></el-table-column>
            <el-table-column label="销售金额" width="100">
              <template slot-scope="scope">
                ￥{{ formatMoney(scope.row.amount) }}
              </template>
            </el-table-column>
            <el-table-column label="销售日期" width="160">
              <template slot-scope="scope">
                {{ formatDateTimeToString(scope.row.saleDate) }}
              </template>
            </el-table-column>
            <el-table-column prop="operatorName" label="操作人" width="100"></el-table-column>
          </el-table>
        </el-card>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button type="success" icon="el-icon-download" @click="handleExportDetail">导出Excel</el-button>
        <el-button type="primary" @click="handleRefreshDetail">刷新数据</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  createBatch,
  updateBatch,
  deleteBatch,
  forceStartBatch,
  listBatches,
  getBatchDetail,
  refreshBatchSales,
  refreshAllBatches,
  getBatchStatistics,
  exportBatchSalesToExcel
} from '@/api/payback'
import ProductSelectionDialog from '@/components/ProductSelectionDialog'

export default {
  name: 'PaybackIndex',
  components: {
    ProductSelectionDialog
  },
  data() {
    return {
      listQuery: {
        keyword: null,
        batchStatus: null,
        startDate: null,
        endDate: null,
        pageNum: 1,
        pageSize: 10
      },
      dateRange: null,
      list: [],
      total: 0,
      listLoading: false,
      statistics: {},
      // 添加/编辑弹窗
      batchDialogVisible: false,
      dialogType: 'add',
      batchForm: {
        productId: null,
        productName: '',
        productSn: '',
        productPic: '',
        replenishmentQuantity: 1,
        replenishmentAmount: null,
        targetAmount: null,
        replenishmentDate: null
      },
      batchRules: {
        productId: [{ required: true, message: '请选择商品', trigger: 'change' }],
        replenishmentQuantity: [{ required: true, message: '请输入补货数量', trigger: 'blur' }],
        replenishmentAmount: [{ required: true, message: '请输入补货金额', trigger: 'blur' }],
        targetAmount: [{ required: true, message: '请输入回本目标金额', trigger: 'blur' }]
      },
      submitLoading: false,
      editBatchId: null,
      // 商品选择弹窗
      productSelectionVisible: false,
      selectedProductsForDialog: [],
      // 详情弹窗
      detailDialogVisible: false,
      detailData: null,
      detailLoading: false
    }
  },
  computed: {
    channelList() {
      if (!this.detailData || !this.detailData.channelSummary) return []
      return Object.values(this.detailData.channelSummary)
    }
  },
  watch: {
    dateRange(val) {
      if (val && val.length === 2) {
        this.listQuery.startDate = val[0]
        this.listQuery.endDate = val[1]
      } else {
        this.listQuery.startDate = null
        this.listQuery.endDate = null
      }
    }
  },
  created() {
    this.getList()
    this.getStatistics()
  },
  methods: {
    getList() {
      this.listLoading = true
      listBatches(this.listQuery).then(response => {
        this.list = response.data.list
        this.total = response.data.total
        this.listLoading = false
      }).catch(() => {
        this.listLoading = false
      })
    },
    getStatistics() {
      getBatchStatistics().then(response => {
        this.statistics = response.data
      })
    },
    handleSearchList() {
      this.listQuery.pageNum = 1
      this.getList()
    },
    handleResetSearch() {
      this.listQuery = {
        keyword: null,
        batchStatus: null,
        startDate: null,
        endDate: null,
        pageNum: 1,
        pageSize: 10
      }
      this.dateRange = null
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
    // 添加批次
    handleAddBatch() {
      this.dialogType = 'add'
      this.editBatchId = null
      this.batchForm = {
        productId: null,
        productName: '',
        productSn: '',
        productPic: '',
        replenishmentQuantity: 1,
        replenishmentAmount: null,
        targetAmount: null,
        replenishmentDate: this.formatDateTimeToString(new Date())
      }
      this.batchDialogVisible = true
      this.$nextTick(() => {
        this.$refs.batchForm && this.$refs.batchForm.clearValidate()
      })
    },
    // 编辑批次
    handleEditBatch(row) {
      this.dialogType = 'edit'
      this.editBatchId = row.id
      this.batchForm = {
        productId: row.productId,
        productName: row.productName,
        productSn: row.productSn,
        productPic: row.productPic,
        replenishmentQuantity: row.replenishmentQuantity,
        replenishmentAmount: row.replenishmentAmount,
        targetAmount: row.targetAmount,
        replenishmentDate: this.formatDateTimeToString(row.replenishmentDate)
      }
      this.batchDialogVisible = true
    },
    // 提交批次
    handleSubmitBatch() {
      this.$refs.batchForm.validate(valid => {
        if (!valid) return
        this.submitLoading = true
        const data = {
          productId: this.batchForm.productId,
          replenishmentQuantity: this.batchForm.replenishmentQuantity,
          replenishmentAmount: this.batchForm.replenishmentAmount,
          targetAmount: this.batchForm.targetAmount,
          replenishmentDate: this.batchForm.replenishmentDate
        }
        if (this.dialogType === 'add') {
          createBatch(data).then(() => {
            this.$message.success('创建批次成功')
            this.batchDialogVisible = false
            this.getList()
            this.getStatistics()
          }).finally(() => {
            this.submitLoading = false
          })
        } else {
          updateBatch(this.editBatchId, data).then(() => {
            this.$message.success('更新批次成功')
            this.batchDialogVisible = false
            this.getList()
          }).finally(() => {
            this.submitLoading = false
          })
        }
      })
    },
    // 显示商品选择弹窗
    showProductSelectionDialog() {
      this.selectedProductsForDialog = this.batchForm.productId ? [{
        id: this.batchForm.productId,
        name: this.batchForm.productName,
        productSn: this.batchForm.productSn,
        pic: this.batchForm.productPic
      }] : []
      this.$nextTick(() => {
        this.productSelectionVisible = true
      })
    },
    // 处理商品选择确认
    handleProductSelectionConfirm(selectedProducts) {
      if (selectedProducts && selectedProducts.length > 0) {
        const product = selectedProducts[0]
        this.batchForm.productId = product.id
        this.batchForm.productName = product.name
        this.batchForm.productSn = product.productSn
        this.batchForm.productPic = product.pic
      }
      this.productSelectionVisible = false
    },
    // 清除已选商品
    clearSelectedProduct() {
      this.batchForm.productId = null
      this.batchForm.productName = ''
      this.batchForm.productSn = ''
      this.batchForm.productPic = ''
    },
    // 查看详情
    handleShowDetail(row) {
      this.detailLoading = true
      this.detailDialogVisible = true
      getBatchDetail(row.id).then(response => {
        this.detailData = response.data
        this.detailLoading = false
      }).catch(() => {
        this.detailLoading = false
        this.detailDialogVisible = false
      })
    },
    // 刷新详情数据
    handleRefreshDetail() {
      if (!this.detailData) return
      this.detailLoading = true
      refreshBatchSales(this.detailData.id).then(() => {
        return getBatchDetail(this.detailData.id)
      }).then(response => {
        this.detailData = response.data
        this.$message.success('刷新成功')
        this.detailLoading = false
        this.getList()
        this.getStatistics()
      }).catch(() => {
        this.detailLoading = false
      })
    },
    // 导出详情Excel
    handleExportDetail() {
      if (!this.detailData) return
      const loading = this.$loading({
        lock: true,
        text: '正在导出...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      exportBatchSalesToExcel(this.detailData.id).then(response => {
        loading.close()
        const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = '回本分析_' + this.detailData.productName + '_第' + this.detailData.batchNo + '批.xlsx'
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
      }).catch(() => {
        loading.close()
        this.$message.error('导出失败')
      })
    },
    // 批量刷新
    handleRefreshAll() {
      this.$confirm('确认批量刷新所有活跃批次的销售数据?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const loading = this.$loading({
          lock: true,
          text: '正在刷新数据...',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        })
        refreshAllBatches().then(response => {
          loading.close()
          this.$message.success(response.message || '批量刷新完成')
          this.getList()
          this.getStatistics()
        }).catch(() => {
          loading.close()
        })
      })
    },
    // 更多操作
    handleCommand(command, row) {
      switch (command) {
        case 'refresh':
          this.handleRefreshSingle(row)
          break
        case 'forceStart':
          this.handleForceStart(row)
          break
        case 'delete':
          this.handleDelete(row)
          break
      }
    },
    // 刷新单个批次
    handleRefreshSingle(row) {
      refreshBatchSales(row.id).then(() => {
        this.$message.success('刷新成功')
        this.getList()
        this.getStatistics()
      })
    },
    // 强制启动
    handleForceStart(row) {
      this.$confirm('确认强制启动该批次? 当前活跃批次将被标记为"提前结束"。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        forceStartBatch(row.id).then(() => {
          this.$message.success('强制启动成功')
          this.getList()
          this.getStatistics()
        })
      })
    },
    // 删除批次
    handleDelete(row) {
      const message = row.currentSoldQuantity > 0 
        ? '该批次已有销售记录，确认删除?' 
        : '确认删除该批次?'
      this.$confirm(message, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteBatch(row.id).then(() => {
          this.$message.success('删除成功')
          this.getList()
          this.getStatistics()
        })
      })
    },
    // 工具方法
    formatMoney(value) {
      if (value === null || value === undefined) return '0.00'
      return parseFloat(value).toFixed(2)
    },
    formatRate(value) {
      if (value === null || value === undefined) return '0.00'
      return parseFloat(value).toFixed(2)
    },
    formatDateToString(date) {
      if (!date) return null
      const d = new Date(date)
      const year = d.getFullYear()
      const month = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    formatDateTimeToString(date) {
      if (!date) return null
      const d = new Date(date)
      const year = d.getFullYear()
      const month = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      const hours = String(d.getHours()).padStart(2, '0')
      const minutes = String(d.getMinutes()).padStart(2, '0')
      const seconds = String(d.getSeconds()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    },
    getProgressPercentage(row) {
      if (!row.paybackProgress) return 0
      return Math.min(Math.round(parseFloat(row.paybackProgress)), 100)
    },
    getProgressColor(row) {
      const percentage = this.getProgressPercentage(row)
      if (percentage >= 100) return '#67c23a'
      if (percentage >= 80) return '#409eff'
      if (percentage >= 50) return '#e6a23c'
      return '#f56c6c'
    },
    getStatusTagType(status) {
      const typeMap = {
        0: 'info',
        1: 'primary',
        2: 'success',
        3: 'warning'
      }
      return typeMap[status] || 'info'
    }
  }
}
</script>

<style scoped>
.statistics-cards {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  padding: 15px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}

.stat-value.active {
  color: #409eff;
}

.stat-value.success {
  color: #67c23a;
}

.stat-value.pending {
  color: #e6a23c;
}

.stat-value.profit {
  color: #67c23a;
}

.stat-value.loss {
  color: #f56c6c;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.filter-container {
  margin-bottom: 20px;
}

.table-toolbar {
  margin-bottom: 20px;
}

.table-container {
  background: #fff;
  border-radius: 4px;
}

.product-info {
  display: flex;
  align-items: center;
}

.product-pic {
  width: 50px;
  height: 50px;
  border-radius: 4px;
  margin-right: 10px;
  object-fit: cover;
}

.product-details {
  flex: 1;
}

.product-name {
  font-weight: bold;
  margin-bottom: 4px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-sn {
  font-size: 12px;
  color: #909399;
}

.progress-container {
  position: relative;
}

.progress-text {
  text-align: center;
  font-size: 12px;
  margin-top: 2px;
}

.profit-text {
  color: #67c23a;
}

.loss-text {
  color: #f56c6c;
}

.pagination-container {
  background: #fff;
  padding: 32px 16px;
  text-align: right;
}

.detail-content {
  max-height: 600px;
  overflow-y: auto;
}

.detail-card {
  margin-bottom: 15px;
}

.card-header {
  font-weight: bold;
}

.detail-product-pic {
  width: 100%;
  max-width: 120px;
  border-radius: 4px;
}

.detail-info h3 {
  margin: 0 0 10px 0;
  color: #303133;
}

.detail-info p {
  margin: 5px 0;
  color: #606266;
}

.analysis-item h4 {
  margin: 0 0 10px 0;
  color: #303133;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 5px;
}

.analysis-item p {
  margin: 5px 0;
}

.progress-detail {
  margin: 20px 0;
}

.progress-detail h4 {
  margin-bottom: 15px;
}

.profit-item {
  text-align: center;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 4px;
}

.profit-item.profit {
  background: #f0f9eb;
}

.profit-item.loss {
  background: #fef0f0;
}

.profit-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.profit-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.profit-item.profit .profit-value {
  color: #67c23a;
}

.profit-item.loss .profit-value {
  color: #f56c6c;
}

.product-select-container {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.selected-product-info {
  display: flex;
  align-items: center;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

.selected-product-pic {
  width: 50px;
  height: 50px;
  border-radius: 4px;
  margin-right: 10px;
  object-fit: cover;
}

.selected-product-detail {
  flex: 1;
}

.selected-product-name {
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}

.selected-product-sn {
  font-size: 12px;
  color: #909399;
}
</style>
