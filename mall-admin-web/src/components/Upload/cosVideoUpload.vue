<template>
  <div class="cos-video-upload">
    <input
      ref="fileInput"
      type="file"
      accept="video/*"
      style="display: none"
      @change="handleFileChange"
    />

    <div v-if="!videoUrl && !uploading">
      <el-button type="primary" size="small" @click="triggerUpload">选择视频</el-button>
      <div class="upload-tip">视频大小不超过500MB</div>
    </div>

    <div v-if="uploading" class="upload-progress">
      <el-progress 
        :percentage="uploadProgress" 
        :status="uploadStatus">
      </el-progress>
      <div class="progress-info">
        <span>{{ progressText }}</span>
        <el-button v-if="uploadProgress < 100" type="danger" size="mini" @click="cancelUpload">取消</el-button>
      </div>
    </div>

    <div v-if="videoUrl" class="video-preview">
      <video 
        controls 
        style="width: 100%; max-height: 300px" 
        :src="videoUrl">
      </video>
      <div class="video-actions">
        <el-button type="text" @click="previewVideo">预览</el-button>
        <el-button type="text" @click="removeVideo">移除</el-button>
      </div>
    </div>

    <el-input 
      v-model="urlInput" 
      placeholder="或直接输入视频链接" 
      class="url-input"
      @change="handleUrlChange">
    </el-input>

    <el-dialog 
      title="视频预览" 
      :visible.sync="previewVisible" 
      width="70%">
      <video 
        controls 
        style="width: 100%" 
        :src="videoUrl">
      </video>
    </el-dialog>
  </div>
</template>

<script>
import COS from 'cos-js-sdk-v5'
import { getStsToken } from '@/api/cos'

