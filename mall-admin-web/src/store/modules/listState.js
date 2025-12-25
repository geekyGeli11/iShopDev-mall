/**
 * 列表页面状态管理模块
 * 用于保持商品列表、订单列表、会员列表等页面的搜索条件和分页状态
 */

const STORAGE_KEY = 'mall_admin_list_states'

// 获取 sessionStorage 中的状态
function getStorageState() {
  try {
    const stored = sessionStorage.getItem(STORAGE_KEY)
    return stored ? JSON.parse(stored) : {}
  } catch (error) {
    console.warn('Failed to parse list states from sessionStorage:', error)
    return {}
  }
}

// 保存状态到 sessionStorage
function setStorageState(states) {
  try {
    sessionStorage.setItem(STORAGE_KEY, JSON.stringify(states))
  } catch (error) {
    console.warn('Failed to save list states to sessionStorage:', error)
  }
}

const state = {
  // 商品列表状态
  productList: {
    listQuery: null,
    scrollPosition: 0,
    selectProductCateValue: null, // 商品分类选择值
    lastUpdateTime: null
  },
  // 订单列表状态
  orderList: {
    listQuery: null,
    scrollPosition: 0,
    dateRange: null, // 日期范围
    lastUpdateTime: null
  },
  // 会员列表状态
  memberList: {
    listQuery: null,
    scrollPosition: 0,
    dateRange: null, // 注册时间范围
    lastUpdateTime: null
  }
}

const mutations = {
  // 设置列表状态
  SET_LIST_STATE(state, { pageKey, listState }) {
    if (state[pageKey]) {
      state[pageKey] = {
        ...state[pageKey],
        ...listState,
        lastUpdateTime: Date.now()
      }
    }
  },

  // 清除列表状态
  CLEAR_LIST_STATE(state, pageKey) {
    if (state[pageKey]) {
      state[pageKey] = {
        listQuery: null,
        scrollPosition: 0,
        selectProductCateValue: null,
        dateRange: null,
        lastUpdateTime: null
      }
    }
  },

  // 从 sessionStorage 恢复所有状态
  RESTORE_FROM_STORAGE(state) {
    const storedStates = getStorageState()
    Object.keys(storedStates).forEach(pageKey => {
      if (state[pageKey]) {
        // 检查状态是否过期（超过1小时自动清除）
        const stateAge = Date.now() - (storedStates[pageKey].lastUpdateTime || 0)
        if (stateAge < 60 * 60 * 1000) { // 1小时
          state[pageKey] = { ...state[pageKey], ...storedStates[pageKey] }
        }
      }
    })
  }
}

const actions = {
  // 保存列表状态
  saveListState({ commit, state }, { pageKey, listState }) {
    // 更新 Vuex 状态
    commit('SET_LIST_STATE', { pageKey, listState })
    
    // 同步到 sessionStorage
    const allStates = {}
    Object.keys(state).forEach(key => {
      if (state[key].listQuery || state[key].scrollPosition) {
        allStates[key] = state[key]
      }
    })
    setStorageState(allStates)
  },

  // 清除列表状态
  clearListState({ commit, state }, pageKey) {
    commit('CLEAR_LIST_STATE', pageKey)
    
    // 从 sessionStorage 中移除
    const storedStates = getStorageState()
    delete storedStates[pageKey]
    setStorageState(storedStates)
  },

  // 从 sessionStorage 恢复状态
  restoreFromStorage({ commit }) {
    commit('RESTORE_FROM_STORAGE')
  },

  // 清除所有过期状态
  clearExpiredStates({ state }) {
    const storedStates = getStorageState()
    const validStates = {}
    const now = Date.now()
    
    Object.keys(storedStates).forEach(pageKey => {
      const stateAge = now - (storedStates[pageKey].lastUpdateTime || 0)
      if (stateAge < 60 * 60 * 1000) { // 保留1小时内的状态
        validStates[pageKey] = storedStates[pageKey]
      }
    })
    
    setStorageState(validStates)
  }
}

const getters = {
  // 获取指定页面的列表状态
  getListState: (state) => (pageKey) => {
    return state[pageKey] || null
  },

  // 检查指定页面是否有保存的状态
  hasListState: (state) => (pageKey) => {
    const pageState = state[pageKey]
    return !!(pageState && pageState.listQuery)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}
