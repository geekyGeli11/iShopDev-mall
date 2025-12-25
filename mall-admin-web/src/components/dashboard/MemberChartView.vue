<template>
  <div class="member-chart-view">
    <el-row :gutter="20">
      <!-- 会员增长趋势折线图 -->
      <el-col :span="16">
        <el-card class="chart-card" shadow="hover">
          <div slot="header">
            <span>会员增长趋势</span>
          </div>
          <div ref="trendLineChart" class="chart-container-large"></div>
        </el-card>
      </el-col>
      
      <!-- 会员消费Top5饼图 -->
      <el-col :span="8">
        <el-card class="chart-card" shadow="hover">
          <div slot="header">
            <span>Top5会员消费占比</span>
          </div>
          <div ref="topSpendersPieChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 会员消费排行柱状图 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card class="chart-card" shadow="hover">
          <div slot="header">
            <span>会员消费排行</span>
          </div>
          <div ref="spendingBarChart" class="chart-container-large"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 小程序访问趋势 -->
    <el-row :gutter="20" style="margin-top: 20px" v-if="data.wechatVisitData && data.wechatVisitData.visitTrend">
      <el-col :span="24">
        <el-card class="chart-card" shadow="hover">
          <div slot="header">
            <span>小程序访问趋势</span>
          </div>
          <div ref="visitTrendChart" class="chart-container-large"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 用户画像分布 -->
    <el-row :gutter="20" style="margin-top: 20px" v-if="data.wechatVisitData && data.wechatVisitData.userPortrait">
      <el-col :span="8">
        <el-card class="chart-card" shadow="hover">
          <div slot="header">
            <span>性别分布</span>
          </div>
          <div ref="genderChart" class="chart-container"></div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="chart-card" shadow="hover">
          <div slot="header">
            <span>年龄分布</span>
          </div>
          <div ref="ageChart" class="chart-container"></div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="chart-card" shadow="hover">
          <div slot="header">
            <span>地域分布 (Top 10)</span>
          </div>
          <div ref="regionChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 访问留存数据 -->
    <el-row :gutter="20" style="margin-top: 20px" v-if="data.wechatVisitData && data.wechatVisitData.visitRetain">
      <el-col :span="24">
        <el-card class="chart-card" shadow="hover">
          <div slot="header">
            <span>访问留存分析</span>
          </div>
          <div ref="retainChart" class="chart-container-large"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'MemberChartView',
  
  props: {
    data: {
      type: Object,
      required: true
    }
  },
  
  data() {
    return {
      trendLineChart: null,
      topSpendersPieChart: null,
      spendingBarChart: null,
      visitTrendChart: null,
      genderChart: null,
      ageChart: null,
      regionChart: null,
      retainChart: null
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
          if (!this.trendLineChart && !this.topSpendersPieChart && !this.spendingBarChart) {
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
    /**
     * 初始化图表
     */
    initCharts() {
      this.initTrendLineChart()
      this.initTopSpendersPieChart()
      this.initSpendingBarChart()
      this.initVisitTrendChart()
      this.initGenderChart()
      this.initAgeChart()
      this.initRegionChart()
      this.initRetainChart()
    },
    
    /**
     * 初始化会员增长趋势折线图
     */
    initTrendLineChart() {
      if (!this.$refs.trendLineChart) return
      
      if (!this.trendLineChart) {
        this.trendLineChart = echarts.init(this.$refs.trendLineChart)
      }
      
      if (!this.data.memberTrendData || this.data.memberTrendData.length === 0) {
        this.trendLineChart.setOption({
          title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#999', fontSize: 14 } }
        }, true)
        return
      }
      
      const dates = this.data.memberTrendData.map(item => item.date)
      const newMembers = this.data.memberTrendData.map(item => item.newMembers)
      const activeMembers = this.data.memberTrendData.map(item => item.activeMembers)
      
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          }
        },
        legend: {
          data: ['新增会员', '活跃会员']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: dates
        },
        yAxis: {
          type: 'value',
          name: '会员数',
          axisLabel: {
            formatter: '{value}人'
          }
        },
        series: [
          {
            name: '新增会员',
            type: 'line',
            smooth: true,
            data: newMembers,
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(103, 194, 58, 0.5)' },
                { offset: 1, color: 'rgba(103, 194, 58, 0.1)' }
              ])
            },
            itemStyle: {
              color: '#67c23a'
            },
            lineStyle: {
              width: 3
            },
            animationDelay: (idx) => idx * 10
          },
          {
            name: '活跃会员',
            type: 'line',
            smooth: true,
            data: activeMembers,
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(64, 158, 255, 0.5)' },
                { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
              ])
            },
            itemStyle: {
              color: '#409eff'
            },
            lineStyle: {
              width: 3
            },
            animationDelay: (idx) => idx * 10 + 100
          }
        ],
        animationEasing: 'cubicInOut'
      }
      
      this.trendLineChart.setOption(option, true)
    },
    
    /**
     * 初始化Top5会员消费占比饼图
     */
    initTopSpendersPieChart() {
      if (!this.$refs.topSpendersPieChart) return
      
      if (!this.topSpendersPieChart) {
        this.topSpendersPieChart = echarts.init(this.$refs.topSpendersPieChart)
      }
      
      if (!this.data.topSpenders || this.data.topSpenders.length === 0) {
        this.topSpendersPieChart.setOption({
          title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#999', fontSize: 14 } }
        }, true)
        return
      }
      
      const top5 = this.data.topSpenders.slice(0, 5)
      const chartData = top5.map(spender => ({
        name: spender.memberName || '未知会员',
        value: parseFloat(spender.totalSpending)
      }))
      
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: ¥{c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          textStyle: {
            fontSize: 12
          }
        },
        series: [
          {
            name: 'Top5会员',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '16',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: chartData,
            animationType: 'scale',
            animationEasing: 'elasticOut',
            animationDelay: (idx) => idx * 100
          }
        ],
        color: ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de']
      }
      
      this.topSpendersPieChart.setOption(option, true)
    },
    
    /**
     * 初始化会员消费排行柱状图
     */
    initSpendingBarChart() {
      if (!this.$refs.spendingBarChart) return
      
      if (!this.spendingBarChart) {
        this.spendingBarChart = echarts.init(this.$refs.spendingBarChart)
      }
      
      if (!this.data.topSpenders || this.data.topSpenders.length === 0) {
        this.spendingBarChart.setOption({
          title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#999', fontSize: 14 } }
        }, true)
        return
      }
      
      const members = this.data.topSpenders.map(spender => 
        spender.memberName || '未知会员'
      )
      const spendings = this.data.topSpenders.map(spender => 
        parseFloat(spender.totalSpending)
      )
      const orders = this.data.topSpenders.map(spender => 
        spender.orderCount
      )
      
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        legend: {
          data: ['总消费金额', '订单数']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: members,
          axisLabel: {
            interval: 0,
            rotate: 30
          }
        },
        yAxis: [
          {
            type: 'value',
            name: '消费金额(元)',
            axisLabel: {
              formatter: '¥{value}'
            }
          },
          {
            type: 'value',
            name: '订单数',
            axisLabel: {
              formatter: '{value}单'
            }
          }
        ],
        series: [
          {
            name: '总消费金额',
            type: 'bar',
            data: spendings,
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#83bff6' },
                { offset: 0.5, color: '#188df0' },
                { offset: 1, color: '#188df0' }
              ])
            },
            emphasis: {
              itemStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: '#2378f7' },
                  { offset: 0.7, color: '#2378f7' },
                  { offset: 1, color: '#83bff6' }
                ])
              }
            },
            animationDelay: (idx) => idx * 100
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
            animationDelay: (idx) => idx * 100 + 50
          }
        ],
        animationEasing: 'elasticOut',
        animationDelayUpdate: (idx) => idx * 5
      }
      
      this.spendingBarChart.setOption(option, true)
    },
    
    /**
     * 更新图表 - 不销毁，直接更新数据
     */
    updateCharts() {
      this.initTrendLineChart()
      this.initTopSpendersPieChart()
      this.initSpendingBarChart()
    },
    
    /**
     * 销毁图表
     */
    destroyCharts() {
      window.removeEventListener('resize', this.handleResize)
      
      const charts = [
        'trendLineChart',
        'topSpendersPieChart',
        'spendingBarChart',
        'visitTrendChart',
        'genderChart',
        'ageChart',
        'regionChart',
        'retainChart'
      ]
      
      charts.forEach(chartName => {
        if (this[chartName]) {
          this[chartName].dispose()
          this[chartName] = null
        }
      })
    },
    
    /**
     * 处理窗口大小变化
     */
    handleResize() {
      const charts = [
        'trendLineChart',
        'topSpendersPieChart',
        'spendingBarChart',
        'visitTrendChart',
        'genderChart',
        'ageChart',
        'regionChart',
        'retainChart'
      ]
      
      charts.forEach(chartName => {
        if (this[chartName]) this[chartName].resize()
      })
    },

    /**
     * 初始化小程序访问趋势图表
     */
    initVisitTrendChart() {
      if (!this.$refs.visitTrendChart || !this.data.wechatVisitData || !this.data.wechatVisitData.visitTrend) return
      
      if (!this.visitTrendChart) {
        this.visitTrendChart = echarts.init(this.$refs.visitTrendChart)
      }
      
      const visitTrend = this.data.wechatVisitData.visitTrend
      const dates = visitTrend.map(item => item.date)
      const uvData = visitTrend.map(item => item.visitUv)
      const pvData = visitTrend.map(item => item.visitPv)
      const newUserData = visitTrend.map(item => item.newUser)
      
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'cross' }
        },
        legend: {
          data: ['访问用户数(UV)', '访问次数(PV)', '新增用户数']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: dates
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '访问用户数(UV)',
            type: 'line',
            smooth: true,
            data: uvData,
            itemStyle: { color: '#409eff' },
            lineStyle: { width: 2 }
          },
          {
            name: '访问次数(PV)',
            type: 'line',
            smooth: true,
            data: pvData,
            itemStyle: { color: '#67c23a' },
            lineStyle: { width: 2 }
          },
          {
            name: '新增用户数',
            type: 'line',
            smooth: true,
            data: newUserData,
            itemStyle: { color: '#e6a23c' },
            lineStyle: { width: 2 }
          }
        ]
      }
      
      this.visitTrendChart.setOption(option, true)
    },

    /**
     * 初始化性别分布图表
     */
    initGenderChart() {
      if (!this.$refs.genderChart || !this.data.wechatVisitData || !this.data.wechatVisitData.userPortrait) return
      
      if (!this.genderChart) {
        this.genderChart = echarts.init(this.$refs.genderChart)
      }
      
      const genderData = this.data.wechatVisitData.userPortrait.genderDistribution || []
      const chartData = genderData.map(item => ({
        name: this.getGenderLabel(item.gender),
        value: item.count
      }))
      
      const option = {
        tooltip: { trigger: 'item', formatter: '{a} <br/>{b}: {c} ({d}%)' },
        series: [{
          name: '性别分布',
          type: 'pie',
          radius: '50%',
          data: chartData,
          emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' } }
        }],
        color: ['#5470c6', '#91cc75', '#fac858']
      }
      
      this.genderChart.setOption(option, true)
    },

    /**
     * 初始化年龄分布图表
     */
    initAgeChart() {
      if (!this.$refs.ageChart || !this.data.wechatVisitData || !this.data.wechatVisitData.userPortrait) return
      
      if (!this.ageChart) {
        this.ageChart = echarts.init(this.$refs.ageChart)
      }
      
      const ageData = this.data.wechatVisitData.userPortrait.ageDistribution || []
      const ageRanges = ageData.map(item => item.ageRange)
      const counts = ageData.map(item => item.count)
      
      const option = {
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: ageRanges },
        yAxis: { type: 'value' },
        series: [{
          name: '用户数',
          type: 'bar',
          data: counts,
          itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#83bff6' },
            { offset: 1, color: '#188df0' }
          ]) }
        }]
      }
      
      this.ageChart.setOption(option, true)
    },

    /**
     * 初始化地域分布图表
     */
    initRegionChart() {
      if (!this.$refs.regionChart || !this.data.wechatVisitData || !this.data.wechatVisitData.userPortrait) return
      
      if (!this.regionChart) {
        this.regionChart = echarts.init(this.$refs.regionChart)
      }
      
      const regionData = this.data.wechatVisitData.userPortrait.regionDistribution || []
      const regions = regionData.map(item => item.region)
      const counts = regionData.map(item => item.count)
      
      const option = {
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: regions, axisLabel: { rotate: 45 } },
        yAxis: { type: 'value' },
        series: [{
          name: '用户数',
          type: 'bar',
          data: counts,
          itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#f5a623' },
            { offset: 1, color: '#f76b1c' }
          ]) }
        }]
      }
      
      this.regionChart.setOption(option, true)
    },

    /**
     * 初始化访问留存图表
     */
    initRetainChart() {
      if (!this.$refs.retainChart || !this.data.wechatVisitData || !this.data.wechatVisitData.visitRetain) return
      
      if (!this.retainChart) {
        this.retainChart = echarts.init(this.$refs.retainChart)
      }
      
      const retainData = this.data.wechatVisitData.visitRetain
      const periods = retainData.map(item => item.period)
      const newUsers = retainData.map(item => item.newUser)
      const retainUsers = retainData.map(item => item.retainUser)
      const retainRates = retainData.map(item => item.retainRate)
      
      const option = {
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        legend: { data: ['新增用户', '留存用户', '留存率(%)'] },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: periods },
        yAxis: [
          { type: 'value', name: '用户数' },
          { type: 'value', name: '留存率(%)', max: 100 }
        ],
        series: [
          {
            name: '新增用户',
            type: 'bar',
            data: newUsers,
            itemStyle: { color: '#5470c6' }
          },
          {
            name: '留存用户',
            type: 'bar',
            data: retainUsers,
            itemStyle: { color: '#91cc75' }
          },
          {
            name: '留存率(%)',
            type: 'line',
            yAxisIndex: 1,
            data: retainRates,
            itemStyle: { color: '#fac858' },
            lineStyle: { width: 2 }
          }
        ]
      }
      
      this.retainChart.setOption(option, true)
    },

    /**
     * 获取性别标签
     */
    getGenderLabel(gender) {
      const labels = {
        'male': '男性',
        'female': '女性',
        'unknown': '未知'
      }
      return labels[gender] || gender
    }
  }
}
</script>

<style scoped>
.member-chart-view {
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
  height: 400px;
  min-height: 400px;
}
</style>
