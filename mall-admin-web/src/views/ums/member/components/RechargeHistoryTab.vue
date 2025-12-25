<template>
  <div class="recharge-history-tab">
    <!-- 筛选条件 -->
    <el-form :inline="true" :model="listQuery" size="small">
      <el-form-item label="时间范围：">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
          @change="handleDateChange">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="订单状态：">
        <el-select v-model="listQuery.status" placeholder="全部" clearable>
          <el-option v-for="item in statusOptions"
                     :key="item.value"
                     :label="item.label"
                     :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 数据表格 -->
    <el-table
      :data="list"
      style="width: 100%"
      v-loading="loading"
      border
      stripe>
      <el-table-column label="订单编号" prop="orderSn" width="180"></el-table-column>
      <el-table-column label="充值金额" prop="amount" width="120" align="center">
        <template slot-scope="scope">
          ￥{{ scope.row.amount }}
        </template>
      </el-table-column>
      <el-table-column label="支付方式" prop="payType" width="100" align="center">
        <template slot-scope="scope">
          {{ formatPayType(scope.row.payType) }}
        </template>
      </el-table-column>
      <el-table-column label="订单状态" prop="status" width="100" align="center">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ formatStatus(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="160" align="center">
        <template slot-scope="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="支付时间" prop="payTime" width="160" align="center">
        <template slot-scope="scope">
          {{ formatDate(scope.row.payTime) }}
        </template>
      </el-table-column>
      <el-table-column label="第三方流水号" prop="paySn" width="140" align="center">
        <template slot-scope="scope">
          {{ scope.row.paySn || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="note" align="center">
        <template slot-scope="scope">
          {{ scope.row.note || '-' }}
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        layout="total, sizes, prev, pager, next"
        :current-page.sync="listQuery.pageNum"
        :page-size="listQuery.pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import { getMemberRechargeHistory } from '@/api/rechargeOrder'
import { formatDate } from '@/utils/index'

export default {
  name: 'RechargeHistoryTab',
  props: {
    memberId: {
      type: [String, Number],
      required: true
    }
  },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      dateRange: [],
      listQuery: {
        memberId: null,
        pageNum: 1,
        pageSize: 10,
        status: null,
        startDate: null,
        endDate: null
      },
      statusOptions: [
        { label: '待支付', value: 0 },
        { label: '支付成功', value: 1 },
        { label: '支付失败', value: 2 },
        { label: '已取消', value: 3 }
      ]
    }
  },
  watch: {
    memberId: {
      immediate: true,
      handler(val) {
        if (val) {
          this.listQuery.memberId = val
          this.loadData()
        }
      }
    }
  },
  methods: {
    loadData() {
      if (!this.listQuery.memberId) return
      
      this.loading = true
      getMemberRechargeHistory(this.listQuery).then(response => {
        this.loading = false
        if (response.code === 200) {
          this.list = response.data.list || []
          this.total = response.data.total || 0
        } else {
          this.$message.error(response.message || '获取充值记录失败')
        }
      }).catch(error => {
        this.loading = false
        console.error('获取充值记录失败:', error)
        this.$message.error('获取充值记录失败')
      })
    },
    
    handleSearch() {
      this.listQuery.pageNum = 1
      this.loadData()
    },
    
    handleReset() {
      this.listQuery = {
        memberId: this.memberId,
        pageNum: 1,
        pageSize: 10,
        status: null,
        startDate: null,
        endDate: null
      }
      this.dateRange = []
      this.loadData()
    },
    
    handleDateChange(dateRange) {
      if (dateRange && dateRange.length === 2) {
        this.listQuery.startDate = dateRange[0]
        this.listQuery.endDate = dateRange[1]
      } else {
        this.listQuery.startDate = null
        this.listQuery.endDate = null
      }
    },
    
    handleSizeChange(val) {
      this.listQuery.pageSize = val
      this.listQuery.pageNum = 1
      this.loadData()
    },
    
    handleCurrentChange(val) {
      this.listQuery.pageNum = val
      this.loadData()
    },
    
    formatDate(date) {
      return date ? formatDate(new Date(date), 'YYYY-MM-DD HH:mm:ss') : '-'
    },
    
    formatPayType(payType) {
      switch (payType) {
        case 1: return '支付宝'
        case 2: return '微信'
        case 3: return '余额支付'
        default: return '未支付'
      }
    },
    
    formatStatus(status) {
      switch (status) {
        case 0: return '待支付'
        case 1: return '支付成功'
        case 2: return '支付失败'
        case 3: return '已取消'
        default: return '未知状态'
      }
    },
    
    getStatusType(status) {
      switch (status) {
        case 0: return 'warning'
        case 1: return 'success'
        case 2: return 'danger'
        case 3: return 'info'
        default: return ''
      }
    }
  }
}
</script>

<style scoped>
.recharge-history-tab {
  padding: 20px;
}

.pagination-container {
  margin-top: 20px;
  text-align: center;
}
</style> 