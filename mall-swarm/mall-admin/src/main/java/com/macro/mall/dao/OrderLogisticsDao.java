package com.macro.mall.dao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.macro.mall.dto.OrderLogistics;
/**
 * 订单物流信息查询Dao
 * Created by fgh on 2025/02/22.
 */
public interface OrderLogisticsDao {
    /**
     * 查询订单快递信息
     */
    OrderLogistics getOrderLogisticsByOrderId(Long orderId);
}
