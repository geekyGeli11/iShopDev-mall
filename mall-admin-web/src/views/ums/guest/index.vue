<template>
  <div class="app-container">
    <div class="filter-container">
      <el-card class="filter-card" shadow="never">
        <div slot="header" class="clearfix">
          <span>筛选搜索</span>
        </div>
        <el-form :inline="true" :model="listQuery" size="small" label-width="140px">
          <el-form-item label="关键字：">
            <el-input v-model="listQuery.keyword" class="input-width" placeholder="游客ID或设备ID" clearable></el-input>
          </el-form-item>
          <el-form-item label="设备类型：">
            <el-select v-model="listQuery.deviceType" placeholder="全部" clearable class="input-width">
              <el-option label="自助结算机" value="selfcheck"></el-option>
              <el-option label="移动设备" value="mobile"></el-option>
              <el-option label="其他" value="other"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="状态：">
            <el-select v-model="listQuery.status" placeholder="全部" clearable class="input-width">
              <el-option label="正常" :value="1"></el-option>
              <el-option label="禁用" :value="0"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="所属学校：">
            <el-select v-model="listQuery.schoolId" placeholder="全部" clearable class="input-width">
              <el-option 
                v-for="item in schoolOptions"
                :key="item.id"
                :label="item.schoolName"
                :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="创建时间：">
            <el-date-picker
              class="input-width"
              v-model="dateRange"
              value-format="yyyy-MM-dd"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期">
            </el-date-picker>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearchList()" size="small">
              查询搜索
            </el-button>
            <el-button @click="handleResetSearch()" size="small">
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
    <div class="table-container">
      <el-card class="operate-container" shadow="never">
        <i class="el-icon-tickets" style="margin-top: 5px"></i>
        <span style="margin-top: 5px">数据列表</span>
        <el-button
          style="float: right"
          icon="el-icon-delete"
          type="danger"
          size="mini"
          @click="handleCleanExpired">
          清理过期游客
        </el-button>
      </el-card>
      <div class="table-wrapper">
        <el-table ref="guestTable"
                  :data="list"
                  style="width: 100%;"
                  v-loading="listLoading" border>
          <el-table-column label="游客ID" width="180" align="center">
            <template slot-scope="scope">{{ scope.row.guestId }}</template>
          </el-table-column>
          <el-table-column label="设备ID" width="150" align="center">
            <template slot-scope="scope">{{ scope.row.deviceId || '-' }}</template>
          </el-table-column>
          <el-table-column label="设备类型" width="100" align="center">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.deviceType === 'selfcheck'" type="primary">自助结算机</el-tag>
              <el-tag v-else-if="scope.row.deviceType === 'mobile'" type="success">移动设备</el-tag>
              <el-tag v-else type="info">其他</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="登录IP" width="120" align="center">
            <template slot-scope="scope">{{ scope.row.loginIp || '-' }}</template>
          </el-table-column>
          <el-table-column label="所属学校" width="120" align="center">
            <template slot-scope="scope">{{ scope.row.schoolName || '未绑定' }}</template>
          </el-table-column>
          <el-table-column label="状态" width="80" align="center">
            <template slot-scope="scope">
              <el-switch
                @change="handleStatusChange(scope.$index, scope.row)"
                :active-value="1"
                :inactive-value="0"
                v-model="scope.row.status">
              </el-switch>
            </template>
          </el-table-column>
          <el-table-column label="订单数" width="80" align="center">
            <template slot-scope="scope">{{ scope.row.orderCount || 0 }}</template>
          </el-table-column>
          <el-table-column label="消费金额" width="100" align="center">
            <template slot-scope="scope">￥{{ scope.row.orderAmount || 0 }}</template>
          </el-table-column>
          <el-table-column label="会话时长" width="100" align="center">
            <template slot-scope="scope">{{ scope.row.sessionDuration || 0 }}分钟</template>
          </el-table-column>
          <el-table-column label="创建时间" width="160" align="center">
            <template slot-scope="scope">{{ scope.row.createTime | formatDateTime }}</template>
          </el-table-column>
          <el-table-column label="最后活跃" width="160" align="center">
            <template slot-scope="scope">{{ scope.row.lastActiveTime | formatDateTime }}</template>
          </el-table-column>
          <el-table-column label="操作" width="120" align="center">
            <template slot-scope="scope">
              <el-button
                size="mini"
                type="danger"
                @click="handleDelete(scope.$index, scope.row)">删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="pagination-container">
        <el-pagination
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          layout="total, sizes,prev, pager, next,jumper"
          :current-page.sync="listQuery.pageNum"
          :page-size="listQuery.pageSize"
          :page-sizes="[10,15,20]"
          :total="total">
        </el-pagination>
      </div>
    </div>
  </div>
</template>

<script>
import { fetchList, updateStatus, deleteGuest, cleanExpiredGuests, getSchoolList } from '@/api/guest'
import { formatDate } from '@/utils/date'

const defaultListQuery = {
  pageNum: 1,
  pageSize: 20,
  keyword: null,
  deviceType: null,
  status: null,
  schoolId: null,
  startTime: null,
  endTime: null
}

export default {
  name: 'GuestList',
  data() {
    return {
      listQuery: Object.assign({}, defaultListQuery),
      list: [],
      total: 0,
      listLoading: false,
      dateRange: null,
      schoolOptions: []
    }
  },
  created() {
    this.getList()
    this.getSchoolList()
  },
  watch: {
    dateRange(newVal) {
      if (newVal && newVal.length === 2) {
        this.listQuery.startTime = newVal[0]
        this.listQuery.endTime = newVal[1]
      } else {
        this.listQuery.startTime = null
        this.listQuery.endTime = null
      }
    }
  },
  methods: {
    getList() {
      this.listLoading = true
      fetchList(this.listQuery).then(response => {
        this.listLoading = false
        this.list = response.data.list
        this.total = response.data.total
      })
    },
    getSchoolList() {
      getSchoolList().then(response => {
        this.schoolOptions = response.data || []
      }).catch(error => {
        console.error('获取学校列表失败:', error)
      })
    },
    handleSearchList() {
      this.listQuery.pageNum = 1
      this.getList()
    },
    handleResetSearch() {
      this.listQuery = Object.assign({}, defaultListQuery)
      this.dateRange = null
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
    handleStatusChange(index, row) {
      this.$confirm('是否要修改该游客的状态?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        updateStatus(row.guestId, row.status).then(response => {
          this.$message({
            type: 'success',
            message: '修改成功!'
          })
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消修改'
        })
        this.getList()
      })
    },
    handleDelete(index, row) {
      this.$confirm('是否要删除该游客?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteGuest(row.guestId).then(response => {
          this.$message({
            type: 'success',
            message: '删除成功!'
          })
          this.getList()
        })
      })
    },
    handleCleanExpired() {
      this.$confirm('是否要清理7天前的过期游客数据?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        cleanExpiredGuests(7).then(response => {
          this.$message({
            type: 'success',
            message: response.message
          })
          this.getList()
        })
      })
    }
  }
}
</script>

<style scoped>
.input-width {
  width: 203px;
}
</style>
