<template>
  <freight-template-detail :is-edit="false"></freight-template-detail>
</template>

<script>
import FreightTemplateDetail from './components/FreightTemplateDetail'

export default {
  name: 'AddFreightTemplate',
  components: { FreightTemplateDetail }
}
</script>

<style scoped>
  .form-container {
    margin-bottom: 20px;
  }
</style>