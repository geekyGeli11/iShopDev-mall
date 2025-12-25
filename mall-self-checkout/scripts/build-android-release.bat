@echo off
echo ========================================
echo Mall自助收银系统 - Android发布版APK构建脚本
echo ========================================

chcp 65001

REM 获取环境参数，默认为prod（生产环境）
set BUILD_ENV=%1
if "%BUILD_ENV%"=="" set BUILD_ENV=prod

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

echo.
echo 🔍 检查密钥库配置...
if not exist "android\keystore.properties" (
    echo ❌ 未找到密钥库配置文件 android\keystore.properties
    echo.
    echo 🔑 请先生成密钥库：
    echo   keytool -genkeypair -v -keystore android\mall-release-key.keystore -alias mall-key-alias -keyalg RSA -keysize 2048 -validity 10000 -storepass Mall123456 -keypass Mall123456 -dname "CN=Mall"
    echo.
    echo 📝 然后创建 android\keystore.properties 文件：
    echo   storeFile=mall-release-key.keystore
    echo   storePassword=Mall123456
    echo   keyAlias=mall-key-alias
    echo   keyPassword=Mall123456
    pause
    exit /b 1
)

echo ✅ 密钥库配置文件存在

if not exist "android\mall-release-key.keystore" (
    echo ❌ 未找到密钥库文件 android\mall-release-key.keystore
    echo.
    echo 🔑 正在自动生成密钥库...
    keytool -genkeypair -v -keystore android\mall-release-key.keystore -alias mall-key-alias -keyalg RSA -keysize 2048 -validity 10000 -storepass Mall123456 -keypass Mall123456 -dname "CN=Mall"
    if %errorlevel% neq 0 (
        echo ❌ 密钥库生成失败！
        pause
        exit /b 1
    )
    echo ✅ 密钥库生成成功
) else (
    echo ✅ 密钥库文件存在
)

echo.
echo 🧹 1. 清理之前的构建...
if exist "dist\renderer" rmdir /s /q "dist\renderer"

echo.
echo 🔧 检查Vite配置（修复Android 7.1.2白屏问题）...
findstr /C:"base: './'," vite.config.capacitor.ts >nul
if %errorlevel% neq 0 (
    echo ⚠️  警告: vite.config.capacitor.ts 中未找到 base: './' 配置
    echo 这可能导致在Android 7.1.2设备上出现白屏问题
    echo 建议检查配置文件
)

echo.
echo 🔨 2. 构建Web应用（%BUILD_ENV%环境）...
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
echo 🔧 3. 修复Android 7.1.2兼容性...
call scripts\fix-android7-compatibility.bat

echo.
echo 🔄 4. 同步到Android项目...
call npx cap sync android
if %errorlevel% neq 0 (
    echo ❌ 同步Android项目失败！
    pause
    exit /b 1
)

echo.
echo 🔍 验证资源路径配置...
findstr /C:"src=\"./assets/" android\app\src\main\assets\public\index.html >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ 资源路径配置正确（使用相对路径）
) else (
    echo ⚠️  警告: 检测到可能的绝对路径，这可能导致Android 7.1.2设备白屏
)

REM 更新版本信息（如果提供了版本参数）
if not "%VERSION_NAME%"=="" (
    echo.
    echo 📝 4. 更新版本信息...
    echo 正在更新 versionName 为: %VERSION_NAME%

    REM 备份原始文件
    copy android\app\build.gradle android\app\build.gradle.backup >nul 2>&1

    REM 使用PowerShell更新build.gradle中的版本信息（指定UTF-8编码，无BOM）
    powershell -Command "$content = Get-Content android\app\build.gradle -Raw -Encoding UTF8; $content = $content -replace 'versionName \"[^\"]*\"', 'versionName \"%VERSION_NAME%\"'; [System.IO.File]::WriteAllText('android\app\build.gradle', $content, [System.Text.UTF8Encoding]::new($false))"

    if not "%VERSION_CODE%"=="" (
        echo 正在更新 versionCode 为: %VERSION_CODE%
        powershell -Command "$content = Get-Content android\app\build.gradle -Raw -Encoding UTF8; $content = $content -replace 'versionCode [0-9]+', 'versionCode %VERSION_CODE%'; [System.IO.File]::WriteAllText('android\app\build.gradle', $content, [System.Text.UTF8Encoding]::new($false))"
    )

    echo ✅ 版本信息更新完成
)

echo.
echo 🚀 5. 构建Android发布版APK...
echo ⚠️  这可能需要几分钟时间...
cd android
call .\gradlew assembleRelease
if %errorlevel% neq 0 (
    echo ❌ 构建Android发布版APK失败！
    cd ..
    pause
    exit /b 1
)

echo.
echo ✅ 6. 构建完成！
cd ..

REM 检查发布版APK文件
if exist "android\app\build\outputs\apk\release\app-release.apk" (
    for %%F in ("android\app\build\outputs\apk\release\app-release.apk") do (
        echo 📦 发布版APK: %%~nxF
        echo 📏 文件大小: %%~zF 字节
        echo 📍 完整路径: %%~fF
    )
    
    echo.
    echo ✅ 发布版APK已签名，可以用于：
    echo   📱 直接安装到设备测试
    echo   🏪 发布到应用商店（Google Play, 华为应用市场等）
    echo   📤 分发给用户
    
    echo.
    echo 📱 安装命令：
    echo   adb install android\app\build\outputs\apk\release\app-release.apk

    echo.
    echo 📋 使用说明:
    echo   scripts\build-android-release.bat                    # 使用默认版本信息
    echo   scripts\build-android-release.bat prod 1.2.0 1002   # 指定版本名称和版本号
    echo   scripts\build-android-release.bat staging 1.1.1     # 只指定版本名称
) else (
    echo ❌ 未找到发布版APK文件！
)

echo.
pause 