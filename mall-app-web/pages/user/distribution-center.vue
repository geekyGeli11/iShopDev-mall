<template>
  <view class="distribution-center">
    <!-- 导航栏 - 始终显示在最顶部 -->
    <nav-bar
      :title="getNavTitle()"
      :back="true"
      :placeholder="getNavPlaceholder()"
      :bg-color="getNavBgColor()"
      :color="getNavTextColor()"
    ></nav-bar>

    <!-- 分销商申请页面 -->
    <view v-if="!isDistributor && applyStatus !== 'rejected'" class="apply-page">
      <!-- 顶部容器 -->
      <view class="top-container">
        <!-- 顶部修饰图片 -->
        <image class="top-bg" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/top-bg.png" mode="aspectFill"></image>

        <!-- 申请成为团长卡片 -->
        <view class="apply-card">
          <view class="card-header">
            <view class="user-phone">{{ localUserInfo.phone || '未绑定手机号' }}</view>
            <view class="card-title">申请成为文创团长</view>
          </view>
          <view class="advantage-images">
            <image src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/apply_card_1.png" mode="widthFix"></image>
            <image src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/apply_card_2.png" mode="widthFix"></image>
            <image src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/apply_card_3.png" mode="widthFix"></image>
          </view>
        </view>

        <!-- 一键申请按钮 -->
        <view class="apply-button-container">
          <image
            class="apply-button"
            src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/apply_button.png"
            mode="widthFix"
            @tap="goToApplyPage"
          ></image>
        </view>
      </view>

      <!-- 下方团长优势容器 -->
      <view class="benefits-container">

        <image src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/benefit_card_1.png" mode="widthFix"></image>
        <image src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/benefit_card_2.png" mode="widthFix"></image>
        <image src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/benefit_card_3.png" mode="widthFix"></image>
        <image src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/benefit_card_4.png" mode="widthFix"></image>
      </view>
    </view>

    <!-- 申请失败状态 -->
    <view v-else-if="!isDistributor && applyStatus === 'rejected'" class="rejected-page">
      <view class="status-section rejected">
        <view class="status-card">
          <view class="status-icon rejected-icon">❌</view>
          <text class="status-title">申请未通过</text>
          <text class="status-desc">{{ rejectReason || '很抱歉，您的申请未通过审核' }}</text>
          <button class="reapply-btn" @tap="handleReapply">重新申请</button>
        </view>
      </view>
    </view>

    <!-- 已是分销商：显示分销中心 -->
    <view v-else class="distributor-center">
      <!-- 背景装饰 -->
      <view class="background-decoration"></view>

      <!-- 内容区域 -->
      <view class="content">
        <!-- 分销商状态：已通过审核 -->
        <view v-if="isDistributor" class="distributor-content">
          <!-- 顶部图片卡片 -->
          <view class="top-card">
            <view class="card-content">
              <image class="banner-image" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/banner.png" mode="aspectFill"></image>
              <view class="record-btn" @tap="handleViewRecords">
                <text class="record-text">推广记录</text>
                <text class="record-arrow">></text>
              </view>
            </view>
          </view>

          <!-- 我的推荐 -->
          <view class="recommend-section">
            <view class="recommend-left">
              <text class="recommend-label">我的推荐</text>
              <view class="recommend-avatars">
                <image
                  v-for="(avatar, index) in recommendData.avatars"
                  :key="index"
                  class="avatar-item"
                  :src="avatar"
                  mode="aspectFill">
                </image>
                <text class="recommend-count">{{ recommendData.count }}人</text>
              </view>
            </view>
            <view class="recommend-right">
              <view class="invite-btn" @tap="handleInvitePartner">
                <image class="invite-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/gift-icon.png"></image>
                <text class="invite-text">邀请伙伴</text>
              </view>
            </view>
          </view>

          <!-- 团长推广标题 -->
          <view class="section-header">
            <view class="title-wrapper">
              <text class="title-text">团长推广推荐</text>
              <image class="title-indicator"
                src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/more-goods-indicator.png"
                mode="aspectFit" />
            </view>
          </view>

          <!-- 商品分类 -->
          <view class="category-section">
            <scroll-view class="category-scroll" scroll-x="true" show-scrollbar="false">
              <view class="category-grid">
                <view
                  class="category-item"
                  :class="{ active: selectedCategoryId === 0 }"
                  @tap="handleCategoryChange(0)">
                  <view class="category-icon-wrapper">
                    <image class="category-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/category/all_product.png"></image>
                  </view>
                  <text class="category-name">全部商品</text>
                </view>
                <view
                  v-for="category in categoryList"
                  :key="category.id"
                  class="category-item"
                  :class="{ active: selectedCategoryId === category.id }"
                  @tap="handleCategoryChange(category.id)">
                  <view class="category-icon-wrapper">
                    <image class="category-icon" :src="category.icon || 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/category-default.png'"></image>
                  </view>
                  <text class="category-name">{{ category.name }}</text>
                </view>
              </view>
            </scroll-view>
          </view>

          <!-- 商品列表 -->
          <view class="product-section">
            <view class="product-grid">
              <view
                v-for="product in productList"
                :key="product.id"
                class="product-item"
                @tap="handleProductPromotion(product)">
                <view class="product-image-wrapper">
                  <image class="product-thumb" :src="product.pic" mode="aspectFill"></image>
                  <view class="commission-badge">
                    <text class="commission-text">5%</text>
                  </view>
                </view>
                <view class="product-info">
                  <text class="product-name">{{ product.name }}</text>
                  <view class="price-sales-row">
                    <text class="product-price">¥{{ product.price }}</text>
                    <view class="promotion-action">
                      <text class="promotion-text">去推广</text>
                    </view>
                  </view>
                </view>
              </view>
            </view>

            <!-- 加载更多 -->
            <view v-if="productHasMore" class="load-more-section" @tap="loadMoreProducts">
              <text class="load-more-text">{{ productLoading ? '加载中...' : '加载更多' }}</text>
            </view>
          </view>


        </view>
      </view>
    </view>


    
    <!-- 提现申请弹窗 -->
    <withdraw-dialog
      :visible="showWithdrawDialog"
      :available-amount="availableAmount"
      @close="showWithdrawDialog = false"
      @submit="handleWithdrawSubmit"
    ></withdraw-dialog>
    
    <!-- 推广海报弹窗 -->
    <poster-dialog
      :visible="showPosterDialog"
      :user-info="userInfo"
      @close="showPosterDialog = false"
    ></poster-dialog>
  </view>
