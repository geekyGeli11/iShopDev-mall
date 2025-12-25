<template> 
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
      <div>
        <i class="el-icon-search"></i>
        <span>筛选搜索</span>
        <el-button
          style="float: right"
          @click="handleSearchListWithState()"
          type="primary"
          size="small">
          查询结果
        </el-button>
        <el-button
          style="float: right;margin-right: 15px"
          @click="handleResetSearchWithState()"
          size="small">
          重置
        </el-button>
      </div>
      <div style="margin-top: 15px">
        <el-form :inline="true" :model="listQuery" size="small" label-width="140px">
          <el-form-item label="输入搜索：">
            <el-input style="width: 203px" v-model="listQuery.keyword" placeholder="商品名称"></el-input>
          </el-form-item>
          <el-form-item label="商品货号：">
            <el-input style="width: 203px" v-model="listQuery.productSn" placeholder="商品货号/SKU编码"></el-input>
          </el-form-item>
          <el-form-item label="关联学校：">
            <el-select v-model="listQuery.schoolId" placeholder="请选择学校" clearable :disabled="isStoreAdmin">
              <el-option
                v-for="item in schoolOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="商品分类：">
            <el-select
              v-model="selectProductCateValue"
              placeholder="商品分类"
              clearable
              style="width: 200px">
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
          <el-form-item label="商品品牌：">
            <el-select v-model="listQuery.brandId" placeholder="请选择品牌" clearable>
              <el-option
                v-for="item in brandOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="上架状态：">
            <el-select v-model="listQuery.publishStatus" placeholder="全部" clearable>
              <el-option
                v-for="item in publishStatusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="DIY状态：">
            <el-select v-model="listQuery.diyEnabled" placeholder="全部" clearable>
              <el-option
                v-for="item in diyStatusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <!-- <el-form-item label="审核状态：">
            <el-select v-model="listQuery.verifyStatus" placeholder="全部" clearable>
              <el-option
                v-for="item in verifyStatusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item> -->
        </el-form>
      </div>
    </el-card>
    <el-card class="operate-container" shadow="never">
      <i class="el-icon-tickets"></i>
      <span>数据列表</span>
      <el-button
        class="btn-add"
        @click="handleAddProduct()"
        size="mini">
        添加
      </el-button>
      <el-button
        style="margin-left: 10px"
        @click="handleExportProducts()"
        size="mini"
        type="success">
        导出
      </el-button>
    </el-card>
    <div class="table-container">
      <el-table ref="productTable"
                :data="list"
                style="width: 100%"
                @selection-change="handleSelectionChange"
                v-loading="listLoading"
                border>
        <el-table-column type="selection" width="60" align="center"></el-table-column>
        <el-table-column label="编号" width="100" align="center">
          <template slot-scope="scope">{{scope.row.id}}</template>
        </el-table-column>
        <el-table-column label="商品图片" width="120" align="center">
          <template slot-scope="scope"><img style="height: 80px" :src="scope.row.pic"></template>
        </el-table-column>
        <el-table-column label="商品名称" align="center">
          <template slot-scope="scope">
            <p>{{scope.row.name}}</p>
            <p>品牌：{{scope.row.brandName}}</p>
          </template>
        </el-table-column>
        <el-table-column label="价格/货号" width="120" align="center">
          <template slot-scope="scope">
            <p>价格：￥{{scope.row.price}}</p>
            <p>货号：{{scope.row.productSn}}</p>
          </template>
        </el-table-column>
        <el-table-column label="商品分类" width="120" align="center">
          <template slot-scope="scope">{{scope.row.productCategoryName || '无'}}</template>
        </el-table-column>
        <el-table-column label="状态" width="120" align="center">
          <template slot-scope="scope">
            <p>上架：
              <el-switch
                @change="handlePublishStatusChange(scope.$index, scope.row)"
                :active-value="1"
                :inactive-value="0"
                v-model="scope.row.publishStatus">
              </el-switch>
            </p>
            <p>推荐：
              <el-switch
                @change="handleRecommendStatusChange(scope.$index, scope.row)"
                :active-value="1"
                :inactive-value="0"
                v-model="scope.row.recommandStatus">
              </el-switch>
            </p>
            <p>DIY：
              <el-switch
                @change="handleDiyStatusChange(scope.$index, scope.row)"
                :active-value="1"
                :inactive-value="0"
                v-model="scope.row.diyEnabled">
              </el-switch>
            </p>
          </template>
        </el-table-column>
        <el-table-column label="运费模板" width="120" align="center">
          <template slot-scope="scope">{{scope.row.freightTemplateName || '无'}}</template>
        </el-table-column>
        <el-table-column label="关联学校" width="150" align="center">
          <template slot-scope="scope">{{scope.row.schoolNames || '无'}}</template>
        </el-table-column>
        <el-table-column label="排序" width="100" align="center">
          <template slot-scope="scope">{{scope.row.sort}}</template>
        </el-table-column>
        <el-table-column label="SKU库存" width="100" align="center">
          <template slot-scope="scope">
            <el-button type="primary" icon="el-icon-edit" @click="handleShowSkuEditDialog(scope.$index, scope.row)" circle></el-button>
          </template>
        </el-table-column>
        <el-table-column label="销量" width="100" align="center">
          <template slot-scope="scope">{{scope.row.sale}}</template>
        </el-table-column>
        <!-- <el-table-column label="审核状态" width="100" align="center">
          <template slot-scope="scope">
            <p>{{scope.row.verifyStatus | verifyStatusFilter}}</p>
            <p>
              <el-button
                type="text"
                @click="handleShowVerifyDetail(scope.$index, scope.row)">审核详情
              </el-button>
            </p>
          </template>
        </el-table-column> -->
        <el-table-column label="操作" width="320" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              @click="handleShowProduct(scope.$index, scope.row)">查看
            </el-button>
            <el-button
              size="mini"
              @click="handleUpdateProduct(scope.$index, scope.row)">编辑
            </el-button>
            <el-button
              size="mini"
              type="text"
              @click="handleShare(scope.$index, scope.row)">分享
            </el-button>
            <el-button
              size="mini"
              type="danger"
              @click="handleDelete(scope.$index, scope.row)">删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="batch-operate-container">
      <el-select
        size="small"
        v-model="operateType" placeholder="批量操作">
        <el-option
          v-for="item in operates"
          :key="item.value"
          :label="item.label"
          :value="item.value">
        </el-option>
      </el-select>
      <el-button
        style="margin-left: 20px"
        class="search-button"
        @click="handleBatchOperate()"
        type="primary"
        size="small">
        确定
      </el-button>
    </div>
    <div class="pagination-container">
      <el-pagination
        background
        @size-change="handleSizeChangeWithState"
        @current-change="handleCurrentChangeWithState"
        layout="total, sizes,prev, pager, next,jumper"
        :page-size="listQuery.pageSize"
        :page-sizes="[5,10,15]"
        :current-page.sync="listQuery.pageNum"
        :total="total">
      </el-pagination>
    </div>

    <!-- 分享弹窗 -->
    <el-dialog
      title="商品分享"
      :visible.sync="shareDialogVisible"
      width="600px"
      @close="handleShareDialogClose">
      <div v-if="shareInfo">
        <el-row :gutter="20">
          <el-col :span="24">
            <h4>{{ shareInfo.productName }}</h4>
            <p>{{ shareInfo.shareDescription }}</p>
          </el-col>
        </el-row>

        <el-row :gutter="20" style="margin-top: 20px;">
          <el-col :span="12">
            <div class="share-item">
              <h5>分享链接</h5>
              <el-input
                v-model="shareInfo.shareLink"
                readonly
                type="textarea"
                :rows="3"
                placeholder="分享链接">
                <template slot="append">
                  <el-button @click="copyToClipboard(shareInfo.shareLink)">复制</el-button>
                </template>
              </el-input>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="share-item">
              <h5>小程序码</h5>
              <div class="qrcode-container">
                <img
                  v-if="shareInfo.miniProgramCodeBase64"
                  :src="shareInfo.miniProgramCodeBase64"
                  alt="小程序码"
                  style="width: 150px; height: 150px;" />
                <div v-else class="qrcode-placeholder">
                  <i class="el-icon-picture"></i>
                  <p>{{ shareInfo.miniProgramCodeUrl || '小程序码生成失败' }}</p>
                </div>
                <div style="margin-top: 10px;">
                  <el-button
                    size="small"
                    @click="downloadQRCode"
                    :disabled="!shareInfo.miniProgramCodeBase64">
                    下载小程序码
                  </el-button>
                  <el-button
                    size="small"
                    @click="copyQRCodeBase64"
                    :disabled="!shareInfo.miniProgramCodeBase64">
                    复制图片
                  </el-button>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <div v-if="shareLoading" style="text-align: center; padding: 40px;">
        <i class="el-icon-loading"></i>
        <p>正在生成分享信息...</p>
      </div>

      <span slot="footer" class="dialog-footer">
        <el-button @click="shareDialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>

    <el-dialog
      title="编辑货品信息"
      :visible.sync="editSkuInfo.dialogVisible"
      width="80%">
      <div>
        <div style="margin-bottom: 15px;">
          <span style="font-weight: bold;">商品货号：</span>
          <span>{{editSkuInfo.productSn}}</span>
          <el-input placeholder="按sku编号搜索" v-model="editSkuInfo.keyword" size="small" style="width: 50%;margin-left: 20px">
            <el-button slot="append" icon="el-icon-search" @click="handleSearchEditSku"></el-button>
          </el-input>
        </div>
        
        <!-- SKU列表 -->
        <el-table style="width: 100%;margin-top: 20px"
                  :data="editSkuInfo.stockList"
                  border
                  v-loading="editSkuInfo.loading"
                  row-key="id"
                  :expand-row-keys="editSkuInfo.expandedRows"
                  @expand-change="handleExpandChange">
          <el-table-column type="expand" width="50">
            <template slot-scope="scope">
              <div style="padding: 20px; background-color: #f5f7fa;">
                <div style="margin-bottom: 10px;">
                  <strong>门店库存分布</strong>
                </div>
                <el-table
                  :data="scope.row.storeStocks || []"
                  style="width: 100%"
                  border
                  size="small">
                  <el-table-column
                    label="门店名称"
                    width="200"
                    align="center">
                    <template slot-scope="storeScope">
                      {{ getStoreName(storeScope.row.storeId) }}
                    </template>
                  </el-table-column>
                  <el-table-column
                    label="库存数量"
                    width="120"
                    align="center">
                    <template slot-scope="storeScope">
                      {{ storeScope.row.stock }}
                    </template>
                  </el-table-column>
                  <el-table-column
                    label="库存预警值"
                    width="120"
                    align="center">
                    <template slot-scope="storeScope">
                      {{ storeScope.row.lowStock || '-' }}
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </template>
          </el-table-column>
          <el-table-column
            label="SKU编号"
            width="120"
            align="center">
            <template slot-scope="scope">
              <el-input v-model="scope.row.skuCode" disabled></el-input>
            </template>
          </el-table-column>
          <el-table-column
            v-for="(item,index) in editSkuInfo.productAttr"
            :label="item.name"
            :key="item.id"
            width="100"
            align="center">
            <template slot-scope="scope">
              {{getProductSkuSp(scope.row,index)}}
            </template>
          </el-table-column>
          <el-table-column
            label="销售价格"
            width="100"
            align="center">
            <template slot-scope="scope">
              <el-input v-model="scope.row.price"></el-input>
            </template>
          </el-table-column>
          <el-table-column
            label="商品库存"
            width="100"
            align="center">
            <template slot-scope="scope">
              <el-input v-model="scope.row.stock" disabled></el-input>
            </template>
          </el-table-column>
          <el-table-column
            label="库存预警值"
            width="100"
            align="center">
            <template slot-scope="scope">
              <el-input v-model="scope.row.lowStock" disabled></el-input>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editSkuInfo.dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleEditSkuConfirm">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog
      title="选择关联学校"
      :visible.sync="schoolSelectDialog.visible"
      width="50%">
      <el-checkbox-group v-model="schoolSelectDialog.selectedSchools">
        <el-checkbox
          v-for="school in schoolOptions"
          :key="school.value"
          :label="school.value">
          {{school.label}}
        </el-checkbox>
      </el-checkbox-group>
      <span slot="footer" class="dialog-footer">
        <el-button @click="schoolSelectDialog.visible = false">取 消</el-button>
        <el-button type="primary" @click="handleSchoolSelectConfirm">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog
      title="商品详情"
      :visible.sync="productDetailDialog.visible"
      width="80%"
      :close-on-click-modal="false">
      <div v-if="productDetailDialog.product">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form label-width="120px">
              <el-form-item label="商品ID：">
                <span>{{productDetailDialog.product.id}}</span>
              </el-form-item>
              <el-form-item label="商品名称：">
                <span>{{productDetailDialog.product.name}}</span>
              </el-form-item>
              <el-form-item label="商品货号：">
                <span>{{productDetailDialog.product.productSn}}</span>
              </el-form-item>
              <el-form-item label="品牌名称：">
                <span>{{productDetailDialog.product.brandName}}</span>
              </el-form-item>
              <el-form-item label="商品分类：">
                <span>{{productDetailDialog.product.productCategoryName}}</span>
              </el-form-item>
              <el-form-item label="商品价格：">
                <span>￥{{productDetailDialog.product.price}}</span>
              </el-form-item>
              <el-form-item label="促销价格：">
                <span>{{productDetailDialog.product.promotionPrice ? '￥' + productDetailDialog.product.promotionPrice : '无'}}</span>
              </el-form-item>
              <el-form-item label="商品库存：">
                <span>{{productDetailDialog.product.stock}}</span>
              </el-form-item>
              <el-form-item label="销量：">
                <span>{{productDetailDialog.product.sale}}</span>
              </el-form-item>
              <el-form-item label="商品单位：">
                <span>{{productDetailDialog.product.unit}}</span>
              </el-form-item>
              <el-form-item label="商品重量：">
                <span>{{productDetailDialog.product.weight}}克</span>
              </el-form-item>
            </el-form>
          </el-col>
          <el-col :span="12">
            <el-form label-width="120px">
              <el-form-item label="上架状态：">
                <el-tag :type="productDetailDialog.product.publishStatus === 1 ? 'success' : 'danger'">
                  {{productDetailDialog.product.publishStatus === 1 ? '已上架' : '已下架'}}
                </el-tag>
              </el-form-item>
              <el-form-item label="DIY状态：">
                <el-tag :type="productDetailDialog.product.diyEnabled === 1 ? 'success' : 'info'">
                  {{productDetailDialog.product.diyEnabled === 1 ? '支持DIY' : '不支持DIY'}}
                </el-tag>
              </el-form-item>
              <el-form-item label="运费模板：">
                <span>{{productDetailDialog.product.freightTemplateName || '无'}}</span>
              </el-form-item>
              <el-form-item label="关联学校：">
                <span>{{productDetailDialog.product.schoolNames || '无'}}</span>
              </el-form-item>
              <el-form-item label="商品副标题：">
                <span>{{productDetailDialog.product.subTitle}}</span>
              </el-form-item>
              <el-form-item label="商品关键词：">
                <span>{{productDetailDialog.product.keywords}}</span>
              </el-form-item>
              <el-form-item label="商品备注：">
                <span>{{productDetailDialog.product.note}}</span>
              </el-form-item>
              <el-form-item label="商品服务：">
                <span>{{productDetailDialog.product.serviceIds}}</span>
              </el-form-item>
            </el-form>
          </el-col>
        </el-row>
        <el-row v-if="productDetailDialog.product.pic">
          <el-col :span="24">
            <el-form label-width="120px">
              <el-form-item label="商品图片：">
                <img :src="productDetailDialog.product.pic" style="width: 200px; height: 200px; object-fit: cover;">
              </el-form-item>
            </el-form>
          </el-col>
        </el-row>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="productDetailDialog.visible = false">关 闭</el-button>
      </span>
    </el-dialog>
    <el-dialog
      title="选择商品分类"
      :visible.sync="categorySelectDialog.visible"
      width="50%">
      <el-select
        v-model="categorySelectDialog.selectedCategory"
        placeholder="请选择分类"
        clearable
        style="width: 100%">
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
      <span slot="footer" class="dialog-footer">
        <el-button @click="categorySelectDialog.visible = false">取 消</el-button>
        <el-button type="primary" @click="handleCategorySelectConfirm">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
  import {
    fetchListWithDetails,
    updateDeleteStatus,
    updateNewStatus,
    updateRecommendStatus,
    updatePublishStatus,
    updateProductDiyStatus,
    updateProductSchools,
    updateProductCategory,
    exportProducts,
    generateShareInfo
  } from '@/api/product'
  import {fetchList as fetchSkuStockList,update as updateSkuStockList} from '@/api/skuStock'
  import {fetchList as fetchProductAttrList} from '@/api/productAttr'
  import {fetchList as fetchBrandList} from '@/api/brand'
  import {fetchListWithChildren} from '@/api/productCate'
  import {fetchEnabledSchools} from '@/api/school'
  import {fetchStoreSkuStockBySku, fetchStoreSkuStockByProduct} from '@/api/storeSkuStock'
  import {getAllStores} from '@/api/storeAddress'
  import { mapState } from 'vuex'

  const defaultListQuery = {
    keyword: null,
    pageNum: 1,
    pageSize: 5,
    publishStatus: null,
    verifyStatus: null,
    productSn: null,
    productCategoryId: null,
    brandId: null,
    diyEnabled: null,
    schoolId: null
  };
  import listStateMixin from '@/mixins/listStateMixin'

  export default {
    name: "productList",
    mixins: [listStateMixin],
    data() {
      return {
        // 设置页面标识用于状态管理
        pageKey: 'productList',
        editSkuInfo:{
          dialogVisible:false,
          productId:null,
          productSn:'',
          productAttributeCategoryId:null,
          stockList:[],
          productAttr:[],
          keyword:null,
          loading:false,
          expandedRows:[]
        },
        storeOptions:[],
        operates: [
          {
            label: "商品上架",
            value: "publishOn"
          },
          {
            label: "商品下架",
            value: "publishOff"
          },
          {
            label: "设为推荐",
            value: "recommendOn"
          },
          {
            label: "取消推荐",
            value: "recommendOff"
          },
          {
            label: "设为新品",
            value: "newOn"
          },
          {
            label: "取消新品",
            value: "newOff"
          },
          {
            label: "转移到分类",
            value: "transferCategory"
          },
          {
            label: "移入回收站",
            value: "recycle"
          },
          {
            label: "启用DIY",
            value: "diyOn"
          },
          {
            label: "禁用DIY",
            value: "diyOff"
          },
          {
            label: "批量修改关联学校",
            value: "updateSchools"
          },
          {
            label: "批量转移到分类",
            value: "updateCategory"
          }
        ],
        operateType: null,
        listQuery: Object.assign({}, defaultListQuery),
        list: null,
        total: null,
        listLoading: true,
        selectProductCateValue: null,
        multipleSelection: [],
        productCateOptions: [],
        brandOptions: [],
        schoolOptions: [],
        publishStatusOptions: [{
          value: 1,
          label: '上架'
        }, {
          value: 0,
          label: '下架'
        }],
        verifyStatusOptions: [{
          value: 1,
          label: '审核通过'
        }, {
          value: 0,
          label: '未审核'
        }],
        diyStatusOptions: [{
          value: 1,
          label: '支持DIY'
        }, {
          value: 0,
          label: '不支持DIY'
        }],
        schoolSelectDialog: {
          visible: false,
          selectedSchools: []
        },
        productDetailDialog: {
          visible: false,
          product: {}
        },
        categorySelectDialog: {
          visible: false,
          selectedCategory: null
        },
        shareDialogVisible: false,
        shareInfo: null,
        shareLoading: false
      }
    },
    computed: {
      ...mapState('user', ['adminType', 'schoolId', 'schoolName']),
      // 是否为门店账号
      isStoreAdmin() {
        return this.adminType === true
      }
    },
    created() {
      // 检查路由参数，如果是从库存紧张跳转过来的则显示提示
      if (this.$route.query.lowStock === 'true') {
        this.$message.info('请在SKU库存列中查看和管理商品库存，点击编辑按钮可查看库存预警值');
      }

      // 如果是门店账号，自动设置学校筛选条件
      if (this.isStoreAdmin && this.schoolId) {
        this.listQuery.schoolId = this.schoolId;
      }

      // 如果没有保存的状态，则正常加载数据
      if (!this.$store.getters['listState/hasListState'](this.pageKey)) {
        this.getList();
      }
      this.getBrandList();
      this.getProductCateList();
      this.getSchoolList();
      this.loadStores();
    },
    watch: {
      selectProductCateValue: function (newValue) {
        if (newValue != null) {
          // 直接使用选择的分类ID（支持一级分类和二级分类）
          this.listQuery.productCategoryId = newValue;
        } else {
          this.listQuery.productCategoryId = null;
        }

      }
    },
    filters: {
      verifyStatusFilter(value) {
        if (value === 1) {
          return '审核通过';
        } else {
          return '未审核';
        }
      }
    },
    methods: {
      getProductSkuSp(row, index) {
        let spData = JSON.parse(row.spData);
        if(spData!=null&&index<spData.length){
          return spData[index].value;
        }else{
          return null;
        }
      },
      getList() {
        this.listLoading = true;
        fetchListWithDetails(this.listQuery).then(response => {
          this.listLoading = false;
          this.list = response.data.list;
          this.total = response.data.total;
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
      getSchoolList() {
        fetchEnabledSchools().then(response => {
          this.schoolOptions = [];
          let schoolList = response.data;
          for (let i = 0; i < schoolList.length; i++) {
            this.schoolOptions.push({label: schoolList[i].schoolName, value: schoolList[i].id});
          }
        });
      },
      handleShowSkuEditDialog(index,row){
        this.editSkuInfo.dialogVisible=true;
        this.editSkuInfo.productId=row.id;
        this.editSkuInfo.productSn=row.productSn;
        this.editSkuInfo.productAttributeCategoryId = row.productAttributeCategoryId;
        this.editSkuInfo.keyword=null;
        this.editSkuInfo.loading=true;
        // 获取SKU列表
        fetchSkuStockList(row.id,{keyword:this.editSkuInfo.keyword}).then(response=>{
          // 直接使用返回的SKU列表，每条记录就是一个独立的SKU
          const skuList = response.data || [];
          // 为每个SKU添加空的storeStocks数组
          this.editSkuInfo.stockList = skuList.map(sku => ({
            ...sku,
            storeStocks: []
          }));
          // 获取门店库存信息
          this.loadStoreStocksForSkus(row.id);
        }).finally(() => {
          this.editSkuInfo.loading=false;
        });
        if(row.productAttributeCategoryId!=null){
          fetchProductAttrList(row.productAttributeCategoryId,{type:0}).then(response=>{
            this.editSkuInfo.productAttr=response.data.list;
          });
        }
      },
      // 加载SKU的门店库存信息
      loadStoreStocksForSkus(productId) {
        fetchStoreSkuStockByProduct(productId).then(response => {
          if (response.data && Array.isArray(response.data)) {
            // 按skuId分组门店库存
            const storeStockMap = {};
            response.data.forEach(item => {
              if (!storeStockMap[item.skuId]) {
                storeStockMap[item.skuId] = [];
              }
              storeStockMap[item.skuId].push({
                storeId: item.storeId,
                stock: item.stock,
                lowStock: item.lowStock
              });
            });
            // 将门店库存信息添加到对应的SKU，使用$set确保响应式更新
            this.editSkuInfo.stockList.forEach((sku, index) => {
              this.$set(this.editSkuInfo.stockList[index], 'storeStocks', storeStockMap[sku.id] || []);
            });
          }
        }).catch(error => {
          console.error('获取门店库存信息失败:', error);
        });
      },
      handleSearchEditSku(){
        this.editSkuInfo.loading=true;
        fetchSkuStockList(this.editSkuInfo.productId,{keyword:this.editSkuInfo.keyword}).then(response=>{
          // 直接使用返回的SKU列表，每条记录就是一个独立的SKU
          const skuList = response.data || [];
          // 为每个SKU添加空的storeStocks数组
          this.editSkuInfo.stockList = skuList.map(sku => ({
            ...sku,
            storeStocks: []
          }));
          // 获取门店库存信息
          this.loadStoreStocksForSkus(this.editSkuInfo.productId);
        }).finally(() => {
          this.editSkuInfo.loading=false;
        });
      },
      handleEditSkuConfirm(){
        if(this.editSkuInfo.stockList==null||this.editSkuInfo.stockList.length<=0){
          this.$message({
            message: '暂无sku信息',
            type: 'warning',
            duration: 1000
          });
          return
        }
        this.$confirm('是否要进行修改', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(()=>{
          // 直接提交SKU列表（不包含门店库存信息）
          updateSkuStockList(this.editSkuInfo.productId, this.editSkuInfo.stockList).then(response=>{
            this.$message({
              message: '修改成功',
              type: 'success',
              duration: 1000
            });
            this.editSkuInfo.dialogVisible=false;
          });
        });
      },
      handleExpandChange(row, expandedRows){
        // 更新展开行的keys
        this.editSkuInfo.expandedRows = expandedRows.map(r => r.id);
        // 展开行时加载门店库存数据（如果尚未加载或为空）
        if(!row.storeStocks || row.storeStocks.length === 0){
          fetchStoreSkuStockBySku(row.id).then(response=>{
            this.$set(row, 'storeStocks', response.data || []);
          }).catch(error=>{
            this.$message.error('获取门店库存分布失败：' + (error.message || '未知错误'));
          });
        }
      },
      loadStores(){
        getAllStores().then(response=>{
          this.storeOptions = [];
          if(response.data && Array.isArray(response.data)){
            for(let i = 0; i < response.data.length; i++){
              const store = response.data[i];
              const label = store.isWarehouse ? `${store.addressName} [地库]` : store.addressName;
              this.storeOptions.push({
                label: label,
                value: store.id
              });
            }
          }
        }).catch(error=>{
          console.error('获取门店列表失败:', error);
        });
      },
      getStoreName(storeId){
        if(!storeId) return '';
        const store = this.storeOptions.find(item => item.value === storeId);
        return store ? store.label : '未知门店';
      },
      handleSearchList() {
        this.listQuery.pageNum = 1;
        this.getList();
      },
      handleAddProduct() {
        this.$router.push({path:'/pms/addProduct'});
      },
      handleBatchOperate() {
        if(this.operateType==null){
          this.$message({
            message: '请选择操作类型',
            type: 'warning',
            duration: 1000
          });
          return;
        }
        if(this.multipleSelection==null||this.multipleSelection.length<1){
          this.$message({
            message: '请选择要操作的商品',
            type: 'warning',
            duration: 1000
          });
          return;
        }
        this.$confirm('是否要进行该批量操作?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          let ids=[];
          for(let i=0;i<this.multipleSelection.length;i++){
            ids.push(this.multipleSelection[i].id);
          }
          switch (this.operateType) {
            case this.operates[0].value:
              this.updatePublishStatus(1,ids);
              break;
            case this.operates[1].value:
              this.updatePublishStatus(0,ids);
              break;
            case this.operates[2].value:
              this.updateRecommendStatus(1,ids);
              break;
            case this.operates[3].value:
              this.updateRecommendStatus(0,ids);
              break;
            case this.operates[4].value:
              this.updateNewStatus(1,ids);
              break;
            case this.operates[5].value:
              this.updateNewStatus(0,ids);
              break;
            case this.operates[6].value:
              break;
            case this.operates[7].value:
              this.updateDeleteStatus(1,ids);
              break;
            case this.operates[8].value:
              this.updateDiyStatus(1,ids);
              break;
            case this.operates[9].value:
              this.updateDiyStatus(0,ids);
              break;
            case this.operates[10].value:
              this.handleBatchUpdateSchools();
              return; // 不执行后续的getList()
            case this.operates[11].value:
              this.handleBatchUpdateCategory();
              return; // 不执行后续的getList()
            default:
              break;
          }
          this.getList();
        });
      },
      handleSizeChange(val) {
        this.listQuery.pageNum = 1;
        this.listQuery.pageSize = val;
        this.getList();
      },
      handleCurrentChange(val) {
        this.listQuery.pageNum = val;
        this.getList();
      },
      handleSelectionChange(val) {
        this.multipleSelection = val;
      },
      handlePublishStatusChange(index, row) {
        let ids = [];
        ids.push(row.id);
        this.updatePublishStatus(row.publishStatus, ids);
      },

      handleDiyStatusChange(index, row) {
        let ids = [];
        ids.push(row.id);
        this.updateDiyStatus(row.diyEnabled, ids);
      },
      handleRecommendStatusChange(index, row) {
        let ids = [];
        ids.push(row.id);
        this.updateRecommendStatus(row.recommandStatus, ids);
      },
      handleResetSearch() {
        this.selectProductCateValue = null;
        this.listQuery = Object.assign({}, defaultListQuery);
      },
      handleDelete(index, row){
        this.$confirm('是否要进行删除操作?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          let ids = [];
          ids.push(row.id);
          this.updateDeleteStatus(1,ids);
        });
      },
      handleUpdateProduct(index,row){
        this.$router.push({path:'/pms/updateProduct',query:{id:row.id}});
      },
      handleShowProduct(index,row){
        this.productDetailDialog.visible = true;
        this.productDetailDialog.product = row;
      },
      handleShowVerifyDetail(index,row){
        console.log("handleShowVerifyDetail",row);
      },
      updatePublishStatus(publishStatus, ids) {
        let params = new URLSearchParams();
        params.append('ids', ids);
        params.append('publishStatus', publishStatus);
        updatePublishStatus(params).then(response => {
          this.$message({
            message: '修改成功',
            type: 'success',
            duration: 1000
          });
        });
      },
      updateNewStatus(newStatus, ids) {
        let params = new URLSearchParams();
        params.append('ids', ids);
        params.append('newStatus', newStatus);
        updateNewStatus(params).then(response => {
          this.$message({
            message: '修改成功',
            type: 'success',
            duration: 1000
          });
        });
      },
      updateRecommendStatus(recommendStatus, ids) {
        let params = new URLSearchParams();
        params.append('ids', ids);
        params.append('recommendStatus', recommendStatus);
        updateRecommendStatus(params).then(response => {
          this.$message({
            message: '修改成功',
            type: 'success',
            duration: 1000
          });
        });
      },
      updateDeleteStatus(deleteStatus, ids) {
        let params = new URLSearchParams();
        params.append('ids', ids);
        params.append('deleteStatus', deleteStatus);
        updateDeleteStatus(params).then(response => {
          this.$message({
            message: '删除成功',
            type: 'success',
            duration: 1000
          });
        });
        this.getList();
      },
      updateDiyStatus(diyEnabled, ids) {
        let params = new URLSearchParams();
        // 处理多个ID的情况
        if (Array.isArray(ids)) {
          ids.forEach(id => {
            params.append('ids', id);
          });
        } else {
          params.append('ids', ids);
        }
        params.append('diyEnabled', diyEnabled);
        updateProductDiyStatus(params).then(response => {
          this.$message({
            message: 'DIY状态修改成功',
            type: 'success',
            duration: 1000
          });
        });
      },
      handleBatchUpdateSchools() {
        this.schoolSelectDialog.visible = true;
        this.schoolSelectDialog.selectedSchools = [];
      },
      handleSchoolSelectConfirm() {
        if (this.schoolSelectDialog.selectedSchools.length === 0) {
          this.$message({
            message: '请选择至少一个学校',
            type: 'warning',
            duration: 1000
          });
          return;
        }

        let productIds = [];
        for (let i = 0; i < this.multipleSelection.length; i++) {
          productIds.push(this.multipleSelection[i].id);
        }

        let params = new URLSearchParams();
        productIds.forEach(id => {
          params.append('productIds', id);
        });
        this.schoolSelectDialog.selectedSchools.forEach(schoolId => {
          params.append('schoolIds', schoolId);
        });

        updateProductSchools(params).then(response => {
          this.$message({
            message: '批量修改学校关联成功',
            type: 'success',
            duration: 1000
          });
          this.schoolSelectDialog.visible = false;
          this.getList();
        });
      },
      handleBatchUpdateCategory() {
        this.categorySelectDialog.visible = true;
        this.categorySelectDialog.selectedCategory = null;
      },
      handleCategorySelectConfirm() {
        if (!this.categorySelectDialog.selectedCategory) {
          this.$message({
            message: '请选择分类',
            type: 'warning',
            duration: 1000
          });
          return;
        }

        let productIds = [];
        for (let i = 0; i < this.multipleSelection.length; i++) {
          productIds.push(this.multipleSelection[i].id);
        }

        let params = new URLSearchParams();
        productIds.forEach(id => {
          params.append('ids', id);
        });
        params.append('productCategoryId', this.categorySelectDialog.selectedCategory);

        updateProductCategory(params).then(response => {
          this.$message({
            message: '批量转移分类成功',
            type: 'success',
            duration: 1000
          });
          this.categorySelectDialog.visible = false;
          this.getList();
        });
      },
      handleExportProducts() {
        this.$confirm('是否要导出当前搜索条件下的商品数据?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'info'
        }).then(() => {
          exportProducts(this.listQuery).then(response => {
            this.downloadFile(response);
            this.$message({
              message: '导出成功',
              type: 'success',
              duration: 1000
            });
          }).catch(() => {
            this.$message({
              message: '导出失败',
              type: 'error',
              duration: 1000
            });
          });
        });
      },
      downloadFile(response) {
        // 处理CSV文件下载
        const disposition = response.headers['content-disposition'];
        let fileName = 'product_export_' + Date.now() + '.csv';
        if (disposition && disposition.includes('filename=')) {
          fileName = decodeURIComponent(disposition.split('filename=')[1]);
        }
        const blob = new Blob([response.data], { type: 'text/csv;charset=utf-8' });
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = fileName;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
      },
      handleShare(index, row) {
        this.shareDialogVisible = true;
        this.shareLoading = true;
        this.shareInfo = null;

        generateShareInfo(row.id).then(response => {
          this.shareLoading = false;
          if (response.code === 200) {
            this.shareInfo = response.data;
            // 调试信息：检查base64数据是否正确接收
            if (this.shareInfo.miniProgramCodeBase64) {
              console.log('小程序码base64数据长度:', this.shareInfo.miniProgramCodeBase64.length);
              console.log('小程序码base64数据前缀:', this.shareInfo.miniProgramCodeBase64.substring(0, 50));
            } else {
              console.log('未接收到小程序码base64数据');
            }
          } else {
            this.$message.error(response.message || '生成分享信息失败');
            this.shareDialogVisible = false;
          }
        }).catch(error => {
          this.shareLoading = false;
          this.$message.error('生成分享信息失败: ' + (error.message || error));
          this.shareDialogVisible = false;
          console.error('分享信息生成错误:', error);
        });
      },
      handleShareDialogClose() {
        this.shareInfo = null;
        this.shareLoading = false;
      },
      copyToClipboard(text) {
        // 创建临时文本域
        const textArea = document.createElement('textarea');
        textArea.value = text;
        document.body.appendChild(textArea);
        textArea.select();
        try {
          document.execCommand('copy');
          this.$message.success('复制成功');
        } catch (err) {
          this.$message.error('复制失败');
        }

        document.body.removeChild(textArea);
      },
      downloadQRCode() {
        if (this.shareInfo && this.shareInfo.miniProgramCodeBase64) {
          try {
            // 将base64转换为blob
            const base64Data = this.shareInfo.miniProgramCodeBase64.replace(/^data:image\/png;base64,/, '');
            const byteCharacters = atob(base64Data);
            const byteNumbers = new Array(byteCharacters.length);
            for (let i = 0; i < byteCharacters.length; i++) {
              byteNumbers[i] = byteCharacters.charCodeAt(i);
            }
            const byteArray = new Uint8Array(byteNumbers);
            const blob = new Blob([byteArray], { type: 'image/png' });

            // 创建下载链接
            const link = document.createElement('a');
            link.href = URL.createObjectURL(blob);
            link.download = `product-${this.shareInfo.productId}-qrcode.png`;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            URL.revokeObjectURL(link.href);

            this.$message.success('下载成功');
          } catch (error) {
            this.$message.error('下载失败：' + error.message);
          }
        }
      },
      copyQRCodeBase64() {
        if (this.shareInfo && this.shareInfo.miniProgramCodeBase64) {
          this.copyToClipboard(this.shareInfo.miniProgramCodeBase64);
        }
      }
    }
  }
</script>
<style scoped>
  .share-item {
    margin-bottom: 20px;
  }

  .share-item h5 {
    margin-bottom: 10px;
    color: #333;
    font-weight: bold;
  }

  .qrcode-container {
    text-align: center;
  }

  .qrcode-placeholder {
    width: 150px;
    height: 150px;
    border: 2px dashed #ddd;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    margin: 0 auto;
    color: #999;
  }

  .qrcode-placeholder i {
    font-size: 48px;
    margin-bottom: 10px;
  }

  .qrcode-placeholder p {
    margin: 0;
    font-size: 12px;
  }
</style>


