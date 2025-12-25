<script>
	/**
	 * vuex管理登陆状态，具体可以参考官方登陆模板示例
	 */
	import {
		mapMutations
	} from 'vuex';
	import updateManager from '@/utils/updateManager.js';
	import { verifyInviteParam, establishInviteRelation } from '@/api/invite.js';
import { handleLaunchRedirect, handleShowRedirect } from '@/utils/pageRedirect.js';
	
	export default {
		data() {
			return {
				lastProcessedInviteParam: null, // 上次处理的邀请参数
			}
		},
		methods: {
			...mapMutations(['login']),
			
			// 处理邀请参数
			handleInviteParam(options) {
				console.log('App启动参数:', options);

				let inviteParam = null;

				// 优先检查query参数中的邀请信息（小程序卡片分享）
				if (options.query && options.query.inviteParam) {
					const param = options.query.inviteParam;
					if (this.isValidInviteParam(param)) {
						inviteParam = param;
						console.log('从小程序卡片进入，邀请参数:', inviteParam);
					} else {
						console.log('query中的参数不是有效的邀请参数:', param);
					}
				}

				// 检查scene参数（小程序码）
				if (!inviteParam && options.scene) {
					const param = this.parseInviteFromScene(options.scene);
					if (param && this.isValidInviteParam(param)) {
						inviteParam = param;
						console.log('从小程序码进入，邀请参数:', inviteParam);
					} else {
						console.log('scene参数不是邀请参数，跳过处理:', options.scene);
					}
				}

				// 处理有效的邀请参数
				if (inviteParam && inviteParam !== this.lastProcessedInviteParam) {
					this.lastProcessedInviteParam = inviteParam;
					// 验证并保存邀请信息
					this.verifyAndSaveInvite(inviteParam);
				} else if (inviteParam === this.lastProcessedInviteParam) {
					console.log('邀请参数重复，跳过处理:', inviteParam);
				}
			},
			
			// 验证并保存邀请信息
			async verifyAndSaveInvite(inviteParam) {
				try {
					console.log('验证邀请参数:', inviteParam);
					
					// 调用API验证邀请参数
					const result = await verifyInviteParam({
						inviteParam: inviteParam,
						deviceInfo: JSON.stringify({
							platform: 'miniprogram',
							version: '1.0.0'
						}),
						sceneType: 1
					});
					
					if (result.code === 200 && result.data) {
						// 验证成功，保存邀请信息到本地存储
						const inviteInfo = {
							param: inviteParam,
							timestamp: new Date().getTime(),
							expireTime: new Date().getTime() + (3 * 24 * 60 * 60 * 1000), // 3天后过期
							verified: true
						};
						
						uni.setStorageSync('pendingInvite', inviteInfo);
						console.log('邀请参数验证成功，已保存到本地');
					} else {
						console.log('邀请参数验证失败:', result.message || '未知错误');
					}
				} catch (error) {
					console.error('验证邀请参数失败:', error);
				}
			},
			
			// 检查是否为有效的邀请参数（新格式）
			isValidInviteParam(param) {
				if (!param || typeof param !== 'string') {
					return false;
				}

				// 新格式：INVITE_v2_开头
				if (!param.startsWith('INVITE_v2_')) {
					console.log('参数不是邀请格式:', param);
					return false;
				}

				// 验证格式：INVITE_v2_{userId}_{timestamp}_{random}
				const parts = param.split('_');
				if (parts.length !== 5) {
					console.log('邀请参数格式不正确，分割后长度:', parts.length);
					return false;
				}

				const [prefix, version, userId, timestamp, random] = parts;

				// 检查前缀和版本
				if (prefix !== 'INVITE' || version !== 'v2') {
					console.log('邀请参数前缀或版本不正确:', prefix, version);
					return false;
				}

				// 检查用户ID和时间戳是否为数字
				if (!/^\d+$/.test(userId) || !/^\d+$/.test(timestamp)) {
					console.log('邀请参数中的用户ID或时间戳格式不正确:', userId, timestamp);
					return false;
				}

				// 检查随机字符串格式（6位字母数字）
				if (!/^[a-zA-Z0-9]{6}$/.test(random)) {
					console.log('邀请参数随机字符串格式不正确:', random);
					return false;
				}

				// 检查时间戳有效性（3天内）
				const now = Date.now();
				const paramTime = parseInt(timestamp);
				const threeDays = 3 * 24 * 60 * 60 * 1000;

				if (now - paramTime > threeDays) {
					console.log('邀请参数已过期:', new Date(paramTime), '当前时间:', new Date(now));
					return false;
				}

				console.log('邀请参数格式验证通过:', param);
				return true;
			},
			
			// 从场景值中解析邀请参数
			parseInviteFromScene(scene) {
				// 直接返回scene，由isValidInviteParam判断是否为有效的邀请参数
				console.log('解析场景值:', scene);
				return scene;
			},
			

		},
		onLaunch: function(options) {
			// 检测小程序版本更新
			updateManager.checkForUpdate();

			// 处理邀请参数
			this.handleInviteParam(options);

			// 检查页面有效性并处理重定向
			handleLaunchRedirect(options);

			let userInfo = uni.getStorageSync('userInfo') || '';
			if(userInfo.id){
				//更新登陆状态
				uni.getStorage({
					key: 'userInfo',
					success: (res) => {
						this.login(res.data);
					}
				});
			}

		},
		onShow: function(options) {
			console.log('App Show');
			// 检查是否需要提醒用户更新
			updateManager.checkUpdateReminder();

			// 处理邀请参数（从分享进入）
			this.handleInviteParam(options);

			// 检查页面有效性并处理重定向
			handleShowRedirect(options);
		},
		onHide: function() {
			console.log('App Hide')
		},
		// 全局分享配置 - 使所有页面都可分享
		// #ifdef MP-WEIXIN
		onShareAppMessage(res) {
			// 获取当前页面路径
			const pages = getCurrentPages();
			const currentPage = pages[pages.length - 1];
			const route = currentPage.route;
			const options = currentPage.options || {};
			
			// 构建分享参数
			let shareParams = '';
			if (Object.keys(options).length > 0) {
				shareParams = '?' + Object.keys(options).map(key => `${key}=${options[key]}`).join('&');
			}
			
			// 设置默认分享内容
			return {
				title: '广横走',
				path: `/${route}${shareParams}`,
				imageUrl: '', // 可以设置默认分享图片
				success: function(res) {
					uni.showToast({
						title: '分享成功'
					});
				},
				fail: function(res) {
					uni.showToast({
						title: '分享失败',
						icon: 'none'
					});
				}
			};
		},
		// 朋友圈分享
		onShareTimeline() {
			// 获取当前页面路径
			const pages = getCurrentPages();
			const currentPage = pages[pages.length - 1];
			
			// 设置默认朋友圈分享内容
			return {
				title: '广横走文创',
				query: '',
				imageUrl: ''
			};
		}
		// #endif
	}
