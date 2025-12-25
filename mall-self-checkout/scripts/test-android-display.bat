@echo off
echo ========================================
echo Android 显示适配测试脚本
echo ========================================
echo.

echo 1. 构建项目...
call npm run build
if %errorlevel% neq 0 (
    echo 构建失败！
    pause
    exit /b 1
)

echo.
echo 2. 同步到Android...
call npx cap sync android
if %errorlevel% neq 0 (
    echo 同步失败！
    pause
    exit /b 1
)

echo.
echo 3. 构建Android APK...
cd android
call gradlew assembleDebug
if %errorlevel% neq 0 (
    echo Android构建失败！
    pause
    exit /b 1
)

echo.
echo ========================================
echo 构建完成！
echo ========================================
echo.
echo APK文件位置：
echo android\app\build\outputs\apk\debug\app-debug.apk
echo.
echo 测试要点：
echo 1. 在1920x1080设备上安装APK
echo 2. 检查是否全屏显示，无内容截断
echo 3. 检查按钮是否完整显示
echo 4. 检查时间显示是否在正确位置
echo 5. 检查注册提示是否可见
echo.
echo 如果仍有问题，请检查：
echo - 设备的显示设置（缩放比例）
echo - 系统导航栏设置
echo - WebView版本
echo.
pause
