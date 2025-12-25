package com.macro.mall.controller;

import cn.hutool.core.util.StrUtil;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.AdminWechatBindingStatus;
import com.macro.mall.mapper.UmsAdminMapper;
import com.macro.mall.model.UmsAdmin;
import com.macro.mall.service.WechatServiceAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员微信绑定管理接口
 */
@RestController
@RequestMapping("/admin/wechat")
@Tag(name = "AdminWechatController", description = "管理员微信绑定管理")
public class AdminWechatController {
    
    @Autowired
    private WechatServiceAccountService wechatServiceAccountService;
    
    @Autowired
    private UmsAdminMapper adminMapper;
    
    @Operation(summary = "获取管理员微信绑定状态")
    @GetMapping("/bindingStatus/{adminId}")
    public CommonResult<AdminWechatBindingStatus> getBindingStatus(@PathVariable Long adminId) {
        UmsAdmin admin = adminMapper.selectByPrimaryKey(adminId);
        if (admin == null) {
            return CommonResult.failed("管理员不存在");
        }
        
        AdminWechatBindingStatus status = new AdminWechatBindingStatus();
        status.setBound(StrUtil.isNotEmpty(admin.getWxServiceOpenid()));
        status.setNickname(admin.getWxServiceNickname());
        status.setHeadImgUrl(admin.getWxServiceHeadimg());
        status.setBindTime(admin.getWxServiceBindtime());
        
        return CommonResult.success(status);
    }
    
    @Operation(summary = "生成微信绑定二维码")
    @PostMapping("/generateQRCode/{adminId}")
    public CommonResult<String> generateQRCode(@PathVariable Long adminId) {
        UmsAdmin admin = adminMapper.selectByPrimaryKey(adminId);
        if (admin == null) {
            return CommonResult.failed("管理员不存在");
        }
        
        // 生成临时二维码，有效期5分钟
        String qrCodeUrl = wechatServiceAccountService.generateQRCode(adminId, 300);
        return CommonResult.success(qrCodeUrl);
    }
    
    @Operation(summary = "解除微信绑定")
    @PostMapping("/unbind/{adminId}")
    public CommonResult<Void> unbind(@PathVariable Long adminId) {
        wechatServiceAccountService.unbindWechat(adminId);
        return CommonResult.success(null);
    }
}
