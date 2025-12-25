package com.macro.mall.selfcheck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 会员登录结果
 * 
 * @author macro
 * @since 1.0.0
 */
@Data
@Schema(description = "会员登录结果")
public class MemberLoginResult {

    @Schema(description = "会员ID")
    private Long memberId;

    @Schema(description = "手机号")
    private String telephone;

    @Schema(description = "会员用户名")
    private String username;

    @Schema(description = "会员昵称")
    private String nickname;

    @Schema(description = "会员头像")
    private String icon;

    @Schema(description = "会员等级")
    private Integer memberLevel;

    @Schema(description = "会员积分")
    private Integer integration;

    @Schema(description = "会员余额")
    private BigDecimal balance;

    @Schema(description = "访问令牌")
    private String token;

    @Schema(description = "令牌类型")
    private String tokenType = "Bearer";

    @Schema(description = "令牌过期时间(秒)")
    private Long expiresIn;

    @Schema(description = "登录时间")
    private Long loginTime;
} 