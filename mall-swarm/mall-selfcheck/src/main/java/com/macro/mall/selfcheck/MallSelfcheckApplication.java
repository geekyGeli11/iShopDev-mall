package com.macro.mall.selfcheck;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * mall自助结算系统服务启动类
 * 
 * @author macro
 * @since 1.0.0
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class MallSelfcheckApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallSelfcheckApplication.class, args);
    }
}