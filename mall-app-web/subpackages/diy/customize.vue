<template>
  <view class="diy-customize-container">
    <!-- 加载状态 -->
    <view v-if="isLoading" class="loading-container">
      <view class="loading-content">
        <image class="loading-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/loading.gif" mode="aspectFit" />
        <text class="loading-text">正在加载定制数据...</text>
      </view>
    </view>

    <!-- 主内容区域 -->
    <view v-else class="main-content">
      <!-- 自定义导航栏 -->
      <nav-bar
        v-if="selectedFaces && selectedFaces.length > 0"
        :back="true"
        :placeholder="true"
        :has-slot="true"
      >
        <!-- 定制面切换Tab -->
        <view class="face-tabs">
          <scroll-view class="tabs-scroll" scroll-x="true" show-scrollbar="false">
            <view class="tabs-container">
              <view
                v-for="(face, index) in selectedFaces"
                :key="index"
                :class="['tab-item', currentFaceIndex === index ? 'active' : '']"
                @tap="switchFace(index)"
                v-if="face"
              >
                <text class="tab-name">{{ face.name || '未命名' }}</text>
              </view>
            </view>
          </scroll-view>
        </view>
      </nav-bar>

    <!-- 步骤进度条 -->
    <view class="step-progress">
      <view class="step-container">
        <view
          v-for="(step, index) in steps"
          :key="index"
          :class="['step-item', currentFaceStep === index ? 'active' : '', index < currentFaceStep ? 'completed' : '']"
        >
          <!-- 步骤数字标签 -->
          <view class="step-number">
            <text>{{ index + 1 }}</text>
          </view>
          <!-- 步骤图标 -->
          <view class="step-icon">
            <image
              class="step-image"
              :src="getStepIcon(index, currentFaceStep === index || index < currentFaceStep)"
              mode="aspectFit"
            />
          </view>
          <!-- 步骤名称 -->
          <text class="step-name">{{ step.name }}</text>
        </view>

        <!-- 连接线 -->
        <view class="step-lines">
          <view
            v-for="index in steps.length - 1"
            :key="index"
            :class="['step-line', index - 1 < currentFaceStep ? 'completed' : '']"
          ></view>
        </view>
      </view>
    </view>

    <!-- 提示信息 -->
    <view class="tip-info">
      <image class="tip-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/bell.png" mode="aspectFit" />
      <text class="tip-text">原始图片模板可修改部分元素位置大小，快试试吧~</text>
    </view>

    <!-- 画布区域 -->
    <view class="canvas-container" :class="{ 'modal-active': hasActiveModal }">
      <!-- Canvas编辑器 -->
      <!-- #ifdef MP-WEIXIN -->
      <!-- 微信小程序：使用v-if隐藏Canvas，通过key强制重新渲染并恢复状态 -->
      <simple-canvas-editor
        v-if="!hasActiveModal && currentFaceStep !== 3"
        ref="canvasEditor"
        :key="canvasKey"
        :width="canvasWidth"
        :height="canvasHeight"
        :background-image="currentFaceImage"
        :elements="currentFaceElements"
        :current-face-id="currentFaceId"
        :is-locked="isCanvasLocked"
        @element-added="handleElementAdded"
        @element-deleted="handleElementDeleted"
        @history-changed="handleHistoryChanged"
        @elements-updated="handleElementsUpdated"
        @canvas-resized="handleCanvasResized"
        @edit-text="handleEditText"
      />
      <!-- #endif -->

      <!-- #ifndef MP-WEIXIN -->
      <!-- 其他平台：使用v-show（CSS可以控制） -->
      <simple-canvas-editor
        v-show="!hasActiveModal && currentFaceStep !== 3"
        ref="canvasEditor"
        :width="canvasWidth"
        :height="canvasHeight"
        :background-image="currentFaceImage"
        :elements="currentFaceElements"
        :current-face-id="currentFaceId"
        :is-locked="isCanvasLocked"
        @element-added="handleElementAdded"
        @element-deleted="handleElementDeleted"
        @history-changed="handleHistoryChanged"
        @elements-updated="handleElementsUpdated"
        @canvas-resized="handleCanvasResized"
        @edit-text="handleEditText"
      />
      <!-- #endif -->

      <!-- 第四步：图片剪裁组件 -->
      <view v-if="currentFaceStep === 3 && !hasActiveModal" class="crop-editor-container">
        <qf-image-cropper
          ref="imageCropper"
          :src="currentFaceImage"
          :width="cropperCutWidth"
          :height="cropperCutHeight"
          :widthPercent="cropperWidthPercent"
          :heightPercent="cropperHeightPercent"
          :imageWidthPercent="cropperImageWidthPercent"
          :imageHeightPercent="cropperImageHeightPercent"
          :radius="cropperRadius"
          :showBorder="true"
          :showGrid="true"
          :showAngle="false"
          :showBottomBar="false"
          :bounce="true"
          :rotatable="false"
          :choosable="false"
          :checkRange="true"
          :minScale="0.5"
          :maxScale="3"
          @crop="handleCropComplete"
        >
          <!-- 第四步的底部按钮（放在插件的 slot 中，避免被插件遮挡） -->
          <view class="cropper-bottom-buttons">
            <view class="nav-btn prev-btn" @tap="debouncedPrevStep">
              <text class="nav-btn-text">上一步</text>
            </view>
            <view class="nav-btn next-btn" @tap="debouncedHandleStep4NextAction">
              <text class="nav-btn-text">{{ getStep4NextButtonText() }}</text>
            </view>
          </view>
        </qf-image-cropper>
      </view>

      <!-- 弹窗显示时的占位提示（仅微信小程序） -->
      <!-- #ifdef MP-WEIXIN -->
      <view
        v-if="hasActiveModal"
        class="canvas-placeholder"
        :style="{
          width: canvasWidth + 'px',
          height: canvasHeight + 'px'
        }"
      >
        <text class="placeholder-text">设计预览已隐藏</text>
        <text class="placeholder-desc">关闭弹窗后恢复显示</text>
      </view>
      <!-- #endif -->

      <!-- 切换面时的loading遮罩 -->
      <view v-if="isSwitching" class="switching-overlay">
        <view class="switching-content">
          <view class="loading-spinner"></view>
          <text class="switching-text">切换中...</text>
        </view>
      </view>


    </view>

    <!-- 操作工具栏 -->
    <view class="operation-toolbar">
      <view class="toolbar-left">
        <view class="toolbar-btn" @tap="handleUndo" :class="{ disabled: !canUndo }">
          <image class="toolbar-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/undo.png" mode="aspectFit" />
        </view>
        <view class="toolbar-btn" @tap="handleRedo" :class="{ disabled: !canRedo }">
          <image class="toolbar-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/redo.png" mode="aspectFit" />
        </view>
      </view>
      <view class="toolbar-right">
        <view class="toolbar-btn" @tap="toggleEditFace">
          <image class="toolbar-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/switch_face.png" mode="aspectFit" />
        </view>
      </view>
    </view>

    <!-- 操作按钮区域 -->
    <view class="action-buttons-container">
      <!-- 替换素材/添加素材按钮（仅在第1步和第3步显示） -->
      <view v-if="currentFaceStep === 0 || currentFaceStep === 2" class="action-btn material-btn" @tap="debouncedOpenMaterialModal">
        <text class="btn-text">{{ getMaterialButtonText() }}</text>
      </view>

      <!-- AI风格化/撤销AI生成 动态按钮（仅在AI风格化步骤显示） -->
      <view v-if="currentFaceStep === 1"
            :class="['action-btn', hasAIGeneratedImage ? 'undo-btn' : 'ai-define-btn']"
            @tap="handleAIStyleButtonClick">
        <text class="btn-text">{{ hasAIGeneratedImage ? '撤销AI生成' : getAIStyleButtonText() }}</text>
      </view>

      <!-- 下一步按钮 -->
      <!-- AI风格化步骤的上一步和下一步按钮 -->
      <view v-if="currentFaceStep === 1" class="step-navigation-buttons">
        <view class="nav-btn prev-btn" @tap="debouncedPrevStep">
          <text class="nav-btn-text">上一步</text>
        </view>
        <view class="nav-btn next-btn" @tap="debouncedNextStep">
          <text class="nav-btn-text">下一步</text>
        </view>
      </view>

      <!-- 第一步的下一步按钮（保持原来的全宽样式） -->
      <view v-else-if="currentFaceStep === 0" class="action-btn normal-next-btn" @tap="debouncedNextStep">
        <text class="btn-text">{{ getNextStepButtonText() }}</text>
      </view>

      <!-- 第三步（添加素材）的导航按钮 -->
      <view v-if="currentFaceStep === 2" class="step-navigation-buttons">
        <view class="nav-btn prev-btn" @tap="debouncedPrevStep">
          <text class="nav-btn-text">上一步</text>
        </view>
        <view class="nav-btn next-btn" @tap="debouncedNextStep">
          <text class="nav-btn-text">下一步</text>
        </view>
      </view>

      <!-- 第四步的导航按钮已移到 qf-image-cropper 组件的 slot 中 -->
    </view>

    <!-- 素材选择模态框 -->
    <material-selection-modal
      :visible="materialModalVisible"
      :material-type="currentMaterialType"
      @close="closeMaterialModal"
      @material-selected="handleMaterialSelected"
    />

    <!-- AI风格化弹窗 -->
    <view v-if="showAIStyleModal" class="ai-style-modal" @tap.stop="closeAIStyleModal">
      <!-- 顶部关闭指示器 -->
      <view class="modal-close-indicator"></view>

      <view class="ai-modal-content" @tap.stop>
        <!-- 弹窗头部 -->
        <view class="ai-modal-header">
          <view class="ai-header-icon">
            <image src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/ai-icon.png" class="ai-icon" mode="aspectFit" />
          </view>
          <text class="ai-modal-title">AI定义</text>
        </view>

        <!-- 底层模型标题 -->
        <view class="ai-section-title">
          <text class="section-title-text">底层模型</text>
        </view>

        <!-- 风格选择网格 -->
        <view class="ai-styles-grid">
          <view
            v-for="style in aiStyleData.styles"
            :key="style.id"
            :class="[
              'style-item',
              aiStyleData.selectedStyleId === style.id ? 'selected' : '',
              styleModelId && styleModelId != style.id ? 'disabled' : ''
            ]"
            @tap="selectAIStyle(style.id)"
          >
            <view class="style-image-container">
              <image :src="style.image" class="style-image" mode="aspectFill" />
              <!-- 未选中时的灰色蒙版 -->
              <view v-if="aiStyleData.selectedStyleId != style.id" class="style-unselected-overlay"></view>
              <!-- 选中时的绿色边框（无蒙版，保持图片清晰） -->
              <view v-if="aiStyleData.selectedStyleId == style.id" class="style-selected-border"></view>
            </view>
            <view class="style-name-container">
              <text class="style-name">{{ style.name }}</text>
            </view>
          </view>
        </view>

        <!-- 画面描述 -->
        <view class="ai-prompt-section">
          <text class="prompt-label">画面描述</text>
          <view class="prompt-input-container">
            <textarea
              v-model="aiStyleData.prompt"
              class="prompt-input"
              placeholder="请输入描述"
              maxlength="200"
            />
          </view>
          <view class="prompt-tip">
            <text class="tip-text">提示：详细的描述有助于生成更符合期望的效果</text>
          </view>
        </view>

        <!-- 底部按钮 -->
        <view class="ai-modal-footer">
          <view class="ai-btn ai-cancel-btn" @tap="closeAIStyleModal">
            <text class="ai-btn-text cancel-text">取消</text>
          </view>
          <view class="ai-btn ai-generate-btn" @tap="startAIGeneration">
            <text class="ai-btn-text generate-text">开始生成</text>
          </view>
        </view>
      </view>
    </view>

    <!-- AI生成进度条弹窗 -->
    <view v-if="aiStyleData.isGenerating" class="ai-progress-modal">
      <view class="ai-progress-content">
        <!-- 背景图片 -->
        <image src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/AI_generating_bg.png" class="progress-bg-image" mode="aspectFit" />

        <!-- 生成状态文本 -->
        <view class="progress-text-container">
          <text class="progress-title">AI 生成中...</text>
          <text class="progress-subtitle">请耐心等待</text>
        </view>

        <!-- 进度条 -->
        <view class="progress-bar-container">
          <view class="progress-bar">
            <view
              class="progress-fill"
              :style="{ width: aiStyleData.generationProgress + '%' }"
            ></view>
          </view>
          <text class="progress-percentage">{{ aiStyleData.generationProgress.toFixed(2) }}%</text>
        </view>

        <!-- 取消按钮 -->
        <view class="progress-cancel-btn" @tap="cancelAIGeneration">
          <text class="cancel-btn-text">取消生成</text>
        </view>
      </view>
    </view>

    <!-- 文字编辑弹窗 -->
    <view v-if="showTextEditModal" class="text-edit-modal" @tap="closeTextEditModal">
      <view class="text-edit-content" @tap.stop>
        <view class="text-edit-header">
          <text class="text-edit-title">编辑文字</text>
          <view class="text-edit-close" @tap="closeTextEditModal">
            <text>×</text>
          </view>
        </view>
        
        <!-- 文字输入 -->
        <view class="text-edit-section">
          <text class="section-label">文字内容</text>
          <input 
            class="text-input" 
            v-model="textEditData.text" 
            placeholder="请输入文字"
            maxlength="20"
          />
        </view>
        
        <!-- 颜色选择 -->
        <view class="text-edit-section">
          <text class="section-label">文字颜色</text>
          <view class="color-picker">
            <view 
              v-for="color in textColors" 
              :key="color"
              :class="['color-item', textEditData.color === color ? 'selected' : '']"
              :style="{ backgroundColor: color }"
              @tap="selectTextColor(color)"
            >
              <text v-if="textEditData.color === color" class="color-check">✓</text>
            </view>
          </view>
        </view>
        
        <!-- 底部按钮 -->
        <view class="text-edit-footer">
          <view class="text-edit-btn cancel" @tap="closeTextEditModal">
            <text>取消</text>
          </view>
          <view class="text-edit-btn confirm" @tap="confirmTextEdit">
            <text>确定</text>
          </view>
        </view>
      </view>
    </view>
    </view><!-- 关闭 main-content -->
  </view><!-- 关闭 diy-customize-container -->
</template>

<script>
import NavBar from '@/components/nav-bar.vue';
import MaterialSelectionModal from '../../components/diy/MaterialSelectionModal.vue';
import SimpleCanvasEditor from '../../components/diy/SimpleCanvasEditor.vue';
import QfImageCropper from '@/uni_modules/qf-image-cropper/components/qf-image-cropper/qf-image-cropper.vue';
import { saveDIYDesign, getDIYProductTemplate, uploadDIYImage, aiStylization } from '@/api/diy.js';
import { getProductCustomizableAreas, fetchStyleModelCards, getStyleModelDetail } from '@/api/styleModel.js';
import { fetchProductDetail } from '@/api/product.js';

