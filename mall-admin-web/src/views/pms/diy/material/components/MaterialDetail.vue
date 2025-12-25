<template>
  <div class="app-container">
    <el-card class="form-container" shadow="never">
      <el-form ref="materialForm" :model="material" :rules="rules" label-width="150px" size="small">
        <el-form-item label="素材名称：" prop="name">
          <el-input v-model="material.name" class="input-width"/>
        </el-form-item>
        <el-form-item label="素材分类：" prop="categoryId">
          <el-select v-model="material.categoryId" placeholder="请选择素材分类" @change="handleCategoryChange">
            <el-option
              v-for="item in categoryOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="素材类型：">
          <el-tag :type="material.fileType === 1 ? 'success' : 'info'">
            {{ material.fileType === 1 ? '图片' : '文字' }}
          </el-tag>
          <span class="form-tip" style="margin-left: 10px;">根据素材分类自动确定</span>
        </el-form-item>
        <!-- 图片素材 -->
        <el-form-item v-if="material.fileType === 1" label="素材图片：" prop="fileUrl">
          <single-upload 
            v-model="material.fileUrl" 
            :custom-validate="validateTransparentPng"
            upload-tip="仅支持上传透明底的 PNG 图片"
            @input="handleFileChange"
          />
        </el-form-item>
        <!-- 文字素材 -->
        <el-form-item v-if="material.fileType === 2" label="预览图片：" prop="fileUrl">
          <single-upload 
            v-model="material.fileUrl" 
            upload-tip="上传字体效果预览图片（JPG/PNG格式）"
            @input="handleFileChange"
          />
          <div class="form-tip">用于在素材列表中展示字体效果</div>
        </el-form-item>
        <el-form-item v-if="material.fileType === 2" label="字体文件：" prop="fontFileUrl">
          <font-upload 
            v-model="material.fontFileUrl"
            @success="handleFontUploadSuccess"
          />
        </el-form-item>
        <el-form-item v-if="material.fileType === 2" label="字体名称：" prop="fontFamily">
          <el-input
            v-model="material.fontFamily"
            placeholder="请输入字体名称，如：思源黑体、阿里巴巴普惠体"
            class="input-width"
          />
          <div class="form-tip">小程序端加载字体时使用的名称</div>
        </el-form-item>
        <el-form-item label="标签：">
          <el-input
            v-model="material.tags"
            placeholder="多个标签用逗号分隔"
            class="input-width"
          />
        </el-form-item>
        <el-form-item label="描述：">
          <el-input
            v-model="material.description"
            type="textarea"
            :rows="3"
            class="input-width"
          />
        </el-form-item>
        <el-form-item label="是否启用：">
          <el-radio-group v-model="material.status">
            <el-radio :label="1">是</el-radio>
            <el-radio :label="0">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序：">
          <el-input v-model.number="material.sort" class="input-width"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit('materialForm')">提交</el-button>
          <el-button v-if="!isEdit" @click="resetForm('materialForm')">重置</el-button>
          <el-button @click="$router.back()">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { createMaterial, getMaterial, updateMaterial, fetchMaterialCategoryList } from '@/api/diyMaterial'
import SingleUpload from '@/components/Upload/singleUpload'
import FontUpload from '@/components/Upload/fontUpload'

const defaultMaterial = {
  name: '',
  categoryId: null,
  fileType: 1,
  fileUrl: '',
  fontFileUrl: '',
  fontFamily: '',
  tags: '',
  description: '',
  status: 1,
  sort: 0
}

