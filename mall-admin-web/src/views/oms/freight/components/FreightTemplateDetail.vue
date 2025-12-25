<template>
  <el-card class="form-container" shadow="never">
    <el-form :model="freightTemplate"
             :rules="rules"
             ref="freightTemplateForm"
             label-width="150px"
             v-loading="formLoading">
             
      <!-- 模板名称 -->
      <div class="section-title">模板名称</div>
      <el-form-item label="模板名称：" prop="name">
        <el-input v-model="freightTemplate.name" 
                  placeholder="请输入模板名称，如：全国包邮模板" 
                  class="input-width"></el-input>
      </el-form-item>
      
      <!-- 包邮设置 -->
      <div class="section-title">包邮设置</div>
      
      <el-form-item label="包邮类型：">
        <el-radio-group v-model="freightTemplate.freeType" @change="handleFreeTypeChange">
          <el-radio :label="0">不包邮</el-radio>
          <el-radio :label="1">满金额包邮</el-radio>
          <el-radio :label="2">满件数包邮</el-radio>
          <el-radio :label="3">满重量包邮</el-radio>
          <el-radio :label="4">无条件包邮</el-radio>
        </el-radio-group>
      </el-form-item>
      
      <el-form-item label="包邮条件：" v-if="freightTemplate.freeType > 0 && freightTemplate.freeType < 4">
        <div v-if="freightTemplate.freeType === 1" class="free-condition">
          <span>满</span>
          <el-input-number v-model="freightTemplate.freeAmount" 
                           :precision="2" 
                           :min="0" 
                           placeholder="0.00"
                           style="width: 150px; margin: 0 10px"></el-input-number>
          <span>元包邮</span>
        </div>
        <div v-else-if="freightTemplate.freeType === 2" class="free-condition">
          <span>满</span>
          <el-input-number v-model="freightTemplate.freeCount" 
                           :min="1" 
                           placeholder="1"
                           style="width: 150px; margin: 0 10px"></el-input-number>
          <span>件包邮</span>
        </div>
        <div v-else-if="freightTemplate.freeType === 3" class="free-condition">
          <span>满</span>
          <el-input-number v-model="freightTemplate.freeWeight" 
                           :precision="2" 
                           :min="0" 
                           placeholder="0.00"
                           style="width: 150px; margin: 0 10px"></el-input-number>
          <span>kg包邮</span>
        </div>
      </el-form-item>
      
      <!-- 当选择无条件包邮时，隐藏后面所有配置 -->
      <template v-if="parseInt(freightTemplate.freeType) !== 4">
        <!-- 基础信息 -->
        <div class="section-title">基础信息</div>
        
        <el-form-item label="计费类型：" prop="chargeType">
          <el-radio-group v-model="freightTemplate.chargeType" @change="handleChargeTypeChange">
            <el-radio :label="1">按件数</el-radio>
            <el-radio :label="2">按重量</el-radio>
            <el-radio :label="3">按体积</el-radio>
            <el-radio :label="4">固定运费</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="排序：">
          <el-input-number v-model="freightTemplate.sort" 
                           :min="0" 
                           placeholder="数字越小排序越靠前"
                           class="input-width"></el-input-number>
        </el-form-item>
        
        <el-form-item label="是否默认：">
          <el-switch v-model="freightTemplate.isDefault" 
                     :active-value="true" 
                     :inactive-value="false"
                     active-text="是" 
                     inactive-text="否">
          </el-switch>
          <div class="tip-text">设为默认模板后，其他模板将自动取消默认状态</div>
        </el-form-item>
        
        <el-form-item label="备注：">
          <el-input v-model="freightTemplate.remark" 
                    type="textarea" 
                    :autosize="{ minRows: 2, maxRows: 4 }"
                    placeholder="请输入备注信息（选填）"
                    class="input-width"></el-input>
        </el-form-item>
        
        <!-- 运费规则配置 - 使用v-show替代v-if确保响应式更新 -->
        <div class="section-title">
          {{ showFixedConfig ? '固定运费配置' : '运费规则配置' }}
          <el-button v-show="!showFixedConfig"
                     style="float: right" 
                     type="primary" 
                     size="mini" 
                     @click="addRegionRule">
            <i class="el-icon-plus"></i> 添加规则
          </el-button>
        </div>
        
        <!-- 固定运费的简化配置 -->
        <div v-show="showFixedConfig" class="fixed-freight-config">
          <el-form-item label="固定运费：" prop="fixedAmount">
            <el-input-number v-model="freightTemplate.fixedAmount" 
                             :precision="2" 
                             :min="0" 
                             placeholder="0.00"
                             class="input-width"></el-input-number>
            <span style="margin-left: 10px">元</span>
          </el-form-item>
          <div class="tip-text">
            <i class="el-icon-info"></i>
            选择固定运费后，所有订单都将收取相同的运费金额
          </div>
        </div>
        
        <!-- 其他计费类型的运费规则配置 -->
        <div v-show="!showFixedConfig">
          <div v-if="freightTemplate.regionRules.length === 0" class="empty-rules">
            <div class="empty-tip">
              <i class="el-icon-warning" style="font-size: 48px; color: #ccc; margin-bottom: 10px;"></i>
              <p>暂无运费规则</p>
              <p>请添加运费计算规则</p>
              <el-button type="primary" @click="addDefaultRule" size="small">
                添加默认全国规则
              </el-button>
            </div>
          </div>
          
          <el-table v-else :data="freightTemplate.regionRules" style="width: 100%; margin-bottom: 20px" border>
            <el-table-column label="序号" type="index" width="60" align="center"></el-table-column>
            <el-table-column label="配送区域" width="150">
              <template slot-scope="scope">
                <el-tag v-if="scope.row.regionType === 1" type="success" size="small">全国</el-tag>
                <el-tag v-else-if="scope.row.regionType === 2" type="info" size="small">{{ scope.row.regionNames }}</el-tag>
                <el-tag v-else-if="scope.row.regionType === 3" type="warning" size="small">{{ scope.row.distanceStart }}-{{ scope.row.distanceEnd }}km</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="首件/首重" width="140">
              <template slot-scope="scope">
                <span class="rule-text">{{ scope.row.firstCount }}{{ getUnitText() }}</span>
                <span class="rule-price">{{ scope.row.firstAmount }}元</span>
              </template>
            </el-table-column>
            <el-table-column label="续件/续重" width="140">
              <template slot-scope="scope">
                <span class="rule-text">{{ scope.row.additionalCount }}{{ getUnitText() }}</span>
                <span class="rule-price">{{ scope.row.additionalAmount }}元</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" align="center">
              <template slot-scope="scope">
                <el-button size="mini" 
                           type="text" 
                           @click="editRegionRule(scope.$index, scope.row)">编辑</el-button>
                <el-button size="mini" 
                           type="text" 
                           style="color: #f56c6c"
                           @click="deleteRegionRule(scope.$index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </template>
      
      <!-- 操作按钮 -->
      <el-form-item>
        <el-button type="primary" @click="handleSubmitClick">{{ isEdit ? '保存修改' : '保存' }}</el-button>
        <el-button @click="onCancel">取消</el-button>
      </el-form-item>
    </el-form>
    
    <!-- 区域规则编辑对话框 -->
    <el-dialog title="配置运费规则" 
               :visible.sync="regionRuleDialog.visible" 
               width="600px"
               :close-on-click-modal="false"
               @close="handleRegionDialogClose">
      <el-form :model="regionRuleDialog.regionRule" 
               ref="regionRuleForm" 
               label-width="120px" 
               size="small">
               
        <el-form-item label="区域类型：" prop="regionType">
          <el-radio-group v-model="regionRuleDialog.regionRule.regionType" @change="handleRegionTypeChange">
            <el-radio :label="1">全国</el-radio>
            <el-radio :label="2">指定省市</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="选择区域：" v-if="regionRuleDialog.regionRule.regionType === 2">
          <el-input v-model="regionRuleDialog.regionRule.regionNames" 
                    placeholder="请输入省市名称，多个用逗号分隔，如：北京,上海,广东" 
                    style="width: 100%">
          </el-input>
          <div class="tip-text">示例：北京,上海,广东 或 北京市,上海市,广东省</div>
        </el-form-item>
        
        <el-form-item :label="getFirstLabel()" prop="firstCount">
          <el-input-number v-model="regionRuleDialog.regionRule.firstCount" 
                           :precision="getPrecision()" 
                           :min="0" 
                           placeholder="首件数量"
                           style="width: 120px"></el-input-number>
          <span style="margin: 0 10px">{{ getUnitText() }}</span>
          <el-input-number v-model="regionRuleDialog.regionRule.firstAmount" 
                           :precision="2" 
                           :min="0" 
                           placeholder="首件运费"
                           style="width: 120px"></el-input-number>
          <span style="margin-left: 10px">元</span>
        </el-form-item>
        
        <el-form-item :label="getAdditionalLabel()" prop="additionalCount">
          <el-input-number v-model="regionRuleDialog.regionRule.additionalCount" 
                           :precision="getPrecision()" 
                           :min="0" 
                           placeholder="续件数量"
                           style="width: 120px"></el-input-number>
          <span style="margin: 0 10px">{{ getUnitText() }}</span>
          <el-input-number v-model="regionRuleDialog.regionRule.additionalAmount" 
                           :precision="2" 
                           :min="0" 
                           placeholder="续件运费"
                           style="width: 120px"></el-input-number>
          <span style="margin-left: 10px">元</span>
        </el-form-item>
        
        <div class="dialog-tip">
          <i class="el-icon-info"></i>
          <span v-if="regionRuleDialog.regionRule.regionType === 1">全国规则适用于所有地区</span>
          <span v-else-if="regionRuleDialog.regionRule.regionType === 2">指定省市规则优先级高于全国规则</span>
        </div>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="regionRuleDialog.visible = false">取 消</el-button>
        <el-button type="primary" @click="confirmRegionRule">确 定</el-button>
      </div>
    </el-dialog>
  </el-card>
