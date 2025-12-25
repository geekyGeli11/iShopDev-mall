<template>
  <el-dialog
    :title="title"
    :visible.sync="dialogVisible"
    width="80%"
    @close="handleClose">
    <div class="filter-container">
      <el-input
        v-model="listQuery.keyword"
        placeholder="商品名称"
        style="width: 200px;"
        class="filter-item"
        @keyup.enter.native="handleFilter"
        clearable
      />
      <el-input
        v-model="listQuery.productId"
        placeholder="商品ID"
        style="width: 200px;"
        class="filter-item"
        @keyup.enter.native="handleFilter"
        clearable
      />
      <el-select
        v-model="listQuery.brandId"
        placeholder="商品品牌"
        clearable
        class="filter-item"
        style="width: 200px"
      >
        <el-option
          v-for="item in brandOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
      <el-button
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="handleFilter">
        搜索
      </el-button>
    </div>

    <el-table 
      ref="productTable"
      :data="list"
      border
      @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column label="商品图片" width="120" align="center">
        <template slot-scope="scope">
          <img v-if="scope.row.pic" :src="scope.row.pic" style="height: 80px">
        </template>
      </el-table-column>
      <el-table-column label="商品ID" width="100" align="center">
        <template slot-scope="scope">{{scope.row.id}}</template>
      </el-table-column>
      <el-table-column label="商品名称" align="center">
        <template slot-scope="scope">{{scope.row.name}}</template>
      </el-table-column>
      <el-table-column label="品牌" width="120" align="center">
        <template slot-scope="scope">{{scope.row.brandName}}</template>
      </el-table-column>
      <el-table-column label="商品价格" width="120" align="center">
        <template slot-scope="scope">¥{{scope.row.price}}</template>
      </el-table-column>
    </el-table>
    
    <div class="pagination-container">
      <el-pagination
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        layout="total, sizes, prev, pager, next, jumper"
        :current-page.sync="listQuery.pageNum"
        :page-size="listQuery.pageSize"
        :page-sizes="[5, 10, 15]"
        :total="total">
      </el-pagination>
    </div>
    
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">取 消</el-button>
      <el-button type="primary" @click="handleConfirm">确 定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { fetchList } from '@/api/product'
import { fetchList as fetchBrandList } from '@/api/brand'

export default {
  name: 'ProductSelectionDialog',
  props: {
    // 弹窗标题
    title: {
      type: String,
      default: '选择商品'
    },
    // 是否可见
    visible: {
      type: Boolean,
      default: false
    },
    // 支持的选择模式：single(单选), multiple(多选)
    selectionMode: {
      type: String,
      default: 'multiple'
    },
    // 已选择的商品
    selectedProducts: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      dialogVisible: false,
      list: [],
      total: 0,
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        keyword: null,
        productId: null,
        brandId: null
      },
      selectedItems: [],
      brandOptions: []
    }
  },
  watch: {
    visible: {
      handler(val) {
        this.dialogVisible = val
        if (val) {
          this.getList()
          this.getBrandList()
          this.selectedItems = [...this.selectedProducts]
        }
      },
      immediate: true
    },
    // 监听selectedProducts的变化，确保预选逻辑正确执行
    selectedProducts: {
      handler(newVal) {
        // 更新selectedItems
        if (newVal) {
          this.selectedItems = [...newVal]

          // 如果对话框已显示且有商品列表，立即执行预选
          if (this.dialogVisible && this.list && this.list.length > 0) {
            this.preSelectProducts()
          }
        }
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
    getList() {
      fetchList(this.listQuery).then(response => {
        this.list = response.data.list
        this.total = response.data.total
        this.$nextTick(() => {
          this.preSelectProducts()
        })
      })
    },
    getBrandList() {
      fetchBrandList({pageNum: 1, pageSize: 100}).then(response => {
        this.brandOptions = response.data.list.map(item => {
          return {label: item.name, value: item.id}
        })
      })
    },
    preSelectProducts() {
      // 关键修复：确保selectedItems与selectedProducts同步
      if (this.selectedProducts && this.selectedProducts.length > 0) {
        this.selectedItems = [...this.selectedProducts]
      }

      if (!this.$refs.productTable) return

      this.$refs.productTable.clearSelection()

      if (this.selectedItems && this.selectedItems.length > 0) {
        const selectedIds = this.selectedItems.map(item => item.id)

        this.list.forEach(item => {
          if (selectedIds.includes(item.id)) {
            this.$refs.productTable.toggleRowSelection(item, true)
          }
        })
      }
    },
    handleFilter() {
      this.listQuery.pageNum = 1
      this.getList()
    },
    handleSizeChange(val) {
      this.listQuery.pageSize = val
      this.getList()
    },
    handleCurrentChange(val) {
      this.listQuery.pageNum = val
      this.getList()
    },
    handleSelectionChange(selection) {
      if (this.selectionMode === 'single' && selection.length > 1) {
        const lastSelected = selection[selection.length - 1]
        this.$refs.productTable.clearSelection()
        this.$refs.productTable.toggleRowSelection(lastSelected, true)
        this.selectedItems = [lastSelected]
        this.$message({
          message: '单选模式只能选择一个商品',
          type: 'warning'
        })
      } else {
        this.selectedItems = selection
      }
    },
    handleConfirm() {
      this.$emit('selection-confirmed', this.selectedItems)
      this.dialogVisible = false
    },
    handleClose() {
      this.$emit('update:visible', false)
    }
  }
}
</script>

<style scoped>
.filter-container {
  padding-bottom: 10px;
}
.filter-item {
  margin-right: 10px;
  margin-bottom: 10px;
}
.pagination-container {
  margin-top: 15px;
}
</style> 