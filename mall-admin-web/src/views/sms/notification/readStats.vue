<template>
  <div class="app-container">
    <el-card class="operate-container" shadow="never">
      <div class="notification-detail">
        <h3>{{notification.title}}</h3>
        <div class="notification-meta">
          <span>创建时间：{{notification.createTime | formatTime}}</span>
          <span class="ml20">总阅读数：{{notification.readCount}}</span>
        </div>
      </div>
    </el-card>
    <el-card class="table-container" shadow="never">
      <div style="margin-top: 15px">
        <el-table
          :data="list"
          style="width: 100%"
          border
          v-loading="listLoading">
          <el-table-column
            label="用户ID"
            align="center"
            width="100">
            <template slot-scope="scope">{{scope.row.userId}}</template>
          </el-table-column>
          <el-table-column
            label="用户名"
            align="center">
            <template slot-scope="scope">{{scope.row.username}}</template>
          </el-table-column>
          <el-table-column
            label="阅读时间"
            align="center"
            width="180">
            <template slot-scope="scope">{{scope.row.readTime | formatTime}}</template>
          </el-table-column>
        </el-table>
      </div>
      <div class="pagination-container">
        <el-pagination
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          layout="total, sizes,prev, pager, next,jumper"
          :page-size="listQuery.pageSize"
          :page-sizes="[5,10,15]"
          :current-page.sync="listQuery.pageNum"
          :total="total">
        </el-pagination>
      </div>
    </el-card>
  </div>
</template>
<script>
  import {getNotification, fetchReadRecords} from '@/api/notification';
  import {formatDate} from '@/utils/date';

  export default {
    name: 'notificationReadStats',
    data() {
      return {
        notification: {
          id: null,
          title: '',
          createTime: '',
          readCount: 0
        },
        listQuery: {
          pageNum: 1,
          pageSize: 10
        },
        list: [],
        total: 0,
        listLoading: false,
        id: null
      }
    },
    created() {
      if (this.$route.query.id != null) {
        this.id = this.$route.query.id;
        this.getNotificationInfo();
        this.getReadStatsList();
      }
    },
    filters: {
      formatTime(time) {
        if (time == null || time === '') {
          return 'N/A';
        }
        let date = new Date(time);
        return formatDate(date, 'yyyy-MM-dd hh:mm:ss')
      }
    },
    methods: {
      handleSizeChange(val) {
        this.listQuery.pageNum = 1;
        this.listQuery.pageSize = val;
        this.getReadStatsList();
      },
      handleCurrentChange(val) {
        this.listQuery.pageNum = val;
        this.getReadStatsList();
      },
      getNotificationInfo() {
        getNotification(this.id).then(response => {
          this.notification = response.data;
        });
      },
      getReadStatsList() {
        this.listLoading = true;
        fetchReadRecords(this.id, this.listQuery).then(response => {
          this.listLoading = false;
          this.list = response.data.list;
          this.total = response.data.total;
        });
      }
    }
  }
</script>
<style scoped>
  .notification-detail {
    padding: 20px;
  }
  .notification-meta {
    color: #606266;
    margin-top: 10px;
  }
  .ml20 {
    margin-left: 20px;
  }
</style> 