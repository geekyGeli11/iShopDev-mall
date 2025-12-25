<template>
  <diy-template-detail :is-edit="false"></diy-template-detail>
</template>

<script>
import DiyTemplateDetail from './components/DiyTemplateDetail'

export default {
  name: 'AddDiyTemplate',
  components: {
    DiyTemplateDetail
  }
}
</script>
