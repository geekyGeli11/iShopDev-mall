package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 用户信息更新参数
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UmsMemberUpdateParam {
    
    @Schema(title = "昵称")
    private String nickname;
    
    @Schema(title = "头像")
    private String icon;
    
    @Schema(title = "性别：0->未知；1->男；2->女")
    private Integer gender;
    
    @Schema(title = "生日")
    private Date birthday;
    
    @Schema(title = "所在城市")
    private String city;
    
    @Schema(title = "职业")
    private String job;
    
    @Schema(title = "个性签名")
    private String personalizedSignature;
    
    @Schema(title = "帐号启用状态:0->禁用；1->启用")
    private Integer status;
    
    @Schema(title = "会员等级ID")
    private Long memberLevelId;
} 