<template>
  <view class="store-card" :data-store-id="store.id" @tap="onCardClick">
    <!-- 主要内容区域 -->
    <view class="store-main-content">
      <!-- 左侧内容容器 -->
      <view class="store-left-content">
        <!-- 门店基本信息 -->
        <view class="store-header">
          <view class="store-logo-container">
            <image
              :src="store.logo || defaultLogo"
              class="store-logo"
              mode="aspectFill"
            />
          </view>
          <view class="store-basic-info">
            <text class="store-name">{{ store.addressName }}</text>
            <view class="store-hours">
              <view class="hours-tag">营业时间</view>
              <text class="hours-text">{{ store.businessHours || '10:00-18:40' }}</text>
            </view>

            <!-- 门店相册展示区域 -->
            <view v-if="storeImages.length > 0" class="store-gallery">
              <scroll-view class="gallery-scroll" scroll-x="true" show-scrollbar="false">
                <view class="gallery-container">
                  <view
                    v-for="(image, index) in storeImages"
                    :key="index"
                    class="gallery-item"
                    @tap.stop="previewImage(index)"
                  >
                    <image
                      :src="image"
                      class="gallery-image"
                      mode="aspectFill"
                    />
                  </view>
                </view>
              </scroll-view>
            </view>
          </view>
        </view>

        <!-- 门店地址和联系方式 -->
        <view class="store-footer">
          <view class="store-address">
            <image
              class="location-icon"
              src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/address.png"
              mode="aspectFit"
            />
            <text class="address-text">{{ getFullAddress() }}</text>
          </view>
          <view v-if="store.phone" class="store-phone" @tap.stop="callStore">
            <image
              class="phone-icon"
              src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/images/store_phone.png"
              mode="aspectFit"
            />
            <text class="phone-text">{{ store.phone }}</text>
          </view>
        </view>
      </view>

      <!-- 右侧操作区域容器 -->
      <view class="store-right-content">
        <view class="store-actions-right">
          <text class="visit-text">去逛逛</text>
          <view class="visit-btn" @tap.stop="visitStore">
            <image
              class="visit-icon"
              src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/images/store_address.png"
              mode="aspectFit"
            />
          </view>
          <text class="distance-text" v-if="distance">距你{{ formatDistance(distance) }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'StoreCard',
  props: {
    store: {
      type: Object,
      required: true
    },
    distance: {
      type: Number,
      default: null
    }
  },
  data() {
    return {
      defaultLogo: 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/guanghengzou_logo.png'
    }
  },
  mounted() {
    console.log('StoreCard mounted - 门店信息:', {
      storeName: this.store.addressName,
      distance: this.distance,
      storeData: this.store
    });
  },
  computed: {
    // 门店相册图片
    storeImages() {
      // 优先使用storeGallery字段（后端返回的是逗号分隔的字符串）
      if (this.store.storeGallery && typeof this.store.storeGallery === 'string') {
        const images = this.store.storeGallery.split(',').filter(img => img.trim() !== '');
        return images;
      }

      // 兼容store_gallery数组字段
      if (this.store.store_gallery && Array.isArray(this.store.store_gallery)) {
        return this.store.store_gallery
      }

      // 兼容旧的images字段
      if (this.store.images && Array.isArray(this.store.images)) {
        return this.store.images
      }

      // 如果后端暂未返回相册字段，返回空数组
      // 开发测试：如果没有相册数据，返回示例图片用于测试UI效果
      if (process.env.NODE_ENV === 'development') {
        return [
          'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/store/store_image_1.jpg',
          'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/store/store_image_2.jpg',
          'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/store/store_image_3.jpg'
        ];
      }

      return []
    }
  },
  methods: {
    // 卡片点击事件
    onCardClick() {
      this.$emit('click', this.store)
    },

    // 访问门店
    visitStore() {
      // 发送事件给父组件
      this.$emit('visit', this.store)

      // 打开导航
      this.openNavigation()
    },

    // 打开导航
    openNavigation() {
      if (!this.store.latitude || !this.store.longitude) {
        uni.showToast({
          title: '门店位置信息不完整',
          icon: 'none'
        });
        return;
      }

      const latitude = parseFloat(this.store.latitude);
      const longitude = parseFloat(this.store.longitude);
      const name = this.store.addressName || '目标门店';
      const address = this.getFullAddress();

      // 检查是否为有效坐标
      if (isNaN(latitude) || isNaN(longitude)) {
        uni.showToast({
          title: '门店坐标信息无效',
          icon: 'none'
        });
        return;
      }

      // 检查位置权限
      uni.getSetting({
        success: (res) => {
          if (res.authSetting['scope.userLocation'] === false) {
            // 用户拒绝了位置权限，引导用户开启
            uni.showModal({
              title: '位置权限',
              content: '需要位置权限才能使用导航功能，请在设置中开启',
              confirmText: '去设置',
              success: (modalRes) => {
                if (modalRes.confirm) {
                  uni.openSetting();
                }
              }
            });
            return;
          }

          // 使用uni.openLocation打开地图导航
          this.doOpenLocation(latitude, longitude, name, address);
        },
        fail: () => {
          // 获取设置失败，直接尝试打开导航
          this.doOpenLocation(latitude, longitude, name, address);
        }
      });
    },

    // 执行打开地图导航
    doOpenLocation(latitude, longitude, name, address) {
      // 优先使用腾讯地图路线规划插件
      if (this.canUseRoutePlanPlugin()) {
        this.openTencentMapNavigation(latitude, longitude, name, address);
      } else {
        // 降级到系统导航
        console.log('当前环境不支持腾讯地图插件，使用系统导航');
        this.openSystemNavigation(latitude, longitude, name, address);
      }
    },

    // 打开系统默认导航
    openSystemNavigation(latitude, longitude, name, address) {
      uni.openLocation({
        latitude: latitude,
        longitude: longitude,
        name: name,
        address: address,
        scale: 16,
        success: () => {
          console.log('成功打开系统导航');
        },
        fail: (err) => {
          console.error('系统导航失败:', err);
          // 提供手动选择方案
          this.showNavigationOptions(latitude, longitude, name, address);
        }
      });
    },

    // 检查是否可以使用路线规划插件
    canUseRoutePlanPlugin() {
      // #ifdef MP-WEIXIN
      return true; // 在微信小程序中尝试使用腾讯地图插件
      // #endif

      // #ifndef MP-WEIXIN
      return false;
      // #endif
    },

    // 使用腾讯地图路线规划插件导航
    openTencentMapNavigation(latitude, longitude, name, address) {
      // #ifdef MP-WEIXIN
      try {
        const key = 'ZEOBZ-FKOCX-3PM4E-7WRYL-FEMZV-ULF5W'; // 腾讯地图key
        const referer = '广横走x鲤享文创'; // 和腾讯地图开放平台的应用名称一致

        // 先获取用户当前位置作为起点
        uni.getLocation({
          type: 'gcj02',
          success: (locationRes) => {
            this.doOpenTencentMapWithLocation(locationRes, latitude, longitude, name, address, key, referer);
          },
          fail: (locationErr) => {
            console.warn('获取当前位置失败，使用默认起点:', locationErr);
            // 如果获取位置失败，使用默认位置或仅传终点
            this.doOpenTencentMapWithLocation(null, latitude, longitude, name, address, key, referer);
          }
        });
      } catch (e) {
        console.error('腾讯地图插件调用异常:', e);
        uni.showToast({
          title: '腾讯地图插件调用异常',
          icon: 'none',
          duration: 2000
        });
      }
      // #endif
    },

    // 执行腾讯地图导航（带位置信息）
    doOpenTencentMapWithLocation(currentLocation, latitude, longitude, name, address, key, referer) {
      try {
        // 构建终点信息对象
        const endPointData = {
          name: name,
          latitude: parseFloat(latitude),
          longitude: parseFloat(longitude)
        };

        // 构建起点信息对象（如果有当前位置）
        let startPointData = null;
        if (currentLocation) {
          startPointData = {
            name: '我的位置',
            latitude: currentLocation.latitude,
            longitude: currentLocation.longitude
          };
        }

        // 转换为JSON字符串，并处理URL中的特殊字符
        const endPointJson = JSON.stringify(endPointData);
        // 只对可能破坏URL的字符进行替换，而不是完整URL编码
        const safeEndPointJson = endPointJson.replace(/&/g, '%26').replace(/#/g, '%23');

        // 使用官方文档推荐的参数配置，现在跳转权限已配置，可以正常使用
        let pluginUrl = `plugin://routePlan/index?key=${key}&referer=${encodeURIComponent(referer)}&endPoint=${safeEndPointJson}&navigation=1`;

        // 如果有起点信息，添加到URL中
        if (startPointData) {
          const startPointJson = JSON.stringify(startPointData);
          const safeStartPointJson = startPointJson.replace(/&/g, '%26').replace(/#/g, '%23');
          pluginUrl += `&startPoint=${safeStartPointJson}`;
        }

        console.log('腾讯地图导航参数:', {
          起点数据: startPointData,
          终点数据: endPointData,
          完整URL: pluginUrl
        });

        uni.navigateTo({
          url: pluginUrl,
          success: (res) => {
            console.log('成功打开腾讯地图路线规划:', res);
          },
          fail: (error) => {
            console.error('腾讯地图路线规划失败:', error);

            // 如果是跳转权限问题（实物交易管控），降级到系统导航
            if (error.errMsg && error.errMsg.includes('banded')) {
              console.log('检测到实物交易小程序跳转管控，降级到系统导航');
              uni.showToast({
                title: '正在为您打开系统导航',
                icon: 'none',
                duration: 1500
              });
              setTimeout(() => {
                this.openSystemNavigation(latitude, longitude, name, address);
              }, 500);
            } else {
              uni.showToast({
                title: '腾讯地图插件调用失败',
                icon: 'none',
                duration: 2000
              });
            }
          }
        });
      } catch (e) {
        console.error('构建腾讯地图URL异常:', e);
        uni.showToast({
          title: '导航参数构建失败',
          icon: 'none',
          duration: 2000
        });
      }
    },

    // 显示导航选项（备用方案）
    showNavigationOptions(latitude, longitude, name, address) {
      const actionSheetItems = [
        '腾讯地图导航',
        '打开地图页面',
        '复制门店信息',
        '复制地址',
        '复制坐标',
        '重试导航'
      ];

      uni.showActionSheet({
        itemList: actionSheetItems,
        success: (res) => {
          switch (res.tapIndex) {
            case 0:
              // 腾讯地图导航
              this.openTencentMapNavigation(latitude, longitude, name, address);
              break;
            case 1:
              // 打开地图页面
              this.openMapPage(latitude, longitude, name, address);
              break;
            case 2:
              // 复制门店信息
              const storeInfo = `${name}\n地址：${address}\n坐标：${latitude},${longitude}`;
              uni.setClipboardData({
                data: storeInfo,
                success: () => {
                  uni.showToast({
                    title: '门店信息已复制',
                    icon: 'success'
                  });
                }
              });
              break;
            case 3:
              // 复制地址
              uni.setClipboardData({
                data: address,
                success: () => {
                  uni.showToast({
                    title: '地址已复制',
                    icon: 'success'
                  });
                }
              });
              break;
            case 4:
              // 复制坐标
              const coordinates = `${latitude},${longitude}`;
              uni.setClipboardData({
                data: coordinates,
                success: () => {
                  uni.showToast({
                    title: '坐标已复制',
                    icon: 'success'
                  });
                }
              });
              break;
            case 5:
              // 重试导航
              this.doOpenLocation(latitude, longitude, name, address);
              break;
          }
        },
        fail: () => {
          uni.showToast({
            title: '操作取消',
            icon: 'none'
          });
        }
      });
    },

    // 打开地图页面（使用内置地图组件）
    openMapPage(latitude, longitude, name, address) {
      // 可以跳转到一个专门的地图页面，或者使用chooseLocation
      uni.chooseLocation({
        latitude: parseFloat(latitude),
        longitude: parseFloat(longitude),
        success: (res) => {
          console.log('用户选择的位置:', res);
          // 可以根据用户选择的位置进行后续操作
        },
        fail: (err) => {
          console.error('打开地图选择失败:', err);
          uni.showToast({
            title: '无法打开地图选择',
            icon: 'none'
          });
        }
      });
    },

    // 使用外部地图应用导航
    openExternalMapApp(latitude, longitude, name, address) {
      const mapApps = [
        {
          name: '腾讯地图',
          url: `qqmap://map/routeplan?type=drive&to=${name}&tocoord=${latitude},${longitude}`
        },
        {
          name: '高德地图',
          url: `amapuri://route/plan/?dlat=${latitude}&dlon=${longitude}&dname=${name}&dev=0&t=0`
        },
        {
          name: '百度地图',
          url: `baidumap://map/direction?destination=latlng:${latitude},${longitude}|name:${name}&mode=driving`
        }
      ];

      uni.showActionSheet({
        itemList: mapApps.map(app => app.name),
        success: (res) => {
          const selectedApp = mapApps[res.tapIndex];
          // 注意：小程序中直接调用外部URL scheme可能受限
          // 这里仅作为示例，实际使用时需要根据平台限制调整
          console.log('尝试打开:', selectedApp.url);
          uni.showToast({
            title: '请在手机中安装对应地图应用',
            icon: 'none',
            duration: 3000
          });
        }
      });
    },

    // 拨打电话
    callStore() {
      if (this.store.phone) {
        uni.makePhoneCall({
          phoneNumber: this.store.phone,
          success: () => {
            console.log('拨打电话成功')
          },
          fail: (err) => {
            console.error('拨打电话失败:', err)
            uni.showToast({
              title: '拨打电话失败',
              icon: 'none'
            })
          }
        })
      } else {
        uni.showToast({
          title: '暂无联系电话',
          icon: 'none'
        })
      }
    },

    // 预览图片
    previewImage(index) {
      uni.previewImage({
        current: index,
        urls: this.storeImages,
        success: () => {
          console.log('预览图片成功')
        },
        fail: (err) => {
          console.error('预览图片失败:', err)
        }
      })
    },

    // 获取完整地址
    getFullAddress() {
      const { province = '', city = '', region = '', address = '', detailAddress = '' } = this.store
      return `${province}${city}${region}${address || detailAddress}`
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
.store-card {
  background-image: url('https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/images/store_background.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  margin: 0 32rpx 16rpx;
  border-radius: 16rpx;
  padding: 32rpx;
  position: relative;
  overflow: hidden;
}

/* 主要内容区域 */
.store-main-content {
  display: flex;
  align-items: stretch;
  gap: 30rpx;
  height: 100%;
}

/* 左侧内容容器 */
.store-left-content {
  width: 80%;
  flex: 0 0 80%;
  display: flex;
  flex-direction: column;
}

/* 右侧内容容器 */
.store-right-content {
  width: 20%;
  flex: 0 0 20%;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 门店头部 */
.store-header {
  display: flex;
  align-items: stretch; /* 让两边高度相同 */
  margin-bottom: 24rpx;
  min-height: 188rpx; /* 与logo高度一致 */
  gap: 24rpx; /* 增加间距 */
}

.store-logo-container {
  width: 144rpx;
  height: 100%;
  flex-shrink: 0;
  border-radius: 12rpx; /* 添加圆角 */
  overflow: hidden;
  position: relative;
}

.store-logo {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border-radius: 12rpx;
}

.store-basic-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  height: 100%; /* 占满父容器高度 */
  min-height: 188rpx; /* 与logo高度一致 */
}

.store-name {
  font-size: 32rpx; /* 增大字体 */
  font-weight: 600; /* 加粗 */
  color: #000000;
  line-height: 1.3;
  margin-bottom: 20rpx; /* 增加底部间距 */
  opacity: 0.9;
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

.store-actions-right {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 20rpx;
  width: 100%;
  height: 100%;
}

.visit-text {
  font-size: 24rpx;
  color: #000000;
  font-weight: 500;
  line-height: 1.2;
  text-align: center;
}

.visit-btn {
  width: 80rpx;
  height: 80rpx;
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(169, 255, 0, 0.1);
}

.visit-icon {
  width: 80rpx;
  height: 80rpx;
}

.distance-text {
  font-size: 20rpx;
  color: #9FA19D;
  line-height: 1.2;
  text-align: center;
}

/* 门店相册 */
.store-gallery {
  margin-top: 20rpx;
  width: 100%;
  flex: 1; /* 让相册区域占据剩余空间 */
}

.gallery-scroll {
  width: 100%;
  white-space: nowrap;
}

.gallery-container {
  display: flex;
  gap: 8rpx;
  padding: 0 4rpx;
}

.gallery-item {
  width: 80rpx;
  height: 80rpx;
  flex-shrink: 0;
  border-radius: 8rpx;
  overflow: hidden;
  border: 1rpx solid rgba(0, 0, 0, 0.08);
}

.gallery-image {
  width: 100%;
  height: 100%;
}

/* 门店底部 */
.store-footer {
  margin-top: 24rpx;
}

.store-address {
  display: flex;
  align-items: center;
  margin-bottom: 24rpx;
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

.store-phone {
  display: flex;
  align-items: center;
  margin-top: 12rpx;
  cursor: pointer;
}

.phone-icon {
  width: 24rpx;
  height: 24rpx;
  margin-right: 8rpx;
}

.phone-text {
  font-size: 24rpx;
  color: #9FA19D;
  line-height: 1.4;
}




</style>
