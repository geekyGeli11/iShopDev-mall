<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>邀请配置管理</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="resetConfig">重置配置</el-button>
      </div>

      <el-tabs v-model="activeTab" type="card">
        <!-- 基础配置 -->
        <el-tab-pane label="基础配置" name="basic">
          <el-form ref="basicForm" :model="configData.basic" label-width="200px">
            <el-form-item label="邀请参数有效期(天)">
              <el-input-number v-model="configData.basic.param_expire_days" :min="1" :max="30" />
            </el-form-item>
            <el-form-item label="单个参数最大使用次数">
              <el-input-number v-model="configData.basic.param_max_use_count" :min="0" />
              <span class="text-muted">（0表示无限制）</span>
            </el-form-item>
            <el-form-item label="是否启用邀请奖励">
              <el-switch v-model="configData.basic.reward_enable" />
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 邀请者奖励 -->
        <el-tab-pane label="邀请者奖励" name="inviter">
          <el-form ref="inviterForm" :model="configData.inviterReward" label-width="200px">
            <h4>注册奖励</h4>
            <el-form-item label="注册奖励积分">
              <el-input-number v-model="configData.inviterReward.register_points" :min="0" />
            </el-form-item>
            <el-form-item label="奖励描述">
              <el-input v-model="configData.inviterReward.register_desc" />
            </el-form-item>
            
            <h4>首单奖励</h4>
            <el-form-item label="首单奖励积分">
              <el-input-number v-model="configData.inviterReward.first_order_points" :min="0" />
            </el-form-item>
            <el-form-item label="首单返现比例">
              <el-input-number v-model="configData.inviterReward.first_order_cashback_rate"
                              :min="0" :max="1" :step="0.01" :precision="2" />
              <span class="text-muted">（0.05表示5%）</span>
            </el-form-item>
            <el-form-item label="奖励描述">
              <el-input v-model="configData.inviterReward.first_order_desc" />
            </el-form-item>

            <h4>复购分佣配置</h4>
            <el-form-item label="复购分佣比例">
              <el-input-number v-model="configData.inviterReward.repurchase_commission_rate" :min="0" :max="1" :step="0.01" />
              <span class="text-muted">（例如：0.02表示2%）</span>
            </el-form-item>
            <el-form-item label="分佣有效期(天)">
              <el-input-number v-model="configData.inviterReward.repurchase_valid_days" :min="1" :max="365" />
              <span class="text-muted">（被邀请人注册后多少天内的消费可获得分佣）</span>
            </el-form-item>
            <el-form-item label="分佣描述">
              <el-input v-model="configData.inviterReward.repurchase_desc" />
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 被邀请者奖励 -->
        <el-tab-pane label="被邀请者奖励" name="invitee">
          <el-form ref="inviteeForm" :model="configData.inviteeReward" label-width="200px">
            <h4>注册奖励</h4>
            <el-form-item label="注册奖励积分">
              <el-input-number v-model="configData.inviteeReward.register_points" :min="0" />
            </el-form-item>
            <el-form-item label="注册奖励优惠券">
              <el-input v-model="configData.inviteeReward.register_coupon" placeholder="优惠券代码" />
            </el-form-item>
            <el-form-item label="奖励描述">
              <el-input v-model="configData.inviteeReward.register_desc" />
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 防刷机制 -->
        <el-tab-pane label="防刷机制" name="antiCheat">
          <el-form ref="antiCheatForm" :model="configData.antiCheat" label-width="200px">
            <el-form-item label="每IP每日最大注册数">
              <el-input-number v-model="configData.antiCheat.max_register_per_ip_daily" :min="1" />
            </el-form-item>
            <el-form-item label="每设备每日最大注册数">
              <el-input-number v-model="configData.antiCheat.max_register_per_device_daily" :min="1" />
            </el-form-item>
            <el-form-item label="奖励发放延迟时间(小时)">
              <el-input-number v-model="configData.antiCheat.reward_delay_hours" :min="0" />
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>

      <div style="text-align: center; margin-top: 20px;">
        <el-button @click="cancelEdit">取消</el-button>
        <el-button type="primary" @click="saveConfig" :loading="loading">保存配置</el-button>
      </div>
    </el-card>

    <!-- 配置变更历史 -->
    <el-card class="box-card" style="margin-top: 20px;">
      <div slot="header" class="clearfix">
        <span>配置变更历史</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="refreshHistory">刷新</el-button>
      </div>

      <el-table :data="historyList" style="width: 100%">
        <el-table-column prop="configKey" label="配置项" width="250" show-overflow-tooltip />
        <el-table-column label="变更内容" width="200">
          <template slot-scope="scope">
            <div v-if="scope.row.operationType === 'CREATE'" style="color: #67C23A;">
              <i class="el-icon-plus"></i> 新增: {{ scope.row.newValue }}
            </div>
            <div v-else-if="scope.row.operationType === 'UPDATE'" style="color: #E6A23C;">
              <i class="el-icon-edit"></i> {{ scope.row.oldValue }} → {{ scope.row.newValue }}
            </div>
            <div v-else-if="scope.row.operationType === 'DELETE'" style="color: #F56C6C;">
              <i class="el-icon-delete"></i> 删除: {{ scope.row.oldValue }}
            </div>
            <div v-else>
              {{ scope.row.configValue }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="configDesc" label="描述" min-width="150" show-overflow-tooltip />
        <el-table-column prop="changeUser" label="操作人" width="100" />
        <el-table-column prop="changeTime" label="变更时间" width="160">
          <template slot-scope="scope">
            {{ formatDate(scope.row.changeTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="changeReason" label="变更原因" width="120" />
        <el-table-column prop="operationType" label="操作类型" width="80">
          <template slot-scope="scope">
            <el-tag 
              :type="scope.row.operationType === 'CREATE' ? 'success' : 
                     scope.row.operationType === 'UPDATE' ? 'warning' : 'danger'"
              size="mini">
              {{ scope.row.operationType === 'CREATE' ? '新增' : 
                 scope.row.operationType === 'UPDATE' ? '修改' : '删除' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-show="historyTotal > 0"
        background
        :current-page="historyPageNum"
        :page-size="historyPageSize"
        :total="historyTotal"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      ></el-pagination>
    </el-card>
  </div>
</template>

<script>
import { getInviteConfig, updateInviteConfig, getConfigHistory } from '@/api/invite'
import { formatStandardDateTime } from '@/utils/date'

export default {
  name: 'InviteConfig',
  data() {
    return {
      activeTab: 'basic',
      loading: false,
      configData: {
        basic: {
          param_expire_days: 3,
          param_max_use_count: 0,
          reward_enable: true
        },
        inviterReward: {
          register_points: 50,
          register_desc: '邀请好友注册奖励',
          first_order_points: 100,
          first_order_cashback_rate: 0.05,
          first_order_desc: '邀请好友首单奖励',
          repurchase_commission_rate: 0.02,
          repurchase_valid_days: 180,
          repurchase_desc: '邀请好友复购分佣'
        },
        inviteeReward: {
          register_points: 100,
          register_coupon: 'NEWUSER15',
          register_desc: '新用户注册礼包'
        },
        antiCheat: {
          max_register_per_ip_daily: 5,
          max_register_per_device_daily: 3,
          reward_delay_hours: 24
        }
      },
      historyList: [],
      historyTotal: 0,
      historyPageNum: 1,
      historyPageSize: 10
    }
  },
  created() {
    this.getConfig()
    this.getConfigHistory()
  },
  methods: {
    async getConfig() {
      try {
        const response = await getInviteConfig()
        if (response.data) {
          this.configData = {
            basic: response.data.basic || this.configData.basic,
            inviterReward: response.data.inviterReward || this.configData.inviterReward,
            inviteeReward: response.data.inviteeReward || this.configData.inviteeReward,
            antiCheat: response.data.antiCheat || this.configData.antiCheat
          }
        }
      } catch (error) {
        this.$message.error('获取配置失败：' + error.message)
      }
    },
    async saveConfig() {
      this.loading = true
      try {
        // 辅助函数：确保值有效
        const ensureValue = (val, defaultVal = '') => {
          if (val === null || val === undefined || val === 'undefined' || val === 'null') {
            return String(defaultVal)
          }
          return String(val)
        }
        
        // 构建配置项数组
        const configItems = []
        
        console.log('保存前的配置数据:', this.configData)
        
        // 基础配置
        configItems.push(
          { key: 'invite.param.expire_days', value: ensureValue(this.configData.basic.param_expire_days, 3), desc: '邀请参数有效期（天）' },
          { key: 'invite.param.max_use_count', value: ensureValue(this.configData.basic.param_max_use_count, 0), desc: '单个邀请参数最大使用次数' },
          { key: 'invite.reward.enable', value: this.configData.basic.reward_enable ? '1' : '0', desc: '是否启用邀请奖励功能' }
        )
        
        // 邀请者奖励配置
        configItems.push(
          { key: 'invite.reward.inviter.register.points', value: ensureValue(this.configData.inviterReward.register_points, 50), desc: '邀请者-新用户注册奖励积分' },
          { key: 'invite.reward.inviter.register.desc', value: ensureValue(this.configData.inviterReward.register_desc, '邀请好友注册奖励'), desc: '邀请者注册奖励描述' },
          { key: 'invite.reward.inviter.first_order.points', value: ensureValue(this.configData.inviterReward.first_order_points, 100), desc: '邀请者-新用户首单奖励积分' },
          { key: 'invite.reward.inviter.first_order.cashback_rate', value: ensureValue(this.configData.inviterReward.first_order_cashback_rate, 0.05), desc: '邀请者-新用户首单返现比例' },
          { key: 'invite.reward.inviter.first_order.desc', value: ensureValue(this.configData.inviterReward.first_order_desc, '邀请好友首单奖励'), desc: '邀请者首单奖励描述' },
          { key: 'invite.reward.inviter.repurchase.commission_rate', value: ensureValue(this.configData.inviterReward.repurchase_commission_rate, 0.02), desc: '邀请者-复购分佣比例' },
          { key: 'invite.reward.inviter.repurchase.valid_days', value: ensureValue(this.configData.inviterReward.repurchase_valid_days, 180), desc: '复购分佣有效期（天）' },
          { key: 'invite.reward.inviter.repurchase.desc', value: ensureValue(this.configData.inviterReward.repurchase_desc, '邀请好友复购分佣'), desc: '复购分佣描述' }
        )
        
        // 被邀请者奖励配置
        configItems.push(
          { key: 'invite.reward.invitee.register.points', value: ensureValue(this.configData.inviteeReward.register_points, 100), desc: '被邀请者-注册奖励积分' },
          { key: 'invite.reward.invitee.register.coupon', value: ensureValue(this.configData.inviteeReward.register_coupon, 'NEWUSER15'), desc: '被邀请者-注册奖励优惠券代码' },
          { key: 'invite.reward.invitee.register.desc', value: ensureValue(this.configData.inviteeReward.register_desc, '新用户注册礼包'), desc: '被邀请者注册奖励描述' }
        )
        
        // 防刷机制配置
        configItems.push(
          { key: 'invite.anti_cheat.max_register_per_ip_daily', value: ensureValue(this.configData.antiCheat.max_register_per_ip_daily, 5), desc: '每个IP每日最大注册数' },
          { key: 'invite.anti_cheat.max_register_per_device_daily', value: ensureValue(this.configData.antiCheat.max_register_per_device_daily, 3), desc: '每个设备每日最大注册数' },
          { key: 'invite.anti_cheat.reward_delay_hours', value: ensureValue(this.configData.antiCheat.reward_delay_hours, 24), desc: '奖励发放延迟时间（小时）' }
        )
        
        console.log('构建的配置项:', configItems)
        
        const updateResponse = await updateInviteConfig({
          configItems,
          reason: '管理员配置更新'
        })
        
        if (updateResponse && updateResponse.code === 200) {
          this.$message.success('配置保存成功')
          // 延迟一下再刷新历史记录，确保后端数据已更新
          setTimeout(() => {
            this.refreshHistory()
          }, 500)
        } else {
          this.$message.error('配置保存失败：' + (updateResponse.message || '未知错误'))
        }
      } catch (error) {
        this.$message.error('配置保存失败：' + error.message)
      } finally {
        this.loading = false
      }
    },
    async getConfigHistory() {
      try {
        const response = await getConfigHistory({
          pageNum: this.historyPageNum,
          pageSize: this.historyPageSize
        })
        
        console.log('配置历史API响应:', response)
        
        if (response && response.code === 200 && response.data) {
          this.historyList = response.data.list || []
          this.historyTotal = response.data.total || 0
          
          console.log('历史记录数据:', {
            list: this.historyList,
            total: this.historyTotal
          })
          
          if (this.historyList.length === 0) {
            this.$message.info('暂无配置变更历史记录')
          }
        } else {
          console.error('API响应格式异常:', response)
          this.$message.warning('获取历史记录数据格式异常')
        }
      } catch (error) {
        console.error('获取配置历史失败:', error)
        this.$message.error('获取历史记录失败：' + (error.message || '未知错误'))
      }
    },
    refreshHistory() {
      this.historyPageNum = 1
      this.getConfigHistory()
    },
    resetConfig() {
      this.$confirm('确定要重置配置为默认值吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.getConfig()
        this.$message.success('配置已重置')
      })
    },
    cancelEdit() {
      this.getConfig()
    },
    handleSizeChange(val) {
      this.historyPageSize = val
      this.getConfigHistory()
    },
    handleCurrentChange(val) {
      this.historyPageNum = val
      this.getConfigHistory()
    },
    formatDate(date) {
      return formatStandardDateTime(date)
    }
  }
}
</script>

<style scoped>
.text-muted {
  color: #999;
  font-size: 12px;
  margin-left: 10px;
}

h4 {
  margin: 20px 0 10px 0;
  color: #303133;
  border-left: 4px solid #409EFF;
  padding-left: 10px;
}
</style>