#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
音频文件重命名脚本
将中文名称的 m4a 文件重命名为英文名称

使用方法:
python scripts/rename-audio-files.py
"""

import os
import sys
import json
import shutil
from pathlib import Path
from datetime import datetime

class AudioRenamer:
    def __init__(self):
        # 音频文件目录
        self.audio_dir = Path("src/renderer/assets/audio/voice")
        
        # 文件映射：中文名 -> 英文名
        self.file_mapping = {
            "支付成功.m4a": "payment_success.m4a",
            "支付失败.m4a": "payment_failure.m4a", 
            "扫码支付.m4a": "scan_to_pay.m4a",
            "欢迎光临.m4a": "welcome.m4a"
        }
        
        # 语音配置
        self.voice_configs = {
            "payment_success": {
                "filename": "payment_success.m4a",
                "text": "支付成功，欢迎再次光临",
                "path": "/assets/audio/voice/payment_success.m4a"
            },
            "payment_failure": {
                "filename": "payment_failure.m4a", 
                "text": "支付失败，请重试",
                "path": "/assets/audio/voice/payment_failure.m4a"
            },
            "scan_to_pay": {
                "filename": "scan_to_pay.m4a",
                "text": "请扫码支付",
                "path": "/assets/audio/voice/scan_to_pay.m4a"
            },
            "welcome": {
                "filename": "welcome.m4a",
                "text": "欢迎使用广横走商城自助收银系统",
                "path": "/assets/audio/voice/welcome.m4a"
            }
        }

    def check_files(self):
        """检查文件是否存在"""
        print("🔍 检查音频文件...")
        
        existing_files = []
        missing_files = []
        
        for chinese_name, english_name in self.file_mapping.items():
            chinese_path = self.audio_dir / chinese_name
            english_path = self.audio_dir / english_name
            
            if chinese_path.exists():
                file_size = chinese_path.stat().st_size
                print(f"✅ 找到: {chinese_name} ({file_size} bytes)")
                existing_files.append((chinese_name, english_name))
            elif english_path.exists():
                file_size = english_path.stat().st_size
                print(f"✅ 已存在: {english_name} ({file_size} bytes)")
            else:
                print(f"❌ 缺失: {chinese_name}")
                missing_files.append(chinese_name)
        
        return existing_files, missing_files

    def rename_file(self, chinese_name, english_name):
        """重命名单个文件"""
        chinese_path = self.audio_dir / chinese_name
        english_path = self.audio_dir / english_name
        
        try:
            print(f"📝 重命名: {chinese_name} -> {english_name}")
            
            # 如果目标文件已存在，先备份
            if english_path.exists():
                backup_path = self.audio_dir / f"{english_name}.backup"
                shutil.move(str(english_path), str(backup_path))
                print(f"📦 备份已存在的文件: {english_name}.backup")
            
            # 重命名文件
            shutil.move(str(chinese_path), str(english_path))
            
            # 检查结果
            if english_path.exists():
                file_size = english_path.stat().st_size
                print(f"✅ 重命名成功: {english_name} ({file_size} bytes)")
                return True
            else:
                print(f"❌ 重命名失败: {english_name}")
                return False
                
        except Exception as e:
            print(f"❌ 重命名失败: {chinese_name} -> {english_name}")
            print(f"   错误: {e}")
            return False

    def rename_all_files(self):
        """重命名所有文件"""
        existing_files, missing_files = self.check_files()
        
        if not existing_files:
            print("❌ 没有找到需要重命名的文件")
            return False
        
        print(f"\n🚀 开始重命名 {len(existing_files)} 个文件...")
        
        success_count = 0
        total_count = len(existing_files)
        
        for chinese_name, english_name in existing_files:
            if self.rename_file(chinese_name, english_name):
                success_count += 1
        
        print(f"\n🎉 重命名完成! 成功: {success_count}/{total_count}")
        
        if success_count > 0:
            self.generate_config_file()
        
        return success_count == total_count

    def generate_config_file(self):
        """生成语音配置文件"""
        config_data = {
            "version": "1.0.0",
            "generated_at": datetime.now().isoformat(),
            "source": "iPhone录制重命名",
            "format": "m4a",
            "voice_files": self.voice_configs
        }
        
        config_path = self.audio_dir / "voice-config.json"
        with open(config_path, 'w', encoding='utf-8') as f:
            json.dump(config_data, f, ensure_ascii=False, indent=2)
        
        print(f"✅ 配置文件已生成: {config_path}")

    def list_output_files(self):
        """列出输出的文件"""
        print("\n📁 重命名后的音频文件:")
        for voice_id, config in self.voice_configs.items():
            file_path = self.audio_dir / config["filename"]
            if file_path.exists():
                file_size = file_path.stat().st_size
                print(f"✅ {config['filename']} ({file_size} bytes) - {config['text']}")
            else:
                print(f"❌ {config['filename']} - 文件不存在")

def main():
    """主函数"""
    print("📝 音频文件重命名工具")
    print("=" * 40)
    
    # 检查是否在正确的目录
    if not Path("src/renderer").exists():
        print("❌ 请在项目根目录运行此脚本")
        sys.exit(1)
    
    print("📱 将中文名称的 m4a 文件重命名为英文名称")
    print("📂 音频文件目录: src/renderer/assets/audio/voice")
    
    renamer = AudioRenamer()
    
    # 检查文件
    existing_files, missing_files = renamer.check_files()
    
    if not existing_files:
        print("\n❌ 没有找到需要重命名的文件")
        print("请确保以下文件存在:")
        for chinese_name in renamer.file_mapping.keys():
            print(f"   - {chinese_name}")
        sys.exit(1)
    
    print(f"\n📋 将要重命名的文件:")
    for chinese_name, english_name in existing_files:
        print(f"   {chinese_name} -> {english_name}")
    
    if missing_files:
        print(f"\n⚠️  缺少的文件:")
        for file in missing_files:
            print(f"   - {file}")
    
    confirm = input("\n是否开始重命名? (y/N): ")
    if confirm.lower() != 'y':
        print("❌ 用户取消")
        sys.exit(0)
    
    # 开始重命名
    success = renamer.rename_all_files()
    
    # 列出输出文件
    renamer.list_output_files()
    
    if success:
        print("\n🎉 所有文件重命名成功!")
        print("💡 接下来将更新代码以支持 m4a 格式")
    else:
        print("\n⚠️  部分文件重命名失败，请检查错误信息")

if __name__ == "__main__":
    main()
