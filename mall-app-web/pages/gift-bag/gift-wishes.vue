<template>
  <view class="wishes-container">
    <!-- 自定义导航栏 -->
    <nav-bar :placeholder="true" bg-color="rgba(255, 255, 255, 1)" color="#000000" :has-slot="true" :back="true">
      <view class="nav-title-container">
        <text class="nav-title">填写祝福</text>
      </view>
    </nav-bar>

    <!-- 选择祝福 -->
    <view class="wishes-content">
      <!-- 顶部提示 -->
      <view class="wish-preview-container">
        <view class="wish-tip-header">
          <text class="wish-tip-text">-请写下你的祝福 -</text>
        </view>

        <view class="wish-preview-bg">
          <image v-if="selectedImage" class="bg-image" :src="selectedImage.imageUrl" mode="aspectFill"></image>
        </view>
        <view class="wish-card" @tap="showCustomInput">
          <view class="wish-card-image" v-if="selectedImage">
            <image class="card-image" :src="selectedImage.imageUrl" mode="aspectFill"></image>
          </view>
          <view class="wish-text-box">
            <text class="wish-preview-text font-sourcehansans">{{selectedWish ? '"'+selectedWish.content+'"' :
              '"身体棒棒，钱包胖胖"'}}</text>
            <view class="edit-hint-row">
              <text class="edit-hint font-sourcehansans">点击编辑祝福</text>
              <view class="edit-icon">
                <text class="edit-icon-text">✎</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 选择图片祝福 -->
      <view class="wish-images-section">
        <view class="wish-section-title">
          <text class="section-title-text font-sourcehansans">- 左右滑动切换卡券图案 -</text>
        </view>

        <scroll-view class="wish-images-scroll" scroll-x="true" show-scrollbar="false">
          <view class="wish-images-list">
            <view v-for="(item, index) in imageWishes" :key="index" class="wish-image-container"
              :class="{ active: selectedImageIndex === index }" @tap="selectImage(index)">
              <image class="wish-image" :src="item.imageUrl" mode="aspectFill"></image>
              <view class="selection-indicator" v-if="selectedImageIndex === index">
                <view class="check-circle">
                  <view class="check-mark"></view>
                </view>
              </view>
            </view>
          </view>
        </scroll-view>
      </view>

      <!-- 文字祝福选择 -->
      <view class="text-wishes-section">
        <view class="wish-section-title">
          <text class="section-title-text font-sourcehansans">- 点击祝福语 -</text>
        </view>

        <scroll-view class="text-wishes-scroll" scroll-x="true" show-scrollbar="false">
          <view class="text-wishes-list">
            <view v-for="(item, index) in textWishes" :key="index" class="text-wish-item"
              :class="{ active: selectedWishId === item.id }" @tap="selectWish(item)">
              <text class="text-wish-content">{{'"' + item.content + '"'}}</text>
            </view>
          </view>
        </scroll-view>
      </view>
    </view>

    <!-- 底部操作栏 -->
    <view class="bottom-action-bar">
      <view class="action-btns">
        <button class="submit-btn" @tap="submitWish">{{payBtnText}} ¥ {{totalPrice}}</button>
      </view>
    </view>

    <!-- 自定义祝福输入弹窗 -->
    <view class="custom-popup" v-if="showCustomPopup" @tap.stop="hideCustomInput">
      <view class="custom-popup-mask"></view>
      <view class="custom-popup-content" @tap.stop>
        <view class="custom-popup-header">
          <text class="custom-popup-title">自定义祝福</text>
          <view class="custom-popup-close" @tap="hideCustomInput">
            <text>×</text>
          </view>
        </view>
        <view class="custom-input-area">
          <textarea class="custom-wish-textarea" v-model="customWishText" placeholder="请输入您的祝福语（不超过50字）" maxlength="50"
            auto-height :adjust-position="false" :show-confirm-bar="false"></textarea>
          <view class="word-count">
            <text>{{customWishText.length}}/50</text>
          </view>
        </view>
        <view class="custom-popup-footer">
          <button class="confirm-btn" @tap="confirmCustomWish">确认</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getWishList } from '@/api/gift';
