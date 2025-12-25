package com.macro.mall.portal.domain;

import com.macro.mall.model.UmsPointsProductConfig;
import com.macro.mall.model.PmsSkuStock;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 积分换购商品配置详情
 * Created by macro on 2024/01/20.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "积分换购商品配置详情")
public class PointsProductConfigDetail extends UmsPointsProductConfig {
    
    @Schema(description = "商品详细信息")
    private String productDetail;
    
    @Schema(description = "商品品牌名称")
    private String brandName;
    
    @Schema(description = "商品分类名称")
    private String categoryName;
    
    @Schema(description = "用户已兑换数量")
    private Integer userExchangedCount = 0;
    
    @Schema(description = "是否可兑换")
    private Boolean canExchange = true;
    
    @Schema(description = "不可兑换原因")
    private String cannotExchangeReason;
    
    @Schema(description = "是否在活动时间内")
    private Boolean isInActiveTime = true;
    
    @Schema(description = "商品SKU列表")
    private List<PmsSkuStock> skuList;
    
    @Schema(description = "商品规格列表")
    private List<ProductSpecification> specificationList;
} 