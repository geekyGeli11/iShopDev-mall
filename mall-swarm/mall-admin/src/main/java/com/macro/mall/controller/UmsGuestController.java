package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.UmsGuestListVO;
import com.macro.mall.dto.UmsGuestQueryParam;
import com.macro.mall.service.UmsGuestAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 游客管理Controller
 */
@RestController
@Tag(name = "UmsGuestController", description = "游客管理")
@RequestMapping("/guest")
public class UmsGuestController {

    @Autowired
    private UmsGuestAdminService guestAdminService;

    @Operation(summary = "分页查询游客列表")
    @GetMapping("/list")
    public CommonResult<CommonPage<UmsGuestListVO>> list(
        UmsGuestQueryParam queryParam,
        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
        @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize
    ) {
        List<UmsGuestListVO> guestList = guestAdminService.list(queryParam, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(guestList));
    }

    @Operation(summary = "更新游客状态")
    @PostMapping("/updateStatus")
    public CommonResult<Integer> updateStatus(
        @RequestParam String guestId,
        @RequestParam Integer status
    ) {
        int count = guestAdminService.updateStatus(guestId, status);
        if (count > 0) {
            return CommonResult.success(count, "游客状态更新成功");
        }
        return CommonResult.failed("游客状态更新失败");
    }

    @Operation(summary = "删除游客")
    @PostMapping("/delete")
    public CommonResult<Integer> deleteGuest(@RequestParam String guestId) {
        int count = guestAdminService.deleteGuest(guestId);
        if (count > 0) {
            return CommonResult.success(count, "游客删除成功");
        }
        return CommonResult.failed("游客删除失败");
    }

    @Operation(summary = "清理过期游客")
    @PostMapping("/cleanExpired")
    public CommonResult<Integer> cleanExpiredGuests(
        @RequestParam(defaultValue = "7") Integer expiredDays
    ) {
        int count = guestAdminService.cleanExpiredGuests(expiredDays);
        return CommonResult.success(count, "清理完成，共清理" + count + "个过期游客");
    }
}