import { generateGiftOrder, getWechatPayParams, payOrderSuccess } from '@/api/order';
import NavBar from '@/components/nav-bar.vue';

export default {
  components: {
    'nav-bar': NavBar
  },
  data() {
    return {
      textWishes: [], // 文字祝福列表
      imageWishes: [], // 图片祝福列表
      selectedWish: null, // 选中的祝福文字
      selectedWishId: null, // 选中的祝福ID
      selectedImageIndex: 0, // 选中的图片索引
      selectedImage: null, // 选中的图片对象
      customWishText: '', // 自定义祝福文字
      showCustomPopup: false, // 是否显示自定义弹窗
      textPage: 1, // 文字祝福当前页
      imagePage: 1, // 图片祝福当前页
      pageSize: 10, // 每页数据条数
      textTotal: 0, // 文字祝福总数
      imageTotal: 0, // 图片祝福总数
      isLoadingText: false, // 是否正在加载文字祝福
      isLoadingImage: false, // 是否正在加载图片祝福
      giftCartItems: [], // 购物车商品
      totalPrice: 0, // 总价格
      isCustomWish: false, // 是否使用自定义祝福
      isLoading: false, // 是否正在处理支付
      payType: 2, // 支付方式：0-未支付, 1-支付宝, 2-微信
      payBtnText: '确认支付', // 支付按钮文本
      currentOrderData: null, // 当前订单数据
    };
  },
  onLoad() {
    // 获取购物车商品并计算总价
    this.loadGiftCart();
    // 获取祝福语列表
    this.loadTextWishes();
    this.loadImageWishes();
    
    // 重置支付按钮文本
    this.payBtnText = '确认支付';
    
    // 添加页面显示事件监听
    uni.$on('refreshWishPage', this.refresh);
  },
  onShow() {
    // 确保背景图片正确应用样式
    setTimeout(() => {
      this.applyBlurEffect();
    }, 300);
  },
  onUnload() {
    // 移除事件监听
    uni.$off('refreshWishPage');
  },
  methods: {
    // 加载购物车
    loadGiftCart() {
      // 首先检查是否有来自商品详情页的单个商品数据
      const singleGiftData = uni.getStorageSync('singleGiftData');
      if (singleGiftData) {
        try {
          const singleItem = JSON.parse(singleGiftData);
          this.giftCartItems = [singleItem]; // 将单个商品转为数组格式
          // 清除单个商品数据，避免重复使用
          uni.removeStorageSync('singleGiftData');
          console.log('加载来自商品详情页的礼品数据:', this.giftCartItems);
          this.calculateTotalPrice();
          return;
        } catch (error) {
          console.error('解析单个商品数据失败:', error);
          uni.removeStorageSync('singleGiftData');
        }
      }
      
      // 如果没有单个商品数据，则加载购物车数据
      const cartItems = uni.getStorageSync('giftCartItems');
      if (cartItems) {
        try {
          this.giftCartItems = JSON.parse(cartItems);
          console.log('加载来自礼品列表页的购物车数据:', this.giftCartItems);
        } catch (error) {
          console.error('解析购物车数据失败:', error);
          this.giftCartItems = [];
        }
      } else {
        this.giftCartItems = [];
      }
      
      this.calculateTotalPrice();
    },
    
    // 计算总价
    calculateTotalPrice() {
      this.totalPrice = this.giftCartItems.reduce((sum, item) => {
        return sum + (item.price * item.quantity);
      }, 0).toFixed(2);
    },
    
    // 加载文字祝福
    async loadTextWishes(refresh = false) {
      if (this.isLoadingText) return;
      
      this.isLoadingText = true;
      
      if (refresh) {
        this.textPage = 1;
        this.textWishes = [];
      }
      
      try {
        const res = await getWishList({
          type: 0, // 文字祝福
          category: true, // 送礼祝福
          pageNum: this.textPage,
          pageSize: this.pageSize
        });
        
        if (res && res.data) {
          const { list, total } = res.data;
          
          if (refresh) {
            this.textWishes = list || [];
          } else {
            this.textWishes = [...this.textWishes, ...(list || [])];
          }
          
          this.textTotal = total;
        }
      } catch (error) {
        console.error('获取文字祝福失败:', error);
        // 使用示例数据
        // 修改示例数据确保正确显示
        this.textWishes = [
          { id: '1', content: '有你在真好', type: 0 },
          { id: '2', content: '日子如花花，每天笑哈哈', type: 0 },
          { id: '3', content: '烦恼都不见，愿望都实现', type: 0 },
          { id: '4', content: '多喝热水少叹气', type: 0 },
          { id: '5', content: '愿晚风夏夜，吹走你的不愉快', type: 0 }
        ];
        this.textTotal = this.textWishes.length;
        uni.showToast({
          title: '使用本地祝福语展示',
          icon: 'none'
        });
      } finally {
        // 如果没有选中的祝福且有祝福数据，默认选择第一个
        if (!this.selectedWish && this.textWishes && this.textWishes.length > 0) {
          this.selectWish(this.textWishes[0]);
        }
        this.isLoadingText = false;
      }
    },
    
    // 加载图片祝福
    async loadImageWishes(refresh = false) {
      if (this.isLoadingImage) return;
      
      this.isLoadingImage = true;
      
      if (refresh) {
        this.imagePage = 1;
        this.imageWishes = [];
      }
      
      try {
        const res = await getWishList({
          type: 1, // 图片祝福
          category: true, // 送礼祝福
          pageNum: this.imagePage,
          pageSize: this.pageSize
        });
        
        if (res && res.data) {
          const { list, total } = res.data;
          
          if (refresh) {
            this.imageWishes = list || [];
          } else {
            this.imageWishes = [...this.imageWishes, ...(list || [])];
          }
          
          this.imageTotal = total;
        }
      } catch (error) {
        console.error('获取图片祝福失败:', error);
      } finally {
        // 默认选择第一张图片
        if (this.imageWishes && this.imageWishes.length > 0) {
          try {
            this.selectedImageIndex = 0;
            this.selectedImage = this.imageWishes[0];
          } catch (e) {
            console.error('选择默认图片失败:', e);
          }
        }
        this.isLoadingImage = false;
      }
    },
    
    // 加载更多文字祝福
    loadMoreTextWishes() {
      if (this.textWishes.length >= this.textTotal) return;
      
      this.textPage++;
      this.loadTextWishes();
    },
    
    // 加载更多图片祝福
    loadMoreImageWishes() {
      if (this.imageWishes.length >= this.imageTotal) return;
      
      this.imagePage++;
      this.loadImageWishes();
    },
    
    // 选择祝福文字
    selectWish(wish) {
      this.selectedWish = wish;
      this.selectedWishId = wish.id;
      this.isCustomWish = false;
    },
    
    // 选择图片
    selectImage(index) {
      if (index === this.selectedImageIndex) return;
      
      try {
        this.selectedImageIndex = index;
        if (this.imageWishes && this.imageWishes.length > index) {
          this.selectedImage = this.imageWishes[index];
          // 应用新的背景模糊效果
          setTimeout(() => {
            this.applyBlurEffect();
          }, 50);
        }
      } catch (error) {
        console.error('选择图片出错:', error);
      }
    },
    
    // 获取随机祝福
    getRandomWish() {
      if (this.textWishes.length === 0) return;
      
      const randomIndex = Math.floor(Math.random() * this.textWishes.length);
      this.selectWish(this.textWishes[randomIndex]);
    },
    
    // 显示自定义输入
    showCustomInput() {
      this.showCustomPopup = true;
    },
    
    // 隐藏自定义输入
    hideCustomInput() {
      this.showCustomPopup = false;
    },
    
    // 确认自定义祝福
    confirmCustomWish() {
      if (!this.customWishText.trim()) {
        uni.showToast({
          title: '请输入祝福内容',
          icon: 'none'
        });
        return;
      }
      
      // 创建自定义祝福对象
      this.selectedWish = {
        id: 'custom',
        content: this.customWishText,
        type: 0
      };
      
      this.selectedWishId = 'custom';
      this.isCustomWish = true;
      this.hideCustomInput();
    },
    
    // 提交祝福
    submitWish() {
      if (!this.selectedWish) {
        uni.showToast({
          title: '请选择祝福语',
          icon: 'none'
        });
        return;
      }
      
      if (!this.selectedImage && this.imageWishes.length > 0) {
        this.selectedImage = this.imageWishes[this.selectedImageIndex];
      }
      
      // 如果已有订单数据，直接进行支付
      if (this.currentOrderData) {
        this.wxPay(this.currentOrderData);
        return;
      }
      
      if (this.isLoading) return;
      this.isLoading = true;
      
      // 显示加载提示
      uni.showLoading({
        title: '订单处理中...'
      });
      
      // 准备下单参数
      const directBuyParamList = this.giftCartItems.map(item => {
        return {
          productId: item.id,
          productSkuId: item.productSkuId,
          quantity: item.quantity,
          price: item.price,
          productAttr: item.productAttr || '',
          productBrand: item.productBrand || '',
          productCategoryId: item.productCategoryId || '',
          productName: item.name || '',
          productPic: item.pic || '',
          productSkuCode: item.productSkuCode || '',
          productSn: item.productSn || '',
          productSubTitle: item.productSubTitle || '',
          deliveryType: 0 // 默认快递配送
        };
      });
      
      // 创建送礼订单参数
      const giftOrderParam = {
        directBuyParamList: directBuyParamList,
        payType: this.payType, // 微信支付
        giftMessage: this.selectedWish.content,
        giftPic: this.selectedImage ? this.selectedImage.imageUrl : ''
      };
      
      // 保存祝福数据到本地
      const wishData = {
        text: this.selectedWish.content,
        textId: this.isCustomWish ? null : this.selectedWish.id,
        imageUrl: this.selectedImage ? this.selectedImage.imageUrl : null,
        imageId: this.selectedImage ? this.selectedImage.id : null,
        isCustom: this.isCustomWish
      };
      uni.setStorageSync('giftWishData', JSON.stringify(wishData));
      
      // 调用创建送礼订单API
      generateGiftOrder(giftOrderParam).then(res => {
        uni.hideLoading();
        
        if (res.code === 200) {
          // 下单成功，准备调用支付接口
          console.log('下单成功，订单信息：', res.data);
          
          // 保存订单信息
          if (res.data && res.data.order && res.data.order.id) {
            const orderData = {
              orderId: res.data.order.id,
              orderSn: res.data.order.orderSn,
              price: res.data.order.payAmount,
              items: res.data.orderItemList || [],
              createTime: new Date().toISOString()
            };
            uni.setStorageSync('giftOrderData', JSON.stringify(orderData));
            
            // 保存当前订单数据，以便支付失败后重新支付
            this.currentOrderData = res.data.order;
            
            // 调用微信支付
            this.wxPay(res.data.order);
          } else {
            uni.showToast({
              title: '订单创建成功，但缺少支付信息',
              icon: 'none'
            });
            this.isLoading = false;
          }
        } else {
          uni.showToast({
            title: res.message || '下单失败',
            icon: 'none'
          });
          this.isLoading = false;
        }
      }).catch(err => {
        uni.hideLoading();
        console.error('下单失败:', err);
        
        uni.showToast({
          title: '下单失败，请重试',
          icon: 'none'
        });
        this.isLoading = false;
      });
    },
    
    // 微信支付
    wxPay(orderData) {
      // 支付前先请求订阅消息
      this.requestSubscriptionMessage(orderData);
    },

    // 请求订阅消息
    requestSubscriptionMessage(orderData) {
      // #ifdef MP-WEIXIN
      const templateIds = ['LXeyEXDeNIeqeSQ-ZtlYUI-6xuveTFybNdWITAITq3A']; // 礼物领取提醒模板ID
      
      uni.requestSubscribeMessage({
        tmplIds: templateIds,
        success: (res) => {
          console.log('订阅消息请求结果:', res);
          // 无论用户是否同意订阅，都继续支付流程
          this.proceedToPayment(orderData);
        },
        fail: (err) => {
          console.error('订阅消息请求失败:', err);
          // 即使订阅失败也继续支付流程
          this.proceedToPayment(orderData);
        }
      });
      // #endif
      
      // #ifndef MP-WEIXIN
      // 非微信小程序环境直接进入支付
      this.proceedToPayment(orderData);
      // #endif
    },

    // 处理支付逻辑
    proceedToPayment(orderData) {
      // 获取用户信息
      const userInfo = uni.getStorageSync('userInfo') || {};
      
      // 检查用户openId
      if (!userInfo.openid && !userInfo.wxOpenId) {
        uni.showToast({
          title: '用户信息不完整，请重新登录',
          icon: 'none'
        });
        
        setTimeout(() => {
          uni.navigateTo({
            url: '/pages/public/register'
          });
        }, 1500);
        this.isLoading = false;
        return;
      }
      
      // 准备支付参数
      const payData = {
        orderSn: orderData.orderSn,
        amount: parseInt(parseFloat(orderData.payAmount || this.totalPrice) * 100), // 转换为分
        spbillCreateIp: '127.0.0.1', // 客户端IP
        openId: userInfo.openid || userInfo.wxOpenId // 用户openId
      };
      
      uni.showLoading({
        title: '支付处理中...'
      });
      
      // 支付前更新按钮文本
      this.payBtnText = '处理中...';
      
      // 获取微信支付参数
      getWechatPayParams(payData).then(res => {
        uni.hideLoading();
        
        if (res.code === 200 && res.data) {
          const payParams = res.data;
          
          // 调用微信支付API (仅在微信小程序环境)
          // #ifdef MP-WEIXIN
          uni.requestPayment({
            provider: 'wxpay',
            timeStamp: payParams.timeStamp,
            nonceStr: payParams.nonceStr,
            package: payParams.packageValue,
            signType: payParams.signType,
            paySign: payParams.paySign,
            success: (res) => {
              console.log('支付成功', res);
              console.log('订单数据', orderData);
              console.log('订单ID', orderData.id || orderData.orderId);
              // 重置状态
              this.currentOrderData = null;
              this.payBtnText = '确认支付';
              this.paySuccess(orderData);
            },
            fail: (err) => {
              console.error('支付失败', err);
              
              let errMsg = '支付失败';
              if (err.errMsg === 'requestPayment:fail cancel') {
                errMsg = '支付已取消';
                this.payBtnText = '继续支付';
              } else {
                // 其他支付失败情况
                this.payBtnText = '重新支付';
              }
              
              uni.showToast({
                title: errMsg,
                icon: 'none'
              });
            },
            complete: () => {
              this.isLoading = false;
            }
          });
          // #endif
          
          // 非微信小程序环境
          // #ifndef MP-WEIXIN
          uni.showToast({
            title: '当前环境不支持微信支付',
            icon: 'none'
          });
          this.isLoading = false;
          this.payBtnText = '确认支付';
          // #endif
        } else {
          uni.showToast({
            title: res.message || '获取支付参数失败',
            icon: 'none'
          });
          this.isLoading = false;
          this.payBtnText = '重新支付';
        }
      }).catch(err => {
        uni.hideLoading();
        console.error('获取支付参数失败:', err);
        
        uni.showToast({
          title: '获取支付参数失败',
          icon: 'none'
        });
        this.isLoading = false;
        this.payBtnText = '重新支付';
      });
    },
    
    // 支付成功处理
    paySuccess(orderData) {
      // 调用支付成功接口更新订单状态
      this.callPaySuccessApi(orderData);
      
      // 清空购物车和单个商品数据
      uni.removeStorageSync('giftCartItems');
      uni.removeStorageSync('singleGiftData');
      
      // 确保有订单ID
      const orderId = orderData.id || orderData.orderId;
      
      if (!orderId) {
        console.error('支付成功但未获取到订单ID:', orderData);
        uni.showToast({
          title: '获取订单信息失败',
          icon: 'none'
        });
        
        // 如果没有订单ID，尝试从存储的订单数据中获取
        try {
          const storedOrderData = uni.getStorageSync('giftOrderData');
          if (storedOrderData) {
            const parsedOrderData = JSON.parse(storedOrderData);
            if (parsedOrderData && parsedOrderData.orderId) {
              // 跳转到支付成功页面
              uni.redirectTo({
                url: '/pages/gift-bag/gift-confirm?orderId=' + parsedOrderData.orderId
              });
              return;
            }
          }
        } catch (error) {
          console.error('解析存储的订单数据失败:', error);
        }
        
        // 如果仍然没有订单ID，跳转到礼物页面列表
        setTimeout(() => {
          uni.redirectTo({
            url: '/pages/gift-bag/gift-list'
          });
        }, 1500);
        return;
      }
      
      // 跳转到支付成功页面
      uni.redirectTo({
        url: '/pages/gift-bag/gift-confirm?orderId=' + orderId
      });
    },
    
    // 调用支付成功接口
    async callPaySuccessApi(orderData) {
      try {
        const orderId = orderData.id || orderData.orderId;
        if (!orderId) {
          console.error('无法获取订单ID，跳过支付成功接口调用');
          return;
        }
        
        // 获取送礼消息和图片
        const giftMessage = this.selectedWish ? this.selectedWish.content : '';
        const giftPic = this.selectedImage ? this.selectedImage.imageUrl : '';
        
        // 构建 POST 请求体参数
        const params = {
          orderId: orderId,
          payType: this.payType || 2
        };
        
        // 添加送礼消息参数（如果存在）
        if (giftMessage) {
          params.giftMessage = giftMessage;
        }
        
        // 添加礼品图片参数（如果存在）
        if (giftPic) {
          params.giftPic = giftPic;
        }
        
        console.log('调用支付成功接口，参数:', params);
        console.log('送礼消息:', giftMessage);
        console.log('礼品图片:', giftPic);
        
        const response = await payOrderSuccess(params);
        
        if (response && response.code === 200) {
          console.log('支付成功接口调用成功:', response);
        } else {
          console.error('支付成功接口调用失败:', response);
        }
      } catch (error) {
        console.error('调用支付成功接口出错:', error);
        // 不阻断用户流程，只记录错误
      }
    },
    
    // 刷新数据
    refresh() {
      try {
        // 重置状态
        this.selectedWish = null;
        this.selectedWishId = null;
        this.selectedImageIndex = 0;
        this.selectedImage = null;
        this.currentOrderData = null;
        this.payBtnText = '确认支付';
        
        // 重新加载数据
        this.loadTextWishes(true);
        this.loadImageWishes(true);
        
        // 更新购物车
        this.loadGiftCart();
      } catch (error) {
        console.error('刷新数据出错:', error);
      }
    },
    
    // 修复文件引用
    fixImageUrl(url) {
      if (!url) return '';
      if (url.indexOf('http') === 0) return url;
      return url;
    },
    
    // 应用模糊效果
    applyBlurEffect() {
      try {
        // 在某些原生环境中，CSS filter可能不生效
        // 这里我们确保背景元素已正确应用样式
        if (!this.selectedImage) return;
        
        // 在小程序中，这里可以通过额外的方式应用模糊效果
        // 例如调整元素样式或类名
        const query = uni.createSelectorQuery().in(this);
        query.select('.bg-image').boundingClientRect(data => {
          if (data) {
            console.log('背景图片元素已找到，样式应该已应用');
          }
        }).exec();
      } catch (error) {
        console.error('应用模糊效果失败:', error);
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.wishes-container {
  width: 100%;
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 140rpx;
}

/* 导航栏样式 */
.nav-title-container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  
  .nav-title {
    font-size: 18px;
    color: #000000;
    font-weight: 500;
  }
}

/* 主内容区域 */
.wishes-content {
  
  /* 顶部提示 */
  .wish-preview-container {
    padding: 0;
    position: relative;
    height: 900rpx; /* 增加容器高度以容纳更大的卡片 */
    overflow: hidden;
    width: 100%;
    
    /* 顶部提示样式调整 */
    .wish-tip-header {
      position: relative;
      padding: 20rpx 0;
      text-align: center;
      z-index: 10;
      
      .wish-tip-text {
        font-size: 30rpx;
        color: #333333;
        font-weight: 500;
      }
    }
    
    .wish-preview-bg {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-color: #f5f5f5;
      z-index: 1;
      overflow: hidden;
      
      .bg-image {
        width: 100%;
        height: 670rpx;
        opacity: 0.6;
        object-fit: cover;
        filter: blur(15px);
        transform: scale(1.2);
      }
      
      &::after {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(255, 255, 255, 0.3);
      }
    }
    
    .wish-card {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      width: 470rpx;
      height: 600rpx; /* 增加卡片高度以容纳更大的图片 */
      background-color: #FFFFFF;
      border-radius: 10rpx;
      box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.08);
      overflow: hidden;
      z-index: 5;
      padding-bottom: 30rpx;
      
      .wish-card-image {
        width: 100%;
        height: 352rpx; /* 修改为4:3比例 (470rpx * 3/4 = 352.5rpx) */
        overflow: hidden;
        padding: 20rpx;
        box-sizing: border-box;
        
        .card-image {
          width: 100%;
          height: 100%;
          object-fit: cover;
          border-radius: 8rpx;
        }
      }
      
      .wish-text-box {
        padding: 25rpx 25rpx 50rpx; /* 减少上下内边距，优化布局 */
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        margin: 0 25rpx 25rpx;
        border: 1rpx dashed rgba(0, 0, 0, 0.3);
        border-radius: 12rpx;
        position: relative;
        
        .wish-preview-text {
          font-size: 36rpx;
          color: #000000;
          line-height: 1.6;
          text-align: center;
          font-weight: 500;
          margin-bottom: 10rpx;
        }
        
        .edit-hint-row {
          position: absolute;
          bottom: 20rpx;
          right: 20rpx;
          display: flex;
          align-items: center;
          
          .edit-hint {
            font-size: 24rpx;
            color: #999999;
            line-height: 1.45;
            font-weight: 400;
          }
          
          .edit-icon {
            width: 28rpx;
            height: 28rpx;
            margin-left: 8rpx;
            display: flex;
            align-items: center;
            justify-content: center;
            
            .edit-icon-text {
              font-size: 26rpx;
              color: #999999;
              line-height: 1;
            }
          }
        }
      }
    }
  }
  
  /* 图片选择区域 */
  .wish-images-section {
    margin: 0 30rpx 0rpx;
    
    .wish-section-title {
      text-align: center;
      margin-bottom: 30rpx;
      
      .section-title-text {
        font-size: 28rpx;
        color: #000000;
        font-weight: 500;
        line-height: 1.45;
      }
    }
    
    .wish-images-scroll {
      width: 100%;
      white-space: nowrap;
      
      .wish-images-list {
        display: inline-flex;
        padding: 10rpx 0 20rpx;
        
        .wish-image-container {
          width: 220rpx;
          height: 165rpx; /* 修改为4:3比例 (220rpx * 3/4 = 165rpx) */
          border-radius: 12rpx;
          overflow: hidden;
          margin-right: 30rpx;
          position: relative;
          transition: all 0.3s ease;
          box-shadow: 0 4rpx 10rpx rgba(0, 0, 0, 0.08);
          
          &.active {
            width: 240rpx;
            height: 180rpx; /* 修改为4:3比例 (240rpx * 3/4 = 180rpx) */
            transform: translateY(-15rpx);
            box-shadow: 0 8rpx 16rpx rgba(0, 0, 0, 0.12);
          }
          
          .wish-image {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }
          
          .selection-indicator {
            position: absolute;
            top: 10rpx;
            right: 10rpx;
            z-index: 2;
            
            .check-circle {
              width: 40rpx;
              height: 40rpx;
              border-radius: 50%;
              background: linear-gradient(135deg, #C8EBF8 0%, #E2F1F5 100%);
              display: flex;
              align-items: center;
              justify-content: center;
              border: 1rpx solid rgba(255, 255, 255, 0.6);
              
              .check-mark {
                width: 20rpx;
                height: 12rpx;
                border-left: 3rpx solid #000;
                border-bottom: 3rpx solid #000;
                transform: rotate(-45deg) translate(1rpx, -2rpx);
              }
            }
          }
        }
      }
    }
  }
  
  /* 文字祝福选择区域 - 按照Figma设计实现 */
  .text-wishes-section {
    margin: 0 30rpx 60rpx;
    
    .wish-section-title {
      text-align: center;
      margin-bottom: 30rpx;
      
      .section-title-text {
        font-size: 28rpx;
        color: #000000;
        font-weight: 500;
        line-height: 1.45;
        font-family: 'Source Han Sans SC', sans-serif;
      }
    }
    
    .text-wishes-scroll {
      width: 100%;
      white-space: nowrap;
      height: 170rpx; /* 固定高度容纳两行 */
    }
    
    .text-wishes-list {
      display: flex;
      flex-wrap: wrap;
      width: max-content;
      height: 170rpx; /* 固定高度 */
      padding: 0 10rpx;
      flex-direction: column; /* 垂直排列 */
      
      .text-wish-item {
        padding: 15rpx 30rpx;
        border-radius: 100rpx;
        border: 1rpx solid #999999;
        margin: 0 10rpx 20rpx;
        transition: all 0.3s;
        background-color: transparent;
        height: 55rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0; /* 防止压缩 */
        
        &.active {
          background: linear-gradient(to right, #C8EBF8, #E2F1F5);
          border: none;
        }
        
        .text-wish-content {
          font-size: 24rpx;
          color: #000000;
          line-height: 1.45;
          font-family: 'Source Han Sans SC', sans-serif;
          font-weight: 400;
          white-space: nowrap;
        }
      }
    }
  }
}

/* 底部操作栏 */
.bottom-action-bar {
  position: fixed;
  bottom: 20rpx;
  left: 0;
  width: 100%;
  height: 140rpx;
  background-color: #FFFFFF;
  padding: 20rpx 30rpx;
  box-sizing: border-box;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
  z-index: 100;
  
  .action-btns {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    
    .submit-btn {
      width: 80%;
      height: 80rpx;
      background: linear-gradient(135deg, #C8EBF8 0%, #E2F1F5 100%);
      color: #000000;
      font-size: 28rpx;
      font-weight: 500;
      border-radius: 40rpx;
      border: none;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }
}

/* 自定义祝福弹窗 */
.custom-popup {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  z-index: 1000;
  
  .custom-popup-mask {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
  }
  
  .custom-popup-content {
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    background-color: #FFFFFF;
    border-top-left-radius: 30rpx;
    border-top-right-radius: 30rpx;
    padding: 30rpx;
    box-sizing: border-box;
    
    .custom-popup-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 30rpx;
      
      .custom-popup-title {
        font-size: 32rpx;
        font-weight: 500;
        color: #000000;
      }
      
      .custom-popup-close {
        width: 48rpx;
        height: 48rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        
        text {
          font-size: 48rpx;
          color: #999999;
          line-height: 1;
        }
      }
    }
    
    .custom-input-area {
      position: relative;
      margin-bottom: 30rpx;
      
      .custom-wish-textarea {
        width: 100%;
        min-height: 200rpx;
        background-color: #f5f5f5;
        border-radius: 20rpx;
        padding: 20rpx;
        font-size: 28rpx;
        color: #333333;
        box-sizing: border-box;
      }
      
      .word-count {
        position: absolute;
        bottom: 20rpx;
        right: 20rpx;
        
        text {
          font-size: 24rpx;
          color: #999999;
        }
      }
    }
    
    .custom-popup-footer {
      display: flex;
      justify-content: center;
      
      .confirm-btn {
        width: 100%;
        height: 80rpx;
        background: linear-gradient(135deg, #C8EBF8 0%, #E2F1F5 100%);
        color: #000000;
        font-size: 28rpx;
        font-weight: 500;
        border-radius: 40rpx;
        border: none;
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }
  }
}

/* 移除外部编辑图标 */
.edit-icon {
  display: none;
}

/* 引入Source Han Sans字体 */
.font-sourcehansans {
  font-family: 'Source Han Sans SC', sans-serif;
}
</style> 