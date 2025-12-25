/**
 * 使用浏览器 Web Speech API 生成语音文件
 * 在浏览器控制台中运行此脚本
 */

// 语音配置
const voiceConfigs = [
    {
        id: "payment_success",
        filename: "payment_success.mp3",
        text: "支付成功，欢迎再次光临"
    },
    {
        id: "payment_failure", 
        filename: "payment_failure.mp3",
        text: "支付失败，请重试"
    },
    {
        id: "scan_to_pay",
        filename: "scan_to_pay.mp3", 
        text: "请扫码支付"
    },
    {
        id: "welcome",
        filename: "welcome.mp3",
        text: "欢迎使用广横走商城自助收银系统"
    }
];

// 语音生成器类
class VoiceGenerator {
    constructor() {
        this.mediaRecorder = null;
        this.audioChunks = [];
        this.currentConfig = null;
    }

    // 检查浏览器支持
    checkSupport() {
        if (!('speechSynthesis' in window)) {
            console.error('❌ 浏览器不支持 Web Speech API');
            return false;
        }
        
        if (!('MediaRecorder' in window)) {
            console.error('❌ 浏览器不支持 MediaRecorder API');
            return false;
        }
        
        console.log('✅ 浏览器支持语音合成和录制');
        return true;
    }

    // 获取可用语音
    async getVoices() {
        return new Promise((resolve) => {
            let voices = speechSynthesis.getVoices();
            
            if (voices.length === 0) {
                speechSynthesis.onvoiceschanged = () => {
                    voices = speechSynthesis.getVoices();
                    resolve(voices);
                };
            } else {
                resolve(voices);
            }
        });
    }

    // 选择中文语音
    async selectChineseVoice() {
        const voices = await this.getVoices();
        
        // 优先选择中文语音
        const chineseVoices = voices.filter(voice => 
            voice.lang.includes('zh') || 
            voice.name.includes('Chinese') ||
            voice.name.includes('中文')
        );
        
        if (chineseVoices.length > 0) {
            console.log('✅ 找到中文语音:', chineseVoices[0].name);
            return chineseVoices[0];
        }
        
        // 如果没有中文语音，使用默认语音
        console.log('⚠️  未找到中文语音，使用默认语音');
        return voices[0] || null;
    }

    // 设置音频录制
    async setupRecording() {
        try {
            // 创建一个虚拟音频上下文来录制语音合成
            const audioContext = new (window.AudioContext || window.webkitAudioContext)();
            const destination = audioContext.createMediaStreamDestination();
            
            this.mediaRecorder = new MediaRecorder(destination.stream);
            this.audioChunks = [];
            
            this.mediaRecorder.ondataavailable = (event) => {
                if (event.data.size > 0) {
                    this.audioChunks.push(event.data);
                }
            };
            
            return { audioContext, destination };
        } catch (error) {
            console.error('❌ 设置录制失败:', error);
            return null;
        }
    }

    // 生成单个语音文件
    async generateVoice(config) {
        console.log(`🎵 开始生成: ${config.text}`);
        
        const voice = await this.selectChineseVoice();
        if (!voice) {
            console.error('❌ 没有可用的语音');
            return false;
        }

        return new Promise((resolve) => {
            const utterance = new SpeechSynthesisUtterance(config.text);
            utterance.voice = voice;
            utterance.rate = 1.0;
            utterance.pitch = 1.0;
            utterance.volume = 1.0;
            utterance.lang = 'zh-CN';

            // 开始录制
            this.audioChunks = [];
            
            utterance.onstart = () => {
                console.log(`🔴 开始播放: ${config.text}`);
            };

            utterance.onend = () => {
                console.log(`✅ 播放完成: ${config.text}`);
                
                // 等待一下确保录制完成
                setTimeout(() => {
                    this.downloadAudio(config);
                    resolve(true);
                }, 500);
            };

            utterance.onerror = (error) => {
                console.error(`❌ 语音合成失败: ${error.error}`);
                resolve(false);
            };

            // 播放语音
            speechSynthesis.speak(utterance);
        });
    }

