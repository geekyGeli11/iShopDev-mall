<template>
  <div class="business-fullscreen-view">
    <div ref="chart" class="fullscreen-chart"></div>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'BusinessFullscreenView',
  props: { data: { type: Object, default: () => ({}) } },
  data() { return { chart: null } },
  mounted() { this.$nextTick(() => this.initChart()) },
  beforeDestroy() { if (this.chart) { this.chart.dispose(); this.chart = null } },
  watch: { data: { handler() { this.$nextTick(() => this.updateChart()) }, deep: true } },
  methods: {
    initChart() {
      if (!this.$refs.chart || !this.data) return
      this.chart = echarts.init(this.$refs.chart, 'dark')
      const dates = (this.data.trendData && this.data.trendData.map(item => item.date)) || []
      const revenues = (this.data.trendData && this.data.trendData.map(item => parseFloat(item.revenue))) || []
      this.chart.setOption({
        tooltip: { trigger: 'axis', backgroundColor: 'rgba(0,0,0,0.7)', borderColor: '#4facfe', textStyle: { color: '#fff' } },
        grid: { left: '5%', right: '5%', bottom: '10%', top: '10%', containLabel: true },
        xAxis: { type: 'category', data: dates, axisLine: { lineStyle: { color: '#4facfe' } }, axisLabel: { color: '#8c9db5' } },
        yAxis: { type: 'value', axisLine: { lineStyle: { color: '#4facfe' } }, axisLabel: { color: '#8c9db5', formatter: '¥{value}' }, splitLine: { lineStyle: { color: 'rgba(79, 172, 254, 0.1)' } } },
        series: [{
          type: 'line',
          data: revenues,
          smooth: true,
          areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(79, 172, 254, 0.5)' }, { offset: 1, color: 'rgba(79, 172, 254, 0.1)' }]) },
          itemStyle: { color: '#4facfe' },
          lineStyle: { width: 3, shadowColor: 'rgba(79, 172, 254, 0.5)', shadowBlur: 10 }
        }]
      })
      window.addEventListener('resize', this.handleResize)
    },
    updateChart() { if (this.chart) { this.chart.dispose(); this.initChart() } },
    handleResize() { if (this.chart) this.chart.resize() }
  }
}
</script>

<style scoped>
.business-fullscreen-view { width: 100%; height: 100%; }
.fullscreen-chart { width: 100%; height: 100%; }
</style>
