<template>
  <view class="content">
    <view class="address-form">
      <!-- 姓名输入 -->
      <view class="form-item">
        <text class="label">姓名*</text>
        <input 
          class="input" 
          type="text" 
          placeholder="请输入收货人姓名" 
          placeholder-class="placeholder" 
          v-model="addressData.name"
        />
        <view class="bottom-line"></view>
      </view>
      
      <!-- 手机号输入 -->
      <view class="form-item">
        <text class="label">手机号*</text>
        <input 
          class="input" 
          type="number" 
          placeholder="请输入手机号" 
          placeholder-class="placeholder" 
          v-model="addressData.phoneNumber"
        />
        <view class="bottom-line"></view>
      </view>
      
      <!-- 地区选择 -->
      <view class="form-item">
        <text class="label">地区*</text>
        <picker 
          mode="region" 
          :value="[addressData.province, addressData.city, addressData.region]"
          @change="handleRegionChange"
        >
          <view class="picker-view">
            <text v-if="addressData.province" class="input">
              {{ addressData.province + " " + addressData.city + " " + addressData.region }}
            </text>
            <text v-else class="input placeholder">请选择地区</text>
          </view>
        </picker>
        <image 
          class="arrow-icon" 
          src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/address/location_arrow.png"
        ></image>
        <view class="bottom-line"></view>
      </view>
      
      <!-- 详细地址输入 -->
      <view class="form-item">
        <text class="label">地址*</text>
        <input 
          class="input" 
          type="text" 
          placeholder="请输入详细地址" 
          placeholder-class="placeholder" 
          v-model="addressData.detailAddress"
        />
        <view class="bottom-line"></view>
      </view>
      
      <!-- 设为默认地址选项 -->
      <view class="default-row">
        <text class="default-text">设为默认地址</text>
        <view 
          class="checkbox" 
          :class="{ 'checked': addressData.defaultStatus == 1 }"
          @tap="toggleDefault"
        >
          <view v-if="addressData.defaultStatus == 1" class="check-mark"></view>
        </view>
      </view>
    </view>
    
    <!-- 按钮组 -->
    <view class="btn-group">
      <button class="confirm-btn" @tap="confirm">确认</button>
      <button class="cancel-btn" @tap="cancel">取消</button>
    </view>
  </view>
</template>

<script>
import { fetchAddressDetail, addAddress, updateAddress } from '@/api/address.js';

