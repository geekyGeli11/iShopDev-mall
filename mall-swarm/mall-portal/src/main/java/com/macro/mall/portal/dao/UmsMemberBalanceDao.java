package com.macro.mall.portal.dao;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 用户余额Dao
 */
public interface UmsMemberBalanceDao {

    /**
     * 获取用户余额统计信息
     * @param memberId 用户ID
     * @return 统计信息
     */
    Map<String, BigDecimal> getBalanceStatistics(@Param("memberId") Long memberId);

    /**
     * 获取用户今日充值总额
     * @param memberId 用户ID
     * @return 今日充值总额
     */
    BigDecimal getTodayRechargeAmount(@Param("memberId") Long memberId);

    /**
     * 获取用户今日消费总额
     * @param memberId 用户ID
     * @return 今日消费总额
     */
    BigDecimal getTodayConsumeAmount(@Param("memberId") Long memberId);

    /**
     * 获取用户累计充值总额
     * @param memberId 用户ID
     * @return 累计充值总额
     */
    BigDecimal getTotalRechargeAmount(@Param("memberId") Long memberId);

    /**
     * 获取用户累计消费总额
     * @param memberId 用户ID
     * @return 累计消费总额
     */
    BigDecimal getTotalConsumeAmount(@Param("memberId") Long memberId);
} 