package com.macro.mall.dto;

import com.macro.mall.model.CmsLocalGoods;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 本地好物请求参数
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CmsLocalGoodsParam extends CmsLocalGoods {
    @Schema(title = "关联商品ID列表")
    private List<Long> productIds;
} 