    // 下载音频文件
    downloadAudio(config) {
        try {
            // 创建音频 blob
            const audioBlob = new Blob(this.audioChunks, { type: 'audio/wav' });
            
            // 创建下载链接
            const url = URL.createObjectURL(audioBlob);
            const a = document.createElement('a');
            a.href = url;
            a.download = config.filename.replace('.mp3', '.wav'); // 浏览器录制的是 WAV 格式
            
            // 触发下载
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            
            // 清理 URL
            URL.revokeObjectURL(url);
            
            console.log(`💾 已下载: ${config.filename}`);
        } catch (error) {
            console.error('❌ 下载失败:', error);
        }
    }

    // 生成所有语音文件
    async generateAllVoices() {
        console.log('🚀 开始生成所有语音文件...');
        console.log('📋 生成清单:');
        voiceConfigs.forEach((config, index) => {
            console.log(`  ${index + 1}. ${config.text} -> ${config.filename}`);
        });

        let successCount = 0;
        
        for (let i = 0; i < voiceConfigs.length; i++) {
            const config = voiceConfigs[i];
            console.log(`\n📝 [${i + 1}/${voiceConfigs.length}] ${config.text}`);
            
            const success = await this.generateVoice(config);
            if (success) {
                successCount++;
            }
            
            // 等待一下再生成下一个
            if (i < voiceConfigs.length - 1) {
                console.log('⏳ 等待 2 秒...');
                await new Promise(resolve => setTimeout(resolve, 2000));
            }
        }
        
        console.log(`\n🎉 生成完成! 成功: ${successCount}/${voiceConfigs.length}`);
        
        // 生成配置文件
        this.generateConfigFile();
    }

    // 生成配置文件
    generateConfigFile() {
        const configData = {
            version: "1.0.0",
            generated_at: new Date().toISOString(),
            voice_files: {}
        };
        
        voiceConfigs.forEach(config => {
            configData.voice_files[config.id] = {
                filename: config.filename,
                text: config.text,
                path: `/assets/audio/voice/${config.filename}`
            };
        });
        
        // 下载配置文件
        const configBlob = new Blob([JSON.stringify(configData, null, 2)], { 
            type: 'application/json' 
        });
        const url = URL.createObjectURL(configBlob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'voice-config.json';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(url);
        
        console.log('✅ 配置文件已生成: voice-config.json');
    }
}

// 主函数
async function generateVoiceFiles() {
    console.log('🎤 浏览器语音文件生成器');
    console.log('=' .repeat(30));
    
    const generator = new VoiceGenerator();
    
    // 检查浏览器支持
    if (!generator.checkSupport()) {
        return;
    }
    
    console.log('\n⚠️  使用说明:');
    console.log('1. 确保浏览器允许自动播放音频');
    console.log('2. 生成的文件会自动下载到下载文件夹');
    console.log('3. 文件格式为 WAV，需要手动转换为 MP3');
    console.log('4. 将文件复制到 src/renderer/assets/audio/voice/ 目录');
    
    const confirm = confirm('是否开始生成语音文件？');
    if (!confirm) {
        console.log('❌ 用户取消');
        return;
    }
    
    // 开始生成
    await generator.generateAllVoices();
}

// 导出函数供控制台使用
window.generateVoiceFiles = generateVoiceFiles;

// 自动运行
console.log('🎤 语音文件生成器已加载');
console.log('💡 运行 generateVoiceFiles() 开始生成语音文件');

// 如果是直接运行，自动开始
if (typeof window !== 'undefined' && window.location) {
    // 延迟一下确保页面加载完成
    setTimeout(() => {
        console.log('🚀 自动开始生成语音文件...');
        generateVoiceFiles();
    }, 1000);
}
