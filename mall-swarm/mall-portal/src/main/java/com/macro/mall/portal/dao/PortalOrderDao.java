package com.macro.mall.portal.dao;

import com.macro.mall.model.OmsOrderItem;
import com.macro.mall.portal.domain.OmsOrderDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 前台订单自定义Dao
 * Created by macro on 2018/9/4.
 */
public interface PortalOrderDao {
    /**
     * 获取订单及下单商品详情
     */
    OmsOrderDetail getDetail(@Param("orderId") Long orderId);

    /**
     * 修改 pms_sku_stock表的锁定库存及真实库存
     */
    int updateSkuStock(@Param("itemList") List<OmsOrderItem> orderItemList);

    /**
     * 修改 pms_store_sku_stock表的锁定库存及真实库存
     */
    int updateStoreSkuStock(@Param("itemList") List<OmsOrderItem> orderItemList, @Param("storeId") Long storeId);

    /**
     * 同时更新总库存和门店库存（支付成功时使用）
     */
    int updateBothSkuStock(@Param("itemList") List<OmsOrderItem> orderItemList, @Param("storeId") Long storeId);

    /**
     * 更新商品销量
     */
    int updateProductSale(@Param("itemList") List<OmsOrderItem> orderItemList);

    // 注意：以下复合方法已移除，改为在Service层分别调用基础方法：
    // - updateBothSkuStockAndProductSale -> 分别调用 updateSkuStock + updateStoreSkuStock + updateProductSale
    // - updateSkuStockAndProductSale -> 分别调用 updateSkuStock + updateProductSale  
    // - releaseBothSkuStockLock -> 分别调用 releaseSkuStockLock + releaseStoreSkuStockLock

    /**
     * 获取超时订单
     * @param minute 超时时间（分）
     */
    List<OmsOrderDetail> getTimeOutOrders(@Param("minute") Integer minute);

    /**
     * 批量修改订单状态
     */
    int updateOrderStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);

    /**
     * 解除下单商品的库存锁定
     */
    int releaseSkuStockLock(@Param("itemList") List<OmsOrderItem> orderItemList);

    /**
     * 解除下单商品的门店库存锁定
     */
    int releaseStoreSkuStockLock(@Param("itemList") List<OmsOrderItem> orderItemList, @Param("storeId") Long storeId);
}
