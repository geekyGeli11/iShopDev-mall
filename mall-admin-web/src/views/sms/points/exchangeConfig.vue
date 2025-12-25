<template>
  <div class="app-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>积分换购配置管理</h2>
      <p class="page-description">管理积分换购商品和优惠券兑换配置</p>
    </div>

    <!-- 选项卡 -->
    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <el-tab-pane label="商品换购配置" name="product">
        <!-- 商品换购配置内容 -->
        <div class="product-config-container">
          <!-- 搜索栏 -->
          <div class="filter-container">
            <el-input
              v-model="productQuery.keyword"
              placeholder="请输入商品名称"
              style="width: 200px;"
              clearable
              @keyup.enter.native="getProductList"
            />
            <el-select v-model="productQuery.status" placeholder="状态" clearable style="width: 120px;">
              <el-option label="全部" value="" />
              <el-option label="上架" :value="1" />
              <el-option label="下架" :value="0" />
            </el-select>
            <el-button type="primary" icon="el-icon-search" @click="getProductList">搜索</el-button>
            <el-button type="primary" icon="el-icon-plus" @click="handleCreateProduct">添加商品换购</el-button>
            <el-button type="danger" icon="el-icon-delete" :disabled="!productMultipleSelection.length" @click="handleProductBatchDelete">批量删除</el-button>
          </div>

          <!-- 商品配置列表 -->
          <el-table
            v-loading="productListLoading"
            :data="productList"
            border
            :fit="false"
            highlight-current-row
            @selection-change="handleProductSelectionChange"
          >
            <el-table-column type="selection" width="55" />
            
            <el-table-column label="商品信息" width="280">
              <template slot-scope="{row}">
                <div class="product-info">
                  <el-image 
                    :src="row.productPic" 
                    style="width: 60px; height: 60px; border-radius: 4px;"
                    fit="cover"
                  />
                  <div class="product-detail">
                    <div class="product-name">{{ row.productName }}</div>
                    <div class="product-id">商品ID: {{ row.productId }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>
            
            <el-table-column label="价格设置" width="180">
              <template slot-scope="{row}">
                <div class="price-info">
                  <div class="original-price">原价：¥{{ row.originalPrice }}</div>
                  <div class="exchange-price">
                    换购：<span class="highlight">¥{{ row.cashPrice }} + {{ row.pointsPrice }}积分</span>
                  </div>
                </div>
              </template>
            </el-table-column>
            
            <el-table-column label="库存设置" width="120">
              <template slot-scope="{row}">
                <div class="stock-info">
                  <div>总库存：{{ row.totalStock }}</div>
                  <div>剩余：<span :class="{'low-stock': row.remainingStock <= 10}">{{ row.remainingStock }}</span></div>
                  <div>限购：{{ row.perPersonLimit }}件</div>
                </div>
              </template>
            </el-table-column>
            
            <el-table-column label="活动时间" min-width="200">
              <template slot-scope="{row}">
                <div class="time-info">
                  <div>开始：{{ row.startTime | parseTime('{y}-{m}-{d}') }}</div>
                  <div>结束：{{ row.endTime | parseTime('{y}-{m}-{d}') }}</div>
                </div>
              </template>
            </el-table-column>
            
            <el-table-column label="状态" width="100">
              <template slot-scope="{row}">
                <el-tag :type="row.status === 1 ? 'success' : 'info'">
                  {{ row.status === 1 ? '上架' : '下架' }}
                </el-tag>
              </template>
            </el-table-column>
            
            <el-table-column label="排序" width="100">
              <template slot-scope="{row}">
                {{ row.sortOrder }}
              </template>
            </el-table-column>
            
            <el-table-column label="操作" width="280" align="center">
              <template slot-scope="{row, $index}">
                <el-button size="mini" @click="handleUpdateProduct(row)">编辑</el-button>
                <el-button 
                  size="mini" 
                  :type="row.status === 1 ? 'warning' : 'success'"
                  @click="handleProductStatusChange(row, $index)"
                >
                  {{ row.status === 1 ? '下架' : '上架' }}
                </el-button>
                <el-button size="mini" type="danger" @click="handleDeleteProduct(row, $index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 商品配置分页 -->
          <div class="pagination-container">
            <el-pagination
              background
              @size-change="handleProductSizeChange"
              @current-change="handleProductCurrentChange"
              layout="total, sizes,prev, pager, next,jumper"
              :current-page.sync="productQuery.pageNum"
              :page-size="productQuery.pageSize"
              :page-sizes="[5,10,15]"
              :total="productTotal">
            </el-pagination>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="优惠券兑换配置" name="coupon">
        <!-- 优惠券兑换配置内容 -->
        <div class="coupon-config-container">
          <!-- 搜索栏 -->
          <div class="filter-container">
            <el-input
              v-model="couponQuery.keyword"
              placeholder="请输入优惠券名称"
              style="width: 200px;"
              clearable
              @keyup.enter.native="getCouponList"
            />
            <el-select v-model="couponQuery.status" placeholder="状态" clearable style="width: 120px;">
              <el-option label="全部" value="" />
              <el-option label="启用" :value="1" />
              <el-option label="禁用" :value="0" />
            </el-select>
            <el-button type="primary" icon="el-icon-search" @click="getCouponList">搜索</el-button>
            <el-button type="primary" icon="el-icon-plus" @click="handleCreateCoupon">添加优惠券兑换</el-button>
            <el-button type="danger" icon="el-icon-delete" :disabled="!couponMultipleSelection.length" @click="handleCouponBatchDelete">批量删除</el-button>
          </div>

          <!-- 优惠券配置列表 -->
          <el-table
            v-loading="couponListLoading"
            :data="couponList"
            border
             :fit="false"
            highlight-current-row
            @selection-change="handleCouponSelectionChange"
          >
            <el-table-column type="selection" width="55" />
            
            <el-table-column label="优惠券信息" min-width="280">
              <template slot-scope="{row}">
                <div class="coupon-info">
                  <div class="coupon-name">{{ row.couponName }}</div>
                  <div class="coupon-id">优惠券ID: {{ row.couponId }}</div>
                </div>
              </template>
            </el-table-column>
            
            <el-table-column label="兑换积分" min-width="140">
              <template slot-scope="{row}">
                <span class="points-price">{{ row.pointsPrice }}积分</span>
              </template>
            </el-table-column>
            
            <el-table-column label="发放设置" min-width="200">
              <template slot-scope="{row}">
                <div class="count-info">
                  <div>总数量：{{ row.totalCount }}</div>
                  <div>剩余：<span :class="{'low-count': row.remainingCount <= 10}">{{ row.remainingCount }}</span></div>
                  <div>限领：{{ row.perPersonLimit }}张</div>
                </div>
              </template>
            </el-table-column>
            
            <el-table-column label="活动时间" width="160">
              <template slot-scope="{row}">
                <div class="time-info">
                  <div>开始：{{ row.startTime | parseTime('{y}-{m}-{d}') }}</div>
                  <div>结束：{{ row.endTime | parseTime('{y}-{m}-{d}') }}</div>
                </div>
              </template>
            </el-table-column>
            
            <el-table-column label="状态" width="80">
              <template slot-scope="{row}">
                <el-tag :type="row.status === 1 ? 'success' : 'info'">
                  {{ row.status === 1 ? '启用' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            
            <el-table-column label="排序" width="80">
              <template slot-scope="{row}">
                {{ row.sortOrder }}
              </template>
            </el-table-column>
            
            <el-table-column label="操作" width="500" align="center">
              <template slot-scope="{row, $index}">
                <el-button size="mini" @click="handleUpdateCoupon(row)">编辑</el-button>
                <el-button 
                  size="mini" 
                  :type="row.status === 1 ? 'warning' : 'success'"
                  @click="handleCouponStatusChange(row, $index)"
                >
                  {{ row.status === 1 ? '禁用' : '启用' }}
                </el-button>
                <el-button size="mini" type="danger" @click="handleDeleteCoupon(row, $index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 优惠券配置分页 -->
          <div class="pagination-container">
            <el-pagination
              background
              @size-change="handleCouponSizeChange"
              @current-change="handleCouponCurrentChange"
              layout="total, sizes,prev, pager, next,jumper"
              :current-page.sync="couponQuery.pageNum"
              :page-size="couponQuery.pageSize"
              :page-sizes="[5,10,15]"
              :total="couponTotal">
            </el-pagination>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="兑换记录" name="record">
        <!-- 兑换记录内容 -->
        <div class="record-container">
          <!-- 搜索栏 -->
          <div class="filter-container">
            <el-input
              v-model="recordQuery.memberUsername"
              placeholder="用户名"
              style="width: 150px;"
              clearable
            />
            <el-select v-model="recordQuery.exchangeType" placeholder="兑换类型" clearable style="width: 120px;">
              <el-option label="全部" value="" />
              <el-option label="商品换购" :value="1" />
              <el-option label="优惠券兑换" :value="2" />
            </el-select>
            <el-select v-model="recordQuery.exchangeStatus" placeholder="兑换状态" clearable style="width: 120px;">
              <el-option label="全部" value="" />
              <el-option label="成功" :value="1" />
              <el-option label="失败" :value="2" />
              <el-option label="已退回" :value="3" />
            </el-select>
            <el-date-picker
              v-model="recordDateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              style="width: 240px;"
              @change="onRecordDateRangeChange"
            />
            <el-button type="primary" icon="el-icon-search" @click="getRecordList">搜索</el-button>
            <el-button type="success" icon="el-icon-download" @click="handleExportRecord">导出记录</el-button>
          </div>

          <!-- 兑换记录列表 -->
          <el-table
            v-loading="recordListLoading"
            :data="recordList"
            border
            fit
            highlight-current-row
          >
            <el-table-column label="用户信息" width="150">
              <template slot-scope="{row}">
                <div class="user-info">
                  <div class="username">{{ row.memberUsername }}</div>
                  <div class="user-id">ID: {{ row.memberId }}</div>
                </div>
              </template>
            </el-table-column>
            
            <el-table-column label="兑换内容" width="200">
              <template slot-scope="{row}">
                <div class="exchange-info">
                  <div class="target-name">{{ row.targetName }}</div>
                  <div class="exchange-type">
                    <el-tag :type="row.exchangeType === 1 ? 'primary' : 'success'" size="mini">
                      {{ row.exchangeType === 1 ? '商品换购' : '优惠券兑换' }}
                    </el-tag>
                  </div>
                </div>
              </template>
            </el-table-column>
            
            <el-table-column label="消费详情" width="150">
              <template slot-scope="{row}">
                <div class="cost-info">
                  <div class="points-used">积分：{{ row.pointsUsed }}</div>
                  <div v-if="row.cashAmount > 0" class="cash-amount">现金：¥{{ row.cashAmount }}</div>
                  <div class="quantity">数量：{{ row.quantity }}</div>
                </div>
              </template>
            </el-table-column>
            
            <el-table-column label="兑换时间" width="160">
              <template slot-scope="{row}">
                {{ row.exchangeTime | parseTime('{y}-{m}-{d} {h}:{i}:{s}') }}
              </template>
            </el-table-column>
            
            <el-table-column label="状态" width="100">
              <template slot-scope="{row}">
                <el-tag :type="getRecordStatusType(row.exchangeStatus)">
                  {{ getRecordStatusText(row.exchangeStatus) }}
                </el-tag>
              </template>
            </el-table-column>
            
            <el-table-column label="备注" width="120">
              <template slot-scope="{row}">
                <span>{{ row.remark || '-' }}</span>
              </template>
            </el-table-column>
            
            <el-table-column label="操作" width="150" align=center">
              <template slot-scope="{row}">
                <el-button size="mini" @click="handleViewRecord(row)">查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 兑换记录分页 -->
          <div class="pagination-container">
            <el-pagination
              background
              @size-change="handleRecordSizeChange"
              @current-change="handleRecordCurrentChange"
              layout="total, sizes,prev, pager, next,jumper"
              :current-page.sync="recordQuery.pageNum"
              :page-size="recordQuery.pageSize"
              :page-sizes="[5,10,15]"
              :total="recordTotal">
            </el-pagination>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 商品换购配置对话框 -->
    <el-dialog 
      :title="productDialogStatus === 'create' ? '添加商品换购配置' : '编辑商品换购配置'" 
      :visible.sync="productDialogVisible"
      width="800px"
    >
      <el-form 
        ref="productForm" 
        :model="productForm" 
        :rules="productRules" 
        label-width="120px"
      >
        <el-row>
          <el-col :span="12">
            <el-form-item label="商品" prop="productId">
              <el-select
                v-model="productForm.productId"
                placeholder="请选择商品"
                filterable
                remote
                :remote-method="remoteSearchProduct"
                @change="handleProductChange"
              >
                <el-option
                  v-for="item in productOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="原价" prop="originalPrice">
              <el-input v-model="productForm.originalPrice" readonly>
                <template slot="append">元</template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row>
          <el-col :span="12">
            <el-form-item label="换购现金价" prop="cashPrice">
              <el-input v-model="productForm.cashPrice" type="number">
                <template slot="append">元</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="换购积分价" prop="pointsPrice">
              <el-input v-model="productForm.pointsPrice" type="number">
                <template slot="append">积分</template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row>
          <el-col :span="12">
            <el-form-item label="总库存" prop="totalStock">
              <el-input v-model="productForm.totalStock" type="number">
                <template slot="append">件</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="每人限购" prop="perPersonLimit">
              <el-input v-model="productForm.perPersonLimit" type="number">
                <template slot="append">件</template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row>
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker
                v-model="productForm.startTime"
                type="datetime"
                placeholder="选择开始时间"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker
                v-model="productForm.endTime"
                type="datetime"
                placeholder="选择结束时间"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row>
          <el-col :span="12">
            <el-form-item label="排序" prop="sortOrder">
              <el-input v-model="productForm.sortOrder" type="number" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="productForm.status">
                <el-radio :label="1">上架</el-radio>
                <el-radio :label="0">下架</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="换购说明" prop="description">
          <el-input v-model="productForm.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="productDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitProductForm">确定</el-button>
      </div>
    </el-dialog>

    <!-- 优惠券兑换配置对话框 -->
    <el-dialog 
      :title="couponDialogStatus === 'create' ? '添加优惠券兑换配置' : '编辑优惠券兑换配置'" 
      :visible.sync="couponDialogVisible"
      width="800px"
    >
      <el-form 
        ref="couponForm" 
        :model="couponForm" 
        :rules="couponRules" 
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="优惠券" prop="couponId">
              <el-select
                v-model="couponForm.couponId"
                placeholder="请选择优惠券"
                filterable
                @change="handleCouponChange"
              >
                <el-option
                  v-for="item in couponOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="兑换积分" prop="pointsPrice">
              <el-input v-model="couponForm.pointsPrice" type="number">
                <template slot="append">积分</template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="总发放数量" prop="totalCount">
              <el-input v-model="couponForm.totalCount" type="number">
                <template slot="append">张</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="每人限领" prop="perPersonLimit">
              <el-input v-model="couponForm.perPersonLimit" type="number">
                <template slot="append">张</template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker
                v-model="couponForm.startTime"
                type="datetime"
                placeholder="选择开始时间"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker
                v-model="couponForm.endTime"
                type="datetime"
                placeholder="选择结束时间"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="排序" prop="sortOrder">
              <el-input v-model="couponForm.sortOrder" type="number" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="couponForm.status">
                <el-radio :label="1">启用</el-radio>
                <el-radio :label="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="兑换说明" prop="description">
          <el-input v-model="couponForm.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="couponDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCouponForm">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  fetchPointsProductConfigList,
  createPointsProductConfig,
  updatePointsProductConfig,
  deletePointsProductConfig,
  batchDeletePointsProductConfig,
  updatePointsProductStatus,
  fetchPointsCouponConfigList,
  createPointsCouponConfig,
  updatePointsCouponConfig,
  deletePointsCouponConfig,
  batchDeletePointsCouponConfig,
  updatePointsCouponStatus,
  fetchPointsExchangeLogList,
  exportPointsExchangeList
} from '@/api/pointsExchange'
import { fetchList as fetchProductList } from '@/api/product'
import { fetchList as fetchCouponList } from '@/api/coupon'

export default {
  name: 'PointsExchangeConfig',
  components: {},
  data() {
    return {
      // 选项卡
      activeTab: 'product',
      
      // 商品换购配置
      productList: [],
      productTotal: 0,
      productListLoading: false,
      productQuery: {
        pageNum: 1,
        pageSize: 10,
        keyword: '',
        status: ''
      },
      productMultipleSelection: [],
      productDialogVisible: false,
      productDialogStatus: 'create',
      productForm: this.getDefaultProductForm(),
      productOptions: [],
      productRules: {
        productId: [{ required: true, message: '请选择商品', trigger: 'change' }],
        cashPrice: [{ required: true, message: '请输入换购现金价', trigger: 'blur' }],
        pointsPrice: [{ required: true, message: '请输入换购积分价', trigger: 'blur' }],
        totalStock: [{ required: true, message: '请输入总库存', trigger: 'blur' }],
        perPersonLimit: [{ required: true, message: '请输入每人限购数量', trigger: 'blur' }],
        startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
        endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
      },
      
      // 优惠券兑换配置
      couponList: [],
      couponTotal: 0,
      couponListLoading: false,
      couponQuery: {
        pageNum: 1,
        pageSize: 10,
        keyword: '',
        status: ''
      },
      couponMultipleSelection: [],
      couponDialogVisible: false,
      couponDialogStatus: 'create',
      couponForm: this.getDefaultCouponForm(),
      couponOptions: [],
      couponRules: {
        couponId: [{ required: true, message: '请选择优惠券', trigger: 'change' }],
        pointsPrice: [{ required: true, message: '请输入兑换积分', trigger: 'blur' }],
        totalCount: [{ required: true, message: '请输入总发放数量', trigger: 'blur' }],
        perPersonLimit: [{ required: true, message: '请输入每人限领数量', trigger: 'blur' }],
        startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
        endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
      },
      
      // 兑换记录
      recordList: [],
      recordTotal: 0,
      recordListLoading: false,
      recordQuery: {
        pageNum: 1,
        pageSize: 10,
        memberUsername: '',
        exchangeType: '',
        exchangeStatus: '',
        startDate: '',
        endDate: ''
      },
      recordDateRange: []
    }
  },
  created() {
    this.getProductList()
    this.loadProductOptions()
    this.loadCouponOptions()
  },
  methods: {
    // 选项卡切换
    handleTabClick(tab) {
      if (tab.name === 'product') {
        this.getProductList()
      } else if (tab.name === 'coupon') {
        this.getCouponList()
      } else if (tab.name === 'record') {
        this.getRecordList()
      }
    },
    
    // 商品换购配置相关方法
    getDefaultProductForm() {
      return {
        id: null,
        productId: '',
        productName: '',
        productPic: '',
        originalPrice: '',
        cashPrice: '',
        pointsPrice: '',
        totalStock: '',
        perPersonLimit: 1,
        status: 1,
        startTime: '',
        endTime: '',
        sortOrder: 0,
        description: ''
      }
    },
    
    async getProductList() {
      this.productListLoading = true
      try {
        const result = await fetchPointsProductConfigList(this.productQuery)
        this.productList = result.data.list
        this.productTotal = result.data.total
      } catch (error) {
        this.$message.error('获取商品换购配置失败')
      } finally {
        this.productListLoading = false
      }
    },
    
    async loadProductOptions() {
      try {
        const result = await fetchProductList({ pageNum: 1, pageSize: 100 })
        this.productOptions = result.data.list.map(item => ({
          id: item.id,
          name: item.name,
          price: item.price,
          pic: item.pic
        }))
      } catch (error) {
        console.error('加载商品选项失败', error)
      }
    },
    
    remoteSearchProduct(query) {
      if (query !== '') {
        // 远程搜索商品
        this.loadProductOptions()
      }
    },
    
    handleProductChange(productId) {
      const product = this.productOptions.find(item => item.id === productId)
      if (product) {
        this.productForm.productName = product.name
        this.productForm.productPic = product.pic
        this.productForm.originalPrice = product.price
      }
    },
    
    handleCreateProduct() {
      this.productDialogStatus = 'create'
      this.productForm = this.getDefaultProductForm()
      this.productDialogVisible = true
      this.$nextTick(() => {
        this.$refs.productForm.clearValidate()
      })
    },
    
    handleUpdateProduct(row) {
      this.productDialogStatus = 'update'
      this.productForm = Object.assign({}, row)
      this.productDialogVisible = true
      this.$nextTick(() => {
        this.$refs.productForm.clearValidate()
      })
    },
    
    submitProductForm() {
      this.$refs.productForm.validate(async (valid) => {
        if (valid) {
          try {
            if (this.productDialogStatus === 'create') {
              await createPointsProductConfig(this.productForm)
              this.$message.success('添加成功')
            } else {
              await updatePointsProductConfig(this.productForm.id, this.productForm)
              this.$message.success('更新成功')
            }
            this.productDialogVisible = false
            this.getProductList()
          } catch (error) {
            this.$message.error('操作失败')
          }
        }
      })
    },
    
    async handleDeleteProduct(row, index) {
      this.$confirm('确认删除该商品换购配置？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await deletePointsProductConfig(row.id)
          this.$message.success('删除成功')
          this.productList.splice(index, 1)
        } catch (error) {
          this.$message.error('删除失败')
        }
      })
    },
    
    async handleProductBatchDelete() {
      const ids = this.productMultipleSelection.map(item => item.id)
      this.$confirm('确认批量删除选中的商品换购配置？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await batchDeletePointsProductConfig(ids)
          this.$message.success('批量删除成功')
          this.getProductList()
        } catch (error) {
          this.$message.error('批量删除失败')
        }
      })
    },
    
    async handleProductStatusChange(row, index) {
      const status = row.status === 1 ? 0 : 1
      try {
        await updatePointsProductStatus([row.id], status)
        this.$message.success('状态更新成功')
        this.productList[index].status = status
      } catch (error) {
        this.$message.error('状态更新失败')
      }
    },
    
    handleProductSelectionChange(selection) {
      this.productMultipleSelection = selection
    },
    
    // 优惠券兑换配置相关方法
    getDefaultCouponForm() {
      return {
        id: null,
        couponId: '',
        couponName: '',
        pointsPrice: '',
        totalCount: '',
        perPersonLimit: 1,
        status: 1,
        startTime: '',
        endTime: '',
        sortOrder: 0,
        description: ''
      }
    },
    
    async getCouponList() {
      this.couponListLoading = true
      try {
        const result = await fetchPointsCouponConfigList(this.couponQuery)
        this.couponList = result.data.list
        this.couponTotal = result.data.total
      } catch (error) {
        this.$message.error('获取优惠券兑换配置失败')
      } finally {
        this.couponListLoading = false
      }
    },
    
    async loadCouponOptions() {
      try {
        const result = await fetchCouponList({ pageNum: 1, pageSize: 100 })
        this.couponOptions = result.data.list.map(item => ({
          id: item.id,
          name: item.name
        }))
      } catch (error) {
        console.error('加载优惠券选项失败', error)
      }
    },
    
    handleCouponChange(couponId) {
      const coupon = this.couponOptions.find(item => item.id === couponId)
      if (coupon) {
        this.couponForm.couponName = coupon.name
      }
    },
    
    handleCreateCoupon() {
      this.couponDialogStatus = 'create'
      this.couponForm = this.getDefaultCouponForm()
      this.couponDialogVisible = true
      this.$nextTick(() => {
        this.$refs.couponForm.clearValidate()
      })
    },
    
    handleUpdateCoupon(row) {
      this.couponDialogStatus = 'update'
      this.couponForm = Object.assign({}, row)
      this.couponDialogVisible = true
      this.$nextTick(() => {
        this.$refs.couponForm.clearValidate()
      })
    },
    
    submitCouponForm() {
      this.$refs.couponForm.validate(async (valid) => {
        if (valid) {
          try {
            if (this.couponDialogStatus === 'create') {
              await createPointsCouponConfig(this.couponForm)
              this.$message.success('添加成功')
            } else {
              await updatePointsCouponConfig(this.couponForm.id, this.couponForm)
              this.$message.success('更新成功')
            }
            this.couponDialogVisible = false
            this.getCouponList()
          } catch (error) {
            this.$message.error('操作失败')
          }
        }
      })
    },
    
    async handleDeleteCoupon(row, index) {
      this.$confirm('确认删除该优惠券兑换配置？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await deletePointsCouponConfig(row.id)
          this.$message.success('删除成功')
          this.couponList.splice(index, 1)
        } catch (error) {
          this.$message.error('删除失败')
        }
      })
    },
    
    async handleCouponBatchDelete() {
      const ids = this.couponMultipleSelection.map(item => item.id)
      this.$confirm('确认批量删除选中的优惠券兑换配置？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await batchDeletePointsCouponConfig(ids)
          this.$message.success('批量删除成功')
          this.getCouponList()
        } catch (error) {
          this.$message.error('批量删除失败')
        }
      })
    },
    
    async handleCouponStatusChange(row, index) {
      const status = row.status === 1 ? 0 : 1
      try {
        await updatePointsCouponStatus([row.id], status)
        this.$message.success('状态更新成功')
        this.couponList[index].status = status
      } catch (error) {
        this.$message.error('状态更新失败')
      }
    },
    
    handleCouponSelectionChange(selection) {
      this.couponMultipleSelection = selection
    },
    
    // 兑换记录相关方法
    async getRecordList() {
      this.recordListLoading = true
      try {
        const result = await fetchPointsExchangeLogList(this.recordQuery)
        this.recordList = result.data.list
        this.recordTotal = result.data.total
      } catch (error) {
        this.$message.error('获取兑换记录失败')
      } finally {
        this.recordListLoading = false
      }
    },
    
    onRecordDateRangeChange(dateRange) {
      if (dateRange && dateRange.length === 2) {
        this.recordQuery.startDate = dateRange[0]
        this.recordQuery.endDate = dateRange[1]
      } else {
        this.recordQuery.startDate = ''
        this.recordQuery.endDate = ''
      }
    },
    
    async handleExportRecord() {
      try {
        const result = await exportPointsExchangeList(this.recordQuery)
        this.$message.success('导出成功')
        // 处理文件下载
      } catch (error) {
        this.$message.error('导出失败')
      }
    },
    
    handleViewRecord(row) {
      // 查看兑换记录详情
      this.$message.info('查看兑换记录详情功能待实现')
    },
    
    getRecordStatusType(status) {
      const statusMap = {
        1: 'success',
        2: 'danger',
        3: 'warning'
      }
      return statusMap[status] || 'info'
    },
    
    getRecordStatusText(status) {
      const statusMap = {
        1: '成功',
        2: '失败',
        3: '已退回'
      }
      return statusMap[status] || '未知'
    },
    
    // 分页事件处理方法
    handleProductSizeChange(val) {
      this.productQuery.pageSize = val
      this.getProductList()
    },
    
    handleProductCurrentChange(val) {
      this.productQuery.pageNum = val
      this.getProductList()
    },
    
    handleCouponSizeChange(val) {
      this.couponQuery.pageSize = val
      this.getCouponList()
    },
    
    handleCouponCurrentChange(val) {
      this.couponQuery.pageNum = val
      this.getCouponList()
    },
    
    handleRecordSizeChange(val) {
      this.recordQuery.pageSize = val
      this.getRecordList()
    },
    
    handleRecordCurrentChange(val) {
      this.recordQuery.pageNum = val
      this.getRecordList()
    }
  }
}
</script>

