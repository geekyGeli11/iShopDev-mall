package com.macro.mall.portal.domain;

import com.macro.mall.model.PmsProduct;
import com.macro.mall.model.SmsHomeRecommendSubject;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class SubjectDetailResult {
    // 推荐专题
    private SmsHomeRecommendSubject subject;
    // 关联商品列表
    private List<PmsProduct> productList;
}