export default {
  name: 'DIYCustomize',
  components: {
    'nav-bar': NavBar,
    'material-selection-modal': MaterialSelectionModal,
    'simple-canvas-editor': SimpleCanvasEditor,
    'qf-image-cropper': QfImageCropper
  },
  data() {
    return {
      // 基础数据
      productId: '',
      templateId: '',
      styleModelId: '',
      styleModelImage: '', // 风格模型底图
      styleModelFunctionType: 'description_edit', // 风格模型的功能类型
      selectedFaces: [],
      currentFaceIndex: 0,

      // 商品信息
      productInfo: {},

      // 使用的元素列表
      usedElementsList: [],

      // 使用的素材信息
      usedMaterials: [],

      // 画布尺寸（显示用）
      canvasWidth: 303,
      canvasHeight: 348, // 恢复为348px高度，与原始设计保持一致

      // 画布状态控制
      isCanvasLocked: false, // 画布是否锁定（禁用编辑）
      originalCanvasState: null, // 保存AI生成前的原始画布状态

      // Canvas状态保存（解决微信小程序Canvas遮挡问题）
      savedCanvasState: null, // 保存的Canvas状态
      canvasKey: 0, // Canvas组件的key，用于强制重新渲染


      
      // 设计元素
      designElements: {}, // 按面ID存储元素 { faceId: [elements] }
      selectedElementId: null,
      elementIdCounter: 0,
      
      // 步骤控制
      currentStep: 0,
      steps: [
        { name: '替换素材', key: 'replace' },
        { name: 'AI风格化', key: 'ai-style' },
        { name: '添加素材', key: 'add' },
        { name: '图片剪裁', key: 'crop' }
      ],

      // 每个面的步骤状态管理
      faceStepStates: {}, // { faceId: { currentStep: 0, stepData: {...}, completed: [false, false, false, false] } }

      // AI风格化相关数据
      aiStyleData: {
        styles: [], // 风格列表
        selectedStyleId: null, // 选中的风格ID
        prompt: '', // 用户输入的提示词
        fullPrompt: '', // 完整提示词（包含风格描述+用户输入）
        isGenerating: false, // 是否正在生成
        generationProgress: 0, // 生成进度
        generationTaskId: null, // 生成任务ID
        progressTimer: null // 进度查询定时器
      },

      // 弹窗控制
      showAIStyleModal: false, // AI风格化弹窗显示状态
      showDesignCompleteModal: false, // 设计完成确认弹窗显示状态

      // 性能优化相关
      imageCache: new Map(), // 图片缓存
      requestQueue: [], // 请求队列

      // 历史记录状态（从SimpleCanvasEditor同步）
      canvasCanUndo: false,
      canvasCanRedo: false,

      // 底图尺寸（用于蒙版模式的裁剪框计算）
      baseImageWidth: 0,
      baseImageHeight: 0,

      // 其他
      defaultFaceImage: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/default-face.png',
      isLoading: true, // 初始值改为 true,避免数据未加载时渲染

      // 素材模态框
      materialModalVisible: false,

      isSwitching: false, // 是否正在切换面

      // 步骤切换状态锁
      isStepChanging: false,

      // 防抖函数实例
      debouncedNextStep: null,
      debouncedPrevStep: null,
      debouncedOpenMaterialModal: null,
      debouncedOpenAIStyleModal: null,
      debouncedUndoAIGeneration: null,
      debouncedHandleStep4NextAction: null,

      // 文字编辑弹窗相关
      showTextEditModal: false,
      textEditData: {
        text: '',
        color: '#91F104'
      },
      textColors: ['#91F104', '#333333', '#000000', '#FFFFFF', '#FF0000', '#FF6600', '#FFCC00', '#00CC00', '#0066FF', '#9900FF'],
      textEditCallback: null,
    };
  },
  computed: {
    currentFaceElements() {
      // 如果正在加载,返回空数组,避免不必要的计算和日志
      if (this.isLoading) {
        return [];
      }

      // 确保索引在有效范围内
      if (!this.selectedFaces || this.selectedFaces.length === 0 ||
          this.currentFaceIndex < 0 || this.currentFaceIndex >= this.selectedFaces.length) {
        return [];
      }
      const currentFace = this.selectedFaces[this.currentFaceIndex];
      if (!currentFace || !currentFace.id) return [];
      // 确保 designElements 对象存在
      if (!this.designElements) {
        this.designElements = {};
      }
      const elements = this.designElements[currentFace.id] || [];
      console.log('计算当前面素材:', currentFace.id, '素材数量:', elements.length, elements);
      return elements;
    },

    // 当前面的步骤状态
    currentFaceStepState() {
      const currentFace = this.selectedFaces[this.currentFaceIndex];
      if (!currentFace || !currentFace.id) return null;

      // 如果该面还没有步骤状态，初始化一个
      if (!this.faceStepStates[currentFace.id]) {
        this.$set(this.faceStepStates, currentFace.id, {
          currentStep: 0,
          stepData: {
            replace: { completed: false, data: null },
            'ai-style': { completed: false, data: null, generatedImageUrl: null },
            add: { completed: false, data: null },
            crop: { completed: false, data: null, croppedImageUrl: null }
          },
          completed: [false, false, false, false]
        });
      }

      return this.faceStepStates[currentFace.id];
    },

    // 当前面的当前步骤
    currentFaceStep() {
      const stepState = this.currentFaceStepState;
      return stepState ? stepState.currentStep : 0;
    },


    currentFaceId() {
      // 确保索引在有效范围内
      if (!this.selectedFaces || this.selectedFaces.length === 0 ||
          this.currentFaceIndex < 0 || this.currentFaceIndex >= this.selectedFaces.length) {
        return null;
      }
      const currentFace = this.selectedFaces[this.currentFaceIndex];
      return currentFace ? currentFace.id : null;
    },
    canUndo() {
      return this.canvasCanUndo;
    },
    canRedo() {
      return this.canvasCanRedo;
    },

    // 判断当前面是否有AI生成的图片
    hasAIGeneratedImage() {
      const stepState = this.currentFaceStepState;
      return stepState &&
             stepState.stepData &&
             stepState.stepData['ai-style'] &&
             stepState.stepData['ai-style'].generatedImageUrl;
    },

    // 检查是否所有面都已完成设计（包括第四步剪裁）
    allFacesCompleted() {
      if (!this.selectedFaces || this.selectedFaces.length === 0) {
        return false;
      }

      return this.selectedFaces.every(face => {
        const stepState = this.faceStepStates[face.id];
        // 检查第四步（crop）是否完成
        return stepState &&
               stepState.stepData &&
               stepState.stepData.crop &&
               stepState.stepData.crop.completed;
      });
    },

    // 获取下一个未完成的面
    nextUncompletedFace() {
      if (!this.selectedFaces || this.selectedFaces.length === 0) {
        return null;
      }

      for (let i = 0; i < this.selectedFaces.length; i++) {
        const face = this.selectedFaces[i];
        const stepState = this.faceStepStates[face.id];
        const isCompleted = stepState &&
                           stepState.stepData &&
                           stepState.stepData['ai-style'] &&
                           stepState.stepData['ai-style'].completed;

        if (!isCompleted) {
          return { face, index: i };
        }
      }

      return null;
    },
    currentFaceImage() {
      // 如果正在加载,返回空字符串,避免计算错误
      if (this.isLoading) {
        return '';
      }

      console.log('🖼️ 计算当前面图片:', {
        selectedFacesCount: this.selectedFaces?.length || 0,
        currentFaceIndex: this.currentFaceIndex,
        styleModelId: this.styleModelId,
        styleModelImage: this.styleModelImage
      });

      // 获取当前面的步骤状态
      const stepState = this.currentFaceStepState;
      const currentStep = stepState ? stepState.currentStep : 0;

      console.log('📍 当前步骤:', currentStep);

      // 在第二步（AI风格化）时的特殊逻辑
      if (currentStep === 1) {
        // 优先检查是否有AI风格化生成的图片
        if (stepState &&
            stepState.stepData &&
            stepState.stepData['ai-style'] &&
            stepState.stepData['ai-style'].generatedImageUrl) {
          console.log('✅ 第二步：使用AI风格化生成的图片:', stepState.stepData['ai-style'].generatedImageUrl);
          return stepState.stepData['ai-style'].generatedImageUrl;
        }

        // 如果没有AI生成图片，使用合成图片（从第一步生成的）
        if (stepState &&
            stepState.stepData &&
            stepState.stepData['ai-style'] &&
            stepState.stepData['ai-style'].compositeImageUrl) {
          console.log('✅ 第二步：使用合成图片:', stepState.stepData['ai-style'].compositeImageUrl);
          return stepState.stepData['ai-style'].compositeImageUrl;
        }
      }

      // 在第三步（添加素材）时的特殊逻辑
      if (currentStep === 2) {
        // 优先检查是否有AI风格化生成的图片
        if (stepState &&
            stepState.stepData &&
            stepState.stepData['ai-style'] &&
            stepState.stepData['ai-style'].generatedImageUrl) {
          console.log('✅ 第三步：使用AI风格化生成的图片:', stepState.stepData['ai-style'].generatedImageUrl);
          return stepState.stepData['ai-style'].generatedImageUrl;
        }

        // 如果没有AI生成图片，使用合成图片（从第一步生成的）
        if (stepState &&
            stepState.stepData &&
            stepState.stepData['ai-style'] &&
            stepState.stepData['ai-style'].compositeImageUrl) {
          console.log('✅ 第三步：使用合成图片:', stepState.stepData['ai-style'].compositeImageUrl);
          return stepState.stepData['ai-style'].compositeImageUrl;
        }
      }

      // 其他步骤的逻辑
      // 检查当前面是否有背景图片
      const currentFace = this.getCurrentFace();
      if (currentFace && currentFace.backgroundImage) {
        console.log('✅ 使用当前面的背景图片:', currentFace.backgroundImage);
        return currentFace.backgroundImage;
      }

      // 如果有风格模型ID，使用风格模型底图
      if (this.styleModelId && this.styleModelImage) {
        console.log('✅ 使用风格模型底图:', this.styleModelImage);
        return this.styleModelImage;
      }

      // 如果有风格模型ID但还没加载到图片，返回空字符串(不是null)
      if (this.styleModelId && !this.styleModelImage) {
        console.log('⏳ 风格模型底图还未加载,返回空字符串');
        return '';
      }

      // 如果没有风格模型ID，使用默认图片或空字符串
      console.log('⚠️ 没有可用的背景图片,返回空字符串');
      return '';
    },

    // 当前面的定制区域配置
    currentCustomizableArea() {
      // 如果正在加载,返回默认值,避免计算错误
      if (this.isLoading) {
        return { width: 200, height: 200, x: 0, y: 0 };
      }

      console.log('🔍 计算currentCustomizableArea:', {
        selectedFaces: this.selectedFaces,
        selectedFacesLength: this.selectedFaces ? this.selectedFaces.length : 0,
        currentFaceIndex: this.currentFaceIndex
      });

      // 确保 selectedFaces 存在且不为空，并且 currentFaceIndex 在有效范围内
      if (this.selectedFaces && this.selectedFaces.length > 0 &&
          this.currentFaceIndex >= 0 && this.currentFaceIndex < this.selectedFaces.length) {
        const currentFace = this.selectedFaces[this.currentFaceIndex];
        console.log('🎯 当前面数据:', currentFace);

        // 检查当前面是否有定制区域配置
        if (currentFace && currentFace.customizableRegions && currentFace.customizableRegions.length > 0) {
          // 返回第一个定制区域（通常一个面只有一个主要定制区域）
          const area = currentFace.customizableRegions[0];
          console.log('✅ 当前面定制区域配置:', area);
          return area;
        }

        // 兼容旧的数据结构
        if (currentFace && currentFace.regions && currentFace.regions.length > 0) {
          const area = currentFace.regions[0];
          console.log('✅ 当前面定制区域配置(兼容格式):', area);
          return area;
        }

        console.log('⚠️ 当前面没有定制区域配置');
      } else {
        console.log('❌ selectedFaces数据无效或currentFaceIndex超出范围');
      }

      console.log('当前面没有定制区域配置');
      return null;
    },

    // 安全的定制区域配置（确保不为null）
    safeCustomizableArea() {
      const result = this.currentCustomizableArea || {
        width: 200,
        height: 200,
        x: 0,
        y: 0
      };
      console.log('🔍 safeCustomizableArea:', result);
      return result;
    },

    // 裁剪器裁剪区域宽度（根据定制区域尺寸，限制不超过屏幕宽度）
    cropperCutWidth() {
      const area = this.safeCustomizableArea;

      // 如果使用蒙版模式，裁剪框应该是底图的宽度
      if (area.maskImageUrl && this.baseImageWidth > 0) {
        const originalWidth = this.baseImageWidth;

        // 获取屏幕宽度
        const systemInfo = uni.getSystemInfoSync();
        const screenWidth = systemInfo.windowWidth || 375;

        // 设置最大宽度为屏幕宽度的90%
        const maxWidth = screenWidth * 0.9;

        // 如果底图宽度超过最大宽度，进行缩放
        const width = Math.min(originalWidth, maxWidth);

        console.log('📏 裁剪器宽度计算(蒙版模式):', {
          底图宽度: originalWidth,
          屏幕宽度: screenWidth,
          最大宽度: maxWidth,
          最终宽度: width,
          是否缩放: originalWidth > maxWidth
        });

        return width;
      }

      // 传统模式：使用定制区域尺寸
      const originalWidth = area.width || 200;

      // 获取屏幕宽度
      const systemInfo = uni.getSystemInfoSync();
      const screenWidth = systemInfo.windowWidth || 375;

      // 设置最大宽度为屏幕宽度的90%，留出边距
      const maxWidth = screenWidth * 0.9;

      // 如果原始宽度超过最大宽度，进行缩放
      const width = Math.min(originalWidth, maxWidth);

      console.log('📏 裁剪器宽度计算(传统模式):', {
        原始宽度: originalWidth,
        屏幕宽度: screenWidth,
        最大宽度: maxWidth,
        最终宽度: width,
        是否缩放: originalWidth > maxWidth
      });

      return width;
    },

    // 裁剪器裁剪区域高度（根据定制区域尺寸，保持宽高比）
    cropperCutHeight() {
      const area = this.safeCustomizableArea;

      // 如果使用蒙版模式，裁剪框应该是底图的高度
      if (area.maskImageUrl && this.baseImageWidth > 0 && this.baseImageHeight > 0) {
        const originalWidth = this.baseImageWidth;
        const originalHeight = this.baseImageHeight;

        // 获取屏幕宽度
        const systemInfo = uni.getSystemInfoSync();
        const screenWidth = systemInfo.windowWidth || 375;

        // 设置最大宽度为屏幕宽度的90%
        const maxWidth = screenWidth * 0.9;

        // 计算缩放比例
        const scale = Math.min(1, maxWidth / originalWidth);

        // 应用缩放比例到高度，保持宽高比
        const height = originalHeight * scale;

        console.log('📏 裁剪器高度计算(蒙版模式):', {
          底图宽度: originalWidth,
          底图高度: originalHeight,
          缩放比例: scale.toFixed(2),
          最终高度: height,
          宽高比: (originalWidth / originalHeight).toFixed(2)
        });

        return height;
      }

      // 传统模式：使用定制区域尺寸
      const originalWidth = area.width || 200;
      const originalHeight = area.height || 200;

      // 获取屏幕宽度
      const systemInfo = uni.getSystemInfoSync();
      const screenWidth = systemInfo.windowWidth || 375;

      // 设置最大宽度为屏幕宽度的90%
      const maxWidth = screenWidth * 0.9;

      // 计算缩放比例
      const scale = Math.min(1, maxWidth / originalWidth);

      // 应用缩放比例到高度，保持宽高比
      const height = originalHeight * scale;

      console.log('📏 裁剪器高度计算(传统模式):', {
        原始宽度: originalWidth,
        原始高度: originalHeight,
        缩放比例: scale.toFixed(2),
        最终高度: height,
        宽高比: (originalWidth / originalHeight).toFixed(2)
      });

      return height;
    },

    // 裁剪器圆角半径（根据定制区域形状）
    cropperRadius() {
      const area = this.safeCustomizableArea;
      let radius = 0;

      // 如果使用蒙版模式，不设置圆角（蒙版本身已经定义了形状）
      if (area.maskImageUrl) {
        console.log('🎭 蒙版模式，不设置圆角（由蒙版定义形状）');
        return 0;
      }

      // 传统模式：根据定制区域形状设置圆角
      // 如果定制区域是圆形（宽高相等），设置圆角为宽度的一半
      // 注意：插件源码判断条件是 radius >= w / 2
      // 为了确保形成完美圆形，radius 应该等于宽度的一半
      if (area.width === area.height) {
        // 使用宽度的一半作为圆角半径
        radius = area.width / 2;
        console.log('🔵 检测到圆形定制区域');
        console.log('   - 宽度:', area.width, 'px');
        console.log('   - 高度:', area.height, 'px');
        console.log('   - 圆角半径:', radius, 'px');
        console.log('   - 判断条件: radius >= width/2 =>', radius, '>=', area.width / 2, '=', radius >= area.width / 2);
      }
      // 如果有指定圆角半径，使用指定值
      else if (area.borderRadius) {
        radius = area.borderRadius;
        console.log('🔶 使用指定圆角半径:', radius, 'px');
      }
      // 默认不设置圆角（矩形裁剪）
      else {
        console.log('▭ 矩形裁剪，无圆角');
      }

      return radius;
    },

    // 裁剪框显示宽度百分比（控制裁剪框在屏幕上的显示大小）
    // 注意：width/height 属性只控制最终生成图片的尺寸，不控制裁剪框显示大小
    // 裁剪框显示大小由 widthPercent/heightPercent 控制
    cropperWidthPercent() {
      const systemInfo = uni.getSystemInfoSync();
      const screenWidth = systemInfo.windowWidth || 375;
      const cutWidth = this.cropperCutWidth;

      // 计算裁剪框宽度占屏幕宽度的百分比
      const percent = (cutWidth / screenWidth) * 100;

      console.log('📊 裁剪框宽度百分比计算:');
      console.log('   - 屏幕宽度:', screenWidth, 'px');
      console.log('   - 裁剪宽度:', cutWidth, 'px');
      console.log('   - 百分比:', percent.toFixed(2), '%');

      return percent;
    },

    // 裁剪框显示高度百分比
    cropperHeightPercent() {
      const systemInfo = uni.getSystemInfoSync();
      const screenWidth = systemInfo.windowWidth || 375;
      const cutHeight = this.cropperCutHeight;

      // 计算裁剪框高度占屏幕宽度的百分比（注意：是相对于屏幕宽度，不是高度）
      const percent = (cutHeight / screenWidth) * 100;

      console.log('📊 裁剪框高度百分比计算:');
      console.log('   - 屏幕宽度:', screenWidth, 'px');
      console.log('   - 裁剪高度:', cutHeight, 'px');
      console.log('   - 百分比:', percent.toFixed(2), '%');

      return percent;
    },

    // 图片初始显示宽度百分比（控制图片在屏幕上的显示大小，独立于裁剪框）
    cropperImageWidthPercent() {
      // 固定使用80%屏幕宽度显示图片，方便用户查看和操作
      const percent = 80;

      console.log('🖼️ 图片显示宽度百分比:', percent, '%');

      return percent;
    },

    // 图片初始显示高度百分比
    cropperImageHeightPercent() {
      // 固定使用80%屏幕宽度显示图片，方便用户查看和操作
      const percent = 80;

      console.log('🖼️ 图片显示高度百分比:', percent, '%');

      return percent;
    },

    // 检测是否有活跃的弹窗（用于解决iPhone微信小程序canvas遮挡弹窗的问题）
    hasActiveModal() {
      return this.showTextModal ||
             this.showImageModal ||
             this.showShapeModal ||
             this.showStickerModal ||
             this.showFaceSelector ||
             this.showPreview ||
             this.showSaveModal ||
             this.showShareModal ||
             this.showTemplateModal ||
             this.showHistoryModal ||
             this.showLayerModal ||
             this.showSettingsModal ||
             this.showHelpModal ||
             this.showAIStyleModal ||
             this.showTextEditModal ||
             this.aiStyleData.isGenerating ||
             this.materialModalVisible ||
             this.isSwitching;
    },

    // 当前素材类型：第一步显示图片素材(1)，第三步显示全部素材(null)
    currentMaterialType() {
      if (this.currentFaceStep === 0) {
        return 1; // 图片素材
      } else if (this.currentFaceStep === 2) {
        return null; // 显示全部素材（图片和文字）
      }
      return null;
    }
  },
  watch: {
    // 监听弹窗状态变化，保存和恢复Canvas状态
    hasActiveModal: {
      handler(newVal, oldVal) {
        if (newVal !== oldVal) {
          // #ifdef MP-WEIXIN
          if (newVal) {
            // 弹窗显示前，保存Canvas状态
            this.saveCanvasState();
          } else {
            // 弹窗关闭后，恢复Canvas状态
            this.$nextTick(() => {
              this.restoreCanvasState();
            });
          }
          // #endif
        }
      },
      immediate: false
    }
  },
  async onLoad(options) {
    try {
      console.log('📱 页面加载,接收到的参数:', options);

      // 检查登录状态
      const isLoggedIn = await this.checkLoginStatus();
      if (!isLoggedIn) {
        // 未登录，已在 checkLoginStatus 中处理，直接返回
        return;
      }

      // 初始化防抖函数
      this.initDebouncedFunctions();

      // 设置加载状态
      this.isLoading = true;

      // 1. 首先设置基础参数
      this.productId = options.productId;
      this.styleModelId = options.styleModelId || '';

      console.log('✅ 参数设置完成:', {
        productId: this.productId,
        styleModelId: this.styleModelId,
        areaId: options.areaId
      });

      // 2. 处理templateId的获取逻辑
      if (options.templateId) {
        // 情况1：从商品详情页直接传递templateId
        this.templateId = options.templateId;
        console.log('从商品详情页获取templateId:', this.templateId);
      } else if (options.styleModelId) {
        // 情况2：从风格模型流程进入，styleModelId不等于templateId
        // 需要根据商品ID获取实际的templateId
        this.templateId = '';
        console.log('从风格模型流程进入，styleModelId:', this.styleModelId, '需要获取商品的templateId');
      } else {
        // 情况3：其他情况，templateId为空
        this.templateId = '';
        console.log('未传递templateId，需要从商品信息中获取');
      }

      // 3. 并行加载基础数据
      const loadPromises = [];

      // 如果没有templateId，尝试从商品信息中获取
      if (!this.templateId && this.productId) {
        loadPromises.push(this.loadTemplateIdFromProduct());
      }

      // 获取商品详细信息
      if (this.productId) {
        loadPromises.push(this.loadProductInfo());
      }

      // 如果有风格模型ID，加载风格模型详情
      if (this.styleModelId) {
        loadPromises.push(this.loadStyleModelDetail());
      }

      // 等待基础数据加载完成
      await Promise.all(loadPromises);
      console.log('✅ 基础数据加载完成');

      // 4. 加载可定制面数据
      if (options.selectedFaces) {
        this.selectedFaces = JSON.parse(decodeURIComponent(options.selectedFaces));
        console.log('✅ 从URL参数加载selectedFaces:', this.selectedFaces);

        // 确保 currentFaceIndex 在有效范围内
        if (this.currentFaceIndex >= this.selectedFaces.length) {
          this.currentFaceIndex = 0;
        }

        // 初始化设计元素存储
        this.selectedFaces.forEach(face => {
          if (face && face.id) {
            this.$set(this.designElements, face.id, []);
          } else {
            console.warn('无效的面数据:', face);
          }
        });
      } else if (options.areaId) {
        // 从商品选择弹窗跳转过来，需要根据areaId设置选中的定制面
        console.log('📦 从API加载可定制面数据, areaId:', options.areaId);
        await this.loadCustomizableAreas(options.areaId);
      } else {
        // 如果没有传递定制面信息，加载默认的定制面
        console.log('📦 从API加载默认可定制面数据');
        await this.loadCustomizableAreas();
      }

      console.log('✅ 页面初始化完成，最终状态:', {
        productId: this.productId,
        templateId: this.templateId,
        styleModelId: this.styleModelId,
        selectedFacesCount: this.selectedFaces.length,
        currentFaceIndex: this.currentFaceIndex,
        来源: this.styleModelId ? '风格模型流程' : '商品详情页',
        areaId: options.areaId
      });

    } catch (error) {
      console.error('❌ 页面初始化失败:', error);
      uni.showToast({
        title: '页面初始化失败',
        icon: 'none'
      });
    } finally {
      // 确保加载状态被清除
      this.isLoading = false;
    }
  },

  onShow() {
    // 页面显示时的处理
    console.log('DIY定制页面显示');
  },

  onHide() {
    // 页面隐藏时的处理
    console.log('DIY定制页面隐藏');
  },

  onUnload() {
    // 页面卸载时的处理
    console.log('DIY定制页面卸载');
  },
  methods: {
    // 检查登录状态
    async checkLoginStatus() {
      try {
        // 检查本地存储的 tokenInfo
        const tokenInfo = uni.getStorageSync('tokenInfo');
        let hasValidToken = false;

        if (tokenInfo) {
          try {
            const loginData = JSON.parse(tokenInfo);
            hasValidToken = !!(loginData && loginData.token && loginData.tokenHead && loginData.openId);
          } catch (e) {
            console.error('解析tokenInfo失败:', e);
            hasValidToken = false;
          }
        }

        // 如果没有有效的登录信息，提示用户登录
        if (!hasValidToken) {
          return new Promise((resolve) => {
            uni.showModal({
              title: '提示',
              content: '该功能需要登录后使用，是否立即登录？',
              confirmText: '立即登录',
              cancelText: '取消',
              success: (res) => {
                if (res.confirm) {
                  // 用户点击确认，跳转到首页并触发登录弹窗
                  this.$store.commit('setLoginPopup', { show: true, reason: 'unauthorized' });
                  uni.switchTab({
                    url: '/pages/new_index/index',
                    success: () => {
                      // 延迟设置登录弹窗状态，确保首页已加载
                      setTimeout(() => {
                        this.$store.commit('setLoginPopup', { show: true, reason: 'unauthorized' });
                      }, 100);
                    }
                  });
                  resolve(false);
                } else {
                  // 用户点击取消，返回上一页
                  uni.navigateBack({
                    delta: 1
                  });
                  resolve(false);
                }
              }
            });
          });
        }

        console.log('✅ 登录状态检查通过');
        return true;

      } catch (error) {
        console.error('登录状态检查失败:', error);
        return true; // 出错时允许继续，避免阻塞用户
      }
    },

    // 初始化防抖函数
    initDebouncedFunctions() {
      // 为上一步/下一步按钮创建防抖函数，使用短延迟300ms，配合状态锁防止重复点击
      this.debouncedNextStep = this.createDebounce(this.nextStep.bind(this), 300);
      this.debouncedPrevStep = this.createDebounce(this.prevStep.bind(this), 300);

      // 为其他操作按钮创建防抖函数，延迟300ms
      this.debouncedOpenMaterialModal = this.createDebounce(this.openMaterialModal.bind(this), 300);
      this.debouncedOpenAIStyleModal = this.createDebounce(this.openAIStyleModal.bind(this), 300);
      this.debouncedUndoAIGeneration = this.createDebounce(this.undoAIGeneration.bind(this), 300);
      this.debouncedHandleStep4NextAction = this.createDebounce(this.handleStep4NextAction.bind(this), 300);
    },

    // 创建防抖函数（返回一个新的防抖函数实例）
    createDebounce(func, wait) {
      let timeout;
      return function executedFunction(...args) {
        const later = () => {
          clearTimeout(timeout);
          func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
      };
    },

    // 获取下一步按钮文本
    getNextStepButtonText() {
      const currentStep = this.currentFaceStep;
      if (currentStep === 0) {
        return '下一步'; // 替换素材 -> AI风格化
      } else if (currentStep === 1) {
        return '下一步'; // AI风格化 -> 添加素材
      }
      return '下一步';
    },

    // 获取素材按钮文本
    getMaterialButtonText() {
      const currentStep = this.currentFaceStep;
      if (currentStep === 0) {
        return '替换素材'; // 第一步：替换素材
      } else if (currentStep === 2) {
        return '添加素材'; // 第三步：添加素材
      }
      return '替换素材';
    },

    // 获取第三步的下一步按钮文本
    getStep3NextButtonText() {
      return this.allFacesCompleted ? '完成设计' : '继续设计';
    },

    // 处理第三步的下一步操作
    handleStep3NextAction() {
      if (this.allFacesCompleted) {
        // 所有面都已完成，跳转到预览页面
        this.goToPreview();
      } else {
        // 还有未完成的面，切换到下一个未完成的面
        this.continueDesign();
      }
    },

    // 继续设计（切换到下一个未完成的面）
    continueDesign() {
      const nextUncompleted = this.nextUncompletedFace;
      if (nextUncompleted) {
        this.currentFaceIndex = nextUncompleted.index;
        // 切换到第一步（替换素材）
        const stepState = this.faceStepStates[nextUncompleted.face.id];
        if (stepState) {
          stepState.currentStep = 0;
          this.onStepChange();
        }

        uni.showToast({
          title: `切换到${nextUncompleted.face.name}`,
          icon: 'success'
        });
      }
    },

    // 跳转到预览页面
    async goToPreview() {
      try {
        // 显示加载提示
        uni.showLoading({
          title: '正在导出设计...',
          mask: true
        });

        // 收集所有面的设计数据（包括导出画布图片）
        const designData = await this.collectAllDesignData();

        console.log('准备跳转预览页面，设计数据:', designData);

        // 验证必要参数
        if (!this.productId) {
          throw new Error('商品ID不能为空');
        }

        if (!this.templateId) {
          throw new Error('模板ID不能为空');
        }

        if (!designData.faces || designData.faces.length === 0) {
          throw new Error('设计数据不能为空');
        }

        // 先保存设计数据到后端
        const saveResult = await this.saveDesignData(designData);

        // 收集传递给预览页面的额外数据
        const previewParams = this.collectPreviewParams();

        console.log('准备传递的参数:', previewParams);

        // 隐藏加载提示
        uni.hideLoading();

        // 跳转到预览页面，传递完整数据
        // 注意：不要手动编码，uni.navigateTo会自动处理
        uni.navigateTo({
          url: `/subpackages/diy/preview?productId=${this.productId}&templateId=${this.templateId}&designId=${saveResult.designId}&productInfo=${JSON.stringify(previewParams.productInfo)}&styleInfo=${JSON.stringify(previewParams.styleInfo)}&userPrompt=${previewParams.userPrompt}&elementsList=${JSON.stringify(previewParams.elementsList)}`
        });

      } catch (error) {
        console.error('跳转预览页面失败:', error);
        uni.hideLoading();
        uni.showToast({
          title: error.message || '跳转失败',
          icon: 'none'
        });
      }
    },

    // 保存设计数据到后端
    async saveDesignData(designData) {
      try {
        const result = await saveDIYDesign({
          productId: this.productId,
          templateId: this.templateId,
          designData: JSON.stringify(designData) // 将对象序列化为JSON字符串
        });

        if (result && result.code === 200) {
          return { designId: result.data };
        } else {
          throw new Error(result.message || '保存设计数据失败');
        }
      } catch (error) {
        console.error('保存设计数据失败:', error);
        throw error;
      }
    },

    // 收集所有面的设计数据
    async collectAllDesignData() {
      const designData = {
        faces: [],
        productId: this.productId,
        templateId: this.templateId,
        createTime: new Date().toISOString()
      };

      // 遍历所有面，收集设计数据并导出画布图片
      for (const face of this.selectedFaces) {
        const stepState = this.faceStepStates[face.id];
        if (stepState && stepState.stepData) {
          const faceData = {
            faceId: face.id,
            faceName: face.name,
            originalImage: face.image,
            aiGeneratedImageUrl: stepState.stepData['ai-style']?.generatedImageUrl,
            elements: stepState.stepData.add?.elements || [],
            completed: stepState.stepData['ai-style']?.completed || false
          };

          // 导出当前面的画布图片
          try {
            // 优先检查第4步是否有剪裁后的图片
            if (stepState.stepData.crop && stepState.stepData.crop.completed && stepState.stepData.crop.croppedImageUrl) {
              console.log(`面 ${face.name} 使用第4步的剪裁图片:`, stepState.stepData.crop.croppedImageUrl);

              // 上传剪裁后的图片到服务器
              try {
                console.log(`开始上传面 ${face.name} 的第4步剪裁图片到服务器...`);
                const uploadResult = await uploadDIYImage(stepState.stepData.crop.croppedImageUrl);
                if (uploadResult && uploadResult.code === 200) {
                  faceData.canvasImagePath = uploadResult.data;
                  faceData.isClipped = true;
                  console.log(`面 ${face.name} 的第4步剪裁图片上传成功:`, uploadResult.data);
                } else {
                  console.error(`面 ${face.name} 的第4步剪裁图片上传失败:`, uploadResult);
                  // 上传失败时使用临时路径作为备用方案
                  faceData.canvasImagePath = stepState.stepData.crop.croppedImageUrl;
                  faceData.isClipped = true;
                }
              } catch (uploadError) {
                console.error(`面 ${face.name} 的第4步剪裁图片上传异常:`, uploadError);
                // 上传异常时使用临时路径作为备用方案
                faceData.canvasImagePath = stepState.stepData.crop.croppedImageUrl;
                faceData.isClipped = true;
              }
            } else {
              // 获取当前面的可定制区域信息
              const customizableArea = face.customizableRegions && face.customizableRegions.length > 0
                ? face.customizableRegions[0]
                : null;

              // 导出画布图片（如果当前面是正在编辑的面）
              if (this.$refs.canvasEditor && this.currentFaceId === face.id) {
                console.log(`开始导出面 ${face.name} 的画布图片...`);

                // 如果有可定制区域，导出剪裁后的图片
                if (customizableArea && customizableArea.pathData && customizableArea.bounds) {
                console.log(`面 ${face.name} 有可定制区域，导出剪裁后的图片`);
                console.log('剪裁路径:', customizableArea.pathData);
                console.log('剪裁边界:', customizableArea.bounds);

                const clippedImagePath = await this.$refs.canvasEditor.exportClippedCanvasImage(
                  customizableArea.pathData,
                  customizableArea.bounds
                );
                console.log(`面 ${face.name} 的剪裁图片导出成功:`, clippedImagePath);

                // 上传剪裁后的图片到服务器
                try {
                  console.log(`开始上传面 ${face.name} 的剪裁图片到服务器...`);
                  const uploadResult = await uploadDIYImage(clippedImagePath);
                  if (uploadResult && uploadResult.code === 200) {
                    faceData.canvasImagePath = uploadResult.data;
                    faceData.isClipped = true;
                    console.log(`面 ${face.name} 的剪裁图片上传成功:`, uploadResult.data);
                  } else {
                    console.error(`面 ${face.name} 的剪裁图片上传失败:`, uploadResult);
                    // 上传失败时使用临时路径作为备用方案
                    faceData.canvasImagePath = clippedImagePath;
                    faceData.isClipped = true;
                  }
                } catch (uploadError) {
                  console.error(`面 ${face.name} 的剪裁图片上传异常:`, uploadError);
                  // 上传异常时使用临时路径作为备用方案
                  faceData.canvasImagePath = clippedImagePath;
                  faceData.isClipped = true;
                }
              } else {
                // 没有可定制区域，导出完整画布
                console.log(`面 ${face.name} 没有可定制区域，导出完整画布`);
                const canvasImagePath = await this.$refs.canvasEditor.exportCanvasImage();

                // 上传完整画布图片到服务器
                try {
                  console.log(`开始上传面 ${face.name} 的完整画布图片到服务器...`);
                  const uploadResult = await uploadDIYImage(canvasImagePath);
                  if (uploadResult && uploadResult.code === 200) {
                    faceData.canvasImagePath = uploadResult.data;
                    faceData.isClipped = false;
                    console.log(`面 ${face.name} 的完整画布图片上传成功:`, uploadResult.data);
                  } else {
                    console.error(`面 ${face.name} 的完整画布图片上传失败:`, uploadResult);
                    // 上传失败时使用临时路径作为备用方案
                    faceData.canvasImagePath = canvasImagePath;
                    faceData.isClipped = false;
                  }
                } catch (uploadError) {
                  console.error(`面 ${face.name} 的完整画布图片上传异常:`, uploadError);
                  // 上传异常时使用临时路径作为备用方案
                  faceData.canvasImagePath = canvasImagePath;
                  faceData.isClipped = false;
                }
              }
            }
            }
          } catch (error) {
            console.error(`导出面 ${face.name} 的画布图片失败:`, error);
            // 继续处理其他面，不中断流程
          }

          designData.faces.push(faceData);
        }
      }

      return designData;
    },

    // 收集传递给预览页面的参数
    collectPreviewParams() {
      // 获取商品信息
      const productInfo = this.getProductInfo();

      // 获取风格信息
      const styleInfo = this.getStyleInfo();

      // 获取用户提示词
      const userPrompt = this.getUserPrompt();

      // 获取使用的元素列表
      const elementsList = this.getUsedElementsList();

      return {
        productInfo,
        styleInfo,
        userPrompt,
        elementsList
      };
    },

    // 获取商品信息
    getProductInfo() {
      console.log('📦 获取商品信息 - this.productInfo:', this.productInfo);
      console.log('📦 商品信息键数量:', this.productInfo ? Object.keys(this.productInfo).length : 0);

      if (this.productInfo && Object.keys(this.productInfo).length > 0) {
        // 创建纯对象副本，避免Vue响应式对象序列化问题
        const plainProductInfo = {
          id: this.productInfo.id,
          name: this.productInfo.name,
          pic: this.productInfo.pic,
          price: this.productInfo.price,
          subTitle: this.productInfo.subTitle
        };
        console.log('✅ 返回已加载的商品信息(纯对象):', plainProductInfo);
        return plainProductInfo;
      }

      // 如果没有商品信息，返回默认值
      const defaultInfo = {
        id: this.productId,
        name: '个性化定制商品',
        pic: '',
        price: 0,
        subTitle: '个性化定制商品'
      };
      console.log('⚠️ 商品信息为空，返回默认值:', defaultInfo);
      return defaultInfo;
    },

    // 获取风格信息
    getStyleInfo() {
      const selectedStyleId = this.aiStyleData.selectedStyleId;

      if (selectedStyleId && this.aiStyleData.styles.length > 0) {
        const selectedStyle = this.aiStyleData.styles.find(style => style.id === selectedStyleId);
        if (selectedStyle) {
          return {
            id: selectedStyle.id,
            name: selectedStyle.name || '默认风格'
          };
        }
      }

      // 返回默认风格信息
      return {
        id: null,
        name: '默认风格名称'
      };
    },

    // 获取用户提示词
    getUserPrompt() {
      console.log('💬 获取用户提示词 - aiStyleData.fullPrompt:', this.aiStyleData.fullPrompt);
      console.log('💬 获取用户提示词 - aiStyleData.prompt:', this.aiStyleData.prompt);

      // 优先使用完整提示词（包含风格描述+用户输入）
      // 如果没有完整提示词，则使用用户输入的提示词
      // 如果都没有，则显示"暂无描述"
      const prompt = this.aiStyleData.fullPrompt || this.aiStyleData.prompt || '暂无描述';

      console.log('💬 返回提示词:', prompt);
      return prompt;
    },

    // 获取使用的元素列表
    getUsedElementsList() {
      const elementsList = [];

      console.log('开始收集元素列表，selectedFaces:', this.selectedFaces);
      console.log('designElements:', this.designElements);
      console.log('faceStepStates:', this.faceStepStates);

      // 遍历所有面的元素
      this.selectedFaces.forEach(face => {
        if (!face || !face.id) {
          console.log('跳过无效面:', face);
          return;
        }

        // 检查方式1：从designElements中获取（画布上的元素）
        const designElements = this.designElements[face.id] || [];
        console.log(`面 ${face.id} 的设计元素:`, designElements);

        designElements.forEach(element => {
          if (element.name) {
            elementsList.push(element.name);
          } else {
            // 根据元素类型生成默认名称
            switch (element.type) {
              case 'text':
                elementsList.push('文字元素');
                break;
              case 'image':
                elementsList.push('图片元素');
                break;
              case 'shape':
                elementsList.push('形状元素');
                break;
              default:
                elementsList.push('设计元素');
            }
          }
        });

        // 检查方式2：从步骤状态中获取（第三步添加的元素）
        const stepState = this.faceStepStates[face.id];
        console.log(`面 ${face.id} 的步骤状态:`, stepState);

        if (stepState && stepState.stepData && stepState.stepData.add && stepState.stepData.add.elements) {
          console.log(`面 ${face.id} 的步骤元素:`, stepState.stepData.add.elements);

          stepState.stepData.add.elements.forEach(element => {
            if (element.name) {
              elementsList.push(element.name);
            } else {
              // 根据元素类型生成默认名称
              switch (element.type) {
                case 'text':
                  elementsList.push('文字元素');
                  break;
                case 'image':
                  elementsList.push('图片元素');
                  break;
                case 'shape':
                  elementsList.push('形状元素');
                  break;
                default:
                  elementsList.push('设计元素');
              }
            }
          });
        }
      });

      // 去重并返回，如果为空则返回空数组（不返回默认值）
      const uniqueElements = [...new Set(elementsList)];
      console.log('收集到的元素列表:', uniqueElements);

      return uniqueElements;
    },

    // 检查AI风格化是否已完成
    isAIStyleCompleted() {
      const stepState = this.currentFaceStepState;
      return stepState && stepState.stepData['ai-style'].completed;
    },

    // 获取AI风格化按钮文本
    getAIStyleButtonText() {
      return this.isAIStyleCompleted() ? 'AI定义' : 'AI定义';
    },

    // 检查是否可以进行AI风格化
    canPerformAIStyle() {
      // 检查是否有当前面
      const currentFace = this.getCurrentFace();
      if (!currentFace) return false;

      // 检查是否在AI风格化步骤
      if (this.currentFaceStep !== 1) return false;

      // 检查是否正在生成中
      if (this.aiStyleData.isGenerating) return false;

      return true;
    },

    // 画布相关方法
    handleMaterialSelected(materials) {
      console.log('收到素材选择事件:', materials);
      // 当用户选择素材时，添加到画布中（支持预处理）
      if (materials && materials.length > 0) {
        materials.forEach(material => {
          console.log('添加素材到画布:', material, 'fileType:', material.fileType, 'fontFileUrl:', material.fontFileUrl, 'isSystemFont:', material.isSystemFont);
          if (this.$refs.canvasEditor) {
            // 判断是否为文字素材（fileType == 2，包括系统字体和自定义字体）
            const isTextMaterial = (material.fileType == 2 || material.fileType === '2') || material.isSystemFont;
            console.log('是否为文字素材:', isTextMaterial);
            
            if (isTextMaterial) {
              // 文字素材：添加文字到画布
              this.addTextMaterial(material);
            } else {
              // 图片素材：优先使用预处理方法
              if (typeof this.$refs.canvasEditor.addPreprocessedMaterial === 'function') {
                this.$refs.canvasEditor.addPreprocessedMaterial(material);
              } else {
                this.$refs.canvasEditor.addMaterial(material);
              }
            }
          } else {
            console.error('画布组件引用不存在');
          }
        });
      }
      this.closeMaterialModal();
    },

    // 添加文字素材到画布
    async addTextMaterial(material) {
      console.log('添加文字素材:', material);
      
      // 获取字体信息
      const fontFamily = material.fontFamily || 'CustomFont';
      const fontFileUrl = material.fontFileUrl;
      const isSystemFont = material.isSystemFont || !fontFileUrl;
      
      try {
        // 如果不是系统字体，需要加载字体文件
        if (!isSystemFont && fontFileUrl) {
          // 显示加载提示
          uni.showLoading({ title: '加载字体中...' });
          
          // 加载字体文件
          await this.loadFont(fontFamily, fontFileUrl);
          
          uni.hideLoading();
        }
        
        // 添加文字到画布
        if (this.$refs.canvasEditor && typeof this.$refs.canvasEditor.addTextElement === 'function') {
          this.$refs.canvasEditor.addTextElement({
            text: '广横走文创',
            fontFamily: isSystemFont ? 'sans-serif' : fontFamily,
            fontSize: 32,
            color: '#91F104',
            fontFileUrl: fontFileUrl || '',
            materialId: material.id,
            materialName: material.name,
            isSystemFont: isSystemFont
          });
        } else {
          console.error('画布组件不支持添加文字');
          uni.showToast({ title: '添加文字失败', icon: 'none' });
        }
      } catch (error) {
        uni.hideLoading();
        console.error('加载字体失败:', error);
        uni.showToast({ title: '字体加载失败', icon: 'none' });
      }
    },

    // 加载字体文件
    loadFont(fontFamily, fontFileUrl) {
      return new Promise((resolve, reject) => {
        // #ifdef MP-WEIXIN
        // 微信小程序使用 wx.loadFontFace
        wx.loadFontFace({
          global: true,
          family: fontFamily,
          source: `url("${fontFileUrl}")`,
          success: (res) => {
            console.log('字体加载成功:', fontFamily, res);
            resolve(res);
          },
          fail: (err) => {
            console.error('字体加载失败:', fontFamily, err);
            reject(err);
          }
        });
        // #endif
        
        // #ifndef MP-WEIXIN
        // 其他平台使用 CSS @font-face
        const style = document.createElement('style');
        style.textContent = `
          @font-face {
            font-family: '${fontFamily}';
            src: url('${fontFileUrl}');
          }
        `;
        document.head.appendChild(style);
        
        // 等待字体加载
        if (document.fonts && document.fonts.load) {
          document.fonts.load(`16px "${fontFamily}"`).then(() => {
            console.log('字体加载成功:', fontFamily);
            resolve();
          }).catch(reject);
        } else {
          // 降级处理：延迟一段时间后认为加载完成
          setTimeout(resolve, 500);
        }
        // #endif
      });
    },

    // 处理文字编辑事件
    handleEditText({ element, onUpdate }) {
      console.log('打开文字编辑弹窗:', element);
      this.textEditData = {
        text: element.text || '',
        color: element.color || '#91F104',
        elementId: element.id  // 保存元素ID
      };
      this.textEditCallback = onUpdate;
      this.showTextEditModal = true;
    },

    // 关闭文字编辑弹窗
    closeTextEditModal() {
      this.showTextEditModal = false;
      this.textEditCallback = null;
      this.textEditData = { text: '', color: '#91F104', elementId: null };
    },

    // 选择文字颜色
    selectTextColor(color) {
      this.textEditData.color = color;
    },

    // 确认文字编辑
    confirmTextEdit() {
      const { text, color, elementId } = this.textEditData;
      console.log('确认文字编辑:', { text, color, elementId });
      
      // 直接更新 designElements 中的元素
      const currentFace = this.selectedFaces[this.currentFaceIndex];
      console.log('当前面:', currentFace?.id, 'designElements:', this.designElements);
      
      if (currentFace && currentFace.id && this.designElements[currentFace.id]) {
        const elements = this.designElements[currentFace.id];
        console.log('当前面元素:', elements);
        const elementIndex = elements.findIndex(el => el.id === elementId);
        console.log('找到元素索引:', elementIndex);
        
        if (elementIndex !== -1) {
          // 更新元素属性
          const updatedElement = {
            ...elements[elementIndex],
            text: text,
            color: color,
            width: text.length * (elements[elementIndex].fontSize || 32)
          };
          this.$set(elements, elementIndex, updatedElement);
          console.log('文字元素已更新:', updatedElement);
          
          // 强制触发 designElements 的更新
          this.$set(this.designElements, currentFace.id, [...elements]);
          
          // 清除保存的Canvas状态，让Canvas使用更新后的 currentFaceElements
          // 这样 restoreCanvasState 不会恢复旧状态
          this.savedCanvasState = null;
        } else {
          console.log('未找到元素, elementId:', elementId);
        }
      } else {
        console.log('条件不满足:', {
          currentFace: !!currentFace,
          faceId: currentFace?.id,
          hasElements: !!this.designElements[currentFace?.id]
        });
      }
      
      this.closeTextEditModal();
    },

    handleElementAdded(element) {
      console.log('元素已添加到画布:', element);
    },

    handleElementDeleted() {
      console.log('元素已从画布删除');
    },

    handleHistoryChanged(historyState) {
      // 同步SimpleCanvasEditor的历史状态
      console.log('画布历史状态变化:', historyState);
      this.canvasCanUndo = historyState.canUndo;
      this.canvasCanRedo = historyState.canRedo;
    },

    handleElementsUpdated(elements) {
      // 同步SimpleCanvasEditor的素材变化到当前面
      // 确保索引在有效范围内
      if (!this.selectedFaces || this.selectedFaces.length === 0 ||
          this.currentFaceIndex < 0 || this.currentFaceIndex >= this.selectedFaces.length) {
        console.warn('无效的当前面索引，无法更新素材');
        return;
      }

      const currentFace = this.selectedFaces[this.currentFaceIndex];
      if (currentFace && currentFace.id) {
        console.log('更新当前面素材:', currentFace.id, '素材数量:', elements.length);
        // 深拷贝素材数据，确保包含所有属性
        const elementsClone = elements.map(el => ({
          ...el,
          x: el.x,
          y: el.y,
          width: el.width,
          height: el.height,
          rotation: el.rotation || 0,
          scaleX: el.scaleX || 1,
          scaleY: el.scaleY || 1
        }));
        console.log('保存的素材详情:', elementsClone);
        this.$set(this.designElements, currentFace.id, elementsClone);
      } else {
        console.warn('更新素材时当前面状态无效');
      }
    },

    handleCanvasResized(resizeInfo) {
      // 处理画布尺寸变化
      console.log('画布尺寸已调整:', resizeInfo);

      // 更新画布尺寸
      this.canvasWidth = resizeInfo.width;
      this.canvasHeight = resizeInfo.height;

      console.log('父组件画布尺寸已更新为:', this.canvasWidth, 'x', this.canvasHeight);
    },

    handleUndo() {
      if (this.$refs.canvasEditor) {
        this.$refs.canvasEditor.undo();
      }
    },

    handleRedo() {
      if (this.$refs.canvasEditor) {
        this.$refs.canvasEditor.redo();
      }
    },



    // 加载可定制面信息
    async loadCustomizableAreas(selectedAreaId) {
      try {
        this.isLoading = true;

        // 调用API获取商品的可定制面信息
        const response = await getProductCustomizableAreas(this.productId);

        if (response && response.code === 200 && response.data) {
          this.selectedFaces = response.data;
          console.log('✅ 成功加载可定制面数据:', this.selectedFaces);
          // 详细输出第一个面的定制区域信息
          if (this.selectedFaces.length > 0) {
            console.log('🎯 第一个面的详细信息:', this.selectedFaces[0]);
            if (this.selectedFaces[0].customizableRegions) {
              console.log('🔍 定制区域详情:', this.selectedFaces[0].customizableRegions);
            }
          }
        } else {
          // 如果API返回失败，使用模拟数据
          console.log('⚠️ API返回失败，使用模拟数据:', response);
          this.selectedFaces = this.getMockCustomizableAreas();
        }

        // 如果指定了areaId，设置为当前选中的面
        if (selectedAreaId) {
          const areaIndex = this.selectedFaces.findIndex(area => area.id == selectedAreaId);
          if (areaIndex !== -1) {
            this.currentFaceIndex = areaIndex;
          }
        }

        // 确保 currentFaceIndex 在有效范围内
        if (this.selectedFaces.length > 0 && this.currentFaceIndex >= this.selectedFaces.length) {
          this.currentFaceIndex = 0;
        }

        // 初始化设计元素存储，并保存原始底图URL
        this.selectedFaces.forEach(face => {
          if (face && face.id) {
            this.$set(this.designElements, face.id, []);

            // 保存原始底图URL，用于蒙版模式下获取底图尺寸
            if (face.previewImage && !face.originalPreviewImage) {
              this.$set(face, 'originalPreviewImage', face.previewImage);
              console.log(`💾 保存面 ${face.name} 的原始底图URL:`, face.originalPreviewImage);
            }
          } else {
            console.warn('无效的面数据:', face);
          }
        });



        // 加载商品模板信息
        await this.loadProductTemplate();

      } catch (error) {
        console.error('加载可定制面信息失败:', error);
        // 出错时使用模拟数据
        this.selectedFaces = this.getMockCustomizableAreas();
        // 确保 currentFaceIndex 在有效范围内
        if (this.selectedFaces.length > 0 && this.currentFaceIndex >= this.selectedFaces.length) {
          this.currentFaceIndex = 0;
        }
        this.selectedFaces.forEach(face => {
          if (face && face.id) {
            this.$set(this.designElements, face.id, []);
          } else {
            console.warn('无效的面数据:', face);
          }
        });

      } finally {
        this.isLoading = false;
      }
    },

    // 获取模拟可定制面数据
    getMockCustomizableAreas() {
      return [
        {
          id: 1,
          name: '正面',
          previewImage: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/banner1.jpg',
          backgroundImage: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/banner1.jpg'
        },
        {
          id: 2,
          name: '背面',
          previewImage: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/banner2.jpg',
          backgroundImage: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/banner2.jpg'
        }
      ];
    },

    // 从商品信息中获取templateId
    async loadTemplateIdFromProduct() {
      try {
        console.log('尝试从商品信息中获取templateId，商品ID:', this.productId);
        const response = await getDIYProductTemplate(this.productId);

        if (response && response.code === 200 && response.data) {
          if (response.data.id) {
            this.templateId = response.data.id.toString();
            console.log('成功获取templateId:', this.templateId);

            // 如果是从风格模型流程进入，记录风格模型信息
            if (this.styleModelId) {
              console.log('从风格模型流程进入，styleModelId:', this.styleModelId, 'templateId:', this.templateId);
            }
          } else {
            console.warn('商品没有关联的DIY模板');
            uni.showToast({
              title: '该商品暂不支持DIY定制',
              icon: 'none'
            });
            setTimeout(() => {
              uni.navigateBack();
            }, 1500);
          }
        } else {
          throw new Error('获取商品模板信息失败');
        }
      } catch (error) {
        console.error('获取templateId失败:', error);
        uni.showToast({
          title: '获取商品模板信息失败',
          icon: 'none'
        });
        setTimeout(() => {
          uni.navigateBack();
        }, 1500);
      }
    },

    // 获取商品详细信息
    async loadProductInfo() {
      try {
        console.log('🔍 获取商品详细信息，商品ID:', this.productId);
        const response = await fetchProductDetail(this.productId);

        console.log('📡 API 响应:', response);
        console.log('📡 响应码:', response?.code);
        console.log('📡 响应数据:', response?.data);

        if (response && response.code === 200 && response.data) {
          // API 返回的数据结构是 { data: { product: {...} } }
          const product = response.data.product || response.data;

          console.log('📦 原始商品数据:', {
            id: product.id,
            name: product.name,
            pic: product.pic,
            price: product.price,
            subTitle: product.subTitle,
            description: product.description
          });

          this.productInfo = {
            id: product.id,
            name: product.name,
            pic: product.pic,
            price: product.price,
            subTitle: product.subTitle || product.description
          };

          console.log('✅ 成功设置商品信息 - this.productInfo:', this.productInfo);
          console.log('✅ 商品名称:', this.productInfo.name);
          console.log('✅ 商品价格:', this.productInfo.price);
        } else {
          console.warn('⚠️ 获取商品信息失败，使用默认值');
          this.productInfo = {
            id: this.productId,
            name: '个性化定制商品',
            pic: '',
            price: 0,
            subTitle: '个性化定制商品'
          };
        }
      } catch (error) {
        console.error('❌ 获取商品信息失败:', error);
        // 使用默认值，不影响主流程
        this.productInfo = {
          id: this.productId,
          name: '个性化定制商品',
          pic: '',
          price: 0,
          subTitle: '个性化定制商品'
        };
      }
    },

    // 加载商品模板信息
    async loadProductTemplate() {
      try {
        const response = await getDIYProductTemplate(this.productId);

        if (response && response.code === 200 && response.data) {
          // 处理模板数据
          this.processTemplateData(response.data);
        }
      } catch (error) {
        console.error('加载商品模板信息失败:', error);
      }
    },

    // 处理模板数据
    processTemplateData(templateData) {
      // 根据模板数据初始化设计元素
      if (templateData.faces && Array.isArray(templateData.faces)) {
        templateData.faces.forEach(faceTemplate => {
          if (this.designElements[faceTemplate.faceId] && faceTemplate.elements) {
            this.designElements[faceTemplate.faceId] = faceTemplate.elements;
          }
        });
      }
    },

    // 加载风格模型详情
    async loadStyleModelDetail() {
      try {
        console.log('开始加载风格模型详情，styleModelId:', this.styleModelId);
        const response = await getStyleModelDetail(this.styleModelId);

        if (response && response.code === 200 && response.data) {
          // 获取风格模型的底图
          const styleModel = response.data;
          this.styleModelImage = styleModel.styleImage || styleModel.image || styleModel.bannerImage || '';
          console.log('风格模型底图已加载:', this.styleModelImage);

          // 检查并保存 functionType 字段
          if (styleModel.functionType) {
            this.styleModelFunctionType = styleModel.functionType;
            console.log('风格模型功能类型:', this.styleModelFunctionType);
          } else {
            console.warn('⚠️ 风格模型缺少 functionType 字段，将使用默认值 description_edit');
            this.styleModelFunctionType = 'description_edit';
          }
        } else {
          console.warn('获取风格模型详情失败，使用空白画布');
          this.styleModelImage = '';
          this.styleModelFunctionType = 'description_edit';
        }
      } catch (error) {
        console.error('加载风格模型详情失败:', error);
        this.styleModelImage = '';
        this.styleModelFunctionType = 'description_edit';
      }
    },


    

    

    
    // 下一步
    async nextStep() {
      // 使用状态锁防止重复执行
      if (this.isStepChanging) {
        console.log('步骤切换中，忽略重复点击');
        return;
      }

      const stepState = this.currentFaceStepState;
      if (!stepState) return;

      if (stepState.currentStep < this.steps.length - 1) {
        // 立即验证当前步骤是否可以进入下一步
        if (!this.canProceedToNextStep()) {
          uni.showToast({
            title: '请完成当前步骤',
            icon: 'none'
          });
          return;
        }

        // 设置状态锁
        this.isStepChanging = true;

        const currentStep = stepState.currentStep;

        try {
          // 如果是从第一步到第二步，显示处理中提示
          if (currentStep === 0) {
            uni.showLoading({
              title: '处理中...',
              mask: true
            });
          }

          // 执行步骤切换前的准备工作
          await this.prepareStepTransition(currentStep, currentStep + 1);

          // 关闭loading提示
          if (currentStep === 0) {
            uni.hideLoading();
          }

          // 标记当前步骤为完成
          this.$set(stepState.completed, currentStep, true);

          // 进入下一步
          stepState.currentStep++;
          this.onStepChange();

        } catch (error) {
          console.error('步骤切换失败:', error);

          // 确保关闭loading
          uni.hideLoading();

          uni.showToast({
            title: error.message || '步骤切换失败',
            icon: 'none'
          });
        } finally {
          // 释放状态锁，延迟500ms以确保步骤切换完成
          setTimeout(() => {
            this.isStepChanging = false;
          }, 500);
        }
      }
    },

    // 上一步
    async prevStep() {
      // 使用状态锁防止重复执行
      if (this.isStepChanging) {
        console.log('步骤切换中，忽略重复点击');
        return;
      }

      const stepState = this.currentFaceStepState;
      if (!stepState) return;

      if (stepState.currentStep > 0) {
        // 设置状态锁
        this.isStepChanging = true;

        const currentStep = stepState.currentStep;

        try {
          // 执行步骤切换前的准备工作
          await this.prepareStepTransition(currentStep, currentStep - 1);

          // 返回上一步
          stepState.currentStep--;
          this.onStepChange();

        } catch (error) {
          console.error('返回上一步失败:', error);
          uni.showToast({
            title: error.message || '返回失败',
            icon: 'none'
          });
        } finally {
          // 释放状态锁，延迟500ms以确保步骤切换完成
          setTimeout(() => {
            this.isStepChanging = false;
          }, 500);
        }
      }
    },

    // 跳转到指定步骤
    goToStep(stepIndex) {
      const stepState = this.currentFaceStepState;
      if (!stepState) return;

      if (stepIndex >= 0 && stepIndex < this.steps.length) {
        stepState.currentStep = stepIndex;
        this.onStepChange();
      }
    },

    // 步骤变化处理
    onStepChange() {
      const stepState = this.currentFaceStepState;
      if (!stepState) return;

      console.log(`步骤变化: 进入第${stepState.currentStep + 1}步`);

      // 根据当前步骤设置画布状态
      switch (stepState.currentStep) {
        case 0:
          // 第一步：替换素材步骤
          this.enterStep1();
          break;
        case 1:
          // 第二步：AI风格化步骤
          this.enterStep2();
          break;
        case 2:
          // 第三步：添加素材步骤
          this.enterStep3();
          break;
        case 3:
          // 第四步：图片剪裁步骤
          this.enterStep4();
          break;
      }
    },

    // 准备步骤切换
    async prepareStepTransition(fromStep, toStep) {
      console.log(`准备步骤切换: ${fromStep} -> ${toStep}`);

      // 场景1：从第一步进入第二步（替换素材 -> AI风格化）
      if (fromStep === 0 && toStep === 1) {
        await this.prepareStep1ToStep2();
      }
      // 场景2：从第二步返回第一步（AI风格化 -> 替换素材）
      else if (fromStep === 1 && toStep === 0) {
        await this.prepareStep2ToStep1();
      }
      // 场景3：从第二步进入第三步（AI风格化 -> 添加素材）
      else if (fromStep === 1 && toStep === 2) {
        await this.prepareStep2ToStep3();
      }
      // 场景4：从第三步返回第二步（添加素材 -> AI风格化）
      else if (fromStep === 2 && toStep === 1) {
        await this.prepareStep3ToStep2();
      }
      // 场景5：从第三步进入第四步（添加素材 -> 图片剪裁）
      else if (fromStep === 2 && toStep === 3) {
        await this.prepareStep3ToStep4();
      }
      // 场景6：从第四步返回第三步（图片剪裁 -> 添加素材）
      else if (fromStep === 3 && toStep === 2) {
        await this.prepareStep4ToStep3();
      }
    },

    // 场景1：从第一步进入第二步的准备工作
    async prepareStep1ToStep2() {
      console.log('准备从第一步进入第二步');
      console.log('当前画布元素数量:', this.currentFaceElements.length);

      try {
        // 1. 保存当前画布状态作为原始状态
        await this.saveOriginalCanvasState();

        // 2. 生成完整的合成图片（底图 + 所有素材元素）
        console.log('开始生成合成图片...');
        const compositeImageUrl = await this.generateCompositeImage();
        console.log('合成图片生成完成:', compositeImageUrl);

        // 3. 保存合成图片URL用于AI风格化处理
        await this.saveCompositeImageForAI(compositeImageUrl);

        // 4. 清空画布上的所有元素（因为已经合成到背景图中）
        console.log('清空画布元素...');
        await this.clearCanvasElements();

        // 5. 保持画布尺寸不变，避免重新计算导致图片截断
        this.$nextTick(() => {
          this.preserveCanvasSizeForStep2();
        });

        console.log('第一步到第二步准备完成');

        // 验证最终状态
        console.log('验证最终状态:');
        console.log('- currentFaceImage:', this.currentFaceImage);
        console.log('- currentFaceElements长度:', this.currentFaceElements.length);

        const currentStepState = this.currentFaceStepState;
        if (currentStepState && currentStepState.stepData && currentStepState.stepData['ai-style']) {
          console.log('- 步骤数据:', currentStepState.stepData['ai-style']);
        }

      } catch (error) {
        console.error('第一步到第二步准备失败:', error);
        throw new Error('准备AI风格化步骤失败');
      }
    },

    // 场景2：从第二步返回第一步的准备工作
    async prepareStep2ToStep1() {
      console.log('准备从第二步返回第一步');

      try {
        // 恢复原始画布状态（底图 + 元素列表）
        await this.restoreOriginalCanvasState();

        console.log('第二步到第一步准备完成');
      } catch (error) {
        console.error('第二步到第一步准备失败:', error);
        throw new Error('恢复原始画布状态失败');
      }
    },

    // 场景3：从第二步进入第三步的准备工作
    async prepareStep2ToStep3() {
      console.log('准备从第二步进入第三步');

      try {
        const stepState = this.currentFaceStepState;

        // 确保画布背景是正确的图片
        if (stepState && stepState.stepData && stepState.stepData['ai-style']) {
          // 优先使用AI生成的图片
          if (stepState.stepData['ai-style'].generatedImageUrl) {
            console.log('使用AI生成的图片作为第三步背景:', stepState.stepData['ai-style'].generatedImageUrl);
            await this.setCanvasBackgroundImage(stepState.stepData['ai-style'].generatedImageUrl);
          }
          // 如果没有AI生成的图片，使用合成图片（第一步的底图+素材）
          else if (stepState.stepData['ai-style'].compositeImageUrl) {
            console.log('使用合成图片作为第三步背景:', stepState.stepData['ai-style'].compositeImageUrl);
            await this.setCanvasBackgroundImage(stepState.stepData['ai-style'].compositeImageUrl);

            // 更新当前面的背景图片，确保 currentFaceImage 计算属性返回正确的值
            const currentFace = this.getCurrentFace();
            if (currentFace) {
              this.$set(currentFace, 'backgroundImage', stepState.stepData['ai-style'].compositeImageUrl);
            }
          }
        }

        // 清空画布上的所有元素
        await this.clearCanvasElements();

        console.log('第二步到第三步准备完成');
      } catch (error) {
        console.error('第二步到第三步准备失败:', error);
        throw new Error('准备添加素材步骤失败');
      }
    },

    // 场景4：从第三步返回第二步的准备工作
    async prepareStep3ToStep2() {
      console.log('准备从第三步返回第二步');

      try {
        // 保存第三步添加的元素到步骤数据中
        await this.saveStep3Elements();

        // 恢复第二步的画布状态（显示合成图片，无元素）
        const stepState = this.currentFaceStepState;
        if (stepState && stepState.stepData && stepState.stepData['ai-style'] && stepState.stepData['ai-style'].compositeImageUrl) {
          await this.setCanvasBackgroundImage(stepState.stepData['ai-style'].compositeImageUrl);
        }

        // 清空画布上的元素
        await this.clearCanvasElements();

        console.log('第三步到第二步准备完成');
      } catch (error) {
        console.error('第三步到第二步准备失败:', error);
        throw new Error('返回AI风格化步骤失败');
      }
    },

    // 初始化材质选择步骤
    initMaterialStep() {
      console.log('初始化材质选择步骤');
      // 这里可以加载材质相关数据
    },

    // 进入第一步：替换素材
    enterStep1() {
      console.log('进入第一步：替换素材');

      // 解锁画布，允许编辑
      this.isCanvasLocked = false;

      // 初始化材质选择相关数据
      this.initMaterialStep();

      console.log('第一步初始化完成，画布已解锁');
    },

    // 进入第二步：AI风格化
    enterStep2() {
      console.log('进入第二步：AI风格化');

      // 锁定画布，禁止编辑
      this.isCanvasLocked = true;

      // 初始化AI风格化相关数据
      this.initAIStyleStep();

      console.log('第二步初始化完成，画布已锁定');
    },

    // 进入第三步：添加素材
    enterStep3() {
      console.log('进入第三步：添加素材');

      // 解锁画布，允许编辑
      this.isCanvasLocked = false;

      // 初始化添加素材相关数据
      this.initAddMaterialStep();

      console.log('第三步初始化完成，画布已解锁');
    },

    // 进入第四步：图片剪裁
    async enterStep4() {
      console.log('进入第四步：图片剪裁');

      // 锁定画布，不允许编辑
      this.isCanvasLocked = true;

      // 初始化剪裁相关数据（异步获取底图尺寸）
      await this.initCropStep();

      console.log('第四步初始化完成，进入剪裁模式');
    },

    // 初始化AI风格化步骤
    initAIStyleStep() {
      console.log('初始化AI风格化步骤');
      // 加载AI风格相关数据
      this.loadAIStyles();
    },

    // 初始化添加素材步骤
    initAddMaterialStep() {
      console.log('初始化添加素材步骤');

      // 这里可以加载素材库数据
      uni.showToast({
        title: '可以添加素材了',
        icon: 'success',
        duration: 1500
      });
    },
    
    // 获取当前面数据
    getCurrentFace() {
      // 确保索引在有效范围内
      if (!this.selectedFaces || this.selectedFaces.length === 0 ||
          this.currentFaceIndex < 0 || this.currentFaceIndex >= this.selectedFaces.length) {
        return null;
      }
      return this.selectedFaces[this.currentFaceIndex];
    },

    // 获取当前面预览图
    getCurrentFacePreview() {
      // 确保索引在有效范围内
      if (!this.selectedFaces || this.selectedFaces.length === 0 ||
          this.currentFaceIndex < 0 || this.currentFaceIndex >= this.selectedFaces.length) {
        return this.defaultFaceImage;
      }
      const currentFace = this.selectedFaces[this.currentFaceIndex];
      return (currentFace && typeof currentFace === 'object') ? (currentFace.previewImage || this.defaultFaceImage) : this.defaultFaceImage;
    },

    // 获取步骤图标
    getStepIcon(stepIndex, isActive) {
      const iconNames = ['material_bg', 'AI_define', 'add_source', 'crop_image'];
      const iconName = iconNames[stepIndex];
      const suffix = isActive ? '_active' : '';
      return `https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/${iconName}${suffix}.png`;
    },

    // 打开素材选择模态框
    openMaterialModal() {
      this.materialModalVisible = true;
    },

    // 关闭素材选择模态框
    closeMaterialModal() {
      this.materialModalVisible = false;
    },



    // 保存Canvas状态（微信小程序v-if销毁前）
    saveCanvasState() {
      if (this.$refs.canvasEditor && this.$refs.canvasEditor.exportCanvasData) {
        try {
          this.savedCanvasState = this.$refs.canvasEditor.exportCanvasData();
        } catch (error) {
          this.savedCanvasState = null;
        }
      } else {
        this.savedCanvasState = null;
      }
    },

    // 恢复Canvas状态（微信小程序v-if重建后）
    restoreCanvasState() {
      if (this.savedCanvasState && this.$refs.canvasEditor) {
        try {
          // 强制更新canvasKey以重新渲染组件
          this.canvasKey++;

          this.$nextTick(() => {
            if (this.$refs.canvasEditor && this.$refs.canvasEditor.importCanvasData) {
              this.$refs.canvasEditor.importCanvasData(this.savedCanvasState);
            } else {
              // 如果不支持导入，至少确保elements数据同步
              this.currentFaceElements = [...this.savedCanvasState.elements];
            }
          });
        } catch (error) {
          // 恢复失败时的处理
        }
      }
    },

    // 验证当前步骤是否可以进入下一步
    canProceedToNextStep() {
      const stepState = this.currentFaceStepState;
      if (!stepState) return false;

      switch (stepState.currentStep) {
        case 0:
          // 材质选择步骤的验证逻辑
          return true; // 暂时返回true，实际应该检查是否选择了材质
        case 1:
          // AI风格化步骤的验证逻辑（临时跳过验证用于测试）
          return true; // stepState.stepData['ai-style'].completed;
        case 2:
          // 添加素材步骤的验证逻辑
          return true; // 暂时返回true，实际应该检查是否添加了必要的素材
        case 3:
          // 图片剪裁步骤的验证逻辑
          return stepState.stepData.crop.completed;
        default:
          return false;
      }
    },
    
    // 保存设计
    async saveDesign() {
      try {
        uni.showLoading({ title: '保存中...' });
        
        const designData = {
          productId: this.productId,
          faces: this.selectedFaces.filter(face => face && face.id).map(face => ({
            ...face,
            elements: this.designElements[face.id] || []
          }))
        };
        
        const result = await saveDIYDesign(designData);
        
        uni.hideLoading();
        
        if (result && result.code === 200) {
          uni.showToast({
            title: '保存成功',
            icon: 'success'
          });
        } else {
          throw new Error(result.message || '保存失败');
        }
      } catch (error) {
        uni.hideLoading();
        console.error('保存设计失败:', error);
        uni.showToast({
          title: '保存失败',
          icon: 'none'
        });
      }
    },
    
    // 预览设计
    previewDesign() {
      try {
        const designData = {
          productId: this.productId,
          faces: this.selectedFaces.filter(face => face && face.id).map(face => ({
            ...face,
            elements: this.designElements[face.id] || []
          }))
        };
        
        const queryString = `productId=${this.productId}&templateId=${this.templateId}&designData=${encodeURIComponent(JSON.stringify(designData))}`;

        uni.navigateTo({
          url: `/subpackages/diy/preview?${queryString}`
        });
      } catch (error) {
        console.error('预览失败:', error);
        uni.showToast({
          title: '预览失败',
          icon: 'none'
        });
      }
    },
    
    // 完成设计
    completeDesign() {
      this.previewDesign();
    },
    



    // 切换编辑面
    toggleEditFace() {
      // 确保 selectedFaces 存在且有多个面
      if (!this.selectedFaces || this.selectedFaces.length <= 1) {
        console.warn('没有足够的面可以切换');
        return;
      }

      this.currentFaceIndex = (this.currentFaceIndex + 1) % this.selectedFaces.length;
      this.selectedElementId = null;

      // 确保当前面的素材数据已初始化
      const currentFace = this.selectedFaces[this.currentFaceIndex];
      if (currentFace && currentFace.id && !this.designElements[currentFace.id]) {
        console.log('初始化面的素材数据:', currentFace.id);
        this.$set(this.designElements, currentFace.id, []);
      }
    },

    // 画布触摸事件处理
    handleCanvasTouchStart() {
      // 处理画布触摸开始事件
    },

    handleCanvasTouchMove() {
      // 处理画布触摸移动事件
    },

    handleCanvasTouchEnd() {
      // 处理画布触摸结束事件
    },

    // 选择元素
    selectElement(element) {
      this.selectedElementId = element.id;
    },

    // 删除元素
    deleteElement(elementId) {
      // 确保索引在有效范围内
      if (!this.selectedFaces || this.selectedFaces.length === 0 ||
          this.currentFaceIndex < 0 || this.currentFaceIndex >= this.selectedFaces.length) {
        console.warn('无效的当前面索引，无法删除元素');
        return;
      }

      const currentFace = this.selectedFaces[this.currentFaceIndex];
      if (!currentFace || !currentFace.id) return;

      const elements = this.designElements[currentFace.id] || [];
      const index = elements.findIndex(el => el.id === elementId);

      if (index > -1) {
        // 再次检查 currentFace 是否仍然有效
        if (currentFace && currentFace.id && this.designElements[currentFace.id]) {
          elements.splice(index, 1);
          this.selectedElementId = null;
        } else {
          console.warn('删除元素时当前面状态无效');
        }
      }
    },









    // 添加元素
    addElement(elementData) {
      // 确保索引在有效范围内
      if (!this.selectedFaces || this.selectedFaces.length === 0 ||
          this.currentFaceIndex < 0 || this.currentFaceIndex >= this.selectedFaces.length) {
        console.warn('无效的当前面索引，无法添加元素');
        return;
      }

      const currentFace = this.selectedFaces[this.currentFaceIndex];
      if (!currentFace) return;

      const element = {
        id: `element_${++this.elementIdCounter}`,
        ...elementData
      };

      if (!this.designElements[currentFace.id]) {
        this.$set(this.designElements, currentFace.id, []);
      }

      // 再次检查 currentFace 是否仍然有效（防止异步操作中状态变化）
      if (currentFace && currentFace.id && this.designElements[currentFace.id]) {
        this.designElements[currentFace.id].push(element);
        this.selectedElementId = element.id;
      } else {
        console.warn('添加元素时当前面状态无效');
      }
    },

    // 开始调整大小
    startResize(direction) {
      // 处理元素大小调整
      console.log('Start resize:', direction);
    },



    // 优化的面切换方法
    async switchFace(index) {
      // 验证索引有效性
      if (!this.selectedFaces || this.selectedFaces.length === 0 ||
          index < 0 || index >= this.selectedFaces.length) {
        console.warn('无效的面索引:', index);
        return;
      }

      if (index === this.currentFaceIndex || this.isSwitching) {
        return;
      }

      console.log('切换定制面:', this.currentFaceIndex, '->', index);

      // 保存当前面的状态
      const oldFace = this.selectedFaces[this.currentFaceIndex];
      if (oldFace && oldFace.id) {
        const oldElements = this.designElements[oldFace.id] || [];
        console.log('切换前面的素材状态:', oldFace.id, '素材数量:', oldElements.length, oldElements);

        // 保存当前面的步骤状态
        this.saveFaceStepState(oldFace.id);
      }

      // 设置切换状态，显示loading
      this.isSwitching = true;

      try {
        // 短暂延迟，让loading状态显示
        await new Promise(resolve => setTimeout(resolve, 50));

        // 切换面
        this.currentFaceIndex = index;
        this.selectedElementId = null;

        // 确保当前面的素材数据已初始化
        const currentFace = this.selectedFaces[index];
        if (currentFace && currentFace.id && !this.designElements[currentFace.id]) {
          console.log('初始化面的素材数据:', currentFace.id);
          this.$set(this.designElements, currentFace.id, []);
        }

        // 恢复新面的状态
        if (currentFace && currentFace.id) {
          const newElements = this.designElements[currentFace.id] || [];
          console.log('切换后面的素材状态:', currentFace.id, '素材数量:', newElements.length, newElements);

          // 恢复新面的步骤状态
          this.restoreFaceStepState(currentFace.id);
        }

        // 等待画布重绘完成
        await this.$nextTick();

        // 再等待一帧，确保渲染完成
        await new Promise(resolve => setTimeout(resolve, 100));

      } catch (error) {
        console.error('切换面时出现错误:', error);
      } finally {
        this.isSwitching = false;
      }
    },

    // 保存面的步骤状态
    saveFaceStepState(faceId) {
      // 步骤状态已经通过响应式数据自动保存，这里可以添加额外的保存逻辑
      console.log('保存面步骤状态:', faceId, this.faceStepStates[faceId]);
    },

    // 恢复面的步骤状态
    restoreFaceStepState(faceId) {
      // 确保面的步骤状态已初始化
      if (!this.faceStepStates[faceId]) {
        this.$set(this.faceStepStates, faceId, {
          currentStep: 0,
          stepData: {
            replace: { completed: false, data: null },
            'ai-style': { completed: false, data: null, generatedImageUrl: null },
            add: { completed: false, data: null },
            crop: { completed: false, data: null, croppedImageUrl: null }
          },
          completed: [false, false, false, false]
        });
      }

      console.log('恢复面步骤状态:', faceId, this.faceStepStates[faceId]);

      // 触发步骤变化处理
      this.onStepChange();
    },

    // 加载AI风格列表
    async loadAIStyles() {
      try {
        // 调用风格模型卡片列表接口
        const result = await fetchStyleModelCards();
        if (result && result.code === 200) {
          // 转换数据格式以适配AI风格选择器
          this.aiStyleData.styles = (result.data || []).map(card => ({
            id: card.id,
            name: card.name,
            image: card.coverImage,
            description: card.description
          }));
        } else {
          // 如果接口失败，使用模拟数据
          this.aiStyleData.styles = this.getMockAIStyles();
        }

        // 设置默认选中的风格
        if (this.aiStyleData.styles.length > 0) {
          if (this.styleModelId) {
            // 如果从风格列表页面跳转进入，选中指定的风格
            const targetStyle = this.aiStyleData.styles.find(style => style.id == this.styleModelId);
            if (targetStyle) {
              this.aiStyleData.selectedStyleId = targetStyle.id;
              console.log('自动选中指定风格:', targetStyle.name, 'ID:', targetStyle.id);
            } else {
              console.warn('未找到对应的风格ID:', this.styleModelId);
              // 如果找不到指定风格，默认选中第一个
              this.aiStyleData.selectedStyleId = this.aiStyleData.styles[0].id;
            }
          } else {
            // 如果没有指定风格ID，默认选中第一个风格，用户可以自由选择
            this.aiStyleData.selectedStyleId = this.aiStyleData.styles[0].id;
            console.log('默认选中第一个风格:', this.aiStyleData.styles[0].name, '用户可自由选择');
          }
        }

        console.log('加载AI风格列表:', this.aiStyleData.styles);

        // 预加载风格图片
        this.$nextTick(() => {
          this.preloadStyleImages();
        });

      } catch (error) {
        console.error('加载AI风格列表失败:', error);
        // 接口调用失败时使用模拟数据
        this.aiStyleData.styles = this.getMockAIStyles();

        // 预加载模拟数据的图片
        this.$nextTick(() => {
          this.preloadStyleImages();
        });

        uni.showToast({
          title: '加载风格失败，使用默认风格',
          icon: 'none'
        });
      }
    },

    // 获取模拟AI风格数据
    getMockAIStyles() {
      return [
        {
          id: 1,
          name: '油画风格',
          image: 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/mall/images/ai-style-oil.jpg',
          description: '经典油画艺术风格'
        },
        {
          id: 2,
          name: '富贵人间',
          image: 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/mall/images/ai-style-rich.jpg',
          description: '富贵典雅风格'
        },
        {
          id: 3,
          name: '赛博朋克',
          image: 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/mall/images/ai-style-cyber.jpg',
          description: '未来科技风格'
        },
        {
          id: 4,
          name: '多彩插画',
          image: 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/mall/images/ai-style-colorful.jpg',
          description: '多彩插画风格'
        },
        {
          id: 5,
          name: '暖光街道',
          image: 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/mall/images/ai-style-warm.jpg',
          description: '温暖街道风格'
        },
        {
          id: 6,
          name: '新国风国潮建筑插画风',
          image: 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/mall/images/ai-style-chinese.jpg',
          description: '新国风建筑插画风格'
        },
        {
          id: 7,
          name: '清凉夏日',
          image: 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/mall/images/ai-style-summer.jpg',
          description: '清凉夏日风格'
        },
        {
          id: 8,
          name: '国风水墨画',
          image: 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/mall/images/ai-style-ink.jpg',
          description: '传统水墨画风格'
        }
      ];
    },

    // 处理AI风格化按钮点击（动态按钮的统一入口）
    handleAIStyleButtonClick() {
      if (this.hasAIGeneratedImage) {
        // 如果已经生成了AI图片，执行撤销操作
        this.debouncedUndoAIGeneration();
      } else {
        // 如果还没有生成AI图片，打开AI风格化弹窗
        this.debouncedOpenAIStyleModal();
      }
    },

    // 打开AI风格化弹窗
    openAIStyleModal() {
      // 检查是否可以进行AI风格化
      if (!this.canPerformAIStyle()) {
        if (this.aiStyleData.isGenerating) {
          uni.showToast({
            title: 'AI正在生成中，请稍候',
            icon: 'none'
          });
        } else {
          uni.showToast({
            title: '当前无法进行AI风格化',
            icon: 'none'
          });
        }
        return;
      }

      this.showAIStyleModal = true;

      // 兼容性处理：确保弹窗正确显示
      this.$nextTick(() => {
        try {
          // 获取系统信息进行兼容性处理
          const systemInfo = uni.getSystemInfoSync();
          console.log('系统信息:', systemInfo.platform, systemInfo.system);

          // 在iOS设备上进行特殊处理
          if (systemInfo.platform === 'ios') {
            // 小程序环境：使用uni-app的方式处理
            // H5环境：使用DOM操作
            if (typeof document !== 'undefined') {
              // H5环境下的DOM操作
              const modalElement = document.querySelector('.ai-style-modal');
              if (modalElement) {
                modalElement.style.display = 'none';
                modalElement.offsetHeight; // 强制重绘
                modalElement.style.display = 'flex';
              }
            } else {
              // 小程序环境：强制触发页面更新
              this.$forceUpdate();
            }
          }
        } catch (error) {
          console.log('兼容性处理跳过:', error.message);
        }
      });

      // 如果还没有加载风格列表，则加载
      if (this.aiStyleData.styles.length === 0) {
        this.loadAIStyles();
      }

      // 调试信息
      console.log('AI风格化弹窗已打开');
      console.log('当前选中的风格ID:', this.aiStyleData.selectedStyleId);
      console.log('页面参数风格ID:', this.styleModelId);
      console.log('可用风格数量:', this.aiStyleData.styles.length);
      console.log('风格列表:', this.aiStyleData.styles.map(s => ({ id: s.id, name: s.name })));

      // 如果没有选中任何风格，强制选中第一个
      if (!this.aiStyleData.selectedStyleId && this.aiStyleData.styles.length > 0) {
        this.aiStyleData.selectedStyleId = this.aiStyleData.styles[0].id;
        console.log('强制选中第一个风格:', this.aiStyleData.styles[0].name);
      }

      // 如果是从风格列表页面跳转进入的，禁用风格切换
      if (this.styleModelId) {
        console.log('从风格列表页面跳转，风格ID:', this.styleModelId);
      }
    },

    // 关闭AI风格化弹窗
    closeAIStyleModal() {
      this.showAIStyleModal = false;
      // 不重置提示词，保持用户输入
    },

    // 选择AI风格
    selectAIStyle(styleId) {
      // 如果是从风格列表页面跳转进入的，且点击的不是当前锁定的风格，则禁用切换
      if (this.styleModelId && this.styleModelId != styleId) {
        uni.showToast({
          title: '当前风格已锁定',
          icon: 'none'
        });
        return;
      }

      this.aiStyleData.selectedStyleId = styleId;
      console.log('选择AI风格:', styleId);

      // 查找选中的风格信息
      const selectedStyle = this.aiStyleData.styles.find(style => style.id == styleId);
      if (selectedStyle) {
        console.log('选中风格详情:', selectedStyle.name);
      }
    },

    // 开始AI生成
    async startAIGeneration() {
      // 验证风格选择
      if (!this.aiStyleData.selectedStyleId) {
        uni.showToast({
          title: '请选择风格',
          icon: 'none'
        });
        return;
      }

      // 验证提示词输入（可选，但如果输入了需要满足最小长度）
      if (this.aiStyleData.prompt.trim() && this.aiStyleData.prompt.trim().length < 5) {
        uni.showToast({
          title: '画面描述至少需要5个字符',
          icon: 'none'
        });
        return;
      }

      try {
        // 关闭弹窗
        this.closeAIStyleModal();

        // 开始生成流程
        await this.processAIGeneration();

      } catch (error) {
        console.error('AI生成失败:', error);
        uni.showToast({
          title: error.message || '生成失败，请重试',
          icon: 'none'
        });
      }
    },

    // 处理AI生成流程
    async processAIGeneration() {
      // TODO: 实现完整的AI生成流程
      console.log('开始AI生成流程');
      console.log('选中风格:', this.aiStyleData.selectedStyleId);
      console.log('提示词:', this.aiStyleData.prompt);

      // 开始AI生成流程
      this.startAIGenerationProcess();
    },

    // 开始AI生成流程
    async startAIGenerationProcess() {
      try {
        // 设置生成状态
        this.aiStyleData.isGenerating = true;
        this.aiStyleData.generationProgress = 0;
        this.aiStyleData.generationTaskId = this.generateTaskId();

        // 开始进度模拟
        this.startProgressSimulation();

        console.log('开始AI生成流程，任务ID:', this.aiStyleData.generationTaskId);

        // 步骤1: 获取合成画布图片
        console.log('步骤1: 获取合成画布图片...');
        this.updateProgress(10, '正在获取画布图片...');
        const canvasImageUrl = await this.getCompositeImageForAI();

        // 步骤2: 调用AI风格化接口
        console.log('步骤2: 调用AI风格化接口...');
        this.updateProgress(30, '正在进行AI风格化处理...');
        const selectedStyle = this.aiStyleData.styles.find(s => s.id === this.aiStyleData.selectedStyleId);

        // 构建完整的提示词
        const fullPrompt = this.buildFullPrompt(selectedStyle);
        console.log('完整提示词:', fullPrompt);
        console.log('使用的功能类型:', this.styleModelFunctionType);

        // 保存完整提示词，用于后续传递给预览页面
        this.aiStyleData.fullPrompt = fullPrompt;
        console.log('已保存完整提示词到 aiStyleData.fullPrompt');

        const aiResult = await this.callAIStylizationWithRetry({
          imageUrl: canvasImageUrl,
          style: selectedStyle ? selectedStyle.name : '默认风格',  // 仅用于记录，万相API不使用
          prompt: fullPrompt,  // 万相API使用的完整提示词
          functionType: this.styleModelFunctionType  // 传递功能类型
        }, 3); // 最多重试3次

        // 步骤3: 处理AI生成结果
        console.log('步骤3: 处理生成结果...');
        this.updateProgress(90, '正在应用生成结果...');

        if (aiResult && aiResult.code === 200) {
          const resultImageUrl = aiResult.data;
          console.log('AI风格化成功:', resultImageUrl);

          // 完成生成
          this.completeAIGeneration(resultImageUrl);
        } else {
          throw new Error('AI风格化失败: ' + (aiResult.message || '未知错误'));
        }

      } catch (error) {
        console.error('AI生成流程失败:', error);
        this.stopAIGeneration();
        uni.showToast({
          title: error.message || 'AI生成失败',
          icon: 'none'
        });
      }
    },

    // 更新进度和状态文本
    updateProgress(progress, statusText) {
      this.aiStyleData.generationProgress = progress;
      // 可以在这里更新状态文本，如果需要的话
      console.log(`进度: ${progress}% - ${statusText}`);
    },

    // 开始进度模拟
    startProgressSimulation() {
      // 清除之前的定时器
      if (this.aiStyleData.progressTimer) {
        clearInterval(this.aiStyleData.progressTimer);
      }

      // 轻微的进度动画，主要进度由实际步骤控制
      this.aiStyleData.progressTimer = setInterval(() => {
        // 只在当前进度基础上做微小的动画效果
        const currentProgress = this.aiStyleData.generationProgress;
        if (currentProgress < 95) {
          // 在当前进度基础上增加0.1-0.5%的随机波动，保留两位小数
          const increment = Math.random() * 0.5 + 0.1;
          const newProgress = Math.min(currentProgress + increment, 95);
          this.aiStyleData.generationProgress = Math.round(newProgress * 100) / 100; // 保留两位小数
        }
      }, 500); // 更频繁的更新以显示小数变化
    },

    // 停止AI生成
    stopAIGeneration() {
      this.aiStyleData.isGenerating = false;
      this.aiStyleData.generationProgress = 0;
      this.aiStyleData.generationTaskId = null;

      // 清除定时器
      if (this.aiStyleData.progressTimer) {
        clearInterval(this.aiStyleData.progressTimer);
        this.aiStyleData.progressTimer = null;
      }
    },

    // 取消AI生成
    cancelAIGeneration() {
      uni.showModal({
        title: '确认取消',
        content: '确定要取消AI生成吗？',
        success: (res) => {
          if (res.confirm) {
            this.stopAIGeneration();
            uni.showToast({
              title: '已取消生成',
              icon: 'none'
            });
          }
        }
      });
    },

    // 生成任务ID
    generateTaskId() {
      return 'ai_task_' + Date.now() + '_' + Math.random().toString(36).substring(2, 11);
    },

    // 完成AI生成
    completeAIGeneration(resultImageUrl) {
      // 设置进度为100%
      this.aiStyleData.generationProgress = 100;

      // 短暂延迟后关闭进度条
      setTimeout(() => {
        this.stopAIGeneration();

        // 应用生成结果
        if (resultImageUrl) {
          this.applyAIGenerationResult(resultImageUrl);
        }

        uni.showToast({
          title: 'AI生成完成',
          icon: 'success'
        });
      }, 1000);
    },

    // 应用AI生成结果
    applyAIGenerationResult(imageUrl) {
      console.log('应用AI生成结果:', imageUrl);

      try {
        // 获取当前面信息
        const currentFace = this.getCurrentFace();
        if (!currentFace) {
          throw new Error('当前面信息不存在');
        }

        // 更新当前面的背景图片
        this.$set(currentFace, 'backgroundImage', imageUrl);
        this.$set(currentFace, 'previewImage', imageUrl);

        // 保存到当前面的步骤数据中
        const stepState = this.currentFaceStepState;
        if (stepState) {
          // 使用$set确保响应式更新
          this.$set(stepState.stepData['ai-style'], 'generatedImageUrl', imageUrl);
          this.$set(stepState.stepData['ai-style'], 'completed', true);

          // 标记AI风格化步骤为完成
          this.$set(stepState.completed, 1, true);
        }

        // 重要：清空画布上的所有元素，因为它们已经合成到AI生成的背景图中了
        this.clearCanvasElements();

        // 强制更新画布显示
        this.$nextTick(() => {
          // 触发画布重新渲染
          this.forceCanvasUpdate();

          // 额外触发一次强制更新，确保背景图片变化被检测到
          this.$forceUpdate();
        });

        console.log('AI生成结果应用成功，画布元素已清空');

      } catch (error) {
        console.error('应用AI生成结果失败:', error);
        uni.showToast({
          title: '应用结果失败',
          icon: 'none'
        });
      }
    },

    // 强制更新画布
    forceCanvasUpdate() {
      // 触发画布组件重新渲染
      const canvasEditor = this.$refs.canvasEditor;
      if (canvasEditor && canvasEditor.drawCanvas) {
        canvasEditor.drawCanvas();
      }
    },

    // 为第二步保持画布尺寸不变
    preserveCanvasSizeForStep2() {
      try {
        const canvasEditor = this.$refs.canvasEditor;
        if (!canvasEditor || !this.originalCanvasState || !this.originalCanvasState.canvasSize) {
          console.warn('无法保持画布尺寸：缺少必要的状态信息');
          this.forceCanvasUpdate();
          return;
        }

        const savedSize = this.originalCanvasState.canvasSize;
        console.log('保持第二步画布尺寸:', savedSize);

        // 直接设置父组件的画布尺寸，避免修改子组件的prop
        this.canvasWidth = savedSize.width;
        this.canvasHeight = savedSize.height;

        // 设置子组件的内部状态
        canvasEditor.dynamicWidth = savedSize.dynamicWidth;
        canvasEditor.dynamicHeight = savedSize.dynamicHeight;
        canvasEditor.canvasInitialized = true;

        // 强制更新画布显示
        this.$nextTick(() => {
          canvasEditor.initCanvas();
          canvasEditor.drawCanvas();

          console.log('第二步画布尺寸已保持为:', canvasEditor.actualWidth, 'x', canvasEditor.actualHeight);
        });

      } catch (error) {
        console.error('保持画布尺寸失败:', error);
        this.forceCanvasUpdate();
      }
    },

    // 保存原始画布状态
    async saveOriginalCanvasState() {
      try {
        const canvasEditor = this.$refs.canvasEditor;
        if (!canvasEditor) {
          throw new Error('画布组件未找到');
        }

        // 保存当前画布的完整状态，包括尺寸信息和底图状态
        this.originalCanvasState = {
          elements: JSON.parse(JSON.stringify(this.currentFaceElements)),
          backgroundImage: this.currentFaceImage,
          backgroundElement: canvasEditor.backgroundElement ? JSON.parse(JSON.stringify(canvasEditor.backgroundElement)) : null,
          canvasSize: {
            width: canvasEditor.actualWidth,
            height: canvasEditor.actualHeight,
            dynamicWidth: canvasEditor.dynamicWidth,
            dynamicHeight: canvasEditor.dynamicHeight,
            canvasInitialized: canvasEditor.canvasInitialized
          }
        };

        console.log('原始画布状态已保存:', this.originalCanvasState);
      } catch (error) {
        console.error('保存原始画布状态失败:', error);
        throw error;
      }
    },

    // 恢复原始画布状态
    async restoreOriginalCanvasState() {
      try {
        if (!this.originalCanvasState) {
          console.warn('没有保存的原始画布状态');
          return;
        }

        const canvasEditor = this.$refs.canvasEditor;
        if (!canvasEditor) {
          throw new Error('画布组件未找到');
        }

        // 恢复背景图片
        if (this.originalCanvasState.backgroundImage) {
          await this.setCanvasBackgroundImage(this.originalCanvasState.backgroundImage);
        }

        // 恢复底图元素状态
        if (this.originalCanvasState.backgroundElement) {
          canvasEditor.restoreBackgroundElement(this.originalCanvasState.backgroundElement);
        }

        // 恢复画布尺寸
        if (this.originalCanvasState.canvasSize) {
          const savedSize = this.originalCanvasState.canvasSize;
          console.log('恢复画布尺寸:', savedSize);

          // 设置为保存的尺寸
          canvasEditor.dynamicWidth = savedSize.dynamicWidth;
          canvasEditor.dynamicHeight = savedSize.dynamicHeight;
          canvasEditor.canvasInitialized = true;
        }

        // 恢复元素列表
        if (this.originalCanvasState.elements) {
          // 更新当前面的元素数据
          const currentFace = this.getCurrentFace();
          if (currentFace && currentFace.id) {
            this.$set(this.designElements, currentFace.id, JSON.parse(JSON.stringify(this.originalCanvasState.elements)));
          }

          // 通知画布组件恢复元素
          this.$nextTick(() => {
            if (canvasEditor.restoreElements) {
              canvasEditor.restoreElements(this.originalCanvasState.elements);
            }
            canvasEditor.drawCanvas();
          });
        }

        console.log('原始画布状态已恢复');
      } catch (error) {
        console.error('恢复原始画布状态失败:', error);
        throw error;
      }
    },

    // 保存第三步添加的元素
    async saveStep3Elements() {
      try {
        const stepState = this.currentFaceStepState;
        if (!stepState) {
          throw new Error('当前面步骤状态不存在');
        }

        // 保存第三步添加的元素到步骤数据中
        if (!stepState.stepData.add) {
          stepState.stepData.add = {};
        }

        stepState.stepData.add.elements = JSON.parse(JSON.stringify(this.currentFaceElements));
        stepState.stepData.add.completed = this.currentFaceElements.length > 0;

        console.log('第三步元素已保存:', stepState.stepData.add.elements);
      } catch (error) {
        console.error('保存第三步元素失败:', error);
        throw error;
      }
    },

    // 生成完整的合成图片（底图 + 所有素材元素）
    async generateCompositeImage() {
      try {
        console.log('开始生成合成图片...');
        console.log('当前背景图片:', this.currentFaceImage);
        console.log('当前元素数量:', this.currentFaceElements.length);
        console.log('当前元素:', this.currentFaceElements);

        // 确保画布组件存在
        const canvasEditor = this.$refs.canvasEditor;
        if (!canvasEditor) {
          throw new Error('画布组件未找到');
        }

        // 强制重绘画布，确保包含所有最新元素
        canvasEditor.drawCanvas();

        // 等待绘制完成
        await new Promise(resolve => setTimeout(resolve, 100));

        // 生成包含所有元素的完整画布图片
        let compositeImageUrl;
        try {
          compositeImageUrl = await this.getCurrentFaceCanvasImage();
          console.log('完整合成图片已生成:', compositeImageUrl);
        } catch (error) {
          console.warn('画布导出失败，可能是CORS问题，使用当前背景图片作为备用方案:', error);
          // 在浏览器调试环境中，如果遇到CORS问题，使用当前背景图片作为备用方案
          compositeImageUrl = this.currentFaceImage;
          console.log('使用备用方案，合成图片URL:', compositeImageUrl);
        }

        return compositeImageUrl;
      } catch (error) {
        console.error('生成合成图片失败:', error);
        throw error;
      }
    },

    // 设置画布背景图片
    async setCanvasBackgroundImage(imageUrl) {
      try {
        // 更新当前面的背景图片
        const currentFace = this.getCurrentFace();
        if (currentFace) {
          this.$set(currentFace, 'backgroundImage', imageUrl);
          this.$set(currentFace, 'previewImage', imageUrl);
        }

        // 通知画布组件更新背景图片
        this.$nextTick(() => {
          const canvasEditor = this.$refs.canvasEditor;
          if (canvasEditor) {
            canvasEditor.updateBackgroundImage(imageUrl);
          }
        });

        console.log('画布背景图片已设置:', imageUrl);
      } catch (error) {
        console.error('设置画布背景图片失败:', error);
        throw error;
      }
    },

    // 保存合成图片用于AI风格化处理
    async saveCompositeImageForAI(imageUrl) {
      try {
        const stepState = this.currentFaceStepState;
        if (stepState && stepState.stepData && stepState.stepData['ai-style']) {
          // 保存合成图片URL，用于AI风格化处理
          stepState.stepData['ai-style'].compositeImageUrl = imageUrl;
          stepState.stepData['ai-style'].lockedCanvasImageUrl = imageUrl;
        }

        console.log('合成图片已保存用于AI处理:', imageUrl);
      } catch (error) {
        console.error('保存合成图片失败:', error);
        throw error;
      }
    },

    // 获取用于AI处理的合成图片
    async getCompositeImageForAI() {
      try {
        const stepState = this.currentFaceStepState;
        if (stepState && stepState.stepData && stepState.stepData['ai-style'] && stepState.stepData['ai-style'].compositeImageUrl) {
          // 使用已保存的合成图片
          const compositeImageUrl = stepState.stepData['ai-style'].compositeImageUrl;
          console.log('使用已保存的合成图片进行AI处理:', compositeImageUrl);
          return compositeImageUrl;
        } else {
          // 如果没有合成图片，重新生成（兜底方案）
          console.warn('未找到合成图片，重新生成...');
          return await this.getCurrentFaceCanvasImage();
        }
      } catch (error) {
        console.error('获取AI处理图片失败:', error);
        throw error;
      }
    },

    // 清理画布上的所有素材元素
    async clearCanvasElements() {
      try {
        const canvasEditor = this.$refs.canvasEditor;
        if (!canvasEditor) {
          throw new Error('画布组件未找到');
        }

        // 清理画布上的所有元素，但保留背景图片
        canvasEditor.clearAllElements();

        // 同时清理当前面的元素数据
        const currentFace = this.getCurrentFace();
        if (currentFace && currentFace.id) {
          // 直接更新designElements中的数据，这样currentFaceElements计算属性会返回空数组
          this.$set(this.designElements, currentFace.id, []);
        }

        console.log('画布元素已清理');
      } catch (error) {
        console.error('清理画布元素失败:', error);
        throw error;
      }
    },

    // 确保第三步开始时画布状态干净
    ensureCleanCanvasForStep3() {
      try {
        // 检查当前面是否有AI生成的图片
        const stepState = this.currentFaceStepState;
        if (stepState && stepState.stepData && stepState.stepData['ai-style'] && stepState.stepData['ai-style'].generatedImageUrl) {
          // 如果有AI生成的图片，确保画布上没有素材元素
          if (this.currentFaceElements && this.currentFaceElements.length > 0) {
            // 清理当前面的元素数据
            const currentFace = this.getCurrentFace();
            if (currentFace && currentFace.id) {
              this.$set(this.designElements, currentFace.id, []);
            }

            // 通知画布组件清理元素
            const canvasEditor = this.$refs.canvasEditor;
            if (canvasEditor) {
              canvasEditor.clearAllElements();
            }

            console.log('第三步：已清理画布上的素材元素，保留AI生成的背景');
          }
        }
      } catch (error) {
        console.error('确保第三步画布状态失败:', error);
      }
    },

    // 撤销AI生成，恢复原始画布状态
    undoAIGeneration() {
      try {
        if (!this.originalCanvasState) {
          uni.showToast({
            title: '没有可恢复的状态',
            icon: 'none'
          });
          return;
        }

        // 恢复原始画布状态（包括第一步的所有素材元素）
        this.currentFaceElements = JSON.parse(JSON.stringify(this.originalCanvasState.elements));

        // 恢复原始背景图片
        const originalBackgroundImage = this.originalCanvasState.backgroundImage;
        if (originalBackgroundImage) {
          const currentFace = this.getCurrentFace();
          if (currentFace) {
            this.$set(currentFace, 'backgroundImage', originalBackgroundImage);
            this.$set(currentFace, 'previewImage', originalBackgroundImage);
          }
        }

        // 通知画布组件恢复元素和背景
        this.$nextTick(() => {
          const canvasEditor = this.$refs.canvasEditor;
          if (canvasEditor) {
            if (originalBackgroundImage) {
              canvasEditor.updateBackgroundImage(originalBackgroundImage);
            }
            canvasEditor.restoreElements(this.currentFaceElements);
          }
        });

        // 清除AI生成的图片数据
        const stepState = this.currentFaceStepState;
        if (stepState && stepState.stepData && stepState.stepData['ai-style']) {
          stepState.stepData['ai-style'].generatedImageUrl = null;
          stepState.stepData['ai-style'].completed = false;
        }

        // 清除原始状态缓存
        this.originalCanvasState = null;

        console.log('AI生成已撤销，画布状态已恢复，包括第一步的素材');
        uni.showToast({
          title: '已恢复原始设计',
          icon: 'success'
        });

      } catch (error) {
        console.error('撤销AI生成失败:', error);
        uni.showToast({
          title: '撤销失败',
          icon: 'none'
        });
      }
    },

    // 获取当前面画布图片并上传
    async getCurrentFaceCanvasImage() {
      try {
        // 获取画布组件引用
        const canvasEditor = this.$refs.canvasEditor;
        if (!canvasEditor) {
          throw new Error('画布组件未找到');
        }

        // 导出高分辨率画布图片（用于AI风格化）
        const tempFilePath = await canvasEditor.exportHighResCanvasImage();
        console.log('高分辨率画布导出成功:', tempFilePath);

        // 上传图片到服务器
        const uploadResult = await uploadDIYImage(tempFilePath);
        if (uploadResult && uploadResult.code === 200) {
          const imageUrl = uploadResult.data;
          console.log('高分辨率画布图片上传成功:', imageUrl);
          return imageUrl;
        } else {
          throw new Error('图片上传失败: ' + (uploadResult.message || '未知错误'));
        }

      } catch (error) {
        console.error('获取画布图片失败:', error);
        throw error;
      }
    },

    // 测试AI风格化功能（开发调试用）
    async testAIStyleFunction() {
      console.log('=== AI风格化功能测试 ===');

      // 测试1: 检查当前状态
      console.log('当前面信息:', this.getCurrentFace());
      console.log('当前步骤:', this.currentFaceStep);
      console.log('步骤状态:', this.currentFaceStepState);

      // 测试2: 检查风格数据
      console.log('风格列表:', this.aiStyleData.styles);
      console.log('选中风格:', this.aiStyleData.selectedStyleId);

      // 测试3: 检查画布状态
      const canvasEditor = this.$refs.canvasEditor;
      if (canvasEditor) {
        console.log('画布组件存在');
        const canvasData = canvasEditor.getCanvasData();
        console.log('画布数据:', canvasData);
      } else {
        console.log('画布组件不存在');
      }

      // 测试4: 检查是否可以进行AI风格化
      console.log('可以进行AI风格化:', this.canPerformAIStyle());

      console.log('=== 测试完成 ===');
    },

    // 构建完整的提示词（组合用户输入和风格描述）
    buildFullPrompt(selectedStyle) {
      let fullPrompt = '';

      // 1. 检查是否从风格模型进入
      const isFromStyleModel = this.styleModelId && selectedStyle;

      if (isFromStyleModel) {
        console.log('从风格模型进入，风格ID:', this.styleModelId);
        console.log('选中风格:', selectedStyle);

        // 2. 组合风格介绍和用户提示词
        const styleDescription = selectedStyle.description || '';
        const userPrompt = this.aiStyleData.prompt || '';

        if (styleDescription && userPrompt) {
          // 两者都有，进行组合
          fullPrompt = `${styleDescription}，${userPrompt}`;
          console.log('组合风格介绍和用户提示词');
        } else if (styleDescription) {
          // 只有风格介绍
          fullPrompt = styleDescription;
          console.log('使用风格介绍作为提示词');
        } else if (userPrompt) {
          // 只有用户提示词
          fullPrompt = userPrompt;
          console.log('使用用户提示词');
        } else {
          // 都没有，使用风格名称
          fullPrompt = selectedStyle.name || '默认风格';
          console.log('使用风格名称作为提示词');
        }
      } else {
        // 3. 非风格模型进入，只使用用户提示词
        console.log('非风格模型进入，使用用户提示词');
        fullPrompt = this.aiStyleData.prompt || '默认风格';
      }

      console.log('最终提示词:', fullPrompt);
      return fullPrompt;
    },

    // 带重试机制的AI风格化调用
    async callAIStylizationWithRetry(params, maxRetries = 3) {
      let lastError = null;

      for (let attempt = 1; attempt <= maxRetries; attempt++) {
        try {
          console.log(`AI风格化调用尝试 ${attempt}/${maxRetries}`);

          const result = await aiStylization(params);

          // 成功则直接返回
          if (result && result.code === 200) {
            console.log(`AI风格化调用成功，尝试次数: ${attempt}`);
            return result;
          } else {
            throw new Error(result.message || '服务器返回错误');
          }

        } catch (error) {
          lastError = error;
          console.error(`AI风格化调用失败，尝试 ${attempt}/${maxRetries}:`, error);

          // 如果不是最后一次尝试，等待一段时间后重试
          if (attempt < maxRetries) {
            const delay = Math.min(1000 * Math.pow(2, attempt - 1), 5000); // 指数退避，最大5秒
            console.log(`等待 ${delay}ms 后重试...`);
            await new Promise(resolve => setTimeout(resolve, delay));
          }
        }
      }

      // 所有重试都失败了
      throw new Error(`AI风格化调用失败，已重试 ${maxRetries} 次: ${lastError.message}`);
    },



    // 预加载风格图片
    async preloadStyleImages() {
      if (this.aiStyleData.styles.length === 0) return;

      const preloadPromises = this.aiStyleData.styles.map(async (style) => {
        if (this.imageCache.has(style.image)) {
          return; // 已缓存
        }

        try {
          const imageInfo = await uni.getImageInfo({
            src: style.image
          });
          this.imageCache.set(style.image, imageInfo);
          console.log('预加载风格图片成功:', style.name);
        } catch (error) {
          console.warn('预加载风格图片失败:', style.name, error);
        }
      });

      await Promise.allSettled(preloadPromises);
      console.log('风格图片预加载完成');
    },

    // 防抖处理
    debounce(func, wait) {
      let timeout;
      return function executedFunction(...args) {
        const later = () => {
          clearTimeout(timeout);
          func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
      };
    },

    // 节流处理
    throttle(func, limit) {
      let inThrottle;
      return function() {
        const args = arguments;
        const context = this;
        if (!inThrottle) {
          func.apply(context, args);
          inThrottle = true;
          setTimeout(() => inThrottle = false, limit);
        }
      };
    },

  // 页面销毁时清理定时器
  beforeDestroy() {
    this.cleanup();
  },

  // uni-app生命周期
  onUnload() {
    this.cleanup();
  },

  // 清理资源
  cleanup() {
    // 清理定时器
    if (this.aiStyleData.progressTimer) {
      clearInterval(this.aiStyleData.progressTimer);
      this.aiStyleData.progressTimer = null;
    }

    // 清理图片缓存
    if (this.imageCache) {
      this.imageCache.clear();
    }

    // 清理请求队列
    this.requestQueue = [];

    // 重置AI生成状态
    this.aiStyleData.isGenerating = false;
    this.aiStyleData.generationProgress = 0;
    this.aiStyleData.generationTaskId = null;

    console.log('资源清理完成');
  },

  // ==================== 第四步：图片剪裁相关方法 ====================

  // 初始化剪裁步骤
  async initCropStep() {
    console.log('📐 初始化图片剪裁步骤');
    console.log('📐 当前面图片:', this.currentFaceImage);
    console.log('📐 定制区域:', this.safeCustomizableArea);

    // 如果使用蒙版模式，需要获取底图尺寸
    const area = this.safeCustomizableArea;
    if (area.maskImageUrl) {
      try {
        console.log('🎭 蒙版模式，开始获取底图尺寸...');

        // 获取当前面的原始底图URL（originalPreviewImage）
        // 注意：不要使用 previewImage，因为它可能在第一步被替换成用户上传的图片
        const currentFace = this.getCurrentFace();
        const baseImageUrl = currentFace?.originalPreviewImage || currentFace?.previewImage;

        if (!baseImageUrl) {
          throw new Error('未找到底图URL');
        }

        console.log('🖼️ 底图URL:', baseImageUrl);
        console.log('📋 当前面数据:', {
          name: currentFace?.name,
          originalPreviewImage: currentFace?.originalPreviewImage,
          previewImage: currentFace?.previewImage,
          backgroundImage: currentFace?.backgroundImage
        });

        // 获取底图的实际尺寸
        const imageInfo = await this.getImageInfo(baseImageUrl);
        this.baseImageWidth = imageInfo.width;
        this.baseImageHeight = imageInfo.height;
        console.log('✅ 底图尺寸获取成功:', {
          width: this.baseImageWidth,
          height: this.baseImageHeight,
          url: baseImageUrl
        });
      } catch (error) {
        console.error('❌ 获取底图尺寸失败:', error);
        // 使用默认尺寸
        this.baseImageWidth = 750;
        this.baseImageHeight = 750;
      }
    }

    console.log('📐 裁剪器配置:', {
      width: this.cropperCutWidth,
      height: this.cropperCutHeight,
      widthPercent: this.cropperWidthPercent,
      heightPercent: this.cropperHeightPercent,
      imageWidthPercent: this.cropperImageWidthPercent,
      imageHeightPercent: this.cropperImageHeightPercent,
      radius: this.cropperRadius
    });
  },

  // 获取图片信息
  getImageInfo(imagePath) {
    return new Promise((resolve, reject) => {
      uni.getImageInfo({
        src: imagePath,
        success: (res) => {
          resolve({
            width: res.width,
            height: res.height,
            path: res.path
          });
        },
        fail: (err) => {
          reject(err);
        }
      });
    });
  },

  // 处理剪裁完成
  async handleCropComplete(e) {
    try {
      console.log('✂️ 剪裁完成事件:', e);

      // qf-image-cropper 返回的数据格式: { tempFilePath: '...' }
      const croppedImagePath = e.tempFilePath || e;

      if (!croppedImagePath) {
        throw new Error('未获取到剪裁后的图片路径');
      }

      console.log('✅ 剪裁后的图片路径:', croppedImagePath);

      // 保存剪裁结果到步骤数据
      const stepState = this.currentFaceStepState;
      if (stepState) {
        this.$set(stepState.stepData.crop, 'completed', true);
        this.$set(stepState.stepData.crop, 'croppedImageUrl', croppedImagePath);
        this.$set(stepState.stepData.crop, 'cropData', e);
      }

      uni.showToast({
        title: '剪裁完成',
        icon: 'success',
        duration: 1500
      });

      // 检查是否所有面都已完成剪裁
      setTimeout(() => {
        console.log('🔍 检查所有面是否完成:', this.allFacesCompleted);

        if (this.allFacesCompleted) {
          // 所有面都完成了，显示确认弹窗
          console.log('✅ 所有面都已完成剪裁，显示确认弹窗');
          this.showDesignCompleteConfirmModal();
        } else {
          // 还有未完成的面，提示用户
          const uncompletedFaces = this.selectedFaces.filter(face => {
            const faceStepState = this.faceStepStates[face.id];
            return !faceStepState || !faceStepState.stepData.crop || !faceStepState.stepData.crop.completed;
          });

          console.log('⚠️ 还有未完成的面:', uncompletedFaces.map(f => f.name));

          uni.showModal({
            title: '提示',
            content: `当前面已完成剪裁，还有 ${uncompletedFaces.length} 个面未完成。是否继续编辑其他面？`,
            confirmText: '继续编辑',
            cancelText: '稍后处理',
            success: (res) => {
              if (res.confirm) {
                // 切换到第一个未完成的面
                const nextUncompletedIndex = this.selectedFaces.findIndex(face => {
                  const faceStepState = this.faceStepStates[face.id];
                  return !faceStepState || !faceStepState.stepData.crop || !faceStepState.stepData.crop.completed;
                });

                if (nextUncompletedIndex !== -1) {
                  this.switchFace(nextUncompletedIndex);
                }
              }
            }
          });
        }
      }, 1500);

    } catch (error) {
      console.error('❌ 剪裁处理失败:', error);
      uni.showToast({
        title: '剪裁失败: ' + error.message,
        icon: 'none'
      });
    }
  },

  // 生成剪裁后的图片
  async generateCroppedImage(cropData) {
    try {
      console.log('生成剪裁后的图片:', cropData);

      // 使用CropEditor组件的剪裁功能生成图片
      const croppedImagePath = await this.$refs.cropEditor?.exportCroppedImage(cropData);

      if (croppedImagePath) {
        // 上传剪裁后的图片到服务器
        console.log('开始上传剪裁后的图片到服务器...');
        const uploadResult = await uploadDIYImage(croppedImagePath);

        if (uploadResult && uploadResult.code === 200) {
          console.log('剪裁图片上传成功:', uploadResult.data);
          return uploadResult.data;
        } else {
          throw new Error('图片上传失败');
        }
      } else {
        throw new Error('图片剪裁失败');
      }
    } catch (error) {
      console.error('生成剪裁图片失败:', error);
      // 如果剪裁失败，返回原图作为备用方案
      return this.currentFaceImage;
    }
  },

  // 第3步到第4步的准备工作
  async prepareStep3ToStep4() {
    console.log('准备从第三步进入第四步');

    try {
      // 保存第三步的元素状态
      await this.saveStep3Elements();

      // 生成最终的合成图片用于剪裁
      const finalImageUrl = await this.generateFinalCompositeImage();

      // 保存合成图片到当前面
      const currentFace = this.getCurrentFace();
      if (currentFace) {
        this.$set(currentFace, 'backgroundImage', finalImageUrl);
      }

      console.log('第三步到第四步准备完成');
    } catch (error) {
      console.error('第三步到第四步准备失败:', error);
      throw new Error('准备图片剪裁步骤失败');
    }
  },

  // 第4步到第3步的准备工作
  async prepareStep4ToStep3() {
    console.log('准备从第四步返回第三步');

    try {
      // 恢复第三步的画布状态
      await this.restoreStep3State();

      console.log('第四步到第三步准备完成');
    } catch (error) {
      console.error('第四步到第三步准备失败:', error);
      throw new Error('返回添加素材步骤失败');
    }
  },

  // 生成最终的合成图片
  async generateFinalCompositeImage() {
    try {
      // 导出当前画布状态作为最终图片
      const canvasImageUrl = await this.$refs.canvasEditor.exportCanvasImage();
      console.log('最终合成图片生成完成:', canvasImageUrl);
      return canvasImageUrl;
    } catch (error) {
      console.error('生成最终合成图片失败:', error);
      // 如果导出失败，使用当前背景图片作为备用
      return this.currentFaceImage;
    }
  },

  // 恢复第三步状态
  async restoreStep3State() {
    try {
      // 恢复第三步的元素和背景
      const stepState = this.currentFaceStepState;
      if (stepState && stepState.stepData.add && stepState.stepData.add.elements) {
        // 恢复元素
        await this.$refs.canvasEditor.restoreElements(stepState.stepData.add.elements);
      }

      // 恢复背景图片
      if (stepState && stepState.stepData['ai-style'] && stepState.stepData['ai-style'].generatedImageUrl) {
        await this.setCanvasBackgroundImage(stepState.stepData['ai-style'].generatedImageUrl);
      }

    } catch (error) {
      console.error('恢复第三步状态失败:', error);
      throw error;
    }
  },

  // 获取第4步按钮文本
  getStep4NextButtonText() {
    const stepState = this.currentFaceStepState;
    if (stepState && stepState.stepData.crop.completed) {
      return '完成设计';
    }
    return '确认剪裁';
  },

  // 处理第4步的下一步操作
  async handleStep4NextAction() {
    try {
      console.log('🎯 触发图片剪裁');

      // 调用 qf-image-cropper 组件的裁剪方法
      const cropper = this.$refs.imageCropper;
      if (!cropper) {
        throw new Error('未找到裁剪组件');
      }

      // qf-image-cropper 组件会自动触发 @crop 事件
      // 调用组件的 cropClick 方法来触发裁剪
      if (typeof cropper.cropClick === 'function') {
        cropper.cropClick();
      } else {
        throw new Error('裁剪组件不支持 cropClick 方法');
      }

    } catch (error) {
      console.error('❌ 触发剪裁失败:', error);
      uni.showToast({
        title: '剪裁失败: ' + error.message,
        icon: 'none'
      });
    }
  },

  // 显示设计完成确认弹窗
  showDesignCompleteConfirmModal() {
    uni.showModal({
      title: '完成设计',
      content: '所有面都已完成剪裁，是否完成设计并生成预览？',
      confirmText: '完成设计',
      cancelText: '继续编辑',
      success: (res) => {
        if (res.confirm) {
          // 用户确认完成设计
          this.handleDesignComplete();
        } else {
          // 用户选择继续编辑
          console.log('用户选择继续编辑');
        }
      }
    });
  },

  // 处理设计完成
  async handleDesignComplete() {
    try {
      console.log('🎉 开始处理设计完成流程');

      // 显示加载提示
      uni.showLoading({
        title: '正在保存设计...',
        mask: true
      });

      // 收集所有设计数据并导出（包括上传裁剪后的图片）
      const designData = await this.collectAllDesignData();

      console.log('✅ 设计数据收集完成:', designData);

      // 验证必要参数
      if (!this.productId) {
        throw new Error('商品ID不能为空');
      }

      if (!this.templateId) {
        throw new Error('模板ID不能为空');
      }

      if (!designData.faces || designData.faces.length === 0) {
        throw new Error('设计数据不能为空');
      }

      // 保存设计数据到后端
      console.log('💾 开始保存设计数据到后端...');
      const saveResult = await this.saveDesignData(designData);
      console.log('✅ 设计数据保存成功:', saveResult);

      // 收集传递给预览页面的额外数据
      const previewParams = this.collectPreviewParams();

      console.log('📦 准备传递的参数:', previewParams);

      // 隐藏加载提示
      uni.hideLoading();

      // 显示成功提示
      uni.showToast({
        title: '设计完成',
        icon: 'success',
        duration: 1500
      });

      // 延迟跳转到预览页面
      setTimeout(() => {
        uni.navigateTo({
          url: `/subpackages/diy/preview?productId=${this.productId}&templateId=${this.templateId}&designId=${saveResult.designId}&productInfo=${JSON.stringify(previewParams.productInfo)}&styleInfo=${JSON.stringify(previewParams.styleInfo)}&userPrompt=${previewParams.userPrompt}&elementsList=${JSON.stringify(previewParams.elementsList)}`
        });
      }, 1500);

    } catch (error) {
      console.error('❌ 设计完成处理失败:', error);
      uni.hideLoading();
      uni.showToast({
        title: error.message || '设计完成失败',
        icon: 'none',
        duration: 2000
      });
    }
  }
  }
};
</script>

<style lang="scss" scoped>
.diy-customize-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #282828;
}

// 导航栏中的定制面切换
.face-tabs {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 20rpx;
}

.tabs-scroll {
  white-space: nowrap;
}

.tabs-container {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16rpx;
}

.tab-item {
  box-sizing: border-box;
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  padding: 12rpx 32rpx;
  gap: 16rpx;
  width: 120rpx;
  height: 64rpx;
  background: transparent;
  border: 2rpx solid transparent;
  border-radius: 16rpx;
  flex: none;
  transition: all 0.3s ease;

  &.active {
    background: rgba(255, 255, 255, 0.1);
    border-color: #A9FF00;
  }
}

.tab-name {
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.7);
  font-weight: 400;

  .tab-item.active & {
    color: #FFFFFF;
  }
}

