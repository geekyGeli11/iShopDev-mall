<template>
  <div class="coupon-manage-tab">
    <!-- 优惠券操作 -->
    <el-card header="优惠券操作" style="margin-bottom: 20px;">
      <el-button type="primary" @click="showSendDialog">发放优惠券</el-button>
    </el-card>

    <!-- 优惠券使用历史 -->
    <el-card header="优惠券使用历史">
      <el-table :data="historyList" v-loading="loading" style="width: 100%">
        <el-table-column prop="couponCode" label="优惠券码" width="150"></el-table-column>
        
        <el-table-column prop="couponName" label="优惠券名称" min-width="200">
          <template slot-scope="scope">
            <span>{{ scope.row.couponName || '-' }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="couponType" label="优惠券类型" width="120">
          <template slot-scope="scope">
            {{ getCouponTypeName(scope.row.couponType) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="amount" label="优惠金额" width="100">
          <template slot-scope="scope">
            <span class="amount">{{ formatAmount(scope.row.amount) }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="useStatus" label="使用状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.useStatus)">
              {{ getStatusText(scope.row.useStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="useTime" label="使用时间" width="150">
          <template slot-scope="scope">
            {{ scope.row.useTime ? formatDate(scope.row.useTime) : '-' }}
          </template>
        </el-table-column>
        
        <el-table-column prop="createTime" label="获得时间" width="150">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createTime) }}
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

    <!-- 发放优惠券弹窗 -->
    <el-dialog title="发放优惠券" :visible.sync="sendDialogVisible" width="500px">
      <el-form ref="sendForm" :model="sendForm" :rules="sendRules" label-width="100px">
        <el-form-item label="选择优惠券" prop="couponId">
          <el-select v-model="sendForm.couponId" placeholder="请选择要发放的优惠券" style="width: 100%;" @change="onCouponChange">
            <el-option
              v-for="coupon in availableCoupons"
              :key="coupon.id"
              :label="coupon.name"
              :value="coupon.id">
              <div style="display: flex; justify-content: space-between;">
                <span>{{ coupon.name }}</span>
                <span style="color: #8492a6; font-size: 13px;">{{ formatAmount(coupon.amount) }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        

        
        <el-form-item label="发放原因" prop="reason">
          <el-input
            v-model="sendForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入发放优惠券的原因">
          </el-input>
        </el-form-item>
        
        <!-- 选中优惠券信息预览 -->
        <div v-if="selectedCoupon" class="coupon-preview">
          <h4>优惠券信息预览</h4>
          <p><strong>名称：</strong>{{ selectedCoupon.name }}</p>
          <p><strong>类型：</strong>{{ getCouponTypeName(selectedCoupon.type) }}</p>
          <p><strong>优惠金额：</strong>{{ formatAmount(selectedCoupon.amount) }}</p>
          <p><strong>使用门槛：</strong>{{ selectedCoupon.minPoint > 0 ? `满${formatAmount(selectedCoupon.minPoint)}元可用` : '无门槛' }}</p>
          <p><strong>有效期：</strong>{{ selectedCoupon.startTime ? `${formatDate(selectedCoupon.startTime)} 至 ${formatDate(selectedCoupon.endTime)}` : `领取后${selectedCoupon.enableTime}天内有效` }}</p>
        </div>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="sendDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmSend" :loading="sending">确定发放</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { 
  getMemberCouponHistory, 
  sendMemberCoupon,
  getAvailableCoupons 
} from '@/api/member'
import { formatDate, formatAmount } from '@/utils/index'

export default {
  name: 'CouponManageTab',
  props: {
    memberId: {
      type: [String, Number],
      required: true
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
      sendDialogVisible: false,
      sending: false,
      availableCoupons: [],
      selectedCoupon: null,
      sendForm: {
        couponId: null,
        reason: ''
      },
      sendRules: {
        couponId: [
          { required: true, message: '请选择优惠券', trigger: 'change' }
        ],
        reason: [
          { required: true, message: '请输入发放原因', trigger: 'blur' },
          { max: 200, message: '原因长度不能超过200个字符', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    formatDate,
    formatAmount,
    
    async loadData() {
      await this.loadHistoryData()
    },
    
    async loadHistoryData() {
      this.loading = true
      try {
        const response = await getMemberCouponHistory(this.memberId, this.queryForm)
        if (response.code === 200) {
          this.historyList = response.data.list || []
          this.total = response.data.total || 0
        } else {
          this.$message.error(response.message || '获取优惠券历史失败')
        }
      } catch (error) {
        console.error('获取优惠券历史失败:', error)
        this.$message.error('获取优惠券历史失败')
      } finally {
        this.loading = false
      }
    },
    
    async loadAvailableCoupons() {
      try {
        const response = await getAvailableCoupons({
          memberId: this.memberId,
          pageNum: 1,
          pageSize: 100
        })
        if (response.code === 200) {
          this.availableCoupons = response.data.list || []
        }
      } catch (error) {
        console.error('获取可用优惠券失败:', error)
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
    
    async showSendDialog() {
      this.sendForm = {
        couponId: null,
        reason: ''
      }
      this.selectedCoupon = null
      this.sendDialogVisible = true
      await this.loadAvailableCoupons()
    },
    
    onCouponChange(couponId) {
      this.selectedCoupon = this.availableCoupons.find(c => c.id === couponId)
    },
    
    async confirmSend() {
      try {
        await this.$refs.sendForm.validate()
        this.sending = true
        
        const response = await sendMemberCoupon(
          this.memberId,
          this.sendForm.couponId,
          this.sendForm.reason
        )
        
        if (response.code === 200) {
          this.$message.success('优惠券发放成功')
          this.sendDialogVisible = false
          this.loadData()
        } else {
          this.$message.error(response.message || '优惠券发放失败')
        }
      } catch (error) {
        console.error('优惠券发放失败:', error)
        if (error !== false) {
          this.$message.error('优惠券发放失败')
        }
      } finally {
        this.sending = false
      }
    },
    
    getCouponTypeName(type) {
      const typeMap = {
        0: '全场通用',
        1: '指定分类',
        2: '指定商品'
      }
      return typeMap[type] || '未知类型'
    },
    
    getStatusText(status) {
      const statusMap = {
        0: '未使用',
        1: '已使用',
        2: '已过期'
      }
      return statusMap[status] || '未知状态'
    },
    
    getStatusType(status) {
      const typeMap = {
        0: 'warning',
        1: 'success',
        2: 'danger'
      }
      return typeMap[status] || 'info'
    }
  }
}
</script>

<style scoped>
.coupon-manage-tab {
  padding: 20px 0;
}

.amount {
  font-weight: bold;
  color: #E6A23C;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.coupon-preview {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
  margin-top: 10px;
}

.coupon-preview h4 {
  margin-top: 0;
  margin-bottom: 10px;
  color: #303133;
}

.coupon-preview p {
  margin: 5px 0;
  color: #606266;
}
</style> 