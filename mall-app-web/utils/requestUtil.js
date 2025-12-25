import Request from '@/js_sdk/luch-request/request.js'
import { API_BASE_URL } from '@/utils/appConfig.js';
import store from '@/store';

const http = new Request();

// 全局变量控制token刷新状态
let isRefreshing = false;
let failedQueue = [];
let refreshPromise = null; // 缓存刷新Promise，确保同时只有一个刷新操作
let lastRefreshTime = 0; // 记录最后一次刷新时间，避免频繁刷新

// 统一的token获取函数
const getTokenFromStorage = () => {
    try {
        const tokenInfo = uni.getStorageSync('tokenInfo');
        if (!tokenInfo) return null;

        const loginData = JSON.parse(tokenInfo);
        if (loginData.token && loginData.tokenHead) {
            return loginData.tokenHead + loginData.token;
        }
        return null;
    } catch (e) {
        console.error('解析tokenInfo失败:', e);
        return null;
    }
};

// 统一的token信息获取函数
const getTokenInfoFromStorage = () => {
    try {
        const tokenInfo = uni.getStorageSync('tokenInfo');
        if (!tokenInfo) return null;
        return JSON.parse(tokenInfo);
    } catch (e) {
        console.error('解析tokenInfo失败:', e);
        return null;
    }
};

// 处理队列中的请求
const processQueue = (error, token = null) => {
    failedQueue.forEach(prom => {
        if (error) {
            prom.reject(error);
        } else {
            prom.resolve(token);
        }
    });
    failedQueue = [];
};

// 静默登录函数
const silentLogin = async () => {
    try {
        const loginData = getTokenInfoFromStorage();
        if (!loginData) {
            throw new Error('无登录信息');
        }
        if (!loginData.openId) {
            throw new Error('缺少openId');
        }

        // 调用静默登录接口
        const response = await new Promise((resolve, reject) => {
            uni.request({
                url: `${API_BASE_URL}/sso/silentLogin`,
                method: 'POST',
                header: {
                    'content-type': 'application/x-www-form-urlencoded;charset=utf-8'
                },
                data: {
                    openId: loginData.openId
                },
                success: resolve,
                fail: reject
            });
        });

        if (response.data.code === 200) {
            const newTokenData = response.data.data;
            // 构建完整的token数据对象
            const completeTokenData = {
                ...newTokenData,
                openId: loginData.openId
            };

            // 保存新的token信息
            saveTokenInfo(completeTokenData);

            const fullToken = newTokenData.tokenHead + newTokenData.token;
            console.log('静默登录成功，新token已保存:', fullToken.substring(0, 20) + '...');

            // 返回完整的token数据对象，而不是字符串
            console.log('silentLogin即将返回的数据:', {
                tokenHead: completeTokenData.tokenHead,
                tokenLength: completeTokenData.token ? completeTokenData.token.length : 0,
                openId: completeTokenData.openId,
                expiresIn: completeTokenData.expiresIn,
                dataType: typeof completeTokenData
            });
            return completeTokenData;
        } else {
            throw new Error(response.data.message || '静默登录失败');
        }
    } catch (error) {
        console.error('静默登录失败:', error);
        throw error;
    }
};

// 保存token信息 - 统一只保存tokenInfo
const saveTokenInfo = (tokenData) => {
    const tokenInfo = {
        token: tokenData.token,
        tokenHead: tokenData.tokenHead,
        expiresIn: tokenData.expiresIn,
        loginTime: Date.now(),
        openId: tokenData.openId
    };
    uni.setStorageSync('tokenInfo', JSON.stringify(tokenInfo));
    console.log('Token信息已保存:', {
        tokenHead: tokenInfo.tokenHead,
        tokenLength: tokenInfo.token ? tokenInfo.token.length : 0,
        openId: tokenInfo.openId,
        expiresIn: tokenInfo.expiresIn
    });
};

http.setConfig((config) => { /* 设置全局配置 */
    config.baseUrl = API_BASE_URL; /* 根域名不同 */
    config.header = {
        ...config.header,
    };
    return config;
});

/**
 * 自定义验证器，如果返回 true 则进入响应拦截器的响应成功函数 (resolve)，否则进入响应拦截器的响应错误函数 (reject)
 * @param { Number } statusCode - 请求响应体 statusCode（只读）
 * @return { Boolean } 如果为 true，则 resolve，否则 reject
 */
http.validateStatus = (statusCode) => {
    return statusCode === 200;
};

