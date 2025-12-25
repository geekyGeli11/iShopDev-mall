<template>
  <view class="page-container">
    <!-- 步骤1: 选择售后类型和商品 -->
    <view v-if="currentStep === 1" class="step-page">
      <!-- 售后类型选择卡片 -->
      <view class="type-card">
        <view class="card-title">请选择售后类型：</view>
        <view class="type-buttons">
          <view
            class="type-button"
            :class="{ 'active': returnForm.returnType === 2 }"
            @tap="selectReturnType(2)"
          >
            <text class="type-text">退货退款</text>
          </view>
          <view
            class="type-button"
            :class="{ 'active': returnForm.returnType === 1 }"
            @tap="selectReturnType(1)"
          >
            <text class="type-text">仅退款</text>
          </view>
        </view>
      </view>

      <!-- 商品选择列表 -->
      <view
        v-for="item in orderItems"
        :key="item.id"
        class="product-card"
        :class="{ 'disabled': item.hasAfterSale }"
        @tap="toggleSelectItem(item.id)"
      >
        <view class="product-checkbox" :class="{ 'checked': selectedItems.includes(item.id), 'disabled': item.hasAfterSale }">
          <view v-if="selectedItems.includes(item.id)" class="checkbox-inner"></view>
        </view>
        <view class="product-content">
          <image
            class="product-image"
            :src="item.productPic ? item.productPic : '/static/images/default-product.png'"
            mode="aspectFill"
          ></image>
          <view class="product-details">
            <view class="product-header">
              <text class="product-name">{{ item.productName }}</text>
              <text class="product-price">¥ {{ getRealPrice(item) }}</text>
            </view>
            <text class="product-attr">{{ formatProductAttr(item.productAttr) }}</text>
            <text class="product-quantity">×{{ item.productQuantity }}</text>
            <!-- 售后状态标签 -->
            <view v-if="item.hasAfterSale" class="after-sale-tag">
              <text class="tag-text">{{ getAfterSaleStatusText(item.afterSaleInfo) }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 底部按钮 -->
      <view class="bottom-button-container">
        <view class="bottom-button" @tap="nextStep" :class="{ 'disabled': !canGoNext }">
          <text class="bottom-button-text">下一步</text>
        </view>
      </view>
    </view>

    <!-- 步骤2: 选择退货原因 -->
    <view v-if="currentStep === 2" class="step-page">
      <!-- 原因选择卡片 -->
      <view class="reason-card">
        <view class="reason-options">
          <view
            v-for="reason in reasonList"
            :key="reason.id"
            class="reason-item"
            @tap="selectReason(reason.name)"
          >
            <text class="reason-text">{{ reason.name }}</text>
            <view class="reason-checkbox" :class="{ 'checked': selectedReason === reason.name }">
              <view v-if="selectedReason === reason.name" class="checkbox-inner"></view>
            </view>
          </view>
        </view>
      </view>

      <!-- 底部按钮 -->
      <view class="bottom-button-container">
        <view class="bottom-button" @tap="nextStep" :class="{ 'disabled': !selectedReason }">
          <text class="bottom-button-text">下一步</text>
        </view>
      </view>
    </view>

    <!-- 步骤3: 确认信息和提交申请 -->
    <view v-if="currentStep === 3" class="step-page">
      <!-- 商品信息卡片 -->
      <view class="product-info-card">
        <text class="card-title">商品信息</text>
        <view
          v-for="item in selectedOrderItems"
          :key="item.id"
          class="product-card"
        >
          <image
            class="product-image"
            :src="item.productPic ? item.productPic : '/static/images/default-product.png'"
            mode="aspectFill"
          ></image>
          <view class="product-content">
            <view class="product-details">
              <view class="product-header">
                <text class="product-name">{{ item.productName }}</text>
                <text class="product-price">¥{{ getRealPrice(item) }}</text>
              </view>
              <text class="product-attr">{{ formatProductAttr(item.productAttr) }}</text>
              <text class="product-quantity">数量：{{ item.productQuantity }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 退款信息卡片 -->
      <view class="refund-card">
        <view class="refund-item">
          <text class="refund-label">退款金额</text>
          <text class="refund-value">¥{{ calculateRefundAmount() }}</text>
        </view>
        <!-- 仅退款时不显示上传凭证和退货方式 -->
        <view v-if="returnForm.returnType === 2" class="refund-item" @tap="openUploadModal">
          <text class="refund-label">上传凭证和描述</text>
          <text class="refund-arrow">></text>
        </view>
        <view v-if="returnForm.returnType === 2" class="refund-item">
          <text class="refund-label">退货方式</text>
          <text class="refund-value">自行寄回</text>
        </view>
      </view>

      <!-- 底部按钮 -->
      <view class="bottom-button-container">
        <view class="bottom-button" @tap="submitApplication">
          <text class="bottom-button-text">提交申请</text>
        </view>
      </view>
    </view>

    <!-- 上传凭证弹窗 -->
    <view v-if="showUploadModal" class="upload-modal-overlay" @tap="closeUploadModal">
      <view class="upload-modal" @tap.stop>
        <view class="modal-header">
          <text class="modal-title">描述具体情况</text>
          <view class="modal-close" @tap="closeUploadModal">×</view>
        </view>

        <view class="modal-content">
          <!-- 上传图片区域 -->
          <view class="upload-section">
            <view class="upload-grid">
              <view
                v-for="(pic, index) in proofPics"
                :key="index"
                class="upload-item"
              >
                <image
                  class="upload-image"
                  :src="pic"
                  mode="aspectFill"
                  @tap="previewImage(index)"
                ></image>
                <view class="delete-btn" @tap="deleteImage(index)">×</view>
              </view>
              <view
                v-if="proofPics.length < 6"
                class="upload-add"
                @tap="chooseImage"
              >
                <text class="add-icon">+</text>
              </view>
            </view>
          </view>

          <!-- 描述输入区域 -->
          <view class="description-section">
            <text class="section-label">描述具体情况</text>
            <textarea
              class="description-input"
              v-model="returnForm.description"
              placeholder="请详细描述商品问题，方便我们更好地为您处理"
              maxlength="300"
            ></textarea>
            <view class="description-count">{{ returnForm.description.length }}/300</view>
          </view>
        </view>

        <view class="modal-footer">
          <view class="modal-button" @tap="saveUploadInfo">
            <text class="modal-button-text">保存</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { createReturnApply, fetchReturnReasonList, fetchOrderDetail, fetchOrderReturnApplyList, updateReturnApplyDelivery } from '@/api/order.js';
import { uploadImage } from '@/api/common.js';

export default {
  data() {
    return {
      // 流程控制
      currentStep: 1, // 当前步骤：1-选择类型和商品，2-选择原因，3-提交申请

      // 基础数据
      orderId: null,
      orderItemId: null,
      orderItem: {},
      orderItems: [], // 订单商品列表
      orderInfo: {}, // 订单信息
      reasonList: [],
      proofPics: [],
      afterSaleList: [], // 售后申请列表

      // 选择状态
      selectedItems: [], // 选中的商品ID列表
      selectedOrderItems: [], // 选中的商品详情列表
      selectedReason: '', // 选中的退货原因
      selectedReasons: [], // 选中的退货原因数组（兼容旧版）
      trackingNumber: '', // 运单号
      showUploadModal: false, // 显示上传弹窗

      // 弹窗控制
      showReasonPopup: false, // 是否显示退货原因弹窗
      showDeliveryModal: false, // 显示快递单号输入弹窗
      isSubmitting: false, // 是否正在提交

      // 页面状态
      fromNotify: false, // 是否从通知进入
      existingReturnApply: null, // 已存在的退货申请

      // 表单数据
      deliveryForm: {
        company: '',
        trackingNumber: ''
      },
      returnForm: {
        orderId: null,
        productId: null,
        orderSn: '',
        memberUsername: '',
        returnName: '',
        returnPhone: '',
        productPic: '',
        productName: '',
        productBrand: '',
        productAttr: '',
        productCount: 1,
        productPrice: 0,
        productRealPrice: 0,
        returnType: 2, // 1-换货，2-退货退款
        reason: '',
        description: '',
        proofPics: ''
      },

      // 退货地址信息
      returnAddress: {
        receiverName: 'XXXXX 12994877366',
        receiverAddress: '北京市 XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX'
      }
    };
  },
  
  computed: {
    // 是否可以进入下一步
    canGoNext() {
      if (this.currentStep === 1) {
        return this.returnForm.returnType && this.selectedItems.length > 0;
      } else if (this.currentStep === 2) {
        return this.selectedReason;
      }
      return true;
    },

    // 旧版兼容
    canSubmit() {
      return this.selectedReasons.length > 0 &&
             this.returnForm.description.trim() &&
             this.returnForm.returnName.trim() &&
             this.returnForm.returnPhone.trim() &&
             this.returnForm.productCount > 0 &&
             !this.isSubmitting;
    },

    // 是否显示快递单号上传按钮
    showDeliveryButton() {
      return this.existingReturnApply && this.existingReturnApply.status === 1;
    },

    // 是否可以提交快递信息
    canSubmitDelivery() {
      return this.deliveryForm.company.trim() && this.deliveryForm.trackingNumber.trim();
    }
  },
  
  onLoad(options) {
    if (options.orderId && options.orderItemId) {
      this.orderId = options.orderId;
      this.orderItemId = options.orderItemId;
      this.fromNotify = options.fromNotify === '1'; // 检查是否从通知进入
      // 根据传入的订单状态参数自动选择售后类型
      if (options.orderStatus) {
        const orderStatus = parseInt(options.orderStatus);
        if (orderStatus === 1) {
          // 待发货状态，自动选中仅退款
          this.returnForm.returnType = 1;
        } else {
          // 其他状态，自动选中退货退款
          this.returnForm.returnType = 2;
        }
      }

      this.getOrderDetail();
      this.getReturnReasonList();

      // 如果从通知进入，检查是否已有退货申请
      if (this.fromNotify) {
        this.checkExistingReturnApply();
      }
    } else {
      uni.showToast({
        title: '参数错误',
        icon: 'none'
      });
      setTimeout(() => {
        uni.navigateBack();
      }, 1500);
    }
  },
  
  methods: {
    // 获取订单详情
    async getOrderDetail() {
      uni.showLoading({
        title: '加载中...'
      });

      try {
        // 同时获取订单详情和售后申请列表
        const [orderRes, afterSaleRes] = await Promise.all([
          fetchOrderDetail(this.orderId),
          fetchOrderReturnApplyList(this.orderId)
        ]);

        uni.hideLoading();

        if (orderRes.code === 200 && orderRes.data) {
          this.order = orderRes.data;
          this.orderInfo = orderRes.data;

          // 获取售后申请列表
          this.afterSaleList = afterSaleRes.code === 200 ? (afterSaleRes.data || []) : [];

          // 设置订单商品列表并标记售后状态
          this.orderItems = this.processOrderItemsWithAfterSaleStatus(this.order.orderItemList || []);

          // 找到对应的订单项
          const orderItem = this.order.orderItemList.find(item => item.id == this.orderItemId);
          if (orderItem) {
            this.orderItem = orderItem;

            // 初始化退货表单
            this.returnForm = {
              ...this.returnForm,
              orderId: this.order.id,
              productId: orderItem.productId,
              orderSn: this.order.orderSn,
              memberUsername: this.order.memberUsername || '',
              returnName: this.order.receiverName || '',
              returnPhone: this.order.receiverPhone || '',
              productPic: orderItem.productPic,
              productName: orderItem.productName,
              productBrand: orderItem.productBrand || '',
              productAttr: orderItem.productAttr,
              productCount: 1,
              productPrice: orderItem.productPrice,
              productRealPrice: orderItem.productRealPrice,
              reason: '',
              description: '',
              proofPics: ''
            };

            // 如果是从特定商品进入，自动选中该商品（如果该商品未申请售后）
            if (this.orderItemId) {
              const targetItem = this.orderItems.find(item => item.id == this.orderItemId);
              if (targetItem && !targetItem.hasAfterSale) {
                this.selectedItems = [parseInt(this.orderItemId)];
                this.selectedOrderItems = [targetItem];
                console.log('选中的商品:', targetItem);
              } else if (targetItem && targetItem.hasAfterSale) {
                uni.showToast({
                  title: '该商品已申请售后，无法重复申请',
                  icon: 'none'
                });
                setTimeout(() => {
                  uni.navigateBack();
                }, 1500);
                return;
              }
            } else {
              // 如果没有指定商品，默认选中所有未申请售后的商品
              const availableItems = this.orderItems.filter(item => !item.hasAfterSale);
              this.selectedItems = availableItems.map(item => item.id);
              this.selectedOrderItems = [...availableItems];
              console.log('选中的可用商品:', this.selectedOrderItems);

              // 如果没有可选商品，提示用户
              if (availableItems.length === 0) {
                uni.showToast({
                  title: '所有商品都已申请售后',
                  icon: 'none'
                });
                setTimeout(() => {
                  uni.navigateBack();
                }, 1500);
                return;
              }
            }


          } else {
            uni.showToast({
              title: '商品信息不存在',
              icon: 'none'
            });
            setTimeout(() => {
              uni.navigateBack();
            }, 1500);
          }
        } else {
          uni.showToast({
            title: orderRes.message || '获取订单详情失败',
            icon: 'none'
          });
        }
      } catch (err) {
        console.error('获取订单详情失败', err);
        uni.hideLoading();

        uni.showToast({
          title: '获取订单详情失败',
          icon: 'none'
        });
      }
    },

    // 处理订单商品列表，标记售后状态
    processOrderItemsWithAfterSaleStatus(orderItems) {
      return orderItems.map(item => {
        const afterSaleInfo = this.getAfterSaleInfoForItem(item.id);
        return {
          ...item,
          hasAfterSale: !!afterSaleInfo,
          afterSaleInfo: afterSaleInfo
        };
      });
    },

    // 获取商品项的售后信息
    getAfterSaleInfoForItem(orderItemId) {
      return this.afterSaleList.find(apply =>
        apply.orderItemId === orderItemId &&
        apply.status !== 3 && // 排除已拒绝的申请
        apply.status !== 4    // 排除已取消的申请
      );
    },

    // 获取售后状态文本
    getAfterSaleStatusText(afterSaleInfo) {
      if (!afterSaleInfo) return '';

      const statusMap = {
        0: '售后审核中',
        1: '退货中',
        2: '售后完成'
      };

      return statusMap[afterSaleInfo.status] || '售后中';
    },

    // 获取退货原因列表
    getReturnReasonList() {
      fetchReturnReasonList().then(res => {
        if (res.code === 200 && res.data) {
          // 后端已经过滤了启用的退货原因
          this.reasonList = res.data;
        } else {
          console.log('获取退货原因失败', res.message);
        }
      }).catch(err => {
        console.error('获取退货原因失败', err);
      });
    },
    
    // 增加数量
    increaseQuantity() {
      if (this.returnForm.productCount < this.orderItem.productQuantity) {
        this.returnForm.productCount++;
      }
    },
    
    // 减少数量
    decreaseQuantity() {
      if (this.returnForm.productCount > 1) {
        this.returnForm.productCount--;
      }
    },
    
    // 显示退货原因弹窗
    showReasonModal() {
      this.showReasonPopup = true;
    },
    
    // 隐藏退货原因弹窗
    hideReasonModal() {
      this.showReasonPopup = false;
    },
    
    // 切换退货原因选择
    toggleReason(reason) {
      const index = this.selectedReasons.indexOf(reason);
      if (index > -1) {
        this.selectedReasons.splice(index, 1);
      } else {
        this.selectedReasons.push(reason);
      }
    },
    
    // 确认退货原因选择
    confirmReasonSelection() {
      if (this.selectedReasons.length === 0) {
        uni.showToast({
          title: '请至少选择一个退货原因',
          icon: 'none'
        });
        return;
      }
      
      // 将选中的原因合并为字符串
      this.returnForm.reason = this.selectedReasons.join('；');
      this.hideReasonModal();
    },
    
    // 选择图片
    chooseImage() {
      uni.chooseImage({
        count: 6 - this.proofPics.length,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          // 逐个上传选中的图片
          this.uploadImages(res.tempFilePaths);
        }
      });
    },
    
    // 上传图片到服务器
    async uploadImages(tempFilePaths) {
      if (tempFilePaths.length === 0) return;
      
      uni.showLoading({
        title: '上传图片中...'
      });
      
      let successCount = 0;
      const totalCount = tempFilePaths.length;
      
      for (let i = 0; i < tempFilePaths.length; i++) {
        try {
          const result = await uploadImage(tempFilePaths[i]);
          if (result.data) {
            // 上传成功，添加到图片列表
            this.proofPics.push(result.data);
            this.returnForm.proofPics = this.proofPics.join(',');
            successCount++;
          }
        } catch (error) {
          console.error('图片上传失败', error);
          uni.showToast({
            title: error.message || '图片上传失败',
            icon: 'none'
          });
        }
      }
      
      uni.hideLoading();
      
      if (successCount > 0) {
        uni.showToast({
          title: `成功上传${successCount}张图片`,
          icon: 'success'
        });
      }
    },
    
    // 预览图片
    previewImage(index) {
      uni.previewImage({
        current: index,
        urls: this.proofPics
      });
    },
    
    // 删除图片
    deleteImage(index) {
      uni.showModal({
        title: '确认删除',
        content: '确定要删除这张图片吗？',
        success: (res) => {
          if (res.confirm) {
            this.proofPics.splice(index, 1);
            this.returnForm.proofPics = this.proofPics.join(',');
            uni.showToast({
              title: '图片已删除',
              icon: 'success'
            });
          }
        }
      });
    },
    
    // 提交退货申请
    submitReturn() {
      if (!this.canSubmit) {
        uni.showToast({
          title: '请完善申请信息',
          icon: 'none'
        });
        return;
      }
      
      // 验证手机号格式
      const phoneRegex = /^1[3-9]\d{9}$/;
      if (!phoneRegex.test(this.returnForm.returnPhone)) {
        uni.showToast({
          title: '请输入正确的手机号',
          icon: 'none'
        });
        return;
      }
      
      uni.showModal({
        title: '确认提交',
        content: '确定要提交退货申请吗？提交后将等待商家审批。',
        success: (res) => {
          if (res.confirm) {
            this.isSubmitting = true;
            
            uni.showLoading({
              title: '提交中...'
            });
            
            // 构造多步骤申请参数
            const multiStepParam = {
              orderId: this.returnForm.orderId,
              returnType: this.returnForm.returnType,
              productItems: this.selectedOrderItems.map(item => ({
                orderItemId: item.id,
                productId: item.productId,
                productCount: item.productQuantity,
                returnAmount: this.getRealPrice(item) * item.productQuantity
              })),
              reason: this.returnForm.reason,
              description: this.returnForm.description,
              proofPics: this.returnForm.proofPics,
              returnName: this.returnForm.returnName,
              returnPhone: this.returnForm.returnPhone
            };

            createReturnApply(multiStepParam).then(res => {
              uni.hideLoading();
              this.isSubmitting = false;
              
              if (res.code === 200) {
                uni.showModal({
                  title: '申请提交成功',
                  content: '您的退货申请已提交，请等待商家审批。您可以在订单详情中查看退货进度。',
                  showCancel: false,
                  confirmText: '知道了',
                  success: () => {
                    uni.navigateBack();
                  }
                });
              } else {
                uni.showToast({
                  title: res.message || '提交失败',
                  icon: 'none'
                });
              }
            }).catch(err => {
              console.error('提交退货申请失败', err);
              uni.hideLoading();
              this.isSubmitting = false;
              
              uni.showToast({
                title: '提交失败',
                icon: 'none'
              });
            });
          }
        }
      });
    },
    
    // 检查是否已有退货申请
    checkExistingReturnApply() {
      fetchOrderReturnApplyList(this.orderId).then(res => {
        if (res.code === 200 && res.data && res.data.length > 0) {
          // 找到对应商品的退货申请
          const returnApply = res.data.find(item => item.productId == this.orderItemId);
          if (returnApply) {
            this.existingReturnApply = returnApply;
            
            // 如果已有退货申请且状态为已确认(status=1)，显示快递单号上传按钮
            if (returnApply.status === 1) {
              uni.showToast({
                title: '您的退货申请已通过，请上传快递单号',
                icon: 'none',
                duration: 3000
              });
            }
          }
        }
      }).catch(err => {
        console.error('获取退货申请列表失败', err);
      });
    },
    
    // 显示快递单号输入弹窗
    showDeliveryInput() {
      this.showDeliveryModal = true;
    },
    
    // 隐藏快递单号输入弹窗
    hideDeliveryModal() {
      this.showDeliveryModal = false;
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
      
      uni.showLoading({
        title: '提交中...'
      });
      
      const deliveryData = {
        returnApplyId: this.existingReturnApply.id,
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
          this.existingReturnApply.deliveryCompany = this.deliveryForm.company;
          this.existingReturnApply.deliverySn = this.deliveryForm.trackingNumber;
          
          this.hideDeliveryModal();
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
    
    // 格式化商品规格
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

    // 获取商品实际支付价格
    getRealPrice(item) {
      // 使用实际支付金额 realAmount
      let realAmount = item.realAmount;

      // 如果没有 realAmount，则手动计算实际支付金额
      if (realAmount === undefined || realAmount === null) {
        const productPrice = item.productPrice || 0;
        const promotionAmount = item.promotionAmount || 0;
        const couponAmount = item.couponAmount || 0;
        const integrationAmount = item.integrationAmount || 0;

        realAmount = productPrice - promotionAmount - couponAmount - integrationAmount;
      }

      // 确保金额不为负数
      realAmount = Math.max(0, realAmount);

      return realAmount.toFixed(2);
    },

    // 新增方法：多步骤流程控制
    // 选择售后类型
    selectReturnType(type) {
      this.returnForm.returnType = type;
    },

    // 切换选择商品
    toggleSelectItem(itemId) {
      // 检查商品是否已申请售后
      const item = this.orderItems.find(item => item.id === itemId);
      if (item && item.hasAfterSale) {
        uni.showToast({
          title: '该商品已申请售后，无法重复申请',
          icon: 'none'
        });
        return;
      }

      const index = this.selectedItems.indexOf(itemId);
      if (index > -1) {
        this.selectedItems.splice(index, 1);
      } else {
        this.selectedItems.push(itemId);
      }
      // 更新选中的商品详情
      this.updateSelectedOrderItems();
    },

    // 更新选中的商品详情
    updateSelectedOrderItems() {
      this.selectedOrderItems = this.orderItems.filter(item =>
        this.selectedItems.includes(item.id)
      );
    },

    // 选择退货原因
    selectReason(reason) {
      this.selectedReason = reason;
      this.returnForm.reason = reason;
    },

    // 打开上传弹窗
    openUploadModal() {
      this.showUploadModal = true;
    },

    // 关闭上传弹窗
    closeUploadModal() {
      this.showUploadModal = false;
    },

    // 保存上传信息
    saveUploadInfo() {
      this.showUploadModal = false;
      uni.showToast({
        title: '保存成功',
        icon: 'success'
      });
    },

    // 下一步
    nextStep() {
      if (this.currentStep < 6) {
        this.currentStep++;
      }
    },

    // 上一步
    prevStep() {
      if (this.currentStep > 1) {
        this.currentStep--;
      }
    },

    // 计算退款金额
    calculateRefundAmount() {
      let total = 0;
      this.selectedOrderItems.forEach(item => {
        // 使用实际支付金额 realAmount，这个金额已经扣除了优惠、折扣等
        // realAmount = productPrice - promotionAmount - couponAmount - integrationAmount
        let realAmount = item.realAmount;

        // 如果没有 realAmount，则手动计算实际支付金额
        if (realAmount === undefined || realAmount === null) {
          const productPrice = item.productPrice || 0;
          const promotionAmount = item.promotionAmount || 0;
          const couponAmount = item.couponAmount || 0;
          const integrationAmount = item.integrationAmount || 0;

          realAmount = productPrice - promotionAmount - couponAmount - integrationAmount;
        }

        // 确保金额不为负数
        realAmount = Math.max(0, realAmount);

        total += realAmount * (item.productQuantity || 1);
      });
      return total.toFixed(2);
    },

    // 提交申请
    submitApplication() {
      // 验证必填信息
      if (this.returnForm.returnType === 2 && !this.returnForm.description.trim()) {
        uni.showToast({
          title: '请填写问题描述',
          icon: 'none'
        });
        return;
      }

      uni.showModal({
        title: '确认提交',
        content: '确定要提交售后申请吗？',
        success: (res) => {
          if (res.confirm) {
            this.doSubmitApplication();
          }
        }
      });
    },

    // 执行提交申请
    doSubmitApplication() {
      uni.showLoading({
        title: '提交中...'
      });

      // 构造多步骤申请参数
      const multiStepParam = {
        orderId: this.returnForm.orderId,
        returnType: this.returnForm.returnType,
        productItems: this.selectedOrderItems.map(item => ({
          orderItemId: item.id,
          productId: item.productId,
          productCount: item.productQuantity,
          returnAmount: this.getRealPrice(item) * item.productQuantity
        })),
        reason: this.returnForm.reason,
        description: this.returnForm.description,
        proofPics: this.proofPics.join(','),
        returnName: this.returnForm.returnName,
        returnPhone: this.returnForm.returnPhone
      };

      // 调用API提交申请
      createReturnApply(multiStepParam).then(res => {
        uni.hideLoading();
        if (res.code === 200) {
          uni.showModal({
            title: '申请提交成功',
            content: '您的售后申请已提交，请等待商家审核。审核通过后您可以在售后订单中查看进度。',
            showCancel: false,
            confirmText: '知道了',
            success: () => {
              // 回到订单列表页并切换到售后tab
              uni.redirectTo({
                url: '/pages/order/order?tab=3' // tab=3 表示售后tab
              });
            }
          });
        } else {
          uni.showToast({
            title: res.message || '提交失败',
            icon: 'none'
          });
        }
      }).catch(err => {
        console.error('提交申请失败', err);
        uni.hideLoading();
        uni.showToast({
          title: '提交失败',
          icon: 'none'
        });
      });
    },

    // 确认已寄出
    confirmShipped() {
      this.currentStep = 5; // 跳转到输入运单号步骤
    },

    // 取消输入运单号
    cancelTracking() {
      this.currentStep = 4; // 返回寄回商品步骤
    },

    // 确认运单号
    confirmTracking() {
      if (!this.trackingNumber.trim()) {
        uni.showToast({
          title: '请输入运单号',
          icon: 'none'
        });
        return;
      }

      uni.showLoading({
        title: '提交中...'
      });

      // 这里调用API提交运单号
      // updateReturnApplyDelivery({
      //   trackingNumber: this.trackingNumber
      // }).then(res => {
      //   uni.hideLoading();
      //   if (res.code === 200) {
      //     this.currentStep = 6; // 跳转到成功页面
      //   }
      // });

      // 临时模拟成功
      setTimeout(() => {
        uni.hideLoading();
        this.currentStep = 6; // 跳转到成功页面
      }, 1000);
    },

    // 提交最终申请
    submitFinalApplication() {
      uni.showToast({
        title: '申请已提交',
        icon: 'success'
      });
      setTimeout(() => {
        uni.navigateBack();
      }, 1500);
    },

    // 关闭页面
    closePage() {
      uni.navigateBack();
    }
  }
};
</script>

<style lang="scss">
// 根据Figma设计稿重新设计样式
page {
  background: #F8F8F8;
}

.page-container {
  min-height: 100vh;
  background: #F8F8F8;
  padding: 16rpx;
  padding-bottom: 120rpx;
}

// 步骤页面
.step-page {
  display: flex;
  flex-direction: column;
}

// 售后类型选择卡片
.type-card {
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 32rpx;
  margin-bottom: 16rpx;

  .card-title {
    font-family: PingFang SC;
    font-size: 32rpx;
    font-weight: 400;
    color: #0A0D05;
    margin-bottom: 30rpx;
  }

  .type-buttons {
    display: flex;

    .type-button {
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 16rpx 24rpx;
      border-radius: 16rpx;
      border: 2rpx solid #EEEEEE;
      margin-right: 16rpx;

      &:last-child {
        margin-right: 0;
      }

      &.active {
        background: #A9FF00;
        border-color: #000000;
      }

      .type-text {
        font-family: PingFang SC;
        font-size: 32rpx;
        font-weight: 400;
        color: #0A0D05;
      }
    }
  }
}

// 商品卡片
.product-card {
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 32rpx;
  margin-bottom: 16rpx;
  display: flex;
  align-items: flex-start;

  // 禁用状态
  &.disabled {
    opacity: 0.6;
    background: #f9f9f9;
  }

  .product-checkbox {
    width: 40rpx;
    height: 40rpx;
    border-radius: 50%;
    border: 3rpx solid #E1E1E1;
    background: #D9D9D9;
    margin-right: 32rpx;
    margin-top: 0;
    align-self: center;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;

    &.checked {
      background: #A9FF00;
      border-color: #000000;

      .checkbox-inner {
        width: 20rpx;
        height: 20rpx;
        background: #000000;
        border-radius: 50%;
      }
    }

    // 禁用状态的复选框
    &.disabled {
      border-color: #cccccc;
      background: #f5f5f5;
    }
  }

  .product-content {
    flex: 1;
    display: flex;
    align-items: flex-start;

    .product-image {
      width: 120rpx;
      height: 120rpx;
      border-radius: 16rpx;
      margin-right: 24rpx;
      background: #F5F5F5;
    }

    .product-details {
      flex: 1;

      .product-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 8rpx;

        .product-name {
          font-family: PingFang SC;
          font-size: 28rpx;
          font-weight: 400;
          color: #0A0D05;
          line-height: 1.4;
          flex: 1;
          margin-right: 16rpx;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }

        .product-price {
          font-family: PingFang SC;
          font-size: 28rpx;
          font-weight: 400;
          color: #0A0D05;
          line-height: 1.4;
          flex-shrink: 0;
        }
      }

      .product-attr {
        font-family: PingFang SC;
        font-size: 24rpx;
        font-weight: 400;
        color: #9FA19D;
        line-height: 1.4;
        margin-bottom: 8rpx;
      }

      .product-quantity {
        font-family: PingFang SC;
        font-size: 24rpx;
        font-weight: 400;
        color: #9FA19D;
      }

      // 售后状态标签
      .after-sale-tag {
        background-color: #ff6b35;
        color: #ffffff;
        padding: 6rpx 12rpx;
        border-radius: 12rpx;
        font-size: 20rpx;
        margin-top: 8rpx;
        display: inline-block;

        .tag-text {
          font-size: 20rpx;
          color: #ffffff;
        }
      }
    }
  }
}

// 原因选择卡片
.reason-card {
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 32rpx;
  margin-bottom: 16rpx;

  .reason-options {
    .reason-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 24rpx 0;
      border-bottom: 1rpx solid #F0F0F0;

      &:last-child {
        border-bottom: none;
      }

      .reason-text {
        flex: 1;
        font-family: PingFang SC;
        font-size: 32rpx;
        font-weight: 400;
        color: #0A0D05;
      }

      .reason-checkbox {
        width: 40rpx;
        height: 40rpx;
        border-radius: 50%;
        border: 3rpx solid #EEEEEE;
        background: #FFFFFF;
        margin-left: 32rpx;
        display: flex;
        align-items: center;
        justify-content: center;

        &.checked {
          background: #A9FF00;
          border-color: #000000;

          .checkbox-inner {
            width: 20rpx;
            height: 20rpx;
            background: #000000;
            border-radius: 50%;
          }
        }
      }
    }
  }
}

// 退款信息卡片
.refund-card {
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 32rpx;
  margin-bottom: 16rpx;

  .refund-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 24rpx 0;
    border-bottom: 1rpx solid #F0F0F0;

    &:last-child {
      border-bottom: none;
    }

    .refund-label {
      font-family: PingFang SC;
      font-size: 28rpx;
      font-weight: 400;
      color: #9FA19D;
    }

    .refund-value {
      font-family: PingFang SC;
      font-size: 28rpx;
      font-weight: 400;
      color: #0A0D05;
    }

    .refund-arrow {
      font-family: PingFang SC;
      font-size: 28rpx;
      font-weight: 400;
      color: #9FA19D;
    }
  }
}