<style scoped>
.page-header {
  margin-bottom: 20px;
}

.page-description {
  color: #666;
  margin: 5px 0 0 0;
}

.filter-container {
  margin-bottom: 20px;
}

.filter-container .el-input,
.filter-container .el-select,
.filter-container .el-date-picker {
  margin-right: 10px;
}

.product-info {
  display: flex;
  align-items: center;
}

.product-detail {
  margin-left: 10px;
}

.product-name {
  font-weight: bold;
  margin-bottom: 5px;
}

.product-id {
  font-size: 12px;
  color: #999;
}

.price-info .original-price {
  text-decoration: line-through;
  color: #999;
  font-size: 12px;
}

.price-info .exchange-price {
  color: #f56c6c;
  font-weight: bold;
}

.price-info .highlight {
  color: #e6a23c;
}

.stock-info, .count-info, .time-info {
  font-size: 12px;
  line-height: 1.5;
}

.low-stock, .low-count {
  color: #f56c6c;
  font-weight: bold;
}

.points-price {
  color: #409eff;
  font-weight: bold;
}

.coupon-info .coupon-name {
  font-weight: bold;
  margin-bottom: 5px;
}

.coupon-info .coupon-id {
  font-size: 12px;
  color: #999;
}

.user-info .username {
  font-weight: bold;
}

.user-info .user-id {
  font-size: 12px;
  color: #999;
}

.exchange-info .target-name {
  margin-bottom: 5px;
}

.cost-info {
  font-size: 12px;
  line-height: 1.5;
}

.cost-info .points-used {
  color: #409eff;
}

.cost-info .cash-amount {
  color: #67c23a;
}

.dialog-footer {
  text-align: right;
}

.el-table .el-button + .el-button {
  margin-left: 5px;
}

.el-table .el-table__row .el-button {
  margin-right: 8px;
}
</style>