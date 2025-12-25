package com.macro.mall.dao;

import com.macro.mall.dto.OmsOrderDeliveryParam;
import com.macro.mall.dto.OmsOrderDetail;
import com.macro.mall.dto.OmsOrderQueryParam;
import com.macro.mall.dto.OmsOrderWithItems;
import com.macro.mall.dto.OmsStockDeductionDetail;
import com.macro.mall.model.OmsOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单自定义查询Dao
 * Created by macro on 2018/10/12.
 */
public interface OmsOrderDao {
    /**
     * 条件查询订单
     */
    List<OmsOrder> getList(@Param("queryParam") OmsOrderQueryParam queryParam);

    /**
     * 条件查询订单（包含商品信息）
     */
    List<OmsOrderWithItems> getListWithItems(@Param("queryParam") OmsOrderQueryParam queryParam);

    /**
     * 批量发货
     */
    int delivery(@Param("list") List<OmsOrderDeliveryParam> deliveryParamList);

    /**
     * 获取订单详情
     */
    OmsOrderDetail getDetail(@Param("id") Long id);

    /**
     * 统计订单数量
     */
    Long count(@Param("queryParam") OmsOrderQueryParam queryParam);

    /**
     * 根据订单号获取库存扣除详情
     */
    List<OmsStockDeductionDetail> getStockDeductionByOrderSn(@Param("orderSn") String orderSn);
}
