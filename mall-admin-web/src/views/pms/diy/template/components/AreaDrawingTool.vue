<template>
  <div class="area-drawing-tool">
    <!-- 简化工具栏 - 只保留编辑区域按钮 -->
    <div class="toolbar">
      <div class="area-summary">
        <span>DIY区域：</span>
        <el-tag type="info" size="small">{{ diyAreas.length }} 个区域</el-tag>
        <el-button
          type="primary"
          size="small"
          icon="el-icon-edit"
          @click="openAreaEditor"
          style="margin-left: 10px;">
          编辑区域
        </el-button>
      </div>
    </div>

    <!-- 区域编辑弹窗 -->
    <el-dialog
      title="DIY区域编辑"
      :visible.sync="areaEditorVisible"
      width="90%"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      class="area-editor-dialog">

      <div class="area-editor-content">
        <!-- 左侧：工具栏和区域列表 -->
        <div class="editor-sidebar">
          <!-- 绘制工具栏 -->
          <el-card shadow="never" class="tools-card">
            <div slot="header">
              <span>绘制工具</span>
            </div>
            <div class="drawing-tools">
              <el-button-group>
                <el-button
                  :type="drawingMode === 'select' ? 'primary' : 'default'"
                  @click="setDrawingMode('select')"
                  icon="el-icon-mouse"
                  size="small">
                  选择
                </el-button>
                <el-button
                  :type="drawingMode === 'rect' ? 'primary' : 'default'"
                  @click="setDrawingMode('rect')"
                  icon="el-icon-crop"
                  size="small"
                  :disabled="hasMaxAreas">
                  矩形
                </el-button>
                <el-button
                  :type="drawingMode === 'circle' ? 'primary' : 'default'"
                  @click="setDrawingMode('circle')"
                  icon="el-icon-circle-check"
                  size="small"
                  :disabled="hasMaxAreas">
                  圆形
                </el-button>
                <el-button
                  :type="drawingMode === 'brush' ? 'primary' : 'default'"
                  @click="setDrawingMode('brush')"
                  icon="el-icon-edit"
                  size="small"
                  :disabled="hasMaxAreas">
                  画笔
                </el-button>
              </el-button-group>

              <!-- 画笔工具设置 -->
              <div v-if="drawingMode === 'brush'" style="margin-top: 10px; padding: 10px; background: #f5f7fa; border-radius: 4px;">
                <div style="margin-bottom: 8px;">
                  <span style="font-size: 12px; color: #606266;">画笔粗细：</span>
                  <el-slider
                    v-model="brushWidth"
                    :min="5"
                    :max="50"
                    :step="5"
                    @change="updateBrushWidth"
                    style="margin-top: 5px;">
                  </el-slider>
                </div>
                <div style="font-size: 12px; color: #909399;">
                  <i class="el-icon-info"></i> 用画笔涂抹需要DIY的区域
                </div>
              </div>

              <div style="margin-top: 10px;">
                <el-button
                  @click="saveCurrentSurface"
                  icon="el-icon-check"
                  type="success"
                  size="small"
                  :loading="saveLoading">
                  保存区域
                </el-button>
                <el-button
                  @click="deleteSelected"
                  icon="el-icon-delete"
                  type="danger"
                  size="small"
                  :disabled="!selectedObject">
                  删除选中
                </el-button>
              </div>
            </div>
          </el-card>

          <!-- 区域列表 -->
          <el-card shadow="never" class="areas-card" style="margin-top: 15px;">
            <div slot="header">
              <span>区域列表 ({{ diyAreas.length }})</span>
            </div>
            <div class="areas-list">
              <div
                v-for="(area, index) in diyAreas"
                :key="area.id || index"
                class="area-item"
                :class="{ 'active': selectedAreaIndex === index }"
                @click="selectAreaByIndex(index)">
                <div class="area-info">
                  <div class="area-name">
                    {{ area.name || `区域${index + 1}` }}
                    <el-tag v-if="area.maskImageUrl" type="success" size="mini" style="margin-left: 5px;">画笔</el-tag>
                  </div>
                  <div class="area-details">
                    <span v-if="area.maskImageUrl">蒙版模式</span>
                    <template v-else>
                      <span>位置: ({{ area.x || 0 }}, {{ area.y || 0 }})</span>
                      <span v-if="area.pathData && area.pathData.includes('A ')">半径: {{ Math.round(Math.min(area.width || 50, area.height || 50) / 2) }}</span>
                      <span v-else>尺寸: {{ area.width || 0 }} × {{ area.height || 0 }}</span>
                    </template>
                  </div>
                </div>
                <div class="area-actions">
                  <el-button
                    v-if="!area.maskImageUrl"
                    type="text"
                    size="mini"
                    icon="el-icon-edit"
                    @click.stop="editArea(index)">
                  </el-button>
                  <el-button
                    type="text"
                    size="mini"
                    icon="el-icon-delete"
                    @click.stop="deleteAreaByIndex(index)">
                  </el-button>
                </div>
              </div>
              <div v-if="diyAreas.length === 0" class="empty-areas">
                <i class="el-icon-info"></i>
                <p>暂无DIY区域，请在右侧画布上绘制</p>
              </div>
            </div>
          </el-card>
        </div>

        <!-- 右侧：大尺寸画布预览 -->
        <div class="editor-canvas">
          <div class="canvas-header">
            <h4>{{ surface.name || '模板面' }} - 预览编辑</h4>
            <div class="canvas-info">
              <span>画布尺寸: {{ largeImageWidth || imageWidth }} × {{ largeImageHeight || imageHeight }}</span>
            </div>
          </div>

          <div class="canvas-container-large" ref="canvasContainerLarge">
            <div class="canvas-wrapper-large" v-if="surface && surface.exampleImage">
              <!-- 背景图片 -->
              <img
                ref="surfaceImageLarge"
                :src="surface.exampleImage"
                @load="onImageLoadLarge"
                :style="{
                  position: 'absolute',
                  zIndex: 1,
                  width: largeImageWidth + 'px',
                  height: largeImageHeight + 'px',
                  objectFit: 'contain'
                }">

              <!-- Fabric.js 画布 -->
              <canvas
                ref="fabricCanvasLarge"
                :width="largeImageWidth"
                :height="largeImageHeight"
                style="position: absolute; z-index: 2; border: 2px solid #409eff;">
              </canvas>
            </div>

            <div v-else class="no-image-large">
              <i class="el-icon-picture-outline"></i>
              <p>请先上传模板面示例图片</p>
            </div>
          </div>
        </div>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="closeAreaEditor">关闭</el-button>
        <el-button
          type="primary"
          @click="saveAndCloseEditor"
          :loading="saveLoading">
          保存并关闭
        </el-button>
      </div>
    </el-dialog>

    <!-- 区域属性编辑面板 -->
    <div v-if="selectedObject" class="properties-panel">
      <el-card shadow="never">
        <div slot="header">
          <span>区域属性</span>
          <el-button
            style="float: right; padding: 3px 0"
            type="text"
            @click="selectedObject = null">
            关闭
          </el-button>
        </div>
        <el-form :model="objectProperties" label-width="80px" size="small">
          <el-form-item label="名称：">
            <el-input v-model="objectProperties.name" @change="updateObjectName"></el-input>
          </el-form-item>
          <el-form-item label="X坐标：">
            <el-input-number v-model="objectProperties.left" @change="updateObjectPosition" :min="0" style="width: 100%"></el-input-number>
          </el-form-item>
          <el-form-item label="Y坐标：">
            <el-input-number v-model="objectProperties.top" @change="updateObjectPosition" :min="0" style="width: 100%"></el-input-number>
          </el-form-item>
          <el-form-item label="宽度：" v-if="objectProperties.width !== undefined">
            <el-input-number v-model="objectProperties.width" @change="updateObjectSize" :min="10" style="width: 100%"></el-input-number>
          </el-form-item>
          <el-form-item label="高度：" v-if="objectProperties.height !== undefined">
            <el-input-number v-model="objectProperties.height" @change="updateObjectSize" :min="10" style="width: 100%"></el-input-number>
          </el-form-item>
          <el-form-item label="旋转角度：">
            <el-input-number v-model="objectProperties.angle" @change="updateObjectRotation" :min="0" :max="360" style="width: 100%"></el-input-number>
          </el-form-item>
          <el-form-item label="透明度：">
            <el-slider v-model="objectProperties.opacity" @change="updateObjectOpacity" :min="0" :max="1" :step="0.1"></el-slider>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script>
import * as fabric from 'fabric'
import { fetchAreaList, createArea, updateArea, deleteArea } from '@/api/diyTemplate'
import { upload } from '@/api/oss'

