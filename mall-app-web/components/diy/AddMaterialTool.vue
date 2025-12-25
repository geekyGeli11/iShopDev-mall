<template>
  <view class="add-material-tool" v-if="visible">
    <!-- 功能选择 -->
    <view class="function-tabs">
      <view 
        v-for="(tab, index) in functionTabs" 
        :key="tab.key"
        :class="['tab-item', selectedFunction === tab.key ? 'active' : '']"
        @tap="selectFunction(tab.key)"
      >
        <image class="tab-icon" :src="tab.icon" mode="aspectFit" />
        <text class="tab-text">{{ tab.name }}</text>
      </view>
    </view>

    <!-- 添加Logo功能 -->
    <view v-if="selectedFunction === 'logo'" class="logo-section">
      <view class="section-title">
        <image class="title-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/logo.png" mode="aspectFit" />
        <text class="title-text">添加Logo</text>
      </view>
      
      <!-- Logo来源选择 -->
      <view class="logo-sources">
        <view class="source-item" @tap="selectFromLibrary">
          <image class="source-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/library.png" mode="aspectFit" />
          <text class="source-text">从素材库选择</text>
        </view>
        <view class="source-item" @tap="uploadLogo">
          <image class="source-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/upload.png" mode="aspectFit" />
          <text class="source-text">上传Logo</text>
        </view>
      </view>
      
      <!-- 常用Logo -->
      <view class="common-logos">
        <text class="common-title">常用Logo</text>
        <view class="logo-grid">
          <view 
            v-for="(logo, index) in commonLogos" 
            :key="logo.id"
            class="logo-item"
            @tap="selectLogo(logo)"
          >
            <image class="logo-image" :src="logo.url" mode="aspectFit" />
            <text class="logo-name">{{ logo.name }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 添加文字功能 -->
    <view v-if="selectedFunction === 'text'" class="text-section">
      <view class="section-title">
        <image class="title-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/text.png" mode="aspectFit" />
        <text class="title-text">添加文字</text>
      </view>
      
      <!-- 文字输入 -->
      <view class="text-input-section">
        <view class="input-container">
          <textarea 
            class="text-input"
            v-model="textContent"
            placeholder="请输入文字内容"
            :maxlength="100"
            auto-height
          />
          <view class="char-count">{{ textContent.length }}/100</view>
        </view>
        
        <!-- 快速文字 -->
        <view class="quick-texts">
          <text class="quick-title">常用文字：</text>
          <view class="text-tags">
            <view 
              v-for="(text, index) in quickTexts" 
              :key="index"
              class="text-tag"
              @tap="addQuickText(text)"
            >
              <text class="tag-text">{{ text }}</text>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 字体选择 -->
      <view class="font-selection">
        <text class="font-title">字体样式</text>
        <scroll-view class="font-scroll" scroll-x="true" show-scrollbar="false">
          <view class="font-container">
            <view 
              v-for="(font, index) in fontList" 
              :key="font.id"
              :class="['font-item', selectedFontId === font.id ? 'active' : '']"
              @tap="selectFont(font)"
            >
              <text class="font-preview" :style="{ fontFamily: font.family }">{{ font.name }}</text>
            </view>
          </view>
        </scroll-view>
      </view>
      
      <!-- 文字样式 -->
      <view class="text-style">
        <view class="style-row">
          <text class="style-label">字号</text>
          <view class="size-controls">
            <view class="size-btn" @tap="decreaseFontSize">
              <text class="size-text">-</text>
            </view>
            <text class="size-value">{{ fontSize }}</text>
            <view class="size-btn" @tap="increaseFontSize">
              <text class="size-text">+</text>
            </view>
          </view>
        </view>
        
        <view class="style-row">
          <text class="style-label">颜色</text>
          <view class="color-picker">
            <view 
              v-for="(color, index) in colorPresets" 
              :key="index"
              :class="['color-item', selectedColor === color ? 'active' : '']"
              :style="{ backgroundColor: color }"
              @tap="selectColor(color)"
            ></view>
            <view class="custom-color" @tap="showColorPicker">
              <image class="color-wheel" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/color-wheel.png" mode="aspectFit" />
            </view>
          </view>
        </view>
        
        <view class="style-row">
          <text class="style-label">样式</text>
          <view class="text-styles">
            <view 
              :class="['style-btn', isBold ? 'active' : '']"
              @tap="toggleBold"
            >
              <text class="style-icon">B</text>
            </view>
            <view 
              :class="['style-btn', isItalic ? 'active' : '']"
              @tap="toggleItalic"
            >
              <text class="style-icon">I</text>
            </view>
            <view 
              :class="['style-btn', isUnderline ? 'active' : '']"
              @tap="toggleUnderline"
            >
              <text class="style-icon">U</text>
            </view>
          </view>
        </view>
        
        <view class="style-row">
          <text class="style-label">对齐</text>
          <view class="align-controls">
            <view 
              v-for="(align, index) in alignOptions" 
              :key="align.value"
              :class="['align-btn', textAlign === align.value ? 'active' : '']"
              @tap="selectAlign(align.value)"
            >
              <image class="align-icon" :src="align.icon" mode="aspectFit" />
            </view>
          </view>
        </view>
      </view>
      
      <!-- 添加文字按钮 -->
      <view class="add-text-btn" @tap="addText">
        <text class="add-text-text">添加文字</text>
      </view>
    </view>

    <!-- 添加形状功能 -->
    <view v-if="selectedFunction === 'shape'" class="shape-section">
      <view class="section-title">
        <image class="title-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/shape.png" mode="aspectFit" />
        <text class="title-text">添加形状</text>
      </view>
      
      <view class="shape-grid">
        <view 
          v-for="(shape, index) in shapeList" 
          :key="shape.id"
          class="shape-item"
          @tap="addShape(shape)"
        >
          <image class="shape-image" :src="shape.icon" mode="aspectFit" />
          <text class="shape-name">{{ shape.name }}</text>
        </view>
      </view>
    </view>

    <!-- 颜色选择器弹窗 -->
    <view v-if="showColorPickerModal" class="color-picker-modal" @touchmove.stop.prevent @tap="hideColorPicker">
      <view class="color-picker-content" @tap.stop>
        <view class="picker-header">
          <text class="picker-title">选择颜色</text>
          <view class="close-btn" @tap="hideColorPicker">
            <image class="close-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/common/close.png" mode="aspectFit" />
          </view>
        </view>
        
        <view class="color-palette">
          <view 
            v-for="(color, index) in extendedColors" 
            :key="index"
            class="palette-color"
            :style="{ backgroundColor: color }"
            @tap="selectCustomColor(color)"
          ></view>
        </view>
        
        <view class="hex-input">
          <text class="hex-label">HEX:</text>
          <input 
            class="hex-input-field"
            v-model="customHexColor"
            placeholder="#000000"
            maxlength="7"
            @input="onHexInput"
          />
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getDIYFonts, getDIYColorPresets, uploadDIYImage } from '@/api/diy.js';

