<template>
  <div class="product-dashboard">
    <!-- 加载状态 -->
    <div v-if="loading" v-loading="true" element-loading-text="正在加载商品销售数据..." class="loading-container">
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
      <!-- 卡片视图 -->
      <div v-if="viewMode === 'card'" class="card-view">
        <product-card-view :data="data" />
      </div>
      
      <!-- 表格视图 -->
      <div v-else-if="viewMode === 'table'" class="table-view">
        <product-table-view :data="data" />
      </div>
      
      <!-- 图表视图 -->
      <div v-else-if="viewMode === 'chart'" class="chart-view">
        <product-chart-view :data="data" />
      </div>
    </div>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex'
import ProductCardView from './ProductCardView.vue'
import ProductTableView from './ProductTableView.vue'
import ProductChartView from './ProductChartView.vue'

export default {
  name: 'ProductDashboard',
  
  components: {
    ProductCardView,
    ProductTableView,
    ProductChartView
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
      error: null
    }
  },
  
  computed: {
    ...mapState('dashboard', {
      data: state => state.productData,
      loading: state => state.loading.product
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
  },
  
  methods: {
    ...mapActions('dashboard', ['fetchProductData']),
    
    /**
     * 加载数据
     */
    async loadData() {
      try {
        this.error = null
        await this.fetchProductData()
      } catch (error) {
        console.error('加载商品销售数据失败:', error)
        this.error = error.message || '加载数据失败，请稍后重试'
      }
    },
    
    /**
     * 重试加载
     */
    handleRetry() {
      this.loadData()
    }
  }
}
</script>

<style scoped>
.product-dashboard {
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
</style>
