<template>
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
      <div>
        <i class="el-icon-search"></i>
        <span>筛选搜索</span>
        <el-button
          style="float:right"
          type="primary"
          @click="handleSearchList()"
          size="small">
          查询搜索
        </el-button>
        <el-button
          style="float:right;margin-right: 15px"
          @click="handleResetSearch()"
          size="small">
          重置
        </el-button>
      </div>
      <div style="margin-top: 15px">
        <el-form :inline="true" :model="listQuery" size="small" label-width="140px">
          <el-form-item label="模板名称：">
            <el-input v-model="listQuery.keyword" class="input-width" placeholder="模板名称"></el-input>
          </el-form-item>
          <el-form-item label="状态：">
            <el-select v-model="listQuery.status" class="input-width" placeholder="全部" clearable>
              <el-option
                v-for="item in statusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="计费类型：">
            <el-select v-model="listQuery.chargeType" class="input-width" placeholder="全部" clearable>
              <el-option
                v-for="item in chargeTypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="operate-container" shadow="never">
      <i class="el-icon-tickets"></i>
      <span>数据列表</span>
      <el-button
        style="float: right"
        type="primary"
        @click="handleAdd()"
        size="small">
        添加运费模板
      </el-button>
      <el-button
        style="float: right; margin-right: 10px"
        type="warning"
        @click="handleRefreshAllProduct()"
        size="small">
        刷新全部商品运费模板
      </el-button>
    </el-card>
    <!-- 刷新全部商品运费模板对话框 -->
    <el-dialog
      title="刷新全部商品运费模板"
      :visible.sync="refreshDialogVisible"
      width="500px">
      <el-form label-width="120px">
        <el-form-item label="选择运费模板：">
          <el-select v-model="selectedTemplateId" placeholder="请选择运费模板" style="width: 100%">
            <el-option
              v-for="item in enabledTemplateList"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <span style="color: #E6A23C; font-size: 12px">
            <i class="el-icon-warning"></i>
            此操作将覆盖所有商品的运费模板设置，请谨慎操作！
          </span>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="refreshDialogVisible = false" size="small">取 消</el-button>
        <el-button type="primary" @click="confirmRefreshAllProduct()" size="small" :loading="refreshLoading">确 定</el-button>
      </span>
    </el-dialog>
    <div class="table-container">
      <el-table ref="freightTable"
                :data="list"
                style="width: 100%;"
                @selection-change="handleSelectionChange"
                v-loading="listLoading" border>
        <el-table-column type="selection" width="60" align="center"></el-table-column>
        <el-table-column label="编号" width="80" align="center">
          <template slot-scope="scope">{{scope.row.id}}</template>
        </el-table-column>
        <el-table-column label="模板名称" align="center">
          <template slot-scope="scope">
            <span>{{scope.row.name}}</span>
            <el-tag v-if="scope.row.isDefault === true" type="success" size="mini" style="margin-left: 8px">默认</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="计费类型" width="120" align="center">
          <template slot-scope="scope">{{scope.row.chargeType | formatChargeType}}</template>
        </el-table-column>
        <el-table-column label="配送类型" width="120" align="center">
          <template slot-scope="scope">{{scope.row.deliveryType | formatDeliveryType}}</template>
        </el-table-column>
        <el-table-column label="包邮条件" width="180" align="center">
          <template slot-scope="scope">{{scope.row | formatFreeCondition}}</template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-switch
              @change="handleStatusChange(scope.$index,scope.row)"
              :active-value="1"
              :inactive-value="0"
              v-model="scope.row.status">
            </el-switch>
          </template>
        </el-table-column>
        <el-table-column label="排序" width="100" align="center">
          <template slot-scope="scope">{{scope.row.sort}}</template>
        </el-table-column>
        <el-table-column label="创建时间" width="180" align="center">
          <template slot-scope="scope">{{scope.row.createTime | formatDateTime}}</template>
        </el-table-column>
        <el-table-column label="操作" width="280" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="primary"
              @click="handleUpdate(scope.$index, scope.row)">编辑</el-button>
            <el-button
              v-if="scope.row.isDefault !== true"
              size="mini"
              type="warning"
              @click="handleSetDefault(scope.$index, scope.row)">设为默认</el-button>
            <el-button
              size="mini"
              type="danger"
              @click="handleDelete(scope.$index, scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="batch-operate-container">
      <el-select
        size="small"
        v-model="operateType" placeholder="批量操作">
        <el-option
          v-for="item in operateOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value">
        </el-option>
      </el-select>
      <el-button
        style="margin-left: 20px"
        class="search-button"
        @click="handleBatchOperate()"
        type="primary"
        size="small">
        确定
      </el-button>
    </div>
    <div class="pagination-container">
      <el-pagination
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        layout="total, sizes,prev, pager, next,jumper"
        :current-page.sync="listQuery.pageNum"
        :page-size="listQuery.pageSize"
        :page-sizes="[5,10,15]"
        :total="total">
      </el-pagination>
    </div>
  </div>
</template>

