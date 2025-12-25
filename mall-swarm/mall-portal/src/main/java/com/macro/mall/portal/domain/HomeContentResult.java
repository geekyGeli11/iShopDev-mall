package com.macro.mall.portal.domain;

import com.macro.mall.model.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 首页内容返回信息封装
 * Created by macro on 2019/1/28.
 */
@Getter
@Setter
public class HomeContentResult {
    //轮播广告
    private List<SmsHomeAdvertise> advertiseList;
    //当前秒杀场次
    private HomeFlashPromotion homeFlashPromotion;
    //新品推荐
    private List<com.macro.mall.portal.domain.NewProductDTO> newProductList;
    //人气推荐（包含价格）
    private List<Map<String, Object>> hotProductList;
}
