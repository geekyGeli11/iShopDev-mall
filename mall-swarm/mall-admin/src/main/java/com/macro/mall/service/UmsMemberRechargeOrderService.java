package com.macro.mall.service;

import com.macro.mall.dto.UmsMemberRechargeOrderQueryParam;
import com.macro.mall.model.UmsMemberRechargeOrder;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 充值订单管理Service
 */
public interface UmsMemberRechargeOrderService {

    /**
     * 分页查询充值订单
     */
    List<UmsMemberRechargeOrder> list(UmsMemberRechargeOrderQueryParam queryParam, Integer pageSize, Integer pageNum);

    /**
     * 获取充值订单详情
     */
    UmsMemberRechargeOrder getItem(Long id);

    /**
     * 批量删除充值订单
     */
    int delete(List<Long> ids);

    /**
     * 导出充值订单
     */
    void exportRechargeOrder(UmsMemberRechargeOrderQueryParam queryParam, HttpServletResponse response);

    /**
     * 获取用户充值记录
     */
    List<UmsMemberRechargeOrder> getMemberRechargeHistory(Long memberId, Integer status, String startDate, String endDate, Integer pageSize, Integer pageNum);
} 