<template>
  <div class="product-chart-view">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <div slot="header"><span>Top商品销售对比</span></div>
          <div ref="topProductsChart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <div slot="header"><span>品类分布</span></div>
          <div ref="categoryChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'ProductChartView',
  props: { data: { type: Object, required: true } },
  data() { return { topProductsChart: null, categoryChart: null } },
  mounted() {
    window.addEventListener('resize', this.handleResize)
    if (this.data) {
      this.$nextTick(() => this.initCharts())
    }
  },
  beforeDestroy() { this.destroyCharts() },
  watch: {
    data: {
      handler(newData) {
        if (!newData) return
        this.$nextTick(() => {
          if (!this.topProductsChart && !this.categoryChart) {
            this.initCharts()
          } else {
            this.initTopProductsChart()
            this.initCategoryChart()
          }
        })
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
    initCharts() {
      this.initTopProductsChart()
      this.initCategoryChart()
    },
    initTopProductsChart() {
      if (!this.$refs.topProductsChart) return
      if (!this.topProductsChart) {
        this.topProductsChart = echarts.init(this.$refs.topProductsChart)
      }
      if (!this.data.topSellingByAmount || this.data.topSellingByAmount.length === 0) {
        this.topProductsChart.setOption({
          title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#999', fontSize: 14 } }
        })
        return
      }
      const products = this.data.topSellingByAmount.slice(0, 10).map(p => p.productName)
      const amounts = this.data.topSellingByAmount.slice(0, 10).map(p => parseFloat(p.salesAmount))
      this.topProductsChart.setOption({
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: products, axisLabel: { rotate: 30 } },
        yAxis: { type: 'value', name: '销售金额(元)' },
        series: [{ type: 'bar', data: amounts, itemStyle: { color: '#5470c6' } }]
      }, true)
    },
    initCategoryChart() {
      if (!this.$refs.categoryChart) return
      if (!this.categoryChart) {
        this.categoryChart = echarts.init(this.$refs.categoryChart)
      }
      if (!this.data.categoryDistribution || this.data.categoryDistribution.length === 0) {
        this.categoryChart.setOption({
          title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#999', fontSize: 14 } }
        })
        return
      }
      const chartData = this.data.categoryDistribution.map(c => ({ name: c.category, value: parseFloat(c.salesAmount) }))
      this.categoryChart.setOption({
        tooltip: { trigger: 'item' },
        series: [{ type: 'pie', radius: '50%', data: chartData }]
      }, true)
    },
    destroyCharts() {
      window.removeEventListener('resize', this.handleResize)
      if (this.topProductsChart) { this.topProductsChart.dispose(); this.topProductsChart = null }
      if (this.categoryChart) { this.categoryChart.dispose(); this.categoryChart = null }
    },
    handleResize() {
      if (this.topProductsChart) this.topProductsChart.resize()
      if (this.categoryChart) this.categoryChart.resize()
    }
  }
}
</script>

<style scoped>
.product-chart-view { padding: 20px; }
.chart-card { min-height: 450px; }
.chart-container { width: 100%; height: 400px; min-height: 400px; }
</style>
