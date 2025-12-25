<template> 
  <el-card class="form-container" shadow="never">
    <el-form :model="coupon"
             :rules="rules"
             ref="couponFrom"
             label-width="150px"
             size="small">
      <el-form-item label="优惠券类型：">
        <el-select v-model="coupon.type">
          <el-option
            v-for="type in typeOptions"
            :key="type.value"
            :label="type.label"
            :value="type.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="优惠方式：">
        <el-radio-group v-model="coupon.couponType" @change="handleCouponTypeChange">
          <el-radio-button :label="0">满减券</el-radio-button>
          <el-radio-button :label="1">打折券</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="优惠券名称：" prop="name">
        <el-input v-model="coupon.name" class="input-width"></el-input>
      </el-form-item>
      <el-form-item label="适用平台：">
        <el-select v-model="coupon.platform">
          <el-option
            v-for="item in platformOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="总发行量：" prop="publishCount">
        <el-input v-model.number="coupon.publishCount" placeholder="只能输入正整数" class="input-width"></el-input>
      </el-form-item>
      <el-form-item v-if="coupon.couponType === 0" label="面额：" prop="amount">
        <el-input v-model.number="coupon.amount" placeholder="面值只能是数值，限2位小数" class="input-width">
          <template slot="append">元</template>
        </el-input>
      </el-form-item>
      <el-form-item v-if="coupon.couponType === 1" label="折扣率：" prop="discountRate">
        <el-input v-model.number="coupon.discountRate" placeholder="请输入0.1-0.99之间的数值" class="input-width">
          <template slot="append">折</template>
        </el-input>
        <div style="margin-top: 5px; color: #999; font-size: 12px;">
          例如：0.8表示8折，0.5表示5折
        </div>
      </el-form-item>
      <el-form-item label="每人限领：">
        <el-input v-model="coupon.perLimit" placeholder="只能输入正整数" class="input-width">
          <template slot="append">张</template>
        </el-input>
      </el-form-item>
      <el-form-item label="使用门槛：" prop="minPoint">
        <el-input v-model.number="coupon.minPoint" placeholder="只能输入正整数" class="input-width">
          <template slot="prepend">满</template>
          <template slot="append">元可用</template>
        </el-input>
      </el-form-item>
      <el-form-item label="可领取日期：" prop="enableTime">
        <el-date-picker type="date" placeholder="选择日期" v-model="coupon.enableTime" class="input-width"></el-date-picker>
      </el-form-item>
      <el-form-item label="有效期：">
        <el-date-picker type="date" placeholder="选择日期" v-model="coupon.startTime" style="width: 150px"></el-date-picker>
        <span style="margin-left: 20px;margin-right: 20px">至</span>
        <el-date-picker type="date" placeholder="选择日期" v-model="coupon.endTime" style="width: 150px"></el-date-picker>
      </el-form-item>
      <el-form-item label="可使用商品：">
        <el-radio-group v-model="coupon.useType">
          <el-radio-button :label="0">全场通用</el-radio-button>
          <el-radio-button :label="1">指定分类</el-radio-button>
          <el-radio-button :label="2">指定商品</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="排除逻辑：">
        <el-switch
          v-model="coupon.enableExclude"
          active-text="启用排除逻辑"
          inactive-text="不启用排除逻辑">
        </el-switch>
        <div style="margin-top: 5px; color: #999; font-size: 12px;">
          启用后可以设置不可用的商品或分类，与上面的"可使用商品"组合使用
        </div>
      </el-form-item>
      <el-form-item v-show="coupon.useType===1">
        <el-select
          v-model="selectProductCate"
          placeholder="请选择分类名称"
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
        <el-button @click="handleAddProductCategoryRelation()">添加</el-button>
        <el-table ref="productCateRelationTable"
                  :data="coupon.productCategoryRelationList"
                  style="width: 100%;margin-top: 20px"
                  border>
          <el-table-column label="分类名称" align="center">
            <template slot-scope="scope">
              <span v-if="scope.row.parentCategoryName">
                {{scope.row.parentCategoryName}} > {{scope.row.productCategoryName}}
              </span>
              <span v-else>
                {{scope.row.productCategoryName}}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="100">
            <template slot-scope="scope">
              <el-button size="mini"
                         type="text"
                         @click="handleDeleteProductCateRelation(scope.$index, scope.row)">删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>
      <el-form-item v-show="coupon.useType===2">
        <el-button @click="showProductSelectionDialog" type="primary" icon="el-icon-plus">选择商品</el-button>
        <div style="margin-top: 5px; color: #999; font-size: 12px;">
          点击按钮选择可用商品
        </div>
        <el-table ref="productRelationTable"
                  :data="coupon.productRelationList"
                  style="width: 100%;margin-top: 20px"
                  border>
          <el-table-column label="商品名称" align="center">
            <template slot-scope="scope">{{scope.row.productName}}</template>
          </el-table-column>
          <el-table-column label="货号" align="center"  width="120" >
            <template slot-scope="scope">NO.{{scope.row.productSn}}</template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="100">
            <template slot-scope="scope">
              <el-button size="mini"
                         type="text"
                         @click="handleDeleteProductRelation(scope.$index, scope.row)">删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>

      <!-- 排除商品分类配置 -->
      <el-form-item v-show="coupon.enableExclude" label="排除商品分类：">
        <el-select
          v-model="selectExcludeProductCate"
          placeholder="请选择要排除的分类"
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
        <el-button @click="handleAddProductCategoryExcludeRelation()">添加</el-button>
        <el-table ref="productCateExcludeRelationTable"
                  :data="coupon.productCategoryExcludeRelationList"
                  style="width: 100%;margin-top: 20px"
                  border>
          <el-table-column label="排除分类名称" align="center">
            <template slot-scope="scope">
              <span v-if="scope.row.parentCategoryName">
                {{scope.row.parentCategoryName}} > {{scope.row.productCategoryName}}
              </span>
              <span v-else>
                {{scope.row.productCategoryName}}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="100">
            <template slot-scope="scope">
              <el-button size="mini"
                         type="text"
                         @click="handleDeleteProductCateExcludeRelation(scope.$index, scope.row)">删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>

      <!-- 排除商品配置 -->
      <el-form-item v-show="coupon.enableExclude" label="排除商品：">
        <el-button @click="showExcludeProductSelectionDialog" type="primary" icon="el-icon-plus">选择排除商品</el-button>
        <div style="margin-top: 5px; color: #999; font-size: 12px;">
          点击按钮选择要排除的商品
        </div>
        <el-table ref="productExcludeRelationTable"
                  :data="coupon.productExcludeRelationList"
                  style="width: 100%;margin-top: 20px"
                  border>
          <el-table-column label="排除商品名称" align="center">
            <template slot-scope="scope">{{scope.row.productName}}</template>
          </el-table-column>
          <el-table-column label="货号" align="center"  width="120" >
            <template slot-scope="scope">NO.{{scope.row.productSn}}</template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="100">
            <template slot-scope="scope">
              <el-button size="mini"
                         type="text"
                         @click="handleDeleteProductExcludeRelation(scope.$index, scope.row)">删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>

      <el-form-item label="备注：">
        <el-input
          class="input-width"
          type="textarea"
          :rows="5"
          placeholder="请输入内容"
          v-model="coupon.note">
        </el-input>
      </el-form-item>
      <el-form-item label="关联学校：">
        <el-select
          v-model="selectedSchoolIds"
          multiple
          placeholder="请选择关联学校（不选择表示全局可用）"
          style="width: 400px;"
          @change="handleSchoolChange">
          <el-option
            v-for="school in schoolOptions"
            :key="school.value"
            :label="school.label"
            :value="school.value">
          </el-option>
        </el-select>
        <div style="margin-top: 5px; color: #999; font-size: 12px;">
          不选择学校表示该优惠券全局可用，选择学校后仅在对应学校的商品订单中可用
        </div>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit('couponFrom')">提交</el-button>
        <el-button v-if="!isEdit" @click="resetForm('couponFrom')">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 商品选择对话框 -->
    <product-selection-dialog
      ref="productSelectionDialog"
      :visible.sync="productSelectionVisible"
      :title="'选择可用商品'"
      :selection-mode="'multiple'"
      :selected-products="selectedProductsForDialog"
      @selection-confirmed="handleProductSelectionConfirm">
    </product-selection-dialog>

    <!-- 排除商品选择对话框 -->
    <product-selection-dialog
      ref="excludeProductSelectionDialog"
      :visible.sync="excludeProductSelectionVisible"
      :title="'选择排除商品'"
      :selection-mode="'multiple'"
      :selected-products="selectedExcludeProductsForDialog"
      @selection-confirmed="handleExcludeProductSelectionConfirm">
    </product-selection-dialog>
  </el-card>
