<template>
  <view class="store-list-page">
    <!-- 地图区域 - 置顶显示 -->
    <view class="map-container" :class="{ 'map-expanded': isListCollapsed }" @tap="onMapClick">
      <store-map
        :stores="filteredStores"
        :selectedStoreId="selectedStoreId"
        :searchKeyword="searchKeyword"
        @store-selected="onMapStoreSelected"
        @visit-store="visitStore"
      />
    </view>

    <!-- 顶部导航栏 - 覆盖在地图上方 -->
    <nav-bar
      title=""
      :placeholder="false"
      :back="true"
      :bg-color="'transparent'"
      :hasSlot="true"
      class="floating-nav"
    >
      <!-- 搜索栏作为slot插槽 -->
      <search-bar
        :value="searchKeyword"
        :transparent="true"
        @search="onSearch"
        @clear="onSearchClear"
      />
    </nav-bar>

    <!-- 门店列表区域 -->
    <view class="store-list-container" :class="{ 'list-collapsed': isListCollapsed }">
      <!-- 下滑指示器（仅在展开状态显示） -->
      <view v-if="!isListCollapsed" class="pull-down-indicator" @tap="toggleList">
      </view>

      <!-- 上滑入口提示条（仅在收起状态显示） -->
      <view v-if="isListCollapsed" class="pull-up-indicator" @tap="expandList">
        <view class="indicator-line"></view>
        <text class="indicator-text">上滑查看门店列表</text>
      </view>

      <scroll-view
        class="store-list-scroll"
        scroll-y="true"
        @scroll="onListScroll"
        @touchstart="onTouchStart"
        @touchmove="onTouchMove"
        @touchend="onTouchEnd"
      >
        <view v-if="filteredStores.length > 0" class="store-cards">
          <store-card
            v-for="store in filteredStores"
            :key="store.id"
            :store="store"
            :distance="store.distance"
            @click="onStoreCardClick"
            @visit="visitStore"
          />
        </view>

        <!-- 空状态 -->
        <view v-else-if="!loading" class="empty-state">
          <image
            class="empty-icon"
            src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/empty.png"
            mode="aspectFit"
          />
          <text class="empty-text">{{ searchKeyword ? '未找到相关门店' : '暂无门店信息' }}</text>
        </view>
      </scroll-view>
    </view>

    <!-- 加载提示 -->
    <uni-load-more
      v-if="loading"
      :status="loadingStatus"
      :icon-size="16"
      :content-text="loadingText"
    />
  </view>
</template>

<script>
import NavBar from '@/components/nav-bar.vue';
import SearchBar from '@/components/store/SearchBar.vue';
import StoreMap from '@/components/store/StoreMap.vue';
import StoreCard from '@/components/store/StoreCard.vue';
import { fetchStoreList, fetchStoreListBySchool, searchStores } from '@/api/store.js';

