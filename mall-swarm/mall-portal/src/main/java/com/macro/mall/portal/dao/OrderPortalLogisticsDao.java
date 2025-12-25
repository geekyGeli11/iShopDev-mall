package com.macro.mall.portal.dao;

import com.macro.mall.portal.dto.OrderPortalLogistics;

public interface OrderPortalLogisticsDao {
    /**
     * 查询订单快递信息
     */
    OrderPortalLogistics getOrderLogisticsByOrderId(Long orderId);
}
