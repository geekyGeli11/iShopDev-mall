package com.macro.mall.portal.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.domain.MemberInfoResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * 会员管理Service
 * Created by macro on 2018/8/3.
 */
public interface UmsMemberService {
    /**
     * 根据用户名获取会员
     */
    UmsMember getByUsername(String username);

    /**
     * 根据会员编号获取会员
     */
    UmsMember getById(Long id);

    /**
     * 用户注册
     */
    @Transactional
    void register(String username, String password, String telephone, String authCode);

    /**
     * 生成验证码
     */
    String generateAuthCode(String telephone);

    /**
     * 修改密码
     */
    @Transactional
    void updatePassword(String telephone, String password, String authCode);

    /**
     * 获取当前登录会员
     */
    UmsMember getCurrentMember();

    /**
     * 获取当前登录会员信息（包含扩展信息）
     */
    MemberInfoResult getCurrentMemberInfo();

    /**
     * 根据会员id修改会员积分
     */
    void updateIntegration(Long id,Integer integration);

    /**
     * 登录后获取token
     */
    SaTokenInfo login(String username, String password);

    /**
     * 登出功能
     */
    void logout();

    /**
     * 根据openid 获取用户
     */
    UmsMember getByOpenId(String openId);
    /**
     * 根据 openid 注册用户
     */
    UmsMember registerByOpenIdAndInfo(String openId, String nickname, String icon);

    /**
     * 使用 openid 登录
     */
    SaTokenInfo loginByOpenId(String openId);

    /**
     * 根据手机号获取用户
     */
    UmsMember getByPhone(String phone);

    /**
     * 根据openId和手机号更新或创建用户（简化版本）
     */
    @Transactional
    UmsMember registerByOpenIdAndPhone(String openId, String phone, String nickname, String icon);
    
    /**
     * 根据openId和手机号更新或创建用户（完整版本）
     */
    @Transactional
    UmsMember registerByOpenIdAndPhone(String openId, String phone, String nickname, String icon, Integer gender, String birthday);

    /**
     * 使用手机号登录
     */
    SaTokenInfo loginByPhone(String phone);
}