export default {
  name: 'AddMaterialTool',
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      // 功能选择
      functionTabs: [
        { key: 'logo', name: 'Logo', icon: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/tab-logo.png' },
        { key: 'text', name: '文字', icon: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/tab-text.png' },
        { key: 'shape', name: '形状', icon: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/tab-shape.png' }
      ],
      selectedFunction: 'logo',
      
      // Logo相关
      commonLogos: [
        { id: 1, name: '学校Logo', url: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/school-logo.png' },
        { id: 2, name: '品牌Logo', url: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/brand-logo.png' },
        { id: 3, name: '社团Logo', url: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/club-logo.png' }
      ],
      
      // 文字相关
      textContent: '',
      quickTexts: ['青春无悔', '梦想起航', '友谊万岁', '学无止境', '奋斗青春', '未来可期'],
      fontList: [],
      selectedFontId: null,
      fontSize: 24,
      selectedColor: '#000000',
      isBold: false,
      isItalic: false,
      isUnderline: false,
      textAlign: 'left',
      
      // 颜色相关
      colorPresets: ['#000000', '#FFFFFF', '#FF0000', '#00FF00', '#0000FF', '#FFFF00', '#FF00FF', '#00FFFF'],
      showColorPickerModal: false,
      customHexColor: '#000000',
      extendedColors: [],
      
      // 对齐选项
      alignOptions: [
        { value: 'left', icon: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/align-left.png' },
        { value: 'center', icon: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/align-center.png' },
        { value: 'right', icon: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/align-right.png' }
      ],
      
      // 形状相关
      shapeList: [
        { id: 1, name: '圆形', icon: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/shape-circle.png' },
        { id: 2, name: '矩形', icon: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/shape-rect.png' },
        { id: 3, name: '三角形', icon: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/shape-triangle.png' },
        { id: 4, name: '星形', icon: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/shape-star.png' }
      ]
    };
  },
  watch: {
    visible(newVal) {
      if (newVal) {
        this.initData();
      }
    }
  },
  methods: {
    // 初始化数据
    async initData() {
      try {
        await Promise.all([
          this.loadFonts(),
          this.loadColorPresets()
        ]);
      } catch (error) {
        console.error('初始化数据失败:', error);
      }
    },
    
    // 加载字体
    async loadFonts() {
      try {
        const result = await getDIYFonts();
        if (result && result.code === 200) {
          this.fontList = result.data || [];
          if (this.fontList.length > 0 && !this.selectedFontId) {
            this.selectedFontId = this.fontList[0].id;
          }
        }
      } catch (error) {
        console.error('加载字体失败:', error);
      }
    },
    
    // 加载颜色预设
    async loadColorPresets() {
      try {
        const result = await getDIYColorPresets();
        if (result && result.code === 200) {
          this.extendedColors = result.data || [];
        }
      } catch (error) {
        console.error('加载颜色预设失败:', error);
      }
    },
    
    // 选择功能
    selectFunction(key) {
      this.selectedFunction = key;
    },
    
    // 从素材库选择Logo
    selectFromLibrary() {
      // 这里可以打开素材库选择弹窗
      uni.showToast({
        title: '打开素材库',
        icon: 'none'
      });
    },
    
    // 上传Logo
    uploadLogo() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: async (res) => {
          try {
            uni.showLoading({ title: '上传中...' });
            
            const result = await uploadDIYImage(res.tempFilePaths[0]);
            
            if (result && result.code === 200) {
              this.$emit('add-logo', {
                src: result.data.url,
                name: '自定义Logo'
              });
              
              uni.showToast({
                title: '上传成功',
                icon: 'success'
              });
            } else {
              throw new Error(result.message || '上传失败');
            }
          } catch (error) {
            console.error('上传失败:', error);
            uni.showToast({
              title: '上传失败',
              icon: 'none'
            });
          } finally {
            uni.hideLoading();
          }
        }
      });
    },
    
    // 选择Logo
    selectLogo(logo) {
      this.$emit('add-logo', logo);
    },
    
    // 添加快速文字
    addQuickText(text) {
      this.textContent = text;
    },
    
    // 选择字体
    selectFont(font) {
      this.selectedFontId = font.id;
      this.emitTextStyleChange();
    },
    
    // 增加字号
    increaseFontSize() {
      if (this.fontSize < 72) {
        this.fontSize += 2;
        this.emitTextStyleChange();
      }
    },
    
    // 减少字号
    decreaseFontSize() {
      if (this.fontSize > 12) {
        this.fontSize -= 2;
        this.emitTextStyleChange();
      }
    },
    
    // 选择颜色
    selectColor(color) {
      this.selectedColor = color;
      this.emitTextStyleChange();
    },
    
    // 显示颜色选择器
    showColorPicker() {
      this.showColorPickerModal = true;
    },
    
    // 隐藏颜色选择器
    hideColorPicker() {
      this.showColorPickerModal = false;
    },
    
    // 选择自定义颜色
    selectCustomColor(color) {
      this.selectedColor = color;
      this.customHexColor = color;
      this.hideColorPicker();
      this.emitTextStyleChange();
    },
    
    // HEX输入
    onHexInput() {
      if (/^#[0-9A-Fa-f]{6}$/.test(this.customHexColor)) {
        this.selectedColor = this.customHexColor;
        this.emitTextStyleChange();
      }
    },
    
    // 切换粗体
    toggleBold() {
      this.isBold = !this.isBold;
      this.emitTextStyleChange();
    },
    
    // 切换斜体
    toggleItalic() {
      this.isItalic = !this.isItalic;
      this.emitTextStyleChange();
    },
    
    // 切换下划线
    toggleUnderline() {
      this.isUnderline = !this.isUnderline;
      this.emitTextStyleChange();
    },
    
    // 选择对齐方式
    selectAlign(align) {
      this.textAlign = align;
      this.emitTextStyleChange();
    },
    
    // 添加文字
    addText() {
      if (!this.textContent.trim()) {
        uni.showToast({
          title: '请输入文字内容',
          icon: 'none'
        });
        return;
      }
      
      const selectedFont = this.fontList.find(f => f.id === this.selectedFontId);
      
      this.$emit('add-text', {
        content: this.textContent,
        fontSize: this.fontSize,
        color: this.selectedColor,
        fontFamily: selectedFont ? selectedFont.family : 'Arial',
        fontWeight: this.isBold ? 'bold' : 'normal',
        fontStyle: this.isItalic ? 'italic' : 'normal',
        textDecoration: this.isUnderline ? 'underline' : 'none',
        textAlign: this.textAlign
      });
      
      // 清空输入
      this.textContent = '';
    },
    
    // 添加形状
    addShape(shape) {
      this.$emit('add-shape', shape);
    },
    
    // 发送文字样式变化事件
    emitTextStyleChange() {
      const selectedFont = this.fontList.find(f => f.id === this.selectedFontId);
      
      this.$emit('text-style-change', {
        fontSize: this.fontSize,
        color: this.selectedColor,
        fontFamily: selectedFont ? selectedFont.family : 'Arial',
        fontWeight: this.isBold ? 'bold' : 'normal',
        fontStyle: this.isItalic ? 'italic' : 'normal',
        textDecoration: this.isUnderline ? 'underline' : 'none',
        textAlign: this.textAlign
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.add-material-tool {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.function-tabs {
  display: flex;
  margin-bottom: 30rpx;
  padding: 0 20rpx;
  gap: 20rpx;
}

.tab-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  padding: 20rpx;
  border-radius: 12rpx;
  border: 2rpx solid #EEEEEE;
  transition: all 0.3s ease;
  
  &.active {
    border-color: #A7CB00;
    background-color: rgba(167, 203, 0, 0.1);
  }
}

.tab-icon {
  width: 48rpx;
  height: 48rpx;
  margin-bottom: 8rpx;
}

.tab-text {
  font-size: 24rpx;
  color: #333333;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 20rpx;
  padding: 0 20rpx;
}

.title-icon {
  width: 32rpx;
  height: 32rpx;
}

.title-text {
  font-size: 28rpx;
  font-weight: 600;
  color: #333333;
}

.logo-section,
.text-section,
.shape-section {
  flex: 1;
  overflow-y: auto;
}

.logo-sources {
  display: flex;
  gap: 20rpx;
  margin-bottom: 30rpx;
  padding: 0 20rpx;
}

.source-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30rpx 20rpx;
  background: #F8F8F8;
  border-radius: 12rpx;
  border: 2rpx dashed #CCCCCC;
}

.source-icon {
  width: 48rpx;
  height: 48rpx;
  margin-bottom: 12rpx;
}

.source-text {
  font-size: 26rpx;
  color: #666666;
}

.common-logos {
  padding: 0 20rpx;
}

.common-title {
  font-size: 26rpx;
  color: #333333;
  margin-bottom: 20rpx;
}

.logo-grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 20rpx;
}

.logo-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20rpx;
  background: #FFFFFF;
  border-radius: 12rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}

.logo-image {
  width: 80rpx;
  height: 80rpx;
  margin-bottom: 12rpx;
}

.logo-name {
  font-size: 22rpx;
  color: #333333;
  text-align: center;
}

.text-input-section {
  padding: 0 20rpx;
  margin-bottom: 30rpx;
}

.input-container {
  position: relative;
  background: #F8F8F8;
  border-radius: 12rpx;
  padding: 20rpx;
  margin-bottom: 20rpx;
}

.text-input {
  width: 100%;
  min-height: 80rpx;
  font-size: 26rpx;
  color: #333333;
  line-height: 1.5;
  background: transparent;
  border: none;
  outline: none;
}

.char-count {
  position: absolute;
  bottom: 10rpx;
  right: 15rpx;
  font-size: 20rpx;
  color: #999999;
}

.quick-texts {
  margin-top: 20rpx;
}

.quick-title {
  font-size: 24rpx;
  color: #666666;
  margin-bottom: 16rpx;
}

.text-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.text-tag {
  padding: 8rpx 16rpx;
  background: #F0F0F0;
  border-radius: 16rpx;
  border: 1rpx solid #DDDDDD;
}

.tag-text {
  font-size: 22rpx;
  color: #666666;
}

.font-selection {
  padding: 0 20rpx;
  margin-bottom: 30rpx;
}

.font-title {
  font-size: 26rpx;
  color: #333333;
  margin-bottom: 16rpx;
}

.font-scroll {
  white-space: nowrap;
}

.font-container {
  display: flex;
  gap: 16rpx;
}

.font-item {
  padding: 16rpx 24rpx;
  background: #F8F8F8;
  border-radius: 8rpx;
  border: 2rpx solid transparent;
  
  &.active {
    border-color: #A7CB00;
    background-color: rgba(167, 203, 0, 0.1);
  }
}

.font-preview {
  font-size: 24rpx;
  color: #333333;
  white-space: nowrap;
}

.text-style {
  padding: 0 20rpx;
  margin-bottom: 30rpx;
}

.style-row {
  display: flex;
  align-items: center;
  margin-bottom: 24rpx;
  gap: 20rpx;
}

.style-label {
  font-size: 26rpx;
  color: #333333;
  width: 80rpx;
  flex-shrink: 0;
}

.size-controls {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.size-btn {
  width: 48rpx;
  height: 48rpx;
  background: #F0F0F0;
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.size-text {
  font-size: 24rpx;
  color: #333333;
  font-weight: 600;
}

.size-value {
  font-size: 24rpx;
  color: #333333;
  min-width: 60rpx;
  text-align: center;
}

.color-picker {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.color-item {
  width: 40rpx;
  height: 40rpx;
  border-radius: 8rpx;
  border: 2rpx solid #DDDDDD;
  
  &.active {
    border-color: #A7CB00;
    transform: scale(1.1);
  }
}

.custom-color {
  width: 40rpx;
  height: 40rpx;
  border-radius: 8rpx;
  border: 2rpx solid #DDDDDD;
  display: flex;
  align-items: center;
  justify-content: center;
}

.color-wheel {
  width: 24rpx;
  height: 24rpx;
}

.text-styles {
  display: flex;
  gap: 12rpx;
}

.style-btn {
  width: 48rpx;
  height: 48rpx;
  background: #F0F0F0;
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2rpx solid transparent;
  
  &.active {
    border-color: #A7CB00;
    background-color: rgba(167, 203, 0, 0.1);
  }
}

.style-icon {
  font-size: 24rpx;
  color: #333333;
  font-weight: 600;
}

.align-controls {
  display: flex;
  gap: 12rpx;
}

.align-btn {
  width: 48rpx;
  height: 48rpx;
  background: #F0F0F0;
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2rpx solid transparent;
  
  &.active {
    border-color: #A7CB00;
    background-color: rgba(167, 203, 0, 0.1);
  }
}

.align-icon {
  width: 24rpx;
  height: 24rpx;
}

.add-text-btn {
  margin: 0 20rpx 20rpx;
  padding: 24rpx;
  background: linear-gradient(135deg, #A7CB00, #D7FF35);
  border-radius: 12rpx;
  text-align: center;
}

.add-text-text {
  font-size: 28rpx;
  font-weight: 600;
  color: #000000;
}

.shape-grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr 1fr;
  gap: 20rpx;
  padding: 0 20rpx;
}

.shape-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20rpx;
  background: #FFFFFF;
  border-radius: 12rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}

.shape-image {
  width: 60rpx;
  height: 60rpx;
  margin-bottom: 12rpx;
}

.shape-name {
  font-size: 22rpx;
  color: #333333;
  text-align: center;
}

.color-picker-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.color-picker-content {
  background: #FFFFFF;
  border-radius: 24rpx;
  width: 600rpx;
  max-height: 80vh;
  padding: 40rpx;
}

.picker-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 30rpx;
}

.picker-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
}

.close-btn {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-icon {
  width: 32rpx;
  height: 32rpx;
}

.color-palette {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 12rpx;
  margin-bottom: 30rpx;
}

.palette-color {
  width: 60rpx;
  height: 60rpx;
  border-radius: 8rpx;
  border: 1rpx solid #DDDDDD;
}

.hex-input {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.hex-label {
  font-size: 26rpx;
  color: #333333;
}

.hex-input-field {
  flex: 1;
  padding: 16rpx;
  background: #F8F8F8;
  border-radius: 8rpx;
  border: 1rpx solid #DDDDDD;
  font-size: 26rpx;
  color: #333333;
}
</style>
