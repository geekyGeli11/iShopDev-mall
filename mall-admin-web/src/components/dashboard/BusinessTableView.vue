<template>
  <div class="business-table-view">
    <!-- 总体统计 -->
    <el-card class="summary-card" shadow="hover">
      <div slot="header">
        <span>总体统计</span>
      </div>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="总收入">
          <span class="value-text revenue">¥{{ formatNumber(data.totalRevenue) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="订单数">
          <span class="value-text orders">{{ data.orderCount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="平均订单金额">
          <span class="value-text avg">¥{{ formatNumber(data.avgOrderValue) }}</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
    
    <!-- 销售渠道细分表格 -->
    <el-card class="channel-table-card" shadow="hover">
      <div slot="header">
        <span>销售渠道细分</span>
      </div>
      <el-table
        :data="data.channelBreakdown"
        stripe
        border
        style="width: 100%"
        :default-sort="{prop: 'revenue', order: 'descending'}"
      >
        <el-table-column
          prop="channel"
          label="渠道"
          width="180"
          sortable
        >
          <template slot-scope="scope">
            <el-tag :type="getChannelTagType(scope.row.channel)">
              {{ getChannelName(scope.row.channel) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column
          prop="revenue"
          label="收入金额"
          sortable
          :sort-method="(a, b) => parseFloat(a.revenue) - parseFloat(b.revenue)"
        >
          <template slot-scope="scope">
            <span class="revenue-value">¥{{ formatNumber(scope.row.revenue) }}</span>
          </template>
        </el-table-column>
        
        <el-table-column
          prop="orderCount"
          label="订单数"
          width="120"
          sortable
        >
          <template slot-scope="scope">
            <el-badge :value="scope.row.orderCount" class="order-badge" />
          </template>
        </el-table-column>
        
        <el-table-column
          prop="percentage"
          label="占比"
          width="150"
          sortable
          :sort-method="(a, b) => parseFloat(a.percentage) - parseFloat(b.percentage)"
        >
          <template slot-scope="scope">
            <el-progress 
              :percentage="parseFloat(scope.row.percentage)" 
              :color="getProgressColor(scope.row.percentage)"
            />
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 学校营业数据统计表格 - 仅超级管理员可见 -->
    <el-card v-if="isSuperAdmin && data.schoolStatistics && data.schoolStatistics.length > 0" class="school-table-card" shadow="hover">
      <div slot="header">
        <span>学校营业数据统计</span>
      </div>
      <el-table
        :data="data.schoolStatistics"
        stripe
        border
        style="width: 100%"
        :default-sort="{prop: 'totalAmount', order: 'descending'}"
      >
        <el-table-column
          prop="schoolName"
          label="学校名称"
          width="200"
        />
        
        <el-table-column
          prop="totalAmount"
          label="营业金额"
          sortable
          :sort-method="(a, b) => parseFloat(a.totalAmount) - parseFloat(b.totalAmount)"
        >
          <template slot-scope="scope">
            <span class="revenue-value">¥{{ formatNumber(scope.row.totalAmount) }}</span>
          </template>
        </el-table-column>
        
        <el-table-column
          prop="orderCount"
          label="订单数"
          width="120"
          sortable
        >
          <template slot-scope="scope">
            <el-badge :value="scope.row.orderCount" class="order-badge" />
          </template>
        </el-table-column>
        
        <el-table-column
          prop="avgOrderAmount"
          label="平均订单金额"
          width="150"
          sortable
          :sort-method="(a, b) => parseFloat(a.avgOrderAmount) - parseFloat(b.avgOrderAmount)"
        >
          <template slot-scope="scope">
            <span>¥{{ formatNumber(scope.row.avgOrderAmount) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 门店营业数据统计表格 - 仅超级管理员可见 -->
    <el-card v-if="isSuperAdmin && data.storeStatistics && data.storeStatistics.length > 0" class="store-table-card" shadow="hover">
      <div slot="header">
        <span>门店营业数据统计</span>
      </div>
      <el-table
        :data="data.storeStatistics"
        stripe
        border
        style="width: 100%"
        :default-sort="{prop: 'totalAmount', order: 'descending'}"
      >
        <el-table-column
          prop="storeName"
          label="门店名称"
          width="200"
        />
        
        <el-table-column
          prop="totalAmount"
          label="营业金额"
          sortable
          :sort-method="(a, b) => parseFloat(a.totalAmount) - parseFloat(b.totalAmount)"
        >
          <template slot-scope="scope">
            <span class="revenue-value">¥{{ formatNumber(scope.row.totalAmount) }}</span>
          </template>
        </el-table-column>
        
        <el-table-column
          prop="orderCount"
          label="订单数"
          width="120"
          sortable
        >
          <template slot-scope="scope">
            <el-badge :value="scope.row.orderCount" class="order-badge" />
          </template>
        </el-table-column>
        
        <el-table-column
          prop="avgOrderAmount"
          label="平均订单金额"
          width="150"
          sortable
          :sort-method="(a, b) => parseFloat(a.avgOrderAmount) - parseFloat(b.avgOrderAmount)"
        >
          <template slot-scope="scope">
            <span>¥{{ formatNumber(scope.row.avgOrderAmount) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 趋势数据表格 -->
    <el-card v-if="data.trendData && data.trendData.length > 0" class="trend-table-card" shadow="hover">
      <div slot="header">
        <span>收入趋势</span>
        <el-button 
          style="float: right; padding: 3px 0" 
          type="text"
          @click="exportTrendData"
        >
          导出数据
        </el-button>
      </div>
      <el-table
        :data="paginatedTrendData"
        stripe
        border
        style="width: 100%"
        :default-sort="{prop: 'date', order: 'descending'}"
      >
        <el-table-column
          prop="date"
          label="日期"
          width="150"
          sortable
        />
        
        <el-table-column
          prop="revenue"
          label="收入金额"
          sortable
          :sort-method="(a, b) => parseFloat(a.revenue) - parseFloat(b.revenue)"
        >
          <template slot-scope="scope">
            <span class="revenue-value">¥{{ formatNumber(scope.row.revenue) }}</span>
          </template>
        </el-table-column>
        
        <el-table-column
          prop="orderCount"
          label="订单数"
          width="120"
          sortable
        >
          <template slot-scope="scope">
            <el-badge :value="scope.row.orderCount" class="order-badge" />
          </template>
        </el-table-column>
        
        <el-table-column
          label="日环比"
          width="150"
        >
          <template slot-scope="scope">
            <span :class="getTrendClass(scope.$index)">
              {{ getTrendText(scope.$index) }}
            </span>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <el-pagination
        v-if="data.trendData.length > pageSize"
        class="pagination"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="data.trendData.length"
      />
    </el-card>
  </div>
</template>

<script>
import { mapState } from 'vuex'

export default {
  name: 'BusinessTableView',
  
  props: {
    data: {
      type: Object,
      required: true
    }
  },
  
  data() {
    return {
      currentPage: 1,
      pageSize: 10
    }
  },
  
  computed: {
    ...mapState({
      username: state => state.user.name,
      roles: state => state.user.roles
    }),
    
    /**
     * 判断是否为超级管理员
     */
    isSuperAdmin() {
      return this.username === 'admin' || 
        (this.roles && this.roles.some(role => role === 'superadmin' || role === '超级管理员'))
    },
    
    /**
     * 分页后的趋势数据
     */
    paginatedTrendData() {
      if (!this.data.trendData) return []
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      return this.data.trendData.slice(start, end)
    }
  },
  
  methods: {
    /**
     * 获取渠道名称
     */
    getChannelName(channel) {
      const channelNames = {
        'miniprogram': '小程序订单',
        'self-checkout': '自助结算订单',
        'non-system': '其他销售收入',
        'pc': 'PC订单',
        'other': '其他订单'
      }
      return channelNames[channel] || channel
    },
    
    /**
     * 获取渠道标签类型
     */
    getChannelTagType(channel) {
      const types = {
        'miniprogram': 'primary',
        'self-checkout': 'warning',
        'non-system': 'success',
        'pc': 'info',
        'other': ''
      }
      return types[channel] || 'info'
    },
    
    /**
     * 获取进度条颜色
     */
    getProgressColor(percentage) {
      const value = parseFloat(percentage)
      if (value >= 50) return '#67c23a'
      if (value >= 30) return '#e6a23c'
      return '#f56c6c'
    },
    
    /**
     * 格式化数字
     */
    formatNumber(value) {
      if (!value) return '0.00'
      return parseFloat(value).toLocaleString('zh-CN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      })
    },
    
    /**
     * 获取趋势文本
     */
    getTrendText(index) {
      if (!this.data.trendData || index >= this.data.trendData.length - 1) {
        return '-'
      }
      
      const current = parseFloat(this.data.trendData[index].revenue)
      const previous = parseFloat(this.data.trendData[index + 1].revenue)
      
      if (previous === 0) return '-'
      
      const change = ((current - previous) / previous * 100).toFixed(2)
      return change > 0 ? `+${change}%` : `${change}%`
    },
    
    /**
     * 获取趋势样式类
     */
    getTrendClass(index) {
      if (!this.data.trendData || index >= this.data.trendData.length - 1) {
        return ''
      }
      
      const current = parseFloat(this.data.trendData[index].revenue)
      const previous = parseFloat(this.data.trendData[index + 1].revenue)
      
      if (current > previous) return 'trend-up'
      if (current < previous) return 'trend-down'
      return 'trend-equal'
    },
    
    /**
     * 处理每页大小变化
     */
    handleSizeChange(val) {
      this.pageSize = val
      this.currentPage = 1
    },
    
    /**
     * 处理当前页变化
     */
    handleCurrentChange(val) {
      this.currentPage = val
    },
    
    /**
     * 导出趋势数据
     */
    exportTrendData() {
      // 简单的CSV导出
      const headers = ['日期', '收入金额', '订单数']
      const rows = this.data.trendData.map(item => [
        item.date,
        item.revenue,
        item.orderCount
      ])
      
      let csvContent = headers.join(',') + '\n'
      rows.forEach(row => {
        csvContent += row.join(',') + '\n'
      })
      
      const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
      const link = document.createElement('a')
      link.href = URL.createObjectURL(blob)
      link.download = `营业数据趋势_${new Date().toISOString().split('T')[0]}.csv`
      link.click()
    }
  }
}
</script>

<style scoped>
.business-table-view {
  padding: 20px;
}

.summary-card,
.channel-table-card,
.trend-table-card {
  margin-bottom: 20px;
}

.value-text {
  font-size: 18px;
  font-weight: bold;
}

.value-text.revenue {
  color: #67c23a;
}

.value-text.orders {
  color: #409eff;
}

.value-text.avg {
  color: #e6a23c;
}

.revenue-value {
  color: #67c23a;
  font-weight: bold;
}

.order-badge {
  margin-left: 10px;
}

.trend-up {
  color: #67c23a;
  font-weight: bold;
}

.trend-down {
  color: #f56c6c;
  font-weight: bold;
}

.trend-equal {
  color: #909399;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
