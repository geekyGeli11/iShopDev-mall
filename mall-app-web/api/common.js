import request from '@/utils/requestUtil'
import { API_BASE_URL } from '@/utils/appConfig.js'

// 生成小程序码
export function generateQrCode(params) {
	return request({
		method: 'GET',
		url: '/wx/miniprogram/qrcode',
        params: params,
        responseType: 'arraybuffer'
	})
}

// 统一的token获取函数
const getTokenFromStorage = () => {
	try {
		const tokenInfo = uni.getStorageSync('tokenInfo');
		if (!tokenInfo) return '';

		const loginData = JSON.parse(tokenInfo);
		if (loginData.token && loginData.tokenHead) {
			return loginData.tokenHead + loginData.token;
		}
		return '';
	} catch (e) {
		console.error('解析tokenInfo失败:', e);
		return '';
	}
};

// 上传图片到COS
export function uploadImage(filePath) {
	return new Promise((resolve, reject) => {
		// 获取基础URL，将portal替换为admin
		const baseUrl = API_BASE_URL.replace('/mall-portal', '/mall-admin');

		uni.uploadFile({
			url: baseUrl + '/aliyun/oss/upload',
			filePath: filePath,
			name: 'file',
			header: {
				'Authorization': getTokenFromStorage()
			},
			success: (res) => {
				try {
					const result = JSON.parse(res.data);
					if (result.code === 200) {
						resolve(result);
					} else {
						reject(new Error(result.message || '上传失败'));
					}
				} catch (e) {
					reject(new Error('解析响应失败'));
				}
			},
			fail: (err) => {
				reject(err);
			}
		});
	});
}
