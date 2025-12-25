<template>
  <div class="integration-manage-tab">
    <!-- 积分操作 -->
    <el-card header="积分操作" style="margin-bottom: 20px;">
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="current-integration">
            <h3>当前积分：<span class="integration-value">{{ currentIntegration }}</span></h3>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="integration-actions">
            <el-button type="primary" @click="showGiveDialog">发放积分</el-button>
            <el-button type="warning" @click="showDeductDialog">扣减积分</el-button>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 积分历史 -->
    <el-card header="积分变更历史">
      <el-table :data="historyList" v-loading="loading" style="width: 100%">
        <el-table-column prop="createTime" label="变更时间" width="150">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="changeType" label="变更类型" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.changeType === 0 ? 'success' : 'danger'">
              {{ scope.row.changeType === 0 ? '增加' : '减少' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="changeCount" label="变更数量" width="100">
          <template slot-scope="scope">
            <span :class="scope.row.changeType === 0 ? 'increase' : 'decrease'">
              {{ scope.row.changeType === 0 ? '+' : '-' }}{{ scope.row.changeCount }}
            </span>
          </template>
        </el-table-column>
        
        <el-table-column prop="operateNote" label="备注" min-width="200">
          <template slot-scope="scope">
            {{ scope.row.operateNote || '-' }}
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="queryForm.pageNum"
          :page-size="queryForm.pageSize"
          :total="total"
          layout="total, sizes, prev, pager, next">
        </el-pagination>
      </div>
    </el-card>

    <!-- 发放积分弹窗 -->
    <el-dialog title="发放积分" :visible.sync="giveDialogVisible" width="400px">
      <el-form ref="giveForm" :model="giveForm" :rules="giveRules" label-width="80px">
        <el-form-item label="积分数量" prop="integration">
          <el-input-number
            v-model="giveForm.integration"
            :min="1"
            :max="99999"
            style="width: 100%;"
            placeholder="请输入要发放的积分数量">
          </el-input-number>
        </el-form-item>
        
        <el-form-item label="发放原因" prop="reason">
          <el-input
            v-model="giveForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入发放积分的原因">
          </el-input>
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="giveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmGive" :loading="giving">确定发放</el-button>
      </div>
    </el-dialog>

    <!-- 扣减积分弹窗 -->
    <el-dialog title="扣减积分" :visible.sync="deductDialogVisible" width="400px">
      <el-form ref="deductForm" :model="deductForm" :rules="deductRules" label-width="80px">
        <el-form-item label="积分数量" prop="integration">
          <el-input-number
            v-model="deductForm.integration"
            :min="1"
            :max="currentIntegration"
            style="width: 100%;"
            placeholder="请输入要扣减的积分数量">
          </el-input-number>
          <div class="tips">当前积分：{{ currentIntegration }}</div>
        </el-form-item>
        
        <el-form-item label="扣减原因" prop="reason">
          <el-input
            v-model="deductForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入扣减积分的原因">
          </el-input>
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="deductDialogVisible = false">取消</el-button>
        <el-button type="warning" @click="confirmDeduct" :loading="deducting">确定扣减</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { 
  getMemberIntegrationHistory, 
  giveMemberIntegration,
  deductMemberIntegration 
} from '@/api/member'
import { formatDate } from '@/utils/index'

export default {
  name: 'IntegrationManageTab',
  props: {
    memberId: {
      type: [String, Number],
      required: true
    },
    currentIntegration: {
      type: Number,
      default: 0
    }
  },
  data() {
    return {
      loading: false,
      historyList: [],
      total: 0,
      queryForm: {
        pageNum: 1,
        pageSize: 20
      },
      giveDialogVisible: false,
      deductDialogVisible: false,
      giving: false,
      deducting: false,
      giveForm: {
        integration: 0,
        reason: ''
      },
      deductForm: {
        integration: 0,
        reason: ''
      },
      giveRules: {
        integration: [
          { required: true, message: '请输入积分数量', trigger: 'blur' },
          { type: 'number', min: 1, message: '积分数量必须大于0', trigger: 'blur' }
        ],
        reason: [
          { required: true, message: '请输入发放原因', trigger: 'blur' }
        ]
      },
      deductRules: {
        integration: [
          { required: true, message: '请输入积分数量', trigger: 'blur' },
          { type: 'number', min: 1, message: '积分数量必须大于0', trigger: 'blur' }
        ],
        reason: [
          { required: true, message: '请输入扣减原因', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    formatDate,
    
    async loadData() {
      await this.loadHistoryData()
    },
    
    async loadHistoryData() {
      this.loading = true
      try {
        const response = await getMemberIntegrationHistory(this.memberId, this.queryForm)
        if (response.code === 200) {
          this.historyList = response.data.list || []
          this.total = response.data.total || 0
        } else {
          this.$message.error(response.message || '获取积分历史失败')
        }
      } catch (error) {
        console.error('获取积分历史失败:', error)
        this.$message.error('获取积分历史失败')
      } finally {
        this.loading = false
      }
    },
    
    handleSizeChange(val) {
      this.queryForm.pageSize = val
      this.queryForm.pageNum = 1
      this.loadHistoryData()
    },
    
    handleCurrentChange(val) {
      this.queryForm.pageNum = val
      this.loadHistoryData()
    },
    
    showGiveDialog() {
      this.giveForm = {
        integration: 0,
        reason: ''
      }
      this.giveDialogVisible = true
    },
    
    showDeductDialog() {
      this.deductForm = {
        integration: 0,
        reason: ''
      }
      this.deductDialogVisible = true
    },
    
    async confirmGive() {
      try {
        await this.$refs.giveForm.validate()
        this.giving = true
        
        const response = await giveMemberIntegration(
          this.memberId,
          this.giveForm.integration,
          this.giveForm.reason
        )
        
        if (response.code === 200) {
          this.$message.success('积分发放成功')
          this.giveDialogVisible = false
          this.loadData()
          this.$emit('refresh')
        } else {
          this.$message.error(response.message || '积分发放失败')
        }
      } catch (error) {
        console.error('积分发放失败:', error)
        if (error !== false) {
          this.$message.error('积分发放失败')
        }
      } finally {
        this.giving = false
      }
    },

    async confirmDeduct() {
      try {
        await this.$refs.deductForm.validate()
        
        if (this.deductForm.integration > this.currentIntegration) {
          this.$message.error('扣减积分不能超过当前积分')
          return
        }
        
        this.deducting = true
        
        const response = await deductMemberIntegration(
          this.memberId,
          this.deductForm.integration,
          this.deductForm.reason
        )
        
        if (response.code === 200) {
          this.$message.success('积分扣减成功')
          this.deductDialogVisible = false
          this.loadData()
          this.$emit('refresh')
        } else {
          this.$message.error(response.message || '积分扣减失败')
        }
      } catch (error) {
        console.error('积分扣减失败:', error)
        if (error !== false) {
          this.$message.error('积分扣减失败')
        }
      } finally {
        this.deducting = false
      }
    }
  }
}
</script>

<style scoped>
.integration-manage-tab {
  padding: 20px 0;
}

.current-integration {
  text-align: center;
}

.integration-value {
  color: #409EFF;
  font-size: 32px;
  font-weight: bold;
}

.integration-actions {
  text-align: center;
}

.integration-actions .el-button {
  margin: 0 10px;
}

.increase {
  color: #67C23A;
  font-weight: bold;
}

.decrease {
  color: #F56C6C;
  font-weight: bold;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.tips {
  color: #909399;
  font-size: 12px;
  margin-top: 5px;
}
</style>
