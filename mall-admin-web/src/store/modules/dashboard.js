/**
 * Dashboard Store模块
 * 管理首页看板的状态和数据
 */

const state = {
  // 当前激活的标签页：'business', 'member', 'product'
  activeTab: 'business',
  
  // 筛选器状态
  filters: {
    timeRange: 'today', // today, last3days, last7days, last30days, last3months, last6months
    startDate: null,
    endDate: null,
    schoolId: null,
    storeId: null
  },
  
  // 视图模式：'card', 'table', 'chart'
  viewMode: {
    business: 'card',
    member: 'card',
    product: 'card'
  },
  
  // 全屏模式
  isFullscreen: false,
  
  // 数据缓存
  businessData: null,
  memberData: null,
  productData: null,
  
  // 加载状态
  loading: {
    business: false,
    member: false,
    product: false
  },
  
  // 筛选选项
  filterOptions: {
    schools: [],
    stores: []
  },
  
  // 缓存时间戳
  cacheTimestamp: {
    business: null,
    member: null,
    product: null
  },
  
  // 缓存有效期（毫秒）
  cacheExpireTime: 5 * 60 * 1000 // 5分钟
};

const mutations = {
  /**
   * 设置当前激活的标签页
   */
  SET_ACTIVE_TAB(state, tab) {
    state.activeTab = tab;
  },
  
  /**
   * 设置筛选器
   */
  SET_FILTERS(state, filters) {
    state.filters = { ...state.filters, ...filters };
  },
  
  /**
   * 设置时间范围
   */
  SET_TIME_RANGE(state, { timeRange, startDate, endDate }) {
    state.filters.timeRange = timeRange;
    state.filters.startDate = startDate;
    state.filters.endDate = endDate;
  },
  
  /**
   * 设置学校筛选
   */
  SET_SCHOOL_FILTER(state, schoolId) {
    state.filters.schoolId = schoolId;
  },
  
  /**
   * 设置门店筛选
   */
  SET_STORE_FILTER(state, storeId) {
    state.filters.storeId = storeId;
  },
  
  /**
   * 设置视图模式
   */
  SET_VIEW_MODE(state, { tab, mode }) {
    state.viewMode[tab] = mode;
  },
  
  /**
   * 设置全屏模式
   */
  SET_FULLSCREEN(state, isFullscreen) {
    state.isFullscreen = isFullscreen;
  },
  
  /**
   * 设置营业数据
   */
  SET_BUSINESS_DATA(state, data) {
    state.businessData = data;
    state.cacheTimestamp.business = Date.now();
  },
  
  /**
   * 设置会员数据
   */
  SET_MEMBER_DATA(state, data) {
    state.memberData = data;
    state.cacheTimestamp.member = Date.now();
  },
  
  /**
   * 设置商品数据
   */
  SET_PRODUCT_DATA(state, data) {
    state.productData = data;
    state.cacheTimestamp.product = Date.now();
  },
  
  /**
   * 设置加载状态
   */
  SET_LOADING(state, { type, loading }) {
    state.loading[type] = loading;
  },
  
  /**
   * 设置筛选选项
   */
  SET_FILTER_OPTIONS(state, options) {
    state.filterOptions = options;
  },
  
  /**
   * 清除缓存
   */
  CLEAR_CACHE(state, type) {
    if (type) {
      state[`${type}Data`] = null;
      state.cacheTimestamp[type] = null;
    } else {
      state.businessData = null;
      state.memberData = null;
      state.productData = null;
      state.cacheTimestamp = {
        business: null,
        member: null,
        product: null
      };
    }
  }
};

