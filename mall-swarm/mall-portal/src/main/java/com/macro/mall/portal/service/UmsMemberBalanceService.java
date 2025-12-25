package com.macro.mall.portal.service;

import com.macro.mall.portal.domain.MemberBalanceInfo;
import com.macro.mall.portal.domain.RechargeConfigResult;
import com.macro.mall.portal.domain.RechargeOrderParam;
import com.macro.mall.portal.domain.RechargeOrderResult;
import com.macro.mall.model.UmsMemberBalanceHistory;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户余额管理Service
 */
public interface UmsMemberBalanceService {

    /**
     * 获取用户余额信息
     * @param memberId 用户ID
     * @return 余额信息
     */
    MemberBalanceInfo getBalanceInfo(Long memberId);

    /**
     * 创建充值订单
     * @param memberId 用户ID
     * @param param 充值参数
     * @return 充值订单结果
     */
    RechargeOrderResult createRechargeOrder(Long memberId, RechargeOrderParam param);

    /**
     * 处理充值支付成功
     * @param orderSn 订单号
     * @param payType 支付方式
     * @param paySn 第三方支付流水号
     * @return 处理结果
     */
    boolean handleRechargePaySuccess(String orderSn, Integer payType, String paySn);

    /**
     * 获取充值配置
     * @return 充值配置信息
     */
    RechargeConfigResult getRechargeConfig();

    /**
     * 余额消费
     * @param memberId 用户ID
     * @param amount 消费金额
     * @param businessType 业务类型
     * @param businessId 业务ID
     * @param remark 备注
     * @return 是否成功
     */
    boolean consumeBalance(Long memberId, BigDecimal amount, String businessType, String businessId, String remark);

    /**
     * 余额退款
     * @param memberId 用户ID
     * @param amount 退款金额
     * @param businessType 业务类型
     * @param businessId 业务ID
     * @param remark 备注
     * @return 是否成功
     */
    boolean refundBalance(Long memberId, BigDecimal amount, String businessType, String businessId, String remark);

    /**
     * 余额变动记录（内部方法）
     * @param memberId 用户ID
     * @param changeType 变动类型：1-充值，2-消费，3-退款，4-转入，5-转出
     * @param amount 变动金额
     * @param businessType 业务类型
     * @param businessId 业务ID
     * @param remark 备注
     * @return 是否成功
     */
    boolean recordBalanceChange(Long memberId, Integer changeType, BigDecimal amount, 
                               String businessType, String businessId, String remark);

    /**
     * 检查用户余额是否足够
     * @param memberId 用户ID
     * @param amount 需要的金额
     * @return 是否足够
     */
    boolean checkBalanceEnough(Long memberId, BigDecimal amount);

    /**
     * 获取用户余额变动历史
     * @param memberId 用户ID
     * @param changeType 变动类型（可选）：1-充值，2-消费，3-退款，4-转入，5-转出
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 历史记录列表
     */
    List<UmsMemberBalanceHistory> getBalanceHistory(Long memberId, Integer changeType, Integer pageNum, Integer pageSize);
} 