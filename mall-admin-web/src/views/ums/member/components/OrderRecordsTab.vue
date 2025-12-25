<template>
  <div class="order-records-tab">
    <!-- 订单统计 -->
    <el-card header="订单统计" style="margin-bottom: 20px;">
      <div class="order-stats" v-if="orderStatistics">
        <div class="stat-item">
          <div class="stat-value">{{ orderStatistics.totalCount || 0 }}</div>
          <div class="stat-label">总订单数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ formatAmount(orderStatistics.totalAmount) }}</div>
          <div class="stat-label">总消费金额</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ orderStatistics.completedCount || 0 }}</div>
          <div class="stat-label">已完成订单</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ orderStatistics.giftCount || 0 }}</div>
          <div class="stat-label">送礼订单</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ formatAmount(orderStatistics.avgAmount) }}</div>
          <div class="stat-label">平均客单价</div>
        </div>
      </div>
    </el-card>

    <!-- 订单筛选 -->
    <el-card header="订单列表">
      <div class="filter-section">
        <el-form :inline="true" :model="queryForm" class="filter-form">
          <el-form-item label="订单类型">
            <el-select v-model="queryForm.orderType" placeholder="全部类型" clearable @change="loadOrderData">
              <el-option label="全部订单" :value="null"></el-option>
              <el-option label="正常订单" :value="0"></el-option>
              <el-option label="秒杀订单" :value="1"></el-option>
              <el-option label="送礼订单" :value="2"></el-option>
            </el-select>
          </el-form-item>
          
          <el-form-item label="订单状态">
            <el-select v-model="queryForm.status" placeholder="全部状态" clearable @change="loadOrderData">
              <el-option label="全部状态" :value="null"></el-option>
              <el-option label="待付款" :value="0"></el-option>
              <el-option label="待发货" :value="1"></el-option>
              <el-option label="已发货" :value="2"></el-option>
              <el-option label="已完成" :value="3"></el-option>
              <el-option label="已关闭" :value="4"></el-option>
              <el-option label="无效订单" :value="5"></el-option>
            </el-select>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="loadOrderData">搜索</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 订单列表 -->
      <el-table
        :data="orderList"
        v-loading="loading"
        style="width: 100%">
        
        <el-table-column prop="orderSn" label="订单编号" width="160">
          <template slot-scope="scope">
            <span class="order-link" @click="viewOrderDetail(scope.row)">
              {{ scope.row.orderSn }}
            </span>
          </template>
        </el-table-column>
        
        <el-table-column prop="totalAmount" label="订单金额" width="100">
          <template slot-scope="scope">
            <span class="amount">{{ formatAmount(scope.row.totalAmount) }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="payAmount" label="实付金额" width="100">
          <template slot-scope="scope">
            <span class="amount paid">{{ formatAmount(scope.row.payAmount) }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="订单状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="orderType" label="订单类型" width="100">
          <template slot-scope="scope">
            <el-tag :type="getOrderTypeTagType(scope.row)">
              {{ getOrderTypeText(scope.row) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="payType" label="支付方式" width="100">
          <template slot-scope="scope">
            {{ getPayTypeText(scope.row.payType) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="createTime" label="下单时间" width="150">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="paymentTime" label="支付时间" width="150">
          <template slot-scope="scope">
            {{ scope.row.paymentTime ? formatDate(scope.row.paymentTime) : '-' }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="120" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="viewOrderDetail(scope.row)">
              查看详情
            </el-button>
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
          :page-sizes="[10, 20, 50, 100]"
          :current-page="queryForm.pageNum"
          :page-size="queryForm.pageSize"
          :total="total">
        </el-pagination>
      </div>
    </el-card>

    <!-- 订单详情弹窗 -->
    <el-dialog title="订单详情" :visible.sync="detailDialogVisible" width="80%" :before-close="closeDetailDialog">
      <div v-if="selectedOrder">
        <el-descriptions :column="3" border>
          <el-descriptions-item label="订单编号">{{ selectedOrder.orderSn }}</el-descriptions-item>
          <el-descriptions-item label="订单金额">{{ formatAmount(selectedOrder.totalAmount) }}</el-descriptions-item>
          <el-descriptions-item label="实付金额">{{ formatAmount(selectedOrder.payAmount) }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusType(selectedOrder.status)">
              {{ getStatusText(selectedOrder.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="支付方式">{{ getPayTypeText(selectedOrder.payType) }}</el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ formatDate(selectedOrder.createTime) }}</el-descriptions-item>
        </el-descriptions>
        
        <div style="margin-top: 20px;">
          <h4>收货信息</h4>
          <p><strong>收货人：</strong>{{ selectedOrder.receiverName }}</p>
          <p><strong>联系电话：</strong>{{ selectedOrder.receiverPhone }}</p>
          <p><strong>收货地址：</strong>{{ getFullAddress(selectedOrder) }}</p>
        </div>
        
        <div style="margin-top: 20px;" v-if="selectedOrder.note">
          <h4>订单备注</h4>
          <p>{{ selectedOrder.note }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getMemberOrders, getMemberOrderStatistics } from '@/api/member'
import { formatDate, formatAmount } from '@/utils/index'

export default {
  name: 'OrderRecordsTab',
  props: {
    memberId: {
      type: [String, Number],
      required: true
    }
  },
  data() {
    return {
      loading: false,
      orderList: [],
      orderStatistics: null,
      total: 0,
      queryForm: {
        orderType: null,
        status: null,
        pageNum: 1,
        pageSize: 20
      },
      detailDialogVisible: false,
      selectedOrder: null
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    formatDate,
    formatAmount,
    
    async loadData() {
      await Promise.all([
        this.loadOrderData(),
        this.loadOrderStatistics()
      ])
    },
    
    async loadOrderData() {
      this.loading = true
      try {
        const response = await getMemberOrders(this.memberId, {
          orderType: this.queryForm.orderType,
          status: this.queryForm.status,
          pageNum: this.queryForm.pageNum,
          pageSize: this.queryForm.pageSize
        })
        
        if (response.code === 200) {
          this.orderList = response.data.list || []
          this.total = response.data.total || 0
        } else {
          this.$message.error(response.message || '获取订单列表失败')
        }
      } catch (error) {
        console.error('获取订单列表失败:', error)
        this.$message.error('获取订单列表失败')
      } finally {
        this.loading = false
      }
    },
    
    async loadOrderStatistics() {
      try {
        const response = await getMemberOrderStatistics(this.memberId)
        if (response.code === 200) {
          this.orderStatistics = response.data
        }
      } catch (error) {
        console.error('获取订单统计失败:', error)
      }
    },
    
    resetQuery() {
      this.queryForm = {
        orderType: null,
        status: null,
        pageNum: 1,
        pageSize: 20
      }
      this.loadOrderData()
    },
    
    handleSizeChange(val) {
      this.queryForm.pageSize = val
      this.queryForm.pageNum = 1
      this.loadOrderData()
    },
    
    handleCurrentChange(val) {
      this.queryForm.pageNum = val
      this.loadOrderData()
    },
    
    viewOrderDetail(order) {
      this.selectedOrder = order
      this.detailDialogVisible = true
    },
    
    closeDetailDialog() {
      this.detailDialogVisible = false
      this.selectedOrder = null
    },
    
    getStatusText(status) {
      const statusMap = {
        0: '待付款',
        1: '待发货',
        2: '已发货',
        3: '已完成',
        4: '已关闭',
        5: '无效订单'
      }
      return statusMap[status] || '未知状态'
    },
    
    getStatusType(status) {
      const typeMap = {
        0: 'warning',
        1: 'primary',
        2: 'info',
        3: 'success',
        4: 'danger',
        5: 'danger'
      }
      return typeMap[status] || 'info'
    },
    
    getPayTypeText(payType) {
      const payTypeMap = {
        0: '未支付',
        1: '支付宝',
        2: '微信支付',
        3: '余额支付'
      }
      return payTypeMap[payType] || '未知'
    },
    
    getFullAddress(order) {
      return `${order.receiverProvince || ''}${order.receiverCity || ''}${order.receiverRegion || ''}${order.receiverDetailAddress || ''}`
    },
    
    getOrderTypeText(order) {
      // 优先判断是否为送礼订单
      if (order.isGift === true) {
        return '送礼订单'
      }
      // 再判断是否为秒杀订单
      if (order.orderType === 1) {
        return '秒杀订单'
      }
      // 默认为正常订单
      return '正常订单'
    },
    
    getOrderTypeTagType(order) {
      // 优先判断是否为送礼订单
      if (order.isGift === true) {
        return 'warning'
      }
      // 再判断是否为秒杀订单
      if (order.orderType === 1) {
        return 'danger'
      }
      // 默认为正常订单
      return 'info'
    }
  }
}
</script>

<style scoped>
.order-records-tab {
  padding: 20px 0;
}

.order-stats {
  display: flex;
  gap: 40px;
  justify-content: space-around;
}

.stat-item {
  text-align: center;
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}

.filter-section {
  margin-bottom: 20px;
}

.filter-form {
  margin: 0;
}

.amount {
  font-weight: bold;
  color: #E6A23C;
}

.amount.paid {
  color: #67C23A;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.order-link {
  color: #409EFF;
  cursor: pointer;
  text-decoration: none;
  transition: color 0.3s;
}

.order-link:hover {
  color: #66b1ff;
  text-decoration: underline;
}

@media (max-width: 768px) {
  .order-stats {
    flex-direction: column;
    gap: 20px;
  }
}
</style> 