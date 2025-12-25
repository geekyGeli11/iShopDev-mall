<template> 
  <div class="app-container">
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
        <el-button
          style="float:right;margin-right: 15px"
          type="primary"
          @click="handleExport"
          size="small">
          导出数据
        </el-button>
      </div>
      <div style="margin-top: 15px">
        <el-form :inline="true" :model="listQuery" size="small" label-width="140px">
          <el-form-item label="订单编号：">
            <el-input v-model="listQuery.orderSn" class="input-width" placeholder="充值订单号"></el-input>
          </el-form-item>
          <el-form-item label="用户账号：">
            <el-input v-model="listQuery.memberUsername" class="input-width" placeholder="用户名"></el-input>
          </el-form-item>
          <el-form-item label="创建时间：">
            <el-date-picker
              class="input-width"
              v-model="listQuery.createTime"
              value-format="yyyy-MM-dd"
              type="date"
              placeholder="请选择时间">
            </el-date-picker>
          </el-form-item>
          <el-form-item label="支付时间：">
            <el-date-picker
              class="input-width"
              v-model="listQuery.payTime"
              value-format="yyyy-MM-dd"
              type="date"
              placeholder="请选择时间">
            </el-date-picker>
          </el-form-item>
          <el-form-item label="订单状态：">
            <el-select v-model="listQuery.status" class="input-width" placeholder="全部" clearable>
              <el-option v-for="item in statusOptions"
                         :key="item.value"
                         :label="item.label"
                         :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="支付方式：">
            <el-select v-model="listQuery.payType" class="input-width" placeholder="全部" clearable>
              <el-option v-for="item in payTypeOptions"
                         :key="item.value"
                         :label="item.label"
                         :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="operate-container" shadow="never">
      <i class="el-icon-tickets"></i>
      <span>数据列表</span>
    </el-card>
    <div class="table-container">
      <el-table ref="rechargeOrderTable"
                :data="list"
                style="width: 100%;"
                @selection-change="handleSelectionChange"
                v-loading="listLoading" border>
        <el-table-column type="selection" width="60" align="center"></el-table-column>
        <el-table-column label="编号" width="80" align="center">
          <template slot-scope="scope">{{scope.row.id}}</template>
        </el-table-column>
        <el-table-column label="订单编号" width="180" align="center">
          <template slot-scope="scope">{{scope.row.orderSn}}</template>
        </el-table-column>
        <el-table-column label="用户账号" align="center">
          <template slot-scope="scope">{{scope.row.memberUsername}}</template>
        </el-table-column>
        <el-table-column label="充值金额" width="120" align="center">
          <template slot-scope="scope">￥{{scope.row.amount}}</template>
        </el-table-column>
        <el-table-column label="支付方式" width="120" align="center">
          <template slot-scope="scope">{{scope.row.payType | formatPayType}}</template>
        </el-table-column>
        <el-table-column label="订单状态" width="120" align="center">
          <template slot-scope="scope">{{scope.row.status | formatStatus}}</template>
        </el-table-column>
        <el-table-column label="创建时间" width="160" align="center">
          <template slot-scope="scope">{{scope.row.createTime | formatCreateTime}}</template>
        </el-table-column>
        <el-table-column label="支付时间" width="160" align="center">
          <template slot-scope="scope">{{scope.row.payTime | formatPayTime}}</template>
        </el-table-column>
        <el-table-column label="第三方流水号" width="140" align="center">
          <template slot-scope="scope">{{scope.row.paySn || '-'}}</template>
        </el-table-column>
        <el-table-column label="备注" align="center">
          <template slot-scope="scope">{{scope.row.note || '-'}}</template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              @click="handleViewDetail(scope.$index, scope.row)"
            >查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="batch-operate-container">
      <el-select
        size="small"
        v-model="operateType" placeholder="批量操作">
        <el-option
          v-for="item in operateOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value">
        </el-option>
      </el-select>
      <el-button
        style="margin-left: 20px"
        class="search-button"
        @click="handleBatchOperate()"
        type="primary"
        size="small">
        确定
      </el-button>
    </div>
    <div class="pagination-container">
      <el-pagination
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        layout="total, sizes,prev, pager, next,jumper"
        :current-page.sync="listQuery.pageNum"
        :page-size="listQuery.pageSize"
        :page-sizes="[5,10,15]"
        :total="total">
      </el-pagination>
    </div>

    <!-- 订单详情对话框 -->
    <el-dialog
      title="充值订单详情"
      :visible.sync="detailDialogVisible"
      width="800px">
      <div v-if="selectedOrder">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单编号">{{selectedOrder.orderSn}}</el-descriptions-item>
          <el-descriptions-item label="用户账号">{{selectedOrder.memberUsername}}</el-descriptions-item>
          <el-descriptions-item label="充值金额">￥{{selectedOrder.amount}}</el-descriptions-item>
          <el-descriptions-item label="支付方式">{{selectedOrder.payType | formatPayType}}</el-descriptions-item>
          <el-descriptions-item label="订单状态">{{selectedOrder.status | formatStatus}}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{selectedOrder.createTime | formatCreateTime}}</el-descriptions-item>
          <el-descriptions-item label="支付时间">{{selectedOrder.payTime | formatPayTime}}</el-descriptions-item>
          <el-descriptions-item label="第三方流水号">{{selectedOrder.paySn || '-'}}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{selectedOrder.note || '-'}}</el-descriptions-item>
        </el-descriptions>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关 闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { formatDate } from '@/utils/date'
