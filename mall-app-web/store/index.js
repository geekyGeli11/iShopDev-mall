import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

const store = new Vuex.Store({
	state: {
		hasLogin: false,
		userInfo: {},
		selectedCategoryId: null,
		// 学校相关状态
		selectedSchool: null,
		lastSchoolId: null, // 用于检测学校是否发生变化
		// 登录弹窗状态管理（临时状态，不持久化）
		shouldShowLoginPopup: false,
		loginPopupReason: '', // 弹窗原因：'unauthorized' | 'expired'
	},
	mutations: {
		login(state, provider) {

			state.hasLogin = true;
			state.userInfo = provider;
			uni.setStorage({ //缓存用户登陆状态
				key: 'userInfo',
				data: provider
			})
			console.log(state.userInfo);
		},
		logout(state) {
			state.hasLogin = false;
			state.userInfo = {};
			uni.removeStorage({
				key: 'userInfo'
			});
			uni.removeStorage({
				key: 'tokenInfo' // 统一只清除tokenInfo
			})
		},
		setSelectedCategoryId(state, id) {
			state.selectedCategoryId = id; // 存储 sid
		},
		// 设置选中的学校
		setSelectedSchool(state, school) {
			state.selectedSchool = school;
			state.lastSchoolId = school ? school.id : null;
		},
		// 更新最后记录的学校ID（用于检测变化）
		updateLastSchoolId(state, schoolId) {
			state.lastSchoolId = schoolId;
		},
		// 设置登录弹窗显示状态
		setLoginPopup(state, { show, reason = '' }) {
			state.shouldShowLoginPopup = show;
			state.loginPopupReason = reason;
		},
		// 清除登录弹窗状态
		clearLoginPopup(state) {
			state.shouldShowLoginPopup = false;
			state.loginPopupReason = '';
		},
	},
	actions: {
		// 更新用户积分
		updateUserIntegration({ commit, state }) {
			// 从API获取最新的积分数据
			import('@/api/point').then(({ getCurrentIntegration }) => {
				getCurrentIntegration().then(res => {
					if (res.code === 200) {
						// 更新state中的用户积分
						const updatedUserInfo = { ...state.userInfo, integration: res.data || 0 };
						commit('login', updatedUserInfo);
					}
				}).catch(err => {
					console.error('更新用户积分失败', err);
				});
			});
		}
	}
})

export default store