<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input
        v-model="listQuery.platform"
        placeholder="平台类型"
        style="width: 200px;"
        class="filter-item"
        @keyup.enter.native="handleFilter"
      />
      <el-button v-waves class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">
        搜索
      </el-button>
      <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-plus" @click="handleCreate">
        上传新版本
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
      <el-table-column label="版本信息" min-width="150px">
        <template slot-scope="{row}">
          <div>
            <span class="link-type">{{ row.versionName }}</span>
            <el-tag size="mini" style="margin-left: 5px;">{{ row.versionCode }}</el-tag>
          </div>
          <div style="margin-top: 5px; font-size: 12px; color: #999;">
            {{ row.targetPlatform }}
          </div>
        </template>
      </el-table-column>

      <el-table-column label="更新类型" width="100px" align="center">
        <template slot-scope="{row}">
          <el-tag :type="row.updateType ? 'danger' : 'warning'" size="mini">
            {{ row.updateType ? '强制更新' : '可选更新' }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="状态" width="80px" align="center">
        <template slot-scope="{row}">
          <el-tag :type="row.isActive ? 'success' : 'info'" size="mini">
            {{ row.isActive ? '激活' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="文件大小" width="100px" align="center">
        <template slot-scope="{row}">
          {{ formatFileSize(row.apkFileSize) }}
        </template>
      </el-table-column>

      <el-table-column label="发布时间" width="150px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.releaseTime | parseTime('{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>

      <el-table-column label="更新内容" min-width="200px">
        <template slot-scope="{row}">
          <span>{{ row.updateContent || '无' }}</span>
        </template>
      </el-table-column>

      <el-table-column label="操作" align="center" width="200px" class-name="small-padding fixed-width">
        <template slot-scope="{row}">
          <el-button type="primary" size="mini" @click="handleUpdate(row)">
            编辑
          </el-button>
          <el-button 
            :type="row.isActive ? 'warning' : 'success'" 
            size="mini" 
            @click="handleToggleStatus(row)"
          >
            {{ row.isActive ? '停用' : '激活' }}
          </el-button>
          <el-button type="danger" size="mini" @click="handleDelete(row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 上传对话框 -->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" width="600px">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="temp"
        label-position="left"
        label-width="100px"
        style="width: 500px; margin-left:20px;"
      >
        <el-form-item label="APK文件" prop="file" v-if="dialogStatus === 'create'">
          <el-upload
            ref="upload"
            class="upload-demo"
            :action="uploadAction"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            :before-upload="beforeUpload"
            :auto-upload="false"
            :limit="1"
            accept=".apk"
          >
            <el-button slot="trigger" size="small" type="primary">选择APK文件</el-button>
            <div slot="tip" class="el-upload__tip">只能上传APK文件，且不超过100MB</div>
          </el-upload>
        </el-form-item>

        <el-form-item label="版本名称" prop="versionName">
          <el-input v-model="temp.versionName" placeholder="如: 1.2.0" />
        </el-form-item>

        <el-form-item label="更新类型" prop="updateType">
          <el-radio-group v-model="temp.updateType">
            <el-radio :label="false">可选更新</el-radio>
            <el-radio :label="true">强制更新</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="最低支持版本">
          <el-input v-model="temp.minSupportVersion" placeholder="如: 1.0.0" />
        </el-form-item>

        <el-form-item label="是否激活">
          <el-switch v-model="temp.isActive" />
        </el-form-item>

        <el-form-item label="更新内容">
          <el-input
            v-model="temp.updateContent"
            :autosize="{ minRows: 3, maxRows: 6}"
            type="textarea"
            placeholder="请输入更新内容描述"
          />
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">
          取消
        </el-button>
        <el-button type="primary" @click="dialogStatus==='create'?createData():updateData()">
          确定
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  fetchVersionList,
  uploadApkAndCreateVersion,
  updateVersion,
  toggleVersionStatus,
  deleteVersion,
  validateApkFile
} from '@/api/appUpdate.js'
import waves from '@/directive/waves'
import { parseTime } from '@/utils'

export default {
  name: 'AppUpdateManagement',
  directives: { waves },
  filters: {
    parseTime
  },
  data() {
    return {
      tableKey: 0,
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        platform: 'android'
      },
      temp: {
        id: undefined,
        versionName: '',
        updateType: false,
        updateContent: '',
        minSupportVersion: '',
        targetPlatform: 'android',
        isActive: true,
        file: null
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑版本',
        create: '上传新版本'
      },
      rules: {
        versionName: [{ required: true, message: '版本名称不能为空', trigger: 'blur' }],
        updateType: [{ required: true, message: '请选择更新类型', trigger: 'change' }],
        file: [{ required: true, message: '请选择APK文件', trigger: 'change' }]
      },
      uploadAction: ''
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      fetchVersionList(this.listQuery).then(response => {
        this.list = response.data
        this.listLoading = false
      })
    },
    handleFilter() {
      this.getList()
    },
    resetTemp() {
      this.temp = {
        id: undefined,
        versionName: '',
        updateType: false,
        updateContent: '',
        minSupportVersion: '',
        targetPlatform: 'android',
        isActive: true,
        file: null
      }
    },
    handleCreate() {
      this.resetTemp()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    handleUpdate(row) {
      this.temp = Object.assign({}, row)
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    handleFileChange(file, fileList) {
      this.temp.file = file.raw
      // 验证文件
      const formData = new FormData()
      formData.append('file', file.raw)
      validateApkFile(formData).then(response => {
        if (response.code !== 200) {
          this.$message.error(response.message)
          this.$refs.upload.clearFiles()
          this.temp.file = null
        }
      })
    },
    handleFileRemove() {
      this.temp.file = null
    },
    beforeUpload(file) {
      return false // 阻止自动上传
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          if (!this.temp.file) {
            this.$message.error('请选择APK文件')
            return
          }

          const formData = new FormData()
          formData.append('file', this.temp.file)
          formData.append('versionName', this.temp.versionName)
          formData.append('updateType', this.temp.updateType)
          formData.append('updateContent', this.temp.updateContent || '')
          formData.append('minSupportVersion', this.temp.minSupportVersion || '')
          formData.append('targetPlatform', this.temp.targetPlatform)
          formData.append('isActive', this.temp.isActive)

          uploadApkAndCreateVersion(formData).then(() => {
            this.dialogFormVisible = false
            this.$notify({
              title: '成功',
              message: '版本创建成功',
              type: 'success',
              duration: 2000
            })
            this.getList()
          })
        }
      })
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const tempData = Object.assign({}, this.temp)
          delete tempData.file
          updateVersion(tempData.id, tempData).then(() => {
            this.dialogFormVisible = false
            this.$notify({
              title: '成功',
              message: '版本更新成功',
              type: 'success',
              duration: 2000
            })
            this.getList()
          })
        }
      })
    },
    handleToggleStatus(row) {
      const action = row.isActive ? '停用' : '激活'
      this.$confirm(`确定要${action}版本 ${row.versionName} 吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        toggleVersionStatus(row.id, !row.isActive).then(() => {
          this.$message.success(`${action}成功`)
          this.getList()
        })
      })
    },
    handleDelete(row) {
      this.$confirm(`确定要删除版本 ${row.versionName} 吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteVersion(row.id).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      })
    },
    formatFileSize(bytes) {
      if (bytes === 0) return '0 B'
      const k = 1024
      const sizes = ['B', 'KB', 'MB', 'GB']
      const i = Math.floor(Math.log(bytes) / Math.log(k))
      return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
    }
  }
}
</script>

<style scoped>
.link-type {
  color: #337ab7;
  cursor: pointer;
}

.link-type:hover {
  color: rgb(32, 160, 255);
}
</style>
