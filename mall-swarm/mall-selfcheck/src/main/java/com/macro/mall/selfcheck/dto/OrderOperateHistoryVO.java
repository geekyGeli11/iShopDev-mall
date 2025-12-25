package com.macro.mall.selfcheck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 订单操作历史VO
 * 
 * @author macro
 * @since 1.0.0
 */
@Data
@Schema(title = "订单操作历史")
public class OrderOperateHistoryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(title = "历史记录ID", description = "操作历史唯一标识")
    private Long id;

    @Schema(title = "订单ID", description = "所属订单ID")
    private Long orderId;

    @Schema(title = "操作人员", description = "操作人员标识")
    private String operateMan;

    @Schema(title = "操作时间", description = "操作时间")
    private Date createTime;

    @Schema(title = "订单状态", description = "操作后的订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单")
    private Integer orderStatus;

    @Schema(title = "操作备注", description = "操作备注")
    private String note;

    @Schema(title = "操作类型", description = "操作类型：CREATE-创建，PAY-支付，CANCEL-取消，SHIP-发货，RECEIVE-收货")
    private String operateType;

    @Schema(title = "操作描述", description = "操作描述")
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOperateMan() {
        return operateMan;
    }

    public void setOperateMan(String operateMan) {
        this.operateMan = operateMan;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "OrderOperateHistoryVO{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", operateMan='" + operateMan + '\'' +
                ", createTime=" + createTime +
                ", orderStatus=" + orderStatus +
                ", note='" + note + '\'' +
                ", operateType='" + operateType + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
} 