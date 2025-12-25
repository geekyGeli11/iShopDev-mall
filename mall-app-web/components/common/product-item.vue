<script>
export default {
    name: 'ProductItem',
    props: {
        product: {
            type: Object,
            default: () => ({})
        },
        // 添加新属性，表示是否在多商品视图中显示
        inMultiView: {
            type: Boolean,
            default: false
        }
    },
    methods: {
        goToDetail() {
            // 判断是否在多商品视图中，如果是则直接跳转到单商品视图
            const url = this.inMultiView 
                ? `/pages/activities/local-goods-detail/local-goods-detail?id=${this.product.id}&view_type=single` 
                : `/pages/goods/goods?goods_id=${this.product.id}`;
            uni.navigateTo({
                url
            });
        },
        // ... existing code ...
    }
}
</script> 