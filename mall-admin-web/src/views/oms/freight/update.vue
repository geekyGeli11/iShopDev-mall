<template>
  <freight-template-detail :is-edit="true"></freight-template-detail>
</template>

<script>
import FreightTemplateDetail from './components/FreightTemplateDetail'

  export default {
  name: 'UpdateFreightTemplate',
  components: { FreightTemplateDetail }
  }
</script>

<style scoped>
  .form-container {
    margin-bottom: 20px;
  }
</style>