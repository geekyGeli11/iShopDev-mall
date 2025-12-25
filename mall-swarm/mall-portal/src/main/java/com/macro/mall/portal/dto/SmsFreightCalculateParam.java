package com.macro.mall.portal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * 运费计算参数
 * Created by AI Assistant
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SmsFreightCalculateParam {
    
    @Valid
    @NotEmpty(message = "商品列表不能为空")
    @Schema(title = "商品列表", required = true)
    private List<FreightCalculateItemParam> items;
    
    @NotEmpty(message = "收货省份不能为空")
    @Schema(title = "收货省份", required = true)
    private String receiverProvince;
    
    @Schema(title = "收货城市")
    private String receiverCity;
    
    @Schema(title = "收货区域")
    private String receiverRegion;
    
    @Schema(title = "收货地址经度")
    private BigDecimal receiverLongitude;
    
    @Schema(title = "收货地址纬度")
    private BigDecimal receiverLatitude;
    
    @Schema(title = "商家地址经度")
    private BigDecimal merchantLongitude;
    
    @Schema(title = "商家地址纬度")
    private BigDecimal merchantLatitude;
    
    /**
     * 运费计算商品项参数
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class FreightCalculateItemParam {
        
        @NotNull(message = "商品ID不能为空")
        @Schema(title = "商品ID", required = true)
        private Long productId;
        
        @Schema(title = "SKU ID")
        private Long skuId;
        
        @NotNull(message = "商品数量不能为空")
        @Schema(title = "商品数量", required = true)
        private Integer quantity;
        
        @Schema(title = "商品重量(kg)")
        private BigDecimal weight;
        
        @Schema(title = "商品体积(m³)")
        private BigDecimal volume;
        
        @Schema(title = "商品价格")
        private BigDecimal price;
        
        @Schema(title = "运费模板ID")
        private Long freightTemplateId;
    }
} 