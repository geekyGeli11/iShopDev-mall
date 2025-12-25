<template>
  <div class="member-card-view">
    <!-- 总体数据卡片 -->
    <el-row :gutter="20" class="summary-cards">
      <el-col :span="8">
        <el-card class="data-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon new-member-icon">
              <i class="el-icon-user-solid"></i>
            </div>
            <div class="card-info">
              <div class="card-label">新增会员数</div>
              <div class="card-value">
                <count-to
                  :start-val="0"
                  :end-val="data.newMemberCount || 0"
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
            <div class="card-icon active-member-icon">
              <i class="el-icon-s-custom"></i>
            </div>
            <div class="card-info">
              <div class="card-label">总活跃会员数</div>
              <div class="card-value">
                <count-to
                  :start-val="0"
                  :end-val="data.totalActiveMembers || 0"
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
            <div class="card-icon growth-icon">
              <i class="el-icon-top"></i>
            </div>
            <div class="card-info">
              <div class="card-label">会员增长率</div>
              <div class="card-value" :class="getGrowthClass(data.growthRate)">
                <count-to
                  :start-val="0"
                  :end-val="parseFloat(data.growthRate || 0)"
                  :duration="2000"
                  :decimals="2"
                  suffix="%"
                />
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 会员消费排行榜 -->
    <div class="ranking-section">
      <el-card class="section-container" shadow="hover">
        <div class="section-header">
          <h3>会员消费排行榜 TOP 10</h3>
        </div>
        <div class="ranking-list">
          <div 
            v-for="(spender, index) in (data.topSpenders || [])" 
            :key="index"
            class="ranking-item"
          >
            <span class="rank-number" :class="getRankClass(index)">{{ index + 1 }}</span>
            <span class="member-name">{{ spender.memberName || '未知会员' }}</span>
            <span class="member-code">{{ spender.memberCode || '-' }}</span>
            <span class="spending-amount">¥{{ formatNumber(spender.totalSpending) }}</span>
          </div>
          <el-empty v-if="!data.topSpenders || data.topSpenders.length === 0" description="暂无数据" />
        </div>
      </el-card>
    </div>
    
    <!-- 会员增长趋势 -->
    <div v-if="data.memberTrendData && data.memberTrendData.length > 0" class="trend-section">
      <el-card class="section-container" shadow="hover">
        <div class="section-header">
          <h3>会员增长趋势</h3>
        </div>
        <div class="trend-list">
          <div 
            v-for="(item, index) in data.memberTrendData" 
            :key="index"
            class="trend-item"
          >
            <div class="trend-date">{{ item.date }}</div>
            <div class="trend-stats">
              <span class="stat-badge new-members">
                <i class="el-icon-user"></i>
                新增 {{ item.newMembers }}
              </span>
              <span class="stat-badge active-members">
                <i class="el-icon-s-custom"></i>
                活跃 {{ item.activeMembers }}
              </span>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 小程序访问概况 -->
    <div v-if="data.wechatVisitData && data.wechatVisitData.visitSummary" class="wechat-visit-section">
      <el-row :gutter="20" class="visit-summary-cards">
        <el-col :span="6">
          <el-card class="visit-card" shadow="hover">
            <div class="visit-content">
              <div class="visit-icon">
                <i class="el-icon-user"></i>
              </div>
              <div class="visit-info">
                <div class="visit-label">总访问用户数</div>
                <div class="visit-value">
                  <count-to
                    :start-val="0"
                    :end-val="data.wechatVisitData.visitSummary.totalVisitUv || 0"
                    :duration="2000"
                    separator=","
                  />
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="visit-card" shadow="hover">
            <div class="visit-content">
              <div class="visit-icon">
                <i class="el-icon-s-data"></i>
              </div>
              <div class="visit-info">
                <div class="visit-label">总访问次数</div>
                <div class="visit-value">
                  <count-to
                    :start-val="0"
                    :end-val="data.wechatVisitData.visitSummary.totalVisitPv || 0"
                    :duration="2000"
                    separator=","
                  />
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="visit-card" shadow="hover">
            <div class="visit-content">
              <div class="visit-icon">
                <i class="el-icon-user-add"></i>
              </div>
              <div class="visit-info">
                <div class="visit-label">新增用户数</div>
                <div class="visit-value">
                  <count-to
                    :start-val="0"
                    :end-val="data.wechatVisitData.visitSummary.newUserCount || 0"
                    :duration="2000"
                    separator=","
                  />
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="visit-card" shadow="hover">
            <div class="visit-content">
              <div class="visit-icon">
                <i class="el-icon-s-unfold"></i>
              </div>
              <div class="visit-info">
                <div class="visit-label">平均访问深度</div>
                <div class="visit-value">
                  {{ (data.wechatVisitData.visitSummary.avgVisitDepth || 0).toFixed(2) }}
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 用户画像分布 -->
    <div v-if="data.wechatVisitData && data.wechatVisitData.userPortrait" class="portrait-section">
      <el-card class="section-container" shadow="hover">
        <div class="section-header">
          <h3>用户画像分布</h3>
        </div>
        
        <!-- 性别分布 -->
        <div v-if="data.wechatVisitData.userPortrait.genderDistribution" class="portrait-subsection">
          <h4>性别分布</h4>
          <el-row :gutter="20">
            <el-col 
              v-for="gender in data.wechatVisitData.userPortrait.genderDistribution" 
              :key="gender.gender"
              :span="8"
            >
              <div class="portrait-item">
                <span class="portrait-label">{{ getGenderLabel(gender.gender) }}</span>
                <span class="portrait-value">{{ gender.count }} ({{ gender.percentage.toFixed(2) }}%)</span>
              </div>
            </el-col>
          </el-row>
        </div>

        <!-- 年龄分布 -->
        <div v-if="data.wechatVisitData.userPortrait.ageDistribution" class="portrait-subsection">
          <h4>年龄分布</h4>
          <el-row :gutter="20">
            <el-col 
              v-for="age in data.wechatVisitData.userPortrait.ageDistribution" 
              :key="age.ageRange"
              :span="8"
            >
              <div class="portrait-item">
                <span class="portrait-label">{{ age.ageRange }}</span>
                <span class="portrait-value">{{ age.count }} ({{ age.percentage.toFixed(2) }}%)</span>
              </div>
            </el-col>
          </el-row>
        </div>

        <!-- 地域分布 -->
        <div v-if="data.wechatVisitData.userPortrait.regionDistribution" class="portrait-subsection">
          <h4>地域分布 (Top 10)</h4>
          <el-row :gutter="20">
            <el-col 
              v-for="region in data.wechatVisitData.userPortrait.regionDistribution" 
              :key="region.region"
              :span="12"
            >
              <div class="portrait-item">
                <span class="portrait-label">{{ region.region }}</span>
                <span class="portrait-value">{{ region.count }} ({{ region.percentage.toFixed(2) }}%)</span>
              </div>
            </el-col>
          </el-row>
        </div>
      </el-card>
    </div>

    <!-- 访问趋势数据 -->
    <div v-if="data.wechatVisitData && data.wechatVisitData.visitTrend && data.wechatVisitData.visitTrend.length > 0" class="visit-trend-section">
      <el-card class="section-container" shadow="hover">
        <div class="section-header">
          <h3>小程序访问趋势</h3>
        </div>
        <div class="visit-trend-list">
          <div 
            v-for="(trend, index) in data.wechatVisitData.visitTrend" 
            :key="index"
            class="visit-trend-item"
          >
            <div class="trend-date">{{ trend.date }}</div>
            <div class="trend-stats">
              <span class="stat-badge uv-badge">
                <i class="el-icon-user"></i>
                UV: {{ trend.visitUv }}
              </span>
              <span class="stat-badge pv-badge">
                <i class="el-icon-s-data"></i>
                PV: {{ trend.visitPv }}
              </span>
              <span class="stat-badge new-user-badge">
                <i class="el-icon-user-add"></i>
                新用户: {{ trend.newUser }}
              </span>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 热门访问页面 -->
    <div v-if="data.wechatVisitData && data.wechatVisitData.pageVisit && data.wechatVisitData.pageVisit.length > 0" class="page-visit-section">
      <el-card class="section-container" shadow="hover">
        <div class="section-header">
          <h3>热门访问页面 (Top 10)</h3>
        </div>
        <div class="page-visit-list">
          <div 
            v-for="(page, index) in data.wechatVisitData.pageVisit" 
            :key="index"
            class="page-visit-item"
          >
            <span class="page-rank">{{ index + 1 }}</span>
            <span class="page-path">{{ page.pagePath }}</span>
            <span class="page-stats">
              <span class="stat">UV: {{ page.visitUv }}</span>
              <span class="stat">PV: {{ page.visitPv }}</span>
              <span class="stat">停留: {{ page.avgStayTime }}s</span>
            </span>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import CountTo from 'vue-count-to'

