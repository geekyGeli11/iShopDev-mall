package com.macro.mall.service;

import com.macro.mall.dto.OmsOrderReturnApplyResult;
import com.macro.mall.dto.OmsReturnApplyQueryParam;
import com.macro.mall.dto.OmsUpdateStatusParam;
import com.macro.mall.model.OmsOrderReturnApply;
import com.macro.mall.controller.OmsOrderReturnApplyController.RefundParam;

import java.util.List;

/**
 * 退货申请管理Service
 * Created by macro on 2018/10/18.
 */
public interface OmsOrderReturnApplyService {
    /**
     * 分页查询申请
     */
    List<OmsOrderReturnApply> list(OmsReturnApplyQueryParam queryParam, Integer pageSize, Integer pageNum);

    /**
     * 分页查询申请（包含详细信息）
     */
    List<OmsOrderReturnApplyResult> listWithDetails(OmsReturnApplyQueryParam queryParam, Integer pageSize, Integer pageNum);

    /**
     * 批量删除申请
     */
    int delete(List<Long> ids);

    /**
     * 修改申请状态
     */
    int updateStatus(Long id, OmsUpdateStatusParam statusParam);

    /**
     * 获取指定申请详情
     */
    OmsOrderReturnApplyResult getItem(Long id);

    /**
     * 处理退款
     */
    boolean processRefund(Long id, RefundParam refundParam);

    /**
     * 获取未处理售后申请数量
     */
    Integer getPendingReturnApplyCount();

    /**
     * 根据订单ID获取售后申请列表
     */
    List<OmsOrderReturnApplyResult> getReturnApplyByOrderId(Long orderId);
}
