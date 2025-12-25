package com.macro.mall.selfcheck.service;

import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberSession;
import com.macro.mall.selfcheck.dto.MemberCodeLoginParam;
import com.macro.mall.selfcheck.dto.MemberLoginParam;

/**
 * 自助结算会员服务接口
 * 
 * @author macro
 * @since 1.0.0
 */
public interface SelfcheckMemberService {

    /**
     * 会员登录验证
     * @param loginParam 登录参数
     * @return 会员信息
     */
    UmsMember authenticateMember(MemberLoginParam loginParam);

    /**
     * 会员号码登录验证
     * @param loginParam 会员号码登录参数
     * @return 会员信息
     */
    UmsMember authenticateMemberByCode(MemberCodeLoginParam loginParam);

    /**
     * 根据手机号获取会员信息
     * @param phone 手机号
     * @return 会员信息
     */
    UmsMember getMemberByPhone(String phone);

    /**
     * 根据会员ID获取会员信息
     * @param memberId 会员ID
     * @return 会员信息
     */
    UmsMember getMemberById(Long memberId);

    /**
     * 根据会员号码获取会员信息
     * @param memberCode 会员号码
     * @return 会员信息
     */
    UmsMember getMemberByCode(String memberCode);

    /**
     * 创建会员会话
     * @param member 会员信息
     * @param deviceInfo 设备信息
     * @param loginIp 登录IP
     * @return 会话信息
     */
    UmsMemberSession createMemberSession(UmsMember member, String deviceInfo, String loginIp);

    /**
     * 更新会员活跃时间
     * @param memberId 会员ID
     */
    void updateMemberActivity(Long memberId);

    /**
     * 获取会员会话信息
     * @param memberId 会员ID
     * @return 会话信息
     */
    UmsMemberSession getMemberSession(Long memberId);

    /**
     * 清除会员会话
     * @param memberId 会员ID
     */
    void clearMemberSession(Long memberId);

    /**
     * 检查会员会话是否有效
     * @param memberId 会员ID
     * @return 是否有效
     */
    boolean isSessionValid(Long memberId);

    /**
     * 记录会员登录日志
     * @param member 会员信息
     * @param loginIp 登录IP
     * @param deviceInfo 设备信息
     */
    void recordLoginLog(UmsMember member, String loginIp, String deviceInfo);

    /**
     * 生成会员号码
     * @param memberId 会员ID
     * @return 会员号码
     */
    String generateMemberCode(Long memberId);

    /**
     * 获取会员号码
     * @param memberId 会员ID
     * @return 会员号码
     */
    String getMemberCode(Long memberId);

    /**
     * 绑定会员学校
     * @param memberId 会员ID
     * @param schoolId 学校ID
     * @return 是否成功
     */
    boolean bindMemberSchool(Long memberId, Long schoolId);

    /**
     * 获取会员绑定的学校ID
     * @param memberId 会员ID
     * @return 学校ID
     */
    Long getMemberSchoolId(Long memberId);

    /**
     * 会员登录验证（带学校绑定）
     * @param loginParam 登录参数（包含门店ID和学校ID）
     * @return 会员信息
     */
    UmsMember authenticateMemberWithStoreBinding(MemberLoginParam loginParam);

    /**
     * 会员号码登录验证（带学校绑定）
     * @param loginParam 会员号码登录参数（包含门店ID和学校ID）
     * @return 会员信息
     */
    UmsMember authenticateMemberByCodeWithStoreBinding(MemberCodeLoginParam loginParam);
}