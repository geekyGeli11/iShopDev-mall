<template>
  <div class="app-container">
    <el-card shadow="never" v-loading="loading">
      <div slot="header">
        <span>报损详情</span>
        <el-button style="float: right" size="small" @click="$router.back()">返回</el-button>
      </div>
      
      <!-- 基本信息 -->
      <el-descriptions title="基本信息" :column="3" border>
        <el-descriptions-item label="报损单号">{{ detail.reportSn }}</el-descriptions-item>
        <el-descriptions-item label="报损门店">{{ detail.storeName }}</el-descriptions-item>
        <el-descriptions-item label="处理状态">
          <el-tag :type="getStatusTag(detail.status)" size="small">{{ formatStatus(detail.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="报损总数量">{{ detail.totalQuantity }} 件</el-descriptions-item>
        <el-descriptions-item label="报损总金额">￥{{ detail.totalDamageAmount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="销售总金额">￥{{ detail.totalSalesAmount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="报损类型">
          <el-tag :type="getDamageTypeTag(detail.damageType)" size="small">{{ formatDamageType(detail.damageType) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="报损原因">{{ detail.damageReason }}</el-descriptions-item>
        <el-descriptions-item label="报损时间">{{ detail.createTime | formatTime }}</el-descriptions-item>
      </el-descriptions>

      <!-- 报损商品明细 -->
      <div style="margin-top: 20px">
        <h4>报损商品明细</h4>
        <el-table :data="detail.items || []" border size="small">
          <el-table-column label="商品" min-width="250">
            <template slot-scope="scope">
              <div style="display: flex; align-items: center;">
                <img v-if="scope.row.productPic" :src="scope.row.productPic" style="width: 50px; height: 50px; margin-right: 10px; border-radius: 4px;" />
                <div>
                  <div>{{ scope.row.productName }}</div>
                  <div style="font-size: 12px; color: #999;">SKU: {{ scope.row.skuCode }}</div>
                  <div v-if="scope.row.skuSpec" style="font-size: 12px; color: #999;">规格: {{ formatSkuSpec(scope.row.skuSpec) }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="报损数量" width="100" align="center" prop="damageQuantity"></el-table-column>
          <el-table-column label="成本价" width="100" align="center">
            <template slot-scope="scope">￥{{ scope.row.costPrice || 0 }}</template>
          </el-table-column>
          <el-table-column label="销售价" width="100" align="center">
            <template slot-scope="scope">￥{{ scope.row.salePrice || 0 }}</template>
          </el-table-column>
          <el-table-column label="报损金额" width="100" align="center">
            <template slot-scope="scope">￥{{ scope.row.damageAmount || 0 }}</template>
          </el-table-column>
          <el-table-column label="销售金额" width="100" align="center">
            <template slot-scope="scope">￥{{ scope.row.salesAmount || 0 }}</template>
          </el-table-column>
          <el-table-column label="备注" width="150" align="center" prop="itemRemark" show-overflow-tooltip></el-table-column>
        </el-table>
      </div>

      <!-- 报损描述 -->
      <el-descriptions title="报损描述" :column="1" border style="margin-top: 20px">
        <el-descriptions-item label="详细描述">{{ detail.damageDescription || '无' }}</el-descriptions-item>
        <el-descriptions-item label="瑕疵图片">
          <div v-if="damagePicsList.length > 0" class="image-list">
            <el-image v-for="(pic, index) in damagePicsList" :key="index" :src="pic" :preview-src-list="damagePicsList"
              style="width: 100px; height: 100px; margin-right: 10px" fit="cover"></el-image>
          </div>
          <span v-else>无</span>
        </el-descriptions-item>
      </el-descriptions>

      <!-- 到货瑕疵特有信息 -->
      <el-descriptions v-if="detail.damageType === 1" title="厂家售后信息" :column="3" border style="margin-top: 20px">
        <el-descriptions-item label="供应商名称">{{ detail.supplierName || '无' }}</el-descriptions-item>
        <el-descriptions-item label="联系方式">{{ detail.supplierContact || '无' }}</el-descriptions-item>
        <el-descriptions-item label="厂家反馈">{{ detail.supplierFeedback || '无' }}</el-descriptions-item>
        <el-descriptions-item label="重发单号">{{ detail.reshipmentSn || '无' }}</el-descriptions-item>
        <el-descriptions-item label="重发时间">{{ detail.reshipmentTime | formatTime }}</el-descriptions-item>
        <el-descriptions-item label="验收状态">
          <el-tag v-if="detail.acceptanceStatus !== null" :type="detail.acceptanceStatus === 1 ? 'success' : 'danger'" size="small">
            {{ detail.acceptanceStatus === 1 ? '验收通过' : '验收不通过' }}
          </el-tag>
          <span v-else>待验收</span>
        </el-descriptions-item>
        <el-descriptions-item label="验收时间">{{ detail.acceptanceTime | formatTime }}</el-descriptions-item>
        <el-descriptions-item label="验收备注" :span="2">{{ detail.acceptanceRemark || '无' }}</el-descriptions-item>
      </el-descriptions>

      <!-- 外借损坏特有信息 -->
      <el-descriptions v-if="detail.damageType === 2" title="外借信息" :column="3" border style="margin-top: 20px">
        <el-descriptions-item label="外借用途">{{ detail.borrowPurpose || '无' }}</el-descriptions-item>
        <el-descriptions-item label="借用人">{{ detail.borrowPerson || '无' }}</el-descriptions-item>
        <el-descriptions-item label="借出时间">{{ detail.borrowTime | formatTime }}</el-descriptions-item>
        <el-descriptions-item label="归还时间">{{ detail.returnTime | formatTime }}</el-descriptions-item>
      </el-descriptions>

      <!-- 处理信息 -->
      <el-descriptions title="处理信息" :column="3" border style="margin-top: 20px">
        <el-descriptions-item label="提交人">{{ detail.submitAdminName }}</el-descriptions-item>
        <el-descriptions-item label="处理人">{{ detail.handleAdminName || '无' }}</el-descriptions-item>
        <el-descriptions-item label="处理时间">{{ detail.handleTime | formatTime }}</el-descriptions-item>
        <el-descriptions-item label="处理方式">{{ formatHandleMethod(detail.handleMethod) }}</el-descriptions-item>
        <el-descriptions-item label="完成时间">{{ detail.completeTime | formatTime }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ detail.remark || '无' }}</el-descriptions-item>
        <el-descriptions-item label="处理描述" :span="3">{{ detail.handleDescription || '无' }}</el-descriptions-item>
        <el-descriptions-item label="处理凭证" :span="3">
          <div v-if="handlePicsList.length > 0" class="image-list">
            <el-image v-for="(pic, index) in handlePicsList" :key="index" :src="pic" :preview-src-list="handlePicsList"
              style="width: 100px; height: 100px; margin-right: 10px" fit="cover"></el-image>
          </div>
          <span v-else>无</span>
        </el-descriptions-item>
      </el-descriptions>

      <!-- 处理日志 -->
      <div style="margin-top: 20px">
        <h4>处理日志</h4>
        <el-timeline>
          <el-timeline-item v-for="log in detail.logList" :key="log.id" :timestamp="log.createTime | formatTime" placement="top">
            <el-card shadow="never">
              <p><strong>{{ formatActionType(log.actionType) }}</strong> - {{ log.operatorName }}</p>
              <p>{{ log.actionContent }}</p>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>

      <!-- 操作按钮 -->
      <div style="margin-top: 20px; text-align: center" v-if="detail.status < 3">
        <el-button v-if="detail.status === 0" type="primary" @click="handleStartProcess">开始处理</el-button>
        <el-button v-if="detail.status === 1" type="primary" @click="showHandleDialog">更新处理信息</el-button>
        <el-button v-if="detail.status === 2" type="success" @click="showAcceptanceDialog">验收</el-button>
        <el-button v-if="detail.status === 1" type="success" @click="handleComplete">完成处理</el-button>
        <el-button v-if="detail.status < 3" type="warning" @click="handleClose">关闭</el-button>
      </div>
    </el-card>

    <!-- 更新处理信息对话框 -->
    <el-dialog title="更新处理信息" :visible.sync="handleDialogVisible" width="600px">
      <el-form :model="handleForm" label-width="100px">
        <el-form-item label="处理方式">
          <el-select v-model="handleForm.handleMethod" placeholder="请选择处理方式" style="width:100%">
            <el-option v-for="item in handleMethodOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="处理描述">
          <el-input type="textarea" v-model="handleForm.handleDescription" :rows="3"></el-input>
        </el-form-item>
        <el-form-item label="处理凭证">
          <multi-upload v-model="handleForm.handlePicsList" :maxCount="5"></multi-upload>
        </el-form-item>
        <template v-if="detail.damageType === 1">
          <el-form-item label="厂家反馈">
            <el-input type="textarea" v-model="handleForm.supplierFeedback" :rows="2"></el-input>
          </el-form-item>
          <el-form-item label="重发单号">
            <el-input v-model="handleForm.reshipmentSn" placeholder="厂家重新发货单号"></el-input>
          </el-form-item>
          <el-form-item label="重发时间">
            <el-date-picker v-model="handleForm.reshipmentTime" type="datetime" placeholder="选择重发时间" style="width:100%"></el-date-picker>
          </el-form-item>
        </template>
        <el-form-item label="备注">
          <el-input v-model="handleForm.remark"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="handleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitHandle">确定</el-button>
      </span>
    </el-dialog>

    <!-- 验收对话框 -->
    <el-dialog title="验收" :visible.sync="acceptanceDialogVisible" width="500px">
      <el-form :model="acceptanceForm" label-width="100px">
        <el-form-item label="验收结果">
          <el-radio-group v-model="acceptanceForm.acceptanceStatus">
            <el-radio :label="1">验收通过</el-radio>
            <el-radio :label="2">验收不通过</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="验收备注">
          <el-input type="textarea" v-model="acceptanceForm.acceptanceRemark" :rows="3"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="acceptanceDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAcceptance">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { formatDate } from '@/utils/date'
import { getDamageReportDetail, startHandleDamage, updateHandleDamage, acceptanceDamage, completeDamage, closeDamage } from '@/api/damage'
import MultiUpload from '@/components/Upload/multiUpload'

export default {
  name: 'DamageDetail',
  components: { MultiUpload },
  data() {
    return {
      loading: false,
      detail: {},
      handleDialogVisible: false,
      acceptanceDialogVisible: false,
      handleForm: {
        handleMethod: null,
        handleDescription: '',
        handlePicsList: [],
        supplierFeedback: '',
        reshipmentSn: '',
        reshipmentTime: null,
        remark: ''
      },
      acceptanceForm: {
        acceptanceStatus: 1,
        acceptanceRemark: ''
      },
      damageTypeOptions: [
        { value: 1, label: '到货瑕疵' },
        { value: 2, label: '外借损坏' },
        { value: 3, label: '保存不当' },
        { value: 4, label: '人为原因' },
        { value: 5, label: '其他' }
      ],
      statusOptions: [
        { value: 0, label: '待处理' },
        { value: 1, label: '处理中' },
        { value: 2, label: '待验收' },
        { value: 3, label: '已完成' },
        { value: 4, label: '已关闭' }
      ],
      handleMethodOptions: [
        { value: 1, label: '退回厂家' },
        { value: 2, label: '厂家换货' },
        { value: 3, label: '内部消化' },
        { value: 4, label: '报废处理' },
        { value: 5, label: '折价销售' },
        { value: 6, label: '其他' }
      ],
      actionTypeMap: {
        1: '提交报损',
        2: '开始处理',
        3: '联系厂家',
        4: '厂家发货',
        5: '验收',
        6: '完成处理',
        7: '关闭',
        8: '备注'
      }
    }
  },
  computed: {
    damagePicsList() {
      if (!this.detail.damagePics) return []
      return this.detail.damagePics.split(',').filter(p => p)
    },
    handlePicsList() {
      if (!this.detail.handlePics) return []
      return this.detail.handlePics.split(',').filter(p => p)
    }
  },
  created() {
    this.getDetail()
  },
  filters: {
    formatTime(time) {
      if (!time) return '-'
      return formatDate(new Date(time), 'yyyy-MM-dd hh:mm:ss')
    }
  },
  methods: {
    getDetail() {
      const id = this.$route.query.id
      if (!id) {
        this.$message.error('缺少报损ID')
        return
      }
      this.loading = true
      getDamageReportDetail(id).then(response => {
        this.loading = false
        this.detail = response.data
      })
    },
    formatDamageType(type) {
      const item = this.damageTypeOptions.find(i => i.value === type)
      return item ? item.label : '-'
    },
    formatStatus(status) {
      const item = this.statusOptions.find(i => i.value === status)
      return item ? item.label : '-'
    },
    formatHandleMethod(method) {
      const item = this.handleMethodOptions.find(i => i.value === method)
      return item ? item.label : '-'
    },
    formatActionType(type) {
      return this.actionTypeMap[type] || '操作'
    },
    formatSkuSpec(spec) {
      if (!spec) return ''
      try {
        const specs = JSON.parse(spec)
        if (Array.isArray(specs)) {
          return specs.map(s => s.value || `${s.key}:${s.value}`).filter(s => s).join(' / ')
        }
        return spec
      } catch (e) {
        return spec
      }
    },
    getDamageTypeTag(type) {
      const tags = { 1: 'danger', 2: 'warning', 3: 'info', 4: 'danger', 5: '' }
      return tags[type] || ''
    },
    getStatusTag(status) {
      const tags = { 0: 'warning', 1: 'primary', 2: 'info', 3: 'success', 4: 'info' }
      return tags[status] || ''
    },
    handleStartProcess() {
      this.$confirm('确定开始处理该报损记录?', '提示', { type: 'warning' }).then(() => {
        startHandleDamage(this.detail.id).then(() => {
          this.$message.success('操作成功')
          this.getDetail()
        })
      })
    },
    showHandleDialog() {
      this.handleForm = {
        handleMethod: this.detail.handleMethod,
        handleDescription: this.detail.handleDescription || '',
        handlePicsList: this.detail.handlePics ? this.detail.handlePics.split(',').filter(p => p) : [],
        supplierFeedback: this.detail.supplierFeedback || '',
        reshipmentSn: this.detail.reshipmentSn || '',
        reshipmentTime: this.detail.reshipmentTime,
        remark: ''
      }
      this.handleDialogVisible = true
    },
    submitHandle() {
      const data = Object.assign({}, this.handleForm)
      data.handlePics = data.handlePicsList.join(',')
      delete data.handlePicsList
      updateHandleDamage(this.detail.id, data).then(() => {
        this.$message.success('更新成功')
        this.handleDialogVisible = false
        this.getDetail()
      })
    },
    showAcceptanceDialog() {
      this.acceptanceForm = { acceptanceStatus: 1, acceptanceRemark: '' }
      this.acceptanceDialogVisible = true
    },
    submitAcceptance() {
      acceptanceDamage(this.detail.id, this.acceptanceForm).then(() => {
        this.$message.success('验收完成')
        this.acceptanceDialogVisible = false
        this.getDetail()
      })
    },
    handleComplete() {
      this.$confirm('确定完成该报损处理?', '提示', { type: 'warning' }).then(() => {
        completeDamage(this.detail.id).then(() => {
          this.$message.success('操作成功')
          this.getDetail()
        })
      })
    },
    handleClose() {
      this.$prompt('请输入关闭原因', '关闭报损', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(({ value }) => {
        closeDamage(this.detail.id, value).then(() => {
          this.$message.success('操作成功')
          this.getDetail()
        })
      })
    }
  }
}
</script>
<style scoped>
.image-list {
  display: flex;
  flex-wrap: wrap;
}
</style>
