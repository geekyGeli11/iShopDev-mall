<template>
  <div class="app-container">
    <!-- 页面头部操作栏 -->
    <el-row style="margin-bottom: 20px;">
      <el-col :span="24" style="text-align: right;">
        <el-button 
          type="primary" 
          icon="el-icon-refresh" 
          :loading="loading"
          @click="refreshData">
          {{ loading ? '刷新中...' : '刷新数据' }}
        </el-button>
      </el-col>
    </el-row>

    <!-- 数据概览卡片 -->
    <el-row :gutter="20" class="overview-cards" v-loading="loading">
      <el-col :span="6">
        <el-card class="overview-card">
          <div class="overview-item">
            <div class="overview-icon invite">
              <i class="el-icon-tickets"></i>
            </div>
            <div class="overview-content">
              <div class="overview-number">{{ overviewData.totalInvites || 0 }}</div>
              <div class="overview-label">总邀请数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="overview-card">
          <div class="overview-item">
            <div class="overview-icon register">
              <i class="el-icon-edit"></i>
            </div>
            <div class="overview-content">
              <div class="overview-number">{{ overviewData.registeredUsers || 0 }}</div>
              <div class="overview-label">注册用户</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="overview-card">
          <div class="overview-item">
            <div class="overview-icon order">
              <i class="el-icon-goods"></i>
            </div>
            <div class="overview-content">
              <div class="overview-number">{{ overviewData.firstOrderUsers || 0 }}</div>
              <div class="overview-label">首单用户</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="overview-card">
          <div class="overview-item">
            <div class="overview-icon reward">
              <i class="el-icon-star-on"></i>
            </div>
            <div class="overview-content">
              <div class="overview-number">{{ overviewData.totalRewards || 0 }}</div>
              <div class="overview-label">奖励总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 转化率分析 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card class="chart-card" v-loading="loading">
          <div slot="header" class="card-header">
            <span>转化率分析</span>
          </div>
          <div id="conversionChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card" v-loading="loading">
          <div slot="header" class="card-header">
            <span>奖励类型分布</span>
          </div>
          <div id="rewardTypeChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 趋势分析 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card class="chart-card" v-loading="loading">
          <div slot="header" class="card-header">
            <span>邀请趋势分析</span>
            <div class="chart-controls">
              <el-date-picker
                v-model="trendDateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                size="small"
                @change="handleTrendDateChange"
              ></el-date-picker>
              <el-select v-model="trendType" size="small" @change="handleTrendTypeChange" style="margin-left: 10px;">
                <el-option label="邀请数据" value="invite"></el-option>
                <el-option label="注册数据" value="register"></el-option>
                <el-option label="首单数据" value="order"></el-option>
              </el-select>
            </div>
          </div>
          <div id="trendChart" style="height: 400px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 用户排行榜和地域分布 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card class="chart-card" v-loading="loading">
          <div slot="header" class="card-header">
            <span>邀请达人排行榜</span>
          </div>
          <el-table :data="userRanking" stripe style="width: 100%;">
            <el-table-column prop="rank" label="排名" width="80" align="center"></el-table-column>
            <el-table-column prop="nickname" label="用户昵称"></el-table-column>
            <el-table-column prop="inviteCount" label="邀请人数" align="center"></el-table-column>
          </el-table>
          <div style="text-align: center; margin-top: 10px;">
            <el-pagination
              @current-change="handleRankingPageChange"
              :current-page="rankingPage"
              :page-size="10"
              layout="prev, pager, next"
              :total="rankingTotal"
              small
            ></el-pagination>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card" v-loading="loading">
          <div slot="header" class="card-header">
            <span>地域分布</span>
          </div>
          <div id="regionChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 详细数据分析 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="8">
        <el-card class="detail-card" v-loading="loading">
          <div slot="header" class="card-header">
            <span>奖励统计</span>
          </div>
          <div class="detail-stats">
            <div class="stat-item">
              <span class="stat-label">总奖励数：</span>
              <span class="stat-value">{{ rewardStats.totalRewards || 0 }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">成功发放：</span>
              <span class="stat-value success">{{ rewardStats.successRewards || 0 }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">发放失败：</span>
              <span class="stat-value danger">{{ rewardStats.failRewards || 0 }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="detail-card" v-loading="loading">
          <div slot="header" class="card-header">
            <span>提现统计</span>
          </div>
          <div class="detail-stats">
            <div class="stat-item">
              <span class="stat-label">总申请数：</span>
              <span class="stat-value">{{ withdrawStats.totalApplies || 0 }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">待审核：</span>
              <span class="stat-value warning">{{ withdrawStats.pendingApplies || 0 }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">已通过：</span>
              <span class="stat-value success">{{ withdrawStats.approvedApplies || 0 }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="detail-card" v-loading="loading">
          <div slot="header" class="card-header">
            <span>转化率分析</span>
          </div>
          <div class="detail-stats">
            <div class="stat-item">
              <span class="stat-label">注册转化率：</span>
              <span class="stat-value">{{ conversionData.inviteToRegisterRate || 0 }}%</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">首单转化率：</span>
              <span class="stat-value">{{ conversionData.registerToOrderRate || 0 }}%</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">整体转化率：</span>
              <span class="stat-value primary">{{ overallConversionRate }}%</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import echarts from 'echarts'
import { getAllInviteStatistics, refreshStatisticsCache, getInviteTrend } from '@/api/invite'

export default {
  name: 'InviteStatistics',
  data() {
    return {
      overviewData: {},
      rewardStats: {},
      withdrawStats: {},
      conversionData: {},
      userRanking: [],
      regionData: [],
      trendData: [],
      trendDateRange: [],
      trendType: 'invite',
      rankingPage: 1,
      rankingTotal: 0,
      
      // ECharts 实例
      conversionChart: null,
      rewardTypeChart: null,
      trendChart: null,
      regionChart: null,
      
      // 加载状态
      loading: false
    }
  },
  computed: {
    overallConversionRate() {
      const { totalInvites, firstOrderUsers } = this.overviewData
      if (!totalInvites || totalInvites === 0) return 0
      return ((firstOrderUsers / totalInvites) * 100).toFixed(2)
    }
  },
  created() {
    this.initData()
  },
  mounted() {
    this.$nextTick(() => {
      this.initCharts()
    })
  },
  beforeDestroy() {
    this.destroyCharts()
  },
  methods: {
    async initData() {
      try {
        this.loading = true
        
        // 使用统一接口获取所有数据
        const response = await getAllInviteStatistics({
          startDate: this.trendDateRange && this.trendDateRange[0] ? this.formatDate(this.trendDateRange[0]) : null,
          endDate: this.trendDateRange && this.trendDateRange[1] ? this.formatDate(this.trendDateRange[1]) : null,
          trendType: this.trendType,
          pageSize: 10,
          pageNum: this.rankingPage
        })

        if (response.code === 200 && response.data) {
          const data = response.data
          
          // 直接使用后端返回的结构化数据
          this.overviewData = data.overviewData || {}
          this.rewardStats = data.rewardStats || {}
          this.withdrawStats = data.withdrawStats || {}
          this.conversionData = data.conversionData || {}
          this.userRanking = data.userRanking || []
          this.rankingTotal = this.userRanking.length
          this.regionData = data.regionData || []
          this.trendData = data.trendData || []
          
          // 更新图表
          this.$nextTick(() => {
            this.updateCharts()
          })
        } else {
          this.$message.error(response.message || '数据加载失败')
        }
      } catch (error) {
        console.error('加载统计数据失败:', error)
        this.$message.error('数据加载失败')
      } finally {
        this.loading = false
      }
    },

    async loadTrendData() {
      try {
        const startDate = (this.trendDateRange && this.trendDateRange[0]) ? this.formatDate(this.trendDateRange[0]) : null
        const endDate = (this.trendDateRange && this.trendDateRange[1]) ? this.formatDate(this.trendDateRange[1]) : null
        
        const response = await getInviteTrend({
          startDate,
          endDate,
          type: this.trendType
        })
        this.trendData = response.data || []
      } catch (error) {
        console.error('加载趋势数据失败:', error)
      }
    },

    async refreshData() {
      try {
        this.loading = true
        this.$message.info('正在刷新数据...')
        
        // 刷新缓存
        await refreshStatisticsCache()
        
        // 重新加载数据
        await this.initData()
        
        this.$message.success('数据刷新成功')
      } catch (error) {
        console.error('刷新数据失败:', error)
        this.$message.error('数据刷新失败')
      } finally {
        this.loading = false
      }
    },

    initCharts() {
      // 初始化转化率图表
      this.conversionChart = echarts.init(document.getElementById('conversionChart'))
      
      // 初始化奖励类型图表
      this.rewardTypeChart = echarts.init(document.getElementById('rewardTypeChart'))
      
      // 初始化趋势图表
      this.trendChart = echarts.init(document.getElementById('trendChart'))
      
      // 初始化地域分布图表
      this.regionChart = echarts.init(document.getElementById('regionChart'))

      // 监听窗口大小变化
      window.addEventListener('resize', this.handleResize)
    },

    updateCharts() {
      this.updateConversionChart()
      this.updateRewardTypeChart()
      this.updateTrendChart()
      this.updateRegionChart()
    },

    updateConversionChart() {
      if (!this.conversionChart) return

      const option = {
        title: {
          text: '邀请转化漏斗',
          left: 'center',
          textStyle: { fontSize: 14 }
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)'
        },
        series: [
          {
            name: '转化步骤',
            type: 'funnel',
            left: '10%',
            top: 60,
            bottom: 60,
            width: '80%',
            min: 0,
            max: this.overviewData.totalInvites || 100,
            minSize: '0%',
            maxSize: '100%',
            sort: 'descending',
            gap: 2,
            label: {
              show: true,
              position: 'inside'
            },
            labelLine: {
              length: 10,
              lineStyle: {
                width: 1,
                type: 'solid'
              }
            },
            itemStyle: {
              borderColor: '#fff',
              borderWidth: 1
            },
            emphasis: {
              label: {
                fontSize: 20
              }
            },
            data: [
              { value: this.overviewData.totalInvites || 0, name: '总邀请' },
              { value: this.overviewData.registeredUsers || 0, name: '注册用户' },
              { value: this.overviewData.firstOrderUsers || 0, name: '首单用户' }
            ]
          }
        ]
      }

      this.conversionChart.setOption(option)
    },

    updateRewardTypeChart() {
      if (!this.rewardTypeChart) return

      const rewardTypeData = []
      if (this.rewardStats.rewardTypeCount) {
        Object.entries(this.rewardStats.rewardTypeCount).forEach(([name, value]) => {
          // 映射奖励类型名称
          const typeNames = {
            'type1': '注册奖励',
            'type2': '首单奖励', 
            'type3': '推广奖励',
            'type4': '特殊奖励'
          }
          rewardTypeData.push({ 
            name: typeNames[name] || name, 
            value: value 
          })
        })
      }

      const option = {
        title: {
          text: '奖励类型分布',
          left: 'center',
          textStyle: { fontSize: 14 }
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)'
        },
        legend: {
          bottom: '5%',
          left: 'center'
        },
        series: [
          {
            name: '奖励类型',
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['50%', '50%'],
            data: rewardTypeData,
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      }

      this.rewardTypeChart.setOption(option)
    },

    updateTrendChart() {
      if (!this.trendChart) return

      const dates = this.trendData.map(item => item.date)
      const counts = this.trendData.map(item => item.count)

      const option = {
        title: {
          text: this.getTrendTitle(),
          left: 'center',
          textStyle: { fontSize: 14 }
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: [this.getTrendTitle()],
          top: 30
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          top: '15%',
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
            name: this.getTrendTitle(),
            type: 'line',
            stack: 'Total',
            data: counts,
            smooth: true,
            areaStyle: {},
            emphasis: {
              focus: 'series'
            }
          }
        ]
      }

      this.trendChart.setOption(option)
    },

    updateRegionChart() {
      if (!this.regionChart) return

      const regions = this.regionData.map(item => item.region)
      const counts = this.regionData.map(item => item.count)

      const option = {
        title: {
          text: '用户地域分布',
          left: 'center',
          textStyle: { fontSize: 14 }
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          top: '15%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: regions,
          axisLabel: {
            rotate: 45
          }
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '用户数',
            type: 'bar',
            data: counts,
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
            }
          }
        ]
      }

      this.regionChart.setOption(option)
    },

    getTrendTitle() {
      const typeMap = {
        invite: '邀请数据',
        register: '注册数据',
        order: '首单数据'
      }
      return typeMap[this.trendType] || '数据趋势'
    },

    async handleTrendDateChange() {
      await this.loadTrendData()
      this.updateTrendChart()
    },

    async handleTrendTypeChange() {
      await this.loadTrendData()
      this.updateTrendChart()
    },

    async handleRankingPageChange(page) {
      this.rankingPage = page
      try {
        const response = await getAllInviteStatistics({
          startDate: this.trendDateRange && this.trendDateRange[0] ? this.formatDate(this.trendDateRange[0]) : null,
          endDate: this.trendDateRange && this.trendDateRange[1] ? this.formatDate(this.trendDateRange[1]) : null,
          trendType: this.trendType,
          pageSize: 10,
          pageNum: page
        })

        if (response.code === 200 && response.data) {
          const data = response.data
          
          this.overviewData = data.overviewData || {}
          this.rewardStats = data.rewardStats || {}
          this.withdrawStats = data.withdrawStats || {}
          this.conversionData = data.conversionData || {}
          this.userRanking = data.userRanking || []
          this.rankingTotal = this.userRanking.length
          this.regionData = data.regionData || []
          this.trendData = data.trendData || []
          
          this.$nextTick(() => {
            this.updateCharts()
          })
        } else {
          this.$message.error(response.message || '数据加载失败')
        }
      } catch (error) {
        console.error('加载排行榜失败:', error)
      }
    },

    handleResize() {
      if (this.conversionChart) this.conversionChart.resize()
      if (this.rewardTypeChart) this.rewardTypeChart.resize()
      if (this.trendChart) this.trendChart.resize()
      if (this.regionChart) this.regionChart.resize()
    },

    destroyCharts() {
      window.removeEventListener('resize', this.handleResize)
      if (this.conversionChart) {
        this.conversionChart.dispose()
        this.conversionChart = null
      }
      if (this.rewardTypeChart) {
        this.rewardTypeChart.dispose()
        this.rewardTypeChart = null
      }
      if (this.trendChart) {
        this.trendChart.dispose()
        this.trendChart = null
      }
      if (this.regionChart) {
        this.regionChart.dispose()
        this.regionChart = null
      }
    },

    formatDate(date) {
      if (!date) return null
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    }
  }
}
</script>

<style scoped>
.overview-cards {
  margin-bottom: 20px;
}

.overview-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.overview-item {
  display: flex;
  align-items: center;
  padding: 10px;
}

.overview-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 24px;
  color: white;
}

.overview-icon.invite {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.overview-icon.register {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.overview-icon.order {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.overview-icon.reward {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.overview-content {
  flex: 1;
}

.overview-number {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  line-height: 1;
}

.overview-label {
  color: #666;
  font-size: 14px;
  margin-top: 5px;
}

.chart-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  color: #333;
}

.chart-controls {
  display: flex;
  align-items: center;
}

.detail-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.detail-stats {
  padding: 10px 0;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f5f5f5;
}

.stat-item:last-child {
  border-bottom: none;
}

.stat-label {
  color: #666;
  font-size: 14px;
}

.stat-value {
  font-weight: bold;
  font-size: 16px;
  color: #333;
}

.stat-value.success {
  color: #67c23a;
}

.stat-value.warning {
  color: #e6a23c;
}

.stat-value.danger {
  color: #f56c6c;
}

.stat-value.primary {
  color: #409eff;
}

.el-table {
  border-radius: 4px;
  overflow: hidden;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .overview-cards .el-col {
    margin-bottom: 15px;
  }
  
  .chart-controls {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style> 