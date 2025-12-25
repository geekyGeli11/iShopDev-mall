<template>
  <view class="content">
    <!-- 微信获取地址按钮 -->
    <view class="wx-address" @tap="getWXAddress">
      <text>获取微信收货地址</text>
      <text class="arrow">〉</text>
    </view>
    
    <!-- 地址列表 -->
    <view class="address-list">
      <view 
        v-for="(item, index) in addressList" 
        :key="index"
        class="list-item"
        @tap="checkAddress(item)"
      >
        <view class="wrapper">
          <view class="name-phone">
            <text class="name">{{ item.name }}</text>
            <text class="phone">{{ item.phoneNumber }}</text>
            <text v-if="item.defaultStatus == 1" class="tag">默认</text>
          </view>
          <view class="address-box">
            <text class="address">{{ item.province + " " + item.city + " " + item.region + " " + item.detailAddress }}</text>
          </view>
          <view class="action-box">
            <view class="default-box">
              <view 
                class="checkbox" 
                :class="{ 'checked': item.defaultStatus == 1 }"
                @tap.stop="setDefault(item)"
              >
                <image 
                  v-if="item.defaultStatus == 1"
                  class="check-icon" 
                  src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/address/checkbox_checked.svg"
                ></image>
              </view>
              <text>设为默认地址</text>
            </view>
            <view class="operation-btns">
              <text class="edit-btn" @tap.stop="addAddress('edit', item)">编辑</text>
              <text class="delete-btn" @tap.stop="handleDeleteAddress(item.id)">删除</text>
            </view>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 新增地址按钮 -->
    <button class="add-btn" @tap="addAddress('add')">新增地址</button>
  </view>
</template>

<script>
import { fetchAddressList, deleteAddress, updateAddress, addAddress } from '@/api/address.js';

