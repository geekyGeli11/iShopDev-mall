<template>
	<view class="login-popup" v-if="visible">
		<view class="login-overlay" @click="handleClose"></view>
		<view class="login-container">

			<!-- 主体内容 -->
			<view class="login-content">
				<view class="notch"></view>

				<!-- 占位空间 -->
				<view class="spacer"></view>

				<view class="login-title">
					<image class="welcome-text-image"
						src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/welcome_text.png"
						mode="aspectFit"></image>
				</view>

				<!-- 隐私协议选择 -->
				<view class="privacy-agreement">
					<view class="checkbox-container" @click="toggleAgreement">
						<view class="checkbox" :class="{ 'checked': isAgreed }">
							<view class="check-inner" v-if="isAgreed"></view>
						</view>
					</view>
					<view class="agreement-text">
						<text>我已阅读、理解并同意</text>
						<text class="agreement-link" @tap.stop="openPrivacy">《隐私政策》</text>
						<text>及</text>
						<text class="agreement-link" @tap.stop="openTerms">《用户服务协议》</text>
						<text>的内容，收集和使用我的个人信息。</text>
					</view>
				</view>

				<!-- 登录按钮 -->
				<view class="button-wrapper">
					<button class="login-button" :class="{ 'disabled': !isAgreed }" :disabled="!isAgreed"
						open-type="getPhoneNumber" @getphonenumber="getPhoneNumber">同意协议并登录</button>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import { loginByPhone } from '@/api/member.js';
import { mapMutations } from 'vuex';

export default {
	name: "login-popup",
	props: {
		visible: {
			type: Boolean,
			default: false
		}
	},
	data() {
		return {
			isAgreed: false
		}
	},
	methods: {
		...mapMutations(['login']),

		// 切换协议同意状态
		toggleAgreement() {
			this.isAgreed = !this.isAgreed;
		},

		// 关闭弹窗
		handleClose() {
			this.$emit('update:visible', false);
			this.$emit('close');
			// 清除 Vuex 中的登录弹窗状态
			this.$store.commit('clearLoginPopup');
		},

		// 获取手机号
		getPhoneNumber(e) {
			console.log('getPhoneNumber 事件触发', e);
			if (!this.isAgreed) {
				uni.showToast({
					title: '请先同意隐私协议',
					icon: 'none'
				});
				return;
			}

			// 用户点击允许
			if (e.detail.errMsg === 'getPhoneNumber:ok') {
				console.log('用户同意获取手机号');
				const { code } = e.detail;

				// 调用后端接口获取手机号
				uni.showLoading({
					title: '登录中...'
				});

				// 调用微信登录接口
				uni.login({
					provider: 'weixin',
					success: (loginRes) => {
						console.log('微信登录成功', loginRes);
						if (loginRes.code) {
							// 获取邀请参数
							const pendingInvite = uni.getStorageSync('pendingInvite');

							// 调用后端登录接口
							loginByPhone({
								code: loginRes.code,
								phoneCode: code,
								inviteParam: pendingInvite || ''
							}).then(response => {
								console.log('登录接口返回', response);
								uni.hideLoading();

								if (response.code === 200) {
									// 登录成功，保存完整的token信息
									const tokenData = response.data;
									const tokenInfo = {
										token: tokenData.token,
										tokenHead: tokenData.tokenHead,
										expiresIn: tokenData.expiresIn,
										loginTime: Date.now(),
										openId: tokenData.openId
									};
									uni.setStorageSync('tokenInfo', JSON.stringify(tokenInfo));
									// 统一只保存tokenInfo，不再保存单独的token

									// 直接保存 member 到本地缓存
									const member = response.data.member;
									uni.setStorageSync('userInfo', member);

									// 更新 store 中的登录状态
									this.login(member);

									// 清除邀请参数（登录成功后后端已处理）
									uni.removeStorageSync('pendingInvite');

									// 判断是否新用户
									if (member && member.sourceType === 1) {
										// 清除 Vuex 中的登录弹窗状态
										this.$store.commit('clearLoginPopup');

										uni.showModal({
											title: '温馨提示',
											content: '请您继续完善个人信息',
											showCancel: false,
											confirmText: '确定',
											success: (res) => {
												if (res.confirm) {
													// 新用户跳转到注册页面，保持returnPageInfo用于后续返回
													uni.navigateTo({ url: '/pages/public/register' });
												}
											}
										});
									} else {
										// 老用户登录成功
										// 检查是否需要返回特定页面
										const returnPageInfo = uni.getStorageSync('returnPageInfo');

										// 清除 Vuex 中的登录弹窗状态
										this.$store.commit('clearLoginPopup');

										if (returnPageInfo) {
											// 清除返回页面信息
											uni.removeStorageSync('returnPageInfo');

											// 构建完整URL
											const url = this.buildUrlWithParams(returnPageInfo.url, returnPageInfo.options);

											uni.showToast({
												title: '登录成功',
												icon: 'success'
											});

											// 延迟跳转，让用户看到成功提示
											setTimeout(() => {
												uni.reLaunch({ url });
											}, 1000);
										} else {
											// 没有返回页面信息，正常处理
											this.$emit('login', member);
											this.handleClose();
											uni.showToast({
												title: '登录成功',
												icon: 'success'
											});
										}
									}
								} else {
									uni.showToast({
										title: response.message || '登录失败',
										icon: 'none'
									});
								}
							}).catch(err => {
								console.error('登录接口调用失败', err);
								uni.hideLoading();
								uni.showToast({
									title: '登录失败，请重试',
									icon: 'none'
								});
							});
						}
					},
					fail: (err) => {
						console.error('微信登录失败', err);
						uni.hideLoading();
						uni.showToast({
							title: '登录失败，请重试',
							icon: 'none'
						});
					}
				});
			} else {
				console.log('用户拒绝获取手机号', e.detail.errMsg);
				// 用户拒绝或获取失败
				uni.showToast({
					title: '请授权手机号以继续',
					icon: 'none'
				});
			}
		},

		// 打开隐私政策 - 通过事件触发父组件显示弹窗
		openPrivacy() {
			this.$emit('openPrivacy');
		},

		// 打开用户服务协议 - 通过事件触发父组件显示弹窗
		openTerms() {
			this.$emit('openTerms');
		},

		// 构建带参数的URL
		buildUrlWithParams(route, options) {
			let url = `/${route}`;
			const params = Object.keys(options || {});
			if (params.length > 0) {
				const queryString = params.map(key => `${key}=${encodeURIComponent(options[key])}`).join('&');
				url += `?${queryString}`;
			}
			return url;
		}
	}
}
</script>

