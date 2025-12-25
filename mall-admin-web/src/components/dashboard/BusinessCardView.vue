<template>
  <div class="business-card-view">
    <!-- 总体数据卡片 -->
    <el-row :gutter="20" class="summary-cards">
      <el-col :span="8">
        <el-card class="data-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon revenue-icon">
              <i class="el-icon-money"></i>
            </div>
            <div class="card-info">
              <div class="card-label">总收入</div>
              <div class="card-value">
                <count-to
                  :start-val="0"
                  :end-val="parseFloat(data.totalRevenue || 0)"
                  :duration="2000"
                  :decimals="2"
                  prefix="¥"
                  separator=","
                />
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="data-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon order-icon">
              <i class="el-icon-s-order"></i>
            </div>
            <div class="card-info">
              <div class="card-label">订单数</div>
              <div class="card-value">
                <count-to
                  :start-val="0"
                  :end-val="data.orderCount || 0"
                  :duration="2000"
                  separator=","
                />
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="data-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon avg-icon">
              <i class="el-icon-s-data"></i>
            </div>
            <div class="card-info">
              <div class="card-label">平均订单金额</div>
              <div class="card-value">
                <count-to
                  :start-val="0"
                  :end-val="parseFloat(data.avgOrderValue || 0)"
                  :duration="2000"
                  :decimals="2"
                  prefix="¥"
                  separator=","
                />
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 销售渠道细分 -->
    <div class="channel-breakdown-section">
      <el-card class="section-container" shadow="hover">
        <div class="section-header">
          <h3>销售渠道细分</h3>
        </div>
        <el-row :gutter="20" class="channel-cards">
          <el-col 
            v-for="channel in data.channelBreakdown" 
            :key="channel.channel"
            :span="8"
          >
            <div class="channel-card">
              <div class="channel-name">{{ getChannelName(channel.channel) }}</div>
              <div class="channel-revenue">
                <count-to
                  :start-val="0"
                  :end-val="parseFloat(channel.revenue || 0)"
                  :duration="2000"
                  :decimals="2"
                  prefix="¥"
                  separator=","
                />
              </div>
              <div class="channel-stats">
                <span class="stat-item">
                  <i class="el-icon-s-order"></i>
                  {{ channel.orderCount }} 单
                </span>
                <span class="stat-item percentage">
                  <i class="el-icon-pie-chart"></i>
                  {{ channel.percentage }}%
                </span>
              </div>
            </div>
          </el-col>
        </el-row>
      </el-card>
    </div>
    
    <!-- 学校营业数据统计 - 仅超级管理员可见 -->
    <div v-if="isSuperAdmin && data.schoolStatistics && data.schoolStatistics.length > 0" class="school-section">
      <el-card class="section-container" shadow="hover">
        <div class="section-header">
          <h3>学校营业数据统计</h3>
        </div>
        <el-row :gutter="20" class="school-cards">
          <el-col 
            v-for="school in data.schoolStatistics" 
            :key="school.schoolId"
            :span="8"
          >
            <div class="school-card">
              <div class="school-name">{{ school.schoolName }}</div>
              <div class="school-amount">
                <count-to
                  :start-val="0"
                  :end-val="parseFloat(school.totalAmount || 0)"
                  :duration="2000"
                  :decimals="2"
                  prefix="¥"
                  separator=","
                />
              </div>
              <div class="school-stats">
                <span class="stat-item">
                  <i class="el-icon-s-order"></i>
                  {{ school.orderCount }} 单
                </span>
                <span class="stat-item">
                  <i class="el-icon-s-data"></i>
                  ¥{{ formatNumber(school.avgOrderAmount) }}
                </span>
              </div>
            </div>
          </el-col>
        </el-row>
      </el-card>
    </div>

    <!-- 门店营业数据统计 - 仅超级管理员可见 -->
    <div v-if="isSuperAdmin && data.storeStatistics && data.storeStatistics.length > 0" class="store-section">
      <el-card class="section-container" shadow="hover">
        <div class="section-header">
          <h3>门店营业数据统计</h3>
        </div>
        <el-row :gutter="20" class="store-cards">
          <el-col 
            v-for="store in data.storeStatistics" 
            :key="store.storeId"
            :span="8"
          >
            <div class="store-card">
              <div class="store-name">{{ store.storeName }}</div>
              <div class="store-amount">
                <count-to
                  :start-val="0"
                  :end-val="parseFloat(store.totalAmount || 0)"
                  :duration="2000"
                  :decimals="2"
                  prefix="¥"
                  separator=","
                />
              </div>
              <div class="store-stats">
                <span class="stat-item">
                  <i class="el-icon-s-order"></i>
                  {{ store.orderCount }} 单
                </span>
                <span class="stat-item">
                  <i class="el-icon-s-data"></i>
                  ¥{{ formatNumber(store.avgOrderAmount) }}
                </span>
              </div>
            </div>
          </el-col>
        </el-row>
      </el-card>
    </div>

    <!-- 趋势数据 -->
    <div v-if="data.trendData && data.trendData.length > 0" class="trend-section">
      <el-card class="section-container" shadow="hover">
        <div class="section-header">
          <h3>收入趋势</h3>
        </div>
        <div class="trend-list">
          <div 
            v-for="(item, index) in data.trendData" 
            :key="index"
            class="trend-item"
          >
            <div class="trend-date">{{ item.date }}</div>
            <div class="trend-revenue">¥{{ formatNumber(item.revenue) }}</div>
            <div class="trend-orders">{{ item.orderCount }} 单</div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import CountTo from 'vue-count-to'