export default {
  data() {
    return {
      addressList: [],
      source: 0 // 0: 默认进入, 1: 从下单页进入
    };
  },
  
  onLoad(options) {
    if (options.source) {
      this.source = parseInt(options.source);
    }
  },
  
  onShow() {
    this.getAddressList();
  },
  
  onUnload() {
    // 页面卸载时，如果是从其他页面进入且用户没有选择具体地址，回传当前默认地址
    if (this.source === 1) {
      const pages = getCurrentPages();
      const prevPage = pages[pages.length - 2]; // 上一个页面
      
      if (prevPage && prevPage.$vm) {
        // 只有在上一页还没有地址时才设置默认地址
        // 避免覆盖用户通过checkAddress选择的地址
        if (!prevPage.$vm.currentAddress || !prevPage.$vm.currentAddress.name) {
          // 找到当前默认地址
          const defaultAddress = this.addressList.find(item => item.defaultStatus === 1);
          const currentAddress = defaultAddress || (this.addressList.length > 0 ? this.addressList[0] : null);
          
          if (currentAddress) {
            prevPage.$vm.currentAddress = currentAddress;
            // 如果上一页有重新计算金额的方法，调用它
            if (typeof prevPage.$vm.calculateAmount === 'function') {
              prevPage.$vm.calculateAmount();
            }
          }
        }
      }
    }
  },
  
  methods: {
    // 获取地址列表
    async getAddressList() {
      uni.showLoading({
        title: '加载中...'
      });
      
      try {
        const result = await fetchAddressList();
        if (result && result.data) {
          this.addressList = result.data;
        } else {
          throw new Error('获取地址列表失败');
        }
      } catch (error) {
        console.error('获取地址列表失败', error);
        uni.showToast({
          title: '获取地址列表失败',
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
      }
    },
    
    // 获取微信地址
    getWXAddress() {
      uni.chooseAddress({
        success: (res) => {
          // 将微信地址转换为应用地址格式
          const wxAddress = {
            name: res.userName,
            phoneNumber: res.telNumber,
            province: res.provinceName,
            city: res.cityName,
            region: res.countyName,
            detailAddress: res.detailInfo,
            defaultStatus: 0
          };
          
          // 直接调用添加地址API
          this.saveWxAddress(wxAddress);
        },
        fail: (err) => {
          console.error('获取微信地址失败', err);
          if (err.errMsg !== 'chooseAddress:fail cancel') {
            uni.showToast({
              title: '获取地址失败',
              icon: 'none'
            });
          }
        }
      });
    },
    
    // 保存微信获取的地址
    async saveWxAddress(address) {
      uni.showLoading({
        title: '保存中...'
      });
      
      try {
        // 直接调用addAddress接口保存微信地址
        const result = await addAddress(address);
        
        if (result && result.code === 200) {
          // 保存成功后重新获取地址列表
          this.getAddressList();
          
          uni.showToast({
            title: '添加成功',
            icon: 'success'
          });
        } else {
          throw new Error('保存地址失败');
        }
      } catch (error) {
        console.error('保存微信地址失败', error);
        uni.showToast({
          title: '保存地址失败',
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
      }
    },
    
    // 设置默认地址
    async setDefault(item) {
      if (item.defaultStatus === 1) return;
      
      uni.showLoading({
        title: '设置中...'
      });
      
      try {
        // 更新地址
        const updatedAddress = {
          ...item,
          defaultStatus: 1
        };
        
        const result = await updateAddress(updatedAddress);
        
        if (result && result.code === 200) {
          // 更新本地列表
          this.addressList.forEach(address => {
            address.defaultStatus = address.id === item.id ? 1 : 0;
          });
          
          uni.showToast({
            title: '设置成功'
          });
        } else {
          throw new Error('设置默认地址失败');
        }
      } catch (error) {
        console.error('设置默认地址失败', error);
        uni.showToast({
          title: '设置失败',
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
      }
    },
    
    // 选择地址（从其他页面进入）
    checkAddress(item) {
      if (this.source === 1) {
        // 回传选择的地址
        const pages = getCurrentPages();
        const prevPage = pages[pages.length - 2];
        
        // 更新上一页的地址
        if (prevPage && prevPage.$vm) {
          prevPage.$vm.currentAddress = item;
          // 如果上一页有重新计算金额的方法，调用它（下单页面）
          if (typeof prevPage.$vm.calculateAmount === 'function') {
            prevPage.$vm.calculateAmount();
          }
          // 如果是商品详情页，可能需要触发其他更新逻辑
          console.log('已回传选择的地址:', item);
        }
        
        uni.navigateBack();
      }
    },
    
    // 新增/编辑地址
    addAddress(type, item) {
      const url = `/pages/address/addressManage?type=${type}` + (item ? `&id=${item.id}` : '');
      uni.navigateTo({ url });
    },
    
    // 删除地址
    handleDeleteAddress(id) {
      uni.showModal({
        title: '提示',
        content: '确定要删除该地址吗？',
        success: async (res) => {
          if (res.confirm) {
            uni.showLoading({
              title: '删除中...'
            });
            
            try {
              const result = await deleteAddress(id);
              
              if (result && result.code === 200) {
                // 从列表中移除
                const index = this.addressList.findIndex(item => item.id === id);
                if (index !== -1) {
                  this.addressList.splice(index, 1);
                }
                
                uni.showToast({
                  title: '删除成功'
                });
              } else {
                throw new Error('删除地址失败');
              }
            } catch (error) {
              console.error('删除地址失败', error);
              uni.showToast({
                title: '删除失败',
                icon: 'none'
              });
            } finally {
              uni.hideLoading();
            }
          }
        }
      });
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
page {
  padding-bottom: 120rpx;
  background-color: #FFFFFF;
}

.content {
  position: relative;
  width: 100%;
}

.wx-address {
  margin: 30rpx;
  padding: 20rpx 0;
  font-size: 24rpx;
  color: #000000;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #EEEEEE;
  
  .arrow {
    color: #CCCCCC;
    font-size: 28rpx;
  }
}

.address-list {
  padding: 0 20rpx 20rpx;
  margin-bottom: 50rpx;
}

.list-item {
  background: #FFFFFF;
  border-radius: 20rpx;
  margin-bottom: 30rpx;
  border: 0.5px solid #999999;
  overflow: hidden;
}

.wrapper {
  padding: 30rpx;
}

.name-phone {
  display: flex;
  align-items: center;
  margin-bottom: 10rpx;
  position: relative;
  
  .name {
    font-size: 24rpx;
    font-weight: 400;
    margin-right: 20rpx;
  }
  
  .phone {
    font-size: 24rpx;
    font-weight: 400;
    color: #000000;
  }
  
  .tag {
    min-width: 44rpx;
    height: 46rpx;
    line-height: 46rpx;
    text-align: center;
    background: rgba($color: #647D00, $alpha: 0.1);
    border-radius: 20rpx;
    font-size: 20rpx;
    font-weight: 500;
    color: #647D00;
    padding: 0 10rpx;
    position: absolute;
    right: 0;
  }
}

.address-box {
  margin-bottom: 20rpx;
  display: flex;
  align-items: flex-start;
  
  .address {
    font-size: 24rpx;
    color: #000000;
    line-height: 1.5;
  }
}

.action-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 0.5px solid #999999;
  padding-top: 20rpx;
  
  .default-box {
    display: flex;
    align-items: center;
    
    .checkbox {
      width: 34rpx;
      height: 34rpx;
      border-radius: 50%;
      border: 0.5px solid #000000;
      margin-right: 10rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      
      &.checked {
        background-color: #A9FF00;
      }
      
      .check-icon {
        width: 20rpx;
        height: 20rpx;
      }
    }
    
    text {
      font-size: 24rpx;
      color: #000000;
    }
  }
  
  .operation-btns {
    display: flex;
    align-items: center;
    
    .edit-btn, .delete-btn {
      font-size: 24rpx;
      color: rgba(0, 0, 0, 0.6);
      margin-left: 30rpx;
    }
  }
}

.add-btn {
  position: fixed;
  left: 30rpx;
  right: 30rpx;
  bottom: 50rpx;
  z-index: 95;
  width: 690rpx;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 40rpx;
  color: $base-color;
  font-size: 28rpx;
  font-weight: 500;
  background: #000000;
  margin: 0;
  padding: 0;
}
</style> 