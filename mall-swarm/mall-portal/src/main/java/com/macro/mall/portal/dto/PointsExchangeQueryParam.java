package com.macro.mall.portal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 积分换购查询参数
 * Created by macro on 2024/01/20.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PointsExchangeQueryParam {
    
    @Schema(title = "配置类型：1-商品换购，2-优惠券兑换")
    private Integer configType;
    
    @Schema(title = "状态：0-禁用/下架，1-启用/上架")
    private Byte status;
    
    @Schema(title = "关键字搜索（商品名称或优惠券名称）")
    private String keyword;
    
    @Schema(title = "商品分类ID（仅商品换购时有效）")
    private Long productCategoryId;
    
    @Schema(title = "最小积分价格")
    private Integer minPointsPrice;
    
    @Schema(title = "最大积分价格")
    private Integer maxPointsPrice;
    
    @Schema(title = "排序方式：0-默认，1-积分价格升序，2-积分价格降序，3-剩余库存降序")
    private Integer sortType = 0;
    
    @Schema(title = "只看可兑换的：true-是，false-否")
    private Boolean onlyAvailable = false;
} 