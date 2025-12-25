<template>
  <div class="detail-container">
    <!-- 桌面端步骤条 -->
    <div v-if="!isMobile">
      <el-steps :active="formatStepStatus(order.status)" finish-status="success" align-center>
        <el-step title="提交订单" :description="formatTime(order.createTime)"></el-step>
        <el-step title="支付订单" :description="formatTime(order.paymentTime)"></el-step>
        <el-step title="平台发货" :description="formatTime(order.deliveryTime)"></el-step>
        <el-step title="确认收货" :description="formatTime(order.receiveTime)"></el-step>
        <el-step title="完成评价" :description="formatTime(order.commentTime)"></el-step>
      </el-steps>
    </div>

    <!-- 移动端订单状态 -->
    <div v-else class="mobile-status-section">
      <van-card class="status-card">
        <template #title>
          <div class="status-header">
            <van-tag :type="getStatusTagType(order.status)">{{order.status | formatStatus}}</van-tag>
          </div>
        </template>
        <template #desc>
          <van-steps direction="vertical" :active="formatStepStatus(order.status)">
            <van-step>
              <h3>提交订单</h3>
              <p>{{formatTime(order.createTime)}}</p>
            </van-step>
            <van-step>
              <h3>支付订单</h3>
              <p>{{formatTime(order.paymentTime)}}</p>
            </van-step>
            <van-step>
              <h3>平台发货</h3>
              <p>{{formatTime(order.deliveryTime)}}</p>
            </van-step>
            <van-step>
              <h3>确认收货</h3>
              <p>{{formatTime(order.receiveTime)}}</p>
            </van-step>
            <van-step>
              <h3>完成评价</h3>
              <p>{{formatTime(order.commentTime)}}</p>
            </van-step>
          </van-steps>
        </template>
      </van-card>
    </div>
    <el-card shadow="never" style="margin-top: 15px" :class="{ 'mobile-card': isMobile }">
      <!-- 桌面端操作区域 -->
      <div v-if="!isMobile" class="operate-container">
        <i class="el-icon-warning color-danger" style="margin-left: 20px"></i>
        <span class="color-danger">当前订单状态：{{order.status | formatStatus}}</span>
        <div class="operate-button-container" v-show="order.status===0">
          <el-button size="mini" @click="showUpdateReceiverDialog">修改收货人信息</el-button>
          <el-button size="mini" @click="showUpdateMoneyDialog">修改费用信息</el-button>
          <el-button size="mini" @click="showMessageDialog">发送站内信</el-button>
          <el-button size="mini" @click="showCloseOrderDialog">关闭订单</el-button>
          <el-button size="mini" @click="showMarkOrderDialog">备注订单</el-button>
        </div>
        <div class="operate-button-container" v-show="order.status===1">
          <el-button size="mini" @click="showUpdateReceiverDialog">修改收货人信息</el-button>
          <el-button size="mini" @click="showMessageDialog">发送站内信</el-button>
          <el-button size="mini">取消订单</el-button>
          <el-button size="mini" @click="showMarkOrderDialog">备注订单</el-button>
          <el-button
            size="mini"
            type="primary"
            @click="handleDeliveryOrder"
            v-show="order.deliveryType===0">订单发货</el-button>
          <!-- 核销订单按钮已隐藏 -->
          <!-- <el-button
            size="mini"
            type="success"
            @click="showPickupDialog"
            v-show="order.deliveryType===1 && (order.pickupStatus===null || order.pickupStatus===0)">核销订单</el-button> -->
        </div>
        <div class="operate-button-container" v-show="order.status===2||order.status===3">
          <el-button size="mini" @click="showLogisticsDialog" v-show="order.deliveryType===0">订单跟踪</el-button>
          <el-button size="mini" @click="showMessageDialog">发送站内信</el-button>
          <el-button size="mini" @click="showMarkOrderDialog">备注订单</el-button>
          <!-- 自助订单退款按钮 -->
          <el-button
            size="mini"
            type="warning"
            @click="showRefundDialog"
            v-show="isAdmin && order.sourceType===2 && order.status===3">退款</el-button>
          <!-- 核销订单按钮已隐藏 -->
          <!-- <el-button
            size="mini"
            type="success"
            @click="showPickupDialog"
            v-show="order.deliveryType===1 && order.status>=1 && (order.pickupStatus===null || order.pickupStatus===0)">核销订单</el-button> -->
        </div>
        <div class="operate-button-container" v-show="order.status===4">
          <el-button size="mini" @click="handleDeleteOrder">删除订单</el-button>
          <el-button size="mini" @click="showMarkOrderDialog">备注订单</el-button>
        </div>
      </div>

      <!-- 移动端操作区域 -->
      <div v-else class="mobile-operate-section">
        <van-card class="operate-card">
          <template #title>
            <div class="operate-title">
              <i class="el-icon-setting"></i>
              <span>订单操作</span>
            </div>
          </template>
          <template #desc>
            <div class="mobile-operate-buttons">
              <!-- 待付款状态 -->
              <template v-if="order.status===0">
                <van-button size="small" @click="showUpdateReceiverDialog">修改收货人</van-button>
                <van-button size="small" @click="showUpdateMoneyDialog">修改费用</van-button>
                <van-button size="small" @click="showCloseOrderDialog">关闭订单</van-button>
                <van-button size="small" @click="showMarkOrderDialog">备注订单</van-button>
              </template>
              <!-- 待发货状态 -->
              <template v-if="order.status===1">
                <van-button size="small" @click="showUpdateReceiverDialog">修改收货人</van-button>
                <van-button size="small" @click="showMarkOrderDialog">备注订单</van-button>
                <van-button
                  size="small"
                  type="primary"
                  @click="handleDeliveryOrder"
                  v-show="order.deliveryType===0">订单发货</van-button>
                <!-- 核销订单按钮已隐藏 -->
                <!-- <van-button
                  size="small"
                  type="success"
                  @click="showPickupDialog"
                  v-show="order.deliveryType===1 && (order.pickupStatus===null || order.pickupStatus===0)">核销订单</van-button> -->
              </template>
              <!-- 已发货/已完成状态 -->
              <template v-if="order.status===2||order.status===3">
                <van-button size="small" @click="showLogisticsDialog" v-show="order.deliveryType===0">订单跟踪</van-button>
                <van-button size="small" @click="showMarkOrderDialog">备注订单</van-button>
                <!-- 自助订单退款按钮 -->
                <van-button
                  size="small"
                  type="warning"
                  @click="showRefundDialog"
                  v-show="isAdmin && order.sourceType===2 && order.status===3">退款</van-button>
                <!-- 核销订单按钮已隐藏 -->
                <!-- <van-button
                  size="small"
                  type="success"
                  @click="showPickupDialog"
                  v-show="order.deliveryType===1 && order.status>=1 && (order.pickupStatus===null || order.pickupStatus===0)">核销订单</van-button> -->
              </template>
              <!-- 已关闭状态 -->
              <template v-if="order.status===4">
                <van-button size="small" @click="handleDeleteOrder">删除订单</van-button>
                <van-button size="small" @click="showMarkOrderDialog">备注订单</van-button>
              </template>
            </div>
          </template>
        </van-card>
      </div>
      <div style="margin-top: 20px">
        <svg-icon icon-class="marker" style="color: #606266"></svg-icon>
        <span class="font-small">基本信息</span>
      </div>

      <!-- 桌面端表格布局 -->
      <div v-if="!isMobile" class="table-layout">
        <el-row>
          <el-col :span="4" class="table-cell-title">订单编号</el-col>
          <el-col :span="4" class="table-cell-title">发货单流水号</el-col>
          <el-col :span="4" class="table-cell-title">用户账号</el-col>
          <el-col :span="4" class="table-cell-title">支付方式</el-col>
          <el-col :span="4" class="table-cell-title">订单来源</el-col>
          <el-col :span="4" class="table-cell-title">订单类型</el-col>
        </el-row>
        <el-row>
          <el-col :span="4" class="table-cell">{{order.orderSn}}</el-col>
          <el-col :span="4" class="table-cell">暂无</el-col>
          <el-col :span="4" class="table-cell">{{order.memberUsername}}</el-col>
          <el-col :span="4" class="table-cell">{{order.payType | formatPayType}}</el-col>
          <el-col :span="4" class="table-cell">{{order.sourceType | formatSourceType}}</el-col>
          <el-col :span="4" class="table-cell">{{order.orderType | formatOrderType}}</el-col>
        </el-row>
        <el-row>
          <el-col :span="4" class="table-cell-title">配送方式</el-col>
          <el-col :span="4" class="table-cell-title">物流单号</el-col>
          <el-col :span="4" class="table-cell-title">自动确认收货时间</el-col>
          <el-col :span="4" class="table-cell-title">订单可得积分</el-col>
          <el-col :span="4" class="table-cell-title">订单可得成长值</el-col>
          <el-col :span="4" class="table-cell-title">活动信息</el-col>
        </el-row>
        <el-row>
          <el-col :span="4" class="table-cell">{{order.deliveryCompany | formatNull}}</el-col>
          <el-col :span="4" class="table-cell">{{order.deliverySn | formatNull}}</el-col>
          <el-col :span="4" class="table-cell">{{order.autoConfirmDay}}天</el-col>
          <el-col :span="4" class="table-cell">{{order.integration}}</el-col>
          <el-col :span="4" class="table-cell">{{order.growth}}</el-col>
          <el-col :span="4" class="table-cell">
            <el-popover
              placement="top-start"
              title="活动信息"
              width="200"
              trigger="hover"
              :content="order.promotionInfo">
              <span slot="reference">{{order.promotionInfo | formatLongText}}</span>
            </el-popover>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="4" class="table-cell-title">取货方式</el-col>
          <el-col :span="20" class="table-cell">{{order.deliveryMethod | formatDeliveryMethod}}</el-col>
        </el-row>
        <el-row v-if="order.note">
          <el-col :span="4" class="table-cell-title">订单备注</el-col>
          <el-col :span="20" class="table-cell">{{order.note}}</el-col>
        </el-row>
      </div>

      <!-- 移动端卡片布局 -->
      <div v-else class="mobile-info-section">
        <van-card class="info-card">
          <template #title>
            <div class="info-title">
              <i class="el-icon-document"></i>
              <span>基本信息</span>
            </div>
          </template>
          <template #desc>
            <van-cell-group>
              <van-cell title="订单编号" :value="order.orderSn" />
              <van-cell title="用户账号" :value="order.memberUsername" />
              <van-cell title="支付方式">
                <template #value>
                  <van-tag :type="getPayTypeTagType(order.payType)">{{order.payType | formatPayType}}</van-tag>
                </template>
              </van-cell>
              <van-cell title="订单来源">
                <template #value>
                  <van-tag :type="getSourceTypeTagType(order.sourceType)">{{order.sourceType | formatSourceType}}</van-tag>
                </template>
              </van-cell>
              <van-cell title="订单类型" :value="order.orderType | formatOrderType" />
              <van-cell title="取货方式">
                <template #value>
                  <van-tag :type="getDeliveryMethodTagType(order.deliveryMethod)">{{order.deliveryMethod | formatDeliveryMethod}}</van-tag>
                </template>
              </van-cell>
              <van-cell v-if="order.deliveryCompany" title="配送方式" :value="order.deliveryCompany" />
              <van-cell v-if="order.deliverySn" title="物流单号" :value="order.deliverySn" />
              <van-cell v-if="order.note" title="订单备注" :value="order.note" />
            </van-cell-group>
          </template>
        </van-card>
      </div>
      <!-- 桌面端收货人信息 -->
      <div v-if="!isMobile" style="margin-top: 20px">
        <svg-icon icon-class="marker" style="color: #606266"></svg-icon>
        <span class="font-small" v-if="order.deliveryType===0">收货人信息</span>
        <span class="font-small" v-else>自提门店信息</span>
        <el-button
          v-if="order.deliveryType===0"
          size="mini"
          type="primary"
          style="float: right; margin-right: 20px;"
          @click="handleCopyAddress">一键复制</el-button>
      </div>
      <div v-if="!isMobile && order.deliveryType===0" class="table-layout">
        <el-row>
          <el-col :span="6" class="table-cell-title">收货人</el-col>
          <el-col :span="6" class="table-cell-title">手机号码</el-col>
          <el-col :span="3" class="table-cell-title">邮政编码</el-col>
          <el-col :span="9" class="table-cell-title">收货地址</el-col>
        </el-row>
        <el-row>
          <el-col :span="6" class="table-cell">{{order.receiverName}}</el-col>
          <el-col :span="6" class="table-cell">{{order.receiverPhone}}</el-col>
          <el-col :span="3" class="table-cell">{{order.receiverPostCode}}</el-col>
          <el-col :span="9" class="table-cell">{{order | formatAddress}}</el-col>
        </el-row>
      </div>
      <div v-if="!isMobile && order.deliveryType!==0" class="table-layout">
        <el-row>
          <el-col :span="6" class="table-cell-title">门店名称</el-col>
          <el-col :span="6" class="table-cell-title">门店负责人电话</el-col>
          <el-col :span="12" class="table-cell-title">门店地址</el-col>
        </el-row>
        <el-row>
          <el-col :span="6" class="table-cell">{{order.storeName}}</el-col>
          <el-col :span="6" class="table-cell">{{order.storePhone}}</el-col>
          <el-col :span="12" class="table-cell">{{order.storeAddress}}</el-col>
        </el-row>
      </div>

      <!-- 移动端收货人信息 -->
      <div v-else class="mobile-receiver-section">
        <van-card class="receiver-card">
          <template #title>
            <div class="receiver-title">
              <i class="el-icon-location"></i>
              <span v-if="order.deliveryType===0">收货人信息</span>
              <span v-else>自提门店信息</span>
              <van-button
                v-if="order.deliveryType===0"
                size="mini"
                type="primary"
                @click="handleCopyAddress">一键复制</van-button>
            </div>
          </template>
          <template #desc>
            <van-cell-group v-if="order.deliveryType===0">
              <van-cell title="收货人" :value="order.receiverName" />
              <van-cell title="手机号码" :value="order.receiverPhone" />
              <van-cell title="邮政编码" :value="order.receiverPostCode" />
              <van-cell title="收货地址" :value="order | formatAddress" />
            </van-cell-group>
            <van-cell-group v-else>
              <van-cell title="门店名称" :value="order.storeName" />
              <van-cell title="门店负责人电话" :value="order.storePhone" />
              <van-cell title="门店地址" :value="order.storeAddress" />
            </van-cell-group>
          </template>
        </van-card>
      </div>
      <!-- 桌面端商品信息 -->
      <div v-if="!isMobile" style="margin-top: 20px">
        <svg-icon icon-class="marker" style="color: #606266"></svg-icon>
        <span class="font-small">商品信息</span>
      </div>
      <el-table
        v-if="!isMobile"
        ref="orderItemTable"
        :data="order.orderItemList"
        style="width: 100%;margin-top: 20px" border>
        <el-table-column label="商品图片" width="120" align="center">
          <template slot-scope="scope">
            <img :src="scope.row.productPic" style="height: 80px">
          </template>
        </el-table-column>
        <el-table-column label="商品名称" align="center">
          <template slot-scope="scope">
            <p>{{scope.row.productName}}</p>
            <p>品牌：{{scope.row.productBrand}}</p>
          </template>
        </el-table-column>
        <el-table-column label="价格/货号" width="120" align="center">
          <template slot-scope="scope">
            <p>价格：￥{{scope.row.productPrice}}</p>
            <p>货号：{{scope.row.productSn}}</p>
          </template>
        </el-table-column>
        <el-table-column label="属性" width="120" align="center">
          <template slot-scope="scope">
            {{scope.row.productAttr | formatProductAttr}}
          </template>
        </el-table-column>
        <el-table-column label="数量" width="120" align="center">
          <template slot-scope="scope">
            {{scope.row.productQuantity}}
          </template>
        </el-table-column>
        <el-table-column label="小计" width="120" align="center">
          <template slot-scope="scope">
            ￥{{scope.row.productPrice*scope.row.productQuantity}}
          </template>
        </el-table-column>
      </el-table>
      <div v-if="!isMobile" style="float: right;margin: 20px">
        合计：<span class="color-danger">{{formatOrderAmount(order)}}</span>
      </div>

      <!-- 桌面端库存扣除信息 -->
      <div v-if="!isMobile && order.stockDeductionList && order.stockDeductionList.length > 0" style="margin-top: 40px; clear: both;">
        <svg-icon icon-class="marker" style="color: #606266"></svg-icon>
        <span class="font-small">库存扣除信息</span>
        <el-table
          :data="order.stockDeductionList"
          style="width: 100%;margin-top: 20px" border>
          <el-table-column label="商品名称" align="center">
            <template slot-scope="scope">
              {{scope.row.productName}}
            </template>
          </el-table-column>
          <el-table-column label="SKU编码" width="150" align="center">
            <template slot-scope="scope">
              {{scope.row.skuCode || '-'}}
            </template>
          </el-table-column>
          <el-table-column label="扣除门店" width="150" align="center">
            <template slot-scope="scope">
              <el-tag :type="scope.row.storeType === 'WAREHOUSE' ? 'warning' : 'primary'" size="small">
                {{scope.row.storeName}}
              </el-tag>
              <span v-if="scope.row.storeType === 'WAREHOUSE'" style="font-size: 12px; color: #E6A23C;">（地库）</span>
            </template>
          </el-table-column>
          <el-table-column label="扣除数量" width="100" align="center">
            <template slot-scope="scope">
              <span class="color-danger">-{{scope.row.deductQuantity}}</span>
            </template>
          </el-table-column>
          <el-table-column label="库存变化" width="150" align="center">
            <template slot-scope="scope">
              {{scope.row.beforeStock}} → {{scope.row.afterStock}}
            </template>
          </el-table-column>
          <el-table-column label="操作时间" width="160" align="center">
            <template slot-scope="scope">
              {{formatTime(scope.row.operationTime)}}
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 移动端商品信息 -->
      <div v-if="isMobile" class="mobile-products-section">
        <van-card class="products-card">
          <template #title>
            <div class="products-title">
              <i class="el-icon-goods"></i>
              <span>商品信息</span>
            </div>
          </template>
          <template #desc>
            <div class="mobile-product-list">
              <van-card
                v-for="(item, index) in order.orderItemList"
                :key="index"
                :num="item.productQuantity"
                :price="item.productPrice"
                :title="item.productName"
                :thumb="item.productPic"
                class="product-item-card"
              >
                <template #tags>
                  <van-tag plain type="primary">{{item.productBrand}}</van-tag>
                </template>
                <template #footer>
                  <van-cell-group>
                    <van-cell title="货号" :value="item.productSn" />
                    <van-cell title="属性" :value="item.productAttr | formatProductAttr" />
                    <van-cell title="小计" :value="'￥' + (item.productPrice * item.productQuantity)" />
                  </van-cell-group>
                </template>
              </van-card>
            </div>
            <van-divider />
            <div class="total-amount">
              <van-cell title="订单合计" :value="formatOrderAmount(order)" />
            </div>
          </template>
        </van-card>
      </div>

      <!-- 移动端库存扣除信息 -->
      <div v-if="isMobile && order.stockDeductionList && order.stockDeductionList.length > 0" class="mobile-stock-deduction-section">
        <van-card class="stock-deduction-card">
          <template #title>
            <div class="stock-deduction-title">
              <i class="el-icon-box"></i>
              <span>库存扣除信息</span>
            </div>
          </template>
          <template #desc>
            <div class="mobile-stock-deduction-list">
              <van-card
                v-for="(item, index) in order.stockDeductionList"
                :key="index"
                class="stock-deduction-item-card"
              >
                <template #title>
                  <div class="stock-deduction-item-header">
                    <span>{{item.productName}}</span>
                    <van-tag :type="item.storeType === 'WAREHOUSE' ? 'warning' : 'primary'" size="small">
                      {{item.storeName}}
                    </van-tag>
                  </div>
                </template>
                <template #desc>
                  <van-cell-group>
                    <van-cell title="SKU编码" :value="item.skuCode || '-'" />
                    <van-cell title="扣除数量">
                      <template #value>
                        <span style="color: #F56C6C;">-{{item.deductQuantity}}</span>
                      </template>
                    </van-cell>
                    <van-cell title="库存变化" :value="item.beforeStock + ' → ' + item.afterStock" />
                    <van-cell title="操作时间" :value="formatTime(item.operationTime)" />
                  </van-cell-group>
                </template>
              </van-card>
            </div>
          </template>
        </van-card>
      </div>

      <!-- 售后申请信息区域 -->
      <div v-if="order.returnApplyList && order.returnApplyList.length > 0" style="margin-top: 40px">
        <!-- 桌面端售后申请信息 -->
        <div v-if="!isMobile">
          <svg-icon icon-class="marker" style="color: #606266"></svg-icon>
          <span class="font-small">售后申请信息</span>
          <el-table
            :data="order.returnApplyList"
            style="width: 100%;margin-top: 20px" border>
            <el-table-column label="申请编号" width="120" align="center">
              <template slot-scope="scope">{{scope.row.id}}</template>
            </el-table-column>
            <el-table-column label="申请时间" width="180" align="center">
              <template slot-scope="scope">{{scope.row.createTime | formatCreateTime}}</template>
            </el-table-column>
            <el-table-column label="申请状态" width="120" align="center">
              <template slot-scope="scope">
                <el-tag :type="getReturnApplyStatusTagType(scope.row.status)">
                  {{scope.row.status | formatReturnApplyStatus}}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="申请金额" width="120" align="center">
              <template slot-scope="scope">￥{{scope.row.returnAmount}}</template>
            </el-table-column>
            <el-table-column label="申请原因" align="center">
              <template slot-scope="scope">{{scope.row.reason}}</template>
            </el-table-column>
            <el-table-column label="处理时间" width="180" align="center">
              <template slot-scope="scope">{{scope.row.handleTime | formatCreateTime}}</template>
            </el-table-column>
            <el-table-column label="操作" width="120" align="center">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="primary"
                  @click="handleViewReturnApplyDetail(scope.row)">
                  查看详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 移动端售后申请信息 -->
        <div v-else class="mobile-return-apply-section">
          <van-card class="return-apply-card">
            <template #title>
              <div class="return-apply-title">
                <i class="el-icon-warning"></i>
                <span>售后申请信息</span>
              </div>
            </template>
            <template #desc>
              <div class="mobile-return-apply-list">
                <van-card
                  v-for="(item, index) in order.returnApplyList"
                  :key="index"
                  class="return-apply-item-card"
                >
                  <template #title>
                    <div class="return-apply-item-header">
                      <span>申请编号：{{item.id}}</span>
                      <van-tag :type="getReturnApplyStatusTagType(item.status)">
                        {{item.status | formatReturnApplyStatus}}
                      </van-tag>
                    </div>
                  </template>
                  <template #desc>
                    <van-cell-group>
                      <van-cell title="申请时间" :value="item.createTime | formatCreateTime" />
                      <van-cell title="申请金额" :value="'￥' + item.returnAmount" />
                      <van-cell title="申请原因" :value="item.reason" />
                      <van-cell v-if="item.handleTime" title="处理时间" :value="item.handleTime | formatCreateTime" />
                    </van-cell-group>
                  </template>
                  <template #footer>
                    <van-button
                      size="small"
                      type="primary"
                      @click="handleViewReturnApplyDetail(item)">
                      查看详情
                    </van-button>
                  </template>
                </van-card>
              </div>
            </template>
          </van-card>
        </div>
      </div>

      <!-- 评论信息区域 -->
      <div style="margin-top: 60px" v-if="order.commentList && order.commentList.length > 0">
        <svg-icon icon-class="marker" style="color: #606266"></svg-icon>
        <span class="font-small">客户评价</span>
        <el-table
          :data="order.commentList"
          style="width: 100%;margin-top: 20px" border>
          <el-table-column label="商品信息" width="200" align="center">
            <template slot-scope="scope">
              <div>
                <div style="font-weight: bold;">{{scope.row.productName || '商品ID: ' + scope.row.productId}}</div>
                <div style="color: #999; font-size: 12px;">ID: {{scope.row.productId}}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="评价用户" width="120" align="center">
            <template slot-scope="scope">
              <div style="display: flex; align-items: center; justify-content: center;">
                <img v-if="scope.row.memberIcon" 
                     :src="scope.row.memberIcon" 
                     style="width: 30px; height: 30px; border-radius: 50%; margin-right: 8px;">
                <div>
                  <div>{{scope.row.memberNickName}}</div>
                  <div v-if="scope.row.replayCount > 0" style="color: #999; font-size: 12px;">
                    {{scope.row.replayCount}}条回复
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="评分" width="180" align="center">
            <template slot-scope="scope">
              <el-rate 
                v-model="scope.row.star" 
                disabled 
                show-score
                text-color="#ff9900">
              </el-rate>
            </template>
          </el-table-column>
          <el-table-column label="评价内容" align="center">
            <template slot-scope="scope">
              <div style="max-width: 300px; word-break: break-all;">
                {{scope.row.content}}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="评价图片" width="100" align="center">
            <template slot-scope="scope">
              <div v-if="scope.row.pics">
                <img 
                  v-for="(pic, index) in getPicList(scope.row.pics)" 
                  :key="index"
                  :src="pic" 
                  style="height: 50px; margin: 2px; cursor: pointer;"
                  @click="previewImage(pic)">
              </div>
              <span v-else>无图片</span>
            </template>
          </el-table-column>
          <el-table-column label="评价时间" width="160" align="center">
            <template slot-scope="scope">
              {{formatTime(scope.row.createTime)}}
            </template>
          </el-table-column>
          <el-table-column label="显示状态" width="80" align="center">
            <template slot-scope="scope">
              <el-tag :type="scope.row.showStatus === 1 ? 'success' : 'danger'">
                {{scope.row.showStatus === 1 ? '显示' : '隐藏'}}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div style="margin-top: 60px">
        <svg-icon icon-class="marker" style="color: #606266"></svg-icon>
        <span class="font-small">费用信息</span>
      </div>
      <div class="table-layout">
        <el-row>
          <el-col :span="6" class="table-cell-title">商品合计</el-col>
          <el-col :span="6" class="table-cell-title">运费</el-col>
          <el-col :span="6" class="table-cell-title">优惠券</el-col>
          <el-col :span="6" class="table-cell-title">积分抵扣</el-col>
        </el-row>
        <el-row>
          <el-col :span="6" class="table-cell">￥{{order.totalAmount}}</el-col>
          <el-col :span="6" class="table-cell">￥{{order.freightAmount}}</el-col>
          <el-col :span="6" class="table-cell">-￥{{order.couponAmount}}</el-col>
          <el-col :span="6" class="table-cell">-￥{{order.integrationAmount}}</el-col>
        </el-row>
        <el-row>
          <el-col :span="6" class="table-cell-title">活动优惠</el-col>
          <el-col :span="6" class="table-cell-title">折扣金额</el-col>
          <el-col :span="6" class="table-cell-title">订单总金额</el-col>
          <el-col :span="6" class="table-cell-title">应付款金额</el-col>
        </el-row>
        <el-row>
          <el-col :span="6" class="table-cell">-￥{{order.promotionAmount}}</el-col>
          <el-col :span="6" class="table-cell">-￥{{order.discountAmount}}</el-col>
          <el-col :span="6" class="table-cell">
            <span class="color-danger">￥{{order.totalAmount+order.freightAmount}}</span>
          </el-col>
          <el-col :span="6" class="table-cell">
            <span class="color-danger">￥{{order.payAmount+order.freightAmount-order.discountAmount}}</span>
          </el-col>
        </el-row>
      </div>
      <div style="margin-top: 20px">
        <svg-icon icon-class="marker" style="color: #606266"></svg-icon>
        <span class="font-small">操作信息</span>
      </div>
      <el-table style="margin-top: 20px;width: 100%"
                ref="orderHistoryTable"
                :data="order.historyList" border>
        <el-table-column label="操作者"  width="120" align="center">
          <template slot-scope="scope">
            {{scope.row.operateMan}}
          </template>
        </el-table-column>
        <el-table-column label="操作时间"  width="160" align="center">
          <template slot-scope="scope">
            {{formatTime(scope.row.createTime)}}
          </template>
        </el-table-column>
        <el-table-column label="订单状态"  width="120" align="center">
          <template slot-scope="scope">
            {{scope.row.orderStatus | formatStatus}}
          </template>
        </el-table-column>
        <el-table-column label="付款状态"  width="120" align="center">
          <template slot-scope="scope">
            {{scope.row.orderStatus | formatPayStatus}}
          </template>
        </el-table-column>
        <el-table-column label="发货状态"  width="120" align="center">
          <template slot-scope="scope">
            {{scope.row.orderStatus | formatDeliverStatus}}
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center">
          <template slot-scope="scope">
            {{scope.row.note}}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog title="修改收货人信息"
               :visible.sync="receiverDialogVisible"
               width="40%">
      <el-form :model="receiverInfo"
               ref="receiverInfoForm"
               label-width="150px">
        <el-form-item label="收货人姓名：">
          <el-input v-model="receiverInfo.receiverName" style="width: 200px"></el-input>
        </el-form-item>
        <el-form-item label="手机号码：">
          <el-input v-model="receiverInfo.receiverPhone" style="width: 200px">
          </el-input>
        </el-form-item>
        <el-form-item label="邮政编码：">
          <el-input v-model="receiverInfo.receiverPostCode" style="width: 200px">
          </el-input>
        </el-form-item>
        <el-form-item label="所在区域：">
          <v-distpicker :province="receiverInfo.receiverProvince"
                        :city="receiverInfo.receiverCity"
                        :area="receiverInfo.receiverRegion"
                        @selected="onSelectRegion"></v-distpicker>
        </el-form-item>
        <el-form-item label="详细地址：">
          <el-input v-model="receiverInfo.receiverDetailAddress" type="textarea" rows="3">
          </el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
      <el-button @click="receiverDialogVisible = false">取 消</el-button>
      <el-button type="primary" @click="handleUpdateReceiverInfo">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog title="修改费用信息"
               :visible.sync="moneyDialogVisible"
               width="40%">
      <div class="table-layout">
        <el-row>
          <el-col :span="6" class="table-cell-title">商品合计</el-col>
          <el-col :span="6" class="table-cell-title">运费</el-col>
          <el-col :span="6" class="table-cell-title">优惠券</el-col>
          <el-col :span="6" class="table-cell-title">积分抵扣</el-col>
        </el-row>
        <el-row>
          <el-col :span="6" class="table-cell">￥{{order.totalAmount}}</el-col>
          <el-col :span="6" class="table-cell">
            <el-input v-model.number="moneyInfo.freightAmount" size="mini"><template slot="prepend">￥</template></el-input>
          </el-col>
          <el-col :span="6" class="table-cell">-￥{{order.couponAmount}}</el-col>
          <el-col :span="6" class="table-cell">-￥{{order.integrationAmount}}</el-col>
        </el-row>
        <el-row>
          <el-col :span="6" class="table-cell-title">活动优惠</el-col>
          <el-col :span="6" class="table-cell-title">折扣金额</el-col>
          <el-col :span="6" class="table-cell-title">订单总金额</el-col>
          <el-col :span="6" class="table-cell-title">应付款金额</el-col>
        </el-row>
        <el-row>
          <el-col :span="6" class="table-cell">-￥{{order.promotionAmount}}</el-col>
          <el-col :span="6" class="table-cell">
            <el-input v-model.number="moneyInfo.discountAmount" size="mini"><template slot="prepend">-￥</template></el-input>
          </el-col>
          <el-col :span="6" class="table-cell">
            <span class="color-danger">￥{{order.totalAmount+moneyInfo.freightAmount}}</span>
          </el-col>
          <el-col :span="6" class="table-cell">
            <span class="color-danger">￥{{order.payAmount+moneyInfo.freightAmount-moneyInfo.discountAmount}}</span>
          </el-col>
        </el-row>
      </div>
      <span slot="footer" class="dialog-footer">
      <el-button @click="moneyDialogVisible = false">取 消</el-button>
      <el-button type="primary" @click="handleUpdateMoneyInfo">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog title="发送站内信"
               :visible.sync="messageDialogVisible"
               width="40%">
      <el-form :model="message"
               ref="receiverInfoForm"
               label-width="150px">
        <el-form-item label="标题：">
          <el-input v-model="message.title" style="width: 200px"></el-input>
        </el-form-item>
        <el-form-item label="内容：">
          <el-input v-model="message.content" type="textarea" rows="3">
          </el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="messageDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleSendMessage">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog title="关闭订单"
               :visible.sync="closeDialogVisible"
               width="40%">
      <el-form :model="closeInfo"
               label-width="150px">
        <el-form-item label="操作备注：">
          <el-input v-model="closeInfo.note" type="textarea" rows="3">
          </el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closeDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleCloseOrder">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog title="备注订单"
               :visible.sync="markOrderDialogVisible"
               width="40%">
      <el-form :model="markInfo"
               label-width="150px">
        <el-form-item label="操作备注：">
          <el-input v-model="markInfo.note" type="textarea" rows="3">
          </el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="markOrderDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleMarkOrder">确 定</el-button>
      </span>
    </el-dialog>
    <logistics-dialog v-model="logisticsDialogVisible"></logistics-dialog>
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
            <span>{{order.orderSn}}</span>
          </el-form-item>
          <el-form-item label="订单金额：">
            <span style="color: #f56c6c; font-weight: bold;">¥{{order.payAmount}}</span>
          </el-form-item>
          <el-form-item label="支付方式：">
            <el-tag :type="getPayTypeTagType(order.payType)">{{order.payType | formatPayType}}</el-tag>
          </el-form-item>
          <el-form-item label="退款金额：" required>
            <el-input-number
              v-model="refundInfo.refundAmount"
              :min="0.01"
              :max="order.payAmount"
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
        <el-button type="warning" @click="handleRefundOrder" :loading="refundLoading">确认退款</el-button>
      </span>
    </el-dialog>
    <!-- 核销订单弹窗已隐藏 -->
    <!-- <el-dialog title="核销订单"
               :visible.sync="pickupDialogVisible"
               width="40%">
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
        <el-button type="primary" @click="handlePickupOrder">确 定</el-button>
      </span>
    </el-dialog> -->
  </div>
