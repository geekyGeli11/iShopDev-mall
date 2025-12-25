<template>
  <div class="product-card-view">
    <!-- 商品销售概览 -->
    <el-row :gutter="20" class="overview-cards">
      <el-col :span="6">
        <el-card class="overview-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon">
              <i class="el-icon-goods"></i>
            </div>
            <div class="card-info">
              <div class="card-title">总商品数</div>
              <div class="card-value">
                <count-to
                  :start-val="0"
                  :end-val="data.totalProducts || 0"
                  :duration="2000"
                  separator=","
                />
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="overview-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon">
              <i class="el-icon-sell"></i>
            </div>
            <div class="card-info">
              <div class="card-title">销售总额</div>
              <div class="card-value">
                <count-to
                  :start-val="0"
                  :end-val="parseFloat(data.totalSalesAmount || 0)"
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
      
      <el-col :span="6">
        <el-card class="overview-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon">
              <i class="el-icon-box"></i>
            </div>
            <div class="card-info">
              <div class="card-title">销售数量</div>
              <div class="card-value">
                <count-to
                  :start-val="0"
                  :end-val="data.totalSalesQuantity || 0"
                  :duration="2000"
                  separator=","
                />
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="overview-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon">
              <i class="el-icon-star-on"></i>
            </div>
            <div class="card-info">
              <div class="card-title">热销商品</div>
              <div class="card-value">
                <count-to
                  :start-val="0"
                  :end-val="data.hotProducts || 0"
                  :duration="2000"
                  separator=","
                />
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 利润汇总卡片 -->
    <el-row :gutter="20" class="summary-cards">
      <el-col :span="8">
        <el-card class="data-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon profit-icon">
              <i class="el-icon-coin"></i>
            </div>
            <div class="card-info">
              <div class="card-label">总利润</div>
              <div class="card-value">
                <count-to
                  :start-val="0"
                  :end-val="parseFloat((data.profitSummary && data.profitSummary.totalProfit) || 0)"
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
            <div class="card-icon margin-icon">
              <i class="el-icon-s-data"></i>
            </div>
            <div class="card-info">
              <div class="card-label">利润率</div>
              <div class="card-value">
                <count-to
                  :start-val="0"
                  :end-val="parseFloat((data.profitSummary && data.profitSummary.profitMargin) || 0)"
                  :duration="2000"
                  :decimals="2"
                  suffix="%"
                />
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="data-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon product-icon">
              <i class="el-icon-goods"></i>
            </div>
            <div class="card-info">
              <div class="card-label">售出商品数</div>
              <div class="card-value">
                <count-to
                  :start-val="0"
                  :end-val="(data.topSellingByAmount && data.topSellingByAmount.length) || 0"
                  :duration="2000"
                />
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 销售排行榜 -->
    <div class="ranking-section">
      <el-card class="section-container" shadow="hover">
        <div class="section-header">
          <h3>商品销售排行榜（按金额）</h3>
        </div>
        <div class="ranking-list">
          <div 
            v-for="(product, index) in (data.topSellingByAmount || [])" 
            :key="index"
            class="ranking-item"
          >
            <span class="rank-number" :class="getRankClass(index)">{{ index + 1 }}</span>
            <span class="product-name">{{ product.productName }}</span>
            <span class="sku-code">{{ product.skuCode || '-' }}</span>
            <span class="sales-amount">¥{{ formatNumber(product.salesAmount) }}</span>
          </div>
          <el-empty v-if="!data.topSellingByAmount || data.topSellingByAmount.length === 0" description="暂无数据" />
        </div>
      </el-card>
    </div>
    
    <!-- 品类分布 -->
    <div v-if="data.categoryDistribution && data.categoryDistribution.length > 0" class="category-section">
      <el-card class="section-container" shadow="hover">
        <div class="section-header">
          <h3>品类销售分布</h3>
        </div>
        <el-row :gutter="20" class="category-cards">
          <el-col 
            v-for="category in data.categoryDistribution.slice(0, 6)" 
            :key="category.category"
            :span="8"
          >
            <div class="category-card">
              <div class="category-name">{{ category.category }}</div>
              <div class="category-amount">¥{{ formatNumber(category.salesAmount) }}</div>
              <div class="category-percentage">{{ category.percentage }}%</div>
            </div>
          </el-col>
        </el-row>
      </el-card>
    </div>
  </div>
</template>

<script>
import CountTo from 'vue-count-to'

export default {
  name: 'ProductCardView',
  
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
    }
  }
}
</script>

<style scoped>
.product-card-view {
  padding: 20px;
}

.overview-cards {
  margin-bottom: 30px;
}

.overview-card {
  height: 120px;
  cursor: pointer;
  transition: all 0.3s;
}

.overview-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
}

.overview-card >>> .el-card__body {
  height: 100%;
  padding: 15px;
}

.overview-card .card-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.overview-card .card-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
  margin-right: 15px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.overview-card .card-title {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
}

.overview-card .card-value {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

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

.profit-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.margin-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.product-icon {
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

.ranking-section,
.category-section {
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

.product-name {
  flex: 1;
  font-weight: 500;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.sku-code {
  flex: 0 0 120px;
  color: #999;
  font-size: 13px;
}

.sales-amount {
  flex: 0 0 100px;
  text-align: right;
  font-weight: bold;
  color: #409eff;
}

.category-cards {
  margin-top: 20px;
}

.category-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  border-radius: 8px;
  color: #fff;
  text-align: center;
  transition: all 0.3s;
  cursor: pointer;
  margin-bottom: 20px;
}

.category-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.3);
}

.category-name {
  font-size: 16px;
  margin-bottom: 10px;
  font-weight: 500;
}

.category-amount {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 10px;
}

.category-percentage {
  font-size: 14px;
  opacity: 0.9;
}

/* 滚动条样式 */
.ranking-list::-webkit-scrollbar {
  width: 6px;
}

.ranking-list::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 3px;
}

.ranking-list::-webkit-scrollbar-thumb:hover {
  background: #bbb;
}
</style>