// 步骤进度条
.step-progress {
  padding: 50rpx 20rpx 20rpx;
}

.step-container {
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 520rpx;
  margin: 0 auto;
}

.step-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4rpx;
  z-index: 2;
  width: 120rpx;
  height: 100rpx;
  padding: 12rpx;
  border-radius: 6rpx;
  background: rgba(238, 238, 238, 0.05);
  border: 1rpx solid rgba(238, 238, 238, 0.1);
  transition: all 0.3s ease;
  position: relative;

  &.active {
    border: 1px solid rgba(238, 238, 238, 0.1);
  }

  &.completed {
    border: 1px solid rgba(238, 238, 238, 0.1);
  }
}

.step-number {
  position: absolute;
  width: 36rpx;
  height: 36rpx;
  left: -8rpx;
  top: -8rpx;
  border-radius: 50%;
  background: #637C00;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 'PingFang SC';
  font-style: normal;
  font-weight: 400;
  font-size: 24rpx;
  line-height: 34rpx;
  text-align: center;
  color: #282921;
  z-index: 3;

  .step-item.active & {
    background: linear-gradient(79.13deg, #91F104 -10.53%, #CDFE16 49.3%, #91F104 103%);
  }

  .step-item.completed & {
    background: linear-gradient(79.13deg, #91F104 -10.53%, #CDFE16 49.3%, #91F104 103%);
  }
}

.step-icon {
  width: 50rpx;
  height: 50rpx;
  border-radius: 6rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.step-image {
  width: 100%;
  height: 100%;
}

.step-name {
  font-size: 20rpx;
  color: #637C00;
  text-align: center;

  .step-item.active & {
    color: #FFFFFF;
  }
}

.step-lines {
  position: absolute;
  top: 37rpx;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-between;
  z-index: 0;
}

.step-line {
  flex: 1;
  height: 2rpx;
  background: #647D00;
  margin: 0 50rpx;
  opacity: 0.3;
  border-top: 2rpx dashed #647D00;
  background: none;

  &.completed {
    border-top: 2rpx dashed #647D00;
    opacity: 1;
  }
}

// 提示信息
.tip-info {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 12rpx 32rpx;
  margin: 0 32rpx 20rpx;
  background: rgba(229, 253, 192, 0.5);
  border-radius: 8rpx;
}

.tip-icon {
  width: 32rpx;
  height: 32rpx;
}

.tip-text {
  font-size: 24rpx;
  color: #FFFFFF;
  line-height: 1.4;
}

// 画布区域
.canvas-container {
  flex: 1;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 20rpx 20rpx;
  border-radius: 16rpx;
  overflow: visible; /* 改为visible，避免裁剪控制按钮 */
  min-height: 350px; /* 增加最小高度，为303x348画布+控制按钮留出足够空间 */
  padding: 40rpx; /* 增加内边距，确保控制按钮不被裁剪 */
  z-index: 1; /* 确保画布容器层级低于弹窗 */
  transition: opacity 0.3s ease, z-index 0s;
}

/* Canvas图片替代样式 */
.canvas-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #f5f5f5;
  border: 2px dashed #ddd;
  border-radius: 8px;
}

.placeholder-text {
  font-size: 16px;
  color: #666;
  margin-bottom: 8px;
}

.placeholder-desc {
  font-size: 12px;
  color: #999;
}

.canvas-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-preview {
  max-width: 90%;
  max-height: 90%;
  border-radius: 8rpx;
}

.elements-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.design-element {
  position: absolute;
  pointer-events: auto;

  &.selected {
    border: 2rpx dashed #A7CB00;
  }
}

// 操作工具栏
.operation-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 40rpx;
  background: rgba(40, 40, 40, 0.8);
  margin: 0 20rpx;
  border-radius: 16rpx;
}

.toolbar-left,
.toolbar-right {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.toolbar-btn {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;

  &.disabled {
    opacity: 0.3;
    pointer-events: none;
  }

  &:active {
    background: rgba(255, 255, 255, 0.2);
    transform: scale(0.95);
  }
}

.toolbar-icon {
  width: 40rpx;
  height: 40rpx;
}

// 操作按钮容器
.action-buttons-container {
  padding: 20rpx 40rpx 90rpx;
  display: flex;
  flex-direction: column;
  gap: 20rpx;
  align-items: center;
}

.action-btn {
  width: 686rpx;
  padding: 16rpx 28rpx;
  border-radius: 8rpx;
  text-align: center;
}

.material-btn {
  background: #A9FF00;
  border: none;
}

.next-btn {
  background: #000000;
  border: none;
}

.complete-btn {
  background: #A9FF00;
  border: none;
}

.btn-text {
  font-size: 28rpx;
  font-weight: 400;

  .material-btn & {
    color: #0A0D05;
  }

  .next-btn & {
    color: #A9FF00;
  }

  .complete-btn & {
    color: #0A0D05;
  }
}

// 元素控制相关样式保持不变
.element-controls {
  position: absolute;
  top: -10rpx;
  left: -10rpx;
  right: -10rpx;
  bottom: -10rpx;
}

.control-point {
  position: absolute;
  width: 20rpx;
  height: 20rpx;
  background: #A7CB00;
  border: 2rpx solid #FFFFFF;
  border-radius: 50%;

  &.top-left {
    top: 0;
    left: 0;
  }

  &.top-right {
    top: 0;
    right: 0;
  }

  &.bottom-left {
    bottom: 0;
    left: 0;
  }

  &.bottom-right {
    bottom: 0;
    right: 0;
  }
}

.delete-btn {
  position: absolute;
  top: -15rpx;
  right: -15rpx;
  width: 30rpx;
  height: 30rpx;
  background: #FF4D4F;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.delete-icon {
  width: 16rpx;
  height: 16rpx;
}

.text-element,
.image-element {
  width: 100%;
  height: 100%;
}

/* 切换面loading样式 */
.switching-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.switching-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20rpx;
}

.loading-spinner {
  width: 60rpx;
  height: 60rpx;
  border: 4rpx solid #f3f3f3;
  border-top: 4rpx solid #007AFF;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.switching-text {
  font-size: 28rpx;
  color: #666;
}

/* AI风格化弹窗样式 */
.ai-style-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: flex-end;
  z-index: 10000 !important;
  /* iOS微信端兼容性修复 */
  -webkit-transform: translate3d(0, 0, 0);
  transform: translate3d(0, 0, 0);
  -webkit-backface-visibility: hidden;
  backface-visibility: hidden;
  /* 强制硬件加速，确保在iOS微信端正确显示 */
  will-change: transform;
}

