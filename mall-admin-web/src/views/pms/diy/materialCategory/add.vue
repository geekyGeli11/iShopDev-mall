<template>
  <div class="app-container">
    <!-- 面包屑导航 -->
    <el-breadcrumb class="app-breadcrumb" separator="/">
      <el-breadcrumb-item>
        <router-link to="/home">首页</router-link>
      </el-breadcrumb-item>
      <el-breadcrumb-item>DIY管理</el-breadcrumb-item>
      <el-breadcrumb-item>
        <router-link to="/diy/materialCategory">DIY素材分类管理</router-link>
      </el-breadcrumb-item>
      <el-breadcrumb-item>添加DIY素材分类</el-breadcrumb-item>
    </el-breadcrumb>

    <!-- 表单区域 -->
    <el-card class="form-container" shadow="never">
      <el-form
        :model="materialCategory"
        :rules="rules"
        ref="materialCategoryForm"
        label-width="150px"
        size="small">
        
        <el-form-item label="分类名称：" prop="name">
          <el-input
            v-model="materialCategory.name"
            class="input-width"
            placeholder="请输入分类名称">
          </el-input>
        </el-form-item>

        <el-form-item label="分类类型：" prop="type">
          <el-radio-group v-model="materialCategory.type">
            <el-radio :label="1">图片素材</el-radio>
            <el-radio :label="2">文字字体</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="分类描述：" prop="description">
          <el-input
            v-model="materialCategory.description"
            type="textarea"
            :rows="5"
            class="input-width"
            placeholder="请输入分类描述">
          </el-input>
        </el-form-item>

        <el-form-item label="排序：" prop="sort">
          <el-input-number
            v-model="materialCategory.sort"
            :min="0"
            :max="1000"
            class="input-width">
          </el-input-number>
        </el-form-item>

        <el-form-item label="是否启用：" prop="status">
          <el-radio-group v-model="materialCategory.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            @click="onSubmit('materialCategoryForm')"
            :loading="submitLoading">
            提交
          </el-button>
          <el-button
            @click="resetForm('materialCategoryForm')">
            重置
          </el-button>
          <el-button
            @click="goBack()">
            返回
          </el-button>
        </el-form-item>

      </el-form>
    </el-card>
  </div>
</template>

<script>
import { createMaterialCategory } from '@/api/diyMaterial'

export default {
  name: 'AddDiyMaterialCategory',
  data() {
    return {
      materialCategory: {
        name: '',
        type: 1,
        description: '',
        sort: 0,
        status: 1
      },
      rules: {
        name: [
          { required: true, message: '请输入分类名称', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '请选择分类类型', trigger: 'change' }
        ],
        description: [
          { max: 200, message: '长度不能超过 200 个字符', trigger: 'blur' }
        ],
        sort: [
          { required: true, message: '请输入排序值', trigger: 'blur' },
          { type: 'number', message: '排序值必须为数字', trigger: 'blur' }
        ],
        status: [
          { required: true, message: '请选择状态', trigger: 'change' }
        ]
      },
      submitLoading: false
    }
  },
  methods: {
    onSubmit(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.submitLoading = true
          createMaterialCategory(this.materialCategory).then(response => {
            this.submitLoading = false
            this.$message({
              message: '添加成功！',
              type: 'success',
              duration: 1000
            })
            this.$router.back()
          }).catch(() => {
            this.submitLoading = false
            this.$message({
              message: '添加失败！',
              type: 'error',
              duration: 1000
            })
          })
        } else {
          this.$message({
            message: '验证失败',
            type: 'error',
            duration: 1000
          })
          return false
        }
      })
    },
    resetForm(formName) {
      this.$refs[formName].resetFields()
      this.materialCategory = {
        name: '',
        type: 1,
        description: '',
        sort: 0,
        status: 1
      }
    },
    goBack() {
      this.$router.back()
    }
  }
}
</script>

<style scoped>
.input-width {
  width: 400px;
}
</style>
