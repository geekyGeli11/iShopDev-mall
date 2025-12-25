package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 积分换购商品配置参数
 * Created by macro on 2024/01/20.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UmsPointsProductConfigParam {
    
    @Schema(title = "配置ID（更新时必填）")
    private Long id;
    
    @Schema(title = "商品ID", required = true)
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    
    @Schema(title = "商品名称", required = true)
    private String productName;
    
    @Schema(title = "商品图片")
    private String productPic;
    
    @Schema(title = "商品原价", required = true)
    @NotNull(message = "商品原价不能为空")
    @DecimalMin(value = "0.01", message = "商品原价必须大于0")
    private BigDecimal originalPrice;
    
    @Schema(title = "换购现金价格", required = true)
    @NotNull(message = "换购现金价格不能为空")
    @DecimalMin(value = "0.00", message = "换购现金价格不能为负数")
    private BigDecimal cashPrice;
    
    @Schema(title = "换购积分价格", required = true)
    @NotNull(message = "换购积分价格不能为空")
    @Min(value = 1, message = "换购积分价格必须大于0")
    private Integer pointsPrice;
    
    @Schema(title = "总库存数量", required = true)
    @NotNull(message = "总库存数量不能为空")
    @Min(value = 0, message = "总库存数量不能为负数")
    private Integer totalStock;
    
    @Schema(title = "每人限购数量", required = true)
    @NotNull(message = "每人限购数量不能为空")
    @Min(value = 1, message = "每人限购数量必须大于0")
    private Integer perPersonLimit;
    
    @Schema(title = "状态：0-下架，1-上架")
    private Byte status = 1;
    
    @Schema(title = "开始时间")
    private Date startTime;
    
    @Schema(title = "结束时间")
    private Date endTime;
    
    @Schema(title = "排序")
    private Integer sortOrder = 0;
    
    @Schema(title = "换购说明")
    private String description;
} 