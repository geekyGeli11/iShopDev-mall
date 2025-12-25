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
          <el-form-item label="姓名搜索：">
            <el-input 
              v-model="listQuery.realName" 
              class="input-width" 
              placeholder="请输入真实姓名" 
              clearable>
            </el-input>
          </el-form-item>
          <el-form-item label="手机号搜索：">
            <el-input 
              v-model="listQuery.phone" 
              class="input-width" 
              placeholder="请输入手机号" 
              clearable>
            </el-input>
          </el-form-item>
          <el-form-item label="申请状态：">
            <el-select v-model="listQuery.status" placeholder="全部" clearable class="input-width">
              <el-option label="待审核" :value="0"></el-option>
              <el-option label="已通过" :value="1"></el-option>
              <el-option label="已拒绝" :value="2"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="申请时间：">
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
      <span>申请列表</span>
      <el-button 
        size="mini" 
        class="btn-add" 
        @click="handleBatchReview(1)" 
        style="margin-left: 20px"
        :disabled="multipleSelection.length === 0">
        批量通过
      </el-button>
      <el-button 
        size="mini" 
        class="btn-add" 
        @click="handleBatchReview(2)"
        :disabled="multipleSelection.length === 0">
        批量拒绝
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
            <div class="statistic-label">总申请数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="statistic-card">
            <div class="statistic-value">{{ statistics.pendingCount || 0 }}</div>
            <div class="statistic-label">待审核</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="statistic-card">
            <div class="statistic-value">{{ statistics.approvedCount || 0 }}</div>
            <div class="statistic-label">已通过</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="statistic-card">
            <div class="statistic-value">{{ statistics.rejectedCount || 0 }}</div>
            <div class="statistic-label">已拒绝</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数据列表 -->
    <div class="table-container">
      <el-table 
        ref="applyTable"
        :data="list"
        style="width: 100%;"
        v-loading="listLoading" 
        border
        @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column label="编号" width="80" align="center">
          <template slot-scope="scope">{{ scope.row.id }}</template>
        </el-table-column>
        <el-table-column label="真实姓名" width="120" align="center">
          <template slot-scope="scope">{{ scope.row.realName || '-' }}</template>
        </el-table-column>
        <el-table-column label="手机号" width="130" align="center">
          <template slot-scope="scope">{{ scope.row.phone || '-' }}</template>
        </el-table-column>
        <el-table-column label="微信号" width="120" align="center">
          <template slot-scope="scope">{{ scope.row.wechat || '-' }}</template>
        </el-table-column>
        <el-table-column label="申请理由" width="200" align="center" show-overflow-tooltip>
          <template slot-scope="scope">{{ scope.row.applyReason || '-' }}</template>
        </el-table-column>
        <el-table-column label="推广经验" width="200" align="center" show-overflow-tooltip>
          <template slot-scope="scope">{{ scope.row.experience || '-' }}</template>
        </el-table-column>
        <el-table-column label="申请时间" width="160" align="center">
          <template slot-scope="scope">{{ scope.row.applyTime | formatDateTime }}</template>
        </el-table-column>
        <el-table-column label="审核时间" width="160" align="center">
          <template slot-scope="scope">{{ scope.row.reviewTime | formatDateTime }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag 
              :type="scope.row.status === 0 ? 'warning' : scope.row.status === 1 ? 'success' : 'danger'"
              size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button 
              size="mini"
              type="text"
              @click="handleViewDetail(scope.row)"
              v-if="scope.row.status === 0">
              审核
            </el-button>
            <el-button 
              size="mini"
              type="text"
              @click="handleViewDetail(scope.row)">
              查看详情
            </el-button>
            <el-button 
              size="mini"
              type="text"
              style="color: #f56c6c"
              @click="handleDelete(scope.row.id)"
              v-if="scope.row.status === 2">
              删除
            </el-button>
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

    <!-- 审核弹窗 -->
    <el-dialog
      title="申请审核"
      :visible.sync="reviewDialogVisible"
      width="800px">
      <el-form :model="reviewForm" label-width="120px" size="small">
        <el-row>
          <el-col :span="12">
            <el-form-item label="申请人：">
              <span>{{ currentApply.realName || '-' }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号：">
              <span>{{ currentApply.phone || '-' }}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="微信号：">
              <span>{{ currentApply.wechat || '-' }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="申请时间：">
              <span>{{ currentApply.applyTime | formatDateTime }}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="自我介绍：">
          <div style="max-height: 100px; overflow-y: auto; padding: 8px; border: 1px solid #dcdfe6; border-radius: 4px; background-color: #f5f7fa;">
            {{ currentApply.selfIntroduction || '-' }}
          </div>
        </el-form-item>
        <el-form-item label="申请理由：">
          <div style="max-height: 80px; overflow-y: auto; padding: 8px; border: 1px solid #dcdfe6; border-radius: 4px; background-color: #f5f7fa;">
            {{ currentApply.applyReason || '-' }}
          </div>
        </el-form-item>
        <el-form-item label="身份证照片：">
          <div style="display: flex; gap: 10px;">
            <div v-if="currentApply.idCardFrontImg">
              <div style="margin-bottom: 5px; font-size: 12px; color: #666;">正面</div>
              <img
                :src="currentApply.idCardFrontImg"
                style="width: 120px; height: 80px; border-radius: 4px; object-fit: cover; cursor: pointer; border: 1px solid #dcdfe6;"
                @click="previewImage(currentApply.idCardFrontImg)"
                alt="身份证正面">
            </div>
            <div v-if="currentApply.idCardBackImg">
              <div style="margin-bottom: 5px; font-size: 12px; color: #666;">反面</div>
              <img
                :src="currentApply.idCardBackImg"
                style="width: 120px; height: 80px; border-radius: 4px; object-fit: cover; cursor: pointer; border: 1px solid #dcdfe6;"
                @click="previewImage(currentApply.idCardBackImg)"
                alt="身份证反面">
            </div>
            <div v-if="!currentApply.idCardFrontImg && !currentApply.idCardBackImg">
              <span style="color: #999;">暂无身份证照片</span>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="私域影响力截图：">
          <div v-if="currentApply.influenceScreenshots && parseImageArray(currentApply.influenceScreenshots).length > 0">
            <div style="display: flex; flex-wrap: wrap; gap: 10px;">
              <img
                v-for="(img, index) in parseImageArray(currentApply.influenceScreenshots)"
                :key="index"
                :src="img"
                style="width: 100px; height: 100px; border-radius: 4px; object-fit: cover; cursor: pointer; border: 1px solid #dcdfe6;"
                @click="previewImage(img)"
                :alt="'私域影响力截图' + (index + 1)">
            </div>
          </div>
          <div v-else>
            <span style="color: #999;">暂无私域影响力截图</span>
          </div>
        </el-form-item>
        <el-form-item label="工作截图：">
          <div v-if="currentApply.workScreenshots && parseImageArray(currentApply.workScreenshots).length > 0">
            <div style="display: flex; flex-wrap: wrap; gap: 10px;">
              <img
                v-for="(img, index) in parseImageArray(currentApply.workScreenshots)"
                :key="index"
                :src="img"
                style="width: 100px; height: 100px; border-radius: 4px; object-fit: cover; cursor: pointer; border: 1px solid #dcdfe6;"
                @click="previewImage(img)"
                :alt="'工作截图' + (index + 1)">
            </div>
          </div>
          <div v-else>
            <span style="color: #999;">暂无工作截图</span>
          </div>
        </el-form-item>
        <el-form-item label="其他证明材料：">
          <div v-if="currentApply.otherCertificates && parseImageArray(currentApply.otherCertificates).length > 0">
            <div style="display: flex; flex-wrap: wrap; gap: 10px;">
              <img
                v-for="(img, index) in parseImageArray(currentApply.otherCertificates)"
                :key="index"
                :src="img"
                style="width: 100px; height: 100px; border-radius: 4px; object-fit: cover; cursor: pointer; border: 1px solid #dcdfe6;"
                @click="previewImage(img)"
                :alt="'其他证明材料' + (index + 1)">
            </div>
          </div>
          <div v-else>
            <span style="color: #999;">暂无其他证明材料</span>
          </div>
        </el-form-item>
        <el-form-item label="审核结果：">
          <el-radio-group v-model="reviewForm.status">
            <el-radio :label="1">通过</el-radio>
            <el-radio :label="2">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见：">
          <el-input
            v-model="reviewForm.reviewComment"
            type="textarea"
            :rows="4"
            placeholder="请输入审核意见"
            style="width: 400px">
          </el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="reviewDialogVisible = false" size="small">取 消</el-button>
        <el-button type="primary" @click="handleReviewConfirm()" size="small">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 批量审核弹窗 -->
    <el-dialog
      title="批量审核"
      :visible.sync="batchReviewDialogVisible"
      width="500px">
      <el-form :model="batchReviewForm" label-width="120px" size="small">
        <el-form-item label="选中申请数：">
          <span>{{ multipleSelection.length }}个申请</span>
        </el-form-item>
        <el-form-item label="审核结果：">
          <el-tag :type="batchReviewForm.status === 1 ? 'success' : 'danger'">
            {{ batchReviewForm.status === 1 ? '批量通过' : '批量拒绝' }}
          </el-tag>
        </el-form-item>
        <el-form-item label="审核意见：">
          <el-input
            v-model="batchReviewForm.reviewComment"
            type="textarea"
            :rows="3"
            placeholder="请输入统一的审核意见"
            style="width: 300px">
          </el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="batchReviewDialogVisible = false" size="small">取 消</el-button>
        <el-button type="primary" @click="handleBatchReviewConfirm()" size="small">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 图片预览弹窗 -->
    <el-dialog
      title="图片预览"
      :visible.sync="imagePreviewVisible"
      width="80%"
      center>
      <div style="text-align: center;">
        <img
          :src="previewImageUrl"
          style="max-width: 100%; max-height: 70vh; object-fit: contain;"
          alt="预览图片">
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="imagePreviewVisible = false" size="small">关 闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { 
  fetchList, 
  getDetail, 
  reviewApply, 
  batchReview, 
  deleteApply, 
  getStatistics 
} from '@/api/distributorApply'
import { formatDate } from '@/utils/date'

const defaultListQuery = {
  pageNum: 1,
  pageSize: 20,
  realName: null,
  phone: null,
  status: null,
  startTime: null,
  endTime: null
}

const defaultReviewForm = {
  status: 1,
  reviewComment: ''
}

const defaultBatchReviewForm = {
  status: 1,
  reviewComment: ''
}

export default {
  name: 'distributorApplyList',
  data() {
    return {
      listQuery: Object.assign({}, defaultListQuery),
      list: null,
      total: null,
      listLoading: false,
      dateRange: null,
      multipleSelection: [],
      statistics: {},
      
      // 审核弹窗
      reviewDialogVisible: false,
      reviewForm: Object.assign({}, defaultReviewForm),
      currentApply: {},
      
      // 批量审核弹窗
      batchReviewDialogVisible: false,
      batchReviewForm: Object.assign({}, defaultBatchReviewForm),

      // 图片预览弹窗
      imagePreviewVisible: false,
      previewImageUrl: ''
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
      this.currentApply = row
      this.reviewForm = Object.assign({}, defaultReviewForm)
      this.reviewDialogVisible = true
    },
    parseImageArray(imageStr) {
      if (!imageStr) return []
      try {
        const parsed = JSON.parse(imageStr)
        return Array.isArray(parsed) ? parsed : []
      } catch (e) {
        return []
      }
    },
    previewImage(imageUrl) {
      this.previewImageUrl = imageUrl
      this.imagePreviewVisible = true
    },
    handleBatchReview(status) {
      if (this.multipleSelection.length === 0) {
        this.$message.warning('请选择要审核的申请')
        return
      }
      this.batchReviewForm = Object.assign({}, defaultBatchReviewForm)
      this.batchReviewForm.status = status
      this.batchReviewDialogVisible = true
    },
    handleDelete(id) {
      this.$confirm('此操作将永久删除该申请记录, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteApply(id).then(response => {
          this.$message.success('删除成功')
          this.getList()
        })
      }).catch(() => {
        this.$message.info('已取消删除')
      })
    },
    handleReviewConfirm() {
      if (!this.reviewForm.reviewComment) {
        this.$message.warning('请输入审核意见')
        return
      }
      
      reviewApply(this.currentApply.id, this.reviewForm.status, this.reviewForm.reviewComment).then(response => {
        this.$message.success('审核成功')
        this.reviewDialogVisible = false
        this.getList()
        this.getStatisticsData()
      })
    },
    handleBatchReviewConfirm() {
      if (!this.batchReviewForm.reviewComment) {
        this.$message.warning('请输入审核意见')
        return
      }
      
      const ids = this.multipleSelection.map(item => item.id).join(',')
      batchReview(ids, this.batchReviewForm.status, this.batchReviewForm.reviewComment).then(response => {
        this.$message.success('批量审核成功')
        this.batchReviewDialogVisible = false
        this.getList()
        this.getStatisticsData()
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
        0: '待审核',
        1: '已通过',
        2: '已拒绝'
      }
      return statusMap[status] || '未知'
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