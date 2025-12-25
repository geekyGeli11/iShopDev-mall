package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

/**
 * 组合商品创建/更新请求参数
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(title = "PmsProductBundleParam", description = "组合商品创建/更新请求参数")
public class PmsProductBundleParam {

    @NotBlank(message = "组合名称不能为空")
    @Schema(title = "组合名称")
    private String name;

    @Schema(title = "组合主图")
    private String pic;

    @Schema(title = "组合宣传图片，多个用逗号分隔")
    private String albumPics;

    @Schema(title = "组合描述")
    private String description;

    @Schema(title = "组合详情描述(富文本)")
    private String detailDesc;

    @NotNull(message = "定价方式不能为空")
    @Schema(title = "定价方式：1-固定价格 2-折扣比例")
    private Integer priceType;

    @Schema(title = "组合固定价格（定价方式为1时必填）")
    private BigDecimal bundlePrice;

    @Schema(title = "折扣比例(0-100)（定价方式为2时必填）")
    private BigDecimal discountRate;

    @Schema(title = "排序")
    private Integer sort;

    @Schema(title = "关联学校ID")
    private Long schoolId;

    @NotNull(message = "组合商品项不能为空")
    @Size(min = 2, message = "组合商品至少需要包含2个商品")
    @Schema(title = "组合商品项列表")
    private List<BundleItemParam> items;
}
