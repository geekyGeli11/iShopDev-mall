package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.SmsNotification;
import com.macro.mall.portal.service.SmsPortalNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 会员通知管理Controller
 */
@Controller
@Tag(name = "SmsPortalNotificationController", description = "会员通知管理")
@RequestMapping("/notification")
public class SmsPortalNotificationController {
    
    @Autowired
    private SmsPortalNotificationService notificationService;
    
    @Operation(summary = "分页获取通知列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<SmsNotification>> list(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                          @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize) {
        Page<SmsNotification> page = notificationService.list(pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(page));
    }
    
    @Operation(summary = "获取最新一条未读通知")
    @RequestMapping(value = "/latestUnread", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<SmsNotification> getLatestUnread() {
        SmsNotification notification = notificationService.getLatestUnread();
        if (notification != null) {
            return CommonResult.success(notification);
        }
        return CommonResult.success(null);
    }
    
    @Operation(summary = "创建通知阅读记录")
    @RequestMapping(value = "/read/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult createReadRecord(@PathVariable Long id) {
        int count = notificationService.createReadRecord(id);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "获取通知详情")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<SmsNotification> getNotification(@PathVariable Long id) {
        SmsNotification notification = notificationService.getNotification(id);
        if (notification != null) {
            return CommonResult.success(notification);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "检查通知是否已读")
    @RequestMapping(value = "/isRead/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Boolean> isRead(@PathVariable Long id) {
        boolean isRead = notificationService.isRead(id);
        return CommonResult.success(isRead);
    }
} 