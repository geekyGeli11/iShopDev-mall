package com.macro.mall.dto;

import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.OmsOrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 包含订单商品信息的订单DTO
 * Created by macro on 2025/01/09.
 */
@Getter
@Setter
public class OmsOrderWithItems extends OmsOrder {

    @Schema(title = "学校名称")
    private String schoolName;

    @Schema(title = "订单商品列表")
    private List<OmsOrderItem> orderItemList;

    @Schema(title = "售后申请数量")
    private Integer returnApplyCount;

    @Schema(title = "最新售后申请状态：0->待处理；1->退货中；2->已完成；3->已拒绝")
    private Integer latestReturnApplyStatus;

    @Schema(title = "最新售后申请ID")
    private Long latestReturnApplyId;

    @Schema(title = "售后申请状态文本")
    private String returnApplyStatusText;
}
