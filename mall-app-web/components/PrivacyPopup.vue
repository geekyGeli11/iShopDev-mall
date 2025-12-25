<template>
	<div v-if="visible" class="popup">
		<scroll-view scroll-y class="agreement-popup center">
			<view class="agreement-content">
				<view class="agreement-title">隐私协议</view>
				<view class="agreement-date">更新日期: 2024年12月21日</view>

				<view class="agreement-section">
					<view class="section-title">【引言】</view>
					<view class="section-content">
						您的信任对我们非常重要，我们深知个人信息对您的重要性，我们将按法律法规要求，采取相应安全保护措施，尽力保护您的个人信息安全可控。鉴于此，广横走文创电商科技有限公司下的广横走文创系统服务提供者（简称"我们"或"广横走文创"）制定本《隐私协议》（下称"本协议/本隐私权协议"）并提醒您:
					</view>
					<view class="section-content">
						本协议适用于广横走文创系统提供的所有产品和服务。我们有可能根据未来信息处理情境的变化更新或修改本协议，在使用广横走文创系统各项产品或服务前，请您务必仔细阅读并透彻理解本协议，在确认充分理解并同意后使用相关产品或服务。
					</view>
				</view>

				<view class="agreement-section">
					<view class="section-title">【第一部分 定义】</view>
					<view class="section-content">
						广横走文创系统包含域名为"www.haojiangzhenhao.com"的网站以及及广横走文创系统平台客户端，如微信公众号、小程序及手机Wap/App，以下简称"广横走文创"或"本平台"。
					</view>
					<view class="section-subcontent">
						<view>广横走文创系统服务提供者：指广横走文创系统的互联网信息及软件技术服务提供者广横走文创电商科技有限公司。</view>
						<view>个人信息：指以电子或者其他方式记录的能够单独或者与其他信息结合识别特定自然人身份或者反映特定自然人活动情况的各种信息。</view>
						<view>个人敏感信息：指包括身份证件号码、个人生物识别信息、银行账号、财产信息、行踪轨迹、交易信息、14岁以下（含）儿童信息等的个人信息。</view>
						<view>个人信息删除：指在实现日常业务功能所涉及的系统中去除个人信息的行为，使其保持不可被检索、访问的状态。</view>
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
	name: 'PrivacyPopup',
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