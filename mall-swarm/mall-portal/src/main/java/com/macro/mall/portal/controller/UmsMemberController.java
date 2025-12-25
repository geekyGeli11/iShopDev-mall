package com.macro.mall.portal.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.portal.domain.MemberInfoResult;
import com.macro.mall.portal.service.QRCodeService;
import com.macro.mall.portal.service.WechatLoginService;
import com.macro.mall.portal.util.StpMemberUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 会员登录注册管理Controller
 * Created by macro on 2018/8/3.
 */
@Controller
@Tag(name = "UmsMemberController", description = "会员登录注册管理")
@RequestMapping("/sso")
public class UmsMemberController {
    @Autowired
    private UmsMemberService memberService;

    @Autowired
    private WechatLoginService wechatLoginService;

    @Autowired
    private QRCodeService qrCodeService;

    @Value("${sa-token.token-prefix}")
    private String tokenHead;

    @Operation(summary = "会员注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult register(@RequestParam String username,
                                 @RequestParam String password,
                                 @RequestParam String telephone,
                                 @RequestParam String authCode) {
        memberService.register(username, password, telephone, authCode);
        return CommonResult.success(null,"注册成功");
    }

//    @Operation(summary = "会员登录")
//    @RequestMapping(value = "/login", method = RequestMethod.POST)
//    @ResponseBody
//    public CommonResult login(@RequestParam String username,
//                              @RequestParam String password) {
//        SaTokenInfo saTokenInfo  = memberService.login(username, password);
//        if (saTokenInfo  == null) {
//            return CommonResult.validateFailed("用户名或密码错误");
//        }
//        Map<String, String> tokenMap = new HashMap<>();
//        tokenMap.put("token", saTokenInfo.getTokenValue() );
//        tokenMap.put("tokenHead", tokenHead+" ");
//        return CommonResult.success(tokenMap);
//    }

    @Operation(summary = "获取会员信息")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<UmsMember> info() {
        UmsMember memberInfo = memberService.getCurrentMember();
        return CommonResult.success(memberInfo);
    }

