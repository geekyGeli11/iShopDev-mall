<template>
  <el-card class="form-container" shadow="never">
    <el-form :model="styleModel"
             :rules="rules"
             ref="styleModelFrom"
             label-width="150px">
      <el-form-item label="风格名称：" prop="name">
        <el-input v-model="styleModel.name" class="input-width"></el-input>
      </el-form-item>
      <el-form-item label="封面图片：" prop="coverImage">
        <single-upload v-model="styleModel.coverImage"></single-upload>
      </el-form-item>
      <el-form-item label="内容图片：" prop="bannerImage">
        <single-upload
          v-model="styleModel.bannerImage"
          @input="handleBannerImageChange"
          :enable-crop="true"
          :crop-aspect-ratio="[1, 1]"
          upload-tip="请上传图片，支持 JPG/PNG 格式，将自动裁剪为 1:1 比例"
        ></single-upload>
      </el-form-item>
      <el-form-item label="功能类型：" prop="functionType">
        <el-radio-group v-model="selectedFunctionCategory" @change="handleFunctionCategoryChange">
          <el-radio-button
            v-for="category in functionCategories"
            :key="category.value"
            :label="category.value">
            {{ category.label }}
          </el-radio-button>
        </el-radio-group>
        <div style="margin-top: 15px; color: #606266; font-size: 13px;">
          {{ selectedCategoryInfo ? selectedCategoryInfo.desc : '' }}
        </div>
      </el-form-item>

      <!-- 二级风格选择（卡片形式） -->
      <el-form-item label="具体风格：" v-if="currentStyleOptions.length > 0">
        <div class="style-cards-container">
          <div
            v-for="style in currentStyleOptions"
            :key="style.value"
            :class="['style-card', { 'active': selectedStyleValue === style.value }]"
            @click="selectStyle(style)">
            <div class="style-card-title">{{ style.label }}</div>
            <div class="style-card-desc">{{ style.desc }}</div>
          </div>
        </div>
      </el-form-item>

      <el-form-item label="风格提示词：" prop="stylePrompt">
        <el-input
          v-model="stylePrompt"
          class="input-width"
          :placeholder="descriptionPlaceholder"
          :disabled="autoUpdateStylePrompt">
        </el-input>
        <el-checkbox v-model="autoUpdateStylePrompt" style="margin-top: 10px;">
          自动更新风格提示词（根据选择的具体风格）
        </el-checkbox>
        <div style="margin-top: 10px; color: #909399; font-size: 12px;">
          提示：此为风格转换的提示词前缀，如"转换成法国绘本风格"
        </div>
      </el-form-item>

      <el-form-item label="图片风格描述：" prop="styleDescription">
        <el-input
          type="textarea"
          :rows="5"
          v-model="styleDescription"
          class="input-width"
          placeholder="上传内容图片后将自动提取风格描述">
        </el-input>
        <div style="margin-top: 10px; color: #909399; font-size: 12px;">
          提示：此为图片的详细风格描述，上传内容图片后自动提取，也可手动编辑
        </div>
      </el-form-item>

      <el-form-item label="完整风格介绍：">
        <div style="padding: 10px; background-color: #F5F7FA; border-radius: 4px; color: #606266; line-height: 1.8;">
          <div v-if="mergedDescription" style="white-space: pre-wrap;">{{ mergedDescription }}</div>
          <div v-else style="color: #C0C4CC;">请选择具体风格或输入风格描述</div>
        </div>
        <div style="margin-top: 10px; color: #909399; font-size: 12px;">
          提示：提交时将合并为"风格提示词：图片风格描述"的格式
        </div>
      </el-form-item>
      <el-form-item label="排序：" prop="sort">
        <el-input v-model="styleModel.sort" class="input-width"></el-input>
      </el-form-item>
      <el-form-item label="是否启用：" prop="status">
        <el-radio-group v-model="styleModel.status">
          <el-radio :label="1">是</el-radio>
          <el-radio :label="0">否</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit('styleModelFrom')">提交</el-button>
        <el-button v-if="!isEdit" @click="resetForm('styleModelFrom')">重置</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
import {createStyleModel, updateStyleModel, getStyleModel, extractStyleDescription} from '@/api/styleModel'
import SingleUpload from '@/components/Upload/singleUpload'

const defaultStyleModel = {
  name: null,
  description: null,
  functionType: 'doodle',
  coverImage: null,
  bannerImage: null,
  sort: 0,
  status: 1
};

