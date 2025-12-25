<template>
  <view class="content">
    <!-- 订单状态导航栏 -->
    <view class="navbar">
      <view 
        v-for="(item, index) in navList" 
        :key="index" 
        class="nav-item" 
        :class="{ 'current': tabCurrentIndex === index }"
        @tap="tabClick(index)"
      >
        <view class="tab-text-container">
          <text class="tab-text">{{ item.text }}</text>
          <image 
            v-if="tabCurrentIndex === index"
            class="tab-indicator" 
            src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/tab_indicator.png" 
            mode="aspectFit"
          />
        </view>
      </view>
    </view>
    
    <!-- 订单列表 -->
    <swiper 
      class="swiper-box" 
      :current="tabCurrentIndex" 
      duration="300" 
      @change="changeTab"
    >
      <swiper-item 
        class="tab-content" 
        v-for="(tabItem, tabIndex) in navList" 
        :key="tabIndex"
      >
        <scroll-view 
          class="list-scroll-content" 
          scroll-y="true" 
          @scrolltolower="loadData('add')"
        >
          <!-- 空订单提示 -->
          <empty v-if="orderList.length === 0"></empty>
          
          <!-- 订单列表 -->
          <view 
            class="order-item" 
            v-for="(item, index) in filteredOrderList"
            :key="index"
            @tap="showOrderDetail(item.id)"
          >
            <!-- 订单头部 -->
            <view class="i-top b-b">
              <text class="time">下单时间：{{ formatOrderTime(item.createTime) }}</text>
              <text class="state">{{ getOrderStatusText(item) }}</text>
              <text 
                v-if="item.status === 3 || item.status === 4" 
                class="del-btn yticon icon-iconfontshanchu1"
                @tap.stop="deleteOrder(item.id)"
              ></text>
            </view>
            
            <!-- 商品列表 -->
            <view
              class="goods-box-single"
              v-for="(orderItem, itemIndex) in item.orderItemList"
              :key="itemIndex"
            >
              <view class="goods-img-container">
                <image
                  class="goods-img"
                  :src="getDIYPreviewImage(item, orderItem) || orderItem.productPic"
                  mode="aspectFill"
                ></image>
                <!-- DIY标识 -->
                <view v-if="isDIYOrder(item)" class="diy-badge">
                  <text class="diy-text">DIY</text>
                </view>
              </view>
              <view class="right">
                <text class="title clamp">{{ orderItem.productName }}</text>
                <text v-if="isDIYOrder(item)" class="diy-label">个性化定制商品</text>
                <text class="attr-box">{{ formatProductAttr(orderItem.productAttr) }}</text>
                <text class="quantity-box">数量: {{ orderItem.productQuantity }}</text>
              </view>
              <view class="price-right">
                <text class="price">¥{{ parseFloat(orderItem.productPrice).toFixed(2) }}</text>
              </view>
            </view>
            
            <!-- 价格信息 -->
            <view class="price-box">
              <text class="quantity">共<text class="num">{{ getTotalQuantity(item) }}</text>件商品</text>
              <text class="amount">订单金额：<text class="price">{{ formatOrderAmount(item) }}</text></text>
            </view>
            
            <!-- 操作按钮 - 待付款 -->
            <view class="action-box b-t" v-if="item.status == 0">
              <text class="countdown" v-if="item.status == 0">剩余{{ formatSeconds(countdownMap[item.id]) }}</text>
              <button class="action-btn" @tap.stop="cancelOrder(item.id)">取消订单</button>
              <button class="action-btn" @tap.stop="payOrder(item.id)">继续支付</button>
            </view>
            
            <!-- 操作按钮 - 售后状态tab专用 -->
            <view class="action-box b-t" v-if="navList[tabCurrentIndex].state === 5">
              <button class="action-btn" @tap.stop="viewReturnProgress(item.id)">查看进度</button>
              <button class="after-sale-btn" @tap.stop="showOrderDetail(item.id)">订单详情</button>
            </view>

            <!-- 操作按钮 - 其他状态tab（非售后tab） -->
            <template v-else>
              <!-- 操作按钮 - 待发货 -->
              <view class="action-box b-t" v-if="item.status == 1">
                <button v-if="item.sourceType === 1" class="after-sale-btn" @tap.stop="getReturnApplyAndToggle(item, 'apply')">
                  {{ hasAfterSaleApply(item) ? '取消售后申请' : '申请售后' }}
                </button>
                <button class="action-btn" @tap.stop="urgeShipment(item.id)">催促发货</button>
              </view>

              <!-- 操作按钮 - 待收货 -->
              <view class="action-box b-t" v-if="item.status == 2">
                <button v-if="item.sourceType === 1" class="after-sale-btn" @tap.stop="getReturnApplyAndToggle(item, 'apply')">
                  {{ hasAfterSaleApply(item) ? '取消售后申请' : '申请售后' }}
                </button>
                <button class="action-btn" @tap.stop="showOrderDetail(item.id)">查看物流</button>
                <button class="action-btn" @tap.stop="receiveOrder(item.id)">确认收货</button>
              </view>

              <!-- 操作按钮 - 已完成 -->
              <view class="action-box b-t" v-if="item.status == 3">
                <button v-if="item.hasComment == 0" class="after-sale-btn" @tap.stop="showAfterSaleEvaluation(item.id)">售后评价</button>
                <button class="action-btn" @tap.stop="showOrderDetail(item.id)">订单详情</button>
              </view>
            </template>
          </view>
          
          <!-- 加载更多组件 -->
          <uni-load-more :status="loadingType"></uni-load-more>
        </scroll-view>
      </swiper-item>
    </swiper>
  </view>
