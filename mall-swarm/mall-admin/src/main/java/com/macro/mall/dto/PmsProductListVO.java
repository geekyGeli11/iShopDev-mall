package com.macro.mall.dto;

import com.macro.mall.model.PmsProduct;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品列表展示VO
 * Created by macro on 2024/12/30.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(title = "商品列表展示VO")
public class PmsProductListVO extends PmsProduct {
    
    @Schema(title = "运费模板名称")
    private String freightTemplateName;
    
    @Schema(title = "关联学校名称（多个用逗号分隔）")
    private String schoolNames;
    
    @Schema(title = "关联学校数量")
    private Integer schoolCount;
}
