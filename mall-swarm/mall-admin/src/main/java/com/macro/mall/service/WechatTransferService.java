package com.macro.mall.service;

import com.macro.mall.dto.WechatTransferParam;
import com.macro.mall.dto.WechatTransferResult;

import java.math.BigDecimal;

/**
 * 微信商家转账到零钱服务（v3版本）
 * 对应接口：POST /v3/transfer/batches
 * 
 * @author macro
 * @date 2024/12/19
 */
public interface WechatTransferService {
    
    /**
     * 发起商家转账到零钱
     * 
     * @param transferParam 转账参数
     * @return 转账结果
     */
    WechatTransferResult transfer(WechatTransferParam transferParam);
    
    /**
     * 构建转账参数
     * 
     * @param withdrawSn 提现编号
     * @param openid 用户openid
     * @param amount 转账金额（元）
     * @param userName 用户姓名（可选）
     * @param userNickname 用户昵称
     * @return 转账参数
     */
    WechatTransferParam buildTransferParam(String withdrawSn, String openid, 
                                         BigDecimal amount, String userName, String userNickname);
} 