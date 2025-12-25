package com.macro.mall.portal.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@Data
public class DirectBuyParam {
    private Long productId;           // 商品 ID
    private Long productSkuId;        // SKU ID
    private Integer quantity;         // 购买数量
    private BigDecimal price;         // 商品价格
    private String productAttr;       // 商品属性
    private String productBrand;      // 商品品牌
    private Long productCategoryId;   // 商品类别 ID
    private String productName;       // 商品名称
    private String productPic;        // 商品图片
    private String productSkuCode;    // 商品 SKU 编号
    private String productSn;         // 商品编号
    private String productSubTitle;   // 商品副标题
    private Integer deliveryType;     // 配送方式：0->快递配送；1->门店自取
}