const actions = {
  /**
   * 切换标签页
   */
  switchTab({ commit }, tab) {
    commit('SET_ACTIVE_TAB', tab);
  },
  
  /**
   * 更新筛选器
   */
  updateFilters({ commit, dispatch }, filters) {
    commit('SET_FILTERS', filters);
    // 清除所有缓存，因为筛选条件变了
    commit('CLEAR_CACHE');
    // 重新获取当前标签的数据
    dispatch('refreshCurrentData');
  },
  
  /**
   * 更新时间范围
   */
  updateTimeRange({ commit, dispatch }, { timeRange, startDate, endDate }) {
    commit('SET_TIME_RANGE', { timeRange, startDate, endDate });
    commit('CLEAR_CACHE');
    dispatch('refreshCurrentData');
  },
  
  /**
   * 更新学校筛选
   */
  updateSchoolFilter({ commit, dispatch }, schoolId) {
    commit('SET_SCHOOL_FILTER', schoolId);
    commit('CLEAR_CACHE');
    dispatch('refreshCurrentData');
  },
  
  /**
   * 更新门店筛选
   */
  updateStoreFilter({ commit, dispatch }, storeId) {
    commit('SET_STORE_FILTER', storeId);
    commit('CLEAR_CACHE');
    dispatch('refreshCurrentData');
  },
  
  /**
   * 切换视图模式
   */
  switchViewMode({ commit, state }, mode) {
    commit('SET_VIEW_MODE', { tab: state.activeTab, mode });
    // 保存到sessionStorage
    sessionStorage.setItem(`dashboard_view_mode_${state.activeTab}`, mode);
  },
  
  /**
   * 恢复视图模式偏好
   */
  restoreViewModePreferences({ commit }) {
    ['business', 'member', 'product'].forEach(tab => {
      const savedMode = sessionStorage.getItem(`dashboard_view_mode_${tab}`);
      if (savedMode) {
        commit('SET_VIEW_MODE', { tab, mode: savedMode });
      }
    });
  },
  
  /**
   * 切换全屏模式
   */
  toggleFullscreen({ commit, state }) {
    commit('SET_FULLSCREEN', !state.isFullscreen);
  },
  
  /**
   * 获取营业数据
   */
  async fetchBusinessData({ commit, state, getters }) {
    // 检查缓存是否有效
    if (getters.isBusinessDataCacheValid) {
      return state.businessData;
    }
    
    commit('SET_LOADING', { type: 'business', loading: true });
    
    try {
      const { getBusinessStatistics } = require('@/api/home');
      const params = {
        startDate: state.filters.startDate,
        endDate: state.filters.endDate,
        schoolId: state.filters.schoolId,
        storeId: state.filters.storeId
      };
      
      const response = await getBusinessStatistics(params.startDate, params.endDate, params.schoolId, params.storeId);
      commit('SET_BUSINESS_DATA', response.data);
      return response.data;
    } catch (error) {
      console.error('获取营业数据失败:', error);
      throw error;
    } finally {
      commit('SET_LOADING', { type: 'business', loading: false });
    }
  },
  
  /**
   * 获取会员数据
   */
  async fetchMemberData({ commit, state, getters }) {
    // 检查缓存是否有效
    if (getters.isMemberDataCacheValid) {
      return state.memberData;
    }
    
    commit('SET_LOADING', { type: 'member', loading: true });
    
    try {
      const { getMemberStatistics } = require('@/api/home');
      const response = await getMemberStatistics(
        state.filters.startDate,
        state.filters.endDate,
        state.filters.schoolId,
        state.filters.storeId,
        10
      );
      commit('SET_MEMBER_DATA', response.data);
      return response.data;
    } catch (error) {
      console.error('获取会员数据失败:', error);
      throw error;
    } finally {
      commit('SET_LOADING', { type: 'member', loading: false });
    }
  },
  
  /**
   * 获取商品数据
   */
  async fetchProductData({ commit, state, getters }) {
    // 检查缓存是否有效
    if (getters.isProductDataCacheValid) {
      return state.productData;
    }
    
    commit('SET_LOADING', { type: 'product', loading: true });
    
    try {
      const { getProductStatistics } = require('@/api/home');
      const response = await getProductStatistics(
        state.filters.startDate,
        state.filters.endDate,
        state.filters.schoolId,
        state.filters.storeId,
        10,
        'amount'
      );
      commit('SET_PRODUCT_DATA', response.data);
      return response.data;
    } catch (error) {
      console.error('获取商品数据失败:', error);
      throw error;
    } finally {
      commit('SET_LOADING', { type: 'product', loading: false });
    }
  },
  
  /**
   * 刷新当前标签的数据
   */
  refreshCurrentData({ state, dispatch }) {
    switch (state.activeTab) {
      case 'business':
        return dispatch('fetchBusinessData');
      case 'member':
        return dispatch('fetchMemberData');
      case 'product':
        return dispatch('fetchProductData');
    }
  },
  
  /**
   * 刷新所有数据
   */
  refreshAllData({ dispatch }) {
    return Promise.all([
      dispatch('fetchBusinessData'),
      dispatch('fetchMemberData'),
      dispatch('fetchProductData')
    ]);
  },
  
  /**
   * 获取筛选选项（学校和门店列表）
   */
  async fetchFilterOptions({ commit, rootState }) {
    try {
      const { fetchEnabledSchools } = require('@/api/school');
      const { getAllStores } = require('@/api/storeAddress');
      
      const [schoolsResponse, storesResponse] = await Promise.all([
        fetchEnabledSchools(),
        getAllStores()
      ]);
      
      // 处理学校数据
      const schools = Array.isArray(schoolsResponse.data) 
        ? schoolsResponse.data 
        : (schoolsResponse.data && schoolsResponse.data.list ? schoolsResponse.data.list : []);
      
      // 处理门店数据
      const stores = Array.isArray(storesResponse.data) 
        ? storesResponse.data 
        : (storesResponse.data && storesResponse.data.list ? storesResponse.data.list : []);
      
      // 确保数据格式正确
      const formattedSchools = schools.map(school => ({
        id: school.id,
        name: school.name || school.schoolName
      }));
      
      const formattedStores = stores.map(store => ({
        id: store.id,
        name: store.addressName || store.name || store.storeName || store.address
      }));
      
      commit('SET_FILTER_OPTIONS', {
        schools: formattedSchools,
        stores: formattedStores
      });
      
      // 如果是门店账号，自动设置筛选条件
      const userState = rootState.user;
      if (userState && userState.adminType === true) {
        // 门店账号，自动设置学校和门店筛选
        if (userState.schoolId) {
          commit('SET_SCHOOL_FILTER', userState.schoolId);
        }
        if (userState.storeId) {
          commit('SET_STORE_FILTER', userState.storeId);
        }
      }
      
      return {
        schools: formattedSchools,
        stores: formattedStores
      };
    } catch (error) {
      console.error('获取筛选选项失败:', error);
      // 返回空数组而不是抛出错误，避免页面崩溃
      commit('SET_FILTER_OPTIONS', {
        schools: [],
        stores: []
      });
      throw error;
    }
  }
};