export default {
  name: "StyleModelDetail",
  components: {SingleUpload},
  props: {
    isEdit: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      styleModel: Object.assign({}, defaultStyleModel),
      rules: {
        name: [
          {required: true, message: '请输入风格名称', trigger: 'blur'},
          {min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur'}
        ],
        functionType: [
          {required: true, message: '请选择功能类型', trigger: 'change'}
        ],
        coverImage: [
          {required: true, message: '请上传封面图片', trigger: 'change'}
        ],
        bannerImage: [
          {required: true, message: '请上传内容图片', trigger: 'change'}
        ],
        description: [
          {required: true, message: '请输入风格介绍', trigger: 'blur'}
        ],
        sort: [
          {required: true, message: '请输入排序', trigger: 'blur'},
          {pattern: /^\d+$/, message: '排序必须为数字', trigger: 'blur'}
        ],
        status: [
          {required: true, message: '请选择是否启用', trigger: 'change'}
        ]
      },
      // 当前选中的功能分类
      selectedFunctionCategory: 'creative',
      // 当前选中的具体风格值（用于UI显示）
      selectedStyleValue: null,
      // 风格提示词（前缀）
      stylePrompt: '',
      // 图片风格描述（自动提取）
      styleDescription: '',
      // 是否自动更新风格提示词
      autoUpdateStylePrompt: true,
      // 功能分类（一级）
      functionCategories: [
        {
          value: 'creative',
          label: '创意生成',
          desc: '基于线稿或参考生成图片'
        },
        {
          value: 'stylization',
          label: '风格化',
          desc: '将图片转换为特定艺术风格'
        },
        {
          value: 'edit',
          label: '编辑修改',
          desc: '对图片进行编辑和修改'
        },
        {
          value: 'enhance',
          label: '增强处理',
          desc: '提升图片质量和效果'
        }
      ],
      // 具体风格选项（二级）
      styleOptionsMap: {
        // 风格化类
        'stylization': [
          {
            value: 'stylization_all_french',
            label: '法国绘本风格',
            desc: '温馨浪漫的法式绘本艺术',
            icon: 'el-icon-picture-outline',
            functionType: 'stylization_all',
            promptPrefix: '转换成法国绘本风格'
          },
          {
            value: 'stylization_all_gold',
            label: '金箔艺术风格',
            desc: '华丽璀璨的金箔装饰艺术',
            icon: 'el-icon-medal',
            functionType: 'stylization_all',
            promptPrefix: '转换成金箔艺术风格'
          },
          {
            value: 'stylization_local_ice',
            label: '冰雕风格',
            desc: '晶莹剔透的冰雕效果',
            icon: 'el-icon-ice-cream-square',
            functionType: 'stylization_local',
            promptPrefix: '变成冰雕风格'
          },
          {
            value: 'stylization_local_cloud',
            label: '云朵风格',
            desc: '轻盈柔软的云朵质感',
            icon: 'el-icon-partly-cloudy',
            functionType: 'stylization_local',
            promptPrefix: '变成云朵风格'
          },
          {
            value: 'stylization_local_lantern',
            label: '花灯风格',
            desc: '喜庆传统的中式花灯',
            icon: 'el-icon-light',
            functionType: 'stylization_local',
            promptPrefix: '变成花灯风格'
          },
          {
            value: 'stylization_local_wooden',
            label: '木板风格',
            desc: '自然质朴的木质纹理',
            icon: 'el-icon-box',
            functionType: 'stylization_local',
            promptPrefix: '变成木板风格'
          },
          {
            value: 'stylization_local_porcelain',
            label: '青花瓷风格',
            desc: '典雅精致的青花瓷艺术',
            icon: 'el-icon-coffee-cup',
            functionType: 'stylization_local',
            promptPrefix: '变成青花瓷风格'
          },
          {
            value: 'stylization_local_fluffy',
            label: '毛茸茸风格',
            desc: '可爱柔软的毛绒质感',
            icon: 'el-icon-orange',
            functionType: 'stylization_local',
            promptPrefix: '变成毛茸茸风格'
          },
          {
            value: 'stylization_local_weaving',
            label: '毛线风格',
            desc: '温暖编织的毛线效果',
            icon: 'el-icon-present',
            functionType: 'stylization_local',
            promptPrefix: '变成毛线风格'
          },
          {
            value: 'stylization_local_balloon',
            label: '气球风格',
            desc: '轻盈飘逸的气球造型',
            icon: 'el-icon-trophy',
            functionType: 'stylization_local',
            promptPrefix: '变成气球风格'
          }
        ],
        // 编辑修改类
        'edit': [
          {
            value: 'description_edit',
            label: '指令编辑',
            desc: '自由添加/修改元素',
            icon: 'el-icon-edit',
            functionType: 'description_edit',
            promptPrefix: ''
          },
          {
            value: 'description_edit_with_mask',
            label: '局部重绘',
            desc: '指定区域重新绘制',
            icon: 'el-icon-brush',
            functionType: 'description_edit_with_mask',
            promptPrefix: ''
          },
          {
            value: 'remove_watermark',
            label: '去文字水印',
            desc: '智能去除图片水印',
            icon: 'el-icon-delete',
            functionType: 'remove_watermark',
            promptPrefix: '去除图像中的文字'
          },
          {
            value: 'expand',
            label: '扩图',
            desc: '扩展画布生成更多内容',
            icon: 'el-icon-full-screen',
            functionType: 'expand',
            promptPrefix: ''
          }
        ],
        // 增强处理类
        'enhance': [
          {
            value: 'super_resolution',
            label: '图像超分',
            desc: '提升图片分辨率和清晰度',
            icon: 'el-icon-zoom-in',
            functionType: 'super_resolution',
            promptPrefix: '图像超分'
          },
          {
            value: 'colorization',
            label: '图像上色',
            desc: '黑白照片智能上色',
            icon: 'el-icon-brush',
            functionType: 'colorization',
            promptPrefix: ''
          }
        ],
        // 创意生成类
        'creative': [
          {
            value: 'doodle',
            label: '线稿生图',
            desc: '将涂鸦线稿转为完整图片',
            icon: 'el-icon-edit-outline',
            functionType: 'doodle',
            promptPrefix: '将原图改风格'
          },
          {
            value: 'control_cartoon_feature',
            label: '参考卡通形象',
            desc: '基于卡通形象生成图片',
            icon: 'el-icon-user',
            functionType: 'control_cartoon_feature',
            promptPrefix: ''
          }
        ]
      },
      extractingStyle: false,
      lastBannerImage: null // 记录上次的图片URL,避免重复提取
    }
  },
  computed: {
    // 获取当前选中的分类信息
    selectedCategoryInfo() {
      return this.functionCategories.find(item => item.value === this.selectedFunctionCategory);
    },
    // 获取当前分类下的风格选项
    currentStyleOptions() {
      return this.styleOptionsMap[this.selectedFunctionCategory] || [];
    },
    // 获取当前选中的风格信息
    selectedStyleInfo() {
      if (!this.selectedStyleValue) return null;
      for (let category in this.styleOptionsMap) {
        const found = this.styleOptionsMap[category].find(item => item.value === this.selectedStyleValue);
        if (found) return found;
      }
      return null;
    },
    // 根据功能类型生成描述占位符
    descriptionPlaceholder() {
      const info = this.selectedStyleInfo;
      if (!info) return '请选择具体风格';
      return info.promptPrefix || '无需提示词';
    },
    // 合并后的完整描述
    mergedDescription() {
      const prompt = this.stylePrompt ? this.stylePrompt.trim() : '';
      const desc = this.styleDescription ? this.styleDescription.trim() : '';

      if (prompt && desc) {
        return `${prompt}：${desc}`;
      } else if (prompt) {
        return prompt;
      } else if (desc) {
        return desc;
      }
      return '';
    }
  },
  created() {
    if (this.isEdit) {
      getStyleModel(this.$route.query.id).then(response => {
        this.styleModel = response.data;
        this.lastBannerImage = this.styleModel.bannerImage; // 记录初始图片URL
        // 根据 functionType 设置对应的分类
        this.initFunctionCategory();
        // 拆分风格介绍
        this.splitDescription();
      });
    } else {
      this.styleModel = Object.assign({}, defaultStyleModel);
      // 新建时默认选择线稿生图
      this.selectedStyleValue = 'doodle';
      this.stylePrompt = '将原图改风格';
    }
  },
  methods: {
    // 初始化功能分类（编辑时使用）
    initFunctionCategory() {
      if (!this.styleModel.functionType) return;

      // 根据 functionType 和 stylePrompt 找到对应的风格选项
      for (let category in this.styleOptionsMap) {
        const styles = this.styleOptionsMap[category];
        for (let style of styles) {
          // 匹配条件：functionType 相同 且 promptPrefix 相同
          if (style.functionType === this.styleModel.functionType &&
              style.promptPrefix === this.stylePrompt) {
            this.selectedFunctionCategory = category;
            this.selectedStyleValue = style.value;
            return;
          }
        }
      }

      // 如果没有精确匹配，只根据 functionType 匹配第一个
      for (let category in this.styleOptionsMap) {
        const found = this.styleOptionsMap[category].find(item => item.functionType === this.styleModel.functionType);
        if (found) {
          this.selectedFunctionCategory = category;
          this.selectedStyleValue = found.value;
          break;
        }
      }
    },
    // 拆分风格介绍（编辑时使用）
    splitDescription() {
      if (!this.styleModel.description) return;

      const desc = this.styleModel.description;
      const colonIndex = desc.indexOf('：');

      if (colonIndex > 0) {
        // 如果包含中文冒号，拆分为两部分
        this.stylePrompt = desc.substring(0, colonIndex);
        this.styleDescription = desc.substring(colonIndex + 1);
      } else {
        // 否则全部作为风格描述
        this.stylePrompt = '';
        this.styleDescription = desc;
      }
    },
    // 切换功能分类
    handleFunctionCategoryChange() {
      // 清空当前选择的具体风格
      this.selectedStyleValue = null;
      this.styleModel.functionType = null;
      if (this.autoUpdateStylePrompt) {
        this.stylePrompt = '';
      }
    },
    // 选择具体风格
    selectStyle(style) {
      // 保存UI选择的值
      this.selectedStyleValue = style.value;
      // 保存实际的 API 功能类型
      this.styleModel.functionType = style.functionType;

      // 如果开启自动更新，则更新风格提示词
      if (this.autoUpdateStylePrompt && style.promptPrefix) {
        this.stylePrompt = style.promptPrefix;
      }
    },
    handleBannerImageChange(imageUrl) {
      // 当内容图片上传完成后,自动提取风格介绍
      // 只有在图片URL真正变化时才提取,避免重复调用
      if (imageUrl && imageUrl.trim() !== '' && imageUrl !== this.lastBannerImage) {
        this.lastBannerImage = imageUrl;
        this.extractStyleFromImage(imageUrl);
      }
    },
    extractStyleFromImage(imageUrl) {
      if (this.extractingStyle) {
        return; // 防止重复调用
      }

      this.extractingStyle = true;
      const loading = this.$loading({
        lock: true,
        text: '正在分析图片风格...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      });

      extractStyleDescription(imageUrl).then(response => {
        loading.close();
        this.extractingStyle = false;

        if (response.code === 200 && response.data) {
          // 将提取的风格描述填充到 styleDescription 字段
          this.styleDescription = response.data;
          this.$message({
            message: '图片风格描述提取成功！',
            type: 'success',
            duration: 2000
          });
        } else {
          this.$message({
            message: response.message || '风格介绍提取失败',
            type: 'warning',
            duration: 3000
          });
        }
      }).catch(error => {
        loading.close();
        this.extractingStyle = false;
        console.error('提取风格介绍失败:', error);
        this.$message({
          message: '风格介绍提取失败，请手动填写',
          type: 'warning',
          duration: 3000
        });
      });
    },
    onSubmit(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.$confirm('是否提交数据', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            // 提交前合并风格提示词和图片风格描述
            this.styleModel.description = this.mergedDescription;

            // 确保 functionType 不为空
            if (!this.styleModel.functionType) {
              this.$message.error('请选择功能类型');
              return;
            }

            if (this.isEdit) {
              updateStyleModel(this.$route.query.id, this.styleModel).then(() => {
                this.$message({
                  message: '修改成功',
                  type: 'success',
                  duration: 1000
                });
                this.$router.back();
              });
            } else {
              createStyleModel(this.styleModel).then(() => {
                this.$refs[formName].resetFields();
                this.resetForm(formName);
                this.$message({
                  message: '提交成功',
                  type: 'success',
                  duration: 1000
                });
              });
            }
          });
        } else {
          this.$message({
            message: '验证失败',
            type: 'error',
            duration: 1000
          });
          return false;
        }
      });
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
      this.styleModel = Object.assign({}, defaultStyleModel);
      this.lastBannerImage = null;
    }
  }
}
</script>

<style scoped>
.input-width {
  width: 400px;
}

/* 风格卡片容器 */
.style-cards-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(110px, 1fr));
  gap: 10px;
  margin-top: 10px;
}

/* 风格卡片 */
.style-card {
  border: 1.5px solid #DCDFE6;
  border-radius: 6px;
  padding: 10px 8px;
  text-align: center;
  cursor: pointer;
  transition: all 0.25s ease;
  background-color: #fff;
}

.style-card:hover {
  border-color: #409EFF;
  box-shadow: 0 2px 8px 0 rgba(64, 158, 255, 0.25);
  transform: translateY(-1px);
}

.style-card.active {
  border-color: #409EFF;
  background-color: #ECF5FF;
  box-shadow: 0 2px 8px 0 rgba(64, 158, 255, 0.35);
}

/* 卡片标题 */
.style-card-title {
  font-size: 13px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 5px;
}

.style-card.active .style-card-title {
  color: #409EFF;
}

/* 卡片描述 */
.style-card-desc {
  font-size: 11px;
  color: #909399;
  line-height: 1.4;
}

.style-card.active .style-card-desc {
  color: #66B1FF;
}
</style>

