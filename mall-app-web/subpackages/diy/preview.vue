<template>
  <view class="diy-preview-container">
    <!-- 自定义导航栏 -->
    <nav-bar
      :back="true"
      :placeholder="true"
      bg-color="transparent"
      :has-slot="false"
    >
    </nav-bar>

    <!-- 预览轮播图 -->
    <view class="preview-section">
      <swiper
        class="preview-swiper"
        :current="currentFaceIndex"
        @change="onSwiperChange"
        :indicator-dots="false"
        :circular="false"
        :duration="300"
        indicator-color="transparent"
        indicator-active-color="transparent"
      >
        <swiper-item v-for="(face, index) in designFaces" :key="index" class="swiper-item">
          <view class="preview-container">
            <image
              class="preview-image"
              :src="face.previewImage || face.finalImage"
              mode="aspectFit"
              @load="onImageLoad"
              @error="onImageError"
            />

            <!-- 加载状态 -->
            <view v-if="face.isGenerating" class="generating-overlay">
              <view class="loading-spinner"></view>
              <text class="loading-text">生成预览图中...</text>
            </view>
          </view>
        </swiper-item>
      </swiper>
    </view>

    <!-- 轮播图指示器 -->
    <view class="face-indicators">
      <view
        v-for="(face, index) in designFaces"
        :key="index"
        :class="['indicator-dot', currentFaceIndex === index ? 'active' : '']"
        @tap="switchToFace(index)"
      ></view>
    </view>

    <!-- 商品信息 -->
    <view class="product-info">
      <text class="product-name">{{ productInfo.name || '商品名称商品名称商品名称' }}</text>
    </view>

    <!-- 设计详情 -->
    <view class="design-details">
      <view class="detail-item">
        <text class="detail-label">使用模型</text>
        <text class="detail-value">{{ styleInfo.name || '默认风格名称' }}</text>
      </view>
      <view class="detail-item">
        <text class="detail-label">使用元素</text>
        <text class="detail-value">{{ getElementsList() }}</text>
      </view>
      <view class="detail-item-vertical">
        <text class="detail-label">画面描述</text>
        <text class="detail-description">{{ userPrompt || '内容内容内容内容内容内容内容内容内容内容内容' }}</text>
      </view>
    </view>

    <!-- 底部操作栏 -->
    <view class="bottom-actions">
      <view class="secondary-btn" @tap="saveAsDraft">
        <text class="btn-text">存草稿</text>
      </view>

      <view class="primary-btn" @tap="proceedToOrder">
        <text class="primary-btn-text">立即下单(¥{{ calculatedPrice.toFixed(2) }})</text>
      </view>
    </view>

    <!-- 生成预览图弹窗 -->
    <view v-if="showGenerateModal" class="generate-modal" @touchmove.stop.prevent>
      <view class="generate-content">
        <view class="generate-header">
          <image class="generate-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/generate.png" mode="aspectFit" />
          <text class="generate-title">正在生成预览图</text>
        </view>
        
        <view class="generate-progress">
          <view class="progress-bar">
            <view class="progress-fill" :style="{ width: generateProgress + '%' }"></view>
          </view>
          <text class="progress-text">{{ generateProgress.toFixed(2) }}%</text>
        </view>
        
        <view class="generate-tips">
          <text class="tip-text">正在为您生成高清预览图，请稍候...</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import NavBar from '@/components/nav-bar.vue';
import { generateDIYPreview, calculateDIYPrice, saveDIYDesign, getDIYDesign } from '@/api/diy.js';

