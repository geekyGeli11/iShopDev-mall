package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 商品规格信息
 * Created by macro on 2024/01/20.
 */
@Data
@Schema(description = "商品规格信息")
public class ProductSpecification {
    
    @Schema(description = "规格名称，如：颜色、尺寸")
    private String name;
    
    @Schema(description = "规格可选值列表，如：[红色, 蓝色, 绿色]")
    private List<String> values;
}