/* 顶部关闭指示器 */
.modal-close-indicator {
  width: 72rpx;
  height: 8rpx;
  background: #FFFFFF;
  border-radius: 4rpx;
  margin: 10rpx auto 0;
  position: absolute;
  top: 10rpx;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10001 !important;
}

.ai-modal-content {
  width: 100%;
  background: #000000;
  border-radius: 8px 8px 0 0;
  padding: 20px;
  max-height: 80vh;
  overflow-y: auto;
  /* iOS微信端兼容性修复 */
  -webkit-transform: translate3d(0, 0, 0);
  transform: translate3d(0, 0, 0);
  position: relative;
  z-index: 10001 !important;
}

.ai-modal-header {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  position: relative;
}

.ai-header-icon {
  width: 24px;
  height: 24px;
  position: absolute;
  left: 0;
}

.ai-icon {
  width: 100%;
  height: 100%;
}

.ai-modal-title {
  font-size: 16px;
  font-weight: 400;
  color: #FFFFFF;
  line-height: 1.4;
  text-align: center;
}

.ai-section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-title-text {
  font-size: 16px;
  font-weight: 500;
  color: #FFFFFF;
  line-height: 1.4;
}

.ai-styles-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-bottom: 24px;
}

.style-item {
  position: relative;
}

