<template>
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
      <div>
        <i class="el-icon-search"></i>
        <span>筛选搜索</span>
        <el-button
          style="float:right"
          type="primary"
          @click="handleSearchList()"
          size="small">
          查询搜索
        </el-button>
        <el-button
          style="float:right;margin-right: 15px"
          @click="handleResetSearch()"
          size="small">
          重置
        </el-button>
      </div>
      <div style="margin-top: 15px">
        <el-form :inline="true" :model="listQuery" size="small" label-width="140px">
          <el-form-item label="用户名：">
            <el-input v-model="listQuery.memberUsername" class="input-width" placeholder="用户名"></el-input>
          </el-form-item>
          <el-form-item label="签到日期：">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              align="right"
              unlink-panels
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              :picker-options="pickerOptions"
              value-format="yyyy-MM-dd"
              class="input-width">
            </el-date-picker>
          </el-form-item>
          <el-form-item label="签到月份：">
            <el-select v-model="listQuery.signinMonth" placeholder="全部" clearable class="input-width">
              <el-option 
                v-for="month in monthOptions" 
                :key="month.value" 
                :label="month.label" 
                :value="month.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    
    <el-card class="operate-container" shadow="never">
      <i class="el-icon-tickets"></i>
      <span>签到记录</span>
      <el-button 
        size="mini" 
        class="btn-add" 
        @click="handleExport"
        :loading="exportLoading">
        导出记录
      </el-button>
    </el-card>
    
    <div class="table-container">
      <el-table 
        ref="signinTable"
        :data="list"
        style="width: 100%;"
        v-loading="listLoading" 
        border>
        <el-table-column label="记录ID" width="80" align="center">
          <template slot-scope="scope">{{scope.row.id}}</template>
        </el-table-column>
        <el-table-column label="用户名" width="120" align="center">
          <template slot-scope="scope">{{scope.row.memberUsername}}</template>
        </el-table-column>
        <el-table-column label="签到日期" width="120" align="center">
          <template slot-scope="scope">{{scope.row.signinDate}}</template>
        </el-table-column>
        <el-table-column label="签到时间" width="150" align="center">
          <template slot-scope="scope">{{scope.row.signinTime | formatDateTime}}</template>
        </el-table-column>
        <el-table-column label="获得积分" width="100" align="center">
          <template slot-scope="scope">
            <span class="points-text">+{{scope.row.pointsEarned}}</span>
          </template>
        </el-table-column>
        <el-table-column label="连续天数" width="100" align="center">
          <template slot-scope="scope">{{scope.row.continuousDays}}天</template>
        </el-table-column>
        <el-table-column label="周期天数" width="100" align="center">
          <template slot-scope="scope">{{scope.row.cycleSigninDays}}天</template>
        </el-table-column>
        <el-table-column label="连签奖励" width="100" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.isRewardClaimed === 1" type="success">已领取</el-tag>
            <el-tag v-else type="info">未领取</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="签到月份" width="100" align="center">
          <template slot-scope="scope">{{scope.row.signinMonth}}</template>
        </el-table-column>
        <el-table-column label="客户端IP" width="120" align="center">
          <template slot-scope="scope">{{scope.row.clientIp || '-'}}</template>
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
</template>

<script>
import { getSigninLogs, exportSigninLogs } from '@/api/signin'
import { formatDate } from '@/utils/date'

const defaultListQuery = {
  pageNum: 1,
  pageSize: 10,
  memberUsername: null,
  signinMonth: null,
  startDate: null,
  endDate: null
}

export default {
  name: 'SigninLogs',
  data() {
    return {
      listQuery: Object.assign({}, defaultListQuery),
      list: [],
      total: 0,
      listLoading: false,
      exportLoading: false,
      dateRange: [],
      pickerOptions: {
        shortcuts: [{
          text: '最近一周',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '最近一个月',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '最近三个月',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
            picker.$emit('pick', [start, end])
          }
        }]
      }
    }
  },
  computed: {
    monthOptions() {
      const options = []
      const currentDate = new Date()
      const currentYear = currentDate.getFullYear()
      const currentMonth = currentDate.getMonth() + 1
      
      // 生成近12个月的选项
      for (let i = 0; i < 12; i++) {
        let year = currentYear
        let month = currentMonth - i
        
        if (month <= 0) {
          year = currentYear - 1
          month = 12 + month
        }
        
        const value = `${year}-${month.toString().padStart(2, '0')}`
        const label = `${year}年${month}月`
        options.push({ value, label })
      }
      
      return options
    }
  },
  created() {
    this.getList()
  },
  filters: {
    formatDateTime(time) {
      if (time == null || time === '') {
        return '-'
      }
      let date = new Date(time)
      return formatDate(date, 'yyyy-MM-dd hh:mm:ss')
    }
  },
  methods: {
    handleResetSearch() {
      this.listQuery = Object.assign({}, defaultListQuery)
      this.dateRange = []
    },
    handleSearchList() {
      this.listQuery.pageNum = 1
      if (this.dateRange && this.dateRange.length === 2) {
        this.listQuery.startDate = this.dateRange[0]
        this.listQuery.endDate = this.dateRange[1]
      } else {
        this.listQuery.startDate = null
        this.listQuery.endDate = null
      }
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
    async getList() {
      this.listLoading = true
      try {
        const result = await getSigninLogs(this.listQuery)
        if (result.code === 200) {
          this.list = result.data.list || []
          this.total = result.data.total || 0
        } else {
          this.$message.error(result.message || '获取签到记录失败')
        }
      } catch (error) {
        this.$message.error('获取签到记录失败')
      } finally {
        this.listLoading = false
      }
    },
    async handleExport() {
      this.exportLoading = true
      try {
        const params = Object.assign({}, this.listQuery)
        if (this.dateRange && this.dateRange.length === 2) {
          params.startDate = this.dateRange[0]
          params.endDate = this.dateRange[1]
        }
        
        const result = await exportSigninLogs(params)
        if (result.code === 200) {
          this.$message.success('导出成功')
          // TODO: 实现文件下载逻辑
        } else {
          this.$message.error(result.message || '导出失败')
        }
      } catch (error) {
        this.$message.error('导出失败')
      } finally {
        this.exportLoading = false
      }
    }
  }
}
</script>

<style scoped>
.input-width {
  width: 200px;
}

.points-text {
  color: #67C23A;
  font-weight: bold;
}

.table-container {
  margin-top: 15px;
}
</style> 