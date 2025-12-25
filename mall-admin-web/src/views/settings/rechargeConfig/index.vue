<template>
  <div class="app-container">
    <el-card class="operate-container" shadow="never">
      <div>
        <i class="el-icon-setting"></i>
        <span>会员余额充值赠送配置</span>
        <el-tooltip class="item" effect="light" content="配置会员充值时的金额选项和赠送规则" placement="top">
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
        <el-button type="warning" size="small" @click="showStatistics">
          <i class="el-icon-data-line"></i>
          统计数据
        </el-button>
      </div>
    </el-card>

    <el-card class="form-container" shadow="never">
      <el-tabs v-model="activeTab" type="border-card">
        <!-- 基础配置 -->
        <el-tab-pane label="基础配置" name="basic">
          <el-form :model="configForm" :rules="rules" ref="configForm" label-width="150px">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="最小充值金额" prop="minAmount">
                  <el-input-number
                    v-model="configForm.minAmount"
                    :precision="2"
                    :step="1"
                    :min="0.01"
                    :max="1000"
                    placeholder="请输入最小充值金额"
                    style="width: 100%"
                  >
                    <template slot="append">元</template>
                  </el-input-number>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="最大充值金额" prop="maxAmount">
                  <el-input-number
                    v-model="configForm.maxAmount"
                    :precision="2"
                    :step="100"
                    :min="1"
                    :max="100000"
                    placeholder="请输入最大充值金额"
                    style="width: 100%"
                  >
                    <template slot="append">元</template>
                  </el-input-number>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="启用充值功能">
                  <el-switch
                    v-model="configForm.enableRecharge"
                    active-text="启用"
                    inactive-text="禁用"
                    disabled
                  ></el-switch>
                  <div class="form-tip">充值功能默认启用（数据库中无此配置项）</div>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="启用消费功能">
                  <el-switch
                    v-model="configForm.enableConsume"
                    active-text="启用"
                    inactive-text="禁用"
                  ></el-switch>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-tab-pane>

        <!-- 快速充值选项 -->
        <el-tab-pane label="快速充值选项" name="quickAmounts">
          <div style="margin-bottom: 15px">
            <el-button type="primary" size="small" @click="addQuickAmount">
              <i class="el-icon-plus"></i>
              添加选项
            </el-button>
            <el-button type="info" size="small" @click="validateQuickAmounts">
              <i class="el-icon-check"></i>
              验证配置
            </el-button>
          </div>

          <el-table :data="configForm.quickAmounts" border style="width: 100%">
            <el-table-column prop="amount" label="充值金额" width="120">
              <template slot-scope="scope">
                <el-input-number
                  v-model="scope.row.amount"
                  :precision="2"
                  :step="10"
                  :min="0.01"
                  size="small"
                  style="width: 100%"
                ></el-input-number>
              </template>
            </el-table-column>
            <el-table-column prop="bonusBalance" label="赠送余额" width="120">
              <template slot-scope="scope">
                <el-input-number
                  v-model="scope.row.bonusBalance"
                  :precision="2"
                  :step="1"
                  :min="0"
                  size="small"
                  style="width: 100%"
                ></el-input-number>
              </template>
            </el-table-column>
            <el-table-column prop="bonusIntegration" label="赠送积分" width="120">
              <template slot-scope="scope">
                <el-input-number
                  v-model="scope.row.bonusIntegration"
                  :step="10"
                  :min="0"
                  size="small"
                  style="width: 100%"
                ></el-input-number>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="描述信息" min-width="200">
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.description"
                  placeholder="请输入描述信息"
                  size="small"
                ></el-input>
              </template>
            </el-table-column>
            <el-table-column prop="enabled" label="启用状态" width="100">
              <template slot-scope="scope">
                <el-switch
                  v-model="scope.row.enabled"
                  size="small"
                ></el-switch>
              </template>
            </el-table-column>
            <el-table-column prop="sort" label="排序" width="80">
              <template slot-scope="scope">
                <el-input-number
                  v-model="scope.row.sort"
                  :min="1"
                  :max="99"
                  size="small"
                  style="width: 100%"
                ></el-input-number>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template slot-scope="scope">
                <el-button
                  type="danger"
                  size="mini"
                  @click="removeQuickAmount(scope.$index)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 赠送配置 -->
        <el-tab-pane label="赠送配置" name="bonus">
          <el-form :model="configForm" :rules="rules" ref="bonusForm" label-width="180px">
            <el-form-item label="启用赠送功能">
              <el-switch
                v-model="configForm.enableBonus"
                active-text="启用"
                inactive-text="禁用"
              ></el-switch>
              <div class="form-tip">关闭后将不会有任何充值赠送</div>
            </el-form-item>

            <el-form-item label="享受赠送的最小金额" prop="bonusMinAmount">
              <el-input-number
                v-model="configForm.bonusMinAmount"
                :precision="2"
                :step="10"
                :min="0"
                :max="1000"
                placeholder="请输入最小金额"
                style="width: 200px"
              >
                <template slot="append">元</template>
              </el-input-number>
              <div class="form-tip">充值金额达到此数值才能享受赠送</div>
            </el-form-item>

            <el-form-item label="赠送余额比例" prop="bonusBalanceRate">
              <el-input-number
                v-model="configForm.bonusBalanceRate"
                :precision="4"
                :step="0.01"
                :min="0"
                :max="1"
                placeholder="请输入比例"
                style="width: 200px"
              >
                <template slot="append">%</template>
              </el-input-number>
              <div class="form-tip">按充值金额的百分比赠送余额，0表示不按比例赠送</div>
            </el-form-item>

            <el-form-item label="赠送积分比例" prop="bonusIntegrationRate">
              <el-input-number
                v-model="configForm.bonusIntegrationRate"
                :precision="4"
                :step="0.01"
                :min="0"
                :max="10"
                placeholder="请输入比例"
                style="width: 200px"
              >
                <template slot="append">倍</template>
              </el-input-number>
              <div class="form-tip">按充值金额的倍数赠送积分，0表示不按比例赠送</div>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 配置历史 -->
        <el-tab-pane label="配置历史" name="history">
          <el-table :data="historyList" border style="width: 100%" v-loading="historyLoading">
            <el-table-column prop="configKey" label="配置项" width="200"></el-table-column>
            <el-table-column prop="configValue" label="配置值" min-width="200" show-overflow-tooltip></el-table-column>
            <el-table-column prop="configDesc" label="描述" width="150"></el-table-column>
            <el-table-column prop="updateTime" label="更新时间" width="180">
              <template slot-scope="scope">
                {{ formatDateTime(scope.row.updateTime) }}
              </template>
            </el-table-column>
          </el-table>
          
          <div class="pagination-container">
            <el-pagination
              background
              @size-change="handleHistorySizeChange"
              @current-change="handleHistoryCurrentChange"
              layout="total, sizes, prev, pager, next, jumper"
              :current-page="historyQuery.pageNum"
              :page-sizes="[10, 20, 50]"
              :page-size="historyQuery.pageSize"
              :total="historyTotal"
            ></el-pagination>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 预览对话框 -->
    <el-dialog title="配置预览" :visible.sync="previewDialogVisible" width="800px">
      <div v-if="previewData">
        <h4>基础配置</h4>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="充值金额范围">
            {{ previewData.minAmount }}元 - {{ previewData.maxAmount }}元
          </el-descriptions-item>
          <el-descriptions-item label="充值功能">
            {{ previewData.enableRecharge ? '启用' : '禁用' }}
          </el-descriptions-item>
          <el-descriptions-item label="消费功能">
            {{ previewData.enableConsume ? '启用' : '禁用' }}
          </el-descriptions-item>
          <el-descriptions-item label="赠送功能">
            {{ previewData.enableBonus ? '启用' : '禁用' }}
          </el-descriptions-item>
        </el-descriptions>

        <h4 style="margin-top: 20px">快速充值选项</h4>
        <el-table :data="previewData.quickAmounts" border size="small">
          <el-table-column prop="amount" label="充值金额" width="100">
            <template slot-scope="scope">{{ scope.row.amount }}元</template>
          </el-table-column>
          <el-table-column prop="bonusBalance" label="赠送余额" width="100">
            <template slot-scope="scope">{{ scope.row.bonusBalance }}元</template>
          </el-table-column>
          <el-table-column prop="bonusIntegration" label="赠送积分" width="100">
            <template slot-scope="scope">{{ scope.row.bonusIntegration }}分</template>
          </el-table-column>
          <el-table-column prop="totalValue" label="总价值" width="100">
            <template slot-scope="scope">{{ scope.row.totalValue }}元</template>
          </el-table-column>
          <el-table-column prop="discountRate" label="优惠比例" width="100">
            <template slot-scope="scope">{{ scope.row.discountRate }}%</template>
          </el-table-column>
          <el-table-column prop="description" label="描述" min-width="150"></el-table-column>
        </el-table>

        <div v-if="previewData.impacts && previewData.impacts.length > 0" style="margin-top: 20px">
          <h4>配置影响</h4>
          <el-alert
            v-for="(impact, index) in previewData.impacts"
            :key="index"
            :title="impact"
            type="warning"
            style="margin-bottom: 10px"
          ></el-alert>
        </div>

        <div style="margin-top: 20px">
          <h4>注意事项</h4>
          <el-alert
            title="充值功能默认启用，该配置项在数据库中不存在"
            type="info"
            :closable="false"
          ></el-alert>
        </div>
      </div>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="previewDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>

    <!-- 统计数据对话框 -->
    <el-dialog title="充值统计数据" :visible.sync="statisticsDialogVisible" width="600px">
      <div v-if="statisticsData">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="总充值金额">
            {{ statisticsData.totalRechargeAmount }}元
          </el-descriptions-item>
          <el-descriptions-item label="总赠送金额">
            {{ statisticsData.totalBonusAmount }}元
          </el-descriptions-item>
          <el-descriptions-item label="充值次数">
            {{ statisticsData.totalRechargeCount }}次
          </el-descriptions-item>
          <el-descriptions-item label="赠送次数">
            {{ statisticsData.totalBonusCount }}次
          </el-descriptions-item>
          <el-descriptions-item label="平均充值金额">
            {{ statisticsData.averageRechargeAmount }}元
          </el-descriptions-item>
        </el-descriptions>
      </div>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="statisticsDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getRechargeConfig,
  updateRechargeConfig,
  getRechargeConfigHistory,
  validateQuickAmounts,
  getRechargeStatistics,
  resetRechargeConfig,
  previewRechargeConfig
} from '@/api/rechargeConfig'
import { formatDateTime } from '@/utils/date'