// 底部按钮容器
.bottom-button-container {
  position: fixed;
  left: 0;
  bottom: 0;
  width: 100%;
  background: #FFFFFF;
  padding: 20rpx 32rpx 40rpx 32rpx;
  box-shadow: 0rpx -2rpx 2rpx 0rpx rgba(0, 0, 0, 0.05);

  .bottom-button {
    width: 100%;
    height: 80rpx;
    background: #20201E;
    border-radius: 16rpx;
    display: flex;
    align-items: center;
    justify-content: center;

    &.disabled {
      background: #F5F5F5;

      .bottom-button-text {
        color: #CCCCCC;
      }
    }

    .bottom-button-text {
      font-family: PingFang SC;
      font-size: 32rpx;
      font-weight: 400;
      color: #A9FF00;
    }
  }
}

// 商品信息卡片
.product-info-card {
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 32rpx;
  margin-bottom: 16rpx;

  .card-title {
    font-family: PingFang SC;
    font-size: 32rpx;
    font-weight: 500;
    color: #0A0D05;
    margin-bottom: 24rpx;
  }

  .product-card {
    display: flex;
    align-items: flex-start;
    margin-bottom: 24rpx;

    &:last-child {
      margin-bottom: 0;
    }

    .product-image {
      width: 120rpx;
      height: 120rpx;
      border-radius: 16rpx;
      margin-right: 24rpx;
      background: #F5F5F5;
      flex-shrink: 0;
    }

    .product-content {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      height: 120rpx;

      .product-details {
        .product-header {
          display: flex;
          justify-content: space-between;
          align-items: flex-start;
          margin-bottom: 8rpx;

          .product-name {
            font-family: PingFang SC;
            font-size: 28rpx;
            font-weight: 400;
            color: #0A0D05;
            line-height: 1.4;
            flex: 1;
            margin-right: 16rpx;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
          }

          .product-price {
            font-family: PingFang SC;
            font-size: 28rpx;
            font-weight: 400;
            color: #0A0D05;
            line-height: 1.4;
            flex-shrink: 0;
          }
        }

        .product-attr {
          font-family: PingFang SC;
          font-size: 24rpx;
          font-weight: 400;
          color: #9FA19D;
          line-height: 1.4;
          margin-bottom: 8rpx;
        }

        .product-quantity {
          font-family: PingFang SC;
          font-size: 24rpx;
          font-weight: 400;
          color: #9FA19D;
          line-height: 1.4;
        }
      }
    }
  }
}

