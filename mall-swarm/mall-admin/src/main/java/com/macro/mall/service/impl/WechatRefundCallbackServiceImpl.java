package com.macro.mall.service.impl;

import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.macro.mall.mapper.OmsOrderReturnApplyMapper;
import com.macro.mall.mapper.OmsRefundRecordMapper;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.mapper.OmsOrderOperateHistoryMapper;
import com.macro.mall.model.OmsOrderReturnApply;
import com.macro.mall.model.OmsOrderReturnApplyExample;
import com.macro.mall.model.OmsRefundRecord;
import com.macro.mall.model.OmsRefundRecordExample;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.OmsOrderOperateHistory;
import com.macro.mall.service.WechatRefundCallbackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 微信退款回调服务实现
 * Created by macro on 2024/01/01.
 */
@Service
public class WechatRefundCallbackServiceImpl implements WechatRefundCallbackService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WechatRefundCallbackServiceImpl.class);

    @Autowired
    private WxPayService wxPayService;

    @Autowired
    private OmsRefundRecordMapper refundRecordMapper;

    @Autowired
    private OmsOrderReturnApplyMapper returnApplyMapper;

    @Autowired
    private OmsOrderMapper orderMapper;

    @Autowired
    private OmsOrderOperateHistoryMapper orderOperateHistoryMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String handleRefundNotify(String xmlData) {
        try {
            LOGGER.info("开始处理微信退款回调通知");

            // 解析微信退款回调数据
            WxPayRefundNotifyResult notifyResult = wxPayService.parseRefundNotifyResult(xmlData);

            if (notifyResult == null) {
                LOGGER.error("微信退款回调数据解析失败");
                return buildFailResponse("数据解析失败");
            }

            // 获取退款信息
            String outRefundNo = notifyResult.getReqInfo().getOutRefundNo();
            String refundId = notifyResult.getReqInfo().getRefundId();
            String refundStatus = notifyResult.getReqInfo().getRefundStatus();
            Integer refundFee = notifyResult.getReqInfo().getRefundFee();

            LOGGER.info("微信退款回调信息 - 商户退款单号：{}，微信退款单号：{}，退款状态：{}，退款金额：{}分",
                    outRefundNo, refundId, refundStatus, refundFee);

            // 查找对应的退款记录
            OmsRefundRecordExample example = new OmsRefundRecordExample();
            example.createCriteria().andRefundSnEqualTo(outRefundNo);
            List<OmsRefundRecord> refundRecords = refundRecordMapper.selectByExample(example);

            if (refundRecords.isEmpty()) {
                LOGGER.error("未找到对应的退款记录，商户退款单号：{}", outRefundNo);
                return buildFailResponse("未找到退款记录");
            }

            OmsRefundRecord refundRecord = refundRecords.get(0);

            // 更新退款记录状态
            OmsRefundRecord updateRecord = new OmsRefundRecord();
            updateRecord.setId(refundRecord.getId());

            if ("SUCCESS".equals(refundStatus)) {
                // 退款成功
                updateRecord.setStatus((byte) 1);
                updateRecord.setRefundTime(new Date());

                // 更新退货申请状态为已完成
                OmsOrderReturnApply updateApply = new OmsOrderReturnApply();
                updateApply.setId(refundRecord.getReturnApplyId());
                updateApply.setStatus(2); // 已完成
                updateApply.setRefundStatus((byte) 1); // 已退款
                updateApply.setRefundProcessStatus((byte) 2); // 退款处理完成
                updateApply.setRefundTime(new Date());
                updateApply.setHandleTime(new Date());
                updateApply.setRefundTransactionId(refundId); // 记录微信退款交易号
                returnApplyMapper.updateByPrimaryKeySelective(updateApply);

                // 微信退款成功后检查并更新订单状态
                // 获取完整的退货申请信息
                OmsOrderReturnApply fullReturnApply = returnApplyMapper.selectByPrimaryKey(refundRecord.getReturnApplyId());
                if (fullReturnApply != null) {
                    checkAndUpdateOrderStatusAfterRefund(fullReturnApply);
                }

                LOGGER.info("微信退款成功处理完成，退款单号：{}，申请ID：{}，微信退款单号：{}",
                    outRefundNo, refundRecord.getReturnApplyId(), refundId);

            } else if ("CHANGE".equals(refundStatus)) {
                // 退款异常，退回原支付银行卡
                updateRecord.setStatus((byte) 1);
                updateRecord.setRefundTime(new Date());

                // 更新退货申请状态为已完成
                OmsOrderReturnApply updateApply = new OmsOrderReturnApply();
                updateApply.setId(refundRecord.getReturnApplyId());
                updateApply.setStatus(2); // 已完成
                updateApply.setRefundStatus((byte) 1); // 已退款
                updateApply.setRefundProcessStatus((byte) 2); // 退款处理完成
                updateApply.setRefundTime(new Date());
                updateApply.setHandleTime(new Date());
                updateApply.setRefundTransactionId(refundId); // 记录微信退款交易号
                returnApplyMapper.updateByPrimaryKeySelective(updateApply);

                // 微信退款异常但成功后也检查并更新订单状态
                OmsOrderReturnApply fullReturnApply = returnApplyMapper.selectByPrimaryKey(refundRecord.getReturnApplyId());
                if (fullReturnApply != null) {
                    checkAndUpdateOrderStatusAfterRefund(fullReturnApply);
                }

                LOGGER.info("微信退款异常处理完成（退回银行卡），退款单号：{}，申请ID：{}，微信退款单号：{}",
                    outRefundNo, refundRecord.getReturnApplyId(), refundId);

            } else if ("REFUNDCLOSE".equals(refundStatus)) {
                // 退款关闭
                updateRecord.setStatus((byte) 2);

                // 更新退货申请的退款失败原因
                OmsOrderReturnApply updateApply = new OmsOrderReturnApply();
                updateApply.setId(refundRecord.getReturnApplyId());
                updateApply.setRefundProcessStatus((byte) 3); // 退款失败
                updateApply.setRefundFailReason("微信退款关闭");
                returnApplyMapper.updateByPrimaryKeySelective(updateApply);

                LOGGER.error("微信退款关闭，退款单号：{}，申请ID：{}", outRefundNo, refundRecord.getReturnApplyId());

            } else {
                // 其他状态
                LOGGER.warn("微信退款未知状态：{}，退款单号：{}，申请ID：{}",
                    refundStatus, outRefundNo, refundRecord.getReturnApplyId());
            }

            // 更新退款记录
            refundRecordMapper.updateByPrimaryKeySelective(updateRecord);

            LOGGER.info("微信退款回调处理完成，退款单号：{}，最终状态：{}", outRefundNo, refundStatus);

            return buildSuccessResponse();

        } catch (WxPayException e) {
            LOGGER.error("微信退款回调处理异常，错误代码：{}，错误信息：{}", e.getErrCode(), e.getErrCodeDes(), e);
            return buildFailResponse("回调处理异常：" + e.getErrCodeDes());
        } catch (Exception e) {
            LOGGER.error("微信退款回调处理异常", e);
            return buildFailResponse("回调处理异常：" + e.getMessage());
        }
    }

    /**
     * 检查并更新订单状态（微信退款成功后）
     *
     * @param returnApply 退货申请信息
     */
    private void checkAndUpdateOrderStatusAfterRefund(OmsOrderReturnApply returnApply) {
        try {
            Long orderId = returnApply.getOrderId();

            // 1. 查询订单信息
            OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
            if (order == null) {
                LOGGER.warn("订单不存在，无法更新状态，订单ID: {}", orderId);
                return;
            }

            // 如果订单已经是已关闭状态，无需重复更新
            if (order.getStatus() != null && order.getStatus() == 4) {
                LOGGER.info("订单已经是已关闭状态，无需更新，订单ID: {}", orderId);
                return;
            }

            // 2. 查询该订单所有已完成且退款成功的退货申请
            OmsOrderReturnApplyExample example = new OmsOrderReturnApplyExample();
            example.createCriteria()
                    .andOrderIdEqualTo(orderId)
                    .andStatusEqualTo(2) // 已完成
                    .andRefundStatusEqualTo((byte) 1); // 已退款

            List<OmsOrderReturnApply> completedRefunds = returnApplyMapper.selectByExample(example);

            // 3. 计算总退款金额
            BigDecimal totalRefundAmount = completedRefunds.stream()
                    .map(OmsOrderReturnApply::getReturnAmount)
                    .filter(amount -> amount != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 4. 获取订单支付金额
            BigDecimal orderPayAmount = order.getPayAmount();
            if (orderPayAmount == null) {
                orderPayAmount = BigDecimal.ZERO;
            }

            LOGGER.info("微信退款后订单检查，订单ID: {}, 订单支付金额: {}, 总退款金额: {}",
                orderId, orderPayAmount, totalRefundAmount);

            // 5. 如果总退款金额 >= 订单支付金额，更新订单状态为已关闭
            if (totalRefundAmount.compareTo(orderPayAmount) >= 0) {
                OmsOrder updateOrder = new OmsOrder();
                updateOrder.setId(orderId);
                updateOrder.setStatus(4); // 已关闭
                updateOrder.setModifyTime(new Date());

                int updateResult = orderMapper.updateByPrimaryKeySelective(updateOrder);
                if (updateResult > 0) {
                    // 记录操作历史
                    OmsOrderOperateHistory history = new OmsOrderOperateHistory();
                    history.setOrderId(orderId);
                    history.setCreateTime(new Date());
                    history.setOperateMan("系统");
                    history.setOrderStatus(4);
                    history.setNote(String.format("微信退款完成，订单自动关闭（退款金额：¥%.2f）", totalRefundAmount));
                    orderOperateHistoryMapper.insert(history);

                    LOGGER.info("微信退款后订单状态已更新为已关闭，订单ID: {}, 总退款金额: {}", orderId, totalRefundAmount);
                } else {
                    LOGGER.error("微信退款后更新订单状态失败，订单ID: {}", orderId);
                }
            } else {
                LOGGER.info("微信退款后部分退款，订单状态保持不变，订单ID: {}, 剩余金额: {}",
                    orderId, orderPayAmount.subtract(totalRefundAmount));
            }

        } catch (Exception e) {
            LOGGER.error("微信退款后检查并更新订单状态异常，退货申请ID: {}", returnApply.getId(), e);
        }
    }

    /**
     * 构建成功响应
     */
    private String buildSuccessResponse() {
        return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
    }

    /**
     * 构建失败响应
     */
    private String buildFailResponse(String message) {
        return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[" + message + "]]></return_msg></xml>";
    }
}
