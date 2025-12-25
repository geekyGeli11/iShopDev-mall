package com.macro.mall.selfcheck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 会员登录参数
 * 
 * @author macro
 * @since 1.0.0
 */
@Data
@Schema(description = "会员登录参数")
public class MemberLoginParam {

    @Schema(description = "手机号", required = true, example = "13800138000")
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String telephone;

    @Schema(description = "验证码", required = true, example = "123456")
    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = "^\\d{1,6}$", message = "验证码格式不正确")
    private String verifyCode;

    @Schema(description = "门店ID", example = "1")
    private Long storeId;

    @Schema(description = "学校ID", example = "1")
    private Long schoolId;
}