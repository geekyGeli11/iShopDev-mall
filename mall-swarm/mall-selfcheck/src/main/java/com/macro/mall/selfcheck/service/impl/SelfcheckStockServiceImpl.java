package com.macro.mall.selfcheck.service.impl;

import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.selfcheck.service.SelfcheckStockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 自助结算库存管理服务实现类
 */
@Slf4j
@Service
public class SelfcheckStockServiceImpl implements SelfcheckStockService {

    @Autowired
    private PmsStoreSkuStockMapper storeSkuStockMapper;

    @Autowired
    private PmsSkuStockMapper skuStockMapper;

    @Autowired
    private PmsProductMapper productMapper;

    @Autowired
    private PmsStockOperationLogMapper stockOperationLogMapper;

    @Override
    public boolean checkStoreStockAvailable(Long storeId, Long productId, Long skuId, Integer quantity) {
        if (storeId == null || productId == null || skuId == null || quantity == null || quantity <= 0) {
            return false;
        }

        PmsStoreSkuStockExample example = new PmsStoreSkuStockExample();
        example.createCriteria()
                .andStoreIdEqualTo(storeId)
                .andProductIdEqualTo(productId)
                .andSkuIdEqualTo(skuId)
                .andStatusEqualTo(1);

        List<PmsStoreSkuStock> storeStocks = storeSkuStockMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(storeStocks)) {
            return false;
        }

