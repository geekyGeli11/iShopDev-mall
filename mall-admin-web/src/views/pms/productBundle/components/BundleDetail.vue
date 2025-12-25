<template>
  <div class="app-container">
    <el-card class="form-container" shadow="never">
      <el-form :model="bundle" :rules="rules" ref="bundleForm" label-width="150px">
        <el-form-item label="组合名称：" prop="name">
          <el-input v-model="bundle.name" placeholder="请输入组合名称"></el-input>
        </el-form-item>
        <el-form-item label="组合主图：" prop="pic">
          <single-upload v-model="bundle.pic"></single-upload>
        </el-form-item>
        <el-form-item label="组合宣传图：">
          <multi-upload v-model="albumPicsList"></multi-upload>
        </el-form-item>
        <el-form-item label="组合描述：">
          <el-input type="textarea" :rows="3" v-model="bundle.description" placeholder="请输入组合描述"></el-input>
        </el-form-item>
        <el-form-item label="组合详情：" prop="detailDesc">
          <tinymce v-model="bundle.detailDesc" :height="300"></tinymce>
        </el-form-item>
        <el-form-item label="定价方式：" prop="priceType">
          <el-radio-group v-model="bundle.priceType" @change="handlePriceTypeChange">
            <el-radio :label="1">固定价格</el-radio>
            <el-radio :label="2">折扣比例</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="组合价格：" v-if="bundle.priceType === 1" prop="bundlePrice">
          <el-input-number v-model="bundle.bundlePrice" :precision="2" :min="0.01" :max="99999"></el-input-number>
          <span style="margin-left: 10px">元</span>
        </el-form-item>
        <el-form-item label="折扣比例：" v-if="bundle.priceType === 2" prop="discountRate">
          <el-input-number v-model="bundle.discountRate" :precision="0" :min="1" :max="99"></el-input-number>
          <span style="margin-left: 10px">%（原价的百分比）</span>
        </el-form-item>
        <el-form-item label="排序：">
          <el-input-number v-model="bundle.sort" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="关联学校：">
          <el-select v-model="bundle.schoolId" placeholder="请选择学校" clearable>
            <el-option
              v-for="item in schoolList"
              :key="item.id"
              :label="item.schoolName"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="组合商品：" prop="items">
          <product-selector v-model="bundle.items" @change="handleProductChange"></product-selector>
        </el-form-item>
        <el-form-item label="原价总和：" v-if="originalPrice > 0">
          <span style="font-size: 18px; color: #909399">¥{{ originalPrice.toFixed(2) }}</span>
        </el-form-item>
        <el-form-item label="组合价格：" v-if="calculatedBundlePrice > 0">
          <span style="font-size: 18px; color: #f56c6c; font-weight: bold">¥{{ calculatedBundlePrice.toFixed(2) }}</span>
          <span style="margin-left: 10px; color: #67c23a" v-if="savedAmount > 0">节省 ¥{{ savedAmount.toFixed(2) }}</span>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit('bundleForm')">提交</el-button>
          <el-button v-if="!isEdit" @click="resetForm('bundleForm')">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import SingleUpload from '@/components/Upload/singleUpload'
import MultiUpload from '@/components/Upload/multiUpload'
import Tinymce from '@/components/Tinymce'
import ProductSelector from './ProductSelector'
import { createBundle, updateBundle, getBundle } from '@/api/productBundle'
import { fetchList as fetchSchoolList } from '@/api/school'

const defaultBundle = {
  name: '',
  pic: '',
  albumPics: '',
  description: '',
  detailDesc: '',
  priceType: 1,
  bundlePrice: null,
  discountRate: null,
  sort: 0,
  schoolId: null,
  items: []
}

