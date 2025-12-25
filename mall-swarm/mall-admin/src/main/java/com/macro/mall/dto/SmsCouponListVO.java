package com.macro.mall.dto;

import com.macro.mall.model.SmsCoupon;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 优惠券列表展示VO，包含关联学校信息
 * Created by macro on 2025/09/05.
 */
@Getter
@Setter
public class SmsCouponListVO extends SmsCoupon {
    
    @Schema(title = "关联学校列表")
    private List<SchoolInfo> schools;
    
    @Schema(title = "关联学校名称（逗号分隔）")
    private String schoolNames;
    
    /**
     * 学校信息内部类
     */
    @Getter
    @Setter
    public static class SchoolInfo {
        @Schema(title = "学校ID")
        private Long id;
        
        @Schema(title = "学校名称")
        private String schoolName;
        
        @Schema(title = "学校状态")
        private Boolean status;
    }
}
