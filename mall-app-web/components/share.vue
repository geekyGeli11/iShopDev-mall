<template>
	<view v-if="show" class="mask" @click="toggleMask" @touchmove.stop.prevent="stopPrevent"
		:style="{backgroundColor: backgroundColor}"
	>
		<view 
			class="mask-content"
			@click.stop.prevent="stopPrevent"
			:style="{transform: transform}"
		>
			<!-- 分享海报卡片 -->
			<view class="poster-card" ref="posterCard">				
				<view class="product-container">
					<!-- 背景图片 -->
					<image class="share-bg" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/share-poster.png" mode="aspectFill"></image>
					
					<!-- 商品图片 -->
					<image class="product-image" :src="product.pic" mode="aspectFill"></image>
					
					<!-- 商品信息 -->
					<view class="share-poster-product-info">
						<view class="share-poster-product-title">{{product.title}}</view>

						<view class="share-poster-product-tags">
							<view class="share-poster-tag points">可得积分{{product.points}}</view>
							<view class="share-poster-tag seconds" v-if="product.limited">秒杀</view>
							<view class="share-poster-tag recommend" v-if="product.recommended">推荐</view>
						</view>

						<view class="share-poster-price-qrcode-container">
							<view class="share-poster-product-price">
								<text class="share-poster-symbol">¥</text>
								<text class="share-poster-price-value">{{product.price}}</text>
								<text class="share-poster-unit">起/份</text>
							</view>
							<!-- 二维码图片 -->
							<image class="share-poster-qr-code" :src="product.qrcode" mode="aspectFill"></image>
						</view>
					</view>
					<!-- 底部文案 -->
					<view class="poster-footer">
						<view class="user-recommend" v-if="product.userName || (user && user.name)">
							<text>{{product.userName || user.name}}的好物推荐</text>
						</view>
					</view>
				</view>							
			</view>
			
			<!-- 下载海报按钮 -->
			<view class="download-btn-container">
				<view class="download-poster-btn" @click="downloadPoster">
					<text>下载海报</text>
				</view>
				
				<view class="divider-container">
					<view class="divider"></view>
					<text class="divider-text">或</text>
					<view class="divider"></view>
				</view>
			</view>

			<!-- 分享按钮列表 -->
			<view class="share-list">
				<view class="share-item" @click="shareToFriend('朋友圈')">
					<image src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/product/friends.png" mode="aspectFit"></image>
					<text>朋友圈</text>
				</view>
				<button class="share-item" open-type="share" @click="prepareShare('微信好友')">
					<image src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/product/friend.png" mode="aspectFit"></image>
					<text>分享好友</text>
				</button>
				<view class="share-item" @click="copyLink">
					<image src="https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/product/copylink.png" mode="aspectFit"></image>
					<text>复制链接</text>
				</view>
			</view>
		</view>
		
		<!-- 用于生成海报的隐藏canvas，使用type="2d"支持同层渲染 -->
		<canvas type="2d" id="posterCanvas" class="poster-canvas"></canvas>
	</view>
</template>

