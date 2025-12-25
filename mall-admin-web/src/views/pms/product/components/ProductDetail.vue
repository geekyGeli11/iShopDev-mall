<template> 
  <el-card class="form-container" shadow="never">
    <el-steps :active="active" finish-status="success" align-center>
      <el-step title="填写商品信息"></el-step>
      <el-step title="填写商品促销"></el-step>
      <el-step title="填写商品属性"></el-step>
      <el-step title="选择商品关联"></el-step>
    </el-steps>
    <product-info-detail
      v-show="showStatus[0]"
      v-model="productParam"
      :is-edit="isEdit"
      @nextStep="nextStep">
    </product-info-detail>
    <product-sale-detail
      v-show="showStatus[1]"
      v-model="productParam"
      :is-edit="isEdit"
      @nextStep="nextStep"
      @prevStep="prevStep">
    </product-sale-detail>
    <product-attr-detail
      v-show="showStatus[2]"
      v-model="productParam"
      :is-edit="isEdit"
      @nextStep="nextStep"
      @prevStep="prevStep">
    </product-attr-detail>
    <product-relation-detail
      v-show="showStatus[3]"
      v-model="productParam"
      :is-edit="isEdit"
      @prevStep="prevStep"
      @finishCommit="finishCommit">
    </product-relation-detail>
  </el-card>
