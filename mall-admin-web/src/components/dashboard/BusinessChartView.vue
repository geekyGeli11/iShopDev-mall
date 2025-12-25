<template>
  <div class="business-chart-view">
    <el-row :gutter="20">
      <!-- 销售渠道占比饼图 -->
      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <div slot="header">
            <span>销售渠道占比</span>
          </div>
          <div ref="channelPieChart" class="chart-container"></div>
        </el-card>
      </el-col>
      
      <!-- 各渠道对比柱状图 -->
      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <div slot="header">
            <span>各渠道对比</span>
          </div>
          <div ref="channelBarChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 收入趋势折线图 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card class="chart-card" shadow="hover">
          <div slot="header">
            <span>收入趋势</span>
          </div>
          <div ref="trendLineChart" class="chart-container-large"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 学校营业数据对比 - 仅超级管理员可见 -->
    <el-row :gutter="20" style="margin-top: 20px" v-if="isSuperAdmin && data.schoolStatistics && data.schoolStatistics.length > 0">
      <el-col :span="24">
        <el-card class="chart-card" shadow="hover">
          <div slot="header">
            <span>学校营业数据对比</span>
          </div>
          <div ref="schoolChart" class="chart-container-large"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 门店营业数据对比 - 仅超级管理员可见 -->
    <el-row :gutter="20" style="margin-top: 20px" v-if="isSuperAdmin && data.storeStatistics && data.storeStatistics.length > 0">
      <el-col :span="24">
        <el-card class="chart-card" shadow="hover">
          <div slot="header">
            <span>门店营业数据对比</span>
          </div>
          <div ref="storeChart" class="chart-container-large"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { mapState } from 'vuex'

