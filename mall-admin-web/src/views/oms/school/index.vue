<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <div class="filter-container">
      <el-input
        v-model="listQuery.schoolName"
        placeholder="学校名称"
        style="width: 200px;"
        class="filter-item"
        @keyup.enter.native="handleSearchList"
      />
      <el-select
        v-model="listQuery.status"
        placeholder="状态"
        clearable
        style="width: 120px"
        class="filter-item"
      >
        <el-option label="启用" :value="true"/>
        <el-option label="禁用" :value="false"/>
      </el-select>
      <el-button
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="handleSearchList"
      >
        搜索
      </el-button>
      <el-button
        class="filter-item"
        type="primary"
        icon="el-icon-plus"
        @click="handleAddSchool"
      >
        添加学校
      </el-button>
    </div>

    <!-- 数据列表 -->
    <div class="table-container">
      <el-table
        ref="schoolTable"
        :data="list"
        style="width: 100%;"
        v-loading="listLoading"
        border
      >
        <el-table-column label="ID" width="80" align="center">
          <template slot-scope="scope">{{ scope.row.id }}</template>
        </el-table-column>
        <el-table-column label="学校名称" align="center">
          <template slot-scope="scope">{{ scope.row.schoolName }}</template>
        </el-table-column>
        <el-table-column label="学校校徽" width="120" align="center">
          <template slot-scope="scope">
            <img v-if="scope.row.schoolLogo" :src="scope.row.schoolLogo" alt="学校校徽" style="width: 60px; height: 60px; object-fit: cover;" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="封面缩略图" width="120" align="center">
          <template slot-scope="scope">
            <img v-if="scope.row.coverThumbnail" :src="scope.row.coverThumbnail" alt="封面缩略图" style="width: 60px; height: 60px; object-fit: cover; border-radius: 4px;" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="true"
              :inactive-value="false"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180" align="center">
          <template slot-scope="scope">{{ scope.row.createTime | formatDateTime }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="primary"
              @click="handleUpdate(scope.row)"
            >
              编辑
            </el-button>
            <el-button
              size="mini"
              type="danger"
              @click="handleDelete(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        layout="total, sizes,prev, pager, next,jumper"
        :current-page.sync="listQuery.pageNum"
        :page-size="listQuery.pageSize"
        :page-sizes="[10,15,20]"
        :total="total"
      />
    </div>

    <!-- 添加/编辑学校对话框 -->
    <el-dialog
      :title="isEdit ? '编辑学校' : '添加学校'"
      :visible.sync="dialogVisible"
      width="40%"
    >
      <el-form
        ref="schoolForm"
        :model="school"
        :rules="schoolRules"
        label-width="120px"
        size="small"
      >
        <el-form-item label="学校名称" prop="schoolName">
          <el-input v-model="school.schoolName" placeholder="请输入学校名称"/>
        </el-form-item>
        <el-form-item label="学校校徽" prop="schoolLogo">
          <single-upload v-model="school.schoolLogo"/>
        </el-form-item>
        <el-form-item label="封面缩略图" prop="coverThumbnail">
          <single-upload
            v-model="school.coverThumbnail"
            :aspect-ratio="1"
            :show-preview="true"
          />
          <div style="color: #999; font-size: 12px; margin-top: 5px;">
            建议上传1:1比例的正方形图片，用于小程序首页和分类页面展示
          </div>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="school.status">
            <el-radio :label="true">启用</el-radio>
            <el-radio :label="false">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" size="small">取 消</el-button>
        <el-button type="primary" @click="handleDialogConfirm" size="small">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  fetchList,
  createSchool,
  updateSchool,
  deleteSchool,
  updateSchoolStatus
} from '@/api/school'
import SingleUpload from '@/components/Upload/singleUpload'

const defaultSchool = {
  id: null,
  schoolName: '',
  schoolLogo: '',
  coverThumbnail: '',
  status: true
}

export default {
  name: 'SchoolList',
  components: {
    SingleUpload
  },
  data() {
    return {
      list: [],
      total: 0,
      listLoading: false,
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        schoolName: null,
        status: null
      },
      dialogVisible: false,
      isEdit: false,
      school: Object.assign({}, defaultSchool),
      schoolRules: {
        schoolName: [
          { required: true, message: '请输入学校名称', trigger: 'blur' },
          { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
        ],
        status: [
          { required: true, message: '请选择状态', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 获取数据列表
    getList() {
      this.listLoading = true
      fetchList(this.listQuery).then(response => {
        this.listLoading = false
        this.list = response.data.list
        this.total = response.data.total
      }).catch(() => {
        this.listLoading = false
      })
    },
    // 搜索
    handleSearchList() {
      this.listQuery.pageNum = 1
      this.getList()
    },
    // 处理分页大小改变
    handleSizeChange(val) {
      this.listQuery.pageNum = 1
      this.listQuery.pageSize = val
      this.getList()
    },
    // 处理页码改变
    handleCurrentChange(val) {
      this.listQuery.pageNum = val
      this.getList()
    },
    // 添加学校
    handleAddSchool() {
      this.dialogVisible = true
      this.isEdit = false
      this.school = Object.assign({}, defaultSchool)
    },
    // 编辑学校
    handleUpdate(row) {
      this.dialogVisible = true
      this.isEdit = true
      this.school = Object.assign({}, row)
    },
    // 删除学校
    handleDelete(row) {
      this.$confirm('是否要删除该学校?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteSchool(row.id).then(response => {
          this.$message({
            type: 'success',
            message: '删除成功!'
          })
          this.getList()
        })
      })
    },
    // 改变状态
    handleStatusChange(row) {
      updateSchoolStatus(row.id, row.status).then(response => {
        this.$message({
          type: 'success',
          message: '状态更新成功!'
        })
      }).catch(() => {
        // 如果更新失败，恢复原状态
        row.status = row.status === 1 ? 0 : 1
      })
    },
    // 对话框确认
    handleDialogConfirm() {
      this.$refs['schoolForm'].validate((valid) => {
        if (valid) {
          if (this.isEdit) {
            updateSchool(this.school.id, this.school).then(response => {
              this.$message({
                type: 'success',
                message: '更新成功!'
              })
              this.dialogVisible = false
              this.getList()
            })
          } else {
            createSchool(this.school).then(response => {
              this.$message({
                type: 'success',
                message: '添加成功!'
              })
              this.dialogVisible = false
              this.getList()
            })
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.filter-container {
  padding-bottom: 10px;
}

.filter-item {
  display: inline-block;
  vertical-align: middle;
  margin-bottom: 10px;
  margin-right: 10px;
}

.table-container {
  margin-top: 20px;
}

.pagination-container {
  margin-top: 20px;
  text-align: center;
}
</style>
