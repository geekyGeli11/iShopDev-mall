<template>
  <div class="user-profile-tab">
    <!-- 用户画像概览 -->
    <el-card header="用户画像概览" style="margin-bottom: 20px;">
      <div class="profile-overview" v-if="profileData">
        <div class="profile-item">
          <div class="profile-label">用户价值</div>
          <div class="profile-value" :class="getValueClass(profileData.userValue)">
            {{ getUserValueText(profileData.userValue) }}
          </div>
        </div>
        <div class="profile-item">
          <div class="profile-label">消费能力</div>
          <div class="profile-value">{{ getConsumptionLevelText(profileData.consumptionLevel) }}</div>
        </div>
        <div class="profile-item">
          <div class="profile-label">活跃度</div>
          <div class="profile-value">{{ getActivityLevelText(profileData.activityLevel) }}</div>
        </div>
        <div class="profile-item">
          <div class="profile-label">忠诚度</div>
          <div class="profile-value">{{ getLoyaltyLevelText(profileData.loyaltyLevel) }}</div>
        </div>
      </div>
      
      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="8">
          <div class="refresh-info">
            <p><strong>最近更新：</strong>{{ profileData ? formatDate(profileData.updateTime) : '-' }}</p>
            <el-button type="primary" size="small" @click="generateProfile" :loading="generating">
              重新生成画像
            </el-button>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- RFM分析 -->
    <el-card header="RFM分析" style="margin-bottom: 20px;" v-if="profileData && profileData.rfmData">
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="rfm-item">
            <div class="rfm-label">最近购买时间 (Recency)</div>
            <div class="rfm-value">{{ profileData.rfmData.recencyDays }}天前</div>
            <div class="rfm-score">评分：{{ profileData.rfmData.recencyScore }}/5</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="rfm-item">
            <div class="rfm-label">购买频次 (Frequency)</div>
            <div class="rfm-value">{{ profileData.rfmData.frequency }}次</div>
            <div class="rfm-score">评分：{{ profileData.rfmData.frequencyScore }}/5</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="rfm-item">
            <div class="rfm-label">消费金额 (Monetary)</div>
            <div class="rfm-value">{{ formatAmount(profileData.rfmData.monetary) }}</div>
            <div class="rfm-score">评分：{{ profileData.rfmData.monetaryScore }}/5</div>
          </div>
        </el-col>
      </el-row>
      
      <div style="margin-top: 15px; text-align: center;">
        <span class="rfm-segment">用户分群：</span>
        <el-tag size="large" :type="getSegmentType(profileData.rfmData.segment)">
          {{ getSegmentText(profileData.rfmData.segment) }}
        </el-tag>
      </div>
    </el-card>

    <!-- 购买偏好分析 -->
    <el-card header="购买偏好分析" style="margin-bottom: 20px;" v-if="profileData && profileData.preferences">
      <el-row :gutter="20">
        <el-col :span="12">
          <h4>偏好商品分类</h4>
          <div class="preference-categories">
            <el-tag 
              v-for="category in profileData.preferences.categories" 
              :key="category.name"
              style="margin: 5px;"
              size="medium">
              {{ category.name }} ({{ category.percentage }}%)
            </el-tag>
          </div>
        </el-col>
        <el-col :span="12">
          <h4>购买习惯</h4>
          <div class="buying-habits">
            <p><strong>常用支付方式：</strong>{{ getPaymentMethodText(profileData.preferences.paymentMethod) }}</p>
            <p><strong>偏好购买时段：</strong>{{ profileData.preferences.buyingTimeSlot }}</p>
            <p><strong>平均订单金额：</strong>{{ formatAmount(profileData.preferences.avgOrderAmount) }}</p>
            <p><strong>优惠券使用率：</strong>{{ profileData.preferences.couponUsageRate }}%</p>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 用户标签 -->
    <el-card header="用户标签">
      <div class="tags-section">
        <div class="system-tags">
          <h4>系统标签</h4>
          <div class="tag-list">
            <el-tag 
              v-for="tag in systemTags" 
              :key="tag.id"
              :type="tag.type || 'primary'"
              style="margin: 5px;">
              {{ tag.name }}
            </el-tag>
            <span v-if="systemTags.length === 0" class="no-tags">暂无系统标签</span>
          </div>
        </div>
        
        <div class="custom-tags" style="margin-top: 20px;">
          <h4>自定义标签</h4>
          <div class="tag-list">
            <el-tag 
              v-for="tag in customTags" 
              :key="tag.id"
              :type="tag.status ? 'success' : 'info'"
              :color="tag.color"
              closable
              @close="removeCustomTag(tag.id)"
              style="margin: 5px;">
              {{ tag.name }}
              <span v-if="tag.description" style="margin-left: 5px; font-size: 12px; opacity: 0.8;">
                ({{ tag.description }})
              </span>
            </el-tag>
            <el-button size="small" @click="showSelectTagDialog">+ 选择标签</el-button>
            <el-button size="small" @click="showAddTagDialog">+ 新建标签</el-button>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 添加标签弹窗 -->
    <el-dialog title="添加自定义标签" :visible.sync="addTagDialogVisible" width="500px">
      <el-form ref="tagForm" :model="tagForm" :rules="tagRules" label-width="80px">
        <el-form-item label="标签名称" prop="name">
          <el-input v-model="tagForm.name" placeholder="请输入标签名称"></el-input>
        </el-form-item>
        <el-form-item label="标签描述" prop="description">
          <el-input 
            v-model="tagForm.description" 
            type="textarea" 
            :rows="3"
            placeholder="请输入标签描述（可选）">
          </el-input>
        </el-form-item>
        <el-form-item label="标签颜色" prop="color">
          <el-color-picker v-model="tagForm.color" show-alpha></el-color-picker>
          <span style="margin-left: 10px; color: #999; font-size: 12px;">选择标签颜色（可选）</span>
        </el-form-item>
        <el-form-item label="标签类型" prop="tagType">
          <el-radio-group v-model="tagForm.tagType">
            <el-radio :label="false">自定义标签</el-radio>
            <el-radio :label="true">系统标签</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="addTagDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAddTag" :loading="addingTag">确定</el-button>
      </div>
    </el-dialog>

    <!-- 选择标签弹窗 -->
    <el-dialog title="选择标签" :visible.sync="selectTagDialogVisible" width="600px">
      <div class="select-tag-content">
        <h4>系统标签</h4>
        <div class="tag-selection">
          <el-tag 
            v-for="tag in availableSystemTags" 
            :key="tag.id"
            :type="getTagSelectType(tag)"
            :color="tag.color"
            @click.native="toggleTagSelection(tag)"
            style="margin: 5px; cursor: pointer;">
            {{ tag.name }}
            <span v-if="tag.description" style="margin-left: 5px; font-size: 12px; opacity: 0.8;">
              ({{ tag.description }})
            </span>
            <i v-if="isTagSelected(tag)" class="el-icon-check" style="margin-left: 5px;"></i>
          </el-tag>
        </div>
        
        <h4 style="margin-top: 20px;">自定义标签</h4>
        <div class="tag-selection">
          <el-tag 
            v-for="tag in availableCustomTags" 
            :key="tag.id"
            :type="getTagSelectType(tag)"
            :color="tag.color"
            @click.native="toggleTagSelection(tag)"
            style="margin: 5px; cursor: pointer;">
            {{ tag.name }}
            <span v-if="tag.description" style="margin-left: 5px; font-size: 12px; opacity: 0.8;">
              ({{ tag.description }})
            </span>
            <i v-if="isTagSelected(tag)" class="el-icon-check" style="margin-left: 5px;"></i>
          </el-tag>
          <span v-if="availableCustomTags.length === 0" class="no-tags">暂无自定义标签</span>
        </div>
        
        <!-- 已选择的标签 -->
        <div v-if="selectedTags.length > 0" style="margin-top: 20px;">
          <h4>已选择的标签 ({{ selectedTags.length }})</h4>
          <div class="selected-tags">
            <el-tag 
              v-for="tag in selectedTags" 
              :key="tag.id"
              :type="tag.tagType ? 'primary' : 'success'"
              :color="tag.color"
              closable
              @close="removeSelectedTag(tag)"
              style="margin: 5px;">
              {{ tag.name }}
            </el-tag>
          </div>
        </div>
      </div>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancelTagSelection">取消</el-button>
        <el-button 
          type="primary" 
          @click="confirmTagSelection" 
          :loading="bindingTags"
          :disabled="selectedTags.length === 0">
          确定 ({{ selectedTags.length }})
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { 
  getMemberProfile, 
  generateMemberProfile,
  createTag,
  bindMemberTag,
  unbindMemberTag,
  getAllTags,
  updateTagStatus
} from '@/api/member'
import { formatDate, formatAmount } from '@/utils/index'

