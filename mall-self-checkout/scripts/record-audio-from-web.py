#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
网页语音录制脚本
用于录制 http://localhost:3000/#/test/audio 页面中播放的语音并保存为 MP3 文件

依赖安装:
pip install selenium pyaudio pydub webdriver-manager

使用方法:
python record-audio-from-web.py
"""

import os
import sys
import time
import json
import threading
from datetime import datetime
from pathlib import Path

try:
    import pyaudio
    import wave
    from pydub import AudioSegment
    from selenium import webdriver
    from selenium.webdriver.common.by import By
    from selenium.webdriver.support.ui import WebDriverWait
    from selenium.webdriver.support import expected_conditions as EC
    from selenium.webdriver.chrome.service import Service
    from webdriver_manager.chrome import ChromeDriverManager
    from selenium.webdriver.chrome.options import Options
except ImportError as e:
    print(f"缺少依赖库: {e}")
    print("请运行: pip install selenium pyaudio pydub webdriver-manager")
    sys.exit(1)

class WebAudioRecorder:
    def __init__(self):
        self.audio_format = pyaudio.paInt16
        self.channels = 2
        self.rate = 44100
        self.chunk = 1024
        self.recording = False
        self.frames = []
        self.audio = pyaudio.PyAudio()
        self.stream = None
        self.driver = None
        
        # 输出目录
        self.output_dir = Path("src/renderer/assets/audio/voice")
        self.output_dir.mkdir(parents=True, exist_ok=True)
        
        # 语音配置
        self.voice_configs = [
            {
                "id": "payment_success",
                "filename": "payment_success.mp3",
                "text": "支付成功，欢迎再次光临",
                "button_text": "支付成功"
            },
            {
                "id": "payment_success_amount",
                "filename": "payment_success_amount.mp3", 
                "text": "支付成功，金额99.99元，欢迎再次光临",
                "button_text": "支付成功（带金额）"
            },
            {
                "id": "payment_failure",
                "filename": "payment_failure.mp3",
                "text": "支付失败，请重试",
                "button_text": "支付失败"
            },
            {
                "id": "scan_to_pay", 
                "filename": "scan_to_pay.mp3",
                "text": "请扫码支付",
                "button_text": "请扫码支付"
            },
            {
                "id": "welcome",
                "filename": "welcome.mp3",
                "text": "欢迎使用广横走商城自助收银系统",
                "button_text": "欢迎语音"
            }
        ]

    def setup_browser(self):
        """设置浏览器"""
        print("🌐 正在启动浏览器...")
        
        chrome_options = Options()
        chrome_options.add_argument("--use-fake-ui-for-media-stream")  # 允许录音
        chrome_options.add_argument("--allow-running-insecure-content")
        chrome_options.add_argument("--disable-web-security")
        chrome_options.add_argument("--disable-features=VizDisplayCompositor")
        
        # 自动下载并设置 ChromeDriver
        service = Service(ChromeDriverManager().install())
        self.driver = webdriver.Chrome(service=service, options=chrome_options)
        
        print("✅ 浏览器启动成功")

    def setup_audio_recording(self):
        """设置音频录制"""
        print("🎤 正在设置音频录制...")
        
        try:
            self.stream = self.audio.open(
                format=self.audio_format,
                channels=self.channels,
                rate=self.rate,
                input=True,
                frames_per_buffer=self.chunk
            )
            print("✅ 音频录制设置成功")
        except Exception as e:
            print(f"❌ 音频录制设置失败: {e}")
            return False
        return True

    def start_recording(self):
        """开始录制音频"""
        self.recording = True
        self.frames = []
        
        def record_audio():
            while self.recording:
                try:
                    data = self.stream.read(self.chunk, exception_on_overflow=False)
                    self.frames.append(data)
                except Exception as e:
                    print(f"录制错误: {e}")
                    break
        
        self.record_thread = threading.Thread(target=record_audio)
        self.record_thread.start()

    def stop_recording(self):
        """停止录制音频"""
        self.recording = False
        if hasattr(self, 'record_thread'):
            self.record_thread.join()

    def save_audio(self, filename):
        """保存音频文件"""
        if not self.frames:
            print("❌ 没有录制到音频数据")
            return False
            
        try:
            # 保存为 WAV 文件
            wav_path = self.output_dir / f"{filename}.wav"
            with wave.open(str(wav_path), 'wb') as wf:
                wf.setnchannels(self.channels)
                wf.setsampwidth(self.audio.get_sample_size(self.audio_format))
                wf.setframerate(self.rate)
                wf.writeframes(b''.join(self.frames))
            
            # 转换为 MP3
            mp3_path = self.output_dir / f"{filename}.mp3"
            audio = AudioSegment.from_wav(str(wav_path))
            audio.export(str(mp3_path), format="mp3", bitrate="128k")
            
            # 删除临时 WAV 文件
            wav_path.unlink()
            
            print(f"✅ 音频已保存: {mp3_path}")
            return True
            
        except Exception as e:
            print(f"❌ 保存音频失败: {e}")
            return False

    def navigate_to_test_page(self):
        """导航到测试页面"""
        print("🔗 正在打开测试页面...")
        
        try:
            self.driver.get("http://localhost:3000/#/test/audio")
            
            # 等待页面加载
            WebDriverWait(self.driver, 10).until(
                EC.presence_of_element_located((By.TAG_NAME, "button"))
            )
            
            print("✅ 测试页面加载成功")
            return True
            
        except Exception as e:
            print(f"❌ 打开测试页面失败: {e}")
            return False

    def click_button_and_record(self, button_text, filename):
        """点击按钮并录制语音"""
        print(f"🎯 正在录制: {button_text}")
        
        try:
            # 查找并点击按钮
            button = WebDriverWait(self.driver, 5).until(
                EC.element_to_be_clickable((By.XPATH, f"//button[contains(text(), '{button_text}')]"))
            )
            
            # 开始录制
            self.start_recording()
            print("🔴 开始录制...")
            
            # 点击按钮
            button.click()
            
            # 录制 5 秒（可以根据语音长度调整）
            time.sleep(5)
            
            # 停止录制
            self.stop_recording()
            print("⏹️ 录制结束")
            
            # 保存音频
            success = self.save_audio(filename)
            
            if success:
                print(f"✅ {button_text} 录制完成")
            else:
                print(f"❌ {button_text} 录制失败")
                
            # 等待一下再进行下一个录制
            time.sleep(2)
            
            return success
            
        except Exception as e:
            print(f"❌ 录制 {button_text} 时出错: {e}")
            self.stop_recording()
            return False

    def record_all_voices(self):
        """录制所有语音"""
        print("🎵 开始录制所有语音...")
        
        success_count = 0
        total_count = len(self.voice_configs)
        
        for config in self.voice_configs:
            print(f"\n📝 [{success_count + 1}/{total_count}] {config['text']}")
            
            if self.click_button_and_record(config['button_text'], config['id']):
                success_count += 1
            
        print(f"\n🎉 录制完成! 成功: {success_count}/{total_count}")
        
        # 生成配置文件
        self.generate_config_file()

    def generate_config_file(self):
        """生成语音配置文件"""
        config_data = {
            "version": "1.0.0",
            "generated_at": datetime.now().isoformat(),
            "voice_files": {}
        }
        
        for config in self.voice_configs:
            config_data["voice_files"][config["id"]] = {
                "filename": config["filename"],
                "text": config["text"],
                "path": f"/assets/audio/voice/{config['filename']}"
            }
        
        config_path = self.output_dir / "voice-config.json"
        with open(config_path, 'w', encoding='utf-8') as f:
            json.dump(config_data, f, ensure_ascii=False, indent=2)
        
        print(f"✅ 配置文件已生成: {config_path}")

    def cleanup(self):
        """清理资源"""
        print("🧹 正在清理资源...")
        
        if self.stream:
            self.stream.stop_stream()
            self.stream.close()
        
        if self.audio:
            self.audio.terminate()
        
        if self.driver:
            self.driver.quit()
        
        print("✅ 资源清理完成")

    def run(self):
        """运行录制流程"""
        print("🚀 开始网页语音录制...")
        print("=" * 50)
        
        try:
            # 设置浏览器
            self.setup_browser()
            
            # 设置音频录制
            if not self.setup_audio_recording():
                return
            
            # 导航到测试页面
            if not self.navigate_to_test_page():
                return
            
            # 等待用户确认
            input("\n⏸️  请确保页面已加载完成，然后按 Enter 开始录制...")
            
            # 录制所有语音
            self.record_all_voices()
            
        except KeyboardInterrupt:
            print("\n⏹️ 用户中断录制")
        except Exception as e:
            print(f"❌ 录制过程中出错: {e}")
        finally:
            self.cleanup()

def main():
    """主函数"""
    print("🎤 网页语音录制工具")
    print("=" * 30)
    
    # 检查是否在正确的目录
    if not Path("src/renderer").exists():
        print("❌ 请在项目根目录运行此脚本")
        sys.exit(1)
    
    # 检查本地服务器是否运行
    print("⚠️  请确保本地开发服务器正在运行:")
    print("   npm run dev")
    print("   http://localhost:3000 应该可以访问")
    
    confirm = input("\n是否继续? (y/N): ")
    if confirm.lower() != 'y':
        print("❌ 用户取消")
        sys.exit(0)
    
    # 开始录制
    recorder = WebAudioRecorder()
    recorder.run()

if __name__ == "__main__":
    main()
