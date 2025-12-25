<template>
  <div>
    <el-upload
      :action="uploadUrl"
      :headers="headers"
      :multiple="false"
      :show-file-list="false"
      :before-upload="beforeUpload"
      :on-success="handleUploadSuccess"
      :on-error="handleUploadError"
      :on-progress="handleUploadProgress"
    >
      <el-button size="small" type="primary">点击上传字体</el-button>
      <div slot="tip" class="el-upload__tip">
        支持 TTF、OTF、WOFF、WOFF2 格式，文件大小不超过 10MB
      </div>
    </el-upload>

    <div v-if="uploadPercentage > 0 && uploadPercentage < 100" class="upload-progress">
      <el-progress :percentage="uploadPercentage" :status="uploadStatus" />
    </div>

    <div v-if="value" class="font-file-info">
      <el-tag type="success" closable @close="handleRemove">
        <i class="el-icon-document"></i>
        {{ fileName }}
      </el-tag>
    </div>
  </div>
</template>

<script>
export default {
  name: 'FontUpload',
  props: {
    value: String
  },
  data() {
    return {
      uploadUrl: process.env.BASE_API + '/aliyun/oss/upload',
      uploadPercentage: 0,
      uploadStatus: ''
    }
  },
  computed: {
    headers() {
      return {
        Authorization: this.getLoginToken()
      }
    },
    fileName() {
      if (!this.value) return ''
      return this.value.substring(this.value.lastIndexOf('/') + 1)
    }
  },
  methods: {
    getLoginToken() {
      const match = document.cookie.match(/(^|;\s*)loginToken=([^;]+)/)
      return match ? decodeURIComponent(match[2]) : ''
    },
    beforeUpload(file) {
      const validExtensions = ['.ttf', '.otf', '.woff', '.woff2']
      const fileName = file.name.toLowerCase()
      const hasValidExtension = validExtensions.some(ext => fileName.endsWith(ext))
      
      if (!hasValidExtension) {
        this.$message.error('仅支持上传 TTF、OTF、WOFF、WOFF2 格式的字体文件')
        return false
      }
      
      const isValidSize = file.size / 1024 / 1024 < 10
      if (!isValidSize) {
        this.$message.error('字体文件大小不能超过 10MB')
        return false
      }
      
      this.uploadPercentage = 0
      this.uploadStatus = ''
      return true
    },
    handleUploadSuccess(res, file) {
      this.$emit('input', res.data)
      this.$emit('success', { url: res.data, fileName: file.name })
      this.uploadPercentage = 100
      this.uploadStatus = 'success'
      this.$message.success('字体文件上传成功')
    },
    handleUploadError(err) {
      this.uploadStatus = 'exception'
      this.$message.error('字体文件上传失败，请重试')
      console.error(err)
    },
    handleUploadProgress(event) {
      this.uploadPercentage = Math.round(event.percent)
      this.uploadStatus = ''
    },
    handleRemove() {
      this.$emit('input', '')
      this.uploadPercentage = 0
    }
  }
}
</script>

<style scoped>
.upload-progress {
  margin-top: 10px;
  width: 300px;
}
.font-file-info {
  margin-top: 10px;
}
.font-file-info .el-tag {
  font-size: 13px;
}
.font-file-info i {
  margin-right: 5px;
}
</style>
