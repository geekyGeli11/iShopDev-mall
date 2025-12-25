<template>
  <div class="app-container">
    <!-- 面包屑导航 -->
    <el-breadcrumb class="app-breadcrumb" separator="/">
      <el-breadcrumb-item>
        <router-link to="/home">首页</router-link>
      </el-breadcrumb-item>
      <el-breadcrumb-item>DIY管理</el-breadcrumb-item>
      <el-breadcrumb-item>DIY素材分类管理</el-breadcrumb-item>
    </el-breadcrumb>

    <!-- 搜索筛选区域 -->
    <el-card class="filter-container" shadow="never">
      <div>
        <i class="el-icon-search"></i>
        <span>筛选搜索</span>
        <el-button
          style="float: right"
          type="primary"
          @click="handleSearchList()"
          size="small">
          查询搜索
        </el-button>
        <el-button
          style="float: right; margin-right: 15px"
          @click="handleResetSearch()"
          size="small">
          重置
        </el-button>
      </div>
      <div style="margin-top: 15px">
        <el-form :inline="true" :model="listQuery" size="small" label-width="140px">
          <el-form-item label="分类名称：">
            <el-input
              v-model="listQuery.name"
              class="input-width"
              placeholder="分类名称"
              clearable>
            </el-input>
          </el-form-item>
          <el-form-item label="分类类型：">
            <el-select
              v-model="listQuery.type"
              placeholder="全部"
              clearable
              class="input-width">
              <el-option
                label="图片素材"
                :value="1">
              </el-option>
              <el-option
                label="文字字体"
                :value="2">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="启用状态：">
            <el-select
              v-model="listQuery.status"
              placeholder="全部"
              clearable
              class="input-width">
              <el-option
                label="启用"
                :value="1">
              </el-option>
              <el-option
                label="禁用"
                :value="0">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 操作按钮区域 -->
    <el-card class="operate-container" shadow="never">
      <i class="el-icon-tickets"></i>
      <span>数据列表</span>
      <el-button
        style="float: right"
        type="primary"
        @click="handleAdd()"
        size="small">
        添加
      </el-button>
      <el-button
        style="float: right; margin-right: 15px"
        type="danger"
        @click="handleDelete()"
        size="small">
        批量删除
      </el-button>
    </el-card>

    <!-- 表格区域 -->
    <div class="table-container">
      <el-table
        ref="categoryTable"
        :data="list"
        style="width: 100%"
        @selection-change="handleSelectionChange"
        v-loading="listLoading">
        <el-table-column type="selection" width="60" align="center"></el-table-column>
        <el-table-column label="编号" width="120" align="center">
          <template slot-scope="scope">{{ scope.row.id }}</template>
        </el-table-column>
        <el-table-column label="分类名称" align="center">
          <template slot-scope="scope">{{ scope.row.name }}</template>
        </el-table-column>
        <el-table-column label="分类类型" width="120" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.type === 1 ? 'primary' : 'success'">
              {{ scope.row.type === 1 ? '图片素材' : '文字字体' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="描述" align="center">
          <template slot-scope="scope">{{ scope.row.description || '暂无描述' }}</template>
        </el-table-column>
        <el-table-column label="排序" width="100" align="center">
          <template slot-scope="scope">{{ scope.row.sort }}</template>
        </el-table-column>
        <el-table-column label="状态" width="140" align="center">
          <template slot-scope="scope">
            <el-switch
              @change="handleStatusChange(scope.$index, scope.row)"
              :active-value="1"
              :inactive-value="0"
              v-model="scope.row.status">
            </el-switch>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180" align="center">
          <template slot-scope="scope">{{ scope.row.createTime | formatDateTime }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              @click="handleUpdate(scope.$index, scope.row)">
              编辑
            </el-button>
            <el-button
              size="mini"
              type="text"
              @click="handleDelete(scope.$index, scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页区域 -->
    <div class="pagination-container">
      <el-pagination
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        layout="total, sizes,prev, pager, next,jumper"
        :current-page.sync="listQuery.pageNum"
        :page-size="listQuery.pageSize"
        :page-sizes="[5,10,15]"
        :total="total">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import {
  fetchMaterialCategoryList,
  deleteMaterialCategory,
  updateMaterialCategoryStatus
} from '@/api/diyMaterial'
import { formatDate } from '@/utils/date'

export default {
  name: 'DiyMaterialCategoryList',
  data() {
    return {
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        type: null,
        status: null
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
    handleResetSearch() {
      this.listQuery = {
        pageNum: 1,
        pageSize: 10,
        name: null,
        type: null,
        status: null
      }
    },
    handleSearchList() {
      this.listQuery.pageNum = 1
      this.getList()
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
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    handleAdd() {
      this.$router.push({ path: '/diy/addMaterialCategory' })
    },
    handleUpdate(index, row) {
      this.$router.push({ path: '/diy/updateMaterialCategory', query: { id: row.id } })
    },
    handleStatusChange(index, row) {
      this.$confirm('是否要修改该状态?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        updateMaterialCategoryStatus({ id: row.id, status: row.status }).then(response => {
          this.$message({
            type: 'success',
            message: '修改成功!'
          })
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消修改'
        })
        this.getList()
      })
    },
    handleDelete(index, row) {
      if (row !== undefined) {
        this.$confirm('是否要删除该分类?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          deleteMaterialCategory(row.id).then(response => {
            this.$message({
              type: 'success',
              message: '删除成功!'
            })
            this.getList()
          })
        })
      } else {
        if (this.multipleSelection.length < 1) {
          this.$message({
            message: '请选择要删除的分类',
            type: 'warning'
          })
          return
        }
        this.$confirm('是否要删除选中的分类?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          let ids = []
          for (let i = 0; i < this.multipleSelection.length; i++) {
            ids.push(this.multipleSelection[i].id)
          }
          let deletePromises = ids.map(id => deleteMaterialCategory(id))
          Promise.all(deletePromises).then(() => {
            this.$message({
              type: 'success',
              message: '删除成功!'
            })
            this.getList()
          }).catch(() => {
            this.$message({
              type: 'error',
              message: '删除失败!'
            })
          })
        })
      }
    },
    getList() {
      this.listLoading = true
      fetchMaterialCategoryList(this.listQuery).then(response => {
        this.listLoading = false
        this.list = response.data.list
        this.total = response.data.total
      })
    }
  }
}
</script>

<style scoped>
.input-width {
  width: 203px;
}
</style>
