package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.SmsNotification;
import com.macro.mall.model.SmsNotificationRead;
import com.macro.mall.service.SmsNotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "SmsNotificationController", description = "通知管理")
@RequestMapping("/sms/notification")
public class SmsNotificationController {

    @Autowired
    private SmsNotificationService notificationService;

    @Operation(summary = "添加通知")
    @PostMapping("/create")
    @ResponseBody
    public CommonResult create(@RequestBody SmsNotification notification) {
        SmsNotification result = notificationService.create(notification);
        if (result != null) {
            return CommonResult.success(result);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "更新通知")
    @PostMapping("/update/{id}")
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @RequestBody SmsNotification notification) {
        SmsNotification result = notificationService.update(id, notification);
        if (result != null) {
            return CommonResult.success(result);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "删除通知")
    @PostMapping("/delete/{id}")
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        boolean success = notificationService.delete(id);
        if (success) {
            return CommonResult.success(success);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "根据ID获取通知详情")
    @GetMapping("/{id}")
    @ResponseBody
    public CommonResult<SmsNotification> getById(@PathVariable Long id) {
        SmsNotification result = notificationService.getById(id);
        if (result != null) {
            return CommonResult.success(result);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "根据条件分页获取通知列表")
    @GetMapping("/list")
    @ResponseBody
    public CommonResult<CommonPage<SmsNotification>> listByFilters(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
            @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        List<SmsNotification> list = notificationService.listByFilters(title, status, startTime, endTime, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(list));
    }
    
    @Operation(summary = "获取通知阅读统计")
    @GetMapping("/readStats/{id}")
    @ResponseBody
    public CommonResult<Map<String, Object>> getReadStats(@PathVariable Long id) {
        Map<String, Object> stats = notificationService.getReadStats(id);
        return CommonResult.success(stats);
    }
    
    @Operation(summary = "分页获取通知阅读记录")
    @GetMapping("/readRecords/{id}")
    @ResponseBody
    public CommonResult<CommonPage<SmsNotificationRead>> listReadRecords(
            @PathVariable Long id,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        
        List<SmsNotificationRead> list = notificationService.listReadRecords(id, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(list));
    }
} 