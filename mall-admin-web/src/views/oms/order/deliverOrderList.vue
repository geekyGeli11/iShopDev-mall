<template>
  <div class="app-container">
    <el-card class="operate-container" shadow="never">
      <i class="el-icon-tickets"></i>
      <span>发货列表</span>
    </el-card>
    <div class="table-container">
      <el-table ref="deliverOrderTable" style="width: 100%;" :data="list" border>
        <el-table-column label="订单编号" width="180" align="center">
          <template slot-scope="scope">{{ scope.row.orderSn }}</template>
        </el-table-column>
        <el-table-column label="收货人" width="180" align="center">
          <template slot-scope="scope">{{ scope.row.receiverName }}</template>
        </el-table-column>
        <el-table-column label="手机号码" width="160" align="center">
          <template slot-scope="scope">{{ scope.row.receiverPhone }}</template>
        </el-table-column>
        <el-table-column label="邮政编码" width="160" align="center">
          <template slot-scope="scope">{{ scope.row.receiverPostCode }}</template>
        </el-table-column>
        <el-table-column label="收货地址" align="center">
          <template slot-scope="scope">{{ scope.row.address }}</template>
        </el-table-column>
        <el-table-column label="发货方式" width="140" align="center">
          <template slot-scope="scope">
            <el-select placeholder="请选择发货方式" v-model="scope.row.deliveryMethod" size="small" @change="handleDeliveryMethodChange(scope.row)">
              <el-option label="快递配送" :value="1"></el-option>
              <el-option label="门店自提" :value="2"></el-option>
              <el-option label="虚拟发货" :value="3"></el-option>
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="发货信息" width="300" align="center">
          <template slot-scope="scope">
            <!-- 快递配送 -->
            <div v-if="scope.row.deliveryMethod === 1">
              <el-select placeholder="请选择物流公司" v-model="scope.row.deliveryCompany" size="small" style="width: 120px; margin-bottom: 5px;">
                <el-option v-for="item in companyOptions" :key="item" :label="item" :value="item">
                </el-option>
              </el-select>
              <el-input size="small" v-model="scope.row.deliverySn" placeholder="物流单号" style="width: 150px;"></el-input>
            </div>
            <!-- 门店自提 -->
            <div v-else-if="scope.row.deliveryMethod === 2">
              <el-select size="small" v-model="scope.row.storeId" placeholder="选择门店" style="width: 200px;" @change="handleStoreChange(scope.row)">
                <el-option v-for="store in storeOptions" :key="store.id" :label="store.name" :value="store.id">
                  <span style="float: left">{{ store.name }}</span>
                  <span style="float: right; color: #8492a6; font-size: 13px">{{ store.address }}</span>
                </el-option>
              </el-select>
            </div>
            <!-- 虚拟发货 -->
            <div v-else-if="scope.row.deliveryMethod === 3">
              <el-input size="small" v-model="scope.row.virtualDeliveryInfo" placeholder="发货说明/激活码等" style="width: 280px;" type="textarea" :rows="2"></el-input>
            </div>
            <!-- 默认提示 -->
            <div v-else>
              <span style="color: #999;">请先选择发货方式</span>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 库存扣除信息 -->
      <div v-if="stockDeductionList && stockDeductionList.length > 0" style="margin-top: 20px;">
        <el-card shadow="never">
          <div slot="header">
            <i class="el-icon-box"></i>
            <span style="margin-left: 5px;">库存扣除信息</span>
            <span style="margin-left: 10px; font-size: 12px; color: #909399;">（可改选发货门店）</span>
          </div>
          <el-table :data="stockDeductionList" border size="small">
            <el-table-column label="商品名称" align="center">
              <template slot-scope="scope">{{ scope.row.productName }}</template>
            </el-table-column>
            <el-table-column label="SKU编码" width="150" align="center">
              <template slot-scope="scope">{{ scope.row.skuCode || '-' }}</template>
            </el-table-column>
            <el-table-column label="扣除门店" width="150" align="center">
              <template slot-scope="scope">
                <el-tag :type="scope.row.storeType === 'WAREHOUSE' ? 'warning' : 'primary'" size="small">
                  {{ scope.row.storeName }}
                </el-tag>
                <span v-if="scope.row.storeType === 'WAREHOUSE'" style="font-size: 12px; color: #E6A23C;">（地库）</span>
              </template>
            </el-table-column>
            <el-table-column label="扣除数量" width="100" align="center">
              <template slot-scope="scope">
                <span style="color: #F56C6C;">-{{ scope.row.deductQuantity }}</span>
              </template>
            </el-table-column>
            <el-table-column label="库存变化" width="150" align="center">
              <template slot-scope="scope">
                {{ scope.row.beforeStock }} → {{ scope.row.afterStock }}
              </template>
            </el-table-column>
            <el-table-column label="操作时间" width="160" align="center">
              <template slot-scope="scope">{{ formatTime(scope.row.operationTime) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="100" align="center">
              <template slot-scope="scope">
                <el-button 
                  type="text" 
                  size="small" 
                  @click="showChangeStoreDialog(scope.row)"
                  :disabled="scope.row.operationReason && scope.row.operationReason.includes('【已改选】')">
                  {{ scope.row.operationReason && scope.row.operationReason.includes('【已改选】') ? '已改选' : '改选门店' }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>

      <!-- 改选发货门店对话框 -->
      <el-dialog title="改选发货门店" :visible.sync="changeStoreDialogVisible" width="600px">
        <div v-if="currentDeductionItem">
          <el-descriptions :column="2" border size="small" style="margin-bottom: 20px;">
            <el-descriptions-item label="商品名称">{{ currentDeductionItem.productName }}</el-descriptions-item>
            <el-descriptions-item label="SKU编码">{{ currentDeductionItem.skuCode || '-' }}</el-descriptions-item>
            <el-descriptions-item label="当前发货门店">{{ currentDeductionItem.storeName }}</el-descriptions-item>
            <el-descriptions-item label="扣除数量">{{ currentDeductionItem.deductQuantity }}</el-descriptions-item>
          </el-descriptions>
          
          <div style="margin-bottom: 10px;">
            <span style="font-weight: bold;">选择新的发货门店：</span>
            <span style="color: #909399; font-size: 12px;">（显示各门店该SKU的库存）</span>
          </div>
          
          <el-table :data="storeStockList" border size="small" v-loading="loadingStoreStock">
            <el-table-column label="门店名称" align="center">
              <template slot-scope="scope">
                {{ scope.row.storeName }}
                <el-tag v-if="scope.row.isWarehouse" type="warning" size="mini" style="margin-left: 5px;">地库</el-tag>
                <el-tag v-if="scope.row.storeId === currentDeductionItem.storeId" type="info" size="mini" style="margin-left: 5px;">当前</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="当前库存" width="100" align="center">
              <template slot-scope="scope">
                <span :style="{ color: scope.row.stock >= currentDeductionItem.deductQuantity ? '#67C23A' : '#F56C6C' }">
                  {{ scope.row.stock }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="库存状态" width="100" align="center">
              <template slot-scope="scope">
                <el-tag v-if="scope.row.stock >= currentDeductionItem.deductQuantity" type="success" size="small">充足</el-tag>
                <el-tag v-else type="danger" size="small">不足</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80" align="center">
              <template slot-scope="scope">
                <el-button 
                  type="primary" 
                  size="mini"
                  :disabled="scope.row.storeId === currentDeductionItem.storeId || scope.row.stock < currentDeductionItem.deductQuantity"
                  @click="handleChangeStore(scope.row)">
                  选择
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button @click="changeStoreDialogVisible = false">取 消</el-button>
        </span>
      </el-dialog>

      <div style="margin-top: 15px;text-align: center">
        <el-button @click="cancel">取消</el-button>
        <el-button @click="confirm" type="primary">确定</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { deliveryOrder, getOrderDetail, getStoreStockList, changeDeliveryStore } from '@/api/order';
import { fetchList as fetchStoreList } from '@/api/storeAddress';
import { formatDate } from '@/utils/date';

// 快递公司映射：中文名称 -> 英文简写
const logisticsMapping = {
  "顺丰快递": "shunfeng",
  "圆通快递": "yuantong",
  "中通快递": "zhongtong",
  "韵达快递": "yunda",
  "申通快递": "shentong",
  "极兔速递": "jtexpress",
  "邮政快递包裹": "youzhengguonei",
  "京东物流": "jd",
  "EMS": "ems",
  "菜鸟速递": "danniao",
  "USPS": "usps",
  "中通快运": "zhongtongkuaiyun",
  "德邦物流": "debangwuliu",
  "顺丰快运": "shunfengkuaiyun",
  "跨越速运": "kuayue",
  "京东快运": "jingdongkuaiyun",
  "安能快运": "annengwuliu",
  "京广速递": "jinguangsudikuaijian",
  "百世快运": "baishiwuliu",
  "中通国际": "zhongtongguoji"
};

const defaultLogisticsCompanies = [
  "顺丰快递",
  "圆通快递",
  "中通快递",
  "韵达快递",
  "申通快递",
  "极兔速递",
  "邮政快递包裹",
  "京东物流",
  "EMS",
  "菜鸟速递",
  "USPS",
  "中通快运",
  "德邦物流",
  "顺丰快运",
  "跨越速运",
  "京东快运",
  "安能快运",
  "京广速递",
  "百世快运",
  "中通国际"
];

export default {
  name: 'deliverOrderList',
  data() {
    return {
      list: [], // 发货订单列表
      companyOptions: defaultLogisticsCompanies, // 快递公司选项
      storeOptions: [], // 门店选项（从API获取）
      loadingStores: false, // 门店加载状态
      stockDeductionList: [], // 库存扣除记录
      changeStoreDialogVisible: false, // 改选门店对话框
      currentDeductionItem: null, // 当前选中的库存扣除记录
      storeStockList: [], // 各门店库存列表
      loadingStoreStock: false, // 加载门店库存状态
      orderDetail: null // 订单详情
    };
  },
  created() {
    // 获取从上个页面传递过来的订单数据
    this.list = this.$route.query.list;
    // 如果list不是数组，转换为数组
    if (!(this.list instanceof Array)) {
      this.list = [];
    }
    // 初始化发货方式和相关字段
    this.list.forEach(item => {
      // 如果没有deliveryMethod，根据deliveryType推断
      if (!item.deliveryMethod) {
        if (item.deliveryType === 1) {
          item.deliveryMethod = 2; // 门店自提
        } else {
          item.deliveryMethod = 1; // 默认快递配送
        }
      }
      // 初始化各种发货方式的字段
      if (!item.storeName) item.storeName = '';
      if (!item.storeId) item.storeId = null;
      if (!item.contactPhone) item.contactPhone = '';
      if (!item.virtualDeliveryInfo) item.virtualDeliveryInfo = '';
    });

    // 获取门店列表
    this.fetchStoreOptions();
    // 获取库存扣除记录
    this.fetchStockDeductionList();
  },
  methods: {
    // 处理发货方式变化
    handleDeliveryMethodChange(row) {
      console.log('发货方式切换开始:', row.deliveryMethod, '订单ID:', row.orderId);

      // 找到当前行在列表中的索引
      const index = this.list.findIndex(item => item.orderId === row.orderId);
      if (index === -1) {
        console.error('找不到对应的订单行');
        return;
      }

      // 保存当前选择的发货方式
      const selectedMethod = row.deliveryMethod;

      // 创建完全新的行对象，确保Vue能检测到变化
      const newRow = {
        ...this.list[index],
        deliveryMethod: selectedMethod,
        deliveryCompany: '',
        deliverySn: '',
        storeName: '',
        storeId: null,
        contactPhone: '',
        virtualDeliveryInfo: ''
      };

      // 使用Vue.set替换整行数据，触发响应式更新
      this.$set(this.list, index, newRow);

      // 延迟更新，确保DOM完全重新渲染
      setTimeout(() => {
        this.$forceUpdate();
        console.log('发货方式切换完成，当前方式:', selectedMethod);
        console.log('更新后的行数据:', this.list[index]);
      }, 50);
    },

    // 获取门店选项
    async fetchStoreOptions() {
      try {
        this.loadingStores = true;
        const response = await fetchStoreList({
          pageNum: 1,
          pageSize: 100 // 获取前100个门店
        });

        if (response && response.data && response.data.list) {
          this.storeOptions = response.data.list.map(store => ({
            id: store.id,
            name: store.addressName,
            address: store.detailAddress || store.address,
            phone: store.phone
          }));
          console.log('门店列表加载成功:', this.storeOptions.length, '个门店');
        } else {
          this.$message.warning('未获取到门店数据');
        }
      } catch (error) {
        console.error('获取门店列表失败:', error);
        this.$message.error('获取门店列表失败');
      } finally {
        this.loadingStores = false;
      }
    },

    // 处理门店选择变化
    handleStoreChange(row) {
      const selectedStore = this.storeOptions.find(store => store.id === row.storeId);
      if (selectedStore) {
        this.$set(row, 'storeName', selectedStore.name);
        this.$set(row, 'contactPhone', selectedStore.phone);
      }
    },
    // 获取库存扣除记录
    async fetchStockDeductionList() {
      if (this.list.length === 0) return;
      
      try {
        // 获取第一个订单的详情（通常发货页面只有一个订单）
        const orderId = this.list[0].orderId;
        if (!orderId) return;
        
        const response = await getOrderDetail(orderId);
        if (response && response.data) {
          this.orderDetail = response.data;
          if (response.data.stockDeductionList) {
            this.stockDeductionList = response.data.stockDeductionList;
          }
        }
      } catch (error) {
        console.error('获取库存扣除记录失败:', error);
      }
    },
    // 显示改选门店对话框
    async showChangeStoreDialog(item) {
      this.currentDeductionItem = item;
      this.changeStoreDialogVisible = true;
      this.storeStockList = [];
      
      // 获取各门店该SKU的库存
      try {
        this.loadingStoreStock = true;
        const response = await getStoreStockList(item.skuId);
        if (response && response.data) {
          this.storeStockList = response.data;
        }
      } catch (error) {
        console.error('获取门店库存失败:', error);
        this.$message.error('获取门店库存失败');
      } finally {
        this.loadingStoreStock = false;
      }
    },
    // 处理改选门店
    async handleChangeStore(newStore) {
      if (!this.currentDeductionItem || !this.orderDetail) return;
      
      this.$confirm(`确定要将发货门店从"${this.currentDeductionItem.storeName}"改为"${newStore.storeName}"吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const param = {
            orderId: this.orderDetail.id,
            orderSn: this.orderDetail.orderSn,
            productId: this.currentDeductionItem.productId,
            skuId: this.currentDeductionItem.skuId,
            originalStoreId: this.currentDeductionItem.storeId,
            newStoreId: newStore.storeId,
            quantity: this.currentDeductionItem.deductQuantity
          };
          
          const response = await changeDeliveryStore(param);
          if (response && response.code === 200) {
            this.$message.success('改选发货门店成功');
            this.changeStoreDialogVisible = false;
            // 刷新库存扣除记录
            await this.fetchStockDeductionList();
          } else {
            this.$message.error(response.message || '改选发货门店失败');
          }
        } catch (error) {
          console.error('改选发货门店失败:', error);
          this.$message.error('改选发货门店失败');
        }
      }).catch(() => {
        // 取消操作
      });
    },
    // 格式化时间
    formatTime(time) {
      if (time == null || time === '') {
        return '';
      }
      let date = new Date(time);
      return formatDate(date, 'yyyy-MM-dd hh:mm:ss');
    },
    // 取消操作，返回上一页
    cancel() {
      this.$router.back();
    },
    // 确认发货操作
    confirm() {
      // 验证表单数据
      const validationErrors = [];
      this.list.forEach((item, index) => {
        if (!item.deliveryMethod) {
          validationErrors.push(`第${index + 1}行：请选择发货方式`);
          return;
        }

        if (item.deliveryMethod === 1) {
          // 快递配送验证
          if (!item.deliveryCompany) {
            validationErrors.push(`第${index + 1}行：请选择物流公司`);
          }
          if (!item.deliverySn) {
            validationErrors.push(`第${index + 1}行：请输入物流单号`);
          }
        } else if (item.deliveryMethod === 2) {
          // 门店自提验证
          if (!item.storeId) {
            validationErrors.push(`第${index + 1}行：请选择门店`);
          }
        } else if (item.deliveryMethod === 3) {
          // 虚拟发货验证
          if (!item.virtualDeliveryInfo) {
            validationErrors.push(`第${index + 1}行：请输入发货说明`);
          }
        }
      });

      if (validationErrors.length > 0) {
        this.$message({
          type: 'error',
          message: validationErrors.join('；')
        });
        return;
      }

      this.$confirm('是否要进行发货操作?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 处理发货数据
        this.list.forEach(item => {
          // 转换物流公司为英文简写（仅快递配送）
          if (item.deliveryMethod === 1 && item.deliveryCompany) {
            item.deliveryCompany = logisticsMapping[item.deliveryCompany] || item.deliveryCompany;
          }

          // 门店自提时，将门店信息存储到deliveryCompany字段，提货码存储到deliverySn字段
          if (item.deliveryMethod === 2) {
            item.deliveryCompany = item.storeName;
            item.deliverySn = item.pickupCode;
          }

          // 虚拟发货时，清空物流相关字段
          if (item.deliveryMethod === 3) {
            item.deliveryCompany = '';
            item.deliverySn = '';
          }
        });

        // 发货请求
        deliveryOrder(this.list).then(() => {
          this.$router.back();
          this.$message({
            type: 'success',
            message: '发货成功!'
          });
        });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消发货'
        });
      });
    }
  }
};
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.operate-container {
  display: flex;
  align-items: center;
  font-size: 18px;
}

.table-container {
  margin-top: 20px;
}
</style>
