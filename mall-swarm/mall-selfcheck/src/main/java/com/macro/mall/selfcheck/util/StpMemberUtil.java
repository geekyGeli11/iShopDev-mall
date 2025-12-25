package com.macro.mall.selfcheck.util;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.listener.SaTokenEventCenter;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpLogic;

/**
 * 自助结算系统会员登录认证工具类
 * 注意：TYPE必须与网关中的StpSelfcheckUtil保持一致
 * 
 * @author macro
 * @since 1.0.0
 */
public class StpMemberUtil {

    private StpMemberUtil() {}
    
    /**
     * 多账号体系下的类型标识 - 必须与网关一致
     */
    public static final String TYPE = "selfcheckMemberLogin";
    
    /**
     * 底层使用的 StpLogic 对象
     */
    public static StpLogic stpLogic = new StpLogicJwtForSimple(TYPE);

    /**
     * 获取当前 StpLogic 的账号类型
     */
    public static String getLoginType(){
        return stpLogic.getLoginType();
    }

    /**
     * 安全的重置 StpLogic 对象
     */
    public static void setStpLogic(StpLogic newStpLogic) {
        // 1、重置此账户的 StpLogic 对象
        stpLogic = newStpLogic;
        
        // 2、添加到全局 StpLogic 集合中
        SaManager.putStpLogic(newStpLogic);
        
        // 3、发布事件：更新了 stpLogic 对象
        SaTokenEventCenter.doSetStpLogic(stpLogic);
    }

    /**
     * 获取 StpLogic 对象
     */
    public static StpLogic getStpLogic() {
        return stpLogic;
    }
    
    // ------------------- 获取 token 相关 -------------------

    /**
     * 返回 token 名称
     */
    public static String getTokenName() {
        return stpLogic.getTokenName();
    }

    /**
     * 在当前会话写入指定 token 值
     */
    public static void setTokenValue(String tokenValue){
        stpLogic.setTokenValue(tokenValue);
    }

    /**
     * 获取当前请求的 token 值
     */
    public static String getTokenValue() {
        return stpLogic.getTokenValue();
    }

    /**
     * 获取当前会话的 token 参数信息
     */
    public static SaTokenInfo getTokenInfo() {
        return stpLogic.getTokenInfo();
    }

    // ------------------- 登录相关操作 -------------------

    /**
     * 会员登录
     */
    public static void login(Object id) {
        stpLogic.login(id);
    }

    /**
     * 会员登录，并指定此次登录 token 的有效期
     */
    public static void login(Object id, long timeout) {
        stpLogic.login(id, timeout);
    }

    /**
     * 会员登录，并指定登录设备类型
     */
    public static void login(Object id, String device) {
        stpLogic.login(id, device);
    }

    /**
     * 会话登录，并指定登录参数Model
     */
    public static void login(Object id, SaLoginModel loginModel) {
        stpLogic.login(id, loginModel);
    }

    // ------------------- 注销相关 -------------------

    /**
     * 当前会话注销登录
     */
    public static void logout() {
        stpLogic.logout();
    }

    /**
     * 会话注销登录，根据账号id
     */
    public static void logout(Object loginId) {
        stpLogic.logout(loginId);
    }

    /**
     * 会话注销登录，根据指定 Token
     */
    public static void logoutByTokenValue(String tokenValue) {
        stpLogic.logoutByTokenValue(tokenValue);
    }

    // ------------------- 会话查询相关 -------------------

    /**
     * 获取当前会话是否已经登录，返回true=已登录，false=未登录
     */
    public static boolean isLogin() {
        return stpLogic.isLogin();
    }

    /**
     * 检验当前会话是否已经登录, 如果未登录，则抛出异常
     */
    public static void checkLogin() {
        stpLogic.checkLogin();
    }

    /**
     * 获取当前会话登录id, 如果未登录，则抛出异常
     */
    public static Object getLoginId() {
        return stpLogic.getLoginId();
    }

    /**
     * 获取当前会话登录id, 如果未登录，则返回默认值
     */
    @SuppressWarnings("unchecked")
    public static <T> T getLoginId(T defaultValue) {
        return stpLogic.getLoginId(defaultValue);
    }