</template>
<script>
  import {getOrderDetail,updateReceiverInfo,updateMoneyInfo,closeOrder,updateOrderNote,deleteOrder,pickupOrder,refundOrder} from '@/api/order';
  import LogisticsDialog from '@/views/oms/order/components/logisticsDialog';
  import {formatDate} from '@/utils/date';
  import VDistpicker from 'v-distpicker';
  const defaultReceiverInfo = {
    orderId:null,
    receiverName:null,
    receiverPhone:null,
    receiverPostCode:null,
    receiverDetailAddress:null,
    receiverProvince:null,
    receiverCity:null,
    receiverRegion:null,
    status:null
  };
  export default {
    name: 'orderDetail',
    components: { VDistpicker, LogisticsDialog},
    data() {
      return {
        id: null,
        order: {},
        receiverDialogVisible:false,
        receiverInfo:Object.assign({},defaultReceiverInfo),
        moneyDialogVisible:false,
        moneyInfo:{orderId:null, freightAmount:0, discountAmount:0,status:null},
        messageDialogVisible:false,
        message: {title:null, content:null},
        closeDialogVisible:false,
        closeInfo:{note:null,id:null},
        markOrderDialogVisible:false,
        markInfo:{note:null},
        logisticsDialogVisible:false,
        pickupDialogVisible:false,
        pickupInfo:{
          pickupCode:'',
          operator:'后台管理员'
        },
        isMobile: false, // 移动端检测
        // 退款相关
        refundDialogVisible: false,
        refundInfo: {
          orderId: null,
          refundAmount: 0,
          refundReason: '',
          operator: '后台管理员'
        },
        refundLoading: false
      }
    },
    computed: {
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
    created() {
      this.id = this.list = this.$route.query.id;
      getOrderDetail(this.id).then(response => {
        this.order = response.data;
      });
      // 检测移动端
      this.checkMobile();
      window.addEventListener('resize', this.checkMobile);
    },
    beforeDestroy() {
      window.removeEventListener('resize', this.checkMobile);
    },
    filters: {
      formatNull(value) {
        if(value===undefined||value===null||value===''){
          return '暂无';
        }else{
          return value;
        }
      },
      formatLongText(value) {
        if(value===undefined||value===null||value===''){
          return '暂无';
        }else if(value.length>8){
          return value.substr(0, 8) + '...';
        }else{
          return value;
        }
      },
      formatPayType(value) {
        if (value === 1) {
          return '支付宝';
        } else if (value === 2) {
          return '微信';
        } else if (value === 3) {
          return '余额支付';
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
      formatOrderType(value) {
        if (value === 1) {
          return '秒杀订单';
        } else {
          return '正常订单';
        }
      },
      formatDeliveryType(value) {
        if (value === 1) {
          return '到店自提';
        } else {
          return '快递配送';
        }
      },
      formatDeliveryMethod(value) {
        if (value === 1) {
          return '快递配送';
        } else if (value === 2) {
          return '门店自提';
        } else if (value === 3) {
          return '虚拟发货';
        } else {
          return '快递配送';
        }
      },
      formatAddress(order) {
        let str = order.receiverProvince;
        if (order.receiverCity != null) {
          str += "  " + order.receiverCity;
        }
        str += "  " + order.receiverRegion;
        str += "  " + order.receiverDetailAddress;
        return str;
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
      formatPayStatus(value) {
        if (value === 0) {
          return '未支付';
        } else if(value===4){
          return '已退款';
        }else{
          return '已支付';
        }
      },
      formatDeliverStatus(value) {
        if (value === 0||value === 1) {
          return '未发货';
        } else {
          return '已发货';
        }
      },
      formatProductAttr(value){
        if(value==null || value === '' || value === undefined){
          return '';
        }
        try {
          let attr = JSON.parse(value);
          if(!Array.isArray(attr)) {
            return '';
          }
          let result='';
          for(let i=0;i<attr.length;i++){
            result+=attr[i].key;
            result+=":";
            result+=attr[i].value;
            result+=";";
          }
          return result;
        } catch (error) {
          console.warn('解析商品属性JSON失败:', value, error);
          return '';
        }
      },
      formatReturnApplyStatus(value) {
        if (value === 0) {
          return '待处理';
        } else if (value === 1) {
          return '退货中';
        } else if (value === 2) {
          return '已完成';
        } else if (value === 3) {
          return '已拒绝';
        } else {
          return '未知状态';
        }
      },
      formatCreateTime(value) {
        if (!value) return '';
        const date = new Date(value);
        return date.toLocaleString('zh-CN', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit'
        });
      }
    },
    methods: {
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
      onSelectRegion(data){
        this.receiverInfo.receiverProvince=data.province.value;
        this.receiverInfo.receiverCity=data.city.value;
        this.receiverInfo.receiverRegion=data.area.value;
      },
      formatTime(time) {
        if (time == null || time === '') {
          return '';
        }
        let date = new Date(time);
        return formatDate(date, 'yyyy-MM-dd hh:mm:ss')
      },
      formatStepStatus(value) {
        if (value === 1) {
          //待发货
          return 2;
        } else if (value === 2) {
          //已发货
          return 3;
        } else if (value === 3) {
          //已完成
          return 4;
        }else {
          //待付款、已关闭、无限订单
          return 1;
        }
      },
      showUpdateReceiverDialog(){
        this.receiverDialogVisible=true;
        this.receiverInfo={
          orderId:this.order.id,
          receiverName:this.order.receiverName,
          receiverPhone:this.order.receiverPhone,
          receiverPostCode:this.order.receiverPostCode,
          receiverDetailAddress:this.order.receiverDetailAddress,
          receiverProvince:this.order.receiverProvince,
          receiverCity:this.order.receiverCity,
          receiverRegion:this.order.receiverRegion,
          status:this.order.status
        }
      },
      handleUpdateReceiverInfo(){
        this.$confirm('是否要修改收货信息?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          updateReceiverInfo(this.receiverInfo).then(response=>{
            this.receiverDialogVisible=false;
            this.$message({
              type: 'success',
              message: '修改成功!'
            });
            getOrderDetail(this.id).then(response => {
              this.order = response.data;
            });
          });
        });
      },
      showUpdateMoneyDialog(){
        this.moneyDialogVisible=true;
        this.moneyInfo.orderId=this.order.id;
        this.moneyInfo.freightAmount=this.order.freightAmount;
        this.moneyInfo.discountAmount=this.order.discountAmount;
        this.moneyInfo.status=this.order.status;
      },
      handleUpdateMoneyInfo(){
        this.$confirm('是否要修改费用信息?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          updateMoneyInfo(this.moneyInfo).then(response=>{
            this.moneyDialogVisible=false;
            this.$message({
              type: 'success',
              message: '修改成功!'
            });
            getOrderDetail(this.id).then(response => {
              this.order = response.data;
            });
          });
        });
      },
      showMessageDialog(){
        this.messageDialogVisible=true;
        this.message.title=null;
        this.message.content=null;
      },
      handleSendMessage(){
        this.$confirm('是否要发送站内信?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.messageDialogVisible=false;
          this.$message({
            type: 'success',
            message: '发送成功!'
          });
        });
      },
      showCloseOrderDialog(){
        this.closeDialogVisible=true;
        this.closeInfo.note=null;
        this.closeInfo.id=this.id;
      },
      handleCloseOrder(){
        this.$confirm('是否要关闭?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
            let params = new URLSearchParams();
            params.append("ids",[this.closeInfo.id]);
            params.append("note",this.closeInfo.note);
            closeOrder(params).then(response=>{
              this.closeDialogVisible=false;
              this.$message({
                type: 'success',
                message: '订单关闭成功!'
              });
              getOrderDetail(this.id).then(response => {
                this.order = response.data;
              });
            });
        });
      },
      showMarkOrderDialog(){
        this.markOrderDialogVisible=true;
        this.markInfo.id=this.id;
        this.closeOrder.note=null;
      },
      handleMarkOrder(){
        this.$confirm('是否要备注订单?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          let params = new URLSearchParams();
          params.append("id",this.markInfo.id);
          params.append("note",this.markInfo.note);
          params.append("status",this.order.status);
          updateOrderNote(params).then(response=>{
            this.markOrderDialogVisible=false;
            this.$message({
              type: 'success',
              message: '订单备注成功!'
            });
            getOrderDetail(this.id).then(response => {
              this.order = response.data;
            });
          });
        });
      },
      handleDeleteOrder(){
        this.$confirm('是否要进行该删除操作?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          let params = new URLSearchParams();
          params.append("ids",[this.id]);
          deleteOrder(params).then(response=>{
            this.$message({
              message: '删除成功！',
              type: 'success',
              duration: 1000
            });
            this.$router.back();
          });
        })
      },
      showLogisticsDialog(){
        this.logisticsDialogVisible=true;
      },
      handleDeliveryOrder(){
        let listItem = this.covertOrder(this.order);
        this.$router.push({path:'/oms/deliverOrderList',query:{list:[listItem]}})
      },
      showPickupDialog(){
        this.pickupDialogVisible=true;
        this.pickupInfo.pickupCode='';
      },
      handlePickupOrder(){
        if(this.pickupInfo.pickupCode==null||this.pickupInfo.pickupCode===''){
          this.$message({
            message: '请输入核销码',
            type: 'warning',
            duration: 1000
          });
          return;
        }
        this.$confirm('是否要核销该订单?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          let params = new URLSearchParams();
          params.append('pickupCode', this.pickupInfo.pickupCode);
          params.append('operator', this.pickupInfo.operator);
          pickupOrder(params).then(response=>{
            this.pickupDialogVisible=false;
            this.pickupInfo.pickupCode='';
            this.$message({
              type: 'success',
              message: '核销成功!'
            });
            getOrderDetail(this.id).then(response => {
              this.order = response.data;
            });
          }).catch(error=>{
            this.$message({
              message: (error.response && error.response.data && error.response.data.message) || '核销失败',
              type: 'error',
              duration: 2000
            });
          });
        });
      },
      handleCopyAddress() {
        const { receiverName, receiverPhone, receiverPostCode } = this.order;
        const address = this.formatFullAddress(this.order);
        const copyContent = `收货人：${receiverName}，手机号：${receiverPhone}，邮编：${receiverPostCode}，地址：${address}`;
        
        this.copyToClipboard(copyContent);
      },
      formatFullAddress(order) {
        let str = order.receiverProvince || '';
        if (order.receiverCity) {
          str += " " + order.receiverCity;
        }
        if (order.receiverRegion) {
          str += " " + order.receiverRegion;
        }
        if (order.receiverDetailAddress) {
          str += " " + order.receiverDetailAddress;
        }
        return str;
      },
      copyToClipboard(text) {
        // 创建临时textarea元素
        const textarea = document.createElement('textarea');
        textarea.value = text;
        document.body.appendChild(textarea);
        textarea.select();

        try {
          document.execCommand('copy');
          this.$message({
            message: '收货人信息已复制到剪贴板',
            type: 'success',
            duration: 1500
          });
        } catch (err) {
          this.$message.error('复制失败，请手动复制');
        } finally {
          document.body.removeChild(textarea);
        }
      },
      // 检测移动端
      checkMobile() {
        this.isMobile = window.innerWidth <= 768;
      },
      // 获取订单状态标签类型
      getStatusTagType(status) {
        const statusMap = {
          0: 'warning', // 待付款
          1: 'primary', // 待发货
          2: 'success', // 已发货
          3: 'success', // 已完成
          4: 'danger'   // 已关闭
        };
        return statusMap[status] || 'default';
      },
      // 获取支付方式标签类型
      getPayTypeTagType(payType) {
        const payTypeMap = {
          0: 'default', // 未支付
          1: 'primary', // 支付宝
          2: 'success', // 微信
          3: 'warning'  // 余额支付
        };
        return payTypeMap[payType] || 'default';
      },
      // 获取订单来源标签类型
      getSourceTypeTagType(sourceType) {
        const sourceTypeMap = {
          0: 'primary', // PC订单
          1: 'success', // 小程序订单
          2: 'warning'  // 自助设备订单
        };
        return sourceTypeMap[sourceType] || 'default';
      },
      // 获取取货方式标签类型
      getDeliveryMethodTagType(deliveryMethod) {
        const deliveryMethodMap = {
          1: 'primary', // 快递配送
          2: 'success', // 门店自提
          3: 'warning'  // 虚拟发货
        };
        return deliveryMethodMap[deliveryMethod] || 'default';
      },
      // 格式化步骤状态
      formatStepStatus(status) {
        // 根据订单状态返回当前步骤
        const stepMap = {
          0: 0, // 待付款 - 提交订单
          1: 1, // 待发货 - 支付订单
          2: 2, // 已发货 - 平台发货
          3: 3, // 已完成 - 确认收货
          4: 0  // 已关闭 - 提交订单
        };
        return stepMap[status] || 0;
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
      // 解析评论图片列表
      getPicList(pics) {
        if (!pics) return [];
        try {
          // 如果是字符串，尝试按逗号分割
          if (typeof pics === 'string') {
            return pics.split(',').filter(pic => pic.trim());
          }
          // 如果是数组，直接返回
          if (Array.isArray(pics)) {
            return pics;
          }
          return [];
        } catch (e) {
          console.error('解析评论图片失败:', e);
          return [];
        }
      },
      // 预览图片
      previewImage(imageUrl) {
        // 使用ElementUI的图片预览功能
        const imgPreview = document.createElement('div');
        imgPreview.innerHTML = `<img src="${imageUrl}" style="max-width: 100%; max-height: 100%;">`;

        this.$alert(imgPreview, '图片预览', {
          dangerouslyUseHTMLString: true,
          showConfirmButton: false,
          closeOnClickModal: true,
          customClass: 'image-preview-dialog'
        });
      },
      // 获取售后申请状态标签类型
      getReturnApplyStatusTagType(status) {
        const statusMap = {
          0: 'warning', // 待处理
          1: 'primary', // 退货中
          2: 'success', // 已完成
          3: 'danger'   // 已拒绝
        };
        return statusMap[status] || 'default';
      },
      // 查看售后申请详情
      handleViewReturnApplyDetail(returnApply) {
        this.$router.push({
          path: '/oms/returnApplyDetail',
          query: {
            id: returnApply.id
          }
        });
      },
      // 显示退款弹窗
      showRefundDialog() {
        this.refundDialogVisible = true;
        this.refundInfo = {
          orderId: this.order.id,
          refundAmount: parseFloat(this.order.payAmount) || 0,
          refundReason: '',
          operator: '后台管理员'
        };
      },
      // 处理退款
      handleRefundOrder() {
        if (!this.refundInfo.refundAmount || this.refundInfo.refundAmount <= 0) {
          this.$message.warning('请输入有效的退款金额');
          return;
        }
        
        if (this.refundInfo.refundAmount > this.order.payAmount) {
          this.$message.warning('退款金额不能超过订单支付金额');
          return;
        }
        
        this.$confirm(`确认退款 ¥${this.refundInfo.refundAmount} 给用户吗？退款将按原支付渠道返回。`, '确认退款', {
          confirmButtonText: '确认退款',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.refundLoading = true;
          refundOrder(this.refundInfo).then(response => {
            this.refundLoading = false;
            this.refundDialogVisible = false;
            this.$message({
              type: 'success',
              message: '退款处理成功！'
            });
            // 刷新订单详情
            getOrderDetail(this.id).then(response => {
              this.order = response.data;
            });
          }).catch(error => {
            this.refundLoading = false;
            this.$message.error((error.response && error.response.data && error.response.data.message) || '退款处理失败');
          });
        }).catch(() => {
          // 取消退款
        });
      }
    }
  }
