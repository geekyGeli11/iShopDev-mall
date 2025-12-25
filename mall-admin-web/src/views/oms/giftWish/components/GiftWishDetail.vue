<template>
  <el-card class="form-container" shadow="never">
    <el-form :model="giftWishParam" :rules="rules" ref="giftWishForm" label-width="150px">
      <el-form-item label="祝福类型" prop="type">
        <el-radio-group v-model="giftWishParam.type">
          <el-radio :label="true">图片</el-radio>
          <el-radio :label="false">文字</el-radio>
        </el-radio-group>
      </el-form-item>
      
      <el-form-item label="分类" prop="category">
        <el-radio-group v-model="giftWishParam.category">
          <el-radio :label="true">送礼祝福</el-radio>
          <el-radio :label="false">收礼感谢语</el-radio>
        </el-radio-group>
      </el-form-item>
      
      <el-form-item label="图片地址" prop="imageUrl" v-if="giftWishParam.type">
        <single-upload v-model="giftWishParam.imageUrl"></single-upload>
      </el-form-item>
      
      <el-form-item label="文字内容" prop="content" v-if="!giftWishParam.type">
        <el-input type="textarea" :rows="4" v-model="giftWishParam.content" placeholder="请输入祝福或感谢文字"></el-input>
      </el-form-item>
      
      <el-form-item label="排序" prop="sort">
        <el-input v-model.number="giftWishParam.sort" placeholder="请输入排序值" class="input-width"></el-input>
      </el-form-item>
      
      <el-form-item label="状态" prop="status">
        <el-switch v-model="giftWishParam.status" :active-value="true" :inactive-value="false"></el-switch>
      </el-form-item>
      
      <el-form-item label="备注" prop="remark">
        <el-input v-model="giftWishParam.remark" placeholder="请输入备注" class="input-width"></el-input>
      </el-form-item>
      
      <el-form-item>
        <el-button type="primary" @click="onSubmit('giftWishForm')">提交</el-button>
        <el-button @click="onCancel">取消</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
import { createGiftWish, getGiftWish, updateGiftWish } from '@/api/giftWish'
import SingleUpload from '@/components/Upload/singleUpload'

const defaultGiftWish = {
  type: true,
  category: true,
  imageUrl: '',
  content: '',
  sort: 0,
  status: true,
  remark: ''
}

export default {
  name: 'GiftWishDetail',
  components: { SingleUpload },
  props: {
    isEdit: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      giftWishParam: Object.assign({}, defaultGiftWish),
      rules: {
        type: [
          { required: true, message: '请选择祝福类型', trigger: 'change' }
        ],
        category: [
          { required: true, message: '请选择分类', trigger: 'change' }
        ],
        imageUrl: [
          { required: true, message: '请上传图片', trigger: 'change', validator: this.validateImageUrl }
        ],
        content: [
          { required: true, message: '请输入祝福文字', trigger: 'blur', validator: this.validateContent }
        ],
        sort: [
          { required: true, message: '请输入排序值', trigger: 'blur' },
          { type: 'number', message: '排序值必须为数字', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    if (this.isEdit) {
      const id = this.$route.query.id
      this.getGiftWishDetail(id)
    }
  },
  methods: {
    // 获取礼物心愿详情
    getGiftWishDetail(id) {
      getGiftWish(id).then(response => {
        this.giftWishParam = response.data
      })
    },
    // 验证图片URL (仅当类型为图片时验证)
    validateImageUrl(rule, value, callback) {
      if (this.giftWishParam.type && !this.giftWishParam.imageUrl) {
        return callback(new Error('请上传图片'))
      }
      callback()
    },
    // 验证文字内容 (仅当类型为文字时验证)
    validateContent(rule, value, callback) {
      if (!this.giftWishParam.type && !this.giftWishParam.content) {
        return callback(new Error('请输入祝福文字'))
      }
      callback()
    },
    // 提交表单
    onSubmit(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.$confirm('是否提交数据', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            if (this.isEdit) {
              updateGiftWish(this.giftWishParam.id, this.giftWishParam).then(response => {
                this.$message({
                  message: '修改成功',
                  type: 'success'
                })
                this.$router.back()
              })
            } else {
              createGiftWish(this.giftWishParam).then(response => {
                this.$message({
                  message: '添加成功',
                  type: 'success'
                })
                this.$router.back()
              })
            }
          })
        } else {
          this.$message({
            message: '请填写必填项',
            type: 'warning'
          })
          return false
        }
      })
    },
    // 取消操作
    onCancel() {
      this.$router.back()
    }
  }
}
</script>

<style scoped>
.input-width {
  width: 370px;
}

.form-container {
  width: 700px;
  margin: 30px auto;
}
</style> 