export default {
  components: {
    'nav-bar': NavBar,
    'search-bar': SearchBar,
    'store-map': StoreMap,
    'store-card': StoreCard
  },
  data() {
    return {
      statusBarHeight: 0,
      navBarHeight: 0,
      loading: true,
      loadingStatus: 'loading',
      loadingText: {
        contentdown: '上拉显示更多',
        contentrefresh: '正在加载...',
        contentnomore: '没有更多数据了'
      },
      storeList: [],
      searchKeyword: '',
      selectedStoreId: null,
      schoolFilter: null, // 学校筛选条件
      isApiFiltered: false, // 标记是否已通过API进行学校筛选
      currentLocation: null,
      searchTimer: null, // 搜索防抖定时器
      isListCollapsed: false, // 门店列表是否收起
      lastScrollTop: 0, // 上次滚动位置
      scrollDirection: 'up', // 滚动方向
      touchStartY: null, // 触摸开始Y坐标
      touchCurrentY: null, // 触摸当前Y坐标
      touchStartTime: null // 触摸开始时间
    };
  },

  computed: {
    // 过滤后的门店列表
    filteredStores() {
      let stores = this.storeList;

      // 如果没有通过API进行学校筛选（即获取的是全部门店），则在前端进行学校筛选
      if (this.schoolFilter && !this.isApiFiltered) {
        stores = stores.filter(store =>
          store.schoolId === this.schoolFilter.id ||
          store.schoolName === this.schoolFilter.name
        );
      }

      // 按搜索关键词筛选
      if (this.searchKeyword) {
        stores = stores.filter(store =>
          store.addressName.includes(this.searchKeyword) ||
          store.address.includes(this.searchKeyword) ||
          (store.province + store.city + store.region).includes(this.searchKeyword)
        );
      }

      return stores;
    }
  },

  onLoad(options) {
    // 获取系统信息
    const systemInfo = uni.getSystemInfoSync();
    this.statusBarHeight = systemInfo.statusBarHeight;
    this.navBarHeight = this.statusBarHeight + 44;

    // 获取学校筛选参数
    if (options.schoolId) {
      this.schoolFilter = {
        id: parseInt(options.schoolId), // 转换为数字类型
        name: decodeURIComponent(options.schoolName || '') // 解码学校名称
      };
    } else {
      // 没有学校参数时，尝试从本地存储获取已选择的学校
      try {
        const schoolInfo = uni.getStorageSync('selectedSchool');
        if (schoolInfo) {
          const school = JSON.parse(schoolInfo);
          this.schoolFilter = {
            id: school.id,
            name: school.schoolName || ''
          };
        }
      } catch (error) {
        console.log('获取本地学校信息失败:', error);
      }
    }

    // 获取当前位置
    this.getCurrentLocation();

    // 获取门店数据
    this.fetchStores();
  },

  methods: {
    // 获取当前位置
    getCurrentLocation() {
      console.log('开始获取位置...');
      uni.getLocation({
        type: 'gcj02',
        success: (res) => {
          console.log('位置获取成功:', res);
          this.currentLocation = {
            longitude: res.longitude,
            latitude: res.latitude
          };
          console.log('当前位置设置为:', this.currentLocation);
          // 位置获取成功后，重新计算所有门店的距离
          this.recalculateDistances();
        },
        fail: (err) => {
          console.warn('获取位置失败:', err);
        }
      });
    },

    // 重新计算所有门店的距离
    recalculateDistances() {
      console.log('开始重新计算距离, 门店数量:', this.storeList?.length);
      console.log('当前位置:', this.currentLocation);
      if (this.storeList && this.storeList.length > 0) {
        this.storeList.forEach((store, index) => {
          const oldDistance = store.distance;
          store.distance = this.calculateDistance(store);
          console.log(`门店${index + 1} (${store.addressName}):`, {
            经度: store.longitude,
            纬度: store.latitude,
            原距离: oldDistance,
            新距离: store.distance
          });
        });
        console.log('距离重新计算完成');
      } else {
        console.log('没有门店数据需要计算距离');
      }
    },

    // 获取门店列表
    async fetchStores() {
      try {
        this.loading = true;
        this.loadingStatus = 'loading';

        let res;
        // 如果有学校筛选条件，使用学校筛选接口
        if (this.schoolFilter && this.schoolFilter.id) {
          res = await fetchStoreListBySchool(this.schoolFilter.id);
          this.isApiFiltered = true; // 标记已通过API筛选
        } else {
          res = await fetchStoreList();
          this.isApiFiltered = false; // 标记未通过API筛选
        }

        if (res && res.code === 200 && res.data) {
          this.storeList = res.data.map(store => {
            // 数据验证和处理
            const processedStore = {
              id: store.id,
              addressName: store.addressName || store.name || store.storeName || '',
              address: store.address || store.detailAddress || '',
              logo: store.logo || 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/guanghengzou_logo.png',
              businessHours: store.businessHours || '10:00-18:40',
              phone: store.phone || '',
              province: store.province || '',
              city: store.city || '',
              region: store.region || '',
              longitude: this.validateCoordinate(store.longitude, 'longitude'),
              latitude: this.validateCoordinate(store.latitude, 'latitude'),
              isDefault: store.isDefault || false,
              schoolId: store.schoolId || null,
              schoolName: store.schoolName || '',
              images: Array.isArray(store.images) ? store.images : [],
              storeGallery: store.storeGallery || '', // 门店相册字段（后端返回逗号分隔字符串）
              store_gallery: Array.isArray(store.store_gallery) ? store.store_gallery : [], // 兼容数组格式
              distance: null // 将在后面计算
            };

            // 计算距离
            processedStore.distance = this.calculateDistance(processedStore);

            return processedStore;
          });
        } else {
          uni.showToast({
            title: '获取门店列表失败',
            icon: 'none'
          });
        }
      } catch (error) {
        console.error('获取门店列表失败:', error);
        uni.showToast({
          title: '网络错误',
          icon: 'none'
        });
      } finally {
        this.loading = false;
        this.loadingStatus = 'noMore';
        // 门店数据加载完成后，如果已有位置信息，重新计算距离
        if (this.currentLocation) {
          console.log('门店数据加载完成，重新计算距离');
          this.recalculateDistances();
        }
      }
    },

    // 执行搜索
    async performSearch(keyword) {
      if (!keyword.trim()) {
        // 如果搜索关键词为空，重新获取门店列表
        await this.fetchStores();
        return;
      }

      try {
        this.loading = true;
        // 确保schoolId为null而不是字符串"null"
        const schoolId = this.schoolFilter && this.schoolFilter.id ? this.schoolFilter.id : null;
        const res = await searchStores(keyword, schoolId);

        // 搜索时，如果传递了schoolId，则认为已通过API筛选
        this.isApiFiltered = schoolId !== null;

        if (res && res.code === 200 && res.data) {
          this.storeList = res.data.map(store => {
            // 数据验证和处理（与fetchStores保持一致）
            const processedStore = {
              id: store.id,
              addressName: store.addressName || store.name || store.storeName || '',
              address: store.address || store.detailAddress || '',
              logo: store.logo || 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/guanghengzou_logo.png',
              businessHours: store.businessHours || '10:00-18:40',
              phone: store.phone || '',
              province: store.province || '',
              city: store.city || '',
              region: store.region || '',
              longitude: this.validateCoordinate(store.longitude, 'longitude'),
              latitude: this.validateCoordinate(store.latitude, 'latitude'),
              isDefault: store.isDefault || false,
              schoolId: store.schoolId || null,
              schoolName: store.schoolName || '',
              images: Array.isArray(store.images) ? store.images : [],
              storeGallery: store.storeGallery || '', // 门店相册字段（后端返回逗号分隔字符串）
              store_gallery: Array.isArray(store.store_gallery) ? store.store_gallery : [], // 兼容数组格式
              distance: null // 将在后面计算
            };

            // 计算距离
            processedStore.distance = this.calculateDistance(processedStore);

            return processedStore;
          });
        }
      } catch (error) {
        console.error('搜索门店失败:', error);
        uni.showToast({
          title: '搜索失败',
          icon: 'none'
        });
      } finally {
        this.loading = false;
        // 搜索结果加载完成后，如果已有位置信息，重新计算距离
        if (this.currentLocation) {
          console.log('搜索结果加载完成，重新计算距离');
          this.recalculateDistances();
        }
      }
    },

    // 验证坐标数据
    validateCoordinate(value, type) {
      const num = parseFloat(value);
      if (isNaN(num)) {
        // 如果坐标无效，返回默认坐标（广州）
        return type === 'longitude' ? 113.3245 : 23.0979;
      }

      // 验证坐标范围
      if (type === 'longitude' && (num < -180 || num > 180)) {
        return 113.3245; // 默认经度
      }
      if (type === 'latitude' && (num < -90 || num > 90)) {
        return 23.0979; // 默认纬度
      }

      return num;
    },

    // 计算距离
    calculateDistance(store) {
      console.log('计算距离 - 输入参数:', {
        currentLocation: this.currentLocation,
        storeLongitude: store.longitude,
        storeLatitude: store.latitude,
        storeName: store.addressName
      });

      if (!this.currentLocation || !store.longitude || !store.latitude) {
        console.log('距离计算失败 - 缺少必要参数');
        return null;
      }

      const lat1 = this.currentLocation.latitude;
      const lng1 = this.currentLocation.longitude;
      const lat2 = parseFloat(store.latitude);
      const lng2 = parseFloat(store.longitude);

      console.log('距离计算 - 坐标信息:', {
        用户位置: { lat: lat1, lng: lng1 },
        门店位置: { lat: lat2, lng: lng2 }
      });

      // 验证坐标有效性
      if (isNaN(lat2) || isNaN(lng2)) {
        console.log('距离计算失败 - 门店坐标无效');
        return null;
      }

      const R = 6371; // 地球半径（公里）
      const dLat = (lat2 - lat1) * Math.PI / 180;
      const dLng = (lng2 - lng1) * Math.PI / 180;
      const a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                Math.sin(dLng/2) * Math.sin(dLng/2);
      const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
      const distance = R * c;

      console.log('距离计算结果:', distance, 'km');
      return distance;
    },

    // 搜索事件（添加防抖优化）
    onSearch(keyword) {
      this.searchKeyword = keyword;

      // 清除之前的搜索定时器
      if (this.searchTimer) {
        clearTimeout(this.searchTimer);
      }

      // 设置新的搜索定时器（防抖）
      this.searchTimer = setTimeout(() => {
        this.performSearch(keyword);
      }, 300);
    },

    // 清空搜索
    onSearchClear() {
      this.searchKeyword = '';
      // 清除搜索定时器
      if (this.searchTimer) {
        clearTimeout(this.searchTimer);
      }
      // 重新获取门店列表
      this.fetchStores();
    },

    // 地图门店选择事件
    onMapStoreSelected(store) {
      this.selectedStoreId = store.id;
      // 滚动到对应的门店卡片
      this.scrollToStoreCard(store.id);
    },

    // 门店卡片点击事件
    onStoreCardClick(store) {
      this.selectedStoreId = store.id;
    },

    // 滚动到指定门店卡片
    scrollToStoreCard(storeId) {
      // 使用uni-app的页面滚动API
      const query = uni.createSelectorQuery().in(this);
      query.select(`.store-card[data-store-id="${storeId}"]`).boundingClientRect((data) => {
        if (data) {
          uni.pageScrollTo({
            scrollTop: data.top - 100, // 预留一些空间
            duration: 300
          });
        }
      }).exec();
    },

    // 列表滚动事件
    onListScroll(e) {
      const { scrollTop, scrollHeight, clientHeight } = e.detail;

      // 判断滚动方向
      if (scrollTop > this.lastScrollTop) {
        this.scrollDirection = 'down';
      } else {
        this.scrollDirection = 'up';
      }
      this.lastScrollTop = scrollTop;

      // 检查是否接近底部
      if (scrollHeight - scrollTop - clientHeight < 100) {
        console.log('接近列表底部');
      }
    },

    // 触摸开始
    onTouchStart(e) {
      this.touchStartY = e.touches[0].clientY;
      this.touchStartTime = Date.now();
    },

    // 触摸移动
    onTouchMove(e) {
      this.touchCurrentY = e.touches[0].clientY;
    },

    // 触摸结束
    onTouchEnd() {
      if (!this.touchStartY || !this.touchCurrentY) return;

      const deltaY = this.touchCurrentY - this.touchStartY;
      const deltaTime = Date.now() - this.touchStartTime;
      const threshold = 30; // 降低滑动阈值，提高灵敏度
      const velocity = Math.abs(deltaY) / deltaTime; // 计算滑动速度

      // 下滑手势：收起列表（在列表顶部或者快速下滑）
      if (deltaY > threshold && (this.lastScrollTop <= 20 || velocity > 0.5)) {
        this.collapseList();
      }
      // 上滑手势：展开列表
      else if (deltaY < -threshold) {
        this.expandList();
      }

      // 重置触摸位置
      this.touchStartY = null;
      this.touchCurrentY = null;
      this.touchStartTime = null;
    },

    // 收起列表
    collapseList() {
      this.isListCollapsed = true;
    },

    // 展开列表
    expandList() {
      this.isListCollapsed = false;
    },

    // 切换列表显示状态
    toggleList() {
      this.isListCollapsed = !this.isListCollapsed;
    },

    // 地图点击事件
    onMapClick() {
      // 如果列表是展开状态，点击地图隐藏列表
      if (!this.isListCollapsed) {
        this.collapseList();
      }
    },

    // 访问门店
    visitStore(store) {
      console.log('访问门店:', store);

      // 存储选中的门店信息到缓存
      uni.setStorageSync('selectedStore', JSON.stringify({
        id: store.id,
        addressName: store.addressName,
        address: store.address,
        logo: store.logo || 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/guanghengzou_logo.png',
        businessHours: store.businessHours,
        phone: store.phone,
        province: store.province,
        city: store.city,
        region: store.region,
        longitude: store.longitude,
        latitude: store.latitude,
        isDefault: store.isDefault,
        schoolId: store.schoolId,
        schoolName: store.schoolName,
        selected: true
      }));

      // 发送事件给首页通知门店已选择
      uni.$emit('storeSelected', store);

      // 延迟返回上一页，让用户看到提示
      setTimeout(() => {
        uni.navigateBack({
          delta: 1
        });
      }, 1500);
    }
  },

  // 页面卸载时清理定时器
  onUnload() {
    if (this.searchTimer) {
      clearTimeout(this.searchTimer);
    }
  },

  // 页面隐藏时清理定时器
  onHide() {
    if (this.searchTimer) {
      clearTimeout(this.searchTimer);
    }
  }
};
</script>

