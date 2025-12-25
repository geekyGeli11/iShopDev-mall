<template>
  <div class="product-selector">
    <!-- 已选商品列表 -->
    <div class="selected-products" v-if="selectedProducts.length > 0">
      <el-table :data="selectedProducts" border size="small">
        <el-table-column label="商品图片" width="80" align="center">
          <template slot-scope="scope">
            <img :src="scope.row.productPic" style="height: 50px">
          </template>
        </el-table-column>
        <el-table-column label="商品名称" prop="productName"></el-table-column>
        <el-table-column label="单价" width="100" align="center">
          <template slot-scope="scope">¥{{ scope.row.price }}</template>
        </el-table-column>
        <el-table-column label="数量" width="150" align="center">
          <template slot-scope="scope">
            <el-input-number
              v-model="scope.row.quantity"
              :min="1"
              :max="99"
              size="mini"
              @change="handleQuantityChange">
            </el-input-number>
          </template>
        </el-table-column>
        <el-table-column label="小计" width="100" align="center">
          <template slot-scope="scope">
            <span style="color: #f56c6c">¥{{ (scope.row.price * scope.row.quantity).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" align="center">
          <template slot-scope="scope">
            <el-button type="text" size="mini" @click="removeProduct(scope.$index)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div v-else class="empty-tip">
      <span>请添加组合商品（至少2个）</span>
    </div>
    
    <!-- 添加商品按钮 -->
    <el-button type="primary" size="small" @click="showDialog" style="margin-top: 10px">
      <i class="el-icon-plus"></i> 添加商品
    </el-button>

    <!-- 商品选择对话框 -->
    <el-dialog title="选择商品" :visible.sync="dialogVisible" width="1000px" append-to-body>
      <div class="dialog-content">
        <!-- 搜索区域 -->
        <el-form :inline="true" size="small" class="search-form">
          <el-form-item label="商品名称">
            <el-input 
              v-model="searchParams.keyword" 
              placeholder="请输入商品名称" 
              clearable
              style="width: 180px"
              @keyup.enter.native="searchProducts">
            </el-input>
          </el-form-item>
          <el-form-item label="商品分类">
            <el-cascader
              v-model="searchParams.categoryIds"
              :options="categoryOptions"
              :props="{ value: 'id', label: 'name', children: 'children', checkStrictly: true, emitPath: false }"
              placeholder="请选择分类"
              clearable
              filterable
              style="width: 180px">
            </el-cascader>
          </el-form-item>
          <el-form-item label="商品品牌">
            <el-select 
              v-model="searchParams.brandId" 
              placeholder="请选择品牌" 
              clearable
              filterable
              style="width: 150px">
              <el-option
                v-for="item in brandOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="所属学校">
            <el-select 
              v-model="searchParams.schoolId" 
              placeholder="请选择学校" 
              clearable
              filterable
              style="width: 150px">
              <el-option
                v-for="item in schoolOptions"
                :key="item.id"
                :label="item.schoolName"
                :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="searchProducts">搜索</el-button>
            <el-button icon="el-icon-refresh" @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
        
        <!-- 商品列表 -->
        <el-table
          ref="productTable"
          :data="productList"
          v-loading="loading"
          @selection-change="handleSelectionChange"
          border
          size="small"
          max-height="400"
          row-key="id">
          <el-table-column type="selection" width="55" align="center" :reserve-selection="true"></el-table-column>
          <el-table-column label="商品图片" width="80" align="center">
            <template slot-scope="scope">
              <el-image 
                :src="scope.row.pic" 
                style="height: 50px; width: 50px"
                fit="cover"
                :preview-src-list="[scope.row.pic]">
                <div slot="error" class="image-slot">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
            </template>
          </el-table-column>
          <el-table-column label="商品名称" prop="name" min-width="200" show-overflow-tooltip></el-table-column>
          <el-table-column label="分类" width="120" align="center">
            <template slot-scope="scope">
              <span>{{ scope.row.productCategoryName || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="品牌" width="100" align="center">
            <template slot-scope="scope">
              <span>{{ scope.row.brandName || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="价格" width="100" align="center">
            <template slot-scope="scope">
              <span style="color: #f56c6c">¥{{ scope.row.price }}</span>
            </template>
          </el-table-column>
          <el-table-column label="库存" width="80" align="center" prop="stock"></el-table-column>
        </el-table>
        
        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            background
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
            :current-page="pageNum"
            :page-sizes="[10, 20, 50]"
            :page-size="pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total">
          </el-pagination>
        </div>
      </div>
      <span slot="footer">
        <span class="selection-info" v-if="tempSelection.length > 0">
          已选择 <span class="count">{{ tempSelection.length }}</span> 件商品
        </span>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmSelection" :disabled="tempSelection.length === 0">确定添加</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { fetchList as fetchProductList } from '@/api/product'
import { fetchListWithChildren as fetchCategoryList } from '@/api/productCate'
import { fetchList as fetchBrandList } from '@/api/brand'
import { fetchEnabledSchools } from '@/api/school'

export default {
  name: 'ProductSelector',
  props: {
    value: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      // 搜索参数
      searchParams: {
        keyword: '',
        categoryIds: null,
        brandId: null,
        schoolId: null
      },
      // 下拉选项
      categoryOptions: [],
      brandOptions: [],
      schoolOptions: [],
      // 商品列表
      productList: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      tempSelection: [],
      selectedProducts: []
    }
  },
  watch: {
    value: {
      handler(val) {
        this.selectedProducts = val || []
      },
      immediate: true
    }
  },
  created() {
    // 预加载下拉选项数据
    this.loadOptions()
  },
  methods: {
    // 加载下拉选项数据
    async loadOptions() {
      try {
        // 并行加载分类、品牌、学校数据
        const [categoryRes, brandRes, schoolRes] = await Promise.all([
          fetchCategoryList(),
          fetchBrandList({ pageNum: 1, pageSize: 100 }),
          fetchEnabledSchools()
        ])
        
        // 处理分类数据
        if (categoryRes.data) {
          this.categoryOptions = categoryRes.data
        }
        
        // 处理品牌数据
        if (brandRes.data && brandRes.data.list) {
          this.brandOptions = brandRes.data.list
        }
        
        // 处理学校数据
        if (schoolRes.data) {
          this.schoolOptions = Array.isArray(schoolRes.data) ? schoolRes.data : []
        }
      } catch (error) {
        console.error('加载选项数据失败:', error)
      }
    },
    showDialog() {
      this.dialogVisible = true
      this.resetSearch()
      this.tempSelection = []
      // 清除表格选择状态
      this.$nextTick(() => {
        if (this.$refs.productTable) {
          this.$refs.productTable.clearSelection()
        }
      })
      this.getProductList()
    },
    getProductList() {
      this.loading = true
      const params = {
        publishStatus: 1, // 只查询已上架商品
        pageNum: this.pageNum,
        pageSize: this.pageSize
      }
      
      // 添加搜索条件
      if (this.searchParams.keyword) {
        params.keyword = this.searchParams.keyword
      }
      if (this.searchParams.categoryIds) {
        params.productCategoryId = this.searchParams.categoryIds
      }
      if (this.searchParams.brandId) {
        params.brandId = this.searchParams.brandId
      }
      if (this.searchParams.schoolId) {
        params.schoolId = this.searchParams.schoolId
      }
      
      fetchProductList(params).then(response => {
        this.loading = false
        this.productList = response.data.list || []
        this.total = response.data.total || 0
      }).catch(() => {
        this.loading = false
        this.$message.error('获取商品列表失败')
      })
    },
    searchProducts() {
      this.pageNum = 1
      this.getProductList()
    },
    resetSearch() {
      this.searchParams = {
        keyword: '',
        categoryIds: null,
        brandId: null,
        schoolId: null
      }
      this.pageNum = 1
    },
    handlePageChange(page) {
      this.pageNum = page
      this.getProductList()
    },
    handleSizeChange(size) {
      this.pageSize = size
      this.pageNum = 1
      this.getProductList()
    },
    handleSelectionChange(selection) {
      this.tempSelection = selection
    },
    confirmSelection() {
      if (this.tempSelection.length === 0) {
        this.$message.warning('请选择商品')
        return
      }
      // 过滤已存在的商品
      const existingIds = this.selectedProducts.map(p => p.productId)
      const newProducts = this.tempSelection
        .filter(p => !existingIds.includes(p.id))
        .map(p => ({
          productId: p.id,
          productName: p.name,
          productPic: p.pic,
          price: p.price,
          quantity: 1,
          sort: 0
        }))
      
      if (newProducts.length === 0) {
        this.$message.warning('所选商品已全部存在于列表中')
        return
      }
      
      const addedCount = newProducts.length
      const skippedCount = this.tempSelection.length - addedCount
      
      this.selectedProducts = [...this.selectedProducts, ...newProducts]
      this.emitChange()
      this.dialogVisible = false
      
      // 提示添加结果
      if (skippedCount > 0) {
        this.$message.success(`成功添加 ${addedCount} 件商品，${skippedCount} 件已存在被跳过`)
      } else {
        this.$message.success(`成功添加 ${addedCount} 件商品`)
      }
    },
    removeProduct(index) {
      this.$confirm('确定要移除该商品吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.selectedProducts.splice(index, 1)
        this.emitChange()
        this.$message.success('已移除')
      }).catch(() => {})
    },
    handleQuantityChange() {
      this.emitChange()
    },
    emitChange() {
      this.$emit('input', this.selectedProducts)
      this.$emit('change', this.selectedProducts)
    }
  }
}
</script>

<style scoped>
.product-selector {
  width: 100%;
}
.selected-products {
  margin-bottom: 10px;
}
.empty-tip {
  padding: 20px;
  text-align: center;
  color: #909399;
  background: #f5f7fa;
  border: 1px dashed #dcdfe6;
  border-radius: 4px;
}
.dialog-content {
  max-height: 550px;
}
.search-form {
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}
.search-form .el-form-item {
  margin-bottom: 10px;
}
.pagination-container {
  margin-top: 15px;
  text-align: right;
}
.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 20px;
}
.selection-info {
  float: left;
  line-height: 36px;
  color: #606266;
}
.selection-info .count {
  color: #409eff;
  font-weight: bold;
  margin: 0 4px;
}
</style>
