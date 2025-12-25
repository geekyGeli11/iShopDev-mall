<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input
        v-model="listQuery.keyword"
        placeholder="素材名称"
        style="width: 200px;"
        class="filter-item"
        @keyup.enter.native="handleSearchList"
      />
      <el-select
        v-model="listQuery.categoryId"
        placeholder="素材分类"
        clearable
        style="width: 200px"
        class="filter-item"
      >
        <el-option
          v-for="item in categoryOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
      <el-select
        v-model="listQuery.status"
        placeholder="状态"
        clearable
        style="width: 120px"
        class="filter-item"
      >
        <el-option label="启用" :value="1"/>
        <el-option label="禁用" :value="0"/>
      </el-select>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleSearchList">
        查询
      </el-button>
      <el-button
        class="filter-item"
        style="margin-left: 10px;"
        type="primary"
        icon="el-icon-edit"
        @click="handleAdd"
      >
        添加
      </el-button>
    </div>

    <el-table
      :key="tableKey"
      v-loading="listLoading"
      :data="list"
      border
      fit
      highlight-current-row
      style="width: 100%;"
    >
      <el-table-column label="编号" prop="id" align="center" width="80">
        <template slot-scope="{row}">
          <span>{{ row.id }}</span>
        </template>
      </el-table-column>
      <el-table-column label="素材图片" width="120" align="center">
        <template slot-scope="{row}">
          <img v-if="row.fileUrl" :src="row.fileUrl" style="height: 60px; width: 60px">
        </template>
      </el-table-column>
      <el-table-column label="素材名称" align="center">
        <template slot-scope="{row}">
          <span>{{ row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column label="素材分类" align="center">
        <template slot-scope="{row}">
          <span>{{ row.categoryName }}</span>
        </template>
      </el-table-column>
      <el-table-column label="素材类型" align="center">
        <template slot-scope="{row}">
          <el-tag :type="row.fileType == 1 ? 'success' : 'info'">
            {{ row.fileType == 1 ? '图片' : '文字' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="110" align="center">
        <template slot-scope="{row}">
          <el-switch
            v-model="row.status"
            :active-value="1"
            :inactive-value="0"
            @change="handleStatusChange(row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="160" align="center">
        <template slot-scope="{row}">
          <span>{{ $formatDate(row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="180" class-name="small-padding fixed-width">
        <template slot-scope="{row}">
          <el-button type="primary" size="mini" @click="handleUpdate(row)">
            编辑
          </el-button>
          <el-button size="mini" type="danger" @click="handleDelete(row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        layout="total, sizes,prev, pager, next,jumper"
        :page-size="listQuery.pageSize"
        :page-sizes="[10,20,50]"
        :current-page.sync="listQuery.pageNum"
        :total="total">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import { fetchMaterialList, deleteMaterial, updateMaterialStatus, fetchMaterialCategoryList } from '@/api/diyMaterial'

export default {
  name: 'DiyMaterialList',
  data() {
    return {
      tableKey: 0,
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        pageNum: 1,
        pageSize: 20,
        keyword: undefined,
        categoryId: undefined,
        status: undefined
      },
      categoryOptions: []
    }
  },
  created() {
    this.getCategoryList().then(() => {
      this.getList()
    })
  },
  methods: {
    getList() {
      this.listLoading = true
      fetchMaterialList(this.listQuery).then(response => {
        this.list = response.data.list
        this.total = response.data.total
        // 为每个素材添加分类名称
        this.list.forEach(item => {
          const category = this.categoryOptions.find(cat => cat.value === item.categoryId)
          item.categoryName = category ? category.label : '未分类'
        })
        this.listLoading = false
      })
    },
    getCategoryList() {
      return fetchMaterialCategoryList({ pageNum: 1, pageSize: 100 }).then(response => {
        this.categoryOptions = response.data.list.map(item => ({
          label: item.name,
          value: item.id
        }))
      })
    },
    handleSearchList() {
      this.listQuery.pageNum = 1
      this.getList()
    },
    handleAdd() {
      this.$router.push({ path: '/diy/addMaterial' })
    },
    handleUpdate(row) {
      this.$router.push({ path: '/diy/updateMaterial', query: { id: row.id }})
    },
    handleDelete(row) {
      this.$confirm('是否要删除该素材?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteMaterial(row.id).then(response => {
          this.$message({
            message: '删除成功！',
            type: 'success',
            duration: 1000
          })
          this.getList()
        })
      })
    },
    handleStatusChange(row) {
      this.$confirm('是否要修改该状态?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        updateMaterialStatus({ ids: row.id, status: row.status }).then(response => {
          this.$message({
            message: '修改成功！',
            type: 'success',
            duration: 1000
          })
        })
      }).catch(() => {
        this.$message({
          message: '取消修改',
          type: 'info',
          duration: 1000
        })
        this.getList()
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
    }
  }
}
</script>
