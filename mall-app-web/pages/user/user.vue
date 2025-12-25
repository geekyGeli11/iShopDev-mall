<template>
  <view class="container">
    <!-- 顶部区域容器（包含导航栏和用户信息的统一背景） -->
    <view class="top-section">
      <nav-bar
        :placeholder="true" 
        bg-color="transparent" 
        :has-slot="false" 
        :titleCenter="false"
      >
      </nav-bar>
    
      <!-- 用户信息区域 -->
      <view class="user-header">
        <!-- 已登录状态 -->
        <template v-if="hasLogin">
          <view class="user-avatar-container">
            <view class="avatar-bg">
              <view class="avatar-bg-blur"></view>
            </view>
            <image
              class="user-avatar"
              :src="userInfo.icon || 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/miss-face.png'"
            ></image>
          </view>

          <view class="user-info">
            <view class="username-container">
              <text class="username">{{ userInfo.nickname || '用户' }}</text>
              <view class="vip-badge">
                <text class="vip-text">VIP {{ userInfo.memberLevel || 1 }}</text>
              </view>
            </view>
            <view class="member-progress">
              <text class="progress-text">积分值：{{ userInfo.integration || 0 }}/{{ nextLevelPoints }}</text>
              <view class="progress-bar">
                <view class="progress-fill" :style="{ width: progressPercentage + '%' }"></view>
              </view>
            </view>
          </view>

          <!-- 会员码入口和编辑按钮 -->
          <view class="right-actions">
            <view class="member-code-area" @tap="showMemberQRCode">
              <image class="qr-code-placeholder" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/category/member_code.png" mode="aspectFit" />
            </view>
            <view class="settings-btn-right" @tap="handleAvatarClick">
              <image class="settings-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/my/setting.png"></image>
            </view>
          </view>
        </template>

        <!-- 未登录状态 -->
        <template v-else>
          <view class="unlogin-info">
            <text class="greeting">Hi，新朋友</text>
            <text class="welcome-text">欢迎来到广横走文创</text>
          </view>

          <!-- 立即登录按钮 -->
          <view class="login-btn-container" @tap="handleAvatarClick">
            <view class="login-btn">
              <text class="login-btn-text">立即登录</text>
            </view>
          </view>
        </template>
      </view>
      
      <!-- 数据统计区域 -->
      <view class="stats-section">
        <view class="stat-item" @tap="toPointDetail">
          <text class="stat-label">积分</text>
          <text class="stat-value">{{ hasLogin ? (userInfo.integration || '0') : '-' }}</text>
        </view>
        <view class="stat-item" @tap="navTo('/pages/coupon/couponList')">
          <text class="stat-label">卡券</text>
          <text class="stat-value">{{ hasLogin ? (couponCount || '0') : '-' }}</text>
        </view>
        <view class="stat-item">
          <text class="stat-label" @tap="navTo('/pages/money/money')">余额(元)</text>
          <text class="stat-value">{{ hasLogin ? (userInfo.balance || '0.00') : '-' }}</text>
        </view>
      </view>

      <!-- 订单状态区域 -->
      <view class="order-status-section">
        <view class="order-status-item" @tap="navTo('/pages/order/order?state=0')">
          <image class="order-status-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/my/paying_order.png"></image>
          <text class="order-status-text">待付款</text>
        </view>
        <view class="order-status-item" @tap="navTo('/pages/order/order?state=1')">
          <image class="order-status-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/my/handling_order.png"></image>
          <text class="order-status-text">待发货</text>
        </view>
        <view class="order-status-item" @tap="navTo('/pages/order/order?state=2')">
          <image class="order-status-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/my/waiting_order.png"></image>
          <text class="order-status-text">待收货</text>
        </view>
        <view class="order-status-item" @tap="navTo('/pages/order/order?state=5')">
          <image class="order-status-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/my/after_sales.png"></image>
          <text class="order-status-text">售后</text>
        </view>
        <view class="order-status-item" @tap="navTo('/pages/order/order?state=-1')">
          <view class="order-icon-container">
            <image class="order-status-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/my/all_order.png"></image>
            <view v-if="totalOrderCount > 0" class="order-count-badge">
              <text class="badge-text">{{ totalOrderCount }}</text>
            </view>
          </view>
          <text class="order-status-text">全部订单</text>
        </view>
      </view>
    </view>

    <view class="content-section">
    
      <!-- 收益展示区域 -->
      <!-- <view class="income-section">
        <view class="income-main">
          <view class="income-header">
            <text class="income-title">当前总收益（元）</text>
            <text class="income-rule" @tap="showComingSoon">规则</text>
          </view>
          <text class="income-amount">{{ formattedCurrentTotalIncome }}</text>

          <view class="income-actions">
            <view class="action-btn detail-btn" @tap="navTo('/pages/user/income')">
              <image class="action-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/my/income_detail.png"></image>
              <text class="action-text">详情</text>
            </view>
            <view class="action-btn invite-btn" @tap="openInvitePopup">
              <image class="action-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/my/gift.png"></image>
              <text class="action-text">邀请伙伴</text>
            </view>
            <view class="withdraw-btn" @tap="handleWithdraw">
              <text class="withdraw-text">提现</text>
            </view>
          </view>
        </view>

        <view class="recommend-section">
          <text class="recommend-title">我的推荐</text>
          <text class="recommend-count">{{ inviteStats.registeredCount || 0 }}人</text>
          <view class="recommend-avatars">
            <image
              v-for="(user, index) in displayedInvitedUsers"
              :key="index"
              class="recommend-avatar"
              :src="user.avatar || 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/miss-face.png'"
            ></image>
            <image
              v-for="index in placeholderAvatarCount"
              :key="index"
              class="recommend-avatar placeholder"
              src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/miss-face.png"
            ></image>
          </view>
        </view>
      </view> -->

      <!-- 新的销售展示区域 -->
      <view class="sales-section">
        <view class="sales-background">
          <!-- 左右卡片容器 -->
          <view class="sales-cards">
            <!-- 左侧卡片 -->
            <view class="sales-card left-card" @tap="goToDistributionCenter">
              <image class="card-image" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/my/sales_left_card.png" mode="aspectFit"></image>
            </view>

            <!-- 右侧卡片 -->
            <view class="sales-card right-card" @tap="goToInviteFriends">
              <image class="card-image" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/my/sales_right_card.png" mode="aspectFit"></image>
            </view>
          </view>
        </view>
      </view>
    
      <!-- 服务菜单区域 -->
      <view class="service-menu">
        <!-- 第一行 -->
        <view class="service-row">
          <view class="service-item" @tap="navTo('/pages/cart/cart')">
            <image class="service-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/my/cart.png"></image>
            <text class="service-text">购物车</text>
          </view>
          <view class="service-item" @tap="goToSignin">
            <image class="service-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/my/points_mall.png"></image>
            <text class="service-text">积分商城</text>
          </view>
          <view class="service-item" @tap="navTo('/pages/signin/signin')">
            <image class="service-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/my/signin_icon.png"></image>
            <text class="service-text">每日签到</text>
          </view>
          <view class="service-item" @tap="showComingSoon">
            <image class="service-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/my/my_work.png"></image>
            <text class="service-text">我的作品</text>
          </view>
        </view>

        <!-- 第二行 -->
        <view class="service-row">
          <view class="service-item" @tap="showComingSoon">
            <image class="service-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/my/my_draft.png"></image>
            <text class="service-text">我的草稿</text>
          </view>
          <view class="service-item" @tap="navTo('/pages/address/address')">
            <image class="service-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/my/my_address.png"></image>
            <text class="service-text">地址管理</text>
          </view>
          <view class="service-item" @tap="navTo('/pages/gift/gift')">
            <image class="service-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/my/my_gift.png"></image>
            <text class="service-text">我的礼物</text>
          </view>
          <view class="service-item" @tap="showComingSoon">
            <image class="service-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/my/ticket_center.png"></image>
            <text class="service-text">发票中心</text>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 登录弹窗 -->
    <login-popup
      :visible="showLoginPopup"
      @close="closeLoginPopup"
      @openPrivacy="openPrivacy"
      @openTerms="openTerms"
    ></login-popup>

    <!-- 邀请弹窗 -->
    <invite-popup :visible="showInvitePopup" @close="closeInvitePopup" @share="handleShare"></invite-popup>

    <!-- 会员码弹窗 -->
    <member-qr-popup
      :show="showMemberQRPopup"
      :memberData="memberQRData"
      @updateShow="showMemberQRPopup = $event"
      @close="closeMemberQRPopup"
    ></member-qr-popup>

    <!-- 隐私政策弹窗 -->
    <privacy-popup
      :visible="showPrivacyPopup"
      @close="closePrivacy"
    ></privacy-popup>

    <!-- 用户协议弹窗 -->
    <terms-popup
      :visible="showTermsPopup"
      @close="closeTerms"
    ></terms-popup>
  </view>
