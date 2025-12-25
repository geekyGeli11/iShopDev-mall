<template>
  <div class="background-setting">
    <h3>{{title}}</h3>
    
    <el-form :model="localConfig" label-width="120px">
      <el-form-item label="背景类型:">
        <el-radio-group v-model="localConfig.type" @change="handleTypeChange">
          <el-radio label="color">纯色背景</el-radio>
          <el-radio label="image">图片背景</el-radio>
          <el-radio label="gradient">渐变背景</el-radio>
        </el-radio-group>
      </el-form-item>
      
      <!-- 纯色背景 -->
      <template v-if="localConfig.type === 'color'">
        <el-form-item label="背景颜色:">
          <el-color-picker v-model="localConfig.color" @change="emitUpdate"></el-color-picker>
          <span class="color-value">{{localConfig.color}}</span>
        </el-form-item>
      </template>
      
      <!-- 图片背景 -->
      <template v-if="localConfig.type === 'image'">
        <el-form-item label="背景图片:">
          <el-upload
            class="image-uploader"
            action="/admin/upload/image"
            :show-file-list="false"
            :on-success="handleImageSuccess"
            :before-upload="beforeImageUpload">
            <img v-if="localConfig.image" :src="localConfig.image" class="uploaded-image">
            <i v-else class="el-icon-plus image-uploader-icon"></i>
          </el-upload>
        </el-form-item>
        
        <el-form-item label="图片重复:">
          <el-select v-model="localConfig.repeat" @change="emitUpdate">
            <el-option label="不重复" value="no-repeat"></el-option>
            <el-option label="水平重复" value="repeat-x"></el-option>
            <el-option label="垂直重复" value="repeat-y"></el-option>
            <el-option label="全部重复" value="repeat"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="图片位置:">
          <el-select v-model="localConfig.position" @change="emitUpdate">
            <el-option label="居中" value="center center"></el-option>
            <el-option label="左上" value="left top"></el-option>
            <el-option label="右上" value="right top"></el-option>
            <el-option label="左下" value="left bottom"></el-option>
            <el-option label="右下" value="right bottom"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="图片大小:">
          <el-select v-model="localConfig.size" @change="emitUpdate">
            <el-option label="覆盖" value="cover"></el-option>
            <el-option label="包含" value="contain"></el-option>
            <el-option label="原始大小" value="auto"></el-option>
            <el-option label="拉伸" value="100% 100%"></el-option>
          </el-select>
        </el-form-item>
      </template>
      
      <!-- 渐变背景 -->
      <template v-if="localConfig.type === 'gradient'">
        <el-form-item label="渐变方向:">
          <el-select v-model="localConfig.gradient.direction" @change="emitUpdate">
            <el-option label="从上到下" value="to bottom"></el-option>
            <el-option label="从下到上" value="to top"></el-option>
            <el-option label="从左到右" value="to right"></el-option>
            <el-option label="从右到左" value="to left"></el-option>
            <el-option label="对角线(左上到右下)" value="to bottom right"></el-option>
            <el-option label="对角线(右上到左下)" value="to bottom left"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="起始颜色:">
          <el-color-picker v-model="localConfig.gradient.colors[0]" @change="emitUpdate"></el-color-picker>
          <span class="color-value">{{localConfig.gradient.colors[0]}}</span>
        </el-form-item>
        
        <el-form-item label="结束颜色:">
          <el-color-picker v-model="localConfig.gradient.colors[1]" @change="emitUpdate"></el-color-picker>
          <span class="color-value">{{localConfig.gradient.colors[1]}}</span>
        </el-form-item>
      </template>
      
      <el-form-item label="透明度:">
        <el-slider 
          v-model="localConfig.opacity" 
          :min="0" 
          :max="1" 
          :step="0.1"
          @change="emitUpdate">
        </el-slider>
      </el-form-item>
    </el-form>
    
    <!-- 预览区域 -->
    <div class="preview-section">
      <h4>预览效果</h4>
      <div class="preview-container" :style="previewStyle">
        <div class="preview-content">
          <p>这是页面内容预览</p>
          <div class="preview-button">按钮示例</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "BackgroundSetting",
  props: {
    config: {
      type: Object,
      required: true
    },
    title: {
      type: String,
      default: '背景设置'
    }
  },
  data() {
    return {
      localConfig: { ...this.config }
    }
  },
  computed: {
    previewStyle() {
      let style = {
        opacity: this.localConfig.opacity
      };
      
      if (this.localConfig.type === 'color') {
        style.backgroundColor = this.localConfig.color;
      } else if (this.localConfig.type === 'image' && this.localConfig.image) {
        style.backgroundImage = `url(${this.localConfig.image})`;
        style.backgroundRepeat = this.localConfig.repeat;
        style.backgroundPosition = this.localConfig.position;
        style.backgroundSize = this.localConfig.size;
      } else if (this.localConfig.type === 'gradient') {
        const { direction, colors } = this.localConfig.gradient;
        style.background = `linear-gradient(${direction}, ${colors[0]}, ${colors[1]})`;
      }
      
      return style;
    }
  },
  watch: {
    config: {
      handler(newVal) {
        this.localConfig = { ...newVal };
      },
      deep: true
    }
  },
  methods: {
    handleTypeChange() {
      this.emitUpdate();
    },
    emitUpdate() {
      this.$emit('update', { ...this.localConfig });
    },
    handleImageSuccess(response) {
      if (response.code === 200) {
        this.localConfig.image = response.data.url;
        this.emitUpdate();
        this.$message.success('图片上传成功');
      } else {
        this.$message.error('图片上传失败');
      }
    },
    beforeImageUpload(file) {
      const isImage = file.type.indexOf('image/') === 0;
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isImage) {
        this.$message.error('只能上传图片文件!');
      }
      if (!isLt2M) {
        this.$message.error('上传图片大小不能超过 2MB!');
      }
      return isImage && isLt2M;
    }
  }
}
</script>

<style scoped>
.background-setting h3 {
  margin-bottom: 20px;
  color: #303133;
}

.color-value {
  margin-left: 10px;
  color: #909399;
  font-family: monospace;
}

.image-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.image-uploader:hover {
  border-color: #409EFF;
}

.image-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}

.uploaded-image {
  width: 178px;
  height: 178px;
  display: block;
}

.preview-section {
  margin-top: 30px;
}

.preview-section h4 {
  margin-bottom: 15px;
  color: #303133;
}

.preview-container {
  width: 100%;
  height: 200px;
  border: 1px solid #DCDFE6;
  border-radius: 8px;
  position: relative;
  overflow: hidden;
}

.preview-content {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  color: #303133;
}

.preview-button {
  background-color: #409EFF;
  color: white;
  padding: 8px 16px;
  border-radius: 4px;
  margin-top: 10px;
  display: inline-block;
}
</style>
