<template>
  <div style="margin-top: 50px">
    <el-form :model="value" :rules="rules" ref="productInfoForm" label-width="120px" class="form-inner-container" size="small">
      <el-form-item label="商品分类：" prop="productCategoryId">
        <el-select 
          v-model="value.productCategoryId" 
          placeholder="请选择商品分类"
          @change="handleCateSelectChange"
          clearable>
          <el-option-group
            v-for="group in productCateOptions"
            :key="group.value"
            :label="group.label">
            <!-- 一级分类选项 -->
            <el-option
              :key="'parent-' + group.value"
              :label="group.label"
              :value="group.value">
              <span style="font-weight: bold">{{ group.label }}</span>
            </el-option>
            <!-- 二级分类选项 -->
            <el-option
              v-for="item in group.children"
              :key="item.value"
              :label="'　└ ' + item.label"
              :value="item.value">
            </el-option>
          </el-option-group>
        </el-select>
      </el-form-item>
      <el-form-item label="商品名称：" prop="name">
        <el-input v-model="value.name"></el-input>
      </el-form-item>
      <el-form-item label="副标题：" prop="subTitle">
        <el-input v-model="value.subTitle"></el-input>
      </el-form-item>
      <el-form-item label="商品品牌：" prop="brandId">
        <el-select
          v-model="value.brandId"
          @change="handleBrandChange"
          placeholder="请选择品牌">
          <el-option
            v-for="item in brandOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="商品介绍：">
        <el-input
          :autoSize="true"
          v-model="value.description"
          type="textarea"
          placeholder="请输入内容"></el-input>
      </el-form-item>
      <el-form-item label="商品货号：">
        <el-input v-model="value.productSn"></el-input>
      </el-form-item>
      <el-form-item label="购买方式：" prop="purchaseType">
        <el-radio-group v-model="value.purchaseType">
          <el-radio :label="0">快递+线下自提</el-radio>
          <el-radio :label="1">仅线下自提</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="商品售价：">
        <el-input v-model="value.price"></el-input>
      </el-form-item>
      <el-form-item label="市场价：">
        <el-input v-model="value.originalPrice"></el-input>
      </el-form-item>
      <el-form-item label="商品库存：">
        <el-input v-model="value.stock"></el-input>
      </el-form-item>
      <el-form-item label="计量单位：">
        <el-input v-model="value.unit"></el-input>
      </el-form-item>
      <el-form-item label="商品重量：">
        <el-input v-model="value.weight" style="width: 300px"></el-input>
        <span style="margin-left: 20px">克</span>
      </el-form-item>
      <el-form-item label="运费模板：" prop="freightTemplateId">
        <el-select
          v-model="value.freightTemplateId"
          placeholder="请选择运费模板"
          @change="handleFreightTemplateChange"
          @clear="handleFreightTemplateClear"
          clearable
          style="width: 300px">
          <el-option
            v-for="item in freightTemplateOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value">
            <span>{{ item.label }}</span>
            <span style="float: right; color: #8492a6; font-size: 12px">{{ item.chargeTypeText }}</span>
          </el-option>
        </el-select>
        <span style="margin-left: 20px; color: #909399; font-size: 12px">
          选择运费计算模板，不选择将使用默认运费
        </span>
        <div style="margin-left: 20px; margin-top: 5px; color: #f56c6c; font-size: 11px" v-if="$route.query.debug">
          调试信息：当前运费模板ID = {{ value.freightTemplateId }}
        </div>
      </el-form-item>
      <el-form-item label="排序">
        <el-input v-model="value.sort"></el-input>
      </el-form-item>

      <!-- DIY配置区域 -->
      <div class="section-title">
        <i class="el-icon-edit"></i> DIY定制配置
      </div>

      <el-form-item label="支持DIY定制：">
        <el-switch
          v-model="value.diyEnabled"
          :active-value="1"
          :inactive-value="0"
          active-text="支持"
          inactive-text="不支持">
        </el-switch>
        <span style="margin-left: 10px; color: #909399; font-size: 12px;">
          启用后用户可以对此商品进行个性化定制
        </span>
      </el-form-item>

      <el-form-item v-if="value.diyEnabled === 1" label="DIY模板：">
        <el-select
          v-model="value.diyTemplateId"
          placeholder="请选择DIY模板"
          clearable
          style="width: 300px"
          @focus="loadDiyTemplateOptions">
          <el-option
            v-for="item in diyTemplateOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
        <span style="margin-left: 10px; color: #909399; font-size: 12px;">
          选择适合此商品的DIY模板
        </span>
      </el-form-item>

      <!-- 学校关联配置区域 -->
      <div class="section-title">
        <i class="el-icon-school"></i> 学校关联配置
      </div>

      <el-form-item label="关联学校：">
        <el-select
          v-model="value.schoolIds"
          multiple
          placeholder="请选择关联的学校"
          clearable
          style="width: 100%">
          <el-option
            v-for="item in schoolOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
        <span style="margin-left: 10px; color: #909399; font-size: 12px;">
          选择此商品可以在哪些学校销售，不选择表示在所有学校都可以销售
        </span>
      </el-form-item>

      <!-- 回本分析配置区域 - 已移至独立的回本分析模块，此处隐藏 -->
      <!-- 注意：商品表中的回本分析字段保留，但不在商品编辑页面显示 -->
      <!-- 请使用 商品管理 -> 回本分析 模块进行批次管理 -->
      <template v-if="false">
        <div class="section-title">
          <i class="el-icon-data-analysis"></i> 回本分析配置
        </div>
        
        <el-form-item label="启用回本分析：">
          <el-switch
            v-model="value.enablePaybackAnalysis"
            :active-value="true"
            :inactive-value="false"
            active-text="启用"
            inactive-text="关闭">
          </el-switch>
          <span style="margin-left: 10px; color: #909399; font-size: 12px;">
            启用后可以设置商品回本目标并跟踪销售进度
          </span>
        </el-form-item>
        
        <template v-if="value.enablePaybackAnalysis">
          <el-form-item label="回本目标数量：" prop="paybackTargetQuantity">
            <el-input-number
              v-model="value.paybackTargetQuantity"
              :min="1"
              :max="999999"
              :precision="0"
              placeholder="请输入回本目标销售数量"
              style="width: 200px">
            </el-input-number>
            <span style="margin-left: 10px; color: #909399; font-size: 12px;">件</span>
          </el-form-item>
          
          <el-form-item label="回本目标金额：" prop="paybackTargetAmount">
            <el-input-number
              v-model="value.paybackTargetAmount"
              :precision="2"
              :min="0.01"
              :max="99999999.99"
              placeholder="请输入回本目标金额"
              style="width: 200px">
            </el-input-number>
            <span style="margin-left: 10px; color: #909399; font-size: 12px;">元</span>
          </el-form-item>
          
          <el-form-item label="回本开始日期：" prop="paybackStartDate">
            <el-date-picker
              v-model="value.paybackStartDate"
              type="date"
              placeholder="选择开始统计的日期"
              value-format="yyyy-MM-dd"
              style="width: 200px">
            </el-date-picker>
            <span style="margin-left: 10px; color: #909399; font-size: 12px;">
              从此日期开始统计销售数据
            </span>
          </el-form-item>
        </template>
      </template>
      
      <el-form-item style="text-align: center">
        <el-button type="primary" size="medium" @click="handleNext('productInfoForm')">下一步，填写商品促销</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
  import {fetchListWithChildren} from '@/api/productCate'
  import {fetchList as fetchBrandList} from '@/api/brand'
  import {getProduct} from '@/api/product'
  import {fetchFreightTemplateList} from '@/api/freight'
  import {fetchTemplateList} from '@/api/diyTemplate'
  import {fetchList as fetchSchoolList} from '@/api/school';

  export default {
    name: "ProductInfoDetail",
    props: {
      value: Object,
      isEdit: {
        type: Boolean,
        default: false
      }
    },
    data() {
      return {
        hasEditCreated:false,
        productCateOptions: [],
        brandOptions: [],
        freightTemplateOptions: [],
        diyTemplateOptions: [],
        schoolOptions: [],
        rules: {
          name: [
            {required: true, message: '请输入商品名称', trigger: 'blur'},
            {min: 2, max: 140, message: '长度在 2 到 140 个字符', trigger: 'blur'}
          ],
          subTitle: [{required: true, message: '请输入商品副标题', trigger: 'blur'}],
          productCategoryId: [{required: true, message: '请选择商品分类', trigger: 'blur'}],
          brandId: [{required: true, message: '请选择商品品牌', trigger: 'blur'}],
          description: [{required: true, message: '请输入商品介绍', trigger: 'blur'}],
          purchaseType: [{ required: true, message: '请选择购买方式', trigger: 'change'}],
          freightTemplateId: [{required: false, message: '请选择运费模板', trigger: 'blur'}],
          requiredProp: [{required: true, message: '该项为必填项', trigger: 'blur'}],
          // 回本分析配置验证规则
          paybackTargetQuantity: [
            {
              validator: (rule, value, callback) => {
                if (this.value.enablePaybackAnalysis) {
                  if (!value || value <= 0) {
                    callback(new Error('请输入有效的回本目标数量'));
                  } else {
                    callback();
                  }
                } else {
                  callback();
                }
              },
              trigger: 'blur'
            }
          ],
          paybackTargetAmount: [
            {
              validator: (rule, value, callback) => {
                if (this.value.enablePaybackAnalysis) {
                  if (!value || value <= 0) {
                    callback(new Error('请输入有效的回本目标金额'));
                  } else {
                    callback();
                  }
                } else {
                  callback();
                }
              },
              trigger: 'blur'
            }
          ],
          paybackStartDate: [
            {
              validator: (rule, value, callback) => {
                if (this.value.enablePaybackAnalysis) {
                  if (!value) {
                    callback(new Error('请选择回本开始日期'));
                  } else {
                    callback();
                  }
                } else {
                  callback();
                }
              },
              trigger: 'change'
            }
          ]
        }
      };
    },
    created() {
      this.getProductCateList();
      this.getBrandList();
      this.getFreightTemplateList();
      this.loadSchoolOptions(); // 页面加载时就加载学校选项
    },
    computed:{
      //商品的编号
      productId(){
        return this.value.id;
      }
    },
    watch: {
      productId:function(newValue){
        if(!this.isEdit)return;
        if(this.hasEditCreated)return;
        if(newValue===undefined||newValue==null||newValue===0)return;
        this.handleEditCreated();
      },
      value: {
        handler(newValue) {
          // 确保schoolIds始终是一个数组
          if (newValue && !Array.isArray(newValue.schoolIds)) {
            this.$set(newValue, 'schoolIds', []);
          }
        },
        immediate: true
      }
    },
    methods: {
      //处理编辑逻辑
      handleEditCreated(){
        if(this.value.productCategoryId!=null){
          // 直接使用现有的productCategoryId，因为el-select直接绑定到value.productCategoryId
        }

        // 如果商品启用了DIY且有模板ID，确保模板选项正确显示
        if(this.value.diyEnabled === 1 && this.value.diyTemplateId) {
          this.ensureCurrentTemplateInOptions();
        }

        // 处理学校关联数据
        if(this.value && this.value.schoolList && Array.isArray(this.value.schoolList) && this.value.schoolList.length > 0) {
          this.value.schoolIds = this.value.schoolList.map(school => school.id).filter(id => id != null);
          // 确保学校选项中包含当前关联的学校
          this.ensureCurrentSchoolsInOptions();
        } else {
          // 确保schoolIds始终是一个数组
          if (this.value) {
            this.value.schoolIds = this.value.schoolIds || [];
          }
        }

        this.hasEditCreated=true;
      },
      getProductCateList() {
        fetchListWithChildren().then(response => {
          let list = response.data;
          this.productCateOptions = [];
          for (let i = 0; i < list.length; i++) {
            let children = [];
            if (list[i].children != null && list[i].children.length > 0) {
              for (let j = 0; j < list[i].children.length; j++) {
                children.push({
                  label: list[i].children[j].name, 
                  value: list[i].children[j].id,
                  leaf: true
                });
              }
            }
            this.productCateOptions.push({
              label: list[i].name, 
              value: list[i].id, 
              children: children,
              leaf: children.length === 0
            });
          }
        });
      },
      getBrandList() {
        fetchBrandList({pageNum: 1, pageSize: 100}).then(response => {
          this.brandOptions = [];
          let brandList = response.data.list;
          for (let i = 0; i < brandList.length; i++) {
            this.brandOptions.push({label: brandList[i].name, value: brandList[i].id});
          }
        });
      },
      getFreightTemplateList() {
        fetchFreightTemplateList({pageNum: 1, pageSize: 100, status: 1}).then(response => {
          this.freightTemplateOptions = [];
          let templateList = response.data.list;
          for (let i = 0; i < templateList.length; i++) {
            const chargeTypeMap = {
              1: '按件数',
              2: '按重量',
              3: '按体积',
              4: '固定运费'
            };
            this.freightTemplateOptions.push({
              label: templateList[i].name,
              value: templateList[i].id,
              chargeTypeText: chargeTypeMap[templateList[i].chargeType] || '未知'
            });
          }
        }).catch(error => {
          console.error('获取运费模板列表失败:', error);
        });
      },
      getCateNameById(id){
        let name=null;
        for(let i=0;i<this.productCateOptions.length;i++){
          for(let j=0;j<this.productCateOptions[i].children.length;j++){
            if(this.productCateOptions[i].children[j].value===id){
              name=this.productCateOptions[i].children[j].label;
              return name;
            }
          }
        }
        return name;
      },
      handleNext(formName){
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.$emit('nextStep');
          } else {
            this.$message({
              message: '验证失败',
              type: 'error',
              duration:1000
            });
            return false;
          }
        });
      },
      handleBrandChange(val) {
        let brandName = '';
        for (let i = 0; i < this.brandOptions.length; i++) {
          if (this.brandOptions[i].value === val) {
            brandName = this.brandOptions[i].label;
            break;
          }
        }
        this.value.brandName = brandName;
      },
      handleFreightTemplateChange(val) {
        // 运费模板选择变化时的处理逻辑
        console.log('运费模板change事件，新值:', val, '类型:', typeof val);
        this.value.freightTemplateId = val;
        
        // 使用 $nextTick 确保数据更新完成
        this.$nextTick(() => {
          console.log('change后value.freightTemplateId:', this.value.freightTemplateId);
        });
      },
      handleFreightTemplateClear() {
        // 处理清空运费模板选择
        console.log('运费模板clear事件触发');
        this.value.freightTemplateId = null;
        
        // 使用 $nextTick 确保数据更新完成  
        this.$nextTick(() => {
          console.log('clear后value.freightTemplateId:', this.value.freightTemplateId);
        });
      },
      loadDiyTemplateOptions() {
        // 只在需要时加载，避免页面加载时的问题
        if (this.diyTemplateOptions.length > 0) return;

        fetchTemplateList({ pageNum: 1, pageSize: 100, status: 1 }).then(response => {
          this.diyTemplateOptions = [];
          if (response.data && response.data.list) {
            let templateList = response.data.list;
            for (let i = 0; i < templateList.length; i++) {
              this.diyTemplateOptions.push({
                label: templateList[i].name,
                value: templateList[i].id
              });
            }
          }

          // 如果当前商品已选择模板但选项中没有对应的模板，添加当前模板到选项中
          this.ensureCurrentTemplateInOptions();
        }).catch(error => {
          console.error('获取DIY模板列表失败:', error);
          // 不显示错误消息，避免影响用户体验
        });
      },
      ensureCurrentTemplateInOptions() {
        if (this.value.diyTemplateId && this.value.diyTemplateName) {
          // 检查当前选择的模板是否在选项列表中
          const existingOption = this.diyTemplateOptions.find(option => option.value === this.value.diyTemplateId);
          if (!existingOption) {
            // 如果不存在，添加当前模板到选项中
            this.diyTemplateOptions.unshift({
              label: this.value.diyTemplateName,
              value: this.value.diyTemplateId
            });
          }
        }
      },
      handleCateSelectChange(value) {
        if (value) {
          let isFirstLevel = true;
          let categoryName = '';
          let parentId = 0;
          
          // 先在一级分类中查找
          for (let i = 0; i < this.productCateOptions.length; i++) {
            if (this.productCateOptions[i].value === value) {
              categoryName = this.productCateOptions[i].label;
              isFirstLevel = true;
              break;
            }
            
            // 查找二级分类
            if (this.productCateOptions[i].children) {
              for (let j = 0; j < this.productCateOptions[i].children.length; j++) {
                if (this.productCateOptions[i].children[j].value === value) {
                  categoryName = this.productCateOptions[i].children[j].label;
                  parentId = this.productCateOptions[i].value;
                  isFirstLevel = false;
                  break;
                }
              }
              if (!isFirstLevel && categoryName) break;
            }
          }
          
          // 设置分类信息
          this.value.productCategoryName = categoryName;
          this.value.cateParentId = parentId;
        } else {
          // 清空选择
          this.value.productCategoryId = null;
          this.value.productCategoryName = null;
          this.value.cateParentId = null;
        }
      },
      loadSchoolOptions() {
        fetchSchoolList({ pageNum: 1, pageSize: 100, status: true }).then(response => {
          this.schoolOptions = [];
          if (response.data && response.data.list) {
            let schoolList = response.data.list;
            for (let i = 0; i < schoolList.length; i++) {
              this.schoolOptions.push({
                label: schoolList[i].schoolName,
                value: schoolList[i].id
              });
            }
          }

          // 如果是编辑模式且已有学校关联数据，确保选项中包含当前关联的学校
          this.ensureCurrentSchoolsInOptions();
        }).catch(error => {
          console.error('获取学校列表失败:', error);
          // 不显示错误消息，避免影响用户体验
        });
      },
      ensureCurrentSchoolsInOptions() {
        if (this.value && this.value.schoolList && Array.isArray(this.value.schoolList) && this.value.schoolList.length > 0) {
          // 检查当前关联的学校是否都在选项列表中
          this.value.schoolList.forEach(school => {
            if (school && school.id && school.schoolName) {
              const existingOption = this.schoolOptions.find(option => option.value === school.id);
              if (!existingOption) {
                // 如果不存在，添加到选项中
                this.schoolOptions.push({
                  label: school.schoolName,
                  value: school.id
                });
              }
            }
          });
        }
      }
    }
  }
</script>

<style scoped>
  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: #333;
    margin: 30px 0 20px 0;
    padding-bottom: 10px;
    border-bottom: 2px solid #409eff;
    position: relative;
  }
  
  .section-title:first-child {
    margin-top: 0;
  }
</style>