</template>

<script>
import { memberInfo, getMemberQRCode } from '@/api/member';
import { fetchMemberCouponList } from '@/api/coupon';
import { fetchOrderList } from '@/api/order';
import { getMyInviteStatistics, applyWithdraw } from '@/api/invite';
import NavBar from '@/components/nav-bar.vue';
import LoginPopup from '@/components/login-popup.vue';
import InvitePopup from '@/components/invite-popup.vue';
import MemberQRPopup from '@/components/MemberQRPopup.vue';
import PrivacyPopup from '@/components/PrivacyPopup.vue';
import TermsPopup from '@/components/TermsPopup.vue';
import { mapState } from 'vuex';

export default {
  components: {
    'nav-bar': NavBar,
    'login-popup': LoginPopup,
    'invite-popup': InvitePopup,
    'member-qr-popup': MemberQRPopup,
    'privacy-popup': PrivacyPopup,
    'terms-popup': TermsPopup
  },
  computed: {
    ...mapState(['hasLogin', 'userInfo']),

    // 格式化当前总收益
    formattedCurrentTotalIncome() {
      const amount = this.inviteStats.currentTotalIncome || 0;
      return amount.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    },

    // 显示的被邀请人列表（最多5个）
    displayedInvitedUsers() {
      const users = this.inviteStats.invitedUsers || [];
      return users.slice(0, 5);
    },

    // 占位头像数量
    placeholderAvatarCount() {
      const userCount = this.displayedInvitedUsers.length;
      return Math.max(0, 5 - userCount);
    },

    // 下一等级所需积分
    nextLevelPoints() {
      const currentLevel = this.userInfo.memberLevel || 1;
      // 简单的等级积分计算，可以根据实际业务调整
      const levelPoints = {
        1: 500,
        2: 1000,
        3: 2000,
        4: 5000,
        5: 10000
      };
      return levelPoints[currentLevel + 1] || 10000;
    },

    // 积分进度百分比
    progressPercentage() {
      const currentPoints = this.userInfo.integration || 0;
      const nextPoints = this.nextLevelPoints;
      const currentLevel = this.userInfo.memberLevel || 1;
      const currentLevelMinPoints = currentLevel === 1 ? 0 :
        currentLevel === 2 ? 500 :
        currentLevel === 3 ? 1000 :
        currentLevel === 4 ? 2000 : 5000;

      const progress = ((currentPoints - currentLevelMinPoints) / (nextPoints - currentLevelMinPoints)) * 100;
      return Math.min(Math.max(progress, 0), 100);
    }
  },
  data() {
    return {
      totalOrderCount: 0,
      couponCount: 0,
      showLoginPopup: false, // 控制登录弹窗显示
      showInvitePopup: false, // 控制邀请弹窗显示
      showPrivacyPopup: false, // 控制隐私政策弹窗显示
      showTermsPopup: false, // 控制用户协议弹窗显示
      // 会员码弹窗
      showMemberQRPopup: false,
      memberQRData: {
        memberCode: '',
        memberName: '',
        memberAvatar: '',
        qrCodeBase64: ''
      },
      // 邀请统计数据
      inviteStats: {
        currentTotalIncome: 0,
        registeredCount: 0,
        invitedUsers: []
      },
      // 签到状态
      signinStatus: {
        isSigninToday: false,
        todayPoints: 5,
        continuousDays: 0
      }
    };
  },
  onLoad() {
    
  },
  onShow() {
    // 检查登录状态
    this.checkLogin();

    // 只有在登录状态下才调用需要登录权限的API
    if (this.hasLogin) {
      this.loadUserInfo();
      this.loadInviteStats(); // 加载邀请统计数据
      this.loadSigninStatus(); // 加载签到状态
      this.getOrderCount();
      this.getCouponCount();
    } else {
      // 未登录状态下重置数据
      this.resetUserData();
    }
  },
  
  // 分享给好友
  onShareAppMessage(res) {
    console.log('用户页面分享事件触发:', res);

    // 默认分享
    return {
      title: '广横走文创 - 甄选本地好物',
      path: '/pages/new_index/index',
      imageUrl: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/share/user_share.jpg'
    };
  },
  // 分享到朋友圈
  onShareTimeline() {
    return {
      title: '广横走文创 - 甄选本地好物',
      query: ''
    }
  },
  methods: {
    // 检查登录状态
    checkLogin() {
      // 使用统一的tokenInfo检查登录状态
      const tokenInfo = uni.getStorageSync('tokenInfo');
      const userInfoStorage = uni.getStorageSync('userInfo');

      let hasValidToken = false;
      if (tokenInfo) {
        try {
          const loginData = JSON.parse(tokenInfo);
          hasValidToken = !!(loginData && loginData.token && loginData.tokenHead && loginData.openId);
        } catch (e) {
          console.error('解析tokenInfo失败:', e);
          hasValidToken = false;
        }
      }

      if (hasValidToken && !!userInfoStorage && !this.hasLogin) {
        // 如果本地有有效的 tokenInfo 和 userInfo，但 store 中 hasLogin 为 false，则提交登录到 store
        console.log('检测到本地登录信息，恢复登录状态');
        this.$store.commit('login', userInfoStorage);
      } else if ((!hasValidToken || !userInfoStorage) && this.hasLogin) {
        // 如果本地没有有效的 tokenInfo 或 userInfo，但 store 中 hasLogin 为 true，则提交登出到 store
        console.log('本地登录信息无效，清除登录状态');
        this.$store.commit('logout');
      }

      if (!this.hasLogin) {
        this.resetUserData();
      }
    },

    // 重置用户数据（未登录状态）
    resetUserData() {
      this.couponCount = 0;
      this.totalOrderCount = 0;
      this.inviteStats = {
        currentTotalIncome: 0,
        registeredCount: 0,
        invitedUsers: []
      };
      this.signinStatus = {
        isSigninToday: false,
        todayPoints: 5,
        continuousDays: 0
      };
    },
    
    // 加载用户信息
    loadUserInfo() {
      if (!this.hasLogin) {
        return;
      }

      memberInfo().then(res => {
        if (res.code === 200 && res.data) {
          // 更新 store 中的 userInfo
          this.$store.commit('login', res.data);
        }
      }).catch(err => {
        console.error('获取用户信息失败', err);
      });
    },
    
    // 处理头像点击
    handleAvatarClick() {
      if (this.hasLogin) {
        // 已登录，进入注册页面完善信息
        uni.navigateTo({
          url: '/pages/public/register'
        });
      } else {
        // 未登录，打开登录弹窗
        this.showLoginPopup = true;
      }
    },
    
    // 关闭登录弹窗
    closeLoginPopup() {
      this.showLoginPopup = false;
    },
    
    // 打开邀请弹窗
    openInvitePopup() {
      if (!this.hasLogin) {
        this.showLoginPopup = true;
        return;
      }
      this.showInvitePopup = true;
    },
    
    // 关闭邀请弹窗
    closeInvitePopup() {
      this.showInvitePopup = false;
    },
    
    // 会员码弹窗相关方法
    async showMemberQRCode() {
      if (!this.hasLogin) {
        this.showLoginPopup = true;
        return;
      }

      try {
        const result = await getMemberQRCode();
        if (result && result.data) {
          this.memberQRData = result.data;
          this.showMemberQRPopup = true;
        } else {
          uni.showToast({
            title: '获取会员码失败',
            icon: 'none'
          });
        }
      } catch (error) {
        console.error('获取会员码失败', error);
        uni.showToast({
          title: '获取会员码失败',
          icon: 'none'
        });
      }
    },

    closeMemberQRPopup() {
      this.showMemberQRPopup = false;
    },
    
    // 处理分享（保留用于调试）
    handleShare(shareData) {
      console.log('分享数据:', shareData);
    },
    
    // 获取订单数量
    getOrderCount() {
      if (!this.hasLogin) {
        this.totalOrderCount = 0;
        return;
      }

      // 获取全部订单，不按状态过滤
      fetchOrderList({ pageNum: 1, pageSize: 1000, status: -1 }).then(res => {
        if (res.code === 200 && res.data) {
          // 直接使用total字段，更准确
          this.totalOrderCount = res.data.total || (res.data.list ? res.data.list.length : 0);
        }
      }).catch(err => {
        console.error('获取订单列表失败', err);
        // 失败时重置为0
        this.totalOrderCount = 0;
      });
    },
    
    // 获取优惠券数量
    getCouponCount() {
      if (!this.hasLogin) {
        this.couponCount = 0;
        return;
      }

      fetchMemberCouponList(0).then(res => {
        if (res.code === 200 && res.data) {
          this.couponCount = res.data.length || 0;
        }
      }).catch(err => {
        console.error('获取优惠券数量失败', err);
      });
    },
    
    
    // 获取用户信息
    getUserInfo() {
      if (!this.hasLogin) {
        uni.navigateTo({
          url: '/pages/public/register'
        });
      } else {
        uni.navigateTo({
          url: '/pages/public/register'
        });
      }
    },
    
    // 通用导航
    navTo(url) {
      if (!this.hasLogin && !url.includes('aboutUs')) {
        url = '/pages/public/register';
      }
      uni.navigateTo({
        url
      });
    },
    
    // 跳转到积分详情
    toPointDetail() {
      if (!this.hasLogin) {
        uni.navigateTo({
          url: '/pages/public/register'
        });
        return;
      }
      
      uni.navigateTo({
        url: '/pages/signin/signin'
      });
    },
    
    // 显示"敬请期待"提示
    showComingSoon() {
      uni.showToast({
        title: '敬请期待',
        icon: 'none',
        duration: 2000
      });
    },

    // 跳转到分销中心
    goToDistributionCenter() {
      if (!this.hasLogin) {
        this.showLoginPopup = true;
        return;
      }

      uni.navigateTo({
        url: '/pages/user/distribution-center'
      });
    },
    goToSignin() {
      if (!this.hasLogin) {
        this.showLoginPopup = true;
        return;
      }

      uni.navigateTo({
        url: '/pages/signin/signin'
      });
    },
    
    // 跳转到邀请好友页面
    goToInviteFriends() {
      if (!this.hasLogin) {
        this.showLoginPopup = true;
        return;
      }

      uni.navigateTo({
        url: '/pages/user/invite-friends'
      });
    },
    
    // 加载邀请统计数据
    async loadInviteStats() {
      if (!this.hasLogin) {
        this.inviteStats = {
          currentTotalIncome: 0,
          registeredCount: 0,
          invitedUsers: []
        };
        return;
      }

      try {
        const response = await getMyInviteStatistics();

        if (response.code === 200) {
          this.inviteStats = {
            ...this.inviteStats,
            ...response.data
          };
          console.log('邀请统计数据:', this.inviteStats);
        } else {
          console.error('获取邀请统计失败:', response.message);
        }
      } catch (error) {
        console.error('获取邀请统计异常:', error);
        // 降级处理：保持默认值
      }
    },
    
    // 处理提现
    handleWithdraw() {
      if (!this.hasLogin) {
        this.showLoginPopup = true;
        return;
      }
      
      // 检查可提现金额
      const withdrawableAmount = this.inviteStats.withdrawableAmount || 0;
      
      if (withdrawableAmount <= 0) {
        uni.showToast({
          title: '暂无可提现金额',
          icon: 'none',
          duration: 2000
        });
        return;
      }
      
      // 显示提现选择弹窗
      uni.showActionSheet({
        itemList: ['微信钱包提现', '银行卡提现'],
        success: (res) => {
          if (res.tapIndex === 0) {
            this.withdrawToWechat();
          } else if (res.tapIndex === 1) {
            this.withdrawToBank();
          }
        }
      });
    },
    
    // 微信钱包提现
    withdrawToWechat() {
      // 简化处理：直接调用提现API
      const withdrawableAmount = this.inviteStats.withdrawableAmount || 0;
      
      uni.showModal({
        title: '确认提现',
        content: `确认提现 ¥${withdrawableAmount.toFixed(2)} 到微信钱包？`,
        success: async (res) => {
          if (res.confirm) {
            try {
              uni.showLoading({ title: '提交中...' });
              const userInfo = uni.getStorageSync('userInfo');
              const response = await applyWithdraw({
                amount: withdrawableAmount,
                withdrawType: 1, // 微信钱包
                wechatOpenid: userInfo.openid,
                remark: '微信钱包提现'
              });
              
              uni.hideLoading();
              
              if (response.code === 200) {
                uni.showToast({
                  title: '提现申请提交成功',
                  icon: 'success'
                });
                // 刷新数据
                this.loadInviteStats();
              } else {
                uni.showToast({
                  title: response.message || '提现申请失败',
                  icon: 'none'
                });
              }
            } catch (error) {
              uni.hideLoading();
              uni.showToast({
                title: '提现申请失败',
                icon: 'none'
              });
            }
          }
        }
      });
    },
    
    // 银行卡提现
    withdrawToBank() {
      uni.showToast({
        title: '银行卡提现功能开发中',
        icon: 'none',
        duration: 2000
      });
    },
    
    // 关注官方账号
    followOfficialAccount() {
      uni.showToast({
        title: '请在微信中搜索"广横走文创"关注我们',
        icon: 'none',
        duration: 3000
      });
    },
    
    // 加载签到状态
    loadSigninStatus() {
      if (!this.hasLogin) {
        this.signinStatus = {
          isSigninToday: false,
          todayPoints: 5,
          continuousDays: 0
        };
        return;
      }

      // 调用签到状态API
      import('@/api/signin').then(({ getSigninStatus }) => {
        getSigninStatus().then(res => {
          if (res.code === 200) {
            this.signinStatus = res.data;
          }
        }).catch(err => {
          console.error('获取签到状态失败', err);
          // 降级处理：使用默认值
          this.signinStatus = {
            isSigninToday: false,
            todayPoints: 5,
            continuousDays: 0
          };
        });
      });
    },

    // 打开隐私政策弹窗
    openPrivacy() {
      this.showPrivacyPopup = true;
    },

    // 关闭隐私政策弹窗
    closePrivacy() {
      this.showPrivacyPopup = false;
    },

    // 打开用户协议弹窗
    openTerms() {
      this.showTermsPopup = true;
    },

    // 关闭用户协议弹窗
    closeTerms() {
      this.showTermsPopup = false;
    }
  }
};
</script>

