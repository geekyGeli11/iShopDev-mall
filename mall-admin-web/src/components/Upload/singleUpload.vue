<template>
  <div>
    <el-upload
      :action="useOss ? ossUploadUrl : minioUploadUrl"
      :data="useOss ? null : dataObj"
      :headers="headers"
      list-type="picture"
      :multiple="false"
      :show-file-list="showFileList"
      :file-list="fileList"
      :before-upload="beforeUpload"
      :on-remove="handleRemove"
      :on-success="handleUploadSuccess"
      :on-preview="handlePreview"
      :on-error="handleUploadError"
      :on-progress="handleUploadProgress"
    >
      <el-button size="small" type="primary">点击上传</el-button>
      <div slot="tip" class="el-upload__tip">
        {{ uploadTip || '仅支持上传 JPG/PNG/GIF 格式的图片或 MP4/MOV 格式的视频' }}
      </div>
    </el-upload>

    <div v-if="uploadPercentage > 0" class="upload-progress">
      <el-progress :percentage="uploadPercentage" :status="uploadStatus" />
    </div>

    <!-- 图片预览对话框 -->
    <el-dialog :visible.sync="dialogVisible">
      <img class="preview-image" :src="fileList[0].url" alt="" />
    </el-dialog>

    <!-- 图片裁剪对话框 -->
    <el-dialog
      title="图片裁剪"
      :visible.sync="cropDialogVisible"
      width="800px"
      :close-on-click-modal="false"
      @close="handleCropDialogClose"
      @open="handleCropDialogOpened"
    >
      <div class="crop-container" v-show="cropImageUrl">
        <VueCropper
          ref="cropper"
          :img="cropImageUrl"
          :outputSize="1"
          :outputType="cropOutputType"
          :info="true"
          :full="false"
          :canMove="true"
          :canMoveBox="true"
          :original="false"
          :autoCrop="true"
          :autoCropWidth="cropWidth"
          :autoCropHeight="cropHeight"
          :fixedBox="false"
          :fixed="cropFixed"
          :fixedNumber="cropFixedNumber"
          :centerBox="true"
          :infoTrue="true"
          :enlarge="1"
        ></VueCropper>
      </div>
      <div v-show="!cropImageUrl" class="crop-loading">
        <i class="el-icon-loading"></i> 正在加载图片...
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="handleCropDialogClose">取消</el-button>
        <el-button type="primary" @click="handleCropConfirm" :loading="cropUploading" :disabled="!cropImageUrl">确定裁剪并上传</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
// 使用本地的 vue-cropper 源码，避免打包后加载失败
import VueCropper from '@/components/VueCropper/vue-cropper.vue'

