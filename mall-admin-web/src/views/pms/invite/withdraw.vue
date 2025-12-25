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
      <div>
        <el-form :inline="true" :model="listQuery" size="small" label-width="140px">
          <el-form-item label="提现状态：">
            <el-select v-model="listQuery.status" placeholder="全部" clearable class="input-width">
              <el-option label="全部" value=""></el-option>
              <el-option label="待审核" :value="0"></el-option>
              <el-option label="审核通过" :value="1"></el-option>
              <el-option label="提现成功" :value="2"></el-option>
              <el-option label="提现失败" :value="3"></el-option>
              <el-option label="已取消" :value="4"></el-option>
              <el-option label="审核拒绝" :value="5"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="用户手机号：">
            <el-input v-model="listQuery.userPhone" class="input-width" placeholder="用户手机号"></el-input>
          </el-form-item>
          <el-form-item label="提现编号：">
            <el-input v-model="listQuery.withdrawSn" class="input-width" placeholder="提现编号"></el-input>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    
    <div class="table-container">
      <el-table ref="withdrawTable"
                :data="list"
                style="width: 100%;"
                v-loading="listLoading"
                border>
        <el-table-column label="提现编号" width="180" align="center">
          <template slot-scope="scope">{{ scope.row.withdrawSn }}</template>
        </el-table-column>
        <el-table-column label="用户信息" width="200" align="center">
          <template slot-scope="scope">
            <div>
              <img v-if="scope.row.userIcon" :src="scope.row.userIcon" style="width: 30px; height: 30px; border-radius: 15px; margin-right: 8px;" />
              <div>{{ scope.row.userNickname }}</div>
              <div style="color: #999; font-size: 12px;">{{ scope.row.userPhone }}</div>
              <div style="color: #999; font-size: 12px;">ID: {{ scope.row.userId }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="申请金额" width="100" align="center">
          <template slot-scope="scope">
            <div style="color: #E6A23C; font-weight: bold;">¥{{ scope.row.applyAmount }}</div>
          </template>
        </el-table-column>
        <el-table-column label="手续费" width="80" align="center">
          <template slot-scope="scope">
            <div style="color: #F56C6C;">-¥{{ scope.row.feeAmount }}</div>
          </template>
        </el-table-column>
        <el-table-column label="实际到账" width="100" align="center">
          <template slot-scope="scope">
            <div style="color: #67C23A; font-weight: bold;">¥{{ scope.row.actualAmount }}</div>
          </template>
        </el-table-column>
        <el-table-column label="提现方式" width="120" align="center">
          <template slot-scope="scope">
            <div>{{ scope.row.withdrawTypeText }}</div>
            <div style="color: #999; font-size: 12px;">{{ scope.row.accountName }}</div>
          </template>
        </el-table-column>
        <el-table-column label="申请时间" width="160" align="center">
          <template slot-scope="scope">{{ formatDateTime(scope.row.applyTime) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getStatusTagType(scope.row.status)">
              {{ scope.row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" align="center">
          <template slot-scope="scope">
            <el-button size="mini" @click="handleViewDetail(scope.row)">查看详情</el-button>
            <el-button 
              v-if="scope.row.status === 0" 
              size="mini" 
              type="success" 
              @click="handleApprove(scope.row)"
            >
              审核通过
            </el-button>
            <el-button 
              v-if="scope.row.status === 0" 
              size="mini" 
              type="danger" 
              @click="handleReject(scope.row)"
            >
              审核拒绝
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <!-- 分页组件 -->
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

    <!-- 详情对话框 -->
    <el-dialog title="提现详情" :visible.sync="detailVisible" width="800px">
      <div v-if="currentDetail">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="detail-item">
              <label>提现编号：</label>
              <span>{{ currentDetail.withdrawSn }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="detail-item">
              <label>会员编号：</label>
              <span>{{ currentDetail.memberCode }}</span>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="detail-item">
              <label>用户昵称：</label>
              <span>{{ currentDetail.userNickname }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="detail-item">
              <label>用户手机：</label>
              <span>{{ currentDetail.userPhone }}</span>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="detail-item">
              <label>申请金额：</label>
              <span style="color: #E6A23C; font-weight: bold;">¥{{ currentDetail.applyAmount }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="detail-item">
              <label>手续费：</label>
              <span style="color: #F56C6C;">¥{{ currentDetail.feeAmount }}</span>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="detail-item">
              <label>实际到账：</label>
              <span style="color: #67C23A; font-weight: bold;">¥{{ currentDetail.actualAmount }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="detail-item">
              <label>提现方式：</label>
              <span>{{ currentDetail.withdrawTypeText }}</span>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="detail-item">
              <label>账户名称：</label>
              <span>{{ currentDetail.accountName }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="detail-item">
              <label>提现账户：</label>
              <span>{{ currentDetail.withdrawAccount }}</span>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="detail-item">
              <label>申请时间：</label>
              <span>{{ formatDateTime(currentDetail.applyTime) }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="detail-item">
              <label>预期到账时间：</label>
              <span>{{ formatDateTime(currentDetail.expectedArrivalTime) }}</span>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <div class="detail-item">
              <label>备注信息：</label>
              <span>{{ currentDetail.remark || '-' }}</span>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="detail-item">
              <label>当前状态：</label>
              <el-tag :type="getStatusTagType(currentDetail.status)">
                {{ currentDetail.statusText }}
              </el-tag>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="detail-item">
              <label>更新时间：</label>
              <span>{{ formatDateTime(currentDetail.updatedAt) }}</span>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-dialog>

    <!-- 审核对话框 -->
    <el-dialog :title="approveForm.type === 'approve' ? '审核通过' : '审核拒绝'" :visible.sync="approveVisible" width="500px">
      <el-form :model="approveForm" label-width="100px">
        <el-form-item label="提现编号：">
          <span>{{ approveForm.withdrawSn }}</span>
        </el-form-item>
        <el-form-item label="申请金额：">
          <span style="color: #E6A23C; font-weight: bold;">¥{{ approveForm.applyAmount }}</span>
        </el-form-item>
        <el-form-item label="实际到账：">
          <span style="color: #67C23A; font-weight: bold;">¥{{ approveForm.actualAmount }}</span>
        </el-form-item>
        <el-form-item label="审核备注：">
          <el-input 
            v-model="approveForm.remark" 
            type="textarea" 
            :placeholder="approveForm.type === 'approve' ? '审核通过备注（可选）' : '拒绝原因（必填）'"
            :rows="3">
          </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="approveVisible = false">取消</el-button>
        <el-button 
          :type="approveForm.type === 'approve' ? 'success' : 'danger'" 
          @click="handleConfirmApprove"
          :loading="approveLoading"
        >
          {{ approveForm.type === 'approve' ? '确认通过' : '确认拒绝' }}
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import { getWithdrawApplies, getWithdrawApplyDetail, approveWithdrawApply } from '@/api/invite'

  export default {
    name: 'withdrawList',
    data() {
      return {
        listQuery: {
          pageNum: 1,
          pageSize: 5,
          status: '',
          userPhone: '',
          withdrawSn: ''
        },
        list: [],
        total: 0,
        listLoading: false,
        detailVisible: false,
        currentDetail: null,
        approveVisible: false,
        approveLoading: false,
        approveForm: {
          id: '',
          withdrawSn: '',
          applyAmount: '',
          actualAmount: '',
          type: '', // 'approve' 或 'reject'
          remark: ''
        }
      }
    },
    created() {
      this.getList()
    },
    methods: {
      handleResetSearch() {
        this.listQuery = {
          pageNum: 1,
          pageSize: 5,
          status: '',
          userPhone: '',
          withdrawSn: ''
        }
        this.getList()
      },
      handleSearchList() {
        this.listQuery.pageNum = 1
        this.getList()
      },
      getList() {
        this.listLoading = true
        getWithdrawApplies(this.listQuery).then(response => {
          this.listLoading = false
          this.list = response.data.list || []
          this.total = response.data.total || 0
        }).catch(() => {
          this.listLoading = false
        })
      },
      handleSizeChange(val) {
        this.listQuery.pageSize = val
        this.getList()
      },
      handleCurrentChange(val) {
        this.listQuery.pageNum = val
        this.getList()
      },
      getStatusTagType(status) {
        switch (status) {
          case 0: return 'warning'  // 待审核
          case 1: return 'success'  // 审核通过
          case 2: return 'success'  // 提现成功
          case 3: return 'danger'   // 提现失败
          case 4: return 'info'     // 已取消
          case 5: return 'danger'   // 审核拒绝
          default: return 'info'
        }
      },
      formatDateTime(datetime) {
        if (!datetime) return '-'
        return new Date(datetime).toLocaleString('zh-CN')
      },
      handleViewDetail(row) {
        this.currentDetail = row
        this.detailVisible = true
      },
      handleApprove(row) {
        this.approveForm = {
          id: row.id,
          withdrawSn: row.withdrawSn,
          applyAmount: row.applyAmount,
          actualAmount: row.actualAmount,
          type: 'approve',
          remark: ''
        }
        this.approveVisible = true
      },
      handleReject(row) {
        this.approveForm = {
          id: row.id,
          withdrawSn: row.withdrawSn,
          applyAmount: row.applyAmount,
          actualAmount: row.actualAmount,
          type: 'reject',
          remark: ''
        }
        this.approveVisible = true
      },
      handleConfirmApprove() {
        if (this.approveForm.type === 'reject' && !this.approveForm.remark.trim()) {
          this.$message.error('拒绝审核必须填写拒绝原因')
          return
        }
        
        this.approveLoading = true
        const data = {
          status: this.approveForm.type === 'approve' ? 1 : 2,
          remark: this.approveForm.remark
        }
        
        approveWithdrawApply(this.approveForm.id, data).then(response => {
          this.approveLoading = false
          this.approveVisible = false
          this.$message.success(this.approveForm.type === 'approve' ? '审核通过成功' : '审核拒绝成功')
          this.getList()
        }).catch(error => {
          this.approveLoading = false
          this.$message.error(error.message || '操作失败')
        })
      }
    }
  }
</script>

<style scoped>
  .input-width {
    width: 203px;
  }
  
  .pagination-container {
    padding: 32px 16px;
  }
  
  .detail-item {
    margin-bottom: 15px;
  }
  
  .detail-item label {
    font-weight: bold;
    margin-right: 10px;
    color: #606266;
  }
</style>