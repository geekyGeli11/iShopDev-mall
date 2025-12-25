package com.macro.mall.selfcheck.service;

import com.macro.mall.model.OmsOrderItem;

import java.util.List;

/**
 * 自助结算库存管理服务接口
 */
public interface SelfcheckStockService {

    /**
     * 检查门店库存是否充足
     * @param storeId 门店ID
     * @param productId 商品ID
     * @param skuId SKU ID
     * @param quantity 需要的数量
     * @return 是否充足
     */
    boolean checkStoreStockAvailable(Long storeId, Long productId, Long skuId, Integer quantity);

    /**
     * 检查总库存是否充足
     * @param productId 商品ID
     * @param skuId SKU ID
     * @param quantity 需要的数量
     * @return 是否充足
     */
    boolean checkTotalStockAvailable(Long productId, Long skuId, Integer quantity);

    /**
     * 扣减门店库存
     * @param storeId 门店ID
     * @param orderId 订单ID
     * @param orderSn 订单号
     * @param productId 商品ID
     * @param skuId SKU ID
     * @param quantity 扣减数量
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @return 扣减结果
     */
    StockDeductResult deductStoreStock(Long storeId, Long orderId, String orderSn, 
                                      Long productId, Long skuId, Integer quantity,
                                      Long operatorId, String operatorName);

    /**
     * 扣减总库存
     * @param orderId 订单ID
     * @param orderSn 订单号
     * @param productId 商品ID
     * @param skuId SKU ID
     * @param quantity 扣减数量
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @param reason 扣减原因
     * @return 扣减结果
     */
    StockDeductResult deductTotalStock(Long orderId, String orderSn, 
                                      Long productId, Long skuId, Integer quantity,
                                      Long operatorId, String operatorName, String reason);

    /**
     * 智能库存扣减（优先扣减门店库存，不足时扣减总库存）
     * @param storeId 门店ID
     * @param orderId 订单ID
     * @param orderSn 订单号
     * @param productId 商品ID
     * @param skuId SKU ID
     * @param quantity 扣减数量
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @return 扣减结果
     */
    StockDeductResult smartDeductStock(Long storeId, Long orderId, String orderSn, 
                                      Long productId, Long skuId, Integer quantity,
                                      Long operatorId, String operatorName);

    /**
     * 批量智能库存扣减（处理订单中的所有商品）
     * @param storeId 门店ID
     * @param orderId 订单ID
     * @param orderSn 订单号
     * @param orderItems 订单商品列表
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @return 扣减结果列表
     */
    List<StockDeductResult> batchSmartDeductStock(Long storeId, Long orderId, String orderSn,
                                                 List<OmsOrderItem> orderItems,
                                                 Long operatorId, String operatorName);

    /**
     * 获取门店库存数量
     * @param storeId 门店ID
     * @param productId 商品ID
     * @param skuId SKU ID
     * @return 库存数量
     */
    Integer getStoreStockQuantity(Long storeId, Long productId, Long skuId);

    /**
     * 获取总库存数量
     * @param productId 商品ID
     * @param skuId SKU ID
     * @return 库存数量
     */
    Integer getTotalStockQuantity(Long productId, Long skuId);

    /**
     * 库存扣减结果
     */
    class StockDeductResult {
        private boolean success;
        private String message;
        private String operationNo;
        private Integer beforeStock;
        private Integer afterStock;
        private String stockType; // STORE: 门店库存, TOTAL: 总库存
        private String reason;

        public StockDeductResult() {}

        public StockDeductResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public static StockDeductResult success(String operationNo, Integer beforeStock, Integer afterStock, String stockType) {
            StockDeductResult result = new StockDeductResult(true, "库存扣减成功");
            result.setOperationNo(operationNo);
            result.setBeforeStock(beforeStock);
            result.setAfterStock(afterStock);
            result.setStockType(stockType);
            return result;
        }

        public static StockDeductResult failure(String message) {
            return new StockDeductResult(false, message);
        }

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getOperationNo() { return operationNo; }
        public void setOperationNo(String operationNo) { this.operationNo = operationNo; }
        public Integer getBeforeStock() { return beforeStock; }
        public void setBeforeStock(Integer beforeStock) { this.beforeStock = beforeStock; }
        public Integer getAfterStock() { return afterStock; }
        public void setAfterStock(Integer afterStock) { this.afterStock = afterStock; }
        public String getStockType() { return stockType; }
        public void setStockType(String stockType) { this.stockType = stockType; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}
