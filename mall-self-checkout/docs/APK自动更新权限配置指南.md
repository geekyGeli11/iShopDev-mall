# APK自动更新权限配置指南

## 概述

本文档详细说明了mall-self-checkout应用实现APK自动下载和安装所需的权限配置和注意事项。

## 权限配置清单

### 1. AndroidManifest.xml权限

```xml
<!-- 网络相关权限 -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />

<!-- APK安装相关权限 -->
<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

<!-- 文件下载权限 -->
<uses-permission android:name="android.permission.DOWNLOAD_WITHOUT_NOTIFICATION" />

<!-- 设备信息权限（用于设备标识） -->
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
```

### 2. Application配置

```xml
<application
    android:requestLegacyExternalStorage="true"
    android:preserveLegacyExternalStorage="true"
    android:usesCleartextTraffic="true"
    android:networkSecurityConfig="@xml/network_security_config">
```

### 3. FileProvider配置

```xml
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>
```

## 权限说明

### 关键权限详解

1. **REQUEST_INSTALL_PACKAGES**
   - **用途**: Android 8.0+安装APK必需
   - **级别**: 特殊权限，需要用户手动授权
   - **获取方式**: 跳转到系统设置页面

2. **READ/WRITE_EXTERNAL_STORAGE**
   - **用途**: 读写外部存储，下载APK文件
   - **级别**: 危险权限，需要运行时请求
   - **Android 11+**: 需要特殊处理

3. **DOWNLOAD_WITHOUT_NOTIFICATION**
   - **用途**: 后台下载文件不显示通知
   - **级别**: 普通权限
   - **注意**: 某些设备可能限制

## Android版本兼容性

### Android 7.0+ (API 24+)
- **FileProvider**: 必须使用FileProvider共享文件
- **文件URI**: 不能直接使用file://协议

### Android 8.0+ (API 26+)
- **安装权限**: 需要REQUEST_INSTALL_PACKAGES权限
- **未知来源**: 需要用户在设置中允许安装未知来源应用

### Android 10+ (API 29+)
- **分区存储**: 默认启用分区存储
- **外部存储**: 需要requestLegacyExternalStorage="true"

### Android 11+ (API 30+)
- **存储权限**: 存储权限模型变更
- **文件访问**: 更严格的文件访问限制

### Android 14+ (API 34+)
- **部分照片访问**: 新的存储权限模型
- **运行时权限**: 更细粒度的权限控制

## 权限请求流程

### 1. 应用启动时检查
```typescript
// 检查所有必需权限
const permissionStatus = await PermissionManager.checkAllPermissions()
if (!permissionStatus.granted) {
  // 显示权限说明并请求权限
  await PermissionManager.requestAllPermissions()
}
```

### 2. 下载前检查
```typescript
// 检查存储权限
const storagePermission = await PermissionManager.checkStoragePermission()
if (!storagePermission.granted) {
  await PermissionManager.requestStoragePermission()
}
```

### 3. 安装前检查
```typescript
// 检查安装权限
const installPermission = await PermissionManager.checkInstallPermission()
if (!installPermission.granted) {
  await PermissionManager.requestInstallPermission()
}
```

## 常见问题和解决方案

### 1. 安装权限被拒绝
**问题**: 用户拒绝了安装权限
**解决**: 
- 显示权限说明对话框
- 引导用户到设置页面手动开启
- 提供跳转到应用设置的功能

### 2. 文件下载失败
**问题**: 存储权限不足或路径不正确
**解决**:
- 检查存储权限状态
- 使用应用私有目录存储
- 确保FileProvider路径配置正确

### 3. APK安装失败
**问题**: FileProvider URI无效或权限不足
**解决**:
- 检查file_paths.xml配置
- 确保FileProvider authorities正确
- 验证APK文件完整性

### 4. Android 11+存储访问
**问题**: 分区存储限制文件访问
**解决**:
- 使用应用专用目录
- 申请MANAGE_EXTERNAL_STORAGE权限（谨慎使用）
- 使用MediaStore API

## 测试建议

### 1. 权限测试
- 测试首次安装时的权限请求流程
- 测试权限被拒绝后的处理逻辑
- 测试权限撤销后的恢复机制

### 2. 兼容性测试
- 在不同Android版本上测试
- 测试不同厂商的设备（小米、华为、OPPO等）
- 测试不同的系统安全设置

### 3. 边界情况测试
- 存储空间不足
- 网络中断时的下载恢复
- APK文件损坏的处理

## 安全考虑

### 1. 文件完整性
- 使用MD5校验确保APK文件完整性
- 验证APK签名（如果需要）
- 检查APK包名和版本信息

### 2. 权限最小化
- 只请求必需的权限
- 在需要时才请求权限
- 向用户解释权限用途

### 3. 安全下载
- 使用HTTPS下载APK文件
- 验证下载来源的可信性
- 防止中间人攻击

## 部署注意事项

### 1. 生产环境
- 关闭调试模式
- 使用正式签名证书
- 配置正确的网络安全策略

### 2. 企业部署
- 考虑使用MDM（移动设备管理）
- 配置企业证书信任
- 设置设备策略和限制

### 3. 用户体验
- 提供清晰的权限说明
- 优化下载和安装流程
- 处理各种异常情况

## 总结

APK自动更新功能需要仔细处理各种权限和兼容性问题。通过正确配置权限、处理不同Android版本的差异、提供良好的用户体验，可以实现稳定可靠的自动更新功能。

建议在实际部署前进行充分的测试，特别是在目标设备和Android版本上的兼容性测试。
