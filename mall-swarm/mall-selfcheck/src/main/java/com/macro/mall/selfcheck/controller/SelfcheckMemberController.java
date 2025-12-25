package com.macro.mall.selfcheck.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.util.RequestUtil;
import com.macro.mall.model.UmsGuest;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberSession;
import com.macro.mall.selfcheck.dto.MemberCodeLoginParam;
import com.macro.mall.selfcheck.dto.MemberLoginParam;
import com.macro.mall.selfcheck.exception.SmsException;
import com.macro.mall.selfcheck.service.SelfcheckGuestService;
import com.macro.mall.selfcheck.service.SelfcheckMemberService;
import com.macro.mall.selfcheck.service.SelfcheckSecurityService;
import com.macro.mall.selfcheck.service.SelfcheckSmsService;
import com.macro.mall.selfcheck.util.StpMemberUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.stp.StpLogic;

/**
 * 自助结算会员管理Controller
 * 
 * @author macro
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/member")
@Tag(name = "SelfcheckMemberController", description = "自助结算会员管理")
public class SelfcheckMemberController {

    @Value("${sa-token.token-prefix}")
    private String tokenHead;

    @Autowired
    private SelfcheckMemberService memberService;

    @Autowired
    private SelfcheckSmsService smsService;

    @Autowired
    private SelfcheckSecurityService securityService;

    @Autowired
    private SelfcheckGuestService guestService;

    @Operation(summary = "会员登录")
    @PostMapping("/login")
    public CommonResult<Map<String, Object>> login(@Valid @RequestBody MemberLoginParam loginParam,
                                                   HttpServletRequest request) {
        try {
            String phone = loginParam.getTelephone();
            String loginIp = RequestUtil.getRequestIp(request);
            String deviceInfo = request.getHeader("User-Agent");

            // 安全检查
            if (securityService.detectAbnormalLogin(phone, loginIp, deviceInfo)) {
                long lockTtl = securityService.getLoginLockTtl(phone);
                if (lockTtl > 0) {
                    return CommonResult.failed("账号已被锁定，请等待 " + lockTtl + " 秒后重试");
                }
                return CommonResult.failed("检测到异常登录行为，请稍后重试");
            }

            // 验证会员身份（带学校绑定）
            UmsMember member = memberService.authenticateMemberWithStoreBinding(loginParam);

            // 执行SA-Token登录
            StpMemberUtil.login(member.getId());

            // 创建会话
            UmsMemberSession session = memberService.createMemberSession(member, deviceInfo, loginIp);

            // 记录登录日志
            memberService.recordLoginLog(member, loginIp, deviceInfo);

            // 记录安全日志
            securityService.recordLoginAttempt(phone, true, loginIp);

            // 获取Token信息
            SaTokenInfo tokenInfo = StpMemberUtil.getTokenInfo();

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("token", tokenInfo.getTokenValue());
            result.put("tokenHead", tokenHead + " ");
            result.put("memberId", member.getId());
            result.put("memberInfo", member);
            result.put("sessionId", session.getSessionId());
            result.put("expiresIn", tokenInfo.getTokenTimeout());

            log.info("会员登录成功，手机号：{}，会员ID：{}，IP：{}", phone, member.getId(), loginIp);

            return CommonResult.success(result);

        } catch (Exception e) {
            String phone = loginParam.getTelephone();
            String loginIp = RequestUtil.getRequestIp(request);

            // 记录登录失败
            securityService.recordLoginAttempt(phone, false, loginIp);

            log.error("会员登录失败，手机号：{}，IP：{}，错误：{}", phone, loginIp, e.getMessage());

            return CommonResult.failed(e.getMessage());
        }
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public CommonResult<String> logout(@RequestParam(required = false) String guestId) {
        try {
            // 处理会员退出登录
            if (StpMemberUtil.isLogin()) {
                Long memberId = StpMemberUtil.getLoginIdAsLong();
                
                // 清除会员会话
                memberService.clearMemberSession(memberId);
                
                // SA-Token退出登录
                StpMemberUtil.logout();
                
                log.info("会员退出登录成功，会员ID：{}", memberId);
                
                return CommonResult.success("会员退出登录成功");
            }
            
            // 处理游客退出
            if (guestId != null && !guestId.trim().isEmpty()) {
                guestService.clearGuestSession(guestId);
                
                log.info("游客退出成功，游客ID：{}", guestId);
                
                return CommonResult.success("游客退出成功");
            }
            
            return CommonResult.success("退出成功");
            
        } catch (Exception e) {
            log.error("退出登录失败：{}", e.getMessage());
            return CommonResult.failed("退出登录失败");
        }
    }

    @Operation(summary = "发送验证码")
    @PostMapping("/sendVerifyCode")
    public CommonResult<Map<String, Object>> sendVerifyCode(@RequestParam String telephone,
                                                           HttpServletRequest request) {
        try {
            String loginIp = RequestUtil.getRequestIp(request);

            // 验证手机号格式
            if (telephone == null || !telephone.matches("^1[3-9]\\d{9}$")) {
                return CommonResult.validateFailed("手机号格式不正确");
            }

            // 检查IP是否被锁定
            if (securityService.isIpBlocked(loginIp)) {
                return CommonResult.failed("操作过于频繁，请稍后重试");
            }

            // 检查发送频率限制
            if (!smsService.canSendCode(telephone)) {
                long remainingTime = smsService.getRateLimitTtl(telephone);
                return CommonResult.failed("发送过于频繁，请等待 " + remainingTime + " 秒后重试");
            }

            // 发送验证码
            String code = smsService.sendVerifyCode(telephone);

            Map<String, Object> result = new HashMap<>();
            result.put("message", "验证码发送成功");
            result.put("codeLength", 6);
            result.put("expireTime", 300); // 5分钟有效期
            result.put("canResendAfter", smsService.getRateLimitTtl(telephone));

            log.info("验证码发送成功，手机号：{}，IP：{}", telephone, loginIp);

            return CommonResult.success(result);

        } catch (SmsException e) {
            log.error("短信发送失败，手机号：{}，错误类型：{}，错误信息：{}", 
                     telephone, e.getErrorType(), e.getMessage());
            
            // 根据错误类型返回不同的消息
            switch (e.getErrorType()) {
                case RATE_LIMIT:
                    return CommonResult.failed(e.getMessage()); // 流控限制
                case ACCOUNT_ISSUE:
                    return CommonResult.failed("服务暂时不可用，请稍后重试"); // 账户问题
                case PHONE_ISSUE:
                    return CommonResult.validateFailed(e.getMessage()); // 手机号问题
                default:
                    return CommonResult.failed(e.getMessage());
            }
        } catch (Exception e) {
            log.error("验证码发送失败，手机号：{}，错误：{}", telephone, e.getMessage(), e);
            return CommonResult.failed("验证码发送失败，请稍后重试");
        }
    }

    @Operation(summary = "刷新活跃状态")
    @PostMapping("/refreshActivity")
    public CommonResult<String> refreshActivity(@RequestParam(required = false) String guestId) {
        try {
            // 检查会员登录状态
            if (StpMemberUtil.isLogin()) {
                Long memberId = StpMemberUtil.getLoginIdAsLong();
                
                // 更新会员活跃时间
                memberService.updateMemberActivity(memberId);
                
                // 更新SA-Token最后活跃时间
                StpMemberUtil.updateLastActiveToNow();
                
                return CommonResult.success("会员活跃状态已刷新");
            }
            
            // 检查游客会话
            if (guestId != null && !guestId.trim().isEmpty()) {
                if (guestService.isGuestSessionValid(guestId)) {
                    guestService.updateGuestActivity(guestId);
                    return CommonResult.success("游客活跃状态已刷新");
                } else {
                    return CommonResult.failed("游客会话已过期");
                }
            }
            
            return CommonResult.unauthorized("未登录或会话无效");
            
        } catch (Exception e) {
            log.error("刷新活跃状态失败：{}", e.getMessage());
            return CommonResult.failed("刷新活跃状态失败");
        }
    }

    @Operation(summary = "获取当前会员信息")
    @GetMapping("/info")
    public CommonResult<Map<String, Object>> getMemberInfo() {
        try {
            // 检查登录状态
            if (!StpMemberUtil.isLogin()) {
                return CommonResult.unauthorized("用户未登录");
            }

            // 获取当前登录的会员ID
            Long memberId = StpMemberUtil.getLoginIdAsLong();

            // 更新会员活跃时间
            memberService.updateMemberActivity(memberId);

            // 获取会员详细信息
            UmsMember member = memberService.getMemberById(memberId);
            if (member == null) {
                return CommonResult.failed("会员信息不存在");
            }

            // 获取会话信息
            UmsMemberSession session = memberService.getMemberSession(memberId);
            
            // 构建返回结果，包含会员信息和会话信息
            Map<String, Object> result = new HashMap<>();
            result.put("member", member);
            if (session != null) {
                result.put("sessionInfo", session);
                result.put("lastActiveTime", session.getLastActiveTime());
                result.put("loginIp", session.getLoginIp());
            }

            return CommonResult.success(result);

        } catch (Exception e) {
            log.error("获取会员信息失败：{}", e.getMessage());
            return CommonResult.failed("获取会员信息失败");
        }
    }

    @Operation(summary = "检查登录状态")
    @GetMapping("/checkLogin")
    public CommonResult<Map<String, Object>> checkLogin() {
        boolean isLogin = StpMemberUtil.isLogin();
        
        Map<String, Object> result = new HashMap<>();
        result.put("isLogin", isLogin);
        
        if (isLogin) {
            result.put("memberId", StpMemberUtil.getLoginIdAsLong());
            result.put("tokenTimeout", StpMemberUtil.getTokenTimeout());
        }
        
        return CommonResult.success(result);
    }

    @Operation(summary = "游客登录")
    @PostMapping("/guestLogin")
    public CommonResult<Map<String, Object>> guestLogin(@RequestParam(required = false) String deviceId,
                                                        @RequestParam(required = false) String deviceType,
                                                        @RequestParam(required = false) Long storeId,
                                                        @RequestParam(required = false) Long schoolId,
                                                        HttpServletRequest request) {
        try {
            String loginIp = RequestUtil.getRequestIp(request);

            // 生成设备ID（如果未提供）
            if (deviceId == null || deviceId.trim().isEmpty()) {
                deviceId = "device_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
            }

            // 设置默认设备类型
            if (deviceType == null || deviceType.trim().isEmpty()) {
                deviceType = "unknown";
            }

            // 创建游客会话（带学校绑定）
            UmsGuest guest;
            if (schoolId != null) {
                guest = guestService.createGuestSessionWithSchoolBinding(deviceId, deviceType, loginIp, schoolId);
            } else {
                guest = guestService.createGuestSession(deviceId, deviceType, loginIp);
            }

            // 直接使用网关注册的StpLogic
            StpLogic stpLogic = SaManager.getStpLogic("selfcheckMemberLogin");
            if (stpLogic == null) {
                return CommonResult.failed("认证系统未初始化");
            }
            
            // 游客ID使用负数以区分会员ID
            long guestLoginId = -Math.abs(guest.getGuestId().hashCode());
            stpLogic.login(guestLoginId);

            // 获取Token信息
            SaTokenInfo tokenInfo = stpLogic.getTokenInfo();

            // 在Redis中关联游客信息和SA-Token的loginId
            guestService.linkGuestWithToken(guest.getGuestId(), guestLoginId);

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("guestId", guest.getGuestId());
            result.put("deviceId", guest.getDeviceId());
            result.put("deviceType", guest.getDeviceType());
            result.put("isGuest", true);
            result.put("createTime", guest.getCreateTime());
            result.put("token", tokenInfo.getTokenValue());
            result.put("tokenHead", tokenHead + " ");
            result.put("expiresIn", tokenInfo.getTokenTimeout());

            log.info("游客登录成功，游客ID：{}，设备ID：{}，IP：{}", guest.getGuestId(), deviceId, loginIp);

            return CommonResult.success(result);

        } catch (Exception e) {
            log.error("游客登录失败，错误：{}", e.getMessage());
            return CommonResult.failed("游客登录失败：" + e.getMessage());
        }
    }

    @Operation(summary = "游客信息查询")
    @GetMapping("/guestInfo")
    public CommonResult<UmsGuest> getGuestInfo(@RequestParam String guestId) {
        try {
            if (guestId == null || guestId.trim().isEmpty()) {
                return CommonResult.validateFailed("游客ID不能为空");
            }

            UmsGuest guest = guestService.getGuest(guestId);
            if (guest == null) {
                return CommonResult.failed("游客信息不存在或已过期");
            }

            // 更新游客活跃时间
            guestService.updateGuestActivity(guestId);

            return CommonResult.success(guest);

        } catch (Exception e) {
            log.error("获取游客信息失败，游客ID：{}，错误：{}", guestId, e.getMessage());
            return CommonResult.failed("获取游客信息失败");
        }
    }

    @Operation(summary = "获取当前登录信息")
    @GetMapping("/getCurrentUser")
    public CommonResult<Map<String, Object>> getCurrentUser() {
        try {
            Map<String, Object> result = new HashMap<>();
            
            // 检查是否是SA-Token登录状态
            if (StpMemberUtil.isLogin()) {
                Long loginId = StpMemberUtil.getLoginIdAsLong();
                
                if (loginId > 0) {
                    // 正数loginId表示会员
                    result.put("userType", "member");
                    result.put("loginId", loginId);
                    result.put("isLogin", true);
                    
                    // 获取会员信息
                    UmsMember member = memberService.getMemberById(loginId);
                    if (member != null) {
                        result.put("userInfo", member);
                        memberService.updateMemberActivity(loginId);
                    }
                } else {
                    // 负数loginId表示游客
                    result.put("userType", "guest");
                    result.put("loginId", loginId);
                    result.put("isLogin", true);
                    
                    // 获取游客信息
                    String guestId = guestService.getGuestIdByLoginId(loginId);
                    if (guestId != null) {
                        UmsGuest guest = guestService.getGuest(guestId);
                        if (guest != null) {
                            result.put("guestId", guestId);
                            result.put("userInfo", guest);
                            guestService.updateGuestActivity(guestId);
                        }
                    }
                }
                
                result.put("tokenTimeout", StpMemberUtil.getTokenTimeout());
                return CommonResult.success(result);
            } else {
                result.put("userType", "anonymous");
                result.put("isLogin", false);
                return CommonResult.success(result);
            }
            
        } catch (Exception e) {
            log.error("获取当前登录信息失败：{}", e.getMessage());
            return CommonResult.failed("获取当前登录信息失败");
        }
    }

    @Operation(summary = "检查会话状态")
    @GetMapping("/checkSession")
    public CommonResult<Map<String, Object>> checkSession(@RequestParam(required = false) String guestId) {
        try {
            Map<String, Object> result = new HashMap<>();

            // 检查会员登录状态
            if (StpMemberUtil.isLogin()) {
                Long memberId = StpMemberUtil.getLoginIdAsLong();
                boolean isValid = memberService.isSessionValid(memberId);
                
                result.put("isLogin", true);
                result.put("isMember", true);
                result.put("isGuest", false);
                result.put("memberId", memberId);
                result.put("isValid", isValid);
                
                if (isValid) {
                    memberService.updateMemberActivity(memberId);
                }
                
                return CommonResult.success(result);
            }

            // 检查游客会话状态
            if (guestId != null && !guestId.trim().isEmpty()) {
                boolean isValid = guestService.isGuestSessionValid(guestId);
                
                result.put("isLogin", false);
                result.put("isMember", false);
                result.put("isGuest", true);
                result.put("guestId", guestId);
                result.put("isValid", isValid);
                
                if (isValid) {
                    guestService.updateGuestActivity(guestId);
                }
                
                return CommonResult.success(result);
            }

            // 未登录状态
            result.put("isLogin", false);
            result.put("isMember", false);
            result.put("isGuest", false);
            result.put("isValid", false);

            return CommonResult.success(result);

        } catch (Exception e) {
            log.error("检查会话状态失败：{}", e.getMessage());
            return CommonResult.failed("检查会话状态失败");
        }
    }

    @Operation(summary = "会员号码登录")
    @PostMapping("/loginByCode")
    public CommonResult<Map<String, Object>> loginByCode(@Valid @RequestBody MemberCodeLoginParam loginParam,
                                                        HttpServletRequest request) {
        try {
            String memberCode = loginParam.getMemberCode();
            String loginIp = RequestUtil.getRequestIp(request);
            String deviceInfo = request.getHeader("User-Agent");

            // 安全检查
            if (securityService.detectAbnormalLogin(memberCode, loginIp, deviceInfo)) {
                return CommonResult.failed("检测到异常登录行为，请稍后重试");
            }

            // 验证会员号码（带学校绑定）
            UmsMember member = memberService.authenticateMemberByCodeWithStoreBinding(loginParam);

            // 执行SA-Token登录
            StpMemberUtil.login(member.getId());

            // 创建会话
            UmsMemberSession session = memberService.createMemberSession(member, deviceInfo, loginIp);

            // 记录登录日志
            memberService.recordLoginLog(member, loginIp, deviceInfo);

            // 记录安全日志
            securityService.recordLoginAttempt(memberCode, true, loginIp);

            // 获取Token信息
            SaTokenInfo tokenInfo = StpMemberUtil.getTokenInfo();

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("token", tokenInfo.getTokenValue());
            result.put("tokenHead", tokenHead + " ");
            result.put("memberId", member.getId());
            result.put("memberInfo", member);
            result.put("memberCode", memberCode);
            result.put("sessionId", session.getSessionId());
            result.put("expiresIn", tokenInfo.getTokenTimeout());

            log.info("会员号码登录成功，会员号码：{}，会员ID：{}，IP：{}", memberCode, member.getId(), loginIp);

            return CommonResult.success(result);

        } catch (Exception e) {
            String memberCode = loginParam.getMemberCode();
            String loginIp = RequestUtil.getRequestIp(request);

            // 记录登录失败
            securityService.recordLoginAttempt(memberCode, false, loginIp);

            log.error("会员号码登录失败，会员号码：{}，IP：{}，错误：{}", memberCode, loginIp, e.getMessage());

            return CommonResult.failed(e.getMessage());
        }
    }

    @Operation(summary = "获取会员号码")
    @GetMapping("/getMemberCode")
    public CommonResult<Map<String, Object>> getMemberCode() {
        try {
            // 检查登录状态
            if (!StpMemberUtil.isLogin()) {
                return CommonResult.unauthorized("用户未登录");
            }

            // 获取当前登录的会员ID
            Long memberId = StpMemberUtil.getLoginIdAsLong();

            // 获取或生成会员号码
            String memberCode = memberService.getMemberCode(memberId);
            if (memberCode == null) {
                memberCode = memberService.generateMemberCode(memberId);
            }

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("memberCode", memberCode);
            result.put("codeFormat", "M + 日期(yyMMdd) + 5位随机数");
            result.put("example", "M24012012345");

            return CommonResult.success(result);

        } catch (Exception e) {
            log.error("获取会员号码失败：{}", e.getMessage());
            return CommonResult.failed("获取会员号码失败");
        }
    }

    @Operation(summary = "生成会员二维码")
    @GetMapping("/generateQRCode")
    public CommonResult<Map<String, Object>> generateQRCode() {
        try {
            // 检查登录状态
            if (!StpMemberUtil.isLogin()) {
                return CommonResult.unauthorized("用户未登录");
            }

            // 获取当前登录的会员ID
            Long memberId = StpMemberUtil.getLoginIdAsLong();

            // 获取或生成会员号码
            String memberCode = memberService.getMemberCode(memberId);
            if (memberCode == null) {
                memberCode = memberService.generateMemberCode(memberId);
            }

            // 构建二维码数据
            Map<String, Object> qrData = new HashMap<>();
            qrData.put("type", "member_code");
            qrData.put("memberCode", memberCode);
            qrData.put("memberId", memberId);
            qrData.put("timestamp", System.currentTimeMillis());

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("qrData", qrData);
            result.put("qrText", memberCode); // 简单模式，直接使用会员号码
            result.put("memberCode", memberCode);

            return CommonResult.success(result);

        } catch (Exception e) {
            log.error("生成会员二维码失败：{}", e.getMessage());
            return CommonResult.failed("生成会员二维码失败");
        }
    }

    @Operation(summary = "绑定会员学校")
    @PostMapping("/bindSchool")
    public CommonResult<String> bindMemberSchool(@RequestParam Long schoolId, HttpServletRequest request) {
        try {
            // 获取当前登录的会员ID
            Long memberId = StpMemberUtil.getLoginIdAsLong();
            if (memberId == null) {
                return CommonResult.failed("请先登录");
            }

            boolean success = memberService.bindMemberSchool(memberId, schoolId);
            if (success) {
                return CommonResult.success("学校绑定成功");
            } else {
                return CommonResult.failed("学校绑定失败");
            }
        } catch (Exception e) {
            log.error("绑定会员学校失败：{}", e.getMessage());
            return CommonResult.failed("绑定学校失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取会员绑定的学校ID")
    @GetMapping("/getSchoolId")
    public CommonResult<Long> getMemberSchoolId(HttpServletRequest request) {
        try {
            // 获取当前登录的会员ID
            Long memberId = StpMemberUtil.getLoginIdAsLong();
            if (memberId == null) {
                return CommonResult.failed("请先登录");
            }

            Long schoolId = memberService.getMemberSchoolId(memberId);
            return CommonResult.success(schoolId);
        } catch (Exception e) {
            log.error("获取会员学校ID失败：{}", e.getMessage());
            return CommonResult.failed("获取学校信息失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取会员余额信息")
    @GetMapping("/balance/info")
    public CommonResult<Map<String, Object>> getBalanceInfo() {
        try {
            // 检查登录状态
            if (!StpMemberUtil.isLogin()) {
                return CommonResult.unauthorized("用户未登录");
            }

            // 获取当前登录的会员ID
            Long memberId = StpMemberUtil.getLoginIdAsLong();

            // 获取会员信息
            UmsMember member = memberService.getMemberById(memberId);
            if (member == null) {
                return CommonResult.failed("会员信息不存在");
            }

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("balance", member.getBalance() != null ? member.getBalance() : 0);
            result.put("frozenBalance", 0); // 暂时没有冻结余额功能
            result.put("totalRecharge", 0); // 可以后续从历史记录中统计
            result.put("totalConsume", 0); // 可以后续从历史记录中统计

            return CommonResult.success(result);

        } catch (Exception e) {
            log.error("获取会员余额信息失败：{}", e.getMessage());
            return CommonResult.failed("获取余额信息失败");
        }
    }
}