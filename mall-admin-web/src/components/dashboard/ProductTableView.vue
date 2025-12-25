<template>
  <div class="product-table-view">
    <el-card class="summary-card" shadow="hover">
      <div slot="header"><span>利润汇总</span></div>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="总利润">
          <span class="value-text profit">¥{{ formatNumber(data.profitSummary.totalProfit) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="利润率">
          <span class="value-text margin">{{ data.profitSummary.profitMargin }}%</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
    
    <el-card class="ranking-table-card" shadow="hover">
      <div slot="header"><span>商品销售排行（按金额）</span></div>
      <el-table :data="data.topSellingByAmount" stripe border>
        <el-table-column type="index" label="排名" width="80" align="center" />
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="skuCode" label="SKU" width="150" />
        <el-table-column prop="salesAmount" label="销售金额" sortable>
          <template slot-scope="scope">
            <span class="amount-value">¥{{ formatNumber(scope.row.salesAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="salesQuantity" label="销售数量" width="120" sortable />
        <el-table-column prop="profit" label="利润" sortable>
          <template slot-scope="scope">
            <span class="profit-value">¥{{ formatNumber(scope.row.profit) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'ProductTableView',
  props: { data: { type: Object, required: true } },
  methods: {
    formatNumber(value) {
      if (!value) return '0.00'
      return parseFloat(value).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
    }
  }
}
</script>

<style scoped>
.product-table-view { padding: 20px; }
.summary-card, .ranking-table-card { margin-bottom: 20px; }
.value-text { font-size: 18px; font-weight: bold; }
.value-text.profit { color: #67c23a; }
.value-text.margin { color: #e6a23c; }
.amount-value, .profit-value { font-weight: bold; color: #67c23a; }
</style>
