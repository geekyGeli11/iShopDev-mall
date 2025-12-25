<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span class="title">客服配置</span>
      </div>

      <el-form
        ref="formRef"
        :model="formData"
        label-width="150px"
        class="form-container"
      >
        <el-form-item label="微信客服二维码">
          <single-upload
            v-model="formData.wechatQrcodeUrl"
            upload-tip="仅支持上传 JPG/PNG 格式的二维码图片"
          />
        </el-form-item>

        <el-form-item label="微信号">
          <el-input
            v-model="formData.wechatAccount"
            placeholder="请输入微信号"
            clearable
          />
        </el-form-item>

        <el-form-item label="企业微信二维码">
          <single-upload
            v-model="formData.enterpriseWechatQrcodeUrl"
            upload-tip="仅支持上传 JPG/PNG 格式的二维码图片"
          />
        </el-form-item>

        <el-form-item label="客服电话">
          <el-input
            v-model="formData.servicePhone"
            placeholder="请输入客服电话"
            clearable
          />
        </el-form-item>

        <el-form-item label="服务时间">
          <el-input
            v-model="formData.serviceHours"
            placeholder="例如：09:00-18:00"
            clearable
          />
        </el-form-item>

        <el-form-item label="服务描述">
          <el-input
            v-model="formData.serviceDescription"
            type="textarea"
            :rows="4"
            placeholder="请输入服务描述"
            clearable
          />
        </el-form-item>

        <el-form-item label="是否启用">
          <el-switch v-model="formData.isEnabled" :active-value="1" :inactive-value="0" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">保存配置</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { getCustomerServiceConfig, updateCustomerServiceConfig } from '@/api/customerService'
import singleUpload from '@/components/Upload/singleUpload'

export default {
  name: 'CustomerService',
  components: {
    singleUpload
  },
  data() {
    return {
      formData: {
        wechatQrcodeUrl: '',
        wechatAccount: '',
        enterpriseWechatQrcodeUrl: '',
        servicePhone: '',
        serviceHours: '',
        serviceDescription: '',
        isEnabled: 1
      },
      loading: false,
      originalData: {}
    }
  },
  mounted() {
    this.loadConfig()
  },
  methods: {
    loadConfig() {
      getCustomerServiceConfig().then(response => {
        if (response.code === 200 && response.data) {
          // 将布尔值转换为数字（true -> 1, false -> 0）
          const isEnabled = response.data.isEnabled
          const isEnabledValue = typeof isEnabled === 'boolean' ? (isEnabled ? 1 : 0) : (isEnabled || 1)
          
          this.formData = {
            id: response.data.id,
            wechatQrcodeUrl: response.data.wechatQrcodeUrl || '',
            wechatAccount: response.data.wechatAccount || '',
            enterpriseWechatQrcodeUrl: response.data.enterpriseWechatQrcodeUrl || '',
            servicePhone: response.data.servicePhone || '',
            serviceHours: response.data.serviceHours || '',
            serviceDescription: response.data.serviceDescription || '',
            isEnabled: isEnabledValue
          }
          this.originalData = JSON.parse(JSON.stringify(this.formData))
        }
      }).catch(error => {
        this.$message.error('加载客服配置失败')
        console.error(error)
      })
    },

    handleSubmit() {
      this.loading = true
      // 准备提交数据，将数字转换为布尔值
      const submitData = {
        ...this.formData,
        isEnabled: this.formData.isEnabled === 1
      }
      updateCustomerServiceConfig(submitData)
        .then(response => {
          if (response.code === 200) {
            this.$message.success('客服配置保存成功')
            this.originalData = JSON.parse(JSON.stringify(this.formData))
          } else {
            this.$message.error(response.message || '保存失败')
          }
        })
        .catch(error => {
          this.$message.error('保存客服配置失败')
          console.error(error)
        })
        .finally(() => {
          this.loading = false
        })
    },
    handleReset() {
      this.formData = JSON.parse(JSON.stringify(this.originalData))
    }
  }
}
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.box-card {
  margin-bottom: 20px;
}

.title {
  font-size: 18px;
  font-weight: bold;
}

.form-container {
  max-width: 600px;
}
</style>