// 上传弹窗样式
.upload-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.upload-modal {
  width: 90%;
  max-width: 600rpx;
  background: #FFFFFF;
  border-radius: 16rpx;
  overflow: hidden;

  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 32rpx;
    border-bottom: 1rpx solid #F0F0F0;

    .modal-title {
      font-family: PingFang SC;
      font-size: 32rpx;
      font-weight: 500;
      color: #0A0D05;
    }

    .modal-close {
      font-family: PingFang SC;
      font-size: 40rpx;
      font-weight: 400;
      color: #9FA19D;
      width: 40rpx;
      height: 40rpx;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }

  .modal-content {
    padding: 32rpx;
    max-height: 60vh;
    overflow-y: auto;

    .upload-section {
      margin-bottom: 32rpx;

      .upload-grid {
        display: flex;
        flex-wrap: wrap;

        .upload-item {
          position: relative;
          width: 120rpx;
          height: 120rpx;
          margin-right: 16rpx;
          margin-bottom: 16rpx;

          .upload-image {
            width: 100%;
            height: 100%;
            border-radius: 8rpx;
            background: #F5F5F5;
          }

          .delete-btn {
            position: absolute;
            top: -8rpx;
            right: -8rpx;
            width: 32rpx;
            height: 32rpx;
            background: #FF4444;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20rpx;
            color: #FFFFFF;
          }
        }

        .upload-add {
          width: 120rpx;
          height: 120rpx;
          background: #F8F8F8;
          border: 2rpx dashed #DDDDDD;
          border-radius: 8rpx;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 16rpx;
          margin-bottom: 16rpx;

          .add-icon {
            font-family: PingFang SC;
            font-size: 48rpx;
            font-weight: 300;
            color: #CCCCCC;
          }
        }
      }
    }

    .description-section {
      .section-label {
        font-family: PingFang SC;
        font-size: 28rpx;
        font-weight: 400;
        color: #0A0D05;
        margin-bottom: 16rpx;
        display: block;
      }

      .description-input {
        width: 100%;
        min-height: 200rpx;
        padding: 16rpx;
        background: #F8F8F8;
        border-radius: 8rpx;
        font-family: PingFang SC;
        font-size: 28rpx;
        font-weight: 400;
        color: #0A0D05;
        border: none;
        resize: none;
        box-sizing: border-box;

        &::placeholder {
          color: #9FA19D;
        }
      }

      .description-count {
        text-align: right;
        margin-top: 8rpx;
        font-family: PingFang SC;
        font-size: 24rpx;
        font-weight: 400;
        color: #9FA19D;
      }
    }
  }

  .modal-footer {
    padding: 32rpx;
    border-top: 1rpx solid #F0F0F0;

    .modal-button {
      width: 100%;
      height: 80rpx;
      background: #A9FF00;
      border-radius: 16rpx;
      display: flex;
      align-items: center;
      justify-content: center;

      .modal-button-text {
        font-family: PingFang SC;
        font-size: 32rpx;
        font-weight: 400;
        color: #0A0D05;
      }
    }
  }
}
</style>