<script>
	// 使用条件编译避免小程序环境的导入错误
	// #ifndef MP-WEIXIN
	import html2canvas from '@/js_sdk/html2canvas/index.js'
	// #endif

	export default {
		data() {
			return {
				transform: 'translateY(100%)',
				timer: 0,
				backgroundColor: 'rgba(0,0,0,0)',
				show: false,
				config: {},
				canvasContext: null,
				defaultQrcode: 'https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/product/sales.png',
				shareType: '', // 记录分享类型
				user: {
					name: '广横走文创' // 默认用户名
				}
			};
		},
		props:{
			contentHeight:{
				type: Number,
				default: 700
			},
			//是否是tabbar页面
			hasTabbar:{
				type: Boolean,
				default: false
			},
			shareList:{
				type: Array,
				default: function(){
					return [];
				}
			},
			// 新增商品信息属性
			product: {
				type: Object,
				default: function() {
					return {
						id: '', // 商品ID，用于构建分享链接
						title: '鲍祥潮式腊味豪华礼盒(1000g)',
						price: '198',
						points: '100',
						limited: true,
						recommended: true,
						description: '潮汕风味 浸江酿造 全味共享',
						qrcode: '',
						pic: '', // 商品主图
						brandName: '潼江珍好',
						// 推广相关参数
						distributorId: null, // 分销商ID
						isPromotion: false // 是否为推广分享
					}
				}
			},
		},
		created() {
			this.config = {
				backgroundColor: 'rgba(0,0,0,.6)',
				transform: 'translateY(0)',
			}
		},
		// 增加小程序分享能力
		onShareAppMessage(res) {
			// 来自页面内分享按钮
			let path = `/pages/product/product?goods_id=${this.product.id}`;

			// 如果是推广分享，添加推广参数
			if (this.product.isPromotion && this.product.distributorId) {
				path += `&distributorId=${this.product.distributorId}&fromPromotion=true`;
			}

			let shareObj = {
				title: this.product.title,
				path: path,
				imageUrl: this.product.pic,
				desc: this.product.description,
				success: function(res) {
					uni.showToast({
						title: '分享成功'
					})
				},
				fail: function(res) {
					uni.showToast({
						title: '分享失败',
						icon: 'none'
					})
				}
			}
			return shareObj;
		},
		methods:{
			toggleMask(nickname){
				//防止高频点击
				if(this.timer == 1){
					return;
				}
				this.timer = 1;
				setTimeout(()=>{
					this.timer = 0;
				}, 500)
				
				// 如果传入昵称，则更新用户名
				if (nickname) {
					// 确保user对象存在
					if (!this.user) {
						this.user = {};
					}
					this.user.name = nickname;
				}
				
				if(this.show){
					this.transform = 'translateY(100%)';
					this.backgroundColor = 'rgba(0,0,0,0)';
					setTimeout(()=>{
						this.show = false;
						this.hasTabbar && uni.showTabBar();
					}, 300)
					return;
				}
				
				this.show = true;
				//等待mask重绘完成执行
				if(this.hasTabbar){
					uni.hideTabBar({
						success: () => {
							setTimeout(()=>{
								this.backgroundColor = this.config.backgroundColor;
								this.transform = 'translateY(0)';
							}, 50)
						}
					});
				}else{
					setTimeout(()=>{
						this.backgroundColor = this.config.backgroundColor;
						this.transform = 'translateY(0)';
					}, 50)
				}
			},
			//防止冒泡和滚动穿透
			stopPrevent(){},
			//为分享准备数据
			prepareShare(type) {
				// 记录分享类型，在onShareAppMessage中使用
				this.shareType = type;
				// 不需要关闭弹窗，系统会自动处理分享
			},
			
			//分享操作
			shareToFriend(type){
				if (type === '朋友圈') {
					// #ifdef MP-WEIXIN
					// 在微信小程序中，需要调用专门的分享到朋友圈的API
					wx.showShareMenu({
						withShareTicket: true,
						menus: ['shareTimeline'],
						success: () => {
							this.$api.msg('请点击右上角···，选择分享到朋友圈');
							this.toggleMask();
						}
					});
					// #endif
					
					// #ifndef MP-WEIXIN
					this.$api.msg('当前环境不支持分享到朋友圈');
					this.toggleMask();
					// #endif
				} else if (type === '微信好友') {
					// 这个分支代码已不再需要，通过button的open-type="share"属性自动处理
					// 保留是为了兼容非微信小程序环境
					// #ifndef MP-WEIXIN
					uni.share({
						provider: 'weixin',
						scene: 'WXSceneSession',
						type: 0,
						title: this.product.title,
						summary: this.product.description || '',
						imageUrl: this.product.pic || '',
						href: `/pages/product/detail?id=${this.product.id}`,
						success: () => {
							this.$api.msg('分享成功');
							this.toggleMask();
						},
						fail: () => {
							this.$api.msg('分享失败');
						}
					});
					// #endif
				}
			},
			// 复制链接
			copyLink() {
				uni.showLoading({
					title: '生成链接中...'
				});
				
				// 小程序环境中的页面路径
				let pageUrl = `/pages/product/detail?id=${this.product.id}`;
				
				// 生成小程序路径
				try {
					// 由于小程序限制，用户只能复制文本，不能直接跳转
					// 我们生成一个包含商品信息的文本
					const productInfo = `${this.product.title} - ¥${this.product.price} 查看详情，请在微信搜索"${uni.getAccountInfoSync().miniProgram.appName || '商城'}"小程序`;
					
					uni.hideLoading();
					uni.setClipboardData({
						data: productInfo,
						success: () => {
							this.$api.msg('商品信息已复制');
							this.toggleMask();
						}
					});
				} catch (error) {
					console.error('复制链接失败', error);
					// 失败时使用备用方案
					const productLink = `${this.product.title} 价格:¥${this.product.price}`;
					
					uni.hideLoading();
					uni.setClipboardData({
						data: productLink,
						success: () => {
							this.$api.msg('商品信息已复制');
							this.toggleMask();
						}
					});
				}
			},
			// 下载海报
			downloadPoster() {
				// 通过ref获取组件实例
				const query = uni.createSelectorQuery().in(this);
				query.select('.poster-card').boundingClientRect(data => {
					if (!data) {
						this.$api.msg('获取元素失败');
						return;
					}
					
					// 生成海报图片
					uni.showLoading({
						title: '生成海报中...'
					});
					
					// #ifdef MP-WEIXIN
					// 在微信小程序环境下，直接使用Canvas 2D API
					this.generatePosterWithCanvas2D();
					// #endif
					
					// #ifndef MP-WEIXIN
					// 在非微信小程序环境下，尝试使用html2canvas
					if(typeof html2canvas !== 'undefined') {
						html2canvas(this.$refs.posterCard, {
							useCORS: true,
							scale: 2,
							logging: false
						}).then(canvas => {
							const imgUrl = canvas.toDataURL('image/png');
							// 保存图片
							uni.hideLoading();
							this.$api.msg('海报已生成，长按图片可保存');
							// 这里可以显示海报图片
						}).catch(err => {
							console.error('HTML2Canvas错误:', err);
							// 失败时尝试使用Canvas 2D API
							this.generatePosterWithCanvas2D();
						});
					} else {
						// html2canvas不可用时使用Canvas 2D API
						this.generatePosterWithCanvas2D();
					}
					// #endif
				}).exec();
			},
			// 使用Canvas 2D API生成海报
			generatePosterWithCanvas2D() {
				const query = uni.createSelectorQuery().in(this);
				query.select('#posterCanvas')
					.fields({ node: true, size: true })
					.exec((res) => {
						if (!res[0] || !res[0].node) {
							uni.hideLoading();
							this.$api.msg('获取Canvas节点失败');
							return;
						}
						
						const canvas = res[0].node;
						const ctx = canvas.getContext('2d');
						
						// 设置画布尺寸，保持与海报卡片比例一致
						canvas.width = 520;
						canvas.height = 956;
						
						// 先加载背景图片
						const bgImage = canvas.createImage();
						bgImage.onload = () => {
							// 绘制背景
							ctx.drawImage(bgImage, 0, 0, canvas.width, canvas.height);
							
							// 如果有商品主图，添加商品图片
							if (this.product.pic) {
								const productImg = canvas.createImage();
								productImg.onload = () => {
									// 绘制商品图片（居中显示）
									const imgWidth = canvas.width - 60; // 左右各留30px边距
									const imgHeight = 460 * (canvas.width / 520); // 按比例计算高度
									
									const imgX = 30; // 左边距
									const imgY = 100; // 与UI一致的顶部距离
									
									ctx.drawImage(productImg, imgX, imgY, imgWidth, imgHeight);
									
									// 绘制商品信息
									this.drawPosterContent(ctx, canvas, imgY + imgHeight);
								};
								productImg.onerror = () => {
									console.error('商品图片加载失败');
									this.drawPosterContent(ctx, canvas, 100 + 460 * (canvas.width / 520));
								};
								productImg.src = this.product.pic;
							} else {
								this.drawPosterContent(ctx, canvas, 100 + 460 * (canvas.width / 520));
							}
						};
						
						// 加载背景图
						bgImage.src = 'https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/distribution/share-poster.png';
						
						// 处理背景图加载失败
						bgImage.onerror = () => {
							console.error('背景图片加载失败');
							// 使用填充色作为备用
							ctx.fillStyle = '#E2F1F5';
							ctx.fillRect(0, 0, canvas.width, canvas.height);
							
							// 继续处理其他内容（图片加载失败时）
							if (this.product.pic) {
								const productImg = canvas.createImage();
								productImg.onload = () => {
									const imgWidth = canvas.width - 60;
									const imgHeight = 460 * (canvas.width / 520);
									const imgX = 30;
									const imgY = 100;
									ctx.drawImage(productImg, imgX, imgY, imgWidth, imgHeight);
									this.drawPosterContent(ctx, canvas, imgY + imgHeight);
								};
								productImg.onerror = () => {
									this.drawPosterContent(ctx, canvas, 100 + 460 * (canvas.width / 520));
								};
								productImg.src = this.product.pic;
							} else {
								this.drawPosterContent(ctx, canvas, 100 + 460 * (canvas.width / 520));
							}
						};
					});
			},
			// 绘制海报内容
			drawPosterContent(ctx, canvas, startY) {
				const paddingX = 30; // 与UI一致的左右内边距
				
				// 先绘制商品信息区域的白色背景 - 与商品图片一样宽，只有底部有圆角
				ctx.fillStyle = '#FFFFFF';
				const infoBoxHeight = 220; // 白色背景区域高度
				const infoBoxWidth = canvas.width - paddingX * 2; // 与商品图片同宽
				const infoBoxX = paddingX;
				const cornerRadius = 12; // 圆角大小
				
				// 绘制只有底部有圆角的矩形
				ctx.beginPath();
				// 左上角 - 直角
				ctx.moveTo(infoBoxX, startY);
				// 上边
				ctx.lineTo(infoBoxX + infoBoxWidth, startY);
				// 右上角 - 直角
				ctx.lineTo(infoBoxX + infoBoxWidth, startY + infoBoxHeight - cornerRadius);
				// 右下角 - 圆角
				ctx.arcTo(infoBoxX + infoBoxWidth, startY + infoBoxHeight, infoBoxX + infoBoxWidth - cornerRadius, startY + infoBoxHeight, cornerRadius);
				// 底边
				ctx.lineTo(infoBoxX + cornerRadius, startY + infoBoxHeight);
				// 左下角 - 圆角
				ctx.arcTo(infoBoxX, startY + infoBoxHeight, infoBoxX, startY + infoBoxHeight - cornerRadius, cornerRadius);
				// 左边
				ctx.lineTo(infoBoxX, startY);
				ctx.closePath();
				ctx.fill();
				
				// 绘制标题
				ctx.font = 'bold 32px sans-serif';
				ctx.fillStyle = '#333333';
				ctx.textAlign = 'left';
				ctx.fillText(this.product.title, paddingX + 20, startY + 40, infoBoxWidth - 40);
				
				// 绘制标签 - 往上移，靠近标题
				const tags = [];
				if (this.product.points) tags.push(`可得积分${this.product.points}`);
				if (this.product.limited) tags.push('秒杀');
				if (this.product.recommended) tags.push('推荐');
				
				let tagY = startY + 60; // 减小与标题的距离
				if (tags.length > 0) {
					let tagX = paddingX + 20; // 与标题对齐
					for (let i = 0; i < tags.length; i++) {
						const tagWidth = 110;
						const tagHeight = 30;
						const tagRadius = 6; // 标签圆角
						
						// 绘制带圆角的标签背景
						ctx.fillStyle = i === 0 ? '#FFF0F0' : i === 1 ? '#FFDDE5' : '#FFD700';
						ctx.beginPath();
						ctx.moveTo(tagX + tagRadius, tagY);
						ctx.arcTo(tagX + tagWidth, tagY, tagX + tagWidth, tagY + tagRadius, tagRadius);
						ctx.arcTo(tagX + tagWidth, tagY + tagHeight, tagX + tagWidth - tagRadius, tagY + tagHeight, tagRadius);
						ctx.arcTo(tagX, tagY + tagHeight, tagX, tagY + tagHeight - tagRadius, tagRadius);
						ctx.arcTo(tagX, tagY, tagX + tagRadius, tagY, tagRadius);
						ctx.closePath();
						ctx.fill();
						
						// 标签文字
						ctx.font = '20px sans-serif';
						ctx.fillStyle = i === 0 ? '#F15B34' : i === 1 ? '#FF3366' : '#333';
						ctx.textAlign = 'center';
						ctx.fillText(tags[i], tagX + 55, tagY + 22);
						
						tagX += 120;
					}
				}
				
				// 绘制价格和二维码在同一行 - 往下移动
				const priceY = tagY + 90; // 增加与标签的距离
				
				// 绘制价格 - 左对齐
				ctx.font = 'bold 46px sans-serif';
				ctx.fillStyle = '#000000';
				ctx.textAlign = 'left';
				ctx.fillText(`¥${this.product.price}`, paddingX + 20, priceY);
				
				// 计算价格文字宽度
				const priceWidth = ctx.measureText(`¥${this.product.price}`).width;
				
				// 绘制"起/份"，确保不重叠
				ctx.font = '24px sans-serif';
				ctx.fillStyle = '#666666';
				ctx.textAlign = 'left';
				ctx.fillText('起/份', paddingX + 20 + priceWidth + 10, priceY);
				
				// 绘制二维码 - 右侧位置
				if (this.product.qrcode || this.defaultQrcode) {
					const qrImage = canvas.createImage();
					qrImage.onload = () => {
						const qrSize = 88 * (canvas.width / 520); // 按比例计算二维码大小
						const qrX = infoBoxX + infoBoxWidth - qrSize - 20; // 右侧位置，留出边距
						const qrY = priceY - qrSize + 20; // 垂直对齐在价格行
						
						ctx.drawImage(qrImage, qrX, qrY, qrSize, qrSize);
						
						// 绘制底部推荐文案
						this.drawRecommendText(ctx, canvas);
						
						// 生成海报
						this.generateFinalPoster(canvas);
					};
					qrImage.onerror = () => {
						console.error('二维码图片加载失败');
						
						// 绘制底部推荐文案
						this.drawRecommendText(ctx, canvas);
						
						this.generateFinalPoster(canvas);
					};
					qrImage.src = this.product.qrcode || this.defaultQrcode;
				} else {
					// 绘制底部推荐文案
					this.drawRecommendText(ctx, canvas);
					
					this.generateFinalPoster(canvas);
				}
			},
			// 新增方法：绘制底部推荐文案
			drawRecommendText(ctx, canvas) {
				if (this.user && this.user.name) {
					// 取用户名和product中的userName属性，以product.userName优先
					const userName = this.product.userName || this.user.name;
					
					// 绘制推荐文案背景
					const textContent = `${userName}的好物推荐`;
					const textWidth = Math.min(ctx.measureText(textContent).width + 60, canvas.width - 80);
					const textHeight = 40;
					const textX = (canvas.width - textWidth) / 2;
					const textY = canvas.height - 60;
					
					// 绘制带圆角的白色背景
					ctx.fillStyle = '#FFFFFF';
					const cornerRadius = 20;
					
					ctx.beginPath();
					ctx.moveTo(textX + cornerRadius, textY);
					ctx.arcTo(textX + textWidth, textY, textX + textWidth, textY + textHeight, cornerRadius);
					ctx.arcTo(textX + textWidth, textY + textHeight, textX + textWidth - cornerRadius, textY + textHeight, cornerRadius);
					ctx.arcTo(textX, textY + textHeight, textX, textY + textHeight - cornerRadius, cornerRadius);
					ctx.arcTo(textX, textY, textX + cornerRadius, textY, cornerRadius);
					ctx.closePath();
					
					// 添加阴影效果
					ctx.shadowColor = 'rgba(0, 0, 0, 0.1)';
					ctx.shadowBlur = 4;
					ctx.shadowOffsetX = 0;
					ctx.shadowOffsetY = 2;
					ctx.fill();
					
					// 重置阴影设置
					ctx.shadowColor = 'transparent';
					ctx.shadowBlur = 0;
					ctx.shadowOffsetX = 0;
					ctx.shadowOffsetY = 0;
					
					// 绘制文本
					ctx.font = 'bold 28px sans-serif';
					ctx.fillStyle = '#333333';
					ctx.textAlign = 'center';
					ctx.fillText(textContent, canvas.width / 2, textY + 28);
				}
			},
			// 生成最终海报并保存
			generateFinalPoster(canvas) {
				// 将canvas转为图片并保存
				wx.canvasToTempFilePath({
					canvas: canvas,
					success: (res) => {
						// 保存图片到相册
						uni.saveImageToPhotosAlbum({
							filePath: res.tempFilePath,
							success: () => {
								uni.hideLoading();
								this.$api.msg('海报已保存到相册');
								this.toggleMask();
							},
							fail: (err) => {
								uni.hideLoading();
								this.$api.msg('保存失败，请检查权限');
								console.error(err);
							}
						});
					},
					fail: (err) => {
						uni.hideLoading();
						this.$api.msg('生成海报失败');
						console.error(err);
					}
				});
			}
		}
	}