</template>

<script>
  import { 
    createFreightTemplate, 
    getFreightTemplateDetail, 
    updateFreightTemplate 
  } from '@/api/freight'

  const defaultFreightTemplate = {
    name: '',
    chargeType: 1,
    deliveryType: 1, // 默认快递配送
    freeType: 0,
    freeAmount: 0,
    freeCount: 0,
    freeWeight: 0,
    sort: 0,
    remark: '',
    fixedAmount: 0, // 固定运费金额
    regionRules: [],
    isDefault: false // 新增：是否默认模板
  }

  export default {
    name: 'FreightTemplateDetail',
    props: {
      isEdit: {
        type: Boolean,
        default: false
      }
    },
    computed: {
      isFixedFreight() {
        return parseInt(this.freightTemplate.chargeType) === 4
      },
      isFreeShipping() {
        return parseInt(this.freightTemplate.freeType) === 4
      },
      showFixedConfig() {
        return parseInt(this.freightTemplate.chargeType) === 4
      }
    },

    data() {
      return {
        formLoading: false,
        freightTemplate: Object.assign({}, defaultFreightTemplate),
        regionRuleDialog: {
          visible: false,
          isEdit: false,
          editIndex: -1,
          regionRule: {
            regionType: 1,
            regionNames: '',
            distanceStart: 0,
            distanceEnd: 0,
            firstCount: 1,
            firstAmount: 0,
            additionalCount: 1,
            additionalAmount: 0
          }
        },
        rules: {
          name: [
            { required: true, message: '请输入模板名称', trigger: 'blur' },
            { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
          ],
          chargeType: [
            { required: true, message: '请选择计费类型', trigger: 'change' }
          ],
          fixedAmount: [
            { required: true, message: '请输入固定运费金额', trigger: 'blur', validator: this.validateFixedAmount }
          ]
        }
      }
    },
    created() {
      if (this.isEdit) {
        this.getTemplateDetail()
      }
    },
    methods: {
      // 验证固定运费金额
      validateFixedAmount(rule, value, callback) {
        if (parseInt(this.freightTemplate.chargeType) === 4 && (value === null || value === undefined || value < 0)) {
          return callback(new Error('请输入有效的固定运费金额'))
        }
        callback()
      },
      getTemplateDetail() {
        let id = this.$route.params.id
        if (id != null) {
          this.formLoading = true
          getFreightTemplateDetail(id).then(response => {
            this.formLoading = false
            let data = response.data
            
            this.freightTemplate = {
              id: data.id,
              name: data.name,
              chargeType: parseInt(data.chargeType) || 1,
              deliveryType: parseInt(data.deliveryType) || 1,
              freeType: parseInt(data.freeType) || 0,
              freeAmount: data.freeAmount || 0,
              freeCount: data.freeCount || 0,
              freeWeight: data.freeWeight || 0,
              sort: data.sort || 0,
              remark: data.remark || '',
              fixedAmount: 0,
              regionRules: data.regionRules || [],
              isDefault: data.isDefault || false // 获取默认状态
            }
            
            // 如果是固定运费类型，从运费规则中提取固定金额
            if (parseInt(data.chargeType) === 4 && data.regionRules && data.regionRules.length === 1) {
              const rule = data.regionRules[0]
              if (rule.regionType === 1 && rule.additionalAmount === 0) {
                // 这是一个模拟的固定运费规则，提取固定金额
                this.freightTemplate.fixedAmount = rule.firstAmount || 0
                // 清空运费规则，用于固定运费模式显示
                this.freightTemplate.regionRules = []
              }
            }
          }).catch((error) => {
            this.formLoading = false
            this.$message.error('获取模板详情失败')
          })
        }
      },
      handleChargeTypeChange(newValue) {
        // 如果选择固定运费，清空区域规则
        if (parseInt(newValue) === 4) {
          this.freightTemplate.regionRules = []
          if (!this.freightTemplate.fixedAmount) {
            this.freightTemplate.fixedAmount = 0
          }
          return
        }
        
        // 如果在编辑模式且已有规则，提示用户
        if (this.isEdit && this.freightTemplate.regionRules && this.freightTemplate.regionRules.length > 0) {
          this.$confirm('修改计费类型将清空所有运费规则，是否继续?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            this.freightTemplate.regionRules = []
            this.freightTemplate.fixedAmount = 0
          }).catch(() => {
            // 取消则恢复原来的计费类型
            this.getTemplateDetail()
          })
        } else {
          // 新增模式或无规则时直接重置
          this.freightTemplate.regionRules = []
          this.freightTemplate.fixedAmount = 0
        }
      },
      handleFreeTypeChange(newValue) {
        // 重置包邮条件
        this.freightTemplate.freeAmount = 0
        this.freightTemplate.freeCount = 0
        this.freightTemplate.freeWeight = 0
        
        // 如果选择无条件包邮，清空运费规则和固定金额
        if (parseInt(newValue) === 4) {
          this.freightTemplate.regionRules = []
          this.freightTemplate.fixedAmount = 0
          // 计费类型重置为按件数（避免与固定运费冲突）
          this.freightTemplate.chargeType = 1
        }
      },
      addRegionRule() {
        this.regionRuleDialog.visible = true
        this.regionRuleDialog.isEdit = false
        this.regionRuleDialog.editIndex = -1
        this.resetRegionRule()
      },
      addDefaultRule() {
        // 添加默认全国运费规则
        const defaultRule = {
          regionType: 1,
          regionNames: '',
          distanceStart: 0,
          distanceEnd: 0,
          firstCount: 1,
          firstAmount: 10,
          additionalCount: 1,
          additionalAmount: 5
        }
        this.freightTemplate.regionRules.push(defaultRule)
        this.$message.success('已添加默认全国运费规则，您可以编辑修改')
      },
      editRegionRule(index, row) {
        this.regionRuleDialog.visible = true
        this.regionRuleDialog.isEdit = true
        this.regionRuleDialog.editIndex = index
        this.regionRuleDialog.regionRule = { ...row }
      },
      deleteRegionRule(index) {
        this.$confirm('确认删除该运费规则?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.freightTemplate.regionRules.splice(index, 1)
          this.$message.success('删除成功')
        })
      },
      confirmRegionRule() {
        // 表单验证
        if (this.regionRuleDialog.regionRule.regionType === 2 && !this.regionRuleDialog.regionRule.regionNames) {
          this.$message.error('请输入区域名称')
          return
        }
        
        if (this.regionRuleDialog.isEdit) {
          this.$set(this.freightTemplate.regionRules, this.regionRuleDialog.editIndex, { ...this.regionRuleDialog.regionRule })
        } else {
          this.freightTemplate.regionRules.push({ ...this.regionRuleDialog.regionRule })
        }
        this.regionRuleDialog.visible = false
        this.$message.success(this.regionRuleDialog.isEdit ? '修改成功' : '添加成功')
      },
      handleRegionDialogClose() {
        this.resetRegionRule()
      },
      handleRegionTypeChange() {
        this.regionRuleDialog.regionRule.regionNames = ''
        this.regionRuleDialog.regionRule.distanceStart = 0
        this.regionRuleDialog.regionRule.distanceEnd = 0
      },
      resetRegionRule() {
        this.regionRuleDialog.regionRule = {
          regionType: 1,
          regionNames: '',
          distanceStart: 0,
          distanceEnd: 0,
          firstCount: 1,
          firstAmount: 0,
          additionalCount: 1,
          additionalAmount: 0
        }
      },
      getUnitText() {
        const map = {
          1: '件',
          2: 'kg',
          3: 'm³',
          4: '次'
        }
        return map[this.freightTemplate.chargeType] || '件'
      },
      getPrecision() {
        return this.freightTemplate.chargeType === 2 || this.freightTemplate.chargeType === 3 ? 2 : 0
      },
      getFirstLabel() {
        const map = {
          1: '首件：',
          2: '首重：',
          3: '首体积：',
          4: '固定运费：'
        }
        return map[this.freightTemplate.chargeType] || '首件：'
      },
      getAdditionalLabel() {
        const map = {
          1: '续件：',
          2: '续重：',
          3: '续体积：',
          4: '固定运费：'
        }
        return map[this.freightTemplate.chargeType] || '续件：'
      },
      handleSubmitClick() {
        this.onSubmit()
      },
      onSubmit() {
        this.$refs.freightTemplateForm.validate((valid) => {
          if (valid) {
            // 无条件包邮时不需要验证运费规则
            if (this.isFreeShipping) {
              this.submitForm()
              return
            }
            
            // 固定运费时：创建一个全国运费规则来模拟固定运费
            if (this.showFixedConfig) {
              if (this.freightTemplate.fixedAmount === null || this.freightTemplate.fixedAmount === undefined || this.freightTemplate.fixedAmount < 0) {
                this.$message.error('请输入有效的固定运费金额')
                return
              }
              
              // 用全国运费规则模拟固定运费
              this.freightTemplate.regionRules = [{
                regionType: 1, // 全国
                regionNames: '',
                distanceStart: 0,
                distanceEnd: 0,
                firstCount: 1,
                firstAmount: this.freightTemplate.fixedAmount,
                additionalCount: 1,
                additionalAmount: 0 // 续件运费为0，实现固定运费效果
              }]
              
              this.submitForm()
              return
            }
            
            // 其他情况需要验证运费规则
            if (!this.freightTemplate.regionRules || this.freightTemplate.regionRules.length === 0) {
              this.$message.error('请至少添加一条运费规则')
              return
            }
            
            this.submitForm()
          } else {
            this.$message.error('请完善表单信息')
            return false
          }
        })
      },
      submitForm() {
        const action = this.isEdit ? '修改' : '保存'
        this.$confirm(`是否确认${action}?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          // 准备提交数据，移除后端不支持的字段
          const submitData = { ...this.freightTemplate }
          delete submitData.fixedAmount // 移除后端不支持的字段
          
          if (this.isEdit) {
            updateFreightTemplate(submitData.id, submitData).then(response => {
              this.$message.success('修改成功')
              this.$router.back()
            }).catch(error => {
              this.$message.error('修改失败')
            })
          } else {
            createFreightTemplate(submitData).then(response => {
              this.$message.success('保存成功')
              this.$router.back()
            }).catch(error => {
              this.$message.error('保存失败')
            })
          }
        })
      },
      onCancel() {
        this.$router.back()
      }
    }
  }
</script>

<style scoped>
  .input-width {
    width: 370px;
  }

  .form-container {
    width: 1000px;
    margin: 30px auto;
  }
  
  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: #333;
    margin: 30px 0 20px 0;
    padding-bottom: 10px;
    border-bottom: 2px solid #409eff;
    position: relative;
  }
  
  .section-title:first-child {
    margin-top: 0;
  }
  
  .free-condition {
    display: flex;
    align-items: center;
  }
  
  .fixed-freight-config {
    margin-bottom: 20px;
  }
  
  .empty-rules {
    text-align: center;
    padding: 40px 20px;
    margin-bottom: 20px;
    border: 1px dashed #ddd;
    border-radius: 4px;
    background-color: #fafafa;
  }
  
  .empty-tip {
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  
  .empty-tip p {
    margin: 5px 0;
    color: #666;
    font-size: 14px;
  }
  
  .rule-text {
    margin-right: 10px;
  }
  
  .rule-price {
    color: #f56c6c;
    font-weight: bold;
  }
  
  .dialog-tip {
    padding: 10px;
    background: #f4f4f5;
    border-radius: 4px;
    margin-top: 15px;
    color: #666;
    font-size: 12px;
  }
  
  .dialog-tip i {
    margin-right: 5px;
    color: #409eff;
  }
  
  .tip-text {
    color: #999;
    font-size: 12px;
    margin-top: 5px;
  }
</style> 