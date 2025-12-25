package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 多步骤售后申请参数
 * Created by macro on 2025/01/15.
 */
@Data
public class OmsOrderReturnApplyMultiStepParam {
    
    @Schema(title = "订单ID")
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    
    @Schema(title = "售后类型：1->仅退款；2->退货退款")
    @NotNull(message = "售后类型不能为空")
    private Byte returnType;
    
    @Schema(title = "申请商品列表")
    @NotNull(message = "申请商品列表不能为空")
    private List<ReturnProductItem> productItems;
    
    @Schema(title = "退货原因")
    private String reason;
    
    @Schema(title = "问题描述")
    private String description;
    
    @Schema(title = "凭证图片，以逗号隔开")
    private String proofPics;
    
    @Schema(title = "退货人姓名")
    private String returnName;
    
    @Schema(title = "退货人电话")
    private String returnPhone;
    
    /**
     * 退货商品项
     */
    @Data
    public static class ReturnProductItem {
        @Schema(title = "订单项ID")
        @NotNull(message = "订单项ID不能为空")
        private Long orderItemId;
        
        @Schema(title = "商品ID")
        @NotNull(message = "商品ID不能为空")
        private Long productId;
        
        @Schema(title = "退货数量")
        @NotNull(message = "退货数量不能为空")
        private Integer productCount;
        
        @Schema(title = "退款金额")
        private BigDecimal returnAmount;
    }
}
