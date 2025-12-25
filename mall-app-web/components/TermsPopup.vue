<template>
	<div v-if="visible" class="popup">
		<scroll-view scroll-y class="agreement-popup center">
			<view class="agreement-content">
				<view class="agreement-title">用户服务协议</view>
				<view class="agreement-date">更新日期: 2024年10月21日</view>

				<view class="agreement-section">
					<view class="section-title">【引言】</view>
					<view class="section-content">欢迎访问广横走文创系统并使用我们提供的产品和服务。</view>
					<view class="section-content">
						在使用广横走文创系统的服务前，请您务必仔细阅读并透彻理解本广横走文创系统服务协议(以下或简称"服务协议")，在确认充分理解后选择接受或不接受本服务协议；一旦您勾选"同意协议"或开始以任何方式使用广横走文创系统服务，即表明您已阅读并同意受本服务协议的约束。如您不同意本服务协议或其中任何条款约定，您应不再进行下一步或停止登录程序。
					</view>
				</view>

				<view class="agreement-section">
					<view class="section-title">【签约主体以及协议范围】</view>
					<view class="section-content">
						广横走文创系统服务协议是您与汕头市广横走文创电商科技有限公司（以下简称"本公司"）就使用广横走文创系统服务（包含为您提供网站页面信息浏览、账号登录服务等）而订立的有效合约。
					</view>
					<view class="section-content">
						广横走文创系统包含域名为"www.haojiangzhenhao.com"的网站以及及广横走文创系统平台客户端，如微信公众号、小程序及手机Wap/App，以下简称"广横走文创"或"本平台"。
					</view>
				</view>

				<view class="agreement-section">
					<view class="section-title">一、总则</view>
					<view class="section-subcontent">
						<view>1.1
							为维护网上公共秩序和社会稳定,在使用本公司提供的任何服务时,请您自觉遵守中华人民共和国相关法律、法规、部门规章及其他立法机关、行政机关根据法律授权制定的具有强制约束力的法律文件。
						</view>
						<view>1.2 根据国家法律法规变化及网络运营需要，本公司有权对本协议条款不时地进行修改。用户可随时登陆查阅最新协议；用户应不时关注并阅读最新版的协议及相关条款。</view>
					</view>
				</view>

				<view class="agreement-footer">
					<view class="company-name">广横走文创电商科技有限公司</view>
					<view class="sign-date">2024年12月21日</view>
				</view>
			</view>
		</scroll-view>
		<view class="popup-btn" @tap="close">关闭</view>
	</div>
</template>

<script>
export default {
	name: 'TermsPopup',
	props: {
		visible: {
			type: Boolean,
			default: false
		}
	},
	methods: {
		close() {
			this.$emit('close');
		}
	}
}
</script>

<style lang="scss" scoped>
.popup {
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	z-index: 1100;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	background-color: rgba(0, 0, 0, 0.5);

	.agreement-popup {
		width: 700rpx;
		height: 85vh;
		max-height: 1200rpx;
		background-color: #fff;
		border-radius: 20rpx;
		display: flex;
		flex-direction: column;
		overflow: hidden;
		z-index: 1101;
	}

	.agreement-content {
		padding: 40rpx;
		flex: 1;
		overflow-y: auto;

		.agreement-title {
			font-size: 36rpx;
			font-weight: bold;
			text-align: center;
			margin-bottom: 20rpx;
		}

		.agreement-date {
			font-size: 24rpx;
			color: #999;
			text-align: center;
			margin-bottom: 40rpx;
		}

		.agreement-section {
			margin-bottom: 30rpx;

			.section-title {
				font-size: 32rpx;
				font-weight: bold;
				margin-bottom: 20rpx;
				color: #333;
			}

			.section-content {
				font-size: 28rpx;
				color: #666;
				line-height: 1.6;
				margin-bottom: 20rpx;
			}

			.section-subcontent {
				font-size: 28rpx;
				color: #666;
				line-height: 1.6;
				padding-left: 20rpx;

				view {
					margin-bottom: 16rpx;
				}
			}
		}

		.agreement-footer {
			text-align: right;
			margin-top: 40rpx;

			.company-name {
				font-weight: bold;
				font-size: 28rpx;
				margin-bottom: 10rpx;
			}

			.sign-date {
				font-size: 24rpx;
				color: #999;
			}
		}
	}

	.popup-btn {
		width: 240rpx;
		height: 80rpx;
		line-height: 80rpx;
		text-align: center;
		background-color: #A9FF00;
		color: #000;
		font-size: 28rpx;
		font-weight: 500;
		border-radius: 40rpx;
		margin-top: 30rpx;
		margin-bottom: 30rpx;
		box-shadow: 0 6rpx 16rpx rgba(169, 255, 0, 0.3);

		&:active {
			opacity: 0.85;
			transform: translateY(2rpx);
			box-shadow: 0 2rpx 8rpx rgba(169, 255, 0, 0.3);
		}

		transition: all 0.2s ease;
	}
}
</style> 