</template>

<script>
import uniLoadMore from '@/components/uni-load-more/uni-load-more.vue';
import empty from '@/components/empty';
import { fetchOrderList, cancelUserOrder, confirmReceiveOrder, deleteUserOrder, fetchOrderReturnApplyList, cancelReturnApply } from '@/api/order.js';

export default {
  components: {
    uniLoadMore,
    empty
  },
  data() {
    return {
      tabCurrentIndex: 0,
      navList: [
        { state: -1, text: '全部' },
        { state: 0, text: '待付款' },
        { state: 1, text: '待发货' },
        { state: 2, text: '待收货' },
        { state: 3, text: '已完成' },
        { state: 5, text: '售后' },
        { state: 4, text: '已取消' }
      ],
      orderList: [],
      page: 1,
      limit: 10,
      loadingType: 'more',
      orderStatusMap: {
        0: '待付款',
        1: '待发货',
        2: '待收货',
        3: '已完成',
        4: '已取消'
      },
      countdownMap: {}, // 订单倒计时映射
      timer: null // 定时器
    };
  },
  onLoad(options) {
    // 标签切换
    if (options.state !== undefined) {
      // 将state值转换为navList数组索引
      const stateValue = parseInt(options.state);
      const targetIndex = this.navList.findIndex(item => item.state === stateValue);
      this.tabCurrentIndex = targetIndex >= 0 ? targetIndex : 0;
    }
    
    // 加载数据
    this.loadData('refresh');
  },
  onShow() {
    // 页面显示时检查是否需要刷新
    const needRefresh = uni.getStorageSync('needRefreshOrderList');
    if (needRefresh) {
      // 清除标志
      uni.removeStorageSync('needRefreshOrderList');
      // 刷新当前页面数据
      this.loadData('refresh');
    }
  },
  onUnload() {
    // 页面卸载时清理定时器
    if (this.timer) {
      clearInterval(this.timer);
      this.timer = null;
    }
  },
  computed: {
    // 过滤后的订单列表
    filteredOrderList() {
      const currentState = this.navList[this.tabCurrentIndex].state;

      // 如果是售后tab，显示所有有售后申请的订单
      if (currentState === 5) {
        return this.orderList.filter(order => this.hasAfterSaleApply(order));
      }

      // 如果是全部tab，显示所有订单
      if (currentState === -1) {
        return this.orderList;
      }

      // 其他tab：显示对应状态且无售后申请的订单
      return this.orderList.filter(order => {
        // 订单状态匹配
        const statusMatch = order.status === currentState;
        // 无售后申请
        const noAfterSale = !this.hasAfterSaleApply(order);

        return statusMatch && noAfterSale;
      });
    }
  },
  methods: {
    // 获取订单状态文本
    getOrderStatusText(order) {
      // 如果在售后tab，显示售后状态
      if (this.navList[this.tabCurrentIndex].state === 5) {
        return '售后中';
      }

      // 其他tab显示正常的订单状态
      return this.orderStatusMap[order.status] || '未知状态';
    },

    // 判断订单是否有售后申请
    hasAfterSaleApply(order) {
      // 优先使用 afterSaleStatusInfo 中的状态
      if (order.afterSaleStatusInfo && order.afterSaleStatusInfo.afterSaleStatus > 0) {
        return true;
      }

      // 兼容旧的 hasReturnApply 字段
      if (order.hasReturnApply > 0) {
        return true;
      }

      return false;
    },
    
    // 获取订单商品总数量
    getTotalQuantity(order) {
      if (!order.orderItemList || !order.orderItemList.length) {
        return 0;
      }
      
      return order.orderItemList.reduce((sum, item) => sum + item.productQuantity, 0);
    },
    
    // 格式化下单时间
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

    // 修复剩余支付时间计算，兼容 ISO8601
    getOrderTimeLeft(order) {
      if (!order.createTime) return '00:00:00';
      let orderTime = Date.parse(order.createTime);
      if (isNaN(orderTime)) {
        // 兼容 Safari/部分安卓
        orderTime = Date.parse(order.createTime.replace(/-/g, '/'));
      }
      if (isNaN(orderTime)) return '00:00:00';
      const expireTime = orderTime + 120 * 60 * 1000;
      const now = Date.now();
      if (now >= expireTime) return '00:00:00';
      const diff = expireTime - now;
      const hours = Math.floor(diff / (60 * 60 * 1000));
      const minutes = Math.floor((diff % (60 * 60 * 1000)) / (60 * 1000));
      const seconds = Math.floor((diff % (60 * 1000)) / 1000);
      return `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
    },
    
    // 标签切换
    tabClick(index) {
      this.tabCurrentIndex = index;
      this.page = 1;
      this.orderList = [];
      this.loadData('refresh');
    },
    
    // 顶部swiper滑动
    changeTab(e) {
      this.tabCurrentIndex = e.detail.current;
      this.page = 1;
      this.orderList = [];
      this.loadData('refresh');
    },
    
    // 加载数据
    loadData(type) {
      // 加载状态判断
      if (type === 'add') {
        if (this.loadingType === 'nomore') {
          return;
        }
        this.page += 1;
      } else {
        this.page = 1;
        this.loadingType = 'loading';
      }
      
      // 获取当前状态
      const state = this.navList[this.tabCurrentIndex].state;

      // 调用API获取订单数据
      // 注意：state=5 表示售后状态，后端会特殊处理为筛选 after_sale_status > 0 的订单
      const params = {
        pageNum: this.page,
        pageSize: this.limit,
        status: state
      };
      
      fetchOrderList(params).then(res => {
        if (res.code === 200 && res.data) {
          const { list, pageNum, pageSize, total } = res.data;
          
          if (type === 'refresh') {
            this.orderList = list || [];
          } else {
            this.orderList = [...this.orderList, ...(list || [])];
          }
          
          // 设置加载状态
          this.loadingType = this.orderList.length >= total ? 'nomore' : 'more';
          // 初始化倒计时
          this.initCountdown();
        } else {
          // 请求失败
          uni.showToast({
            title: res.message || '获取订单列表失败',
            icon: 'none'
          });
          this.loadingType = 'more';
        }
      }).catch(err => {
        console.error('获取订单列表失败', err);
        uni.showToast({
          title: '获取订单列表失败',
          icon: 'none'
        });
        this.loadingType = 'more';
      });
    },
    
    // 初始化倒计时
    initCountdown() {
      // 清理旧定时器
      if (this.timer) {
        clearInterval(this.timer);
        this.timer = null;
      }
      // 初始化 countdownMap
      this.countdownMap = {};
      const now = Date.now();
      this.orderList.forEach(order => {
        if (order.status === 0 && order.createTime) {
          let orderTime = Date.parse(order.createTime);
          if (isNaN(orderTime)) {
            orderTime = Date.parse(order.createTime.replace(/-/g, '/'));
          }
          if (!isNaN(orderTime)) {
            const expireTime = orderTime + 120 * 60 * 1000;
            let left = Math.floor((expireTime - now) / 1000);
            this.countdownMap[order.id] = left > 0 ? left : 0;
          }
        }
      });
      // 启动定时器
      this.timer = setInterval(() => {
        let changed = false;
        Object.keys(this.countdownMap).forEach(id => {
          if (this.countdownMap[id] > 0) {
            this.countdownMap[id]--;
            changed = true;
          }
        });
        if (changed) {
          this.$forceUpdate();
        }
      }, 1000);
    },
    
    // 格式化秒为 HH:mm:ss
    formatSeconds(sec) {
      if (typeof sec !== 'number' || sec <= 0) return '00:00:00';
      const h = String(Math.floor(sec / 3600)).padStart(2, '0');
      const m = String(Math.floor((sec % 3600) / 60)).padStart(2, '0');
      const s = String(sec % 60).padStart(2, '0');
      return `${h}:${m}:${s}`;
    },
    
    // 查看订单详情
    showOrderDetail(id) {
      uni.navigateTo({
        url: `/pages/order/orderDetail?id=${id}`
      });
    },
    
    // 取消订单
    cancelOrder(id) {
      uni.showModal({
        title: '提示',
        content: '确定取消此订单？',
        success: res => {
          if (res.confirm) {
            // 调用取消订单API
            cancelUserOrder({
              orderId: id
            }).then(res => {
              if (res.code === 200) {
                uni.showToast({
                  title: '订单已取消'
                });
                
                // 刷新订单列表
                this.loadData('refresh');
              } else {
                uni.showToast({
                  title: res.message || '取消订单失败',
                  icon: 'none'
                });
              }
            }).catch(err => {
              console.error('取消订单失败', err);
              uni.showToast({
                title: '取消订单失败',
                icon: 'none'
              });
            });
          }
        }
      });
    },
    
    // 继续支付
    payOrder(id) {
      uni.navigateTo({
        url: `/pages/money/pay?id=${id}`
      });
    },
    
    // 确认收货
    receiveOrder(id) {
      uni.showModal({
        title: '提示',
        content: '确认已收到货物？',
        success: res => {
          if (res.confirm) {
            // 调用确认收货API
            confirmReceiveOrder({
              orderId: id
            }).then(res => {
              if (res.code === 200) {
                uni.showToast({
                  title: '已确认收货'
                });
                
                // 刷新订单列表
                this.loadData('refresh');
              } else {
                uni.showToast({
                  title: res.message || '确认收货失败',
                  icon: 'none'
                });
              }
            }).catch(err => {
              console.error('确认收货失败', err);
              uni.showToast({
                title: '确认收货失败',
                icon: 'none'
              });
            });
          }
        }
      });
    },
    
    // 删除订单
    deleteOrder(id) {
      uni.showModal({
        title: '提示',
        content: '确定删除此订单？',
        success: res => {
          if (res.confirm) {
            // 调用删除订单API
            deleteUserOrder({
              orderId: id
            }).then(res => {
              if (res.code === 200) {
                uni.showToast({
                  title: '订单已删除'
                });
                
                // 刷新订单列表
                this.loadData('refresh');
              } else {
                uni.showToast({
                  title: res.message || '删除订单失败',
                  icon: 'none'
                });
              }
            }).catch(err => {
              console.error('删除订单失败', err);
              uni.showToast({
                title: '删除订单失败',
                icon: 'none'
              });
            });
          }
        }
      });
    },
    
    // 解析商品属性
    formatProductAttr(attr) {
      if (!attr) return '';
      let arr = [];
      try {
        const attrs = typeof attr === 'string' ? JSON.parse(attr) : attr;
        arr = attrs.map(item => `${item.key}：${item.value}`);
      } catch (e) {
        // 解析失败直接返回原始内容
        return attr;
      }
      return arr.join('，');
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

    // 获取售后申请ID并根据状态切换申请或取消
    getReturnApplyAndToggle(order, action) {
      // 首先检查是否已有售后申请
      if (this.hasAfterSaleApply(order)) {
        // 已有售后申请，则执行取消操作
        this.doCancelReturnApply(order);
      } else {
        // 没有售后申请，则申请售后
        this.applyAfterSale(order);
      }
    },

    // 取消售后申请
    doCancelReturnApply(order) {
      // 先获取该订单的售后申请ID
      uni.showLoading({
        title: '加载中...'
      });

      fetchOrderReturnApplyList(order.id).then(res => {
        uni.hideLoading();

        if (res.code === 200 && res.data && res.data.length > 0) {
          // 获取最新的售后申请
          const latestApply = res.data[0];
          
          uni.showModal({
            title: '提示',
            content: '确定要取消售后申请吗？',
            success: (confirmRes) => {
              if (confirmRes.confirm) {
                // 调用取消售后申请API
                cancelReturnApply(latestApply.id).then(cancelRes => {
                  if (cancelRes.code === 200) {
                    uni.showToast({
                      title: '售后申请已取消'
                    });
                    // 刷新订单列表
                    this.loadData('refresh');
                  } else {
                    uni.showToast({
                      title: cancelRes.message || '取消售后申请失败',
                      icon: 'none'
                    });
                  }
                }).catch(err => {
                  console.error('取消售后申请失败', err);
                  uni.showToast({
                    title: '取消售后申请失败',
                    icon: 'none'
                  });
                });
              }
            }
          });
        } else {
          uni.showToast({
            title: '未找到售后申请',
            icon: 'none'
          });
        }
      }).catch(err => {
        console.error('获取售后申请失败', err);
        uni.hideLoading();
        uni.showToast({
          title: '获取售后信息失败',
          icon: 'none'
        });
      });
    },

    // 申请售后
    applyAfterSale(order) {
      console.log('申请售后 - 原始订单数据:', order);
      console.log('订单ID:', order.id);
      console.log('订单来源类型:', order.sourceType);
      console.log('订单商品列表:', order.orderItemList);
      
      // 检查订单来源类型，只有小程序订单才能申请退货
      if (order.sourceType !== 1) {
        uni.showToast({
          title: '只有小程序订单才能申请退货',
          icon: 'none'
        });
        return;
      }
      
      // 检查是否有订单商品
      if (!order.orderItemList || order.orderItemList.length === 0) {
        uni.showToast({
          title: '订单商品信息异常',
          icon: 'none'
        });
        return;
      }
      
      // 如果只有一个商品，直接跳转
      if (order.orderItemList.length === 1) {
        const orderItem = order.orderItemList[0];
        console.log('订单商品信息:', orderItem);
        console.log('商品ID:', orderItem.id);
        
        const orderId = order.id;
        const orderItemId = orderItem.id;
        const orderStatus = order.status;
        const url = `/pages/order/returnApply?orderId=${orderId}&orderItemId=${orderItemId}&orderStatus=${orderStatus}`;

        console.log('准备跳转的URL:', url);
        
        if (!orderId || !orderItemId) {
          console.error('订单ID或商品ID为空', { orderId, orderItemId });
          uni.showToast({
            title: '订单信息异常，无法跳转',
            icon: 'none'
          });
          return;
        }
        
        uni.navigateTo({
          url: url,
          success: (res) => {
            console.log('跳转成功:', res);
          },
          fail: (err) => {
            console.error('跳转失败:', err);
            uni.showToast({
              title: '页面跳转失败',
              icon: 'none'
            });
          }
        });
      } else {
        // 多个商品时，显示选择商品列表
        this.showSelectProductModal(order);
      }
    },
    
    // 显示选择商品弹窗
    showSelectProductModal(order) {
      const itemList = order.orderItemList.map(item => item.productName);
      
      uni.showActionSheet({
        itemList: itemList,
        success: (res) => {
          const selectedItem = order.orderItemList[res.tapIndex];
          uni.navigateTo({
            url: `/pages/order/returnApply?orderId=${order.id}&orderItemId=${selectedItem.id}&orderStatus=${order.status}`
          });
        }
      });
    },
    
    // 查看退货进度
    viewReturnProgress(orderId) {
      // 先获取该订单的售后申请列表，然后跳转到售后进度页面
      uni.showLoading({
        title: '加载中...'
      });

      fetchOrderReturnApplyList(orderId).then(res => {
        uni.hideLoading();

        if (res.code === 200 && res.data && res.data.length > 0) {
          // 获取最新的售后申请
          const latestApply = res.data[0];
          uni.navigateTo({
            url: `/pages/order/afterSaleProgress?returnApplyId=${latestApply.id}`
          });
        } else {
          // 如果没有找到售后申请，跳转到订单详情页面
          uni.showToast({
            title: '未找到售后申请',
            icon: 'none'
          });
          uni.navigateTo({
            url: `/pages/order/orderDetail?id=${orderId}`
          });
        }
      }).catch(err => {
        console.error('获取售后申请失败', err);
        uni.hideLoading();
        uni.showToast({
          title: '获取售后信息失败',
          icon: 'none'
        });

        // 出错时跳转到订单详情页面
        uni.navigateTo({
          url: `/pages/order/orderDetail?id=${orderId}`
        });
      });
    },
    
    // 催促发货
    urgeShipment(orderId) {
      uni.showModal({
        title: '催促配送',
        content: '已帮您催促快递尽快配送',
        showCancel: false,
        confirmText: '我知道了',
        success: (res) => {
          if (res.confirm) {
            uni.showToast({
              title: '催促信息已发送',
              icon: 'success'
            });
          }
        }
      });
    },
    
    // 售后评价
    showAfterSaleEvaluation(orderId) {
      // 找到对应的订单
      const order = this.orderList.find(item => item.id === orderId);
      if (!order || !order.orderItemList || order.orderItemList.length === 0) {
        uni.showToast({
          title: '订单信息异常',
          icon: 'none'
        });
        return;
      }
      
      // 如果只有一个商品，直接跳转
      if (order.orderItemList.length === 1) {
        const productId = order.orderItemList[0].productId;
        uni.navigateTo({
          url: `/pages/order/afterSaleEvaluation?orderId=${orderId}&productId=${productId}`
        });
      } else {
        // 多个商品时，让用户选择要评价的商品
        const itemList = order.orderItemList.map(item => item.productName);
        
        uni.showActionSheet({
          itemList: itemList,
          success: (res) => {
            const selectedItem = order.orderItemList[res.tapIndex];
            uni.navigateTo({
              url: `/pages/order/afterSaleEvaluation?orderId=${orderId}&productId=${selectedItem.productId}`
            });
          }
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
page,
.content {
  background: #F0F0F0;
  height: 100%;
}
.swiper-box {
  height: calc(100% - 40px);
}
.list-scroll-content {
  height: 100%;
}
.navbar {
  display: flex;
  height: 40px;
  padding: 0 5px;
  background: #fff;
  box-shadow: 0 1px 5px rgba(0, 0, 0, 0.06);
  position: relative;
  z-index: 10;
  
  .nav-item {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100%;
    font-size: 14px;
    font-family: "Source Han Sans CN";
    color: #000000;
    font-weight: 350;
    position: relative;
    opacity: 0.4;
    
    &.current {
      color: #000000;
      opacity: 1;
    }
    
    .tab-text-container {
      display: inline-flex;
      flex-direction: column;
      align-items: center;
      position: relative;
      height: 100%;
      justify-content: center;
      
      .tab-text {
        font-size: 14px;
        font-family: "Source Han Sans CN";
        color: inherit;
        font-weight: inherit;
        white-space: nowrap;
        z-index: 1;
      }
      
      .tab-indicator {
        position: absolute;
        bottom: 30rpx;
        left: 0;
        right: 0;
        width: 100%;
        height: 20rpx;
        z-index: 0;
      }
    }
  }
}
.uni-swiper-item {
  height: auto;
}
.order-item {
  display: flex;
  flex-direction: column;
  padding: 0 15px;
  background: #fff;
  margin-top: 10rpx;
  border-radius: 8px;
  overflow: hidden;
  
  .i-top {
    display: flex;
    align-items: center;
    height: 50px;
    font-size: 14px;
    position: relative;
    
    .time {
      flex: 1;
      font-family: "Source Han Sans SC";
      font-weight: 400;
      color: #000000;
    }
    
    .state {
      font-family: "Source Han Sans SC";
      font-weight: 500;
      color: #647D00;
    }
    
    .del-btn {
      padding: 10rpx 0 10rpx 36rpx;
      font-size: 30rpx;
      color: #909399;
      position: relative;
      
      &:after {
        content: "";
        width: 0;
        height: 30rpx;
        border-left: 1px solid #DCDFE6;
        position: absolute;
        left: 20rpx;
        top: 50%;
        transform: translateY(-50%);
      }
    }
  }
  
  /* 多条商品 */
  .goods-box {
    height: 160rpx;
    padding: 20rpx 0;
    white-space: nowrap;
    
    .goods-item {
      width: 120rpx;
      height: 120rpx;
      display: inline-block;
      margin-right: 24rpx;
    }
    
    .goods-img {
      display: block;
      width: 100%;
      height: 100%;
    }
  }
  
  /* 单条商品 */
  .goods-box-single {
    display: flex;
    padding: 15px 0;
    position: relative;
    
    &:after {
      content: "";
      position: absolute;
      left: 0;
      bottom: 0;
      right: 0;
      height: 1px;
      background-color: #DDDDDD;
      transform: scaleY(0.5);
    }
    
    .goods-img-container {
      position: relative;
      width: 120rpx;
      height: 120rpx;
    }

    .goods-img {
      display: block;
      width: 120rpx;
      height: 120rpx;
      border-radius: 5px;
    }

    .diy-badge {
      position: absolute;
      top: 0;
      right: 0;
      background: linear-gradient(135deg, #ff6b6b, #ff8e8e);
      border-radius: 0 5px 0 8px;
      padding: 2rpx 6rpx;
      z-index: 2;
    }

    .diy-text {
      color: white;
      font-size: 18rpx;
      font-weight: bold;
    }

    .diy-label {
      color: #ff6b6b;
      font-size: 22rpx;
      margin-bottom: 6rpx;
      font-weight: 500;
    }
    
    .right {
      flex: 1;
      display: flex;
      flex-direction: column;
      padding: 0 30rpx 0 24rpx;
      overflow: hidden;
      
      .title {
        font-size: 14px;
        color: #000000;
        line-height: 1.5;
        font-family: "Source Han Sans SC";
      }
      
      .attr-box {
        font-size: 14px;
        color: #000000;
        padding: 10rpx 12rpx;
        font-family: "Source Han Sans SC";
        opacity: 0.7;
      }
      
      .quantity-box {
        font-size: 14px;
        color: #000000;
        padding: 10rpx 12rpx;
        font-family: "Source Han Sans SC";
        opacity: 0.7;
      }
    }
    
    .price-right {
      display: flex;
      justify-content: flex-end;
      align-items: center;
      padding: 0 24rpx;
      
      .price {
        font-size: 14px;
        color: #000000;
        font-family: "Source Han Sans SC";
        font-weight: 500;
      }
    }
  }
  
  .price-box {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px 0;
    font-size: 14px;
    color: #000000;
    font-family: "Source Han Sans SC";
    font-weight: 400;
    
    .quantity {
      color: #000000;
      
      .num {
        margin: 0 4px;
        color: #000000;
      }
    }
    
    .amount {
      color: #000000;
      font-weight: 500;
      
      .price {
        font-weight: 500;
      }
    }
  }
  
  .action-box {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    height: 60px;
    position: relative;
    padding: 10px 0;
    
    .countdown {
      position: absolute;
      left: 0;
      font-size: 12px;
      font-family: "Source Han Sans SC";
      font-weight: 400;
      color: #010101;
    }
  }
  
  .action-btn {
    width: 120px;
    height: 40px;
    margin: 0;
    margin-left: 10px;
    padding: 0;
    text-align: center;
    line-height: 40px;
    font-size: 14px;
    color: #A9FF00;
    background: #000000;
    border: none;
    border-radius: 16rpx;
    font-family: "Source Han Sans SC";
    font-weight: 500;
    
    &:first-child {
      margin-left: 0;
    }
  }
  
  .after-sale-btn {
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    padding: 16rpx 24rpx;
    gap: 16rpx;
    width: 160rpx;
    height: 72rpx;
    margin: 0;
    margin-right: 10px;
    background: #F5F5F5;
    border-radius: 16rpx;
    border: none !important;
    font-family: 'PingFang SC';
    font-style: normal;
    font-weight: 400;
    font-size: 28rpx;
    line-height: 40rpx;
    color: #0A0D05;
    flex: none;
    order: 0;
    flex-grow: 0;
    
    &::after {
      border: none;
    }
  }
}

/* load-more */
.uni-load-more {
  display: flex;
  flex-direction: row;
  height: 80rpx;
  align-items: center;
  justify-content: center;
}
.uni-load-more__text {
  font-size: 28rpx;
  color: #999;
}
.uni-load-more__img {
  height: 24px;
  width: 24px;
  margin-right: 10px;
  
  > view {
    position: absolute;
    
    view {
      width: 6px;
      height: 2px;
      border-top-left-radius: 1px;
      border-bottom-left-radius: 1px;
      background: #999;
      position: absolute;
      opacity: 0.2;
      transform-origin: 50%;
      animation: load 1.56s ease infinite;
      
      &:nth-child(1) {
        transform: rotate(90deg);
        top: 2px;
        left: 9px;
      }
      
      &:nth-child(2) {
        transform: rotate(180deg);
        top: 11px;
        right: 0;
      }
      
      &:nth-child(3) {
        transform: rotate(270deg);
        bottom: 2px;
        left: 9px;
      }
      
      &:nth-child(4) {
        top: 11px;
        left: 0;
      }
    }
  }
}

.load1,
.load2,
.load3 {
  height: 24px;
  width: 24px;
}
.load2 {
  transform: rotate(30deg);
}
.load3 {
  transform: rotate(60deg);
}

.load1 {
  view:nth-child(1) {
    animation-delay: 0s;
  }
  
  view:nth-child(2) {
    animation-delay: 0.39s;
  }
  
  view:nth-child(3) {
    animation-delay: 0.78s;
  }
  
  view:nth-child(4) {
    animation-delay: 1.17s;
  }
}

.load2 {
  view:nth-child(1) {
    animation-delay: 0.13s;
  }
  
  view:nth-child(2) {
    animation-delay: 0.52s;
  }
  
  view:nth-child(3) {
    animation-delay: 0.91s;
  }
  
  view:nth-child(4) {
    animation-delay: 1.3s;
  }
}

.load3 {
  view:nth-child(1) {
    animation-delay: 0.26s;
  }
  
  view:nth-child(2) {
    animation-delay: 0.65s;
  }
  
  view:nth-child(3) {
    animation-delay: 1.04s;
  }
  
  view:nth-child(4) {
    animation-delay: 1.43s;
  }
}

@keyframes load {
  0% {
    opacity: 1;
  }
  100% {
    opacity: 0.2;
  }
}
</style> 