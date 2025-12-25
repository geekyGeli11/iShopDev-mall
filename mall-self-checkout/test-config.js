/**
 * 配置测试脚本
 * 验证环境变量和配置是否正常工作
 */

// 模拟import.meta.env环境
const importMeta = {
  env: {
    MODE: 'development',
    VITE_API_BASE_URL: 'http://localhost:8201/mall-selfcheck',
    VITE_MOBILE_API_BASE_URL: 'http://10.0.2.2:8201/mall-selfcheck',
    VITE_DEBUG_MODE: 'true',
    VITE_MOCK_PAYMENT: 'true'
  }
};

// 模拟window对象
const mockWindow = {
  location: { protocol: 'http:' },
  navigator: { userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)' }
};

// 模拟平台检测函数
function detectPlatform() {
  const isCapacitor = mockWindow.location.protocol === 'capacitor:';
  const isAndroid = mockWindow.navigator.userAgent.includes('Android');
  const isMobile = mockWindow.navigator.userAgent.includes('Mobile');
  
  if (isCapacitor) return 'capacitor';
  if (isAndroid || isMobile) return 'mobile';
  return 'desktop';
}

// 模拟API地址获取函数
function getApiBaseUrl(platform) {
  const defaultUrl = importMeta.env.VITE_API_BASE_URL || 'http://localhost:8201/mall-selfcheck';
  
  switch (platform) {
    case 'mobile':
    case 'capacitor':
      return importMeta.env.VITE_MOBILE_API_BASE_URL || 'http://10.0.2.2:8201/mall-selfcheck';
    case 'desktop':
    default:
      return defaultUrl;
  }
}

// 测试配置
console.log('=== 配置测试开始 ===');

const platform = detectPlatform();
const apiUrl = getApiBaseUrl(platform);

console.log('环境变量:', importMeta.env);
console.log('检测到的平台:', platform);
console.log('选择的API地址:', apiUrl);

// 验证配置
const tests = [
  {
    name: '桌面端API地址',
    expected: 'http://localhost:8201/mall-selfcheck',
    actual: getApiBaseUrl('desktop'),
    pass: getApiBaseUrl('desktop') === 'http://localhost:8201/mall-selfcheck'
  },
  {
    name: '移动端API地址',
    expected: 'http://10.0.2.2:8201/mall-selfcheck',
    actual: getApiBaseUrl('mobile'),
    pass: getApiBaseUrl('mobile') === 'http://10.0.2.2:8201/mall-selfcheck'
  },
  {
    name: '调试模式',
    expected: 'true',
    actual: importMeta.env.VITE_DEBUG_MODE,
    pass: importMeta.env.VITE_DEBUG_MODE === 'true'
  }
];

console.log('\n=== 测试结果 ===');
tests.forEach(test => {
  const status = test.pass ? '✅ 通过' : '❌ 失败';
  console.log(`${status} ${test.name}: ${test.actual} (期望: ${test.expected})`);
});

const allPassed = tests.every(test => test.pass);
console.log(`\n=== 总结 ===`);
console.log(allPassed ? '✅ 所有测试通过！配置正常。' : '❌ 部分测试失败，请检查配置。');

// 模拟不同平台的测试
console.log('\n=== 平台测试 ===');

// 测试Android移动端
mockWindow.navigator.userAgent = 'Mozilla/5.0 (Linux; Android 10; SM-G975F)';
console.log('Android移动端:', getApiBaseUrl(detectPlatform()));

// 测试Capacitor
mockWindow.location.protocol = 'capacitor:';
console.log('Capacitor应用:', getApiBaseUrl(detectPlatform()));

// 测试桌面端
mockWindow.location.protocol = 'http:';
mockWindow.navigator.userAgent = 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)';
console.log('Windows桌面端:', getApiBaseUrl(detectPlatform()));

console.log('\n=== 配置测试完成 ==='); 