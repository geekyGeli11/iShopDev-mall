<template>
  <notification-detail :is-edit="false"></notification-detail>
</template>

<script>
import NotificationDetail from './components/NotificationDetail'

export default {
  name: 'AddNotification',
  components: { NotificationDetail }
}
</script> 