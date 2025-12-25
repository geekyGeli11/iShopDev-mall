package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 微信小程序访问数据VO
 * Created by macro on 2025/11/28.
 */
@Data
@Schema(description = "微信小程序访问数据")
public class WechatVisitDataVO {
    
    @Schema(description = "访问趋势数据")
    private List<VisitTrendVO> visitTrend;
    
    @Schema(description = "访问留存数据")
    private List<VisitRetainVO> visitRetain;
    
    @Schema(description = "用户画像分布数据")
    private UserPortraitVO userPortrait;
    
    @Schema(description = "访问页面数据")
    private List<PageVisitVO> pageVisit;
    
    @Schema(description = "访问概况数据")
    private VisitSummaryVO visitSummary;
}
