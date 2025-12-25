<template>
  <view class="store-map-container">
    <map
      id="storeMap"
      class="store-map"
      :longitude="mapCenter.longitude"
      :latitude="mapCenter.latitude"
      :scale="mapScale"
      :markers="mapMarkers"
      :show-location="true"
      :enable-3D="false"
      :enable-overlooking="false"
      :enable-zoom="true"
      :enable-scroll="true"
      :enable-rotate="false"
      @markertap="onMarkerTap"
      @regionchange="onRegionChange"
      @tap="onMapTap"
    >
      <!-- 地图控件 -->
      <cover-view class="map-controls">
        <cover-view class="control-btn location-btn" @tap="centerToCurrentLocation">
          <cover-image 
            class="control-icon" 
            src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/address.png"
          />
        </cover-view>
      </cover-view>
    </map>

    <!-- 门店详情弹窗 -->
    <view v-if="selectedStore" class="store-detail-popup" @tap="closeStoreDetail">
      <view class="popup-content" @tap.stop>
        <view class="store-info">
          <view class="store-header">
            <image 
              :src="selectedStore.logo || defaultLogo" 
              class="store-logo"
              mode="aspectFit"
            />
            <view class="store-basic-info">
              <text class="store-name">{{ selectedStore.addressName }}</text>
              <view class="store-hours">
                <view class="hours-tag">营业时间</view>
                <text class="hours-text">{{ selectedStore.businessHours || '10:00-18:40' }}</text>
              </view>
            </view>
          </view>
          <view class="store-address">
            <image 
              class="location-icon" 
              src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/address.png" 
              mode="aspectFit" 
            />
            <text class="address-text">{{ getFullAddress(selectedStore) }}</text>
          </view>
          <view class="store-actions">
            <view class="action-btn navigate-btn" @tap="navigateToStore">
              <text class="btn-text">导航</text>
            </view>
            <view class="action-btn visit-btn" @tap="visitStore">
              <text class="btn-text">去逛逛</text>
            </view>
          </view>
        </view>
        <view class="close-btn" @tap="closeStoreDetail">
          <view class="close-icon">×</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'StoreMap',
  props: {
    stores: {
      type: Array,
      default: () => []
    },
    selectedStoreId: {
      type: [String, Number],
      default: null
    },
    searchKeyword: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      mapCenter: {
        longitude: 113.3245,
        latitude: 23.0979
      },
      mapScale: 13,
      selectedStore: null,
      currentLocation: null,
      defaultLogo: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/guanghengzou_logo.png'
    }
  },
  computed: {
    // 地图标记点
    mapMarkers() {
      return this.filteredStores.map((store, index) => {
        const longitude = parseFloat(store.longitude);
        const latitude = parseFloat(store.latitude);

        // 验证坐标有效性
        if (isNaN(longitude) || isNaN(latitude)) {
          console.warn(`门店 ${store.addressName} 坐标无效:`, store.longitude, store.latitude);
          return null;
        }

        return {
          id: store.id,
          longitude: longitude,
          latitude: latitude,
          iconPath: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/images/store_map_icon.png',
          width: this.selectedStoreId === store.id ? 50 : 40,
          height: this.selectedStoreId === store.id ? 50 : 40,
          callout: {
            content: store.addressName,
            color: '#000000',
            fontSize: 12,
            borderRadius: 4,
            bgColor: '#ffffff',
            padding: 8,
            display: 'BYCLICK'
          }
        };
      }).filter(marker => marker !== null);
    },
    // 过滤后的门店列表
    filteredStores() {
      if (!this.searchKeyword) {
        return this.stores
      }
      return this.stores.filter(store => 
        store.addressName.includes(this.searchKeyword) ||
        store.address.includes(this.searchKeyword)
      )
    }
  },
  watch: {
    selectedStoreId(newId) {
      if (newId) {
        this.centerToStore(newId)
        // 高亮选中的门店标记
        this.highlightSelectedStore(newId)
      }
    },
    stores: {
      handler(newStores) {
        if (newStores.length > 0) {
          this.initMapCenter()
        }
      },
      immediate: true
    },
    searchKeyword: {
      handler(newKeyword) {
        // 搜索关键词变化时，重新计算地图中心点
        if (this.filteredStores.length > 0) {
          this.initMapCenter()
        }
      }
    }
  },
  mounted() {
    this.getCurrentLocation()
  },
  methods: {
    // 获取当前位置
    getCurrentLocation() {
      uni.getLocation({
        type: 'gcj02',
        success: (res) => {
          this.currentLocation = {
            longitude: res.longitude,
            latitude: res.latitude
          }
          this.mapCenter = { ...this.currentLocation }
        },
        fail: (err) => {
          console.warn('获取位置失败:', err)
          // 使用默认位置（广州）
        }
      })
    },

    // 初始化地图中心点
    initMapCenter() {
      if (this.stores.length === 0) return
      
      // 计算所有门店的中心点
      let totalLng = 0
      let totalLat = 0
      let validCount = 0
      
      this.stores.forEach(store => {
        const lng = parseFloat(store.longitude)
        const lat = parseFloat(store.latitude)
        if (!isNaN(lng) && !isNaN(lat)) {
          totalLng += lng
          totalLat += lat
          validCount++
        }
      })
      
      if (validCount > 0) {
        this.mapCenter = {
          longitude: totalLng / validCount,
          latitude: totalLat / validCount
        }
      }
    },

    // 定位到指定门店
    centerToStore(storeId) {
      const store = this.stores.find(s => s.id === storeId)
      if (store && store.longitude && store.latitude) {
        this.mapCenter = {
          longitude: parseFloat(store.longitude),
          latitude: parseFloat(store.latitude)
        }
        this.mapScale = 16
      }
    },

    // 高亮选中的门店
    highlightSelectedStore(storeId) {
      // 这里可以通过修改markers的样式来高亮选中的门店
      // uni-app的map组件可以通过不同的iconPath来实现
      console.log('高亮门店:', storeId)
    },

    // 定位到当前位置
    centerToCurrentLocation() {
      if (this.currentLocation) {
        this.mapCenter = { ...this.currentLocation }
        this.mapScale = 15
      } else {
        this.getCurrentLocation()
      }
    },

    // 标记点击事件
    onMarkerTap(e) {
      const markerId = e.detail.markerId
      const store = this.stores.find(s => s.id === markerId)
      if (store) {
        this.selectedStore = store
        this.$emit('store-selected', store)
      }
    },

    // 地图区域变化事件
    onRegionChange(e) {
      if (e.detail.type === 'end') {
        this.mapCenter = {
          longitude: e.detail.centerLocation.longitude,
          latitude: e.detail.centerLocation.latitude
        }
      }
    },

    // 地图点击事件
    onMapTap() {
      // 可以在这里处理地图点击事件
    },

    // 关闭门店详情
    closeStoreDetail() {
      this.selectedStore = null
    },

    // 导航到门店
    navigateToStore() {
      if (!this.selectedStore) return
      
      const store = this.selectedStore
      const destination = `${store.latitude},${store.longitude}`
      const name = store.addressName
      
      // 调用系统地图导航
      uni.openLocation({
        latitude: parseFloat(store.latitude),
        longitude: parseFloat(store.longitude),
        name: name,
        address: this.getFullAddress(store),
        success: () => {
          console.log('打开地图成功')
        },
        fail: (err) => {
          console.error('打开地图失败:', err)
          uni.showToast({
            title: '打开地图失败',
            icon: 'none'
          })
        }
      })
    },

    // 访问门店
    visitStore() {
      if (this.selectedStore) {
        this.$emit('visit-store', this.selectedStore)
        this.closeStoreDetail()
      }
    },

    // 获取完整地址
    getFullAddress(store) {
      return `${store.province || ''}${store.city || ''}${store.region || ''}${store.address || store.detailAddress || ''}`
    }
  }
}
</script>

