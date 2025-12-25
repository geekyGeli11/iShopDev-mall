package com.macro.mall.portal.service;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.portal.domain.DiyOrderDetailVO;
import com.macro.mall.portal.domain.DiyOrderParam;
import com.macro.mall.portal.domain.DiyOrderResult;

import java.math.BigDecimal;
import java.util.List;

/**
 * 小程序端DIY订单Service
 * Created by macro on 2024/12/20.
 */
public interface PortalDiyOrderService {

    /**
     * 创建DIY订单
     */
    DiyOrderResult createOrder(DiyOrderParam orderParam);

    /**
     * 获取DIY订单详情
     */
    DiyOrderResult getDiyOrderDetail(Long orderId, Long memberId);

    /**
     * 取消DIY订单
     */
    int cancelDiyOrder(Long orderId, Long memberId);

    /**
     * 计算DIY订单价格
     */
    BigDecimal calculateDiyOrderPrice(DiyOrderParam orderParam);

    /**
     * 获取用户DIY订单列表
     */
    CommonPage<DiyOrderDetailVO> getDiyOrderList(Long memberId, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 更新订单生产状态
     */
    int updateProductionStatus(Long orderId, Byte productionStatus);

    /**
     * 获取订单生产状态
     */
    String getProductionStatusName(Byte productionStatus);

    /**
     * 确认收货
     */
    int confirmReceive(Long orderId, Long memberId);
}
