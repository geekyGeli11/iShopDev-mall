<template> 
  <el-card class="form-container" shadow="never">
    <el-form :model="homeAdvertise"
             :rules="rules"
             ref="homeAdvertiseFrom"
             label-width="150px"
             size="small">
      <el-form-item label="广告名称：" prop="name">
        <el-input v-model="homeAdvertise.name" class="input-width"></el-input>
      </el-form-item>
      <el-form-item label="广告位置：">
        <el-select v-model="homeAdvertise.type">
          <el-option
            v-for="type in typeOptions"
            :key="type.value"
            :label="type.label"
            :value="type.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="开始时间：" prop="startTime">
        <el-date-picker
          type="datetime"
          placeholder="选择日期"
          v-model="homeAdvertise.startTime"></el-date-picker>
      </el-form-item>
      <el-form-item label="到期时间：" prop="endTime">
        <el-date-picker
          type="datetime"
          placeholder="选择日期"
          v-model="homeAdvertise.endTime"></el-date-picker>
      </el-form-item>
      <el-form-item label="上线/下线：">
        <el-radio-group v-model="homeAdvertise.status">
          <el-radio :label="0">下线</el-radio>
          <el-radio :label="1">上线</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="关联学校：">
        <el-select v-model="homeAdvertise.schoolId" placeholder="请选择学校" clearable class="input-width">
          <el-option label="全平台通用" :value="null"></el-option>
          <el-option
            v-for="school in schoolOptions"
            :key="school.id"
            :label="school.schoolName"
            :value="school.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="广告图片：">
        <single-upload v-model="homeAdvertise.pic"></single-upload>
        <el-alert
          title="图片尺寸规范"
          type="info"
          :closable="false"
          style="margin-top: 10px;">
          <div style="font-size: 12px; line-height: 1.8;">
            <p style="margin: 0 0 8px 0;"><strong>推荐尺寸（2倍图）：</strong>686 × 276 px（宽高比 2.49:1）</p>
            <p style="margin: 0 0 8px 0;"><strong>高清尺寸（3倍图）：</strong>1029 × 414 px</p>
            <p style="margin: 0 0 8px 0;"><strong>文件大小：</strong>建议控制在 100-150KB 以内</p>
            <p style="margin: 0; color: #E6A23C;">⚠️ 请勿使用 343×138 px 的图片，会导致在高分辨率屏幕上显示模糊</p>
          </div>
        </el-alert>
      </el-form-item>
      <el-form-item label="排序：">
        <el-input v-model="homeAdvertise.sort" class="input-width"></el-input>
      </el-form-item>
      <el-form-item label="广告链接：" prop="url">
        <div class="url-input-container">
          <el-input v-model="homeAdvertise.url" class="url-input" placeholder="请选择商品、分类、组合商品或手动输入链接"></el-input>
          <div class="button-group">
            <el-button size="small" type="primary" @click="openProductSelectDialog">选择商品</el-button>
            <el-button size="small" type="success" @click="openCategorySelectDialog">选择商品分类</el-button>
            <el-button size="small" type="warning" @click="openBundleSelectDialog">选择组合商品</el-button>
          </div>
        </div>

        <!-- 已选商品展示 -->
        <div v-if="selectedProduct" class="selected-product-container">
          <div class="selected-product-item">
            <div class="product-image">
              <img :src="selectedProduct.pic" style="width: 60px; height: 60px;">
            </div>
            <div class="product-info">
              <div class="product-name">{{selectedProduct.name}}</div>
              <div class="product-price">¥{{selectedProduct.price}}</div>
            </div>
            <div class="product-action">
              <el-button size="mini" type="danger" icon="el-icon-delete" circle @click="handleRemoveProduct"></el-button>
            </div>
          </div>
        </div>

        <!-- 已选分类展示 -->
        <div v-if="selectedCategory" class="selected-category-container">
          <div class="selected-category-item">
            <div class="category-image">
              <img :src="selectedCategory.icon || 'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20190519/default.png'" style="width: 60px; height: 60px;">
            </div>
            <div class="category-info">
              <div class="category-name">{{selectedCategory.name}}</div>
              <div class="category-desc">商品分类</div>
            </div>
            <div class="category-action">
              <el-button size="mini" type="danger" icon="el-icon-delete" circle @click="handleRemoveCategory"></el-button>
            </div>
          </div>
        </div>

        <!-- 已选组合商品展示 -->
        <div v-if="selectedBundle" class="selected-bundle-container">
          <div class="selected-bundle-item">
            <div class="bundle-image">
              <img :src="selectedBundle.pic || 'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20190519/default.png'" style="width: 60px; height: 60px;">
            </div>
            <div class="bundle-info">
              <div class="bundle-name">{{selectedBundle.name}}</div>
              <div class="bundle-price">组合价: ¥{{selectedBundle.bundlePrice}}</div>
            </div>
            <div class="bundle-action">
              <el-button size="mini" type="danger" icon="el-icon-delete" circle @click="handleRemoveBundle"></el-button>
            </div>
          </div>
        </div>
      </el-form-item>
      <el-form-item label="广告备注：">
        <el-input
          class="input-width"
          type="textarea"
          :rows="5"
          placeholder="请输入内容"
          v-model="homeAdvertise.note">
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit('homeAdvertiseFrom')">提交</el-button>
        <el-button v-if="!isEdit" @click="resetForm('homeAdvertiseFrom')">重置</el-button>
      </el-form-item>
    </el-form>
    
    <!-- 商品选择对话框 -->
    <product-selection-dialog
      :visible.sync="productSelectVisible"
      selection-mode="single"
      :selected-products="selectedProduct ? [selectedProduct] : []"
      @selection-confirmed="handleSelectProducts">
    </product-selection-dialog>

    <!-- 分类选择对话框 -->
    <category-selection-dialog
      :visible.sync="categorySelectVisible"
      :selected-category="selectedCategory"
      @selection-confirmed="handleSelectCategory">
    </category-selection-dialog>

    <!-- 组合商品选择对话框 -->
    <bundle-selection-dialog
      :visible.sync="bundleSelectVisible"
      :selected-bundle="selectedBundle"
      @selection-confirmed="handleSelectBundle">
    </bundle-selection-dialog>
  </el-card>
