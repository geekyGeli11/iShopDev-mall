<template>
  <el-dialog :title="dialogTitle" :visible.sync="visible" :before-close="handleClose" width="40%">
    <!-- 快递配送：显示物流轨迹 -->
    <div v-if="deliveryMethod === 1">
      <el-steps direction="vertical" :active="logisticsList.length - 1" finish-status="success" space="50px">
        <el-step v-for="(item, index) in logisticsList" :key="index" :title="item.context" :description="item.time" />
      </el-steps>
    </div>

    <!-- 门店自提：显示提货信息 -->
    <div v-else-if="deliveryMethod === 2" class="pickup-info">
      <el-card>
        <div slot="header">
          <span>门店自提信息</span>
        </div>
        <div class="info-row">
          <span class="label">提货门店：</span>
          <span class="value">{{ orderInfo.deliveryCompany || '待确认' }}</span>
        </div>
        <div class="info-row" v-if="orderInfo.contactPhone">
          <span class="label">联系电话：</span>
          <span class="value">{{ orderInfo.contactPhone }}</span>
        </div>
        <div class="info-row">
          <span class="label">发货状态：</span>
          <span class="value">{{ getPickupStatus() }}</span>
        </div>
        <el-alert
          title="商品已到店，请到指定门店提取商品"
          type="info"
          :closable="false"
          style="margin-top: 15px;">
        </el-alert>
      </el-card>
    </div>

    <!-- 虚拟发货：显示发货信息 -->
    <div v-else-if="deliveryMethod === 3" class="virtual-info">
      <el-card>
        <div slot="header">
          <span>虚拟发货信息</span>
        </div>
        <div class="virtual-content">
          <p>{{ orderInfo.virtualDeliveryInfo || '商品已发货，请查看相关信息' }}</p>
        </div>
        <el-alert
          title="虚拟商品已发货，请按说明使用"
          type="success"
          :closable="false"
          style="margin-top: 15px;">
        </el-alert>
      </el-card>
    </div>

    <!-- 未知发货方式或未发货 -->
    <div v-else class="no-info">
      <el-empty description="暂无物流信息"></el-empty>
    </div>
  </el-dialog>
</template>

<script>
import { getOrderLogisticsInfo, getOrderDetail } from '@/api/order'; // 引入接口方法
import { Message } from 'element-ui'
export default {
  name: 'logisticsDialog',
  props: {
    value: Boolean,
    orderId: Number // 接收父组件传递的订单ID
  },
  computed: {
    visible: {
      get() {
        return this.value;
      },
      set(visible) {
        this.$emit('input', visible); // 关闭弹窗
      }
    },
    dialogTitle() {
      if (this.deliveryMethod === 1) return '物流跟踪';
      if (this.deliveryMethod === 2) return '提货信息';
      if (this.deliveryMethod === 3) return '发货信息';
      return '订单跟踪';
    }
  },
  data() {
    return {
      logisticsList: [], // 初始化为空数组
      deliveryMethod: 1, // 发货方式：1-快递配送，2-门店自提，3-虚拟发货
      orderInfo: {} // 订单信息
    };
  },
  methods: {
    emitInput(val) {
      this.$emit('input', val); // 关闭弹窗
    },
    handleClose() {
      this.emitInput(false);
    },
    async fetchOrderData() {
      try {
        // 先获取订单基本信息，确定发货方式
        const orderResponse = await getOrderDetail(this.orderId);
        if (orderResponse && orderResponse.data) {
          this.orderInfo = orderResponse.data;

          // 判断发货方式的逻辑优化 - 改进检测逻辑
          let deliveryMethod = orderResponse.data.deliveryMethod;

          console.log('订单详情数据:', {
            deliveryMethod: orderResponse.data.deliveryMethod,
            virtualDeliveryInfo: orderResponse.data.virtualDeliveryInfo,
            deliveryCompany: orderResponse.data.deliveryCompany,
            deliverySn: orderResponse.data.deliverySn
          });

          // 优先检查虚拟发货信息（无论deliveryMethod是否有值）
          if (orderResponse.data.virtualDeliveryInfo && orderResponse.data.virtualDeliveryInfo.trim() !== '') {
            deliveryMethod = 3; // 虚拟发货
            console.log('检测到虚拟发货信息，强制设置为虚拟发货');
          }
          // 检查门店自提（有发货公司但没有物流单号）
          else if (orderResponse.data.deliveryCompany && orderResponse.data.deliveryCompany.trim() !== '' &&
                   (!orderResponse.data.deliverySn || orderResponse.data.deliverySn.trim() === '')) {
            deliveryMethod = 2; // 门店自提
            console.log('检测到门店自提，设置为门店自提');
          }
          // 如果没有明确的发货方式，使用默认值
          else if (!deliveryMethod) {
            deliveryMethod = 1; // 默认快递配送
            console.log('使用默认发货方式：快递配送');
          }

          this.deliveryMethod = deliveryMethod;
          console.log('最终确定的发货方式:', this.deliveryMethod);

          // 根据发货方式获取相应数据
          if (this.deliveryMethod === 1) {
            // 快递配送：获取物流轨迹
            await this.fetchLogisticsData();
          }
          // 门店自提和虚拟发货直接使用订单信息，不需要额外请求
        }
      } catch (error) {
        console.error('获取订单信息失败', error);
        Message({
          message: '获取订单信息失败',
          type: 'error',
          duration: 3 * 1000
        });
      }
    },
    async fetchLogisticsData() {
      try {
        const response = await getOrderLogisticsInfo(this.orderId);
        console.log('接口返回的数据:', response);
        if (response && response.data && response.data.logisticsInfo) {
          // 复制原数组并反转（避免修改原数据）
          this.logisticsList = [...response.data.logisticsInfo].reverse();
        } else {
          Message({
            message: response.data.message || '暂无物流信息',
            type: 'info',
            duration: 3 * 1000
          })
        }
      } catch (error) {
        console.error('获取物流信息失败', error);
      }
    },
    getPickupStatus() {
      if (this.orderInfo.status === 1) {
        return '待发货';
      }
      if (this.orderInfo.status === 2) {
        return '已到店，等待提货';
      }
      if (this.orderInfo.status === 3) {
        return '已提货';
      }
      return '未知状态';
    }
  },
  watch: {
    orderId(newOrderId) {
      if (newOrderId) {
        this.logisticsList = [];
        this.orderInfo = {};
        this.deliveryMethod = 1;
        this.fetchOrderData(); // 订单ID发生变化时重新请求订单数据
      }
    }
  }
};
</script>
<style scoped>
.pickup-info .info-row {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.pickup-info .label {
  font-weight: bold;
  min-width: 100px;
  color: #606266;
}

.pickup-info .value {
  flex: 1;
  color: #303133;
}



.virtual-info .virtual-content {
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 4px;
  margin-bottom: 15px;
  line-height: 1.6;
  color: #303133;
}

.no-info {
  text-align: center;
  padding: 40px 0;
}
</style>