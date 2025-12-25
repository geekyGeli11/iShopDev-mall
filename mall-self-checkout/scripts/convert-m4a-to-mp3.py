#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
M4A 转 MP3 转换脚本
将 iPhone 录制的 m4a 音频文件转换为 mp3 格式

依赖安装:
pip install pydub

使用方法:
python scripts/convert-m4a-to-mp3.py
"""

import os
import sys
import json
from pathlib import Path
from datetime import datetime

try:
    from pydub import AudioSegment
except ImportError as e:
    print(f"缺少依赖库: {e}")
    print("请运行: pip install pydub")
    sys.exit(1)

class AudioConverter:
    def __init__(self):
        # 音频文件目录
        self.audio_dir = Path("src/renderer/assets/audio/voice")
        
        # 文件映射：m4a文件名 -> 目标mp3文件名
        self.file_mapping = {
            "支付成功.m4a": "payment_success.mp3",
            "支付失败.m4a": "payment_failure.mp3", 
            "扫码支付.m4a": "scan_to_pay.mp3",
            "欢迎光临.m4a": "welcome.mp3"
        }
        
        # 语音配置
        self.voice_configs = {
            "payment_success": {
                "filename": "payment_success.mp3",
                "text": "支付成功，欢迎再次光临",
                "path": "/assets/audio/voice/payment_success.mp3"
            },
            "payment_failure": {
                "filename": "payment_failure.mp3", 
                "text": "支付失败，请重试",
                "path": "/assets/audio/voice/payment_failure.mp3"
            },
            "scan_to_pay": {
                "filename": "scan_to_pay.mp3",
                "text": "请扫码支付",
                "path": "/assets/audio/voice/scan_to_pay.mp3"
            },
            "welcome": {
                "filename": "welcome.mp3",
                "text": "欢迎使用广横走商城自助收银系统",
                "path": "/assets/audio/voice/welcome.mp3"
            }
        }

    def check_files(self):
        """检查 m4a 文件是否存在"""
        print("🔍 检查音频文件...")
        
        missing_files = []
        existing_files = []
        
        for m4a_file, mp3_file in self.file_mapping.items():
            m4a_path = self.audio_dir / m4a_file
            if m4a_path.exists():
                file_size = m4a_path.stat().st_size
                print(f"✅ 找到: {m4a_file} ({file_size} bytes)")
                existing_files.append((m4a_file, mp3_file))
            else:
                print(f"❌ 缺失: {m4a_file}")
                missing_files.append(m4a_file)
        
        if missing_files:
            print(f"\n⚠️  缺少 {len(missing_files)} 个文件:")
            for file in missing_files:
                print(f"   - {file}")
        
        return existing_files, missing_files

    def convert_file(self, m4a_file, mp3_file):
        """转换单个文件"""
        m4a_path = self.audio_dir / m4a_file
        mp3_path = self.audio_dir / mp3_file
        
        try:
            print(f"🔄 转换: {m4a_file} -> {mp3_file}")
            
            # 加载 m4a 文件
            audio = AudioSegment.from_file(str(m4a_path), format="m4a")
            
            # 转换为 mp3
            audio.export(str(mp3_path), format="mp3", bitrate="128k")
            
            # 检查输出文件
            if mp3_path.exists():
                output_size = mp3_path.stat().st_size
                print(f"✅ 转换成功: {mp3_file} ({output_size} bytes)")
                return True
            else:
                print(f"❌ 转换失败: {mp3_file}")
                return False
                
        except Exception as e:
            print(f"❌ 转换失败: {m4a_file} -> {mp3_file}")
            print(f"   错误: {e}")
            return False

    def convert_all_files(self):
        """转换所有文件"""
        existing_files, missing_files = self.check_files()
        
        if not existing_files:
            print("❌ 没有找到可转换的 m4a 文件")
            return False
        
        print(f"\n🚀 开始转换 {len(existing_files)} 个文件...")
        
        success_count = 0
        total_count = len(existing_files)
        
        for m4a_file, mp3_file in existing_files:
            if self.convert_file(m4a_file, mp3_file):
                success_count += 1
        
        print(f"\n🎉 转换完成! 成功: {success_count}/{total_count}")
        
        if success_count > 0:
            self.generate_config_file()
            self.backup_m4a_files()
        
        return success_count == total_count

    def generate_config_file(self):
        """生成语音配置文件"""
        config_data = {
            "version": "1.0.0",
            "generated_at": datetime.now().isoformat(),
            "source": "iPhone录制转换",
            "voice_files": self.voice_configs
        }
        
        config_path = self.audio_dir / "voice-config.json"
        with open(config_path, 'w', encoding='utf-8') as f:
            json.dump(config_data, f, ensure_ascii=False, indent=2)
        
        print(f"✅ 配置文件已生成: {config_path}")

    def backup_m4a_files(self):
        """备份 m4a 文件"""
        backup_dir = self.audio_dir / "backup_m4a"
        backup_dir.mkdir(exist_ok=True)
        
        print(f"📦 备份 m4a 文件到: {backup_dir}")
        
        for m4a_file in self.file_mapping.keys():
            m4a_path = self.audio_dir / m4a_file
            if m4a_path.exists():
                backup_path = backup_dir / m4a_file
                try:
                    # 复制文件到备份目录
                    import shutil
                    shutil.copy2(str(m4a_path), str(backup_path))
                    print(f"✅ 已备份: {m4a_file}")
                except Exception as e:
                    print(f"⚠️  备份失败: {m4a_file} - {e}")

    def list_output_files(self):
        """列出输出的 mp3 文件"""
        print("\n📁 生成的 MP3 文件:")
        for voice_id, config in self.voice_configs.items():
            mp3_path = self.audio_dir / config["filename"]
            if mp3_path.exists():
                file_size = mp3_path.stat().st_size
                print(f"✅ {config['filename']} ({file_size} bytes) - {config['text']}")
            else:
                print(f"❌ {config['filename']} - 文件不存在")

def main():
    """主函数"""
    print("🎵 M4A 转 MP3 转换工具")
    print("=" * 40)
    
    # 检查是否在正确的目录
    if not Path("src/renderer").exists():
        print("❌ 请在项目根目录运行此脚本")
        sys.exit(1)
    
    print("📱 将 iPhone 录制的 m4a 文件转换为 mp3 格式")
    print("📂 音频文件目录: src/renderer/assets/audio/voice")
    
    converter = AudioConverter()
    
    # 检查文件
    existing_files, missing_files = converter.check_files()
    
    if not existing_files:
        print("\n❌ 没有找到可转换的 m4a 文件")
        print("请确保以下文件存在:")
        for m4a_file in converter.file_mapping.keys():
            print(f"   - {m4a_file}")
        sys.exit(1)
    
    print(f"\n📋 将要转换的文件:")
    for m4a_file, mp3_file in existing_files:
        print(f"   {m4a_file} -> {mp3_file}")
    
    if missing_files:
        print(f"\n⚠️  缺少的文件:")
        for file in missing_files:
            print(f"   - {file}")
    
    confirm = input("\n是否开始转换? (y/N): ")
    if confirm.lower() != 'y':
        print("❌ 用户取消")
        sys.exit(0)
    
    # 开始转换
    success = converter.convert_all_files()
    
    # 列出输出文件
    converter.list_output_files()
    
    if success:
        print("\n🎉 所有文件转换成功!")
        print("💡 接下来请运行以下命令重新构建应用:")
        print("   npm run build:capacitor:dev")
        print("   npx cap sync android")
    else:
        print("\n⚠️  部分文件转换失败，请检查错误信息")

if __name__ == "__main__":
    main()
