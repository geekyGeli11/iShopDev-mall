package com.macro.mall.selfcheck.config;

import cn.dev33.satoken.SaManager;
import com.macro.mall.selfcheck.util.StpMemberUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * SA-Token初始化配置
 * 确保自助结算系统的StpLogic对象被正确注册到全局管理器
 * 
 * @author macro
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class SaTokenInitConfig {

    /**
     * 初始化SA-Token自助结算账号体系
     */
    @PostConstruct
    public void initSaToken() {
        // 注册自助结算StpLogic
        SaManager.putStpLogic(StpMemberUtil.getStpLogic());
        log.info("已注册自助结算StpLogic，TYPE: {}", StpMemberUtil.TYPE);
        
        // 验证注册结果
        if (SaManager.getStpLogic(StpMemberUtil.TYPE) != null) {
            log.info("自助结算StpLogic注册成功");
        } else {
            log.error("自助结算StpLogic注册失败");
        }
    }
} 