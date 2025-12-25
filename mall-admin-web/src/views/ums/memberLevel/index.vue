<template>
  <div class="app-container">
    <div class="filter-container">
      <el-button
        class="filter-item"
        style="margin-left: 10px;"
        type="primary"
        icon="el-icon-edit"
        @click="handleCreate"
      >
        添加会员等级
      </el-button>
    </div>

    <el-table
      :key="tableKey"
      v-loading="listLoading"
      :data="list"
      border
      fit
      highlight-current-row
      style="width: 100%;"
    >
      <el-table-column label="ID" prop="id" align="center" width="80">
        <template slot-scope="{row}">
          <span>{{ row.id }}</span>
        </template>
      </el-table-column>
      <el-table-column label="等级名称" width="150px" align="center">
        <template slot-scope="{row}">
          <span class="link-type" @click="handleUpdate(row)">{{ row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column label="成长值" width="120px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.growthPoint }}</span>
        </template>
      </el-table-column>
      <el-table-column label="折扣" width="100px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.memberDiscount }}%</span>
        </template>
      </el-table-column>
      <el-table-column label="免邮标准" width="120px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.freeFreightPoint }}元</span>
        </template>
      </el-table-column>
      <el-table-column label="赠送优惠券" width="150px" align="center">
        <template slot-scope="{row}">
          <span v-if="row.giftCouponName">{{ row.giftCouponName }}</span>
          <span v-else class="text-muted">未设置</span>
        </template>
      </el-table-column>
      <el-table-column label="默认等级" width="100px" align="center">
        <template slot-scope="{row}">
          <el-tag :type="row.defaultStatus === 1 ? 'success' : 'info'">
            {{ row.defaultStatus === 1 ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="特权" width="200px" align="center">
        <template slot-scope="{row}">
          <div class="privilege-tags">
            <el-tag v-if="row.commentGrowthPoint > 0" size="mini" type="primary">
              评论奖励
            </el-tag>
            <el-tag v-if="row.priviledgeFreeFreight === 1" size="mini" type="success">
              免邮特权
            </el-tag>
            <el-tag v-if="row.priviledgeSignIn === 1" size="mini" type="warning">
              签到特权
            </el-tag>
            <el-tag v-if="row.priviledgeComment === 1" size="mini" type="info">
              评论特权
            </el-tag>
            <el-tag v-if="row.priviledgePromotion === 1" size="mini" type="danger">
              促销特权
            </el-tag>
            <el-tag v-if="row.priviledgeMemberPrice === 1" size="mini" type="primary">
              会员价格
            </el-tag>
            <el-tag v-if="row.priviledgeBirthday === 1" size="mini" type="success">
              生日特权
            </el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="230" class-name="small-padding fixed-width">
        <template slot-scope="{row,$index}">
          <el-button type="primary" size="mini" @click="handleUpdate(row)">
            编辑
          </el-button>
          <el-button 
            v-if="row.defaultStatus !== 1" 
            size="mini" 
            type="danger" 
            @click="handleDelete(row,$index)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加/编辑对话框 -->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" width="800px">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="temp"
        label-position="left"
        label-width="120px"
        style="width: 100%; padding: 0 20px;"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="等级名称" prop="name">
              <el-input v-model="temp.name" placeholder="请输入等级名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="成长值" prop="growthPoint">
              <el-input-number 
                v-model="temp.growthPoint" 
                :min="0" 
                :max="999999"
                placeholder="达到该等级所需成长值"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="会员折扣" prop="memberDiscount">
              <el-input-number 
                v-model="temp.memberDiscount" 
                :min="1" 
                :max="100"
                :precision="2"
                placeholder="会员享受的折扣百分比"
                style="width: 100%"
              />
              <span class="form-tip">%（如：98表示98%折扣）</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="免邮标准" prop="freeFreightPoint">
              <el-input-number 
                v-model="temp.freeFreightPoint" 
                :min="0" 
                :max="99999"
                :precision="2"
                placeholder="免邮门槛金额"
                style="width: 100%"
              />
              <span class="form-tip">元</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="评论奖励" prop="commentGrowthPoint">
              <el-input-number 
                v-model="temp.commentGrowthPoint" 
                :min="0" 
                :max="999"
                placeholder="评论获得的成长值"
                style="width: 100%"
              />
              <span class="form-tip">成长值</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="默认等级">
              <el-switch
                v-model="temp.defaultStatus"
                :active-value="1"
                :inactive-value="0"
                active-text="是"
                inactive-text="否"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="升级赠送优惠券">
              <el-select
                v-model="temp.giftCouponId"
                placeholder="请选择优惠券"
                filterable
                remote
                :remote-method="searchCoupons"
                :loading="couponLoading"
                clearable
                style="width: 100%"
              >
                <el-option
                  v-for="coupon in couponOptions"
                  :key="coupon.id"
                  :label="coupon.name"
                  :value="coupon.id"
                >
                  <span style="float: left">{{ coupon.name }}</span>
                  <span style="float: right; color: #8492a6; font-size: 13px">
                    {{ coupon.type === 0 ? '全场通用' : coupon.type === 1 ? '指定分类' : '指定商品' }}
                  </span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="会员特权">
          <el-row :gutter="10">
            <el-col :span="8">
              <el-checkbox v-model="temp.priviledgeFreeFreight" :true-label="1" :false-label="0">
                免邮特权
              </el-checkbox>
            </el-col>
            <el-col :span="8">
              <el-checkbox v-model="temp.priviledgeSignIn" :true-label="1" :false-label="0">
                签到特权
              </el-checkbox>
            </el-col>
            <el-col :span="8">
              <el-checkbox v-model="temp.priviledgeComment" :true-label="1" :false-label="0">
                评论特权
              </el-checkbox>
            </el-col>
          </el-row>
          <el-row :gutter="10" style="margin-top: 10px;">
            <el-col :span="8">
              <el-checkbox v-model="temp.priviledgePromotion" :true-label="1" :false-label="0">
                促销特权
              </el-checkbox>
            </el-col>
            <el-col :span="8">
              <el-checkbox v-model="temp.priviledgeMemberPrice" :true-label="1" :false-label="0">
                会员价格
              </el-checkbox>
            </el-col>
            <el-col :span="8">
              <el-checkbox v-model="temp.priviledgeBirthday" :true-label="1" :false-label="0">
                生日特权
              </el-checkbox>
            </el-col>
          </el-row>
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="temp.note"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">
          取消
        </el-button>
        <el-button type="primary" @click="dialogStatus==='create'?createData():updateData()">
          确定
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { fetchList, createMemberLevel, updateMemberLevel, deleteMemberLevel, getMemberLevel, getCouponOptions } from '@/api/memberLevel'

export default {
  name: 'MemberLevelList',
  data() {
    return {
      tableKey: 0,
      list: [],
      listLoading: true,
      dialogFormVisible: false,
      dialogStatus: '',
      couponLoading: false,
      couponOptions: [],
      textMap: {
        update: '编辑会员等级',
        create: '添加会员等级'
      },
      temp: {
        id: undefined,
        name: '',
        growthPoint: 0,
        memberDiscount: 100,
        freeFreightPoint: 0,
        commentGrowthPoint: 0,
        defaultStatus: 0,
        giftCouponId: null,
        priviledgeFreeFreight: 0,
        priviledgeSignIn: 0,
        priviledgeComment: 0,
        priviledgePromotion: 0,
        priviledgeMemberPrice: 0,
        priviledgeBirthday: 0,
        note: ''
      },
      rules: {
        name: [{ required: true, message: '等级名称不能为空', trigger: 'blur' }],
        growthPoint: [{ required: true, message: '成长值不能为空', trigger: 'blur' }],
        memberDiscount: [{ required: true, message: '会员折扣不能为空', trigger: 'blur' }],
        freeFreightPoint: [{ required: true, message: '免邮标准不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
    this.loadCouponOptions()
  },
  methods: {
    getList() {
      this.listLoading = true
      fetchList().then(response => {
        this.list = response.data
        this.listLoading = false
      }).catch(() => {
        this.listLoading = false
      })
    },
    loadCouponOptions() {
      getCouponOptions().then(response => {
        this.couponOptions = response.data || []
      })
    },
    searchCoupons(keyword) {
      if (keyword !== '') {
        this.couponLoading = true
        getCouponOptions(keyword).then(response => {
          this.couponOptions = response.data || []
          this.couponLoading = false
        }).catch(() => {
          this.couponLoading = false
        })
      } else {
        this.loadCouponOptions()
      }
    },
    resetTemp() {
      this.temp = {
        id: undefined,
        name: '',
        growthPoint: 0,
        memberDiscount: 100,
        freeFreightPoint: 0,
        commentGrowthPoint: 0,
        defaultStatus: 0,
        giftCouponId: null,
        priviledgeFreeFreight: 0,
        priviledgeSignIn: 0,
        priviledgeComment: 0,
        priviledgePromotion: 0,
        priviledgeMemberPrice: 0,
        priviledgeBirthday: 0,
        note: ''
      }
    },
    handleCreate() {
      this.resetTemp()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    handleUpdate(row) {
      this.temp = Object.assign({}, row)
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          createMemberLevel(this.temp).then(() => {
            this.dialogFormVisible = false
            this.$notify({
              title: '成功',
              message: '创建成功',
              type: 'success',
              duration: 2000
            })
            this.getList()
          })
        }
      })
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const tempData = Object.assign({}, this.temp)
          updateMemberLevel(tempData.id, tempData).then(() => {
            this.dialogFormVisible = false
            this.$notify({
              title: '成功',
              message: '更新成功',
              type: 'success',
              duration: 2000
            })
            this.getList()
          })
        }
      })
    },
    handleDelete(row, index) {
      this.$confirm('此操作将永久删除该会员等级, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteMemberLevel(row.id).then(() => {
          this.$notify({
            title: '成功',
            message: '删除成功',
            type: 'success',
            duration: 2000
          })
          this.list.splice(index, 1)
        })
      })
    }
  }
}
</script>

<style scoped>
.link-type {
  color: #409EFF;
  cursor: pointer;
}

.link-type:hover {
  color: #66b1ff;
}

.privilege-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.privilege-tags .el-tag {
  margin: 2px;
}

.form-tip {
  color: #909399;
  font-size: 12px;
  margin-left: 8px;
}

.text-muted {
  color: #909399;
}

.filter-container {
  padding-bottom: 10px;
}

.dialog-footer {
  text-align: right;
}

.el-form-item {
  margin-bottom: 18px;
}

.el-checkbox {
  margin-right: 20px;
}
</style>