export default {
  name: 'BusinessChartView',
  
  props: {
    data: {
      type: Object,
      required: true
    }
  },
  
  data() {
    return {
      channelPieChart: null,
      channelBarChart: null,
      trendLineChart: null,
      schoolChart: null,
      storeChart: null
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
    }
  },
  
  mounted() {
    window.addEventListener('resize', this.handleResize)
    if (this.data) {
      this.$nextTick(() => {
        this.initCharts()
      })
    }
  },
  
  beforeDestroy() {
    this.destroyCharts()
  },
  
  watch: {
    data: {
      handler(newData) {
        if (!newData) return
        this.$nextTick(() => {
          if (!this.channelPieChart && !this.channelBarChart && !this.trendLineChart) {
            this.initCharts()
          } else {
            this.updateCharts()
          }
        })
      },
      deep: true,
      immediate: true
    }
  },
  
  methods: {
    initCharts() {
      this.initChannelPieChart()
      this.initChannelBarChart()
      this.initTrendLineChart()
      this.initSchoolChart()
      this.initStoreChart()
    },
    
    initChannelPieChart() {
      if (!this.$refs.channelPieChart) return
      
      if (!this.channelPieChart) {
        this.channelPieChart = echarts.init(this.$refs.channelPieChart)
      }
      
      if (!this.data.channelBreakdown || this.data.channelBreakdown.length === 0) {
        this.channelPieChart.setOption({
          title: {
            text: '暂无数据',
            left: 'center',
            top: 'center',
            textStyle: { color: '#999', fontSize: 14 }
          }
        }, true)
        return
      }
      
      const chartData = this.data.channelBreakdown.map(item => ({
        name: this.getChannelName(item.channel),
        value: parseFloat(item.revenue)
      }))
      
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: ¥{c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [{
          name: '销售渠道',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: { show: false, position: 'center' },
          emphasis: {
            label: { show: true, fontSize: '20', fontWeight: 'bold' }
          },
          labelLine: { show: false },
          data: chartData,
          animationType: 'scale',
          animationEasing: 'elasticOut',
          animationDuration: 1000
        }],
        color: ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de']
      }
      
      this.channelPieChart.setOption(option, true)
    },
    
    initChannelBarChart() {
      if (!this.$refs.channelBarChart) return
      
      if (!this.channelBarChart) {
        this.channelBarChart = echarts.init(this.$refs.channelBarChart)
      }
      
      if (!this.data.channelBreakdown || this.data.channelBreakdown.length === 0) {
        this.channelBarChart.setOption({
          title: {
            text: '暂无数据',
            left: 'center',
            top: 'center',
            textStyle: { color: '#999', fontSize: 14 }
          }
        }, true)
        return
      }
      
      const channels = this.data.channelBreakdown.map(item => this.getChannelName(item.channel))
      const revenues = this.data.channelBreakdown.map(item => parseFloat(item.revenue))
      const orders = this.data.channelBreakdown.map(item => item.orderCount)
      
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' }
        },
        legend: { data: ['收入金额', '订单数'] },
        xAxis: { type: 'category', data: channels },
        yAxis: [
          { type: 'value', name: '收入金额(元)', axisLabel: { formatter: '¥{value}' } },
          { type: 'value', name: '订单数', axisLabel: { formatter: '{value}单' } }
        ],
        series: [
          {
            name: '收入金额',
            type: 'bar',
            data: revenues,
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#83bff6' },
                { offset: 0.5, color: '#188df0' },
                { offset: 1, color: '#188df0' }
              ])
            },
            animationDuration: 1000
          },
          {
            name: '订单数',
            type: 'bar',
            yAxisIndex: 1,
            data: orders,
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#f5a623' },
                { offset: 0.5, color: '#f76b1c' },
                { offset: 1, color: '#f76b1c' }
              ])
            },
            animationDuration: 1000
          }
        ],
        animationEasing: 'elasticOut'
      }
      
      this.channelBarChart.setOption(option, true)
    },
    
    initTrendLineChart() {
      if (!this.$refs.trendLineChart) return
      
      if (!this.trendLineChart) {
        this.trendLineChart = echarts.init(this.$refs.trendLineChart)
      }
      
      if (!this.data.trendData || this.data.trendData.length === 0) {
        this.trendLineChart.setOption({
          title: {
            text: '暂无数据',
            left: 'center',
            top: 'center',
            textStyle: { color: '#999', fontSize: 14 }
          }
        }, true)
        return
      }
      
      const dates = this.data.trendData.map(item => item.date)
      const revenues = this.data.trendData.map(item => parseFloat(item.revenue))
      const orders = this.data.trendData.map(item => item.orderCount)
      
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'cross' }
        },
        legend: { data: ['收入金额', '订单数'] },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', boundaryGap: false, data: dates },
        yAxis: [
          { type: 'value', name: '收入金额(元)', axisLabel: { formatter: '¥{value}' } },
          { type: 'value', name: '订单数', axisLabel: { formatter: '{value}单' } }
        ],
        series: [
          {
            name: '收入金额',
            type: 'line',
            smooth: true,
            data: revenues,
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(88, 160, 253, 0.5)' },
                { offset: 1, color: 'rgba(88, 160, 253, 0.1)' }
              ])
            },
            itemStyle: { color: '#58a0fd' },
            lineStyle: { width: 3 },
            animationDuration: 1500
          },
          {
            name: '订单数',
            type: 'line',
            smooth: true,
            yAxisIndex: 1,
            data: orders,
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(255, 191, 0, 0.5)' },
                { offset: 1, color: 'rgba(255, 191, 0, 0.1)' }
              ])
            },
            itemStyle: { color: '#ffbf00' },
            lineStyle: { width: 3 },
            animationDuration: 1500
          }
        ],
        animationEasing: 'cubicInOut'
      }
      
      this.trendLineChart.setOption(option, true)
    },
    
    updateCharts() {
      this.initChannelPieChart()
      this.initChannelBarChart()
      this.initTrendLineChart()
    },
    
    destroyCharts() {
      window.removeEventListener('resize', this.handleResize)
      const charts = ['channelPieChart', 'channelBarChart', 'trendLineChart', 'schoolChart', 'storeChart']
      charts.forEach(chartName => {
        if (this[chartName]) {
          this[chartName].dispose()
          this[chartName] = null
        }
      })
    },
    
    handleResize() {
      const charts = ['channelPieChart', 'channelBarChart', 'trendLineChart', 'schoolChart', 'storeChart']
      charts.forEach(chartName => {
        if (this[chartName]) this[chartName].resize()
      })
    },

    /**
     * 初始化学校营业数据对比图表
     */
    initSchoolChart() {
      if (!this.$refs.schoolChart || !this.data.schoolStatistics || this.data.schoolStatistics.length === 0) return
      
      if (!this.schoolChart) {
        this.schoolChart = echarts.init(this.$refs.schoolChart)
      }
      
      const schools = this.data.schoolStatistics.map(item => item.schoolName)
      const amounts = this.data.schoolStatistics.map(item => parseFloat(item.totalAmount))
      const orders = this.data.schoolStatistics.map(item => item.orderCount)
      
      const option = {
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        legend: { data: ['营业金额', '订单数'] },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: schools },
        yAxis: [
          { type: 'value', name: '营业金额(元)', axisLabel: { formatter: '¥{value}' } },
          { type: 'value', name: '订单数', axisLabel: { formatter: '{value}单' } }
        ],
        series: [
          {
            name: '营业金额',
            type: 'bar',
            data: amounts,
            itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#83bff6' },
              { offset: 1, color: '#188df0' }
            ]) }
          },
          {
            name: '订单数',
            type: 'bar',
            yAxisIndex: 1,
            data: orders,
            itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#f5a623' },
              { offset: 1, color: '#f76b1c' }
            ]) }
          }
        ]
      }
      
      this.schoolChart.setOption(option, true)
    },

    /**
     * 初始化门店营业数据对比图表
     */
    initStoreChart() {
      if (!this.$refs.storeChart || !this.data.storeStatistics || this.data.storeStatistics.length === 0) return
      
      if (!this.storeChart) {
        this.storeChart = echarts.init(this.$refs.storeChart)
      }
      
      const stores = this.data.storeStatistics.map(item => item.storeName)
      const amounts = this.data.storeStatistics.map(item => parseFloat(item.totalAmount))
      const orders = this.data.storeStatistics.map(item => item.orderCount)
      
      const option = {
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        legend: { data: ['营业金额', '订单数'] },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: stores },
        yAxis: [
          { type: 'value', name: '营业金额(元)', axisLabel: { formatter: '¥{value}' } },
          { type: 'value', name: '订单数', axisLabel: { formatter: '{value}单' } }
        ],
        series: [
          {
            name: '营业金额',
            type: 'bar',
            data: amounts,
            itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#91cc75' },
              { offset: 1, color: '#67c23a' }
            ]) }
          },
          {
            name: '订单数',
            type: 'bar',
            yAxisIndex: 1,
            data: orders,
            itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#fac858' },
              { offset: 1, color: '#e6a23c' }
            ]) }
          }
        ]
      }
      
      this.storeChart.setOption(option, true)
    },
    
    getChannelName(channel) {
      const channelNames = {
        'miniprogram': '小程序订单',
        'self-checkout': '自助结算订单',
        'non-system': '其他销售收入',
        'pc': 'PC订单',
        'other': '其他订单'
      }
      return channelNames[channel] || channel
    }
  }
}
</script>

<style scoped>
.business-chart-view {
  padding: 20px;
}

.chart-card {
  min-height: 450px;
}

.chart-container {
  width: 100%;
  height: 400px;
  min-height: 400px;
}

.chart-container-large {
  width: 100%;
  height: 500px;
  min-height: 500px;
}
</style>