</script>

<style lang='scss'>
	/*
		全局公共样式和字体图标
	*/
	@font-face {
		font-family: yticon;
		font-weight: normal;
		font-style: normal;
		src: url('https://at.alicdn.com/t/font_1078604_w4kpxh0rafi.ttf') format('truetype');
	}
	
	.main-color {
	  color: #0088FF;
	}
	
	.center {
	  display: flex;
	  align-items: center;
	  justify-content: center;
	}
	
	.search-box {
	  display: flex;
	  align-items: center;
	  margin: 0 32rpx;
	  padding: 0 20rpx;
	  height: 64rpx;
	  background: rgba(255, 255, 255, 0.8);
	  border-radius: 32rpx;
	
	  .search-icon {
	    width: 48rpx;
	    height: 48rpx;
	  }
	
	  .search-input {
	    flex: 1;
	    padding-left: 20rpx;
	    font-size: 28rpx;
	    color: #333333;
	  }
	}
	
	.search-input-box {
	  display: flex;
	  align-items: center;
	  margin-right: 32rpx;
	  height: 66rpx;
	  background: rgba(255, 255, 255, 0.8);
	  border-radius: 33rpx;
	
	  .search-icon {
	    margin: 0 8rpx 0 20rpx;
	    width: 48rpx;
	    height: 48rpx;
	  }
	
	  .search-input {
	    flex: 1;
	    font-size: 28rpx;
	  }
	
	  .search-btn {
	    margin-left: 8rpx;
	    padding: 0 20rpx 0 18rpx;
	    font-size: 28rpx;
	    color: #0088FF;
	    border-left: 1px solid #DDDDDD;
	  }
	}

	.translucent-overlay {
	  position: relative;
	
	  &::before {
	    content: '';
	    position: absolute;
	    top: 0;
	    left: 0;
	    right: 0;
	    bottom: 0;
	    background: rgba(0, 0, 0, 0.3);
	    z-index: 2;
	  }
	}

	.yticon {
		font-family: "yticon" !important;
		font-size: 16px;
		font-style: normal;
		-webkit-font-smoothing: antialiased;
		-moz-osx-font-smoothing: grayscale;
	}

	.icon-yiguoqi1:before {
		content: "\e700";
	}

	.icon-iconfontshanchu1:before {
		content: "\e619";
	}

	.icon-iconfontweixin:before {
		content: "\e611";
	}

	.icon-alipay:before {
		content: "\e636";
	}

	.icon-shang:before {
		content: "\e624";
	}

	.icon-shouye:before {
		content: "\e626";
	}

	.icon-shanchu4:before {
		content: "\e622";
	}

	.icon-xiaoxi:before {
		content: "\e618";
	}

	.icon-jiantour-copy:before {
		content: "\e600";
	}

	.icon-fenxiang2:before {
		content: "\e61e";
	}

	.icon-pingjia:before {
		content: "\e67b";
	}

	.icon-daifukuan:before {
		content: "\e68f";
	}

	.icon-pinglun-copy:before {
		content: "\e612";
	}

	.icon-dianhua-copy:before {
		content: "\e621";
	}

	.icon-shoucang:before {
		content: "\e645";
	}

	.icon-xuanzhong2:before {
		content: "\e62f";
	}

	.icon-gouwuche_:before {
		content: "\e630";
	}

	.icon-icon-test:before {
		content: "\e60c";
	}

	.icon-icon-test1:before {
		content: "\e632";
	}

	.icon-bianji:before {
		content: "\e646";
	}

	.icon-jiazailoading-A:before {
		content: "\e8fc";
	}

	.icon-zuoshang:before {
		content: "\e613";
	}

	.icon-jia2:before {
		content: "\e60a";
	}

	.icon-huifu:before {
		content: "\e68b";
	}

	.icon-sousuo:before {
		content: "\e7ce";
	}

	.icon-arrow-fine-up:before {
		content: "\e601";
	}

	.icon-hot:before {
		content: "\e60e";
	}

	.icon-lishijilu:before {
		content: "\e6b9";
	}

	.icon-zhengxinchaxun-zhifubaoceping-:before {
		content: "\e616";
	}

	.icon-naozhong:before {
		content: "\e64a";
	}

	.icon-xiatubiao--copy:before {
		content: "\e608";
	}

	.icon-shoucang_xuanzhongzhuangtai:before {
		content: "\e6a9";
	}

	.icon-jia1:before {
		content: "\e61c";
	}

	.icon-bangzhu1:before {
		content: "\e63d";
	}

	.icon-arrow-left-bottom:before {
		content: "\e602";
	}

	.icon-arrow-right-bottom:before {
		content: "\e603";
	}

	.icon-arrow-left-top:before {
		content: "\e604";
	}

	.icon-icon--:before {
		content: "\e744";
	}

	.icon-zuojiantou-up:before {
		content: "\e605";
	}

	.icon-xia:before {
		content: "\e62d";
	}

	.icon--jianhao:before {
		content: "\e60b";
	}

	.icon-weixinzhifu:before {
		content: "\e61a";
	}

	.icon-comment:before {
		content: "\e64f";
	}

	.icon-weixin:before {
		content: "\e61f";
	}

	.icon-fenlei1:before {
		content: "\e620";
	}

	.icon-erjiye-yucunkuan:before {
		content: "\e623";
	}

	.icon-Group-:before {
		content: "\e688";
	}

	.icon-you:before {
		content: "\e606";
	}

	.icon-forward:before {
		content: "\e607";
	}

	.icon-tuijian:before {
		content: "\e610";
	}

	.icon-bangzhu:before {
		content: "\e679";
	}

	.icon-share:before {
		content: "\e656";
	}

	.icon-yiguoqi:before {
		content: "\e997";
	}

	.icon-shezhi1:before {
		content: "\e61d";
	}

	.icon-fork:before {
		content: "\e61b";
	}

	.icon-kafei:before {
		content: "\e66a";
	}

	.icon-iLinkapp-:before {
		content: "\e654";
	}

	.icon-saomiao:before {
		content: "\e60d";
	}

	.icon-shezhi:before {
		content: "\e60f";
	}

	.icon-shouhoutuikuan:before {
		content: "\e631";
	}

	.icon-gouwuche:before {
		content: "\e609";
	}

	.icon-dizhi:before {
		content: "\e614";
	}

	.icon-fenlei:before {
		content: "\e706";
	}

	.icon-xingxing:before {
		content: "\e70b";
	}

	.icon-tuandui:before {
		content: "\e633";
	}

	.icon-zuanshi:before {
		content: "\e615";
	}

	.icon-zuo:before {
		content: "\e63c";
	}

	.icon-shoucang2:before {
		content: "\e62e";
	}

	.icon-shouhuodizhi:before {
		content: "\e712";
	}

	.icon-yishouhuo:before {
		content: "\e71a";
	}

	.icon-dianzan-ash:before {
		content: "\e617";
	}


	view,
	scroll-view,
	swiper,
	swiper-item,
	cover-view,
	cover-image,
	icon,
	text,
	rich-text,
	progress,
	button,
	checkbox,
	form,
	input,
	label,
	radio,
	slider,
	switch,
	textarea,
	navigator,
	audio,
	camera,
	image,
	video {
		box-sizing: border-box;
	}
	/* 骨架屏替代方案 */
	.Skeleton {
		background: #f3f3f3;
		padding: 20upx 0;
		border-radius: 8upx;
	}

	/* 图片载入替代方案 */
	.image-wrapper {
		font-size: 0;
		background: #f3f3f3;
		border-radius: 4px;

		image {
			width: 100%;
			height: 100%;
			transition: .6s;
			opacity: 0;

			&.loaded {
				opacity: 1;
			}
		}
	}

	.clamp {
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
		display: block;
	}

	.common-hover {
		background: #f5f5f5;
	}

	/*边框*/
	.b-b:after,
	.b-t:after {
		position: absolute;
		z-index: 3;
		left: 0;
		right: 0;
		height: 0;
		content: '';
		transform: scaleY(.5);
		border-bottom: 1px solid $border-color-base;
	}

	.b-b:after {
		bottom: 0;
	}

	.b-t:after {
		top: 0;
	}

	/* button样式改写 */
	uni-button,
	button {
		height: 80upx;
		line-height: 80upx;
		font-size: $font-lg + 2upx;
		font-weight: normal;

		&.no-border:before,
		&.no-border:after {
			border: 0;
		}
	}

	uni-button[type=default],
	button[type=default] {
		color: $font-color-dark;
	}

	/* input 样式 */
	.input-placeholder {
		color: #999999;
	}

	.placeholder {
		color: #999999;
	}

	/* 富文本相关样式 */
	.rich-text-wrapper {
	  width: 100%;
	  overflow: hidden;
	  box-sizing: border-box;
	  max-width: 690rpx;
	}

	.rich-text-content {
	  width: 100%;
	  overflow-x: hidden;
	  word-break: break-word;
	  box-sizing: border-box;
	  font-size: 28rpx;
	  line-height: 1.8;
	}

	/* 直接对图片和段落元素应用样式，不使用深度选择器 */
	.rich-text-content image,
	.rich-text-content img {
	  max-width: 100% !important;
	  height: auto !important;
	  display: block !important;
	  margin: 0 auto !important;
	  width: auto !important;
	  box-sizing: border-box !important;
	}

	.rich-text-content p {
	  margin: 0 !important;
	  padding: 0 !important;
	  margin-bottom: 20rpx !important;
	  width: 100% !important;
	  overflow-x: hidden !important;
	  box-sizing: border-box !important;
	}

	.rich-text-content * {
	  max-width: 100% !important;
	  box-sizing: border-box !important;
	  overflow-wrap: break-word !important;
	}

	/* 全局隐藏swiper默认指示器 - 小程序兼容 */
	.uni-swiper-dots {
	  display: none !important;
	  opacity: 0 !important;
	  visibility: hidden !important;
	}

	.uni-swiper-dot {
	  display: none !important;
	  opacity: 0 !important;
	  visibility: hidden !important;
	  width: 0 !important;
	  height: 0 !important;
	}

	/* 针对预览页面的特殊处理 */
	.diy-preview-container .uni-swiper-dots,
	.diy-preview-container .uni-swiper-dot {
	  display: none !important;
	}
</style>