export default {
  name: 'DIYPreview',
  components: {
    'nav-bar': NavBar
  },
  data() {
    return {
      // 基础数据
      productId: '',
      templateId: '',
      designId: '',
      productInfo: {},
      templateInfo: {},
      designData: null,
      designFaces: [],
      currentFaceIndex: 0,

      // 新增字段
      styleInfo: {},
      userPrompt: '',
      elementsList: [],

      // 价格相关
      calculatedPrice: 0,
      basePrice: 0,

      // 生成状态
      showGenerateModal: false,
      generateProgress: 0,
      isGenerating: false,

      // 其他
      createTime: new Date(),
      savedDraftId: null
    };
  },
  async onLoad(options) {
    try {
      this.productId = options.productId;
      this.templateId = options.templateId;
      this.designId = options.designId;

      // 接收从定制页传递的额外参数
      console.log('接收到的原始参数:', options);

      if (options.productInfo) {
        try {
          // uni-app已经自动解码一次，直接解析JSON
          this.productInfo = JSON.parse(options.productInfo);
          console.log('解析商品信息成功:', this.productInfo);
        } catch (error) {
          console.error('解析商品信息失败:', error, 'raw:', options.productInfo);
          // 如果直接解析失败，尝试解码后再解析
          try {
            this.productInfo = JSON.parse(decodeURIComponent(options.productInfo));
            console.log('二次解析商品信息成功:', this.productInfo);
          } catch (error2) {
            console.error('二次解析商品信息也失败:', error2);
            this.productInfo = {};
          }
        }
      }

      if (options.styleInfo) {
        try {
          this.styleInfo = JSON.parse(options.styleInfo);
          console.log('解析风格信息成功:', this.styleInfo);
        } catch (error) {
          console.error('解析风格信息失败:', error, 'raw:', options.styleInfo);
          try {
            this.styleInfo = JSON.parse(decodeURIComponent(options.styleInfo));
            console.log('二次解析风格信息成功:', this.styleInfo);
          } catch (error2) {
            console.error('二次解析风格信息也失败:', error2);
            this.styleInfo = { name: '默认风格名称' };
          }
        }
      }

      if (options.userPrompt) {
        try {
          this.userPrompt = options.userPrompt;
          console.log('解析用户提示词成功:', this.userPrompt);
        } catch (error) {
          console.error('解析用户提示词失败:', error, 'raw:', options.userPrompt);
          this.userPrompt = '用户输入的设计要求';
        }
      }

      if (options.elementsList) {
        try {
          this.elementsList = JSON.parse(options.elementsList);
          console.log('解析元素列表成功:', this.elementsList);
        } catch (error) {
          console.error('解析元素列表失败:', error, 'raw:', options.elementsList);
          try {
            this.elementsList = JSON.parse(decodeURIComponent(options.elementsList));
            console.log('二次解析元素列表成功:', this.elementsList);
          } catch (error2) {
            console.error('二次解析元素列表也失败:', error2);
            // 解析失败时设置为空数组，而不是默认值
            this.elementsList = [];
          }
        }
      }

      if (options.designId) {
        // 通过设计ID加载设计数据
        await this.loadDesignById(options.designId);
      } else if (options.designData) {
        // 直接传递设计数据（兼容旧方式）
        this.designData = JSON.parse(decodeURIComponent(options.designData));
        this.initPreview();
      } else {
        uni.showToast({
          title: '设计数据不存在',
          icon: 'none'
        });
        setTimeout(() => {
          uni.navigateBack();
        }, 1500);
      }
    } catch (error) {
      console.error('页面初始化失败:', error);
      uni.showToast({
        title: '页面初始化失败',
        icon: 'none'
      });
    }
  },
  methods: {
    // 通过设计ID加载设计数据
    async loadDesignById(designId) {
      try {
        uni.showLoading({
          title: '加载设计数据...'
        });

        const response = await getDIYDesign(designId);

        if (response && response.code === 200 && response.data) {
          // 解析设计数据
          const designRecord = response.data;
          this.designData = JSON.parse(designRecord.designData);

          // 初始化预览
          this.initPreview();
        } else {
          throw new Error(response.message || '获取设计数据失败');
        }
      } catch (error) {
        console.error('加载设计数据失败:', error);
        uni.showToast({
          title: '设计数据加载失败',
          icon: 'none'
        });
        setTimeout(() => {
          uni.navigateBack();
        }, 1500);
      } finally {
        uni.hideLoading();
      }
    },

    // 初始化预览
    async initPreview() {
      try {
        // 设置基础数据
        this.designFaces = this.designData.faces || [];

        // 只在URL参数没有提供时，才从designData中读取
        // 优先使用URL参数传递的数据（从customize页面传递）
        if (!this.productInfo || Object.keys(this.productInfo).length === 0) {
          console.log('从 designData 中读取商品信息');
          this.productInfo = this.designData.productInfo || {};
        } else {
          console.log('使用 URL 参数传递的商品信息:', this.productInfo);
        }

        if (!this.styleInfo || Object.keys(this.styleInfo).length === 0) {
          console.log('从 designData 中读取风格信息');
          this.styleInfo = this.designData.styleInfo || {};
        } else {
          console.log('使用 URL 参数传递的风格信息:', this.styleInfo);
        }

        if (!this.userPrompt || this.userPrompt === '') {
          console.log('从 designData 中读取用户提示词');
          this.userPrompt = this.designData.userPrompt || '';
        } else {
          console.log('使用 URL 参数传递的用户提示词:', this.userPrompt);
        }

        if (!this.elementsList || this.elementsList.length === 0) {
          console.log('从 designData 中读取元素列表');
          this.elementsList = this.designData.elementsList || [];
        } else {
          console.log('使用 URL 参数传递的元素列表:', this.elementsList);
        }

        this.basePrice = this.productInfo.price || 0;

        // 生成预览图
        await this.generatePreviewImages();

        // 计算价格
        await this.calculatePrice();

      } catch (error) {
        console.error('初始化预览失败:', error);
      }
    },
    
    // 生成预览图
    async generatePreviewImages() {
      try {
        this.showGenerateModal = true;
        this.generateProgress = 0;
        this.isGenerating = true;

        // 模拟进度
        const progressInterval = setInterval(() => {
          if (this.generateProgress < 90) {
            this.generateProgress += Math.random() * 10;
          }
        }, 200);

        // 调用生成接口 - 修复参数传递
        const result = await generateDIYPreview({
          templateId: parseInt(this.templateId),
          designData: JSON.stringify(this.designData),
          productId: parseInt(this.productId)
        });

        clearInterval(progressInterval);
        this.generateProgress = 100;

        if (result && result.code === 200) {
          // 更新预览图 - 支持新的多面预览格式
          const responseData = result.data;

          if (responseData.previewImages && Array.isArray(responseData.previewImages)) {
            // 新格式：多面预览图数组
            responseData.previewImages.forEach((faceResult, index) => {
              if (this.designFaces[index]) {
                this.designFaces[index].finalImage = faceResult.previewImageUrl;
                this.designFaces[index].hdImage = faceResult.hdPreviewImageUrl;
                this.designFaces[index].isGenerating = false;
                this.designFaces[index].status = faceResult.status;
              }
            });
          } else if (responseData.previewImageUrl) {
            // 兼容旧格式：单张预览图
            if (this.designFaces.length > 0) {
              this.designFaces[0].finalImage = responseData.previewImageUrl;
              this.designFaces[0].hdImage = responseData.hdPreviewImageUrl;
              this.designFaces[0].isGenerating = false;
            }
          }

          setTimeout(() => {
            this.showGenerateModal = false;
          }, 500);
        } else {
          throw new Error(result.message || '生成预览图失败');
        }

      } catch (error) {
        console.error('生成预览图失败:', error);
        this.showGenerateModal = false;

        // 使用默认预览图
        this.designFaces.forEach(face => {
          face.isGenerating = false;
          if (!face.finalImage) {
            face.finalImage = face.previewImage;
          }
        });

        uni.showToast({
          title: '预览图生成失败，使用默认预览',
          icon: 'none'
        });
      } finally {
        this.isGenerating = false;
      }
    },
    
    // 计算价格
    async calculatePrice() {
      try {
        // 检查必需参数
        if (!this.designId) {
          console.warn('缺少designId，使用基础价格计算');
          // 降级处理：前端简单计算 DIY价格 = 基础价格 * 1.5
          this.calculatedPrice = parseFloat((this.basePrice * 1.5).toFixed(2));
          return;
        }

        console.log('调用后端价格计算接口，参数:', {
          designId: this.designId,
          productId: this.productId,
          quantity: 1
        });

        // 调用后端价格计算接口 - 使用与order页面相同的参数
        const result = await calculateDIYPrice({
          productId: parseInt(this.productId),
          designId: parseInt(this.designId),
          quantity: 1, // 预览阶段默认数量为1
          memberReceiveAddressId: 1, // 预览阶段使用默认地址ID
          deliveryType: 1, // 预览阶段使用默认配送方式（标准快递）
          couponId: null, // 预览阶段不使用优惠券
          useIntegration: 0, // 预览阶段不使用积分
          schoolId: null // 预览阶段不指定学校
        });

        if (result && result.code === 200) {
          this.calculatedPrice = parseFloat((result.data || this.basePrice * 1.5).toFixed(2));
          console.log('后端价格计算成功:', {
            originalPrice: this.basePrice,
            calculatedPrice: this.calculatedPrice,
            apiResult: result.data
          });
        } else {
          console.warn('价格计算API返回错误:', result);
          // 降级处理：前端简单计算
          this.calculatedPrice = parseFloat((this.basePrice * 1.5).toFixed(2));
        }
      } catch (error) {
        console.error('计算价格失败:', error);
        // 降级处理：前端简单计算 DIY价格 = 基础价格 * 1.5
        this.calculatedPrice = parseFloat((this.basePrice * 1.5).toFixed(2));
      }
    },
    
    // 切换面
    switchToFace(index) {
      this.currentFaceIndex = index;
    },
    
    // 轮播变化
    onSwiperChange(e) {
      this.currentFaceIndex = e.detail.current;
    },
    
    // 上一个面
    prevFace() {
      if (this.currentFaceIndex > 0) {
        this.currentFaceIndex--;
      }
    },
    
    // 下一个面
    nextFace() {
      if (this.currentFaceIndex < this.designFaces.length - 1) {
        this.currentFaceIndex++;
      }
    },
    

    
    // 图片加载完成
    onImageLoad() {
      // 图片加载完成处理
    },
    
    // 图片加载错误
    onImageError() {
      // 图片加载错误处理
    },
    
    // 获取总元素数
    getTotalElements() {
      return this.designFaces.reduce((total, face) => {
        return total + (face.elements ? face.elements.length : 0);
      }, 0);
    },

    // 获取元素描述
    getElementsDescription() {
      if (!this.designData || !this.designData.faces) {
        return '文字 | 图片';
      }

      const elementTypes = new Set();
      this.designData.faces.forEach(face => {
        if (face.elements) {
          face.elements.forEach(element => {
            if (element.type === 'text') elementTypes.add('文字');
            if (element.type === 'image') elementTypes.add('图片');
            if (element.type === 'shape') elementTypes.add('形状');
          });
        }
      });

      return Array.from(elementTypes).join(' | ') || '文字 | 图片';
    },

    // 获取元素列表（数组格式）
    getElementsList() {
      console.log('获取元素列表，elementsList:', this.elementsList);

      // 优先使用从定制页面传递过来的元素列表
      if (this.elementsList && this.elementsList.length > 0) {
        return this.elementsList.join('、');
      }

      // 如果没有传递元素列表，尝试从设计数据中提取
      if (!this.designData || !this.designData.faces) {
        return '暂无元素';
      }

      const elementNames = [];
      this.designData.faces.forEach(face => {
        if (face.elements) {
          face.elements.forEach(element => {
            if (element.name) {
              elementNames.push(element.name);
            } else if (element.type === 'text') {
              elementNames.push('文字元素');
            } else if (element.type === 'image') {
              elementNames.push('图片元素');
            } else if (element.type === 'shape') {
              elementNames.push('形状元素');
            }
          });
        }
      });

      // 如果没有找到任何元素，显示"暂无元素"
      return elementNames.length > 0 ? elementNames.join('、') : '暂无元素';
    },

    // 获取设计描述
    getDesignDescription() {
      if (!this.designData) {
        return '个性化定制设计，展现独特风格与创意表达';
      }

      const elementCount = this.getTotalElements();
      const faceCount = this.designFaces.length;

      return `包含${faceCount}个定制面，共${elementCount}个设计元素，融合个人风格与创意理念的独特定制作品`;
    },
    
    // 格式化时间
    formatTime(time) {
      const date = new Date(time);
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
    },
    
    // 编辑设计
    editDesign() {
      uni.navigateBack();
    },
    
    // 继续编辑
    continueEdit() {
      uni.navigateBack();
    },
    
    // 保存草稿
    async saveAsDraft() {
      try {
        uni.showLoading({ title: '保存中...' });

        // 验证必要参数
        if (!this.productId) {
          throw new Error('商品ID不能为空');
        }
        if (!this.templateId) {
          throw new Error('模板ID不能为空');
        }
        if (!this.designFaces || this.designFaces.length === 0) {
          throw new Error('设计数据不能为空');
        }

        // 检查预览图生成状态
        const hasGeneratingFaces = this.designFaces.some(face => face.isGenerating);
        if (hasGeneratingFaces) {
          const shouldWait = await this.confirmWaitForPreview();
          if (shouldWait) {
            await this.waitForPreviewGeneration();
          }
        }

        // 收集预览图数据
        const previewImageData = this.collectPreviewImageData();

        // 构建完整的设计数据，包含预览图信息
        const designData = {
          faces: this.designFaces.map(face => ({
            faceId: face.faceId || face.id,
            faceName: face.faceName || face.name,
            originalImage: face.originalImage || face.image,
            aiGeneratedImageUrl: face.aiGeneratedImageUrl || face.previewImage,
            // 预览图相关字段
            previewImageUrl: face.finalImage || face.previewImage,
            finalImage: face.finalImage,
            hdImage: face.hdImage,
            isPreviewGenerated: !!(face.finalImage || face.previewImage),
            previewStatus: face.status || 'unknown',
            // 其他字段
            elements: face.elements || [],
            completed: face.completed || false,
            isGenerating: face.isGenerating || false
          })),
          productId: this.productId,
          templateId: this.templateId,
          createTime: new Date().toISOString(),
          // 预览图汇总信息
          previewImageSummary: previewImageData
        };

        // 收集预览图URL数组
        const previewImages = this.designFaces.map(face => ({
          faceId: face.faceId || face.id,
          previewImageUrl: face.finalImage || face.previewImage,
          hdImageUrl: face.hdImage,
          status: face.status || 'unknown'
        })).filter(item => item.previewImageUrl); // 过滤掉没有预览图的面

        // 构建保存参数
        const saveParams = {
          productId: this.productId,
          templateId: this.templateId,
          designData: JSON.stringify(designData),
          previewImages: JSON.stringify(previewImages), // 新增预览图数组
          isDraft: true
        };

        // 如果存在设计ID，则更新现有记录
        if (this.designId) {
          saveParams.designId = this.designId;
        }

        console.log('保存草稿数据（包含预览图）:', {
          ...saveParams,
          isUpdate: !!this.designId,
          previewImageCount: previewImages.length,
          generatedCount: previewImageData.generatedCount
        });

        const result = await saveDIYDesign(saveParams);

        if (result && result.code === 200) {
          // 更新设计ID（用于后续更新操作）
          if (result.data && (typeof result.data === 'number' || result.data.designId)) {
            this.designId = result.data.designId || result.data;
            this.savedDraftId = this.designId;
          }

          // 确保先隐藏loading再显示成功提示
          uni.hideLoading();

          setTimeout(() => {
            uni.showToast({
              title: this.designId ? '草稿更新成功' : '草稿保存成功',
              icon: 'success',
              duration: 2000
            });
          }, 100);

          console.log('草稿保存成功，设计ID:', this.designId);
        } else {
          throw new Error(result.message || '保存失败');
        }
      } catch (error) {
        console.error('保存草稿失败:', error);

        // 确保先隐藏loading再显示错误提示
        uni.hideLoading();

        setTimeout(() => {
          uni.showToast({
            title: error.message || '保存失败',
            icon: 'none',
            duration: 2000
          });
        }, 100);
      }
    },

    // 收集预览图数据
    collectPreviewImageData() {
      const previewData = {
        totalImages: this.designFaces.length,
        generatedCount: 0,
        pendingCount: 0,
        failedCount: 0,
        images: []
      };

      this.designFaces.forEach((face) => {
        const imageInfo = {
          faceId: face.faceId || face.id,
          faceName: face.faceName || face.name,
          previewImageUrl: face.finalImage || face.previewImage,
          hdImageUrl: face.hdImage,
          isGenerated: !!(face.finalImage || face.previewImage),
          isGenerating: face.isGenerating || false,
          status: face.status || 'unknown'
        };

        previewData.images.push(imageInfo);

        // 统计状态
        if (imageInfo.isGenerated && !imageInfo.isGenerating) {
          previewData.generatedCount++;
        } else if (imageInfo.isGenerating) {
          previewData.pendingCount++;
        } else {
          previewData.failedCount++;
        }
      });

      console.log('预览图数据收集完成:', previewData);
      return previewData;
    },

    // 确认是否等待预览图生成
    async confirmWaitForPreview() {
      return new Promise((resolve) => {
        uni.showModal({
          title: '预览图生成中',
          content: '部分预览图正在生成中，是否等待生成完成后再保存草稿？',
          confirmText: '等待完成',
          cancelText: '直接保存',
          success: (res) => {
            resolve(res.confirm);
          },
          fail: () => {
            resolve(false);
          }
        });
      });
    },

    // 等待预览图生成完成
    async waitForPreviewGeneration() {
      const maxWaitTime = 30000; // 最大等待30秒
      const checkInterval = 1000; // 每秒检查一次
      let waitedTime = 0;

      return new Promise((resolve) => {
        const checkGeneration = () => {
          const hasGenerating = this.designFaces.some(face => face.isGenerating);

          if (!hasGenerating) {
            console.log('所有预览图生成完成');
            resolve(true);
            return;
          }

          waitedTime += checkInterval;
          if (waitedTime >= maxWaitTime) {
            console.log('等待预览图生成超时');
            uni.showToast({
              title: '预览图生成超时，将保存当前状态',
              icon: 'none'
            });
            resolve(false);
            return;
          }

          // 更新等待提示
          const remainingTime = Math.ceil((maxWaitTime - waitedTime) / 1000);
          uni.showLoading({
            title: `等待预览图生成中... ${remainingTime}s`
          });

          setTimeout(checkGeneration, checkInterval);
        };

        checkGeneration();
      });
    },

    // 分享设计
    shareDesign() {
      uni.showActionSheet({
        itemList: ['分享到微信', '分享到朋友圈', '复制链接'],
        success: (res) => {
          switch (res.tapIndex) {
            case 0:
              this.shareToWeChat();
              break;
            case 1:
              this.shareToMoments();
              break;
            case 2:
              this.copyShareLink();
              break;
          }
        }
      });
    },
    
    // 分享到微信
    shareToWeChat() {
      // 实现微信分享
      uni.showToast({
        title: '分享到微信',
        icon: 'none'
      });
    },
    
    // 分享到朋友圈
    shareToMoments() {
      // 实现朋友圈分享
      uni.showToast({
        title: '分享到朋友圈',
        icon: 'none'
      });
    },
    
    // 复制分享链接
    copyShareLink() {
      uni.setClipboardData({
        data: `我设计了一个超棒的定制商品，快来看看吧！`,
        success: () => {
          uni.showToast({
            title: '链接已复制',
            icon: 'success'
          });
        }
      });
    },
    
    // 保存到相册
    saveToAlbum() {
      // 这里可以实现保存预览图到相册的功能
      uni.showToast({
        title: '保存到相册功能开发中',
        icon: 'none'
      });
    },
    
    // 立即下单
    proceedToOrder() {
      try {
        const orderData = {
          productId: this.productId,
          designId: this.designId, // 添加设计ID
          templateId: this.templateId, // 添加模板ID
          designData: this.designData,
          calculatedPrice: this.calculatedPrice,
          designFaces: this.designFaces
        };

        console.log('传递给订单页面的数据:', orderData);

        const queryString = `orderData=${encodeURIComponent(JSON.stringify(orderData))}`;

        uni.navigateTo({
          url: `/subpackages/diy/order?${queryString}`
        });
      } catch (error) {
        console.error('跳转下单页面失败:', error);
        uni.showToast({
          title: '跳转失败',
          icon: 'none'
        });
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.diy-preview-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #FFFFFF;
}

.product-info {
  background: #FFFFFF;
  padding: 30rpx;
  text-align: left;
}

.product-name {
  font-size: 32rpx;
  font-weight: 400;
  color: #333333;
  line-height: 1.4;
}

.preview-section {
  background: #FFFFFF;
  height: 462px;
  position: relative;
}

.preview-swiper {
  height: 100%;
}

/* 隐藏swiper默认指示器 - 小程序兼容方式 */
.preview-swiper .uni-swiper-dots,
.preview-swiper .uni-swiper-dot {
  display: none !important;
  opacity: 0 !important;
  visibility: hidden !important;
  width: 0 !important;
  height: 0 !important;
}

/* 针对可能的其他指示器样式 */
.preview-swiper .swiper-pagination-bullets,
.preview-swiper .swiper-pagination-fraction,
.preview-swiper .swiper-pagination-custom {
  display: none !important;
}

.swiper-item {
  height: 100%;
}

.preview-container {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40rpx;
}

.preview-image {
  width: 100%;
  height: 100%;
  max-width: 500rpx;
  max-height: 500rpx;
}

.face-indicators {
  display: flex;
  justify-content: center;
  gap: 12rpx;
  padding: 20rpx;
  background: #FFFFFF;
}

.indicator-dot {
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
  background: #EEEEEE;
  transition: all 0.3s ease;

  &.active {
    background: #A9FF00;
    transform: scale(1.2);
  }
}

.generating-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 16rpx;
}

.loading-spinner {
  width: 60rpx;
  height: 60rpx;
  border: 4rpx solid #EEEEEE;
  border-top: 4rpx solid #A7CB00;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20rpx;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text {
  font-size: 26rpx;
  color: #666666;
}



.design-details {
  background: #FFFFFF;
  padding: 30rpx;
  flex: 1;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 32rpx;
  margin-bottom: 16rpx;
}

.detail-item-vertical {
  display: flex;
  flex-direction: row;
  gap: 32rpx;
  align-items: flex-start;
  margin-bottom: 16rpx;
}

.detail-label {
  font-size: 28rpx;
  color: #666666;
  font-weight: 400;
  min-width: 120rpx;
}

.detail-value {
  font-size: 28rpx;
  color: #333333;
  font-weight: 400;
}

.detail-description {
  font-size: 28rpx;
  color: #333333;
  font-weight: 400;
  line-height: 1.5;
  flex: 1;
}

.bottom-actions {
  background: #FFFFFF;
  padding: 30rpx;
  margin-bottom: 80rpx;
  display: flex;
  gap: 20rpx;
  align-items: center;
  border-top: 1rpx solid #EEEEEE;
}

.secondary-btn {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 28rpx;
  background: #F5F5F5;
  border-radius: 16rpx;
  min-width: 120rpx;
}

.btn-text {
  font-size: 28rpx;
  color: #666666;
  font-weight: 400;
}

.primary-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 28rpx;
  background: #282921;
  border-radius: 16rpx;
}

.primary-btn-text {
  font-size: 28rpx;
  font-weight: 400;
  color: #A9FF00;
}

.generate-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.generate-content {
  background: #FFFFFF;
  border-radius: 24rpx;
  width: 600rpx;
  padding: 60rpx 40rpx 40rpx;
}

.generate-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 40rpx;
}

.generate-icon {
  width: 80rpx;
  height: 80rpx;
  margin-bottom: 20rpx;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.generate-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
}

.generate-progress {
  display: flex;
  align-items: center;
  gap: 20rpx;
  margin-bottom: 30rpx;
}

.progress-bar {
  flex: 1;
  height: 12rpx;
  background: #EEEEEE;
  border-radius: 6rpx;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #A7CB00, #D7FF35);
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 24rpx;
  color: #A7CB00;
  font-weight: 600;
  width: 80rpx;
  text-align: right;
}

.generate-tips {
  text-align: center;
}

.tip-text {
  font-size: 26rpx;
  color: #666666;
  line-height: 1.5;
}
</style>