</script>

<style lang='scss'>
	/* 使用更高特异性来避免被product页面覆盖 */
	.mask[class]{
		position: fixed !important;
		left: 0 !important;
		top: 0 !important;
		right: 0 !important;
		bottom: 0 !important;
		width: 100% !important;
		height: 100% !important;
		z-index: 9999 !important; /* 更高的z-index值 */
		transition: .3s !important;
		background-color: rgba(0,0,0,.6) !important; /* 直接设置背景颜色，不使用backgroundColor变量 */
		
		&::before {
			content: "";
			position: absolute;
			left: 0;
			top: 0;
			right: 0;
			bottom: 0;
			background: rgba(0,0,0,.6);
			z-index: -1;
		}
	}
	
	.mask-content[class]{
		position: absolute !important;
		left: 0 !important;
		right: 0 !important;
		bottom: 0 !important;
		width: 100% !important;
		height: auto !important; /* 改为自适应高度 */
		max-height: 100vh !important; /* 最大高度限制 */
		transition: .3s !important;
		background: #ffffff !important;
		display: flex !important;
		flex-direction: column !important;
		align-items: center !important;
		padding: 40rpx 0 0 0 !important; /* 只保留顶部内边距 */
		overflow-y: auto !important;
		
		/* 海报卡片样式 */
		.poster-card {
			width: 520rpx;
			height: 956rpx;
			background-color: transparent;
			border-radius: 24rpx;
			overflow: hidden;
			margin-bottom: 30rpx; /* 减少底部间距 */
			flex-shrink: 0; /* 防止被压缩 */
			
			.poster-header {
				padding: 30rpx 0;
				text-align: center;
				font-size: 36rpx;
				font-weight: bold;
				color: #333;
			}
			
			.product-container {
				padding: 0 30rpx;
				position: relative;
				
				.share-bg {
					position: absolute;
					top: 0;
					left: 0;
					width: 520rpx;
					height: 956rpx;
					z-index: 0;
				}
				
				.product-image {
					width: 100%;
					height: 460rpx;
					border-radius: 12rpx 12rpx 0 0; /* 修改圆角，只在上方有圆角 */
					object-fit: contain;
					position: relative;
					top: 100rpx; /* 使用top替代margin-top，只让图片下移 */
					margin-top: 0; /* 清除margin-top，防止容器整体下移 */
					margin-bottom: -2rpx; /* 使用负边距强制连接 */
					z-index: 1;
					display: block; /* 确保是块级元素 */
					font-size: 0; /* 消除潜在的行内元素间隙 */
				}
				
				.share-poster-product-info {
					background-color: #fff !important;
					position: relative !important;
					z-index: 1 !important;
					margin-top: 98rpx !important; /* 调整顶部边距，补偿图片下移的距离 */
					padding: 20rpx 30rpx !important; /* 增加左右内边距 */
					border-radius: 0 0 12rpx 12rpx !important; /* 只在底部有圆角 */
					border-top: 2rpx solid #fff !important; /* 添加与背景相同颜色的边框，消除可能的间隙 */
					
					.share-poster-product-title {
						font-size: 32rpx !important;
						font-weight: bold !important;
						color: #333 !important;
						text-align: left !important; /* 改为左对齐 */
						margin-bottom: 20rpx !important;
						line-height: 1.3 !important;
					}

					.share-poster-product-tags {
						display: flex !important;
						justify-content: flex-start !important; /* 改为左对齐 */
						margin-bottom: 20rpx !important;
						flex-wrap: wrap !important; /* 允许标签换行 */

						.share-poster-tag {
							margin: 0 10rpx 10rpx 0 !important; /* 修改间距，右侧和底部有间距 */
							padding: 4rpx 16rpx !important;
							border-radius: 6rpx !important;
							font-size: 24rpx !important;

							&.points {
								background-color: #FFF0F0 !important;
								color: #F15B34 !important;
							}

							&.seconds {
								background-color: #FFDDE5 !important;
								color: #FF3366 !important;
							}

							&.recommend {
								background-color: #FFD700 !important;
								color: #333 !important;
							}
						}
					}
					
					.share-poster-price-qrcode-container {
						display: flex !important;
						justify-content: space-between !important; /* 价格靠左，二维码靠右 */
						align-items: center !important; /* 垂直居中对齐 */
						margin-top: 20rpx !important;

						.share-poster-product-price {
							text-align: left !important; /* 价格左对齐 */

							.share-poster-symbol {
								font-size: 32rpx !important;
								color: #000 !important;
								font-weight: bold !important;
							}

							.share-poster-price-value {
								font-size: 48rpx !important;
								color: #000 !important;
								font-weight: bold !important;
							}

							.share-poster-unit {
								font-size: 24rpx !important;
								color: #666 !important;
								margin-left: 4rpx !important;
							}
						}

						.share-poster-qr-code {
							width: 88rpx !important;
							height: 88rpx !important;
							flex-shrink: 0 !important;

							image {
								width: 100% !important;
								height: 100% !important;
							}
						}
					}
				}
			}
			
			.poster-footer {
				position: absolute;
				bottom: -120rpx;
				left: 0;
				width: 100%;
				text-align: center;
				z-index: 10;
				
				.user-recommend {
					margin-top: 30rpx;
					font-size: 28rpx;
					font-weight: bold;
					color: #333;
					background-color: #ffffff;
					border-radius: 50rpx;
					display: inline-block;
					padding: 10rpx 30rpx;
				}
			}
		}
		
		/* 下载按钮区域 */
		.download-btn-container {
			width: 690rpx;
			display: flex;
			flex-direction: column;
			align-items: center;
			margin-bottom: 20rpx; /* 减少底部间距 */
			
			.download-poster-btn {
				background: $main-color;
				color: #000;
				font-size: 32rpx;
				padding: 0; /* 移除内边距 */
				border-radius: 100rpx;
				margin-bottom: 30rpx;
				width: 100%; /* 设置宽度占满容器 */
				text-align: center; /* 文字居中 */
				height: 88rpx; /* 固定高度 */
				line-height: 88rpx; /* 行高与高度一致，垂直居中 */
				box-sizing: border-box; /* 确保padding不会增加总宽度 */
				display: flex; /* 使用flex布局 */
				justify-content: center; /* 水平居中 */
				align-items: center; /* 垂直居中 */
				
				text {
					/* 确保文字垂直居中 */
					height: auto;
					line-height: normal;
				}
			}
			
			.divider-container {
				width: 100%;
				display: flex;
				align-items: center;
				justify-content: center;
				margin: 20rpx 0;
				
				.divider {
					height: 1px;
					background-color: #ccc;
					flex: 1;
				}
				
				.divider-text {
					padding: 0 20rpx;
					color: #666;
					font-size: 28rpx;
				}
			}
		}
		
		/* 分享按钮列表 */
		.share-list {
			display: flex;
			justify-content: space-around;
			width: 100%;
			padding: 30rpx 0; /* 减少上下内边距 */
			background-color: #ffffff;
			/* 移除固定定位，改为正常文档流 */
			margin-top: auto; /* 自动推到底部 */
			
			/* 微信分享按钮样式重置 */
			button.share-item {
				background-color: transparent !important;
				line-height: normal !important;
				border: none !important;
				padding: 0 !important;
				margin: 0 !important;
				font-size: inherit !important;
				color: inherit !important;
				border-radius: 0 !important;
				box-sizing: border-box !important;
				width: auto !important;
				height: auto !important;
				display: flex !important;
				flex-direction: column !important;
				align-items: center !important;
				
				&::after {
					border: none !important;
				}
			}
		}
		
		.share-item {
			display: flex;
			flex-direction: column;
			align-items: center;
			
			image {
				width: 80rpx;
				height: 80rpx;
				margin-bottom: 10rpx;
			}
			
			text {
				font-size: 28rpx;
				color: #333;
			}
		}
	}
	
	/* 海报画布 - 隐藏但可用于生成图片 */
	.poster-canvas {
		position: fixed;
		left: -2000px;
		top: -2000px;
		width: 600px;
		height: 900px;
		z-index: -1;
	}
</style>



