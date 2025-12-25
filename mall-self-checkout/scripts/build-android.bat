@echo off
echo ========================================
echo Mall自助收银系统 - Android APK构建脚本
echo ========================================

chcp 65001

REM 获取环境参数，默认为dev（开发环境）
set BUILD_ENV=%1
if "%BUILD_ENV%"=="" set BUILD_ENV=dev

REM 获取版本参数
set VERSION_NAME=%2
set VERSION_CODE=%3

echo 构建环境: %BUILD_ENV%
if not "%VERSION_NAME%"=="" (
    echo 版本名称: %VERSION_NAME%
)
if not "%VERSION_CODE%"=="" (
    echo 版本号: %VERSION_CODE%
)
echo.

REM 检查环境
echo.
echo 🔍 检查构建环境...
if not exist "android\local.properties" (
    echo ❌ 未找到 android\local.properties 文件
    echo 请先运行: scripts\check-android-env.bat
    pause
    exit /b 1
)

if not exist "node_modules" (
    echo ❌ 项目依赖未安装
    echo 正在安装依赖...
    call npm install
    if %errorlevel% neq 0 (
        echo 依赖安装失败！
        pause
        exit /b 1
    )
)

echo ✅ 环境检查通过

echo.
echo 🧹 1. 清理之前的构建...
if exist "dist\renderer" rmdir /s /q "dist\renderer"

echo.
echo � 检查Vite配置（修复Android 7.1.2白屏问题）...
findstr "base: './'," vite.config.capacitor.ts
if %errorlevel% neq 0 (
    echo ⚠️  警告: vite.config.capacitor.ts 中未找到 base: './' 配置
    echo 这可能导致在Android 7.1.2设备上出现白屏问题
    echo 建议检查配置文件
)

echo.
echo �🔨 2. 构建Web应用（%BUILD_ENV%环境）...
if "%BUILD_ENV%"=="dev" (
    echo 使用开发环境配置（适用于MuMu模拟器）
    call npm run build:capacitor:dev
) else if "%BUILD_ENV%"=="staging" (
    echo 使用预发布环境配置
    call npm run build:capacitor:staging
) else if "%BUILD_ENV%"=="prod" (
    echo 使用生产环境配置
    call npm run build:capacitor
) else (
    echo ❌ 不支持的环境: %BUILD_ENV%
    echo 支持的环境: dev, staging, prod
    pause
    exit /b 1
)

if %errorlevel% neq 0 (
    echo ❌ 构建Web应用失败！
    pause
    exit /b 1
)

echo.
echo  4. 同步到Android项目...
call npx cap sync android
if %errorlevel% neq 0 (
    echo ❌ 同步Android项目失败！
    pause
    exit /b 1
)

echo.
echo 🔍 验证资源路径配置...
findstr "src=\"./assets/" android\app\src\main\assets\public\index.html
if %errorlevel% equ 0 (
    echo ✅ 资源路径配置正确（使用相对路径）
) else (
    echo ⚠️  警告: 检测到可能的绝对路径，这可能导致Android 7.1.2设备白屏
)

REM 更新版本信息（如果提供了版本参数）
if not "%VERSION_NAME%"=="" (
    echo.
    echo 📝 4. 更新版本信息...
    echo 版本名称: %VERSION_NAME%
    if not "%VERSION_CODE%"=="" (
        echo 版本号: %VERSION_CODE%
    )
    echo 注意: 版本信息需要手动在 android\app\build.gradle 中更新
    echo 版本信息显示完成
)

echo.
echo 📱 5. 构建Android APK...
cd android
call .\gradlew assembleDebug
if %errorlevel% neq 0 (
    echo ❌ 构建Android APK失败！
    cd ..
    pause
    exit /b 1
)

echo.
echo ✅ 6. 构建完成！
cd ..

REM 检查APK文件
if exist "android\app\build\outputs\apk\debug\app-debug.apk" (
    for %%F in ("android\app\build\outputs\apk\debug\app-debug.apk") do (
        echo 📦 APK文件: %%~nxF
        echo 📏 文件大小: %%~zF 字节
        echo 📍 完整路径: %%~fF
    )
    
    echo.
    echo 📱 安装命令（需要连接Android设备）:
    echo   adb install android\app\build\outputs\apk\debug\app-debug.apk
    
    echo.
    echo 使用说明:
    echo   开发环境（默认，适用于MuMu模拟器）
    echo   开发环境（API: http://10.0.2.2:8201）
    echo   预发布环境
    echo   生产环境
    echo   指定版本名称和版本号

    echo.
    echo 发布版本构建:
    echo   使用 build-android-release.bat
) else (
    echo ❌ 未找到APK文件！
)

echo.
pause 