export default {
  name: 'UserProfileTab',
  props: {
    memberId: {
      type: [String, Number],
      required: true
    }
  },
  data() {
    return {
      profileData: null,
      systemTags: [],
      customTags: [],
      generating: false,
      addTagDialogVisible: false,
      addingTag: false,
      selectTagDialogVisible: false,
      allTags: [],
      availableSystemTags: [],
      availableCustomTags: [],
      selectedTags: [],
      bindingTags: false,
      tagForm: {
        name: '',
        description: '',
        color: '',
        tagType: false // false表示自定义标签，true表示系统标签
      },
      tagRules: {
        name: [
          { required: true, message: '请输入标签名称', trigger: 'blur' },
          { max: 20, message: '标签名称不能超过20个字符', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.loadProfileData()
  },
  methods: {
    formatDate,
    formatAmount,
    
    async loadProfileData() {
      try {
        const response = await getMemberProfile(this.memberId)
        if (response.code === 200) {
          const data = response.data
          
          // 转换数据结构以匹配前端期望的格式
          this.profileData = {
            userValue: this.calculateUserValue(data.rfmScore),
            consumptionLevel: this.calculateConsumptionLevel(data.spendingAnalysis),
            activityLevel: this.calculateActivityLevel(data.activityAnalysis),
            loyaltyLevel: this.calculateLoyaltyLevel(data.rfmScore),
            updateTime: data.lastUpdated,
            rfmData: {
              // 直接使用后端返回的真实数据
              recencyDays: (data.rfmScore && data.rfmScore.recencyDays) || 999,
              recencyScore: (data.rfmScore && data.rfmScore.recencyScore) || 0,
              frequency: (data.rfmScore && data.rfmScore.frequency) || 0,
              frequencyScore: (data.rfmScore && data.rfmScore.frequencyScore) || 0,
              monetary: (data.rfmScore && data.rfmScore.monetary) || 0,
              monetaryScore: (data.rfmScore && data.rfmScore.monetaryScore) || 0,
              segment: this.calculateSegment(data.rfmScore)
            },
            preferences: {
              categories: (data.preferences && data.preferences.categories) || [],
              paymentMethod: (data.preferences && data.preferences.paymentMethod) || 1,
              buyingTimeSlot: (data.preferences && data.preferences.buyingTimeSlot) || '全天',
              avgOrderAmount: (data.preferences && data.preferences.avgOrderAmount) || 0,
              couponUsageRate: (data.preferences && data.preferences.couponUsageRate) || 0
            }
          }
          
          // 处理标签数据
          // 系统自动生成的标签（基于用户行为分析）
          this.systemTags = (data.userTags || []).map((tag, index) => ({
            id: index + 1,
            name: tag,
            type: 'primary'
          }))
          
          // 用户手动绑定的标签
          if (data.memberTags && Array.isArray(data.memberTags)) {
            // 按标签类型分离
            this.systemTags = [...this.systemTags, ...data.memberTags.filter(tag => tag.tagType === true)]
            this.customTags = data.memberTags.filter(tag => tag.tagType === false)
          } else {
            this.customTags = []
          }
        } else {
          this.$message.error(response.message || '获取用户画像失败')
        }
      } catch (error) {
        console.error('获取用户画像失败:', error)
        this.$message.error('获取用户画像失败')
      }
    },
    
    async generateProfile() {
      try {
        this.generating = true
        const response = await generateMemberProfile(this.memberId)
        if (response.code === 200) {
          this.$message.success('用户画像生成成功')
          // 重新加载数据
          await this.loadProfileData()
        } else {
          this.$message.error(response.message || '生成用户画像失败')
        }
      } catch (error) {
        console.error('生成用户画像失败:', error)
        this.$message.error('生成用户画像失败')
      } finally {
        this.generating = false
      }
    },
    
    async showSelectTagDialog() {
      try {
        // 重置选择状态
        this.selectedTags = []
        
        // 获取所有标签
        const response = await getAllTags()
        if (response.code === 200) {
          this.allTags = response.data
          
          // 获取用户已绑定的标签名称（包括系统生成的和手动绑定的）
          const currentTagNames = [
            // 系统自动生成的标签名称
            ...this.systemTags.map(tag => tag.name),
            // 手动绑定的标签名称
            ...this.customTags.map(tag => tag.name)
          ]
          
          // 过滤出未绑定的标签（使用名称比较）
          const availableTags = this.allTags.filter(tag => !currentTagNames.includes(tag.name))
          
          // 按类型分组
          this.availableSystemTags = availableTags.filter(tag => tag.tagType === true)
          this.availableCustomTags = availableTags.filter(tag => tag.tagType === false)
          
          this.selectTagDialogVisible = true
        } else {
          this.$message.error('获取标签列表失败：' + response.message)
        }
      } catch (error) {
        console.error('获取标签列表失败:', error)
        this.$message.error('获取标签列表失败')
      }
    },
    
    // 切换标签选择状态
    toggleTagSelection(tag) {
      console.log('点击标签:', tag.name, 'ID:', tag.id)
      console.log('当前选择的标签:', this.selectedTags)
      
      const index = this.selectedTags.findIndex(t => t.id === tag.id)
      if (index > -1) {
        // 如果已选择，则移除
        this.selectedTags.splice(index, 1)
        console.log('移除标签:', tag.name)
      } else {
        // 如果未选择，则添加
        this.selectedTags.push(tag)
        console.log('添加标签:', tag.name)
      }
      
      console.log('更新后选择的标签:', this.selectedTags)
    },
    
    // 检查标签是否已选择
    isTagSelected(tag) {
      return this.selectedTags.some(t => t.id === tag.id)
    },
    
    // 获取标签的显示类型（选中时高亮）
    getTagSelectType(tag) {
      if (this.isTagSelected(tag)) {
        return 'danger' // 选中时用红色高亮
      }
      
      if (tag.tagType === true) {
        return tag.status ? 'primary' : 'info'
      } else {
        return tag.status ? 'success' : 'info'
      }
    },
    
    // 移除已选择的标签
    removeSelectedTag(tag) {
      const index = this.selectedTags.findIndex(t => t.id === tag.id)
      if (index > -1) {
        this.selectedTags.splice(index, 1)
      }
    },
    
    // 确认选择并绑定标签
    async confirmTagSelection() {
      if (this.selectedTags.length === 0) {
        this.$message.warning('请先选择要绑定的标签')
        return
      }
      
      try {
        this.bindingTags = true
        let successCount = 0
        
        // 逐个绑定标签
        for (const tag of this.selectedTags) {
          try {
            const response = await bindMemberTag(this.memberId, tag.id)
            if (response.code === 200) {
              successCount++
            }
          } catch (error) {
            console.error('绑定标签失败:', tag.name, error)
          }
        }
        
        if (successCount > 0) {
          this.$message.success(`成功绑定 ${successCount} 个标签`)
          this.selectTagDialogVisible = false
          this.selectedTags = []
          // 重新加载用户画像数据以显示新标签
          await this.loadProfileData()
        } else {
          this.$message.error('标签绑定失败')
        }
      } catch (error) {
        console.error('批量绑定标签失败:', error)
        this.$message.error('批量绑定标签失败')
      } finally {
        this.bindingTags = false
      }
    },
    
    // 取消选择
    cancelTagSelection() {
      this.selectedTags = []
      this.selectTagDialogVisible = false
    },

    showAddTagDialog() {
      this.tagForm = {
        name: '',
        description: '',
        color: '',
        tagType: false // 默认为自定义标签
      }
      this.addTagDialogVisible = true
    },
    
    async confirmAddTag() {
      try {
        await this.$refs.tagForm.validate()
        this.addingTag = true
        
        // 创建标签（支持完整字段）
        const createResponse = await createTag(
          this.tagForm.name, 
          this.tagForm.description, 
          this.tagForm.tagType, 
          this.tagForm.color
        )
        
        if (createResponse.code === 200) {
          // 获取所有标签以获取新创建的标签ID
          const allTagsResponse = await getAllTags()
          if (allTagsResponse.code === 200) {
            const newTag = allTagsResponse.data.find(tag => tag.name === this.tagForm.name)
            if (newTag) {
              // 给当前用户绑定这个标签
              const bindResponse = await bindMemberTag(this.memberId, newTag.id)
              
              if (bindResponse.code === 200) {
                this.$message.success('标签添加成功')
                // 重新加载用户画像数据以显示新标签
                await this.loadProfileData()
              } else {
                this.$message.error('标签绑定失败：' + bindResponse.message)
              }
            }
          }
        } else {
          this.$message.error('标签创建失败：' + createResponse.message)
        }
        
        this.addTagDialogVisible = false
      } catch (error) {
        console.error('添加标签失败:', error)
        if (error !== false) {
          this.$message.error('添加标签失败')
        }
      } finally {
        this.addingTag = false
      }
    },
    
    async removeCustomTag(tagId) {
      try {
        // 调用解绑标签的API
        const response = await unbindMemberTag(this.memberId, tagId)
        
        if (response.code === 200) {
          this.$message.success('标签解绑成功')
          // 重新加载用户画像数据以更新标签显示
          await this.loadProfileData()
        } else {
          this.$message.error('标签解绑失败：' + response.message)
        }
      } catch (error) {
        console.error('删除标签失败:', error)
        this.$message.error('删除标签失败')
      }
    },
    
    getUserValueText(value) {
      const valueMap = {
        1: '低价值用户',
        2: '一般价值用户',
        3: '重要价值用户',
        4: '高价值用户',
        5: '超高价值用户'
      }
      return valueMap[value] || '未知'
    },
    
    getValueClass(value) {
      const classMap = {
        1: 'value-low',
        2: 'value-normal',
        3: 'value-important',
        4: 'value-high',
        5: 'value-super'
      }
      return classMap[value] || ''
    },
    
    getConsumptionLevelText(level) {
      const levelMap = {
        1: '低消费',
        2: '中消费',
        3: '高消费'
      }
      return levelMap[level] || '未知'
    },
    
    getActivityLevelText(level) {
      const levelMap = {
        1: '不活跃',
        2: '一般活跃',
        3: '高活跃'
      }
      return levelMap[level] || '未知'
    },
    
    getLoyaltyLevelText(level) {
      const levelMap = {
        1: '低忠诚',
        2: '中忠诚',
        3: '高忠诚'
      }
      return levelMap[level] || '未知'
    },
    
    getSegmentText(segment) {
      const segmentMap = {
        'champion': '忠实用户',
        'loyal': '忠诚用户', 
        'potential': '潜力用户',
        'promising': '新用户',
        'need_attention': '需关注用户',
        'about_to_sleep': '即将流失',
        'at_risk': '有流失风险',
        'cannot_lose': '重要挽回用户',
        'hibernating': '休眠用户',
        'lost': '已流失用户'
      }
      return segmentMap[segment] || '未分类'
    },
    
    getSegmentType(segment) {
      const typeMap = {
        'champion': 'success',
        'loyal': 'success',
        'potential': 'primary',
        'promising': 'primary',
        'need_attention': 'warning',
        'about_to_sleep': 'warning',
        'at_risk': 'danger',
        'cannot_lose': 'danger',
        'hibernating': 'info',
        'lost': 'info'
      }
      return typeMap[segment] || 'info'
    },
    
    getPaymentMethodText(method) {
      const methodMap = {
        1: '支付宝',
        2: '微信支付',
        3: '银行卡'
      }
      return methodMap[method] || '未知'
    },
    
    // 数据转换方法
    calculateUserValue(rfmScore) {
      if (!rfmScore || !rfmScore.totalScore) return 1
      const score = rfmScore.totalScore
      if (score >= 13) return 5      // 超高价值用户 
      if (score >= 10) return 4      // 高价值用户
      if (score >= 7) return 3       // 重要价值用户
      if (score >= 5) return 2       // 一般价值用户
      return 1                       // 低价值用户
    },
    
    calculateConsumptionLevel(spendingAnalysis) {
      // 根据消费分析计算消费等级
      if (!spendingAnalysis || Object.keys(spendingAnalysis).length === 0) return 1
      
      const totalAmount = parseFloat(spendingAnalysis.totalAmount || 0)
      
      if (totalAmount >= 5000) return 3      // 高消费
      if (totalAmount >= 1000) return 2      // 中消费  
      return 1                               // 低消费
    },
    
    calculateActivityLevel(activityAnalysis) {
      if (!activityAnalysis) return 1
      
      const loginCount = activityAnalysis.recent30DaysLoginCount || 0
      const registerDays = activityAnalysis.registerDays || 1
      
      // 计算活跃度：基于最近30天登录次数和注册天数
      const activityRate = loginCount / Math.min(registerDays, 30)
      
      if (activityRate >= 0.8) return 3      // 高活跃
      if (activityRate >= 0.3) return 2      // 一般活跃  
      return 1                               // 不活跃
    },
    
    calculateLoyaltyLevel(rfmScore) {
      if (!rfmScore) return 1
      
      const frequency = rfmScore.frequencyScore || 0
      const recency = rfmScore.recencyScore || 0
      
      // 忠诚度基于购买频次和最近购买时间
      const loyaltyScore = (frequency + recency) / 2
      
      if (loyaltyScore >= 4) return 3        // 高忠诚
      if (loyaltyScore >= 2.5) return 2      // 中忠诚
      return 1                               // 低忠诚
    },
    
    calculateSegment(rfmScore) {
      if (!rfmScore) return 'lost'
      
      const r = rfmScore.recencyScore || 0
      const f = rfmScore.frequencyScore || 0 
      const m = rfmScore.monetaryScore || 0
      
      // 基于 RFM 评分的用户分群逻辑
      if (r >= 4 && f >= 4 && m >= 4) return 'champion'
      if (r >= 3 && f >= 4 && m >= 3) return 'loyal'
      if (r >= 4 && f <= 2 && m >= 3) return 'promising'
      if (r >= 3 && f <= 3 && m >= 3) return 'potential'
      if (r <= 3 && f >= 3 && m >= 3) return 'need_attention'
      if (r <= 2 && f >= 2 && m >= 2) return 'about_to_sleep'
      if (r <= 2 && f >= 3 && m <= 2) return 'at_risk'
      if (r <= 1 && f >= 4 && m >= 4) return 'cannot_lose'
      if (r <= 2 && f <= 2 && m <= 2) return 'hibernating'
      return 'lost'
    }
  }
}
</script>

<style scoped>
.user-profile-tab {
  padding: 20px 0;
}

.profile-overview {
  display: flex;
  gap: 40px;
  justify-content: space-around;
}

.profile-item {
  text-align: center;
  flex: 1;
}

.profile-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.profile-value {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.value-low { color: #F56C6C; }
.value-normal { color: #E6A23C; }
.value-important { color: #409EFF; }
.value-high { color: #67C23A; }
.value-super { color: #722ED1; }

.refresh-info {
  text-align: center;
}

.rfm-item {
  text-align: center;
  padding: 15px;
  border: 1px solid #EBEEF5;
  border-radius: 4px;
}

.rfm-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.rfm-value {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.rfm-score {
  font-size: 12px;
  color: #909399;
}

.rfm-segment {
  font-size: 16px;
  font-weight: bold;
  margin-right: 10px;
}

.preference-categories {
  min-height: 60px;
}

.buying-habits p {
  margin: 8px 0;
  color: #606266;
}

.tags-section h4 {
  margin-bottom: 10px;
  color: #303133;
}

.tag-list {
  min-height: 40px;
  line-height: 40px;
}

.no-tags {
  color: #C0C4CC;
  font-style: italic;
}

.select-tag-content h4 {
  margin-bottom: 10px;
  color: #303133;
  font-size: 14px;
}

.tag-selection {
  min-height: 40px;
  padding: 10px;
  border: 1px dashed #DCDFE6;
  border-radius: 4px;
  background-color: #FAFAFA;
}

.tag-selection .el-tag {
  transition: all 0.3s ease;
}

.tag-selection .el-tag:hover {
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.selected-tags {
  padding: 10px;
  border: 1px solid #DCDFE6;
  border-radius: 4px;
  background-color: #F5F7FA;
  min-height: 40px;
}

.selected-tags .el-tag {
  margin: 3px;
}
</style> 