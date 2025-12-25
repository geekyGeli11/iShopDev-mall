package com.macro.mall.selfcheck.service;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.selfcheck.dto.VersionCheckParam;
import com.macro.mall.selfcheck.vo.VersionCheckVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 应用更新服务测试类
 */
@SpringBootTest
@ActiveProfiles("test")
public class AppUpdateServiceTest {

    @Autowired
    private AppUpdateService appUpdateService;

    @Test
    public void testCheckVersion() {
        // 创建版本检查参数
        VersionCheckParam param = new VersionCheckParam();
        param.setDeviceId("test-device-001");
        param.setCurrentVersionCode(1000);
        param.setCurrentVersionName("1.0.0");
        param.setDeviceName("测试设备");
        param.setDeviceModel("Android Device");
        param.setAndroidVersion("11");
        param.setPlatform("android");

        // 执行版本检查
        CommonResult<VersionCheckVO> result = appUpdateService.checkVersion(param);
        
        // 验证结果
        System.out.println("检查结果: " + result);
        
        if (result.getCode() == 200) {
            VersionCheckVO data = result.getData();
            System.out.println("有更新: " + data.getHasUpdate());
            System.out.println("强制更新: " + data.getForceUpdate());
            
            if (data.getLatestVersion() != null) {
                System.out.println("最新版本: " + data.getLatestVersion().getVersionName());
            }
        }
    }

    @Test
    public void testGetVersionList() {
        CommonResult result = appUpdateService.getVersionList("android");
        System.out.println("版本列表: " + result);
    }

    @Test
    public void testGetDeviceList() {
        CommonResult result = appUpdateService.getDeviceList(null);
        System.out.println("设备列表: " + result);
    }
}