</template>
<script>
  import SingleUpload from '@/components/Upload/singleUpload'
  import {createHomeAdvertise, getHomeAdvertise, updateHomeAdvertise} from '@/api/homeAdvertise'
  import {fetchEnabledSchools} from '@/api/school'
  import ProductSelectionDialog from '@/components/ProductSelectionDialog'
  import CategorySelectionDialog from '@/components/CategorySelectionDialog'
  import BundleSelectionDialog from '@/components/BundleSelectionDialog'
  
  const defaultTypeOptions = [
    {
      label: 'PC首页轮播',
      value: 0
    },
    {
      label: 'APP首页轮播',
      value: 1
    }
  ];
  const defaultHomeAdvertise = {
    name: null,
    type: 1,
    pic: null,
    startTime: null,
    endTime: null,
    status: 0,
    url: null,
    note: null,
    sort: 0,
    schoolId: null
  };
  export default {
    name: 'HomeAdvertiseDetail',
    components:{SingleUpload, ProductSelectionDialog, CategorySelectionDialog, BundleSelectionDialog},
    props: {
      isEdit: {
        type: Boolean,
        default: false
      }
    },
    data() {
      return {
        homeAdvertise: null,
        rules: {
          name: [
            {required: true, message: '请输入广告名称', trigger: 'blur'},
            {min: 2, max: 140, message: '长度在 2 到 140 个字符', trigger: 'blur'}
          ],
          url: [
            {required: true, message: '请输入广告链接', trigger: 'blur'}
          ],
          startTime: [
            {required: true, message: '请选择开始时间', trigger: 'blur'}
          ],
          endTime: [
            {required: true, message: '请选择到期时间', trigger: 'blur'}
          ],
          pic: [
            {required: true, message: '请选择广告图片', trigger: 'blur'}
          ]
        },
        typeOptions: Object.assign({}, defaultTypeOptions),
        productSelectVisible: false,
        selectedProduct: null,
        categorySelectVisible: false,
        selectedCategory: null,
        bundleSelectVisible: false,
        selectedBundle: null,
        schoolOptions: []
      }
    },
    created(){
      this.getSchoolOptions();
      if (this.isEdit) {
        getHomeAdvertise(this.$route.query.id).then(response => {
          this.homeAdvertise = response.data;
          // 判断url类型
          if (this.homeAdvertise.url) {
            // 如果是分类链接格式: /pages/category/category?categoryId=xxx
            if (this.homeAdvertise.url.includes('/pages/category/category?categoryId=')) {
              this.tryLoadCategoryInfo(this.homeAdvertise.url);
            }
            // 如果是组合商品链接格式: /pages/product/bundleDetail?id=xxx
            else if (this.homeAdvertise.url.includes('/pages/product/bundleDetail?id=')) {
              this.tryLoadBundleInfo(this.homeAdvertise.url);
            }
            // 如果url是数字，则可能是商品ID，尝试获取商品信息
            else if (!isNaN(this.homeAdvertise.url)) {
              this.tryLoadProductInfo(this.homeAdvertise.url);
            }
          }
        });
      }else{
        this.homeAdvertise = Object.assign({},defaultHomeAdvertise);
      }
    },
    methods: {
      getSchoolOptions() {
        fetchEnabledSchools().then(response => {
          this.schoolOptions = response.data;
        }).catch(error => {
          console.error('获取学校列表失败:', error);
        });
      },
      tryLoadProductInfo(productId) {
        // 如果有商品ID，可以通过API获取商品详情
        // 这里简化处理，实际应根据需求实现
        import('@/api/product').then(module => {
          module.getProduct(productId).then(response => {
            if (response.data) {
              this.selectedProduct = {
                id: response.data.id,
                name: response.data.name,
                pic: response.data.pic,
                price: response.data.price
              };
            }
          }).catch(() => {
            // 获取失败时不处理，保持url原样
          });
        });
      },
      tryLoadCategoryInfo(url) {
        // 从URL中提取分类ID
        const match = url.match(/categoryId=(\d+)/);
        if (match && match[1]) {
          const categoryId = parseInt(match[1]);
          // 获取分类信息
          import('@/api/productCate').then(module => {
            module.getProductCate(categoryId).then(response => {
              if (response.data) {
                this.selectedCategory = {
                  id: response.data.id,
                  name: response.data.name,
                  icon: response.data.icon
                };
              }
            }).catch(() => {
              // 获取失败时不处理，保持url原样
            });
          });
        }
      },
      openProductSelectDialog() {
        this.productSelectVisible = true;
      },
      handleSelectProducts(products) {
        if (products && products.length > 0) {
          const product = products[0];
          this.selectedProduct = product;
          // 清除已选分类
          this.selectedCategory = null;
          // 将商品ID作为链接
          this.homeAdvertise.url = product.id.toString();
        }
      },
      handleRemoveProduct() {
        this.selectedProduct = null;
        this.homeAdvertise.url = null;
      },
      openCategorySelectDialog() {
        this.categorySelectVisible = true;
      },
      handleSelectCategory(category) {
        if (category) {
          this.selectedCategory = category;
          // 清除已选商品
          this.selectedProduct = null;
          // 将分类链接保存为特定格式: /pages/category/category?categoryId=xxx
          this.homeAdvertise.url = `/pages/category/category?categoryId=${category.id}`;
        }
      },
      handleRemoveCategory() {
        this.selectedCategory = null;
        this.homeAdvertise.url = null;
      },
      // 组合商品相关方法
      tryLoadBundleInfo(url) {
        // 匹配 /pages/product/bundleDetail?id=xxx 格式
        const match = url.match(/bundleDetail\?id=(\d+)/);
        if (match && match[1]) {
          const bundleId = parseInt(match[1]);
          import('@/api/productBundle').then(module => {
            module.getBundle(bundleId).then(response => {
              if (response.data) {
                this.selectedBundle = {
                  id: response.data.id,
                  name: response.data.name,
                  pic: response.data.pic,
                  bundlePrice: response.data.bundlePrice
                };
              }
            }).catch(() => {});
          });
        }
      },
      openBundleSelectDialog() {
        this.bundleSelectVisible = true;
      },
      handleSelectBundle(bundle) {
        if (bundle) {
          this.selectedBundle = bundle;
          // 清除已选商品和分类
          this.selectedProduct = null;
          this.selectedCategory = null;
          // 设置组合商品详情页链接: /pages/product/bundleDetail?id=xxx
          this.homeAdvertise.url = `/pages/product/bundleDetail?id=${bundle.id}`;
        }
      },
      handleRemoveBundle() {
        this.selectedBundle = null;
        this.homeAdvertise.url = null;
      },
      onSubmit(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.$confirm('是否提交数据', '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            }).then(() => {
              if (this.isEdit) {
                updateHomeAdvertise(this.$route.query.id, this.homeAdvertise).then(response => {
                  this.$refs[formName].resetFields();
                  this.$message({
                    message: '修改成功',
                    type: 'success',
                    duration:1000
                  });
                  this.$router.back();
                });
              } else {
                createHomeAdvertise(this.homeAdvertise).then(response => {
                  this.$refs[formName].resetFields();
                  this.homeAdvertise = Object.assign({},defaultHomeAdvertise);
                  this.$message({
                    message: '提交成功',
                    type: 'success',
                    duration:1000
                  });
                });
              }
            });

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
      resetForm(formName) {
        this.$refs[formName].resetFields();
        this.homeAdvertise = Object.assign({},defaultHomeAdvertise);
        this.selectedProduct = null;
        this.selectedCategory = null;
        this.selectedBundle = null;
      }
    }
  }