<style lang="scss">
.login-popup {
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	z-index: 999;
	display: flex;
	align-items: center;
	justify-content: center;
}

.login-overlay {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background-color: rgba(0, 0, 0, 0.6);
	backdrop-filter: blur(4px);
}

.login-container {
	position: relative;
	width: 316px;
	height: 520px;
	background-image: url('https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/new_index/login_popup_background.png');
	background-size: cover;
	background-position: center;
	background-repeat: no-repeat;
	border-radius: 15px;
	padding: 0;
	box-sizing: border-box;
	display: flex;
	flex-direction: column;
	align-items: center;
	z-index: 1000;
	overflow: visible;
}

.login-logo {
	position: absolute;
	top: -40px;
	left: 0;
	width: 90px;
	height: 90px;
	z-index: 1001;

	.logo-image {
		width: 100%;
		height: 100%;
	}
}

.login-header {
	width: 100%;
	padding: 40px 0 20px;
	text-align: center;

	.header-image {
		width: 200rpx;
		height: 45rpx;
	}
}

.login-content {
	position: relative;
	width: 100%;
	flex: 1;
	padding: 30px 26px 40px;
	display: flex;
	flex-direction: column;
}

.notch {
	display: none;
}

.login-title {
	text-align: center;
	margin-top: 40px;

	.welcome-text-image {
		width: 210px;
		height: 76px;
	}
}

.spacer {
	flex: 1;
}

.privacy-agreement {
	display: flex;
	align-items: flex-start;
	margin-top: 20px;
	margin-bottom: 30px;
	gap: 8px;

	.checkbox-container {
		padding: 0;
		cursor: pointer;
	}

	.checkbox {
		width: 16px;
		height: 16px;
		border-radius: 50%;
		border: 1px solid #EEEEEE;
		background-color: #FFFFFF;
		display: flex;
		align-items: center;
		justify-content: center;

		&.checked {
			background-color: #FFFFFF;
		}

		.check-inner {
			width: 8px;
			height: 8px;
			border-radius: 50%;
			background-color: #20201E;
		}
	}

	.agreement-text {
		flex: 1;
		font-size: 14px;
		color: #20201E;
		line-height: 1.4;
		font-family: 'PingFang SC', sans-serif;

		.agreement-link {
			color: #20201E;
			font-weight: 400;
			text-decoration: underline;
		}
	}
}

.button-wrapper {
	width: 264px;
	height: auto;
	margin-bottom: 100rpx;
}

// .wx-button {
//   position: absolute;
//   top: 0;
//   left: 0;
//   width: 100%;
//   height: 100%;
//   opacity: 0;
//   z-index: 2;
// }

.login-button {
	display: flex;
	flex-direction: row;
	justify-content: center;
	align-items: center;
	padding: 10px 14px;
	gap: 8px;
	width: 264px;
	height: auto;
	min-height: 42px;
	background: #20201E;
	border-radius: 8px;
	color: #A9FF00;
	font-size: 16px;
	font-weight: 400;
	font-family: 'PingFang SC', sans-serif;
	line-height: 1.4;
	border: none;
	outline: none;
	box-shadow: none;
	margin: 0;

	&.disabled {
		background: #3A3A38;
		color: #A9FF00;
		opacity: 1;
	}
}
</style>