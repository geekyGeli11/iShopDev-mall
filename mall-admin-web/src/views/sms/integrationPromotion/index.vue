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
          <el-form-item label="活动名称：">
            <el-input v-model="listQuery.name" class="input-width" placeholder="活动名称"></el-input>
          </el-form-item>
          <el-form-item label="活动状态：">
            <el-select v-model="listQuery.status" placeholder="全部" clearable class="input-width">
              <el-option v-for="item in statusOptions"
                         :key="item.value"
                         :label="item.label"
                         :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="operate-container" shadow="never">
      <i class="el-icon-star-on"></i>
      <span>数据列表</span>
      <el-button size="mini" class="btn-add" @click="handleAdd()">添加</el-button>
    </el-card>

    <div class="table-container">
      <el-table
        ref="integrationPromotionTable"
        v-loading="listLoading"
        :data="list"
        style="width: 100%;"
        @selection-change="handleSelectionChange"
        border
      >
      <el-table-column type="selection" width="60" align="center" />
      <el-table-column label="编号" width="80" align="center">
        <template slot-scope="scope">{{ scope.row.id }}</template>
      </el-table-column>
      <el-table-column label="活动名称" align="center">
        <template slot-scope="scope">{{ scope.row.name }}</template>
      </el-table-column>
      <el-table-column label="活动描述" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.description || '暂无描述' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="积分倍数" width="100" align="center">
        <template slot-scope="scope">
          <el-tag :type="getMultiplierTagType(scope.row.multiplier)">
            {{ scope.row.multiplier }}倍
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="活动时间" width="300" align="center">
        <template slot-scope="scope">
          <div>
            <div>开始：{{ scope.row.startTime | formatDateTime }}</div>
            <div>结束：{{ scope.row.endTime | formatDateTime }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="优先级" width="80" align="center">
        <template slot-scope="scope">{{ scope.row.priority }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100" align="center">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            :active-value="true"
            :inactive-value="false"
            @change="handleStatusChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleUpdate(scope.row)">
            编辑
          </el-button>
          <el-button size="mini" type="text" @click="handleDelete(scope.row.id, scope.row.name)">
            删除
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

    <!-- 添加或修改对话框 -->
    <el-dialog :title="isEdit ? '编辑积分营销活动' : '添加积分营销活动'" :visible.sync="dialogVisible" width="800px">
      <el-form ref="integrationPromotionForm" :model="integrationPromotion" :rules="rules" label-width="150px">
        <el-form-item label="活动名称" prop="name">
          <el-input v-model="integrationPromotion.name" placeholder="请输入活动名称" />
        </el-form-item>
        <el-form-item label="活动描述" prop="description">
          <el-input
            v-model="integrationPromotion.description"
            type="textarea"
            :rows="3"
            placeholder="请输入活动描述"
          />
        </el-form-item>
        <el-form-item label="活动时间" prop="timeRange">
          <el-date-picker
            v-model="integrationPromotion.timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            format="yyyy-MM-dd HH:mm:ss"
            value-format="yyyy-MM-dd HH:mm:ss"
            @change="handleTimeRangeChange"
          />
        </el-form-item>
        <el-form-item label="积分倍数" prop="multiplier">
          <el-input-number
            v-model="integrationPromotion.multiplier"
            :precision="2"
            :step="0.1"
            :min="1"
            :max="10"
            placeholder="请输入积分倍数"
          />
          <span style="margin-left: 10px; color: #999;">倍（如1.5表示1.5倍积分）</span>
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-input-number
            v-model="integrationPromotion.priority"
            :min="0"
            :max="999"
            placeholder="请输入优先级"
          />
          <span style="margin-left: 10px; color: #999;">数值越大优先级越高</span>
        </el-form-item>
        <el-form-item label="适用类型" prop="applicableType">
          <el-radio-group v-model="integrationPromotion.applicableType">
            <el-radio :label="0">全部商品</el-radio>
            <el-radio :label="1">指定分类</el-radio>
            <el-radio :label="2">指定商品</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="integrationPromotion.applicableType !== 0" label="适用对象ID" prop="applicableIds">
          <el-input
            v-model="integrationPromotion.applicableIds"
            placeholder="请输入ID，多个用逗号分隔"
          />
          <span style="margin-left: 10px; color: #999;">多个ID用逗号分隔</span>
        </el-form-item>
        <el-form-item label="最小订单金额">
          <el-input-number
            v-model="integrationPromotion.minOrderAmount"
            :precision="2"
            :step="1"
            :min="0"
            placeholder="最小订单金额"
          />
          <span style="margin-left: 10px; color: #999;">元（为空表示无限制）</span>
        </el-form-item>
        <el-form-item label="单笔最大积分">
          <el-input-number
            v-model="integrationPromotion.maxIntegrationReward"
            :min="0"
            placeholder="单笔订单最大积分奖励"
          />
          <span style="margin-left: 10px; color: #999;">为空表示无限制</span>
        </el-form-item>
        <el-form-item label="活动状态" prop="status">
          <el-radio-group v-model="integrationPromotion.status">
            <el-radio :label="true">启用</el-radio>
            <el-radio :label="false">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleDialogConfirm">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  fetchIntegrationPromotionList,
  createIntegrationPromotion,
  updateIntegrationPromotion,
  deleteIntegrationPromotion,
  deleteBatchIntegrationPromotion,
  updateIntegrationPromotionStatus
} from '@/api/integrationPromotion'
import { formatDate } from '@/utils/date'

const defaultIntegrationPromotion = {
  name: '',
  description: '',
  startTime: '',
  endTime: '',
  timeRange: [],
  multiplier: 1.0,
  status: true,
  priority: 0,
  applicableType: 0,
  applicableIds: '',
  minOrderAmount: null,
  maxIntegrationReward: null
}

export default {
  name: 'IntegrationPromotionList',
  filters: {
    formatDateTime(time) {
      if (time == null || time === '') {
        return 'N/A'
      }
      const date = new Date(time)
      return formatDate(date, 'yyyy-MM-dd hh:mm:ss')
    }
  },
  data() {
    return {
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        status: null
      },
      statusOptions: [
        { label: '启用', value: true },
        { label: '禁用', value: false }
      ],
      multipleSelection: [],
      dialogVisible: false,
      isEdit: false,
      integrationPromotion: Object.assign({}, defaultIntegrationPromotion),
      rules: {
        name: [
          { required: true, message: '请输入活动名称', trigger: 'blur' },
          { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
        ],
        timeRange: [
          { required: true, message: '请选择活动时间', trigger: 'change' }
        ],
        multiplier: [
          { required: true, message: '请输入积分倍数', trigger: 'blur' }
        ],
        priority: [
          { required: true, message: '请输入优先级', trigger: 'blur' }
        ],
        status: [
          { required: true, message: '请选择活动状态', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      fetchIntegrationPromotionList(this.listQuery).then(response => {
        this.list = response.data.list
        this.total = parseInt(response.data.total)
        this.listLoading = false
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
        name: null,
        status: null
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
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    handleAdd() {
      this.isEdit = false
      this.integrationPromotion = Object.assign({}, defaultIntegrationPromotion)
      // 在设置完数据后再显示对话框，并清除验证状态
      this.$nextTick(() => {
        this.dialogVisible = true
        this.$nextTick(() => {
          if (this.$refs['integrationPromotionForm']) {
            this.$refs['integrationPromotionForm'].clearValidate()
          }
        })
      })
    },
    handleUpdate(row) {
      this.isEdit = true
      this.integrationPromotion = Object.assign({}, row)

      // 直接设置时间范围，转换为日期选择器需要的格式
      if (row.startTime && row.endTime) {
        // 转换ISO时间为本地时间字符串格式
        const startDate = new Date(row.startTime)
        const endDate = new Date(row.endTime)

        // 格式化为 yyyy-MM-dd HH:mm:ss 格式
        const formatDateTime = (date) => {
          const year = date.getFullYear()
          const month = String(date.getMonth() + 1).padStart(2, '0')
          const day = String(date.getDate()).padStart(2, '0')
          const hours = String(date.getHours()).padStart(2, '0')
          const minutes = String(date.getMinutes()).padStart(2, '0')
          const seconds = String(date.getSeconds()).padStart(2, '0')
          return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
        }

        this.integrationPromotion.timeRange = [formatDateTime(startDate), formatDateTime(endDate)]
        this.integrationPromotion.startTime = row.startTime
        this.integrationPromotion.endTime = row.endTime
      } else {
        this.integrationPromotion.timeRange = []
        this.integrationPromotion.startTime = ''
        this.integrationPromotion.endTime = ''
      }

      // 显示对话框
      this.dialogVisible = true

      // 清除验证状态
      this.$nextTick(() => {
        if (this.$refs['integrationPromotionForm']) {
          this.$refs['integrationPromotionForm'].clearValidate()
        }
      })
    },
    handleTimeRangeChange(value) {
      if (value && value.length === 2) {
        this.integrationPromotion.startTime = value[0]
        this.integrationPromotion.endTime = value[1]
      } else {
        this.integrationPromotion.startTime = ''
        this.integrationPromotion.endTime = ''
      }
    },
    handleDialogConfirm() {
      this.$refs['integrationPromotionForm'].validate((valid) => {
        if (valid) {
          // 确保timeRange转换为startTime和endTime
          if (this.integrationPromotion.timeRange && this.integrationPromotion.timeRange.length === 2) {
            // 统一转换为Date对象，然后转为ISO字符串
            const startDate = this.integrationPromotion.timeRange[0] instanceof Date
              ? this.integrationPromotion.timeRange[0]
              : new Date(this.integrationPromotion.timeRange[0])
            const endDate = this.integrationPromotion.timeRange[1] instanceof Date
              ? this.integrationPromotion.timeRange[1]
              : new Date(this.integrationPromotion.timeRange[1])

            this.integrationPromotion.startTime = startDate.toISOString()
            this.integrationPromotion.endTime = endDate.toISOString()
          }

          // 创建提交数据，移除timeRange字段
          const submitData = Object.assign({}, this.integrationPromotion)
          delete submitData.timeRange

          this.$confirm('是否确认保存?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            if (this.isEdit) {
              updateIntegrationPromotion(submitData.id, submitData).then(response => {
                this.$message({
                  message: '修改成功',
                  type: 'success',
                  duration: 1000
                })
                this.dialogVisible = false
                this.getList()
              })
            } else {
              createIntegrationPromotion(submitData).then(response => {
                this.$message({
                  message: '添加成功',
                  type: 'success',
                  duration: 1000
                })
                this.dialogVisible = false
                this.getList()
              })
            }
          })
        } else {
          this.$message({
            message: '验证失败',
            type: 'error',
            duration: 1000
          })
          return false
        }
      })
    },
    handleStatusChange(row) {
      this.$confirm('是否要修改该状态?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        updateIntegrationPromotionStatus(row.id, row.status).then(response => {
          this.$message({
            type: 'success',
            message: '修改成功!'
          })
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消修改'
        })
        this.getList()
      })
    },
    handleDelete(id, name) {
      this.$confirm('是否要删除该积分营销活动?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteIntegrationPromotion(id).then(response => {
          this.$message({
            type: 'success',
            message: '删除成功!'
          })
          this.getList()
        })
      })
    },
    handleDeleteBatch() {
      if (this.multipleSelection.length < 1) {
        this.$message({
          message: '请至少选择一条记录',
          type: 'warning',
          duration: 1000
        })
        return
      }
      this.$confirm('是否要删除选中的积分营销活动?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const ids = this.multipleSelection.map(item => item.id)
        deleteBatchIntegrationPromotion(ids).then(response => {
          this.$message({
            type: 'success',
            message: '删除成功!'
          })
          this.getList()
        })
      })
    },
    getMultiplierTagType(multiplier) {
      if (multiplier >= 2.0) {
        return 'danger'
      } else if (multiplier >= 1.5) {
        return 'warning'
      } else {
        return 'success'
      }
    }
  }
}
</script>

<style scoped>
.input-width {
  width: 203px;
}

.filter-container {
  padding-bottom: 10px;
}

.operate-container {
  margin-top: 20px;
}

.operate-container .btn-add {
  float: right;
}

.table-container {
  margin-top: 20px;
}

.pagination-container {
  margin-top: 20px;
  text-align: center;
}
</style>
