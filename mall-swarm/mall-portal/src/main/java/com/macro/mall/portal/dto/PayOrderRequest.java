package com.macro.mall.portal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayOrderRequest {
    private String orderSn;
    private Integer amount; // 单位：分
    private String spbillCreateIp;
    private String openId;
}
