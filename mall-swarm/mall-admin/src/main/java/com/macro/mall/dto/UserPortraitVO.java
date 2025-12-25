package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户画像分布数据VO
 * Created by macro on 2025/11/28.
 */
@Data
@Schema(description = "用户画像分布数据")
public class UserPortraitVO {
    
    @Schema(description = "性别分布")
    private List<GenderDistributionVO> genderDistribution;
    
    @Schema(description = "年龄分布")
    private List<AgeDistributionVO> ageDistribution;
    
    @Schema(description = "地域分布")
    private List<RegionDistributionVO> regionDistribution;
    
    @Data
    @Schema(description = "性别分布")
    public static class GenderDistributionVO {
        @Schema(description = "性别（male/female/unknown）")
        private String gender;
        
        @Schema(description = "用户数")
        private Integer count;
        
        @Schema(description = "占比（百分比）")
        private Double percentage;
    }
    
    @Data
    @Schema(description = "年龄分布")
    public static class AgeDistributionVO {
        @Schema(description = "年龄段（如：18-24）")
        private String ageRange;
        
        @Schema(description = "用户数")
        private Integer count;
        
        @Schema(description = "占比（百分比）")
        private Double percentage;
    }
    
    @Data
    @Schema(description = "地域分布")
    public static class RegionDistributionVO {
        @Schema(description = "地区名称")
        private String region;
        
        @Schema(description = "用户数")
        private Integer count;
        
        @Schema(description = "占比（百分比）")
        private Double percentage;
    }
}