export default {
  name: 'MemberCardView',
  
  components: {
    CountTo
  },
  
  props: {
    data: {
      type: Object,
      required: true
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
     * 获取排名样式类
     */
    getRankClass(index) {
      if (index === 0) return 'rank-gold'
      if (index === 1) return 'rank-silver'
      if (index === 2) return 'rank-bronze'
      return ''
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
.member-card-view {
  padding: 20px;
}

/* 总体数据卡片 */
.summary-cards {
  margin-bottom: 30px;
}

.data-card {
  height: 140px;
  cursor: pointer;
  transition: all 0.3s;
}

.data-card:hover {
  transform: translateY(-5px);
}

.data-card >>> .el-card__body {
  height: 100%;
  padding: 20px;
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

.new-member-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.active-member-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.growth-icon {
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

.growth-positive {
  color: #67c23a;
}

.growth-negative {
  color: #f56c6c;
}

.growth-neutral {
  color: #909399;
}

/* 排行榜 */
.ranking-section,
.trend-section {
  margin-bottom: 30px;
}

.section-container {
  min-height: 200px;
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

.ranking-list {
  max-height: 400px;
  overflow-y: auto;
}

.ranking-item {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  border-bottom: 1px solid #eee;
  transition: background 0.3s;
}

.ranking-item:hover {
  background: #f5f7fa;
}

.rank-number {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: #e0e0e0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  margin-right: 15px;
  font-size: 14px;
}

.rank-gold {
  background: linear-gradient(135deg, #f5af19 0%, #f12711 100%);
  color: #fff;
}

.rank-silver {
  background: linear-gradient(135deg, #bdc3c7 0%, #2c3e50 100%);
  color: #fff;
}

.rank-bronze {
  background: linear-gradient(135deg, #b8860b 0%, #8b4513 100%);
  color: #fff;
}

.member-name {
  flex: 1;
  font-weight: 500;
  color: #333;
}

.member-code {
  flex: 0 0 120px;
  color: #999;
  font-size: 13px;
}

.spending-amount {
  flex: 0 0 100px;
  text-align: right;
  font-weight: bold;
  color: #409eff;
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
  flex: 0 0 120px;
  color: #666;
  font-size: 14px;
  font-weight: 500;
}

.trend-stats {
  flex: 1;
  display: flex;
  gap: 20px;
}

.stat-badge {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 5px 12px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 500;
}

.stat-badge.new-members {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.stat-badge.active-members {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: #fff;
}

.stat-badge i {
  font-size: 14px;
}

/* 小程序访问数据 */
.wechat-visit-section {
  margin-bottom: 30px;
}

.visit-summary-cards {
  margin-bottom: 20px;
}

.visit-card {
  height: 120px;
  cursor: pointer;
  transition: all 0.3s;
}

.visit-card:hover {
  transform: translateY(-5px);
}

.visit-card >>> .el-card__body {
  height: 100%;
  padding: 15px;
}

.visit-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.visit-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
  margin-right: 15px;
}

.visit-info {
  flex: 1;
}

.visit-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
}

.visit-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

/* 用户画像 */
.portrait-section,
.page-visit-section {
  margin-bottom: 30px;
}

.portrait-subsection {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.portrait-subsection:last-child {
  border-bottom: none;
}

.portrait-subsection h4 {
  margin: 0 0 15px 0;
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.portrait-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 10px;
}

.portrait-label {
  color: #606266;
  font-size: 13px;
}

.portrait-value {
  color: #303133;
  font-weight: 500;
  font-size: 13px;
}

/* 访问趋势 */
.visit-trend-section {
  margin-bottom: 30px;
}

.visit-trend-list {
  max-height: 400px;
  overflow-y: auto;
}

.visit-trend-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #eee;
  transition: background 0.3s;
}

.visit-trend-item:hover {
  background: #f5f7fa;
}

.trend-date {
  flex: 0 0 120px;
  color: #666;
  font-size: 14px;
  font-weight: 500;
}

.trend-stats {
  flex: 1;
  display: flex;
  gap: 20px;
}

.stat-badge.uv-badge {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
}

.stat-badge.pv-badge {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
}

.stat-badge.new-user-badge {
  background: linear-gradient(135deg, #e6a23c 0%, #ebb563 100%);
}

/* 热门页面 */
.page-visit-list {
  max-height: 400px;
  overflow-y: auto;
}

.page-visit-item {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  border-bottom: 1px solid #eee;
  transition: background 0.3s;
}

.page-visit-item:hover {
  background: #f5f7fa;
}

.page-rank {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: #e0e0e0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  margin-right: 15px;
  font-size: 12px;
}

.page-path {
  flex: 1;
  color: #333;
  font-size: 13px;
  word-break: break-all;
}

.page-stats {
  display: flex;
  gap: 15px;
  margin-left: 15px;
}

.page-stats .stat {
  color: #999;
  font-size: 12px;
  white-space: nowrap;
}

/* 滚动条样式 */
.trend-list::-webkit-scrollbar,
.ranking-list::-webkit-scrollbar,
.page-visit-list::-webkit-scrollbar {
  width: 6px;
}

.trend-list::-webkit-scrollbar-thumb,
.ranking-list::-webkit-scrollbar-thumb,
.page-visit-list::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 3px;
}

.trend-list::-webkit-scrollbar-thumb:hover,
.ranking-list::-webkit-scrollbar-thumb:hover,
.page-visit-list::-webkit-scrollbar-thumb:hover {
  background: #bbb;
}
</style>