export default {
  name: 'CosVideoUpload',
  props: {
    value: {
      type: String,
      default: ''
    },
    maxSize: {
      type: Number,
      default: 500 // 默认500MB
    }
  },
  data() {
    return {
      videoUrl: '',
      urlInput: '',
      uploading: false,
      uploadProgress: 0,
      uploadStatus: '',
      progressText: '',
      previewVisible: false,
      cos: null,
      taskId: null,
      Bucket: null,
      Region: null,
      dirPrefix: null
    }
  },
  watch: {
    value: {
      immediate: true,
      handler(val) {
        this.videoUrl = val;
        this.urlInput = val;
      }
    }
  },
  methods: {
    triggerUpload() {
      this.$refs.fileInput.click();
    },
    async handleFileChange(event) {
      const file = event.target.files[0];
      if (!file) return;

      // 检查文件大小
      const fileSize = file.size / 1024 / 1024;
      if (fileSize > this.maxSize) {
        this.$message.error(`视频大小不能超过${this.maxSize}MB!`);
        this.$refs.fileInput.value = ''; // 清空文件输入
        return;
      }

      this.uploading = true;
      this.uploadProgress = 0;
      this.uploadStatus = '';
      this.progressText = '准备上传...';

      try {
        // 获取密钥并初始化COS
        await this.initCos();
        
        // 生成唯一文件名 (添加日期前缀，匹配后端格式)
        const today = new Date();
        const datePrefix = `${today.getFullYear()}${String(today.getMonth() + 1).padStart(2, '0')}${String(today.getDate()).padStart(2, '0')}/`;
        
        const fileExt = file.name.substring(file.name.lastIndexOf('.'));
        const fileName = `${datePrefix}video_${Date.now()}${fileExt}`;
        
        // 上传到腾讯云COS
        this.uploadToCos(file, fileName);
      } catch (error) {
        console.error('上传初始化失败:', error);
        this.$message.error('上传初始化失败，请重试');
        this.uploading = false;
        this.$refs.fileInput.value = ''; // 清空文件输入
      }
    },
    async initCos() {
      try {
        const result = await getStsToken();
        console.log('COS密钥返回结果:', result);
        
        if (result.code === 200 && result.data) {
          const data = result.data;
          console.log('获取到COS密钥数据:', data);
          
          // 获取存储桶和地域
          const Bucket = data.bucketName;
          const Region = data.region;
          const dirPrefix = data.dirPrefix;
          
          // 初始化COS实例 - 使用永久密钥模式
          this.cos = new COS({
            SecretId: data.credentials.tmpSecretId,
            SecretKey: data.credentials.tmpSecretKey,
            // 注意：这里使用的是永久密钥，不需要设置 getAuthorization
            FileParallelLimit: 3,    // 控制文件上传并发数
            ChunkParallelLimit: 3,   // 控制单个文件下分片上传并发数
            ChunkSize: 1024 * 1024 * 5,  // 控制分片大小，单位B
          });
          
          // 保存存储桶信息以供上传使用
          this.Bucket = Bucket;
          this.Region = Region;
          this.dirPrefix = dirPrefix;
          
          console.log('COS初始化成功:', {
            Bucket,
            Region,
            dirPrefix
          });
        } else {
          console.error('获取COS密钥失败，返回数据:', result);
          throw new Error('获取COS密钥失败');
        }
      } catch (error) {
        console.error('初始化COS失败:', error);
        throw error;
      }
    },
    uploadToCos(file, fileName) {
      if (!this.cos) {
        this.$message.error('COS初始化失败，请重试');
        this.uploading = false;
        return;
      }

      const uploadParams = {
        Bucket: this.Bucket,
        Region: this.Region,
        Key: this.dirPrefix + fileName,
        Body: file,
        onTaskReady: (taskId) => {
          this.taskId = taskId;
          console.log('文件上传任务创建，taskId:', taskId);
        },
        onProgress: (info) => {
          this.uploadProgress = Math.floor(info.percent * 100);
          this.progressText = `上传中... ${this.uploadProgress}%`;
          
          if (this.uploadProgress >= 100) {
            this.uploadStatus = 'success';
            this.progressText = '处理中...';
          }
        }
      };

      console.log('上传参数:', uploadParams);

      if (file.size > 1024 * 1024 * 10) {
        // 大文件使用分片上传
        this.cos.sliceUploadFile(uploadParams, this.handleUploadComplete);
      } else {
        // 小文件使用简单上传
        this.cos.putObject(uploadParams, this.handleUploadComplete);
      }
    },
    handleUploadComplete(err, data) {
      this.uploading = false;
      this.$refs.fileInput.value = ''; // 清空文件输入

      if (err) {
        console.error('上传失败详情:', err);
        this.$message.error(`视频上传失败: ${err.message || '请重试'}`);
        return;
      }

      console.log('上传成功，返回数据:', data);
      // 上传成功，获取视频URL
      const videoUrl = `https://${data.Location}`;
      this.videoUrl = videoUrl;
      this.urlInput = videoUrl;
      this.$emit('input', videoUrl);
      this.$message.success('视频上传成功');
    },
    cancelUpload() {
      if (this.taskId && this.cos) {
        this.cos.cancelTask(this.taskId);
        this.$message.info('上传已取消');
      }
      this.uploading = false;
      this.uploadProgress = 0;
      this.taskId = null;
      this.$refs.fileInput.value = ''; // 清空文件输入
    },
    previewVideo() {
      if (this.videoUrl) {
        this.previewVisible = true;
      }
    },
    removeVideo() {
      this.videoUrl = '';
      this.urlInput = '';
      this.$emit('input', '');
    },
    handleUrlChange(val) {
      this.videoUrl = val;
      this.$emit('input', val);
    }
  }
}
</script>

<style scoped>
.cos-video-upload {
  width: 100%;
}
.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}
.upload-progress {
  margin: 10px 0;
}
.progress-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 5px;
  font-size: 12px;
  color: #909399;
}
.video-preview {
  margin: 10px 0;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 10px;
}
.video-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 5px;
}
.url-input {
  margin-top: 10px;
}
</style> 