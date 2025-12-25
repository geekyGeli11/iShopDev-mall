package com.macro.mall.selfcheck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 会员号码登录参数
 * 
 * @author macro
 * @since 1.0.0
 */
@Data
@Schema(description = "会员号码登录参数")
public class MemberCodeLoginParam {

    @Schema(description = "会员号码", required = true, example = "M24012012345")
    @NotBlank(message = "会员号码不能为空")
    @Pattern(regexp = "^M\\d{11}$", message = "会员号码格式不正确")
    private String memberCode;

    @Schema(description = "登录类型", required = true, example = "code", allowableValues = {"phone", "code"})
    @NotBlank(message = "登录类型不能为空")
    private String loginType = "code";

    @Schema(description = "门店ID", example = "1")
    private Long storeId;

    @Schema(description = "学校ID", example = "1")
    private Long schoolId;
}