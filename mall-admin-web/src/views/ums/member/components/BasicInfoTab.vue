<template>
  <div class="basic-info-tab">
    <!-- 数据加载状态 -->
    <div v-if="!memberDetail" class="loading-placeholder">
      <el-card>
        <div style="text-align: center; padding: 40px 0;">
          <i class="el-icon-loading" style="font-size: 24px; color: #409EFF;"></i>
          <p style="margin-top: 15px; color: #909399;">正在加载用户信息...</p>
        </div>
      </el-card>
    </div>
    
    <!-- 主要内容 -->
    <el-row :gutter="20" v-else>
      <el-col :span="12">
        <el-card header="个人资料">
          <el-form ref="basicForm" :model="editForm" :rules="rules" label-width="100px">
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="editForm.nickname" :disabled="!editMode"></el-input>
            </el-form-item>
            
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="editForm.gender" :disabled="!editMode">
                <el-radio :label="0">未知</el-radio>
                <el-radio :label="1">男</el-radio>
                <el-radio :label="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
            
            <el-form-item label="生日" prop="birthday">
              <el-date-picker
                v-model="editForm.birthday"
                type="date"
                placeholder="选择日期"
                value-format="yyyy-MM-dd"
                :disabled="!editMode"
                style="width: 100%;">
              </el-date-picker>
            </el-form-item>
            
            <el-form-item label="城市" prop="city">
              <el-input v-model="editForm.city" :disabled="!editMode"></el-input>
            </el-form-item>
            
            <el-form-item label="职业" prop="job">
              <el-input v-model="editForm.job" :disabled="!editMode"></el-input>
            </el-form-item>
            
            <el-form-item label="个性签名" prop="personalizedSignature">
              <el-input
                v-model="editForm.personalizedSignature"
                type="textarea"
                :rows="3"
                :disabled="!editMode"
                placeholder="请输入个性签名">
              </el-input>
            </el-form-item>
            
            <el-form-item label="状态" prop="status">
              <el-switch
                v-model="editForm.status"
                :active-value="1"
                :inactive-value="0"
                active-text="启用"
                inactive-text="禁用"
                :disabled="!editMode">
              </el-switch>
            </el-form-item>
            
            <el-form-item>
              <el-button v-if="!editMode" type="primary" @click="enableEdit">编辑信息</el-button>
              <template v-else>
                <el-button type="primary" @click="saveBasicInfo" :loading="saving">保存</el-button>
                <el-button @click="cancelEdit">取消</el-button>
              </template>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card header="账户信息">
          <div class="account-info">
            <div class="info-item">
              <span class="label">会员码：</span>
              <span class="value">{{ memberDetail.memberCode || '-' }}</span>
            </div>
            
            <div class="info-item">
              <span class="label">手机号：</span>
              <span class="value">{{ formatPhone(memberDetail.phone) }}</span>
            </div>
            
            <div class="info-item">
              <span class="label">会员等级：</span>
              <span class="value">{{ memberDetail.memberLevelName || '普通会员' }}</span>
            </div>
            
            <div class="info-item">
              <span class="label">当前积分：</span>
              <span class="value highlight">{{ memberDetail.integration || 0 }}</span>
            </div>
            
            <div class="info-item">
              <span class="label">成长值：</span>
              <span class="value">{{ memberDetail.growth || 0 }}</span>
            </div>
            
            <div class="info-item">
              <span class="label">历史积分：</span>
              <span class="value">{{ memberDetail.historyIntegration || 0 }}</span>
            </div>
            
            <div class="info-item">
              <span class="label">注册时间：</span>
              <span class="value">{{ formatDate(memberDetail.createTime) }}</span>
            </div>
            
            <div class="info-item">
              <span class="label">用户来源：</span>
              <span class="value">{{ getSourceTypeName(memberDetail.sourceType) }}</span>
            </div>
          </div>
        </el-card>
        
        <el-card header="统计概览" style="margin-top: 20px;">
          <div class="stats-grid">
            <div class="stat-card">
              <div class="stat-number">{{ memberDetail.orderCount || 0 }}</div>
              <div class="stat-label">总订单数</div>
            </div>
            
            <div class="stat-card">
              <div class="stat-number">{{ formatAmount(memberDetail.orderAmount) }}</div>
              <div class="stat-label">累计消费</div>
            </div>
            
            <div class="stat-card">
              <div class="stat-number">{{ memberDetail.couponCount || 0 }}</div>
              <div class="stat-label">优惠券数</div>
            </div>
            
            <div class="stat-card">
              <div class="stat-number">{{ memberDetail.inviteFriendCount || 0 }}</div>
              <div class="stat-label">邀请人数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { updateMember } from '@/api/member'
import { formatDate, formatPhone, formatAmount } from '@/utils/index'

export default {
  name: 'BasicInfoTab',
  props: {
    memberDetail: {
      type: Object,
      required: false,
      default: null
    }
  },
  data() {
    return {
      editMode: false,
      saving: false,
      editForm: {},
      originalForm: {},
      rules: {
        nickname: [
          { max: 64, message: '昵称长度不能超过64个字符', trigger: 'blur' }
        ],
        city: [
          { max: 64, message: '城市长度不能超过64个字符', trigger: 'blur' }
        ],
        job: [
          { max: 100, message: '职业长度不能超过100个字符', trigger: 'blur' }
        ],
        personalizedSignature: [
          { max: 200, message: '个性签名长度不能超过200个字符', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    memberDetail: {
      handler(newVal) {
        if (newVal) {
          this.initEditForm()
        }
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    formatDate,
    formatPhone,
    formatAmount,
    
    initEditForm() {
      this.editForm = {
        nickname: this.memberDetail.nickname || '',
        gender: this.memberDetail.gender || 0,
        birthday: this.memberDetail.birthday || '',
        city: this.memberDetail.city || '',
        job: this.memberDetail.job || '',
        personalizedSignature: this.memberDetail.personalizedSignature || '',
        status: this.memberDetail.status
      }
      this.originalForm = { ...this.editForm }
    },
    
    enableEdit() {
      this.editMode = true
    },
    
    cancelEdit() {
      this.editForm = { ...this.originalForm }
      this.editMode = false
      this.$refs.basicForm.clearValidate()
    },
    
    async saveBasicInfo() {
      try {
        await this.$refs.basicForm.validate()
        this.saving = true
        
        const response = await updateMember(this.memberDetail.id, this.editForm)
        if (response.code === 200) {
          this.$message.success('用户信息更新成功')
          this.editMode = false
          this.originalForm = { ...this.editForm }
          this.$emit('refresh')
        } else {
          this.$message.error(response.message || '更新失败')
        }
      } catch (error) {
        console.error('更新用户信息失败:', error)
        if (error !== false) { // 表单验证失败时会返回false
          this.$message.error('更新用户信息失败')
        }
      } finally {
        this.saving = false
      }
    },
    
    getSourceTypeName(sourceType) {
      const sourceMap = {
        0: 'PC端',
        1: '小程序',
        2: 'APP',
        3: 'H5'
      }
      return sourceMap[sourceType] || '未知'
    }
  }
}
</script>

<style scoped>
.basic-info-tab {
  padding: 20px 0;
}

.account-info {
  padding: 10px 0;
}

.info-item {
  display: flex;
  margin-bottom: 12px;
  line-height: 1.5;
}

.info-item .label {
  width: 90px;
  color: #909399;
  flex-shrink: 0;
}

.info-item .value {
  color: #303133;
  flex: 1;
}

.info-item .value.highlight {
  color: #409EFF;
  font-weight: bold;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.stat-card {
  text-align: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 4px;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}
</style> 