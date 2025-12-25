package com.macro.mall.dto;

import com.macro.mall.model.UmsMemberLevel;

/**
 * 会员等级VO，包含关联的优惠券信息
 * Created by macro on 2025/09/01.
 */
public class UmsMemberLevelVO extends UmsMemberLevel {
    
    private String giftCouponName;
    
    public String getGiftCouponName() {
        return giftCouponName;
    }
    
    public void setGiftCouponName(String giftCouponName) {
        this.giftCouponName = giftCouponName;
    }
}
