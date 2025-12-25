<template>
  <el-card class="form-container" shadow="never">
    <el-form :model="notification"
             :rules="rules"
             ref="notificationForm"
             label-width="150px">
      <el-form-item label="通知标题：" prop="title">
        <el-input v-model="notification.title" class="input-width"></el-input>
      </el-form-item>
      <el-form-item label="通知封面：">
        <single-upload v-model="notification.pic"></single-upload>
      </el-form-item>
      <el-form-item label="通知内容：">
        <tinymce ref="editor" v-model="notification.content"></tinymce>
      </el-form-item>
      <el-form-item label="是否启用：">
        <el-switch
          v-model="notification.status"
          :active-value="1"
          :inactive-value="0">
        </el-switch>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit('notificationForm')">提交</el-button>
        <el-button @click="cancelCurrent()">取消</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>
<script>
  import {createNotification, getNotification, updateNotification} from '@/api/notification';
  import SingleUpload from '@/components/Upload/singleUpload';
  import Tinymce from '@/components/Tinymce';
  
  const defaultNotification = {
    title: '',
    pic: '',
    content: '',
    status: 1
  };
  
  export default {
    name: 'NotificationDetail',
    components: {SingleUpload, Tinymce},
    props: {
      isEdit: {
        type: Boolean,
        default: false
      }
    },
    data() {
      return {
        notification: Object.assign({}, defaultNotification),
        rules: {
          title: [
            {required: true, message: '请输入通知标题', trigger: 'blur'},
            {min: 2, max: 140, message: '长度在 2 到 140 个字符', trigger: 'blur'}
          ]
        }
      }
    },
    created() {
      if (this.isEdit) {
        const id = this.$route.query.id;
        this.getNotificationInfo(id);
      }
    },
    methods: {
      getNotificationInfo(id) {
        getNotification(id).then(response => {
          this.notification = response.data;
        });
      },
      onSubmit(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.$confirm('是否提交数据', '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            }).then(() => {
              if (this.isEdit) {
                const id = this.$route.query.id;
                updateNotification(id, this.notification).then(response => {
                  this.$message({
                    message: '修改成功',
                    type: 'success',
                    duration: 1000
                  });
                  this.$router.back();
                });
              } else {
                createNotification(this.notification).then(response => {
                  this.$refs[formName].resetFields();
                  this.$message({
                    message: '提交成功',
                    type: 'success',
                    duration: 1000
                  });
                  this.$router.back();
                });
              }
            });
          } else {
            this.$message({
              message: '验证失败',
              type: 'error',
              duration: 1000
            });
            return false;
          }
        });
      },
      cancelCurrent() {
        this.$router.back();
      }
    }
  }
</script>
<style scoped>
  .form-container {
    width: 800px;
  }
  .input-width {
    width: 50%;
  }
</style> 