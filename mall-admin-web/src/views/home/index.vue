<template>
  <div class="home-container">
    <!-- 筛选器 -->
    <dashboard-filters
      :filters="filters"
      :view-mode="currentViewMode"
      :schools="filterOptions.schools"
      :stores="filterOptions.stores"
      @filter-change="handleFilterChange"
      @view-mode-change="handleViewModeChange"
      @fullscreen-click="handleFullscreenClick"
    />
    
    <!-- 标签导航 -->
    <el-tabs v-model="activeTab" @tab-click="handleTabClick" class="dashboard-tabs">
      <el-tab-pane label="营业数据" name="business">
        <business-dashboard :view-mode="currentViewMode" />
      </el-tab-pane>
      
      <el-tab-pane label="会员数据" name="member">
        <member-dashboard :view-mode="currentViewMode" />
      </el-tab-pane>
      
      <el-tab-pane label="商品销售数据" name="product">
        <product-dashboard :view-mode="currentViewMode" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { mapState, mapActions, mapGetters } from 'vuex'
import DashboardFilters from '@/components/DashboardFilters.vue'
import BusinessDashboard from '@/components/dashboard/BusinessDashboard.vue'
import MemberDashboard from '@/components/dashboard/MemberDashboard.vue'
import ProductDashboard from '@/components/dashboard/ProductDashboard.vue'
import { calculateDateRange } from '@/utils/dateUtils'

export default {
  name: 'HomeIndex',
  
  components: {
    DashboardFilters,
    BusinessDashboard,
    MemberDashboard,
    ProductDashboard
  },
  
  data() {
    return {
      activeTab: 'business'
    }
  },
  
  computed: {
    ...mapState('dashboard', ['filters', 'filterOptions']),
    ...mapGetters('dashboard', ['currentViewMode'])
  },
  
  created() {
    // 初始化筛选器
    this.initFilters()
    // 加载筛选选项
    this.loadFilterOptions()
    // 恢复视图模式偏好
    this.restoreViewModePreferences()
  },
  
  methods: {
    ...mapActions('dashboard', [
      'switchTab',
      'updateFilters',
      'updateTimeRange',
      'switchViewMode',
      'fetchFilterOptions',
      'restoreViewModePreferences',
      'toggleFullscreen',
      'refreshAllData'
    ]),
    
    /**
     * 初始化筛选器
     */
    initFilters() {
      // 默认设置为当日
      const { startDate, endDate } = calculateDateRange('today')
      this.updateTimeRange({
        timeRange: 'today',
        startDate,
        endDate
      })
    },
    
    /**
     * 加载筛选选项
     */
    async loadFilterOptions() {
      try {
        await this.fetchFilterOptions()
      } catch (error) {
        console.error('加载筛选选项失败:', error)
        this.$message.error('加载筛选选项失败')
      }
    },
    
    /**
     * 处理标签切换
     */
    handleTabClick(tab) {
      this.switchTab(tab.name)
    },
    
    /**
     * 处理筛选器变化
     */
    handleFilterChange(filters) {
      this.updateFilters(filters)
    },
    
    /**
     * 处理视图模式变化
     */
    handleViewModeChange(mode) {
      this.switchViewMode(mode)
    },
    
    /**
     * 处理全屏按钮点击
     */
    handleFullscreenClick() {
      this.showFullscreenDashboard()
    },
    
    /**
     * 显示全屏看板
     */
    async showFullscreenDashboard() {
      try {
        // 先加载所有数据
        await this.refreshAllData()
        
        // 创建全屏看板组件
        const FullscreenDashboard = () => import('@/components/dashboard/FullscreenDashboard.vue')
        
        // 创建临时容器
        const container = document.createElement('div')
        container.id = 'fullscreen-dashboard-container'
        container.style.cssText = 'position: fixed; top: 0; left: 0; width: 100%; height: 100%; z-index: 9999;'
        document.body.appendChild(container)
        
        // 创建挂载点（$mount 会替换元素，所以需要一个子元素）
        const mountPoint = document.createElement('div')
        container.appendChild(mountPoint)
        
        // 清理函数
        let isCleanedUp = false
        const cleanup = () => {
          if (isCleanedUp) return
          isCleanedUp = true
          
          // 清理组件
          if (vm) {
            vm.$destroy()
          }
          // 移除容器
          if (container && container.parentNode) {
            container.parentNode.removeChild(container)
          }
          // 移除事件监听
          document.removeEventListener('fullscreenchange', exitHandler)
          document.removeEventListener('webkitfullscreenchange', exitHandler)
          document.removeEventListener('mozfullscreenchange', exitHandler)
          document.removeEventListener('MSFullscreenChange', exitHandler)
        }
        
        // 监听退出全屏事件
        const exitHandler = () => {
          if (!document.fullscreenElement && 
              !document.webkitFullscreenElement && 
              !document.mozFullScreenElement && 
              !document.msFullscreenElement) {
            cleanup()
          }
        }
        
        // 动态创建组件实例
        const vm = new this.$root.constructor({
          router: this.$router,
          store: this.$store,
          render: h => h(FullscreenDashboard, {
            on: {
              'exit-fullscreen': cleanup
            }
          })
        }).$mount(mountPoint)
        
        document.addEventListener('fullscreenchange', exitHandler)
        document.addEventListener('webkitfullscreenchange', exitHandler)
        document.addEventListener('mozfullscreenchange', exitHandler)
        document.addEventListener('MSFullscreenChange', exitHandler)
      } catch (error) {
        console.error('显示全屏看板失败:', error)
        this.$message.error('显示全屏看板失败')
      }
    }
  }
}
</script>

<style scoped>
.home-container {
  padding: 20px;
  background: #f0f2f5;
  min-height: calc(100vh - 84px);
}

.dashboard-tabs {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.dashboard-tabs >>> .el-tabs__header {
  margin-bottom: 20px;
}

.dashboard-tabs >>> .el-tabs__item {
  font-size: 16px;
  font-weight: 500;
}

.dashboard-tabs >>> .el-tabs__item.is-active {
  color: #409eff;
}
</style>
