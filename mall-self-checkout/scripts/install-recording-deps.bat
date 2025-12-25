@echo off
chcp 65001 >nul
echo 🎤 安装网页语音录制依赖
echo ================================

echo.
echo 📦 正在安装 Python 依赖...
echo.

REM 检查 Python 是否安装
python --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 未找到 Python，请先安装 Python 3.7+
    echo 下载地址: https://www.python.org/downloads/
    pause
    exit /b 1
)

echo ✅ Python 已安装
python --version

echo.
echo 📦 安装依赖包...

REM 升级 pip
echo 🔄 升级 pip...
python -m pip install --upgrade pip

REM 安装核心依赖
echo 📦 安装 selenium (网页自动化)...
pip install selenium

echo 📦 安装 webdriver-manager (自动管理浏览器驱动)...
pip install webdriver-manager

echo 📦 安装 sounddevice (音频录制)...
pip install sounddevice

echo 📦 安装 soundfile (音频文件处理)...
pip install soundfile

echo 📦 安装 pydub (音频处理)...
pip install pydub

echo 📦 安装 numpy (数值计算)...
pip install numpy

echo.
echo ⚠️  注意事项:
echo 1. 需要安装 FFmpeg 用于音频转换:
echo    - 下载: https://ffmpeg.org/download.html
echo    - 或使用 chocolatey: choco install ffmpeg
echo.
echo 2. 确保 Chrome 浏览器已安装
echo.
echo 3. 如果 sounddevice 安装失败，请尝试:
echo    - 安装 Microsoft Visual C++ 14.0 或更高版本
echo    - 或使用 conda: conda install sounddevice
echo.

echo ✅ 依赖安装完成!
echo.
echo 🚀 使用方法:
echo 1. 启动开发服务器: npm run dev
echo 2. 运行录制脚本:
echo    - 自动录制: python scripts/record-system-audio.py
echo    - 手动录制: python scripts/record-audio-from-web.py
echo.

pause