export default {
  name: 'RechargeConfig',
  data() {
    return {
      activeTab: 'basic',
      saving: false,
      previewDialogVisible: false,
      statisticsDialogVisible: false,
      historyLoading: false,
      configForm: {
        minAmount: 1.00,
        maxAmount: 50000.00,
        enableRecharge: true,
        enableConsume: true,
        enableBonus: true,
        bonusBalanceRate: 0.00,
        bonusIntegrationRate: 0.00,
        bonusMinAmount: 50.00,
        quickAmounts: [],
        updateRemark: ''
      },
      rules: {
        minAmount: [
          { required: true, message: '请输入最小充值金额', trigger: 'blur' },
          { type: 'number', min: 0.01, message: '最小充值金额必须大于0', trigger: 'blur' }
        ],
        maxAmount: [
          { required: true, message: '请输入最大充值金额', trigger: 'blur' },
          { type: 'number', min: 1, message: '最大充值金额必须大于0', trigger: 'blur' }
        ],
        bonusMinAmount: [
          { type: 'number', min: 0, message: '最小金额不能为负数', trigger: 'blur' }
        ],
        bonusBalanceRate: [
          { type: 'number', min: 0, max: 1, message: '比例必须在0-1之间', trigger: 'blur' }
        ],
        bonusIntegrationRate: [
          { type: 'number', min: 0, max: 10, message: '比例必须在0-10之间', trigger: 'blur' }
        ]
      },
      previewData: null,
      statisticsData: null,
      historyList: [],
      historyTotal: 0,
      historyQuery: {
        pageNum: 1,
        pageSize: 10
      }
    }
  },
  created() {
    this.getConfig()
    this.getConfigHistory()
  },
  methods: {
    formatDateTime,

    getConfig() {
      getRechargeConfig().then(response => {
        if (response && response.code === 200 && response.data) {
          this.configForm = { ...this.configForm, ...response.data }

          // 确保quickAmounts数组中的每个选项都有enabled和sort字段
          if (this.configForm.quickAmounts) {
            this.configForm.quickAmounts = this.configForm.quickAmounts.map((option, index) => ({
              ...option,
              enabled: option.enabled !== undefined ? option.enabled : true,
              sort: option.sort !== undefined ? option.sort : index + 1
            }))
          }

          this.$message.success('配置加载成功')
        } else {
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
          this.saving = true

          // 验证最小金额不能大于最大金额
          if (this.configForm.minAmount >= this.configForm.maxAmount) {
            this.$message.error('最小充值金额不能大于等于最大充值金额')
            this.saving = false
            return
          }

          updateRechargeConfig(this.configForm).then(response => {
            if (response && response.code === 200) {
              this.$message.success('配置保存成功')
              this.getConfig() // 重新加载配置
              this.getConfigHistory() // 刷新历史记录
            } else {
              this.$message.error(response.message || '配置保存失败')
            }
          }).catch(error => {
            console.error('保存配置失败:', error)
            this.$message.error('保存配置失败：' + (error.message || '网络错误'))
          }).finally(() => {
            this.saving = false
          })
        } else {
          this.$message.error('请检查表单填写是否正确')
        }
      })
    },

    handleResetConfig() {
      this.$confirm('确定要恢复默认配置吗？所有当前配置将被重置。', '确认重置', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        resetRechargeConfig().then(response => {
          if (response && response.code === 200) {
            this.$message.success('配置已重置为默认值')
            this.getConfig()
            this.getConfigHistory()
          } else {
            this.$message.error(response.message || '重置配置失败')
          }
        }).catch(error => {
          console.error('重置配置失败:', error)
          this.$message.error('重置配置失败：' + (error.message || '网络错误'))
        })
      })
    },

    handlePreviewConfig() {
      previewRechargeConfig(this.configForm).then(response => {
        if (response && response.code === 200) {
          this.previewData = response.data
          this.previewDialogVisible = true
        } else {
          this.$message.error(response.message || '预览配置失败')
        }
      }).catch(error => {
        console.error('预览配置失败:', error)
        this.$message.error('预览配置失败：' + (error.message || '网络错误'))
      })
    },

    showStatistics() {
      getRechargeStatistics().then(response => {
        if (response && response.code === 200) {
          this.statisticsData = response.data
          this.statisticsDialogVisible = true
        } else {
          this.$message.error(response.message || '获取统计数据失败')
        }
      }).catch(error => {
        console.error('获取统计数据失败:', error)
        this.$message.error('获取统计数据失败：' + (error.message || '网络错误'))
      })
    },

    addQuickAmount() {
      const newOption = {
        amount: 100.00,
        bonusBalance: 0.00,
        bonusIntegration: 0,
        description: '充值100元',
        enabled: true,
        sort: this.configForm.quickAmounts.length + 1
      }
      this.configForm.quickAmounts.push(newOption)
    },

    removeQuickAmount(index) {
      this.configForm.quickAmounts.splice(index, 1)
      // 重新排序
      this.configForm.quickAmounts.forEach((item, idx) => {
        item.sort = idx + 1
      })
    },

    validateQuickAmounts() {
      if (this.configForm.quickAmounts.length === 0) {
        this.$message.warning('请先添加快速充值选项')
        return
      }

      validateQuickAmounts({ quickAmounts: this.configForm.quickAmounts }).then(response => {
        if (response && response.code === 200) {
          const result = response.data
          if (result.valid) {
            this.$message.success('配置验证通过')
            if (result.warnings && result.warnings.length > 0) {
              this.$alert(result.warnings.join('\n'), '验证警告', {
                type: 'warning'
              })
            }
          } else {
            this.$alert(result.errors.join('\n'), '验证失败', {
              type: 'error'
            })
          }
        } else {
          this.$message.error(response.message || '验证失败')
        }
      }).catch(error => {
        console.error('验证配置失败:', error)
        this.$message.error('验证配置失败：' + (error.message || '网络错误'))
      })
    },

    getConfigHistory() {
      this.historyLoading = true
      getRechargeConfigHistory(this.historyQuery).then(response => {
        if (response && response.code === 200) {
          this.historyList = response.data || []
          this.historyTotal = response.total || 0
        } else {
          this.$message.error(response.message || '获取配置历史失败')
        }
      }).catch(error => {
        console.error('获取配置历史失败:', error)
        this.$message.error('获取配置历史失败：' + (error.message || '网络错误'))
      }).finally(() => {
        this.historyLoading = false
      })
    },

    handleHistorySizeChange(val) {
      this.historyQuery.pageSize = val
      this.getConfigHistory()
    },

    handleHistoryCurrentChange(val) {
      this.historyQuery.pageNum = val
      this.getConfigHistory()
    }
  }
}
</script>

