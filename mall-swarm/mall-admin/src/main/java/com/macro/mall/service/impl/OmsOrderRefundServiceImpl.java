package com.macro.mall.service.impl;

import com.macro.mall.dto.OmsOrderRefundParam;
import com.macro.mall.dto.OmsOrderRefundResult;
import com.macro.mall.dto.WechatRefundParam;
import com.macro.mall.dto.WechatRefundResult;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.mapper.OmsOrderItemMapper;
import com.macro.mall.mapper.OmsOrderOperateHistoryMapper;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.mapper.UmsMemberBalanceHistoryMapper;
import com.macro.mall.mapper.PmsSkuStockMapper;
import com.macro.mall.mapper.PmsStoreSkuStockMapper;
import com.macro.mall.mapper.PmsStockOperationLogMapper;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.model.*;
import com.macro.mall.service.OmsOrderRefundService;
import com.macro.mall.service.WechatRefundService;
import com.macro.mall.service.AlipayRefundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 订单退款服务实现
 * 支持自助订单的微信支付、支付宝支付、余额支付退款
 * Created by macro on 2024/01/01.
 */
@Service
public class OmsOrderRefundServiceImpl implements OmsOrderRefundService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(OmsOrderRefundServiceImpl.class);
    
    @Autowired
    private OmsOrderMapper orderMapper;
    
    @Autowired
    private OmsOrderItemMapper orderItemMapper;
    
    @Autowired
    private OmsOrderOperateHistoryMapper orderOperateHistoryMapper;
    
    @Autowired
    private UmsMemberMapper memberMapper;
    
    @Autowired
    private UmsMemberBalanceHistoryMapper memberBalanceHistoryMapper;
    
    @Autowired
    private PmsSkuStockMapper skuStockMapper;
    
    @Autowired
    private PmsStoreSkuStockMapper storeSkuStockMapper;
    
    @Autowired
    private PmsStockOperationLogMapper stockOperationLogMapper;
    
    @Autowired
    private PmsProductMapper productMapper;
    
    @Autowired(required = false)
    private WechatRefundService wechatRefundService;
    
    @Autowired(required = false)
    private AlipayRefundService alipayRefundService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OmsOrderRefundResult processRefund(OmsOrderRefundParam refundParam) {
        OmsOrderRefundResult result = new OmsOrderRefundResult();
        
        try {
            // 1. 获取订单信息
            OmsOrder order = orderMapper.selectByPrimaryKey(refundParam.getOrderId());
            if (order == null) {
                result.setSuccess(false);
                result.setErrorMsg("订单不存在");
                return result;
            }
            
            result.setOrderId(order.getId());
            result.setOrderSn(order.getOrderSn());
            
            // 2. 验证订单状态（只有已完成状态的自助订单可以退款）
            if (order.getStatus() != 3) {
                result.setSuccess(false);
                result.setErrorMsg("只有已完成状态的订单可以退款");
                return result;
            }
            
            // 3. 验证是否为自助订单（sourceType=2）
            if (order.getSourceType() == null || order.getSourceType() != 2) {
                result.setSuccess(false);
                result.setErrorMsg("只有自助设备订单可以使用此退款功能");
                return result;
            }
            
            // 4. 验证退款金额
            BigDecimal refundAmount = refundParam.getRefundAmount();
            if (refundAmount == null || refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
                result.setSuccess(false);
                result.setErrorMsg("退款金额必须大于0");
                return result;
            }
            
            if (refundAmount.compareTo(order.getPayAmount()) > 0) {
                result.setSuccess(false);
                result.setErrorMsg("退款金额不能超过订单支付金额");
                return result;
            }
            
            // 5. 检查订单是否已经退款（通过订单状态判断）
            if (order.getStatus() == 4) {
                result.setSuccess(false);
                result.setErrorMsg("该订单已关闭，不能重复退款");
                return result;
            }
            
            // 6. 根据支付方式进行退款
            Integer payType = order.getPayType();
            if (payType == null) {
                result.setSuccess(false);
                result.setErrorMsg("订单支付方式为空，无法退款");
                return result;
            }
            
            LOGGER.info("开始处理订单退款，订单号：{}，支付方式：{}，退款金额：{}", 
                order.getOrderSn(), payType, refundAmount);
            
            switch (payType) {
                case 2: // 微信支付
                    processWechatRefund(order, refundParam, result);
                    break;
                case 1: // 支付宝支付
                    processAlipayRefund(order, refundParam, result);
                    break;
                case 3: // 余额支付
                    processBalanceRefund(order, refundParam, result);
                    break;
                default:
                    result.setSuccess(false);
                    result.setErrorMsg("不支持的支付方式：" + payType);
                    return result;
            }
            
            // 7. 如果退款成功，更新订单状态和记录操作历史
            if (result.isSuccess()) {
                // 回滚库存（门店库存和总库存）
                rollbackStock(order, refundParam);
                
                // 更新订单状态为已关闭
                updateOrderStatus(order, refundParam, result);
                
                // 记录操作历史
                saveOperateHistory(order, refundParam, result);
                
                LOGGER.info("订单退款成功，订单号：{}，退款单号：{}，退款金额：{}", 
                    order.getOrderSn(), result.getRefundSn(), result.getRefundAmount());
            }
            
            return result;
            
        } catch (Exception e) {
            LOGGER.error("订单退款异常，订单ID：{}", refundParam.getOrderId(), e);
            result.setSuccess(false);
            result.setErrorMsg("退款处理异常：" + e.getMessage());
            return result;
        }
    }

    /**
     * 处理微信退款
     */
    private void processWechatRefund(OmsOrder order, OmsOrderRefundParam refundParam, OmsOrderRefundResult result) {
        try {
            if (wechatRefundService == null) {
                result.setSuccess(false);
                result.setErrorMsg("微信退款服务未启用");
                return;
            }
            
            // 构建微信退款参数
            WechatRefundParam wechatRefundParam = new WechatRefundParam();
            wechatRefundParam.setOutTradeNo(order.getOrderSn());
            wechatRefundParam.setOutRefundNo("RF" + System.currentTimeMillis());
            wechatRefundParam.setRefundFee((int)(refundParam.getRefundAmount().doubleValue() * 100));
            wechatRefundParam.setTotalFee((int)(order.getPayAmount().doubleValue() * 100));
            wechatRefundParam.setRefundDesc(refundParam.getRefundReason() != null ? 
                refundParam.getRefundReason() : "自助订单退款");
            
            // 调用微信退款
            WechatRefundResult wechatResult = wechatRefundService.processRefund(wechatRefundParam);
            
            if (wechatResult.isSuccess()) {
                result.setSuccess(true);
                result.setRefundSn(wechatRefundParam.getOutRefundNo());
                result.setRefundAmount(refundParam.getRefundAmount());
                result.setRefundMethod("微信退款");
                result.setRefundStatus("PROCESSING");
                result.setRefundTime(new Date());
            } else {
                result.setSuccess(false);
                result.setErrorMsg(wechatResult.getErrorMsg());
            }
            
        } catch (Exception e) {
            LOGGER.error("微信退款异常，订单号：{}", order.getOrderSn(), e);
            result.setSuccess(false);
            result.setErrorMsg("微信退款异常：" + e.getMessage());
        }
    }
    
    /**
     * 处理支付宝退款
     * 兼容新旧订单：先用订单号退款，失败则尝试用note中的支付宝交易号退款
     */
    private void processAlipayRefund(OmsOrder order, OmsOrderRefundParam refundParam, OmsOrderRefundResult result) {
        try {
            if (alipayRefundService == null) {
                result.setSuccess(false);
                result.setErrorMsg("支付宝退款服务未启用");
                return;
            }
            
            String refundSn = "AF" + System.currentTimeMillis();
            String refundReason = refundParam.getRefundReason() != null ? refundParam.getRefundReason() : "自助订单退款";
            
            // 1. 先尝试使用订单号作为商户订单号进行退款（新订单）
            LOGGER.info("尝试使用订单号进行支付宝退款，订单号：{}", order.getOrderSn());
            boolean refundSuccess = alipayRefundService.processRefund(
                order.getOrderSn(),
                refundSn,
                refundParam.getRefundAmount(),
                refundReason
            );
            
            // 2. 如果订单号退款失败，尝试从note中提取支付宝交易号进行退款（旧订单兼容）
            if (!refundSuccess) {
                String tradeNo = extractTransactionId(order.getNote());
                if (tradeNo != null && !tradeNo.isEmpty()) {
                    LOGGER.info("订单号退款失败，尝试使用支付宝交易号退款，交易号：{}", tradeNo);
                    refundSn = "AF" + System.currentTimeMillis(); // 重新生成退款单号
                    refundSuccess = alipayRefundService.processRefundByTradeNo(
                        tradeNo,
                        refundSn,
                        refundParam.getRefundAmount(),
                        refundReason
                    );
                }
            }
            
            if (refundSuccess) {
                result.setSuccess(true);
                result.setRefundSn(refundSn);
                result.setRefundAmount(refundParam.getRefundAmount());
                result.setRefundMethod("支付宝退款");
                result.setRefundStatus("SUCCESS");
                result.setRefundTime(new Date());
            } else {
                result.setSuccess(false);
                result.setErrorMsg("支付宝退款失败，请检查订单支付状态");
            }
            
        } catch (Exception e) {
            LOGGER.error("支付宝退款异常，订单号：{}", order.getOrderSn(), e);
            result.setSuccess(false);
            result.setErrorMsg("支付宝退款异常：" + e.getMessage());
        }
    }
    
    /**
     * 从订单备注中提取支付流水号（支付宝交易号）
     * 格式：；支付流水号：xxx
     */
    private String extractTransactionId(String note) {
        if (note == null || note.isEmpty()) {
            return null;
        }
        String prefix = "支付流水号：";
        int startIndex = note.indexOf(prefix);
        if (startIndex == -1) {
            return null;
        }
        startIndex += prefix.length();
        int endIndex = note.indexOf("；", startIndex);
        if (endIndex == -1) {
            endIndex = note.length();
        }
        return note.substring(startIndex, endIndex).trim();
    }
    
    /**
     * 处理余额退款
     */
    @Transactional(rollbackFor = Exception.class)
    private void processBalanceRefund(OmsOrder order, OmsOrderRefundParam refundParam, OmsOrderRefundResult result) {
        try {
            // 获取用户信息
            UmsMember member = memberMapper.selectByPrimaryKey(order.getMemberId());
            if (member == null) {
                result.setSuccess(false);
                result.setErrorMsg("用户不存在");
                return;
            }
            
            // 计算退款后余额
            BigDecimal currentBalance = member.getBalance() != null ? member.getBalance() : BigDecimal.ZERO;
            BigDecimal newBalance = currentBalance.add(refundParam.getRefundAmount());
            
            // 更新用户余额
            UmsMember updateMember = new UmsMember();
            updateMember.setId(order.getMemberId());
            updateMember.setBalance(newBalance);
            int updateCount = memberMapper.updateByPrimaryKeySelective(updateMember);
            
            if (updateCount <= 0) {
                result.setSuccess(false);
                result.setErrorMsg("更新用户余额失败");
                return;
            }
            
            // 记录余额变动历史
            UmsMemberBalanceHistory balanceHistory = new UmsMemberBalanceHistory();
            balanceHistory.setMemberId(order.getMemberId());
            balanceHistory.setChangeType((byte) 3); // 退款
            balanceHistory.setAmount(refundParam.getRefundAmount());
            balanceHistory.setBalanceBefore(currentBalance);
            balanceHistory.setBalanceAfter(newBalance);
            balanceHistory.setBusinessType("ORDER_REFUND");
            balanceHistory.setBusinessId(order.getOrderSn());
            balanceHistory.setRemark("自助订单退款：" + (refundParam.getRefundReason() != null ? 
                refundParam.getRefundReason() : "订单退款"));
            balanceHistory.setOperator(refundParam.getOperator() != null ? 
                refundParam.getOperator() : "SYSTEM");
            balanceHistory.setCreateTime(new Date());
            memberBalanceHistoryMapper.insertSelective(balanceHistory);
            
            // 设置退款结果
            result.setSuccess(true);
            result.setRefundSn("BF" + System.currentTimeMillis());
            result.setRefundAmount(refundParam.getRefundAmount());
            result.setRefundMethod("余额退款");
            result.setRefundStatus("SUCCESS");
            result.setRefundTime(new Date());
            
            LOGGER.info("余额退款成功，订单号：{}，用户ID：{}，退款金额：{}，余额变化：{} -> {}",
                order.getOrderSn(), order.getMemberId(), refundParam.getRefundAmount(), 
                currentBalance, newBalance);
            
        } catch (Exception e) {
            LOGGER.error("余额退款异常，订单号：{}", order.getOrderSn(), e);
            result.setSuccess(false);
            result.setErrorMsg("余额退款异常：" + e.getMessage());
        }
    }

    /**
     * 更新订单状态
     */
    private void updateOrderStatus(OmsOrder order, OmsOrderRefundParam refundParam, OmsOrderRefundResult result) {
        // 全额退款时关闭订单
        if (result.getRefundAmount().compareTo(order.getPayAmount()) >= 0) {
            OmsOrder updateOrder = new OmsOrder();
            updateOrder.setId(order.getId());
            updateOrder.setStatus(4); // 已关闭
            updateOrder.setModifyTime(new Date());
            orderMapper.updateByPrimaryKeySelective(updateOrder);
        }
    }

    /**
     * 保存操作历史
     */
    private void saveOperateHistory(OmsOrder order, OmsOrderRefundParam refundParam, OmsOrderRefundResult result) {
        OmsOrderOperateHistory history = new OmsOrderOperateHistory();
        history.setOrderId(order.getId());
        history.setCreateTime(new Date());
        history.setOperateMan(refundParam.getOperator() != null ? refundParam.getOperator() : "系统");
        history.setOrderStatus(4);
        history.setNote(String.format("自助订单退款：¥%.2f，退款方式：%s，退款单号：%s，退款原因：%s",
            result.getRefundAmount(),
            result.getRefundMethod(),
            result.getRefundSn(),
            refundParam.getRefundReason() != null ? refundParam.getRefundReason() : "订单退款"));
        orderOperateHistoryMapper.insert(history);
    }

    @Override
    public OmsOrderRefundResult queryRefundStatus(Long orderId) {
        OmsOrderRefundResult result = new OmsOrderRefundResult();
        
        // 查询订单
        OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            result.setSuccess(false);
            result.setErrorMsg("订单不存在");
            return result;
        }
        
        result.setOrderId(order.getId());
        result.setOrderSn(order.getOrderSn());
        result.setSuccess(true);
        
        // 根据订单状态判断退款状态
        if (order.getStatus() == 4) {
            result.setRefundStatus("SUCCESS");
        } else {
            result.setRefundStatus("NONE");
        }
        
        return result;
    }

    @Override
    public boolean canRefund(Long orderId) {
        OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            return false;
        }
        
        // 只有已完成状态的自助订单可以退款
        if (order.getStatus() != 3) {
            return false;
        }
        
        // 验证是否为自助订单
        if (order.getSourceType() == null || order.getSourceType() != 2) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 回滚订单商品库存（门店库存和总库存）
     * 退款成功后将商品库存加回去
     */
    private void rollbackStock(OmsOrder order, OmsOrderRefundParam refundParam) {
        try {
            // 1. 获取订单商品列表
            OmsOrderItemExample itemExample = new OmsOrderItemExample();
            itemExample.createCriteria().andOrderIdEqualTo(order.getId());
            List<OmsOrderItem> orderItems = orderItemMapper.selectByExample(itemExample);
            
            if (CollectionUtils.isEmpty(orderItems)) {
                LOGGER.warn("订单商品列表为空，跳过库存回滚，订单号：{}", order.getOrderSn());
                return;
            }
            
            Long storeId = order.getStoreId();
            String operatorName = refundParam.getOperator() != null ? refundParam.getOperator() : "系统退款";
            
            LOGGER.info("开始回滚订单库存，订单号：{}，门店ID：{}，商品数量：{}", 
                order.getOrderSn(), storeId, orderItems.size());
            
            // 2. 遍历订单商品，回滚库存
            for (OmsOrderItem item : orderItems) {
                Long productId = item.getProductId();
                Long skuId = item.getProductSkuId();
                Integer quantity = item.getProductQuantity();
                
                if (skuId == null || quantity == null || quantity <= 0) {
                    LOGGER.warn("订单商品信息不完整，跳过库存回滚，商品ID：{}，SKU ID：{}，数量：{}", 
                        productId, skuId, quantity);
                    continue;
                }
                
                // 回滚总库存
                rollbackTotalStock(order, item, operatorName);
                
                // 如果有门店ID，回滚门店库存
                if (storeId != null) {
                    rollbackStoreStock(order, storeId, item, operatorName);
                }
            }
            
            LOGGER.info("订单库存回滚完成，订单号：{}", order.getOrderSn());
            
        } catch (Exception e) {
            // 库存回滚失败不影响退款流程，但记录错误日志
            LOGGER.error("订单库存回滚失败，订单号：{}，错误：{}", order.getOrderSn(), e.getMessage(), e);
        }
    }
    
    /**
     * 回滚总库存
     */
    private void rollbackTotalStock(OmsOrder order, OmsOrderItem item, String operatorName) {
        try {
            Long skuId = item.getProductSkuId();
            Integer quantity = item.getProductQuantity();
            
            // 获取当前SKU库存
            PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(skuId);
            if (skuStock == null) {
                LOGGER.warn("SKU库存记录不存在，跳过总库存回滚，SKU ID：{}", skuId);
                return;
            }
            
            Integer currentStock = skuStock.getStock() != null ? skuStock.getStock() : 0;
            Integer newStock = currentStock + quantity;
            Integer currentSale = skuStock.getSale() != null ? skuStock.getSale() : 0;
            Integer newSale = Math.max(0, currentSale - quantity);
            
            // 更新库存
            PmsSkuStock updateRecord = new PmsSkuStock();
            updateRecord.setId(skuId);
            updateRecord.setStock(newStock);
            updateRecord.setSale(newSale);
            
            int updateResult = skuStockMapper.updateByPrimaryKeySelective(updateRecord);
            if (updateResult > 0) {
                LOGGER.info("总库存回滚成功，SKU ID：{}，回滚数量：{}，库存变化：{} -> {}，销量变化：{} -> {}", 
                    skuId, quantity, currentStock, newStock, currentSale, newSale);
                
                // 记录库存操作日志
                recordStockOperationLog(order, item, null, quantity, currentStock, newStock, 
                    operatorName, "退款入库-总库存", "TOTAL");
            } else {
                LOGGER.error("总库存回滚失败，SKU ID：{}", skuId);
            }
            
        } catch (Exception e) {
            LOGGER.error("总库存回滚异常，SKU ID：{}，错误：{}", item.getProductSkuId(), e.getMessage(), e);
        }
    }
    
    /**
     * 回滚门店库存
     */
    private void rollbackStoreStock(OmsOrder order, Long storeId, OmsOrderItem item, String operatorName) {
        try {
            Long productId = item.getProductId();
            Long skuId = item.getProductSkuId();
            Integer quantity = item.getProductQuantity();
            
            // 查询门店库存记录
            PmsStoreSkuStockExample example = new PmsStoreSkuStockExample();
            example.createCriteria()
                .andStoreIdEqualTo(storeId)
                .andProductIdEqualTo(productId)
                .andSkuIdEqualTo(skuId);
            
            List<PmsStoreSkuStock> storeStocks = storeSkuStockMapper.selectByExample(example);
            if (CollectionUtils.isEmpty(storeStocks)) {
                LOGGER.warn("门店库存记录不存在，跳过门店库存回滚，门店ID：{}，SKU ID：{}", storeId, skuId);
                return;
            }
            
            PmsStoreSkuStock storeStock = storeStocks.get(0);
            Integer currentStock = storeStock.getStock() != null ? storeStock.getStock() : 0;
            Integer newStock = currentStock + quantity;
            Integer currentSaleCount = storeStock.getSaleCount() != null ? storeStock.getSaleCount() : 0;
            Integer newSaleCount = Math.max(0, currentSaleCount - quantity);
            
            // 更新门店库存
            PmsStoreSkuStock updateRecord = new PmsStoreSkuStock();
            updateRecord.setId(storeStock.getId());
            updateRecord.setStock(newStock);
            updateRecord.setSaleCount(newSaleCount);
            updateRecord.setUpdateTime(new Date());
            
            int updateResult = storeSkuStockMapper.updateByPrimaryKeySelective(updateRecord);
            if (updateResult > 0) {
                LOGGER.info("门店库存回滚成功，门店ID：{}，SKU ID：{}，回滚数量：{}，库存变化：{} -> {}，销量变化：{} -> {}", 
                    storeId, skuId, quantity, currentStock, newStock, currentSaleCount, newSaleCount);
                
                // 记录库存操作日志
                recordStockOperationLog(order, item, storeId, quantity, currentStock, newStock, 
                    operatorName, "退款入库-门店库存", "STORE");
            } else {
                LOGGER.error("门店库存回滚失败，门店ID：{}，SKU ID：{}", storeId, skuId);
            }
            
        } catch (Exception e) {
            LOGGER.error("门店库存回滚异常，门店ID：{}，SKU ID：{}，错误：{}", 
                storeId, item.getProductSkuId(), e.getMessage(), e);
        }
    }
    
    /**
     * 记录库存操作日志
     */
    private void recordStockOperationLog(OmsOrder order, OmsOrderItem item, Long storeId,
                                        Integer quantity, Integer beforeStock, Integer afterStock,
                                        String operatorName, String reason, String stockType) {
        try {
            // 获取商品信息
            PmsProduct product = productMapper.selectByPrimaryKey(item.getProductId());
            PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(item.getProductSkuId());
            
            String operationNo = generateOperationNo("REFUND_IN");
            
            PmsStockOperationLog log = new PmsStockOperationLog();
            log.setOperationNo(operationNo);
            log.setOperationType((byte) 1); // 1-入库
            log.setOperationSubtype((byte) 4); // 4-退款入库
            log.setProductId(item.getProductId());
            log.setProductName(product != null ? product.getName() : item.getProductName());
            log.setProductSn(product != null ? product.getProductSn() : item.getProductSn());
            log.setSkuId(item.getProductSkuId());
            log.setSkuCode(skuStock != null ? skuStock.getSkuCode() : item.getProductSkuCode());
            log.setStoreId(storeId);
            log.setOrderId(order.getId());
            log.setOrderSn(order.getOrderSn());
            log.setBeforeStock(beforeStock);
            log.setOperationQuantity(quantity); // 正数表示入库
            log.setAfterStock(afterStock);
            log.setOperationReason(reason + "（订单号：" + order.getOrderSn() + "）");
            log.setOperatorName(operatorName);
            log.setCreatedAt(new Date());
            
            stockOperationLogMapper.insert(log);
            
        } catch (Exception e) {
            // 记录日志失败不影响主流程
            LOGGER.error("记录库存操作日志失败，订单号：{}，错误：{}", order.getOrderSn(), e.getMessage());
        }
    }
    
    /**
     * 生成操作单号
     */
    private String generateOperationNo(String operationType) {
        String dateStr = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return operationType + "_" + dateStr + "_" + System.currentTimeMillis() % 1000;
    }
}