const getters = {
  /**
   * 获取当前标签的数据
   */
  currentData: (state) => {
    switch (state.activeTab) {
      case 'business':
        return state.businessData;
      case 'member':
        return state.memberData;
      case 'product':
        return state.productData;
      default:
        return null;
    }
  },
  
  /**
   * 获取当前标签的加载状态
   */
  currentLoading: (state) => {
    return state.loading[state.activeTab];
  },
  
  /**
   * 获取当前标签的视图模式
   */
  currentViewMode: (state) => {
    return state.viewMode[state.activeTab];
  },
  
  /**
   * 检查营业数据缓存是否有效
   */
  isBusinessDataCacheValid: (state) => {
    if (!state.businessData || !state.cacheTimestamp.business) {
      return false;
    }
    const now = Date.now();
    return (now - state.cacheTimestamp.business) < state.cacheExpireTime;
  },
  
  /**
   * 检查会员数据缓存是否有效
   */
  isMemberDataCacheValid: (state) => {
    if (!state.memberData || !state.cacheTimestamp.member) {
      return false;
    }
    const now = Date.now();
    return (now - state.cacheTimestamp.member) < state.cacheExpireTime;
  },
  
  /**
   * 检查商品数据缓存是否有效
   */
  isProductDataCacheValid: (state) => {
    if (!state.productData || !state.cacheTimestamp.product) {
      return false;
    }
    const now = Date.now();
    return (now - state.cacheTimestamp.product) < state.cacheExpireTime;
  }
};

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
};
