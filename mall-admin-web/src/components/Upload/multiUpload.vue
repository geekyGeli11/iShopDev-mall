<template> 
  <div>
    <el-upload :key="uploadKey" :action="useOss ? ossUploadUrl : minioUploadUrl" :data="useOss ? dataObj : null" :headers="headers"
      list-type="picture-card" :file-list="fileList" :before-upload="beforeUpload" :on-remove="handleRemove"
      :on-success="handleUploadSuccess" :on-preview="handlePreview" :limit="maxCount" :on-exceed="handleExceed"
      :on-change="handleChange" :multiple="true" accept="image/*">
      <i class="el-icon-plus"></i>
    </el-upload>
    <el-dialog :visible.sync="dialogVisible">
      <img width="100%" :src="dialogImageUrl" alt="">
    </el-dialog>
    <div v-show="showTip" class="el-upload__tip">可通过拖拽调整图片顺序，支持多选上传</div>
  </div>
</template>
<script>
import Sortable from 'sortablejs'

export default {
  name: 'multiUpload',
  props: {
    //图片属性数组
    value: Array,
    //最大上传图片数量
    maxCount: {
      type: Number,
      default: 5
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
        host: ''
      },
      dialogVisible: false,
      dialogImageUrl: null,
      useOss: true, //使用oss->true;使用MinIO->false
      ossUploadUrl: process.env.BASE_API + '/aliyun/oss/upload',
      minioUploadUrl: process.env.BASE_API + '/minio/upload',
      showTip: false,
      sortable: null,
      // 用于保存当前的文件列表
      currentFileList: [],
      // 标识是否正在拖拽中，避免外部更新干扰
      isDragging: false,
      // 用于强制重新渲染的key
      uploadKey: 0
    };
  },
  computed: {
    headers() {
      return {
        Authorization: `${this.getLoginToken()}`,
      };
    },
    fileList() {
      return this.currentFileList;
    }
  },
  watch: {
    value: {
      handler() {
        // 拖拽过程中不要同步外部数据，避免干扰
        if (!this.isDragging) {
          this.syncFromValue();
        }
      },
      deep: true
    }
  },
  mounted() {
    // 初始化时设置currentFileList
    this.syncFromValue();
    this.$nextTick(() => {
      this.initSortable();
    });
  },
  updated() {
    // 拖拽过程中不要重新初始化sortable
    if (this.isDragging) {
      return;
    }
    
    this.$nextTick(() => {
      if (this.currentFileList.length > 0) {
        this.initSortable();
      }
    });
  },
  beforeDestroy() {
    if (this.sortable) {
      this.sortable.destroy();
      this.sortable = null;
    }
  },
  methods: {
    syncFromValue() {
      if (this.value && this.value.length > 0) {
        const newFileList = this.value.map((url, index) => ({ 
          url, 
          name: `image-${index}`,
          uid: `file-${index}-${Date.now()}` // 每次都生成新的uid
        }));
        this.currentFileList = newFileList;
        // 更新key强制重新渲染
        this.uploadKey++;
      } else {
        this.currentFileList = [];
        this.uploadKey++;
      }
    },
    emitInput(fileList) {
      let value = [];
      for (let i = 0; i < fileList.length; i++) {
        value.push(fileList[i].url);
      }
      // 更新内部状态
      this.currentFileList = fileList;
      this.$emit('input', value);
    },
    handleRemove(file, fileList) {
      this.currentFileList = fileList;
      this.emitInput(fileList);
    },
    handlePreview(file) {
      this.dialogVisible = true;
      this.dialogImageUrl = file.url;
    },
    beforeUpload(file) {
      const isValidType = ['image/jpeg', 'image/png', 'image/gif'].includes(file.type);
      const isValidSize = file.size / 1024 / 1024 < 50;

      if (!isValidType) {
        this.$message.error(`文件 ${file.name} 格式不支持，仅支持 JPG/PNG/GIF 格式`);
        return false;
      }
      if (!isValidSize) {
        this.$message.error(`文件 ${file.name} 大小超过限制，不能超过 50MB`);
        return false;
      }
      return true;
    },
    handleUploadSuccess(res, file, fileList) {
      let url = this.useOss
        ? res.data // 使用 OSS 的 URL
        : res.data.url; // 使用 MinIO 返回的 URL
      
      // 添加新上传成功的文件
      let newFileList = [...this.currentFileList];
      newFileList.push({ 
        name: file.name, 
        url: url,
        uid: `file-${newFileList.length}-${Date.now()}`
      });
      
      this.emitInput(newFileList);
      this.showTip = newFileList.length > 1;
      
      // 更新uploadKey强制重新渲染
      this.uploadKey++;
    },
    handleExceed(files, fileList) {
      this.$message({
        message: '最多只能上传' + this.maxCount + '张图片',
        type: 'warning',
        duration: 1000
      });
    },
    // 获取 loginToken
    getLoginToken() {
      const match = document.cookie.match(/(^|;\s*)loginToken=([^;]+)/);
      return match ? decodeURIComponent(match[2]) : '';
    },
    // 初始化拖拽排序
    initSortable() {
      const el = this.$el.querySelector('.el-upload-list');
      if (!el) {
        return;
      }
      
      // 避免重复初始化
      if (this.sortable) {
        this.sortable.destroy();
        this.sortable = null;
      }
      
      // 只有当有文件时才初始化sortable
      if (this.currentFileList.length === 0) {
        return;
      }
      
      this.sortable = Sortable.create(el, {
        animation: 300,
        easing: 'cubic-bezier(1, 0, 0, 1)',
        ghostClass: 'sortable-ghost',
        chosenClass: 'sortable-chosen',
        dragClass: 'sortable-drag',
        forceFallback: true, // 强制使用HTML5 DnD
        fallbackTolerance: 3,
        onStart: evt => {
          // 拖拽开始
          this.isDragging = true;
          evt.item.style.opacity = '0.8';
          // 添加拖拽中的样式
          this.$el.classList.add('dragging');
        },
        onEnd: evt => {
          // 拖拽结束
          evt.item.style.opacity = '';
          this.$el.classList.remove('dragging');
          
          const oldIndex = evt.oldIndex;
          const newIndex = evt.newIndex;
          
          // 如果位置没有变化，不需要处理
          if (oldIndex === newIndex) {
            return;
          }
          
          // 获取当前的URL数组
          const urlArray = this.currentFileList.map(f => f.url);
          
          // 在URL数组上执行交换操作
          const movedUrl = urlArray.splice(oldIndex, 1)[0];
          urlArray.splice(newIndex, 0, movedUrl);
          
          // 立即更新内部状态
          this.currentFileList = urlArray.map((url, index) => ({
            url,
            name: `image-${index}`,
            uid: `file-${index}-${Date.now()}`
          }));
          
          // 触发父组件更新
          this.$emit('input', urlArray);
          
          // 立即结束拖拽状态
          this.isDragging = false;
          
          // 更新uploadKey强制重新渲染upload组件
          this.uploadKey++;
          
          // 延迟重新初始化sortable
          this.$nextTick(() => {
            setTimeout(() => {
              this.showTip = this.currentFileList.length > 1;
              this.initSortable();
            }, 100);
          });
        }
      });
      
      this.showTip = this.currentFileList.length > 1;
    },
    // 处理文件列表变化
    handleChange(file, fileList) {
      // 不在这里处理，让handleUploadSuccess处理成功上传
      this.showTip = this.currentFileList.length > 1;
    }
  }
}
</script>
<style>
.el-upload__tip {
  font-size: 12px;
  color: #606266;
  margin-top: 7px;
  text-align: center;
}

