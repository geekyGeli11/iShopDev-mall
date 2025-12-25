<template>
  <div class="fullscreen-dashboard" :class="{ 'is-fullscreen': isFullscreen }">
    <!-- 全屏容器 -->
    <div ref="fullscreenContainer" class="fullscreen-container">
      <!-- 顶部标题栏 -->
      <div class="dashboard-header">
        <div class="header-decoration-left"></div>
        <div class="header-title">
          <span class="title-text">数据看板大屏</span>
        </div>
        <div class="header-decoration-right"></div>
        
        <!-- 退出全屏按钮 -->
        <div class="exit-fullscreen-btn" @click="exitFullscreen">
          <i class="el-icon-close"></i>
        </div>
        
        <!-- 时间显示 -->
        <div class="current-time">{{ currentTime }}</div>
      </div>
      
      <!-- 主内容区 -->
      <div class="dashboard-content">
        <!-- 左侧：营业数据 -->
        <div class="left-panel">
          <div class="panel-box">
            <div class="panel-title">
              <span>营业数据</span>
            </div>
            <div class="panel-content">
              <business-fullscreen-view :data="businessData" />
            </div>
          </div>
        </div>
        
        <!-- 中间：核心指标 -->
        <div class="center-panel">
          <!-- 顶部核心指标卡片 -->
          <div class="core-metrics">
            <div class="metric-card">
              <div class="metric-label">总收入</div>
              <div class="metric-value">
                <count-to
                  :start-val="0"
                  :end-val="parseFloat((businessData && businessData.totalRevenue) || 0)"
                  :duration="2000"
                  :decimals="2"
                  prefix="¥"
                  separator=","
                  class="metric-number"
                />
              </div>
            </div>
            
            <div class="metric-card">
              <div class="metric-label">新增会员</div>
              <div class="metric-value">
                <count-to
                  :start-val="0"
                  :end-val="(memberData && memberData.newMemberCount) || 0"
                  :duration="2000"
                  separator=","
                  class="metric-number"
                />
              </div>
            </div>
            
            <div class="metric-card">
              <div class="metric-label">总利润</div>
              <div class="metric-value">
                <count-to
                  :start-val="0"
                  :end-val="parseFloat((productData && productData.profitSummary && productData.profitSummary.totalProfit) || 0)"
                  :duration="2000"
                  :decimals="2"
                  prefix="¥"
                  separator=","
                  class="metric-number"
                />
              </div>
            </div>
          </div>
          
          <!-- 中间图表区域 -->
          <div class="center-chart-box">
            <div class="panel-title">
              <span>{{ centerChartTitle }}</span>
            </div>
            <div class="center-chart-content">
              <component :is="currentCenterComponent" :data="currentCenterData" />
            </div>
          </div>
        </div>
        
        <!-- 右侧：会员和商品数据 -->
        <div class="right-panel">
          <div class="panel-box">
            <div class="panel-title">
              <span>{{ rightPanelTitle }}</span>
            </div>
            <div class="panel-content">
              <component :is="currentRightComponent" :data="currentRightData" />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex'
import CountTo from 'vue-count-to'
import BusinessFullscreenView from './BusinessFullscreenView.vue'
import MemberFullscreenView from './MemberFullscreenView.vue'
import ProductFullscreenView from './ProductFullscreenView.vue'