http.interceptor.request((config, cancel) => { /* 请求之前拦截器 */
    const token = getTokenFromStorage();
    if (token) {
        config.header = {
            'Authorization': token,
            ...config.header,
        };
    } else {
        config.header = {
            ...config.header,
        };
    }

    // 不再显示默认的loading，改为使用自定义的全屏加载组件
    // 如果需要显示loading，可以在config中设置showLoading为true
    if (config.showLoading) {
        uni.showLoading({
            title: config.loadingTitle || '加载中...',
            mask: true
        });
    }

    return config;
});

// 递归替换图片地址
function replaceImageURLs(data) {
    const baseURL = 'http://43.138.175.133:9090/mall/';
    const cdnURL = 'https://cdn.haojiangzhenhao.hello4am.com/mall/';

    if (typeof data === 'string') {
        // 如果是 albumPics 等多个 URL 组成的字符串，按逗号拆分
        if (data.includes(',')) {
            return data.split(',').map(url => url.startsWith(baseURL) ? url.replace(baseURL, cdnURL) : url).join(',');
        }
        // 其他字符串直接替换
        if (data.startsWith(baseURL)) {
            return data.replace(baseURL, cdnURL);
        }
        return data;
    } else if (Array.isArray(data)) {
        return data.map(item => replaceImageURLs(item));
    } else if (typeof data === 'object' && data !== null) {
        const newData = {};
        for (const key in data) {
            if (data.hasOwnProperty(key)) {
                newData[key] = replaceImageURLs(data[key]);
            }
        }
        return newData;
    }
    return data;
}