</script>
<style scoped>
  .detail-container {
    width: 80%;
    padding: 20px 20px 20px 20px;
    margin: 20px auto;
  }

  .operate-container {
    background: #F2F6FC;
    height: 80px;
    margin: -20px -20px 0;
    line-height: 80px;
  }

  .operate-button-container {
    float: right;
    margin-right: 20px
  }

  .table-layout {
    margin-top: 20px;
    border-left: 1px solid #DCDFE6;
    border-top: 1px solid #DCDFE6;
  }

  .table-cell {
    height: 60px;
    line-height: 40px;
    border-right: 1px solid #DCDFE6;
    border-bottom: 1px solid #DCDFE6;
    padding: 10px;
    font-size: 14px;
    color: #606266;
    text-align: center;
    overflow: hidden;
  }

  .table-cell-title {
    border-right: 1px solid #DCDFE6;
    border-bottom: 1px solid #DCDFE6;
    padding: 10px;
    background: #F2F6FC;
    text-align: center;
    font-size: 14px;
    color: #303133;
  }

  /* 图片预览对话框样式 */
  .image-preview-dialog .el-message-box {
    width: auto;
    max-width: 90%;
    max-height: 90%;
  }

  .image-preview-dialog .el-message-box__content {
    text-align: center;
    padding: 20px;
  }

  /* 移动端样式 */
  @media (max-width: 768px) {
    .detail-container {
      padding: 10px;
    }

    /* 移动端卡片通用样式 */
    .mobile-status-section,
    .mobile-operate-section,
    .mobile-info-section,
    .mobile-receiver-section,
    .mobile-products-section,
    .mobile-return-apply-section {
      margin: 15px 0;
    }

    .status-card,
    .operate-card,
    .info-card,
    .receiver-card,
    .products-card,
    .return-apply-card {
      border-radius: 12px;
      box-shadow: 0 2px 12px rgba(0,0,0,0.1);
    }

    /* 卡片标题样式 */
    .status-header,
    .operate-title,
    .info-title,
    .receiver-title,
    .products-title,
    .return-apply-title {
      display: flex;
      align-items: center;
      font-size: 16px;
      font-weight: 600;
      color: #333;
    }

    .status-header i,
    .operate-title i,
    .info-title i,
    .receiver-title i,
    .products-title i,
    .return-apply-title i {
      margin-right: 8px;
      font-size: 18px;
      color: #409eff;
    }

    /* Vant步骤条样式优化 */
    .van-steps--vertical .van-step__title {
      font-size: 14px;
      font-weight: 500;
    }

    .van-steps--vertical .van-step__content {
      font-size: 12px;
      color: #999;
    }

    /* 移动端操作按钮 */
    .mobile-operate-buttons {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      margin-top: 10px;
    }

    .mobile-operate-buttons .van-button {
      flex: 1;
      min-width: calc(50% - 4px);
    }

    /* 收货人信息标题 */
    .receiver-title {
      justify-content: space-between;
      align-items: center;
    }

    .receiver-title .van-button {
      margin-left: auto;
    }

    /* 商品列表样式 */
    .mobile-product-list {
      margin-top: 10px;
    }

    /* 售后申请列表样式 */
    .mobile-return-apply-list {
      margin-top: 10px;
    }

    .return-apply-item-card {
      margin-bottom: 15px;
      border: 1px solid #ebeef5;
      border-radius: 8px;
    }

    .return-apply-item-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 14px;
      font-weight: 500;
    }

    .product-item-card {
      margin-bottom: 15px;
      border-radius: 8px;
      box-shadow: 0 1px 4px rgba(0,0,0,0.1);
    }

    .product-item-card .van-card__thumb {
      width: 80px;
      height: 80px;
      border-radius: 6px;
    }

    .product-item-card .van-card__title {
      font-size: 14px;
      font-weight: 500;
      line-height: 1.4;
    }

    .product-item-card .van-card__price {
      color: #ff6b35;
      font-size: 16px;
      font-weight: 600;
    }

    .product-item-card .van-card__num {
      color: #666;
      font-size: 14px;
    }

    /* 总金额样式 */
    .total-amount .van-cell__value {
      color: #ff6b35;
      font-size: 18px;
      font-weight: 600;
    }

    /* Vant组件样式调整 */
    .van-cell {
      padding: 12px 16px;
    }

    .van-cell__title {
      font-size: 14px;
      color: #333;
    }

    .van-cell__value {
      font-size: 14px;
      color: #666;
    }

    .van-tag {
      font-size: 12px;
      padding: 2px 6px;
    }

    /* 隐藏桌面端组件 */
    .table-layout {
      display: none !important;
    }

    /* 移动端对话框适配 */
    .el-dialog {
      width: 95% !important;
      margin-top: 5vh !important;
    }

    .el-dialog__body {
      padding: 15px 20px;
    }

    /* 移动端表单适配 */
    .el-form-item__label {
      width: 100px !important;
      font-size: 14px;
    }

    .el-input,
    .el-textarea {
      font-size: 14px;
    }

    /* 移动端按钮适配 */
    .el-button--mini {
      padding: 5px 8px;
      font-size: 12px;
    }
  }
</style>


