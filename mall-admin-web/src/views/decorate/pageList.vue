<template>
  <div class="app-container">
    <el-card shadow="never">
      <div slot="header">
        <span>装修页面管理</span>
        <el-button type="primary" size="small" style="float:right" @click="handleCreate">新建页面</el-button>
      </div>
      <el-form :inline="true" :model="queryParams" class="filter-form" size="small">
        <el-form-item label="门店ID">
          <el-input v-model="queryParams.storeId" placeholder="请输入门店ID" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="pageList" stripe border style="width:100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="pageType" label="类型" width="80" />
        <el-table-column prop="latestVersion" label="最新版本" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">{{ scope.row.status === 1 ? '已发布' : '草稿' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="160" />
        <el-table-column label="操作" width="180">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="editPage(scope.row)">编辑</el-button>
            <el-button type="text" size="small" @click="publish(scope.row)" v-if="scope.row.status === 0">发布</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        layout="total, prev, pager, next"
        :total="total"
        :page-size="queryParams.pageSize"
        @current-change="handlePageChange" />
    </el-card>
  </div>
</template>

<script>
import { fetchPageList, createPage, publishPage } from '@/api/decorate';

export default {
  name: 'DecoratePageList',
  data() {
    return {
      pageList: [],
      total: 0,
      queryParams: {
        storeId: '',
        pageNum: 1,
        pageSize: 10
      }
    };
  },
  created() {
    this.fetchData();
  },
  methods: {
    fetchData() {
      if (!this.queryParams.storeId) {
        this.$message.warning('请输入门店ID');
        return;
      }
      fetchPageList(this.queryParams).then(res => {
        if (res.code === 200) {
          this.pageList = res.data.list || [];
          this.total = res.data.total || 0;
        }
      });
    },
    handlePageChange(page) {
      this.queryParams.pageNum = page;
      this.fetchData();
    },
    handleCreate() {
      this.$prompt('输入页面标题', '新建页面', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPlaceholder: '首页',
        inputValidator: val => !!val
      }).then(({ value }) => {
        const data = {
          title: value,
          storeId: Number(this.queryParams.storeId),
          pageType: 1
        };
        createPage(data).then(res => {
          if (res.code === 200) {
            this.$message.success('创建成功');
            this.fetchData();
          }
        });
      }).catch(() => {});
    },
    editPage(row) {
      this.$router.push({ path: '/decorate/editor', query: { pageId: row.id, storeId: row.storeId } });
    },
    publish(row) {
      this.$confirm('确定发布此页面吗？', '提示').then(() => {
        publishPage(row.id).then(res => {
          if (res.code === 200) {
            this.$message.success('发布成功');
            this.fetchData();
          }
        });
      }).catch(() => {});
    }
  }
};
</script> 