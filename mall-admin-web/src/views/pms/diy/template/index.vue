<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input
        v-model="listQuery.keyword"
        placeholder="模板名称"
        style="width: 200px;"
        class="filter-item"
        @keyup.enter.native="handleSearchList"
      />
      <el-select
        v-model="listQuery.productCategoryId"
        placeholder="商品分类"
        clearable
        style="width: 200px"
        class="filter-item"
      >
        <el-option-group
          v-for="group in categoryOptions"
          :key="group.value"
          :label="group.label">
          <!-- 一级分类选项 -->
          <el-option
            :key="'parent-' + group.value"
            :label="group.label"
            :value="group.value">
            <span style="font-weight: bold">{{ group.label }}</span>
          </el-option>
          <!-- 二级分类选项 -->
          <el-option
            v-for="item in group.children"
            :key="item.value"
            :label="'　└ ' + item.label"
            :value="item.value">
          </el-option>
        </el-option-group>
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
      <el-table-column label="模板名称" align="center">
        <template slot-scope="{row}">
          <span>{{ row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column label="适用分类" align="center">
        <template slot-scope="{row}">
          <span>{{ getCategoryName(row.productCategoryId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="描述" align="center">
        <template slot-scope="{row}">
          <span>{{ row.description }}</span>
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
      <el-table-column label="操作" align="center" width="320" class-name="small-padding fixed-width">
        <template slot-scope="{row}">
          <el-button type="info" size="mini" @click="handleDetail(row)">
            详情
          </el-button>
          <el-button type="warning" size="mini" @click="handlePreview(row)">
            预览
          </el-button>
          <el-button type="primary" size="mini" @click="handleUpdate(row)">
            编辑
          </el-button>
          <el-button type="success" size="mini" @click="handleCopy(row)">
            复制
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

    <!-- 复制模板对话框 -->
    <el-dialog title="复制模板" :visible.sync="copyDialogVisible" width="400px">
      <el-form ref="copyForm" :model="copyForm" :rules="copyRules" label-width="100px">
        <el-form-item label="新模板名称" prop="newName">
          <el-input v-model="copyForm.newName" placeholder="请输入新模板名称"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="copyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmCopy">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { fetchTemplateList, deleteTemplate, updateTemplateStatus, copyTemplate } from '@/api/diyTemplate'
import { fetchListWithChildren } from '@/api/productCate'

export default {
  name: 'DiyTemplateList',
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
        productCategoryId: undefined,
        status: undefined
      },
      categoryOptions: [],
      copyDialogVisible: false,
      copyForm: {
        id: null,
        newName: ''
      },
      copyRules: {
        newName: [
          { required: true, message: '请输入新模板名称', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
    this.getCategoryList()
  },
  methods: {
    getList() {
      this.listLoading = true
      fetchTemplateList(this.listQuery).then(response => {
        this.list = response.data.list
        this.total = response.data.total
        this.listLoading = false
      })
    },
    getCategoryList() {
      fetchListWithChildren().then(response => {
        let list = response.data;
        this.categoryOptions = [];
        for (let i = 0; i < list.length; i++) {
          let children = [];
          if (list[i].children != null && list[i].children.length > 0) {
            for (let j = 0; j < list[i].children.length; j++) {
              children.push({
                label: list[i].children[j].name,
                value: list[i].children[j].id,
                leaf: true
              });
            }
          }
          this.categoryOptions.push({
            label: list[i].name,
            value: list[i].id,
            children: children,
            leaf: children.length === 0
          });
        }
      }).catch(error => {
        console.error('获取分类列表失败:', error);
      });
    },
    getCategoryName(categoryId) {
      if (!categoryId) return '全部分类'

      // 先在一级分类中查找
      for (let i = 0; i < this.categoryOptions.length; i++) {
        if (this.categoryOptions[i].value === categoryId) {
          return this.categoryOptions[i].label
        }
        // 再在二级分类中查找
        if (this.categoryOptions[i].children) {
          for (let j = 0; j < this.categoryOptions[i].children.length; j++) {
            if (this.categoryOptions[i].children[j].value === categoryId) {
              return this.categoryOptions[i].children[j].label
            }
          }
        }
      }
      return '未知分类'
    },
    handleSearchList() {
      this.listQuery.pageNum = 1
      this.getList()
    },
    handleAdd() {
      this.$router.push({ path: '/diy/addTemplate' })
    },
    handleDetail(row) {
      this.$router.push({ path: '/diy/templateDetail', query: { id: row.id }})
    },
    handlePreview(row) {
      this.$router.push({ path: `/diy/templatePreview/${row.id}` })
    },
    handleUpdate(row) {
      this.$router.push({ path: '/diy/updateTemplate', query: { id: row.id }})
    },
    handleCopy(row) {
      this.copyForm.id = row.id
      this.copyForm.newName = row.name + '_副本'
      this.copyDialogVisible = true
    },
    confirmCopy() {
      this.$refs.copyForm.validate((valid) => {
        if (valid) {
          copyTemplate(this.copyForm.id, { newName: this.copyForm.newName }).then(response => {
            this.$message({
              message: '复制成功！',
              type: 'success',
              duration: 1000
            })
            this.copyDialogVisible = false
            this.getList()
          })
        }
      })
    },
    handleDelete(row) {
      this.$confirm('是否要删除该模板?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteTemplate(row.id).then(response => {
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
        updateTemplateStatus({ ids: row.id, status: row.status }).then(response => {
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
