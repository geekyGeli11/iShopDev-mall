/**
 * 列表页面状态管理 Mixin
 * 用于保持和恢复列表页面的搜索条件、分页状态等
 */

import { debounce } from '@/utils'

export default {
  data() {
    return {
      // 页面标识，需要在使用的组件中设置
      pageKey: '',
      // 是否正在恢复状态
      isRestoringState: false,
      // 防抖保存状态的方法
      debouncedSaveState: null
    }
  },

  created() {
    // 初始化防抖方法
    this.debouncedSaveState = debounce(this.saveCurrentState, 300)
    
    // 从 sessionStorage 恢复状态
    this.$store.dispatch('listState/restoreFromStorage')
  },

  mounted() {
    // 页面挂载后尝试恢复状态
    this.restoreState()
    
    // 清理过期状态
    this.$store.dispatch('listState/clearExpiredStates')
  },

  methods: {
    /**
     * 保存当前页面状态
     */
    saveCurrentState() {
      if (!this.pageKey || this.isRestoringState) {
        return
      }

      try {
        const state = {
          listQuery: this.listQuery ? { ...this.listQuery } : null,
          scrollPosition: this.getScrollPosition()
        }

        // 商品列表特殊处理
        if (this.pageKey === 'productList' && this.selectProductCateValue !== undefined) {
          state.selectProductCateValue = this.selectProductCateValue
        }

        // 订单列表和会员列表的日期范围处理
        if ((this.pageKey === 'orderList' || this.pageKey === 'memberList') && this.dateRange) {
          state.dateRange = [...this.dateRange]
        }

        this.$store.dispatch('listState/saveListState', {
          pageKey: this.pageKey,
          listState: state
        })
      } catch (error) {
        console.warn('Failed to save list state:', error)
      }
    },

    /**
     * 恢复页面状态
     */
    restoreState() {
      if (!this.pageKey) {
        console.warn('pageKey is not set for listStateMixin')
        return
      }

      try {
        this.isRestoringState = true
        const savedState = this.$store.getters['listState/getListState'](this.pageKey)
        
        if (savedState && savedState.listQuery) {
          // 恢复查询条件
          this.listQuery = { ...this.listQuery, ...savedState.listQuery }

          // 商品列表特殊处理
          if (this.pageKey === 'productList' && savedState.selectProductCateValue !== undefined) {
            this.selectProductCateValue = savedState.selectProductCateValue
          }

          // 订单列表和会员列表的日期范围处理
          if ((this.pageKey === 'orderList' || this.pageKey === 'memberList') && savedState.dateRange) {
            this.dateRange = [...savedState.dateRange]
          }

          // 重新获取数据
          this.getList()

          // 恢复滚动位置
          this.$nextTick(() => {
            if (savedState.scrollPosition) {
              this.restoreScrollPosition(savedState.scrollPosition)
            }
            this.isRestoringState = false
          })
        } else {
          this.isRestoringState = false
        }
      } catch (error) {
        console.warn('Failed to restore list state:', error)
        this.isRestoringState = false
      }
    },

    /**
     * 清除当前页面状态
     */
    clearCurrentState() {
      if (this.pageKey) {
        this.$store.dispatch('listState/clearListState', this.pageKey)
      }
    },

    /**
     * 获取当前滚动位置
     */
    getScrollPosition() {
      return document.documentElement.scrollTop || document.body.scrollTop || 0
    },

    /**
     * 恢复滚动位置
     */
    restoreScrollPosition(position) {
      try {
        window.scrollTo({
          top: position,
          behavior: 'auto'
        })
      } catch (error) {
        // 降级处理
        window.scrollTo(0, position)
      }
    },

    /**
     * 重写搜索方法，添加状态保存
     */
    handleSearchListWithState() {
      // 调用原有的搜索方法
      if (this.handleSearchList) {
        this.handleSearchList()
      }
      // 保存状态
      this.$nextTick(() => {
        this.debouncedSaveState()
      })
    },

    /**
     * 重写重置方法，清除状态
     */
    handleResetSearchWithState() {
      // 调用原有的重置方法
      if (this.handleResetSearch) {
        this.handleResetSearch()
      }
      // 清除保存的状态
      this.clearCurrentState()
    },

    /**
     * 重写分页方法，添加状态保存
     */
    handleCurrentChangeWithState(val) {
      // 调用原有的分页方法
      if (this.handleCurrentChange) {
        this.handleCurrentChange(val)
      }
      // 保存状态
      this.$nextTick(() => {
        this.debouncedSaveState()
      })
    },

    /**
     * 重写页面大小改变方法，添加状态保存
     */
    handleSizeChangeWithState(val) {
      // 调用原有的方法
      if (this.handleSizeChange) {
        this.handleSizeChange(val)
      }
      // 保存状态
      this.$nextTick(() => {
        this.debouncedSaveState()
      })
    }
  },

  beforeRouteLeave(to, from, next) {
    // 离开页面时保存状态
    this.saveCurrentState()
    next()
  },

  beforeDestroy() {
    // 组件销毁前保存状态
    this.saveCurrentState()
  }
}
