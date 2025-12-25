<template>
  <div class="consumption-history-tab">
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
      <el-form-item label="业务类型：">
        <el-select v-model="listQuery.businessType" placeholder="全部" clearable>
          <el-option v-for="item in businessTypeOptions"
                     :key="item.value"
                     :label="item.label"
                     :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="变动类型：">
        <el-select v-model="listQuery.changeType" placeholder="全部" clearable>
          <el-option v-for="item in changeTypeOptions"
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
      <el-table-column label="业务类型" prop="businessType" width="120" align="center">
        <template slot-scope="scope">
          {{ formatBusinessType(scope.row.businessType) }}
        </template>
      </el-table-column>
      <el-table-column label="变动类型" prop="changeType" width="100" align="center">
        <template slot-scope="scope">
          <el-tag :type="getChangeTypeColor(scope.row.changeType)">
            {{ formatChangeType(scope.row.changeType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="变动金额" prop="amount" width="120" align="center">
        <template slot-scope="scope">
          <span :class="{'amount-positive': scope.row.amount > 0, 'amount-negative': scope.row.amount < 0}">
            {{ scope.row.amount > 0 ? '+' : '' }}{{ scope.row.amount }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="变动前余额" prop="balanceBefore" width="120" align="center">
        <template slot-scope="scope">
          ￥{{ scope.row.balanceBefore }}
        </template>
      </el-table-column>
      <el-table-column label="变动后余额" prop="balanceAfter" width="120" align="center">
        <template slot-scope="scope">
          ￥{{ scope.row.balanceAfter }}
        </template>
      </el-table-column>
      <el-table-column label="关联业务ID" prop="businessId" width="140" align="center">
        <template slot-scope="scope">
          {{ scope.row.businessId || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="变动时间" prop="createTime" width="160" align="center">
        <template slot-scope="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作人" prop="operator" width="100" align="center">
        <template slot-scope="scope">
          {{ scope.row.operator || '系统' }}
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" align="center">
        <template slot-scope="scope">
          {{ scope.row.remark || '-' }}
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
import { getMemberConsumptionHistory } from '@/api/member'
import { formatDate } from '@/utils/index'

export default {
  name: 'ConsumptionHistoryTab',
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
        businessType: null,
        changeType: null,
        startDate: null,
        endDate: null
      },
      businessTypeOptions: [
        { label: '订单消费', value: 'order' },
        { label: '充值', value: 'recharge' },
        { label: '充值赠送', value: 'recharge_bonus' },
        { label: '退款', value: 'refund' },
        { label: '转账转入', value: 'transfer_in' },
        { label: '转账转出', value: 'transfer_out' },
        { label: '系统调整', value: 'admin_adjust' }
      ],
      changeTypeOptions: [
        { label: '充值', value: 1 },
        { label: '消费', value: 2 },
        { label: '退款', value: 3 },
        { label: '转入', value: 4 },
        { label: '转出', value: 5 }
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
      getMemberConsumptionHistory(this.listQuery).then(response => {
        this.loading = false
        if (response.code === 200) {
          this.list = response.data.list || []
          this.total = response.data.total || 0
        } else {
          this.$message.error(response.message || '获取消费记录失败')
        }
      }).catch(error => {
        this.loading = false
        console.error('获取消费记录失败:', error)
        this.$message.error('获取消费记录失败')
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
        businessType: null,
        changeType: null,
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
    
    formatBusinessType(businessType) {
      const typeMap = {
        'order': '订单消费',
        'recharge': '充值',
        'recharge_bonus': '充值赠送',
        'refund': '退款',
        'transfer_in': '转账转入',
        'transfer_out': '转账转出',
        'admin_adjust': '系统调整'
      }
      return typeMap[businessType] || businessType || '-'
    },
    
    formatChangeType(changeType) {
      const typeMap = {
        1: '充值',
        2: '消费',
        3: '退款',
        4: '转入',
        5: '转出'
      }
      return typeMap[changeType] || '未知'
    },
    
    getChangeTypeColor(changeType) {
      const colorMap = {
        1: 'success',  // 充值
        2: 'danger',   // 消费
        3: 'warning',  // 退款
        4: 'success',  // 转入
        5: 'info'      // 转出
      }
      return colorMap[changeType] || ''
    }
  }
}
</script>

<style scoped>
.consumption-history-tab {
  padding: 20px;
}

.pagination-container {
  margin-top: 20px;
  text-align: center;
}

.amount-positive {
  color: #67C23A;
  font-weight: bold;
}

.amount-negative {
  color: #F56C6C;
  font-weight: bold;
}
</style> 