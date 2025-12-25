<template>
  <div class="business-dashboard">
    <!-- 加载状态 -->
    <div v-if="loading" v-loading="true" element-loading-text="正在加载营业数据..." class="loading-container">
    </div>
    
    <!-- 错误状态 -->
    <div v-else-if="error" class="error-container">
      <el-alert
        title="数据加载失败"
        type="error"
        :description="error"
        show-icon
        :closable="false"
      >
        <el-button type="primary" size="small" @click="handleRetry">
          重试
        </el-button>
      </el-alert>
    </div>
    
    <!-- 空数据状态 -->
    <div v-else-if="!data || !hasData" class="empty-container">
      <el-empty description="暂无数据">
        <el-button type="primary" @click="handleRetry">刷新数据</el-button>
      </el-empty>
    </div>
    
    <!-- 数据展示 -->
    <div v-else class="dashboard-content">
      <!-- 顶部统计卡片 -->
      <el-row :gutter="20" class="statistics-cards" style="margin-bottom: 20px;">
        <el-col :span="6">
          <el-card shadow="hover" class="statistic-card">
            <div class="card-content">
              <div class="statistic-title">今日订单总数</div>
              <div class="statistic-value">{{ homeStatistics.todayOrderCount || 0 }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="statistic-card">
            <div class="card-content">
              <div class="statistic-title">今日销售总额</div>
              <div class="statistic-value">¥{{ (homeStatistics.todaySaleAmount || 0).toFixed(2) }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="statistic-card">
            <div class="card-content">
              <div class="statistic-title">昨日销售总额</div>
              <div class="statistic-value">¥{{ (homeStatistics.yesterdaySaleAmount || 0).toFixed(2) }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="statistic-card">
            <div class="card-content">
              <div class="statistic-title">近7天销售总额</div>
              <div class="statistic-value">¥{{ (homeStatistics.last7DaysSaleAmount || 0).toFixed(2) }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <!-- 待处理事务面板 -->
      <el-card shadow="hover" class="pending-tasks" style="margin-bottom: 20px;">
        <div slot="header" class="panel-header">
          <span>待处理事务</span>
          <el-button 
            type="text" 
            icon="el-icon-refresh" 
            size="small"
            :loading="refreshing"
            @click="handleRefreshCache"
            style="float: right; padding: 3px 0;">
            刷新数据
          </el-button>
        </div>
        <el-row :gutter="20">
          <el-col :span="8">
            <div class="task-item" @click="navigateToOrders(0)">
              <span>待付款订单</span>
              <el-tag type="danger" size="small">{{ homeStatistics.pendingPaymentCount || 0 }}</el-tag>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="task-item" @click="navigateToOrders(1)">
              <span>待发货订单</span>
              <el-tag type="info" size="small">{{ homeStatistics.pendingDeliveryCount || 0 }}</el-tag>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="task-item" @click="navigateToOrders(2)">
              <span>待确认收货订单</span>
              <el-tag type="warning" size="small">{{ homeStatistics.pendingConfirmCount || 0 }}</el-tag>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="20" style="margin-top: 15px;">
          <el-col :span="8">
            <div class="task-item" @click="navigateToOrders(3)">
              <span>已完成订单</span>
              <el-tag type="success" size="small">{{ homeStatistics.completedOrderCount || 0 }}</el-tag>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="task-item" @click="navigateToStock()">
              <span>新缺货登记</span>
              <el-tag type="danger" size="small">{{ homeStatistics.newSourceCount || 0 }}</el-tag>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="task-item" @click="navigateToRefund()">
              <span>待处理退款申请</span>
              <el-tag type="warning" size="small">{{ homeStatistics.pendingRefundCount || 0 }}</el-tag>
            </div>
          </el-col>
        </el-row>
      </el-card>
      
      <!-- 卡片视图 -->
      <div v-if="viewMode === 'card'" class="card-view">
        <business-card-view :data="data" />
      </div>
      
      <!-- 表格视图 -->
      <div v-else-if="viewMode === 'table'" class="table-view">
        <business-table-view :data="data" />
      </div>
      
      <!-- 图表视图 -->
      <div v-else-if="viewMode === 'chart'" class="chart-view">
        <business-chart-view :data="data" />
      </div>
    </div>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex'
import BusinessCardView from './BusinessCardView.vue'
import BusinessTableView from './BusinessTableView.vue'
import BusinessChartView from './BusinessChartView.vue'
import { getHomeStatistics, refreshStatisticsCache } from '@/api/home'

export default {
  name: 'BusinessDashboard',
  
  components: {
    BusinessCardView,
    BusinessTableView,
    BusinessChartView
  },
  
  props: {
    viewMode: {
      type: String,
      default: 'card',
      validator: (value) => ['card', 'table', 'chart'].includes(value)
    }
  },
  
  data() {
    return {
      error: null,
      refreshing: false,
      homeStatistics: {
        todayOrderCount: 0,
        todaySaleAmount: 0,
        yesterdaySaleAmount: 0,
        last7DaysSaleAmount: 0,
        pendingPaymentCount: 0,
        pendingDeliveryCount: 0,
        pendingConfirmCount: 0,
        completedOrderCount: 0,
        newSourceCount: 0,
        pendingRefundCount: 0
      }
    }
  },
  
  computed: {
    ...mapState('dashboard', {
      data: state => state.businessData,
      loading: state => state.loading.business
    }),
    
    /**
     * 检查是否有数据
     */
    hasData() {
      // 只要数据对象存在就显示，即使数值为 0
      return this.data !== null && this.data !== undefined
    }
  },
  
  mounted() {
    this.loadData()
    this.loadHomeStatistics()
  },
  
  methods: {
    ...mapActions('dashboard', ['fetchBusinessData']),
    
    /**
     * 加载数据
     */
    async loadData() {
      try {
        this.error = null
        await this.fetchBusinessData()
      } catch (error) {
        console.error('加载营业数据失败:', error)
        this.error = error.message || '加载数据失败，请稍后重试'
      }
    },
    
    /**
     * 重试加载
     */
    handleRetry() {
      this.loadData()
      this.loadHomeStatistics()
    },
    
    /**
     * 加载首页统计数据
     */
    async loadHomeStatistics() {
      try {
        const response = await getHomeStatistics()
        if (response && response.code === 200) {
          this.homeStatistics = response.data || {}
        }
      } catch (error) {
        console.error('加载首页统计数据失败:', error)
      }
    },
    
    /**
     * 导航到订单列表
     */
    navigateToOrders(status) {
      this.$router.push({
        path: '/oms/order',
        query: { status }
      })
    },
    
    /**
     * 导航到库存管理
     */
    navigateToStock() {
      this.$router.push('/pms/stock')
    },
    
    /**
     * 导航到退款申请
     */
    navigateToRefund() {
      this.$router.push('/oms/returnApply')
    },
    
    /**
     * 刷新缓存并重新加载数据
     */
    async handleRefreshCache() {
      try {
        this.refreshing = true
        await refreshStatisticsCache()
        await this.loadHomeStatistics()
        this.$message.success('数据已刷新')
      } catch (error) {
        console.error('刷新缓存失败:', error)
        this.$message.error('刷新失败，请稍后重试')
      } finally {
        this.refreshing = false
      }
    }
  }
}
</script>

<style scoped>
.business-dashboard {
  min-height: 400px;
}

.loading-container,
.error-container,
.empty-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
  padding: 40px;
}

.dashboard-content {
  padding: 20px 0;
}

.card-view,
.table-view,
.chart-view {
  width: 100%;
}

/* 统计卡片样式 */
.statistics-cards .statistic-card {
  border-radius: 8px;
  transition: all 0.3s;
}

.statistics-cards .statistic-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.statistics-cards .card-content {
  padding: 10px;
}

.statistics-cards .statistic-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.statistics-cards .statistic-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

/* 待处理事务样式 */
.pending-tasks .panel-header {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.pending-tasks .task-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f5f7fa;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.pending-tasks .task-item:hover {
  background: #e6f7ff;
  transform: translateX(5px);
}

.pending-tasks .task-item span {
  font-size: 14px;
  color: #606266;
}
</style>
