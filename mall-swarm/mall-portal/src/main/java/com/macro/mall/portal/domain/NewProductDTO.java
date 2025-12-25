package com.macro.mall.portal.domain;

import com.macro.mall.model.SmsHomeNewProduct;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 新品推荐扩展DTO
 */
@Getter
@Setter
public class NewProductDTO extends SmsHomeNewProduct {
    @Schema(title = "商品价格")
    private BigDecimal price;
    
    @Schema(title = "商品图片")
    private String pic;
    
    @Schema(title = "新品状态：0->不是新品；1->新品")
    private Integer newStatus;
    
    @Schema(title = "商品推荐状态：0->不推荐；1->推荐")
    private Integer productRecommandStatus;

    @Schema(title = "商品分类ID")
    private Long productCategoryId;

    @Schema(title = "商品分类名称")
    private String productCategoryName;

    @Schema(title = "商品销量")
    private Integer salesCount;
} 