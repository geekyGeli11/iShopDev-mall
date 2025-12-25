<template>
  <el-dialog
    title="选择组合商品"
    :visible.sync="dialogVisible"
    width="70%"
    @close="handleClose">
    <div class="filter-container">
      <el-input
        v-model="listQuery.keyword"
        placeholder="组合名称"
        style="width: 200px;"
        class="filter-item"
        @keyup.enter.native="handleFilter"
        clearable
      />
      <el-select
        v-model="listQuery.publishStatus"
        placeholder="上架状态"
        clearable
        class="filter-item"
        style="width: 150px">
        <el-option label="上架" :value="1"></el-option>
        <el-option label="下架" :value="0"></el-option>
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
      ref="bundleTable"
      :data="list"
      border
      highlight-current-row
      @row-click="handleRowClick"
      v-loading="listLoading">
      <el-table-column label="选择" width="60" align="center">
        <template slot-scope="scope">
          <el-radio v-model="currentRowId" :label="scope.row.id">&nbsp;</el-radio>
        </template>
      </el-table-column>
      <el-table-column label="组合图片" width="100" align="center">
        <template slot-scope="scope">
          <img v-if="scope.row.pic" :src="scope.row.pic" style="height: 60px">
        </template>
      </el-table-column>
      <el-table-column label="编号" width="80" align="center">
        <template slot-scope="scope">{{scope.row.id}}</template>
      </el-table-column>
      <el-table-column label="组合名称" align="center">
        <template slot-scope="scope">{{scope.row.name}}</template>
      </el-table-column>
      <el-table-column label="原价" width="100" align="center">
        <template slot-scope="scope">¥{{scope.row.originalPrice}}</template>
      </el-table-column>
      <el-table-column label="组合价" width="100" align="center">
        <template slot-scope="scope">
          <span style="color: #f56c6c">¥{{scope.row.bundlePrice}}</span>
        </template>
      </el-table-column>
      <el-table-column label="上架状态" width="100" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.publishStatus === 1" type="success" size="small">上架</el-tag>
          <el-tag v-else type="info" size="small">下架</el-tag>
        </template>
      </el-table-column>
    </el-table>
    
    <div class="pagination-container">
      <el-pagination
        background
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
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
import { fetchList } from '@/api/productBundle'

export default {
  name: 'BundleSelectionDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    selectedBundle: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      dialogVisible: false,
      list: [],
      total: 0,
      listLoading: false,
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        keyword: null,
        publishStatus: 1 // 默认只显示上架的
      },
      currentRow: null,
      currentRowId: null
    }
  },
  watch: {
    visible: {
      handler(val) {
        this.dialogVisible = val
        if (val) {
          this.getList()
          this.currentRow = this.selectedBundle
          this.currentRowId = this.selectedBundle ? this.selectedBundle.id : null
        }
      },
      immediate: true
    }
  },
  methods: {
    getList() {
      this.listLoading = true
      fetchList(this.listQuery).then(response => {
        this.list = response.data.list
        this.total = response.data.total
        this.listLoading = false
        this.$nextTick(() => {
          this.preSelectBundle()
        })
      }).catch(() => {
        this.listLoading = false
      })
    },
    preSelectBundle() {
      if (!this.selectedBundle) return
      this.currentRowId = this.selectedBundle.id
      const row = this.list.find(item => item.id === this.selectedBundle.id)
      if (row) {
        this.currentRow = row
      }
    },
    handleRowClick(row) {
      this.currentRow = row
      this.currentRowId = row.id
    },
    handleFilter() {
      this.listQuery.pageNum = 1
      this.getList()
    },
    handleSizeChange(val) {
      this.listQuery.pageSize = val
      this.getList()
    },
    handlePageChange(val) {
      this.listQuery.pageNum = val
      this.getList()
    },
    handleConfirm() {
      if (!this.currentRow) {
        this.$message.warning('请选择一个组合商品')
        return
      }
      this.$emit('selection-confirmed', this.currentRow)
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
