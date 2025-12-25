package com.macro.mall.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.constant.AuthConstant;
import com.macro.mall.util.StpMemberUtil;
import com.macro.mall.util.StpSelfcheckUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;

import java.util.*;

/**
 * @auther macrozheng
 * @description Sa-Token相关配置
 * @date 2023/11/28
 */
@Configuration
public class SaTokenConfig {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 注册Sa-Token全局过滤器
     */
    @Bean
    public SaReactorFilter getSaReactorFilter(IgnoreUrlsConfig ignoreUrlsConfig) {
        return new SaReactorFilter()
                // 拦截地址
                .addInclude("/**")
                // 配置白名单路径
                .setExcludeList(ignoreUrlsConfig.getUrls())
                // 鉴权方法：每次访问进入
                .setAuth(obj -> {
                    // 对于OPTIONS预检请求直接放行
                    SaRouter.match(SaHttpMethod.OPTIONS).stop();
                    // 登录认证：商城前台会员认证
                    SaRouter.match("/mall-portal/**", r -> StpMemberUtil.checkLogin()).stop();
                    // 登录认证：自助结算系统会员认证
                    SaRouter.match("/mall-selfcheck/**", r -> StpSelfcheckUtil.checkLogin()).stop();
                    // 登录认证：管理后台用户认证
                    SaRouter.match("/mall-admin/**", r -> {
                        StpUtil.checkLogin();
                        // 权限认证：管理后台用户权限校验
                        // 获取Redis中缓存的各个接口路径所需权限规则
                        Map<Object, Object> pathResourceMap = redisTemplate.opsForHash().entries(AuthConstant.PATH_RESOURCE_MAP);
                        if (pathResourceMap.isEmpty()) {
                            // 如果Redis中没有权限规则，跳过权限校验（仅校验登录）
                            return;
                        }
                        // 获取到访问当前接口所需权限（一个路径对应多个资源时，拥有任意一个资源都可以访问该路径）
                        List<String> needPermissionList = new ArrayList<>();
                        // 获取当前请求路径（去掉/mall-admin前缀）
                        String requestPath = SaHolder.getRequest().getRequestPath();
                        String adminPath = requestPath.replaceFirst("/mall-admin", "");
                        // 创建路径匹配器
                        PathMatcher pathMatcher = new AntPathMatcher();
                        Set<Map.Entry<Object, Object>> entrySet = pathResourceMap.entrySet();
                        for (Map.Entry<Object, Object> entry : entrySet) {
                            String pattern = (String) entry.getKey();
                            if (pathMatcher.match(pattern, adminPath)) {
                                needPermissionList.add((String) entry.getValue());
                            }
                        }
                        // 接口需要权限时鉴权
                        if(CollUtil.isNotEmpty(needPermissionList)){
                            StpUtil.checkPermissionOr(Convert.toStrArray(needPermissionList));
                        }
                    }).stop();
                })
                // setAuth方法异常处理
                .setError(this::handleException);
    }

    /**
     * 自定义异常处理
     */
    private CommonResult handleException(Throwable e) {
        //设置错误返回格式为JSON
        ServerWebExchange exchange = SaReactorSyncHolder.getContext();
        HttpHeaders headers = exchange.getResponse().getHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");
        headers.set("Access-Control-Allow-Origin", "*");
        headers.set("Cache-Control","no-cache");
        CommonResult result = null;
        if(e instanceof NotLoginException){
            NotLoginException notLoginException = (NotLoginException) e;
            String message = "暂未登录或token已经过期";
            // 根据具体的登录异常类型提供更详细的错误信息
            if (notLoginException.getType().equals("memberLogin")) {
                message = "会员未登录或登录已过期，请先登录";
            } else if (notLoginException.getType().equals("selfcheckMemberLogin")) {
                message = "自助结算用户未登录或登录已过期，请先登录";
            }
            result = CommonResult.unauthorized(message);
        }else if(e instanceof NotPermissionException){
            result = CommonResult.forbidden("没有相关权限");
        }else{
            result = CommonResult.failed(e.getMessage());
        }
        return result;
    }
}

