<template>
  <div class="member-table-view">
    <!-- 总体统计 -->
    <el-card class="summary-card" shadow="hover">
      <div slot="header">
        <span>总体统计</span>
      </div>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="新增会员数">
          <span class="value-text new">{{ data.newMemberCount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="总活跃会员数">
          <span class="value-text active">{{ data.totalActiveMembers }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="会员增长率">
          <span class="value-text" :class="getGrowthClass(data.growthRate)">
            {{ data.growthRate }}%
          </span>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
    
    <!-- 会员消费排行榜表格 -->
    <el-card class="ranking-table-card" shadow="hover">
      <div slot="header">
        <span>会员消费排行榜</span>
      </div>
      <el-table
        :data="data.topSpenders"
        stripe
        border
        style="width: 100%"
        :default-sort="{prop: 'totalSpending', order: 'descending'}"
      >
        <el-table-column
          type="index"
          label="排名"
          width="80"
          align="center"
        >
          <template slot-scope="scope">
            <el-tag 
              :type="getRankTagType(scope.$index)" 
              effect="dark"
              size="medium"
            >
              {{ scope.$index + 1 }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column
          prop="memberName"
          label="会员名称"
          width="150"
        />
        
        <el-table-column
          prop="memberCode"
          label="会员编号"
          width="150"
        />
        
        <el-table-column
          prop="totalSpending"
          label="总消费金额"
          sortable
          :sort-method="(a, b) => parseFloat(a.totalSpending) - parseFloat(b.totalSpending)"
        >
          <template slot-scope="scope">
            <span class="spending-value">¥{{ formatNumber(scope.row.totalSpending) }}</span>
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
          label="平均订单金额"
          width="150"
        >
          <template slot-scope="scope">
            <span class="avg-value">
              ¥{{ calculateAvgOrder(scope.row.totalSpending, scope.row.orderCount) }}
            </span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 小程序访问概况 -->
    <el-card v-if="data.wechatVisitData && data.wechatVisitData.visitSummary" class="visit-summary-card" shadow="hover">
      <div slot="header">
        <span>小程序访问概况</span>
      </div>
      <el-descriptions :column="4" border>
        <el-descriptions-item label="总访问用户数">
          <span class="value-text visit">{{ data.wechatVisitData.visitSummary.totalVisitUv || 0 }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="总访问次数">
          <span class="value-text visit">{{ data.wechatVisitData.visitSummary.totalVisitPv || 0 }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="新增用户数">
          <span class="value-text visit">{{ data.wechatVisitData.visitSummary.newUserCount || 0 }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="平均访问深度">
          <span class="value-text visit">{{ (data.wechatVisitData.visitSummary.avgVisitDepth || 0).toFixed(2) }}</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 访问留存数据表格 -->
    <el-card v-if="data.wechatVisitData && data.wechatVisitData.visitRetain && data.wechatVisitData.visitRetain.length > 0" class="retain-table-card" shadow="hover">
      <div slot="header">
        <span>访问留存分析</span>
      </div>
      <el-table
        :data="data.wechatVisitData.visitRetain"
        stripe
        border
        style="width: 100%"
      >
        <el-table-column
          prop="period"
          label="周期"
          width="150"
        />
        
        <el-table-column
          prop="newUser"
          label="新增用户数"
          width="150"
        >
          <template slot-scope="scope">
            <el-tag type="success">{{ scope.row.newUser }}</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column
          prop="retainUser"
          label="留存用户数"
          width="150"
        >
          <template slot-scope="scope">
            <el-tag type="primary">{{ scope.row.retainUser }}</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column
          prop="retainRate"
          label="留存率(%)"
          width="150"
        >
          <template slot-scope="scope">
            <span :class="getRetainRateClass(scope.row.retainRate)">
              {{ scope.row.retainRate.toFixed(2) }}%
            </span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 会员增长趋势表格 -->
    <el-card v-if="data.memberTrendData && data.memberTrendData.length > 0" class="trend-table-card" shadow="hover">
      <div slot="header">
        <span>会员增长趋势</span>
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
          sortable
        />
        
        <el-table-column
          prop="newMembers"
          label="新增会员数"
          sortable
        >
          <template slot-scope="scope">
            <el-tag type="success">{{ scope.row.newMembers }}</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column
          prop="activeMembers"
          label="活跃会员数"
          sortable
        >
          <template slot-scope="scope">
            <el-tag type="primary">{{ scope.row.activeMembers }}</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column
          label="新增环比"
        >
          <template slot-scope="scope">
            <span :class="getNewMemberTrendClass(scope.$index)">
              {{ getNewMemberTrendText(scope.$index) }}
            </span>
          </template>
        </el-table-column>
        
        <el-table-column
          label="活跃环比"
        >
          <template slot-scope="scope">
            <span :class="getActiveMemberTrendClass(scope.$index)">
              {{ getActiveMemberTrendText(scope.$index) }}
            </span>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <el-pagination
        v-if="data.memberTrendData.length > pageSize"
        class="pagination"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="data.memberTrendData.length"
      />
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'MemberTableView',
  
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
    /**
     * 分页后的趋势数据
     */
    paginatedTrendData() {
      if (!this.data.memberTrendData) return []
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      return this.data.memberTrendData.slice(start, end)
    }
  },
  
  methods: {
    /**
     * 获取增长率样式类
     */
    getGrowthClass(growthRate) {
      const rate = parseFloat(growthRate || 0)
      if (rate > 0) return 'growth-positive'
      if (rate < 0) return 'growth-negative'
      return 'growth-neutral'
    },
    
    /**
     * 获取排名标签类型
     */
    getRankTagType(index) {
      if (index === 0) return 'danger'
      if (index === 1) return 'warning'
      if (index === 2) return 'success'
      return 'info'
    },
    
    /**
     * 计算平均订单金额
     */
    calculateAvgOrder(totalSpending, orderCount) {
      if (!orderCount || orderCount === 0) return '0.00'
      const avg = parseFloat(totalSpending) / orderCount
      return avg.toFixed(2)
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
     * 获取新增会员趋势文本
     */
    getNewMemberTrendText(index) {
      if (!this.data.memberTrendData || index >= this.data.memberTrendData.length - 1) {
        return '-'
      }
      
      const current = this.data.memberTrendData[index].newMembers
      const previous = this.data.memberTrendData[index + 1].newMembers
      
      if (previous === 0) return '-'
      
      const change = ((current - previous) / previous * 100).toFixed(2)
      return change > 0 ? `+${change}%` : `${change}%`
    },
    
    /**
     * 获取新增会员趋势样式类
     */
    getNewMemberTrendClass(index) {
      if (!this.data.memberTrendData || index >= this.data.memberTrendData.length - 1) {
        return ''
      }
      
      const current = this.data.memberTrendData[index].newMembers
      const previous = this.data.memberTrendData[index + 1].newMembers
      
      if (current > previous) return 'trend-up'
      if (current < previous) return 'trend-down'
      return 'trend-equal'
    },
    
    /**
     * 获取活跃会员趋势文本
     */
    getActiveMemberTrendText(index) {
      if (!this.data.memberTrendData || index >= this.data.memberTrendData.length - 1) {
        return '-'
      }
      
      const current = this.data.memberTrendData[index].activeMembers
      const previous = this.data.memberTrendData[index + 1].activeMembers
      
      if (previous === 0) return '-'
      
      const change = ((current - previous) / previous * 100).toFixed(2)
      return change > 0 ? `+${change}%` : `${change}%`
    },
    
    /**
     * 获取活跃会员趋势样式类
     */
    getActiveMemberTrendClass(index) {
      if (!this.data.memberTrendData || index >= this.data.memberTrendData.length - 1) {
        return ''
      }
      
      const current = this.data.memberTrendData[index].activeMembers
      const previous = this.data.memberTrendData[index + 1].activeMembers
      
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
     * 获取留存率样式类
     */
    getRetainRateClass(rate) {
      if (rate > 100) return 'retain-high'
      if (rate > 50) return 'retain-medium'
      return 'retain-low'
    },
    
    /**
     * 导出趋势数据
     */
    exportTrendData() {
      const headers = ['日期', '新增会员数', '活跃会员数']
      const rows = this.data.memberTrendData.map(item => [
        item.date,
        item.newMembers,
        item.activeMembers
      ])
      
      let csvContent = headers.join(',') + '\n'
      rows.forEach(row => {
        csvContent += row.join(',') + '\n'
      })
      
      const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
      const link = document.createElement('a')
      link.href = URL.createObjectURL(blob)
      link.download = `会员数据趋势_${new Date().toISOString().split('T')[0]}.csv`
      link.click()
    }
  }
}
</script>

<style scoped>
.member-table-view {
  padding: 20px;
}

.summary-card,
.ranking-table-card,
.trend-table-card {
  margin-bottom: 20px;
}

.value-text {
  font-size: 18px;
  font-weight: bold;
}

.value-text.new {
  color: #67c23a;
}

.value-text.active {
  color: #409eff;
}

.growth-positive {
  color: #67c23a;
}

.growth-negative {
  color: #f56c6c;
}

.growth-neutral {
  color: #909399;
}

.spending-value {
  color: #67c23a;
  font-weight: bold;
}

.avg-value {
  color: #e6a23c;
  font-weight: 500;
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

.visit-summary-card,
.retain-table-card {
  margin-bottom: 20px;
}

.value-text.visit {
  color: #409eff;
  font-size: 18px;
  font-weight: bold;
}

.retain-high {
  color: #67c23a;
  font-weight: bold;
}

.retain-medium {
  color: #e6a23c;
  font-weight: bold;
}

.retain-low {
  color: #f56c6c;
  font-weight: bold;
}
</style>