</script>
<style scoped>
  .input-width {
    width: 70%;
  }

  /* URL输入容器样式 */
  .url-input-container {
    display: flex;
    align-items: flex-start;
    gap: 10px;
    width: 100%;
  }

  .url-input {
    flex: 1;
    max-width: 500px;
  }

  .button-group {
    display: flex;
    gap: 10px;
    flex-shrink: 0;
  }

  .selected-product-container {
    margin-top: 10px;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    padding: 10px;
  }
  .selected-product-item {
    display: flex;
    align-items: center;
    padding: 5px;
  }
  .product-image {
    margin-right: 10px;
  }
  .product-image img {
    border-radius: 4px;
    object-fit: cover;
  }
  .product-info {
    flex: 1;
  }
  .product-name {
    font-weight: bold;
    margin-bottom: 5px;
  }
  .product-price {
    color: #f56c6c;
  }
  .product-action {
    margin-left: 10px;
  }

  .selected-category-container {
    margin-top: 10px;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    padding: 10px;
  }
  .selected-category-item {
    display: flex;
    align-items: center;
    padding: 5px;
  }
  .category-image {
    margin-right: 10px;
  }
  .category-image img {
    border-radius: 4px;
    object-fit: cover;
  }
  .category-info {
    flex: 1;
  }
  .category-name {
    font-weight: bold;
    margin-bottom: 5px;
  }
  .category-desc {
    color: #67c23a;
    font-size: 12px;
  }
  .category-action {
    margin-left: 10px;
  }

  .selected-bundle-container {
    margin-top: 10px;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    padding: 10px;
  }
  .selected-bundle-item {
    display: flex;
    align-items: center;
    padding: 5px;
  }
  .bundle-image {
    margin-right: 10px;
  }
  .bundle-image img {
    border-radius: 4px;
    object-fit: cover;
  }
  .bundle-info {
    flex: 1;
  }
  .bundle-name {
    font-weight: bold;
    margin-bottom: 5px;
  }
  .bundle-price {
    color: #e6a23c;
    font-size: 12px;
  }
  .bundle-action {
    margin-left: 10px;
  }
</style>