<script>
  import {
    fetchFreightTemplateList,
    deleteFreightTemplate,
    batchDeleteFreightTemplate,
    updateFreightTemplateStatus,
    setDefaultFreightTemplate,
    fetchAllEnabledFreightTemplates,
    refreshAllProductFreightTemplate
  } from '@/api/freight'

  export default {
    name: 'freightTemplateList',
    data() {
      return {
        listQuery: {
          pageNum: 1,
          pageSize: 10,
          keyword: null,
          status: null,
          chargeType: null
        },
        list: null,
        total: null,
        listLoading: false,
        multipleSelection: [],
        operateType: null,
        statusOptions: [
          {
            label: '启用',
            value: 1
          },
          {
            label: '禁用',
            value: 0
          }
        ],
        chargeTypeOptions: [
          {
            label: '按件数',
            value: 1
          },
          {
            label: '按重量',
            value: 2
          },
          {
            label: '按体积',
            value: 3
          },
          {
            label: '固定运费',
            value: 4
          }
        ],
        operateOptions: [
          {
            label: '批量删除',
            value: 'delete'
          },
          {
            label: '批量启用',
            value: 'enable'
          },
          {
            label: '批量禁用',
            value: 'disable'
          }
        ],
        refreshDialogVisible: false,
        selectedTemplateId: null,
        enabledTemplateList: [],
        refreshLoading: false
      }
    },
    created() {
      this.getList()
    },
    filters: {
      formatChargeType(chargeType) {
        const map = {
          1: '按件数',
          2: '按重量', 
          3: '按体积',
          4: '固定运费'
        }
        return map[chargeType] || '未知'
      },
      formatDeliveryType(deliveryType) {
        const map = {
          1: '快递配送',
          2: '同城配送',
          3: '到店自提'
        }
        return map[deliveryType] || '未知'
      },
      formatFreeCondition(row) {
        if (row.freeType === 0) {
          return '不包邮'
        } else if (row.freeType === 1) {
          return `满${row.freeAmount}元包邮`
        } else if (row.freeType === 2) {
          return `满${row.freeCount}件包邮`
        } else if (row.freeType === 3) {
          return `满${row.freeWeight}kg包邮`
        }
        return '未设置'
      },
      formatDateTime(time) {
        if (time == null || time === '') {
          return 'N/A'
        }
        let date = new Date(time)
        return date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate() + ' ' + date.getHours() + ':' + date.getMinutes() + ':' + date.getSeconds()
      }
    },
    methods: {
      handleResetSearch() {
        this.listQuery = {
          pageNum: 1,
          pageSize: 10,
          keyword: null,
          status: null,
          chargeType: null
        }
      },
      handleSearchList() {
        this.listQuery.pageNum = 1
        this.getList()
      },
      handleSizeChange(val) {
        this.listQuery.pageNum = 1
        this.listQuery.pageSize = val
        this.getList()
      },
      handleCurrentChange(val) {
        this.listQuery.pageNum = val
        this.getList()
      },
      handleSelectionChange(val) {
        this.multipleSelection = val
      },
      handleAdd() {
        this.$router.push('/settings/freight/add')
      },
      handleUpdate(index, row) {
        this.$router.push('/settings/freight/update/' + row.id)
      },
      handleStatusChange(index, row) {
        this.$confirm('是否要修改该模板状态?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          updateFreightTemplateStatus([row.id], row.status).then(response => {
            this.$message({
              type: 'success',
              message: '修改成功!'
            })
          })
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '取消修改'
          })
          this.getList()
        })
      },
      handleDelete(index, row) {
        this.$confirm('是否要删除该运费模板?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          deleteFreightTemplate(row.id).then(response => {
            this.$message({
              type: 'success',
              message: '删除成功!'
            })
            this.getList()
          })
        })
      },
      handleBatchOperate() {
        if (this.multipleSelection < 1) {
          this.$message({
            message: '请选择一条记录',
            type: 'warning',
            duration: 1000
          })
          return
        }
        let ids = []
        for (let i = 0; i < this.multipleSelection.length; i++) {
          ids.push(this.multipleSelection[i].id)
        }
        if (this.operateType === 'delete') {
          this.$confirm('是否要删除选中的运费模板?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            batchDeleteFreightTemplate(ids.join(',')).then(response => {
              this.$message({
                type: 'success',
                message: '删除成功!'
              })
              this.getList()
            })
          })
        } else if (this.operateType === 'enable') {
          updateFreightTemplateStatus(ids.join(','), 1).then(response => {
            this.$message({
              type: 'success',
              message: '启用成功!'
            })
            this.getList()
          })
        } else if (this.operateType === 'disable') {
          updateFreightTemplateStatus(ids.join(','), 0).then(response => {
            this.$message({
              type: 'success',
              message: '禁用成功!'
            })
            this.getList()
          })
        }
      },
      handleSetDefault(index, row) {
        this.$confirm('是否要设为默认运费模板?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          setDefaultFreightTemplate(row.id).then(response => {
            this.$message({
              type: 'success',
              message: '设为默认成功!'
            })
            this.getList()
          })
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '取消设为默认'
          })
          this.getList()
        })
      },
      getList() {
        this.listLoading = true
        fetchFreightTemplateList(this.listQuery).then(response => {
          this.listLoading = false
          this.list = response.data.list
          this.total = response.data.total
        })
      },
      handleRefreshAllProduct() {
        // 获取启用的运费模板列表
        fetchAllEnabledFreightTemplates().then(response => {
          this.enabledTemplateList = response.data
          this.selectedTemplateId = null
          this.refreshDialogVisible = true
        })
      },
      confirmRefreshAllProduct() {
        if (!this.selectedTemplateId) {
          this.$message({
            message: '请选择运费模板',
            type: 'warning'
          })
          return
        }
        this.$confirm('此操作将覆盖所有商品的运费模板设置，是否继续?', '警告', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.refreshLoading = true
          refreshAllProductFreightTemplate(this.selectedTemplateId).then(response => {
            this.refreshLoading = false
            this.refreshDialogVisible = false
            this.$message({
              type: 'success',
              message: '刷新成功!'
            })
          }).catch(() => {
            this.refreshLoading = false
          })
        })
      }
    }
  }
</script>

<style scoped>
  .input-width {
    width: 203px;
  }
</style>