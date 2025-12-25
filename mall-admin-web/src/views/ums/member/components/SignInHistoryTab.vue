<template>
  <div class="signin-history-container">
    <!-- 搜索筛选区域 -->
    <el-card class="filter-container" shadow="never">
      <el-form :inline="true" :model="queryParams" size="small" label-width="100px">
        <el-form-item label="签到日期：">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="yyyy-MM-dd"
            value-format="yyyy-MM-dd"
            @change="handleDateRangeChange">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="签到状态：">
          <el-select v-model="queryParams.status" placeholder="全部" clearable>
            <el-option label="正常签到" :value="1"></el-option>
            <el-option label="补签" :value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 签到统计信息 -->
    <el-card class="summary-container" shadow="never">
      <div class="summary-title">签到统计</div>
      <el-row :gutter="20" class="summary-row">
        <el-col :span="6">
          <div class="summary-item">
            <div class="summary-value">{{ signInSummary.totalDays || 0 }}</div>
            <div class="summary-label">总签到天数</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="summary-item">
            <div class="summary-value">{{ signInSummary.continuousDays || 0 }}</div>
            <div class="summary-label">连续签到天数</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="summary-item">
            <div class="summary-value">{{ signInSummary.thisMonthDays || 0 }}</div>
            <div class="summary-label">本月签到天数</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="summary-item">
            <div class="summary-value">{{ signInSummary.totalPoints || 0 }}</div>
            <div class="summary-label">累计获得积分</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 签到记录列表 -->
    <el-card class="table-container" shadow="never">
      <div slot="header" class="table-header">
        <span>签到记录</span>
        <el-button
          type="primary"
          size="small"
          icon="el-icon-plus"
          @click="showSupplementDialog"
          style="float: right;">
          管理员补签
        </el-button>
      </div>
      <el-table
        :data="signInList"
        v-loading="listLoading"
        border
        stripe
        style="width: 100%">
        <el-table-column label="签到日期" width="120" align="center">
          <template slot-scope="scope">
            {{ formatSigninDate(scope.row.signinDate) }}
          </template>
        </el-table-column>
        <el-table-column label="签到时间" width="160" align="center">
          <template slot-scope="scope">
            {{ formatDateTime(scope.row.signinTime) }}
          </template>
        </el-table-column>
        <el-table-column label="获得积分" width="100" align="center">
          <template slot-scope="scope">
            <span class="points-earned">+{{ scope.row.pointsEarned || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="连续签到天数" width="120" align="center">
          <template slot-scope="scope">
            {{ scope.row.continuousDays || 0 }}天
          </template>
        </el-table-column>
        <el-table-column label="周期签到天数" width="120" align="center">
          <template slot-scope="scope">
            {{ scope.row.cycleSigninDays || 0 }}天
          </template>
        </el-table-column>
        <el-table-column label="连签奖励" width="100" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.isRewardClaimed === 1" type="success" size="small">已领取</el-tag>
            <el-tag v-else type="info" size="small">未领取</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="客户端IP" width="130" align="center">
          <template slot-scope="scope">
            {{ scope.row.clientIp || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="签到月份" width="100" align="center">
          <template slot-scope="scope">
            {{ scope.row.signinMonth || '-' }}
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          layout="total, sizes, prev, pager, next, jumper"
          :page-sizes="[10, 20, 50]"
          :page-size="queryParams.pageSize"
          :current-page.sync="queryParams.pageNum"
          :total="total">
        </el-pagination>
      </div>
    </el-card>

    <!-- 补签对话框 -->
    <el-dialog
      title="管理员补签"
      :visible.sync="supplementDialog.visible"
      width="400px"
      @close="resetSupplementForm">
      <el-form :model="supplementForm" :rules="supplementRules" ref="supplementForm" label-width="100px">
        <el-form-item label="补签日期" prop="signinDate">
          <el-date-picker
            v-model="supplementForm.signinDate"
            type="date"
            placeholder="选择补签日期"
            format="yyyy-MM-dd"
            value-format="yyyy-MM-dd"
            style="width: 100%;">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="补签原因" prop="reason">
          <el-input
            v-model="supplementForm.reason"
            type="textarea"
            placeholder="请输入补签原因"
            :rows="3">
          </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="supplementDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="handleSupplement" :loading="supplementDialog.loading">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getSignInHistory, getSignInSummary, supplementSignIn } from '@/api/member'
import { formatDate } from '@/utils/index'

export default {
  name: 'SignInHistoryTab',
  props: {
    memberId: {
      type: [String, Number],
      required: true
    }
  },
  data() {
    return {
      listLoading: false,
      signInList: [],
      signInSummary: {},
      total: 0,
      dateRange: [],
      queryParams: {
        pageNum: 1,
        pageSize: 20,
        memberId: null,
        startDate: null,
        endDate: null,
        status: null
      },
      // 补签相关
      supplementDialog: {
        visible: false,
        loading: false
      },
      supplementForm: {
        signinDate: '',
        reason: ''
      },
      supplementRules: {
        signinDate: [
          { required: true, message: '请选择补签日期', trigger: 'change' }
        ],
        reason: [
          { required: true, message: '请输入补签原因', trigger: 'blur' },
          { min: 2, max: 100, message: '补签原因长度在 2 到 100 个字符', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    memberId: {
      handler(newVal) {
        if (newVal) {
          this.queryParams.memberId = newVal
          this.loadSignInData()
        }
      },
      immediate: true
    }
  },
  methods: {
    formatDate,

    // 加载签到数据
    loadSignInData() {
      this.getSignInSummary()
      this.getSignInHistory()
    },

    // 获取签到统计信息
    async getSignInSummary() {
      try {
        const response = await getSignInSummary(this.memberId)
        if (response.code === 200) {
          this.signInSummary = response.data || {}
        }
      } catch (error) {
        console.error('获取签到统计失败:', error)
      }
    },

    // 获取签到记录列表
    async getSignInHistory() {
      this.listLoading = true
      try {
        const params = { ...this.queryParams }
        const response = await getSignInHistory(params)
        if (response.code === 200) {
          this.signInList = response.data.list || []
          this.total = response.data.total || 0
        } else {
          this.$message.error(response.message || '获取签到记录失败')
        }
      } catch (error) {
        console.error('获取签到记录失败:', error)
        this.$message.error('获取签到记录失败')
      } finally {
        this.listLoading = false
      }
    },

    // 处理日期范围变化
    handleDateRangeChange(dateRange) {
      if (dateRange && dateRange.length === 2) {
        this.queryParams.startDate = dateRange[0]
        this.queryParams.endDate = dateRange[1]
      } else {
        this.queryParams.startDate = null
        this.queryParams.endDate = null
      }
    },

    // 处理搜索
    handleSearch() {
      this.queryParams.pageNum = 1
      this.getSignInHistory()
    },

    // 重置搜索
    handleReset() {
      this.dateRange = []
      this.queryParams = {
        pageNum: 1,
        pageSize: 20,
        memberId: this.memberId,
        startDate: null,
        endDate: null,
        status: null
      }
      this.getSignInHistory()
    },

    // 分页大小变化
    handleSizeChange(size) {
      this.queryParams.pageSize = size
      this.queryParams.pageNum = 1
      this.getSignInHistory()
    },

    // 页码变化
    handleCurrentChange(page) {
      this.queryParams.pageNum = page
      this.getSignInHistory()
    },

    // 格式化签到日期
    formatSigninDate(date) {
      if (!date) return '-'
      
      try {
        // 如果已经是 YYYY-MM-DD 格式的字符串，直接返回
        if (typeof date === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(date)) {
          return date
        }
        
        // 尝试转换为 Date 对象并格式化
        const dateObj = new Date(date)
        if (isNaN(dateObj.getTime())) {
          return '-'
        }
        
        const year = dateObj.getFullYear()
        const month = String(dateObj.getMonth() + 1).padStart(2, '0')
        const day = String(dateObj.getDate()).padStart(2, '0')
        
        return `${year}-${month}-${day}`
      } catch (error) {
        console.error('日期格式化错误:', error, date)
        return '-'
      }
    },

    // 格式化日期时间
    formatDateTime(dateTime) {
      if (!dateTime) return '-'
      
      try {
        const dateObj = new Date(dateTime)
        if (isNaN(dateObj.getTime())) {
          return '-'
        }
        
        const year = dateObj.getFullYear()
        const month = String(dateObj.getMonth() + 1).padStart(2, '0')
        const day = String(dateObj.getDate()).padStart(2, '0')
        const hours = String(dateObj.getHours()).padStart(2, '0')
        const minutes = String(dateObj.getMinutes()).padStart(2, '0')
        const seconds = String(dateObj.getSeconds()).padStart(2, '0')
        
        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
      } catch (error) {
        console.error('日期时间格式化错误:', error, dateTime)
        return '-'
      }
    },

    // 显示补签对话框
    showSupplementDialog() {
      this.supplementDialog.visible = true
    },

    // 重置补签表单
    resetSupplementForm() {
      if (this.$refs.supplementForm) {
        this.$refs.supplementForm.resetFields()
      }
      this.supplementForm = {
        signinDate: '',
        reason: ''
      }
    },

    // 处理补签
    async handleSupplement() {
      try {
        await this.$refs.supplementForm.validate()
        
        this.supplementDialog.loading = true
        
        const response = await supplementSignIn(
          this.memberId,
          this.supplementForm.signinDate,
          this.supplementForm.reason
        )
        
        if (response.code === 200) {
          this.$message.success('补签成功')
          this.supplementDialog.visible = false
          this.resetSupplementForm()
          // 刷新数据
          this.loadSignInData()
        } else {
          // 显示后端返回的具体错误信息
          this.$message.error(response.message || '补签失败')
        }
      } catch (error) {
        console.error('补签失败:', error)
        
        // 简化的错误处理逻辑
        let errorMessage = '补签失败，请重试'
        
        if (error.response && error.response.data && error.response.data.message) {
          errorMessage = error.response.data.message
        } else if (error.message) {
          errorMessage = error.message
        }
        
        this.$message.error(errorMessage)
      } finally {
        this.supplementDialog.loading = false
      }
    }
  }
}
</script>

<style scoped>
.signin-history-container {
  padding: 10px;
}

.filter-container {
  margin-bottom: 20px;
}

.summary-container {
  margin-bottom: 20px;
}

.summary-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #303133;
}

.summary-row {
  text-align: center;
}

.summary-item {
  padding: 10px;
}

.summary-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 5px;
}

.summary-label {
  font-size: 14px;
  color: #909399;
}

.table-container {
  margin-bottom: 20px;
}

.table-header {
  font-weight: bold;
  color: #303133;
}

.points-earned {
  color: #67C23A;
  font-weight: bold;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .summary-row .el-col {
    margin-bottom: 10px;
  }
  
  .summary-value {
    font-size: 20px;
  }
}
</style> 