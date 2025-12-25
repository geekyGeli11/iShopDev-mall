/**
 * 语音文件生成脚本
 * 使用 edge-tts (Microsoft Edge TTS) 生成中文语音文件
 */

const { execSync } = require('child_process')
const fs = require('fs')
const path = require('path')

// 语音文件配置
const voiceConfig = {
  // 使用微软 Edge TTS 的中文语音
  voice: 'zh-CN-XiaoxiaoNeural', // 女声，清晰自然
  // voice: 'zh-CN-YunxiNeural',   // 男声备选
  rate: '+0%',    // 语速：正常
  pitch: '+0Hz',  // 音调：正常
  volume: '+0%'   // 音量：正常
}

// 需要生成的语音文本
const voiceTexts = [
  {
    id: 'payment_success',
    text: '支付成功，欢迎再次光临',
    filename: 'payment_success.mp3'
  },
  {
    id: 'payment_success_with_amount',
    text: '支付成功，金额{amount}元，欢迎再次光临',
    filename: 'payment_success_amount.mp3'
  },
  {
    id: 'payment_failure',
    text: '支付失败，请重试',
    filename: 'payment_failure.mp3'
  },
  {
    id: 'scan_to_pay',
    text: '请扫码支付',
    filename: 'scan_to_pay.mp3'
  },
  {
    id: 'welcome',
    text: '欢迎使用广横走商城自助收银系统',
    filename: 'welcome.mp3'
  },
  {
    id: 'scan_product',
    text: '请扫描商品条码',
    filename: 'scan_product.mp3'
  },
  {
    id: 'add_product_success',
    text: '商品添加成功',
    filename: 'add_product_success.mp3'
  }
]

// 输出目录
const outputDir = path.join(__dirname, '../src/renderer/assets/audio/voice')

/**
 * 检查依赖
 */
function checkDependencies() {
  console.log('🔍 检查依赖...')
  
  try {
    execSync('edge-tts --version', { stdio: 'pipe' })
    console.log('✅ edge-tts 已安装')
  } catch (error) {
    console.log('❌ edge-tts 未安装，正在安装...')
    try {
      execSync('pip install edge-tts', { stdio: 'inherit' })
      console.log('✅ edge-tts 安装成功')
    } catch (installError) {
      console.error('❌ edge-tts 安装失败，请手动安装：')
      console.error('   pip install edge-tts')
      process.exit(1)
    }
  }
}

/**
 * 创建输出目录
 */
function createOutputDir() {
  if (!fs.existsSync(outputDir)) {
    fs.mkdirSync(outputDir, { recursive: true })
    console.log(`📁 创建目录: ${outputDir}`)
  }
}

/**
 * 生成单个语音文件
 */
function generateVoiceFile(voiceItem) {
  const outputPath = path.join(outputDir, voiceItem.filename)
  
  // 处理包含变量的文本（用于测试）
  let text = voiceItem.text
  if (text.includes('{amount}')) {
    text = text.replace('{amount}', '99.99')
  }
  
  const command = `edge-tts --voice "${voiceConfig.voice}" --rate "${voiceConfig.rate}" --pitch "${voiceConfig.pitch}" --volume "${voiceConfig.volume}" --text "${text}" --write-media "${outputPath}"`
  
  try {
    console.log(`🎵 生成语音: ${voiceItem.filename}`)
    console.log(`   文本: ${text}`)
    
    execSync(command, { stdio: 'pipe' })
    
    // 检查文件是否生成成功
    if (fs.existsSync(outputPath)) {
      const stats = fs.statSync(outputPath)
      console.log(`✅ 生成成功: ${voiceItem.filename} (${(stats.size / 1024).toFixed(1)} KB)`)
    } else {
      console.log(`❌ 生成失败: ${voiceItem.filename}`)
    }
  } catch (error) {
    console.error(`❌ 生成失败: ${voiceItem.filename}`)
    console.error(`   错误: ${error.message}`)
  }
}

/**
 * 生成所有语音文件
 */
function generateAllVoiceFiles() {
  console.log(`🎵 开始生成 ${voiceTexts.length} 个语音文件...`)
  console.log(`📁 输出目录: ${outputDir}`)
  console.log(`🎤 使用语音: ${voiceConfig.voice}`)
  console.log('')
  
  voiceTexts.forEach((voiceItem, index) => {
    console.log(`[${index + 1}/${voiceTexts.length}] ${voiceItem.id}`)
    generateVoiceFile(voiceItem)
    console.log('')
  })
}

/**
 * 生成语音文件映射配置
 */
function generateVoiceConfig() {
  const configPath = path.join(outputDir, 'voice-config.json')
  
  const config = {
    version: '1.0.0',
    voice: voiceConfig.voice,
    generatedAt: new Date().toISOString(),
    files: voiceTexts.map(item => ({
      id: item.id,
      filename: item.filename,
      text: item.text,
      path: `./assets/audio/voice/${item.filename}`
    }))
  }
  
  fs.writeFileSync(configPath, JSON.stringify(config, null, 2), 'utf8')
  console.log(`📝 生成配置文件: voice-config.json`)
}

/**
 * 主函数
 */
function main() {
  console.log('========================================')
  console.log('🎵 语音文件生成工具')
  console.log('========================================')
  console.log('')
  
  try {
    checkDependencies()
    createOutputDir()
    generateAllVoiceFiles()
    generateVoiceConfig()
    
    console.log('========================================')
    console.log('✅ 语音文件生成完成！')
    console.log('========================================')
    console.log('')
    console.log('📁 文件位置:', outputDir)
    console.log('🎵 生成文件数量:', voiceTexts.length)
    console.log('')
    console.log('下一步：')
    console.log('1. 检查生成的语音文件')
    console.log('2. 运行应用测试语音播放')
    
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
  generateAllVoiceFiles,
  voiceTexts,
  outputDir
}
