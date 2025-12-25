<template>
  <div class="detail-container">
    <el-card shadow="never">
      <span class="font-title-medium">退货商品</span>
      <el-table
        border
        class="standard-margin"
        ref="productTable"
        :data="productList">
        <el-table-column label="商品图片" width="160" align="center">
          <template slot-scope="scope">
            <img style="height:80px" :src="scope.row.productPic">
          </template>
        </el-table-column>
        <el-table-column label="商品名称" align="center">
          <template slot-scope="scope">
            <span class="font-small">{{scope.row.productName}}</span><br>
            <span class="font-small">品牌：{{scope.row.productBrand}}</span>
          </template>
        </el-table-column>
        <el-table-column label="价格/货号" width="180" align="center">
          <template slot-scope="scope">
            <span class="font-small">价格：￥{{scope.row.productRealPrice}}</span><br>
            <span class="font-small">货号：NO.{{scope.row.productId}}</span>
          </template>
        </el-table-column>
        <el-table-column label="属性" width="180" align="center">
          <template slot-scope="scope">{{scope.row.productAttr}}</template>
        </el-table-column>
        <el-table-column label="数量" width="100" align="center">
          <template slot-scope="scope">{{scope.row.productCount}}</template>
        </el-table-column>
        <el-table-column label="小计" width="100" align="center">
          <template slot-scope="scope">￥{{totalAmount}}</template>
        </el-table-column>
      </el-table>
      <div style="float:right;margin-top:15px;margin-bottom:15px">
        <span class="font-title-medium">合计：</span>
        <span class="font-title-medium color-danger">￥{{totalAmount}}</span>
      </div>
    </el-card>
    <el-card shadow="never" class="standard-margin">
      <span class="font-title-medium">服务单信息</span>

      <!-- 流程状态提示 -->
      <div class="process-tips" style="margin: 15px 0; padding: 10px; background: #f0f9ff; border-left: 4px solid #409eff;">
        <div v-if="orderReturnApply.returnType === 1">
          <strong>仅退款流程：</strong>
          <span v-if="orderReturnApply.status === 0">等待商家审核 → 商家同意后立即退款</span>
          <span v-else-if="orderReturnApply.status === 1">商家已同意，可以进行退款操作</span>
          <span v-else-if="orderReturnApply.status === 2">退款已完成</span>
          <span v-else-if="orderReturnApply.status === 3">申请已被拒绝</span>
        </div>
        <div v-else-if="orderReturnApply.returnType === 2">
          <strong>退货退款流程：</strong>
          <span v-if="orderReturnApply.status === 0">等待商家审核 → 商家同意并提供退货地址 → 用户寄回商品 → 商家确认收货后退款</span>
          <span v-else-if="orderReturnApply.status === 1">商家已同意，等待用户寄回商品</span>
          <span v-else-if="orderReturnApply.status === 2">商品已收到，退款已完成</span>
          <span v-else-if="orderReturnApply.status === 3">申请已被拒绝</span>
        </div>
      </div>
      <div class="form-container-border">
        <el-row>
          <el-col :span="6" class="form-border form-left-bg font-small">服务单号</el-col>
          <el-col class="form-border font-small" :span="18">{{orderReturnApply.id}}</el-col>
        </el-row>
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6">售后类型</el-col>
          <el-col class="form-border font-small" :span="18">
            <el-tag :type="orderReturnApply.returnType === 1 ? 'warning' : 'danger'" size="small">
              {{ orderReturnApply.returnType === 1 ? '仅退款' : '退货退款' }}
            </el-tag>
          </el-col>
        </el-row>
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6">申请状态</el-col>
          <el-col class="form-border font-small" :span="18">
            <el-tag :type="getStatusType(orderReturnApply.status)" size="small">
              {{orderReturnApply.status | formatStatus}}
            </el-tag>
          </el-col>
        </el-row>
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6">退款状态</el-col>
          <el-col class="form-border font-small" :span="18">
            <el-tag :type="getRefundStatusType(orderReturnApply)" size="small">
              {{ getRefundStatusText(orderReturnApply) }}
            </el-tag>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6" class="form-border form-left-bg font-small" style="height:50px;line-height:30px">订单编号
          </el-col>
          <el-col class="form-border font-small" :span="18" style="height:50px">
            {{orderReturnApply.orderSn}}
            <el-button type="text" size="small" @click="handleViewOrder">查看</el-button>
          </el-col>
        </el-row>
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6">申请时间</el-col>
          <el-col class="form-border font-small" :span="18">{{orderReturnApply.createTime | formatTime}}</el-col>
        </el-row>
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6">用户账号</el-col>
          <el-col class="form-border font-small" :span="18">{{orderReturnApply.memberUsername}}</el-col>
        </el-row>
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6">联系人</el-col>
          <el-col class="form-border font-small" :span="18">{{orderReturnApply.returnName}}</el-col>
        </el-row>
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6">联系电话</el-col>
          <el-col class="form-border font-small" :span="18">{{orderReturnApply.returnPhone}}</el-col>
        </el-row>
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6">退货原因</el-col>
          <el-col class="form-border font-small" :span="18">{{orderReturnApply.reason}}</el-col>
        </el-row>
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6">问题描述</el-col>
          <el-col class="form-border font-small" :span="18">{{orderReturnApply.description}}</el-col>
        </el-row>
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6" style="height:100px;line-height:80px">凭证图片
          </el-col>
          <el-col class="form-border font-small" :span="18" style="height:100px">
            <template v-if="proofPics && proofPics.length > 0">
              <img v-for="(item, index) in proofPics" :key="index" style="width:80px;height:80px;margin:10px 5px" :src="item">
            </template>
            <span v-else style="line-height:80px;color:#999">暂无图片</span>
          </el-col>
        </el-row>
      </div>
      <div class="form-container-border">
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6">订单金额</el-col>
          <el-col class="form-border font-small" :span="18">￥{{totalAmount}}</el-col>
        </el-row>
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6" style="height:52px;line-height:32px">确认退款金额
          </el-col>
          <el-col class="form-border font-small" style="height:52px" :span="18">
            ￥
            <el-input size="small" v-model="updateStatusParam.returnAmount"
                      :disabled="orderReturnApply.status!==0"
                      style="width:200px;margin-left: 10px"></el-input>
          </el-col>
        </el-row>
        <!-- 退货地址设置（仅退货退款类型且状态不是已拒绝时显示） -->
        <div v-show="orderReturnApply.returnType === 2 && orderReturnApply.status!==3">
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6" style="height:52px;line-height:32px">收货方式
          </el-col>
          <el-col class="form-border font-small" style="height:52px" :span="18">
            <el-radio-group v-model="addressInputMode" :disabled="orderReturnApply.status!==0" @change="handleAddressModeChange">
              <el-radio :label="'select'">选择收货点</el-radio>
              <el-radio :label="'manual'">手动填写</el-radio>
            </el-radio-group>
          </el-col>
        </el-row>
        
        <!-- 选择收货点模式 -->
        <div v-if="addressInputMode === 'select'">
          <el-row>
            <el-col class="form-border form-left-bg font-small" :span="6" style="height:52px;line-height:32px">选择收货点
            </el-col>
            <el-col class="form-border font-small" style="height:52px" :span="18">
              <el-select size="small"
                         style="width:200px"
                         :disabled="orderReturnApply.status!==0"
                         v-model="updateStatusParam.companyAddressId"
                         @change="handleAddressSelect">
                <el-option v-for="address in companyAddressList"
                           :key="address.id"
                           :value="address.id"
                           :label="address.addressName">
                </el-option>
              </el-select>
            </el-col>
          </el-row>
          <el-row>
            <el-col class="form-border form-left-bg font-small" :span="6">收货人姓名</el-col>
            <el-col class="form-border font-small" :span="18">{{currentAddress.name}}</el-col>
          </el-row>
          <el-row>
            <el-col class="form-border form-left-bg font-small" :span="6">所在区域</el-col>
            <el-col class="form-border font-small" :span="18">{{currentAddress | formatRegion}}</el-col>
          </el-row>
          <el-row>
            <el-col class="form-border form-left-bg font-small" :span="6">详细地址</el-col>
            <el-col class="form-border font-small" :span="18">{{currentAddress.detailAddress}}</el-col>
          </el-row>
          <el-row>
            <el-col class="form-border form-left-bg font-small" :span="6">联系电话</el-col>
            <el-col class="form-border font-small" :span="18">{{currentAddress.phone}}</el-col>
          </el-row>
        </div>
        
        <!-- 手动填写模式 -->
        <div v-if="addressInputMode === 'manual'">
          <el-row>
            <el-col class="form-border form-left-bg font-small" :span="6">收货人姓名</el-col>
            <el-col class="form-border font-small" :span="18">
              <el-input size="small" 
                        v-model="manualAddress.name" 
                        :disabled="orderReturnApply.status!==0"
                        style="width:200px" 
                        placeholder="请输入收货人姓名"></el-input>
            </el-col>
          </el-row>
          <el-row>
            <el-col class="form-border form-left-bg font-small" :span="6">省份</el-col>
            <el-col class="form-border font-small" :span="18">
              <el-input size="small" 
                        v-model="manualAddress.province" 
                        :disabled="orderReturnApply.status!==0"
                        style="width:200px" 
                        placeholder="请输入省份"></el-input>
            </el-col>
          </el-row>
          <el-row>
            <el-col class="form-border form-left-bg font-small" :span="6">城市</el-col>
            <el-col class="form-border font-small" :span="18">
              <el-input size="small" 
                        v-model="manualAddress.city" 
                        :disabled="orderReturnApply.status!==0"
                        style="width:200px" 
                        placeholder="请输入城市"></el-input>
            </el-col>
          </el-row>
          <el-row>
            <el-col class="form-border form-left-bg font-small" :span="6">区域</el-col>
            <el-col class="form-border font-small" :span="18">
              <el-input size="small" 
                        v-model="manualAddress.region" 
                        :disabled="orderReturnApply.status!==0"
                        style="width:200px" 
                        placeholder="请输入区域"></el-input>
            </el-col>
          </el-row>
          <el-row>
            <el-col class="form-border form-left-bg font-small" :span="6">详细地址</el-col>
            <el-col class="form-border font-small" :span="18">
              <el-input size="small" 
                        v-model="manualAddress.detailAddress" 
                        :disabled="orderReturnApply.status!==0"
                        style="width:300px" 
                        placeholder="请输入详细地址"></el-input>
            </el-col>
          </el-row>
          <el-row>
            <el-col class="form-border form-left-bg font-small" :span="6">联系电话</el-col>
            <el-col class="form-border font-small" :span="18">
              <el-input size="small" 
                        v-model="manualAddress.phone" 
                        :disabled="orderReturnApply.status!==0"
                        style="width:200px" 
                        placeholder="请输入联系电话"></el-input>
            </el-col>
          </el-row>
        </div>
        </div>
      </div>
      <div class="form-container-border" v-show="orderReturnApply.status!==0">
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6">处理人员</el-col>
          <el-col class="form-border font-small" :span="18">{{orderReturnApply.handleMan}}</el-col>
        </el-row>
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6">处理时间</el-col>
          <el-col class="form-border font-small" :span="18">{{orderReturnApply.handleTime | formatTime}}</el-col>
        </el-row>
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6">处理备注</el-col>
          <el-col class="form-border font-small" :span="18">{{orderReturnApply.handleNote}}</el-col>
        </el-row>
      </div>

      <!-- 快递信息（仅退货退款类型且已填写快递信息时显示） -->
      <div class="form-container-border" v-show="orderReturnApply.returnType === 2 && orderReturnApply.deliverySn">
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6">快递公司</el-col>
          <el-col class="form-border font-small" :span="18">{{orderReturnApply.deliveryCompany || '未填写'}}</el-col>
        </el-row>
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6">快递单号</el-col>
          <el-col class="form-border font-small" :span="18">{{orderReturnApply.deliverySn || '未填写'}}</el-col>
        </el-row>
      </div>
      <div class="form-container-border" v-show="orderReturnApply.status===2">
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6">收货人员</el-col>
          <el-col class="form-border font-small" :span="18">{{orderReturnApply.receiveMan}}</el-col>
        </el-row>
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6" >收货时间</el-col>
          <el-col class="form-border font-small" :span="18">{{orderReturnApply.receiveTime | formatTime}}</el-col>
        </el-row>
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6">收货备注</el-col>
          <el-col class="form-border font-small" :span="18">{{orderReturnApply.receiveNote}}</el-col>
        </el-row>
      </div>
      <div class="form-container-border" v-show="orderReturnApply.status===0">
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6" style="height:52px;line-height:32px">处理备注</el-col>
          <el-col class="form-border font-small" :span="18">
            <el-input  size="small" v-model="updateStatusParam.handleNote" style="width:200px;margin-left: 10px"></el-input>
          </el-col>
        </el-row>
      </div>
      <div class="form-container-border" v-show="orderReturnApply.status===1">
        <el-row>
          <el-col class="form-border form-left-bg font-small" :span="6" style="height:52px;line-height:32px">收货备注</el-col>
          <el-col class="form-border font-small" :span="18">
            <el-input  size="small" v-model="updateStatusParam.receiveNote" style="width:200px;margin-left: 10px"></el-input>
          </el-col>
        </el-row>
      </div>
      <!-- 待处理状态的操作按钮 -->
      <div style="margin-top:15px;text-align: center" v-show="orderReturnApply.status===0">
        <el-button type="primary" size="small" @click="handleUpdateStatus(1)">
          {{ orderReturnApply.returnType === 1 ? '同意退款' : '确认退货' }}
        </el-button>
        <el-button type="danger" size="small" @click="handleUpdateStatus(3)">
          {{ orderReturnApply.returnType === 1 ? '拒绝退款' : '拒绝退货' }}
        </el-button>
      </div>

      <!-- 退货中状态的操作按钮（仅退货退款类型） -->
      <div style="margin-top:15px;text-align: center" v-show="orderReturnApply.status===1 && orderReturnApply.returnType === 2 && orderReturnApply.refundStatus !== 1 && !hasRefundInProgress">
        <el-button type="primary" size="small" @click="handleUpdateStatus(2)" :disabled="refundLoading">确认收货</el-button>
        <el-button type="success" size="small" @click="handleRefund" :loading="refundLoading" :disabled="refundLoading">
          {{ refundLoading ? '退款中...' : '退款' }}
        </el-button>
      </div>

      <!-- 退货退款类型已确认收货但未退款的操作按钮 -->
      <div style="margin-top:15px;text-align: center" v-show="orderReturnApply.status===2 && orderReturnApply.returnType === 2 && orderReturnApply.refundStatus !== 1 && !hasRefundInProgress">
        <el-button type="success" size="small" @click="handleRefund" :loading="refundLoading" :disabled="refundLoading">
          {{ refundLoading ? '退款中...' : '立即退款' }}
        </el-button>
      </div>

      <!-- 仅退款类型且已同意的操作按钮 -->
      <div style="margin-top:15px;text-align: center" v-show="orderReturnApply.status===1 && orderReturnApply.returnType === 1 && orderReturnApply.refundStatus !== 1 && !hasRefundInProgress">
        <el-button type="success" size="small" @click="handleRefund" :loading="refundLoading" :disabled="refundLoading">
          {{ refundLoading ? '退款中...' : '立即退款' }}
        </el-button>
      </div>

      <!-- 退款处理中的提示 -->
      <div style="margin-top:15px;text-align: center" v-show="hasRefundInProgress">
        <el-alert
          title="退款处理中"
          type="info"
          description="微信退款正在处理中，请耐心等待处理结果"
          show-icon
          :closable="false">
        </el-alert>
      </div>
    </el-card>

    <!-- 退款记录 -->
    <el-card shadow="never" class="standard-margin" v-if="refundRecords && refundRecords.length > 0">
      <span class="font-title-medium">退款记录</span>
      <el-table
        border
        class="standard-margin"
        :data="refundRecords">
        <el-table-column label="退款单号" align="center">
          <template slot-scope="scope">{{scope.row.refundSn}}</template>
        </el-table-column>
        <el-table-column label="退款金额" width="120" align="center">
          <template slot-scope="scope">￥{{scope.row.refundAmount}}</template>
        </el-table-column>
        <el-table-column label="退款状态" width="120" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.refundStatus === 1 ? 'success' : 'warning'" size="small">
              {{ scope.row.refundStatus === 1 ? '退款成功' : '退款中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="退款时间" width="180" align="center">
          <template slot-scope="scope">{{scope.row.createTime | formatTime}}</template>
        </el-table-column>
        <el-table-column label="备注" align="center">
          <template slot-scope="scope">{{scope.row.note || '无'}}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>
<script>
  import {getApplyDetail,updateApplyStatus,processRefund,getRefundRecords} from '@/api/returnApply';
  import {fetchList} from '@/api/companyAddress';
  import {formatDate} from '@/utils/date';

  const defaultUpdateStatusParam = {
    companyAddressId: null,
    handleMan: 'admin',
    handleNote: null,
    receiveMan: 'admin',
    receiveNote: null,
    returnAmount: 0,
    status: 0
  };
  const defaultOrderReturnApply = {
    id: null,
    orderId: null,
    companyAddressId: null,
    productId: null,
    orderSn: null,
    createTime: null,
    memberUsername: null,
    returnAmount: null,
    returnName: null,
    returnPhone: null,
    status: null,
    handleTime: null,
    productPic: null,
    productName: null,
    productBrand: null,
    productAttr: null,
    productCount: null,
    productPrice: null,
    productRealPrice: null,
    reason: null,
    description: null,
    proofPics: null,
    handleNote: null,
    handleMan: null,
    receiveMan: null,
    receiveTime: null,
    receiveNote: null
  };
  export default {
    name: 'returnApplyDetail',
    data() {
      return {
        id: null,
        orderReturnApply: Object.assign({},defaultOrderReturnApply),
        productList: null,
        proofPics: null,
        updateStatusParam: Object.assign({}, defaultUpdateStatusParam),
        companyAddressList: null,
        addressInputMode: 'select', // 收货地址输入模式：'select' 选择收货点，'manual' 手动填写
        manualAddress: { // 手动填写的地址信息
          name: '',
          province: '',
          city: '',
          region: '',
          detailAddress: '',
          phone: ''
        },
        refundLoading: false, // 退款处理中状态
        refundRecords: [] // 退款记录
      }
    },
    created() {
      this.id = this.$route.query.id;
      this.getDetail();
      this.getRefundRecordsList();
    },
    computed: {
      totalAmount() {
        if (this.orderReturnApply != null) {
          return this.orderReturnApply.productRealPrice * this.orderReturnApply.productCount;
        } else {
          return 0;
        }
      },
      currentAddress() {
        console.log("currentAddress()");
        
        // 如果是手动填写模式，返回手动填写的地址
        if (this.addressInputMode === 'manual') {
          return {
            name: this.manualAddress.name,
            province: this.manualAddress.province,
            city: this.manualAddress.city,
            region: this.manualAddress.region,
            detailAddress: this.manualAddress.detailAddress,
            phone: this.manualAddress.phone
          };
        }
        
        // 如果是选择模式，返回选中的收货点地址
        let id = this.updateStatusParam.companyAddressId;
        if(this.companyAddressList==null)return {};
        for (let i = 0; i < this.companyAddressList.length; i++) {
          let address = this.companyAddressList[i];
          if (address.id === id) {
            return address;
          }
        }
        
        // 如果没有找到对应的收货点，可能是历史数据中存储了手动地址信息
        if (this.orderReturnApply && this.orderReturnApply.handleNote) {
          const manualAddressInfo = this.parseManualAddressFromNote(this.orderReturnApply.handleNote);
          if (manualAddressInfo) {
            return manualAddressInfo;
          }
        }
        
        return {};
      },

      // 判断是否有退款正在处理中
      hasRefundInProgress() {
        // 首先检查退款处理状态字段
        if (this.orderReturnApply.refundProcessStatus === 1) {
          return true; // 退款处理中
        }

        // 兼容性检查：如果没有refundProcessStatus字段，则检查退款记录
        if (!this.refundRecords || this.refundRecords.length === 0) {
          return false;
        }
        // 检查是否有状态为0（退款中）的记录
        return this.refundRecords.some(record => record.status === 0);
      }
    },
    filters: {
      formatStatus(status) {
        if (status === 0) {
          return "待处理";
        } else if (status === 1) {
          return "退货中";
        } else if (status === 2) {
          return "已完成";
        } else {
          return "已拒绝";
        }
      },
      formatTime(time) {
        if (time == null || time === '') {
          return 'N/A';
        }
        let date = new Date(time);
        return formatDate(date, 'yyyy-MM-dd hh:mm:ss')
      },
      formatRegion(address) {
        let str = address.province;
        if (address.city != null) {
          str += "  " + address.city;
        }
        str += "  " + address.region;
        return str;
      }
    },
    methods: {
      // 获取状态标签类型
      getStatusType(status) {
        const statusTypes = {
          0: 'warning',  // 待处理
          1: 'primary',  // 退货中
          2: 'success',  // 已完成
          3: 'danger'    // 已拒绝
        };
        return statusTypes[status] || 'info';
      },

      // 获取退款状态标签类型
      getRefundStatusType(returnApply) {
        // 优先检查refundProcessStatus字段
        if (returnApply.refundProcessStatus === 3) {
          return 'danger'; // 退款失败
        }

        if (returnApply.refundProcessStatus === 1) {
          return 'warning'; // 退款中
        }

        if (returnApply.refundProcessStatus === 2 || returnApply.refundStatus === 1) {
          return 'success'; // 已退款
        }

        // 兼容性检查：如果没有refundProcessStatus字段，检查退款记录
        if (this.hasRefundInProgress) {
          return 'warning'; // 退款中
        }

        if (returnApply.refundStatus === 1) {
          return 'success'; // 已退款
        }

        return 'info'; // 未退款
      },

      // 获取退款状态文本
      getRefundStatusText(returnApply) {
        // 优先检查refundProcessStatus字段
        if (returnApply.refundProcessStatus === 3) {
          return '退款失败';
        }

        if (returnApply.refundProcessStatus === 1) {
          return '退款中';
        }

        if (returnApply.refundProcessStatus === 2 || returnApply.refundStatus === 1) {
          return '已退款';
        }

        // 兼容性检查：如果没有refundProcessStatus字段，检查退款记录
        if (this.hasRefundInProgress) {
          return '退款中';
        }

        if (returnApply.refundStatus === 1) {
          return '已退款';
        }

        return '未退款';
      },

      handleViewOrder(){
        this.$router.push({path:'/oms/orderDetail',query:{id:this.orderReturnApply.orderId}});
      },

      // 获取退款记录
      getRefundRecordsList() {
        getRefundRecords(this.id).then(response => {
          this.refundRecords = response.data || [];
        }).catch(err => {
          console.error('获取退款记录失败', err);
        });
      },
      getDetail() {
        getApplyDetail(this.id).then(response => {
          console.log("getDetail")
          this.orderReturnApply = response.data;
          this.productList = [];
          this.productList.push(this.orderReturnApply);
          if (this.orderReturnApply.proofPics != null && this.orderReturnApply.proofPics.trim() !== '') {
            this.proofPics = this.orderReturnApply.proofPics.split(",").filter(item => item.trim() !== '')
          } else {
            this.proofPics = []
          }
          //退货中和完成
          if(this.orderReturnApply.status===1||this.orderReturnApply.status===2){
            this.updateStatusParam.returnAmount=this.orderReturnApply.returnAmount;
            this.updateStatusParam.companyAddressId=this.orderReturnApply.companyAddressId;
            
            // 检查是否使用了手动填写的地址
            const manualAddressInfo = this.parseManualAddressFromNote(this.orderReturnApply.handleNote);
            if (manualAddressInfo) {
              // 使用手动填写模式，并填充地址信息
              this.addressInputMode = 'manual';
              this.manualAddress = manualAddressInfo;
            } else if (this.orderReturnApply.companyAddressId) {
              // 使用选择模式
              this.addressInputMode = 'select';
            } else {
              // 默认为选择模式
              this.addressInputMode = 'select';
            }
          }
          this.getCompanyAddressList();
        });
      },
      getCompanyAddressList() {
        fetchList().then(response => {
          console.log("getCompanyAddressList()")
          this.companyAddressList = response.data;
          for (let i = 0; i < this.companyAddressList.length; i++) {
            if (this.companyAddressList[i].receiveStatus === 1&&this.orderReturnApply.status===0) {
              this.updateStatusParam.companyAddressId = this.companyAddressList[i].id;
            }
          }
        });
      },
      handleAddressModeChange(mode) {
        // 切换地址输入模式时清空相关数据
        if (mode === 'select') {
          // 切换到选择模式，清空手动填写的数据
          this.manualAddress = {
            name: '',
            province: '',
            city: '',
            region: '',
            detailAddress: '',
            phone: ''
          };
          // 如果有可用的收货点，选择第一个
          if (this.companyAddressList && this.companyAddressList.length > 0) {
            this.updateStatusParam.companyAddressId = this.companyAddressList[0].id;
          }
        } else if (mode === 'manual') {
          // 切换到手动模式，清空选择的收货点
          this.updateStatusParam.companyAddressId = null;
        }
      },
      
      handleAddressSelect(addressId) {
        // 选择收货点时，清空手动填写的数据
        this.manualAddress = {
          name: '',
          province: '',
          city: '',
          region: '',
          detailAddress: '',
          phone: ''
        };
      },
      
      // 从处理备注中解析手动地址信息
      parseManualAddressFromNote(note) {
        if (!note) return null;
        
        try {
          // 查找手动收货地址的JSON字符串，使用更宽松的匹配模式
          const addressMatch = note.match(/手动收货地址：(\{[^}]*\})/);
          if (addressMatch && addressMatch[1]) {
            // 尝试修复可能被截断的JSON
            let jsonStr = addressMatch[1];
            
            // 如果JSON字符串不完整，尝试从完整的备注中提取
            if (!jsonStr.endsWith('}')) {
              // 查找最后一个完整的手动收货地址JSON
              const allMatches = note.match(/手动收货地址：(\{[^}]*\})/g);
              if (allMatches && allMatches.length > 0) {
                const lastMatch = allMatches[allMatches.length - 1];
                const lastJsonMatch = lastMatch.match(/手动收货地址：(\{[^}]*\})/);
                if (lastJsonMatch && lastJsonMatch[1]) {
                  jsonStr = lastJsonMatch[1];
                }
              }
            }
            
            const addressInfo = JSON.parse(jsonStr);
            if (addressInfo.type === 'manual') {
              return {
                name: addressInfo.name || '',
                province: addressInfo.province || '',
                city: addressInfo.city || '',
                region: addressInfo.region || '',
                detailAddress: addressInfo.detailAddress || '',
                phone: addressInfo.phone || ''
              };
            }
          }
        } catch (e) {
          console.error('解析手动地址信息失败:', e);
          console.error('原始备注内容:', note);
        }
        
        return null;
      },
      
      // 验证地址信息是否完整
      validateAddress() {
        if (this.addressInputMode === 'select') {
          return this.updateStatusParam.companyAddressId != null;
        } else if (this.addressInputMode === 'manual') {
          return this.manualAddress.name && 
                 this.manualAddress.province && 
                 this.manualAddress.city && 
                 this.manualAddress.region && 
                 this.manualAddress.detailAddress && 
                 this.manualAddress.phone;
        }
        return false;
      },
      
      handleUpdateStatus(status){
        // 验证地址信息
        if (!this.validateAddress()) {
          this.$message({
            type: 'warning',
            message: '请完善收货地址信息',
            duration: 2000
          });
          return;
        }
        
        this.updateStatusParam.status=status;
        
        // 如果是手动填写模式，将地址信息存储在handleNote中
        if (this.addressInputMode === 'manual') {
          const manualAddressInfo = {
            type: 'manual',
            name: this.manualAddress.name,
            province: this.manualAddress.province,
            city: this.manualAddress.city,
            region: this.manualAddress.region,
            detailAddress: this.manualAddress.detailAddress,
            phone: this.manualAddress.phone
          };
          
          // 检查是否已经包含手动地址信息，避免重复添加
          const originalNote = this.updateStatusParam.handleNote || '';
          const addressNote = `手动收货地址：${JSON.stringify(manualAddressInfo)}`;
          
          // 如果原备注中不包含手动收货地址信息，才添加
          if (!originalNote.includes('手动收货地址：')) {
            this.updateStatusParam.handleNote = originalNote ? `${originalNote}；${addressNote}` : addressNote;
          } else {
            // 如果已经包含，则替换原有的手动地址信息
            const updatedNote = originalNote.replace(/手动收货地址：\{[^}]*\}/g, addressNote);
            this.updateStatusParam.handleNote = updatedNote;
          }
          
          // 清空companyAddressId，表示使用手动填写的地址
          this.updateStatusParam.companyAddressId = null;
        }
        
        this.$confirm('是否要进行此操作?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          updateApplyStatus(this.id,this.updateStatusParam).then(response=>{
            this.$message({
              type: 'success',
              message: '操作成功!',
              duration:1000
            });
            // 刷新当前页面数据而不是返回列表页
            this.getDetail();
            this.getRefundRecordsList();
          });
        });
      },
      
      // 处理退款
      handleRefund() {
        // 验证退款金额
        if (!this.orderReturnApply.returnAmount || this.orderReturnApply.returnAmount <= 0) {
          this.$message({
            type: 'warning',
            message: '退款金额不能为空或小于等于0',
            duration: 2000
          });
          return;
        }
        
        // 检查是否已经退款（防止重复退款）
        if (this.orderReturnApply.refundStatus === 1) {
          this.$message({
            type: 'warning',
            message: '该退货申请已经退款，请勿重复操作',
            duration: 2000
          });
          return;
        }
        
        // 确认退款操作
        this.$confirm(
          `确定要退款 ¥${this.orderReturnApply.returnAmount} 给用户吗？\n\n` +
          `订单号：${this.orderReturnApply.orderSn}\n` +
          `退款原因：退货退款`, 
          '确认退款', 
          {
            confirmButtonText: '确定退款',
            cancelButtonText: '取消',
            type: 'warning',
            dangerouslyUseHTMLString: false
          }
        ).then(() => {
          this.refundLoading = true;
          
          // 调用退款API
          this.processRefundAction();
        }).catch(() => {
          // 用户取消退款
        });
      },
      
      // 执行退款操作
      processRefundAction() {
        const refundData = {
          returnApplyId: this.orderReturnApply.id,
          orderId: this.orderReturnApply.orderId,
          orderSn: this.orderReturnApply.orderSn,
          refundAmount: this.orderReturnApply.returnAmount,
          refundReason: this.orderReturnApply.returnType === 1 ? '仅退款' : '退货退款'
        };

        // 调用实际的退款API
        processRefund(this.orderReturnApply.id, refundData).then(response => {
          this.refundLoading = false;

          if (response.code === 200) {
            this.$message({
              type: 'success',
              message: '退款成功！',
              duration: 2000
            });

            // 刷新页面数据和退款记录
            this.getDetail();
            this.getRefundRecordsList();
          } else {
            this.$message({
              type: 'error',
              message: response.message || '退款失败',
              duration: 3000
            });
          }
        }).catch(error => {
          this.refundLoading = false;
          console.error('退款失败:', error);

          this.$message({
            type: 'error',
            message: '退款失败，请稍后重试',
            duration: 3000
          });
        });
      }
    }
  }
</script>
<style scoped>
  .detail-container {
    position: absolute;
    left: 0;
    right: 0;
    width: 1080px;
    padding: 35px 35px 15px 35px;
    margin: 20px auto;
  }

  .standard-margin {
    margin-top: 15px;
  }
  .form-border {
    border-right: 1px solid #DCDFE6;
    border-bottom: 1px solid #DCDFE6;
    padding: 10px;
  }

  .form-container-border {
    border-left: 1px solid #DCDFE6;
    border-top: 1px solid #DCDFE6;
    margin-top: 15px;
  }

  .form-left-bg {
    background: #F2F6FC;
  }
</style>