import { mapState } from 'vuex'

export default {
  name: 'BusinessCardView',
  
  components: {
    CountTo
  },
  
  props: {
    data: {
      type: Object,
      required: true
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
     * 格式化数字
     */
    formatNumber(value) {
      if (!value) return '0.00'
      return parseFloat(value).toLocaleString('zh-CN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      })
    }
  }
}
</script>

<style scoped>
.business-card-view {
  padding: 20px;
}

/* 总体数据卡片 */
.summary-cards {
  margin-bottom: 30px;
}

.data-card {
  height: 140px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.data-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
}

.card-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.card-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
  margin-right: 20px;
}

.revenue-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.order-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.avg-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.card-info {
  flex: 1;
}

.card-label {
  font-size: 14px;
  color: #999;
  margin-bottom: 10px;
}

.card-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

/* 销售渠道细分 */
.channel-breakdown-section,
.trend-section {
  margin-bottom: 30px;
}

.section-container {
  padding: 20px;
  min-height: 300px;
}

.section-header {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #409eff;
}

.section-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.channel-cards {
  margin-top: 20px;
}

.channel-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  border-radius: 8px;
  color: #fff;
  text-align: center;
  transition: all 0.3s;
  cursor: pointer;
}

.channel-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.3);
}

.channel-name {
  font-size: 16px;
  margin-bottom: 15px;
  font-weight: 500;
}

.channel-revenue {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 15px;
}

.channel-stats {
  display: flex;
  justify-content: space-around;
  font-size: 14px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.stat-item i {
  font-size: 16px;
}

/* 学校营业数据 */
.school-section,
.store-section {
  margin-bottom: 30px;
}

.school-cards,
.store-cards {
  margin-top: 20px;
}

.school-cards > .el-col,
.store-cards > .el-col {
  margin-bottom: 20px;
}

.school-card,
.store-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  border-radius: 8px;
  color: #fff;
  text-align: center;
  transition: all 0.3s;
  cursor: pointer;
}

.school-card:hover,
.store-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.3);
}

.school-name,
.store-name {
  font-size: 16px;
  margin-bottom: 15px;
  font-weight: 500;
}

.school-amount,
.store-amount {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 15px;
}

.school-stats,
.store-stats {
  display: flex;
  justify-content: space-around;
  font-size: 14px;
}

/* 趋势数据 */
.trend-list {
  max-height: 400px;
  overflow-y: auto;
}

.trend-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #eee;
  transition: background 0.3s;
}

.trend-item:hover {
  background: #f5f7fa;
}

.trend-date {
  flex: 1;
  color: #666;
  font-size: 14px;
}

.trend-revenue {
  flex: 1;
  text-align: center;
  font-size: 16px;
  font-weight: bold;
  color: #409eff;
}

.trend-orders {
  flex: 1;
  text-align: right;
  color: #999;
  font-size: 14px;
}

/* 滚动条样式 */
.trend-list::-webkit-scrollbar {
  width: 6px;
}

.trend-list::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 3px;
}

.trend-list::-webkit-scrollbar-thumb:hover {
  background: #bbb;
}
</style>
