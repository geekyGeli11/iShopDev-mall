<template>
  <div class="app-container">
    <!-- Tab切换放到页面顶部 -->
    <el-tabs v-model="activeTab" type="border-card" class="top-tabs">
      <el-tab-pane label="库存管理" name="stock"></el-tab-pane>
      <el-tab-pane label="操作记录" name="logs"></el-tab-pane>
      <el-tab-pane label="调货审核" name="transfer"></el-tab-pane>
    </el-tabs>

    <!-- 库存管理专属的筛选搜索栏 -->
    <el-card v-if="activeTab === 'stock'" class="filter-container" shadow="never">
      <div>
        <i class="el-icon-search"></i>
        <span>筛选搜索</span>
        <el-button
          style="float: right"
          @click="handleSearchList()"
          type="primary"
          size="small">
          查询结果
        </el-button>
        <el-button
          style="float: right;margin-right: 15px"
          @click="handleResetSearch()"
          size="small">
          重置
        </el-button>
      </div>
      <div style="margin-top: 15px">
        <el-form :inline="true" :model="listQuery" size="small" label-width="140px">
          <el-form-item label="输入搜索：">
            <el-input style="width: 203px" v-model="listQuery.keyword" placeholder="商品名称"></el-input>
          </el-form-item>
          <el-form-item label="商品货号：">
            <el-input style="width: 203px" v-model="listQuery.productSn" placeholder="商品货号"></el-input>
          </el-form-item>
          <el-form-item label="商品分类：">
            <el-cascader
              clearable
              v-model="selectProductCateValue"
              :options="productCateOptions">
            </el-cascader>
          </el-form-item>
          <el-form-item label="商品品牌：">
            <el-select v-model="listQuery.brandId" placeholder="请选择品牌" clearable>
              <el-option
                v-for="item in brandOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="上架状态：">
            <el-select v-model="listQuery.publishStatus" placeholder="全部" clearable>
              <el-option
                v-for="item in publishStatusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="审核状态：">
            <el-select v-model="listQuery.verifyStatus" placeholder="全部" clearable>
              <el-option
                v-for="item in verifyStatusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="学校筛选：">
            <el-select 
              v-model="listQuery.schoolId" 
              placeholder="全部学校" 
              :clearable="!isStoreAdmin" 
              :disabled="isStoreAdmin"
              style="width: 203px" 
              @change="handleSchoolChange">
              <el-option
                v-for="item in schoolOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="门店筛选：" v-if="listQuery.schoolId">
            <el-select 
              v-model="listQuery.storeId" 
              placeholder="全部门店" 
              :clearable="!isStoreAdmin" 
              :disabled="isStoreAdmin"
              style="width: 203px"
              @change="handleStoreChange">
              <el-option
                v-for="item in filteredStoreOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 库存管理操作栏 -->
    <el-card v-if="activeTab === 'stock'" class="operate-container" shadow="never">
      <div class="operate-header">
        <div class="operate-title">
          <i class="el-icon-tickets"></i>
          <span>库存管理</span>
          <el-tag v-if="listQuery.storeId" type="primary" size="mini" style="margin-left: 10px">
            当前门店：{{ getStoreName(listQuery.storeId) }}
          </el-tag>
          <el-tag v-else type="info" size="mini" style="margin-left: 10px">
            总库存模式
          </el-tag>
          <el-tag v-if="!listQuery.storeId" type="warning" size="mini" style="margin-left: 10px">
            提示：调货功能需要先选择门店
          </el-tag>
        </div>
        <div class="operate-buttons">
          <el-button
            @click="showStoreDistribution = !showStoreDistribution"
            type="info"
            size="small">
            {{ showStoreDistribution ? '隐藏门店分布' : '显示门店分布' }}
          </el-button>
          <el-button
            v-if="!isBatchMode"
            @click="enterBatchMode"
            type="warning"
            size="small">
            批量操作
          </el-button>
          <el-button
            v-if="isBatchMode"
            @click="exitBatchMode"
            type="info"
            size="small">
            取消批量操作
          </el-button>
        </div>
      </div>
      <!-- 批量操作模式下的操作栏 -->
      <div v-if="isBatchMode" style="margin-top: 15px; padding: 15px; background-color: #f5f7fa; border-radius: 4px;">
        <div style="margin-bottom: 10px;">
          <span style="font-weight: bold;">已选择 {{ multipleSelection.length }} 个商品</span>
        </div>
        <el-form :inline="true" size="small">
          <el-form-item label="操作类型：">
            <el-select v-model="batchOperationType" placeholder="请选择操作类型" style="width: 150px">
              <el-option label="入库" value="in"></el-option>
              <el-option label="出库" value="out"></el-option>
              <el-option label="调整" value="adjust"></el-option>
              <el-option v-if="listQuery.storeId" label="调货" value="transfer"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              size="small"
              @click="handleBatchOperation"
              :disabled="!batchOperationType || multipleSelection.length === 0">
              开始操作
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 库存管理内容区域 -->
    <div v-if="activeTab === 'stock'" class="content-container">
      <div class="table-container">
        <el-table
          ref="productTable"
          :data="productList"
          style="width: 100%"
          @selection-change="handleSelectionChange"
          v-loading="listLoading"
          :key="isBatchMode ? 'batch' : 'normal'">
          <el-table-column v-if="isBatchMode" type="selection" width="60" align="center"></el-table-column>
          <el-table-column v-else width="60" align="center"></el-table-column>
          <el-table-column label="商品编号" width="80" align="center">
            <template slot-scope="scope">{{ scope.row.id }}</template>
          </el-table-column>
          <el-table-column label="商品名称" min-width="200" align="center">
            <template slot-scope="scope">
              <div class="product-info">
                <img :src="scope.row.pic" width="80" height="80" />
                <span>{{ scope.row.name }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="商品货号" width="110" align="center">
            <template slot-scope="scope">{{ scope.row.productSn }}</template>
          </el-table-column>
          <el-table-column label="商品分类" width="80" align="center">
            <template slot-scope="scope">{{ scope.row.productCategoryName }}</template>
          </el-table-column>
          <el-table-column label="品牌" width="80" align="center">
            <template slot-scope="scope">{{ scope.row.brandName }}</template>
          </el-table-column>
          <el-table-column v-if="listQuery.storeId" label="门店库存" width="180" align="center">
            <template slot-scope="scope">
              <div v-if="scope.row.storeStockDisplay" class="store-stock-display">
                <span v-for="(line, index) in scope.row.storeStockDisplay.split('\n')" :key="index">
                  {{ line }}<br v-if="index < scope.row.storeStockDisplay.split('\n').length - 1" />
                </span>
              </div>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="总库存" width="80" align="center">
            <template slot-scope="scope">{{ scope.row.stock }}</template>
          </el-table-column>
          <el-table-column label="低库存预警" width="90" align="center">
            <template slot-scope="scope">{{ scope.row.lowStock }}</template>
          </el-table-column>
          <el-table-column label="库存状态" width="100" align="center">
            <template slot-scope="scope">
              <el-tag 
                :type="scope.row.stock > scope.row.lowStock ? 'success' : 'danger'"
                size="mini">
                {{ scope.row.stock > scope.row.lowStock ? '库存充足' : '库存不足' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column v-if="showStoreDistribution" label="门店分布" width="150" align="center">
            <template slot-scope="scope">
              <el-button
                size="mini"
                type="text"
                @click="handleViewStoreStock(scope.row)">
                查看分布
              </el-button>
            </template>
          </el-table-column>
          <el-table-column v-if="!isBatchMode" label="操作" :width="listQuery.storeId ? '320' : '240'" align="center">
            <template slot-scope="scope">
              <div class="operation-buttons">
                <el-button
                  size="mini"
                  type="success"
                  @click="handleStockOperation(scope.row, 'in')">
                  入库
                </el-button>
                <el-button
                  size="mini"
                  type="warning"
                  @click="handleStockOperation(scope.row, 'out')">
                  出库
                </el-button>
                <el-button
                  size="mini"
                  type="info"
                  @click="handleStockOperation(scope.row, 'adjust')">
                  调整
                </el-button>
                <el-button
                  v-if="listQuery.storeId"
                  size="mini"
                  type="primary"
                  @click="handleStockOperation(scope.row, 'transfer')">
                  调货
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <div class="pagination-container">
        <el-pagination
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          layout="total, sizes,prev, pager, next,jumper"
          :page-size="listQuery.pageSize"
          :page-sizes="[5,10,15]"
          :current-page.sync="listQuery.pageNum"
          :total="total">
        </el-pagination>
      </div>
    </div>

    <!-- 操作记录内容区域 -->
    <div v-if="activeTab === 'logs'" class="content-container">
      <!-- 操作记录筛选 -->
      <el-card class="filter-container" shadow="never" style="margin-bottom: 20px;">
        <el-form :inline="true" :model="logQuery" size="small">
          <el-form-item label="商品搜索：">
            <el-input v-model="logQuery.keyword" placeholder="商品名称/货号" style="width: 150px" clearable></el-input>
          </el-form-item>
          <el-form-item label="操作类型：">
            <el-select v-model="logQuery.operationType" placeholder="全部类型" clearable style="width: 100px">
              <el-option label="入库" value="in"></el-option>
              <el-option label="出库" value="out"></el-option>
              <el-option label="调整" value="adjust"></el-option>
              <el-option label="调货" value="transfer"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="门店筛选：">
            <el-select v-model="logQuery.storeId" placeholder="全部门店" clearable style="width: 150px">
              <el-option
                v-for="item in storeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="时间范围：">
            <el-date-picker
              v-model="logQuery.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="yyyy-MM-dd"
              style="width: 240px">
            </el-date-picker>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearchLog">查询</el-button>
            <el-button @click="handleResetLogSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <div class="table-container">
        <el-table
          :data="logList"
          style="width: 100%"
          v-loading="logLoading">
          <el-table-column label="操作时间" width="180" align="center">
            <template slot-scope="scope">{{ formatLogTime(scope.row.createdAt) }}</template>
          </el-table-column>
          <el-table-column label="操作类型" width="100" align="center">
            <template slot-scope="scope">
              <el-tag 
                :type="getOperationTypeTag(scope.row.operationType)"
                size="mini">
                {{ getOperationTypeText(scope.row.operationType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作子类型" width="100" align="center">
            <template slot-scope="scope">
              <span>{{ getOperationSubtypeText(scope.row.operationType, scope.row.operationSubtype) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="商品信息" align="center">
            <template slot-scope="scope">
              <div>
                {{ scope.row.productName }}
                <span v-if="scope.row.skuCode">({{ scope.row.skuCode }})</span>
              </div>
              <div style="margin-top: 5px;">
                <el-tag 
                  :type="isWarehouse(scope.row.storeId) ? 'warning' : 'primary'"
                  size="mini">
                  {{ getStoreName(scope.row.storeId) }}
                </el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="变更数量" width="100" align="center">
            <template slot-scope="scope">
              <span :class="getOperationQuantityClass(scope.row)">
                {{ getOperationQuantityText(scope.row) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="变更前库存" width="100" align="center">
            <template slot-scope="scope">{{ scope.row.beforeStock }}</template>
          </el-table-column>
          <el-table-column label="变更后库存" width="100" align="center">
            <template slot-scope="scope">{{ scope.row.afterStock }}</template>
          </el-table-column>
          <el-table-column label="操作人" width="100" align="center">
            <template slot-scope="scope">{{ scope.row.operatorName }}</template>
          </el-table-column>
          <el-table-column label="操作理由" align="center">
            <template slot-scope="scope">{{ scope.row.operationReason }}</template>
          </el-table-column>
        </el-table>
      </div>
      
      <div class="pagination-container">
        <el-pagination
          background
          @size-change="handleLogSizeChange"
          @current-change="handleLogCurrentChange"
          layout="total, sizes,prev, pager, next,jumper"
          :page-size="logQuery.pageSize"
          :page-sizes="[5,10,15]"
          :current-page.sync="logQuery.pageNum"
          :total="logTotal">
        </el-pagination>
      </div>
    </div>

    <!-- 调货审核内容区域 -->
    <div v-if="activeTab === 'transfer'" class="content-container">
      <!-- 调货审核筛选 -->
      <el-card class="filter-container" shadow="never" style="margin-bottom: 20px;">
        <!-- 门店账号提示 -->
        <el-alert
          v-if="isStoreAdmin && currentStoreId"
          :title="'当前仅显示与本门店（' + getStoreName(currentStoreId) + '）相关的调货记录'"
          type="info"
          :closable="false"
          style="margin-bottom: 10px;">
        </el-alert>
        <el-form :inline="true" :model="transferQuery" size="small">
          <el-form-item label="操作单号：">
            <el-input v-model="transferQuery.operationNo" placeholder="操作单号" style="width: 150px" clearable></el-input>
          </el-form-item>
          <el-form-item label="审核状态：">
            <el-select v-model="transferQuery.approvalStatus" placeholder="全部状态" clearable style="width: 120px">
              <el-option label="已申请" :value="0"></el-option>
              <el-option label="已发货" :value="1"></el-option>
              <el-option label="已驳回" :value="2"></el-option>
              <el-option label="已确认" :value="3"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="供货门店：">
            <el-select 
              v-model="transferQuery.fromStoreId" 
              placeholder="全部门店" 
              :clearable="!isStoreAdmin" 
              :disabled="isStoreAdmin"
              style="width: 150px">
              <el-option
                v-for="item in storeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="收货门店：">
            <el-select 
              v-model="transferQuery.toStoreId" 
              placeholder="全部门店" 
              :clearable="!isStoreAdmin" 
              :disabled="isStoreAdmin"
              style="width: 150px">
              <el-option
                v-for="item in storeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="时间范围：">
            <el-date-picker
              v-model="transferQuery.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="yyyy-MM-dd"
              style="width: 240px">
            </el-date-picker>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearchTransfer">查询</el-button>
            <el-button @click="handleResetTransferSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <div class="table-container">
        <el-table
          :data="transferList"
          style="width: 100%"
          v-loading="transferLoading">
          <el-table-column label="操作单号" width="180" align="center">
            <template slot-scope="scope">{{ scope.row.operationNo }}</template>
          </el-table-column>
          <el-table-column label="状态" width="100" align="center">
            <template slot-scope="scope">
              <el-tag :type="getTransferStatusTag(scope.row.approvalStatus)" size="mini">
                {{ scope.row.approvalStatusText }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="进度" width="280" align="center">
            <template slot-scope="scope">
              <el-steps :active="getTransferStep(scope.row.approvalStatus)" finish-status="success" simple size="mini" style="margin-top: 5px;">
                <el-step title="已申请"></el-step>
                <el-step title="已发货"></el-step>
                <el-step title="已确认"></el-step>
              </el-steps>
            </template>
          </el-table-column>
          <el-table-column label="供货门店" width="120" align="center">
            <template slot-scope="scope">{{ scope.row.fromStoreName }}</template>
          </el-table-column>
          <el-table-column label="收货门店" width="120" align="center">
            <template slot-scope="scope">{{ scope.row.toStoreName }}</template>
          </el-table-column>
          <el-table-column label="SKU数量" width="80" align="center">
            <template slot-scope="scope">{{ scope.row.totalItems }}</template>
          </el-table-column>
          <el-table-column label="申请人" width="100" align="center">
            <template slot-scope="scope">{{ scope.row.operatorName }}</template>
          </el-table-column>
          <el-table-column label="申请时间" width="160" align="center">
            <template slot-scope="scope">{{ formatDateTime(scope.row.createdAt) }}</template>
          </el-table-column>
          <el-table-column label="操作理由" align="center" show-overflow-tooltip>
            <template slot-scope="scope">{{ scope.row.operationReason }}</template>
          </el-table-column>
          <el-table-column label="操作" width="280" align="center" fixed="right">
            <template slot-scope="scope">
              <el-button size="mini" type="text" @click="handleViewTransferDetail(scope.row)">详情</el-button>
              <el-button 
                v-if="scope.row.canShip" 
                size="mini" 
                type="primary" 
                @click="handleConfirmShipment(scope.row)">确认发货</el-button>
              <el-button 
                v-if="scope.row.canConfirm" 
                size="mini" 
                type="success" 
                @click="handleConfirmReceipt(scope.row)">确认收货</el-button>
              <el-button 
                v-if="scope.row.canReject" 
                size="mini" 
                type="danger" 
                @click="handleRejectTransfer(scope.row)">驳回</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <div class="pagination-container">
        <el-pagination
          background
          @size-change="handleTransferSizeChange"
          @current-change="handleTransferCurrentChange"
          layout="total, sizes,prev, pager, next,jumper"
          :page-size="transferQuery.pageSize"
          :page-sizes="[5,10,15]"
          :current-page.sync="transferQuery.pageNum"
          :total="transferTotal">
        </el-pagination>
      </div>
    </div>

    <!-- 调货详情弹窗 -->
    <el-dialog
      title="调货申请详情"
      :visible.sync="transferDetailVisible"
      width="70%">
      <div v-if="transferDetail">
        <!-- 进度条 -->
        <div style="margin-bottom: 20px; padding: 15px; background-color: #f5f7fa; border-radius: 4px;">
          <el-steps :active="getTransferStep(transferDetail.approval.approvalStatus)" finish-status="success" align-center>
            <el-step title="已申请" :description="formatDateTime(transferDetail.approval.createdAt)"></el-step>
            <el-step title="已发货" :description="transferDetail.approval.shipTime ? formatDateTime(transferDetail.approval.shipTime) : ''"></el-step>
            <el-step title="已确认" :description="transferDetail.approval.confirmTime ? formatDateTime(transferDetail.approval.confirmTime) : ''"></el-step>
          </el-steps>
        </div>

        <!-- 基本信息 -->
        <el-descriptions title="基本信息" :column="3" border style="margin-bottom: 20px;">
          <el-descriptions-item label="操作单号">{{ transferDetail.approval.operationNo }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getTransferStatusTag(transferDetail.approval.approvalStatus)" size="mini">
              {{ getTransferStatusText(transferDetail.approval.approvalStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="SKU数量">{{ transferDetail.approval.totalItems }}</el-descriptions-item>
          <el-descriptions-item label="供货门店">{{ transferDetail.fromStoreName }}</el-descriptions-item>
          <el-descriptions-item label="收货门店">{{ transferDetail.toStoreName }}</el-descriptions-item>
          <el-descriptions-item label="申请人">{{ transferDetail.approval.operatorName }}</el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ formatDateTime(transferDetail.approval.createdAt) }}</el-descriptions-item>
          <el-descriptions-item label="发货人" v-if="transferDetail.approval.shipperName">{{ transferDetail.approval.shipperName }}</el-descriptions-item>
          <el-descriptions-item label="发货时间" v-if="transferDetail.approval.shipTime">{{ formatDateTime(transferDetail.approval.shipTime) }}</el-descriptions-item>
          <el-descriptions-item label="确认人" v-if="transferDetail.approval.confirmerName">{{ transferDetail.approval.confirmerName }}</el-descriptions-item>
          <el-descriptions-item label="确认时间" v-if="transferDetail.approval.confirmTime">{{ formatDateTime(transferDetail.approval.confirmTime) }}</el-descriptions-item>
          <el-descriptions-item label="操作理由" :span="3">{{ transferDetail.approval.operationReason }}</el-descriptions-item>
          <el-descriptions-item label="确认备注" :span="3" v-if="transferDetail.approval.confirmRemark">{{ transferDetail.approval.confirmRemark }}</el-descriptions-item>
        </el-descriptions>

        <!-- 调货明细 -->
        <h4>调货明细</h4>
        <el-table :data="transferDetail.items" style="width: 100%" size="small">
          <el-table-column label="商品名称" align="center">
            <template slot-scope="scope">{{ scope.row.productName }}</template>
          </el-table-column>
          <el-table-column label="SKU编码" width="120" align="center">
            <template slot-scope="scope">{{ scope.row.skuCode }}</template>
          </el-table-column>
          <el-table-column label="申请数量" width="100" align="center">
            <template slot-scope="scope">{{ scope.row.operationQuantity }}</template>
          </el-table-column>
          <el-table-column label="实际收货数量" width="120" align="center">
            <template slot-scope="scope">
              <span v-if="scope.row.actualQuantity !== null">{{ scope.row.actualQuantity }}</span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="数量差异" width="100" align="center">
            <template slot-scope="scope">
              <span v-if="scope.row.quantityDiff !== null" :class="scope.row.quantityDiff !== 0 ? 'text-danger' : ''">
                {{ scope.row.quantityDiff > 0 ? '+' : '' }}{{ scope.row.quantityDiff }}
              </span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="差异原因" align="center" show-overflow-tooltip>
            <template slot-scope="scope">{{ scope.row.diffReason || '-' }}</template>
          </el-table-column>
        </el-table>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="transferDetailVisible = false">关闭</el-button>
        <el-button 
          v-if="transferDetail && transferDetail.canShip" 
          type="primary" 
          @click="handleConfirmShipment(transferDetail.approval)">确认发货</el-button>
        <el-button 
          v-if="transferDetail && transferDetail.canConfirm" 
          type="success" 
          @click="handleShowConfirmReceiptDialog">确认收货</el-button>
        <el-button 
          v-if="transferDetail && transferDetail.canReject" 
          type="danger" 
          @click="handleRejectTransfer(transferDetail.approval)">驳回</el-button>
      </span>
    </el-dialog>

    <!-- 确认收货弹窗 -->
    <el-dialog
      title="确认收货"
      :visible.sync="confirmReceiptVisible"
      width="60%">
      <div v-if="transferDetail">
        <el-alert
          title="请核验实际收货数量，如与申请数量不同请填写差异原因"
          type="info"
          :closable="false"
          style="margin-bottom: 15px;">
        </el-alert>
        
        <el-table :data="confirmReceiptItems" style="width: 100%" size="small">
          <el-table-column label="商品名称" align="center">
            <template slot-scope="scope">{{ scope.row.productName }}</template>
          </el-table-column>
          <el-table-column label="SKU编码" width="120" align="center">
            <template slot-scope="scope">{{ scope.row.skuCode }}</template>
          </el-table-column>
          <el-table-column label="申请数量" width="100" align="center">
            <template slot-scope="scope">{{ scope.row.operationQuantity }}</template>
          </el-table-column>
          <el-table-column label="实际收货数量" width="150" align="center">
            <template slot-scope="scope">
              <el-input-number
                v-model="scope.row.actualQuantity"
                :min="0"
                :max="scope.row.operationQuantity + 100"
                size="small"
                style="width: 120px">
              </el-input-number>
            </template>
          </el-table-column>
          <el-table-column label="差异原因" align="center">
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.diffReason"
                placeholder="如数量不同请填写原因"
                size="small"
                :disabled="scope.row.actualQuantity === scope.row.operationQuantity">
              </el-input>
            </template>
          </el-table-column>
        </el-table>

        <el-form style="margin-top: 15px;">
          <el-form-item label="确认备注：">
            <el-input
              v-model="confirmReceiptRemark"
              type="textarea"
              :rows="2"
              placeholder="可选，填写确认备注">
            </el-input>
          </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="confirmReceiptVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitConfirmReceipt" :loading="confirmReceiptLoading">确认收货</el-button>
      </span>
    </el-dialog>

    <!-- SKU选择和出入库操作弹窗 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="80%"
      @close="handleDialogClose">
      
      <!-- 第一步：选择门店（调货操作时） -->
      <div v-if="operationType === 'transfer' && dialogStep === 1">
        <div class="dialog-header">
          <h4>{{ selectedProduct.name }} - 选择调货门店</h4>
          <div class="transfer-info">
            <span>缺货门店（要增加库存）：{{ getStoreName(transferForm.toStoreId) }}</span>
          </div>
        </div>
        <el-form :model="transferForm" label-width="120px">
          <el-form-item label="供货门店：">
            <el-select v-model="transferForm.fromStoreId" placeholder="请选择供货门店" style="width: 300px">
              <el-option
                v-for="item in storeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
                :disabled="item.value === transferForm.toStoreId">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
        
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="handleTransferNextStep"
            :disabled="!transferForm.fromStoreId || !transferForm.toStoreId">
            下一步
          </el-button>
        </div>
      </div>

      <!-- 第二步：选择SKU和门店库存 -->
      <div v-if="(operationType !== 'transfer' && dialogStep === 1) || (operationType === 'transfer' && dialogStep === 2)">
        <div class="dialog-header">
          <h4>{{ selectedProduct.name }} - 选择SKU</h4>
          <div v-if="operationType === 'transfer'" class="transfer-info">
            <span>调货方向：{{ getStoreName(transferForm.fromStoreId) }} → {{ getStoreName(transferForm.toStoreId) }}</span>
          </div>
          <div v-else-if="listQuery.storeId && (operationType === 'in' || operationType === 'out' || operationType === 'adjust')" class="transfer-info">
            <span>当前门店：{{ getStoreName(listQuery.storeId) }}</span>
          </div>
        </div>
        <el-table
          :data="skuDistributionList"
          style="width: 100%"
          v-loading="skuLoading"
          row-key="skuId">
          <!-- 展开行，显示各门店库存 -->
          <el-table-column type="expand" width="50" align="center">
            <template slot-scope="scope">
              <div class="expand-row">
                <p style="margin-bottom: 10px; font-weight: bold;">选择门店库存：</p>
                <el-table 
                  :data="scope.row.storeDetails" 
                  size="small" 
                  style="margin-left: 20px;"
                  @selection-change="(val) => handleStoreSelection(scope.row, val)">
                  <el-table-column type="selection" width="50" align="center"></el-table-column>
                  <el-table-column label="门店名称" align="center">
                    <template slot-scope="storeScope">{{ storeScope.row.storeName }}</template>
                  </el-table-column>
                  <el-table-column label="库存数量" width="100" align="center">
                    <template slot-scope="storeScope">{{ storeScope.row.stock }}</template>
                  </el-table-column>
                  <el-table-column label="预警数量" width="100" align="center">
                    <template slot-scope="storeScope">{{ storeScope.row.lowStock }}</template>
                  </el-table-column>
                  <el-table-column label="状态" width="100" align="center">
                    <template slot-scope="storeScope">
                      <el-tag 
                        :type="storeScope.row.stock > storeScope.row.lowStock ? 'success' : 'danger'"
                        size="mini">
                        {{ storeScope.row.stock > storeScope.row.lowStock ? '充足' : '不足' }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" width="100" align="center">
                    <template slot-scope="storeScope">
                      <el-button
                        size="mini"
                        type="primary"
                        @click="selectStoreStock(scope.row, storeScope.row, storeScope.$index)">
                        选择
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </template>
          </el-table-column>
          
          <!-- SKU基本信息 -->
          <el-table-column label="SKU编码" width="120" align="center">
            <template slot-scope="scope">{{ scope.row.skuCode }}</template>
          </el-table-column>
          <el-table-column label="规格" align="center">
            <template slot-scope="scope">
              <div class="sku-spec">{{ formatSkuSpec(scope.row.spData) }}</div>
            </template>
          </el-table-column>
          <!-- 总库存 -->
          <el-table-column label="总库存" width="100" align="center">
            <template slot-scope="scope">
              {{ scope.row.totalStock }}
            </template>
          </el-table-column>
          <!-- 门店库存（仅在已选门店时显示） -->
          <el-table-column v-if="listQuery.storeId && (operationType === 'in' || operationType === 'out' || operationType === 'adjust')" label="门店库存" width="100" align="center">
            <template slot-scope="scope">
              {{ scope.row.stock }}
            </template>
          </el-table-column>
          <el-table-column label="预警库存" width="100" align="center">
            <template slot-scope="scope">{{ scope.row.lowStock }}</template>
          </el-table-column>
          <el-table-column label="库存状态" width="100" align="center">
            <template slot-scope="scope">
              <el-tag 
                :type="scope.row.totalStock > scope.row.lowStock ? 'success' : 'danger'"
                size="mini">
                {{ scope.row.totalStock > scope.row.lowStock ? '充足' : '不足' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="已选门店" width="120" align="center">
            <template slot-scope="scope">
              {{ getSelectedStoreCount(scope.row.skuId) }}/{{ scope.row.storeDetails.length }}
            </template>
          </el-table-column>
        </el-table>
        
        <div class="dialog-footer">
          <el-button v-if="operationType === 'transfer'" @click="dialogStep = 1">上一步</el-button>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="handleNextStep"
            :disabled="selectedSkus.length === 0">
            下一步 ({{ selectedSkus.length }})
          </el-button>
        </div>
      </div>

      <!-- 第三步：填写出入库信息 -->
      <div v-if="(operationType !== 'transfer' && dialogStep === 2) || (operationType === 'transfer' && dialogStep === 3)">
        <div class="dialog-header">
          <h4>{{ selectedProduct.name }} - {{ operationTypeText[operationType] }}</h4>
          <div v-if="operationType === 'transfer'" class="transfer-info">
            <span>调货方向：{{ getStoreName(transferForm.fromStoreId) }} → {{ getStoreName(transferForm.toStoreId) }}</span>
          </div>
        </div>
        
        <el-form :model="operationForm" :rules="operationRules" ref="operationForm" label-width="120px">
          <el-form-item label="操作类型：" prop="operationSubtype">
            <el-select v-model="operationForm.operationSubtype" placeholder="请选择操作子类型">
              <el-option
                v-for="item in operationSubtypes"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          
          <el-form-item label="操作理由：" prop="reason">
            <el-input
              v-model="operationForm.reason"
              placeholder="请输入操作理由（必填）"
              type="textarea"
              :rows="3">
            </el-input>
          </el-form-item>
          
          <el-form-item label="操作描述：">
            <el-input
              v-model="operationForm.description"
              type="textarea"
              :rows="3"
              placeholder="请输入详细描述（可选）"
              maxlength="500"
              show-word-limit>
            </el-input>
          </el-form-item>
        </el-form>
        
        <div class="sku-operation-list">
          <h5>操作明细：</h5>
          <!-- 调货操作的特殊表格 -->
          <el-table v-if="operationType === 'transfer'" :data="selectedSkus" style="width: 100%" size="small">
            <el-table-column label="SKU编码" width="90" align="center">
              <template slot-scope="scope">{{ scope.row.skuCode }}</template>
            </el-table-column>
            <el-table-column label="规格" width="70" align="center">
              <template slot-scope="scope">
                <div class="sku-spec" style="font-size: 12px;">{{ formatSkuSpec(scope.row.spData) }}</div>
              </template>
            </el-table-column>
            <el-table-column label="调货信息" min-width="500" align="left">
              <template slot-scope="scope">
                <div style="font-size: 12px; line-height: 1.8;">
                  <div>
                    <span style="color: #606266; display: inline-block; width: 40px;">调出：</span>
                    <span style="font-weight: bold;">{{ getStoreName(transferForm.fromStoreId) }}</span>
                    <span style="color: #909399;">（当前库存 {{ scope.row.currentStock }}）</span>
                    <span style="margin: 0 15px;">→</span>
                    <el-input-number
                      v-model="scope.row.operationQuantity"
                      :min="getMinOperationQuantity(scope.row)"
                      :max="getMaxOperationQuantity(scope.row)"
                      :step="1"
                      size="small"
                      style="width: 100px; vertical-align: middle;"
                      @change="handleOperationQuantityChange(scope.row)">
                    </el-input-number>
                  </div>
                  <div style="margin-top: 5px;">
                    <span style="color: #606266; display: inline-block; width: 40px;">调入：</span>
                    <span style="font-weight: bold;">{{ getStoreName(transferForm.toStoreId) }}</span>
                    <span style="color: #909399;">（当前库存 {{ getTransferTargetStock(scope.row) }}）</span>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="操作后库存" width="140" align="center">
              <template slot-scope="scope">
                <div style="font-size: 12px; line-height: 1.6;">
                  <div>调出：{{ scope.row.currentStock - scope.row.operationQuantity }}</div>
                  <div>调入：{{ getTransferTargetStock(scope.row) + scope.row.operationQuantity }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="60" align="center">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="danger"
                  @click="removeSelectedSku(scope.$index)">
                  移除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <!-- 非调货操作的表格 -->
          <el-table v-else :data="selectedSkus" style="width: 100%">
            <el-table-column label="SKU编码" width="120" align="center">
              <template slot-scope="scope">{{ scope.row.skuCode }}</template>
            </el-table-column>
            <el-table-column label="规格" align="center">
              <template slot-scope="scope">
                <div class="sku-spec">{{ formatSkuSpec(scope.row.spData) }}</div>
              </template>
            </el-table-column>
            <el-table-column v-if="operationType !== 'transfer' && selectedSkus.some(s => s.selectedStoreName)" label="门店名称" width="150" align="center">
              <template slot-scope="scope">
                {{ scope.row.selectedStoreName || '总库存' }}
              </template>
            </el-table-column>
            <el-table-column label="当前库存" width="150" align="center">
              <template slot-scope="scope">
                <div>{{ getCurrentStockText(scope.row) }}</div>
                <el-tag 
                  v-if="operationType === 'out' && getCurrentStock(scope.row) === 0"
                  type="danger" 
                  size="mini" 
                  style="margin-top: 5px;">
                  无库存
                </el-tag>
                <el-tag 
                  v-else-if="operationType === 'out' && getCurrentStock(scope.row) < 10"
                  type="warning" 
                  size="mini" 
                  style="margin-top: 5px;">
                  库存不足
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作数量" width="180" align="center">
              <template slot-scope="scope">
                <el-input-number
                  v-model="scope.row.operationQuantity"
                  :min="getMinOperationQuantity(scope.row)"
                  :max="getMaxOperationQuantity(scope.row)"
                  :step="1"
                  size="small"
                  style="width: 120px"
                  @change="handleOperationQuantityChange(scope.row)">
                </el-input-number>
                <div v-if="operationType === 'out' && scope.row.operationQuantity > getCurrentStock(scope.row)" 
                     style="color: #F56C6C; font-size: 12px; margin-top: 5px;">
                  超出库存！
                </div>
              </template>
            </el-table-column>
            <el-table-column label="操作后库存" width="100" align="center">
              <template slot-scope="scope">
                {{ calculateAfterStock(scope.row) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80" align="center">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="danger"
                  @click="removeSelectedSku(scope.$index)">
                  移除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        
        <div class="dialog-footer">
          <el-button @click="dialogStep = operationType === 'transfer' ? 2 : 1">上一步</el-button>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="handleNextStepToOverview">
            下一步
          </el-button>
        </div>
      </div>

      <!-- 总览单确认步骤 -->
      <div v-if="(operationType !== 'transfer' && dialogStep === 3) || (operationType === 'transfer' && dialogStep === 4)">
        <div class="dialog-header">
          <h4>{{ selectedProduct.name }} - 操作总览单</h4>
          <div v-if="operationType === 'transfer'" class="transfer-info">
            <span>调货方向：{{ getStoreName(transferForm.fromStoreId) }} → {{ getStoreName(transferForm.toStoreId) }}</span>
          </div>
        </div>

        <!-- 操作摘要 -->
        <div class="overview-summary" style="margin-bottom: 20px; padding: 15px; background-color: #f5f7fa; border-radius: 4px;">
          <div style="margin-bottom: 10px;">
            <span style="margin-right: 30px;"><strong>操作类型：</strong>{{ operationTypeText[operationType] }}</span>
            <span><strong>操作时间：</strong>{{ getCurrentDateTime() }}</span>
          </div>
          <div style="margin-bottom: 10px;">
            <span style="margin-right: 30px;"><strong>操作理由：</strong>{{ operationForm.reason }}</span>
            <span><strong>总操作数量：</strong>{{ getTotalOperationQuantity() }}</span>
          </div>
          <div v-if="operationForm.description">
            <span><strong>操作描述：</strong>{{ operationForm.description }}</span>
          </div>
        </div>

        <!-- 操作明细表格 -->
        <div class="overview-details" style="margin-bottom: 20px;">
          <h5>操作明细：</h5>
          <el-table :data="selectedSkus" style="width: 100%" size="small">
            <el-table-column label="SKU编码" width="120" align="center">
              <template slot-scope="scope">{{ scope.row.skuCode }}</template>
            </el-table-column>
            <el-table-column label="规格" align="center">
              <template slot-scope="scope">
                <div class="sku-spec">{{ formatSkuSpec(scope.row.spData) }}</div>
              </template>
            </el-table-column>
            <el-table-column v-if="operationType !== 'transfer' && selectedSkus.some(s => s.selectedStoreName)" label="门店名称" width="150" align="center">
              <template slot-scope="scope">
                {{ scope.row.selectedStoreName || '总库存' }}
              </template>
            </el-table-column>
            <el-table-column label="操作前库存" width="100" align="center">
              <template slot-scope="scope">
                {{ getCurrentStock(scope.row) }}
              </template>
            </el-table-column>
            <el-table-column label="操作数量" width="100" align="center">
              <template slot-scope="scope">
                <span :class="getOperationQuantityClass({operationType: operationType, operationQuantity: scope.row.operationQuantity})">
                  {{ (scope.row.operationQuantity > 0 ? '+' : '') + scope.row.operationQuantity }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="操作后库存" width="100" align="center">
              <template slot-scope="scope">
                {{ calculateAfterStock(scope.row) }}
              </template>
            </el-table-column>
          </el-table>
        </div>

        <div class="dialog-footer">
          <el-button @click="dialogStep = operationType === 'transfer' ? 3 : 2">上一步</el-button>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="handleSubmitOperation"
            :loading="submitLoading">
            确认{{ operationTypeText[operationType] }}
          </el-button>
        </div>
      </div>
    </el-dialog>

    <!-- 批量操作对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="90%"
      @close="handleDialogClose"
      v-if="isBatchMode && batchProducts.length > 0">
      
      <!-- 批量操作SKU选择 -->
      <div v-if="dialogStep === 1 || (operationType === 'transfer' && dialogStep === 2)">
        <div class="dialog-header">
          <h4>批量{{ operationTypeText[operationType] }} - 选择SKU</h4>
          <div v-if="operationType === 'transfer'" class="transfer-info">
            <span>调货方向：{{ getStoreName(transferForm.fromStoreId) }} → {{ getStoreName(transferForm.toStoreId) }}</span>
          </div>
        </div>
        
        <!-- 为每个商品显示SKU列表 -->
        <div v-for="(item, index) in batchSkuDistributionList" :key="index" style="margin-bottom: 30px; border: 1px solid #ddd; padding: 15px; border-radius: 4px;">
          <h5 style="margin-top: 0;">{{ item.product.name }}</h5>
          <el-table
            :data="item.skuList"
            style="width: 100%"
            row-key="skuId">
            <el-table-column type="expand" width="50" align="center">
              <template slot-scope="scope">
                <div class="expand-row">
                  <p style="margin-bottom: 10px; font-weight: bold;">选择门店库存：</p>
                  <el-table 
                    :data="scope.row.storeDetails" 
                    size="small" 
                    style="margin-left: 20px;">
                    <el-table-column label="门店名称" align="center">
                      <template slot-scope="storeScope">{{ storeScope.row.storeName }}</template>
                    </el-table-column>
                    <el-table-column label="库存数量" width="100" align="center">
                      <template slot-scope="storeScope">{{ storeScope.row.stock }}</template>
                    </el-table-column>
                    <el-table-column label="预警数量" width="100" align="center">
                      <template slot-scope="storeScope">{{ storeScope.row.lowStock }}</template>
                    </el-table-column>
                    <el-table-column label="状态" width="100" align="center">
                      <template slot-scope="storeScope">
                        <el-tag 
                          :type="storeScope.row.stock > storeScope.row.lowStock ? 'success' : 'danger'"
                          size="mini">
                          {{ storeScope.row.stock > storeScope.row.lowStock ? '充足' : '不足' }}
                        </el-tag>
                      </template>
                    </el-table-column>
                    <el-table-column label="操作" width="100" align="center">
                      <template slot-scope="storeScope">
                        <el-button
                          size="mini"
                          type="primary"
                          @click="selectBatchStoreStock(item.product, scope.row, storeScope.row)">
                          选择
                        </el-button>
                      </template>
                    </el-table-column>
                  </el-table>
                </div>
              </template>
            </el-table-column>
            
            <el-table-column label="SKU编码" width="120" align="center">
              <template slot-scope="scope">{{ scope.row.skuCode }}</template>
            </el-table-column>
            <el-table-column label="规格" align="center">
              <template slot-scope="scope">
                <div class="sku-spec">{{ formatSkuSpec(scope.row.spData) }}</div>
              </template>
            </el-table-column>
            <el-table-column label="总库存" width="100" align="center">
              <template slot-scope="scope">
                {{ scope.row.totalStock }}
              </template>
            </el-table-column>
            <el-table-column label="预警库存" width="100" align="center">
              <template slot-scope="scope">{{ scope.row.lowStock }}</template>
            </el-table-column>
            <el-table-column label="库存状态" width="100" align="center">
              <template slot-scope="scope">
                <el-tag 
                  :type="scope.row.totalStock > scope.row.lowStock ? 'success' : 'danger'"
                  size="mini">
                  {{ scope.row.totalStock > scope.row.lowStock ? '充足' : '不足' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="已选门店" width="120" align="center">
              <template slot-scope="scope">
                {{ getBatchSelectedStoreCount(scope.row.skuId) }}/{{ scope.row.storeDetails.length }}
              </template>
            </el-table-column>
          </el-table>
        </div>
        
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="handleBatchNextStep"
            :disabled="batchSelectedSkus.length === 0">
            下一步 ({{ batchSelectedSkus.length }})
          </el-button>
        </div>
      </div>
      
      <!-- 批量操作填写信息 -->
      <div v-if="(operationType !== 'transfer' && dialogStep === 2) || (operationType === 'transfer' && dialogStep === 3)">
        <div class="dialog-header">
          <h4>批量{{ operationTypeText[operationType] }} - 填写操作信息</h4>
          <div v-if="operationType === 'transfer'" class="transfer-info">
            <span>调货方向：{{ getStoreName(transferForm.fromStoreId) }} → {{ getStoreName(transferForm.toStoreId) }}</span>
          </div>
        </div>
        
        <el-form :model="operationForm" :rules="operationRules" ref="operationForm" label-width="120px">
          <el-form-item label="操作类型：" prop="operationSubtype">
            <el-select v-model="operationForm.operationSubtype" placeholder="请选择操作子类型">
              <el-option
                v-for="item in operationSubtypes"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          
          <el-form-item label="操作理由：" prop="reason">
            <el-input
              v-model="operationForm.reason"
              placeholder="请输入操作理由（必填）"
              type="textarea"
              :rows="3">
            </el-input>
          </el-form-item>
          
          <el-form-item label="操作描述：">
            <el-input
              v-model="operationForm.description"
              type="textarea"
              :rows="3"
              placeholder="请输入详细描述（可选）"
              maxlength="500"
              show-word-limit>
            </el-input>
          </el-form-item>
        </el-form>
        
        <div class="sku-operation-list">
          <h5>操作明细（已选择 {{ batchSelectedSkus.length }} 个SKU）：</h5>
          <!-- 调货操作的特殊表格 -->
          <el-table v-if="operationType === 'transfer'" :data="batchSelectedSkus" style="width: 100%" size="small">
            <el-table-column label="商品名称" width="100" align="center">
              <template slot-scope="scope">{{ scope.row.productName }}</template>
            </el-table-column>
            <el-table-column label="SKU编码" width="90" align="center">
              <template slot-scope="scope">{{ scope.row.skuCode }}</template>
            </el-table-column>
            <el-table-column label="规格" width="70" align="center">
              <template slot-scope="scope">
                <div class="sku-spec" style="font-size: 12px;">{{ formatSkuSpec(scope.row.spData) }}</div>
              </template>
            </el-table-column>
            <el-table-column label="调货信息" min-width="500" align="left">
              <template slot-scope="scope">
                <div style="font-size: 12px; line-height: 1.8;">
                  <div>
                    <span style="color: #606266; display: inline-block; width: 40px;">调出：</span>
                    <span style="font-weight: bold;">{{ getStoreName(transferForm.fromStoreId) }}</span>
                    <span style="color: #909399;">（当前库存 {{ scope.row.currentStock }}）</span>
                    <span style="margin: 0 15px;">→</span>
                    <el-input-number
                      v-model="scope.row.operationQuantity"
                      :min="getMinOperationQuantity(scope.row)"
                      :max="getMaxOperationQuantity(scope.row)"
                      :step="1"
                      size="small"
                      style="width: 100px; vertical-align: middle;"
                      @change="handleOperationQuantityChange(scope.row)">
                    </el-input-number>
                  </div>
                  <div style="margin-top: 5px;">
                    <span style="color: #606266; display: inline-block; width: 40px;">调入：</span>
                    <span style="font-weight: bold;">{{ getStoreName(transferForm.toStoreId) }}</span>
                    <span style="color: #909399;">（当前库存 {{ getTransferTargetStock(scope.row) }}）</span>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="操作后库存" width="140" align="center">
              <template slot-scope="scope">
                <div style="font-size: 12px; line-height: 1.6;">
                  <div>调出：{{ scope.row.currentStock - scope.row.operationQuantity }}</div>
                  <div>调入：{{ getTransferTargetStock(scope.row) + scope.row.operationQuantity }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="60" align="center">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="danger"
                  @click="removeBatchSelectedSku(scope.$index)">
                  移除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <!-- 非调货操作的表格 -->
          <el-table v-else :data="batchSelectedSkus" style="width: 100%" size="small">
            <el-table-column label="商品名称" width="120" align="center">
              <template slot-scope="scope">{{ scope.row.productName }}</template>
            </el-table-column>
            <el-table-column label="SKU编码" width="100" align="center">
              <template slot-scope="scope">{{ scope.row.skuCode }}</template>
            </el-table-column>
            <el-table-column label="规格" width="80" align="center">
              <template slot-scope="scope">
                <div class="sku-spec" style="font-size: 12px;">{{ formatSkuSpec(scope.row.spData) }}</div>
              </template>
            </el-table-column>
            <el-table-column label="门店名称" width="120" align="center">
              <template slot-scope="scope">
                {{ scope.row.selectedStoreName || '总库存' }}
              </template>
            </el-table-column>
            <el-table-column label="当前库存" width="80" align="center">
              <template slot-scope="scope">
                {{ scope.row.currentStock }}
              </template>
            </el-table-column>
            <el-table-column label="操作数量" width="140" align="center">
              <template slot-scope="scope">
                <el-input-number
                  v-model="scope.row.operationQuantity"
                  :min="getMinOperationQuantity(scope.row)"
                  :max="getMaxOperationQuantity(scope.row)"
                  :step="1"
                  size="small"
                  style="width: 110px"
                  @change="handleOperationQuantityChange(scope.row)">
                </el-input-number>
              </template>
            </el-table-column>
            <el-table-column label="操作后库存" width="100" align="center">
              <template slot-scope="scope">
                {{ calculateAfterStock(scope.row) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="60" align="center">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="danger"
                  @click="removeBatchSelectedSku(scope.$index)">
                  移除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        
        <div class="dialog-footer">
          <el-button @click="dialogStep = 1">上一步</el-button>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="handleBatchNextStepToOverview">
            下一步
          </el-button>
        </div>
      </div>

      <!-- 批量操作总览单确认步骤 -->
      <div v-if="(operationType !== 'transfer' && dialogStep === 3) || (operationType === 'transfer' && dialogStep === 4)">
        <div class="dialog-header">
          <h4>批量{{ operationTypeText[operationType] }} - 操作总览单</h4>
          <div v-if="operationType === 'transfer'" class="transfer-info">
            <span>调货方向：{{ getStoreName(transferForm.fromStoreId) }} → {{ getStoreName(transferForm.toStoreId) }}</span>
          </div>
        </div>

        <!-- 操作摘要 -->
        <div class="overview-summary" style="margin-bottom: 20px; padding: 15px; background-color: #f5f7fa; border-radius: 4px;">
          <div style="margin-bottom: 10px;">
            <span style="margin-right: 30px;"><strong>操作类型：</strong>{{ operationTypeText[operationType] }}</span>
            <span><strong>操作时间：</strong>{{ getCurrentDateTime() }}</span>
          </div>
          <div style="margin-bottom: 10px;">
            <span style="margin-right: 30px;"><strong>操作理由：</strong>{{ operationForm.reason }}</span>
            <span><strong>总操作数量：</strong>{{ getTotalOperationQuantity() }}</span>
          </div>
          <div style="margin-bottom: 10px;">
            <span><strong>涉及商品数：</strong>{{ getBatchProductCount() }}</span>
          </div>
          <div v-if="operationForm.description">
            <span><strong>操作描述：</strong>{{ operationForm.description }}</span>
          </div>
        </div>

        <!-- 操作明细表格 -->
        <div class="overview-details" style="margin-bottom: 20px;">
          <h5>操作明细（共 {{ batchSelectedSkus.length }} 个SKU）：</h5>
          <!-- 调货操作的特殊表格 -->
          <el-table v-if="operationType === 'transfer'" :data="batchSelectedSkus" style="width: 100%" size="small">
            <el-table-column label="商品名称" width="100" align="center">
              <template slot-scope="scope">{{ scope.row.productName }}</template>
            </el-table-column>
            <el-table-column label="SKU编码" width="90" align="center">
              <template slot-scope="scope">{{ scope.row.skuCode }}</template>
            </el-table-column>
            <el-table-column label="规格" width="70" align="center">
              <template slot-scope="scope">
                <div class="sku-spec" style="font-size: 12px;">{{ formatSkuSpec(scope.row.spData) }}</div>
              </template>
            </el-table-column>
            <el-table-column label="调货信息" min-width="500" align="left">
              <template slot-scope="scope">
                <div style="font-size: 12px; line-height: 1.8;">
                  <div>
                    <span style="color: #606266; display: inline-block; width: 40px;">调出：</span>
                    <span style="font-weight: bold;">{{ getStoreName(transferForm.fromStoreId) }}</span>
                    <span style="color: #909399;">（当前库存 {{ scope.row.currentStock }}）</span>
                    <span style="margin: 0 15px;">→</span>
                    <span :class="getOperationQuantityClass({operationType: operationType, operationQuantity: scope.row.operationQuantity})" style="font-weight: bold; display: inline-block; min-width: 50px;">
                      {{ (scope.row.operationQuantity > 0 ? '+' : '') + scope.row.operationQuantity }}
                    </span>
                  </div>
                  <div style="margin-top: 5px;">
                    <span style="color: #606266; display: inline-block; width: 40px;">调入：</span>
                    <span style="font-weight: bold;">{{ getStoreName(transferForm.toStoreId) }}</span>
                    <span style="color: #909399;">（当前库存 {{ getTransferTargetStock(scope.row) }}）</span>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="操作后库存" width="140" align="center">
              <template slot-scope="scope">
                <div style="font-size: 12px; line-height: 1.6;">
                  <div>调出：{{ scope.row.currentStock - scope.row.operationQuantity }}</div>
                  <div>调入：{{ getTransferTargetStock(scope.row) + scope.row.operationQuantity }}</div>
                </div>
              </template>
            </el-table-column>
          </el-table>
          
          <!-- 非调货操作的表格 -->
          <el-table v-else :data="batchSelectedSkus" style="width: 100%" size="small">
            <el-table-column label="商品名称" width="120" align="center">
              <template slot-scope="scope">{{ scope.row.productName }}</template>
            </el-table-column>
            <el-table-column label="SKU编码" width="100" align="center">
              <template slot-scope="scope">{{ scope.row.skuCode }}</template>
            </el-table-column>
            <el-table-column label="规格" width="80" align="center">
              <template slot-scope="scope">
                <div class="sku-spec" style="font-size: 12px;">{{ formatSkuSpec(scope.row.spData) }}</div>
              </template>
            </el-table-column>
            <el-table-column label="门店名称" width="100" align="center">
              <template slot-scope="scope">
                {{ scope.row.selectedStoreName || '总库存' }}
              </template>
            </el-table-column>
            <el-table-column label="操作前库存" width="90" align="center">
              <template slot-scope="scope">
                {{ scope.row.currentStock }}
              </template>
            </el-table-column>
            <el-table-column label="操作数量" width="90" align="center">
              <template slot-scope="scope">
                <span :class="getOperationQuantityClass({operationType: operationType, operationQuantity: scope.row.operationQuantity})">
                  {{ (scope.row.operationQuantity > 0 ? '+' : '') + scope.row.operationQuantity }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="操作后库存" width="90" align="center">
              <template slot-scope="scope">
                {{ calculateAfterStock(scope.row) }}
              </template>
            </el-table-column>
          </el-table>
        </div>

        <div class="dialog-footer">
          <el-button @click="dialogStep = operationType === 'transfer' ? 3 : 2">上一步</el-button>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="handleBatchSubmitOperation"
            :loading="submitLoading">
            确认批量{{ operationTypeText[operationType] }}
          </el-button>
        </div>
      </div>
    </el-dialog>

    <!-- 批量调货门店选择对话框 -->
    <el-dialog
      title="选择调货门店"
      :visible.sync="batchTransferDialogVisible"
      width="500px"
      @close="handleBatchTransferCancel">
      <div>
        <p style="margin-bottom: 15px;">缺货门店（要增加库存）：<strong>{{ getStoreName(listQuery.storeId) }}</strong></p>
        <p style="margin-bottom: 10px;">请选择供货门店：</p>
        <el-select 
          v-model="batchTransferStoreId" 
          placeholder="请选择供货门店"
          style="width: 100%">
          <el-option
            v-for="store in storeOptions.filter(s => s.value !== listQuery.storeId)"
            :key="store.value"
            :label="store.label"
            :value="store.value">
          </el-option>
        </el-select>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="handleBatchTransferCancel">取消</el-button>
        <el-button type="primary" @click="handleBatchTransferConfirm">确定</el-button>
      </span>
    </el-dialog>

    <!-- 门店库存分布弹窗 -->
    <el-dialog
      title="门店库存分布"
      :visible.sync="storeStockDialogVisible"
      width="80%">
      <div v-if="selectedProduct.id">
        <h4>{{ selectedProduct.name }} - 门店库存分布</h4>
        <el-table 
          :data="skuStockDistribution" 
          v-loading="storeStockLoading"
          :default-expand-all="false">
          <!-- 展开行 -->
          <el-table-column type="expand" width="50" align="center">
            <template slot-scope="scope">
              <div class="expand-row">
                <el-table :data="scope.row.storeDetails" size="small" style="margin-left: 50px;">
                  <el-table-column label="门店名称" align="center">
                    <template slot-scope="storeScope">{{ storeScope.row.storeName }}</template>
                  </el-table-column>
                  <el-table-column label="库存数量" width="100" align="center">
                    <template slot-scope="storeScope">{{ storeScope.row.stock }}</template>
                  </el-table-column>
                  <el-table-column label="预警数量" width="100" align="center">
                    <template slot-scope="storeScope">{{ storeScope.row.lowStock }}</template>
                  </el-table-column>
                  <el-table-column label="状态" width="100" align="center">
                    <template slot-scope="storeScope">
                      <el-tag 
                        :type="storeScope.row.stock > storeScope.row.lowStock ? 'success' : 'danger'"
                        size="mini">
                        {{ storeScope.row.stock > storeScope.row.lowStock ? '充足' : '不足' }}
                      </el-tag>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </template>
          </el-table-column>
          
          <!-- SKU基本信息 -->
          <el-table-column label="SKU编码" width="120" align="center">
            <template slot-scope="scope">{{ scope.row.skuCode }}</template>
          </el-table-column>
          <el-table-column label="规格" align="center">
            <template slot-scope="scope">
              <div class="sku-spec">{{ formatSkuSpec(scope.row.spData) }}</div>
            </template>
          </el-table-column>
          <el-table-column label="总库存" width="100" align="center">
            <template slot-scope="scope">{{ scope.row.totalStock }}</template>
          </el-table-column>
          <el-table-column label="预警数量" width="100" align="center">
            <template slot-scope="scope">{{ scope.row.lowStock }}</template>
          </el-table-column>
          <el-table-column label="状态" width="100" align="center">
            <template slot-scope="scope">
              <el-tag 
                :type="scope.row.totalStock > scope.row.lowStock ? 'success' : 'danger'"
                size="mini">
                {{ scope.row.totalStock > scope.row.lowStock ? '充足' : '不足' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="门店数" width="100" align="center">
            <template slot-scope="scope">{{ scope.row.storeDetails.length }}</template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { fetchList } from '@/api/product'
import { fetchList as fetchSkuList } from '@/api/skuStock'
import { fetchList as fetchBrandList } from '@/api/brand'
import { fetchListWithChildren } from '@/api/productCate'
import { fetchEnabledSchools } from '@/api/school'
import { executeStockOperation, fetchStockOperationLogs, fetchTransferApprovalList, fetchTransferApprovalDetail, confirmShipment, confirmReceipt, rejectTransfer, submitTransferApproval } from '@/api/stock'
import { mapState } from 'vuex'

const defaultListQuery = {
  keyword: null,
  pageNum: 1,
  pageSize: 10,
  publishStatus: null,
  verifyStatus: null,
  productSn: null,
  productCategoryId: null,
  brandId: null,
  storeId: null,
  schoolId: null
}

export default {
  name: 'StockManagement',
  components: {
  },
  data() {
    return {
      // 门店筛选初始化标记
      storeFilterInitialized: false,
      // 主页面数据
      activeTab: 'stock',
      listLoading: false,
      productList: [],
      listQuery: Object.assign({}, defaultListQuery),
      total: 0,
      multipleSelection: [],
      selectProductCateValue: null,
      productCateOptions: [],
      brandOptions: [],
      publishStatusOptions: [{
        value: 1,
        label: '上架'
      }, {
        value: 0,
        label: '下架'
      }],
      verifyStatusOptions: [{
        value: 1,
        label: '审核通过'
      }, {
        value: 0,
        label: '未审核'
      }],
      schoolOptions: [],
      
      // 操作记录数据
      logLoading: false,
      logList: [],
      logQuery: {
        pageNum: 1,
        pageSize: 10,
        keyword: null,
        operationType: null,
        storeId: null,
        dateRange: null
      },
      logTotal: 0,
      
      // 弹窗相关
      dialogVisible: false,
      dialogStep: 1, // 1: 选择SKU, 2: 填写操作信息
      dialogTitle: '',
      selectedProduct: {},
      operationType: '', // 'in', 'out', 'adjust'
      operationTypeText: {
        'in': '入库',
        'out': '出库',
        'adjust': '调整',
        'transfer': '调货'
      },
      
      // SKU相关
      skuLoading: false,
      skuList: [],
      skuDistributionList: [],
      selectedSkus: [],
      
      // 操作表单
      operationForm: {
        operationSubtype: '',
        reason: '',
        description: '',
        batchQuantity: 1
      },
      operationRules: {
        operationSubtype: [
          { required: true, message: '请选择操作类型', trigger: 'change' }
        ],
        reason: [
          { required: true, message: '请输入操作理由', trigger: 'blur' }
        ]
      },
      submitLoading: false,
      
      // 操作子类型
      operationSubtypes: [],
      
      // 门店相关
      storeOptions: [],
      filteredStoreOptions: [],
      showStoreDistribution: false,
      storeStockDialogVisible: false,
      storeStockList: [],
      skuStockDistribution: [],
      storeStockLoading: false,
      
      // 门店SKU库存列表（选中门店时显示）
      storeSkuStockList: [],
      storeSkuStockLoading: false,
      
      // 调货相关
      transferForm: {
        fromStoreId: null,
        toStoreId: null
      },
      
      // 批量操作相关
      isBatchMode: false,
      batchOperationType: '',
      batchProducts: [],
      batchSkuDistributionList: [],
      batchSelectedSkus: [],
      batchTransferStoreId: '', // 批量调货时选择的供货门店
      batchTransferDialogVisible: false, // 批量调货门店选择对话框显示状态
      
      // 调货审核相关
      transferLoading: false,
      transferList: [],
      transferQuery: {
        pageNum: 1,
        pageSize: 10,
        operationNo: '',
        approvalStatus: null,
        fromStoreId: null,
        toStoreId: null,
        dateRange: null
      },
      transferTotal: 0,
      transferDetailVisible: false,
      transferDetail: null,
      confirmReceiptVisible: false,
      confirmReceiptItems: [],
      confirmReceiptRemark: '',
      confirmReceiptLoading: false
    }
  },
  computed: {
    // 从 Vuex user 模块获取状态（user 模块没有 namespaced，通过 state.user 访问）
    adminType() {
      return this.$store.state.user.adminType
    },
    currentStoreId() {
      return this.$store.state.user.storeId
    },
    currentSchoolId() {
      return this.$store.state.user.schoolId
    },
    // 是否为门店账号
    isStoreAdmin() {
      return this.adminType === true
    },
    // 根据操作类型获取子类型选项
    operationSubtypeOptions() {
      const options = {
        'in': [
          { value: 1, label: '采购' },
          { value: 2, label: '退换货' },
          { value: 3, label: '盘点' },
          { value: 4, label: '调拨' },
          { value: 5, label: '外借' },
          { value: 6, label: '赠送' },
          { value: 7, label: '其它' }
        ],
        'out': [
          { value: 1, label: '销售' },
          { value: 2, label: '损耗' },
          { value: 3, label: '盘点' },
          { value: 4, label: '外借' },
          { value: 5, label: '赠送' },
          { value: 6, label: '其它' },
          { value: 7, label: '现金销售' }
        ],
        'adjust': [
          { value: 1, label: '库存修正' },
          { value: 2, label: '价格调整' }
        ],
        'transfer': [
          { value: 1, label: '物流' },
          { value: 2, label: '货拉拉' },
          { value: 3, label: '顺风车' },
          { value: 4, label: '其它' }
        ]
      }
      return options[this.operationType] || []
    }
  },
  watch: {
    operationType(val) {
      this.operationSubtypes = this.operationSubtypeOptions
      this.operationForm.operationSubtype = ''
    },
    activeTab(val) {
      // 切换到操作记录标签时自动加载数据
      if (val === 'logs') {
        this.getLogList()
      }
      // 切换到调货审核标签时自动加载数据
      if (val === 'transfer') {
        this.getTransferList()
      }
    },
    selectProductCateValue: function (newValue) {
      if (newValue != null && newValue.length == 2) {
        this.listQuery.productCategoryId = newValue[1]
      } else {
        this.listQuery.productCategoryId = null
      }
    },
    // 监听 adminType 变化，确保 Vuex state 加载后正确设置筛选条件
    adminType: {
      handler(val) {
        console.log('adminType changed:', val, 'storeFilterInitialized:', this.storeFilterInitialized)
        if (val === true && !this.storeFilterInitialized) {
          this.$nextTick(() => {
            this.initStoreFilter()
          })
        }
      },
      immediate: true
    }
  },
  created() {
    this.storeFilterInitialized = false
    // 如果是门店账号，自动设置学校和门店筛选条件
    this.initStoreFilter()
    this.getList()
    this.getBrandList()
    this.getProductCateList()
    this.getStoreList()
    this.getSchoolList().then(() => {
      // 如果是门店账号且有学校ID，加载对应的门店列表
      if (this.isStoreAdmin && this.currentSchoolId) {
        this.loadFilteredStoreOptions(this.currentSchoolId)
      }
    })
  },
  methods: {
    // 初始化门店筛选条件（门店账号专用）
    initStoreFilter() {
      console.log('initStoreFilter called, isStoreAdmin:', this.isStoreAdmin, 'currentSchoolId:', this.currentSchoolId, 'currentStoreId:', this.currentStoreId)
      if (this.isStoreAdmin && !this.storeFilterInitialized) {
        if (this.currentSchoolId) {
          this.listQuery.schoolId = this.currentSchoolId
          console.log('Set listQuery.schoolId to:', this.currentSchoolId)
          // 加载对应学校的门店列表
          this.loadFilteredStoreOptions(this.currentSchoolId)
        }
        if (this.currentStoreId) {
          this.listQuery.storeId = this.currentStoreId
          // 同时初始化调货审核的门店筛选条件
          this.transferQuery.fromStoreId = this.currentStoreId
          this.transferQuery.toStoreId = this.currentStoreId
          console.log('Set listQuery.storeId to:', this.currentStoreId)
        }
        this.storeFilterInitialized = true
        // 重新加载列表数据
        this.getList()
      }
    },
    
    // 获取商品列表
    getList() {
      this.listLoading = true
      fetchList(this.listQuery).then(response => {
        this.listLoading = false
        this.productList = response.data.list
        this.total = parseInt(response.data.total)
      })
    },
    
    // 获取品牌列表
    getBrandList() {
      fetchBrandList({pageNum: 1, pageSize: 100}).then(response => {
        this.brandOptions = []
        let brandList = response.data.list
        for (let i = 0; i < brandList.length; i++) {
          this.brandOptions.push({label: brandList[i].name, value: brandList[i].id})
        }
      })
    },
    
    // 获取商品分类列表
    getProductCateList() {
      fetchListWithChildren().then(response => {
        let list = response.data
        this.productCateOptions = []
        for (let i = 0; i < list.length; i++) {
          let children = []
          if (list[i].children != null && list[i].children.length > 0) {
            for (let j = 0; j < list[i].children.length; j++) {
              children.push({label: list[i].children[j].name, value: list[i].children[j].id})
            }
          }
          this.productCateOptions.push({label: list[i].name, value: list[i].id, children: children})
        }
      })
    },
    
    // 获取门店列表
    getStoreList() {
      // 使用新的 API 获取所有门店（包含地库和中央仓库）
      import('@/api/storeAddress').then(module => {
        module.getAllStores().then(response => {
          this.storeOptions = []
          if (response.data && Array.isArray(response.data)) {
            for (let i = 0; i < response.data.length; i++) {
              const store = response.data[i]
              // 为地库和中央仓库添加特殊标识
              let label = store.addressName
              if (store.storeType === 'CENTRAL_WAREHOUSE') {
                label = `${store.addressName} [仓库]`
              } else if (store.storeType === 'WAREHOUSE' || store.isWarehouse) {
                label = `${store.addressName} [地库]`
              }
              this.storeOptions.push({
                label: label,
                value: store.id,
                isWarehouse: store.isWarehouse || false,
                storeType: store.storeType || 'STORE'
              })
            }
          }
        }).catch(error => {
          console.error('获取门店列表失败:', error)
        })
      })
    },
    
    // 获取学校列表
    getSchoolList() {
      return fetchEnabledSchools().then(response => {
        this.schoolOptions = []
        let schoolList = response.data
        for (let i = 0; i < schoolList.length; i++) {
          this.schoolOptions.push({label: schoolList[i].schoolName, value: schoolList[i].id})
        }
      }).catch(error => {
        console.error('获取学校列表失败:', error)
      })
    },
    
    // 加载指定学校的门店列表
    loadFilteredStoreOptions(schoolId) {
      if (!schoolId) {
        this.filteredStoreOptions = []
        return
      }
      import('@/api/storeAddress').then(module => {
        module.fetchList({ schoolId: schoolId, pageSize: 100 }).then(response => {
          this.filteredStoreOptions = []
          if (response.data && response.data.list) {
            for (let i = 0; i < response.data.list.length; i++) {
              const store = response.data.list[i]
              this.filteredStoreOptions.push({
                label: store.addressName,
                value: store.id
              })
            }
          }
        }).catch(error => {
          console.error('获取门店列表失败:', error)
        })
      })
    },
    
    // 获取操作记录
    getLogList() {
      this.logLoading = true
      // 构建查询参数
      const query = { 
        pageNum: this.logQuery.pageNum,
        pageSize: this.logQuery.pageSize
      }
      // 添加筛选条件
      if (this.logQuery.keyword) {
        query.keyword = this.logQuery.keyword
      }
      if (this.logQuery.operationType) {
        query.operationType = this.logQuery.operationType
      }
      if (this.logQuery.storeId) {
        query.storeId = this.logQuery.storeId
      }
      // 处理时间范围
      if (this.logQuery.dateRange && this.logQuery.dateRange.length === 2) {
        query.startTime = this.logQuery.dateRange[0] + ' 00:00:00'
        query.endTime = this.logQuery.dateRange[1] + ' 23:59:59'
      }
      // 如果是门店账号，强制添加门店筛选条件
      if (this.isStoreAdmin && this.currentStoreId) {
        query.storeId = this.currentStoreId
      }
      fetchStockOperationLogs(query).then(response => {
        this.logLoading = false
        if (response.data && response.data.list) {
          this.logList = response.data.list
          this.logTotal = parseInt(response.data.total)
        } else {
          this.logList = []
          this.logTotal = 0
        }
      }).catch(() => {
        this.logLoading = false
        this.logList = []
        this.logTotal = 0
      })
    },
    
    // 搜索操作记录
    handleSearchLog() {
      this.logQuery.pageNum = 1
      this.getLogList()
    },
    
    // 重置操作记录搜索
    handleResetLogSearch() {
      this.logQuery.keyword = null
      this.logQuery.operationType = null
      this.logQuery.storeId = null
      this.logQuery.dateRange = null
      this.logQuery.pageNum = 1
      this.getLogList()
    },
    
    // 格式化操作记录时间（转换为北京时间）
    formatLogTime(timeStr) {
      if (!timeStr) return ''
      try {
        const date = new Date(timeStr)
        // 转换为北京时间格式
        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        const hours = String(date.getHours()).padStart(2, '0')
        const minutes = String(date.getMinutes()).padStart(2, '0')
        const seconds = String(date.getSeconds()).padStart(2, '0')
        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
      } catch (e) {
        return timeStr
      }
    },
    
    // 处理商品选择
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    
    // 处理搜索
    handleSearchList() {
      this.listQuery.pageNum = 1
      this.getList()
    },
    
    // 重置搜索
    handleResetSearch() {
      this.selectProductCateValue = []
      this.listQuery = Object.assign({}, defaultListQuery)
      // 如果是门店账号，保留学校和门店筛选条件
      if (this.isStoreAdmin) {
        if (this.currentSchoolId) {
          this.listQuery.schoolId = this.currentSchoolId
        }
        if (this.currentStoreId) {
          this.listQuery.storeId = this.currentStoreId
        }
      } else {
        this.filteredStoreOptions = []
      }
    },
    
    // 处理学校选择变化
    handleSchoolChange(schoolId) {
      // 清空门店选择
      this.listQuery.storeId = null
      
      if (schoolId) {
        // 根据学校ID获取对应的门店列表
        import('@/api/storeAddress').then(module => {
          module.fetchList({schoolId: schoolId, pageNum: 1, pageSize: 1000}).then(response => {
            this.filteredStoreOptions = []
            if (response.data && response.data.list && Array.isArray(response.data.list)) {
              for (let i = 0; i < response.data.list.length; i++) {
                const store = response.data.list[i]
                // 为地库和中央仓库添加特殊标识
                let label = store.addressName
                if (store.storeType === 'CENTRAL_WAREHOUSE') {
                  label = `${store.addressName} [仓库]`
                } else if (store.storeType === 'WAREHOUSE' || store.isWarehouse) {
                  label = `${store.addressName} [地库]`
                }
                this.filteredStoreOptions.push({
                  label: label,
                  value: store.id,
                  isWarehouse: store.isWarehouse || false,
                  storeType: store.storeType || 'STORE'
                })
              }
            }
            // 添加全局共享的仓库（地库和中央仓库），这些仓库没有学校ID但应该在所有学校下可见
            this.storeOptions.forEach(store => {
              if (store.storeType === 'CENTRAL_WAREHOUSE' || store.storeType === 'WAREHOUSE' || store.isWarehouse) {
                // 检查是否已存在
                const exists = this.filteredStoreOptions.some(s => s.value === store.value)
                if (!exists) {
                  this.filteredStoreOptions.unshift(store)
                }
              }
            })
          }).catch(error => {
            console.error('获取学校门店列表失败:', error)
            this.filteredStoreOptions = []
          })
        })
      } else {
        this.filteredStoreOptions = []
      }
    },
    
    // 处理门店选择变化
    handleStoreChange(storeId) {
      // 门店变化时重新查询商品列表，接口会返回门店库存信息
      this.handleSearchList()
    },
    
    // 进入批量操作模式
    enterBatchMode() {
      this.isBatchMode = true
      this.batchOperationType = ''
      this.multipleSelection = []
      this.$refs.productTable.clearSelection()
    },
    
    // 退出批量操作模式
    exitBatchMode() {
      this.isBatchMode = false
      this.batchOperationType = ''
      this.multipleSelection = []
      this.batchProducts = []
      this.batchSkuDistributionList = []
      this.batchSelectedSkus = []
      this.$refs.productTable.clearSelection()
    },
    
    // 处理批量操作
    handleBatchOperation() {
      if (!this.batchOperationType) {
        this.$message.warning('请选择操作类型')
        return
      }
      
      if (this.multipleSelection.length === 0) {
        this.$message.warning('请选择至少一个商品')
        return
      }
      
      // 对于调货操作，需要先选择门店
      if (this.batchOperationType === 'transfer') {
        this.showBatchTransferDialog()
      } else {
        // 加载所有选中商品的SKU信息
        this.loadBatchSkuDistribution()
      }
    },
    
    // 显示批量调货门店选择对话框
    showBatchTransferDialog() {
      // 对于批量调货，需要先选择供货门店
      // 重置选择值
      this.batchTransferStoreId = ''
      
      // 构建可用的门店列表，排除缺货门店
      const availableStores = this.storeOptions.filter(store => store.value !== this.listQuery.storeId)
      
      if (availableStores.length === 0) {
        this.$message.warning('没有可用的供货门店')
        return
      }
      
      // 显示对话框
      this.batchTransferDialogVisible = true
    },
    
    // 处理批量调货门店选择确认
    handleBatchTransferConfirm() {
      if (!this.batchTransferStoreId) {
        this.$message.warning('请选择供货门店')
        return
      }
      this.transferForm.fromStoreId = this.batchTransferStoreId
      this.transferForm.toStoreId = this.listQuery.storeId || null
      this.batchTransferDialogVisible = false
      // 设置操作类型为调货，以便 loadBatchSkuDistribution 能正确过滤数据
      this.operationType = 'transfer'
      this.loadBatchSkuDistribution()
    },
    
    // 处理批量调货门店选择取消
    handleBatchTransferCancel() {
      this.batchTransferDialogVisible = false
      this.batchTransferStoreId = ''
    },
    
    // 加载批量操作的SKU分布信息
    loadBatchSkuDistribution() {
      this.skuLoading = true
      this.batchProducts = this.multipleSelection
      this.batchSkuDistributionList = []
      this.batchSelectedSkus = [] // 初始化批量选择的SKU列表
      
      // 为每个选中的商品加载SKU信息
      const promises = this.batchProducts.map(product => {
        return fetchSkuList(product.id).then(response => {
          return {
            product: product,
            skuList: response.data || []
          }
        }).catch(() => {
          return {
            product: product,
            skuList: []
          }
        })
      })
      
      Promise.all(promises).then(results => {
        this.skuLoading = false
        
        // 为每个商品加载门店库存分布
        import('@/api/storeSkuStock').then(module => {
          const storePromises = results.map(result => {
            return module.fetchStoreSkuStockByProduct(result.product.id).then(response => {
              let storeStockData = response.data || []
              
              // 根据操作类型和筛选条件过滤门店SKU数据
              if (this.operationType === 'transfer') {
                // 调货操作：只显示供货门店的SKU
                storeStockData = storeStockData.filter(item => item.storeId === this.transferForm.fromStoreId)
              } else if (this.listQuery.schoolId && !this.listQuery.storeId) {
                // 如果选择了学校但没选门店，只显示该学校的门店SKU
                const schoolStoreIds = this.filteredStoreOptions.map(s => s.value)
                storeStockData = storeStockData.filter(item => schoolStoreIds.includes(item.storeId))
              } else if (this.listQuery.storeId) {
                // 如果选择了门店，只显示该门店的SKU
                storeStockData = storeStockData.filter(item => item.storeId === this.listQuery.storeId)
              }
              
              // 为每个SKU构建门店详情
              // 按skuId分组，这样相同编码的不同SKU会被分别显示
              const skuMap = {}
              storeStockData.forEach(item => {
                if (!skuMap[item.skuId]) {
                  skuMap[item.skuId] = {
                    id: item.id,
                    skuId: item.skuId,
                    skuCode: item.skuCode,
                    spData: item.spData,
                    lowStock: item.lowStock,
                    totalStock: item.totalStock || 0,
                    storeDetails: []
                  }
                }
                skuMap[item.skuId].storeDetails.push({
                  storeId: item.storeId,
                  storeName: item.addressName || this.getStoreName(item.storeId),
                  stock: item.stock,
                  lowStock: item.lowStock,
                  skuId: item.skuId,
                  id: item.id
                })
              })
              
              return {
                product: result.product,
                skuList: Object.values(skuMap),
                storeStockData: storeStockData
              }
            }).catch(() => {
              return {
                product: result.product,
                skuList: [],
                storeStockData: []
              }
            })
          })
          
          Promise.all(storePromises).then(storeResults => {
            this.batchSkuDistributionList = storeResults
            this.showBatchOperationDialog()
          })
        })
      })
    },
    
    // 显示批量操作对话框
    showBatchOperationDialog() {
      this.operationType = this.batchOperationType
      this.dialogTitle = `批量${this.operationTypeText[this.batchOperationType]}`
      this.dialogStep = this.batchOperationType === 'transfer' ? 2 : 1
      this.dialogVisible = true
    },
    
    // 处理分页
    handleSizeChange(val) {
      this.listQuery.pageNum = 1
      this.listQuery.pageSize = val
      this.getList()
    },
    
    handleCurrentChange(val) {
      this.listQuery.pageNum = val
      this.getList()
    },
    
    handleLogSizeChange(val) {
      this.logQuery.pageNum = 1
      this.logQuery.pageSize = val
      this.getLogList()
    },
    
    handleLogCurrentChange(val) {
      this.logQuery.pageNum = val
      this.getLogList()
    },
    
    // 处理出入库操作
    handleStockOperation(product, type) {
      this.selectedProduct = product
      this.operationType = type
      this.dialogTitle = `${product.name} - ${this.operationTypeText[type]}`
      this.dialogStep = 1
      this.dialogVisible = true
      
      // 重置调货表单
      if (type === 'transfer') {
        this.transferForm = {
          fromStoreId: null,
          toStoreId: this.listQuery.storeId || null
        }
      }
      
      this.getSKUList(product.id)
    },
    
    // 获取SKU列表
    getSKUList(productId) {
      this.skuLoading = true
      this.selectedSkus = []
      
      fetchSkuList(productId).then(response => {
        this.skuLoading = false
        let skuList = response.data || []
        
        // 对于入库、出库、调整操作，需要获取所有门店的库存分布
        // 对于调货操作，如果在门店视图，需要获取门店库存信息
        if (this.operationType === 'in' || this.operationType === 'out' || this.operationType === 'adjust' || this.operationType === 'transfer') {
          this.loadSkuStoreDistribution(skuList)
        } else {
          this.skuList = skuList
          this.skuDistributionList = []
        }
      }).catch(error => {
        this.skuLoading = false
        this.$message.error('获取SKU列表失败：' + (error.message || '未知错误'))
      })
    },

    // 加载SKU的门店库存分布
    loadSkuStoreDistribution(skuList) {
      console.log('loadSkuStoreDistribution called with:', {
        operationType: this.operationType,
        fromStoreId: this.transferForm.fromStoreId,
        toStoreId: this.transferForm.toStoreId,
        listQueryStoreId: this.listQuery.storeId,
        skuListLength: skuList.length
      })
      
      if (skuList.length === 0) {
        this.skuDistributionList = []
        return
      }

      import('@/api/storeSkuStock').then(module => {
        module.fetchStoreSkuStockByProduct(this.selectedProduct.id).then(response => {
          console.log('fetchStoreSkuStockByProduct response:', response.data)
          if (response.data && Array.isArray(response.data)) {
            // 处理原始数据，添加门店名称
            let rawData = response.data.map(item => ({
              ...item,
              storeName: item.addressName || this.getStoreName(item.storeId)
            }))
            
            // 如果选择了学校但没选门店，只显示该学校的门店库存
            if (this.listQuery.schoolId && !this.listQuery.storeId) {
              const schoolStoreIds = this.filteredStoreOptions.map(s => s.value)
              rawData = rawData.filter(item => schoolStoreIds.includes(item.storeId))
            }
            
            // 先构建完整的SKU映射（包含所有门店的库存信息）
            // 按skuId分组，这样相同编码的不同SKU会被分别显示
            const skuMap = {}
            rawData.forEach(item => {
              if (!skuMap[item.skuId]) {
                skuMap[item.skuId] = {
                  id: item.id,
                  skuId: item.skuId,
                  skuCode: item.skuCode,
                  spData: item.spData,
                  lowStock: item.lowStock,
                  totalStock: item.totalStock || 0,
                  storeDetails: []
                }
              }
              skuMap[item.skuId].storeDetails.push({
                storeId: item.storeId,
                storeName: item.storeName,
                stock: item.stock,
                lowStock: item.lowStock,
                skuId: item.skuId,
                id: item.id
              })
            })
            
            // 调货操作：显示源门店的库存信息
            if (this.operationType === 'transfer') {
              console.log('Processing transfer operation, fromStoreId:', this.transferForm.fromStoreId)
              const filteredList = []
              Object.values(skuMap).forEach(sku => {
                // 如果已选择供货门店，只显示供货门店的库存
                if (this.transferForm.fromStoreId) {
                  const storeDetail = sku.storeDetails.find(s => s.storeId === this.transferForm.fromStoreId)
                  console.log('Looking for storeId', this.transferForm.fromStoreId, 'in sku', sku.skuCode, 'storeDetails:', sku.storeDetails)
                  if (storeDetail) {
                    const item = {
                      id: storeDetail.id,
                      skuId: sku.skuId,
                      skuCode: sku.skuCode,
                      spData: sku.spData,
                      lowStock: sku.lowStock,
                      totalStock: sku.totalStock,
                      storeId: storeDetail.storeId,
                      storeName: storeDetail.storeName,
                      stock: storeDetail.stock,
                      // 只显示供货门店的库存信息，但保留所有门店信息用于查询调入门店库存
                      storeDetails: [storeDetail],
                      allStoreDetails: sku.storeDetails
                    }
                    console.log('Adding item to filteredList:', item)
                    filteredList.push(item)
                  }
                } else {
                  // 如果还没选择供货门店，显示所有门店的库存分布
                  console.log('fromStoreId not set, adding all stores for sku:', sku.skuCode)
                  filteredList.push(sku)
                }
              })
              console.log('Final skuDistributionList for transfer:', filteredList)
              this.skuDistributionList = filteredList
            } else if (this.listQuery.storeId) {
              // 已选择门店时，显示该门店的库存信息，但展开行只显示该门店
              const filteredList = []
              Object.values(skuMap).forEach(sku => {
                const storeDetail = sku.storeDetails.find(s => s.storeId === this.listQuery.storeId)
                if (storeDetail) {
                  filteredList.push({
                    id: storeDetail.id,
                    skuId: sku.skuId,
                    skuCode: sku.skuCode,
                    spData: sku.spData,
                    lowStock: sku.lowStock,
                    totalStock: sku.totalStock,
                    storeId: storeDetail.storeId,
                    storeName: storeDetail.storeName,
                    stock: storeDetail.stock,
                    // 只包含当前选中门店的库存信息
                    storeDetails: [storeDetail]
                  })
                }
              })
              this.skuDistributionList = filteredList
            } else {
              // 未选择门店时，显示所有门店的库存分布
              this.skuDistributionList = Object.values(skuMap)
            }
          } else {
            this.skuDistributionList = []
          }
        }).catch(error => {
          console.error('加载SKU门店库存分布失败:', error)
          this.skuDistributionList = []
        })
      })
    },

    // 为SKU加载门店库存信息
    loadStoreStockForSkus(skuList) {
      if (skuList.length === 0) {
        this.skuList = []
        return
      }

      // 加载门店库存信息
      import('@/api/storeSkuStock').then(module => {
        const promises = skuList.map(sku => {
          const storeId = this.operationType === 'transfer' ? this.transferForm.fromStoreId : this.listQuery.storeId
          if (storeId) {
            return module.getStoreSkuStock(storeId, sku.id).then(response => {
              return {
                ...sku,
                storeStock: response.data ? response.data.stock : 0,
                operationQuantity: this.getInitialQuantity(sku)
              }
            }).catch(() => {
              return {
                ...sku,
                storeStock: 0,
                operationQuantity: 0
              }
            })
          } else {
            return Promise.resolve({
              ...sku,
              storeStock: 0,
              operationQuantity: this.getInitialQuantity(sku)
            })
          }
        })

        Promise.all(promises).then(results => {
          this.skuList = results
        }).catch(error => {
          console.error('加载门店库存失败:', error)
          this.skuList = skuList.map(item => ({
            ...item,
            storeStock: 0,
            operationQuantity: this.getInitialQuantity(item)
          }))
        })
      })
    },
    
    // 快速选择SKU（已选门店时）
    quickSelectSku(skuItem) {
      const storeId = this.listQuery.storeId
      const existingIndex = this.selectedSkus.findIndex(item => 
        item.skuId === skuItem.skuId && item.selectedStoreId === storeId
      )
      
      if (existingIndex > -1) {
        // 如果已选，则移除
        this.selectedSkus.splice(existingIndex, 1)
        this.$message.info('已取消选择')
      } else {
        // 如果未选，则添加
        const selectedItem = {
          id: skuItem.id,
          skuId: skuItem.skuId,
          skuCode: skuItem.skuCode,
          spData: skuItem.spData,
          lowStock: skuItem.lowStock,
          price: skuItem.price,
          selectedStoreId: storeId,
          selectedStoreName: skuItem.storeName,
          currentStock: skuItem.stock,
          operationQuantity: this.getInitialQuantity({stock: skuItem.stock, lowStock: skuItem.lowStock})
        }
        this.selectedSkus.push(selectedItem)
        this.$message.success('已选择')
      }
    },
    
    // 检查SKU是否已选择
    isSkuSelected(skuId, storeId) {
      return this.selectedSkus.some(item => 
        item.skuId === skuId && item.selectedStoreId === storeId
      )
    },
    
    // 选择门店库存
    selectStoreStock(skuItem, storeItem, storeIndex) {
      // 创建一个唯一的键用于追踪选中的门店库存
      const key = `${skuItem.skuId}_${storeItem.storeId}`
      
      // 检查是否已经选中过，按skuId查找
      const existingIndex = this.selectedSkus.findIndex(item => 
        item.skuId === skuItem.skuId && item.selectedStoreId === storeItem.storeId
      )
      
      if (existingIndex > -1) {
        // 如果已选，则移除
        this.selectedSkus.splice(existingIndex, 1)
        this.$message.info('已取消选择该门店库存')
      } else {
        // 如果未选，则添加
        // 注意：id 应该使用 storeItem.id（pms_store_sku_stock表的id），而不是 skuItem.id
        const selectedItem = {
          id: storeItem.id,
          skuId: skuItem.skuId,
          skuCode: skuItem.skuCode,
          spData: skuItem.spData,
          lowStock: skuItem.lowStock,
          price: skuItem.price,
          selectedStoreId: storeItem.storeId,
          selectedStoreName: storeItem.storeName,
          currentStock: storeItem.stock,
          operationQuantity: this.getInitialQuantity({stock: storeItem.stock, lowStock: storeItem.lowStock})
        }
        this.selectedSkus.push(selectedItem)
        this.$message.success(`已选择 ${storeItem.storeName} 的库存`)
      }
    },
    
    // 处理展开行内表格的复选框选择
    handleStoreSelection(skuItem, selectedRows) {
      // 获取该SKU已选择的门店列表，按skuId查找
      const currentSelectedSkus = this.selectedSkus.filter(item => item.skuId === skuItem.skuId)
      
      // 获取当前展开行中选中的门店ID列表
      const currentSelectedStoreIds = selectedRows.map(row => row.storeId)
      
      // 移除未被选中的项
      for (let i = currentSelectedSkus.length - 1; i >= 0; i--) {
        if (!currentSelectedStoreIds.includes(currentSelectedSkus[i].selectedStoreId)) {
          const index = this.selectedSkus.indexOf(currentSelectedSkus[i])
          if (index > -1) {
            this.selectedSkus.splice(index, 1)
          }
        }
      }
      
      // 添加新选中的项
      selectedRows.forEach(storeItem => {
        const existingIndex = this.selectedSkus.findIndex(item => 
          item.skuId === skuItem.skuId && item.selectedStoreId === storeItem.storeId
        )
        if (existingIndex === -1) {
          // 注意：id 应该使用 storeItem.id（pms_store_sku_stock表的id），而不是 skuItem.id
          const selectedItem = {
            id: storeItem.id,
            skuId: skuItem.skuId,
            skuCode: skuItem.skuCode,
            spData: skuItem.spData,
            lowStock: skuItem.lowStock,
            price: skuItem.price,
            selectedStoreId: storeItem.storeId,
            selectedStoreName: storeItem.storeName,
            currentStock: storeItem.stock,
            operationQuantity: this.getInitialQuantity({stock: storeItem.stock, lowStock: storeItem.lowStock})
          }
          this.selectedSkus.push(selectedItem)
        }
      })
    },
    
    // 获取某个SKU已选中的门店数
    // 按skuId查找
    getSelectedStoreCount(skuId) {
      return this.selectedSkus.filter(item => item.skuId === skuId).length
    },
    
    // 处理SKU选择
    handleSkuSelectionChange(val) {
      this.selectedSkus = val.map(item => ({
        ...item,
        operationQuantity: item.operationQuantity || this.getInitialQuantity(item)
      }))
    },
    
    // 获取初始操作数量
    getInitialQuantity(sku) {
      // 如果通过门店选择，使用选中的库存
      const currentStock = sku.currentStock !== undefined ? sku.currentStock : (sku.stock || 0)
      switch (this.operationType) {
        case 'out':
        case 'transfer':
          return Math.min(1, currentStock)
        case 'adjust':
          return currentStock
        default:
          return 1
      }
    },
    
    // 获取当前日期时间
    getCurrentDateTime() {
      const now = new Date()
      const year = now.getFullYear()
      const month = String(now.getMonth() + 1).padStart(2, '0')
      const day = String(now.getDate()).padStart(2, '0')
      const hours = String(now.getHours()).padStart(2, '0')
      const minutes = String(now.getMinutes()).padStart(2, '0')
      const seconds = String(now.getSeconds()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    },
    
    // 获取总操作数量
    getTotalOperationQuantity() {
      return this.selectedSkus.reduce((total, sku) => {
        return total + (Math.abs(sku.operationQuantity) || 0)
      }, 0)
    },
    
    // 调货操作的下一步
    handleTransferNextStep() {
      if (!this.transferForm.fromStoreId || !this.transferForm.toStoreId) {
        this.$message.warning('请选择供货门店和缺货门店')
        return
      }
      if (this.transferForm.fromStoreId === this.transferForm.toStoreId) {
        this.$message.warning('供货门店和缺货门店不能相同')
        return
      }
      this.dialogStep = 2
      // 重新加载SKU列表，以获取供货门店的库存信息
      this.getSKUList(this.selectedProduct.id)
    },

    // 下一步
    handleNextStep() {
      if (this.selectedSkus.length === 0) {
        this.$message.warning('请选择至少一个SKU')
        return
      }
      
      // 调货操作进入第3步（填写操作信息），其他操作进入第2步（填写操作信息）
      this.dialogStep = this.operationType === 'transfer' ? 3 : 2
      this.operationSubtypes = this.operationSubtypeOptions
    },
    
    // 从操作信息填写进入总览单
    handleNextStepToOverview() {
      this.$refs.operationForm.validate((valid) => {
        if (valid) {
          // 进入总览单步骤
          // 调货操作进入第4步，其他操作进入第3步
          this.dialogStep = this.operationType === 'transfer' ? 4 : 3
        }
      })
    },
    
    // 移除选择的SKU
    removeSelectedSku(index) {
      this.selectedSkus.splice(index, 1)
    },
    
    // 计算操作后库存
    calculateAfterStock(sku) {
      if (this.operationType === 'adjust') {
        return sku.operationQuantity || 0
      }
      
      const quantity = sku.operationQuantity || 0
      const currentStock = this.getCurrentStock(sku)
      
      if (this.operationType === 'out' || this.operationType === 'transfer') {
        return Math.max(0, currentStock - quantity)
      } else {
        return currentStock + quantity
      }
    },

    // 获取当前库存数量
    getCurrentStock(sku) {
      // 如果通过门店选择，显示选中门店的库存
      if (sku.selectedStoreId) {
        return sku.currentStock || 0
      }
      // 如果是调货操作且选择了源门店，显示源门店的库存
      if (this.operationType === 'transfer' && this.transferForm.fromStoreId) {
        return sku.storeStock || 0
      }
      // 如果选择了门店筛选，显示门店库存
      if (this.listQuery.storeId) {
        return sku.storeStock || 0
      }
      // 否则显示总库存
      return sku.stock || 0
    },

    // 获取当前库存显示文本
    getCurrentStockText(sku) {
      const stock = this.getCurrentStock(sku)
      // 如果通过门店选择，显示选中门店的库存
      if (sku.selectedStoreId) {
        return `${stock}（${sku.selectedStoreName}）`
      }
      if (this.operationType === 'transfer' && this.transferForm.fromStoreId) {
        return `${stock}（${this.getStoreName(this.transferForm.fromStoreId)}）`
      }
      if (this.listQuery.storeId) {
        return `${stock}（门店库存）`
      }
      return `${stock}（总库存）`
    },

    // 获取操作数量的最小值
    getMinOperationQuantity(sku) {
      if (this.operationType === 'out' || this.operationType === 'transfer') {
        return 1
      }
      if (this.operationType === 'adjust') {
        return 0
      }
      return 1
    },

    // 获取操作数量的最大值
    getMaxOperationQuantity(sku) {
      if (this.operationType === 'out' || this.operationType === 'transfer') {
        return this.getCurrentStock(sku)
      }
      return 999999
    },

    // 处理操作数量变化
    handleOperationQuantityChange(sku) {
      const currentStock = this.getCurrentStock(sku)
      const quantity = sku.operationQuantity || 0
      
      // 出库操作时，如果数量超过库存，显示警告
      if (this.operationType === 'out' && quantity > currentStock) {
        this.$message.warning(`SKU ${sku.skuCode} 的出库数量(${quantity})不能超过当前库存(${currentStock})`)
        // 自动调整为最大库存
        this.$set(sku, 'operationQuantity', currentStock)
      }
      
      // 调货操作时，如果库存为 0，不允许调货
      if (this.operationType === 'transfer' && currentStock === 0) {
        this.$message.error(`SKU ${sku.skuCode} 的调出门店库存为 0，无法调货`)
        this.$set(sku, 'operationQuantity', 0)
        return
      }
      
      // 调货操作时，如果数量超过供货门店库存，显示警告
      if (this.operationType === 'transfer' && quantity > currentStock) {
        this.$message.warning(`SKU ${sku.skuCode} 的调货数量(${quantity})不能超过供货门店库存(${currentStock})`)
        // 自动调整为最大库存
        this.$set(sku, 'operationQuantity', currentStock)
      }
    },

    // 获取门店名称
    getStoreName(storeId) {
      if (!storeId) return ''
      // 先从filteredStoreOptions中查找，再从storeOptions中查找
      let store = this.filteredStoreOptions.find(item => item.value === storeId)
      if (!store) {
        store = this.storeOptions.find(item => item.value === storeId)
      }
      return store ? store.label : '未知门店'
    },
    
    // 判断是否为地库
    isWarehouse(storeId) {
      if (!storeId) return false
      // 先从filteredStoreOptions中查找，再从storeOptions中查找
      let store = this.filteredStoreOptions.find(item => item.value === storeId)
      if (!store) {
        store = this.storeOptions.find(item => item.value === storeId)
      }
      return store ? (store.isWarehouse || store.storeType === 'WAREHOUSE') : false
    },
    
    // 判断是否为中央仓库
    isCentralWarehouse(storeId) {
      if (!storeId) return false
      let store = this.filteredStoreOptions.find(item => item.value === storeId)
      if (!store) {
        store = this.storeOptions.find(item => item.value === storeId)
      }
      return store ? store.storeType === 'CENTRAL_WAREHOUSE' : false
    },
    
    // 提交操作
    handleSubmitOperation() {
      // 验证操作数据
      const validateResult = this.validateOperationData()
      if (!validateResult.valid) {
        this.$message.error(validateResult.message)
        return
      }
      
      this.submitLoading = true
      
      // 调货操作走审核流程
      if (this.operationType === 'transfer') {
        this.submitTransferApproval()
        return
      }
      
      // 构造请求数据
      const requestData = {
        productId: this.selectedProduct.id,
        operationType: this.operationType,
        operationSubtype: this.operationForm.operationSubtype,
        reason: this.operationForm.reason,
        description: this.operationForm.description
      }

      // 构造操作明细
      requestData.items = this.selectedSkus.map(sku => {
        const item = {
          storeSkuStockId: sku.id,
          skuId: sku.skuId,
          skuCode: sku.skuCode,
          operationQuantity: sku.operationQuantity,
          beforeStock: this.getCurrentStock(sku)
        }

        // 如果通过门店选择，添加选中的门店ID
        if (sku.selectedStoreId) {
          item.storeId = sku.selectedStoreId
        } else if (this.listQuery.storeId) {
          // 如果是门店操作，添加门店ID
          item.storeId = this.listQuery.storeId
        }

        return item
      })
      
      // 调用后端API
      executeStockOperation(requestData).then(response => {
        this.submitLoading = false
        this.$message.success('操作成功')
        this.dialogVisible = false
        this.getList() // 刷新列表
      }).catch(error => {
        this.submitLoading = false
        this.$message.error('操作失败：' + (error.message || '未知错误'))
      })
    },
    
    // 提交调货审核申请
    submitTransferApproval() {
      // 构造调货申请数据
      const requestData = {
        productId: this.selectedProduct.id,
        fromStoreId: this.transferForm.fromStoreId,
        toStoreId: this.transferForm.toStoreId,
        operationSubtype: this.operationForm.operationSubtype,
        reason: this.operationForm.reason,
        description: this.operationForm.description,
        items: this.selectedSkus.map(sku => {
          // 获取供货门店当前库存
          let fromStoreStock = this.getCurrentStock(sku)
          // 获取收货门店当前库存
          let toStoreStock = this.getTransferTargetStock(sku)
          
          return {
            skuId: sku.skuId,
            skuCode: sku.skuCode,
            productName: this.selectedProduct.name,
            productSn: this.selectedProduct.productSn,
            operationQuantity: sku.operationQuantity,
            fromStoreStock: fromStoreStock,
            toStoreStock: toStoreStock
          }
        })
      }
      
      // 调用调货申请API
      submitTransferApproval(requestData).then(response => {
        this.submitLoading = false
        this.$message.success('调货申请提交成功，请等待供货门店确认发货')
        this.dialogVisible = false
        this.getList() // 刷新列表
        // 切换到调货审核tab
        this.activeTab = 'transfer'
      }).catch(error => {
        this.submitLoading = false
        this.$message.error('调货申请提交失败：' + (error.message || '未知错误'))
      })
    },
    
    // 批量操作下一步
    handleBatchNextStep() {
      if (this.batchSelectedSkus.length === 0) {
        this.$message.warning('请选择至少一个SKU')
        return
      }
      this.dialogStep = this.operationType === 'transfer' ? 3 : 2
      this.operationSubtypes = this.operationSubtypeOptions
    },
    
    // 批量操作从填写信息进入总览单
    handleBatchNextStepToOverview() {
      this.$refs.operationForm.validate((valid) => {
        if (valid) {
          // 进入总览单步骤
          // 调货操作进入第4步，其他操作进入第3步
          this.dialogStep = this.operationType === 'transfer' ? 4 : 3
        }
      })
    },
    
    // 获取批量操作中涉及的商品数
    getBatchProductCount() {
      const productIds = new Set(this.batchSelectedSkus.map(sku => sku.productId))
      return productIds.size
    },
    
    // 获取调货操作中调入门店的当前库存
    getTransferTargetStock(sku) {
      // 优先从 SKU 对象中获取 targetStock（批量操作）
      if (sku.targetStock !== undefined) {
        return sku.targetStock
      }
      
      // 从 batchSkuDistributionList 中查找（批量操作）
      if (this.batchSkuDistributionList && this.batchSkuDistributionList.length > 0) {
        // 找到对应商品的分布数据
        const productData = this.batchSkuDistributionList.find(item => item.product.id === sku.productId)
        if (productData && productData.storeStockData) {
          // 找到调入门店的库存，按skuId查找
          const targetStoreStock = productData.storeStockData.find(
            item => item.skuId === sku.skuId && item.storeId === this.transferForm.toStoreId
          )
          if (targetStoreStock) {
            return targetStoreStock.stock
          }
        }
      }
      
      // 从 skuDistributionList 中查找（单个操作）
      // 按skuId查找，这样可以正确处理相同编码的不同SKU
      // 使用 allStoreDetails（包含所有门店）而不是 storeDetails（只包含供货门店）
      if (this.skuDistributionList && this.skuDistributionList.length > 0) {
        // 找到对应SKU的门店详情
        const skuData = this.skuDistributionList.find(item => item.skuId === sku.skuId)
        const storeDetailsToSearch = skuData && skuData.allStoreDetails ? skuData.allStoreDetails : (skuData ? skuData.storeDetails : null)
        if (storeDetailsToSearch) {
          const targetStore = storeDetailsToSearch.find(store => store.storeId === this.transferForm.toStoreId)
          if (targetStore) {
            return targetStore.stock
          }
        }
      }
      
      return 0
    },
    
    // 批量操作中选择门店库存
    selectBatchStoreStock(product, skuItem, storeItem) {
      // 创建唯一键，按skuId而不是skuCode
      const key = `${product.id}_${skuItem.skuId}_${storeItem.storeId}`
      
      // 检查是否已经选中过，按skuId查找
      const existingIndex = this.batchSelectedSkus.findIndex(item => 
        item.productId === product.id && 
        item.skuId === skuItem.skuId && 
        item.selectedStoreId === storeItem.storeId
      )
      
      if (existingIndex > -1) {
        // 如果已选，则移除
        this.batchSelectedSkus.splice(existingIndex, 1)
        this.$message.info('已取消选择该门店库存')
      } else {
        // 如果未选，则添加
        // 获取调入门店的库存
        let targetStock = 0
        if (this.operationType === 'transfer' && this.batchSkuDistributionList && this.batchSkuDistributionList.length > 0) {
          const productData = this.batchSkuDistributionList.find(p => p.product.id === product.id)
          if (productData && productData.storeStockData) {
            const targetStoreData = productData.storeStockData.find(
              s => s.skuId === skuItem.skuId && s.storeId === this.transferForm.toStoreId
            )
            targetStock = targetStoreData ? targetStoreData.stock : 0
          }
        }
        
        // 注意：id 应该使用 storeItem.id（pms_store_sku_stock表的id），而不是 skuItem.id
        const selectedItem = {
          productId: product.id,
          productName: product.name,
          id: storeItem.id,
          skuId: skuItem.skuId,
          skuCode: skuItem.skuCode,
          spData: skuItem.spData,
          lowStock: skuItem.lowStock,
          price: skuItem.price,
          selectedStoreId: storeItem.storeId,
          selectedStoreName: storeItem.storeName,
          currentStock: storeItem.stock,
          targetStock: targetStock,  // 添加调入门店库存
          operationQuantity: this.getInitialQuantity({stock: storeItem.stock, lowStock: storeItem.lowStock})
        }
        this.batchSelectedSkus.push(selectedItem)
        this.$message.success(`已选择 ${storeItem.storeName} 的库存`)
      }
    },
    
    // 获取批量操作中某个SKU已选中的门店数
    // 获取批量操作中某个SKU已选中的门店数
    // 按skuId查找
    getBatchSelectedStoreCount(skuId) {
      return this.batchSelectedSkus.filter(item => 
        item.skuId === skuId
      ).length
    },
    
    // 移除批量操作中选择的SKU
    removeBatchSelectedSku(index) {
      this.batchSelectedSkus.splice(index, 1)
    },
    
    // 提交批量操作
    handleBatchSubmitOperation() {
      // 验证操作数据
      const validateResult = this.validateOperationData()
      if (!validateResult.valid) {
        this.$message.error(validateResult.message)
        return
      }
      
      this.submitLoading = true
      
      // 按商品分组选中的SKU
      const productGroups = {}
      this.batchSelectedSkus.forEach(sku => {
        if (!productGroups[sku.productId]) {
          productGroups[sku.productId] = []
        }
        productGroups[sku.productId].push(sku)
      })
      
      // 为每个商品构造请求数据
      const requests = Object.keys(productGroups).map(productId => {
        const product = this.batchProducts.find(p => p.id === parseInt(productId))
        const skus = productGroups[productId]
        
        const requestData = {
          productId: parseInt(productId),
          operationType: this.operationType,
          operationSubtype: this.operationForm.operationSubtype,
          reason: this.operationForm.reason,
          description: this.operationForm.description
        }

        // 调货操作需要额外的门店信息
        if (this.operationType === 'transfer') {
          requestData.fromStoreId = this.transferForm.fromStoreId
          requestData.toStoreId = this.transferForm.toStoreId
        }

        // 构造操作明细 - 仅包含选中的SKU
        requestData.items = skus.map(sku => {
          const item = {
            storeSkuStockId: sku.id,
            skuId: sku.skuId,
            skuCode: sku.skuCode,
            operationQuantity: sku.operationQuantity,
            beforeStock: sku.currentStock
          }

          // 如果通过门店选择，添加选中的门店ID
          if (sku.selectedStoreId) {
            item.storeId = sku.selectedStoreId
          }

          // 调货操作需要目标门店信息
          if (this.operationType === 'transfer') {
            item.targetStoreId = this.transferForm.toStoreId
            
            // 从 storeStockData 中获取调入门店的库存
            const productData = this.batchSkuDistributionList.find(p => p.product.id === parseInt(productId))
            if (productData && productData.storeStockData) {
              const targetStoreStock = productData.storeStockData.find(
                s => s.skuId === sku.skuId && s.storeId === this.transferForm.toStoreId
              )
              item.targetBeforeStock = targetStoreStock ? targetStoreStock.stock : 0
            } else {
              item.targetBeforeStock = 0
            }
          }

          return item
        })

        return requestData
      })
      
      // 批量执行操作
      const promises = requests.map(requestData => executeStockOperation(requestData))
      
      Promise.all(promises).then(responses => {
        this.submitLoading = false
        this.submitLoading = false
        this.$message.success('批量操作成功')
        this.dialogVisible = false
        this.exitBatchMode()
        this.getList() // 刷新列表
      }).catch(error => {
        this.submitLoading = false
        this.$message.error('批量操作失败：' + (error.message || '未知错误'))
      })
    },
    
    // 获取商品的门店库存详情
    getProductStoreDetails(item, skuId) {
      if (!item.storeStockData || item.storeStockData.length === 0) {
        return []
      }
      
      return item.storeStockData
        .filter(stock => stock.skuId === skuId)
        .map(stock => ({
          storeId: stock.storeId,
          storeName: stock.addressName || this.getStoreName(stock.storeId),
          stock: stock.stock,
          lowStock: stock.lowStock
        }))
    },
    
    // 获取批量操作中商品的SKU数量
    getBatchProductSkuCount(productId) {
      const productData = this.batchSkuDistributionList.find(item => item.product.id === productId)
      return productData ? productData.skuList.length : 0
    },

    // 查看门店库存分布
    handleViewStoreStock(product) {
      this.selectedProduct = product
      this.storeStockDialogVisible = true
      this.getStoreStockDistribution(product.id)
    },

    // 获取门店库存分布
    getStoreStockDistribution(productId) {
      this.storeStockLoading = true
      import('@/api/storeSkuStock').then(module => {
        module.fetchStoreSkuStockByProduct(productId).then(response => {
          this.storeStockLoading = false
          if (response.data && Array.isArray(response.data)) {
            // 处理原始数据，添加门店名称
            const rawData = response.data.map(item => ({
              ...item,
              storeName: item.addressName || this.getStoreName(item.storeId)
            }))
            
            // 按skuId分组，这样相同编码的不同SKU会被分别显示
            const skuMap = {}
            rawData.forEach(item => {
              if (!skuMap[item.skuId]) {
                skuMap[item.skuId] = {
                  id: item.id,
                  skuId: item.skuId,
                  skuCode: item.skuCode,
                  spData: item.spData,
                  lowStock: item.lowStock,
                  totalStock: item.totalStock || 0,  // 使用后端返回的总库存（来自pms_sku_stock表）
                  storeDetails: []
                }
              }
              skuMap[item.skuId].storeDetails.push({
                storeId: item.storeId,
                storeName: item.storeName,
                stock: item.stock,
                lowStock: item.lowStock,
                id: item.id
              })
            })
            
            // 转换为数组并保存
            this.storeStockList = rawData
            this.skuStockDistribution = Object.values(skuMap)
          } else {
            this.storeStockList = []
            this.skuStockDistribution = []
          }
        }).catch(error => {
          this.storeStockLoading = false
          this.storeStockList = []
          this.skuStockDistribution = []
          this.$message.error('获取门店库存分布失败：' + (error.message || '未知错误'))
        })
      })
    },
    
    // 关闭弹窗
    handleDialogClose() {
      this.dialogStep = 1
      this.selectedSkus = []
      this.batchSelectedSkus = []
      this.operationForm = {
        operationSubtype: '',
        reason: '',
        description: '',
        batchQuantity: 1
      }
      this.transferForm = {
        fromStoreId: null,
        toStoreId: null
      }
      this.$refs.operationForm && this.$refs.operationForm.resetFields()
    },
    
    // 获取操作类型标签颜色
    getOperationTypeTag(type) {
      const tagMap = {
        1: 'success', // 入库
        2: 'warning', // 出库
        3: 'info',    // 调整
        4: 'primary'  // 调货
      }
      return tagMap[type] || 'info'
    },
    
    // 获取操作类型文本
    getOperationTypeText(type) {
      const typeMap = {
        1: '入库',
        2: '出库', 
        3: '调整',
        4: '调货'
      }
      return typeMap[type] || '未知'
    },
    
    // 获取操作子类型文本
    getOperationSubtypeText(operationType, subtype) {
      const subtypeMap = {
        1: { // 入库
          1: '采购',
          2: '退换货',
          3: '盘点',
          4: '调拨',
          5: '外借',
          6: '赠送',
          7: '其它'
        },
        2: { // 出库
          1: '销售',
          2: '损耗',
          3: '盘点',
          4: '外借',
          5: '赠送',
          6: '其它',
          7: '现金销售'
        },
        3: { // 调整
          1: '库存修正',
          2: '价格调整'
        },
        4: { // 调货
          1: '物流',
          2: '货拉拉',
          3: '顺风车',
          4: '其它'
        }
      }
      const typeSubtypes = subtypeMap[operationType]
      if (typeSubtypes && typeSubtypes[subtype]) {
        return typeSubtypes[subtype]
      }
      return '-'
    },
    
    // 获取操作数量的样式类
    getOperationQuantityClass(row) {
      const quantity = row.operationQuantity
      
      // 入库操作：绿色
      if (row.operationType === 1) {
        return 'text-success'
      }
      // 出库操作：红色
      if (row.operationType === 2) {
        return 'text-danger'
      }
      // 调整操作：根据数量正负决定
      if (row.operationType === 3) {
        return quantity > 0 ? 'text-success' : 'text-danger'
      }
      // 调货操作：根据数量正负决定（正数绿色，负数红色）
      if (row.operationType === 4) {
        return quantity > 0 ? 'text-success' : 'text-danger'
      }
      return ''
    },
    
    // 获取操作数量的显示文本
    getOperationQuantityText(row) {
      const quantity = row.operationQuantity
      
      // 入库操作：显示 +数量
      if (row.operationType === 1) {
        return '+' + quantity
      }
      // 出库操作：显示 -数量
      if (row.operationType === 2) {
        return '-' + quantity
      }
      // 调整操作：根据数量正负显示
      if (row.operationType === 3) {
        return (quantity > 0 ? '+' : '') + quantity
      }
      // 调货操作：直接显示 operationQuantity（后端已经返回正负值）
      // 负数表示调出，正数表示调入
      if (row.operationType === 4) {
        return (quantity > 0 ? '+' : '') + quantity
      }
      return quantity
    },
    
    // 验证操作数据
    validateOperationData() {
      // 根据是否为批量操作选择要验证的SKU列表
      const skuList = this.isBatchMode ? this.batchSelectedSkus : this.selectedSkus
      
      // 检查是否有选中的SKU
      if (skuList.length === 0) {
        return { valid: false, message: '请选择至少一个SKU' }
      }
      
      // 调货操作需要检查门店选择
      if (this.operationType === 'transfer') {
        if (!this.transferForm.fromStoreId || !this.transferForm.toStoreId) {
          return { valid: false, message: '请选择供货门店和缺货门店' }
        }
      }
      
      // 检查每个SKU的操作数量
      for (let i = 0; i < skuList.length; i++) {
        const sku = skuList[i]
        const quantity = sku.operationQuantity || 0
        const currentStock = sku.currentStock || 0
        
        if (quantity <= 0 && this.operationType !== 'adjust') {
          return { valid: false, message: `SKU ${sku.skuCode} 的操作数量必须大于0` }
        }
        
        // 出库操作：严格验证不能超过门店库存
        if (this.operationType === 'out') {
          if (quantity > currentStock) {
            const stockInfo = sku.selectedStoreName 
              ? `门店"${sku.selectedStoreName}"的库存` 
              : '当前库存'
            return { 
              valid: false, 
              message: `SKU ${sku.skuCode} 的出库数量(${quantity})不能超过${stockInfo}(${currentStock})，请调整出库数量或选择其他门店` 
            }
          }
          if (currentStock === 0) {
            const stockInfo = sku.selectedStoreName 
              ? `门店"${sku.selectedStoreName}"` 
              : '当前'
            return { 
              valid: false, 
              message: `SKU ${sku.skuCode} 在${stockInfo}没有库存，无法出库` 
            }
          }
        }
        
        // 调货操作：验证供货门店库存
        if (this.operationType === 'transfer') {
          // 检查供货门店库存是否充足
          if (currentStock === 0) {
            return { 
              valid: false, 
              message: `SKU ${sku.skuCode} 在供货门店"${this.getStoreName(this.transferForm.fromStoreId)}"没有库存，无法调货` 
            }
          }
          
          if (quantity > currentStock) {
            return { 
              valid: false, 
              message: `SKU ${sku.skuCode} 的调货数量(${quantity})不能超过供货门店库存(${currentStock})，请调整调货数量` 
            }
          }
          
          // 检查调货数量是否大于0
          if (quantity <= 0) {
            return { 
              valid: false, 
              message: `SKU ${sku.skuCode} 的调货数量必须大于0` 
            }
          }
        }
        
        if (this.operationType === 'adjust' && quantity < 0) {
          return { valid: false, message: `SKU ${sku.skuCode} 的调整后库存不能小于0` }
        }
      }
      
      return { valid: true }
    },
    
    // 格式化SKU规格
    formatSkuSpec(spData) {
      if (!spData) return '暂无规格'
      
      try {
        // 如果已经是格式化的字符串（如 "重量 680g"），直接返回
        if (typeof spData === 'string' && !spData.startsWith('[') && !spData.startsWith('{')) {
          return spData
        }
        
        // 如果是JSON字符串，先解析
        let specs = typeof spData === 'string' ? JSON.parse(spData) : spData
        
        // 如果是数组格式 [{"key":"重量","value":"680g"}]
        if (Array.isArray(specs)) {
          const result = specs.map(spec => {
            if (spec.key && spec.value) {
              return `${spec.key}: ${spec.value}`
            }
            return JSON.stringify(spec)
          }).join(', ')
          return result
        }
        
        // 如果是对象格式 {"重量":"680g","口味":"原味"}
        if (typeof specs === 'object' && specs !== null) {
          const result = Object.entries(specs).map(([key, value]) => `${key}: ${value}`).join(', ')
          return result
        }
        
        // 如果是其他格式，转为字符串
        const result = String(specs)
        return result
      } catch (e) {
        // 解析失败时直接返回原始数据
        return String(spData) || '暂无规格'
      }
    },
    
    // ==================== 调货审核相关方法 ====================
    
    // 获取调货申请列表
    getTransferList() {
      this.transferLoading = true
      // 构建查询参数
      const query = { ...this.transferQuery }
      // 处理时间范围
      if (this.transferQuery.dateRange && this.transferQuery.dateRange.length === 2) {
        query.startTime = this.transferQuery.dateRange[0] + ' 00:00:00'
        query.endTime = this.transferQuery.dateRange[1] + ' 23:59:59'
      }
      delete query.dateRange
      // 如果是门店账号，添加门店筛选条件（只显示与当前门店相关的调货记录）
      if (this.isStoreAdmin && this.currentStoreId) {
        query.relatedStoreId = this.currentStoreId
      }
      fetchTransferApprovalList(query).then(response => {
        this.transferList = response.data.list
        this.transferTotal = response.data.total
        this.transferLoading = false
      }).catch(() => {
        this.transferLoading = false
      })
    },
    
    // 搜索调货申请
    handleSearchTransfer() {
      this.transferQuery.pageNum = 1
      this.getTransferList()
    },
    
    // 重置调货搜索
    handleResetTransferSearch() {
      this.transferQuery = {
        pageNum: 1,
        pageSize: 10,
        operationNo: '',
        approvalStatus: null,
        fromStoreId: null,
        toStoreId: null,
        dateRange: null
      }
      // 如果是门店账号，保留门店筛选条件
      if (this.isStoreAdmin && this.currentStoreId) {
        this.transferQuery.fromStoreId = this.currentStoreId
        this.transferQuery.toStoreId = this.currentStoreId
      }
      this.getTransferList()
    },
    
    // 调货分页大小改变
    handleTransferSizeChange(val) {
      this.transferQuery.pageSize = val
      this.getTransferList()
    },
    
    // 调货分页页码改变
    handleTransferCurrentChange(val) {
      this.transferQuery.pageNum = val
      this.getTransferList()
    },
    
    // 获取调货状态标签类型
    getTransferStatusTag(status) {
      const tagMap = {
        0: 'warning',  // 已申请
        1: 'primary',  // 已发货
        2: 'danger',   // 已驳回
        3: 'success'   // 已确认
      }
      return tagMap[status] || 'info'
    },
    
    // 获取调货状态文本
    getTransferStatusText(status) {
      const textMap = {
        0: '已申请',
        1: '已发货',
        2: '已驳回',
        3: '已确认'
      }
      return textMap[status] || '未知'
    },
    
    // 获取调货进度步骤
    getTransferStep(status) {
      if (status === 2) return 0  // 已驳回显示在第一步
      const stepMap = {
        0: 0,  // 已申请
        1: 1,  // 已发货
        3: 3   // 已确认
      }
      return stepMap[status] || 0
    },
    
    // 格式化日期时间
    formatDateTime(dateTime) {
      if (!dateTime) return ''
      const date = new Date(dateTime)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      const seconds = String(date.getSeconds()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    },
    
    // 查看调货详情
    handleViewTransferDetail(row) {
      fetchTransferApprovalDetail(row.id).then(response => {
        this.transferDetail = response.data
        this.transferDetailVisible = true
      }).catch(error => {
        this.$message.error('获取详情失败：' + (error.message || '未知错误'))
      })
    },
    
    // 确认发货
    handleConfirmShipment(row) {
      this.$confirm('确认发货后将扣减供货门店库存，是否继续？', '确认发货', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        confirmShipment(row.id).then(response => {
          this.$message.success('确认发货成功')
          this.getTransferList()
          if (this.transferDetailVisible) {
            this.handleViewTransferDetail(row)
          }
        }).catch(error => {
          this.$message.error('确认发货失败：' + (error.message || '未知错误'))
        })
      }).catch(() => {})
    },
    
    // 显示确认收货弹窗
    handleShowConfirmReceiptDialog() {
      if (!this.transferDetail) return
      
      // 初始化确认收货数据
      this.confirmReceiptItems = this.transferDetail.items.map(item => ({
        itemId: item.id,
        productName: item.productName,
        skuCode: item.skuCode,
        operationQuantity: item.operationQuantity,
        actualQuantity: item.operationQuantity,  // 默认等于申请数量
        diffReason: ''
      }))
      this.confirmReceiptRemark = ''
      this.confirmReceiptVisible = true
    },
    
    // 确认收货（从列表）
    handleConfirmReceipt(row) {
      // 先获取详情再显示确认收货弹窗
      fetchTransferApprovalDetail(row.id).then(response => {
        this.transferDetail = response.data
        this.handleShowConfirmReceiptDialog()
      }).catch(error => {
        this.$message.error('获取详情失败：' + (error.message || '未知错误'))
      })
    },
    
    // 提交确认收货
    handleSubmitConfirmReceipt() {
      if (!this.transferDetail) return
      
      // 验证数量差异是否填写了原因
      for (const item of this.confirmReceiptItems) {
        if (item.actualQuantity !== item.operationQuantity && !item.diffReason) {
          this.$message.warning('实际收货数量与申请数量不同时，请填写差异原因')
          return
        }
      }
      
      this.confirmReceiptLoading = true
      const param = {
        approvalId: this.transferDetail.approval.id,
        confirmRemark: this.confirmReceiptRemark,
        items: this.confirmReceiptItems.map(item => ({
          itemId: item.itemId,
          actualQuantity: item.actualQuantity,
          diffReason: item.diffReason
        }))
      }
      
      confirmReceipt(param).then(response => {
        this.$message.success('确认收货成功')
        this.confirmReceiptVisible = false
        this.transferDetailVisible = false
        this.getTransferList()
        this.confirmReceiptLoading = false
      }).catch(error => {
        this.$message.error('确认收货失败：' + (error.message || '未知错误'))
        this.confirmReceiptLoading = false
      })
    },
    
    // 驳回调货申请
    handleRejectTransfer(row) {
      this.$prompt('请输入驳回原因', '驳回调货申请', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        inputPattern: /\S+/,
        inputErrorMessage: '驳回原因不能为空'
      }).then(({ value }) => {
        rejectTransfer(row.id, value).then(response => {
          this.$message.success('驳回成功')
          this.getTransferList()
          if (this.transferDetailVisible) {
            this.transferDetailVisible = false
          }
        }).catch(error => {
          this.$message.error('驳回失败：' + (error.message || '未知错误'))
        })
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.app-container {
  background: #f5f5f5;
  min-height: calc(100vh - 84px);
}

.top-tabs {
  margin-bottom: 20px;
}

.top-tabs /deep/ .el-tabs__content {
  display: none;
}

.filter-container {
  margin-bottom: 20px;
}

.filter-container .el-card__body {
  padding: 20px;
}

.operate-container {
  margin-bottom: 20px;
}

.operate-container .el-card__body {
  padding: 20px;
}

.operate-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.operate-title {
  display: flex;
  align-items: center;
}

.operate-buttons {
  display: flex;
  gap: 10px;
}

.content-container {
  background: transparent;
}

.table-container {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
}

.pagination-container {
  background: #fff;
  padding: 20px;
  text-align: center;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.product-info img {
  border-radius: 4px;
  object-fit: cover;
}

.operation-buttons {
  display: flex;
  gap: 5px;
  justify-content: center;
  flex-wrap: wrap;
}

.operation-buttons .el-button {
  margin: 2px 0;
}

.sku-spec {
  line-height: 1.5;
  word-break: break-word;
  max-width: 200px;
}

.dialog-header {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.dialog-footer {
  text-align: right;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.sku-operation-list {
  margin-top: 20px;
}

.sku-operation-list h5 {
  margin-bottom: 10px;
  color: #333;
}

.text-success {
  color: #67C23A;
}

.text-danger {
  color: #F56C6C;
}

.el-tabs /deep/ .el-tabs__content {
  padding: 0;
}

.el-tabs /deep/ .el-tab-pane {
  background: #fff;
  border-radius: 4px;
}

.transfer-info {
  margin-top: 10px;
  padding: 10px;
  background: #f0f9ff;
  border: 1px solid #b3d8ff;
  border-radius: 4px;
  color: #1890ff;
  font-weight: bold;
}

.store-stock-display {
  text-align: left;
  font-size: 12px;
  line-height: 1.6;
  color: #606266;
}
</style> 