<style lang="scss" scoped>
.app-container {
  .operate-container {
    margin-bottom: 20px;

    .el-card__body {
      padding: 20px;
    }
  }

  .form-container {
    .el-card__body {
      padding: 20px;
    }

    .el-tabs__content {
      padding-top: 20px;
    }
  }

  .form-tip {
    font-size: 12px;
    color: #999;
    margin-top: 5px;
    line-height: 1.4;
  }

  .pagination-container {
    margin-top: 20px;
    text-align: center;
  }

  .el-descriptions {
    margin-bottom: 20px;
  }

  .dialog-footer {
    text-align: center;
  }

  .el-input-number {
    .el-input__inner {
      text-align: left;
    }
  }

  .el-table {
    .el-input-number {
      width: 100%;
    }

    .el-input {
      width: 100%;
    }
  }

  .el-alert {
    .el-alert__title {
      font-size: 13px;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .app-container {
    .el-col {
      margin-bottom: 20px;
    }

    .el-table {
      font-size: 12px;

      .el-input-number,
      .el-input {
        font-size: 12px;
      }
    }

    .el-dialog {
      width: 95% !important;
      margin: 0 auto;
    }
  }
}

// 深度选择器修复
::v-deep .el-input-number .el-input__inner {
  text-align: left;
}

::v-deep .el-tabs__item {
  font-weight: 500;
}

::v-deep .el-form-item__label {
  font-weight: 500;
  color: #606266;
}

::v-deep .el-descriptions__label {
  font-weight: 500;
}
</style>
