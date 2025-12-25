/**
 * 环境配置测试脚本
 * 验证不同环境下的配置是否正确
 */

// 模拟不同环境的配置
const environments = {
  development: {
    MODE: 'development',
    VITE_API_BASE_URL: 'http://localhost:8201/mall-selfcheck',
    VITE_MOBILE_API_BASE_URL: 'http://10.0.2.2:8201/mall-selfcheck',
    VITE_DEBUG_MODE: 'true',
    VITE_MOCK_PAYMENT: 'true'
  },
  staging: {
    MODE: 'staging', 
    VITE_API_BASE_URL: 'https://test.haojiangzhenhao.hello4am.com',
    VITE_DEBUG_MODE: 'true',
    VITE_MOCK_PAYMENT: 'false'
  },
  production: {
    MODE: 'production',
    VITE_API_BASE_URL: 'https://haojiangzhenhao.hello4am.com',
    VITE_DEBUG_MODE: 'false',
    VITE_MOCK_PAYMENT: 'false'
  }
};

// 模拟不同设备
const devices = {
  desktop: {
    userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)',
    protocol: 'http:'
  },
  mobile: {
    userAgent: 'Mozilla/5.0 (Linux; Android 10; SM-G975F)',
    protocol: 'http:'
  },
  capacitor: {
    userAgent: 'Mozilla/5.0 (Linux; Android 10; SM-G975F)',
    protocol: 'capacitor:'
  }
};

function detectPlatform(device) {
  const isCapacitor = device.protocol === 'capacitor:';
  const isAndroid = device.userAgent.includes('Android');
  const isMobile = device.userAgent.includes('Mobile');
  
  if (isCapacitor) return 'capacitor';
  if (isAndroid || isMobile) return 'mobile';
  return 'desktop';
}

function getApiUrl(env, platform) {
  if (env.MODE === 'development') {
    // 开发环境根据设备选择地址
    return platform === 'desktop' 
      ? env.VITE_API_BASE_URL 
      : env.VITE_MOBILE_API_BASE_URL;
  }
  // 其他环境使用统一地址
  return env.VITE_API_BASE_URL;
}

console.log('=== 环境配置测试开始 ===\n');

// 测试所有环境和设备组合
Object.entries(environments).forEach(([envName, env]) => {
  console.log(`📁 环境: ${envName.toUpperCase()}`);
  console.log(`   MODE: ${env.MODE}`);
  
  Object.entries(devices).forEach(([deviceName, device]) => {
    const platform = detectPlatform(device);
    const apiUrl = getApiUrl(env, platform);
    
    console.log(`   ${deviceName.padEnd(10)} → ${apiUrl}`);
  });
  
  console.log(`   调试模式: ${env.VITE_DEBUG_MODE}`);
  console.log(`   模拟支付: ${env.VITE_MOCK_PAYMENT || 'undefined'}`);
  console.log('');
});

// 验证配置合理性
console.log('=== 配置验证 ===');

const validations = [
  {
    name: '开发环境 - 桌面端使用localhost',
    pass: getApiUrl(environments.development, 'desktop').includes('localhost')
  },
  {
    name: '开发环境 - 移动端使用10.0.2.2',
    pass: getApiUrl(environments.development, 'mobile').includes('10.0.2.2')
  },
  {
    name: '生产环境 - 使用线上域名',
    pass: getApiUrl(environments.production, 'desktop').includes('haojiangzhenhao.hello4am.com')
  },
  {
    name: '预发布环境 - 使用测试域名',
    pass: getApiUrl(environments.staging, 'desktop').includes('test.haojiangzhenhao.hello4am.com')
  },
  {
    name: '生产环境 - 关闭调试模式',
    pass: environments.production.VITE_DEBUG_MODE === 'false'
  },
  {
    name: '生产环境 - 关闭模拟支付',
    pass: environments.production.VITE_MOCK_PAYMENT === 'false'
  }
];

validations.forEach(validation => {
  const status = validation.pass ? '✅' : '❌';
  console.log(`${status} ${validation.name}`);
});

const allPassed = validations.every(v => v.pass);
console.log(`\n${allPassed ? '✅ 所有验证通过！' : '❌ 部分验证失败！'}`);

console.log('\n=== 使用说明 ===');
console.log('🔧 环境切换命令：');
console.log('   npm run dev              → 开发环境');
console.log('   npm run build:staging    → 预发布环境');
console.log('   npm run build            → 生产环境');
console.log('');
console.log('📱 设备支持：');
console.log('   桌面端浏览器             → 自动使用localhost');
console.log('   移动端浏览器/模拟器       → 自动使用10.0.2.2');
console.log('   Capacitor应用           → 自动使用移动端配置');
console.log('\n=== 测试完成 ==='); 