<template>
  <view v-if="visible" class="store-selector-popup" @tap="handleMaskClick" @touchmove.stop.prevent="preventScroll">
    <view class="popup-content" @tap.stop @touchmove.stop>
      <!-- 弹窗头部 -->
      <view class="popup-header">
        <view class="header-title">选择高校</view>
        <view class="close-btn" @tap="close">
          <view class="close-icon-container">
            <view class="close-line close-line-1"></view>
            <view class="close-line close-line-2"></view>
          </view>
        </view>
      </view>

      <!-- 门店列表 -->
      <scroll-view class="store-list" scroll-y="true">
        <view v-for="(schoolGroup, schoolIndex) in schoolGroups" :key="schoolIndex" class="school-group">
          <!-- 学校头部 -->
          <view class="school-header">
            <view class="school-logo-container" @tap="selectSchoolOnly(schoolGroup)">
              <image
                v-if="schoolGroup.school && schoolGroup.school.schoolLogo"
                :src="schoolGroup.school.schoolLogo"
                class="school-logo"
                mode="aspectFit"
              />
              <view v-else class="school-logo-default">
                <!-- 华南理工大学默认logo SVG -->
                <view class="logo-svg-container">
                  <view class="logo-circle"></view>
                  <view class="logo-text">华工</view>
                </view>
              </view>
            </view>
            <view class="selection-indicator" :class="{ 'selected': selectedSchoolId && schoolGroup.school && selectedSchoolId == schoolGroup.school.id }" @tap="selectSchoolOnly(schoolGroup)">
              <view class="indicator-dot" v-if="selectedSchoolId && schoolGroup.school && selectedSchoolId == schoolGroup.school.id"></view>
            </view>
          </view>

          <!-- 门店列表 -->
          <view class="stores-container">
            <view
              v-for="(store, storeIndex) in schoolGroup.stores"
              :key="storeIndex"
              class="store-item"
              @tap="selectStore(store)"
            >
              <view class="store-info">
                <view class="store-header">
                  <view class="store-name">{{ store.addressName }}</view>
                  <view class="store-hours">
                    <view class="hours-tag">营业时间</view>
                    <text class="hours-text">{{ store.businessHours || '10:00-18:40' }}</text>
                  </view>
                </view>
                <view class="store-address">
                  <image class="location-icon" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/address.png" mode="aspectFit" />
                  <text class="address-text">{{ store.province + store.city + store.region + store.detailAddress }}</text>
                </view>
              </view>
            </view>
          </view>
        </view>

        <!-- 空状态 -->
        <view v-if="!loading && schoolGroups.length === 0" class="empty-state">
          <text class="empty-text">暂无门店信息</text>
        </view>

        <!-- 加载状态 -->
        <view v-if="loading" class="loading-state">
          <text class="loading-text">加载中...</text>
        </view>
      </scroll-view>
    </view>
  </view>
</template>

<script>
// 不再需要API导入，使用外部传入的数据

