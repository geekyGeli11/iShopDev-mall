<template>
  <view class="search-bar-container">
    <view class="search-input-wrapper">
      <view class="search-icon">
        <image 
          class="icon-image" 
          src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/search.png" 
          mode="aspectFit"
        />
      </view>
      <input
        class="search-input"
        type="text"
        :value="searchValue"
        placeholder="搜索门店"
        placeholder-class="search-placeholder"
        @input="onInput"
        @focus="onFocus"
        @blur="onBlur"
        @confirm="onConfirm"
      />
      <view v-if="searchValue" class="clear-btn" @tap="clearSearch">
        <view class="clear-icon">×</view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'SearchBar',
  props: {
    value: {
      type: String,
      default: ''
    },
    placeholder: {
      type: String,
      default: '搜索门店'
    },
    transparent: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      searchValue: '',
      isFocused: false,
      debounceTimer: null
    }
  },
  watch: {
    value: {
      handler(newVal) {
        this.searchValue = newVal
      },
      immediate: true
    }
  },
  methods: {
    // 输入事件
    onInput(e) {
      const value = e.detail.value
      this.searchValue = value
      
      // 防抖处理
      if (this.debounceTimer) {
        clearTimeout(this.debounceTimer)
      }
      
      this.debounceTimer = setTimeout(() => {
        this.$emit('input', value)
        this.$emit('search', value)
      }, 300)
    },

    // 获得焦点
    onFocus() {
      this.isFocused = true
      this.$emit('focus')
    },

    // 失去焦点
    onBlur() {
      this.isFocused = false
      this.$emit('blur')
    },

    // 确认搜索
    onConfirm(e) {
      const value = e.detail.value
      this.$emit('confirm', value)
      this.$emit('search', value)
    },

    // 清空搜索
    clearSearch() {
      this.searchValue = ''
      this.$emit('input', '')
      this.$emit('search', '')
      this.$emit('clear')
    }
  },
  beforeDestroy() {
    if (this.debounceTimer) {
      clearTimeout(this.debounceTimer)
    }
  }
}
</script>

<style scoped>
.search-bar-container {
  padding: 0 32rpx 0;
  background-color: transparent;
}

.search-input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  background-color: #ffffff;
  border: 2rpx solid #EEEEEE;
  border-radius: 20rpx;
  padding: 0 32rpx;
  height: 72rpx;
  box-shadow: 0px 1px 3px rgba(0, 0, 0, 0.05);
}

.search-icon {
  width: 32rpx;
  height: 32rpx;
  margin-right: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-image {
  width: 28rpx;
  height: 28rpx;
}

.search-input {
  flex: 1;
  font-size: 28rpx;
  color: #000000;
  line-height: 1.4;
  background-color: transparent;
}

.search-placeholder {
  color: #9FA19D;
  font-size: 28rpx;
}

.clear-btn {
  width: 40rpx;
  height: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: 16rpx;
}

.clear-icon {
  font-size: 32rpx;
  color: #9FA19D;
  line-height: 1;
}
</style>
