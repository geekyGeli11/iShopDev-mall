<template>
  <div class="app-container">
    <el-card shadow="never">
      <div slot="header">
        <span>组件库管理</span>
        <el-button style="float:right" type="primary" size="small" @click="openDialog()">新建组件</el-button>
      </div>
      <el-table :data="list" stripe border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="type" label="类型" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="icon" label="预览图">
          <template slot-scope="scope">
            <img v-if="scope.row.icon" :src="scope.row.icon" style="width:40px;height:40px" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template slot-scope="scope">
            <el-button type="text" @click="openDialog(scope.row)">编辑</el-button>
            <el-button type="text" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px">
      <el-form :model="form" label-width="100px" ref="form">
        <el-form-item label="类型" prop="type" :rules="[{required:true,message:'请输入'}]">
          <el-input v-model="form.type" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="名称" prop="name" :rules="[{required:true,message:'请输入'}]">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="预览图">
          <el-upload
            :http-request="customUpload"
            :show-file-list="false">
            <img v-if="form.icon" :src="form.icon" class="avatar" />
            <i v-else class="el-icon-plus avatar-uploader-icon" />
          </el-upload>
        </el-form-item>
        <el-form-item label="JSON-Schema">
          <el-input type="textarea" v-model="form.configSchema" rows="10" placeholder="{}" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { fetchComponentList, createComponent, updateComponent, deleteComponent } from '@/api/decorate'
import { upload as ossUpload } from '@/api/oss'

export default {
  name: 'DecorateComponentList',
  data() {
    return {
      list: [],
      dialogVisible: false,
      dialogTitle: '新建组件',
      form: { id: null, type: '', name: '', icon: '', configSchema: '{}' },
      isEdit: false
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      fetchComponentList().then(res => {
        if (res.code === 200) this.list = res.data || []
      })
    },
    openDialog(row) {
      this.dialogVisible = true
      if (row) {
        this.dialogTitle = '编辑组件'
        this.form = { ...row, configSchema: row.configSchema || '{}' }
        this.isEdit = true
      } else {
        this.dialogTitle = '新建组件'
        this.form = { id: null, type: '', name: '', icon: '', configSchema: '{}' }
        this.isEdit = false
      }
    },
    customUpload(param) {
      const formData = new FormData();
      formData.append('file', param.file);
      ossUpload(formData).then(res => {
        if (res.code === 200) {
          // OSS接口现在返回简单的文件URL，和COS接口格式一致
          this.form.icon = res.data;
          this.$message.success('上传成功');
        } else {
          this.$message.error('上传失败');
        }
      });
    },
    submit() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const fn = this.isEdit ? updateComponent : createComponent
        fn(this.isEdit ? this.form.id : undefined, this.form).then(res => {
          if (res.code === 200) {
            this.$message.success('保存成功')
            this.dialogVisible = false
            this.load()
          }
        })
      })
    },
    handleDelete(row) {
      this.$confirm('确定删除该组件吗?','提示').then(()=>{
        deleteComponent(row.id).then(res=>{
          if(res.code===200){
            this.$message.success('删除成功');
            this.load();
          }
        })
      })
    }
  }
}
</script>

<style scoped>
.avatar {
  width: 80px;
  height: 80px;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}
</style> 