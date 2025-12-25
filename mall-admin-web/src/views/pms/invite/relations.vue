<template>
  <div class="app-container">
    <!-- 搜索筛选 -->
    <el-card class="filter-container" shadow="never">
      <div>
        <el-form :inline="true" :model="listQuery" size="small" label-width="140px">
          <el-form-item label="邀请者ID:">
            <el-input
              v-model="listQuery.inviterId"
              placeholder="请输入邀请者ID"
              style="width: 160px;"
              clearable
            />
          </el-form-item>
          <el-form-item label="被邀请者ID:">
            <el-input
              v-model="listQuery.inviteeId"
              placeholder="请输入被邀请者ID"
              style="width: 160px;"
              clearable
            />
          </el-form-item>
          <el-form-item label="邀请状态:">
            <el-select v-model="listQuery.status" placeholder="请选择状态" clearable style="width: 160px;">
              <el-option label="已邀请未注册" :value="0" />
              <el-option label="已注册未首单" :value="1" />
              <el-option label="已完成首单" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="邀请时间:">
            <el-date-picker
              v-model="listQuery.dateRange"
              type="datetimerange"
              value-format="yyyy-MM-dd HH:mm:ss"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              style="width: 300px;"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="handleSearchList">查询</el-button>
            <el-button icon="el-icon-refresh" @click="handleResetSearch">重置</el-button>
            <el-button type="success" icon="el-icon-download" @click="handleExport" :loading="exportLoading">导出</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 数据概览 -->
    <el-card class="box-card" shadow="never" style="margin-top: 20px;">
      <div slot="header" class="clearfix">
        <span>数据概览</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="getOverview">刷新</el-button>
      </div>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value">{{ overview.totalRelations || 0 }}</div>
            <div class="stat-label">总邀请关系</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value">{{ overview.registeredCount || 0 }}</div>
            <div class="stat-label">已注册用户</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value">{{ overview.completedCount || 0 }}</div>
            <div class="stat-label">已完成首单</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value">{{ ((overview.registerRate || 0) * 100).toFixed(1) }}%</div>
            <div class="stat-label">注册转化率</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 邀请记录列表 -->
    <el-card class="operate-container" shadow="never" style="margin-top: 20px;">
      <div slot="header" class="clearfix">
        <span>邀请记录列表</span>
        <div style="float: right;">
          <el-button
            size="mini"
            type="danger"
            icon="el-icon-delete"
            :disabled="multipleSelection.length === 0"
            @click="handleBatchUpdate"
          >
            批量操作
          </el-button>
        </div>
      </div>

      <el-table
        ref="inviteTable"
        :data="list"
        style="width: 100%"
        @selection-change="handleSelectionChange"
        v-loading="listLoading"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column label="邀请ID" width="80" align="center">
          <template slot-scope="scope">{{ scope.row.id }}</template>
        </el-table-column>
        <el-table-column label="邀请者" width="150" align="center">
          <template slot-scope="scope">
            <div>{{ scope.row.inviterNickname || '未知' }}</div>
            <div class="text-muted">{{ scope.row.inviterPhone || '' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="被邀请者" width="150" align="center">
          <template slot-scope="scope">
            <div>{{ scope.row.inviteeNickname || '未知' }}</div>
            <div class="text-muted">{{ scope.row.inviteePhone || '' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="邀请参数" width="180" align="center">
          <template slot-scope="scope">{{ scope.row.inviteParam }}</template>
        </el-table-column>
        <el-table-column label="邀请时间" width="160" align="center">
          <template slot-scope="scope">{{ formatDate(scope.row.inviteTime) }}</template>
        </el-table-column>
        <el-table-column label="注册时间" width="160" align="center">
          <template slot-scope="scope">
            {{ scope.row.registerTime ? formatDate(scope.row.registerTime) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="首单信息" width="150" align="center">
          <template slot-scope="scope">
            <div v-if="scope.row.firstOrderTime">
              <div>{{ formatDate(scope.row.firstOrderTime) }}</div>
              <div class="text-muted">¥{{ scope.row.firstOrderAmount || 0 }}</div>
            </div>
            <div v-else>-</div>
          </template>
        </el-table-column>
        <el-table-column label="邀请状态" width="120" align="center">
          <template slot-scope="scope">
            <el-tag :type="getStatusTagType(scope.row.status)">
              {{ scope.row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="奖励状态" width="130" align="center">
          <template slot-scope="scope">
            <el-tag :type="getRewardStatusTagType(scope.row.rewardStatus)">
              {{ scope.row.rewardStatusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="进入场景" width="120" align="center">
          <template slot-scope="scope">{{ scope.row.sceneTypeText }}</template>
        </el-table-column>
        <el-table-column label="IP地址" width="120" align="center">
          <template slot-scope="scope">{{ scope.row.ipAddress || '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" @click="handleViewDetail(scope.row)">详情</el-button>
            <el-button size="mini" type="warning" @click="handleUpdateStatus(scope.row)">状态</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-show="total > 0"
        background
        :current-page="listQuery.pageNum"
        :page-size="listQuery.pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      ></el-pagination>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog title="邀请关系详情" :visible.sync="detailVisible" width="60%">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="邀请ID">{{ detailData.id }}</el-descriptions-item>
        <el-descriptions-item label="邀请参数">{{ detailData.inviteParam }}</el-descriptions-item>
        <el-descriptions-item label="邀请者昵称">{{ detailData.inviterNickname }}</el-descriptions-item>
        <el-descriptions-item label="邀请者手机">{{ detailData.inviterPhone }}</el-descriptions-item>
        <el-descriptions-item label="被邀请者昵称">{{ detailData.inviteeNickname }}</el-descriptions-item>
        <el-descriptions-item label="被邀请者手机">{{ detailData.inviteePhone }}</el-descriptions-item>
        <el-descriptions-item label="邀请时间">{{ formatDate(detailData.inviteTime) }}</el-descriptions-item>
        <el-descriptions-item label="注册时间">
          {{ detailData.registerTime ? formatDate(detailData.registerTime) : '未注册' }}
        </el-descriptions-item>
        <el-descriptions-item label="首单时间">
          {{ detailData.firstOrderTime ? formatDate(detailData.firstOrderTime) : '未下单' }}
        </el-descriptions-item>
        <el-descriptions-item label="首单金额">¥{{ detailData.firstOrderAmount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="邀请状态">
          <el-tag :type="getStatusTagType(detailData.status)">{{ detailData.statusText }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="奖励状态">
          <el-tag :type="getRewardStatusTagType(detailData.rewardStatus)">{{ detailData.rewardStatusText }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="进入场景">{{ detailData.sceneTypeText }}</el-descriptions-item>
        <el-descriptions-item label="来源页面">{{ detailData.sourcePage || '-' }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ detailData.ipAddress || '-' }}</el-descriptions-item>
        <el-descriptions-item label="设备信息" :span="2">
          <pre>{{ detailData.deviceInfo || '无' }}</pre>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 状态更新对话框 -->
    <el-dialog title="更新邀请状态" :visible.sync="statusVisible" width="400px">
      <el-form :model="statusForm" label-width="100px">
        <el-form-item label="状态">
          <el-select v-model="statusForm.status" placeholder="请选择状态">
            <el-option label="已邀请未注册" :value="0" />
            <el-option label="已注册未首单" :value="1" />
            <el-option label="已完成首单" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="statusForm.remark" type="textarea" placeholder="请输入变更原因" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="statusVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmUpdateStatus" :loading="statusLoading">确定</el-button>
      </div>
    </el-dialog>

    <!-- 批量操作对话框 -->
    <el-dialog title="批量更新状态" :visible.sync="batchVisible" width="400px">
      <el-form :model="batchForm" label-width="100px">
        <el-form-item label="选中记录">
          <span>{{ multipleSelection.length }} 条记录</span>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="batchForm.status" placeholder="请选择状态">
            <el-option label="已邀请未注册" :value="0" />
            <el-option label="已注册未首单" :value="1" />
            <el-option label="已完成首单" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="batchForm.remark" type="textarea" placeholder="请输入变更原因" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="batchVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmBatchUpdate" :loading="batchLoading">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getInviteRelations, getInviteRelationDetail, updateInviteRelationStatus, batchUpdateInviteStatus, exportInviteRecords, getInviteOverview } from '@/api/invite'
import { formatStandardDateTime } from '@/utils/date'
// import Pagination from '@/components/Pagination' // 使用Element UI分页组件

export default {
  name: 'InviteRelations',
  data() {
    return {
      listQuery: {
        pageNum: 1,
        pageSize: 20,
        inviterId: null,
        inviteeId: null,
        status: null,
        dateRange: null
      },
      list: [],
      total: 0,
      listLoading: false,
      exportLoading: false,
      multipleSelection: [],
      overview: {},
      
      // 详情对话框
      detailVisible: false,
      detailData: {},
      
      // 状态更新对话框
      statusVisible: false,
      statusLoading: false,
      statusForm: {
        id: null,
        status: null,
        remark: ''
      },
      
      // 批量操作对话框
      batchVisible: false,
      batchLoading: false,
      batchForm: {
        status: null,
        remark: ''
      }
    }
  },
  created() {
    this.getList()
    this.getOverview()
  },
  methods: {
    async getList() {
      this.listLoading = true
      try {
        const params = { ...this.listQuery }
        if (params.dateRange && params.dateRange.length === 2) {
          params.startTime = params.dateRange[0]
          params.endTime = params.dateRange[1]
          delete params.dateRange
        }
        
        const response = await getInviteRelations(params)
        if (response.data) {
          this.list = response.data.list || []
          this.total = response.data.total || 0
        }
      } catch (error) {
        this.$message.error('获取数据失败：' + error.message)
      } finally {
        this.listLoading = false
      }
    },
    
    async getOverview() {
      try {
        const response = await getInviteOverview()
        if (response.data) {
          this.overview = response.data
        }
      } catch (error) {
        console.error('获取概览数据失败：', error)
      }
    },
    
    handleSearchList() {
      this.listQuery.pageNum = 1
      this.getList()
    },
    
    handleResetSearch() {
      this.listQuery = {
        pageNum: 1,
        pageSize: 20,
        inviterId: null,
        inviteeId: null,
        status: null,
        dateRange: null
      }
      this.getList()
    },

    handleSizeChange(val) {
      this.listQuery.pageSize = val
      this.getList()
    },

    handleCurrentChange(val) {
      this.listQuery.pageNum = val
      this.getList()
    },
    
    async handleExport() {
      this.exportLoading = true
      try {
        const params = { ...this.listQuery }
        if (params.dateRange && params.dateRange.length === 2) {
          params.startTime = params.dateRange[0]
          params.endTime = params.dateRange[1]
          delete params.dateRange
        }
        delete params.pageNum
        delete params.pageSize
        
        const response = await exportInviteRecords(params)
        if (response.data) {
          // 这里应该处理文件下载逻辑
          this.$message.success('导出成功，共 ' + response.data.length + ' 条记录')
          // 实际项目中可以使用 file-saver 等库进行文件下载
        }
      } catch (error) {
        this.$message.error('导出失败：' + error.message)
      } finally {
        this.exportLoading = false
      }
    },
    
    async handleViewDetail(row) {
      try {
        const response = await getInviteRelationDetail(row.id)
        if (response.data) {
          this.detailData = response.data
          this.detailVisible = true
        }
      } catch (error) {
        this.$message.error('获取详情失败：' + error.message)
      }
    },
    
    handleUpdateStatus(row) {
      this.statusForm = {
        id: row.id,
        status: row.status,
        remark: ''
      }
      this.statusVisible = true
    },
    
    async confirmUpdateStatus() {
      if (this.statusForm.status === null) {
        this.$message.error('请选择状态')
        return
      }
      
      this.statusLoading = true
      try {
        await updateInviteRelationStatus(this.statusForm.id, {
          status: this.statusForm.status,
          remark: this.statusForm.remark
        })
        this.$message.success('状态更新成功')
        this.statusVisible = false
        this.getList()
      } catch (error) {
        this.$message.error('状态更新失败：' + error.message)
      } finally {
        this.statusLoading = false
      }
    },
    
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    
    handleBatchUpdate() {
      if (this.multipleSelection.length === 0) {
        this.$message.error('请先选择要操作的记录')
        return
      }
      this.batchForm = {
        status: null,
        remark: ''
      }
      this.batchVisible = true
    },
    
    async confirmBatchUpdate() {
      if (this.batchForm.status === null) {
        this.$message.error('请选择状态')
        return
      }
      
      this.batchLoading = true
      try {
        const ids = this.multipleSelection.map(item => item.id)
        await batchUpdateInviteStatus({
          ids,
          status: this.batchForm.status,
          remark: this.batchForm.remark
        })
        this.$message.success('批量更新成功')
        this.batchVisible = false
        this.getList()
      } catch (error) {
        this.$message.error('批量更新失败：' + error.message)
      } finally {
        this.batchLoading = false
      }
    },
    
    getStatusTagType(status) {
      const map = {
        0: 'info',
        1: 'warning', 
        2: 'success'
      }
      return map[status] || 'info'
    },
    
    getRewardStatusTagType(status) {
      const map = {
        0: 'danger',
        1: 'warning',
        2: 'warning',
        3: 'success'
      }
      return map[status] || 'info'
    },
    
    formatDate(date) {
      return formatStandardDateTime(date)
    }
  }
}
</script>

<style scoped>
.text-muted {
  color: #999;
  font-size: 12px;
}

.stat-item {
  text-align: center;
  padding: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background-color: #f8f9fa;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}

pre {
  font-family: monospace;
  font-size: 12px;
  color: #666;
  background: #f5f5f5;
  padding: 8px;
  border-radius: 4px;
  max-height: 200px;
  overflow-y: auto;
}
</style> 