<template>
  <view class="image-processor">
    <!-- 隐藏的Canvas用于图片处理 -->
    <canvas 
      canvas-id="imageProcessorCanvas" 
      :style="{ width: canvasWidth + 'px', height: canvasHeight + 'px', position: 'absolute', left: '-9999px' }"
    ></canvas>
  </view>
</template>

<script>
export default {
  name: 'ImageProcessor',
  data() {
    return {
      canvasWidth: 800,
      canvasHeight: 600,
      ctx: null
    };
  },
  
  mounted() {
    this.initCanvas();
  },
  
  methods: {
    /**
     * 初始化Canvas
     */
    initCanvas() {
      this.ctx = uni.createCanvasContext('imageProcessorCanvas', this);
    },

    /**
     * 根据定制区域预处理图片
     * @param {String} imagePath 图片路径
     * @param {Object} customizableArea 定制区域信息
     * @returns {Promise<String>} 处理后的图片路径
     */
    async processImageForCustomArea(imagePath, customizableArea) {
      try {
        console.log('开始预处理图片:', { imagePath, customizableArea });
        
        // 1. 获取图片信息
        const imageInfo = await this.getImageInfo(imagePath);
        console.log('图片信息:', imageInfo);
        
        // 2. 根据定制区域类型选择处理策略
        const processedImagePath = await this.processImageByAreaType(imageInfo, customizableArea);
        
        console.log('图片预处理完成:', processedImagePath);
        return processedImagePath;
        
      } catch (error) {
        console.error('图片预处理失败:', error);
        throw error;
      }
    },

    /**
     * 根据区域类型处理图片
     */
    async processImageByAreaType(imageInfo, customizableArea) {
      const areaType = this.detectAreaType(customizableArea);
      console.log('检测到区域类型:', areaType);
      
      switch (areaType) {
        case 'circle':
          return await this.processImageForCircle(imageInfo, customizableArea);
        case 'rectangle':
          return await this.processImageForRectangle(imageInfo, customizableArea);
        default:
          return await this.processImageForCustomShape(imageInfo, customizableArea);
      }
    },

    /**
     * 检测区域类型
     */
    detectAreaType(customizableArea) {
      if (!customizableArea.customizableRegions || customizableArea.customizableRegions.length === 0) {
        return 'rectangle';
      }
      
      const region = customizableArea.customizableRegions[0];
      const pathData = region.pathData || '';
      
      // 检查是否为圆形路径
      if (pathData.includes('A ') || pathData.includes('a ')) {
        return 'circle';
      }
      
      // 检查是否为标准矩形路径
      if (pathData.match(/^M\s+\d+\s+\d+\s+L.*Z$/)) {
        return 'rectangle';
      }
      
      return 'custom';
    },

    /**
     * 为圆形区域处理图片
     */
    async processImageForCircle(imageInfo, customizableArea) {
      const region = customizableArea.customizableRegions[0];
      const size = Math.max(region.width || 200, region.height || 200);
      
      // 设置Canvas尺寸为正方形
      this.canvasWidth = size;
      this.canvasHeight = size;
      
      // 清空Canvas
      this.ctx.clearRect(0, 0, size, size);
      
      // 创建圆形裁剪路径
      this.ctx.save();
      this.ctx.beginPath();
      this.ctx.arc(size / 2, size / 2, size / 2, 0, 2 * Math.PI);
      this.ctx.clip();
      
      // 计算图片缩放以填满圆形
      const scale = Math.max(size / imageInfo.width, size / imageInfo.height);
      const scaledWidth = imageInfo.width * scale;
      const scaledHeight = imageInfo.height * scale;
      const x = (size - scaledWidth) / 2;
      const y = (size - scaledHeight) / 2;
      
      // 绘制图片
      this.ctx.drawImage(imageInfo.path, x, y, scaledWidth, scaledHeight);
      this.ctx.restore();
      this.ctx.draw();
      
      // 导出处理后的图片
      return await this.exportCanvasImage();
    },

    /**
     * 为矩形区域处理图片
     */
    async processImageForRectangle(imageInfo, customizableArea) {
      const region = customizableArea.customizableRegions[0];
      const width = region.width || 200;
      const height = region.height || 200;
      
      // 设置Canvas尺寸
      this.canvasWidth = width;
      this.canvasHeight = height;
      
      // 清空Canvas
      this.ctx.clearRect(0, 0, width, height);
      
      // 计算图片缩放以填满矩形
      const scale = Math.max(width / imageInfo.width, height / imageInfo.height);
      const scaledWidth = imageInfo.width * scale;
      const scaledHeight = imageInfo.height * scale;
      const x = (width - scaledWidth) / 2;
      const y = (height - scaledHeight) / 2;
      
      // 绘制图片
      this.ctx.drawImage(imageInfo.path, x, y, scaledWidth, scaledHeight);
      this.ctx.draw();
      
      // 导出处理后的图片
      return await this.exportCanvasImage();
    },

    /**
     * 为自定义形状处理图片
     */
    async processImageForCustomShape(imageInfo, customizableArea) {
      const region = customizableArea.customizableRegions[0];
      const width = region.width || 200;
      const height = region.height || 200;
      
      // 设置Canvas尺寸
      this.canvasWidth = width;
      this.canvasHeight = height;
      
      // 清空Canvas
      this.ctx.clearRect(0, 0, width, height);
      
      // 如果有SVG路径，创建裁剪路径
      if (region.pathData) {
        this.ctx.save();
        this.createSVGClipPath(region.pathData);
        this.ctx.clip();
      }
      
      // 计算图片缩放
      const scale = Math.max(width / imageInfo.width, height / imageInfo.height);
      const scaledWidth = imageInfo.width * scale;
      const scaledHeight = imageInfo.height * scale;
      const x = (width - scaledWidth) / 2;
      const y = (height - scaledHeight) / 2;
      
      // 绘制图片
      this.ctx.drawImage(imageInfo.path, x, y, scaledWidth, scaledHeight);
      
      if (region.pathData) {
        this.ctx.restore();
      }
      
      this.ctx.draw();
      
      // 导出处理后的图片
      return await this.exportCanvasImage();
    },

    /**
     * 创建SVG裁剪路径
     */
    createSVGClipPath(pathData) {
      try {
        // 简化的SVG路径解析
        const commands = pathData.match(/[MmLlHhVvCcSsQqTtAaZz][^MmLlHhVvCcSsQqTtAaZz]*/g);
        if (!commands) return;
        
        this.ctx.beginPath();
        
        for (const command of commands) {
          const type = command[0];
          const coords = command.slice(1).trim().split(/[\s,]+/).map(Number);
          
          switch (type) {
            case 'M':
              if (coords.length >= 2) {
                this.ctx.moveTo(coords[0], coords[1]);
              }
              break;
            case 'L':
              if (coords.length >= 2) {
                this.ctx.lineTo(coords[0], coords[1]);
              }
              break;
            case 'Z':
            case 'z':
              this.ctx.closePath();
              break;
          }
        }
      } catch (error) {
        console.warn('SVG路径解析失败，使用矩形裁剪:', error);
      }
    },

    /**
     * 获取图片信息
     */
    getImageInfo(imagePath) {
      return new Promise((resolve, reject) => {
        uni.getImageInfo({
          src: imagePath,
          success: (res) => {
            resolve(res);
          },
          fail: (error) => {
            reject(error);
          }
        });
      });
    },

    /**
     * 导出Canvas图片
     */
    exportCanvasImage() {
      return new Promise((resolve, reject) => {
        uni.canvasToTempFilePath({
          canvasId: 'imageProcessorCanvas',
          success: (res) => {
            resolve(res.tempFilePath);
          },
          fail: (error) => {
            reject(error);
          }
        }, this);
      });
    }
  }
};
</script>

<style scoped>
.image-processor {
  position: relative;
}
</style>
