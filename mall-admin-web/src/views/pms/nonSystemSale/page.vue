<template>
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
      <div>
        <i class="el-icon-search"></i>
        <span>其他销售录入</span>
      </div>
    </el-card>
    
    <non-system-sale></non-system-sale>
  </div>
</template>

<script>
import NonSystemSale from './index'

export default {
  name: 'NonSystemSalePage',
  components: {
    NonSystemSale
  }
}
</script>

<style scoped>
.app-container {
  background: #f5f5f5;
  min-height: calc(100vh - 84px);
}

.filter-container {
  margin-bottom: 20px;
}

.filter-container .el-card__body {
  padding: 20px;
}
</style>
