<template>
  <div class="app-container">
    <!-- 筛选搜索区域 -->
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
        <el-form :inline="true" :model="listQuery" size="small" label-width="120px">
          <el-form-item label="关键字搜索：">
            <el-input 
              v-model="listQuery.keyword" 
              class="input-width" 
              placeholder="昵称/手机号" 
              clearable>
            </el-input>
          </el-form-item>
          <el-form-item label="分销商状态：">
            <el-select v-model="listQuery.distributorStatus" placeholder="全部" clearable class="input-width">
              <el-option label="普通用户" :value="0"></el-option>
              <el-option label="分销商" :value="1"></el-option>
              <el-option label="暂停" :value="2"></el-option>
              <el-option label="禁用" :value="3"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="分销商等级：">
            <el-select v-model="listQuery.distributorLevel" placeholder="全部" clearable class="input-width">
              <el-option label="普通" :value="0"></el-option>
              <el-option label="初级" :value="1"></el-option>
              <el-option label="中级" :value="2"></el-option>
              <el-option label="高级" :value="3"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="注册时间：">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="yyyy-MM-dd"
              class="input-width">
            </el-date-picker>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 操作区域 -->
    <el-card class="operate-container" shadow="never">
      <i class="el-icon-tickets"></i>
      <span>分销商列表</span>
      <el-button 
        size="mini" 
        class="btn-add" 
        @click="handleBatchStatus(1)" 
        style="margin-left: 20px"
        :disabled="multipleSelection.length === 0">
        批量启用
      </el-button>
      <el-button 
        size="mini" 
        class="btn-add" 
        @click="handleBatchStatus(3)"
        :disabled="multipleSelection.length === 0">
        批量禁用
      </el-button>
      <el-button 
        size="mini" 
        class="btn-add" 
        @click="handleRefreshStats()">
        刷新统计
      </el-button>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="statistic-card">
            <div class="statistic-value">{{ statistics.totalCount || 0 }}</div>
            <div class="statistic-label">总分销商数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="statistic-card">
            <div class="statistic-value">{{ statistics.activeCount || 0 }}</div>
            <div class="statistic-label">活跃分销商</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="statistic-card">
            <div class="statistic-value">{{ statistics.totalCommission || '0.00' }}</div>
            <div class="statistic-label">累计佣金</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="statistic-card">
            <div class="statistic-value">{{ statistics.totalCustomers || 0 }}</div>
            <div class="statistic-label">推广客户数</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数据列表 -->
    <div class="table-container">
      <el-table 
        ref="distributorTable"
        :data="list"
        style="width: 100%;"
        v-loading="listLoading" 
        border
        @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column label="用户ID" width="80" align="center">
          <template slot-scope="scope">{{ scope.row.id }}</template>
        </el-table-column>
        <el-table-column label="头像" width="80" align="center">
          <template slot-scope="scope">
            <img v-if="scope.row.icon" :src="scope.row.icon" style="height: 40px;width: 40px;border-radius: 50%">
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="昵称" width="120" align="center">
          <template slot-scope="scope">{{ scope.row.nickname || '未设置' }}</template>
        </el-table-column>
        <el-table-column label="手机号" width="130" align="center">
          <template slot-scope="scope">{{ scope.row.phone | formatPhone }}</template>
        </el-table-column>
        <el-table-column label="分销商等级" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getLevelTagType(scope.row.distributorLevel)" size="small">
              {{ getLevelText(scope.row.distributorLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="一级客户数" width="100" align="center">
          <template slot-scope="scope">{{ scope.row.level1CustomerCount || 0 }}</template>
        </el-table-column>
        <el-table-column label="二级客户数" width="100" align="center">
          <template slot-scope="scope">{{ scope.row.level2CustomerCount || 0 }}</template>
        </el-table-column>
        <el-table-column label="累计佣金" width="120" align="center">
          <template slot-scope="scope">￥{{ scope.row.totalCommission || '0.00' }}</template>
        </el-table-column>
        <el-table-column label="审核通过时间" width="160" align="center">
          <template slot-scope="scope">{{ scope.row.approvedTime | formatDateTime }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag 
              :type="getStatusTagType(scope.row.isDistributor)"
              size="small">
              {{ getStatusText(scope.row.isDistributor) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button 
              size="mini"
              type="text"
              @click="handleViewDetail(scope.row)">
              查看详情
            </el-button>
            <el-dropdown @command="handleAction" trigger="click">
              <el-button size="mini" type="text">
                更多<i class="el-icon-arrow-down el-icon--right"></i>
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item :command="{action: 'updateStatus', row: scope.row}">
                  {{ scope.row.isDistributor === 1 ? '暂停' : '启用' }}
                </el-dropdown-item>
                <el-dropdown-item :command="{action: 'updateLevel', row: scope.row}">
                  调整等级
                </el-dropdown-item>
                <el-dropdown-item :command="{action: 'viewCommission', row: scope.row}">
                  佣金明细
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
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
        :page-sizes="[10,20,50,100]"
        :total="total">
      </el-pagination>
    </div>

    <!-- 详情弹窗 -->
    <el-dialog
      title="分销商详情"
      :visible.sync="detailDialogVisible"
      width="800px">
      <el-form :model="currentDistributor" label-width="120px" size="small">
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户昵称：">
              <span>{{ currentDistributor.nickname || '未设置' }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号：">
              <span>{{ currentDistributor.phone || '-' }}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="分销商等级：">
              <el-tag :type="getLevelTagType(currentDistributor.distributorLevel)" size="small">
                {{ getLevelText(currentDistributor.distributorLevel) }}
              </el-tag>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态：">
              <el-tag :type="getStatusTagType(currentDistributor.isDistributor)" size="small">
                {{ getStatusText(currentDistributor.isDistributor) }}
              </el-tag>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="一级客户数：">
              <span>{{ currentDistributor.level1CustomerCount || 0 }}个</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="二级客户数：">
              <span>{{ currentDistributor.level2CustomerCount || 0 }}个</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="累计佣金：">
              <span style="color: #67C23A; font-weight: bold;">￥{{ currentDistributor.totalCommission || '0.00' }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="推广订单金额：">
              <span>￥{{ currentDistributor.totalOrderAmount || '0.00' }}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="申请时间：">
              <span>{{ currentDistributor.applyTime | formatDateTime }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="审核通过时间：">
              <span>{{ currentDistributor.approvedTime | formatDateTime }}</span>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false" size="small">关 闭</el-button>
      </span>
    </el-dialog>

    <!-- 等级调整弹窗 -->
    <el-dialog
      title="调整分销商等级"
      :visible.sync="levelDialogVisible"
      width="500px">
      <el-form :model="levelForm" label-width="120px" size="small">
        <el-form-item label="分销商：">
          <span>{{ currentDistributor.nickname || '未设置' }}</span>
        </el-form-item>
        <el-form-item label="当前等级：">
          <el-tag :type="getLevelTagType(currentDistributor.distributorLevel)" size="small">
            {{ getLevelText(currentDistributor.distributorLevel) }}
          </el-tag>
        </el-form-item>
        <el-form-item label="新等级：">
          <el-select v-model="levelForm.level" placeholder="请选择等级" style="width: 200px">
            <el-option label="普通" :value="0"></el-option>
            <el-option label="初级" :value="1"></el-option>
            <el-option label="中级" :value="2"></el-option>
            <el-option label="高级" :value="3"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="levelDialogVisible = false" size="small">取 消</el-button>
        <el-button type="primary" @click="handleLevelConfirm()" size="small">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { 
  fetchList, 
  getDetail, 
  updateStatus, 
  updateLevel, 
  enableDistributor, 
  disableDistributor, 
  pauseDistributor, 
  batchUpdateStatus, 
  getStatistics 
} from '@/api/distributor'
import { formatDate } from '@/utils/date'

const defaultListQuery = {
  pageNum: 1,
  pageSize: 20,
  keyword: null,
  distributorStatus: null,
  distributorLevel: null,
  startTime: null,
  endTime: null
}

const defaultLevelForm = {
  level: 0
}

export default {
  name: 'distributorList',
  data() {
    return {
      listQuery: Object.assign({}, defaultListQuery),
      list: null,
      total: null,
      listLoading: false,
      dateRange: null,
      multipleSelection: [],
      statistics: {},
      
      // 详情弹窗
      detailDialogVisible: false,
      currentDistributor: {},
      
      // 等级调整弹窗
      levelDialogVisible: false,
      levelForm: Object.assign({}, defaultLevelForm)
    }
  },
  created() {
    this.getList()
    this.getStatisticsData()
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
  filters: {
    formatDateTime(time) {
      if (time == null || time === '') {
        return '-'
      }
      let date = new Date(time)
      return formatDate(date, 'yyyy-MM-dd hh:mm:ss')
    },
    formatPhone(phone) {
      if (!phone) return '-'
      return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
    }
  },
  methods: {
    handleResetSearch() {
      this.listQuery = Object.assign({}, defaultListQuery)
      this.dateRange = null
    },
    handleSearchList() {
      this.listQuery.pageNum = 1
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
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    handleViewDetail(row) {
      this.currentDistributor = row
      this.detailDialogVisible = true
    },
    handleAction(command) {
      const { action, row } = command
      this.currentDistributor = row
      
      if (action === 'updateStatus') {
        this.handleToggleStatus(row)
      } else if (action === 'updateLevel') {
        this.levelForm = Object.assign({}, defaultLevelForm)
        this.levelForm.level = row.distributorLevel
        this.levelDialogVisible = true
      } else if (action === 'viewCommission') {
        this.$message.info('佣金明细功能开发中')
      }
    },
    handleToggleStatus(row) {
      const newStatus = row.isDistributor === 1 ? 2 : 1
      const statusText = newStatus === 1 ? '启用' : '暂停'
      
      this.$confirm(`确定要${statusText}该分销商吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        updateStatus(row.id, newStatus, `${statusText}操作`).then(response => {
          this.$message.success(`${statusText}成功`)
          this.getList()
        })
      })
    },
    handleBatchStatus(status) {
      if (this.multipleSelection.length === 0) {
        this.$message.warning('请选择要操作的分销商')
        return
      }
      
      const statusText = status === 1 ? '启用' : '禁用'
      this.$confirm(`确定要批量${statusText}选中的分销商吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const userIds = this.multipleSelection.map(item => item.id)
        batchUpdateStatus(userIds, status, `批量${statusText}操作`).then(response => {
          this.$message.success(`批量${statusText}成功`)
          this.getList()
        })
      })
    },
    handleLevelConfirm() {
      updateLevel(this.currentDistributor.id, this.levelForm.level, '管理员调整等级').then(response => {
        this.$message.success('等级调整成功')
        this.levelDialogVisible = false
        this.getList()
      })
    },
    handleRefreshStats() {
      this.getStatisticsData()
      this.$message.success('统计数据已刷新')
    },
    getList() {
      this.listLoading = true
      fetchList(this.listQuery).then(response => {
        this.list = response.data.list
        this.total = parseInt(response.data.total)
        this.listLoading = false
      })
    },
    getStatisticsData() {
      getStatistics().then(response => {
        this.statistics = response.data
      })
    },
    getStatusText(status) {
      const statusMap = {
        0: '普通用户',
        1: '分销商',
        2: '暂停',
        3: '禁用'
      }
      return statusMap[status] || '未知'
    },
    getStatusTagType(status) {
      const typeMap = {
        0: 'info',
        1: 'success',
        2: 'warning',
        3: 'danger'
      }
      return typeMap[status] || 'info'
    },
    getLevelText(level) {
      const levelMap = {
        0: '普通',
        1: '初级',
        2: '中级',
        3: '高级'
      }
      return levelMap[level] || '普通'
    },
    getLevelTagType(level) {
      const typeMap = {
        0: 'info',
        1: 'success',
        2: 'warning',
        3: 'danger'
      }
      return typeMap[level] || 'info'
    }
  }
}
</script>

<style scoped>
.input-width {
  width: 200px;
}

.statistic-card {
  text-align: center;
  padding: 10px;
}

.statistic-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 5px;
}

.statistic-label {
  font-size: 14px;
  color: #666;
}
</style> 