export default {
  name: 'FullscreenDashboard',
  
  components: {
    CountTo,
    BusinessFullscreenView,
    MemberFullscreenView,
    ProductFullscreenView
  },
  
  data() {
    return {
      isFullscreen: false,
      currentTime: '',
      timeInterval: null,
      autoRefreshInterval: null,
      autoCarouselInterval: null,
      currentCarouselIndex: 0,
      carouselComponents: [
        { center: 'BusinessFullscreenView', right: 'MemberFullscreenView', centerTitle: '收入趋势', rightTitle: '会员数据' },
        { center: 'MemberFullscreenView', right: 'ProductFullscreenView', centerTitle: '会员增长', rightTitle: '商品销售' },
        { center: 'ProductFullscreenView', right: 'BusinessFullscreenView', centerTitle: '商品销售', rightTitle: '营业数据' }
      ]
    }
  },
  
  computed: {
    ...mapState('dashboard', {
      businessData: state => state.businessData,
      memberData: state => state.memberData,
      productData: state => state.productData
    }),
    
    currentCenterComponent() {
      return this.carouselComponents[this.currentCarouselIndex].center
    },
    
    currentRightComponent() {
      return this.carouselComponents[this.currentCarouselIndex].right
    },
    
    centerChartTitle() {
      return this.carouselComponents[this.currentCarouselIndex].centerTitle
    },
    
    rightPanelTitle() {
      return this.carouselComponents[this.currentCarouselIndex].rightTitle
    },
    
    currentCenterData() {
      const component = this.currentCenterComponent
      if (component === 'BusinessFullscreenView') return this.businessData
      if (component === 'MemberFullscreenView') return this.memberData
      if (component === 'ProductFullscreenView') return this.productData
      return null
    },
    
    currentRightData() {
      const component = this.currentRightComponent
      if (component === 'BusinessFullscreenView') return this.businessData
      if (component === 'MemberFullscreenView') return this.memberData
      if (component === 'ProductFullscreenView') return this.productData
      return null
    }
  },
  
  mounted() {
    this.updateTime()
    this.startTimeUpdate()
    this.enterFullscreen()
  },
  
  beforeDestroy() {
    this.stopTimeUpdate()
    this.stopAutoRefresh()
    this.stopAutoCarousel()
    this.exitFullscreen()
  },
  
  methods: {
    ...mapActions('dashboard', ['refreshAllData']),
    
    enterFullscreen() {
      const element = this.$refs.fullscreenContainer
      if (!element) return
      
      const requestFullscreen = element.requestFullscreen ||
                                element.webkitRequestFullscreen ||
                                element.mozRequestFullScreen ||
                                element.msRequestFullscreen
      
      if (requestFullscreen) {
        requestFullscreen.call(element)
        this.isFullscreen = true
        this.startAutoRefresh()
        this.startAutoCarousel()
      }
    },
    
    exitFullscreen() {
      const exitFullscreen = document.exitFullscreen ||
                            document.webkitExitFullscreen ||
                            document.mozCancelFullScreen ||
                            document.msExitFullscreen
      
      if (exitFullscreen) {
        exitFullscreen.call(document)
        this.isFullscreen = false
        this.stopAutoRefresh()
        this.stopAutoCarousel()
        this.$emit('exit-fullscreen')
      }
    },
    
    updateTime() {
      const now = new Date()
      const year = now.getFullYear()
      const month = String(now.getMonth() + 1).padStart(2, '0')
      const day = String(now.getDate()).padStart(2, '0')
      const hours = String(now.getHours()).padStart(2, '0')
      const minutes = String(now.getMinutes()).padStart(2, '0')
      const seconds = String(now.getSeconds()).padStart(2, '0')
      this.currentTime = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    },
    
    startTimeUpdate() {
      this.timeInterval = setInterval(() => {
        this.updateTime()
      }, 1000)
    },
    
    stopTimeUpdate() {
      if (this.timeInterval) {
        clearInterval(this.timeInterval)
        this.timeInterval = null
      }
    },
    
    startAutoRefresh() {
      this.autoRefreshInterval = setInterval(() => {
        this.refreshAllData()
      }, 30000)
    },
    
    stopAutoRefresh() {
      if (this.autoRefreshInterval) {
        clearInterval(this.autoRefreshInterval)
        this.autoRefreshInterval = null
      }
    },
    
    startAutoCarousel() {
      this.autoCarouselInterval = setInterval(() => {
        this.currentCarouselIndex = (this.currentCarouselIndex + 1) % this.carouselComponents.length
      }, 10000)
    },
    
    stopAutoCarousel() {
      if (this.autoCarouselInterval) {
        clearInterval(this.autoCarouselInterval)
        this.autoCarouselInterval = null
      }
    }
  }
}
</script>

<style scoped>
.fullscreen-dashboard {
  width: 100%;
  height: 100vh;
  background: #000;
  overflow: hidden;
}

.fullscreen-container {
  width: 100%;
  height: 100%;
  background: linear-gradient(to bottom, #0a0e27 0%, #0f1b3c 100%);
  padding: 20px;
  box-sizing: border-box;
  position: relative;
}

.dashboard-header {
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  margin-bottom: 20px;
}

.header-decoration-left,
.header-decoration-right {
  width: 300px;
  height: 2px;
  background: linear-gradient(to right, transparent, #4facfe);
}

.header-decoration-right {
  background: linear-gradient(to left, transparent, #4facfe);
}

.header-title {
  display: flex;
  align-items: center;
  gap: 20px;
  margin: 0 40px;
}

.title-text {
  font-size: 42px;
  font-weight: bold;
  background: linear-gradient(to right, #4facfe 0%, #00f2fe 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 0 0 20px rgba(79, 172, 254, 0.5);
  letter-spacing: 4px;
}

.exit-fullscreen-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  z-index: 1000;
}

.exit-fullscreen-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: scale(1.1);
}

.exit-fullscreen-btn i {
  font-size: 24px;
  color: #fff;
}

.current-time {
  position: absolute;
  top: 10px;
  left: 10px;
  font-size: 18px;
  color: #4facfe;
  font-family: 'Courier New', monospace;
  letter-spacing: 2px;
}

.dashboard-content {
  display: flex;
  gap: 20px;
  height: calc(100% - 140px);
}

.left-panel,
.right-panel {
  flex: 1;
  min-width: 0;
}

.center-panel {
  flex: 1.5;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.panel-box {
  height: 100%;
  padding: 20px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(79, 172, 254, 0.3);
  border-radius: 8px;
}

.panel-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
  font-size: 20px;
  color: #4facfe;
  font-weight: bold;
  border-bottom: 2px solid rgba(79, 172, 254, 0.3);
  padding-bottom: 10px;
}

.panel-content {
  height: calc(100% - 60px);
  overflow: hidden;
}

.core-metrics {
  display: flex;
  gap: 20px;
  height: 150px;
}

.metric-card {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(79, 172, 254, 0.3);
  border-radius: 8px;
}

.metric-label {
  font-size: 16px;
  color: #8c9db5;
  margin-bottom: 15px;
}

.metric-value {
  font-size: 36px;
  font-weight: bold;
  color: #4facfe;
}

.metric-number {
  font-family: 'Arial', sans-serif;
  text-shadow: 0 0 10px rgba(79, 172, 254, 0.5);
}

.center-chart-box {
  flex: 1;
  padding: 20px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(79, 172, 254, 0.3);
  border-radius: 8px;
}

.center-chart-content {
  height: calc(100% - 60px);
  overflow: hidden;
}

@keyframes glow {
  0%, 100% {
    text-shadow: 0 0 10px rgba(79, 172, 254, 0.5);
  }
  50% {
    text-shadow: 0 0 20px rgba(79, 172, 254, 0.8);
  }
}

.title-text {
  animation: glow 3s ease-in-out infinite;
}
</style>
