<template>
  <view class="simple-canvas-editor" :style="containerStyle">
    <canvas
      canvas-id="simple-diy-canvas"
      id="simple-diy-canvas"
      class="canvas"
      :style="canvasStyle"
      :width="actualWidth"
      :height="actualHeight"
      @touchstart="handleTouchStart"
      @touchmove="handleTouchMove"
      @touchend="handleTouchEnd"
    ></canvas>
    
    <!-- 选中素材的控制点（锁定状态下隐藏，拖拽时也隐藏，底图选中时也隐藏） -->
    <view v-if="selectedElement && !isLocked && !isDragging && !selectedElement.isBackground" class="control-points" :style="controlPointsStyle">
      <!-- 删除按钮 -->
      <view
        class="delete-btn"
        :style="deleteBtnStyle"
        @tap="deleteSelectedElement"
      >
        <text class="delete-icon">×</text>
      </view>
    </view>

    <!-- 图片处理器组件 -->
    <ImageProcessor ref="imageProcessor" />
  </view>
</template>

<script>
import ImageProcessor from './ImageProcessor.vue';

export default {
  name: 'SimpleCanvasEditor',
  components: {
    ImageProcessor
  },
  props: {
    width: {
      type: Number,
      default: 303
    },
    height: {
      type: Number,
      default: 348
    },
    backgroundImage: {
      type: String,
      default: ''
    },
    elements: {
      type: Array,
      default: () => []
    },
    currentFaceId: {
      type: [String, Number],
      default: null
    },
    isLocked: {
      type: Boolean,
      default: false
    },
    // 是否根据背景图自动调整画布尺寸
    autoResize: {
      type: Boolean,
      default: true
    },
    // 最大画布尺寸限制
    maxWidth: {
      type: Number,
      default: 400
    },
    maxHeight: {
      type: Number,
      default: 500
    }
  },
  data() {
    return {
      ctx: null,
      canvasElements: [], // 画布上的所有元素（内部状态）
      selectedElement: null, // 当前选中的元素
      history: [], // 操作历史
      historyIndex: -1, // 当前历史索引

      // 触摸相关
      touchStartX: 0,
      touchStartY: 0,
      isDragging: false,

      // 手势操作相关
      isGesturing: false,
      gestureStartDistance: 0,
      gestureStartAngle: 0,
      gestureStartScale: 1,
      gestureStartRotation: 0,
      touches: [],

      // 底图相关
      backgroundLoaded: false,
      backgroundImageObj: null,
      backgroundElement: null, // 底图作为可交互元素

      // 动态画布尺寸
      dynamicWidth: 303,
      dynamicHeight: 348,
      canvasInitialized: false,

      // 双击检测
      lastTapTime: 0
    };
  },
  computed: {
    // 实际使用的画布宽度
    actualWidth() {
      return this.autoResize && this.canvasInitialized ? this.dynamicWidth : this.width;
    },
    // 实际使用的画布高度
    actualHeight() {
      return this.autoResize && this.canvasInitialized ? this.dynamicHeight : this.height;
    },
    // 容器样式 - 始终保持固定占位大小
    containerStyle() {
      return {
        width: this.width + 'px',
        height: this.height + 'px',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center'
      };
    },
    // 画布样式 - 根据背景图调整
    canvasStyle() {
      console.log('计算canvas样式，尺寸:', this.actualWidth, 'x', this.actualHeight);
      return {
        width: this.actualWidth + 'px',
        height: this.actualHeight + 'px',
        display: 'block',
        maxWidth: this.width + 'px',
        maxHeight: this.height + 'px'
      };
    },
    canUndo() {
      return this.historyIndex > 0;
    },
    canRedo() {
      return this.historyIndex < this.history.length - 1;
    },
    controlPointsStyle() {
      // 控制点容器的样式，让它覆盖画布区域
      const containerWidth = this.width;
      const containerHeight = this.height;
      const canvasWidth = this.actualWidth;
      const canvasHeight = this.actualHeight;

      const offsetX = (containerWidth - canvasWidth) / 2;
      const offsetY = (containerHeight - canvasHeight) / 2;

      return {
        left: offsetX + 'px',
        top: offsetY + 'px',
        width: canvasWidth + 'px',
        height: canvasHeight + 'px'
      };
    },

    deleteBtnStyle() {
      if (!this.selectedElement || this.selectedElement.isBackground) return { display: 'none' };

      // 删除按钮位于选中元素的右上角
      const btnSize = 24;
      const btnX = Math.max(0, Math.min(this.actualWidth - btnSize, this.selectedElement.x + this.selectedElement.width - btnSize / 2));
      const btnY = Math.max(0, Math.min(this.actualHeight - btnSize, this.selectedElement.y - btnSize / 2));

      return {
        left: btnX + 'px',
        top: btnY + 'px',
        display: 'block',
        position: 'absolute',
        zIndex: 100
      };
    },

  },
  watch: {
    backgroundImage(newVal) {
      if (newVal) {
        this.loadBackgroundImage(newVal);
      }
    },
    isLocked(newVal) {
      // 当画布被锁定时，清除选中状态
      if (newVal) {
        this.selectedElement = null;
        this.drawCanvas();
        console.log('画布已锁定，清除选中状态');
      }
    },

    elements: {
      handler(newElements) {
        console.log('接收到新的素材数据:', newElements, '当前元素数量:', this.canvasElements.length);
        
        // 如果传入空数组但当前已有元素，不要清空（防止底图加载时清空已添加的元素）
        if ((!newElements || newElements.length === 0) && this.canvasElements.length > 0) {
          console.log('传入空数组但当前已有元素，保持现有元素不变');
          return;
        }
        
        // 过滤掉无效元素，然后深拷贝，确保保留所有属性
        this.canvasElements = newElements ? newElements.filter(el => el && typeof el === 'object').map(el => {
          console.log('处理素材:', el.id, '类型:', el.type, '尺寸:', el.width, 'x', el.height, '加载状态:', el.loaded);
          
          // 文字元素的处理
          if (el.type === 'text') {
            const textElement = {
              ...el,
              x: el.x !== undefined ? el.x : 0,
              y: el.y !== undefined ? el.y : 0,
              width: el.width !== undefined ? el.width : 100,
              height: el.height !== undefined ? el.height : 40,
              rotation: el.rotation || 0,
              scaleX: el.scaleX || 1,
              scaleY: el.scaleY || 1,
              type: 'text',
              text: el.text || '',
              fontFamily: el.fontFamily || 'sans-serif',
              fontSize: el.fontSize || 32,
              color: el.color || '#91F104',
              id: el.id,
              loaded: true
            };
            console.log('处理文字元素:', textElement);
            return textElement;
          }
          
          // 图片元素的处理
          const element = {
            ...el,
            x: el.x !== undefined ? el.x : 0,
            y: el.y !== undefined ? el.y : 0,
            // 只有当宽高未定义时才使用默认值，避免覆盖正确的比例尺寸
            width: el.width !== undefined ? el.width : 100,
            height: el.height !== undefined ? el.height : 100,
            rotation: el.rotation || 0,
            scaleX: el.scaleX || 1,
            scaleY: el.scaleY || 1,
            type: el.type || 'image',
            src: el.src,
            id: el.id,
            loaded: el.loaded || false,
            imageInfo: el.imageInfo || null
          };

          // 如果素材还没有加载，且有图片URL，则开始加载
          if (!element.loaded && element.src) {
            console.log('检测到未加载的素材，开始加载:', element.src);
            this.preloadImage(element.src).then((imageInfo) => {
              console.log('素材图片加载成功:', imageInfo);
              element.loaded = true;
              element.imageInfo = imageInfo;

              // 根据图片实际尺寸调整素材尺寸，保持原始比例
              const maxSize = Math.min(this.actualWidth, this.actualHeight) * 0.3; // 最大不超过画布的30%
              const imgWidth = imageInfo.width;
              const imgHeight = imageInfo.height;

              // 计算缩放比例，保持原始比例
              const scale = Math.min(maxSize / imgWidth, maxSize / imgHeight);
              element.width = Math.round(imgWidth * scale);
              element.height = Math.round(imgHeight * scale);

              // 重新居中
              element.x = (this.actualWidth - element.width) / 2;
              element.y = (this.actualHeight - element.height) / 2;

              console.log('素材尺寸调整:', {
                素材ID: element.id,
                原始尺寸: { width: imgWidth, height: imgHeight },
                缩放比例: scale,
                最终尺寸: { width: element.width, height: element.height },
                位置: { x: element.x, y: element.y }
              });

              this.drawCanvas();
              // 触发更新事件，保存加载完成的状态
              this.$emit('elements-updated', this.canvasElements);
            }).catch(err => {
              console.error('素材图片加载失败:', err);
              element.loaded = false;
              this.drawCanvas();
            });
          }

          return element;
        }) : [];

        this.selectedElement = null; // 清除选中状态
        console.log('画布内部素材状态:', this.canvasElements);
        this.drawCanvas();
      },
      immediate: true,
      deep: true
    },
    currentFaceId(newFaceId, oldFaceId) {
      if (newFaceId !== oldFaceId) {
        console.log('定制面切换:', oldFaceId, '->', newFaceId);
        // 面切换时清除选中状态
        this.selectedElement = null;
        // 注意：不在这里重绘，因为elements的watch会处理重绘
      }
    },
    width(newVal) {
      console.log('画布宽度变化:', newVal);
      this.$nextTick(() => {
        this.drawCanvas();
      });
    },
    height(newVal) {
      console.log('画布高度变化:', newVal);
      this.$nextTick(() => {
        this.drawCanvas();
      });
    }
  },
  mounted() {
    // 如果没有背景图且启用自动调整，先设置为默认尺寸
    if (!this.backgroundImage && this.autoResize) {
      this.resetToDefaultSize();
    } else {
      this.canvasInitialized = true;
    }

    this.initCanvas();

    if (this.backgroundImage) {
      this.loadBackgroundImage(this.backgroundImage);
    }
  },
  methods: {
    // 初始化画布
    initCanvas() {
      this.$nextTick(() => {
        console.log('初始化画布，尺寸:', this.actualWidth, 'x', this.actualHeight, '当前元素数量:', this.canvasElements.length, 'props elements:', this.elements?.length);
        this.ctx = uni.createCanvasContext('simple-diy-canvas', this);

        // 调试canvas元素尺寸
        this.debugCanvasSize();

        // 如果 props 中有元素但内部状态为空，先同步一次
        if (this.elements && this.elements.length > 0 && this.canvasElements.length === 0) {
          console.log('initCanvas: 检测到 props 有元素但内部为空，手动同步');
          this.syncElementsFromProps();
        }

        // 延迟绘制，确保 elements watch 已经处理完毕
        setTimeout(() => {
          // 再次检查并同步
          if (this.elements && this.elements.length > 0 && this.canvasElements.length === 0) {
            console.log('延迟绘制前: 再次同步 props 元素');
            this.syncElementsFromProps();
          }
          
          console.log('延迟绘制，当前元素数量:', this.canvasElements.length);
          this.drawCanvas();
          
          // 只有当有元素时才保存历史记录，避免保存空状态
          if (this.canvasElements.length > 0 || this.history.length === 0) {
            this.saveToHistory();
          }

          // 发送初始历史状态
          this.$emit('history-changed', {
            canUndo: this.canUndo,
            canRedo: this.canRedo
          });
        }, 100);
      });
    },
    
    // 从 props 同步元素到内部状态
    syncElementsFromProps() {
      if (!this.elements || this.elements.length === 0) return;
      
      console.log('syncElementsFromProps: 同步', this.elements.length, '个元素');
      this.canvasElements = this.elements.filter(el => el && typeof el === 'object').map(el => {
        if (el.type === 'text') {
          return {
            ...el,
            x: el.x !== undefined ? el.x : 0,
            y: el.y !== undefined ? el.y : 0,
            width: el.width !== undefined ? el.width : 100,
            height: el.height !== undefined ? el.height : 40,
            rotation: el.rotation || 0,
            scaleX: el.scaleX || 1,
            scaleY: el.scaleY || 1,
            type: 'text',
            text: el.text || '',
            fontFamily: el.fontFamily || 'sans-serif',
            fontSize: el.fontSize || 32,
            color: el.color || '#91F104',
            id: el.id,
            loaded: true
          };
        }
        return {
          ...el,
          x: el.x !== undefined ? el.x : 0,
          y: el.y !== undefined ? el.y : 0,
          width: el.width !== undefined ? el.width : 100,
          height: el.height !== undefined ? el.height : 100,
          rotation: el.rotation || 0,
          scaleX: el.scaleX || 1,
          scaleY: el.scaleY || 1,
          type: el.type || 'image',
          src: el.src,
          id: el.id,
          loaded: el.loaded || false,
          imageInfo: el.imageInfo || null
        };
      });
      console.log('syncElementsFromProps: 同步完成，当前元素数量:', this.canvasElements.length);
    },

    // 调试canvas尺寸
    debugCanvasSize() {
      try {
        const query = uni.createSelectorQuery().in(this);
        query.select('#simple-diy-canvas').boundingClientRect((rect) => {
          if (rect) {
            console.log('Canvas元素实际尺寸:', {
              width: rect.width,
              height: rect.height,
              期望宽度: this.actualWidth,
              期望高度: this.actualHeight,
              尺寸匹配: rect.width === this.actualWidth && rect.height === this.actualHeight
            });
          }
        }).exec();
      } catch (error) {
        console.log('无法获取canvas尺寸信息:', error);
      }
    },
    
    // 绘制画布
    drawCanvas() {
      if (!this.ctx) return;

      // 清空画布
      this.ctx.clearRect(0, 0, this.actualWidth, this.actualHeight);

      // 绘制背景色
      this.ctx.setFillStyle('#f5f5f5');
      this.ctx.fillRect(0, 0, this.actualWidth, this.actualHeight);

      // 绘制底图（作为第一个元素）
      if (this.backgroundElement) {
        this.drawElement(this.backgroundElement);
      }

      // 绘制所有元素
      console.log('drawCanvas: 开始绘制所有元素，数量:', this.canvasElements.length);
      this.canvasElements.forEach((element, index) => {
        if (element && typeof element === 'object') {
          console.log('drawCanvas: 绘制元素', index, '类型:', element.type, '内容:', element.text || element.src);
          this.drawElement(element);
        } else {
          console.warn('发现无效的画布元素:', element);
        }
      });

      // 绘制选中元素的边框
      if (this.selectedElement) {
        this.drawSelectionBorder(this.selectedElement);
      }

      console.log('drawCanvas: 调用 ctx.draw()');
      this.ctx.draw();
    },

    // 绘制画布（不包含边界线，用于导出）
    drawCanvasForExport() {
      if (!this.ctx) return;

      // 清空画布
      this.ctx.clearRect(0, 0, this.actualWidth, this.actualHeight);

      // 绘制背景色
      this.ctx.setFillStyle('#f5f5f5');
      this.ctx.fillRect(0, 0, this.actualWidth, this.actualHeight);

      // 绘制底图（作为第一个元素）
      if (this.backgroundElement) {
        this.drawElement(this.backgroundElement);
      }

      // 注意：不绘制定制区域边界

      // 绘制所有元素
      this.canvasElements.forEach(element => {
        if (element && typeof element === 'object') {
          this.drawElement(element);
        } else {
          console.warn('发现无效的画布元素:', element);
        }
      });

      // 注意：不绘制选中元素的边框

      this.ctx.draw();
    },

    // 绘制定制区域边界
    // 绘制单个元素
    drawElement(element) {
      if (!this.ctx || !element || typeof element !== 'object') return;

      if ((element.type === 'image' || element.type === 'background') && element.loaded && element.imageInfo) {
        try {
          // 在小程序中，直接使用图片路径
          const imageSrc = element.imageInfo.path || element.src;
          console.log('绘制素材图片:', imageSrc, element);

          // 保存当前画布状态
          this.ctx.save();

          // 计算元素中心点
          const centerX = element.x + element.width / 2;
          const centerY = element.y + element.height / 2;

          // 移动到元素中心点
          this.ctx.translate(centerX, centerY);

          // 应用旋转
          if (element.rotation) {
            this.ctx.rotate(element.rotation);
          }

          // 应用缩放
          if (element.scaleX !== 1 || element.scaleY !== 1) {
            this.ctx.scale(element.scaleX || 1, element.scaleY || 1);
          }

          // 绘制图片（相对于中心点）
          this.ctx.drawImage(
            imageSrc,
            -element.width / 2,
            -element.height / 2,
            element.width,
            element.height
          );

          // 恢复画布状态
          this.ctx.restore();
        } catch (error) {
          console.error('绘制素材图片失败:', error, element);
          // 绘制错误占位符
          this.ctx.setFillStyle('#ffebee');
          this.ctx.fillRect(element.x, element.y, element.width, element.height);
          this.ctx.setStrokeStyle('#f44336');
          this.ctx.setLineWidth(2);
          this.ctx.strokeRect(element.x, element.y, element.width, element.height);
          this.ctx.setFillStyle('#f44336');
          this.ctx.setFontSize(10);
          this.ctx.fillText('图片错误', element.x + 10, element.y + element.height / 2);
        }
      } else if (element.type === 'image' && !element.loaded) {
        // 绘制加载中的占位符
        this.ctx.setFillStyle('#f0f0f0');
        this.ctx.fillRect(element.x, element.y, element.width, element.height);
        this.ctx.setStrokeStyle('#ddd');
        this.ctx.setLineWidth(1);
        this.ctx.strokeRect(element.x, element.y, element.width, element.height);
        this.ctx.setFillStyle('#999999');
        this.ctx.setFontSize(12);
        this.ctx.fillText('加载中...', element.x + 20, element.y + element.height / 2);
      } else if (element.type === 'text') {
        // 绘制文字元素
        try {
          // 保存当前画布状态
          this.ctx.save();

          // 计算元素中心点
          const centerX = element.x + element.width / 2;
          const centerY = element.y + element.height / 2;

          // 移动到元素中心点
          this.ctx.translate(centerX, centerY);

          // 应用旋转
          if (element.rotation) {
            this.ctx.rotate(element.rotation);
          }

          // 应用缩放
          if (element.scaleX !== 1 || element.scaleY !== 1) {
            this.ctx.scale(element.scaleX || 1, element.scaleY || 1);
          }

          // 设置字体样式
          const fontSize = element.fontSize || 32;
          const fontFamily = element.fontFamily || '';
          const textColor = element.color || '#91F104';
          const isSystemFont = element.isSystemFont || !fontFamily || fontFamily === 'sans-serif';
          
          // 小程序Canvas设置字体
          if (isSystemFont) {
            // 系统字体：只设置字体大小，使用默认字体
            this.ctx.setFontSize(fontSize);
          } else {
            // 自定义字体：使用 font 属性设置字体族
            // 格式: "font-style font-weight font-size font-family"
            try {
              this.ctx.font = `normal normal ${fontSize}px "${fontFamily}"`;
            } catch (e) {
              console.warn('设置自定义字体失败，使用默认字体:', e);
              this.ctx.setFontSize(fontSize);
            }
          }
          
          this.ctx.setFillStyle(textColor);
          this.ctx.setTextAlign('center');
          this.ctx.setTextBaseline('middle');

          console.log('绘制文字:', element.text, '字体:', fontFamily, '是否系统字体:', isSystemFont, '大小:', fontSize, '颜色:', textColor);

          // 绘制文字（相对于中心点）
          this.ctx.fillText(element.text || '', 0, 0);
          
          console.log('文字绘制完成');

          // 恢复画布状态
          this.ctx.restore();

          // 更新元素宽度（根据实际文字宽度）
          // 注意：小程序的measureText可能不准确，这里使用估算值
          const textWidth = (element.text || '').length * fontSize;
          if (element.width !== textWidth) {
            element.width = textWidth;
            element.height = fontSize * 1.2;
          }
        } catch (error) {
          console.error('绘制文字元素失败:', error, element);
        }
      }
    },
    
    // 绘制选中边框
    drawSelectionBorder(element) {
      if (!this.ctx) return;
      
      this.ctx.setStrokeStyle('#007AFF');
      this.ctx.setLineWidth(2);
      this.ctx.strokeRect(
        element.x - 2,
        element.y - 2,
        element.width + 4,
        element.height + 4
      );
    },
    
    // 添加素材到画布
    addMaterial(material) {
      // 如果画布被锁定，禁用添加功能
      if (this.isLocked) {
        console.log('画布已锁定，禁用添加素材功能');
        uni.showToast({
          title: '当前步骤无法添加素材',
          icon: 'none'
        });
        return;
      }

      console.log('开始添加素材到画布:', material);

      // 获取图片URL，支持多种字段名
      const imageUrl = material.fileUrl || material.image || material.url || material.src;
      console.log('素材图片URL:', imageUrl);

      if (!imageUrl) {
        console.error('素材缺少图片URL:', material);
        uni.showToast({
          title: '素材图片URL无效',
          icon: 'none'
        });
        return;
      }

      const element = {
        id: Date.now(),
        type: 'image',
        src: imageUrl,
        x: (this.actualWidth - 150) / 2, // 居中
        y: (this.actualHeight - 150) / 2,
        width: 150,
        height: 150,
        rotation: 0, // 旋转角度（弧度）
        scaleX: 1, // X轴缩放
        scaleY: 1, // Y轴缩放
        name: material.name || '未命名素材',
        loaded: false,
        imageInfo: null
      };

      // 先添加到画布，显示加载状态
      this.canvasElements.push(element);
      this.selectedElement = element;
      this.drawCanvas();

      // 预加载图片
      this.preloadImage(element.src).then((imageInfo) => {
        console.log('素材图片加载成功:', imageInfo);
        element.loaded = true;
        element.imageInfo = imageInfo;

        // 根据图片实际尺寸调整素材尺寸
        const maxSize = Math.min(this.actualWidth, this.actualHeight) * 0.5; // 最大不超过画布的50%
        const imgWidth = imageInfo.width;
        const imgHeight = imageInfo.height;

        // 计算缩放比例，保持原始比例
        const scale = Math.min(maxSize / imgWidth, maxSize / imgHeight);
        element.width = Math.round(imgWidth * scale);
        element.height = Math.round(imgHeight * scale);

        // 重新居中
        element.x = (this.actualWidth - element.width) / 2;
        element.y = (this.actualHeight - element.height) / 2;

        console.log('素材尺寸调整:', {
          原始尺寸: { width: imgWidth, height: imgHeight },
          缩放比例: scale,
          最终尺寸: { width: element.width, height: element.height }
        });

        // 重新绘制画布
        this.drawCanvas();
        // 图片加载完成后再保存历史记录
        this.saveToHistory();
        // 触发素材更新事件，确保父组件保存最新的加载状态
        this.$emit('elements-updated', this.canvasElements);
      }).catch(err => {
        console.error('素材图片加载失败:', err, element.src);
        element.loaded = false;
        // 即使加载失败也要重新绘制，显示错误状态
        this.drawCanvas();
        this.saveToHistory();
        // 即使加载失败也要触发更新事件
        this.$emit('elements-updated', this.canvasElements);
      });

      console.log('素材已添加到画布，当前元素数量:', this.canvasElements.length);
      this.$emit('element-added', element);
      this.$emit('elements-updated', this.canvasElements);
    },

    /**
     * 添加预处理后的素材到画布
     * 支持根据定制区域自动裁剪和缩放图片
     */
    async addPreprocessedMaterial(material) {
      // 如果画布被锁定，禁用添加功能
      if (this.isLocked) {
        console.log('画布已锁定，禁用添加素材功能');
        uni.showToast({
          title: '当前步骤无法添加素材',
          icon: 'none'
        });
        return;
      }

      console.log('开始添加预处理素材到画布:', material);

      try {
        // 获取图片URL
        const imageUrl = material.fileUrl || material.image || material.url || material.src;
        if (!imageUrl) {
          throw new Error('素材缺少图片URL');
        }

        // 创建新的画布元素
        const element = {
          id: Date.now(),
          type: 'image',
          src: imageUrl,
          x: (this.actualWidth - 150) / 2, // 居中
          y: (this.actualHeight - 150) / 2,
          width: 150,
          height: 150,
          rotation: 0,
          scaleX: 1,
          scaleY: 1,
          name: material.name || '未命名素材',
          loaded: false,
          imageInfo: null
        };

        // 先添加到画布，显示加载状态
        this.canvasElements.push(element);
        this.selectedElement = element;
        this.drawCanvas();

        // 4. 预加载图片
        const imageInfo = await this.preloadImageAsync(element.src);
        console.log('预处理素材图片加载成功:', imageInfo);

        element.loaded = true;
        element.imageInfo = imageInfo;

        // 5. 根据图片实际尺寸调整素材尺寸
        const maxSize = Math.min(this.actualWidth, this.actualHeight) * 0.5;
        const imgWidth = imageInfo.width;
        const imgHeight = imageInfo.height;

        // 计算缩放比例，保持原始比例
        const scale = Math.min(maxSize / imgWidth, maxSize / imgHeight);
        element.width = Math.round(imgWidth * scale);
        element.height = Math.round(imgHeight * scale);

        // 重新居中
        element.x = (this.actualWidth - element.width) / 2;
        element.y = (this.actualHeight - element.height) / 2;

        console.log('素材尺寸调整:', {
          原始尺寸: { width: imgWidth, height: imgHeight },
          缩放比例: scale,
          最终尺寸: { width: element.width, height: element.height }
        });

        // 6. 重新绘制画布
        this.drawCanvas();
        this.saveToHistory();
        this.$emit('elements-updated', this.canvasElements);
        this.$emit('element-added', element);

      } catch (error) {
        console.error('添加预处理素材失败:', error);
        uni.showToast({
          title: '添加素材失败',
          icon: 'none'
        });
      }
    },

    /**
     * 异步预加载图片
     */
    preloadImageAsync(src) {
      return new Promise((resolve, reject) => {
        uni.getImageInfo({
          src: src,
          success: resolve,
          fail: reject
        });
      });
    },

    /**
     * 编辑文字元素
     * @param {Object} element - 文字元素
     */
    editTextElement(element) {
      if (!element || element.type !== 'text') return;

      console.log('编辑文字元素:', element);

      // 触发事件，让父组件打开文字编辑弹窗
      this.$emit('edit-text', {
        element: element,
        onUpdate: (updates) => {
          // 更新文字属性
          if (updates.text !== undefined) {
            element.text = updates.text;
            element.width = updates.text.length * element.fontSize;
          }
          if (updates.color !== undefined) {
            element.color = updates.color;
          }
          if (updates.fontFamily !== undefined) {
            element.fontFamily = updates.fontFamily;
          }
          if (updates.fontSize !== undefined) {
            element.fontSize = updates.fontSize;
            element.width = element.text.length * updates.fontSize;
            element.height = updates.fontSize * 1.2;
          }
          // 重新绘制画布
          this.drawCanvas();
          this.saveToHistory();
          this.$emit('elements-updated', this.canvasElements);
          console.log('文字已更新:', element);
        }
      });
    },

    /**
     * 添加文字元素到画布
     * @param {Object} options - 文字配置
     * @param {string} options.text - 文字内容
     * @param {string} options.fontFamily - 字体名称
     * @param {number} options.fontSize - 字体大小
     * @param {string} options.color - 文字颜色
     * @param {string} options.fontFileUrl - 字体文件URL
     */
    addTextElement(options) {
      // 如果画布被锁定，禁用添加功能
      if (this.isLocked) {
        console.log('画布已锁定，禁用添加文字功能');
        uni.showToast({
          title: '当前步骤无法添加文字',
          icon: 'none'
        });
        return;
      }

      console.log('添加文字元素到画布:', options);

      const {
        text = '广横走文创',
        fontFamily = '',
        fontSize = 32,
        color = '#91F104',
        fontFileUrl = '',
        materialId = null,
        materialName = '',
        isSystemFont = false
      } = options;

      // 判断是否为系统字体
      const useSystemFont = isSystemFont || !fontFamily || fontFamily === 'sans-serif';

      // 计算文字宽度（估算）
      const textWidth = text.length * fontSize;
      const textHeight = fontSize * 1.2;

      const element = {
        id: Date.now(),
        type: 'text',
        text: text,
        fontFamily: useSystemFont ? '' : fontFamily,
        fontSize: fontSize,
        color: color,
        fontFileUrl: fontFileUrl,
        materialId: materialId,
        materialName: materialName,
        isSystemFont: useSystemFont,
        x: (this.actualWidth - textWidth) / 2, // 居中
        y: (this.actualHeight - textHeight) / 2,
        width: textWidth,
        height: textHeight,
        rotation: 0,
        scaleX: 1,
        scaleY: 1,
        loaded: true
      };

      // 添加到画布
      this.canvasElements.push(element);
      this.selectedElement = element;
      
      // 重新绘制画布
      this.drawCanvas();
      this.saveToHistory();
      
      console.log('文字元素已添加到画布，当前元素数量:', this.canvasElements.length);
      this.$emit('element-added', element);
      this.$emit('elements-updated', this.canvasElements);
    },
    
    // 预加载图片
    preloadImage(src) {
      return new Promise((resolve, reject) => {
        uni.getImageInfo({
          src: src,
          success: resolve,
          fail: reject
        });
      });
    },

    // 加载底图
    loadBackgroundImage(src) {
      if (!src) {
        console.log('底图URL为空，清除底图');
        this.backgroundLoaded = false;
        this.backgroundImageObj = null;
        // 如果没有背景图，恢复默认尺寸
        if (this.autoResize) {
          this.resetToDefaultSize();
        }
        this.drawCanvas();
        return;
      }

      console.log('开始加载底图:', src);
      this.backgroundLoaded = false; // 重置加载状态

      this.preloadImage(src).then((imageInfo) => {
        this.backgroundImageObj = imageInfo;
        this.backgroundLoaded = true;
        console.log('底图加载成功:', imageInfo);

        // 创建底图元素对象，使其可交互
        this.createBackgroundElement(imageInfo, src);

        // 如果画布还未初始化，根据背景图调整画布尺寸
        if (this.autoResize && !this.canvasInitialized) {
          this.adjustCanvasSizeToBackground(imageInfo);
        }

        this.drawCanvas();
      }).catch(err => {
        console.error('底图加载失败:', err, src);
        this.backgroundLoaded = false;
        this.backgroundImageObj = null;
        // 加载失败时恢复默认尺寸
        if (this.autoResize) {
          this.resetToDefaultSize();
        }
        this.drawCanvas();
      });
    },

    // 创建底图元素对象，使其可交互
    createBackgroundElement(imageInfo, src) {
      if (!imageInfo) return;

      // 计算底图的初始位置和尺寸
      const imgWidth = imageInfo.width;
      const imgHeight = imageInfo.height;

      // 底图初始缩放以适应画布，确保至少高度完全显示，用户可以进一步调整
      const canvasWidth = this.actualWidth;
      const canvasHeight = this.actualHeight;

      // 计算缩放比例，确保图片至少高度完全显示在画布内
      const scaleX = canvasWidth / imgWidth;
      const scaleY = canvasHeight / imgHeight;
      const scale = Math.min(scaleX, scaleY); // 保持比例，完全显示在画布内

      const displayWidth = Math.round(imgWidth * scale);
      const displayHeight = Math.round(imgHeight * scale);

      // 从画布左上角开始显示
      const x = 0;
      const y = 0;

      this.backgroundElement = {
        id: 'background',
        type: 'background',
        src: src,
        x: x,
        y: y,
        width: displayWidth,
        height: displayHeight,
        rotation: 0,
        scaleX: 1,
        scaleY: 1,
        name: '底图',
        loaded: true,
        imageInfo: imageInfo,
        isBackground: true // 标记为底图
      };

      console.log('底图元素已创建:', {
        原始尺寸: { width: imgWidth, height: imgHeight },
        画布尺寸: { width: canvasWidth, height: canvasHeight },
        缩放比例: scale,
        显示尺寸: { width: displayWidth, height: displayHeight },
        位置: { x: x, y: y }
      });
    },

    // 根据背景图调整画布尺寸
    adjustCanvasSizeToBackground(imageInfo) {
      if (!imageInfo) return;

      const imgWidth = imageInfo.width;
      const imgHeight = imageInfo.height;

      console.log('背景图原始尺寸:', imgWidth, 'x', imgHeight);

      // 计算适合容器的画布尺寸，保持背景图的宽高比
      const containerWidth = this.width;
      const containerHeight = this.height;

      // 计算缩放比例，确保画布适合容器，但优先保证宽度适配
      const scaleX = containerWidth / imgWidth;
      const scaleY = containerHeight / imgHeight;

      // 优先按宽度缩放，高度可以超出容器（用户可以滚动或拖拽查看）
      const scale = scaleX;

      let newWidth = Math.round(imgWidth * scale);
      let newHeight = Math.round(imgHeight * scale);

      // 确保最小尺寸
      const minSize = 100;
      if (newWidth < minSize || newHeight < minSize) {
        const minScale = Math.max(minSize / newWidth, minSize / newHeight);
        newWidth = Math.round(newWidth * minScale);
        newHeight = Math.round(newHeight * minScale);
      }

      // 限制最大尺寸
      if (newWidth > this.maxWidth) {
        const maxScale = this.maxWidth / newWidth;
        newWidth = this.maxWidth;
        newHeight = Math.round(newHeight * maxScale);
      }

      this.dynamicWidth = newWidth;
      this.dynamicHeight = newHeight;
      this.canvasInitialized = true;

      console.log('画布尺寸已调整为:', newWidth, 'x', newHeight, '容器尺寸:', containerWidth, 'x', containerHeight);

      // 通知父组件画布尺寸已改变
      this.$emit('canvas-resized', {
        width: newWidth,
        height: newHeight,
        containerWidth: containerWidth,
        containerHeight: containerHeight,
        originalImageSize: { width: imgWidth, height: imgHeight }
      });

      // 重新初始化画布
      this.$nextTick(() => {
        this.initCanvas();
      });
    },

    // 重置为默认尺寸
    resetToDefaultSize() {
      this.dynamicWidth = this.width;
      this.dynamicHeight = this.height;
      this.canvasInitialized = true;

      console.log('画布尺寸已重置为默认:', this.width, 'x', this.height);

      // 通知父组件画布尺寸已改变
      this.$emit('canvas-resized', {
        width: this.width,
        height: this.height,
        originalImageSize: null
      });

      // 重新初始化画布
      this.$nextTick(() => {
        this.initCanvas();
      });
    },

    // 处理触摸开始
    handleTouchStart(e) {
      // 阻止默认行为，避免页面滚动等干扰
      e.preventDefault && e.preventDefault();

      // 如果画布被锁定，禁用所有触摸交互
      if (this.isLocked) {
        console.log('画布已锁定，禁用触摸交互');
        return;
      }

      this.touches = Array.from(e.touches);

      if (this.touches.length === 1) {
        // 单指操作：选择和拖拽
        const touch = this.touches[0];

        // 直接使用触摸坐标，因为触摸事件已经是相对于画布的坐标
        this.touchStartX = touch.x;
        this.touchStartY = touch.y;

        console.log('触摸开始:', touch.x, touch.y);

        // 检查触摸点是否在画布范围内
        if (touch.x < 0 || touch.x > this.actualWidth || touch.y < 0 || touch.y > this.actualHeight) {
          console.log('触摸点在画布外');
          this.selectedElement = null;
          return;
        }

        const element = this.getElementAtPoint(touch.x, touch.y);
        if (element) {
          // 检测双击（用于编辑文字）
          const now = Date.now();
          if (this.selectedElement === element && 
              element.type === 'text' && 
              this.lastTapTime && 
              now - this.lastTapTime < 300) {
            // 双击文字元素，弹出编辑框
            this.editTextElement(element);
            this.lastTapTime = 0;
            return;
          }
          this.lastTapTime = now;
          
          this.selectedElement = element;
          this.isDragging = true;
          console.log('选中元素:', element);
        } else {
          this.selectedElement = null;
          this.lastTapTime = 0;
          console.log('未选中任何元素');
        }
      } else if (this.touches.length === 2 && this.selectedElement) {
        // 双指操作：缩放和旋转
        console.log('检测到双指操作，选中元素:', this.selectedElement.name);
        this.isGesturing = true;
        this.isDragging = false;

        const touch1 = this.touches[0];
        const touch2 = this.touches[1];

        // 计算初始距离和角度
        this.gestureStartDistance = this.getDistance(touch1, touch2);
        this.gestureStartAngle = this.getAngle(touch1, touch2);
        this.gestureStartScale = this.selectedElement.scaleX || 1;
        this.gestureStartRotation = this.selectedElement.rotation || 0;

        console.log('开始双指手势操作:', {
          距离: this.gestureStartDistance,
          角度: this.gestureStartAngle * 180 / Math.PI,
          初始缩放: this.gestureStartScale,
          初始旋转: this.gestureStartRotation * 180 / Math.PI
        });
      }

      this.drawCanvas();
    },
    
    // 处理触摸移动
    handleTouchMove(e) {
      // 阻止默认行为，避免页面滚动等干扰
      e.preventDefault && e.preventDefault();

      // 如果画布被锁定，禁用所有触摸交互
      if (this.isLocked) {
        return;
      }

      this.touches = Array.from(e.touches);

      if (this.isGesturing && this.touches.length === 2 && this.selectedElement) {
        // 双指手势操作
        console.log('执行双指手势操作');
        const touch1 = this.touches[0];
        const touch2 = this.touches[1];

        // 计算当前距离和角度
        const currentDistance = this.getDistance(touch1, touch2);
        const currentAngle = this.getAngle(touch1, touch2);

        // 计算缩放比例
        const scaleRatio = currentDistance / this.gestureStartDistance;
        const newScale = Math.max(0.2, Math.min(3, this.gestureStartScale * scaleRatio));

        // 计算旋转角度
        const angleDiff = currentAngle - this.gestureStartAngle;
        const newRotation = this.gestureStartRotation + angleDiff;

        // 应用变换
        this.selectedElement.scaleX = newScale;
        this.selectedElement.scaleY = newScale;
        this.selectedElement.rotation = newRotation;

        console.log('手势操作中:', {
          缩放: newScale.toFixed(2),
          旋转: (newRotation * 180 / Math.PI).toFixed(1) + '度'
        });

        this.drawCanvas();
      } else if (this.isDragging && this.touches.length === 1 && this.selectedElement) {
        // 单指拖拽操作
        const touch = this.touches[0];

        const deltaX = touch.x - this.touchStartX;
        const deltaY = touch.y - this.touchStartY;

        this.selectedElement.x += deltaX;
        this.selectedElement.y += deltaY;

        // 边界检查（底图允许移动到画布外部，其他元素限制在画布内）
        if (this.selectedElement.isBackground) {
          // 底图允许移动到画布外部，但至少要有一部分在画布内
          const minVisibleSize = 50; // 至少50px在画布内可见
          this.selectedElement.x = Math.max(-this.selectedElement.width + minVisibleSize,
                                           Math.min(this.actualWidth - minVisibleSize, this.selectedElement.x));
          this.selectedElement.y = Math.max(-this.selectedElement.height + minVisibleSize,
                                           Math.min(this.actualHeight - minVisibleSize, this.selectedElement.y));
        } else {
          // 普通元素限制在画布内
          this.selectedElement.x = Math.max(0, Math.min(this.actualWidth - this.selectedElement.width, this.selectedElement.x));
          this.selectedElement.y = Math.max(0, Math.min(this.actualHeight - this.selectedElement.height, this.selectedElement.y));
        }

        this.touchStartX = touch.x;
        this.touchStartY = touch.y;

        this.drawCanvas();
      }
    },
    
    // 处理触摸结束
    handleTouchEnd(e) {
      // 阻止默认行为
      e.preventDefault && e.preventDefault();

      // 如果画布被锁定，禁用所有触摸交互
      if (this.isLocked) {
        return;
      }

      if (this.isDragging || this.isGesturing) {
        this.saveToHistory();
        // 触发素材更新事件，确保父组件能够保存最新的素材状态
        this.$emit('elements-updated', this.canvasElements);
      }

      this.isDragging = false;
      this.isGesturing = false;
      this.touches = [];
    },
    
    // 获取指定点的元素
    getElementAtPoint(x, y) {
      // 从后往前遍历（后添加的在上层）
      for (let i = this.canvasElements.length - 1; i >= 0; i--) {
        const element = this.canvasElements[i];
        if (element && typeof element === 'object' &&
            x >= element.x && x <= element.x + element.width &&
            y >= element.y && y <= element.y + element.height) {
          return element;
        }
      }

      // 检查底图元素（在最底层）
      if (this.backgroundElement &&
          x >= this.backgroundElement.x && x <= this.backgroundElement.x + this.backgroundElement.width &&
          y >= this.backgroundElement.y && y <= this.backgroundElement.y + this.backgroundElement.height) {
        return this.backgroundElement;
      }

      return null;
    },

    // 计算两点间距离
    getDistance(touch1, touch2) {
      const dx = touch2.x - touch1.x;
      const dy = touch2.y - touch1.y;
      return Math.sqrt(dx * dx + dy * dy);
    },

    // 计算两点间角度
    getAngle(touch1, touch2) {
      const dx = touch2.x - touch1.x;
      const dy = touch2.y - touch1.y;
      return Math.atan2(dy, dx);
    },
    
    // 删除选中元素
    deleteSelectedElement() {
      // 如果画布被锁定，禁用删除功能
      if (this.isLocked) {
        console.log('画布已锁定，禁用删除功能');
        return;
      }

      if (!this.selectedElement) return;

      // 底图不能被删除
      if (this.selectedElement.isBackground) {
        console.log('底图不能被删除');
        return;
      }

      const index = this.canvasElements.indexOf(this.selectedElement);
      if (index > -1) {
        this.canvasElements.splice(index, 1);
        this.selectedElement = null;
        this.drawCanvas();
        this.saveToHistory();

        this.$emit('element-deleted');
        this.$emit('elements-updated', this.canvasElements);
      }
    },


    
    // 保存到历史记录
    saveToHistory() {
      // 移除当前索引之后的历史记录
      this.history = this.history.slice(0, this.historyIndex + 1);
      
      // 添加新的历史记录
      this.history.push(JSON.stringify({
        elements: this.canvasElements,
        selectedElement: this.selectedElement ? this.selectedElement.id : null
      }));
      
      this.historyIndex++;
      
      // 限制历史记录数量
      if (this.history.length > 20) {
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
      this.canvasElements = state.elements;

      // 恢复选中元素
      if (state.selectedElement) {
        this.selectedElement = this.canvasElements.find(el => el && el.id === state.selectedElement);
      } else {
        this.selectedElement = null;
      }
      
      this.drawCanvas();

      this.$emit('history-changed', {
        canUndo: this.canUndo,
        canRedo: this.canRedo
      });

      this.$emit('elements-updated', this.canvasElements);
    },

    // 清空画布
    clearCanvas() {
      this.canvasElements = [];
      this.selectedElement = null;
      this.drawCanvas();
      this.saveToHistory();
      this.$emit('elements-updated', this.canvasElements);
    },

    // 清理所有元素（保留背景图片）
    clearAllElements() {
      this.canvasElements = [];
      this.selectedElement = null;
      this.drawCanvas();
      // 注意：这里不保存历史记录，因为这是步骤切换时的自动清理
      this.$emit('elements-updated', this.canvasElements);
      console.log('画布元素已清理，保留背景图片');
    },

    // 恢复元素到画布（用于撤销AI生成）
    restoreElements(elements) {
      this.canvasElements = JSON.parse(JSON.stringify(elements));
      this.selectedElement = null;
      this.drawCanvas();
      this.saveToHistory();
      this.$emit('elements-updated', this.canvasElements);
      console.log('画布元素已恢复:', this.canvasElements.length, '个元素');
    },

    // 更新背景图片
    updateBackgroundImage(imageUrl) {
      if (imageUrl) {
        this.loadBackgroundImage(imageUrl);
        console.log('画布背景图片已更新:', imageUrl);
      }
    },

    // 恢复底图元素状态
    restoreBackgroundElement(backgroundElement) {
      if (backgroundElement) {
        this.backgroundElement = JSON.parse(JSON.stringify(backgroundElement));
        console.log('底图元素状态已恢复:', this.backgroundElement);
        this.drawCanvas();
      }
    },



    // 导出画布为图片
    exportCanvasImage() {
      return new Promise((resolve, reject) => {
        if (!this.ctx) {
          reject(new Error('画布未初始化'));
          return;
        }

        // 先绘制一次画布，确保最新状态（不包含边界线）
        this.drawCanvasForExport();

        // 等待绘制完成
        setTimeout(() => {
          uni.canvasToTempFilePath({
            canvasId: 'simple-diy-canvas',
            success: (res) => {
              console.log('画布导出成功:', res.tempFilePath);
              resolve(res.tempFilePath);
            },
            fail: (error) => {
              console.error('画布导出失败:', error);
              reject(error);
            }
          }, this);
        }, 100);
      });
    },

    // 导出高分辨率画布图片（用于AI风格化）
    exportHighResCanvasImage() {
      return new Promise((resolve, reject) => {
        if (!this.ctx) {
          reject(new Error('画布未初始化'));
          return;
        }

        // 计算放大倍数，确保最小尺寸达到512像素
        const minSize = 512;
        const currentWidth = this.actualWidth;
        const currentHeight = this.actualHeight;
        const scaleX = Math.max(1, minSize / currentWidth);
        const scaleY = Math.max(1, minSize / currentHeight);
        const scale = Math.max(scaleX, scaleY); // 取较大的缩放比，确保两个维度都≥512

        const exportWidth = Math.round(currentWidth * scale);
        const exportHeight = Math.round(currentHeight * scale);

        console.log(`导出高分辨率图片: 原尺寸${currentWidth}x${currentHeight} -> 导出尺寸${exportWidth}x${exportHeight}, 缩放比例${scale}`);

        // 先绘制一次画布，确保最新状态（不包含边界线）
        this.drawCanvasForExport();

        // 等待绘制完成
        setTimeout(() => {
          uni.canvasToTempFilePath({
            canvasId: 'simple-diy-canvas',
            x: 0,
            y: 0,
            width: currentWidth,      // 使用画布实际宽度（导出区域）
            height: currentHeight,    // 使用画布实际高度（导出区域）
            destWidth: exportWidth,   // 放大到目标宽度（生成图片尺寸）
            destHeight: exportHeight, // 放大到目标高度（生成图片尺寸）
            fileType: 'png',
            quality: 1,
            success: (res) => {
              console.log('高分辨率画布导出成功:', res.tempFilePath, `画布尺寸: ${currentWidth}x${currentHeight}, 导出尺寸: ${exportWidth}x${exportHeight}`);
              resolve(res.tempFilePath);
            },
            fail: (error) => {
              console.error('高分辨率画布导出失败:', error);
              reject(error);
            }
          }, this);
        }, 100);
      });
    },

    /**
     * 导出画布图片并根据路径剪裁
     * @param {String} pathData - SVG路径数据，用于剪裁
     * @param {String} bounds - 边界框数据 "x,y,width,height"
     * @returns {Promise<String>} - 剪裁后的图片临时路径
     */
    exportClippedCanvasImage(pathData, bounds) {
      return new Promise(async (resolve, reject) => {
        if (!this.ctx) {
          reject(new Error('画布未初始化'));
          return;
        }

        if (!pathData || !bounds) {
          // 如果没有路径数据或边界框，直接导出完整画布
          this.exportCanvasImage().then(resolve).catch(reject);
          return;
        }

        console.log('开始导出并剪裁画布图片');
        console.log('路径数据:', pathData);
        console.log('边界框:', bounds);

        try {
          // 1. 先导出完整画布
          const fullCanvasPath = await this.exportCanvasImage();
          console.log('完整画布导出成功:', fullCanvasPath);

          // 2. 解析边界框
          const [x, y, width, height] = bounds.split(',').map(Number);
          console.log('解析边界框:', { x, y, width, height });

          // 3. 判断形状类型（圆形或矩形）
          const isCircle = pathData.includes('A '); // 圆形路径包含 A 命令
          console.log('形状类型:', isCircle ? '圆形' : '矩形');

          // 4. 创建临时Canvas进行剪裁
          const clippedImagePath = await this.clipImageWithShape(
            fullCanvasPath,
            { x, y, width, height },
            isCircle
          );

          console.log('图片剪裁成功:', clippedImagePath);
          resolve(clippedImagePath);

        } catch (error) {
          console.error('导出并剪裁画布失败:', error);
          reject(error);
        }
      });
    },

    /**
     * 使用指定形状剪裁图片
     * 注意：小程序Canvas剪裁需要在页面中有对应的canvas元素
     * 由于动态创建canvas的限制，这里采用简化方案：
     * 1. 使用主画布进行剪裁绘制
     * 2. 导出剪裁后的图片
     * 3. 恢复主画布原始内容
     *
     * @param {String} imagePath - 原始图片路径
     * @param {Object} bounds - 边界框 {x, y, width, height}
     * @param {Boolean} isCircle - 是否为圆形
     * @returns {Promise<String>} - 剪裁后的图片路径
     */
    async clipImageWithShape(imagePath, bounds, isCircle) {
      const { x, y, width, height } = bounds;

      console.log('开始剪裁图片:', { imagePath, bounds, isCircle });

      // 保存当前画布状态
      const currentElements = JSON.parse(JSON.stringify(this.canvasElements));
      const currentBackground = this.backgroundImage;

      try {
        // 1. 清空画布
        this.ctx.clearRect(0, 0, this.actualWidth, this.actualHeight);

        // 2. 设置剪裁区域
        this.ctx.save();
        this.ctx.beginPath();

        if (isCircle) {
          // 圆形剪裁
          const centerX = x + width / 2;
          const centerY = y + height / 2;
          const radius = Math.min(width, height) / 2;
          console.log('绘制圆形剪裁路径:', { centerX, centerY, radius });
          this.ctx.arc(centerX, centerY, radius, 0, 2 * Math.PI);
        } else {
          // 矩形剪裁
          console.log('绘制矩形剪裁路径:', { x, y, width, height });
          this.ctx.rect(x, y, width, height);
        }

        this.ctx.clip();

        // 3. 绘制完整画布图片
        await new Promise((resolve, reject) => {
          this.ctx.drawImage(imagePath, 0, 0, this.actualWidth, this.actualHeight);
          this.ctx.draw(false, resolve);
        });

        // 4. 等待绘制完成
        await new Promise(resolve => setTimeout(resolve, 100));

        // 5. 导出剪裁区域
        const clippedPath = await new Promise((resolve, reject) => {
          uni.canvasToTempFilePath({
            canvasId: 'simple-diy-canvas',
            x: x,
            y: y,
            width: width,
            height: height,
            destWidth: width,
            destHeight: height,
            success: (res) => {
              console.log('剪裁区域导出成功:', res.tempFilePath);
              resolve(res.tempFilePath);
            },
            fail: (error) => {
              console.error('剪裁区域导出失败:', error);
              reject(error);
            }
          }, this);
        });

        // 6. 恢复画布原始内容
        this.ctx.restore();
        this.canvasElements = currentElements;
        this.backgroundImage = currentBackground;
        this.drawCanvas();

        return clippedPath;

      } catch (error) {
        // 出错时也要恢复画布
        this.ctx.restore();
        this.canvasElements = currentElements;
        this.backgroundImage = currentBackground;
        this.drawCanvas();
        throw error;
      }
    },

    // 获取画布数据（包含元素和背景）
    getCanvasData() {
      return {
        elements: this.canvasElements,
        backgroundImage: this.backgroundImage,
        backgroundElement: this.backgroundElement,
        width: this.actualWidth,
        height: this.actualHeight
      };
    },

    // 导出Canvas数据（用于保存状态）
    exportCanvasData() {
      return {
        elements: JSON.parse(JSON.stringify(this.canvasElements)), // 深拷贝
        backgroundImage: this.backgroundImage,
        width: this.actualWidth,
        height: this.actualHeight,
        selectedElement: this.selectedElement ? JSON.parse(JSON.stringify(this.selectedElement)) : null
      };
    },

    // 导入Canvas数据（用于恢复状态）
    importCanvasData(data) {
      if (!data) return;

      try {
        // 恢复元素数据
        if (data.elements && Array.isArray(data.elements)) {
          this.canvasElements = JSON.parse(JSON.stringify(data.elements)); // 深拷贝
        }

        // 恢复选中元素
        if (data.selectedElement) {
          this.selectedElement = JSON.parse(JSON.stringify(data.selectedElement));
        }

        // 重新绘制Canvas
        this.$nextTick(() => {
          this.drawCanvas();
        });

        // Canvas数据导入成功
      } catch (error) {
        // Canvas数据导入失败
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.simple-canvas-editor {
  position: relative;
  background: #f5f5f5;
  border-radius: 8rpx;
  overflow: hidden;
  flex-shrink: 0; /* 防止被flex容器压缩 */
  z-index: 1; /* 确保画布组件层级低于弹窗 */
  /* 使用flex布局让画布居中 */
  display: flex;
  align-items: center;
  justify-content: center;
}

.canvas {
  display: block;
  background: #ffffff;
  /* iOS微信端兼容性修复 */
  position: relative;
  z-index: 1;
  -webkit-transform: translateZ(0);
  transform: translateZ(0);
  /* 小程序Canvas尺寸强制设置 */
  width: 303px !important;
  height: 348px !important;
}

.control-points {
  position: absolute;
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


</style>