    @Operation(summary = "获取会员详细信息（包含等级信息）")
    @RequestMapping(value = "/memberDetail", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<MemberInfoResult> memberDetail() {
        MemberInfoResult memberInfo = memberService.getCurrentMemberInfo();
        return CommonResult.success(memberInfo);
    }

    @Operation(summary = "登出功能")
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult logout() {
        memberService.logout();
        return CommonResult.success(null);
    }

    @Operation(summary = "获取会员二维码")
    @RequestMapping(value = "/memberQRCode", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> getMemberQRCode() {
        try {
            // 获取当前登录用户
            UmsMember member = memberService.getCurrentMember();
            if (member == null) {
                return CommonResult.unauthorized("请先登录");
            }

            // 检查用户是否有会员码
            String memberCode = member.getMemberCode();
            if (memberCode == null || memberCode.trim().isEmpty()) {
                return CommonResult.failed("会员码不存在，请联系客服");
            }

            // 直接使用会员码作为二维码内容（不再使用JSON格式）
            String qrContent = memberCode;

            // 生成二维码
            String qrCodeBase64 = qrCodeService.generateQRCodeBase64(qrContent);

            if (qrCodeBase64 == null) {
                return CommonResult.failed("二维码生成失败");
            }

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("memberCode", memberCode);
            result.put("qrCodeBase64", qrCodeBase64);
            result.put("memberName", member.getNickname() != null ? member.getNickname() : member.getUsername());
            result.put("memberAvatar", member.getIcon());
            result.put("memberLevel", member.getMemberLevelId());
            result.put("memberPhone", member.getPhone());

            return CommonResult.success(result, "获取会员二维码成功");

        } catch (Exception e) {
            return CommonResult.failed("获取会员二维码失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取验证码")
    @RequestMapping(value = "/getAuthCode", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getAuthCode(@RequestParam String telephone) {
        String authCode = memberService.generateAuthCode(telephone);
        return CommonResult.success(authCode,"获取验证码成功");
    }

    @Operation(summary = "修改密码")
    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updatePassword(@RequestParam String telephone,
                                 @RequestParam String password,
                                 @RequestParam String authCode) {
        memberService.updatePassword(telephone,password,authCode);
        return CommonResult.success(null,"密码修改成功");
    }

    @Operation(summary = "微信小程序注册和资料完善")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult wechatLogin(@RequestParam String code,
                                    @RequestParam String nickname,
                                    @RequestParam(required = false) String phone,
                                    @RequestParam(required = false) String icon,
                                    @RequestParam(required = false) Integer gender,
                                    @RequestParam(required = false) String birthday,
                                    @RequestParam(required = false) String inviteParam) {
        try {
            // 获取 openid
            String openId = wechatLoginService.getOpenId(code);
            
            // 保存邀请参数到Session（如果存在）
            if (inviteParam != null && !inviteParam.trim().isEmpty()) {
                // 这里先设置一个临时Session，等登录后再正式设置
                // 由于此时还未登录，暂时存储到线程本地变量或其他方式
                saveInviteParamForLogin(inviteParam);
            }
            
            // 根据 openid 注册或更新用户信息
            UmsMember member = memberService.registerByOpenIdAndPhone(openId, phone, nickname, icon, gender, birthday);
            
            // 使用 openid 登录并获取 Token
            SaTokenInfo tokenInfo = memberService.loginByOpenId(openId);
            
            // 登录成功后，如果有邀请参数，保存到Session
            if (inviteParam != null && !inviteParam.trim().isEmpty()) {
                StpMemberUtil.getSession().set("inviteParam", inviteParam);
            }
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("token", tokenInfo.getTokenValue());
            result.put("tokenHead", tokenHead + " ");
            result.put("member", member);
            result.put("openId", openId); // 添加openId用于静默登录
            result.put("expiresIn", tokenInfo.getTokenTimeout());

            return CommonResult.success(result);
        } catch (Exception e) {
            return CommonResult.failed("登录失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "微信小程序手机号授权登录")
    @RequestMapping(value = "/loginByPhone", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult wechatPhoneLogin(@RequestParam String code, 
                                         @RequestParam(required = false) String phoneCode,
                                         @RequestParam(required = false) String phone,
                                         @RequestParam(required = false) String nickname, 
                                         @RequestParam(required = false) String icon,
                                         @RequestParam(required = false) Integer gender,
                                         @RequestParam(required = false) String birthday,
                                         @RequestParam(required = false) String inviteParam) {
        try {
            // 获取 openid
            String openId = wechatLoginService.getOpenId(code);
            
            // 保存邀请参数到临时存储（如果存在）
            if (inviteParam != null && !inviteParam.trim().isEmpty()) {
                saveInviteParamForLogin(inviteParam);
            }
            
            // 获取手机号
            if (phone == null || phone.isEmpty()) {
                if (phoneCode == null || phoneCode.isEmpty()) {
                    return CommonResult.failed("手机号或手机号授权码不能为空");
                }
                phone = wechatLoginService.getPhoneNumber(phoneCode);
            }
            
            // 根据 openid 和手机号登录或注册用户，更新或创建用户信息
            UmsMember member = memberService.registerByOpenIdAndPhone(openId, phone, nickname, icon, gender, birthday);
            
            // 登录并获取 Token
            SaTokenInfo tokenInfo = memberService.loginByPhone(phone);
            
            // 登录成功后，如果有邀请参数，保存到Session
            if (inviteParam != null && !inviteParam.trim().isEmpty()) {
                StpMemberUtil.getSession().set("inviteParam", inviteParam);
            }
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("token", tokenInfo.getTokenValue());
            result.put("tokenHead", tokenHead + " ");
            result.put("member", member);
            result.put("openId", openId); // 添加openId用于静默登录
            result.put("expiresIn", tokenInfo.getTokenTimeout());

            return CommonResult.success(result);
        } catch (Exception e) {
            return CommonResult.failed("登录失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取微信手机号")
    @RequestMapping(value = "/getPhoneNumber", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult getPhoneNumber(@RequestParam String phoneCode) {
        try {
            // 获取手机号
            String phone = wechatLoginService.getPhoneNumber(phoneCode);
            return CommonResult.success(phone);
        } catch (Exception e) {
            return CommonResult.failed("获取手机号失败: " + e.getMessage());
        }
    }

    @Operation(summary = "静默登录刷新token")
    @RequestMapping(value = "/silentLogin", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult silentLogin(@RequestParam String openId) {
        try {
            // 根据openId查找用户
            UmsMember member = memberService.getByOpenId(openId);
            if (member == null) {
                return CommonResult.failed("用户不存在");
            }
            if (member.getStatus() != 1) {
                return CommonResult.failed("用户已被禁用");
            }

            // 重新登录获取新token
            SaTokenInfo tokenInfo = memberService.loginByOpenId(openId);

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("token", tokenInfo.getTokenValue());
            result.put("tokenHead", tokenHead + " ");
            result.put("expiresIn", tokenInfo.getTokenTimeout());
            result.put("openId", openId);

            return CommonResult.success(result);
        } catch (Exception e) {
            return CommonResult.failed("静默登录失败: " + e.getMessage());
        }
    }

    /**
     * 临时存储邀请参数的线程本地变量
     */
    private static final ThreadLocal<String> inviteParamThreadLocal = new ThreadLocal<>();
    
    /**
     * 在登录前保存邀请参数
     */
    private void saveInviteParamForLogin(String inviteParam) {
        inviteParamThreadLocal.set(inviteParam);
    }
    
    /**
     * 获取临时存储的邀请参数
     */
    public static String getInviteParamFromThreadLocal() {
        return inviteParamThreadLocal.get();
    }
    
    /**
     * 清除临时存储的邀请参数
     */
    public static void clearInviteParamFromThreadLocal() {
        inviteParamThreadLocal.remove();
    }
}
