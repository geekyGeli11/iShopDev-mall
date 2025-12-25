<template>
  <div class="app-container">
    <el-card class="operate-container" shadow="never">
      <div>
        <i class="el-icon-setting"></i>
        <span>提现配置管理</span>
        <el-tooltip class="item" effect="light" content="配置系统的提现相关参数，包括手续费、金额限制等" placement="top">
          <el-button size="mini" type="text" icon="el-icon-question"></el-button>
        </el-tooltip>
      </div>
      <div style="margin-top: 15px">
        <el-button type="primary" size="small" @click="handleSaveConfig" :loading="saving">
          <i class="el-icon-check"></i>
          保存配置
        </el-button>
        <el-button type="info" size="small" @click="handleResetConfig">
          <i class="el-icon-refresh"></i>
          恢复默认
        </el-button>
        <el-button type="success" size="small" @click="handlePreviewConfig">
          <i class="el-icon-view"></i>
          预览效果
        </el-button>
      </div>
    </el-card>

    <!-- 配置表单 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <!-- 基础配置 -->
      <el-col :span="12">
        <el-card shadow="never">
          <div slot="header" class="card-header">
            <span><i class="el-icon-money"></i> 金额限制配置</span>
          </div>
          <el-form ref="configForm" :model="configForm" :rules="configRules" label-width="140px">
            <el-form-item label="最小提现金额" prop="minWithdrawAmount">
              <el-input-number 
                v-model="configForm.minWithdrawAmount" 
                :precision="2" 
                :step="1" 
                :min="0.01" 
                :max="1000"
                style="width: 200px">
              </el-input-number>
              <span style="margin-left: 10px; color: #909399;">元</span>
            </el-form-item>
            
            <el-form-item label="最大提现金额" prop="maxWithdrawAmount">
              <el-input-number 
                v-model="configForm.maxWithdrawAmount" 
                :precision="2" 
                :step="100" 
                :min="1" 
                :max="100000"
                style="width: 200px">
              </el-input-number>
              <span style="margin-left: 10px; color: #909399;">元</span>
            </el-form-item>
            
            <el-form-item label="每日提现次数" prop="maxDailyCount">
              <el-input-number 
                v-model="configForm.maxDailyCount" 
                :step="1" 
                :min="1" 
                :max="10"
                style="width: 200px">
              </el-input-number>
              <span style="margin-left: 10px; color: #909399;">次</span>
            </el-form-item>
            
            <el-form-item label="月提现限额" prop="monthlyLimit">
              <el-input-number 
                v-model="configForm.monthlyLimit" 
                :precision="2" 
                :step="1000" 
                :min="0" 
                :max="1000000"
                style="width: 200px">
              </el-input-number>
              <span style="margin-left: 10px; color: #909399;">元（0表示不限制）</span>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 手续费配置 -->
      <el-col :span="12">
        <el-card shadow="never">
          <div slot="header" class="card-header">
            <span><i class="el-icon-coin"></i> 手续费配置</span>
          </div>
          <el-form ref="feeForm" :model="configForm" :rules="configRules" label-width="140px">
            <el-form-item label="微信手续费率" prop="wechatFeeRate">
              <el-input-number
                v-model="configForm.wechatFeeRate"
                :precision="4"
                :step="0.001"
                :min="0"
                :max="1"
                style="width: 200px">
              </el-input-number>
              <span style="margin-left: 10px; color: #909399;">（{{ (configForm.wechatFeeRate * 100).toFixed(2) }}%）</span>
            </el-form-item>
            

            
            <el-form-item label="最小手续费" prop="minFeeAmount">
              <el-input-number 
                v-model="configForm.minFeeAmount" 
                :precision="2" 
                :step="0.01" 
                :min="0" 
                :max="10"
                style="width: 200px">
              </el-input-number>
              <span style="margin-left: 10px; color: #909399;">元</span>
            </el-form-item>
            
            <el-form-item label="最大手续费" prop="maxFeeAmount">
              <el-input-number 
                v-model="configForm.maxFeeAmount" 
                :precision="2" 
                :step="1" 
                :min="0.01" 
                :max="1000"
                style="width: 200px">
              </el-input-number>
              <span style="margin-left: 10px; color: #909399;">元</span>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <!-- 时间配置 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card shadow="never">
          <div slot="header" class="card-header">
            <span><i class="el-icon-time"></i> 时间配置</span>
          </div>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form :model="configForm" label-width="140px">
                <el-form-item label="审核时限">
                  <el-input-number 
                    v-model="configForm.auditTimeLimit" 
                    :step="1" 
                    :min="1" 
                    :max="72"
                    style="width: 200px">
                  </el-input-number>
                  <span style="margin-left: 10px; color: #909399;">小时</span>
                </el-form-item>
                
                <el-form-item label="微信到账时间">
                  <el-input-number 
                    v-model="configForm.wechatArrivalDays" 
                    :step="1" 
                    :min="1" 
                    :max="7"
                    style="width: 200px">
                  </el-input-number>
                  <span style="margin-left: 10px; color: #909399;">工作日</span>
                </el-form-item>
                

              </el-form>
            </el-col>
            
            <el-col :span="8">
              <el-form :model="configForm" label-width="140px">
                <el-form-item label="自动审核">
                  <el-switch 
                    v-model="configForm.autoAudit"
                    active-text="开启"
                    inactive-text="关闭">
                  </el-switch>
                  <div style="font-size: 12px; color: #909399; margin-top: 5px">
                    开启后符合条件的申请将自动通过审核
                  </div>
                </el-form-item>
                
                <el-form-item label="自动审核金额" v-show="configForm.autoAudit">
                  <el-input-number 
                    v-model="configForm.autoAuditAmount" 
                    :precision="2" 
                    :step="10" 
                    :min="0" 
                    :max="1000"
                    style="width: 200px">
                  </el-input-number>
                  <span style="margin-left: 10px; color: #909399;">元以下自动通过</span>
                </el-form-item>
                
                <el-form-item label="通知开关">
                  <el-switch 
                    v-model="configForm.notificationEnabled"
                    active-text="开启"
                    inactive-text="关闭">
                  </el-switch>
                </el-form-item>
              </el-form>
            </el-col>
            
            <el-col :span="8">
              <el-form :model="configForm" label-width="140px">
                <el-form-item label="提现时段限制">
                  <el-switch 
                    v-model="configForm.timeRestriction"
                    active-text="开启"
                    inactive-text="关闭">
                  </el-switch>
                </el-form-item>
                
                <el-form-item label="开始时间" v-show="configForm.timeRestriction">
                  <el-time-picker
                    v-model="configForm.startTime"
                    placeholder="选择开始时间"
                    format="HH:mm"
                    value-format="HH:mm"
                    style="width: 200px">
                  </el-time-picker>
                </el-form-item>
                
                <el-form-item label="结束时间" v-show="configForm.timeRestriction">
                  <el-time-picker
                    v-model="configForm.endTime"
                    placeholder="选择结束时间"
                    format="HH:mm"
                    value-format="HH:mm"
                    style="width: 200px">
                  </el-time-picker>
                </el-form-item>
              </el-form>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <!-- 预览对话框 -->
    <el-dialog title="配置预览" :visible.sync="previewDialogVisible" width="600px">
      <div class="preview-content">
        <h4>用户提现体验预览</h4>
        <el-divider></el-divider>
        
        <div class="preview-item">
          <strong>金额限制：</strong>
          单次提现 ¥{{ configForm.minWithdrawAmount }} - ¥{{ configForm.maxWithdrawAmount }}
        </div>
        
        <div class="preview-item">
          <strong>提现次数：</strong>
          每日最多 {{ configForm.maxDailyCount }} 次
          <span v-if="configForm.monthlyLimit > 0">，每月限额 ¥{{ configForm.monthlyLimit }}</span>
        </div>
        
        <div class="preview-item">
          <strong>手续费示例：</strong>
          <ul>
            <li>微信提现 ¥100：手续费 ¥{{ calculateFee(100, 'wechat').toFixed(2) }}，实际到账 ¥{{ (100 - calculateFee(100, 'wechat')).toFixed(2) }}</li>
          </ul>
        </div>
        
        <div class="preview-item">
          <strong>到账时间：</strong>
          微信钱包 {{ configForm.wechatArrivalDays }} 个工作日
        </div>
        
        <div class="preview-item" v-if="configForm.autoAudit">
          <strong>自动审核：</strong>
          ¥{{ configForm.autoAuditAmount }} 以下申请自动通过
        </div>
        
        <div class="preview-item" v-if="configForm.timeRestriction">
          <strong>提现时段：</strong>
          {{ configForm.startTime }} - {{ configForm.endTime }}
        </div>
      </div>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="previewDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getWithdrawConfig, updateWithdrawConfig } from '@/api/invite'

