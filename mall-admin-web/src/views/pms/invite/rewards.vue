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
          <el-form-item label="用户ID：">
            <el-input
              v-model="listQuery.userId"
              class="input-width"
              placeholder="用户ID">
            </el-input>
          </el-form-item>
          <el-form-item label="用户类型：">
            <el-select
              v-model="listQuery.userType"
              placeholder="全部"
              clearable
              class="input-width">
              <el-option
                v-for="item in userTypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="奖励类型：">
            <el-select
              v-model="listQuery.rewardType"
              placeholder="全部"
              clearable
              class="input-width">
              <el-option
                v-for="item in rewardTypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="触发类型：">
            <el-select
              v-model="listQuery.triggerType"
              placeholder="全部"
              clearable
              class="input-width">
              <el-option
                v-for="item in triggerTypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="状态：">
            <el-select
              v-model="listQuery.status"
              placeholder="全部"
              clearable
              class="input-width">
              <el-option
                v-for="item in statusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="创建时间：">
            <el-date-picker
              class="input-width"
              v-model="listQuery.createTime"
              value-format="yyyy-MM-dd"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期">
            </el-date-picker>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    
    <!-- 数据概览 -->
    <el-card class="box-card" shadow="never" style="margin-top: 20px;">
      <div slot="header" class="clearfix">
        <span>数据概览</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="getSummary">刷新</el-button>
      </div>
      <el-row :gutter="20">
        <el-col :span="5">
          <div class="stat-item">
            <div class="stat-value">{{ rewardSummary.totalRewards || 0 }}</div>
            <div class="stat-label">总奖励数</div>
          </div>
        </el-col>
        <el-col :span="5">
          <div class="stat-item">
            <div class="stat-value">{{ rewardSummary.sentRewards || 0 }}</div>
            <div class="stat-label">成功发放</div>
          </div>
        </el-col>
        <el-col :span="5">
          <div class="stat-item">
            <div class="stat-value">{{ rewardSummary.failedRewards || 0 }}</div>
            <div class="stat-label">发放失败</div>
          </div>
        </el-col>
        <el-col :span="5">
          <div class="stat-item">
            <div class="stat-value">{{ rewardSummary.pendingRewards || 0 }}</div>
            <div class="stat-label">处理中</div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-item">
            <div class="stat-value">{{ ((rewardSummary.successRate || 0) * 100).toFixed(2) }}%</div>
            <div class="stat-label">成功率</div>
          </div>
        </el-col>
      </el-row>
    </el-card>
    
    <el-card class="operate-container" shadow="never">
      <div class="batch-operate-container">
        <el-select
          size="small"
          v-model="operateType" placeholder="批量操作">
          <el-option
            v-for="item in operateOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
        <el-button
          style="margin-left: 20px"
          class="search-button"
          @click="handleBatchOperate()"
          type="primary"
          size="small">
          确定
        </el-button>
        <el-button
          style="margin-left: 20px"
          class="search-button"
          @click="handleExport()"
          type="success"
          size="small">
          导出记录
        </el-button>
      </div>
    </el-card>
    
    <div class="table-container">
      <el-table ref="rewardTable"
                :data="list"
                style="width: 100%;"
                @selection-change="handleSelectionChange"
                v-loading="listLoading"
                border>
        <el-table-column type="selection" width="60" align="center"></el-table-column>
        <el-table-column label="用户ID" width="80" align="center">
          <template slot-scope="scope">{{ scope.row.userId }}</template>
        </el-table-column>
        <el-table-column label="用户类型" width="100" align="center">
          <template slot-scope="scope">{{ scope.row.userTypeText }}</template>
        </el-table-column>
        <el-table-column label="奖励类型" width="100" align="center">
          <template slot-scope="scope">{{ scope.row.rewardTypeText }}</template>
        </el-table-column>
        <el-table-column label="奖励值" width="100" align="center">
          <template slot-scope="scope">{{ scope.row.rewardValue }}</template>
        </el-table-column>
        <el-table-column label="触发类型" width="100" align="center">
          <template slot-scope="scope">{{ scope.row.triggerTypeText }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getStatusTagType(scope.row.status)">
              {{ scope.row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="失败原因" width="150" align="center">
          <template slot-scope="scope">{{ scope.row.failReason || '-' }}</template>
        </el-table-column>
        <el-table-column label="备注" width="150" align="center">
          <template slot-scope="scope">{{ scope.row.remark || '-' }}</template>
        </el-table-column>
        <el-table-column label="创建时间" width="160" align="center">
          <template slot-scope="scope">{{ scope.row.createTime | formatDateTime }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              @click="handleViewDetail(scope.$index, scope.row)">查看详情
            </el-button>
            <el-button
              v-if="scope.row.status === 2"
              size="mini"
              type="warning"
              @click="handleRetryReward(scope.$index, scope.row)">重试发放
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
        :page-sizes="[5,10,15]"
        :total="total">
      </el-pagination>
    </div>
    
    <!-- 奖励详情对话框 -->
    <el-dialog
      title="奖励详情"
      :visible.sync="detailDialogVisible"
      width="600px">
      <el-form :model="rewardDetail" label-width="120px">
        <el-form-item label="用户ID：">
          <span>{{ rewardDetail.userId }}</span>
        </el-form-item>
        <el-form-item label="用户类型：">
          <span>{{ rewardDetail.userTypeText }}</span>
        </el-form-item>
        <el-form-item label="奖励类型：">
          <span>{{ rewardDetail.rewardTypeText }}</span>
        </el-form-item>
        <el-form-item label="奖励值：">
          <span>{{ rewardDetail.rewardValue }}</span>
        </el-form-item>
        <el-form-item label="触发类型：">
          <span>{{ rewardDetail.triggerTypeText }}</span>
        </el-form-item>
        <el-form-item label="状态：">
          <el-tag :type="getStatusTagType(rewardDetail.status)">
            {{ rewardDetail.statusText }}
          </el-tag>
        </el-form-item>
        <el-form-item label="失败原因：">
          <span>{{ rewardDetail.failReason || '-' }}</span>
        </el-form-item>
        <el-form-item label="备注：">
          <span>{{ rewardDetail.remark || '-' }}</span>
        </el-form-item>
        <el-form-item label="创建时间：">
          <span>{{ rewardDetail.createTime | formatDateTime }}</span>
        </el-form-item>
        <el-form-item label="更新时间：">
          <span>{{ rewardDetail.updateTime | formatDateTime }}</span>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">取 消</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
  import { getInviteRewards, getInviteRewardDetail, retrySendReward, batchSendReward, exportRewardRecords, getRewardSummary } from '@/api/invite'
  import { formatStandardDateTime } from '@/utils/date'

  export default {
    name: 'inviteRewardList',
    data() {
      return {
        listQuery: {
          pageNum: 1,
          pageSize: 5,
          userId: null,
          userType: null,
          rewardType: null,
          triggerType: null,
          status: null,
          createTime: null
        },
        list: null,
        total: null,
        listLoading: false,
        multipleSelection: [],
        operateType: null,
        detailDialogVisible: false,
        rewardDetail: {},
        rewardSummary: {},
        userTypeOptions: [
          { label: '邀请者', value: 1 },
          { label: '被邀请者', value: 2 }
        ],
        rewardTypeOptions: [
          { label: '积分', value: 1 },
          { label: '优惠券', value: 2 },
          { label: '现金红包', value: 3 }
        ],
        triggerTypeOptions: [
          { label: '注册完成', value: 1 },
          { label: '首单完成', value: 2 },
          { label: '复购完成', value: 3 }
        ],
        statusOptions: [
          { label: '处理中', value: 0 },
          { label: '已发放', value: 1 },
          { label: '发放失败', value: 2 }
        ],
        operateOptions: [
          { label: '批量重试发放', value: 1 }
        ]
      }
    },
    created() {
      this.getList()
      this.getSummary()
    },
    filters: {
      formatDateTime(time) {
        return formatStandardDateTime(time)
      }
    },
    methods: {
      handleResetSearch() {
        this.listQuery = {
          pageNum: 1,
          pageSize: 5,
          userId: null,
          userType: null,
          rewardType: null,
          triggerType: null,
          status: null,
          createTime: null
        }
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
      handleViewDetail(index, row) {
        getInviteRewardDetail(row.id).then(response => {
          this.rewardDetail = response.data
          this.detailDialogVisible = true
        })
      },
      handleRetryReward(index, row) {
        this.$confirm('确定要重试发放该奖励吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          retrySendReward(row.id).then(response => {
            this.$message({
              type: 'success',
              message: '重试发放成功！'
            })
            this.getList()
          })
        })
      },
      handleBatchOperate() {
        if (this.multipleSelection < 1) {
          this.$message({
            message: '请选择要操作的奖励',
            type: 'warning'
          })
          return
        }
        if (this.operateType === null) {
          this.$message({
            message: '请选择操作类型',
            type: 'warning'
          })
          return
        }
        if (this.operateType === 1) {
          // 批量重试发放
          this.$confirm('确定要批量重试发放选中的奖励吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            let ids = this.multipleSelection.map(item => item.id)
            batchSendReward({ ids: ids }).then(response => {
              this.$message({
                type: 'success',
                message: '批量操作成功！'
              })
              this.getList()
            })
          })
        }
      },
      handleExport() {
        this.$confirm('确定要导出奖励记录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          let params = { ...this.listQuery }
          if (params.createTime) {
            params.startTime = params.createTime[0]
            params.endTime = params.createTime[1]
            delete params.createTime
          }
          exportRewardRecords(params).then(response => {
            this.$message({
              type: 'success',
              message: '导出成功！'
            })
          })
        })
      },
      getList() {
        this.listLoading = true
        let params = { ...this.listQuery }
        if (params.createTime) {
          params.startTime = params.createTime[0]
          params.endTime = params.createTime[1]
          delete params.createTime
        }
        getInviteRewards(params).then(response => {
          this.listLoading = false
          this.list = response.data.list
          this.total = parseInt(response.data.total)
        })
      },
      getSummary() {
        getRewardSummary().then(response => {
          this.rewardSummary = response.data
        })
      },
      getStatusTagType(status) {
        switch (status) {
          case 0: return 'info'    // 处理中
          case 1: return 'success' // 成功
          case 2: return 'danger'  // 失败
          default: return 'info'
        }
      }
    }
  }
</script>

<style scoped>
  .input-width {
    width: 203px;
  }
  
  .box-card {
    margin-bottom: 20px;
  }
  
  .stat-item {
    text-align: center;
    padding: 24px 20px;
    background: #FFFFFF;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    transition: transform 0.2s ease;
  }
  
  .stat-item:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
  }
  
  .stat-value {
    font-size: 28px;
    font-weight: bold;
    color: #409EFF;
    margin-bottom: 10px;
    text-shadow: 0 1px 2px rgba(0,0,0,0.1);
  }
  
  .stat-label {
    font-size: 14px;
    color: #666;
    font-weight: 500;
  }
</style> 