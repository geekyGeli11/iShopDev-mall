package com.macro.mall.portal.domain;

import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.OmsOrderItem;
import com.macro.mall.portal.dto.SmsFreightCalculateResult;

import java.util.List;

/**
 * 包含订单商品信息的订单详情
 * Created by macro on 2018/9/4.
 */
public class OmsOrderDetail extends OmsOrder {
    private List<OmsOrderItem> orderItemList;
    
    /**
     * 是否有退货申请：0->无退货申请；1->有退货申请
     */
    private Integer hasReturnApply;

    /**
     * 售后状态详情
     */
    private AfterSaleStatusInfo afterSaleStatusInfo;
    
    /**
     * 是否已评价：0->未评价；1->已评价
     */
    private Integer hasComment;
    
    /**
     * 运费计算详情
     */
    private SmsFreightCalculateResult freightDetail;
    
    /**
     * 是否包邮订单
     */
    private Boolean isFreeShipping;
    
    /**
     * 包邮原因
     */
    private String freeShippingReason;

    public List<OmsOrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OmsOrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }

    public Integer getHasReturnApply() {
        return hasReturnApply;
    }

    public void setHasReturnApply(Integer hasReturnApply) {
        this.hasReturnApply = hasReturnApply;
    }

    public Integer getHasComment() {
        return hasComment;
    }

    public void setHasComment(Integer hasComment) {
        this.hasComment = hasComment;
    }

    public SmsFreightCalculateResult getFreightDetail() {
        return freightDetail;
    }

    public void setFreightDetail(SmsFreightCalculateResult freightDetail) {
        this.freightDetail = freightDetail;
    }

    public Boolean getIsFreeShipping() {
        return isFreeShipping;
    }

    public void setIsFreeShipping(Boolean isFreeShipping) {
        this.isFreeShipping = isFreeShipping;
    }

    public String getFreeShippingReason() {
        return freeShippingReason;
    }

    public void setFreeShippingReason(String freeShippingReason) {
        this.freeShippingReason = freeShippingReason;
    }

    public AfterSaleStatusInfo getAfterSaleStatusInfo() {
        return afterSaleStatusInfo;
    }

    public void setAfterSaleStatusInfo(AfterSaleStatusInfo afterSaleStatusInfo) {
        this.afterSaleStatusInfo = afterSaleStatusInfo;
    }

    /**
     * 售后状态详情信息
     */
    public static class AfterSaleStatusInfo {
        /**
         * 售后状态：0->无售后；1->售后申请中；2->售后处理中；3->售后完成；4->售后拒绝
         */
        private Integer afterSaleStatus;

        /**
         * 售后申请ID
         */
        private Long returnApplyId;

        /**
         * 售后申请状态：0->待处理；1->退货中；2->已完成；3->已拒绝
         */
        private Integer returnApplyStatus;

        /**
         * 退款处理状态：0->未退款；1->退款中；2->退款成功；3->退款失败
         */
        private Byte refundProcessStatus;

        /**
         * 售后类型：1->仅退款；2->退货退款
         */
        private Byte returnType;

        /**
         * 售后状态描述
         */
        private String statusDescription;

        public Integer getAfterSaleStatus() {
            return afterSaleStatus;
        }

        public void setAfterSaleStatus(Integer afterSaleStatus) {
            this.afterSaleStatus = afterSaleStatus;
        }

        public Long getReturnApplyId() {
            return returnApplyId;
        }

        public void setReturnApplyId(Long returnApplyId) {
            this.returnApplyId = returnApplyId;
        }

        public Integer getReturnApplyStatus() {
            return returnApplyStatus;
        }

        public void setReturnApplyStatus(Integer returnApplyStatus) {
            this.returnApplyStatus = returnApplyStatus;
        }

        public Byte getRefundProcessStatus() {
            return refundProcessStatus;
        }

        public void setRefundProcessStatus(Byte refundProcessStatus) {
            this.refundProcessStatus = refundProcessStatus;
        }

        public Byte getReturnType() {
            return returnType;
        }

        public void setReturnType(Byte returnType) {
            this.returnType = returnType;
        }

        public String getStatusDescription() {
            return statusDescription;
        }

        public void setStatusDescription(String statusDescription) {
            this.statusDescription = statusDescription;
        }
    }
}
