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
          <el-form-item label="提现状态：">
            <el-select v-model="listQuery.status" placeholder="全部状态" clearable style="width: 150px">
              <el-option label="全部" :value="null"></el-option>
              <el-option label="待审核" :value="0"></el-option>
              <el-option label="审核通过" :value="1"></el-option>
              <el-option label="提现成功" :value="2"></el-option>
              <el-option label="提现失败" :value="3"></el-option>
              <el-option label="已取消" :value="4"></el-option>
              <el-option label="审核拒绝" :value="5"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="提现方式：">
            <el-select v-model="listQuery.withdrawType" placeholder="全部方式" clearable style="width: 150px">
              <el-option label="全部" :value="null"></el-option>
              <el-option label="微信钱包" :value="1"></el-option>
              <el-option label="银行卡" :value="2"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="申请时间：">
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
            <div class="statistics-value">{{ statistics.totalApplications || 0 }}</div>
            <div class="statistics-label">总申请数量</div>
          </div>
          <i class="el-icon-document statistics-icon" style="color: #409EFF"></i>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="statistics-card">
          <div class="statistics-content">
            <div class="statistics-value">¥{{ statistics.totalAmount || 0 }}</div>
            <div class="statistics-label">申请提现总额</div>
          </div>
          <i class="el-icon-money statistics-icon" style="color: #67C23A"></i>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="statistics-card">
          <div class="statistics-content">
            <div class="statistics-value">{{ statistics.pendingCount || 0 }}</div>
            <div class="statistics-label">待审核申请</div>
          </div>
          <i class="el-icon-time statistics-icon" style="color: #E6A23C"></i>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="statistics-card">
          <div class="statistics-content">
            <div class="statistics-value">¥{{ statistics.successAmount || 0 }}</div>
            <div class="statistics-label">成功提现金额</div>
          </div>
          <i class="el-icon-success statistics-icon" style="color: #F56C6C"></i>
        </el-card>
      </el-col>
    </el-row>

    <!-- 操作区域 -->
    <el-card class="operate-container" shadow="never">
      <div>
        <i class="el-icon-info"></i>
        <span>批量操作</span>
        <!-- 状态说明 -->
        <el-tooltip class="item" effect="light" content="提现状态说明：待审核→审核通过/审核拒绝→提现成功/提现失败" placement="top">
          <el-button size="mini" type="text" icon="el-icon-question"></el-button>
        </el-tooltip>
      </div>
      <div style="margin-top: 15px">
        <el-button style="margin-left: 15px" type="primary" size="small" @click="handleBatchApprove">
          批量通过
        </el-button>
        <el-button style="margin-left: 15px" type="danger" size="small" @click="handleBatchReject">
          批量拒绝
        </el-button>
        <el-button style="margin-left: 15px" type="success" size="small" @click="handleBatchProcess">
          批量确认到账
        </el-button>
        <el-button style="margin-left: 15px" type="info" size="small" @click="handleExport">
          导出记录
        </el-button>
        <el-button style="margin-left: 15px" size="small" @click="handleRefreshStats">
          刷新统计
        </el-button>
      </div>
    </el-card>

    <!-- 数据表格 -->
    <div class="table-container" style="margin-top: 20px">
      <el-table
        ref="withdrawTable"
        :data="list"
        style="width: 100%"
        @selection-change="handleSelectionChange"
        v-loading="listLoading"
        border>
        <el-table-column type="selection" width="60" align="center"></el-table-column>
        <el-table-column label="申请ID" width="80" align="center">
          <template slot-scope="scope">{{ scope.row.id }}</template>
        </el-table-column>
        <el-table-column label="用户信息" width="180" align="center">
          <template slot-scope="scope">
            <div>
              <div style="font-weight: bold">ID: {{ scope.row.userId }}</div>
              <div style="color: #909399; font-size: 12px">{{ scope.row.nickname || '未知用户' }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="提现单号" width="160" align="center">
          <template slot-scope="scope">{{ scope.row.withdrawSn }}</template>
        </el-table-column>
        <el-table-column label="申请金额" width="120" align="center">
          <template slot-scope="scope">
            <div>
              <div style="font-weight: bold; color: #E6A23C">¥{{ scope.row.applyAmount }}</div>
              <div style="font-size: 12px; color: #909399">手续费: ¥{{ scope.row.feeAmount }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="实际到账" width="100" align="center">
          <template slot-scope="scope">
            <div style="font-weight: bold; color: #67C23A">¥{{ scope.row.actualAmount }}</div>
          </template>
        </el-table-column>
        <el-table-column label="资金来源" width="150" align="center">
          <template slot-scope="scope">
            <div v-if="scope.row.fundsSources">
              <div v-for="source in scope.row.fundsSources" :key="source.type" style="margin-bottom: 2px">
                <el-tag :type="getFundsSourceTagType(source.type)" size="mini">
                  {{ getFundsSourceText(source.type) }}: ¥{{ source.amount }}
                </el-tag>
              </div>
            </div>
            <div v-else style="color: #909399">-</div>
          </template>
        </el-table-column>
        <el-table-column label="提现方式" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getWithdrawTypeTagType(scope.row.withdrawType)" size="small">
              {{ getWithdrawTypeText(scope.row.withdrawType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="提现状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getStatusTagType(scope.row.status)" size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="申请时间" width="160" align="center">
          <template slot-scope="scope">
            {{ formatDate(scope.row.applyTime) }}
          </template>
        </el-table-column>
        <el-table-column label="处理时间" width="160" align="center">
          <template slot-scope="scope">
            {{ scope.row.processTime ? formatDate(scope.row.processTime) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button
              size="mini"
              @click="handleViewDetail(scope.row)"
              type="primary">查看详情
            </el-button>
            <el-dropdown @command="handleMoreAction" style="margin-left: 10px" v-if="scope.row.status === 0 || scope.row.status === 1">
              <el-button size="mini">
                更多<i class="el-icon-arrow-down el-icon--right"></i>
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item 
                  :command="{action: 'approve', row: scope.row}"
                  v-if="scope.row.status === 0">
                  审核通过
                </el-dropdown-item>
                <el-dropdown-item 
                  :command="{action: 'reject', row: scope.row}"
                  v-if="scope.row.status === 0">
                  审核拒绝
                </el-dropdown-item>
                <el-dropdown-item 
                  :command="{action: 'process', row: scope.row}"
                  v-if="scope.row.status === 1">
                  确认到账
                </el-dropdown-item>
                <el-dropdown-item 
                  :command="{action: 'fail', row: scope.row}"
                  v-if="scope.row.status === 1">
                  处理失败
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

    <!-- 提现详情弹窗 -->
    <el-dialog title="提现申请详情" :visible.sync="detailDialogVisible" width="800px">
      <div v-if="currentWithdraw">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-descriptions title="基本信息" :column="1" border>
              <el-descriptions-item label="申请ID">{{ currentWithdraw.id }}</el-descriptions-item>
              <el-descriptions-item label="提现单号">{{ currentWithdraw.withdrawSn }}</el-descriptions-item>
              <el-descriptions-item label="用户ID">{{ currentWithdraw.userId }}</el-descriptions-item>
              <el-descriptions-item label="申请金额">¥{{ currentWithdraw.applyAmount }}</el-descriptions-item>
              <el-descriptions-item label="手续费">¥{{ currentWithdraw.feeAmount }}</el-descriptions-item>
              <el-descriptions-item label="实际到账">¥{{ currentWithdraw.actualAmount }}</el-descriptions-item>
              <el-descriptions-item label="提现方式">
                <el-tag :type="getWithdrawTypeTagType(currentWithdraw.withdrawType)">
                  {{ getWithdrawTypeText(currentWithdraw.withdrawType) }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="提现状态">
                <el-tag :type="getStatusTagType(currentWithdraw.status)">
                  {{ getStatusText(currentWithdraw.status) }}
                </el-tag>
              </el-descriptions-item>
            </el-descriptions>
          </el-col>
          <el-col :span="12">
            <el-descriptions title="资金来源构成" :column="1" border>
              <el-descriptions-item v-if="currentWithdraw.fundsSources" v-for="source in currentWithdraw.fundsSources" 
                :key="source.type" :label="getFundsSourceText(source.type)">
                ¥{{ source.amount }}
                <el-tag :type="getFundsSourceTagType(source.type)" size="mini" style="margin-left: 10px">
                  {{ ((source.amount / currentWithdraw.applyAmount) * 100).toFixed(1) }}%
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item v-if="!currentWithdraw.fundsSources" label="暂无数据">-</el-descriptions-item>
            </el-descriptions>
          </el-col>
        </el-row>
        
        <el-descriptions title="时间信息" :column="2" border style="margin-top: 20px">
          <el-descriptions-item label="申请时间">{{ formatDateTimeDetail(currentWithdraw.applyTime) }}</el-descriptions-item>
          <el-descriptions-item label="审核时间">
            {{ currentWithdraw.auditTime ? formatDateTimeDetail(currentWithdraw.auditTime) : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="处理时间">
            {{ currentWithdraw.processTime ? formatDateTimeDetail(currentWithdraw.processTime) : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="预计到账时间">
            {{ currentWithdraw.expectedArrivalTime ? formatDateTimeDetail(currentWithdraw.expectedArrivalTime) : '-' }}
          </el-descriptions-item>
        </el-descriptions>

        <el-descriptions title="账户信息" :column="2" border style="margin-top: 20px" v-if="currentWithdraw.withdrawAccount">
          <el-descriptions-item label="提现账户">{{ currentWithdraw.withdrawAccount }}</el-descriptions-item>
          <el-descriptions-item label="账户名称">{{ currentWithdraw.accountName || '-' }}</el-descriptions-item>
        </el-descriptions>

        <el-descriptions title="备注信息" :column="1" border style="margin-top: 20px">
          <el-descriptions-item label="申请备注">{{ currentWithdraw.remark || '-' }}</el-descriptions-item>
          <el-descriptions-item label="审核备注">{{ currentWithdraw.auditRemark || '-' }}</el-descriptions-item>
          <el-descriptions-item label="处理备注">{{ currentWithdraw.processRemark || '-' }}</el-descriptions-item>
          <el-descriptions-item label="失败原因" v-if="currentWithdraw.failureReason">{{ currentWithdraw.failureReason }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关 闭</el-button>
      </span>
    </el-dialog>

    <!-- 审核弹窗 -->
    <el-dialog title="审核提现申请" :visible.sync="auditDialogVisible" width="500px">
      <el-form :model="auditForm" :rules="auditRules" ref="auditForm" label-width="120px">
        <el-form-item label="审核结果：" prop="result">
          <el-radio-group v-model="auditForm.result">
            <el-radio :label="1">通过</el-radio>
            <el-radio :label="2">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见：" prop="remark">
          <el-input
            type="textarea"
            :rows="3"
            placeholder="请输入审核意见"
            v-model="auditForm.remark">
          </el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="auditDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleAuditConfirm">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  getInviteWithdraws,
  getInviteWithdrawDetail,
  auditWithdrawApply,
  processWithdrawApply,
  batchAuditWithdraw,
  exportWithdrawRecords,
  getWithdrawStatistics,
  getWithdrawFundsSources
} from '@/api/invite'
import { formatDateTime } from '@/utils/date'

export default {
  name: 'InviteWithdrawList',
  data() {
    return {
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        status: null,
        withdrawType: null,
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
      auditDialogVisible: false,
      currentWithdraw: null,
      auditForm: {
        result: 1,
        remark: ''
      },
      auditRules: {
        result: [{ required: true, message: '请选择审核结果', trigger: 'change' }],
        remark: [{ required: true, message: '请输入审核意见', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
    this.getStatistics()
  },
  methods: {
    getList() {
      this.listLoading = true
      getInviteWithdraws(this.listQuery).then(response => {
        this.list = response.data.list
        this.total = parseInt(response.data.total)
        this.listLoading = false
      }).catch(() => {
        this.listLoading = false
      })
    },

    getStatistics() {
      getWithdrawStatistics().then(response => {
        this.statistics = response.data
      })
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
        status: null,
        withdrawType: null,
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
      getInviteWithdrawDetail(row.id).then(response => {
        this.currentWithdraw = response.data
        // 获取资金来源数据
        getWithdrawFundsSources(row.id).then(fundsResponse => {
          this.currentWithdraw.fundsSources = fundsResponse.data
          this.detailDialogVisible = true
        })
      })
    },

    handleMoreAction(command) {
      const { action, row } = command
      switch (action) {
        case 'approve':
          this.handleApprove(row)
          break
        case 'reject':
          this.handleReject(row)
          break
        case 'process':
          this.handleProcess(row)
          break
        case 'fail':
          this.handleFail(row)
          break
      }
    },

    handleApprove(row) {
      this.$confirm(`
        <div style="text-align: left;">
          <p>提现申请详情：</p>
          <p>申请人：<strong>${row.userNickname || row.userPhone}</strong></p>
          <p>申请金额：<strong style="color: #E6A23C;">¥${row.applyAmount}</strong></p>
          <p>手续费：<strong>¥${row.feeAmount}</strong></p>
          <p>实际到账：<strong style="color: #67C23A;">¥${row.actualAmount}</strong></p>
          <p>提现方式：<strong>${this.getWithdrawTypeText(row.withdrawType)}</strong></p>
          <p style="color: #909399; font-size: 12px;">确认通过后将自动进入转账流程</p>
        </div>
      `, '确认审核通过', {
        confirmButtonText: '确认通过',
        cancelButtonText: '取消',
        type: 'success',
        dangerouslyUseHTMLString: true
      }).then(() => {
        this.auditForm = {
          result: 1,
          remark: '审核通过'
        }
        this.currentWithdraw = row
        this.submitAudit()
      })
    },

    handleReject(row) {
      this.$prompt(`
        拒绝 ${row.userNickname || row.userPhone} 的提现申请，请输入拒绝原因：
      `, '审核拒绝', {
        confirmButtonText: '确认拒绝',
        cancelButtonText: '取消',
        inputPattern: /.+/,
        inputErrorMessage: '拒绝原因不能为空',
        inputType: 'textarea',
        inputPlaceholder: '请输入详细的拒绝原因，将发送给用户'
      }).then(({ value }) => {
        this.auditForm = {
          result: 2,
          remark: value
        }
        this.currentWithdraw = row
        this.submitAudit()
      })
    },

    handleAuditConfirm() {
      this.$refs.auditForm.validate(valid => {
        if (valid) {
          const params = {
            result: this.auditForm.result,
            remark: this.auditForm.remark
          }
          auditWithdrawApply(this.currentWithdraw.id, params).then(response => {
            this.$message.success('审核成功')
            this.auditDialogVisible = false
            this.getList()
          })
        }
      })
    },

    handleProcess(row) {
      this.$confirm('确定该提现申请已成功到账吗?', '确认到账', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }).then(() => {
        processWithdrawApply(row.id, { status: 2, remark: '提现成功' }).then(response => {
          this.$message.success('处理成功')
          this.getList()
        })
      })
    },

    handleFail(row) {
      this.$prompt('请输入失败原因', '处理失败', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /.+/,
        inputErrorMessage: '失败原因不能为空'
      }).then(({ value }) => {
        processWithdrawApply(row.id, { status: 3, remark: value }).then(response => {
          this.$message.success('处理成功')
          this.getList()
        })
      })
    },

    handleBatchApprove() {
      if (this.multipleSelection.length === 0) {
        this.$message.warning('请选择要审核的申请')
        return
      }

      const pendingApplies = this.multipleSelection.filter(item => item.status === 0)
      if (pendingApplies.length === 0) {
        this.$message.warning('请选择待审核的申请')
        return
      }

      // 计算批量审核的总金额
      const totalAmount = pendingApplies.reduce((sum, item) => sum + parseFloat(item.applyAmount || 0), 0)

      this.$confirm(`
        <div style="text-align: left;">
          <p>即将批量通过 <strong>${pendingApplies.length}</strong> 条提现申请</p>
          <p>涉及总金额：<strong style="color: #E6A23C;">¥${totalAmount.toFixed(2)}</strong></p>
          <p style="color: #909399; font-size: 12px;">通过后将自动进入转账流程，请确认操作。</p>
        </div>
      `, '确认批量审核', {
        confirmButtonText: '确认通过',
        cancelButtonText: '取消',
        type: 'warning',
        dangerouslyUseHTMLString: true
      }).then(() => {
        const ids = pendingApplies.map(item => item.id)
        batchAuditWithdraw(ids, { result: 1, remark: '批量审核通过' }).then(response => {
          this.$message.success(`成功审核通过 ${pendingApplies.length} 条申请`)
          this.getList()
          this.refreshStatistics()
        })
      })
    },

    handleBatchReject() {
      if (this.multipleSelection.length === 0) {
        this.$message.warning('请选择要审核的申请')
        return
      }

      const pendingApplies = this.multipleSelection.filter(item => item.status === 0)
      if (pendingApplies.length === 0) {
        this.$message.warning('请选择待审核的申请')
        return
      }

      this.$prompt(`
        即将批量拒绝 ${pendingApplies.length} 条提现申请，请输入拒绝原因：
      `, '批量拒绝', {
        confirmButtonText: '确认拒绝',
        cancelButtonText: '取消',
        inputPattern: /.+/,
        inputErrorMessage: '拒绝原因不能为空',
        inputType: 'textarea',
        inputPlaceholder: '请输入详细的拒绝原因，将发送给用户'
      }).then(({ value }) => {
        const ids = pendingApplies.map(item => item.id)
        batchAuditWithdraw(ids, { result: 2, remark: value }).then(response => {
          this.$message.success(`成功拒绝 ${pendingApplies.length} 条申请`)
          this.getList()
          this.refreshStatistics()
        })
      })
    },

    handleBatchProcess() {
      if (this.multipleSelection.length === 0) {
        this.$message.warning('请选择要处理的申请')
        return
      }

      const approvedApplies = this.multipleSelection.filter(item => item.status === 1)
      if (approvedApplies.length === 0) {
        this.$message.warning('请选择已审核通过的申请')
        return
      }

      this.$confirm(`确定要批量处理选中的 ${approvedApplies.length} 条申请为成功状态吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const promises = approvedApplies.map(item => 
          processWithdrawApply(item.id, { status: 2, remark: '批量处理成功' })
        )
        Promise.all(promises).then(() => {
          this.$message.success('批量处理成功')
          this.getList()
        })
      })
    },

    handleExport() {
      this.$confirm('确定要导出提现记录数据吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        this.$message({
          message: '正在导出，请稍候...',
          type: 'info'
        });
        exportWithdrawRecords(this.listQuery).then(response => {
          const disposition = response.headers['content-disposition'];
          let fileName = 'withdraw_records_' + Date.now() + '.csv';
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

    handleRefreshStats() {
      this.refreshStatistics()
      this.$message.success('统计数据已刷新')
    },

    refreshStatistics() {
      this.getStatistics()
    },

    // 时间格式化方法
    formatDate(date, format = 'YYYY-MM-DD HH:mm') {
      return formatDateTime(date, format)
    },

    formatDateTimeDetail(date) {
      return formatDateTime(date, 'YYYY-MM-DD HH:mm:ss')
    },

    // 辅助方法
    getStatusText(status) {
      const statusMap = { 
        0: '待审核', 
        1: '审核通过', 
        2: '提现成功', 
        3: '提现失败', 
        4: '已取消', 
        5: '审核拒绝' 
      }
      return statusMap[status] || '未知'
    },

    getStatusTagType(status) {
      const typeMap = { 
        0: 'info',      // 待审核 - 蓝色
        1: 'warning',   // 审核通过 - 黄色
        2: 'success',   // 提现成功 - 绿色
        3: 'danger',    // 提现失败 - 红色
        4: 'info',      // 已取消 - 蓝色
        5: 'danger'     // 审核拒绝 - 红色
      }
      return typeMap[status] || 'info'
    },

    getWithdrawTypeText(type) {
      const typeMap = { 1: '微信钱包', 2: '银行卡' }
      return typeMap[type] || '未知'
    },

    getWithdrawTypeTagType(type) {
      const typeMap = { 1: 'success', 2: 'primary' }
      return typeMap[type] || 'info'
    },

    getFundsSourceText(type) {
      const typeMap = { 1: '邀请奖励', 2: '一级分销佣金', 3: '二级分销佣金' }
      return typeMap[type] || '未知'
    },

    getFundsSourceTagType(type) {
      const typeMap = { 1: 'primary', 2: 'success', 3: 'warning' }
      return typeMap[type] || 'info'
    },

    // 提交审核
    submitAudit() {
      auditWithdrawApply(this.currentWithdraw.id, this.auditForm).then(response => {
        if (this.auditForm.result === 1) {
          this.$message.success('审核通过成功')
        } else {
          this.$message.success('审核拒绝成功')
        }
        this.getList()
        this.refreshStatistics()
      }).catch(error => {
        this.$message.error('审核操作失败：' + (error.message || '未知错误'))
      })
    },

    // 状态流程说明
    getStatusDescription(status) {
      const descriptions = {
        0: '等待管理员审核',
        1: '审核通过，等待转账处理',
        2: '转账成功，资金已到账',
        3: '转账失败，请联系技术支持',
        4: '用户主动取消申请',
        5: '审核未通过，已通知用户'
      }
      return descriptions[status] || '状态异常'
    },

    // 添加状态变更历史查看
    handleViewStatusHistory(row) {
      // TODO: 实现状态变更历史查看功能
      this.$message.info('状态变更历史功能开发中')
    },

    // 添加提现配置管理跳转
    handleWithdrawConfig() {
      this.$router.push('/ums/invite/withdrawConfig')
    },

    // 添加用户余额查询
    handleCheckUserBalance(row) {
      // TODO: 查询用户当前可提现余额
      this.$message.info(`正在查询用户 ${row.userNickname || row.userPhone} 的余额信息`)
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
</style> 