<template>
  <gift-wish-detail :is-edit="true"></gift-wish-detail>
</template>

<script>
import GiftWishDetail from './components/GiftWishDetail'

export default {
  name: 'UpdateGiftWish',
  components: { GiftWishDetail }
}
</script> 