export default {
  name: 'AreaDrawingTool',
  props: {
    surface: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      // Fabric.js 画布实例
      canvas: null,

      // 绘制模式
      drawingMode: 'select', // select, rect, circle, brush
      isDrawing: false,
      selectedObject: null,

      // 画笔相关
      brushWidth: 20,
      brushPaths: [], // 存储画笔路径

      // 数据
      diyAreas: [],
      selectedAreaIndex: -1,

      // 图片尺寸
      imageWidth: 0,
      imageHeight: 0,

      // 大尺寸画布
      largeImageWidth: 0,
      largeImageHeight: 0,

      // 原图尺寸(用于坐标转换)
      originalImageWidth: 0,
      originalImageHeight: 0,

      // 弹窗状态
      areaEditorVisible: false,

      // 对象属性
      objectProperties: {
        name: '',
        left: 0,
        top: 0,
        width: 0,
        height: 0,
        angle: 0,
        opacity: 1
      },

      // 绘制状态
      startX: 0,
      startY: 0,
      currentShape: null,

      // 保存状态
      saveLoading: false,

      // 区域编辑
      areaDialogVisible: false,
      editingAreaIndex: -1,
      areaForm: {
        name: '',
        x: 0,
        y: 0,
        width: 100,
        height: 100
      },
      areaRules: {
        name: [
          { required: true, message: '请输入区域名称', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    areaDialogTitle() {
      return this.editingAreaIndex >= 0 ? '编辑DIY区域' : '添加DIY区域'
    },
    // 检查是否已达到最大区域数量（每个面只允许一个区域）
    hasMaxAreas() {
      return this.diyAreas.length >= 1
    }
  },
  watch: {
    surface: {
      handler(newSurface) {
        if (newSurface && newSurface.id) {
          this.loadAreas()
        } else {
          this.diyAreas = []
        }
      },
      immediate: true
    }
  },
  mounted() {
    // 画布初始化将在图片加载完成后进行
    this.setupKeyboardShortcuts()
  },
  beforeDestroy() {
    if (this.canvas) {
      this.canvas.dispose()
    }
    this.removeKeyboardShortcuts()
    this.hideTooltip()
    if (this.tooltipElement) {
      document.body.removeChild(this.tooltipElement)
    }
  },
  methods: {
    // 获取区域尺寸文本显示
    getAreaSizeText(area) {
      if (!area.pathData) {
        return `尺寸: ${area.width || 0} × ${area.height || 0}`
      }

      const pathType = this.identifyPathType(area.pathData, area)
      if (pathType === 'circle') {
        const circleParams = this.parseCircleFromPath(area.pathData)
        return `半径: ${Math.round(circleParams.radius)}`
      } else {
        return `尺寸: ${area.width || 0} × ${area.height || 0}`
      }
    },

    // 初始化Fabric.js画布
    initCanvas() {
      if (!this.$refs.fabricCanvas) return

      this.canvas = new fabric.Canvas(this.$refs.fabricCanvas, {
        selection: true,
        preserveObjectStacking: true
      })

      // 设置画布事件监听
      this.setupCanvasEvents()
    },

    // 设置画布事件
    setupCanvasEvents() {
      if (!this.canvas) return

      // 鼠标按下事件
      this.canvas.on('mouse:down', this.onMouseDown)
      // 鼠标移动事件
      this.canvas.on('mouse:move', this.onMouseMove)
      // 鼠标抬起事件
      this.canvas.on('mouse:up', this.onMouseUp)

      // 对象选择事件
      this.canvas.on('selection:created', this.onObjectSelected)
      this.canvas.on('selection:updated', this.onObjectSelected)
      this.canvas.on('selection:cleared', this.onSelectionCleared)

      // 对象修改事件
      this.canvas.on('object:modified', this.onObjectModified)
      this.canvas.on('object:moving', this.onObjectMoving)
      this.canvas.on('object:scaling', this.onObjectScaling)
      this.canvas.on('object:rotating', this.onObjectRotating)

      // 鼠标悬停事件
      this.canvas.on('mouse:over', this.onMouseOver)
      this.canvas.on('mouse:out', this.onMouseOut)

      // 路径事件
      this.canvas.on('path:created', this.onPathCreated)
    },

    loadAreas() {
      if (!this.surface || !this.surface.id) return

      fetchAreaList(this.surface.id).then(response => {
        this.diyAreas = (response.data || []).map(area => {
          // 解析bounds字符串为对象
          if (area.bounds) {
            const [x, y, width, height] = area.bounds.split(',').map(Number)
            area.x = x
            area.y = y
            area.width = width
            area.height = height
          }
          return area
        })
        this.loadAreasToCanvas()
        this.emitChange()
      })
    },

    // 将区域数据加载到画布
    loadAreasToCanvas() {
      if (!this.canvas) {
        console.warn('画布未初始化，无法加载区域')
        return
      }

      console.log('开始加载区域到画布，区域数量:', this.diyAreas.length)
      this.canvas.clear()

      this.diyAreas.forEach((area, index) => {
        console.log('加载区域:', index, area)
        try {
          // 统一使用 createObjectFromArea 方法
          this.createObjectFromArea(area, index)
        } catch (error) {
          console.error('加载区域失败:', error, area)
          // 加载失败时创建默认矩形
          this.createDefaultRectFromArea(area, index)
        }
      })

      this.canvas.renderAll()
      console.log('区域加载完成，画布对象数量:', this.canvas.getObjects().length)
    },

    // 从区域数据创建Fabric对象
    createObjectFromArea(area, index) {
      console.log('🔄 创建区域对象:', area)
      let fabricObject = null

      // 计算从原图坐标到画布坐标的缩放比例
      const scaleToCanvas = this.largeImageWidth > 0 && this.originalImageWidth > 0
        ? this.largeImageWidth / this.originalImageWidth
        : 1

      console.log('🔄 坐标转换比例(原图→画布):', {
        原图尺寸: `${this.originalImageWidth} x ${this.originalImageHeight}`,
        画布尺寸: `${this.largeImageWidth} x ${this.largeImageHeight}`,
        缩放比例: scaleToCanvas
      })

      try {
        // 检查是否为蒙版模式(画笔模式)
        if (area.maskImageUrl) {
          console.log('🎨 检测到蒙版模式区域，不在画布上显示形状')
          // 蒙版模式下不在画布上创建可见对象
          // 只在区域列表中显示即可
          return
        }

        // 根据pathData判断形状类型并创建相应的Fabric对象
        if (area.pathData && area.pathData.trim()) {
          console.log('解析路径数据:', area.pathData)

          // 更智能的路径类型识别
          const pathType = this.identifyPathType(area.pathData, area)
          console.log('识别的路径类型:', pathType)

          if (pathType === 'circle') {
            // 圆形路径 - 从SVG路径数据中解析圆形参数(原图坐标)
            const circleParams = this.parseCircleFromPath(area.pathData)
            console.log('解析的圆形参数(原图坐标):', circleParams)

            // 转换为画布坐标
            const canvasLeft = (circleParams.centerX - circleParams.radius) * scaleToCanvas
            const canvasTop = (circleParams.centerY - circleParams.radius) * scaleToCanvas
            const canvasRadius = circleParams.radius * scaleToCanvas

            console.log('转换后的画布坐标:', { canvasLeft, canvasTop, canvasRadius })

            fabricObject = new fabric.Circle({
              left: canvasLeft,
              top: canvasTop,
              radius: canvasRadius,
              fill: 'rgba(64, 158, 255, 0.3)',
              stroke: '#409eff',
              strokeWidth: 2,
              selectable: true
            })
          } else if (pathType === 'rectangle') {
            // 标准矩形路径 - 从pathData解析坐标和尺寸(原图坐标)
            const rectParams = this.parseRectFromPath(area.pathData)
            console.log('从pathData解析的矩形参数(原图坐标):', rectParams)

            if (rectParams) {
              // 转换为画布坐标
              const canvasLeft = rectParams.x * scaleToCanvas
              const canvasTop = rectParams.y * scaleToCanvas
              const canvasWidth = rectParams.width * scaleToCanvas
              const canvasHeight = rectParams.height * scaleToCanvas

              console.log('转换后的画布坐标:', { canvasLeft, canvasTop, canvasWidth, canvasHeight })

              fabricObject = new fabric.Rect({
                left: canvasLeft,
                top: canvasTop,
                width: canvasWidth,
                height: canvasHeight,
                fill: 'rgba(64, 158, 255, 0.3)',
                stroke: '#409eff',
                strokeWidth: 2,
                selectable: true
              })
            } else {
              // 解析失败，使用area的属性(原图坐标)并转换
              fabricObject = new fabric.Rect({
                left: (area.x || 0) * scaleToCanvas,
                top: (area.y || 0) * scaleToCanvas,
                width: (area.width || 100) * scaleToCanvas,
                height: (area.height || 100) * scaleToCanvas,
                fill: 'rgba(64, 158, 255, 0.3)',
                stroke: '#409eff',
                strokeWidth: 2,
                selectable: true
              })
            }
          } else {
            // 其他情况都创建矩形(原图坐标)并转换
            fabricObject = new fabric.Rect({
              left: (area.x || 0) * scaleToCanvas,
              top: (area.y || 0) * scaleToCanvas,
              width: (area.width || 100) * scaleToCanvas,
              height: (area.height || 100) * scaleToCanvas,
              fill: 'rgba(64, 158, 255, 0.3)',
              stroke: '#409eff',
              strokeWidth: 2,
              selectable: true
            })
          }
        } else {
          // 没有pathData或pathData为空，创建默认矩形(原图坐标)并转换
          fabricObject = new fabric.Rect({
            left: (area.x || 0) * scaleToCanvas,
            top: (area.y || 0) * scaleToCanvas,
            width: (area.width || 100) * scaleToCanvas,
            height: (area.height || 100) * scaleToCanvas,
            fill: 'rgba(64, 158, 255, 0.3)',
            stroke: '#409eff',
            strokeWidth: 2,
            selectable: true
          })
        }
      } catch (error) {
        console.error('创建Fabric对象失败:', error, area)
        // 创建失败时，使用默认矩形(原图坐标)并转换
        fabricObject = new fabric.Rect({
          left: (area.x || 0) * scaleToCanvas,
          top: (area.y || 0) * scaleToCanvas,
          width: (area.width || 100) * scaleToCanvas,
          height: (area.height || 100) * scaleToCanvas,
          fill: 'rgba(64, 158, 255, 0.3)',
          stroke: '#409eff',
          strokeWidth: 2,
          selectable: true
        })
      }

      // 设置通用属性和交互属性
      fabricObject.set({
        areaIndex: index,
        areaId: area.id,
        areaName: area.name,
        // 启用所有交互功能
        selectable: true,
        evented: true,
        moveable: true,
        // 启用变换控制
        hasControls: true,
        hasBorders: true,
        hasRotatingPoint: true,
        // 设置控制点样式
        cornerColor: '#409eff',
        cornerStyle: 'circle',
        cornerSize: 8,
        transparentCorners: false,
        borderColor: '#409eff',
        borderScaleFactor: 2
      })

      this.canvas.add(fabricObject)
    },

    // 智能识别路径类型
    identifyPathType(pathData, area) {
      if (!pathData || typeof pathData !== 'string') {
        return 'rectangle'
      }

      const path = pathData.trim()

      // 检查是否为圆形路径（包含弧线命令A）
      if (path.includes('A ')) {
        return 'circle'
      }

      // 检查是否为标准矩形路径
      if (this.isStandardRectanglePath(path, area)) {
        return 'rectangle'
      }

      // 其他情况都视为矩形路径
      return 'rectangle'
    },

    // 从SVG路径数据中解析圆形参数
    parseCircleFromPath(pathData) {
      try {
        // 圆形路径格式: M cx cy A rx ry 0 1 1 cx+rx cy A rx ry 0 1 1 cx cy Z
        // 或者: M startX startY A rx ry 0 1 1 endX endY A rx ry 0 1 1 startX startY Z

        // 使用正则表达式解析路径
        const pathRegex = /M\s+([\d.]+)\s+([\d.]+)\s+A\s+([\d.]+)\s+([\d.]+)\s+0\s+1\s+1\s+([\d.]+)\s+([\d.]+)/
        const match = pathData.match(pathRegex)

        if (match) {
          const [, startX, startY, rx, ry, endX, endY] = match.map(Number)

          // 计算圆心坐标
          const centerX = (startX + endX) / 2
          const centerY = (startY + endY) / 2

          // 使用rx作为半径（假设是正圆）
          const radius = rx

          console.log('圆形路径解析结果:', {
            pathData,
            startX, startY, endX, endY,
            rx, ry,
            centerX, centerY, radius
          })

          return {
            centerX,
            centerY,
            radius
          }
        }

        // 如果解析失败，返回默认值
        console.warn('圆形路径解析失败，使用默认值:', pathData)
        return {
          centerX: 100,
          centerY: 100,
          radius: 50
        }
      } catch (error) {
        console.error('解析圆形路径时出错:', error, pathData)
        return {
          centerX: 100,
          centerY: 100,
          radius: 50
        }
      }
    },

    // 从SVG路径数据中解析矩形参数
    parseRectFromPath(pathData) {
      try {
        // 矩形路径格式: M x1 y1 L x2 y1 L x2 y2 L x1 y2 Z
        const rectRegex = /M\s+([\d.]+)\s+([\d.]+)\s+L\s+([\d.]+)\s+([\d.]+)\s+L\s+([\d.]+)\s+([\d.]+)\s+L\s+([\d.]+)\s+([\d.]+)/
        const match = pathData.match(rectRegex)

        if (match) {
          const [, x1, y1, x2, y2_1, x2_2, y2, x1_2, y2_2] = match.map(Number)

          // 计算矩形的位置和尺寸
          const x = Math.min(x1, x2)
          const y = Math.min(y1, y2)
          const width = Math.abs(x2 - x1)
          const height = Math.abs(y2 - y1)

          console.log('✅ 矩形路径解析成功:', {
            pathData,
            解析结果: { x, y, width, height },
            原始坐标: {
              左上: `(${x1}, ${y1})`,
              右上: `(${x2}, ${y2_1})`,
              右下: `(${x2_2}, ${y2})`,
              左下: `(${x1_2}, ${y2_2})`
            }
          })

          return { x, y, width, height }
        }

        // 如果解析失败，返回null
        console.warn('矩形路径解析失败:', pathData)
        return null
      } catch (error) {
        console.error('解析矩形路径时出错:', error, pathData)
        return null
      }
    },

    // 检查是否为标准矩形路径
    isStandardRectanglePath(pathData, area) {
      try {
        // 标准矩形路径格式: M x y L x+w y L x+w y+h L x y+h Z
        const regex = /^M\s+(\d+(?:\.\d+)?)\s+(\d+(?:\.\d+)?)\s+L\s+(\d+(?:\.\d+)?)\s+(\d+(?:\.\d+)?)\s+L\s+(\d+(?:\.\d+)?)\s+(\d+(?:\.\d+)?)\s+L\s+(\d+(?:\.\d+)?)\s+(\d+(?:\.\d+)?)\s+Z$/
        const match = pathData.trim().match(regex)

        if (!match) {
          return false
        }

        const [, x1, y1, x2, y2, x3, y3, x4, y4] = match.map(Number)

        // 检查是否形成标准矩形
        // 矩形的四个点应该是: (x,y), (x+w,y), (x+w,y+h), (x,y+h)
        const isRectangle = (
          x1 === x4 && y1 === y2 &&  // 第一点和第四点x相同，第一点和第二点y相同
          x2 === x3 && y3 === y4 &&  // 第二点和第三点x相同，第三点和第四点y相同
          Math.abs(x2 - x1) > 0 &&   // 有宽度
          Math.abs(y3 - y1) > 0      // 有高度
        )

        console.log('矩形路径检查:', { pathData, isRectangle, points: [x1, y1, x2, y2, x3, y3, x4, y4] })
        return isRectangle
      } catch (error) {
        console.error('矩形路径检查失败:', error)
        return false
      }
    },

    // 从区域数据创建默认矩形
    createDefaultRectFromArea(area, index) {
      // 计算从原图坐标到画布坐标的缩放比例
      const scaleToCanvas = this.largeImageWidth > 0 && this.originalImageWidth > 0
        ? this.largeImageWidth / this.originalImageWidth
        : 1

      const fabricObject = new fabric.Rect({
        left: (area.x || 0) * scaleToCanvas,
        top: (area.y || 0) * scaleToCanvas,
        width: (area.width || 100) * scaleToCanvas,
        height: (area.height || 100) * scaleToCanvas,
        fill: 'rgba(64, 158, 255, 0.3)',
        stroke: '#409eff',
        strokeWidth: 2,
        selectable: true
      })

      // 设置通用属性和交互属性
      fabricObject.set({
        areaIndex: index,
        areaId: area.id,
        areaName: area.name,
        // 启用所有交互功能
        selectable: true,
        evented: true,
        moveable: true,
        // 启用变换控制
        hasControls: true,
        hasBorders: true,
        hasRotatingPoint: true,
        // 设置控制点样式
        cornerColor: '#409eff',
        cornerStyle: 'circle',
        cornerSize: 8,
        transparentCorners: false,
        borderColor: '#409eff',
        borderScaleFactor: 2
      })

      this.canvas.add(fabricObject)
    },



    onImageLoad() {
      const img = this.$refs.surfaceImage
      this.imageWidth = img.clientWidth
      this.imageHeight = img.clientHeight

      // 图片加载完成后初始化画布
      this.$nextTick(() => {
        if (!this.canvas) {
          this.initCanvas()
        } else {
          // 重新设置画布尺寸
          this.canvas.setDimensions({
            width: this.imageWidth,
            height: this.imageHeight
          })
        }
      })
    },

    // 大尺寸图片加载
    onImageLoadLarge() {
      const img = this.$refs.surfaceImageLarge
      if (img && img.naturalWidth && img.naturalHeight) {
        console.log('📸 图片加载完成，原始尺寸:', img.naturalWidth, 'x', img.naturalHeight)

        // 保存原图尺寸(用于坐标转换)
        this.originalImageWidth = img.naturalWidth
        this.originalImageHeight = img.naturalHeight

        // 计算合适的显示尺寸（最大800px宽度）
        const maxWidth = 800
        const aspectRatio = img.naturalHeight / img.naturalWidth

        if (img.naturalWidth > maxWidth) {
          this.largeImageWidth = maxWidth
          this.largeImageHeight = maxWidth * aspectRatio
        } else {
          this.largeImageWidth = img.naturalWidth
          this.largeImageHeight = img.naturalHeight
        }

        // 计算缩放比例
        const scaleRatio = this.largeImageWidth / this.originalImageWidth
        console.log('📐 画布缩放比例:', scaleRatio, '(画布尺寸:', this.largeImageWidth, 'x', this.largeImageHeight, ')')

        // 设置图片显示尺寸
        img.style.width = this.largeImageWidth + 'px'
        img.style.height = this.largeImageHeight + 'px'

        // 初始化大画布
        this.$nextTick(() => {
          this.initLargeCanvas()
        })
      } else {
        console.warn('图片尺寸获取失败，延迟重试')
        // 延迟重试
        setTimeout(() => {
          this.onImageLoadLarge()
        }, 100)
      }
    },

    // 初始化大画布
    initLargeCanvas() {
      if (!this.$refs.fabricCanvasLarge) return

      // 如果已有画布，先销毁
      if (this.canvas) {
        this.canvas.dispose()
      }

      this.canvas = new fabric.Canvas(this.$refs.fabricCanvasLarge, {
        selection: true,
        preserveObjectStacking: true,
        width: this.largeImageWidth,
        height: this.largeImageHeight,
        // 启用交互功能
        interactive: true,
        moveCursor: 'move',
        hoverCursor: 'move',
        defaultCursor: 'default',
        // 启用对象控制
        centeredScaling: false,
        centeredRotation: true
      })

      // 设置画布事件监听
      this.setupCanvasEvents()

      // 确保画布完全初始化后再加载区域
      this.$nextTick(() => {
        this.loadAreasToCanvas()
        this.canvas.renderAll()
        console.log('画布初始化完成，已加载区域数量:', this.diyAreas.length)
      })
    },

    // 设置绘制模式
    setDrawingMode(mode) {
      // 检查是否已达到最大区域数量
      if (mode !== 'select' && this.hasMaxAreas) {
        this.$message.warning('每个模板面只允许编辑一个DIY区域，请先删除现有区域')
        return
      }

      this.drawingMode = mode
      this.selectedObject = null
      this.isDrawing = false

      if (this.canvas) {
        // 根据模式设置画布属性
        if (mode === 'select') {
          this.canvas.selection = true
          this.canvas.defaultCursor = 'default'
          this.canvas.isDrawingMode = false
        } else if (mode === 'brush') {
          // 画笔模式
          this.canvas.selection = false
          this.canvas.isDrawingMode = true
          this.canvas.freeDrawingBrush = new fabric.PencilBrush(this.canvas)
          this.canvas.freeDrawingBrush.color = 'black'
          this.canvas.freeDrawingBrush.width = this.brushWidth
          this.brushPaths = [] // 清空之前的画笔路径
          console.log('🖌️ 画笔模式已启用，画笔粗细:', this.brushWidth)
        } else {
          this.canvas.selection = false
          this.canvas.defaultCursor = 'crosshair'
          this.canvas.isDrawingMode = false
        }

        // 清除当前选择
        this.canvas.discardActiveObject()
        this.canvas.renderAll()
      }
    },

    // 更新画笔粗细
    updateBrushWidth() {
      if (this.canvas && this.canvas.freeDrawingBrush) {
        this.canvas.freeDrawingBrush.width = this.brushWidth
        console.log('画笔粗细已更新:', this.brushWidth)
      }
    },

    // 鼠标事件处理
    onMouseDown(options) {
      if (this.drawingMode === 'select') return

      // 检查是否已达到最大区域数量
      if (this.hasMaxAreas) {
        this.$message.warning('每个模板面只允许编辑一个DIY区域')
        return
      }

      const pointer = this.canvas.getPointer(options.e)
      this.startX = pointer.x
      this.startY = pointer.y

      switch (this.drawingMode) {
        case 'rect':
          this.isDrawing = true
          this.startDrawingRect(pointer)
          break
        case 'circle':
          this.isDrawing = true
          this.startDrawingCircle(pointer)
          break
      }
    },

    onMouseMove(options) {
      if (this.drawingMode === 'select' || !this.isDrawing) return

      const pointer = this.canvas.getPointer(options.e)

      switch (this.drawingMode) {
        case 'rect':
          this.updateDrawingRect(pointer)
          break
        case 'circle':
          this.updateDrawingCircle(pointer)
          break
      }
    },

    onMouseUp(options) {
      if (!this.isDrawing || this.drawingMode === 'select') return

      this.isDrawing = false

      if (this.currentShape) {
        this.finalizeShape()
      }
    },



    // 对象选择事件
    onObjectSelected(options) {
      const activeObject = options.target || options.selected[0]
      if (activeObject) {
        this.selectedObject = activeObject
        this.updateObjectProperties(activeObject)

        // 更新选中的区域索引
        if (activeObject.areaIndex !== undefined) {
          this.selectedAreaIndex = activeObject.areaIndex
        }

        // 增强选择样式
        this.enhanceSelectedObject(activeObject)

        // 显示选择反馈
        const areaName = activeObject.areaName || `区域${(activeObject.areaIndex || 0) + 1}`
        this.showOperationFeedback(`已选择 ${areaName}`, 'info')
      }
    },

    onSelectionCleared() {
      // 恢复之前选中对象的样式
      if (this.selectedObject) {
        this.restoreObjectStyle(this.selectedObject)
      }

      this.selectedObject = null
      this.selectedAreaIndex = -1
      this.hideTooltip()
    },

    onObjectModified(options) {
      const obj = options.target
      if (obj && obj.areaIndex !== undefined) {
        // 更新区域数据
        this.updateAreaFromObject(obj)

        // 显示修改完成提示
        this.showOperationFeedback('区域已修改', 'success')
      }
    },

    // 对象移动中事件
    onObjectMoving(options) {
      const obj = options.target
      if (obj && obj.areaIndex !== undefined) {
        // 实时更新属性面板
        this.updateObjectProperties(obj)

        // 显示实时坐标
        this.showCoordinateTooltip(obj)
      }
    },

    // 对象缩放中事件
    onObjectScaling(options) {
      const obj = options.target
      if (obj && obj.areaIndex !== undefined) {
        // 实时更新属性面板
        this.updateObjectProperties(obj)

        // 显示实时尺寸
        this.showSizeTooltip(obj)
      }
    },

    // 对象旋转中事件
    onObjectRotating(options) {
      const obj = options.target
      if (obj && obj.areaIndex !== undefined) {
        // 实时更新属性面板
        this.updateObjectProperties(obj)

        // 显示实时角度
        this.showAngleTooltip(obj)
      }
    },

    // 鼠标悬停事件
    onMouseOver(options) {
      if (options.target && options.target.areaIndex !== undefined) {
        // 高亮显示悬停的区域
        options.target.set({
          stroke: '#67c23a',
          strokeWidth: 3
        })
        this.canvas.renderAll()

        // 显示区域信息提示
        this.showAreaTooltip(options.target)
      }
    },

    // 鼠标离开事件
    onMouseOut(options) {
      if (options.target && options.target.areaIndex !== undefined) {
        // 恢复正常样式
        options.target.set({
          stroke: '#409eff',
          strokeWidth: 2
        })
        this.canvas.renderAll()

        // 隐藏提示
        this.hideTooltip()
      }
    },

    // 路径创建事件
    onPathCreated(options) {
      if (options.path && this.drawingMode === 'brush') {
        // 画笔模式：收集路径
        this.brushPaths.push(options.path)
        console.log('🖌️ 画笔路径已添加，当前路径数:', this.brushPaths.length)

        // 设置路径样式
        options.path.set({
          stroke: 'black',
          strokeWidth: this.brushWidth,
          fill: null,
          selectable: false
        })

        this.canvas.renderAll()
      }
    },

    // 更新对象属性面板
    updateObjectProperties(obj) {
      this.objectProperties = {
        name: obj.areaName || `区域${(obj.areaIndex || 0) + 1}`,
        left: Math.round(obj.left || 0),
        top: Math.round(obj.top || 0),
        width: Math.round(obj.width * (obj.scaleX || 1)),
        height: Math.round(obj.height * (obj.scaleY || 1)),
        angle: Math.round(obj.angle || 0),
        opacity: obj.opacity || 1
      }
    },

    // 从对象更新区域数据
    updateAreaFromObject(obj) {
      if (obj.areaIndex !== undefined && this.diyAreas[obj.areaIndex]) {
        const area = this.diyAreas[obj.areaIndex]

        // 计算实际的位置和尺寸
        area.x = Math.round(obj.left || 0)
        area.y = Math.round(obj.top || 0)
        area.width = Math.round((obj.width || 0) * (obj.scaleX || 1))
        area.height = Math.round((obj.height || 0) * (obj.scaleY || 1))
        area.bounds = `${area.x},${area.y},${area.width},${area.height}`

        // 更新pathData
        area.pathData = this.getShapePathData(obj)

        console.log('更新区域数据:', {
          区域名称: area.name,
          bounds: area.bounds,
          pathData: area.pathData
        })

        // 标记需要更新
        if (area.id) {
          area.needsUpdate = true
        }

        this.emitChange()
      }
    },

    // 添加DIY区域
    addArea() {
      this.editingAreaIndex = -1
      this.areaForm = {
        name: `区域${this.diyAreas.length + 1}`,
        x: 50,
        y: 50,
        width: 100,
        height: 100
      }
      this.areaDialogVisible = true
    },

    // 选择区域
    selectAreaByIndex(index) {
      this.selectedAreaIndex = index

      // 检查是否为蒙版模式区域
      const area = this.diyAreas[index]
      if (area && area.maskImageUrl) {
        console.log('🎨 选中蒙版模式区域，不在画布上选中对象')
        // 蒙版模式区域没有画布对象，只更新选中索引
        return
      }

      // 在画布上选中对应的对象
      if (this.canvas && index >= 0 && index < this.diyAreas.length) {
        const objects = this.canvas.getObjects()
        if (objects[index]) {
          this.canvas.setActiveObject(objects[index])
          this.canvas.renderAll()
          console.log('选中画布对象:', index, objects[index])
        }
      }
    },

    // 编辑区域
    editArea(index) {
      this.editingAreaIndex = index
      const area = this.diyAreas[index]
      this.areaForm = {
        name: area.name,
        x: area.x || 0,
        y: area.y || 0,
        width: area.width || 100,
        height: area.height || 100
      }
      this.areaDialogVisible = true
    },

    // 删除区域
    deleteAreaByIndex(index) {
      const area = this.diyAreas[index]

      // 从画布上删除对应的对象
      if (this.canvas) {
        const objects = this.canvas.getObjects()
        if (objects[index]) {
          this.canvas.remove(objects[index])
          this.canvas.renderAll()
          console.log('从画布删除对象:', index)
        }
      }

      // 从服务器删除
      if (area.id) {
        deleteArea(area.id).then(() => {
          this.diyAreas.splice(index, 1)
          this.selectedAreaIndex = -1
          this.selectedObject = null
          this.emitChange()
          this.$message.success('删除成功')
        })
      } else {
        this.diyAreas.splice(index, 1)
        this.selectedAreaIndex = -1
        this.selectedObject = null
        this.emitChange()
      }
    },

    // 绘制工具方法
    startDrawingRect(pointer) {
      const rect = new fabric.Rect({
        left: pointer.x,
        top: pointer.y,
        width: 0,
        height: 0,
        fill: 'rgba(64, 158, 255, 0.3)',
        stroke: '#409eff',
        strokeWidth: 2,
        selectable: false
      })

      this.canvas.add(rect)
      this.currentShape = rect
    },

    updateDrawingRect(pointer) {
      if (!this.currentShape) return

      const width = Math.abs(pointer.x - this.startX)
      const height = Math.abs(pointer.y - this.startY)
      const left = Math.min(pointer.x, this.startX)
      const top = Math.min(pointer.y, this.startY)

      this.currentShape.set({
        left: left,
        top: top,
        width: width,
        height: height
      })

      this.canvas.renderAll()
    },

    startDrawingCircle(pointer) {
      const circle = new fabric.Circle({
        left: pointer.x,
        top: pointer.y,
        radius: 0,
        fill: 'rgba(64, 158, 255, 0.3)',
        stroke: '#409eff',
        strokeWidth: 2,
        selectable: false
      })

      this.canvas.add(circle)
      this.currentShape = circle
    },

    updateDrawingCircle(pointer) {
      if (!this.currentShape) return

      const radius = Math.sqrt(
        Math.pow(pointer.x - this.startX, 2) +
        Math.pow(pointer.y - this.startY, 2)
      ) / 2

      this.currentShape.set({
        radius: radius,
        left: this.startX - radius,
        top: this.startY - radius
      })

      this.canvas.renderAll()
    },





    // 完成形状绘制
    finalizeShape() {
      if (!this.currentShape) return

      this.currentShape.set({
        selectable: true
      })

      // 添加到区域列表
      this.addShapeToAreas(this.currentShape)

      this.currentShape = null
      this.canvas.renderAll()
    },

    // 将形状添加到区域列表
    addShapeToAreas(shape) {
      console.log('🔍 addShapeToAreas - 形状信息:', {
        type: shape.type,
        left: shape.left,
        top: shape.top,
        width: shape.width,
        height: shape.height,
        scaleX: shape.scaleX,
        scaleY: shape.scaleY
      })

      // 计算从画布坐标到原图坐标的缩放比例
      const scaleToOriginal = this.originalImageWidth > 0
        ? this.originalImageWidth / this.largeImageWidth
        : 1

      // 计算画布坐标
      const canvasLeft = Math.round(shape.left || 0)
      const canvasTop = Math.round(shape.top || 0)
      const canvasWidth = Math.round((shape.width || 0) * (shape.scaleX || 1))
      const canvasHeight = Math.round((shape.height || 0) * (shape.scaleY || 1))

      // 转换为原图坐标
      const left = Math.round(canvasLeft * scaleToOriginal)
      const top = Math.round(canvasTop * scaleToOriginal)
      const width = Math.round(canvasWidth * scaleToOriginal)
      const height = Math.round(canvasHeight * scaleToOriginal)

      console.log('🔍 addShapeToAreas - 坐标转换:', {
        画布坐标: { left: canvasLeft, top: canvasTop, width: canvasWidth, height: canvasHeight },
        原图坐标: { left, top, width, height },
        缩放比例: scaleToOriginal
      })

      const pathData = this.getShapePathData(shape)
      console.log('🔍 addShapeToAreas - pathData:', pathData)

      // 验证pathData不为空
      if (!pathData || pathData.trim() === '') {
        console.error('生成路径数据失败，无法保存区域:', shape)
        this.$message.error('生成区域路径数据失败，请重试')
        return
      }

      // 验证区域尺寸不超过限制(1000px)
      const MAX_SIZE = 1000
      if (width > MAX_SIZE || height > MAX_SIZE) {
        console.error('区域尺寸超出限制:', { width, height, maxSize: MAX_SIZE })
        this.$message.error(`定制区域的宽度和高度不能超过 ${MAX_SIZE}px，当前尺寸: ${width}x${height}px。请重新绘制较小的区域。`)
        // 移除刚绘制的形状
        this.canvas.remove(shape)
        this.canvas.renderAll()
        return
      }

      const area = {
        surfaceId: this.surface.id,
        name: `区域${this.diyAreas.length + 1}`,
        x: left,
        y: top,
        width: width,
        height: height,
        bounds: `${left},${top},${width},${height}`,
        pathData: pathData
      }

      console.log('✅ 准备保存区域(原图坐标):', area)

      // 设置形状属性
      shape.set({
        areaIndex: this.diyAreas.length,
        areaName: area.name
      })

      // 先添加到本地列表
      this.diyAreas.push(area)
      this.emitChange()

      // 如果有surface ID，生成蒙版并保存到数据库
      if (this.surface && this.surface.id) {
        this.saveAreaWithMask(area, shape)
      }
    },

    /**
     * 保存区域并生成蒙版
     */
    async saveAreaWithMask(area, shape) {
      try {
        // 1. 生成并上传蒙版图片
        let maskImageUrl = null
        try {
          console.log('🎨 开始生成蒙版图片...')
          const maskDataURL = this.exportMaskFromShape(shape)
          maskImageUrl = await this.uploadMaskToOSS(maskDataURL)
          console.log('✅ 蒙版图片上传成功:', maskImageUrl)
          area.maskImageUrl = maskImageUrl
        } catch (error) {
          console.error('蒙版图片生成/上传失败:', error)
          this.$message.warning(`蒙版图片上传失败，将继续保存区域数据`)
        }

        // 2. 保存区域到数据库
        const response = await createArea(area)

        // 3. 更新区域ID
        area.id = response.data.id
        shape.set({ areaId: area.id })

        this.$message.success(`${area.name}已保存`)
      } catch (error) {
        console.error('保存区域失败:', error)
        const errorMessage = error.response && error.response.data && error.response.data.message
          ? error.response.data.message
          : error.message || '未知错误'
        this.$message.error(`保存区域失败: ${errorMessage}`)
      }
    },

    // 获取形状的路径数据(转换为原图坐标)
    getShapePathData(shape) {
      try {
        // 计算从画布坐标到原图坐标的缩放比例
        const scaleToOriginal = this.originalImageWidth > 0
          ? this.originalImageWidth / this.largeImageWidth
          : 1

        console.log('🔄 坐标转换比例:', {
          原图尺寸: `${this.originalImageWidth} x ${this.originalImageHeight}`,
          画布尺寸: `${this.largeImageWidth} x ${this.largeImageHeight}`,
          缩放比例: scaleToOriginal
        })

        if (shape.type === 'rect') {
          // 画布坐标
          const canvasLeft = Math.round(shape.left || 0)
          const canvasTop = Math.round(shape.top || 0)
          const canvasWidth = Math.round((shape.width || 0) * (shape.scaleX || 1))
          const canvasHeight = Math.round((shape.height || 0) * (shape.scaleY || 1))

          // 转换为原图坐标
          const left = Math.round(canvasLeft * scaleToOriginal)
          const top = Math.round(canvasTop * scaleToOriginal)
          const width = Math.round(canvasWidth * scaleToOriginal)
          const height = Math.round(canvasHeight * scaleToOriginal)

          console.log('📐 生成矩形路径数据:', {
            画布坐标: { left: canvasLeft, top: canvasTop, width: canvasWidth, height: canvasHeight },
            原图坐标: { left, top, width, height },
            pathData: `M ${left} ${top} L ${left + width} ${top} L ${left + width} ${top + height} L ${left} ${top + height} Z`
          })

          return `M ${left} ${top} L ${left + width} ${top} L ${left + width} ${top + height} L ${left} ${top + height} Z`
        } else if (shape.type === 'circle') {
          // 画布坐标
          const canvasLeft = Math.round(shape.left || 0)
          const canvasTop = Math.round(shape.top || 0)
          const canvasRadius = Math.round((shape.radius || 50) * (shape.scaleX || 1))

          // 转换为原图坐标
          const left = Math.round(canvasLeft * scaleToOriginal)
          const top = Math.round(canvasTop * scaleToOriginal)
          const radius = Math.round(canvasRadius * scaleToOriginal)
          const cx = left + radius
          const cy = top + radius

          console.log('📐 生成圆形路径数据:', {
            画布坐标: { left: canvasLeft, top: canvasTop, radius: canvasRadius },
            原图坐标: { left, top, radius, cx, cy },
            pathData: `M ${cx - radius} ${cy} A ${radius} ${radius} 0 1 1 ${cx + radius} ${cy} A ${radius} ${radius} 0 1 1 ${cx - radius} ${cy} Z`
          })

          return `M ${cx - radius} ${cy} A ${radius} ${radius} 0 1 1 ${cx + radius} ${cy} A ${radius} ${radius} 0 1 1 ${cx - radius} ${cy} Z`
        }

        // 如果无法获取特定类型的路径数据，生成默认矩形路径
        const canvasLeft = Math.round(shape.left || 0)
        const canvasTop = Math.round(shape.top || 0)
        const canvasWidth = Math.round((shape.width || 100) * (shape.scaleX || 1))
        const canvasHeight = Math.round((shape.height || 100) * (shape.scaleY || 1))

        const left = Math.round(canvasLeft * scaleToOriginal)
        const top = Math.round(canvasTop * scaleToOriginal)
        const width = Math.round(canvasWidth * scaleToOriginal)
        const height = Math.round(canvasHeight * scaleToOriginal)

        return `M ${left} ${top} L ${left + width} ${top} L ${left + width} ${top + height} L ${left} ${top + height} Z`
      } catch (error) {
        console.error('生成路径数据失败:', error, shape)
        // 返回默认矩形路径
        return 'M 0 0 L 100 0 L 100 100 L 0 100 Z'
      }
    },

    // 删除选中对象
    deleteSelected() {
      if (!this.selectedObject) {
        this.$message.warning('请先选择要删除的区域')
        return
      }

      try {
        // 保存选中对象的引用，防止在删除过程中被清空
        const objectToDelete = this.selectedObject
        console.log('删除选中对象:', objectToDelete)

        // 从画布中移除对象
        this.canvas.remove(objectToDelete)

        // 从区域列表中删除 - 增强安全检查
        if (objectToDelete &&
            typeof objectToDelete.areaIndex === 'number' &&
            objectToDelete.areaIndex >= 0) {
          console.log('删除区域索引:', objectToDelete.areaIndex)
          this.deleteAreaByIndex(objectToDelete.areaIndex)
        } else if (objectToDelete && objectToDelete.areaId) {
          // 如果没有areaIndex但有areaId，通过ID删除
          console.log('通过ID删除区域:', objectToDelete.areaId)
          const areaIndex = this.diyAreas.findIndex(area => area.id === objectToDelete.areaId)
          if (areaIndex >= 0) {
            this.deleteAreaByIndex(areaIndex)
          }
        } else {
          console.warn('无法确定要删除的区域索引或ID:', objectToDelete)
        }

        // 清空选中对象
        this.selectedObject = null

        // 重新渲染画布
        this.canvas.renderAll()

        // 删除后重新启用绘制工具
        this.$message.success('区域已删除，现在可以绘制新的区域')
      } catch (error) {
        console.error('删除区域失败:', error)
        this.$message.error('删除区域失败: ' + (error.message || '未知错误'))
      }
    },

    // 清空画布
    clearAll() {
      this.$confirm('确认清空所有DIY区域吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 清空Fabric画布
        if (this.canvas) {
          this.canvas.clear()
        }

        // 删除服务器数据
        const deletePromises = this.diyAreas
          .filter(area => area.id)
          .map(area => deleteArea(area.id))

        Promise.all(deletePromises).then(() => {
          this.diyAreas = []
          this.selectedAreaIndex = -1
          this.selectedObject = null
          this.emitChange()
          this.$message.success('清空成功')
        })
      })
    },

    // 属性编辑方法
    updateObjectName() {
      if (this.selectedObject) {
        this.selectedObject.areaName = this.objectProperties.name

        // 更新区域数据
        if (this.selectedObject.areaIndex !== undefined) {
          const area = this.diyAreas[this.selectedObject.areaIndex]
          if (area) {
            area.name = this.objectProperties.name
            this.emitChange()
          }
        }
      }
    },

    updateObjectPosition() {
      if (this.selectedObject) {
        this.selectedObject.set({
          left: this.objectProperties.left,
          top: this.objectProperties.top
        })
        this.canvas.renderAll()
        this.updateAreaFromObject(this.selectedObject)
      }
    },

    updateObjectSize() {
      if (this.selectedObject) {
        const scaleX = this.objectProperties.width / this.selectedObject.width
        const scaleY = this.objectProperties.height / this.selectedObject.height

        this.selectedObject.set({
          scaleX: scaleX,
          scaleY: scaleY
        })
        this.canvas.renderAll()
        this.updateAreaFromObject(this.selectedObject)
      }
    },

    updateObjectRotation() {
      if (this.selectedObject) {
        this.selectedObject.set({
          angle: this.objectProperties.angle
        })
        this.canvas.renderAll()
      }
    },

    updateObjectOpacity() {
      if (this.selectedObject) {
        this.selectedObject.set({
          opacity: this.objectProperties.opacity
        })
        this.canvas.renderAll()
      }
    },

    // 保存区域
    saveArea() {
      this.$refs.areaForm.validate(valid => {
        if (valid) {
          // 验证区域尺寸不超过限制(1000px)
          const MAX_SIZE = 1000
          if (this.areaForm.width > MAX_SIZE || this.areaForm.height > MAX_SIZE) {
            this.$message.error(`定制区域的宽度和高度不能超过 ${MAX_SIZE}px，当前尺寸: ${this.areaForm.width}x${this.areaForm.height}px`)
            return
          }

          const areaData = {
            surfaceId: this.surface.id,
            name: this.areaForm.name,
            pathData: `M ${this.areaForm.x} ${this.areaForm.y} L ${this.areaForm.x + this.areaForm.width} ${this.areaForm.y} L ${this.areaForm.x + this.areaForm.width} ${this.areaForm.y + this.areaForm.height} L ${this.areaForm.x} ${this.areaForm.y + this.areaForm.height} Z`,
            bounds: `${this.areaForm.x},${this.areaForm.y},${this.areaForm.width},${this.areaForm.height}`,
            x: this.areaForm.x,
            y: this.areaForm.y,
            width: this.areaForm.width,
            height: this.areaForm.height
          }

          if (this.editingAreaIndex >= 0) {
            // 编辑模式
            const area = this.diyAreas[this.editingAreaIndex]
            if (area.id) {
              updateArea(area.id, areaData).then(() => {
                Object.assign(this.diyAreas[this.editingAreaIndex], areaData)
                this.areaDialogVisible = false
                this.emitChange()
                this.$message.success('更新成功')
              })
            } else {
              Object.assign(this.diyAreas[this.editingAreaIndex], areaData)
              this.areaDialogVisible = false
              this.emitChange()
            }
          } else {
            // 新增模式
            if (this.surface.id) {
              createArea(areaData).then(response => {
                areaData.id = response.data.id
                this.diyAreas.push(areaData)
                this.areaDialogVisible = false
                this.emitChange()
                this.$message.success('添加成功')
              })
            } else {
              this.diyAreas.push(areaData)
              this.areaDialogVisible = false
              this.emitChange()
            }
          }
        }
      })
    },

    getAreaStyle(area) {
      return {
        position: 'absolute',
        left: (area.x || 0) + 'px',
        top: (area.y || 0) + 'px',
        width: (area.width || 100) + 'px',
        height: (area.height || 100) + 'px'
      }
    },

    // 保存当前模板面的所有区域
    async saveCurrentSurface() {
      if (!this.surface || !this.surface.id) {
        this.$message.warning('请先保存模板面信息')
        return
      }

      // 画笔模式：先保存画笔区域
      if (this.drawingMode === 'brush' && this.brushPaths.length > 0) {
        await this.saveBrushArea()
        return
      }

      if (this.diyAreas.length === 0) {
        this.$message.warning('当前面没有DIY区域需要保存')
        return
      }

      this.saveLoading = true

      // 统计需要保存的区域
      const unsavedAreas = this.diyAreas.filter(area => !area.id)
      const updatedAreas = this.diyAreas.filter(area => area.id && area.needsUpdate)

      const savePromises = []

      // 保存新区域
      for (const area of unsavedAreas) {
        // 验证pathData不为空
        if (!area.pathData || area.pathData.trim() === '') {
          console.error('区域pathData为空，跳过保存:', area)
          this.$message.error(`区域"${area.name}"的路径数据为空，无法保存`)
          continue
        }

        // 从canvas对象重新获取bounds，确保数据准确
        const canvasObj = this.canvas.getObjects().find(obj => obj.areaIndex === this.diyAreas.indexOf(area))

        // 生成并上传蒙版图片
        let maskImageUrl = null

        if (canvasObj) {
          // 计算从画布坐标到原图坐标的缩放比例
          const scaleToOriginal = this.originalImageWidth > 0
            ? this.originalImageWidth / this.largeImageWidth
            : 1

          const bounds = canvasObj.getBoundingRect()

          // 转换为原图坐标
          area.x = Math.round(bounds.left * scaleToOriginal)
          area.y = Math.round(bounds.top * scaleToOriginal)
          area.width = Math.round(bounds.width * scaleToOriginal)
          area.height = Math.round(bounds.height * scaleToOriginal)
          area.bounds = `${area.x},${area.y},${area.width},${area.height}`

          console.log('🔄 从canvas对象重新计算bounds(转换为原图坐标):', {
            区域名称: area.name,
            画布bounds: bounds,
            原图bounds: area.bounds,
            缩放比例: scaleToOriginal,
            pathData: area.pathData
          })

          // 生成并上传蒙版图片
          try {
            console.log('🎨 开始生成蒙版图片...')
            const maskDataURL = this.exportMaskFromShape(canvasObj)
            maskImageUrl = await this.uploadMaskToOSS(maskDataURL)
            console.log('✅ 蒙版图片上传成功:', maskImageUrl)
          } catch (error) {
            console.error('蒙版图片生成/上传失败:', error)
            this.$message.warning(`区域"${area.name}"的蒙版图片上传失败，将继续保存区域数据`)
          }
        } else {
          console.warn('⚠️ 未找到对应的canvas对象，无法生成蒙版图片')
        }

        // 验证区域尺寸不超过限制(1000px)
        const MAX_SIZE = 1000
        if (area.width > MAX_SIZE || area.height > MAX_SIZE) {
          console.error('区域尺寸超出限制:', { name: area.name, width: area.width, height: area.height })
          this.$message.error(`区域"${area.name}"的尺寸超出限制(${area.width}x${area.height}px)，最大允许 ${MAX_SIZE}px`)
          continue
        }

        const areaData = {
          surfaceId: this.surface.id,
          name: area.name,
          x: area.x,
          y: area.y,
          width: area.width,
          height: area.height,
          bounds: area.bounds,
          pathData: area.pathData,
          maskImageUrl: maskImageUrl // 添加蒙版URL
        }

        console.log('准备保存区域:', areaData)

        savePromises.push(
          createArea(areaData).then(response => {
            area.id = response.data.id
            area.maskImageUrl = maskImageUrl
            area.needsUpdate = false
            return { type: 'create', area: area }
          }).catch(error => {
            console.error('保存区域失败:', error)
            throw error
          })
        )
      }

      // 更新已有区域
      for (const area of updatedAreas) {
        // 验证pathData不为空
        if (!area.pathData || area.pathData.trim() === '') {
          console.error('区域pathData为空，跳过更新:', area)
          this.$message.error(`区域"${area.name}"的路径数据为空，无法更新`)
          continue
        }

        // 从canvas对象重新获取bounds，确保数据准确
        const canvasObj = this.canvas.getObjects().find(obj => obj.areaId === area.id)
        if (canvasObj) {
          // 计算从画布坐标到原图坐标的缩放比例
          const scaleToOriginal = this.originalImageWidth > 0
            ? this.originalImageWidth / this.largeImageWidth
            : 1

          const bounds = canvasObj.getBoundingRect()

          // 转换为原图坐标
          const oldBounds = area.bounds
          area.x = Math.round(bounds.left * scaleToOriginal)
          area.y = Math.round(bounds.top * scaleToOriginal)
          area.width = Math.round(bounds.width * scaleToOriginal)
          area.height = Math.round(bounds.height * scaleToOriginal)
          area.bounds = `${area.x},${area.y},${area.width},${area.height}`

          console.log('🔄 从canvas对象重新计算bounds(转换为原图坐标):', {
            区域名称: area.name,
            画布bounds: bounds,
            旧原图bounds: oldBounds,
            新原图bounds: area.bounds,
            缩放比例: scaleToOriginal
          })

          // 重新生成并上传蒙版图片
          try {
            console.log('🎨 重新生成蒙版图片...')
            const maskDataURL = this.exportMaskFromShape(canvasObj)
            const maskImageUrl = await this.uploadMaskToOSS(maskDataURL)
            area.maskImageUrl = maskImageUrl
            console.log('✅ 蒙版图片重新上传成功:', maskImageUrl)
          } catch (error) {
            console.error('蒙版图片生成/上传失败:', error)
            this.$message.warning(`区域"${area.name}"的蒙版图片上传失败，将继续更新区域数据`)
          }
        }

        // 验证区域尺寸不超过限制(1000px)
        const MAX_SIZE = 1000
        if (area.width > MAX_SIZE || area.height > MAX_SIZE) {
          console.error('区域尺寸超出限制:', { name: area.name, width: area.width, height: area.height })
          this.$message.error(`区域"${area.name}"的尺寸超出限制(${area.width}x${area.height}px)，最大允许 ${MAX_SIZE}px`)
          continue
        }

        const updateData = {
          surfaceId: area.surfaceId,
          name: area.name,
          x: area.x,
          y: area.y,
          width: area.width,
          height: area.height,
          bounds: area.bounds,
          pathData: area.pathData,
          maskImageUrl: area.maskImageUrl // 添加蒙版URL
        }

        console.log('更新区域数据:', updateData)

        savePromises.push(
          updateArea(area.id, updateData).then(() => {
            area.needsUpdate = false
            return { type: 'update', area: area }
          })
        )
      }

      if (savePromises.length === 0) {
        this.saveLoading = false
        this.$message.success('所有区域已是最新状态')
        return
      }

      Promise.all(savePromises).then(results => {
        this.saveLoading = false

        const createCount = results.filter(r => r.type === 'create').length
        const updateCount = results.filter(r => r.type === 'update').length

        let message = '保存成功！'
        if (createCount > 0 && updateCount > 0) {
          message = `保存成功！新增 ${createCount} 个区域，更新 ${updateCount} 个区域`
        } else if (createCount > 0) {
          message = `保存成功！新增 ${createCount} 个区域`
        } else if (updateCount > 0) {
          message = `保存成功！更新 ${updateCount} 个区域`
        }

        this.$message.success(message)
        this.emitChange()
      }).catch(error => {
        this.saveLoading = false
        console.error('保存失败:', error)
        this.$message.error('保存失败，请重试')
      })
    },

    // 弹窗控制方法
    openAreaEditor() {
      this.areaEditorVisible = true
      // 弹窗打开后初始化大画布
      this.$nextTick(() => {
        if (this.$refs.surfaceImageLarge && this.$refs.surfaceImageLarge.complete) {
          this.onImageLoadLarge()
        } else {
          // 如果图片还没加载完成，等待图片加载
          this.$refs.surfaceImageLarge.onload = () => {
            this.onImageLoadLarge()
          }
        }
      })
    },

    closeAreaEditor() {
      this.areaEditorVisible = false
      // 销毁大画布，恢复小画布
      if (this.canvas) {
        this.canvas.dispose()
        this.canvas = null
      }

      // 重新初始化小画布
      this.$nextTick(() => {
        if (this.$refs.surfaceImage && this.$refs.surfaceImage.complete) {
          this.onImageLoad()
        }
      })
    },

    saveAndCloseEditor() {
      // 先保存，再关闭
      this.saveCurrentSurface()

      // 延迟关闭，等待保存完成
      setTimeout(() => {
        this.closeAreaEditor()
      }, 1000)
    },

    emitChange() {
      this.$emit('areas-change', this.diyAreas)
    },

    // 视觉反馈和提示方法
    showOperationFeedback(message, type = 'info') {
      this.$message({
        message: message,
        type: type,
        duration: 1500,
        showClose: false
      })
    },

    showCoordinateTooltip(obj) {
      const x = Math.round(obj.left || 0)
      const y = Math.round(obj.top || 0)
      this.showTooltip(`位置: (${x}, ${y})`, obj)
    },

    showSizeTooltip(obj) {
      const width = Math.round(obj.width * (obj.scaleX || 1))
      const height = Math.round(obj.height * (obj.scaleY || 1))
      this.showTooltip(`尺寸: ${width} × ${height}`, obj)
    },

    showAngleTooltip(obj) {
      const angle = Math.round(obj.angle || 0)
      this.showTooltip(`角度: ${angle}°`, obj)
    },

    showAreaTooltip(obj) {
      const areaName = obj.areaName || `区域${(obj.areaIndex || 0) + 1}`
      const x = Math.round(obj.left || 0)
      const y = Math.round(obj.top || 0)
      const width = Math.round(obj.width * (obj.scaleX || 1))
      const height = Math.round(obj.height * (obj.scaleY || 1))

      this.showTooltip(`${areaName}\n位置: (${x}, ${y})\n尺寸: ${width} × ${height}`, obj)
    },

    showTooltip(text, obj) {
      // 创建或更新提示框
      if (!this.tooltipElement) {
        this.tooltipElement = document.createElement('div')
        this.tooltipElement.className = 'area-tooltip'
        document.body.appendChild(this.tooltipElement)
      }

      this.tooltipElement.innerHTML = text.replace(/\n/g, '<br>')
      this.tooltipElement.style.display = 'block'

      // 计算位置
      if (obj && this.canvas) {
        const canvasRect = this.canvas.getElement().getBoundingClientRect()
        const objCoords = obj.getCenterPoint()

        this.tooltipElement.style.left = (canvasRect.left + objCoords.x + 10) + 'px'
        this.tooltipElement.style.top = (canvasRect.top + objCoords.y - 30) + 'px'
      }
    },

    hideTooltip() {
      if (this.tooltipElement) {
        this.tooltipElement.style.display = 'none'
      }
    },

    // 增强选择样式
    enhanceSelectedObject(obj) {
      if (obj) {
        obj.set({
          stroke: '#e6a23c',
          strokeWidth: 3,
          cornerColor: '#409eff',
          cornerSize: 8,
          transparentCorners: false,
          cornerStyle: 'circle'
        })
        this.canvas.renderAll()
      }
    },

    // 恢复默认样式
    restoreObjectStyle(obj) {
      if (obj) {
        obj.set({
          stroke: '#409eff',
          strokeWidth: 2,
          cornerColor: '#409eff',
          cornerSize: 6,
          transparentCorners: false,
          cornerStyle: 'rect'
        })
        this.canvas.renderAll()
      }
    },

    // 键盘快捷键支持
    setupKeyboardShortcuts() {
      this.keyboardHandler = (e) => {
        // 只在画布获得焦点时响应快捷键
        if (!this.canvas || !document.activeElement ||
            !document.activeElement.closest('.canvas-container')) {
          return
        }

        switch (e.key) {
          case 'Delete':
          case 'Backspace':
            if (this.selectedObject) {
              this.deleteSelected()
              e.preventDefault()
            }
            break
          case 'Escape':
            this.canvas.discardActiveObject()
            this.canvas.renderAll()
            this.setDrawingMode('select')
            e.preventDefault()
            break
          case 'c':
            if (e.ctrlKey || e.metaKey) {
              this.copySelected()
              e.preventDefault()
            }
            break
          case 'v':
            if (e.ctrlKey || e.metaKey) {
              this.pasteObject()
              e.preventDefault()
            }
            break
          case 'z':
            if (e.ctrlKey || e.metaKey) {
              if (e.shiftKey) {
                this.redo()
              } else {
                this.undo()
              }
              e.preventDefault()
            }
            break
          case 's':
            if (e.ctrlKey || e.metaKey) {
              this.saveCurrentSurface()
              e.preventDefault()
            }
            break
        }
      }

      document.addEventListener('keydown', this.keyboardHandler)
    },

    removeKeyboardShortcuts() {
      if (this.keyboardHandler) {
        document.removeEventListener('keydown', this.keyboardHandler)
      }
    },

    // 复制选中对象
    copySelected() {
      if (this.selectedObject) {
        this.copiedObject = this.selectedObject.toObject()
        this.showOperationFeedback('已复制区域', 'success')
      }
    },

    // 粘贴对象
    pasteObject() {
      if (this.copiedObject && !this.hasMaxAreas) {
        const obj = fabric.util.object.clone(this.copiedObject)
        obj.left += 20
        obj.top += 20

        fabric.util.object.enlivenObjects([obj], (objects) => {
          const pastedObj = objects[0]
          this.canvas.add(pastedObj)
          this.addShapeToAreas(pastedObj)
          this.canvas.setActiveObject(pastedObj)
          this.canvas.renderAll()

          this.showOperationFeedback('已粘贴区域', 'success')
        })
      } else if (this.hasMaxAreas) {
        this.showOperationFeedback('每个模板面只允许一个区域', 'warning')
      }
    },

    // 撤销操作（简单实现）
    undo() {
      // 这里可以实现更复杂的撤销逻辑
      this.showOperationFeedback('撤销功能开发中', 'info')
    },

    // 重做操作（简单实现）
    redo() {
      // 这里可以实现更复杂的重做逻辑
      this.showOperationFeedback('重做功能开发中', 'info')
    },

    // ========== 画笔模式相关方法 ==========

    /**
     * 保存画笔区域
     */
    async saveBrushArea() {
      if (!this.brushPaths || this.brushPaths.length === 0) {
        this.$message.warning('请先用画笔涂抹需要DIY的区域')
        return
      }

      this.saveLoading = true

      try {
        // 1. 导出蒙版图片
        console.log('🎨 开始导出蒙版图片...')
        const maskDataURL = this.exportMaskImage()

        // 2. 上传蒙版到OSS
        console.log('📤 开始上传蒙版图片...')
        const maskUrl = await this.uploadMaskToOSS(maskDataURL)
        console.log('✅ 蒙版上传成功:', maskUrl)

        // 3. 计算边界框
        const bounds = this.calculateBrushBounds()

        // 4. 保存区域数据
        const areaData = {
          surfaceId: this.surface.id,
          name: `画笔区域${this.diyAreas.length + 1}`,
          bounds: `${bounds.x},${bounds.y},${bounds.width},${bounds.height}`,
          pathData: '', // 画笔模式传空字符串
          maskImageUrl: maskUrl // 蒙版URL
        }

        console.log('💾 保存区域数据:', areaData)

        const response = await createArea(areaData)

        // 保存成功后,添加完整的区域数据到列表
        const savedArea = {
          id: response.data.id,
          surfaceId: this.surface.id,
          name: areaData.name,
          x: bounds.x,
          y: bounds.y,
          width: bounds.width,
          height: bounds.height,
          bounds: areaData.bounds,
          pathData: areaData.pathData,
          maskImageUrl: maskUrl
        }
        this.diyAreas.push(savedArea)

        this.$message.success('画笔区域保存成功')

        // 5. 清空画笔路径并切换到选择模式
        this.brushPaths = []
        this.setDrawingMode('select')
        this.emitChange()

      } catch (error) {
        console.error('保存画笔区域失败:', error)
        this.$message.error('保存失败: ' + (error.message || '未知错误'))
      } finally {
        this.saveLoading = false
      }
    },

    /**
     * 从形状对象导出蒙版图片(矩形/圆形)
     * 返回黑白蒙版图片的DataURL
     */
    exportMaskFromShape(shapeObj) {
      // 创建临时画布，使用原图尺寸
      const tempCanvas = document.createElement('canvas')
      tempCanvas.width = this.originalImageWidth
      tempCanvas.height = this.originalImageHeight
      const ctx = tempCanvas.getContext('2d')

      // 填充白色背景
      ctx.fillStyle = 'white'
      ctx.fillRect(0, 0, tempCanvas.width, tempCanvas.height)

      // 计算从画布坐标到原图坐标的缩放比例
      const scaleToOriginal = this.originalImageWidth > 0 && this.largeImageWidth > 0
        ? this.originalImageWidth / this.largeImageWidth
        : 1

      console.log('🎨 生成蒙版图片:', {
        原图尺寸: `${this.originalImageWidth} x ${this.originalImageHeight}`,
        画布尺寸: `${this.largeImageWidth} x ${this.largeImageHeight}`,
        缩放比例: scaleToOriginal,
        形状类型: shapeObj.type
      })

      // 绘制形状为黑色
      ctx.fillStyle = 'black'
      ctx.save()

      if (shapeObj.type === 'rect') {
        // 矩形 - 转换为原图坐标
        const left = shapeObj.left * scaleToOriginal
        const top = shapeObj.top * scaleToOriginal
        const width = shapeObj.width * shapeObj.scaleX * scaleToOriginal
        const height = shapeObj.height * shapeObj.scaleY * scaleToOriginal

        console.log('矩形蒙版:', { left, top, width, height })
        ctx.fillRect(left, top, width, height)
      } else if (shapeObj.type === 'circle') {
        // 圆形 - 转换为原图坐标
        const centerX = (shapeObj.left + (shapeObj.radius * shapeObj.scaleX)) * scaleToOriginal
        const centerY = (shapeObj.top + (shapeObj.radius * shapeObj.scaleY)) * scaleToOriginal
        const radius = shapeObj.radius * shapeObj.scaleX * scaleToOriginal

        console.log('圆形蒙版:', { centerX, centerY, radius })
        ctx.beginPath()
        ctx.arc(centerX, centerY, radius, 0, 2 * Math.PI)
        ctx.fill()
      }

      ctx.restore()

      // 导出为DataURL
      return tempCanvas.toDataURL('image/png')
    },

    /**
     * 导出蒙版图片(画笔模式)
     * 返回黑白蒙版图片的DataURL
     */
    exportMaskImage() {
      // 创建临时画布，使用原图尺寸
      const tempCanvas = document.createElement('canvas')
      tempCanvas.width = this.originalImageWidth
      tempCanvas.height = this.originalImageHeight
      const ctx = tempCanvas.getContext('2d')

      // 填充白色背景
      ctx.fillStyle = 'white'
      ctx.fillRect(0, 0, tempCanvas.width, tempCanvas.height)

      // 计算从画布坐标到原图坐标的缩放比例
      const scaleToOriginal = this.originalImageWidth > 0 && this.largeImageWidth > 0
        ? this.originalImageWidth / this.largeImageWidth
        : 1

      console.log('🎨 生成画笔蒙版图片:', {
        原图尺寸: `${this.originalImageWidth} x ${this.originalImageHeight}`,
        画布尺寸: `${this.largeImageWidth} x ${this.largeImageHeight}`,
        缩放比例: scaleToOriginal,
        画笔路径数量: this.brushPaths.length
      })

      // 绘制画笔路径为黑色
      ctx.strokeStyle = 'black'
      ctx.lineCap = 'round'
      ctx.lineJoin = 'round'

      this.brushPaths.forEach(path => {
        const pathData = path.path
        if (!pathData || pathData.length === 0) return

        ctx.beginPath()
        ctx.lineWidth = (path.strokeWidth || this.brushWidth) * scaleToOriginal

        pathData.forEach((point) => {
          const command = point[0]
          if (command === 'M') {
            ctx.moveTo(point[1] * scaleToOriginal, point[2] * scaleToOriginal)
          } else if (command === 'L') {
            ctx.lineTo(point[1] * scaleToOriginal, point[2] * scaleToOriginal)
          } else if (command === 'Q') {
            ctx.quadraticCurveTo(
              point[1] * scaleToOriginal,
              point[2] * scaleToOriginal,
              point[3] * scaleToOriginal,
              point[4] * scaleToOriginal
            )
          }
        })

        ctx.stroke()
      })

      // 导出为DataURL
      return tempCanvas.toDataURL('image/png')
    },

    /**
     * 上传蒙版到OSS
     */
    async uploadMaskToOSS(dataURL) {
      // 将DataURL转换为Blob
      const blob = await fetch(dataURL).then(r => r.blob())

      // 创建FormData
      const formData = new FormData()
      formData.append('file', blob, 'mask.png')

      // 调用上传接口
      const response = await upload(formData)

      if (response.code === 200) {
        return response.data
      } else {
        throw new Error(response.message || '上传失败')
      }
    },

    /**
     * 计算画笔路径的边界框
     */
    calculateBrushBounds() {
      let minX = Infinity
      let minY = Infinity
      let maxX = -Infinity
      let maxY = -Infinity

      // 找出最大的画笔宽度
      let maxStrokeWidth = this.brushWidth
      this.brushPaths.forEach(path => {
        const strokeWidth = path.strokeWidth || this.brushWidth
        maxStrokeWidth = Math.max(maxStrokeWidth, strokeWidth)
      })

      console.log('🎨 计算画笔边界框 - 最大画笔宽度:', maxStrokeWidth)

      this.brushPaths.forEach(path => {
        const pathData = path.path
        if (!pathData || pathData.length === 0) return

        pathData.forEach(point => {
          const command = point[0]
          if (command === 'M' || command === 'L') {
            const x = point[1]
            const y = point[2]
            minX = Math.min(minX, x)
            minY = Math.min(minY, y)
            maxX = Math.max(maxX, x)
            maxY = Math.max(maxY, y)
          } else if (command === 'Q') {
            const x1 = point[1]
            const y1 = point[2]
            const x2 = point[3]
            const y2 = point[4]
            minX = Math.min(minX, x1, x2)
            minY = Math.min(minY, y1, y2)
            maxX = Math.max(maxX, x1, x2)
            maxY = Math.max(maxY, y1, y2)
          }
        })
      })

      // 扩展边界框以包含画笔宽度
      // 画笔是圆形的，所以需要在四个方向都扩展 strokeWidth/2
      const halfStroke = maxStrokeWidth / 2
      minX -= halfStroke
      minY -= halfStroke
      maxX += halfStroke
      maxY += halfStroke

      console.log('📐 画布坐标边界框 (扩展前):', {
        minX: minX + halfStroke,
        minY: minY + halfStroke,
        maxX: maxX - halfStroke,
        maxY: maxY - halfStroke
      })
      console.log('📐 画布坐标边界框 (扩展后):', { minX, minY, maxX, maxY })

      // 计算从画布坐标到原图坐标的缩放比例
      const scaleToOriginal = this.originalImageWidth > 0
        ? this.originalImageWidth / this.largeImageWidth
        : 1

      // 转换为原图坐标
      const bounds = {
        x: Math.round(minX * scaleToOriginal),
        y: Math.round(minY * scaleToOriginal),
        width: Math.round((maxX - minX) * scaleToOriginal),
        height: Math.round((maxY - minY) * scaleToOriginal)
      }

      console.log('✅ 原图坐标边界框:', bounds)
      console.log('📏 扩展量 (原图坐标):', Math.round(halfStroke * scaleToOriginal))

      return bounds
    }
  }
}
</script>

<style scoped>
.area-drawing-tool {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background: #fff;
}

.toolbar {
  padding: 12px;
  border-bottom: 1px solid #ebeef5;
  background: #fafafa;
}

.drawing-tools {
  margin-bottom: 10px;
}

.area-list {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.area-list .el-tag {
  cursor: pointer;
  margin-right: 5px;
}

.empty-text {
  color: #909399;
  font-size: 14px;
}

.canvas-container {
  position: relative;
  min-height: 500px;
  background: #f5f7fa;
  padding: 20px;
  overflow: auto;
}

.canvas-wrapper {
  position: relative;
  display: inline-block;
  border: 1px solid #ddd;
  background: #fff;
}

.no-image {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400px;
  color: #909399;
}

.no-image i {
  font-size: 48px;
  margin-bottom: 16px;
}

.properties-panel {
  position: fixed;
  top: 50%;
  right: 20px;
  transform: translateY(-50%);
  width: 280px;
  max-height: 80vh;
  overflow-y: auto;
  z-index: 1000;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.properties-panel .el-card {
  margin: 0;
}

.properties-panel .el-form-item {
  margin-bottom: 15px;
}

.properties-panel .el-form-item__label {
  font-size: 12px;
  line-height: 1.2;
}

.properties-panel .el-input-number {
  width: 100%;
}

/* 工具按钮样式 */
.el-button-group .el-button {
  padding: 8px 12px;
  font-size: 12px;
}

.el-button-group .el-button.el-button--primary {
  background-color: #409eff;
  border-color: #409eff;
}

/* 画布样式 */
canvas {
  cursor: crosshair;
}

canvas.select-mode {
  cursor: default;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .properties-panel {
    position: relative;
    right: auto;
    top: auto;
    transform: none;
    width: 100%;
    margin-top: 20px;
  }
}



/* 区域摘要样式 */
.area-summary {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-top: 1px solid #ebeef5;
  margin-top: 10px;
}

/* 弹窗样式 */
.area-editor-dialog .el-dialog {
  margin-top: 5vh !important;
}

.area-editor-dialog .el-dialog__body {
  padding: 20px;
}

.area-editor-content {
  display: flex;
  gap: 20px;
  height: 70vh;
}

.editor-sidebar {
  width: 300px;
  flex-shrink: 0;
}

.editor-canvas {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.tools-card .el-card__body,
.areas-card .el-card__body {
  padding: 15px;
}

.drawing-tools .el-button-group {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.drawing-tools .el-button {
  margin: 0;
}

.areas-list {
  max-height: 400px;
  overflow-y: auto;
}

.area-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.area-item:hover {
  border-color: #409eff;
  background-color: #f5f7fa;
}

.area-item.active {
  border-color: #409eff;
  background-color: #ecf5ff;
}

.area-info {
  flex: 1;
}

.area-name {
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.area-details {
  font-size: 12px;
  color: #909399;
}

.area-details span {
  display: block;
  margin-bottom: 2px;
}

.area-actions {
  display: flex;
  gap: 5px;
}

.empty-areas {
  text-align: center;
  padding: 40px 20px;
  color: #909399;
}

.empty-areas i {
  font-size: 48px;
  margin-bottom: 10px;
  display: block;
}

.empty-areas p {
  margin: 0;
  font-size: 14px;
}

.canvas-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.canvas-header h4 {
  margin: 0;
  color: #303133;
}

.canvas-info {
  font-size: 12px;
  color: #909399;
}

.canvas-container-large {
  flex: 1;
  position: relative;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: auto;
  background: #f5f7fa;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 20px;
}

.canvas-wrapper-large {
  position: relative;
  display: inline-block;
  margin: auto;
}

.no-image-large {
  text-align: center;
  color: #909399;
}

.no-image-large i {
  font-size: 64px;
  margin-bottom: 15px;
  display: block;
}

.no-image-large p {
  margin: 0;
  font-size: 16px;
}

/* 交互增强样式 */
.area-tooltip {
  position: fixed;
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 8px 12px;
  border-radius: 4px;
  font-size: 12px;
  line-height: 1.4;
  z-index: 9999;
  pointer-events: none;
  white-space: nowrap;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  display: none;
}

/* 画布交互状态 */
.canvas-container canvas {
  transition: cursor 0.2s ease;
}

.canvas-container canvas:hover {
  cursor: crosshair;
}

.canvas-container canvas.selecting {
  cursor: default;
}

.canvas-container canvas.moving {
  cursor: move;
}

.canvas-container canvas.resizing {
  cursor: nw-resize;
}

/* 区域项交互增强 */
.area-item {
  transition: all 0.3s ease;
}

.area-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

.area-item.active {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.3);
}

/* 工具按钮增强 */
.drawing-tools .el-button {
  transition: all 0.3s ease;
}

.drawing-tools .el-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.drawing-tools .el-button.is-active {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

/* 操作反馈动画 */
@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.05); }
  100% { transform: scale(1); }
}

.operation-feedback {
  animation: pulse 0.3s ease-in-out;
}

/* 拖拽状态指示 */
.dragging {
  opacity: 0.8;
  transform: scale(1.02);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}

/* 缩放状态指示 */
.scaling {
  opacity: 0.9;
  outline: 2px dashed #409eff;
  outline-offset: 4px;
}

/* 旋转状态指示 */
.rotating {
  opacity: 0.9;
  outline: 2px dotted #e6a23c;
  outline-offset: 4px;
}
</style>
