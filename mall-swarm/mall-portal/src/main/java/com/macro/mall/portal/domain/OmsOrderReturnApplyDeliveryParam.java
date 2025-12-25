package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 退货申请快递信息更新参数
 * Created by macro on 2024/12/20.
 */
@Getter
@Setter
public class OmsOrderReturnApplyDeliveryParam {
    @Schema(title = "退货申请ID")
    private Long returnApplyId;
    
    @Schema(title = "快递公司")
    private String deliveryCompany;
    
    @Schema(title = "快递单号")
    private String deliverySn;
} 