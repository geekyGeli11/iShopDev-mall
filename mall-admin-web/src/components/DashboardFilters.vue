<template>
  <div class="dashboard-filters">
    <el-row :gutter="20" type="flex" align="middle">
      <!-- 时间范围选择器 -->
      <el-col :span="4">
        <el-select 
          v-model="localFilters.timeRange" 
          placeholder="选择时间范围"
          @change="handleTimeRangeChange"
          style="width: 100%"
        >
          <el-option label="当日" value="today"></el-option>
          <el-option label="近3日" value="last3days"></el-option>
          <el-option label="近7日" value="last7days"></el-option>
          <el-option label="近30日" value="last30days"></el-option>
          <el-option label="近3个月" value="last3months"></el-option>
          <el-option label="近半年" value="last6months"></el-option>
          <el-option label="自定义" value="custom"></el-option>
        </el-select>
      </el-col>
      
      <!-- 自定义日期范围选择器 -->
      <el-col :span="6" v-if="localFilters.timeRange === 'custom'">
        <el-date-picker
          v-model="customDateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
          :picker-options="pickerOptions"
          @change="handleCustomDateChange"
          style="width: 100%"
        ></el-date-picker>
      </el-col>
      
      <!-- 学校筛选器 -->
      <el-col :span="4">
        <el-select 
          v-model="localFilters.schoolId" 
          placeholder="选择学校"
          clearable
          :disabled="isStoreAdmin"
          @change="handleFilterChange"
          style="width: 100%"
        >
          <el-option
            v-for="school in schools"
            :key="school.id"
            :label="school.name"
            :value="school.id"
          ></el-option>
        </el-select>
      </el-col>
      
      <!-- 门店筛选器 -->
      <el-col :span="4">
        <el-select 
          v-model="localFilters.storeId" 
          placeholder="选择门店"
          clearable
          :disabled="isStoreAdmin"
          @change="handleFilterChange"
          style="width: 100%"
        >
          <el-option
            v-for="store in stores"
            :key="store.id"
            :label="store.name"
            :value="store.id"
          ></el-option>
        </el-select>
      </el-col>
      
      <!-- 视图模式切换 -->
      <el-col :span="localFilters.timeRange === 'custom' ? 4 : 6">
        <el-radio-group v-model="localViewMode" @change="handleViewModeChange" size="small">
          <el-radio-button label="card">
            <i class="el-icon-s-grid"></i> 卡片
          </el-radio-button>
          <el-radio-button label="table">
            <i class="el-icon-s-order"></i> 表格
          </el-radio-button>
          <el-radio-button label="chart">
            <i class="el-icon-s-data"></i> 图表
          </el-radio-button>
        </el-radio-group>
      </el-col>
      
      <!-- 全屏按钮 -->
      <el-col :span="2" style="text-align: right">
        <el-button 
          type="primary" 
          icon="el-icon-full-screen" 
          circle
          @click="handleFullscreenClick"
          title="全屏模式"
        ></el-button>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { debounce } from '@/utils/debounce'
import { calculateDateRange } from '@/utils/dateUtils'
import { mapState } from 'vuex'

export default {
  name: 'DashboardFilters',
  
  props: {
    filters: {
      type: Object,
      default: () => ({
        timeRange: 'today',
        schoolId: null,
        storeId: null
      })
    },
    viewMode: {
      type: String,
      default: 'card'
    },
    schools: {
      type: Array,
      default: () => []
    },
    stores: {
      type: Array,
      default: () => []
    }
  },
  
  data() {
    return {
      localFilters: { ...this.filters },
      localViewMode: this.viewMode,
      customDateRange: null,
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now()
        },
        shortcuts: [{
          text: '最近一周',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '最近一个月',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '最近三个月',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
            picker.$emit('pick', [start, end])
          }
        }]
      }
    }
  },
  
  computed: {
    ...mapState('user', ['adminType']),
    // 是否为门店账号
    isStoreAdmin() {
      return this.adminType === true
    }
  },
  
  watch: {
    filters: {
      handler(newVal) {
        this.localFilters = { ...newVal }
      },
      deep: true
    },
    viewMode(newVal) {
      this.localViewMode = newVal
    }
  },
  
  created() {
    // 创建防抖的筛选器变化处理函数
    this.debouncedFilterChange = debounce(this.emitFilterChange, 300)
  },
  
  methods: {
    /**
     * 处理时间范围变化
     */
    handleTimeRangeChange(timeRange) {
      if (timeRange === 'custom') {
        // 自定义模式，等待用户选择日期
        return
      }
      const { startDate, endDate } = calculateDateRange(timeRange)
      this.localFilters.startDate = startDate
      this.localFilters.endDate = endDate
      this.customDateRange = null
      this.debouncedFilterChange()
    },
    
    /**
     * 处理自定义日期范围变化
     */
    handleCustomDateChange(dateRange) {
      if (dateRange && dateRange.length === 2) {
        this.localFilters.startDate = dateRange[0]
        this.localFilters.endDate = dateRange[1]
        this.debouncedFilterChange()
      }
    },
    
    /**
     * 处理筛选器变化
     */
    handleFilterChange() {
      this.debouncedFilterChange()
    },
    
    /**
     * 触发筛选器变化事件
     */
    emitFilterChange() {
      this.$emit('filter-change', { ...this.localFilters })
    },
    
    /**
     * 处理视图模式变化
     */
    handleViewModeChange(mode) {
      this.$emit('view-mode-change', mode)
    },
    
    /**
     * 处理全屏按钮点击
     */
    handleFullscreenClick() {
      this.$emit('fullscreen-click')
    }
  }
}
</script>

<style scoped>
.dashboard-filters {
  padding: 20px;
  background: #fff;
  border-radius: 4px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
</style>
