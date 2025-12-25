<template>
  <div class="app-container">
    <!-- 用户基本信息卡片 -->
    <el-card class="user-info-card" style="margin-bottom: 20px;">
      <div slot="header" class="clearfix">
        <span>用户信息</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="$router.go(-1)">
          返回列表
        </el-button>
      </div>
      <div class="user-info-content" v-if="memberDetail">
        <div class="user-avatar">
          <div class="avatar-wrapper">
            <img v-if="getAvatarUrl(memberDetail.icon)" 
                 :src="getAvatarUrl(memberDetail.icon)" 
                 class="avatar-img"
                 @error="handleAvatarError">
            <div v-else class="avatar-placeholder">
              <i class="el-icon-user-solid"></i>
            </div>
          </div>
        </div>
        <div class="user-basic-info">
          <h3>{{ memberDetail.nickname || '未设置昵称' }}</h3>
          <p><strong>会员码：</strong>{{ memberDetail.memberCode }}</p>
          <p><strong>手机号：</strong>{{ formatPhone(memberDetail.phone) }}</p>
          <p><strong>注册时间：</strong>{{ formatDate(memberDetail.createTime) }}</p>
          <p><strong>会员等级：</strong>{{ memberDetail.memberLevelName || '普通会员' }}</p>
        </div>
        <div class="user-stats">
          <div class="stat-item">
            <div class="stat-value">{{ memberDetail.integration || 0 }}</div>
            <div class="stat-label">当前积分</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ memberDetail.orderCount || 0 }}</div>
            <div class="stat-label">订单数量</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ formatAmount(memberDetail.orderAmount) }}</div>
            <div class="stat-label">消费总额</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">
              <el-tag :type="memberDetail.status === 1 ? 'success' : 'danger'">
                {{ memberDetail.status === 1 ? '正常' : '禁用' }}
              </el-tag>
            </div>
            <div class="stat-label">账户状态</div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- Tab切换区域 -->
    <el-card>
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="基本信息" name="basic">
          <BasicInfoTab 
            :member-id="memberId" 
            :member-detail="memberDetail"
            @refresh="loadMemberDetail" />
        </el-tab-pane>
        
        <el-tab-pane label="订单记录" name="orders">
          <OrderRecordsTab :member-id="memberId" />
        </el-tab-pane>
        
        <el-tab-pane label="积分管理" name="integration">
          <IntegrationManageTab 
            :member-id="memberId" 
            :current-integration="memberDetail ? memberDetail.integration : 0"
            @refresh="loadMemberDetail" />
        </el-tab-pane>
        
        <el-tab-pane label="优惠券管理" name="coupon">
          <CouponManageTab :member-id="memberId" />
        </el-tab-pane>
        
        <el-tab-pane label="邀请数据" name="invite">
          <InviteDataTab :member-id="memberId" />
        </el-tab-pane>
        
        <el-tab-pane label="用户画像" name="profile">
          <UserProfileTab :member-id="memberId" />
        </el-tab-pane>
        
        <el-tab-pane label="储值记录" name="recharge">
          <RechargeHistoryTab :member-id="memberId" />
        </el-tab-pane>
        
        <el-tab-pane label="余额变动记录" name="consumption">
          <ConsumptionHistoryTab :member-id="memberId" />
        </el-tab-pane>
        
        <el-tab-pane label="签到记录" name="signin">
          <SignInHistoryTab :member-id="memberId" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import { getMemberDetail } from '@/api/member'
import { formatDate, formatAmount, formatPhone } from '@/utils/index'
import BasicInfoTab from './components/BasicInfoTab'
import OrderRecordsTab from './components/OrderRecordsTab'
import IntegrationManageTab from './components/IntegrationManageTab'
import CouponManageTab from './components/CouponManageTab'
import InviteDataTab from './components/InviteDataTab'
import UserProfileTab from './components/UserProfileTab'
import RechargeHistoryTab from './components/RechargeHistoryTab'
import ConsumptionHistoryTab from './components/ConsumptionHistoryTab'
import SignInHistoryTab from './components/SignInHistoryTab'

export default {
  name: 'MemberDetail',
  components: {
    BasicInfoTab,
    OrderRecordsTab,
    IntegrationManageTab,
    CouponManageTab,
    InviteDataTab,
    UserProfileTab,
    RechargeHistoryTab,
    ConsumptionHistoryTab,
    SignInHistoryTab
  },
  data() {
    return {
      memberId: null,
      memberDetail: null,
      activeTab: 'basic',
      loading: false
    }
  },
  created() {
    this.memberId = this.$route.params.id
    this.loadMemberDetail()
  },
  methods: {
    formatDate,
    formatAmount,
    formatPhone,
    
    getAvatarUrl(iconUrl) {
      // 如果有头像URL且有效，返回头像URL
      if (iconUrl && iconUrl.trim() !== '') {
        return iconUrl
      }
      // 返回null，让el-avatar使用默认的图标
      return null
    },
    
    handleAvatarError(e) {
      // 头像加载失败时的处理
      e.target.style.display = 'none'
      e.target.nextElementSibling.style.display = 'flex'
    },
    
    async loadMemberDetail() {
      if (!this.memberId) {
        this.$message.error('用户ID不能为空')
        return
      }
      
      this.loading = true
      try {
        const response = await getMemberDetail(this.memberId)
        if (response.code === 200) {
          this.memberDetail = response.data
        } else {
          this.$message.error(response.message || '获取用户详情失败')
        }
      } catch (error) {
        console.error('获取用户详情失败:', error)
        this.$message.error('获取用户详情失败')
      } finally {
        this.loading = false
      }
    },
    
    handleTabClick(tab) {
      // Tab切换时的逻辑处理
      console.log('切换到Tab:', tab.name)
    }
  }
}
</script>

<style scoped>
.user-info-card {
  border-radius: 8px;
}

.user-info-content {
  display: flex;
  align-items: center;
  gap: 30px;
}

.user-avatar {
  flex-shrink: 0;
}

.avatar-wrapper {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid #f0f0f0;
  position: relative;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  background: #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
  font-size: 24px;
}

.user-basic-info {
  flex: 1;
}

.user-basic-info h3 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 20px;
}

.user-basic-info p {
  margin: 5px 0;
  color: #606266;
  font-size: 14px;
}

.user-stats {
  display: flex;
  gap: 30px;
  align-items: center;
}

.stat-item {
  text-align: center;
  min-width: 80px;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 12px;
  color: #909399;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-info-content {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }
  
  .user-stats {
    justify-content: center;
    gap: 20px;
  }
}
</style> 