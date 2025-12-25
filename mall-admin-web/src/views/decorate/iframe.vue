<template>
  <div class="iframe-wrapper" v-loading="loading">
    <iframe :src="iframeSrc" frameborder="0" @load="handleLoad" />
  </div>
</template>

<script>
export default {
  name: 'DecorateIframe',
  data() {
    return {
      iframeSrc: this.buildSrc(),
      loading: true
    };
  },
  methods: {
    buildSrc() {
      const token = this.$store.getters.token || '';
      /**
       * dev: 指向本地 3001
       * prod: 指向部署到 /diy/
       */
      const base = process.env.NODE_ENV === 'development'
        ? 'http://localhost:3001/#/editor'
        : '/diy/#/editor';
      return `${base}?token=${token}`;
    },
    handleLoad(){
      this.loading = false;
    }
  }
};
</script>

<style scoped>
.iframe-wrapper {
  width: 100%;
  height: calc(100vh - 84px); /* 头部 + 面包屑 */
}
.iframe-wrapper iframe {
  width: 100%;
  height: 100%;
}
</style> 