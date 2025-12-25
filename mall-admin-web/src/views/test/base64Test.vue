<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>Base64数据传输测试</span>
      </div>
      
      <el-form :inline="true">
        <el-form-item label="优惠券ID">
          <el-input v-model="testCouponId" placeholder="请输入优惠券ID"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="testGenerateShare" :loading="testing">测试生成分享</el-button>
        </el-form-item>
      </el-form>
      
      <div v-if="testResult">
        <h3>测试结果：</h3>
        <el-row :gutter="20">
          <el-col :span="12">
            <h4>分享链接：</h4>
            <el-input type="textarea" :rows="3" v-model="testResult.shareLink" readonly></el-input>
          </el-col>
          <el-col :span="12">
            <h4>小程序码状态：</h4>
            <el-input type="textarea" :rows="3" v-model="testResult.miniProgramCodeUrl" readonly></el-input>
          </el-col>
        </el-row>
        
        <div style="margin-top: 20px;">
          <h4>Base64数据信息：</h4>
          <p v-if="testResult.miniProgramCodeBase64">
            <strong>数据长度：</strong>{{ testResult.miniProgramCodeBase64.length }}<br>
            <strong>数据前缀：</strong>{{ testResult.miniProgramCodeBase64.substring(0, 100) }}...<br>
            <strong>是否为有效base64：</strong>{{ isValidBase64(testResult.miniProgramCodeBase64) ? '是' : '否' }}
          </p>
          <p v-else style="color: red;">未接收到base64数据</p>
        </div>
        
        <div v-if="testResult.miniProgramCodeBase64" style="margin-top: 20px;">
          <h4>小程序码预览：</h4>
          <img :src="testResult.miniProgramCodeBase64" alt="小程序码" style="width: 200px; height: 200px; border: 1px solid #ddd;" />
        </div>
      </div>
      
      <div v-if="errorInfo" style="margin-top: 20px;">
        <h3 style="color: red;">错误信息：</h3>
        <pre>{{ errorInfo }}</pre>
      </div>
    </el-card>
  </div>
</template>

<script>
import { generateShareInfo } from '@/api/coupon'

export default {
  name: 'Base64Test',
  data() {
    return {
      testCouponId: '1',
      testing: false,
      testResult: null,
      errorInfo: null
    }
  },
  methods: {
    testGenerateShare() {
      if (!this.testCouponId) {
        this.$message.warning('请输入优惠券ID');
        return;
      }
      
      this.testing = true;
      this.testResult = null;
      this.errorInfo = null;
      
      generateShareInfo(this.testCouponId).then(response => {
        this.testing = false;
        console.log('完整响应数据:', response);
        
        if (response.code === 200) {
          this.testResult = response.data;
          this.$message.success('测试成功');
        } else {
          this.errorInfo = `错误代码: ${response.code}\n错误信息: ${response.message}`;
          this.$message.error('测试失败');
        }
      }).catch(error => {
        this.testing = false;
        this.errorInfo = `请求异常: ${error.message || error}`;
        this.$message.error('请求失败');
        console.error('测试错误:', error);
      });
    },
    
    isValidBase64(str) {
      if (!str || typeof str !== 'string') return false;
      if (!str.startsWith('data:image/')) return false;
      
      try {
        const base64Data = str.split(',')[1];
        return btoa(atob(base64Data)) === base64Data;
      } catch (e) {
        return false;
      }
    }
  }
}
</script>

<style scoped>
.box-card {
  margin: 20px;
}

pre {
  background: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  white-space: pre-wrap;
  word-wrap: break-word;
}
</style>
