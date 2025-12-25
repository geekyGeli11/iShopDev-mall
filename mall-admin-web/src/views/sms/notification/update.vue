<template>
  <notification-detail :is-edit="true"></notification-detail>
</template>

<script>
import NotificationDetail from './components/NotificationDetail'

export default {
  name: 'UpdateNotification',
  components: { NotificationDetail }
}
</script> 