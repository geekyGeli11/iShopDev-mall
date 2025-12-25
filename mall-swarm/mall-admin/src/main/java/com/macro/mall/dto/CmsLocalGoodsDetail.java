package com.macro.mall.dto;

import com.macro.mall.model.CmsLocalGoods;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 本地好物详情DTO，包含关联商品信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CmsLocalGoodsDetail extends CmsLocalGoods {
    @Schema(title = "关联商品列表")
    private List<RelatedProduct> productList;
    
    @Data
    public static class RelatedProduct {
        @Schema(title = "商品ID")
        private Long id;
        
        @Schema(title = "商品标题")
        private String title;
        
        @Schema(title = "商品价格")
        private Double price;
        
        @Schema(title = "商品封面图")
        private String pic;
        
        @Schema(title = "排序")
        private Integer sort;
    }
} 