package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.service.PmsInviteRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 邀请关系管理Controller
 * Created by guanghengzhou on 2024/01/20.
 */
@Controller
@Tag(name = "PmsInviteRelationController", description = "邀请关系管理")
@RequestMapping("/invite/relations")
public class PmsInviteRelationController {
    
    @Autowired
    private PmsInviteRelationService inviteRelationService;
    
    @Operation(summary = "获取邀请关系列表（分页查询）")
    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<Map<String, Object>>> getInviteRelations(
            @RequestParam(value = "inviterId", required = false) Long inviterId,
            @RequestParam(value = "inviteeId", required = false) Long inviteeId,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        
        List<Map<String, Object>> relationList = inviteRelationService.list(
                inviterId, inviteeId, status, startTime, endTime, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(relationList));
    }
    
    @Operation(summary = "获取邀请关系详情")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> getInviteRelationDetail(@PathVariable Long id) {
        Map<String, Object> detail = inviteRelationService.getDetail(id);
        if (detail != null) {
            return CommonResult.success(detail);
        }
        return CommonResult.failed("邀请关系不存在");
    }
    
    @Operation(summary = "更新邀请关系状态")
    @RequestMapping(value = "/{id}/status", method = RequestMethod.PUT)
    @ResponseBody
    public CommonResult updateInviteRelationStatus(@PathVariable Long id,
                                                   @RequestBody Map<String, Object> requestData) {
        Integer status = (Integer) requestData.get("status");
        String remark = (String) requestData.get("remark");
        
        if (status == null) {
            return CommonResult.failed("状态不能为空");
        }
        
        int count = inviteRelationService.updateStatus(id, status, remark);
        if (count > 0) {
            return CommonResult.success(count, "状态更新成功");
        }
        return CommonResult.failed("状态更新失败");
    }
    
    @Operation(summary = "导出邀请记录")
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<Map<String, Object>>> exportInviteRecords(
            @RequestParam(value = "inviterId", required = false) Long inviterId,
            @RequestParam(value = "inviteeId", required = false) Long inviteeId,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime) {
        
        List<Map<String, Object>> exportData = inviteRelationService.exportInviteRecords(
                inviterId, inviteeId, status, startTime, endTime);
        return CommonResult.success(exportData, "导出数据获取成功");
    }
    
    @Operation(summary = "批量更新邀请状态")
    @RequestMapping(value = "/batch-update", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult batchUpdateInviteStatus(@RequestBody Map<String, Object> requestData) {
        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) requestData.get("ids");
        Integer status = (Integer) requestData.get("status");
        String remark = (String) requestData.get("remark");
        
        if (ids == null || ids.isEmpty()) {
            return CommonResult.failed("请选择要更新的记录");
        }
        if (status == null) {
            return CommonResult.failed("状态不能为空");
        }
        
        int count = inviteRelationService.batchUpdateStatus(ids, status, remark);
        if (count > 0) {
            return CommonResult.success(count, "批量更新成功");
        }
        return CommonResult.failed("批量更新失败");
    }
    
    @Operation(summary = "获取邀请统计概览")
    @RequestMapping(value = "/overview", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> getInviteOverview() {
        Map<String, Object> overview = inviteRelationService.getInviteOverview();
        return CommonResult.success(overview);
    }
} 