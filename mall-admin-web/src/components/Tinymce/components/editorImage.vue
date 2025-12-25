<template>
  <div class="upload-container">
    <el-button icon='el-icon-upload' size="mini" :style="{ background: color, borderColor: color }"
      @click=" dialogVisible = true" type="primary">上传图片
    </el-button>
    <el-dialog append-to-body :visible.sync="dialogVisible">
      <el-upload class="editor-slide-upload" :action="useOss ? ossUploadUrl : minioUploadUrl"
        :data="useOss ? dataObj : null" :headers="headers" :multiple="true" :file-list="fileList" :show-file-list="true"
        list-type="picture-card" :on-remove="handleRemove" :on-success="handleSuccess" :before-upload="beforeUpload">
        <el-button size="small" type="primary">点击上传</el-button>
      </el-upload>
      <el-button @click="dialogVisible = false">取 消</el-button>
      <el-button type="primary" @click="handleSubmit">确 定</el-button>
    </el-dialog>
  </div>
</template>

<script>

export default {
  name: 'editorSlideUpload',
  props: {
    color: {
      type: String,
      default: '#1890ff'
    }
  },
  data() {
    return {
      dialogVisible: false,
      listObj: {},
      fileList: [],
      dataObj: {
        policy: '',
        signature: '',
        key: '',
        ossaccessKeyId: '',
        dir: '',
        host: ''
      },
      useOss: true, //使用oss->true;使用MinIO->false
      ossUploadUrl: 'https://guanghengzou.hello4am.com/mall-admin/aliyun/oss/upload',
      // ossUploadUrl: 'http://localhost:8201/mall-admin/aliyun/oss/upload',
      minioUploadUrl: 'https://haojiangzhenhao.hello4am.com/mall-admin/minio/upload',
    }
  },
  computed: {
    headers() {
      return {
        Authorization: `${this.getLoginToken()}`,
      };
    }
  },
  methods: {
    checkAllSuccess() {
      return Object.keys(this.listObj).every(item => this.listObj[item].hasSuccess)
    },
    handleSubmit() {
      const arr = Object.keys(this.listObj).map(v => this.listObj[v])
      if (!this.checkAllSuccess()) {
        this.$message('请等待所有图片上传成功 或 出现了网络问题，请刷新页面重新上传！')
        return
      }
      console.log('准备提交的图片数据:', arr); // 调试点
      this.$emit('successCBK', arr);
      console.log('提交成功，触发 successCBK 事件'); // 调试点
      this.listObj = {};
      this.fileList = [];
      this.dialogVisible = false;
    },
    handleSuccess(response, file) {
      const uid = file.uid;
      const objKeyArr = Object.keys(this.listObj)

      console.log('上传成功的 response:', response); // 调试点
      console.log('当前文件 uid:', uid); // 调试点
      console.log('上传前的 listObj:', this.listObj); // 调试点
      console.log('上传前的 objKeyArr:', objKeyArr);

      for (let i = 0, len = objKeyArr.length; i < len; i++) {
        console.log('列表里的uid:', this.listObj[objKeyArr[i]].uid);
        console.log('当前文件uid:', uid);
        if (this.listObj[objKeyArr[i]].uid === uid) {
          this.listObj[objKeyArr[i]].url = response.data;
          if (!this.useOss) {
            //不使用oss直接获取图片路径
            this.listObj[objKeyArr[i]].url = response.data.url;
          }
          this.listObj[objKeyArr[i]].hasSuccess = true;
          console.log('更新后的 listObj:', this.listObj); // 调试点
          return
        }
      }
    },
    handleRemove(file) {
      const uid = file.uid;
      const objKeyArr = Object.keys(this.listObj);
      for (let i = 0, len = objKeyArr.length; i < len; i++) {
        if (this.listObj[objKeyArr[i]].uid === uid) {
          delete this.listObj[objKeyArr[i]];
          return
        }
      }
    },
    beforeUpload(file) {
      let _self = this;
      const fileName = file.uid;
      this.listObj[fileName] = {hasSuccess: false, uid: file.uid, width: this.width, height: this.height};
      
      const isValidType = ['image/jpeg', 'image/png', 'image/gif'].includes(file.type);
      const isValidSize = file.size / 1024 / 1024 < 50; // 文件大小限制为50MB

      if (!isValidType) {
        this.$message.error('仅支持上传 JPG/PNG 格式的图片');
        return false;
      }
      if (!isValidSize) {
        this.$message.error('文件大小不能超过 10MB');
        return false;
      }
      return true;
    },
    // 获取 loginToken
    getLoginToken() {
      const match = document.cookie.match(/(^|;\s*)loginToken=([^;]+)/);
      return match ? decodeURIComponent(match[2]) : '';
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.upload-container .editor-slide-upload {
  margin-bottom: 20px;
}
</style>