http.interceptor.response((response) => { /* 请求之后拦截器 */
    // 只有在请求时显示了loading才隐藏
    if (response.config && response.config.showLoading) {
        uni.hideLoading();
    }

    const res = response.data;

    // 仅在 res.data 存在时进行替换
    // if (res && res.data) {
    //     res.data = replaceImageURLs(res.data);
    // }

    if (res?.code !== 200) {
        // 提示错误信息
        uni.showToast({
            title: res?.message || '请求失败',
            duration: 1500,
        });

        // 401 未登录处理 - 区分未登录和登录过期场景
        if (res?.code === 401) {
            const originalRequest = response.config;

            // 检查登录状态，区分两种401场景
            const loginData = getTokenInfoFromStorage();
            const hasValidLoginInfo = !!(loginData && loginData.openId && loginData.token);

            // 场景1: 未登录场景（无token或无openId）
            if (!hasValidLoginInfo) {
                // 检查是否已经设置了登录弹窗状态
                if (!store.state.shouldShowLoginPopup) {
                    // 设置登录弹窗状态到 Vuex（临时状态，不持久化）
                    store.commit('setLoginPopup', { show: true, reason: 'unauthorized' });

                    uni.showModal({
                        title: '提示',
                        content: '该功能需要登录后使用，点击确认完成登录',
                        confirmText: '确认',
                        cancelText: '取消',
                        success: function (modalRes) {
                            if (modalRes.confirm) {
                                // 用户确认时，先清除当前弹窗状态，然后跳转到首页
                                // 首页会重新检查并设置弹窗状态
                                store.commit('clearLoginPopup');
                                uni.switchTab({
                                    url: '/pages/new_index/index'
                                });
                                // 延迟重新设置弹窗状态，确保首页已加载
                                setTimeout(() => {
                                    store.commit('setLoginPopup', { show: true, reason: 'unauthorized' });
                                }, 100);
                            } else {
                                // 用户取消时清除弹窗状态
                                store.commit('clearLoginPopup');
                            }
                        }
                    });
                }
                return Promise.reject(response);
            }

            // 场景2: 登录过期场景（有token和openId但已过期）- 尝试静默登录
            const now = Date.now();
            const timeSinceLastRefresh = now - lastRefreshTime;

            // 如果距离上次刷新不到5秒，直接使用当前token重试
            if (timeSinceLastRefresh < 5000) {
                console.log('距离上次刷新时间过短，直接重试:', timeSinceLastRefresh + 'ms');
                const currentToken = getTokenFromStorage();
                if (currentToken) {
                    const retryConfig = {
                        ...originalRequest,
                        header: {
                            ...originalRequest.header,
                            'Authorization': currentToken
                        }
                    };
                    return http.request(retryConfig);
                }
            }

            if (!refreshPromise) {
                console.log('开始静默登录刷新token...');
                isRefreshing = true;
                lastRefreshTime = now; // 记录刷新开始时间

                // 立即设置refreshPromise，防止并发请求重复创建
                refreshPromise = silentLogin()
                    .then(newTokenData => {
                        console.log('401处理接收到silentLogin返回的数据:', {
                            tokenHead: newTokenData ? newTokenData.tokenHead : 'undefined',
                            tokenLength: newTokenData && newTokenData.token ? newTokenData.token.length : 0,
                            openId: newTokenData ? newTokenData.openId : 'undefined',
                            expiresIn: newTokenData ? newTokenData.expiresIn : 'undefined',
                            dataType: typeof newTokenData,
                            isNull: newTokenData === null,
                            isUndefined: newTokenData === undefined
                        });

                        // 验证token是否正确保存（silentLogin已经保存了）
                        const savedToken = getTokenFromStorage();
                        const expectedToken = newTokenData.tokenHead + newTokenData.token;

                        console.log('Token保存验证:', {
                            saved: savedToken ? savedToken.substring(0, 20) + '...' : 'null',
                            expected: expectedToken ? expectedToken.substring(0, 20) + '...' : 'null',
                            match: savedToken === expectedToken
                        });

                        if (savedToken !== expectedToken) {
                            console.error('Token保存验证失败！');
                            throw new Error('Token保存失败');
                        }

                        console.log('处理队列中的请求...');
                        processQueue(null, savedToken);
                        return savedToken;
                    })
                    .catch(error => {
                        console.log('静默登录失败:', error);
                        processQueue(error, null);

                        // 静默登录失败，使用Vuex统一管理登录弹窗状态
                        if (!store.state.shouldShowLoginPopup) {
                            store.commit('setLoginPopup', { show: true, reason: 'expired' });
                            uni.showModal({
                                title: '提示',
                                content: '登录已过期，请重新登录',
                                confirmText: '重新登录',
                                cancelText: '取消',
                                success: function (modalRes) {
                                    if (modalRes.confirm) {
                                        uni.switchTab({
                                            url: '/pages/new_index/index'
                                        });
                                    } else {
                                        // 用户取消时清除弹窗状态
                                        store.commit('clearLoginPopup');
                                    }
                                }
                            });
                        }
                        throw error;
                    })
                    .finally(() => {
                        console.log('静默登录流程结束，重置状态');
                        isRefreshing = false;
                        refreshPromise = null;
                    });
            }

            // 等待刷新完成，然后重试原请求
            return refreshPromise.then(savedToken => {
                console.log('静默登录完成，开始重试原请求...');
                console.log('重试URL:', originalRequest.url);
                console.log('使用token:', savedToken ? savedToken.substring(0, 20) + '...' : 'null');

                // 重新构建请求配置，确保使用最新token
                const retryConfig = {
                    ...originalRequest,
                    header: {
                        ...originalRequest.header,
                        'Authorization': savedToken
                    }
                };

                // 使用http实例重试，确保经过拦截器处理
                return http.request(retryConfig)
                    .then(retryResponse => {
                        console.log('重试请求成功:', retryResponse);
                        return retryResponse;
                    })
                    .catch(retryError => {
                        console.error('重试请求失败:', retryError);
                        throw retryError;
                    });
            }).catch(error => {
                console.error('静默登录或重试失败:', error);
                return Promise.reject(response);
            });
        } else {
            // 正在刷新token，等待刷新完成
            return refreshPromise.then(savedToken => {
                console.log('等待静默登录完成，开始队列重试...');
                console.log('队列重试URL:', originalRequest.url);
                console.log('使用token:', savedToken ? savedToken.substring(0, 20) + '...' : 'null');

                // 重新构建请求配置，确保使用最新token
                const retryConfig = {
                    ...originalRequest,
                    header: {
                        ...originalRequest.header,
                        'Authorization': savedToken
                    }
                };

                // 使用http实例重试，确保经过拦截器处理
                return http.request(retryConfig)
                    .then(retryResponse => {
                        console.log('队列重试请求成功:', retryResponse);
                        return retryResponse;
                    })
                    .catch(retryError => {
                        console.error('队列重试请求失败:', retryError);
                        throw retryError;
                    });
            }).catch(error => {
                console.error('队列重试失败:', error);
                return Promise.reject(response);
            });
        }
    } else {
        return response.data;
    }
}, (response) => {
    // 只有在请求时显示了loading才隐藏
    if (response.config && response.config.showLoading) {
        uni.hideLoading();
    }

    // 提示错误信息
    console.log('response error', response?.errMsg || '未知错误');
    // uni.showToast({
    //     title: response?.errMsg || '请求错误',
    //     duration: 1500,
    // });
    return Promise.reject(response);
});

export function request(options = {}) {
    return http.request(options);
}

export default request;
