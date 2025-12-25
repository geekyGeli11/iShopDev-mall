<template>
  <div class="app-container">
    <el-card class="operate-container" shadow="never">
      <i class="el-icon-brush" style="margin-right: 5px"></i>
      <span>主题色搭配</span>
    </el-card>
    
    <el-card shadow="never">
      <div class="theme-config">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="color-section">
              <h3>主色调配置</h3>
              <el-form :model="themeForm" label-width="120px">
                <el-form-item label="主色调:">
                  <el-color-picker v-model="themeForm.primaryColor"></el-color-picker>
                  <span class="color-value">{{themeForm.primaryColor}}</span>
                </el-form-item>
                <el-form-item label="辅助色:">
                  <el-color-picker v-model="themeForm.secondaryColor"></el-color-picker>
                  <span class="color-value">{{themeForm.secondaryColor}}</span>
                </el-form-item>
                <el-form-item label="背景色:">
                  <el-color-picker v-model="themeForm.backgroundColor"></el-color-picker>
                  <span class="color-value">{{themeForm.backgroundColor}}</span>
                </el-form-item>
                <el-form-item label="文字色:">
                  <el-color-picker v-model="themeForm.textColor"></el-color-picker>
                  <span class="color-value">{{themeForm.textColor}}</span>
                </el-form-item>
                <el-form-item label="边框色:">
                  <el-color-picker v-model="themeForm.borderColor"></el-color-picker>
                  <span class="color-value">{{themeForm.borderColor}}</span>
                </el-form-item>
              </el-form>
            </div>
          </el-col>
          
          <el-col :span="12">
            <div class="preview-section">
              <h3>预览效果</h3>
              <div class="theme-preview" :style="previewStyle">
                <div class="preview-header">
                  <span>页面标题</span>
                </div>
                <div class="preview-content">
                  <div class="preview-button" :style="buttonStyle">按钮样式</div>
                  <div class="preview-text">这是文字颜色预览</div>
                  <div class="preview-border" :style="borderStyle">边框样式预览</div>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
        
        <div class="preset-themes">
          <h3>预设主题</h3>
          <el-row :gutter="10">
            <el-col :span="4" v-for="(preset, index) in presetThemes" :key="index">
              <div class="preset-item" @click="applyPreset(preset)">
                <div class="preset-colors">
                  <div class="preset-color" :style="{backgroundColor: preset.primaryColor}"></div>
                  <div class="preset-color" :style="{backgroundColor: preset.secondaryColor}"></div>
                  <div class="preset-color" :style="{backgroundColor: preset.backgroundColor}"></div>
                </div>
                <div class="preset-name">{{preset.name}}</div>
              </div>
            </el-col>
          </el-row>
        </div>
        
        <div class="action-buttons">
          <el-button type="primary" @click="saveTheme" :loading="saving">保存配置</el-button>
          <el-button @click="resetTheme">重置</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "ThemeConfig",
  data() {
    return {
      saving: false,
      themeForm: {
        primaryColor: '#409EFF',
        secondaryColor: '#67C23A',
        backgroundColor: '#FFFFFF',
        textColor: '#303133',
        borderColor: '#DCDFE6'
      },
      presetThemes: [
        {
          name: '默认蓝',
          primaryColor: '#409EFF',
          secondaryColor: '#67C23A',
          backgroundColor: '#FFFFFF',
          textColor: '#303133',
          borderColor: '#DCDFE6'
        },
        {
          name: '商务黑',
          primaryColor: '#2C3E50',
          secondaryColor: '#34495E',
          backgroundColor: '#ECF0F1',
          textColor: '#2C3E50',
          borderColor: '#BDC3C7'
        },
        {
          name: '活力橙',
          primaryColor: '#E67E22',
          secondaryColor: '#F39C12',
          backgroundColor: '#FEF9E7',
          textColor: '#D35400',
          borderColor: '#F8C471'
        },
        {
          name: '清新绿',
          primaryColor: '#27AE60',
          secondaryColor: '#2ECC71',
          backgroundColor: '#E8F8F5',
          textColor: '#1E8449',
          borderColor: '#A9DFBF'
        },
        {
          name: '优雅紫',
          primaryColor: '#8E44AD',
          secondaryColor: '#9B59B6',
          backgroundColor: '#F4ECF7',
          textColor: '#6C3483',
          borderColor: '#D2B4DE'
        },
        {
          name: '温暖红',
          primaryColor: '#E74C3C',
          secondaryColor: '#C0392B',
          backgroundColor: '#FDEDEC',
          textColor: '#A93226',
          borderColor: '#F1948A'
        }
      ]
    }
  },
  computed: {
    previewStyle() {
      return {
        backgroundColor: this.themeForm.backgroundColor,
        color: this.themeForm.textColor,
        borderColor: this.themeForm.borderColor
      }
    },
    buttonStyle() {
      return {
        backgroundColor: this.themeForm.primaryColor,
        borderColor: this.themeForm.primaryColor
      }
    },
    borderStyle() {
      return {
        borderColor: this.themeForm.borderColor
      }
    }
  },
  created() {
    this.loadTheme();
  },
  methods: {
    loadTheme() {
      // TODO: 从后端加载当前主题配置
    },
    saveTheme() {
      this.saving = true;
      // TODO: 保存主题配置到后端
      setTimeout(() => {
        this.saving = false;
        this.$message.success('主题配置保存成功');
      }, 1000);
    },
    resetTheme() {
      this.themeForm = {
        primaryColor: '#409EFF',
        secondaryColor: '#67C23A',
        backgroundColor: '#FFFFFF',
        textColor: '#303133',
        borderColor: '#DCDFE6'
      };
    },
    applyPreset(preset) {
      this.themeForm = { ...preset };
      delete this.themeForm.name;
    }
  }
}
</script>

<style scoped>
.operate-container {
  margin-bottom: 20px;
}

.theme-config {
  padding: 20px;
}

.color-section h3,
.preview-section h3,
.preset-themes h3 {
  margin-bottom: 20px;
  color: #303133;
}

.color-value {
  margin-left: 10px;
  color: #909399;
  font-family: monospace;
}

.theme-preview {
  border: 2px solid;
  border-radius: 8px;
  padding: 20px;
  min-height: 200px;
}

.preview-header {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid;
}

.preview-button {
  display: inline-block;
  padding: 8px 16px;
  border-radius: 4px;
  color: white;
  margin-bottom: 15px;
  cursor: pointer;
}

.preview-text {
  margin-bottom: 15px;
}

.preview-border {
  border: 1px solid;
  padding: 10px;
  border-radius: 4px;
}

.preset-themes {
  margin-top: 30px;
}

.preset-item {
  text-align: center;
  cursor: pointer;
  padding: 10px;
  border-radius: 8px;
  transition: all 0.3s;
}

.preset-item:hover {
  background-color: #F5F7FA;
}

.preset-colors {
  display: flex;
  justify-content: center;
  margin-bottom: 8px;
}

.preset-color {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  margin: 0 2px;
}

.preset-name {
  font-size: 12px;
  color: #606266;
}

.action-buttons {
  margin-top: 30px;
  text-align: center;
}
</style>