.style-item.disabled {
  opacity: 0.6;
  pointer-events: none;
}

.style-image-container {
  position: relative;
  width: 100%;
  height: 83px;
  border-radius: 8px;
  overflow: hidden;
}

.style-image {
  width: 100%;
  height: 100%;
  border-radius: 8px;
}

/* 未选中时的灰色蒙版 */
.style-unselected-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.55);
  border-radius: 8px;
}

/* 选中时的绿色边框（无蒙版，保持图片清晰） */
.style-selected-border {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border: 1px solid #A9FF00;
  border-radius: 8px;
  pointer-events: none;
}

.style-name-container {
  background: linear-gradient(0deg, rgba(0, 0, 0, 0.8) 0%, rgba(0, 0, 0, 0.08) 100%);
  padding: 3px 8px;
  border-radius: 0 0 8px 8px;
  margin-top: -22px;
  position: relative;
  z-index: 1;
}

.style-name {
  font-size: 12px;
  font-weight: 400;
  color: #FFFFFF;
  line-height: 1.4;
  text-align: center;
  display: block;
}

.ai-prompt-section {
  margin-bottom: 24px;
}

.prompt-label {
  font-size: 14px;
  font-weight: 400;
  color: #FFFFFF;
  line-height: 1.4;
  margin-bottom: 8px;
  display: block;
}