import { fetchRechargeOrderList, exportRechargeOrder, deleteRechargeOrder } from '@/api/rechargeOrder'

const defaultListQuery = {
  pageNum: 1,
  pageSize: 10,
  orderSn: null,
  memberUsername: null,
  status: null,
  payType: null,
  createTime: null,
  payTime: null
}

const statusOptions = [
  { label: '待支付', value: 0 },
  { label: '支付成功', value: 1 },
  { label: '支付失败', value: 2 },
  { label: '已取消', value: 3 }
]

const payTypeOptions = [
  { label: '支付宝', value: 1 },
  { label: '微信', value: 2 }
]

export default {
  name: 'RechargeOrderList',
  data() {
    return {
      listQuery: Object.assign({}, defaultListQuery),
      statusOptions: statusOptions,
      payTypeOptions: payTypeOptions,
      list: null,
      total: null,
      listLoading: false,
      multipleSelection: [],
      operateType: 1,
      operateOptions: [
        {
          label: "批量删除",
          value: 1
        }
      ],
      detailDialogVisible: false,
      selectedOrder: null
    }
  },
  created() {
    this.getList()
  },
  filters: {
    formatCreateTime(time) {
      if (!time) return '-'
      let date = new Date(time)
      return formatDate(date, 'yyyy-MM-dd hh:mm:ss')
    },
    formatPayTime(time) {
      if (!time) return '-'
      let date = new Date(time)
      return formatDate(date, 'yyyy-MM-dd hh:mm:ss')
    },
    formatPayType(value) {
      if (value === 1) {
        return '支付宝'
      } else if (value === 2) {
        return '微信'
      } else if (value === 3) {
        return '余额支付'
      } else {
        return '未支付'
      }
    },
    formatStatus(value) {
      if (value === 0) {
        return '待支付'
      } else if (value === 1) {
        return '支付成功'
      } else if (value === 2) {
        return '支付失败'
      } else if (value === 3) {
        return '已取消'
      } else {
        return '未知状态'
      }
    }
  },
  methods: {
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    handleResetSearch() {
      this.listQuery = Object.assign({}, defaultListQuery)
    },
    handleSearchList() {
      this.listQuery.pageNum = 1
      this.getList()
    },
    handleViewDetail(index, row) {
      this.selectedOrder = row
      this.detailDialogVisible = true
    },
    handleBatchOperate() {
      if (this.multipleSelection == null || this.multipleSelection.length < 1) {
        this.$message({
          message: '请选择要操作的充值订单',
          type: 'warning',
          duration: 1000
        })
        return
      }
      if (this.operateType === 1) {
        // 批量删除
        this.$confirm('是否要进行删除操作?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          let ids = []
          for (let i = 0; i < this.multipleSelection.length; i++) {
            ids.push(this.multipleSelection[i].id)
          }
          this.deleteOrders(ids)
        })
      }
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
    getList() {
      this.listLoading = true
      fetchRechargeOrderList(this.listQuery).then(response => {
        this.listLoading = false
        this.list = response.data.list
        this.total = response.data.total
      }).catch(() => {
        this.listLoading = false
      })
    },
    deleteOrders(ids) {
      deleteRechargeOrder(ids).then(response => {
        this.$message({
          message: '删除成功！',
          type: 'success',
          duration: 1000
        })
        this.getList()
      })
    },
    handleExport() {
      this.$confirm('确认导出当前筛选条件下的充值订单数据？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message({
          message: '正在导出，请稍候...',
          type: 'info'
        })
        exportRechargeOrder(this.listQuery).then(response => {
          const disposition = response.headers['content-disposition']
          let fileName = 'recharge_order_export_' + Date.now() + '.csv'
          if (disposition && disposition.includes('filename=')) {
            fileName = decodeURIComponent(disposition.split('filename=')[1])
          }
          const blob = new Blob([response.data], { type: 'text/csv;charset=utf-8' })
          const url = window.URL.createObjectURL(blob)
          const link = document.createElement('a')
          link.href = url
          link.download = fileName
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link)
          window.URL.revokeObjectURL(url)
          this.$message({
            message: '导出成功！',
            type: 'success'
          })
        })
      })
    }
  }
}
</script>

<style scoped>
.input-width {
  width: 203px;
}
</style> 