<style lang="scss" scoped>
.store-list-page {
  position: relative;
  width: 100%;
  min-height: 100vh;
  background-color: #F8F8F8;
  font-family: 'PingFang SC', -apple-system, BlinkMacSystemFont, sans-serif;
}

/* 浮动导航栏 - 覆盖在地图上方 */
.floating-nav {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background: transparent !important;
}

/* 地图容器 - 置顶显示 */
.map-container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  width: 100%;
  height: 40vh;
  min-height: 400rpx;
  background-color: #ffffff;
  transition: height 0.3s ease-in-out;
  z-index: 1;
}

.map-container.map-expanded {
  height: calc(100vh - 120rpx);
}

/* 门店列表容器 */
.store-list-container {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  width: 100%;
  height: 60vh; /* 展开状态占用60%页面高度 */
  background-color: #F8F8F8;
  border-radius: 16rpx 16rpx 0 0;
  z-index: 100;
  transition: all 0.3s ease-in-out;
  box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.1);
}

.store-list-container.list-collapsed {
  height: 150rpx;
  overflow: hidden;
}

/* 下滑指示器 */
.pull-down-indicator {
  position: absolute;
  width: 60rpx;
  height: 4rpx;
  left: calc(50% - 30rpx);
  top: 16rpx;
  background: #66656B;
  border-radius: 3rpx;
  z-index: 100;
  cursor: pointer;
}

/* 上滑入口提示条 */
.pull-up-indicator {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  height: 150rpx;
  background-color: #ffffff;
  border-radius: 16rpx 16rpx 0 0;
  cursor: pointer;
  padding-top: 32rpx;
  z-index: 100;
}

.indicator-line {
  width: 60rpx;
  height: 4rpx;
  background-color: #66656B;
  border-radius: 3rpx;
  margin-bottom: 16rpx;
}

.indicator-text {
  font-size: 22rpx;
  color: #9FA19D;
  line-height: 1.2;
}

.store-list-scroll {
  height: calc(100% - 48rpx);
  padding-top: 48rpx;
}

.store-cards {
  padding-bottom: 60rpx;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 32rpx;
  text-align: center;
}

.empty-icon {
  width: 160rpx;
  height: 160rpx;
  margin-bottom: 32rpx;
  opacity: 0.6;
}

.empty-text {
  font-size: 28rpx;
  color: #9FA19D;
  line-height: 1.4;
  font-family: 'PingFang SC', -apple-system, BlinkMacSystemFont, sans-serif;
}

/* 加载状态 */
.uni-load-more {
  margin: 40rpx 0;
}
</style> 