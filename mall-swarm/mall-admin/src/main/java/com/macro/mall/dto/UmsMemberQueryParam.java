package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询参数
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UmsMemberQueryParam {
    
    @Schema(title = "关键字搜索（用户名/昵称/手机号）")
    private String keyword;
    
    @Schema(title = "用户状态：0->禁用；1->启用")
    private Integer status;
    
    @Schema(title = "会员等级ID")
    private Long memberLevelId;
    
    @Schema(title = "用户来源：1->小程序")
    private Integer sourceType;
    
    @Schema(title = "注册开始时间（格式：yyyy-MM-dd）")
    private String startTime;
    
    @Schema(title = "注册结束时间（格式：yyyy-MM-dd）")
    private String endTime;
    
    @Schema(title = "城市")
    private String city;
    
    @Schema(title = "性别：0->未知；1->男；2->女")
    private Integer gender;

    @Schema(title = "绑定学校ID")
    private Long schoolId;
}