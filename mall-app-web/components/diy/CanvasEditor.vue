<template>
  <view class="canvas-editor">
    <canvas 
      canvas-id="diy-canvas"
      class="canvas"
      :style="{ width: canvasWidth + 'px', height: canvasHeight + 'px' }"
      @touchstart="handleTouchStart"
      @touchmove="handleTouchMove"
      @touchend="handleTouchEnd"
      @tap="handleTap"
    ></canvas>
    
    <!-- 选中素材的控制点 -->
    <view v-if="selectedElement" class="control-points">
      <!-- 删除按钮 -->
      <view
        class="delete-btn"
        :style="deleteBtnStyle"
        @tap="deleteSelectedElement"
      >
        <text class="delete-icon">×</text>
      </view>

      <!-- 缩放控制点 -->
      <view
        class="scale-handle"
        :style="scaleHandleStyle"
        @touchstart="startScale"
        @touchmove="handleScale"
        @touchend="endScale"
      ></view>

      <!-- 旋转控制点 -->
      <view
        class="rotate-handle"
        :style="rotateHandleStyle"
        @touchstart="startRotate"
        @touchmove="handleRotate"
        @touchend="endRotate"
      ></view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'CanvasEditor',
  props: {
    width: {
      type: Number,
      default: 375
    },
    height: {
      type: Number,
      default: 400
    }
  },
  data() {
    return {
      canvasWidth: 375,
      canvasHeight: 400,
      ctx: null,
      elements: [], // 画布上的所有元素
      selectedElement: null, // 当前选中的元素
      history: [], // 操作历史
      historyIndex: -1, // 当前历史索引
      
      // 触摸相关
      touchStartX: 0,
      touchStartY: 0,
      isDragging: false,
      isScaling: false,
      isRotating: false,
      
      // 缩放相关
      initialDistance: 0,
      initialScale: 1,
      
      // 旋转相关
      initialAngle: 0,
      initialRotation: 0
    };
  },
  computed: {
    canUndo() {
      return this.historyIndex > 0;
    },
    canRedo() {
      return this.historyIndex < this.history.length - 1;
    },
    deleteBtnStyle() {
      if (!this.selectedElement) return {};

      return {
        left: (this.selectedElement.x - 20) + 'px',
        top: (this.selectedElement.y - 20) + 'px'
      };
    },
    scaleHandleStyle() {
      if (!this.selectedElement) return {};

      return {
        left: (this.selectedElement.x + this.selectedElement.width + 5) + 'px',
        top: (this.selectedElement.y + this.selectedElement.height + 5) + 'px'
      };
    },
    rotateHandleStyle() {
      if (!this.selectedElement) return {};

      return {
        left: (this.selectedElement.x + this.selectedElement.width / 2 - 10) + 'px',
        top: (this.selectedElement.y - 30) + 'px'
      };
    }
  },
  mounted() {
    this.initCanvas();
  },
  methods: {
    // 初始化画布
    initCanvas() {
      this.canvasWidth = this.width;
      this.canvasHeight = this.height;
      
      this.$nextTick(() => {
        this.ctx = uni.createCanvasContext('diy-canvas', this);
        this.drawCanvas();
        this.saveToHistory();
      });
    },
    
    // 绘制画布
    drawCanvas() {
      if (!this.ctx) return;
      
      // 清空画布
      this.ctx.clearRect(0, 0, this.canvasWidth, this.canvasHeight);
      
      // 绘制背景
      this.ctx.setFillStyle('#f5f5f5');
      this.ctx.fillRect(0, 0, this.canvasWidth, this.canvasHeight);
      
      // 绘制所有元素
      this.elements.forEach(element => {
        this.drawElement(element);
      });
      
      // 绘制选中元素的边框
      if (this.selectedElement) {
        this.drawSelectionBorder(this.selectedElement);
      }
      
      this.ctx.draw();
    },
    
    // 绘制单个元素
    drawElement(element) {
      if (!this.ctx) return;
      
      this.ctx.save();
      
      // 移动到元素中心
      this.ctx.translate(element.x + element.width / 2, element.y + element.height / 2);
      
      // 旋转
      this.ctx.rotate(element.rotation * Math.PI / 180);
      
      // 缩放
      this.ctx.scale(element.scaleX, element.scaleY);
      
      if (element.type === 'image' && element.loaded !== false) {
        // 绘制图片（只有加载完成的图片才绘制）
        this.ctx.drawImage(
          element.src,
          -element.width / 2,
          -element.height / 2,
          element.width,
          element.height
        );
      } else if (element.type === 'image' && element.loaded === false) {
        // 绘制加载中的占位符
        this.ctx.setFillStyle('#f0f0f0');
        this.ctx.fillRect(
          -element.width / 2,
          -element.height / 2,
          element.width,
          element.height
        );
        this.ctx.setFillStyle('#999999');
        this.ctx.setFontSize(12);
        this.ctx.fillText('加载中...', -20, 0);
      } else if (element.type === 'text') {
        // 绘制文字
        this.ctx.setFillStyle(element.color || '#000000');
        this.ctx.setFontSize(element.fontSize || 16);
        this.ctx.fillText(
          element.text,
          -element.width / 2,
          -element.height / 2
        );
      }
      
      this.ctx.restore();
    },
    
    // 绘制选中边框
    drawSelectionBorder(element) {
      if (!this.ctx) return;
      
      this.ctx.save();
      this.ctx.setStrokeStyle('#007AFF');
      this.ctx.setLineWidth(2);
      this.ctx.strokeRect(
        element.x - 2,
        element.y - 2,
        element.width + 4,
        element.height + 4
      );
      this.ctx.restore();
    },
    
    // 添加素材到画布
    addMaterial(material) {
      const element = {
        id: Date.now(),
        type: 'image',
        src: material.fileUrl || material.image,
        x: (this.canvasWidth - 100) / 2, // 居中
        y: (this.canvasHeight - 100) / 2,
        width: 100,
        height: 100,
        scaleX: 1,
        scaleY: 1,
        rotation: 0,
        name: material.name,
        loaded: false
      };

      // 预加载图片
      this.preloadImage(element.src).then(() => {
        element.loaded = true;
        this.drawCanvas();
      }).catch(err => {
        console.error('图片加载失败:', err);
      });

      this.elements.push(element);
      this.selectedElement = element;
      this.drawCanvas();
      this.saveToHistory();

      this.$emit('element-added', element);
    },

    // 预加载图片
    preloadImage(src) {
      return new Promise((resolve, reject) => {
        // 在小程序中，我们可以使用uni.getImageInfo来预加载图片
        uni.getImageInfo({
          src: src,
          success: resolve,
          fail: reject
        });
      });
    },
    
    // 处理触摸开始
    handleTouchStart(e) {
      const touch = e.touches[0];
      const touchX = touch.x || touch.clientX;
      const touchY = touch.y || touch.clientY;

      this.touchStartX = touchX;
      this.touchStartY = touchY;

      // 检查是否点击了某个元素
      const clickedElement = this.getElementAtPoint(touchX, touchY);
      if (clickedElement) {
        this.selectedElement = clickedElement;
        this.isDragging = true;
      } else {
        this.selectedElement = null;
      }

      this.drawCanvas();
    },

    // 处理触摸移动
    handleTouchMove(e) {
      if (!this.isDragging || !this.selectedElement) return;

      const touch = e.touches[0];
      const touchX = touch.x || touch.clientX;
      const touchY = touch.y || touch.clientY;

      const deltaX = touchX - this.touchStartX;
      const deltaY = touchY - this.touchStartY;

      this.selectedElement.x += deltaX;
      this.selectedElement.y += deltaY;

      this.touchStartX = touchX;
      this.touchStartY = touchY;

      this.drawCanvas();
    },
    
    // 处理触摸结束
    handleTouchEnd(e) {
      if (this.isDragging) {
        this.saveToHistory();
      }

      this.isDragging = false;
      this.isScaling = false;
      this.isRotating = false;
    },

    // 处理点击事件
    handleTap(e) {
      // 如果不是拖拽状态，处理点击选择
      if (!this.isDragging) {
        const touch = e.detail || e.touches[0] || e.changedTouches[0];
        const touchX = touch.x || touch.clientX;
        const touchY = touch.y || touch.clientY;

        const clickedElement = this.getElementAtPoint(touchX, touchY);
        this.selectedElement = clickedElement;
        this.drawCanvas();
      }
    },
    
    // 获取指定点的元素
    getElementAtPoint(x, y) {
      // 从后往前遍历（后添加的在上层）
      for (let i = this.elements.length - 1; i >= 0; i--) {
        const element = this.elements[i];
        if (x >= element.x && x <= element.x + element.width &&
            y >= element.y && y <= element.y + element.height) {
          return element;
        }
      }
      return null;
    },
    
    // 删除选中元素
    deleteSelectedElement() {
      if (!this.selectedElement) return;
      
      const index = this.elements.indexOf(this.selectedElement);
      if (index > -1) {
        this.elements.splice(index, 1);
        this.selectedElement = null;
        this.drawCanvas();
        this.saveToHistory();
        
        this.$emit('element-deleted');
      }
    },
    
    // 保存到历史记录
    saveToHistory() {
      // 移除当前索引之后的历史记录
      this.history = this.history.slice(0, this.historyIndex + 1);
      
      // 添加新的历史记录
      this.history.push(JSON.stringify({
        elements: this.elements,
        selectedElement: this.selectedElement ? this.selectedElement.id : null
      }));
      
      this.historyIndex++;
      
      // 限制历史记录数量
      if (this.history.length > 50) {
        this.history.shift();
        this.historyIndex--;
      }
      
      this.$emit('history-changed', {
        canUndo: this.canUndo,
        canRedo: this.canRedo
      });
    },
    
    // 撤销操作
    undo() {
      if (!this.canUndo) return;
      
      this.historyIndex--;
      this.restoreFromHistory();
    },
    
    // 重做操作
    redo() {
      if (!this.canRedo) return;
      
      this.historyIndex++;
      this.restoreFromHistory();
    },
    
    // 从历史记录恢复
    restoreFromHistory() {
      if (this.historyIndex < 0 || this.historyIndex >= this.history.length) return;

      const state = JSON.parse(this.history[this.historyIndex]);
      this.elements = state.elements;

      // 恢复选中元素
      if (state.selectedElement) {
        this.selectedElement = this.elements.find(el => el.id === state.selectedElement);
      } else {
        this.selectedElement = null;
      }

      this.drawCanvas();

      this.$emit('history-changed', {
        canUndo: this.canUndo,
        canRedo: this.canRedo
      });
    },

    // 开始缩放
    startScale(e) {
      e.stopPropagation();
      this.isScaling = true;
      this.initialScale = this.selectedElement.scaleX;

      const touch = e.touches[0];
      this.touchStartX = touch.x || touch.clientX;
      this.touchStartY = touch.y || touch.clientY;
    },

    // 处理缩放
    handleScale(e) {
      if (!this.isScaling || !this.selectedElement) return;

      e.stopPropagation();
      const touch = e.touches[0];
      const deltaX = (touch.x || touch.clientX) - this.touchStartX;
      const deltaY = (touch.y || touch.clientY) - this.touchStartY;

      const distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
      const scale = this.initialScale + distance / 100;

      this.selectedElement.scaleX = Math.max(0.1, Math.min(3, scale));
      this.selectedElement.scaleY = this.selectedElement.scaleX;

      this.drawCanvas();
    },

    // 结束缩放
    endScale(e) {
      e.stopPropagation();
      if (this.isScaling) {
        this.saveToHistory();
      }
      this.isScaling = false;
    },

    // 开始旋转
    startRotate(e) {
      e.stopPropagation();
      this.isRotating = true;
      this.initialRotation = this.selectedElement.rotation;

      const touch = e.touches[0];
      const centerX = this.selectedElement.x + this.selectedElement.width / 2;
      const centerY = this.selectedElement.y + this.selectedElement.height / 2;

      this.initialAngle = Math.atan2(
        (touch.y || touch.clientY) - centerY,
        (touch.x || touch.clientX) - centerX
      ) * 180 / Math.PI;
    },

    // 处理旋转
    handleRotate(e) {
      if (!this.isRotating || !this.selectedElement) return;

      e.stopPropagation();
      const touch = e.touches[0];
      const centerX = this.selectedElement.x + this.selectedElement.width / 2;
      const centerY = this.selectedElement.y + this.selectedElement.height / 2;

      const currentAngle = Math.atan2(
        (touch.y || touch.clientY) - centerY,
        (touch.x || touch.clientX) - centerX
      ) * 180 / Math.PI;

      const deltaAngle = currentAngle - this.initialAngle;
      this.selectedElement.rotation = this.initialRotation + deltaAngle;

      this.drawCanvas();
    },

    // 结束旋转
    endRotate(e) {
      e.stopPropagation();
      if (this.isRotating) {
        this.saveToHistory();
      }
      this.isRotating = false;
    },

    // 清空画布
    clearCanvas() {
      this.elements = [];
      this.selectedElement = null;
      this.drawCanvas();
      this.saveToHistory();
    },

    // 导出画布数据
    exportCanvas() {
      return {
        elements: this.elements,
        width: this.canvasWidth,
        height: this.canvasHeight
      };
    },

    // 导入画布数据
    importCanvas(data) {
      this.elements = data.elements || [];
      this.selectedElement = null;
      this.drawCanvas();
      this.saveToHistory();
    }
  }
};
</script>

<style lang="scss" scoped>
.canvas-editor {
  position: relative;
  background: #f5f5f5;
  border-radius: 8rpx;
  overflow: hidden;
}

.canvas {
  display: block;
  background: #ffffff;
}

.control-points {
  position: absolute;
  top: 0;
  left: 0;
  pointer-events: none;
}

.delete-btn {
  position: absolute;
  width: 40rpx;
  height: 40rpx;
  background: #ff4757;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  pointer-events: auto;
  z-index: 10;
}

.delete-icon {
  color: #ffffff;
  font-size: 24rpx;
  font-weight: bold;
}

.scale-handle,
.rotate-handle {
  position: absolute;
  width: 20rpx;
  height: 20rpx;
  background: #007AFF;
  border: 2rpx solid #ffffff;
  border-radius: 50%;
  pointer-events: auto;
  z-index: 10;
}

.scale-handle {
  background: #28a745;
}

.rotate-handle {
  background: #ffc107;
}
</style>