export default {
  name: 'BundleDetail',
  components: { SingleUpload, MultiUpload, Tinymce, ProductSelector },
  props: {
    isEdit: {
      type: Boolean,
      default: false
    }
  },
  data() {
    const validateItems = (rule, value, callback) => {
      if (!value || value.length < 2) {
        callback(new Error('组合商品至少需要包含2个商品'))
      } else {
        callback()
      }
    }
    const validateBundlePrice = (rule, value, callback) => {
      if (this.bundle.priceType === 1) {
        if (!value || value <= 0) {
          callback(new Error('请输入组合价格'))
        } else if (this.originalPrice > 0 && value >= this.originalPrice) {
          callback(new Error('组合价格必须低于原价总和'))
        } else {
          callback()
        }
      } else {
        callback()
      }
    }
    const validateDiscountRate = (rule, value, callback) => {
      if (this.bundle.priceType === 2) {
        if (!value || value <= 0 || value >= 100) {
          callback(new Error('请输入有效的折扣比例(1-99)'))
        } else {
          callback()
        }
      } else {
        callback()
      }
    }
    return {
      bundle: Object.assign({}, defaultBundle),
      albumPicsList: [],
      schoolList: [],
      rules: {
        name: [{ required: true, message: '请输入组合名称', trigger: 'blur' }],
        pic: [{ required: true, message: '请上传组合主图', trigger: 'change' }],
        detailDesc: [{ required: true, message: '请输入组合详情', trigger: 'blur' }],
        priceType: [{ required: true, message: '请选择定价方式', trigger: 'change' }],
        bundlePrice: [{ validator: validateBundlePrice, trigger: 'blur' }],
        discountRate: [{ validator: validateDiscountRate, trigger: 'blur' }],
        items: [{ validator: validateItems, trigger: 'change' }]
      }
    }
  },
  computed: {
    originalPrice() {
      if (!this.bundle.items || this.bundle.items.length === 0) return 0
      return this.bundle.items.reduce((sum, item) => {
        return sum + (item.price || 0) * (item.quantity || 1)
      }, 0)
    },
    calculatedBundlePrice() {
      if (this.bundle.priceType === 1) {
        return this.bundle.bundlePrice || 0
      } else if (this.bundle.priceType === 2 && this.bundle.discountRate) {
        return this.originalPrice * this.bundle.discountRate / 100
      }
      return 0
    },
    savedAmount() {
      return this.originalPrice - this.calculatedBundlePrice
    }
  },
  created() {
    this.getSchoolList()
    if (this.isEdit) {
      const id = this.$route.query.id
      if (id) {
        this.getBundleDetail(id)
      }
    }
  },
  methods: {
    getSchoolList() {
      fetchSchoolList({ pageNum: 1, pageSize: 100 }).then(response => {
        this.schoolList = response.data.list || []
      }).catch(() => {
        this.schoolList = []
      })
    },
    getBundleDetail(id) {
      getBundle(id).then(response => {
        const data = response.data
        this.bundle = {
          name: data.name,
          pic: data.pic,
          albumPics: data.albumPics,
          description: data.description,
          detailDesc: data.detailDesc,
          priceType: data.priceType,
          bundlePrice: data.bundlePrice,
          discountRate: data.discountRate,
          sort: data.sort,
          schoolId: data.schoolId,
          items: data.items ? data.items.map(item => ({
            productId: item.productId,
            productName: item.productName,
            productPic: item.productPic,
            price: item.productPrice,
            quantity: item.quantity,
            sort: item.sort
          })) : []
        }
        if (data.albumPics) {
          this.albumPicsList = data.albumPics.split(',')
        }
      })
    },
    handlePriceTypeChange() {
      if (this.bundle.priceType === 1) {
        this.bundle.discountRate = null
      } else {
        this.bundle.bundlePrice = null
      }
    },
    handleProductChange(items) {
      this.bundle.items = items
    },
    onSubmit(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          // 处理宣传图片
          this.bundle.albumPics = this.albumPicsList.join(',')
          // 构建提交数据
          const submitData = {
            ...this.bundle,
            items: this.bundle.items.map(item => ({
              productId: item.productId,
              quantity: item.quantity,
              sort: item.sort || 0
            }))
          }
          if (this.isEdit) {
            const id = this.$route.query.id
            updateBundle(id, submitData).then(response => {
              this.$message({
                message: '修改成功',
                type: 'success',
                duration: 1000
              })
              this.$router.back()
            })
          } else {
            createBundle(submitData).then(response => {
              this.$message({
                message: '创建成功',
                type: 'success',
                duration: 1000
              })
              this.$router.back()
            })
          }
        }
      })
    },
    resetForm(formName) {
      this.$refs[formName].resetFields()
      this.bundle = Object.assign({}, defaultBundle)
      this.albumPicsList = []
    }
  }
}
</script>
<style scoped>
.form-container {
  width: 800px;
}
</style>
