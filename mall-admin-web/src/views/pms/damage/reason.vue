<template>
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
      <div>
        <i class="el-icon-search"></i>
        <span>筛选搜索</span>
        <el-button style="float:right" type="primary" @click="handleSearchList()" size="small">查询搜索</el-button>
        <el-button style="float:right;margin-right: 15px" @click="handleResetSearch()" size="small">重置</el-button>
      </div>
      <div style="margin-top: 15px">
        <el-form :inline="true" :model="listQuery" size="small" label-width="100px">
          <el-form-item label="原因名称：">
            <el-input v-model="listQuery.keyword" class="input-width" placeholder="原因名称"></el-input>
          </el-form-item>
          <el-form-item label="原因类型：">
            <el-select v-model="listQuery.type" placeholder="全部类型" clearable class="input-width">
              <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="operate-container" shadow="never">
      <i class="el-icon-tickets"></i>
      <span>数据列表</span>
      <el-button size="mini" type="primary" style="float:right" @click="handleAdd()">添加原因</el-button>
    </el-card>
    <div class="table-container">
      <el-table ref="reasonTable" :data="list" style="width: 100%;" v-loading="listLoading" border>
        <el-table-column label="ID" width="80" align="center" prop="id"></el-table-column>
        <el-table-column label="原因名称" align="center" prop="name"></el-table-column>
        <el-table-column label="原因类型" width="120" align="center">
          <template slot-scope="scope">
            <el-tag :type="getTypeTag(scope.row.type)" size="small">{{ formatType(scope.row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="描述" align="center" prop="description" show-overflow-tooltip></el-table-column>
        <el-table-column label="排序" width="80" align="center" prop="sort"></el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0"
              @change="handleStatusChange(scope.row)"></el-switch>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="160" align="center">
          <template slot-scope="scope">{{ scope.row.createTime | formatTime }}</template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="text" style="color:#F56C6C" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="pagination-container">
      <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange"
        layout="total, sizes, prev, pager, next, jumper" :current-page.sync="listQuery.pageNum"
        :page-size="listQuery.pageSize" :page-sizes="[10, 20, 50]" :total="total">
      </el-pagination>
    </div>

    <!-- 添加/编辑对话框 -->
    <el-dialog :title="dialogType === 'add' ? '添加报损原因' : '编辑报损原因'" :visible.sync="dialogVisible" width="500px">
      <el-form :model="reasonForm" :rules="reasonRules" ref="reasonForm" label-width="100px">
        <el-form-item label="原因名称" prop="name">
          <el-input v-model="reasonForm.name" placeholder="请输入原因名称"></el-input>
        </el-form-item>
        <el-form-item label="原因类型" prop="type">
          <el-select v-model="reasonForm.type" placeholder="请选择原因类型" style="width:100%">
            <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input type="textarea" v-model="reasonForm.description" :rows="3" placeholder="请输入描述"></el-input>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="reasonForm.sort" :min="0" :max="999"></el-input-number>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="reasonForm.status" :active-value="1" :inactive-value="0"></el-switch>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { formatDate } from '@/utils/date'
import { fetchDamageReasonList, createDamageReason, updateDamageReason, deleteDamageReason, updateDamageReasonStatus } from '@/api/damage'

const defaultListQuery = {
  pageNum: 1,
  pageSize: 10,
  keyword: null,
  type: null
}

const defaultReasonForm = {
  name: '',
  type: null,
  description: '',
  sort: 0,
  status: 1
}

export default {
  name: 'DamageReason',
  data() {
    return {
      listQuery: Object.assign({}, defaultListQuery),
      list: [],
      total: 0,
      listLoading: false,
      dialogVisible: false,
      dialogType: 'add',
      reasonForm: Object.assign({}, defaultReasonForm),
      reasonRules: {
        name: [{ required: true, message: '请输入原因名称', trigger: 'blur' }],
        type: [{ required: true, message: '请选择原因类型', trigger: 'change' }]
      },
      typeOptions: [
        { value: 1, label: '到货瑕疵' },
        { value: 2, label: '外借损坏' },
        { value: 3, label: '保存不当' },
        { value: 4, label: '人为原因' },
        { value: 5, label: '其他' }
      ]
    }
  },
  created() {
    this.getList()
  },
  filters: {
    formatTime(time) {
      if (!time) return '-'
      return formatDate(new Date(time), 'yyyy-MM-dd hh:mm:ss')
    }
  },
  methods: {
    getList() {
      this.listLoading = true
      fetchDamageReasonList(this.listQuery).then(response => {
        this.listLoading = false
        this.list = response.data.list
        this.total = response.data.total
      })
    },
    handleSearchList() {
      this.listQuery.pageNum = 1
      this.getList()
    },
    handleResetSearch() {
      this.listQuery = Object.assign({}, defaultListQuery)
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
    formatType(type) {
      const item = this.typeOptions.find(i => i.value === type)
      return item ? item.label : '未知'
    },
    getTypeTag(type) {
      const tags = { 1: 'danger', 2: 'warning', 3: 'info', 4: 'danger', 5: '' }
      return tags[type] || ''
    },
    handleAdd() {
      this.dialogType = 'add'
      this.reasonForm = Object.assign({}, defaultReasonForm)
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogType = 'edit'
      this.reasonForm = Object.assign({}, row)
      this.dialogVisible = true
    },
    handleSubmit() {
      this.$refs.reasonForm.validate(valid => {
        if (valid) {
          if (this.dialogType === 'add') {
            createDamageReason(this.reasonForm).then(() => {
              this.$message.success('添加成功')
              this.dialogVisible = false
              this.getList()
            })
          } else {
            updateDamageReason(this.reasonForm.id, this.reasonForm).then(() => {
              this.$message.success('更新成功')
              this.dialogVisible = false
              this.getList()
            })
          }
        }
      })
    },
    handleStatusChange(row) {
      updateDamageReasonStatus(row.id, row.status).then(() => {
        this.$message.success('状态更新成功')
      }).catch(() => {
        row.status = row.status === 1 ? 0 : 1
      })
    },
    handleDelete(row) {
      this.$confirm('确定删除该报损原因?', '提示', { type: 'warning' }).then(() => {
        deleteDamageReason(row.id).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      })
    }
  }
}
</script>
<style scoped>
.input-width {
  width: 200px;
}
</style>
