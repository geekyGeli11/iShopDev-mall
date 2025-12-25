// 验证会员码格式的脚本
console.log('=== 会员码格式验证测试 ===\n');

// 您提供的JSON格式
const testJson = '{"type":"member_code","memberCode":"M25072445428","memberId":240,"timestamp":1756367064326}';

// 模拟前端处理逻辑
function processMemberCodeScan(scannedData) {
  console.log('📱 扫描到数据:', scannedData);
  
  let memberCode = '';
  let memberId = null;
  let result = {
    success: false,
    memberCode: '',
    memberId: null,
    format: '',
    message: '',
    details: {}
  };

  try {
    // 尝试解析JSON格式
    const parsed = JSON.parse(scannedData);
    console.log('✅ JSON解析成功:', parsed);
    
    // 检查是否为会员码二维码格式
    if (parsed.type === 'member_code' && parsed.memberCode) {
      memberCode = parsed.memberCode.toString();
      memberId = parsed.memberId || null;
      result.format = 'JSON';
      result.memberCode = memberCode;
      result.memberId = memberId;
      result.details = {
        type: parsed.type,
        timestamp: parsed.timestamp
      };
      result.success = true;
      result.message = `JSON格式解析成功`;
      console.log('🎯 解析JSON格式会员码成功:', { memberCode, memberId, timestamp: parsed.timestamp });
    } else {
      result.message = '❌ 扫描的二维码格式不正确，请扫描会员专用二维码';
      console.log('❌ JSON格式不正确:', parsed);
      return result;
    }
  } catch (error) {
    // 如果不是JSON格式，当作普通会员码处理
    memberCode = scannedData.trim().toUpperCase();
    result.format = '传统格式';
    result.memberCode = memberCode;
    result.success = true;
    result.message = `传统格式识别成功`;
    console.log('📝 直接使用扫描数据作为会员码:', memberCode);
  }

  // 验证会员码格式
  if (!memberCode || memberCode.length === 0) {
    result.success = false;
    result.message = '❌ 扫描到的会员码为空，请重新扫描';
    console.log('❌ 会员码为空');
    return result;
  }

  return result;
}

// 简化的会员码处理（前端不做严格验证）
function handleMemberCodeLogin(memberCode, memberId = null) {
  console.log('🔍 处理会员码登录:', memberCode);

  // 简单处理会员码格式，去除空格并转大写
  let processedCode = memberCode.trim().toUpperCase();
  console.log('📝 处理后:', processedCode);

  // 基本验证：不能为空
  if (!processedCode) {
    return {
      success: false,
      message: '❌ 会员码不能为空'
    };
  }

  // 构建登录参数
  const loginParams = {
    memberCode: processedCode,
    loginType: 'code',
  };

  // 如果有 memberId，添加到参数中
  if (memberId) {
    loginParams.memberId = memberId;
  }

  console.log('📤 发送到后端的参数:', loginParams);

  return {
    success: true,
    memberCode: processedCode,
    loginParams: loginParams,
    message: '✅ 会员码处理成功，发送到后端验证'
  };
}

// 执行测试
console.log('🧪 测试1: JSON格式处理');
console.log('=' .repeat(50));
const scanResult = processMemberCodeScan(testJson);
console.log('📋 扫描结果:', scanResult);
console.log('');

console.log('🧪 测试2: 简化的会员码处理');
console.log('=' .repeat(50));
if (scanResult.success) {
  const loginResult = handleMemberCodeLogin(scanResult.memberCode, scanResult.memberId);
  console.log('📋 登录处理结果:', loginResult);
} else {
  console.log('❌ 扫描失败，无法进行登录处理');
}
console.log('');

console.log('🧪 测试3: 各种格式测试（简化处理）');
console.log('=' .repeat(50));
const testCases = [
  'M25072445428',      // 您的会员码
  '25072445428',       // 纯数字
  'M12345678901',      // 其他格式
  'A25072445428',      // 任意前缀
  'abc123',            // 任意字符
  '  M25072445428  ',  // 带空格
  ''                   // 空字符串
];

testCases.forEach((testCase, index) => {
  console.log(`\n📝 测试用例 ${index + 1}: "${testCase}"`);
  const result = handleMemberCodeLogin(testCase);
  console.log(`   结果: ${result.message}`);
  if (result.success) {
    console.log(`   处理后: ${result.memberCode}`);
    console.log(`   登录参数:`, result.loginParams);
  }
});

console.log('\n🎯 总结:');
console.log('- JSON格式解析: ✅ 支持');
console.log('- 前端验证: ❌ 移除严格验证');
console.log('- 后端验证: ✅ 交给后端处理');
console.log('- 向后兼容: ✅ 支持所有格式');
