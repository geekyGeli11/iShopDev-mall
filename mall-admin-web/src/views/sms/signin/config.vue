<template>
  <div class="app-container">
    <el-card class="operate-container" shadow="never">
      <i class="el-icon-setting"></i>
      <span>签到配置</span>
      <el-button 
        style="float: right" 
        type="primary" 
        @click="handleSave"
        :loading="saveLoading"
        size="small">
        保存配置
      </el-button>
    </el-card>

    <el-card class="form-container" shadow="never">
      <el-form 
        ref="configForm" 
        :model="configForm" 
        :rules="configRules" 
        label-width="150px">
        
        <el-form-item label="配置名称：" prop="configName">
          <el-input v-model="configForm.configName" placeholder="输入配置名称"></el-input>
        </el-form-item>

        <el-form-item label="基础积分：" prop="basePoints">
          <el-input-number 
            v-model="configForm.basePoints" 
            :min="1" 
            :max="100"
            placeholder="初次签到获得积分"></el-input-number>
          <span class="input-tip">初次签到获得的积分数量</span>
        </el-form-item>

        <el-form-item label="递增积分：" prop="incrementPoints">
          <el-input-number 
            v-model="configForm.incrementPoints" 
            :min="1" 
            :max="50"
            placeholder="连续签到每日递增积分"></el-input-number>
          <span class="input-tip">连续签到每日递增的积分数量</span>
        </el-form-item>

        <el-form-item label="每日上限：" prop="maxDailyPoints">
          <el-input-number 
            v-model="configForm.maxDailyPoints" 
            :min="1" 
            :max="200"
            placeholder="每日最高可获得积分"></el-input-number>
          <span class="input-tip">每日最高可获得的积分数量</span>
        </el-form-item>

        <el-form-item label="连签天数：" prop="continuousDaysForReward">
          <el-input-number 
            v-model="configForm.continuousDaysForReward" 
            :min="1" 
            :max="365"
            placeholder="连签奖励所需天数"></el-input-number>
          <span class="input-tip">连续签到多少天可获得奖励</span>
        </el-form-item>

        <el-form-item label="奖励优惠券：" prop="rewardCouponId">
          <el-select 
            v-model="configForm.rewardCouponId" 
            placeholder="选择奖励优惠券" 
            clearable>
            <el-option 
              v-for="coupon in couponList" 
              :key="coupon.id" 
              :label="coupon.name" 
              :value="coupon.id">
            </el-option>
          </el-select>
          <span class="input-tip">连签奖励发放的优惠券</span>
        </el-form-item>

        <el-form-item label="签到周期：" prop="cycleDays">
          <el-input-number 
            v-model="configForm.cycleDays" 
            :min="1" 
            :max="365"
            placeholder="签到周期天数"></el-input-number>
          <span class="input-tip">签到周期（天），超过此天数重置连签</span>
        </el-form-item>

        <el-form-item label="启用状态：" prop="isActive">
          <el-switch 
            v-model="configForm.isActive" 
            :active-value="1" 
            :inactive-value="0"
            active-text="启用"
            inactive-text="禁用">
          </el-switch>
        </el-form-item>

      </el-form>
    </el-card>
  </div>
</template>

<script>
import { getSigninConfig, updateSigninConfig } from '@/api/signin'
import { fetchList as fetchCouponList } from '@/api/coupon'

export default {
  name: 'SigninConfig',
  data() {
    return {
      configForm: {
        configName: '默认签到配置',
        basePoints: 5,
        incrementPoints: 3,
        maxDailyPoints: 30,
        continuousDaysForReward: 30,
        rewardCouponId: null,
        cycleDays: 30,
        isActive: 1
      },
      configRules: {
        configName: [
          { required: true, message: '请输入配置名称', trigger: 'blur' }
        ],
        basePoints: [
          { required: true, message: '请输入基础积分', trigger: 'blur' }
        ],
        incrementPoints: [
          { required: true, message: '请输入递增积分', trigger: 'blur' }
        ],
        maxDailyPoints: [
          { required: true, message: '请输入每日上限', trigger: 'blur' }
        ],
        continuousDaysForReward: [
          { required: true, message: '请输入连签天数', trigger: 'blur' }
        ],
        cycleDays: [
          { required: true, message: '请输入签到周期', trigger: 'blur' }
        ]
      },
      couponList: [],
      saveLoading: false
    }
  },
  created() {
    this.getConfig()
    this.getCouponList()
  },
  methods: {
    async getConfig() {
      try {
        const result = await getSigninConfig()
        if (result.code === 200 && result.data) {
          this.configForm = result.data
        }
      } catch (error) {
        console.log('获取配置失败:', error)
      }
    },
    async getCouponList() {
      try {
        const result = await fetchCouponList({ pageNum: 1, pageSize: 100 })
        if (result.code === 200) {
          this.couponList = result.data.list || []
        }
      } catch (error) {
        console.log('获取优惠券列表失败:', error)
      }
    },
    handleSave() {
      this.$refs['configForm'].validate(async (valid) => {
        if (valid) {
          this.saveLoading = true
          try {
            const result = await updateSigninConfig(this.configForm)
            if (result.code === 200) {
              this.$message.success('配置保存成功')
            } else {
              this.$message.error(result.message || '配置保存失败')
            }
          } catch (error) {
            this.$message.error('配置保存失败')
          } finally {
            this.saveLoading = false
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.form-container {
  margin-top: 15px;
}

.input-tip {
  margin-left: 10px;
  color: #999;
  font-size: 12px;
}
</style> 