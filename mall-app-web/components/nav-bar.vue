<template>
	<view>
		<view v-if="placeholder" :style="{ height: placeHolderHeight + 'px', backgroundColor: placeholderColor }" />
		<view class="nav-bar"
			:style="{ paddingTop: statusBarHeight + 'px', height: navBarHeight + 'px', backgroundColor: bgColor }">
			<view class="nav-back-btn" :style="{ height: navBarHeight + 'px' }" @click="goBack" v-if="back">
				<image class="back-image"
					src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/my/back_icon.png" />
			</view>
			<view v-if="hasSlot" :style="{ width: slotWidth }">
				<slot></slot>
			</view>
			<view v-else class="nav-title" :style="{ color: color }">{{ title }}</view>
		</view>
	</view>
</template>

<script>
	export default {
		props: {
			title: {
				type: String,
				default: '',
			},
			back: {
				type: Boolean,
				default: false,
			},
			color: {
				type: String,
				default: '#000000',
			},
			bgColor: {
				type: String,
				default: 'transparent',
			},
			placeholder: {
				type: Boolean,
				default: true,
			},
			placeholderColor: {
				type: String,
				default: 'transparent',
			},
			hasSlot: {
				type: Boolean,
				default: false,
			},
		},
		data() {
			return {
				statusBarHeight: 0,
				navBarHeight: 0,
				placeHolderHeight: 0,
				imageColor: '',
				slotWidth: 0,
			};
		},
		mounted() {
			const menuInfo = uni.getMenuButtonBoundingClientRect();
			const {
				statusBarHeight
			} = uni.getSystemInfoSync();
			const navBarHeight = (menuInfo.top - statusBarHeight) * 2 + menuInfo.height;
			const fullNavBarHeight = navBarHeight + statusBarHeight;
			const slotWidth = this.back ?
				`calc(${menuInfo.left}px - 64rpx - 40rpx)` :
				`${menuInfo.left}px`;
			this.statusBarHeight = statusBarHeight;
			this.navBarHeight = navBarHeight;
			this.placeHolderHeight = fullNavBarHeight;
			this.imageColor = this.color.replace('#', '');
			this.slotWidth = slotWidth;
			this.$emit('ready', fullNavBarHeight);
		},
		methods: {
			goBack() {
				uni.navigateBack({
					success: () => {},
					fail: () => {
						uni.reLaunch({
							url: '/pages/new_index/index',
						});
					},
				});
			},
		},
	};
</script>

<style scoped>
	.nav-bar {
		box-sizing: content-box;
		position: fixed;
		top: 0;
		left: 0;
		display: flex;
		align-items: center;
		width: 100vw;
		line-height: 1;
		z-index: 100;
		overflow: hidden;
	}

	.nav-back-btn {
		display: flex;
		align-items: center;
		justify-content: center;
		padding: 0 32rpx;
		z-index: 20;
	}

	.back-image {
		width: 28px;
		height: 28px;
	}

	.nav-title {
		position: absolute;
		left: 50%;
		transform: translateX(-50%);
		font-size: 18px;
		color: inherit;
	}
</style>