export default {
  name: 'singleUpload',
  components: {
    VueCropper
  },
  props: {
    value: String,
    // 是否启用裁剪功能
    enableCrop: {
      type: Boolean,
      default: false
    },
    // 裁剪宽高比（宽:高），例如 [1, 1] 表示 1:1，[16, 9] 表示 16:9
    cropAspectRatio: {
      type: Array,
      default: () => [1, 1]
    },
    // 自定义上传提示
    uploadTip: {
      type: String,
      default: ''
    },
    // 自定义验证函数，返回 { valid: boolean, message: string }
    customValidate: {
      type: Function,
      default: null
    }
  },
  computed: {
    headers() {
      return {
        Authorization: `${this.getLoginToken()}`,
      };
    },
    imageUrl() {
      return this.value;
    },
    imageName() {
      if (this.value != null && this.value !== '') {
        return this.value.substr(this.value.lastIndexOf('/') + 1);
      } else {
        return null;
      }
    },
    fileList() {
      return [
        {
          name: this.imageName,
          url: this.imageUrl,
        },
      ];
    },
    showFileList: {
      get: function () {
        return this.value !== null && this.value !== '' && this.value !== undefined;
      },
      set: function (newValue) {},
    },
    // 裁剪是否固定比例
    cropFixed() {
      return this.enableCrop && this.cropAspectRatio && this.cropAspectRatio.length === 2;
    },
    // 裁剪固定比例数值
    cropFixedNumber() {
      if (this.cropFixed) {
        return this.cropAspectRatio;
      }
      return [1, 1];
    },
    // 裁剪框默认宽度
    cropWidth() {
      return 400;
    },
    // 裁剪框默认高度
    cropHeight() {
      if (this.cropFixed) {
        return Math.round(this.cropWidth * this.cropAspectRatio[1] / this.cropAspectRatio[0]);
      }
      return 400;
    }
  },
  data() {
    return {
      dataObj: {
        policy: '',
        signature: '',
        key: '',
        ossaccessKeyId: '',
        dir: '',
        host: '',
      },
      dialogVisible: false,
      useOss: true,
      ossUploadUrl: process.env.BASE_API + '/aliyun/oss/upload',
      minioUploadUrl: process.env.BASE_API + '/minio/upload',
      uploadPercentage: 0,
      uploadStatus: '',
      // 裁剪相关
      cropDialogVisible: false,
      cropImageUrl: '',
      cropOutputType: 'png',
      cropUploading: false,
      currentFile: null
    };
  },
  methods: {
    emitInput(val) {
      this.$emit('input', val);
    },
    handleRemove(file, fileList) {
      this.emitInput('');
    },
    handlePreview(file) {
      this.dialogVisible = true;
    },

    async beforeUpload(file) {
      const isValidType = ['image/jpeg', 'image/png', 'image/gif', 'video/mp4', 'video/mov'].includes(file.type);
      const isValidSize = file.size / 1024 / 1024 < 50;

      if (!isValidType) {
        this.$message.error('仅支持上传 JPG/PNG/GIF 格式的图片或 MP4/MOV 格式的视频');
        return false;
      }
      if (!isValidSize) {
        this.$message.error('文件大小不能超过 50MB');
        return false;
      }

      // 如果有自定义验证函数，先执行自定义验证
      if (this.customValidate && typeof this.customValidate === 'function') {
        try {
          const result = await this.customValidate(file);
          if (!result.valid) {
            this.$message.error(result.message || '文件验证失败');
            return false;
          }
        } catch (error) {
          console.error('自定义验证失败:', error);
          this.$message.error('文件验证失败，请重试');
          return false;
        }
      }

      // 如果启用裁剪且是图片，直接显示裁剪对话框，不进行验证
      if (this.enableCrop && file.type.startsWith('image/')) {
        return new Promise((resolve, reject) => {
          const reader = new FileReader();
          reader.onload = (e) => {
            // 直接拉起裁剪组件，不进行验证
            this.handleCropImage(file, e.target.result);
            reject('crop'); // 阻止自动上传，等待裁剪完成
          };
          reader.readAsDataURL(file);
        });
      }

      return true;
    },
    handleUploadSuccess(res, file) {
      this.showFileList = true;
      this.fileList.pop();
      let url = this.useOss ? res.data : res.data.url;
      this.fileList.push({ name: file.name, url: url });
      this.emitInput(this.fileList[0].url);

      this.uploadPercentage = 100;
      this.uploadStatus = 'success';

      this.$message({
        message: '文件上传成功',
        type: 'success',
        duration: 2000,
      });
    },
    handleUploadError(err) {
      this.uploadStatus = 'exception';
      this.$message.error('文件上传失败，请重试');
      console.error(err);
    },
    handleUploadProgress(event) {
      this.uploadPercentage = Math.round((event.percent / 100) * 100);
      this.uploadStatus = 'active';
    },
    getLoginToken() {
      const match = document.cookie.match(/(^|;\s*)loginToken=([^;]+)/);
      return match ? decodeURIComponent(match[2]) : '';
    },
    // 处理图片裁剪
    handleCropImage(file, imageUrl) {
      this.currentFile = file;
      this.cropOutputType = file.type === 'image/png' ? 'png' : 'jpeg';
      this.cropImageUrl = imageUrl;
      this.cropDialogVisible = true;
    },
    // 对话框打开完成后的回调
    handleCropDialogOpened() {
      console.log('Crop dialog opened, image URL:', this.cropImageUrl);
    },
    // 关闭裁剪对话框
    handleCropDialogClose() {
      this.cropDialogVisible = false;
      this.cropImageUrl = '';
      this.currentFile = null;
      this.cropUploading = false;
    },
    // 确认裁剪并上传
    handleCropConfirm() {
      if (!this.$refs.cropper) {
        this.$message.error('裁剪组件未就绪，请重试');
        return;
      }

      this.$refs.cropper.getCropBlob((blob) => {
        if (!blob) {
          this.$message.error('裁剪失败，请重试');
          return;
        }

        // 创建 File 对象
        const fileName = this.currentFile.name;
        const file = new File([blob], fileName, { type: blob.type });

        // 开始上传
        this.cropUploading = true;
        this.uploadCroppedImage(file);
      });
    },
    // 上传裁剪后的图片
    uploadCroppedImage(file) {
      const formData = new FormData();

      if (this.useOss) {
        formData.append('file', file);
      } else {
        // MinIO 上传需要添加额外参数
        Object.keys(this.dataObj).forEach(key => {
          formData.append(key, this.dataObj[key]);
        });
        formData.append('file', file);
      }

      const uploadUrl = this.useOss ? this.ossUploadUrl : this.minioUploadUrl;

      // 使用 axios 上传
      const xhr = new XMLHttpRequest();
      xhr.open('POST', uploadUrl, true);
      xhr.setRequestHeader('Authorization', this.getLoginToken());

      xhr.upload.onprogress = (event) => {
        if (event.lengthComputable) {
          this.uploadPercentage = Math.round((event.loaded / event.total) * 100);
          this.uploadStatus = 'active';
        }
      };

      xhr.onload = () => {
        this.cropUploading = false;
        if (xhr.status === 200) {
          const res = JSON.parse(xhr.responseText);
          this.handleUploadSuccess(res, file);
          this.handleCropDialogClose();
        } else {
          this.handleUploadError(new Error('上传失败'));
        }
      };

      xhr.onerror = () => {
        this.cropUploading = false;
        this.handleUploadError(new Error('上传失败'));
      };

      xhr.send(formData);
    }
  },
};
</script>

<style scoped>
.upload-progress {
  margin-top: 10px;
}

.preview-image {
  max-width: 100%;
  max-height: 80vh;
  display: block;
  margin: 0 auto;
  object-fit: contain;
}

.crop-container {
  width: 100%;
  height: 500px;
  position: relative;
}

.crop-loading {
  width: 100%;
  height: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  color: #409EFF;
}

.crop-loading i {
  font-size: 24px;
  margin-right: 10px;
}

.dialog-footer {
  text-align: right;
}
</style>
