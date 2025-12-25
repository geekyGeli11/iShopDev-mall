@echo off
echo ========================================
echo Mall自助收银系统 - 版本管理工具
echo ========================================

chcp 65001

REM 获取操作类型
set ACTION=%1
set VERSION_NAME=%2
set VERSION_CODE=%3

if "%ACTION%"=="" (
    echo 📋 使用说明:
    echo   scripts\version-manager.bat show                    # 显示当前版本信息
    echo   scripts\version-manager.bat set 1.2.0 1002         # 设置版本名称和版本号
    echo   scripts\version-manager.bat auto                    # 自动递增版本号
    echo   scripts\version-manager.bat sync                    # 同步package.json和build.gradle版本
    echo.
    pause
    exit /b 0
)

if "%ACTION%"=="show" (
    echo.
    echo 📱 当前Android版本信息:
    
    REM 读取build.gradle中的版本信息
    for /f "tokens=2 delims= " %%a in ('findstr "versionCode" android\app\build.gradle') do set CURRENT_VERSION_CODE=%%a
    for /f "tokens=2 delims=^"" %%a in ('findstr "versionName" android\app\build.gradle') do set CURRENT_VERSION_NAME=%%a
    
    echo   版本名称: %CURRENT_VERSION_NAME%
    echo   版本号: %CURRENT_VERSION_CODE%
    
    echo.
    echo 📦 package.json版本信息:
    for /f "tokens=2 delims=^"^, " %%a in ('findstr "version" package.json') do set PACKAGE_VERSION=%%a
    echo   版本: %PACKAGE_VERSION%
    
    echo.
    exit /b 0
)

if "%ACTION%"=="set" (
    if "%VERSION_NAME%"=="" (
        echo ❌ 请提供版本名称
        echo 用法: scripts\version-manager.bat set 1.2.0 1002
        pause
        exit /b 1
    )
    
    echo.
    echo 📝 设置版本信息...
    echo 版本名称: %VERSION_NAME%
    
    REM 更新build.gradle（指定UTF-8编码）
    powershell -Command "$content = Get-Content android\app\build.gradle -Encoding UTF8; $content -replace 'versionName \".*\"', 'versionName \"%VERSION_NAME%\"' | Set-Content android\app\build.gradle -Encoding UTF8"

    if not "%VERSION_CODE%"=="" (
        echo 版本号: %VERSION_CODE%
        powershell -Command "$content = Get-Content android\app\build.gradle -Encoding UTF8; $content -replace 'versionCode [0-9]+', 'versionCode %VERSION_CODE%' | Set-Content android\app\build.gradle -Encoding UTF8"
    )
    
    REM 更新package.json
    powershell -Command "(Get-Content package.json) -replace '\"version\": \".*\"', '\"version\": \"%VERSION_NAME%\"' | Set-Content package.json"
    
    echo ✅ 版本信息更新完成
    echo.
    
    REM 显示更新后的版本信息
    call scripts\version-manager.bat show
    exit /b 0
)

if "%ACTION%"=="auto" (
    echo.
    echo 🔄 自动递增版本号...
    
    REM 读取当前版本号
    for /f "tokens=2 delims= " %%a in ('findstr "versionCode" android\app\build.gradle') do set CURRENT_VERSION_CODE=%%a
    
    REM 计算新版本号
    set /a NEW_VERSION_CODE=%CURRENT_VERSION_CODE%+1
    
    echo 当前版本号: %CURRENT_VERSION_CODE%
    echo 新版本号: %NEW_VERSION_CODE%
    
    REM 更新版本号（指定UTF-8编码）
    powershell -Command "$content = Get-Content android\app\build.gradle -Encoding UTF8; $content -replace 'versionCode [0-9]+', 'versionCode %NEW_VERSION_CODE%' | Set-Content android\app\build.gradle -Encoding UTF8"
    
    echo ✅ 版本号自动递增完成
    echo.
    
    REM 显示更新后的版本信息
    call scripts\version-manager.bat show
    exit /b 0
)

if "%ACTION%"=="sync" (
    echo.
    echo 🔄 同步版本信息...
    
    REM 从package.json读取版本
    for /f "tokens=2 delims=^"^, " %%a in ('findstr "version" package.json') do set PACKAGE_VERSION=%%a
    
    echo 从package.json同步版本: %PACKAGE_VERSION%
    
    REM 更新build.gradle的versionName（指定UTF-8编码）
    powershell -Command "$content = Get-Content android\app\build.gradle -Encoding UTF8; $content -replace 'versionName \".*\"', 'versionName \"%PACKAGE_VERSION%\"' | Set-Content android\app\build.gradle -Encoding UTF8"
    
    echo ✅ 版本信息同步完成
    echo.
    
    REM 显示更新后的版本信息
    call scripts\version-manager.bat show
    exit /b 0
)

echo ❌ 不支持的操作: %ACTION%
echo 支持的操作: show, set, auto, sync
pause
exit /b 1