</template>

<script>
import { mapState } from 'vuex';
import NavBar from '@/components/nav-bar.vue';
import WithdrawDialog from '@/components/distribution/WithdrawDialog.vue';
import PosterDialog from '@/components/distribution/PosterDialog.vue';

import {
  getDistributorApplyStatus,
  getMyDistributionStats,
  getMyCommissionRecords,
  getMyCommissionSummary,
  getMyCustomers,
  getMyCustomerStats,
  applyCommissionWithdraw,
  getWithdrawStatistics,
  getMyWithdrawRecords
} from '@/api/distribution';

import { fetchProductCateList } from '@/api/home';
import { searchProductList } from '@/api/product';

export default {
  name: 'DistributionCenter',
  components: {
    'nav-bar': NavBar,
    'withdraw-dialog': WithdrawDialog,
    'poster-dialog': PosterDialog
  },
  
  data() {
    return {
      // 状态数据
      isDistributor: false,
      applyStatus: 'none', // none, pending, approved, rejected
      applyTime: '',
      rejectReason: '',

      // 用户信息（本地副本）
      localUserInfo: {
        phone: '',
        integration: 0
      },
      
      // 分销数据
      distributionData: {},
      commissionData: {},
      commissionRecords: [],
      customerData: {},
      customerList: [],
      
      // 提现数据
      availableAmount: 0,
      withdrawRecords: [],
      withdrawStatistics: {},
      
      // 标签页
      currentTab: 'commission',
      tabs: [
        { key: 'commission', label: '佣金收益' },
        { key: 'customer', label: '客户管理' },
        { key: 'withdraw', label: '提现管理' }
      ],
      
      // 分页和加载状态
      loading: false,
      hasMore: false,
      currentPage: 1,
      pageSize: 20,
      
      // 弹窗状态
      showWithdrawDialog: false,
      showPosterDialog: false,
      
      // 筛选条件
      commissionFilter: 'all',
      periodFilter: 'month',
      customerFilter: 'all',

      // 商品数据
      categoryList: [],
      selectedCategoryId: 0, // 0表示全部商品
      productList: [],
      productLoading: false,
      productHasMore: false,
      productCurrentPage: 1,
      productPageSize: 10,

      // 推荐数据
      recommendData: {
        count: 0,
        avatars: []
      }
    }
  },
  
  computed: {
    ...mapState(['hasLogin', 'userInfo'])
  },
  
  onLoad() {
    if (this.hasLogin) {
      this.initUserInfo();
      this.initData();
    }
  },
  
  onShow() {
    if (this.hasLogin) {
      this.refreshData();
    }
  },
  
  onReachBottom() {
    if (this.hasMore && !this.loading) {
      this.loadMoreData();
    }
  },
  
  onPullDownRefresh() {
    this.refreshData();
    setTimeout(() => {
      uni.stopPullDownRefresh();
    }, 1000);
  },
  
  methods: {
    // ========== 导航栏配置 ==========

    getNavTitle() {
      if (this.isDistributor) {
        return '团长分佣';
      } else if (this.applyStatus === 'rejected') {
        return '申请失败';
      } else {
        return '申请成为团长';
      }
    },

    getNavPlaceholder() {
      return this.isDistributor;
    },

    getNavBgColor() {
      return this.isDistributor ? 'transparent' : 'transparent';
    },

    getNavTextColor() {
      return '#000';
    },

    // ========== 初始化和数据加载 ==========

    // 初始化用户信息
    initUserInfo() {
      if (this.userInfo) {
        this.localUserInfo = {
          phone: this.userInfo.phone || '',
          integration: this.userInfo.integration || 0
        };
      }
    },

    async initData() {
      try {
        uni.showLoading({ title: '加载中...' });
        await this.loadApplyStatus();
        
        if (this.isDistributor) {
          await Promise.all([
            this.loadDistributionData(),
            this.loadCommissionData(),
            this.loadCustomerData(),
            this.loadWithdrawData(),
            this.loadCategoryList(),
            this.loadProductList(),
            this.loadRecommendData()
          ]);
        }
      } catch (error) {
        console.error('初始化数据失败:', error);
        uni.showToast({
          title: error.message || '加载失败',
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
      }
    },
    
    async refreshData() {
      try {
        await this.loadApplyStatus();
        
        if (this.isDistributor) {
          if (this.currentTab === 'commission') {
            await this.loadCommissionData();
          } else if (this.currentTab === 'customer') {
            await this.loadCustomerData();
          } else if (this.currentTab === 'withdraw') {
            await this.loadWithdrawData();
          }
        }
      } catch (error) {
        console.error('刷新数据失败:', error);
      }
    },
    
    // ========== 申请状态管理 ==========
    
    async loadApplyStatus() {
      try {
        const res = await getDistributorApplyStatus();
        console.log('申请状态响应:', res.data);

        // 根据API返回的数据设置状态
        this.isDistributor = res.data.isDistributor || false;

        // 如果已经是分销商，设置为已通过状态
        if (this.isDistributor) {
          this.applyStatus = 'approved';
        } else {
          // 如果有待审核申请，设置为待审核状态
          if (res.data.hasPendingApply) {
            this.applyStatus = 'pending';
          } else {
            this.applyStatus = 'none';
          }
        }

        // 设置其他状态信息
        this.applyTime = res.data.applyTime || '';
        this.rejectReason = res.data.rejectReason || '';

        console.log('设置状态:', {
          isDistributor: this.isDistributor,
          applyStatus: this.applyStatus,
          hasPendingApply: res.data.hasPendingApply
        });
      } catch (error) {
        console.error('获取申请状态失败:', error);
        throw error;
      }
    },
    
    // ========== 分销数据加载 ==========
    
    async loadDistributionData() {
      try {
        const res = await getMyDistributionStats();
        this.distributionData = res.data;
      } catch (error) {
        console.error('获取分销统计失败:', error);
        throw error;
      }
    },
    
    async loadCommissionData() {
      try {
        this.loading = true;
        this.currentPage = 1;
        
        const [summaryRes, recordsRes] = await Promise.all([
          getMyCommissionSummary({
            period: this.periodFilter
          }),
          getMyCommissionRecords({
            filter: this.commissionFilter,
            page: this.currentPage,
            size: this.pageSize
          })
        ]);
        
        this.commissionData = summaryRes.data;
        this.commissionRecords = recordsRes.data.list;
        this.hasMore = recordsRes.data.hasMore;
      } catch (error) {
        console.error('获取佣金数据失败:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async loadCustomerData() {
      try {
        this.loading = true;
        this.currentPage = 1;
        
        const [statsRes, listRes] = await Promise.all([
          getMyCustomerStats(),
          getMyCustomers({
            level: this.customerFilter,
            page: this.currentPage,
            size: this.pageSize
          })
        ]);
        
        this.customerData = statsRes.data;
        this.customerList = listRes.data.list;
        this.hasMore = listRes.data.hasMore;
      } catch (error) {
        console.error('获取客户数据失败:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async loadWithdrawData() {
      try {
        this.loading = true;
        
        const [statsRes, recordsRes] = await Promise.all([
          getWithdrawStatistics(),
          getMyWithdrawRecords({
            page: this.currentPage,
            size: this.pageSize
          })
        ]);
        
        this.withdrawStatistics = statsRes.data;
        this.availableAmount = statsRes.data.availableAmount || 0;
        this.withdrawRecords = recordsRes.data.list;
        this.hasMore = recordsRes.data.hasMore;
      } catch (error) {
        console.error('获取提现数据失败:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },

    // ========== 推荐数据加载 ==========

    async loadRecommendData() {
      try {
        // 调用我的客户列表接口获取推荐数据
        const res = await getMyCustomers({
          level: 'all',
          page: 1,
          size: 20
        });

        console.log('推荐数据接口返回:', res);

        if (res && res.data) {
          const customerList = res.data.list || [];
          const totalCount = res.data.total || 0;

          // 提取头像列表（最多显示3个）
          let avatars = customerList
            .filter(customer => customer.avatar) // 过滤有头像的客户
            .slice(0, 3) // 最多取3个
            .map(customer => customer.avatar);

          // 如果没有真实头像，使用默认头像
          if (avatars.length === 0 && totalCount > 0) {
            avatars = [
              'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/avatar1.png',
              'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/avatar2.png',
              'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/avatar3.png'
            ].slice(0, Math.min(3, totalCount));
          }

          this.recommendData = {
            count: totalCount,
            avatars: avatars
          };
        } else {
          this.recommendData = { count: 0, avatars: [] };
        }
      } catch (error) {
        console.error('获取推荐数据失败:', error);
        this.recommendData = { count: 0, avatars: [] };
      }
    },

    // ========== 商品数据加载 ==========

    async loadCategoryList() {
      try {
        const res = await fetchProductCateList(0); // 0表示获取一级分类
        if (res && res.data) {
          this.categoryList = res.data;
        }
      } catch (error) {
        console.error('获取商品分类失败:', error);
      }
    },

    async loadProductList() {
      try {
        this.productLoading = true;
        this.productCurrentPage = 1;

        const params = {
          pageNum: this.productCurrentPage,
          pageSize: this.productPageSize
        };

        // 如果选择了特定分类，添加分类参数
        if (this.selectedCategoryId > 0) {
          params.productCategoryId = this.selectedCategoryId;
        }

        const res = await searchProductList(params);
        if (res && res.data) {
          this.productList = res.data.list || [];
          this.productHasMore = res.data.total > this.productPageSize;
        }
      } catch (error) {
        console.error('获取商品列表失败:', error);
        this.productList = [];
      } finally {
        this.productLoading = false;
      }
    },

    async loadMoreProducts() {
      if (this.productLoading || !this.productHasMore) return;

      try {
        this.productLoading = true;
        this.productCurrentPage++;

        const params = {
          pageNum: this.productCurrentPage,
          pageSize: this.productPageSize
        };

        if (this.selectedCategoryId > 0) {
          params.productCategoryId = this.selectedCategoryId;
        }

        const res = await searchProductList(params);
        if (res && res.data && res.data.list) {
          this.productList.push(...res.data.list);
          this.productHasMore = this.productList.length < res.data.total;
        }
      } catch (error) {
        console.error('加载更多商品失败:', error);
        this.productCurrentPage--;
      } finally {
        this.productLoading = false;
      }
    },

    // 切换商品分类
    handleCategoryChange(categoryId) {
      if (this.selectedCategoryId === categoryId) return;

      this.selectedCategoryId = categoryId;
      this.loadProductList();
    },

    // 查看推广记录
    handleViewRecords() {
      uni.navigateTo({
        url: '/pages/user/invite-records'
      });
    },

    // 邀请伙伴
    handleInvitePartner() {
      uni.navigateTo({
        url: '/pages/user/invite-friends'
      });
    },

    // 商品推广
    handleProductPromotion(product) {
      // 跳转到商品详情页，并传递推广参数
      uni.navigateTo({
        url: `/pages/product/product?id=${product.id}&isPromotion=true&distributorId=${this.userInfo.id || ''}`
      });
    },

    // ========== 标签页切换 ==========
    
    switchTab(key) {
      if (this.currentTab === key) return;
      
      this.currentTab = key;
      this.currentPage = 1;
      this.hasMore = false;
      
      if (key === 'commission') {
        this.loadCommissionData();
      } else if (key === 'customer') {
        this.loadCustomerData();
      } else if (key === 'withdraw') {
        this.loadWithdrawData();
      }
    },
    
    // ========== 分页加载 ==========
    
    async loadMoreData() {
      if (this.loading || !this.hasMore) return;
      
      try {
        this.loading = true;
        this.currentPage++;
        
        let res;
        if (this.currentTab === 'commission') {
          res = await getMyCommissionRecords({
            filter: this.commissionFilter,
            page: this.currentPage,
            size: this.pageSize
          });
          this.commissionRecords.push(...res.data.list);
        } else if (this.currentTab === 'customer') {
          res = await getMyCustomers({
            level: this.customerFilter,
            page: this.currentPage,
            size: this.pageSize
          });
          this.customerList.push(...res.data.list);
        } else if (this.currentTab === 'withdraw') {
          res = await getMyWithdrawRecords({
            page: this.currentPage,
            size: this.pageSize
          });
          this.withdrawRecords.push(...res.data.list);
        }
        
        this.hasMore = res.data.hasMore;
      } catch (error) {
        console.error('加载更多数据失败:', error);
        this.currentPage--;
        uni.showToast({
          title: '加载失败',
          icon: 'none'
        });
      } finally {
        this.loading = false;
      }
    },
    
    // ========== 事件处理 ==========
    
    goToApplyPage() {
      uni.navigateTo({
        url: '/pages/user/distributor-apply'
      });
    },
    
    handleReapply() {
      // 跳转到申请页面
      uni.navigateTo({
        url: '/pages/user/distributor-apply'
      });
    },
    
    handleGenerateCode() {
      uni.navigateTo({
        url: '/pages/user/distribution-qrcode'
      });
    },
    
    handleSharePoster() {
      this.showPosterDialog = true;
    },
    
    handleApplyWithdraw() {
      this.showWithdrawDialog = true;
    },
    
    handleViewCustomers() {
      this.currentTab = 'customer';
      this.loadCustomerData();
    },
    
    // ========== 筛选和详情 ==========
    
    handleCommissionFilter(filter) {
      if (this.commissionFilter === filter) return;
      this.commissionFilter = filter;
      this.loadCommissionData();
    },
    
    handlePeriodChange(period) {
      if (this.periodFilter === period) return;
      this.periodFilter = period;
      this.loadCommissionData();
    },
    
    handleCustomerFilter(filter) {
      if (this.customerFilter === filter) return;
      this.customerFilter = filter;
      this.loadCustomerData();
    },
    
    handleRecordDetail(record) {
      uni.navigateTo({
        url: `/pages/user/commission-detail?id=${record.id}`
      });
    },
    
    handleCustomerDetail(customer) {
      uni.navigateTo({
        url: `/pages/user/customer-detail?id=${customer.customerId}`
      });
    },
    
    handleWithdrawDetail(record) {
      uni.navigateTo({
        url: `/pages/user/withdraw-detail?id=${record.id}`
      });
    },
    
    async handleWithdrawSubmit(withdrawData) {
      try {
        uni.showLoading({ title: '提交中...' });
        
        const res = await applyCommissionWithdraw(withdrawData);
        if (res.code === 200) {
          uni.showToast({
            title: '提现申请成功',
            icon: 'success'
          });
          this.showWithdrawDialog = false;
          this.loadWithdrawData();
        } else {
          throw new Error(res.message);
        }
      } catch (error) {
        console.error('提现申请失败:', error);
        uni.showToast({
          title: error.message || '申请失败',
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.distribution-center {
  position: relative;
  min-height: 100vh;
  background: #F8F8F8;
}

/* 申请页面样式 */
.apply-page {
  position: relative;
  min-height: 100vh;
  background: #F8F8F8;
}

.top-container {
  position: relative;
  height: 900rpx; /* 调整高度以适应新的布局 */
  background: #A9FF00;
  overflow: visible; /* 改为visible让曲线显示 */
  margin-top: -88rpx; /* 导航栏高度的负值，让背景延伸到导航栏下方 */
  padding-top: 88rpx; /* 为导航栏留出空间 */
  border-radius: 0 0 50% 50% / 0 0 80rpx 80rpx; /* 底部曲线：两边高中间低 */
  border-bottom: 8rpx solid #71C100; /* 绿色描边 */
}



.top-bg {
  position: absolute;
  top: 88rpx; /* 从导航栏下方开始 */
  left: 0;
  width: 100%;
  height: 465rpx; /* 固定高度 */
  z-index: 1;
}

.apply-card {
  position: absolute;
  top: 520rpx; /* 向上移动，给按钮留出更多空间 */
  left: 30rpx;
  right: 30rpx;
  z-index: 20; /* 提高z-index，确保不被曲线区域遮挡 */
  background: #FFFFFF;
  border-radius: 24rpx;
  padding: 40rpx 30rpx;
  margin-bottom: 100rpx; /* 增加底部间距，为按钮留空间 */
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.15);
}

.card-header {
  text-align: center;
  margin-bottom: 30rpx;
}

.user-phone {
  font-size: 28rpx;
  color: #666;
  margin-bottom: 10rpx;
}

.card-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #000;
}

.advantage-images {
  display: flex;
  justify-content: space-between;
  gap: 15rpx;
}

.advantage-images image {
  flex: 1;
  width: 100%;
  max-height: 140rpx;
}



.apply-button-container {
  position: absolute;
  bottom: -40rpx; /* 向上移动20rpx，从-60rpx改为-40rpx */
  left: 50%;
  transform: translateX(-50%);
  z-index: 50; /* 确保在所有元素之上 */
  display: flex;
  justify-content: center;
  align-items: center;
}

.apply-button {
  width: 500rpx !important;
  height: 120rpx !important;
  filter: drop-shadow(0 8rpx 16rpx rgba(0, 0, 0, 0.2));
}

.benefits-container {
  background: #DFFFD6;
  padding: 200rpx 30rpx 80rpx; /* 大幅增加顶部内边距 */
  margin-top: -160rpx; /* 向上移动覆盖空白区域 */
  position: relative;
  z-index: 10; /* 降低z-index，不遮挡曲线 */
  display: flex;
  flex-direction: column;
  gap: 30rpx;
}

.benefits-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 120rpx;
  background: transparent; /* 透明区域，让曲线显示 */
  z-index: 1;
}

.benefits-container image {
  width: 100%;
  height: auto;
  display: block;
}

/* 分销商中心样式 */
.distributor-center {
  position: relative;
  min-height: 100vh;
  background: #F8F8F8;
}

.background-decoration {
  position: absolute;
  top: -170rpx; /* 导航栏高度的负值 */
  left: 0;
  width: 100%;
  height: 488rpx; /* 原高度 + 导航栏高度 */
  background: linear-gradient(180deg, rgba(221, 255, 153, 0.8) 0%, rgba(221, 255, 153, 0) 100%);
  z-index: 1;
}

.content {
  position: relative;
  z-index: 5;
  padding: 20rpx 20rpx 120rpx;
}



/* 状态卡片样式 */
.status-section {
  margin-bottom: 40rpx;
}

.status-card {
  background: #FFFFFF;
  border-radius: 20rpx;
  padding: 80rpx 40rpx;
  text-align: center;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.1);
}

.status-icon {
  font-size: 120rpx;
  margin-bottom: 32rpx;
  
  &.pending-icon {
    color: #FF9500;
  }
  
  &.rejected-icon {
    color: #FF3B30;
  }
}

.status-title {
  display: block;
  font-size: 36rpx;
  font-weight: bold;
  color: #000000;
  margin-bottom: 16rpx;
}

.status-desc {
  display: block;
  font-size: 28rpx;
  color: #666666;
  margin-bottom: 24rpx;
  line-height: 1.5;
}

.status-time {
  display: block;
  font-size: 24rpx;
  color: #999999;
}

.reapply-btn {
  background: #A9FF00;
  color: #000000;
  border-radius: 44rpx;
  padding: 24rpx 60rpx;
  border: none;
  font-size: 28rpx;
  font-weight: 500;
  margin-top: 40rpx;
}

/* 分销商内容区域 */
.distributor-content {
  background: #F8F8F8;
  min-height: 100vh;
}

/* 顶部图片卡片 */
.top-card {
  position: relative;
  height: 270rpx;
  margin: 20rpx 0;
  border-radius: 20rpx;
  overflow: hidden;
}

.card-bg {
  width: 100%;
  height: 100%;
}

.card-content {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0;
}

.banner-image {
  width: 100%;
  height: 100%;
}

.record-btn {
  position: absolute;
  right: 0;
  top: 30rpx;
  transform: translateY(-50%);
  display: flex;
  align-items: center;
  background: linear-gradient(180deg, #A7FD03 0%, #B2F597 100%);
  border-radius: 16px 0px 0px 16px;
  padding: 8rpx 32rpx;
}

.record-text {
  font-size: 26rpx;
  color: #000000;
  margin-right: 8rpx;
}

.record-arrow {
  font-size: 20rpx;
  color: #000000;
}

/* 我的推荐 */
.recommend-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(90deg, #A7FD03 0%, #B2F597 51.39%);
  margin: 20rpx 0;
  padding: 24rpx 30rpx;
  border-radius: 16rpx;
}

.recommend-left {
  display: flex;
  align-items: center;
}

.recommend-label {
  font-size: 28rpx;
  color: #000000;
  margin-right: 20rpx;
}

.recommend-avatars {
  display: flex;
  align-items: center;
}

.avatar-item {
  width: 48rpx;
  height: 48rpx;
  border-radius: 50%;
  margin-right: 8rpx;
  border: 2rpx solid #FFFFFF;
}

.recommend-count {
  font-size: 28rpx;
  color: #000000;
  font-weight: bold;
  margin-left: 8rpx;
}

.recommend-right {
  display: flex;
  align-items: center;
}

.invite-btn {
  display: flex;
  align-items: center;
  background: #000000;
  border-radius: 30rpx;
  padding: 12rpx 24rpx;
}

.invite-icon {
  width: 32rpx;
  height: 32rpx;
  margin-right: 8rpx;
}

.invite-text {
  font-size: 24rpx;
  color: #A9FF00;
}

/* 团长推广标题 */
.section-header {
  margin: 30rpx 0 20rpx;
}

.title-wrapper {
  display: inline-flex;
  align-items: center;
  position: relative;
}

.title-text {
  font-size: 32rpx;
  font-weight: bold;
  color: #000000;
  position: relative;
  z-index: 2;
}

.title-indicator {
  width: 34rpx;
  height: 34rpx;
  position: absolute;
  right: -20rpx;
  z-index: 1;
}

/* 商品分类区域 */
.category-section {
  width: 100%;
  background: #FFFFFF;
  margin-bottom: 30rpx;
  padding: 20rpx 0;
}

.category-scroll {
  width: 100%;
}

.category-grid {
  display: flex;
  padding: 0 30rpx;
  gap: 20rpx;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 120rpx;
  padding: 20rpx 10rpx;
  border-radius: 16rpx;
  background: #FFFFFF;
  flex-shrink: 0;
}

.category-item.active {
  background: #F8F8F8;
}

.category-icon-wrapper {
  width: 80rpx;
  height: 80rpx;
  margin-bottom: 12rpx;
}

.category-icon {
  width: 100%;
  height: 100%;
}

.category-name {
  font-size: 24rpx;
  color: #333333;
  text-align: center;
}

/* 商品列表区域 */
.product-section {
  margin-bottom: 40rpx;
  overflow: visible;
}

.product-grid {
  column-count: 2;
  column-gap: 16rpx;
  padding: 10rpx 18rpx 0 18rpx;
  overflow: visible;
}

.product-item {
  break-inside: avoid;
  margin-bottom: 24rpx;
  background: #fff;
  border-radius: 8rpx;
  overflow: visible;
  box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.04);
  position: relative;
  margin-left: 18rpx;
  margin-top: 10rpx;
}

.product-image-wrapper {
  position: relative;
  width: 100%;
  height: 320rpx;
}

.product-thumb {
  width: 100%;
  height: 100%;
}

.commission-badge {
  position: absolute;
  top: -10rpx;
  left: -15rpx;
  width: 142rpx;
  height: 57rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
}

.commission-badge::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url('https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/sales-tag-background.png');
  background-size: 100% 100%;
  background-repeat: no-repeat;
  z-index: 1;
}

.commission-text {
  position: relative;
  z-index: 2;
  font-size: 22rpx;
  font-weight: 500;
  color: #000000;
  line-height: 1;
  text-align: center;
}

.product-info {
  padding: 16rpx;
}

.product-name {
  font-size: 26rpx;
  color: #000;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 8rpx;
}

.price-sales-row {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 8rpx;
}

.product-price {
  font-size: 36rpx;
  font-weight: 600;
  color: #647D00;
  flex: 0 0 auto;
}

.promotion-action {
  flex: 1 1 auto;
  text-align: right;
}

.promotion-text {
  background: #000000;
  color: #A9FF00;
  font-size: 24rpx;
  font-weight: 400;
  padding: 8rpx 16rpx;
  border-radius: 12rpx;
  display: inline-block;
}

/* 加载更多 */
.load-more-section {
  text-align: center;
  padding: 40rpx 0;
}

.load-more-text {
  font-size: 28rpx;
  color: #999999;
}

/* 申请状态区域 */
.status-section {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
  padding: 40rpx;
}

.status-card {
  background: #FFFFFF;
  border-radius: 20rpx;
  padding: 60rpx 40rpx;
  text-align: center;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.1);
  max-width: 600rpx;
  width: 100%;
}

.status-icon {
  font-size: 120rpx;
  margin-bottom: 30rpx;
  display: block;
}

.rejected-icon {
  color: #FF6B6B;
}

.not-applied-icon {
  color: #A9FF00;
}

.status-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 20rpx;
  display: block;
}

.status-desc {
  font-size: 28rpx;
  color: #666666;
  line-height: 1.6;
  margin-bottom: 40rpx;
  display: block;
}

.reapply-btn,
.apply-btn {
  background: #A9FF00;
  color: #000000;
  font-size: 32rpx;
  font-weight: bold;
  padding: 20rpx 60rpx;
  border-radius: 50rpx;
  border: none;
  margin-top: 20rpx;
}

.reapply-btn:active,
.apply-btn:active {
  background: #8FE600;
}

.tab-container {
  background: #FFFFFF;
  border-radius: 20rpx;
  margin-top: 30rpx;
  overflow: hidden;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.1);
}

.tab-header {
  display: flex;
  background: #F8F9FA;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 32rpx;
  position: relative;
  
  &.active {
    background: #FFFFFF;
    
    .tab-text {
      color: #A9FF00;
      font-weight: bold;
    }
    
    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 50%;
      transform: translateX(-50%);
      width: 60rpx;
      height: 4rpx;
      background: #A9FF00;
      border-radius: 2rpx;
    }
  }
}

.tab-text {
  font-size: 30rpx;
  color: #666666;
  transition: all 0.3s;
}

.tab-content {
  min-height: 600rpx;
}
</style> 