</template>
<script>
  import {createCoupon,getCoupon,updateCoupon,getCouponSchools,updateCouponSchools} from '@/api/coupon';
  import {fetchSimpleList as fetchProductList} from '@/api/product';
  import {fetchListWithChildren} from '@/api/productCate';
  import {fetchList as fetchSchoolList} from '@/api/school';
  import ProductSelectionDialog from '@/components/ProductSelectionDialog';
  const defaultCoupon = {
    type: 0,
    name: null,
    platform: 0,
    amount: null,
    couponType: 0, // 默认为满减券
    discountRate: null,
    enableExclude: false, // 默认不启用排除逻辑
    perLimit: 1,
    minPoint: null,
    startTime: null,
    endTime: null,
    useType: 0,
    note: null,
    publishCount: null,
    productRelationList: [],
    productCategoryRelationList: [],
    productExcludeRelationList: [],
    productCategoryExcludeRelationList: []
  };
  const defaultTypeOptions = [
    {
      label: '全场赠券',
      value: 0
    },
    {
      label: '营销赠券',
      value: 1
    }
  ];
  const defaultPlatformOptions = [
    {
      label: '全平台',
      value: 0
    },
    {
      label: '移动平台',
      value: 1
    },
    {
      label: 'PC平台',
      value: 2
    }
  ];
  export default {
    name: 'CouponDetail',
    components: {
      ProductSelectionDialog
    },
    props: {
      isEdit: {
        type: Boolean,
        default: false
      }
    },
    data() {
      return {
        coupon: Object.assign({}, defaultCoupon),
        typeOptions: Object.assign({}, defaultTypeOptions),
        platformOptions: Object.assign({}, defaultPlatformOptions),
        rules: {
          name: [
            {required: true, message: '请输入优惠券名称', trigger: 'blur'},
            {min: 2, max: 140, message: '长度在 2 到 140 个字符', trigger: 'blur'}
          ],
          publishCount: [
            {type: 'number',required: true, message: '只能输入正整数', trigger: 'blur'}
          ],
          enableTime: [
            {type: 'date',required: true, message: '请选择日期', trigger: 'change'}
          ],
          amount: [
            {
              validator: (rule, value, callback) => {
                if (this.coupon.couponType === 0) {
                  if (!value || value <= 0) {
                    callback(new Error('满减券必须设置面额'));
                  } else if (value > 10000) {
                    callback(new Error('面额不能超过10000元'));
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
          discountRate: [
            {
              validator: (rule, value, callback) => {
                if (this.coupon.couponType === 1) {
                  if (!value || value <= 0 || value >= 1) {
                    callback(new Error('打折券折扣率必须在0.1-0.99之间'));
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
          minPoint: [
            {type: 'number',required: true,message: '只能输入正整数',trigger: 'blur'}
          ]
        },
        selectProduct:null,
        selectProductLoading: false,
        selectProductOptions:[],
        selectProductCate: null,
        selectExcludeProduct: null,
        selectExcludeProductCate: null,
        productCateOptions: [],
        selectedSchoolIds: [],
        schoolOptions: [],
        // 商品选择对话框相关
        productSelectionVisible: false,
        selectedProductsForDialog: [],
        excludeProductSelectionVisible: false,
        selectedExcludeProductsForDialog: []
      }
    },
    created(){
      this.loadSchoolOptions();
      if(this.isEdit){
        getCoupon(this.$route.query.id).then(response=>{
          this.coupon=response.data;
          // 将日期字符串转换为 Date 对象
          if (this.coupon.enableTime) {
            this.coupon.enableTime = new Date(this.coupon.enableTime);
          }
          if (this.coupon.startTime) {
            this.coupon.startTime = new Date(this.coupon.startTime);
          }
          if (this.coupon.endTime) {
            this.coupon.endTime = new Date(this.coupon.endTime);
          }
          // 加载优惠券关联的学校
          this.loadCouponSchools();
        });
      }
      this.getProductCateList();
    },
    methods:{
      onSubmit(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            // 最终校验：检查可用和排除列表是否有冲突
            if (!this.validateFinalConflicts()) {
              return;
            }

            this.$confirm('是否提交数据', '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            }).then(() => {
              if(this.isEdit){
                updateCoupon(this.$route.query.id,this.coupon).then(response=>{
                  // 更新学校关联
                  return updateCouponSchools(this.$route.query.id, this.selectedSchoolIds);
                }).then(response => {
                  this.$refs[formName].resetFields();
                  this.$message({
                    message: '修改成功',
                    type: 'success',
                    duration:1000
                  });
                  this.$router.back();
                }).catch(error => {
                  this.$message.error('保存失败：' + (error.message || '未知错误'));
                });
              }else{
                createCoupon(this.coupon).then(response=>{
                  // 创建成功后更新学校关联
                  const couponId = response.data;
                  if (couponId && this.selectedSchoolIds.length > 0) {
                    return updateCouponSchools(couponId, this.selectedSchoolIds);
                  }
                  return Promise.resolve();
                }).then(response => {
                  this.$refs[formName].resetFields();
                  this.$message({
                    message: '提交成功',
                    type: 'success',
                    duration:1000
                  });
                  this.$router.back();
                }).catch(error => {
                  this.$message.error('保存失败：' + (error.message || '未知错误'));
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
        this.coupon = Object.assign({},defaultCoupon);
      },
      searchProductMethod(query){
        if (query !== '') {
          this.loading = true;
          fetchProductList({keyword:query}).then(response=>{
            this.loading=false;
            let productList = response.data;
            this.selectProductOptions = [];
            for(let i=0;i<productList.length;i++){
              let item = productList[i];
              this.selectProductOptions.push({productId:item.id,productName:item.name,productSn:item.productSn});
            }
          });
        } else {
          this.selectProductOptions = [];
        }
      },
      handleAddProductRelation(){
        if(this.selectProduct===null){
          this.$message({
            message: '请先选择商品',
            type: 'warning'
          });
          return
        }
        this.coupon.productRelationList.push(this.getProductById(this.selectProduct));
        this.selectProduct=null;
      },
      handleDeleteProductRelation(index,row){
        this.coupon.productRelationList.splice(index,1);
      },
      handleAddProductCategoryRelation(){
        if(this.selectProductCate===null){
          this.$message({
            message: '请先选择商品分类',
            type: 'warning'
          });
          return
        }

        // 检查是否与排除分类冲突
        if(this.checkCategoryConflict(this.selectProductCate, 'include')){
          return;
        }

        // 检查是否已存在
        const exists = this.coupon.productCategoryRelationList.some(item =>
          item.productCategoryId === this.selectProductCate
        );
        if(exists){
          this.$message({
            message: '该分类已添加，请勿重复添加',
            type: 'warning'
          });
          return;
        }

        this.coupon.productCategoryRelationList.push(this.getProductCateById(this.selectProductCate));
        this.selectProductCate=null;
      },
      handleDeleteProductCateRelation(index,row){
        this.coupon.productCategoryRelationList.splice(index,1);
      },
      // 处理优惠券类型变化
      handleCouponTypeChange(value) {
        if (value === 0) {
          // 切换到满减券，清空折扣率
          this.coupon.discountRate = null;
        } else if (value === 1) {
          // 切换到打折券，清空面额
          this.coupon.amount = null;
        }
      },

      // 删除排除商品关系
      handleDeleteProductExcludeRelation(index,row){
        this.coupon.productExcludeRelationList.splice(index,1);
      },
      // 添加排除商品分类关系
      handleAddProductCategoryExcludeRelation(){
        if(this.selectExcludeProductCate===null){
          this.$message({
            message: '请先选择要排除的商品分类',
            type: 'warning'
          });
          return
        }

        // 检查是否与可用分类冲突
        if(this.checkCategoryConflict(this.selectExcludeProductCate, 'exclude')){
          return;
        }

        // 检查是否已存在
        const exists = this.coupon.productCategoryExcludeRelationList.some(item =>
          item.productCategoryId === this.selectExcludeProductCate
        );
        if(exists){
          this.$message({
            message: '该分类已添加到排除列表，请勿重复添加',
            type: 'warning'
          });
          return;
        }

        this.coupon.productCategoryExcludeRelationList.push(this.getProductCateById(this.selectExcludeProductCate));
        this.selectExcludeProductCate=null;
      },
      // 删除排除商品分类关系
      handleDeleteProductCateExcludeRelation(index,row){
        this.coupon.productCategoryExcludeRelationList.splice(index,1);
      },
      // 显示商品选择对话框
      showProductSelectionDialog() {
        // 重新构造数据，确保数据的一致性和完整性
        this.selectedProductsForDialog = this.coupon.productRelationList.map(item => {
          const productId = item.productId;
          return {
            id: typeof productId === 'string' ? parseInt(productId) : productId,
            name: item.productName,
            productSn: item.productSn || '',
            pic: item.pic || '',
            brandName: item.brandName || '',
            price: item.price || 0
          };
        });

        // 确保数据设置完成后再显示对话框
        this.$nextTick(() => {
          this.productSelectionVisible = true;
        });
      },
      // 处理商品选择确认
      handleProductSelectionConfirm(selectedProducts) {
        // 检查是否与排除商品冲突
        const conflictProducts = this.checkProductConflict(selectedProducts, 'include');
        if(conflictProducts.length > 0){
          this.$message({
            message: `以下商品已在排除列表中，请先移除：${conflictProducts.map(p => p.name).join('、')}`,
            type: 'error'
          });
          return;
        }

        // 保存完整商品对象，用于预选功能
        this.selectedProductsForDialog = [...selectedProducts];

        // 转换为业务需要的格式
        this.coupon.productRelationList = selectedProducts.map(product => ({
          productId: product.id,
          productName: product.name,
          productSn: product.productSn
        }));
      },
      // 显示排除商品选择对话框
      showExcludeProductSelectionDialog() {
        // 重新构造数据，确保数据的一致性和完整性
        this.selectedExcludeProductsForDialog = this.coupon.productExcludeRelationList.map(item => {
          const productId = item.productId;
          return {
            id: typeof productId === 'string' ? parseInt(productId) : productId,
            name: item.productName,
            productSn: item.productSn || '',
            pic: item.pic || '',
            brandName: item.brandName || '',
            price: item.price || 0
          };
        });

        // 确保数据设置完成后再显示对话框
        this.$nextTick(() => {
          this.excludeProductSelectionVisible = true;
        });
      },
      // 处理排除商品选择确认
      handleExcludeProductSelectionConfirm(selectedProducts) {
        // 检查是否与可用商品冲突
        const conflictProducts = this.checkProductConflict(selectedProducts, 'exclude');
        if(conflictProducts.length > 0){
          this.$message({
            message: `以下商品已在可用列表中，请先移除：${conflictProducts.map(p => p.name).join('、')}`,
            type: 'error'
          });
          return;
        }

        // 保存完整商品对象，用于预选功能
        this.selectedExcludeProductsForDialog = [...selectedProducts];

        // 转换为业务需要的格式
        this.coupon.productExcludeRelationList = selectedProducts.map(product => ({
          productId: product.id,
          productName: product.name,
          productSn: product.productSn
        }));
      },
      getProductById(id){
        for(let i=0;i<this.selectProductOptions.length;i++){
          if(id===this.selectProductOptions[i].productId){
            return this.selectProductOptions[i];
          }
        }
        return null;
      },
      getProductCateList() {
        fetchListWithChildren().then(response => {
          let list = response.data;
          this.productCateOptions = [];
          for (let i = 0; i < list.length; i++) {
            let children = [];
            if (list[i].children != null && list[i].children.length > 0) {
              for (let j = 0; j < list[i].children.length; j++) {
                children.push({label: list[i].children[j].name, value: list[i].children[j].id});
              }
            }
            this.productCateOptions.push({label: list[i].name, value: list[i].id, children: children});
          }
        });
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
        }).catch(error => {
          console.error('获取学校列表失败:', error);
        });
      },
      loadCouponSchools() {
        if (this.isEdit && this.$route.query.id) {
          getCouponSchools(this.$route.query.id).then(response => {
            if (response.data) {
              this.selectedSchoolIds = response.data.map(school => school.id);
            }
          }).catch(error => {
            console.error('获取优惠券关联学校失败:', error);
          });
        }
      },
      handleSchoolChange(schoolIds) {
        this.selectedSchoolIds = schoolIds;
      },
      getProductCateById(categoryId){
        // 先查找是否是一级分类
        for (let i = 0; i < this.productCateOptions.length; i++) {
          if (this.productCateOptions[i].value === categoryId) {
            // 是一级分类
            return {
              productCategoryId: categoryId,
              productCategoryName: this.productCateOptions[i].label,
              parentCategoryName: ''
            };
          }
          // 查找二级分类
          if (this.productCateOptions[i].children) {
            for (let j = 0; j < this.productCateOptions[i].children.length; j++) {
              if (this.productCateOptions[i].children[j].value === categoryId) {
                return {
                  productCategoryId: categoryId,
                  productCategoryName: this.productCateOptions[i].children[j].label,
                  parentCategoryName: this.productCateOptions[i].label
                };
              }
            }
          }
        }
        return null;
      },

      /**
       * 检查分类冲突
       * @param categoryId 要检查的分类ID
       * @param type 操作类型：'include'表示添加到可用列表，'exclude'表示添加到排除列表
       */
      checkCategoryConflict(categoryId, type) {
        if (type === 'include') {
          // 检查是否与排除分类冲突
          const conflictInExclude = this.coupon.productCategoryExcludeRelationList.some(item =>
            item.productCategoryId === categoryId
          );
          if (conflictInExclude) {
            this.$message({
              message: '该分类已在排除列表中，不能同时设为可用分类',
              type: 'error'
            });
            return true;
          }

          // 检查父子分类冲突
          const parentChildConflict = this.checkParentChildCategoryConflict(categoryId, 'exclude');
          if (parentChildConflict) {
            this.$message({
              message: '该分类与排除列表中的父/子分类存在冲突',
              type: 'error'
            });
            return true;
          }
        } else if (type === 'exclude') {
          // 检查是否与可用分类冲突
          const conflictInInclude = this.coupon.productCategoryRelationList.some(item =>
            item.productCategoryId === categoryId
          );
          if (conflictInInclude) {
            this.$message({
              message: '该分类已在可用列表中，不能同时设为排除分类',
              type: 'error'
            });
            return true;
          }

          // 检查父子分类冲突
          const parentChildConflict = this.checkParentChildCategoryConflict(categoryId, 'include');
          if (parentChildConflict) {
            this.$message({
              message: '该分类与可用列表中的父/子分类存在冲突',
              type: 'error'
            });
            return true;
          }
        }
        return false;
      },

      /**
       * 检查父子分类冲突
       * @param categoryId 要检查的分类ID
       * @param listType 要检查的列表类型：'include'或'exclude'
       */
      checkParentChildCategoryConflict(categoryId, listType) {
        const targetList = listType === 'include'
          ? this.coupon.productCategoryRelationList
          : this.coupon.productCategoryExcludeRelationList;

        // 获取当前分类信息
        const currentCategoryInfo = this.getCategoryInfo(categoryId);
        if (!currentCategoryInfo) return false;

        for (let item of targetList) {
          const existingCategoryInfo = this.getCategoryInfo(item.productCategoryId);
          if (!existingCategoryInfo) continue;

          // 检查是否是父子关系
          if (this.isParentChildRelation(currentCategoryInfo, existingCategoryInfo)) {
            return true;
          }
        }
        return false;
      },

      /**
       * 获取分类信息
       */
      getCategoryInfo(categoryId) {
        // 查找一级分类
        for (let group of this.productCateOptions) {
          if (group.value === categoryId) {
            return { id: categoryId, name: group.label, level: 1, parentId: null };
          }
          // 查找二级分类
          if (group.children) {
            for (let child of group.children) {
              if (child.value === categoryId) {
                return {
                  id: categoryId,
                  name: child.label,
                  level: 2,
                  parentId: group.value
                };
              }
            }
          }
        }
        return null;
      },

      /**
       * 检查两个分类是否是父子关系
       */
      isParentChildRelation(category1, category2) {
        // 如果其中一个是另一个的父分类
        return (category1.level === 1 && category2.level === 2 && category2.parentId === category1.id) ||
               (category2.level === 1 && category1.level === 2 && category1.parentId === category2.id);
      },

      /**
       * 检查商品冲突
       * @param products 要检查的商品列表
       * @param type 操作类型：'include'或'exclude'
       */
      checkProductConflict(products, type) {
        const conflictProducts = [];
        const targetList = type === 'include'
          ? this.coupon.productExcludeRelationList
          : this.coupon.productRelationList;

        for (let product of products) {
          const exists = targetList.some(item => item.productId === product.id);
          if (exists) {
            conflictProducts.push(product);
          }
        }
        return conflictProducts;
      },

      /**
       * 最终校验：检查可用和排除列表是否有冲突
       */
      validateFinalConflicts() {
        // 检查分类冲突
        const categoryConflicts = [];
        const productConflicts = [];

        // 检查分类直接冲突
        for (let includeCategory of this.coupon.productCategoryRelationList) {
          const conflict = this.coupon.productCategoryExcludeRelationList.find(excludeCategory =>
            excludeCategory.productCategoryId === includeCategory.productCategoryId
          );
          if (conflict) {
            categoryConflicts.push(includeCategory.productCategoryName);
          }
        }

        // 检查分类父子冲突
        for (let includeCategory of this.coupon.productCategoryRelationList) {
          for (let excludeCategory of this.coupon.productCategoryExcludeRelationList) {
            const includeCategoryInfo = this.getCategoryInfo(includeCategory.productCategoryId);
            const excludeCategoryInfo = this.getCategoryInfo(excludeCategory.productCategoryId);

            if (includeCategoryInfo && excludeCategoryInfo &&
                this.isParentChildRelation(includeCategoryInfo, excludeCategoryInfo)) {
              categoryConflicts.push(`${includeCategory.productCategoryName} 与 ${excludeCategory.productCategoryName}`);
            }
          }
        }

        // 检查商品冲突
        for (let includeProduct of this.coupon.productRelationList) {
          const conflict = this.coupon.productExcludeRelationList.find(excludeProduct =>
            excludeProduct.productId === includeProduct.productId
          );
          if (conflict) {
            productConflicts.push(includeProduct.productName);
          }
        }

        // 显示冲突信息
        if (categoryConflicts.length > 0) {
          this.$message({
            message: `以下分类在可用和排除列表中存在冲突：${categoryConflicts.join('、')}`,
            type: 'error'
          });
          return false;
        }

        if (productConflicts.length > 0) {
          this.$message({
            message: `以下商品在可用和排除列表中存在冲突：${productConflicts.join('、')}`,
            type: 'error'
          });
          return false;
        }

        // 检查业务逻辑合理性
        if (this.coupon.enableExclude &&
            this.coupon.productExcludeRelationList.length === 0 &&
            this.coupon.productCategoryExcludeRelationList.length === 0) {
          this.$message({
            message: '已启用排除逻辑，但未配置任何排除商品或分类',
            type: 'warning'
          });
        }

        return true;
      }
    },

  }
</script>
<style scoped>
  .input-width {
    width: 60%;
  }
</style>


