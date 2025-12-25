package com.mall.selfcheckout.plugins;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.core.content.FileProvider;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

/**
 * APK安装器插件
 * 提供Android APK安装、权限管理等功能
 */
@CapacitorPlugin(
    name = "AppInstaller",
    permissions = {
        @Permission(strings = { Manifest.permission.REQUEST_INSTALL_PACKAGES }),
        @Permission(strings = { Manifest.permission.READ_EXTERNAL_STORAGE }),
        @Permission(strings = { Manifest.permission.WRITE_EXTERNAL_STORAGE })
    }
)
public class AppInstallerPlugin extends Plugin {

    private static final String TAG = "AppInstallerPlugin";
    private static final int REQUEST_INSTALL_PERMISSION = 1001;

    /**
     * 安装APK文件
     */
    @PluginMethod
    public void installApk(PluginCall call) {
        String filePath = call.getString("filePath");
        String packageName = call.getString("packageName");
        boolean silent = call.getBoolean("silent", false);

        if (filePath == null || filePath.isEmpty()) {
            call.reject("文件路径不能为空");
            return;
        }

        try {
            File apkFile = new File(filePath);
            if (!apkFile.exists()) {
                call.reject("APK文件不存在: " + filePath);
                return;
            }

            // 检查安装权限
            if (!hasInstallPermission()) {
                call.reject("没有安装权限，请先申请权限");
                return;
            }

            // 执行安装
            boolean success = performInstall(apkFile, silent);
            
            JSObject result = new JSObject();
            result.put("success", success);
            
            if (success) {
                call.resolve(result);
            } else {
                result.put("error", "安装失败");
                call.resolve(result);
            }

        } catch (Exception e) {
            JSObject result = new JSObject();
            result.put("success", false);
            result.put("error", e.getMessage());
            call.resolve(result);
        }
    }

    /**
     * 检查安装权限
     */
    @PluginMethod
    public void checkInstallPermission(PluginCall call) {
        JSObject result = new JSObject();
        result.put("hasPermission", hasInstallPermission());
        call.resolve(result);
    }

    /**
     * 请求安装权限
     */
    @PluginMethod
    public void requestInstallPermission(PluginCall call) {
        if (hasInstallPermission()) {
            JSObject result = new JSObject();
            result.put("granted", true);
            call.resolve(result);
            return;
        }

        // Android 8.0+ 需要特殊权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
            intent.setData(Uri.parse("package:" + getContext().getPackageName()));

            // 保存call以便在权限回调中使用
            bridge.saveCall(call);
            startActivityForResult(call, intent, REQUEST_INSTALL_PERMISSION);
        } else {
            JSObject result = new JSObject();
            result.put("granted", true);
            call.resolve(result);
        }
    }

    /**
     * 获取APK信息
     */
    @PluginMethod
    public void getApkInfo(PluginCall call) {
        String filePath = call.getString("filePath");

        if (filePath == null || filePath.isEmpty()) {
            call.reject("文件路径不能为空");
            return;
        }

        try {
            File apkFile = new File(filePath);
            if (!apkFile.exists()) {
                call.reject("APK文件不存在: " + filePath);
                return;
            }

            PackageManager pm = getContext().getPackageManager();
            PackageInfo packageInfo = pm.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
            
            if (packageInfo == null) {
                call.reject("无法解析APK文件");
                return;
            }

            ApplicationInfo appInfo = packageInfo.applicationInfo;
            appInfo.sourceDir = filePath;
            appInfo.publicSourceDir = filePath;

            JSObject result = new JSObject();
            result.put("packageName", packageInfo.packageName);
            result.put("versionName", packageInfo.versionName);
            result.put("versionCode", packageInfo.versionCode);
            result.put("appName", pm.getApplicationLabel(appInfo).toString());

            call.resolve(result);

        } catch (Exception e) {
            call.reject("获取APK信息失败: " + e.getMessage());
        }
    }

    /**
     * 计算文件MD5
     */
    @PluginMethod
    public void calculateMd5(PluginCall call) {
        String filePath = call.getString("filePath");

        if (filePath == null || filePath.isEmpty()) {
            call.reject("文件路径不能为空");
            return;
        }

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                call.reject("文件不存在: " + filePath);
                return;
            }

            String md5 = calculateFileMd5(file);
            
            JSObject result = new JSObject();
            result.put("md5", md5);
            call.resolve(result);

        } catch (Exception e) {
            call.reject("计算MD5失败: " + e.getMessage());
        }
    }

    /**
     * 检查应用是否已安装
     */
    @PluginMethod
    public void isAppInstalled(PluginCall call) {
        String packageName = call.getString("packageName");

        if (packageName == null || packageName.isEmpty()) {
            call.reject("包名不能为空");
            return;
        }

        try {
            PackageManager pm = getContext().getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
            
            JSObject result = new JSObject();
            result.put("installed", true);
            result.put("versionCode", packageInfo.versionCode);
            call.resolve(result);

        } catch (PackageManager.NameNotFoundException e) {
            JSObject result = new JSObject();
            result.put("installed", false);
            call.resolve(result);
        } catch (Exception e) {
            call.reject("检查安装状态失败: " + e.getMessage());
        }
    }

    /**
     * 卸载应用
     */
    @PluginMethod
    public void uninstallApp(PluginCall call) {
        String packageName = call.getString("packageName");

        if (packageName == null || packageName.isEmpty()) {
            call.reject("包名不能为空");
            return;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:" + packageName));
            getActivity().startActivity(intent);
            
            JSObject result = new JSObject();
            result.put("success", true);
            call.resolve(result);

        } catch (Exception e) {
            JSObject result = new JSObject();
            result.put("success", false);
            call.resolve(result);
        }
    }

    /**
     * 检查是否有安装权限
     */
    private boolean hasInstallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return getContext().getPackageManager().canRequestPackageInstalls();
        }
        return true;
    }

    /**
     * 执行APK安装
     */
    private boolean performInstall(File apkFile, boolean silent) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // Android 7.0+ 使用FileProvider
                Uri apkUri = FileProvider.getUriForFile(
                    getContext(),
                    getContext().getPackageName() + ".fileprovider",
                    apkFile
                );
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            }
            
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            
            if (silent) {
                // 静默安装需要系统权限或root权限
                // 这里只是启动安装界面
            }
            
            getContext().startActivity(intent);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 计算文件MD5值
     */
    private String calculateFileMd5(File file) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        FileInputStream fis = new FileInputStream(file);
        
        byte[] buffer = new byte[8192];
        int length;
        
        while ((length = fis.read(buffer)) != -1) {
            md.update(buffer, 0, length);
        }
        
        fis.close();
        
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder();
        
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        
        return sb.toString();
    }

    /**
     * 处理权限请求结果
     */
    @Override
    protected void handleOnActivityResult(int requestCode, int resultCode, Intent data) {
        super.handleOnActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_INSTALL_PERMISSION) {
            PluginCall savedCall = bridge.getSavedCall("requestInstallPermission");
            if (savedCall != null) {
                JSObject result = new JSObject();
                result.put("granted", hasInstallPermission());
                savedCall.resolve(result);
            }
        }
    }
}