.prompt-input-container {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 12px;
}

.prompt-input {
  width: 100%;
  min-height: 80px;
  background: transparent;
  border: none;
  color: #FFFFFF;
  font-size: 14px;
  line-height: 1.4;
  resize: none;
}

.prompt-input::placeholder {
  color: #9FA19D;
}

.prompt-tip {
  margin-top: 8px;
}

.tip-text {
  font-size: 12px;
  color: #FFFFFF;
  line-height: 1.4;
}

.ai-modal-footer {
  display: flex;
  gap: 12px;
  margin-top: 24px;
  margin-bottom: 16px;
}

.ai-btn {
  flex: 1;
  padding: 8px 14px;
  border-radius: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.ai-cancel-btn {
  background: transparent;
  border: 1px solid #EEEEEE;
}

.ai-generate-btn {
  background: #A9FF00;
}

.ai-btn-text {
  font-size: 14px;
  font-weight: 400;
  line-height: 1.4;
  text-align: center;
}

.cancel-text {
  color: #666666;
}

.generate-text {
  color: #0A0D05;
}

/* AI定义按钮样式 */
.ai-define-btn {
  background: #A9FF00;
  color: #0A0D05;
}

/* 普通步骤的下一步按钮（保持原来的全宽样式） */
.normal-next-btn {
  background: #A9FF00;
  color: #0A0D05;
}

/* AI风格化步骤的导航按钮布局 */
.step-navigation-buttons {
  width: 686rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-top: 12px;
}

/* 裁剪器底部按钮（在插件 slot 中） */
.cropper-bottom-buttons {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 999;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 12px 16px;
  padding-bottom: calc(12px + env(safe-area-inset-bottom));
  background: #282828;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}



.undo-btn {
  background: #FFFFFF;
  color: #000000;
  border: 1px solid #E0E0E0;
  margin-top: 16rpx;
}

.undo-btn .btn-text {
  color: #000000;
}

/* 导航按钮基础样式 */
.nav-btn {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  padding: 8px 14px;
  border-radius: 8px;
  min-height: 36px;
}

/* 上一步按钮样式 */
.prev-btn {
  background: #FFFFFF;
  border: 1px solid #EEEEEE;
  flex: 0 0 166rpx;
}

.prev-btn .nav-btn-text {
  font-family: 'PingFang SC';
  font-style: normal;
  font-weight: 400;
  font-size: 14px;
  line-height: 20px;
  text-align: center;
  color: #666666;
}

/* 下一步按钮样式 */
.next-btn {
  background: #000000;
  flex: 1;
}

.next-btn .nav-btn-text {
  font-family: 'PingFang SC';
  font-style: normal;
  font-weight: 400;
  font-size: 14px;
  line-height: 20px;
  text-align: center;
  color: #A9FF00;
}

/* AI生成进度条弹窗样式 */
.ai-progress-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10002 !important;
  /* iOS微信端兼容性修复 */
  -webkit-transform: translate3d(0, 0, 0);
  transform: translate3d(0, 0, 0);
  -webkit-backface-visibility: hidden;
  backface-visibility: hidden;
  will-change: transform;
}

.ai-progress-content {
  width: 300px;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 20px;
}

.progress-bg-image {
  width: 120px;
  height: 120px;
  margin-bottom: 20px;
}

.progress-text-container {
  text-align: center;
  margin-bottom: 30px;
}

.progress-title {
  font-size: 18px;
  font-weight: 500;
  color: #FFFFFF;
  line-height: 1.4;
  margin-bottom: 8px;
  display: block;
}

.progress-subtitle {
  font-size: 14px;
  font-weight: 400;
  color: #9FA19D;
  line-height: 1.4;
  display: block;
}

.progress-bar-container {
  width: 100%;
  margin-bottom: 30px;
}

.progress-bar {
  width: 100%;
  height: 4px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 2px;
  overflow: hidden;
  margin-bottom: 8px;
}

.progress-fill {
  height: 100%;
  background: #A9FF00;
  border-radius: 2px;
  transition: width 0.3s ease;
}

.progress-percentage {
  font-size: 12px;
  color: #FFFFFF;
  text-align: center;
  display: block;
}

.progress-cancel-btn {
  padding: 8px 20px;
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 20px;
}

.cancel-btn-text {
  font-size: 14px;
  color: #FFFFFF;
  text-align: center;
}

/* 全局兼容性修复 - 适用于所有环境 */
/* 确保canvas元素不会覆盖fixed定位的弹窗 */
canvas {
  -webkit-transform: translateZ(0);
  transform: translateZ(0);
  position: relative;
  z-index: 1 !important;
}

/* 确保fixed定位的弹窗在最顶层 */
.ai-style-modal,
.ai-progress-modal {
  -webkit-transform: translate3d(0, 0, 0);
  transform: translate3d(0, 0, 0);
  -webkit-backface-visibility: hidden;
  backface-visibility: hidden;
  will-change: transform;
}

// 剪裁编辑器容器
.crop-editor-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  overflow: hidden;
  background-color: #000;

  /* 小程序兼容性 */
  /* #ifdef MP-WEIXIN */
  box-sizing: border-box;
  position: relative;
  /* #endif */
}

