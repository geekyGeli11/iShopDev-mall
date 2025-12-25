@echo off
echo ================================
echo TTS 功能测试脚本
echo ================================
echo.

echo 1. 构建 Capacitor 项目...
call npm run build:capacitor
if %errorlevel% neq 0 (
    echo 构建失败！
    pause
    exit /b 1
)

echo.
echo 2. 同步到 Android...
call npx cap sync android
if %errorlevel% neq 0 (
    echo 同步失败！
    pause
    exit /b 1
)

echo.
echo 3. 打开 Android Studio...
call npx cap open android

echo.
echo ================================
echo 测试说明：
echo ================================
echo 1. 在 Android Studio 中运行应用
echo 2. 访问 /test/tts 页面测试 TTS 功能
echo 3. 进行支付流程测试语音播报
echo.
echo 测试要点：
echo - 检查 TTS 初始化状态
echo - 测试支付成功语音播报
echo - 测试支付失败语音播报
echo - 调节语音参数（语速、音调、音量）
echo - 验证不同语言支持
echo.
pause