export default {
  name: 'WithdrawConfig',
  data() {
    return {
      saving: false,
      previewDialogVisible: false,
      configForm: {
        // 金额配置
        minWithdrawAmount: 10.00,
        maxWithdrawAmount: 5000.00,
        maxDailyCount: 3,
        monthlyLimit: 0,
        
        // 手续费配置
        wechatFeeRate: 0.02,
        minFeeAmount: 0.01,
        maxFeeAmount: 50.00,

        // 时间配置
        auditTimeLimit: 24,
        wechatArrivalDays: 3,
        autoAudit: false,
        autoAuditAmount: 50.00,
        notificationEnabled: true,
        timeRestriction: false,
        startTime: '09:00',
        endTime: '18:00'
      },
      configRules: {
        minWithdrawAmount: [
          { required: true, message: '请设置最小提现金额', trigger: 'blur' }
        ],
        maxWithdrawAmount: [
          { required: true, message: '请设置最大提现金额', trigger: 'blur' }
        ],
        wechatFeeRate: [
          { required: true, message: '请设置微信手续费率', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getConfig()
  },
  methods: {
    getConfig() {
      getWithdrawConfig().then(response => {
        console.log('获取配置响应:', response)
        if (response && response.code === 200 && response.data) {
          console.log('后端返回的配置数据:', response.data)
          this.configForm = { ...this.configForm, ...response.data }
          console.log('合并后的配置数据:', this.configForm)
          this.$message.success('配置加载成功')
        } else {
          console.warn('获取配置失败或数据为空:', response)
          this.$message.warning('获取配置失败，使用默认配置')
        }
      }).catch(error => {
        console.error('获取配置API调用失败:', error)
        this.$message.error('获取配置失败：' + (error.message || '网络错误'))
      })
    },

    handleSaveConfig() {
      this.$refs.configForm.validate((valid) => {
        if (valid) {
          // 验证逻辑
          if (this.configForm.minWithdrawAmount >= this.configForm.maxWithdrawAmount) {
            this.$message.error('最小提现金额不能大于等于最大提现金额')
            return
          }
          
          if (this.configForm.minFeeAmount >= this.configForm.maxFeeAmount) {
            this.$message.error('最小手续费不能大于等于最大手续费')
            return
          }

          this.saving = true
          updateWithdrawConfig(this.configForm).then(response => {
            this.$message.success('配置保存成功')
          }).catch(error => {
            this.$message.error('配置保存失败：' + (error.message || '未知错误'))
          }).finally(() => {
            this.saving = false
          })
        }
      })
    },

    handleResetConfig() {
      this.$confirm('确定要恢复默认配置吗？所有当前配置将被重置。', '确认重置', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.configForm = {
          minWithdrawAmount: 10.00,
          maxWithdrawAmount: 5000.00,
          maxDailyCount: 3,
          monthlyLimit: 0,
          wechatFeeRate: 0.02,
          minFeeAmount: 0.01,
          maxFeeAmount: 50.00,
          auditTimeLimit: 24,
          wechatArrivalDays: 3,
          autoAudit: false,
          autoAuditAmount: 50.00,
          notificationEnabled: true,
          timeRestriction: false,
          startTime: '09:00',
          endTime: '18:00'
        }
        this.$message.success('已恢复默认配置')
      })
    },

    handlePreviewConfig() {
      this.previewDialogVisible = true
    },

    calculateFee(amount, type) {
      const rate = type === 'wechat' ? this.configForm.wechatFeeRate : this.configForm.bankFeeRate
      let fee = amount * rate
      
      if (fee < this.configForm.minFeeAmount) {
        fee = this.configForm.minFeeAmount
      }
      if (fee > this.configForm.maxFeeAmount) {
        fee = this.configForm.maxFeeAmount
      }
      
      return fee
    }
  }
}
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.card-header {
  font-weight: bold;
  color: #303133;
}

.preview-content {
  line-height: 1.8;
}

.preview-item {
  margin-bottom: 15px;
  padding-left: 10px;
}

.preview-item ul {
  margin-top: 5px;
  padding-left: 20px;
}

.preview-item li {
  margin-bottom: 5px;
}
</style> 