export default {
  data() {
    return {
      addressData: {
        id: null,
        name: '',
        phoneNumber: '',
        province: '',
        city: '',
        region: '',
        detailAddress: '',
        defaultStatus: 0
      },
      type: 'add', // add: 新增, edit: 编辑
      addressId: null,
      fromGift: false, // 是否从礼物确认页面跳转过来
      giftRecordId: null, // 礼物记录ID
    };
  },
  
  onLoad(options) {
    // 获取页面类型和地址ID
    if (options.type) {
      this.type = options.type;
    }
    
    if (options.id) {
      this.addressId = options.id;
      this.getAddressDetail();
    }
    
    // 检查是否从礼物确认页跳转过来
    if (options.fromGift === '1') {
      this.fromGift = true;
      if (options.giftRecordId) {
        this.giftRecordId = options.giftRecordId;
      }
    }
    
    // 检查是否有微信地址
    if (options.fromWx === '1') {
      const wxAddress = uni.getStorageSync('wxAddress');
      if (wxAddress) {
        this.addressData = wxAddress;
        uni.removeStorageSync('wxAddress');
      }
    }
    
    // 设置导航栏标题
    uni.setNavigationBarTitle({
      title: this.type === 'add' ? '新增地址' : '编辑地址'
    });
  },
  
  methods: {
    // 获取地址详情
    async getAddressDetail() {
      if (this.type !== 'edit' || !this.addressId) return;
      
      uni.showLoading({
        title: '加载中...'
      });
      
      try {
        const result = await fetchAddressDetail(this.addressId);
        
        if (result && result.data) {
          this.addressData = result.data;
        } else {
          throw new Error('获取地址详情失败');
        }
      } catch (error) {
        console.error('获取地址详情失败', error);
        uni.showToast({
          title: '获取详情失败',
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
      }
    },
    
    // 地区选择器变化
    handleRegionChange(e) {
      const [province, city, region] = e.detail.value;
      this.addressData.province = province;
      this.addressData.city = city;
      this.addressData.region = region;
    },
    
    // 切换默认地址
    toggleDefault() {
      this.addressData.defaultStatus = this.addressData.defaultStatus === 1 ? 0 : 1;
    },
    
    // 验证表单
    validate() {
      if (!this.addressData.name) {
        uni.showToast({
          title: '请输入收货人姓名',
          icon: 'none'
        });
        return false;
      }
      
      if (!this.addressData.phoneNumber) {
        uni.showToast({
          title: '请输入手机号',
          icon: 'none'
        });
        return false;
      }
      
      if (!/^1\d{10}$/.test(this.addressData.phoneNumber)) {
        uni.showToast({
          title: '手机号格式不正确',
          icon: 'none'
        });
        return false;
      }
      
      if (!this.addressData.province || !this.addressData.city || !this.addressData.region) {
        uni.showToast({
          title: '请选择地区',
          icon: 'none'
        });
        return false;
      }
      
      if (!this.addressData.detailAddress) {
        uni.showToast({
          title: '请输入详细地址',
          icon: 'none'
        });
        return false;
      }
      
      return true;
    },
    
    // 确认保存
    async confirm() {
      if (!this.validate()) return;
      
      uni.showLoading({
        title: '保存中...'
      });
      
      try {
        let result;
        
        if (this.type === 'add') {
          // 新增地址
          result = await addAddress(this.addressData);
        } else {
          // 编辑地址
          result = await updateAddress(this.addressData);
        }
        
        if (result && result.code === 200) {
          // 保存成功后获取新地址ID (如果是新增地址)
          if (this.type === 'add' && result.data) {
            this.addressData.id = result.data.addressId;
          }
          
          uni.showToast({
            title: '保存成功',
            icon: 'success',
            duration: 1500,
            success: () => {
              setTimeout(() => {
                // 获取页面栈，回传地址信息给调用页面
                const pages = getCurrentPages();
                const prevPage = pages[pages.length - 2];
                
                if (prevPage && prevPage.$vm) {
                  // 如果是从礼物确认页面跳转过来
                  if (this.fromGift && this.giftRecordId) {
                    prevPage.$vm.addressInfo = this.addressData;
                  }
                  // 如果是从商品详情页或下单页跳转过来，且新增/编辑的是默认地址
                  else if (this.addressData.defaultStatus === 1) {
                    prevPage.$vm.currentAddress = this.addressData;
                    // 如果上一页有重新计算金额的方法，调用它
                    if (typeof prevPage.$vm.calculateAmount === 'function') {
                      prevPage.$vm.calculateAmount();
                    }
                  }
                }
                
                uni.navigateBack();
              }, 1500);
            }
          });
        } else {
          throw new Error('保存地址失败');
        }
      } catch (error) {
        console.error('保存地址失败', error);
        uni.showToast({
          title: '保存失败',
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
      }
    },
    
    // 为其他页面调用的保存地址方法
    async saveAddress(addressData) {
      if (!addressData) return Promise.reject(new Error('地址数据为空'));
      
      try {
        const result = await addAddress(addressData);
        
        if (result && result.code === 200) {
          return Promise.resolve(result);
        } else {
          return Promise.reject(new Error('保存地址失败'));
        }
      } catch (error) {
        return Promise.reject(error);
      }
    },
    
    // 取消
    cancel() {
      uni.navigateBack();
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
  background: #FFFFFF;
}

.content {
  padding: 0 15px;
}

.address-form {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-item {
  position: relative;
  padding-bottom: 10px;
  
  .label {
    font-family: "Source Han Sans SC";
    font-weight: 500;
    font-size: 32rpx;
    color: #000000;
    display: block;
    margin-bottom: 10px;
  }
  
  .input {
    font-family: "Source Han Sans SC";
    font-weight: 400;
    font-size: 24rpx;
    color: #000000;
    width: 100%;
    padding: 4px 0;
  }
  
  .placeholder {
    color: #808080;
  }
  
  .bottom-line {
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 1px;
    background-color: #999999;
    opacity: 0.5;
  }
  
  .arrow-icon {
    position: absolute;
    right: 0;
    bottom: 30rpx;
    width: 32rpx;
    height: 32rpx;
  }
}

.picker-view {
  width: 100%;
}

.default-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
  
  .default-text {
    font-family: "Source Han Sans CN";
    font-weight: 400;
    font-size: 24rpx;
    color: #000000;
  }
  
  .checkbox {
    width: 34rpx;
    height: 34rpx;
    border-radius: 50%;
    border: 0.5px solid #000000;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #FFFFFF;
    transition: all 0.2s ease;
    
    &.checked {
      background-color: #A9FF00;
      border-color: #A9FF00;
    }
    
    .check-mark {
      width: 10rpx;
      height: 16rpx;
      border: solid #000000;
      border-width: 0 1px 1px 0;
      transform: rotate(45deg);
    }
  }
}

.btn-group {
  margin-top: 60px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.confirm-btn {
  width: 100%;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 40rpx;
  background: #000000;
  color: $base-color;
  font-family: "PingFang SC";
  font-weight: 500;
  font-size: 28rpx;
  border: none;
}

.cancel-btn {
  width: 100%;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 40rpx;
  background: #FFFFFF;
  color: #000000;
  font-family: "PingFang SC";
  font-weight: 500;
  font-size: 28rpx;
  border: 1px solid #000000;
}
</style> 