/* 加载状态样式 */
.loading-container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #F5F5F5;
  z-index: 9999;
}

.loading-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.loading-icon {
  width: 60px;
  height: 60px;
  margin-bottom: 16px;
}

.loading-text {
  font-size: 14px;
  color: #666666;
}

.main-content {
  width: 100%;
  height: 100%;
}

// 文字编辑弹窗
.text-edit-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.text-edit-content {
  width: 600rpx;
  background: #FFFFFF;
  border-radius: 24rpx;
  padding: 40rpx;
}

.text-edit-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40rpx;
}

.text-edit-title {
  font-size: 36rpx;
  font-weight: 600;
  color: #333333;
}

.text-edit-close {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  color: #999999;
}

.text-edit-section {
  margin-bottom: 32rpx;
}

.section-label {
  font-size: 28rpx;
  color: #666666;
  margin-bottom: 16rpx;
  display: block;
}

.text-input {
  width: 100%;
  height: 80rpx;
  border: 2rpx solid #E5E5E5;
  border-radius: 12rpx;
  padding: 0 24rpx;
  font-size: 28rpx;
  color: #333333;
  box-sizing: border-box;
}

.color-picker {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
}

.color-item {
  width: 60rpx;
  height: 60rpx;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2rpx solid #E5E5E5;
  position: relative;

  &.selected {
    border-color: #A9FF00;
    border-width: 4rpx;
  }
}

.color-check {
  font-size: 28rpx;
  color: #FFFFFF;
  text-shadow: 0 0 4rpx rgba(0, 0, 0, 0.5);
}

.text-edit-footer {
  display: flex;
  gap: 24rpx;
  margin-top: 40rpx;
}

.text-edit-btn {
  flex: 1;
  height: 88rpx;
  border-radius: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;

  &.cancel {
    background: #F5F5F5;
    color: #666666;
  }

  &.confirm {
    background: #A9FF00;
    color: #333333;
  }
}

</style>