</template>
<script>
  import ProductInfoDetail from './ProductInfoDetail';
  import ProductSaleDetail from './ProductSaleDetail';
  import ProductAttrDetail from './ProductAttrDetail';
  import ProductRelationDetail from './ProductRelationDetail';
  import {createProduct,getProduct,updateProduct} from '@/api/product';

  const defaultProductParam = {
    albumPics: '',
    brandId: null,
    brandName: '',
    deleteStatus: 0,
    description: '',
    detailDesc: '',
    detailHtml: '',
    detailMobileHtml: '',
    detailTitle: '',
    freightTemplateId: null,
    flashPromotionCount: 0,
    flashPromotionId: 0,
    flashPromotionPrice: 0,
    flashPromotionSort: 0,
    giftPoint: 0,
    giftGrowth: 0,
    keywords: '',
    lowStock: 0,
    name: '',
    newStatus: 0,
    note: '',
    originalPrice: 0,
    pic: '',
    //会员价格{memberLevelId: 0,memberPrice: 0,memberLevelName: null}
    memberPriceList: [],
    //商品满减
    productFullReductionList: [{fullPrice: 0, reducePrice: 0}],
    //商品阶梯价格
    productLadderList: [{count: 0,discount: 0,price: 0}],
    previewStatus: 0,
    price: 0,
    productAttributeCategoryId: null,
    //商品属性相关{productAttributeId: 0, value: ''}
    productAttributeValueList: [],
    //商品sku库存信息{lowStock: 0, pic: '', price: 0, sale: 0, skuCode: '', spData: '', stock: 0}
    skuStockList: [],
    //商品相关专题{subjectId: 0}
    subjectProductRelationList: [],
    //商品相关优选{prefrenceAreaId: 0}
    prefrenceAreaProductRelationList: [],
    productCategoryId: null,
    productCategoryName: '',
    productSn: '',
    promotionEndTime: '',
    promotionPerLimit: 0,
    promotionPrice: null,
    promotionStartTime: '',
    promotionType: 0,
    publishStatus: 0,
    recommandStatus: 0,
    sale: 0,
    serviceIds: '',
    sort: 0,
    stock: 0,
    subTitle: '',
    unit: '',
    usePointLimit: 0,
    verifyStatus: 0,
    weight: 0,
    purchaseType: 0,
    // 回本分析相关字段
    enablePaybackAnalysis: 0,          // 是否启用回本分析：0-禁用，1-启用
    paybackTargetQuantity: null,       // 回本目标销售数量
    paybackTargetAmount: null,         // 回本目标金额
    paybackStartDate: null,            // 回本开始日期
    // DIY相关字段
    diyEnabled: 0,                     // 是否支持DIY：0-不支持，1-支持
    diyTemplateId: null,               // 关联的DIY模板ID
    // 学校关联字段
    schoolIds: []                      // 关联的学校ID列表
  };
  export default {
    name: 'ProductDetail',
    components: {ProductInfoDetail, ProductSaleDetail, ProductAttrDetail, ProductRelationDetail},
    props: {
      isEdit: {
        type: Boolean,
        default: false
      }
    },
    data() {
      return {
        active: 0,
        productParam: Object.assign({}, defaultProductParam),
        showStatus: [true, false, false, false]
      }
    },
    created(){
      if(this.isEdit){
        getProduct(this.$route.query.id).then(response=>{
          this.productParam=response.data;
          // 后端返回 Boolean，转换为 0/1 供表单使用
          if (typeof this.productParam.purchaseType === 'boolean') {
            this.productParam.purchaseType = this.productParam.purchaseType ? 1 : 0;
          }
          
          // 回本分析字段的兼容性处理（如果后端还没有这些字段）
          if (this.productParam.enablePaybackAnalysis === undefined) {
            this.productParam.enablePaybackAnalysis = 0;
          }
          if (this.productParam.paybackTargetQuantity === undefined) {
            this.productParam.paybackTargetQuantity = null;
          }
          if (this.productParam.paybackTargetAmount === undefined) {
            this.productParam.paybackTargetAmount = null;
          }
          if (this.productParam.paybackStartDate === undefined) {
            this.productParam.paybackStartDate = null;
          }

          // DIY字段的兼容性处理
          if (this.productParam.diyEnabled === undefined) {
            this.productParam.diyEnabled = 0;
          }
          if (this.productParam.diyTemplateId === undefined) {
            this.productParam.diyTemplateId = null;
          }
        });
      }
    },
    methods: {
      hideAll() {
        for (let i = 0; i < this.showStatus.length; i++) {
          this.showStatus[i] = false;
        }
      },
      prevStep() {
        if (this.active > 0 && this.active < this.showStatus.length) {
          this.active--;
          this.hideAll();
          this.showStatus[this.active] = true;
        }
      },
      nextStep() {
        if (this.active < this.showStatus.length - 1) {
          this.active++;
          this.hideAll();
          this.showStatus[this.active] = true;
        }
      },
      finishCommit(isEdit) {
        this.$confirm('是否要提交该产品', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          // 提交时将 0/1 转回 Boolean 发送给后端
          const submitParam = {...this.productParam};
          if (typeof submitParam.purchaseType === 'number') {
            submitParam.purchaseType = submitParam.purchaseType === 1;
          }
          
          // 确保运费模板ID为空时设置为null（避免提交空字符串或undefined）
          if (!submitParam.freightTemplateId) {
            submitParam.freightTemplateId = null;
          }
          
          // 处理回本分析的布尔值转换
          if (typeof submitParam.enablePaybackAnalysis === 'boolean') {
            // 如果前端使用的是布尔值，转换为后端期望的数字格式
            submitParam.enablePaybackAnalysis = submitParam.enablePaybackAnalysis ? 1 : 0;
          }
          
          console.log('提交前的关键数据:', {
            freightTemplateId: submitParam.freightTemplateId,
            enablePaybackAnalysis: submitParam.enablePaybackAnalysis,
            paybackTargetQuantity: submitParam.paybackTargetQuantity,
            paybackTargetAmount: submitParam.paybackTargetAmount
          });

          if(isEdit){
            updateProduct(this.$route.query.id,submitParam).then(response=>{
              this.$message({
                type: 'success',
                message: '提交成功',
                duration:1000
              });
              this.$router.back();
            });
          }else{
            createProduct(submitParam).then(response=>{
              this.$message({
                type: 'success',
                message: '提交成功',
                duration:1000
              });
              location.reload();
            });
          }
        })
      }
    }
  }
</script>
<style>
  .form-container {
    width: 960px;
  }
  .form-inner-container {
    width: 800px;
  }
</style>


