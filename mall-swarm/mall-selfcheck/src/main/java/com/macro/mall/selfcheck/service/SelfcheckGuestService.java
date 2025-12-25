package com.macro.mall.selfcheck.service;

import com.macro.mall.model.UmsGuest;

/**
 * 自助结算游客服务接口
 * 
 * @author macro
 * @since 1.0.0
 */
public interface SelfcheckGuestService {

    /**
     * 创建游客会话
     * @param deviceId 设备ID
     * @param deviceType 设备类型
     * @param loginIp 登录IP
     * @return 游客信息
     */
    UmsGuest createGuestSession(String deviceId, String deviceType, String loginIp);

    /**
     * 获取游客信息
     * @param guestId 游客ID
     * @return 游客信息
     */
    UmsGuest getGuest(String guestId);

    /**
     * 更新游客活跃时间
     * @param guestId 游客ID
     */
    void updateGuestActivity(String guestId);

    /**
     * 清除游客会话
     * @param guestId 游客ID
     */
    void clearGuestSession(String guestId);

    /**
     * 检查游客会话是否有效
     * @param guestId 游客ID
     * @return 是否有效
     */
    boolean isGuestSessionValid(String guestId);

    /**
     * 生成游客ID
     * @param deviceId 设备ID
     * @return 游客ID
     */
    String generateGuestId(String deviceId);

    /**
     * 绑定游客学校
     * @param guestId 游客ID
     * @param schoolId 学校ID
     * @return 是否成功
     */
    boolean bindGuestSchool(String guestId, Long schoolId);

    /**
     * 获取游客绑定的学校ID
     * @param guestId 游客ID
     * @return 学校ID
     */
    Long getGuestSchoolId(String guestId);

    /**
     * 创建游客会话（带学校绑定）
     * @param deviceId 设备ID
     * @param deviceType 设备类型
     * @param loginIp 登录IP
     * @param schoolId 学校ID
     * @return 游客信息
     */
    UmsGuest createGuestSessionWithSchoolBinding(String deviceId, String deviceType, String loginIp, Long schoolId);

    /**
     * 设置游客订单状态
     * @param guestId 游客ID
     * @param hasActiveOrder 是否有活跃订单
     */
    void setGuestOrderStatus(String guestId, boolean hasActiveOrder);

    /**
     * 关联游客信息和SA-Token的loginId
     * @param guestId 游客ID
     * @param guestLoginId SA-Token的loginId
     */
    void linkGuestWithToken(String guestId, long guestLoginId);

    /**
     * 根据SA-Token的loginId获取游客ID
     * @param guestLoginId SA-Token的loginId
     * @return 游客ID
     */
    String getGuestIdByLoginId(long guestLoginId);

    /**
     * 根据游客ID获取SA-Token的loginId
     * @param guestId 游客ID
     * @return SA-Token的loginId，如果不存在则返回null
     */
    Long getLoginIdByGuestId(String guestId);
} 