<style scoped>
.store-map-container {
  position: relative;
  width: 100%;
  height: 100%;
}

.store-map {
  width: 100%;
  height: 100%;
}

/* 地图控件 */
.map-controls {
  position: absolute;
  right: 20rpx;
  bottom: 20rpx;
  z-index: 10;
}

.control-btn {
  width: 80rpx;
  height: 80rpx;
  background-color: #ffffff;
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0px 2px 8px rgba(0, 0, 0, 0.15);
  margin-bottom: 16rpx;
}

.control-icon {
  width: 40rpx;
  height: 40rpx;
}

/* 门店详情弹窗 */
.store-detail-popup {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: rgba(0, 0, 0, 0.3);
  z-index: 20;
  padding: 0 32rpx 32rpx;
}

.popup-content {
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 32rpx;
  position: relative;
  box-shadow: 0px 0px 6px rgba(0, 0, 0, 0.08);
}

.close-btn {
  position: absolute;
  top: 16rpx;
  right: 16rpx;
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-icon {
  font-size: 32rpx;
  color: #999999;
  line-height: 1;
}

.store-header {
  display: flex;
  align-items: center;
  margin-bottom: 24rpx;
}

.store-logo {
  width: 80rpx;
  height: 80rpx;
  border-radius: 8rpx;
  margin-right: 24rpx;
}

.store-basic-info {
  flex: 1;
}

.store-name {
  font-size: 28rpx;
  font-weight: 500;
  color: #000000;
  line-height: 1.4;
  margin-bottom: 8rpx;
}

.store-hours {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.hours-tag {
  padding: 4rpx 8rpx;
  background-color: transparent;
  border: 1rpx solid #282921;
  border-radius: 6rpx;
  font-size: 18rpx;
  color: #282921;
  line-height: 1.3;
}

.hours-text {
  font-size: 24rpx;
  color: #000000;
  opacity: 0.5;
  line-height: 1.3;
}

.store-address {
  display: flex;
  align-items: center;
  margin-bottom: 32rpx;
}

.location-icon {
  width: 24rpx;
  height: 24rpx;
  margin-right: 8rpx;
}

.address-text {
  font-size: 24rpx;
  color: #9FA19D;
  line-height: 1.4;
  flex: 1;
}

.store-actions {
  display: flex;
  gap: 16rpx;
}

.action-btn {
  flex: 1;
  height: 72rpx;
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.navigate-btn {
  background-color: #f5f5f5;
}

.visit-btn {
  background-color: #A9FF00;
}

.btn-text {
  font-size: 28rpx;
  font-weight: 500;
  line-height: 1;
}

.navigate-btn .btn-text {
  color: #000000;
}

.visit-btn .btn-text {
  color: #000000;
}
</style>