export default {
  name: 'MaterialDetail',
  components: { SingleUpload, FontUpload },
  props: {
    isEdit: {
      type: Boolean,
      default: false
    }
  },
  data() {
    const validateFileUrl = (rule, value, callback) => {
      if (this.material.fileType === 1 && !value) {
        callback(new Error('请上传素材图片'))
      } else if (this.material.fileType === 2 && !value) {
        callback(new Error('请上传预览图片'))
      } else {
        callback()
      }
    }
    const validateFontFileUrl = (rule, value, callback) => {
      if (this.material.fileType === 2 && !value) {
        callback(new Error('请上传字体文件'))
      } else {
        callback()
      }
    }
    const validateFontFamily = (rule, value, callback) => {
      if (this.material.fileType === 2 && !value) {
        callback(new Error('请输入字体名称'))
      } else {
        callback()
      }
    }
    return {
      material: Object.assign({}, defaultMaterial),
      categoryOptions: [],
      rules: {
        name: [
          { required: true, message: '请输入素材名称', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        categoryId: [
          { required: true, message: '请选择素材分类', trigger: 'change' }
        ],
        fileUrl: [
          { required: true, validator: validateFileUrl, trigger: 'blur' }
        ],
        fontFileUrl: [
          { required: true, validator: validateFontFileUrl, trigger: 'blur' }
        ],
        fontFamily: [
          { required: true, validator: validateFontFamily, trigger: 'blur' }
        ]
      }
    }
  },


  created() {
    if (this.isEdit) {
      if (this.$route.query.id != null) {
        this.getMaterial(this.$route.query.id)
      }
    } else {
      this.material = Object.assign({}, defaultMaterial)
    }
    this.getCategoryList()
  },
  methods: {
    getMaterial(id) {
      getMaterial(id).then(response => {
        const data = response.data
        // 确保 fileType 是数字类型
        if (data.fileType !== undefined) {
          data.fileType = parseInt(data.fileType)
        }
        this.material = data
      })
    },
    getCategoryList() {
      fetchMaterialCategoryList({ pageNum: 1, pageSize: 100 }).then(response => {
        this.categoryOptions = response.data.list.map(item => ({
          label: item.name,
          value: item.id,
          type: item.type // 分类类型：1-图片，2-文字
        }))
      })
    },
    // 分类变更时自动设置素材类型
    handleCategoryChange(categoryId) {
      const category = this.categoryOptions.find(item => item.value === categoryId)
      if (category) {
        // 分类的type就是素材的fileType
        this.material.fileType = parseInt(category.type)
        // 切换类型时清空相关字段
        if (this.material.fileType === 1) {
          this.material.fontFileUrl = ''
          this.material.fontFamily = ''
        }
        this.material.fileUrl = ''
      }
    },
    /**
     * 验证图片是否为透明底的 PNG 图片
     * @param {File} file - 上传的文件对象
     * @returns {Promise<{valid: boolean, message: string}>}
     */
    validateTransparentPng(file) {
      return new Promise((resolve) => {
        // 1. 检查文件类型是否为 PNG
        if (file.type !== 'image/png') {
          resolve({
            valid: false,
            message: '仅支持上传 PNG 格式的图片'
          })
          return
        }

        // 2. 使用 Canvas 检测图片是否有透明通道
        const reader = new FileReader()
        reader.onload = (e) => {
          const img = new Image()
          img.onload = () => {
            const canvas = document.createElement('canvas')
            const ctx = canvas.getContext('2d')
            canvas.width = img.width
            canvas.height = img.height
            ctx.drawImage(img, 0, 0)

            try {
              const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height)
              const data = imageData.data
              
              // 检查是否存在透明像素（alpha < 255）
              let hasTransparency = false
              for (let i = 3; i < data.length; i += 4) {
                if (data[i] < 255) {
                  hasTransparency = true
                  break
                }
              }

              if (!hasTransparency) {
                resolve({
                  valid: false,
                  message: '图片必须包含透明底，请上传透明底的 PNG 图片'
                })
              } else {
                resolve({
                  valid: true,
                  message: ''
                })
              }
            } catch (error) {
              console.error('图片透明度检测失败:', error)
              resolve({
                valid: false,
                message: '图片验证失败，请重试'
              })
            }
          }
          img.onerror = () => {
            resolve({
              valid: false,
              message: '图片加载失败，请重试'
            })
          }
          img.src = e.target.result
        }
        reader.onerror = () => {
          resolve({
            valid: false,
            message: '文件读取失败，请重试'
          })
        }
        reader.readAsDataURL(file)
      })
    },
    handleFileChange(url) {
      // 文件上传成功后的回调
      console.log('文件上传成功:', url)
    },
    // 字体文件上传成功
    handleFontUploadSuccess({ url, fileName }) {
      // 如果没有填写字体名称，自动从文件名提取
      if (!this.material.fontFamily) {
        const fontName = fileName.substring(0, fileName.lastIndexOf('.'))
        this.material.fontFamily = fontName
      }
    },
    onSubmit(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.$confirm('是否提交数据', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            if (this.isEdit) {
              updateMaterial(this.material.id, this.material).then(response => {
                this.$message({
                  message: '修改成功！',
                  type: 'success',
                  duration: 1000,
                  onClose: () => {
                    this.$router.back()
                  }
                })
              })
            } else {
              createMaterial(this.material).then(response => {
                this.$message({
                  message: '提交成功！',
                  type: 'success',
                  duration: 1000,
                  onClose: () => {
                    this.$router.back()
                  }
                })
              })
            }
          })
        } else {
          this.$message({
            message: '验证失败',
            type: 'error',
            duration: 1000
          })
          return false
        }
      })
    },
    resetForm(formName) {
      this.$refs[formName].resetFields()
      this.material = Object.assign({}, defaultMaterial)
    }
  }
}
</script>

<style scoped>
.input-width {
  width: 400px;
}
.form-tip {
  font-size: 12px;
  color: #909399;
  line-height: 1.5;
  margin-top: 5px;
}
</style>

