package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 风格模型查询参数
 * Created by macro on 2024/8/25.
 */
public class PmsStyleModelQueryParam {
    @Schema(title = "关键字")
    private String keyword;

    @Schema(title = "状态：0-禁用，1-启用")
    private Integer status;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
