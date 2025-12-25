<template>
  <div class="app-container">
    <!-- 筛选搜索区域 -->
    <el-card class="filter-container" shadow="never">
      <div>
        <i class="el-icon-search"></i>
        <span>筛选搜索</span>
      </div>
      <div style="margin-top: 15px">
        <el-form :inline="true" :model="listQuery" size="small" label-width="140px">
          <el-form-item label="用户搜索：">
            <el-input v-model="listQuery.userId" placeholder="请输入用户ID" clearable style="width: 180px"></el-input>
          </el-form-item>
          <el-form-item label="奖励类型：">
            <el-select v-model="listQuery.commissionType" placeholder="全部类型" clearable style="width: 150px">
              <el-option label="全部" :value="null"></el-option>
              <el-option label="邀请奖励" :value="1"></el-option>
              <el-option label="一级分销佣金" :value="2"></el-option>
              <el-option label="二级分销佣金" :value="3"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="发放状态：">
            <el-select v-model="listQuery.status" placeholder="全部状态" clearable style="width: 150px">
              <el-option label="全部" :value="null"></el-option>
              <el-option label="待发放" :value="0"></el-option>
              <el-option label="已发放" :value="1"></el-option>
              <el-option label="发放失败" :value="2"></el-option>
              <el-option label="已过期" :value="3"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="奖励金额：">
            <el-select v-model="listQuery.rewardType" placeholder="全部奖励" clearable style="width: 150px">
              <el-option label="全部" :value="null"></el-option>
              <el-option label="积分" :value="1"></el-option>
              <el-option label="优惠券" :value="2"></el-option>
              <el-option label="现金红包" :value="3"></el-option>
              <el-option label="商品" :value="4"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="发放时间：">
            <el-date-picker
              v-model="timeRange"
              type="datetimerange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              style="width: 300px"
              @change="handleTimeRangeChange">
            </el-date-picker>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="handleSearchList()">查询</el-button>
            <el-button icon="el-icon-refresh" @click="handleResetSearch()">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 统计概览卡片 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="6">
        <el-card class="statistics-card">
          <div class="statistics-content">
            <div class="statistics-value">{{ statistics.totalRewards || 0 }}</div>
            <div class="statistics-label">总奖励记录数</div>
          </div>
          <i class="el-icon-medal statistics-icon" style="color: #409EFF"></i>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="statistics-card">
          <div class="statistics-content">
            <div class="statistics-value">¥{{ statistics.totalAmount || 0 }}</div>
            <div class="statistics-label">累计奖励金额</div>
          </div>
          <i class="el-icon-money statistics-icon" style="color: #67C23A"></i>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="statistics-card">
          <div class="statistics-content">
            <div class="statistics-value">¥{{ statistics.inviteAmount || 0 }}</div>
            <div class="statistics-label">邀请奖励总额</div>
          </div>
          <i class="el-icon-user statistics-icon" style="color: #E6A23C"></i>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="statistics-card">
          <div class="statistics-content">
            <div class="statistics-value">¥{{ statistics.commissionAmount || 0 }}</div>
            <div class="statistics-label">分销佣金总额</div>
          </div>
          <i class="el-icon-share statistics-icon" style="color: #F56C6C"></i>
        </el-card>
      </el-col>
    </el-row>

    <!-- 操作区域 -->
    <el-card class="operate-container" shadow="never" style="margin-top: 20px">
      <div>
        <i class="el-icon-tickets"></i>
        <span>数据列表</span>
      </div>
      <div style="margin-top: 15px">
        <el-button
          type="primary"
          size="small"
          icon="el-icon-refresh"
          @click="handleBatchSend"
          :disabled="multipleSelection.length === 0">
          批量发放
        </el-button>
        <el-button
          type="warning"
          size="small"
          icon="el-icon-refresh-right"
          @click="handleBatchRetry"
          :disabled="multipleSelection.length === 0">
          批量重试
        </el-button>
        <el-button
          type="success"
          size="small"
          icon="el-icon-download"
          @click="handleExport">
          导出数据
        </el-button>
        <el-button
          type="info"
          size="small"
          icon="el-icon-refresh"
          @click="refreshStatistics">
          刷新统计
        </el-button>
      </div>
    </el-card>

    <!-- 数据表格 -->
    <div class="table-container" style="margin-top: 20px">
      <el-table
        ref="rewardTable"
        :data="list"
        style="width: 100%"
        @selection-change="handleSelectionChange"
        v-loading="listLoading"
        border>
        <el-table-column type="selection" width="60" align="center"></el-table-column>
        <el-table-column label="奖励ID" width="80" align="center">
          <template slot-scope="scope">{{ scope.row.id }}</template>
        </el-table-column>
        <el-table-column label="用户信息" width="200" align="center">
          <template slot-scope="scope">
            <div>
              <div style="font-weight: bold">ID: {{ scope.row.userId }}</div>
              <div style="color: #909399; font-size: 12px">{{ scope.row.nickname || '未知用户' }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="奖励类型" width="120" align="center">
          <template slot-scope="scope">
            <el-tag :type="getCommissionTypeTagType(scope.row.commissionType)" size="small">
              {{ getCommissionTypeText(scope.row.commissionType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="奖励内容" width="150" align="center">
          <template slot-scope="scope">
            <div>
              <el-tag :type="getRewardTypeTagType(scope.row.rewardType)" size="small">
                {{ getRewardTypeText(scope.row.rewardType) }}
              </el-tag>
              <div style="margin-top: 5px; font-weight: bold; color: #E6A23C">
                {{ scope.row.rewardValue }} {{ getRewardUnit(scope.row.rewardType) }}
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="奖励描述" min-width="200" align="center">
          <template slot-scope="scope">{{ scope.row.rewardDesc }}</template>
        </el-table-column>
        <el-table-column label="触发类型" width="100" align="center">
          <template slot-scope="scope">
            <el-tag size="small">{{ getTriggerTypeText(scope.row.triggerType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发放状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getStatusTagType(scope.row.status)" size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发放时间" width="160" align="center">
          <template slot-scope="scope">
            {{ scope.row.sendTime ? formatDateTime(scope.row.sendTime, 'YYYY-MM-DD HH:mm') : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="过期时间" width="160" align="center">
          <template slot-scope="scope">
            {{ scope.row.expireTime ? formatDateTime(scope.row.expireTime, 'YYYY-MM-DD HH:mm') : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button
              size="mini"
              @click="handleViewDetail(scope.row)"
              type="primary">查看详情
            </el-button>
            <el-dropdown @command="handleMoreAction" style="margin-left: 10px" v-if="scope.row.status !== 1">
              <el-button size="mini">
                更多<i class="el-icon-arrow-down el-icon--right"></i>
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item 
                  :command="{action: 'retry', row: scope.row}"
                  v-if="scope.row.status === 2">
                  重试发放
                </el-dropdown-item>
                <el-dropdown-item 
                  :command="{action: 'send', row: scope.row}"
                  v-if="scope.row.status === 0">
                  立即发放
                </el-dropdown-item>
                <el-dropdown-item 
                  :command="{action: 'cancel', row: scope.row}"
                  v-if="scope.row.status === 0">
                  取消奖励
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-container" v-show="total > 0">
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

    <!-- 奖励详情弹窗 -->
    <el-dialog title="奖励详情" :visible.sync="detailDialogVisible" width="600px">
      <div v-if="currentReward">
        <el-table :data="getDetailTableData(currentReward)" border style="width: 100%">
          <el-table-column prop="label" label="项目" width="120" align="right" 
                          :show-overflow-tooltip="false" class-name="detail-label-column">
          </el-table-column>
          <el-table-column prop="value" label="内容" :show-overflow-tooltip="false">
            <template slot-scope="scope">
              <span v-html="scope.row.value"></span>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关 闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  getInviteRewards,
  getInviteRewardDetail,
  updateInviteRewardStatus,
  retrySendReward,
  batchSendReward,
  exportRewardRecords,
  getCommissionStatistics
} from '@/api/invite'
import { formatDateTime } from '@/utils/date'

export default {
  name: 'InviteRewardList',
  data() {
    return {
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        userType: null,
        rewardType: null,
        triggerType: null,
        status: null,
        commissionType: null,
        startTime: null,
        endTime: null
      },
      list: [],
      total: 0,
      listLoading: false,
      multipleSelection: [],
      timeRange: null,
      statistics: {},
      detailDialogVisible: false,
      currentReward: null
    }
  },
  created() {
    this.getList()
    this.getStatistics()
  },
  methods: {
    formatDateTime,

    getList() {
      this.listLoading = true
      getInviteRewards(this.listQuery).then(response => {
        this.list = response.data.list
        this.total = parseInt(response.data.total)
        this.listLoading = false
      }).catch(() => {
        this.listLoading = false
      })
    },

    getStatistics() {
      getCommissionStatistics().then(response => {
        const data = response.data
        // 将后端返回的字段映射为前端期望的字段
        this.statistics = {
          // 总奖励记录数 = 邀请奖励数量 + 一级分销数量 + 二级分销数量
          totalRewards: (data.inviteRewardCount || 0) + (data.level1CommissionCount || 0) + (data.level2CommissionCount || 0),
          // 累计奖励金额 = 邀请奖励总额 + 总佣金
          totalAmount: (data.inviteRewardTotal || 0) + (data.totalCommission || 0),
          // 邀请奖励总额
          inviteAmount: data.inviteRewardTotal || 0,
          // 分销佣金总额
          commissionAmount: data.totalCommission || 0
        }
      })
    },

    refreshStatistics() {
      this.getStatistics()
      this.$message.success('统计数据已刷新')
    },

    handleSearchList() {
      this.listQuery.pageNum = 1
      this.getList()
    },

    handleResetSearch() {
      this.listQuery = {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        userType: null,
        rewardType: null,
        triggerType: null,
        status: null,
        commissionType: null,
        startTime: null,
        endTime: null
      }
      this.timeRange = null
      this.getList()
    },

    handleTimeRangeChange(value) {
      if (value && value.length === 2) {
        this.listQuery.startTime = value[0]
        this.listQuery.endTime = value[1]
      } else {
        this.listQuery.startTime = null
        this.listQuery.endTime = null
      }
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
      getInviteRewardDetail(row.id).then(response => {
        this.currentReward = response.data
        this.detailDialogVisible = true
      })
    },

    handleMoreAction(command) {
      const { action, row } = command
      switch (action) {
        case 'retry':
          this.handleRetry(row)
          break
        case 'send':
          this.handleSend(row)
          break
        case 'cancel':
          this.handleCancel(row)
          break
      }
    },

    handleRetry(row) {
      this.$confirm('确定要重试发放该奖励吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        retrySendReward(row.id).then(response => {
          this.$message.success('重试发放成功')
          this.getList()
        })
      })
    },

    handleSend(row) {
      this.$confirm('确定要立即发放该奖励吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        batchSendReward([row.id]).then(response => {
          this.$message.success('发放成功')
          this.getList()
        })
      })
    },

    handleCancel(row) {
      this.$confirm('确定要取消该奖励吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        updateInviteRewardStatus(row.id, { status: 3 }).then(response => {
          this.$message.success('取消成功')
          this.getList()
        })
      })
    },

    handleBatchSend() {
      if (this.multipleSelection.length === 0) {
        this.$message.warning('请选择要发放的奖励记录')
        return
      }

      this.$confirm(`确定要批量发放选中的 ${this.multipleSelection.length} 条奖励记录吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const ids = this.multipleSelection.map(item => item.id)
        batchSendReward(ids).then(response => {
          this.$message.success('批量发放成功')
          this.getList()
        })
      })
    },

    handleBatchRetry() {
      if (this.multipleSelection.length === 0) {
        this.$message.warning('请选择要重试的奖励记录')
        return
      }

      const failedRecords = this.multipleSelection.filter(item => item.status === 2)
      if (failedRecords.length === 0) {
        this.$message.warning('请选择发放失败的记录进行重试')
        return
      }

      this.$confirm(`确定要批量重试选中的 ${failedRecords.length} 条失败记录吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const promises = failedRecords.map(item => retrySendReward(item.id))
        Promise.all(promises).then(() => {
          this.$message.success('批量重试成功')
          this.getList()
        })
      })
    },

    handleExport() {
      this.$confirm('确定要导出奖励记录数据吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        this.$message({
          message: '正在导出，请稍候...',
          type: 'info'
        });
        exportRewardRecords(this.listQuery).then(response => {
          const disposition = response.headers['content-disposition'];
          let fileName = 'reward_records_' + Date.now() + '.csv';
          if (disposition && disposition.includes('filename=')) {
            fileName = decodeURIComponent(disposition.split('filename=')[1]);
          }
          const blob = new Blob([response.data], { type: 'text/csv;charset=utf-8' });
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          link.download = fileName;
          document.body.appendChild(link);
          link.click();
          document.body.removeChild(link);
          window.URL.revokeObjectURL(url);
          this.$message.success('导出成功');
        }).catch(error => {
          this.$message.error('导出失败：' + (error.message || '未知错误'));
        });
      })
    },

    // 辅助方法
    getCommissionTypeText(type) {
      const typeMap = { 1: '邀请奖励', 2: '一级分销佣金', 3: '二级分销佣金' }
      return typeMap[type] || '未知'
    },

    getCommissionTypeTagType(type) {
      const typeMap = { 1: 'primary', 2: 'success', 3: 'warning' }
      return typeMap[type] || 'info'
    },

    getRewardTypeText(type) {
      const typeMap = { 1: '积分', 2: '优惠券', 3: '现金红包', 4: '商品' }
      return typeMap[type] || '未知'
    },

    getRewardTypeTagType(type) {
      const typeMap = { 1: 'primary', 2: 'success', 3: 'warning', 4: 'info' }
      return typeMap[type] || 'info'
    },

    getRewardUnit(type) {
      const unitMap = { 1: '积分', 2: '张', 3: '元', 4: '个' }
      return unitMap[type] || ''
    },

    getTriggerTypeText(type) {
      const typeMap = { 1: '注册完成', 2: '首单完成', 3: '累计消费达标' }
      return typeMap[type] || '未知'
    },

    getStatusText(status) {
      const statusMap = { 0: '待发放', 1: '已发放', 2: '发放失败', 3: '已过期' }
      return statusMap[status] || '未知'
    },

    getStatusTagType(status) {
      const typeMap = { 0: 'info', 1: 'success', 2: 'danger', 3: 'warning' }
      return typeMap[status] || 'info'
    },

    // 获取详情表格数据
    getDetailTableData(reward) {
      const details = []
      
      details.push({ label: '奖励ID', value: reward.id })
      details.push({ label: '用户ID', value: reward.userId })
      
      // 用户信息
      if (reward.userNickname) {
        details.push({ label: '用户昵称', value: reward.userNickname })
      }
      if (reward.userTypeText) {
        details.push({ label: '用户类型', value: reward.userTypeText })
      }
      if (reward.inviterNickname) {
        details.push({ label: '邀请者', value: reward.inviterNickname })
      }
      if (reward.inviteeNickname) {
        details.push({ label: '被邀请者', value: reward.inviteeNickname })
      }
      
      // 奖励类型 - 根据数据中的实际字段来显示
      if (reward.commissionType !== undefined) {
        // 如果有佣金类型信息，显示佣金类型
        const commissionTypeTag = `<span class="el-tag el-tag--${this.getCommissionTypeTagType(reward.commissionType)} el-tag--small">${this.getCommissionTypeText(reward.commissionType)}</span>`
        details.push({ label: '奖励类型', value: commissionTypeTag })
      } else {
        // 否则显示基础的奖励类型
        const basicTypeTag = `<span class="el-tag el-tag--${this.getRewardTypeTagType(reward.rewardType)} el-tag--small">${this.getRewardTypeText(reward.rewardType)}</span>`
        details.push({ label: '奖励类型', value: basicTypeTag })
      }
      
      // 奖励内容 - 带标签和数值
      const rewardTypeTag = `<span class="el-tag el-tag--${this.getRewardTypeTagType(reward.rewardType)} el-tag--small">${this.getRewardTypeText(reward.rewardType)}</span>`
      const rewardContent = `${rewardTypeTag} ${reward.rewardValue} ${this.getRewardUnit(reward.rewardType)}`
      details.push({ label: '奖励内容', value: rewardContent })
      
      details.push({ label: '触发类型', value: this.getTriggerTypeText(reward.triggerType) })
      
      // 发放状态 - 带标签
      const statusTag = `<span class="el-tag el-tag--${this.getStatusTagType(reward.status)} el-tag--small">${this.getStatusText(reward.status)}</span>`
      details.push({ label: '发放状态', value: statusTag })
      
      // 可选字段
      if (reward.orderId) {
        details.push({ label: '关联订单', value: reward.orderId })
      }
      if (reward.orderAmount) {
        details.push({ label: '订单金额', value: `¥${reward.orderAmount}` })
      }
      if (reward.commissionRate) {
        details.push({ label: '佣金比例', value: `${(reward.commissionRate * 100).toFixed(2)}%` })
      }
      if (reward.productCategory) {
        details.push({ label: '商品分类', value: reward.productCategory })
      }
      
      details.push({ label: '创建时间', value: this.formatDateTime(reward.createdAt) })
      details.push({ 
        label: '发放时间', 
        value: reward.sendTime ? this.formatDateTime(reward.sendTime, 'YYYY-MM-DD HH:mm') : '-' 
      })
      
      if (reward.expireTime) {
        details.push({ 
          label: '过期时间', 
          value: this.formatDateTime(reward.expireTime, 'YYYY-MM-DD HH:mm')
        })
      }
      
      if (reward.sendResult) {
        details.push({ label: '发放结果', value: reward.sendResult })
      }
      
      details.push({ label: '奖励描述', value: reward.rewardDesc })
      
      return details
    }
  }
}
</script>

<style scoped>
.filter-container, .operate-container {
  margin-bottom: 20px;
}

.filter-container i, .operate-container i {
  color: #409EFF;
  margin-right: 5px;
}

.statistics-card {
  text-align: center;
  position: relative;
  overflow: hidden;
}

.statistics-content {
  padding: 20px 0;
}

.statistics-value {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 8px;
}

.statistics-label {
  font-size: 14px;
  color: #909399;
}

.statistics-icon {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 40px;
  opacity: 0.8;
}

.table-container {
  background: white;
  padding: 20px;
  border-radius: 4px;
}

/* 详情表格样式 */
.detail-label-column {
  background-color: #fafafa !important;
  font-weight: bold;
  color: #606266;
}

.detail-label-column .cell {
  padding-right: 12px;
}

/* 确保HTML标签正确渲染 */
.el-table .cell span {
  display: inline-block;
}

.el-tag--small {
  height: 24px;
  line-height: 22px;
  font-size: 12px;
  padding: 0 8px;
}
</style> 