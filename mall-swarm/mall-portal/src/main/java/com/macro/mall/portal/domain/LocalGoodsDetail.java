package com.macro.mall.portal.domain;

import com.macro.mall.model.CmsLocalGoods;
import com.macro.mall.model.PmsProduct;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 本地好物详情结果封装
 */
@Getter
@Setter
public class LocalGoodsDetail {
    // 本地好物信息
    private CmsLocalGoods localGoods;
    // 关联商品列表
    private List<PmsProduct> productList;
} 