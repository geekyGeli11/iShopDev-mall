package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.common.util.WxMessageUtil;
import com.macro.mall.dao.OmsOrderReturnApplyDao;
import com.macro.mall.dto.OmsOrderReturnApplyResult;
import com.macro.mall.dto.OmsReturnApplyQueryParam;
import com.macro.mall.dto.OmsUpdateStatusParam;
import com.macro.mall.mapper.OmsOrderReturnApplyMapper;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.mapper.OmsRefundRecordMapper;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.mapper.UmsMemberBalanceHistoryMapper;
import com.macro.mall.mapper.OmsOrderOperateHistoryMapper;
import com.macro.mall.mapper.OmsOrderOperateHistoryMapper;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.OmsOrderReturnApply;
import com.macro.mall.model.OmsOrderReturnApplyExample;
import com.macro.mall.model.OmsRefundRecord;
import com.macro.mall.model.OmsRefundRecordExample;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberBalanceHistory;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberExample;
import com.macro.mall.model.OmsOrderOperateHistory;
import com.macro.mall.service.OmsOrderReturnApplyService;
import com.macro.mall.service.PmsProductPaybackService;
import com.macro.mall.service.WechatRefundService;
import com.macro.mall.service.AlipayRefundService;

import com.macro.mall.dto.WechatRefundParam;
import com.macro.mall.dto.WechatRefundResult;
import com.macro.mall.controller.OmsOrderReturnApplyController.RefundParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Date;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单退货管理Service
 * Created by macro on 2018/10/18.
 */
