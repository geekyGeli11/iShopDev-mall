<template>
  <gift-wish-detail :is-edit="false"></gift-wish-detail>
</template>

<script>
import GiftWishDetail from './components/GiftWishDetail'

export default {
  name: 'AddGiftWish',
  components: { GiftWishDetail }
}
</script> 