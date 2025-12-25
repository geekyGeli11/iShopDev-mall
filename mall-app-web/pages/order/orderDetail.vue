<template>
  <view>
    <!-- 订单状态区域 -->
    <view class="status-section">
      <text class="status-text">{{ orderStatus.text }}</text>

      <!-- 未付款状态 -->
      <text v-if="order.status === 0" class="status-desc">
        请尽快完成支付 剩余{{ formatRemainTime }}将自动取消
      </text>

      <!-- 待发货状态 -->
      <text v-if="order.status === 1" class="status-desc">
        卖家正在处理 请耐心等待发货
      </text>

      <!-- 待收货状态 -->
      <text v-if="order.status === 2" class="status-desc">
        卖家已发货 请注意查收
      </text>

      <!-- 已完成状态 -->
      <text v-if="order.status === 3" class="status-desc">
        收货确认成功
      </text>

      <!-- 已取消状态 -->
      <text v-if="order.status === 4" class="status-desc">
        订单已取消
      </text>

      <view class="g-divider"></view>
    </view>

    <!-- 商品信息区域 -->
    <view class="goods-section">
      <view class="g-header">
        <text class="name">商品信息</text>
      </view>
      <view class="g-divider"></view>

      <view v-for="item in order.orderItemList" :key="item.id" class="g-item">
        <view class="product-container">
          <view class="product-image-container">
            <image class="product-image"
              :src="getDIYPreviewImage(order, item) || item.productPic || 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/order/product_image.png'">
            </image>
            <!-- DIY标识 -->
            <view v-if="isDIYOrder(order)" class="diy-badge">
              <text class="diy-text">DIY</text>
            </view>
          </view>
          <view class="product-info">
            <view class="product-top">
              <text class="product-name">{{ item.productName }}</text>
              <text v-if="isDIYOrder(order)" class="diy-label">个性化定制商品</text>
              <text class="product-spec">{{ formatProductAttr(item.productAttr) }}</text>
            </view>
            <view class="price-quantity">
              <text class="product-price">¥<text class="price-number">{{ parseFloat(item.productPrice).toFixed(2)
                  }}</text>起/份</text>
              <text class="product-quantity">x{{ item.productQuantity }}</text>
            </view>
          </view>
        </view>
      </view>

      <view class="g-divider"></view>
    </view>

    <!-- DIY设计信息区域 -->
    <view v-if="isDIYOrder(order) && order.diyInfo" class="diy-design-section">
      <view class="g-header">
        <text class="name">DIY设计信息</text>
        <text class="diy-status" :class="diyStatusClass">
          {{ getDIYStatusText(order.diyInfo.productionStatus) }}
        </text>
      </view>
      <view class="g-divider"></view>

      <!-- 设计预览图 -->
      <view v-if="order.diyInfo.previewImages" class="diy-preview-section">
        <text class="diy-section-title">设计预览</text>
        <scroll-view class="diy-preview-scroll" scroll-x="true">
          <view class="diy-preview-list">
            <image v-for="(image, index) in getDIYPreviewImages(order.diyInfo)" :key="index" class="diy-preview-image"
              :src="image" mode="aspectFit" @tap="previewDIYImage(image)" />
          </view>
        </scroll-view>
      </view>

      <!-- 设计详情按钮 -->
      <view class="diy-actions">
        <view class="diy-action-btn" @tap="viewDIYDesign">
          <text class="diy-action-text">查看设计详情</text>
        </view>
      </view>

      <view class="g-divider"></view>
    </view>

    <!-- 核销码信息区域（仅自提订单显示） -->
    <view v-if="order.deliveryType === 1 && order.status >= 1 && order.pickupStatus === 0" class="pickup-info-section">
      <view class="pickup-info">
        <view class="pickup-title">取货信息</view>
        <view class="pickup-code-section">
          <view class="pickup-code-label">取货码</view>
          <view class="pickup-code">{{ order.pickupCode || '生成中...' }}</view>
        </view>
        <view class="pickup-qr-section" v-if="qrCodeImage">
          <view class="pickup-qr-label">取货二维码</view>
          <image class="pickup-qr-image" :src="qrCodeImage" mode="aspectFit" />
          <view class="pickup-qr-tip">请向商家出示此二维码完成取货</view>
        </view>
      </view>
    </view>

    <!-- 已核销状态显示 -->
    <view v-if="order.deliveryType === 1 && order.pickupStatus === 1" class="pickup-section completed">
      <view class="pickup-header">
        <text class="pickup-title">取货完成</text>
      </view>
      <view class="g-divider"></view>

      <view class="pickup-completed-info">
        <view class="completed-item">
          <text class="completed-label">核销码：</text>
          <text class="completed-value">{{ order.pickupCode }}</text>
        </view>
        <view class="completed-item">
          <text class="completed-label">取货时间：</text>
          <text class="completed-value">{{ formatOrderTime(order.pickupTime) }}</text>
        </view>
        <view class="completed-item" v-if="order.pickupOperator">
          <text class="completed-label">服务人员：</text>
          <text class="completed-value">{{ order.pickupOperator }}</text>
        </view>
      </view>
    </view>

    <!-- 订单信息区域 -->
    <view class="info-section">
      <view class="info-item">
        <text class="info-label">订单编号</text>
        <view class="info-value-container">
          <text class="info-value">{{ order.orderSn }}</text>
          <view class="copy-icon" @tap="copyOrderSn(order.orderSn)">
            <text class="copy-text">复制</text>
          </view>
        </view>
      </view>
      <view class="info-item">
        <text class="info-label">下单时间</text>
        <text class="info-value">{{ formatOrderTime(order.createTime) }}</text>
      </view>

      <view class="g-divider"></view>
    </view>

    <!-- 物流信息区域（非未付款和已取消状态显示） -->
    <view v-if="order.status !== 0 && order.status !== 4" class="info-section">
      <text class="section-title">物流信息</text>
      <view class="g-divider"></view>

      <!-- 已发货状态 -->
      <view v-if="order.status > 1" class="logistics-info">
        <!-- 快递配送 -->
        <view v-if="order.deliveryMethod === 1 && logisticsInfo.deliveryCompany && logisticsInfo.deliverySn">
          <view class="logistics-row">
            <text class="logistics-label">{{ logisticsInfo.deliveryCompany + '：' }}</text>
            <view class="logistics-value-container">
              <text class="logistics-value">{{ logisticsInfo.deliverySn }}</text>
              <view class="copy-icon" @tap="copyLogisticsSn(logisticsInfo.deliverySn)">
                <text class="copy-text">复制</text>
              </view>
            </view>
          </view>
          <view class="logistics-action" @tap="showLogisticsDetail">
            <text class="action-text">查看物流详情</text>
          </view>
        </view>

        <!-- 门店自提 -->
        <view v-else-if="order.deliveryMethod === 2">
          <view class="pickup-info">
            <view class="pickup-row">
              <text class="pickup-label">提货门店：</text>
              <text class="pickup-value">{{ logisticsInfo.deliveryCompany || '待确认' }}</text>
            </view>
            <view v-if="order.contactPhone" class="pickup-row">
              <text class="pickup-label">联系电话：</text>
              <text class="pickup-value">{{ order.contactPhone }}</text>
            </view>
            <view class="pickup-tip">
              <text class="tip-text">商品已到店，请到门店提取商品</text>
            </view>
          </view>
        </view>

        <!-- 虚拟发货 -->
        <view v-else-if="order.deliveryMethod === 3">
          <view class="virtual-info">
            <view class="virtual-row">
              <text class="virtual-label">发货说明：</text>
            </view>
            <view class="virtual-content">
              <text class="virtual-text">{{ order.virtualDeliveryInfo || '商品已发货，请查看相关信息' }}</text>
            </view>
            <view class="virtual-tip">
              <text class="tip-text">虚拟商品已发货，请按说明使用</text>
            </view>
          </view>
        </view>

        <!-- 没有物流信息或未知发货方式 -->
        <view v-else class="no-logistics">
          <text class="no-logistics-text">暂无物流信息</text>
          <text class="no-logistics-tip">请耐心等待发货</text>
        </view>
      </view>

      <!-- 未发货状态 -->
      <view v-else class="no-logistics">
        <text class="no-logistics-text">暂无物流信息</text>
        <text class="no-logistics-tip">请耐心等待发货</text>
      </view>
    </view>

    <!-- 收货信息区域 -->
    <view class="info-section">
      <text class="section-title">配送信息</text>
      <view class="g-divider"></view>

      <!-- 门店自取信息 -->
      <view v-if="order.deliveryType === 1 && order.storeId" class="store-info">
        <view class="store-row">
          <text class="store-name">{{ order.storeName || '门店信息' }}</text>
          <text class="delivery-type">门店自取</text>
        </view>
        <text class="store-address">
          {{ order.storeAddress || '门店地址待更新' }}
        </text>
        <view v-if="order.storePhone" class="store-phone">
          <text class="store-phone-label">联系电话：</text>
          <text class="store-phone-value">{{ order.storePhone }}</text>
        </view>
        <view v-if="order.storeBusinessHours" class="store-hours">
          <text class="store-hours-label">营业时间：</text>
          <text class="store-hours-value">{{ order.storeBusinessHours }}</text>
        </view>
      </view>

      <!-- 收货地址信息 -->
      <view v-else class="customer-info">
        <view class="customer-row">
          <text class="customer-name">{{ order.receiverName }}</text>
          <text class="customer-phone">{{ order.receiverPhone }}</text>
        </view>
        <text class="customer-address">
          {{ order.receiverProvince + " " + order.receiverCity + " " + order.receiverRegion + " " +
            order.receiverDetailAddress }}
        </text>
      </view>
    </view>

    <!-- 价格信息区域 -->
    <view class="price-section">
      <view class="price-item">
        <text class="price-label">小计</text>
        <text class="price-value">￥{{ parseFloat(order.totalAmount).toFixed(2) }}</text>
      </view>

      <view class="price-item">
        <text class="price-label">运费</text>
        <text class="price-value free">免费配送</text>
      </view>

      <view v-if="order.promotionAmount > 0" class="price-item">
        <text class="price-label">活动优惠</text>
        <text class="price-value red">-￥{{ order.promotionAmount }}</text>
      </view>

      <view v-if="order.couponAmount > 0" class="price-item">
        <text class="price-label">优惠券</text>
        <text class="price-value red">-￥{{ order.couponAmount }}</text>
      </view>

      <!-- 积分使用显示（积分兑换订单） -->
      <view v-if="isPointsExchangeOrder(order) && (order.useIntegration > 0 || order.integration > 0)" class="price-item">
        <text class="price-label">使用积分</text>
        <text class="price-value red">{{ order.useIntegration || order.integration || 0 }}积分</text>
      </view>

      <view class="price-item total">
        <text class="price-label">总计</text>
        <text class="price-value">{{ formatOrderAmount(order) }}</text>
      </view>
    </view>

    <!-- 退货进度区域 -->
    <view v-if="returnApplyList.length > 0" class="return-progress-section">
      <text class="section-title">退货进度</text>
      <view v-for="(returnApply, index) in returnApplyList" :key="returnApply.id" class="return-item">
        <view class="return-header">
          <view class="return-product-info">
            <image class="return-product-image"
              :src="returnApply.productPic || 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/order/product_image.png'"
              mode="aspectFill"></image>
            <view class="return-product-details">
              <text class="return-product-name">{{ returnApply.productName }}</text>
              <text class="return-product-count">退货数量：{{ returnApply.productCount }}</text>
            </view>
          </view>
          <view class="return-status" :style="{ color: getReturnStatusColor(returnApply.status) }">
            {{ getReturnStatusText(returnApply.status) }}
          </view>
        </view>

        <view class="return-info">
          <view class="return-info-item">
            <text class="return-info-label">退货原因：</text>
            <text class="return-info-value">{{ returnApply.reason || '无' }}</text>
          </view>
          <view class="return-info-item">
            <text class="return-info-label">申请时间：</text>
            <text class="return-info-value">{{ formatOrderTime(returnApply.createTime) }}</text>
          </view>
          <view v-if="returnApply.handleTime" class="return-info-item">
            <text class="return-info-label">处理时间：</text>
            <text class="return-info-value">{{ formatOrderTime(returnApply.handleTime) }}</text>
          </view>
          <view v-if="returnApply.description" class="return-info-item">
            <text class="return-info-label">问题描述：</text>
            <text class="return-info-value">{{ returnApply.description }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 备注区域 -->
    <view class="remark-section">
      <text class="section-title">备注</text>
      <view v-if="order.note" class="remark-content">
        <text>{{ order.note }}</text>
      </view>
      <view v-else class="remark-placeholder">
        <text>请填写备注信息</text>
      </view>
    </view>

    <!-- 底部按钮区域 -->
    <view v-if="order.status !== 4" class="footer">
      <!-- 待付款 -->
      <view v-if="order.status === 0" class="footer-content single-btn">
        <button class="primary-btn" @tap="payOrder(order.id)">继续支付</button>
      </view>

      <!-- 待发货 -->
      <view v-if="order.status === 1" class="footer-content single-btn">
        <button class="primary-btn" open-type="contact" @contact="handleContact">联系客服</button>
      </view>

      <!-- 待收货 -->
      <view v-if="order.status === 2" class="footer-content multi-btn">
        <button class="primary-btn" @tap="viewLogistics">查看物流</button>
        <button class="primary-btn" @tap="receiveOrder(order.id)">确认收货</button>
      </view>

      <!-- 已完成 -->
      <view v-if="order.status === 3" class="footer-content multi-btn">
        <button v-if="order.hasReturnApply === 0 && order.sourceType === 1" class="outline-btn"
          @tap="applyAfterSale">申请售后</button>
        <button v-else-if="hasApprovedReturnApply" class="outline-btn" @tap="showDeliveryModal">上传快递信息</button>
        <button v-else-if="hasDeliveryInfo" class="outline-btn" @tap="showDeliveryInfoModal">查看快递信息</button>
        <button v-else-if="order.hasReturnApply > 0" class="outline-btn" @tap="scrollToReturnProgress">查看退货进度</button>
        <!-- <button class="primary-btn" @tap="reviewOrder">评价商品</button> -->
      </view>
    </view>

    <!-- 物流弹窗背景 -->
    <view v-if="showLogisticsPopup" class="logistics-overlay"></view>

    <!-- 物流弹窗 -->
    <view v-if="showLogisticsPopup" class="logistics-popup">
      <view class="popup-content">
        <text class="popup-title">物流详情</text>
        <view class="logistics-list">
          <view v-for="(item, index) in logisticsList" :key="index" class="logistics-item">
            <view class="logistics-details">
              <view style="display:flex;align-items:center;">
                <view class="dot" :class="[index === 0 ? 'solid' : 'hollow']" style="margin-right:10rpx;"></view>
                <text class="logistics-time">{{ item.time }}</text>
              </view>
              <text class="logistics-status" style="padding-left:20rpx;">{{ item.status }}</text>

              <!-- 连接线 -->
              <view v-if="index < logisticsList.length - 1"
                style="height:20rpx;width:2rpx;background-color:#333;margin-left:5rpx;"></view>
            </view>
          </view>
        </view>
        <view class="popup-btn-container">
          <button class="primary-btn close-popup" @tap="closeLogisticsPopup">关闭</button>
        </view>
      </view>
    </view>

    <!-- 快递信息上传弹窗 -->
    <view v-if="showDeliveryPopup" class="delivery-modal-overlay" @tap="hideDeliveryModal">
      <view class="delivery-modal" @tap.stop>
        <view class="modal-header">
          <text class="modal-title">上传快递信息</text>
          <text class="modal-close" @tap="hideDeliveryModal">×</text>
        </view>
        <view class="modal-content">
          <view class="form-item">
            <text class="form-label">快递公司</text>
            <input class="form-input" v-model="deliveryForm.company" placeholder="请输入快递公司名称" />
          </view>
          <view class="form-item">
            <text class="form-label">快递单号</text>
            <input class="form-input" v-model="deliveryForm.trackingNumber" placeholder="请输入快递单号" />
          </view>
        </view>
        <view class="modal-footer">
          <button class="modal-btn cancel-btn" @tap="hideDeliveryModal">取消</button>
          <button class="modal-btn confirm-btn" @tap="submitDeliveryInfo" :disabled="!canSubmitDelivery">确定</button>
        </view>
      </view>
    </view>

    <!-- 查看快递信息弹窗 -->
    <view v-if="showDeliveryInfoPopup" class="delivery-info-modal-overlay" @tap="hideDeliveryInfoModal">
      <view class="delivery-info-modal" @tap.stop>
        <view class="modal-header">
          <text class="modal-title">快递信息</text>
          <text class="modal-close" @tap="hideDeliveryInfoModal">×</text>
        </view>
        <view class="modal-content">
          <view class="info-item">
            <text class="info-label">快递公司：</text>
            <text class="info-value">{{ currentDeliveryInfo.deliveryCompany }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">快递单号：</text>
            <view class="info-value-container">
              <text class="info-value">{{ currentDeliveryInfo.deliverySn }}</text>
              <view class="copy-icon" @tap="copyDeliverySn(currentDeliveryInfo.deliverySn)">
                <text class="copy-text">复制</text>
              </view>
            </view>
          </view>
        </view>
        <view class="modal-footer">
          <button class="modal-btn confirm-btn" @tap="hideDeliveryInfoModal">确定</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { fetchOrderDetail, fetchOrderLogistics, confirmReceiveOrder, getPickupQRCode, fetchOrderReturnApplyList, updateReturnApplyDelivery } from '@/api/order.js';

export default {
  data() {
    return {
      orderId: null,
      order: {},
      hasExpiredChecked: false,
      orderStatus: {
        text: '处理中'
      },
      formatRemainTime: '00:00:00',
      logisticsInfo: {},
      showLogisticsPopup: false,
      logisticsList: [],
      countDownTimer: null,
      qrCodeImage: '', // 核销码QR码图片
      returnApplyList: [], // 退货申请列表
      showDeliveryPopup: false, // 显示快递信息上传弹窗
      showDeliveryInfoPopup: false, // 显示查看快递信息弹窗
      deliveryForm: {
        company: '',
        trackingNumber: ''
      },
      currentDeliveryInfo: { // 当前查看的快递信息
        deliveryCompany: '',
        deliverySn: ''
      }
    };
  },

  computed: {
    // 是否有已通过但未上传快递信息的退货申请（状态为1且无快递信息）
    hasApprovedReturnApply() {
      return this.returnApplyList.some(item =>
        item.status === 1 && (!item.deliveryCompany || !item.deliverySn)
      );
    },

    // 是否有已上传快递信息的退货申请
    hasDeliveryInfo() {
      return this.returnApplyList.some(item =>
        item.status === 1 && item.deliveryCompany && item.deliverySn
      );
    },

    // 是否可以提交快递信息
    canSubmitDelivery() {
      return this.deliveryForm.company.trim() && this.deliveryForm.trackingNumber.trim();
    },

    // DIY状态样式类
    diyStatusClass() {
      if (!this.order || !this.order.diyInfo) {
        return '';
      }
      const status = this.order.diyInfo.productionStatus;
      const classMap = {
        0: 'status-pending',
        1: 'status-processing',
        2: 'status-completed',
        3: 'status-shipped'
      };
      return classMap[status] || '';
    }
  },

  onLoad(options) {
    if (options.id) {
      this.orderId = options.id;
      this.getOrderDetail();
    }
  },

  onUnload() {
    // 清除定时器
    if (this.countDownTimer) {
      clearTimeout(this.countDownTimer);
      this.countDownTimer = null;
    }
  },

  methods: {
    // 获取订单详情
    getOrderDetail() {
      uni.showLoading({
        title: '加载中...'
      });

      fetchOrderDetail(this.orderId).then(res => {
        uni.hideLoading();

        if (res.code === 200 && res.data) {
          this.order = res.data;

          // 设置订单状态
          this.setOrderStatus();

          // 如果是待付款状态，设置倒计时
          if (this.order.status === 0) {
            this.setCountDown();
          }

          // 获取物流信息
          if (this.order.status >= 2) {
            this.getLogisticsInfo();
          }

          // 如果是自提订单且有核销码，获取二维码
          if (this.order.deliveryType === 1 && this.order.pickupCode && this.order.pickupStatus === 0) {
            this.getPickupQRCode();
          }

          // 获取退货申请列表
          this.getReturnApplyList();
        } else {
          uni.showToast({
            title: res.message || '获取订单详情失败',
            icon: 'none'
          });
        }
      }).catch(err => {
        console.error('获取订单详情失败', err);
        uni.hideLoading();

        uni.showToast({
          title: '获取订单详情失败',
          icon: 'none'
        });
      });
    },

    // 判断是否为积分兑换订单
    isPointsExchangeOrder(order) {
      return order.orderType === 2;
    },

    // 格式化订单金额显示
    formatOrderAmount(order) {
      if (this.isPointsExchangeOrder(order)) {
        // 积分兑换订单：显示 "积分 + 支付金额" 格式
        // 优先使用 useIntegration，如果为空则使用 integration 字段
        const pointsUsed = order.useIntegration || order.integration || 0;
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
        return `¥${parseFloat(order.payAmount || 0).toFixed(2)}`;
      }
    },

    // 设置订单状态文本
    setOrderStatus() {
      const status = this.order.status;
      const statusMap = {
        0: '待付款',
        1: '待发货',
        2: '待收货',
        3: '已完成',
        4: '已取消'
      };

      this.orderStatus = {
        text: statusMap[status] || '处理中'
      };
    },

    // 设置倒计时
    setCountDown() {
      // 清除上一个定时器
      if (this.countDownTimer) {
        clearTimeout(this.countDownTimer);
        this.countDownTimer = null;
      }

      const createTime = new Date(this.order.createTime.replace(/-/g, '/'));
      const expireTime = new Date(createTime.getTime() + 120 * 60 * 1000);
      const now = new Date();

      if (expireTime > now) {
        const remainTime = expireTime - now;

        const hours = Math.floor(remainTime / (1000 * 60 * 60));
        const minutes = Math.floor((remainTime % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((remainTime % (1000 * 60)) / 1000);

        this.formatRemainTime =
          String(hours).padStart(2, '0') + ':' +
          String(minutes).padStart(2, '0') + ':' +
          String(seconds).padStart(2, '0');

        // 定时更新
        this.countDownTimer = setTimeout(() => {
          this.setCountDown();
        }, 1000);
      } else {
        this.formatRemainTime = '00:00:00';
        // 订单已过期，仅获取一次最新状态即可
        // 使用标志位避免重复请求
        if (!this.hasExpiredChecked) {
          this.hasExpiredChecked = true;
          this.getOrderDetail(); // 获取最新状态，预期订单状态会变为"已取消"
        }
      }
    },

    // 获取物流信息
    getLogisticsInfo() {
      fetchOrderLogistics(this.orderId).then(res => {
        if (res.code === 200 && res.data) {
          this.logisticsInfo = {
            deliveryCompany: res.data.deliveryCompany || '',
            deliverySn: res.data.deliverySn || ''
          };

          // 修复：使用正确的字段名 logisticsInfo 而不是 list
          const logisticsData = res.data.logisticsInfo || [];

          // 转换数据格式以适应前端显示
          this.logisticsList = logisticsData.map(item => ({
            time: item.time,
            status: item.context // 使用 context 字段作为状态描述
          }));
        } else {
          console.log('获取物流信息失败', res.message);
        }
      }).catch(err => {
        console.error('获取物流信息失败', err);
      });
    },

    // 显示物流详情（仅快递配送）
    showLogisticsDetail() {
      if (this.order.deliveryMethod === 1 && this.logisticsList.length > 0) {
        this.showLogisticsPopup = true;
      } else {
        uni.showToast({
          title: '暂无物流轨迹信息',
          icon: 'none'
        });
      }
    },

    // 获取核销码二维码
    getPickupQRCode() {
      if (!this.order.pickupCode) {
        return;
      }

      getPickupQRCode(this.order.pickupCode).then(res => {
        if (res.code === 200 && res.data) {
          // 确保返回的数据格式正确
          if (res.data.startsWith('data:image/')) {
            this.qrCodeImage = res.data;
          } else {
            this.qrCodeImage = 'data:image/png;base64,' + res.data;
          }
        } else {
          console.log('获取核销码二维码失败', res.message);
        }
      }).catch(err => {
        console.error('获取核销码二维码失败', err);
      });
    },

    // 格式化时间
    formatOrderTime(time) {
      if (!time) return '';
      // 兼容 ISO8601 带时区格式
      const date = new Date(time);
      if (isNaN(date.getTime())) return time;
      const y = date.getFullYear();
      const m = String(date.getMonth() + 1).padStart(2, '0');
      const d = String(date.getDate()).padStart(2, '0');
      const h = String(date.getHours()).padStart(2, '0');
      const min = String(date.getMinutes()).padStart(2, '0');
      const s = String(date.getSeconds()).padStart(2, '0');
      return `${y}-${m}-${d} ${h}:${min}:${s}`;
    },

    // 复制订单编号
    copyOrderSn(orderSn) {
      uni.setClipboardData({
        data: orderSn,
        success: () => {
          uni.showToast({
            title: '复制成功'
          });
        }
      });
    },

    // 复制物流单号
    copyLogisticsSn(deliverySn) {
      uni.setClipboardData({
        data: deliverySn,
        success: () => {
          uni.showToast({
            title: '复制成功'
          });
        }
      });
    },

    // 复制核销码
    copyPickupCode(pickupCode) {
      if (!pickupCode) {
        uni.showToast({
          title: '核销码暂未生成',
          icon: 'none'
        });
        return;
      }

      uni.setClipboardData({
        data: pickupCode,
        success: () => {
          uni.showToast({
            title: '复制成功'
          });
        }
      });
    },

    // 支付订单
    payOrder(orderId) {
      uni.navigateTo({
        url: `/pages/money/pay?id=${orderId}`
      });
    },

    // 查看物流
    viewLogistics() {
      this.showLogisticsPopup = true;
    },

    // 关闭物流弹窗
    closeLogisticsPopup() {
      this.showLogisticsPopup = false;
    },

    // 确认收货
    receiveOrder(orderId) {
      uni.showModal({
        title: '提示',
        content: '确认已收到商品？',
        success: (res) => {
          if (res.confirm) {
            uni.showLoading({
              title: '处理中...'
            });

            confirmReceiveOrder({
              orderId: orderId
            }).then(res => {
              uni.hideLoading();

              if (res.code === 200) {
                uni.showToast({
                  title: '确认收货成功'
                });

                // 刷新订单详情
                this.getOrderDetail();
              } else {
                uni.showToast({
                  title: res.message || '确认收货失败',
                  icon: 'none'
                });
              }
            }).catch(err => {
              console.error('确认收货失败', err);
              uni.hideLoading();

              uni.showToast({
                title: '确认收货失败',
                icon: 'none'
              });
            });
          }
        }
      });
    },

    // 申请售后
    applyAfterSale() {
      // 检查订单来源类型，只有小程序订单才能申请退货
      if (this.order.sourceType !== 1) {
        uni.showToast({
          title: '只有小程序订单才能申请退货',
          icon: 'none'
        });
        return;
      }

      // 检查是否有订单商品
      if (!this.order.orderItemList || this.order.orderItemList.length === 0) {
        uni.showToast({
          title: '订单商品信息异常',
          icon: 'none'
        });
        return;
      }

      // 如果只有一个商品，直接跳转
      if (this.order.orderItemList.length === 1) {
        const orderItem = this.order.orderItemList[0];
        uni.navigateTo({
          url: `/pages/order/returnApply?orderId=${this.order.id}&orderItemId=${orderItem.id}&orderStatus=${this.order.status}`
        });
      } else {
        // 多个商品时，显示选择商品列表
        this.showSelectProductModal();
      }
    },

    // 显示选择商品弹窗
    showSelectProductModal() {
      const itemList = this.order.orderItemList.map(item => item.productName);

      uni.showActionSheet({
        itemList: itemList,
        success: (res) => {
          const selectedItem = this.order.orderItemList[res.tapIndex];
          uni.navigateTo({
            url: `/pages/order/returnApply?orderId=${this.order.id}&orderItemId=${selectedItem.id}&orderStatus=${this.order.status}`
          });
        }
      });
    },

    // 评价商品
    reviewOrder() {
      uni.showToast({
        title: '评价功能暂未开放',
        icon: 'none'
      });
    },

    // 拉起微信小程序客服
    handleContact(e) {
      // 可根据需要处理客服回调事件
    },

    // 获取退货申请列表
    getReturnApplyList() {
      fetchOrderReturnApplyList(this.orderId).then(res => {
        if (res.code === 200 && res.data) {
          this.returnApplyList = res.data;
        } else {
          console.log('获取退货申请列表失败', res.message);
        }
      }).catch(err => {
        console.error('获取退货申请列表失败', err);
      });
    },

    // 获取退货状态文本
    getReturnStatusText(status) {
      const statusMap = {
        0: '待处理',
        1: '退货中',
        2: '已完成',
        3: '已拒绝'
      };
      return statusMap[status] || '未知状态';
    },

    // 获取退货状态颜色
    getReturnStatusColor(status) {
      const colorMap = {
        0: '#FF9500', // 橙色-待处理
        1: '#007AFF', // 蓝色-退货中
        2: '#34C759', // 绿色-已完成
        3: '#FF3B30'  // 红色-已拒绝
      };
      return colorMap[status] || '#999999';
    },

    // 滚动到退货进度区域
    scrollToReturnProgress() {
      uni.pageScrollTo({
        selector: '.return-progress-section',
        duration: 300
      });
    },

    // 处理商品规格格式化
    formatProductAttr(attr) {
      if (!attr) return '';
      let attrObj;
      try {
        attrObj = typeof attr === 'string' ? JSON.parse(attr) : attr;
      } catch (e) {
        return '';
      }
      let attrStr = '';
      if (Array.isArray(attrObj)) {
        for (let item of attrObj) {
          attrStr += item.key + ': ' + item.value + '; ';
        }
      }
      return attrStr;
    },

    // 显示快递信息上传弹窗
    showDeliveryModal() {
      this.showDeliveryPopup = true;
    },

    // 隐藏快递信息上传弹窗
    hideDeliveryModal() {
      this.showDeliveryPopup = false;
      // 清空表单
      this.deliveryForm = {
        company: '',
        trackingNumber: ''
      };
    },

    // 提交快递信息
    submitDeliveryInfo() {
      if (!this.canSubmitDelivery) {
        uni.showToast({
          title: '请填写完整的快递信息',
          icon: 'none'
        });
        return;
      }

      // 找到状态为1的退货申请
      const approvedReturnApply = this.returnApplyList.find(item => item.status === 1);
      if (!approvedReturnApply) {
        uni.showToast({
          title: '未找到已通过的退货申请',
          icon: 'none'
        });
        return;
      }

      uni.showLoading({
        title: '提交中...'
      });

      const deliveryData = {
        returnApplyId: approvedReturnApply.id,
        deliveryCompany: this.deliveryForm.company,
        deliverySn: this.deliveryForm.trackingNumber
      };

      // 调用更新快递信息的API
      updateReturnApplyDelivery(deliveryData).then(res => {
        uni.hideLoading();

        if (res.code === 200) {
          uni.showToast({
            title: '快递信息提交成功',
            icon: 'success'
          });

          // 更新本地数据
          approvedReturnApply.deliveryCompany = this.deliveryForm.company;
          approvedReturnApply.deliverySn = this.deliveryForm.trackingNumber;

          this.hideDeliveryModal();

          // 刷新退货申请列表
          this.getReturnApplyList();
        } else {
          uni.showToast({
            title: res.message || '提交失败',
            icon: 'none'
          });
        }
      }).catch(err => {
        console.error('提交快递信息失败', err);
        uni.hideLoading();

        uni.showToast({
          title: '提交失败',
          icon: 'none'
        });
      });
    },

    // 显示查看快递信息弹窗
    showDeliveryInfoModal() {
      // 找到有快递信息的退货申请
      const deliveryReturnApply = this.returnApplyList.find(item =>
        item.status === 1 && item.deliveryCompany && item.deliverySn
      );

      if (deliveryReturnApply) {
        this.currentDeliveryInfo = {
          deliveryCompany: deliveryReturnApply.deliveryCompany,
          deliverySn: deliveryReturnApply.deliverySn
        };
        this.showDeliveryInfoPopup = true;
      } else {
        uni.showToast({
          title: '未找到快递信息',
          icon: 'none'
        });
      }
    },

    // 隐藏查看快递信息弹窗
    hideDeliveryInfoModal() {
      this.showDeliveryInfoPopup = false;
      this.currentDeliveryInfo = {
        deliveryCompany: '',
        deliverySn: ''
      };
    },

    // 复制快递单号
    copyDeliverySn(deliverySn) {
      if (!deliverySn) {
        uni.showToast({
          title: '快递单号为空',
          icon: 'none'
        });
        return;
      }

      uni.setClipboardData({
        data: deliverySn,
        success: () => {
          uni.showToast({
            title: '复制成功'
          });
        }
      });
    },

    // 判断是否为DIY订单
    isDIYOrder(order) {
      // 检查订单是否包含DIY信息
      if (order.diyInfo) {
        return true;
      }
      // 检查订单项中是否包含DIY标识
      if (order.orderItemList && order.orderItemList.length > 0) {
        return order.orderItemList.some(item =>
          item.productName && (
            item.productName.includes('（定制版）') ||
            item.productName.includes('DIY') ||
            item.productName.includes('定制')
          )
        );
      }
      return false;
    },

    // 获取DIY预览图
    getDIYPreviewImage(order, orderItem) {
      // 如果是DIY订单，尝试获取DIY预览图
      if (this.isDIYOrder(order) && order.diyInfo) {
        // 如果订单包含DIY信息，使用DIY预览图
        if (order.diyInfo.previewImage) {
          return order.diyInfo.previewImage;
        }
        // 如果有最终效果图，使用第一张
        if (order.diyInfo.finalImages) {
          const images = order.diyInfo.finalImages.split(',');
          return images[0];
        }
      }
      return null;
    },

    // 获取DIY预览图列表
    getDIYPreviewImages(diyInfo) {
      const images = [];
      if (diyInfo.previewImage) {
        images.push(diyInfo.previewImage);
      }
      if (diyInfo.finalImages) {
        const finalImages = diyInfo.finalImages.split(',');
        images.push(...finalImages);
      }
      return images;
    },

    // 获取DIY生产状态文本
    getDIYStatusText(status) {
      const statusMap = {
        0: '待生产',
        1: '生产中',
        2: '已完成',
        3: '已发货'
      };
      return statusMap[status] || '未知状态';
    },



    // 预览DIY图片
    previewDIYImage(image) {
      uni.previewImage({
        urls: [image],
        current: image
      });
    },

    // 查看DIY设计详情
    viewDIYDesign() {
      if (this.order.diyInfo && this.order.diyInfo.designId) {
        uni.navigateTo({
          url: `/subpackages/diy/designDetail?designId=${this.order.diyInfo.designId}&orderId=${this.order.id}`
        });
      } else {
        uni.showToast({
          title: '设计信息不完整',
          icon: 'none'
        });
      }
    }
  }
};
</script>

<style lang="scss">
@charset "UTF-8";

/* 页面左右间距 */
/* 文字尺寸 */
/*文字颜色*/
/* 边框颜色 */
/* 图片加载中颜色 */
/* 行为相关颜色 */
page {
  background: #f7f7f7;
  padding-bottom: 120rpx;
}

.status-section {
  width: 750rpx;
  height: 152rpx;
  position: relative;
  background: #FFFFFF;

  .status-text {
    position: absolute;
    height: 48rpx;
    left: 30rpx;
    top: 0rpx;
    font-family: "Source Han Sans SC";
    font-style: normal;
    font-weight: 500;
    font-size: 32rpx;
    line-height: 48rpx;
    display: flex;
    align-items: center;
    color: #000000;
  }

  .status-desc {
    position: absolute;
    height: 36rpx;
    left: 30rpx;
    top: 78rpx;
    font-family: "Source Han Sans SC";
    font-style: normal;
    font-weight: 400;
    font-size: 24rpx;
    line-height: 36rpx;
    color: #000000;
  }
}

.g-divider {
  position: absolute;
  width: 750rpx;
  height: 0rpx;
  left: calc(50% - 375rpx);
  top: 152rpx;
  border: 8rpx solid #F9F9F9;
}

.goods-section {
  background: #FFFFFF;
  padding: 20rpx 30rpx;
  margin-top: 10rpx;
  box-sizing: border-box;

  .g-header {
    display: flex;
    align-items: center;
    padding: 10rpx 0 15rpx;

    .name {
      font-size: 32rpx;
      font-weight: 500;
      font-family: "Source Han Sans SC";
      color: #000000;
    }
  }

  .g-divider {
    width: 100%;
    height: 1rpx;
    background-color: #DDDDDD;
    margin: 0;
    border: none;
  }

  .g-item {
    padding: 20rpx 0;
    position: relative;

    .product-container {
      display: flex;
      width: 100%;
    }

    .product-image-container {
      position: relative;
      width: 240rpx;
      height: 240rpx;
      flex-shrink: 0;
    }

    .product-image {
      width: 240rpx;
      height: 240rpx;
      background-color: #F6F3E8;
      border-radius: 10rpx;
    }

    .diy-badge {
      position: absolute;
      top: 0;
      right: 0;
      background: linear-gradient(135deg, #ff6b6b, #ff8e8e);
      border-radius: 0 10rpx 0 12rpx;
      padding: 6rpx 12rpx;
      z-index: 2;
    }

    .diy-text {
      color: white;
      font-size: 22rpx;
      font-weight: bold;
    }

    .diy-label {
      color: #ff6b6b;
      font-size: 26rpx;
      margin-bottom: 8rpx;
      font-weight: 500;
    }

    .product-info {
      flex: 1;
      padding-left: 20rpx;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
    }

    .product-top {
      display: flex;
      flex-direction: column;
      gap: 4rpx;
    }

    .product-name {
      font-size: 28rpx;
      font-weight: 500;
      font-family: "Source Han Sans SC";
      color: #000000;
      line-height: 1.5;
    }

    .product-spec {
      font-size: 28rpx;
      font-weight: 400;
      font-family: "Source Han Sans SC";
      color: #999999;
    }

    .price-quantity {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-top: 10rpx;
    }

    .product-price {
      font-size: 22rpx;
      font-weight: 400;
      font-family: "Source Han Sans CN";
      color: #000000;
      line-height: 1.5;

      .price-number {
        font-size: 32rpx;
        font-weight: 500;
      }
    }

    .product-quantity {
      font-size: 24rpx;
      font-weight: 400;
      font-family: "Source Han Sans SC";
      color: #000000;
      line-height: 1.6;
    }
  }
}

.info-section {
  background: #FFFFFF;
  padding: 20rpx 30rpx;
  margin-top: 10rpx;

  .section-title {
    font-size: 32rpx;
    font-weight: 500;
    color: #000000;
    margin-bottom: 10rpx;
    padding: 10rpx 0;
  }

  .no-logistics {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 40rpx 0;

    .no-logistics-text {
      font-size: 28rpx;
      font-weight: 500;
      color: #000000;
      margin-bottom: 10rpx;
    }

    .no-logistics-tip {
      font-size: 24rpx;
      font-weight: 400;
      color: #999999;
    }
  }

  .info-item {
    display: flex;
    justify-content: space-between;
    padding: 20rpx 0;
  }

  .info-label {
    font-size: 28rpx;
    color: #000000;
  }

  .info-value-container {
    display: flex;
    align-items: center;
  }

  .info-value {
    font-size: 28rpx;
    color: #000000;
    font-weight: 400;
    text-align: right;
  }

  .copy-icon {
    margin-left: 10rpx;
    padding: 0 10rpx;
    height: 32rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #f5f5f5;
    border-radius: 6rpx;

    .copy-text {
      font-size: 22rpx;
      color: #666666;
    }
  }

  .logistics-info {
    padding: 15rpx 0;

    .logistics-row {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 10rpx 0;
    }

    .logistics-label {
      font-size: 28rpx;
      color: #000000;
      font-weight: 500;
    }

    .logistics-value-container {
      display: flex;
      align-items: center;
    }

    .logistics-value {
      font-size: 28rpx;
      color: #000000;
      margin-right: 10rpx;
    }

    .copy-icon {
      margin-left: 10rpx;
      padding: 0 10rpx;
      height: 32rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      background-color: #f5f5f5;
      border-radius: 6rpx;

      .copy-text {
        font-size: 22rpx;
        color: #666666;
      }
    }

    .logistics-action {
      margin-top: 15rpx;
      text-align: center;
      padding: 10rpx 0;
      border-top: 1rpx solid #F0F0F0;

      .action-text {
        font-size: 26rpx;
        color: #0088FF;
      }
    }
  }

  .pickup-info {
    padding: 15rpx 0;

    .pickup-row {
      display: flex;
      align-items: center;
      margin-bottom: 15rpx;

      &:last-child {
        margin-bottom: 0;
      }
    }

    .pickup-label {
      font-size: 28rpx;
      color: #000000;
      margin-right: 20rpx;
      min-width: 140rpx;
    }

    .pickup-value-container {
      display: flex;
      align-items: center;
      flex: 1;
    }

    .pickup-value {
      font-size: 28rpx;
      color: #666666;
      flex: 1;
    }

    .pickup-tip {
      margin-top: 15rpx;
      padding: 15rpx;
      background: #FFF7E6;
      border-radius: 8rpx;
      border-left: 4rpx solid #FF9500;

      .tip-text {
        font-size: 24rpx;
        color: #FF9500;
      }
    }
  }

  .virtual-info {
    padding: 15rpx 0;

    .virtual-row {
      margin-bottom: 10rpx;
    }

    .virtual-label {
      font-size: 28rpx;
      color: #000000;
    }

    .virtual-content {
      background: #F8F8F8;
      padding: 20rpx;
      border-radius: 8rpx;
      margin-bottom: 15rpx;

      .virtual-text {
        font-size: 26rpx;
        color: #333333;
        line-height: 1.5;
      }
    }

    .virtual-tip {
      padding: 15rpx;
      background: #E8F5E8;
      border-radius: 8rpx;
      border-left: 4rpx solid #52C41A;

      .tip-text {
        font-size: 24rpx;
        color: #52C41A;
      }
    }
  }

  .customer-info {
    padding: 15rpx 0;
    color: #000000;
    font-size: 28rpx;

    .customer-row {
      display: flex;
      margin-bottom: 6rpx;
      margin-bottom: 10rpx;
    }

    .customer-name {
      margin-right: 20rpx;
    }

    .customer-phone {
      color: #666666;
    }

    .customer-address {
      color: #666666;
      line-height: 1.4;
      font-size: 26rpx;
    }
  }

  .store-info {
    padding: 15rpx 0;
    color: #000000;
    font-size: 28rpx;

    .store-row {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 10rpx;
    }

    .store-name {
      font-size: 28rpx;
      font-weight: 500;
      color: #000000;
    }

    .delivery-type {
      font-size: 24rpx;
      color: #0088FF;
      background: #E8F3FF;
      padding: 4rpx 12rpx;
      border-radius: 12rpx;
    }

    .store-address {
      color: #666666;
      line-height: 1.4;
      font-size: 26rpx;
      margin-bottom: 10rpx;
    }

    .store-phone,
    .store-hours {
      display: flex;
      align-items: center;
      margin-bottom: 8rpx;

      &:last-child {
        margin-bottom: 0;
      }
    }

    .store-phone-label,
    .store-hours-label {
      font-size: 26rpx;
      color: #666666;
      margin-right: 10rpx;
      min-width: 100rpx;
    }

    .store-phone-value,
    .store-hours-value {
      font-size: 26rpx;
      color: #000000;
      flex: 1;
    }
  }
}

.pickup-info-section {
  background: #FFFFFF;
  padding: 30rpx;
  margin-top: 10rpx;
  display: flex;
  justify-content: center;
  align-items: center;

  .pickup-info {
    padding: 40rpx;
    background: #f8f9fa;
    border-radius: 16rpx;
    width: 600rpx;
  }

  .pickup-title {
    font-size: 32rpx;
    color: #303133;
    font-weight: bold;
    text-align: center;
    margin-bottom: 30rpx;
  }

  .pickup-code-section {
    text-align: center;
    margin-bottom: 40rpx;
  }

  .pickup-code-label {
    font-size: 28rpx;
    color: #666;
    margin-bottom: 15rpx;
  }

  .pickup-code {
    font-size: 48rpx;
    color: #0088FF;
    font-weight: bold;
    letter-spacing: 8rpx;
    padding: 20rpx 30rpx;
    background: #fff;
    border-radius: 12rpx;
    border: 2rpx solid #0088FF;
    display: inline-block;
  }

  .pickup-qr-section {
    text-align: center;
  }

  .pickup-qr-label {
    font-size: 28rpx;
    color: #666;
    margin-bottom: 20rpx;
  }

  .pickup-qr-image {
    width: 300rpx;
    height: 300rpx;
    background: #fff;
    border-radius: 12rpx;
    border: 2rpx solid #e5e5e5;
    margin-bottom: 20rpx;
  }

  .pickup-qr-tip {
    font-size: 24rpx;
    color: #999;
    line-height: 1.4;
  }
}

.pickup-section {
  background: #FFFFFF;
  padding: 30rpx;
  margin-top: 10rpx;

  &.completed {
    background: #F8F9FA;
  }

  .pickup-completed-info {
    .completed-item {
      display: flex;
      justify-content: space-between;
      margin-bottom: 15rpx;

      .completed-label {
        font-size: 28rpx;
        color: #666666;
      }

      .completed-value {
        font-size: 28rpx;
        color: #333333;
        font-weight: 500;
      }
    }
  }
}

.price-section {
  background: #FFFFFF;
  padding: 30rpx;
  margin-top: 10rpx;

  .price-item {
    display: flex;
    justify-content: space-between;
    margin-bottom: 20rpx;

    .price-label {
      font-size: 28rpx;
      color: #333333;
    }

    .price-value {
      font-size: 28rpx;
      color: #333333;

      &.red {
        color: #FF3D4D;
      }

      &.free {
        color: #15BE77;
      }
    }
  }

  .total {
    margin-top: 20rpx;
    border-top: 1rpx solid #EEEEEE;
    padding-top: 20rpx;

    .price-label {
      font-size: 32rpx;
      font-weight: 500;
    }

    .price-value {
      font-size: 32rpx;
      font-weight: 500;
      color: #FF3D4D;
    }
  }
}

.return-progress-section {
  background: #FFFFFF;
  padding: 30rpx;
  margin-top: 10rpx;

  .section-title {
    font-size: 32rpx;
    font-weight: 500;
    color: #000000;
    margin-bottom: 20rpx;
  }

  .return-item {
    border: 1rpx solid #f0f0f0;
    border-radius: 12rpx;
    padding: 24rpx;
    margin-bottom: 20rpx;

    &:last-child {
      margin-bottom: 0;
    }

    .return-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20rpx;

      .return-product-info {
        display: flex;
        align-items: center;
        flex: 1;

        .return-product-image {
          width: 80rpx;
          height: 80rpx;
          border-radius: 8rpx;
          background: #f5f5f5;
          margin-right: 16rpx;
        }

        .return-product-details {
          flex: 1;

          .return-product-name {
            font-size: 28rpx;
            color: #000000;
            font-weight: 500;
            display: block;
            margin-bottom: 8rpx;
          }

          .return-product-count {
            font-size: 24rpx;
            color: #666666;
            display: block;
          }
        }
      }

      .return-status {
        font-size: 28rpx;
        font-weight: 500;
        padding: 8rpx 16rpx;
        background: rgba(0, 0, 0, 0.05);
        border-radius: 20rpx;
      }
    }

    .return-info {
      .return-info-item {
        display: flex;
        margin-bottom: 12rpx;

        &:last-child {
          margin-bottom: 0;
        }

        .return-info-label {
          width: 140rpx;
          font-size: 26rpx;
          color: #666666;
          flex-shrink: 0;
        }

        .return-info-value {
          flex: 1;
          font-size: 26rpx;
          color: #333333;
          line-height: 1.4;
        }
      }
    }
  }
}

.remark-section {
  background: #FFFFFF;
  padding: 30rpx;
  margin-top: 10rpx;

  .section-title {
    font-size: 32rpx;
    font-weight: 500;
    color: #000000;
    margin-bottom: 20rpx;
  }

  .remark-content {
    font-size: 28rpx;
    color: #333333;
    padding: 10rpx 0;
  }

  .remark-placeholder {
    font-size: 28rpx;
    color: #999999;
    padding: 10rpx 0;
  }
}

.footer {
  position: fixed;
  left: 0;
  bottom: 0;
  width: 100%;
  height: 150rpx;
  background: #FFFFFF;
  box-shadow: 0px -4rpx 16rpx rgba(0, 0, 0, 0.05);
  z-index: 100;
  padding-bottom: 15rpx;

  .footer-content {
    display: flex;
    height: 100%;
    padding: 0 30rpx 30rpx 30rpx;
    align-items: center;

    &.single-btn {
      justify-content: flex-end;
    }

    &.multi-btn {
      justify-content: flex-end;
      gap: 16rpx;
    }
  }

  .primary-btn {
    width: 252rpx;
    height: 84rpx;
    background: #20201E;
    border-radius: 16rpx;
    font-family: 'PingFang SC';
    font-size: 32rpx;
    font-weight: 400;
    color: #A9FF00;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 20rpx 28rpx;
    margin: 0;
    border: none;
  }

  .outline-btn {
    width: 252rpx;
    height: 84rpx;
    background: #FFFFFF;
    border: 1rpx solid #20201E;
    border-radius: 16rpx;
    font-family: 'PingFang SC';
    font-size: 32rpx;
    font-weight: 400;
    color: #20201E;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 20rpx 28rpx;
    margin: 0;
  }
}

.logistics-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1000;
}

.logistics-popup {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 600rpx;
  background: #FFFFFF;
  border-radius: 20rpx;
  z-index: 1001;
  padding: 30rpx;

  .popup-content {
    width: 100%;
  }

  .popup-title {
    font-size: 32rpx;
    font-weight: 500;
    color: #000000;
    text-align: center;
    margin-bottom: 30rpx;
  }

  .logistics-list {
    max-height: 600rpx;
    overflow-y: auto;
  }

  .logistics-item {
    padding: 20rpx 0;

    .logistics-details {
      padding-left: 20rpx;
      position: relative;
    }

    .dot {
      width: 10rpx;
      height: 10rpx;
      border-radius: 50%;
      display: inline-block;

      &.solid {
        background-color: #333;
      }

      &.hollow {
        border: 1rpx solid #333;
        background-color: #fff;
      }
    }

    .logistics-time {
      font-size: 24rpx;
      color: #999999;
      margin-bottom: 10rpx;
    }

    .logistics-status {
      font-size: 28rpx;
      color: #333333;
      padding-left: 40rpx;
    }
  }

  .popup-btn-container {
    margin-top: 30rpx;
    display: flex;
    justify-content: center;
  }

  .close-popup {
    width: 200rpx;
    background: linear-gradient(90deg, #C8EBF8 0%, #E2F1F5 100%);
  }
}

// 快递信息上传弹窗样式
.delivery-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.delivery-modal {
  width: 600rpx;
  background: #ffffff;
  border-radius: 20rpx;
  overflow: hidden;

  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 30rpx;
    border-bottom: 1rpx solid #f0f0f0;

    .modal-title {
      font-size: 32rpx;
      font-weight: 500;
      color: #000000;
    }

    .modal-close {
      font-size: 40rpx;
      color: #999999;
      width: 40rpx;
      height: 40rpx;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }

  .modal-content {
    padding: 30rpx;

    .form-item {
      margin-bottom: 30rpx;

      &:last-child {
        margin-bottom: 0;
      }

      .form-label {
        display: block;
        font-size: 28rpx;
        color: #000000;
        margin-bottom: 15rpx;
      }

      .form-input {
        width: 100%;
        height: 80rpx;
        padding: 0 20rpx;
        background: #f8f9fa;
        border-radius: 12rpx;
        font-size: 28rpx;
        color: #000000;
        border: none;
        box-sizing: border-box;
      }
    }
  }

  .modal-footer {
    display: flex;
    padding: 30rpx;
    gap: 20rpx;
    border-top: 1rpx solid #f0f0f0;

    .modal-btn {
      flex: 1;
      height: 80rpx;
      border-radius: 40rpx;
      font-size: 28rpx;
      font-weight: 400;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0;
      border: none;

      &.cancel-btn {
        background: #f5f5f5;
        color: #666666;
      }

      &.confirm-btn {
        background: linear-gradient(90deg, #C8EBF8 0%, #E2F1F5 100%);
        color: #000000;

        &[disabled] {
          background: #f5f5f5;
          color: #cccccc;
        }
      }
    }
  }
}

// 查看快递信息弹窗样式
.delivery-info-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.delivery-info-modal {
  width: 600rpx;
  background: #ffffff;
  border-radius: 20rpx;
  overflow: hidden;

  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 30rpx;
    border-bottom: 1rpx solid #f0f0f0;

    .modal-title {
      font-size: 32rpx;
      font-weight: 500;
      color: #000000;
    }

    .modal-close {
      font-size: 40rpx;
      color: #999999;
      width: 40rpx;
      height: 40rpx;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }

  .modal-content {
    padding: 30rpx;

    .info-item {
      display: flex;
      align-items: center;
      margin-bottom: 30rpx;

      &:last-child {
        margin-bottom: 0;
      }

      .info-label {
        font-size: 28rpx;
        color: #666666;
        width: 140rpx;
        flex-shrink: 0;
      }

      .info-value {
        font-size: 28rpx;
        color: #000000;
        flex: 1;
      }

      .info-value-container {
        display: flex;
        align-items: center;
        flex: 1;

        .info-value {
          flex: 1;
        }

        .copy-icon {
          margin-left: 10rpx;
          padding: 0 10rpx;
          height: 32rpx;
          display: flex;
          align-items: center;
          justify-content: center;
          background-color: #f5f5f5;
          border-radius: 6rpx;

          .copy-text {
            font-size: 22rpx;
            color: #666666;
          }
        }
      }
    }
  }

  .modal-footer {
    padding: 30rpx;
    border-top: 1rpx solid #f0f0f0;

    .modal-btn {
      width: 100%;
      height: 80rpx;
      border-radius: 40rpx;
      font-size: 28rpx;
      font-weight: 400;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0;
      border: none;

      &.confirm-btn {
        background: linear-gradient(90deg, #C8EBF8 0%, #E2F1F5 100%);
        color: #000000;
      }
    }
  }

  // DIY设计信息区域样式
  .diy-design-section {
    background: white;
    margin-bottom: 20rpx;
    padding: 0 30rpx;

    .diy-status {
      font-size: 26rpx;
      padding: 4rpx 12rpx;
      border-radius: 12rpx;
      margin-left: auto;

      &.status-pending {
        background: #fff3cd;
        color: #856404;
      }

      &.status-processing {
        background: #d1ecf1;
        color: #0c5460;
      }

      &.status-completed {
        background: #d4edda;
        color: #155724;
      }

      &.status-shipped {
        background: #e2e3e5;
        color: #383d41;
      }
    }

    .diy-section-title {
      font-size: 28rpx;
      font-weight: 500;
      color: #333;
      margin-bottom: 20rpx;
      display: block;
    }

    .diy-preview-section {
      margin-bottom: 30rpx;
    }

    .diy-preview-scroll {
      white-space: nowrap;
    }

    .diy-preview-list {
      display: inline-flex;
      gap: 20rpx;
    }

    .diy-preview-image {
      width: 200rpx;
      height: 200rpx;
      border-radius: 10rpx;
      background: #f5f5f5;
    }

    .diy-actions {
      padding: 20rpx 0;
    }

    .diy-action-btn {
      background: linear-gradient(135deg, #ff6b6b, #ff8e8e);
      color: white;
      padding: 20rpx 40rpx;
      border-radius: 25rpx;
      text-align: center;
    }

    .diy-action-text {
      font-size: 28rpx;
      font-weight: 500;
    }
  }
}
</style>