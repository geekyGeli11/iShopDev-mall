<template>
  <div class="member-fullscreen-view">
    <dv-scroll-ranking-board v-if="rankingConfig.data.length > 0" :config="rankingConfig" class="ranking-board" />
    <div v-else class="no-data">暂无数据</div>
  </div>
</template>

<script>
export default {
  name: 'MemberFullscreenView',
  props: { data: { type: Object, default: () => ({}) } },
  computed: {
    rankingConfig() {
      if (!this.data || !this.data.topSpenders || this.data.topSpenders.length === 0) {
        return { data: [] }
      }
      const rankingData = this.data.topSpenders.map((spender) => ({
        name: `${spender.memberName || '未知会员'} (${spender.memberCode || '-'})`,
        value: parseFloat(spender.totalSpending || 0).toFixed(2)
      }))
      return {
        data: rankingData,
        rowNum: 10,
        waitTime: 2000,
        carousel: 'single',
        unit: '元',
        sort: true,
        valueFormatter: (val) => `¥${val}`,
        hoverPause: false
      }
    }
  }
}
</script>

<style scoped>
.member-fullscreen-view { width: 100%; height: 100%; }
.ranking-board { width: 100%; height: 100%; }
.no-data { display: flex; align-items: center; justify-content: center; height: 100%; color: #8c9db5; font-size: 18px; }
</style>
