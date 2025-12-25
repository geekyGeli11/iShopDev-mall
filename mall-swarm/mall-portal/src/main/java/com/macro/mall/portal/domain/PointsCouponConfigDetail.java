package com.macro.mall.portal.domain;

import com.macro.mall.model.UmsPointsCouponConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 积分换购优惠券配置详情
 * Created by macro on 2024/01/20.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "积分换购优惠券配置详情")
public class PointsCouponConfigDetail extends UmsPointsCouponConfig {
    
    @Schema(description = "优惠券类型：0->全场赠券；1->会员赠券；2->购物赠券；3->注册赠券")
    private Integer couponType;
    
    @Schema(description = "优惠券金额")
    private BigDecimal amount;
    
    @Schema(description = "使用门槛；0表示无门槛")
    private BigDecimal minPoint;
    
    @Schema(description = "优惠券开始时间")
    private Date couponStartTime;
    
    @Schema(description = "优惠券结束时间")
    private Date couponEndTime;
    
    @Schema(description = "使用类型：0->全场通用；1->指定分类；2->指定商品")
    private Integer useType;
    
    @Schema(description = "用户已兑换数量")
    private Integer userExchangedCount = 0;
    
    @Schema(description = "是否可兑换")
    private Boolean canExchange = true;
    
    @Schema(description = "不可兑换原因")
    private String cannotExchangeReason;
    
    @Schema(description = "是否在活动时间内")
    private Boolean isInActiveTime = true;

    @Schema(description = "优惠券类型：0->满减券；1->打折券")
    private Integer couponTypeDetail;

    @Schema(description = "打折率（0.1-0.99），仅打折券使用")
    private BigDecimal discountRate;
} 