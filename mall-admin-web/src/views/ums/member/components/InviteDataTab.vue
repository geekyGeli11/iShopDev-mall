<template>
  <div class="invite-data-tab">
    <!-- 邀请统计 -->
    <el-card header="邀请统计" style="margin-bottom: 20px;">
      <div class="invite-stats" v-if="inviteSummary">
        <div class="stat-item">
          <div class="stat-value">{{ inviteSummary.totalInviteCount || 0 }}</div>
          <div class="stat-label">累计邀请人数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ inviteSummary.validInviteCount || 0 }}</div>
          <div class="stat-label">有效邀请人数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ formatAmount(inviteSummary.totalRewardAmount) }}</div>
          <div class="stat-label">累计奖励金额</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ formatAmount(inviteSummary.totalWithdrawAmount) }}</div>
          <div class="stat-label">累计提现金额</div>
        </div>
      </div>
    </el-card>

    <!-- Tab切换 -->
    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <!-- 邀请关系 -->
      <el-tab-pane label="邀请关系" name="relations">
        <el-table :data="relationsList" v-loading="relationsLoading" style="width: 100%">
          <el-table-column prop="inviteeNickname" label="被邀请人昵称" width="150">
            <template slot-scope="scope">
              {{ scope.row.inviteeNickname || '未设置' }}
            </template>
          </el-table-column>
          
          <el-table-column prop="inviteePhone" label="被邀请人手机" width="130">
            <template slot-scope="scope">
              {{ formatPhone(scope.row.inviteePhone) }}
            </template>
          </el-table-column>
          
          <el-table-column prop="inviteTime" label="邀请时间" width="150">
            <template slot-scope="scope">
              {{ formatDate(scope.row.inviteTime) }}
            </template>
          </el-table-column>
          
          <el-table-column prop="isValid" label="是否有效" width="100">
            <template slot-scope="scope">
              <el-tag :type="scope.row.isValid ? 'success' : 'danger'">
                {{ scope.row.isValid ? '有效' : '无效' }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="firstOrderTime" label="首次下单时间" width="150">
            <template slot-scope="scope">
              {{ scope.row.firstOrderTime ? formatDate(scope.row.firstOrderTime) : '-' }}
            </template>
          </el-table-column>
          
          <el-table-column prop="firstOrderAmount" label="首单金额" width="100">
            <template slot-scope="scope">
              {{ scope.row.firstOrderAmount ? formatAmount(scope.row.firstOrderAmount) : '-' }}
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-container">
          <el-pagination
            @size-change="handleRelationsSizeChange"
            @current-change="handleRelationsCurrentChange"
            :current-page="relationsQuery.pageNum"
            :page-size="relationsQuery.pageSize"
            :total="relationsTotal"
            layout="total, sizes, prev, pager, next">
          </el-pagination>
        </div>
      </el-tab-pane>

      <!-- 奖励记录 -->
      <el-tab-pane label="奖励记录" name="rewards">
        <el-table :data="rewardsList" v-loading="rewardsLoading" style="width: 100%">
          <el-table-column prop="rewardType" label="奖励类型" width="120">
            <template slot-scope="scope">
              {{ getRewardTypeName(scope.row.rewardType) }}
            </template>
          </el-table-column>
          
          <el-table-column prop="rewardValue" label="奖励金额" width="100">
            <template slot-scope="scope">
              <span class="reward-amount">{{ formatAmount(scope.row.rewardValue || scope.row.amount) }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="inviteeNickname" label="被邀请人" width="150">
            <template slot-scope="scope">
              {{ scope.row.inviteeNickname || '未设置' }}
            </template>
          </el-table-column>
          
          <el-table-column prop="sendTime" label="获得时间" width="150">
            <template slot-scope="scope">
              {{ formatDate(scope.row.sendTime || scope.row.createTime) }}
            </template>
          </el-table-column>
          
          <el-table-column prop="status" label="状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="getRewardStatusType(scope.row.status)">
                {{ getRewardStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="sendResult" label="备注" min-width="200">
            <template slot-scope="scope">
              {{ scope.row.sendResult || scope.row.remark || '-' }}
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-container">
          <el-pagination
            @size-change="handleRewardsSizeChange"
            @current-change="handleRewardsCurrentChange"
            :current-page="rewardsQuery.pageNum"
            :page-size="rewardsQuery.pageSize"
            :total="rewardsTotal"
            layout="total, sizes, prev, pager, next">
          </el-pagination>
        </div>
      </el-tab-pane>

      <!-- 提现记录 -->
      <el-tab-pane label="提现记录" name="withdraw">
        <el-table :data="withdrawList" v-loading="withdrawLoading" style="width: 100%">
          <el-table-column prop="withdrawSn" label="提现单号" width="180">
            <template slot-scope="scope">
              {{ scope.row.withdrawSn || '-' }}
            </template>
          </el-table-column>
          
          <el-table-column label="提现金额" width="120">
            <template slot-scope="scope">
              <div>
                <div><span class="withdraw-amount">{{ formatAmount(scope.row.applyAmount) }}</span></div>
                <div style="font-size: 12px; color: #999;">
                  手续费: {{ formatAmount(scope.row.feeAmount) }}
                </div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column label="提现方式" width="120">
            <template slot-scope="scope">
              {{ getWithdrawTypeName(scope.row.withdrawType) }}
            </template>
          </el-table-column>
          
          <el-table-column label="提现账号" width="180">
            <template slot-scope="scope">
              <div>
                <div>{{ scope.row.accountName || '-' }}</div>
                <div style="font-size: 12px; color: #999;">
                  {{ formatWithdrawAccount(scope.row.withdrawAccount, scope.row.withdrawType) }}
                </div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="applyTime" label="申请时间" width="150">
            <template slot-scope="scope">
              {{ formatDate(scope.row.applyTime) }}
            </template>
          </el-table-column>
          
          <el-table-column prop="status" label="状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="getWithdrawStatusType(scope.row.status)">
                {{ getWithdrawStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="processTime" label="处理时间" width="150">
            <template slot-scope="scope">
              {{ scope.row.processTime ? formatDate(scope.row.processTime) : '-' }}
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-container">
          <el-pagination
            @size-change="handleWithdrawSizeChange"
            @current-change="handleWithdrawCurrentChange"
            :current-page="withdrawQuery.pageNum"
            :page-size="withdrawQuery.pageSize"
            :total="withdrawTotal"
            layout="total, sizes, prev, pager, next">
          </el-pagination>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { 
  getMemberInviteSummary,
  getMemberInviteRelations,
  getMemberInviteRewards,
  getMemberInviteWithdraw
} from '@/api/member'
import { formatDate, formatAmount, formatPhone } from '@/utils/index'

export default {
  name: 'InviteDataTab',
  props: {
    memberId: {
      type: [String, Number],
      required: true
    }
  },
  data() {
    return {
      activeTab: 'relations',
      inviteSummary: null,
      
      // 邀请关系
      relationsList: [],
      relationsLoading: false,
      relationsTotal: 0,
      relationsQuery: {
        pageNum: 1,
        pageSize: 20
      },
      
      // 奖励记录
      rewardsList: [],
      rewardsLoading: false,
      rewardsTotal: 0,
      rewardsQuery: {
        pageNum: 1,
        pageSize: 20
      },
      
      // 提现记录
      withdrawList: [],
      withdrawLoading: false,
      withdrawTotal: 0,
      withdrawQuery: {
        pageNum: 1,
        pageSize: 20
      }
    }
  },
  created() {
    this.loadSummaryData()
    this.loadRelationsData()
  },
  methods: {
    formatDate,
    formatAmount,
    formatPhone,
    
    async loadSummaryData() {
      try {
        const response = await getMemberInviteSummary(this.memberId)
        if (response.code === 200) {
          this.inviteSummary = response.data
        }
      } catch (error) {
        console.error('获取邀请统计失败:', error)
      }
    },
    
    async loadRelationsData() {
      this.relationsLoading = true
      try {
        const response = await getMemberInviteRelations(this.memberId, this.relationsQuery)
        if (response.code === 200) {
          this.relationsList = response.data.list || []
          this.relationsTotal = response.data.total || 0
        } else {
          this.$message.error(response.message || '获取邀请关系失败')
        }
      } catch (error) {
        console.error('获取邀请关系失败:', error)
        this.$message.error('获取邀请关系失败')
      } finally {
        this.relationsLoading = false
      }
    },
    
    async loadRewardsData() {
      this.rewardsLoading = true
      try {
        const response = await getMemberInviteRewards(this.memberId, this.rewardsQuery)
        if (response.code === 200) {
          this.rewardsList = response.data.list || []
          this.rewardsTotal = response.data.total || 0
        } else {
          this.$message.error(response.message || '获取奖励记录失败')
        }
      } catch (error) {
        console.error('获取奖励记录失败:', error)
        this.$message.error('获取奖励记录失败')
      } finally {
        this.rewardsLoading = false
      }
    },
    
    async loadWithdrawData() {
      this.withdrawLoading = true
      try {
        const response = await getMemberInviteWithdraw(this.memberId, this.withdrawQuery)
        if (response.code === 200) {
          this.withdrawList = response.data.list || []
          this.withdrawTotal = response.data.total || 0
        } else {
          this.$message.error(response.message || '获取提现记录失败')
        }
      } catch (error) {
        console.error('获取提现记录失败:', error)
        this.$message.error('获取提现记录失败')
      } finally {
        this.withdrawLoading = false
      }
    },
    
    handleTabClick(tab) {
      if (tab.name === 'rewards' && this.rewardsList.length === 0) {
        this.loadRewardsData()
      } else if (tab.name === 'withdraw' && this.withdrawList.length === 0) {
        this.loadWithdrawData()
      }
    },
    
    // 邀请关系分页
    handleRelationsSizeChange(val) {
      this.relationsQuery.pageSize = val
      this.relationsQuery.pageNum = 1
      this.loadRelationsData()
    },
    
    handleRelationsCurrentChange(val) {
      this.relationsQuery.pageNum = val
      this.loadRelationsData()
    },
    
    // 奖励记录分页
    handleRewardsSizeChange(val) {
      this.rewardsQuery.pageSize = val
      this.rewardsQuery.pageNum = 1
      this.loadRewardsData()
    },
    
    handleRewardsCurrentChange(val) {
      this.rewardsQuery.pageNum = val
      this.loadRewardsData()
    },
    
    // 提现记录分页
    handleWithdrawSizeChange(val) {
      this.withdrawQuery.pageSize = val
      this.withdrawQuery.pageNum = 1
      this.loadWithdrawData()
    },
    
    handleWithdrawCurrentChange(val) {
      this.withdrawQuery.pageNum = val
      this.loadWithdrawData()
    },
    
    getRewardTypeName(type) {
      const typeMap = {
        1: '积分',
        2: '优惠券',
        3: '现金红包',
        4: '商品'
      }
      return typeMap[type] || '其他奖励'
    },
    
    getRewardStatusText(status) {
      const statusMap = {
        0: '待发放',
        1: '已发放',
        2: '已取消'
      }
      return statusMap[status] || '未知状态'
    },
    
    getRewardStatusType(status) {
      const typeMap = {
        0: 'warning',
        1: 'success',
        2: 'danger'
      }
      return typeMap[status] || 'info'
    },
    
    getWithdrawStatusText(status) {
      const statusMap = {
        0: '待审核',
        1: '审核通过',
        2: '提现成功',
        3: '提现失败',
        4: '已取消',
        5: '审核拒绝'
      }
      return statusMap[status] || '未知状态'
    },
    
    getWithdrawStatusType(status) {
      const typeMap = {
        0: 'warning',
        1: 'primary',
        2: 'success',
        3: 'danger',
        4: 'info',
        5: 'danger'
      }
      return typeMap[status] || 'info'
    },
    
    formatBankCard(cardNo) {
      if (!cardNo) return '-'
      return cardNo.replace(/(\d{4})(?=\d)/g, '$1 ')
    },
    
    getWithdrawTypeName(type) {
      const typeMap = {
        1: '微信钱包',
        2: '银行卡'
      }
      return typeMap[type] || '未知'
    },
    
    formatWithdrawAccount(account, type) {
      if (!account) return '-'
      
      if (type === 1) {
        // 微信提现，显示部分openid
        return account.length > 10 ? account.substring(0, 8) + '...' : account
      } else if (type === 2) {
        // 银行卡提现，格式化卡号
        return this.formatBankCard(account)
      }
      
      return account
    }
  }
}
</script>

<style scoped>
.invite-data-tab {
  padding: 20px 0;
}

.invite-stats {
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

.reward-amount {
  font-weight: bold;
  color: #67C23A;
}

.withdraw-amount {
  font-weight: bold;
  color: #E6A23C;
}

.el-table .cell {
  line-height: 1.4;
}

.el-table .cell > div {
  padding: 2px 0;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}
</style> 