<style lang="scss">
@charset "UTF-8";

.container {
  background-color: #FFFFFF;
  min-height: 100vh;
  padding-bottom: 120rpx;
}

/* 顶部区域容器（包含导航栏、用户信息、订单状态区域的统一背景） */
.top-section {
  background-image: url('https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/my/user_top_background.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  padding: 0;
  position: relative;
  width: 100%;
  height: 574rpx; /* 287px * 2 = 574rpx */
  overflow: visible; /* 允许内容超出容器显示 */
}

/* 导航栏样式已移除 */

/* 用户头像区域 */
.user-header {
  padding: 20rpx 30rpx 40rpx 30rpx;
  display: flex;
  align-items: center;
  gap: 24rpx;
}

.user-avatar-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-bg {
  position: absolute;
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  background: rgba(209, 247, 57, 0.4);
  z-index: 1;
}

.avatar-bg-blur {
  position: absolute;
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  background: rgba(165, 247, 57, 0.4);
  filter: blur(156px);
}

.user-avatar {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  border: 2rpx solid #000;
  box-shadow: 0 6rpx 11rpx rgba(0, 0, 0, 0.17);
  z-index: 2;
  position: relative;
}

.settings-icon {
  width: 28rpx;
  height: 28rpx;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
  flex: 1;
}

.username-container {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.username {
  font-size: 36rpx;
  font-weight: 600;
  color: #000;
}

.vip-badge {
  background: linear-gradient(89.33deg, #000000 0.41%, #262626 99.35%);
  border-radius: 0px 12rpx;
  width: 60rpx;
  height: 26rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.vip-text {
  font-style: normal;
  font-weight: 400;
  font-size: 20rpx;
  line-height: 26rpx;
  background: linear-gradient(180deg, #A7FD03 0%, #B2F597 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.member-progress {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.progress-text {
  font-size: 24rpx;
  color: #666;
}

.progress-bar {
  width: 100%;
  height: 6rpx;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 3rpx;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(180deg, #A7FD03 0%, #B2F597 100%);
  border-radius: 3rpx;
  transition: width 0.3s ease;
}

/* 未登录状态样式 */
.unlogin-info {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  padding: 0;
  gap: 16rpx;
  flex: 1;
}

.greeting {
  width: 432rpx;
  height: 40rpx;
  font-family: 'PingFang SC', sans-serif;
  font-style: normal;
  font-weight: 400;
  font-size: 28rpx;
  line-height: 40rpx;
  color: #333333;
  flex: none;
  order: 0;
  align-self: stretch;
  flex-grow: 0;
}

.welcome-text {
  width: 432rpx;
  height: 66rpx;
  font-family: 'Alibaba PuHuiTi', sans-serif;
  font-style: normal;
  font-weight: 700;
  font-size: 48rpx;
  line-height: 66rpx;
  display: flex;
  align-items: center;
  color: #000000;
  flex: none;
  order: 1;
  align-self: stretch;
  flex-grow: 0;
}

/* 立即登录按钮 */
.login-btn-container {
  margin-left: 16rpx;
}

.login-btn {
  background: #000;
  border-radius: 32rpx;
  padding: 16rpx 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-btn-text {
  font-size: 28rpx;
  font-weight: 600;
  color: #A9FF00;
}

/* 右侧操作区域 */
.right-actions {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

/* 会员码区域 */
.member-code-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  padding: 16rpx;
  justify-content: center;
  height: 100rpx; /* 与用户头像高度保持一致 */
}

.qr-code-placeholder {
  width: 40rpx;
  height: 40rpx;
  border-radius: 8rpx;
}

.member-text {
  font-size: 20rpx;
  color: #000000;
  font-weight: 400;
}

/* 右侧设置按钮 */
.settings-btn-right {
  width: 48rpx;
  height: 48rpx;
  background: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}

/* 数据统计区域 */
.stats-section {
  display: flex;
  width: 60%;
  padding: 40rpx 20rpx;
}

/* 内容区域 */
.content-section {
  background: #FFFFFF;
  margin-top: 80rpx;
  position: relative;
  z-index: 1;
  padding-top: 40rpx;
}

.stat-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
}

.stat-label {
  font-size: 28rpx;
  color: #666;
}

.stat-value {
  font-size: 36rpx;
  font-weight: 600;
  color: #333;
}

/* 订单状态区域 */
.order-status-section {
  display: flex;
  margin: 0 30rpx 40rpx;
  background: #fff;
  border-radius: 16rpx;
  padding: 40rpx 20rpx;
  box-shadow: 0 2rpx 14rpx rgba(0, 0, 0, 0.09);
  position: relative;
  z-index: 10;
}

.order-status-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-between;
  height: 88rpx;
}

.order-status-icon {
  width: 48rpx;
  height: 48rpx;
  display: block;
  flex-shrink: 0;
}

.order-status-text {
  font-size: 28rpx;
  color: #666;
  text-align: center;
  line-height: 28rpx;
  margin-top: 8rpx;
}

.order-icon-container {
  position: relative;
  display: inline-block;
}

.order-count-badge {
  position: absolute;
  top: -8rpx;
  right: -8rpx;
  min-width: 32rpx;
  height: 32rpx;
  background: #ff4757;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 8rpx;
  box-sizing: border-box;
}

.badge-text {
  font-size: 20rpx;
  color: #fff;
  font-weight: 600;
  line-height: 1;
}

/* 收益展示区域 - 已注释 */
/*
.income-section {
  margin: 0 30rpx 40rpx;
  border-radius: 16rpx;
  box-shadow: 0 6rpx 8rpx 2rpx rgba(43, 50, 0, 0.08);
  overflow: hidden;
}

.income-main {
  background: linear-gradient(186.28deg, #C8FF3F 6.78%, #FBFFF3 117.82%);
  padding: 40rpx 30rpx;
  position: relative;
}

.income-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.income-title {
  font-size: 32rpx;
  color: #666;
}

.income-rule {
  font-size: 28rpx;
  color: #9FA19D;
  text-decoration: underline;
}

.income-amount {
  font-size: 48rpx;
  font-weight: 700;
  color: #27243B;
  margin-bottom: 40rpx;
}

.income-actions {
  display: flex;
  gap: 20rpx;
  margin-bottom: 40rpx;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 16rpx;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 8rpx;
  padding: 12rpx 16rpx;
  flex: 1;
}

.action-icon {
  width: 32rpx;
  height: 32rpx;
}

.action-text {
  font-size: 28rpx;
  color: #27243B;
}

.withdraw-btn {
  background: #000;
  border-radius: 32rpx;
  padding: 12rpx 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.withdraw-text {
  font-size: 28rpx;
  font-weight: 600;
  color: #A9FF00;
}

.recommend-section {
  background: #fff;
  padding: 30rpx;
  position: relative;
}

.recommend-title {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 20rpx;
}

.recommend-count {
  position: absolute;
  top: 30rpx;
  right: 30rpx;
  font-size: 24rpx;
  color: #9FA19D;
}

.recommend-avatars {
  display: flex;
  gap: 8rpx;
}

.recommend-avatar {
  width: 60rpx;
  height: 60rpx;
  border-radius: 50%;
  border: 2rpx solid #EEEEEE;
}

.recommend-avatar.placeholder {
  opacity: 0.3;
}
*/

/* 新的销售展示区域 */
.sales-section {
  margin: 0 30rpx 40rpx;
  border-radius: 16rpx;
  overflow: hidden;
}

.sales-background {
  width: 100%;
  height: 476rpx;
  background-image: url('https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/my/sales_background.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  display: flex;
  align-items: center;
  justify-content: center;
  padding-top: 90rpx;
}

.sales-cards {
  display: flex;
  gap: 20rpx;
  padding: 0 30rpx;
  width: 100%;
  justify-content: center;
}

.sales-card {
  width: 302rpx;  /* 151px * 2 = 302rpx */
  height: 322rpx; /* 161px * 2 = 322rpx */
  border-radius: 12rpx;
  overflow: hidden;
  flex-shrink: 0;
}

.card-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

/* 服务菜单区域 */
.service-menu {
  background: #fff;
  border-radius: 16rpx;
  padding: 40rpx 30rpx;
}

.service-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 40rpx;
}

.service-row:last-child {
  margin-bottom: 0;
}

.service-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1;
  gap: 16rpx;
}

.service-icon {
  width: 48rpx;
  height: 48rpx;
}

.service-text {
  font-size: 24rpx;
  color: #333;
  text-align: center;
  line-height: 1.2;
}

/* 通用hover效果 */
.common-hover {
  background-color: #f5f5f5;
}
</style> 