        PmsStoreSkuStock storeStock = storeStocks.get(0);
        return storeStock.getStock() != null && storeStock.getStock() >= quantity;
    }

    @Override
    public boolean checkTotalStockAvailable(Long productId, Long skuId, Integer quantity) {
        if (productId == null || skuId == null || quantity == null || quantity <= 0) {
            return false;
        }

        PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(skuId);
        return skuStock != null && skuStock.getStock() != null && skuStock.getStock() >= quantity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StockDeductResult deductStoreStock(Long storeId, Long orderId, String orderSn, 
                                             Long productId, Long skuId, Integer quantity,
                                             Long operatorId, String operatorName) {
        try {
            // 1. 获取门店库存记录
            PmsStoreSkuStockExample example = new PmsStoreSkuStockExample();
            example.createCriteria()
                    .andStoreIdEqualTo(storeId)
                    .andProductIdEqualTo(productId)
                    .andSkuIdEqualTo(skuId)
                    .andStatusEqualTo(1);

            List<PmsStoreSkuStock> storeStocks = storeSkuStockMapper.selectByExample(example);
            if (CollectionUtils.isEmpty(storeStocks)) {
                return StockDeductResult.failure("门店库存记录不存在");
            }

            PmsStoreSkuStock storeStock = storeStocks.get(0);
            Integer currentStock = storeStock.getStock() != null ? storeStock.getStock() : 0;

            // 2. 检查库存是否充足
            if (currentStock < quantity) {
                return StockDeductResult.failure("门店库存不足，当前库存：" + currentStock + "，需要：" + quantity);
            }

            // 3. 扣减库存
            Integer newStock = currentStock - quantity;
            PmsStoreSkuStock updateRecord = new PmsStoreSkuStock();
            updateRecord.setId(storeStock.getId());
            updateRecord.setStock(newStock);
            updateRecord.setSaleCount((storeStock.getSaleCount() != null ? storeStock.getSaleCount() : 0) + quantity);
            updateRecord.setUpdateTime(new Date());

            int updateResult = storeSkuStockMapper.updateByPrimaryKeySelective(updateRecord);
            if (updateResult <= 0) {
                return StockDeductResult.failure("门店库存扣减失败");
            }

            // 4. 生成操作单号并记录操作日志
            String operationNo = generateOperationNo("STORE_OUT");
            recordStockOperation(operationNo, storeId, orderId, orderSn, productId, skuId, 
                               quantity, currentStock, newStock, operatorId, operatorName, 
                               "订单销售出库", "STORE");

            log.info("门店库存扣减成功，门店ID：{}，SKU ID：{}，扣减数量：{}，剩余库存：{}", 
                    storeId, skuId, quantity, newStock);

            return StockDeductResult.success(operationNo, currentStock, newStock, "STORE");

        } catch (Exception e) {
            log.error("门店库存扣减失败", e);
            return StockDeductResult.failure("门店库存扣减失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StockDeductResult deductTotalStock(Long orderId, String orderSn, 
                                             Long productId, Long skuId, Integer quantity,
                                             Long operatorId, String operatorName, String reason) {
        try {
            // 1. 获取总库存记录
            PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(skuId);
            if (skuStock == null) {
                return StockDeductResult.failure("SKU库存记录不存在");
            }

            Integer currentStock = skuStock.getStock() != null ? skuStock.getStock() : 0;

            // 2. 检查库存是否充足
            if (currentStock < quantity) {
                return StockDeductResult.failure("总库存不足，当前库存：" + currentStock + "，需要：" + quantity);
            }

            // 3. 扣减库存
            Integer newStock = currentStock - quantity;
            PmsSkuStock updateRecord = new PmsSkuStock();
            updateRecord.setId(skuStock.getId());
            updateRecord.setStock(newStock);
            updateRecord.setSale(skuStock.getSale() != null ? skuStock.getSale() + quantity : quantity);

            int updateResult = skuStockMapper.updateByPrimaryKeySelective(updateRecord);
            if (updateResult <= 0) {
                return StockDeductResult.failure("总库存扣减失败");
            }

            // 4. 生成操作单号并记录操作日志
            String operationNo = generateOperationNo("TOTAL_OUT");
            recordStockOperation(operationNo, null, orderId, orderSn, productId, skuId, 
                               quantity, currentStock, newStock, operatorId, operatorName, 
                               reason, "TOTAL");

            log.info("总库存扣减成功，SKU ID：{}，扣减数量：{}，剩余库存：{}", 
                    skuId, quantity, newStock);

            return StockDeductResult.success(operationNo, currentStock, newStock, "TOTAL");

        } catch (Exception e) {
            log.error("总库存扣减失败", e);
            return StockDeductResult.failure("总库存扣减失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StockDeductResult smartDeductStock(Long storeId, Long orderId, String orderSn, 
                                             Long productId, Long skuId, Integer quantity,
                                             Long operatorId, String operatorName) {
        log.info("开始智能库存扣减，门店ID：{}，SKU ID：{}，数量：{}", storeId, skuId, quantity);

        // 1. 如果有门店ID，必须从门店库存扣减
        if (storeId != null) {
            // 检查门店库存是否充足
            if (!checkStoreStockAvailable(storeId, productId, skuId, quantity)) {
                Integer storeStock = getStoreStockQuantity(storeId, productId, skuId);
                log.error("门店库存不足，门店ID：{}，SKU ID：{}，当前库存：{}，需要：{}", 
                         storeId, skuId, storeStock, quantity);
                return StockDeductResult.failure("门店库存不足，当前库存：" + storeStock + "，需要：" + quantity);
            }
            
            // 扣减门店库存
            StockDeductResult storeResult = deductStoreStock(storeId, orderId, orderSn, productId, skuId, 
                                                           quantity, operatorId, operatorName);
            if (!storeResult.isSuccess()) {
                return storeResult;
            }
            
            // 同步扣减商品总库存
            String reason = "门店销售同步扣减总库存（订单号：" + orderSn + "，门店ID：" + storeId + "）";
            StockDeductResult totalResult = deductTotalStock(orderId, orderSn, productId, skuId, 
                                                            quantity, operatorId, operatorName, reason);
            if (!totalResult.isSuccess()) {
                log.warn("门店库存扣减成功但总库存扣减失败，门店ID：{}，SKU ID：{}，错误：{}", 
                        storeId, skuId, totalResult.getMessage());
                // 总库存扣减失败不影响订单，但记录警告
                storeResult.setReason("门店库存扣减成功，总库存同步扣减失败：" + totalResult.getMessage());
            } else {
                storeResult.setReason("门店库存和总库存同步扣减成功");
                log.info("门店库存和总库存同步扣减成功，门店ID：{}，SKU ID：{}", storeId, skuId);
            }
            
            return storeResult;
        }

        // 2. 没有门店ID，只扣减总库存
        log.info("无门店ID，直接扣减总库存，SKU ID：{}", skuId);
        String reason = "无门店信息，直接扣减总库存（订单号：" + orderSn + "）";
        StockDeductResult result = deductTotalStock(orderId, orderSn, productId, skuId, 
                                                   quantity, operatorId, operatorName, reason);
        
        if (result.isSuccess()) {
            log.info("总库存扣减成功，SKU ID：{}", skuId);
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<StockDeductResult> batchSmartDeductStock(Long storeId, Long orderId, String orderSn,
                                                        List<OmsOrderItem> orderItems,
                                                        Long operatorId, String operatorName) {
        List<StockDeductResult> results = new ArrayList<>();
        
        for (OmsOrderItem item : orderItems) {
            StockDeductResult result = smartDeductStock(storeId, orderId, orderSn, 
                                                       item.getProductId(), item.getProductSkuId(), 
                                                       item.getProductQuantity(), operatorId, operatorName);
            results.add(result);
            
            // 如果有任何一个商品扣减失败，记录错误但继续处理其他商品
            if (!result.isSuccess()) {
                log.error("商品库存扣减失败，商品ID：{}，SKU ID：{}，错误：{}", 
                         item.getProductId(), item.getProductSkuId(), result.getMessage());
            }
        }
        
        return results;
    }

    @Override
    public Integer getStoreStockQuantity(Long storeId, Long productId, Long skuId) {
        if (storeId == null || productId == null || skuId == null) {
            return 0;
        }

        PmsStoreSkuStockExample example = new PmsStoreSkuStockExample();
        example.createCriteria()
                .andStoreIdEqualTo(storeId)
                .andProductIdEqualTo(productId)
                .andSkuIdEqualTo(skuId)
                .andStatusEqualTo(1);

        List<PmsStoreSkuStock> storeStocks = storeSkuStockMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(storeStocks)) {
            return 0;
        }

        return storeStocks.get(0).getStock() != null ? storeStocks.get(0).getStock() : 0;
    }

    @Override
    public Integer getTotalStockQuantity(Long productId, Long skuId) {
        if (productId == null || skuId == null) {
            return 0;
        }

        PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(skuId);
        return skuStock != null && skuStock.getStock() != null ? skuStock.getStock() : 0;
    }

    /**
     * 生成操作单号
     */
    private String generateOperationNo(String operationType) {
        String dateStr = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return operationType + "_" + dateStr + "_" + System.currentTimeMillis() % 1000;
    }

    /**
     * 记录库存操作日志
     */
    private void recordStockOperation(String operationNo, Long storeId, Long orderId, String orderSn,
                                     Long productId, Long skuId, Integer quantity, 
                                     Integer beforeStock, Integer afterStock,
                                     Long operatorId, String operatorName, String reason, String stockType) {
        try {
            // 获取商品和SKU信息
            PmsProduct product = productMapper.selectByPrimaryKey(productId);
            PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(skuId);

            PmsStockOperationLog log = new PmsStockOperationLog();
            log.setOperationNo(operationNo);
            log.setOperationType((byte) 2); // 2-出库
            log.setOperationSubtype((byte) 1); // 1-销售出库
            log.setProductId(productId);
            log.setProductName(product != null ? product.getName() : "");
            log.setProductSn(product != null ? product.getProductSn() : "");
            log.setSkuId(skuId);
            log.setSkuCode(skuStock != null ? skuStock.getSkuCode() : "");
            log.setStoreId(storeId); // 门店ID字段
            log.setOrderId(orderId); // 关联订单ID
            log.setOrderSn(orderSn); // 关联订单编号
            log.setBeforeStock(beforeStock);
            log.setOperationQuantity(-quantity); // 负数表示出库
            log.setAfterStock(afterStock);
            log.setOperationReason(reason);
            log.setOperatorId(operatorId);
            log.setOperatorName(operatorName);
            log.setCreatedAt(new Date());

            stockOperationLogMapper.insert(log);

        } catch (Exception e) {
            // 记录日志失败不影响主流程
            log.error("记录库存操作日志失败", e);
        }
    }
}