    /**
     * 获取当前会话登录id, 如果未登录，则返回null
     */
    public static Object getLoginIdDefaultNull() {
        return stpLogic.getLoginIdDefaultNull();
    }

    /**
     * 获取当前会话登录id, 并转换为String类型
     */
    public static String getLoginIdAsString() {
        return stpLogic.getLoginIdAsString();
    }

    /**
     * 获取当前会话登录id, 并转换为int类型
     */
    public static int getLoginIdAsInt() {
        return stpLogic.getLoginIdAsInt();
    }

    /**
     * 获取当前会话登录id, 并转换为long类型
     */
    public static long getLoginIdAsLong() {
        return stpLogic.getLoginIdAsLong();
    }

    /**
     * 获取指定 token 对应的账号id，如果未登录，则返回 null
     */
    public static Object getLoginIdByToken(String tokenValue) {
        return stpLogic.getLoginIdByToken(tokenValue);
    }

    /**
     * 获取当前 Token 的扩展信息（此函数只在jwt模式下生效）
     */
    public static Object getExtra(String key) {
        return stpLogic.getExtra(key);
    }

    /**
     * 获取指定 Token 的扩展信息（此函数只在jwt模式下生效）
     */
    public static Object getExtra(String tokenValue, String key) {
        return stpLogic.getExtra(tokenValue, key);
    }

    // ------------------- Account-Session 相关 -------------------

    /**
     * 获取指定账号 id 的 Account-Session
     */
    public static SaSession getSessionByLoginId(Object loginId, boolean isCreate) {
        return stpLogic.getSessionByLoginId(loginId, isCreate);
    }

    /**
     * 获取指定账号 id 的 Account-Session，如果该 SaSession 尚未创建，则新建并返回
     */
    public static SaSession getSessionByLoginId(Object loginId) {
        return stpLogic.getSessionByLoginId(loginId);
    }

    /**
     * 获取当前已登录账号的 Account-Session，如果该 SaSession 尚未创建，则新建并返回
     */
    public static SaSession getSession() {
        return stpLogic.getSession();
    }

    /**
     * 获取当前已登录账号的 Account-Session，如果该 SaSession 尚未创建，isCreate=是否新建并返回
     */
    public static SaSession getSession(boolean isCreate) {
        return stpLogic.getSession(isCreate);
    }

    // ------------------- Token-Session 相关 -------------------

    /**
     * 获取指定 token 的 Token-Session
     */
    public static SaSession getTokenSessionByToken(String tokenValue) {
        return stpLogic.getTokenSessionByToken(tokenValue);
    }

    /**
     * 获取当前 token 的 Token-Session
     */
    public static SaSession getTokenSession() {
        return stpLogic.getTokenSession();
    }

    /**
     * 获取当前匿名 Token-Session
     */
    public static SaSession getAnonTokenSession() {
        return stpLogic.getAnonTokenSession();
    }

    // ------------------- 过期时间相关 -------------------

    /**
     * 获取当前会话 token 剩余有效时间（单位: 秒）
     */
    public static long getTokenTimeout() {
        return stpLogic.getTokenTimeout();
    }

    /**
     * 获取指定 token 剩余有效时间（单位: 秒）
     */
    public static long getTokenTimeout(String token) {
        return stpLogic.getTokenTimeout(token);
    }

    /**
     * 获取当前登录账号的 Account-Session 剩余有效时间（单位: 秒）
     */
    public static long getSessionTimeout() {
        return stpLogic.getSessionTimeout();
    }

    /**
     * 获取当前 token 的 Token-Session 剩余有效时间（单位: 秒）
     */
    public static long getTokenSessionTimeout() {
        return stpLogic.getTokenSessionTimeout();
    }

    /**
     * 续签当前 token：(将 [最后操作时间] 更新为当前时间戳)
     */
    public static void updateLastActiveToNow() {
        stpLogic.updateLastActiveToNow();
    }

    /**
     * 检查当前 token 是否已被冻结，如果是则抛出异常
     */
    public static void checkActiveTimeout() {
        stpLogic.checkActiveTimeout();
    }
} 