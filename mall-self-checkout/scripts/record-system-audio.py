#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
系统音频录制脚本 (简化版)
用于录制系统播放的音频并保存为 MP3 文件

依赖安装:
pip install selenium webdriver-manager sounddevice soundfile pydub

使用方法:
1. 启动开发服务器: npm run dev
2. 运行脚本: python scripts/record-system-audio.py
"""

import os
import sys
import time
import json
import threading
from datetime import datetime
from pathlib import Path

try:
    import sounddevice as sd
    import soundfile as sf
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
    print("请运行: pip install selenium webdriver-manager sounddevice soundfile pydub")
    sys.exit(1)

class SystemAudioRecorder:
    def __init__(self):
        self.sample_rate = 44100
        self.channels = 2
        self.recording = False
        self.audio_data = []
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

    def list_audio_devices(self):
        """列出可用的音频设备"""
        print("🎧 可用的音频设备:")
        devices = sd.query_devices()
        for i, device in enumerate(devices):
            if device['max_input_channels'] > 0:
                print(f"  {i}: {device['name']} (输入通道: {device['max_input_channels']})")
        return devices

    def setup_browser(self):
        """设置浏览器"""
        print("🌐 正在启动浏览器...")
        
        chrome_options = Options()
        chrome_options.add_argument("--autoplay-policy=no-user-gesture-required")
        chrome_options.add_argument("--disable-web-security")
        chrome_options.add_argument("--allow-running-insecure-content")
        
        # 自动下载并设置 ChromeDriver
        service = Service(ChromeDriverManager().install())
        self.driver = webdriver.Chrome(service=service, options=chrome_options)
        
        print("✅ 浏览器启动成功")

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

    def record_audio_callback(self, indata, frames, time, status):
        """音频录制回调函数"""
        if self.recording:
            self.audio_data.append(indata.copy())

    def start_recording(self, device_id=None):
        """开始录制音频"""
        self.recording = True
        self.audio_data = []
        
        try:
            self.stream = sd.InputStream(
                device=device_id,
                channels=self.channels,
                samplerate=self.sample_rate,
                callback=self.record_audio_callback
            )
            self.stream.start()
            return True
        except Exception as e:
            print(f"❌ 开始录制失败: {e}")
            return False

    def stop_recording(self):
        """停止录制音频"""
        self.recording = False
        if hasattr(self, 'stream'):
            self.stream.stop()
            self.stream.close()

    def save_audio(self, filename):
        """保存音频文件"""
        if not self.audio_data:
            print("❌ 没有录制到音频数据")
            return False
            
        try:
            # 合并音频数据
            import numpy as np
            audio_array = np.concatenate(self.audio_data, axis=0)
            
            # 保存为 WAV 文件
            wav_path = self.output_dir / f"{filename}.wav"
            sf.write(str(wav_path), audio_array, self.sample_rate)
            
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

    def manual_record_voice(self, config):
        """手动录制单个语音"""
        print(f"\n📝 准备录制: {config['text']}")
        print(f"🎯 按钮文本: {config['button_text']}")
        
        input("⏸️  请手动点击网页上的按钮，然后立即按 Enter 开始录制...")
        
        # 开始录制
        if not self.start_recording():
            return False
            
        print("🔴 正在录制... (5秒后自动停止)")
        
        # 录制 5 秒
        time.sleep(5)
        
        # 停止录制
        self.stop_recording()
        print("⏹️ 录制结束")
        
        # 保存音频
        success = self.save_audio(config['id'])
        
        if success:
            print(f"✅ {config['button_text']} 录制完成")
        else:
            print(f"❌ {config['button_text']} 录制失败")
            
        return success

    def auto_record_voice(self, config, device_id=None):
        """自动录制单个语音"""
        print(f"\n📝 正在录制: {config['text']}")
        
        try:
            # 查找并点击按钮
            button = WebDriverWait(self.driver, 5).until(
                EC.element_to_be_clickable((By.XPATH, f"//button[contains(text(), '{config['button_text']}')]"))
            )
            
            # 开始录制
            if not self.start_recording(device_id):
                return False
                
            print("🔴 开始录制...")
            
            # 点击按钮
            button.click()
            
            # 录制 5 秒
            time.sleep(5)
            
            # 停止录制
            self.stop_recording()
            print("⏹️ 录制结束")
            
            # 保存音频
            success = self.save_audio(config['id'])
            
            if success:
                print(f"✅ {config['button_text']} 录制完成")
            else:
                print(f"❌ {config['button_text']} 录制失败")
                
            # 等待一下再进行下一个录制
            time.sleep(2)
            
            return success
            
        except Exception as e:
            print(f"❌ 录制 {config['button_text']} 时出错: {e}")
            self.stop_recording()
            return False

    def record_all_voices(self, manual_mode=False, device_id=None):
        """录制所有语音"""
        print("🎵 开始录制所有语音...")
        
        success_count = 0
        total_count = len(self.voice_configs)
        
        for i, config in enumerate(self.voice_configs):
            print(f"\n📝 [{i + 1}/{total_count}] {config['text']}")
            
            if manual_mode:
                success = self.manual_record_voice(config)
            else:
                success = self.auto_record_voice(config, device_id)
                
            if success:
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
        
        if hasattr(self, 'stream'):
            try:
                self.stream.stop()
                self.stream.close()
            except:
                pass
        
        if self.driver:
            self.driver.quit()
        
        print("✅ 资源清理完成")

    def run(self):
        """运行录制流程"""
        print("🚀 开始系统音频录制...")
        print("=" * 50)
        
        try:
            # 列出音频设备
            devices = self.list_audio_devices()
            
            # 选择录制模式
            print("\n🎛️  选择录制模式:")
            print("1. 自动模式 (推荐)")
            print("2. 手动模式")
            
            mode = input("请选择 (1/2): ").strip()
            manual_mode = mode == "2"
            
            device_id = None
            if not manual_mode:
                # 选择音频设备
                print("\n🎧 选择音频输入设备:")
                print("0. 使用默认设备")
                for i, device in enumerate(devices):
                    if device['max_input_channels'] > 0:
                        print(f"{i+1}. {device['name']}")
                
                device_choice = input("请选择设备编号 (默认为0): ").strip()
                if device_choice and device_choice != "0":
                    try:
                        device_id = int(device_choice) - 1
                    except:
                        print("⚠️  无效选择，使用默认设备")
            
            # 设置浏览器
            self.setup_browser()
            
            # 导航到测试页面
            if not self.navigate_to_test_page():
                return
            
            # 等待用户确认
            if manual_mode:
                print("\n⚠️  手动模式说明:")
                print("1. 脚本会提示您点击按钮")
                print("2. 您手动点击网页上的按钮后立即按 Enter")
                print("3. 脚本会录制 5 秒音频")
            
            input("\n⏸️  请确保页面已加载完成，然后按 Enter 开始录制...")
            
            # 录制所有语音
            self.record_all_voices(manual_mode, device_id)
            
        except KeyboardInterrupt:
            print("\n⏹️ 用户中断录制")
        except Exception as e:
            print(f"❌ 录制过程中出错: {e}")
        finally:
            self.cleanup()

def main():
    """主函数"""
    print("🎤 系统音频录制工具")
    print("=" * 30)
    
    # 检查是否在正确的目录
    if not Path("src/renderer").exists():
        print("❌ 请在项目根目录运行此脚本")
        sys.exit(1)
    
    # 检查本地服务器是否运行
    print("⚠️  请确保:")
    print("1. 本地开发服务器正在运行: npm run dev")
    print("2. http://localhost:3000 可以访问")
    print("3. 系统音量已打开")
    
    confirm = input("\n是否继续? (y/N): ")
    if confirm.lower() != 'y':
        print("❌ 用户取消")
        sys.exit(0)
    
    # 开始录制
    recorder = SystemAudioRecorder()
    recorder.run()

if __name__ == "__main__":
    main()