/* 拖拽排序样式 */
.sortable-ghost {
  opacity: 0.4;
  background: #f0f9ff;
  border: 2px dashed #409eff;
  border-radius: 6px;
}

.sortable-chosen {
  cursor: move;
  transform: rotate(2deg);
  z-index: 999;
}

.sortable-drag {
  transform: rotate(5deg);
  opacity: 0.8;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
}

/* 使上传列表项可拖拽 */
.el-upload-list .el-upload-list__item {
  cursor: move;
  transition: all 0.3s ease;
  position: relative;
}

.el-upload-list .el-upload-list__item:hover {
  background-color: #f5f7fa;
  transform: scale(1.02);
}

/* 拖拽状态下的容器样式 */
.dragging .el-upload-list .el-upload-list__item:not(.sortable-chosen) {
  opacity: 0.7;
}

/* 拖拽提示样式优化 */
.el-upload__tip {
  background: #e6f7ff;
  border: 1px solid #91d5ff;
  border-radius: 4px;
  padding: 5px 10px;
  color: #1890ff;
}

/* 图片列表布局优化 */
.el-upload-list--picture-card .el-upload-list__item {
  margin-right: 10px;
  margin-bottom: 10px;
  border-radius: 6px;
}

.el-upload-list--picture-card .el-upload-list__item:hover .el-upload-list__item-actions {
  opacity: 1;
}
</style>
