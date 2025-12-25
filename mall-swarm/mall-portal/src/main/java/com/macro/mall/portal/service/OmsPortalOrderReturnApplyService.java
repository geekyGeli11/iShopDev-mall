package com.macro.mall.portal.service;

import com.macro.mall.portal.domain.OmsOrderReturnApplyParam;
import com.macro.mall.portal.domain.OmsOrderReturnApplyDeliveryParam;
import com.macro.mall.portal.domain.OmsOrderReturnApplyMultiStepParam;
import com.macro.mall.model.OmsOrderReturnApply;

import java.util.List;
import java.util.Map;

/**
 * 订单退货管理Service
 * Created by macro on 2018/10/17.
 */
public interface OmsPortalOrderReturnApplyService {
    /**
     * 提交申请（原有方法，保持兼容性）
     */
    int create(OmsOrderReturnApplyParam returnApply);

    /**
     * 多步骤提交售后申请
     */
    int createMultiStep(OmsOrderReturnApplyMultiStepParam returnApply);

    /**
     * 根据订单ID查询退货申请列表
     */
    List<OmsOrderReturnApply> getByOrderId(Long orderId);

    /**
     * 更新退货申请快递信息
     */
    int updateDeliveryInfo(OmsOrderReturnApplyDeliveryParam deliveryParam);

    /**
     * 更新订单售后状态
     */
    int updateOrderAfterSaleStatus(Long orderId, Integer afterSaleStatus);

    /**
     * 获取售后申请详情（包含商品详情）
     */
    OmsOrderReturnApply getReturnApplyDetail(Long returnApplyId);

    /**
     * 取消售后申请（仅待审核状态可取消）
     */
    int cancelReturnApply(Long returnApplyId);

    /**
     * 更新售后申请状态
     */
    int updateReturnApplyStatus(Long returnApplyId, Integer status, String handleNote);

    /**
     * 更新退款处理状态
     */
    int updateRefundProcessStatus(Long returnApplyId, Byte refundProcessStatus, String refundTransactionId, String refundFailReason);

    /**
     * 根据订单ID和状态查询售后申请
     */
    List<OmsOrderReturnApply> getByOrderIdAndStatus(Long orderId, Integer status);

    /**
     * 获取用户的售后申请列表
     */
    List<OmsOrderReturnApply> getUserReturnApplyList(Long memberId, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 用户填写退货快递信息
     */
    int fillReturnDeliveryInfo(Long returnApplyId, String deliveryCompany, String deliverySn, String deliveryNote);

    /**
     * 获取退货地址信息
     */
    Map<String, Object> getReturnAddress(Long returnApplyId);

    /**
     * 查询快递物流信息
     */
    Map<String, Object> getDeliveryTrackingInfo(String deliveryCompany, String deliverySn);
}
