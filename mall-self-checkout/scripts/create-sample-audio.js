/**
 * 创建示例音频文件
 * 由于网络限制，我们创建一些占位符音频文件用于测试
 */

const fs = require('fs')
const path = require('path')

// 输出目录
const outputDir = path.join(__dirname, '../src/renderer/assets/audio/voice')

// 语音文件配置
const voiceFiles = [
  { id: 'payment_success', filename: 'payment_success.mp3', text: '支付成功，欢迎再次光临' },
  { id: 'payment_success_with_amount', filename: 'payment_success_amount.mp3', text: '支付成功，金额99.99元，欢迎再次光临' },
  { id: 'payment_failure', filename: 'payment_failure.mp3', text: '支付失败，请重试' },
  { id: 'scan_to_pay', filename: 'scan_to_pay.mp3', text: '请扫码支付' },
  { id: 'welcome', filename: 'welcome.mp3', text: '欢迎使用广横走商城自助收银系统' },
  { id: 'scan_product', filename: 'scan_product.mp3', text: '请扫描商品条码' },
  { id: 'add_product_success', filename: 'add_product_success.mp3', text: '商品添加成功' }
]

/**
 * 创建一个简单的音频文件（静音MP3）
 * 这是一个最小的MP3文件，用于测试音频播放功能
 */
function createSilentMP3(duration = 1000) {
  // 这是一个1秒静音MP3文件的最小字节数据
  const mp3Header = Buffer.from([
    0xFF, 0xFB, 0x90, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
  ])
  return mp3Header
}

/**
 * 创建示例音频文件
 */
function createSampleAudioFiles() {
  console.log('🎵 创建示例音频文件...')
  console.log(`📁 输出目录: ${outputDir}`)
  
  // 确保目录存在
  if (!fs.existsSync(outputDir)) {
    fs.mkdirSync(outputDir, { recursive: true })
    console.log('📁 创建目录成功')
  }
  
  // 创建每个音频文件
  voiceFiles.forEach((voiceFile, index) => {
    const filePath = path.join(outputDir, voiceFile.filename)
    
    try {
      // 创建一个简单的MP3文件
      const audioData = createSilentMP3()
      fs.writeFileSync(filePath, audioData)
      
      console.log(`✅ [${index + 1}/${voiceFiles.length}] ${voiceFile.filename}`)
      console.log(`   文本: ${voiceFile.text}`)
      console.log(`   大小: ${audioData.length} bytes`)
      
    } catch (error) {
      console.error(`❌ 创建失败: ${voiceFile.filename}`)
      console.error(`   错误: ${error.message}`)
    }
  })
  
  // 创建配置文件
  const configPath = path.join(outputDir, 'voice-config.json')
  const config = {
    version: '1.0.0',
    type: 'sample', // 标记为示例文件
    note: '这些是示例音频文件，实际部署时请替换为真实的语音文件',
    generatedAt: new Date().toISOString(),
    files: voiceFiles.map(item => ({
      id: item.id,
      filename: item.filename,
      text: item.text,
      path: `./assets/audio/voice/${item.filename}`
    }))
  }
  
  fs.writeFileSync(configPath, JSON.stringify(config, null, 2), 'utf8')
  console.log('📝 生成配置文件: voice-config.json')
  
  console.log('')
  console.log('========================================')
  console.log('✅ 示例音频文件创建完成！')
  console.log('========================================')
  console.log('')
  console.log('📋 说明：')
  console.log('• 这些是静音的示例文件，用于测试音频播放功能')
  console.log('• 实际部署时，请替换为真实的语音文件')
  console.log('• 可以使用以下方式生成真实语音：')
  console.log('  1. 使用在线TTS服务（如百度、阿里云、腾讯云）')
  console.log('  2. 使用本地TTS软件录制')
  console.log('  3. 人工录制语音文件')
  console.log('')
  console.log('🎯 下一步：')
  console.log('1. 测试音频播放功能')
  console.log('2. 替换为真实语音文件')
  console.log('3. 调整音频播放参数')
}

/**
 * 主函数
 */
function main() {
  console.log('========================================')
  console.log('🎵 示例音频文件生成工具')
  console.log('========================================')
  console.log('')
  
  try {
    createSampleAudioFiles()
  } catch (error) {
    console.error('❌ 生成过程中发生错误:', error.message)
    process.exit(1)
  }
}

// 运行主函数
if (require.main === module) {
  main()
}

module.exports = {
  createSampleAudioFiles,
  voiceFiles,
  outputDir
}
