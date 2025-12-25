<template>
  <div class="product-sku-selector">
    <!-- 商品选择对话框 -->
    <el-dialog
      title="选择商品和SKU"
      :visible.sync="visible"
      width="1200px"
      append-to-body
      @close="handleClose">
      
      <!-- 搜索和筛选区域 -->
      <div class="search-section">
        <el-form :inline="true" label-width="100px">
          <el-form-item label="商品搜索：">
            <el-input 
              v-model="searchQuery.keyword" 
              placeholder="商品名称或货号"
              style="width: 250px"
              @keyup.enter.native="handleSearch">
            </el-input>
          </el-form-item>
          <el-form-item label="商品分类：">
            <el-select 
              v-model="searchQuery.categoryId" 
              placeholder="请选择分类"
              style="width: 200px"
              clearable
              @change="handleSearch">
              <el-option
                v-for="cate in categoryOptions"
                :key="cate.id"
                :label="cate.name"
                :value="cate.id">
              </el-option>
            </el-select>
          </el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form>
      </div>

      <!-- 商品列表和SKU选择区域 -->
      <div class="product-list-section">
        <el-table
          ref="productTable"
          :data="productList"
          style="width: 100%"
          v-loading="productListLoading"
          stripe>
          <el-table-column label="商品ID" width="70" align="center">
            <template slot-scope="scope">{{ scope.row.id }}</template>
          </el-table-column>
          <el-table-column label="商品图片" width="80" align="center">
            <template slot-scope="scope">
              <img v-if="scope.row.pic" :src="scope.row.pic" class="product-pic" />
            </template>
          </el-table-column>
          <el-table-column label="商品名称" min-width="180" align="left">
            <template slot-scope="scope">
              <div class="product-name">{{ scope.row.name }}</div>
              <div class="product-sn">货号: {{ scope.row.productSn }}</div>
            </template>
          </el-table-column>
          <el-table-column label="SKU" width="250" align="center">
            <template slot-scope="scope">
              <el-select 
                v-model="selectedSkuMap[scope.row.id]"
                placeholder="选择SKU"
                @change="handleSkuChange(scope.row, $event)">
                <el-option
                  v-for="sku in scope.row.skuStocks"
                  :key="sku.id"
                  :label="getSkuLabel(sku)"
                  :value="sku.id">
                </el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="库存" width="150" align="center">
            <template slot-scope="scope">
              <div>总库存: {{ getSkuStock(scope.row) }}</div>
              <div v-if="storeId" style="color: #ff7043; font-weight: bold;">
                门店库存: {{ getStoreSkuStock(scope.row) }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100" align="center">
            <template slot-scope="scope">
              <el-button 
                size="mini" 
                type="primary"
                :disabled="!selectedSkuMap[scope.row.id]"
                @click="handleAddProduct(scope.row)">
                添加到列表
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            background
            @size-change="handlePageSizeChange"
            @current-change="handlePageChange"
            layout="total, sizes, prev, pager, next, jumper"
            :page-size="searchQuery.pageSize"
            :page-sizes="[10, 20, 50]"
            :current-page.sync="searchQuery.pageNum"
            :total="productTotal">
          </el-pagination>
        </div>
      </div>

      <!-- 已选择的商品列表 -->
      <div class="selected-items-section">
        <h4>已选择商品</h4>
        <el-table
          :data="selectedItems"
          style="width: 100%">
          <el-table-column label="商品名称" width="200" align="left">
            <template slot-scope="scope">
              <div>{{ scope.row.productName }}</div>
              <div style="font-size: 12px; color: #999;">ID: {{ scope.row.productId }}</div>
            </template>
          </el-table-column>
          <el-table-column label="SKU编码/规格" width="200" align="center">
            <template slot-scope="scope">
              <div>{{ scope.row.skuCode }}</div>
              <div v-if="scope.row.specs" style="font-size: 12px; color: #666; margin-top: 4px;">
                {{ scope.row.specs }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="库存" width="150" align="center">
            <template slot-scope="scope">
              <div>总库存: {{ scope.row.availableStock }}</div>
              <div v-if="storeId" style="color: #ff7043; font-weight: bold;">
                门店库存: {{ scope.row.storeStock !== undefined ? scope.row.storeStock : 0 }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="选购数量" width="140" align="center">
            <template slot-scope="scope">
              <el-input-number 
                v-model="scope.row.quantity" 
                :min="1"
                :max="scope.row.availableStock"
                style="width: 100%">
              </el-input-number>
            </template>
          </el-table-column>
          <el-table-column label="销售价格" width="120" align="center">
            <template slot-scope="scope">
              <el-input 
                v-model.number="scope.row.salePrice"
                type="number"
                step="0.01">
              </el-input>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" align="center">
            <template slot-scope="scope">
              <el-button 
                size="mini" 
                type="danger" 
                @click="handleRemoveItem(scope.$index)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <div v-if="selectedItems.length === 0" class="empty-tip">
          还未选择任何商品
        </div>
      </div>

      <!-- 对话框底部按钮 -->
      <span slot="footer" class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleConfirm" :disabled="selectedItems.length === 0">
          确认选择 ({{ selectedItems.length }})
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { fetchList as fetchProductList } from '@/api/product'
import { fetchList as fetchCategoryList } from '@/api/productCate'
import { fetchList as fetchSkuList } from '@/api/skuStock'

// 导入获取 SKU 库存及门店库存的 API
import { getSkuStockWithStore } from '@/api/nonSystemSale'

export default {
  name: 'ProductSkuSelector',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    storeId: {
      type: Number,
      default: null
    },
    schoolId: {
      type: Number,
      default: null
    }
  },
  data() {
    return {
      // 搜索条件
      searchQuery: {
        keyword: '',
        categoryId: null,
        pageNum: 1,
        pageSize: 10
      },
      // 商品列表
      productList: [],
      productTotal: 0,
      productListLoading: false,
      // 分类选项
      categoryOptions: [],
      // 已选择的SKU映射
      selectedSkuMap: {},
      // 已选择的商品列表
      selectedItems: []
    }
  },
  created() {
    this.loadCategories()
  },
  watch: {
    visible(newVal) {
      if (newVal) {
        this.handleSearch()
      }
    }
  },
  methods: {
    // 加载分类
    loadCategories() {
      fetchCategoryList(0).then(response => {
        if (response.data && response.data.list) {
          this.categoryOptions = response.data.list || []
        } else if (Array.isArray(response.data)) {
          this.categoryOptions = response.data || []
        }
      }).catch(error => {
        console.error('获取分类失败:', error)
      })
    },
    // 搜索商品
    handleSearch() {
      this.searchQuery.pageNum = 1
      this.loadProducts()
    },
    // 重置搜索
    handleReset() {
      this.searchQuery = {
        keyword: '',
        categoryId: null,
        pageNum: 1,
        pageSize: 10
      }
      this.selectedSkuMap = {}
      this.loadProducts()
    },
    // 加载商品列表
    loadProducts() {
      this.productListLoading = true
      const params = {
        keyword: this.searchQuery.keyword,
        productCategoryId: this.searchQuery.categoryId,
        pageNum: this.searchQuery.pageNum,
        pageSize: this.searchQuery.pageSize
      }
      
      // 如果指定了学校ID，添加到参数中
      if (this.schoolId) {
        params.schoolId = this.schoolId
      }
      
      // 使用通用的商品列表API，支持学校ID参数
      fetchProductList(params).then(response => {
        this.productList = response.data.list || []
        this.productTotal = response.data.total || 0
        // 为每个商品加载SKU列表（等待完成）
        return this.loadSkusForProducts()
      }).then(() => {
        this.productListLoading = false
      }).catch(error => {
        this.productListLoading = false
        this.$message.error('获取商品列表失败: ' + (error.message || '未知错误'))
      })
    },
    // 为商品列表加载SKU数据
    async loadSkusForProducts() {
      const promises = this.productList.map((product, index) => {
        if (!product.skuStocks) {
          // 如果需要门店库存，使用新的 API
          const apiCall = this.storeId ? 
            getSkuStockWithStore(product.id) :
            fetchSkuList(product.id)
          
          return apiCall.then(response => {
            let skuList = []
            
            console.log('SKU API返回数据:', response)
            
            // 处理新 API 返回的数据格式
            if (this.storeId && response.data && Array.isArray(response.data)) {
              skuList = response.data
            } else if (!this.storeId) {
              // 处理旧 API 返回的数据格式
              skuList = response.data || []
              if (!Array.isArray(skuList)) {
                if (skuList.list && Array.isArray(skuList.list)) {
                  skuList = skuList.list
                } else {
                  skuList = []
                }
              }
            }
            
            console.log('处理后的SKU列表:', skuList)
            
            // 添加库存信息到SKU
            const skuStocks = skuList.map(sku => {
              const specs = this.parseSpecs(sku.spData)
              console.log(`SKU ${sku.skuCode} 的规格:`, specs)
              return {
                id: sku.id,
                skuCode: sku.skuCode,
                stock: sku.stock,
                price: sku.price,
                spData: sku.spData,
                specs: specs,
                storeStocks: sku.storeStocks || []
              }
            })
            
            console.log('最终SKU列表:', skuStocks)
            
            // 使用 Vue.set 确保响应式更新
            this.$set(this.productList[index], 'skuStocks', skuStocks)
          }).catch(error => {
            console.error(`获取产品 ${product.id} 的SKU失败:`, error)
            this.$set(this.productList[index], 'skuStocks', [])
          })
        }
        return Promise.resolve()
      })
      // 等待所有SKU加载完成
      await Promise.all(promises)
    },
    // 获取SKU库存
    getSkuStock(product) {
      const skuId = this.selectedSkuMap[product.id]
      if (!skuId) return '-'
      const sku = product.skuStocks.find(s => s.id === skuId)
      return sku ? sku.stock : '-'
    },
    
    // 获取门店SKU库存
    getStoreSkuStock(product) {
      if (!this.storeId) return '-'
      const skuId = this.selectedSkuMap[product.id]
      if (!skuId) return '-'
      const sku = product.skuStocks.find(s => s.id === skuId)
      if (!sku || !sku.storeStocks) return '-'
      const storeStock = sku.storeStocks.find(s => s.storeId === this.storeId)
      return storeStock ? storeStock.stock : 0
    },
    // 解析规格数据
    parseSpecs(spData) {
      if (!spData) {
        console.log('spData为空')
        return ''
      }
      try {
        const data = JSON.parse(spData)
        console.log('解析后的规格数据:', data)
        
        // 处理数组格式的规格数据
        if (Array.isArray(data) && data.length > 0) {
          const specs = data.map(item => {
            if (item.value) {
              return item.value
            } else if (item.key && item.value) {
              return `${item.key}:${item.value}`
            }
            return ''
          }).filter(s => s).join(' / ')
          console.log('生成的规格字符串:', specs)
          return specs
        }
        
        // 处理对象格式的规格数据
        if (data && typeof data === 'object' && !Array.isArray(data)) {
          if (Object.keys(data).length > 0) {
            const specs = Object.values(data).join(' / ')
            console.log('生成的规格字符串:', specs)
            return specs
          }
        }
      } catch (e) {
        console.error('解析SKU规格数据失败:', e, 'spData:', spData)
      }
      return ''
    },
    // 生成SKU标签，包含规格信息
    getSkuLabel(sku) {
      let label = sku.skuCode || ''
      
      // 调试：打印SKU信息
      console.log('SKU信息:', {
        skuCode: sku.skuCode,
        spData: sku.spData,
        specs: sku.specs,
        stock: sku.stock
      })
      
      if (sku.specs && sku.specs.trim()) {
        label += ` - ${sku.specs}`
      }
      label += ` (库存:${sku.stock || 0})`
      return label
    },
    // SKU变化时触发
    handleSkuChange(product, skuId) {
      // 只是更新映射，不做其他处理
    },
    // 添加商品到选择列表
    handleAddProduct(product) {
      const skuId = this.selectedSkuMap[product.id]
      if (!skuId) {
        this.$message.warning('请先选择SKU')
        return
      }
      
      // 查找对应的SKU
      const sku = product.skuStocks.find(s => s.id === skuId)
      if (!sku) {
        this.$message.error('SKU信息获取失败')
        return
      }
      
      // 检查是否已添加
      const existItem = this.selectedItems.find(item => item.skuId === skuId)
      if (existItem) {
        this.$message.warning('该商品SKU已添加，请勿重复添加')
        return
      }
      
      // 获取门店库存
      let storeStock = 0
      if (this.storeId && sku.storeStocks && sku.storeStocks.length > 0) {
        const storeStockItem = sku.storeStocks.find(s => s.storeId === this.storeId)
        storeStock = storeStockItem ? storeStockItem.stock : 0
      }
      
      // 添加到已选择列表
      this.selectedItems.push({
        productId: product.id,
        productName: product.name,
        productPic: product.pic || '',
        skuId: skuId,
        skuCode: sku.skuCode,
        specs: sku.specs,
        availableStock: sku.stock,
        storeStock: storeStock,
        quantity: 1,
        systemPrice: sku.price || product.price || 0,
        salePrice: sku.price || product.price || 0
      })
      
      this.$message.success('添加成功')
      
      // 清除该产品的SKU选择
      this.$delete(this.selectedSkuMap, product.id)
    },
    // 删除已选择的商品
    handleRemoveItem(index) {
      this.selectedItems.splice(index, 1)
    },
    // 分页大小变化
    handlePageSizeChange(size) {
      this.searchQuery.pageSize = size
      this.searchQuery.pageNum = 1
      this.loadProducts()
    },
    // 页码变化
    handlePageChange(page) {
      this.searchQuery.pageNum = page
      this.loadProducts()
    },
    // 关闭对话框
    handleClose() {
      this.$emit('update:visible', false)
    },
    // 确认选择
    handleConfirm() {
      if (this.selectedItems.length === 0) {
        this.$message.warning('请至少选择一个商品')
        return
      }
      
      // 验证所有商品信息完整
      for (let item of this.selectedItems) {
        if (!item.productId || !item.skuId || !item.quantity || !item.salePrice) {
          this.$message.warning('请完整填写所有商品信息')
          return
        }
      }
      
      // 发送选择完成事件，携带已选择的商品列表
      this.$emit('confirm', this.selectedItems)
      this.handleClose()
    }
  }
}
</script>

<style scoped>
.product-sku-selector {
  width: 100%;
}

.product-sku-selector ::v-deep .el-dialog {
  z-index: 2000 !important;
}

.product-sku-selector ::v-deep .v-modal {
  z-index: 1999 !important;
}

.search-section {
  padding: 15px 0;
  border-bottom: 1px solid #eee;
  margin-bottom: 15px;
}

.product-list-section {
  margin-bottom: 20px;
}

.product-pic {
  max-width: 60px;
  max-height: 60px;
  object-fit: cover;
  border-radius: 4px;
}

.product-name {
  font-weight: 500;
}

.product-sn {
  font-size: 12px;
  color: #999;
}

.selected-items-section {
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.selected-items-section h4 {
  margin: 0 0 15px 0;
  color: #333;
}

.empty-tip {
  text-align: center;
  color: #999;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.pagination-container {
  margin-top: 15px;
  text-align: right;
}

.dialog-footer {
  text-align: right;
}

.dialog-footer .el-button {
  margin-left: 10px;
}
</style>
