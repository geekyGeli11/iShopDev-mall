#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
手动语音录制脚本
用于手动录制语音并保存为 MP3 文件

依赖安装:
pip install sounddevice soundfile pydub numpy

使用方法:
python scripts/record-manual-audio.py
"""

import os
import sys
import time
import json
from datetime import datetime
from pathlib import Path

try:
    import sounddevice as sd
    import soundfile as sf
    from pydub import AudioSegment
    import numpy as np
except ImportError as e:
    print(f"缺少依赖库: {e}")
    print("请运行: pip install sounddevice soundfile pydub numpy")
    sys.exit(1)

class ManualAudioRecorder:
    def __init__(self):
        self.sample_rate = 44100
        self.channels = 2
        self.recording = False
        self.audio_data = []
        
        # 输出目录
        self.output_dir = Path("src/renderer/assets/audio/voice")
        self.output_dir.mkdir(parents=True, exist_ok=True)
        
        # 语音配置
        self.voice_configs = [
            {
                "id": "payment_success",
                "filename": "payment_success.mp3",
                "text": "支付成功，欢迎再次光临"
            },
            {
                "id": "payment_failure",
                "filename": "payment_failure.mp3", 
                "text": "支付失败，请重试"
            },
            {
                "id": "scan_to_pay",
                "filename": "scan_to_pay.mp3",
                "text": "请扫码支付"
            },
            {
                "id": "welcome",
                "filename": "welcome.mp3",
                "text": "欢迎使用广横走商城自助收银系统"
            }
        ]

    def list_audio_devices(self):
        """列出可用的音频设备"""
        print("🎧 可用的音频设备:")
        devices = sd.query_devices()
        input_devices = []
        for i, device in enumerate(devices):
            if device['max_input_channels'] > 0:
                input_devices.append((i, device))
                print(f"  {len(input_devices)}: {device['name']} (输入通道: {device['max_input_channels']})")
        return input_devices

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
            audio_array = np.concatenate(self.audio_data, axis=0)
            
            # 保存为 WAV 文件
            wav_path = self.output_dir / f"{filename}.wav"
            sf.write(str(wav_path), audio_array, self.sample_rate)
            
            # 转换为 MP3 (如果有 ffmpeg)
            mp3_path = self.output_dir / f"{filename}.mp3"
            try:
                audio = AudioSegment.from_wav(str(wav_path))
                audio.export(str(mp3_path), format="mp3", bitrate="128k")
                # 删除临时 WAV 文件
                wav_path.unlink()
                print(f"✅ 音频已保存: {mp3_path}")
            except Exception as e:
                print(f"⚠️  MP3 转换失败，保留 WAV 格式: {wav_path}")
                print(f"   错误: {e}")
                # 重命名 WAV 为 MP3 (虽然格式不对，但可以用)
                wav_path.rename(mp3_path)
                print(f"✅ 音频已保存: {mp3_path} (WAV格式)")
            
            return True
            
        except Exception as e:
            print(f"❌ 保存音频失败: {e}")
            return False

    def record_voice(self, config, device_id=None):
        """录制单个语音"""
        print(f"\n📝 准备录制: {config['text']}")
        print(f"🎯 文件名: {config['filename']}")
        
        input("⏸️  请准备好，然后按 Enter 开始录制...")
        
        # 开始录制
        if not self.start_recording(device_id):
            return False
            
        print("🔴 正在录制... (5秒后自动停止)")
        print("   请现在说出语音内容!")
        
        # 录制 5 秒
        time.sleep(5)
        
        # 停止录制
        self.stop_recording()
        print("⏹️ 录制结束")
        
        # 保存音频
        success = self.save_audio(config['id'])
        
        if success:
            print(f"✅ {config['text']} 录制完成")
        else:
            print(f"❌ {config['text']} 录制失败")
            
        return success

    def record_all_voices(self, device_id=None):
        """录制所有语音"""
        print("🎵 开始录制所有语音...")
        print("\n📋 录制清单:")
        for i, config in enumerate(self.voice_configs):
            print(f"  {i+1}. {config['text']}")
        
        print("\n⚠️  录制说明:")
        print("1. 每个语音录制 5 秒")
        print("2. 按 Enter 开始录制")
        print("3. 听到提示后立即说出语音内容")
        print("4. 请保持安静的环境")
        
        input("\n准备好了吗？按 Enter 开始...")
        
        success_count = 0
        total_count = len(self.voice_configs)
        
        for i, config in enumerate(self.voice_configs):
            print(f"\n📝 [{i + 1}/{total_count}] {config['text']}")
            
            if self.record_voice(config, device_id):
                success_count += 1
            
            if i < total_count - 1:
                print("\n⏳ 等待 2 秒后继续下一个...")
                time.sleep(2)
            
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
        if hasattr(self, 'stream'):
            try:
                self.stream.stop()
                self.stream.close()
            except:
                pass

    def run(self):
        """运行录制流程"""
        print("🎤 手动语音录制工具")
        print("=" * 30)
        
        try:
            # 列出音频设备
            input_devices = self.list_audio_devices()
            
            if not input_devices:
                print("❌ 没有找到可用的音频输入设备")
                return
            
            # 选择音频设备
            print("\n🎧 选择音频输入设备:")
            print("0. 使用默认设备")
            for i, (device_id, device) in enumerate(input_devices):
                print(f"{i+1}. {device['name']}")
            
            device_choice = input("请选择设备编号 (默认为0): ").strip()
            device_id = None
            if device_choice and device_choice != "0":
                try:
                    choice_idx = int(device_choice) - 1
                    if 0 <= choice_idx < len(input_devices):
                        device_id = input_devices[choice_idx][0]
                        print(f"✅ 已选择: {input_devices[choice_idx][1]['name']}")
                    else:
                        print("⚠️  无效选择，使用默认设备")
                except:
                    print("⚠️  无效选择，使用默认设备")
            else:
                print("✅ 使用默认设备")
            
            # 录制所有语音
            self.record_all_voices(device_id)
            
        except KeyboardInterrupt:
            print("\n⏹️ 用户中断录制")
        except Exception as e:
            print(f"❌ 录制过程中出错: {e}")
        finally:
            self.cleanup()

def main():
    """主函数"""
    print("🎤 手动语音录制工具")
    print("=" * 30)
    
    # 检查是否在正确的目录
    if not Path("src/renderer").exists():
        print("❌ 请在项目根目录运行此脚本")
        sys.exit(1)
    
    print("⚠️  使用说明:")
    print("1. 确保麦克风正常工作")
    print("2. 选择合适的录音设备")
    print("3. 保持安静的录音环境")
    print("4. 按提示录制每个语音")
    
    confirm = input("\n是否继续? (y/N): ")
    if confirm.lower() != 'y':
        print("❌ 用户取消")
        sys.exit(0)
    
    # 开始录制
    recorder = ManualAudioRecorder()
    recorder.run()

if __name__ == "__main__":
    main()