export default {
  name: 'StoreSelector',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    currentStore: {
      type: Object,
      default: null
    },
    schoolGroups: {
      type: Array,
      default: () => []
    },
    selectedSchoolId: {
      type: [String, Number],
      default: null
    }
  },
  data() {
    return {
      loading: false,
      // 用于保存原始页面样式
      originalPageStyle: null,
      originalBodyOverflow: '',
      originalBodyPosition: '',
      originalBodyTop: '',
      originalBodyLeft: '',
      originalBodyRight: '',
      originalHtmlOverflow: '',
      scrollTop: 0
    }
  },
  watch: {
    visible(newVal) {
      if (newVal) {
        // 弹窗打开时禁用页面滚动
        this.disablePageScroll()
      } else {
        // 弹窗关闭时恢复页面滚动
        this.enablePageScroll()
      }
    }
  },

  mounted() {
    // 如果组件挂载时弹窗已经是显示状态，则禁用页面滚动
    if (this.visible) {
      this.disablePageScroll()
    }
  },

  beforeDestroy() {
    // 组件销毁前确保恢复页面滚动
    this.enablePageScroll()
  },
  methods: {

    // 已移除loadStoreGroups方法，现在使用外部传入的数据

    // 获取学校显示图片（优先使用封面缩略图，回退到校徽）
    getSchoolDisplayImage(school) {
      if (!school) {
        return null;
      }

      // 优先使用封面缩略图
      if (school.coverThumbnail) {
        return school.coverThumbnail;
      }

      // 回退到校徽
      if (school.schoolLogo) {
        return school.schoolLogo;
      }

      // 没有图片
      return null;
    },

    // 切换分组展开/收起
    toggleGroup(groupIndex) {
      this.schoolGroups[groupIndex].expanded = !this.schoolGroups[groupIndex].expanded
      this.$forceUpdate()
    },
    
    // 选择学校（点击学校logo或选择指示器）
    selectSchoolOnly(schoolGroup) {
      if (schoolGroup.school) {
        // 构建学校信息
        const schoolInfo = {
          id: schoolGroup.school.id,
          schoolName: schoolGroup.school.schoolName,
          schoolLogo: schoolGroup.school.schoolLogo || null,
          coverThumbnail: schoolGroup.school.coverThumbnail || null
        }
        // 立即触发选择事件并关闭弹窗
        this.$emit('select', schoolInfo)
        this.close()
      }
    },

    // 选择门店（同时选择对应的学校）
    selectStore(store) {
      // 从选中的门店中提取学校信息
      const schoolInfo = this.getSchoolFromStore(store)
      if (schoolInfo) {
        // 立即触发选择事件并关闭弹窗
        this.$emit('select', schoolInfo)
        this.close()
      }
    },



    // 从门店信息中获取学校信息
    getSchoolFromStore(store) {
      if (!store) return null
      // 查找门店所属的学校
      for (const schoolGroup of this.schoolGroups) {
        if (schoolGroup.stores && schoolGroup.stores.some(s => s.id === store.id)) {
          return {
            id: schoolGroup.school.id,
            schoolName: schoolGroup.school.schoolName,
            schoolLogo: schoolGroup.school.schoolLogo || null,
            coverThumbnail: schoolGroup.school.coverThumbnail || null
          }
        }
      }
      return null
    },
    
    // 关闭弹窗
    close() {
      this.$emit('close')
    },

    // 禁用页面滚动
    disablePageScroll() {
      // #ifdef MP-WEIXIN || MP-ALIPAY || MP-BAIDU || MP-TOUTIAO || MP-QQ
      // 小程序环境下通过设置页面样式禁用滚动
      const pages = getCurrentPages()
      if (pages.length > 0) {
        const currentPage = pages[pages.length - 1]
        if (currentPage && currentPage.$page && currentPage.$page.meta) {
          this.originalPageStyle = currentPage.$page.meta.disableScroll
          currentPage.$page.meta.disableScroll = true
        }
      }
      // #endif

      // #ifdef H5
      // H5环境下通过设置body和html样式禁用滚动
      if (typeof document !== 'undefined') {
        // 保存原始样式
        this.originalBodyOverflow = document.body.style.overflow
        this.originalBodyPosition = document.body.style.position
        this.originalBodyTop = document.body.style.top
        this.originalBodyLeft = document.body.style.left
        this.originalBodyRight = document.body.style.right
        this.originalHtmlOverflow = document.documentElement.style.overflow

        // 获取当前滚动位置
        this.scrollTop = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop || 0

        // 设置禁用滚动的样式
        document.body.style.overflow = 'hidden'
        document.body.style.position = 'fixed'
        document.body.style.top = `-${this.scrollTop}px`
        document.body.style.left = '0'
        document.body.style.right = '0'
        document.body.style.width = '100%'
        document.documentElement.style.overflow = 'hidden'
      }
      // #endif

      // 添加全局触摸事件监听器
      if (typeof document !== 'undefined') {
        document.addEventListener('touchmove', this.preventGlobalScroll, { passive: false })
      }
    },

    // 恢复页面滚动
    enablePageScroll() {
      // #ifdef MP-WEIXIN || MP-ALIPAY || MP-BAIDU || MP-TOUTIAO || MP-QQ
      // 小程序环境下恢复页面滚动
      const pages = getCurrentPages()
      if (pages.length > 0) {
        const currentPage = pages[pages.length - 1]
        if (currentPage && currentPage.$page && currentPage.$page.meta) {
          currentPage.$page.meta.disableScroll = this.originalPageStyle || false
        }
      }
      // #endif

      // #ifdef H5
      // H5环境下恢复body和html样式
      if (typeof document !== 'undefined') {
        // 恢复原始样式
        document.body.style.overflow = this.originalBodyOverflow || ''
        document.body.style.position = this.originalBodyPosition || ''
        document.body.style.top = this.originalBodyTop || ''
        document.body.style.left = this.originalBodyLeft || ''
        document.body.style.right = this.originalBodyRight || ''
        document.body.style.width = ''
        document.documentElement.style.overflow = this.originalHtmlOverflow || ''

        // 恢复滚动位置
        if (this.scrollTop) {
          window.scrollTo(0, this.scrollTop)
        }
      }
      // #endif

      // 移除全局触摸事件监听器
      if (typeof document !== 'undefined') {
        document.removeEventListener('touchmove', this.preventGlobalScroll)
      }
    },
    
    // 点击遮罩关闭
    handleMaskClick() {
      this.close()
    },

    // 阻止背景滚动
    preventScroll(e) {
      // 阻止事件冒泡和默认行为，防止背景页面滚动
      e.preventDefault()
      e.stopPropagation()
      return false
    },

    // 全局阻止滚动事件
    preventGlobalScroll(e) {
      // 检查事件目标是否在弹窗内部
      const popup = document.querySelector('.store-selector-popup')
      const scrollArea = document.querySelector('.store-list')

      if (popup && scrollArea) {
        // 如果触摸事件发生在滚动区域内，允许滚动
        if (scrollArea.contains(e.target)) {
          return true
        }
      }

      // 否则阻止滚动
      e.preventDefault()
      e.stopPropagation()
      return false
    },
    
    // 获取完整地址
    getFullAddress(store) {
      return `${store.province}${store.city}${store.region}${store.detailAddress}`
    },

    // 格式化距离
    formatDistance(distance) {
      if (distance < 1) {
        return `${(distance * 1000).toFixed(0)}m`
      } else {
        return `${distance.toFixed(1)}km`
      }
    }
  }
}
</script>

