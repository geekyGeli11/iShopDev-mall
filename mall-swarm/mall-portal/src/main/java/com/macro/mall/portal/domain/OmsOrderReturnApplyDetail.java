package com.macro.mall.portal.domain;

import com.macro.mall.model.OmsOrderReturnApply;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 退货申请详细信息（包含退货地址）
 * Created by macro on 2024/12/20.
 */
@Getter
@Setter
public class OmsOrderReturnApplyDetail extends OmsOrderReturnApply {
    
    @Schema(title = "退货收货人姓名")
    private String returnAddressName;
    
    @Schema(title = "退货收货人电话")
    private String returnAddressPhone;
    
    @Schema(title = "退货收货地址")
    private String returnAddressDetail;
}