@Service
public class OmsOrderReturnApplyServiceImpl implements OmsOrderReturnApplyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OmsOrderReturnApplyServiceImpl.class);

    @Value("${template.return-notify:}")
    private String returnNotifyTemplateId;

    @Autowired
    private OmsOrderReturnApplyDao returnApplyDao;
    @Autowired
    private OmsOrderReturnApplyMapper returnApplyMapper;
    @Autowired
    private UmsMemberMapper umsMemberMapper;
    
    @Autowired
    private OmsOrderMapper omsOrderMapper;
    
    @Autowired
    private OmsRefundRecordMapper omsRefundRecordMapper;
    
    @Autowired(required = false)
    private WxMessageUtil wxMessageUtil;
    
    @Autowired
    private WechatRefundService wechatRefundService;
    
    @Autowired(required = false)
    private AlipayRefundService alipayRefundService;
    
    @Autowired
    private PmsProductPaybackService paybackService;

    @Autowired
    private UmsMemberMapper memberMapper;

    @Autowired
    private UmsMemberBalanceHistoryMapper memberBalanceHistoryMapper;

    @Autowired
    private OmsOrderOperateHistoryMapper orderOperateHistoryMapper;
    @Override
    public List<OmsOrderReturnApply> list(OmsReturnApplyQueryParam queryParam, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum,pageSize);
        return returnApplyDao.getList(queryParam);
    }

    @Override
    public List<OmsOrderReturnApplyResult> listWithDetails(OmsReturnApplyQueryParam queryParam, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        return returnApplyDao.getListWithDetails(queryParam);
    }

    @Override
    public int delete(List<Long> ids) {
        OmsOrderReturnApplyExample example = new OmsOrderReturnApplyExample();
        example.createCriteria().andIdIn(ids).andStatusEqualTo(3);
        return returnApplyMapper.deleteByExample(example);
    }

    @Override
    public int updateStatus(Long id, OmsUpdateStatusParam statusParam) {
        Integer status = statusParam.getStatus();
        OmsOrderReturnApply returnApply = new OmsOrderReturnApply();
        if(status.equals(1)){
            //确认退货
            returnApply.setId(id);
            returnApply.setStatus(1);
            returnApply.setReturnAmount(statusParam.getReturnAmount());
            returnApply.setCompanyAddressId(statusParam.getCompanyAddressId());
            returnApply.setHandleTime(new Date());
            returnApply.setHandleMan(statusParam.getHandleMan());
            returnApply.setHandleNote(statusParam.getHandleNote());
            
            // 确认退货后发送订阅消息通知用户补充退货信息
            sendReturnNotifyMessage(id);
        }else if(status.equals(2)){
            //确认收货（对于退货退款类型，这里不是最终完成状态）
            returnApply.setId(id);
            returnApply.setStatus(2);
            returnApply.setReceiveTime(new Date());
            returnApply.setReceiveMan(statusParam.getReceiveMan());
            returnApply.setReceiveNote(statusParam.getReceiveNote());

            // 注意：对于退货退款类型，确认收货后状态为2，但还需要退款才算真正完成
        }else if(status.equals(3)){
            //拒绝退货
            returnApply.setId(id);
            returnApply.setStatus(3);
            returnApply.setHandleTime(new Date());
            returnApply.setHandleMan(statusParam.getHandleMan());
            returnApply.setHandleNote(statusParam.getHandleNote());
        }else{
            return 0;
        }
        return returnApplyMapper.updateByPrimaryKeySelective(returnApply);
    }

    /**
     * 发送退货通知订阅消息
     */
    private void sendReturnNotifyMessage(Long returnApplyId) {
        try {
            // 获取退货申请详情
            OmsOrderReturnApplyResult returnApplyResult = returnApplyDao.getDetail(returnApplyId);
            if (returnApplyResult == null) {
                LOGGER.warn("退货申请不存在，ID：{}", returnApplyId);
                return;
            }

            // 根据用户名查询用户信息
            UmsMemberExample memberExample = new UmsMemberExample();
            memberExample.createCriteria().andUsernameEqualTo(returnApplyResult.getMemberUsername());
            List<UmsMember> memberList = umsMemberMapper.selectByExample(memberExample);
            
            if (memberList.isEmpty()) {
                LOGGER.warn("用户不存在，用户名：{}", returnApplyResult.getMemberUsername());
                return;
            }
            
            UmsMember member = memberList.get(0);
            if (member.getOpenid() == null || member.getOpenid().isEmpty()) {
                LOGGER.warn("用户未绑定微信，用户名：{}", returnApplyResult.getMemberUsername());
                return;
            }

            // 构建订阅消息数据
            Map<String, String> templateData = new HashMap<>();
            templateData.put("character_string1", returnApplyResult.getOrderSn()); // 订单单号
            templateData.put("thing2", returnApplyResult.getProductName() != null ? returnApplyResult.getProductName() : "商品"); // 商品信息
            templateData.put("thing3", "退货中"); // 退货类型
            templateData.put("date4", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())); // 提醒时间
            templateData.put("thing5", "您的退货申请已通过，请及时补充退货快递信息"); // 温馨提示

            // 构建小程序跳转路径，携带订单ID和商品ID参数
            String miniProgramPath = "/pages/order/returnApply?orderId=" + returnApplyResult.getOrderId() + 
                                   "&orderItemId=" + returnApplyResult.getProductId() + "&fromNotify=1";

            // 发送订阅消息（如果微信服务可用）
            if (wxMessageUtil != null) {
                boolean sendResult = wxMessageUtil.sendSubscribeMessage(
                    member.getOpenid(), 
                    returnNotifyTemplateId, 
                    templateData, 
                    miniProgramPath
                );

                if (sendResult) {
                    LOGGER.info("退货通知订阅消息发送成功，申请ID：{}", returnApplyId);
                } else {
                    LOGGER.warn("退货通知订阅消息发送失败，申请ID：{}", returnApplyId);
                }
            } else {
                LOGGER.info("微信服务不可用，跳过发送退货通知订阅消息，申请ID：{}", returnApplyId);
            }

        } catch (Exception e) {
            LOGGER.error("发送退货通知订阅消息异常，申请ID：{}", returnApplyId, e);
        }
    }

    @Override
    public OmsOrderReturnApplyResult getItem(Long id) {
        return returnApplyDao.getDetail(id);
    }

    @Override
    public boolean processRefund(Long id, RefundParam refundParam) {
        try {
            // 1. 获取退货申请详情
            OmsOrderReturnApplyResult returnApply = returnApplyDao.getDetail(id);
            if (returnApply == null) {
                LOGGER.error("退货申请不存在，ID：{}", id);
                return false;
            }

            // 2. 验证退货申请状态
            // 仅退款类型：状态为1（退货中）时可以退款
            // 退货退款类型：状态为1（退货中）或2（已完成/已确认收货）时可以退款
            if (returnApply.getReturnType() == 1) {
                // 仅退款类型：只有状态为1时才能退款
                if (returnApply.getStatus() != 1) {
                    LOGGER.error("仅退款申请状态不正确，当前状态：{}，申请ID：{}", returnApply.getStatus(), id);
                    return false;
                }
            } else if (returnApply.getReturnType() == 2) {
                // 退货退款类型：状态为1或2时都可以退款
                if (returnApply.getStatus() != 1 && returnApply.getStatus() != 2) {
                    LOGGER.error("退货退款申请状态不正确，当前状态：{}，申请ID：{}", returnApply.getStatus(), id);
                    return false;
                }
            } else {
                LOGGER.error("未知的售后类型：{}，申请ID：{}", returnApply.getReturnType(), id);
                return false;
            }

            // 3. 验证退款金额
            if (returnApply.getReturnAmount() == null || returnApply.getReturnAmount().doubleValue() <= 0) {
                LOGGER.error("退款金额不正确：{}，申请ID：{}", returnApply.getReturnAmount(), id);
                return false;
            }
            
            // 4. 查询订单信息获取实际支付金额，用于验证退款金额
            OmsOrder order = omsOrderMapper.selectByPrimaryKey(returnApply.getOrderId());
            if (order == null) {
                LOGGER.error("订单不存在，订单ID：{}", returnApply.getOrderId());
                return false;
            }
            
            // 5. 验证退款金额不能超过订单实际支付金额
            if (returnApply.getReturnAmount().doubleValue() > order.getPayAmount().doubleValue()) {
                LOGGER.error("退款金额：{}超过订单支付金额：{}，申请ID：{}", 
                    returnApply.getReturnAmount(), order.getPayAmount(), id);
                return false;
            }
            
            // 6. 检查是否已经退款（防止重复退款）
            if (returnApply.getRefundStatus() != null && returnApply.getRefundStatus() == 1) {
                LOGGER.warn("该退货申请已经退款，申请ID：{}，退款状态：{}", id, returnApply.getRefundStatus());
                return false;
            }
            
            // 7. 检查退款记录表中是否已有成功的退款记录
            OmsRefundRecordExample refundExample = new OmsRefundRecordExample();
            refundExample.createCriteria()
                .andReturnApplyIdEqualTo(id)
                .andStatusEqualTo((byte) 1); // 1表示退款成功
            List<OmsRefundRecord> existingRefunds = omsRefundRecordMapper.selectByExample(refundExample);
            if (!existingRefunds.isEmpty()) {
                LOGGER.warn("该退货申请已存在成功的退款记录，申请ID：{}，退款记录数：{}", id, existingRefunds.size());
                return false;
            }

            // 8. 根据订单支付方式进行退款处理
            RefundResult refundResult = processRefundByPayType(returnApply, refundParam, order);
            
            if (refundResult != null && refundResult.isSuccess()) {
                // 9. 退款成功，创建退款记录
                OmsRefundRecord refundRecord = new OmsRefundRecord();
                refundRecord.setReturnApplyId(id);
                refundRecord.setRefundAmount(returnApply.getReturnAmount());
                refundRecord.setRefundTime(new Date());
                refundRecord.setRefundSn(refundResult.getRefundSn());
                // 根据退款方式设置不同的状态
                if ("余额退款".equals(refundResult.getRefundMethod())) {
                    refundRecord.setStatus((byte) 1); // 余额退款立即成功
                } else {
                    refundRecord.setStatus((byte) 0); // 微信退款初始状态为处理中
                }

                int insertCount = omsRefundRecordMapper.insertSelective(refundRecord);
                if (insertCount <= 0) {
                    LOGGER.error("创建退款记录失败，申请ID：{}", id);
                    return false;
                }

                // 10. 更新退货申请的退款处理状态和申请状态
                OmsOrderReturnApply updateApply = new OmsOrderReturnApply();
                updateApply.setId(id);

                // 根据退款方式设置不同的状态
                if ("余额退款".equals(refundResult.getRefundMethod())) {
                    updateApply.setRefundProcessStatus((byte) 2); // 余额退款立即成功
                    updateApply.setRefundStatus((byte) 1); // 已退款
                    updateApply.setRefundTime(new Date());
                    updateApply.setStatus(2); // 申请状态设置为已完成
                } else {
                    updateApply.setRefundProcessStatus((byte) 1); // 微信退款处理中
                    updateApply.setRefundStatus((byte) 0); // 微信退款处理中
                    // 申请状态保持当前状态，等待退款回调后再更新为已完成
                }
                
                // 更新处理备注，记录退款信息
                String refundNote;
                if ("余额退款".equals(refundResult.getRefundMethod())) {
                    refundNote = String.format("已退款：¥%.2f，退款单号：%s，退款方式：%s，退款原因：%s",
                        returnApply.getReturnAmount(),
                        refundResult.getRefundSn(),
                        refundResult.getRefundMethod(),
                        refundParam.getRefundReason() != null ? refundParam.getRefundReason() : "退货退款");
                } else {
                    refundNote = String.format("退款处理中：¥%.2f，退款单号：%s，退款方式：%s，退款原因：%s",
                        returnApply.getReturnAmount(),
                        refundResult.getRefundSn(),
                        refundResult.getRefundMethod(),
                        refundParam.getRefundReason() != null ? refundParam.getRefundReason() : "退货退款");
                }
                
                String originalNote = returnApply.getHandleNote() != null ? returnApply.getHandleNote() : "";
                updateApply.setHandleNote(originalNote.isEmpty() ? refundNote : originalNote + "；" + refundNote);
                
                int finalUpdateCount = returnApplyMapper.updateByPrimaryKeySelective(updateApply);

                if (finalUpdateCount > 0) {
                    if ("余额退款".equals(refundResult.getRefundMethod())) {
                        LOGGER.info("余额退款成功，申请ID：{}，退款金额：{}，退款单号：{}",
                            id, returnApply.getReturnAmount(), refundResult.getRefundSn());

                        // 余额退款成功后检查并更新订单状态
                        checkAndUpdateOrderStatusAfterRefund(returnApply.getOrderId());
                    } else {
                        LOGGER.info("微信退款提交成功，申请ID：{}，退款金额：{}，退款单号：{}，等待微信处理结果",
                            id, returnApply.getReturnAmount(), refundResult.getRefundSn());
                    }

                    // 退款成功后更新商品回本分析数据
                    try {
                        paybackService.updateOnRefund(returnApply.getOrderId(), returnApply.getOrderSn());
                        LOGGER.info("退款成功后回本分析更新完成，订单ID: {}", returnApply.getOrderId());
                    } catch (Exception e) {
                        LOGGER.error("退款成功后更新回本分析失败，订单ID: {}, 错误: {}", returnApply.getOrderId(), e.getMessage(), e);
                        // 回本分析更新失败不影响退款成功
                    }

                    return true;
                } else {
                    LOGGER.error("更新退货申请退款状态失败，申请ID：{}", id);
                    // 回滚：删除已创建的退款记录
                    omsRefundRecordMapper.deleteByPrimaryKey(refundRecord.getId());
                    return false;
                }
            } else {
                LOGGER.error("微信退款失败，申请ID：{}，错误信息：{}", 
                    id, refundResult != null ? refundResult.getErrorMsg() : "未知错误");
                return false;
            }

        } catch (Exception e) {
            LOGGER.error("处理退款异常，申请ID：{}", id, e);
            return false;
        }
    }

    /**
     * 根据支付方式处理退款
     */
    private RefundResult processRefundByPayType(OmsOrderReturnApplyResult returnApply, RefundParam refundParam, OmsOrder order) {
        try {
            LOGGER.info("开始处理退款，订单号：{}，支付方式：{}，退款金额：{}",
                returnApply.getOrderSn(), order.getPayType(), returnApply.getReturnAmount());

            RefundResult refundResult = new RefundResult();

            // 根据支付方式进行不同的退款处理
            if (order.getPayType() == null) {
                LOGGER.error("订单支付方式为空，无法进行退款，订单号：{}", returnApply.getOrderSn());
                refundResult.setSuccess(false);
                refundResult.setErrorMsg("订单支付方式为空，无法进行退款");
                return refundResult;
            }

            switch (order.getPayType()) {
                case 3: // 余额支付
                    return processBalanceRefund(returnApply, refundParam, order);
                case 2: // 微信支付
                    return processWechatRefundNew(returnApply, refundParam, order);
                case 1: // 支付宝支付
                    return processAlipayRefundNew(returnApply, refundParam, order);
                default:
                    LOGGER.error("不支持的支付方式：{}，订单号：{}", order.getPayType(), returnApply.getOrderSn());
                    refundResult.setSuccess(false);
                    refundResult.setErrorMsg("不支持的支付方式：" + order.getPayType());
                    return refundResult;
            }

        } catch (Exception e) {
            LOGGER.error("退款处理异常，订单号：{}", returnApply.getOrderSn(), e);
            RefundResult errorResult = new RefundResult();
            errorResult.setSuccess(false);
            errorResult.setErrorMsg("退款处理异常：" + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 处理余额退款
     */
    @Transactional(rollbackFor = Exception.class)
    private RefundResult processBalanceRefund(OmsOrderReturnApplyResult returnApply, RefundParam refundParam, OmsOrder order) {
        try {
            LOGGER.info("开始处理余额退款，订单号：{}，用户ID：{}，退款金额：{}",
                returnApply.getOrderSn(), order.getMemberId(), returnApply.getReturnAmount());

            // 1. 获取用户信息
            UmsMember member = memberMapper.selectByPrimaryKey(order.getMemberId());
            if (member == null) {
                LOGGER.error("用户不存在，用户ID：{}", order.getMemberId());
                RefundResult errorResult = new RefundResult();
                errorResult.setSuccess(false);
                errorResult.setErrorMsg("用户不存在");
                return errorResult;
            }

            // 2. 计算退款后的余额
            BigDecimal currentBalance = member.getBalance() != null ? member.getBalance() : BigDecimal.ZERO;
            BigDecimal newBalance = currentBalance.add(returnApply.getReturnAmount());

            // 3. 更新用户余额
            UmsMember updateMember = new UmsMember();
            updateMember.setId(order.getMemberId());
            updateMember.setBalance(newBalance);
            int updateCount = memberMapper.updateByPrimaryKeySelective(updateMember);

            if (updateCount <= 0) {
                LOGGER.error("更新用户余额失败，用户ID：{}", order.getMemberId());
                RefundResult errorResult = new RefundResult();
                errorResult.setSuccess(false);
                errorResult.setErrorMsg("更新用户余额失败");
                return errorResult;
            }

            // 4. 记录余额变动历史
            UmsMemberBalanceHistory balanceHistory = new UmsMemberBalanceHistory();
            balanceHistory.setMemberId(order.getMemberId());
            balanceHistory.setChangeType((byte) 3); // 3-退款
            balanceHistory.setAmount(returnApply.getReturnAmount());
            balanceHistory.setBalanceBefore(currentBalance);
            balanceHistory.setBalanceAfter(newBalance);
            balanceHistory.setBusinessType("ORDER_REFUND");
            balanceHistory.setBusinessId(returnApply.getOrderSn());
            balanceHistory.setRemark("订单退款：" + (refundParam.getRefundReason() != null ? refundParam.getRefundReason() : "退货退款"));
            balanceHistory.setOperator("SYSTEM");
            balanceHistory.setCreateTime(new Date());

            int historyCount = memberBalanceHistoryMapper.insertSelective(balanceHistory);
            if (historyCount <= 0) {
                LOGGER.error("记录余额变动历史失败，用户ID：{}", order.getMemberId());
                // 余额变动历史记录失败不影响退款成功，只记录日志
            }

            // 5. 构建成功结果
            RefundResult refundResult = new RefundResult();
            refundResult.setSuccess(true);
            refundResult.setRefundSn("BF" + System.currentTimeMillis()); // 余额退款单号
            refundResult.setRefundMethod("余额退款");

            LOGGER.info("余额退款成功，订单号：{}，用户ID：{}，退款金额：{}，余额变化：{} -> {}",
                returnApply.getOrderSn(), order.getMemberId(), returnApply.getReturnAmount(), currentBalance, newBalance);

            return refundResult;

        } catch (Exception e) {
            LOGGER.error("余额退款处理异常，订单号：{}，用户ID：{}", returnApply.getOrderSn(), order.getMemberId(), e);
            RefundResult errorResult = new RefundResult();
            errorResult.setSuccess(false);
            errorResult.setErrorMsg("余额退款处理异常：" + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 处理微信支付退款（新版本）
     */
    private RefundResult processWechatRefundNew(OmsOrderReturnApplyResult returnApply, RefundParam refundParam, OmsOrder order) {
        try {
            LOGGER.info("开始处理微信退款，订单号：{}，退款金额：{}",
                returnApply.getOrderSn(), returnApply.getReturnAmount());

            // 构建微信退款参数
            WechatRefundParam wechatRefundParam = new WechatRefundParam();
            wechatRefundParam.setOutTradeNo(returnApply.getOrderSn()); // 商户订单号
            wechatRefundParam.setOutRefundNo("WF" + System.currentTimeMillis()); // 商户退款单号

            // 使用退货申请中设置的退款金额（支持部分退款）
            wechatRefundParam.setRefundFee((int)(returnApply.getReturnAmount().doubleValue() * 100)); // 退款金额（分）

            // 使用订单的实际支付金额作为总金额
            wechatRefundParam.setTotalFee((int)(order.getPayAmount().doubleValue() * 100)); // 订单实际支付总金额（分）

            wechatRefundParam.setRefundDesc(refundParam.getRefundReason() != null ? refundParam.getRefundReason() : "退货退款");

            LOGGER.info("微信退款参数 - 退款金额：¥{}, 订单实际支付总金额：¥{}",
                returnApply.getReturnAmount(),
                order.getPayAmount());

            // 调用微信退款服务
            WechatRefundResult wechatResult = wechatRefundService.processRefund(wechatRefundParam);

            // 转换为统一的退款结果
            RefundResult refundResult = new RefundResult();
            if (wechatResult.isSuccess()) {
                refundResult.setSuccess(true);
                refundResult.setRefundSn(wechatRefundParam.getOutRefundNo());
                refundResult.setRefundMethod("微信退款");
                LOGGER.info("微信退款成功，订单号：{}，微信退款单号：{}",
                    returnApply.getOrderSn(), wechatResult.getRefundId());
            } else {
                refundResult.setSuccess(false);
                refundResult.setErrorMsg(wechatResult.getErrorMsg());
                LOGGER.error("微信退款失败，订单号：{}，错误信息：{}",
                    returnApply.getOrderSn(), wechatResult.getErrorMsg());
            }

            return refundResult;

        } catch (Exception e) {
            LOGGER.error("微信退款处理异常，订单号：{}", returnApply.getOrderSn(), e);
            RefundResult errorResult = new RefundResult();
            errorResult.setSuccess(false);
            errorResult.setErrorMsg("微信退款处理异常：" + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 处理支付宝退款（新版本）
     */
    private RefundResult processAlipayRefundNew(OmsOrderReturnApplyResult returnApply, RefundParam refundParam, OmsOrder order) {
        try {
            LOGGER.info("开始处理支付宝退款，订单号：{}，退款金额：{}",
                returnApply.getOrderSn(), returnApply.getReturnAmount());

            if (alipayRefundService == null) {
                LOGGER.error("支付宝退款服务未启用，订单号：{}", returnApply.getOrderSn());
                RefundResult errorResult = new RefundResult();
                errorResult.setSuccess(false);
                errorResult.setErrorMsg("支付宝退款服务未启用");
                return errorResult;
            }

            // 生成退款单号
            String refundSn = "AF" + System.currentTimeMillis();

            // 调用支付宝退款服务
            boolean refundSuccess = alipayRefundService.processRefund(
                returnApply.getOrderSn(),
                refundSn,
                returnApply.getReturnAmount(),
                refundParam.getRefundReason() != null ? refundParam.getRefundReason() : "退货退款"
            );

            // 转换为统一的退款结果
            RefundResult refundResult = new RefundResult();
            if (refundSuccess) {
                refundResult.setSuccess(true);
                refundResult.setRefundSn(refundSn);
                refundResult.setRefundMethod("支付宝退款");
                LOGGER.info("支付宝退款成功，订单号：{}，退款单号：{}",
                    returnApply.getOrderSn(), refundSn);
            } else {
                refundResult.setSuccess(false);
                refundResult.setErrorMsg("支付宝退款失败");
                LOGGER.error("支付宝退款失败，订单号：{}", returnApply.getOrderSn());
            }

            return refundResult;

        } catch (Exception e) {
            LOGGER.error("支付宝退款处理异常，订单号：{}", returnApply.getOrderSn(), e);
            RefundResult errorResult = new RefundResult();
            errorResult.setSuccess(false);
            errorResult.setErrorMsg("支付宝退款处理异常：" + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 处理微信支付退款
     */
    private WechatRefundResult processWechatRefund(OmsOrderReturnApplyResult returnApply, RefundParam refundParam, OmsOrder order) {
        try {
            LOGGER.info("开始处理微信退款，订单号：{}，退款金额：{}", 
                returnApply.getOrderSn(), returnApply.getReturnAmount());
            
            // 构建微信退款参数
            WechatRefundParam wechatRefundParam = new WechatRefundParam();
            wechatRefundParam.setOutTradeNo(returnApply.getOrderSn()); // 商户订单号
            wechatRefundParam.setOutRefundNo("RF" + System.currentTimeMillis()); // 商户退款单号
            
            // 使用退货申请中设置的退款金额（支持部分退款）
            wechatRefundParam.setRefundFee((int)(returnApply.getReturnAmount().doubleValue() * 100)); // 退款金额（分）
            
            // 使用订单的实际支付金额作为总金额
            wechatRefundParam.setTotalFee((int)(order.getPayAmount().doubleValue() * 100)); // 订单实际支付总金额（分）
            
            wechatRefundParam.setRefundDesc(refundParam.getRefundReason() != null ? refundParam.getRefundReason() : "退货退款");
            
            LOGGER.info("退款参数 - 退款金额：¥{}, 订单实际支付总金额：¥{}", 
                returnApply.getReturnAmount(), 
                order.getPayAmount());
            
            // 调用微信退款服务
            WechatRefundResult refundResult = wechatRefundService.processRefund(wechatRefundParam);
            
            if (refundResult.isSuccess()) {
                LOGGER.info("微信退款成功，订单号：{}，微信退款单号：{}", 
                    returnApply.getOrderSn(), refundResult.getRefundId());
                return refundResult;
            } else {
                LOGGER.error("微信退款失败，订单号：{}，错误信息：{}", 
                    returnApply.getOrderSn(), refundResult.getErrorMsg());
                return refundResult;
            }

        } catch (Exception e) {
            LOGGER.error("微信退款处理异常，订单号：{}", returnApply.getOrderSn(), e);
            // 返回失败的退款结果
            WechatRefundResult errorResult = new WechatRefundResult();
            errorResult.setSuccess(false);
            errorResult.setErrorMsg("退款处理异常：" + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 检查并更新订单状态（退款成功后）
     * 如果订单的所有退款金额达到订单支付金额，则将订单状态更新为已关闭
     *
     * @param orderId 订单ID
     */
    private void checkAndUpdateOrderStatusAfterRefund(Long orderId) {
        try {
            // 1. 查询订单信息
            OmsOrder order = omsOrderMapper.selectByPrimaryKey(orderId);
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

            LOGGER.info("订单退款检查，订单ID: {}, 订单支付金额: {}, 总退款金额: {}",
                orderId, orderPayAmount, totalRefundAmount);

            // 5. 如果总退款金额 >= 订单支付金额，更新订单状态为已关闭
            if (totalRefundAmount.compareTo(orderPayAmount) >= 0) {
                OmsOrder updateOrder = new OmsOrder();
                updateOrder.setId(orderId);
                updateOrder.setStatus(4); // 已关闭
                updateOrder.setModifyTime(new Date());

                int updateResult = omsOrderMapper.updateByPrimaryKeySelective(updateOrder);
                if (updateResult > 0) {
                    // 记录操作历史
                    OmsOrderOperateHistory history = new OmsOrderOperateHistory();
                    history.setOrderId(orderId);
                    history.setCreateTime(new Date());
                    history.setOperateMan("系统");
                    history.setOrderStatus(4);
                    history.setNote(String.format("全额退款完成，订单自动关闭（退款金额：¥%.2f）", totalRefundAmount));
                    orderOperateHistoryMapper.insert(history);

                    LOGGER.info("订单状态已更新为已关闭，订单ID: {}, 总退款金额: {}", orderId, totalRefundAmount);
                } else {
                    LOGGER.error("更新订单状态失败，订单ID: {}", orderId);
                }
            } else {
                LOGGER.info("部分退款，订单状态保持不变，订单ID: {}, 剩余金额: {}",
                    orderId, orderPayAmount.subtract(totalRefundAmount));
            }

        } catch (Exception e) {
            LOGGER.error("检查并更新订单状态异常，订单ID: {}", orderId, e);
        }
    }

    /**
     * 统一的退款结果类
     */
    private static class RefundResult {
        private boolean success;
        private String refundSn;
        private String refundMethod;
        private String errorMsg;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getRefundSn() {
            return refundSn;
        }

        public void setRefundSn(String refundSn) {
            this.refundSn = refundSn;
        }

        public String getRefundMethod() {
            return refundMethod;
        }

        public void setRefundMethod(String refundMethod) {
            this.refundMethod = refundMethod;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public void setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
        }
    }

    @Override
    public Integer getPendingReturnApplyCount() {
        return returnApplyDao.getPendingReturnApplyCount();
    }

    @Override
    public List<OmsOrderReturnApplyResult> getReturnApplyByOrderId(Long orderId) {
        return returnApplyDao.getReturnApplyByOrderId(orderId);
    }
}
