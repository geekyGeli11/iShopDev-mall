<template>
  <div class="product-fullscreen-view">
    <div ref="chart" class="fullscreen-chart"></div>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'ProductFullscreenView',
  props: { data: { type: Object, default: () => ({}) } },
  data() { return { chart: null } },
  mounted() { this.$nextTick(() => this.initChart()) },
  beforeDestroy() { if (this.chart) { this.chart.dispose(); this.chart = null } },
  watch: { data: { handler() { this.$nextTick(() => this.updateChart()) }, deep: true } },
  methods: {
    initChart() {
      if (!this.$refs.chart || !this.data || !this.data.categoryDistribution) return
      this.chart = echarts.init(this.$refs.chart, 'dark')
      const chartData = this.data.categoryDistribution.map(c => ({ name: c.category, value: parseFloat(c.salesAmount) }))
      this.chart.setOption({
        tooltip: { trigger: 'item', backgroundColor: 'rgba(0,0,0,0.7)', borderColor: '#4facfe', textStyle: { color: '#fff' } },
        legend: { orient: 'vertical', left: 'left', textStyle: { color: '#8c9db5' } },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: { borderRadius: 10, borderColor: '#0a0e27', borderWidth: 2, shadowBlur: 10, shadowColor: 'rgba(79, 172, 254, 0.3)' },
          label: { show: false },
          emphasis: { label: { show: true, fontSize: '20', fontWeight: 'bold', color: '#fff' } },
          data: chartData,
          animationType: 'scale',
          animationEasing: 'elasticOut'
        }],
        color: ['#4facfe', '#00f2fe', '#43e97b', '#38f9d7', '#fa709a', '#fee140']
      })
      window.addEventListener('resize', this.handleResize)
    },
    updateChart() { if (this.chart) { this.chart.dispose(); this.initChart() } },
    handleResize() { if (this.chart) this.chart.resize() }
  }
}
</script>

<style scoped>
.product-fullscreen-view { width: 100%; height: 100%; }
.fullscreen-chart { width: 100%; height: 100%; }
</style>
