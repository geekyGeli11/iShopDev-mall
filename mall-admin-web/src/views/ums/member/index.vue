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
          @click="handleSearchListWithState()"
          size="small">
          查询搜索
        </el-button>
        <el-button
          style="float:right;margin-right: 15px"
          @click="handleResetSearchWithState()"
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
              placeholder="昵称/手机号/会员码" 
              clearable>
            </el-input>
          </el-form-item>
          <el-form-item label="用户状态：">
            <el-select v-model="listQuery.status" placeholder="全部" clearable class="input-width">
              <el-option label="启用" :value="1"></el-option>
              <el-option label="禁用" :value="0"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="会员等级：">
            <el-select v-model="listQuery.memberLevelId" placeholder="全部" clearable class="input-width">
              <el-option 
                v-for="item in memberLevelOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
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
          <el-form-item label="所在城市：">
            <el-input 
              v-model="listQuery.city" 
              class="input-width" 
              placeholder="请输入城市" 
              clearable>
            </el-input>
          </el-form-item>
          <el-form-item label="性别：">
            <el-select v-model="listQuery.gender" placeholder="全部" clearable class="input-width">
              <el-option label="男" :value="1"></el-option>
              <el-option label="女" :value="2"></el-option>
              <el-option label="未知" :value="0"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="所属学校：">
            <el-select v-model="listQuery.schoolId" placeholder="全部" clearable class="input-width">
              <el-option
                v-for="item in schoolOptions"
                :key="item.id"
                :label="item.schoolName"
                :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 操作区域 -->
    <el-card class="operate-container" shadow="never">
      <i class="el-icon-tickets"></i>
      <span>数据列表</span>
      <el-button 
        size="mini" 
        class="btn-add" 
        @click="handleBatchIntegration()" 
        style="margin-left: 20px">
        批量积分
      </el-button>
      <el-button 
        size="mini" 
        class="btn-add" 
        @click="handleBatchCoupon()">
        批量优惠券
      </el-button>
      <el-button 
        size="mini" 
        class="btn-add" 
        @click="handleExport()">
        导出数据
      </el-button>
    </el-card>

    <!-- 数据列表 -->
    <div class="table-container">
      <el-table 
        ref="memberTable"
        :data="list"
        style="width: 100%;"
        v-loading="listLoading" 
        border
        @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column label="编号" width="80" align="center">
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
        <el-table-column label="会员码" width="130" align="center">
          <template slot-scope="scope">{{ scope.row.memberCode || '-' }}</template>
        </el-table-column>
        <el-table-column label="会员等级" width="100" align="center">
          <template slot-scope="scope">{{ scope.row.memberLevelName || '普通会员' }}</template>
        </el-table-column>
        <el-table-column label="积分" width="80" align="center">
          <template slot-scope="scope">{{ scope.row.integration || 0 }}</template>
        </el-table-column>
        <el-table-column label="订单数" width="80" align="center">
          <template slot-scope="scope">{{ scope.row.orderCount || 0 }}</template>
        </el-table-column>
        <el-table-column label="消费金额" width="100" align="center">
          <template slot-scope="scope">￥{{ scope.row.consumeAmount || '0.00' }}</template>
        </el-table-column>
        <el-table-column label="城市" width="100" align="center">
          <template slot-scope="scope">{{ scope.row.city || '-' }}</template>
        </el-table-column>
        <el-table-column label="所属学校" width="120" align="center">
          <template slot-scope="scope">{{ scope.row.schoolName || '未绑定' }}</template>
        </el-table-column>
        <el-table-column label="注册时间" width="160" align="center">
          <template slot-scope="scope">{{ scope.row.createTime | formatDateTime }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-switch
              @change="handleStatusChange(scope.$index, scope.row)"
              :active-value="1"
              :inactive-value="0"
              v-model="scope.row.status">
            </el-switch>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button 
              size="mini"
              type="text"
              @click="handleViewDetail(scope.row.id)">
              查看详情
            </el-button>
            <el-button 
              size="mini"
              type="text"
              @click="handleSendCoupon(scope.row)">
              发放优惠券
            </el-button>
            <el-button 
              size="mini"
              type="text"
              @click="handleManageIntegration(scope.row)">
              积分管理
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        background
        @size-change="handleSizeChangeWithState"
        @current-change="handleCurrentChangeWithState"
        layout="total, sizes,prev, pager, next,jumper"
        :current-page.sync="listQuery.pageNum"
        :page-size="listQuery.pageSize"
        :page-sizes="[10,20,50,100]"
        :total="total">
      </el-pagination>
    </div>

    <!-- 积分管理弹窗 -->
    <el-dialog
      title="积分管理"
      :visible.sync="integrationDialogVisible"
      width="500px">
      <el-form :model="integrationForm" label-width="100px" size="small">
        <el-form-item label="用户昵称：">
          <span>{{ currentMember.nickname || '未设置' }}</span>
        </el-form-item>
        <el-form-item label="当前积分：">
          <span>{{ currentMember.integration || 0 }}</span>
        </el-form-item>
        <el-form-item label="操作类型：">
          <el-radio-group v-model="integrationForm.operationType">
            <el-radio :label="0">增加积分</el-radio>
            <el-radio :label="1">扣减积分</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="积分数量：">
          <el-input-number
            v-model="integrationForm.integration"
            :min="1"
            :max="10000"
            style="width: 200px">
          </el-input-number>
        </el-form-item>
        <el-form-item label="操作原因：">
          <el-input
            v-model="integrationForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入操作原因"
            style="width: 300px">
          </el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="integrationDialogVisible = false" size="small">取 消</el-button>
        <el-button type="primary" @click="handleIntegrationConfirm()" size="small">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 优惠券发放弹窗 -->
    <el-dialog
      title="发放优惠券"
      :visible.sync="couponDialogVisible"
      width="600px">
      <el-form :model="couponForm" label-width="100px" size="small">
        <el-form-item label="用户昵称：">
          <span>{{ currentMember.nickname || '未设置' }}</span>
        </el-form-item>
        <el-form-item label="选择优惠券：">
          <el-select v-model="couponForm.couponId" placeholder="请选择优惠券" style="width: 400px">
            <el-option
              v-for="item in availableCoupons"
              :key="item.id"
              :label="`${item.name} (${item.amount}元 满${item.minPoint}元可用)`"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="发放原因：">
          <el-input
            v-model="couponForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入发放原因"
            style="width: 400px">
          </el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="couponDialogVisible = false" size="small">取 消</el-button>
        <el-button type="primary" @click="handleCouponConfirm()" size="small">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 批量积分弹窗 -->
    <el-dialog
      title="批量积分调整"
      :visible.sync="batchIntegrationDialogVisible"
      width="500px">
      <el-form :model="batchIntegrationForm" label-width="120px" size="small">
        <el-form-item label="选中用户数：">
          <span>{{ multipleSelection.length }}个用户</span>
        </el-form-item>
        <el-form-item label="操作类型：">
          <el-radio-group v-model="batchIntegrationForm.operationType">
            <el-radio :label="0">增加积分</el-radio>
            <el-radio :label="1">扣减积分</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="积分数量：">
          <el-input-number
            v-model="batchIntegrationForm.integration"
            :min="1"
            :max="10000"
            style="width: 200px">
          </el-input-number>
        </el-form-item>
        <el-form-item label="操作原因：">
          <el-input
            v-model="batchIntegrationForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入操作原因"
            style="width: 300px">
          </el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="batchIntegrationDialogVisible = false" size="small">取 消</el-button>
        <el-button type="primary" @click="handleBatchIntegrationConfirm()" size="small">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 批量优惠券弹窗 -->
    <el-dialog
      title="批量发放优惠券"
      :visible.sync="batchCouponDialogVisible"
      width="600px">
      <el-form :model="batchCouponForm" label-width="120px" size="small">
        <el-form-item label="选中用户数：">
          <span>{{ multipleSelection.length }}个用户</span>
        </el-form-item>
        <el-form-item label="选择优惠券：">
          <el-select v-model="batchCouponForm.couponId" placeholder="请选择优惠券" style="width: 400px">
            <el-option
              v-for="item in availableCoupons"
              :key="item.id"
              :label="`${item.name} (${item.amount}元 满${item.minPoint}元可用)`"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="发放原因：">
          <el-input
            v-model="batchCouponForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入发放原因"
            style="width: 400px">
          </el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="batchCouponDialogVisible = false" size="small">取 消</el-button>
        <el-button type="primary" @click="handleBatchCouponConfirm()" size="small">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { 
  fetchList, 
  updateStatus, 
  giveIntegration, 
  deductIntegration, 
  sendCoupon, 
  getAvailableCoupons,
  batchAdjustIntegration,
  batchSendCoupon,
  exportMemberData 
} from '@/api/member'
import { fetchList as fetchMemberLevelList } from '@/api/memberLevel'
import { formatDate } from '@/utils/date'

const defaultListQuery = {
  pageNum: 1,
  pageSize: 20,
  keyword: null,
  status: null,
  memberLevelId: null,
  startTime: null,
  endTime: null,
  city: null,
  gender: null,
  schoolId: null
}

const defaultIntegrationForm = {
  operationType: 0,
  integration: 1,
  reason: ''
}

const defaultCouponForm = {
  couponId: null,
  reason: ''
}

const defaultBatchIntegrationForm = {
  operationType: 0,
  integration: 1,
  reason: ''
}

const defaultBatchCouponForm = {
  couponId: null,
  reason: ''
}

import listStateMixin from '@/mixins/listStateMixin'

export default {
  name: 'memberList',
  mixins: [listStateMixin],
  data() {
    return {
      // 设置页面标识用于状态管理
      pageKey: 'memberList',
      listQuery: Object.assign({}, defaultListQuery),
      list: null,
      total: null,
      listLoading: false,
      dateRange: null,
      memberLevelOptions: [],
      schoolOptions: [],
      multipleSelection: [],
      
      // 积分管理弹窗
      integrationDialogVisible: false,
      integrationForm: Object.assign({}, defaultIntegrationForm),
      currentMember: {},
      
      // 优惠券发放弹窗
      couponDialogVisible: false,
      couponForm: Object.assign({}, defaultCouponForm),
      availableCoupons: [],
      
      // 批量积分弹窗
      batchIntegrationDialogVisible: false,
      batchIntegrationForm: Object.assign({}, defaultBatchIntegrationForm),
      
      // 批量优惠券弹窗
      batchCouponDialogVisible: false,
      batchCouponForm: Object.assign({}, defaultBatchCouponForm)
    }
  },
  created() {
    // 如果没有保存的状态，则正常加载数据
    if (!this.$store.getters['listState/hasListState'](this.pageKey)) {
      this.getList()
    }
    this.getMemberLevelList()
    this.getAvailableCouponsList()
    this.getSchoolList()
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
        return 'N/A'
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
    handleStatusChange(index, row) {
      this.$confirm('是否要修改该用户状态?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        updateStatus(row.id, row.status).then(response => {
          this.$message({
            type: 'success',
            message: '状态修改成功!'
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
    handleViewDetail(id) {
      this.$router.push({ path: `/member/detail/${id}` })
    },
    handleManageIntegration(row) {
      this.currentMember = row
      this.integrationForm = Object.assign({}, defaultIntegrationForm)
      this.integrationDialogVisible = true
    },
    handleIntegrationConfirm() {
      this.$confirm('确认执行积分操作?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const { operationType, integration, reason } = this.integrationForm
        const apiCall = operationType === 0 ? giveIntegration : deductIntegration
        
        apiCall(this.currentMember.id, integration, reason).then(response => {
          this.$message({
            message: '积分操作成功！',
            type: 'success'
          })
          this.integrationDialogVisible = false
          this.getList()
        }).catch(error => {
          this.$message.error(error.message || '积分操作失败')
        })
      })
    },
    handleSendCoupon(row) {
      this.currentMember = row
      this.couponForm = Object.assign({}, defaultCouponForm)
      this.couponDialogVisible = true
    },
    handleCouponConfirm() {
      this.$confirm('确认发放优惠券?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const { couponId, reason } = this.couponForm
        sendCoupon(this.currentMember.id, couponId, reason).then(response => {
          this.$message({
            message: '优惠券发放成功！',
            type: 'success'
          })
          this.couponDialogVisible = false
          this.getList()
        }).catch(error => {
          this.$message.error(error.message || '优惠券发放失败')
        })
      })
    },
    handleBatchIntegration() {
      if (this.multipleSelection.length === 0) {
        this.$message.warning('请先选择用户')
        return
      }
      this.batchIntegrationForm = Object.assign({}, defaultBatchIntegrationForm)
      this.batchIntegrationDialogVisible = true
    },
    handleBatchIntegrationConfirm() {
      this.$confirm(`确认对${this.multipleSelection.length}个用户执行批量积分操作?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const memberIds = this.multipleSelection.map(item => item.id)
        const { operationType, integration, reason } = this.batchIntegrationForm
        
        batchAdjustIntegration(memberIds, integration, operationType, reason).then(response => {
          this.$message({
            message: `批量积分操作成功！成功处理${response.data}个用户`,
            type: 'success'
          })
          this.batchIntegrationDialogVisible = false
          this.getList()
        }).catch(error => {
          this.$message.error(error.message || '批量积分操作失败')
        })
      })
    },
    handleBatchCoupon() {
      if (this.multipleSelection.length === 0) {
        this.$message.warning('请先选择用户')
        return
      }
      this.batchCouponForm = Object.assign({}, defaultBatchCouponForm)
      this.batchCouponDialogVisible = true
    },
    handleBatchCouponConfirm() {
      this.$confirm(`确认对${this.multipleSelection.length}个用户发放优惠券?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const memberIds = this.multipleSelection.map(item => item.id)
        const { couponId, reason } = this.batchCouponForm
        
        batchSendCoupon(memberIds, couponId, reason).then(response => {
          this.$message({
            message: `批量优惠券发放成功！成功处理${response.data}个用户`,
            type: 'success'
          })
          this.batchCouponDialogVisible = false
          this.getList()
        }).catch(error => {
          this.$message.error(error.message || '批量优惠券发放失败')
        })
      })
    },
    handleExport() {
      this.$confirm('确认导出用户数据?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        exportMemberData(this.listQuery).then(response => {
          this.$message({
            message: `数据导出成功！共导出${response.data.length}条记录`,
            type: 'success'
          })
          // 这里可以处理文件下载逻辑
        }).catch(error => {
          this.$message.error(error.message || '数据导出失败')
        })
      })
    },
    getList() {
      this.listLoading = true
      fetchList(this.listQuery).then(response => {
        this.listLoading = false
        this.list = response.data.list
        this.total = response.data.total
      }).catch(() => {
        this.listLoading = false
      })
    },
    getMemberLevelList() {
      fetchMemberLevelList({ pageSize: 100, pageNum: 1, defaultStatus: 0 }).then(response => {
        this.memberLevelOptions = response.data.list.map(item => ({
          label: item.name,
          value: item.id
        }))
      })
    },
    getAvailableCouponsList() {
      getAvailableCoupons({ pageSize: 100, pageNum: 1 }).then(response => {
        this.availableCoupons = response.data.list || []
      })
    },
    getSchoolList() {
      // 调用获取学校列表的API
      this.$http({
        url: '/member/schools',
        method: 'get'
      }).then(response => {
        this.schoolOptions = response.data || []
      }).catch(error => {
        console.error('获取学校列表失败:', error)
      })
    }
  }
}
</script>

<style scoped>
.input-width {
  width: 180px;
}

.btn-add {
  margin-left: 20px;
}
</style> 