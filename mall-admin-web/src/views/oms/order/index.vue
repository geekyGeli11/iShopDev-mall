<template> 
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
      <div>
        <i class="el-icon-search"></i>
        <span>筛选搜索</span>
        <el-button
          style="float:right"
          type="primary"
          @click="handleSearchListWithState()"
          size="small">
          查询搜索
        </el-button>
        <el-button
          style="float:right;margin-right: 15px"
          @click="handleResetSearchWithState()"
          size="small">
          重置
        </el-button>
        <!-- 桌面端显示所有按钮 -->
        <el-button
          v-if="!isMobile"
          style="float:right;margin-right: 15px"
          type="primary"
          @click="handleExport"
          size="small">
          导出数据
        </el-button>
        <el-button
          v-if="!isMobile"
          style="float:right;margin-right: 15px"
          type="success"
          @click="handleDownloadTemplate"
          size="small">
          下载发货模板
        </el-button>
        <el-button
          v-if="!isMobile"
          style="float:right;margin-right: 15px"
          type="warning"
          @click="handleBatchDelivery"
          size="small">
          批量发货
        </el-button>
      </div>
      <div style="margin-top: 15px">
        <!-- 移动端简化搜索表单 -->
        <div v-if="isMobile" class="mobile-search-form">
          <div class="search-row">
            <el-input
              v-model="listQuery.orderSn"
              placeholder="订单编号"
              size="small"
              clearable>
              <i slot="prefix" class="el-input__icon el-icon-search"></i>
            </el-input>
          </div>
          <div class="search-row">
            <el-input
              v-model="listQuery.receiverKeyword"
              placeholder="收货人姓名/手机号码"
              size="small"
              clearable>
              <i slot="prefix" class="el-input__icon el-icon-user"></i>
            </el-input>
          </div>
          <div class="search-row">
            <el-input
              v-model="listQuery.productName"
              placeholder="商品名称"
              size="small"
              clearable>
              <i slot="prefix" class="el-input__icon el-icon-goods"></i>
            </el-input>
          </div>
          <div class="search-row">
            <el-select
              v-model="listQuery.status"
              placeholder="订单状态"
              size="small"
              clearable
              style="width: 100%">
              <el-option v-for="item in statusOptions"
                         :key="item.value"
                         :label="item.label"
                         :value="item.value">
              </el-option>
            </el-select>
          </div>
          <div class="search-row">
            <el-select
              v-model="listQuery.hasReturnApply"
              placeholder="售后申请"
              size="small"
              clearable
              style="width: 100%">
              <el-option v-for="item in returnApplyOptions"
                         :key="item.value"
                         :label="item.label"
                         :value="item.value">
              </el-option>
            </el-select>
          </div>
          <div class="search-row">
            <div class="mobile-date-picker">
              <el-button
                size="small"
                style="width: 100%; text-align: left; color: #606266;"
                @click="showMobileDatePicker = true">
                <i class="el-icon-date" style="margin-right: 5px;"></i>
                <span v-if="dateRange && dateRange.length === 2">
                  {{dateRange[0]}} 至 {{dateRange[1]}}
                </span>
                <span v-else style="color: #C0C4CC;">选择时间范围</span>
              </el-button>
            </div>
          </div>
          <div class="search-actions">
            <el-button size="small" @click="handleSearchListWithState" type="primary" style="flex: 1;">搜索</el-button>
            <el-button size="small" @click="handleResetSearchWithState" style="flex: 1; margin-left: 10px;">重置</el-button>
          </div>
        </div>

        <!-- 桌面端完整搜索表单 -->
        <el-form v-else :inline="true" :model="listQuery" size="small" label-width="140px">
          <el-form-item label="输入搜索：">
            <el-input v-model="listQuery.orderSn" class="input-width" placeholder="订单编号"></el-input>
          </el-form-item>
          <el-form-item label="收货人：">
            <el-input v-model="listQuery.receiverKeyword" class="input-width" placeholder="收货人姓名/手机号码"></el-input>
          </el-form-item>
          <el-form-item label="商品名称：">
            <el-input v-model="listQuery.productName" class="input-width" placeholder="商品名称"></el-input>
          </el-form-item>
          <el-form-item label="提交时间：">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="yyyy-MM-dd"
              class="input-width"
              @change="handleDateChange">
            </el-date-picker>
          </el-form-item>
          <el-form-item label="订单状态：">
            <el-select v-model="listQuery.status" class="input-width" placeholder="全部" clearable>
              <el-option v-for="item in statusOptions"
                         :key="item.value"
                         :label="item.label"
                         :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="订单分类：">
            <el-select v-model="listQuery.orderType" class="input-width" placeholder="全部" clearable>
              <el-option v-for="item in orderTypeOptions"
                         :key="item.value"
                         :label="item.label"
                         :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="订单来源：">
            <el-select v-model="listQuery.sourceType" class="input-width" placeholder="全部" clearable>
              <el-option v-for="item in sourceTypeOptions"
                         :key="item.value"
                         :label="item.label"
                         :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="取货方式：">
            <el-select v-model="listQuery.deliveryType" class="input-width" placeholder="全部" clearable>
              <el-option v-for="item in deliveryTypeOptions"
                         :key="item.value"
                         :label="item.label"
                         :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="支付方式：">
            <el-select v-model="listQuery.payType" class="input-width" placeholder="全部" clearable>
              <el-option v-for="item in payTypeOptions"
                         :key="item.value"
                         :label="item.label"
                         :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="是否礼物：">
            <el-select v-model="listQuery.isGift" class="input-width" placeholder="全部" clearable>
              <el-option v-for="item in isGiftOptions"
                         :key="item.value"
                         :label="item.label"
                         :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="售后申请：">
            <el-select v-model="listQuery.hasReturnApply" class="input-width" placeholder="全部" clearable>
              <el-option v-for="item in returnApplyOptions"
                         :key="item.value"
                         :label="item.label"
                         :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="发货门店：">
            <el-select 
              v-model="listQuery.deliveryStoreId" 
              class="input-width" 
              placeholder="全部" 
              :clearable="!isStoreAdmin" 
              :disabled="isStoreAdmin"
              filterable>
              <el-option v-for="item in storeOptions"
                         :key="item.id"
                         :label="item.addressName"
                         :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="operate-container" shadow="never">
      <i class="el-icon-tickets"></i>
      <span>数据列表</span>
    </el-card>
    <div class="table-container" :class="{'mobile-card-view': isMobile}">
      <!-- 移动端卡片视图 -->
      <div v-if="isMobile" v-loading="listLoading">
        <div v-for="order in list" :key="order.id" class="order-card">
          <div class="order-card-header">
            <div>
              <div style="font-weight: bold; font-size: 16px;">订单 #{{order.orderSn}}</div>
              <div style="color: #999; font-size: 12px;">{{order.createTime | formatCreateTime}}</div>
            </div>
            <div>
              <el-tag :type="getStatusTagType(order.status)">{{order.status | formatStatus}}</el-tag>
            </div>
          </div>
          <div class="order-card-body">
            <div class="order-card-row">
              <span class="order-card-label">下单学校:</span>
              <span class="order-card-value">{{order.schoolName || '-'}}</span>
            </div>
            <div class="order-card-row">
              <span class="order-card-label">订单金额:</span>
              <span class="order-card-value" style="color: #f56c6c; font-weight: bold;">￥{{order.totalAmount}}</span>
            </div>
            <div class="order-card-row">
              <span class="order-card-label">支付方式:</span>
              <span class="order-card-value">{{formatPayTypeForOrder(order)}}</span>
            </div>
            <div class="order-card-row">
              <span class="order-card-label">收货人:</span>
              <span class="order-card-value">{{order.receiverName}} {{order.receiverPhone}}</span>
            </div>
            <div class="order-card-row" v-if="order.deliveryCompany || order.deliverySn">
              <span class="order-card-label">物流信息:</span>
              <span class="order-card-value">{{order.deliveryCompany}} {{order.deliverySn}}</span>
            </div>
            <div class="order-card-row">
              <span class="order-card-label">售后申请:</span>
              <span class="order-card-value">
                <el-tag v-if="order.returnApplyCount > 0" type="warning" size="mini">
                  {{order.returnApplyCount}}个申请
                </el-tag>
                <span v-else style="color: #C0C4CC;">无售后</span>
              </span>
            </div>
          </div>
          <div class="order-card-actions">
            <el-button size="mini" @click="handleViewOrder(order.id)">查看</el-button>
            <el-button v-if="order.status===1" size="mini" type="primary" @click="handleDeliveryOrder(order.id)">发货</el-button>
            <el-button v-if="order.deliveryCompany && order.deliverySn" size="mini" type="success" @click="handleViewLogistics(order.id)">物流</el-button>
            <el-button v-if="order.returnApplyCount > 0" size="mini" type="warning" @click="handleViewReturnApply(order)">售后</el-button>
            <el-button v-if="isAdmin && order.sourceType===2 && order.status===3" size="mini" type="warning" @click="handleRefundOrder(null, order)">退款</el-button>
          </div>
        </div>
      </div>

      <!-- 桌面端表格视图 -->
      <el-table v-if="!isMobile" ref="orderTable"
                :data="list"
                style="width: 100%;"
                @selection-change="handleSelectionChange"
                v-loading="listLoading" border>
        <el-table-column type="expand" width="50" align="center">
          <template slot-scope="props">
            <div class="order-detail-expand">
              <h4>订单商品信息</h4>
              <el-table :data="props.row.orderItemList" border style="margin: 10px 0;">
                <el-table-column label="商品图片" width="80" align="center">
                  <template slot-scope="scope">
                    <img v-if="scope.row.productPic" :src="scope.row.productPic" style="width: 50px; height: 50px;">
                  </template>
                </el-table-column>
                <el-table-column label="商品名称" prop="productName" min-width="200"></el-table-column>
                <el-table-column label="商品规格" width="150">
                  <template slot-scope="scope">
                    <span>{{ formatProductAttr(scope.row.productAttr) }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="单价" width="100" align="center">
                  <template slot-scope="scope">￥{{scope.row.productPrice}}</template>
                </el-table-column>
                <el-table-column label="数量" prop="productQuantity" width="80" align="center"></el-table-column>
                <el-table-column label="小计" width="100" align="center">
                  <template slot-scope="scope">￥{{(scope.row.productPrice * scope.row.productQuantity).toFixed(2)}}</template>
                </el-table-column>
              </el-table>
            </div>
          </template>
        </el-table-column>
        <el-table-column type="selection" width="60" align="center"></el-table-column>
        <el-table-column label="编号" width="80" align="center">
          <template slot-scope="scope">{{scope.row.id}}</template>
        </el-table-column>
        <el-table-column label="订单编号" width="180" align="center">
          <template slot-scope="scope">{{scope.row.orderSn}}</template>
        </el-table-column>
        <el-table-column label="提交时间" width="180" align="center">
          <template slot-scope="scope">{{scope.row.createTime | formatCreateTime}}</template>
        </el-table-column>
        <el-table-column label="下单学校" align="center">
          <template slot-scope="scope">{{scope.row.schoolName || '-'}}</template>
        </el-table-column>
        <el-table-column label="订单类型" width="120" align="center">
          <template slot-scope="scope">
            <el-tag :type="getOrderTypeTagType(scope.row.orderType)" size="small">
              {{scope.row.orderType | formatOrderType}}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="订单金额" width="140" align="center">
          <template slot-scope="scope">{{formatOrderAmount(scope.row)}}</template>
        </el-table-column>
        <el-table-column label="支付方式" width="120" align="center">
          <template slot-scope="scope">
            <el-tag :type="getPayTypeTagType(scope.row)" size="small">
              {{formatPayTypeForOrder(scope.row)}}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="订单来源" width="120" align="center">
          <template slot-scope="scope">
            <el-tag :type="getSourceTypeTagType(scope.row.sourceType)" size="small">
              {{scope.row.sourceType | formatSourceType}}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="取货方式" width="120" align="center">
          <template slot-scope="scope">
            <el-tag :type="getDeliveryMethodTagType(scope.row)" size="small">
              {{formatDeliveryMethod(scope.row)}}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="订单状态" width="120" align="center">
          <template slot-scope="scope">
            <el-tag :type="getStatusTagType(scope.row.status)" size="small">
              {{scope.row.status | formatStatus}}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="收货人信息" width="200" align="center">
          <template slot-scope="scope">
            <div v-if="scope.row.receiverName" class="receiver-info">
              <div class="receiver-name">
                {{scope.row.receiverName}}
                <el-button
                  type="text"
                  size="mini"
                  icon="el-icon-copy-document"
                  @click="copyReceiverInfo(scope.row)"
                  title="复制收货人信息">
                </el-button>
              </div>
              <div class="text-muted receiver-phone">{{scope.row.receiverPhone}}</div>
              <div class="text-muted receiver-address" style="font-size: 12px;">
                {{formatAddress(scope.row)}}
              </div>
            </div>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="物流信息" width="150" align="center">
          <template slot-scope="scope">
            <div v-if="scope.row.deliveryCompany || scope.row.deliverySn">
              <div v-if="scope.row.deliveryCompany">{{scope.row.deliveryCompany}}</div>
              <div v-if="scope.row.deliverySn" class="text-muted">{{scope.row.deliverySn}}</div>
            </div>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="售后申请" width="120" align="center">
          <template slot-scope="scope">
            <div v-if="scope.row.returnApplyCount > 0">
              <el-tag type="warning" size="small">
                {{scope.row.returnApplyCount}}个申请
              </el-tag>
              <div style="margin-top: 5px;">
                <el-button
                  type="text"
                  size="mini"
                  @click="handleViewReturnApply(scope.row)"
                  style="color: #409EFF;">
                  查看详情
                </el-button>
              </div>
            </div>
            <span v-else style="color: #C0C4CC;">无售后</span>
          </template>
        </el-table-column>
        <el-table-column label="是否礼物" width="80" align="center">
          <template slot-scope="scope">{{scope.row.isGift ? '是' : '否'}}</template>
        </el-table-column>
        <el-table-column label="操作" width="300" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              @click="handleViewOrder(scope.$index, scope.row)"
            >查看订单</el-button>
            <el-button
              size="mini"
              @click="handleCloseOrder(scope.$index, scope.row)"
              v-show="scope.row.status===0">关闭订单</el-button>
            <el-button
              size="mini"
              @click="handleDeliveryOrder(scope.$index, scope.row)"
              v-show="scope.row.deliveryType===0 && scope.row.status===1">订单发货</el-button>
            <!-- 核销订单按钮已隐藏 -->
            <!-- <el-button
              size="mini"
              type="success"
              @click="handlePickupOrder(scope.$index, scope.row)"
              v-show="scope.row.deliveryType===1 && scope.row.status>=1 && (scope.row.pickupStatus===null || scope.row.pickupStatus===0)">核销订单</el-button> -->
            <el-button
              size="mini"
              @click="handleViewLogistics(scope.$index, scope.row)"
              v-show="scope.row.deliveryType===0 && (scope.row.status===2||scope.row.status===3)">订单跟踪</el-button>
            <el-button
              size="mini"
              type="warning"
              @click="handleRefundOrder(scope.$index, scope.row)"
              v-show="isAdmin && scope.row.sourceType===2 && scope.row.status===3">退款</el-button>
            <el-button
              size="mini"
              type="danger"
              @click="handleDeleteOrder(scope.$index, scope.row)"
              v-show="scope.row.status===4">删除订单</el-button>
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
        @size-change="handleSizeChangeWithState"
        @current-change="handleCurrentChangeWithState"
        layout="total, sizes,prev, pager, next,jumper"
        :current-page.sync="listQuery.pageNum"
        :page-size="listQuery.pageSize"
        :page-sizes="[5,10,15]"
        :total="total">
      </el-pagination>
    </div>
    <el-dialog
      title="关闭订单"
      :visible.sync="closeOrder.dialogVisible" width="30%">
      <span style="vertical-align: top">操作备注：</span>
      <el-input
        style="width: 80%"
        type="textarea"
        :rows="5"
        placeholder="请输入内容"
        v-model="closeOrder.content">
      </el-input>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closeOrder.dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleCloseOrderConfirm">确 定</el-button>
      </span>
    </el-dialog>
    <logistics-dialog v-model="logisticsDialogVisible" :order-id="selectedOrderId"></logistics-dialog>
    <el-dialog
      title="核销订单"
      :visible.sync="pickupDialogVisible" width="30%">
      <el-form :model="pickupInfo"
               label-width="100px">
        <el-form-item label="核销码：">
          <el-input v-model="pickupInfo.pickupCode" placeholder="请输入6位数字核销码" maxlength="6"></el-input>
        </el-form-item>
        <el-form-item label="操作员：">
          <el-input v-model="pickupInfo.operator" placeholder="请输入操作员姓名"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="pickupDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handlePickupConfirm">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 批量发货弹窗 -->
    <el-dialog
      title="批量发货"
      :visible.sync="batchDeliveryDialogVisible"
      width="50%"
      :close-on-click-modal="false">
      <div class="batch-delivery-content">
        <div style="margin-bottom: 20px;">
          <el-alert
            title="批量发货说明"
            type="info"
            :closable="false"
            show-icon>
          </el-alert>
          <div style="margin-top: 10px; padding: 10px; background-color: #f4f4f5; border-radius: 4px; line-height: 1.6; color: #606266;">
            <p style="margin: 5px 0;">1. 点击"下载发货模板"，系统会自动生成包含所有待发货订单的Excel模板</p>
            <p style="margin: 5px 0;">2. 在模板中填写对应订单的物流公司和物流单号（订单号已自动填写）</p>
            <p style="margin: 5px 0;">3. 上传填写完成的Excel文件进行批量发货</p>
            <p style="margin: 5px 0;">4. 系统将自动验证订单状态，只有待发货状态的订单才能发货</p>
          </div>
        </div>

        <div style="margin: 20px 0;">
          <el-upload
            ref="batchDeliveryUpload"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
            :show-file-list="false"
            accept=".xlsx,.xls">
            <el-button type="primary" icon="el-icon-upload">上传发货文件</el-button>
          </el-upload>
          <div style="margin-top: 10px; color: #999; font-size: 12px;">
            支持.xlsx、.xls格式文件，文件大小不超过10MB
          </div>
        </div>

        <!-- 上传结果展示 -->
        <div v-if="batchDeliveryResult" style="margin-top: 20px;">
          <h4>处理结果</h4>
          <el-alert
            :title="`成功发货 ${batchDeliveryResult.successCount} 个订单，失败 ${batchDeliveryResult.failCount} 个订单`"
            :type="batchDeliveryResult.failCount > 0 ? 'warning' : 'success'"
            :closable="false"
            show-icon>
          </el-alert>

          <div v-if="batchDeliveryResult.failList && batchDeliveryResult.failList.length > 0" style="margin-top: 15px;">
            <h5>失败订单列表：</h5>
            <el-table :data="batchDeliveryResult.failList" border size="small">
              <el-table-column prop="orderSn" label="订单号" width="180"></el-table-column>
              <el-table-column prop="reason" label="失败原因"></el-table-column>
            </el-table>
          </div>
        </div>
      </div>

      <span slot="footer" class="dialog-footer">
        <el-button @click="closeBatchDeliveryDialog">关 闭</el-button>
      </span>
    </el-dialog>

    <!-- 移动端时间选择器弹窗 -->
    <van-popup
      v-model="showMobileDatePicker"
      position="bottom"
      :style="{ height: '60%' }"
      round
      closeable
      close-icon-position="top-right">
      <div class="mobile-date-picker-container">
        <div class="mobile-date-header">
          <h3>选择时间范围</h3>
        </div>

        <!-- 快捷选择按钮 -->
        <div class="date-shortcuts">
          <div class="shortcut-row">
            <button
              class="shortcut-btn"
              :class="{ active: selectedShortcut === 'today' }"
              @click="selectQuickDate('today')">
              今天
            </button>
            <button
              class="shortcut-btn"
              :class="{ active: selectedShortcut === 'week' }"
              @click="selectQuickDate('week')">
              最近一周
            </button>
            <button
              class="shortcut-btn"
              :class="{ active: selectedShortcut === 'month' }"
              @click="selectQuickDate('month')">
              最近一月
            </button>
          </div>
        </div>

        <!-- 自定义日期选择 -->
        <div class="custom-date-section">
          <div class="date-item">
            <label>开始日期</label>
            <div class="date-input" @click="showStartDatePicker = true">
              {{ mobileStartDate || '请选择开始日期' }}
            </div>
          </div>
          <div class="date-item">
            <label>结束日期</label>
            <div class="date-input" @click="showEndDatePicker = true">
              {{ mobileEndDate || '请选择结束日期' }}
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="mobile-date-actions">
          <button class="cancel-btn" @click="cancelMobileDatePicker">取消</button>
          <button class="confirm-btn" @click="confirmMobileDateRange">确定</button>
        </div>
      </div>
    </van-popup>

    <!-- 开始日期选择器 -->
    <van-popup v-model="showStartDatePicker" position="bottom">
      <van-datetime-picker
        v-model="startDateValue"
        type="date"
        title="选择开始日期"
        @confirm="confirmStartDate"
        @cancel="showStartDatePicker = false"
      />
    </van-popup>

    <!-- 结束日期选择器 -->
    <van-popup v-model="showEndDatePicker" position="bottom">
      <van-datetime-picker
        v-model="endDateValue"
        type="date"
        title="选择结束日期"
        @confirm="confirmEndDate"
        @cancel="showEndDatePicker = false"
      />
    </van-popup>

    <!-- 售后申请选择对话框 -->
    <el-dialog
      title="选择售后申请"
      :visible.sync="returnApplySelectDialog.visible"
      width="600px"
      @close="handleCloseReturnApplyDialog">
      <div style="margin-bottom: 15px;">
        <span style="color: #666;">订单编号：{{ returnApplySelectDialog.orderSn }}</span>
      </div>
      <el-table
        :data="returnApplySelectDialog.returnApplyList"
        style="width: 100%">
        <el-table-column prop="id" label="申请ID" width="80"></el-table-column>
        <el-table-column prop="productName" label="商品名称" show-overflow-tooltip></el-table-column>
        <el-table-column prop="returnAmount" label="退款金额" width="100">
          <template slot-scope="scope">
            ¥{{ scope.row.returnAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="getReturnApplyStatusType(scope.row.status)">
              {{ getReturnApplyStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="150">
          <template slot-scope="scope">
            {{ scope.row.createTime | formatCreateTime }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template slot-scope="scope">
            <el-button
              type="primary"
              size="mini"
              @click="handleSelectReturnApply(scope.row)">
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button @click="handleCloseReturnApplyDialog">取消</el-button>
      </div>
    </el-dialog>

    <!-- 退款弹窗 -->
    <el-dialog title="订单退款"
               :visible.sync="refundDialogVisible"
               width="500px"
               :close-on-click-modal="false">
      <div class="refund-dialog-content">
        <el-alert
          title="退款说明"
          type="warning"
          :closable="false"
          show-icon
          style="margin-bottom: 20px;">
          <template slot="default">
            退款将按照用户支付渠道原路返回（微信/支付宝/余额）
          </template>
        </el-alert>
        
        <el-form :model="refundInfo" label-width="100px">
          <el-form-item label="订单编号：">
            <span>{{refundInfo.orderSn}}</span>
          </el-form-item>
          <el-form-item label="订单金额：">
            <span style="color: #f56c6c; font-weight: bold;">¥{{refundInfo.payAmount}}</span>
          </el-form-item>
          <el-form-item label="支付方式：">
            <el-tag :type="refundInfo.payType === 2 ? 'success' : (refundInfo.payType === 1 ? 'primary' : 'warning')">
              {{getPayTypeText(refundInfo.payType)}}
            </el-tag>
          </el-form-item>
          <el-form-item label="退款金额：" required>
            <el-input-number
              v-model="refundInfo.refundAmount"
              :min="0.01"
              :max="refundInfo.payAmount"
              :precision="2"
              :step="0.01"
              style="width: 200px;">
            </el-input-number>
            <span style="margin-left: 10px; color: #909399;">元</span>
          </el-form-item>
          <el-form-item label="退款原因：">
            <el-input
              type="textarea"
              v-model="refundInfo.refundReason"
              :rows="3"
              placeholder="请输入退款原因（选填）"
              maxlength="200"
              show-word-limit>
            </el-input>
          </el-form-item>
          <el-form-item label="操作人：">
            <el-input v-model="refundInfo.operator" placeholder="请输入操作人姓名" style="width: 200px;"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="refundDialogVisible = false">取 消</el-button>
        <el-button type="warning" @click="handleRefundConfirm" :loading="refundLoading">确认退款</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
  import {fetchList,closeOrder,deleteOrder,pickupOrder,exportOrder,refundOrder} from '@/api/order'
  import {formatDate} from '@/utils/date';
  import LogisticsDialog from '@/views/oms/order/components/logisticsDialog';
  import {getReturnApplyByOrderId} from '@/api/returnApply';
  import {getAllStores} from '@/api/storeAddress';
  import { mapState } from 'vuex';
  const defaultListQuery = {
    pageNum: 1,
    pageSize: 10,
    orderSn: null,
    receiverKeyword: null,
    status: null,
    orderType: null,
    sourceType: null,
    startTime: null,
    endTime: null,
    deliveryType: null,
    payType: null,
    isGift: null,
    productName: null,
    hasReturnApply: null,
    schoolId: null,
    deliveryStoreId: null
  };
  import listStateMixin from '@/mixins/listStateMixin'

  export default {
    name: "orderList",
    mixins: [listStateMixin],
    components:{LogisticsDialog},
    data() {
      return {
        // 门店筛选初始化标记
        storeFilterInitialized: false,
        // 设置页面标识用于状态管理
        pageKey: 'orderList',
        listQuery: Object.assign({}, defaultListQuery),
        listLoading: true,
        list: null,
        total: null,
        selectedOrderId: null, // 存储选中的订单ID
        operateType: null,
        multipleSelection: [],
        dateRange: null, // 日期范围选择器
        isMobile: false, // 是否为移动端
        closeOrder:{
          dialogVisible:false,
          content:null,
          orderIds:[]
        },
        statusOptions: [
          {
            label: '待付款',
            value: 0
          },
          {
            label: '待发货',
            value: 1
          },
          {
            label: '已发货',
            value: 2
          },
          {
            label: '已完成',
            value: 3
          },
          {
            label: '已关闭',
            value: 4
          }
        ],
        orderTypeOptions: [
          {
            label: '正常订单',
            value: 0
          },
          {
            label: '秒杀订单',
            value: 1
          },
          {
            label: '积分兑换订单',
            value: 2
          }
        ],
        sourceTypeOptions: [
          {
            label: 'PC订单',
            value: 0
          },
          {
            label: '小程序订单',
            value: 1
          },
          {
            label: '自助设备订单',
            value: 2
          }
        ],
        deliveryTypeOptions: [
          {
            label: '快递配送',
            value: 0
          },
          {
            label: '到店自提',
            value: 1
          }
        ],
        payTypeOptions: [
          {
            label: '未支付',
            value: 0
          },
          {
            label: '支付宝',
            value: 1
          },
          {
            label: '微信',
            value: 2
          },
          {
            label: '余额支付',
            value: 3
          },
          {
            label: '积分支付',
            value: 4
          }
        ],
        isGiftOptions: [
          {
            label: '是',
            value: true
          },
          {
            label: '否',
            value: false
          }
        ],
        returnApplyOptions: [
          {
            label: '有售后申请',
            value: true
          },
          {
            label: '无售后申请',
            value: false
          }
        ],
        operateOptions: [
          {
            label: "批量发货",
            value: 1
          },
          {
            label: "关闭订单",
            value: 2
          },
          {
            label: "删除订单",
            value: 3
          }
        ],
        logisticsDialogVisible:false,
        pickupDialogVisible:false,
        pickupInfo:{
          pickupCode:'',
          operator:'后台管理员',
          orderId:null
        },
        batchDeliveryDialogVisible: false, // 批量发货弹窗
        batchDeliveryResult: null, // 批量发货结果
        uploadUrl: process.env.BASE_API + '/order/batchDelivery', // 上传地址
        uploadHeaders: {
          'Authorization': 'Bearer ' + (localStorage.getItem('token') || '')
        },
        showMobileDatePicker: false, // 移动端时间选择器显示状态
        showStartDatePicker: false, // 开始日期选择器
        showEndDatePicker: false, // 结束日期选择器
        mobileStartDate: '', // 移动端开始日期
        mobileEndDate: '', // 移动端结束日期
        startDateValue: new Date(), // Vant日期选择器的开始日期值
        endDateValue: new Date(), // Vant日期选择器的结束日期值
        selectedShortcut: '', // 当前选中的快捷选项
        returnApplySelectDialog: {
          visible: false,
          returnApplyList: [],
          orderId: null,
          orderSn: ''
        },
        storeOptions: [], // 门店列表选项
        // 退款相关
        refundDialogVisible: false,
        refundInfo: {
          orderId: null,
          orderSn: '',
          payAmount: 0,
          payType: null,
          refundAmount: 0,
          refundReason: '',
          operator: '后台管理员'
        },
        refundLoading: false
      }
    },
    computed: {
      // 从 Vuex user 模块获取状态
      adminType() {
        return this.$store.state.user.adminType
      },
      currentSchoolId() {
        return this.$store.state.user.schoolId
      },
      currentStoreId() {
        return this.$store.state.user.storeId
      },
      // 是否为门店账号
      isStoreAdmin() {
        return this.adminType === true
      },
      // 是否为管理员（用于退款按钮权限控制）
      isAdmin() {
        const roles = this.$store.state.user.roles
        if (roles && roles.length > 0) {
          return roles.some(role => 
            role === 'admin' || 
            role === '管理员' || 
            role === 'super_admin' || 
            role === '超级管理员'
          )
        }
        return false
      }
    },
    watch: {
      // 监听 adminType 变化，确保 Vuex state 加载后正确设置筛选条件
      adminType: {
        handler(val) {
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
      // 检查路由参数，如果有status参数则自动设置筛选条件
      if (this.$route.query.status !== undefined) {
        this.listQuery.status = parseInt(this.$route.query.status);
        const statusText = ['待付款', '待发货', '已发货', '已完成', '已关闭'][this.listQuery.status];
        this.$message.success(`已自动筛选${statusText}订单`);
      }
      this.checkMobile();

      // 如果是门店账号，自动设置学校和发货门店筛选条件
      this.initStoreFilter()

      // 获取门店列表
      this.getStoreList();

      // 如果没有保存的状态，则正常加载数据
      if (!this.$store.getters['listState/hasListState'](this.pageKey)) {
        this.getList();
      }
    },
    filters: {
      formatCreateTime(time) {
        let date = new Date(time);
        return formatDate(date, 'yyyy-MM-dd hh:mm:ss')
      },
      formatPayType(value) {
        if (value === 1) {
          return '支付宝';
        } else if (value === 2) {
          return '微信';
        } else if (value === 3) {
          return '余额支付';
        } else if (value === 4) {
          return '积分支付';
        } else {
          return '未支付';
        }
      },
      formatSourceType(value) {
        if (value === 1) {
          return '小程序订单';
        } else if (value === 2) {
          return '自助设备订单';
        } else {
          return 'PC订单';
        }
      },
      formatDeliveryType(value) {
        if (value === 0) {
          return '快递配送';
        } else if (value === 1) {
          return '门店自取';
        }
      },

      formatStatus(value) {
        if (value === 1) {
          return '待发货';
        } else if (value === 2) {
          return '已发货';
        } else if (value === 3) {
          return '已完成';
        } else if (value === 4) {
          return '已关闭';
        } else if (value === 5) {
          return '无效订单';
        } else {
          return '待付款';
        }
      },
      formatOrderType(value) {
        if (value === 1) {
          return '秒杀订单';
        } else if (value === 2) {
          return '积分兑换订单';
        } else {
          return '正常订单';
        }
      },
    },
    methods: {
      // 初始化门店筛选条件（门店账号专用）
      initStoreFilter() {
        console.log('Order initStoreFilter called, isStoreAdmin:', this.isStoreAdmin, 'currentSchoolId:', this.currentSchoolId, 'currentStoreId:', this.currentStoreId, 'storeFilterInitialized:', this.storeFilterInitialized)
        if (this.isStoreAdmin && !this.storeFilterInitialized) {
          if (this.currentSchoolId) {
            this.listQuery.schoolId = this.currentSchoolId
            console.log('Order: Set schoolId to:', this.currentSchoolId)
          }
          if (this.currentStoreId) {
            this.listQuery.deliveryStoreId = this.currentStoreId
            console.log('Order: Set deliveryStoreId to:', this.currentStoreId)
          }
          this.storeFilterInitialized = true
          // 重新加载列表数据
          this.getList()
        }
      },
      handleResetSearch() {
        this.listQuery = Object.assign({}, defaultListQuery);
        this.dateRange = null;
        // 如果是门店账号，保留学校和发货门店筛选条件
        if (this.isStoreAdmin) {
          if (this.currentSchoolId) {
            this.listQuery.schoolId = this.currentSchoolId
          }
          if (this.currentStoreId) {
            this.listQuery.deliveryStoreId = this.currentStoreId
          }
        }
      },
      handleDateChange(dateRange) {
        if (dateRange && dateRange.length === 2) {
          this.listQuery.startTime = dateRange[0];
          this.listQuery.endTime = dateRange[1];
        } else {
          this.listQuery.startTime = null;
          this.listQuery.endTime = null;
        }
      },
      checkMobile() {
        // 检测是否为移动端
        const userAgent = navigator.userAgent;
        this.isMobile = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(userAgent);
      },
      formatAddress(row) {
        // 格式化收货地址
        if (!row.receiverProvince && !row.receiverCity && !row.receiverRegion && !row.receiverDetailAddress) {
          return '-';
        }
        const parts = [
          row.receiverProvince,
          row.receiverCity,
          row.receiverRegion,
          row.receiverDetailAddress
        ].filter(part => part && part.trim());

        const fullAddress = parts.join('');
        // 移动端显示简化地址
        if (this.isMobile && fullAddress.length > 20) {
          return fullAddress.substring(0, 20) + '...';
        }
        return fullAddress;
      },
      // 判断是否为积分兑换订单
      isPointsExchangeOrder(order) {
        return order.orderType === 2;
      },
      // 格式化订单金额显示
      formatOrderAmount(order) {
        if (this.isPointsExchangeOrder(order)) {
          // 积分兑换订单：显示 "积分 + 支付金额" 格式
          const pointsUsed = order.useIntegration || 0;
          const cashAmount = parseFloat(order.payAmount || 0).toFixed(2);

          if (pointsUsed > 0 && parseFloat(cashAmount) > 0) {
            return `${pointsUsed}积分 + ¥${cashAmount}`;
          } else if (pointsUsed > 0) {
            return `${pointsUsed}积分`;
          } else {
            return `¥${cashAmount}`;
          }
        } else {
          // 普通订单：只显示支付金额
          return `¥${parseFloat(order.totalAmount || 0).toFixed(2)}`;
        }
      },
      // 获取订单类型标签颜色
      getOrderTypeTagType(orderType) {
        if (orderType === 1) {
          return 'warning'; // 秒杀订单 - 橙色
        } else if (orderType === 2) {
          return 'success'; // 积分兑换订单 - 绿色
        } else {
          return ''; // 正常订单 - 默认色
        }
      },
      // 格式化商品规格属性
      formatProductAttr(productAttr) {
        if (!productAttr) return '-';
        try {
          // 尝试解析JSON格式的规格数据
          const attrs = JSON.parse(productAttr);
          if (Array.isArray(attrs)) {
            // 格式: [{"key":"颜色","value":"红色"},{"key":"尺寸","value":"L"}]
            return attrs.map(attr => `${attr.key}:${attr.value}`).filter(Boolean).join('; ');
          } else if (typeof attrs === 'object') {
            // 格式: {"key":"规格","value":"3XL"}
            if (attrs.key && attrs.value) {
              return `${attrs.key}:${attrs.value}`;
            }
            return attrs.value || attrs.v || productAttr;
          }
          return productAttr;
        } catch (e) {
          // 非JSON格式，直接返回原值
          return productAttr;
        }
      },
      handleSearchList() {
        this.listQuery.pageNum = 1;
        this.getList();
      },
      handleSelectionChange(val){
        this.multipleSelection = val;
      },
      handleViewOrder(indexOrId, row){
        const orderId = row ? row.id : indexOrId;
        this.$router.push({path:'/oms/orderDetail',query:{id:orderId}})
      },

      handleCloseOrder(indexOrId, row){
        const orderId = row ? row.id : indexOrId;
        this.closeOrder.dialogVisible=true;
        this.closeOrder.orderIds=[orderId];
      },
      handleDeliveryOrder(indexOrId, row){
        const order = row || this.list.find(item => item.id === indexOrId);
        let listItem = this.covertOrder(order);
        this.$router.push({path:'/oms/deliverOrderList',query:{list:[listItem]}})
      },
      handleViewLogistics(indexOrId, row){
        const orderId = row ? row.id : indexOrId;
        this.selectedOrderId = orderId; // 获取订单ID
        this.logisticsDialogVisible=true;
      },
      handleDeleteOrder(indexOrId, row){
        const orderId = row ? row.id : indexOrId;
        let ids=[];
        ids.push(orderId);
        this.deleteOrder(ids);
      },
      handlePickupOrder(indexOrId, row){
        const orderId = row ? row.id : indexOrId;
        this.pickupDialogVisible=true;
        this.pickupInfo.orderId=orderId;
        this.pickupInfo.pickupCode='';
      },
      handleCompleteOrder(orderId) {
        this.$confirm('确认完成该订单?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          // 这里应该调用完成订单的API
          this.$message.success('订单已完成');
          this.getList();
        });
      },
      async handleViewReturnApply(order) {
        try {
          // 获取该订单的售后申请列表
          const response = await getReturnApplyByOrderId(order.id);
          const returnApplyList = response.data;

          if (!returnApplyList || returnApplyList.length === 0) {
            this.$message.warning('该订单暂无售后申请');
            return;
          }

          if (returnApplyList.length === 1) {
            // 只有一个售后申请，直接跳转到详情页
            this.$router.push({
              path: '/oms/returnApplyDetail',
              query: {
                id: returnApplyList[0].id
              }
            });
          } else {
            // 有多个售后申请，显示选择对话框
            this.returnApplySelectDialog.visible = true;
            this.returnApplySelectDialog.returnApplyList = returnApplyList;
            this.returnApplySelectDialog.orderId = order.id;
            this.returnApplySelectDialog.orderSn = order.orderSn;
          }
        } catch (error) {
          console.error('获取售后申请列表失败:', error);
          this.$message.error('获取售后申请列表失败');
        }
      },
      // 选择售后申请并跳转到详情页
      handleSelectReturnApply(returnApply) {
        this.returnApplySelectDialog.visible = false;
        this.$router.push({
          path: '/oms/returnApplyDetail',
          query: {
            id: returnApply.id
          }
        });
      },
      // 关闭售后申请选择对话框
      handleCloseReturnApplyDialog() {
        this.returnApplySelectDialog.visible = false;
        this.returnApplySelectDialog.returnApplyList = [];
        this.returnApplySelectDialog.orderId = null;
        this.returnApplySelectDialog.orderSn = '';
      },
      // 获取售后申请状态文本
      getReturnApplyStatusText(status) {
        const statusMap = {
          0: '待处理',
          1: '退货中',
          2: '已完成',
          3: '已拒绝'
        };
        return statusMap[status] || '未知';
      },
      // 获取售后申请状态标签类型
      getReturnApplyStatusType(status) {
        const typeMap = {
          0: 'warning',  // 待处理
          1: 'primary',  // 退货中
          2: 'success',  // 已完成
          3: 'danger'    // 已拒绝
        };
        return typeMap[status] || 'info';
      },
      getStatusTagType(status) {
        const statusMap = {
          0: 'info',     // 待付款
          1: 'warning',  // 待发货
          2: 'primary',  // 已发货
          3: 'success',  // 已完成
          4: 'danger'    // 已关闭
        };
        return statusMap[status] || 'info';
      },
      handleBatchOperate(){
        if(this.multipleSelection==null||this.multipleSelection.length<1){
          this.$message({
            message: '请选择要操作的订单',
            type: 'warning',
            duration: 1000
          });
          return;
        }
        if(this.operateType===1){
          //批量发货
          let list=[];
          for(let i=0;i<this.multipleSelection.length;i++){
            if(this.multipleSelection[i].status===1){
              list.push(this.covertOrder(this.multipleSelection[i]));
            }
          }
          if(list.length===0){
            this.$message({
              message: '选中订单中没有可以发货的订单',
              type: 'warning',
              duration: 1000
            });
            return;
          }
          this.$router.push({path:'/oms/deliverOrderList',query:{list:list}})
        }else if(this.operateType===2){
          //关闭订单
          this.closeOrder.orderIds=[];
          for(let i=0;i<this.multipleSelection.length;i++){
            this.closeOrder.orderIds.push(this.multipleSelection[i].id);
          }
          this.closeOrder.dialogVisible=true;
        }else if(this.operateType===3){
          //删除订单
          let ids=[];
          for(let i=0;i<this.multipleSelection.length;i++){
            ids.push(this.multipleSelection[i].id);
          }
          this.deleteOrder(ids);
        }
      },
      handleSizeChange(val){
        this.listQuery.pageNum = 1;
        this.listQuery.pageSize = val;
        this.getList();
      },
      handleCurrentChange(val){
        this.listQuery.pageNum = val;
        this.getList();
      },
      handleCloseOrderConfirm() {
        if (this.closeOrder.content == null || this.closeOrder.content === '') {
          this.$message({
            message: '操作备注不能为空',
            type: 'warning',
            duration: 1000
          });
          return;
        }
        let params = new URLSearchParams();
        params.append('ids', this.closeOrder.orderIds);
        params.append('note', this.closeOrder.content);
        closeOrder(params).then(response=>{
          this.closeOrder.orderIds=[];
          this.closeOrder.dialogVisible=false;
          this.getList();
          this.$message({
            message: '修改成功',
            type: 'success',
            duration: 1000
          });
        });
      },
      getList() {
        this.listLoading = true;
        fetchList(this.listQuery).then(response => {
          this.listLoading = false;
          this.list = response.data.list;
          this.total = response.data.total;
        });
      },
      getStoreList() {
        getAllStores().then(response => {
          this.storeOptions = response.data || [];
          // 门店列表加载完成后，如果是门店账号，确保发货门店筛选值正确显示
          if (this.isStoreAdmin && this.currentStoreId) {
            this.$nextTick(() => {
              this.listQuery.deliveryStoreId = this.currentStoreId
            })
          }
        }).catch(() => {
          this.storeOptions = [];
        });
      },
      deleteOrder(ids){
        this.$confirm('是否要进行该删除操作?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          let params = new URLSearchParams();
          params.append("ids",ids);
          deleteOrder(params).then(response=>{
            this.$message({
              message: '删除成功！',
              type: 'success',
              duration: 1000
            });
            this.getList();
          });
        })
      },
      handlePickupConfirm(){
        if(this.pickupInfo.pickupCode==null||this.pickupInfo.pickupCode===''){
          this.$message({
            message: '请输入核销码',
            type: 'warning',
            duration: 1000
          });
          return;
        }
        let params = new URLSearchParams();
        params.append('pickupCode', this.pickupInfo.pickupCode);
        params.append('operator', this.pickupInfo.operator);
        pickupOrder(params).then(response=>{
          this.pickupDialogVisible=false;
          this.pickupInfo.pickupCode='';
          this.$message({
            message: '核销成功',
            type: 'success',
            duration: 1000
          });
          this.getList();
        }).catch(error=>{
          this.$message({
            message: (error.response && error.response.data && error.response.data.message) || '核销失败',
            type: 'error',
            duration: 2000
          });
        });
      },
      covertOrder(order){
        let address=order.receiverProvince+order.receiverCity+order.receiverRegion+order.receiverDetailAddress;
        let listItem={
          orderId:order.id,
          orderSn:order.orderSn,
          receiverName:order.receiverName,
          receiverPhone:order.receiverPhone,
          receiverPostCode:order.receiverPostCode,
          address:address,
          deliveryCompany:null,
          deliverySn:null
        };
        return listItem;
      },
      handleExport() {
        this.$confirm('确认导出当前筛选条件下的订单数据？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$message({
            message: '正在导出，请稍候...',
            type: 'info'
          });
          exportOrder(this.listQuery).then(response => {
            const disposition = response.headers['content-disposition'];
            let fileName = 'order_export_' + Date.now() + '.csv';
            if (disposition && disposition.includes('filename=')) {
              fileName = decodeURIComponent(disposition.split('filename=')[1]);
            }
            const blob = new Blob([response.data], { type: 'text/csv;charset=utf-8' });
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;
            link.download = fileName;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            window.URL.revokeObjectURL(url);
          });
        });
      },
      // 下载发货模板
      handleDownloadTemplate() {
        this.$message({
          message: '正在生成模板，请稍候...',
          type: 'info'
        });

        // 调用后端接口下载模板
        const url = process.env.BASE_API + '/order/deliveryTemplate';
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', '批量发货模板.xlsx');

        // 添加认证头
        fetch(url, {
          method: 'GET',
          headers: {
            'Authorization': 'Bearer ' + (localStorage.getItem('token') || '')
          }
        }).then(response => {
          if (response.ok) {
            return response.blob();
          }
          throw new Error('下载失败');
        }).then(blob => {
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          link.download = '批量发货模板.xlsx';
          document.body.appendChild(link);
          link.click();
          document.body.removeChild(link);
          window.URL.revokeObjectURL(url);
          this.$message.success('模板下载成功');
        }).catch(error => {
          console.error('下载模板失败:', error);
          this.$message.error('模板下载失败，请重试');
        });
      },
      // 批量发货
      handleBatchDelivery() {
        this.batchDeliveryDialogVisible = true;
        this.batchDeliveryResult = null;
      },
      // 关闭批量发货弹窗
      closeBatchDeliveryDialog() {
        this.batchDeliveryDialogVisible = false;
        this.batchDeliveryResult = null;
        this.getList(); // 刷新列表
      },
      // 上传前验证
      beforeUpload(file) {
        const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' ||
                       file.type === 'application/vnd.ms-excel';
        const isLt10M = file.size / 1024 / 1024 < 10;

        if (!isExcel) {
          this.$message.error('只能上传Excel文件!');
          return false;
        }
        if (!isLt10M) {
          this.$message.error('文件大小不能超过10MB!');
          return false;
        }

        this.$message.info('正在上传文件，请稍候...');
        return true;
      },
      // 上传成功
      handleUploadSuccess(response) {
        if (response.code === 200) {
          this.batchDeliveryResult = response.data;
          this.$message.success('批量发货处理完成');
        } else {
          this.$message.error(response.message || '批量发货失败');
        }
      },
      // 上传失败
      handleUploadError(error) {
        console.error('上传失败:', error);
        this.$message.error('文件上传失败，请重试');
      },
      // 选择快捷日期
      selectQuickDate(type) {
        this.selectedShortcut = type;
        const end = new Date();
        const start = new Date();

        switch(type) {
          case 'today':
            start.setHours(0, 0, 0, 0);
            end.setHours(23, 59, 59, 999);
            break;
          case 'week':
            start.setTime(start.getTime() - 7 * 24 * 60 * 60 * 1000);
            break;
          case 'month':
            start.setTime(start.getTime() - 30 * 24 * 60 * 60 * 1000);
            break;
        }

        this.mobileStartDate = this.formatDate(start);
        this.mobileEndDate = this.formatDate(end);
      },

      // 确认开始日期
      confirmStartDate(value) {
        this.mobileStartDate = this.formatDate(value);
        this.showStartDatePicker = false;
        this.selectedShortcut = ''; // 清除快捷选择
      },

      // 确认结束日期
      confirmEndDate(value) {
        this.mobileEndDate = this.formatDate(value);
        this.showEndDatePicker = false;
        this.selectedShortcut = ''; // 清除快捷选择
      },

      // 取消移动端日期选择
      cancelMobileDatePicker() {
        this.showMobileDatePicker = false;
        this.selectedShortcut = '';
        // 恢复原来的值
        if (this.dateRange && this.dateRange.length === 2) {
          this.mobileStartDate = this.dateRange[0];
          this.mobileEndDate = this.dateRange[1];
        } else {
          this.mobileStartDate = '';
          this.mobileEndDate = '';
        }
      },

      // 确认移动端时间范围选择
      confirmMobileDateRange() {
        if (this.mobileStartDate && this.mobileEndDate) {
          this.dateRange = [this.mobileStartDate, this.mobileEndDate];
          this.handleDateChange([this.mobileStartDate, this.mobileEndDate]);
        } else {
          this.dateRange = [];
          this.handleDateChange([]);
        }
        this.showMobileDatePicker = false;
      },

      // 格式化日期
      formatDate(date) {
        if (!date) return '';
        const d = new Date(date);
        const year = d.getFullYear();
        const month = String(d.getMonth() + 1).padStart(2, '0');
        const day = String(d.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
      },
      // 获取订单状态标签类型
      getStatusTagType(status) {
        const statusMap = {
          0: 'warning', // 待付款
          1: 'primary', // 待发货
          2: 'success', // 已发货
          3: 'success', // 已完成
          4: 'danger',  // 已关闭
          5: 'info'     // 无效订单
        };
        return statusMap[status] || 'info';
      },
      // 获取支付方式标签类型
      getPayTypeTagType(order) {
        // 积分兑换订单特殊处理
        if (order.orderType === 2) {
          if (order.payAmount === 0) {
            return 'warning'; // 纯积分兑换
          } else {
            // 积分+现金兑换，根据实际支付方式显示
            const payTypeMap = {
              0: 'info',    // 未支付
              1: 'primary', // 支付宝
              2: 'success', // 微信
              3: 'warning', // 余额支付
              4: 'warning'  // 积分支付
            };
            return payTypeMap[order.payType] || 'info';
          }
        }

        // 普通订单
        const payTypeMap = {
          0: 'info',    // 未支付
          1: 'primary', // 支付宝
          2: 'success', // 微信
          3: 'warning', // 余额支付
          4: 'warning'  // 积分支付
        };
        return payTypeMap[order.payType] || 'info';
      },
      // 格式化订单支付方式显示
      formatPayTypeForOrder(order) {
        // 积分兑换订单特殊处理
        if (order.orderType === 2) {
          if (order.payAmount === 0) {
            return '积分支付';
          } else {
            // 积分+现金兑换，显示现金支付方式
            if (order.payType === 1) {
              return '积分+支付宝';
            } else if (order.payType === 2) {
              return '积分+微信';
            } else if (order.payType === 3) {
              return '积分+余额';
            } else {
              return '积分+现金';
            }
          }
        }

        // 普通订单使用原有逻辑
        return this.$options.filters.formatPayType(order.payType);
      },
      // 获取订单来源标签类型
      getSourceTypeTagType(sourceType) {
        const sourceTypeMap = {
          0: 'primary', // PC订单
          1: 'success', // 小程序订单
          2: 'warning'  // 自助设备订单
        };
        return sourceTypeMap[sourceType] || 'info';
      },
      // 获取取货方式标签类型
      getDeliveryTypeTagType(deliveryType) {
        const deliveryTypeMap = {
          0: 'primary', // 快递配送
          1: 'success'  // 门店自取
        };
        return deliveryTypeMap[deliveryType] || 'info';
      },
      // 新的发货方式标签类型方法（支持deliveryMethod字段）
      getDeliveryMethodTagType(row) {
        // 优先使用新的deliveryMethod字段
        if (row.deliveryMethod !== null && row.deliveryMethod !== undefined) {
          const deliveryMethodMap = {
            1: 'primary', // 快递配送
            2: 'success', // 门店自提
            3: 'warning'  // 虚拟发货
          };
          return deliveryMethodMap[row.deliveryMethod] || 'info';
        }
        // 降级使用旧的deliveryType字段
        else if (row.deliveryType !== null && row.deliveryType !== undefined) {
          const deliveryTypeMap = {
            0: 'primary', // 快递配送
            1: 'success'  // 门店自取
          };
          return deliveryTypeMap[row.deliveryType] || 'info';
        }
        return 'primary'; // 默认值
      },
      // 新的发货方式格式化方法（支持deliveryMethod字段）
      formatDeliveryMethod(row) {
        // 优先使用新的deliveryMethod字段
        if (row.deliveryMethod !== null && row.deliveryMethod !== undefined) {
          switch (row.deliveryMethod) {
            case 1:
              return '快递配送';
            case 2:
              return '门店自提';
            case 3:
              return '虚拟发货';
            default:
              return '未知';
          }
        }
        // 降级使用旧的deliveryType字段
        else if (row.deliveryType !== null && row.deliveryType !== undefined) {
          if (row.deliveryType === 0) {
            return '快递配送';
          } else if (row.deliveryType === 1) {
            return '门店自取';
          }
        }
        return '快递配送'; // 默认值
      },
      // 复制收货人信息
      copyReceiverInfo(row) {
        const receiverInfo = `收货人：${row.receiverName}\n电话：${row.receiverPhone}\n地址：${this.formatAddress(row)}`;

        // 使用现代浏览器的 Clipboard API
        if (navigator.clipboard && window.isSecureContext) {
          navigator.clipboard.writeText(receiverInfo).then(() => {
            this.$message.success('收货人信息已复制到剪贴板');
          }).catch(() => {
            this.fallbackCopyTextToClipboard(receiverInfo);
          });
        } else {
          // 降级方案
          this.fallbackCopyTextToClipboard(receiverInfo);
        }
      },
      // 降级复制方案
      fallbackCopyTextToClipboard(text) {
        const textArea = document.createElement('textarea');
        textArea.value = text;
        textArea.style.position = 'fixed';
        textArea.style.left = '-999999px';
        textArea.style.top = '-999999px';
        document.body.appendChild(textArea);
        textArea.focus();
        textArea.select();

        try {
          document.execCommand('copy');
          this.$message.success('收货人信息已复制到剪贴板');
        } catch (err) {
          this.$message.error('复制失败，请手动复制');
        }

        document.body.removeChild(textArea);
      },
      // 处理退款订单
      handleRefundOrder(index, row) {
        this.refundDialogVisible = true;
        this.refundInfo = {
          orderId: row.id,
          orderSn: row.orderSn,
          payAmount: row.payAmount,
          payType: row.payType,
          refundAmount: parseFloat(row.payAmount) || 0,
          refundReason: '',
          operator: '后台管理员'
        };
      },
      // 确认退款
      handleRefundConfirm() {
        if (!this.refundInfo.refundAmount || this.refundInfo.refundAmount <= 0) {
          this.$message.warning('请输入有效的退款金额');
          return;
        }
        
        if (this.refundInfo.refundAmount > this.refundInfo.payAmount) {
          this.$message.warning('退款金额不能超过订单支付金额');
          return;
        }
        
        this.$confirm(`确认退款 ¥${this.refundInfo.refundAmount} 给用户吗？退款将按原支付渠道返回。`, '确认退款', {
          confirmButtonText: '确认退款',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.refundLoading = true;
          refundOrder({
            orderId: this.refundInfo.orderId,
            refundAmount: this.refundInfo.refundAmount,
            refundReason: this.refundInfo.refundReason,
            operator: this.refundInfo.operator
          }).then(response => {
            this.refundLoading = false;
            this.refundDialogVisible = false;
            this.$message({
              type: 'success',
              message: '退款处理成功！'
            });
            // 刷新列表
            this.getList();
          }).catch(error => {
            this.refundLoading = false;
            this.$message.error((error.response && error.response.data && error.response.data.message) || '退款处理失败');
          });
        }).catch(() => {
          // 取消退款
        });
      },
      // 获取支付方式文本
      getPayTypeText(payType) {
        const payTypeMap = {
          1: '支付宝',
          2: '微信',
          3: '余额支付'
        };
        return payTypeMap[payType] || '未知';
      }
    }
  }
</script>
<style scoped>
  .input-width {
    width: 203px;
  }

  /* 移动端搜索表单样式 */
  .mobile-search-form {
    padding: 15px;
    background: #f8f9fa;
    border-radius: 8px;
    margin-bottom: 15px;
  }

  .search-row {
    margin-bottom: 12px;
  }

  .search-row:last-child {
    margin-bottom: 0;
  }

  .mobile-date-picker .el-button {
    border: 1px solid #dcdfe6;
    background: #fff;
    justify-content: flex-start;
  }

  .search-actions {
    display: flex;
    margin-top: 15px;
    gap: 10px;
  }

  /* 收货人信息样式 */
  .receiver-info {
    text-align: left;
  }

  .receiver-name {
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-weight: 500;
  }

  .receiver-name .el-button {
    padding: 0;
    margin-left: 5px;
    color: #409eff;
  }

  .receiver-name .el-button:hover {
    color: #66b1ff;
  }

  .receiver-phone, .receiver-address {
    margin-top: 2px;
    color: #666;
  }

  /* 移动端时间选择器样式 */
  .mobile-date-picker-container {
    padding: 20px;
    background: #fff;
  }

  .mobile-date-header {
    text-align: center;
    margin-bottom: 20px;
    border-bottom: 1px solid #eee;
    padding-bottom: 15px;
  }

  .mobile-date-header h3 {
    margin: 0;
    font-size: 18px;
    color: #333;
  }

  .date-shortcuts {
    margin-bottom: 25px;
  }

  .shortcut-row {
    display: flex;
    gap: 10px;
    justify-content: space-around;
  }

  .shortcut-btn {
    flex: 1;
    padding: 10px 15px;
    border: 1px solid #ddd;
    border-radius: 6px;
    background: #fff;
    color: #666;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.3s;
  }

  .shortcut-btn.active {
    background: #409eff;
    color: #fff;
    border-color: #409eff;
  }

  .shortcut-btn:hover {
    border-color: #409eff;
    color: #409eff;
  }

  .custom-date-section {
    margin-bottom: 30px;
  }

  .date-item {
    margin-bottom: 15px;
  }

  .date-item label {
    display: block;
    margin-bottom: 8px;
    font-size: 14px;
    color: #333;
    font-weight: 500;
  }

  .date-input {
    padding: 12px 15px;
    border: 1px solid #ddd;
    border-radius: 6px;
    background: #f8f9fa;
    color: #333;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.3s;
  }

  .date-input:active {
    background: #e9ecef;
  }

  .mobile-date-actions {
    display: flex;
    gap: 15px;
    padding-top: 20px;
    border-top: 1px solid #eee;
  }

  .cancel-btn, .confirm-btn {
    flex: 1;
    padding: 12px 20px;
    border: none;
    border-radius: 6px;
    font-size: 16px;
    cursor: pointer;
    transition: all 0.3s;
  }

  .cancel-btn {
    background: #f5f5f5;
    color: #666;
  }

  .cancel-btn:active {
    background: #e9ecef;
  }

  .confirm-btn {
    background: #409eff;
    color: #fff;
  }

  .confirm-btn:active {
    background: #337ab7;
  }

  .text-muted {
    color: #999;
    font-size: 12px;
  }

  .order-detail-expand {
    padding: 20px;
    background-color: #f8f9fa;
  }

  .order-detail-expand h4 {
    margin: 0 0 15px 0;
    color: #333;
    font-size: 14px;
    font-weight: bold;
  }

  /* 移动端适配 */
  @media (max-width: 768px) {
    .app-container {
      padding: 10px;
    }

    .filter-container {
      padding: 10px;
    }

    .el-form--inline .el-form-item {
      display: block;
      margin-bottom: 10px;
      width: 100%;
    }

    .el-form--inline .el-form-item .el-form-item__content {
      width: 100%;
    }

    .input-width {
      width: 100% !important;
    }

    .operate-container {
      padding: 10px;
    }

    .batch-operate-container {
      padding: 10px;
    }

    /* 移动端表格优化 */
    .table-container {
      overflow-x: auto;
      -webkit-overflow-scrolling: touch;
    }

    .el-table {
      min-width: 1000px;
      font-size: 12px;
    }

    .el-table th,
    .el-table td {
      padding: 8px 4px;
    }

    /* 隐藏部分列在移动端 */
    .el-table .mobile-hidden {
      display: none;
    }

    /* 移动端卡片式布局 */
    .mobile-card-view {
      display: block;
    }

    .mobile-card-view .el-table {
      display: none;
    }

    .order-card {
      background: white;
      border-radius: 8px;
      padding: 15px;
      margin-bottom: 10px;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }

    .order-card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 10px;
      padding-bottom: 10px;
      border-bottom: 1px solid #eee;
    }

    .order-card-body {
      font-size: 14px;
      line-height: 1.6;
    }

    .order-card-row {
      display: flex;
      justify-content: space-between;
      margin-bottom: 5px;
    }

    .order-card-label {
      color: #666;
      font-weight: 500;
    }

    .order-card-value {
      color: #333;
      text-align: right;
      flex: 1;
      margin-left: 10px;
    }

    .order-card-actions {
      margin-top: 15px;
      padding-top: 10px;
      border-top: 1px solid #eee;
      text-align: right;
    }

    .order-card-actions .el-button {
      margin-left: 5px;
      padding: 5px 10px;
      font-size: 12px;
    }
  }

  /* 批量发货弹窗样式 */
  .batch-delivery-content {
    padding: 10px 0;
  }

  .batch-delivery-content .el-alert {
    margin-bottom: 20px;
  }

  .batch-delivery-content .el-alert .el-alert__description p {
    margin: 5px 0;
    line-height: 1.6;
    color: #606266;
  }
</style>