<style scoped>
.store-selector-popup {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 9999;
  display: flex;
  align-items: flex-end;
  /* 阻止背景滚动的关键样式 */
  overflow: hidden;
  touch-action: none;
}

.popup-content {
  width: 100%;
  height: 80vh;
  background-color: #fff;
  border-radius: 16rpx 16rpx 0 0;
  display: flex;
  flex-direction: column;
  /* 恢复弹窗内部的触摸行为 */
  touch-action: auto;
  overflow: hidden;
}

.popup-header {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 32rpx 40rpx;
  position: relative;
  border-bottom: 1rpx solid #f5f5f5;
}

.header-title {
  font-size: 32rpx;
  font-weight: 400;
  color: #0A0D05;
  text-align: center;
  line-height: 1.4;
}

.close-btn {
  position: absolute;
  right: 40rpx;
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-icon-container {
  width: 24rpx;
  height: 24rpx;
  position: relative;
}

.close-line {
  position: absolute;
  width: 24rpx;
  height: 2rpx;
  background-color: #000000;
  top: 50%;
  left: 0;
}

.close-line-1 {
  transform: translateY(-50%) rotate(45deg);
}

.close-line-2 {
  transform: translateY(-50%) rotate(-45deg);
}

.store-list {
  flex: 1;
  padding: 0 40rpx 0rpx;
  height: 0; /* 确保 flex: 1 生效 */
  /* 确保滚动区域可以正常滚动 */
  touch-action: pan-y;
}

.school-group {
  margin-bottom: 0rpx;
}

.school-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 40rpx 0 32rpx;
  border-bottom: 1rpx solid #EEEEEE;
}

.school-logo-container {
  position: relative;
  width: 320rpx;
  height: 80rpx;
  cursor: pointer;
  transition: opacity 0.2s ease;
}

.school-logo-container:active {
  opacity: 0.7;
}

.selection-indicator {
  width: 48rpx;
  height: 48rpx;
  border-radius: 24rpx;
  background-color: transparent;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 4rpx solid #EEEEEE;
  transition: all 0.3s ease;
}

.selection-indicator.selected {
  background-color: #A9FF00;
  border-color: #A9FF00;
}

.indicator-dot {
  width: 24rpx;
  height: 24rpx;
  border-radius: 12rpx;
  background-color: #ffffff;
}

.school-logo {
  width: 240rpx;
  height: 80rpx;
  border-radius: 8rpx;
}

.school-logo-default {
  width: 240rpx;
  height: 80rpx;
  border-radius: 8rpx;
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  position: relative;
  overflow: hidden;
  padding: 0 20rpx;
  border: 1rpx solid #f0f0f0;
}

.logo-svg-container {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  width: 100%;
  height: 100%;
  position: relative;
}

.logo-circle {
  width: 48rpx;
  height: 48rpx;
  border: 2rpx solid #004EA2;
  border-radius: 24rpx;
  position: relative;
  margin-right: 16rpx;
  background: linear-gradient(135deg, #004EA2 0%, #0075C2 100%);
}

.logo-text {
  font-size: 24rpx;
  color: #004EA2;
  font-weight: 500;
  z-index: 1;
}

.stores-container {
  padding-top: 0;
}

.store-item {
  padding: 32rpx 0;
  border-bottom: 1rpx solid #EEEEEE;
  border-top: 1rpx solid #EEEEEE;
  margin-bottom: 2rpx;
}

.store-item:first-child {
  border-top: none;
}

.store-item:last-child {
  border-bottom: none;
}

/* 移除门店选中状态的视觉效果 */

.store-info {
  width: 100%;
}

.store-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16rpx;
}

.store-name {
  font-size: 24rpx;
  font-weight: 400;
  color: #000000;
  opacity: 0.9;
  line-height: 1.5;
}

.store-hours {
  display: flex;
  align-items: center;
  gap: 8rpx;
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



.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
}

.empty-text {
  font-size: 28rpx;
  color: #999999;
}

.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
}

.loading-text {
  font-size: 28rpx;
  color: #999999;
}


</style>
