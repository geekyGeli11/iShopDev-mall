package com.macro.mall.dto;

import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.OmsOrderItem;
import com.macro.mall.model.OmsOrderOperateHistory;
import com.macro.mall.model.OmsOrderReturnApply;
import com.macro.mall.model.PmsComment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 订单详情信息
 * Created by macro on 2018/10/11.
 */
public class OmsOrderDetail extends OmsOrder {
    @Getter
    @Setter
    @Schema(title = "订单商品列表")
    private List<OmsOrderItem> orderItemList;
    @Getter
    @Setter
    @Schema(title = "订单操作记录列表")
    private List<OmsOrderOperateHistory> historyList;
    @Getter
    @Setter
    @Schema(title = "订单评论列表")
    private List<PmsComment> commentList;
    @Getter
    @Setter
    @Schema(title = "售后申请列表")
    private List<OmsOrderReturnApply> returnApplyList;
    @Getter
    @Setter
    @Schema(title = "售后申请数量")
    private Integer returnApplyCount;
    @Getter
    @Setter
    @Schema(title = "是否有售后申请")
    private Boolean hasReturnApply;
    @Getter
    @Setter
    @Schema(title = "库存扣除详情列表")
    private List<OmsStockDeductionDetail> stockDeductionList;
}
