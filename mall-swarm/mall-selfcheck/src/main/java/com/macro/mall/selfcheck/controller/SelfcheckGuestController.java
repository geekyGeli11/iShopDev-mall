package com.macro.mall.selfcheck.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.util.RequestUtil;
import com.macro.mall.model.UmsGuest;
import com.macro.mall.selfcheck.dto.GuestLoginParam;
import com.macro.mall.selfcheck.service.SelfcheckGuestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 自助结算游客管理Controller
 */
@Slf4j
@RestController
@RequestMapping("/guest")
@Tag(name = "SelfcheckGuestController", description = "自助结算游客管理")
public class SelfcheckGuestController {

    @Autowired
    private SelfcheckGuestService guestService;

    @Operation(summary = "游客登录")
    @PostMapping("/login")
    public CommonResult<Map<String, Object>> guestLogin(@Valid @RequestBody GuestLoginParam loginParam,
                                                        HttpServletRequest request) {
        try {
            String loginIp = RequestUtil.getRequestIp(request);
            String userAgent = request.getHeader("User-Agent");

            // 使用带学校绑定的游客会话创建
            UmsGuest guest;
            if (loginParam.getSchoolId() != null) {
                guest = guestService.createGuestSessionWithSchoolBinding(
                    loginParam.getDeviceId(),
                    loginParam.getDeviceType(),
                    loginIp,
                    loginParam.getSchoolId()
                );
            } else {
                guest = guestService.createGuestSession(
                    loginParam.getDeviceId(),
                    loginParam.getDeviceType(),
                    loginIp
                );
            }
            
            if (guest != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("guestId", guest.getGuestId());
                result.put("deviceId", guest.getDeviceId());
                result.put("createTime", guest.getCreateTime());
                result.put("schoolId", guest.getSchoolId());
                
                return CommonResult.success(result);
            } else {
                return CommonResult.failed("游客登录失败");
            }
        } catch (Exception e) {
            log.error("游客登录失败：{}", e.getMessage());
            return CommonResult.failed("游客登录失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取游客信息")
    @GetMapping("/info")
    public CommonResult<UmsGuest> getGuestInfo(@RequestParam String guestId) {
        try {
            UmsGuest guest = guestService.getGuest(guestId);
            if (guest != null) {
                return CommonResult.success(guest);
            } else {
                return CommonResult.failed("游客不存在或已过期");
            }
        } catch (Exception e) {
            log.error("获取游客信息失败：{}", e.getMessage());
            return CommonResult.failed("获取游客信息失败：" + e.getMessage());
        }
    }

    @Operation(summary = "绑定游客学校")
    @PostMapping("/bindSchool")
    public CommonResult<String> bindGuestSchool(@RequestParam String guestId, 
                                               @RequestParam Long schoolId,
                                               HttpServletRequest request) {
        try {
            boolean success = guestService.bindGuestSchool(guestId, schoolId);
            if (success) {
                return CommonResult.success("学校绑定成功");
            } else {
                return CommonResult.failed("学校绑定失败");
            }
        } catch (Exception e) {
            log.error("绑定游客学校失败：{}", e.getMessage());
            return CommonResult.failed("绑定学校失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取游客绑定的学校ID")
    @GetMapping("/getSchoolId")
    public CommonResult<Long> getGuestSchoolId(@RequestParam String guestId) {
        try {
            Long schoolId = guestService.getGuestSchoolId(guestId);
            return CommonResult.success(schoolId);
        } catch (Exception e) {
            log.error("获取游客学校ID失败：{}", e.getMessage());
            return CommonResult.failed("获取学校信息失败：" + e.getMessage());
        }
    }

    @Operation(summary = "更新游客活跃时间")
    @PostMapping("/updateActivity")
    public CommonResult<String> updateGuestActivity(@RequestParam String guestId) {
        try {
            guestService.updateGuestActivity(guestId);
            return CommonResult.success("活跃时间更新成功");
        } catch (Exception e) {
            log.error("更新游客活跃时间失败：{}", e.getMessage());
            return CommonResult.failed("更新活跃时间失败：" + e.getMessage());
        }
    }

    @Operation(summary = "清除游客会话")
    @PostMapping("/logout")
    public CommonResult<String> guestLogout(@RequestParam String guestId) {
        try {
            guestService.clearGuestSession(guestId);
            return CommonResult.success("游客登出成功");
        } catch (Exception e) {
            log.error("游客登出失败：{}", e.getMessage());
            return CommonResult.failed("游客登出失败：" + e.getMessage());
        }
    }
}
