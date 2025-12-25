<template>
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
      <div>
        <i class="el-icon-search"></i>
        <span>筛选搜索</span>
        <el-button
          style="float: right"
          @click="searchList()"
          type="primary"
          size="small">
          查询结果
        </el-button>
      </div>
      <div style="margin-top: 15px">
        <el-form :inline="true" :model="listQuery" size="small" label-width="140px">
          <el-form-item label="输入搜索：">
            <el-input style="width: 203px" v-model="listQuery.keyword" placeholder="组合名称"></el-input>
          </el-form-item>
          <el-form-item label="上架状态：">
            <el-select v-model="listQuery.publishStatus" placeholder="全部" clearable>
              <el-option label="上架" :value="1"></el-option>
              <el-option label="下架" :value="0"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="operate-container" shadow="never">
      <i class="el-icon-tickets"></i>
      <span>数据列表</span>
      <el-button
        class="btn-add"
        @click="handleAdd()"
        size="mini">
        添加
      </el-button>
    </el-card>
    <div class="table-container">
      <el-table ref="bundleTable"
                :data="list"
                style="width: 100%"
                @selection-change="handleSelectionChange"
                v-loading="listLoading"
                border>
        <el-table-column type="selection" width="60" align="center"></el-table-column>
        <el-table-column label="编号" width="80" align="center">
          <template slot-scope="scope">{{scope.row.id}}</template>
        </el-table-column>
        <el-table-column label="组合图片" width="120" align="center">
          <template slot-scope="scope">
            <img style="height: 60px" :src="scope.row.pic">
          </template>
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
        <el-table-column label="定价方式" width="100" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.priceType === 1" type="primary" size="small">固定价格</el-tag>
            <el-tag v-else type="success" size="small">折扣比例</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="销量" width="80" align="center">
          <template slot-scope="scope">{{scope.row.saleCount}}</template>
        </el-table-column>
        <el-table-column label="排序" width="80" align="center">
          <template slot-scope="scope">{{scope.row.sort}}</template>
        </el-table-column>
        <el-table-column label="上架状态" width="100" align="center">
          <template slot-scope="scope">
            <el-switch
              @change="handlePublishStatusChange(scope.$index, scope.row)"
              :active-value="1"
              :inactive-value="0"
              v-model="scope.row.publishStatus">
            </el-switch>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="160" align="center">
          <template slot-scope="scope">{{scope.row.createTime | formatDateTime}}</template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              @click="handleUpdate(scope.$index, scope.row)">编辑
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
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        layout="total, sizes,prev, pager, next,jumper"
        :page-size="listQuery.pageSize"
        :page-sizes="[10, 20, 50]"
        :current-page.sync="listQuery.pageNum"
        :total="total">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import { fetchList, deleteBundle, updatePublishStatus } from '@/api/productBundle'
import { formatDate } from '@/utils/date'

export default {
  name: 'productBundleList',
  data() {
    return {
      operates: [
        { label: "上架", value: "publish" },
        { label: "下架", value: "unpublish" },
        { label: "删除", value: "delete" }
      ],
      operateType: null,
      listQuery: {
        keyword: null,
        publishStatus: null,
        pageNum: 1,
        pageSize: 10
      },
      list: null,
      total: null,
      listLoading: true,
      multipleSelection: []
    }
  },
  created() {
    this.getList()
  },
  filters: {
    formatDateTime(time) {
      if (time == null || time === '') {
        return 'N/A'
      }
      let date = new Date(time)
      return formatDate(date, 'yyyy-MM-dd hh:mm:ss')
    }
  },
  methods: {
    getList() {
      this.listLoading = true
      fetchList(this.listQuery).then(response => {
        this.listLoading = false
        this.list = response.data.list
        this.total = response.data.total
      })
    },
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    handleAdd() {
      this.$router.push({ path: '/sms/addProductBundle' })
    },
    handleUpdate(index, row) {
      this.$router.push({ path: '/sms/updateProductBundle', query: { id: row.id } })
    },
    handleDelete(index, row) {
      this.$confirm('是否要删除该组合商品?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteBundle(row.id).then(response => {
          this.$message({
            message: '删除成功',
            type: 'success',
            duration: 1000
          })
          this.getList()
        })
      })
    },
    handlePublishStatusChange(index, row) {
      let ids = row.id.toString()
      updatePublishStatus(ids, row.publishStatus).then(response => {
        this.$message({
          message: '修改成功',
          type: 'success',
          duration: 1000
        })
      }).catch(error => {
        row.publishStatus = row.publishStatus === 0 ? 1 : 0
      })
    },
    handleSizeChange(val) {
      this.listQuery.pageNum = 1
      this.listQuery.pageSize = val
      this.getList()
    },
    handleCurrentChange(val) {
      this.listQuery.pageNum = val
      this.getList()
    },
    searchList() {
      this.listQuery.pageNum = 1
      this.getList()
    },
    handleBatchOperate() {
      if (this.multipleSelection.length < 1) {
        this.$message({
          message: '请选择一条记录',
          type: 'warning',
          duration: 1000
        })
        return
      }
      let ids = this.multipleSelection.map(item => item.id).join(',')
      if (this.operateType === 'publish') {
        updatePublishStatus(ids, 1).then(response => {
          this.getList()
          this.$message({ message: '上架成功', type: 'success', duration: 1000 })
        })
      } else if (this.operateType === 'unpublish') {
        updatePublishStatus(ids, 0).then(response => {
          this.getList()
          this.$message({ message: '下架成功', type: 'success', duration: 1000 })
        })
      } else if (this.operateType === 'delete') {
        this.$confirm('是否要删除选中的组合商品?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          // 批量删除需要逐个调用
          let promises = this.multipleSelection.map(item => deleteBundle(item.id))
          Promise.all(promises).then(() => {
            this.getList()
            this.$message({ message: '删除成功', type: 'success', duration: 1000 })
          })
        })
      } else {
        this.$message({
          message: '请选择批量操作类型',
          type: 'warning',
          duration: 1000
        })
      }
    }
  }
}
</script>
<style rel="stylesheet/scss" lang="scss" scoped>
</style>
